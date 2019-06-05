package com.shanjin.common.interceptor;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hazelcast.util.StringUtil;
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

    @SuppressWarnings("unchecked")
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
            throws Exception {
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map params = request.getParameterMap();
        
        boolean success = true;
//        // 判断是否存在token参数
//        if (params.containsKey(Constant.DEFAULT_TOKEN_NAME)) {
//            String[] tokens = (String[]) (String[]) params.get(Constant.DEFAULT_TOKEN_NAME);
//            if ((tokens == null) || (tokens.length < 1)) {
//            } else {
//                token = tokens[0];
//            }
//            // 验证token是否存在
//            Object data = memcacheHelper.getDataFormMemcache(Constant.DEFAULT_TOKEN_NAME, token);
//            if (data != null) {
//                System.out.println("已经验证过");
//            } else {
//                String authKey = validateService.getAuthKey();
//                String text = AESUtil.decrypt(token, authKey);
//                // 如果验证通过
//                if (validateToken(text)) {
//                    memcacheHelper.setDataFormMemcache(Constant.DEFAULT_TOKEN_NAME, token, token,
//                            Constant.DEFAULT_TOKEN_TIME);
//                    success = true;
//                }
//            }
//        }
//        if (success == false) {
//            response.sendRedirect(request.getContextPath() + "/error/vaildateFailed");
//        }
        
        String time = (String)request.getParameter("time");
        String clientId = (String)request.getParameter("clientId");
        String phone = (String)request.getParameter("phone");
        String token = (String)request.getParameter("token");
        
        if(StringUtil.isNullOrEmpty(time) || StringUtil.isNullOrEmpty(clientId) || StringUtil.isNullOrEmpty(phone) || StringUtil.isNullOrEmpty(token)) {
        	success = false;
        } else {
        	String lastTime = validateService.lastValidatedTime(clientId);
        	if(time.equals(lastTime)) {
        		success = false;
        	} else {
        		String userKey = userService.getUserKey(phone, clientId);
        		if(StringUtil.isNullOrEmpty(userKey)) {
        			success = false;
        		} else {
        			String validToken = MD5Util.MD5_32(time+ ""+ clientId + "" + phone + "" +userKey);
        			if(!validToken.equals(token)) {
        				success = false;
        			} else {
        				validateService.updateLastValidatedTime(clientId, time);
        			}
        		}
        	}
        	
        }
        if (!success) {
        	response.sendRedirect(request.getContextPath() + "/error/vaildateFailed");
        }
        
        return success;
    }

    public static void main(String[] args) {
        // validateToken("1|2|3");
    }

}
