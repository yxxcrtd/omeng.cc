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
                     {
                         "id": "appVersion",
                         "text": "版本管理",
                         "description": "版本管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Huiyuanxinxiguanli",
                         "url": "/terminal/index"
                     }
                 
			]
}