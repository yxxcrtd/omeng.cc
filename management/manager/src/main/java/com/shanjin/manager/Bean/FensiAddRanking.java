package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class FensiAddRanking extends Model<FensiAddRanking>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static FensiAddRanking dao=new FensiAddRanking();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
