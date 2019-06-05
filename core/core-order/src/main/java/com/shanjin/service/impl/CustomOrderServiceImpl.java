package com.shanjin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.*;
import com.shanjin.cache.service.impl.GenericCacheServiceImpl;
import com.shanjin.cache.service.impl.JedisLock;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.*;
import com.shanjin.dao.*;
import com.shanjin.exception.ApplicationException;
import com.shanjin.model.RuleConfig;
import com.shanjin.service.*;
import com.shanjin.thread.UpdateMerchantPlan;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.shanjin.common.constant.Constant.*;
import static com.shanjin.common.util.BusinessUtil.checkServiceTime;
import static com.shanjin.common.util.BusinessUtil.judgeAuth;
import static com.shanjin.common.util.DateUtil.formatDate;
import static com.shanjin.common.util.DateUtil.getNowYYYYMMDDHHMMSS;
import static com.shanjin.common.util.StringUtil.filterFourCharString;

/**
 *
 * 项目名称：core-order 类名称：CustomOrderServiceImpl 类描述：自定义订单实现类 创建人：Huang yulai
 * 创建时间：2016年3月17日 上午10:56:28 修改人： 修改时间： 修改备注：
 *
 * @version V1.0
 */
@Service("customOrderService")
public class CustomOrderServiceImpl implements ICustomOrderService {

    // 本地异常日志记录对象
    private static final Logger logger = Logger.getLogger(CustomOrderServiceImpl.class);

    @Resource
    private ICommonService commonService;

    @Resource
    private ICommonCacheService commonCacheService;


    @Resource
    private IUserRelatedCacheServices userRelatedCacheServices;

    @Resource
    private IOrderInfoService orderInfoService;

    @Resource
    private IUserOrderDao userOrderDao;

    @Resource
    private ICustomOrderDao customOrderDao;

    @Resource
    private IIpCityCacheService ipCityCacheServices;

    @Resource
    private IImageCacheService imageCacheService;

    @Resource
    private IOderStatusDescCacheService orderStatusDesCacheService;

    @Resource
    private IOrderInfoDao orderInfoDao;

    @Resource
    private IMerchantPlanDao merchantPlanDao;

    @Resource
    private IMyMerchantService myMerchantService;

    @Resource
    private MerchantPayService merchantPayService;


    @Resource
    private IMerchantCacheService merchantCacheService;

    @Resource
    private IPushDao pushDao;

    @Resource
    private IMerchantPlanAttachmentDao merchantPlanAttachmentDao;

    @Resource
    private IMerchantOrderAbandonDao merchantOrderAbandonDao;

    @Resource
    private IVouchersInfoDao vouchersInfoDao;

    @Resource
    private ITimeLineDao   timeLineDao;

    @Resource
    private IDictionaryService  dictionaryService;

    @Resource
    private IPushService pushService;

    @Resource
    private IBlackListDao blackListDao;
	
	@Resource
	private IPurifyDao   purifyDao;	
	
	@Resource
	private IncService    incService;
	
	@Resource
	private IEvaluationDao evaluationDao;
	
	@Resource
	private IKingService kingService;
	
	@Resource
	private IMyIncomeService incomeService;

    @Override
    public String getCustomOrderFormVersion(String serviceId)
            throws Exception {
        String version = StringUtil.null2Str(commonCacheService.getObject(
                CacheConstants.ORDER_FORM_VERSION, serviceId));
        if (StringUtil.isNullStr(version)) {
            // 缓存为空
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("serviceId", serviceId);
            version = customOrderDao.getOrderFormVersion(param);
            if(!StringUtil.isNullStr(version)){
                commonCacheService.setObject(version,
                        CacheConstants.ORDER_FORM_VERSION, serviceId);
            }

        }
        return version;
    }


    /**
     * 获取订单详情表单
     *
     //	 * @param serviceNick
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCustomOrderForm(String serviceId) {
        List<Map<String, Object>> orderForm = new ArrayList<Map<String, Object>>();
        orderForm = (List<Map<String, Object>>) commonCacheService.getObject(
                CacheConstants.ORDER_FORM, serviceId);
        if (orderForm==null||orderForm.isEmpty()) {
            // 缓存为空
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("serviceId", serviceId);
            //查看当前服务是否被删除并且发布状态为已发布
            int total=customOrderDao.getServiceTypeStatus(param);
            if(total==0){
                return orderForm;
            }

            orderForm = customOrderDao.getOrderForm(param);
            try {
                if (orderForm != null && orderForm.size() > 0) {

                    for (Map<String, Object> map : orderForm) {
                        BusinessUtil.disposePath(map, "icon");
                        String colItems = StringUtil.null2Str(map
                                .get("colItems"));
                        List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
                        if (StringUtil.isNotEmpty(colItems)) {
                            String[] items = colItems.split(",");
                            if (items != null && items.length > 0) {
                                for (String item : items) {
                                    Map<String, Object> colMap = new HashMap<String, Object>();
                                    colMap.put("itemName", item);
                                    itemList.add(colMap);
                                }
                            }
                        }
                        map.put("colItems", itemList);
                        /**
                         * 控件联动
                         */
                        // String controlPara =
                        // StringUtil.null2Str(map.get("controlPara"));
                        // List<Map<String, Object>> controlMethod = new
                        // ArrayList<Map<String, Object>>();
                        // if(!StringUtil.isNullStr(controlPara)){
                        // param.put("modeId", map.get("id"));
                        // controlMethod = customOrderDao.getControlData(param);
                        // }
                        // map.put("controlMethod", controlMethod);
                    }

                    commonCacheService.setObject(orderForm,
                            CacheConstants.ORDER_FORM, serviceId);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return orderForm;
            }
        }

        return orderForm;
    }
    /**
     * 保存订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject saveCustomOrder(String serviceId,
                 Map<String, Object> orderInfoMap, List<String> voicePaths,
                                      List<String> picturePaths, String ip) throws Exception {
        orderInfoMap.put("serviceType", serviceId);
        orderInfoMap = StringUtil.formatDollarSign(orderInfoMap);
        
        ResultJSONObject jsonObject = new ResultJSONObject("000", "订单保存成功");
        // 如果省市为空，则根据IP查省市
        String province = (String) orderInfoMap.get("province");
        String city = (String) orderInfoMap.get("city");
        if (StringUtils.isEmpty(province) || StringUtils.isEmpty(city)) {
            if (ip != null) {
                if (!Constant.DEVMODE) {
                    // Modify IP --CITY 缓存 2015.9.21 --Revoke Yu
                    JSONObject cachedIpAddress = ipCityCacheServices
                            .getCity(ip);
                    if (cachedIpAddress != null) {
                        province = (String) cachedIpAddress.get("province");
                        city = (String) cachedIpAddress.get("city");
                    } else {
                        String[] provinceAndCity = BusinessUtil
                                .getProvinceAndCityByIp(ip);
                        province = provinceAndCity[0];
                        city = provinceAndCity[1];
                    }
                    if (StringUtils.isNotEmpty(ip)
                            && StringUtils.isNotEmpty(province)
                            && StringUtils.isNotEmpty(city)) {
                        ipCityCacheServices.cachedCity(ip, province, city);
                    }
                }
            } else {
                province = Constant.EMPTY;
                city = Constant.EMPTY;
            }
        }
        
        String[] provinceAndCity = BusinessUtil.handlerProvinceAndCity(
                province, city);
        orderInfoMap.put("province", provinceAndCity[0]);
        orderInfoMap.put("city", provinceAndCity[1]);

        //旧版订单没有是否立即服务，默认全部是预约服务。插入方法里判断如果是立即服务，serviceTime插入now()
        if(!orderInfoMap.containsKey("isImmediate")){
        	orderInfoMap.put("isImmediate", 0);
        }else if("1".equals(orderInfoMap.get("isImmediate").toString())){//立即服务,默认当前时间
        	orderInfoMap.put("serviceTime", DateUtil.getNowYYYYMMDDHHMM());
        }

        // 生成订单编号，使用20位随机字符串
        String orderNo = IdGenerator.getOrderNo(20);
        orderInfoMap.put("orderNo", orderNo);

        if (orderInfoMap.get("goodsIds") != null) {
            orderInfoMap.put("orderStatus", 3);
        } else {
            orderInfoMap.put("orderStatus", 1);
            orderInfoMap.put("merchantId", null);
        }
        String address=StringUtil.null2Str(orderInfoMap.get("address"));
        String detailAddress=StringUtil.null2Str(orderInfoMap.get("detailAddress"));
        orderInfoMap.put("address", address+detailAddress);
        this.userOrderDao.insertCommonOrder(orderInfoMap);

        Long orderId = (Long) orderInfoMap.get("orderId");
        // 保存订单明细
        orderInfoMap.put("orderId", orderId);
        saveOrderDetail(orderInfoMap);

        // 保存商品明细
        if (orderInfoMap.get("goodsIds") != null) {
            this.saveOrderGoods((Long) orderInfoMap.get("orderId"),
                    (String) orderInfoMap.get("goodsIds"),
                    (String) orderInfoMap.get("goodsNums"));
        }

        // 判断是否有统计信息,来确认在不同APP下面是否有记录
        int result = this.userOrderDao.checkUserStatisticsIsEmpty(orderInfoMap);
        if (result == 0) {
            this.userOrderDao.initUserInfoStatistics(orderInfoMap);
        }
        this.userOrderDao.updateUserSstatisticsBespeak(orderInfoMap);

        // 保存附件信息
        this.insertOrderAttachment(orderId, voicePaths, picturePaths);
        Map<String, Object> orderInfo = userOrderDao.getBasicOrderInfo(orderInfoMap);
        String userId = orderInfoMap.get("userId").toString();
        orderInfo.put("address", address+detailAddress);
        // 用户户订单缓存过，则把新加的订单也插入缓存
        if (userRelatedCacheServices.getUserOrderEntrySize(userId) > 0) {
            userRelatedCacheServices.addUserOrderCache(orderInfo, userId, orderId.toString());
        }

        //保存用户行为  -Revoke 2016.5.13
        Map<String,Object> actionMap= new HashMap<String,Object>();
        actionMap.put("actionCode",BusinessAction.PLACE_ORDER);
        actionMap.put("orderId",orderId);
        timeLineDao.insertTimeLine(actionMap);

        jsonObject = new ResultJSONObject("000", "订单保存成功");
        jsonObject.put("orderId", orderId);
        // jsonObject.put("orderText", detailOrder);
        //
        // jsonObject.put("orderInfo", orderInfo);

        //订单推送
        Map<String,Object> pushMap=new HashMap<String, Object>();
        pushMap.put("phone", orderInfoMap.get("phone"));
        pushMap.put("userId", userId);
        pushMap.put("orderId", orderId);
        pushMap.put("serviceTime", orderInfoMap.get("serviceTime"));
        pushMap.put("isImmediate", orderInfoMap.get("isImmediate"));//1-立即服务，0-预约服务，对应需要修改订单推送至商家pushServiceImpl.orderPush
        pushMap.put("serviceTypeId", orderInfoMap.get("serviceTypeId"));
        pushMap.put("province", province);
        pushMap.put("city", city);
        pushMap.put("longitude", orderInfoMap.get("longitude"));
        pushMap.put("latitude", orderInfoMap.get("latitude"));
        pushMap.put("data", "");
        pushMap.put("pushType", 1);
        pushMap.put("serviceTypeId", serviceId);
        pushService.basicPush(pushMap);

        return jsonObject;

    }

    /**
     * 保存订单详情
     *
     * @param orderInfoMap
     * @return
     */
    private void saveOrderDetail(Map<String, Object> orderInfoMap) {
        if (orderInfoMap != null && !orderInfoMap.isEmpty()) {
            /**
             *
             */
            List<Map<String,Object>> jsonDetailList = new ArrayList<Map<String,Object>>();
            /**
             * 第二步解析自定义表单参数，与自定义表单对应解析
             */
            String serviceId = StringUtil.null2Str(orderInfoMap
                    .get("serviceId"));

            String serviceTypeName = commonService.getServiceTypeName(serviceId);

            if(!StringUtil.isNullStr(serviceTypeName)){
                Map<String, Object> jsonMap = new HashMap<String, Object>();
                jsonMap.put("colName", "serviceId");
                jsonMap.put("icon", "");
                jsonMap.put("colDesc", "服务类型");
                jsonMap.put("value", serviceTypeName);
                jsonDetailList.add(jsonMap);
            }

            List<Map<String, Object>> orderForm = getCustomOrderForm(serviceId);

            if (orderForm != null && !orderForm.isEmpty()) {
                for (Map<String, Object> map : orderForm) {
                    String parmKey = StringUtil.null2Str(map.get("colName")); // 下发表单的参数名
                    String val = StringUtil.null2Str(orderInfoMap.get(parmKey));
                    if(!StringUtil.isNullStr(val)){
                        Map<String, Object> jsonMap = new HashMap<String, Object>();
                        jsonMap.put("colName", parmKey);
                        jsonMap.put("icon", map.get("icon"));
                        jsonMap.put("colDesc", map.get("colDesc"));
                        jsonMap.put("value", val);
                        jsonDetailList.add(jsonMap);
                    }
                }
            }
            if(jsonDetailList.size()>0){
                orderInfoMap.put("jsonDetail", JsonUtil.list2json(jsonDetailList)); // json字符串
                customOrderDao.saveCustomOrderDetail(orderInfoMap);
            }
        }
    }

    /**
     * 查询订单详情
     *
     * @param orderInfoMap
     *            （参数包括orderId,serviceTypeName,serviceNick）
     * @return
     */
    private Object getOrderDetail(Map<String, Object> orderInfoMap) {
    	String isNewInterface = StringUtil.null2Str(orderInfoMap.get("isNewInterface"));
    	
        List<String> list = new ArrayList<String>();
        /**
         * 先给服务项目放在第一行，标题固定“服务类型：”
         */
//		String serviceType = StringUtil.null2Str(orderInfoMap
//				.get("serviceType"));
//		Map<String, Object> serviceTypeMap = commonService
//				.getServiceType(serviceType);
//		String serviceTypeName = "";
//		if (serviceTypeMap != null && !serviceTypeMap.isEmpty()) {
//			serviceTypeName = StringUtil.null2Str(serviceTypeMap
//					.get("serviceTypeName"));
//		}
//		String titile = "服务类型：" + serviceTypeName;
//		list.add(titile);
        Map<String, Object> orderDetail = customOrderDao
                .getOrderDetailByOrderId(orderInfoMap);
        if (orderDetail != null && !orderDetail.isEmpty()) {
            String jsonDetail = StringUtil.null2Str(orderDetail
                    .get("jsonDetail"));
            JSONArray arr= JSONObject.parseArray(jsonDetail);
            if (arr != null&&arr.size()>0) {
                for(int i=0;i<arr.size();i++){
                    JSONObject jsonObject = arr.getJSONObject(i);
                    String value = StringUtil.null2Str(jsonObject.get("value"));
                    if("serviceTime".equals(jsonObject.get("colName").toString())){//如果是立即服务，服务时间显示“立即服务”
                    	String isImmediate = StringUtil.null2Str(orderInfoMap.get("isImmediate"));
                    	if("1".equals(isImmediate) && !"1".equals(isNewInterface)){//立即服务，且不是旧接口
                    		value = "立即服务";
                    	}
                    }
                    if(!StringUtil.isNullStr(value)){
                        value = jsonObject.get("colDesc")+ "：" + value;
                        list.add(value);
                    }
                }
            }


//			JSONObject jsonObject = JSONObject.parseObject(jsonDetail);
//			if (jsonObject != null) {
//				try {
//
//
//					/**
//					 * 第二步解析自定义表单参数，与自定义表单对应解析
//					 */
//
//					List<Map<String, Object>> orderForm = getCustomOrderForm(serviceType);
//					if (orderForm != null && !orderForm.isEmpty()) {
//						for (Map<String, Object> map : orderForm) {
//							String parmKey = StringUtil.null2Str(map
//									.get("colName")); // 下发表单的参数名
//														// 如："colName":
//														// "service_time"
//							String val = StringUtil.null2Str(jsonObject
//									.get(parmKey));
//							if(!StringUtil.isNullStr(val)){
//								// 字段非空
//								val = map.get("colDesc") + "：" + val;
//								list.add(val);
//							}
//						}
//					}
//					/**
//					 * 第三步解析自定义表单的附属参数，如服务地址中追加详细地址
//					 */
//					// TODO:
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}

        }
        return list.toArray();
    }

    /** 保存用户选择的商品 */
    private int saveOrderGoods(Long orderId, String goodsIds, String goodsNums) {
        String[] goodsIdArray = goodsIds.split(Constant.COMMA_EN);
        String[] goodsNumArray = goodsNums.split(Constant.COMMA_EN);

        if (goodsIdArray.length != goodsNumArray.length) {
            return 0;
        }

        List<Map<String, Object>> goodsOrderList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i <= goodsIdArray.length - 1; i++) {
            Map<String, Object> goodsOrderMap = new HashMap<String, Object>();
            goodsOrderMap.put("orderId", orderId);
            goodsOrderMap.put("goodsId", goodsIdArray[i]);
            goodsOrderMap.put("goodsNum", goodsNumArray[i]);
            goodsOrderList.add(goodsOrderMap);
        }
        int i = this.userOrderDao.insertOrderGoods(goodsOrderList);
        return i;
    }

    /**
     * 保存文件到数据库中
     */
    @Transactional(rollbackFor = Exception.class)
    private int insertOrderAttachment(Long orderId, List<String> voicePaths,
                                      List<String> picturePaths) {
        int result = 0;
        try {

            if (voicePaths.size() > 0) {
                for (String path : voicePaths) {
                    Map<String, Object> attaMap = new HashMap<String, Object>();
                    attaMap.put("orderId", orderId);
                    attaMap.put("attachmentType", Constant.VOICE);
                    attaMap.put("attachmentUse", Constant.BUSINESS_VOICE);
                    attaMap.put("path", path);
                    result += this.userOrderDao.insertOrderAttachment(attaMap);
                }
            }

            if (picturePaths.size() > 0) {
                for (String path : picturePaths) {
                    Map<String, Object> attaMap = new HashMap<String, Object>();
                    attaMap.put("orderId", orderId);
                    attaMap.put("attachmentType", Constant.IMAGE);
                    attaMap.put("attachmentUse", Constant.BUSINESS_IMAGE);
                    attaMap.put("path", path);
                    result += this.userOrderDao.insertOrderAttachment(attaMap);
                }
            }
        } catch (Exception e) {
            result = 0;
            logger.error("订单附件保存失败：" + e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /** 获取订单详情 */
    public Map<String, Object> getBasicOrderInfo(Map<String, Object> paramMap) {
        return this.userOrderDao.getBasicOrderInfo(paramMap);
    }

    /**
     * 获取发单用户订单列表
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject getOrderListForSender(Map<String, Object> params) throws Exception {
        Long catalogId = params.get("catalogId")==null?null:StringUtil.nullToLong(params.get("catalogId"));
        Long userId = StringUtil.nullToLong(params.get("userId"));
        String orderStatus = (String) params.get("orderStatus");
        int pageNo = StringUtil.nullToInteger(params.get("pageNo"));

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        int totalPage = 0;
        JSONObject jsonObject = new ResultJSONObject();

        List<Map<String, Object>> cachedOrders = null;

        // 如果 缓存列表存在，则从缓存中读取订单，否则先从数据库中读取订单，再从缓存中加载。
        if (userRelatedCacheServices.getUserOrderEntrySize("" + userId) < 1) {
            cachedOrders = this.getTopnBasicOrderListInfo(userId,
                    CacheConstants.ORDER_PER_USER_SIZE,null);
        } else {
            cachedOrders = userRelatedCacheServices.getCachedUserOrders(""
                    + userId);
        }
        totalPage = constructResultFromCachedOrders(cachedOrders, resultList,
                catalogId, orderStatus, pageNo);

        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "获取用户订单列表成功");
        jsonObject.put("orderList", resultList);
        jsonObject.put("totalPage", totalPage);
        return jsonObject;

    }

    /**
     * 获取发单用户订单列表
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject getOrderListForSenderV1110(Map<String, Object> params) throws Exception {
        Long catalogId = params.get("catalogId")==null?null:StringUtil.nullToLong(params.get("catalogId"));
        Long userId = StringUtil.nullToLong(params.get("userId"));
        String orderStatus = (String) params.get("orderStatus");
        int pageNo = StringUtil.nullToInteger(params.get("pageNo"));

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        int totalPage = 0;
        JSONObject jsonObject = new ResultJSONObject();

        List<Map<String, Object>> cachedOrders = null;

        // 如果 缓存列表存在，则从缓存中读取订单，否则先从数据库中读取订单，再从缓存中加载。
        if (userRelatedCacheServices.getUserOrderEntrySize("" + userId) < 1) {
            cachedOrders = this.getTopnBasicOrderListInfo(userId,
                    CacheConstants.ORDER_PER_USER_SIZE,"V1110");
        } else {
            cachedOrders = userRelatedCacheServices.getCachedUserOrders("" + userId);
            for(Map<String, Object> cachedOrder : cachedOrders){
            	if(!cachedOrder.containsKey("pushCount") || cachedOrder.get("pushCount").toString().equals("0")){//订单推送服务商个数
                    cachedOrders = this.getTopnBasicOrderListInfo(userId,
                            CacheConstants.ORDER_PER_USER_SIZE,"V1110");
            	}
            }
        }
        totalPage = constructResultFromCachedOrdersV1110(cachedOrders, resultList,
                catalogId, orderStatus, pageNo);

        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "获取用户订单列表成功");
        jsonObject.put("orderList", resultList);
        jsonObject.put("totalPage", totalPage);
        return jsonObject;

    }




    /**
     * 获取接单商户订单列表
     */
    @Override
    public JSONObject getOrderListForReceiver(Map<String, Object> params)
            throws Exception {
        int serviceTypeId = StringUtil.nullToInteger(params.get("serviceTypeId"));
//		Long userId = StringUtil.nullToLong(params.get("userId"));
        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
        Integer status = StringUtil.nullToInteger(params.get("status"));
        int pageNo = StringUtil.nullToInteger(params.get("pageNo"));

        JSONObject jsonObject = null;
        jsonObject = new ResultJSONObject("000", "订单基础信息列表加载成功");
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("serviceTypeId", serviceTypeId);// 服务类型ID
        paramMap.put("merchantId", merchantId);	// 商户ID
        paramMap.put("status", status);	// 订单状态

        //最后的返回结果
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        //查询 一个月的商户订单缓存
        List<Map<String, Object>> orderList =merchantCacheService.getCachedMerchantPushOrders(StringUtil.null2Str(merchantId));
        //如果缓存不存在
        if(orderList==null || orderList.size()==0){
            //查询push_merchant_order表中的信息
            orderList = this.customOrderDao.getMerchantOrderList(paramMap);
            merchantCacheService.cachePushOrderForSpecialMerchant(orderList, StringUtil.null2Str(merchantId));
        }else{
        	for(Map record : orderList){
        		if(!record.containsKey("isImmediate")){
                    //查询push_merchant_order表中的信息
                    orderList = this.customOrderDao.getMerchantOrderList(paramMap);
                    merchantCacheService.cachePushOrderForSpecialMerchant(orderList, StringUtil.null2Str(merchantId));
                    break;
        		}
        	}
        }

        //根据查询条件筛选数据
        int totalPage=constructResultFromMerchantCachedOrders(orderList, resultList,paramMap, pageNo);
        //查询order_info表数据
        String orderIds="";
        for(Map<String, Object> map : resultList){
            int orderId=StringUtil.nullToInteger(map.get("orderId"));
            orderIds+=orderId+",";
        }
        if(!orderIds.equals("")){
            orderIds=orderIds.substring(0,orderIds.length()-1);
        }else{
            orderIds="0";
        }
        paramMap.put("orderIds", orderIds);
        List<Map<String, Object>> orderList_ =this.customOrderDao.getMerchantOrderInfo(paramMap);
        List<Map<String, Object>> orderPlan_ =this.customOrderDao.getOrderPlanList(paramMap);

        Map<String,String> dictionary=getOrderStatusMap("receiverOrderStatusMap");
        //合并数据
        for(Map<String, Object> map : resultList){
            int orderId=StringUtil.nullToInteger(map.get("orderId"));
            String orderStatus=StringUtil.null2Str(map.get("orderStatus"));
            for(Map<String, Object> map_ : orderList_){
                int orderId_=StringUtil.nullToInteger(map_.get("orderId"));
                if(orderId==orderId_ && orderId!=0 && orderId_!=0){
                    map.put("userOrderStatus", map_.get("userOrderStatus"));
                    map.put("serviceTypeId", map_.get("serviceTypeId"));
                    map.put("serviceTypeName", map_.get("serviceTypeName"));
                    map.put("userId", map_.get("userId"));
                    map.put("userPhone", map_.get("userPhone"));
                    map.put("userPortraitUrl", map_.get("userPortraitUrl"));
//                    map.put("serviceTime", map_.get("serviceTime"));
                    map.put("serviceTimeStamp", map_.get("serviceTimeStamp"));
//					map.put("orderPrice", map_.get("orderPrice"));
                    map.put("merchantActualPrice", map_.get("merchantActualPrice"));
                    map.put("orderActualPrice", map_.get("orderActualPrice"));
                    map.put("orderPayType", map_.get("orderPayType"));
//					map.put("priceUnit", map_.get("priceUnit"));
                    map.put("longitude", map_.get("longitude"));
                    map.put("latitude", map_.get("latitude"));
                    map.put("address", map_.get("address"));
                    map.put("isImmediate", map_.get("isImmediate"));
                 
                    
                    //获取订单状态对应的描述
                    map.put("orderStatusText", getOrderStatusText(Integer.parseInt(orderStatus), "searchMerchantOrderStatus"));
                }
            }
            map.put("orderStatusName",dictionary.get(orderStatus+""));
            BusinessUtil.disposePath(map, "userPortraitUrl");

            //查询方案价格
            for(Map<String, Object> map_ : orderPlan_){
                int orderId_=StringUtil.nullToInteger(map_.get("orderId"));
                if(orderId==orderId_ && orderId!=0 && orderId_!=0){
                    map.put("orderPrice", map_.get("orderPrice"));
                    map.put("priceUnit", map_.get("priceUnit"));
                }
            }          
        }
        jsonObject.put("totalPage", totalPage);
        jsonObject.put("orderList", resultList);
        return jsonObject;
    }
    /**
     * 获取接单商户订单列表
     */
    public JSONObject getOrderListForReceiverV1110(Map<String, Object> params)
            throws Exception {
        int serviceTypeId = StringUtil.nullToInteger(params.get("serviceTypeId"));
//		Long userId = StringUtil.nullToLong(params.get("userId"));
        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
        int status = StringUtil.nullToInteger(params.get("status"));
        int pageNo = StringUtil.nullToInteger(params.get("pageNo"));

        JSONObject jsonObject = null;
        jsonObject = new ResultJSONObject("000", "订单基础信息列表加载成功");
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("serviceTypeId", serviceTypeId);// 服务类型ID
        paramMap.put("merchantId", merchantId);	// 商户ID
        paramMap.put("status", status);	// 订单状态

        //最后的返回结果
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        //查询 商户订单缓存
        List<Map<String, Object>> orderList =merchantCacheService.getCachedMerchantPushOrders(StringUtil.null2Str(merchantId));
        //如果缓存不存在
        if(orderList==null || orderList.size()==0){
            //查询push_merchant_order表中的信息
            orderList = this.customOrderDao.getMerchantOrderList(paramMap);
            merchantCacheService.cachePushOrderForSpecialMerchant(orderList, StringUtil.null2Str(merchantId));
        }

        //根据查询条件筛选数据
        int totalPage=constructResultFromMerchantCachedOrdersV110(orderList, resultList,paramMap, pageNo);
        //查询order_info表数据
        String orderIds="";
        for(Map<String, Object> map : resultList){
            int orderId=StringUtil.nullToInteger(map.get("orderId"));
            orderIds+=orderId+",";
        }
        if(!orderIds.equals("")){
            orderIds=orderIds.substring(0,orderIds.length()-1);
        }else{
            orderIds="0";
        }
        paramMap.put("orderIds", orderIds);
        List<Map<String, Object>> orderList_ =this.customOrderDao.getMerchantOrderInfo(paramMap);
        
        List<Long> userList=new ArrayList<Long>();
        for(Map<String, Object> map: orderList_){
            Long userId=StringUtil.nullToLong(map.get("userId"));
            userList.add(userId);
        }       
        //查询用户是否是王牌会员
        Map<Long,Boolean> kingMap=kingService.confirmUsersKingIdentity(userList);
        
        
        List<Map<String, Object>> orderPlanCountList =null;
        List<Map<String, Object>> orderPlanList =null;
        if(status==1){//未响应，只需要查询报价方案数量
        	orderPlanCountList=this.customOrderDao.getOrderPlanCountList(paramMap);
        }
        if(status==2){//已响应，查询我是否被选择和用户付款信息
        	orderPlanList=this.customOrderDao.getOrderPlanList(paramMap);
        }

        Map<String,String> dictionary=getOrderStatusMap("merchantOrderStatusV1110");
        //合并数据
        for(Map<String, Object> map : resultList){
        	String descp="";
        	String releaseToNowTime="";
            int orderId=StringUtil.nullToInteger(map.get("orderId"));
            int orderStatus=StringUtil.nullToInteger(map.get("orderStatus"));
            for(Map<String, Object> map_ : orderList_){
                int orderId_=StringUtil.nullToInteger(map_.get("orderId"));
                if(orderId==orderId_ && orderId!=0 && orderId_!=0){
                    map.put("userOrderStatus", map_.get("userOrderStatus"));
                    map.put("serviceTypeId", map_.get("serviceTypeId"));
                    map.put("serviceTypeName", map_.get("serviceTypeName"));
                    map.put("userId", map_.get("userId"));
                    map.put("userPhone", map_.get("userPhone"));
                    map.put("userPortraitUrl", map_.get("userPortraitUrl"));
//                  map.put("serviceTime", map_.get("serviceTime"));
//                  map.put("serviceTimeStamp", map_.get("serviceTimeStamp"));
//					map.put("orderPrice", map_.get("orderPrice"));
//                  map.put("merchantActualPrice", map_.get("merchantActualPrice"));
//                  map.put("orderActualPrice", map_.get("orderActualPrice"));
//                  map.put("orderPayType", map_.get("orderPayType"));
//					map.put("priceUnit", map_.get("priceUnit"));
//                  map.put("longitude", map_.get("longitude"));
//                  map.put("latitude", map_.get("latitude"));
                    map.put("address", map_.get("address"));
                    map.put("isImmediate", map_.get("isImmediate"));
                }
            }
            map.put("orderStatusName",dictionary.get(orderStatus+""));
            BusinessUtil.disposePath(map, "userPortraitUrl");

            if(status==1){//未响应，只需要查询报价方案数量
	            //查询方案价格
	            for(Map<String, Object> map_ : orderPlanCountList){
	                int orderId_=StringUtil.nullToInteger(map_.get("orderId"));
	                if(orderId==orderId_ && orderId!=0 && orderId_!=0){
	                	int orderPlanCount=StringUtil.nullToInteger(map_.get("orderPlanCount"));
	                	if(orderPlanCount==0){
	                		descp="当前还没有其它服务商响应";
	                	}else{
	                		descp="已有"+orderPlanCount+"个服务商响应";
	                	}
	                }
	            }   
	            
	            //查看下单距离现在多长时间
	            String joinTime=StringUtil.null2Str(map.get("joinTime"));//下单时间   
	            releaseToNowTime=BusinessUtil.getReleaseTimeByOrder(joinTime);
	            map.put("releaseToNowTime", releaseToNowTime);
            }else if(status==2){//已响应            	
            	if(orderStatus==2){//服务商已报价，未选择
		            //查询方案价格
		            for(Map<String, Object> map_ : orderPlanList){
		                int orderId_=StringUtil.nullToInteger(map_.get("orderId"));
		                if(orderId==orderId_ && orderId!=0 && orderId_!=0){
		                	int isRead=StringUtil.nullToInteger(map_.get("isRead"));
		                	if(isRead==1){//用户已查看服务商报价
		                		descp="客户已查看方案";
		                	}else{
		                		descp="等待客户查看方案";
		                	}
		                }
		            } 
            	}else if (orderStatus==3){//已选择，未付款
            		descp="客户已选择你为TA服务";
            		//判断是否已有付款记录
            		paramMap.put("orderId", orderId);
            		int orderPayCount=customOrderDao.getOrderPayCount(paramMap);
            		if(orderPayCount!=0){
            			descp="客户已向你付款 "+orderPayCount+"次";
            		}
            	}else{//已付款
            		descp="";
            	}
            	
            }else if (status==0){//已结束
            	if(orderStatus==5){//订单已完成
            		descp="客户确认服务已完成";
            	}else if(orderStatus==7){//用户主动取消订单
            		descp="";
            	}else if(orderStatus==8){//用户选择了其他服务商
            		descp="客户选择了其它服务商";
            	}else if(orderStatus==9){//用户选择了其他服务商
            		descp="";
            	}else{
            		
            	}
            }else{
            	
            }
            map.put("descp", descp);
            
            //查询是否是王牌会员
            if(kingMap!=null){
            	Long userId=StringUtil.nullToLong(map.get("userId"));
            	map.put("isKing", kingMap.get(userId));
            }
           
        }
        
        jsonObject.put("totalPage", totalPage);
        jsonObject.put("orderList", resultList);
        return jsonObject;
    }
    /**
     * 发单用户获取订单详情
     */
    @Override
    public JSONObject getOrderDetailForSender(String orderId) throws Exception {
        JSONObject jsonObject = new ResultJSONObject();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);

        Map<String,Object> orderInfo = this.orderInfoDao.selectOrderGeneral(paramMap);

        String orderStatus_ =StringUtil.null2Str(orderInfo.get("orderStatus"));

    	//转化serviceTime为逾期时间
    	String overdue = this.convertServiceTime(orderInfo);

        //如果用户侧该订单有缓存，则取缓存中的状态    Revoke 2016.6.10
        String userId= orderInfo.get("userId").toString();
        Map<String,Object> cachedOrderInfo=userRelatedCacheServices.getCachedUserOrder(userId, orderId);
        if (cachedOrderInfo!=null){
            orderStatus_ = cachedOrderInfo.get("orderStatus").toString();
            orderInfo.put("orderStatus",orderStatus_);
        }


        Map<String, Object> merchantInfo = null;
        if (StringUtil.isEmpty(orderInfo.get("merchantId"))){
            orderInfo.put("planCount", 0);
        }else{
            orderInfo.put("planCount", 1);
            merchantInfo = this.userOrderDao.getOrderMerchantInfoById(orderInfo.get("merchantId").toString());
        }
        if (StringUtil.isEmpty(orderInfo.get("merchantActualPrice"))){
            orderInfo.put("serviceRecordCount", 0);
        }else{
            orderInfo.put("serviceRecordCount", 1);
        }
        //获取订单详情信息
        paramMap.put("serviceType", orderInfo.get("serviceType"));
        paramMap.put("serviceTypeName", orderInfo.get("serviceTypeName"));
        paramMap.put("orderStatus", orderStatus_);
        paramMap.put("isImmediate", orderInfo.get("isImmediate"));
        Object orderText=getOrderDetail(paramMap);

        if(orderText==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }

        //翻译订单状态
        Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
        orderInfo.put("orderStatusName", dictionary.get(orderStatus_));

//		获取订单附件信息
//		Map<String, Object> orderAttachment = this.getOrderAttachment(paramMap);

        //获取订单状态对应的描述
        jsonObject.put("orderStatusText", getOrderStatusText(StringUtil.nullToInteger(orderStatus_), "searchOrderStatus"));

        //计算失效时间，即到服务时间的时间差
        if(orderStatus_.equals("1") || orderStatus_.equals("2") ){
            //		String joinTime=StringUtil.null2Str(orderInfo.get("joinTime"));
            //		Long serviceTimeMillis=StringUtil.nullToLong(orderInfo.get("serviceTimeStamp"));
            //		String surplusTime=this.computePastTime(orderStatus_,StringUtil.nullToLong(orderId),joinTime,serviceTimeMillis) ;
            Date DateServiceTime=null;
            try {
                DateServiceTime = DateUtil.convertStringToDate(DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN,overdue);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
            String surplusTime=this.computePastTimeV730(DateServiceTime.getTime());
            jsonObject.put("surplusTime", surplusTime);
        }

        Map<String, Object> orderNote = this.orderInfoService.getOrderNote(paramMap);

        BusinessUtil.disposeManyPath(orderInfo, "serviceTypePicPath");
        BusinessUtil.disposeManyPath(merchantInfo, "merchantIcon");
        BusinessUtil.disposeManyPath(orderNote, "picturePath", "voicePath");

        /** start 2015-12-22 王瑞 新增返回员工ID */
        // 如果用户选择服务商了，但是抢单员工ID却没有，则继续查询方案表
        int orderStatus = Integer
                .parseInt(orderInfo.get("orderStatus") == null ? "0"
                        : orderInfo.get("orderStatus").toString());
        Long receiveEmployeesId = StringUtil.nullToLong(orderInfo.get("receiveEmployeesId"));
        if (orderStatus >= 3 && receiveEmployeesId == 0 && merchantInfo != null) {
            Long merchantId = Long
                    .parseLong(merchantInfo.get("merchantId") == null ? "0"
                            : merchantInfo.get("merchantId").toString());
            // 查询方案表，得到抢单员工ID
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("orderId", orderId);
            map.put("merchantId", merchantId);
            receiveEmployeesId = userOrderDao.getReceiveEmployeesIdFromMerchantPlan(map);
            orderInfo.put("receiveEmployeesId", receiveEmployeesId);
            String rongyunToken = userOrderDao.getRongYunToken(receiveEmployeesId);
            orderInfo.put("rongYunToken", rongyunToken);
        }
        /** end */

        //查询订单服务状态记录
        List<Map<String,Object>> getTimelineList=customOrderDao.getTimeline(paramMap);
        getTimelineList=handlerUserTimelint(getTimelineList);
        
        //放入订单过期时间的系统参数
        boolean bool = checkServiceTime(orderInfo);
        //查询是否已经有过逾期操作
        String flag=StringUtil.null2Str(commonCacheService.getObject(CacheConstants.ORDER_PAST_TIME, StringUtil.null2Str(orderId)));
        if (bool && (orderStatus==1 || orderStatus==2) && !"1".equals(flag)) {//过期

            String code=orderStatus==1? BusinessAction.TIMEOUT_ORDER :BusinessAction.TIMEOUT_NOT_CHOOSE ;
            this.pastTime(6, 9, code, StringUtil.nullToLong(orderId), StringUtil.null2Str(orderInfo.get("serviceTime")));

            //修改返回信息
            orderInfo.put("orderStatus", 6);
            orderInfo.put("orderStatusName", dictionary.get("6"));
            orderInfo.put("orderStatusText", getOrderStatusText(6, "searchOrderStatus"));
            jsonObject.put("orderStatusText", getOrderStatusText(6, "searchOrderStatus"));
            if(jsonObject.get("surplusTime") != null){
                jsonObject.remove("surplusTime");
            }

            Map<String,Object> action = new HashMap<String,Object>();
            action.put("actionCode", code);
            action.put("codeName", (BusinessAction.merchantAcionMap).get(code));
            action.put("actionTime",orderInfo.get("serviceTime"));
            getTimelineList.add(action);
        }

        jsonObject.put("orderInfo", orderInfo);
        jsonObject.put("orderText", orderText);
        jsonObject.put("merchantInfo", merchantInfo);
        jsonObject.put("orderAttachment", orderNote);
        jsonObject.put("getTimelineList", getTimelineList);
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "获取用户端订单明细成功");

        return jsonObject;
    }

    /**
     * 发单用户获取订单详情-------V730 版本
     */
    @Override
    public JSONObject getOrderDetailForSenderV730(String orderId) throws Exception {
        JSONObject jsonObject = new ResultJSONObject();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);

        Map<String,Object> orderInfo = this.orderInfoDao.selectOrderGeneral(paramMap);
        
        String orderStatus_ =StringUtil.null2Str(orderInfo.get("orderStatus"));

        //返回orderPrice而不是planPrice
        if (orderInfo.containsKey("planPrice")){
            orderInfo.put("orderPrice", orderInfo.get("planPrice"));
            orderInfo.remove("planPrice");
        }

        //如果用户侧该订单有缓存，则取缓存中的状态    Revoke 2016.6.10
        String userId= orderInfo.get("userId").toString();
        Map<String,Object> cachedOrderInfo=userRelatedCacheServices.getCachedUserOrder(userId, orderId);
        if (cachedOrderInfo!=null){
            orderStatus_ = cachedOrderInfo.get("orderStatus").toString();
            orderInfo.put("orderStatus",orderStatus_);
        }


        Map<String, Object> merchantInfo = null;
        if (StringUtil.isEmpty(orderInfo.get("merchantId"))){
            orderInfo.put("planCount", 0);
        }else{
            orderInfo.put("planCount", 1);
            merchantInfo = myMerchantService.selectMerchantBasicInfo((Long)orderInfo.get("merchantId"));
            if (merchantInfo==null){
            	merchantInfo = this.userOrderDao.getOrderMerchantInfoById(orderInfo.get("merchantId").toString());
            }
            Map<String,Object> merchantStatics=myMerchantService.statisticsInfoEdit((Long)orderInfo.get("merchantId"));
            merchantInfo.put("score", merchantStatics.get("score"));
            merchantInfo.put("starLevel", merchantStatics.get("starLevel"));

            merchantInfo.remove("microWebsiteUrl");
            merchantInfo.remove("vipLevel");
            merchantInfo.remove("detailAddress");
            merchantInfo.remove("city");
            merchantInfo.remove("catalogId");
            merchantInfo.remove("province");
        }
        if (StringUtil.isEmpty(orderInfo.get("merchantActualPrice"))){
            orderInfo.put("serviceRecordCount", 0);
        }else{
            orderInfo.put("serviceRecordCount", 1);
        }
        //获取订单详情信息
        paramMap.put("serviceType", orderInfo.get("serviceType"));
        paramMap.put("serviceTypeName", orderInfo.get("serviceTypeName"));
        paramMap.put("orderStatus", orderStatus_);
        paramMap.put("isImmediate", orderInfo.get("isImmediate"));
        Object orderText=getOrderDetail(paramMap);

        if(orderText==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }

        //翻译订单状态
        Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
        orderInfo.put("orderStatusName", dictionary.get(orderStatus_));

//		获取订单附件信息
//		Map<String, Object> orderAttachment = this.getOrderAttachment(paramMap);

        //获取订单状态对应的描述
        jsonObject.put("orderStatusText", getOrderStatusText(StringUtil.nullToInteger(orderStatus_), "searchOrderStatus"));

        //计算失效时间，即到服务时间的时间差
        if(orderStatus_.equals("1") || orderStatus_.equals("2") ){
        	//转化serviceTime为逾期时间
        	String overdue = this.convertServiceTime(orderInfo);
            Date DateServiceTime=null;
            try {
                DateServiceTime = DateUtil.convertStringToDate(DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN,overdue);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
            String surplusTime=this.computePastTimeV730(DateServiceTime.getTime());
            jsonObject.put("surplusTime", surplusTime);
        }

        Map<String, Object> orderNote = this.orderInfoService.getOrderNote(paramMap);

        BusinessUtil.disposeManyPath(orderInfo, "serviceTypePicPath");
        BusinessUtil.disposeManyPath(merchantInfo, "merchantIcon");
        BusinessUtil.disposeManyPath(orderNote, "picturePath", "voicePath");

        /** start 2015-12-22 王瑞 新增返回员工ID */
        // 如果用户选择服务商了，但是抢单员工ID却没有，则继续查询方案表
        int orderStatus = Integer
                .parseInt(orderInfo.get("orderStatus") == null ? "0"
                        : orderInfo.get("orderStatus").toString());
        Long receiveEmployeesId = StringUtil.nullToLong(orderInfo.get("receiveEmployeesId"));
        if (orderStatus >= 3 && receiveEmployeesId == 0 && merchantInfo != null) {
            Long merchantId = Long
                    .parseLong(merchantInfo.get("id") == null ? "0"
                            : merchantInfo.get("id").toString());
            // 查询方案表，得到抢单员工ID
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("orderId", orderId);
            map.put("merchantId", merchantId);
            receiveEmployeesId = userOrderDao
                    .getReceiveEmployeesIdFromMerchantPlan(map);
            orderInfo.put("receiveEmployeesId", receiveEmployeesId);
            String rongyunToken = userOrderDao.getRongYunToken(receiveEmployeesId);
            orderInfo.put("rongYunToken", rongyunToken);
        }
        /** end */

        // 判断订单是否过期
        boolean bool = checkServiceTime(orderInfo);
        //查询是否已经有过逾期操作
        String flag=StringUtil.null2Str(commonCacheService.getObject(CacheConstants.ORDER_PAST_TIME, StringUtil.null2Str(orderId)));
        if (bool && (orderStatus==1 || orderStatus==2) && !"1".equals(flag)) {//过期
            String code=orderStatus==1? BusinessAction.TIMEOUT_ORDER :BusinessAction.TIMEOUT_NOT_CHOOSE ;
            this.pastTime(6, 9, code, StringUtil.nullToLong(orderId), StringUtil.null2Str(orderInfo.get("serviceTime")));

            //修改返回信息
            orderInfo.put("orderStatus", 6);
            orderInfo.put("orderStatusName", dictionary.get("6"));
            orderInfo.put("orderStatusText", getOrderStatusText(6, "searchOrderStatus"));
            jsonObject.put("orderStatusText", getOrderStatusText(6, "searchOrderStatus"));
            if(jsonObject.get("surplusTime") != null){
                jsonObject.remove("surplusTime");
            }
        }

        //新订单及待选择的加入订单推送的服务商数量   2016-7-18 Revoke Yu
        if (orderInfo.get("orderStatus").equals("1") || orderInfo.get("orderStatus").equals(1)
                || orderInfo.get("orderStatus").equals("2") || orderInfo.get("orderStatus").equals(2)){
            jsonObject.put("pushMerchantCount",customOrderDao.getPushMerchantNum(Long.parseLong(orderId)));

			 /*对于新预约状态，获取TOP32个推送商家信息  2016-7.22 RevokeYu
			 通过配置项来确定是否取该TOP32商家信息  2016-7-25 RevokeYu
			 if ((orderInfo.get("orderStatus").equals("1") || orderInfo.get("orderStatus").equals(1)) && needShowTopPushMerchat()){
				 JSONObject pushMerchantJsonObject = this.getPushMerchantInfos(Long.parseLong(orderId), 0, 32);
				 if (pushMerchantJsonObject.getString("resultCode").equals("000")){
					 	jsonObject.put("pushMerchantInfos", pushMerchantJsonObject.getJSONArray("merchantInfoList"));
				 }
			 }
			 2016.7.26 说暂时不要了
			 */

        }

        //待选择报价方案的订单，增加查看TOP5 报价方案列表  2016-7-18 Revoke Yu
        if (orderInfo.get("orderStatus").equals("2") || orderInfo.get("orderStatus").equals(2)){
            Map<String,Object> planQueryParam = new HashMap<String,Object>(3);
            planQueryParam.put("orderId", orderId);
            planQueryParam.put("pageNo", 0);
            planQueryParam.put("orderBy", -1);
            planQueryParam.put("direction", "desc");

            JSONObject planJsonObject=this.getPricePlanList(planQueryParam);
            if (planJsonObject.getString("resultCode").equals("000")){
                jsonObject.put("totalPage", planJsonObject.getInteger("totalPage"));
                jsonObject.put("merchantPlans", planJsonObject.getJSONArray("merchantPlans"));
            }
        }

        //修订报价方案数量
        orderInfo.put("planCount", merchantPlanDao.getOrderPlanCount(Long.parseLong(orderId)));

        //服务中、待支付、交易成功  获取报价方案价格  2016-7-19 Revoke Yu
        if (orderInfo.get("orderStatus").equals("3") || orderInfo.get("orderStatus").equals(3)
                || orderInfo.get("orderStatus").equals("4") || orderInfo.get("orderStatus").equals(4)
                || orderInfo.get("orderStatus").equals("5") || orderInfo.get("orderStatus").equals(5)){
            Map<String,Object> merchantPlan=merchantPlanDao.getMerchantPlan((Long)orderInfo.get("merchantPlanId"));
            if(StringUtil.isNotEmpty(merchantPlan.get("distance"))){
            	merchantInfo.put("distance", LocationUtil.showDistance(((Integer)merchantPlan.get("distance")).doubleValue()));	
            }
            
            //修订vipStatus
            merchantInfo.put("vipStatus", -1);
			List<RuleConfig> rulConfigs=incService.getRuleConfig((Long)orderInfo.get("merchantId"));
			if (rulConfigs!=null){
				if (rulConfigs.get(0).isVipMerchantOrder()){
					merchantInfo.put("vipStatus", 2);
				}
			}
        }

        //待支付、交易成功状态的订单详情，获取商家确定的支付类型---线上，线下  2016-7-20  Revoke Yu
        if (orderInfo.get("orderStatus").equals("4") || orderInfo.get("orderStatus").equals(4)
                || orderInfo.get("orderStatus").equals("5") || orderInfo.get("orderStatus").equals(5)
                ){
            Map<String,Object> serviceRecQueryParam =new HashMap<String,Object>(3);
            serviceRecQueryParam.put("merchantId", merchantInfo.get("merchantId"));
            serviceRecQueryParam.put("orderId", orderId);
            Map<String,Object> serviceRec=customOrderDao.getMerchantServiceRecord(serviceRecQueryParam);
            merchantInfo.put("payType",serviceRec.get("payType"));
        }

        //对于有报价方案的过期订单，修订其提示
        if ( (orderInfo.get("orderStatus").equals("6") || orderInfo.get("orderStatus").equals(6) ) &&  (Integer)orderInfo.get("planCount")>0){
            jsonObject.put("orderStatusText", getOrderStatusText(8, "searchOrderStatus"));
        }

        //判断是否有摸金机会
        if(orderStatus_.equals("1")){
        	int  isTouchingGold=commonService.isTouchingGold();
        	jsonObject.put("isTouchingGold", isTouchingGold);
        	jsonObject.put("serversTime",DateUtil.getNowTime());
        }
        
        jsonObject.put("orderInfo", orderInfo);
        jsonObject.put("orderText", orderText);
        jsonObject.put("merchantInfo", merchantInfo);
        jsonObject.put("orderAttachment", orderNote);
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "获取用户端订单明细成功");
        
        return jsonObject;
    }

    /**
     * 发单用户获取订单详情-------V1110 版本
     */
    @Override
    public JSONObject getOrderDetailForSenderV1110(String orderId) throws Exception {
        JSONObject jsonObject = new ResultJSONObject();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);

        Map<String,Object> orderInfo = this.orderInfoDao.selectOrderGeneral(paramMap);
        
        String orderStatus_ =StringUtil.null2Str(orderInfo.get("orderStatus"));

        //返回orderPrice而不是planPrice
        if (orderInfo.containsKey("planPrice")){
            orderInfo.put("orderPrice", orderInfo.get("planPrice"));
            orderInfo.remove("planPrice");
        }

        //如果用户侧该订单有缓存，则取缓存中的状态    Revoke 2016.6.10
        String userId= orderInfo.get("userId").toString();
        Map<String,Object> cachedOrderInfo=userRelatedCacheServices.getCachedUserOrder(userId, orderId);
        if (cachedOrderInfo!=null){
            orderStatus_ = cachedOrderInfo.get("orderStatus").toString();
            orderInfo.put("orderStatus",orderStatus_);
        }


        Map<String, Object> merchantInfo = null;
        if (StringUtil.isEmpty(orderInfo.get("merchantId"))){
            orderInfo.put("planCount", 0);
        }else{
            orderInfo.put("planCount", 1);
            merchantInfo = myMerchantService.selectMerchantBasicInfo((Long)orderInfo.get("merchantId"));
            if (merchantInfo==null){
            	merchantInfo = this.userOrderDao.getOrderMerchantInfoById(orderInfo.get("merchantId").toString());
            }
            Map<String,Object> merchantStatics=myMerchantService.statisticsInfoEdit((Long)orderInfo.get("merchantId"));
            merchantInfo.put("score", merchantStatics.get("score"));
            merchantInfo.put("starLevel", merchantStatics.get("starLevel"));

            merchantInfo.remove("microWebsiteUrl");
            merchantInfo.remove("vipLevel");
            merchantInfo.remove("detailAddress");
            merchantInfo.remove("city");
            merchantInfo.remove("catalogId");
            merchantInfo.remove("province");
        }
        if (StringUtil.isEmpty(orderInfo.get("merchantActualPrice"))){
            orderInfo.put("serviceRecordCount", 0);
        }else{
            orderInfo.put("serviceRecordCount", 1);
        }
        //获取订单详情信息
        paramMap.put("serviceType", orderInfo.get("serviceType"));
        paramMap.put("serviceTypeName", orderInfo.get("serviceTypeName"));
        paramMap.put("orderStatus", orderStatus_);
        paramMap.put("isImmediate", orderInfo.get("isImmediate"));
        Object orderText=getOrderDetail(paramMap);

        if(orderText==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }

        //翻译订单状态
        Map<String,String> dictionary = getOrderStatusMap("senderOrderStatusMapV1110");
        orderInfo.put("orderStatusName", dictionary.get(orderStatus_));

        //获取订单状态对应的描述
    	String orderStatusText = getOrderStatusText(StringUtil.nullToInteger(orderStatus_), "searchOrderStatusV1110");
        if(StringUtil.nullToInteger(orderStatus_) == 1){//未响应，已推送xxx
        	orderStatusText = orderStatusText.replace("$pushCount", orderInfo.get("pushCount").toString());
        }else if(StringUtil.nullToInteger(orderStatus_) == 2){//已响应，已有xxxx响应
        	orderStatusText = orderStatusText.replace("$planCount", orderInfo.get("sorPlanCount").toString());
        }
        if("0".equals(orderInfo.get("evaluate").toString()) && "5".equals(orderStatus_)){//已完成未评价
//        	jsonObject.put("orderStatusText", "等待评价");
        	jsonObject.put("descp", "等待评价");//与商户端查看保持统一
        }else{
//        	jsonObject.put("orderStatusText", orderStatusText);
        	jsonObject.put("descp", orderStatusText);
        }
        if(!"0".equals(orderInfo.get("evaluate").toString())){//已评价
    		DecimalFormat df = new DecimalFormat("#.0");
        	Map evaluateMap = orderInfoDao.getEvaluateByOrderId(paramMap);

			int attitudeEvaluation = Integer.parseInt(evaluateMap
					.get("attitudeEvaluation") == null ? "0" : evaluateMap
					.get("attitudeEvaluation") + "");
			int qualityEvaluation = Integer.parseInt(evaluateMap
					.get("qualityEvaluation") == null ? "0" : evaluateMap
					.get("qualityEvaluation") + "");
			int speedEvaluation = Integer.parseInt(evaluateMap
					.get("speedEvaluation") == null ? "0" : evaluateMap
					.get("speedEvaluation") + "");
			int total = attitudeEvaluation + qualityEvaluation
					+ speedEvaluation;

			int avg = new BigDecimal(total).divide(new BigDecimal(3),
					0, BigDecimal.ROUND_HALF_UP).intValue();
			String evaluation = Constant.EMPTY;
			if (avg < 2) {
				evaluation = "差评";
			} else if (2 <= avg && avg <= 4) {
				evaluation = "中评";
			} else {
				evaluation = "好评";
			}
			Map<String, Object> userEvaluation = new HashMap<String, Object>();
			userEvaluation.put("textEvaluation", evaluateMap.get("textEvaluation"));
			userEvaluation.put("joinTime", evaluateMap.get("joinTime"));
			userEvaluation.put("evaluation", evaluation);
			userEvaluation.put("avgEvaluation", df.format(new BigDecimal(total)
					.divide(new BigDecimal(3), 1,
							BigDecimal.ROUND_HALF_UP)));
			userEvaluation.put("starLevel", avg);
			BusinessUtil.disposeManyPath(evaluateMap,"attachmentPaths");
			userEvaluation.put("attachmentPaths", evaluateMap.get("attachmentPaths"));

            jsonObject.put("userEvaluation", userEvaluation);
        }
        
        
        //计算失效时间，即到服务时间的时间差
        if(orderStatus_.equals("1") || orderStatus_.equals("2") ){
        	//转化serviceTime为逾期时间
        	String overdue = this.convertServiceTime(orderInfo);
            Date DateServiceTime=null;
            try {
                DateServiceTime = DateUtil.convertStringToDate(DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN,overdue);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
            String surplusTime=this.computePastTimeV730(DateServiceTime.getTime());
            jsonObject.put("surplusTime", surplusTime);
        }

        Map<String, Object> orderNote = this.orderInfoService.getOrderNote(paramMap);

        BusinessUtil.disposeManyPath(orderInfo, "serviceTypePicPath");
        BusinessUtil.disposeManyPath(merchantInfo, "merchantIcon");
        BusinessUtil.disposeManyPath(orderNote, "picturePath", "voicePath");

        /** start 2015-12-22 王瑞 新增返回员工ID */
        // 如果用户选择服务商了，但是抢单员工ID却没有，则继续查询方案表
        int orderStatus = Integer
                .parseInt(orderInfo.get("orderStatus") == null ? "0"
                        : orderInfo.get("orderStatus").toString());
        Long receiveEmployeesId = StringUtil.nullToLong(orderInfo.get("receiveEmployeesId"));
        if (orderStatus >= 3 && receiveEmployeesId == 0 && merchantInfo != null) {
            Long merchantId = Long
                    .parseLong(merchantInfo.get("id") == null ? "0"
                            : merchantInfo.get("id").toString());
            // 查询方案表，得到抢单员工ID
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("orderId", orderId);
            map.put("merchantId", merchantId);
            receiveEmployeesId = userOrderDao
                    .getReceiveEmployeesIdFromMerchantPlan(map);
            orderInfo.put("receiveEmployeesId", receiveEmployeesId);
            String rongyunToken = userOrderDao.getRongYunToken(receiveEmployeesId);
            orderInfo.put("rongYunToken", rongyunToken);
        }
        /** end */

        // 判断订单是否过期
        boolean bool = checkServiceTime(orderInfo);
        //查询是否已经有过逾期操作
        String flag=StringUtil.null2Str(commonCacheService.getObject(CacheConstants.ORDER_PAST_TIME, StringUtil.null2Str(orderId)));
        if (bool && (orderStatus==1 || orderStatus==2) && !"1".equals(flag)) {//过期
            String code=orderStatus==1? BusinessAction.TIMEOUT_ORDER :BusinessAction.TIMEOUT_NOT_CHOOSE ;
            this.pastTime(6, 9, code, StringUtil.nullToLong(orderId), StringUtil.null2Str(orderInfo.get("serviceTime")));

            //修改返回信息
            orderInfo.put("orderStatus", 6);
            orderInfo.put("orderStatusName", dictionary.get("6"));
            orderInfo.put("orderStatusText", getOrderStatusText(6, "searchOrderStatus"));
            jsonObject.put("orderStatusText", getOrderStatusText(6, "searchOrderStatus"));
            if(jsonObject.get("surplusTime") != null){
                jsonObject.remove("surplusTime");
            }
        }

        //新订单及待选择的加入订单推送的服务商数量   2016-7-18 Revoke Yu
        if (orderInfo.get("orderStatus").equals("1") || orderInfo.get("orderStatus").equals(1)
                || orderInfo.get("orderStatus").equals("2") || orderInfo.get("orderStatus").equals(2)){
            jsonObject.put("pushMerchantCount",customOrderDao.getPushMerchantNum(Long.parseLong(orderId)));
        }

        if ("2".equals(StringUtil.null2Str(orderInfo.get("orderStatus")))  || "6".equals(StringUtil.null2Str(orderInfo.get("orderStatus")))
        		|| "7".equals(StringUtil.null2Str(orderInfo.get("orderStatus")))){
            Map<String,Object> planQueryParam = new HashMap<String,Object>(3);
            planQueryParam.put("orderId", orderId);
            planQueryParam.put("pageNo", 0);
            planQueryParam.put("orderBy", -1);
            planQueryParam.put("direction", "desc");

            JSONObject planJsonObject=this.getPricePlanList(planQueryParam);
            if (planJsonObject.getString("resultCode").equals("000")){
                jsonObject.put("totalPage", planJsonObject.getInteger("totalPage"));
                jsonObject.put("merchantPlans", planJsonObject.getJSONArray("merchantPlans"));
            }
        }

        //修订报价方案数量
        orderInfo.put("planCount", merchantPlanDao.getOrderPlanCount(Long.parseLong(orderId)));

        //服务中、待支付、交易成功  获取报价方案价格  2016-7-19 Revoke Yu
        if (orderInfo.get("orderStatus").equals("3") || orderInfo.get("orderStatus").equals(3)
                || orderInfo.get("orderStatus").equals("4") || orderInfo.get("orderStatus").equals(4)
                || orderInfo.get("orderStatus").equals("5") || orderInfo.get("orderStatus").equals(5)){
            Map<String,Object> merchantPlan=merchantPlanDao.getMerchantPlan((Long)orderInfo.get("merchantPlanId"));
            if(StringUtil.isNotEmpty(merchantPlan.get("distance"))){
                merchantInfo.put("distance", LocationUtil.showDistance(((Integer)merchantPlan.get("distance")).doubleValue()));
            }
            //修订vipStatus
            merchantInfo.put("vipStatus", -1);
			List<RuleConfig> rulConfigs=incService.getRuleConfig((Long)orderInfo.get("merchantId"));
			if (rulConfigs!=null){
				if (rulConfigs.get(0).isVipMerchantOrder()){
					merchantInfo.put("vipStatus", 2);
				}
			}
			
			//查询方案详情，显示
			Map<String,Object> planParamMap = new HashMap<String, Object>();
			planParamMap.put("startNum", 0);
			planParamMap.put("pageSize", 1);
			planParamMap.put("orderId", orderId);
			planParamMap.put("planId", orderInfo.get("merchantPlanId"));
	        List<Map<String, Object>> merchantPlans = this.userOrderDao.getOrderMerchantPlan(planParamMap);
            // 判断企业认证类型 1-企业认证 2-个人认证 0-没有认证
            judgeAuth(merchantPlans, 0);
	        getGoodsForMerchantPlans(merchantPlans);
	        Map<String, Object> selectMerchantPlan = merchantPlans.get(0);
        	
            BusinessUtil.disposeManyPath(selectMerchantPlan, "icoPath",
                    "picturePath", "voicePath");
            if (selectMerchantPlan.get("merchantId") != null) {
                paramMap.put("merchantId",
                        (Long) selectMerchantPlan.get("merchantId"));
                int count = orderInfoDao.selectEvaluationOrderNum(paramMap);
                int re = 5;
                BigDecimal score = new BigDecimal(5);
                if (count != 0) {
                    Integer totalAttitudeEvaluation = Integer
                            .parseInt(selectMerchantPlan.get(
                                    "totalAttitudeEvaluation") == null ? "0"
                                    : selectMerchantPlan.get(
                                    "totalAttitudeEvaluation")
                                    + "");
                    Integer totalQualityEvaluation = Integer
                            .parseInt(selectMerchantPlan.get(
                                    "totalQualityEvaluation") == null ? "0"
                                    : selectMerchantPlan.get(
                                    "totalQualityEvaluation")
                                    + "");
                    Integer totalSpeedEvaluation = Integer
                            .parseInt(selectMerchantPlan.get(
                                    "totalSpeedEvaluation") == null ? "0"
                                    : selectMerchantPlan.get(
                                    "totalSpeedEvaluation")
                                    + "");
                    // 总服务态度评价+总服务质量评价+总服务速度评价
                    Integer totalEvaluation = totalAttitudeEvaluation
                            + totalQualityEvaluation + totalSpeedEvaluation;
                    // 星级
                    BigDecimal starLevel = new BigDecimal(totalEvaluation)
                            .divide(new BigDecimal(count)
                                            .multiply(new BigDecimal(3)), 0,
                                    BigDecimal.ROUND_HALF_UP);
                    re = starLevel.intValue();
                    // 分数
                    score = new BigDecimal(totalEvaluation).divide(new BigDecimal(count).multiply(new BigDecimal(3)), 1, BigDecimal.ROUND_DOWN);
                }
                if (re > 5) {
                    re = 5;
                }
                if (re < 0) {
                    re = 0;
                }
                selectMerchantPlan.put("merchantPoint", re);
                selectMerchantPlan.put("score", score);
            }

            if(StringUtil.null2Str(selectMerchantPlan.get("appType")).equals("gxfw")){
                selectMerchantPlan.put("merchantType", 2);
            }else{
                selectMerchantPlan.put("merchantType", 1);
            }

            // 对用户和商户之间的距离进行编辑
            String distanceStr = LocationUtil.showDistance(StringUtil.nullToInteger(selectMerchantPlan.get("distance")).doubleValue());
            selectMerchantPlan.put("distance", distanceStr);
            //响应时间距离现在多久
//            Date joinTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, StringUtil.null2Str(merchantPlans.get(i).get("joinTime")));
//            DateUtil.
//            merchantPlans.get(i).put("", value);
        
            jsonObject.put("selectMerchantPlan", selectMerchantPlan);
        }

        //已选择、已完成的订单，查询
        if ("3".equals(StringUtil.null2Str(orderInfo.get("orderStatus"))) || "4".equals(StringUtil.null2Str(orderInfo.get("orderStatus")))
        		|| "5".equals(StringUtil.null2Str(orderInfo.get("orderStatus")))){
            Map<String,Object> serviceRecQueryParam =new HashMap<String,Object>(3);
            serviceRecQueryParam.put("merchantId", merchantInfo.get("merchantId"));
            serviceRecQueryParam.put("orderId", orderId);
            Map<String,Object> serviceRec=customOrderDao.getMerchantServiceRecord(serviceRecQueryParam);
            merchantInfo.put("payType",serviceRec.get("payType"));

            Map<String, Object> params = new HashMap<>();
            params.put("orderId", orderId);
            List<Map<String, Object>> paymentDetailList = customOrderDao.findPaymentByOrderId(params);
            jsonObject.put("paymentDetailList", paymentDetailList);
            
        }

        //对于有报价方案的过期订单，修订其提示
        if ( (orderInfo.get("orderStatus").equals("6") || orderInfo.get("orderStatus").equals(6) ) &&  (Integer)orderInfo.get("planCount")>0){
            jsonObject.put("orderStatusText", getOrderStatusText(8, "searchOrderStatus"));
        }

        //判断是否有摸金机会
        if(orderStatus_.equals("1")){
        	int  isTouchingGold=commonService.isTouchingGold();
        	jsonObject.put("isTouchingGold", isTouchingGold);
        	jsonObject.put("serversTime",DateUtil.getNowTime());
        }

        //查看是否是王牌会员
//        boolean isKing=false;
//        List<Long> userList=new ArrayList<Long>();
//        userList.add(Long.parseLong(userId));
//        Map<Long,Boolean> kingMap=kingService.confirmUsersKingIdentity(userList);
//        if(kingMap!=null){
//        	isKing=kingMap.get(Long.parseLong(userId));
//        }
//        orderInfo.put("isKing", isKing);
        
        jsonObject.put("orderInfo", orderInfo);
        jsonObject.put("orderText", orderText);
//        jsonObject.put("merchantInfo", merchantInfo);新版本没用，隐藏掉
        jsonObject.put("orderAttachment", orderNote);
        jsonObject.put("resultCode", "000");
        jsonObject.put("message", "获取用户端订单明细成功");
        
        return jsonObject;
    }

    /**
     * 更新方案状态为已读
     * @param params
     * @return
     * @throws Exception
     */
    @Transactional
    public JSONObject readMerchantPlan(Map<String, Object> params)
            throws ApplicationException,Exception {
    	JSONObject jsonObject = new ResultJSONObject("000", "更新方案已读成功");
        Long orderId = StringUtil.nullToLong(params.get("orderId"));// 订单ID
        Long planId = StringUtil.nullToLong(params.get("planId"));// 方案ID
        int count = orderInfoDao.readMerchantPlan(params);
        return jsonObject;
    }
    
    /**
     * 接单商户获取订单详情
     */
    @Override
    public JSONObject getOrderDetailForReceiver(Map<String, Object> params)
            throws Exception {

        JSONObject jsonObject = new ResultJSONObject("000", "订单详情加载成功");

        Long orderId = StringUtil.nullToLong(params.get("orderId"));// 订单ID
        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        paramMap.put("merchantId", merchantId);

        // 查询订单基础信息
        Map<String, Object> orderInfo = this.orderInfoDao.selectOrderGeneral(paramMap);
        if(orderInfo==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }

    	//转化serviceTime为逾期时间
    	String overdue = this.convertServiceTime(orderInfo);

        //如果推送记录已被清理，则直接返回   Revoke 2016.7.15
        if (!orderInfo.containsKey("merchantOrderStatus") || orderInfo.get("merchantOrderStatus")==null ){
            return new ResultJSONObject("002","订单推送记录已被清理");
        }

        if (StringUtil.isEmpty(orderInfo.get("merchantId"))){
            orderInfo.put("planCount", 0);
        }else{
            orderInfo.put("planCount", 1);
        }
        if (StringUtil.isEmpty(orderInfo.get("merchantActualPrice"))){
            orderInfo.put("serviceRecordCount", 0);
        }else{
            orderInfo.put("serviceRecordCount", 1);
        }

        int orderStatus=StringUtil.nullToInteger(orderInfo.get("orderStatus"));
        int merchantOrderStatus=StringUtil.nullToInteger(orderInfo.get("merchantOrderStatus"));
        paramMap.put("serviceType", orderInfo.get("serviceType"));
//		paramMap.put("orderStatus", orderStatus);
        paramMap.put("merchantOrderStatus", merchantOrderStatus);
        paramMap.put("userId", orderInfo.get("userId"));

        //查询缓存订单状态
        Map<String, Object> cacheOrderInfo= merchantCacheService.getCachedMerchantPushOrder(StringUtil.null2Str(merchantId), StringUtil.null2Str(orderId));
        if(cacheOrderInfo!=null && !(cacheOrderInfo.toString()).equals("{}")){
            int cacheMerchantOrderStatus=StringUtil.nullToInteger(cacheOrderInfo.get("orderStatus"));
            if(cacheMerchantOrderStatus!=0 && merchantOrderStatus!=cacheMerchantOrderStatus){//如果数据库状态和缓存状态不一样，则可能是同步没有更新，则取缓存状态
                merchantOrderStatus=cacheMerchantOrderStatus;
                orderInfo.put("merchantOrderStatus",merchantOrderStatus);
            }
        }else{
            //缓存不存在，则加入缓存
            cacheOrderInfo=customOrderDao.getMerchantOrderMap(paramMap);
            merchantCacheService.updateMerchantCachedOrder(cacheOrderInfo,StringUtil.null2Str(merchantId),StringUtil.null2Str(orderId));
        }

        //翻译订单状态
        Map<String,String> dictionary=getOrderStatusMap("receiverOrderStatusMap");
        orderInfo.put("orderStatusName", dictionary.get(merchantOrderStatus+""));

        //获取订单详情信息
        paramMap.put("isImmediate", orderInfo.get("isImmediate"));
        Object orderText=getOrderDetail(paramMap);
        if(orderText==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }

        //获取订单附件信息
        Map<String, Object> orderAttachment = this.getOrderAttachment(paramMap);
        //获取发单用户信息
        Map<String, Object> orderUserInfo = this.orderInfoDao.selectOrderUserInfo(paramMap);
        if(orderUserInfo==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }

        //查询订单服务状态记录
        List<Map<String,Object>> getTimelineList=null;
        //如果用户选择其他商家，则不返交付清单
        if(merchantOrderStatus==8){
            orderInfo.put("serviceRecordCount", 0);
            //如果没有提供报价方案，则不返回报价方案
            int planNum=merchantPlanDao.selectPlanNum(paramMap);
            if(planNum==0){
                orderInfo.put("planCount", 0);
            }
            //服务时间节点数据只留下单时间，和提交报价方案时间
            String excludeActionCode=BusinessAction.CHOOSE_ORDER+","+BusinessAction.FINISH_SERVER+","+BusinessAction.PAY_ORDER+","+BusinessAction.CANCEL_ORDER+","+","+BusinessAction.COMMENT_ORDER;
            getTimelineList=handlerTimelint(excludeActionCode,merchantId,customOrderDao.getTimeline(paramMap));
        }else{
            getTimelineList=handlerTimelint("0",merchantId,customOrderDao.getTimeline(paramMap));
        }

        //获取订单状态对应的描述
        jsonObject.put("orderStatusText", getOrderStatusText(merchantOrderStatus, "searchMerchantOrderStatus"));

        //计算失效时间，即到服务时间的时间差
        if(orderStatus==1 || orderStatus==2){
//			String joinTime=StringUtil.null2Str(orderInfo.get("joinTime"));
//			Long serviceTimeSeconds=StringUtil.nullToLong(orderInfo.get("serviceTimeStamp"));
//			String surplusTime=this.computePastTime(""+orderStatus,orderId,joinTime,serviceTimeSeconds) ;
            Date DateServiceTime=null;
            try {
                DateServiceTime = DateUtil.convertStringToDate(DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN,overdue);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
            String surplusTime=this.computePastTimeV730(DateServiceTime.getTime());
            jsonObject.put("surplusTime", surplusTime);
        }

        // 订单状态对于不同商户的区分显示编辑
//		orderStatusForMerchantShow(orderInfo, merchantId);
        // 文件路径的补全
        BusinessUtil.disposePath(orderInfo, "serviceTypePath");
        BusinessUtil.disposePath(orderUserInfo, "userPortrait");
        BusinessUtil.disposeManyPath(orderAttachment, "voicePath", "picturePath");


        //是否开启接单收费功能
        Map<String, Object> configMap=commonService.getConfigurationInfoByKey("is_open_orderPrice");
        int isOpenOrderPrice=configMap==null?1:StringUtil.nullToInteger(configMap.get("config_value"));
        if(isOpenOrderPrice==1){//开启收费功能
            // 如果订单状态不是待提供，则不需要查余额
            if (orderStatus==1 || orderStatus==2) {// 待提供状态，即可以抢单
                // 订单余额
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("merchantId", merchantId);
                // map.put("appType", appType);
                BigDecimal orderSurplusMoney = merchantPayService.getOrderSurplusMoney(map);
                BigDecimal orderPrice = merchantPayService.getOrderPrice(map);
                jsonObject.put("orderSurplusPrice", orderSurplusMoney + "");// 订单余额
                jsonObject.put("orderPrice", orderPrice + "");// 抢单费用
            }
        }
        jsonObject.put("isOpenOrderPrice", isOpenOrderPrice);

        // 判断是否是认证商户
        jsonObject.put("auth", myMerchantService.checkIsNotAuth(merchantId));
//
        // 判断订单是否过期
        boolean bool = checkServiceTime(orderInfo);
        //查询是否已经有过逾期操作
        String flag=StringUtil.null2Str(commonCacheService.getObject(CacheConstants.ORDER_PAST_TIME, StringUtil.null2Str(orderId)));
        if (bool && (merchantOrderStatus==1 || merchantOrderStatus==2) && !"1".equals(flag) ) {//过期
            String code=orderStatus==1? BusinessAction.TIMEOUT_ORDER :BusinessAction.TIMEOUT_NOT_CHOOSE ;
            this.pastTime(6, 9, code, StringUtil.nullToLong(orderId), StringUtil.null2Str(orderInfo.get("serviceTime")));

            //修改返回信息
            orderInfo.put("merchantOrderStatus",9);
            orderInfo.put("orderStatusName", dictionary.get("9"));
            orderInfo.put("orderStatusText", getOrderStatusText(9, "searchMerchantOrderStatus"));
            jsonObject.put("orderStatusText", getOrderStatusText(9, "searchMerchantOrderStatus"));
            if(jsonObject.get("surplusTime") != null){
                jsonObject.remove("surplusTime");
            }
            
            if(overdue.contains(" 00:00")){//服务时间不包含时分，则过期时间第二天
            	overdue=overdue.split(" ")[0];
            	overdue=DateUtil.getNDate(overdue, 1);
            }

            Map<String,Object> action = new HashMap<String,Object>();
            action.put("actionCode", code);
            action.put("codeName", (BusinessAction.merchantAcionMap).get(code));
            action.put("actionTime",overdue);
            getTimelineList.add(action);
        }

        jsonObject.put("orderInfo", orderInfo);
        jsonObject.put("orderText",orderText );
        jsonObject.put("orderAttachment", orderAttachment);
        jsonObject.put("orderUserInfo", orderUserInfo);
        jsonObject.put("getTimelineList", getTimelineList);
//		//将此订单状态置为已读
//		params.put("isRead", 1);
//		customOrderDao.setReadStatus(params);
        return jsonObject;
    }



    /**
     * 接单商户获取订单详情
     */
    @Override
    public JSONObject getOrderDetailForReceiverV730(Map<String, Object> params)	throws Exception {

        JSONObject jsonObject = new ResultJSONObject("000", "订单详情加载成功");

        Long orderId = StringUtil.nullToLong(params.get("orderId"));// 订单ID
        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        paramMap.put("merchantId", merchantId);

        // 查询订单基础信息
        Map<String, Object> orderInfo = this.orderInfoDao.selectOrderGeneral(paramMap);
        if(orderInfo==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }
        
        orderInfo.put("orderPrice", orderInfo.get("planPrice"));
        orderInfo.remove("planPrice");

        //如果推送记录已被清理，则直接返回   Revoke 2016.7.15
        if (!orderInfo.containsKey("merchantOrderStatus") || orderInfo.get("merchantOrderStatus")==null ){
            return new ResultJSONObject("002","订单推送记录已被清理");
        }

        if (StringUtil.isEmpty(orderInfo.get("merchantId"))){
            orderInfo.put("planCount", 0);
        }else{
            orderInfo.put("planCount", 1);
        }
        if (StringUtil.isEmpty(orderInfo.get("merchantActualPrice"))){
            orderInfo.put("serviceRecordCount", 0);
        }else{
            orderInfo.put("serviceRecordCount", 1);
        }

        int orderStatus=StringUtil.nullToInteger(orderInfo.get("orderStatus"));
        int merchantOrderStatus=StringUtil.nullToInteger(orderInfo.get("merchantOrderStatus"));
        paramMap.put("serviceType", orderInfo.get("serviceType"));
//		paramMap.put("orderStatus", orderStatus);
        paramMap.put("merchantOrderStatus", merchantOrderStatus);
        paramMap.put("userId", orderInfo.get("userId"));

        //查询缓存订单状态
        Map<String, Object> cacheOrderInfo= merchantCacheService.getCachedMerchantPushOrder(StringUtil.null2Str(merchantId), StringUtil.null2Str(orderId));
        if(cacheOrderInfo!=null && !(cacheOrderInfo.toString()).equals("{}")){
            int cacheMerchantOrderStatus=StringUtil.nullToInteger(cacheOrderInfo.get("orderStatus"));
            if(cacheMerchantOrderStatus!=0 && merchantOrderStatus!=cacheMerchantOrderStatus){//如果数据库状态和缓存状态不一样，则可能是同步没有更新，则取缓存状态
                merchantOrderStatus=cacheMerchantOrderStatus;
                orderInfo.put("merchantOrderStatus",merchantOrderStatus);
            }
        }else{
            //缓存不存在，则加入缓存
            cacheOrderInfo=customOrderDao.getMerchantOrderMap(paramMap);
            merchantCacheService.updateMerchantCachedOrder(cacheOrderInfo,StringUtil.null2Str(merchantId),StringUtil.null2Str(orderId));
        }

        //翻译订单状态
        Map<String,String> dictionary=getOrderStatusMap("receiverOrderStatusMap");
        orderInfo.put("orderStatusName", dictionary.get(merchantOrderStatus+""));

        //获取订单详情信息
        paramMap.put("isImmediate", orderInfo.get("isImmediate"));
        Object orderText=getOrderDetail(paramMap);
        if(orderText==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }

        //获取订单附件信息
        Map<String, Object> orderAttachment = this.getOrderAttachment(paramMap);
        //获取发单用户信息
        Map<String, Object> orderUserInfo = this.orderInfoDao.selectOrderUserInfo(paramMap);
        if(orderUserInfo==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }

        //查询订单服务状态记录
//		List<Map<String,Object>> getTimelineList=null;
        //如果用户选择其他商家，则不返交付清单
        if(merchantOrderStatus==8){
            orderInfo.put("serviceRecordCount", 0);
            //如果没有提供报价方案，则不返回报价方案
            int planNum=merchantPlanDao.selectPlanNum(paramMap);
            if(planNum==0){
                orderInfo.put("planCount", 0);
            }
            //服务时间节点数据只留下单时间，和提交报价方案时间
//			String excludeActionCode=BusinessAction.CHOOSE_ORDER+","+BusinessAction.FINISH_SERVER+","+BusinessAction.PAY_ORDER+","+BusinessAction.CANCEL_ORDER+","+","+BusinessAction.COMMENT_ORDER;
//			getTimelineList=handlerTimelint(excludeActionCode,merchantId,customOrderDao.getTimeline(paramMap));
        }else{
//			getTimelineList=handlerTimelint("0",merchantId,customOrderDao.getTimeline(paramMap));
        }

        //获取订单状态对应的描述
        jsonObject.put("orderStatusText", getOrderStatusText(merchantOrderStatus, "searchMerchantOrderStatus"));
        String overdue = StringUtil.null2Str(orderInfo.get("serviceTime"));
        //计算失效时间，即到服务时间的时间差
        if(orderStatus==1 || orderStatus==2){
        	//转化serviceTime为逾期时间
        	overdue = this.convertServiceTime(orderInfo);
        	
//			String joinTime=StringUtil.null2Str(orderInfo.get("joinTime"));
//			Long serviceTimeSeconds=StringUtil.nullToLong(orderInfo.get("serviceTimeStamp"));
//			String surplusTime=this.computePastTime(""+orderStatus,orderId,joinTime,serviceTimeSeconds) ;
            Date DateServiceTime=null;
            try {
                DateServiceTime = DateUtil.convertStringToDate(DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN,overdue);
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }
            String surplusTime=this.computePastTimeV730(DateServiceTime.getTime());
            jsonObject.put("surplusTime", surplusTime);
        }

        //查看自己的报价方案简介，包含时间,价格和描述
        Map<String,Object> orderPricePlan=customOrderDao.getOrderPlanBrief(paramMap);
        if(orderPricePlan!=null){
            orderPricePlan.put("orderPrice", orderPricePlan.get("discountPrice"));
            orderPricePlan.remove("discountPrice");            
        
            //查询报价方案商品信息
            Long planId=StringUtil.nullToLong(orderPricePlan.get("planId"));
            params.put("planId", planId);
            List<Map<String,Object>> orderPlanGoodsList=customOrderDao.getOrderPlanGoodsList(params);
            //地址加前缀
            BusinessUtil.disposeManyPath(orderPlanGoodsList, "goodsPictureUrl");
            orderPricePlan.put("orderPlanGoodsList", orderPlanGoodsList);           
        }
        BusinessUtil.disposeManyPath(orderPricePlan, "paths");

        // 订单状态对于不同商户的区分显示编辑
//		orderStatusForMerchantShow(orderInfo, merchantId);
        // 文件路径的补全
        BusinessUtil.disposePath(orderInfo, "serviceTypePath");
        BusinessUtil.disposePath(orderUserInfo, "userPortrait");
        BusinessUtil.disposeManyPath(orderAttachment, "voicePath", "picturePath");
//
        // 判断订单是否过期
        boolean bool = checkServiceTime(orderInfo);
        //查询是否已经有过逾期操作
        String flag=StringUtil.null2Str(commonCacheService.getObject(CacheConstants.ORDER_PAST_TIME, StringUtil.null2Str(orderId)));
        if (bool && (merchantOrderStatus==1 || merchantOrderStatus==2) && !"1".equals(flag) ) {//过期
            String code=orderStatus==1? BusinessAction.TIMEOUT_ORDER :BusinessAction.TIMEOUT_NOT_CHOOSE ;
            this.pastTime(6, 9, code, StringUtil.nullToLong(orderId), StringUtil.null2Str(orderInfo.get("serviceTime")));

            //修改返回信息
            orderInfo.put("merchantOrderStatus",9);
            orderInfo.put("orderStatusName", dictionary.get("9"));
            orderInfo.put("orderStatusText", getOrderStatusText(9, "searchMerchantOrderStatus"));
            jsonObject.put("orderStatusText", getOrderStatusText(9, "searchMerchantOrderStatus"));
            if(jsonObject.get("surplusTime") != null){
                jsonObject.remove("surplusTime");
            }
            
            if(overdue.contains(" 00:00")){//服务时间不包含时分，则过期时间第二天
            	overdue=overdue.split(" ")[0];
            	overdue=DateUtil.getNDate(overdue, 1);
            }

//			Map<String,Object> action = new HashMap<String,Object>();
//			action.put("actionCode", code);
//			action.put("codeName", (BusinessAction.merchantAcionMap).get(code));
//			action.put("actionTime",serviceTime);
//			getTimelineList.add(action);
        }
        
        jsonObject.put("orderInfo", orderInfo);
        jsonObject.put("orderText",orderText );
        jsonObject.put("orderAttachment", orderAttachment);
        jsonObject.put("orderUserInfo", orderUserInfo);
//		jsonObject.put("getTimelineList", getTimelineList);
        jsonObject.put("orderPricePlan",orderPricePlan);//商户报价方案，状态=2时查询，其余状态为空
//		//将此订单状态置为已读
//		params.put("isRead", 1);
//		customOrderDao.setReadStatus(params);
        return jsonObject;
    }

    /**
     * 接单商户获取订单详情 V1110
     */
    @Override
    public JSONObject getOrderDetailForReceiverV1110(Map<String, Object> params)	throws Exception {

        JSONObject jsonObject = new ResultJSONObject("000", "订单详情加载成功");

        Long orderId = StringUtil.nullToLong(params.get("orderId"));// 订单ID
        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        paramMap.put("merchantId", merchantId);

        // 查询订单基础信息
        Map<String, Object> orderInfo = this.orderInfoDao.selectOrderGeneral(paramMap);
        if(orderInfo==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }
        
        orderInfo.put("orderPrice", orderInfo.get("planPrice"));
        orderInfo.remove("planPrice");

        //如果推送记录已被清理，则直接返回   Revoke 2016.7.15
        if (!orderInfo.containsKey("merchantOrderStatus") || orderInfo.get("merchantOrderStatus")==null ){
            return new ResultJSONObject("002","订单推送记录已被清理");
        }

        int orderStatus=StringUtil.nullToInteger(orderInfo.get("orderStatus"));
        int merchantOrderStatus=StringUtil.nullToInteger(orderInfo.get("merchantOrderStatus"));
        paramMap.put("serviceType", orderInfo.get("serviceType"));
//		paramMap.put("orderStatus", orderStatus);
        paramMap.put("merchantOrderStatus", merchantOrderStatus);
        paramMap.put("userId", orderInfo.get("userId"));

        //查询缓存订单状态
        Map<String, Object> cacheOrderInfo= merchantCacheService.getCachedMerchantPushOrder(StringUtil.null2Str(merchantId), StringUtil.null2Str(orderId));
        if(cacheOrderInfo!=null && !(cacheOrderInfo.toString()).equals("{}")){
            int cacheMerchantOrderStatus=StringUtil.nullToInteger(cacheOrderInfo.get("orderStatus"));
            if(cacheMerchantOrderStatus!=0 && merchantOrderStatus!=cacheMerchantOrderStatus){//如果数据库状态和缓存状态不一样，则可能是同步没有更新，则取缓存状态
                merchantOrderStatus=cacheMerchantOrderStatus;
                orderInfo.put("merchantOrderStatus",merchantOrderStatus);
            }
        }else{
            //缓存不存在，则加入缓存
            cacheOrderInfo=customOrderDao.getMerchantOrderMap(paramMap);
            merchantCacheService.updateMerchantCachedOrder(cacheOrderInfo,StringUtil.null2Str(merchantId),StringUtil.null2Str(orderId));
        }

        //翻译订单状态
        Map<String,String> dictionary=getOrderStatusMap("merchantOrderStatusV1110");
        orderInfo.put("orderStatusName", dictionary.get(merchantOrderStatus+""));

        //获取订单详情信息
        paramMap.put("isImmediate", orderInfo.get("isImmediate"));
        Object orderText=getOrderDetail(paramMap);
        if(orderText==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }

        //获取订单附件信息
        Map<String, Object> orderAttachment = this.getOrderAttachment(paramMap);
        //获取发单用户信息
        Map<String, Object> orderUserInfo = this.orderInfoDao.selectOrderUserInfo(paramMap);
        if(orderUserInfo==null){
            return new ResultJSONObject("001", "订单详情加载失败");
        }
        
        String descp="";
        if(merchantOrderStatus==1){
        	//查看下单距离现在多长时间
            String joinTime=StringUtil.null2Str(orderInfo.get("joinTime"));//下单时间   
            String releaseToNowTime=BusinessUtil.getReleaseTimeByOrder(joinTime);
            orderInfo.put("releaseToNowTime", releaseToNowTime);
            //查看订单报价方案数量
            int orderPlanCount=customOrderDao.getOrderPlanCount(params);
            if(orderPlanCount==0){
        		descp="当前还没有其它服务商响应";
        	}else{
        		descp="已有"+orderPlanCount+"个服务商响应";
        	}
        }else if (merchantOrderStatus==2){
        	Map<String,Object> orderPricePlan=this.getOrderPlanFromOrderDetailPage( params);
        	jsonObject.put("orderPricePlan",orderPricePlan);
        	int isRead=StringUtil.nullToInteger(orderPricePlan.get("isRead"));
        	if(isRead==1){//用户已查看服务商报价
        		descp="客户已查看方案";
        	}else{
        		descp="等待客户查看方案";
        	}
        }else if (merchantOrderStatus==3 || merchantOrderStatus==4){//当状态未4是，是老的订单拿在新的客户端展示
        	Map<String,Object> orderPricePlan=this.getOrderPlanFromOrderDetailPage( params);
            jsonObject.put("orderPricePlan",orderPricePlan);//商户报价方案，状态=2时查询，其余状态为空

            //支付信息
        	List<Map<String,Object>> orderPayList=customOrderDao.getOrderPayList(params);
        	jsonObject.put("orderPayList",orderPayList);
            //查询付款信息
    		if(orderPayList!=null && orderPayList.size()!=0){
    			descp="客户已向你付款 "+orderPayList.size()+"次";
    		}
    		if(merchantOrderStatus==4){
    			orderInfo.put("isOldOrder", 1);
    		}
       
        }else if (merchantOrderStatus==5 ){//订单完成
        	int isEvaluate=0;
        	//支付信息
        	List<Map<String,Object>> orderPayList=customOrderDao.getOrderPayList(params);
        	jsonObject.put("orderPayList",orderPayList);
        	//评价信息
        	Map<String,Object> orderEvaluation=new HashMap<String, Object>();
        	Map<String,Object> orderEvaluation_=customOrderDao.getOrderEvaluationInfo(params);
        	if(orderEvaluation_ != null){
            	//计算总的星级
        		int attitudeEvaluation =  StringUtil.nullToInteger(orderEvaluation_.get("attitudeEvaluation"));//服务态度
        		int qualityEvaluation =  StringUtil.nullToInteger(orderEvaluation_.get("qualityEvaluation"));//服务质量
        		int speedEvaluation =  StringUtil.nullToInteger(orderEvaluation_.get("speedEvaluation"));//服务速度
                // 总服务态度评价+总服务质量评价+总服务速度评价
                int totalEvaluation = attitudeEvaluation + qualityEvaluation + speedEvaluation;
                // 星级
                int starLevel = totalEvaluation/3;
                orderEvaluation.put("joinTime", orderEvaluation_.get("joinTime"));
                orderEvaluation.put("textEvaluation", orderEvaluation_.get("textEvaluation"));
                orderEvaluation.put("starLevel", starLevel);
                orderEvaluation.put("attachmentPaths", orderEvaluation_.get("attachmentPaths"));
                isEvaluate=1;
        	}
        	jsonObject.put("evaluate",isEvaluate);
        	BusinessUtil.disposeManyPath(orderEvaluation, "attachmentPaths");
        	jsonObject.put("orderEvaluation",orderEvaluation);
        	
        	//报价方案信息
        	Map<String,Object> orderPricePlan=this.getOrderPlanFromOrderDetailPage( params);
            jsonObject.put("orderPricePlan",orderPricePlan);//商户报价方案，状态=2时查询，其余状态为空
        }else if (merchantOrderStatus==7 ){//关闭状态
        	//商户以提供报价方案，被用户取消订单
        	Map<String,Object> orderPricePlan=this.getOrderPlanFromOrderDetailPage( params);
            jsonObject.put("orderPricePlan",orderPricePlan);//商户报价方案，状态=2时查询，其余状态为空
        }else if (merchantOrderStatus==8 ){//用户选择其他服务商
        	descp="客户选择了其它服务商";
        	//商户以提供报价方案，被用户取消订单
        	Map<String,Object> orderPricePlan=this.getOrderPlanFromOrderDetailPage( params);
            jsonObject.put("orderPricePlan",orderPricePlan);//商户报价方案，状态=2时查询，其余状态为空
        }else if (merchantOrderStatus==9){//订单逾期
        	//订单逾期，如果提供了报价方案则查询
        	Map<String,Object> orderPricePlan=this.getOrderPlanFromOrderDetailPage( params);
            jsonObject.put("orderPricePlan",orderPricePlan);//商户报价方案，状态=2时查询，其余状态为空
        }else{
        	
        }
        orderInfo.put("descp", descp);
        
        //计算逾期时间
        String overdue = StringUtil.null2Str(orderInfo.get("serviceTime"));
        
        // 文件路径的补全
        BusinessUtil.disposePath(orderInfo, "serviceTypePath");
        BusinessUtil.disposePath(orderUserInfo, "userPortrait");
        BusinessUtil.disposeManyPath(orderAttachment, "voicePath", "picturePath");
//
        // 判断订单是否过期
        boolean bool = checkServiceTime(orderInfo);
        //查询是否已经有过逾期操作
        String flag=StringUtil.null2Str(commonCacheService.getObject(CacheConstants.ORDER_PAST_TIME, StringUtil.null2Str(orderId)));
        if (bool && (merchantOrderStatus==1 || merchantOrderStatus==2) && !"1".equals(flag) ) {//过期
            String code=orderStatus==1? BusinessAction.TIMEOUT_ORDER :BusinessAction.TIMEOUT_NOT_CHOOSE ;
            this.pastTime(6, 9, code, StringUtil.nullToLong(orderId), StringUtil.null2Str(orderInfo.get("serviceTime")));

            //修改返回信息
            orderInfo.put("merchantOrderStatus",9);
            orderInfo.put("orderStatusName", dictionary.get("9"));
            orderInfo.put("orderStatusText", getOrderStatusText(9, "searchMerchantOrderStatus"));
            jsonObject.put("orderStatusText", getOrderStatusText(9, "searchMerchantOrderStatus"));            
            
            if(overdue.contains(" 00:00")){//服务时间不包含时分，则过期时间第二天
            	overdue=overdue.split(" ")[0];
            	overdue=DateUtil.getNDate(overdue, 1);
            }
        }
        
        //查看是否是王牌会员
        Long userId=StringUtil.nullToLong(orderInfo.get("userId"));
        boolean isKing=false;
        List<Long> userList=new ArrayList<Long>();
        userList.add(userId);
        Map<Long,Boolean> kingMap=kingService.confirmUsersKingIdentity(userList);
        if(kingMap!=null){
        	isKing=kingMap.get(userId);
        }
        orderUserInfo.put("isKing", isKing);

        jsonObject.put("orderInfo", orderInfo);
        jsonObject.put("orderText",orderText );
        jsonObject.put("orderAttachment", orderAttachment);
        jsonObject.put("orderUserInfo", orderUserInfo);
        return jsonObject;
    }
    
    /**
     * 计算失效时间
     //	 * @param serviceTime 服务时间
     //	 * @param currentTime 当前时间
     * @return  
     */
    public String computePastTime(String status,Long orderId,String joinTime,long serviceTimeSeconds){

        Long nowTimeSeconds=System.currentTimeMillis()/1000;

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long joinTimeSeconds=0;
        try{
            Date date=sdf.parse(StringUtil.null2Str(joinTime));
            joinTimeSeconds=date.getTime()/1000;
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        int expiresDay=0;
        int expiresChoiceDay=0;
        //未提供报价方案的过期时间
        Map<String, Object> expiresDayMap = commonService.getConfigurationInfoByKey("expires_day");
        if (expiresDayMap != null) {
            expiresDay = expiresDayMap.get("config_value")==null?7:StringUtil.nullToInteger(expiresDayMap.get("config_value"));
        }
        //已提供报价方案的过期时间
        Map<String, Object> expiresChoiceDayMap = commonService.getConfigurationInfoByKey("expires_choice_day");
        if (expiresChoiceDayMap != null) {
            expiresChoiceDay = expiresChoiceDayMap.get("config_value")==null?7:StringUtil.nullToInteger(expiresChoiceDayMap.get("config_value"));
        }

        long time1=0;
        long time2=0;
        if(StringUtil.nullToInteger(status)==1){
            time1=3600*24*expiresDay;
        }else if (StringUtil.nullToInteger(status)==2){
            time1=3600*24*expiresChoiceDay;
        }else{
            time1=3600*24*7;
        }
        if(serviceTimeSeconds-joinTimeSeconds > time1){//超过过期天数（7天）
            time2=nowTimeSeconds-joinTimeSeconds;
        }else{
            time2=serviceTimeSeconds;
        }

        return DateUtil.getTimeFromBetweenTimes(time1, time2);

    }

    public String computePastTimeV730(long serviceTimeSeconds){
        String result="";
        //分钟差值
        long  remainder = (serviceTimeSeconds - System.currentTimeMillis())/60000 ;
        if (remainder<0){
            result="0分钟";
        }
        else if (remainder<60){
            result=remainder+"分钟";
        }else if (remainder<1440){
            result=remainder/60+"小时";
        }else{
            result=remainder/1440+"天";
        }
        return result;
    }

    /**
     * 处理订单服务状态记录
     * @param timelineList
     */
    public List<Map<String,Object>> handlerTimelint(String excludeActionCode,Long merchantId,List<Map<String,Object>> timelineList){
        List<Map<String,Object>> result=new ArrayList<Map<String,Object>>();
        for(Map<String,Object> map : timelineList){
            boolean temp=true;
            String code=StringUtil.null2Str(map.get("actionCode"));
            Long merchantId_=StringUtil.nullToLong(map.get("merchantId"));
            String codeName=(BusinessAction.merchantAcionMap).get(code);
            for(String excludeCode:excludeActionCode.split(",")){
                if(code.equals(excludeCode)){
//					timelineList.remove(map);
                    temp=false;
                }
            }
            if(code.equals("300")){//提供报价方案，有多条记录，商户筛选出自己的
                if((long)merchantId!=(long)merchantId_){
//					timelineList.remove(map);
                    temp=false;
                }
            }
            if(temp){
                map.put("codeName", codeName);
                result.add(map);
            }
        }
        return result;
    }
    /**
     * 用户端--处理订单服务状态记录
     * @param timelineList
     */
    public List<Map<String,Object>> handlerUserTimelint(List<Map<String,Object>> timelineList){
        List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
        for(Map<String,Object> map : timelineList){
            String code=StringUtil.null2Str(map.get("actionCode"));
            if (code.equals(BusinessAction.BID_ORDER)){
                continue;  //用户端不需要显示商户报价时间
            }
            String codeName=(BusinessAction.merchantAcionMap).get(code);

            Map<String,Object> action = new HashMap<String,Object>();
            action.put("actionCode", code);
            action.put("codeName", codeName);
            action.put("actionTime", map.get("actionTime"));
            result.add(action);
        }

        return result;
    }



    /**
     * 获取订单附件信息图片，声音
     * @param paramMap
     * @return
     */
    public Map<String, Object> getOrderAttachment(Map<String, Object> paramMap) {
        Map<String, Object> orderNote = new HashMap<String, Object>();
        List<Map<String, Object>> attachments = this.orderInfoDao.selectOrderAttachment(paramMap);
        if(attachments != null && attachments.size() > 0) {
            Map<String, String> temp = new HashMap<String, String>();
            for(Map<String, Object> attachment : attachments) {
                Integer type = (Integer)attachment.get("type");
                if(type == 1){
                    String picturePath = temp.get("picturePath");
                    if(picturePath == null) {
                        temp.put("picturePath", (String)attachment.get("path"));
                    } else {
                        StringBuilder str=new StringBuilder();
                        str.append(picturePath).append(",").append((String)attachment.get("path"));
                        temp.put("picturePath", str.toString());
                    }
                } else if (type == 2) {
                    String voicePath = temp.get("voicePath");
                    if(voicePath == null) {
                        temp.put("voicePath", (String)attachment.get("path"));
                    } else {
                        StringBuilder str=new StringBuilder();
                        str.append(voicePath).append(",").append((String)attachment.get("path"));
                        temp.put("voicePath", str.toString());
                    }
                }
            }

            orderNote.putAll(temp);
        }
        return orderNote;
    }
    /**
     * 从数据库冲查询Top发单用户的订单
     *
     * @param userId
     * @param ORDER_PER_USER_SIZE
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> getTopnBasicOrderListInfo(Long userId,int ORDER_PER_USER_SIZE,String version) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("startNum", 0);
        params.put("pageSize", ORDER_PER_USER_SIZE);
        List<Map<String, Object>> resultList = this.customOrderDao
                .getTopnBasicOrderListInfo(params);
        // 文件路径的补全
        for (Map<String, Object> orderMap : resultList) {
            BusinessUtil.disposeManyPath(orderMap, "merchantIcon");
        }
        if (resultList != null && resultList.size() > 0) {

            Map<String,String> dictionary = new HashMap<String, String>();
            if(version != null && "V1110".equals(version)){//新旧版本共存
            	dictionary = getOrderStatusMap("senderOrderStatusMapV1110");
            }else{
            	dictionary = getOrderStatusMap("senderOrderStatusMap");
            }
            for(Map<String,Object> order:resultList){
                order.put("orderStatusName", dictionary.get(order.get("orderStatus").toString()));
            }

            userRelatedCacheServices.cacheUserOrders(resultList, "" + userId);
        }
        return resultList;
    }

    /**
     * 根据缓存的订单来过滤复合条件的记录。
     *
     * @param cachedOrders
     * @param resultList
     * @param catalogId
     * @param orderStatus
     * @param pageNo
     * @return
     */
    private int constructResultFromCachedOrders (
            List<Map<String, Object>> cachedOrders,
            List<Map<String, Object>> resultList, Long catalogId,
            String orderStatus, int pageNo) throws Exception {
        Long nowSec = System.currentTimeMillis()/1000;
        List<Map<String, Object>> filterOrders = new ArrayList<Map<String, Object>>();

        if (cachedOrders == null || cachedOrders.size() < 1) {
            return 0;
        }
        // 获取appType下所属的serviceTypeId,然后匹配
        List<Map<String, Object>> serviceTypeIdsList = new ArrayList<Map<String, Object>>();
        if (catalogId != null) {
            serviceTypeIdsList =commonService.getServiceTypesByCatalogIdForOrder(StringUtil.nullToInteger(catalogId));
        }
        for (Map<String, Object> record : cachedOrders) {
            if(record==null){
                continue;
            }
            String cacheServiceType = StringUtil.null2Str(record.get("serviceType"));
            if (catalogId != null) {
                int temp = 0;
                for (Map<String, Object> map : serviceTypeIdsList) {
                    String id = StringUtil.null2Str(map.get("id"));
                    if (id.equals(cacheServiceType)) {
                        temp = 1;
                    }
                }
                if (temp == 0) {
                    continue;
                }
            }

            if (orderStatus != null
                    && !orderStatus.equals("")
                    && !isCloseOrder(orderStatus,record.get("orderStatus").toString())
                    && !(record.get("orderStatus").toString())
                    .equals(orderStatus)) {
                continue;
            }

            //对于查询待选择和新预约订单列表的，如果服务时间过期，则也跳过。
            if ("1".equals(orderStatus) || "2".equals(orderStatus)){
            	//转化serviceTime为逾期时间
            	String overdue = this.convertServiceTime(record);
                Long serviceTimeSec = DateUtil.getMicorSeconds(DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN,overdue)/1000;
            	
                if (nowSec>serviceTimeSec){
                    continue;
                }
            }


            Map<String,String> dictionary = new HashMap<String, String>();
            dictionary = getOrderStatusMap("senderOrderStatusMap");
            
            record.put("orderStatusName", dictionary.get(record.get("orderStatus").toString()));
            filterOrders.add(record);
        }
        TimeDescComparator cp = new TimeDescComparator("orderId");
        Collections.sort(filterOrders, cp);

        if (filterOrders.size() < 1
                || pageNo * Constant.PAGESIZE > filterOrders.size()) {
            return 0;
        } else {
            for (int num = 0, startIndex = pageNo * Constant.PAGESIZE; num < Constant.PAGESIZE
                    && startIndex < filterOrders.size(); num++, startIndex++) {
                resultList.add(filterOrders.get(startIndex));
            }

            //补充服务商报价方案数量---------2016.7.18   Revoke
            addPlanCountForSendList(resultList);

            //补充过期时间的计算-----------2016.7.18    Revoke
            addSurplusTimeForSendList(resultList,null);

            return 1 + (filterOrders.size() - 1) / Constant.PAGESIZE;
        }

    }

    /**
     * 根据缓存的订单来过滤复合条件的记录。 ----------V1110
     *
     * @param cachedOrders
     * @param resultList
     * @param catalogId
     * @param orderStatus
     * @param pageNo
     * @return
     */
    private int constructResultFromCachedOrdersV1110 (
            List<Map<String, Object>> cachedOrders,
            List<Map<String, Object>> resultList, Long catalogId,
            String orderStatus, int pageNo) throws Exception {
        Long nowSec = System.currentTimeMillis()/1000;
        List<Map<String, Object>> filterOrders = new ArrayList<Map<String, Object>>();

        if (cachedOrders == null || cachedOrders.size() < 1) {
            return 0;
        }
        // 获取appType下所属的serviceTypeId,然后匹配
        List<Map<String, Object>> serviceTypeIdsList = new ArrayList<Map<String, Object>>();
        if (catalogId != null) {
            serviceTypeIdsList =commonService.getServiceTypesByCatalogIdForOrder(StringUtil.nullToInteger(catalogId));
        }

//        List<Long> userList=new ArrayList<Long>();
//        for(Map<String, Object> map: cachedOrders){
//            Long userId=StringUtil.nullToLong(map.get("userId"));
//            userList.add(userId);
//        }
        //查询用户是否是王牌会员
//        Map<Long,Boolean> kingMap=kingService.confirmUsersKingIdentity(userList);
        for (Map<String, Object> record : cachedOrders) {
            if(record==null){
                continue;
            }
            String cacheServiceType = StringUtil.null2Str(record.get("serviceType"));
            if (catalogId != null) {
                int temp = 0;
                for (Map<String, Object> map : serviceTypeIdsList) {
                    String id = StringUtil.null2Str(map.get("id"));
                    if (id.equals(cacheServiceType)) {
                        temp = 1;
                    }
                }
                if (temp == 0) {
                    continue;
                }
            }
            if("3".equals(orderStatus)){
            	orderStatus = "3,4";//新版app查看旧数据，需要兼容"待支付"状态为"已响应"
            }
            if (orderStatus != null
                    && !orderStatus.equals("")
                    && !isCloseOrder(orderStatus,record.get("orderStatus").toString())
                    && !orderStatus.contains((record.get("orderStatus").toString()))) {
                continue;
            }

            //对于查询待选择和新预约订单列表的，如果服务时间过期，则也跳过。
            if ("1".equals(orderStatus) || "2".equals(orderStatus)){
            	//转化serviceTime为逾期时间
            	String overdue = this.convertServiceTime(record);
                Long serviceTimeSec = DateUtil.getMicorSeconds(DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN,overdue)/1000;
            	
                if (nowSec>serviceTimeSec){
                    continue;
                }
            }


            Map<String,String> dictionary = new HashMap<String, String>();
            dictionary = getOrderStatusMap("senderOrderStatusMapV1110");
            
            record.put("orderStatusName", dictionary.get(record.get("orderStatus").toString()));
            //查询是否是王牌会员
//            if(kingMap!=null){
//            	Long userId=StringUtil.nullToLong(record.get("userId"));
//            	record.put("isKing", kingMap.get(userId));
//            }
            
            filterOrders.add(record);
        }
        TimeDescComparator cp = new TimeDescComparator("orderId");
        Collections.sort(filterOrders, cp);

        if (filterOrders.size() < 1
                || pageNo * Constant.PAGESIZE > filterOrders.size()) {
            return 0;
        } else {
            for (int num = 0, startIndex = pageNo * Constant.PAGESIZE; num < Constant.PAGESIZE
                    && startIndex < filterOrders.size(); num++, startIndex++) {
                resultList.add(filterOrders.get(startIndex));
            }

            //补充服务商报价方案数量---------2016.7.18   Revoke
            addPlanCountForSendList(resultList);

            //补充过期时间的计算-----------2016.7.18    Revoke
            addSurplusTimeForSendList(resultList,"V1110");

            return 1 + (filterOrders.size() - 1) / Constant.PAGESIZE;
        }

    }

    private int constructResultFromMerchantCachedOrders (
            List<Map<String, Object>> orderList,
            List<Map<String, Object>> resultList, Map<String,Object> paramMap, int pageNo) throws Exception {

        List<Map<String, Object>> filterOrders = new ArrayList<Map<String, Object>>();

        if (orderList == null || orderList.size() < 1) {
            return 0;
        }

        int orderStatus=StringUtil.nullToInteger(paramMap.get("status"));

        Map<String,String> dictionary=getOrderStatusMap("receiverOrderStatusMap");

        for (Map<String, Object> record : orderList) {
            if(record==null){
                continue;
            }

        	//转化serviceTime为逾期时间
        	this.convertServiceTime(record);
            
            int orderStatus_=StringUtil.nullToInteger(record.get("orderStatus"));

            //根据订单状态筛选
            if (orderStatus != 0
                    && !isCloseOrderForMerchant(StringUtil.null2Str(orderStatus),StringUtil.null2Str(orderStatus_))
                    && orderStatus != orderStatus_) {
                continue;
            }

            //查看是否过期
            boolean bool = checkServiceTime(record);
            if ( bool && (StringUtil.nullToInteger(orderStatus_)==1 || StringUtil.nullToInteger(orderStatus_)==2) ) {//过期
                if(orderStatus==0){//全部状态查询，其余状态查询则过滤掉
                	record.put("orderStatus", 9);
                	record.put("orderStatusName",dictionary.get(9+""));
                }else{
                	 continue;
                }
            }
            
            filterOrders.add(record);
        }
        TimeDescComparator cp = new TimeDescComparator("orderId");
        Collections.sort(filterOrders, cp);
        if (filterOrders.size() < 1
                || pageNo * Constant.PAGESIZE > filterOrders.size()) {
            return 0;
        } else {
            for (int num = 0, startIndex = pageNo * Constant.PAGESIZE; num < Constant.PAGESIZE
                    && startIndex < filterOrders.size(); num++, startIndex++) {
                resultList.add(filterOrders.get(startIndex));
            }
            return 1 + (filterOrders.size() - 1) / Constant.PAGESIZE;
        }

    }
    
    private int constructResultFromMerchantCachedOrdersV110 (
            List<Map<String, Object>> orderList,
            List<Map<String, Object>> resultList, Map<String,Object> paramMap, int pageNo) throws Exception {

        List<Map<String, Object>> filterOrders = new ArrayList<Map<String, Object>>();

        if (orderList == null || orderList.size() < 1) {
            return 0;
        }

        //1，未响应，2已向应，0已结束
        int orderStatus=StringUtil.nullToInteger(paramMap.get("status"));

        Map<String,String> dictionary=getOrderStatusMap("receiverOrderStatusMap");

        for (Map<String, Object> record : orderList) {
            if(record==null){
                continue;
            }

            int orderStatus_=StringUtil.nullToInteger(record.get("orderStatus"));
            
            //如果订单只是推送到商户，但是商户未对订单报价，且用户取消订单，用户订单消失在本商户
//            int userOrderStatus=StringUtil.nullToInteger(record.get("userOrderStatus"));
//            if(orderStatus_ == 1 && userOrderStatus == 7){
//                continue;
//            }
             
        	//转化serviceTime为逾期时间
        	this.convertServiceTime(record);

            //根据订单状态筛选出结果
           boolean res=screenOutByOrderStatus(orderStatus,orderStatus_);
           if(!res){
        	   continue;
           }

            //查看是否过期
            boolean bool = checkServiceTime(record);
            if ( bool && (StringUtil.nullToInteger(orderStatus_)==1 || StringUtil.nullToInteger(orderStatus_)==2) ) {//过期
                if(orderStatus==0){//全部状态查询，其余状态查询则过滤掉
                	record.put("orderStatus", 9);
                	record.put("orderStatusName",dictionary.get(9+""));
                }else{
                	 continue;
                }
            }
            
            filterOrders.add(record);
        }
        TimeDescComparator cp = new TimeDescComparator("orderId");
        Collections.sort(filterOrders, cp);
        if (filterOrders.size() < 1
                || pageNo * Constant.PAGESIZE > filterOrders.size()) {
            return 0;
        } else {
            for (int num = 0, startIndex = pageNo * Constant.PAGESIZE; num < Constant.PAGESIZE
                    && startIndex < filterOrders.size(); num++, startIndex++) {
                resultList.add(filterOrders.get(startIndex));
            }
            return 1 + (filterOrders.size() - 1) / Constant.PAGESIZE;
        }

    }
    
    // 检查订单对应的图标版本是否有更新
    private boolean hasNewImageVersion(Integer lastImageVersion,Integer cachedImageVersion) {
        return lastImageVersion > cachedImageVersion;
    }

    /**
     * 订单状态对于不同接单用户的不同显示
     *
     * @param orderMap
     * @param currentMerchantId
     */
    private void orderStatusForMerchantShow(Map<String, Object> orderMap,long currentMerchantId) {
        int orderStatus = StringUtil.nullToInteger(orderMap.get("orderStatus"));
        if (orderMap.get("merchantId") == null) {// 为未
            if (orderStatus == 2) {
                Map<String, Object> paramMap = new HashMap<String, Object>();
                paramMap.put("merchantId", currentMerchantId);
                paramMap.put("orderId", orderMap.get("orderId"));
                int num = this.merchantPlanDao.selectPlanNum(paramMap);
                if (num == 0) {
                    orderMap.put("orderStatus", 1);
                    // orderMap.put("orderStatusName", "待提供");
                }
            }
        } else {
            long merchantId = StringUtil.nullToLong(orderMap.get("merchantId"));
            if (merchantId != currentMerchantId) {
                if (orderStatus == 3 || orderStatus == 4 || orderStatus == 5) {
                    orderMap.put("orderStatus", 7);
                    // orderMap.put("orderStatusName", "无效订单");
                }
            }
        }
    }

    /** 商品信息编辑 */
    @Override
    public Object orderGoodsInfoEdit(Long orderId, boolean detailFlg)
            throws Exception {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        // 订单ID
        paramMap.put("orderId", orderId);
        List<Map<String, Object>> orderGoodsInfoList = this.orderInfoDao
                .selectOrderGoodsInfo(paramMap);

        int goodsNumTotal = 0;
        BigDecimal goodsPriceTotal = BigDecimal.ZERO;
        for (Map<String, Object> temp : orderGoodsInfoList) {
            Integer goodsNum = (Integer) temp.get("goodsNum");
            BigDecimal goodsPrice = new BigDecimal(String.valueOf(temp.get("goodsPrice")));

            if (!detailFlg) {
                goodsNumTotal = goodsNumTotal + goodsNum;
            }
            goodsPriceTotal = goodsPriceTotal.add(goodsPrice
                    .multiply(new BigDecimal(goodsNum)));
        }
        if (!detailFlg) {
            Map<String, Object> orderGoodsInfo = new HashMap<String, Object>();
            if (goodsNumTotal != 0) {
                orderGoodsInfo.put("goodsNumTotal", goodsNumTotal);
                orderGoodsInfo.put("goodsPriceTotal", goodsPriceTotal);
                orderGoodsInfo.put("goodsInfoTitle", "已买商品");
            }
            return orderGoodsInfo;
        }
        JSONObject jsonObject = new ResultJSONObject("000", "获取订单商品信息成功");
        jsonObject.put("orderGoodsInfoList", orderGoodsInfoList);
        jsonObject.put("goodsPriceTotal", goodsPriceTotal);
        return jsonObject;
    }

    /**
     * 获得报价方案表单
     */
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getPricePlanForm(String serviceType) throws Exception {
        JSONObject jsonObject = new ResultJSONObject("000", "获得自定义报价表单成功");
        String version = (String) commonCacheService.getObject(
                CacheConstants.ORDER_FORM_VERSION, serviceType);
        List<Map<String, Object>> planForm = (List<Map<String, Object>>) commonCacheService
                .getObject(CacheConstants.PLAN_FORM, serviceType);
        if (planForm==null||planForm.size()<1) {
            // 方案表单版本缓存不存在
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("serviceId", StringUtil.nullToLong(serviceType));
            version = customOrderDao.getOrderFormVersion(param);
            planForm = customOrderDao.getPricePlanForm(param);
            if (planForm != null && planForm.size() > 0) {
                for (Map<String, Object> map : planForm) {
                    BusinessUtil.disposePath(map, "icon");
                    String link = StringUtil.null2Str(map.get("link"));
                    if("NEXT_SCENE".equals(StringUtil.null2Str(map.get("controlType")))&&!StringUtil.isNullStr(link)){
                        // 下页链接控件，根据字典类型自动匹配
                        link = "http://api.oomeng.cn/dictInfo/getFormDictList?dictType="+link;
                        if(Constant.DEVMODE){
                            link = "http://192.168.1.58:8080/customOrder/getOrderForm?serviceId="+map.get("serviceId");
                        }

                    }
                    map.put("link", link);
                    String colItems = StringUtil.null2Str(map
                            .get("colItems"));
                    List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
                    if (StringUtil.isNotEmpty(colItems)) {
                        String[] items = colItems.split(",");
                        if (items != null && items.length > 0) {
                            for (String item : items) {
                                Map<String, Object> colMap = new HashMap<String, Object>();
                                colMap.put("itemName", item);
                                itemList.add(colMap);
                            }
                        }
                    }
                    map.put("colItems", itemList);
                }
                commonCacheService.setObject(version,
                        CacheConstants.ORDER_FORM_VERSION, serviceType);
                commonCacheService.setObject(planForm,
                        CacheConstants.PLAN_FORM, serviceType);
            }

        }
        jsonObject.put("version", version);
        jsonObject.put("planForm", planForm);
        return jsonObject;
    }


    /**
     * 接单用户提供报价方案前验证
     *
     * @param params
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject pricePlanVerify(Map<String, Object> params) throws Exception {
        Long userId = StringUtil.nullToLong(params.get("userId"));
        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
        Long orderId = StringUtil.nullToLong(params.get("orderId"));
        String strUserId = StringUtil.null2Str(userId);
        String strOrderId = StringUtil.null2Str(orderId);

        if(userId==0){//老版本，则禁止抢单
            return new ResultJSONObject("009", "升级为新版本才能抢单哦！");
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("merchantId", merchantId);
        String strMerchantId = StringUtil.null2Str(merchantId);

        // 判断是否是认证商户
        // String authType = myMerchantService.checkIsNotAuth(merchantId);
        Map<String, Object> merchantInfo = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO, strMerchantId);
        if (null == merchantInfo) {
            merchantInfo = myMerchantService.selectMerchantBasicInfo(merchantId);
        }
        String authType = null == merchantInfo ? "0" : String.valueOf(merchantInfo.get("auth")); // 如果不为空的话，还是取之前的状态
        if ("0".equals(authType)) {
            return new ResultJSONObject("not_anth_merchant", "你不是认证商户，无法抢单，请申请成为认证商户");
        }
        paramMap.put("orderId", orderId);

        int orderStatus = 0;
        int merchantOrderStatus = 0;

        // 从缓存中获取用户订单状态
        Map<String, Object> userOrderMap = userRelatedCacheServices.getCachedUserOrder(strUserId, strOrderId);
        if (null != userOrderMap) {
            // 从缓存中获取用户订单状态
            orderStatus = Integer.parseInt(String.valueOf(userOrderMap.get("orderStatus")));
        } else {
            // 如果缓存中没有获取到用户订单状态，直接从数据库中获取。不用回放，因为用户只有一个缓存，且数据量不大。
            userOrderMap = this.customOrderDao.selectOrderStatusMap(paramMap);
            if (null != userOrderMap) {
                orderStatus = StringUtil.nullToInteger(userOrderMap.get("orderStatus"));
            }
        }

    	//转化serviceTime为逾期时间
    	String overdue = this.convertServiceTime(userOrderMap);
        
        // 从缓存中获取商户订单状态
        Map<String, Object> merchantOrderMap = merchantCacheService.getCachedMerchantPushOrder(strMerchantId, strOrderId);
        if (null != merchantOrderMap) {
            merchantOrderStatus = StringUtil.nullToInteger(merchantOrderMap.get("orderStatus"));
        } else {
            merchantOrderMap = this.customOrderDao.selectOrderStatusMap(paramMap);
            if (null != merchantOrderMap) {
                merchantOrderStatus = StringUtil.nullToInteger(userOrderMap.get("orderStatus"));
            } else {
                // 将该商户的所有订单放回缓存
                merchantCacheService.updateMerchantCachedOrder(merchantOrderMap, strMerchantId, strOrderId);
            }
        }

        // 判断是否重复抢单
        if (this.merchantPlanDao.selectPlanNum(paramMap) > 0) {
            return new ResultJSONObject("acquire_order_repetition", "你已成功抢单，请勿重复抢单");
        }

        // 判断订单状态
//		Map<String,Object> selectOrderStatusMap = this.customOrderDao.selectOrderStatusMap(paramMap);
//		if(selectOrderStatusMap!=null){
//			orderStatus=StringUtil.nullToInteger(selectOrderStatusMap.get("orderStatus"));
//			merchantOrderStatus=StringUtil.nullToInteger(selectOrderStatusMap.get("merchantOrderStatus"));
//		}

        //订单是否过期
//		Map<String,Object> orderInfo = this.orderInfoDao.selectOrderGeneral(paramMap);
//		boolean bool = checkServiceTime(orderInfo);
        //放入订单过期时间的系统参数
        boolean bool = checkServiceTime(userOrderMap);
        //查询是否已经有过预期操作
//        String flag=StringUtil.null2Str(commonCacheService.getObject(CacheConstants.ORDER_PAST_TIME, StringUtil.null2Str(orderId)));
        if (bool && (orderStatus==1 || orderStatus==2) ) {//过期
//            String code=BusinessAction.TIMEOUT_ORDER;
//            this.pastTime(6, 9, code, StringUtil.nullToLong(orderId), StringUtil.null2Str(userOrderMap.get("serviceTime")));
            return new ResultJSONObject("order_serviceTime_failure", "该订单已逾期关闭！");
        }

        // 状态判断
        if (orderStatus == 7) {
        	paramMap.put("orderStatus", orderStatus);
            // 更新商户侧状态
            this.orderInfoDao.updateMerchantOrderStatus(paramMap);
            merchantCacheService.delOrderFromMerchantCache(StringUtil.null2Str(strMerchantId), strOrderId);
            return new ResultJSONObject("user_cancel_order", "用户订单已取消，抢单无效");
        }
        if (orderStatus != 1 && orderStatus != 2) {
            return new ResultJSONObject("order_status_changed", "用户已选择其他服务商，抢单无效");
        }
        if(merchantOrderStatus!=1){
            return new ResultJSONObject("order_status_error", "商户订单状态已改变，抢单失败，请重新刷新");
        }
        if(merchantOrderStatus ==2){
            return new ResultJSONObject("acquire_order_repetition", "你已成功抢单，请勿重复抢单");
        }

        //是否开启接单收费功能
        Map<String, Object> configMap=commonService.getConfigurationInfoByKey("is_open_orderPrice");
        int isOpenOrderPrice=configMap==null?1:StringUtil.nullToInteger(configMap.get("config_value"));
        if(isOpenOrderPrice==1){//开启接单收费功能
            // 判断余额
            BigDecimal orderSurplusMoney = merchantPayService.getOrderSurplusMoney(paramMap);
            BigDecimal orderPrice = merchantPayService.getOrderPrice(paramMap);
            if (orderPrice.compareTo(BigDecimal.ZERO)!=0 && (orderSurplusMoney.compareTo(BigDecimal.ZERO) ==0 || orderSurplusMoney.compareTo(orderPrice)==-1)) {
                return new ResultJSONObject("acquire_order_failure", "你的抢单余额不足，请及时充值");
            }
        }
        // 判断是否超过抢单最大数，查询订单方案数量
        int grabOrderNum = merchantPlanDao.getOrderPlanCount(orderId);
        int maxGrabOrderNum = -1;
        Map<String, Object> maxGrabOrderNumMap = commonService
                .getConfigurationInfoByKey("max_grabOrder_num");
        if (maxGrabOrderNumMap != null) {
            Object maxGrabOrderNumObj = maxGrabOrderNumMap.get("config_value");
            maxGrabOrderNum =maxGrabOrderNumObj == null ? 30 : StringUtil.nullToInteger(maxGrabOrderNumObj);
        }
        if (grabOrderNum >= maxGrabOrderNum) {
            return new ResultJSONObject("order_status_failure", "该订单报价的服务商已达上限，不能再进行抢单！");
        }
        
        //判断商品数量是否超过限制 目前是报价方案中只允许3个商品      
        int maxOrderPlanGoodsCount=configMap==null?3:StringUtil.nullToInteger(configMap.get("max_orderPlan_goodsCount"));
        if(maxOrderPlanGoodsCount !=0){//开启接单收费功能
        	String goodsInfos=StringUtil.null2Str(params.get("goodsInfos"));
        	if(goodsInfos.split(",").length > maxOrderPlanGoodsCount){
        		return new ResultJSONObject("error", "最多只能选择"+maxOrderPlanGoodsCount+"个商品！");
        	}
        }

        return new ResultJSONObject("000", "校验通过");
    }

    public JSONObject wrapSupplyPricePlan(Map<String, Object> params) throws Exception {
        JSONObject jsonObject = null;
        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
        String strMerchantId = StringUtil.null2Str(merchantId);
        Long orderId = StringUtil.nullToLong(params.get("orderId"));
        String strOrderId = StringUtil.null2Str(orderId);

        GenericCacheServiceImpl genericCacheService = (GenericCacheServiceImpl) commonCacheService;
        JedisLock planLock = genericCacheService.getLock(CacheConstants.MERCHANT_PLAN + strMerchantId + "_" + strOrderId, 5 * 1000, 19 * 1000); // 超时等待5秒，锁定60秒
        try {
            if (planLock.acquire()) {
                jsonObject = supplyPricePlan(params);
            } else {
                return new ResultJSONObject("acquire_order_repetition", "你已成功抢单，请勿重复抢单!!!");
            }
        } catch (Exception e) {
            throw new Exception("重复抢单出现异常：", e);
        } finally {
            genericCacheService.releaseLock(planLock);
        }
        return jsonObject;
    }

    /**
     * 接单用户提供报价方案
     * modify：1.新增订单预约时间校验，过期需处理订单状态
     *         2.新增订单详情保存，根据自定义表单保存
     */
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public JSONObject supplyPricePlan(Map<String, Object> params) throws Exception {
        Long orderId = StringUtil.nullToLong(params.get("orderId"));
        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
        String phone = StringUtil.null2Str(params.get("phone"));
        String planType = StringUtil.null2Str(params.get("planType"));
        String planPrice = StringUtil.null2Str(params.get("planPrice"));//方案价格（已废弃）
        String discountPrice = StringUtil.null2Str(params.get("discountPrice"));//方案价格
        String priceUnit = StringUtil.null2Str(params.get("priceUnit"));//价格单位
        String deposit = StringUtil.null2Str(params.get("deposit")); // 新增订金
        String promise = StringUtil.null2Str(params.get("promise")); // 新增服务承诺
        String detail = StringUtil.null2Str(params.get("detail"));
        List<String> voicePaths = (List<String>) params.get("voicePaths");
        List<String> picturePaths = (List<String>) params.get("picturePaths");
    	String goodsInfos=StringUtil.null2Str(params.get("goodsInfos"));
        String isOffer = "1";
    	
        //如果方案价格为空，那么方案未报价，discountPrice是方案价格，planPrice已经停用 20161018 CUIJIAJUN
        if(discountPrice.equals("") || discountPrice.equals("0")){
        	discountPrice = "999999999.00";
        	isOffer = "0";
        }

        // 如果优惠为空，则等于方案价
        if (discountPrice.equals("") || discountPrice.equals("0")) {
            discountPrice = planPrice;
        }

        JSONObject jsonObject = null;

        // 处理$
        detail = StringUtil.formatDollarSign(detail);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        paramMap.put("merchantId", merchantId);

        // 查询商户提供的方案数量
        int checkImmediately = this.orderInfoDao.checkImmediatelyOrder(paramMap);
        if (checkImmediately > 1) {// 商户已经提供方案
            return new ResultJSONObject("002", "该商户已经抢过这个订单");
        }
        // 查询订单状态+
        int orderStatus = this.orderInfoDao.selectOrderStatus(paramMap);
        if (orderStatus != 1 && orderStatus != 2) {
            return new ResultJSONObject("order_status_changed", "订单状态已改变，抢单失败，请重新刷新");
        }

        int orderStatus_=2;//需要更新的状态
        // 订单状态为1的场合，将订单状态更新成2
        if (orderStatus == 1) {
            paramMap = new HashMap<String, Object>();
            paramMap.put("orderId", orderId);
            paramMap.put("merchantId", merchantId);
            paramMap.put("orderStatus", orderStatus_);
            // 更新订单的状态
            this.orderInfoDao.updateOrderStatus(paramMap);
            this.orderInfoDao.updateMerchantOrderStatus(paramMap);
        }
        if(orderStatus == 2){
            paramMap.put("orderId", orderId);
            paramMap.put("merchantId", merchantId);
            paramMap.put("orderStatus",orderStatus_);
            //更新商户的订单状态
            this.orderInfoDao.updateMerchantOrderStatus(paramMap);
        }
        paramMap = new HashMap<String, Object>();
        paramMap.put("merchantId", merchantId);
        // paramMap.put("appType", appType);
        // 更新商户总抢单次数
        this.merchantPlanDao.updateGrabFrequency(paramMap);

        // 订单ID
        paramMap.put("orderId", orderId);
        // (商户)登陆的手机号码
        paramMap.put("phone", phone);
        // 方案价格
        paramMap.put("planPrice", StringUtil.nullToFloat(planPrice));
        // 优惠价
        paramMap.put("discountPrice", StringUtil.nullToFloat(discountPrice));
        // 方案内容
        paramMap.put("content", filterFourCharString(StringUtil.nullToString(detail))   );
        // 方案类型
        paramMap.put("planType", StringUtil.nullToInteger(planType));
        // 订金
        paramMap.put("deposit", StringUtil.nullToFloat(deposit));
        // 服务承诺
        paramMap.put("promise", promise);
        // 价格单位
        paramMap.put("priceUnit", priceUnit);
        //是否报价
        paramMap.put("isOffer", isOffer);

        // 查询employeeId，此处的employeeId是userId
        Object employeeId = merchantPlanDao.getEmployeeIdByPhone(paramMap);
        paramMap.put("employeeId", employeeId);
        // 保存商户方案
        this.merchantPlanDao.insertMerchantPlan(paramMap);

        Long merchantPlanId = (Long) paramMap.get("merchantPlanId");

        List<Map<String, Object>> merchantPlanAttachmentList = new ArrayList<Map<String, Object>>();

        if (voicePaths.size() > 0) {
            for (String path : voicePaths) {
                Map<String, Object> merchantPlanAttachmentMap = new HashMap<String, Object>();
                merchantPlanAttachmentMap.put("merchantPlanId", merchantPlanId);
                merchantPlanAttachmentMap.put("attachmentType", 2);
                merchantPlanAttachmentMap.put("attachmentUse", 21);
                merchantPlanAttachmentMap.put("path", path);
                merchantPlanAttachmentList.add(merchantPlanAttachmentMap);
            }
        }

        if (picturePaths.size() > 0) {
            for (String path : picturePaths) {
                Map<String, Object> merchantPlanAttachmentMap = new HashMap<String, Object>();
                merchantPlanAttachmentMap.put("merchantPlanId", merchantPlanId);
                merchantPlanAttachmentMap.put("attachmentType", 1);
                merchantPlanAttachmentMap.put("attachmentUse", 11);
                merchantPlanAttachmentMap.put("path", path);
                merchantPlanAttachmentList.add(merchantPlanAttachmentMap);
            }
        }

        if (!merchantPlanAttachmentList.isEmpty()) {
            this.merchantPlanAttachmentDao.insertMerchantPlanAttachment(merchantPlanAttachmentList);
        }
        
        //保存报价方案-商品信息
        String[] goodsInfoArray=goodsInfos.split(",");
        if(!goodsInfos.equals("") && goodsInfoArray.length > 0){
        	List<Map<String,Object>> goodsHistoryList=new ArrayList<Map<String,Object>>();
        	for(String goodsInfo : goodsInfoArray){
        		Long goodsId=StringUtil.nullToLong(goodsInfo.split("_")[0]);
        		int version=StringUtil.nullToInteger(goodsInfo.split("_")[1]);
        		Long goodsHistoryId=myMerchantService.createGoodsHistory(goodsId, version);
        		Map<String,Object> map=new HashMap<String, Object>();
            	map.put("goodsHistoryId", goodsHistoryId);
            	map.put("planId", merchantPlanId);
        		goodsHistoryList.add(map);
        	}        	
        	customOrderDao.savePlanAndGoodsInfo(goodsHistoryList);
        }

        paramMap = new HashMap<String, Object>();
        paramMap.put("merchantId", merchantId);
        // paramMap.put("appType", appType);
        paramMap.put("orderId", orderId);


		/* 接单收费功能*/
        //是否开启接单收费功能
        Map<String, Object> configMap=commonService.getConfigurationInfoByKey("is_open_orderPrice");
        int isOpenOrderPrice=configMap==null?1:StringUtil.nullToInteger(configMap.get("config_value"));
        if(isOpenOrderPrice==1){//开启接单收费功能
            System.out.println("抢单功能开启");
            // 订单金额
            BigDecimal orderPrice = merchantPayService.getOrderPrice(paramMap);
            if (orderPrice.compareTo(BigDecimal.ZERO)==1) {// > 0
                // 抢单成功,订单余额中扣除抢单费
                Map<String, Object> payMap = new HashMap<String, Object>();
                // payMap.put("appType", appType);
                payMap.put("merchantId", merchantId);
                payMap.put("money", orderPrice);
                merchantPayService.deductOrderMoney(payMap);

                // 生成一条扣费记录
                payMap = new HashMap<String, Object>();
                // payMap.put("appType", appType);
                payMap.put("orderId", orderId);
                payMap.put("merchantId", merchantId);
                payMap.put("orderId", orderId);
                payMap.put("payType", 2);
                payMap.put("payMoney", "-"+StringUtil.null2Str(orderPrice));
                merchantPayService.addMerchantOrderPaymentDetails(payMap);
            }
        }else{
            System.out.println("抢单收费功能关闭");
        }
		/*END  接单收费功能*/
        params.put("planId", merchantPlanId);

        savePlanDetail(params); // 保存抢单详情

        jsonObject = new ResultJSONObject("000", "抢单成功");

        // 如果用户订单缓存存在，则同步更新用户-订单 缓存的状态。
        Map<String, Object> cachedOrder = userRelatedCacheServices
                .getCachedUserOrder(orderId.toString());
        if (cachedOrder != null) {
            cachedOrder.put("orderStatus", orderStatus_);
            Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
            cachedOrder.put("orderStatusName", dictionary.get(StringUtil.null2Str(orderStatus_)));
            cachedOrder.put("isOffer", isOffer);
            userRelatedCacheServices.cacheUserOrderWithJson(
                    cachedOrder.get("userId").toString(), cachedOrder);
        }

        // 则同步更新商户-订单 缓存的状态。
        Map<String , Object> upOrderInfo = new HashMap<String,Object>();
        upOrderInfo.put("orderStatus", StringUtil.null2Str(orderStatus_));
        upOrderInfo.put("isOffer", isOffer);
        merchantCacheService.updateMerchantOrderCache(StringUtil.null2Str(merchantId), StringUtil.null2Str(orderId), upOrderInfo);
        
        // 开启异步处理，更新方案的距离、评论数、好评率
        Map<String, Object> orderLocationMap = this.pushDao
                .selectOrderLocation(orderId);
        Map<String, Object> merchantLocationMap = this.myMerchantService
                .getLocationInfo(merchantId);
        UpdateMerchantPlan updateMerchantPlan = new UpdateMerchantPlan(
                merchantId, orderId, orderLocationMap, merchantLocationMap,
                merchantPlanDao);
        MerchantThreadServices.executor(updateMerchantPlan);

        //保存用户行为  -Revoke 2016.5.13
        Map<String,Object> actionMap= new HashMap<String,Object>();
        actionMap.put("actionCode",BusinessAction.BID_ORDER);
        actionMap.put("orderId",orderId);
        actionMap.put("merchantId",merchantId);
        timeLineDao.insertTimeLine(actionMap);

        //抢单推送userId,orderId,merchantId,pushType,data
        //查询是否是私人助理
        int isPrivateAssistant=0;
        Map<String, Object> info = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
        if(info!=null){
        	Object obj=info.get("isPrivateAssistant");
        	if(obj!=null){
        		isPrivateAssistant=StringUtil.nullToInteger(obj);
        	}else{
        		isPrivateAssistant = StringUtil.nullToInteger(customOrderDao.getIsPrivateAssistantByMerchantId(paramMap));
        	}
        }else{
        	isPrivateAssistant = StringUtil.nullToInteger(customOrderDao.getIsPrivateAssistantByMerchantId(paramMap));
        }
        Map<String,Object> pushMap=new HashMap<String, Object>();
        pushMap.put("orderId", orderId);
        pushMap.put("merchantId", merchantId);
        pushMap.put("isPrivateAssistant", isPrivateAssistant);
        pushMap.put("userId", params.get("userId"));
        pushMap.put("data", "");
        pushMap.put("pushType",2);
        pushService.basicPush(pushMap);

        return jsonObject;
    }

    @Override
    public JSONObject getSupplyPricePlanCheckInfo(Map<String, Object> params) throws Exception {

    	if(params.containsKey(("orderId"))){//V1110新版接口，验证

            Map<String, Object> orderParamMap = new HashMap<String, Object>();
            orderParamMap.put("orderId", params.get("orderId"));
            orderParamMap.put("merchantId", params.get("merchantId"));

            // 查询订单基础信息
            Map<String, Object> orderInfo = this.orderInfoDao.selectOrderGeneral(orderParamMap);
            if(orderInfo==null){
                return new ResultJSONObject("001", "订单详情加载失败");
            }
            int orderStatus=StringUtil.nullToInteger(orderInfo.get("orderStatus"));
            if(orderStatus==7){//已关闭
                return new ResultJSONObject("002", "客户已关闭订单");
            }
    	}
        JSONObject jsonObject = new ResultJSONObject("000", "获取抢单验证信息成功");

        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
        //是否开启接单收费功能
        Map<String, Object> configMap=commonService.getConfigurationInfoByKey("is_open_orderPrice");
        int isOpenOrderPrice=configMap==null?1:StringUtil.nullToInteger(configMap.get("config_value"));
        if(isOpenOrderPrice==1){//开启收费功能
            // 如果订单状态不是待提供，则不需要查余额

            // 订单余额
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("merchantId", merchantId);
            // map.put("appType", appType);
            BigDecimal orderSurplusFee = merchantPayService.getOrderSurplusMoney(map);
            BigDecimal orderFee= merchantPayService.getOrderPrice(map);
            jsonObject.put("orderSurplusFee", orderSurplusFee + "");// 订单余额
            jsonObject.put("orderFee", orderFee + "");// 抢单费用

        }
        //是否开启抢单收费
        jsonObject.put("isOpenOrderPrice", isOpenOrderPrice);

        // 判断是否是认证商户
        jsonObject.put("auth", myMerchantService.checkIsNotAuth(merchantId));

        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("merchantId", merchantId);
        paramMap.put("appType", params.get("appType"));

        int payApplyStatus = this.myMerchantService.selectMerchantPayApplyStatus(paramMap);
        //查询商户充值状态 -1-可充值，1-确认中，不可充值
        jsonObject.put("payApplyStatus", payApplyStatus);

        return jsonObject;
    }

    /**
     * 发单用户获取报价方案列表
     */
    @Override
    public JSONObject getPricePlanList(Map<String, Object> params) throws Exception {
        Long orderId = StringUtil.nullToLong(params.get("orderId"));
        int pageNo = StringUtil.nullToInteger(params.get("pageNo"));
        String orderBy = StringUtil.null2Str(params.get("orderBy"));
        String direction = StringUtil.null2Str(params.get("direction"));
        Map<String, Object> paramMap = new HashMap<String, Object>();
        int totalPage = 0;
        JSONObject jsonObject = null;
        paramMap.put("orderId", orderId);
        paramMap.put("isOther", params.get("isOther"));
        paramMap.put("startNum", pageNo * Constant.PAGESIZE);
        
        if (orderBy==null || orderBy.equals("") || orderBy.equals("-1") || orderBy.equals("4")){
        		//默认排序，需要从数据库中取出所有对应的报价方案然后排序   ---Revoke Yu 2016.8.20
        		paramMap.put("pageSize", Short.MAX_VALUE);
        		paramMap.put("startNum", 0);
        }else{
        		paramMap.put("pageSize", Constant.PAGESIZE);
        }

        switch (orderBy) {
            case "1": // 1的场合按优惠价格升序排列
                paramMap.put("orderby", "mp.discount_price");
                break;
            case "2": // 2的场合按距离升序排列
                paramMap.put("orderby", "mp.distance");
                break;
            case "3": // 3按加入时间排序
            	 paramMap.put("orderby", "mp.join_time");
                 break;
            default: // 默认按增值服务及私人助理等优先级排序
            	 paramMap.put("orderby", "mp.join_time");
                break;
        }
        if (direction!=null && direction!=""){
            paramMap.put("sort", direction);
        }
        else{
            paramMap.put("sort", "asc");
        }

        List<Map<String, Object>> merchantPlans = this.userOrderDao.getOrderMerchantPlan(paramMap);
        if (orderBy==null || orderBy.equals("") || orderBy.equals("-1") || orderBy.equals("4")){
    		//私人助理需要进行内存排序并取出要范围的页。 2016-08-20
    		merchantPlans=constructMerchantPlanFromMemory(merchantPlans,pageNo);
        }
        
        for (int i = 0; i < merchantPlans.size(); i++) {
        	//补充vipStatus
        	merchantPlans.get(i).put("vipStatus", -1);
			List<RuleConfig> rulConfigs=incService.getRuleConfig((Long) merchantPlans.get(i).get("merchantId"));
			if (rulConfigs!=null){
				if (rulConfigs.get(0).isVipMerchantOrder()){
					merchantPlans.get(i).put("vipStatus", 2);
				}
			}
        	
            BusinessUtil.disposeManyPath(merchantPlans.get(i), "icoPath",
                    "picturePath", "voicePath");
            if (merchantPlans.get(i).get("merchantId") != null) {
                paramMap.put("merchantId",
                        (Long) merchantPlans.get(i).get("merchantId"));
                int count = orderInfoDao.selectEvaluationOrderNum(paramMap);
                int re = 5;
                BigDecimal score = new BigDecimal(5);
                if (count != 0) {
                    Integer totalAttitudeEvaluation = Integer
                            .parseInt(merchantPlans.get(i).get(
                                    "totalAttitudeEvaluation") == null ? "0"
                                    : merchantPlans.get(i).get(
                                    "totalAttitudeEvaluation")
                                    + "");
                    Integer totalQualityEvaluation = Integer
                            .parseInt(merchantPlans.get(i).get(
                                    "totalQualityEvaluation") == null ? "0"
                                    : merchantPlans.get(i).get(
                                    "totalQualityEvaluation")
                                    + "");
                    Integer totalSpeedEvaluation = Integer
                            .parseInt(merchantPlans.get(i).get(
                                    "totalSpeedEvaluation") == null ? "0"
                                    : merchantPlans.get(i).get(
                                    "totalSpeedEvaluation")
                                    + "");
                    // 总服务态度评价+总服务质量评价+总服务速度评价
                    Integer totalEvaluation = totalAttitudeEvaluation
                            + totalQualityEvaluation + totalSpeedEvaluation;
                    // 星级
                    BigDecimal starLevel = new BigDecimal(totalEvaluation)
                            .divide(new BigDecimal(count)
                                            .multiply(new BigDecimal(3)), 0,
                                    BigDecimal.ROUND_HALF_UP);
                    re = starLevel.intValue();
                    // 分数
                    score = new BigDecimal(totalEvaluation).divide(new BigDecimal(count).multiply(new BigDecimal(3)), 1, BigDecimal.ROUND_DOWN);
                }
                if (re > 5) {
                    re = 5;
                }
                if (re < 0) {
                    re = 0;
                }
                merchantPlans.get(i).put("merchantPoint", re);
                merchantPlans.get(i).put("score", score);
            }

            // 判断企业认证类型 1-企业认证 2-个人认证 0-没有认证
            judgeAuth(merchantPlans, i);

            if(StringUtil.null2Str(merchantPlans.get(i).get("appType")).equals("gxfw")){
                merchantPlans.get(i).put("merchantType", 2);
            }else{
                merchantPlans.get(i).put("merchantType", 1);
            }

            // 对用户和商户之间的距离进行编辑
            String distanceStr = LocationUtil.showDistance(StringUtil.nullToInteger(merchantPlans.get(i).get("distance")).doubleValue());
            merchantPlans.get(i).put("distance", distanceStr);
            //响应时间距离现在多久
//            Date joinTime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, StringUtil.null2Str(merchantPlans.get(i).get("joinTime")));
//            DateUtil.
//            merchantPlans.get(i).put("", value);
        }

        totalPage = this.userOrderDao.getOrderMerchantPlanTotalPage(paramMap);
        totalPage = BusinessUtil.totalPageCalc(totalPage);

        jsonObject = new ResultJSONObject("000", "获取订单供应商列表成功");
        jsonObject.put("totalPage", totalPage);
        
        //获取服务方案中的商品数量及默认图片信息
        getGoodsForMerchantPlans(merchantPlans);
        jsonObject.put("merchantPlans", merchantPlans);
        return jsonObject;
    }

  


	


	/**
     * 发单用户选择报价方案
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject choosePricePlan(String appType,String shopName,Long orderId,Long merchantId,Long merchantPlanId)
            throws Exception {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        int result = 0;
        int check = 0;
        paramMap.put("orderId", orderId);
        paramMap.put("merchantId", merchantId);
        paramMap.put("merchantPlanId", merchantPlanId);
        paramMap.put("appType", appType);
        int orderStatus=0;
        // 如果缓存存在，则根据缓存进行判断
        Map<String, Object> cachedOrderInfo = userRelatedCacheServices
                .getCachedUserOrder(orderId.toString());
        if (cachedOrderInfo != null) {
            if (!cachedOrderInfo.containsKey("merchantId")
                    && !cachedOrderInfo.containsKey("merchantPlanId")
                    && cachedOrderInfo.get("merchantId") == null
                    && cachedOrderInfo.get("merchantPlanId") == null) {
                check = 1;
            }
            orderStatus=StringUtil.nullToInteger(cachedOrderInfo.get("orderStatus"));
        } else {
            // 查询此订单是否被已选报价方案
            check = this.userOrderDao.checkChooseMerchantPlan(paramMap);
        }
      

        //判断订单状态
        if(orderStatus==0){
            orderStatus=userOrderDao.selectOrderStatus(paramMap);
        }
        if(orderStatus!=2){
            return new ResultJSONObject("001", "订单状态不符合选择报价方案条件！");
        }

        if (check > 0) {
            Long receiveEmployeesId = userOrderDao
                    .getMerchantPlanReceiveEmployeesId(paramMap);
            paramMap.put("receiveEmployeesId", receiveEmployeesId);

            Long userId = this.userOrderDao.getOrderUserId(paramMap);
            paramMap.put("userId", userId);

            //订单状态
            paramMap.put("orderStatus", 3);

            //查询报价方案价格
            Map<String,Object> orderPricePlan=customOrderDao.getOrderPlanBrief(paramMap);
            int isOffer = StringUtil.nullToInteger(orderPricePlan.get("isOffer"));
            String discountPrice = "0.00";
            if(isOffer != 0){//未报价
            	discountPrice = orderPricePlan.get("discountPrice").toString();
            }
            
            paramMap.put("orderPrice", discountPrice);//报价方案价格
            paramMap.put("priceUnit", orderPricePlan.get("priceUnit"));//报价方案价格单位

            //修改用户订单信息，包含订单状态
            result += this.userOrderDao.chooseMerchantPlan(paramMap);
            //修改商户订单状态
            result +=orderInfoDao.updateMerchantOrderStatus(paramMap);

            //查询所有报价方案，去除已选方案，merchantId是分区键，直接用 != 导致死锁 2016-10-31 CUIJIAJUN
  			Map<String,Object> merchantParamMap = new HashMap<String, Object>();
  			merchantParamMap.put("orderId", orderId);
  	        List<Map<String,Object>> pushInfos = this.userOrderDao.getOrderPushIds(merchantParamMap);
  	        for(Map<String,Object> pushInfo:pushInfos){
  	        	if(pushInfo.get("merchantId").toString().equals(merchantId.toString())){
  	        		pushInfos.remove(pushInfo);
  	        		break;
  	        	}
  	        }
  	        paramMap.put("pushInfos", pushInfos);
  	        
            paramMap.put("otherOrderStatus", 8);//用户没选择此商户
            result +=orderInfoDao.updateOtherMerchantOrderStatus(paramMap);
            if (this.userOrderDao.checkMerchantUsers(paramMap) > 0) {
                result += this.userOrderDao.updateMerchantUsers(paramMap);
            } else {
                result += this.userOrderDao.insertMerchantUsers(paramMap);
            }

            // 如果缓存存在，同步更新缓存中的状态
            if (cachedOrderInfo != null) {
                cachedOrderInfo.put("shopName", shopName);
                cachedOrderInfo.put("merchantId", merchantId);
                cachedOrderInfo.put("merchantPlanId", merchantPlanId);
                cachedOrderInfo.put("orderStatus", 3);
                cachedOrderInfo.put("orderPrice",orderPricePlan.get("discountPrice"));
                cachedOrderInfo.put("priceUnit", orderPricePlan.get("priceUnit"));//报价方案价格单位

                Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
                cachedOrderInfo.put("orderStatusName", dictionary.get(cachedOrderInfo.get("orderStatus")+""));

                //补充商户概要信息到缓存中
                Map<String,Object> merchantInfo = merchantPlanDao.getMerchantInfoForChoosePlan(merchantId);
                cachedOrderInfo.put("merchantName", merchantInfo.get("name"));
                cachedOrderInfo.put("merchantPhone", merchantInfo.get("telephone"));
                cachedOrderInfo.put("merchantIcon", BusinessUtil.disposeImagePath(merchantInfo.get("path").toString()));

                cachedOrderInfo.put("confirmTime", System.currentTimeMillis());
                userRelatedCacheServices.cacheUserOrderWithJson((String) cachedOrderInfo.get("userId"),cachedOrderInfo);
            }

            //保存用户行为  -Revoke 2016.5.13
            Map<String,Object> actionMap= new HashMap<String,Object>();
            actionMap.put("actionCode",BusinessAction.CHOOSE_ORDER);
            actionMap.put("orderId",orderId);
            actionMap.put("merchantId",merchantId);
            timeLineDao.insertTimeLine(actionMap);

            //推送给选中的服务商
            Map<String,Object> pushMap=new HashMap<String, Object>();
            pushMap.put("data", "");
            pushMap.put("pushType",3);
            pushMap.put("orderId", orderId);
            pushMap.put("merchantId", merchantId);
            pushMap.put("userId",receiveEmployeesId);
            pushService.basicPush(pushMap);

            //推送给未选中的服务商
            Map<String,Object>  empoyeeQuery=new HashMap<String,Object>();
            empoyeeQuery.put("orderId", orderId);
            empoyeeQuery.put("excludeEmployeeID", receiveEmployeesId);

            //多个用户ID，以逗号隔开
            List<Map<String,Object>> otherMerchants=merchantPlanDao.getEmployeeIdsOfMerchantPlan(empoyeeQuery);
            for(Map<String,Object> map : otherMerchants){
                pushMap=new HashMap<String, Object>();
                pushMap.put("data", "");
                pushMap.put("orderId", orderId);
                pushMap.put("merchantId", map.get("merchantId"));
                pushMap.put("pushType",14);
                pushMap.put("userId",map.get("receiveEmployeesId"));
                pushService.basicPush(pushMap);
            }
            //更新商户侧缓存              Revoke 2016.6.6
            merchantCacheService.updateMerchantOrderCache(merchantId.toString(), orderId.toString(), "3");

            List<Map<String,Object>> merchantIds=this.customOrderDao.getMerchantsForSpeicalOrder(paramMap);
            removeSpecialMerchantId(merchantIds,merchantId.toString());
            if (merchantIds!=null && merchantIds.size()>0){
                merchantCacheService.batchUpdateMerchantOrderStatus(orderId.toString(), merchantIds, "8");
            }

            //end of 更新商户侧缓存

        } else {
            return new ResultJSONObject("000", "你已选择此报价方案");
        }
        JSONObject jsonObject = null;
        if (result > 0) {
            jsonObject = new ResultJSONObject("000", "已成功选定服务商");
        } else {
            jsonObject = new ResultJSONObject("008", "选择商户失败");
        }
        return jsonObject;

    }
    

	/**
     * 发单用户选择报价方案
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject choosePricePlanV1110(String appType,String shopName,Long orderId,Long merchantId,Long merchantPlanId,Boolean isPush)
            throws Exception {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        int result = 0;
        int check = 0;
        paramMap.put("orderId", orderId);
        paramMap.put("merchantId", merchantId);
        paramMap.put("merchantPlanId", merchantPlanId);
        paramMap.put("appType", appType);
        int orderStatus=0;
        // 如果缓存存在，则根据缓存进行判断
        Map<String, Object> cachedOrderInfo = userRelatedCacheServices
                .getCachedUserOrder(orderId.toString());
        if (cachedOrderInfo != null) {
            if (!cachedOrderInfo.containsKey("merchantId")
                    && !cachedOrderInfo.containsKey("merchantPlanId")
                    && cachedOrderInfo.get("merchantId") == null
                    && cachedOrderInfo.get("merchantPlanId") == null) {
                check = 1;
            }
            orderStatus=StringUtil.nullToInteger(cachedOrderInfo.get("orderStatus"));
        } else {
            // 查询此订单是否被已选报价方案
            check = this.userOrderDao.checkChooseMerchantPlan(paramMap);
        }
        
        //20161029 是否被选中过
        if(3==orderStatus||check<=0){
        	return new ResultJSONObject("000", "你已选择此报价方案");
        }

        //判断订单状态
        if(orderStatus==0){
            orderStatus=userOrderDao.selectOrderStatus(paramMap);
        }
        if(orderStatus!=2){
            return new ResultJSONObject("001", "订单状态不符合选择报价方案条件！");
        }

        if (check > 0) {
            Long receiveEmployeesId = userOrderDao
                    .getMerchantPlanReceiveEmployeesId(paramMap);
            paramMap.put("receiveEmployeesId", receiveEmployeesId);

            Long userId = this.userOrderDao.getOrderUserId(paramMap);
            paramMap.put("userId", userId);

            //订单状态
            paramMap.put("orderStatus", 3);

            //查询报价方案价格
            Map<String,Object> orderPricePlan=customOrderDao.getOrderPlanBrief(paramMap);
            int isOffer = StringUtil.nullToInteger(orderPricePlan.get("isOffer"));
            String discountPrice = "0.00";
            if(isOffer != 0){//未报价
            	discountPrice = orderPricePlan.get("discountPrice").toString();
            }
            
            paramMap.put("orderPrice", discountPrice);//报价方案价格
            paramMap.put("priceUnit", orderPricePlan.get("priceUnit"));//报价方案价格单位

            //修改用户订单信息，包含订单状态
            result += this.userOrderDao.chooseMerchantPlan(paramMap);
            //修改商户订单状态
            result +=orderInfoDao.updateMerchantOrderStatus(paramMap);
            
            
            //查询所有报价方案，去除已选方案，merchantId是分区键，直接用 != 导致死锁 2016-10-31 CUIJIAJUN
  			Map<String,Object> merchantParamMap = new HashMap<String, Object>();
  			merchantParamMap.put("orderId", orderId);
  	        List<Map<String,Object>> pushInfos = this.userOrderDao.getOrderPushIds(merchantParamMap);
  	        for(Map<String,Object> pushInfo:pushInfos){
  	        	if(pushInfo.get("merchantId").toString().equals(merchantId.toString())){
  	        		pushInfos.remove(pushInfo);
  	        		break;
  	        	}
  	        }
  	        paramMap.put("pushInfos", pushInfos);
            paramMap.put("otherOrderStatus", 8);//用户没选择此商户
            
            result +=orderInfoDao.updateOtherMerchantOrderStatus(paramMap);
            if (this.userOrderDao.checkMerchantUsers(paramMap) > 0) {
                result += this.userOrderDao.updateMerchantUsers(paramMap);
            } else {
                result += this.userOrderDao.insertMerchantUsers(paramMap);
            }

            // 如果缓存存在，同步更新缓存中的状态
            if (cachedOrderInfo != null) {
                cachedOrderInfo.put("shopName", shopName);
                cachedOrderInfo.put("merchantId", merchantId);
                cachedOrderInfo.put("merchantPlanId", merchantPlanId);
                cachedOrderInfo.put("orderStatus", 3);
                cachedOrderInfo.put("orderPrice",orderPricePlan.get("discountPrice"));
                cachedOrderInfo.put("priceUnit", orderPricePlan.get("priceUnit"));//报价方案价格单位

                Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMapV1110");
                cachedOrderInfo.put("orderStatusName", dictionary.get(cachedOrderInfo.get("orderStatus")+""));

                //补充商户概要信息到缓存中
                Map<String,Object> merchantInfo = merchantPlanDao.getMerchantInfoForChoosePlan(merchantId);
                cachedOrderInfo.put("merchantName", merchantInfo.get("name"));
                cachedOrderInfo.put("merchantPhone", merchantInfo.get("telephone"));
                cachedOrderInfo.put("merchantIcon", BusinessUtil.disposeImagePath(merchantInfo.get("path").toString()));

                cachedOrderInfo.put("confirmTime", System.currentTimeMillis());
                userRelatedCacheServices.cacheUserOrderWithJson((String) cachedOrderInfo.get("userId"),cachedOrderInfo);
            }

            //保存用户行为  -Revoke 2016.5.13
            Map<String,Object> actionMap= new HashMap<String,Object>();
            actionMap.put("actionCode",BusinessAction.CHOOSE_ORDER);
            actionMap.put("orderId",orderId);
            actionMap.put("merchantId",merchantId);
            timeLineDao.insertTimeLine(actionMap);

            //20161101确认完成不需要推送
            if(isPush) {
                //推送给选中的服务商
                Map<String, Object> pushMap = new HashMap<String, Object>();
                pushMap.put("data", "");
                pushMap.put("pushType", 3);
                pushMap.put("orderId", orderId);
                pushMap.put("merchantId", merchantId);
                pushMap.put("userId", receiveEmployeesId);
                pushService.basicPush(pushMap);

                //推送给未选中的服务商
                Map<String, Object> empoyeeQuery = new HashMap<String, Object>();
                empoyeeQuery.put("orderId", orderId);
                empoyeeQuery.put("excludeEmployeeID", receiveEmployeesId);

                //多个用户ID，以逗号隔开
                List<Map<String, Object>> otherMerchants = merchantPlanDao.getEmployeeIdsOfMerchantPlan(empoyeeQuery);
                for (Map<String, Object> map : otherMerchants) {
                    pushMap = new HashMap<String, Object>();
                    pushMap.put("data", "");
                    pushMap.put("orderId", orderId);
                    pushMap.put("merchantId", map.get("merchantId"));
                    pushMap.put("pushType", 14);
                    pushMap.put("userId", map.get("receiveEmployeesId"));
                    pushService.basicPush(pushMap);
                }
            }
            //更新商户侧缓存              Revoke 2016.6.6
            merchantCacheService.updateMerchantOrderCache(merchantId.toString(), orderId.toString(), "3");

            List<Map<String,Object>> merchantIds=this.customOrderDao.getMerchantsForSpeicalOrder(paramMap);
            removeSpecialMerchantId(merchantIds,merchantId.toString());
            if (merchantIds!=null && merchantIds.size()>0){
                merchantCacheService.batchUpdateMerchantOrderStatus(orderId.toString(), merchantIds, "8");
            }

            //end of 更新商户侧缓存

        } else {
            return new ResultJSONObject("error", "你已选择此报价方案");
        }
        JSONObject jsonObject = null;
        if (result > 0) {
            jsonObject = new ResultJSONObject("000", "已成功选定服务商");
        } else {
            jsonObject = new ResultJSONObject("008", "选择商户失败");
        }
        return jsonObject;

    }

    /**
     * 查询报价方案详情
     */
    @Override
    public JSONObject getPricePlanDetail(Map<String, Object> params) throws Exception {
        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
        Long orderId = StringUtil.nullToLong(params.get("orderId"));
        Map<String, Object> paramMap = new HashMap<String, Object>();
        // 商户ID
        paramMap.put("merchantId", merchantId);
        // 订单ID
        paramMap.put("orderId", orderId);
        Map<String, Object> orderPlanInfoMap = this.orderInfoDao
                .selectOrderPlanInfo(paramMap);
        if(orderPlanInfoMap==null){
            return new ResultJSONObject("001", "报价方案为空");
        }
        BusinessUtil.disposeManyPath(orderPlanInfoMap, "icoPath",
                "planVoicePath", "planPicturePath");
        int count = orderInfoDao.selectEvaluationOrderNum(paramMap);
        int re = 5;
        BigDecimal score = new BigDecimal(5);
        if (count != 0) {
            Integer totalAttitudeEvaluation = StringUtil.nullToInteger(orderPlanInfoMap.get("totalAttitudeEvaluation"));
            Integer totalQualityEvaluation = StringUtil.nullToInteger(orderPlanInfoMap.get("totalQualityEvaluation"));
            Integer totalSpeedEvaluation = StringUtil.nullToInteger(orderPlanInfoMap.get("totalSpeedEvaluation"));
            // 总服务态度评价+总服务质量评价+总服务速度评价
            Integer totalEvaluation = totalAttitudeEvaluation + totalQualityEvaluation + totalSpeedEvaluation;
            // 星级
            BigDecimal starLevel = new BigDecimal(totalEvaluation).divide(new BigDecimal(count).multiply(new BigDecimal(3)), 0,BigDecimal.ROUND_HALF_UP);
            re = starLevel.intValue();
            // 分数
            score = new BigDecimal(totalEvaluation).divide(new BigDecimal(count).multiply(new BigDecimal(3)), 1, BigDecimal.ROUND_DOWN);
        }
        if (re > 5) {
            re = 5;
        }
        if (re < 0) {
            re = 0;
        }
        orderPlanInfoMap.put("merchantPoint", re);
        orderPlanInfoMap.put("score", score);


        /** 判断企业认证类型 1-企业认证 2-个人认证 0-没有认证 */
        if ((Long) orderPlanInfoMap.get("enterpriseAuth") > 0) {
            orderPlanInfoMap.put("auth", 1);
        } else if ((Long) orderPlanInfoMap.get("personalAuth") > 0) {
            orderPlanInfoMap.put("auth", 2);
        } else {
            orderPlanInfoMap.put("auth", 0);
        }

        if(StringUtil.null2Str(orderPlanInfoMap.get("appType")).equals("gxfw")){
            orderPlanInfoMap.put("merchantType", 2);
        }else{
            orderPlanInfoMap.put("merchantType", 1);
        }

        // 对用户和商户之间的距离进行编辑
        String distanceStr = "";
        if (orderPlanInfoMap.get("distance") != null) {
            int distance = (Integer) orderPlanInfoMap.get("distance");
            if (distance >= 1000) {
                double kmDistance = ((double) distance) / 1000;
                BigDecimal bd = new BigDecimal(kmDistance);
                kmDistance = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                //distanceStr = "距离商户" + kmDistance + "公里";
                distanceStr =  kmDistance + "公里";
            } else {
                //distanceStr = "距离商户" + Math.round(distance) + "米";
                distanceStr =  Math.round(distance) + "米";
            }
            orderPlanInfoMap.put("distance", distanceStr);
        } else {
            orderPlanInfoMap.put("distance", "");
        }

        //查询报价方案商品信息
        Long planId=StringUtil.nullToLong(orderPlanInfoMap.get("planId"));
        params.put("planId", planId);
        List<Map<String,Object>> orderPlanGoodsList=customOrderDao.getOrderPlanGoodsList(params);
        //地址加前缀
        BusinessUtil.disposeManyPath(orderPlanGoodsList, "goodsPictureUrl");
        
        //修订vipStatus  2016.8.29 Revoke
        orderPlanInfoMap.put("vipStatus", -1);
      	List<RuleConfig> rulConfigs=incService.getRuleConfig(merchantId);
      	if (rulConfigs!=null){
      				if (rulConfigs.get(0).isVipMerchantOrder()){
      					orderPlanInfoMap.put("vipStatus", 2);
      				}
      	}
        
        JSONObject jsonObject = new ResultJSONObject("000", "获取订单方案信息成功");
        jsonObject.put("orderPlanInfo", orderPlanInfoMap);
//        jsonObject.put("orderPlanDetail", getPlanDetail(orderPlanInfoMap));
        jsonObject.put("orderPlanGoodsList", orderPlanGoodsList);
        return jsonObject;
    }

    /**
     * 修改报价方案
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject updateOrderPricePlan(Map<String, Object> params) throws Exception {

        Long orderId = StringUtil.nullToLong(params.get("orderId"));
        Long merchantId = StringUtil.nullToLong(params.get("merchantId"));
        String planType = StringUtil.null2Str(params.get("planType"));
        String discountPrice = StringUtil.null2Str(params.get("discountPrice"));
        String planPrice = StringUtil.null2Str(params.get("planPrice"));
        String priceUnit = StringUtil.null2Str(params.get("priceUnit"));
        String deposit = StringUtil.null2Str(params.get("deposit")); // 新增订金
        String promise = StringUtil.null2Str(params.get("promise")); // 新增服务承诺
        String detail = StringUtil.null2Str(params.get("detail"));

        List<String> voicePaths = (List<String>) params.get("voicePaths");
        List<String> picturePaths = (List<String>) params.get("picturePaths");

        // 如果优惠为空，则等于方案价
        if (discountPrice.equals("") || discountPrice.equals("0")) {
            discountPrice = planPrice;
        }

        JSONObject jsonObject =  new ResultJSONObject("000", "修改报价方案成功");;

        // 处理$
        detail = StringUtil.formatDollarSign(detail);

        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("orderId", orderId);// 订单ID
        paramMap.put("merchantId", merchantId);//商户ID

        // 查询订单状态+
        int orderStatus = this.orderInfoDao.selectOrderStatus(paramMap);
        if (orderStatus != 2) {
            return new ResultJSONObject("order_status_changed", "订单状态已改变，禁止修改报价方案！");
        }

        paramMap.put("planPrice", planPrice);// 方案价格
        paramMap.put("discountPrice", discountPrice);// 优惠价
        paramMap.put("content", detail);// 方案内容
        paramMap.put("planType", planType);// 方案内容
        paramMap.put("deposit", StringUtil.nullToFloat(deposit));	// 订金
        paramMap.put("promise", promise);// 服务承诺
        paramMap.put("priceUnit", priceUnit);// 价格单位

        // 修改商户方案主表
        this.merchantPlanDao.updateMerchantPlan(paramMap);

        Long merchantPlanId = (Long) paramMap.get("merchantPlanId");

        //插入修改的附件
        List<Map<String, Object>> merchantPlanAttachmentList = new ArrayList<Map<String, Object>>();
        String paths="";
        if (voicePaths.size() > 0) {
            for (String path : voicePaths) {
                Map<String, Object> merchantPlanAttachmentMap = new HashMap<String, Object>();
                merchantPlanAttachmentMap.put("merchantPlanId", merchantPlanId);
                merchantPlanAttachmentMap.put("attachmentType", 2);
                merchantPlanAttachmentMap.put("attachmentUse", 21);
                merchantPlanAttachmentMap.put("path", path);
                merchantPlanAttachmentList.add(merchantPlanAttachmentMap);
                paths+=path+",";
            }
        }

        if (picturePaths.size() > 0) {
            for (String path : picturePaths) {
                Map<String, Object> merchantPlanAttachmentMap = new HashMap<String, Object>();
                merchantPlanAttachmentMap.put("merchantPlanId", merchantPlanId);
                merchantPlanAttachmentMap.put("attachmentType", 1);
                merchantPlanAttachmentMap.put("attachmentUse", 11);
                merchantPlanAttachmentMap.put("path", path);
                merchantPlanAttachmentList.add(merchantPlanAttachmentMap);
                paths+=path+",";
            }
        }

        if (!merchantPlanAttachmentList.isEmpty()) {
            this.merchantPlanAttachmentDao.insertMerchantPlanAttachment(merchantPlanAttachmentList);
        }

        //删除之前的附件
        if(!paths.equals("")){
            paths=paths.substring(0,paths.length()-1);
        }
        paramMap.put("paths", paths);
        this.merchantPlanAttachmentDao.deleteMerchantPlanAttachment(paramMap);

        //修改报价方案详情
        params.put("planId", merchantPlanId);
        updatePlanDetail(params); // 保存抢单详情

        //抢单推送userId,orderId,merchantId,pushType,data
        Long userId = this.userOrderDao.getOrderUserId(paramMap);
        params.put("userId",userId);
        params.put("data", "");
        params.put("pushType",11);
        pushService.basicPush(params);

        return jsonObject;
    }

    /**
     * 发单用户取消订单
     *
     * @param params
     * @return JSONObject
     * @throws
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject cancelOrderForSender(Map<String, Object> params) throws Exception {
        Long orderId = StringUtil.nullToLong(params.get("orderId"));
        String cancelReason = String.valueOf(params.get("cancelReason"));
        String remark = (String) params.get("remark");

        JSONObject jsonObject = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        String orderStatus = "7";
        paramMap.put("orderId", orderId);
        paramMap.put("orderStatus", orderStatus);
        paramMap.put("cancelReason", cancelReason);
        paramMap.put("remark", remark);

        int status = this.customOrderDao.selectOrderStatus(paramMap);
        String actionCode="";
        if (status == 1) {//未报价
        	actionCode=BusinessAction.CANCEL_ORDER;
        }else if (status == 2) {//已报价未选择
        	actionCode=BusinessAction.CANCEL_ORDER_HAVEPLAN;
        }else if (status > 2) {
            return new ResultJSONObject("order_status_changed", "订单状态已改变，请重新刷新");
        }
        // 更新订单状态
        this.customOrderDao.updateOrderStatus(paramMap);
        // 保存取消理由
        this.customOrderDao.saveCancelReason(paramMap);
        // 更新商户侧状态
        this.orderInfoDao.updateMerchantOrderStatus(paramMap);

        Map<String, Object> cachedOrders = userRelatedCacheServices
                .getCachedUserOrder(orderId.toString());
        if (cachedOrders != null) {
            cachedOrders.put("orderStatus", orderStatus);
            Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
            cachedOrders.put("orderStatusName", dictionary.get(orderStatus+""));

            userRelatedCacheServices.cacheUserOrderWithJson(
                    cachedOrders.get("userId").toString(), cachedOrders);
        }

        //增加更新商户侧订单推送缓存状态--------------Revoke 2016.6.6
        List<Map<String,Object>> merchantIds=this.customOrderDao.getMerchantsForSpeicalOrder(paramMap);
        merchantCacheService.batchUpdateMerchantOrderStatus(orderId.toString(), merchantIds, orderStatus);

        jsonObject = new ResultJSONObject("000", "取消订单成功");

        //保存用户行为  -Revoke 2016.5.13
        Map<String,Object> actionMap= new HashMap<String,Object>();
        actionMap.put("actionCode",actionCode);
        actionMap.put("orderId",orderId);
        timeLineDao.insertTimeLine(actionMap);

        return jsonObject;
    }

    /**
     * 发单用户屏蔽订单
     *
     //	 * @param merchantId
     * @param orderId
     * @return JSONObject
     * @throws
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject deleteOrderForSender(Long orderId) throws Exception {
        JSONObject jsonObject = null;
        int result = 0;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        result = this.userOrderDao.deleteOrder(paramMap);

        userRelatedCacheServices.rmUserOrderCache(orderId.toString());
        if (result > 0) {
            jsonObject = new ResultJSONObject("000", "删除订单成功");
        } else {
            jsonObject = new ResultJSONObject("009", "删除订单失败");
        }
        return jsonObject;
    }

    /**
     * 接单用户屏蔽订单
     *
     * @param merchantId
     * @param orderId
     * @return JSONObject
     * @throws
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject shieldOrderForReceiver(Long merchantId,
                                             Long orderId) throws Exception {

        JSONObject jsonObject = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        paramMap.put("merchantId", merchantId);

        Map<String, Object> merchantOrderInfo=merchantCacheService.getCachedMerchantPushOrder(StringUtil.null2Str(merchantId), StringUtil.null2Str(merchantId));
        if(merchantOrderInfo==null){
            merchantOrderInfo=this.customOrderDao.getMerchantOrderMap(paramMap);
        }
        if(merchantOrderInfo==null){//忽略的订单不是这个商户的订单
            return new ResultJSONObject("order_delete_failure", "订单忽略失败!");
        }

        int orderStatus = StringUtil.nullToInteger(merchantOrderInfo.get("orderStatus"));
        if(orderStatus!=1 && orderStatus!=5 && orderStatus!=6 && orderStatus!=7 &&  orderStatus!=8 && orderStatus!=9){
            return new ResultJSONObject("order_delete_failure", "订单忽略失败!");
        }

        int i=customOrderDao.shieldOrder(paramMap);
        if(i>0){
            jsonObject = new ResultJSONObject("000", "订单忽略成功");
            //删除订单列表缓存
            merchantCacheService.delOrderFromMerchantCache(StringUtil.null2Str(merchantId), StringUtil.null2Str(orderId));
        }else{
            jsonObject = new ResultJSONObject("order_delete_failure", "订单忽略失败");
        }

        return jsonObject;
    }

    /**
     * 发单用户获取订单推送状态
     *
     * @param orderId
     * @return JSONObject
     * @throws
     */
    @Override
    public JSONObject getOrderPushTypeForSender(Long orderId) throws Exception {
        JSONObject jsonObject = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        int result = 0;
        result = this.userOrderDao.getOrderPushType(paramMap);
        // 当已经支付时候
        if (result == 5) {
            // 判断是否评价
            if (this.userOrderDao.checkIsEvaluation(paramMap) == 1) {
                result = 8;
            }
        }
        jsonObject = new ResultJSONObject("000", "获得订单的推送状态成功");
        jsonObject.put("pushType", result);
        return jsonObject;
    }

    /**
     * 获取用户此次订单可以使用的代金券列表
     *
     * @param userId
     * @param serviceType
     * @param merchantId
     * @param pageNo
     * @return JSONObject
     * @throws
     */
    @Override
    public JSONObject getOrderCanUsedVouchers(Long userId, Long serviceType,
                                              Long merchantId, int pageNo) throws Exception {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        paramMap.put("serviceType", serviceType);
        paramMap.put("merchantId", merchantId);
//		paramMap.put("orderPayPrice", orderPayPrice);
        paramMap.put("startNum", pageNo * Constant.PAGESIZE);
        paramMap.put("pageSize", Constant.PAGESIZE);
        // paramMap.put("appType", appType);

        JSONObject jsonObject = null;
        int totalPage = 0;
        List<Map<String, Object>> resultMap = this.userOrderDao
                .getUserAvailablePayVouchersInfo(paramMap);
        for (int i = 0; i < resultMap.size(); i++) {
            BusinessUtil.disposePath(resultMap.get(i), "couponsTypePicPath");
        }
        totalPage = this.userOrderDao
                .getUserAvailablePayVouchersInfoTotalPage(paramMap);
        totalPage = BusinessUtil.totalPageCalc(totalPage);
        jsonObject = new ResultJSONObject("000", "获取用户此次订单可以使用的代金券列表成功");
        jsonObject.put("vouchersInfo", resultMap);
        jsonObject.put("totalPage", totalPage);
        return jsonObject;
    }

    @Override
    public List<Map<String, Object>> getOrderCatalogList() throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("catalogId", "");
        List<Map<String,Object>> catalogList = (List<Map<String, Object>>) commonCacheService.getObject("getOrderCatalogList");
        if(catalogList==null||catalogList.isEmpty()||catalogList.size()<1){
            catalogList = customOrderDao.getCatalogList(param);
            if(catalogList!=null&&catalogList.size()>0){
                BusinessUtil.disposeManyPath(catalogList, "icon");
                BusinessUtil.disposeManyPath(catalogList, "bigIcon");
            }
            commonCacheService.setExpireForObject(catalogList, 2*60, "getOrderCatalogList"); // 2分钟
        }


        return catalogList;
    }

    @Override
    public List<Map<String, Object>> getOrderCatalogAndServiceList(
            String catalogId) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("catalogId", StringUtil.null2Str(catalogId));
        List<Map<String,Object>> catalogOrServiceList = (List<Map<String, Object>>) commonCacheService.getObject("catalogOrServiceList",catalogId);
        if(catalogOrServiceList==null||catalogOrServiceList.isEmpty()||catalogOrServiceList.size()<1){
            List<Map<String,Object>> catalogList = customOrderDao.getCatalogList(param); // 根据一级分类id获取二级分类
            List<Map<String,Object>> serviceList = customOrderDao.getServiceList(param); // 根据一级分类id获取二级服务
            String icon = "";
            catalogOrServiceList = new ArrayList<Map<String,Object>>();
            if(catalogList!=null&&catalogList.size()>0){
                for(Map<String,Object> map:catalogList){
                    map.remove("bigIcon");
                    map.remove("flag");
                    map.remove("url");
                    map.remove("rank");
                    map.remove("isClose");
                    icon = StringUtil.null2Str(map.get("icon"));
                    icon = BusinessUtil.disposeImagePath(icon);
                    map.put("icon", icon);
                    param.put("catalogId", StringUtil.null2Str(map.get("id")));
                    List<Map<String,Object>> childServiceList = customOrderDao.getServiceList(param); // 根据二级分类id获取三级服务
                    BusinessUtil.disposeManyPath(childServiceList, "icon");
                    map.put("serviceList", childServiceList);
                }
                catalogOrServiceList.addAll(catalogList);
            }
            if(serviceList!=null&&serviceList.size()>0){
                BusinessUtil.disposeManyPath(serviceList, "icon");
                catalogOrServiceList.addAll(serviceList);
            }
            if(catalogOrServiceList!=null&&catalogOrServiceList.size()>0){
                commonCacheService.setExpireForObject(catalogOrServiceList, 2*60, "catalogOrServiceList",catalogId); // 2分钟
            }
        }


        return catalogOrServiceList;
    }
    /**
     * 确认订单 金额
     * @param orderId
     * @param merchantId
     * @param price
     * @param vouchersId
     * @param actualPrice
     * @return
     * @throws Exception
     * int
     * @throws
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmOrderPrice(Long orderId, Long merchantId, Double price,Double vouchersPrice, Long vouchersId, Double actualPrice,Object payType) throws Exception {
        int result = 0;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        paramMap.put("merchantId", merchantId);
        paramMap.put("price", price);
        BigDecimal prive_bd=new BigDecimal(StringUtil.null2Str(price));

        int status=0;
        Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
        if(cachedOrders==null){
            status=userOrderDao.selectOrderStatus(paramMap);
        }else{
            status=StringUtil.nullToInteger(cachedOrders.get("orderStatus"));
        }
        //判断订单状态
        if(status!=4){
            return -1;// 重复支付
        }

        if (vouchersId != null && vouchersId > 0) {
            paramMap.put("userVouchersId", vouchersId);
            if(vouchersPrice==null){
                Map<String, Object> voucherInfo = this.vouchersInfoDao.getVouchersInfo(paramMap);
                vouchersPrice =StringUtil.nullToDouble(voucherInfo.get("price"));
            }
            // 判断 输入的金额 当 大于 代金券金额时 执行
            if (price > vouchersPrice) {
                // 判断 输入的金额 减 代金券金额时 是否等于实际金额

                BigDecimal vouchersPrice_bd=new BigDecimal(StringUtil.null2Str(vouchersPrice));
                BigDecimal actualPrice_bd=new BigDecimal(StringUtil.null2Str(actualPrice));
                if (prive_bd.subtract(vouchersPrice_bd).compareTo(actualPrice_bd) != 0 ) {
                    return -1;
                }
            }
        }
        //判断支付金额是否和交付清单最新的金额相等
        Map<String,Object> merchantServiceRecordMap=customOrderDao.getMerchantServiceRecord(paramMap);
        if(merchantServiceRecordMap==null){
            return -1;
        }
        String merchantRecordPrice_=StringUtil.null2Str(merchantServiceRecordMap.get("price"));
        if(merchantRecordPrice_.equals("")){
            //如果没有查询到，则到订单表查询
            Map<String,Object> orderPriceInfo=customOrderDao.getOrderPriceInfo(paramMap);
            merchantRecordPrice_=StringUtil.null2Str(orderPriceInfo.get("merchantActualPrice"));
        }
        if(merchantRecordPrice_.equals("")){
            return -1;
        }
        BigDecimal merchantRecordPrice=new BigDecimal(merchantRecordPrice_);
        if(merchantRecordPrice.compareTo(prive_bd) !=0){//实际要支付的价格和商户交付记录的价格不一致
            return -2;
        }

        if(payType !=null ){
            int payType_=StringUtil.nullToInteger(payType);
            int merchantRecordPayType=StringUtil.nullToInteger(merchantServiceRecordMap.get("payType")==null?"-1":merchantServiceRecordMap.get("payType"));
            if(payType_!=merchantRecordPayType){//实际支付的payType和商户交付记录的payType不一致
                return -3;
            }
        }
        paramMap.put("actualPrice", actualPrice);
        paramMap.put("vouchersPrice", vouchersPrice);
        result += this.userOrderDao.confirmOrderPrice(paramMap);
        // 如果缓存存在，同步更新缓存的状态
        if (cachedOrders != null) {
            //cachedOrders.put("orderPrice", price);
            cachedOrders.put("orderActualPrice", actualPrice);
            //cachedOrders.put("orderStatus", 4);
            cachedOrders.put("vouchersId", vouchersId);
            cachedOrders.put("confirmTime", System.currentTimeMillis());
            userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
        }
        // 删除(商户当前)代金券缓存
        commonCacheService.delObjectContainsKey(CacheConstants.MERCHANT_VOUCHERSINFO + "_" + StringUtil.null2Str(merchantId), true);
        return result;
    }

    /**
     * 确认订单 金额
     * @param orderId
     * @param merchantId
     * @param price
     * @param vouchersId
     * @param actualPrice
     * @return
     * @throws Exception
     * int
     * @throws
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject confirmOrderPriceV160815(Long orderId, Long merchantId, Double price,Double vouchersPrice, Long vouchersId, Double actualPrice,Object payType) throws Exception {
        JSONObject jsonObject = new ResultJSONObject("000", "确认订单金额成功");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        paramMap.put("merchantId", merchantId);
        paramMap.put("price", price);
        BigDecimal prive_bd=new BigDecimal(StringUtil.null2Str(price));

        int status=0;
        Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
        if(cachedOrders==null){
            status=userOrderDao.selectOrderStatus(paramMap);
        }else{
            status=StringUtil.nullToInteger(cachedOrders.get("orderStatus"));
        }
        //判断订单状态
        if(status!=4){
            return new ResultJSONObject("010", "确认订单金额失败");// 重复支付
        }

        if (vouchersId != null && vouchersId > 0) {
            paramMap.put("userVouchersId", vouchersId);
            if(vouchersPrice==null){
                Map<String, Object> voucherInfo = this.vouchersInfoDao.getVouchersInfo(paramMap);
                vouchersPrice =StringUtil.nullToDouble(voucherInfo.get("price"));
            }
            // 判断 输入的金额 当 大于 代金券金额时 执行
            if (price > vouchersPrice) {
                // 判断 输入的金额 减 代金券金额时 是否等于实际金额

                BigDecimal vouchersPrice_bd=new BigDecimal(StringUtil.null2Str(vouchersPrice));
                BigDecimal actualPrice_bd=new BigDecimal(StringUtil.null2Str(actualPrice));
                if (prive_bd.subtract(vouchersPrice_bd).compareTo(actualPrice_bd) != 0 ) {
                    return new ResultJSONObject("010", "确认订单金额失败");
                }
            }
        }
        //判断支付金额是否和交付清单最新的金额相等
        Map<String,Object> merchantServiceRecordMap=customOrderDao.getMerchantServiceRecord(paramMap);
        if(merchantServiceRecordMap==null){
            return new ResultJSONObject("010", "确认订单金额失败");
        }
        String merchantRecordPrice_=StringUtil.null2Str(merchantServiceRecordMap.get("price"));
        if(merchantRecordPrice_.equals("")){
            //如果没有查询到，则到订单表查询
            Map<String,Object> orderPriceInfo=customOrderDao.getOrderPriceInfo(paramMap);
            merchantRecordPrice_=StringUtil.null2Str(orderPriceInfo.get("merchantActualPrice"));
        }
        if(merchantRecordPrice_.equals("")){
            return new ResultJSONObject("010", "确认订单金额失败");
        }
        BigDecimal merchantRecordPrice=new BigDecimal(merchantRecordPrice_);
        if(merchantRecordPrice.compareTo(prive_bd) !=0){//实际要支付的价格和商户交付记录的价格不一致
            return new ResultJSONObject("020", "服务商已修改交付内容，请重新提交");
        }

        if(payType !=null ){
            int payType_=StringUtil.nullToInteger(payType);
            int merchantRecordPayType=StringUtil.nullToInteger(merchantServiceRecordMap.get("payType")==null?"-1":merchantServiceRecordMap.get("payType"));
            if(payType_!=merchantRecordPayType){//实际支付的payType和商户交付记录的payType不一致
                return new ResultJSONObject("030", "服务商已修改交付内容，请重新提交");
            }
        }
        paramMap.put("actualPrice", actualPrice);
        paramMap.put("vouchersPrice", vouchersPrice);
        this.userOrderDao.confirmOrderPrice(paramMap);
        // 如果缓存存在，同步更新缓存的状态
        if (cachedOrders != null) {
            //cachedOrders.put("orderPrice", price);
            cachedOrders.put("orderActualPrice", actualPrice);
            //cachedOrders.put("orderStatus", 4);
            cachedOrders.put("vouchersId", vouchersId);
            cachedOrders.put("confirmTime", System.currentTimeMillis());
            userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
        }
        // 删除(商户当前)代金券缓存
        commonCacheService.delObjectContainsKey(CacheConstants.MERCHANT_VOUCHERSINFO + "_" + StringUtil.null2Str(merchantId), true);
        //查询order_no
        String orderNo=customOrderDao.getOrderNoByOrderId(orderId);
        jsonObject.put("orderNo", orderNo);
        return jsonObject;
    }

    private JSONObject returnJson(JSONObject jsonObject, int flag) {
        jsonObject.put("result", flag);
        return jsonObject;
    }

    /** 完成支付宝订单 Old */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject finishAliPayOrder(Long orderId,String tradeNo,String payDate,String buyerNo) throws Exception {
        System.out.println("支付宝回调成功 Old ------------"+orderId+","+tradeNo+","+payDate);
        JSONObject jsonObject = new JSONObject();
        int result = 0;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
        if (cachedOrders != null && cachedOrders.containsKey("orderPayType") && cachedOrders.get("orderPayType") != null
                && cachedOrders.containsKey("orderStatus") && StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("5")) {
            // return -1; // 重复支付
            return returnJson(jsonObject, -1);
        }

        paramMap.put("orderId", orderId);
        String nowTime="";
        if(StringUtil.isNullStr(payDate)){
        	nowTime= getNowYYYYMMDDHHMMSS();
        }else{
        	nowTime=payDate;
        }
        paramMap.put("nowTime", nowTime);

        //查询订单状态(查询主库)
        int status=userOrderDao.selectOrderStatus(paramMap);
        if(status==5){
            // return -1; // 重复支付
            return returnJson(jsonObject, -1);
        }

        Long userId = this.userOrderDao.getOrderUserId(paramMap);
        paramMap.put("userId", userId);

        Long merchantId = this.userOrderDao.getOrderMerchantId(paramMap);
        paramMap.put("merchantId", merchantId);

        //判断此订单是否已支付
        int count=this.userOrderDao.getOrderPayRecord(paramMap);
        if(count>0){
            // return -1; // 重复支付
            return returnJson(jsonObject, -1);
        }

        Long userVouchersId = this.userOrderDao.getOrderVouchersId(paramMap);
        paramMap.put("userVouchersId", userVouchersId);

        Double orderPrice = this.userOrderDao.getOrderPrice(paramMap);
        paramMap.put("orderPrice", orderPrice);

        Double actualPrice = this.userOrderDao.getOrderActualPrice(paramMap);
        paramMap.put("actualPrice", actualPrice);

        paramMap.put("orderPayType", 1); //业务类型
        int tempOrderStatus = 5;
        paramMap.put("orderStatus", tempOrderStatus);

        paramMap.put("tradeNo", tradeNo);
        
        //20161018
        paramMap.put("buyerNo", buyerNo);
        paramMap.put("orderStatus", tempOrderStatus);
        paramMap.put("paymentStatus", 0);

        result += this.userOrderDao.finishPayOrder(paramMap);
        if(result==0){
            // return 0;
            return returnJson(jsonObject, 0);
        }

        //首单送钱
        this.firstOrderSendMoney(merchantId,orderId);

        result += this.userOrderDao.updateUserStatisticsService(paramMap);
        result += this.userOrderDao.updateMerchantStatisticsService(paramMap);
        if (this.userOrderDao.updateUserVouchersInfo(paramMap) > 0) {
            this.userOrderDao.updateMerchantVouchersInfo(paramMap);
            commonCacheService.delObjectContainsKey(CacheConstants.MERCHANT_VOUCHERSINFO + "_" + merchantId, true);
        }
        
        paramMap.put("orderPayType", 0); //业务类型
        result += this.userOrderDao.insertMerchantPaymentDetails(paramMap);
        paramMap.put("orderPayType", 1); //支付类型
        // 如果缓存存在，同步更新缓存的状态
        if (cachedOrders != null) {
            cachedOrders.put("orderStatus", tempOrderStatus);
            cachedOrders.put("orderPayType", 1);
            cachedOrders.put("dealTime", System.currentTimeMillis());
            Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
            cachedOrders.put("orderStatusName", dictionary.get(cachedOrders.get("orderStatus")+""));
            userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
        }
        //修改商户订单状态
        orderInfoDao.updateMerchantOrderStatus(paramMap);

        // 修改 MerchantPaymentDetails 是否支付为1
        customOrderDao.updatePaymentStatus(paramMap);

        //更新商户侧缓存   ---Revoke 2016.6.6
        merchantCacheService.updateMerchantOrderCache(merchantId.toString(), orderId.toString(), "5");

        //保存用户行为  -Revoke 2016.5.13
        Map<String,Object> actionMap= new HashMap<String,Object>();
        actionMap.put("actionCode",BusinessAction.PAY_ORDER);
        actionMap.put("orderId",orderId);
        actionMap.put("merchantId",merchantId);
        timeLineDao.insertTimeLine(actionMap);

        //推送，用户完成付款
        Map<String,Object> params= new HashMap<String,Object>();
        Long receiveEmployeeUserId = this.customOrderDao.getReceiveEmployeeUserId(paramMap);
        params.put("userId", receiveEmployeeUserId);
        params.put("merchantId", merchantId);
        params.put("orderId", orderId);
        params.put("data", "");
        params.put("pushType",4);
        pushService.asyncCommonPush(params);

        // 包装MQ消息
        Long serviceTypeId = userOrderDao.getOrderServiceTypeId(paramMap);
        String serviceTypeName = commonService.getServiceTypeName(StringUtil.null2Str(serviceTypeId));
        String orderNo = userOrderDao.getOrderNoByOrderId(paramMap);
        Map<String, Object> merchantMap = myMerchantService.selectMerchantBasicInfo(merchantId);
        Map<String, String> userMap = userRelatedCacheServices.getCachedUseInfo(StringUtil.null2Str(userId));
        String payUserPhone = "";
        if (null == userMap || 0 == userMap.size()) {
            payUserPhone = String.valueOf(orderInfoDao.selectOrderUserInfo(paramMap).get("userPhone"));
        } else {
            payUserPhone = userMap.get("phone");
        }
//        Map<String, Object> payTimeMap = userOrderDao.getPaymentTime(paramMap);
//        String payTime = String.valueOf(payTimeMap.get("payTime"));

        Map<String, Object> orderMap = userOrderDao.getOrderInfo(paramMap);

        
        Map<String,Object> bossInfo= merchantPlanDao.getBossIdByMerchant(Long.valueOf(merchantId));
        
        // 是否给Wallet发MQ消息
        if (IS_SEND_MQ_TO_WALLET) {
            Map<String, Object> map = new HashMap<>();
            map.put("merchantId", merchantId); // 商户id
            map.put("merchantUserId", bossInfo.get("user_id")); // 商户userId
            map.put("merchantName", merchantMap.get("name")); // 商户名称
            map.put("userName", ""); // O盟用户名称
            map.put("merchantUserPhone", bossInfo.get("phone")); // 商户手机号
            map.put("orderId", orderNo); // 订单编号
            map.put("orderPayType", 2); // paramMap.get("orderPayType")，支付类型，1:微信支付 2：支付宝支付
            map.put("transSeq", orderId); // 交易流水号
            map.put("thirdTransSeq", tradeNo); // 第三方支付流水号
            map.put("payTime", nowTime); // 支付时间
            map.put("orderTime", orderMap.get("joinTime")); // 订单生成时间
            map.put("orderPrice", orderPrice); // 订单金额
            map.put("transAmount", actualPrice); // 实际支付金额
            map.put("payUserhone", payUserPhone); // 支付用户手机号
            map.put("payUserId", userId); // 支付用户userId
            map.put("orderType", "1"); // 商户订单，固定传1
            map.put("serviceName", serviceTypeName); // 服务名称
            map.put("remark", "服务类型：" + serviceTypeName); // 交易备注
            System.out.println("生成的支付宝MQ消息：" + JSONObject.toJSONString(map));
            String msg = AESUtil.parseByte2HexStr(AESUtil.encrypt(JSONObject.toJSONString(map), DYNAMIC_KEY));
            jsonObject.put("msg1", msg);
        }

        // 是否给C_PLAN发MQ消息
        if (IS_SEND_MQ_TO_C_PLAN) {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("userId", userId);
            map.put("merchantId", merchantId);
            map.put("merchantUserId", bossInfo.get("user_id")); // 商户userId
            map.put("paymentTime", nowTime); // 订单支付时间
            map.put("orderPayType", 1); // 支付方式 1-支付宝支付 2-微信支付 3-现金支付
            map.put("orderActualPrice", actualPrice); // 订单实际支付金额
            map.put("orderPrice", orderPrice); // 订单金额
            map.put("orderType", "1"); // 默认全1商户订单，目前订单多种类型可区分
            map.put("remark", serviceTypeName); // 备注
            String msg = JSONObject.toJSONString(map);
            jsonObject.put("msg2", msg);
        }

        return returnJson(jsonObject, result);
    }

    /** 完成支付宝订单 V1110 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject finishAliPayOrderV1110(Long orderId,String tradeNo,String payDate,String buyerNo, Double consumePrice, Double totalFee, String outTradeNo) throws Exception {
        System.out.println("支付宝回调成功 V1110------------"+orderId+","+tradeNo+","+payDate + ", consumePrice：" + consumePrice + ", String outTradeNo:" + outTradeNo);
        JSONObject jsonObject = new JSONObject();
        int result = 0;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("consumePrice", consumePrice);
        paramMap.put("outTradeNo", outTradeNo);

        Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());

        paramMap.put("orderId", orderId);
        String nowTime="";
        if(StringUtil.isNullStr(payDate)){
            nowTime= getNowYYYYMMDDHHMMSS();
        }else{
            nowTime=payDate;
        }
        paramMap.put("nowTime", nowTime);

        //查询订单状态(查询主库)
        int status=userOrderDao.selectOrderStatus(paramMap);
        if(status==5){
            // return -1; // 重复支付
            return returnJson(jsonObject, -1);
        }

        Long userId = this.userOrderDao.getOrderUserId(paramMap);
        paramMap.put("userId", userId);

        Long merchantId = this.userOrderDao.getOrderMerchantId(paramMap);
        paramMap.put("merchantId", merchantId);

        Long userVouchersId = this.userOrderDao.getOrderVouchersId(paramMap);
        paramMap.put("userVouchersId", userVouchersId);

        Double orderPrice = this.userOrderDao.getOrderPrice(paramMap);
        paramMap.put("orderPrice", orderPrice);

        Double actualPrice = this.userOrderDao.getOrderActualPrice(paramMap);
        paramMap.put("actualPrice", actualPrice);

        paramMap.put("orderPayType", 1); //业务类型
        int tempOrderStatus = 3;
        paramMap.put("orderStatus", tempOrderStatus);

        // 判断是新、老的订单（根据订单order_info中的状态判断是否新老订单）
        Map<String, Object> userOrderCacheMap = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
        if (4 == StringUtil.nullToInteger(userOrderCacheMap.get("orderStatus"))) {
            tempOrderStatus = 5;
        }

        paramMap.put("tradeNo", tradeNo);

        //20161018
        paramMap.put("buyerNo", buyerNo);
        paramMap.put("consumePrice", consumePrice);

        // 处理MerchantPaymentDetails
        if (null == actualPrice) {
            paramMap.put("actualPrice", totalFee);
        }
        
        handleMerchantPaymentDetails(paramMap, 1); // 1-支付宝；2-微信；3-银联；9-消费抵用金支付

        paramMap.put("orderStatus", tempOrderStatus);

        result += this.userOrderDao.finishPayOrder(paramMap);

        //首单送钱
        this.firstOrderSendMoney(merchantId,orderId);

        if (null == paramMap.get("actualPrice")) {
            paramMap.put("actualPrice", totalFee + consumePrice);
        }

        result += this.userOrderDao.updateUserStatisticsService(paramMap);
        result += this.userOrderDao.updateMerchantStatisticsService(paramMap);
        if (this.userOrderDao.updateUserVouchersInfo(paramMap) > 0) {
            this.userOrderDao.updateMerchantVouchersInfo(paramMap);
            commonCacheService.delObjectContainsKey(CacheConstants.MERCHANT_VOUCHERSINFO + "_" + merchantId, true);
        }

        paramMap.put("orderPayType", 0); //业务类型
        paramMap.put("orderPayType", 1); //支付类型
        // 如果缓存存在，同步更新缓存的状态
        if (cachedOrders != null) {
            cachedOrders.put("orderStatus", tempOrderStatus);
            cachedOrders.put("orderPayType", 1);
            cachedOrders.put("dealTime", System.currentTimeMillis());
            Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
            cachedOrders.put("orderStatusName", dictionary.get(cachedOrders.get("orderStatus")+""));
            userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
        }
        //修改商户订单状态
        orderInfoDao.updateMerchantOrderStatus(paramMap);

        // 修改 MerchantPaymentDetails 是否支付为1
        customOrderDao.updatePaymentStatus(paramMap);

        //更新商户侧缓存   ---Revoke 2016.6.6
//        merchantCacheService.updateMerchantOrderCache(merchantId.toString(), orderId.toString(), "5");

        //保存用户行为  -Revoke 2016.5.13
        Map<String,Object> actionMap= new HashMap<String,Object>();
        actionMap.put("actionCode",BusinessAction.PAY_ORDER);
        actionMap.put("orderId",orderId);
        actionMap.put("merchantId",merchantId);
        timeLineDao.insertTimeLine(actionMap);

        //推送，用户完成付款
        Map<String,Object> params= new HashMap<String,Object>();
        Long receiveEmployeeUserId = this.customOrderDao.getReceiveEmployeeUserId(paramMap);
        params.put("userId", receiveEmployeeUserId);
        params.put("merchantId", merchantId);
        params.put("orderId", orderId);
        params.put("data", "");
        params.put("pushType",4);
        pushService.asyncCommonPush(params);

        // 包装MQ消息
        Long serviceTypeId = userOrderDao.getOrderServiceTypeId(paramMap);
        String serviceTypeName = commonService.getServiceTypeName(StringUtil.null2Str(serviceTypeId));
        String orderNo = userOrderDao.getOrderNoByOrderId(paramMap);
        Map<String, Object> merchantMap = myMerchantService.selectMerchantBasicInfo(merchantId);
        Map<String, String> userMap = userRelatedCacheServices.getCachedUseInfo(StringUtil.null2Str(userId));
        String payUserPhone = "";
        if (null == userMap || 0 == userMap.size()) {
            payUserPhone = String.valueOf(orderInfoDao.selectOrderUserInfo(paramMap).get("userPhone"));
        } else {
            payUserPhone = userMap.get("phone");
        }
        //        Map<String, Object> payTimeMap = userOrderDao.getPaymentTime(paramMap);
        //        String payTime = String.valueOf(payTimeMap.get("payTime"));

        Map<String, Object> orderMap = userOrderDao.getOrderInfo(paramMap);
        Map<String,Object> bossInfo= merchantPlanDao.getBossIdByMerchant(Long.valueOf(merchantId));

        // 是否给Wallet发MQ消息
        if (IS_SEND_MQ_TO_WALLET) {
            Map<String, Object> map = new HashMap<>();
            map.put("merchantId", merchantId); // 商户id
            map.put("merchantUserId", bossInfo.get("user_id")); // 商户userId
            map.put("merchantName", merchantMap.get("name")); // 商户名称
            map.put("userName", ""); // O盟用户名称
            map.put("merchantUserPhone", bossInfo.get("phone")); // 商户手机号
            map.put("orderId", orderNo); // 订单编号
            map.put("orderPayType", 2); // paramMap.get("orderPayType")，支付类型，1:微信支付 2：支付宝支付
            map.put("transSeq", paramMap.get("id")); // 交易流水号，换成 merchant_payment_details表的主键ID（V1110版本修改）
            map.put("thirdTransSeq", tradeNo); // 第三方支付流水号
            map.put("payTime", nowTime); // 支付时间
            map.put("orderTime", orderMap.get("joinTime")); // 订单生成时间
            map.put("orderPrice", orderPrice); // 订单金额
            map.put("transAmount", actualPrice); // 实际支付金额
            map.put("payUserhone", payUserPhone); // 支付用户手机号
            map.put("payUserId", userId); // 支付用户userId
            map.put("orderType", "1"); // 商户订单，固定传1
            map.put("serviceName", serviceTypeName); // 服务名称
            map.put("remark", "服务类型：" + serviceTypeName); // 交易备注
            if(consumePrice>0){//混合支付
            	String consumerTransSeq="";
            	Map<String,Object> map1=new HashMap<String, Object>();
            	map1.put("userId", userId);
            	map1.put("amount", consumePrice);
            	map1.put("orderType", 1);
            	map1.put("orderId", orderId);
            	Map<String, Object> kingMap = kingService.userAssetPayment(map1);
                if ("000".equals(kingMap.get("resultCode"))) {
                	consumerTransSeq=StringUtil.null2Str(kingMap.get("sourceTransSeq"));
                }
            	map.put("consumerPayFlag", 1); // 是否消费金抵扣 0否；1是
            	map.put("consumerAmount", StringUtil.nullToDouble(consumePrice)); // 消费抵扣金                
            	map.put("consumerTransSeq", StringUtil.null2Str(consumerTransSeq)); // 消费抵扣金流水号
            }
            System.out.println("生成的支付宝MQ消息：" + JSONObject.toJSONString(map));
            String msg = AESUtil.parseByte2HexStr(AESUtil.encrypt(JSONObject.toJSONString(map), DYNAMIC_KEY));
            jsonObject.put("msg1", msg);
        }

        // 完成支付，回调消费金接口确认扣款
        if (0 < consumePrice) {
            handleConsume(paramMap);
        }

        return returnJson(jsonObject, result);
    }
    
    /** 完成银联订单 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject finishUnionOrder(Long orderId,String tradeNo,String payDate) throws Exception {
        System.out.println("银联回调成功------------"+orderId+","+tradeNo+","+payDate);
        JSONObject jsonObject = new JSONObject();
        int result = 0;

        Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
        if (cachedOrders != null && cachedOrders.containsKey("orderPayType") && cachedOrders.get("orderPayType") != null
                && cachedOrders.containsKey("orderStatus") && StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("5")) {
            // return -1; // 重复支付
            return returnJson(jsonObject, -1);
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        String nowTime="";
        if(StringUtil.isNullStr(payDate)){
        	nowTime= getNowYYYYMMDDHHMMSS();
        }else{
        	nowTime=payDate;
        }
        paramMap.put("nowTime", nowTime);
        

        //查询订单状态(查询主库)
        int status=userOrderDao.selectOrderStatus(paramMap);
        if(status==5){
            // return -1; // 重复支付
            return returnJson(jsonObject, -1);
        }

        Long userId = this.userOrderDao.getOrderUserId(paramMap);
        paramMap.put("userId", userId);

        Long merchantId = this.userOrderDao.getOrderMerchantId(paramMap);
        paramMap.put("merchantId", merchantId);

        //判断此订单是否已支付
        int count=this.userOrderDao.getOrderPayRecord(paramMap);
        if(count>0){
            // return -1; // 重复支付
            return returnJson(jsonObject, -1);
        }

        Long userVouchersId = this.userOrderDao.getOrderVouchersId(paramMap);
        paramMap.put("userVouchersId", userVouchersId);

        Double orderPrice = this.userOrderDao.getOrderPrice(paramMap);
        paramMap.put("orderPrice", orderPrice);

        Double actualPrice = this.userOrderDao.getOrderActualPrice(paramMap);
        paramMap.put("actualPrice", actualPrice);

        paramMap.put("orderPayType", 5); //业务类型
        paramMap.put("orderStatus", 5);

        paramMap.put("tradeNo", tradeNo);
        paramMap.put("paymentStatus", 0);

        result += this.userOrderDao.finishPayOrder(paramMap);
        if(result==0){
            // return 0;
            return returnJson(jsonObject, 0);
        }

        //首单送钱
        this.firstOrderSendMoney(merchantId,orderId);

        result += this.userOrderDao.updateUserStatisticsService(paramMap);
        result += this.userOrderDao.updateMerchantStatisticsService(paramMap);
        if (this.userOrderDao.updateUserVouchersInfo(paramMap) > 0) {
            this.userOrderDao.updateMerchantVouchersInfo(paramMap);
            commonCacheService.delObjectContainsKey(CacheConstants.MERCHANT_VOUCHERSINFO + "_" + merchantId, true);
        }
        
        paramMap.put("orderPayType", 0); //业务类型
        result += this.userOrderDao.insertMerchantPaymentDetails(paramMap);
        paramMap.put("orderPayType", 5); //支付类型
        // 如果缓存存在，同步更新缓存的状态
        if (cachedOrders != null) {
            cachedOrders.put("orderStatus", 5);
            cachedOrders.put("orderPayType", 5);
            cachedOrders.put("dealTime", System.currentTimeMillis());
            Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
            cachedOrders.put("orderStatusName", dictionary.get(cachedOrders.get("orderStatus")+""));
            userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
        }
        //修改商户订单状态
        orderInfoDao.updateMerchantOrderStatus(paramMap);

        //更新商户侧缓存   ---Revoke 2016.6.6
        merchantCacheService.updateMerchantOrderCache(merchantId.toString(), orderId.toString(), "5");

        //保存用户行为  -Revoke 2016.5.13
        Map<String,Object> actionMap= new HashMap<String,Object>();
        actionMap.put("actionCode",BusinessAction.PAY_ORDER);
        actionMap.put("orderId",orderId);
        actionMap.put("merchantId",merchantId);
        timeLineDao.insertTimeLine(actionMap);

        //推送，用户完成付款
        Map<String,Object> params= new HashMap<String,Object>();
        Long receiveEmployeeUserId = this.customOrderDao.getReceiveEmployeeUserId(paramMap);
        params.put("userId", receiveEmployeeUserId);
        params.put("merchantId", merchantId);
        params.put("orderId", orderId);
        params.put("data", "");
        params.put("pushType",4);
        pushService.asyncCommonPush(params);

        // 包装MQ消息
        Long serviceTypeId = userOrderDao.getOrderServiceTypeId(paramMap);
        String serviceTypeName = commonService.getServiceTypeName(StringUtil.null2Str(serviceTypeId));
        String orderNo = userOrderDao.getOrderNoByOrderId(paramMap);
        Map<String, Object> merchantMap = myMerchantService.selectMerchantBasicInfo(merchantId);
        Map<String, String> userMap = userRelatedCacheServices.getCachedUseInfo(StringUtil.null2Str(userId));
        String payUserPhone = "";
        if (null == userMap || 0 == userMap.size()) {
            payUserPhone = String.valueOf(orderInfoDao.selectOrderUserInfo(paramMap).get("userPhone"));
        } else {
            payUserPhone = userMap.get("phone");
        }
//        Map<String, Object> payTimeMap = userOrderDao.getPaymentTime(paramMap);
//        String payTime = String.valueOf(payTimeMap.get("payTime"));

        Map<String, Object> orderMap = userOrderDao.getOrderInfo(paramMap);

        
        Map<String,Object> bossInfo= merchantPlanDao.getBossIdByMerchant(Long.valueOf(merchantId));
        
        // 是否给Wallet发MQ消息
        if (IS_SEND_MQ_TO_WALLET) {
            Map<String, Object> map = new HashMap<>();
            map.put("merchantId", merchantId); // 商户id
            map.put("merchantUserId", bossInfo.get("user_id")); // 商户userId
            map.put("merchantName", merchantMap.get("name")); // 商户名称
            map.put("userName", ""); // O盟用户名称
            map.put("merchantUserPhone", bossInfo.get("phone")); // 商户手机号
            map.put("orderId", orderNo); // 订单编号
            map.put("orderPayType", 5); // paramMap.get("orderPayType")，支付类型，1:微信支付 2：支付宝支付3:线下5:银联支付
            map.put("transSeq", orderId); // 交易流水号
            map.put("thirdTransSeq", tradeNo); // 第三方支付流水号
            map.put("payTime", nowTime); // 支付时间
            map.put("orderTime", orderMap.get("joinTime")); // 订单生成时间
            map.put("orderPrice", orderPrice); // 订单金额
            map.put("transAmount", actualPrice); // 实际支付金额
            map.put("payUserhone", payUserPhone); // 支付用户手机号
            map.put("payUserId", userId); // 支付用户userId
            map.put("orderType", "1"); // 商户订单，固定传1
            map.put("serviceName", serviceTypeName); // 服务名称
            map.put("remark", "服务类型：" + serviceTypeName); // 交易备注
            System.out.println("生成的支付宝MQ消息：" + JSONObject.toJSONString(map));
            String msg = AESUtil.parseByte2HexStr(AESUtil.encrypt(JSONObject.toJSONString(map), DYNAMIC_KEY));
            jsonObject.put("msg1", msg);
        }

        // 是否给C_PLAN发MQ消息
        if (IS_SEND_MQ_TO_C_PLAN) {
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", orderId);
            map.put("userId", userId);
            map.put("merchantId", merchantId);
            map.put("merchantUserId", bossInfo.get("user_id")); // 商户userId
            map.put("paymentTime", nowTime); // 订单支付时间
            map.put("orderPayType", 5); // 支付方式 1-支付宝支付 2-微信支付 3-现金支付4-银联支付
            map.put("orderActualPrice", actualPrice); // 订单实际支付金额
            map.put("orderPrice", orderPrice); // 订单金额
            map.put("orderType", "1"); // 默认全1商户订单，目前订单多种类型可区分
            map.put("remark", serviceTypeName); // 备注
            String msg = JSONObject.toJSONString(map);
            jsonObject.put("msg2", msg);
        }

        return returnJson(jsonObject, result);
    }

    /** 完成微信订单 Old */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject finishWeChatOrder(Long orderId,String tradeNo,String endTime,String openId) {
        System.out.println("微信回调成功（Old）------------"+orderId+","+tradeNo+","+endTime);
        JSONObject jsonObject = new JSONObject();
        int result = 0;

        Map<String, Object> paramMap = new HashMap<>();
        try {
            Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
            if (cachedOrders != null && cachedOrders.containsKey("orderPayType") && cachedOrders.get("orderPayType") != null && cachedOrders.containsKey("orderStatus")
                    && StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("5")) {
                // return -1; // 重复支付
                return returnJson(jsonObject, -1);
            }

            paramMap.put("orderId", orderId);

            String nowTime="";
            if(StringUtil.isNullStr(endTime)){
            	nowTime= getNowYYYYMMDDHHMMSS();
             }else{
            	 nowTime=endTime;
             }
            paramMap.put("nowTime", nowTime);

            //查询订单状态(查询主库)
            int status=customOrderDao.selectOrderStatus(paramMap);
            if(status==5){
                // return -1;// 重复支付
                return returnJson(jsonObject, -1);
            }

            Long userId = this.userOrderDao.getOrderUserId(paramMap);
            paramMap.put("userId", userId);

            Long merchantId = this.userOrderDao.getOrderMerchantId(paramMap);
            paramMap.put("merchantId", merchantId);

            //判断此订单是否已支付
            int count=this.userOrderDao.getOrderPayRecord(paramMap);
            if(count>0){
                // return -1;
                return returnJson(jsonObject, -1);
            }

            Long userVouchersId = this.userOrderDao.getOrderVouchersId(paramMap);
            paramMap.put("userVouchersId", userVouchersId);

            Double orderPrice = this.userOrderDao.getOrderPrice(paramMap);
            paramMap.put("orderPrice", orderPrice);

            Double actualPrice = this.userOrderDao.getOrderActualPrice(paramMap);
            paramMap.put("actualPrice", actualPrice);

            paramMap.put("orderPayType", 2);
            int tempOrderStatus = 5;
            paramMap.put("orderStatus", tempOrderStatus);

            paramMap.put("tradeNo", tradeNo);
            
            paramMap.put("buyerNo", openId);
            paramMap.put("paymentStatus", 0);

            Map<String, Object> resultMap = myMerchantService.selectpaymentStatus(paramMap);
            if (null != resultMap) {
                tempOrderStatus = 4;
            }
            paramMap.put("orderStatus", tempOrderStatus);

            result += this.userOrderDao.finishPayOrder(paramMap);
            if(result==0){
                // return 0;
                return returnJson(jsonObject, 0);
            }

            //首单送钱
            this.firstOrderSendMoney(merchantId,orderId);
            result += this.userOrderDao.updateUserStatisticsService(paramMap);
            result += this.userOrderDao.updateMerchantStatisticsService(paramMap);
            if (this.userOrderDao.updateUserVouchersInfo(paramMap) > 0) {
                this.userOrderDao.updateMerchantVouchersInfo(paramMap);
                commonCacheService.delObjectContainsKey(CacheConstants.MERCHANT_VOUCHERSINFO + "_" + merchantId, true);
            }
            paramMap.put("orderPayType", 0); //业务类型
            result += this.userOrderDao.insertMerchantPaymentDetails(paramMap);
            paramMap.put("orderPayType", 2); //支付类型
            // 如果缓存存在，同步更新缓存的状态
            if (cachedOrders != null) {
                cachedOrders.put("orderStatus", tempOrderStatus);
                cachedOrders.put("orderPayType", 2);
                cachedOrders.put("dealTime", System.currentTimeMillis());
                Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
                cachedOrders.put("orderStatusName", dictionary.get(cachedOrders.get("orderStatus")+""));
                userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
            }

            //修改商户订单状态
            orderInfoDao.updateMerchantOrderStatus(paramMap);

            // 修改 MerchantPaymentDetails 是否支付为1
            customOrderDao.updatePaymentStatus(paramMap);

            //更新商户侧缓存   ---Revoke 2016.6.6
            merchantCacheService.updateMerchantOrderCache(merchantId.toString(), orderId.toString(), "5");

            //保存用户行为  -Revoke 2016.5.13
            Map<String,Object> actionMap= new HashMap<String,Object>();
            actionMap.put("actionCode",BusinessAction.PAY_ORDER);
            actionMap.put("orderId",orderId);
            actionMap.put("merchantId",merchantId);
            timeLineDao.insertTimeLine(actionMap);

            //推送，用户完成付款
            Map<String,Object> params= new HashMap<String,Object>();
            Long receiveEmployeeUserId = this.customOrderDao.getReceiveEmployeeUserId(paramMap);
            params.put("userId", receiveEmployeeUserId);
            params.put("merchantId", merchantId);
            params.put("orderId", orderId);
            params.put("data", "");
            params.put("pushType",4);
            pushService.basicPush(params);

            // 包装MQ消息
            Long serviceTypeId = userOrderDao.getOrderServiceTypeId(paramMap);
            String serviceTypeName = commonService.getServiceTypeName(StringUtil.null2Str(serviceTypeId));
            String orderNo = userOrderDao.getOrderNoByOrderId(paramMap);
            Map<String, Object> merchantMap = myMerchantService.selectMerchantBasicInfo(merchantId);
            Map<String, String> userMap = userRelatedCacheServices.getCachedUseInfo(StringUtil.null2Str(userId));
            String payUserPhone = "";
            if (null == userMap || 0 == userMap.size()) {
                payUserPhone = String.valueOf(orderInfoDao.selectOrderUserInfo(paramMap).get("userPhone"));
            } else {
                payUserPhone = userMap.get("phone");
            }

            Map<String, Object> orderMap = userOrderDao.getOrderInfo(paramMap);


            Map<String,Object> bossInfo= merchantPlanDao.getBossIdByMerchant(Long.valueOf(merchantId));
            
            // 是否给Wallet发MQ消息
            if (IS_SEND_MQ_TO_WALLET) {
                Map<String, Object> map = new HashMap<>();
                map.put("merchantId", merchantId); // 商户id
                map.put("merchantUserId",  bossInfo.get("user_id")); // 商户userId
                map.put("merchantName", merchantMap.get("name")); // 商户名称
                map.put("userName", ""); // O盟用户名称
                map.put("merchantUserPhone", bossInfo.get("phone")); // 商户手机号
                map.put("orderId", orderNo); // 订单编号
                map.put("orderPayType", 1); // 支付类型，1:微信支付 2：支付宝支付
                map.put("transSeq", orderId); // 交易流水号
                map.put("thirdTransSeq", tradeNo); // 第三方支付流水号
                map.put("payTime", nowTime); // 支付时间
                map.put("orderTime", orderMap.get("joinTime")); // 订单生成时间
                map.put("orderPrice", orderPrice); // 订单金额
                map.put("transAmount", actualPrice); // 实际支付金额
                map.put("payUserhone", payUserPhone); // 支付用户手机号
                map.put("payUserId", userId); // 支付用户userId
                map.put("orderType", "1"); // 商户订单，固定传 1
                map.put("serviceName", serviceTypeName); // 服务名称
                map.put("remark", "服务类型：" + serviceTypeName); // 交易备注
                System.out.println("生成的微信MQ消息：" + JSONObject.toJSONString(map));
                String msg = AESUtil.parseByte2HexStr(AESUtil.encrypt(JSONObject.toJSONString(map), DYNAMIC_KEY));
                jsonObject.put("msg1", msg);
            }

            // 是否给C_PLAN发MQ消息
            if (IS_SEND_MQ_TO_C_PLAN) {
                Map<String, Object> map = new HashMap<>();
                map.put("orderId", orderId);
                map.put("userId", userId);
                map.put("merchantId", merchantId);
                map.put("merchantUserId", bossInfo.get("user_id")); // 商户userId
                map.put("paymentTime", nowTime); // 订单支付时间
                map.put("orderPayType", 2); // 支付方式 1-支付宝支付 2-微信支付 3-现金支付
                map.put("orderActualPrice", actualPrice); // 订单实际支付金额
                map.put("orderPrice", orderPrice); // 订单金额
                map.put("orderType", "1"); // 默认全1商户订单，目前订单多种类型可区分
                map.put("remark", serviceTypeName); // 备注
                String msg = JSONObject.toJSONString(map);
                jsonObject.put("msg2", msg);
            }

            return returnJson(jsonObject, result);
        } catch (Exception e) {
            logger.error("微信支付出错：" + e.getMessage(), e);
            throw new ApplicationException(e, "wechatOrder", "微信支付出错");
        }
    }

    /** 完成微信订单 V1110 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject finishWeChatOrderV1110(Long orderId,String tradeNo,String endTime,String openId, Double consumePrice, Double totalFee, String outTradeNo) {
        System.out.println("微信回调成功（V1110）------------ orderId:"+orderId+", tradeNo:"+tradeNo+","+endTime + ",consumePrice:" + consumePrice + ", outTradeNo:" + outTradeNo);
        JSONObject jsonObject = new JSONObject();
        int result = 0;

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("consumePrice", consumePrice);
        paramMap.put("outTradeNo", outTradeNo);

        try {
            Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
//            if (cachedOrders != null && cachedOrders.containsKey("orderPayType") && cachedOrders.get("orderPayType") != null && cachedOrders.containsKey("orderStatus")
//                    && StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("5")) {
//                // return -1; // 重复支付
//                return returnJson(jsonObject, -1);
//            }

            paramMap.put("orderId", orderId);

            String nowTime="";
            if(StringUtil.isNullStr(endTime)){
                nowTime= getNowYYYYMMDDHHMMSS();
            }else{
                nowTime=endTime;
            }
            paramMap.put("nowTime", nowTime);

            //查询订单状态(查询主库)
            int status=customOrderDao.selectOrderStatus(paramMap);
            if(status==5){
                return returnJson(jsonObject, -1);
            }

            Long userId = this.userOrderDao.getOrderUserId(paramMap);
            paramMap.put("userId", userId);

            Long merchantId = this.userOrderDao.getOrderMerchantId(paramMap);
            paramMap.put("merchantId", merchantId);

            Long userVouchersId = this.userOrderDao.getOrderVouchersId(paramMap);
            paramMap.put("userVouchersId", userVouchersId);

            Double orderPrice = this.userOrderDao.getOrderPrice(paramMap);
            paramMap.put("orderPrice", orderPrice);

            Double actualPrice = this.userOrderDao.getOrderActualPrice(paramMap);
            paramMap.put("actualPrice", actualPrice);

            paramMap.put("orderPayType", 2);
            int tempOrderStatus = 3;
            paramMap.put("orderStatus", tempOrderStatus);

            // 判断是新、老的订单（根据订单order_info中的状态判断是否新老订单）
            Map<String, Object> userOrderCacheMap = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
            if (4 == StringUtil.nullToInteger(userOrderCacheMap.get("orderStatus"))) {
                tempOrderStatus = 5;
            }

            paramMap.put("tradeNo", tradeNo);

            paramMap.put("buyerNo", openId);
            paramMap.put("consumePrice", consumePrice);

            // 处理MerchantPaymentDetails
            if (null == actualPrice) {
                paramMap.put("actualPrice", totalFee);
            }
            handleMerchantPaymentDetails(paramMap, 2); // 1-支付宝；2-微信；3-银联；9-消费抵用金支付

            paramMap.put("orderStatus", tempOrderStatus);

            result += this.userOrderDao.finishPayOrder(paramMap);

            //首单送钱
            this.firstOrderSendMoney(merchantId,orderId);

            if (null == paramMap.get("actualPrice")) {
                paramMap.put("actualPrice", totalFee + consumePrice);
            }
            result += this.userOrderDao.updateUserStatisticsService(paramMap);
            result += this.userOrderDao.updateMerchantStatisticsService(paramMap);
            if (this.userOrderDao.updateUserVouchersInfo(paramMap) > 0) {
                this.userOrderDao.updateMerchantVouchersInfo(paramMap);
                commonCacheService.delObjectContainsKey(CacheConstants.MERCHANT_VOUCHERSINFO + "_" + merchantId, true);
            }
            paramMap.put("orderPayType", 0); //业务类型
            paramMap.put("orderPayType", 2); //支付类型
            // 如果缓存存在，同步更新缓存的状态
            if (cachedOrders != null) {
                cachedOrders.put("orderStatus", tempOrderStatus);
                cachedOrders.put("orderPayType", 2);
                cachedOrders.put("dealTime", System.currentTimeMillis());
                Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
                cachedOrders.put("orderStatusName", dictionary.get(cachedOrders.get("orderStatus")+""));
                userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
            }

            //修改商户订单状态
            orderInfoDao.updateMerchantOrderStatus(paramMap);

            // 修改 MerchantPaymentDetails 是否支付为1
            customOrderDao.updatePaymentStatus(paramMap);

            //更新商户侧缓存   ---Revoke 2016.6.6
//            merchantCacheService.updateMerchantOrderCache(merchantId.toString(), orderId.toString(), "5");

            //保存用户行为  -Revoke 2016.5.13
            Map<String,Object> actionMap= new HashMap<String,Object>();
            actionMap.put("actionCode",BusinessAction.PAY_ORDER);
            actionMap.put("orderId",orderId);
            actionMap.put("merchantId",merchantId);
            timeLineDao.insertTimeLine(actionMap);

            //推送，用户完成付款
            Map<String,Object> params= new HashMap<String,Object>();
            Long receiveEmployeeUserId = this.customOrderDao.getReceiveEmployeeUserId(paramMap);
            params.put("userId", receiveEmployeeUserId);
            params.put("merchantId", merchantId);
            params.put("orderId", orderId);
            params.put("data", "");
            params.put("pushType",4);
            pushService.basicPush(params);

            // 包装MQ消息
            Long serviceTypeId = userOrderDao.getOrderServiceTypeId(paramMap);
            String serviceTypeName = commonService.getServiceTypeName(StringUtil.null2Str(serviceTypeId));
            String orderNo = userOrderDao.getOrderNoByOrderId(paramMap);
            Map<String, Object> merchantMap = myMerchantService.selectMerchantBasicInfo(merchantId);
            Map<String, String> userMap = userRelatedCacheServices.getCachedUseInfo(StringUtil.null2Str(userId));
            String payUserPhone = "";
            if (null == userMap || 0 == userMap.size()) {
                payUserPhone = String.valueOf(orderInfoDao.selectOrderUserInfo(paramMap).get("userPhone"));
            } else {
                payUserPhone = userMap.get("phone");
            }
            Map<String, Object> orderMap = userOrderDao.getOrderInfo(paramMap);
            Map<String,Object> bossInfo= merchantPlanDao.getBossIdByMerchant(Long.valueOf(merchantId));

            // 是否给Wallet发MQ消息
            if (IS_SEND_MQ_TO_WALLET) {
                Map<String, Object> map = new HashMap<>();
                map.put("merchantId", merchantId); // 商户id
                map.put("merchantUserId",  bossInfo.get("user_id")); // 商户userId
                map.put("merchantName", merchantMap.get("name")); // 商户名称
                map.put("userName", ""); // O盟用户名称
                map.put("merchantUserPhone", bossInfo.get("phone")); // 商户手机号
                map.put("orderId", orderNo); // 订单编号
                map.put("orderPayType", 1); // paramMap.get("orderPayType")，支付类型，1:微信支付 2：支付宝支付
                map.put("transSeq", paramMap.get("id")); // 交易流水号，换成 merchant_payment_details表的主键ID（V1110版本修改）
                map.put("thirdTransSeq", tradeNo); // 第三方支付流水号
                map.put("payTime", nowTime); // 支付时间
                map.put("orderTime", orderMap.get("joinTime")); // 订单生成时间
                map.put("orderPrice", orderPrice); // 订单金额
                map.put("transAmount", actualPrice); // 实际支付金额
                map.put("payUserhone", payUserPhone); // 支付用户手机号
                map.put("payUserId", userId); // 支付用户userId
                map.put("orderType", "1"); // 商户订单，固定传1
                map.put("serviceName", serviceTypeName); // 服务名称
                map.put("remark", "服务类型：" + serviceTypeName); // 交易备注
                if(consumePrice>0){//混合支付
                	String consumerTransSeq="";
                	Map<String,Object> map1=new HashMap<String, Object>();
                	map1.put("userId", userId);
                	map1.put("amount", consumePrice);
                	map1.put("orderType", 1);
                	map1.put("orderId", orderId);
                	Map<String, Object> kingMap = kingService.userAssetPayment(map1);
                    if ("000".equals(kingMap.get("resultCode"))) {
                    	consumerTransSeq=StringUtil.null2Str(kingMap.get("sourceTransSeq"));
                    }
                	map.put("consumerPayFlag", 1); // 是否消费金抵扣 0否；1是
                	map.put("consumerAmount", StringUtil.nullToDouble(consumePrice)); // 消费抵扣金                
                	map.put("consumerTransSeq", StringUtil.null2Str(consumerTransSeq)); // 消费抵扣金流水号
                }
                System.out.println("生成的微信MQ消息：" + JSONObject.toJSONString(map));
                String msg = AESUtil.parseByte2HexStr(AESUtil.encrypt(JSONObject.toJSONString(map), DYNAMIC_KEY));
                jsonObject.put("msg1", msg);
            }

            // 完成支付，回调消费金接口确认扣款
            if (0 < consumePrice) {
                handleConsume(paramMap);
            }

            return returnJson(jsonObject, result);
        } catch (Exception e) {
            logger.error("微信支付出错：" + e.getMessage(), e);
            throw new ApplicationException(e, "wechatOrder", "微信支付出错");
        }
    }

    // 处理MerchantPaymentDetails
    private void handleMerchantPaymentDetails(Map<String, Object> map, int tradeType) {
        // 判断 merchant_payment_details 表中是否有记录

        map.put("innerTradeNo", map.get("outTradeNo"));
        int count = customOrderDao.findPaymentByTradeNo(map);
        if (0 == count) { // 新订单，插入 merchant_payment_details
            System.out.println("保存 MerchantPaymentDetails 时的订单号和tradeNo：" + map.get("orderId") + ", tradeNo: " + map.get("tradeNo"));
            map.put("paymentType", 0); // 0-订单收入
            map.put("businessId", map.get("orderId"));
            map.put("tradeType", tradeType); // 1-支付宝；2-微信；3-银联；9-消费抵用金支付
            map.put("paymentPrice", map.get("actualPrice"));
            map.put("paymentStatus", 1);
            customOrderDao.insertMerchantPaymentDetails(map);
            System.out.println("========================================================" + map.get("id"));
        } else { // 更新 merchant_payment_details           
            System.out.println("完成支付："+map.toString());
            customOrderDao.updatePaymentStatus(map);
        }
    }

    private void handleConsume(Map<String, Object> map) throws Exception {
        map.put("amount", map.get("consumePrice"));
        map.put("recorderId", customOrderDao.findConsumeBizIdByTradeNo(map));
        map.put("status", 1); // 1支付成功，0支付失败
        kingService.userAssetPayCallBack(map);
    }

    /** 完成现金订单 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int finishCashOrder(Long orderId, Long merchantId, String price,Object payType) {
        try {
            int result = 0;
            String userId = "";


            Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(StringUtil.null2Str(orderId));
            if (cachedOrders != null && cachedOrders.containsKey("orderPayType") && cachedOrders.get("orderPayType") != null && cachedOrders.containsKey("orderStatus")
                    && StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("5")) {
                return -1; // 重复支付
            }

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("orderId", orderId);
            paramMap.put("merchantId", merchantId);
            paramMap.put("price", price);
            paramMap.put("actualPrice", 0);
            String nowTime= getNowYYYYMMDDHHMMSS();
            paramMap.put("nowTime",nowTime);

            if(payType != null) {
                int payType_=StringUtil.nullToInteger(payType);
                //判断支付金额是否和交付清单最新的金额相等
                Map<String,Object> merchantServiceRecordMap=customOrderDao.getMerchantServiceRecord(paramMap);
                if(merchantServiceRecordMap==null){
                    return -1;
                }

                int merchantRecordPayType=StringUtil.nullToInteger(merchantServiceRecordMap.get("payType")==null?"-1":merchantServiceRecordMap.get("payType"));
                if(payType_!=merchantRecordPayType){//实际支付的payType和商户交付记录的payType不一致
                    return -3;
                }
            }
            //查询订单状态(查询主库)
            int status=customOrderDao.selectOrderStatus(paramMap);
            if(status==5){
                return -1;// 重复支付
            }

            //判断此订单是否已支付
            int count=this.userOrderDao.getOrderPayRecord(paramMap);
            if(count>0){
                return -1;
            }

            //		result += this.userOrderDao.confirmOrderPrice(paramMap);

            paramMap.put("orderPayType", 3);
            paramMap.put("orderStatus", 5);
            result += this.userOrderDao.finishPayOrder(paramMap);
            if(result==0){
                return 0;
            }

            //首单送钱
            this.firstOrderSendMoney(merchantId,orderId);

            result += this.userOrderDao.updateUserStatisticsCashService(paramMap);
            result += this.userOrderDao.updateMerchantStatisticsCashService(paramMap);
            paramMap.put("orderPayType", 0); //业务类型
            paramMap.put("paymentStatus", 0);
            result += this.userOrderDao.insertMerchantPaymentDetails(paramMap);
            paramMap.put("orderPayType", 3);

            // 如果缓存存在，同步更新缓存的状态
            if (cachedOrders != null) {
                cachedOrders.put("orderPrice", price);
                cachedOrders.put("orderActualPrice", price);
                cachedOrders.put("merchantId", merchantId);
                cachedOrders.put("confirmTime", System.currentTimeMillis());
                cachedOrders.put("orderStatus", 5);
                cachedOrders.put("orderPayType", 3);
                Map<String,String> dictionary=getOrderStatusMap("receiverOrderStatusMap");
                cachedOrders.put("orderStatusName", dictionary.get(cachedOrders.get("orderStatus")+""));
                cachedOrders.put("dealTime", System.currentTimeMillis());
                userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
            }

            //修改商户订单状态
            orderInfoDao.updateMerchantOrderStatus(paramMap);

            //更新商户侧缓存   ---Revoke 2016.6.6
            merchantCacheService.updateMerchantOrderCache(merchantId.toString(), orderId.toString(), "5");

            //保存用户行为  -Revoke 2016.5.13
            Map<String,Object> actionMap= new HashMap<String,Object>();
            actionMap.put("actionCode",BusinessAction.PAY_ORDER);
            actionMap.put("orderId",orderId);
            actionMap.put("merchantId",merchantId);
            timeLineDao.insertTimeLine(actionMap);

            //推送，用户完成付款
            Map<String,Object> params= new HashMap<String,Object>();
            Long receiveEmployeeUserId = this.customOrderDao.getReceiveEmployeeUserId(paramMap);
            params.put("userId", receiveEmployeeUserId);
            params.put("merchantId", merchantId);
            params.put("orderId", orderId);
            params.put("data", "");
            params.put("pushType",4);
            pushService.basicPush(params);

            // 获取用户Id
            if (cachedOrders != null) {
                userId = String.valueOf(cachedOrders.get("userId"));
            } else {
                userId = String.valueOf(userOrderDao.getOrderUserId(paramMap));
            }
            
            Map<String,Object> bossInfo= merchantPlanDao.getBossIdByMerchant(Long.valueOf(merchantId));

            // 是否完成现金发MQ消息（C_PLAN的需求）
            if (IS_SEND_MQ_TO_CASH) {
                Long serviceTypeId = userOrderDao.getOrderServiceTypeId(paramMap);
                String serviceTypeName = commonService.getServiceTypeName(StringUtil.null2Str(serviceTypeId));
                Map<String, Object> map = new HashMap<>();
                map.put("orderId", orderId);
                map.put("userId", userId);
                map.put("merchantId", merchantId);
                map.put("merchantUserId", bossInfo.get("user_id")); // 商户userId
                map.put("paymentTime", nowTime); // 订单支付时间
                map.put("orderPayType", 3); // 支付方式 1-支付宝支付 2-微信支付 3-现金支付
                map.put("orderActualPrice", paramMap.get("actualPrice")); // 订单实际支付金额
                map.put("orderPrice", paramMap.get("price")); // 订单金额
                map.put("orderType", "1"); // 默认全1商户订单，目前订单多种类型可区分
                map.put("remark", serviceTypeName); // 备注
                pushService.writeToMQ("cplanOrderPayExchange", JSONObject.toJSONString(map));
                BusinessUtil.writeLog("c_plan_cash_mq_success", "订单ID:" + orderId + "，消息体：" + JSONObject.toJSONString(map));
            }

            return result;
        } catch (Exception e) {
            logger.error("现金支付出错 :" + e.getMessage(), e);
            throw new ApplicationException(e, "cashOrder", "现金支付出错");
        }
    }

    /** 结束订单 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int overOrder(Long orderId, Long merchantId, String price) {
        try {
            int result = 0;

            Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
            if (cachedOrders != null && cachedOrders.containsKey("orderPayType") && cachedOrders.get("orderPayType") != null && cachedOrders.containsKey("orderStatus")
                    && StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("5")) {
                return -1; // 重复支付
            }

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("orderId", orderId);
            paramMap.put("merchantId", merchantId);
            paramMap.put("price", price);
            paramMap.put("actualPrice", 0);
            String nowTime= getNowYYYYMMDDHHMMSS();
            paramMap.put("nowTime",nowTime);

            //判断支付金额是否和交付清单最新的金额相等
            Map<String,Object> merchantServiceRecordMap=customOrderDao.getMerchantServiceRecord(paramMap);
            if(merchantServiceRecordMap==null){
                return -1;
            }
//            System.out.println(merchantServiceRecordMap.toString());
            String merchantRecordPrice_=StringUtil.null2Str(merchantServiceRecordMap.get("price"));
            if(merchantRecordPrice_.equals("")){
                //如果没有查询到，则到订单表查询
                Map<String,Object> orderPriceInfo=customOrderDao.getOrderPriceInfo(paramMap);
                merchantRecordPrice_=StringUtil.null2Str(orderPriceInfo.get("merchantActualPrice"));
            }
            if(merchantRecordPrice_.equals("")){
                return -1;
            }
            BigDecimal merchantRecordPrice=new BigDecimal(merchantRecordPrice_);
            if(merchantRecordPrice.compareTo(BigDecimal.ZERO) !=0){//实际要支付的价格和商户交付记录的价格不一致
                return -2;
            }

            //查询订单状态(查询主库)
            int status=customOrderDao.selectOrderStatus(paramMap);
            if(status==5){
                return -1;// 重复支付
            }

            //判断此订单是否已支付
            int count=this.userOrderDao.getOrderPayRecord(paramMap);
            if(count>0){
                return -1;
            }

            Long userId = this.userOrderDao.getOrderUserId(paramMap);
            paramMap.put("userId", userId);

            paramMap.put("orderPayType", 4);
            paramMap.put("orderStatus", 5);
            result += this.userOrderDao.finishPayOrder(paramMap);
            if(result==0){
                return 0;
            }

            //首单送钱
            this.firstOrderSendMoney(merchantId,orderId);

            // result += this.userOrderDao.confirmOrderPrice(paramMap);
            result += this.userOrderDao.updateUserStatisticsCashService(paramMap);
            result += this.userOrderDao.updateMerchantStatisticsCashService(paramMap);
            paramMap.put("orderPayType", 0);
            paramMap.put("paymentStatus", 0);
            result += this.userOrderDao.insertMerchantPaymentDetails(paramMap);
            paramMap.put("orderPayType", 4);

            // 如果缓存存在，同步更新缓存的状态
            if (cachedOrders != null) {
                cachedOrders.put("orderPrice", price);
                cachedOrders.put("orderActualPrice", price);
                cachedOrders.put("merchantId", merchantId);
                cachedOrders.put("confirmTime", System.currentTimeMillis());
                cachedOrders.put("orderStatus", 5);
                cachedOrders.put("orderPayType", 4);
                Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
                cachedOrders.put("orderStatusName", dictionary.get(cachedOrders.get("orderStatus")+""));
                cachedOrders.put("dealTime", System.currentTimeMillis());
                userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
            }
            //修改商户订单状态
            orderInfoDao.updateMerchantOrderStatus(paramMap);

            //更新商户侧缓存   ---Revoke 2016.6.6
            merchantCacheService.updateMerchantOrderCache(merchantId.toString(), orderId.toString(), "5");

            //保存用户行为  -Revoke 2016.5.13
            Map<String,Object> actionMap= new HashMap<String,Object>();
            actionMap.put("actionCode",BusinessAction.PAY_ORDER);
            actionMap.put("orderId",orderId);
            actionMap.put("merchantId",merchantId);
            timeLineDao.insertTimeLine(actionMap);

            //推送，用户完成订单
            Map<String,Object> params= new HashMap<String,Object>();
            Long receiveEmployeeUserId = this.customOrderDao.getReceiveEmployeeUserId(paramMap);
            params.put("userId", receiveEmployeeUserId);
            params.put("merchantId", merchantId);
            params.put("orderId", orderId);
            params.put("data", "");
            params.put("pushType",4);
            pushService.basicPush(params);

            return result;
        } catch (Exception e) {
            logger.error("现金支付出错 :" + e.getMessage(), e);
            throw new ApplicationException(e, "cashOrder", "现金支付出错");
        }
    }
    /** 评价订单 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject evaluationOrder(Long orderId, String attitudeEvaluation,
                                      String qualityEvaluation, String speedEvaluation,
                                      String textEvaluation,List<String> paths) throws Exception {
        JSONObject jsonObject = null;
        String msg = "";

        // 格式化$符号
        textEvaluation = StringUtil.formatDollarSign(textEvaluation);
        int result = 0;
        int check = 0;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        Date date = new Date();
        paramMap.put("joinTime", date);

        Map<String, Object> cachedOrderInfo = userRelatedCacheServices
                .getCachedUserOrder(orderId.toString());
        paramMap.put("orderId", orderId);

        if (cachedOrderInfo == null) {
            check = this.userOrderDao.checkAssessOrder(paramMap);
        } else {
            check = StringUtil.nullToInteger(cachedOrderInfo.get("evaluate"));
        }
        if (check == 0) {
            paramMap.put("attitudeEvaluation", attitudeEvaluation);
            paramMap.put("qualityEvaluation", qualityEvaluation);
            paramMap.put("speedEvaluation", speedEvaluation);
            paramMap.put("textEvaluation", textEvaluation);
            // paramMap.put("appType", appType);
            Long userId = this.userOrderDao.getOrderUserId(paramMap);
            paramMap.put("userId", userId);
            Long merchantId = this.userOrderDao.getOrderMerchantId(paramMap);
            paramMap.put("merchantId", merchantId);
            Integer serviceTypeId = this.userOrderDao.getServiceType(paramMap);
            paramMap.put("serviceTypeId", serviceTypeId);
            result += this.userOrderDao.assessOrder(paramMap); // 保存订单评价
            result += this.userOrderDao.updateAssessMerchantStatistics(paramMap);

            if (cachedOrderInfo != null) {
                cachedOrderInfo.put("evaluate", 1);
                userRelatedCacheServices.cacheUserOrderWithJson(cachedOrderInfo.get("userId").toString(), cachedOrderInfo);
            }
            // // 清除商户缓存信息 TODO 暂时去除
            // commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO_V23,
            // StringUtil.null2Str(merchantId));
            // commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO_V24,
            // StringUtil.null2Str(merchantId));

            //保存评价附件
            if(result>0 && paths!=null && paths.size()>0){
                paramMap.put("attachmentType", 1);
                paramMap.put("paths", paths);
                customOrderDao.saveEvaluationAttachment(paramMap);
            }

            //保存用户行为  -Revoke 2016.5.13
            Map<String,Object> actionMap= new HashMap<String,Object>();
            actionMap.put("actionCode",BusinessAction.COMMENT_ORDER);
            actionMap.put("orderId",orderId);
            actionMap.put("merchantId",merchantId);
            timeLineDao.insertTimeLine(actionMap);

            //推送，用户评价
            Map<String,Object> params= new HashMap<String,Object>();
            Long receiveEmployeeUserId = this.customOrderDao.getReceiveEmployeeUserId(paramMap);
            params.put("userId", receiveEmployeeUserId);
            params.put("merchantId", merchantId);
            params.put("orderId", orderId);
            params.put("data", "");
            params.put("pushType",5);
            pushService.basicPush(params);

            if (IS_SEND_MQ_TO_C_PLAN_AFTER_ORDER_COMMENT) {
                Map<String, Object> bossInfo = merchantPlanDao.getBossIdByMerchant(Long.valueOf(merchantId));
                Map<String, String> userMap = userRelatedCacheServices.getCachedUseInfo(StringUtil.null2Str(userId));
                String payUserPhone = "";
                if (null == userMap || 0 == userMap.size()) {
                    payUserPhone = String.valueOf(orderInfoDao.selectOrderUserInfo(paramMap).get("userPhone"));
                } else {
                    payUserPhone = userMap.get("phone");
                }
                //查询是否是私人助理
                int isPrivateAssistant;
                Map<String, Object> info = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
                if (null == info) {
                    isPrivateAssistant = StringUtil.nullToInteger(customOrderDao.getIsPrivateAssistantByMerchantId(paramMap));
                } else {
                    Object obj = info.get("isPrivateAssistant");
                    if (null == obj) {
                        isPrivateAssistant = StringUtil.nullToInteger(customOrderDao.getIsPrivateAssistantByMerchantId(paramMap));
                    } else {
                        isPrivateAssistant = StringUtil.nullToInteger(obj);
                    }
                }
                Map<String, Object> map = new HashMap<>();
                map.put("merchantUserId", bossInfo.get("user_id")); // 店主UserID
                map.put("merchantId", merchantId); // 店铺ID
                map.put("score", attitudeEvaluation + "," + qualityEvaluation + "," + speedEvaluation); // 评分，预留字段，（态度，质量，速度）
                map.put("orderId", orderId); // 订单ID
                map.put("content", textEvaluation); // 评价内容
                map.put("userPhone", payUserPhone); // 评论人手机号
                map.put("userId", userId); // 评论人用户ID
                map.put("time", formatDate("yyyy-MM-dd HH:mm:ss", date)); // 评论时间
                map.put("isPersonal", isPrivateAssistant); // 是否私人助理，0：否，1：是
                msg = JSONObject.toJSONString(map);
                System.out.println("订单评价消息通知：" + msg);
            }

            commonCacheService.deleteObject(CacheConstants.MERCHANT_ESTIMATE,StringUtil.null2Str(merchantId));
            // 清除商户缓存信息
//    		commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//    				StringUtil.null2Str(merchantId));
    		//评价后更新商铺缓存
			this.updateMerchantCache(merchantId);
        } else {
            result = -1;
        }
        if (result > 0) {
            jsonObject = new ResultJSONObject("000", "更新评价成功");
        } else if (result == -1) {
            jsonObject = new ResultJSONObject("010", "不能重复评价");
        } else {
            jsonObject = new ResultJSONObject("001", "更新评价失败");
        }
        jsonObject.put("msg_comment", msg);
        return jsonObject;
    }

	private void updateMerchantCache(long merchantId) {
		Map<String, Object> merchantInfo = (Map<String, Object>) commonCacheService
				.getObject(CacheConstants.MERCHANT_BASIC_INFO,
						StringUtil.null2Str(merchantId));
		if (merchantInfo != null) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("merchantId", merchantId);
			// 商户的评分和星级
			BigDecimal starLevel = new BigDecimal("5");
			BigDecimal score = new BigDecimal("5.0");
			Map<String, Object> statisticsInfoMap = this.evaluationDao.selectStatisticsInfo(paramMap);
			if (statisticsInfoMap != null) {
				// 星级和评分编辑
				int totalCountEvaluation = StringUtil.nullToInteger(statisticsInfoMap.get("totalCountEvaluation"));
				if (totalCountEvaluation != 0) {
					int totalAttitudeEvaluation = StringUtil.nullToInteger(statisticsInfoMap.get("totalAttitudeEvaluation"));
					int totalQualityEvaluation = StringUtil.nullToInteger(statisticsInfoMap.get("totalQualityEvaluation"));
					int totalSpeedEvaluation = StringUtil.nullToInteger(statisticsInfoMap.get("totalSpeedEvaluation"));
					// 总服务态度评价+总服务质量评价+总服务速度评价
					int totalEvaluation = totalAttitudeEvaluation
							+ totalQualityEvaluation + totalSpeedEvaluation;
					// 分数
					score = new BigDecimal(totalEvaluation).divide(
							new BigDecimal(totalCountEvaluation).multiply(new BigDecimal(3)), 1,
							BigDecimal.ROUND_DOWN);
					// 星级
					starLevel = new BigDecimal(totalEvaluation).divide(
							new BigDecimal(totalCountEvaluation).multiply(new BigDecimal(3)), 0,
							BigDecimal.ROUND_HALF_UP);
				}
			}
			merchantInfo.put("starLevel", starLevel);
			merchantInfo.put("score", score);
			merchantInfo.put("grabFrequency", statisticsInfoMap.get("grabFrequency"));
			merchantInfo.put("totalIncomePrice",
					statisticsInfoMap.get("totalIncomePrice"));

			// 商户的评价数量
			int evaluationCount = this.evaluationDao.getMerchantEvaluationNum(merchantId);
			merchantInfo.put("evaluationCount", evaluationCount);

			// 商户的最新N条评价
			paramMap.put("num", 2);
			List<Map<String, Object>> topEvaluation = this.evaluationDao.getTextEvaluationTopN(paramMap);
			merchantInfo.put("topEvaluation", topEvaluation);
			// 更新缓存中的商铺信息(暂定一天未访问则移除缓存)
			commonCacheService.setObject(merchantInfo,
					CacheConstants.MERCHANT_BASIC_INFO_TIMEOUT,
					CacheConstants.MERCHANT_BASIC_INFO,
					StringUtil.null2Str(merchantId));
		}
	}
	
    /**
     * 用户保存选择的商品
     * @param appType
     * @param orderId
     * @param merchantId
     * @param merchantPlanId
     * @param goodsIds
     * @param goodsNums
     * @return
     * JSONObject
     * @throws
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject saveOrderGoods(String appType,String shopName,Long orderId, Long merchantId, Long merchantPlanId, String goodsIds, String goodsNums) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new ResultJSONObject("000", "用户保存选择的商品成功");
            int i = this.saveOrderGoods(orderId, goodsIds, goodsNums);
            if (i == 0) {
                jsonObject = new ResultJSONObject("add_order_goods_failure", "用户保存选择的商品失败");
            }
            this.choosePricePlan(appType,shopName,merchantId, merchantPlanId, orderId);
        } catch (Exception ex) {
            logger.error("用户保存选择的商品失败", ex);
            throw new ApplicationException(ex, "add_order_goods_failure", "用户保存选择的商品失败");
        }
        return jsonObject;
    }


    /**
     * 商户确认订单支付金额
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public JSONObject confirmOrderPayPrice(Long merchantId,Long orderId) throws Exception {
        JSONObject jsonObject = new ResultJSONObject("000", "商户确认订单支付金额成功");
        //查询报价方案金额，定金金额
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("merchantId", merchantId);
        map.put("orderId", orderId);
        Map<String,Object> orderPriceInfoMap=customOrderDao.getPlanPriceInfo(map);
        jsonObject.put("orderPriceInfoMap", orderPriceInfoMap);
        return jsonObject;
    }

    /**
     * 商户服务完成后保存服务记录
     //	 * @param merchantId
     //	 * @param orderId
     //	 * @param picturePaths
     //	 * @param remark
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject saveMerchantServiceRecord(Map<String, Object> params, List<String> paths) throws Exception {
		/*参数:
		 * merchantId 完成服务的服务商ID
		 * userId 发单用户的的用户ID
		 * orderId
		 * payType 用户付款方式，0线上，1线下
		 * remark 备注
		 * */
        JSONObject jsonObject = new ResultJSONObject("000", "保存交付清单成功");
        if(true){
        	return new ResultJSONObject("001", "O盟新版已上线，无需再提供交付清单，快去更新吧！");
        }
        
        
        Long merchantId=StringUtil.nullToLong(params.get("merchantId"));

        Integer merchantOrderStatus=customOrderDao.getMerchantOrderStatus(params);
        if(merchantOrderStatus!=null && (int)merchantOrderStatus != 3){
            return new ResultJSONObject("001", "状态已改变");
        }

        // 保存服务记录
        params.put("remark", filterFourCharString(String.valueOf(params.get("remark"))));
        int result=customOrderDao.saveMerchantServiceRecord(params);
        Long merchantServiceRecordId=StringUtil.nullToLong(params.get("id"));
        //保存
        if(result>0 && paths!=null && paths.size()>0){
            params.put("merchantServiceRecordId", merchantServiceRecordId);
            params.put("attachmentType", 1);
            params.put("paths", paths);
            customOrderDao.saveMerchantServiceRecordAttachment(params);
        }

        //修改订单实付金额和用户支付方式
        customOrderDao.updOrderActualPrice(params);

        //修改用户订单状态为4	待支付
        int orderStatus=4;
        params.put("orderStatus", orderStatus);
        orderInfoDao.updateOrderStatus(params);
        //修改用户订单状态
        Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(StringUtil.null2Str(params.get("orderId")));
        // 如果缓存存在，同步更新缓存的状态
        if (cachedOrders != null) {
            cachedOrders.put("orderStatus", orderStatus);
            cachedOrders.put("merchantActualPrice",params.get("actualPrice"));
            Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
            cachedOrders.put("orderStatusName", dictionary.get(orderStatus+""));
            userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
        }
        //修改商户订单状态为4	待收款
        orderStatus=4;
        params.put("orderStatus", orderStatus);
        orderInfoDao.updateMerchantOrderStatus(params);

        // 则同步更新商户-订单 缓存的状态。
        merchantCacheService.updateMerchantOrderCache(StringUtil.null2Str(params.get("merchantId")), StringUtil.null2Str(params.get("orderId")), StringUtil.null2Str(orderStatus));

        //保存用户行为  -Revoke 2016.5.13
        Map<String,Object> actionMap= new HashMap<String,Object>();
        actionMap.put("actionCode",BusinessAction.FINISH_SERVER);
        actionMap.put("orderId",StringUtil.nullToLong(params.get("orderId")));
        actionMap.put("merchantId",merchantId);
        timeLineDao.insertTimeLine(actionMap);

        //推送，商户完成服务
        //查询是否是私人助理
        int isPrivateAssistant=0;
        Map<String, Object> info = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
        if(info!=null){
        	Object obj=info.get("isPrivateAssistant");
        	if(obj!=null){
        		isPrivateAssistant=StringUtil.nullToInteger(obj);
        	}else{
        		isPrivateAssistant = StringUtil.nullToInteger(customOrderDao.getIsPrivateAssistantByMerchantId(params));
        	}
        }else{
        	isPrivateAssistant = StringUtil.nullToInteger(customOrderDao.getIsPrivateAssistantByMerchantId(params));
        }
        params.put("isPrivateAssistant", isPrivateAssistant);
        params.put("data", "");
        params.put("pushType",12);
        pushService.basicPush(params);
        return jsonObject;
    }
    /**
     * 商户服务完成后保存服务记录
     //	 * @param merchantId
     //	 * @param orderId
     //	 * @param picturePaths
     //	 * @param remark
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject updateMerchantServiceRecord(Map<String, Object> params, List<String> paths) throws Exception {
		/*参数:
		 * merchantId 完成服务的服务商ID,
		 * userId 发单用户的的用户ID,
		 * orderId,
		 * price 交付价格
		 * remark 备注
		 * */
        JSONObject jsonObject = new ResultJSONObject("000", "修改交付清单成功");

        Long merchantId=StringUtil.nullToLong(params.get("merchantId"));
        Long orderId=StringUtil.nullToLong(params.get("orderId"));

        int merchantOrderStatus=0;
        Map<String, Object> cacheOrderInfo= merchantCacheService.getCachedMerchantPushOrder(StringUtil.null2Str(merchantId), StringUtil.null2Str(orderId));
        if(cacheOrderInfo!=null && !(cacheOrderInfo.toString()).equals("{}")){
            merchantOrderStatus=StringUtil.nullToInteger(cacheOrderInfo.get("orderStatus"));
        }else{
            merchantOrderStatus=StringUtil.nullToInteger(customOrderDao.getMerchantOrderStatus(params));
        }
        if(merchantOrderStatus != 4){
            return new ResultJSONObject("001", "状态已改变，禁止修改交付记录");
        }

        // 修改服务记录
        int result=customOrderDao.updateMerchantServiceRecord(params);

        Long merchantServiceRecordId=StringUtil.nullToLong(params.get("merchantServiceRecordId"));

        String delPaths="";
        //保存附件
        if(result>0 && paths!=null && paths.size()>0){
            params.put("merchantServiceRecordId", merchantServiceRecordId);
            params.put("attachmentType", 1);
            params.put("paths", paths);
            customOrderDao.saveMerchantServiceRecordAttachment(params);
            for(String path : paths){
                delPaths+="'"+path+"',";
            }
        }
        if(StringUtil.isNotEmpty(delPaths)){
            delPaths=delPaths.substring(0,delPaths.length()-1);
        }else{
            delPaths="''";
        }
        //删除之前的附件
        params.put("paths", delPaths);
        this.customOrderDao.deleteMerchantServiceRecordAttachment(params);

        //修改订单实付金额
        customOrderDao.updOrderMerchantActualPrice(params);

        //修改用户侧订单价格
        Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(StringUtil.null2Str(params.get("orderId")));
        // 如果缓存存在，同步更新缓存的状态
        if (cachedOrders != null) {
            cachedOrders.put("merchantActualPrice",params.get("actualPrice"));
            userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
        }

        //推送，修改商户完成服务
        Map<String,Object> pushMap=new HashMap<String, Object>();
        pushMap.put("userId",params.get("userId"));
        pushMap.put("orderId",params.get("orderId"));
        pushMap.put("data", "");
        pushMap.put("pushType",13);
        pushService.basicPush(pushMap);

        return jsonObject;
    }
    /**
     * 商户查询服务记录
     * @param merchantId
     * @param orderId
     */
    @Override
    public JSONObject getMerchantServiceRecord(Long merchantId,Long orderId){
        JSONObject jsonObject = new ResultJSONObject("000", "商户查询服务记录成功");
        Map<String,Object> param=new HashMap<String, Object>();
        param.put("merchantId", merchantId);
        param.put("orderId", orderId);
        //查询服务记录
        Map<String,Object> merchantServiceRecordMap=customOrderDao.getMerchantServiceRecord(param);
        BusinessUtil.disposeManyPath(merchantServiceRecordMap, "paths");
        //查询商户信息
        Map<String, Object> merchantInfo = customOrderDao.getMerchantInfo(param);
        BusinessUtil.disposeManyPath(merchantInfo, "merchantIcon");
        //查询价格信息
        Map<String,Object> orderPriceInfo=customOrderDao.getOrderPriceInfo(param);
        jsonObject.put("merchantInfo", merchantInfo);
        jsonObject.put("merchantServiceRecord", merchantServiceRecordMap);
        jsonObject.put("orderPriceInfo", orderPriceInfo);
        return jsonObject;
    }

    /**
     * 用户查询服务记录
     * @param merchantId
     * @param orderId
     */
    @Override
    public JSONObject getMerchantServiceRecordForUser(Long merchantId,Long orderId){
        JSONObject jsonObject = new ResultJSONObject("000", "更新评价成功");
        Map<String,Object> param=new HashMap<String, Object>();
        param.put("merchantId", merchantId);
        param.put("orderId", orderId);
        Map<String,Object> merchantServiceRecordMap=customOrderDao.getMerchantServiceRecord(param);
        Map<String,Object> confirmOrderPayPrice=customOrderDao.getPlanPriceInfo(param);
        jsonObject.put("merchantServiceRecord", merchantServiceRecordMap);
        jsonObject.put("confirmOrderPayPrice", confirmOrderPayPrice);
        return jsonObject;
    }

    /**
     * 保存报价方案详情
     *
     * @param planInfoMap
     * @return
     */
    @SuppressWarnings("unchecked")
    private void savePlanDetail(Map<String, Object> planInfoMap) {
        if (planInfoMap != null && !planInfoMap.isEmpty()) {

            /**
             * 详情只保存自定义模块的数据，并去掉（内容描述和服务承诺字段）
             */
            List<Map<String,Object>> jsonDetailList = new ArrayList<Map<String,Object>>();

            /**
             * 第二步解析自定义表单参数，与自定义表单对应解析
             */
            String serviceId = StringUtil.null2Str(planInfoMap
                    .get("planType"));
            List<Map<String, Object>> planForm = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.PLAN_FORM, serviceId);
//			System.out.println(JSONObject.toJSONString(planForm));
            if (planForm != null && !planForm.isEmpty()) {
                for (Map<String, Object> map : planForm) {
                    String parmKey = StringUtil.null2Str(map.get("colName")); // 下发表单的参数名
                    if("1".equals(StringUtil.null2Str(map.get("modelType")))&&!"detail".equals(parmKey)&&!"promise".equals(parmKey)&&!"content".equals(parmKey)){
                        // 详情仅保存自定义区域,并排除detail，promise字段
                        Map<String, Object> jsonMap = new HashMap<String, Object>();

                        String val = StringUtil.null2Str(planInfoMap.get(parmKey));
                        if(!StringUtil.isNullStr(val)){
                            jsonMap.put("colName", parmKey);
                            jsonMap.put("icon", map.get("icon"));
                            jsonMap.put("colDesc", map.get("colDesc"));
                            jsonMap.put("value", val);
                            jsonDetailList.add(jsonMap);
                        }

                    }
                }
            }
            if(jsonDetailList.size()>0){
                planInfoMap.put("jsonDetail", JsonUtil.list2json(jsonDetailList)); // json字符串
                customOrderDao.saveCustomPlanDetail(planInfoMap);
            }
        }
    }
    /**
     * 保存报价方案详情
     *
     * @param planInfoMap
     * @return
     */
    private void updatePlanDetail(Map<String, Object> planInfoMap) {
        if (planInfoMap != null && !planInfoMap.isEmpty()) {

            /**
             * 详情只保存自定义模块的数据，并去掉（内容描述和服务承诺字段）
             */
            List<Map<String,Object>> jsonDetailList = new ArrayList<Map<String,Object>>();

            /**
             * 第二步解析自定义表单参数，与自定义表单对应解析
             */
            String serviceId = StringUtil.null2Str(planInfoMap
                    .get("planType"));
            List<Map<String, Object>> planForm = (List<Map<String, Object>>) commonCacheService
                    .getObject(CacheConstants.PLAN_FORM, serviceId);

            if (planForm != null && !planForm.isEmpty()) {
                for (Map<String, Object> map : planForm) {
                    String parmKey = StringUtil.null2Str(map.get("colName")); // 下发表单的参数名
                    if("1".equals(StringUtil.null2Str(map.get("modelType")))&&!"detail".equals(parmKey)&&!"promise".equals(parmKey)&&!"content".equals(parmKey)){
                        // 详情仅保存自定义区域,并排除detail，promise字段
                        Map<String, Object> jsonMap = new HashMap<String, Object>();

                        String val = StringUtil.null2Str(planInfoMap.get(parmKey));
                        if(!StringUtil.isNullStr(val)){
                            jsonMap.put("colName", parmKey);
                            jsonMap.put("icon", map.get("icon"));
                            jsonMap.put("colDesc", map.get("colDesc"));
                            jsonMap.put("value", val);
                            jsonDetailList.add(jsonMap);
                        }

                    }
                }
            }
            if(jsonDetailList.size()>0){
                planInfoMap.put("jsonDetail", JsonUtil.list2json(jsonDetailList)); // json字符串
                customOrderDao.updateCustomPlanDetail(planInfoMap);
            }
        }
    }
    /**
     * 查询报价方案详情
     *
     * @param planInfoMap
     *
     * @return
     */
    private List<Map<String,Object>> getPlanDetail(Map<String, Object> planInfoMap) {
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Map<String, Object> planDetail = customOrderDao
                .getPlanDetailByPlanId(planInfoMap);
        if (planDetail != null && !planDetail.isEmpty()) {
            String content = StringUtil.null2Str(planDetail
                    .get("content"));
            //JSONObject jsonObject = JSONObject.parseObject(content);
            JSONArray arr= JSONObject.parseArray(content);
            if (arr != null&&arr.size()>0) {
                for(int i=0;i<arr.size();i++){
                    JSONObject jsonObject = arr.getJSONObject(i);
                    Map<String, Object> map = new HashMap<String,Object>();
                    map.put("colName", jsonObject.get("colName"));
                    map.put("icon", jsonObject.get("icon"));
                    map.put("colDesc", jsonObject.get("colDesc"));
                    map.put("value", jsonObject.get("value"));
                    list.add(map);
                }

            }

        }
        return list;
    }

    @Override
    public JSONObject getPricePlanListForSender(Map<String, Object> param)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public JSONObject getPricePlanDetailForSender(Map<String, Object> param)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public JSONObject getPricePlanDetailForReceiver(Map<String, Object> param)
            throws Exception {
        // TODO Auto-generated method stub
        return null;
    }


    //获取用户端订单编码 ----2016.5.14
    private Map<String,String> getOrderStatusMap(String type) throws Exception{
        Map<String,String>  result =new HashMap<String,String>();
        JSONObject  dictionJson = dictionaryService.getDict(type, null);
        JSONArray   statusArray = dictionJson.getJSONArray("dicts");
        if (statusArray!=null || statusArray.size()>0){
            for (int i=0;i<statusArray.size();i++){
                result.put(statusArray.getJSONObject(i).getString("dictKey"), statusArray.getJSONObject(i).getString("dictValue"));
            }

        }

        return result;
    }
    //获取用户端订单编码 ----2016.5.14
    private String getOrderStatusText(int orderStatus,String type) throws Exception{

        return getCachedOrderStatusDescribe(type,orderStatus+"");
		/*
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("dictType", type);
		paramMap.put("merchantOrderStatus", orderStatus);
		String orderStatusText=customOrderDao.getOrderStatusText(paramMap);

		return orderStatusText;*/
    }


    //是否隶属于用户端关闭状态
    private boolean isCloseOrder(String queryOrderStatus, String orderStatus) {
        if (queryOrderStatus.equals("-1") && (orderStatus.equals("6") || orderStatus.equals("7"))){
            return true;
        }else{
            return false;
        }
    }//是否隶属于用户端关闭状态
    private boolean isCloseOrderForMerchant(String queryOrderStatus, String orderStatus) {
        if (queryOrderStatus.equals("-1") && (orderStatus.equals("6") || orderStatus.equals("7")|| orderStatus.equals("8")|| orderStatus.equals("9"))){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 首单送钱
     */
    private void firstOrderSendMoney(Long merchantId,Long orderId){
        if(StringUtil.isEmpty(merchantId)){
            return ;
        }
        if(StringUtil.isEmpty(orderId)){
            return ;
        }
        Map<String, Object> configMap=commonService.getConfigurationInfoByKey("firstOrderSendMoney");
        if(configMap==null){
            return;
        }
        double money=StringUtil.nullToDouble(configMap.get("config_value"));
        String startTime=StringUtil.null2Str(configMap.get("standby_field1"));
        String endTime=StringUtil.null2Str(configMap.get("standby_field2"));
        boolean bool=BusinessUtil.isNotPastTime(money, startTime, endTime);
        if(!bool){//过期
            logger.error("首单送钱过期："+startTime+","+endTime);
            return;
        }
        Map<String,Object> paramMap=new HashMap<String, Object>();
        //1，判断是否已经首单送过钱
        paramMap.put("merchantId", merchantId);
        int merchantFlag=customOrderDao.getMerchantFlag(paramMap);
        if(merchantFlag==1){//已首单送钱
            return;
        }
        //2，判断是否只有一个订单
        int merchantOrderCount=customOrderDao.getMerchantOrderCount(paramMap);
        if(merchantOrderCount != 0){//如果不是第一个订单
            return;
        }
        //更新商户标记位
        int i=customOrderDao.updateMerchantFlag(paramMap);
        if(i>0){
            //订单余额增加
            paramMap.put("money", money);
            customOrderDao.updateOrderSurplusPrice(paramMap);
            //保存送钱记录
            paramMap.put("orderId", orderId);
            paramMap.put("payType", 3);
            paramMap.put("payMoney", money);
            merchantPayService.addMerchantOrderPaymentDetails(paramMap);
        }
    }

    public void pastTime(int userStatus,int merchantUserStatus,String code,Long orderId,String serviceTime) throws Exception{

        //保存是否已预期操作到缓存
        commonCacheService.setObject("1", 60, CacheConstants.ORDER_PAST_TIME, StringUtil.null2Str(orderId));

        //修改用户订单状态
        Map<String,Object> param=new HashMap<String, Object>();
        param.put("orderId", orderId);
//        param.put("orderStatus", userStatus);
//
//        // 修改商户订单的状态
//        this.orderInfoDao.updateOrderStatus(param);
//
//        param=new HashMap<String, Object>();
//        param.put("orderId", orderId);
//        param.put("orderStatus", merchantUserStatus);
//        this.orderInfoDao.updateMerchantOrderStatus(param);
        //修改订单缓存状态
        Map<String, Object> cachedOrder = userRelatedCacheServices
                .getCachedUserOrder(StringUtil.null2Str(orderId));
        if(cachedOrder!=null){
            cachedOrder.put("orderStatus", userStatus);
            Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
            cachedOrder.put("orderStatusName", dictionary.get("6"));
            userRelatedCacheServices.cacheUserOrderWithJson(
                    cachedOrder.get("userId").toString(), cachedOrder);
        }

        /*保存用户行为  -Revoke 2016.5.13
         * 只改内存状态，不用插入timeline ----2016.9.7  Revoke Yu
        Map<String,Object> actionMap= new HashMap<String,Object>();
        actionMap.put("actionCode",code);
        actionMap.put("orderId",orderId);
        actionMap.put("merchantId",null);
        actionMap.put("actionTime",serviceTime);
        timeLineDao.insertTimeLine(actionMap);
        */

        //已有报价方案，但用户逾期未选择，更新商户侧订单缓存状态   ----2016.06.11
        List<Map<String,Object>> merchantIds=this.customOrderDao.getMerchantsForSpeicalOrder(param);
        if (merchantIds!=null && merchantIds.size()>0){
            merchantCacheService.batchUpdateMerchantOrderStatus(orderId.toString(), merchantIds,merchantUserStatus+"");
        }

    }


    //从列表里删除指定商户
    private void removeSpecialMerchantId(List<Map<String,Object>> ids,String merchantId){
        if (ids==null || ids.size()<1)
            return;
        int index=0;
        for(Map<String,Object> id:ids){
            if (id.get("merchant_id").toString().equals(merchantId)){
                break;
            }
            index++;
        }
        ids.remove(index);
    }


    @Override
    public void cleanMerchantCache() {
        merchantCacheService.cleanCache();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int moveToHistoryPushOrder() {
        //指定时间内未提供报价方案的商户订单，迁移。  商户侧状态为待提供1
        //指定时间内订单状态为已取消的商户订单，迁移。 商户侧状态为7
        //指定时间内用户未选择报价方案的商户订单，迁移，商户侧状态为6
        //指定时间内所有商户均未提供报价方案的，迁移。  商户侧状态为待提供9
        //指定时间内服务已完成的商户订单,迁移。  商户侧状态为待提供
        return 0;
    }


    @Override
    public List<Map<String, Object>> orderCatalogList(String httpStr) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("catalogId", "");
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> catalogList = (List<Map<String, Object>>) commonCacheService.getObject("orderCatalogList");
        if(catalogList==null||catalogList.isEmpty()||catalogList.size()<1){
            catalogList = customOrderDao.getCatalogList(param);
            if(catalogList!=null&&catalogList.size()>0){
                String Url = "";
                Map<String,Object> thirdMap = null; // 第三方
                for(Map<String,Object> map:catalogList){
                    String icon = StringUtil.null2Str(map.get("icon"));
                    icon = BusinessUtil.disposeImagePath(icon);
                    map.put("icon", icon);
                    String bigIcon = StringUtil.null2Str(map.get("bigIcon"));
                    bigIcon = BusinessUtil.disposeImagePath(bigIcon);
                    map.put("bigIcon", bigIcon);
                    String flag = StringUtil.null2Str(map.get("flag"));//分类标识（0：行业分类，1：个性服务，2：第三方服务）
                    List<Map<String, Object>> orderCatalogAndServiceList = new ArrayList<Map<String, Object>>();
                    if("2".equals(flag)){
                        Url = StringUtil.null2Str(map.get("url"));
                        thirdMap = map;
                        continue;
                    }else{
                        Url = "";
                        orderCatalogAndServiceList = getChildren(map.get("id")+"",httpStr);
                    }


                    map.put("url", Url);
                    map.put("orderCatalogAndServiceList", orderCatalogAndServiceList);
                    list.add(map);
                }
                if(thirdMap!=null&&!thirdMap.isEmpty()){
                    list.add(thirdMap);
                }
            }
            commonCacheService.setExpireForObject(list, 2*60, "orderCatalogList"); // 2分钟
        }else{
            list.addAll(catalogList);
        }


        return list;
    }

    public List<Map<String, Object>> getChildren(String catalogId,String httpStr) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("catalogId", StringUtil.null2Str(catalogId));
        List<Map<String,Object>> catalogOrServiceList = new ArrayList<Map<String,Object>>();;
        List<Map<String,Object>> catalogList = customOrderDao.getCatalogList(param); // 根据一级分类id获取二级分类
        List<Map<String,Object>> serviceList = getServiceList(param,httpStr); // 根据一级分类id获取二级服务
        String icon = "";
        if(catalogList!=null&&catalogList.size()>0){
            for(Map<String,Object> map:catalogList){
                map.remove("bigIcon");
                map.remove("flag");
                map.remove("url");
                map.remove("rank");
                map.remove("isClose");
                icon = StringUtil.null2Str(map.get("icon"));
                icon = BusinessUtil.disposeImagePath(icon);
                map.put("icon", icon);
                param.put("catalogId", StringUtil.null2Str(map.get("id")));
                List<Map<String,Object>> childServiceList = getServiceList(param,httpStr); // 根据二级分类id获取三级服务
                map.put("serviceList", childServiceList);

            }
            catalogOrServiceList.addAll(catalogList);
        }
        catalogOrServiceList.addAll(serviceList);
        return catalogOrServiceList;
    }

    public List<Map<String, Object>> getServiceList(Map<String, Object> param,String httpStr) throws Exception {
        List<Map<String,Object>> serviceList = customOrderDao.getServiceList(param); // 根据一级分类id获取二级服务
        if(serviceList!=null&&serviceList.size()>0){
            for(Map<String,Object> smap:serviceList){
                String sicon = StringUtil.null2Str(smap.get("icon"));
                sicon = BusinessUtil.disposeImagePath(sicon);
                smap.put("icon", sicon);
                String url = httpStr+"/customOrder/getOrderForm?serviceId="+smap.get("id");
                smap.put("url", url);
            }
        }
        return serviceList;
    }


    @Override
    public List<Map<String, Object>> getRecommedServiceList(String httpStr)
            throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        List<Map<String,Object>> list = (List<Map<String, Object>>) commonCacheService.getObject("getRecommedServiceList");
        if(list==null||list.isEmpty()||list.size()<1){
            list = customOrderDao.getRecommedServiceList(param);
            if(list!=null&&list.size()>0){
                String url = "";
                for(Map<String,Object> map:list){
                    url = httpStr+"/customOrder/getOrderForm?serviceId="+map.get("serviceId");
                    map.put("url", url);
                }
            }
            commonCacheService.setExpireForObject(list, 2*60, "getRecommedServiceList"); // 2分钟
        }
        return list;
    }

    //获取用户侧当前页订单处于待选择状态 ---对应的报价方案数量。
    private void addPlanCountForSendList(List<Map<String, Object>> resultList) {
        if (resultList!=null && resultList.size()>0){
            StringBuffer orderIds = new StringBuffer("");
            for(Map<String,Object> order:resultList){
                if (order.get("orderStatus").equals("2") || order.get("orderStatus").equals(2)){
                    orderIds.append(order.get("orderId")).append(",");
                }
            }
            //key-订单id  value--报价方案数量
            Map<String,Long> merchantPlanMaps = new HashMap<String,Long>();

            if(orderIds.length()>1){
                orderIds.deleteCharAt(orderIds.length()-1);
                Map<String,Object> param = new HashMap<String,Object>(1);
                param.put("orderIds",orderIds.toString());
                List<Map<String,Long>> merchantPlans = customOrderDao.getMerchantsPlan(param);
                for(Map<String,Long> plan:merchantPlans){
                    merchantPlanMaps.put(plan.get("order_id").toString(),plan.get("planNum"));
                }
            }

            for(Map<String,Object> order:resultList){
                if (merchantPlanMaps.containsKey(order.get("orderId"))){
                    order.put("planCount", merchantPlanMaps.get(order.get("orderId")));
                }else{
                    order.put("planCount", 0);
                }
            }
        }

    }

    //对于即将显示的用户侧订单列表，状态为1或2处理逾期时间及状态
    private void addSurplusTimeForSendList(List<Map<String, Object>> resultList,String version) throws Exception {
        Map<String,String> dictionary = new HashMap<String, String>();
        if(version != null && "V1110".equals(version)){//新旧版本共存
        	dictionary = getOrderStatusMap("senderOrderStatusMapV1110");
        }else{
        	dictionary = getOrderStatusMap("senderOrderStatusMap");
        }
        if (resultList!=null && resultList.size()>0){
            for(Map<String,Object> order:resultList){
                int orderStatus = StringUtil.nullToInteger(order.get("orderStatus"));
                if (orderStatus == 1 || orderStatus == 2){
                	//转化serviceTime为逾期时间
                	String overdue = this.convertServiceTime(order);
                    
                    long serviceTimeMicSec= DateUtil.getMicorSeconds(DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN,overdue);

                    if (serviceTimeMicSec==-1){
                        order.put("surplusTime", "");
                    }else{
                        String surPlusTime = this.computePastTimeV730(serviceTimeMicSec);
                        if (surPlusTime.equals("0分钟")){
                            order.put("surplusTime","");
                            order.put("orderStatus", "6");
                            order.put("orderStatusName", dictionary.get("6"));
                        }else{
                            order.put("surplusTime",this.computePastTimeV730(serviceTimeMicSec));
                        }
                    }

                }else{
                    order.put("surplusTime", "");
                }
                

            	String orderStatusText = "";
                //增加timeline的节点名称
                if(version != null && "V1110".equals(version)){//新旧版本共存
                	orderStatusText = getOrderStatusText(orderStatus, "searchOrderStatusV1110");
                    if(orderStatus == 1){//未响应，已推送xxx
                    	orderStatusText = orderStatusText.replace("$pushCount", order.get("pushCount").toString());
                    }else if(orderStatus == 2){//已响应，已有xxxx响应
                    	orderStatusText = orderStatusText.replace("$planCount", order.get("planCount").toString());
                    }
                    order.put("descp", orderStatusText);//命名与商户端查看统一
                }else{
                	orderStatusText = getOrderStatusText(orderStatus, "searchOrderStatus");
                	order.put("orderStatusText", getOrderStatusText(orderStatus, "searchOrderStatus"));
                }
            }
        }

    }


    @Override
    public JSONObject getPushMerchantInfos(Long orderId, int pageNo,Integer pageSize)
            throws Exception {
        JSONObject resultJsonObject = new ResultJSONObject("000", "获取推送的商户服务商列表成功");
        List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
        Integer totalRec = customOrderDao.getPushMerchantNum(orderId);
        Integer totalPages= (totalRec+Constant.PAGESIZE-1)/Constant.PAGESIZE;
        if (pageNo<0){
            pageNo=0;
        }
        if(pageNo<totalPages){
            Map<String,Object> params=new HashMap<String,Object>();
            params.put("orderId", orderId);
            params.put("startNum", pageNo);
            params.put("pageSize", pageSize);
            List<Map<String,Object>> merchantIdList=customOrderDao.getPushMerchantInfos(params);
            if (merchantIdList!=null && merchantIdList.size()>0){
                for(Map<String,Object> merchant:merchantIdList){
                    Map<String,Object> merchantInfo=myMerchantService.selectMerchantBasicInfo((Long)merchant.get("merchant_id"));

                    //移除不需要的信息
                    if (merchantInfo!=null && merchantInfo.size()>0){
                        merchantInfo.remove("province");
                        merchantInfo.remove("city");
                        merchantInfo.remove("locationAddress");
                        merchantInfo.remove("detailAddress");
                        merchantInfo.remove("microWebsiteUrl");
                        merchantInfo.remove("catalogId");
                        merchantInfo.remove("appType");
                        merchantInfo.remove("vipLevel");
                        merchantInfo.remove("detail");
                        merchantInfo.remove("phone");
                        resultList.add(merchantInfo);
                    }

                }
            }
        }
        resultJsonObject.put("totalNum", totalRec);
        resultJsonObject.put("totalPage", totalPages);
        resultJsonObject.put("merchantInfoList", resultList);
        return resultJsonObject;
    }

    public JSONObject getOrderMoreDetail(Long orderId){
        JSONObject resultJsonObject = new ResultJSONObject("000", "获取订单更多详情成功");
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        Object orderText=getOrderDetail(paramMap);

        Map<String, Object> orderNote = this.orderInfoService
                .getOrderNote(paramMap);
        BusinessUtil.disposeManyPath(orderNote, "picturePath", "voicePath");

        resultJsonObject.put("orderAttachment", orderNote);
        resultJsonObject.put("orderText", orderText);
        return resultJsonObject;
    }

    /**
     * 获取时间轴
     * @param orderId
     * @param orderStatus
     * @param type 1用户，2商户
     * @return
     */
    @Override
    public JSONObject getTimeline(Long merchantId,Long orderId, int orderStatus,int type){
        if(type!=1 && type!=2){
            return null;
        }

        String timelineName="";
        if(type==1){//用户
            timelineName="timeline_user";
        }
        if(type==2){//商户
            timelineName="timeline_merchant";
        } 

        String excludeCode="";
        List<Map<String,Object>> resultList=new ArrayList<Map<String,Object>>();
        if(orderStatus==1 || orderStatus==2 || orderStatus==3 || orderStatus==4 || orderStatus==5){
            excludeCode="200,300,210,310,230";
        }

        if(orderStatus==6 || orderStatus==7 || orderStatus==8 || orderStatus==9){//订单过期
        	
        	excludeCode="200,250,260,280,290,300,330,360";
            
        }

        Map<String,Object> paramMap=new HashMap<String, Object>();
        paramMap.put("orderId", orderId);
        paramMap.put("excludeCode", excludeCode);
        paramMap.put("timelineName", timelineName);
        List<Map<String,Object>> allLine=customOrderDao.getTimeLineText(paramMap);
        List<Map<String,Object>> getTimelineList=customOrderDao.getTimeline(paramMap);
        if(orderStatus==1 || orderStatus==2 || orderStatus==3 || orderStatus==4 || orderStatus==5){
            for(Map<String,Object> map : allLine){
                String code=StringUtil.null2Str(map.get("code"));
                int x=0;
                int y=0;
                for(Map<String,Object> map_ : getTimelineList){
                    String code_=StringUtil.null2Str(map_.get("actionCode"));
                    if(type==2 && code_.equals("300")){//商户 如果提供了报价方案，且报价方案不是自己的，则去除这一项
                        Long merchantId_=StringUtil.nullToLong(map_.get("merchantId"));
                        if(merchantId!=0 && merchantId_!=0 && !merchantId_.equals(merchantId)){
                            continue;
                        }
                    }

                    if(StringUtil.isNotEmpty(code) && StringUtil.isNotEmpty(code_) && code.equals(code_)){

                        if(type==1 && code_.equals("300")){//用户查看时间轴，显示第一条报价方案的时间
                            if(y>0){
                                continue;
                            }
                            y+=1;
                        }
                        map.put("actionTime", map_.get("actionTime"));
                        map.put("flag", 1);

                        x=1;
                    }
                }
                if(x==0){
                    map.put("flag", 0);
                }
                resultList.add(map);
            }

        }else{

            int y=0;
            for(Map<String,Object> map : getTimelineList){
                String code=StringUtil.null2Str(map.get("actionCode"));             
                
                if(type==2){//

                    Long merchantId_=StringUtil.nullToLong(map.get("merchantId"));
                    if(code.equals("210")){//用户选择报价方案
                        if(merchantId!=0 && merchantId_!=0 && !merchantId_.equals(merchantId)){
                            code="360";
                        }
                    }else{
                        if(merchantId!=0 && merchantId_!=0 && !merchantId_.equals(merchantId)){
                            continue;
                        }
                    }
                }else{
                	//用户查看时间轴，显示第一条报价方案的时间
                	if(code.equals("300")){
                        if(y>0){
                            continue;
                        }
                        y+=1;
                    }else if(code.equals("260")){
                		code="250";
                	}        	
                }

                int x=0;
                for(Map<String,Object> map_ : allLine){
                    String code_=StringUtil.null2Str(map_.get("code"));

                    if(StringUtil.isNotEmpty(code) && StringUtil.isNotEmpty(code_) && code.equals(code_)){
                    	
                        map.put("title", map_.get("title"));
                        map.put("remark", map_.get("remark"));
                        map.put("code", map_.get("code"));
                        map.put("flag", 1);
                        map.remove("actionCode");
                        map.remove("merchantId");
                        x=1;
                    }
                }
                if(x==0){
                    map.put("flag", 0);
                }
                resultList.add(map);
            }

        }
        JSONObject jsonObject = new ResultJSONObject("000", "查询时间轴数据成功");
        jsonObject.put("timeLineList", resultList);
        return jsonObject;
    }

    /**
     * 如果缓存存在，从缓存中获取对应的状态描述，否则从数据库加载到缓存中。
     * @param dictType
     * @param dictKey
     * @return
     */
    private  String getCachedOrderStatusDescribe(String dictType,String dictKey) {
        Object cachedDescribe=commonCacheService.hget(CacheConstants.ORDER_STATUS_DESCRIBE, dictType+"_"+dictKey);
        if (cachedDescribe==null){
            List<Map<String,Object>> statusDescribeList=customOrderDao.getOrderStatustTextList();
            if (statusDescribeList!=null && statusDescribeList.size()>0){
                orderStatusDesCacheService.cacheOrderStatusDescribes(statusDescribeList);
                cachedDescribe=commonCacheService.hget(CacheConstants.ORDER_STATUS_DESCRIBE, dictType+"_"+dictKey);
            }
        }
        return  (String) cachedDescribe;

    }


    @Override
    public Map<String, Object> getCustomOrderBanner(String serviceId)
            throws Exception {
        Map<String, Object> orderBanner = new HashMap<String, Object>();
        orderBanner = (Map<String, Object>) commonCacheService.getObject(
                CacheConstants.ORDER_BANNER, serviceId);
        if (orderBanner==null||orderBanner.isEmpty()) {
            // 缓存为空
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("serviceId", serviceId);
            orderBanner = customOrderDao.getOrderBanner(param);
            try {
                if (orderBanner != null && !orderBanner.isEmpty()) {
                    BusinessUtil.disposePath(orderBanner,"image");
                    commonCacheService.setObject(orderBanner,
                            CacheConstants.ORDER_BANNER, serviceId);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return orderBanner;
            }
        }

        return orderBanner;
    }


    private boolean needShowTopPushMerchat() {
        Integer showTopPushMerchantConfig=0;
        Object cachedShowTopPushMerchant= commonCacheService.getObject(CacheConstants.ORDER_SHOW_TOP_PUSHMERCHANT);
        if (cachedShowTopPushMerchant==null){

            Map<String,Object> configMap=orderInfoDao.getConfigByKey(CacheConstants.ORDER_SHOW_TOP_PUSHMERCHANT);
            showTopPushMerchantConfig = Integer.parseInt((String)configMap.get("config_value"));
            commonCacheService.setObject(showTopPushMerchantConfig,CacheConstants.ORDER_SHOW_TOP_PUSHMERCHANT);

        }else{
            showTopPushMerchantConfig=(Integer)cachedShowTopPushMerchant;
        }
        return showTopPushMerchantConfig==0?false:true;
    }

    @Override
    public List<Map<String, Object>> getCustomOrderFormByOrderId(
            String serviceId, String orderId) throws Exception {
        List<Map<String, Object>> orderForm = new ArrayList<Map<String, Object>>();
        orderForm = (List<Map<String, Object>>) commonCacheService.getObject(
                CacheConstants.ORDER_FORM, serviceId);
        Map<String, Object> param = new HashMap<String, Object>();
        if (orderForm==null||orderForm.isEmpty()) {
            // 缓存为空
            param.put("serviceId", serviceId);
            orderForm = customOrderDao.getOrderForm(param);
            try {
                if (orderForm != null && orderForm.size() > 0) {
                    for (Map<String, Object> map : orderForm) {
                        BusinessUtil.disposePath(map, "icon");
                        String colItems = StringUtil.null2Str(map
                                .get("colItems"));
                        List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
                        if (StringUtil.isNotEmpty(colItems)) {
                            String[] items = colItems.split(",");
                            if (items != null && items.length > 0) {
                                for (String item : items) {
                                    Map<String, Object> colMap = new HashMap<String, Object>();
                                    colMap.put("itemName", item);
                                    itemList.add(colMap);
                                }
                            }
                        }
                        map.put("colItems", itemList);
                        /**
                         * 控件联动
                         */
                    }

                    commonCacheService.setObject(orderForm,
                            CacheConstants.ORDER_FORM, serviceId);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return orderForm;
            }
        }

        if (orderForm != null && orderForm.size() > 0) {
            /// 查询订单详情
            Map<String,Object> detail = new HashMap<String,Object>();
            param.put("orderId", orderId);
            Map<String, Object> orderDetail = customOrderDao
                    .getOrderDetailByOrderId(param);
            if (orderDetail != null && !orderDetail.isEmpty()) {
                String jsonDetail = StringUtil.null2Str(orderDetail
                        .get("jsonDetail"));
                JSONArray arr= JSONObject.parseArray(jsonDetail);
                if (arr != null&&arr.size()>0) {
                    for(int i=0;i<arr.size();i++){
                        JSONObject jsonObject = arr.getJSONObject(i);
                        String value = StringUtil.null2Str(jsonObject.get("value"));
                        if(!StringUtil.isNullStr(value)){
                            detail.put(StringUtil.null2Str(jsonObject.get("colName")), value);
                        }
                    }
                }
            }
            for (Map<String, Object> map : orderForm) {
                String val = StringUtil.null2Str(detail.get(StringUtil.null2Str(map.get("colName"))));
                if(!StringUtil.isNullStr(val)){
                    map.put("defaultValue", detail.get(StringUtil.null2Str(map.get("colName"))));
                }
            }
        }

        return orderForm;
    }


    @Override
    public List<Map<String, Object>> thirdServiceList(String catalogId)
            throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("catalogId", StringUtil.null2Str(catalogId));
        List<Map<String,Object>> thirdServiceList = (List<Map<String, Object>>) commonCacheService.getObject("thirdServiceList",catalogId);
        if(thirdServiceList==null||thirdServiceList.isEmpty()||thirdServiceList.size()<1){
            List<Map<String,Object>> catalogList = customOrderDao.getCatalogList(param); // 根据一级分类id获取二级分类
            String icon = "";
            thirdServiceList = new ArrayList<Map<String,Object>>();
            if(catalogList!=null&&catalogList.size()>0){
                for(Map<String,Object> map:catalogList){
//					map.remove("bigIcon");
//					map.remove("flag");
//					map.remove("url");
//					map.remove("rank");
//					map.remove("isClose");
                    icon = StringUtil.null2Str(map.get("icon"));
                    icon = BusinessUtil.disposeImagePath(icon);
                    map.put("icon", icon);
                }
                thirdServiceList.addAll(catalogList);
            }

            if(thirdServiceList!=null&&thirdServiceList.size()>0){
                commonCacheService.setExpireForObject(thirdServiceList, 2*60, "thirdServiceList",catalogId); // 2分钟
            }
        }


        return thirdServiceList;
    }

    /**
     * 检查是否为黑名单用户
     *
     * @param userId 用户ID
     * @return true 是；false 不是
     */
    public boolean checkInBlacklist(Long userId) {
        Map<String, Object> blackListMap = null;
        // 先验证缓存中是否有黑名单的 Key，不存在就从数据库中查，然后放入缓存
        if (commonCacheService.isExistKey(CacheConstants.USER_BLACKLIST)) {
            blackListMap = (Map<String, Object>) commonCacheService.getObject(CacheConstants.USER_BLACKLIST);
        } else {
            List<Map<String, Object>> blackList = blackListDao.findAllBlackList();
            if (null != blackList && 0 < blackList.size()) {
                blackListMap = new HashMap<>();
                for (Map map : blackList) {
                    blackListMap.put(String.valueOf(map.get("user_id")), "1");
                }
                commonCacheService.setObject(blackListMap, CacheConstants.USER_BLACKLIST);
            }
        }
        return isInBlank(blackListMap, userId);
    }

    /**
     * 判断 userId 是否在 map 中
     *
     * @param blackMap 黑名单的Map
     * @param userId 用户ID
     * @return
     * @backup 可以使用通用的方法做判断
     */
    private boolean isInBlank(Map<String, Object> blackMap, Long userId) {
        if (null == blackMap) {
            return false;
        }
        for (Object entry : blackMap.keySet()) {
            if (userId == Long.parseLong(String.valueOf(entry))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Long getOrderIdByOrderNo(String orderNo){
        return customOrderDao.getOrderIdByOrderNo(orderNo);
    }


 

    /**
     * 处理未发送的MQ
     *
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void dealUnSentList() throws Exception {
        JedisLock jedisLock = userRelatedCacheServices.getDealUnSentMQExireLock();
        try {
            if (jedisLock.acquire()) {
                List<Map<String, Object>> unSentList = customOrderDao.getUnSentList();
                if (null != unSentList && 0 < unSentList.size()) {
                    for (Map<String, Object> map : unSentList) {
                        // 修改状态
                        customOrderDao.updateMQSendFailure(Long.parseLong(String.valueOf(map.get("id"))));

                        // 发送MQ消息
                        System.out.println("开始发送MQ：" + new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS").format(new Date()));
                        writeToMQ(String.valueOf(map.get("queueName")), String.valueOf(map.get("msg")));
                        System.out.println("结束发送MQ：" + new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS").format(new Date()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userRelatedCacheServices.releaseDealUnSentMQrExireLock(jedisLock);
        }
    }

    /**
     * 发送MQ
     *
     * @param mqExchange MQ exchange
     * @param msg 发送的消息
     * @throws Exception
     */
    public void writeToMQ(String mqExchange, String msg) throws Exception {
        pushService.writeToMQ(mqExchange, msg);
        // throw new NullPointerException();
    }

    /**
     * 插入MQ发送失败记录
     *
     * @param orderId 订单ID
     * @return
     * @throws Exception
     */
    public void insertMQSendFailure(String mqExchange, String msg, Long orderId) {
        Map<String, Object> params = new HashMap<>();
        params.put("business_id", orderId);
        params.put("queueName", mqExchange);
        params.put("msg", msg);
        params.put("status", 0); // 0无处理；1已处理
        params.put("type", 1);
        customOrderDao.insertMQSendFailure(params);
    }

    
    /**
     * 补充列表中每个待显示报价方案中商品信息
     * @param merchantPlans
     */
    private void getGoodsForMerchantPlans(
			List<Map<String, Object>> merchantPlans) {
    		if  (merchantPlans==null || merchantPlans.size()<1){
    			 return;
    		}
    		Map paramMap = new HashMap(1);
    		for (Map<String,Object> merchant:merchantPlans){
    			    Long planId=(Long) merchant.get("planId");
    				int totalGoods=merchantPlanDao.getTotalPlainGoods(planId);   				
    				List<Map<String,Object>> goodsInfo = merchantPlanDao.getGoodsInfoByPlan(planId);
    				if (goodsInfo!=null && goodsInfo.size()>0){
    					for(Map<String,Object> goods:goodsInfo){
    						BusinessUtil.disposeManyPath(goods, "goodsPictureUrl");
    					}
    				}
    				merchant.put("orderPlanGoodsList", goodsInfo);
    				//距离现在时间
    				merchant.put("timeDistance", DateUtil.showTime(DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, merchant.get("joinTime").toString())));
    				
    		}
	}
    
//    /**
//     * 修改没有写入MQ的记录
//     * @return
//     * @throws Exception
//     */
//    public JSONObject updateMQSendFailure(Long id) throws Exception {
//        JSONObject jsonObject = null;
//        try {
//            customOrderDao.updateMQSendFailure(id);
//            jsonObject = new ResultJSONObject("000", "没有写入MQ的记录修改成功");
//        } catch (Exception e) {
//            jsonObject = new ResultJSONObject("001", "没有写入MQ的记录修改失败：" + e);
//        }
//        return jsonObject;
//    }
    /**
     * 半个小时无报价方案的订单的订单
     * @return
     */
    @Override
    public  List<Map<String,Object>> getPushOrderList(){
    	BusinessUtil.writeLog("push",DateUtil.getNowTime()+"开始调用N分钟无报价方案推送私人助理定时器");    	
		
		JedisLock lock = commonCacheService.getLock(CacheConstants.PUSH_PRIVATEASSISTANT_LOCK, 10, 200);
		List<Map<String,Object>> getPushOrderList=new ArrayList<Map<String,Object>>();
		try{
			if(lock.acquire()){//获取锁				
		    	//从配置中获取多长时间无订单则需要推送私人助理
		    	Map<String, Object> configMap=commonService.getConfigurationInfoByKey("minutes_noPlanOrderToPush");
		        int minutes=configMap==null?30:StringUtil.nullToInteger(configMap.get("config_value"));
		    	BusinessUtil.writeLog("push",minutes+"分钟无报价方案，则推送私人助理");
		    	
		        //获取上次处理的最大的ID
		        Map<String,Object> lastPurifyRec=purifyDao.getLastPurify(purifyDao.NMINUTES_NOPLANORDER_TO_PUSH);
				Long lastMaxOrderId = -1L;
				if (lastPurifyRec==null){
					lastMaxOrderId=1L;
				}else{
					lastMaxOrderId = (Long) lastPurifyRec.get("maxOrderId");
				}
		    	BusinessUtil.writeLog("push","上次处理最大的orderId为："+lastMaxOrderId);
		    	
		    	//获取前minutes的时间
		    	String startTime=DateUtil.getDateTimeByMinuts(DateUtil.getNowTime(), -minutes);
		    	BusinessUtil.writeLog("push","临界时间为："+startTime);
		    	if(startTime==null || !DateUtil.isValidDate(startTime, "yyyy-MM-dd HH:mm:ss")){
		    		return null;
		    	}
		    	
		    	Map<String,Object> map=new HashMap<String,Object>();
		    	map.put("startTime", startTime);
		    	map.put("lastMaxOrderId", lastMaxOrderId);
		    	
		    	getPushOrderList=customOrderDao.getNoPlanOrderList(map);
		    	BusinessUtil.writeLog("push","本次需要处理的订单数为："+(getPushOrderList==null?0:getPushOrderList.size()));
		    	
		    	//本次处理最大的orderId为：
		    	if(getPushOrderList==null || getPushOrderList.size()==0){
		    		return null;
		    	}
		    	
		    	lastMaxOrderId=StringUtil.nullToLong(getPushOrderList.get(0).get("orderId"));
		        BusinessUtil.writeLog("push","本次处理最大的orderId为："+lastMaxOrderId+"\n");
		    	
		    	//将本次的最大ID保存到表中
				 Map<String,Object> purifyRec=new HashMap<String,Object>(2);
				 purifyRec.put("purifyType", purifyDao.NMINUTES_NOPLANORDER_TO_PUSH);
				 purifyRec.put("maxOrderId", lastMaxOrderId);
				 purifyDao.insertPurify(purifyRec);			 
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			commonCacheService.releaseLock(lock);
		}
		return getPushOrderList;
    }

    public JSONObject getMQ(String orderId, String tradeNo, int type) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        paramMap.put("orderId", orderId);
        Long userId = this.userOrderDao.getOrderUserId(paramMap);
        paramMap.put("userId", userId);

        Long merchantId = this.userOrderDao.getOrderMerchantId(paramMap);
        paramMap.put("merchantId", merchantId);

        Double orderPrice = this.userOrderDao.getOrderPrice(paramMap);
        paramMap.put("orderPrice", orderPrice);

        Double actualPrice = this.userOrderDao.getOrderActualPrice(paramMap);
        paramMap.put("actualPrice", actualPrice);

        Long serviceTypeId = userOrderDao.getOrderServiceTypeId(paramMap);
        String serviceTypeName = commonService.getServiceTypeName(StringUtil.null2Str(serviceTypeId));
        String orderNo = userOrderDao.getOrderNoByOrderId(paramMap);
        Map<String, Object> merchantMap = myMerchantService.selectMerchantBasicInfo(merchantId);
        Map<String, String> userMap = userRelatedCacheServices.getCachedUseInfo(StringUtil.null2Str(userId));
        String payUserPhone = "";
        if (null == userMap || 0 == userMap.size()) {
            payUserPhone = String.valueOf(orderInfoDao.selectOrderUserInfo(paramMap).get("userPhone"));
        } else {
            payUserPhone = userMap.get("phone");
        }
        Map<String, Object> payTimeMap = userOrderDao.getPaymentTime(paramMap);
        String payTime = String.valueOf(payTimeMap.get("payTime"));

        Map<String, Object> orderMap = userOrderDao.getOrderInfo(paramMap);

        Map<String, Object> bossInfo= merchantPlanDao.getBossIdByMerchant(Long.valueOf(merchantId));

        Map<String, Object> map = new HashMap<>();
        map.put("merchantId", merchantId); // 商户id
        map.put("merchantUserId", bossInfo.get("user_id")); // 商户userId
        map.put("merchantName", merchantMap.get("name")); // 商户名称
        map.put("userName", ""); // O盟用户名称
        map.put("merchantUserPhone", bossInfo.get("phone")); // 商户手机号
        map.put("orderId", orderNo); // 订单编号
        map.put("orderPayType", 2); // paramMap.get("orderPayType")，支付类型，1:微信支付 2：支付宝支付
        map.put("transSeq", orderId); // 交易流水号
        map.put("thirdTransSeq", tradeNo); // 第三方支付流水号
        map.put("payTime", payTime); // 支付时间
        map.put("orderTime", orderMap.get("joinTime")); // 订单生成时间
        map.put("orderPrice", orderPrice); // 订单金额
        map.put("transAmount", actualPrice); // 实际支付金额
        map.put("payUserhone", payUserPhone); // 支付用户手机号
        map.put("payUserId", userId); // 支付用户userId
        map.put("orderType", "1"); // 商户订单，固定传1
        map.put("serviceName", serviceTypeName); // 服务名称
        map.put("remark", "服务类型：" + serviceTypeName); // 交易备注
        System.out.println("支付宝发送的MQ消息：" + JSONObject.toJSONString(map));
        String msg = AESUtil.parseByte2HexStr(AESUtil.encrypt(JSONObject.toJSONString(map), DYNAMIC_KEY));
        jsonObject.put("msg", msg);

        return jsonObject;
    }
    
    /**
     * 修改商户营业类型
     */
    @SuppressWarnings({"unchecked" })
	@Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject updMerchantBusinessType(Map<String,Object> paramMap) throws Exception{
    	JSONObject jsonObject = new ResultJSONObject("000", "修改商户营业类型成功");
    	int i=customOrderDao.updMerchantBusinessType(paramMap);
    	if(i==0){
    		jsonObject = new ResultJSONObject("000", "修改商户营业类型失败");
    	}else{
        	Long merchantId=StringUtil.nullToLong(paramMap.get("merchantId"));
        	int businessType=StringUtil.nullToInteger(paramMap.get("businessType"));
    		//修改商户缓存
    		Map<String, Object> info = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
    		if(info!=null){
    			info.put("businessType", businessType);
    			// 更新缓存中的商铺信息(暂定一天未访问则移除缓存)
    			commonCacheService.setObject(info,CacheConstants.MERCHANT_BASIC_INFO_TIMEOUT,CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
    		}  
    	}
    	return jsonObject;
    }
    
    
    
    private List<Map<String,Object>> constructMerchantPlanFromMemory(
			List<Map<String, Object>> merchantPlans,int pageNo) {
        List<Map<String,Object>> result= new ArrayList<Map<String,Object>>();
    	
    	long  vipWeight = 9*10000*10000l;
        long  honestWeight = 8*1000*1000l;
        long  privateWeight = -7*1000*1000l;
    	
        long now = System.currentTimeMillis();
        
		if (merchantPlans!=null && merchantPlans.size()>0){
			for(Map<String,Object> merchantPlan:merchantPlans){
				long score=(now - ((Date)merchantPlan.get("origJoinTime")).getTime())/1000;
				//特权权重
				merchantPlan.put("vipStatus", -1);
				List<RuleConfig> rulConfigs=incService.getRuleConfig((Long) merchantPlan.get("merchantId"));
				if (rulConfigs!=null){
					if (rulConfigs.get(0).isVipMerchantOrder()){
						 merchantPlan.put("vipStatus", 2);
						 score = score+vipWeight;
					}else if (rulConfigs.get(0).isContractMerchantOrder()){
						 score = score+honestWeight;
					}else{
						//无排序特权
					}
				}
				
				//私人助理权重
				if (((Integer)merchantPlan.get("isPrivateAssistant"))==1){
					score = score + privateWeight;
				}
				
				merchantPlan.put("weight", score);
			}
		}
		
		//按权重排序
		TimeDescComparator cp = new TimeDescComparator("weight");
	    Collections.sort(merchantPlans, cp);
		
		//取需要显示的页
	    if (merchantPlans.size() < 1
                || pageNo * Constant.PAGESIZE > merchantPlans.size()) {
	    		//空结果集
        } else {
            for (int num = 0, startIndex = pageNo * Constant.PAGESIZE; num < Constant.PAGESIZE
                    && startIndex < merchantPlans.size(); num++, startIndex++) {
            		result.add(merchantPlans.get(startIndex));
            }
        }
	    return result;
     }
    
    /**
     * 检查预约时间是否精确到时，分
     * @param serviceId
     * @return
     */
    private boolean actualServiceTime(String serviceId){
    	 boolean result=false;
    	 List<Map<String, Object>> orderForm = getCustomOrderForm(serviceId);

         if (orderForm != null && !orderForm.isEmpty()) {
             for (Map<String, Object> map : orderForm) {
                 String parmKey = StringUtil.null2Str(map.get("colName")); // 下发表单的参数名
                 if (parmKey.equals("serviceTime")){
                	 	 result=map.get("showType").equals("2");
                	 	 break;
                 }
             }
         }
    	 return result;
    }
    
    /**订单serviceTime转化为逾期时间**/
    private String convertServiceTime(Map<String,Object> orderInfo){
        String serviceTime = StringUtil.null2Str(orderInfo.get("serviceTime"));
        String overdue = "";
        if(!serviceTime.equals("")){//根据serviceTime计算逾期时间overdue
        	//获取系统配置
        	Map<String, Object> immediateDateMap = commonService.getConfigurationInfoByKey(CacheConstants.EXPIRES_IMMEDIATE_DAY);
            Map<String, Object> noImmediateDateMap = commonService.getConfigurationInfoByKey(CacheConstants.EXPIRES_NO_IMMEDIATE_DAY);
            if(immediateDateMap == null || immediateDateMap.size() == 0 || noImmediateDateMap == null || noImmediateDateMap.size() == 0){
            	throw new ApplicationException("expires_immediate_day_exceprion","系统参数配置异常，请与管理员联系。");
            }
            int immediateDate = Integer.parseInt(immediateDateMap.get("config_value").toString());
            int noImmediateDate = Integer.parseInt(noImmediateDateMap.get("config_value").toString());
            
            //是否立即服务
        	int days = 0;
            String isImmediate = StringUtil.null2Str(orderInfo.get("isImmediate"));
            if("1".equals(isImmediate)){//立即服务，逾期时间=服务时间+7天
            	days = immediateDate;
            }else{//预约服务
            	//是否有商家抢单
                String orderStatus = StringUtil.null2Str(orderInfo.get("orderStatus"));
            	if("2".equals(orderStatus)){//有抢单，逾期时间=服务时间+3天
                	days = noImmediateDate;
            	}
            }
            overdue = DateUtil.getAfterDateN(serviceTime, days,DateUtil.DATE_TIME_YYYY_MM_DD_HH_MM_PATTERN);
            orderInfo.put("overdue", overdue); 
        }
        return overdue;
    }

    @Transactional(rollbackFor = Exception.class)
	@Override
    public JSONObject tobeConfirmed(Map<String, Object> params) throws Exception {
        JSONObject jsonObject = new ResultJSONObject("000","订单支付待确认记录插入成功");
        Integer returnValue = customOrderDao.findPaymentByTradeNo(params);
        
        if (returnValue==0){
        		//回调尚未到达，则插入
        		params.put("tradeNo", params.get("tradeNo"));
        		params.put("innerTradeNo", params.get("tradeNo"));
        		params.put("paymentStatus", 0);
        		params.put("consumeBizId", 0);
   
        		params.put("orderId", StringUtil.nullToLong(params.get("businessId")));
                Long userId = userOrderDao.getOrderUserId(params);
                
                double consumerPrice = StringUtil.nullToDouble(params.get("consumePrice"));
                if (consumerPrice > 0){       
                		Map<String,Object> kingParam = new HashMap<String,Object>();
                		kingParam.put("userId", userId);
                		kingParam.put("amount", params.get("consumePrice"));
                		kingParam.put("orderId", params.get("businessId"));
                		kingParam.put("orderType", 1);
                		Map<String,Object>  kingResult = kingService.userAssetAdvance(params);
                		if ("000".equals(kingResult.get("resultCode"))) {
                			params.put("consumeBizId", kingResult.get("recorderId"));
                		}
          
                }
        		customOrderDao.insertMerchantPaymentDetails(params);
        }else{
        	
        		//回调已生成记录，忽略
        	
        }
       
        return jsonObject;
    }

    private void wrapMQForWallet(Map<String, Object> map) throws Exception {
    	String nowTime=StringUtil.null2Str(map.get("nowTime"));
    	int orderPayType=StringUtil.nullToInteger(map.get("orderPayType"));
        Long orderId = StringUtil.nullToLong(map.get("orderId"));
        Long merchantId = userOrderDao.getOrderMerchantId(map);
        Map<String, Object> merchantMap = myMerchantService.selectMerchantBasicInfo(merchantId);
        String orderNo = userOrderDao.getOrderNoByOrderId(map);
        Map<String, Object> orderMap = userOrderDao.getOrderInfo(map);
        Double orderPrice = userOrderDao.getOrderPrice(map);
        Map<String, String> userMap = userRelatedCacheServices.getCachedUseInfo(StringUtil.null2Str(map.get("userId")));
        String payUserPhone = "";
        if (null == userMap || 0 == userMap.size()) {
            payUserPhone = String.valueOf(orderInfoDao.selectOrderUserInfo(map).get("userPhone"));
        } else {
            payUserPhone = userMap.get("phone");
        }
        Long serviceTypeId = userOrderDao.getOrderServiceTypeId(map);
        String serviceTypeName = commonService.getServiceTypeName(StringUtil.null2Str(serviceTypeId));
        Map<String, Object> walletMap=new HashMap<String, Object>();
        walletMap.put("merchantId", merchantId); // 商户id
        walletMap.put("merchantUserId",  StringUtil.nullToLong(map.get("userId"))); // 商户userId
        walletMap.put("merchantName", StringUtil.null2Str(merchantMap.get("name"))); // 商户名称
        walletMap.put("userName", ""); // O盟用户名称
        walletMap.put("merchantUserPhone", StringUtil.null2Str(payUserPhone)); // 商户手机号
        walletMap.put("orderId", StringUtil.null2Str(orderNo)); // 订单编号
        walletMap.put("orderPayType", StringUtil.nullToInteger(map.get("orderPayType"))); // 支付类型，1:微信支付 2：支付宝支付；9：纯消费金支付
        walletMap.put("transSeq", StringUtil.null2Str(orderId)); // 交易流水号
        walletMap.put("thirdTransSeq", StringUtil.null2Str((orderPayType==9) ? "" : map.get("orderPayType"))); // 如果是纯消费金支付，则第三方支付流水号为空
        walletMap.put("payTime", nowTime); // 支付时间
        walletMap.put("orderTime", StringUtil.null2Str(orderMap.get("joinTime"))); // 订单生成时间
        walletMap.put("orderPrice", StringUtil.nullToDouble(orderPrice)); // 订单金额
        walletMap.put("transAmount", StringUtil.nullToDouble((orderPayType==9) ? 0 : map.get("consumePrice"))); // 实际支付金额
        walletMap.put("payUserhone", StringUtil.null2Str(payUserPhone)); // 支付用户手机号
        walletMap.put("payUserId", StringUtil.nullToLong(map.get("userId"))); // 支付用户userId
        walletMap.put("orderType", 1); // 商户订单，固定传1
        walletMap.put("serviceName", StringUtil.null2Str(serviceTypeName)); // 服务名称
        walletMap.put("remark", "服务类型：" + StringUtil.null2Str(serviceTypeName)); // 交易备注
        if (orderPayType==9 || orderPayType==10) {
        	int flag=0;
        	if(orderPayType==10){
        		flag=1;
        	}
        	walletMap.put("consumerPayFlag", flag); // 是否消费金抵扣 0否；1是
        	walletMap.put("consumerAmount", StringUtil.nullToDouble(map.get("consumePrice"))); // 消费抵扣金
        }
        walletMap.put("consumerTransSeq", StringUtil.null2Str(map.get("consumerTransSeq"))); // 消费抵扣金流水号
        String unEncrypt = JSONObject.toJSONString(walletMap);
        System.out.println("生成的微信MQ消息：" + unEncrypt);
        BusinessUtil.writeLog("wallet_order_mq_success", "订单ID:" + orderId + "，消息体：" + unEncrypt);
        String msg = AESUtil.parseByte2HexStr(AESUtil.encrypt(unEncrypt, DYNAMIC_KEY));
        pushService.writeToMQ("opay.orderExchange", msg);
    }

    public JSONObject findPaymentByOrderId(Long orderId) {
        JSONObject jsonObject = new ResultJSONObject("000", "查询收支明细成功");
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        List<Map<String, Object>> paymentDetailList = customOrderDao.findPaymentByOrderId(params);
        jsonObject.put("paymentDetailList", paymentDetailList);
        return jsonObject;
    }

    @Transactional(rollbackFor = Exception.class)
    public JSONObject updatePaymentStatus(Map<String, Object> params) throws Exception {
        JSONObject jsonObject = new ResultJSONObject();
        Integer returnValue = 0;
        try {
            returnValue = customOrderDao.updatePaymentStatus(params);
            if (1 == returnValue) {
                jsonObject.put("000", "修改收支明细成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("999", "修改收支明细出现异常");
        }
        return jsonObject;
    }



    /**
	 * ｛商户订单确认服务完成｝
	 * 
	 * @param para
	 * @return
	 * @author Liuxingwen
	 * @throws Exception
	 * @created 2016年10月26日 上午10:50:22
	 * @lastModified
	 * @history
	 * //用户端订单的状态 1-新预约 2-待选择 3-已确认 4-已完成 5-支付完成 6-订单已过期 7-无效订单',
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject completionOrder(Map<String, Object> para) throws Exception {
		JSONObject jsonObject = null;
		String appType=StringUtil.null2Str(para.get("appType"));
		String shopName=StringUtil.null2Str(para.get("shopName"));
		Long orderId=StringUtil.nullToLong(para.get("orderId"));
		Long merchantId=StringUtil.nullToLong(para.get("merchantId"));
		Long merchantPlanId=StringUtil.nullToLong(para.get("merchantPlanId"));

		int orderStatus = 0;
		// 如果缓存存在，则根据缓存进行判断
		Map<String, Object> cachedOrderInfo = userRelatedCacheServices
				.getCachedUserOrder(orderId.toString());
		if (cachedOrderInfo != null) {
			orderStatus = StringUtil.nullToInteger(cachedOrderInfo
					.get("orderStatus"));
		}
		// 判断订单状态
		if (orderStatus == 0) {
			orderStatus = userOrderDao.selectOrderStatus(para);
		}
		if (orderStatus == 0 || orderStatus == 1) {
			jsonObject = new ResultJSONObject("002", "商户订单状态不符合直接进行订单确认！");
			return jsonObject;
		}
		if (orderStatus == 2) {
			//报价方案步骤 
			jsonObject = choosePricePlanV1110(appType, shopName, orderId,merchantId, merchantPlanId,false);
			String msgcodeString = jsonObject.getString("resultCode").trim();
			if (!"000".equals(msgcodeString)) {
				jsonObject = new ResultJSONObject("002", "商户订单状态不符合直接进行订单确认！");
				throw new Exception("002");
//				return jsonObject;
			}
		}
		//=======================================
		
		//=======================================
				Map<String, Object> resultMap = myMerchantService.selectpaymentStatus(para);
				if (null != resultMap) {
					//没有未确认的支付数据mpd.payment_status,
					//mpd.payment_price业务处理金额
					//,mpd.consume_price 消费抵用金
					jsonObject = new ResultJSONObject("002", "商户订单第三方支付状态没有确认完成，订单状态不符合直接进行订单确认");
					return jsonObject;
//					if((null ==resultMap.get("payment_price")
//							|| "0".equals(StringUtil.toString(resultMap.get("payment_price")))
//							||"".equals(StringUtil.toString(resultMap.get("payment_price"))))
//							&&
//							(null !=resultMap.get("consume_price")
//							&&!"".equals(StringUtil.toString(resultMap.get("consume_price")))
//							&&!"0".equals(StringUtil.toString(resultMap.get("consume_price")))
//							))
//							
//					{
//						//payment_status
//						//StringUtil.toString(resultMap.get("payment_price"))
//						if("0".equals(StringUtil.toString(resultMap.get("payment_status"))))
//						{
//						jsonObject = new ResultJSONObject("002", "商户订单第三方支付状态没有确认完成，订单状态不符合直接进行订单确认");
//						return jsonObject;
//						}
//					}
					
				}
				
//		Map<String, Object> resultMap = myMerchantService.selectpaymentStatus(para);
//		if (null != resultMap) {//没有未确认的支付数据
//			jsonObject = new ResultJSONObject("002", "商户订单第三方支付状态没有确认完成，订单状态不符合直接进行订单确认");
//			return jsonObject;
//		}

		
		para.put("orderStatus", 5);//1-新预约 2-待选择 3-已确认 4-已完成 5-支付完成 6-订单已过期 7-无效订单
		int update = customOrderDao.completionOrder(para);
		
		if (update <= 0) {
			jsonObject = new ResultJSONObject("003", "商户订单确认服务完成未成功，订单不存在");
			throw new Exception("003");
//			return jsonObject;
		}
		
		//更新push_merchant_order
		para.put("orderStatus", 5);
		update=orderInfoDao.updateMerchantOrderStatus(para);		
		// 查询所有报价方案，去除已选方案，merchantId是分区键，直接用 != 导致死锁 2016-10-31 CUIJIAJUN
		Map<String, Object> merchantParamMap = new HashMap<String, Object>();
		merchantParamMap.put("orderId", orderId);
		List<Map<String, Object>> pushInfos = this.userOrderDao.getOrderPushIds(merchantParamMap);
		for (Map<String, Object> pushInfo : pushInfos) {
			if (pushInfo.get("merchantId").toString().equals(merchantId.toString())) {
				pushInfos.remove(pushInfo);
				break;
			}
		}
		para.put("pushInfos", pushInfos);
		para.put("otherOrderStatus", 8);// 用户没选择此商户'1待提供,2已提供,3服务中,4待收款,5服务完成,6订单已过期，7无效订单（别人已抢单），8订单关闭（用户已取消）',		
		update +=orderInfoDao.updateOtherMerchantOrderStatus(para);
//         if (this.userOrderDao.checkMerchantUsers(para) > 0) {
//        	 update += this.userOrderDao.updateMerchantUsers(paramMap);
//         } else {
//        	 update += this.userOrderDao.insertMerchantUsers(paramMap);
//         }
        
		
		
		// 更新缓存
//		String orderId = String.valueOf(para.get("orderId")).trim();
		Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
		if (cachedOrders != null) {
			cachedOrders.put("orderStatus", 5);
//			cachedOrders.put("orderPayType", 1);
//			cachedOrders.put("dealTime", System.currentTimeMillis());
			Map<String, String> dictionary = getOrderStatusMap("senderOrderStatusMap");
			cachedOrders.put("orderStatusName",dictionary.get(cachedOrders.get("orderStatus") + ""));
			userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
		}
		// 更新商户侧缓存 ---Revoke 2016.6.6
		merchantId = this.userOrderDao.getOrderMerchantId(para);
        Map<String, Object> bossInfo = null;
        if (null != merchantId) {
            merchantCacheService.updateMerchantOrderCache(merchantId.toString(),orderId.toString(), "5");
            bossInfo = merchantPlanDao.getBossIdByMerchant(Long.valueOf(merchantId));
        }

        // 更新 收支明细 状态
        List<Map<String, Object>> paymentDetailList = customOrderDao.findPaymentByOrderId(para);
        Double paymentPrice = 0.0;
        Double consumePrice = 0.0;
        for (Map<String, Object> map : paymentDetailList) {
            // customOrderDao.updatePaymentStatus(map);
            paymentPrice += StringUtil.nullToDouble(map.get("paymentPrice"));
            consumePrice += StringUtil.nullToDouble(map.get("consumePrice"));
        }

        Long userId = userOrderDao.getOrderUserId(para);
        para.put("userId", userId);
        para.put("merchantId", merchantId);
        para.put("merchantUserId", null == bossInfo ? "" : bossInfo.get("user_id")); // 商户userId
        para.put("paymentTime", getNowYYYYMMDDHHMMSS()); // 订单支付时间
        para.put("orderPayType", 4); // 支付方式 1-支付宝支付 2-微信支付 3-现金支付 4-多次支付
        para.put("orderActualPrice", BigDecimal.valueOf(paymentPrice)); // 订单实际支付金额
        para.put("orderPrice", cachedOrders.get("orderPrice")); // 订单金额
        para.put("orderType", "1"); // 默认1商户订单，目前订单多种类型可区分
        para.put("remark", ""); // 备注
        para.put("consumePrice", BigDecimal.valueOf(consumePrice)); // 消费抵用金

        // 给 C_PLAN 发消息
        if (IS_SEND_MQ_TO_C_PLAN_AFTER_ORDER_PAYMENT_SUCCESS) {
            sendMQForCPLAN(para);
        }

		jsonObject = new ResultJSONObject("000", "商户订单确认服务完成成功");
		return jsonObject;
	}


    // 是否给C_PLAN发MQ消息
    private void sendMQForCPLAN(Map<String, Object> map) {
        try {
            pushService.writeToMQ("cplanOrderPayExchange", JSONObject.toJSONString(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
        BusinessUtil.writeLog("c_plan_multi_pay_mq_success", "订单ID:" + map.get("orderId") + "，消息体：" + JSONObject.toJSONString(map));
    }
	
	/*
	 * 根据订单状态判断是否符合条件
	 */
	public boolean screenOutByOrderStatus(int orderStatus,int orderStatus_){
		boolean bool=false;
		if(orderStatus==1 && orderStatus_==1){
			bool=true;
		}else if (orderStatus==2 && (orderStatus_==2 || orderStatus_==3 || orderStatus_==4)){
			bool=true;
		}else if (orderStatus==0 && orderStatus_>=5){
			bool=true;
		}
		return bool;
	}


	@Override
	public JSONObject getPayParm(Integer type, String outTradeNo,
			Double totalFee, Integer payType, String subject,
			Integer employeeNumber, String appType, Integer clientType,
			Integer pkgId, Long userId, String openId, Double consumePrice,
			String inviteCode) throws Exception {
		        JSONObject jsonObject=merchantPayService.getPayParm(type,outTradeNo, totalFee, payType,subject,employeeNumber,appType,clientType,pkgId,userId,openId,consumePrice,inviteCode);
		        return jsonObject;
	}
	/**
	 * 获取订单号
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public String getOrderNoByOrderId(Long orderId){
		 String orderNo=customOrderDao.getOrderNoByOrderId(orderId);
		 return orderNo;
		   
	}
	
	//订单详情查看报价方案
	public Map<String, Object> getOrderPlanFromOrderDetailPage(Map<String, Object> params){
    	//查询报价方案信息
    	Map<String,Object> orderPricePlan=customOrderDao.getOrderPlanBrief(params);
    	
		if(orderPricePlan!=null){   
            BusinessUtil.disposeManyPath(orderPricePlan, "paths");
            
            //查询报价方案商品信息
            Long planId=StringUtil.nullToLong(orderPricePlan.get("planId"));
            params.put("planId", planId);
            List<Map<String,Object>> orderPlanGoodsList=customOrderDao.getOrderPlanGoodsList(params);
            //地址加前缀
            BusinessUtil.disposeManyPath(orderPlanGoodsList, "goodsPictureUrl");
            orderPricePlan.put("orderPlanGoodsList", orderPlanGoodsList);           
        }
		return orderPricePlan;
	}


	// 购买王牌计划
    @Transactional(rollbackFor = Exception.class)
    public JSONObject buyKingPlan(Map<String, Object> params) throws Exception {
        JSONObject jsonObject = new ResultJSONObject();
        try {


            kingService.saveConfirmOrder(params);

            //发mq消息给C计划
            String innerTradeNo=StringUtil.nullToString(params.get("innerTradeNo"));
            Map<String,Object> orderMap=kingService.getKingOrderByInnerTradeNo(innerTradeNo);
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", StringUtil.nullToLong(orderMap.get("orderId")));
            map.put("userId", StringUtil.nullToLong(params.get("userId")));
            map.put("merchantId", 0);
            map.put("paymentTime", StringUtil.nullToLong(params.get("payTime"))); // 订单支付时间
            map.put("orderPayType", 7); // 支付方式 1-支付宝支付 2-微信支付 3-现金支付 4-多次支付7:王牌计划
            map.put("orderActualPrice", StringUtil.nullToDouble(params.get("btotalFee"))); // 订单实际支付金额
            map.put("orderPrice", StringUtil.nullToDouble(params.get("btotalFee"))); // 订单金额
            map.put("orderType", "1"); // 默认全1商户订单，目前订单多种类型可区分
            map.put("pkgId", StringUtil.nullToDouble(params.get("bizNo")));  //服务包ID
            map.put("pkgName", StringUtil.nullToLong(orderMap.get("pkgName"))); // //服务包名称
            map.put("serviceId", 4); //  //服务大类ID   2增值服务，3诚信服务商，4王牌计划，5联合计划
            map.put("serviceNumber", StringUtil.nullToString(params.get("inviteCode"))); // 备注
            String msg = JSONObject.toJSONString(map);
            System.out.println("开通王牌计划会员后给C_PLAN发MQ消息：" + msg);
            try {
                pushService.writeToMQ("cplan.incPkgOrderQueue", msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("购买王牌计划失败", e);
        }
        return jsonObject;
    }
    // 增值服务购买待确认
    @Transactional(rollbackFor = Exception.class)
    public JSONObject incServiceToBeConfirmed(Map<String, Object> params) throws Exception {
        int type=StringUtil.nullToInteger(params.get("type"));
        JSONObject jsonObject = new ResultJSONObject("000", "增值服务购买待确认成功");
        switch(type){
        	case 20:
                jsonObject=insertKingPlanToBeConfirmed(params);
        			break;
        	case 2:
        			jsonObject = myMerchantService.addEmployeeNumApplyNeedConfirm(params);
        			break;
        	case 3:
        			jsonObject =  incomeService.addGrapMoneyNeeConfirm(params);
        			break;
        	case 4:
        	case 5:
        	case 6:
        			jsonObject =  myMerchantService.addIncPackageNeedConfirm(params);
        			break;
        }
        return jsonObject;
    }

    @Override
    public JSONObject insertKingPlanToBeConfirmed(Map<String, Object> params) throws Exception {
        int payType=StringUtil.nullToInteger(params.get("paymentType"));
        params.put("payType", payType);
        String orderNo=StringUtil.nullToString(params.get("innerTradeNo"));
        params.put("orderNo", orderNo);
        int bizNo=StringUtil.nullToInteger(params.get("packageId"));
        params.put("bizNo", bizNo);
        double payAmount=StringUtil.nullToDouble(params.get("paymentAmount"));
        params.put("payAmount", payAmount);
        params.put("orderAmount", payAmount);
        String payTime=StringUtil.nullToString(params.get("paymentTime"));
        params.put("payTime", payTime);
        params.put("bizType", "KING_ACT");
        params.put("status", 1);
        //创建时间
        params.put("createTime", DateUtil.getNowYYYYMMDDHHMM());
        JSONObject jsonObject=kingService.insertPreConfirmOrder(params);


        return jsonObject;
    }
    
    @Override
    public int updateOrderActualPrice(Map<String, Object> paramMap){
    	return customOrderDao.updateOrderActualPrice(paramMap);
    	
    }


    /**
     * 纯消费金抵扣的支付，如果是老订单，则支付的同时，置订单的状态为完成；否则只插入状态为确认的支付记录。
     */
	@Override
	public JSONObject payBuyConsumerMoney(Map<String, Object> params)
			throws Exception {
		JSONObject result = new JSONObject();
		
		String appType=StringUtil.null2Str(params.get("appType"));
    	Long orderId=StringUtil.nullToLong(params.get("businessId"));
    	Long merchantId=StringUtil.nullToLong(params.get("merchantId"));
		String shopName = StringUtil.null2Str(params.get("shopName"));
		Long merchantPlanId = StringUtil.nullToLong(params.get("merchantPlanId"));
		
		
		Map<String, Object> cachedOrders = userRelatedCacheServices.getCachedUserOrder(orderId.toString());
	    if (cachedOrders != null  && cachedOrders.containsKey("orderStatus") && StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("5")) {
	    	   result.put("resultCode", "-1");
	           result.put("message", "订单重复支付");
	           return result;
	    }
	    
		params.put("tradeNo", params.get("tradeNo"));
		params.put("innerTradeNo", params.get("tradeNo"));
		params.put("paymentStatus", 1);
		params.put("consumeBizId", 0);
		params.put("orderId", StringUtil.nullToLong(params.get("businessId")));
		params.put("orderPayType", 9);
		params.put("consumerPayFlag", 0);
        params.put("paymentPrice", 0.0);   //第三方支付金额
		
     
        Double consumePrice = StringUtil.nullToDouble(params.get("consumePrice"));
        Long userId = userOrderDao.getOrderUserId(params);
                       
        Map<String,Object> kingParam = new HashMap<String,Object>();
    	kingParam.put("userId", userId);
    	kingParam.put("amount", consumePrice);
    	kingParam.put("orderId", params.get("orderId"));
    	kingParam.put("orderType", 1);
    	
            
         Map<String,Object> kingResult= kingService.userAssetPayment(kingParam);
            
        if ("000".equals(kingResult.get("resultCode"))) {
                    params.put("consumerTransSeq", kingResult.get("sourceTransSeq"));
                    params.put("consumeBizId", kingResult.get("recorderId"));
        }
            
        params.put("paymentTime", DateUtil.getNowTime());
        params.put("userId", userId);
        
        customOrderDao.insertMerchantPaymentDetails(params);
        
        params.put("nowTime",  DateUtil.getNowTime());
	    	    
	    if  (cachedOrders != null  && cachedOrders.containsKey("orderStatus") && StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("4")) {
	    		//老订单,设置订单状态为支付完成，并累计金额
	            cachedOrders.put("orderStatus", 5);
	            cachedOrders.put("orderPayType", 9);
	            cachedOrders.put("dealTime", System.currentTimeMillis());
	            Map<String,String> dictionary=getOrderStatusMap("senderOrderStatusMap");
	            cachedOrders.put("orderStatusName", dictionary.get(cachedOrders.get("orderStatus")+""));
	            userRelatedCacheServices.cacheUserOrderWithJson(cachedOrders.get("userId").toString(), cachedOrders);
	            
	            //修改商户订单状态
	            Map<String,Object> updateParam=new HashMap<String,Object>();
	            updateParam.put("orderStatus", 5);
	            updateParam.put("orderId", orderId);
	            updateParam.put("merchantId", merchantId);
	            
	            orderInfoDao.updateMerchantOrderStatus(updateParam);

	            merchantCacheService.updateMerchantOrderCache(merchantId.toString(), orderId.toString(), "5");
	            
	            userOrderDao.setOrderFinished(updateParam);
	            
	            
	        try{
	            updateParam.put("userId", userId);
	            updateParam.put("actualPrice", consumePrice);
	            updateParam.put("orderPrice", consumePrice);
	            this.userOrderDao.updateUserStatisticsService(updateParam);
	            this.userOrderDao.updateMerchantStatisticsService(updateParam);
	        }catch(Exception e){
	        	//统计出错，忽略继续
	        	logger.warn(e.getMessage(), e);
	        }
	            
	           
	            //推送，用户完成付款
	            Map<String,Object> pushParams= new HashMap<String,Object>();
	            Long receiveEmployeeUserId = this.customOrderDao.getReceiveEmployeeUserId(updateParam);
	            pushParams.put("userId", receiveEmployeeUserId);
	            pushParams.put("merchantId", merchantId);
	            pushParams.put("orderId", orderId);
	            pushParams.put("data", "");
	            pushParams.put("pushType",4);
	            pushService.asyncCommonPush(params);
	            
	            
	           
	            // 是否给C_PLAN发MQ消息
	            if (IS_SEND_MQ_TO_C_PLAN) {
	            	Map<String,Object> bossInfo= merchantPlanDao.getBossIdByMerchant(Long.valueOf(merchantId));
	            	Long serviceTypeId = userOrderDao.getOrderServiceTypeId(pushParams);
	            	String serviceTypeName = commonService.getServiceTypeName(StringUtil.null2Str(serviceTypeId));
	                Map<String, Object> map = new HashMap<>();
	                map.put("orderId", orderId);
	                map.put("userId", userId);
	                map.put("merchantId", merchantId);
	                map.put("merchantUserId", bossInfo.get("user_id")); // 商户userId
	                map.put("paymentTime", getNowYYYYMMDDHHMMSS()); // 订单支付时间
	                map.put("orderPayType", 9); // 支付方式 1-支付宝支付 2-微信支付 3-现金支付
	                map.put("orderActualPrice", consumePrice); // 订单实际支付金额
	                map.put("orderPrice", consumePrice); // 订单金额
	                map.put("orderType", "1"); // 默认全1商户订单，目前订单多种类型可区分
	                map.put("remark", serviceTypeName); // 备注
	                String msg = JSONObject.toJSONString(map);
	                writeToMQ("cplanOrderPayExchange", msg);
	            }   
	    }else if (cachedOrders != null  && cachedOrders.containsKey("orderStatus") && StringUtil.null2Str(cachedOrders.get("orderStatus")).equals("2")) {
	    		//待选择新订单首次支付
	    		JSONObject chooseResult = choosePricePlanV1110(appType, shopName, orderId, merchantId, merchantPlanId,true);
	    }
	       
        // 给钱包发MQ
         if ( IS_SEND_MQ_TO_WALLET) {
                     wrapMQForWallet(params);
         }

         result.put("resultCode", "000");
         result.put("message", "支付待确认接口保存成功");
       
        return result;	
	}

}
