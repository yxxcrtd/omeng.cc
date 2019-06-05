package com.shanjin.omeng.token.api;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.omeng.token.bean.TokenBean;

/**
 * O盟Token服务，提供给钱包
 * @author xmsheng
 *
 */
public interface OTokenService {
	
	/**
	 * 
	 * @param tokenBean
	 * @return
	 */
	public JSONObject validateToken(TokenBean tokenBean);

}
