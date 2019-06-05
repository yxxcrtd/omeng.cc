package com.shanjin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IMerchantCacheService;
import com.shanjin.cache.service.impl.GenericCacheServiceImpl;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.*;
import com.shanjin.dao.*;
import com.shanjin.exception.ApplicationException;
import com.shanjin.model.RuleConfig;
import com.shanjin.outServices.aliOss.AliOssUtil;
import com.shanjin.push.PushAndroidMessageByProxy;
import com.shanjin.push.PushMessageUtil;
import com.shanjin.service.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.shanjin.common.constant.Constant.IS_SEND_MQ_TO_C_PLAN_WITH_INCLUDE_CONSULTANT;
import static com.shanjin.common.constant.Constant.IS_SEND_MQ_TO_C_PLAN_WITH_OPEN_INCREASE_SERVICE;
import static com.shanjin.common.util.DateUtil.*;
import static com.shanjin.common.util.StringUtil.*;

@Service("myMerchantervice")
public class MyMerchantServiceImpl implements IMyMerchantService {
	// 本地异常日志记录对象
	private static final Logger logger = Logger
			.getLogger(MyMerchantServiceImpl.class);

	//抢单金规则
	private static final String  GRAP_MOENY_RULE= "grap_money";
	
	private static final String  VEHICLE_INSURANCE_RULE= "vehicle_insurance";
	
	//VIP logo
	private static final String  VIP_TAG = "vip_logo_show";

	// Private consultant
	private static final String PRIVATE_CONSULTANT = "adviser_call";
	
	@Resource
	private IMerchantInfoDao iMerchantInfoDao;

	@Resource
	private IMerchantAttachmentDao iMerchantAttachmentDao;

	@Resource
	private IAndroidAppPushConfigDao androidAppPushConfigDao;

	@Resource
	private IMerchantEmployeesDao iMerchantEmployeesDao;

	@Resource
	private IMerchantVouchersPermissionsDao iMerchantVouchersPermissionsDao;

	@Resource
	private IMerchantStatisticsDao iMerchantStatisticsDao;

	@Resource
	private IEvaluationDao evaluationDao;

	@Resource
	private IMerchantWithdrawDao iMerchantWithdrawDao;

	@Resource
	private IMerchantServiceTypeDao iMerchantServiceTypeDao;

	@Resource
	private IMerchantContactDao iMerchantContactDao;

	@Resource
	private IVouchersInfoDao iVouchersInfoDao;

	@Resource
	private IMerchantPushDao iMerchantPushDao;

	@Resource
	private IMerchantAuthDao iMerchantAuthDao;

	@Resource
	private IMerchantAlbumDao iMerchantAlbumDao;

	@Resource
	private IMerchantPhotoDao iMerchantPhotoDao;

	@Resource
	private IUserMerchant2Dao userMerchantDao;

	@Resource
	private IMerchantEmployeesNumApplyDao iMerchantEmployeesNumApplyDao;

	@Resource
	private IMerchantVipApplyDao iMerchantVipApplyDao;

	@Resource
	private ICommonCacheService commonCacheService;

	@Resource
	private IValidateService validateService;

	@Resource
	private IMerchantCacheService merchantCacheService;

	@Resource
	private IDictionaryService dictionaryService;

	@Resource
	private IMerchantPayService iMerchantPayService;

	@Resource
	private IMerchantGoodsClassificationDao iMerchantGoodsClassificationDao;

	@Resource
	private IMerchantGoodsDao iMerchantGoodsDao;

	@Resource
	private IMerchantForSearchDao mertchantDao;

	@Resource
	private IMerchantGoodsClassificationRelationDao iMerchantGoodsClassificationRelationDao;

	@Resource
	private ICommonService commonService;

	@Resource
	private IMerchantServiceTagDao iMerchantServiceTagDao;

	@Resource
	private IGxfwIndexDao gxfwIndexDao;

	@Resource
	private IMerchantValueAddServiceDao iMerchantValueAddServiceDao;

	@Resource
	private IValueAddedIncomeService valueAddedIncomeService;

	@Resource
	private IElasticSearchService elasticSearchService;

	@Resource
	private IMessageCenterService messageCenterService;

	@Resource
	private IOrderDao iOrderDao;

	@Resource
	private IPushService pushService;
	
	@Resource
	private IMyIncomeService myMerchantService;
	
	@Resource
	private IncService incService;

    @Resource
    private IMerchantPlanDao merchantPlanDao;

	@Resource
	private IMerchantPaymentDetailsDao merchantPaymentDetailsDao;
	
	/** 登陆界面 获取验证码 */
	@Override
	public JSONObject getVerificationCode(String appType, String phone,
			String clientId, String ip) throws Exception {
		JSONObject jsonObject = null;
		// 生成验证码
		String verificationCode = BusinessUtil.createVerificationCode(phone);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
		paramMap.put("verificationCode", verificationCode);
		paramMap.put("clientId", clientId);
		paramMap.put("appType", appType);
		paramMap.put("ip", ip);
		// 缓存商户预验证信息
		commonCacheService.setObject(paramMap,
				CacheConstants.MERCHANT_PRE_VERIFY_TIMEOUT,
				CacheConstants.MERCHANT_PRE_VERIFY, phone, appType);
		// 短信发送验证码
		jsonObject = BusinessUtil.sendVerificationCode(phone, verificationCode,
				1);
		return jsonObject;
	}

	/** 登陆界面 获取验证码 */
	@Override
	public JSONObject getVoiceVerificationCode(String appType, String phone,
			String clientId, String ip) throws Exception {
		JSONObject jsonObject = null;
		// 生成验证码
		String verificationCode = BusinessUtil.createVerificationCode(phone);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
		paramMap.put("verificationCode", verificationCode);
		paramMap.put("clientId", clientId);
		paramMap.put("appType", appType);
		paramMap.put("ip", ip);
		// 缓存商户预验证信息
		commonCacheService.setObject(paramMap,
				CacheConstants.MERCHANT_PRE_VERIFY_TIMEOUT,
				CacheConstants.MERCHANT_PRE_VERIFY, phone, appType);
		// 短信发送验证码
		jsonObject = BusinessUtil.sendVerificationCode(phone, verificationCode,
				2);
		return jsonObject;
	}

	/** 登陆界面 验证验证码 */
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public JSONObject validateVerificationCode(String appType, String phone,
			String verificationCode, String clientId, String clientType,
			String pushId) {

		JSONObject jsonObject = new ResultJSONObject();
		if (pushId == null || "".equals(pushId)) {
			pushId = clientId;
		}
		Long userId = null;
		try {
			Map<String, Object> merchantPreVerifyCached = (Map<String, Object>) commonCacheService
					.getObject(CacheConstants.MERCHANT_PRE_VERIFY, phone,
							appType);
			if (merchantPreVerifyCached == null
					|| merchantPreVerifyCached.isEmpty()) {
				// 缓存过期
				return new ResultJSONObject("verificationCode_past", "验证码已经失效");
			}
			if (!merchantPreVerifyCached.get("verificationCode").equals(
					verificationCode)) {
				return new ResultJSONObject("verificationCode_error", "请填写正确的验证码");
			}

			// 判断appType是否是合并的，如果是合并的则转换一下
			if (!BusinessUtil.handlerAppType(appType)) {
				return new ResultJSONObject("001", "此应用已合并，请升级后登录");
			}

			Map<String, Object> merchantInfo = null;
			Long merchantId = null;
			final Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("phone", phone);
			paramMap.put("appType", appType);
			Map<String, Object> merchantInfoForLoginCached = (Map<String, Object>) commonCacheService
					.getObject(CacheConstants.MERCHANT_INFO_FOR_LOGIN, phone,
							appType);
			if (merchantInfoForLoginCached == null
					|| merchantInfoForLoginCached.isEmpty()) {
				// 商户信息（登陆时使用的信息）
				merchantInfoForLoginCached = this.iMerchantEmployeesDao
						.selectMerchantInfoForLoginByPhone(paramMap);
				if (merchantInfoForLoginCached == null
						|| merchantInfoForLoginCached.isEmpty()) {
					String key = DynamicKeyGenerator.generateDynamicKey();

					// 判断此手机号在user_info表中是否已存在
					Map<String, Object> userInfo = iMerchantEmployeesDao
							.getUserInfoByPhone(phone);
					if (userInfo == null) {
						userInfo = new HashMap<String, Object>();
						userId = IdGenerator.generateID(18);
						userInfo.put("userId", "" + userId);
						userInfo.put("userKey", key);
						userInfo.put("phone", phone);
						userInfo.put("verificationCode", verificationCode); // 此项目待删除
						userInfo.put("ip", Constant.EMPTY);
						userInfo.put("province", Constant.EMPTY);
						userInfo.put("city", Constant.EMPTY);
						this.iMerchantEmployeesDao.insertUserInfo(userInfo);// 保存到user_info表
					}

					// 创建新商户及相关信息，并放入缓存
					merchantInfo = new HashMap<String, Object>();
					String ip = (String) merchantPreVerifyCached.get("ip");

					// String[]
					// provinceAndCity=BusinessUtil.getProvinceAndCityByIp(ip);
					merchantInfo.put("ip", ip);
					merchantInfo.put("province", "");
					merchantInfo.put("city", "");

					merchantId = IdGenerator.generateID(18);
					merchantInfo.put("merchantId", merchantId);
					merchantInfo.put("phone", phone);
					merchantInfo.put("verificationCode", verificationCode);
					merchantInfo.put("appType", appType);
					merchantInfo.put("clientId", clientId);

					// 根据appType查询catalog表的主键ID，用于老版本商户入驻，新老版本的过渡性方案
					merchantInfo.put("catalogId", this.iMerchantInfoDao
							.selectCatalogIdFromCatalog(appType));

					// 注册新商户
					this.iMerchantInfoDao.insertMerchantInfo(merchantInfo);// 保存到merchant_info表
					// 保存商户默认图标
					merchantInfo.put("path",
							Constant.DEFAULT_MERCHANT_PORTRAIT_PTAH);
					this.iMerchantAttachmentDao
							.insertMerchantIcon(merchantInfo);
					// 保存商户的联系方式（默认是注册的手机号码）
					this.iMerchantContactDao.insertTelephone(merchantInfo);

					// 员工类型 1：老板
					merchantInfo.put("employeesType", 1);
					// 保存注册的手机号码,保存验证码
					merchantInfo.put("employeeKey", key);
					merchantInfo.put("userId", userInfo.get("userId"));
					this.iMerchantEmployeesDao.insertPhone(merchantInfo);// 保存到merchant_employees表表
					Long employeesId = (Long) merchantInfo.get("employeesId");

					// 商户统计信息初期化
					Map<String, Object> moneyMap = commonService
							.getConfigurationInfoByKey("register_sendMoney_num");
					String money = BusinessUtil.registerGiveMoney(moneyMap);// 判断是否要注册送钱
					merchantInfo.put("orderSurplusPrice", money);
					this.iMerchantStatisticsDao
							.insertMerchantStatistics(merchantInfo);

					// 赠送员工
					Map<String, Object> employeesMap = commonService
							.getConfigurationInfoByKey("sent_employees_num");
					String employeesNum = BusinessUtil
							.registerGiveEmployeesNum(employeesMap);// 赠送员工
					Map<String, Object> employeesNumMap = new HashMap<String, Object>();
					employeesNumMap.put("merchantId", merchantId);
					employeesNumMap.put("employeeNum", employeesNum);
					iMerchantEmployeesNumApplyDao
							.setMerchantEmployeesNum(employeesNumMap);

					merchantInfoForLoginCached = new HashMap<String, Object>();
					merchantInfoForLoginCached.put("merchantId", merchantId);
					merchantInfoForLoginCached.put("employeesType", 1);
					merchantInfoForLoginCached.put("phone", phone);
					merchantInfoForLoginCached.put("employeesId", employeesId);
				}

				// 缓存商户信息（登陆时使用的信息）
				commonCacheService.setObject(merchantInfoForLoginCached,
						CacheConstants.MERCHANT_INFO_FOR_LOGIN_TIMEOUT,
						CacheConstants.MERCHANT_INFO_FOR_LOGIN, phone, appType);
			}

			Map<String, Object> merchantOpenedInfo = this.iMerchantEmployeesDao
					.selectOpenedInfo(paramMap);
			if (merchantOpenedInfo == null) {
				merchantInfoForLoginCached.put("isOpened", 0);
				merchantInfoForLoginCached.put("haveService", 0);
			} else {
				Long haveService = (Long) merchantOpenedInfo.get("haveService");
				String name = (String) merchantOpenedInfo.get("name");
				String locationAddress = (String) merchantOpenedInfo
						.get("locationAddress");
				if (haveService.equals(new Long(0))
						|| StringUtils.isEmpty(name)
						|| StringUtils.isEmpty(locationAddress)
						|| locationAddress.equals("null")) {
					merchantInfoForLoginCached.put("isOpened", 0);
					merchantInfoForLoginCached.put("haveService", 0);
				} else {
					merchantInfoForLoginCached.put("isOpened", 1);
					merchantInfoForLoginCached.put("haveService", haveService);
				}
			}
			merchantInfoForLoginCached.put("encryptedKey",
					this.keyEdit(phone, clientId));

			// 2015年12月12日 登陆之后，把商户的名称和头像也返回 李焕民
			paramMap.put("merchantId", StringUtil
					.null2Str(merchantInfoForLoginCached.get("merchantId")));
			merchantInfo = iMerchantInfoDao.selectMerchantBasicInfo(paramMap);
			if (merchantInfo != null) {
				merchantInfoForLoginCached.put("name",
						StringUtil.null2Str(merchantInfo.get("name")));
				if (merchantInfo.get("path") == null
						|| !StringUtil.isNotEmpty(merchantInfo.get("path"))) {
					merchantInfo.put("path",
							Constant.DEFAULT_MERCHANT_PORTRAIT_PTAH);
				}
				BusinessUtil.disposePath(merchantInfo, "path");
				merchantInfoForLoginCached.put("portraitPath", merchantInfo
						.get("path").toString());
			}

			jsonObject.put("merchantInfoForLogin", merchantInfoForLoginCached);
			merchantId = Long.valueOf(String.valueOf(merchantInfoForLoginCached
					.get("merchantId")));

			// 修改商户推送信息
			// this.updateMerchantPush(appType, phone, merchantId, pushId,
			// clientId, clientType);

			paramMap.put("clientId", clientId);
			paramMap.put("pushId", pushId);
			paramMap.put("clientType", clientType);
			if (userId == null) {
				userId = iMerchantEmployeesDao.getUserIdByMerchantId(paramMap);
			}
			paramMap.put("userId", userId);
			paramMap.put("pushType", 8);
			paramMap.put("data", "isExit,1," + merchantId + "," + appType);
			updatePushUserDevice(paramMap);

			jsonObject.put("resultCode", "000");
			jsonObject.put("message", "验证码验证通过,成功登陆");

		} catch (Exception ex) {
			logger.error("", ex);
			throw new ApplicationException(ex, "validate_failure", "验证失败");
		}
		return jsonObject;
	}

	/** 开店 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject openShop(String appType, String userId, String phone)
			throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		String catalogId = this.iMerchantInfoDao
				.selectCatalogIdFromCatalog(appType);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
		paramMap.put("appType", appType);
		Map<String, Object> merchantInfoIsExits = this.iMerchantEmployeesDao
				.selectMerchantInfoForLoginByPhone(paramMap);
		Map<String, Object> merchantInfoForOpen = new HashMap<String, Object>();
		if (merchantInfoIsExits == null || merchantInfoIsExits.isEmpty()) {

			Long merchantId = IdGenerator.generateID(18);
			
			String isOpenShop=StringUtil.null2Str(commonCacheService.getObject(CacheConstants.IS_OPENSHOP,merchantId+"",catalogId+""));
			if(isOpenShop.equals("1")){
				return new ResultJSONObject("001", "系统正在注册店铺中，请耐心等待！");
			}
			
			// 创建新商户及相关信息
			Map<String, Object> merchantInfo = new HashMap<String, Object>();
			merchantInfo.put("province", "");
			merchantInfo.put("city", "");
			merchantInfo.put("appType", appType);
			merchantInfo.put("catalogId", catalogId);
			merchantInfo.put("merchantId", merchantId);
			merchantInfo.put("phone", phone);
			
			commonCacheService.setObject("1", 120, CacheConstants.IS_OPENSHOP, merchantId+"",catalogId+"");

			// 注册新商户
			this.iMerchantInfoDao.insertMerchantInfo(merchantInfo);
			// 保存商户默认图标
			merchantInfo.put("path", Constant.DEFAULT_MERCHANT_PORTRAIT_PTAH);
			this.iMerchantAttachmentDao.insertMerchantIcon(merchantInfo);
			// 保存商户的联系方式（默认是注册的手机号码）
			this.iMerchantContactDao.insertTelephone(merchantInfo);

			// 员工类型 1：老板
			merchantInfo.put("employeesType", 1);
			// 用户Id
			merchantInfo.put("userId", userId);
			this.iMerchantEmployeesDao.insertPhone(merchantInfo);
			Long employeesId = (Long) merchantInfo.get("employeesId");

			// 商户统计信息初期化,开店送钱
			Map<String, Object> moneyMap = commonService
					.getConfigurationInfoByKey("register_sendMoney_num");
			String money = BusinessUtil.registerGiveMoney(moneyMap);// 判断是否要注册送钱
			merchantInfo.put("orderSurplusPrice", money);
			this.iMerchantStatisticsDao.insertMerchantStatistics(merchantInfo);
			if (!"0".equals(money)) {
				// 保存送钱记录
				paramMap.put("merchantId", merchantId);
				paramMap.put("orderId", 0);
				paramMap.put("payType", 4);
				paramMap.put("payMoney", money);
				iMerchantPayService.addMerchantOrderPaymentDetails(paramMap);
			}

			// 赠送员工
			Map<String, Object> employeesMap = commonService
					.getConfigurationInfoByKey("sent_employees_num");
			String employeesNum = BusinessUtil.registerGiveEmployeesNum(employeesMap);// 赠送员工
			Map<String, Object> employeesNumMap = new HashMap<String, Object>();
			employeesNumMap.put("merchantId", merchantId);
			employeesNumMap.put("employeeNum", employeesNum);
			this.iMerchantEmployeesNumApplyDao
					.setMerchantEmployeesNum(employeesNumMap);

			merchantInfoForOpen.put("merchantId", merchantId);
			merchantInfoForOpen.put("employeesId", employeesId);
			merchantInfoForOpen.put("employeesType", 1);
			merchantInfoForOpen.put("catalogId", catalogId);
			jsonObject.put("resultCode", "000");

			Integer merchantTotal = (Integer) commonCacheService
					.getObject(CacheConstants.MERCHANT_TOTAL);
			if (merchantTotal == null) {
				merchantTotal = this.iMerchantInfoDao.selectMerchantTotal();
				commonCacheService.setObject(merchantTotal,
						CacheConstants.MERCHANT_TOTAL_EXPIRTIME,
						CacheConstants.MERCHANT_TOTAL);
			}
			// 计算是第多少位开店的商户
			jsonObject.put("merchantTotal", merchantTotal + 1);

		} else {
			merchantInfoForOpen.put("merchantId",
					merchantInfoIsExits.get("merchantId"));
			merchantInfoForOpen.put("employeesId",
					merchantInfoIsExits.get("employeesId"));
			merchantInfoForOpen.put("employeesType",
					merchantInfoIsExits.get("employeesType"));

			Long haveService = (Long) merchantInfoIsExits.get("haveService");
			String name = (String) merchantInfoIsExits.get("name");
			String locationAddress = (String) merchantInfoIsExits
					.get("locationAddress");
			if (haveService.equals(new Long(0)) || StringUtils.isEmpty(name)
					|| StringUtils.isEmpty(locationAddress)
					|| locationAddress.equals("null")) {
				// merchantInfoForOpen.put("isOpened", 0);
				// merchantInfoForOpen.put("haveService", 0);
				Integer merchantTotal = (Integer) commonCacheService
						.getObject(CacheConstants.MERCHANT_TOTAL);
				if (merchantTotal == null) {
					merchantTotal = this.iMerchantInfoDao.selectMerchantTotal();
					commonCacheService.setObject(merchantTotal,
							CacheConstants.MERCHANT_TOTAL_EXPIRTIME,
							CacheConstants.MERCHANT_TOTAL);
				}
				// 计算是第多少位开店的商户
				jsonObject.put("merchantTotal", merchantTotal + 1);
				jsonObject.put("resultCode", "000");
			} else {
				jsonObject.put("merchantTotal", -1);
				// merchantInfoForOpen.put("isOpened", 1);
				// merchantInfoForOpen.put("haveService", haveService);
				jsonObject.put("resultCode", "001");
				jsonObject.put("message", "店铺已经存在");
				return jsonObject;
			}
			merchantInfoForOpen.put("name", name);
			merchantInfoForOpen.put("locationAddress", locationAddress);
		}
		merchantInfoForOpen.put("catalogId", catalogId);

		paramMap.put("merchantId", merchantInfoForOpen.get("merchantId"));
		Map<String, Object> merchantInfo = iMerchantInfoDao
				.selectMerchantBasicInfo(paramMap);
		if (merchantInfo != null) {
			if (StringUtil.isEmpty(merchantInfo.get("path"))) {
				merchantInfo.put("path",
						Constant.DEFAULT_MERCHANT_PORTRAIT_PTAH);
			}
			BusinessUtil.disposePath(merchantInfo, "path");
			merchantInfoForOpen
					.put("path", merchantInfo.get("path").toString());
		}
		jsonObject.put("merchantInfoForOpen", merchantInfoForOpen);

		return jsonObject;
	}

	// /** 我的店铺信息查询 */
	// @Override
	// public JSONObject selectMyMerchant(String appType, String phone, Long
	// merchantId) throws Exception {
	// JSONObject jsonObject = new ResultJSONObject();
	// Map<String, Object> paramMap = new HashMap<String, Object>();
	// paramMap.put("phone", phone);
	// paramMap.put("appType", appType);
	// paramMap.put("merchantId", merchantId);
	//
	// Map<String, Object> merchantInfo = basicInfo(merchantId);
	// if (merchantInfo == null) {
	// return new ResultJSONObject("merchant_info_null", "店铺信息为空");
	// }
	// // 服务项目
	// merchantInfo.put("serviceItem", serviceItemNameEdit(appType,
	// merchantId));
	// // 评价数量
	// int evaluationOrderNum =
	// this.evaluationDao.getMerchantEvaluationNum(merchantId);
	// if (evaluationOrderNum == 0) {
	// // 没有用户评价的时候设置默认值
	// merchantInfo.put("starLevel", 5);
	// } else {
	// Map<String, Object> previewInfoMap =
	// iMerchantStatisticsDao.selectPreviewInfo(paramMap);
	// if (previewInfoMap != null) {
	// int totalAttitudeEvaluation =
	// Integer.parseInt(previewInfoMap.get("totalAttitudeEvaluation") == null ?
	// "0" : previewInfoMap.get("totalAttitudeEvaluation").toString());
	// int totalQualityEvaluation =
	// Integer.parseInt(previewInfoMap.get("totalQualityEvaluation") == null ?
	// "0" : previewInfoMap.get("totalQualityEvaluation").toString());
	// int totalSpeedEvaluation =
	// Integer.parseInt(previewInfoMap.get("totalSpeedEvaluation") == null ? "0"
	// : previewInfoMap.get("totalSpeedEvaluation").toString());
	// // 总服务态度评价+总服务质量评价+总服务速度评价
	// int totalEvaluation = totalAttitudeEvaluation + totalQualityEvaluation +
	// totalSpeedEvaluation;
	// // 星级
	// BigDecimal starLevel = new BigDecimal(totalEvaluation).divide(new
	// BigDecimal(evaluationOrderNum).multiply(new BigDecimal(3)), 0,
	// BigDecimal.ROUND_HALF_UP);
	// merchantInfo.put("starLevel", starLevel);
	// } else {
	// // 统计信息异常的时候
	// merchantInfo.put("starLevel", 5);
	// }
	// }
	//
	// /** start 2015-12-9 王瑞 ：查询是商户未读的消息 */
	// // 查询当前已读的最大的消息ID
	// Object maxMsgIdObj = iMerchantInfoDao.getMerchantMaxMsgId(merchantId);
	// long maxMsgId = Long.parseLong(maxMsgIdObj == null ? "0" :
	// maxMsgIdObj.toString());
	// // 查询大于当前最大消息ID的消息数目，即为未读消息数
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put("merchantId", merchantId);
	// map.put("maxMsgId", maxMsgId);
	// int unReadMsgCount = messageCenterService.getUnreadMsgCount(map);
	// merchantInfo.put("unReadMsgCount", unReadMsgCount);
	// /** end */
	//
	// jsonObject.put("resultCode", "000");
	// jsonObject.put("message", "店铺信息加载成功");
	// jsonObject.put("merchantInfo", merchantInfo);
	// return jsonObject;
	// }

	/** 我的店铺信息查询 */
	@Override
	public JSONObject selectMyMerchantV23(String appType, String phone,
			Long merchantId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
		paramMap.put("appType", appType);
		paramMap.put("merchantId", merchantId);

		Map<String, Object> info = merchantBasicInfo(merchantId);
		if (info == null) {
			return new ResultJSONObject("merchant_info_null", "店铺信息为空");
		}
		Map<String, Object> merchantInfo = new HashMap<String, Object>();
		merchantInfo.put("id", info.get("id"));
		merchantInfo.put("name", info.get("name"));
		merchantInfo.put("microWebsiteUrl", info.get("microWebsiteUrl"));
		merchantInfo.put("vipLevel", info.get("vipLevel"));
		merchantInfo.put("iconUrl", info.get("iconUrl"));
		merchantInfo.put("auth", info.get("auth"));
		merchantInfo.put("vipStatus", info.get("vipStatus"));
		merchantInfo.put("vipLevel", info.get("vipLevel"));
		merchantInfo.put("vipBackgroundUrl", info.get("vipBackgroundUrl"));

//		Map<String, Object> statisticsInfo = this.iMerchantStatisticsDao
//				.selectStatisticsInfo(paramMap);
//		if (statisticsInfo.get("totalCountEvaluation") == null) {
//			merchantInfo.put("totalCountEvaluation", 0);
//		} else {
//			merchantInfo.put("totalCountEvaluation",
//					statisticsInfo.get("totalCountEvaluation"));
//		}
		merchantInfo.put("totalCountEvaluation",info.get("evaluationCount"));
		// 魅力值
//		merchantInfo.put("charmValue", charmValueEdit(merchantId));
		merchantInfo.put("charmValue", info.get("charmValue"));
		// 相片总数
//		int photoTotal = this.iMerchantPhotoDao.selectPhotoTotal(paramMap);
		merchantInfo.put("photoTotal", info.get("photosCount"));

		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "店铺信息加载成功");
		jsonObject.put("merchantInfo", merchantInfo);
		return jsonObject;
	}

	/** 我的店铺信息查询 */
	@Override
	public JSONObject selectMyMerchantV24(String appType, String phone,
			Long merchantId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
		paramMap.put("appType", appType);
		paramMap.put("merchantId", merchantId);

		Map<String, Object> info = merchantBasicInfo(merchantId);
		if (info == null) {
			return new ResultJSONObject("merchant_info_null", "店铺信息为空");
		}
		Map<String, Object> merchantInfo = new HashMap<String, Object>();
		merchantInfo.put("name", info.get("name"));
		merchantInfo.put("iconUrl", info.get("iconUrl"));
		merchantInfo.put("isPrivateAssistant", info.get("isPrivateAssistant"));
		merchantInfo.put("businessType", info.get("businessType"));
		merchantInfo.put("vipStatus", info.get("vipStatus"));
		merchantInfo.put("vipLevel", info.get("vipLevel"));
		merchantInfo.put("vipBackgroundUrl", info.get("vipBackgroundUrl"));
		
//		补齐VIP 标识
//		merchantInfo.put("vipStatus", -1);
//		List<RuleConfig> rulConfigs=incService.getRuleConfig(merchantId);
//		if (rulConfigs!=null){
//			if (rulConfigs.get(0).isVipMerchantOrder()){
//				merchantInfo.put("vipStatus", 2);
//			}
//		}

		merchantInfo.put("vipStatus", info.get("vipStatus"));

		// 统计信息
//		Map<String, Object> map = statisticsInfoEdit(merchantId);
		merchantInfo.put("starLevel", info.get("starLevel"));
		merchantInfo.put("score", info.get("score"));
		merchantInfo.put("grabFrequency", info.get("grabFrequency"));
		merchantInfo.put("totalIncomePrice", info.get("totalIncomePrice"));

		// 魅力值
//		merchantInfo.put("charmValue", charmValueEdit(merchantId));
		merchantInfo.put("charmValue", info.get("charmValue"));

		// 相册总数 ,商户总数--- 改为取自缓存 2016.4.29 Revoke
		// Map cachededOutline = getCachedMerchantOutLine(merchantId,appType);
		// merchantInfo.put("photoTotal", cachededOutline.get("photoTotal"));
		// 暂时设置成0，过渡性方案
		merchantInfo.put("photoTotal", 0);

		if (appType.equals("gxfw")) {
			merchantInfo.put("goodsTotal", 0);
		} else {
			// merchantInfo.put("goodsTotal",
			// cachededOutline.get("goodsTotal"));
			// 暂时设置成0，过渡性方案
			merchantInfo.put("goodsTotal", 0);
		}

		// end of --revoke

		// begin of 增加商户类型，认证类型返回，增加粉丝数 ，活动配置 2016.4.29 Revoke
		merchantInfo.put("merchantType", info.get("merchantType"));
		merchantInfo.put("auth", info.get("auth"));

//		int collectionNum = getFans(merchantId);
		merchantInfo.put("collectionNum", info.get("fansCount"));

		/*
		 * JSONObject activity = commonService.getShareHtml();
		 * 
		 * jsonObject.put("shareActivity",
		 * activity.getJSONObject("shareActivity")); jsonObject.put("webUrl",
		 * activity.getString("webUrl"));
		 */

		// end of --Revoke

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		// 截止时间
		paramMap.put("cutoffTime", calendar.getTime());
		// 代金券总数（有效期内且没有被领取的代金券数量）
		// String catalogIds=getCatalogIdByAppType(appType);
		// if(StringUtil.isEmpty(catalogIds)){
		// merchantInfo.put("vouchersCount", 0);
		// }else{
		// paramMap.put("catalogIds", getCatalogIdByAppType(appType));
		// int vouchersCount =
		// this.iMerchantVouchersPermissionsDao.selectCurrentVouchersInfoCount(paramMap);
		// merchantInfo.put("vouchersCount", vouchersCount);
		// 暂时设置成0，过渡性方案
		merchantInfo.put("vouchersCount", 0);
		// }

		// 新订单数
		// int newOrderNum = this.iOrderDao.selectNewOrderNum(paramMap);
		// merchantInfo.put("newOrderNum", newOrderNum);
		// 暂时设置成0，过渡性方案
		merchantInfo.put("newOrderNum", 0);

		// 剪彩次数
//		Integer cuttingNum = this.iMerchantInfoDao.selectCuttingNum(paramMap);
//		if (cuttingNum == null) {
//			cuttingNum = 0;
//		}
		merchantInfo.put("cuttingNum", info.get("cuttingNum"));

		// 接单计划 0-未设置1-已设置
//		Integer alreadySetOrderPlan = this.iMerchantInfoDao
//				.selectAlreadySetOrderPlan(paramMap);
//		if (alreadySetOrderPlan == null) {
//			merchantInfo.put("alreadySetOrderPlan", 0);
//		} else {
//			merchantInfo.put("alreadySetOrderPlan", 1);
//		}
		merchantInfo.put("alreadySetOrderPlan", info.get("alreadySetOrderPlan"));

		//查询本月收入
		String nowDate=DateUtil.getNowYYYYMMDD();
		String startTime=nowDate.split("-")[0]+"-"+nowDate.split("-")[1]+"-01 00:00:00";
		String endTime=DateUtil.getNextMonth(nowDate.split("-")[0]+"-"+nowDate.split("-")[1])+" 00:00:00";
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		BigDecimal monthMoney=iMerchantWithdrawDao.getMoneyByTime(paramMap);
		merchantInfo.put("monthMoney", monthMoney);	
		
		//查询最新一个商品信息
//		paramMap.put("num", 1);
//		List<Map<String, Object>> topGoodsInfo = iMerchantGoodsDao.selectLastGoodsInfo(paramMap);
//		BusinessUtil.disposeManyPath(topGoodsInfo, "goodsPictureUrl");
//
//		merchantInfo.put("topGoodsInfo", topGoodsInfo);	
		merchantInfo.put("topGoodsInfo", info.get("last3GoodsInfo"));	
				
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "店铺信息加载成功");
		jsonObject.put("merchantInfo", merchantInfo);
		return jsonObject;
	}

	/** 店铺详细信息 */
	@Override
	public JSONObject merchantDetailInfo(Long merchantId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> info = merchantBasicInfo(merchantId);
		if (info == null) {
			return new ResultJSONObject("merchant_info_null", "店铺信息为空");
		}
		Map<String, Object> merchantInfo = new HashMap<String, Object>();
		merchantInfo.put("name", info.get("name"));
		merchantInfo.put("iconUrl", info.get("iconUrl"));
		merchantInfo.put("auth", info.get("auth"));
		merchantInfo.put("phone", info.get("phone"));
		merchantInfo.put("longitude", info.get("longitude"));
		merchantInfo.put("latitude", info.get("latitude"));
		merchantInfo.put("locationAddress", info.get("locationAddress"));
		merchantInfo.put("detailAddress", info.get("detailAddress"));
		merchantInfo.put("detail", info.get("detail"));
		merchantInfo.put("province", info.get("province"));
		merchantInfo.put("city", info.get("city"));
		merchantInfo.put("appType", info.get("appType"));
		merchantInfo.put("microWebsiteUrl", info.get("microWebsiteUrl"));
		merchantInfo.put("merchantType", info.get("merchantType"));
		merchantInfo.put("isPrivateAssistant", info.get("isPrivateAssistant"));
		merchantInfo.put("vipStatus", info.get("vipStatus"));
		merchantInfo.put("vipLevel", info.get("vipLevel"));
		merchantInfo.put("vipBackgroundUrl", info.get("vipBackgroundUrl"));
		

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		// 相片总数
//		int photoTotal = this.iMerchantPhotoDao.selectPhotoTotal(paramMap);
		merchantInfo.put("photoTotal", info.get("photosCount"));
		// 最新1张相片URL
		merchantInfo.put("last1PhotoUrl", info.get("topPhotosInfo"));

		// 商品总数
//		int goodsTotal = this.iMerchantGoodsDao.selectGoodsCount(paramMap);
		merchantInfo.put("goodsTotal", info.get("goodsCount"));
		// 商户最新3个商品信息
//		merchantInfo.put("last3GoodsInfo", last3GoodsInfo(merchantId));
		merchantInfo.put("last3GoodsInfo", info.get("last3GoodsInfo"));

		// 服务数量
		int serviceNum = this.iMerchantServiceTypeDao
				.selectMerchantServiceNum(paramMap);
		merchantInfo.put("serviceNum", serviceNum);

		// 统计信息
//		Map<String, Object> map = statisticsInfoEdit(merchantId);
//		merchantInfo.put("starLevel", map.get("starLevel"));
//		merchantInfo.put("score", map.get("score"));
//		merchantInfo.put("grabFrequency", map.get("grabFrequency"));
//		merchantInfo.put("serviceFrequency", map.get("serviceFrequency"));
//		merchantInfo.put("totalCountEvaluation",map.get("totalCountEvaluation"));
		merchantInfo.put("starLevel", info.get("starLevel"));
		merchantInfo.put("score", info.get("score"));
		merchantInfo.put("grabFrequency", info.get("grabFrequency"));
		merchantInfo.put("totalIncomePrice", info.get("totalIncomePrice"));
		merchantInfo.put("totalCountEvaluation",info.get("totalCountEvaluation"));

		// 评价统计
//		List<Map<String, String>> evaluationInfo = this.iOrderDao
//				.selectEvaluationCount(paramMap);
//		merchantInfo.put("evaluationInfo", evaluationInfo);
		merchantInfo.put("evaluationInfo", info.get("topEvaluation"));
		jsonObject.put("merchantInfo", merchantInfo);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "店铺详细信息加载成功");
		return jsonObject;
	}

	/** 店铺详细信息2 */
	@Override
	public JSONObject merchantDetailInfo_2(Long merchantId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> info = merchantBasicInfo(merchantId);
		if (info == null) {
			return new ResultJSONObject("merchant_info_null", "店铺信息为空");
		}
	
		
		Map<String, Object> merchantInfo = new HashMap<String, Object>();
		merchantInfo.put("name", info.get("name"));
		merchantInfo.put("iconUrl", info.get("iconUrl"));
		merchantInfo.put("auth", info.get("auth"));
		merchantInfo.put("phone", info.get("phone"));
		merchantInfo.put("longitude", info.get("longitude"));
		merchantInfo.put("latitude", info.get("latitude"));
        merchantInfo.put("mapType", info.get("mapType"));
		merchantInfo.put("locationAddress", info.get("locationAddress"));
		merchantInfo.put("detailAddress", info.get("detailAddress"));
		merchantInfo.put("detail", info.get("detail"));
		merchantInfo.put("province", info.get("province"));
		merchantInfo.put("city", info.get("city"));
		merchantInfo.put("appType", info.get("appType"));
		merchantInfo.put("microWebsiteUrl", info.get("microWebsiteUrl"));
		merchantInfo.put("merchantType", info.get("merchantType"));
		merchantInfo.put("isPrivateAssistant", info.get("isPrivateAssistant"));
		merchantInfo.put("fansCount", info.get("fansCount"));
		merchantInfo.put("vipStatus", info.get("vipStatus"));
		merchantInfo.put("vipLevel", info.get("vipLevel"));
		merchantInfo.put("vipBackgroundUrl", info.get("vipBackgroundUrl"));
		
		//补齐VIP 标识
//		merchantInfo.put("vipStatus", -1);
//		List<RuleConfig> rulConfigs=incService.getRuleConfig(merchantId);
//		if (rulConfigs!=null){
//			if (rulConfigs.get(0).isVipMerchantOrder()){
//				merchantInfo.put("vipStatus", 2);
//			}
//		}	

		merchantInfo.put("vipStatus", info.get("vipStatus"));
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);

		// 粉丝数
//		int collectionNum = getFans(merchantId);
		merchantInfo.put("collectionNum", info.get("fansCount"));

		// 相册
//		Map cachededOutline = getCachedMerchantOutLine(merchantId,
//				info.get("appType").toString());

		merchantInfo.put("photoTotal", info.get("photosCount"));
//		Map<String,Object> last4PhotoUrl = (Map<String, Object>) info.get("topPhotosInfo");
		merchantInfo.put("last4PhotoUrl", info.get("topPhotosInfo"));

		// 商品
//		if (!info.get("appType").toString().equals("gxfw")) {2016.09.14测试提：个性服务店铺预览和用户查看个性店铺需要显示商品
//			merchantInfo.put("goodsTotal", cachededOutline.get("goodsTotal"));从info获取，以前的缓存删除 2016.09.05 cuijiajun
//			// 商户最新3个商品信息
//			merchantInfo.put("last3GoodsInfo",
//					cachededOutline.get("lastGoodsInfo"));
			merchantInfo.put("goodsTotal", info.get("goodsCount"));
			// 商户最新3个商品信息
			merchantInfo.put("last3GoodsInfo", info.get("last3GoodsInfo"));
		
//		}

		// 服务数量
		// int serviceNum =
		// this.iMerchantServiceTypeDao.selectMerchantServiceNum(paramMap);
		// merchantInfo.put("serviceNum", serviceNum);

		// 服务类型
//		List<String> serviceTypeList = getCachedServiceTypeName(merchantId);
//		if (serviceTypeList.size() > 16) {
//			serviceTypeList = serviceTypeList.subList(0, 16);
//		}
//		merchantInfo.put("serviceTypeNames",
//				StringUtils.join(serviceTypeList.toArray(), ","));
		merchantInfo.put("serviceTypeNames",info.get("merchantServiceTypeNames"));
		
		// 统计信息
//		Map<String, Object> map = getCachedMerchantEstimate(merchantId);
		merchantInfo.put("starLevel", info.get("starLevel"));
		merchantInfo.put("score", info.get("score"));
		merchantInfo.put("grabFrequency", info.get("grabFrequency"));
		merchantInfo.put("totalCountEvaluation",info.get("evaluationCount"));

		// 评价
		// List<Map<String, String>> evaluationTag =
		// this.iOrderDao.selectEvaluationCount(paramMap);
		// merchantInfo.put("evaluationTag", evaluationTag);

		merchantInfo.put("userEvaluation", info.get("topEvaluation"));

		jsonObject.put("merchantInfo", merchantInfo);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "店铺详细信息加载成功");

		return jsonObject;
	}

	// 从缓存中获取服务项目列表 20160905商铺的缓存集中到一个，其他的清除掉 cuijiajun
//	private List<String> getCachedServiceTypeName(Long merchantId) {
//		Object cachedServiceType = commonCacheService.getObject(
//				CacheConstants.MERCHANT_SERVICES_LIST, merchantId.toString());
//		if (cachedServiceType == null) {
//			Map<String, Object> param = new HashMap<String, Object>();
//			param.put("merchantId", merchantId);
//			List<String> serviceTypeList = this.iMerchantServiceTypeDao
//					.selectMerchantServiceTypeName(param);
//			commonCacheService.setObject(serviceTypeList,
//					CacheConstants.MERCHANT_SERVICES_TIMEOUT,
//					CacheConstants.MERCHANT_SERVICES_LIST,
//					merchantId.toString());
//			return serviceTypeList;
//		}
//		return (List<String>) cachedServiceType;
//	}

//	/** 判断商户是否认证 */
//	@Override
//	public String checkIsNotAuth(Long merchantId) {
//		String authType = "0";
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("merchantId", merchantId);
//		Map<String, Object> authMap = this.iMerchantAuthDao
//				.selectAuthType(paramMap);
//		if (authMap != null) {
//			authType = authMap.get("authType") == null ? "0" : authMap.get(
//					"authType").toString();
//		}
//		return authType;
//	}
    /**
     * 判断商户是否认证
     *
     * @Retrun 0-不是认证商户；1-企业认证；2-个人认证
     */
    @Override
    public String checkIsNotAuth(Long merchantId) {
        // 从缓存中获取当前商户的的授权类型
        String stringAuthType = merchantCacheService.getAuthTypeFromMerchantCache(merchantId);
        if ("0".equals(stringAuthType)) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("merchantId", merchantId);
            Map<String, Object> authMap = this.iMerchantAuthDao.selectAuthType(paramMap);
            if (authMap != null) {
                stringAuthType = authMap.get("authType") == null ? "0" : authMap.get("authType").toString();
            }
            // 在缓存中添加当前商户的的授权类型
            merchantCacheService.setAuthTypeFromMerchantCache(merchantId, stringAuthType);
        }
        return stringAuthType;
    }

    /** 魅力值信息查询 */
	@Override
	public JSONObject charmValueInfo(String appType, Long merchantId)
			throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		Map<String, Object> charmValueInfo = this.iMerchantInfoDao
				.checkMerchantInfo(paramMap);
		String iconUrl = StringUtil.null2Str(charmValueInfo.get("iconUrl"));
		if (!StringUtil.isNullStr(iconUrl)
				&& !Constant.DEFAULT_MERCHANT_PORTRAIT_PTAH.equals(iconUrl)) {
			if (Constant.USE_ALIOSS) {
				iconUrl = AliOssUtil.getViewUrl(iconUrl);
			} else {
				// 设置商家图标的全路径（拼接服务器地址）
				iconUrl = Constant.NGINX_PATH + iconUrl;
			}
		} else {
			iconUrl = Constant.EMPTY;
		}
		// 获得当前认证状态。 authType:0-未认证 1-企业认证 2-个人认证,authStatus:0未认证，1已认证，2待审核
		Map<String, Object> authMap = this.iMerchantAuthDao
				.selectAuthType(paramMap);
		if (authMap != null) {
			String authType = authMap.get("authType") == null ? "0" : authMap
					.get("authType").toString();
			String authStatus = authMap.get("authStatus") == null ? "0"
					: authMap.get("authStatus").toString();
			charmValueInfo.put("authStatus", authStatus);
			charmValueInfo.put("auth", authType);

		} else {
			authMap = this.iMerchantAuthDao.selectMerchantAuth(paramMap);
			if (authMap != null) {
				String authType = authMap.get("authType") == null ? "0"
						: authMap.get("authType").toString();
				String authStatus = authMap.get("authStatus") == null ? "0"
						: authMap.get("authStatus").toString();
				charmValueInfo.put("authStatus", authStatus);
				charmValueInfo.put("auth", authType);
			} else {
				charmValueInfo.put("auth", 0);
				charmValueInfo.put("authStatus", 0);
			}
		}
		charmValueInfo.put("iconUrl", iconUrl);
		jsonObject.put("charmValueInfo", charmValueInfo);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "魅力值信息查询成功");
		return jsonObject;
	}

	/** 我的店铺信息查询 */
	@Override
	public JSONObject selectMyMerchantForUser(String appType, Long userId,
			Long merchantId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("appType", appType);
		Map<String, Object> info = merchantBasicInfo(merchantId);
		if (info == null) {
			return new ResultJSONObject("merchant_info_null", "店铺信息为空");
		}
//		info.put("lastPhotosUrl", last4Photo(merchantId));
		info.put("lastPhotosUrl", info.get("topPhotosInfo"));
		

		for (Map.Entry<String, Object> entry : info.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}
		paramMap.put("userId", userId);
		// paramMap.put("appType", appType);

		// 验证商家是否被用户收藏
		int collection = this.userMerchantDao.checkCollectionMerchant(paramMap);
		if (collection > 0) {
			collection = 1;
		} else {
			collection = 0;
		}
		jsonObject.put("collection", collection);

		// jsonObject.put("haveGoods",
		// this.iMerchantGoodsDao.selectGoodsCount(paramMap));

		int evaluationOrderNum = this.evaluationDao
				.getMerchantEvaluationNum(merchantId);
		Map<String, Object> previewInfoMap = iMerchantStatisticsDao
				.selectPreviewInfo(paramMap);
		if (previewInfoMap != null) {
			// 评价人数
			jsonObject.put("evaluationOrderNum", evaluationOrderNum);
			Integer totalAttitudeEvaluation = Integer.parseInt(previewInfoMap
					.get("totalAttitudeEvaluation") == null ? "0"
					: previewInfoMap.get("totalAttitudeEvaluation") + "");
			Integer totalQualityEvaluation = Integer.parseInt(previewInfoMap
					.get("totalQualityEvaluation") == null ? "0"
					: previewInfoMap.get("totalQualityEvaluation") + "");
			Integer totalSpeedEvaluation = Integer.parseInt(previewInfoMap
					.get("totalSpeedEvaluation") == null ? "0" : previewInfoMap
					.get("totalSpeedEvaluation") + "");
			int serviceFrequency = Integer.parseInt(previewInfoMap.get(
					"serviceFrequency").toString());
			// 总服务态度评价+总服务质量评价+总服务速度评价
			int totalEvaluation = totalAttitudeEvaluation
					+ totalQualityEvaluation + totalSpeedEvaluation;
			if (evaluationOrderNum == 0) {
				// 没有用户评价的时候设置默认值
				jsonObject.put("evaluationOrderNum", 0);
				jsonObject.put("starLevel", 5);
			} else {
				// 星级
				BigDecimal starLevel = new BigDecimal(totalEvaluation).divide(
						new BigDecimal(evaluationOrderNum)
								.multiply(new BigDecimal(3)), 0,
						BigDecimal.ROUND_HALF_UP);
				if (starLevel.compareTo(new BigDecimal(5)) > 0) {
					starLevel = new BigDecimal(5);
				}
				if (starLevel.compareTo(new BigDecimal(0)) < 0) {
					starLevel = new BigDecimal(0);
				}
				jsonObject.put("evaluationOrderNum", evaluationOrderNum);
				jsonObject.put("starLevel", starLevel);
			}
			jsonObject.put("serviceFrequency", serviceFrequency);
		} else {
			// 统计信息异常的时候
			jsonObject.put("evaluationOrderNum", 0);
			jsonObject.put("serviceFrequency", 0);
			jsonObject.put("starLevel", 5);
		}
		return jsonObject;
	}

	/** 我的店铺信息查询 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject selectMyMerchantForUserV24(Long userId, Long merchantId,
			double longitude, double latitude) throws Exception {
		JSONObject jsonObject = merchantDetailInfo(merchantId);
		Map<String, Object> merchantInfo = (Map<String, Object>) jsonObject
				.get("merchantInfo");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("merchantId", merchantId);
		// 验证商家是否被用户收藏
		int collection = this.userMerchantDao.checkCollectionMerchant(paramMap);
		if (collection > 0) {
			collection = 1;
		} else {
			collection = 0;
		}
		merchantInfo.put("collection", collection);

		// // 计算成交率
		// BigDecimal grabFrequency = new
		// BigDecimal(String.valueOf(merchantInfo.get("grabFrequency")));
		// BigDecimal serviceFrequency = new
		// BigDecimal(String.valueOf(merchantInfo.get("serviceFrequency")));
		// BigDecimal dealRate = serviceFrequency.divide(grabFrequency, 0,
		// BigDecimal.ROUND_HALF_UP);
		// merchantInfo.put("dealRate", dealRate + "%");

		// String formatDistance = null;
		// if (StringUtil.isNotEmpty(merchantInfo.get("longitude")) &&
		// StringUtil.isNotEmpty(merchantInfo.get("latitude"))) {
		// // 如果不为空则查询距离
		// Double distance =
		// LocationUtil.getDistance(Double.parseDouble(StringUtil.null2Str(merchantInfo.get("longitude"))),
		// Double.parseDouble(StringUtil.null2Str(merchantInfo.get("latitude"))),
		// longitude, latitude);
		// Long distanceValue = Math.round(distance);
		// if (distanceValue >= 1000l) {
		// BigDecimal bd = new BigDecimal(distance).divide(new
		// BigDecimal(1000));
		// bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		// formatDistance = "距离" + bd + "公里";
		// } else {
		// formatDistance = "距离" + Math.round(distance) + "米";
		// }
		// merchantInfo.put("distance", formatDistance);
		// } else {
		// // 值为-1的时候代表无经纬度数据，无法计算距离
		// merchantInfo.put("distance", -1);
		// }
		merchantInfo.put("distance", BusinessUtil.calcDistance(
				Double.parseDouble(StringUtil.null2Str(merchantInfo
						.get("longitude"))), Double.parseDouble(StringUtil
						.null2Str(merchantInfo.get("latitude"))), longitude,
				latitude));
		return jsonObject;
	}

	/** 我的店铺信息查询2 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject selectMyMerchantForUserV24_2(Long userId,
			Long merchantId, Double longitude, Double latitude)
			throws Exception {
		JSONObject jsonObject = merchantDetailInfo_2(merchantId);		
		
		Map<String, Object> merchantInfo = (Map<String, Object>) jsonObject.get("merchantInfo");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", userId);
		paramMap.put("merchantId", merchantId);
		
		
		
		// 验证商家是否被用户收藏
		int collection = this.userMerchantDao.checkCollectionMerchant(paramMap);
		if (collection > 0) {
			collection = 1;
		} else {
			collection = 0;
		}
		merchantInfo.put("collection", collection);

		// // 计算成交率
		// BigDecimal grabFrequency = new
		// BigDecimal(String.valueOf(merchantInfo.get("grabFrequency")));
		// BigDecimal serviceFrequency = new
		// BigDecimal(String.valueOf(merchantInfo.get("serviceFrequency")));
		// BigDecimal dealRate = serviceFrequency.divide(grabFrequency, 0,
		// BigDecimal.ROUND_HALF_UP);
		// merchantInfo.put("dealRate", dealRate + "%");

		merchantInfo.put("distance", BusinessUtil.calcDistance(
				Double.parseDouble(StringUtil.null2Str(merchantInfo
						.get("longitude"))), Double.parseDouble(StringUtil
						.null2Str(merchantInfo.get("latitude"))), longitude,
				latitude));
		return jsonObject;
	}

	/** 更新店铺名称,店铺详情介绍 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateNameAndDetail(String appType, Long merchantId,
			String name, String detail, String microWebsiteUrl,
			String invitationCode) {

		// 格式化$符号
		name = StringUtil.formatDollarSign(name);
		// 去空格
		name = name.trim();
        name = filterFourCharString(name);

		detail = StringUtil.formatDollarSign(detail);
        detail = filterFourCharString(detail);

		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户主键
		paramMap.put("merchantId", merchantId);
		// 店铺名称
		paramMap.put("name", name);
		// 店铺详情介绍
		paramMap.put("detail", detail);
		// 应用程序类型
		paramMap.put("appType", appType);
		if (StringUtils.isNotEmpty(invitationCode)) {
			// 邀请码
			paramMap.put("invitationCode", invitationCode);
			int checkResult = this.iMerchantInfoDao
					.checkInvitationCode(paramMap);
			if (checkResult == 0) {
				paramMap.remove("invitationCode");
			}
		}
		try {
			int count = this.iMerchantInfoDao.checkNameSingle(paramMap);
			if (count == 0) {
				this.iMerchantInfoDao.updateNameAndDetail(paramMap);
				jsonObject = new ResultJSONObject("000", "店铺名称保存成功");
//				// 清除商户缓存信息
//				commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//						StringUtil.null2Str(merchantId));
				//更新商铺缓存
				Map<String, Object> newValue = new HashMap<String, Object>();
				newValue.put("name", name);
				newValue.put("detail", detail);
				this.updateMerchantCache(merchantId, newValue);
				// 王瑞 2016-8-11 商户信息整合 暂时注释掉
				//修改商户缓存
//				Map<String, Object> merchantInfo = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
//				if(merchantInfo!=null){
//					merchantInfo.put("name", name);
//					merchantInfo.put("detail", detail);
//					commonCacheService.setObject(merchantInfo,CacheConstants.MERCHANT_BASIC_INFO_TIMEOUT,CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
//				}
			} else {
				jsonObject = new ResultJSONObject(
						"shop_name_used_by_other_merchant", "店铺名称已被其他商户使用");
			}
		} catch (Exception ex) {
			logger.error("店铺名称保存失败", ex);
			throw new ApplicationException(ex, "shop_name_save_failure",
					"店铺名称保存失败");
		}
		return jsonObject;
	}

	/** 更新店铺详情介绍 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateMerchantDetail(Long merchantId, String detail)
			throws Exception {
		JSONObject jsonObject = null;

		detail = StringUtil.formatDollarSign(detail);

		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户主键
		paramMap.put("merchantId", merchantId);
		// 店铺详情介绍
		paramMap.put("detail", detail);

		this.iMerchantInfoDao.updateMerchantDetail(paramMap);
		jsonObject = new ResultJSONObject("000", "店铺详情保存成功");
//		// 清除商户缓存信息
//		commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//				StringUtil.null2Str(merchantId));
		//更新商品缓存
		Map<String, Object> newValue = new HashMap<String, Object>();
		newValue.put("detail", detail);
		this.updateMerchantCache(merchantId, newValue);

		return jsonObject;
	}

	/** 商户头像上传 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateMerchantIcon(String appType, Long merchantId,
			String resultPath) {
		JSONObject jsonObject = null;
		try {
			// 需要存储到数据库的相对路径
			if (StringUtils.isNotEmpty(resultPath)) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				// 商户ID
				paramMap.put("merchantId", merchantId);
				// 附件路径
				paramMap.put("path", resultPath);
				// 更新图标信息
				this.iMerchantAttachmentDao.updateMerchantIcon(paramMap);
//				// 清除商户缓存信息
//				commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//						StringUtil.null2Str(merchantId));
				//更新商品缓存
				Map<String, Object> newValue = new HashMap<String, Object>();
				newValue.put("iconUrl", BusinessUtil.disposeImagePath(resultPath));
				this.updateMerchantCache(merchantId, newValue);
				
				// 王瑞 2016-8-11 商户信息整合
				//修改商户缓存
//				Map<String, Object> merchantInfo = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
//				if(merchantInfo!=null){
//					merchantInfo.put("iconUrl", resultPath);
//					commonCacheService.setObject(merchantInfo,CacheConstants.MERCHANT_BASIC_INFO_TIMEOUT,CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
//				}
				
				jsonObject = new ResultJSONObject("000", "店铺图标保存成功");

				// 2015年12月12日 商户保存头像成功之后，把URL也返回 李焕民
				Map<String, Object> attachMap = new HashMap<String, Object>();
				attachMap.put("portraitPath", resultPath);
				BusinessUtil.disposePath(attachMap, "portraitPath");
				jsonObject.put("portraitPath", attachMap.get("portraitPath")
						.toString());
			} else {
				jsonObject = new ResultJSONObject("shop_icon_save_failure",
						"店铺图标保存失败");
			}
		} catch (Exception ex) {
			logger.error("店铺图标保存失败", ex);
			throw new ApplicationException(ex, "shop_icon_save_failure",
					"店铺图标保存失败");
		}
		return jsonObject;
	}

	/**查询vip背景列表 */
	@Override
	public JSONObject selectVipBackgroundUrlList(String appType,long merchantId) {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		paramMap.put("merchantId", merchantId);
		
		List<Map<String, Object>> vipBackgroundUrlList =  (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.MERCHANT_VIP_BG);
		if(vipBackgroundUrlList == null || vipBackgroundUrlList.size() == 0){
			vipBackgroundUrlList = this.iMerchantInfoDao.selectVipBackgroundUrlList(paramMap);
			for (Map<String, Object> vipUrl : vipBackgroundUrlList) {
				// 商品图片路径的补全
				BusinessUtil.disposePath(vipUrl, "image");
			}
			commonCacheService.setObject(vipBackgroundUrlList,CacheConstants.MERCHANT_VIP_BG);
		}
		jsonObject.put("vipBackgroundUrlList", vipBackgroundUrlList);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "查询vip背景模板成功");
		
		
		return jsonObject;
	}

	/**设置vip背景*/
	@Override
	public JSONObject setVipBackgroundUrl(Long merchantId,String vipBackgroundUrl) {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("vipBackgroundUrl", vipBackgroundUrl);

		int a = this.iMerchantInfoDao.setVipBackgroundUrl(paramMap);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "设置vip背景模板成功");
		Map<String, Object> newValue = new HashMap<String, Object>();
		newValue.put("vipBackgroundUrl", BusinessUtil.disposeImagePath(vipBackgroundUrl));
		this.updateMerchantCache(merchantId, newValue);
		return jsonObject;
	}
    
	/** 商户已提供（1）未提供（0）的服务项目的编辑 */
	@Override
	public JSONObject selectMerchantServiceForChoose(String appType,
			Long merchantId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		paramMap.put("merchantId", merchantId);

		List<Map<String, Object>> serviceTypeList = this.iMerchantServiceTypeDao
				.selectMerchantServiceForChoose(paramMap);
		jsonObject.put("serviceTypeList", serviceTypeList);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "服务项目加载成功");

		return jsonObject;
	}

	/** 商户已提供（1）未提供（0）的服务项目的编辑-多级服务项目一切性查询 */
	@Override
	public JSONObject selectMerchantServiceForChooseMultilevel(String appType,
			Long merchantId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		paramMap.put("merchantId", merchantId);

		// List<Map<String, Object>> serviceTypeList =
		// this.iMerchantServiceTypeDao.selectMerchantServiceForChooseMultilevel(paramMap);
		// // 将查询的数组组合成上下级的样式
		// List<Map<String, Object>> serviceTypeListNew =
		// BusinessUtil.handlerMultilevelServiceType(serviceTypeList);

		String catalogId = this.iMerchantInfoDao.selectCatalogId(paramMap);
		paramMap.put("catalogId", catalogId);

		Map<String, Object> info = getCatalogTreeAndService(catalogId);
		List<String> idList = this.iMerchantServiceTypeDao
				.selectServiceTypeId(paramMap);
		List<Map<String, Object>> serviceTypeList = getChildsServiceTypeForOldVersion(
				info, idList);
		edit(serviceTypeList);
		jsonObject.put("serviceTypeList", serviceTypeList);
		// jsonObject.put("serviceTypeList", serviceTypeListNew);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "服务项目加载成功");

		return jsonObject;
	}

	/** 服务项目保存 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject insertMerchantServiceType(String appType,
			String serviceTypes, Long merchantId, String phone) {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		paramMap.put("merchantId", merchantId);
		paramMap.put("serviceTypes", serviceTypes);

		// 将原来的serviceType转换成自增Id
		if (StringUtil.isEmpty(serviceTypes)) {
			return new ResultJSONObject("error", "保存的服务项目为空");
		}
		// String
		// serviceTypeIds=iMerchantServiceTypeDao.serviceTypeConvertToId(paramMap);

		try {
			this.iMerchantServiceTypeDao.deleteMerchantServiceType(paramMap);
			List<Map<String, Object>> merchantServiceTypeList = new ArrayList<Map<String, Object>>();
			for (String serviceTypeId : serviceTypes.split(",")) {
				Map<String, Object> merchantServiceTypeMap = new HashMap<String, Object>();
				merchantServiceTypeMap.put("merchantId", merchantId);
				merchantServiceTypeMap.put("serviceTypeId",
						Long.valueOf(serviceTypeId));
				merchantServiceTypeMap.put("appType", appType);
				merchantServiceTypeList.add(merchantServiceTypeMap);
			}
			this.iMerchantServiceTypeDao
					.insertMerchantServiceType(merchantServiceTypeList);

			jsonObject = new ResultJSONObject("000", "服务项目保存成功");
			// 清除商户缓存信息
//			commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//					StringUtil.null2Str(merchantId));
			//更新商品缓存
			String merchantServiceTypeNames=iMerchantServiceTypeDao.getMerchantServiceTypeNames(paramMap);
			Map<String, Object> newValue = new HashMap<String, Object>();
			newValue.put("merchantServiceTypeNames", merchantServiceTypeNames);
			this.updateMerchantCache(merchantId, newValue);
			
			// 清除服务名称列表缓存-----Revoke 2016.4.27
			commonCacheService.deleteObject(
					CacheConstants.MERCHANT_SERVICES_LIST,
					StringUtil.null2Str(merchantId));

		} catch (Exception ex) {
			logger.error("服务项目保存失败", ex);
			throw new ApplicationException(ex, "service_item_save_failure",
					"服务项目保存失败");
		}
		return jsonObject;
	}

	/** 服务保存 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject insertMerchantServiceType(Long merchantId,
			String serviceTypeIds, String appType) {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("appType", appType);
		try {
			this.iMerchantServiceTypeDao.deleteMerchantServiceType(paramMap);
			List<Map<String, Object>> merchantServiceTypeList = new ArrayList<Map<String, Object>>();
			for (String serviceTypeId : serviceTypeIds.split(",")) {
				Map<String, Object> merchantServiceTypeMap = new HashMap<String, Object>();
				merchantServiceTypeMap.put("merchantId", merchantId);
				merchantServiceTypeMap.put("serviceTypeId",
						Long.valueOf(serviceTypeId));
				merchantServiceTypeMap.put("appType", appType);
				merchantServiceTypeList.add(merchantServiceTypeMap);
			}
			this.iMerchantServiceTypeDao
					.insertMerchantServiceType(merchantServiceTypeList);

			jsonObject = new ResultJSONObject("000", "服务项目保存成功");
			// 清除商户缓存信息
//			commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//					StringUtil.null2Str(merchantId));
			//更新商品缓存
			String merchantServiceTypeNames=iMerchantServiceTypeDao.getMerchantServiceTypeNames(paramMap);
			Map<String, Object> newValue = new HashMap<String, Object>();
			newValue.put("merchantServiceTypeNames", merchantServiceTypeNames);
			this.updateMerchantCache(merchantId, newValue);

			// 清除服务名称列表缓存-----Revoke 2016.4.27
			commonCacheService.deleteObject(
					CacheConstants.MERCHANT_SERVICES_LIST,
					StringUtil.null2Str(merchantId));

			
			/**
			 * 加入更新商户服务及位置索引 2016.6.20 Revoke
			 * 
			 */
			Object cachedMerchant = commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,
					StringUtil.null2Str(merchantId));
			String name = "";
			if (cachedMerchant != null) {
				Map<String,Object> merchantInfo = (Map<String, Object>) cachedMerchant;
				name = StringUtil.null2Str(((Map<String, Object>) cachedMerchant).get("name"));
				Object latitude =  merchantInfo.get("latitude");
				Object longitude = merchantInfo.get("longitude");
				if (name!=null && name!="" && latitude!=null && longitude!=null){
					CalloutServices.executor(new MerchantElasticIndexRunner(
							elasticSearchService, merchantId.toString(), "",
							(Double)latitude, (Double)longitude));
				}
			} else {
				Map<String,Object> merchantInfo = iMerchantInfoDao.selectMerchantInfo(paramMap);
				if (merchantInfo!=null && merchantInfo.size()>0) {
						Object latitude = merchantInfo.get("latitude");
						Object longitude = merchantInfo.get("longitude");
						if (latitude!=null && longitude!=null){
							CalloutServices.executor(new MerchantElasticIndexRunner(
									elasticSearchService, merchantId.toString(), "",
									(Double)latitude, (Double)longitude));
						}
				}
			}
			
		} catch (Exception ex) {
			logger.error("服务项目保存失败", ex);
			throw new ApplicationException(ex, "merchant_service_save_failure",
					"商户服务保存失败");
		}
		return jsonObject;
	}

	/** 商户已提供（1）未提供（0）的服务项目的编辑 */
	@Override
	public JSONObject selectMerchantServiceForChoose(Long merchantId,
			String merchantType, String appType) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);

		String catalogId = this.iMerchantInfoDao.selectCatalogId(paramMap);

		paramMap.put("catalogId", catalogId);
		List<Map<String, Object>> serviceTypeList = null;
		// 1 行业商户 2个性商户
		if ("1".equals(merchantType) || StringUtil.isEmpty(merchantType)) {
			// if (appType.startsWith("yxt") || appType.startsWith("ams")) {
			Map<String, Object> info = getCatalogTreeAndService(catalogId);
			List<String> idList = this.iMerchantServiceTypeDao
					.selectServiceTypeId(paramMap);
			serviceTypeList = getChildsServiceType(info, idList);
			// } else {
			// serviceTypeList =
			// this.iMerchantServiceTypeDao.selectCategoryMerchantService(paramMap);
			// }
		} else if ("2".equals(merchantType)) {
			Map<String, Object> info = getCatalogTreeAndService(catalogId);
			List<String> idList = this.iMerchantServiceTypeDao
					.selectServiceTypeId(paramMap);
			serviceTypeList = getChildsServiceType(info, idList);
			JSONObject personApplyService = personApplyServiceQuery(merchantId);
			jsonObject.put("personApplyServiceAuditPass",
					personApplyService.get("personApplyServiceAuditPass"));
			jsonObject.put("personApplyServiceAuditing",
					personApplyService.get("personApplyServiceAuditing"));
			jsonObject.put("personApplyServiceAuditNotPassed",
					personApplyService.get("personApplyServiceAuditNotPassed"));
		}
		jsonObject.put("serviceTypeList", serviceTypeList);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "服务加载成功");

		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getChildsServiceTypeForOldVersion(
			Map<String, Object> map, List<String> idList) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		Object children = map.get("children");
		if (children != null) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) children;
			for (Map<String, Object> map_ : list) {
				Map<String, Object> returnMap = new HashMap<String, Object>();
				returnMap.put("id", map_.get("id"));
				returnMap.put("serviceTypeName", map_.get("name"));
				if (idList.contains(String.valueOf(map_.get("id")))) {
					returnMap.put("chose", 1);
				} else {
					returnMap.put("chose", 0);
				}
				List<Map<String, Object>> list_ = getChildsServiceTypeForOldVersion(
						map_, idList);
				if (!list_.isEmpty()) {
					returnMap.put("serviceTypeList", list_);
				}
				returnList.add(returnMap);
			}
		}
		return returnList;
	}

	@SuppressWarnings("unchecked")
	private void edit(List<Map<String, Object>> list) {
		for (Map<String, Object> map : list) {
			boolean flg1 = false;
			List<Map<String, Object>> serviceTypeList = (List<Map<String, Object>>) map
					.get("serviceTypeList");
			if (serviceTypeList == null) {
				return;
			}
			for (Map<String, Object> map_ : serviceTypeList) {
				boolean flg2 = false;
				if (map_.get("serviceTypeList") != null) {
					for (Map<String, Object> map__ : (List<Map<String, Object>>) map_
							.get("serviceTypeList")) {
						if (String.valueOf(map__.get("chose")).equals("1")) {
							flg2 = true;
							break;
						}
					}
				}
				if (flg2) {
					map_.put("chose", 1);
					flg1 = true;
				} else if (String.valueOf(map_.get("chose")).equals("1")) {
					if (map_.get("serviceTypeList") == null
							|| ((List<Map<String, Object>>) map_
									.get("serviceTypeList")).isEmpty()) {
						flg1 = true;
					} else {
						boolean flg = false;
						for (Map<String, Object> map__ : (List<Map<String, Object>>) map_
								.get("serviceTypeList")) {
							if (String.valueOf(map__.get("chose")).equals("1")) {
								map_.put("chose", 1);
								flg1 = true;
								flg = true;
								break;
							}
						}
						if (!flg) {
							map_.put("chose", 0);
						}
					}
				}
			}
			if (flg1) {
				map.put("chose", 1);
			} else if (String.valueOf(map.get("chose")).equals("1")) {
				if (map.get("serviceTypeList") == null
						|| ((List<Map<String, Object>>) map
								.get("serviceTypeList")).isEmpty()) {
					map.put("chose", 1);
				} else {
					boolean flg = false;
					for (Map<String, Object> map_ : (List<Map<String, Object>>) map
							.get("serviceTypeList")) {
						if (String.valueOf(map_.get("chose")).equals("1")) {
							map.put("chose", 1);
							flg = true;
							break;
						}
					}
					if (!flg) {
						map.put("chose", 0);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getChildsServiceType(
			Map<String, Object> map, List<String> idList) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		if(map==null){
			return null;
		}
		Object children = map.get("children");
		if (children != null) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) children;
			for (Map<String, Object> map_ : list) {
				Map<String, Object> returnMap = new HashMap<String, Object>();
				returnMap.put("serviceTypeId", map_.get("id"));
				returnMap.put("serviceTypeName", map_.get("name"));
				returnMap.put("iconPath", map_.get("iconPath"));
				returnMap.put("bigIconPath", map_.get("bigIconPath"));
				returnMap.put("demand", map_.get("demand"));
				if (idList.contains(String.valueOf(map_.get("id")))) {
					returnMap.put("chose", 1);
				} else {
					returnMap.put("chose", 0);
				}
				List<Map<String, Object>> list_ = getChildsServiceType(map_,
						idList);
				if (!list_.isEmpty()) {
					returnMap.put("serviceTypeList", list_);
				}
				BusinessUtil.disposeManyPath(returnMap, "iconPath",
						"bigIconPath");
				returnList.add(returnMap);
			}
		}
		return returnList;
	}

	/** 个人申请的服务的保存 */
	@Override
	public JSONObject personApplyServiceSave(String serviceNames,
			Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);

		int num = this.iMerchantServiceTypeDao.personApplyServiceNum(paramMap);
		if (num >= 16) {
			return new ResultJSONObject("beyond", "你已经申请了16个服务,已达上限");
		}

		// 目前一个商户一次只能提交一个服务的申请，未来可能会有多个，这里的数组长度目前只会是1，serviceNames暂时只有一个服务名，且字符串中不含"，"
		// String[] serviceNameArray = serviceNames.split(",");
		// paramMap.put("serviceNameArray", serviceNameArray);
		paramMap.put("serviceName", serviceNames);
		int count = this.iMerchantServiceTypeDao
				.checkApplyServiceInServiceType(paramMap);
		if (count == 1) {
			return new ResultJSONObject("beyond", "平台已有该服务，无需再添加");
		}
		Map<String, Object> status = this.iMerchantServiceTypeDao
				.selectApplyServiceInfo(paramMap);
		if (status != null) {
			String isDel = String.valueOf(status.get("isDel"));
			String isAudit = String.valueOf(status.get("isAudit"));
			if ("0".equals(isDel)) {
				if ("0".equals(isAudit)) {
					return new ResultJSONObject("repeat",
							"你已申请该服务，正在审核中，请勿重复提交");
				} else if ("2".equals(isAudit)) {
					return new ResultJSONObject("repeat",
							"你已申请过该服务，且未通过审核，请勿重复提交");
				}
			}
		}
		this.iMerchantServiceTypeDao.personApplyServiceSave(paramMap);
		return new ResultJSONObject("000", "个人申请的服务保存成功");
	}

	/** 个人申请的服务的查询 */
	@Override
	public JSONObject personApplyServiceQuery(Long merchantId) {
		// List<Map<String, Object>> personApplyServiceAuditPass = new
		// ArrayList<Map<String,Object>>();
		List<Map<String, Object>> personApplyServiceAuditing = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> personApplyServiceAuditNotPassed = new ArrayList<Map<String, Object>>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);

		List<Map<String, Object>> personApplyInfo = this.iMerchantServiceTypeDao
				.personApplyServiceQuery(paramMap);
		for (Map<String, Object> map : personApplyInfo) {
			String isAudit = String.valueOf(map.get("isAudit"));
			if ("0".equals(isAudit)) {
				personApplyServiceAuditing.add(map);
			} else if ("2".equals(isAudit)) {
				personApplyServiceAuditNotPassed.add(map);
			}
		}
		List<Map<String, Object>> personApplyServiceAuditPass = this.iMerchantServiceTypeDao
				.selectMerchantService(paramMap);
		for (Map<String, Object> map : personApplyServiceAuditPass) {
			map.put("name", map.get("serviceTypeName"));
			map.remove("serviceTypeName");
			map.put("isAudit", 1);
		}
		JSONObject jsonObject = new ResultJSONObject("000", "个人申请的服务查询成功");
		jsonObject.put("personApplyServiceAuditPass",
				personApplyServiceAuditPass);
		jsonObject
				.put("personApplyServiceAuditing", personApplyServiceAuditing);
		jsonObject.put("personApplyServiceAuditNotPassed",
				personApplyServiceAuditNotPassed);
		return jsonObject;
	}

	/** 个人申请的服务的删除 */
	@Override
	public JSONObject personApplyServiceDelete(Long merchantId, Long id,
			int isAudit) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("id", id);
		// if (isAudit == 1) {
		// this.iMerchantServiceTypeDao.deleteByServiceTypeId(paramMap);
		// } else {
		// this.iMerchantServiceTypeDao.personApplyServiceDelete(paramMap);
		// }
		JSONObject jsonObject = null;
		if (isAudit == 0) {
			this.iMerchantServiceTypeDao.personApplyServiceDelete(paramMap);
			jsonObject = new ResultJSONObject("000", "个人申请的服务删除成功");
		} else {
			jsonObject = new ResultJSONObject("audit_error", "该服务暂时无法删除");
		}
		return jsonObject;
	}

	/** 个人服务（技能）搜索 */
	public JSONObject personServiceSearch(String keyword) {
		List<Map<String, Object>> services = commonService
				.getPersonCatalogAndServices(keyword);
		JSONObject jsonObject = new ResultJSONObject("000", "个人服务（技能）搜索成功");
		jsonObject.put("serviceTypeList", transform(services));
		return jsonObject;
	}

	private List<Map<String, Object>> transform(List<Map<String, Object>> list) {
		List<Map<String, Object>> returnList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> map : list) {
			Map<String, Object> returnMap = new HashMap<String, Object>();
			returnMap.put("serviceTypeId", map.get("id"));
			returnMap.put("serviceTypeName", map.get("name"));
			returnMap.put("iconPath", map.get("iconPath"));
			returnMap.put("bigIconPath", map.get("bigIconPath"));
			returnMap.put("demand", map.get("demand"));
			if (map.get("children") != null) {
				returnMap.put("serviceTypeList",
						transform((List<Map<String, Object>>) map
								.get("children")));
			}
			BusinessUtil.disposeManyPath(returnMap, "iconPath", "bigIconPath");
			returnList.add(returnMap);
		}
		return returnList;
	}

	/** 更新联系方式 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject contactSave(String appType, Long merchantId,
			String telephone) {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 联系电话
		paramMap.put("telephone", telephone);
		try {
			this.iMerchantContactDao.updateTelephone(paramMap);
			jsonObject = new ResultJSONObject("000", "联系方式保存成功");
			// 清除商户缓存信息
//			commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//					StringUtil.null2Str(merchantId));
			//更新商品缓存
			Map<String, Object> newValue = new HashMap<String, Object>();
			newValue.put("phone", telephone);
			this.updateMerchantCache(merchantId, newValue);
			
		} catch (Exception ex) {
			logger.error("联系方式保存失败", ex);
			throw new ApplicationException(ex, "contact_save_failure",
					"联系方式保存失败");
		}
		return jsonObject;
	}

	/** 省市 保存 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject citySave(String appType, Long merchantId,
			String province, String city) {
		JSONObject jsonObject = null;
		if (StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city)) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("merchantId", merchantId);
			// 省份
			paramMap.put("province", province);
			// 城市
			paramMap.put("city", city);
			try {
				this.iMerchantInfoDao.updateProvinceCity(paramMap);
				jsonObject = new ResultJSONObject("000", "商户的省份城市保存成功");
			} catch (Exception ex) {
				logger.error("商户的省份城市保存失败", ex);
				throw new ApplicationException(ex,
						"merchant_province_city_save_failure", "商户的省份城市保存失败");
			}
		} else {
			jsonObject = new ResultJSONObject("000", "商户的省份城市为空，不保存");
		}
		return jsonObject;
	}

	/** 更新店铺地理位置 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateLocation(String appType, Long merchantId,
			String province, String city, Double latitude, Double longitude, String mapType,
			String location, String detailAddress) {
		JSONObject jsonObject = null;

		String[] provinceAndCity = BusinessUtil.handlerProvinceAndCity(
				province, city);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);// 商户主键
		paramMap.put("province", provinceAndCity[0]);// 省
		paramMap.put("city", provinceAndCity[1]);// 市
		paramMap.put("longitude", longitude);// 经度
		paramMap.put("latitude", latitude);// 纬度
        paramMap.put("mapType", StringUtil.isNullStr(mapType) ? 0 : "gaode".equals(mapType) ? 1 : 0); // 地图类型
		paramMap.put("locationAddress", location);// 商户地址
		paramMap.put("detailAddress", detailAddress);// 商户详细地址（对地址的补充）
		try {
			if (StringUtils.isNotEmpty(location) && !location.equals("null")) {
				this.iMerchantInfoDao.updateLocation(paramMap);
			}
			jsonObject = new ResultJSONObject("000", "地理位置保存成功");

			/**
			 * 加入更新商户位置索引 2016.4.25 Revoke
			 * 
			 */
			Object cachedMerchant = commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,
					StringUtil.null2Str(merchantId));
			String name = "";
			if (cachedMerchant != null) {
				name = StringUtil
						.null2Str(((Map<String, Object>) cachedMerchant)
								.get("name"));
				if (name != "") {
					CalloutServices.executor(new MerchantElasticIndexRunner(
							elasticSearchService, merchantId.toString(), name,
							latitude, longitude));
				}
			} else {
				CalloutServices.executor(new MerchantElasticIndexRunner(
						elasticSearchService, merchantId.toString(), "",
						latitude, longitude));
			}

			// end of 加入更新商户位置索引

			// 清除商户缓存信息
//			commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//					StringUtil.null2Str(merchantId));
			//更新商品缓存
			Map<String, Object> newValue = new HashMap<String, Object>();
			newValue.put("province", provinceAndCity[0]);// 省
			newValue.put("city", provinceAndCity[1]);// 市
			newValue.put("longitude", longitude);// 经度
			newValue.put("latitude", latitude);// 纬度
			newValue.put("mapType", StringUtil.isNullStr(mapType) ? 0 : "gaode".equals(mapType) ? 1 : 0); // 地图类型
			newValue.put("locationAddress", location);// 商户地址
			newValue.put("detailAddress", detailAddress);// 商户详细地址（对地址的补充）
			this.updateMerchantCache(merchantId, newValue);

		} catch (Exception ex) {
			logger.error("地理位置保存失败", ex);
			throw new ApplicationException(ex, "location_save_failure",
					"地理位置保存失败");
		}
		return jsonObject;
	}

	/** 查询当前代金券信息 */
	@Override
	public JSONObject selectCurrentVouchersInfo(String appType,
			Long merchantId, int pageNo) throws Exception {
		JSONObject jsonObject = null;
		// 获取代金券缓存信息
		jsonObject = (JSONObject) commonCacheService.getObject(
				CacheConstants.MERCHANT_VOUCHERSINFO,
				StringUtil.null2Str(merchantId), String.valueOf(pageNo));
		if (jsonObject != null) {
			return jsonObject;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 应用程序类型
		paramMap.put("appType", appType);
		// 查询起始记录行号
		paramMap.put("rows", pageNo * Constant.PAGESIZE);
		// 每页显示的记录数
		paramMap.put("pageSize", Constant.PAGESIZE);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		// 截止时间
		paramMap.put("cutoffTime", calendar.getTime());

		// paramMap.put("catalogIds", getCatalogIdByAppType(appType));

		int count = this.iMerchantVouchersPermissionsDao
				.selectCurrentVouchersInfoCount(paramMap);
		jsonObject = new ResultJSONObject("000", "当前代金券信息加载成功");
		if (count == 0) {
			jsonObject.put("totalPage", 0);
			jsonObject.put("currentVouchersInfoList",
					new ArrayList<HashMap<String, String>>());
		} else {
			jsonObject.put("totalPage", BusinessUtil.totalPageCalc(count));
			List<Map<String, Object>> currentVouchersInfoList = this.iMerchantVouchersPermissionsDao
					.selectCurrentVouchersInfo(paramMap);
			for (Map<String, Object> map : currentVouchersInfoList) {
				BusinessUtil.disposePath(map, "couponsTypePicPath");
			}
			jsonObject.put("currentVouchersInfoList", currentVouchersInfoList);
			// 添加代金券缓存信息
			commonCacheService.setObject(jsonObject,
					CacheConstants.VOUCHERS_TIMEOUT,
					CacheConstants.MERCHANT_VOUCHERSINFO,
					StringUtil.null2Str(merchantId), String.valueOf(pageNo));
		}

		return jsonObject;
	}

	/** 查询历史代金券信息 */
	@Override
	public JSONObject selectHistoryVouchersInfo(String appType,
			Long merchantId, int pageNo) throws Exception {
		JSONObject jsonObject = null;
		// 获取历史代金券缓存信息
		jsonObject = (JSONObject) commonCacheService.getObject(
				CacheConstants.MERCHANT_HISTORY_VOUCHERSINFO,
				StringUtil.null2Str(merchantId), String.valueOf(pageNo));
		if (jsonObject != null) {
			return jsonObject;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 应用程序类型
		paramMap.put("appType", appType);
		// 查询起始记录行号
		paramMap.put("rows", pageNo * Constant.PAGESIZE);
		// 每页显示的记录数
		paramMap.put("pageSize", Constant.PAGESIZE);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		// 截止时间
		paramMap.put("cutoffTime", calendar.getTime());

		// paramMap.put("catalogIds", getCatalogIdByAppType(appType));

		int count = this.iMerchantVouchersPermissionsDao
				.selectHistoryVouchersInfoCount(paramMap);
		jsonObject = new ResultJSONObject("000", "历史代金券信息加载成功");
		if (count == 0) {
			jsonObject.put("totalPage", 0);
			jsonObject.put("historyVouchersInfoList",
					new ArrayList<HashMap<String, String>>());
		} else {
			jsonObject.put("totalPage", BusinessUtil.totalPageCalc(count));
			List<Map<String, Object>> historyVouchersInfoLis = this.iMerchantVouchersPermissionsDao
					.selectHistoryVouchersInfo(paramMap);
			for (Map<String, Object> map : historyVouchersInfoLis) {
				BusinessUtil.disposePath(map, "couponsTypePicPath");
			}
			jsonObject.put("historyVouchersInfoList", historyVouchersInfoLis);
			// 添加代金券缓存信息
			commonCacheService.setObject(jsonObject,
					CacheConstants.VOUCHERS_TIMEOUT,
					CacheConstants.MERCHANT_HISTORY_VOUCHERSINFO,
					StringUtil.null2Str(merchantId), String.valueOf(pageNo));
		}

		return jsonObject;
	}

	/** 删除历史代金券信息 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject deleteHistoryVouchers(String appType, String userPhone,
			Long id, Long merchantId) {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户代金券权限表ID或者用户关联代金券表ID
		paramMap.put("id", id);
		try {
			if (StringUtils.isEmpty(userPhone)) {
				// 代金券过期
				this.iMerchantVouchersPermissionsDao
						.deletePastVouchers(paramMap);
			} else {
				// 代金券已使用
				this.iMerchantVouchersPermissionsDao
						.deleteUsedVouchers(paramMap);
			}
			// 删除历史代金券缓存
			commonCacheService.delObjectContainsKey(
					CacheConstants.MERCHANT_HISTORY_VOUCHERSINFO + "_"
							+ StringUtil.null2Str(merchantId), true);
			jsonObject = new ResultJSONObject("000", "删除历史代金券成功");
		} catch (Exception ex) {
			logger.error("删除历史代金券失败", ex);
			throw new ApplicationException(ex,
					"delete_history_vouchers_failure", "删除历史代金券失败");
		}
		return jsonObject;
	}

	/** 代金券信息加载 */
	@Override
	public JSONObject selectVouchersType(String appType, Long merchantId)
			throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("appType", appType);
		try {
			paramMap.put("catalogIds", getCatalogIdByAppType(appType));
			List<Map<String, Object>> vouchersTypeList = this.iVouchersInfoDao
					.selectVouchersType(paramMap);

			if (!vouchersTypeList.isEmpty()) {
				jsonObject = new ResultJSONObject("000", "代金券类型加载成功");
			} else {
				jsonObject = new ResultJSONObject("vouchers_type_empty",
						"代金券类型为空");
			}
			jsonObject.put("vouchersTypeList", vouchersTypeList);
		} catch (Exception ex) {
			jsonObject = new ResultJSONObject("vouchers_type_load_failure",
					"代金券类型加载失败");
			logger.error("代金券类型加载失败", ex);
		}
		return jsonObject;
	}

	/**
	 * 获取代金券剩余数量
	 */
	@Override
	public JSONObject getSurplusVouchersNumber(Long vouchersId)
			throws Exception {
		JSONObject jsonObject = null;

		// 判断商户添加代金券的数量是否大于剩余代金券总数
		Map<String, Object> vouchersCountMap = this.iVouchersInfoDao
				.getSurplusVouchersNumber(vouchersId);
		if (vouchersCountMap != null) {
			long totalCount = Long
					.parseLong(vouchersCountMap.get("totalCount") == null ? "0"
							: vouchersCountMap.get("totalCount").toString());
			long usedCount = Long
					.parseLong(vouchersCountMap.get("usedCount") == null ? "0"
							: vouchersCountMap.get("usedCount").toString());
			jsonObject = new ResultJSONObject("000", "信息加载成功");
			jsonObject.put("surplusNumber", totalCount - usedCount);
		}
		return jsonObject;
	}

	/** 保存商户代金券信息 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject insertMerchantVouchersPermissions(String appType,
			Long merchantId, Long vouchersId, String count, String cutoffTime) {
		try {
			Integer countTemp = Integer.valueOf(count);
			if (countTemp == null || countTemp < 1 || countTemp > 999) {
				return new ResultJSONObject("vouchers_count_error", "代金券数量填写错误");
			}
		} catch (Exception ex) {
			logger.error("代金券数量填写错误", ex);
			throw new ApplicationException(ex, "vouchers_count_error",
					"代金券数量填写错误");
		}
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 代金券ID
		paramMap.put("vouchersId", vouchersId);
		// 数量
		paramMap.put("count", count);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			// 截止时间（设置的日期的当天23:59:59）
			Date mvCutoffTime = DateUtils.addSeconds(
					DateUtils.addDays(sdf.parse(cutoffTime), 1), -1);

			String vCutoffTime = this.iVouchersInfoDao
					.selectVouchersCutoffTime(paramMap);
			if (sdf.parse(cutoffTime).compareTo(sdf.parse(vCutoffTime)) > 0
					|| new Date().compareTo(sdf.parse(vCutoffTime)) > 0) {
				return new ResultJSONObject("vouchers_time_beyond",
						"代金券时间超过截止日期");
			}
			paramMap.put("cutoffTime", mvCutoffTime);
			this.iMerchantVouchersPermissionsDao
					.insertMerchantVouchersPermissions(paramMap);
			jsonObject = new ResultJSONObject("000", "代金券添加成功");
			// 删除代金券缓存
			commonCacheService.delObjectContainsKey(
					CacheConstants.MERCHANT_VOUCHERSINFO + "_"
							+ StringUtil.null2Str(merchantId), true);
		} catch (Exception ex) {
			logger.error("代金券添加失败", ex);
			throw new ApplicationException(ex, "vouchers_add_failure",
					"代金券添加失败");
		}
		return jsonObject;
	}

	/** 申请认证的信息查询 */
	@Override
	public JSONObject selectApplyAuthInfo(String appType, Long merchantId)
			throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		Map<String, Object> authMap = this.iMerchantAuthDao
				.selectMerchantAuth(paramMap);
		BusinessUtil.disposePath(authMap, "path");
		// 查询认证信息
		List<Map<String, Object>> ListAuthInfo = this.iMerchantAuthDao
				.selectMerchantAuthList(paramMap);
		Map<String, Object> enterpriseAuth = null;
		Map<String, Object> personAuth = null;

		for (Map<String, Object> authInfo : ListAuthInfo) {
			BusinessUtil.disposePath(authInfo, "path");
			int authType = Integer
					.parseInt(authInfo.get("authType") == null ? "0" : authInfo
							.get("authType").toString());
			if (authType == 1) {// 1-企业认证
				enterpriseAuth = authInfo;
			}
			if (authType == 2) {// 2-个人认证
				personAuth = authInfo;
			}
		}

		jsonObject = new ResultJSONObject("000", "认证信息加载成功");
		jsonObject.put("authInfo", authMap);
		jsonObject.put("enterpriseAuth", enterpriseAuth);
		jsonObject.put("personAuth", personAuth);

		return jsonObject;
	}

	/** 提交认证申请 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject insertMerchantAuth(String appType, Long merchantId,
			int authType, String resultPath) throws Exception {
		JSONObject jsonObject = null;
		if (StringUtils.isNotEmpty(resultPath)) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// 商户ID
			paramMap.put("merchantId", merchantId);
			// 查询商户待审核数量
			Integer pendingAuthCount = this.iMerchantAuthDao
					.selectPendingAuthCount(paramMap);
			if (pendingAuthCount.intValue() > 0) {
				return new ResultJSONObject("001", "认证申请已提交，请勿重复提交");
			}
			// 查询商户的认证类型待审核和已审核的数量
			// 认证类型
			paramMap.put("authType", authType);
			Integer count = this.iMerchantAuthDao.selectAuthCount(paramMap);
			if (count.intValue() > 0) {
				return new ResultJSONObject("001", "认证申请已提交，请勿重复提交");
			}
			// 认证状态
			paramMap.put("authStatus", 2);
			// 认证的图片路径
			paramMap.put("path", resultPath);
			this.iMerchantAuthDao.insertMerchantAuth(paramMap);

			// 更新商户表的认证类型和状态，给后台查询提供方便
			this.iMerchantAuthDao.updateMerchantAuth(paramMap);

			jsonObject = new ResultJSONObject("000", "提交申请认证成功");

			// 清除商户缓存信息
			commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
					StringUtil.null2Str(merchantId));
		}
		return jsonObject;
	}

	/** 取消认证申请 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject cancelAuth(String appType, Long merchantId)
			throws Exception {
		JSONObject jsonObject = null;
		int i = this.iMerchantAuthDao.cancelAuth(merchantId);
		if (i > 0) {
			jsonObject = new ResultJSONObject("000", "取消申请认证成功");
			// 更新
			this.iMerchantAuthDao.cancelAuthUpdateMerchant(merchantId);
			// 清除商户缓存信息
			commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
		}
		return jsonObject;
	}

	/** 查询相册 */
	@Override
	public JSONObject selectAlbum(String appType, Long merchantId)
			throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		List<Map<String, Object>> albumList = this.iMerchantAlbumDao
				.selectAlbum(paramMap);
		for (Map<String, Object> albumMap : albumList) {
			BusinessUtil.disposePath(albumMap, "coverPath");
		}
		jsonObject = new ResultJSONObject("000", "查询相册成功");
		jsonObject.put("albumInfo", albumList);

		return jsonObject;
	}

	/** 新建相册 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject insertAlbum(String appType, Long merchantId,
			String albumName) throws Exception {

		JSONObject jsonObject = null;

		// 格式化$符号
		albumName = StringUtil.formatDollarSign(albumName);

		Map<String, Object> maxAlbumNumMap = commonService
				.getConfigurationInfoByKey("max_album_num");
		Object maxAlbumNumObj = maxAlbumNumMap.get("config_value");
		int maxAlbumNum = Integer.parseInt(maxAlbumNumObj == null ? "0"
				: maxAlbumNumObj.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		if (this.iMerchantAlbumDao.selectAlbumCount(paramMap) >= maxAlbumNum) {
			return new ResultJSONObject("album_num_beyond_max", "相册数量已达最大值");
		}
		// 相册名
		paramMap.put("albumName", albumName);
		this.iMerchantAlbumDao.insertAlbum(paramMap);

		// 将新建相册的信息返回给前端
		Map<String, Object> albumMap = new HashMap<String, Object>();
		albumMap.put("albumId", paramMap.get("albumId"));
		albumMap.put("albumName", albumName);
		jsonObject = new ResultJSONObject("000", "新建相册成功");
		jsonObject.put("albumInfo", albumMap);

		return jsonObject;
	}

	/** 重命名相册 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateAlbum(String appType, Long albumId, String albumName)
			throws Exception {
		// 格式化$符号
		albumName = StringUtil.formatDollarSign(albumName);

		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 相册ID
		paramMap.put("albumId", albumId);
		// 新相册名
		paramMap.put("albumName", albumName);

		this.iMerchantAlbumDao.updateAlbum(paramMap);
		jsonObject = new ResultJSONObject("000", "重命名相册成功");

		return jsonObject;
	}

	/** 删除相册 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject deleteAlbum(String appType, Long merchantId, Long albumId)
			throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 相册ID
		paramMap.put("albumId", albumId);

		// 删除相册
		this.iMerchantAlbumDao.updateAlbumStatus(paramMap);
		// 删除相册中的相片
		this.iMerchantPhotoDao.updatePhotoStatusByAlbumId(paramMap);
		jsonObject = new ResultJSONObject("000", "删除相册成功");

		// 强制更新商店概要缓存 -----Revoke 2016.5.3
		commonCacheService.deleteObject(CacheConstants.MERCHANT_OUTLINE,
				merchantId.toString());

		Map<String, Object> newValue = new HashMap<String, Object>();
		//相片数量
		paramMap.put("merchantId", merchantId);
		int photosCount=this.iMerchantPhotoDao.selectPhotoTotal(paramMap);
		newValue.put("photosCount", photosCount);
		//前四相片
		paramMap.put("num", 4);
		Map<String, Object> topPhotosInfo = this.iMerchantPhotoDao.selectLastPhotos(paramMap);
		BusinessUtil.disposeManyPath(topPhotosInfo, "path");
		newValue.put("topPhotosInfo", topPhotosInfo.get("path"));
		this.updateMerchantCache(merchantId, newValue);
		
		return jsonObject;
	}

	/** 查询相片 */
	@Override
	public JSONObject selectPhoto(String appType, Long albumId)
			throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("albumId", albumId);

		List<Map<String, Object>> photoList = this.iMerchantPhotoDao
				.selectPhotoByAlbumId(paramMap);
		for (Map<String, Object> photoMap : photoList) {
			BusinessUtil.disposePath(photoMap, "path");
		}
		jsonObject = new ResultJSONObject("000", "查询相片成功");
		jsonObject.put("photoInfo", photoList);

		return jsonObject;
	}

	/** 新建相片的校验 */
	@Override
	public JSONObject insertPhotoVerify(String appType, Long merchantId,
			Long albumId) {
		JSONObject jsonObject = null;
		Map<String, Object> maxPhotoNumMap = commonService
				.getConfigurationInfoByKey("max_photo_num");
		Object maxPhotoNumObj = maxPhotoNumMap.get("config_value");
		int maxPhotoNum = Integer.parseInt(maxPhotoNumObj == null ? "0"
				: maxPhotoNumObj.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		if (this.iMerchantPhotoDao.selectPhotoTotal(paramMap) >= maxPhotoNum) {
			jsonObject = new ResultJSONObject("photo_num_beyond_max",
					"相片数量已达最大值");
		}
		return jsonObject;
	}

	/** 新建相片 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject insertPhoto(String appType, Long merchantId,
			Long albumId, String resultPath) throws Exception {
		JSONObject jsonObject = null;
		// 新上传的相片返回给商户端显示
		Map<String, Object> photoMap = new HashMap<String, Object>();
		// String filePath = null;

		// 存储到数据库的相对路径
		if (StringUtils.isNotEmpty(resultPath)) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("albumId", albumId);
			paramMap.put("path", resultPath);

			this.iMerchantPhotoDao.insertPhoto(paramMap);
			photoMap.put("photoId", paramMap.get("photoId"));
			photoMap.put("path", resultPath);
			BusinessUtil.disposePath(photoMap, "path");

			jsonObject = new ResultJSONObject("000", "新建相片成功");
			jsonObject.put("photoInfo", photoMap);

			// 强制更新商店概要缓存 -----Revoke 2016.4.26
			commonCacheService.deleteObject(CacheConstants.MERCHANT_OUTLINE,
					merchantId.toString());
		}

		return jsonObject;
	}

	/** 新建多个相片的校验 */
	@Override
	public JSONObject insertPhotosVerify(String appType, Long merchantId,
			Long albumId, int newPhotoNum) {
		JSONObject jsonObject = null;
		Map<String, Object> maxPhotoNumMap = commonService
				.getConfigurationInfoByKey("max_photo_num");
		Object maxPhotoNumObj = maxPhotoNumMap.get("config_value");
		int maxPhotoNum = Integer.parseInt(maxPhotoNumObj == null ? "0"
				: maxPhotoNumObj.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		int photoTotal = this.iMerchantPhotoDao.selectPhotoTotal(paramMap);

		if (photoTotal >= maxPhotoNum) {
			jsonObject = new ResultJSONObject("photo_num_beyond_max",
					"上传相片数已达上限");
		}
		return jsonObject;
	}

	/** 新建多个相片 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject insertPhotos(String appType, Long merchantId,
			Long albumId, List<String> photoPaths) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = null;

		// 存储到数据库的相对路径
		if (!photoPaths.isEmpty()) {
			List<Map<String, Object>> photoPathList = new ArrayList<Map<String, Object>>();
			for (String photoPath : photoPaths) {
				paramMap = new HashMap<String, Object>();
				paramMap.put("albumId", albumId);
				paramMap.put("path", photoPath);
				photoPathList.add(paramMap);
			}
			int i=this.iMerchantPhotoDao.insertPhotos(photoPathList);
			if (i>0){
				jsonObject = new ResultJSONObject("000", "新建相片成功");
			}else{
				jsonObject = new ResultJSONObject("insertPhotos_fail", "新建相片失败");
			}
		} else {
			jsonObject = new ResultJSONObject("000", "新建相片成功");
		}

		// 强制更新商店概要缓存 -----Revoke 2016.2.26
		commonCacheService.deleteObject(CacheConstants.MERCHANT_OUTLINE,
				merchantId.toString());

		Map<String, Object> newValue = new HashMap<String, Object>();
		//相片数量
		paramMap.put("merchantId", merchantId);
		int photosCount=this.iMerchantPhotoDao.selectPhotoTotal(paramMap);
		newValue.put("photosCount", photosCount);
		//前四相片
		paramMap.put("num", 4);
		Map<String, Object> topPhotosInfo = this.iMerchantPhotoDao.selectLastPhotos(paramMap);
		BusinessUtil.disposeManyPath(topPhotosInfo, "path");
		newValue.put("topPhotosInfo", topPhotosInfo.get("path"));
		this.updateMerchantCache(merchantId, newValue);

		return jsonObject;
	}

	/** 删除相片 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject deletePhoto(String appType, Long merchantId,
			String photoIds) throws Exception {
		JSONObject jsonObject = null;

		String[] photoIdStringArray = photoIds.split(",");
		List<Long> photoIdList = new ArrayList<Long>();
		for (int i = 0; i < photoIdStringArray.length; i++) {
			photoIdList.add(Long.valueOf(photoIdStringArray[i]));
		}
		// 删除相片的路径记录
		this.iMerchantPhotoDao.updatePhotoStatusById(photoIdList);
		jsonObject = new ResultJSONObject("000", "删除相片成功");

		// 强制更新商店概要缓存 -----Revoke 2016.2.26
		commonCacheService.deleteObject(CacheConstants.MERCHANT_OUTLINE,
				merchantId.toString());

		Map<String, Object> newValue = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		//相片数量
		int photosCount=this.iMerchantPhotoDao.selectPhotoTotal(paramMap);
		newValue.put("photosCount", photosCount);
		//前四相片
		paramMap.put("num", 4);
		Map<String, Object> topPhotosInfo = this.iMerchantPhotoDao.selectLastPhotos(paramMap);
		BusinessUtil.disposeManyPath(topPhotosInfo, "path");
		newValue.put("topPhotosInfo", topPhotosInfo.get("path"));
		this.updateMerchantCache(merchantId, newValue);

		return jsonObject;
	}

	/** 商户销户 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject closeMerchant(String appType, Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		JSONObject jsonObject = null;
		try {
			// 关闭商户
			this.iMerchantInfoDao.deleteMerchant(paramMap);
			// 删除商户的所有员工
			this.iMerchantEmployeesDao.deleteAllEmployee(paramMap);
			jsonObject = new ResultJSONObject("000", "商户销户成功");
		} catch (Exception ex) {
			logger.error("商户销户失败", ex);
			throw new ApplicationException(ex, "merchant_close_failure",
					"商户销户失败");
		}
		return jsonObject;
	}

	/** 员工列表显示 */
	@Override
	public JSONObject employeesInfo(String appType, Long merchantId, int pageNo)
			throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("appType", appType);
		// 查询起始记录行号
		paramMap.put("rows", pageNo * Constant.PAGESIZE);
		// 每页显示的记录数
		paramMap.put("pageSize", Constant.PAGESIZE);

		JSONObject jsonObject = null;

		int count = this.iMerchantEmployeesDao
				.selectMerchantEmployeesCount(paramMap);
		jsonObject = new ResultJSONObject("000", "员工列表显示成功");
		if (count == 0) {
			jsonObject.put("totalPage", 0);
			jsonObject.put("employeesInfo",
					new ArrayList<HashMap<String, Object>>());
		} else {
			String catalogIds = "0,";
			// 根据merchantId查询catalogId
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("merchantId", merchantId);
			String catalogId = iMerchantInfoDao.selectCatalogId(param);
			// 根据catalogId查询serviceTypeId
			List<Map<String, Object>> serviceTypeList = commonService
					.getServiceTypesByCatalogId(StringUtil
							.nullToInteger(catalogId));
			for (Map<String, Object> map : serviceTypeList) {
				int id = StringUtil.nullToInteger(map.get("id"));
				catalogIds += id + ",";
			}
			if (!catalogIds.equals("")) {
				catalogIds = catalogIds.substring(0, catalogIds.length() - 1);
			}
			paramMap.put("catalogIds", catalogIds);
			jsonObject.put("totalPage", BusinessUtil.totalPageCalc(count));
			jsonObject.put("employeesInfo", this.iMerchantEmployeesDao
					.selectMerchantEmployeesInfo(paramMap));
		}
		// 商户最大员工数
		jsonObject.put("maxEmployeeNum",
				this.iMerchantInfoDao.selectMaxEmployeeNum(paramMap));

		//查询商户最大员工申请状态-1-可申请，1-确认中，不可申请
		int employeesNumApplyStatus = iMerchantEmployeesNumApplyDao.selectEmployeesNumApplyStatus(paramMap);
		jsonObject.put("employeesNumApplyStatus", employeesNumApplyStatus);
		
		// 当前商户已有的员工数
		jsonObject.put("merchantEmployeesNum", count);

		return jsonObject;
	}

	/** 添加员工 获取验证码 */
	@Override
	public JSONObject getVerificationCodeForAddEmployee(String appType,
			Long merchantId, String name, String phone, Integer type)
			throws Exception {
		// 格式化$符号
		name = StringUtil.formatDollarSign(name);

		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("phone", phone);
		paramMap.put("verificationStatus", 1);
		paramMap.put("appType", appType);

		int merchantEmployeesNum = this.iMerchantEmployeesDao
				.selectMerchantEmployeesCount(paramMap);

		Integer temp = this.iMerchantInfoDao.selectMaxEmployeeNum(paramMap);
		if (temp == null) {
			System.out.println("最大员工数: " + temp);
			System.out.println("查询最大员工数异常");
			System.out.println("merchantId: " + merchantId);
			System.out.println("phone: " + phone);
		}
		int maxEmployeeNum = temp;
		if (merchantEmployeesNum >= maxEmployeeNum) {
			return new ResultJSONObject("employee_num_beyond_max",
					"员工数已达最大值,设置更多员工,请提申请");
		}
		Integer employeesType = this.iMerchantEmployeesDao
				.checkEmployeesPhoneInCurrent(paramMap);
		if (employeesType != null && employeesType == 1) {
			return new ResultJSONObject("employee_repeated_add", "该手机号已注册为商户");
		} else if (employeesType != null && employeesType == 2) {
			return new ResultJSONObject("employee_repeated_add",
					"该员工已添加,请勿重复添加");
		}
		int count = this.iMerchantEmployeesDao
				.checkEmployeesPhoneInOther(paramMap);
		if (count == 1) {
			return new ResultJSONObject("employee_belong_to_other_merchant",
					"该手机号属于其他商户或其员工");
		}
		// 验证码
		String verificationCode = BusinessUtil.createVerificationCode(phone);
		paramMap.put("verificationCode", verificationCode);
		// 员工姓名
		paramMap.put("name", name);
		// 缓存添加的员工的预保存信息
		commonCacheService.setObject(paramMap,
				CacheConstants.MERCHANT_ADD_EMPLOYEE_TIMEOUT,
				CacheConstants.MERCHANT_ADD_EMPLOYEE, phone, appType);
		jsonObject = BusinessUtil.sendVerificationCode(phone, verificationCode,
				type);
		return jsonObject;
	}

	/** 查询剩余可添加员工（顾问号）数 */
	@Override
	public JSONObject surplusEmployeesNum(String appType, Long merchantId)
			throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("appType", appType);

		JSONObject jsonObject = null;

		// 商户最大员工数
		int maxEmployeeNum = this.iMerchantInfoDao
				.selectMaxEmployeeNum(paramMap);
		// 当前商户已有的员工数
		int merchantEmployeesNum = this.iMerchantEmployeesDao
				.selectMerchantEmployeesCount(paramMap);
		jsonObject = new ResultJSONObject("000", "可添加员工数查询成功");
		// 剩余可添加员工（顾问号）数
		jsonObject.put("surplusEmployeesNum", maxEmployeeNum
				- merchantEmployeesNum);

		return jsonObject;
	}

	/** 添加员工 确定 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addEmployeeConfirm(String appType, Long merchantId,
			String employeePhone, String verificationCode) throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> merchantAddEmployeeCached = (Map<String, Object>) commonCacheService
				.getObject(CacheConstants.MERCHANT_ADD_EMPLOYEE, employeePhone,
						appType);
		if (merchantAddEmployeeCached == null
				|| merchantAddEmployeeCached.isEmpty()) {
			// 缓存过期
			return new ResultJSONObject("verificationCode_past", "验证码已经失效");
		}
		if (!String.valueOf(merchantAddEmployeeCached.get("verificationCode"))
				.equals(verificationCode)) {
			return new ResultJSONObject("verificationCode_error", "请填写正确的验证码");
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("name",
				String.valueOf(merchantAddEmployeeCached.get("name")));
		paramMap.put("phone", employeePhone);
		// 员工类型 2：普通员工
		paramMap.put("employeesType", 2);
		paramMap.put("verificationStatus", 1);
		// 应用程序类型
		paramMap.put("appType", appType);
		paramMap.put("employeeKey", DynamicKeyGenerator.generateDynamicKey());

		int merchantEmployeesNum = this.iMerchantEmployeesDao
				.selectMerchantEmployeesCount(paramMap);
		int maxEmployeeNum = this.iMerchantInfoDao
				.selectMaxEmployeeNum(paramMap);
		if (merchantEmployeesNum >= maxEmployeeNum) {
			return new ResultJSONObject("employee_num_beyond_max",
					"员工数已达最大值,设置更多员工,请提申请");
		}
		Integer employeesType = this.iMerchantEmployeesDao
				.checkEmployeesPhoneInCurrent(paramMap);
		if (employeesType != null && employeesType == 1) {
			return new ResultJSONObject("employee_repeated_add", "该手机号已注册为商户");
		} else if (employeesType != null && employeesType == 2) {
			return new ResultJSONObject("employee_repeated_add",
					"该员工已添加,请勿重复添加");
		}

		int count = this.iMerchantEmployeesDao
				.checkEmployeesPhoneInOther(paramMap);
		if (count == 1) {
			return new ResultJSONObject("employee_belong_to_other_merchant",
					"该手机号属于其他商户或其员工");
		}

		/* version：1.1.0，date：2016-3-9，author：wangrui */
		// 判断此手机号是否已注册，如果已注册则直接新增到员工表，如果没有注册则新增到用户表和员工表
		Map<String, Object> employee_userInfo = this.iMerchantEmployeesDao
				.getUserInfoByPhone(employeePhone);
		if (employee_userInfo == null) {// 新增到用户表和员工表
			Map<String, Object> userInfo = new HashMap<String, Object>();
			userInfo.put("userId", "" + IdGenerator.generateID(18));
			userInfo.put("userKey", DynamicKeyGenerator.generateDynamicKey());
			userInfo.put("phone", employeePhone);
			userInfo.put("verificationCode", verificationCode);
			userInfo.put("ip", Constant.EMPTY);
			userInfo.put("province", Constant.EMPTY);
			userInfo.put("city", Constant.EMPTY);
			this.iMerchantEmployeesDao.insertUserInfo(userInfo);

			paramMap.put("userId", userInfo.get("userId"));
			this.iMerchantEmployeesDao.insertPhone(paramMap);

		} else {
			// 新增到员工表
			paramMap.put("userId", employee_userInfo.get("userId"));
			this.iMerchantEmployeesDao.insertPhone(paramMap);
		}
		/* end */
		jsonObject = new ResultJSONObject("000", "添加员工成功");
		// 清除商户员工号预验证缓存信息
		commonCacheService.deleteObject(CacheConstants.MERCHANT_PRE_VERIFY,
				employeePhone, appType);
		// 清除商户员工号登陆场合缓存信息
		commonCacheService.deleteObject(CacheConstants.MERCHANT_INFO_FOR_LOGIN,
				employeePhone);
		commonCacheService.deleteObject(CacheConstants.MERCHANT_INFO_FOR_LOGIN,
				employeePhone, appType);// 老版本，未来可删除
		// 清除商户添加的员工缓存信息
		commonCacheService.deleteObject(CacheConstants.MERCHANT_ADD_EMPLOYEE,
				employeePhone, appType);

		return jsonObject;
	}

	/** 删除单个员工 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject deleteEmployee(String appType, Long userId,
			Long merchantId, String employeePhone) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("phone", employeePhone);
		if (userId == null) {
			// 根据merchantId查询userId
			userId = iMerchantEmployeesDao.getUserIdByMerchantId(paramMap);
		}
		paramMap.put("userId", userId);

		this.iMerchantEmployeesDao.deleteEmployee(paramMap);
		jsonObject = new ResultJSONObject("000", "删除员工成功");
		// 清除商户员工号预验证缓存信息
		commonCacheService.deleteObject(CacheConstants.MERCHANT_PRE_VERIFY,
				employeePhone, appType);
		// 清除商户员工号登陆场合缓存信息
		commonCacheService.deleteObject(CacheConstants.MERCHANT_INFO_FOR_LOGIN,
				employeePhone);// 新版本
		commonCacheService.deleteObject(CacheConstants.MERCHANT_INFO_FOR_LOGIN,
				employeePhone, appType);// 老版本，未来可删除
		// 清除商户添加的员工缓存信息
		commonCacheService.deleteObject(CacheConstants.MERCHANT_ADD_EMPLOYEE,
				employeePhone, appType);

		paramMap.put("appType", appType);
		// int i = iMerchantPushDao.checkEmployeeOnline(paramMap);
		// 在线信息
		Map<String, Object> clientMap = this.iMerchantEmployeesDao
				.getEmployeeClientMap(paramMap);
		if (clientMap == null) {
			return jsonObject;
		} else {
			if (userId == null) {// 没有传userId则是老版本，则不用推送
				return jsonObject;
			}
			commonCacheService.deleteObject("employeeKey",
					StringUtil.null2Str(clientMap.get("clientId"))
							+ employeePhone);
			// List<Map<String, Object>> onlineMerchantList=new
			// ArrayList<Map<String,Object>>();
			// onlineMerchantList.add(clientMap);
			Map<String, Object> paras = new HashMap<String, Object>();
			paras.put("userId", userId);
			paras.put("pushType", 6);
			pushService.basicPush(paras);
			// Map<String, Object>
			// configMap=pushService.getConfigurationInfoByKey();
			// PushManager.push(configMap,onlineMerchantList, paras,"userId");
		}

		return jsonObject;
	}

	/** 员工老板身份转换 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateBossEmployeeType(String appType, Long merchantId,
			String newBossPhone, String newEmployeePhone, String newEmployeeName)
			throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("phone", newBossPhone);
		paramMap.put("employeeType", 1);
		this.iMerchantEmployeesDao.updateEmployeeType(paramMap);

		paramMap.put("phone", newEmployeePhone);
		paramMap.put("name", newEmployeeName);
		paramMap.put("employeeType", 2);
		this.iMerchantEmployeesDao.updateEmployeeType(paramMap);

		Map<String, Object> paras = new HashMap<String, Object>();
		// 查询userId
		paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("phone", newBossPhone);
		Long userId = iMerchantEmployeesDao.getUserIdByMerchantId(paramMap);
		paras.put("userId", userId);
		paras.put("pushType", 7);
		pushService.basicPush(paras);

		// 清除商户(原)老板号登陆场合缓存信息
		commonCacheService.deleteObject(CacheConstants.MERCHANT_INFO_FOR_LOGIN,
				newEmployeePhone);// 新版本
		commonCacheService.deleteObject(CacheConstants.MERCHANT_INFO_FOR_LOGIN,
				newEmployeePhone, appType);// 老版本，未来可删除
		// 清除商户(原)员工号登陆场合缓存信息
		commonCacheService.deleteObject(CacheConstants.MERCHANT_INFO_FOR_LOGIN,
				newBossPhone);// 新版本
		commonCacheService.deleteObject(CacheConstants.MERCHANT_INFO_FOR_LOGIN,
				newBossPhone, appType);// 老版本，未来可删除
		jsonObject = new ResultJSONObject("000", "员工老板身份转换成功");

		return jsonObject;
	}

	/** 增加员工数申请 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject increaseEmployeeNumApply(int pkgId, String appType, Long merchantId,
			int increaseEmployeeNum, String money, int applyStatus,
			String payNo, String payType, Map<String, Object> paramMap) throws Exception {
		JSONObject jsonObject = null;
        String phone = "";
		if(paramMap == null){
			paramMap = new HashMap<String, Object>();
		}
		paramMap.put("merchantId", merchantId);
		paramMap.put("increaseEmployeeNum", increaseEmployeeNum);
		paramMap.put("appType", appType);
		paramMap.put("applyStatus", applyStatus);
		paramMap.put("money", money);
		paramMap.put("payNo", payNo);
		paramMap.put("payType", payType);
		
		if (!paramMap.containsKey("openTime") || paramMap.get("openTime")==null || paramMap.get("openTime").equals("")){
			if (!payType.equals("3")){
				paramMap.put("openTime", new Date(System.currentTimeMillis()));
			}
		}
		paramMap.put("tradeNo", paramMap.get("tradeNo"));

			Integer checkResult = iMerchantEmployeesNumApplyDao.checkEmployeesNumApplyByInnerTradeNo(paramMap);
		
			if (checkResult==0){
				//this.iMerchantEmployeesNumApplyDao.insertMerchantEmployeesNumApply(paramMap);
				this.iMerchantEmployeesNumApplyDao.addMerchantEmployeesNumApplyWithConfirm(paramMap);
			}else{
				this.iMerchantEmployeesNumApplyDao.confirmBuyEmployeeNumRecord(paramMap);
			}

			List<String> paths = null;
			if(paramMap.containsKey("paths")){
				paths = (List<String>) paramMap.get("paths");
			}
			//保存缴费凭证信息
			myMerchantService.savePayApplyFile(paths, payNo, "employeesNum");
			
			if (applyStatus == 2) {
				iMerchantEmployeesNumApplyDao.updateMerchantEmployeesNum(paramMap);
				valueAddedIncomeService.calculateIncome(merchantId, Double.parseDouble(money), "API", 3);
			}
			commonCacheService.deleteObject(CacheConstants.VALUE_ADD_SERVICE, merchantId.toString());

            if (0 < pkgId) {
                Date date = new Date();
                Map<String, Object> params = new HashMap<>();
                Map<String, Object> bossInfo = merchantPlanDao.getBossIdByMerchant(merchantId);
                phone = null2Str(bossInfo.get("phone"));
                if (null != bossInfo) {
                    params.put("userId", bossInfo.get("user_id"));
                    params.put("phone", bossInfo.get("phone"));
                }
                params.put("id", paramMap.get("id"));
                params.put("effictive", getDateTimeByMinuts(formatDate("yyyy-MM-dd HH:mm:ss", date), 365 * 24 * 60));
                params.put("effictiveTime", date);
                Map<String, Object> merchantMap = selectMerchantBasicInfo(merchantId);
                Map<String, Object> pkgMap = getPkgInfoById(pkgId);
                List<Map<String, Object>> itemRules = iMerchantInfoDao.pkgRuleItem(pkgId);
                if (null != itemRules && 0 < itemRules.size()) {
                    String desc = itemRules.get(0).get("name").toString();
                    for (Map<String, Object> itemRule : itemRules) {
                        if (GRAP_MOENY_RULE.equals(itemRule.get("ruleCode"))) {
                            for (int i = 0; i < increaseEmployeeNum; i++) {
                                dealGrapMoney(merchantId, itemRule);
                            }
                        } else if (VEHICLE_INSURANCE_RULE.equals(itemRule.get("ruleCode"))) {
                            for (int i = 0; i < increaseEmployeeNum; i++) {
                                sendVehicleInsuranceMQ(params, desc, pkgMap, date, null2Str(itemRule.get("ruleCode")), null2Str(itemRule.get("ruleValue")), merchantMap);
                            }
                        }
                    }
                }
            }
        sendMessageAndSMS(paramMap, phone);
		jsonObject = new ResultJSONObject("000", "增加员工数申请已提交");
		return jsonObject;
	}

    // 消息和短信
    private void sendMessageAndSMS(Map<String, Object> map, String phone) throws Exception {
        String msg = "你已成功开通" + map.get("increaseEmployeeNum") + "个员工账号";

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("messageId", 0);
        messageMap.put("messageType", 1);
        messageMap.put("customerType", 1);
        messageMap.put("customerId", map.get("merchantId"));
        messageMap.put("title", "开通员工账号");
        messageMap.put("content", msg);
        messageCenterService.saveCustomerMessageCenter(messageMap);

        SmsUtil.asyncSendMsg(phone, msg);
    }

	/** VIP申请 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject vipApply(String appType, Long merchantId, String money,
			int applyStatus, String payNo, String payType, String tradeNo,Map<String,Object> paramMap) throws Exception {
		JSONObject jsonObject = null;
		if(paramMap == null){
			paramMap = new HashMap<String, Object>();
		}
		paramMap.put("merchantId", merchantId);
		paramMap.put("appType", appType);
		paramMap.put("applyVipLevel", 1);
		paramMap.put("applyStatus", applyStatus);
		paramMap.put("money", money);
		paramMap.put("payNo", payNo);
		paramMap.put("payType", payType);
		paramMap.put("tradeNo", tradeNo);
		if (tradeNo!=null && !tradeNo.equals("")){
			paramMap.put("openTime", new Date(System.currentTimeMillis()));
		}
		List<String> paths = null;
		if(paramMap.containsKey("paths")){
			paths = (List<String>) paramMap.get("paths");
		}
		Integer status = this.iMerchantVipApplyDao
				.selectMerchantVipApplyStatus(paramMap);
		if (status == null || status == 3) {
			Integer checkResult = this.iMerchantVipApplyDao
					.checkMerchantVipApplyByPayNo(paramMap);
			if (checkResult == 0) {
				this.iMerchantVipApplyDao.insertMerchantVipApply(paramMap);
				
				//保存缴费凭证信息
				myMerchantService.savePayApplyFile(paths, payNo, "vip");
				
				
				commonCacheService
						.deleteObject(CacheConstants.VALUE_ADD_SERVICE,
								merchantId.toString());
				if (applyStatus == 2) {
					this.iMerchantVipApplyDao.updateMerchantVipApply(paramMap);
					valueAddedIncomeService.calculateIncome(merchantId,
							Double.parseDouble(money), "API", 1);
				}
				//更新商铺缓存
				commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
						StringUtil.null2Str(merchantId));  
			}
			jsonObject = new ResultJSONObject("000", "VIP申请已成功提交");
		} else if (status == 2) {
			jsonObject = new ResultJSONObject("001", "你已经是VIP商户");
		} else {
			jsonObject = new ResultJSONObject("002", "VIP申请已提交过，请勿重复提交");
		}

		return jsonObject;
	}

	/** VIP申请状态 */
	@Override
	public JSONObject vipApplyStatus(String appType, Long merchantId)
			throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("appType", appType);
		Integer applyStatus = this.iMerchantVipApplyDao
				.selectMerchantVipApplyStatus(paramMap);
		if (applyStatus==1){
			jsonObject = new ResultJSONObject("000", "VIP申请状态开通成功");
			jsonObject.put("applyStatus", 2);
			jsonObject.put("applyStatusName", "已开通");
		}else{
			jsonObject = new ResultJSONObject("000", "没有申请记录");
			jsonObject.put("applyStatus", -1);
			jsonObject.put("applyStatusName", "未开通");
		}
		return jsonObject;
	}

	/** 商户员工密钥编辑 */
	private String keyEdit(String phone, String clientId) throws Exception {
		String employeeKey = getEmployeeKey(phone);
		logger.debug("====================employeeKey===================="
				+ employeeKey);
		String dynamicKey = validateService.getDynamicKey(clientId);
		logger.debug("====================dynamicKey================="
				+ dynamicKey);
		try {
			employeeKey = AESUtil.parseByte2HexStr(AESUtil.encrypt(employeeKey,
					dynamicKey));
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.debug("====================encryptedKey==============="
				+ employeeKey);
		validateService.removeDynamicKey(clientId);
		return employeeKey;
	}

	/** 获取员工密钥 */
	@Override
	public String getEmployeeKey(String phone) {
		String employeeKey = (String) commonCacheService.getObject(
				"employeeKey", phone);
		if (employeeKey == null) {
			employeeKey = getEmployeeKeyFromDB(phone);
			commonCacheService.setObject(employeeKey, "employeeKey", phone);
		}
		return employeeKey;
	}

	/** 从数据库中获取员工密钥 */
	private String getEmployeeKeyFromDB(String phone) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
		// paramMap.put("appType", appType);
		return this.iMerchantEmployeesDao.getEmployeeKey(paramMap);
	}

	/** 更新商户的推送信息 可删除 */
	@SuppressWarnings("unchecked")
	private void updateMerchantPush(String appType, String phone,
			Long merchantId, String pushId, String clientId, String clientType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();

		paramMap.put("appType", appType);
		paramMap.put("phone", phone);
		// clientId 字段是给个推 推送使用的
		paramMap.put("clientId", pushId);
		// 这个是APP自己生成的唯一设备编号
		paramMap.put("deviceId", clientId);
		paramMap.put("clientType", clientType);
		paramMap.put("merchantId", merchantId);
		// 先检查是否需要推送

		Long userId = iMerchantEmployeesDao.getUserIdByMerchantId(paramMap);

		paramMap.put("userId", userId);
		int result = iMerchantPushDao.checkClientByUserId(userId);
		// 如果不等于0，说明自己已经登陆，则不处理，避免频繁操作数据库 2015年9月10日
		if (result == 0) {
			// 查询商户推送信息表
			List<Map<String, Object>> merchantClients = iMerchantPushDao
					.selectMerchantPush(paramMap);
			// 删除推送信息信息
			// 先删除这个手机号在相同app的用户，并且会触发推送
			int cleanCount = this.iMerchantPushDao.deleteMerchantPush(paramMap);
			// 删除这个设备的推送
			this.iMerchantPushDao.deleteMerchantPushByDeviceId(paramMap);
			// 判断如果踢掉其他商户，则推送给相应的客户端
			if (cleanCount > 0) {
				List<Map<String, Object>> AndroidPushInfo = new ArrayList<Map<String, Object>>();// 安卓终端
				// List<String> iosClientIDs = new ArrayList<String>();// 苹果终端
				List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> tempmap : merchantClients) {
					String cid = StringUtil.null2Str(tempmap.get("client_id"));
					if (!StringUtil.isNullStr(cid)) {
						// 不给当前的设备推送信息，但是需要删除这个设备的记录
						if (cid.equals(pushId)) {
							continue;
						}
						if (Constant.PUSH_CLIENT_TYPE_ANDROID == StringUtil
								.nullToInteger(tempmap.get("client_type"))) {
							Map<String, Object> info = new HashMap<String, Object>();
							info.put("clientId", cid);
							info.put("merchantId", StringUtil.null2Str(tempmap
									.get("merchant_id")));
							info.put("phone",
									StringUtil.null2Str(tempmap.get("phone")));
							info.put("appType", StringUtil.null2Str(tempmap
									.get("app_type")));
							// 安卓
							AndroidPushInfo.add(info);
						} else {
							// ios
							// iosClientIDs.add(cid);
							Map<String, Object> info = new HashMap<String, Object>();
							info.put("token", cid);
							info.put("merchantId", StringUtil.null2Str(tempmap
									.get("merchant_id")));
							iosPushInfoList.add(info);
						}
					}
				}
				try {
					// 通知之前的终端登出
					if (AndroidPushInfo != null && AndroidPushInfo.size() > 0) {
						// 安卓推送
						// 不通过代理
						// PushAndroidMessageToSHB pushToShb = new
						// PushAndroidMessageToSHB(appType, AndroidPushInfo,
						// "isExit,1", androidAppPushConfigDao,
						// commonCacheService);
						// Thread push = new Thread(pushToShb);
						// push.start();

						// 安卓推送
						// 通过代理方式
						Map<String, Object> pushMap = null;
						appType = Constant.PUSH_CONFIG.APP_TYPE_OMENGP;
						pushMap = (Map<String, Object>) commonCacheService
								.getObject(CacheConstants.PUSH_MAP, appType);
						if (pushMap == null) {
							Map<String, Object> paramPushMap = new HashMap<String, Object>();
							paramPushMap.put("appType", appType);
							pushMap = androidAppPushConfigDao
									.getPushConfig(paramPushMap);
							commonCacheService.setObject(pushMap,
									CacheConstants.PUSH_MAP, appType);
						}
						StringBuilder param = new StringBuilder();
						param.append("AndroidPushInfo=")
								.append(JSONObject
										.toJSONString(AndroidPushInfo))
								.append("&transmissionContent=isExit,1")
								.append("&pushMap=")
								.append(JSONObject.toJSONString(pushMap))
								.append("&concatKey=phone");
						// String param = "AndroidPushInfo=" +
						// JSONObject.toJSONString(AndroidPushInfo) +
						// "&transmissionContent=" + "isExit,1" + "&pushMap=" +
						// JSONObject.toJSONString(pushMap) +
						// "&concatKey=phone";
						PushAndroidMessageByProxy pushByProxy = new PushAndroidMessageByProxy(
								Constant.WEB_PROXY_URL + "/doPushToSHB?",
								param.toString());
						CalloutServices.executor(pushByProxy);

					}
					if (iosPushInfoList != null && iosPushInfoList.size() > 0) {
						// ios推送
						Map<String, Object> msg = PushMessageUtil
								.getIosMerchantPushMsg(appType, 0L, 0, "1", "0");
						Map<String, Object> cert = dictionaryService
								.getIosPushCertFromDict(Constant.IOS_MERCHANT_CERT);
						cert.put(Constant.IOS_CERT_TYPE,
								Constant.IOS_MERCHANT_CERT);

						// 不通过代理推送
						// IosPushUtil.push(iosPushInfoList, msg, cert);

						// 通过代理推送
						StringBuilder param = new StringBuilder();
						param.append("iosPushInfoList=")
								.append(JSONObject
										.toJSONString(iosPushInfoList))
								.append("&msg=")
								.append(JSONObject.toJSONString(msg))
								.append("&cert=")
								.append(JSONObject.toJSONString(cert));
						// String param = "iosPushInfoList=" +
						// JSONObject.toJSONString(iosPushInfoList) + "&msg=" +
						// JSONObject.toJSONString(msg) + "&cert=" +
						// JSONObject.toJSONString(cert);
						PushAndroidMessageByProxy pushByProxy = new PushAndroidMessageByProxy(
								Constant.WEB_PROXY_URL + "/doIosPush?",
								param.toString());
						CalloutServices.executor(pushByProxy);

					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("更新商户的推送信息", e);
				}
			}
			this.iMerchantPushDao.insertMerchantPush(paramMap);
		}
	}

	/** 服务项目显示的名字编辑 */
	private String serviceItemNameEdit(String appType, Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("appType", appType);
		paramMap.put("merchantId", merchantId);
		List<String> serviceTypeList = this.iMerchantServiceTypeDao
				.selectMerchantServiceTypeName(paramMap);
		StringBuilder service = new StringBuilder();
		for (String serviceType : serviceTypeList) {
			service.append(serviceType).append(Constant.COMMA_EN);
		}
		return BusinessUtil.deleteLastComma(service.toString());
	}

	/** 商户端退出应用 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject deleteMerchantPush(String clientId) throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("clientId", clientId);
		this.iMerchantPushDao.deleteMerchantPushByClientId(paramMap);
		jsonObject = new ResultJSONObject("000", "退出成功");

		return jsonObject;
	}

	/** 根据经纬度计算用户与商户之间的距离 */
	@Override
	public JSONObject selectCalcDistance(String appType, double longitude,
			double latitude, int range) throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("longitude", longitude);
		paramMap.put("latitude", latitude);
		paramMap.put("range", range);
		paramMap.put("appType", appType);
		jsonObject = new ResultJSONObject("000", "店铺距离加载成功");
		jsonObject.put("distanceList",
				this.iMerchantInfoDao.selectCalcDistance(paramMap));

		return jsonObject;
	}

	/** 获取商户在当前应用程序中所能提供的服务项目(订单页面标题显示用) */
	@Override
	public JSONObject selectMerchantProvideServiceType(String appType,
			Long merchantId, boolean allFlg) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("appType", appType);
		List<Map<String, Object>> merchantServiceTypeList = null;
		if (appType.startsWith("yxt") || appType.equals("ams_szx")
				|| appType.equals("ams_ydb")) {
			merchantServiceTypeList = new ArrayList<Map<String, Object>>();
		} else {
			merchantServiceTypeList = this.iMerchantServiceTypeDao
					.selectMerchantService(paramMap);
		}
		if (allFlg) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", 0);
			map.put("serviceTypeName", "全部");
			merchantServiceTypeList.add(0, map);
		}
		jsonObject = new ResultJSONObject("000", "商户服务项目加载成功");
		jsonObject.put("merchantServiceTypeList", merchantServiceTypeList);

		return jsonObject;
	}

	/** 验证商户是否已经退出 */
	@Override
	public JSONObject checkClient(String appType, Long userId, String clientId,
			String pushId, String phone, Long merchantId) throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> paramMap = new HashMap<String, Object>();
		int result = 0;
		if (userId == null) {
			// paramMap.put("appType", appType);
			// // 生成的设备ID
			// paramMap.put("clientId", clientId);
			paramMap.put("phone", phone);
			paramMap.put("merchantId", merchantId);
			// result = iMerchantPushDao.checkClient(paramMap);
			userId = iMerchantEmployeesDao.getUserIdByMerchantId(paramMap);
		}
		paramMap.put("userId", userId);
		paramMap.put("clientId", clientId);
		paramMap.put("pushId", pushId);
		result = iMerchantPushDao.checkClient(paramMap);
		if (result > 0) {
			jsonObject = new ResultJSONObject("000", "商户尚未退出");
		} else {
			jsonObject = new ResultJSONObject("exit", "商户已经退出");
		}
		return jsonObject;
	}

	/** 更新微官网URL */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateMicroWebsiteUrl(String appType, Long merchantId,
			String microWebsiteUrl) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		// 微官网
		paramMap.put("microWebsiteUrl", microWebsiteUrl);

		this.iMerchantInfoDao.updateMicroWebsiteUrl(paramMap);
		jsonObject = new ResultJSONObject("000", "微官网地址保存成功");
		// 清除商户缓存信息
//		commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//				StringUtil.null2Str(merchantId));
		//更新商品缓存
		Map<String, Object> newValue = new HashMap<String, Object>();
		newValue.put("microWebsiteUrl", microWebsiteUrl);
		this.updateMerchantCache(merchantId, newValue);

		return jsonObject;
	}

	/** 增值服务 */
	@Override
	public JSONObject addedServices(String appType, Long merchantId)
			throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("appType", appType);

		// 商户最大员工数
		int maxEmployeeNum = this.iMerchantInfoDao
				.selectMaxEmployeeNum(paramMap);
		// 当前商户已有的员工数
		int merchantEmployeesNum = this.iMerchantEmployeesDao
				.selectMerchantEmployeesCount(paramMap);
		jsonObject = new ResultJSONObject("000", "增值服务查询成功");
		// 剩余可添加员工（顾问号）数
		jsonObject.put("surplusEmployeesNum", maxEmployeeNum
				- merchantEmployeesNum);
		// 查询商户订单余额
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("merchantId", merchantId);
		map.put("appType", appType);
		jsonObject.put("orderSurplusMoney",this.iMerchantPayService.getOrderSurplusMoney(map));
		
		//查询商户最大员工申请状态-1-可申请，1-确认中，不可申请
		int employeesNumApplyStatus = iMerchantEmployeesNumApplyDao.selectEmployeesNumApplyStatus(paramMap);
		jsonObject.put("employeesNumApplyStatus", employeesNumApplyStatus);
//		if(employeesNumApplyStatus == 1){
//			paramMap.put("type", "employeesNum");
//			//顾问号申请最新记录
//			Map applying = iMerchantValueAddServiceDao.selectMerchantApplying(paramMap);
//			jsonObject.put("employeesNum",applying);
//		}
		int payApplyStatus = this.iMerchantStatisticsDao.selectMerchantPayApplyStatus(paramMap);
		//查询商户充值状态 -1-可充值，1-确认中，不可充值
		jsonObject.put("payApplyStatus", payApplyStatus);
//		if(payApplyStatus == 1){
//			paramMap.put("type", "topup");
//			//订单推送申请最新记录
//			Map applying = iMerchantValueAddServiceDao.selectMerchantApplying(paramMap);
//			jsonObject.put("topup",applying);
//		}
		
		// 商户接单次数
		jsonObject.put("grabFrequency", this.iMerchantStatisticsDao
				.selectGrabFrequency(paramMap).get("grabFrequency"));

		return jsonObject;
	}

	/** 更改当前使用的设备记录的clientId */
	@Override
	public JSONObject updateClientId(String appType, Long userId,
			String pushId, String phone, Long merchantId, String clientType,
			String cientId) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		JSONObject jsonObject = null;
		paramMap.put("appType", appType);
		paramMap.put("pushId", pushId);
		paramMap.put("phone", phone);
		paramMap.put("merchantId", merchantId);
		paramMap.put("cientId", cientId);
		paramMap.put("clientType", clientType);

		if (merchantId == null) {
			return new ResultJSONObject("000", "更改设备记录的clientId成功");
		}
		if (userId == null) {
			userId = iMerchantEmployeesDao.getUserIdByMerchantId(paramMap);
		}
		paramMap.put("userId", userId);
		int checkCount = iMerchantPushDao.checkClientByUserId(userId);
		if (checkCount == 0) {
			int result = iMerchantPushDao.updateClientId(paramMap);
			if (result > 0) {
				jsonObject = new ResultJSONObject("000", "更改设备记录的clientId成功");
			} else {
				logger.error("更改设备记录的clientId失败");
				jsonObject = new ResultJSONObject("fail", "更改设备记录的clientId失败");
			}
		} else {
			jsonObject = new ResultJSONObject("000", "更改设备记录的clientId成功");
		}

		return jsonObject;
	}

	/** 查询（商品）分类信息 */
	@Override
	public JSONObject selectGoodsClassificationInfo(String appType,
			Long merchantId,String isSelect) throws Exception {
		JSONObject jsonObject = null;
        //查询可添加分类数
        Map<String, Object> maxGoodsClassificationNumMap = commonService
                .getConfigurationInfoByKey("max_goodsClassification_num");
        Object maxGoodsClassificationNumObj = maxGoodsClassificationNumMap
                .get("config_value");
        int maxGoodsClassificationNum = Integer
                .parseInt(maxGoodsClassificationNumObj == null ? "0"
                        : maxGoodsClassificationNumObj.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);

		if(isSelect != null && "1".equals(isSelect)){//选择模式，只查上架商品
			paramMap.put("isAll", 0);//上架	
		}
		
		List<Map<String, Object>> goodsClassificationInfoList = this.iMerchantGoodsClassificationDao
				.selectGoodsClassificationInfo(paramMap);
		int goodsTotal = this.iMerchantGoodsDao.selectGoodsCount(paramMap);
		int outShelfGoodsTotal = this.iMerchantGoodsClassificationDao.selectIsOutGoodsCount(paramMap);
		jsonObject = new ResultJSONObject("000", "商品分类查询信息成功");
		jsonObject.put("goodsClassificatioInfoList",
				goodsClassificationInfoList);
		jsonObject.put("goodsTotal", goodsTotal);
		jsonObject.put("outShelfGoodsTotal", outShelfGoodsTotal);
        jsonObject.put("maxGoodsClassificationNum", maxGoodsClassificationNum);
        jsonObject.put("goodsClassificationNum", goodsClassificationInfoList != null ? goodsClassificationInfoList.size() : 0);
		return jsonObject;
	}

	/** 新建（商品）分类 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addGoodsClassification(String appType, Long merchantId,
			String classificationName) throws Exception {
		JSONObject jsonObject = null;

		Map<String, Object> maxGoodsClassificationNumMap = commonService
				.getConfigurationInfoByKey("max_goodsClassification_num");
		Object maxGoodsClassificationNumObj = maxGoodsClassificationNumMap
				.get("config_value");
		int maxGoodsClassificationNum = Integer
				.parseInt(maxGoodsClassificationNumObj == null ? "0"
						: maxGoodsClassificationNumObj.toString());

		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		int num = this.iMerchantGoodsClassificationDao
				.selectGoodsClassificationNum(paramMap);
		if (num >= maxGoodsClassificationNum) {
			return new ResultJSONObject("goods_classification_num_beyond",
					"商品分类数量已达最大值");
		}
		if (goodsClassificationNameRepetitionCheck(merchantId, null,
				classificationName)) {
			return new ResultJSONObject("goods_classification_name_repetition",
					"商品分类名重复");
		}
		paramMap.put("classificationName", classificationName);
		this.iMerchantGoodsClassificationDao
				.insertGoodsClassification(paramMap);
		jsonObject = new ResultJSONObject("000", "新建商品分类成功");
		return jsonObject;
	}

	/** 重命名（商品）分类名 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject renameGoodsClassification(String appType,
			Long merchantId, Long classificationId, String classificationName)
			throws Exception {
		if (goodsClassificationNameRepetitionCheck(merchantId,
				classificationId, classificationName)) {
			return new ResultJSONObject("goods_classification_name_repetition",
					"商品分类名重复");
		}
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商品分类ID
		paramMap.put("classificationId", classificationId);
		// 新商品分类名
		paramMap.put("classificationName", classificationName);

		this.iMerchantGoodsClassificationDao
				.updateGoodsClassificationName(paramMap);
		jsonObject = new ResultJSONObject("000", "重命名商品分类成功");

		return jsonObject;
	}

	/** （商品）分类名重复校验 */
	private boolean goodsClassificationNameRepetitionCheck(Long merchantId,
			Long classificationId, String classificationName) {
		if (StringUtils.isEmpty(classificationName)) {
			return false;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("classificationId", classificationId);
		List<Map<String, Object>> goodsClassificationInfoList = this.iMerchantGoodsClassificationDao
				.selectGoodsClassificationName(paramMap);
		for (Map<String, Object> map : goodsClassificationInfoList) {
			if (StringUtils.equals(classificationName,
					(String) map.get("classificationName"))) {
				return true;
			}
		}
		return false;
	}

	/** 删除（商品）分类 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject deleteGoodsClassification(String appType,
			Long merchantId, Long classificationId) throws ApplicationException,Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 分类ID
		paramMap.put("classificationId", classificationId);

		List<Long> goodsIdList = this.iMerchantGoodsClassificationRelationDao
				.selectAllGoodsIdByclassificationId(paramMap);
		if(goodsIdList != null && goodsIdList.size() > 0){
			throw new ApplicationException("deleteGoodsClassification_exception", "该分类下有商品，不能删除");
		}
//		Map<String, Object> goodsIdMap = new HashMap<String, Object>();
//		StringBuilder goodsIds = new StringBuilder();
//		for (Long goodsId : goodsIdList) {
//			int num = Collections.frequency(goodsIdList, goodsId);
//			if (num == 1) {
//				goodsIds.append(goodsId).append(Constant.COMMA_EN);
//			}
//		}
//		goodsIds = goodsIds.insert(0, Constant.COMMA_EN);
		// 删除分类
		int a = this.iMerchantGoodsClassificationDao
				.updateGoodsClassificationStatus(paramMap);
		// 删除商品分类关联
		int b = this.iMerchantGoodsClassificationRelationDao
				.deleteRelationByClassificationId(paramMap);
//		if (StringUtils.isNotEmpty(goodsIds)) {
//			goodsIdMap.put("goodsId", String.valueOf(goodsIds));
//			// 删除分类中的商品
//			int c = this.iMerchantGoodsDao.updateManyGoodsStatus(goodsIdMap);
//		}
		jsonObject = new ResultJSONObject("000", "删除商品分类成功");

		// 强制更新商店概要缓存 -----Revoke 2016.2.26
		commonCacheService.deleteObject(CacheConstants.MERCHANT_OUTLINE,
				merchantId.toString());

		return jsonObject;
	}

	/** 查询商品信息 */
	@Override
	public JSONObject selectGoodsInfo(String appType, Long merchantId,
			Long classificationId, int pageNo) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 分类ID
		paramMap.put("isAll", "1");//商铺查询全部商品，包括已下架
		paramMap.put("merchantId", merchantId);
		//商品总数 包括上架+下架
		int count = this.iMerchantGoodsDao.selectGoodsInfoCount(paramMap);
		
		paramMap.put("classificationId", classificationId);
		int totalPageCount = this.iMerchantGoodsDao.selectGoodsInfoCount(paramMap);
		jsonObject = new ResultJSONObject("000", "商品信息查询成功");
		if (totalPageCount == 0) {
			jsonObject.put("totalPage", 0);
			jsonObject.put("goodsInfoList",
					new ArrayList<HashMap<String, String>>());
		} else {
			// 查询起始记录行号
			paramMap.put("rows", pageNo * Constant.PAGESIZE);
			// 每页显示的记录数
			paramMap.put("pageSize", Constant.PAGESIZE);
			List<Map<String, Object>> goodsInfoList = this.iMerchantGoodsDao
					.selectGoodsInfo(paramMap);
			for (Map<String, Object> goodsInfoMap : goodsInfoList) {
				// 商品图片路径的补全
				BusinessUtil.disposePath(goodsInfoMap, "goodsPictureUrl");
			}
			jsonObject.put("totalPage", BusinessUtil.totalPageCalc(totalPageCount));
			jsonObject.put("goodsInfoList", goodsInfoList);
		}
		jsonObject.put("maxGoodsNum", this.getMaxGoodsNum(merchantId));
		jsonObject.put("goodsCount", count);
		return jsonObject;
	}

	/** 查询商品信息 */
	@Override
	public JSONObject selectGoodsList(Long merchantId,
			Long classificationId, int pageNo) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 分类ID
		paramMap.put("merchantId", merchantId);
		//商品总数 包括上架+下架
		int count = this.iMerchantGoodsDao.selectGoodsInfoCount(paramMap);

		paramMap.put("isAll", "0");//商铺查询上架商品
		paramMap.put("classificationId", classificationId);
		int totalPageCount = this.iMerchantGoodsDao.selectGoodsInfoCount(paramMap);
		jsonObject = new ResultJSONObject("000", "商品信息查询成功");
		if (totalPageCount == 0) {
			jsonObject.put("totalPage", 0);
			jsonObject.put("goodsInfoList",
					new ArrayList<HashMap<String, String>>());
		} else {
			// 查询起始记录行号
			paramMap.put("rows", pageNo * Constant.PAGESIZE);
			// 每页显示的记录数
			paramMap.put("pageSize", Constant.PAGESIZE);
			List<Map<String, Object>> goodsInfoList = this.iMerchantGoodsDao
					.selectGoodsInfo(paramMap);
			for (Map<String, Object> goodsInfoMap : goodsInfoList) {
				// 商品图片路径的补全
				BusinessUtil.disposePath(goodsInfoMap, "goodsPictureUrl");
			}
			jsonObject.put("totalPage", BusinessUtil.totalPageCalc(totalPageCount));
			jsonObject.put("goodsInfoList", goodsInfoList);
		}

		jsonObject.put("maxOrderPlanGoodsCount", this.getmaxOrderPlanGoodsCount(merchantId));
        jsonObject.put("maxGoodsNum", this.getMaxGoodsNum(merchantId));
		jsonObject.put("goodsCount", count);
		return jsonObject;
	}
	
	/** 新建商品 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject addGoods(String appType, Long merchantId,
			String classificationIds, String goodsName, String goodsPrice,
			String goodsDescribe,String goodsPriceUnit, List<String> paths) throws Exception {
		JSONObject jsonObject = null;
		Map<String, Object> goodsMap = new HashMap<String, Object>();
		// 商户ID
		goodsMap.put("merchantId", merchantId);
		// 商品名称
        if (goodsName == null) {
            throw new ApplicationException("add_goods_name_null","商品名称不能为空");
        }
        if (goodsName.length() > 20) {
            throw new ApplicationException("add_goods_name_null","商品名称不能超过20个字");
        }
        goodsMap.put("goodsName", goodsName);
		// 商品价格
		goodsMap.put("goodsPrice", goodsPrice);
		// 商品描述
		if (!StringUtil.isNullStr(goodsDescribe) && (goodsDescribe.length() > 200)) {
            throw new ApplicationException("add_goods_name_null","商品描述不能超过200个字");
        }
		goodsMap.put("goodsDescribe", goodsDescribe);
		//商品价格单位
		goodsMap.put("goodsPriceUnit", goodsPriceUnit);
		//商品上下架状态，默认上架
		goodsMap.put("isOut", 0);
		
		// 商品图片路径
		String goodsPictureUrl = paths.get(0);//第一张为默认图片
		goodsMap.put("goodsPictureUrl", goodsPictureUrl);

		//保存商品信息并返回商品id
		this.iMerchantGoodsDao.insertGoods(goodsMap);
		insertGoodsClassificationRelation(merchantId, classificationIds,
				(Long) goodsMap.get("goodsId"));
		//保存商品图片信息
		List<Map<String,Object>> goodsPicList = new ArrayList<Map<String,Object>>();
		for(int i = 0;i<paths.size();i++){
			String path = paths.get(i);
			Map<String, Object> picMap = new HashMap<String, Object>();
			picMap.put("goodsId", goodsMap.get("goodsId"));
			picMap.put("path", path);
			picMap.put("ordernumber", i);
			goodsPicList.add(picMap);
			
		}
		this.iMerchantGoodsDao.insertGoodsPic(goodsPicList);
		
		jsonObject = new ResultJSONObject("000", "商品保存成功");

		// commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
		// StringUtil.null2Str(merchantId)); 待删除

		// 强制更新商店概要缓存 -----Revoke 2016.2.26
		commonCacheService.deleteObject(CacheConstants.MERCHANT_OUTLINE,
				merchantId.toString());
		//更新商品缓存
//		commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//				merchantId.toString());

		//更新商品缓存
		Map<String, Object> newValue = new HashMap<String, Object>();
		//商品数量
		goodsMap.put("isAll", 0);
		int goodsCount=this.iMerchantGoodsDao.selectGoodsCount(goodsMap);
		newValue.put("goodsCount", goodsCount);
		//前三商品
		goodsMap.put("num", 3);
		Map<String, Object> topGoodsInfo = iMerchantGoodsDao.selectLastGoodsPic(goodsMap);
		BusinessUtil.disposeManyPath(topGoodsInfo, "path");
		newValue.put("topGoodsInfo", topGoodsInfo.get("path"));
		newValue.put("last3GoodsInfo", last3GoodsInfo(merchantId));
		this.updateMerchantCache(merchantId, newValue);
		
		return jsonObject;
	}

	/** 更新商品信息 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject updateGoodsInfo(String appType, Long merchantId,
			Long goodsId, String classificationIds, String goodsName,
			String goodsPrice, String goodsDescribe,String goodsPriceUnit, List<String> paths) throws ApplicationException,Exception{
		JSONObject jsonObject = null;

        if (goodsName == null) {
            throw new ApplicationException("add_goods_name_null","商品名称不能为空");
        }
        if (goodsName.length() > 20) {
            throw new ApplicationException("add_goods_name_null","商品名称不能超过20个字");
        }
        if (!StringUtil.isNullStr(goodsDescribe) && (goodsDescribe.length() > 200)) {
			throw new ApplicationException("add_goods_name_null","商品描述不能超过200个字");
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goodsId", goodsId);
		paramMap.put("goodsName", goodsName);
		paramMap.put("goodsPrice", goodsPrice);
		paramMap.put("goodsDescribe", goodsDescribe);
		paramMap.put("goodsPriceUnit", goodsPriceUnit);
		
		String goodsPictureUrl = null;
		if(paths != null && paths.size()>0){
			goodsPictureUrl = paths.get(0);
		}
		paramMap.put("goodsPictureUrl", goodsPictureUrl);
		try {
			this.iMerchantGoodsDao.updateGoodsInfo(paramMap);
			List list = new ArrayList();
			list.add(goodsId);
			this.iMerchantGoodsClassificationRelationDao
					.deleteRelationByGoodsId(list);
			insertGoodsClassificationRelation(merchantId, classificationIds,
					goodsId);

			if(!StringUtil.isNull(goodsPictureUrl)){
				//删除原附件
				this.iMerchantGoodsDao.deleteGoodsPic(list);
				//保存商品图片信息
				List<Map<String,Object>> goodsPicList = new ArrayList<Map<String,Object>>();
				for(int i = 0;i<paths.size();i++){
					String path = paths.get(i);
					if(StringUtil.isNotEmpty(path)){
						Map<String, Object> picMap = new HashMap<String, Object>();
						picMap.put("goodsId", goodsId);
						picMap.put("path", path);
						picMap.put("ordernumber", i);
						goodsPicList.add(picMap);
					}
				}
				this.iMerchantGoodsDao.insertGoodsPic(goodsPicList);
			}
			//上架更新产品版本号：如果有快照（被是用过）那么更新版本号+1，如果没有快照（未被使用）不用更新版本号，防止产生垃圾数据
			iMerchantGoodsDao.updateGoodsVersion(paramMap);
			jsonObject = new ResultJSONObject("000", "商品信息更新成功");
		} catch (Exception ex) {
			logger.error("商品信息更新失败", ex);
			throw new ApplicationException(ex, "goods_info_update_failure",
					"商品信息更新失败");
		}

		// 强制更新商店概要缓存 -----Revoke 2016.2.26
		commonCacheService.deleteObject(CacheConstants.MERCHANT_OUTLINE,
				merchantId.toString());
		//更新商品缓存
//		commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//				merchantId.toString());
		//更新商品缓存
		Map<String, Object> newValue = new HashMap<String, Object>();
		Map<String,Object> goodsMap = new HashMap<String, Object>();
		goodsMap.put("merchantId", merchantId);
		//商品数量
//		goodsMap.put("isAll", 0);
//		int goodsCount=this.iMerchantGoodsDao.selectGoodsCount(goodsMap);
//		newValue.put("goodsCount", goodsCount);
		//前三商品
		goodsMap.put("num", 3);
		Map<String, Object> topGoodsInfo = iMerchantGoodsDao.selectLastGoodsPic(goodsMap);
		BusinessUtil.disposeManyPath(topGoodsInfo, "path");
		newValue.put("topGoodsInfo", topGoodsInfo.get("path"));
		newValue.put("last3GoodsInfo", last3GoodsInfo(merchantId));
		this.updateMerchantCache(merchantId, newValue);

		return jsonObject;
	}

	/** 商品信息校验 */
	@Override
	public JSONObject goodsInfoCheck(Long merchantId, Long goodsId,
			String goodsName, String classificationIds, int addOrUpdate) {
		// addOrUpdate: 1添加商品场合，2更新商品场合
		JSONObject jsonObject = null;
//		Map<String, Object> maxGoodsNumMap = commonService
//				.getConfigurationInfoByKey("max_goods_num");
//		Object maxGoodsNumObj = maxGoodsNumMap.get("config_value");
//		int maxGoodsNum = Integer.parseInt(maxGoodsNumObj == null ? "0"
//				: maxGoodsNumObj.toString());
		int maxGoodsNum = this.getMaxGoodsNum(merchantId);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		if (addOrUpdate == 1) {
			if (this.iMerchantGoodsDao.selectGoodsCount(paramMap) >= maxGoodsNum) {
				jsonObject = new ResultJSONObject("goods_num_beyond_max",
						"商品数量已达最大值");
			}
		}
		if (goodsNameRepetitionCheck(merchantId, goodsId, goodsName)) {
			jsonObject = new ResultJSONObject("goods_name_repetition", "商品名称重复");
		}
		if (StringUtils.isEmpty(classificationIds)) {
			jsonObject = new ResultJSONObject("classification_is_empty",
					"至少选择一个分类");
		}
		if (classificationIds.contains(Constant.COMMA_EN)
				&& classificationIds.split(Constant.COMMA_EN).length > 3) {
			jsonObject = new ResultJSONObject("classification_is_beyond",
					"至多选择三个分类");
		}
		return jsonObject;
	}


    //允许方案中选择商品数
    public int getmaxOrderPlanGoodsCount(Long merchantId){
        int maxOrderPlanGoodsCount = 0;
        Map<String, Object> maxGoodsNumMap = commonService
                .getConfigurationInfoByKey("max_orderPlan_goodsCount");
        if(maxGoodsNumMap == null){
        	throw new ApplicationException("no_max_orderPlan_goodsCount", "未配置初始化参数:max_orderPlan_goodsCount");
        }
        Object maxGoodsNumObj = maxGoodsNumMap.get("config_value");
        maxOrderPlanGoodsCount = Integer.parseInt(maxGoodsNumObj == null ? "0"
                : maxGoodsNumObj.toString());
        return maxOrderPlanGoodsCount;
    }


	public int getMaxGoodsNum(Long merchantId){
		List<RuleConfig> ruleList = incService.getRuleConfig(merchantId);
		RuleConfig rule = ruleList.get(0);
		int goodsUpperLimit = 0;
		if(rule.getGoodsUpperLimit() == null || rule.getGoodsUpperLimit() == 0){//没有规则，取系统默认配置
			Map<String, Object> maxGoodsNumMap = commonService
					.getConfigurationInfoByKey("max_goods_num");
			Object maxGoodsNumObj = maxGoodsNumMap.get("config_value");
			goodsUpperLimit = Integer.parseInt(maxGoodsNumObj == null ? "0"
					: maxGoodsNumObj.toString());
		}else{
			goodsUpperLimit = rule.getGoodsUpperLimit();
		}
		return goodsUpperLimit;
	}
	
	/** 商品名重复校验 */
	private boolean goodsNameRepetitionCheck(Long merchantId, Long goodsId,
			String goodsName) {
		if (StringUtils.isEmpty(goodsName)) {
			return false;
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("goodsId", goodsId);
		List<Map<String, Object>> goodsClassificationInfoList = this.iMerchantGoodsDao
				.selectGoodsName(paramMap);
		for (Map<String, Object> map : goodsClassificationInfoList) {
			if (StringUtils.equals(goodsName, (String) map.get("goodsName"))) {
				return true;
			}
		}
		return false;
	}

	/** 商品分类关联的保存 */
	private void insertGoodsClassificationRelation(Long merchantId,
			String classificationIds, Long goodsId) {
		List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
		for (String classificationId : classificationIds.split(",")) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// 商户ID
			paramMap.put("merchantId", merchantId);
			// 分类ID
			paramMap.put("classificationId", classificationId);
			// 商品ID
			paramMap.put("goodsId", goodsId);
			paramList.add(paramMap);
		}
		this.iMerchantGoodsClassificationRelationDao
				.insertGoodsClassificationRelation(paramList);
	}

	/** 删除商品 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject deleteGoods(String appType, Long merchantId,
			Long classificationId, String goodsId) {
		JSONObject jsonObject = null;
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		// 商品分类ID
//		paramMap.put("classificationId", classificationId);
//		// 商品ID
//		paramMap.put("goodsId", goodsId);
		try {
			if(StringUtil.isNotEmpty(goodsId)){
				String[] goodsIds = goodsId.split(",");
				List list = new ArrayList();
				for(String goodsId2 : goodsIds){
					list.add(goodsId2.trim());
				}
				this.iMerchantGoodsClassificationRelationDao
						.deleteRelationByGoodsId(list);
				this.iMerchantGoodsDao.deleteGoodsPic(list);
				this.iMerchantGoodsDao.updateManyGoodsStatus(list);
			}
			jsonObject = new ResultJSONObject("000", "商品删除成功");

			// commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
			// StringUtil.null2Str(merchantId)); 待删除
		} catch (Exception ex) {
			logger.error("商品删除失败", ex);
			throw new ApplicationException(ex, "goods_delete_failure", "商品删除失败");
		}

		// 强制更新商店概要缓存 -----Revoke 2016.2.26
		commonCacheService.deleteObject(CacheConstants.MERCHANT_OUTLINE,
				merchantId.toString());
		//更新商品缓存
//		commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,
//				merchantId.toString());
		//更新商品缓存
		Map<String, Object> newValue = new HashMap<String, Object>();
		Map<String,Object> goodsMap = new HashMap<String, Object>();
		goodsMap.put("merchantId", merchantId);
		//商品数量
		goodsMap.put("isAll", 0); 
		int goodsCount=this.iMerchantGoodsDao.selectGoodsCount(goodsMap);
		newValue.put("goodsCount", goodsCount);
		//前三商品
		goodsMap.put("num", 3);
		Map<String, Object> topGoodsInfo = iMerchantGoodsDao.selectLastGoodsPic(goodsMap);
		BusinessUtil.disposeManyPath(topGoodsInfo, "path");
		newValue.put("topGoodsInfo", topGoodsInfo.get("path"));
		newValue.put("last3GoodsInfo", last3GoodsInfo(merchantId));
		this.updateMerchantCache(merchantId, newValue);
		
		return jsonObject;
	}

	/** 商户最新2张商品图片 */
	private String last2GoodsPictures(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		Map<String, Object> picturesUrl = this.iMerchantGoodsDao
				.selectLast2GoodsPictures(paramMap);
		if (picturesUrl == null || picturesUrl.isEmpty()
				|| picturesUrl.get("path") == null) {
			return Constant.EMPTY;
		}
		BusinessUtil.disposeManyPath(picturesUrl, "path");
		return picturesUrl.get("path").toString();
	}

	/** 商户最新3张商品信息 */
	private List<Map<String, Object>> last3GoodsInfo(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		paramMap.put("isAll", 0);
		List<Map<String, Object>> last3GoodsInfo = this.iMerchantGoodsDao
				.selectLast3GoodsInfo(paramMap);
		BusinessUtil.disposeManyPath(last3GoodsInfo, "goodsPictureUrl");
		return last3GoodsInfo;
	}

	/** 用户查看商户的客户评价 */
	@Override
	public JSONObject userEvaluationForUser(String appType, Long userId,
			Long merchantId, int pageNo) {
		JSONObject jsonObject = new ResultJSONObject("000", "顾客评价信息加载成功");
		DecimalFormat df = new DecimalFormat("#.0");
		int evaluationOrderNum = this.evaluationDao
				.getMerchantEvaluationNum(merchantId);
		try {
			if (pageNo == 0) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("merchantId", merchantId);
				paramMap.put("appType", appType);
				Map<String, Object> previewInfoMap = iMerchantStatisticsDao
						.selectPreviewInfo(paramMap);
				if (previewInfoMap != null) {
					// 评价人数
					jsonObject.put("evaluationOrderNum", evaluationOrderNum);
					Integer totalAttitudeEvaluation = Integer
							.parseInt(previewInfoMap
									.get("totalAttitudeEvaluation") == null ? "0"
									: previewInfoMap
											.get("totalAttitudeEvaluation")
											+ "");
					Integer totalQualityEvaluation = Integer
							.parseInt(previewInfoMap
									.get("totalQualityEvaluation") == null ? "0"
									: previewInfoMap
											.get("totalQualityEvaluation") + "");
					Integer totalSpeedEvaluation = Integer
							.parseInt(previewInfoMap
									.get("totalSpeedEvaluation") == null ? "0"
									: previewInfoMap
											.get("totalSpeedEvaluation") + "");
					// 总服务态度评价+总服务质量评价+总服务速度评价
					int totalEvaluation = totalAttitudeEvaluation
							+ totalQualityEvaluation + totalSpeedEvaluation;
					if (evaluationOrderNum == 0) {
						// 没有用户评价的时候设置默认值
						jsonObject.put("avgEvaluation", 5);
						jsonObject.put("starLevel", 5);
						jsonObject.put("evaluationOrderNum", 0);
						jsonObject.put("avgAttitudeEvaluation", 0);
						jsonObject.put("starLevelAttitude", 0);
						jsonObject.put("avgQualityEvaluation", 0);
						jsonObject.put("starLevelQuality", 0);
						jsonObject.put("avgSpeedEvaluation", 0);
						jsonObject.put("starLevelSpeed", 0);
					} else {
						// 星级
						BigDecimal starLevel = new BigDecimal(totalEvaluation)
								.divide(new BigDecimal(evaluationOrderNum)
										.multiply(new BigDecimal(3)), 0,
										BigDecimal.ROUND_HALF_UP);
						if (starLevel.compareTo(new BigDecimal(5)) > 0) {
							starLevel = new BigDecimal(5);
						}
						if (starLevel.compareTo(new BigDecimal(0)) < 0) {
							starLevel = new BigDecimal(0);
						}
						jsonObject.put("avgEvaluation", df
								.format(new BigDecimal(totalEvaluation).divide(
										new BigDecimal(evaluationOrderNum)
												.multiply(new BigDecimal(3)),
										1, BigDecimal.ROUND_HALF_UP)));
						jsonObject.put("starLevel", starLevel);
						jsonObject
								.put("evaluationOrderNum", evaluationOrderNum);

						jsonObject.put("avgAttitudeEvaluation", df
								.format(new BigDecimal(totalAttitudeEvaluation)
										.divide(new BigDecimal(
												evaluationOrderNum), 1,
												BigDecimal.ROUND_HALF_UP)));
						jsonObject.put("starLevelAttitude", new BigDecimal(
								totalAttitudeEvaluation).divide(new BigDecimal(
								evaluationOrderNum), 0,
								BigDecimal.ROUND_HALF_UP));

						jsonObject.put("avgQualityEvaluation", df
								.format(new BigDecimal(totalQualityEvaluation)
										.divide(new BigDecimal(
												evaluationOrderNum), 1,
												BigDecimal.ROUND_HALF_UP)));
						jsonObject.put("starLevelQuality", new BigDecimal(
								totalQualityEvaluation).divide(new BigDecimal(
								evaluationOrderNum), 0,
								BigDecimal.ROUND_HALF_UP));

						jsonObject.put("avgSpeedEvaluation", df
								.format(new BigDecimal(totalSpeedEvaluation)
										.divide(new BigDecimal(
												evaluationOrderNum), 1,
												BigDecimal.ROUND_HALF_UP)));
						jsonObject.put("starLevelSpeed", new BigDecimal(
								totalSpeedEvaluation).divide(new BigDecimal(
								evaluationOrderNum), 0,
								BigDecimal.ROUND_HALF_UP));
					}
				} else {
					// 统计信息异常的时候
					jsonObject.put("totalEvaluation", 5);
					jsonObject.put("starLevel", 5);
					jsonObject.put("evaluationOrderNum", 0);
					jsonObject.put("avgAttitudeEvaluation", 0);
					jsonObject.put("starLevelAttitude", 0);
					jsonObject.put("avgQualityEvaluation", 0);
					jsonObject.put("starLevelQuality", 0);
					jsonObject.put("avgSpeedEvaluation", 0);
					jsonObject.put("starLevelSpeed", 0);
				}
			}
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// 商户ID
			paramMap.put("merchantId", merchantId);
			// 应用程序类型
			paramMap.put("appType", appType);
			// 查询起始记录行号
			paramMap.put("rows", pageNo * Constant.PAGESIZE);
			// 每页显示的记录数
			paramMap.put("pageSize", Constant.PAGESIZE);

			if (evaluationOrderNum == 0) {
				jsonObject.put("totalPage", 0);
				jsonObject.put("userEvaluationList",
						new ArrayList<HashMap<String, String>>());
				return jsonObject;
			} else {
				jsonObject.put("totalPage",
						BusinessUtil.totalPageCalc(evaluationOrderNum));
				List<Map<String, Object>> userEvaluationMapList = this.evaluationDao
						.getMerchantEvaluationsForUser(paramMap);
				List<Map<String, Object>> userEvaluationMapListNew = new ArrayList<Map<String, Object>>();
				for (Map<String, Object> map : userEvaluationMapList) {
					int attitudeEvaluation = Integer.parseInt(map
							.get("attitudeEvaluation") == null ? "0" : map
							.get("attitudeEvaluation") + "");
					int qualityEvaluation = Integer.parseInt(map
							.get("qualityEvaluation") == null ? "0" : map
							.get("qualityEvaluation") + "");
					int speedEvaluation = Integer.parseInt(map
							.get("speedEvaluation") == null ? "0" : map
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
					Map<String, Object> mapNew = new HashMap<String, Object>();
					mapNew.put("textEvaluation", map.get("textEvaluation"));
					mapNew.put("time", map.get("time"));
					mapNew.put("userName", map.get("userName"));
					mapNew.put("phone", map.get("phone"));
					mapNew.put("path", BusinessUtil.disposeImagePath(String
							.valueOf(map.get("path"))));

					// Revoke 2016.5.5 如果用户名为空，则回传屏蔽4位的phone 作为 用户名
					Object userName = map.get("userName");
					if (userName == null
							|| userName.toString().trim().length() < 1) {
						Object phone = map.get("phone");
						if (phone != null && phone.toString().length() > 0) {
							phone = phone.toString().substring(0, 3) + "****"
									+ phone.toString().substring(7, phone.toString().length());
							mapNew.put("userName", phone);
						}

					}
					mapNew.put("evaluation", evaluation);
					mapNew.put("avgEvaluation", df.format(new BigDecimal(total)
							.divide(new BigDecimal(3), 1,
									BigDecimal.ROUND_HALF_UP)));
					mapNew.put("starLevel", avg);
					BusinessUtil.disposeManyPath(map,"attachmentPaths");
					mapNew.put("attachmentPaths", map.get("attachmentPaths"));
					userEvaluationMapListNew.add(mapNew);
				}
				jsonObject.put("userEvaluationList", userEvaluationMapListNew);
			}
		} catch (Exception ex) {
			jsonObject = new ResultJSONObject(
					"user_evaluation_info_load_failure", "顾客评价信息加载失败");
			logger.error(ex.getMessage(), ex);
		}
		return jsonObject;
	}

	/** 商户最新1张相片 */
	private String last1Photo(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		Map<String, Object> photosUrl = this.iMerchantPhotoDao
				.selectLast1Photo(paramMap);
		if (photosUrl == null || photosUrl.isEmpty()
				|| photosUrl.get("path") == null) {
			return Constant.EMPTY;
		}
		BusinessUtil.disposeManyPath(photosUrl, "path");
		return photosUrl.get("path").toString();
	}

	/** 商户最新2张相片 */
	private String last2Photo(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		Map<String, Object> photosUrl = this.iMerchantPhotoDao
				.selectLast2Photo(paramMap);
		if (photosUrl == null || photosUrl.isEmpty()
				|| photosUrl.get("path") == null) {
			return Constant.EMPTY;
		}
		BusinessUtil.disposeManyPath(photosUrl, "path");
		return photosUrl.get("path").toString();
	}

	/** 商户最新4张相片 */
	private String last4Photo(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		Map<String, Object> photosUrl = this.iMerchantPhotoDao
				.selectLast4Photo(paramMap);
		if (photosUrl == null || photosUrl.isEmpty()
				|| photosUrl.get("path") == null) {
			return Constant.EMPTY;
		}
		BusinessUtil.disposeManyPath(photosUrl, "path");
		return photosUrl.get("path").toString();
	}

	/** 商户的服务标签保存 */
	@Override
	public JSONObject serviceTagSave(String tag, Long merchantId) {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tag", tag);
		paramMap.put("merchantId", merchantId);

		/*
		 * int auditResult =
		 * this.iMerchantServiceTagDao.checkAuditMerchantServiceTag(paramMap);
		 * if (auditResult == 0) { jsonObject = new ResultJSONObject("audit",
		 * "该个性服务标签正在审核"); return jsonObject; }
		 */
		if (this.iMerchantServiceTagDao.checkMerchantServiceTag(paramMap) > 0) {
			jsonObject = new ResultJSONObject("exist", "该个性服务已经添加过");
			return jsonObject;
		}
		int checkCount = this.iMerchantServiceTagDao
				.checkAddMerchantServiceTag(paramMap);
		if (checkCount >= 10) {
			jsonObject = new ResultJSONObject("big", "该商户已经添加了10个服务标签");
			return jsonObject;
		}
		this.iMerchantServiceTagDao.insertMerchantServiceTag(paramMap);

		// 先检查商户服务标签是否添加为关键词 然后 添加商户服务标签为待审核关键词
		if (this.iMerchantServiceTagDao.checkServiceTagForKeyword(paramMap) == 0) {
			this.iMerchantServiceTagDao.insertServiceTagForKeyword(paramMap);
		}

		jsonObject = new ResultJSONObject("000", "个性服务添加成功");
		return jsonObject;
	}

	/** 删除服务标签 */
	@Override
	public JSONObject deleteServiceTag(String tag, Long merchantId) {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tag", tag);
		paramMap.put("merchantId", merchantId);
		List<Map<String, Object>> tagIds = this.iMerchantServiceTagDao
				.selectMerchantServiceTagId(paramMap);
		if (this.iMerchantServiceTagDao.deleteMerchantServiceTag(paramMap) > 0) {
			// 删除个性服务的索引
			List<String> idList = new ArrayList<String>();
			for (Map<String, Object> tagId : tagIds) {
				idList.add(StringUtil.nullToString(tagId.get("id")));
			}
			if (idList.size() > 0) {
				String ids = "";
				for (String id : idList) {
					ids = ids + id + ",";
				}
				if (ids.length() > 0) {
					ids = ids.substring(0, ids.length() - 1);
				}
				try {
					elasticSearchService.delDocument(ids, "gxfwindex");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		jsonObject = new ResultJSONObject("000", "个性服务删除成功");
		return jsonObject;
	}

	/** 查询服务标签 */
	@Override
	public JSONObject selectServiceTag(Long merchantId) {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		List<Map<String, Object>> myTag = this.iMerchantServiceTagDao
				.selectMerchantServiceTag(paramMap);
		List<Map<String, Object>> recommendTag = this.gxfwIndexDao
				.selectRecommendServiceTag();
		jsonObject = new ResultJSONObject("000", "查询个性服务标签");
		jsonObject.put("myTagCount", (myTag == null) ? 0 : myTag.size());
		jsonObject.put("myTag", myTag);
		jsonObject.put("recommendTagCount", (recommendTag == null) ? 0
				: recommendTag.size());
		jsonObject.put("recommendTag", recommendTag);
		return jsonObject;
	}

	/** 选择推荐的服务标签保存 */
	@Override
	public JSONObject chooseServiceTagSave(String tags, Long merchantId) {
		JSONObject jsonObject = null;
		String[] tagArray = tags.split(",");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("tagArray", tagArray);
		paramMap.put("merchantId", merchantId);
		int checkCount = 0;
		checkCount = this.iMerchantServiceTagDao
				.checkAddMerchantServiceTag(paramMap);
		if (checkCount >= 16) {
			jsonObject = new ResultJSONObject("big", "该商户已经添加了16个服务标签");
			return jsonObject;
		}
		if (checkCount + tagArray.length > 16) {
			jsonObject = new ResultJSONObject("big", "该商户只能添加16个服务标签");
			return jsonObject;
		}
		this.iMerchantServiceTagDao.chooseServiceTagSave(paramMap);
		jsonObject = new ResultJSONObject("000", "选择个性服务添加成功");
		return jsonObject;
	}

	/** 查询增值服务记录 */
	@Override
	public JSONObject selectValueAddedService(Long merchantId, int pageNo) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		int totalPage = 0;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		paramMap.put("startNum", pageNo * Constant.PAGESIZE);
		paramMap.put("pageSize", Constant.PAGESIZE);
		List<Map<String, Object>> cachedOrders = null;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		if (commonCacheService.getObject(CacheConstants.VALUE_ADD_SERVICE, merchantId.toString()) == null) {
			List<Map<String, Object>> selectList = this.iMerchantValueAddServiceDao.selectMerchantValueAddService(paramMap);
			for (Map<String, Object> map : selectList) {
				if (StringUtil.null2Str(map.get("type")).equals("topup")) {
//					map.put("title", "接单余额：" + StringUtil.null2Str(map.get("money")) + "元");
					map.put("title", "订单推送");
				}else if  (StringUtil.null2Str(map.get("type")).equals("employeesNum")) {
					map.put("title", "员工账号：" + StringUtil.null2Str(map.get("applyIncreaseEmployeeNum")) + "个");
				}else
				{
					String status=StringUtil.null2Str(map.get("applyStatus"));
					if(status.equals("1")){
						map.put("applyStatus",2);
					}
					map.put("title",map.get("type"));
				}
				if (StringUtil.null2Str(map.get("applyStatus")).equals("0")) {
					map.put("applyStatusName", "待支付");
				}
				if (StringUtil.null2Str(map.get("applyStatus")).equals("1")) {
					map.put("applyStatusName", "待开通");
				}
				if (StringUtil.null2Str(map.get("applyStatus")).equals("2")) {
					map.put("applyStatusName", "已开通");
				}
				if (StringUtil.null2Str(map.get("applyStatus")).equals("3")) {
					map.put("applyStatusName", "无效");
				}
				if (StringUtil.null2Str(map.get("applyStatus")).equals("4")) {
					map.put("applyStatusName", "已过期");
				}
				map.remove("sortTime");
				BusinessUtil.disposePath(map, "iconPath");
			}
			commonCacheService.setObject(selectList, CacheConstants.VALUE_ADD_SERVICE, merchantId.toString());
			cachedOrders = selectList;
		} else {
			cachedOrders = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.VALUE_ADD_SERVICE, merchantId.toString());
		}

		totalPage = constructResultFromCachedOrders(cachedOrders, resultList, pageNo);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "获取商户增加服务记录列表成功");
		jsonObject.put("orderList", resultList);
		jsonObject.put("totalPage", totalPage);
		return jsonObject;
	}

	/** 删除服务记录 */
	@Override
	public JSONObject delValueAddedService(Long merchantId, Long serviceId,
			String serviceType) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("serviceId", serviceId);
		if (serviceType.equals("topup")) {
			this.iMerchantValueAddServiceDao.delTopupService(paramMap);
		}
		if (serviceType.equals("vip")) {
			this.iMerchantValueAddServiceDao.delVipService(paramMap);
		}
		if (serviceType.equals("employeesNum")) {
			this.iMerchantValueAddServiceDao.delEmployeesService(paramMap);
		}
		commonCacheService.deleteObject(CacheConstants.VALUE_ADD_SERVICE,
				merchantId.toString());
		return new ResultJSONObject("000", "删除服务记录成功");
	}

    public JSONObject updateMerchantEmployeesNumApplyOpenTime(Long serviceId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("serviceId", serviceId);
        iMerchantValueAddServiceDao.updateMerchantEmployeesNumApplyOpenTime(paramMap);
        return new ResultJSONObject("000", "修改开通时间成功");
    }

	/** 根据缓存的订单来过滤复合条件的记录 */
	private int constructResultFromCachedOrders(
			List<Map<String, Object>> cachedOrders,
			List<Map<String, Object>> resultList, int pageNo) {
		if (cachedOrders == null || cachedOrders.size() < 1) {
			return 0;
		}
		if (cachedOrders.size() < 1
				|| pageNo * Constant.PAGESIZE > cachedOrders.size()) {
			return 0;
		} else {
			for (int num = 0, startIndex = pageNo * Constant.PAGESIZE; num < Constant.PAGESIZE
					&& startIndex < cachedOrders.size(); num++, startIndex++) {
				resultList.add(cachedOrders.get(startIndex));
			}
			return 1 + (cachedOrders.size() - 1) / Constant.PAGESIZE;
		}

	}

	@Override
	public Map<String, Object> getLocationInfo(Long merchantId)
			throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		return iMerchantInfoDao.selectNameAndAddressForUser(paramMap);
	}

	@Override
	public Map<String, Object> selectMerchantBasicForUser(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);

		Map<String, Object> merchantInfo = null;
		try {
			merchantInfo = iMerchantInfoDao.selectMerchantBasicInfo(paramMap);
			if (merchantInfo != null) {
				String path = StringUtil.null2Str(merchantInfo.get("path"));
				merchantInfo.put("path", BusinessUtil.disposeImagePath(path));
			}
		} catch (Exception ex) {
			logger.error("店铺信息加载失败", ex);
			return merchantInfo;
		}
		return merchantInfo;
	}

    /**
     * 获取商户基本信息
     *
     * @param merchantId 商户ID
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> selectMerchantBasicInfo(Long merchantId) throws Exception {
//        return basicInfo(merchantId);
        return merchantBasicInfo(merchantId);
    }

	/** 验证商户信息完成度 */
	@Override
	public Map<String, Object> checkMerchantInfo(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		Map<String, Object> checkMerchantInfo = iMerchantInfoDao
				.checkMerchantInfo(paramMap);
		Map<String, Object> merchantInfo = new HashMap<String, Object>();
		if (checkMerchantInfo != null) {
			// 判断店铺LOGO
			if (StringUtil.null2Str(checkMerchantInfo.get("iconUrl")).equals(
					Constant.DEFAULT_MERCHANT_PORTRAIT_PTAH)) {
				// merchantInfo.put("logo", "0");
			} else {
				merchantInfo.put("logo", "1");
			}
			// 判断店铺名称
			if (StringUtil.isNotEmpty(checkMerchantInfo.get("name"))) {
				merchantInfo.put("name", "1");
			} else {
				// merchantInfo.put("name", "0");
			}
			// 判断店铺简介
			if (StringUtil.isNotEmpty(checkMerchantInfo.get("detail"))) {
				merchantInfo.put("detail", "1");
			} else {
				// merchantInfo.put("detail", "0");
			}
			// 联系信息
			if (StringUtil.isNotEmpty(checkMerchantInfo.get("phone"))) {
				merchantInfo.put("phone", "1");
			} else {
				// merchantInfo.put("phone", "0");
			}
			// 店铺地址
			if (StringUtil.isNotEmpty(checkMerchantInfo.get("locationAddress"))
					&& StringUtil
							.isNotEmpty(checkMerchantInfo.get("longitude"))
					&& StringUtil.isNotEmpty(checkMerchantInfo.get("latitude"))) {
				merchantInfo.put("address", "1");
			} else {
				// merchantInfo.put("address", "0");
			}
			// 店铺相册
			if (StringUtil.nullToInteger(checkMerchantInfo.get("photo")) > 0) {
				merchantInfo.put("photo", "1");
			} else {
				// merchantInfo.put("photo", "0");
			}
			// 店铺商品
			if (StringUtil.nullToInteger(checkMerchantInfo.get("goods")) > 0) {
				merchantInfo.put("goods", "1");
			} else {
				// merchantInfo.put("goods", "0");
			}
			// 获得认证类型。 0-未认证 1-企业认证 2-个人认证
			Map<String, Object> authMap = this.iMerchantAuthDao
					.selectAuthType(paramMap);
			if (authMap != null) {
				String authType = authMap.get("authType") == null ? "0"
						: authMap.get("authType").toString();
				// String authStatus = authMap.get("authStatus") == null ? "0" :
				// authMap.get("authStatus").toString();
				// merchantInfo.put("authStatus", authStatus);
				// if (!authStatus.equals("1")) {
				// merchantInfo.put("auth", 0);
				// } else {
				merchantInfo.put("auth", authType);
				// }
			} else {
				// merchantInfo.put("auth", 0);
			}
			return merchantInfo;
		} else {
			return null;
		}
	}

	/** 计算魅力值 */
	private int charmValueEdit(Long merchantId) {
		int charmValue = 0;
		Map<String, Object> map = checkMerchantInfo(merchantId);
		// if (!"xhf".equals(appType) && !"dgf".equals(appType) &&
		// !"ydc".equals(appType) && !"swg".equals(appType)) {
		if (map.get("logo") != null) {
			charmValue = charmValue + 20;
		}
		if (map.get("name") != null) {
			charmValue = charmValue + 10;
		}
		if (map.get("detail") != null) {
			charmValue = charmValue + 15;
		}
		if (map.get("auth") != null) {
			charmValue = charmValue + 10;
		}
		if (map.get("phone") != null) {
			charmValue = charmValue + 10;
		}
		if (map.get("address") != null) {
			charmValue = charmValue + 10;
		}
		if (map.get("photo") != null) {
			charmValue = charmValue + 25;
		}
		// } else {
		// if (map.get("logo") != null) {
		// charmValue = charmValue + 20;
		// }
		// if (map.get("name") != null) {
		// charmValue = charmValue + 10;
		// }
		// if (map.get("detail") != null) {
		// charmValue = charmValue + 10;
		// }
		// if (map.get("auth") != null) {
		// charmValue = charmValue + 10;
		// }
		// if (map.get("phone") != null) {
		// charmValue = charmValue + 10;
		// }
		// if (map.get("address") != null) {
		// charmValue = charmValue + 10;
		// }
		// if (map.get("photo") != null) {
		// charmValue = charmValue + 20;
		// }
		// if (map.get("goods") != null) {
		// charmValue = charmValue + 10;
		// }
		// }
		return charmValue;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject quickRegGxfwMerchantInfo(String phone, String name,
			String detial, String tags, String ip) {
		String appType = "gxfw";
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("phone", phone);
			paramMap.put("appType", appType);
			// 先检查手机号是否注册
			Map<String, Object> merchantInfoForLoginCached = this.iMerchantEmployeesDao
					.selectMerchantInfoForLoginByPhone(paramMap);
			if (merchantInfoForLoginCached == null
					|| merchantInfoForLoginCached.isEmpty()) {
				// 先检查名称是否被注册
				// 格式化$符号
				name = StringUtil.formatDollarSign(name);
				// 去空格
				name = name.trim();
				paramMap.put("name", name);
				int count = this.iMerchantInfoDao.checkNameForReg(paramMap);

				if (count == 0) {
					// 创建新商户及相关信息，并放入缓存
					Map<String, Object> merchantInfo = new HashMap<String, Object>();
					String[] provinceAndCity = BusinessUtil
							.getProvinceAndCityByIp(ip);
					merchantInfo.put("ip", ip);
					merchantInfo.put("province", provinceAndCity[0]);
					merchantInfo.put("city", provinceAndCity[1]);

					Long merchantId = IdGenerator.generateID(18);
					merchantInfo.put("merchantId", merchantId);
					merchantInfo.put("phone", phone);
					merchantInfo.put("name", name);
					merchantInfo.put("appType", appType);
					// 注册新商户
					this.iMerchantInfoDao.insertMerchantInfo(merchantInfo);
					// 保存商户默认图标
					merchantInfo.put("path",
							Constant.DEFAULT_MERCHANT_PORTRAIT_PTAH);
					this.iMerchantAttachmentDao
							.insertMerchantIcon(merchantInfo);
					// 保存商户的联系方式（默认是注册的手机号码）
					this.iMerchantContactDao.insertTelephone(merchantInfo);

					// 员工类型 1：老板
					merchantInfo.put("employeesType", 1);
					// 保存注册的手机号码,保存验证码
					merchantInfo.put("employeeKey",
							DynamicKeyGenerator.generateDynamicKey());
					this.iMerchantEmployeesDao.insertPhone(merchantInfo);
					Long employeesId = (Long) merchantInfo.get("employeesId");

					// 商户统计信息初期化
					Map<String, Object> moneyMap = commonService
							.getConfigurationInfoByKey("register_sendMoney_num");
					String money = BusinessUtil.registerGiveMoney(moneyMap);// 判断是否要注册送钱
					merchantInfo.put("orderSurplusPrice", money);
					this.iMerchantStatisticsDao
							.insertMerchantStatistics(merchantInfo);

					// 赠送员工
					Map<String, Object> employeesMap = commonService
							.getConfigurationInfoByKey("sent_employees_num");
					String employeesNum = BusinessUtil
							.registerGiveEmployeesNum(employeesMap);// 赠送员工
					Map<String, Object> employeesNumMap = new HashMap<String, Object>();
					employeesNumMap.put("merchantId", merchantId);
					employeesNumMap.put("employeeNum", employeesNum);
					iMerchantEmployeesNumApplyDao
							.setMerchantEmployeesNum(employeesNumMap);

					merchantInfoForLoginCached = new HashMap<String, Object>();
					merchantInfoForLoginCached.put("merchantId", merchantId);
					merchantInfoForLoginCached.put("employeesType", 1);
					merchantInfoForLoginCached.put("phone", phone);
					merchantInfoForLoginCached.put("employeesId", employeesId);
					commonCacheService.setObject(merchantInfoForLoginCached,
							CacheConstants.MERCHANT_INFO_FOR_LOGIN_TIMEOUT,
							CacheConstants.MERCHANT_INFO_FOR_LOGIN, phone,
							appType);

					if (StringUtil.isNotEmpty(tags)) {
						String[] tagArray = tags.split(",");
						merchantInfo.put("tagArray", tagArray);
						int checkCount = 0;
						checkCount = this.iMerchantServiceTagDao
								.checkAddMerchantServiceTag(merchantInfo);
						if (checkCount + tagArray.length <= 16) {
							this.iMerchantServiceTagDao
									.chooseServiceTagSave(merchantInfo);
						}
					}
					return new ResultJSONObject("000", "注册商户成功");
				} else {
					return new ResultJSONObject("exist_name", "商户名称已经存在");
				}
			} else {
				return new ResultJSONObject("exist_phone", "商户手机号已经存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e, "fail", "验证失败");
		}

	}
	/**
	 * 用户店铺列表
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> myMerchantList(Long userId) throws Exception{

		List<Map<String, Object>> list =(List<Map<String, Object>>)commonCacheService.getObject(CacheConstants.USER_SHOP_LIST, userId+"");
		if(list==null){
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("userId", userId);
			list = iMerchantInfoDao.myMerchantList(paramMap);
		}
		if (list != null && list.size() > 0) {	
			int temp=0;
			for (int i=0;i<list.size();i++) {
				Map<String, Object> map=list.get(i);

				Long merchantId=StringUtil.nullToLong(map.get("merchantId"));					
				Map<String,Object> merchantInfo=merchantBasicInfo(merchantId);				
				map.put("name", merchantInfo.get("name"));	
				map.put("path", merchantInfo.get("iconUrl"));		
				map.put("isOpened", 1);		
				map.put("authType", merchantInfo.get("auth"));
				map.put("vipStatus", merchantInfo.get("vipStatus"));
				map.put("catalogName", merchantInfo.get("catalogName"));
				map.put("appType", merchantInfo.get("appType"));
				map.put("merchantIdStr",""+map.get("merchantId"));
				
				String appType=StringUtil.null2Str(merchantInfo.get("appType"));
				//私人助理店铺排到最前
				if(appType.equals("srzl")){
					temp=1;
					map.put("sort",0);
				}else{
					map.put("sort",i);
				}
			}
			
			//排序
			if(temp==1){
				Collections.sort(list, new Comparator<Map<String,Object>>() {
		            public int compare(Map<String,Object> arg0, Map<String,Object> arg1) {
		                return StringUtil.nullToInteger(arg0.get("sort")).compareTo(StringUtil.nullToInteger(arg1.get("sort")));
		            }
		        });
			}
		}
		return list;
	}

	/** 商户统计信息编辑 */
	public Map<String, Object> statisticsInfoEdit(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		Map<String, Object> statisticsInfoMap = this.iMerchantStatisticsDao
				.selectStatisticsInfo(paramMap);
		if (statisticsInfoMap == null) {
			statisticsInfoMap = new HashMap<String, Object>();
		}
		// 星级和评分编辑
		int totalCountEvaluation = Integer.parseInt(statisticsInfoMap
				.get("totalCountEvaluation") == null ? "0" : statisticsInfoMap
				.get("totalCountEvaluation").toString());
		if (totalCountEvaluation == 0) {
			// 没有用户评价的时候设置默认值
			statisticsInfoMap.put("starLevel", 5);
			statisticsInfoMap.put("score", 5.0);
		} else {
			if (statisticsInfoMap != null) {
				int totalAttitudeEvaluation = Integer
						.parseInt(statisticsInfoMap
								.get("totalAttitudeEvaluation") == null ? "0"
								: statisticsInfoMap.get(
										"totalAttitudeEvaluation").toString());
				int totalQualityEvaluation = Integer.parseInt(statisticsInfoMap
						.get("totalQualityEvaluation") == null ? "0"
						: statisticsInfoMap.get("totalQualityEvaluation")
								.toString());
				int totalSpeedEvaluation = Integer.parseInt(statisticsInfoMap
						.get("totalSpeedEvaluation") == null ? "0"
						: statisticsInfoMap.get("totalSpeedEvaluation")
								.toString());
				// 总服务态度评价+总服务质量评价+总服务速度评价
				int totalEvaluation = totalAttitudeEvaluation
						+ totalQualityEvaluation + totalSpeedEvaluation;
				// 分数
				BigDecimal score = new BigDecimal(totalEvaluation).divide(
						new BigDecimal(totalCountEvaluation)
								.multiply(new BigDecimal(3)), 1,
						BigDecimal.ROUND_DOWN);
				statisticsInfoMap.put("score", score);
				// 星级
				BigDecimal starLevel = new BigDecimal(totalEvaluation).divide(
						new BigDecimal(totalCountEvaluation)
								.multiply(new BigDecimal(3)), 0,
						BigDecimal.ROUND_HALF_UP);
				statisticsInfoMap.put("starLevel", starLevel);
			} else {
				// 统计信息异常的时候
				statisticsInfoMap.put("starLevel", 5);
				statisticsInfoMap.put("score", 5.0);
			}
		}
		return statisticsInfoMap;
	}

	/**
	 * 抽取 更新用户推送信息 为函数
	 * 
	 * @param param
	 */
	private void updatePushUserDevice(Map<String, Object> param)
			throws Exception {
		// 重复登录推送
		// pushService.repeatLoginPush(param);

		// 删除已存在的推送设备
		iOrderDao.deleteUserPushByUserId(param);
		iOrderDao.deleteUserPushByClientId(param);
		// 上报推送设备信息
		iOrderDao.insertUserPush(param);
	}

	private Map<String, Object> getCatalogTreeAndService(String id) {
		if(StringUtil.isEmpty(id)){
			return null;
		}
		String key = CacheConstants.CATALOG_AND_SERVICES_FOR_MERCHANT + "_"
				+ id;
		Object cachedCatalogsTree = commonCacheService.getObject(key);
		if (cachedCatalogsTree == null) {
			List<Map<String, Object>> catalogs = iMerchantInfoDao
					.getCatalogById(Integer.parseInt(id));
			if (catalogs == null || catalogs.size() == 0) {
				return new HashMap<String, Object>();
			}
			Map<String, Object> parentCatalog = catalogs.get(0);
			getSubTree(parentCatalog, true);
			commonCacheService.setObject(parentCatalog, key);
			((GenericCacheServiceImpl) commonCacheService).setExpire(key,
					CacheConstants.CATALOG_AND_SERVICES_EXPIRTIME);
			return parentCatalog;
		} else {
			return (Map<String, Object>) cachedCatalogsTree;
		}
	}

	private Object getSubTree(Map<String, Object> catalog, boolean loadService) {
		int id = (int) catalog.get("id");
		int isLeaf = (int) catalog.get("leaf");
		List result = new ArrayList();
		if (isLeaf == 0) {
			List<Map<String, Object>> children = iMerchantInfoDao
					.getCataLogs(id);
			if (children != null && children.size() > 0) {
				for (Map<String, Object> child : children) {
					if (child == null) {
						continue;
					}
					result.add(getSubTree(child, loadService));
				}
			}
		}
		if (loadService) {
			// 加载叶子级的分类对应的服务项目列表
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("catalogIds", id);
			List<Map<String, Object>> serviceItems = iMerchantInfoDao
					.getServiceTypeByCatalogs(param);
			if (serviceItems != null && serviceItems.size() > 0) {
				for (Map<String, Object> serviceItem : serviceItems) {
					result.add(serviceItem);
				}
			}
		}
		catalog.put("children", result);
		return catalog;
	}

	/**
	 * 商户基本信息
	 */
//	@SuppressWarnings("unchecked")
//	private Map<String, Object> basicInfo(Long merchantId) throws Exception {
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("merchantId", merchantId);
//		// 先从缓存中读取该商铺的基本信息
//		Map<String, Object> info = (Map<String, Object>) commonCacheService
//				.getObject(CacheConstants.MERCHANT_BASIC_INFO,
//						StringUtil.null2Str(merchantId));
////		 System.out.println("数基本信息缓存"+JSONObject.toJSONString(info));
//		if (info == null) {
//			// 缓存中商铺信息不存在，从db中读取
//			// **************************将原本的商铺（名字、省、市、经纬度、地址、电话、微官网、详细介绍、图标、vip等级）基本数据查询合并一起查询***************************
//			info = this.iMerchantInfoDao.selectMerchantInfo(paramMap);
////			 System.out.println("数基本信息数据库"+JSONObject.toJSONString(info));
//			if (info == null) {
//				return null;
//			}
//			String iconUrl = StringUtil.null2Str(info.get("iconUrl"));
//			if (!StringUtil.isNullStr(iconUrl)) {
//				iconUrl = BusinessUtil.disposeImagePath(iconUrl);
//			}
//			info.put("iconUrl", iconUrl);
//			// 是否有认证 0-没有 1-企业认证 2-个人认证
//			Map<String, Object> authMap = this.iMerchantAuthDao
//					.selectAuthType(paramMap);
//			if (authMap != null) {
//				String authType = authMap.get("authType") == null ? "0"
//						: authMap.get("authType").toString();
//				info.put("auth", authType);
//			} else {
//				info.put("auth", 0);
//			}
//			// 0未知 1企业 2自由（个性）服务
//			String appType = (String) info.get("appType");
//			if (appType == null) {
//				info.put("merchantType", 0);
//			} else if (!appType.equals("gxfw")) {
//				info.put("merchantType", 1);
//			} else {
//				info.put("merchantType", 2);
//			}
//			// 更新缓存中的商铺信息(暂定一天未访问则移除缓存)
//			commonCacheService.setObject(info,
//					CacheConstants.MERCHANT_BASIC_INFO_TIMEOUT,
//					CacheConstants.MERCHANT_BASIC_INFO,
//					StringUtil.null2Str(merchantId));
//		}
//		return info;
//	}
	/**
	 * 商户基本信息
	 * @param merchantId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> merchantBasicInfo(Long merchantId) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		// 先从缓存中读取该商铺的基本信息
		Map<String, Object> info = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,
				StringUtil.null2Str(merchantId));
		if (info == null) {		
			info = this.iMerchantInfoDao.selectMerchantInfo(paramMap);
			if (info == null) {
				return null;
			}
			
			//头像路径格式化
			String iconUrl = StringUtil.null2Str(info.get("iconUrl"));
			if (!StringUtil.isNullStr(iconUrl)) {
				iconUrl = BusinessUtil.disposeImagePath(iconUrl);
			}
			info.put("iconUrl", iconUrl);
			
			// 是否有认证 0-没有 1-企业认证 2-个人认证
			Map<String, Object> authMap = this.iMerchantAuthDao.selectAuthType(paramMap);
			if (authMap != null) {
				String authType = authMap.get("authType") == null ? "0" : authMap.get("authType").toString();
				info.put("auth", authType);
				info.put("authStatus", 1);
			} else {
				info.put("auth", 0);
				info.put("authStatus", 0);
			}

			//补齐VIP 标识
			info.put("vipStatus", -1);
			List<RuleConfig> rulConfigs=incService.getRuleConfig(merchantId);
			if (rulConfigs!=null){
				if (rulConfigs.get(0).isVipMerchantOrder()){
					info.put("vipStatus", 2);
				}
			}
			if(StringUtil.isNotEmpty(info.get("vipBackgroundUrl"))){
				info.put("vipBackgroundUrl", BusinessUtil.disposeImagePath(info.get("vipBackgroundUrl").toString()));
			}else{
				if("2".equals(info.get("vipStatus").toString())){//是vip如果没有背景模板，给默认值
					paramMap.put("isDefault", 1);
					List<Map<String, Object>> vipBackgroundUrlList = this.iMerchantInfoDao.selectVipBackgroundUrlList(paramMap);
					if(vipBackgroundUrlList!=null && vipBackgroundUrlList.size()!=0){
						Map<String, Object> vipMap = vipBackgroundUrlList.get(0);
						info.put("vipBackgroundUrl", BusinessUtil.disposeImagePath(vipMap.get("image").toString()));
					}
				}else{
					info.put("vipBackgroundUrl", "");
				}
			}
			// 0未知 1企业 2自由（个性）服务
			String appType = StringUtil.null2Str(info.get("appType"));
			if (appType == null) {
				info.put("merchantType", 0);
			} else if (!appType.equals("gxfw")) {
				info.put("merchantType", 1);
			} else {
				info.put("merchantType", 2);
			}		
			
			//相片数量
			int photosCount=this.iMerchantPhotoDao.selectPhotoTotal(paramMap);
			info.put("photosCount", photosCount);
			
			//前四相片
			paramMap.put("num", 4);
			Map<String, Object> topPhotosInfo = this.iMerchantPhotoDao.selectLastPhotos(paramMap);
			BusinessUtil.disposeManyPath(topPhotosInfo, "path");
			info.put("topPhotosInfo", topPhotosInfo.get("path"));
			
			//商品数量
			paramMap.put("isAll", 0);
			int goodsCount=this.iMerchantGoodsDao.selectGoodsCount(paramMap);
			info.put("goodsCount", goodsCount);
			
			//前三商品
			paramMap.put("num", 3);
			Map<String, Object> topGoodsInfo = iMerchantGoodsDao.selectLastGoodsPic(paramMap);
			BusinessUtil.disposeManyPath(topGoodsInfo, "path");
			info.put("topGoodsInfo", topGoodsInfo.get("path"));
			
			info.put("last3GoodsInfo", last3GoodsInfo(merchantId));
			
			//商户粉丝数
			int fansCount= this.iMerchantInfoDao.selectCollectionNum(paramMap);
			info.put("fansCount", fansCount);
			
			//魅力值
			int charmValue = this.charmValueEdit(merchantId);
			info.put("charmValue", charmValue);

			//剪裁次数
			Integer cuttingNum = this.iMerchantInfoDao.selectCuttingNum(paramMap);
			if (cuttingNum == null) {
				cuttingNum = 0;
			}
			info.put("cuttingNum", cuttingNum);

			// 接单计划 0-未设置1-已设置
			Integer alreadySetOrderPlan = this.iMerchantInfoDao
					.selectAlreadySetOrderPlan(paramMap);
			if (alreadySetOrderPlan == null) {
				info.put("alreadySetOrderPlan", 0);
			} else {
				info.put("alreadySetOrderPlan", 1);
			}

			//服务数量(实时变化的)
			int serviceNum = this.iMerchantServiceTypeDao
					.selectMerchantServiceNum(paramMap);
			info.put("serviceNum", serviceNum);
			
			//商户服务类型
			String merchantServiceTypeNames=iMerchantServiceTypeDao.getMerchantServiceTypeNames(paramMap);
			info.put("merchantServiceTypeNames", merchantServiceTypeNames);
			
			//商户的评分和星级
			BigDecimal starLevel=new BigDecimal("5");
			BigDecimal score=new BigDecimal("5.0");
			String grabFrequency = "0";
			String totalIncomePrice = "0";
			Map<String, Object> statisticsInfoMap = this.evaluationDao.selectStatisticsInfo(paramMap);
			if (statisticsInfoMap != null) {				
				// 星级和评分编辑
				int totalCountEvaluation = StringUtil.nullToInteger(statisticsInfoMap.get("totalCountEvaluation"));
				if (totalCountEvaluation != 0) {					
					int totalAttitudeEvaluation = StringUtil.nullToInteger(statisticsInfoMap.get("totalAttitudeEvaluation"));
					int totalQualityEvaluation = StringUtil.nullToInteger(statisticsInfoMap.get("totalQualityEvaluation"));
					int totalSpeedEvaluation = StringUtil.nullToInteger(statisticsInfoMap.get("totalSpeedEvaluation"));
					// 总服务态度评价+总服务质量评价+总服务速度评价
					int totalEvaluation = totalAttitudeEvaluation + totalQualityEvaluation + totalSpeedEvaluation;
					// 分数
					score = new BigDecimal(totalEvaluation).divide(new BigDecimal(totalCountEvaluation).multiply(new BigDecimal(3)), 1,BigDecimal.ROUND_DOWN);
					// 星级
					starLevel = new BigDecimal(totalEvaluation).divide(new BigDecimal(totalCountEvaluation).multiply(new BigDecimal(3)), 0,BigDecimal.ROUND_HALF_UP);				
				}
				grabFrequency = statisticsInfoMap.get("grabFrequency").toString();
				totalIncomePrice = statisticsInfoMap.get("totalIncomePrice").toString();
			}
			info.put("starLevel", starLevel);
			info.put("score", score);
			info.put("grabFrequency", grabFrequency);
			info.put("totalIncomePrice", grabFrequency);
			
			//商户的评价数量
			int evaluationCount = this.evaluationDao.getMerchantEvaluationNum(merchantId);
			info.put("evaluationCount", evaluationCount);
			
			//商户的最新N条评价
			paramMap.put("num", 2);
			List<Map<String, Object>> topEvaluation = this.evaluationDao.getTextEvaluationTopN(paramMap);
			info.put("topEvaluation", topEvaluation);
			
//			//商户是否被用户收藏（订单详情查看商户信息） 
//			int isCollection = this.userMerchantDao.checkCollectionMerchant(paramMap);
//			if (isCollection > 0) {
//				isCollection = 1;
//			} else {
//				isCollection = 0;
//			}
//			info.put("isCollection", isCollection);
			
			// 更新缓存中的商铺信息(暂定一天未访问则移除缓存)
			commonCacheService.setObject(info,CacheConstants.MERCHANT_BASIC_INFO_TIMEOUT,CacheConstants.MERCHANT_BASIC_INFO,
					StringUtil.null2Str(merchantId));
		}
		return info;
	}
	public String getCatalogIdByAppType(String appType) {
		// 根据appType去查询catalogId
		int catalogId = 0;
		List<Map<String, Object>> catalogList = commonService
				.getCatalogsByAppType("'" + appType + "'");
		if (catalogList != null && catalogList.size() > 0) {
			catalogId = StringUtil.nullToInteger(catalogList.get(0).get("id"));
		}
		if (catalogId == 0) {
			logger.info("代金券列表：根据appType没有查找到catalogId");
			return "0";
		}
		// 根据catalogId查询所有原子性服务项目
		String catalogIds = "0,";
		List<Map<String, Object>> serviceTypeList = commonService
				.getServiceTypesByCatalogId(catalogId);
		for (Map<String, Object> map : serviceTypeList) {
			int id = StringUtil.nullToInteger(map.get("id"));
			catalogIds += id + ",";
		}
		if (!catalogIds.equals("")) {
			catalogIds = catalogIds.substring(0, catalogIds.length() - 1);
		}
		return catalogIds;
	}

	/** 添加代金券（多条） 用于接单计划 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject insertMerchantVouchersPermissions(Long merchantId,
			String vouchersIds, String counts, String cutoffTimes,
			Integer planNum) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		Integer orderPlanNum = this.iMerchantInfoDao
				.selectAlreadySetOrderPlan(paramMap);
		if (orderPlanNum != null) {
			return new ResultJSONObject("order_plan_repeat", "你已设置过订单计划，请勿重复设置");
		}
		JSONObject jsonObject = null;

		String[] vouchersIdArr = vouchersIds.split(",");
		String[] countArr = counts.split(",");
		String[] cutoffTimeArr = cutoffTimes.split(",");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Map<String, Object>> vouchersList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < vouchersIdArr.length; i++) {
			if ("0".equals(countArr[i])) {
				continue;
			}
			if (countArr[i].compareTo("999") > 0) {
				return new ResultJSONObject("vouchers_count_beyond",
						"代金券数量超过最大值999");
			}
			Map<String, Object> vouchersParamMap = new HashMap<String, Object>();
			// 代金券ID
			// planNum+=StringUtil.nullToInteger(vouchersIdArr[i]);
			vouchersParamMap.put("vouchersId", vouchersIdArr[i]);
			vouchersParamMap.put("merchantId", merchantId);
			// 数量
			vouchersParamMap.put("count", countArr[i]);
			// 截止时间（设置的日期的当天23:59:59）
			Date mvCutoffTime = null;
			try {
				mvCutoffTime = DateUtils.addSeconds(
						DateUtils.addDays(sdf.parse(cutoffTimeArr[i]), 1), -1);
			} catch (ParseException e) {
				throw new ApplicationException(e, "cutoffTime_error", "截止时间错误");
			}
			vouchersParamMap.put("cutoffTime", mvCutoffTime);
			vouchersList.add(vouchersParamMap);
		}

		try {
			this.iMerchantVouchersPermissionsDao
					.insertMerchantVouchersPermissionsBatch(vouchersList);
			jsonObject = new ResultJSONObject("000", "代金券添加成功");
			// 目标接单数量
			paramMap.put("planNum", planNum);
			this.iMerchantInfoDao.insertAlreadySetOrderPlanMerchant(paramMap);
			// 删除代金券缓存
			commonCacheService.delObjectContainsKey(
					CacheConstants.MERCHANT_VOUCHERSINFO + "_"
							+ StringUtil.null2Str(merchantId), true);
		} catch (Exception ex) {
			logger.error("代金券添加失败", ex);
			throw new ApplicationException(ex, "vouchers_add_failure",
					"代金券添加失败");
		}
		return jsonObject;
	}

	/** 接单计划 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject orderPlan(Long merchantId) {
		JSONObject jsonObject = null;
		Map<String, Object> orderPlan = new HashMap<String, Object>();

		// 统计信息
		Map<String, Object> map = statisticsInfoEdit(merchantId);
		String grabFrequency = String.valueOf(map.get("grabFrequency"));
		String serviceFrequency = String.valueOf(map.get("serviceFrequency"));

		// 抢单次数
		orderPlan.put("grabFrequency", grabFrequency);
		// 服务次数
		orderPlan.put("serviceFrequency", serviceFrequency);
		// 成交率 （服务次数除以抢单次数）
		BigDecimal dealRate = null;
		if ("0".equals(grabFrequency)) {
			dealRate = BigDecimal.ZERO;
			orderPlan.put("dealRate", "0%");
		} else {
			dealRate = new BigDecimal(serviceFrequency).divide(new BigDecimal(
					grabFrequency), 2, BigDecimal.ROUND_HALF_UP);
			orderPlan.put("dealRate", dealRate.multiply(new BigDecimal(100))
					.intValue() + "%");
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		Integer orderPlanNum = this.iMerchantInfoDao
				.selectAlreadySetOrderPlan(paramMap);
		// 订单计划数目
		orderPlan.put("orderPlanNum", StringUtil.nullToInteger(orderPlanNum));

		long surplusDays = 0;
		try {
			surplusDays = commonService.calcThisYearSurplusDays();
		} catch (Exception e) {
			jsonObject = new ResultJSONObject("calc_fail", "计算今年剩余天数错误");
		}
		// 今年剩余天数
		orderPlan.put("surplusDays", surplusDays);
		// 达成率 （服务次数除以接单计划数目）
		BigDecimal achievingRate = new BigDecimal(serviceFrequency).divide(
				new BigDecimal(orderPlanNum), 2, BigDecimal.ROUND_HALF_UP);
		orderPlan.put("achievingRate",
				achievingRate.multiply(new BigDecimal(100)).intValue() + "%");

		jsonObject = new ResultJSONObject("000", "接单计划展示信息查询成功");
		StringBuffer sb = new StringBuffer("截止到当前店铺总抢单").append(grabFrequency)
				.append("次，达成交易").append(serviceFrequency).append("单，抢单率")
				.append(dealRate.multiply(new BigDecimal(100)).intValue())
				.append("%，低于行业平均水平，继续加油哦~");
		orderPlan.put("describe", sb);
		jsonObject.put("orderPlan", orderPlan);
		return jsonObject;
	}

	/** 商户最新 n张商品信息 */
	private List<Map<String, Object>> lastnGoodsInfo(Long merchantId,
			int goodsNum) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		paramMap.put("goodsNum", goodsNum);
		List<Map<String, Object>> lastnGoodsInfo = this.iMerchantGoodsDao
				.selectLastnGoodsInfo(paramMap);
		BusinessUtil.disposeManyPath(lastnGoodsInfo, "goodsPictureUrl");
		return lastnGoodsInfo;
	}

	// 从缓存获取相册、商品，20160905商铺的缓存集中到一个，其他的清除掉 cuijiajun
//	private Map<String, Object> getCachedMerchantOutLine(Long merchantId,
//			String appType) {
//		Object cached = commonCacheService.getObject(
//				CacheConstants.MERCHANT_OUTLINE, merchantId.toString());
//		if (cached == null) {
//			Map<String, Object> merchantOutLineMap = new HashMap<String, Object>();
//
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap.put("merchantId", merchantId);
//
//			// 获取 相片总数
//			Integer photoTotal = this.iMerchantPhotoDao
//					.selectPhotoTotal(paramMap);
//
//			String lastPhotos = last4Photo(merchantId);
//			merchantOutLineMap.put("photoTotal", photoTotal);
//			merchantOutLineMap.put("lastPhotos", lastPhotos);
//
//			if (!appType.equals("gxfw")) {
//				// 获取商品总数
//				int goodsTotal = this.iMerchantGoodsDao
//						.selectGoodsCount(paramMap);
//				merchantOutLineMap.put("goodsTotal", goodsTotal);
//
//				List<Map<String, Object>> lastGoodsInfos = lastnGoodsInfo(
//						merchantId, 3);
//				merchantOutLineMap.put("lastGoodsInfo", lastGoodsInfos);
//			}
//
//			commonCacheService.setObject(merchantOutLineMap,
//					CacheConstants.MERCHANT_OUTLINE_TIMEOUT,
//					CacheConstants.MERCHANT_OUTLINE, merchantId.toString());
//
//			return merchantOutLineMap;
//
//		}
//		return (Map<String, Object>) cached;
//	}

	// 从缓存获取商店等级，星级，评价信息
	private Map<String, Object> getCachedMerchantEstimate(Long merchantId) {
		Object cached = commonCacheService.getObject(
				CacheConstants.MERCHANT_ESTIMATE, merchantId.toString());
		if (cached == null) {
			Map<String, Object> estimate = statisticsInfoEdit(merchantId);
			Map<String, Object> param = new HashMap<String, Object>();

			param.put("merchantId", merchantId);
			List<Map<String, Object>> userEvaluation = this.evaluationDao
					.getTextEvaluationTop2(param);
			for (Map<String, Object> evaluationMap : userEvaluation) {
				BusinessUtil.disposePath(evaluationMap, "path");
			}

			estimate.put("userEvaluation", userEvaluation);
			commonCacheService.setObject(estimate,
					CacheConstants.MERCHANT_ESTIMATE_TIMEOUT,
					CacheConstants.MERCHANT_ESTIMATE, merchantId.toString());

			return estimate;
		}
		return (Map<String, Object>) cached;

	}

	// 从缓存获取粉丝数
//	private int getFans(Long merchantId) {
//		Object cachedFansNum = commonCacheService.getObject(
//				CacheConstants.MERCHANT_FANS, merchantId.toString());
//		if (cachedFansNum == null) {
//			// 粉丝数
//			Map<String, Object> param = new HashMap<String, Object>();
//			param.put("merchantId", merchantId);
//			Integer collectionNum = this.iMerchantInfoDao
//					.selectCollectionNum(param);
//			commonCacheService.setObject(collectionNum,
//					CacheConstants.MERCHANT_FANS_TIMEOUT,
//					CacheConstants.MERCHANT_FANS, merchantId.toString());
//			return collectionNum;
//		}
//		return (int) cachedFansNum;
//	}

	/** 商户编辑页信息查询 */
	@Override
	public JSONObject merchantEditPageInfo(Long merchantId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> info = merchantBasicInfo(merchantId);
		if (info == null) {
			return new ResultJSONObject("merchant_info_null", "店铺信息为空");
		}
		
		Map<String, Object> merchantInfo = new HashMap<String, Object>();
		merchantInfo.put("name", info.get("name"));
		merchantInfo.put("iconUrl", info.get("iconUrl"));
		merchantInfo.put("phone", info.get("phone"));
		merchantInfo.put("longitude", info.get("longitude"));
		merchantInfo.put("latitude", info.get("latitude"));
        merchantInfo.put("mapType", info.get("mapType"));
		merchantInfo.put("locationAddress", info.get("locationAddress"));
		merchantInfo.put("detailAddress", info.get("detailAddress"));
		merchantInfo.put("detail", info.get("detail"));
		merchantInfo.put("microWebsiteUrl", info.get("microWebsiteUrl"));
		merchantInfo.put("isPrivateAssistant", info.get("isPrivateAssistant"));
		merchantInfo.put("vipStatus", info.get("vipStatus"));
		merchantInfo.put("vipLevel", info.get("vipLevel"));
		merchantInfo.put("vipBackgroundUrl", info.get("vipBackgroundUrl"));
		
//		从info获取，以前的缓存删除 2016.09.05 cuijiajun
//		Map<String, Object> paramMap = new HashMap<String, Object>();
//		paramMap.put("merchantId", merchantId);
//		Map<String, Object> authMap = this.iMerchantAuthDao
//				.selectMerchantAuth(paramMap);
//		if (authMap != null) {
//			String authType = authMap.get("authType") == null ? "0" : authMap
//					.get("authType").toString();
//			String authStatus = authMap.get("authStatus") == null ? "0"
//					: authMap.get("authStatus").toString();
//			merchantInfo.put("authStatus", authStatus);
//			merchantInfo.put("auth", authType);
//		} else {
//			merchantInfo.put("auth", 0);
//			merchantInfo.put("authStatus", 0);
//		}
		merchantInfo.put("auth", info.get("auth"));
		merchantInfo.put("authStatus", info.get("authStatus"));

//		int fansCount=iMerchantInfoDao.selectCollectionNum(paramMap);从info获取，以前的缓存删除 2016.09.05 cuijiajun
	
		merchantInfo.put("fansCount", info.get("fansCount"));
		jsonObject.put("merchantInfo", merchantInfo);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "商户编辑页信息查询成功");
		return jsonObject;
	}

	@Override
	public JSONObject getCollections(Long merchantId, int pageNo) {
		JSONObject jsonObject = new ResultJSONObject("000", "查询商户收藏列表成功");

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("merchantId", merchantId);
		Integer collectionNum = this.iMerchantInfoDao
				.selectCollectionNum(param);
		Integer totalPage = (collectionNum + Constant.PAGESIZE - 1)
				/ Constant.PAGESIZE;
		param.put("startNo", pageNo * Constant.PAGESIZE);
		param.put("pageSize", Constant.PAGESIZE);
		List<Map<String, Object>> collections = iMerchantInfoDao
				.getCollections(param);
		if (collections != null && collections.size() > 0) {
			for (Map<String, Object> collection : collections) {
				if (StringUtil.isEmpty(collection.get("path"))) {
					collection.put("path", Constant.DEFAULT_USER_PORTRAIT_PTAH);
				}

				String phone = collection.get("phone").toString();
				if (phone != null && phone.length() > 0) {
					phone = phone.substring(0, 3) + "****"
							+ phone.substring(7, phone.length());
					collection.put("phone", phone);

				}

			}

			BusinessUtil.disposeManyPath(collections, "path");
		}
		jsonObject.put("totalPage", totalPage);
		jsonObject.put("collections", collections);

		return jsonObject;
	}
	
	// 更新位置索引----剥离到单独的线程
	class MerchantElasticIndexRunner implements Runnable {
		private IElasticSearchService elasticSearchService;
		private String merchantId;
		private String name;
		private Double latitude;
		private Double longitude;

		MerchantElasticIndexRunner(IElasticSearchService elasticSearchService,
				String merchantId, String name, Double latitude,
				Double longitude) {
			this.elasticSearchService = elasticSearchService;
			this.merchantId = merchantId;
			this.latitude = latitude;
			this.longitude = longitude;
			this.name = name;
		}

		@Override
		public void run() {
			try {
				elasticSearchService.updateDocument(merchantId.toString(),
						name, latitude, longitude);
			} catch (Exception e) {
				logger.error("位置索引更新失败:" + e.getMessage(), e);
			}
		}

	}


	@Override
	public JSONObject selectMerchantAccountStatistics(Map<String,Object> params) throws Exception {
		JSONObject jsonObject = null;
		Map<String,Object> merchantAccountMoneyRes =  iMerchantStatisticsDao.selectMerchantIncomeIndex(params);
		if(null != merchantAccountMoneyRes){
			jsonObject = new ResultJSONObject("000","商户收入首页账单查询成功");
			jsonObject.put("monthTotalAmount", merchantAccountMoneyRes.get("monthTotalAmount"));
			jsonObject.put("yesterdayTotalAmount", merchantAccountMoneyRes.get("yesterdayTotalAmount"));
			jsonObject.put("orderSurplusPrice", merchantAccountMoneyRes.get("orderSurplusPrice"));
			jsonObject.put("payApplyStatus", merchantAccountMoneyRes.get("payApplyStatus"));//充值金额状态，1-可充值、0-待确认
		}else{
			jsonObject = new ResultJSONObject("query_merchant_account_statis_failure","商户收入首页账单查询失败");
		}
		return jsonObject;
	}

	@Override
	public int selectMerchantPayApplyStatus(Map paramMap) {
		return iMerchantStatisticsDao.selectMerchantPayApplyStatus(paramMap);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public JSONObject changeGoodsStatus(String appType, Long merchantId,
			int targetStatus, String goodsId) throws ApplicationException,
			Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("targetStatus", targetStatus);
		
		//上架更新产品版本号：如果有快照（被是用过）那么更新版本号，如果没有快照（未被使用）不用更新版本号，防止产生垃圾数据
//		if(targetStatus == 1){
//			iMerchantGoodsDao.updateGoodsVersion(paramMap);
//		}
		int count = 0;
		if(StringUtil.isNotEmpty(goodsId)){
			String[] goodsIds = goodsId.split(",");
			List list = new ArrayList();
			for(String goodsId2 : goodsIds){
				list.add(goodsId2.trim());
			}
			paramMap.put("goodsIds", list);
			count = iMerchantGoodsDao.changeGoodsStatus(paramMap);
		}
		String result = null;
		if(targetStatus==0){
			result = "上架";
		}else{
			result = "下架";
		}
		//强制更新商店概要缓存 
		commonCacheService.deleteObject(CacheConstants.MERCHANT_OUTLINE,
				merchantId.toString());
		//更新店铺缓存
		Map<String, Object> newValue = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		//商品数量
		paramMap.put("isAll", 0);
		int goodsCount=this.iMerchantGoodsDao.selectGoodsCount(paramMap);
		newValue.put("goodsCount", goodsCount);
		//前三商品
		paramMap.put("num", 3);
		Map<String, Object> topGoodsInfo = iMerchantGoodsDao.selectLastGoodsPic(paramMap);
		BusinessUtil.disposeManyPath(topGoodsInfo, "path");
		newValue.put("topGoodsInfo", topGoodsInfo.get("path"));
					  
		newValue.put("last3GoodsInfo", last3GoodsInfo(merchantId));
		this.updateMerchantCache(merchantId, newValue);
		
		jsonObject = new ResultJSONObject("000",count+"件商品"+result+"成功");
		return jsonObject;
	}

	@Override
	public JSONObject selectGoodsDetail(String appType, Long merchantId,
			String goodsId) throws ApplicationException, Exception {
		JSONObject jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goodsId", goodsId);
		Map<String,Object> goodsInfo = iMerchantGoodsDao.selectGoodsDetail(paramMap);
		List<Map<String, Object>> paths = iMerchantGoodsDao.selectGoodsPic(paramMap);
		jsonObject = new ResultJSONObject("000","商品详情查询成功");
		for (Map<String, Object> goodsPicMap : paths) {
			// 商品图片路径的补全
			BusinessUtil.disposePath(goodsPicMap, "path");
		}
		BusinessUtil.disposePath(goodsInfo, "goodsPictureUrl");
		goodsInfo.put("paths", paths);
		jsonObject.put("goodsInfo", goodsInfo);
		
		return jsonObject;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Long createGoodsHistory(Long goodsId, int version)
			throws ApplicationException, Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goodsId", goodsId);
		paramMap.put("version", version);
		//查询此版本商品是否存在快照
		Long goodsHistoryId = iMerchantGoodsDao.findeGoodsHistoryByGoodsId(paramMap);
		//不存在快照，生成快照
		if(goodsHistoryId == null || goodsHistoryId == 0){
			iMerchantGoodsDao.insertGoodsHistory(paramMap);
			goodsHistoryId = Long.parseLong(paramMap.get("goodsHistoryId").toString());
			iMerchantGoodsDao.insertGoodsHistoryPic(paramMap);
		}
		return goodsHistoryId;
	}

	@Override
	public JSONObject selectGoodsHistoryInfo(String goodsHistoryId,String goodsId,String version)
			throws ApplicationException, Exception {
		JSONObject jsonObject = new ResultJSONObject("000","查询商品快照信息成功");
		Map<String,Object> paramMap = new HashMap<String, Object>();
		paramMap.put("goodsHistoryId", goodsHistoryId);
		paramMap.put("goodsId", goodsId);
		paramMap.put("version", version);
		Map historyInfo = iMerchantGoodsDao.selectGoodsHistoryInfo(paramMap);
		if(historyInfo == null || historyInfo.size() == 0){
			throw new ApplicationException("select_goods_history_info_service_exception","查询信息失败，未找到商品快照信息");
		}
		if(goodsHistoryId == null || "0".equals(goodsHistoryId)){
			paramMap.put("goodsHistoryId", historyInfo.get("goodsHistoryId"));
		}
		List<Map<String,Object>> paths = iMerchantGoodsDao.selectGoodsHistoryPic(paramMap);
		for (Map<String, Object> goodsPicMap : paths) {
			// 商品图片路径的补全
			BusinessUtil.disposePath(goodsPicMap, "path");
		}
		BusinessUtil.disposePath(historyInfo, "goodsPictureUrl");
		historyInfo.put("paths", paths);
		jsonObject.put("goodsHistoryInfo",historyInfo);
		return jsonObject;
	}

    /**
     * 开通增值服务
     */
    @Transactional(rollbackFor = Exception.class)
    public JSONObject openIncreaseService(Map<String, Object> params) throws Exception {
        JSONObject jsonObject = new ResultJSONObject();
        try {
            int pkgId = StringUtil.nullToInteger(params.get("pkgId"));
            Map<String, Object> pkgMap = getPkgInfoById(pkgId);
            if (pkgId==0){
            	        //兼容0831前发布出去的包 购买或查看VIP 增值服务。
            			pkgId =  StringUtil.nullToInteger(pkgMap.get("id"));
            			params.put("pkgId", pkgId);
            }
            
            Integer effictive = iMerchantInfoDao.getEffictiveByPkgId(pkgId); // DB中的有效期为null表示永久
            int payType = StringUtil.nullToInteger(params.get("payType"));
            Date payTime=null;
            if(1==payType){
                payTime = DateUtil.parseDate(DATE_TIME_PATTERN, null2Str(params.get("payTime")));
            }else{
                payTime = DateUtil.parseDate(DATE_TIME_NO_SPACE_PATTERN, null2Str(params.get("payTime")));
            }
			
            Date date = null == payTime ? new Date() : payTime;
            params.put("effictiveTime", date);
            if (null != effictive) {
                params.put("effictive", getDateTimeByMinuts(formatDate("yyyy-MM-dd HH:mm:ss", date), effictive * 24 * 60)); // 不插入lose_effictive_time也表示永久
            }
            // 重复支付（根据orderNo判断是否存在）
            int result =iMerchantInfoDao.checkIsOpenIncreaseServiceByInnerTradeNo(params);
            
            
                // 不从APP端传userId（支付宝订单号有最大长度60的限制），改成从bossInfo中获取，所以将bossInfo提前
            	Map<String, Object> bossInfo = merchantPlanDao.getBossIdByMerchant(nullToLong(params.get("merchantId")));
                if (null != bossInfo) {
                	params.put("userId", bossInfo.get("user_id"));
                    params.put("phone", bossInfo.get("phone"));
                }
            	int returnId =0;
            	if (result==0) {
            		returnId=iMerchantInfoDao.addIncService(params);
            	}else{
            		returnId=iMerchantInfoDao.confirmIncServiceBuyRecord(params);
            	}
            	
				Long merchantId = nullToLong(params.get("merchantId"));
                Map<String, Object> merchantMap = selectMerchantBasicInfo(merchantId);

                params.put("inviteCode", params.get("inviteCode"));//私人助理服务码，推送时需要
                params.put("merchantType", merchantMap.get("merchantType"));//店铺类型 2-个性服务，其他，商铺，推送时需要
                
            	List<Map<String,Object>> itemRules=iMerchantInfoDao.pkgRuleItem(pkgId);
				String desc = itemRules.get(0).get("name").toString();
            	StringBuffer msg = new StringBuffer("你已成功开通").append(desc).append("服务");
                if (1 == returnId) {
                    if (itemRules != null && itemRules.size() > 0) {
                        for (Map<String, Object> itemRule : itemRules) {
                            if (itemRule.get("ruleCode").equals(this.GRAP_MOENY_RULE)) {
                                //处理抢单金
                                dealGrapMoney(merchantId, itemRule);
                            } else if (itemRule.get("ruleCode").equals(this.VEHICLE_INSURANCE_RULE)) {
                                //处理阳光车险规则
                                // modifyMsg(msg, itemRule);

								// 阳光车险MQ
								sendVehicleInsuranceMQ(params, desc, pkgMap, date, null2Str(itemRule.get("ruleCode")), null2Str(itemRule.get("ruleValue")), merchantMap);
                            } else if (itemRule.get("ruleCode").equals(this.VIP_TAG)) {
								//含VIP 标签的，优先推送，更新商户VIP标志位
								updateMerchantVipStatus(merchantId);
							} else if (PRIVATE_CONSULTANT.equals(itemRule.get("ruleCode"))) { // 私人顾问
								// 购买服务包含私人助理的商户发MQ消息
								if (IS_SEND_MQ_TO_C_PLAN_WITH_INCLUDE_CONSULTANT) {
									sendConsultantMQ(pkgId, params, pkgMap, bossInfo, date);
								}
                            } else {
                            	// 其它规则
                            }
                        }
                    }

                    // 开通增值服务后，添加提醒消息
                    Map<String, Object> messageMap = new HashMap<String,Object>();
                    messageMap.put("messageId", 0);
                    messageMap.put("messageType", 1);
                    messageMap.put("customerType", 1);
                    messageMap.put("customerId", params.get("merchantId"));
                    messageMap.put("title", "增值服务开通");
                    messageMap.put("content", msg.toString());
                    messageCenterService.saveCustomerMessageCenter(messageMap);

                    //发送短信
                    SmsUtil.asyncSendMsg(bossInfo.get("phone").toString(), msg.toString());
                    //SmsUtil.sendSms(bossInfo.get("phone").toString(), msg.toString());
                    
                    // 删除开通记录的缓存
                    commonCacheService.deleteObject(CacheConstants.VALUE_ADD_SERVICE, StringUtil.null2Str(nullToLong(params.get("merchantId"))));
                    commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
                    // 开通增值服务后给C_PLAN发MQ消息
                    if (IS_SEND_MQ_TO_C_PLAN_WITH_OPEN_INCREASE_SERVICE) {
                        sendOpenIncreaseServiceMQ(pkgId, params, pkgMap, bossInfo, date);
                    }
                    
                    jsonObject.put("resultCode", "000");
                    jsonObject.put("message", "开通增值服务成功");
                } else {
					jsonObject.put("resultCode", "001");
					jsonObject.put("message", "开通增值服务失败");
                }
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
            throw new RuntimeException("增值服务购买失败",e);
        }
        return jsonObject;
    }

    //设置商户VIP标志
    private void updateMerchantVipStatus(Long merchantId) {
    	iMerchantInfoDao.updateMerchantVipPushStauts(merchantId);
    	Map<String, Object> info = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,
    	        StringUtil.null2Str(merchantId));
    	if (info!=null && info.size()>1){
    		 info.put("vipStatus", 2);
    		 commonCacheService.setObject(info,CacheConstants.MERCHANT_BASIC_INFO_TIMEOUT,CacheConstants.MERCHANT_BASIC_INFO,
    		          StringUtil.null2Str(merchantId));
    	}
		
	}

	private void sendVehicleInsuranceMQ(Map<String, Object> params, String desc, Map<String, Object> pkgMap, Date date, String ruleCode, String ruleValue, Map<String, Object> merchantMap) {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", params.get("userId")); // 用户ID
        map.put("phone", params.get("phone")); // 用户id关联的手机号
        map.put("origin_desc", new StringBuffer().append(merchantMap.get("name")).append("店铺购买").append(desc)); // 券来源描述，按需求进行拼装
        map.put("origin_id", params.get("id")); // 来源号：基于来源类型 如 商户购买增值服务，该值可记录订单Id
		map.put("origin_id2", pkgMap.get("serviceName")); // 来源号备用字段，备用,商户购买增值服务服务包所属服务service_name
		map.put("origin_id3", pkgMap.get("serviceKey")); // 来源号备用字段，备用,商户购买增值服务服务包所属服务service_key
        map.put("origin_type", 1); // 来源类型 1:商户购买增值服务 2.用户推荐
        map.put("start_time", formatDate("yyyy-MM-dd", (Date) params.get("effictiveTime"))); // 开始时间 yyyy-MM-dd
        if (null != params.get("effictive")) {
            map.put("end_time", formatDate("yyyy-MM-dd", convertStringToDate(null2Str(params.get("effictive"))))); // 截止时间 yyyy-MM-dd
        } else {
            map.put("end_time", formatDate("yyyy-MM-dd", convertStringToDate(null2Str("2099-12-31"))));
        }
		map.put("vou_code", ruleCode + "_" + ruleValue);
		map.put("vou_no", MD5Util.MD5_32(ruleCode + "_" + ruleValue + params.get("userId") + System.currentTimeMillis() + (int) (Math.random() * 900 + 100))); // 券编号 券唯一标识（小写 md5(券编码s+user_id+时间戳+3位随机数(001))生成）
        map.put("status", 0); // 券状态 0 未使用 1：已使用 -1 已过期
        map.put("create_time", formatDate(DATE_TIME_PATTERN, date)); // 创建时间
		map.put("is_del", 0); // 0 未删除；1已删除
        String msg = JSONObject.toJSONString(map);
		BusinessUtil.writeLog("sunshineVehicleInsurance_mq_success", "增值服务ID:" + params.get("id") + "，消息体：" + msg);
        try {
            pushService.writeToMQ("sunshineVehicleInsuranceExchange", msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("阳光车险MQ失败：", e);
        }
    }

	/**
     * 根据包ID获取包的基本信息
     * @param pkgId
     * @return
     */
    public Map<String, Object> getPkgInfoById(int pkgId) {
        return iMerchantInfoDao.getPkgInfoById(pkgId);
    }

    // 开通增值服务后给C_PLAN发MQ消息
    private void sendOpenIncreaseServiceMQ(int pkgId, Map<String, Object> params, Map<String, Object> pkgMap, Map<String, Object> bossInfo, Date date) {
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", params.get("orderNo")); //订单ID
        map.put("userId", bossInfo.get("user_id")); // 购买该服务的店主UserID
        if("2".equals(StringUtil.null2Str(params.get("merchantType")))){//2，个性服务，商铺id传0
            map.put("merchantId", 0); // 店铺ID
        }else{
            map.put("merchantId", params.get("merchantId")); // 店铺ID
        }
        map.put("paymentTime", formatDate("yyyy-MM-dd HH:mm:ss", date)); // 订单支付时间
        map.put("orderPayType", params.get("payType")); // 支付方式：1-支付宝支付 2-微信支付 3-现金支付
        map.put("orderActualPrice", params.get("tradeAmount")); // 订单实际支付金额
        map.put("orderPrice", params.get("tradeAmount")); // 订单金额
        map.put("pkgId", pkgId); // 服务包ID
        map.put("pkgName", null == pkgMap ? "" : pkgMap.get("name"));
        map.put("serviceId", null == pkgMap ? "" : pkgMap.get("service_id")); // 服务大类ID
        map.put("serviceNumber", params.get("inviteCode")); // 私人助理服务码
		String msg = JSONObject.toJSONString(map);
        System.out.println("开通增值服务后给C_PLAN发MQ消息：" + msg);
        try {
            pushService.writeToMQ("cplan.incPkgOrderExchange", msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 购买服务包含私人助理的商户发MQ消息
    private void sendConsultantMQ(int pkgId, Map<String, Object> params, Map<String, Object> pkgMap, Map<String, Object> bossInfo, Date date) throws Exception {
        //List<Map<String, Object>> pkgIds = iMerchantInfoDao.getConsultantPkgIds();
        //for (Map<String, Object> maps : pkgIds) {
            //if (pkgId == StringUtil.nullToInteger(maps.get("id"))) {
                Map<String, Object> merchantMap = selectMerchantBasicInfo(nullToLong(params.get("merchantId")));
                Map<String, Object> map = new HashMap<>();
                map.put("userId", bossInfo.get("user_id")); // 购买该服务的老板UserID
                map.put("merchantId", params.get("merchantId")); // 购买该服务的店铺ID
                map.put("merchantName", merchantMap.get("name")); // 店铺名称
                map.put("userPhone", bossInfo.get("phone")); // 老板手机号
                map.put("orderTime", formatDate("yyyy-MM-dd HH:mm:ss", date)); // 购买服务的时间
                map.put("merchantType", params.get("payType")); // 0 自由服务者 1一般商店
                map.put("orderType", StringUtil.nullToInteger(params.get("payType")) == 3 ? 2 : 1); // 1线上购买 2线下购买
                map.put("province", merchantMap.get("province")); // 店铺所在省份
                map.put("merchantAddress", merchantMap.get("locationAddress")); // 店铺地址
                map.put("city", merchantMap.get("city")); // 店铺所在城市
                map.put("pkgName", null == pkgMap ? "" : pkgMap.get("name")); // 套餐名称

                map.put("lng", merchantMap.get("longitude")); // 经度
                map.put("lat", merchantMap.get("longitude")); // 纬度
                map.put("serviceAppType", merchantMap.get("merchantServiceTypeNames")); // 服务商：店铺行业
                map.put("image", merchantMap.get("iconUrl")); // 服务商：店铺头像
                map.put("serviceNumber", params.get("inviteCode")); // 私人助理服务码
                
                String msg = JSONObject.toJSONString(map);
                System.out.println("开通增值服务后给C_PLAN发MQ消息(包含私人助理)：" + msg);
                pushService.writeToMQ("cplan.orderCooperatorExchange", msg);
            //}
        //}
    }

    //处理抢单金赠送
    private void dealGrapMoney(Long merchantId,Map<String,Object> rule){
    	 // 1，插入商户订单明细
    	Map<String, Object> orderMap = new HashMap<>(4);
    	orderMap.put("merchantId", merchantId);
    	orderMap.put("orderId", 0);
    	orderMap.put("payType", 6); // VIP充值送抢单金
    	orderMap.put("payMoney", rule.get("ruleValue"));
    	iMerchantPayService.addMerchantOrderPaymentDetails( orderMap);

    	// 2，更新商户订单金额
    	Map<String, Object> statMap = new HashMap<>(2);
    	statMap.put("merchantId", merchantId);
    	statMap.put("money", rule.get("ruleValue"));
    	iMerchantPayService.updateMerchantOrderSurplusPrice(statMap);
    }

    //处理阳光车险
    private void modifyMsg(StringBuffer msg, Map<String, Object> itemRule) {
		msg.append(",并获得一张").append(itemRule.get("ruleValue")).append("元阳光车险返现资格券，9月30日前将放入你的钱包中，敬请关注！");
	}

	private void updateMerchantCache(long merchantId,Map newValue) {
		if(newValue != null){
			Map<String, Object> merchantInfo = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO,
							StringUtil.null2Str(merchantId));
			if(merchantInfo != null){
				merchantInfo.putAll(newValue);
				// 更新缓存中的商铺信息(暂定一天未访问则移除缓存)
				commonCacheService.setObject(merchantInfo,CacheConstants.MERCHANT_BASIC_INFO_TIMEOUT,
						CacheConstants.MERCHANT_BASIC_INFO,StringUtil.null2Str(merchantId));
			}
		}
	}

	/**
	 *  @param params
	 *  @return
	 *  @author Liuxingwen
	 *  @created 2016年10月26日 下午4:03:15
	 *  @lastModified      
	 *  @history            
	 */
	@Override
	public Map<String, Object> selectpaymentStatus(Map<String, Object> params) {
		// TODO Auto-generated method stub
		
		
		return merchantPaymentDetailsDao.selectpaymentStatus(params);
//		return merchantPaymentDetailsDao.selectpaymentByOrderId(params);
	}

	@Override
	@Transactional
	public JSONObject addEmployeeNumApplyNeedConfirm(Map<String, Object> params) {
			JSONObject jsonObject = new ResultJSONObject("000","雇问号支付待确认记录增加成功");
			int paymentType=StringUtil.nullToInteger(params.get("paymentType"));
	        String innerTradeNo=StringUtil.nullToString(params.get("innerTradeNo"));
	        String merchantId = StringUtil.nullToString(params.get("merchantId"));
	        String payTime=StringUtil.nullToString(params.get("paymentTime"));
	        int pkgId=StringUtil.nullToInteger(params.get("packageId"));
	        double payAmount=StringUtil.nullToDouble(params.get("paymentAmount"));
	        int employeeNum = StringUtil.nullToInteger(params.get("increaseEmployeeNum"));
	        String inviteCode =StringUtil.nullToString(params.get("inviteCode"));
	        String appType =StringUtil.nullToString(params.get("appType"));
	        Integer clientType =StringUtil.nullToInteger(params.get("clientType"));
	        Map<String,Object>  applyMap = new HashMap<String,Object>();
	        applyMap.put("merchantId", merchantId);
	        applyMap.put("increaseEmployeeNum", employeeNum);
	        applyMap.put("appType", appType);
	        applyMap.put("applyStatus", 0);
	        applyMap.put("money", payAmount);
	        applyMap.put("payNo", null);
	        applyMap.put("payType", paymentType);
	        applyMap.put("clientType", clientType);
	        applyMap.put("buyConfirm", 0);
	        applyMap.put("innerTradeNo", innerTradeNo);
	        applyMap.put("inviteCode", inviteCode);
	        
	        if (iMerchantEmployeesNumApplyDao.checkEmployeesNumApplyByInnerTradeNo(applyMap)==0){
	                iMerchantEmployeesNumApplyDao.addMerchantEmployeesNumApplyWithConfirm(applyMap);
	        }else{
	        	jsonObject = new ResultJSONObject("001","回调请求已提前到达并插入记录");
	        }
	        return jsonObject;
	}

	@Override
	public JSONObject addIncPackageNeedConfirm(Map<String, Object> params) {
		JSONObject jsonObject = new ResultJSONObject("000","增值服务支付待确认记录增加成功");
		int paymentType=StringUtil.nullToInteger(params.get("paymentType"));
        String innerTradeNo=StringUtil.nullToString(params.get("innerTradeNo"));
        String merchantId = StringUtil.nullToString(params.get("merchantId"));
        String payTime=StringUtil.nullToString(params.get("paymentTime"));
        String userId=StringUtil.nullToString(params.get("userId"));
        int pkgId=StringUtil.nullToInteger(params.get("packageId"));
        double payAmount=StringUtil.nullToDouble(params.get("paymentAmount"));
        int employeeNum = StringUtil.nullToInteger(params.get("increaseEmployeeNum"));
        String inviteCode =StringUtil.nullToString(params.get("inviteCode"));
        String appType =StringUtil.nullToString(params.get("appType"));
        Integer clientType =StringUtil.nullToInteger(params.get("clientType"));
        Map<String,Object>  applyMap = new HashMap<String,Object>();
		
        applyMap.put("pkgId", pkgId);
        applyMap.put("userId", userId);
        applyMap.put("merchantId", merchantId);
        applyMap.put("payType", paymentType); 
        applyMap.put("tradeAmount", payAmount);
        applyMap.put("create_time", new Date(System.currentTimeMillis()));
        applyMap.put("buyConfirm", 0);
        applyMap.put("innerTradeNo", innerTradeNo);
        applyMap.put("inviteCode", inviteCode);
		
        if (iMerchantInfoDao.checkIsOpenIncreaseServiceByInnerTradeNo(applyMap)==0){
        	iMerchantInfoDao.addIncService(applyMap);
        }else{
        	 jsonObject = new ResultJSONObject("001","回调请求已提前到达并插入记录");
        }
        
        return jsonObject;
	}

	@Override
	public String selectMerchantIcon(Long merchantId) {
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("merchantId",merchantId);
		return this.iMerchantAttachmentDao.selectMerchantIcon(paramsMap);
	}

	@Override
	public String selectMerchantQrcode(Long merchantId) {
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("merchantId",merchantId);
		return this.iMerchantAttachmentDao.selectMerchantQrcode(paramsMap);
	}

	@Override
	public int insertMerchantQrcode(Long merchantId,String path) {
		Map<String,Object> paramsMap = new HashMap<String,Object>();
		paramsMap.put("merchantId",merchantId);
		paramsMap.put("path",path);
		return this.iMerchantAttachmentDao.insertMerchantQrcode(paramsMap);
	}
}
