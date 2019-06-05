package com.shanjin.common.interceptor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor; 
import org.springframework.web.servlet.ModelAndView;

import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.MD5Util;

public class BusinessInterceptor implements HandlerInterceptor {

	private String getRequestToken(HttpServletRequest request){
		String token="";
		String ip=IPutil.getIpAddr(request);
		String path=request.getServletPath();
		StringBuffer paraString=new StringBuffer("[ ");;
        Map<String, String[]> paramMap = request.getParameterMap();  
        Set<Entry<String, String[]>> set = paramMap.entrySet();  
        Iterator<Entry<String, String[]>> it = set.iterator();  
        while (it.hasNext()) {  
            Entry<String, String[]> entry = it.next();  
            paraString.append(entry.getKey()+":");
            for (String i : entry.getValue()) {  
                paraString.append(i);
            }  
            paraString.append("; ");
        }
        paraString.append("]");
        token=MD5Util.MD5_32(ip+path+paraString.toString());
        return  token;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		String formhash=getRequestToken(request);
		Set<String> token= (Set<String>) request.getSession().getAttribute("token");
		 if (token== null || !token.contains(formhash)) {
		 }else{
			 token.remove(formhash);
			 request.getSession().setAttribute("token", token);
		 }
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2, ModelAndView arg3) throws Exception {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		//获取操作内容
		try {
            String formhash=getRequestToken(request);
            Set<String> token= (Set<String>)  request.getSession().getAttribute("token");
            if (token== null) {
                token=new HashSet<String>();
              }
              // 检测重复问题
              while (token.contains(formhash)) {
            	  request.getRequestDispatcher("/error/repeatSubmit").forward(request,response);
                return false;
              }
              // 保存到session里面
              token.add(formhash);
              // 保存
              request.getSession().setAttribute("token", token);
              return true;
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}




}
