package com.shanjin.common.interceptor;

import com.shanjin.common.constant.Constant;
import com.shanjin.common.security.SecurityFactoy;
import com.shanjin.common.security.SecurityInterface;
import com.shanjin.common.util.ClientParamUtil;
import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.service.IUserInfoService;
import com.shanjin.service.IValidateService;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * 请求合法性检查拦截器
 *  1. 检查头部是否合法
 *  2. 对于头部合法的请求，检查客户端生成的加密串与服务器端生成的加密串是否已一致
 * 
 * @author Revoke 2016.5.2
 *
 */
public class ValidateRequestInterceptor implements HandlerInterceptor {

    @Resource
    private IValidateService validateService;
    
    @Resource
    private IUserInfoService userService;
    
    private Logger logger = Logger.getLogger(ClientLogInterceptor.class);

    @SuppressWarnings("unchecked")
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
            throws Exception {
    }

	/**
	 * CLIENT-TYPE 1|2
	 * VERSION
	 * APIVERSION 1.0
	 * TIME 时间戳
	 * POSTFIX : encryptStr = AES_encrypt(clientType+"|"+version+"|"+apiVersion+"|"+TIME,md5(TIME))
	 * @param request
	 * @param response
	 * @param handler
	 * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {      
    	String ip = IPutil.getIpAddr(request);
		String path = request.getServletPath();

		if(Constant.PRESSURETEST){
			return true;
		}
		
//		logParams(request);
    	if (isInvalidHeader(request)){
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.addHeader("ERROR",genWarnInfo(ip,path));
			PrintWriter out = response.getWriter();
			out.print(genWarnInfo(ip,path));
			return false;
        }
        
        if (isInvalidSecurityStr(request)){
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.addHeader("ERROR","SecurityError "+genWarnInfo(ip,path));
			PrintWriter out = response.getWriter();
			out.print("SecurityError "+genWarnInfo(ip,path));
        	return false;
        }
        
        return true;
    }

    /**
     * 
     * @param request
     * @return
     */
    private boolean isInvalidSecurityStr(HttpServletRequest request) {
    	String apiVersion = stringProcess(ClientParamUtil.getAPIVersion(request));
		
    	String securityInfo = stringProcess(ClientParamUtil.getSecurityInfo(request));
    	
    	//新版，无TOKEN控制的接口将启用 安全控制机制
    	if  (apiVersion!=""){
    		   if (!SecurityFactoy.isSupportVersion(apiVersion)){
    			   	return true;
    		   }else{
    			   SecurityInterface securityInterface=SecurityFactoy.getSecurity(apiVersion);
    			   String encryptStr = securityInterface.encode(request);
    			   return !encryptStr.equals(securityInfo);
    		   }
    	}
    	
		return false;
	}

	/**
     * 判断请求内容是否含有必须的栏位。 
     * @param request
     * @return
     */
	private boolean isInvalidHeader(HttpServletRequest request) {
		String clientType = stringProcess(ClientParamUtil.getClientType(request));
		if (StringUtil.isNullStr(clientType)){
			return true;
		}

		String version = stringProcess(ClientParamUtil.getVersion(request));
		if (StringUtil.isNullStr(version)){
			return true;
		}

		String apiVersion = stringProcess(ClientParamUtil.getAPIVersion(request));
		
		String securityInfo = stringProcess(ClientParamUtil.getSecurityInfo(request));
		//apiVersion有值，加密信息必须不为空
		if (!StringUtil.isNullStr(apiVersion) && securityInfo=="")
				return true;
		
		return false;
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
    
    private String  genWarnInfo(String ip,String path) {
    	StringBuilder  builder=new StringBuilder("Invalid request IP:");
    	builder.append(ip).append("   path：").append(path);
    	return builder.toString();
    }
    
    
    
    private void logParams(HttpServletRequest request) {
		try {
			Enumeration hnames=request.getHeaderNames();
			System.out.println("\n---"+request.getServletPath()+"-------header--------------");
			for (Enumeration e = hnames ; e.hasMoreElements() ;) {
                String thisName=e.nextElement().toString();
                String thisValue=request.getHeader(thisName);
                System.out.println(thisName+"=="+thisValue);
            }
			System.out.println("---"+request.getServletPath()+"-------params--------------\n\n");
			Enumeration rnames=request.getParameterNames();
			for (Enumeration e = rnames ; e.hasMoreElements() ;) {
                String thisName=e.nextElement().toString();
                String thisValue=request.getParameter(thisName);
                System.out.println(thisName+"=="+thisValue);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
