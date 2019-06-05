package com.shanjin.awechat;

public class NameValuePair {
	
	private String value;
	
	private String name;

	
	public NameValuePair( String name,String value) {
		super();
		this.value = value;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	
	

}
