package com.shanjin.dao;

import java.util.List;
import java.util.Map;

public interface ICommonDao {
	
	/** 根据用户id获取用户信息 **/
	public Map<String, Object> getBasicUserInfoByUid(Map<String, Object> paramMap);
	
	/** 根据员工id获取该员工所属的商铺信息 **/
	public Map<String, Object> getBasicMerchantInfoByUid(Map<String, Object> paramMap);

	/** 获取当前分享页面 **/
	public List<Map<String, Object>> getShareHtml();

	/** 获取静态活动页 **/
	public String getStaticActivityHtml(Map<String, Object> param);
	
	/** 获取活动列表 **/
	public List<Map<String, Object>> getActivityList(Map<String, Object> param);
	
	
	/** 获取重复提交配置表 **/
	public List<Map<String, Object>> getRestrictList();

	/** 获取活动分享平台**/
	public List<Map<String, Object>> getActivityPlateForm(long activity_id);
	
	/**获取交易明细导航列表***/
	public List<Map<String,Object>> getPaymentNaviList();
	
	/**
	 * 获取用户首页需求提示文字
	 * @return
	 */
	public String  getUserHomePageRequireTip();
	
	/**
	 * 获取用户首页上的订单信息
	 * @param userId
	 * @return
	 */
	public Map<String,Object> getUserHomePageGoods(Long userId);
	
	/**
	 * 获取运营配置的虚拟购买信息
	 * @return
	 */
	public List<String> getMockupBuyInfo();
	
	
	/**
	 * 获取用户开店数量
	 */
	public Integer getMerchantNumById(Long userId);

	/**
	 * 获取帮助页的分组
	 */
	public List<Map<String, Object>> getSystemHelpGroupList();

	/**
	 * 获取帮助页的分组的具体详情
	 */
	public List<Map<String, Object>> getSystemHelpGroupDetailList(Map<String, Object> map);

	/**
	 * 保存帮助反馈页点赞情况
	 */
	public void saveHelpEvaluation(Map<String, Object> param);
	
	/**
	 * 新增启动app信息
	 * @return
	 */
	public int addStartAppInfo(Map<String, Object> param);
	
	/**
	 * 获取私人助理的信息
	 * @param param
	 * @return
	 */
	public Map<String,Object>  getBasicUserInfoByAssistantId(Map<String,Object> param);
}
