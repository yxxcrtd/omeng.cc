package com.shanjin.Jest;

import io.searchbox.annotations.JestId;

/**
 * 商户概要信息  2016.4.21
 * @author Revoke 
 *
 */
public class MertChantOutline {
	@JestId
	private String id;
	private String name;
	private String location;
	
	private String serviceTypeIds;
		
	public void setLocation(String location) {
		this.location = location;
	}

	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLocation() {
		return location;
	}



	public String getServiceTypeIds() {
		return serviceTypeIds;
	}



	public void setServiceTypeIds(String serviceTypeIds) {
		this.serviceTypeIds = serviceTypeIds;
	}



	
}
