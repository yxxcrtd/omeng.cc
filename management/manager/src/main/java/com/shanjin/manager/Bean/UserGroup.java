package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

/**
 * 实体类
 * @author huangyulai
 * 
 *
 */
public class UserGroup extends Model<UserGroup>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final UserGroup dao = new UserGroup();
	
	private Long userId;

	private Long groupId;
	
	private Long total;


	public UserGroup(){}

	public UserGroup(Long userId, Long groupId) {
		super();
		this.userId = userId;
		this.groupId = groupId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}
	

	
}
