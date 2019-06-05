package com.shanjin.common.aspect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;


import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.shanjin.common.util.IPutil;
  
/** 
 * 切点类 
 */  
@Aspect  
@Component  
public  class SystemLogAspect {  
    //本地异常日志记录对象  
     private  static  final Logger logger = Logger.getLogger(SystemLogAspect.class);
  
    //Controller层切点  
    @Pointcut("@annotation(com.shanjin.common.aspect.SystemControllerLog)")  
     public  void controllerAspect() {  
    }  
    long startTime=0;
    long endTime=0;
    String logInfo="";
    /** 
     * 前置通知 用于拦截Controller层记录用户的操作 
     * 
     * @param joinPoint 切点 
     */  
    @Before("controllerAspect()")  
     public  void doBefore(JoinPoint joinPoint) {  
    			startTime=new Date().getTime();
    			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
    			
    			//获取操作内容
    			try {    
    				String nowTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    				String ip= IPutil.getIpAddr(request);
    				String requestMethod=request.getMethod();
    				String controllerPath=joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()";
    				String methodDescription=getControllerMethodDescription(joinPoint);
    				StringBuffer paraString=new StringBuffer("[ ");
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
    				String paramNames=paraString.toString();    				
    				logInfo=nowTime+"\t"+ip+"\t"+requestMethod+"\t"+controllerPath+"\t"+paramNames+"\t"+methodDescription;
    				System.err.println("接口信息："+logInfo);
//    				logger.info(logInfo);
				} catch (Exception e) {
					e.printStackTrace();
					
				}
    }  

    @AfterReturning(value="@annotation(com.shanjin.common.aspect.SystemControllerLog)", argNames="rtv", returning="rtv")  
     public  void doAfter(JoinPoint  joinPoint,Object rtv) {    		
		String returnValue=rtv.toString();
		System.err.println("返回值信息："+returnValue);
    	endTime=new Date().getTime();
    	System.err.println("接口执行时间："+(endTime-startTime)+"ms");

		logger.info(logInfo+"\t"+returnValue+"\t"+(endTime-startTime)+"\n");
		
    }
  
    /** 
     * 获取注解中对方法的描述信息 用于Controller层注解 
     * 
     * @param joinPoint 切点 
     * @return 方法描述 
     * @throws Exception 
     */  
     public  static String getControllerMethodDescription(JoinPoint joinPoint)  throws Exception {  
         Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
         String description = method.getAnnotation(SystemControllerLog.class).description();
         return description;
    }  
}  