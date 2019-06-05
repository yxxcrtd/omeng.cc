package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class FensiAddTotal extends Model<FensiAddTotal>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static FensiAddTotal dao=new FensiAddTotal();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
