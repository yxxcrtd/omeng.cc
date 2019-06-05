package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class AppKey extends Model<AppKey>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static AppKey dao=new AppKey();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
