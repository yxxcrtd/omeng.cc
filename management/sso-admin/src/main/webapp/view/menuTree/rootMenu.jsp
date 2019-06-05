<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/cms.tld" prefix="cms" %>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setHeader("Expires","0");

	request.setCharacterEncoding("UTF-8");		
%>
{
    "data": [
        <cms:havePerm url='/systemManager'>
        {
            "id": "root_system",
            "text": "系统管理",
            "children":"systemManagerTree",
            "description": "系统管理",
            "father_id": null,
            "leaf": 0,
            "type_id": "module",
            "iconCls": "Xitongguanli",
            "url": ""
        },</cms:havePerm>
    ]
}