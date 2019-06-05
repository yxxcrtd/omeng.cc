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
        },
        </cms:havePerm>
        <cms:havePerm url='/report'>
            {
            "id": "root_report",
            "text": "报表管理",
            "children":"reportTree",
            "description": "报表管理",
            "father_id": null,
            "leaf": 0,
            "type_id": "module",
            "iconCls": "Caiwuguanli",
            "url": ""
            },
        </cms:havePerm>
     
    ]
}