package com.shanjin.manager.Bean;

import com.jfinal.plugin.activerecord.Model;

public class AgentEmployee extends Model<AgentEmployee>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static AgentEmployee dao=new AgentEmployee();
	private long total;
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}
