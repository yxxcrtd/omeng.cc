package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class UserWord extends Model<UserWord>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static UserWord dao=new UserWord();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
