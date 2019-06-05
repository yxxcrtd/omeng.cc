package com.shanjin.manager.intercepter;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.shanjin.cache.util.IpCityUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.IPutil;
import com.shanjin.manager.Bean.SystemResource;
import com.shanjin.manager.Bean.UserSession;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.utils.ManagerUtil;
import com.shanjin.manager.utils.StringUtil;

public class ClientLogInterceptor implements Interceptor{
	private final Logger log = Logger.getLogger(ClientLogInterceptor.class);

	private Log accessLoger = LogFactory.getLog("reqAndResLog");
	@Override
	public void intercept(ActionInvocation ai) {
		Controller controller = ai.getController(); 
		HttpServletRequest request = controller.getRequest();
		
		String logInfo = "";
		try {
			logInfo = getLogInfo(request);
			String url = stringProcess(request.getRequestURI());
			if(!url.contains(".")||url.endsWith(".jsp")||url.endsWith(".html")){
				accessLoger.info(logInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		    ai.invoke();
		}
	    ai.invoke();
	}
	
	private String getLogInfo(HttpServletRequest request) throws Exception {

		String returnStr = "";

		String ip = stringProcess(ManagerUtil.getIpAddr(request));
		//String ip = "221.178.181.45";
		String url = stringProcess(request.getRequestURI());
		
		String queryStr = request.getQueryString();
		String referer = stringProcess(StringUtil.null2Str(
				request.getHeader("Referer")).split("\\?")[0]);
		HttpSession session = ManagerUtil.getSession(request);
		String userName = stringProcess(StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME)));
		SystemResource sr = (SystemResource) Constant.sysResource.get(url);
		String rName = "";
		if(sr!=null){
			rName = stringProcess(StringUtil.null2Str(sr.getStr("resName")));
		}
		returnStr = DateUtil.formatDate("yyyyMMddHHmmss", new Date())+"|"+userName+"|"+ip+"|"+url+"|"+rName+"|"+referer+"|"+queryStr;
		//returnStr = DateUtil.formatDate("yyyyMMddHHmmss", new Date())+"|"+userName+"|"+ip+"|"+url+"|"+rName+"|"+referer;
		return returnStr;
	}
	
	/**
	 * @param s
	 *            处理字符串，把"|"替换为空格
	 */
	public String stringProcess(String s) {
		if (s != null) {
			s = s.replaceAll("\\|", " ");
			return s;
		}
		return "";
	}


}
