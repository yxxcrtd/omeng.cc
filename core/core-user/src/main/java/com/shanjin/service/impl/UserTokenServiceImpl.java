package com.shanjin.service.impl;
import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.MD5Util;
import com.shanjin.common.util.StringUtil;
import com.shanjin.omeng.token.api.OTokenService;
import com.shanjin.omeng.token.bean.ResultJSONObject;
import com.shanjin.omeng.token.bean.TokenBean;
import com.shanjin.service.IUserInfoService;
import com.shanjin.service.IValidateService;

/**
 * 用户token验证,向钱包提供服务
 * @author xmsheng
 *
 */
@Service("oTokenService")
public class UserTokenServiceImpl implements OTokenService{

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(UserTokenServiceImpl.class);
	
	@Resource
    private IUserInfoService userService;

    @Resource
    private IValidateService validateService;

	@Override
	public JSONObject validateToken(TokenBean tokenBean) {
		try{
			String phone = tokenBean.getPhone();
			String clientId = tokenBean.getClientId();
			String time = tokenBean.getTime();
			String validatedToken = tokenBean.getToken();
//			System.out.println("tokenBean参数：{}"+JSONObject.toJSONString(tokenBean));
			JSONObject jsonObject = new ResultJSONObject("000","token校验通过");
			if(StringUtil.isEmpty(phone) || StringUtil.isEmpty(clientId) || StringUtil.isEmpty(time) || StringUtil.isEmpty(validatedToken)){
				String param="[钱包调用校验Token]"+"time="+time+",phone="+phone+",clientId="+clientId+",token="+validatedToken;
				BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS() + "：" + param);
				return new ResultJSONObject("token_validate_params_error", "token验证参数错误");
			}
			String lastTime = validateService.lastValidatedTime(clientId);
			if(time.equals(lastTime)){
				String param="[钱包调用校验Token]"+"time="+time+",lastTime="+lastTime+",phone="+phone+",clientId="+clientId+",token="+validatedToken;
				BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS() + "：" + param);
				return new ResultJSONObject("token_validate_time_error", "token重复验证");
			}
			String userKey = userService.getUserKey(phone, clientId);
			if(StringUtil.isEmpty(userKey)){
				String param="[钱包调用校验Token]"+"time="+time+",userKey="+userKey+",phone="+phone+",clientId="+clientId+",token="+validatedToken;
				BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS() + "：" + param);
				return new ResultJSONObject("token_validate_error", "token验证不通过");
			}
			String token =  MD5Util.MD5_32(time+ ""+ clientId + "" + phone + "" +userKey);
			if(!validatedToken.toUpperCase().equals(token)){
				String param="[钱包调用校验Token]"+"time="+time+",userKey="+userKey+",phone="+phone+",clientId="+clientId+",token="+token+",validToken="+token;
				BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS() + "：" + param);
				return new ResultJSONObject("token_validate_error", "token验证不通过");
			}
			validateService.updateLastValidatedTime(clientId, time);
			jsonObject.put("userKey", userKey);
			return jsonObject;
			
		}catch(Exception e){
			e.printStackTrace();
			logger.error("钱包调用token系统异常",e);
			return new ResultJSONObject("token_validate_system_error","token校验系统异常");
		}
	}
	
	public static void main(String args[]){
		String time="1467600651566";
		String clientId="78fc5723b3c29414744c45cd17b2ccb22525814fdc48d58b2921d53b680a205e";
		String phone="15105609557";
		String userKey="531305356CC333EEF0C104FF5A8E334F";
		String token="937AA19A666BCCDD0861C0149617A75D";
		System.out.println(MD5Util.MD5_32(time+ ""+ clientId + "" + phone + "" +userKey));
	}
	
}
