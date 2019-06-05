package com.shanjin.omeng.token.api;

import com.alibaba.fastjson.JSONObject;

/**
 * 提供给钱包的用户服务
 * @author xmsheng
 * @createTime 2016/9/7 16:21
 *
 */
public interface IOpayUserService {
	
	/**
	 * 银联银行卡三要素验证接口
	 * @param omengUserId
	 * @param realName
	 * @param certNo
	 * @param cardNo
	 * @return
	 */
	public JSONObject comVerifyBg(Long omengUserId,String realName,String certNo,String cardNo);
	
	
	
	/**
	 * 查询用户实名认证信息，
	 * 未实名认证或者认证不通过 返货错误状态码：001，
	 * 实名认证通过返回状态码：000，并返回实名认证真实姓名realName,省份证号 certNo 
	 * @param omengUserId
	 * @return
	 */
	public JSONObject getUserUnionpayVerify(Long omengUserId);
	

}
