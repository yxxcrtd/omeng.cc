package com.shanjin.omeng.token.bean;

import java.io.Serializable;

/**
 * token验证实体
 * 
 * @author xmsheng
 *
 */
public class TokenBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4067351773724612025L;
	
	private String time; // 时间
	private String clientId; // 客户端id
	private String phone; // 用户手机号
	private String token; // token验证
	
	public TokenBean(String time,String clientId,String phone,String token){
		this.time = time;
		this.clientId = clientId;
		this.phone = phone;
		this.token = token;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
