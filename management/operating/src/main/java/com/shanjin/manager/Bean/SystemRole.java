package com.shanjin.manager.Bean;

import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

/**
 * 系统角色
 * authority_role_info 
 * @author Huang yulai
 *
 */
public class SystemRole extends Model<SystemRole>{
	private static final long serialVersionUID = 1L;

	public static final SystemRole dao = new SystemRole();

	private Long id;
	
	private String roleName;
	
	private String createName;
	
	private String updateName;

	private String remark;

	private int disabled;
	
	private Date createTime;
	
	private Date updateTime;
	
	private Long total;
	
	private boolean checked;
	
	public SystemRole(){
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getCreateName() {
		return createName;
	}

	public void setCreateName(String createName) {
		this.createName = createName;
	}

	public String getUpdateName() {
		return updateName;
	}

	public void setUpdateName(String updateName) {
		this.updateName = updateName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getDisabled() {
		return disabled;
	}

	public void setDisabled(int disabled) {
		this.disabled = disabled;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
}
