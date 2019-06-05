package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class Agent extends Model<Agent>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Agent dao=new Agent();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
