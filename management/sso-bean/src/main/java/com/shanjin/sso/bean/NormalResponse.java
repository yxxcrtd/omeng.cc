package com.shanjin.sso.bean;


public class NormalResponse extends Response{
	
	public NormalResponse(Object data) {
		this.setCode(ResponseCode.OK);
		this.setMessage("");
		this.setData(data);
		this.setSuccess(true);
	}
	
	public NormalResponse(Object data, Long total) {
		this.setCode(ResponseCode.OK);
		this.setMessage("");
		this.setData(data);
		this.setSuccess(true);
		this.setTotal(total);
		this.setLimit(10);
	}
	
	public NormalResponse(Object data, Long total,String msg) {
		this.setCode(ResponseCode.OK);
		this.setMessage(msg);
		this.setData(data);
		this.setSuccess(true);
		this.setTotal(total);
		this.setLimit(10);
	}
	public NormalResponse(Object data,String msg) {
		this.setCode(ResponseCode.OK);
		this.setMessage(msg);
		this.setData(data);
		this.setSuccess(true);
		this.setLimit(10);
	}
	public NormalResponse() {
		this.setCode(ResponseCode.OK);
		this.setMessage("");
		this.setData(null);
		this.setSuccess(true);
	}
}
