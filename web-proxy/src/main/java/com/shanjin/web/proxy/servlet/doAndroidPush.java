package com.shanjin.web.proxy.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.log4j.Logger;








import com.alibaba.fastjson.JSONObject;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.push.AndroidPushSend;
import com.shanjin.util.PushConfig;
import com.shanjin.web.proxy.util.StringUtil;

@WebServlet("/doAndroidPush")
public class doAndroidPush extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String host = "http://sdk.open.api.igexin.com/apiex.htm";

	// private static Logger logger = Logger.getLogger(doPushToSHB.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("进入到doAndroidPush");		
		
		String androidPushInfoListStr = java.net.URLDecoder.decode(request.getParameter("androidPushInfoList"),"utf-8");
		String msg = request.getParameter("msg");
		String configMap=request.getParameter("configMap");
	
		String concatKey=request.getParameter("concatKey");
		if(!Constant.PRESSURETEST){//如果压力测试则不打印
			BusinessUtil.writeLog("push","安卓版推送时间： " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			BusinessUtil.writeLog("push","推送内容：" + msg);
		}
		if (androidPushInfoListStr != null && msg != null) {
			List<Map<String, Object>> androidPushInfoList = (List<Map<String, Object>>) JSONObject.parse(androidPushInfoListStr);
			Map<String, Object> configObject = JSONObject.parseObject(configMap);
			Map<String, Object> msgObject = JSONObject.parseObject(msg);

			//判断是否开启Android推送
			String isOpenAndroidOrderPush=configObject.get("is_open_androidOrderPush")==null?"1":configObject.get("is_open_androidOrderPush").toString();//默认开启
			if(isOpenAndroidOrderPush.equals("0")){//关闭订单Android推送
				BusinessUtil.writeLog("push","Android推送关闭");
				return ;
			}
			AndroidPushSend.send(configObject,msgObject,concatKey, androidPushInfoList);
		} else {
			System.out.println("推送对象为空");
		}
	}
}
