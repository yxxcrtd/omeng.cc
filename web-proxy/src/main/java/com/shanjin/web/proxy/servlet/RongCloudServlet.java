package com.shanjin.web.proxy.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.rong.cloud.RongCloudApiHttpClient;

@WebServlet(name = "rongCloudService", urlPatterns = { "/rongCloudService" })
public class RongCloudServlet extends HttpServlet{

	/**
	 *  融云服务
	 */
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uid = request.getParameter("uid");
		String name = request.getParameter("name");
		String portraitUri = request.getParameter("portraitUri");
		PrintWriter pw = response.getWriter();
		try{
			String token = RongCloudApiHttpClient.getRongCloudToken(uid, name, portraitUri);
			JSONObject jsonObject = new JSONObject();  
		    jsonObject.put("token", token);  
			pw.print(jsonObject);
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			pw.close();	
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
