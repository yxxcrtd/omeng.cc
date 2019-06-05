package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class UserFeedback extends Model<UserFeedback>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static UserFeedback dao=new UserFeedback();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
