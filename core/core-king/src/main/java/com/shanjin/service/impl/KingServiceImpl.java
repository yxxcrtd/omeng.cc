package com.shanjin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.common.util.TransSeq;
import com.shanjin.dao.*;
import com.shanjin.model.PersAssInfo;
import com.shanjin.model.king.KingActOrder;
import com.shanjin.model.king.KingAssetConsRecord;
import com.shanjin.model.king.KingMember;
import com.shanjin.model.king.KingUserAsset;
import com.shanjin.outServices.aliOss.AliOssUtil;
import com.shanjin.service.ICplanKingService;
import com.shanjin.service.IKingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/10/28
 * @desc 王牌会员接口实现
 */
@Service("kingServiceImpl")
public class KingServiceImpl implements IKingService, ApplicationContextAware {

    @Resource
    private KingUserAssetMapper kingUserAssetMapper;
    @Resource
    private KingAssetConsRecordMapper kingAssetConsRecordMapper;
    @Autowired
    private UserRelateMapper userRelateMapper;
    @Autowired
    private ActivityRelateMapper activityInfoMapper;
    @Autowired
    private KingMemberMapper kingMemberMapper;
    @Autowired
    private ICplanKingService cplanKingService;
    @Autowired
    private KingActOrderMapper actOrderMapper;

    private List<BizPayConfirmOperate> bizOperateList;

    private static final Logger logger = LoggerFactory.getLogger(KingServiceImpl.class);


    private static final String DETAIL_TABLE = "consume_pkg";
    private static final String MEMBER_STATUS = "member_status";
    private static final String KING_SHOW = "king_show";

    private static final String NO_SHOW_KING_LOGO = "1";
    private static final String LIGHT_SHOW_KING_LOGO = "2";
    private static final String GRAY_SHOW_KING_LOG = "3";
    private static final String NOT_MEMBER = "1";
    private static final String MEMBER_ONLINE = "2";
    private static final String MEMBER_EXPIRE = "3";
    private static final String MEMBER_CHECK = "4";//审核检查中
    /**
     * 预支付
     */
    private static final int RECORD_STATUS_ADVANCE = 1;
    /**
     * 预支成功
     */
    private static final int RECORD_STATUS_SUCCESS = 2;
    /**
     * 预支失败
     */
    private static final int RECORD_STATUS_FAILURE = 3;

    /**
     * 预支付确认成功
     */
    private static final int RECORD_CALLBACK_SUCCESS = 1;
    /**
     * 预支付确认失败
     */
    private static final int RECORD_CALLBACK_FAILURE = 0;

    /**
     * 支付类型：订单支付
     */
    private static final int RECORD_TYPE_ORDER = 1;


    @Override
    public Map<String, String> getKingLoginStatus(Long userId) {

        Map<String, String> map = new HashMap<>(2);
        //查看王牌会员信息
        KingMember member = kingMemberMapper.getEntityByKey(userId);
        if (null != member) {
            if (DateUtil.getNextDate(member.getEndTime()).compareTo(new Date()) > 0) {
                map.put(MEMBER_STATUS, MEMBER_ONLINE);
                map.put(KING_SHOW, LIGHT_SHOW_KING_LOGO);
            } else {
                map.put(MEMBER_STATUS, MEMBER_EXPIRE);
                map.put(KING_SHOW, NO_SHOW_KING_LOGO);
            }
        } else {
            int count = actOrderMapper.getConfirmOrderCount(userId);

            if (count > 0) {
                map.put(MEMBER_STATUS, MEMBER_CHECK);
                map.put(KING_SHOW, GRAY_SHOW_KING_LOG);
            } else {
                map.put(MEMBER_STATUS, NOT_MEMBER);
                boolean flag = true;
                //查看活动是否上线
                Map<String, Object> actInfoMap = activityInfoMapper.getActivityInfo(DETAIL_TABLE);
                if (actInfoMap != null && !actInfoMap.isEmpty()) {
                    //查看活动是否过期
                    int isPub = (int) actInfoMap.get("isPub");
                    Date etime = (Date) actInfoMap.get("etime");
                    //活动是否发布
                    if (1 == isPub) {
                        //活动是否过期
                        if (new Date().compareTo(etime) <= 0) {
                            flag = false;
                            map.put(KING_SHOW, GRAY_SHOW_KING_LOG);
                        }
                    }
                }
                if (flag) {
                    map.put(KING_SHOW, NO_SHOW_KING_LOGO);//活动过期或为进行 设置不显示
                }
            }

        }
        return map;
    }

    @Override
    public List<Map<String, Object>> getConsumePkgList() {
        List<Map<String, Object>> list = activityInfoMapper.getAllPkgList();
        if (!CollectionUtils.isEmpty(list)) {
            for (Map<String, Object> pkgMap : list) {
                String pkgType = (String) pkgMap.get("pkgType");
                String consValue = (String) pkgMap.get("consumeGoldType");
                pkgMap.put("title", pkgType + ",送" + consValue);
            }
        }
        return list;
    }


    @Override
    public JSONObject queryKingUserAsset(long userId) throws Exception {
        JSONObject jsonObject = new ResultJSONObject("000", "查询用户消费金信息成功");
        KingMember king = kingMemberMapper.getEntityByKey(userId);
        KingUserAsset userAsset = kingUserAssetMapper.getEntityByKey(userId);
        BigDecimal asset = BigDecimal.valueOf(0);
        BigDecimal availableAmount = BigDecimal.valueOf(0);
        int isKing = 1;
        if (null == king || null == userAsset) {
            jsonObject.put("userAsset", 0);
            jsonObject.put("availableAmount", 0);
            jsonObject.put("isKing", 0);
            return jsonObject;
        } else {
            isKing = 1;
            availableAmount = this.computeUserAvailableAmount(userAsset);
            asset = userAsset.getAssetAmount();
        }
        jsonObject.put("userAsset", asset);
        jsonObject.put("availableAmount", availableAmount);
        jsonObject.put("isKing", isKing);
        return jsonObject;
    }

    @Override
    public JSONObject queryUserAssetAmount(long userId) throws Exception {
        JSONObject jsonObject = new ResultJSONObject("000", "查询用户消费金月可用余额成功");
        KingMember king = kingMemberMapper.getEntityByKey(userId);
        KingUserAsset userAsset = kingUserAssetMapper.getEntityByKey(userId);
        BigDecimal availableAmount = BigDecimal.valueOf(0);
        int isKing = 1;
        if (null == king || null == userAsset) {
            jsonObject = new ResultJSONObject("000", "查询用户消费金月可用余额成功");
            jsonObject.put("availableAmount", 0);
            jsonObject.put("isKing", 0);
            return jsonObject;
        } else {
            isKing = 1;
            availableAmount = this.computeUserAvailableAmount(userAsset);
        }
        jsonObject.put("availableAmount", availableAmount);
        jsonObject.put("isKing", isKing);
        return jsonObject;
    }

    /**
     * 计算用户月可用余额
     *
     * @param asset
     * @return
     */
    public BigDecimal computeUserAvailableAmount(KingUserAsset asset) {
        BigDecimal assetAmount = asset.getAssetAmount();
        if (assetAmount.compareTo(BigDecimal.valueOf(0)) < 1) {
            return BigDecimal.valueOf(0);
        }
        BigDecimal limitedAmount = asset.getLimitedAmount();
        //比较余额和额度
        BigDecimal tmp = assetAmount.compareTo(limitedAmount) >= 0 ? limitedAmount : assetAmount;
        //查询该月 已使用金额 （成功+预扣）
        String begin = DateUtil.formatDate("yyyy-MM", new Date());
        begin = begin + "-01 00:00:00";
        List<Integer> statusList = new ArrayList<>();
        statusList.add(RECORD_STATUS_ADVANCE);
        statusList.add(RECORD_STATUS_SUCCESS);
        BigDecimal consAmount = queryConsAmount(asset.getUserId(), statusList, null, begin, null, null);
        return tmp.subtract(consAmount);
    }

    /**
     * 查询已消费金额
     *
     * @param userId
     * @param statusList
     * @param status
     * @param beginTime
     * @return
     */
    public BigDecimal queryConsAmount(Long userId, List<Integer> statusList, Integer status, String beginTime, Long orderId, Integer orderType) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", userId);
        param.put("beginTime", beginTime);
        param.put("statusList", statusList);
        param.put("stu", status);
        param.put("orderId", orderId);
        param.put("orderType", orderType);
        return kingAssetConsRecordMapper.queryConsAmount(param);
    }

    @Override
    public JSONObject queryKingUserAssetRecorderList(Map<String, Object> param)
            throws Exception {
        if (null == param.get("userId") || null == param.get("pageNo")) {
            return new ResultJSONObject("001", "重要参数不可为空！");
        }

        long userId = (Long) param.get("userId");
        JSONObject jsonObject = queryKingUserAsset(userId);
        param.put("status", RECORD_STATUS_SUCCESS);
        int total = kingAssetConsRecordMapper.queryKingUserAssetRecorderNum(param);
        int pageSize = null == param.get("pageSize") ? 12 : (Integer) param.get("pageSize");
        if (total > 0) {
            param.put("startNo", (Integer) param.get("pageNo") * pageSize);
            jsonObject.put("recorderList", this.kingAssetConsRecordMapper.queryKingUserAssetRecorderList(param));
        }
        jsonObject.put("totalPage", total % pageSize > 0 ? total / pageSize + 1 : total / pageSize);
        return jsonObject;
    }

    /**
     * 校验订单是否已经存在
     *
     * @param userId
     * @param orderId
     * @return
     */
    public Boolean checkOrder(Long userId, Long orderId, Integer orderType) {

        List<Integer> statusList = new ArrayList<>();
        statusList.add(RECORD_STATUS_ADVANCE);
        statusList.add(RECORD_STATUS_SUCCESS);
        BigDecimal orderAmount = this.queryConsAmount(userId, statusList, null, null, orderId, orderType);
        if (orderAmount.compareTo(BigDecimal.valueOf(0)) > 0) {
            return false;
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject userAssetPayment(Map<String, Object> param)
            throws Exception {
        logger.info("userAssetPayment:" + JSONObject.toJSONString(param));
        JSONObject jsonObject = new ResultJSONObject("000", "消费金支付成功！");
        Long userId = (Long) param.get("userId");
        Long orderId = (Long) param.get("orderId");
        Double amount = (Double) param.get("amount");
        Integer orderType = (Integer) param.get("orderType");
        if (null == userId || null == orderId || null == amount || null == orderType) {
            jsonObject = new ResultJSONObject("001", "重要参数不可为空！");
            logger.info("userAssetPayment:" + "param:" + param + "return:" + jsonObject.toJSONString());
            return jsonObject;
        }
        //查询账单是否已经存在 防止重复提交
//        if (!checkOrder(userId, orderId, orderType)) {
//            jsonObject = new ResultJSONObject("003", "扣款失败该账单已经存在！");
//            logger.info("userAssetPayment:" + "param:" + param + "return:" + jsonObject.toJSONString());
//            return jsonObject;
//        }
        //查询用户消费金账户信息
        KingUserAsset userAsset = kingUserAssetMapper.getEntityByKey(userId);
        if (null == userAsset) {
            jsonObject = new ResultJSONObject("002", "消费金余额不足！");
            logger.info("userAssetPayment:" + "param:" + param + "return:" + jsonObject.toJSONString());
            return jsonObject;
        }
        //可用余额
        BigDecimal availableAmount = this.computeUserAvailableAmount(userAsset);
        //校验可用余额
        //余额不足
        if (availableAmount.compareTo(BigDecimal.valueOf(amount)) < 0) {
            jsonObject = new ResultJSONObject("002", "消费金余额不足！");
            logger.info("userAssetPayment:" + "param:" + param + "return:" + jsonObject.toJSONString());
            return jsonObject;
        }
        Date creatTime = new Date();
        param.put("amount", BigDecimal.valueOf(amount));
        String sourceTransSeq = TransSeq.generateTransSeqNo(orderType);
        param.put("sourceTransSeq", sourceTransSeq);
        //添加扣款记录
        Long recorderId = addAssetRecorder(param, creatTime, RECORD_STATUS_SUCCESS);
        //修改账户可用余额
        modifyUserAsset(userId, BigDecimal.valueOf(-amount), BigDecimal.valueOf(0), creatTime);

        jsonObject.put("recorderId", recorderId);
        jsonObject.put("sourceTransSeq", sourceTransSeq);
        logger.info("userAssetPayment:" + "param:" + param + "return:" + jsonObject.toJSONString());
        return jsonObject;
    }

    //添加消费金使用记录
    @Transactional(rollbackFor = Exception.class)
    public long addAssetRecorder(Map<String, Object> param, Date creatTime, int Status) throws Exception {
        KingAssetConsRecord po = new KingAssetConsRecord();
        po.setBizNo(param.get("orderId").toString());
        po.setConsAmount((BigDecimal) param.get("amount"));
        po.setConsType((Integer) param.get("orderType"));
        po.setCreateTime(creatTime);
        po.setCreator(param.get("userId").toString());
        po.setSourceTransSeq(param.get("sourceTransSeq").toString());
        if (RECORD_STATUS_SUCCESS == Status) {
            po.setModifier(param.get("userId").toString());
            po.setModifyTime(creatTime);
        }
        po.setUserId((Long) param.get("userId"));
        po.setStatus(Status);
        this.kingAssetConsRecordMapper.saveEntity(po);
        return po.getId();
    }

    /**
     * 修改用户消费金余额
     *
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void modifyUserAsset(Long userId, BigDecimal asset, BigDecimal block, Date time) throws Exception {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", userId);
        param.put("modifier", userId);
        param.put("asset", asset);
        param.put("block", block);
        param.put("time", time);
        this.kingUserAssetMapper.modifyUserAsset(param);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject userAssetAdvance(Map<String, Object> param)
            throws Exception {
        logger.info("userAssetAdvance:" + JSONObject.toJSONString(param));
        JSONObject jsonObject = new ResultJSONObject("000", "消费金预付成功！");
        Long userId = (Long) param.get("userId");
        Long orderId = (Long) param.get("orderId");
        Double amount = (Double) param.get("amount");
        Integer orderType = (Integer) param.get("orderType");
        if (null == userId || null == orderId || null == amount || null == orderType) {
            jsonObject = new ResultJSONObject("001", "重要参数不可为空！");
            logger.info("userAssetAdvance:" + "param:" + param + "return:" + jsonObject.toJSONString());
            return jsonObject;
        }
        //查询账单是否已经存在 防止重复提交
//        if (!checkOrder(userId, orderId, orderType)) {
//            jsonObject = new ResultJSONObject("003", "预扣款失败该账单已经存在！");
//            logger.info("userAssetAdvance:" + "param:" + param + "return:" + jsonObject.toJSONString());
//            return jsonObject;
//        }
        //查询用户消费金账户信息
        KingUserAsset userAsset = kingUserAssetMapper.getEntityByKey(userId);
        if (null == userAsset) {
            jsonObject = new ResultJSONObject("002", "消费金余额不足！");
            logger.info("userAssetAdvance:" + "param:" + param + "return:" + jsonObject.toJSONString());
            return jsonObject;
        }
        //可用余额
        BigDecimal availableAmount = this.computeUserAvailableAmount(userAsset);
        //校验可用余额
        //余额不足
        if (availableAmount.compareTo(BigDecimal.valueOf(amount)) < 0) {
            jsonObject = new ResultJSONObject("002", "消费金余额不足！");
            logger.info("userAssetAdvance:" + "param:" + param + "return:" + jsonObject.toJSONString());
            return jsonObject;
        }
        Date creatTime = new Date();
        param.put("amount", BigDecimal.valueOf(amount));
        String sourceTransSeq = TransSeq.generateTransSeqNo(orderType);
        param.put("sourceTransSeq", sourceTransSeq);
        //添加扣款记录
        Long recorderId = addAssetRecorder(param, creatTime, RECORD_STATUS_ADVANCE);
        //修改账户可用余额
        modifyUserAsset(userId, BigDecimal.valueOf(-amount), BigDecimal.valueOf(amount), creatTime);
        jsonObject.put("recorderId", recorderId);
        jsonObject.put("sourceTransSeq", sourceTransSeq);
        logger.info("userAssetAdvance:" + "param:" + param + "return:" + jsonObject.toJSONString());
        return jsonObject;

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JSONObject userAssetPayCallBack(Map<String, Object> param)
            throws Exception {
        logger.info("userAssetPayCallBack:" + JSONObject.toJSONString(param));
        JSONObject jsonObject = new JSONObject();
        Long userId = (Long) param.get("userId");
        Long recorderId = (Long) param.get("recorderId");
        Integer status = (Integer) param.get("status");
        if (null == userId || null == recorderId || null == status) {
            jsonObject = new ResultJSONObject("001", "重要参数不可为空！");
            logger.info("userAssetPayCallBack:" + "param:" + param + "return:" + jsonObject.toJSONString());
            return jsonObject;
        }
        //查询账单是否存在
        KingAssetConsRecord op = this.kingAssetConsRecordMapper.getEntityByKey(recorderId);
        if (null == op || userId != op.getUserId()) {
            jsonObject = new ResultJSONObject("002", "消费金预扣记录不存在！");
            logger.info("userAssetPayCallBack:" + "param:" + param + "return:" + jsonObject.toJSONString());
            return jsonObject;
        }
        if (RECORD_STATUS_ADVANCE != op.getStatus()) {
            jsonObject = new ResultJSONObject("003", "该消费金预扣记录已经结算！");
            logger.info("userAssetPayCallBack:" + "param:" + param + "return:" + jsonObject.toJSONString());
            return jsonObject;
        }
        //修改消费金预扣记录
        Date time = new Date();
        status = status == RECORD_CALLBACK_SUCCESS ? RECORD_STATUS_SUCCESS : RECORD_STATUS_FAILURE;
        op.setStatus(status);
        op.setModifier(userId.toString());
        op.setModifyTime(time);
        this.kingAssetConsRecordMapper.updateEntity(op);
        jsonObject.put("amount", op.getConsAmount());
        jsonObject.put("recorderId", recorderId);
        jsonObject.put("sourceTransSeq", op.getSourceTransSeq());
        //修改账户可用余额
        if (status == RECORD_STATUS_SUCCESS) {
            this.modifyUserAsset(userId, BigDecimal.valueOf(0), BigDecimal.valueOf(-1).multiply(op.getConsAmount()), time);
            jsonObject.put("resultCode", "000");
            jsonObject.put("message", "消费金扣款成功！");

        } else {
            this.modifyUserAsset(userId, op.getConsAmount(), BigDecimal.valueOf(-1).multiply(op.getConsAmount()), time);
            jsonObject.put("resultCode", "000");
            jsonObject.put("message", "消费金退款款成功！");
        }
        logger.info("userAssetPayCallBack:" + "param:" + param + "return:" + jsonObject.toJSONString());
        return jsonObject;
    }

    @Override
    public Map<String, Object> getUserInfo(Long userId) {
        Map<String, Object> userMap = userRelateMapper.getUserInfoById(userId);
        if (null == userMap || userMap.isEmpty()) {
            logger.error("无法获取指定用户信息,userId={}", userId);
            return null;
        }
        String image = (String) userMap.get("image");
        image = StringUtil.isEmpty(image) ? "" : AliOssUtil.getViewUrl(image);
        userMap.put("image", image);
        return userMap;
    }

    @Override
    public JSONObject queryKingInfo(Long userId) {
        if (null == userId) {
            return new ResultJSONObject("001", "用户id不可为空！");
        }

        //查询用户消费金账户信息
        KingMember king = kingMemberMapper.getEntityByKey(userId);

        JSONObject jsonObject = new ResultJSONObject("000", "王牌计划购买成功展示王牌会员私人助理信息及消费金信息成功！");
        if (null == king) {
            jsonObject.put("isKing", 0);
            return jsonObject;
        } else {
            jsonObject.put("isKing", 1);
            //计算剩余天数
            jsonObject.put("startDate", DateUtil.formatDate("yyyy.MM.dd", king.getStartTime()));
            jsonObject.put("endDate", DateUtil.formatDate("yyyy.MM.dd", king.getEndTime()));
            //余额
            jsonObject.put("userAsset", king.getConsGoldDeno());
        }

        PersAssInfo info = this.cplanKingService.getPersAssInfoByUserId(userId);
        Map<String, Object> map = null;
        if (null != info) {
            map = new HashMap<String, Object>();
            map.put("persAssName", info.getName());
            map.put("persAssPhone", info.getPhone());
        }
        jsonObject.put("persAssInfo", map);
        return jsonObject;
    }

    @Override
    public Map<String, Object> shareAct(Long actId) {
        Map<String, Object> map = activityInfoMapper.getActivityShare(actId);
        if (map != null) {
            String shareImage = (String) map.get("shareImage");
            if (!StringUtil.isEmpty(shareImage)) {
                shareImage = BusinessUtil.disposeImagePath(shareImage);
                map.put("shareImage", shareImage);
            }
        }
        return map;
    }

    @Override
    public Map<String, Object> getKingOrderByInnerTradeNo(String innerTradeNo) {
        return actOrderMapper.selectKingOrderByInnerTradeNo(innerTradeNo);
    }


    @Override
    public Map<Long, Boolean> confirmUsersKingIdentity(List<Long> userIds) {
        Map<Long, Boolean> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(userIds)) {
            for (Long userId : userIds) {
                map.put(userId, false);
            }
            List<KingMember> list = kingMemberMapper.findInByKeyIds(userIds);
            if (!CollectionUtils.isEmpty(list)) {
                for (KingMember member : list) {
                    map.put(member.getUserId(), true);
                }
            }
        }
        return map;
    }

    @Override
    public Map<String, Object> getConsumePkgDetail(Long pkgId) {
        return activityInfoMapper.getPkgDetail(pkgId);
    }

    @Override
    public JSONObject insertPreConfirmOrder(Map<String, Object> orderMap) {

        KingActOrder order = map2Order(orderMap);
        int count = actOrderMapper.atomInsertOrder(order);
        if (count == 0) {
            logger.warn("新增待确认订单已存在，order:{}", order);
            return new ResultJSONObject("001", "指定的订单号已存在，订单编号：" + order.getOrderNo());
        }
        return new ResultJSONObject("000", "");
    }

    @Override
    public JSONObject saveConfirmOrder(Map<String, Object> map) {
        KingActOrder order = map2Order(map);
        int count = actOrderMapper.atomUpdateConfirmOrder(order);
        if (count == 0) {
            count = actOrderMapper.atomInsertOrder(order);
            if (count == 0) {
                count = actOrderMapper.atomUpdateConfirmOrder(order);
            }
        }

        if (count == 0) {
            logger.error("支付回调接口回写订单结果异常");
            return new ResultJSONObject("001", "回调保存订单异常");
        }
        int status = order.getStatus();
        if (2 == status) {
            logger.info("支付确认状态为2成功状态，进行业务处理");
            if (bizOperateList != null && !bizOperateList.isEmpty()) {
                try {
                    for (BizPayConfirmOperate<KingActOrder> bizPayConfirmOperate : bizOperateList) {
                        bizPayConfirmOperate.operateBiz(order.getBizType(), order);
                    }
                } catch (Exception e) {
                    logger.error("支付确认业务处理异常", e);
                    return new ResultJSONObject("002", "支付确认业务处理异常;" + e.getMessage());
                }
            }
        }

        return new ResultJSONObject("000", "");
    }

    @Override
    public JSONObject checkKingAssetConsRecord(int hour) throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("method", "定时处理预扣记录");
        Date checkTime = new Date(new Date().getTime() - hour * 60 * 60 * 1000);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("checkTime", checkTime);
        jo.put("checkTime", checkTime);
        param.put("status", RECORD_STATUS_ADVANCE);
        //1订单支付
        param.put("consType", RECORD_TYPE_ORDER);
        List<KingAssetConsRecord> orderList = this.kingAssetConsRecordMapper.queryKingUserAssetRecorderParam(param);
        int orderNu = 0;
        int success = 0;
        int failure = 0;
        int advance = 0;
        if (null != orderList && 0 != orderList.size()) {
            orderNu = orderList.size();

        }
        jo.put("待结算订单数量", orderNu);
        jo.put("支付成功", success);
        jo.put("支付失败", failure);
        jo.put("待结算订单数量", advance);
        //
        return null;
    }

    @Override
    public JSONObject queryKingUserAssetInfo(Long userId) {
        if (null == userId) {
            return new ResultJSONObject("001", "用户id不可为空！");
        }

        //查询用户消费金账户信息
        KingMember king = kingMemberMapper.getEntityByKey(userId);
        KingUserAsset userAsset = kingUserAssetMapper.getEntityByKey(userId);

        JSONObject jsonObject = new ResultJSONObject("000", "王牌会员私人助理信息及消费金信息成功！");
        if (null == king || null == userAsset) {
            jsonObject.put("isKing", 0);
            return jsonObject;
        } else {
            jsonObject.put("isKing", 1);
            //计算剩余天数
            int remainingDate = DateUtil.daysBetweenDate(king.getEndTime(), new Date());
            jsonObject.put("remainingDate", remainingDate >= 0 ? remainingDate : 0);
            //余额
            jsonObject.put("userAsset", userAsset.getAssetAmount());
        }

        PersAssInfo info = this.cplanKingService.getPersAssInfoByUserId(userId);
        Map<String, Object> map = null;
        if (null != info) {
            map = new HashMap<String, Object>();
            map.put("persAssName", info.getName());
            map.put("persAssPhone", info.getPhone());
        }
        jsonObject.put("persAssInfo", map);
        return jsonObject;
    }


    private KingActOrder map2Order(Map<String, Object> orderMap) {
        KingActOrder order = new KingActOrder();
        order.setUserId((Long) orderMap.get("userId"));
        order.setOrderNo((String) orderMap.get("orderNo"));
        order.setBizNo((String) orderMap.get("bizNo"));
        order.setInviteCode((String) orderMap.get("inviteCode"));
        order.setOrderAmount((BigDecimal) orderMap.get("orderAmount"));
        order.setPayAmount((BigDecimal) orderMap.get("payAmount"));
        order.setPayType((Integer) orderMap.get("payType"));
        order.setCreateTime((Date) orderMap.get("createTime"));
        order.setModifyTime((Date) orderMap.get("modifyTime"));
        order.setCreator((String) orderMap.get("creator"));
        order.setModifier((String) orderMap.get("modifier"));
        order.setBizType((String) orderMap.get("bizType"));
        order.setName((String) orderMap.get("name"));
        order.setStatus((Integer) orderMap.get("status"));//预下单 待确认
        order.setPayTime((Date) orderMap.get("payTime"));
        order.setRemark((String) orderMap.get("remark"));
        if("KING_ACT_MERCHANT".equals(order.getBizType())){
            order.setMerchantId((Long) orderMap.get("merchantId"));
        }
        return order;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, BizPayConfirmOperate> map = applicationContext.getBeansOfType(BizPayConfirmOperate.class);
        StringBuilder sb = new StringBuilder("初始化支付确认业务操作: ");
        if (map != null && !map.isEmpty()) {
            bizOperateList = new ArrayList<>(5);
            for (Map.Entry<String, BizPayConfirmOperate> entry : map.entrySet()) {
                bizOperateList.add(entry.getValue());
                sb.append(entry.getKey() + ",");
            }
            sb.setLength(sb.length() - 1);
        }
        logger.info("初始化IkingService.bizOperateList  字段;{}", sb.toString());
    }

	@Override
	public JSONObject queryUserKingMemberInfo(Long userId) throws Exception{
		JSONObject jsonObject = new ResultJSONObject("000", "查询用户王牌会员信息成功！");
		//查询用户消费金账户信息
        KingMember king = kingMemberMapper.getEntityByKey(userId);
        KingUserAsset userAsset = kingUserAssetMapper.getEntityByKey(userId);
        if (null == king || null == userAsset) {
            jsonObject.put("isKing", 0);
            return jsonObject;
        } else {
            jsonObject.put("isKing", 1);
            //计算剩余天数
            jsonObject.put("consGoldDeno", king.getConsGoldDeno());
            //余额
            jsonObject.put("limitedAmount", userAsset.getLimitedAmount());
        }
		return jsonObject;
	}

	@Override
	public JSONObject h5Exit(String openId, Long userId) throws Exception {
		logger.info("h5Exit:openId:"+openId+"userId:"+userId);
		if (null == userId || StringUtil.isEmpty(openId)) {
            return new ResultJSONObject("001", "重要参数不可为空！");
        }
		//根据opend,userId 删除 wechat_user 关联 userId 记录
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("openId", openId);
		param.put("userId", userId);
		int num = this.userRelateMapper.delWechatUser(param);
		JSONObject jsonObject = new ResultJSONObject("000", "快捷支付h5登出成功！");
		
		return jsonObject;
	}
}
