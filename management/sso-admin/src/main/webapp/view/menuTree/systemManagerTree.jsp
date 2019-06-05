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
                    <cms:havePerm url='/systemManager/index'>
                     {
                         "id": "systemManager1",
                         "text": "账号管理",
                         "description": "账号管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Zhanghaoguanli",
                         "url": "/systemManager/index"
                     },
                     </cms:havePerm>
                     
                     <cms:havePerm url='/systemManager/groupIndex'>
					 {
                         "id": "systemManager2",
                         "text": "群组管理",
                         "description": "群组管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Qunzuguanli",
                         "url": "/systemManager/groupIndex"
                     },
                     </cms:havePerm>
                    <cms:havePerm url='/systemManager/roleIndex'>
					 {
                         "id": "systemManager3",
                         "text": "角色管理",
                         "description": "角色管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Jueseguanli",
                         "url": "/systemManager/roleIndex"
                     },
                     </cms:havePerm>
                     <cms:havePerm url='/systemManager/resourceIndex'>
                     {
                         "id": "systemManager4",
                         "text": "资源管理",
                         "description": "资源管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Ziyuanguanli",
                         "url": "/systemManager/resourceIndex"
                     },
                     </cms:havePerm>
                     <cms:havePerm url='/systemManager/operateLogIndex'>
                     {
                         "id": "systemManager6",
                         "text": "操作日志",
                         "description": "操作日志",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Caozuorizhi",
                         "url": "/systemManager/operateLogIndex"
                     }</cms:havePerm>

			]
}