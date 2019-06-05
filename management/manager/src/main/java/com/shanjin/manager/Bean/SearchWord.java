package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class SearchWord extends Model<SearchWord>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static SearchWord dao=new SearchWord();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
