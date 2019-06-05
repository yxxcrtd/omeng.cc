package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class OrderRewardAccount extends Model<OrderRewardAccount>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static OrderRewardAccount dao=new OrderRewardAccount();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
