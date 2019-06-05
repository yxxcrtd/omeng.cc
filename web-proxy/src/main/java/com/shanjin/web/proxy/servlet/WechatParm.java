package com.shanjin.web.proxy.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.shanjin.web.proxy.wechat.GetWxOrderno;

/**
 * Servlet implementation class wechatParm
 */
@WebServlet("/wechatParm")
public class WechatParm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(WechatParm.class);

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
		PrintWriter pw = response.getWriter();
		//url
		String createOrderURL = request.getParameter("createOrderURL");
		//xml字符串
		String xml = request.getParameter("xml");
		Map<String,Object> returnMap=new HashMap();
		JSONObject jsonObject = new JSONObject();
				try {
					returnMap = GetWxOrderno.getPayNo(createOrderURL, xml);
				    jsonObject.put("returnCode", returnMap.get("returnCode").toString());  
				    jsonObject.put("returnData", returnMap.get("returnData").toString());  
					System.out.println("获取到的预支付jsonObject：" + jsonObject);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			
			pw.print(jsonObject);
			pw.flush();
		pw.close();
	}

}
