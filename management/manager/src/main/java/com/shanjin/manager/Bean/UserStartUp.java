package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class UserStartUp extends Model<UserStartUp>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static UserStartUp dao=new UserStartUp();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
