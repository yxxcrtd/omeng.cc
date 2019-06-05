package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class MerchantAdviserApply extends Model<MerchantAdviserApply>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static MerchantAdviserApply dao=new MerchantAdviserApply();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
