package com.shanjin.thread;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.MD5Util;

public class CleanSessionToken implements Runnable {
	private HttpServletRequest request;

	public CleanSessionToken(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(10000);
			if (request.getSession(false) != null) {
				String formhash = getRequestToken(request);
				Set<String> token = (Set<String>) request.getSession(false).getAttribute("token");
				if (token == null || !token.contains(formhash)) {
				} else {
					token.remove(formhash);
					request.getSession().setAttribute("token", token);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getRequestToken(HttpServletRequest request) {
		String token = "";
		String ip = IPutil.getIpAddr(request);
		String path = request.getServletPath();
		StringBuffer paraString = new StringBuffer("[ ");
		;
		Map<String, String[]> paramMap = request.getParameterMap();
		Set<Entry<String, String[]>> set = paramMap.entrySet();
		Iterator<Entry<String, String[]>> it = set.iterator();
		while (it.hasNext()) {
			Entry<String, String[]> entry = it.next();
			paraString.append(entry.getKey() + ":");
			for (String i : entry.getValue()) {
				paraString.append(i);
			}
			paraString.append("; ");
		}
		paraString.append("]");
		token = MD5Util.MD5_32(ip + path + paraString.toString());
		return token;
	}
}
