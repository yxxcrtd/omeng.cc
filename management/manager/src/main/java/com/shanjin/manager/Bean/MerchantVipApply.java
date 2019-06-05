package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class MerchantVipApply extends Model<MerchantVipApply>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static MerchantVipApply dao=new MerchantVipApply();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
