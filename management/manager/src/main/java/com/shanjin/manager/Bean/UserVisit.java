package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class UserVisit extends Model<UserVisit>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static UserVisit dao=new UserVisit();
	private long total;
	private String rate;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}
	 
}
