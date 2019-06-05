package com.shanjin.sso.bean;

import java.util.Collection;

public abstract class Response {
private boolean success;
	
	private ResponseCode code;
	
	private String message;
	
	private Object data;
	
	private long total;
	
	private long limit;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ResponseCode getCode() {
		return code;
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	@SuppressWarnings("rawtypes")
	public long getTotal() {
		if(this.data == null) {
			this.total = 0L;
		} else {
			if(this.total > 0L) return this.total;
			if(this.data instanceof Collection) {
				this.total = ((Collection) this.data).size();
			} else {
				this.total = 0L;
			}
		}
		
		return this.total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}
	
}
