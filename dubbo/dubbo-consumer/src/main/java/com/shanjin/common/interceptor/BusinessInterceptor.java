package com.shanjin.common.interceptor;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import redis.clients.jedis.Jedis;

import com.shanjin.cache.util.JedisPoolUtil;
import com.shanjin.cache.util.SerializeUtil;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.IPutil;
import com.shanjin.common.util.MD5Util;
import com.shanjin.controller.CommonController;
import com.shanjin.controller.CustomOrderController;

public class BusinessInterceptor implements HandlerInterceptor,ApplicationContextAware {
	Logger logger = LoggerFactory.getLogger(CustomOrderController.class);
	
	private ApplicationContext ctx;
	
	private String getRequestToken(HttpServletRequest request) {
		String token = "";
		String ip = IPutil.getIpAddr(request);
		String path = request.getServletPath();
		StringBuffer paraString = new StringBuffer("[ ");
		Map<String, String[]> paramMap = request.getParameterMap();
		Set<Entry<String, String[]>> set = paramMap.entrySet();
		Iterator<Entry<String, String[]>> it = set.iterator();
		while (it.hasNext()) {
			Entry<String, String[]> entry = it.next();
			
			//2016-6-16 日 时间和TOKEN 总是变化的，不能作为重复提交的依据  Revoke
			if (entry.getKey().equals("time") || entry.getKey().equals("token")){
				continue;
			}
			paraString.append(entry.getKey() + ":");
			for (String i : entry.getValue()) {
				paraString.append(i);
			}
			paraString.append("; ");
		}
		paraString.append("]");
		token = MD5Util.MD5_32(ip + path + paraString.toString());
		
		return token;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3) throws Exception {
		try {
			
			String key = "api_request_";
			String formhash = getRequestToken(request);
			key = key + formhash;
			Jedis jedis = null;
			try {
				jedis = JedisPoolUtil.getJedis();
				if (jedis.exists(key.getBytes()) && getRestrictUpdateTime(request)==-1) {
					// 判断缓存中key是否存在，若存在，del ,对于限制保存的URL，通过过期机制来处理
					jedis.del(key.getBytes());
				}
			} catch (Exception e) {
				// 释放redis对象
				JedisPoolUtil.returnBrokenRes(jedis);
				e.printStackTrace();
			} finally {
				// 返还到连接池
				JedisPoolUtil.returnRes(jedis);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3) throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 获取操作内容
		try {
			//如果压力测试
			if(Constant.PRESSURETEST){
				return true;
			} 
			
			String key = "api_request_";
			String formhash = getRequestToken(request);
			key = key + formhash;
			Jedis jedis = null;
			try {
				jedis = JedisPoolUtil.getJedis();
								
				if (jedis.setnx(key.getBytes(), SerializeUtil.serialize(formhash))==0) {
					if (jedis.ttl(key.getBytes())!=-1 && jedis.ttl(key.getBytes())!=-2){
					   if (Constant.DEVMODE){
						   System.out.println("-------------重复提交-------key ----"+new String(key.getBytes()));
						   System.out.println("-----------ttl------"+jedis.ttl(key.getBytes()));
					   }
						request.getRequestDispatcher("/error/repeatSubmit").forward(request, response);
						return false;
					}
				}
				Integer interval=getRestrictUpdateTime(request);
				if (interval>-1){
					jedis.expire(key.getBytes(), interval);
				}else{
					//未限制的，10 秒钟过期，防止afterCompletion 走不到
					jedis.expire(key.getBytes(),10);
				}
					
			} catch (Exception e) {
				// 释放redis对象
				JedisPoolUtil.returnBrokenRes(jedis);
				e.printStackTrace();
			} finally {
				// 返还到连接池
				JedisPoolUtil.returnRes(jedis);
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	//判断是否为限制频繁提交的请求, -1 未限制  其它 过期的秒数
	private int getRestrictUpdateTime(HttpServletRequest request){
		     String path=request.getServletPath();
		     
		     CommonController commonCtrl = ctx.getBean(CommonController.class);
			 Map<String,Integer> restrictUrl = commonCtrl.getRepeateUpdate();
			 
			 if (restrictUrl==null || restrictUrl.size()<1){
				 logger.warn("限制频繁保存的URL配置表为空！");
				 return -1;
			 }
			
			 for(Entry<String,Integer> entry:restrictUrl.entrySet()){
				 	 if ( path.contains(entry.getKey()) )
				 		   return  entry.getValue();
			 }
					 
		     return -1;		
	}
	
	/** set Object */
	public String redisSet(String key, int seconds, Object object) {

		return null;
	}

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
			this.ctx = ctx;
		
	}

}
