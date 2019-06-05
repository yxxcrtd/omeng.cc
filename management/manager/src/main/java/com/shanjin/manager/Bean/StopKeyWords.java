package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class StopKeyWords extends Model<StopKeyWords>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static StopKeyWords dao=new StopKeyWords();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
