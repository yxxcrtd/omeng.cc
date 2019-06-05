package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class CustomKeyWords extends Model<CustomKeyWords>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static CustomKeyWords dao=new CustomKeyWords();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
