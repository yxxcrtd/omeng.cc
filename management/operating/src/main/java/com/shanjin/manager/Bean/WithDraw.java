package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class WithDraw extends Model<WithDraw>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static WithDraw dao=new WithDraw();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}

}
