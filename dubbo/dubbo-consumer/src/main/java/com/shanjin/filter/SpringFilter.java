package com.shanjin.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.shanjin.service.ICommonService;

@Service("parameterFilter")
public class SpringFilter implements Filter {
	@Reference
	private ICommonService commonService;


	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
					Map<String,String[]> m = new HashMap<String,String[]>(request.getParameterMap());  
					m.put("clientId", new String[]{"1111"});  
					request = new ParameterRequestWrapper((HttpServletRequest)request, m);  
					
					chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {


	}
	
	
	
	class ParameterRequestWrapper extends HttpServletRequestWrapper {  
		  
	    private Map<String, String[]> params;  
	  
	    public ParameterRequestWrapper(HttpServletRequest request,  
	            Map<String, String[]> newParams) {  
	        super(request);  
	          
	        this.params = newParams;  
	    }  
	  
	    public String getParameter(String name) {  
	        String result = "";  
	          
	        Object v = params.get(name);  
	        if (v == null) {  
	            result = null;  
	        } else if (v instanceof String[]) {  
	            String[] strArr = (String[]) v;  
	            if (strArr.length > 0) {  
	                result =  strArr[0];  
	            } else {  
	                result = null;  
	            }  
	        } else if (v instanceof String) {  
	            result = (String) v;  
	        } else {  
	            result =  v.toString();  
	        }  
	          
	        return result;  
	    }  
	  
	    public Map getParameterMap() {  
	        return params;  
	    }  
	  
	    public Enumeration getParameterNames() {  
	        return new Vector(params.keySet()).elements();  
	    }  
	  
	    public String[] getParameterValues(String name) {  
	        String[] result = null;  
	          
	        Object v = params.get(name);  
	        if (v == null) {  
	            result =  null;  
	        } else if (v instanceof String[]) {  
	            result =  (String[]) v;  
	        } else if (v instanceof String) {  
	            result =  new String[] { (String) v };  
	        } else {  
	            result =  new String[] { v.toString() };  
	        }  
	          
	        return result;  
	    }  
	  
	}  

}
