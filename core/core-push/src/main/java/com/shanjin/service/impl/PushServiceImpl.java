package com.shanjin.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.CacheConstants;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;

import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.IMerchantCacheService;
//import com.shanjin.cache.CacheConstants;
//import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.CalloutServices;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.PositionUtil;
import com.shanjin.common.util.SmsUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IPushDao;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IDictionaryService;
import com.shanjin.service.IElasticSearchService;
import com.shanjin.service.IPushService;
import com.shanjin.util.PushConfig;
import com.shanjin.util.PushManager;

@Service("pushService")
public class PushServiceImpl implements IPushService {
	private static final Logger logger = Logger.getLogger(PushServiceImpl.class);

	@Resource
	private IPushDao pushDao;
	@Resource
	private ICommonCacheService commonCacheService;
	
	@Resource
	private IElasticSearchService elasticSearchService;
	
	@Resource
	private IMerchantCacheService  merchantCacheService;	
	
	@Resource
	private IDictionaryService  dictionaryService;
	
    @Resource
    private ICommonService commonService;
	
	@Resource
	RabbitTemplate   orderTemplate;
	
	//获取配置信息 
	Map<String,Object> configMap=null;
	
	//获取配置信息 
	Map<String,Object> assistantConfigMap=null;
	
	int isOpenMqPush= 0;	
	
	
	private static final String  ASSISTANT_APP="assistant";
	
	@Override
	public void basicPush(Map<String, Object> paras){
		try{
			int pushType=StringUtil.nullToInteger(paras.get("pushType"));
			String msg=JSONObject.toJSONString(paras);
			//获取配置参数
			configMap=getConfigurationInfoByKey();
			//是否开启MQ消息队列推送
			isOpenMqPush=configMap.get("is_open_mqPush")==null?0:StringUtil.nullToInteger(configMap.get("is_open_mqPush"));//默认开启
			System.out.println("是否开启MQ消息队列推送："+isOpenMqPush);
			System.out.println("推送类型："+pushType);
			switch (pushType) {
				case 1://订单推送
					if(isOpenMqPush==1){//消息队列推送	
						writeToMQ("newOrderExchange",msg);
						BusinessUtil.writeLog("pushOrder","写入MQ的推送订单参数"+msg);
					}else if (isOpenMqPush==0){//直接推送
						asyncOrderPush(paras);
					}else{//直接推送
						asyncOrderPush(paras);
					}
					break;
					
				case 8://多终端推送
					if(isOpenMqPush==1){//消息队列推送
						writeToMQ("newOrderExchange",msg);
					}else if (isOpenMqPush==0){//直接推送
						asyncRepeatLoginPush(paras);
					}else{//直接推送
						asyncRepeatLoginPush(paras);
					}
					break;
					
				case 20://私人助理推送
					if(isOpenMqPush==1){//消息队列推送
						writeToMQ("newOrderExchange",msg);
					}else if (isOpenMqPush==0){//直接推送
						asyncPrivateAssistantPush(paras);
					}else{//直接推送
						asyncPrivateAssistantPush(paras);
					}
					break;
				case 200: //推送给私人助理APP
					if(isOpenMqPush==1){//消息队列推送
						writeToMQ("newOrderExchange",msg);
					}else{ 
					    //直接推送
						asyncPushOrderToAssistants(paras);
					}
					break;
				default://公共推送		
					if(isOpenMqPush==1){//消息队列推送
						writeToMQ("newOrderExchange",msg);
					}else if (isOpenMqPush==0){//直接推送
						asyncCommonPush(paras);
					}else{//直接推送
						asyncCommonPush(paras);
					}
					break;
				}
		}catch (Exception e){
			BusinessUtil.writeLog("push","推送异常："+e.getMessage());	
			e.printStackTrace();			
		}
		
	}
	/**
	 * 异步调用commonPush
	 * @param paras
	 */
	@Override
	public void asyncCommonPush(Map<String, Object> paras){
		AsyncPush push = new AsyncPush(paras,1);
		CalloutServices.executor(push);
	}

	/**
	 * 异步调用repeatLoginPush
	 * @param paras
	 */
	@Override
	public void asyncRepeatLoginPush(Map<String, Object> paras){
		AsyncPush push = new AsyncPush(paras,2);
		CalloutServices.executor(push);
	}

	/**
	 * 异步调用orderPush
	 * @param paras
	 */
	@Override
	public void asyncOrderPush(Map<String, Object> paras){
		AsyncPush push = new AsyncPush(paras,3);
		CalloutServices.executor(push);		
	}
	/**
	 * 异步调用orderPush
	 * @param paras
	 */
	@Override
	public void asyncPrivateAssistantPush(Map<String, Object> paras){
		AsyncPush push = new AsyncPush(paras,4);
		CalloutServices.executor(push);		
	}

	/**
	 * 私人助理推送
	 * @param paras
	 */
	@Override
	public void asyncPushOrderToAssistants(Map<String, Object> paras)
			throws Exception {
		AsyncPush push = new AsyncPush(paras,5);
		CalloutServices.executor(push);	
	}
	/**
	 * commonPush 线程
	 * @author Administrator
	 *
	 */
	class AsyncPush implements Runnable {
//		 private IPushDao pushDao;
		 private Map<String, Object> paras ;
		 private int type ;
		 
		public AsyncPush(Map<String, Object> paras,int type){
//				this.pushDao = pushDao;
				this.paras = paras;
				this.type = type;
		}
		
		@Override
		public void run() {
			try {
				if(type==1){
					commonPush(paras);
				}else if (type==2){
					repeatLoginPush(paras);
				}else if (type==3){
					orderPush(paras);
				}else if (type==4){
					privateAssistantPush(paras);
				}else if (type==5){
					//推送给商店所属的私人助理  -Revoke 2016.10.28
					pushOrderToAssistants(paras);
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 共通推送
	 * 
	 * */
	@Override
	public JSONObject commonPush(Map<String, Object> paras) throws Exception {
		//需要的参数userId,orderId,merchantId,pushType
		long time=System.currentTimeMillis();
		Long userId=StringUtil.nullToLong(paras.get("userId")==null?0:paras.get("userId"));
		BusinessUtil.writeLog("push",userId+"-"+DateUtil.getNowTime()+"开始commonPush推送");
		BusinessUtil.writeLog("push",userId+"-"+"推送类型："+paras.get("pushType"));
		
		//查询推送对象
		List<Map<String, Object>> onlineMerchantList =this.pushDao.getUserClientId(paras);
		BusinessUtil.writeLog("push",userId+"-"+"查询到的所有推送对象："+JSONObject.toJSONString(onlineMerchantList));		
		if(onlineMerchantList==null || onlineMerchantList.size()==0){
			logger.info("commonPush："+"没有查询到推送对象");			
			return null;
		}
		
		//获取配置信息
		if(configMap==null){
			configMap=getConfigurationInfoByKey();
		}
		
        System.out.println("是否是私人助理抢单："+paras.get("isPrivateAssistant"));
		JSONObject jobj=PushManager.push(configMap,onlineMerchantList, paras,"userId");		
		
		//删除在线记录
		int pushType=StringUtil.nullToInteger(paras.get("pushType"));
		if(pushType==6 || pushType==8){//删除员工，多终端登录，老板员工身份转换	
			pushDao.deleteUserPushByUserId(paras);
		}
		BusinessUtil.writeLog("push",userId+"-推送所用时间："+(System.currentTimeMillis()-time)+"\n");
		return jobj;

	}
	
	/**
	 * 重复登录推送
	 * @param param    
	 * void   
	 * @throws
	 */
	@Override
	public JSONObject repeatLoginPush(Map<String, Object> param)throws Exception{

		Long userId=StringUtil.nullToLong(param.get("userId"));
		BusinessUtil.writeLog("push",userId+"-"+DateUtil.getNowTime()+"开始多终端登录推送");
		JSONObject jobj=new JSONObject();

		List<Map<String, Object>> userPushList = pushDao.selectUserPushList(param); // 查询用户的推送设备
		BusinessUtil.writeLog("push",userId+"-"+"多终端登录用户："+JSONObject.toJSONString(userPushList));
		List<Map<String, Object>> androidPushInfoList = new ArrayList<Map<String, Object>>();// 安卓终端
		List<Map<String, Object>> iosPushInfoList = new ArrayList<Map<String, Object>>(); // ios终端
		if(userPushList!=null && userPushList.size() >0){	
			
			for(Map<String, Object> map : userPushList){
				String clientId = StringUtil.null2Str(map.get("clientId"));
				if(!param.get("clientId").equals(clientId)){
					// 非当前访问的设备，需推送通知下线
					int clientType = StringUtil.nullToInteger(map.get("clientType"));
					if(clientType==Constant.PUSH_CLIENT_TYPE_ANDROID){
						androidPushInfoList.add(map);
					}else if(clientType==Constant.PUSH_CLIENT_TYPE_IOS){
						iosPushInfoList.add(map);
					}
				}
			}

			//获取配置信息
			if(configMap==null){
				configMap=getConfigurationInfoByKey();
			}
			String androidPushInfo=PushManager.androidPush(configMap,androidPushInfoList,param,"userId"); 
			String iosPushInfo=PushManager.iosPush(configMap,iosPushInfoList,param);
			jobj.put("androidPushInfo", androidPushInfo);
			jobj.put("iosPushInfo", iosPushInfo);
			
		}
		
		//如果是小米的手机，则pushId需要解码
		String phoneModel=StringUtil.null2Str(param.get("phoneModel"));
		if(StringUtil.isNotEmpty(phoneModel) && phoneModel.toLowerCase().contains("xiaomi")){
			String pushId=StringUtil.null2Str(param.get("pushId"));
			pushId=java.net.URLDecoder.decode(pushId,   "utf-8");
			param.put("pushId", pushId);
		}
		
		//在线信息更新
		int count=pushDao.getUserPushCountByUserId(param);
		if(count==0){			
			// 上报推送设备信息
			pushDao.insertUserPush(param);
		}else if (count==1){
			pushDao.updateUserPush(param);
		}else{			
			// 删除已存在的推送设备
			pushDao.deleteUserPushByUserIdOrClientId(param);
			// 上报推送设备信息
			pushDao.insertUserPush(param);
		}
		return jobj;
	}
	/**
	 * 订单推送
	 * 
	 * */
	@Override
	public JSONObject orderPush(Map<String, Object> paras) throws Exception {	
		long time=System.currentTimeMillis();
		
		Long orderId = StringUtil.nullToLong(paras.get("orderId"));
		int isImmediate = StringUtil.nullToInteger(paras.get("isImmediate"));
		String serviceTime = StringUtil.null2Str(paras.get("serviceTime"));
		int serviceTypeId=StringUtil.nullToInteger(paras.get("serviceTypeId"));
		String phone = StringUtil.null2Str(paras.get("phone"));
		String pushWay = StringUtil.null2Str(paras.get("pushWay"));
		String province= StringUtil.null2Str(paras.get("province"));
		String city= StringUtil.null2Str(paras.get("city"));
		String[] provinceAndCity = null;
		provinceAndCity = BusinessUtil.handlerProvinceAndCity(province, city);
		province=provinceAndCity[0];
		city=provinceAndCity[1];
		paras.put("province", province);
		paras.put("city", city);

		BusinessUtil.writeLog("push",orderId+"-"+DateUtil.getNowYYYYMMDDHHMMSS()+"开始发送订单");
		BusinessUtil.writeLog("push",orderId+"-订单推送参数："+JSONObject.toJSONString(paras));
		
		//测试号发单只能测试号收到
		if(phone.startsWith(Constant.TESTPHONE)){
			paras.put("testPhone", Constant.TESTPHONE);
		}

		//获取配置信息 
		if(configMap==null){
			configMap=getConfigurationInfoByKey();
		}
		
		//是否开启自己发单自己抢单
		String isOpenBrushOrder=configMap.get("is_open_brushOrder")==null?"0":configMap.get("is_open_brushOrder").toString();//默认开启
		if(isOpenBrushOrder.equals("1")){//开启刷单
			paras.put("merchantId", null);//用户非搜索引擎过滤自己
		}
		paras.put("isOpenBrushOrder", isOpenBrushOrder);//用户搜索殷勤过滤自己
		BusinessUtil.writeLog("push",orderId+"-是否开启自己发单自己抢单："+isOpenBrushOrder);
		
		//配置信息中获取经纬度推送的数量
		String rangePushLimit=configMap.get("rangePush_limit")==null?"100":configMap.get("rangePush_limit").toString();
		if(!rangePushLimit.equals("0")){
			paras.put("rangeLimit", rangePushLimit);
		}
		BusinessUtil.writeLog("push",orderId+"-经纬度推送的数量："+rangePushLimit);
		
		//配置信息中获取城市推送的数量
		String cityPushLimit=configMap.get("cityPush_limit")==null?"100":configMap.get("cityPush_limit").toString();
		if(!cityPushLimit.equals("0")){
			paras.put("cityLimit", cityPushLimit);
		}
		BusinessUtil.writeLog("push",orderId+"-城市推送的数量："+cityPushLimit);
		
		//配置信息中获取N天未登录则不推送订单
		String pushNotLoginDays=configMap.get("push_notLogin_days")==null?"7":configMap.get("push_notLogin_days").toString();
		BusinessUtil.writeLog("push",orderId+"-N天未登录则不推送订单："+pushNotLoginDays);	
		
		//配置信息中获取是否开启搜索引擎查询周围商户
		int is_open_search_pushOrder=configMap.get("is_open_search_pushOrder")==null?1:StringUtil.nullToInteger(configMap.get("is_open_search_pushOrder"));	
		BusinessUtil.writeLog("push",orderId+"-是否开启搜索引擎查询周围商户："+is_open_search_pushOrder);	
		
		List<Map<String, Object>> allMerchantList = new ArrayList<Map<String, Object>>();// 所有商户(包含老板和员工)
		List<Map<String, Object>> allVIPMerchantList = new ArrayList<Map<String, Object>>();// 所有商户(包含老板和员工)
		List<Map<String, Object>> onlineMerchantList = new ArrayList<Map<String, Object>>();// 在线商户

		List<Integer> serviceTypeIdList= getPushServiceTypeIdList(serviceTypeId);
		paras.put("serviceTypeIdList", serviceTypeIdList);
		BusinessUtil.writeLog("push",orderId+"-推送关联服务类型："+JSONObject.toJSONString(serviceTypeIdList));	
		
		if(pushWay.equals("")){
			pushWay="1";
		}
		if ("1".equals(pushWay)) {// 正常保存订单推送
			String pushRange ="";
			String orderPushType ="";
			// 获取推送范围和推送类型
			Map<String, Object> pushInfo = this.getPushRange(serviceTypeId);
			if(pushInfo==null){
				BusinessUtil.writeLog("push",orderId+"-没有配置推送距离："+serviceTypeId);	
				pushRange ="3";
				orderPushType ="0";
			}else{
				pushRange =pushInfo.get("pushRange") == null ? "3" : StringUtil.null2Str(pushInfo.get("pushRange"));//推送距离
				orderPushType =pushInfo.get("pushType") == null ? "0" : StringUtil.null2Str(pushInfo.get("pushType"));// 推送方式
				BusinessUtil.writeLog("push",orderId+"-推送距离："+pushRange);	
				BusinessUtil.writeLog("push",orderId+"-推送方式："+orderPushType);	
			}
			paras.put("pushRange", pushRange);
			paras.put("orderPushType", orderPushType);
			
			if (orderPushType.equals("0")) {
				if(rangePushLimit.equals("0")){
					return null;
				}
				
				//查询VIP商户
//				int vipCount=0;
				allVIPMerchantList=pushDao.getAllVIPMerchantByCity(paras);						
				BusinessUtil.writeLog("push",orderId+"-查询"+city+"VIP数量："+(allVIPMerchantList==null?0:allVIPMerchantList.size())+",商户信息："+JSONObject.toJSONString(allVIPMerchantList));	

//				if(allVIPMerchantList!=null ){
//					vipCount=allVIPMerchantList.size();
//				}
				//剩余商户数量
//				rangePushLimit=""+(StringUtil.nullToInteger(rangePushLimit)-vipCount);
//				paras.put("rangeLimit", rangePushLimit);
//				BusinessUtil.writeLog("push",orderId+"-剩余商户数量："+rangePushLimit);	
							
				if(StringUtil.nullToInteger(rangePushLimit)>0){					
					//是否开启所搜引擎查找商户
					if(is_open_search_pushOrder==1){						
						// 按照经纬度搜索引擎查询
						allMerchantList= this.getAllMerchantByRangeForSearch(paras,allVIPMerchantList);	
						BusinessUtil.writeLog("push",orderId+"-搜索引擎经纬度查到的商户数量："+(allMerchantList==null?0:allMerchantList.size())+",商户信息："+JSONObject.toJSONString(allMerchantList));	
	
						if(allMerchantList==null || allMerchantList.size()==0){
							//按照经纬度数据库查询
							allMerchantList = this.getAllMerchantByRange(paras);
							BusinessUtil.writeLog("push",orderId+"-数据库经纬度查到的商户数量："+(allMerchantList==null?0:allMerchantList.size())+",商户信息："+JSONObject.toJSONString(allMerchantList));	
						}
					}else{
						//按照经纬度数据库查询
						allMerchantList = this.getAllMerchantByRange(paras);
						BusinessUtil.writeLog("push",orderId+"-数据库经纬度查到的商户数量："+(allMerchantList==null?0:allMerchantList.size())+",商户信息："+JSONObject.toJSONString(allMerchantList));	
						if(allMerchantList==null || allMerchantList.size()==0){
							//按照经纬度数据库查询
							allMerchantList = this.getAllMerchantByRangeForSearch(paras,allVIPMerchantList);
							BusinessUtil.writeLog("push",orderId+"-搜索引擎经纬度查到的商户数量："+(allMerchantList==null?0:allMerchantList.size())+",商户信息："+JSONObject.toJSONString(allMerchantList));	
						}
					}
				}
				//合并VIP商户和普通商户
				mergeMerchant(allVIPMerchantList,allMerchantList);
				
				if (allMerchantList == null || allMerchantList.size() < 1) {
					BusinessUtil.writeLog("push",orderId+"-经纬度没查询到，根据省市查询："+province+","+city);	

					// 按照城市推查询
					if(StringUtil.isEmpty(province) || StringUtil.isEmpty(city)){
						Thread.sleep(5000);//查询订单数据，5s停顿，防止没有同步
						Map<String, Object> map = this.pushDao.getProvinceAndCityByorderId(orderId);
						if (map != null) {
							province = StringUtil.null2Str(map.get("province"));
							city = StringUtil.null2Str(map.get("city"));
							provinceAndCity = BusinessUtil.handlerProvinceAndCity(province, city);
							province=provinceAndCity[0];
							city=provinceAndCity[1];
						}
					} 
					paras.put("province", province);
					paras.put("city", city);
					allMerchantList = this.getAllMerchantByCity(paras);						
					BusinessUtil.writeLog("push",orderId+"-数据库城市查到的商户数量："+(allMerchantList==null?0:allMerchantList.size())+",商户信息："+JSONObject.toJSONString(allMerchantList));	
				}
				
				//如果城市也没有推送到，则推送到私人助理
				if (allMerchantList == null || allMerchantList.size() < 1) {
					if(cityPushLimit.equals("0")){
						return null;
					}
					
					BusinessUtil.writeLog("push",orderId+"-城市没查询到，推送："+province+","+city);	
					Map<String,Object> orderMap=new HashMap<String, Object>();   
					orderMap.put("orderId", orderId);  
					orderMap.put("city", city); 
					orderMap.put("serviceTime", serviceTime);
					
					List<Map<String,Object>> pushOrderList=new ArrayList<Map<String,Object>>();
					pushOrderList.add(orderMap);
					
					Map<String,Object> pushMap=new HashMap<String, Object>();    
				    pushMap.put("pushOrderList",pushOrderList);      
				    pushMap.put("data", "");
				    pushMap.put("pushType", 20);					
				    basicPush(pushMap);
				    return null;
				}
				
			} else if (orderPushType.equals("1")) {
				if(cityPushLimit.equals("0")){
					return null;
				}
				
				allVIPMerchantList=pushDao.getAllVIPMerchantByCity(paras);						
				BusinessUtil.writeLog("push",orderId+"-查询"+city+"VIP数量："+(allVIPMerchantList==null?0:allVIPMerchantList.size())+",商户信息："+JSONObject.toJSONString(allVIPMerchantList));	

				// 按照城市推查询
				if(StringUtil.isEmpty(province) || StringUtil.isEmpty(city)){
					Thread.sleep(5000);//查询订单数据，5s停顿，防止没有同步
					Map<String, Object> map = this.pushDao.getProvinceAndCityByorderId(orderId);
					if (map != null) {
						province = StringUtil.null2Str(map.get("province"));
						city = StringUtil.null2Str(map.get("city"));
						provinceAndCity = BusinessUtil.handlerProvinceAndCity(province, city);
						province=provinceAndCity[0];
						city=provinceAndCity[1];
					}
				}
				if (StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city)) {					
					paras.put("province", province);
					paras.put("city", city);
					allMerchantList = this.getAllMerchantByCity(paras);
					BusinessUtil.writeLog("push",orderId+"-数据库城市查到的商户数量："+(allMerchantList==null?0:allMerchantList.size())+",商户信息："+JSONObject.toJSONString(allMerchantList));	
				}
				//合并VIP商户和普通商户
				mergeMerchant(allVIPMerchantList,allMerchantList);
				
			} else if (orderPushType.equals("2")) {//个性服务查询				
				BusinessUtil.writeLog("push",orderId+"-暂时没有个性服务标签推送");	
				return null;
			} else if (orderPushType.equals("3")) {// 推送给所有
				provinceAndCity = BusinessUtil.handlerProvinceAndCity(province, city);
				if (StringUtils.isNotEmpty(province) && StringUtils.isNotEmpty(city)) {
					// 如果城市不为空，则按照城市推送
					paras.put("province", provinceAndCity[0]);
					paras.put("city", provinceAndCity[1]);
					allMerchantList = this.getAllMerchantByCity(paras);
				} else {// 如果城市为空，则推送给所有商户
					allMerchantList = this.getAllMerchant(paras);
				}
			} else {// 其他

			}			

		} else if ("2".equals(pushWay)) {// 快速预约推送
			BusinessUtil.writeLog("push",orderId+"-暂时没有一对一发单推送");	
			return null;
		}
		
		// 去重复数据,判断商户最后登录时间是大于设置的天数，如果大于则发送短信
		allMerchantList = this.HandlerPushMerchants(allMerchantList, pushNotLoginDays,serviceTypeId,orderId);
		
		// 如果没有推送对象，则直接返回
		if (allMerchantList == null || allMerchantList.size() == 0) {
			BusinessUtil.writeLog("push",orderId+"-无推送服务商,订单号为："+orderId);	
			return null;
		}
		BusinessUtil.writeLog("push",orderId+"-去重复之后商户数量："+(allMerchantList==null?0:allMerchantList.size())+",商户信息："+JSONObject.toJSONString(allMerchantList));	
		
		// 查询在线商户
		onlineMerchantList = this.getOnlineMerchant(allMerchantList);
		if (onlineMerchantList == null || onlineMerchantList.size() == 0) {
			BusinessUtil.writeLog("push",orderId+"-无推送服务商,订单号为："+orderId);				
		}
		BusinessUtil.writeLog("push",orderId+"-在线商户数量"+(onlineMerchantList==null?0:onlineMerchantList.size())+",商户信息："+JSONObject.toJSONString(onlineMerchantList));
		
		//合并老板和员工，插入到push_merchant_order
		allMerchantList=mergeBossAndEmployee(allMerchantList);
		BusinessUtil.writeLog("push",orderId+"-合并老板和员工后商户后商户数量："+(allMerchantList==null?0:allMerchantList.size())+",商户信息："+JSONObject.toJSONString(allMerchantList));
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderId", orderId);
		paramMap.put("serviceTime", serviceTime);
		paramMap.put("clientIdList", allMerchantList);
		if (allMerchantList != null && allMerchantList.size() > 0) {
			// 将查询到的商户插入商户-订单表
			this.pushDao.insertPushMerchantOrder(paramMap);
			
			List<Map<String,Object>> pushOrderInfos = new ArrayList<Map<String,Object>>();
			for(Map<String, Object> merchant : allMerchantList){
				Map<String,Object> each= new HashMap<String,Object>();
				each.put("merchantId", merchant.get("merchantId"));
				each.put("orderId", orderId);
				each.put("serviceTime", serviceTime);
				each.put("orderStatus",1);
				each.put("joinTime", DateUtil.getNowYYYYMMDDHHMMSS());
				each.put("isImmediate", isImmediate);
				pushOrderInfos.add(each);				
			}
			merchantCacheService.cachePushOrderForAllMerchants(pushOrderInfos,orderId.toString());			
		}
		
		// 推送
		JSONObject jobj=PushManager.push(configMap,onlineMerchantList, paras,"userId");		
		BusinessUtil.writeLog("push",orderId+"-推送所用时间："+(System.currentTimeMillis()-time)+"\n");
		
		return jobj;

	}
	
	/**
	 * 私人助理推送
	 */
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject privateAssistantPush(Map<String, Object> paras) throws Exception{
		
		long time=System.currentTimeMillis();
		JSONObject jobj=null;
		try{	

			BusinessUtil.writeLog("push","privateAssistantPush-开始私人助理推送："+DateUtil.getNowTime());
			
			if(paras==null || paras.size()==0){
				BusinessUtil.writeLog("push","privateAssistantPush-推送参数为空："+paras);
				return null;
			}
			
			List<Map<String, Object>> pushOrderList=(List<Map<String, Object>>)paras.get("pushOrderList");
			BusinessUtil.writeLog("push","privateAssistantPush-共有："+(pushOrderList==null?0:pushOrderList.size()+"个订单需要推送给私人助理"));
			BusinessUtil.writeLog("push","privateAssistantPush-订单为："+JSONObject.toJSONString(pushOrderList));
			
			if(pushOrderList==null || pushOrderList.size()==0){
				return null;
			}
			
			//查询订单涉及到了那些城市
			String orderIds="";
			List<String> cityList=new ArrayList<String>();
			for(Map<String, Object> orderMap:pushOrderList){
				Long orderId=StringUtil.nullToLong(orderMap.get("orderId"));
				String city=StringUtil.null2Str(orderMap.get("city"));
				if (Collections.frequency(cityList, city) < 1){//没有重复的数据				
					cityList.add(city);
				}
				orderIds+=orderId+",";
			}
			if(!orderIds.equals("")){
				orderIds=orderIds.substring(0,orderIds.length()-1);
			}
			
			BusinessUtil.writeLog("push","privateAssistantPush-此次推送涉及到的城市有："+(cityList==null?"":cityList.toString()));
			if(cityList==null || cityList.size()==0){
				return null;
			}
			
			//查询城市私人助理
			Map<String, Object> cityPrivateAssistantMap=new HashMap<String, Object>();
			for(String city:cityList){
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("orderIds", orderIds);//过滤已经有此订单的私人助理
				paramMap.put("city", city);
				List<Map<String, Object>> privateAssistantList=pushDao.getCityPrivateAssistants(paramMap);	
				BusinessUtil.writeLog("push","privateAssistantPush-"+city+"共有："+(privateAssistantList==null?0:privateAssistantList.size()+"个私人助理"));		
				if(privateAssistantList==null || privateAssistantList.size()==0){
					continue;
				}
				cityPrivateAssistantMap.put(city, privateAssistantList);
			}
			if(cityPrivateAssistantMap.size()==0){
				return null;
			}
			
			//将订单匹配到所属城市的私人助理
			for(Map<String, Object> orderMap:pushOrderList){
				Long orderId=StringUtil.nullToLong(orderMap.get("orderId"));
				String city=StringUtil.null2Str(orderMap.get("city"));
				String serviceTime=StringUtil.null2Str(orderMap.get("serviceTime"));
				int isImmediate = StringUtil.nullToInteger(paras.get("isImmediate"));
				
				List<Map<String, Object>> privateAssistantList=(List<Map<String, Object>>)cityPrivateAssistantMap.get(city);			
		
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("orderId", orderId);
				paramMap.put("serviceTime", serviceTime);
				paramMap.put("clientIdList", privateAssistantList);
				if (privateAssistantList != null && privateAssistantList.size() > 0) {
					
					// 将查询到的商户插入商户-订单表
					int i=this.pushDao.insertPushMerchantOrder(paramMap);
					if(i>0){
						BusinessUtil.writeLog("push","privateAssistantPush-订单插入到push_merchant_order成功");		
					}
					
					//插入商户订单缓存
					List<Map<String,Object>> pushOrderInfos = new ArrayList<Map<String,Object>>();
					for(Map<String, Object> merchant : privateAssistantList){
						Map<String,Object> each= new HashMap<String,Object>();
						each.put("merchantId", merchant.get("merchantId"));
						each.put("orderId", orderId);
						each.put("orderStatus",1);
						each.put("isImmediate",isImmediate);
						each.put("joinTime", DateUtil.getNowYYYYMMDDHHMMSS());
						pushOrderInfos.add(each);				
					}
					merchantCacheService.cachePushOrderForAllMerchants(pushOrderInfos,orderId.toString());					
				}	
				
				// 查询在线商户
				List<Map<String, Object>> onlineMerchantList = this.getOnlineMerchant(privateAssistantList);
				if (onlineMerchantList == null || onlineMerchantList.size() == 0) {
					BusinessUtil.writeLog("push","privateAssistantPush-"+orderId+"-无推送服务商,订单号为："+orderId);				
				}
				BusinessUtil.writeLog("push","privateAssistantPush-"+orderId+"-在线私人助理数量"+(onlineMerchantList==null?0:onlineMerchantList.size())+",私人助理信息："+JSONObject.toJSONString(onlineMerchantList));
				
				//获取配置信息 
				if(configMap==null){
					configMap=getConfigurationInfoByKey();
				}
				// 推送
				paras.put("orderId", orderId);
				paras.put("pushType", 1);
				jobj=PushManager.push(configMap,onlineMerchantList, paras,"userId");
			}
		}catch(Exception e){
			String errorStr=ExceptionUtils.getStackTrace(e);
			BusinessUtil.writeLog("push","privateAssistantPush-推送异常："+errorStr);
		}finally{
			BusinessUtil.writeLog("push","privateAssistantPush-推送所用时间："+(System.currentTimeMillis()-time)+"\n");
		}
		return jobj;
	}

	/**
	 * 查找所有商户
	 */
	public List<Map<String, Object>> getAllMerchant(Map<String, Object> paramMap) {
		List<Map<String, Object>> resultList = pushDao.getAllMerchant(paramMap);
		return resultList;
	}

	/**
	 * 根据经纬度查找所有商户
	 */
	public List<Map<String, Object>> getAllMerchantByRange(Map<String, Object> paramMap) throws Exception{
		List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
		Double longitude = StringUtil.nullToDouble(paramMap.get("longitude"));
		Double latitude = StringUtil.nullToDouble(paramMap.get("latitude"));
		long orderId=StringUtil.nullToLong(paramMap.get("orderId"));
		if(longitude==0 || latitude==0){
			Thread.sleep(5000);//查询订单数据，5s停顿，防止没有同步
			Map<String, Object> locationMap = pushDao.selectOrderLocation(orderId);
			if (locationMap != null) {
				if (locationMap.get("longitude") != null && locationMap.get("latitude") != null) {
					longitude = (Double) locationMap.get("longitude");
					latitude = (Double) locationMap.get("latitude");
				}
			} else {
				System.out.println("经纬度：" + longitude + "," + latitude);
			}	
		}
		if(longitude==0 || latitude==0){
			System.out.println("经纬度：" + longitude + "," + latitude);
			return null;
		}
		double pushRange = Double.parseDouble(paramMap.get("pushRange") == null ? "0" : paramMap.get("pushRange").toString());
		Map<String, Double> locationInfo = PositionUtil.calcTopLfRgBtCoordinate(pushRange, longitude, latitude);
		paramMap.put("leftLongitude", locationInfo.get("leftLongitude"));
		paramMap.put("rightLongitude", locationInfo.get("rightLongitude"));
		paramMap.put("buttomLatitude", locationInfo.get("buttomLatitude"));
		paramMap.put("topLatitude", locationInfo.get("topLatitude"));
		resultList = pushDao.getAllMerchantByRange(paramMap);
		
		return resultList;
	}
	/**
	 * 根据搜索引擎查找所有商户
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getAllMerchantByRangeForSearch(Map<String, Object> paramMap,List<Map<String, Object>> allVIPMerchantList) throws Exception {

		List<Map<String, Object>> resultList=new ArrayList<Map<String,Object>>();
		int isOpenBrushOrder=StringUtil.nullToInteger(paramMap.get("isOpenBrushOrder"));
		double longitude = StringUtil.nullToDouble(paramMap.get("longitude"));
		double latitude = StringUtil.nullToDouble(paramMap.get("latitude"));
		long orderId=StringUtil.nullToLong(paramMap.get("orderId"));
		
		if(longitude==0 || latitude==0){//如果参数经纬度错误，则从数据库查询
			Thread.sleep(5000);//查询订单数据，5s停顿，防止没有同步
			Map<String, Object> locationMap = pushDao.selectOrderLocation(orderId);
			if (locationMap != null) {
				if (locationMap.get("longitude") != null && locationMap.get("latitude") != null) {
					longitude = StringUtil.nullToDouble(locationMap.get("longitude"));
					latitude = StringUtil.nullToDouble(locationMap.get("latitude"));
				}
			} 
		}
		if(longitude==0 || latitude==0){
			return null;
		}
		BusinessUtil.writeLog("push",orderId+"-订单经纬度："+longitude+","+latitude);	
		
		double pushRange = paramMap.get("pushRange") == null ? 3 : StringUtil.nullToDouble(paramMap.get("pushRange"));
		List<Integer> serviceTypeIdList=(List<Integer>)paramMap.get("serviceTypeIdList");
		int limit=StringUtil.nullToInteger(paramMap.get("rangeLimit"));
		if(limit==0){
			limit=10000;//搜索引擎默认返回数量
		}
		//根据手机号查询此人下属所有店铺
		String merchantIdsOfSenduser="";
		if(isOpenBrushOrder==0){//开启过滤自己
			merchantIdsOfSenduser=pushDao.getMerchantIdsByPhone(paramMap);
		}
		for(int serviceTypeId : serviceTypeIdList){
			List<Map<String, Object>> merchantList = elasticSearchService.getUserServiceTypeIdNearBy(longitude, latitude, pushRange, serviceTypeId+"", limit,allVIPMerchantList);
			if(merchantList==null || merchantList.size()==0){
				continue;
			}			
			BusinessUtil.writeLog("push",orderId+"-服务类型："+serviceTypeId+"查到的商户数量："+(merchantList==null?0:merchantList.size())+",商户信息："+JSONObject.toJSONString(merchantList));	
			for(Map<String, Object> map : merchantList){
				String merchantId=StringUtil.null2Str(map.get("merchantId"));
				
				//是否过滤自己发单
				if(isOpenBrushOrder==0 && merchantIdsOfSenduser !=null ){//开启过滤自己
					if(merchantIdsOfSenduser.contains(merchantId)){
						continue;
					}
				}
				
				if (Collections.frequency(resultList, map) < 1){//没有重复的数据				
					resultList.add(map);
				}
			}
		}
		
		return resultList;
	} 

	/**
	 * 根据城市查找所有商户
	 */
	public List<Map<String, Object>> getAllMerchantByCity(Map<String, Object> paramMap) {
		List<Map<String, Object>> resultList = pushDao.getAllMerchantByCity(paramMap);
		return resultList;
	}

	

	/**
	 * 查找在线商户
	 */
	public List<Map<String, Object>> getOnlineMerchant(List<Map<String, Object>> allMerchantList) {
		
		if(allMerchantList==null || allMerchantList.size()==0){
			return null;
		}
		
		List<Map<String, Object>> resultList=new ArrayList<Map<String,Object>>();
	
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		//只保留userId
		for(Map<String, Object> map : allMerchantList){
			Object userId=map.get("userId");
			Map<String, Object> map1=new HashMap<String, Object>();
			map1.put("userId", userId);
			list.add(map1);
		}
		if(list.size()==0){
			Map<String, Object> map1=new HashMap<String, Object>();
			map1.put("userId", 0);
			list.add(map1);
		}
		List<Map<String, Object>> onlineList = pushDao.getOnlineMerchant(list);
		if(onlineList==null || onlineList.size()==0){
			return null;
		}
		
		//将不在线的移除掉
		for(Map<String, Object> map1 : allMerchantList){
			String userId1=StringUtil.null2Str(map1.get("userId"));
			for(Map<String, Object> map2 : onlineList){
				String userId2=StringUtil.null2Str(map2.get("userId"));
				if(userId1.equals(userId2)){
					map1.put("pushId", map2.get("pushId"));
					map1.put("clientType", map2.get("clientType"));
					map1.put("phoneModel", map2.get("phoneModel"));
					resultList.add(map1);
				}
			}
		}
		return resultList;
	}
	/**推送的时候查询所有符合条件的服务类型**/
	@SuppressWarnings("unchecked")
	public List<Integer> getPushServiceTypeIdList(int serviceTypeId){
		
		List<Integer> elevanceServiceTypes=new ArrayList<Integer>();
		elevanceServiceTypes.add(serviceTypeId);
		
		List<Map<String,Object>> relevanceServiceTypeIdList=(List<Map<String,Object>>)commonCacheService.getObject(CacheConstants.RELEVANCE_SERVICE_TYPE_ID);
		if(relevanceServiceTypeIdList==null || relevanceServiceTypeIdList.size()==0){
			relevanceServiceTypeIdList=pushDao.getPushServiceTypeIdList();
			commonCacheService.setObject(relevanceServiceTypeIdList,CacheConstants.RELEVANCE_SERVICE_TYPE_ID);
		}
		for(Map<String,Object> map : relevanceServiceTypeIdList){
			int serviceTypeId_=StringUtil.nullToInteger(map.get("serviceTypeId"));
			int relevanceServiceTypeId=StringUtil.nullToInteger(map.get("relevanceServiceTypeId"));
			if(serviceTypeId!=0 && serviceTypeId_!=0 && serviceTypeId==serviceTypeId_){
				elevanceServiceTypes.add(relevanceServiceTypeId);
			}
		}
		return elevanceServiceTypes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> getConfigurationInfoByKey(){
		//推送配置
		Map<String,Object> pushConfigMap=(Map<String, Object>) commonCacheService.getObject(CacheConstants.PUSH_CONFIG_KEY);
		if(pushConfigMap==null){
			pushConfigMap=new HashMap<String, Object>();
			// 读取配置信息缓存
			Map<String, Object> allConfigurationInfoMap = null;
			try{
				allConfigurationInfoMap=(Map<String, Object>) commonCacheService.getObject(CacheConstants.CONFIG_KEY);
			}catch(Exception e){
				commonCacheService.deleteObject(CacheConstants.CONFIG_KEY);
			}
			if (allConfigurationInfoMap == null) {// 如果没有缓存配置信息则读取数据库
				allConfigurationInfoMap=new HashMap<String, Object>();
				List<Map<String,Object>> listConfigurationInfo = pushDao.getConfigurationInfo();
				for(Map<String,Object> map : listConfigurationInfo){
					String configKey=StringUtil.null2Str(map.get("config_key"));
					allConfigurationInfoMap.put(configKey, map);
				}
				commonCacheService.setObject(allConfigurationInfoMap, CacheConstants.CONFIG_KEY);
			}
			
			List<String> keyList=PushConfig.getConfigKeyList();
			for(String key : keyList){
				Map<String, Object> configurationInfoMap=(Map<String, Object>)allConfigurationInfoMap.get(key);
				if(configurationInfoMap==null){
					BusinessUtil.writeLog("push","缓存中没有推送配置项："+key);	
					continue;
				}
				String configKey = StringUtil.null2Str(configurationInfoMap.get("config_key"));
				String configValue = StringUtil.null2Str(configurationInfoMap.get("config_value"));
				String standbyField1 = StringUtil.null2Str(configurationInfoMap.get("standby_field1"));
				String standbyField2 = StringUtil.null2Str(configurationInfoMap.get("standby_field2"));
				if(StringUtil.isNotEmpty(standbyField1)){
					configValue+=","+standbyField1;
				}
				if(StringUtil.isNotEmpty(standbyField2)){
					configValue+=","+standbyField2;
				}
				pushConfigMap.put(configKey, configValue);
			}
			commonCacheService.setObject(pushConfigMap, CacheConstants.PUSH_CONFIG_KEY);
		}
		return pushConfigMap;
	}	
		
	@SuppressWarnings("unchecked")
	public Map<String, Object> getPushRange(int serviceTypeId){
		Map<String,Object> allPpushRangeMap=null;
		try{
			allPpushRangeMap=(Map<String,Object>)commonCacheService.getObject(CacheConstants.PUSH_RANGE);
		}catch(Exception e){
			commonCacheService.deleteObject(CacheConstants.PUSH_RANGE);
		}
		if(allPpushRangeMap==null){
			allPpushRangeMap=new HashMap<String, Object>();
			List<Map<String,Object>> pushRangeList=this.pushDao.getPushRange();
			for(Map<String,Object> map: pushRangeList){
				String serviceTypeId_=StringUtil.null2Str(map.get("serviceTypeId"));
				allPpushRangeMap.put(serviceTypeId_, map);
			}
			commonCacheService.setObject(allPpushRangeMap, CacheConstants.PUSH_RANGE);
		}
		Map<String,Object> pushRangeMap=(Map<String,Object>)allPpushRangeMap.get(StringUtil.null2Str(serviceTypeId));
		return pushRangeMap;
	}
	
	//推送商户处理
	public List<Map<String, Object>> HandlerPushMerchants(List<Map<String, Object>> allMerchantList, String pushNotLoginDays,int serviceTypeId,Long orderId) {

		if(allMerchantList==null || allMerchantList.size()==0){
			return new ArrayList<Map<String,Object>>();
		}			    
		
		String userIds="";
		
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
		for(Map<String,Object> map : allMerchantList){
			userIds+=StringUtil.null2Str(map.get("userId"))+",";
		}
		if(StringUtil.isNotEmpty(userIds)){
			userIds=userIds.substring(0,userIds.length()-1);
		}
		Map<String,Object> paramMap=new HashMap<String, Object>();
		paramMap.put("userIds", userIds);
		//查询商户最后的活跃时间
		List<Map<String, Object>> merchantLastActiveList =pushDao.getMerchantActiveList(paramMap);

	    for (Map<String, Object> allMap : allMerchantList) {
	    	long userId=StringUtil.nullToLong(allMap.get("userId"));
		
			for(Map<String, Object> merchantMap : merchantLastActiveList){
				long userId_=StringUtil.nullToLong(merchantMap.get("userId"));
				if(userId!=0 && userId_!=0 && userId==userId_){
					String lastActiveTime=merchantMap.get("lastActiveTime")==null?"1900-01-01 00:00:00":merchantMap.get("lastActiveTime").toString() ;
					allMap.put("lastActiveTime", lastActiveTime);
				}
			}
			//去重复数据
			if (Collections.frequency(tempList, allMap) < 1){//没有重复的数据				
				tempList.add(allMap);				
			}
	    }	
	    
	    //配置的时间段内才会发送短信	 
		if(configMap==null){
			configMap=getConfigurationInfoByKey(); //获取配置信息 
		}	
		String sendorderSms_time=configMap.get("sendorderSms_time")==null?"1,08:00,10:00":StringUtil.null2Str(configMap.get("sendorderSms_time"));//默认开启
		if(sendorderSms_time.contains(",")){
			int isOpen=StringUtil.nullToInteger(sendorderSms_time.split(",")[0]);
			if(isOpen==1){//开启此项过滤
				String startTime=StringUtil.null2Str(sendorderSms_time.split(",")[1]);//格式：08:00
				String endTime=StringUtil.null2Str(sendorderSms_time.split(",")[2]);//格式：10:00
			    if(StringUtil.isNotEmpty(startTime) && StringUtil.isNotEmpty(endTime)){
				    int nowTime=StringUtil.nullToInteger((DateUtil.getNowYYYYMMDDHHMM()).split(" ")[1].replaceFirst(":", ""));
			    	int startTime_=StringUtil.nullToInteger(startTime.replaceFirst(":", ""));
			    	int endTime_=StringUtil.nullToInteger(endTime.replaceFirst(":", ""));
			    	if(nowTime>=startTime_ && nowTime<=endTime_){
						//异步发送短信
						asyncSendMsg(tempList,serviceTypeId,orderId,pushNotLoginDays);	
			    	}
			    }
			}
		}
		return tempList;
	}
	/**
	 * 异步发送 订单短信
	 * @param phone
	 * @param content
	 */
	public void asyncSendMsg(final List<Map<String, Object>> tempList,final int serviceTypeId,final Long orderId,final String pushNotLoginDays) {
		CalloutServices.executor(new Runnable(){
			@Override
			public void run() {
				long days= 1000;
				 for (Map<String, Object> allMap : tempList) {
					long userId=StringUtil.nullToLong(allMap.get("userId"));					 
		 

					String nowDate=DateUtil.getNowYYYYMMDD();
					//如果活跃字段为空，则默认为不活跃
					String lastActiveTime=allMap.get("lastActiveTime")==null?"1900-01-01 00:00:00":allMap.get("lastActiveTime").toString() ;
					if(!lastActiveTime.equals("")){
						lastActiveTime=lastActiveTime.split(" ")[0];//取日期
						days=DateUtil.getDaysBetweenDAY1andDAY2(lastActiveTime, nowDate);
						
					}
//					BusinessUtil.writeLog("push",orderId+"-最后活跃时间："+lastActiveTime+",活跃天数："+days+",配置的天数："+pushNotLoginDays);	
					if(days >= StringUtil.nullToLong(pushNotLoginDays)){
						//获取配置信息 
						if(configMap==null){
							configMap=getConfigurationInfoByKey();
						}							
						//每个用户总订单短信发送次数
						int totalSendSmsCount=configMap.get("total_sendSms_count")==null?3:StringUtil.nullToInteger(configMap.get("total_sendSms_count"));//默认开启
						
						//查看发送次数
						List<Map<String,Object>> sendList=pushDao.getSendList(userId);
						//如果发送次数大于或者等于配置次数，则不发短信
						if(sendList!=null && sendList.size()>=totalSendSmsCount){
//							BusinessUtil.writeLog("push",orderId+"-用户已发送"+sendList.size()+"次短信,配置的最大短信次数："+totalSendSmsCount+",次数达到上限");	
							continue;
						}else{
							if(sendList!=null && sendList.size()>0){
								//判断距离上次发送短信间隔多少天，
								String joinTime=StringUtil.null2Str(sendList.get(0).get("joinTime"));
								joinTime=joinTime.split(" ")[0];//年月日格式
								days=DateUtil.getDaysBetweenDAY1andDAY2(joinTime, nowDate);
								
								if(days < totalSendSmsCount){
									continue;
								}								
							}
						}
//						BusinessUtil.writeLog("push",orderId+"-用户已发送"+count+"次短信,配置的最大短信次数："+totalSendSmsCount);	
						
						//获取短信内容短链接
						String orderSmsContentLink=configMap.get("order_sms_content_link")==null?"":configMap.get("order_sms_content_link").toString();//默认开启

						//发送短信
						String phone=StringUtil.null2Str(allMap.get("phone"));	
						String serviceTypeName = commonService.getServiceTypeName(serviceTypeId+"");
						String msg="【O盟】你有一条（"+serviceTypeName+"）订单尚未处理，登录APP立即接单 "+orderSmsContentLink+"?ID="+userId;							
						if(Constant.DEVMODE){
							//开发模式下，如果是以下测试手机，则发送短信，否则不发送
							if(!"13500501324,18055136291,15056025547,15156079323,18130066518,18620515928,18226611171".contains(phone)){
								continue;
							}
						}
						System.out.println(phone);
						JSONObject result=SmsUtil.sendSms(phone, msg);	
						if(result.get("resultCode").equals("000")){//短信发送成功
//							BusinessUtil.writeLog("push",orderId+"-给用户发送短信："+msg);
							//保存短信发送记录
							Map<String,Object> param=new HashMap<String, Object>();
							param.put("userId", userId);
							param.put("phone", phone);
							param.put("orderId", orderId);
							pushDao.addOrderSendSmsInfo(param);
						}else{
							BusinessUtil.writeLog("push",orderId+"-短信发送失败"+result.get("resultCode"));
						}
					}				
				 }		
			}
		});
	}
	//合并老板员工，用于插入商户订单表
	public List<Map<String, Object>> mergeBossAndEmployee(List<Map<String, Object>> allMerchantList) {
		
		List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
	    for (Map<String, Object> allMap : allMerchantList) {
		    int temp=0;
			String merchantId = StringUtil.null2Str(allMap.get("merchantId"));
			 for (Map<String, Object> allMap_ : tempList) {
				 String merchantId_ = StringUtil.null2Str(allMap_.get("merchantId"));
				 if(!merchantId.equals("") && !merchantId_.equals("") && merchantId.equals(merchantId_)){
					 temp=1;
				 }
			 }
			
			if(temp==0){
				tempList.add(allMap);
			}	
	    }		
		return tempList;
	}
	/**
	 * 写入MQ
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void writeToMQ(String queueName,String msg) throws Exception{	
		try{
			orderTemplate.send(queueName,null, MessageBuilder.withBody(msg.getBytes("UTF-8"))
			.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build(), new CorrelationData(msg));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 合并VIP商户和普通商户
	 */
	private void mergeMerchant(List<Map<String, Object>> allVIPMerchantList,List<Map<String, Object>> allMerchantList){
		for(Map<String, Object> map:allVIPMerchantList){
			allMerchantList.add(map);
		}	
	}
	
	/**
	 *查看商户活跃时间
	 */
	public List<Map<String,Object>> getMerchantActiveTime(List<Map<String, Object>> allMerchantList){
		String userIds="";
		for(Map<String,Object> map : allMerchantList){

			userIds+=StringUtil.null2Str(map.get("userId"))+",";
		}
		if(StringUtil.isNotEmpty(userIds)){
			userIds=userIds.substring(0,userIds.length()-1);
		}
		Map<String,Object> paramMap=new HashMap<String, Object>();
		paramMap.put("userIds", userIds);
		if(allMerchantList==null || allMerchantList.size()==0){
			return new ArrayList<Map<String,Object>>();
		}
		//查询商户最后的活跃时间
		List<Map<String, Object>> merchantLastLoginList =pushDao.getMerchantLastLoginList(paramMap);
		for(Map<String, Object> map : allMerchantList){			
			long merchantId=StringUtil.nullToLong(map.get("merchantId"));			
			for(Map<String, Object> merchantMap : merchantLastLoginList){
				long merchantId_=StringUtil.nullToLong(merchantMap.get("merchantId"));
				if(merchantId!=0 && merchantId_!=0 && merchantId==merchantId_){
					//如果活跃字段为空，则默认为不活跃
					map.put("lastActiveTime", merchantMap.get("lastActiveTime")==null?"1900-01-01 00:00:00":merchantMap.get("lastActiveTime")) ;				
				}
			}
		}
		return allMerchantList;
	}
	
	
	@Override
	public JSONObject pushOrderToAssistants(Map<String, Object> paras)
			throws Exception {
		long time=System.currentTimeMillis();
		JSONObject jobj=null;
		try{	

			BusinessUtil.writeLog("push","pushOrderToAssistants-开始订单私人APP推送："+DateUtil.getNowTime());
			
			if(paras==null || paras.size()==0){
				BusinessUtil.writeLog("push","pushOrderToAssistants-推送参数为空："+paras);
				return null;
			}
			
			List<Map<String, Object>> pushOrderList=(List<Map<String, Object>>)paras.get("pushOrderList");
			BusinessUtil.writeLog("push","pushOrderToAssistants-共有："+(pushOrderList==null?0:pushOrderList.size()+"个订单需要推送给私人助理"));
		
			
			if(pushOrderList==null || pushOrderList.size()==0){
				return null;
			}
			
		
			//获取配置信息 
			if(assistantConfigMap==null){
				assistantConfigMap=getConfigurationInfoByKey(ASSISTANT_APP);
				
				assistantConfigMap.put("push-masterSecret", assistantConfigMap.get("push-masterSecret_"+ASSISTANT_APP));
				assistantConfigMap.put("push-appSecret", assistantConfigMap.get("push-appSecret_"+ASSISTANT_APP));
				assistantConfigMap.put("push-appkey", assistantConfigMap.get("push-appkey_"+ASSISTANT_APP));
				assistantConfigMap.put("push-appId", assistantConfigMap.get("push-appId_"+ASSISTANT_APP));
				assistantConfigMap.put("push-iphoneCertPath", assistantConfigMap.get("push-iphoneCertPath_"+ASSISTANT_APP));
				assistantConfigMap.put("push-iphoneCertPassword", assistantConfigMap.get("push-iphoneCertPassword_"+ASSISTANT_APP));
			}
			// 推送
			paras.put("pushType", 200);
			jobj=PushManager.push(assistantConfigMap,pushOrderList, paras,"userId");
		}catch(Exception e){
			String errorStr=ExceptionUtils.getStackTrace(e);
			BusinessUtil.writeLog("push","pushOrderToAssistants-推送异常："+errorStr);
		}finally{
			BusinessUtil.writeLog("push","pushOrderToAssistants-推送所用时间："+(System.currentTimeMillis()-time)+"\n");
		}
		return jobj;
	}
	
	
	//获取某应用的推送配置信息
	public Map<String,Object> getConfigurationInfoByKey(String appType){
		//推送配置
		
		Map<String,Object> pushConfigMap=(Map<String, Object>) commonCacheService.getObject(CacheConstants.PUSH_CONFIG_KEY,appType);
		if(pushConfigMap==null){
			pushConfigMap=new HashMap<String, Object>();
			// 读取配置信息缓存
			Map<String, Object> allConfigurationInfoMap = null;
			try{
				allConfigurationInfoMap=(Map<String, Object>) commonCacheService.getObject(CacheConstants.CONFIG_KEY);
			}catch(Exception e){
				commonCacheService.deleteObject(CacheConstants.CONFIG_KEY);
			}
			if (allConfigurationInfoMap == null) {// 如果没有缓存配置信息则读取数据库
				allConfigurationInfoMap=new HashMap<String, Object>();
				List<Map<String,Object>> listConfigurationInfo = pushDao.getConfigurationInfo();
				for(Map<String,Object> map : listConfigurationInfo){
					String configKey=StringUtil.null2Str(map.get("config_key"));
					allConfigurationInfoMap.put(configKey, map);
				}
				commonCacheService.setObject(allConfigurationInfoMap, CacheConstants.CONFIG_KEY);
			}
			
			List<String> keyList=PushConfig.getConfigKeyList(appType);
			for(String key : keyList){
				Map<String, Object> configurationInfoMap=(Map<String, Object>)allConfigurationInfoMap.get(key);
				if(configurationInfoMap==null){
					BusinessUtil.writeLog("push","缓存中没有推送配置项："+key);	
					continue;
				}
				String configKey = StringUtil.null2Str(configurationInfoMap.get("config_key"));
				String configValue = StringUtil.null2Str(configurationInfoMap.get("config_value"));
				String standbyField1 = StringUtil.null2Str(configurationInfoMap.get("standby_field1"));
				String standbyField2 = StringUtil.null2Str(configurationInfoMap.get("standby_field2"));
				if(StringUtil.isNotEmpty(standbyField1)){
					configValue+=","+standbyField1;
				}
				if(StringUtil.isNotEmpty(standbyField2)){
					configValue+=","+standbyField2;
				}
				pushConfigMap.put(configKey, configValue);
			}
			commonCacheService.setObject(pushConfigMap, CacheConstants.PUSH_CONFIG_KEY,appType);
		}
		return pushConfigMap;
	}	
	
	
	
	
	public static void main(String[] args) {
		
		
	}
}
