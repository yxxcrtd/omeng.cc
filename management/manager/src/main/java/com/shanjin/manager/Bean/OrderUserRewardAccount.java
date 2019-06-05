package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class OrderUserRewardAccount extends Model<OrderUserRewardAccount>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static OrderUserRewardAccount dao=new OrderUserRewardAccount();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
