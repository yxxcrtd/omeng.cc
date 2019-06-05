package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class MerchantPushApply extends Model<MerchantPushApply>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static MerchantPushApply dao=new MerchantPushApply();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
