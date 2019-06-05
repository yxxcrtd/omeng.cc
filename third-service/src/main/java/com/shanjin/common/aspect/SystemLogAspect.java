package com.shanjin.common.aspect;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 切点类
 */
@Aspect
@Component
public class SystemLogAspect {
    // 本地异常日志记录对象
    private static final Logger logger = Logger.getLogger(SystemLogAspect.class);

    // Controller层切点
    @Pointcut("@annotation(com.shanjin.common.aspect.SystemControllerLog)")
    public void controllerAspect() {
    }

    /**
     * 前置通知 用于拦截Controller层记录用户的操作
     * 
     * @param joinPoint
     *            切点
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        // 获取操作内容
        try {

            System.out.println(request.getMethod() + " 请求方法:"
                    + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            logger.info(request.getMethod() + " 请求方法:"
                    + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            System.out.println("方法描述:" + getControllerMethodDescription(joinPoint));
            logger.info("方法描述:" + getControllerMethodDescription(joinPoint));
            StringBuffer paraString = new StringBuffer("[ ");
            Map<String, String[]> paramMap = request.getParameterMap();
            Set<Entry<String, String[]>> set = paramMap.entrySet();
            Iterator<Entry<String, String[]>> it = set.iterator();
            while (it.hasNext()) {
                Entry<String, String[]> entry = it.next();
                paraString.append(entry.getKey() + ":");
                for (String i : entry.getValue()) {
                    paraString.append(i);
                }
                paraString.append("; ");
            }
            paraString.append("]");
            System.out.println("请求参数:" + paraString.toString());
            logger.info("请求参数:" + paraString.toString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     * 
     * @param joinPoint
     *            切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String description = method.getAnnotation(SystemControllerLog.class).description();
        return description;
    }
}