<%
/* *
 功能：支付宝服务器异步通知页面
 版本：3.3
 日期：2012-08-17
 说明：
 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。

 //***********页面功能说明***********
 创建该页面文件时，请留心该页面文件中无任何HTML代码及空格。
 该页面不能在本机电脑测试，请到服务器上做测试。请确保外部可以访问该页面。
 该页面调试工具请使用写文本函数logResult，该函数在com.alipay.util文件夹的AlipayNotify.java类文件中
 如果没有收到该页面返回的 success 信息，支付宝会在24小时内按一定的时间策略重发通知
 //********************************
 * */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.alipay.util.*"%>
<%@ page import="com.alipay.config.*"%>
<%@ page import="com.alipay.service.*"%>
<%
	//获取支付宝POST过来反馈信息
	Map<String,String> params = new HashMap<String,String>();
	if(ProxyNotify.verify(params)){//验证成功
		out.println("success");	//请不要修改或删除
	}else{//验证失败
		out.println("fail");
	}
%>
