package com.shanjin.manager.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;







import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.common.util.StringUtil;
import com.shanjin.util.PushConfig;
import com.shanjin.util.PushManager;

/**
 * 推送工具类
 * @author Huang yulai
 *
 */
public class PushUtil {
	// 本地异常日志记录对象
		private static final Logger logger = Logger.getLogger(PushUtil.class);
		private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
		/**
		 * 服务商认证消息推送
		 */
		public static boolean pushAuditMsg(Long merchantId, String appType, int authStatus) {
			boolean flag = true;

			try {
				StringBuffer sb = new StringBuffer();
				sb.append(" SELECT up.user_id AS userId,up.client_id AS clientId,up.client_type AS clientType, ");
				sb.append(" up.push_id AS pushId FROM merchant_employees me INNER JOIN user_push up ON me.user_id=up.user_id ");
				sb.append(" WHERE me.is_del=0 AND me.merchant_id=").append(merchantId);
				List<Record> merchantClientList = Db.find(sb.toString()); 
				if(merchantClientList==null||merchantClientList.size()<1){
					return flag;
				}
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for(Record r : merchantClientList){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("pushId", r.getStr("pushId"));
					map.put("userId", r.getLong("userId"));
					map.put("merchantId", merchantId);
					map.put("clientType", r.getInt("clientType"));
					list.add(map);
				}
				Map<String, Object> paras = new HashMap<String,Object>();
				Map<String,Object> configMap=null;
				int pushType = 10; // 9 通过，10 未通过
				if(authStatus==1){
					pushType = 9;
					configMap=getConfigurationInfoByKey("pushType-9");
				}else{
					configMap=getConfigurationInfoByKey("pushType-10");
				}
				paras.put("pushType", pushType);
				paras.put("appType", appType);
				paras.put("merchantId", merchantId);
				PushManager.push(configMap,list, paras,"userId");
			} catch (Exception e) {
				flag = false;
				e.printStackTrace();
				logger.error("", e);
				return flag;
			}
			return flag;
		}
		
	
		private static Map<String, Object> getConfigurationInfoByKey(String key) {
			// 读取配置信息缓存
			Map<String, Object> allConfigurationInfoMap =null;
			try{
				allConfigurationInfoMap=(Map<String, Object>) commonCacheService.getObject(CacheConstants.CONFIG_KEY);
			}catch(Exception e){
				commonCacheService.deleteObject(CacheConstants.CONFIG_KEY);
			}
			if (allConfigurationInfoMap == null) {// 如果没有缓存配置信息则读取数据库
				allConfigurationInfoMap=new HashMap<String, Object>();
				List<Map<String, Object>> listConfigurationInfo =new ArrayList<Map<String, Object>>();
				String sql="select * from configuration_info";
				List<Record> list=Db.find(sql);
				Map<String, Object> map=null;
				if(list!=null&&list.size()>0){
				for(Record r:list){
					 map=new HashMap<String, Object>();
					 map.put("config_key", r.getStr("config_key"));
					 map.put("config_value", r.getStr("config_value"));
					 map.put("remark", r.getStr("remark"));
					 map.put("is_show", r.getInt("is_show"));
					 listConfigurationInfo.add(map);
				}
				}
				for(Map<String,Object> map1 : listConfigurationInfo){
					String configKey=StringUtil.null2Str(map1.get("config_key"));
					allConfigurationInfoMap.put(configKey, map1);
				}
				commonCacheService.setObject(allConfigurationInfoMap, CacheConstants.CONFIG_KEY);
			}
			
			Map<String, Object> configurationInfoMap = (Map<String, Object>)allConfigurationInfoMap.get(key);
			if(configurationInfoMap==null){
				System.out.println("------------配置参数为空："+key);
			}
			
			return configurationInfoMap;
		}	
}

