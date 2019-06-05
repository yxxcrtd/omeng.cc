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
			        <cms:havePerm url='/agent/agentShow'>
                     {
                         "id": "agent_1",
                         "text": "代理商信息管理",
                         "description": "代理商信息管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "dailishangxinxiguanli",
                         "url": "/agent/agentShow"
                     },
                    </cms:havePerm>
                    <cms:havePerm url='/agent'>
                     {
                         "id": "agent_2",
                         "text": "代理商消费记录管理",
                         "description": "代理商消费记录管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Dailishangchongzhi",
                         "url": "/agent"
                     },</cms:havePerm> 
                     <cms:havePerm url='/agent/agentEmpolyeeShow'>
                     {
                         "id": "agent_3",
                         "text": "代理商员工管理",
                         "description": "代理商员工管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "dailishangyuangong",
                         "url": "/agent/agentEmpolyeeShow"
                     },</cms:havePerm> 
                    <%--  <cms:havePerm url='/view/welcome/nopermission.html'>
                     {
                         "id": "agent_4",
                         "text": "代理商员工管理",
                         "description": "代理商员工管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "dailishangyuangong",
                         "url": "/view/welcome/repeatLanding.html"
                     },</cms:havePerm>  --%>
			]
}