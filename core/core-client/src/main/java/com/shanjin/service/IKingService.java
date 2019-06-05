package com.shanjin.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/10/28
 * @desc 王牌活动service
 */
public interface IKingService {

    /**
     * 获取王牌会员登录相关状态
     * @param userId
     * @return
     * 会员标识 member_status
     * 1：非会员
     * 2：未过期会员
     * 3：过期会员
     * 铭牌显示 king_show
     * 1: 显灰色
     * 2：高亮显示状态
     * 3：不显示
     */
   Map<String,String> getKingLoginStatus(Long userId);


    //添加获取王牌会员套餐信息列表
	List<Map<String,Object>> getConsumePkgList();



	/**
	 * 查询用户消费金信息：余额，月可用余额
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONObject queryKingUserAsset(long userId) throws Exception;

	/**
	 * 查询用户消费金月可用余额
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public JSONObject queryUserAssetAmount(long userId) throws Exception;

	/**
	 * 用户消费金明细
	 */
	public JSONObject queryKingUserAssetRecorderList(Map<String, Object> param) throws Exception;

	/**
	 * 消费金全额支付
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject userAssetPayment(Map<String, Object> param)throws Exception;

	/**
	 * 消费金预支付
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject userAssetAdvance(Map<String, Object> param)throws Exception;
	
	/**
	 * 消费金支付反馈
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JSONObject userAssetPayCallBack(Map<String, Object> param)throws Exception;

	/**
	 * 获取用户信息
	 * @param userId
     */
	Map<String,Object> getUserInfo(Long userId);
	
	/**
	 * 王牌会员私人助理信息及消费金信息
	 * @param userId
	 * @return
	 */
	public JSONObject  queryKingUserAssetInfo(Long userId);

	/**
	 * 查询用户列表是否为王牌会员
	 * @param userIds
	 * @return
     */
	Map<Long,Boolean> confirmUsersKingIdentity(List<Long> userIds);


	/**
	 * 获取王牌活动服务包详情信息
	 * @param pkgId
	 * @return
     */
	Map<String,Object> getConsumePkgDetail(Long pkgId);


	/**
	 * 插入待确认订单
	 * @param orderMap
     */
	JSONObject insertPreConfirmOrder(Map<String,Object> orderMap);

	/**
	 * 保存确认订单
	 * @param orderMap
     */
	JSONObject saveConfirmOrder(Map<String,Object> orderMap);


	/**
	 * 定时处理预扣记录
	 * @param hour 小时数
	 * @return
	 * @throws Exception
	 */
	public JSONObject checkKingAssetConsRecord(int hour)throws Exception;
	
	/**
	 * 王牌计划购买成功展示王牌会员私人助理信息及消费金信息
	 * @param userId
	 * @return
	 */
	public JSONObject  queryKingInfo(Long userId);


	/**
	 *活动分享
	 * @param actId
	 * @return
     */
	Map<String,Object> shareAct(Long actId);

    /**
     *查询订单id和名称
     * @param innerTradeNo
     * @return
     */
    Map<String,Object> getKingOrderByInnerTradeNo(String innerTradeNo);
	
	/**
	 * 查询用户王牌会员信息
	 * @param userId
	 * @return
	 */
	public JSONObject  queryUserKingMemberInfo(Long userId)throws Exception;
	
	/**
	 * 快捷支付h5登出
	 * @param openId
	 * @param userId
	 * @return
	 */
	public JSONObject h5Exit(String openId,Long userId )throws Exception;
	
}
