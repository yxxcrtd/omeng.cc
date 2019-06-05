package com.shanjin.incr.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IUserRelatedCacheServices;
import com.shanjin.cache.util.RedisDistributedLock;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DynamicKeyGenerator;
import com.shanjin.common.util.IdGenerator;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.*;
import com.shanjin.incr.model.*;
import com.shanjin.model.PersAssInfo;
import com.shanjin.service.CplanSerive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/8/17
 * @desc TODO
 * @see
 */
@Service("cplanService")
public class CplanServiceImpl implements CplanSerive {

    private static final String PERS_ASSI_INFO_PREKEY = "PERS_ASSI";
    private static final String PRE_LOCK_KEY = "phone:";
    private static final String CFG_KEY_ASSISTANT_URL = "cplan_assistant_req_url";
    private static final String CFG_KEY_ASSI_ORDER_PRICE = "cplan_order_surplus_price";
    private static final String CATALOG_KEY_SRZL = "cplan_catalog_srzl";
    private static final String SERVICE_TYPE_SRZL = "cplan_service_type_srzl";

    private static final String APPTYPE_SRZL = "srzl";

    private static final int EXPIRE_TIME = 60 * 60 * 24;//一天过期

    private static final Logger logger = LoggerFactory.getLogger(CplanServiceImpl.class);


    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ConfigurationInfoMapper configurationInfoMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private MerchantInfoMapper merchantInfoMapper;
    @Autowired
    private MerchantEmployeesMapper merchantEmployeesMapper;
    @Autowired
    private MerchantContactMapper merchantContactMapper;
    @Autowired
    private MerchantStatisticsMapper merchantStatisticsMapper;
    @Autowired
    private UserStatisticsMapper userStatisticsMapper;
    @Autowired
    private MerchantAttachmentMapper merchantAttachmentMapper;
    @Autowired
    private MerchantAuthMapper merchantAuthMapper;
    @Autowired
    private CatalogMapper catalogMapper;
    @Autowired
    private ServiceTypeMapper serviceTypeMapper;
    @Autowired
    private MerchantServiceTypeMapper merchantServiceTypeMapper;


    @Autowired
    private ICommonCacheService commonCacheService;
    @Autowired
    private IUserRelatedCacheServices userRelatedCacheServices;


    @Override
    public PersAssInfo getPersAssInfo(Long merchantId) throws RuntimeException {

        //参数校验
        if (null == merchantId) {
            throw new RuntimeException("商户Id不能为空");
        }
        PersAssInfo persAssInfo = getCache(merchantId);
        if (null == persAssInfo) {
            String url = getRequestUrl();
            Map<String, Object> map = new HashMap<>(1);

            map.put("merchantId", merchantId);
            map.put("cplanServiceId", "0");
            String respStr = restTemplate.postForObject(url, map, String.class);
            JSONObject jsonObject = parseObject(respStr);
            String retCode = jsonObject.getString("retcode");
            if ("000".equals(retCode)) {
                persAssInfo = JSON.parseObject(jsonObject.getString("retdata"), PersAssInfo.class);
                setCache(merchantId, persAssInfo);
            } else if ("001".equals(retCode)) {
                logger.info("商户未开启私人助理，找不到该信息");
            } else {
                String retMsg = jsonObject.getString("retmsg");
                throw new RuntimeException("接口响应异常，响应码:" + retCode + ",响应消息:" + retMsg);
            }
        }

        return persAssInfo;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> openShopForPersAss(PersAssInfo persAssInfo) {
        UserInfo userInfo;
        MerchantInfo merchantInfo;


        //设置完整地址
        {
            //省市区县 规范化
            String[] provinceCity = BusinessUtil.handlerProvinceAndCity(persAssInfo.getProvince(), persAssInfo.getCity());
            String address = persAssInfo.isPIM()? provinceCity[0]+"市"+persAssInfo.getCity() : persAssInfo.getProvince() +persAssInfo.getCity();
            persAssInfo.setAddress(address);
            persAssInfo.setProvince(provinceCity[0]);
            persAssInfo.setCity(persAssInfo.isPIM()?provinceCity[0] : provinceCity[1]);
        }

        boolean flag = RedisDistributedLock.lockAsync(200, PRE_LOCK_KEY + persAssInfo.getPhone());
        if (!flag) {
            throw new RuntimeException("另一个服务正在操作中，请耐心等待，稍后重试...");
        }
        //当前操作锁定后，后续操作
        try {


            // 用户检查
            userInfo = checkUserInfo(persAssInfo.getPhone());
            merchantInfo = null;
            if (userInfo == null) {
                userInfo = createUserInfo(persAssInfo);
            } else {
                //商户检查
                merchantInfo = checkMerchantInfo(userInfo);
            }

            if (null == merchantInfo) {
                //创建商户信息
                merchantInfo = createMerchantInfo(userInfo, persAssInfo);
            }

            //缓存存储
            //将userKey放入缓存
            commonCacheService.setObject(userInfo.getUserKey(), "employeeKey", persAssInfo.getPhone());
            //缓存userInfo
//            Map<String, String> userInfoMap = BeanUtil.bean2StrMap(userInfo);
            //添加用户默认头像
//            userInfoMap.put("portraitPath", BusinessUtil.disposeImagePath(Constant.DEFAULT_USER_PORTRAIT_PTAH));
//            userRelatedCacheServices.cacheUserInfo(userInfoMap);
        } finally {
            //解锁
            RedisDistributedLock.unLock(PRE_LOCK_KEY + persAssInfo.getPhone());
        }
        //返回数据
        Map<String, Object> map = new HashMap<>(2);
        map.put("userId", userInfo.getId());
        map.put("merchantId", merchantInfo.getId());
        return map;
    }

    /**
     * 私人助理商户检查
     *
     * @param userInfo
     * @return
     */
    private MerchantInfo checkMerchantInfo(UserInfo userInfo) {
        //商户检查
        List<MerchantInfo> list = merchantInfoMapper.findPersAssistantByPhone(userInfo.getPhone());
        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 创建商户信息
     *
     * @param userInfo
     * @param persAssInfo
     * @return
     */
    private MerchantInfo createMerchantInfo(UserInfo userInfo, PersAssInfo persAssInfo) {

        Long id = IdGenerator.generateID(18);
        // 创建实体
        MerchantInfo merchantInfo = new MerchantInfo();
        merchantInfo.setId(id);
        merchantInfo.setAppType("");
        merchantInfo.setCity(persAssInfo.getCity());
        merchantInfo.setProvince(persAssInfo.getProvince());
        merchantInfo.setJoinTime(new Date());
        merchantInfo.setIsPrivateAssistant(1);
        merchantInfo.setMaxEmployeeNum(1);
        merchantInfo.setName("私人助理-" + persAssInfo.getName());
        merchantInfo.setAuthStatus(1);//认证通过
        merchantInfo.setAuthType(2);//个人认证
        merchantInfo.setIsDel(0);
        merchantInfo.setOrderPrice(1L);//抢单金额
        merchantInfo.setAppType(APPTYPE_SRZL);
        merchantInfo.setCatalogId(getCatalogId());
        merchantInfo.setLocationAddress(persAssInfo.getAddress());
        merchantInfo.setLatitude(0.0);
        merchantInfo.setLongitude(0.0);
        merchantInfo.setFlag(1);//默认标识为1
        merchantInfoMapper.saveEntity(merchantInfo);
        //保存认证信息表
        MerchantAuth merchantAuth = new MerchantAuth();
        merchantAuth.setMerchantId(merchantInfo.getId());
        merchantAuth.setAuthType(2);//个人认证
        merchantAuth.setAuthStatus(1);//认证通过
        merchantAuth.setPath("");
        merchantAuth.setJoinTime(new Date());
        merchantAuth.setAuthTime(merchantAuth.getJoinTime());
        merchantAuth.setOperUser("私人助理_sys");
        merchantAuthMapper.saveEntity(merchantAuth);
        //保存商户联系方式
        MerchantContact merchantContact = new MerchantContact();
        merchantContact.setMerchantId(id);
        merchantContact.setTelephone(persAssInfo.getPhone());
        merchantContactMapper.saveEntity(merchantContact);
        //保存雇员表
        MerchantEmployees merchantEmployees = new MerchantEmployees();
        merchantEmployees.setEmployeesType(1); //老板类型
        merchantEmployees.setUserId(userInfo.getId());
        merchantEmployees.setMerchantId(merchantInfo.getId());
        merchantEmployees.setPhone(persAssInfo.getPhone());
        merchantEmployees.setIsDel(0);
        merchantEmployees.setJoinTime(new Date());
        merchantEmployees.setAppType(APPTYPE_SRZL);
        merchantEmployees.setVerificationStatus(1);//设置默认状态
        merchantEmployeesMapper.saveEntity(merchantEmployees);
        //保存商户统计表
        MerchantStatistics merchantStatistics = new MerchantStatistics();
        merchantStatistics.setMerchantId(merchantInfo.getId());
        merchantStatistics.setGrabFrequency(0);
        merchantStatistics.setServiceFrequency(0);
        merchantStatistics.setTotalIncomePrice(new BigDecimal(0));
        merchantStatistics.setTotalWithdrawPrice(merchantStatistics.getTotalIncomePrice());
        merchantStatistics.setSurplusPrice(merchantStatistics.getTotalIncomePrice());
        merchantStatistics.setOrderSurplusPrice(getOrdersurplusPrice());
        merchantStatistics.setTotalAttitudeEvaluation(0);
        merchantStatistics.setTotalQualityEvaluation(0);
        merchantStatistics.setTotalSpeedEvaluation(0);
        merchantStatistics.setTotalCountEvaluation(0);
        merchantStatistics.setAppType(APPTYPE_SRZL);
        merchantStatisticsMapper.saveEntity(merchantStatistics);
        //添加商户默认头像到附件表中
        MerchantAttachment merchantAttachment = new MerchantAttachment();
        merchantAttachment.setMerchantId(merchantInfo.getId());
        merchantAttachment.setAttachmentType(1);
        merchantAttachment.setAttachmentUse(11);
        merchantAttachment.setPath(Constant.DEFAULT_MERCHANT_PORTRAIT_PTAH);
        merchantAttachment.setJoinTime(new Date());
        merchantAttachment.setIsDel(0);
        merchantAttachmentMapper.saveEntity(merchantAttachment);
        //添加商户服务类型
        MerchantServiceType merchantServiceType = new MerchantServiceType();
        merchantServiceType.setAppType(APPTYPE_SRZL);
        merchantServiceType.setMerchantId(merchantInfo.getId());
        merchantServiceType.setServiceTypeId(getSRZLSvcTypeId());
        merchantServiceTypeMapper.saveEntity(merchantServiceType);
        return merchantInfo;
    }


    /**
     * 创建用户
     *
     * @param persAssInfo
     * @return null 说明被其他线程锁定新增操作，此处不做处理
     */
    private UserInfo createUserInfo(PersAssInfo persAssInfo) {

        //新增操作
        String userId = StringUtil.null2Str(IdGenerator.generateID(18));
        String userKey = DynamicKeyGenerator.generateDynamicKey();

        UserInfo userInfo = new UserInfo();
        userInfo.setId(Long.valueOf(userId));
        userInfo.setUserId(Long.valueOf(userId));
        userInfo.setName(persAssInfo.getName());
        userInfo.setPhone(persAssInfo.getPhone());
        userInfo.setIsDel(0);
        userInfo.setCity(persAssInfo.getCity());
        userInfo.setProvince(persAssInfo.getProvince());
        userInfo.setUserKey(userKey);
        userInfo.setUserType(1);//商铺用户
        userInfo.setJoinTime(new Date());
        userInfo.setIsVerification(1);

        userInfoMapper.saveEntity(userInfo);//保存用户

        //创建用户统计表
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.setUserId(userInfo.getId());
        userStatistics.setBespeakFrequency(0);
        userStatistics.setServiceFrequency(0);
        userStatistics.setTotalActualPrice(new BigDecimal(0));
        userStatistics.setTotalPayPrice(userStatistics.getTotalActualPrice());
        userStatisticsMapper.saveEntity(userStatistics);
        return userInfo;
    }

    /**
     * 开店 用户检查
     *
     * @param phone
     * @return
     */
    private UserInfo checkUserInfo(String phone) {
        UserInfo user = new UserInfo();
        user.setPhone(phone);
        user.setIsDel(0);

        List<UserInfo> list = userInfoMapper.findByParamObj(user);
        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() > 1) {
                logger.warn("根据手机号查询的用户记录存在多条 请检查数据的有效性");
            }
            return list.get(0);
        }
        return null;
    }


    /**
     * 私人助理关店
     *
     * @param userId
     * @param merchantId
     */
    @Override
    public void closeShopForPersAss(Long userId, Long merchantId) {

        //逻辑删 商户信息
        MerchantInfo merchantInfo = merchantInfoMapper.getEntityByKey(merchantId);
        if (null != merchantInfo && 1 == merchantInfo.getIsPrivateAssistant() && 0 == merchantInfo.getIsDel()) {
            //删除商户信息表
            MerchantInfo temp = new MerchantInfo(merchantId);
            temp.setIsDel(1);//逻辑删除
            merchantInfoMapper.updateEntity(temp);
            //删除雇员表
            merchantEmployeesMapper.logicDelByMerchantId(merchantId);
        } else {
            throw new RuntimeException("无效的私人助理商户ID或私人助理已关店");
        }
    }

    /**
     * 缓存获取私人助理信息
     *
     * @param userId
     * @return
     */
    private PersAssInfo getCache(Long userId) {
        return (PersAssInfo) commonCacheService.getObject(PERS_ASSI_INFO_PREKEY, userId + "");
    }

    /**
     * 缓存私人助理信息
     *
     * @param userId
     * @param persAssInfo
     */
    private void setCache(Long userId, PersAssInfo persAssInfo) {
        commonCacheService.setObject(persAssInfo, EXPIRE_TIME, PERS_ASSI_INFO_PREKEY, "" + userId);
    }

    /**
     * 查询 私人助理 请求url
     *
     * @return
     */
    private String getRequestUrl() {

        String url = (String) commonCacheService.getObject(CFG_KEY_ASSISTANT_URL);
        if (StringUtils.isEmpty(url)) {
            ConfigurationInfo configurationInfo = new ConfigurationInfo();
            configurationInfo.setConfigKey(CFG_KEY_ASSISTANT_URL);
            List<ConfigurationInfo> list = configurationInfoMapper.findByParamObj(configurationInfo);
            if (CollectionUtils.isEmpty(list)) {
                throw new RuntimeException("config_info 表未配置相关k-v，config_key:" + CFG_KEY_ASSISTANT_URL);
            }
            url = list.get(0).getConfigValue();
            if (StringUtils.isEmpty(url)) {
                throw new RuntimeException("config_info 表配置信息错误，config_key:" + CFG_KEY_ASSISTANT_URL);
            }
            commonCacheService.setObject(url, CFG_KEY_ASSISTANT_URL);
        }
        return url;
    }

    /**
     * 创建私人助理ID
     *
     * @return
     */
    private Long getSRZLSvcTypeId() {
        Long val = (Long) commonCacheService.getObject(SERVICE_TYPE_SRZL);
        if (null == val) {
            ServiceType serviceType = new ServiceType();
            serviceType.setAppType(APPTYPE_SRZL);
            serviceType.setIsPub(1);
            List<ServiceType> list = serviceTypeMapper.findByParamObj(serviceType);
            if (CollectionUtils.isEmpty(list)) {
                throw new RuntimeException("service_type 未配置私人助理信息");
            }
            val = list.get(0).getId();
            commonCacheService.setObject(val, SERVICE_TYPE_SRZL);
        }
        return val;
    }

    /**
     * 查询 字典获取私人助理的抢单金初始值
     */
    private BigDecimal getOrdersurplusPrice() {
        BigDecimal val = (BigDecimal) commonCacheService.getObject(CFG_KEY_ASSI_ORDER_PRICE);
        if (null == val) {
            ConfigurationInfo configurationInfo = new ConfigurationInfo();
            configurationInfo.setConfigKey(CFG_KEY_ASSI_ORDER_PRICE);
            List<ConfigurationInfo> list = configurationInfoMapper.findByParamObj(configurationInfo);
            if (CollectionUtils.isEmpty(list)) {
                throw new RuntimeException("config_info 表未配置相关k-v，config_key:" + CFG_KEY_ASSI_ORDER_PRICE);
            }
            String tempVal = list.get(0).getConfigValue();
            if (StringUtils.isEmpty(tempVal)) {
                throw new RuntimeException("config_info 表配置信息错误，config_key:" + CFG_KEY_ASSI_ORDER_PRICE);
            }
            val = new BigDecimal(tempVal);
            commonCacheService.setObject(val, CFG_KEY_ASSI_ORDER_PRICE);
        }
        return val;
    }

    /**
     * 获取apptype='srzl'的catalogId
     *
     * @return
     */
    private Integer getCatalogId() {
        Integer val = (Integer) commonCacheService.getObject(CATALOG_KEY_SRZL);
        if (null == val) {
            Catalog catalog = new Catalog();
            catalog.setAlias(APPTYPE_SRZL);
            catalog.setIsDel(0);
            List<Catalog> list = catalogMapper.findByParamObj(catalog);
            if (CollectionUtils.isEmpty(list)) {
                throw new RuntimeException("catalog表中不存在apptype Alias 为" + APPTYPE_SRZL + "的记录");
            }
            val = list.get(0).getId();
            commonCacheService.setObject(val, CATALOG_KEY_SRZL);
        }
        return val;
    }
}
