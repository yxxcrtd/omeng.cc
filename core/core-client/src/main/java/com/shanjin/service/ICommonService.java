package com.shanjin.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

public interface ICommonService {

	/** 获得APP启动页 */
	public JSONObject checkLoading(String clientType) throws Exception;

	/** 检查是否有更新 */
	public JSONObject checkUpdate(String packageName, int version) throws Exception;

	/** 检查是否为黑名单（手机）TODO 没有用到 */
	public JSONObject checkBlackList(String phone, String appType) throws Exception;

	/** 获取所有app信息，包括appType、appName、image等 */
	public JSONObject getAllAppInfo() throws Exception;

	/** 获取service信息，包括serviceType、serviceName、image、appType等 */
	public JSONObject getServiceInfo(String appType, Long parentId) throws Exception;

	/** 多级服务项目一切性查询（递归形式），获取service信息，包括serviceType、serviceName、image、appType等 */
	public JSONObject getServiceInfoMultilevel(String appType, String alias) throws Exception;

	/** 艾秘书服务列表 */
	public JSONObject getAmsServiceInfo(String appType, Boolean needLoadChildren) throws Exception;

	/** 获取城市信息 */
	public JSONObject getCity(Long parentId) throws Exception;

	/** 获取地区信息列表，供移动端一次性选择城市 */
	public JSONObject getAreaList() throws Exception;

	/** 获取地区信息列表，供移动端一次性选择城市 */
	public JSONObject getAllCitys() throws Exception;

	/** 获取轮播图列表 */
	public List<Map<String, Object>> getSliderPics(String appType) throws Exception;

	public String helloWorld(String helloStr);

	/** 查看顾客评价 */
	public JSONObject selectUserEvaluation(String appType, Long merchantId, Long orderId, int pageNo) throws Exception;

	/** 获取服务类型map */
	Map<String, String> getServiceInfoMap(String appType, Long parentId);

	/**
	 * 获取红妆所有的二级列表
	 * 
	 * @param childDictType
	 */
	public JSONObject gethzDictInfo(String dictType, String childDictType) throws Exception;

	public JSONObject iosPushTest(String appType);

	/**
	 * 生成验证码
	 * 
	 * @throws Exception
	 */
	public JSONObject createVerificationCode(Long merchantId, String phone) throws Exception;

	/** 保存验证码的拦截器的缓存 */
	public boolean saveVerificationCodeCache(Object obj, int seconds, String key, String... attachedKey) throws Exception;

	/** 获得验证码的拦截器的缓存 */
	public Object getVerificationCodeCache(String key, String... attachedKey) throws Exception;

	/** 清除验证码的拦截器的缓存 */
	public boolean cleanVerificationCodeCache(String key, String... attachedKey) throws Exception;

	/**
	 * 临时测试使用-将接口日志信息插入到数据库
	 */
	public void saveSystemLog(Map<String, Object> param);

	/** 根据appType查询推送信息 **/
	public Map<String, Object> getPushInfo(String appType);

	/**
	 * 查找所有推送对象
	 */
	public List<Map<String, Object>> getAllMerchant(Map<String, Object> paramMap);

	/**
	 * 根据城市查找推送对象
	 */
	public List<Map<String, Object>> getAllMerchantByCity(Map<String, Object> paramMap);

	/**
	 * 根据服务标签查找推送对象
	 */
	public List<Map<String, Object>> getMerchantClientIdsByServiceTag(Map<String, Object> paramMap);

	/**
	 * 根据经纬度查找推送对象
	 */
	public List<Map<String, Object>> getAllMerchantByRange(Map<String, Object> paramMap);

	/**
	 * 根据经纬度查找推送对象
	 */
	public List<Map<String, Object>> getOnlineMerchant(List<Map<String, Object>> allMerchantList);

	/** 获取推荐的标签 */
	public JSONObject selectRecommendServiceTag() throws Exception;

	/**
	 * 获取配置信息
	 * 
	 * @return
	 * @throws
	 */
	public Map<String, Object> getConfigurationInfoByKey(String key);
	
	
	
	/**
	 * 根据appTypes 获取行业信息
	 * @param appTypes
	 * @return
	 */
	public List<Map<String,Object>> getAppInfosByAppType(String appTypes);
	
	

	/** 根据关键词搜索APP */
	public JSONObject serachAppType(String keyword) throws Exception;

	/**
	 * 从serviceType列表中获取某个serviceTypeId对应的名称
	 * 
	 * @param serviceTypeId
	 * @param appType
	 * @return String
	 * @throws
	 */
	//public Map<String, Object> getServiceType(String serviceTypeId);

	public String getServiceTypeName(String serviceTypeId);
	
	public String getServiceTypeName(String serviceTypeId, String appType);

	/**
	 * 查找订单服务类型
	 * 
	 * @param paramMap
	 * @return
	 * @throws
	 */
	public Long selectOrderServiceType(Map<String, Object> paramMap);

	/**
	 * 记录设备
	 * 
	 * @param phone
	 * @param appType
	 * @param clientType
	 * @param version
	 * @param model
	 * @param resolution
	 * @param userType
	 * @return
	 * @throws Exception
	 */
	public JSONObject recordDevice(String phone, String appType, Integer clientType, String version, String osVersion, String model, Integer userType, String clientId) throws Exception;

	/**
	 * 获取融云token信息
	 * 
	 * @param uid
	 *            用户或者商户id
	 * @param clientFlag
	 *            用户或者商户标识（1：商户，2：用户）
	 * */
	public String getRongCloudToken(String uid, String clientFlag, String clientId, String name, String portraitUri) throws Exception;

	/**
	 * 保存融云token
	 */
	public void saveRongCloudToken(String uid, String clientFlag, String token, String clientId);

	/** 用户反馈信息 */
	public boolean feedback(Map<String, Object> paramMap, List<String> picturePaths) throws Exception;

	/**
	 * 根据用户id获取基本信息（用户基本信息或者商户基本信息）
	 * 
	 * @param uid
	 *            (用户或者商户员工id)
	 * @param clientFlag
	 *            用户或者商户标识（1：商户，2：用户）
	 * @return
	 */
	public Map<String, Object> getBasicInfoByUid(String uid, String clientFlag);

	/** 检查开通服务的城市 */
	public boolean checkServiceCity(String province, String city) throws Exception;

	/** 获得服务类型的参数 */
	public JSONObject getServiceParas(String appType, String serviceType) throws Exception;
	
	/**
	 * 用户首页图片轮播
	 * @return
	 * @throws Exception
	 */
	public JSONObject getUserHomeBanner() throws Exception;

	
	/**
	 * 商户首页图片轮播
	 * @return
	 * @throws Exception
	 */
	public JSONObject getMerchantHomeBanner() throws Exception;
	
	
	/**
	 * 获取用户首页信息 2016-08-31 版本
	 * @param city
	 * @param province
	 * @param userId
	 * @return
	 * @throws Excetion
	 */
	public JSONObject getUserHomePage(String province,String city, Long userId,String version) throws Exception; 
	
	/**
	 * 用户首页推荐
	 * @return
	 * @throws Exception
	 */
	public JSONObject getUserHomePageRecommend(String province,String city) throws Exception;

	
	/**
	 * 用户首页附近商户查询
	 * @param longitude
	 * @param latitude
	 * @param range
	 * @param startNo
	 * @param condition
	 * @return
	 */
	public JSONObject getUserNearBy(double longitude,double latitude,double searchRange,int startNo, String condition) throws Exception;
	
	
	/**
	 * 热门搜索 之 查看全部
	 * @return
	 * @throws Exception
	 */
	public JSONObject getHotSearch() throws Exception;
	
	
	/**
	 * 获取行业标签及自定义标签
	 * @return
	 * @throws Exception
	 */
	public JSONObject getTags() throws Exception;
	
	
	
	/** 获取所有分类 */
	public List  getCataLogs();
	
	
	/**
	 * 获取分类对应的服务项目
	 * @param catalogIds
	 * @return
	 */
	public List<Map<String,Object>>  getServiceByCatalog(String catalogIds);
	
	

	/**
	 * 根据APPTYPE 和 SERVICE_TYPE_ID  列表，查找出服务id
	 * @param params
	 * @return
	 */
	public String  getServiceIds(List<Map<String,Object>>  params);
	
	
	/**
	 * 根据服务ID 查找对应的顶级分类
	 * @param serviceIds
	 * @return
	 */
	public  List<Map<String,Object>>  getCatalogInfo(String serviceIds);

	
	/**
	 * 根据传入的服务ids，找出属于自定义的服务对应的服务信息类表
	 * @param serviceIds
	 * @return
	 */
	public List<Map<String, Object>> getSearchedCustomServiceInfo(
			String serviceIds);

	
	/**
	 * 获取指定分类下的子级分类及对应的服务项目
	 * @param id
	 * @return
	 */
	public Map<String,Object>  getCatalogTreeAndService(String id);
	
	
	
	/**
	 * 根据appType 获取顶级分类信息。
	 * @param appTypes
	 * @return
	 */
	public  List<Map<String,Object>>  getCatalogsByAppType(String appTypes);
	
	
	/**
	 * 获取个人入住对应的分类及其服务列表
	 * @return
	 */
	public Map<String, Object> getPersonCatalogAndServices();
	
	
	/**
	 * 获取某分类下的所有服务项目
	 * @return
	 */
	public List<Map<String,Object>> getAllServicesByCatalogId(String catalogId); 
	public List<Map<String,Object>> getServiceTypesByCatalogId(Integer catalogId); 
	public List<Map<String,Object>> getServiceTypesByCatalogIdForOrder(Integer catalogId); 
	
	
	
	/**
	 * 获取某页的自定义分类及其子服务
	 * @param pageNo
	 * @return
	 */
	public List<Map<String, Object>> getPersonCatalogAndServices(int pageNo,int pageSize);
	
	
	
	/**
	 * 按搜索关键字获取某页的自定义分类及其子服务
	 * @param keyword
	 * @return
	 */
	public List<Map<String, Object>> getPersonCatalogAndServices(String keyword);
	
	
	/**
	 * 计算自定义一级分类的总页数
	 * @return
	 */
	public long  getPersonCatalogAndServicesPageNum(int pageNo,int pageSize);
	
	
	/**
	 * 获取商户入驻行业一级分类，不含子分类及服务
	 * @return
	 */
	public List<Map<String,Object>> getMerchantCataLogs();
	
	/**
	 * 获取分类或服务列表
	 * @return
	 */
	public List<Map<String,Object>> getCatalogOrServiceList(Map<String,Object> param);

	/**
	 * 计算今年剩余天数
	 * @return
	 */
	public long calcThisYearSurplusDays() throws Exception;

	/**
	 * 分享通用接口
	 * @return
	 */
	public JSONObject getShareHtml();

	/**
	 * 静态活动页接口
	 * @return
	 */
	public JSONObject getStaticActivityHtml(Map<String, Object> param);
	
	/**
	 * 活动入口列表接口
	 * @return
	 */
	public JSONObject getActivityList(Map<String, Object> param);
	
	
	/**
	 * 获取配置的防止重复提交的URL及生存期
	 * @return
	 */
	public Map<String,Integer> getRestrictUpdate();
	
	/**获取交易明细列表筛选导航列表***/
	public JSONObject getPaymentNaviList();

	
	/**
	 * 获取某城市的服务商数量
	 * @param city
	 * @param province
	 * @return
	 */
	public Integer getSummaryByCity(String province,String city);

	/**
	 * 查询帮助反馈列表
	 * @return
	 */
	public JSONObject getSystemHelpList();

	/**
	 *保存帮助反馈页点赞情况
	 * @return
	 */
	public JSONObject saveHelpEvaluation(Map<String, Object> param);
	
	/**是否有摸金金辉**/
	public int isTouchingGold();
	
	/** 统计短信启动APP次数 **/
	public JSONObject statisticsStartCount(Long userId);
	
	/** 获取推荐服务商列表 **/
	public JSONObject recommendMerchantList(Map<String, Object> param);

}
