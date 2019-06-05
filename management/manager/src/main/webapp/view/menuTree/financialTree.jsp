<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/cms.tld" prefix="cms" %>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setHeader("Expires","0");

	request.setCharacterEncoding("UTF-8");		
%>
{
			"data":[
			 
                     // <cms:havePerm url='/finacial/agentConsume'>
                    // {
                     //    "id": "finacial_2",
                     //    "text": "代理商消费管理",
                     //    "description": "代理商消费管理",
                     //    "father_id": "1",
                     //    "leaf": 1,
                     //    "type_id": "MENU",
                     //    "iconCls": "Dailishangxiaofeiguanli",
                     //    "url": "http://www.baidu.com"
                    // },</cms:havePerm>
                     <cms:havePerm url='/merchants/showWithDraw'>
                     {
                         "id": "finacial_3",
                         "text": "服务商提现管理",
                         "description": "代理商提现管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Fuwushangtixianguanli",
                         "url": "/merchants/showWithDraw"
                     }</cms:havePerm>
			]
}