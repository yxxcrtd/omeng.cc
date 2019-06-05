package com.shanjin.common.interceptor;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseHeaderFilter implements Filter{
	FilterConfig fc;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,  
            FilterChain chain) throws IOException, ServletException {  
        HttpServletResponse response = (HttpServletResponse) res;  
        // set the provided HTTP response parameters  
        for (Enumeration e = fc.getInitParameterNames(); e.hasMoreElements();) {  
            String headerName = (String) e.nextElement();  
            response.addHeader(headerName, fc.getInitParameter(headerName));  
        }  
        // pass the request/response on  
        chain.doFilter(req, response);  
    }  
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.fc = filterConfig; 
	}
	
	@Override
	public void destroy() {
		this.fc = null; 
	} 
}
