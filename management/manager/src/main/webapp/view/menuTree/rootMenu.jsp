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
        <cms:havePerm url='/agentshow'>
        {
            "id": "root_agent",
            "text": "代理商管理",
            "children":"agentManagerTree",
            "description": "代理商管理",
            "father_id": null,
            "leaf": 0,
            "type_id": "module",
            "iconCls": "Dailishangguanli",
            "url": ""
        },</cms:havePerm>
        <cms:havePerm url='/merchants'>
        {
            "id": "root_merchants",
            "text": "服务商管理",
            "children":"merchantsTree",
            "description": "服务商管理",
            "father_id": null,
            "leaf": 0,
            "type_id": "module",
            "iconCls": "Fuwushangguanli",
            "url": ""
        },</cms:havePerm>
        <cms:havePerm url='/operator'>
           {
            "id": "root_operator",
            "text": "运营管理",
            "children":"operatorTree",
            "description": "运营管理",
            "father_id": null,
            "leaf": 0,
            "type_id": "module",
            "iconCls": "Yunyingguanli",
            "url": ""
        },</cms:havePerm>
        <cms:havePerm url='/financial'>
        {
            "id": "root_financial",
            "text": "财务管理",
            "children":"financialTree",
            "description": "财务管理",
            "father_id": null,
            "leaf": 0,
            "type_id": "module",
            "iconCls": "Caiwuguanli",
            "url": ""
        },</cms:havePerm>
        <cms:havePerm url='/user'>
        {
            "id": "root_user",
            "text": "会员管理",
            "children":"userTree",
            "description": "会员管理",
            "father_id": null,
            "leaf": 0,
            "type_id": "module",
            "iconCls": "Huiyuanguanli",
            "url": ""
        },</cms:havePerm>
        <cms:havePerm url='/order'>
        {
            "id": "root_order",
            "text": "订单管理",
            "children":"orderTree",
            "description": "订单管理",
            "father_id": null,
            "leaf": 0,
            "type_id": "module",
            "iconCls": "Dingdanguanli",
            "url": ""
        },</cms:havePerm>
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
       <cms:havePerm url='/statistic'>
        {
            "id": "root_statistic",
            "text": "统计分析",
            "children":"statisticTree",
            "description": "统计分析",
            "father_id": null,
            "leaf": 0,
            "type_id": "module",
            "iconCls": "tongjixinxi",
            "url": ""
        },</cms:havePerm>
          <cms:havePerm url='/edit'>
        {
            "id": "root_edit",
            "text": "编辑器管理",
            "children":"editHtmlTree",
            "description": "编辑器管理",
            "father_id": null,
            "leaf": 0,
            "type_id": "module",
            "iconCls": "tongjixinxi",
            "url": ""
        },</cms:havePerm> 
    ]
}