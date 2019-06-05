package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * 群组角色
 * @author lijie
 * authority_group_role
 *
 */
public class GroupRole extends Model<GroupRole>{

	private static final long serialVersionUID = 1L;

	public static final GroupRole dao = new GroupRole();

	private Long groupId;

	private Long resId;
	
	private Long total;

	public GroupRole() {
	}

	public GroupRole(Long groupId, Long resId) {
		super();
		this.groupId = groupId;
		this.resId = resId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
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
