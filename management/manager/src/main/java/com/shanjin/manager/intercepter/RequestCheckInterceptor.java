package com.shanjin.manager.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.sso.bean.UserSession;
import com.shanjin.manager.constant.Constant;
import com.shanjin.manager.utils.BusinessUtil;
import com.shanjin.manager.utils.StringUtil;

public class RequestCheckInterceptor implements Interceptor{
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	
	public  static final String IGNORE_CHECK_URL = "/welcome/,/systemManager/showImage,/systemManager/login,/systemManager/logout,"
			+ "/systemManager/showLoginUserInfo,/systemManager/showBlank,/systemManager/showUserPwd,/systemManager/editSystemUserInfo,/systemManager/modifyUserPwd";
	
	
	public void intercept(ActionInvocation ai) {
		Controller controller = ai.getController(); 
		HttpServletRequest request = controller.getRequest();
		HttpSession session = request.getSession();
		String curSid = session.getId();
		String uri = request.getRequestURI();
		String userName = StringUtil.null2Str(session.getAttribute(UserSession._USER_NAME));
		if(uri.contains("/welcome")||uri.contains("/systemManager/showImage")||uri.contains("/systemManager/login")||uri.contains("/systemManager/logout")){
			
		}else{
			// 检查用户是否被剔除
			boolean isRemove = true;
			if(!userName.equals(Constant.ADMIN)){
				String cacheSessionid = StringUtil.null2Str(commonCacheService.getObject("session", userName));
				if(StringUtil.isNullStr(cacheSessionid)){
					//登陆
					controller.setAttr("error", "登陆失效，重新登陆！");
					controller.redirect("/view/welcome/index.jsp");
					return;
				}else{
					if(!curSid.equals(cacheSessionid)){
						controller.setAttr("error", "此账号在其他处登陆，请重新登陆！");
						controller.redirect("/view/welcome/repeatLanding.html");
						return;
					}
				}
			}

//			Map<String,Object> sessionMap = (Map<String, Object>) commonCacheService.getObject("sessionMap");
//			if(sessionMap!=null&&!sessionMap.isEmpty()){
//				 for (String key : sessionMap.keySet()) {
//					 if(curSid.equals(key)){
//						 // 合法
//						 isRemove = false;
//					 }
//				 }
//			}
//			
//			if(isRemove){
//				controller.setAttr("error", "此账号在其他处登陆，请重新登陆！");
//				controller.redirect("/view/welcome/index.jsp");
//				return;
//			}
			
			
			if(!checkIgnoreURI(uri)){
				// uri请求需要check
				String isLogin = StringUtil.null2Str(session.getAttribute(UserSession._USER_IS_LOGIN));
				if (!"true".equals(isLogin)){
					controller.setAttr("error", "登陆失效，重新登陆！");
					controller.redirect("/view/welcome/index.jsp");
					return;
				}
	
				if(!Constant.ADMIN.equals(userName)&&!BusinessUtil.havePerm(uri,StringUtil.null2Str(session.getAttribute(UserSession._USER_RESOURCES)))){
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
	    ai.invoke();
	}
	
	private boolean checkIgnoreURI(String uri){
		boolean flag = false;
		if(Constant.commonResourcePathList!=null&&Constant.commonResourcePathList.size()>0){
			for(String url : Constant.commonResourcePathList){
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
		if("/systemManager/showBlank".equals(uri)){
			flag = true ;
		}
		return flag;
	}
}
