package com.shanjin.manager.Bean;


public class EmptyResponse extends Response {

	public EmptyResponse() {
		this.setCode(ResponseCode.EMPTY);
		this.setMessage("没有数据！");
		this.setData("");
		this.setSuccess(true);
	}
	public EmptyResponse(long total) {
		this.setCode(ResponseCode.EMPTY);
		this.setMessage("没有数据！");
		this.setData("");
		this.setTotal(total);
		this.setSuccess(true);
	}
	public EmptyResponse(String error) {
		this.setCode(ResponseCode.EXCEPTION);
		this.setMessage(error);
		this.setData("");
		this.setSuccess(true);
	}
}
