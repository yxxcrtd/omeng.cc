package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * 实体类
 * @author lijie
 * 
 * http://www.shanjin.com.cn
 * Date:2015年5月25日
 *
 */
public class UserRole extends Model<UserRole>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final UserRole dao = new UserRole();
	
	private Long userId;

	private Long roleId;
	
	private Long total;

	
	private String id;
	
	private String roleid;
	
	private String rolename;
	
	private String userid;
	
	private String username;

	public UserRole(){}

	public UserRole(Long userId, Long roleId) {
		super();
		this.userId = userId;
		this.roleId = roleId;
	}
	
	public UserRole(int id, String roleid, String rolename, String userid, String username) {
		super();
		this.roleid = roleid;
		this.rolename = rolename;
		this.userid = userid;
		this.username = username;
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleid() {
		return roleid;
	}

	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	
	
}
