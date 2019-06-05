package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class MerchantServiceTag extends Model<MerchantServiceTag>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static MerchantServiceTag dao=new MerchantServiceTag();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
