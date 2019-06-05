package com.shanjin.manager.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;






import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;















import org.json.JSONException;
import org.json.JSONObject;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.shanjin.cache.CacheConstants;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IImageCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.cache.service.impl.ImageCacheServiceImpl;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.HttpRequest;
import com.shanjin.common.util.StringUtil;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.service.ICommonService;
import com.shanjin.manager.service.impl.CommonServiceImpl;

/**
 * 初始化系统数据
 * @author Huang yulai
 *
 */
public class CommonUtil {
	private  static final Logger log = Logger.getLogger(CommonUtil.class);
	private static ICommonService commonService=new CommonServiceImpl();
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	private static IImageCacheService imageCacheService = new ImageCacheServiceImpl();
	/**
	 * 初始化系统操作资源
	 */
	public static void initSystemResourceMap(){
		System.out.println("========initSystemResourceMap start=====");
		try{
			List<SystemResource> list= commonService.getAllResouse();
			Constant.sysResource.clear();
			Constant.commonResourcePathList.clear();
			if(list!=null&&list.size()>0){
				for(SystemResource sr : list){
					String linkPath = sr.getStr("linkPath");
					Long resId = sr.getLong("id");
					if(linkPath.startsWith("/manager")){
						linkPath = linkPath.substring(8);
						SystemResource.dao.findById(resId).set("linkPath", linkPath).update();
					}
					
					Constant.sysResource.put(linkPath, sr);
					int isCommon = StringUtil.nullToInteger(sr.getInt("isCommon"));
					if(isCommon==1){
						Constant.commonResourcePathList.add(linkPath);
					}
					
				}
			}
			System.out.println("========initSystemResourceMap end=====");
		} catch (Exception e) {
			log.error("init initSystemResourceMap failed,because of"+e.getMessage(),e);
		}
	}
	
	/**
	 * 初始化服务项目
	 */
	public static void initServiceType(){
		System.out.println("========initServiceType start=====");
		try{
			Constant.serviceTypeMap.clear();
			Constant.serviceTypeNameMap.clear();
			List<Record> list= commonService.getServiceTypeForSearch();
			if(list!=null&&list.size()>0){
				String appType="";
				String serviceName="";
				String serviceId="";
				for(Record sr : list){
					appType = sr.getStr("app_type");
					serviceName = sr.getStr("service_type_name");
					serviceId = StringUtil.null2Str(sr.getInt("service_type_id")); 
					Constant.serviceTypeMap.put(appType+serviceName, serviceId);
					Constant.serviceTypeNameMap.put(appType+serviceId, serviceName);
				}
			}
			System.out.println("========initServiceType end=====");
		} catch (Exception e) {
			log.error("init initServiceType failed,because of"+e.getMessage(),e);
		}
	}
	
	/**
	 * 初始化服务项目（根据主键ID决定）
	 */
	public static void initServiceTypeById(){
		System.out.println("========initServiceTypeById start=====");
		try{
			Constant.serviceTypeByIdMap.clear();
			Constant.serviceTypeNameByIdMap.clear();
			List<Record> list= commonService.getServiceTypeForSearch();
			if(list!=null&&list.size()>0){
				String serviceName="";
				String id="";
				for(Record sr : list){
					serviceName = sr.getStr("service_type_name");
					id = StringUtil.null2Str(sr.getLong("id")); 
					Constant.serviceTypeByIdMap.put(serviceName.trim(), id);
					Constant.serviceTypeNameByIdMap.put(id, serviceName.trim());
				}
			}
			System.out.println("========initServiceTypeById end=====");
		} catch (Exception e) {
			log.error("init initServiceTypeById failed,because of"+e.getMessage(),e);
		}
	}
	
	
	/**
	 * 初始化app类型（用户和商户）
	 */
	public static void initAppInfoList(){
		System.out.println("========initAppInfoList start=====");
		try{
			Constant.merchantAppInfoMapList.clear();
			Constant.userAppInfoMapList.clear();
			Constant.merchantAppInfoMap.clear();
			Constant.userAppInfoMap.clear();
			List<Record> merchantAppInfoList= commonService.getAppTypeByPackType(Constant.PACKAGE_TYPE_MERCHANT);
			List<Record> userAppInfoList= commonService.getAppTypeByPackType(Constant.PACKAGE_TYPE_USER);
			String appType="";
			String appName="";
			if(merchantAppInfoList!=null&&merchantAppInfoList.size()>0){
				for(Record sr : merchantAppInfoList){
					appType = sr.getStr("app_type");
					appName = sr.getStr("app_name");
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("app_type", appType);
					map.put("app_name", appName);
					Constant.merchantAppInfoMapList.add(map);
					if(!StringUtil.isNullStr(appType)&&!StringUtil.isNullStr(appName)){
						Constant.merchantAppInfoMap.put(appType, appName);
					}
				}
			}
			if(userAppInfoList!=null&&userAppInfoList.size()>0){
				for(Record sr : userAppInfoList){
					appType = sr.getStr("app_type");
					appName = sr.getStr("app_name");
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("app_type", appType);
					map.put("app_name", appName);
					Constant.userAppInfoMapList.add(map);
					if(!StringUtil.isNullStr(appType)&&!StringUtil.isNullStr(appName)){
						Constant.userAppInfoMap.put(appType, appName);
					}
				}
			}
			System.out.println("========initAppInfoList end=====");
		} catch (Exception e) {
			log.error("init initAppInfoList failed,because of"+e.getMessage(),e);
		}
		
		//  设置第三方app信息
		Constant.thirdAppInfoMap.put("dsf@dd", "滴滴出行");
		Constant.thirdAppInfoMap.put("dsf@uber", "uber");
		Constant.thirdAppInfoMap.put("dsf@12306", "12306");
		Constant.thirdAppInfoMap.put("dsf@xc", "携程");
		Constant.thirdAppInfoMap.put("dsf@my", "猫眼电影");
		Constant.thirdAppInfoMap.put("dsf@yx", "优信二手车");
		//
		Constant.merchantAppInfoMap.put("dsf@dd", "滴滴出行");
		Constant.merchantAppInfoMap.put("dsf@uber", "uber");
		Constant.merchantAppInfoMap.put("dsf@12306", "12306");
		Constant.merchantAppInfoMap.put("dsf@xc", "携程");
		Constant.merchantAppInfoMap.put("dsf@my", "猫眼电影");
		Constant.merchantAppInfoMap.put("dsf@yx", "优信二手车");
	}
	
	/**
	 * 刷新系统缓存数据
	 */
	public static void flushSystemParam(){
		System.out.println("========flushSystemParam start=====");
		try{
			commonCacheService.delObjectContainsKey(CacheConstants.DICT_LIST_KEY, true);
			commonCacheService.delObjectContainsKey(CacheConstants.DICT_MAP_KEY, true);
			commonCacheService.delObjectContainsKey(CacheConstants.SERVICE_TYPE_LIST_KEY, true);
			System.out.println("========flushSystemParam end=====");
		} catch (Exception e) {
			log.error("flushSystemParam failed,because of"+e.getMessage(),e);
		}
	}
	
	/** 刷新活动，删除缓存 */
	public static void flushActivityCache(){
		System.out.println("========flushActivityCache start=====");
		try{
			commonCacheService.deleteObject(CacheConstants.INDEX_LIST_1);
			commonCacheService.deleteObject(CacheConstants.INDEX_LIST_2);
			commonCacheService.deleteObject(CacheConstants.INDEX_LIST_3);
			commonCacheService.deleteObject(CacheConstants.MERCHANT_INDEX);
			commonCacheService.deleteObject(CacheConstants.STRATEGY_INDEX);
			System.out.println("========flushActivityCache end=====");
		} catch (Exception e) {
			log.error("flushActivityCache failed,because of"+e.getMessage(),e);
		}
	}
	
	/**
	 * 刷新系统缓存数据
	 */
	public static void flushMerchant(String phone,String appType,String merchant_id){
		System.out.println("========flushSystemParam start=====");
		try{
			commonCacheService.deleteObject(CacheConstants.MERCHANT_BASIC_INFO, merchant_id,appType);
			commonCacheService.deleteObject(CacheConstants.MERCHANT_INFO_FOR_LOGIN, phone,appType);
			final  Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("ids", merchant_id);
			paramMap.put("indexName", "merchantindex");
		    new Thread(new Runnable() {			
				@Override
				public void run() {
					String res = HttpUtil.httpClientPost(Constant.WEB_SERACH_URL+"delDocument", paramMap);
					System.out.println("当前时间："+new Date()+"resp="+res);
				}
			}).start();
			System.out.println("========flushMerchant end=====");
		} catch (Exception e) {
			log.error("flushMerchant failed,because of"+e.getMessage(),e);
		}
	}
	
	/**
	 * 重建商户索引
	 */
	public static void rebuildMerchantIndex(){
		System.out.println("========rebuildMerchantIndex start=====");
		try{
		    Map<String,Object> paramMap = new HashMap<String,Object>();
		    HttpUtil.httpClientPost(Constant.WEB_SERACH_URL+"rebuildMerchantIndex", paramMap);
			System.out.println("========flushMerchant end=====");
		} catch (Exception e) {
			log.error("rebuildMerchantIndex failed,because of"+e.getMessage(),e);
		}
	}
	
	
	public static int getUserBalance(Long userId){
		String sql = "SELECT t.balance FROM authority_user_info t WHERE t.id="+userId;
		return Db.queryInt(sql);
	}
	
	/**
	 * 刷新系统图片缓存数据(flag为true手动刷新版本号增加，未false启动版本号不增加)
	 */
	public static void flushImageCache(String type,boolean flag){
		List<Record> list = new ArrayList<Record>();
		StringBuffer sb = new StringBuffer();
		System.out.println("========flushImageCache start=====");
		if(type.equals(CacheConstants.IMAGE_CACHE.ORDER_ICON)||type.equals(CacheConstants.IMAGE_CACHE.SHOW_ICON)){
		    // 刷新版本号
			String typeVersion = type+CacheConstants.IMAGE_CACHE.VERSION;
			if(flag){
				Db.update("UPDATE  data_version SET version=version+1 WHERE is_del=0 AND data_type=?",typeVersion);
			}
			int version = Db.queryInt("SELECT t.version FROM data_version t WHERE t.is_del=0 AND t.data_type=?", typeVersion);
			imageCacheService.setImageVersionCache(typeVersion, version);
            System.out.println("image cache version of "+typeVersion+" is:"+imageCacheService.getImageVersionCache(typeVersion));
			
			// 服务类型图片（展示和订单）
			sb.append("SELECT * FROM service_type_attachment t WHERE t.is_del=0 AND t.attachment_style='").append(type).append("'");
			list = Db.find(sb.toString());
			if(list!=null&&list.size()>0){
				String sti = "";
				String path = "";
				Map<String,String> map = new HashMap<String,String>();
				for(Record record : list){
					sti = StringUtil.null2Str(record.getLong("service_type_id"));
					path  = record.getStr("path");
					map.put(sti, path);
				}
				imageCacheService.setImageMapCache(type, map);
			}
		}
		System.out.println("========flushImageCache end=====");
	}
	
	public static void initIpCityCache() {
		TreeMap<Long, String[]> ipTreeMap = new TreeMap<Long, String[]>();
		//HashMap<Long, String[]> ipHashMap = new HashMap<Long, String[]>();
		try {
			ipTreeMap.clear();
			//ipHashMap.clear();
			String sql = "SELECT * FROM ip_address ";
			List<Record> list = Db.find(sql);
			if(list!=null&&list.size()>0){
				Long start = 0L;
				Long end = 0L;
				String province = "";
				String city = "";
				String country = "";
				for(Record record : list){
					start = record.getLong("start");
					end = record.getLong("end");
					province = record.getStr("province");
					city = record.getStr("city");
					country = record.getStr("country");
					ipTreeMap.put(start, new String[] {StringUtil.null2Str(end).trim(), province.trim(), city.trim(),country.trim() });
					//ipHashMap.put(start, new String[] {StringUtil.null2Str(end).trim(), province.trim(), city.trim(),country.trim() });
				}
			}
			commonCacheService.setObject(ipTreeMap, CacheConstants.IP_CITY_TREE_MAP);
			//commonCacheService.setObject(ipHashMap, "ip_city_tree_map");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static void handleDbData(){
		System.out.println("========handleDbData start=====");
		try{
//			List<Record> list = Db.find("SELECT * FROM vouchers_info t WHERE 1=1");
//			if(list!=null&&list.size()>0){
//				for(Record r : list){
//					String icon_path = r.getStr("icon_path");
//					if(!StringUtil.isNullStr(icon_path)){
//						if(icon_path.contains("/resource")){
//							icon_path = icon_path.replace("/resource", "/manFile/image");
//							r.set("icon_path", icon_path);
//							Db.update("vouchers_info", r);
//						}
//					}
//				}
//			}
			System.out.println("========handleDbData end=====");
		} catch (Exception e) {
			log.error("init handleDbData failed,because of"+e.getMessage(),e);
		}
	}
	

	public static void testBatchInsert(){
//		System.out.println("批量插入测试开始");
//        long s = System.currentTimeMillis();
//        int index = 0;
//		for(int i = 0;i<5000;i++){
//			Record record = new Record();
//			String  st=StringUtil.null2Str(Constant.serviceTypeMap.get("ams_yyt"+"锦旗"));
//			   if(!StringUtil.isNullStr(st)){
//				Long service_type = StringUtil.nullToLong(st);
//				record.set("keyword", "testddd").set("service_type", service_type).set("app_type", "ams_yyt")
//				.set("service_type_name", "锦旗").set("url", "").set("img_path", "");
//				Db.save("app_key_words_bak", record);
//				index++;
//			   }
//
//		}
//	    long e = System.currentTimeMillis();
//        System.out.println("批量插入测试结束,成功"+index+"条，耗时 ："+(e-s)+"ms");
		Connection conn = DBUtil.getConnection();
		if(conn==null){
			return;
		}
		String result = "";
        long s = System.currentTimeMillis();
		try {
			    System.out.println("批量插入测试开始");
			    String keyword = "带老人去走走";
			    String service_type_name = "老人长辈";
			    String app_type = "lxz";
			    String st = StringUtil.null2Str(Constant.serviceTypeMap.get(app_type+service_type_name));
			    Long service_type = 0L;
			    if(!StringUtil.isNullStr(st)){
			   			service_type = StringUtil.nullToLong(st);
			    }
			    String sql="insert into app_key_words_bak(keyword,service_type,service_type_name,app_type,url,img_path) values(?,?,?,?,?,?)"; 
				PreparedStatement prest = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);  
				for(int i = 0;i<5000;i++){
					prest.setString(1, keyword); // keyword
					prest.setLong(2, service_type); // service_type
					prest.setString(3, service_type_name); // service_type_name
					prest.setString(4, app_type); // app_type
					prest.setString(5, ""); // url
					prest.setString(6, ""); // img_path
					prest.addBatch();
				}
				prest.executeBatch();
				conn.commit();
			    ResultSet rs = prest.getGeneratedKeys(); //获取结果  
			   // List<Long> list = new ArrayList<Long>();   
			    while(rs.next()) {  
			       //  list.add(rs.getLong(1));//取得ID
			         result = result +"|"+ rs.getLong(1);
			    }  
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		long e = System.currentTimeMillis();
        System.out.println("插入的ID为 ："+ result);
        System.out.println("批量插入测试结束，耗时 ："+(e-s)+"ms");
	}
	
	public static void flushCataCache(String id) {
		String sql="select ca.catalog_id from catalog_service ca where ca.service_id=?";
		List<Record> res=Db.find(sql,id);
		String cata_id="";
		if(res!=null&&res.size()>0){
			cata_id=getRootCataId(res.get(0).getInt("catalog_id"));
			commonCacheService.deleteObject(CacheConstants.CATALOG_SERVICE_TYPE_LIST, cata_id);
		}
		
	}

	private static String getRootCataId(int cata_id) {
		String sql="select * from catalog c where c.id=?";
		List<Record> res=Db.find(sql,cata_id);
		if(res!=null&&res.size()>0){
			int level=res.get(0).getInt("level");
			if(level==0){
				return res.get(0).getInt("id").toString();
			}else{
				return getRootCataId(res.get(0).getInt("parentid"));
			}
		}
		return null;
	}
	
	public static void flushCataOrderCache(String id) {
		String sql="select ca.catalog_id from catalog_service_for_order ca where ca.service_id=?";
		List<Record> res=Db.find(sql,id);
		String cata_id="";
		if(res!=null&&res.size()>0){
			cata_id=getRootOrderCataId(res.get(0).getInt("catalog_id"));
			commonCacheService.deleteObject(CacheConstants.ORDER_CATALOG_SERVICE_TYPE_LIST, cata_id);
		}
		
	}

	private static String getRootOrderCataId(int cata_id) {
		String sql="select * from catalog_for_order c where c.id=?";
		List<Record> res=Db.find(sql,cata_id);
		if(res!=null&&res.size()>0){
			int level=res.get(0).getInt("level");
			if(level==0){
				return res.get(0).getInt("id").toString();
			}else{
				return getRootCataId(res.get(0).getInt("parentid"));
			}
		}
		return null;
	}
	
	public static void writeMqFailure(long id,String msg,String queueName,int type) {
		Record re=new Record();
		re.set("business_id", id).set("msg", msg).set("queueName", queueName).set("type", type).set("status", 0).set("join_time", new Date());
		Db.save("mq_send_failure", re);
		
	}
	
}
