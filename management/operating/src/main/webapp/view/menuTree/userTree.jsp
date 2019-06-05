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
			        <cms:havePerm url='/user'>
                     {
                         "id": "user1",
                         "text": "会员信息管理",
                         "description": "会员信息管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Huiyuanxinxiguanli",
                         "url": "/user"
                     },</cms:havePerm>
                     <cms:havePerm url='/user/showRecycle'>
                     {
                         "id": "user2",
                         "text": "会员回收站",
                         "description": "会员回收站",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Huiyuanhuoshouzhan",
                         "url": "/user/showRecycle"
                     },</cms:havePerm>
                     <cms:havePerm url='/user/showblackUser'>
                     {
                         "id": "user3",
                         "text": "黑名单管理",
                         "description": "黑名单管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Huiyuanhuoshouzhan",
                         "url": "/user/showblackUser"
                     }</cms:havePerm>
			]
}