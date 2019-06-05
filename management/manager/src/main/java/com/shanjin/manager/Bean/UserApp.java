package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * 项目代理商用户代理的app
 * @author Huang yulai
 * authority_user_app
 */
public class UserApp extends Model<UserApp>{
	private static final long serialVersionUID = 1L;

	public static final UserApp dao = new UserApp();

	private Long userId;

	private Long appId;
	
	private Long total;

	public UserApp() {
	}

	public UserApp(Long userId, Long appId) {
		super();
		this.userId = userId;
		this.appId = appId;
	}


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
	
	
}
