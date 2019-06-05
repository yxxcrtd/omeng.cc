package com.shanjin.web.proxy.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.shanjin.common.util.HttpRequest;

public class IpServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		String ip = request.getParameter("ip");
		PrintWriter pw = response.getWriter();
		
		if("sina".equals(type)) {
			String param = "format=json&ip=" + ip;
			String result = HttpRequest.sendGet("http://int.dpool.sina.com.cn/iplookup/iplookup.php", param);
			pw.print(result);
			pw.flush();
		} else if ("baidu".equals(type)) {
			String param = "ak=4b85605436f29eae01eb6c95ce1c0cf7&ip=" + ip + "&coor=bd09ll";
			String result = HttpRequest.sendGet("http://api.map.baidu.com/location/ip", param);
			pw.print(result);
			pw.flush();
		}
		pw.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
