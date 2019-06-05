package com.shanjin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IImageCacheService;
import com.shanjin.cache.service.IMerchantCacheService;
import com.shanjin.cache.service.impl.GenericCacheServiceImpl;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.*;
import com.shanjin.dao.*;
import com.shanjin.push.IosPushUtil;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IDictionaryService;
import com.shanjin.service.IElasticSearchService;
import com.shanjin.service.api.IOrderApiService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("commonService")
public class CommonServiceImpl implements ICommonService {
	private static final String[]  phonePrefix={"138","139","137"};
	
	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(CommonServiceImpl.class);
	@Resource
	private IAppConfigDao appConfigDao;

	@Resource
	private IAppUpdateDao appUpdateDao;

	@Resource
	private ILoadingDao loadingDao;

	@Resource
	private IServiceTypeDao serviceTypeDao;

	@Resource
	private IAppInfoDao appInfoDao;

	@Resource
	private IAreaDao areaDao;

	@Resource
	private IDictionaryDao dictionaryDao;

	@Resource
	private ISliderInfoDao sliderInfoDao;

	@Resource
	private IEvaluationDao evaluationDao;

	@Resource
	private ICommonCacheService commonCacheService;

	@Resource
	private IMerchantCacheService merchantCacheService;

	@Resource
	private IDictionaryService dictionaryService;

	@Resource
	private IVerificationCodeDao verificationCodeDao;

	@Resource
	private IBlackListDao blackListDao;

	@Resource
	private IOrderApiService orderApiService;

	@Resource
	private IGxfwIndexDao gxfwIndexDao;

	@Resource
	private IConfigurationInfoDao configurationInfoDao;

	@Resource
	private IAppKeyWordDao appKeyWordDao;

	@Resource
	private IDeviceDao deviceDao;

	@Resource
	private IImageCacheService imageCache;

	@Resource
	private IElasticSearchService elasticSearchService;

	@Resource
	private IRongCloudDao rongCloudDao;

	@Resource
	private IFeedbackDao feedbackDao;

	@Resource
	private ICommonDao commonDao;

	@Resource
	private IServiceCityDao serviceCityDao;

	@Resource
	private IOrderObjectDao orderObjectDao;
	
	@Resource
	private IManagerSliderInfoDao userSliderInfoDao;
	
	@Resource
	private IManagerHotRecommendDao userRecommendDao;
	
	@Resource
	private IServiceTagDao   iServiceTagDao;
	
	@Resource
	private ICataLogDao icataLogDao;
	
	@Resource
	private IMerchantForSearchDao merchantForSearchDao;
	
	@Resource 
	private IRecommendMerchantDao recommendMerchantDao;
	//热门搜索、个性服务、其它 对应的CAATLOG_ID 值
	private static final short HOT_CATALOG_ID=-1;
		
	private static final short HOT_CUSTOM_ID=-2;
		
	private static final short HOT_OTHER_ID=-3;
	
	
	/** 获得APP更新 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject checkUpdate(String packageName, int version) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("packageName", packageName);
		paramMap.put("version", version);
		// 先从缓存中读取所有的客户端版本发布列表（后台发布）,为保证缓存命中率，后台保证至少发布一条客户端版本
		List<Map<String, Object>> pubList = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.UPDATE_CLIENT_VERSION);
		// 缓存中没有任何客户端版本记录，则从数据库中读取一遍（尽量降低这种可能）
		if (pubList == null || pubList.size() < 1) {
			pubList = appUpdateDao.getPubClientVersionList();
			commonCacheService.deleteObject(CacheConstants.UPDATE_CLIENT_VERSION);
			if (pubList != null && pubList.size() > 0) {
				commonCacheService.setObject(pubList, CacheConstants.UPDATE_CLIENT_VERSION);
			}
		}
		// 客户端版本升级详细信息（包括：包名packageName、包类型packageType、更新方式updateType、app类型appType、版本号version、下载地址downloadUrl、升级提示detail）
		Map<String, Object> info = new HashMap<String, Object>();
		String isUpdate = "0"; // 是否有新版本（1：有，0：没有）
		jsonObject = new ResultJSONObject("000", "目前你的版本是最新的");

		if (pubList == null || pubList.size() < 1) {
			// 没有任何发布版本，直接返回无更新
			jsonObject.put("info", info);
			jsonObject.put("isUpdate", isUpdate);
			return jsonObject;
		}
		// 版本检测（当前客户端版本与最新版本比较版本号）
		info = appUpdateInfo(packageName, version, pubList);
		if (info != null && !StringUtil.isNullStr(StringUtil.null2Str(info.get("packageName")))) {

			jsonObject = new ResultJSONObject("000", "发现新版本");
			isUpdate = "1";
		}
		jsonObject.put("info", info);
		jsonObject.put("isUpdate", isUpdate);
		return jsonObject;
	}

	/**
	 * 检测版本更新信息
	 * 
	 * @param packageName
	 * @param version
	 * @param pubList
	 * @return 最新版本(version 版本号最大的版本)
	 */
//	private Map<String, Object> appUpdateInfo(String packageName, int version, List<Map<String, Object>> pubList) {
//		Map<String, Object> info = new HashMap<String, Object>();
//		int bigThanNow = 0;
//		/**
//		 * 记录发布的版本中比当前客户端版本 版本号大的版本个数
//		 * （如：当前客户端版本1.0.0，但实际已发布了1.0.0、1.0.1、1.0.2三个版本，那么这个数值为2）
//		 * 记录此数值的意义在于，如果客户端长期未升级，但已发布多个新版本，则版本升级默认需强制升级
//		 * 暂定服务器支持两个版本同时使用，更早的客户端版本须强制升级至最新
//		 **/
//		int latestVersion = version; // 已经发布的最新版本号
//		if (pubList != null && pubList.size() > 0) {
//			int pubVersion = 0;
//			String pubPackageName = null; // 发布的客户端包名
//			for (Map<String, Object> pub : pubList) {
//				pubPackageName = StringUtil.null2Str(pub.get("packageName"));
//				if (pubPackageName.equals(packageName)) {
//					// 相同的包名，再检测版本号
//					pubVersion = pubVersionStrToInt(StringUtil.null2Str(pub.get("version")));// 后台实际发布的版本号可能为2.3.3
//																								// 或者2.3.3.160114
//					if (pubVersion > version) {
//						// 发布的此版本比当前客户端的版本号大，需要更新
//						bigThanNow++;
//						if (latestVersion < pubVersion) {
//							// 记录最新的版本号
//							latestVersion = pubVersion;
//							info = pub;
//						}
//
//					}
//				}
//
//			}
//		}
//		if (bigThanNow > 1) {
//			// 当前客户端版本到最新版本之间有超过1个新版本，设置为强制升级
//			info.put("updateType", 2); // 1:提示升级；2：强制升级
//		}
//		return info;
//	}
	private Map<String, Object> appUpdateInfo(String packageName, int version, List<Map<String, Object>> pubList) {
		Map<String, Object> info = new HashMap<String, Object>();
		int bigThanNow = 0;
		/**
		 * 记录发布的版本中比当前客户端版本 版本号大的版本个数
		 * （如：当前客户端版本1.0.0，但实际已发布了1.0.0、1.0.1、1.0.2三个版本，那么这个数值为2）
		 * 记录此数值的意义在于，如果客户端长期未升级，但已发布多个新版本，则版本升级默认需强制升级
		 * 暂定服务器支持两个版本同时使用，更早的客户端版本须强制升级至最新
		 **/
		String v = StringUtil.null2Str(version);
		String latestVersion = v; // 已经发布的最新版本号
		if (pubList != null && pubList.size() > 0) {
			String pubVersion = "";
			String pubPackageName = null; // 发布的客户端包名
			for (Map<String, Object> pub : pubList) {
				pubPackageName = StringUtil.null2Str(pub.get("packageName"));
				if (pubPackageName.equals(packageName)) {
					// 相同的包名，再检测版本号
					pubVersion = pubVersionStrExceptDot(StringUtil.null2Str(pub.get("version")));// 后台实际发布的版本号可能为2.3.3
																								// 或者2.3.3.160114
					if (pubVersion.compareTo(v)>0) {
						// 发布的此版本比当前客户端的版本号大，需要更新
						bigThanNow++;
						if (latestVersion.compareTo(pubVersion) < 0 ) {
							// 记录最新的版本号
							latestVersion = pubVersion;
							info = pub;
						}

					}
				}

			}
		}
		if (bigThanNow > 1) {
			// 当前客户端版本到最新版本之间有超过1个新版本，设置为强制升级
			info.put("updateType", 2); // 1:提示升级；2：强制升级
		}
		return info;
	}


	/**
	 * 后台发布的客户端版本转换（如果版本号是101这种三位数字的，直接转换成int即可；若是1.0.1这种类型的，也需要转换成101整数形式）
	 * 
	 * @param pubVersion
	 * @return
	 */
	private static int pubVersionStrToInt(String pubVersion) {
		int version = 0;
		if (pubVersion.contains(".")) {
			// “.”号分割的版本号，如1.0.2
			String v = "";
			String[] arr = pubVersion.split("\\.");
			if (arr != null && arr.length > 0) {
				for (int i = 0; i < arr.length; i++) {
					v = v + arr[i];
				}
			}
			version = StringUtil.nullToInteger(v);
		} else {
			version = StringUtil.nullToInteger(pubVersion);
		}
		return version;
	}
	
	/**
	 * 去除“.”
	 * @param pubVersion
	 * @return
	 */
	private static String pubVersionStrExceptDot(String pubVersion) {
		String version = "";
		if (pubVersion.contains(".")) {
			// “.”号分割的版本号，如1.0.2
			String[] arr = pubVersion.split("\\.");
			if (arr != null && arr.length > 0) {
				for (int i = 0; i < arr.length; i++) {
					version = version + arr[i];
				}
			}
		}
		return version;
	}

	/**
	 * 获取所有app列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAllAppInfo() throws Exception {
		JSONObject jsonObject = null;
		List<Map<String, Object>> results = null;
		// ******先读取缓存数据*****
		results = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.APP_LIST_KEY);
		if (results == null || results.size() < 1) {
			// 读取DB
			results = appInfoDao.getAPPList();
			for (int i = 0; i < results.size(); i++) {
				BusinessUtil.disposePath(results.get(i), "path");
			}
			commonCacheService.setObject(results, CacheConstants.APP_LIST_KEY);
		}

		jsonObject = new ResultJSONObject("000", "获得应用列表成功");
		jsonObject.put("appList", results);

		return jsonObject;
	}

	/**
	 * 根据父id获取所有子服务列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getServiceInfo(String appType, Long parentId) throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		paramMap.put("parentId", parentId);
		List<Map<String, Object>> results = null;
		// ******先读取缓存数据*****
		results = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.SERVICE_TYPE_LIST_KEY, appType, StringUtil.null2Str(parentId));
		if (results == null || results.size() < 1) {
			// 读取db数据
			if (parentId == null) {
				results = serviceTypeDao.getParentServiceType(paramMap);
			} else {
				results = serviceTypeDao.getChildServiceType(paramMap);
			}
			commonCacheService.setObject(results, CacheConstants.SERVICE_TYPE_LIST_KEY, appType, StringUtil.null2Str(parentId));
		}
		jsonObject = new ResultJSONObject("000", "获得子服务列表成功");
		jsonObject.put("serviceList", results);

		return jsonObject;
	}

	/**
	 * 多级服务项目一切性查询（递归形式）
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getServiceInfoMultilevel(String appType, String alias) throws Exception {
		JSONObject jsonObject = null;
		// if (!appType.startsWith("yxt")) {
		// jsonObject = new ResultJSONObject("app_type_error", "服务类型参数错误");
		// }
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		// paramMap.put("parentId", parentId);
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		// ******先读取缓存数据*****
		results = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.SERVICE_TYPE_LIST_KEY, appType, alias);
		if (results == null || results.size() < 1) {
			// results = serviceTypeDao.getYxtServiceType(paramMap);
//			 results = BusinessUtil.getChildsServiceTypeForUser(results, parentId);
			Map<String, Object> catalogMap = serviceTypeDao.getCatalogByAlias(alias);
			Long parentId = StringUtil.nullToLong(catalogMap.get("id"));
			results=this.getCatalogByParentId(parentId);
			commonCacheService.setObject(results, CacheConstants.SERVICE_TYPE_LIST_KEY, appType, alias);
		}
		// 从图片缓存中获取图片
//		Map<String, String> orderIcon_map = imageCache.getImageMapCache(CacheConstants.IMAGE_CACHE.SHOW_ICON);
//		if (orderIcon_map == null) {// 从数据库中获取
//			List<Map<String, Object>> listShowIcon = serviceTypeDao.getServiceShowIconImg();
//			orderIcon_map = BusinessUtil.getShowIconMap(listShowIcon);
//			imageCache.setImageMapCache(CacheConstants.IMAGE_CACHE.SHOW_ICON, orderIcon_map);
//		}
//		BusinessUtil.getImageInfoFromCache(results, orderIcon_map);

		jsonObject = new ResultJSONObject("000", "获得服务列表成功");
		jsonObject.put("serviceList", results);

		return jsonObject;
	}

	/**
	 * 获取所有服务列表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> getServiceInfoMap(String appType, Long parentId) {
		Map<String, String> serviceInfoMap = null;
		JSONObject jsonObject = null;
		try {
			jsonObject = getServiceInfo(appType, parentId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if ("000".equals(jsonObject.get("resultCode"))) {
			List<Map<String, Object>> serviceList = (List<Map<String, Object>>) jsonObject.get("serviceList");
			if (serviceList != null) {
				serviceInfoMap = new HashMap<String, String>();
				for (Map<String, Object> result : serviceList) {
					serviceInfoMap.put(result.get("serviceType").toString(), result.get("servicName").toString());
				}
			}
		}
		return serviceInfoMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getCity(Long parentId) throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("parentId", parentId);
		List<Map<String, Object>> results = null;
		// ******先读取缓存数据*****
		results = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.AREA_LIST_KEY, StringUtil.null2Str(parentId));
		if (results == null || results.size() < 1) {
			if (parentId == null) {
				results = areaDao.getProvince();
			} else {
				results = areaDao.getCity(paramMap);
			}
			commonCacheService.setObject(results, CacheConstants.AREA_LIST_KEY, StringUtil.null2Str(parentId));
		}

		jsonObject = new ResultJSONObject("000", "获得城市列表成功");
		jsonObject.put("cityList", results);

		return jsonObject;
	}

	/** 获取地区信息列表，供移动端一次性选择城市 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAreaList() throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000", "获取地区信息列表成功");
		List<Map<String, Object>> areaList = null;
		List<Map<String, Object>> areas = null;
		// ******先读取缓存数据*****
		areaList = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.PROVINCE_CITY_LIST_KEY);
		if (areaList == null || areaList.size() < 1) {
			areaList = new ArrayList<Map<String, Object>>();
			areas = areaDao.getCityList();
			if (areas != null) {
				for (Map<String, Object> area : areas) {
					if (area.get("parentId") == null) {
						Map<String, Object> areaInfo = new HashMap<String, Object>();
						areaInfo.put("province", area.get("area"));
						List<Map<String, Object>> citys = new ArrayList<Map<String, Object>>();
						for (Map<String, Object> city : areas) {
							if (city.get("parentId") != null && String.valueOf(city.get("parentId")).equals(String.valueOf(area.get("id")))) {
								Map<String, Object> cityInfo = new HashMap<String, Object>();
								cityInfo.put("city", city.get("area"));
								cityInfo.put("cityId", city.get("id"));
								citys.add(cityInfo);
							}
						}
						areaInfo.put("citys", citys);
						areaList.add(areaInfo);
					}
				}
			}
			commonCacheService.setObject(areaList, CacheConstants.PROVINCE_CITY_LIST_KEY);
		}
		jsonObject.put("areaList", areaList);
		return jsonObject;
	}
	/** 获取地区信息列表，供移动端一次性选择城市 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAllCitys() throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000", "获取地区信息列表成功");
		List<Map<String, Object>> areaList = null;
		List<Map<String, Object>> areas = null;
		// ******先读取缓存数据*****
		areaList = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.PROVINCE_CITY_LIST_KEY_2);
		if (areaList == null || areaList.size() < 1) {
			areaList = new ArrayList<Map<String, Object>>();
			areas = areaDao.getAllCityList();
			if (areas != null) {
				for(String letter : StringUtil.alphabet){					
					Map<String, Object> areaInfo = new HashMap<String, Object>();
					areaInfo.put("letter", letter);
					List<Map<String, Object>> citys = new ArrayList<Map<String, Object>>();
					for (Map<String, Object> area : areas) {
						Map<String, Object> cityInfo = new HashMap<String, Object>();
						String province=StringUtil.null2Str(area.get("province"));
						String city=StringUtil.null2Str(area.get("city"));
						String indexStr=StringUtil.null2Str(area.get("indexStr"));
						if(StringUtil.matchProvince(province)){
							continue;
						}
						if(!indexStr.equals("") && indexStr.toLowerCase().equals(letter.toLowerCase())){
							cityInfo.put("province", province);
							cityInfo.put("city", city);
							citys.add(cityInfo);
						} 
					}
					if("BSTC".contains(letter.toUpperCase())){
						addCrownCity(letter,citys);
					}
					if(citys.size()==0){
						continue;
					}
					areaInfo.put("cityList", citys);
					areaList.add(areaInfo);
				}
			}
			commonCacheService.setObject(areaList, CacheConstants.PROVINCE_CITY_LIST_KEY_2);
		}
		jsonObject.put("areaList", areaList);
		return jsonObject;
	}
	private void addCrownCity(String letter, List<Map<String, Object>> citys) {
		Map<String, Object> cityInfo = new HashMap<String, Object>();
		if(letter.toUpperCase().equals("B")){
			cityInfo.put("province", "北京");
			cityInfo.put("city", "北京");
			citys.add(cityInfo);
		}else if(letter.toUpperCase().equals("T")){
			cityInfo.put("province", "天津");
			cityInfo.put("city", "天津");
			citys.add(cityInfo);
		}else if(letter.toUpperCase().equals("S")){
			cityInfo.put("province", "上海");
			cityInfo.put("city", "上海");
			citys.add(cityInfo);
		}else if(letter.toUpperCase().equals("C")){
			cityInfo.put("province", "重庆");
			cityInfo.put("city", "重庆");
			citys.add(cityInfo);
		}
	}

	/** 获取轮播图列表 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getSliderPics(String appType) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		List<Map<String, Object>> sliderPics = null;
		sliderPics = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.SLIDER_LIST_KEY, appType);
		if (sliderPics != null && sliderPics.size() > 0) {
			// 缓存中存在某种app的轮播图，判断时间是否过期，若过期，清除该缓存，从db重新读取
		} else {
			// 无轮播图，先考虑理论当前一定存在，读db
			sliderPics = sliderInfoDao.getSliderPics(paramMap);
			for (int i = 0; i < sliderPics.size(); i++) {
				BusinessUtil.disposePath(sliderPics.get(i), "path");
			}
			commonCacheService.setObject(sliderPics, CacheConstants.SLIDER_LIST_KEY, appType);
		}
		return sliderPics;
	}

	@Override
	public String helloWorld(String helloStr) {
		// 联通性测试，先暂时保留system.out
		System.out.println("===========================helloworld================================");
		return "helloStr" + helloStr;
	}

	/** 查看顾客评价 */
	@Override
	public JSONObject selectUserEvaluation(String appType, Long merchantId, Long orderId, int pageNo) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 订单ID
		paramMap.put("orderId", orderId);
		// 应用程序类型
		paramMap.put("appType", appType);
		// 查询起始记录行号
		paramMap.put("rows", pageNo * Constant.PAGESIZE);
		// 每页显示的记录数
		paramMap.put("pageSize", Constant.PAGESIZE);

		JSONObject jsonObject = new ResultJSONObject("000", "服务评价信息加载成功");

		if (orderId == null) {
			int count = evaluationDao.getMerchantEvaluationNum(merchantId);
			if (count == 0) {
				jsonObject.put("totalPage", 0);
				jsonObject.put("userEvaluationList", new ArrayList<HashMap<String, String>>());
				return jsonObject;
			} else {
				jsonObject.put("totalPage", BusinessUtil.totalPageCalc(count));
			}
		}
		List<Map<String, Object>> userEvaluationMapList = evaluationDao.getMerchantEvaluations(paramMap);
		if (userEvaluationMapList.size() == 1) {
			jsonObject.put("totalPage", 0);
		}
		for (Map<String, Object> merchantUsersMap : userEvaluationMapList) {
			BusinessUtil.disposeManyPath(merchantUsersMap, "path");
			BusinessUtil.disposeManyPath(merchantUsersMap, "attachmentPaths");
		}
		jsonObject.put("userEvaluationList", userEvaluationMapList);

		return jsonObject;
	}

	@Override
	public JSONObject iosPushTest(String appType) {
		JSONObject jsonObject = null;
		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("merchantId", 1L);
		map.put("token", "6eb55fd4e7ef1fae99c97a6a0a29cec2a933183ddc0c27e148d7c8f4b00d6a37");
		iosPushInfoList.add(map);
		Map<String, Object> msg = new HashMap<String, Object>();
		Random r = new Random();
		int i = r.nextInt(9);
		msg.put("orderId", 1139);
		String exit = "0"; // 是否是退出消息（1：是，0：否）
		msg.put("exit", 1);
		String alertMsg = "";
		if (i == 0) {
			exit = "1";
			alertMsg = "你的账号已在别处登录！";
		} else if (i == 3) {
			alertMsg = "你已成功获得订单，请为用户提供优质服务！";
		} else if (i == 5) {
			alertMsg = "服务完成！";
		} else if (i == 8) {
			alertMsg = "用户已对你进行评价，请查看！";
		} else {
			i = 1;
			alertMsg = "你有一条新订单，请及时查看！";
		}
		msg.put("exit", exit);
		msg.put("alertMsg", alertMsg);
		msg.put("pushType", i + "");
		msg.put("appType", appType);
		Map<String, Object> cert = dictionaryService.getIosPushCertFromDict(Constant.IOS_MERCHANT_CERT);
		cert.put(Constant.IOS_CERT_TYPE, Constant.IOS_MERCHANT_CERT);
		IosPushUtil.push(iosPushInfoList, msg, cert);
		jsonObject = new ResultJSONObject("000", "ios推送测试成功");
		return jsonObject;
	}

	/** 生成手机验证码 */
	@Override
	public JSONObject createVerificationCode(Long merchantId, String phone) throws Exception {
		JSONObject result = new ResultJSONObject("000", "验证码已经发送,请耐心等候");

		// 生成验证码
		String verificationCode = BusinessUtil.createVerificationCode(merchantId + "");

		// 将验证码保存到缓存
		String cacheResult = merchantCacheService.createVerificationCodeCache(merchantId, verificationCode);

		// 将验证码保存到数据库
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("merchantId", merchantId);
		map.put("verificationCode", verificationCode);
		int dbResult = this.verificationCodeDao.updateVerificationInfo(map);

		if (cacheResult == null && dbResult == 0) {// 保存缓存和保存数据库都失败
			result.put("create_bankCard_verificationCode_failure", "验证码保存失败!");
			return result;
		}
		// 将验证码发送到手机
		if (Constant.DEVMODE) {// 开发模式，则直接返回发送成功
		} else {
			result = SmsUtil.sendSms(phone, "验证码：" + verificationCode + ",10分钟内有效。如非本人操作，请忽略本短信。");
		}
		return result;
	}

	/** 保存验证码缓存 */
	@Override
	public boolean saveVerificationCodeCache(Object obj, int seconds, String key, String... attachedKey) throws Exception {
		if (commonCacheService.setObject(obj, seconds, key, attachedKey) != null) {
			return true;
		} else {
			return false;
		}
	}

	/** 获得验证码缓存 */
	@Override
	public Object getVerificationCodeCache(String key, String... attachedKey) throws Exception {
		return commonCacheService.getObject(key, attachedKey);
	}

	/** 清除验证码缓存 */
	@Override
	public boolean cleanVerificationCodeCache(String key, String... attachedKey) throws Exception {
		return commonCacheService.deleteObject(key, attachedKey);
	}

	/** 获取红妆所有的二级列表 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject gethzDictInfo(String dictType, String childDictType) throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("dictType", dictType);
		List<Map<String, Object>> results = null;
		// ******先读取缓存数据*****
		results = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.HZ_DICT_LIST_KEY, dictType, childDictType);
		if (results == null || results.size() < 1) {

			results = dictionaryDao.getParentDict(paramMap);
			List<Map<String, Object>> childsList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> map : results) {
				Object parentId = map.get("id");
				if (StringUtil.nullToInteger(parentId) != 0) {
					paramMap.put("dictType", childDictType);
					paramMap.put("parentId", parentId);
					childsList = dictionaryDao.getChildDict(paramMap);
				}

				LinkedHashMap<Long, Map<String, Object>> dicts = new LinkedHashMap<Long, Map<String, Object>>();
				if (childsList != null) {
					for (Map<String, Object> result : childsList) {
						Long id = (Long) result.get("id");
						String dictKey = (String) result.get("dictKey");
						String dictValue = (String) result.get("dictValue");
						Integer isLeaves = (Integer) result.get("isLeaves");
						Integer attachmentType = (Integer) result.get("attachmentType");
						String attachmentStyle = (String) result.get("attachmentStyle");
						String path = (String) result.get("path");

						Map<String, Object> values = dicts.get(id);
						if (values == null) {
							values = new HashMap<String, Object>();
							values.put("id", id);
							values.put("dictKey", dictKey);
							values.put("dictValue", dictValue);
							values.put("isLeaves", isLeaves);
							dicts.put(id, values);
						}
						if (attachmentType != null && attachmentStyle != null && path != null) {
							values.put("attachmentType", attachmentType);
							values.put(attachmentStyle, BusinessUtil.disposeImagePath(path));
						}
					}

				}
				map.put("itemList", new ArrayList<Map<String, Object>>(dicts.values()));
			}
			commonCacheService.setObject(results, CacheConstants.HZ_DICT_LIST_KEY, dictType, childDictType);
		}
		//
		jsonObject = new ResultJSONObject("000", "获得二级列表成功");
		jsonObject.put("itemList", results);

		return jsonObject;
	}

	/**
	 * 临时测试使用-将接口日志信息插入到数据库
	 */
	public void saveSystemLog(Map<String, Object> param) {
		verificationCodeDao.saveSystemLog(param);
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject checkLoading(String clientType) throws Exception {
		JSONObject jsonObject = null;
		// 先从缓存中读取所有的客户端启动页发布列表（后台发布）,为保证缓存命中率，后台保证至少每个客户端发布一条启动页
		List<Map<String, Object>> pubList = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.LOADING);
		// 缓存中没有任何记录，则从数据库中读取一遍（尽量降低这种可能）
		if (pubList == null || pubList.size() < 1) {
			pubList = loadingDao.getLoadingList();
			commonCacheService.deleteObject(CacheConstants.LOADING);
			if (pubList != null && pubList.size() > 0) {
				commonCacheService.setObject(pubList, CacheConstants.LOADING);
			}
		}
		Map<String, Object> info = new HashMap<String, Object>();
		String haveLoading = "0"; // 是否有新版本（1：有，0：没有）
		jsonObject = new ResultJSONObject("000", "暂未发布新的加载页");
		if (pubList == null || pubList.size() < 1) {
			jsonObject.put("info", info);
			jsonObject.put("haveLoading", haveLoading);
			return jsonObject;
		}
		for (Map<String, Object> map : pubList) {
				String st = StringUtil.null2Str(map.get("stime"));
				String et = StringUtil.null2Str(map.get("etime"));
				long stime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, st).getTime();
				long etime = DateUtil.parseDate(DateUtil.DATE_TIME_PATTERN, et).getTime();
				Date now = new Date();
				long ntime = now.getTime();
				if (ntime >= stime && ntime <= etime) {
					info.put("id", map.get("id"));
					info.put("title", map.get("title"));
					info.put("link", map.get("link"));
					info.put("showType", map.get("showType"));
					String image = BusinessUtil.disposeImagePath(StringUtil.null2Str(map.get("image")));
					info.put("image", image);
					String fileName = image.substring(image.lastIndexOf("/") + 1);
					info.put("fileName", fileName);
					info.put("stime", st);
					info.put("etime", et);
					jsonObject = new ResultJSONObject("000", "发现发布加载页");
					haveLoading = "1";
					break;
				}
		}
		jsonObject.put("info", info);
		jsonObject.put("haveLoading", haveLoading);
		return jsonObject;
	}

	/** 判断是否为黑名单（手机） */
	@Override
	public JSONObject checkBlackList(String phone, String appType) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
		paramMap.put("appType", appType);
		Integer checkCout = blackListDao.checkBlackList(paramMap);
		if (checkCout > 0) {
			jsonObject = new ResultJSONObject("black", "已经列为黑名单用户");
		} else {
			jsonObject = new ResultJSONObject("000", "不在黑名单列表内");
		}
		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getAmsServiceInfo(String appType, Boolean needLoadChildren) throws Exception {
		JSONObject jsonObject = new JSONObject();

		// 获取服务ICON版本，艾秘书是在orderIcon 中 设置的
		Integer lastImgVersion = imageCache.getImageVersionCache(CacheConstants.IMAGE_CACHE.ORDER_ICON_VERSION);

		// 生成缓存时，服务图标对应的版本。
		Object amsImageVersion = commonCacheService.getObject(CacheConstants.AMS_SERVICE_VERSION_CACHE_KEY, appType);

		Integer cachedImageVersion = -1;

		if (amsImageVersion != null && !amsImageVersion.toString().trim().equals("")) {
			cachedImageVersion = Integer.parseInt(amsImageVersion.toString());
		}

		List<Map<String, Object>> services = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.SERVICE_TYPE_LIST_KEY, appType, "withChildrens");

		// 图标有更新，需要重新生成缓存
		if (hasNewImageVersion(lastImgVersion, cachedImageVersion)) {
			services = null;
		}

		if (services == null || services.size() < 1) {
			// 缓存未命中，则从数据库中构造，然后放入缓存

			services = serviceTypeDao.getAmsServiceType(appType);
			if (services != null && services.size() > 0) {
				// 图片地址加 http前缀
				for (Map<String, Object> service : services) {
					// 删除末尾的订单图标，只保留服务的图标
					String[] oriPathes = service.get("path").toString().split(",");
					StringBuffer serviceIcon = new StringBuffer();
					serviceIcon.append(oriPathes[0]);
					service.put("path", serviceIcon.toString());
					BusinessUtil.disposeManyPath(service, "path");

					if (needLoadChildren) {

						Map<String, Object> condition = new HashMap<String, Object>();
						condition.put("appType", appType);
						condition.put("parentId", service.get("service_type_id"));
						List<Map<String, Object>> childServices = serviceTypeDao.getChildServiceType(condition);
						if (childServices != null && childServices.size() > 0) {
							for (Map<String, Object> childService : childServices) {
								condition.put("parentId", childService.get("serviceType"));
								List<Map<String, Object>> grandSonServices = serviceTypeDao.getChildServiceType(condition);
								childService.put("serviceList", grandSonServices);
							}
						}
						service.put("serviceList", childServices);
					}
				}
				commonCacheService.setObject(services, CacheConstants.SERVICE_TYPE_LIST_KEY, appType, "withChildrens");

				// 登记艾秘书服务缓存对应的图像版本
				commonCacheService.setObject(lastImgVersion, CacheConstants.AMS_SERVICE_VERSION_CACHE_KEY, appType);
			}
		}
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "获取服务列表成功");
		jsonObject.put("servicesList", services);

		return jsonObject;
	}

	/** 根据appType查询推送信息 **/
	public Map<String, Object> getPushInfo(String appType) {
		return loadingDao.getPushInfo(appType);
	}

	/**
	 * 查找所有商户
	 */
	@Override
	public List<Map<String, Object>> getAllMerchant(Map<String, Object> paramMap) {
		List<Map<String, Object>> resultList = loadingDao.getAllMerchant(paramMap);
		return resultList;
	}

	/**
	 * 根据城市查找所有商户
	 */
	@Override
	public List<Map<String, Object>> getAllMerchantByCity(Map<String, Object> paramMap) {
		List<Map<String, Object>> resultList = loadingDao.getAllMerchantByCity(paramMap);
		return resultList;
	}

	/**
	 * 根据经纬度查找所有商户
	 */
	@Override
	public List<Map<String, Object>> getAllMerchantByRange(Map<String, Object> paramMap) {
		List<Map<String, Object>> resultList = null;
		Double longitude = null;
		Double latitude = null;
		Map<String, Object> locationMap = orderApiService.selectOrderLocation(paramMap);
		if (locationMap != null) {
			if (locationMap.get("longitude") != null && locationMap.get("latitude") != null) {
				longitude = (Double) locationMap.get("longitude");
				latitude = (Double) locationMap.get("latitude");
			}
		} else {
			logger.info("---------------------------------------推送功能：经纬度：" + longitude + "," + latitude);
		}

		if (longitude != null && latitude != null) {
			double pushRange = Double.parseDouble(paramMap.get("pushRange") == null ? "0" : paramMap.get("pushRange").toString());
			Map<String, Double> locationInfo = PositionUtil.calcTopLfRgBtCoordinate(pushRange, longitude, latitude);
			paramMap.put("leftLongitude", locationInfo.get("leftLongitude"));
			paramMap.put("rightLongitude", locationInfo.get("rightLongitude"));
			paramMap.put("buttomLatitude", locationInfo.get("buttomLatitude"));
			paramMap.put("topLatitude", locationInfo.get("topLatitude"));
			resultList = loadingDao.getAllMerchantByRange(paramMap);
		}
		return resultList;
	}

	@Override
	public List<Map<String, Object>> getMerchantClientIdsByServiceTag(Map<String, Object> paramMap) {
		List<Map<String, Object>> resultList = null;
		Double longitude = null;
		Double latitude = null;

		String orderTitel = gxfwIndexDao.getOrderTitle(paramMap);
		System.out.println(orderTitel);
		if (StringUtils.isEmpty(orderTitel)) {
			return null;
		}

		Map<String, Object> locationMap = orderApiService.selectOrderLocation(paramMap);
		if (locationMap != null) {
			if (locationMap.get("longitude") != null && locationMap.get("latitude") != null) {
				longitude = (Double) locationMap.get("longitude");
				latitude = (Double) locationMap.get("latitude");
			}
		}
		if (longitude != null && latitude != null) {
			double pushRange = Double.parseDouble(paramMap.get("pushRange") == null ? "0" : paramMap.get("pushRange").toString());
			Map<String, Double> locationInfo = PositionUtil.calcTopLfRgBtCoordinate(pushRange, longitude, latitude);
			paramMap.put("leftLongitude", locationInfo.get("leftLongitude"));
			paramMap.put("rightLongitude", locationInfo.get("rightLongitude"));
			paramMap.put("buttomLatitude", locationInfo.get("buttomLatitude"));
			paramMap.put("topLatitude", locationInfo.get("topLatitude"));
			List<Map> merchantIds = elasticSearchService.iGxfwSearchMerchantId(orderTitel);
			if (merchantIds != null && merchantIds.size() > 0) {
				paramMap.put("merchantIds", merchantIds);
				System.out.println("merchantIds:" + merchantIds);
				resultList = loadingDao.getClientIdsByServiceTag(paramMap);
				System.out.println(resultList);
			} else {
				return null;
			}
		}
		return resultList;
	}

	/**
	 * 查找在线商户
	 */
	@Override
	public List<Map<String, Object>> getOnlineMerchant(List<Map<String, Object>> allMerchantList) {
		List<Map<String, Object>> resultList = loadingDao.getOnlineMerchant(allMerchantList);
		return resultList;
	}

	/**
	 * 获取推荐的标签
	 */
	@Override
	public JSONObject selectRecommendServiceTag() throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000", "查询推荐的服务标签");
		List<Map<String, Object>> recommendTag = this.gxfwIndexDao.selectRecommendServiceTag();
		jsonObject.put("recommendTagCount", (recommendTag == null) ? 0 : recommendTag.size());
		jsonObject.put("recommendTag", recommendTag);
		return jsonObject;
	}

	/**
	 * 获取配置信息
	 * 
	 * @return
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getConfigurationInfoByKey(String key) {
		// 读取配置信息缓存
		Map<String, Object> allConfigurationInfoMap =null;
		try{
			allConfigurationInfoMap=(Map<String, Object>) commonCacheService.getObject(CacheConstants.CONFIG_KEY);
		}catch(Exception e){
			commonCacheService.deleteObject(CacheConstants.CONFIG_KEY);
		}
		if (allConfigurationInfoMap == null) {// 如果没有缓存配置信息则读取数据库
			allConfigurationInfoMap=new HashMap<String, Object>();
			List<Map<String, Object>> listConfigurationInfo = configurationInfoDao.getConfigurationInfo();
			for(Map<String,Object> map : listConfigurationInfo){
				String configKey=StringUtil.null2Str(map.get("config_key"));
				allConfigurationInfoMap.put(configKey, map);
			}
			commonCacheService.setObject(allConfigurationInfoMap, CacheConstants.CONFIG_KEY);
		}
		
		Map<String, Object> configurationInfoMap = (Map<String, Object>)allConfigurationInfoMap.get(key);
		if(configurationInfoMap==null){
			System.out.println("------------配置参数为空："+key);
		}
		
		return configurationInfoMap;
	}

	/**
	 * 查询应用的服务类型
	 * 
	 * @return List<Map<String,Object>>
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getServiceTypeByAppType(String appType, String parentId) {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		paramMap.put("parentId", parentId);
		List<Map<String, Object>> serviceTypeList = null;
		// ******先读取缓存数据*****
		serviceTypeList = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.SERVICE_TYPE_LIST_KEY, appType, StringUtil.null2Str(parentId));
		if (serviceTypeList == null || serviceTypeList.size() < 1) {
			// 读取db数据
			if (parentId == null) {
				serviceTypeList = serviceTypeDao.getParentServiceType(paramMap);
			} else {
				serviceTypeList = serviceTypeDao.getChildServiceType(paramMap);
			}
			commonCacheService.setObject(serviceTypeList, CacheConstants.SERVICE_TYPE_LIST_KEY, appType, StringUtil.null2Str(parentId));
		}
		return serviceTypeList;
	}

//	/**
//	 * 从serviceType列表中获取某个serviceTypeId对应的名称 
//	 * @param serviceTypeId
//	 * @param appType
//	 * @return
//	 * @throws
//	 */
//	public Map<String,Object> getServiceType(String serviceTypeId) {
//		if (StringUtils.isEmpty(serviceTypeId) ) {
//			return null;
//		}
//		List<Map<String, Object>> serviceTypeList =this.getAllServiceType();
//		for (Map<String, Object> map : serviceTypeList) {
//			String serviceTypeId_ = map.get("serviceTypeId") == null ? "" : map.get("serviceTypeId").toString();
//			if (serviceTypeId.equals(serviceTypeId_)) {				
//				return map;
//			}
//		}
//		return null;
//	}
	@SuppressWarnings("unchecked")
	public String getServiceTypeName(String serviceTypeId) {
		if (StringUtils.isEmpty(serviceTypeId) ) {
			return null;
		}
		Map<String, Object> allServiceTypeMap =this.getAllServiceTypeMap();
		if(allServiceTypeMap==null){
			return null;
		}
		Map<String, Object> serviceTypeMap=(Map<String, Object>)allServiceTypeMap.get(serviceTypeId);
		if(serviceTypeMap==null){
			return null;
		}
		return StringUtil.null2Str(serviceTypeMap.get("serviceTypeName"));
	}
	public String getServiceTypeName(String serviceTypeId, String appType) {
		if (StringUtils.isEmpty(serviceTypeId) || StringUtils.isEmpty(appType)) {
			return null;
		}
		List<Map<String, Object>> serviceTypeList = getServiceTypeByAppType(appType, null);
		for (Map<String, Object> map : serviceTypeList) {
			String serviceTypeId_ = map.get("serviceType") == null ? "" : map.get("serviceType").toString();
			if (serviceTypeId.equals(serviceTypeId_)) {
				String serviceTypeName = map.get("servicName") == null ? "" : map.get("servicName").toString();
				return serviceTypeName;
			}
		}
		return null;
	}
//	/**
//	 * 查询所有serviceType
//	 * @return
//	 */
//	public List<Map<String, Object>> getAllServiceType(){		
//		@SuppressWarnings("unchecked")
//		List<Map<String, Object>> serviceTypeList =(List<Map<String, Object>>)commonCacheService.getObject(CacheConstants.CACHE_ALLSERVICETYPE);
//		if(serviceTypeList==null || serviceTypeList.size()==0){
//			serviceTypeList=serviceTypeDao.getAllServiceType();
//			commonCacheService.setObject(serviceTypeList,CacheConstants.CACHE_ALLSERVICETYPE);
//		}
//		return serviceTypeList;
//	}
	/**
	 * 查询所有serviceType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAllServiceTypeMap(){		
		Map<String, Object> allServiceTypeMap =null;
		try{
			allServiceTypeMap=(Map<String, Object>)commonCacheService.getObject(CacheConstants.CACHE_ALLSERVICETYPE);
		}catch(Exception e){
			commonCacheService.deleteObject(CacheConstants.CACHE_ALLSERVICETYPE);
		}
		if(allServiceTypeMap==null){
			allServiceTypeMap=new HashMap<String, Object>();
			List<Map<String, Object>> serviceTypeList=serviceTypeDao.getAllServiceType();
			for(Map<String, Object> map : serviceTypeList){
				String serviceTypeId=StringUtil.null2Str(map.get("serviceTypeId"));
				allServiceTypeMap.put(serviceTypeId, map);
			}
			commonCacheService.setObject(allServiceTypeMap,CacheConstants.CACHE_ALLSERVICETYPE);
		}
		return allServiceTypeMap;
	}


	/** 根据关键词搜索APP */
	@Override
	public JSONObject serachAppType(String keyword) throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000", "根据关键词搜索APP成功");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("keyword", keyword);
		Map<String, Object> resultMap = this.appKeyWordDao.serachAppType(paramMap);
		if (resultMap != null) {
			jsonObject.put("appInfo", resultMap);
		} else {
			if (this.appKeyWordDao.checkUserAppNameKeyWord(keyword) == 0) {
				// 搜索APP名称没结果的时候，记录用户搜索的关键词
				this.appKeyWordDao.insertUserAppNameKeyWord(keyword);
			}
		}
		return jsonObject;
	}

	/**
	 * 查找订单服务类型
	 * 
	 * @param paramMap
	 * @return
	 * @throws
	 */
	public Long selectOrderServiceType(Map<String, Object> paramMap) {
		return serviceTypeDao.selectOrderServiceType(paramMap);
	}

	/**
	 * 记录设备信息
	 */
	@Override
	public JSONObject recordDevice(String phone, String appType, Integer clientType, String version, String osVersion, String model, Integer userType, String clientId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000", "记录设备信息成功");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
		paramMap.put("appType", appType);
		paramMap.put("clientType", clientType);
		paramMap.put("version", version);
		paramMap.put("osVersion", osVersion);
		paramMap.put("model", model);
		paramMap.put("userType", userType);
		paramMap.put("clientId", clientId);
		Integer checkDevice = deviceDao.checkDeviceInfo(paramMap);
		if (checkDevice == 0) {
			deviceDao.recordDeviceInfo(paramMap);
		}
		return jsonObject;
	}

	// 检查服务对应的图标版本是否有更新
	private boolean hasNewImageVersion(Integer lastImageVersion, Integer cachedImageVersion) {
		return lastImageVersion > cachedImageVersion;
	}

	/**
	 * 获取融云token信息
	 */
	@Override
	public String getRongCloudToken(String uid, String clientFlag, String clientId, String name, String portraitUri) throws Exception {
		Map<String, Object> resultMap = null;
		String token = "";
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uid", uid);
		paramMap.put("clientId", clientId);
		if ("2".equals(clientFlag)) {
			// 用户
			resultMap = rongCloudDao.getRongCloudUserToken(paramMap);
		} else {
			// 商户
			resultMap = rongCloudDao.getRongCloudMerchantToken(paramMap);
		}
		if (resultMap != null && !resultMap.isEmpty()) {
			// 本地数据库
			token = StringUtil.null2Str(resultMap.get("token"));
		} else {
			// 请求融云服务器获取token
			token = RongCloudUtil.getRongCloudTokenFromProxy(uid, name, portraitUri);
			if (StringUtil.isNullStr(token)) {
				// 融云服务器响应异常，返回提示信息
				token = "error";
			} else {
				if ("proxyerror".equals(token)) {
					return token;
				} else {
					saveRongCloudToken(uid, clientFlag, token, clientId);
				}
			}
		}
		return token;
	}

	@Override
	public void saveRongCloudToken(String uid, String clientFlag, String token, String clientId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uid", uid);
		paramMap.put("clientFlag", clientFlag);
		paramMap.put("token", token);
		paramMap.put("clientId", clientId);
		if ("2".equals(clientFlag)) {
			// 用户
			rongCloudDao.delRongCloudUserToken(paramMap);
			rongCloudDao.saveRongCloudUserToken(paramMap);
		} else {
			// 商户
			rongCloudDao.delRongCloudMerchantToken(paramMap);
			rongCloudDao.saveRongCloudMerchantToken(paramMap);
		}
	}

	/** 用户反馈信息 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean feedback(Map<String, Object> paramMap, List<String> picturePaths) throws Exception {
		int result = 0;
		
		//如果是老版本，则将merchantId转换成userId
//		Long userId=StringUtil.nullToLong(paramMap.get("userId"));
//		int customerType = Integer.parseInt(paramMap.get("customerType") == null ? "0" : paramMap.get("customerType").toString());
//		if(userId==0L && customerType==1){//老版本且商户反馈
//			Long customerId=StringUtil.nullToLong(paramMap.get("customerId"));
//			paramMap.put("merchantId", customerId);
//			userId= feedbackDao.getBossUserIdByMerchantId(paramMap);
//		}
//		paramMap.put("userId", userId);
		// 保存反馈信息
		String content = (String) paramMap.get("content");
		content = StringUtil.formatDollarSign(content);
		paramMap.put("content", content);
		result = this.feedbackDao.addFeedback(paramMap);
		String feedbackId = paramMap.get("id") == null ? "0" : paramMap.get("id").toString();
		// 保存图片信息
		if (result > 0 && picturePaths.size() > 0) {
			paramMap = new HashMap<String, Object>();
			paramMap.put("feedbackId", feedbackId);
			paramMap.put("picturePaths", picturePaths);
			result = this.feedbackDao.addFeedbackAttachment(paramMap);
		}
		return result > 0;
	}

	@Override
	public Map<String, Object> getBasicInfoByUid(String uid, String clientFlag) {
		Map<String, Object> resultMap = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("uid", uid);
		try {
			if ("2".equals(clientFlag)) {
				// 用户
				if (StringUtil.nullToLong(uid)<9000000) {
						resultMap = commonDao.getBasicUserInfoByAssistantId(paramMap);
				}else{
						resultMap = commonDao.getBasicUserInfoByUid(paramMap);
				}
			} else {
				// 商户
				resultMap = commonDao.getBasicMerchantInfoByUid(paramMap);
			}
			if (resultMap != null) {
				String path = StringUtil.null2Str(resultMap.get("path"));
				resultMap.put("path", BusinessUtil.disposeImagePath(path));
			}
		} catch (Exception ex) {
			logger.error("用户或商户基本信息加载失败", ex);
			return resultMap;
		}

		return resultMap;
	}

//	/** 检查开通服务的城市 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public boolean checkServiceCity(String province, String city) throws Exception {
//		List<Map<String, Object>> serviceCityList=(List<Map<String, Object>>)commonCacheService.getObject(CacheConstants.SERVICE_CITY);
//		if(serviceCityList==null || serviceCityList.size()==0){
//			serviceCityList=serviceCityDao.getAllServiceCity();
//		}
//
//		for(Map<String, Object> map : serviceCityList ){
//			String province_=StringUtil.null2Str(map.get("province"));
//			String city_=StringUtil.null2Str(map.get("city"));
//			int isOpen=StringUtil.nullToInteger(map.get("isOpen"));
//			if(StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(province_) && province_.equals(province) &&
//			   StringUtil.isNotEmpty(city_) && StringUtil.isNotEmpty(city_) && city_.equals(city) &&
//			   isOpen==1){
//				return true;
//			}
//		}
//		return false;
//
//	}
    /**
     * 检查开通服务的城市
     *
     * @param province 省份
     * @param city 城市
     * @return boolean类型
     * @throws Exception
     */
    @Override
    public boolean checkServiceCity(String province, String city) throws Exception {
        boolean returnValue = false;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("province", province);
        paramMap.put("city", city);

        // is_open 与数据库定义一致（0:未开通; 1:已开通）
        // Object is_open = commonCacheService.getCommonCacheWithHGet(CacheConstants.SERVICE_CITY, province + "_" + city, province + "_" + city, 0);
        Object is_open = commonCacheService.getObject(CacheConstants.SERVICE_CITY + province, city);

        // 如果缓存中不存在，查库，然后放入缓存
        if (null == is_open) {
            // 每次只查一条记录
            int intOpenInDB = serviceCityDao.checkServiceCity(paramMap);
            // 如果数据库返回0表示没有记录或未开通，数据库中没有的也存储到缓存，防止频繁的查库；返回1表示已开通（数据库中存储的是已经开通）
            if (1 == intOpenInDB) {
                returnValue = true;
                // commonCacheService.setCommonCacheWithHGet(CacheConstants.SERVICE_CITY + province + "_" + city, province + "_" + city, "1", CacheConstants.SERVICE_CITY_EXPIRE);
                commonCacheService.setObject("1", CacheConstants.SERVICE_CITY + province, city);
            } else {
                // commonCacheService.setCommonCacheWithHGet(CacheConstants.SERVICE_CITY + province + "_" + city, province + "_" + city, "0", CacheConstants.SERVICE_CITY_EXPIRE);
                commonCacheService.setObject("0", CacheConstants.SERVICE_CITY + province, city);
            }
        }
        // 缓存中有记录，并且是1
        if (null != is_open && 1 == Integer.parseInt(is_open.toString())) {
            returnValue = true;
        }
        return returnValue;
    }

	@Override
	public JSONObject getServiceParas(String appType, String serviceType) throws Exception {
		JSONObject jsonObject = (JSONObject) commonCacheService.getObject(CacheConstants.SERVICE_TYPE_PARAS, appType, serviceType);
		if (jsonObject == null) {
			jsonObject = new ResultJSONObject("000", "获得服务类型参数成功");
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("appType", appType);
			paramMap.put("serviceType", serviceType);
			List<Map<String, Object>> paras = orderObjectDao.getServiceParas(paramMap);
			for (int i = 0; i < paras.size(); i++) {
				BusinessUtil.disposePath(paras.get(i), "icon");
				Long moderId = (Long) paras.get(i).get("id");
				String colItems = (String) paras.get(i).get("colItems");
				if (StringUtil.isNotEmpty(colItems)) {
					paras.get(i).put("colItems", colItems.split(","));
				} else {
					paras.get(i).put("colItems", new String[0]);
				}

				// 获得子控件
				List<Map<String, Object>> subPara = orderObjectDao.getServiceSubParas(moderId);
				for (int subIndex = 0; subIndex < subPara.size(); subIndex++) {
					BusinessUtil.disposePath(subPara.get(subIndex), "icon");
					String subColItems = (String) subPara.get(subIndex).get("colItems");
					if (StringUtil.isNotEmpty(subColItems)) {
						subPara.get(i).put("colItems", subColItems.split(","));
					} else {
						subPara.get(i).put("colItems", new String[0]);
					}
				}

				// 获得关联控件
				List<Map<String, Object>> cascadePara = orderObjectDao.getServiceCascadeParas(moderId);
				for (int cascadeIndex = 0; cascadeIndex < cascadePara.size(); cascadeIndex++) {
					Long cascadeId = (Long) cascadePara.get(cascadeIndex).get("cascadeId");
					List<Map<String, Object>> cascadeItems = orderObjectDao.getServiceCascadeItems(cascadeId);
					cascadePara.get(cascadeIndex).put("cascadeItems", cascadeItems);
				}

				paras.get(i).put("subPara", subPara);
				paras.get(i).put("cascadePara", cascadePara);
			}
			jsonObject.put("paras", paras);
			Date nowTime = new Date();
			SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
			jsonObject.put("version", time.format(nowTime));
			commonCacheService.setObject(jsonObject, CacheConstants.SERVICE_TYPE_PARAS, appType, serviceType);
		}
		return jsonObject;
	}

	@Override
	public JSONObject getUserHomeBanner() throws Exception {
		Object cachedObject = commonCacheService.getObject(CacheConstants.USER_HOME_PAGE_BANNER);
		JSONObject jsonObject = null;
		
		if (cachedObject!=null)
				jsonObject = (JSONObject)cachedObject;
		
		if (jsonObject!=null){
			long lastFetchTime= jsonObject.getLong("fetchTime");
			long now =System.currentTimeMillis();
			//如果缓存存在，且是规定有效期内生成的，则直接返回。
			if ((now -lastFetchTime)/1000 < CacheConstants.USER_HOME_PAGE_BANNER_EXPIRTIME ){
				return jsonObject;
			}
		}
		
		
		//缓存不存在或已过有效期（默认十分钟），则从数据库中查询返回。
	   List<Map<String,Object>> sliderInfos = userSliderInfoDao.getUserBannerList();
	   
	   BusinessUtil.disposeManyPath(sliderInfos, "pics_path");
	   jsonObject = new ResultJSONObject("000", "获得用户首页Banner成功");
	   
	   
	   
	   jsonObject.put("sliders", sliderInfos);
	   jsonObject.put("fetchTime", System.currentTimeMillis());
	   commonCacheService.setObject(jsonObject, CacheConstants.USER_HOME_PAGE_BANNER_EXPIRTIME, CacheConstants.USER_HOME_PAGE_BANNER);
	   return jsonObject;
	}
	
	
	
	@Override
	public JSONObject getMerchantHomeBanner() throws Exception {
		Object cachedObject = commonCacheService.getObject(CacheConstants.MERCHANT_HOME_PAGE_BANNER);
		JSONObject jsonObject = null;
		
		if (cachedObject!=null)
				jsonObject = (JSONObject)cachedObject;
		
		if (jsonObject!=null){
			long lastFetchTime= jsonObject.getLong("fetchTime");
			long now =System.currentTimeMillis();
			//如果缓存存在，且是规定有效期内生成的，则直接返回。
			if ((now -lastFetchTime)/1000 < CacheConstants.MERCHANT_HOME_PAGE_BANNER_EXPIRTIME ){
				return jsonObject;
			}
		}
		
		
		//缓存不存在或已过有效期（默认十分钟），则从数据库中查询返回。
	   List<Map<String,Object>> sliderInfos = userSliderInfoDao.getMerchantBannerList();
	   
	   BusinessUtil.disposeManyPath(sliderInfos, "pics_path");
	   jsonObject = new ResultJSONObject("000", "获得商户首页Banner成功");
	   
	   
	   
	   jsonObject.put("sliders", sliderInfos);
	   jsonObject.put("fetchTime", System.currentTimeMillis());
	   commonCacheService.setObject(jsonObject, CacheConstants.MERCHANT_HOME_PAGE_BANNER_EXPIRTIME, CacheConstants.MERCHANT_HOME_PAGE_BANNER);
	   return jsonObject;
	}

	@Override
	public JSONObject getUserHomePageRecommend(String province,String city) throws Exception {
		Object cachedObject = commonCacheService.getObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,province,city);
		JSONObject jsonObject = null;
		
		if (cachedObject!=null)
				jsonObject = (JSONObject)cachedObject;
		
		if (jsonObject!=null){
			long lastFetchTime= jsonObject.getLong("fetchTime");
			long now =System.currentTimeMillis();
			//如果缓存存在，且是规定有效期内生成的，则直接返回。
			if ((now -lastFetchTime)/1000 < CacheConstants.USER_HOME_PAGE_RECOMMEND_EXPIRTIME ){
				return jsonObject;
			}
		}
	
	
		
		//缓存不存在或已过有效期（默认十分钟），则从数据库中查询返回。
		 Map<String,Object> param = new HashMap<String,Object>();
		 param.put("province", province);
		 param.put("city",city);
		 List<Map<String,Object>> tradeInfos = userRecommendDao.getUserHomeCommend(param);
		 
		 if (tradeInfos==null || tradeInfos.size()<1){
			  if ((!StringUtil.isNullStr("province")) && (!StringUtil.isNullStr("city"))){
	
				  
				  //1. 按省，市条件未找到；则先扩大范围为按省查
				  	param.put("city","");
				  	Object cachedProvinceObject = commonCacheService.getObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,province,"original");
				  	if (cachedProvinceObject!=null){
				  			tradeInfos = (List<Map<String,Object>>)cachedProvinceObject;
				  	}
				  	else {

				  		tradeInfos = userRecommendDao.getUserHomeCommend(param);
				  		
				  		if (tradeInfos==null || tradeInfos.size()<1){
					  		//2. 按省、市条件未找到，扩大范围到全国
					  		param.put("province","全国");
						    param.put("city",null);
						    Object cachedCountryObject = commonCacheService.getObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,"全国","original");
						    if (cachedCountryObject!=null){
						    	tradeInfos = (List<Map<String,Object>>)cachedCountryObject;
						    }else{
						    	tradeInfos = userRecommendDao.getUserHomeCommend(param);
						  		commonCacheService.setObject(tradeInfos, CacheConstants.USER_HOME_PAGE_RECOMMEND,"全国","original");
						    }
						  
					  	}else{
					  		 commonCacheService.setObject(tradeInfos, CacheConstants.USER_HOME_PAGE_RECOMMEND,province,"original");
					  	}
				  	}
			  }
			  else if ((!StringUtil.isNullStr("province")) && StringUtil.isNullStr("city")){
				  //按省条件未找到；则扩大范围为全国
				  	param.put("province","全国");
				    param.put("city",null);
				    Object cachedCountryObject = commonCacheService.getObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,"全国","original");
				    if (cachedCountryObject!=null){
				    	tradeInfos = (List<Map<String,Object>>)cachedCountryObject;
				    }else{
					    tradeInfos = userRecommendDao.getUserHomeCommend(param);
				    	commonCacheService.setObject(tradeInfos, CacheConstants.USER_HOME_PAGE_RECOMMEND,"全国","original");
				    }
			  }
			 
		 }
		 BusinessUtil.disposeManyPath(tradeInfos, "pics_path");
		 
		 
		 //恢复查询条件 ---查询个性化推荐
		 param.put("province", province);
		 param.put("city",city);
		 List<Map<String,Object>> personInfo = userRecommendDao.getUserHomePersonCommend(param);
		 if (personInfo==null || personInfo.size()<1){
			  if ((!StringUtil.isNullStr("province")) && (!StringUtil.isNullStr("city"))){
				  //1. 按省，市条件未找到；则先扩大范围为按省查
				  	param.put("city","");
					Object cachedPersonObject = commonCacheService.getObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,province,"originalPerson");
					if (cachedPersonObject!=null){
						personInfo = (List<Map<String,Object>>)cachedPersonObject;
					}else{
							personInfo = userRecommendDao.getUserHomePersonCommend(param);
						  	if (personInfo==null || personInfo.size()<1){
						  			//2. 按省、市条件未找到，扩大范围到全国
						  			param.put("province","全国");
						  			param.put("city",null);
						  			cachedPersonObject = commonCacheService.getObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,"全国","originalPerson");
						  			if (cachedPersonObject!=null){
						  				personInfo =  (List<Map<String,Object>>)cachedPersonObject;
						  			}else{
						  				personInfo = userRecommendDao.getUserHomePersonCommend(param);
						  				getTopParentCatalog(personInfo);
						  				commonCacheService.setObject(personInfo, CacheConstants.USER_HOME_PAGE_RECOMMEND,province,"originalPerson");
						  			}
						  	}else{
						  			getTopParentCatalog(personInfo);
						  		 	commonCacheService.setObject(personInfo, CacheConstants.USER_HOME_PAGE_RECOMMEND,province,"originalPerson");
						  	}
					}
				  		
			  }
			  else if ((!StringUtil.isNullStr("province")) && StringUtil.isNullStr("city")){
				  //按省条件未找到；则扩大范围为全国
				  	param.put("province","全国");
				    param.put("city",null);
					Object cachedPersonObject = commonCacheService.getObject(CacheConstants.USER_HOME_PAGE_RECOMMEND,"全国","originalPerson");
					if (cachedPersonObject!=null){ 
							personInfo = (List<Map<String,Object>>)cachedPersonObject;
					}else{
							personInfo = userRecommendDao.getUserHomePersonCommend(param);
							getTopParentCatalog(personInfo);
							commonCacheService.setObject(personInfo, CacheConstants.USER_HOME_PAGE_RECOMMEND,province,"originalPerson");
					}
			  }
		 }else{
			 	getTopParentCatalog(personInfo);
		 }
		 
		 BusinessUtil.disposeManyPath(personInfo, "pics_path");
		 
		 
		 
		  jsonObject = new ResultJSONObject("000", "获得用户首页推荐成功");
		  jsonObject.put("tradeInfo", tradeInfos);
		  jsonObject.put("personInfo", personInfo);
		  
		  jsonObject.put("fetchTime", System.currentTimeMillis());
		  commonCacheService.setObject(jsonObject, CacheConstants.USER_HOME_PAGE_RECOMMEND_EXPIRTIME, CacheConstants.USER_HOME_PAGE_RECOMMEND,province,city);
		   return jsonObject;
	}
	


	public JSONObject getUserNearBy(double longitude,double latitude,double searchRange,int pageNo, String condition) {		
		//--begin 经纬度精确到小数点后三位，三位内同样的精度直接取缓存数据。
		 java.text.DecimalFormat   df=new   java.text.DecimalFormat("##.###"); 
		
		String longKey = df.format(longitude);
		String latKey =	df.format(latitude);	
		
		StringBuffer cacheKey= new StringBuffer();
		cacheKey.append(CacheConstants.USER_HOME_PAGE_NEARBY).append("_").append(longKey).append("_").append(latKey);
		
		List<Map<String, Object>> merchantInfos =null;
		
		Object cachedMerchantInfos = commonCacheService.getObject(cacheKey.toString());
		
		if (cachedMerchantInfos==null){
				merchantInfos = searchMerchantNearBy(longitude, latitude, searchRange);
				commonCacheService.setObject(merchantInfos,CacheConstants.USER_HOME_PAGE_NEARBY, longKey,latKey);
				GenericCacheServiceImpl generalService = (GenericCacheServiceImpl)commonCacheService;
				generalService.setExpire(cacheKey.toString(), CacheConstants.USER_HOME_PAGE_NEARBY_EXPIRTIME);
		}else{
			   merchantInfos = (List<Map<String, Object>>) cachedMerchantInfos;
		}
		
		// --end   经纬度精确到小数点后三位，三位内同样的精度直接取缓存数据。
		
		
		List<Map<String,Object>> pageResult = new ArrayList<Map<String,Object>>();
		
		Integer totalCount= constructResultFromCachedMerchants(merchantInfos,pageResult,condition,pageNo);
		
		cutdownServiceNames(merchantInfos,4);
		
		JSONObject jsonObject = new ResultJSONObject("000", "获得用户首页附近商家推荐成功");
		jsonObject.put("merchantInfo", pageResult);
		jsonObject.put("totalPage", totalCount);
		
		return jsonObject;
	}



	@Override
	public JSONObject getHotSearch() throws Exception {
		JSONObject resultObject = new ResultJSONObject("000", "热门搜索---查看全部获取成功");
		
		Map<String,String> appicons = getAllAppicons();
		
		Object cachedHotSearch =  commonCacheService.getObject(CacheConstants.USER_HOME_HOTSEARCH_ALL_HOT);
		
		
		//处理热门搜素
		Map<String,Object>  hotSearch = new HashMap<String,Object>();
		
		if (cachedHotSearch==null) {
				List<Map<String,Object>> hotSearchList = userRecommendDao.getHotSearch();
			 
				if (appicons.containsKey(this.HOT_CATALOG_ID+"_smallIcon")){
				 hotSearch.put("smallIcon", appicons.get(this.HOT_CATALOG_ID+"_smallIcon"));
				}else {
				 hotSearch.put("smallIcon", "");
				}
			 
				if (appicons.containsKey(this.HOT_CATALOG_ID+"_bigIcon")){
				 hotSearch.put("bigIcon", appicons.get(this.HOT_CATALOG_ID+"_bigIcon"));
				}else {
				 hotSearch.put("bigIcon", "");
				}
				hotSearch.put("title", "热门搜索");
				
				getTopParentCatalog(hotSearchList);
				
				hotSearch.put("serviceNames", hotSearchList);
				commonCacheService.setObject(hotSearch,CacheConstants.USER_HOME_HOTSEARCH_ALL_HOT);
		}else{
					
				hotSearch = (Map<String,Object>)cachedHotSearch ;
		}
		
		resultObject.put("hotSearch", hotSearch);
		
		
		
		//处理行业
		Object cachedTradeInfos =  commonCacheService.getObject(CacheConstants.USER_HOME_HOTSEARCH_ALL_TRADE);
		
		List<Map<String,Object>> tradeInfos =null;
		
		if (cachedTradeInfos == null) {
			tradeInfos = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> catalogs = getCataLogs();
			
			for(Map<String,Object> catalog:catalogs){
						if (catalog==null){
							continue;
						}
						Map<String,Object> upCatalog = new HashMap<String,Object>();
						upCatalog.put("title", catalog.get("name").toString());
						upCatalog.put("id", catalog.get("id"));
						upCatalog.put("alias", catalog.get("alias"));
						String catalogId= catalog.get("id").toString();
						 if (appicons.containsKey(catalogId+"_smallIcon")){
							 upCatalog.put("smallIcon", appicons.get(catalogId+"_smallIcon"));
						 }else {
							 upCatalog.put("smallIcon", "");
						 }
						 
						 if (appicons.containsKey(catalogId+"_bigIcon")){
							 upCatalog.put("bigIcon", appicons.get(catalogId+"_bigIcon"));
						 }else {
							 upCatalog.put("bigIcon", "");
						 }
						 
						 List<Map<String,Object>> servicesNames=getAllServicesByCatalogId( catalog.get("id").toString());
						
						 upCatalog.put("totalCount",   (servicesNames==null?0:servicesNames.size())); 
						 upCatalog.put("serviceNames", getTopnServices(servicesNames,8));
						 tradeInfos.add(upCatalog);
			}
		
			commonCacheService.setObject(tradeInfos,CacheConstants.USER_HOME_HOTSEARCH_ALL_TRADE);
				
		}else{

				tradeInfos = (List<Map<String,Object>>)cachedTradeInfos;

			}
		resultObject.put("tradeInfo", tradeInfos);
		
		
		
		//处理个性服务
		Object cachedCustomInfos =  commonCacheService.getObject(CacheConstants.USER_HOME_HOTSEARCH_ALL_CUSTOM);
		Map<String,Object> customInfo = new HashMap<String,Object>();
		if (cachedCustomInfos ==null){
				Map<String,Object> catalog = getPersonCatalogAndServices();
				customInfo.put("title", "个性服务");
				 if (appicons.containsKey(this.HOT_CUSTOM_ID+"_smallIcon")){
					 customInfo.put("smallIcon", appicons.get(this.HOT_CUSTOM_ID+"_smallIcon"));
				 }else {
					 customInfo.put("smallIcon", "");
				 }
				 
				 if (appicons.containsKey(this.HOT_CUSTOM_ID+"_bigIcon")){
					 customInfo.put("bigIcon", appicons.get(this.HOT_CUSTOM_ID+"_bigIcon"));
				 }else {
					 customInfo.put("bigIcon", "");
				 }
				 List<Map<String,Object>> servicesNames= getAllServicesByCatalogId(catalog.get("id").toString());
				 customInfo.put("totalCount",   (servicesNames==null?0:servicesNames.size())); 
				 customInfo.put("serviceNames", getTopnServices(servicesNames,8));
				
		}else{
			customInfo = (Map<String,Object>)cachedCustomInfos;
			
		}
		resultObject.put("customInfo", customInfo);
		
		
		//处理其它
		Object cachedExternal = commonCacheService.getObject(CacheConstants.USER_HOME_HOTSEARCH_ALL_EXTERNAL);
		Map<String,Object> externalInfo = new HashMap<String,Object>();
		if (cachedExternal==null){
				externalInfo.put("title","其它");
					if (appicons.containsKey(this.HOT_OTHER_ID+"_smallIcon")){
						externalInfo.put("smallIcon", appicons.get(this.HOT_OTHER_ID+"_smallIcon"));
					}else {
						externalInfo.put("smallIcon", "");
					}
			 
					if (appicons.containsKey(this.HOT_OTHER_ID+"_bigIcon")){
						externalInfo.put("bigIcon", appicons.get(this.HOT_OTHER_ID+"_bigIcon"));
					}else {
						externalInfo.put("bigIcon", "");
					}
					List<Map<String,Object>> externalInfoList = userRecommendDao.getExternalInfos();
					externalInfo.put("serviceNames", externalInfoList);
		}else{
				externalInfo = (Map<String,Object>)cachedExternal;
		}
		
		resultObject.put("externalInfo", externalInfo);
		return resultObject;
	}
	
	
	@Override
	public JSONObject getTags() throws Exception {
			JSONObject resultObject = new ResultJSONObject("000", "商户入驻行业标签及自定标签部获取成功");
		
			Object cachedTradeTags = commonCacheService.getObject(CacheConstants.MERCHANT_TRADE_TAG);
			List<Map<String,Object>> tradeTags=null;
			if (cachedTradeTags==null){
				 tradeTags =  iServiceTagDao.getSystemTags();
				 commonCacheService.setObject(tradeTags, CacheConstants.MERCHANT_TRADE_TAG);
			}else {
				  tradeTags = (List<Map<String,Object>>) cachedTradeTags;
			}
			resultObject.put("tradeTag", tradeTags);
			
		/*	自定义标签 不要了---- 2016.3.19
			int maxTagNum = 25; //总共显示的最大标签数
			int cutomTagNum = maxTagNum;  //剩下自定义标签需要显示的数目
			if (tradeTags!=null){
				cutomTagNum = maxTagNum-tradeTags.size(); 
			}
			
			
			Object cachedCustomTags = commonCacheService.getObject(CacheConstants.MERCHANT_CUSTOM_TAG);
			List<Map<String,Object>> customTags=null;
			if (cachedCustomTags==null){
					customTags = iServiceTagDao.getCustomTags();
					commonCacheService.setObject(customTags,CacheConstants.MERCHANT_CUSTOM_TAG);
			}else{
					customTags = (List<Map<String,Object>>)cachedCustomTags;
			}
			
			List<Map<String,Object>> showCustomTag = new ArrayList<Map<String,Object>> (); //要显示的自定义标签
			if (cutomTagNum >0  && customTags!=null){
					 for (Map<String,Object> tag:customTags){
						 	showCustomTag.add(tag);
						 	cutomTagNum--;
						 	if (cutomTagNum<1){
						 		break;
						 	}
					 }
			}
			
			resultObject.put("customTag", showCustomTag);
			*/
				
			return resultObject;
	}
	
	
	
	private List<Map<String, Object>> searchMerchantNearBy(double longitude,
			double latitude, double searchRange) {
		List<Map<String,Object>> merchantInfos= new ArrayList<Map<String,Object>>();
		
		/*
		
		Map<String, Double> locationInfo = PositionUtil.calcTopLfRgBtCoordinate(searchRange, longitude, latitude);
		Map<String,Object> paramMap= new HashMap<String,Object>();
		paramMap.put("leftLongitude", locationInfo.get("leftLongitude"));
		paramMap.put("rightLongitude", locationInfo.get("rightLongitude"));
		paramMap.put("buttomLatitude", locationInfo.get("buttomLatitude"));
		paramMap.put("topLatitude", locationInfo.get("topLatitude"));
		List<Map<String,Object>> merchantInfos=userSliderInfoDao.getUserHomePageByRange(paramMap);
		*/
		
		//先根据搜索引擎限定 范围内的商户，再取商户信息。
		String   searchIds = elasticSearchService.getMerchantIds( (Math.round(searchRange)*1000)+"m", ""+latitude, ""+longitude, 200);
		if (searchIds.length()<1){
			  return merchantInfos;  //附近无商家
		}
	
		Map<String,Object> param =new HashMap<String,Object>();
		param.put("ids", searchIds);	
		merchantInfos=userSliderInfoDao.getUserHomePageByIds(param);
		
		
		
		if (merchantInfos!=null && merchantInfos.size()>0){
			  String merchantIds=getAllMerchants(merchantInfos);
			 
			//  3.22 新需求不显示TAG,显示的是服务项目  
			//  Map<String,Object> merchantTags= getMerchantTags(merchantIds);
			  
			  Map<String,Object> merchantServiceItemNames= getMerchantServiceItemNames(merchantIds);
			  
			  for(Map<String,Object> merchantInfo:merchantInfos){
				  	BusinessUtil.disposeManyPath(merchantInfo, "icoPath");
				  
					int count = (int)merchantInfo.get("totalCount");
					int re = 5;
					String score="5.0分";
					if (count != 0) {
						Integer totalAttitudeEvaluation = Integer.parseInt(merchantInfo.get("totalAttitudeEvaluation") == null ? "0" : merchantInfo.get("totalAttitudeEvaluation") + "");
						Integer totalQualityEvaluation = Integer.parseInt(merchantInfo.get("totalQualityEvaluation") == null ? "0" : merchantInfo.get("totalQualityEvaluation") + "");
						Integer totalSpeedEvaluation = Integer.parseInt(merchantInfo.get("totalSpeedEvaluation") == null ? "0" : merchantInfo.get("totalSpeedEvaluation") + "");
						// 总服务态度评价+总服务质量评价+总服务速度评价
						Integer totalEvaluation = totalAttitudeEvaluation + totalQualityEvaluation + totalSpeedEvaluation;
						score = new BigDecimal(totalEvaluation).divide(new BigDecimal(count).multiply(new BigDecimal(3)), 1, BigDecimal.ROUND_DOWN).toPlainString().trim()+"分";
						
						// 星级
						BigDecimal starLevel = new BigDecimal(totalEvaluation).divide(new BigDecimal(count).multiply(new BigDecimal(3)), 0, BigDecimal.ROUND_HALF_UP);
						re = starLevel.intValue();
					}
					if (re > 5) {
						re = 5;
					}
					if (re < 0) {
						re = 0;
					}
					merchantInfo.put("star", re);
					merchantInfo.put("score", score);
					String appType = merchantInfo.get("app_type").toString();
					if (appType.equals("gxfw")){
						merchantInfo.put("merchantType", 2);
					}else{
						merchantInfo.put("merchantType", 1);
					}
					merchantInfo.remove("app_type");
					
					/** 判断企业认证类型 1-企业认证 2-个人认证 0-没有认证 */
					if ((Long) merchantInfo.get("enterpriseAuth") > 0) {
						merchantInfo.put("auth", 1);
					} else if ((Long) merchantInfo.get("personalAuth") > 0) {
						merchantInfo.put("auth", 2);
					} else {
						merchantInfo.put("auth", 0);
					}
					
					String merchantId=merchantInfo.get("id").toString();
					
					/*
					if (merchantTags.containsKey(merchantId)){
						merchantInfo.put("tags", merchantTags.get(merchantId));
					}else{
						merchantInfo.put("tags", "");
					}*/
					
					if (merchantServiceItemNames.containsKey(merchantId)){
						merchantInfo.put("serviceNames", merchantServiceItemNames.get(merchantId));
					}else{
						merchantInfo.put("serviceNames", "");
					}
					
						
					if (StringUtil.isNotEmpty(merchantInfo.get("longitude")) && StringUtil.isNotEmpty(merchantInfo.get("latitude")))
					 {
						// 如果不为空则查询距离
						Double distance = LocationUtil.getDistance(Double.parseDouble(StringUtil.null2Str(merchantInfo.get("longitude"))),
						Double.parseDouble(StringUtil.null2Str(merchantInfo.get("latitude"))), longitude,latitude);
						Long distanceValue = Math.round(distance);
						merchantInfo.put("distance", distanceValue);
					}
					
					 //删除前端不用的字段
					  merchantInfo.remove("totalSpeedEvaluation");
					  merchantInfo.remove("enterpriseAuth");
					  merchantInfo.remove("personalAuth");
					  merchantInfo.remove("totalQualityEvaluation");
					  merchantInfo.remove("totalCount");
					  merchantInfo.remove("totalAttitudeEvaluation");
					  
			  }
			  
			  
				DistansComparator cp = new DistansComparator("distance");
				Collections.sort(merchantInfos, cp);
				
				
				
				//距离折算
				for (Map<String,Object> merchantInfo:merchantInfos) {
					String strDistance = merchantInfo.get("distance").toString();
					String formatDistance="";
					if (strDistance!= null && !strDistance.trim().equals("")) {
							int distance = Integer.parseInt(strDistance);
							if (distance >= 1000) {
									double kmDistance = ((double) distance) / 1000;
									BigDecimal bd = new BigDecimal(kmDistance);
									kmDistance = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
									formatDistance = "距离" + kmDistance + "公里";
							} else {
								formatDistance = "距离" + Math.round(distance) + "米";
							}
									merchantInfo.put("distance", formatDistance);
					} 
				}
		}
		return merchantInfos;
	}

 //生成 merchant_id-->service type name  HashMap	
 private Map<String, Object> getMerchantServiceItemNames(String merchantIds) {
	 Map<String,Object>   results = new HashMap<String,Object>();
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("merchantIds", merchantIds);
		List<Map<String,Object>>  namesList= userSliderInfoDao.getMerchantServiceName(param);
		if (namesList!=null && namesList.size()>0){
				for(Map<String,Object> name:namesList){
						results.put(name.get("merchant_id").toString(),name.get("serviceTypeNames"));
				}
			
		}
		return results;
	}

//生成 merchant_id-->tags HashMap	
  private Map<String, Object> getMerchantTags(String merchantIds) {
			Map<String,Object>   results = new HashMap<String,Object>();
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("merchantIds", merchantIds);
			List<Map<String,Object>>  tagList= userSliderInfoDao.getMerchantTags(param);
			if (tagList!=null && tagList.size()>0){
					for(Map<String,Object> tag:tagList){
							results.put(tag.get("merchant_id").toString(),tag.get("tags"));
					}
				
			}
			return results;
	}
  
  
  
	// 根据缓存的附近商家来过滤复合条件的记录。
	private int constructResultFromCachedMerchants(List<Map<String, Object>> merchantInfos, List<Map<String, Object>> resultList, String searchCondition, int pageNo) {

		List<Map<String, Object>> filterMerchants = new ArrayList<Map<String, Object>>();

		if (merchantInfos == null || merchantInfos.size() < 1) {
			return 0;
		}

		if (searchCondition!=null){
			searchCondition = searchCondition.trim();
		}
			 
		boolean hasSeachCondition = (searchCondition==null || searchCondition.length()<1)?false:true; 


		for (Map<String, Object> eachMerchant : merchantInfos) {
			if (!hasSeachCondition){
				filterMerchants.add(eachMerchant);
				continue;
			}
			
			
			String merchantName = "";
			if (eachMerchant.get("name")!=null)
				merchantName=eachMerchant.get("name").toString();
			
			String merchantTags ="";
			if (eachMerchant.get("tags")!=null)
				merchantTags = eachMerchant.get("tags").toString();
			
			String serviceItemNames ="";
			if (eachMerchant.get("serviceNames")!=null)
				serviceItemNames = eachMerchant.get("serviceNames").toString();
			
			if (merchantName.indexOf(searchCondition)>-1 || merchantTags.indexOf(searchCondition)>-1 || serviceItemNames.indexOf(searchCondition)>-1){
					filterMerchants.add(eachMerchant);
			}
		}
	
		if (filterMerchants.size() < 1 || pageNo * Constant.PAGESIZE > filterMerchants.size()) {
			return 0;
		} else {
			for (int num = 0, startIndex = pageNo * Constant.PAGESIZE; num < Constant.PAGESIZE && startIndex < filterMerchants.size(); num++, startIndex++) {
				resultList.add(filterMerchants.get(startIndex));
			}
			return 1 + (filterMerchants.size() - 1) / Constant.PAGESIZE;
		}

	}

//	@Override
//	public List<Map<String, Object>> selectUserPushList(Long userId) throws Exception {
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("userId", userId);
//		return pushDao.selectAcceptUserPushList(paramMap);
//	}
//
//	@Override
//	public List<Map<String, Object>> selectAcceptUserPushList(String appType,
//			String serviceType) throws Exception {
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("appType", appType);
//		return pushDao.selectAcceptUserPushList(paramMap);
//	}
//
//	@Override
//	public void deleteUserPushExceptDeviceId(Long userId)
//			throws Exception {
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("userId", userId);
//		pushDao.deleteUserPushExceptDeviceId(paramMap);
//		return ;
//	}
//
//	@Override
//	public void insertUserPush(Long userId, int clientType,String clientId, String deviceId)
//			throws Exception {
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("userId", userId);
//		paramMap.put("clientType", clientType);
//		paramMap.put("clientId", clientId);
//		paramMap.put("deviceId", deviceId);
//		pushDao.insertUserPush(paramMap);
//		return ;
//	}
	
	 private String getAllMerchants(List<Map<String,Object>> merchantsInfo){
		 StringBuffer merchantIds = new StringBuffer();
		 for(Map<String,Object> merchantInfo:merchantsInfo){
			  merchantIds.append(",").append(merchantInfo.get("id"));
		 }
		 merchantIds.deleteCharAt(0);
		 return merchantIds.toString();
	 }

	
	//获取用户热门搜索页所有应用的ICON，形成  appname_smallIcon     picPath;  appName_bigIcon picPath;
		 private Map<String,String> getAllAppicons(){
			 		Map<String,String> result = new HashMap<String,String>();
			 		
			 		List<Map<String,Object>> hotAppIcons = userRecommendDao.getAllHotIcons();
					BusinessUtil.disposeManyPath(hotAppIcons, "pics_path");
			 		
					if (hotAppIcons!=null && hotAppIcons.size()>0){
						  for (Map<String,Object> hotAppIcon:hotAppIcons){
							  	String iconPath="";
							  	if (hotAppIcon.get("pics_path")!=null){
							  		 iconPath = hotAppIcon.get("pics_path").toString();
							  	}
							    if ((Integer)hotAppIcon.get("size")==1)
							    	result.put(hotAppIcon.get("catalog_id").toString()+"_smallIcon", iconPath);
							    else
							    	result.put(hotAppIcon.get("catalog_id").toString()+"_bigIcon", iconPath);
						  }
						
					}
			 		return result;
		 }
		 
	@Override
	public List<Map<String, Object>> getAppInfosByAppType(String appTypes) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("appTypes", appTypes);
		return userRecommendDao.getAppInfoByAppType(param);
	}
	 
	
	private void getTradeInfosFromCache(Object cachedProvinceObject, List<Map<String,Object>> tradeInfos) {
		JSONObject cachedJSON = (JSONObject) cachedProvinceObject;
		JSONArray  tradeJsonArray = cachedJSON.getJSONArray("tradeInfo");
		if  (tradeJsonArray!=null && tradeJsonArray.size()>0){
				 int totalLength = tradeJsonArray.size();
				 for (int i=0;i<totalLength;i++){
					  Map<String,Object> eachRecord=new HashMap<String,Object>();
					  JSONObject eachJsonObject = tradeJsonArray.getJSONObject(i);
					  eachRecord.put("title", eachJsonObject.get("title"));
					  eachRecord.put("detail", eachJsonObject.get("detail"));
					  eachRecord.put("color", eachJsonObject.get("detail"));
					  eachRecord.put("app_type", eachJsonObject.get("app_type"));
					  eachRecord.put("service_type", eachJsonObject.get("service_type"));
					  eachRecord.put("is_hot", eachJsonObject.get("is_hot"));
					  eachRecord.put("pics_path", eachJsonObject.get("pics_path"));
				 }
			
		}
		
	}


	
	public List<Map<String,Object>> getMerchantCataLogs(){
				Object  cachedCatalogs =  commonCacheService.getObject(CacheConstants.MERCHANT_CATALOG_TOP);
				List<Map<String,Object>>    catalogs=null;
				if  (cachedCatalogs!=null){
							catalogs = (ArrayList)cachedCatalogs;
				}else{
						catalogs = icataLogDao.getMerchantatalog();
						if (catalogs==null){
							catalogs = new ArrayList<Map<String,Object>>();
						}else{
							BusinessUtil.disposeManyPath(catalogs, "iconPath");
							BusinessUtil.disposeManyPath(catalogs, "bigIconPath");
							
						}
						commonCacheService.setObject(catalogs,CacheConstants.MERCHANT_CATALOG_TOP);
				}
				return catalogs;
	}
	
	
	public List getCataLogs(){
			Object  cachedCatalogTree =  commonCacheService.getObject(CacheConstants.CATALOG);
			List  catalogTree=null;
			
		    if  (cachedCatalogTree!=null){
		    		catalogTree = (ArrayList)cachedCatalogTree;
		    		long fetchTime = (long) commonCacheService.getObject(CacheConstants.CATALOG_FETCHTIME);
		    		long currTime = System.currentTimeMillis();
		    		//缓存未过期，可直接返回
		    		if ( (currTime-fetchTime)/1000 < CacheConstants.CATALOG_EXPIRTIME)
		    			 return catalogTree;
		    }
			 
			List<Map<String,Object>> catalogs = icataLogDao.getCataLogs(0);
			catalogTree= new ArrayList();
			if (catalogs!=null && catalogs.size()>0){
					for(Map<String,Object> catalog: catalogs){
						     if (catalog==null || catalog.get("alias").equals("gxfw")){
						    	 continue;
						     }
							 catalogTree.add(getSubTree(catalog,false));
					}
			}
			commonCacheService.setObject(catalogTree, CacheConstants.CATALOG);
			commonCacheService.setObject(System.currentTimeMillis(), CacheConstants.CATALOG_FETCHTIME);
			return catalogTree;
	}

	

	
	
	private Object getSubTree(Map<String,Object> catalog,boolean loadService) {
		 int id = (int) catalog.get("id");
		 int isLeaf= (int) catalog.get("leaf");
		 List  result = new ArrayList();
		 if (isLeaf==0){
			 List<Map<String,Object>> children = icataLogDao.getCataLogs(id);
			 if (children!=null && children.size()>0){
				 for(Map<String,Object> child: children){
					    if (child==null){
					    	continue;
					    }
					 	result.add(getSubTree(child,loadService));
				 } 
			 }
		 }
		 if (loadService){
				  //加载叶子级的分类对应的服务项目列表
				    Map<String,Object> param = new HashMap<String,Object>();
				    param.put("catalogIds", id);
				  	List<Map<String,Object>> serviceItems= icataLogDao.getServiceTypeByCatalogs(param);
				  	if (serviceItems!=null && serviceItems.size()>0){
				  		       for(Map<String,Object> serviceItem:serviceItems){
				  		    	   	result.add(serviceItem);
				  		       }
				  	}
			  }
		 catalog.put("children", result);
		 return catalog;
	}

	@Override
	public List<Map<String, Object>> getServiceByCatalog(String catalogIds) {
		StringBuilder catalogs = new StringBuilder();
		String[] catalogArray=catalogIds.split(",");
		for(String catalog:catalogArray){
			  catalogs.append(",").append("'").append(catalog).append("'");
		}
		catalogs.deleteCharAt(0);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("catalogIds", catalogs.toString());
		return  icataLogDao.getServiceTypeByCatalogs(param);
	}
	
	
	
	public Map<String, Object> getPersonCatalogAndServices(){
				Map<String,Object> catalog = icataLogDao.getPersonalCatalog();
				String cataogId = catalog.get("id").toString();
				return getCatalogTreeAndService(cataogId);
	}
	
	
	
	
	/**
	 * 根据某个顶级分类，获取顶级分类下各分类对应的服务id,及服务名称。
	 * @param upCatalog
	 * @return
	 */
	private List<Map<String,Object>> getServicesByCatalogId(Map<String, Object> upCatalog) {
		List<Map<String,Object>>  services = null;
		String  leafCatalogIds = getLeafCatalogId(upCatalog);
		Map<String,Object> param=new HashMap<String,Object>();
		param.put("catalogIds", leafCatalogIds);
		services = userRecommendDao.getServiceInfoByCatalogIds(param);
		return services;
	}
	
	
	/**
	 * 获取某个分类下所有叶子级的分类id
	 * @param catalog
	 * @return
	 */
	private String getLeafCatalogId(Map<String,Object> catalog){
		int isLeaf = (int)catalog.get("leaf");
		String catalogId = catalog.get("id").toString();
		if (isLeaf==1){
			 return  catalogId;
		}else{
			 StringBuilder builder = new StringBuilder(""+Integer.MIN_VALUE);
			 List<Map<String,Object>> children = (List<Map<String, Object>>) catalog.get("children");
			 for(Map<String,Object> child:children){
				    if (child==null){
				    	continue;
				    }
				 	builder.append(",").append(getLeafCatalogId(child));
			 }
			 return builder.toString();
		}
	}
	
	
	//补充 推荐的个人服务 归属的顶级分类信息
	private void  getTopParentCatalog(List<Map<String,Object>>tradeInfos)
	{
		for(Map<String,Object> tradeInfo: tradeInfos){
				Long serviceId= (Long) tradeInfo.get("serviceId");
				List<Map<String,Object>> catalogs = icataLogDao.getCatalogByServiceId(serviceId.intValue());
				List<Map<String,Object>> topCatalogs = new ArrayList<Map<String,Object>>();
				if (catalogs!=null || catalogs.size()>0){
					for (Map<String,Object> catalog:catalogs){
						   topCatalogs.add(getTopParent(catalog));
						    
					}
				}
				
				tradeInfo.put("topCatalog", topCatalogs);
			
		}
		
	}
	
	/**
	 * 获取当前分类的顶级分类
	 * @param catalog
	 * @return
	 */
	private Map<String,Object> getTopParent(Map<String,Object> catalog){
		int parentid = (int)catalog.get("parentid");
		int level = (int)catalog.get("level");
		int catalogId = (int)catalog.get("id");
		String description = StringUtil.null2Str(catalog.get("demand"));
		String iconPath =  StringUtil.null2Str(catalog.get("iconPath"));
		String bigIconPath =  StringUtil.null2Str(catalog.get("bigIconPath"));
		String alias = "";
		if (catalog.get("alias")!=null){
				alias = catalog.get("alias").toString();
		}
				
		String name = catalog.get("name").toString();
		if (level==0){
			//顶层分类
				Map<String,Object> catalogInfo = new HashMap<String,Object>();
				catalogInfo.put("id", catalogId);
				catalogInfo.put("alias", alias);
				catalogInfo.put("name", name);
				catalogInfo.put("demand", description);
				catalogInfo.put("iconPath", iconPath);
				catalogInfo.put("bigIconPath", bigIconPath);
				return catalogInfo;
				
		}else{
				List<Map<String,Object>> parents =  icataLogDao.getCatalogById(parentid);
				return getTopParent(parents.get(0));
		}
	}

	@Override
	public String getServiceIds(List<Map<String, Object>> params) {
		StringBuilder ids= new StringBuilder(""+Integer.MIN_VALUE);
		for(Map<String,Object> condition:params){
			Long id = serviceTypeDao.getServiceByIsearch(condition);
			if (id!=null){
				 ids.append(",").append(id.toString());
			}
		}
		return ids.toString();
	}
	
	public  List<Map<String,Object>>  getCatalogInfo(String serviceIds) {
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		String[] serviceArray= serviceIds.split(",");
		List<Map<String,Object>> topCatalogs = new ArrayList<Map<String,Object>>();
		for(String service:serviceArray){
			 Long serviceId = Long.parseLong(service);
			 if (serviceId<0){
				 continue;
			 }
			 List<Map<String,Object>> catalogs = icataLogDao.getCatalogByServiceId(serviceId.intValue());	
				if (catalogs!=null && catalogs.size()>0){
					for (Map<String,Object> catalog:catalogs){
						    Map<String,Object> topCatalog = getTopParent(catalog);
						    if (topCatalog!=null && topCatalog.size()>0 && !topCatalog.get("alias").toString().equals("gxfw")){
						    	topCatalogs.add(getTopParent(catalog));
						    }
				    
					}
				}
		}
		
		//过滤重复的顶级分类
		if (topCatalogs.size()>0){
			Map<String,String>  catalogIds = new HashMap<String,String>();
			for(Map<String,Object> catalog:topCatalogs){
						 String id = catalog.get("id").toString();
						 if (!catalogIds.containsKey(id)){
							   catalogIds.put(id, id);
							   result.add(catalog);
						 }
			}
			
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> getSearchedCustomServiceInfo(
			String serviceIds) {
			Map<String,Object> param = new HashMap<String,Object>();
			param.put("serviceIds", serviceIds);
			List<Map<String,Object>> services = userRecommendDao.getCustomServicesByRange(param);
					
			return services;
	}

	@Override
	public Map<String, Object> getCatalogTreeAndService(String id) {
		String key = CacheConstants.CATALOG_AND_SERVICES+"_"+id;
		Object cachedCatalogsTree =  commonCacheService.getObject(key);
		if (cachedCatalogsTree==null){
			List<Map<String,Object>> catalogs=icataLogDao.getCatalogById(Integer.parseInt(id));
			if(catalogs==null || catalogs.size()==0){
				return null;
			}
			Map<String,Object> parentCatalog = catalogs.get(0);
			getSubTree(parentCatalog,true);
			commonCacheService.setObject(parentCatalog, key);
			((GenericCacheServiceImpl)commonCacheService).setExpire(key, CacheConstants.CATALOG_AND_SERVICES_EXPIRTIME);
			return parentCatalog;
		}else{
			return  (Map<String,Object>)cachedCatalogsTree;
		}
		
	}

	@Override
	public List<Map<String, Object>> getCatalogsByAppType(String appTypes) {
		        Map<String,Object> param = new HashMap<String,Object>();
		        param.put("appTypes", appTypes);
				return icataLogDao.getCataLogsByAppType(param);
	}
	
	
	
	private   void  extractServices(Map<String,Object> catalog,List services){
				List<Map<String,Object>>  children= (List<Map<String,Object>>)catalog.get("children");
				int leaf = (int)catalog.get("leaf");
				if (leaf==0){
					//非叶子级分类，需要继续往下找二级分类
					if (children.size()>0){
					 	for(Map<String,Object> child:children){
					 		  if (child.containsKey("leaf")) {
					 			extractServices(child,services);
					 		  }else{
					 			 services.add(child);
					 		  }
					 	}
				 }	
				}else{
					if (children.size()>0){
					 	for(Map<String,Object> child:children){
					 		services.add(child);
					 	}
					}
				}
		}

	@Override
	public List<Map<String, Object>> getAllServicesByCatalogId(String catalogId) {
			 Object cachedServices =  commonCacheService.getObject(CacheConstants.USER_HOME_HOTSEARCH__SERVICES_UNDER_CATALOG,catalogId);
			 List<Map<String,Object>> services=null;
			if (cachedServices ==null){
					 Map<String,Object> catalogTree = getCatalogTreeAndService(catalogId);
					 services= new ArrayList<Map<String,Object>>();
					 extractServices(catalogTree,services);
					 commonCacheService.setObject(services, CacheConstants.USER_HOME_HOTSEARCH__SERVICES_UNDER_CATALOG,catalogId);
			}else{
				      services = (List<Map<String,Object>>)cachedServices;
			}
		return services;
	}
	/**
	 * 获取catalog下所有原子性服务项目
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getServiceTypesByCatalogId(Integer catalogId) {
		List<Map<String,Object>> serviceTypeList=new ArrayList<Map<String,Object>>();		
		serviceTypeList=(List<Map<String,Object>>)commonCacheService.getObject(CacheConstants.CATALOG_SERVICE_TYPE_LIST, catalogId+"");
		if(serviceTypeList==null || serviceTypeList.size()==0){
			Map<String,Object> paramMap=new HashMap<String, Object>();
			paramMap.put("catalogIds", catalogId);
			serviceTypeList=icataLogDao.getServiceTypeByCatalogs(paramMap);
			getCatalogByCatalogId(serviceTypeList,catalogId);
			commonCacheService.setObject(serviceTypeList, CacheConstants.CATALOG_SERVICE_TYPE_LIST, catalogId+"");
		}
		return serviceTypeList;
	}
	/**
	 * 获取catalog下所有原子性服务项目
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getServiceTypesByCatalogIdForOrder(Integer catalogId) {
		List<Map<String,Object>> serviceTypeList=new ArrayList<Map<String,Object>>();		
		serviceTypeList=(List<Map<String,Object>>)commonCacheService.getObject(CacheConstants.ORDER_CATALOG_SERVICE_TYPE_LIST, catalogId+"");
		if(serviceTypeList==null || serviceTypeList.size()==0){
			Map<String,Object> paramMap=new HashMap<String, Object>();
			paramMap.put("catalogIds", catalogId);
			serviceTypeList=icataLogDao.getServiceTypeByCatalogsForOrder(paramMap);
			getCatalogByCatalogIdForOrder(serviceTypeList,catalogId);
			commonCacheService.setObject(serviceTypeList, CacheConstants.ORDER_CATALOG_SERVICE_TYPE_LIST, catalogId+"");
		}
		return serviceTypeList;
	}
	public void getCatalogByCatalogId(List<Map<String,Object>> serviceTypeList,Integer catalogId) {
		List<Map<String,Object>> catalogsList=icataLogDao.getCataLogs(catalogId);
		if(catalogsList==null || catalogsList.size()==0){
			return ;
		}
		for(Map<String,Object> map : catalogsList){
			int id=StringUtil.nullToInteger(map.get("id"));
			int leaf=StringUtil.nullToInteger(map.get("leaf"));
			if(leaf==1){//是叶子，则直接查询服务类型
				Map<String,Object> paramMap=new HashMap<String, Object>();
				paramMap.put("catalogIds", id);
				List<Map<String,Object>> serviceTypeList1=icataLogDao.getServiceTypeByCatalogs(paramMap);
				for(Map<String,Object> map1 : serviceTypeList1){
					serviceTypeList.add(map1);
				}
			}else{
				Map<String,Object> paramMap=new HashMap<String, Object>();
				paramMap.put("catalogIds", catalogId);
				List<Map<String,Object>> serviceTypeList2=icataLogDao.getServiceTypeByCatalogs(paramMap);
				for(Map<String,Object> map2 : serviceTypeList2){
					serviceTypeList.add(map2);
				}
				getCatalogByCatalogId(serviceTypeList,id);
			}
		}
	}
	public void getCatalogByCatalogIdForOrder(List<Map<String,Object>> serviceTypeList,Integer catalogId) {
		List<Map<String,Object>> catalogsList=icataLogDao.getCataLogsForOrder(catalogId);
		if(catalogsList==null || catalogsList.size()==0){
			return ;
		}
		for(Map<String,Object> map : catalogsList){
			int id=StringUtil.nullToInteger(map.get("id"));
			int leaf=StringUtil.nullToInteger(map.get("leaf"));
			if(leaf==1){//是叶子，则直接查询服务类型
				Map<String,Object> paramMap=new HashMap<String, Object>();
				paramMap.put("catalogIds", id);
				List<Map<String,Object>> serviceTypeList1=icataLogDao.getServiceTypeByCatalogsForOrder(paramMap);
				for(Map<String,Object> map1 : serviceTypeList1){
					serviceTypeList.add(map1);
				}
			}else{
				Map<String,Object> paramMap=new HashMap<String, Object>();
				paramMap.put("catalogIds", catalogId);
				List<Map<String,Object>> serviceTypeList2=icataLogDao.getServiceTypeByCatalogsForOrder(paramMap);
				for(Map<String,Object> map2 : serviceTypeList2){
					serviceTypeList.add(map2);
				}
				getCatalogByCatalogIdForOrder(serviceTypeList,id);
			}
		}
	}
  //获取前n个服务项目	
  private List<Map<String,Object>> getTopnServices(List<Map<String,Object>> servicesNames,int size){
	  		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
	  		if (servicesNames!=null || servicesNames.size()>0){
	  			  int i=1;
	  			  for(Map<String,Object> service:servicesNames){
	  				  		result.add(service);
	  				  		i++;
	  				  		if (i>size){
	  				  			break;
	  				  		}
	  			  }
	  			
	  		}
	  		return result;
  }
  
  public List<Map<String, Object>>  getCatalogByParentId( Long parentId) {
		List<Map<String, Object>> resultList_=new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> list = serviceTypeDao.getCatalogByParentId(parentId);
		for (Map<String, Object> map : list) {
			Long parentId_ = StringUtil.nullToLong(map.get("id"));
			List<Map<String, Object>> list_ =getCatalogByParentId( parentId_);
			map.put("catalogList", list_);
			resultList_.add(map);
		}
		return resultList_;
	}
  
  
	//缩小搜搜附近 返回的服务项目数目
	private void cutdownServiceNames(List<Map<String, Object>> merchantInfos,
			int maxNumOfNames) {
		
			if (merchantInfos!=null && merchantInfos.size()>0){
				for(Map<String,Object> merchantInfo:merchantInfos){
							String servicesName=merchantInfo.get("serviceNames").toString();
							if (servicesName!=null && servicesName.length()>0){
										String[] servicesNameArray = servicesName.split(",");
										if (servicesNameArray.length>maxNumOfNames){
											  StringBuilder cutdownNames = new StringBuilder();
											  for(int i=0;i<maxNumOfNames;i++){
												   cutdownNames.append(servicesNameArray[i]);
												   if (i!=maxNumOfNames-1){
													     cutdownNames.append(",");
												   }
											  }
											  merchantInfo.put("serviceNames", cutdownNames.toString());
										}
								
							}
				}
				
			}
		
	}

	@Override
	public List<Map<String, Object>> getPersonCatalogAndServices(int pageNo,int pageSize) {
		Long totalNumber =  icataLogDao.getTopCatalogNumber();
		Long totalPageNo = (totalNumber+pageSize-1)/pageSize;
		Long startNumber = (long) (pageNo * pageSize);
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("rows", startNumber);
		param.put("pageSize",pageSize);
		List<Map<String,Object>> catalogList = icataLogDao.getPersonalCatalogByPage(param);
		if (catalogList!=null && catalogList.size()>0){
				for (Map<String,Object> catalog:catalogList){
					String cataogId = catalog.get("id").toString();
					Map<String,Object> subTree=getCatalogTreeAndService(cataogId);
					catalog.put("children", subTree.get("children"));
				}
				BusinessUtil.disposeManyPath(catalogList, "iconPath");
				BusinessUtil.disposeManyPath(catalogList, "bigIconPath");
		}else{
			catalogList = new ArrayList<Map<String,Object>>();
		}
		return catalogList;
	}

	@Override
	public long getPersonCatalogAndServicesPageNum(int pageNo,int pageSize) {
		Long totalNumber =  icataLogDao.getTopCatalogNumber();
		Long totalPageNo = (totalNumber+pageSize-1)/pageSize;
		return totalPageNo;
	}

	
	@Override
	public List<Map<String, Object>> getCatalogOrServiceList(Map<String,Object> param) {
		String leaf = StringUtil.null2Str(param.get("leaf"));
		String catalogId = StringUtil.null2Str(param.get("catalogId"));
		param.put("leaf", leaf);
		param.put("catalogId", catalogId);
		List<Map<String,Object>> catalogOrServiceList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> catalogList = icataLogDao.getCatalogList(param);
		List<Map<String,Object>> serviceList = icataLogDao.getServiceList(param);
//		catalogList = icataLogDao.getCatalogList(param);
		
//		if("1".equals(leaf)&&!StringUtil.isNullStr(catalogId)){
//			catalogList = icataLogDao.getServiceList(param);
//		}else{
//			catalogList = icataLogDao.getCatalogList(param);
//		}
		if(catalogList!=null&&catalogList.size()>0){
			BusinessUtil.disposeManyPath(catalogList, "icon");
			catalogOrServiceList.addAll(catalogList);
		}
		if(serviceList!=null&&serviceList.size()>0){
			BusinessUtil.disposeManyPath(serviceList, "icon");
			catalogOrServiceList.addAll(serviceList);
		}
		return catalogOrServiceList;
	}

	@Override
	public long calcThisYearSurplusDays() throws Exception {
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();

		calendar.set(Calendar.MONTH, 11);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date last = calendar.getTime();

		long surplusDays = DateUtil.getBetweenDays(now, last);
		return surplusDays;
	}

	@Override
	public JSONObject getShareHtml() {
		JSONObject jsonObject = null;
		// 先从缓存中读取所有的分享页面，没有从数据库中获取
		List<Map<String, Object>> pubList = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.SHARE_HTML);
		
		if (pubList == null || pubList.size() < 1) {
			pubList = commonDao.getShareHtml();
			if (pubList != null && pubList.size() > 0) {
				commonCacheService.setObject(pubList, CacheConstants.SHARE_HTML);
			}
		}
		
		if (pubList == null || pubList.size() < 1) {
			jsonObject = new ResultJSONObject("001", "当前无活动");
		}else{
			jsonObject = new ResultJSONObject("000", "获取活动成功");
			BusinessUtil.disposeManyPath(pubList, "image");
			jsonObject.put("webUrl", pubList.get(0).get("webUrl"));
			pubList.get(0).remove("webUrl");
			jsonObject.put("shareActivity", pubList.get(0));
			
		}
		
		return jsonObject;
	}

	@Override
	public List<Map<String, Object>> getPersonCatalogAndServices(String keyword) {
		//1. 按自定义分类的分类名称搜索
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("keyword", keyword);
		
		StringBuffer catalogIds = new StringBuffer("");
		List<Map<String,Object>> catalogList = icataLogDao.getPersonalCatalogByKeywords(param);
		if (catalogList!=null && catalogList.size()>0){
				for (Map<String,Object> catalog:catalogList){
					String cataogId = catalog.get("id").toString();
					Map<String,Object> subTree=getCatalogTreeAndService(cataogId);
					catalog.put("children", subTree.get("children"));
					catalogIds.append(",").append(cataogId);
				}
				BusinessUtil.disposeManyPath(catalogList, "iconPath");
				BusinessUtil.disposeManyPath(catalogList, "bigIconPath");
		}else{
			catalogList = new ArrayList<Map<String,Object>>();
		}
		
		//2.按自定义分类挂靠的服务名称搜索。
		if (catalogIds.length()>1){
			   String ids=catalogIds.deleteCharAt(0).toString();
			   param.put("excludes", ids);
		}else {
			  param.put("excludes", "-999");
		}
		List<Map<String,Object>> servicesAndCatalog = icataLogDao.getPersonalServiceAndCatalogByKeywords(param);
		
		
		//合并 1 ，2 两个结果集
		if (servicesAndCatalog!=null && servicesAndCatalog.size()>0){
				BusinessUtil.disposeManyPath(servicesAndCatalog, "big_icon_path");
				BusinessUtil.disposeManyPath(servicesAndCatalog, "icon_path");
			
			    String parentID="";
			    Map<String,Object> catalog = new HashMap<String,Object>();
			    List<Map<String,Object>> children = new ArrayList<Map<String,Object>>();
			    catalog.put("children", children);
				for (Map<String,Object> serviceAndCatalog:servicesAndCatalog){
					       String cataLogId = StringUtil.null2Str(serviceAndCatalog.get("catalogId"));
					       String catalogName =StringUtil.null2Str(serviceAndCatalog.get("cataLogName"));
					       String demand = StringUtil.null2Str(serviceAndCatalog.get("demand"));
					       String bigIconPath = StringUtil.null2Str(serviceAndCatalog.get("big_icon_path"));
					       String iconPath = StringUtil.null2Str(serviceAndCatalog.get("icon_path"));
					       String alias = StringUtil.null2Str(serviceAndCatalog.get("alias"));
					       String parentid = StringUtil.null2Str(serviceAndCatalog.get("parentid"));
					       String id = StringUtil.null2Str(serviceAndCatalog.get("id"));
					       String name = StringUtil.null2Str(serviceAndCatalog.get("name"));
					       Integer leaf = (Integer)serviceAndCatalog.get("leaf");
					       if (parentID!="" &&  !parentID.endsWith(cataLogId)){
					    	   //保存上个分类，并开启一个新分类
					    	   	catalogList.add(catalog);
					    	   	catalog =  new HashMap<String,Object>();
					    	   	children = new ArrayList<Map<String,Object>>();
					    	   	catalog.put("children", children);
					       }
						   parentID = cataLogId;
						   	
					       catalog.put("id", Integer.parseInt(cataLogId));
					       catalog.put("iconPath", iconPath);
					       catalog.put("bigIconPath", bigIconPath);
					       catalog.put("alias", alias);
					       catalog.put("name", catalogName);
					       catalog.put("parentid", Integer.parseInt(parentid));
					       catalog.put("leaf", leaf);
					       catalog.put("demand", demand);
					       
					       Map<String,Object> child = new HashMap<String,Object>();
					       child.put("id", Long.parseLong(id));
					       child.put("name", name);
					       child.put("catalogId", Integer.parseInt(cataLogId));
					       children.add(child);
				}
				if (children.size()>0){
					  catalogList.add(catalog);
				}
				
			
		}
		
		return catalogList;
	}
	@Override
	public JSONObject getStaticActivityHtml(Map<String, Object> param) {
		
		JSONObject jsonObject = null;
		String act_key=(String) param.get("act_key");
		// 先从缓存中读取所有的分享页面，没有从数据库中获取
		String url = (String) commonCacheService.getObject(CacheConstants.STATIC_HTML,act_key);
		
		if (url == null || url.equals("")) {
			url = commonDao.getStaticActivityHtml(param);
			if (url != null && !url.equals("")) {
				commonCacheService.setObject(url, CacheConstants.STATIC_HTML,act_key);
			}
		}
		jsonObject = new ResultJSONObject("000", "获取活动成功");
		jsonObject.put("url", url);
			
		return jsonObject;
	}

	@Override
	public JSONObject getActivityList(Map<String, Object> param) {
		JSONObject jsonObject = new ResultJSONObject("001", "获取活动列表失败");
		// 先从缓存中读取所有的分享页面，没有从数据库中获取
		String entrance = StringUtil.null2Str(param.get("entrance"));
		Map<String,Object> map1 = new HashMap<String,Object>();
		Map<String,Object> map2 = new HashMap<String,Object>();
		Map<String,Object> map3 = new HashMap<String,Object>();
		Map<String,Object> map4 = new HashMap<String,Object>();
		Map<String,Object> map5 = new HashMap<String,Object>();
		
		if(entrance.equals(CacheConstants.INDEX_LIST)){
			// 用户首页
			List<Map<String,Object>> list1 = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.INDEX_LIST_1);
			List<Map<String,Object>> list2 = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.INDEX_LIST_2);
			List<Map<String,Object>> list3 = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.INDEX_LIST_3);
			List<Map<String,Object>> list4 = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.INDEX_LIST_4);
			List<Map<String,Object>> list5 = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.INDEX_LIST_5);
			if(list1==null||list1.isEmpty()){
				param.put("entrance", CacheConstants.INDEX_LIST_1);
				list1 = commonDao.getActivityList(param);
				list1 = handleUrl(list1,param);
				commonCacheService.setObject(list1, CacheConstants.INDEX_LIST_1);
			}
			if(list2==null||list2.isEmpty()){
				param.put("entrance", CacheConstants.INDEX_LIST_2);
				list2 = commonDao.getActivityList(param);
				list2 = handleUrl(list2,param);
				commonCacheService.setObject(list2, CacheConstants.INDEX_LIST_2);
			}
			if(list3==null||list3.isEmpty()){
				param.put("entrance", CacheConstants.INDEX_LIST_3);
				list3 = commonDao.getActivityList(param);
				list3 = handleUrl(list3,param);
				commonCacheService.setObject(list3, CacheConstants.INDEX_LIST_3);
			}
			if(list4==null||list4.isEmpty()){
				param.put("entrance", CacheConstants.INDEX_LIST_4);
				list4 = commonDao.getActivityList(param);
				list4 = handleUrl(list4,param);
				commonCacheService.setObject(list4, CacheConstants.INDEX_LIST_4);
			}
			if(list5==null||list5.isEmpty()){
				param.put("entrance", CacheConstants.INDEX_LIST_5);
				list5 = commonDao.getActivityList(param);
				list5 = handleUrl(list5,param);
				commonCacheService.setObject(list5, CacheConstants.INDEX_LIST_5);
			}
			jsonObject = new ResultJSONObject("000", "获取首页活动成功");
			String tag = "";
			if(list1!=null&&list1.size()>0){
				tag = StringUtil.null2Str(list1.get(0).get("tag"));
			}
			map1.put("tag", tag);
			map1.put("bannerList", list1);
			jsonObject.put("banner1", map1);
			if(list2!=null&&list2.size()>0){
				tag = StringUtil.null2Str(list2.get(0).get("tag"));
			}else{
				tag = "";
			}
			map2.put("tag", tag);
			map2.put("bannerList", list2);
			jsonObject.put("banner2", map2);
			
			if(list3!=null&&list3.size()>0){
				tag = StringUtil.null2Str(list3.get(0).get("tag"));
			}else{
				tag = "";
			}
			map3.put("tag", tag);
			map3.put("bannerList", list3);
			jsonObject.put("banner3", map3);
			
			if(list4!=null&&list4.size()>0){
				tag = StringUtil.null2Str(list4.get(0).get("tag"));
			}else{
				tag = "";
			}
			map4.put("tag", tag);
			map4.put("bannerList", list4);
			jsonObject.put("banner4", map4);
			
			if(list5!=null&&list5.size()>0){
				tag = StringUtil.null2Str(list5.get(0).get("tag"));
			}else{
				tag = "";
			}
			map5.put("tag", tag);
			map5.put("bannerList", list5);
			jsonObject.put("banner5", map5);
			
		}else{
			// 商户主页.经营攻略
			List<Map<String,Object>> list = (List<Map<String, Object>>) commonCacheService.getObject(entrance);
			if(list==null||list.isEmpty()||list.size()<1){
				list = commonDao.getActivityList(param);
				list = handleUrl(list,param);
				commonCacheService.setObject(list, entrance);
			}
			jsonObject = new ResultJSONObject("000", "获取首页活动成功");
			jsonObject.put("bannerList", list);
		}

		return jsonObject;
	}
	
	/**
	 * 处理url（活动详情页拼接活动ID）
	 * @param list
	 * @return
	 */
	private List<Map<String,Object>> handleUrl(List<Map<String,Object>> list,Map<String, Object> param){
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> plateFormList=new ArrayList<Map<String,Object>>();
		if(list!=null&&!list.isEmpty()&&list.size()>0){
			for(Map<String,Object> map:list){
//				long stime = StringUtil.nullToLong(map.get("stime"));
//				long etime = StringUtil.nullToLong(map.get("etime"));
//				long ntime = new Date().getTime();
//				if(ntime>=stime&&ntime<=etime){
					// 活动在当前时间
					String url = StringUtil.null2Str(map.get("url"));
					String shareLink = StringUtil.null2Str(map.get("shareLink"));
					String new_url = StringUtil.null2Str(map.get("new_url"));
					if(!StringUtil.isNullStr(url)){
						if(url.endsWith("?")||url.endsWith("&")){
							url = url+"activityId="+map.get("id");
						}else{
							url = url+"?activityId="+map.get("id");
						}
						map.put("url", url);
					}
					if(!StringUtil.isNullStr(shareLink)){
						if(url.endsWith("?")||url.endsWith("&")){
							shareLink = shareLink+"activityId="+map.get("id");
						}else{
							shareLink = shareLink+"?activityId="+map.get("id");
						}
						map.put("shareLink", shareLink);
					}
					
					if(!StringUtil.isNullStr(new_url)){
						if(url.endsWith("?")||url.endsWith("&")){
							new_url = new_url+"activityId="+map.get("id");
						}else{
							new_url = new_url+"?activityId="+map.get("id");
						}
						map.put("new_url", new_url);
					}
					
					//查询该活动要分享的平台
					plateFormList=commonDao.getActivityPlateForm(Long.parseLong(map.get("id").toString()));
					map.put("plateForm", plateFormList);
					// 处理图片
					String bImage = StringUtil.null2Str(map.get("bImage"));
					if(!StringUtil.isNullStr(bImage)){
						bImage=BusinessUtil.disposeImagePath(bImage);
					}
					String sImage = StringUtil.null2Str(map.get("sImage"));
					if(!StringUtil.isNullStr(sImage)){
						sImage=BusinessUtil.disposeImagePath(sImage);
					}
					String shareImage = StringUtil.null2Str(map.get("shareImage"));
					if(!StringUtil.isNullStr(shareImage)){
						shareImage=BusinessUtil.disposeImagePath(shareImage);
					}
					String newImage = StringUtil.null2Str(map.get("newImage"));
					if(!StringUtil.isNullStr(newImage)){
						newImage=BusinessUtil.disposeImagePath(newImage);
					}
					map.put("sImage", sImage);
					map.put("bImage", bImage);
					map.put("shareImage", shareImage);
					map.put("newImage", newImage);
					resultList.add(map);
				}

			}
//		}
		return resultList;
	}

	@Override
	public Map<String, Integer> getRestrictUpdate() {
		Map<String,Integer> result=null;
		Object cachedUrls = commonCacheService.getObject(CacheConstants.RESTRICT_UPDATE_URL);
		if (cachedUrls ==null){
				 List<Map<String,Object>> urls=commonDao.getRestrictList();
				 if (urls!=null && urls.size()>0){
					    result = new HashMap<String,Integer>();
					 	for(Map<String,Object> url:urls){
					 		 result.put(url.get("url").toString(), (Integer)url.get("interval"));
					 	}
					 	commonCacheService.setObject(result,CacheConstants.RESTRICT_UPDATE_URL);
				 }
		}else{
			  result = (Map<String,Integer>)cachedUrls;
		}
		return result;
	}

	@Override
	public JSONObject getPaymentNaviList() {
		JSONObject jsonObject = new ResultJSONObject("000","查询交易明细导航成功");
		List<Map<String,Object>> navaList =null;
		Object navaListObjct =commonCacheService.getObject(CacheConstants.MERCHANT_PAYMENT_LIST_NAVI);
		if(null == navaListObjct){
			navaList = commonDao.getPaymentNaviList();			
			Map<String,Object> allNavilMap = new HashMap<String,Object>();
			allNavilMap.put("paymentType", -1);
			allNavilMap.put("detailsName", "全部");
			navaList.add(0, allNavilMap);
			if(null != navaList && navaList.size() > 0){
				commonCacheService.setObject(navaList,CacheConstants.MERCHANT_PAYMENT_LIST_NAVI_EXPIRETIME, CacheConstants.MERCHANT_PAYMENT_LIST_NAVI);
			}
		}else{
			navaList =(List<Map<String,Object>>)navaListObjct;
		}
		jsonObject.put("navailList", navaList);
		return jsonObject;
	}

	@Override
	public Integer getSummaryByCity(String province,String city) {
		Integer result=-1;
		Object cachedSummary = commonCacheService.getObject(CacheConstants.CITY_MERCHANT_SUMMARY, province, city);
		if (cachedSummary==null){
			 boolean isOpen = false;
			 
			 try {
				isOpen= checkServiceCity(province,city);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("判断是否开通出错", e);
			}
			 
			if (isOpen){
				Map<String,String> param = new HashMap<String,String>();
				param.put("province", province);
				param.put("city", city);
				result=merchantForSearchDao.getMerchantQualityByCity(param);
				commonCacheService.setObject(result, CacheConstants.CITY_MERCHANT_SUMMARY_EXPIRETIME, CacheConstants.CITY_MERCHANT_SUMMARY, province,city);
			}else{
				commonCacheService.setObject(result, CacheConstants.CITY_MERCHANT_SUMMARY_EXPIRETIME, CacheConstants.CITY_MERCHANT_SUMMARY, province,city);
			}
		}else{
			result = (Integer) cachedSummary;
		}
		return result;
	}
	
	/**
	 * 获取用户首页 ---有需求告诉我文本提示
	 * @return
	 */
	private String  getUserHomepageRequireTip(){
		 String result=null;
		 Object cachedTip=commonCacheService.getObject(CacheConstants.USER_HOMEPAGE_REQUIRE_TIP);
		 if (cachedTip==null){
			  result=commonDao.getUserHomePageRequireTip();
			  commonCacheService.setObject(result,CacheConstants.USER_HOMEPAGE_REQUIRE_TIP);
		 }else{
			  result= (String) cachedTip;
		 }
		 return result;
	}
	
	/**
	 * 获取用户首页 ---最新订单信息
	 * @param userId
	 * @return
	 */
	public Map<String,Object> getUserHomePageGoods(Long userId,String version) throws Exception{
			Map<String,Object> goods=commonDao.getUserHomePageGoods(userId);
			Map<String,String> dictionary = new HashMap<String, String>();
			if(StringUtil.isEmpty(version)){
				dictionary = getOrderStatusMap("senderOrderStatusMap");
			}else{
				dictionary = getOrderStatusMap("senderOrderStatusMapV1110");
			}
			if (goods!=null && goods.size()>0){
				goods.put("orderStatusName", dictionary.get(goods.get("orderStatus").toString()));
			}
			return goods;
	}
	
	
	
	
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

	@Override
	public JSONObject getUserHomePage(String province,String city, Long userId,String version)
			throws Exception {
		JSONObject jsonObject = new ResultJSONObject("000","获取用户首页信息成功");
		
		//获取该城市对应的服务商数量
		if (province!=null &&  !province.equals("") &&  city!=null   &&  !city.equals("")){
				String[] provinceAndCity = BusinessUtil.handlerProvinceAndCity(
	                province, city);
	     	 	Integer citySummary=this.getSummaryByCity(provinceAndCity[0],provinceAndCity[1]);
			 	jsonObject.put("cityQuality", citySummary);
		}
		
		
		//获取用户活动
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("entrance", CacheConstants.INDEX_LIST);
		JSONObject  acitivitiesObject = getActivityList(param);
		if (acitivitiesObject.containsKey("banner1")){
			jsonObject.put("banner1", acitivitiesObject.getJSONObject("banner1"));
		}
		if (acitivitiesObject.containsKey("banner2")){
			jsonObject.put("banner2", acitivitiesObject.getJSONObject("banner2"));
		}
		if (acitivitiesObject.containsKey("banner3")){
			jsonObject.put("banner3", acitivitiesObject.getJSONObject("banner3"));
		}
		if (acitivitiesObject.containsKey("banner4")){
			jsonObject.put("banner4", acitivitiesObject.getJSONObject("banner4"));
		}
		if (acitivitiesObject.containsKey("banner5")){
			jsonObject.put("banner5", acitivitiesObject.getJSONObject("banner5"));
		}
		
		
		//获取用户最新一笔商品，无商品则获取轮动的订单购买文字。
		Map<String,Object> goods=null;
		if (userId!=null){
			goods=getUserHomePageGoods(userId,version);
			jsonObject.put("goods", goods);
		}
		
		if (goods==null || goods.size()<1){
			jsonObject.put("buyInfos", getMockupInfo());
		}
		
		
		//发需求的文字提示
		jsonObject.put("tip", getUserHomepageRequireTip());
		
		
		//判断用户是否开店
		 if(hasShop(userId)){
		    jsonObject.put("hasShop", true);
		 }else{
			 jsonObject.put("hasShop",false); 	
		 }
		 
		//判断是否有摸金计划活动
		int  isTouchingGold=this.isTouchingGold();
		jsonObject.put("isTouchingGold",isTouchingGold); 
		//首页文案推荐
		List<Map<String, Object>> resultMap = null;
		resultMap=this.appKeyWordDao.selectrecommendLabel(null);
		jsonObject.put("labeList", resultMap);
		

		//推荐服务商根据城市
//		Map<String, Object> recommendMerchanMap=new HashMap<String, Object>();
		Map<String, Object> parMap=new HashMap<String, Object>();
		parMap.put("province", province);
		parMap.put("city", city);
//		List<Map<String, Object>> recommendMerchantList = null;
//		recommendMerchantList=recommendMerchantDao.getRecommendMerchantListByPC(parMap);
//		if(null !=recommendMerchantList)
//		{
//			for (Map<String, Object> map : recommendMerchantList) {
//				
//				BusinessUtil.disposePath(map, "backImage");
//			}
//		}
		int recommendMerchanCount=recommendMerchantDao.getRecommendMerchantListByPCount(parMap);
		jsonObject.put("recommendMerchanCount", recommendMerchanCount);
//		jsonObject.put("recommendMerchanCount", null==recommendMerchantList?0:recommendMerchantList.size());
		
//		jsonObject.put("recommendMerchan", recommendMerchantList);
		return jsonObject;
	}
	
	
	
	private List<String> getMockupInfo(){
		List<String> result=new ArrayList<String>();
		List<String> buyInfo=null;
	    Object cachedMockupInfo=commonCacheService.getObject(CacheConstants.USER_HOMEPAGE_MOCKUP_BUYINFO);
	    if (cachedMockupInfo ==null || result.size()<1 ){
	    	    buyInfo= commonDao.getMockupBuyInfo();
	    		commonCacheService.setObject(buyInfo, CacheConstants.USER_HOMEPAGE_MOCKUP_BUYINFO);
	    }else{
	    	  buyInfo = (List<String>) cachedMockupInfo;
	    }
	    
	    int mockupRecSize = buyInfo.size();
	    
	    //模拟30条数据
	    if (mockupRecSize>0){
	    	for (int i=0;i<30;i++){
	    		int phoneIndex = IdGenerator.getRandomValue(phonePrefix.length);
	    		int mockupIndex = IdGenerator.getRandomValue(mockupRecSize);
	    		StringBuffer item =new StringBuffer(phonePrefix[phoneIndex]);
	    		item.append("****").append(IdGenerator.getRandomValue(10))
	    		.append(IdGenerator.getRandomValue(10))
	    		.append(IdGenerator.getRandomValue(10))
	    		.append(IdGenerator.getRandomValue(10))
	    		.append(" ").append(buyInfo.get(mockupIndex));
	    		result.add(item.toString());
	    	}
	    } 
	    return result;
	}
	
	
	/**
	 * 判断用户是否拥有商户
	 * @param userId
	 * @return
	 */
	private boolean hasShop(Long userId) {
		    Boolean result=false;
		    Integer shopCount=0;
		    
		    if (userId==null){
		    	return false;
		    }
		    
			Object cachedFlag = commonCacheService.getObject(CacheConstants.USER_HOMEPAGE_HAS_SHOP_FLAG,userId.toString());
			if (cachedFlag==null){
				 shopCount=commonDao.getMerchantNumById(userId);
				 commonCacheService.setObject(shopCount,CacheConstants.USER_HOMEPAGE_HAS_SHOP_FLAG_EXPIRETIME,CacheConstants.USER_HOMEPAGE_HAS_SHOP_FLAG, userId.toString());
			}else{
				 shopCount =(Integer)cachedFlag;
			}
			result=shopCount>0?true:false;
			return result;	 
	}

	@Override
	public JSONObject getSystemHelpList() {

		JSONObject jsonObject = null;
		jsonObject=(JSONObject) commonCacheService.getObject(CacheConstants.SYSTEM_HELP_FEEDBACK);
		if(null == jsonObject){
			List<Map<String,Object>> groupList = commonDao.getSystemHelpGroupList();
			if(groupList!=null&&groupList.size()>0){
				//遍历所有的分组，查找分组下具体帮助反馈详情
				List<Map<String,Object>> groupDetailList=null;
				for(Map<String,Object> map:groupList){
					groupDetailList=commonDao.getSystemHelpGroupDetailList(map);
					for(Map<String,Object> detailmap:groupDetailList){
					if (!StringUtil.isNullStr(detailmap.get("image").toString())) {
						detailmap.put("image", BusinessUtil.disposeImagePath(detailmap.get("image").toString()));
					}
					}
					map.put("groupDetail", groupDetailList);
				}
			}
			jsonObject = new ResultJSONObject("000","查询帮助反馈列表成功");
			jsonObject.put("groupList", groupList);
			commonCacheService.setObject(jsonObject,CacheConstants.SYSTEM_HELP_FEEDBACK);
		}
		
		return jsonObject;
	
	}

	@Override
	public JSONObject saveHelpEvaluation(Map<String, Object> param) {
		JSONObject jsonObject = null;
		String link_id = StringUtil.null2Str(param.get("link_id"));
		String eval =  StringUtil.null2Str(param.get("eval"));
		if(link_id.equals("")||eval.equals("")){
			jsonObject = new ResultJSONObject("001","链接ID或者好评值不可为空");
		}
		commonDao.saveHelpEvaluation(param);
		jsonObject = new ResultJSONObject("000","保存帮助反馈页点赞成功");
		return jsonObject;
	}
	
	@Override
	public int isTouchingGold(){
		int isTouchingGold =0;
		Map<String,Object> map=this.getConfigurationInfoByKey("touchingGold_deadline");
		if(map==null){
			return isTouchingGold;
		}
		int isOpenTouchingGold=StringUtil.nullToInteger(map.get("config_value"));
		if(isOpenTouchingGold==1){
			String startTime=StringUtil.null2Str(map.get("standby_field1"));
			String endTime=StringUtil.null2Str(map.get("standby_field2"));
			if(StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime) && 
					startTime.contains("-") && endTime.contains("-")){
				int startTimeInt=StringUtil.nullToInteger(startTime.replace("-", ""));
				int endTimeInt=StringUtil.nullToInteger(endTime.replace("-", ""));
				int nowTimeInt=StringUtil.nullToInteger(DateUtil.getNowYYYYMMDD().replace("-", ""));
				if(startTimeInt<=nowTimeInt && nowTimeInt<=endTimeInt){
					isTouchingGold=1;
				}
			}
		}
		return isTouchingGold;
	}
	
	/**
	 * 统计短信启动APP次数
	 */
	@Override
	public JSONObject statisticsStartCount(Long userId){
		JSONObject jsonObject = new ResultJSONObject("000","保存启动app记录成功");
		Map<String, Object> param=new HashMap<String, Object>();
		param.put("userId", userId);
		int i=commonDao.addStartAppInfo(param);
		if(i==0){
			jsonObject = new ResultJSONObject("001","保存启动app记录失败");
		}
		return jsonObject;
	}

	@Override
	public JSONObject recommendMerchantList(Map<String, Object> param) {
		//String appType,String province,String city,Integer pageNo,Integer pageSize
		JSONObject jsonObject = new ResultJSONObject("000","获取推荐服务商成功");
		int pageNo = StringUtil.nullToInteger(param.get("pageNo"));
		int pageSize = StringUtil.nullToInteger(param.get("pageSize"));
		if(pageNo==0){
			pageNo = 0; //默认第0页
		}
		param.put("pageNo", pageNo);
		if(pageSize==0){
			pageSize = 10; //默认每页十条
		}
		param.put("pageSize", pageSize);
		int start = pageNo*pageSize;
		param.put("start", start);
		List<Map<String,Object>> appTypeList = null; // 查询服务列表（服务商视角）

		List<Map<String,Object>> recommendMerchantList = null;
		try {
			appTypeList = recommendMerchantDao.getRecommendAppList(param);
			recommendMerchantList = recommendMerchantDao.recommendMerchantList(param);
			if(null!=recommendMerchantList&&recommendMerchantList.size()>0){
				for(Map<String,Object> map : recommendMerchantList){
					BusinessUtil.disposePath(map, "image","pic");
					int attitude = Integer.parseInt(map.get("attitude") == null ? "0": map.get("attitude").toString());
					int quality = Integer.parseInt(map.get("quality") == null ? "0": map.get("quality").toString());
					int speed = Integer.parseInt(map.get("speed") == null ? "0": map.get("speed").toString());
					int countEva = Integer.parseInt(map.get("countEva") == null ? "0": map.get("countEva").toString());
					if(countEva==0){
						// 没有用户评价的时候设置默认值
						map.put("starLevel", 5);
						map.put("score", 5.0);
					}else{
						// 总服务态度评价+总服务质量评价+总服务速度评价
						int totalEvaluation = attitude + quality + speed;
						// 分数
						BigDecimal score = new BigDecimal(totalEvaluation).divide(
								new BigDecimal(countEva)
										.multiply(new BigDecimal(3)), 1,
								BigDecimal.ROUND_DOWN);
						map.put("score", score);
						// 星级
						BigDecimal starLevel = new BigDecimal(totalEvaluation).divide(
								new BigDecimal(countEva)
										.multiply(new BigDecimal(3)), 0,
								BigDecimal.ROUND_HALF_UP);
						map.put("starLevel", starLevel);
					}
				}
			}
			
		} catch (Exception e) {
			appTypeList = new ArrayList<Map<String,Object>>();
			recommendMerchantList = new ArrayList<Map<String,Object>>();
			e.printStackTrace();
			jsonObject = new ResultJSONObject("001","获取推荐服务商失败");
		} finally{
			jsonObject.put("appTypeList", appTypeList);
			jsonObject.put("recommendMerchantList", recommendMerchantList);
		}
		return jsonObject;
	}
	
}
