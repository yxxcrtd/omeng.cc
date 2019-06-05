package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * 角色资源
 * @author Huang yulai
 * authority_role_resource
 *
 */
public class RoleResource extends Model<RoleResource>{

	private static final long serialVersionUID = 1L;

	public static final RoleResource dao = new RoleResource();

	private Long roleId;

	private Long resId;
	
	private Long total;

	public RoleResource() {
	}

	public RoleResource(Long roleId, Long resId) {
		super();
		this.roleId = roleId;
		this.resId = resId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getResId() {
		return resId;
	}

	public void setResId(Long resId) {
		this.resId = resId;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
	
	

}
