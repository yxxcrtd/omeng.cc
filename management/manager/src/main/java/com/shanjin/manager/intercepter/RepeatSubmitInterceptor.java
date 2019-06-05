package com.shanjin.manager.intercepter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.cache.service.impl.CommonCacheServiceImpl;
import com.shanjin.manager.utils.StringUtil;

public class RepeatSubmitInterceptor implements Interceptor{
	private static ICommonCacheService commonCacheService = new CommonCacheServiceImpl();
	public  static final String IGNORE_CHECK_URL = "/agent/getAgent,/merchants/exportExcel,/merchants/exportStoreAuditExcel";
	
	public void intercept(ActionInvocation ai) {
		Controller controller = ai.getController(); 
		HttpServletRequest request = controller.getRequest();
		String uri = request.getRequestURI();
		if(checkIgnoreURI(uri)){
			System.out.println("拦截器："+uri);
		Cookie[] coo= request.getCookies();
		if(coo!=null && coo.length>0){
			String getLastAcess=(String) commonCacheService.getObject("manager_url", coo[0].getValue().toString(),uri);
			if(!StringUtil.isNullStr(getLastAcess)){
				return;
		    }else{
		    	commonCacheService.setObject("repeat_url",30, "manager_url", coo[0].getValue().toString(),uri);
		    }
		}else{
			controller.redirect("/view/welcome/index.jsp");
		}
		}
	    ai.invoke();
	}
	
	private boolean checkIgnoreURI(String uri){
		boolean flag = false;
		if(IGNORE_CHECK_URL!=null&&!IGNORE_CHECK_URL.equals("")){
			String[] urls=IGNORE_CHECK_URL.split(",");
			for(int i=0;i<urls.length;i++){
				if(urls[i].equals(uri)){
					return true;
				}
			}
		}
		return flag;
	}	
	
}
