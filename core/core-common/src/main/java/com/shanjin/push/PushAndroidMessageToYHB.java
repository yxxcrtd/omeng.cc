package com.shanjin.push;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IAndroidAppPushConfigDao;

public class PushAndroidMessageToYHB implements Runnable {

	static String host = "http://sdk.open.api.igexin.com/apiex.htm";
	private static Logger logger = Logger.getLogger(PushAndroidMessageToYHB.class);
	private IAndroidAppPushConfigDao androidAppPushConfigDao;
	private ICommonCacheService commonCacheService;
	private String appType;
	private List<Map<String, Object>> AndroidPushInfo;
	private String transmissionContent;

	public PushAndroidMessageToYHB(String appType, List<Map<String, Object>> AndroidPushInfo, String transmissionContent, IAndroidAppPushConfigDao androidAppPushConfigDao, ICommonCacheService commonCacheService) {
		this.appType = appType;
		this.AndroidPushInfo = AndroidPushInfo;
		this.transmissionContent = transmissionContent;
		this.androidAppPushConfigDao = androidAppPushConfigDao;
		this.commonCacheService = commonCacheService;
	}

	@Override
	public void run() {
		Map<String, Object> pushMap = null;
		pushMap = (Map<String, Object>) commonCacheService.getObject(CacheConstants.PUSH_MAP, appType);
		if (pushMap == null) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("appType", appType);
			pushMap = androidAppPushConfigDao.getPushConfig(paramMap);
			commonCacheService.setObject(pushMap, CacheConstants.PUSH_MAP, appType);
		}

		if (pushMap != null) {
			String appkey = StringUtil.null2Str(pushMap.get("appKey"));
			String master = StringUtil.null2Str(pushMap.get("masterSecret"));
			String appId = StringUtil.null2Str(pushMap.get("appId"));
			IGtPush push = new IGtPush(host, appkey, master);
			try {
				push.connect();
				IPushResult ret = null;
				for (Map<String, Object> pushInfo : AndroidPushInfo) {
					String transData = transmissionContent;
					transData = transData.concat("," + StringUtil.null2Str(pushInfo.get("userId")));
					TransmissionTemplate template = TransmissionTemplate(transData, pushMap);
					SingleMessage message = new SingleMessage();
					message.setData(template);
					message.setOffline(true);
					message.setOfflineExpireTime(24 * 1000 * 3600 * 3);

					Target target = new Target();
					target.setAppId(appId);
					target.setClientId(StringUtil.null2Str(pushInfo.get("clientId")));
					try {
						ret = push.pushMessageToSingle(message, target);
					} catch (Exception e) {
						e.printStackTrace();
						ret = push.pushMessageToSingle(message, target);
					}
					if (ret != null) {if(logger.isInfoEnabled()){
						StringBuilder str=new StringBuilder();
						str.append(pushInfo).append(" ").append( ret.getResponse().toString());
						logger.info("个推返回结果：  pushInfo:" +str);
					}
					} else {
						logger.error("个推返回结果为空");
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static TransmissionTemplate TransmissionTemplate(String transmissionContent, Map<String, Object> pushMap) throws Exception {
		String appkey = "";
		String appId = "";
		TransmissionTemplate template = new TransmissionTemplate();
		appId = StringUtil.null2Str(pushMap.get("appId"));
		appkey = StringUtil.null2Str(pushMap.get("appKey"));
		template.setAppId(appId);
		template.setAppkey(appkey);
		template.setTransmissionType(2);
		template.setTransmissionContent(transmissionContent);
		return template;
	}

}
