package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class ServiceWord extends Model<ServiceWord>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static ServiceWord dao=new ServiceWord();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
