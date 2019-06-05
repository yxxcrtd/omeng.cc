package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class AgentCharge extends Model<AgentCharge>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static AgentCharge dao=new AgentCharge();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
