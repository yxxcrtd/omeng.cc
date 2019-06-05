package com.shanjin.common.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hazelcast.util.StringUtil;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.MD5Util;
import com.shanjin.service.IUserInfoService;
import com.shanjin.service.IValidateService;

/**
 * 验证token拦截器
 * 
 * @author lihuanmin
 *
 */
public class ValidateTokenInterceptor implements HandlerInterceptor {

    @Resource
    private IValidateService validateService;
    
    @Resource
    private IUserInfoService userService;
    
    private Logger logger = Logger.getLogger(ValidateTokenInterceptor.class);

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
            throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        boolean success = true;        

		if(Constant.PRESSURETEST){
			return true;
		}

        String isPush = (String)request.getParameter("isPush");
        String time = Constant.EMPTY;;
        String clientId = Constant.EMPTY;
        if (com.shanjin.common.util.StringUtil.null2Str(isPush).equals("1")) {
        	time = (String)request.getParameter("pushTime");
        	clientId = (String)request.getParameter("pushClientId");
        } else {
        	time = (String)request.getParameter("time");
        	clientId = (String)request.getParameter("clientId");
        }
        String phone = (String)request.getParameter("phone");
        String token = (String)request.getParameter("token");
        
        if(StringUtil.isNullOrEmpty(time) || StringUtil.isNullOrEmpty(clientId) || StringUtil.isNullOrEmpty(phone) || StringUtil.isNullOrEmpty(token)) {
			String param="time="+time+",phone="+phone+",clientId="+clientId+",token="+token;
			BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
        	success = false;
        } else {
        	String lastTime = validateService.lastValidatedTime(clientId);
        	if(time.equals(lastTime)) {
				String param="time="+time+",lastTime="+lastTime+",phone="+phone+",clientId="+clientId+",token="+token;
				BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
        		success = false;
        	} else {
        		String userKey = userService.getUserKey(phone, clientId);
        		if(StringUtil.isNullOrEmpty(userKey)) {
					String param="time="+time+",userKey="+userKey+",phone="+phone+",clientId="+clientId+",token="+token;
					BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
        			success = false;
        		} else {
        			String validToken = MD5Util.MD5_32(time+ ""+ clientId + "" + phone + "" +userKey);
        			if(!validToken.equals(token.toUpperCase())) {
						String param="time="+time+",userKey="+userKey+",phone="+phone+",clientId="+clientId+",token="+token+",validToken="+validToken;
						BusinessUtil.writeLog("interface",DateUtil.getNowYYYYMMDDHHMMSS()+"："+param);
        				success = false;
        			} else {
        				validateService.updateLastValidatedTime(clientId, time);
        			}
        		}
        	}        	
        }
        if (!success) {
			logger.error("---------------token问题---------------");
        	response.sendRedirect(request.getContextPath() + "/error/vaildateFailed");
        }
        
        return success;
    }

    public static void main(String[] args) {
        // validateToken("1|2|3");
    }

}
