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

import com.alibaba.fastjson.JSONObject;
import com.shanjin.common.constant.Constant;
import com.shanjin.common.util.BusinessUtil;
import com.shanjin.push.IosPushSend;

/**
 * Servlet implementation class doIosPush
 */
@WebServlet("/doIosPush")
public class doIosPush extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public doIosPush() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String iosPushInfoList = request.getParameter("iosPushInfoList");
		String msg = request.getParameter("msg");
		String configMap=request.getParameter("configMap");
		if(!Constant.PRESSURETEST){//如果压力测试则不打印
			BusinessUtil.writeLog("push","IOS推送时间： " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			BusinessUtil.writeLog("push","推送内容：" + msg);
		}
		List<Map<String, Object>> iosPushInfoListObject = (List<Map<String, Object>>) JSONObject.parse(iosPushInfoList);
		Map<String, Object> configObject = JSONObject.parseObject(configMap);

		//判断是否开启IOS推送
		String isOpenIosOrderPush=configObject.get("is_open_iosOrderPush")==null?"1":configObject.get("is_open_iosOrderPush").toString();//默认开启
		if(isOpenIosOrderPush.equals("0")){//关闭订单IOS推送
			BusinessUtil.writeLog("push","IOS推送关闭");
			return ;
		}
		Map<String, Object> msgObject = JSONObject.parseObject(msg);
		IosPushSend.send(configObject,msgObject, iosPushInfoListObject);
		
//		String cert = request.getParameter("cert");
//
//		System.out.println("");
//		System.out.println("====苹果推送日志 时间 " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "====");
//		System.out.println("iosPushInfoList:" + iosPushInfoList);
//		System.out.println("msg:" + msg);
//		System.out.println("cert:" + cert);
//		if (iosPushInfoList != null && msg != null && cert != null) {
//			List<Map<String, Object>> iosPushInfoListObject = (List<Map<String, Object>>) JSONObject.parse(iosPushInfoList);
//			Map<String, Object> msgObject = JSONObject.parseObject(msg);
//			Map<String, Object> certObject = JSONObject.parseObject(cert);
//			if (iosPushInfoListObject != null && iosPushInfoListObject.size() > 0) {
//				for (Map<String, Object> map : iosPushInfoListObject) {
//					System.out.println("map:" + map);
//					IosPushUtil.push(map, msgObject, certObject);
//				}
//			}
//		}
	}

}
