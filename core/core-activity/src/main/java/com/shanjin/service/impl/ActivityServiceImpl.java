package com.shanjin.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.support.CorrelationData;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IUserRelatedCacheServices;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IActivityPushDao;
import com.shanjin.dao.ICuttingDao;
import com.shanjin.dao.IMerchantInfoForH5Dao;
import com.shanjin.dao.IRequirementDao;
import com.shanjin.dao.IUserDao;
import com.shanjin.push.util.AESUtil;
import com.shanjin.push.util.PushConfig;
import com.shanjin.service.IActivityService;

/**
 * 活动相关实现类
 * @author 
 *
 */
@Service("activityService")
public class ActivityServiceImpl implements IActivityService{
	@Resource
	private ICuttingDao cuttingDao;
	
	@Resource
	private IActivityPushDao pushDao;
	
	@Resource
	private ICommonCacheService commonCacheService;
	
	@Resource
	private IUserRelatedCacheServices userRelatedCacheServices;

	@Resource
	private IMerchantInfoForH5Dao merchantInfoForH5Dao;
	
	@Resource
	private IUserDao userDao;
	
	@Resource
	private IRequirementDao requirementDao;
	
	@Resource
	RabbitTemplate orderTemplate;
	
	private Map<String,Object> getServiceMapById(String serviceId){
		List<Map<String, Object>> serviceList =(List<Map<String, Object>>)commonCacheService.getObject(CacheConstants.ACTIVITY_CACHE_ALLSERVICETYPE);
		if(serviceList==null){
			serviceList=cuttingDao.getAllServiceList();
			commonCacheService.setObject(serviceList, CacheConstants.ACTIVITY_CACHE_ALLSERVICETYPE);
		}
		
		if(serviceList!=null&&serviceList.size()>0){
			for(Map<String, Object> map : serviceList){
				if(serviceId.equals(StringUtil.null2Str(map.get("serviceTypeId")))){
					return map;
				}
			}
		}
		return null;
	}
	
	private List<Map<String,Object>> getLabelList(){
		List<Map<String, Object>> labelList =(List<Map<String, Object>>)commonCacheService.getObject(CacheConstants.LABEL_LIST);
		if(labelList==null){
			labelList=cuttingDao.getLabelList();
			commonCacheService.setObject(labelList, CacheConstants.LABEL_LIST);
		}
		return labelList;
	}

	@Override
	public JSONObject getCuttingInfo(String merchantId)
			throws Exception {
		JSONObject jsonObject = (JSONObject) commonCacheService.getObject(CacheConstants.MERCHANTINFO_CUTT,merchantId);
		if(jsonObject==null){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pageNo", 0);
		paramMap.put("pageSize", 10);
		paramMap.put("merchantId", merchantId);
		jsonObject = new ResultJSONObject("000", "获取商铺开店信息和剪彩统计信息成功");
		List<Map<String,Object>> cuttingDetail =null;
		
		int flag=1;
		// 非商户第一次入驻，需查询活动统计详情
		Map<String, Object> labelCount = cuttingDao.getLabelCount(paramMap);
		if(labelCount==null||labelCount.isEmpty()){
			flag=2;
		}
		paramMap.put("flag", flag);	
		cuttingDetail = cuttingDao.getCuttingDetail(paramMap);
		
		Map<String, Object> merchantInfo=null;
		merchantInfo = cuttingDao.getMerchantInfo(paramMap);
		if (merchantInfo != null && !merchantInfo.isEmpty()) {
				String icon = StringUtil.null2Str(merchantInfo.get("icon"));
				if (StringUtil.isNullStr(icon)) {
					icon = Constant.DEFAULT_MERCHANT_PORTRAIT_PTAH;
				}
				icon = BusinessUtil.disposeImagePath(icon);
				merchantInfo.put("icon", icon);
				String serviceIds = StringUtil.null2Str(merchantInfo
						.get("serviceIds"));
				String serviceNames = "";
				if (!StringUtil.isNullStr(serviceIds)) {
					String[] ss = serviceIds.split(",");
					int i = 0;
					for (String s : ss) {
						Map<String, Object> serviceTypeMap = getServiceMapById(s);
						if (serviceTypeMap != null && !serviceTypeMap.isEmpty()) {
							i++;
							if (i > 4)
								break; // 超过四个服务项目不显示
							serviceNames = serviceNames
									+ serviceTypeMap.get("serviceTypeName")
									+ " ";
						}
					}
				}
				if (!StringUtil.isNullStr(serviceNames)) {
					serviceNames = serviceNames.trim();
				}
				merchantInfo.remove("serviceIds");
				merchantInfo.put("serviceNames", serviceNames);
			
		
		}
		jsonObject.put("merchantInfo", merchantInfo);
		jsonObject.put("cuttingDetail", cuttingDetail);
		
		commonCacheService.setObject(jsonObject,CacheConstants.WEEK_STAR_EXPIRTIME, CacheConstants.MERCHANTINFO_CUTT,merchantId);
		}
		return jsonObject;
	}

	@Override
	public JSONObject getLabelList(String merchantId,int pageNo, int pageSize) {
		JSONObject jsonObject = new ResultJSONObject("000", "获取剪彩详情成功");
		Map<String, Object> paramMap = new HashMap<String, Object>();
		List<Map<String,Object>> cuttingDetail = null;
		pageSize=StringUtil.nullToInteger(pageSize);
		if(pageSize>50){
			pageSize=50;
		}
		pageNo=StringUtil.nullToInteger(pageNo)*pageSize;
		paramMap.put("merchantId", merchantId);
		paramMap.put("pageNo", pageNo);
		paramMap.put("pageSize", pageSize);
		cuttingDetail = cuttingDao.getCuttingDetail(paramMap);
		jsonObject.put("cuttingDetail", cuttingDetail);
		return jsonObject;
	}
	
	@Override
	@Transactional
	public JSONObject saveCuttingInfo(Map<String, Object> paramMap)
			throws Exception {
		JSONObject jsonObject = null;  
		paramMap.put("payTime", getDate());
		if(paramMap.get("nickname")==null||paramMap.get("nickname").toString().equals("")){
			paramMap.put("nickname", "未知");
		}
		int hasCutting = cuttingDao.checkMerchantCutting(paramMap); // 商户是否已被剪彩 ，即剪彩数>0
        if(hasCutting==0){
        	//插入
        	cuttingDao.insertCuttingInfo(paramMap);
        }
        //获取剪彩的信息
        long cuttingId=cuttingDao.getCuttingIdByMer(paramMap);
    	paramMap.put("cuttingId", cuttingId);
    	
        if(paramMap.get("type").equals("1")){//剪彩赠送标签
        	String openId=(String) commonCacheService.getObject(paramMap.get("openId").toString());
    		if(!StringUtil.isNullStr(openId)){
    			jsonObject = new ResultJSONObject("006", "不要重复提交");
    			return jsonObject; 
    		}
        	String labelName=getLabelName(paramMap);
        	if(StringUtil.isNullStr(labelName)){
        		jsonObject = new ResultJSONObject("002", "标签不存在");
    			return jsonObject; 
        	}
        	
        	cuttingDao.updateCuttingInfo(paramMap);
            cuttingDao.insertCuttingInfoLabel(paramMap);
        }else if(paramMap.get("type").equals("2")){//剪彩赠送红包
        	int troCount=cuttingDao.checkCuttingInfobyTranId(paramMap);
        	if(troCount>0){
        		jsonObject = new ResultJSONObject("000", "保存剪彩信息成功");
        		return jsonObject;
        	}
        	
        	//更新剪彩主体信息
        	cuttingDao.updateCuttingInfoCash(paramMap);
        	//更新剪彩红包详情信息
            cuttingDao.insertCuttingInfoCash(paramMap);
            
            paramMap.put("transSeq", paramMap.get("id"));
        	//更新商户余额信息
        	cuttingDao.updateMerchantStatics(paramMap);
        	//更新商户收支明细信息
        	cuttingDao.insertMerchantPayMentDetail(paramMap);
        	
        	sendMq("opay.ribbonCutExchange",paramMap);
        }
//       if(paramMap.get("type").equals("2")){
//        final String data=getDataCutt(paramMap);
//        final List<Map<String,Object>> pushMerchantList = cuttingDao.getPushMerchant(paramMap);
//        final String appType=cuttingDao.getAppType(paramMap);
//        final Map<String,Object> configMap=getConfigurationInfoByKey();
//        new Thread(new Runnable() {			
//			@Override
//			public void run() {	
//				try{
//	                PushUtil.pushMerchantMsg(configMap,pushMerchantList,appType,data);
//				}catch(Exception e){
//					e.printStackTrace();
//				}
//			}
//		}).start();
//       //获取配置信息
//       }    
		jsonObject = new ResultJSONObject("000", "保存剪彩信息成功");
		commonCacheService.setObject("openId",30, paramMap.get("openId").toString());
		commonCacheService.deleteObject(CacheConstants.MERCHANTINFO_CUTT,paramMap.get("merchantId").toString());
		return jsonObject;
	}

	private void sendMq(String queueName, Map<String, Object> paramMap) {
		Map<String,Object> configMap=new HashMap<String,Object>();
		String msg="";
		String encryptedText="";
		Map<String,Object> merInfo=cuttingDao.getMerchantBasic(paramMap);
		if(merInfo!=null){
		try {
		configMap.put("merchantId", paramMap.get("merchantId"));
		configMap.put("merchantUserId", merInfo.get("user_id"));
		configMap.put("merchantName", merInfo.get("name"));
		configMap.put("merchantUserPhone", merInfo.get("phone"));
		configMap.put("userName", "");
		configMap.put("transSeq", paramMap.get("transSeq"));
		configMap.put("payType", "1");
		configMap.put("thirdOrderId", paramMap.get("out_trade_no"));
		configMap.put("thirdTransNo", paramMap.get("transaction_id"));
		configMap.put("payTime",  paramMap.get("payTime"));
		configMap.put("transAmount", paramMap.get("total_fee"));
		configMap.put("openUserId", paramMap.get("openId"));
		configMap.put("openUserName", paramMap.get("nickname"));
		configMap.put("remark", "剪彩红包");
		
		 msg=JSONObject.toJSONString(configMap);
		 encryptedText= AESUtil.parseByte2HexStr(AESUtil.encrypt(msg, "367937E1967092280C56077755E4C65B"));
			writeToMQ(queueName,encryptedText);
		} catch (Exception e) {
			writeMqFailure(paramMap.get("transSeq"), encryptedText,"opay.ribbonCutExchange", 4);
			e.printStackTrace();
		}
		}
	}

	private void writeMqFailure(Object id, String encryptedText, String queueName, int type) {
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("business_id", (long)id);
		paramMap.put("msg", encryptedText);
		paramMap.put("queueName", queueName);
		paramMap.put("type", type);
		cuttingDao.insertMqFailure(paramMap);
	}

	private String getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	private void writeToMQ(String queueName,String msg) throws Exception{	
	
	   orderTemplate.send(queueName,null, MessageBuilder.withBody(msg.getBytes("UTF-8"))
	   .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).build(), new CorrelationData(msg));
		
	}
	
	private String getLabelName(Map<String, Object> paramMap) {
		String labelName="";
		List<Map<String,Object>> map=getLabelList();
		for(int i=0;i<map.size();i++){
			if(map.get(i).get("label").equals(paramMap.get("label"))){
				labelName=(String) map.get(i).get("labelDesc");
			}
		}
		return labelName;
	}

	private String getDataCutt(Map<String, Object> paramMap) {
		String data="";
		if(paramMap.get("type").equals("1")){//剪彩赠送标签
        	data="好友"+paramMap.get("nickname")+"给店铺捧场送上"+paramMap.get("labelName");
        }else if(paramMap.get("type").equals("2")){//剪彩赠送红包
        	data="好友"+paramMap.get("nickname")+"给店铺捧场送上"+paramMap.get("total_fee")+"元红包";
        }
		 
		return data;
	}

	private Map<String,Object> getConfigurationInfoByKey(){
		Map<String,Object> configMap=new HashMap<String,Object>();
		// 读取配置信息缓存
		List<Map<String, Object>> listConfigurationInfo = (List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.CONFIG_KEY);
		if (listConfigurationInfo == null) {// 如果没有缓存配置信息则读取数据库
			listConfigurationInfo = pushDao.getConfigurationInfo();
			commonCacheService.setObject(listConfigurationInfo, CacheConstants.CONFIG_KEY);
		}
		List<String> keyList=PushConfig.getConfigKeyList();
		for (Map<String, Object> map : listConfigurationInfo) {
			String configKey = map.get("config_key") == null ? "" : map.get("config_key").toString();
			String configValue = map.get("config_value") == null ? "" : map.get("config_value").toString();
			if (keyList.contains(configKey)) {
				configMap.put(configKey, configValue);
			}		
		}
		return configMap;
	}

	@Override
	public JSONObject getMerchantNum() throws Exception {
	    JSONObject jsonObject = null;
		int count = cuttingDao.getMerchantCount(); // 
		count = count + 1;
		jsonObject = new ResultJSONObject("000", "获取开店总数成功");
        jsonObject.put("count", count);

		return jsonObject;
	}

	/**
	 * 商户信息 （用于商户信息分享，此方法为过渡性方案）
	 */
	@Override
	public JSONObject merchantInfoForH5(Long merchantId, Double longitude, Double latitude, String openId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> info = basicInfo(merchantId);
		if (info == null) {
			return new ResultJSONObject("merchant_info_null", "店铺信息为空");
		}
		Map<String, Object> merchantInfo = new HashMap<String, Object>();
		merchantInfo.put("auth", info.get("auth"));
		merchantInfo.put("name", info.get("name"));
		merchantInfo.put("iconUrl", info.get("iconUrl"));
		merchantInfo.put("phone", info.get("phone"));
		merchantInfo.put("city", info.get("city"));
		merchantInfo.put("locationAddress", info.get("locationAddress"));
		merchantInfo.put("detailAddress", info.get("detailAddress"));
		merchantInfo.put("detail", info.get("detail"));
		merchantInfo.put("microWebsiteUrl", info.get("microWebsiteUrl"));
		merchantInfo.put("longitude", info.get("longitude"));
		merchantInfo.put("latitude", info.get("latitude"));
		merchantInfo.put("vipBackgroundUrl", info.get("vipBackgroundUrl"));
		if(info.get("isPrivateAssistant").toString().equals("1")){
			merchantInfo.put("isPa", true);
		}else{
			merchantInfo.put("isPa", false);
		}
		
		// 0未知 1企业 2自由（个性）服务
		String appType = (String) info.get("appType");
		if (appType == null) {
			merchantInfo.put("merchantType", 0);
		} else if (!appType.equals("gxfw")) {
			merchantInfo.put("merchantType", 1);
			// 背景图（暂时固定）
			merchantInfo.put("bgpic", "http://activity.omeng.cc/web/app/img/default/bgpic.png");
		} else {
			merchantInfo.put("merchantType", 2);
			// 背景图（暂时固定）
			merchantInfo.put("bgpic", "http://activity.omeng.cc/web/app/img/default/bgpicStore.png");
		}
		String result = BusinessUtil.calcDistance(Double.parseDouble(StringUtil.null2Str(info.get("longitude"))),
				Double.parseDouble(StringUtil.null2Str(info.get("latitude"))), longitude, latitude);
		if ("-1".equals(result)) {
			merchantInfo.put("distance", 0);
		} else {
			merchantInfo.put("distance", result);
		}
		// 代金券总数（有效期内且没有被领取的代金券数量）
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		int vouchersCount = this.merchantInfoForH5Dao.selectCurrentVouchersInfoCount(paramMap);
		merchantInfo.put("vouchersCount", vouchersCount);
		// 服务类型
		List<String> serviceTypeList = this.merchantInfoForH5Dao.selectMerchantServiceTypeName(paramMap);
		merchantInfo.put("serviceTypeList", serviceTypeList);

		Map<String, Object> photo = new HashMap<String, Object>();
		// 相片总数
		photo.put("photoTotal", this.merchantInfoForH5Dao.selectPhotoTotal(paramMap));
		// 最新4张相片URL
		photo.put("last4PhotoUrl", last4Photo(merchantId));
		merchantInfo.put("photo", photo);
		
		Map<String, Object> goods = new HashMap<String, Object>();
		// 商品总数
		goods.put("goodsTotal", this.merchantInfoForH5Dao.selectGoodsCount(paramMap));
		// 最新3个商品信息
		goods.put("last3GoodsInfo", last3GoodsInfo(merchantId));
		merchantInfo.put("goods", goods);

		Map<String, Object> evaluation = new HashMap<String, Object>();
		// 统计信息
		Map<String, Object> map = statisticsInfoEdit(merchantId);
		evaluation.put("starLevel", map.get("starLevel"));
		evaluation.put("score", map.get("score"));
		evaluation.put("totalCountEvaluation", map.get("totalCountEvaluation"));

		List<Map<String, Object>> evaluationInfo = this.merchantInfoForH5Dao.getTextEvaluationTop2(paramMap);
		for (Map<String, Object> evaluationMap : evaluationInfo) {
			BusinessUtil.disposePath(evaluationMap, "path");

			String phone = String.valueOf(evaluationMap.get("phone"));
			phone = phone.substring(0,3) + "****" + phone.substring(7, phone.length());
			evaluationMap.put("phone", phone);
		}
		evaluation.put("evaluationInfo", evaluationInfo);
		merchantInfo.put("evaluation", evaluation);

		if (StringUtils.isEmpty(openId)) {
			merchantInfo.put("collection", 0);
		} else {
			paramMap.put("openid", openId);
			Long userId = this.merchantInfoForH5Dao.selectUserIdByopenId(paramMap);
			if (userId == null) {
				merchantInfo.put("collection", 0);
			} else {
				paramMap.put("userId", userId);
				// 验证商家是否被用户收藏
				int collection = this.merchantInfoForH5Dao.checkCollectionMerchant(paramMap);
				if (collection > 0) {
					collection = 1;
				} else {
					collection = 0;
				}
				merchantInfo.put("collection", collection);
			}
		}
		Integer collectionNum = this.merchantInfoForH5Dao.selectCollectionNum(paramMap);
		merchantInfo.put("collectionNum", collectionNum);
		
		boolean isVipStatus=getVipStatusByMerchanId(merchantId);
		merchantInfo.put("isVip", isVipStatus);

		jsonObject = new ResultJSONObject("000", "商户信息查询成功");
		jsonObject.put("merchantInfo", merchantInfo);
		return jsonObject;
	}

	private boolean getVipStatusByMerchanId(Long merchantId) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("merchantId", merchantId);
		boolean isVipStatus=false;
		List<Map<String, Object>> merchantServiceList=merchantInfoForH5Dao.getMerchantServiceList(paramMap);
		if(merchantServiceList!=null&&merchantServiceList.size()>0){
			StringBuffer pkgIds=new StringBuffer();
			for(Map<String, Object> map:merchantServiceList){
				pkgIds.append(map.get("pkg_id"));
				pkgIds.append(",");
			}
		paramMap.put("pkgIds", pkgIds.deleteCharAt(pkgIds.length()-1));
		int vipCout=merchantInfoForH5Dao.getVipStatus(paramMap);
		if(vipCout>0){
			isVipStatus=true;
		}	
		}
		return isVipStatus;
	}

	/**
	 * 商户基本信息 （用于商户信息分享，此方法为过渡性方案）
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> basicInfo(Long merchantId) throws Exception {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("merchantId", merchantId);
		// 先从缓存中读取该商铺的基本信息
		Map<String, Object> info = (Map<String, Object>) commonCacheService.getObject(CacheConstants.MERCHANT_BASIC_INFO, StringUtil.null2Str(merchantId));
		if (info == null) {
			// 缓存中商铺信息不存在，从db中读取
			// **************************将原本的商铺（名字、省、市、经纬度、地址、电话、微官网、详细介绍、图标、vip等级）基本数据查询合并一起查询***************************
			info = this.merchantInfoForH5Dao.selectMerchantInfo(paramMap);
			if (info == null) {
				return null;
			}
			String iconUrl = StringUtil.null2Str(info.get("iconUrl"));
			if (!StringUtil.isNullStr(iconUrl)) {
				iconUrl = BusinessUtil.disposeImagePath(iconUrl);
			}
			String vipBackgroundUrl = StringUtil.null2Str(info.get("vipBackgroundUrl"));
			if (!StringUtil.isNullStr(vipBackgroundUrl)) {
				vipBackgroundUrl = BusinessUtil.disposeImagePath(vipBackgroundUrl);
			}else{
				boolean isVipStatus=getVipStatusByMerchanId(merchantId);
				if(isVipStatus){//是vip如果没有背景模板，给默认值
					paramMap.put("isDefault", 1);
					List<Map<String, Object>> vipBackgroundUrlList = this.merchantInfoForH5Dao.selectVipBackgroundUrlList(paramMap);
					if(vipBackgroundUrlList!=null && vipBackgroundUrlList.size()!=0){
						Map<String, Object> vipMap = vipBackgroundUrlList.get(0);
						info.put("vipBackgroundUrl", BusinessUtil.disposeImagePath(vipMap.get("image").toString()));
					}
				}else{
					info.put("vipBackgroundUrl", "");
				}
			}
			
			info.put("vipBackgroundUrl", vipBackgroundUrl);
			info.put("iconUrl", iconUrl);
			// 是否有认证 0-没有 1-企业认证 2-个人认证
			Map<String, Object> authMap = this.merchantInfoForH5Dao.selectAuthType(paramMap);
			if (authMap != null) {
				String authType = authMap.get("authType") == null ? "0" : authMap.get("authType").toString();
				info.put("auth", authType);
			} else {
				info.put("auth", 0);
			}
			// 0未知 1企业 2自由（个性）服务
			String appType = (String) info.get("appType");
			if (appType == null) {
				info.put("merchantType", 0);
			} else if (!appType.equals("gxfw")) {
				info.put("merchantType", 1);
			} else {
				info.put("merchantType", 2);
			}
		}
		return info;
	}

	/** 商户最新4张相片 */
	private String last4Photo(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		Map<String, Object> photosUrl = this.merchantInfoForH5Dao.selectLast4Photo(paramMap);
		if (photosUrl == null || photosUrl.isEmpty() || photosUrl.get("path") == null) {
			return Constant.EMPTY;
		}
		BusinessUtil.disposeManyPath(photosUrl, "path");
		return photosUrl.get("path").toString();
	}

	/** 商户最新3张商品信息 */
	private List<Map<String, Object>> last3GoodsInfo(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 商户ID
		paramMap.put("merchantId", merchantId);
		List<Map<String, Object>> last3GoodsInfo = this.merchantInfoForH5Dao.selectLast3GoodsInfo(paramMap);
		BusinessUtil.disposeManyPath(last3GoodsInfo, "goodsPictureUrl");
		return last3GoodsInfo;
	}

	/** 商户统计信息编辑 */
	private Map<String, Object> statisticsInfoEdit(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		Map<String, Object> statisticsInfoMap = this.merchantInfoForH5Dao.selectStatisticsInfo(paramMap);
		if (statisticsInfoMap == null) {
			statisticsInfoMap = new HashMap<String, Object>();
		}
		// 星级和评分编辑
		int totalCountEvaluation = Integer.parseInt(statisticsInfoMap.get("totalCountEvaluation") == null ? "0" : statisticsInfoMap.get("totalCountEvaluation").toString());
		if (totalCountEvaluation == 0) {
			// 没有用户评价的时候设置默认值
			statisticsInfoMap.put("starLevel", 5);
			statisticsInfoMap.put("score", 5.0);
		} else {
			if (statisticsInfoMap != null) {
				int totalAttitudeEvaluation = Integer.parseInt(statisticsInfoMap.get("totalAttitudeEvaluation") == null ? "0" : statisticsInfoMap.get("totalAttitudeEvaluation").toString());
				int totalQualityEvaluation = Integer.parseInt(statisticsInfoMap.get("totalQualityEvaluation") == null ? "0" : statisticsInfoMap.get("totalQualityEvaluation").toString());
				int totalSpeedEvaluation = Integer.parseInt(statisticsInfoMap.get("totalSpeedEvaluation") == null ? "0" : statisticsInfoMap.get("totalSpeedEvaluation").toString());
				// 总服务态度评价+总服务质量评价+总服务速度评价
				int totalEvaluation = totalAttitudeEvaluation + totalQualityEvaluation + totalSpeedEvaluation;
				// 分数
				BigDecimal score = new BigDecimal(totalEvaluation).divide(new BigDecimal(totalCountEvaluation).multiply(new BigDecimal(3)), 1, BigDecimal.ROUND_DOWN);
				statisticsInfoMap.put("score", score);
				// 星级
				BigDecimal starLevel = new BigDecimal(totalEvaluation).divide(new BigDecimal(totalCountEvaluation).multiply(new BigDecimal(3)), 0, BigDecimal.ROUND_HALF_UP);
				statisticsInfoMap.put("starLevel", starLevel);
			} else {
				// 统计信息异常的时候
				statisticsInfoMap.put("starLevel", 5);
				statisticsInfoMap.put("score", 5.0);
			}
		}
		return statisticsInfoMap;
	}

	@Override
	public JSONObject alreadySetOrderPlan(Long merchantId) throws Exception {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		// 接单计划 0-未设置1-已设置
		Integer alreadySetOrderPlan = this.merchantInfoForH5Dao.selectAlreadySetOrderPlan(paramMap);
		if (alreadySetOrderPlan == null) {
			jsonObject.put("alreadySetOrderPlan", 0);
		} else {
			jsonObject.put("alreadySetOrderPlan", 1);
		}
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "商户是否设置过接单计划查询成功");
		return jsonObject;
	}

	@Override
	public JSONObject getMerchantInfo(String merchantId) {
		JSONObject jsonObject = (JSONObject) commonCacheService.getObject(CacheConstants.MERCHANTINFO_ADVER,merchantId);
		if(jsonObject==null){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("merchantId", merchantId);
		jsonObject = new ResultJSONObject("000", "获取商家宣传信息成功");
		Map<String, Object> merchantInfo=null;
		merchantInfo = cuttingDao.getMerchantInfoForAdver(paramMap);
		if (merchantInfo != null && !merchantInfo.isEmpty()) {
				String icon = StringUtil.null2Str(merchantInfo.get("icon"));
				if (StringUtil.isNullStr(icon)) {
					icon = Constant.DEFAULT_MERCHANT_PORTRAIT_PTAH;
				}
				icon = BusinessUtil.disposeImagePath(icon);
				merchantInfo.put("icon", icon);
				String serviceIds = StringUtil.null2Str(merchantInfo
						.get("serviceIds"));
				String serviceNames = "";
				if (!StringUtil.isNullStr(serviceIds)) {
					String[] ss = serviceIds.split(",");
					int i = 0;
					for (String s : ss) {
						Map<String, Object> serviceTypeMap = getServiceMapById(s);
						if (serviceTypeMap != null && !serviceTypeMap.isEmpty()) {
							i++;
							if (i > 4)
								break; // 超过四个服务项目不显示
							serviceNames = serviceNames
									+ serviceTypeMap.get("serviceTypeName")
									+ " ";
						}
					}
				}
				if (!StringUtil.isNullStr(serviceNames)) {
					serviceNames = serviceNames.trim();
				}
				merchantInfo.remove("serviceIds");
				merchantInfo.put("serviceNames", serviceNames);
				paramMap.put("join_time", StringUtil.null2Str(merchantInfo.get("join_time")));
		
		}
		jsonObject.put("merchantInfo", merchantInfo);
		
		int ranking=cuttingDao.getRankingMerchant(paramMap);
		jsonObject.put("ranking", ranking+1);
		commonCacheService.setObject(jsonObject,CacheConstants.WEEK_STAR_EXPIRTIME, CacheConstants.MERCHANTINFO_ADVER,merchantId);
		}
		return jsonObject;
	}

	@Override
	public JSONObject getOrderActivityDetail(String activityId) {
		JSONObject jsonObject = new ResultJSONObject();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("activityId", activityId);
		Map<String,Object> basicInfo = (Map<String, Object>) commonCacheService.getObject("orderRequireInfo", activityId);
		List<Map<String,Object>> serviceList = (List<Map<String,Object>>) commonCacheService.getObject("orderRequireList", activityId);
		if(basicInfo==null||basicInfo.isEmpty()||serviceList==null||serviceList.isEmpty()){
			basicInfo = requirementDao.getRequireActivity(paramMap);
			serviceList = requirementDao.getRequireServiceList(paramMap);

			BusinessUtil.disposeManyPath(basicInfo,"image");
			BusinessUtil.disposeManyPath(serviceList, "image");
			if(serviceList!=null&&serviceList.size()>0){
				for(Map<String,Object> map : serviceList){
					String url = "http://api.oomeng.cn/customOrder/getOrderForm?serviceId="+map.get("serviceId");
					if(Constant.DEVMODE){
						url = "http://192.168.1.58:8080/customOrder/getOrderForm?serviceId="+map.get("serviceId");
					}
					map.put("url", url);
				}
			}
			commonCacheService.setExpireForObject(basicInfo, 2*60, "orderRequireInfo", activityId);
			commonCacheService.setExpireForObject(serviceList, 2*60, "orderRequireList", activityId); // 2分钟
		}
    	jsonObject.put("basicInfo", basicInfo);
    	jsonObject.put("serviceList", serviceList);
		jsonObject.put("resultCode", "000");
		jsonObject.put("message", "获取发单活动详情查询成功");
		return jsonObject;
	}

	@Override
	public JSONObject getMerchantInfoFanSi(Long merchantId, String openId) throws Exception {
		JSONObject jsonObject = merchantInfoForH5(merchantId,0D,0D,"");
		if(!jsonObject.get("resultCode").equals("000")){
			jsonObject = new ResultJSONObject("001", "商户信息不存在");
			return jsonObject; 
		}
		Map<String, Object> paramMap = new HashMap<String, Object>(); 
		List<Map<String, Object>> endTime=cuttingDao.getEndTime(paramMap);
		if(endTime!=null&&endTime.size()>0){
			paramMap.put("time", endTime.get(0).get("end_time"));	
			paramMap.put("flag", "true");
			jsonObject.put("flag", "true");
		}else{
			paramMap.put("flag", "false");
			jsonObject.put("flag", "false");
		}
		
		paramMap.put("merchantId", merchantId);
		
		Map<String, Object> fansiInfo = new HashMap<String, Object>(); 
		//查询平台开放城市
		int cityCount=cuttingDao.getCityCount(paramMap);
		//查询平台商铺总粉丝数
		int fansCount=cuttingDao.getFansCount(paramMap);
		//计算平台累计送出的金额
		int money=9999+8888+6666;
		int finalMoney=money*cityCount+fansCount;
		//int finalMoney=9841000+fansCount;
		if(endTime!=null&&endTime.size()>0){
			fansiInfo.put("finalMoney", "10000000");
		}else{
		    fansiInfo.put("finalMoney", finalMoney);
		}
		int perfansCount=cuttingDao.getPerFansCount(paramMap);
		fansiInfo.put("perfansCount", perfansCount);
		paramMap.put("perfansCount", perfansCount);
		int perRanking=cuttingDao.getperRanking(paramMap);
		if(perfansCount==0){
			perRanking=perRanking+1;
		    fansiInfo.put("perRanking", perRanking);
		}else{
			fansiInfo.put("perRanking", perRanking);
		}
		boolean openCity=false;
		if(getOpenCityById(merchantId)){
	      fansiInfo.put("perRankingMoney", getperRankingMoney(perRanking));
		  openCity=true;
		 }
		if(!StringUtil.isNullStr(openId)){
			paramMap.put("openId", openId);
			int checkFocus=cuttingDao.checkFocusMerbyOpenId(paramMap);
			if(checkFocus==0){
				jsonObject.put("checkFocus", false);
			}else{
				jsonObject.put("checkFocus", true);
			}
		}
		
		jsonObject.put("openCity", openCity);
		jsonObject.put("fansiInfo", fansiInfo);
		
		return jsonObject;
	}

	private boolean getOpenCityById(Long merchantId) {
		Map<String, Object> paramMap = new HashMap<String, Object>(); 
		paramMap.put("merchantId", merchantId);
		String city=cuttingDao.getCityByMerchantId(paramMap);
		List<Map<String, Object>> openCity=(List<Map<String, Object>>) commonCacheService.getObject(CacheConstants.OPEN_CITY);
		if(openCity==null||openCity.size()<1){	
			openCity = cuttingDao.getOpenCity();
		}
		for(int i=0;i<openCity.size();i++){
			if(openCity.get(i).get("city").equals(city)){
				return true;
			}
		}
		return false;
	}

	private int getperRankingMoney(int ranking) {
    int perRankingMoney=0;
		switch (ranking){
     case 1:
    	 perRankingMoney= 9999;
    	 break;
     case 2:
    	 perRankingMoney= 8888;
    	 break;
     case 3:
    	 perRankingMoney= 6666;
    	 break;
     default:
    	 perRankingMoney= 0;
     }
     
		return perRankingMoney;
	}

	@Override
	public JSONObject getRankingFanSiByCity(Long merchantId) {
		JSONObject  jsonObject = null;
		Map<String, Object> paramMap = new HashMap<String, Object>(); 
		paramMap.put("merchantId", merchantId);
		paramMap.put("pageNo", 0);
		paramMap.put("pageSize", 10);
		String province="";
		String city="";
		List<Map<String, Object>> proAndCity=cuttingDao.getProAndCity(paramMap);
		if(proAndCity!=null&&proAndCity.size()>0){
			 province=proAndCity.get(0).get("province").toString();
			 city=proAndCity.get(0).get("city").toString();
			jsonObject=(JSONObject) commonCacheService.getObject(CacheConstants.FENSI_RANKING,province,city);
		}
		if (jsonObject == null) {
			jsonObject = new ResultJSONObject("000", "获取粉丝排名成功");
			List<Map<String, Object>> endTime = cuttingDao.getEndTime(paramMap);
			if (endTime != null && endTime.size() > 0) {
				paramMap.put("time", endTime.get(0).get("end_time"));
				paramMap.put("flag", "true");
				jsonObject.put("flag", "true");
			} else {
				paramMap.put("flag", "false");
				jsonObject.put("flag", "false");
			}

			List<Map<String, Object>> cityRanking = cuttingDao.getCityRanking(paramMap);
			List<Map<String, Object>> countyRanking = cuttingDao.getCountyRanking(paramMap);

			for (int i = 0; i < cityRanking.size(); i++) {
				String iconUrl = StringUtil.null2Str(cityRanking.get(i).get("iconUrl"));
				if (!StringUtil.isNullStr(iconUrl)) {
					iconUrl = BusinessUtil.disposeImagePath(iconUrl);
				}
				cityRanking.get(i).put("iconUrl", iconUrl);
			}
			for (int i = 0; i < countyRanking.size(); i++) {
				String iconUrl = StringUtil.null2Str(countyRanking.get(i).get("iconUrl"));
				if (!StringUtil.isNullStr(iconUrl)) {
					iconUrl = BusinessUtil.disposeImagePath(iconUrl);
				}
				countyRanking.get(i).put("iconUrl", iconUrl);
			}
			int allFansiCount = cuttingDao.getFansAllCountByCity(paramMap);
			jsonObject.put("allFansiCount", allFansiCount);
			jsonObject.put("cityRanking", cityRanking);
			jsonObject.put("countyRanking", countyRanking);
			
			commonCacheService.setObject(jsonObject,CacheConstants.USER_HOME_PAGE_BANNER_EXPIRTIME, CacheConstants.FENSI_RANKING, province,city);
		}
		return jsonObject;
	}

	@Override
	public JSONObject getRankingFanSiByCityPage(Long merchantId, int pageNo, int pageSize) {
		JSONObject  jsonObject = new ResultJSONObject("000", "获取粉丝城市排名分页成功");
		Map<String, Object> paramMap = new HashMap<String, Object>(); 
		paramMap.put("merchantId", merchantId);
		pageSize=StringUtil.nullToInteger(pageSize);
		if(pageSize>50){
			pageSize=50;
		}
		pageNo=StringUtil.nullToInteger(pageNo)*pageSize;
		paramMap.put("pageNo", pageNo);
		paramMap.put("pageSize", pageSize);
		List<Map<String, Object>> endTime=cuttingDao.getEndTime(paramMap);
		if(endTime!=null&&endTime.size()>0){
			paramMap.put("time", endTime.get(0).get("end_time"));	
			paramMap.put("flag", "true");
			jsonObject.put("flag", "true");
		}else{
			paramMap.put("flag", "false");
			jsonObject.put("flag", "false");
		}
		
		List<Map<String, Object>> cityRanking=cuttingDao.getCityRanking(paramMap);
		
		for(int i=0;i<cityRanking.size();i++){
			String iconUrl = StringUtil.null2Str(cityRanking.get(i).get("iconUrl"));
			if (!StringUtil.isNullStr(iconUrl)) {
				iconUrl = BusinessUtil.disposeImagePath(iconUrl);
			}
			cityRanking.get(i).put("iconUrl", iconUrl);
		}
		int allFansiCount=cuttingDao.getFansAllCountByCity(paramMap);
		jsonObject.put("cityRanking", cityRanking);
		jsonObject.put("allFansiCount", allFansiCount);
		return jsonObject;
	}

	@Override
	public void checkFansiActEndTime() {
		System.out.println("执行job");
		Map<String, Object> paramMap = new HashMap<String, Object>(); 
		List<Map<String, Object>> endTime=cuttingDao.getEndTime(paramMap);
		if(endTime!=null&&endTime.size()>0){
			
		}else{
			paramMap.put("flag", "false");
			//查询平台开放城市
			int cityCount=cuttingDao.getCityCount(paramMap);
			//查询平台商铺总粉丝数
			int fansCount=cuttingDao.getFansCount(paramMap);
			//计算平台累计送出的金额
			int money=9999+8888+6666;
			int finalMoney=money*cityCount+fansCount;
  		    //	int finalMoney=9840900+fansCount;			
			if(finalMoney>=10000000){
				String nowDate=DateUtil.getNowTime();
				paramMap.put("time", nowDate);
				cuttingDao.insertFansiEndTime(paramMap);
				BusinessUtil.sendVerificationCode("15705604584", "1000万粉丝活动已截止", 2,"");
			}
		}
		
	}

	@Override
	public JSONObject getActivityShare(String activityId) {
		Map<String, Object> paramMap = new HashMap<String, Object>(); 
		paramMap.put("activityId", activityId);
		JSONObject	jsonObject=(JSONObject) commonCacheService.getObject(CacheConstants.ACTIVITY_SHARE,activityId);
		if(jsonObject==null){
			Map<String, Object> activityInfo=cuttingDao.getActivityShare(paramMap);
			if(activityInfo!=null){
				String shareImage = StringUtil.null2Str(activityInfo.get("shareImage"));
				if (!StringUtil.isNullStr(shareImage)) {
					shareImage = BusinessUtil.disposeImagePath(shareImage);
				}
				activityInfo.put("shareImage", shareImage);	
			}
			jsonObject = new ResultJSONObject("000", "获取活动分享详情成功");
			jsonObject.put("activityInfo", activityInfo);
		}
    	
		return jsonObject;
	}
}
