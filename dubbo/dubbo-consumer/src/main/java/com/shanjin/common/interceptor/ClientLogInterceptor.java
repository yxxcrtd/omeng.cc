package com.shanjin.common.interceptor;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.shanjin.cache.util.IpCityUtil;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.constant.TopicConstants;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.common.util.ClientParamUtil;
import com.shanjin.common.util.DateUtil;
import com.shanjin.common.util.StringUtil;
import com.shanjin.kafka.ProducerFactory;
import com.shanjin.kafka.ProducerInterface;

public class ClientLogInterceptor implements HandlerInterceptor{
	private final Logger log = Logger.getLogger(ClientLogInterceptor.class);

	private Log accessLoger = LogFactory.getLog("reqAndResLog");
	
	private static ProducerInterface Producer; 
	
	static
	{
		if (Constant.KAFKA_LOG)
			Producer = ProducerFactory.getProducer(false,BusinessUtil.getIpAddress());
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception arg3)
			throws Exception {
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object arg2, ModelAndView arg3) throws Exception {
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
		try{
			String info = getLogInfo(request);
			if(!StringUtil.isNullStr(info)){
				
				if(!Constant.PRESSURETEST){
					accessLoger.info(info);
				}
				
				if (Constant.KAFKA_LOG){
					Producer.sendMsg(TopicConstants.CLIENT_ACCESS_LOG, BusinessUtil.getLogId(), info);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return true;
		}

		return true;
	}
	
	private String getLogInfo(HttpServletRequest request) throws Exception {
		String returnStr = "";

		String uuid = stringProcess(ClientParamUtil.getUUID(request));
		if(StringUtil.isNullStr(uuid)){
			return returnStr;
		}
		String url = stringProcess(request.getRequestURI());
		String referer = stringProcess(StringUtil.null2Str(request.getHeader("Referer")).split("\\?")[0]);
		String phone = stringProcess(ClientParamUtil.getPhone(request));
		String clientType = stringProcess(ClientParamUtil.getClientType(request));
		String ua = stringProcess(ClientParamUtil.getUa(request));
		String version = stringProcess(ClientParamUtil.getVersion(request));
		String channel = stringProcess(ClientParamUtil.getChannel(request));
		String model = stringProcess(ClientParamUtil.getModel(request));
		String resolution = stringProcess(ClientParamUtil.getResolution(request));
		String system = stringProcess(ClientParamUtil.getSystem(request));
		String ip = stringProcess(ClientParamUtil.getIpAddr(request));
		String appType = stringProcess(request.getParameter("appType"));
		String queryStr = request.getQueryString();
		String clientFlag = stringProcess(ClientParamUtil.getClientFlag(request));
		// 时间|uuid|请求相对路径|源页面绝对路径|登陆手机号|客户端类型|appType|clientFlag|ua|客户端版本|渠道ID|客户端ip地址|省|市|手机型号|分辨率|操作系统|queryStr
		returnStr = DateUtil.formatDate("yyyyMMddHHmmss", new Date()) + "|"
				+ uuid + "|" + url + "|" + referer + "|" + phone + "|"
				+ clientType + "|" + appType + "|" + clientFlag + "|" + ua + "|" + version + "|"
				+ channel + "|" + ip + "|"+IpCityUtil.getCity(ip)+"|"+ model + "|"+ resolution + "|"+ system + "|" +queryStr ;
		return returnStr;
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



}
