package com.shanjin.common.interceptor;

import com.shanjin.cache.service.ICommonCacheService;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.dao.IUserDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证token拦截器
 *
 * @author lihuanmin
 */
public class ValidateTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private ICommonCacheService commonCacheService;
    @Autowired
    private IUserDao userDao;

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

        String time = request.getParameter("time");
        String clientId = request.getParameter("clientId");
        String phone = request.getParameter("phone");
        String token = request.getParameter("token");
        //time校验 //cacahe查找
        String lastTime = getLastTime(clientId);
        String userKey = getUserKey(phone, clientId);
        String param = "time=" + time + ",phone=" + phone + ",clientId=" + clientId + ",token=" + token;
        //非空教研
        if (StringUtils.isEmpty(time) || StringUtils.isEmpty(clientId) || StringUtils.isEmpty(phone) || StringUtils.isEmpty(token)) {
            logUnsuccessHandler(request, response, param);
            return false;
        }
        //time校验
        if (time.equals(lastTime)) {
            logUnsuccessHandler(request, response, param);
            return false;
        }
        //userKey非空教研
        if (StringUtils.isEmpty(userKey)) {
            logUnsuccessHandler(request, response, param);
            return false;
        }
        String validToken = genMd5(time, clientId, phone, userKey);
        //token equals 校验
        if (!token.equalsIgnoreCase(validToken)) {
            param += ",validToken=" + validToken;
            logUnsuccessHandler(request, response, param);
            return false;
        }
        //更新lastTime
        updatelatestValidTime(clientId, time);

        return true;
    }

    private void logUnsuccessHandler(HttpServletRequest request, HttpServletResponse response, String logInfo) throws IOException {
        BusinessUtil.writeLog("interface", DateUtil.getNowYYYYMMDDHHMMSS() + "：" + logInfo);
        response.sendRedirect(request.getContextPath() + "/error/vaildateFailed");
    }


    /**
     * MD5是生成
     *
     * @param time
     * @param clientId
     * @param phone
     * @param userKey
     * @return
     */
    private String genMd5(String time, String clientId, String phone, String userKey) {
        StringBuilder sb = new StringBuilder("");
        sb.append(time).append(clientId).append(phone).append(userKey);
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes()).toUpperCase();
    }


    /**
     * 获取用户key信息
     *
     * @param phone
     * @param clientId
     * @return
     */
    private String getUserKey(String phone, String clientId) {
        String userKey = (String) commonCacheService.getObject("userKey", clientId + phone);
        if (userKey == null) {

            Map<String, Object> param = new HashMap<>();
            Map<String, Object> userInfo = null;
            param.put("phone", phone);
            userInfo = userDao.getUserInfoByPhone(userInfo);
            if (userInfo != null && !userInfo.isEmpty()) {
                userKey = (String) userInfo.get("user_key");
                if (!StringUtils.isEmpty(userKey)) {
                    commonCacheService.setObject(userKey, "userKey", clientId + phone);
                }
            }
        }
        return userKey;
    }

    /**
     * 缓存查找上次请求时间
     *
     * @param clientId
     * @return
     */
    private String getLastTime(String clientId) {
        return (String) commonCacheService.getObject("validateTime", clientId);
    }

    /**
     * 更新时间
     *
     * @param clientId
     * @param time
     */
    private void updatelatestValidTime(String clientId, String time) {
        commonCacheService.setObject(time, 60 * 10, "validateTime", clientId);
    }

    public static void main(String[] args) {

    }

}
