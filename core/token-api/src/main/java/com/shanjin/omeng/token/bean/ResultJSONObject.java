package com.shanjin.omeng.token.bean;

import com.alibaba.fastjson.JSONObject;

public class ResultJSONObject extends JSONObject {

	private static final long serialVersionUID = 6305779772613611507L;

	public ResultJSONObject() {

	}

	public ResultJSONObject(String resultCode, String message) {
		super();
		super.put("resultCode", resultCode);
		super.put("message", message);
	}
}
