package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class UserChannel extends Model<UserChannel>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static UserChannel dao=new UserChannel();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
