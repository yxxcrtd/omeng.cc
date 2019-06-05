package com.shanjin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.DynamicKeyGenerator;
import com.shanjin.common.util.HttpRequest;
import com.shanjin.common.util.StringUtil;
import com.shanjin.dao.IUserInfoDao;
import com.shanjin.omeng.token.api.IOpayUserService;
import com.shanjin.omeng.token.bean.ResultJSONObject;
import com.shanjin.service.ICommonService;
import com.shanjin.service.IUserInfoService;

/**
 * 提供给钱包用户信息接口服务
 * 
 * @author xmsheng
 * @createTime 2016/9/7 1:35
 * @version 1.0
 *
 */
@Service("opayUserService")
public class OpayUserServiceImpl implements IOpayUserService {

	// 本地异常日志记录对象
	private static final Logger logger = Logger.getLogger(OpayUserServiceImpl.class);

	@Resource
	private IUserInfoService userService;
	
	@Resource
	private IUserInfoDao userInfoDao;
	
	@Resource
	private ICommonService commonService;
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject comVerifyBg(Long omengUserId, String realName, String certNo, String cardNo) {
		JSONObject jsonObejct = null;
		try{
			logger.info("钱包调用银行卡认证接口-->[omengUserId]=" + omengUserId + "[realName]=" + realName + "[certNo]=" + certNo
					+ "[cardNo]=" + cardNo);
			if (null == omengUserId) {
				return new ResultJSONObject("comVerifyBg_system_error", "参数[omnegUserId]错误");
			}
			if (StringUtil.isEmpty(realName)) {
				return new ResultJSONObject("comVerifyBg_system_error", "参数[realName]错误");
			}
			if (StringUtil.isEmpty(certNo)) {
				return new ResultJSONObject("comVerifyBg_system_error", "参数[certNo]错误");
			}
			if (StringUtil.isEmpty(cardNo)) {
				return new ResultJSONObject("comVerifyBg_system_error", "参数[cardNo]错误");
			}
			//用户在业务端是否合法存在,防止业务非法用户
			if(userInfoDao.countAvaliablByUserId(omengUserId) <= 0){
				return new ResultJSONObject("comVerifyBg_system_error","验证用户状态异常，请联系客服！");
			}
			
			//统计是否存在失败的认证信息
			Map<String,Object> paramsMap = new HashMap<String,Object>();
			paramsMap.put("userId", omengUserId); //用户userId
			paramsMap.put("realName", realName); //真实姓名
			paramsMap.put("certNo", certNo); //身份证号
			paramsMap.put("cardNo", cardNo); //银行卡号
			paramsMap.put("verfiyType", 2); //统计银行卡认证
			paramsMap.put("timeLimit", 3); //时间限制为3天
			Map<String,Object> verifyInfoMap = userInfoDao.selectUserVeifyBgInfo(paramsMap);
			if (verifyInfoMap != null) {
				int flag = (int)verifyInfoMap.get("verifyStatus");
				String resCode = (String)verifyInfoMap.get("respCode");
				if (0 == flag) {
					if(this.isUnionpayResError(resCode)){
						return new ResultJSONObject("comVerifyBg_verify_error", "添加银行卡失败，请核实信息重新添加！");
					}
				} else if (1 == flag) {
					return new ResultJSONObject("000", "用户银行卡认证通过！");
				}
			}
			//每日验证次数，默认为3次限制
			int unionPayVerfiyLimti =3;
			Map<String,Object> verifyDayLimitMap= this.commonService.getConfigurationInfoByKey("unionpay_verifybg_day_limit");
			if(verifyDayLimitMap !=null && verifyDayLimitMap.get("config_value")!=null){
				unionPayVerfiyLimti = Integer.parseInt((String)verifyDayLimitMap.get("config_value"));
			}
			if(userInfoDao.countTodayComVerifyBg(omengUserId) >= unionPayVerfiyLimti){
				return new ResultJSONObject("comVerifyBg_system_error","操作过于频繁，请明日再试！");
			}
			//调用银联银行卡三要素认证,发送网络请求
			String transNo = DynamicKeyGenerator.generateDynaminKey15();
			String url = Constant.WEB_PROXY_URL + "/comVerifyBg";
			List<NameValuePair> parameters  = new ArrayList<NameValuePair>();
			parameters.add( new BasicNameValuePair("realName", realName));
			parameters.add( new BasicNameValuePair("certNo", certNo));
			parameters.add( new BasicNameValuePair("cardNo", cardNo));
			parameters.add( new BasicNameValuePair("transNo", transNo));
			String res = HttpRequest.httpClientPost(url, parameters);
			if(null == res){
				logger.info("调用实名认证网络连接异常");
				return new ResultJSONObject("comVerifyBg_system_error","系统网络异常,请稍后重试!");
			}
			Map<String,String> resMap = (Map<String, String>) JSONObject.parse(res);
			if(!"00".equals(resMap.get("respCode"))){
				paramsMap.put("verifyStatus",0);
			}else{
				paramsMap.put("verifyStatus",1);
			}
			paramsMap.put("transNo", transNo);
			paramsMap.put("remark", resMap.get("respMsg")); //银联接口返回描述
			paramsMap.put("respCode", resMap.get("respCode"));//银联接口返回状态码
			userInfoDao.insertConVerifyBgInfo(paramsMap);	
			String unionCode = (String)resMap.get("respCode");
			//如果验证失败，返回信息
			if(this.isUnionpayResError(unionCode)){
				return new ResultJSONObject("comVerifyBg_verify_error", "添加银行卡失败，请核实信息重新添加！");
			}
			//不是验证失败并且不是成功，返回银联认证信息
			if(!"00".equals(resMap.get("respCode"))){
				return new ResultJSONObject("comVerifyBg_system_error","认证不通过，请核实信息重新添加！");
			}
			jsonObejct =  new ResultJSONObject("000","用户银行卡认证通过");
			jsonObejct.put("userId", omengUserId); //返回业务用户id
			jsonObejct.put("realName",realName); //返回认证姓名
			jsonObejct.put("certNo",certNo); //返回认证身份证 
			jsonObejct.put("cardNo",cardNo); //返回认证卡号
			jsonObejct.put("transNo",transNo); //返回业务系统交易流水号			
			return jsonObejct;
		}catch(Exception e){
			logger.error("银行卡三要素认证系统异常"+e);
			jsonObejct = new ResultJSONObject("comVerifyBg_system_error","业务系统异常，请稍后重试");
		}
		return jsonObejct;
	}
	


	@Override
	public JSONObject getUserUnionpayVerify(Long omengUserId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 检查银联认证是否成功
	 * @param msgCode
	 * @return
	 */
	private boolean isUnionpayResError(String msgCode){
		if(StringUtil.isEmpty(msgCode)){
			return true;
		}
		Map<String,Object> verifyBgResCode= this.commonService.getConfigurationInfoByKey("unionpay_verifybg_rescode");
		//默认三要素认证失败的状态码是 "2319"
		String unionPayResCode ="2319";
		if(verifyBgResCode !=null && verifyBgResCode.get("config_value")!=null){
			unionPayResCode = (String)verifyBgResCode.get("config_value");
		}
		String resCodes[] = unionPayResCode.split(",");
		for(int index = 0;index< resCodes.length ;index++){
			if(msgCode.equals(resCodes[index])){
				return true;
			}
		}
		return false;
	}
}
