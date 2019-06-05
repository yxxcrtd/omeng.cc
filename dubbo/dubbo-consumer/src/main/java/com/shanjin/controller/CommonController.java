package com.shanjin.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.service.IUserRelatedCacheServices;
import com.shanjin.common.MsgTools;
import com.shanjin.common.aspect.CheckPara;
import com.shanjin.common.aspect.SystemControllerLog;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.HttpRequest;
import com.shanjin.common.util.StringUtil;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IUserOrderService;
import com.shanjin.service.IValidateService;

@Controller
@RequestMapping("/common")
public class CommonController {

	@Reference
	private ICommonService commonService;

	@Reference
	private IValidateService validateService;

	@Autowired
	private IUserRelatedCacheServices userRelatedCacheServices;
	
	@Resource
	private IUserOrderService userOrderService;

	private Logger logger = Logger.getLogger(CommonController.class);

	/** 获得APP启动页 */
	@RequestMapping("/checkLoading")
	@SystemControllerLog(description = "获得APP启动页")
	public @ResponseBody Object checkLoading(String clientType) {
		JSONObject jsonObject = null;
		try {
			jsonObject = commonService.checkLoading(clientType);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "checkLoading");
			jsonObject = new ResultJSONObject("checkLoading_exception", "获得APP启动页失败");
			logger.error("获得APP启动页失败", e);
		}
		return jsonObject;
	}

	/** ios推送测试用例 */
	@RequestMapping("/iosPushTest")
	@SystemControllerLog(description = "ios推送测试用例")
	public @ResponseBody Object iosPushTest(String appType) {
		return this.commonService.iosPushTest(appType);
	}

	/** 获取当前应用程序所有的服务项目 */
	@RequestMapping("/serviceTypeName")
	@SystemControllerLog(description = "获取当前应用程序所有的服务项目")
	public @ResponseBody Object getServiceTypeName(String appType, Long parentId) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.commonService.getServiceInfo(appType, parentId);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "serviceTypeName");
			jsonObject = new ResultJSONObject("serviceTypeName_exception", "获取当前应用程序所有的服务项目失败");
			logger.error("获取当前应用程序所有的服务项目失败", e);
		}
		return jsonObject;
	}

	/** 获取易学堂所有的服务项目 */
	@RequestMapping("/yxtServiceTypeName")
	@SystemControllerLog(description = "获取易学堂所有的服务项目")
	public @ResponseBody Object getYxtServiceTypeName(String appType, String alias) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.commonService.getServiceInfoMultilevel(appType, alias);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "yxtServiceTypeName");
			jsonObject = new ResultJSONObject("yxtServiceTypeName_exception", "获取易学堂所有的服务项目失败");
			logger.error("获取易学堂所有的服务项目失败", e);
		}
		return jsonObject;
	}

	/** 获取艾秘书所有的服务项目 */
	@RequestMapping("/amsServiceTypeName")
	@SystemControllerLog(description = "获取艾秘书的所有的服务项目")
	public @ResponseBody Object getAmsServiceTypeName(String appType, Boolean loadDict) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.commonService.getAmsServiceInfo(appType, loadDict);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "amsServiceTypeName");
			jsonObject = new ResultJSONObject("amsServiceTypeName_exception", "获取艾秘书所有的服务项目失败");
			logger.error("获取艾秘书所有的服务项目失败", e);
		}
		return jsonObject;
	}

	/** 获取红妆所有的二级列表 */
	@RequestMapping("/getHzDictName")
	@SystemControllerLog(description = "获取红妆所有的二级菜单")
	public @ResponseBody Object gethzDictName(String dictType, String childDictType) {
		JSONObject jsonObject = null;
		try {
			jsonObject = this.commonService.gethzDictInfo(dictType, childDictType);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getHzDictName");

			jsonObject = new ResultJSONObject("getHzDictName_exception", "获取红妆所有的二级菜单失败");
			logger.error("获取红妆所有的二级菜单失败", e);
		}
		return jsonObject;
	}

	/** 获得APP更新 */
	@RequestMapping("/checkUpdate")
	@SystemControllerLog(description = "获得APP更新")
	public @ResponseBody Object checkUpdate(String packageName, Integer version) {
		JSONObject jsonObject = new ResultJSONObject("checkUpdate_exception", "获得APP更新失败");
		if (StringUtil.isNullStr(packageName)) {
			jsonObject = new ResultJSONObject("001", "packageName必传,不能为空");
			return jsonObject;
		}
		if (version == null) {
			jsonObject = new ResultJSONObject("001", "version必传,不能为空");
			return jsonObject;
		}
		// JSONObject jsonObject = new ResultJSONObject();
		try {
			jsonObject = commonService.checkUpdate(packageName, version);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "checkUpdate");

			jsonObject = new ResultJSONObject("checkUpdate_exception", "获得APP更新失败");
			logger.error("获得APP更新失败", e);
		}
		return jsonObject;
	}

	/** 获得所有应用程序列表 */
	@RequestMapping("/getAllApps")
	@SystemControllerLog(description = "获得所有应用程序列表")
	public @ResponseBody Object getAppList() {
		logger.debug("enter getAppList");
		if (commonService == null) {
			logger.error("commonService is null");
		}
		JSONObject jsonObject = new ResultJSONObject();
		try {
			jsonObject = commonService.getAllAppInfo();
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getAllApps");
			jsonObject = new ResultJSONObject("getAllApps_exception", "获得所有应用程序列表失败");
			logger.error("获得所有应用程序列表失败", e);
		}
		return jsonObject;
	}

	/** 获取动态密钥 */
	@RequestMapping("/getDynamicKey")
	@SystemControllerLog(description = "获得动态密钥")
	public @ResponseBody Object getDynamicKey(String clientId) {
		JSONObject jsonObject = new ResultJSONObject();
		String dynamicKey = "";
		try {
			dynamicKey = validateService.getDynamicKey(clientId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "getDynamicKey");

			jsonObject = new ResultJSONObject("getDynamicKey_exception", "获取动态密钥失败");
			logger.error("获取动态密钥失败", e);
		}
		jsonObject.put("dynamicKey", dynamicKey);
		return jsonObject;
	}

	/** 获得轮播图 */
	@RequestMapping("/getSliderPics")
	@SystemControllerLog(description = "获得轮播图")
	@CheckPara(args = "appType")
	public @ResponseBody Object getSliderPics(String appType) {
		JSONObject jsonObject = new ResultJSONObject();
		List<Map<String, Object>> sliderPics = null;
		try {
			sliderPics = commonService.getSliderPics(appType);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "getSliderPics");

			jsonObject = new ResultJSONObject("getSliderPics_exception", "获得轮播图失败");
			logger.error("获得轮播图失败", e);
		}
		jsonObject.put("sliderPics", sliderPics);
		return jsonObject;
	}

	/** 生成验证码 */
	@RequestMapping("/createVerificationCode")
	@SystemControllerLog(description = "生成验证码")
	public @ResponseBody Object createVerificationCode(Long merchantId, String phone) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject = this.commonService.createVerificationCode(merchantId, phone);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "createVerificationCode");

			jsonObject = new ResultJSONObject("createVerificationCode_exception", "生成验证码失败");
			logger.error("生成验证码失败", e);
		}
		return jsonObject;
	}

	/** 验证是否为黑名单用户 */
	@RequestMapping("/checkBlackList")
	@SystemControllerLog(description = "验证是否为黑名单用户")
	public @ResponseBody Object checkBlankList(String phone, String appType) {
		JSONObject jsonObject = new ResultJSONObject();
		try {
			jsonObject = commonService.checkBlackList(phone, appType);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "checkBlackList");

			jsonObject = new ResultJSONObject("checkBlackList_exception", "验证是否为黑名单用户失败");
			logger.error("验证是否为黑名单用户失败", e);
		}
		return jsonObject;
	}

	/** 删除用户的所有订单缓存 **/
	@RequestMapping("/removeUserOrders")
	@SystemControllerLog(description = "清除特定用户的订单缓存")
	public @ResponseBody Object removeUsersOrder(String userId) {
		JSONObject jsonObject = new ResultJSONObject();
		try {
			if (userRelatedCacheServices.removeUserCachedOrders(userId)) {
				jsonObject.put("000", "用户订单缓存清理成功");
			} else {
				jsonObject.put("001", "用户订单缓存清理成功");
			}
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "removeUserOrders");

			jsonObject = new ResultJSONObject("removeUserOrders_exception", "删除用户的所有订单缓存失败");
			logger.error("删除用户的所有订单缓存失败", e);
		}

		return jsonObject;
	}

	/** 获取推荐的标签 */
	@RequestMapping("/selectRecommendServiceTag")
	@SystemControllerLog(description = "获取推荐的标签")
	public @ResponseBody Object selectRecommendServiceTag() {
		JSONObject jsonObject = new ResultJSONObject();
		try {
			jsonObject = commonService.selectRecommendServiceTag();
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "selectRecommendServiceTag");

			jsonObject = new ResultJSONObject("selectRecommendServiceTag_exception", "获取推荐的标签失败");
			logger.error("获取推荐的标签失败", e);
		}
		return jsonObject;
	}

	/** 根据关键词搜索App */
	@RequestMapping("/serachAppType")
	@SystemControllerLog(description = "根据关键词搜索App")
	public @ResponseBody Object serachAppType(String keyword) {
		JSONObject jsonObject = new ResultJSONObject();
		try {
			keyword = StringUtil.formatDollarSign(keyword);
			jsonObject = commonService.serachAppType(keyword);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "serachAppType");

			jsonObject = new ResultJSONObject("serachAppType_exception", "根据关键词搜索App失败");
			logger.error("根据关键词搜索App失败", e);
		}
		return jsonObject;
	}

	/** 记录设备信息 */
	@RequestMapping("/recordDevice")
	@SystemControllerLog(description = "记录设备信息")
	public @ResponseBody Object recordDevice(String phone, String appType, Integer clientType, String version, String osVersion, String model, Integer userType,
			@RequestParam(required = false) String clientId) {
		JSONObject jsonObject = new ResultJSONObject();
		try {
			jsonObject = commonService.recordDevice(phone, appType, clientType, version, osVersion, model, userType, clientId);
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "recordDevice");

			jsonObject = new ResultJSONObject("serachAppType_exception", "记录设备信息失败");
			logger.error("记录设备信息失败", e);
		}
		return jsonObject;
	}

	/** 获取融云token信息 */
	@RequestMapping("/getRongCloudToken")
	@SystemControllerLog(description = "获取融云token信息")
	public @ResponseBody Object getRongCloudToken(String uid, String name, String portraitUri, String clientFlag, String clientId) {
		JSONObject jsonObject = new ResultJSONObject("getRongCloudToken_exception", "获取融云token信息失败");
		if (!StringUtil.isNullStr(clientFlag) && "1".equals(clientFlag) || "2".equals(clientFlag)) {
			// 参数合法
		} else {
			jsonObject = new ResultJSONObject("001", "clientFlag必传1或2【1：商户，2：用户】");
			return jsonObject;
		}
		if (StringUtil.isNullStr(uid)) {
			jsonObject = new ResultJSONObject("001", "uid不能为空，用户版参数uid值为userId，商户版uid值为merchantId");
			return jsonObject;
		}
		if (StringUtil.isNullStr(clientId)) {
			jsonObject = new ResultJSONObject("001", "clientId不能为空");
			return jsonObject;
		}
		String token = "";
		try {
			token = commonService.getRongCloudToken(uid, clientFlag, clientId, name, portraitUri);
			if (!StringUtil.isNullStr(token)) {
				if ("error".equals(token)) {
					jsonObject = new ResultJSONObject("001", "获取token失败【融云服务器响应异常】");
					return jsonObject;
				} else if ("proxyerror".equals(token)) {
					jsonObject = new ResultJSONObject("001", "获取token失败【本地代理proxy服务器响应异常】");
					return jsonObject;
				} else {
					jsonObject = new ResultJSONObject("000", "获取融云token信息成功");
					jsonObject.put("token", token);
				}
			} else {
				jsonObject = new ResultJSONObject("001", "获取token失败【本地provider服务器响应异常】");
				return jsonObject;
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getRongCloudToken");

			jsonObject = new ResultJSONObject("getRongCloudToken_exception", "获取融云token信息失败");
			logger.error("获取融云token信息失败", e);
		}
		return jsonObject;
	}

	/** 用户反馈 */
	@RequestMapping("/feedback")
	@SystemControllerLog(description = "用户反馈")
	public @ResponseBody Object feedback(@RequestParam Map<String, Object> paramMap, HttpServletRequest request) {
		/*
		 * 所传参数有： appType(应用类型，用户端反馈传“omeng”) 老版本
		 * customerType(1商户，2用户)  老版本 商户端customerId是merchantId
		 * userId
		 * phone(手机号码)
		 * customerId(商户Id或者用户Id)
		 * feedbackType(反馈类型：1建议，2咨询，3故障，4新需求，5闪退、卡顿或界面错位) content(反馈内容)
		 */
		JSONObject jsonObject = null;
		int customerType = Integer.parseInt(paramMap.get("customerType") == null ? "0" : paramMap.get("customerType").toString());
		List<String> picturePaths = new ArrayList<String>();

		// 图片上传
		MultipartFile picture0 = null;
		MultipartFile picture1 = null;
		MultipartFile picture2 = null;
		MultipartFile picture3 = null;
		MultipartFile picture4 = null;
		// 转型为MultipartHttpRequest：

		if (request instanceof MultipartHttpServletRequest) {
			try {
				MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
				picture0 = multipartRequest.getFile("picture0");
				picture1 = multipartRequest.getFile("picture1");
				picture2 = multipartRequest.getFile("picture2");
				picture3 = multipartRequest.getFile("picture3");
				picture4 = multipartRequest.getFile("picture4");

				if (picture0 != null && !picture0.isEmpty()) {
					String resultPath = BusinessUtil.saveFeedbackFile(picture0, customerType);
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture1 != null && !picture1.isEmpty()) {
					String resultPath = BusinessUtil.saveFeedbackFile(picture1, customerType);
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture2 != null && !picture2.isEmpty()) {
					String resultPath = BusinessUtil.saveFeedbackFile(picture2, customerType);
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture3 != null && !picture3.isEmpty()) {
					String resultPath = BusinessUtil.saveFeedbackFile(picture3, customerType);
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}
				if (picture4 != null && !picture4.isEmpty()) {
					String resultPath = BusinessUtil.saveFeedbackFile(picture4, customerType);
					if (resultPath.length() > 1) {
						picturePaths.add(resultPath);
					}
				}

			} catch (Exception e) {

				MsgTools.sendMsgOrIgnore(e, "feedback");

				logger.error(" 图片上传失败:" + e.getMessage(), e);
				return new ResultJSONObject("feedback_exception", "图片上传失败，请重新保存反馈信息！");
			}
		}
		try {
			if (this.commonService.feedback(paramMap, picturePaths)) {
				jsonObject = new ResultJSONObject("000", "感谢你的反馈");
			} else {
				jsonObject = new ResultJSONObject("001", "反馈信息失败，请稍后重试");
			}
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("feedback_exception", "用户反馈失败");
			logger.error("用户反馈失败", e);
		}
		return jsonObject;
	}

	/** 获取用户或商户基本信息 */
	@RequestMapping("/getBasicInfoByUid")
	@SystemControllerLog(description = "获取用户或商户基本信息")
	public @ResponseBody Object getBasicInfoByUid(String uid, String clientFlag) {
		JSONObject jsonObject = new ResultJSONObject("getBasicInfoByUid_exception", "获取用户或商户基本信息失败");
		if (!StringUtil.isNullStr(clientFlag) && "1".equals(clientFlag) || "2".equals(clientFlag)) {
			// 参数合法
		} else {
			jsonObject = new ResultJSONObject("001", "clientFlag必传1或2【1：商户，2：用户】");
			return jsonObject;
		}
		if (StringUtil.isNullStr(uid)) {
			jsonObject = new ResultJSONObject("001", "uid不能为空，用户版参数uid值为userId，商户版uid值为员工employeeId");
			return jsonObject;
		}
		try {
			Map<String, Object> map = commonService.getBasicInfoByUid(uid, clientFlag);
			if (map != null && !map.isEmpty()) {
				jsonObject = new ResultJSONObject("000", "用户或商户信息加载成功");
				jsonObject.put("info", map);
			} else {
				jsonObject = new ResultJSONObject("001", "店铺信息为空，请检查参数是否正确");
				jsonObject.put("info", map);
			}
		} catch (Exception e) {

			MsgTools.sendMsgOrIgnore(e, "getBasicInfoByUid");

			logger.error("店铺信息加载失败", e);
			jsonObject = new ResultJSONObject("105", "店铺信息加载失败");
		}
		return jsonObject;
	}

	/** 检查开通服务的城市 */
	@RequestMapping("/checkServiceCity")
	@SystemControllerLog(description = "检查开通服务的城市")
	public @ResponseBody Object checkServiceCity(String province, String city) {
		JSONObject jsonObject = new ResultJSONObject("000", province + " " + city + " 已经开通服务");
		try {
			if (StringUtil.isNotEmpty(province) && StringUtil.isNotEmpty(city)) {
				if (commonService.checkServiceCity(province, city)) {
				} else {
					jsonObject = new ResultJSONObject("fail", province + " " + city + " 尚未开通服务");
				}
			} else {
				jsonObject = new ResultJSONObject("fail", "province 或 city" + " 参数为空");
			}
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "checkServiceCity");

			logger.error(province + " " + city + " 尚未开通服务", e);
			jsonObject = new ResultJSONObject("fail", province + " " + city + " 尚未开通服务");
		}
		return jsonObject;
	}

	/** 获得服务类型的参数 */
	@RequestMapping("/getServiceParas")
	@SystemControllerLog(description = "获得服务类型的参数")
	public @ResponseBody Object getServiceParas(String appType, String serviceType) {
		JSONObject jsonObject = null;
		try {
			if (StringUtil.isNotEmpty(appType) && StringUtil.isNotEmpty(serviceType)) {
				jsonObject = commonService.getServiceParas(appType, serviceType);
			} else {
				jsonObject = new ResultJSONObject("fail", "appType 或 serviceType" + " 参数为空");
			}
		} catch (Exception e) {
			// MsgTools.sendMsgOrIgnore(e, "getServiceParas");
			logger.error("获得服务类型参数失败", e);
			jsonObject = new ResultJSONObject("fail", "获得服务类型参数失败");
		}
		return jsonObject;
	}
		
	/** 获得用户首页Banner的信息 */
	@RequestMapping("/getUserHomePageBanner")
	@SystemControllerLog(description = "获得用户首页轮播图片")
	public @ResponseBody Object getUserHomePageBanner() {
		JSONObject jsonObject = null;
		try {
			jsonObject = commonService.getUserHomeBanner();
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserHomeBanner");
			logger.error("获得用户首页轮播图片", e);
			jsonObject = new ResultJSONObject("fail", "获得用户首页轮播图片");
		}
		return jsonObject;
	}
	
	/** 获得用户首页Banner的信息 */
	@RequestMapping("/getRequireHomePage")
	@SystemControllerLog(description = "获得用户首页信息")
	public @ResponseBody Object getUserHomePageBanner0831(String province,String city,Long userId,String version) {
		JSONObject jsonObject = null;
		try {
			jsonObject = commonService.getUserHomePage(province,city,userId,version);
			
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getRequireHomePage");
			logger.error("获得用户首页信息", e);
			jsonObject = new ResultJSONObject("fail", "获得用户首页信息");
		}
		return jsonObject;
	}
	
	/** 获取某城市开通的商户数量 */
	@RequestMapping("/getShopQualityByCity")
	@SystemControllerLog(description = "获得城市开通商户数量")
	public @ResponseBody Object getShopQualityByCity(String province,String city) {
		JSONObject jsonObject = new ResultJSONObject("000","获得城市开通商户数量");
		try {
			Integer quality = commonService.getSummaryByCity(province,city);
			jsonObject.put("cityQuality",quality);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getRequireHomePage");
			logger.error("获得用户首页信息", e);
			jsonObject = new ResultJSONObject("fail", "获得用户首页信息");
		}
		return jsonObject;
	}
	
	
	
	
	/** 获得商户首页Banner的信息 */
	@RequestMapping("/getMerchantHomePageBanner")
	@SystemControllerLog(description = "获得商户首页轮播图片")
	public @ResponseBody Object getMerchantHomePageBanner() {
		JSONObject jsonObject = null;
		try {
			jsonObject = commonService.getMerchantHomeBanner();
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getMerchantHomePageBanner");
			logger.error("获得商户首页轮播图片", e);
			jsonObject = new ResultJSONObject("fail", "获得商户首页轮播图片");
		}
		return jsonObject;
	}
	
	
	/** 获得用户首页的信息 */
	@RequestMapping("/getUserHomePageRecommend")
	@SystemControllerLog(description = "获得用户首页推荐")
	public @ResponseBody Object getUserHomePageRecommend(String province,String city) {
		JSONObject jsonObject = null;
		try {
			jsonObject = commonService.getUserHomePageRecommend(province,city);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserHomePageRecommend");
			logger.error("获得用户首页推荐", e);
			jsonObject = new ResultJSONObject("fail", "获得用户首页推荐");
		}
		return jsonObject;
	}
	
	
	/** 获得用户附近商家的信息 */
	@RequestMapping("/getUserHomePageNearBy")
	@SystemControllerLog(description = "获得用户首页附近商家")
	public @ResponseBody Object getUserHomePageNearBy(double longitude, double latitude, double searchRange,int startNo,String searchCondition) {
		JSONObject jsonObject = null;
		try {
			jsonObject = commonService.getUserNearBy(longitude,latitude,searchRange,startNo,searchCondition);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserHomePageNearBy");
			logger.error("获取附近商户错误", e);
			jsonObject = new ResultJSONObject("fail", "获取附近商户错误");
		}
		return jsonObject;
	}
	
	
	
	/** 获得用户首页信息 */
	@RequestMapping("/getUserHomePage")
	@SystemControllerLog(description = "获得用户首页")
	public @ResponseBody Object getUserHomePage(String province,String city) {
		JSONObject jsonObject = null;
		try {
			JSONObject userHomeBannerObject = commonService.getUserHomeBanner();
			JSONObject userHomeCommentObject = commonService.getUserHomePageRecommend(province,city);
			
			jsonObject = new ResultJSONObject("000", "获得用户首页推荐成功");
			jsonObject.put("sliders", userHomeBannerObject.get("sliders"));
			jsonObject.put("tradeInfo", userHomeCommentObject.get("tradeInfo"));
			jsonObject.put("personInfo", userHomeCommentObject.get("personInfo"));
			
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserHome");
			logger.error("获得用户首页信息失败", e);
			jsonObject = new ResultJSONObject("fail", "获得用户首页信息失败");
		}
		return jsonObject;
	}
	
	
	/** 热门搜索---查看全部**/
	@RequestMapping("/getUserHomePageAllHotSearch")
	@SystemControllerLog(description = "获得用户首页热门搜索--查看全部信息")
	public @ResponseBody Object getUserAllHomeHotSearch() {
		JSONObject jsonObject = null;
		try {
			jsonObject = commonService.getHotSearch();
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getUserHomePageAllHotSearch");
			logger.error("获得用户首页热门搜索--查看全部信息错误", e);
			jsonObject = new ResultJSONObject("fail", "获得用户首页热门搜索--查看全部信息错误");
		}
		return jsonObject;
	}
	
	
	
	/** 获取商户入驻时的可选标签 */
	@RequestMapping("/getTagsForNewMerchant")
	@SystemControllerLog(description = "获取商家入驻时可选标签")
	public @ResponseBody Object getTagsFormMerchant() {
		JSONObject jsonObject = null;
		try {
			jsonObject = commonService.getTags();
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getTags");
			logger.error("获取商家入驻时可选标签错误", e);
			jsonObject = new ResultJSONObject("fail", "获取商家入驻时可选标签错误");
		}
		return jsonObject;
	}
	
	
	/** 获取行业分类列表--含子类 */
	@RequestMapping("/getCatalogs")
	@SystemControllerLog(description = "获取商家入驻的分类")
	public @ResponseBody Object getCatalogs() {
		JSONObject jsonObject = null;
		try {
			 List catalogs = commonService.getCataLogs();
			 jsonObject = new  ResultJSONObject("000", "获取商家入驻时获取分类成功");
			 jsonObject.put("catalogs", catalogs);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getCatalogs");
			logger.error("获取商家入驻时获取分类错误", e);
			jsonObject = new ResultJSONObject("fail", "获取商家入驻时获取分类错误");
		}
		return jsonObject;
	}
	
	
	/** 获取行业分类列表，不含子分类 */
	@RequestMapping("/getMerchantCatalogs")
	@SystemControllerLog(description = "获取商家入驻的一级分类")
	public @ResponseBody Object getMerchantCatalogs() {
		JSONObject jsonObject = null;
		try {
			 List catalogs = commonService.getMerchantCataLogs();
			 jsonObject = new  ResultJSONObject("000", "获取商家入驻的一级分类成功");
			 jsonObject.put("catalogs", catalogs);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getMerchantCatalogs");
			logger.error("获取商家入驻的一级分类错误", e);
			jsonObject = new ResultJSONObject("fail", "获取商家入驻的一级分类错误");
		}
		return jsonObject;
	}
	
	
	
	/** 获取自定义分类列表 */
	@RequestMapping("/getServiceByCatalogIds")
	@SystemControllerLog(description = "获取商家入驻的分类的服务")
	public @ResponseBody Object getServiceByCatalogIds(String catalogIds) {
		JSONObject jsonObject = null;
		try {
			 if (catalogIds==null || catalogIds.trim().length()<1){
				 	jsonObject = new  ResultJSONObject("fail", "传入分类不能为空");
			 }else{
				 List<Map<String, Object>> services = commonService.getServiceByCatalog(catalogIds);
				 jsonObject = new  ResultJSONObject("000", "获取商家入驻的分类的服务成功");
				 jsonObject.put("services", services);
			 }
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getServiceByCatalogIds");
			logger.error("获取商家入驻的分类的服务错误", e);
			jsonObject = new ResultJSONObject("fail", "获取商家入驻的分类的服务错误");
		}
		return jsonObject;
	}
	
	
	
	
	/** 获取自定义分类及其服务列表  **/ 
	@RequestMapping("/getPersonalCatalogAndServiceV1")
	@SystemControllerLog(description = "获取个人入住的分类及其服务")
	public @ResponseBody Object getPersonalCatalogAndServiceV1() {
		JSONObject jsonObject = null;
		try {
			 	 Map<String, Object> services = commonService.getPersonCatalogAndServices();
				 jsonObject = new  ResultJSONObject("000", "获取个人入住的分类及其服务成功");
				 jsonObject.put("info", services);
			
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getPersonalCatalogAndServiceV1");
			logger.error("获取个人入住的分类及其服务错误", e);
			jsonObject = new ResultJSONObject("fail", "获取个人入住的分类及其服务错误");
		}
		return jsonObject;
	}
	
	
	
	/** 获取自定义分类及其服务列表 **/ 
	@RequestMapping("/getPersonalCatalogAndService")
	@SystemControllerLog(description = "获取商家入驻的分类的服务")
	public @ResponseBody Object getPersonalCatalogAndService(int pageNo) {
		JSONObject jsonObject = null;
		try {
				 int pageSize = Constant.PAGESIZE;
			 	 List<Map<String, Object>> services = commonService.getPersonCatalogAndServices(pageNo,pageSize);
				 jsonObject = new  ResultJSONObject("000", "获取个人入住的分类及其服务成功");
				 jsonObject.put("info", services);
				 jsonObject.put("totalPage", commonService.getPersonCatalogAndServicesPageNum(pageNo,pageSize));
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getPersonalCatalogAndService");
			logger.error("获取个人入住的分类及其服务错误", e);
			jsonObject = new ResultJSONObject("fail", "获取个人入住的分类及其服务错误");
		}
		return jsonObject;
	}
	
	
	/** 按条件搜索自定义分类及其服务列表 **/ 
	@RequestMapping("/searchPersonalCatalogAndService")
	@SystemControllerLog(description = "按条件查询用户自定义服务及其分类")
	public @ResponseBody Object searchPersonalCatalogAndService(String keyword) {
		JSONObject jsonObject = null;
		try {
				 int pageSize = Constant.PAGESIZE;
			 	 List<Map<String, Object>> services = commonService.getPersonCatalogAndServices(keyword);
				 jsonObject = new  ResultJSONObject("000", "获取个人入住的分类及其服务成功");
				 jsonObject.put("info", services);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getPersonalCatalogAndService");
			logger.error("获取个人入住的分类及其服务错误", e);
			jsonObject = new ResultJSONObject("fail", "获取个人入住的分类及其服务错误");
		}
		return jsonObject;
	}
	
	/** 获取自定义分类列表 */
	@RequestMapping("/getCatalogAndServerById")
	@SystemControllerLog(description = "获取顶级分类下各分类及其服务")
	public @ResponseBody Object getCatalogAndServerById(String catalogId) {
		JSONObject jsonObject = null;
		try {
			 if (catalogId==null || catalogId.trim().length()<1){
				 	jsonObject = new  ResultJSONObject("fail", "传入分类不能为空");
			 }else{
				 Map<String, Object>  info = commonService.getCatalogTreeAndService(catalogId);
				 jsonObject = new  ResultJSONObject("000", "获取某分类下子分类及其对应的服务成功");
				 jsonObject.put("info", info);
			 }
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getCatalogAndServerById");
			logger.error("获取某分类下子分类及其对应的服务失败", e);
			jsonObject = new ResultJSONObject("fail", "获取某分类下子分类及其对应的服务失败");
		}
		return jsonObject;
	}
	
	/** 查询行业下所有原子性服务项目 */
	@RequestMapping("/getServiceTypesByCatalogId")
	@SystemControllerLog(description = "获取顶级分类下服务列表")
	public @ResponseBody Object getServiceTypesByCatalogId(Integer type,Integer catalogId) {
		JSONObject jsonObject = null;
		try {
			List<Map<String,Object>> serviceNames =null;
			if(type==1){
				serviceNames=commonService.getServiceTypesByCatalogId(catalogId);
			}else{
				serviceNames=commonService.getServiceTypesByCatalogIdForOrder(catalogId);
			}
			jsonObject = new  ResultJSONObject("000", "获取顶级分类下服务列表成功");
			jsonObject.put("serviceNames", serviceNames);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getServicesUnderCatalog");
			logger.error("获取顶级分类下服务列表失败", e);
			jsonObject = new ResultJSONObject("fail", "获取顶级分类下服务列表失败");
		}
		return jsonObject;
	}
	
	
	/** 热门搜索--全部---某行业下的所有服务列表 */
	@RequestMapping("/getServicesUnderCatalog")
	@SystemControllerLog(description = "获取顶级分类下服务列表")
	public @ResponseBody Object getServicesUnderCatalog(String catalogId) {
		JSONObject jsonObject = null;
		try {
			 if (catalogId==null || catalogId.trim().length()<1){
				 	jsonObject = new  ResultJSONObject("fail", "传入分类不能为空");
			 }else{
				 
				 List<Map<String,Object>> serviceNames = commonService.getAllServicesByCatalogId(catalogId);
				 jsonObject = new  ResultJSONObject("000", "获取顶级分类下服务列表成功");
				 jsonObject.put("serviceNames", serviceNames);
			 }
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getServicesUnderCatalog");
			logger.error("获取顶级分类下服务列表失败", e);
			jsonObject = new ResultJSONObject("fail", "获取顶级分类下服务列表失败");
		}
		return jsonObject;
	}
	
	/** 获取分类或服务项目列表 */
	@RequestMapping("/getCatalogOrServiceList")
	@SystemControllerLog(description = "获取分类列表")
	public @ResponseBody Object getCatalogOrServiceList(String catalogId,HttpServletRequest request) {
		JSONObject jsonObject = null;
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("catalogId", catalogId);
		try {
			 List<Map<String, Object>> catalogOrServiceList = commonService.getCatalogOrServiceList(param);
				if(catalogOrServiceList!=null&&catalogOrServiceList.size()>0){
					String url = "";
					for(Map<String,Object> map:catalogOrServiceList){
						String isService = StringUtil.null2Str(map.get("isService"));
						String isThird = StringUtil.null2Str(map.get("isThird"));
						if("1".equals(isService)){
							url = HttpRequest.getDomain(request)+"/customOrder/getOrderForm?serviceId="+map.get("id");
						}else{
							if("1".equals(isThird)){
								url = StringUtil.null2Str(map.get("url"));
							}else{
								url = HttpRequest.getDomain(request)+"/common/getCatalogOrServiceList?catalogId="+map.get("id")+"&leaf="+map.get("leaf");	
							}
						}
						map.put("url", url);
					}
				}
			 
			 jsonObject = new  ResultJSONObject("000", "分类或服务项目列表");
			 jsonObject.put("catalogOrServiceList", catalogOrServiceList);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "catalogOrServiceList");
			logger.error("分类或服务项目列表错误", e);
			jsonObject = new ResultJSONObject("fail", "分类或服务项目列表错误");
		}
		return jsonObject;
	}

	/** 计算今年剩余天数  **/ 
	@RequestMapping("/calcThisYearSurplusDays")
	@SystemControllerLog(description = "计算今年剩余天数")
	public @ResponseBody Object calcThisYearSurplusDays() {
		JSONObject jsonObject = null;
		try {
			long surplusDays = commonService.calcThisYearSurplusDays();
			jsonObject = new  ResultJSONObject("000", "计算今年剩余天数成功");
			jsonObject.put("surplusDays", surplusDays);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getPersonalCatalogAndService");
			jsonObject = new ResultJSONObject("calc_fail", "计算今年剩余天数错误");
			logger.error("计算今年剩余天数错误", e);
		}
		return jsonObject;
	}
	
	/** 分享通用接口 */
	@RequestMapping("/getShareHtml")
	@SystemControllerLog(description = "分享通用接口")
	public @ResponseBody Object getShareHtml() {
		
		JSONObject jsonObject = new ResultJSONObject();
		try {
			jsonObject = commonService.getShareHtml();
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getShareHtml");
			jsonObject = new ResultJSONObject("getShareHtml_exception", "分享通用接口失败");
			logger.error("分享通用接口失败", e);
		}
		return jsonObject;
	}
	
	/** 静态活动页接口 */
	@RequestMapping("/getStaticActivityHtml")
	@SystemControllerLog(description = "静态活动页接口")
	public @ResponseBody Object getStaticActivityHtml(String act_key) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("act_key", act_key);
		JSONObject jsonObject = new ResultJSONObject();
		try {
			jsonObject = commonService.getStaticActivityHtml(param);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getStaticActivityHtml");
			jsonObject = new ResultJSONObject("getStaticActivityHtml_exception", "静态活动接口失败");
			logger.error("静态活动页接口失败", e);
		}
		return jsonObject;
	}

	/** 活动区域列表 */
	@RequestMapping("/getActivityList")
	@SystemControllerLog(description = "活动区域列表")
	public @ResponseBody Object getActivityList(@RequestParam Map<String,Object> param) {
		JSONObject jsonObject = new ResultJSONObject();
		String entrance = StringUtil.null2Str(param.get("entrance"));
		if(StringUtil.isNullStr(entrance)){
			jsonObject = new ResultJSONObject("001", "活动入口参数不能为空");
			return jsonObject;
		}
		try {
			jsonObject = commonService.getActivityList(param);
		} catch (Exception e) {
			MsgTools.sendMsgOrIgnore(e, "getActivityList");
			jsonObject = new ResultJSONObject("getActivityList_exception", "活动区域列表接口失败");
			logger.error("活动区域列表接口失败", e);
		}
		return jsonObject;
	}
	
	
	/** 无报价方案  */
	@RequestMapping("/noPlan")
	@SystemControllerLog(description = "无报价方案")
	public void  noPlan() {
		userOrderService.handNoBidOrder();
	}
	
	
	/** 查询交易明细导航列表 */
	@RequestMapping("/queryPaymentNavilList")
	@SystemControllerLog(description = "查询交易明细导航列表")
	public @ResponseBody Object  queryPaymentNavilList() {
		JSONObject jsonObject =	null;
		try{
			jsonObject = this.commonService.getPaymentNaviList();
		}catch(Exception e){
			MsgTools.sendMsgOrIgnore(e, "queryPaymentNavilList");
			jsonObject = new ResultJSONObject("queryPaymentNavilList_exception", "查询交易明细导航栏列表异常");
			logger.error("查询交易明细导航栏列表异常", e);	
		}
		return jsonObject;
	}
	
	
	public Map<String,Integer> getRepeateUpdate() {
			return commonService.getRestrictUpdate();
	}
	
	/** 查询帮助反馈列表 */
	@RequestMapping("/getSystemHelpList")
	@SystemControllerLog(description = "查询帮助反馈列表")
	public @ResponseBody Object  getSystemHelpList() {
		JSONObject jsonObject =	null;
		try{
			jsonObject = this.commonService.getSystemHelpList();
		}catch(Exception e){
			MsgTools.sendMsgOrIgnore(e, "getSystemHelpList");
			jsonObject = new ResultJSONObject("getSystemHelpList_exception", "查询帮助反馈列表异常");
			logger.error("查询帮助反馈列表异常", e);	
		}
		return jsonObject;
	}
	
	/**保存帮助反馈页点赞情况 */
	@RequestMapping("/saveHelpEvaluation")
	@SystemControllerLog(description = "保存帮助反馈页点赞情况")
	public @ResponseBody Object  saveHelpEvaluation(@RequestParam Map<String,Object> param) {
		JSONObject jsonObject =	null;
		try{
			jsonObject = this.commonService.saveHelpEvaluation(param);
		}catch(Exception e){
			MsgTools.sendMsgOrIgnore(e, "saveHelpEvaluation");
			jsonObject = new ResultJSONObject("saveHelpEvaluation_exception", "保存帮助反馈页点赞异常");
			logger.error("保存帮助反馈页点赞异常", e);	
		}
		return jsonObject;
	}

	/**统计短信启动APP次数 */
	@RequestMapping("/statisticsStartCount")
	@SystemControllerLog(description = "统计短信启动APP次数")
	public @ResponseBody Object statisticsStartCount(@RequestParam Map<String,Object> param){
		JSONObject jsonObject =	null;
		try{
			Long userId=StringUtil.nullToLong(param.get("userId"));
			jsonObject = this.commonService.statisticsStartCount(userId);
		}catch(Exception e){
			MsgTools.sendMsgOrIgnore(e, "statisticsStartCount",param.toString());
			jsonObject = new ResultJSONObject("statisticsStartCount_exception", "统计短信启动APP次数");
			logger.error("统计短信启动APP次数", e);	
		}
		return jsonObject;
	}
	
	/**获取推荐服务商列表 */
	@RequestMapping("/recommendMerchantList")
	@SystemControllerLog(description = "获取推荐服务商列表")
	public @ResponseBody Object recommendMerchantList(@RequestParam Map<String,Object> param){
		JSONObject jsonObject =	null;
		try{
			// 省份城市decode
			String province = StringUtil.null2Str(param.get("province"));
			String city = StringUtil.null2Str(param.get("city"));
			param.put("province", URLDecoder.decode(province, "UTF-8"));
			param.put("city", URLDecoder.decode(city, "UTF-8"));
			jsonObject = this.commonService.recommendMerchantList(param);
		}catch(Exception e){
			MsgTools.sendMsgOrIgnore(e, "recommendMerchantList",param.toString());
			jsonObject = new ResultJSONObject("recommendMerchantList_exception", "获取推荐服务商列表");
			logger.error("获取推荐服务商列表", e);	
		}
		return jsonObject;
	}
	
	
}
