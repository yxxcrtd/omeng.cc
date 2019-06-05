package com.shanjin.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.cache.service.IIpCityCacheService;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.ResultJSONObject;
import com.shanjin.common.constant.TopicConstants;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.DynamicKeyGenerator;
import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.IdGenerator;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IUserCPlanDao;
import com.shanjin.kafka.ProducerFactory;
import com.shanjin.kafka.ProducerInterface;
import com.shanjin.service.IUserCPlanService;

@Service("userCPlanService")
public class UserCPlanServiceImpl implements IUserCPlanService {
	private static final Logger logger = Logger
			.getLogger(UserCPlanServiceImpl.class);

	private static ProducerInterface vitalProducer;

	static {

		if (Constant.KAFKA_LOG)
			vitalProducer = ProducerFactory.getProducer(true,
					BusinessUtil.getIpAddress());
	}

	@Resource
	private IUserCPlanDao userCPlanDao;

	@Resource
	private IIpCityCacheService ipCityCacheServices;

	@SuppressWarnings("unused")
	@Override
	public JSONObject registerFromCPlan(String phone, String ip)
			throws Exception {
		JSONObject jsonObject = null;
		
		Map<String, Object> respData = new HashMap<String, Object>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("phone", phone);
		Map<String, Object> user = this.userCPlanDao.getUserInfoByPhone(paramMap);
		if (user != null && user.get("id") != null) {
			// 用户已经存在
			jsonObject = new ResultJSONObject("001", "注册失败，用户已存在！");
			
			respData.put("userId", user.get("id"));
			respData.put("phone", phone);
			Timestamp createTime = (Timestamp) user.get("join_time");
			respData.put("createTime", DateUtil.getTimestampToString(
					createTime, DateUtil.DATE_TIME_PATTERN));
			respData.put("isNew", 0);
			
			jsonObject.put("respData", respData);
		} else {
			// 平台不存在，保存用户
			user = new HashMap<String, Object>();
			String userKey = DynamicKeyGenerator.generateDynamicKey();
			String userId = StringUtil.null2Str(IdGenerator.generateID(18));
			user.put("id", userId);
			user.put("userId", userId);
			user.put("userKey", userKey);
			user.put("phone", phone);
			user.put("ip", ip);
			user.put("name", "");
			user.put("userType", "0");
			user.put("province", Constant.EMPTY);
			user.put("city", Constant.EMPTY);
			user.put("platform", 2); // c计划
			user.put("havePlatform", 1);
			if (ip != null && !Constant.DEVMODE) {
				if (Constant.ASYNC_PROCESS) {
					// 异步消息进行 IP-位置解析 Revoke 2015.12.9 日
					vitalProducer.sendMsg(TopicConstants.IP_CITY_RESOLVE,
							BusinessUtil.getLogId(),
							JSONObject.toJSONString(user));
				} else {
					Map<String, String> locationInfo = resolveCityByIp(ip);
					if (locationInfo.size() > 0) {
						user.put("province", locationInfo.get("province"));
						user.put("city", locationInfo.get("city"));
					}
				}
			}
			int n = userCPlanDao.insertUserInfoWithVerification(user);// 用户基本信息
			if (n < 1) {
				jsonObject = new ResultJSONObject("002", "注册用户失败！");
			} else {
				user = this.userCPlanDao.getUserInfoByPhone(paramMap);
				jsonObject = new ResultJSONObject("000", "注册成功！");
				respData.put("userId", user.get("id"));
				respData.put("phone", phone);
				Timestamp createTime = (Timestamp) user.get("join_time");
				respData.put("createTime", DateUtil.getTimestampToString(
						createTime, DateUtil.DATE_TIME_PATTERN));
				respData.put("isNew", 1);
				jsonObject.put("respData", respData);
			}

		}

		return jsonObject;
	}

	/**
	 * 抽取 ip解析成CITY 的逻辑块为方法 2015.12.9 便于以后改为异步调用。
	 * 
	 * @param ip
	 */
	private Map<String, String> resolveCityByIp(String ip) {
		// 根据IP获取省份和城市
		// ADD ip-city 缓存 ---2015.09.21 ----Revoke Yu
		JSONObject cachedIpAddress = ipCityCacheServices.getCity(ip);

		Map<String, String> result = new HashMap<String, String>();

		if (cachedIpAddress != null) {
			result.put("province", cachedIpAddress.get("province").toString());
			result.put("city", cachedIpAddress.get("city").toString());
		} else {
			JSONObject jsonObjectIp = IPutil.getIpLocationBySina(ip);
			if (jsonObjectIp != null) {
				result.put("province",
						StringUtil.null2Str(jsonObjectIp.get("province")));
				result.put("city",
						StringUtil.null2Str(jsonObjectIp.get("city")));
				ipCityCacheServices.cachedCity(ip, result.get("province"),
						result.get("city"));
			} else {
				jsonObjectIp = IPutil.getIpLocationByBaidu(ip);
				if (jsonObjectIp != null) {
					if (jsonObjectIp.get("address") != null) {
						String address = (String) jsonObjectIp.get("address");
						if (address != null && address.length() > 0) {
							String[] addressDetail = address.split("\\|");
							if (addressDetail != null) {
								result.put("province", addressDetail[1]);
								result.put("city", addressDetail[2]);
								ipCityCacheServices.cachedCity(ip,
										addressDetail[1], addressDetail[2]);
							}
						}
					}
				}
			}
		}
		return result;
	}

}
