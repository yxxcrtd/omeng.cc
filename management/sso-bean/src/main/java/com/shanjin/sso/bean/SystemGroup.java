package com.shanjin.sso.bean;

import java.util.Date;

import com.jfinal.plugin.activerecord.Model;

/**
 * 系统群组信息
 * authority_group_info
 * @author Huang yulai
 *
 */
public class SystemGroup  extends Model<SystemGroup>{
	private static final long serialVersionUID = 1L;

	public static final SystemGroup dao = new SystemGroup();

	private Long id;
	
	private String groupName;
	
	private String createName;
	
	private String updateName;

	private String remark;

	private int disabled;
	
	private Date createTime;
	
	private Date updateTime;
	
	private Long total;
	
	public SystemGroup(){
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
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
	
	
}
