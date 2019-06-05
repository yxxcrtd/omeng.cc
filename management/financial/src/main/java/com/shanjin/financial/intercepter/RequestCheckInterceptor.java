package com.shanjin.financial.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.shanjin.financial.constant.Constants;
import com.shanjin.financial.util.BusinessUtil;
import com.shanjin.financial.util.StringUtil;
import com.shanjin.sso.bean.UserSession;

public class RequestCheckInterceptor implements Interceptor{
	
	public  static final String IGNORE_CHECK_URL = "/welcome/,/system/showImage,/system/login,/system/logout,"
			+ "/system/showLoginUserInfo,/system/showBlank,/system/showUserPwd,/system/editSystemUserInfo,/system/modifyUserPwd";
	
	
	public void intercept(ActionInvocation ai) {
		Controller controller = ai.getController(); 
		HttpServletRequest request = controller.getRequest();
		HttpSession session = request.getSession();
		String uri = request.getRequestURI();
		String userName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		if(uri.contains("/welcome")||uri.contains("/system/showImage")||uri.contains("/system/login")||uri.contains("/system/logout")){
			
		}else{
			if(!checkIgnoreURI(uri)){
				// uri请求需要check
				String isLogin = StringUtil.null2Str(session.getAttribute(UserSession._USER_IS_LOGIN));
				if (!"true".equals(isLogin)){
					controller.setAttr("error", "登陆失效，重新登陆！");
					controller.redirect("/view/welcome/index.jsp");
					return;
				}
	
				if(!Constants.ADMIN.equals(userName)&&!BusinessUtil.havePerm(uri,StringUtil.null2Str(session.getAttribute(UserSession._USER_RESOURCES)))){
					// 无权访问
					controller.render("/view/welcome/nopermission.html");
					return;
				}
			}else{
				String isLogin = StringUtil.null2Str(session.getAttribute(UserSession._USER_IS_LOGIN));
				if (!"true".equals(isLogin)&&checkSpecialURI(uri)){
					controller.setAttr("error", "登陆失效，重新登陆！");
					controller.redirect("/view/welcome/index.jsp");
					return;
				}
			}
		}
		controller.getResponse().setHeader("Access-Control-Allow-Origin", "*");
	    ai.invoke();
	}
	
	private boolean checkIgnoreURI(String uri){
		boolean flag = false;
		if(Constants.commonResourcePathList!=null&&Constants.commonResourcePathList.size()>0){
			for(String url : Constants.commonResourcePathList){
				if(url.equals(uri)){
					flag = true ;
					break;
				}
			}
		}
		return flag;
	}
	
	private boolean checkSpecialURI(String uri){
		boolean flag = false;
		if("/system/showBlank".equals(uri)){
			flag = true ;
		}
		return flag;
	}
}
