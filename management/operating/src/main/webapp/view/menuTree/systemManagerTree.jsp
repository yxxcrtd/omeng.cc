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
              <cms:havePerm url='/powerManager'>
                     {
                         "id": "power",
                         "text": "权限管理",
                         "description": "权限管理",
                         "father_id": "1",
                         "leaf": 0,
                         "type_id": "MENU",
                         "iconCls": "Quanxianguanli",
                         "url": "",
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
                     }
                     </cms:havePerm>
                     ]
                    },</cms:havePerm>
                    <cms:havePerm url='/systemParams'>
                    {
                         "id": "systemParam",
                         "text": "系统参数",
                         "description": "系统参数",
                         "father_id": "1",
                         "leaf": 0,
                         "type_id": "MENU",
                         "iconCls": "Xitongcanshu",
                         "url": "",
                         "data":[ 
                     <cms:havePerm url='/systemManager/appIndex'>
                     {
                         "id": "systemManager5",
                         "text": "项目管理",
                         "description": "项目管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Xiangmuguanli",
                         "url": "/systemManager/appIndex"
                     },</cms:havePerm>
                      <cms:havePerm url='/systemManager/systemParamIndex'>
                     {
                         "id": "systemManager7",
                         "text": "字典管理",
                         "description": "字典管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Zidianguanli",
                         "url": "/systemManager/systemParamIndex"
                     }, </cms:havePerm>
                     <cms:havePerm url='/systemManager/valueLabelIndex'>
                     {
                         "id": "valueLabel",
                         "text": "价值标签管理",
                         "description": "价值标签管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "jiazhibiaoqianguanli",
                         "url": "/systemManager/valueLabelIndex"
                     }, </cms:havePerm>
                     <cms:havePerm url='/systemManager/configurationIndex'>
                     {
                         "id": "systemManager20",
                         "text": "配置项管理",
                         "description": "配置项管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "peizhixiangguanli",
                         "url": "/systemManager/configurationIndex"
                     }, </cms:havePerm>
                     <cms:havePerm url='/systemManager/showRestrictUpdate'>
                     {
                         "id": "RestrictUpdate20",
                         "text": "重复提交配置",
                         "description": "重复提交配置",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "peizhixiangguanli",
                         "url": "/systemManager/showRestrictUpdate"
                     }, </cms:havePerm>
                     <cms:havePerm url='/systemManager/serviceTypeIndex'>
                     {
                         "id": "systemManager8",
                         "text": "服务类型",
                         "description": "服务类型",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Fuwuleixing",
                         "url": "/systemManager/serviceTypeIndex"
                     },</cms:havePerm>
                     <cms:havePerm url='/systemManager/searchWordsIndex'>
                     {
                         "id": "systemManager9",
                         "text": "搜索分词",
                         "description": "搜索分词",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "shousufenci",
                         "url": "/systemManager/searchWordsIndex"
                     },</cms:havePerm>
                     <cms:havePerm url='/systemManager/stopKeywordsIndex'>
                     {
                         "id": "yincangguanjianci",
                         "text": "隐藏关键词",
                         "description": "隐藏关键词",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "yincangguanjianci",
                         "url": "/systemManager/stopKeywordsIndex"
                     },</cms:havePerm>
                      <cms:havePerm url='/systemManager/customKeywordsIndex'>
                     {
                         "id": "buchaifenguanjianci",
                         "text": "不拆分关键词",
                         "description": "不拆分关键词",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "buchaifenguanjianci",
                         "url": "/systemManager/customKeywordsIndex"
                     },</cms:havePerm>
                     <cms:havePerm url='/systemManager/showUserWord'>
                     {
                         "id": "userWord",
                         "text": "用户关键词",
                         "description": "用户关键词",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "yonghuguanjianci",
                         "url": "/systemManager/showUserWord"
                     },</cms:havePerm>
                     <cms:havePerm url='/systemManager/showServiceWord'>
                     {
                         "id": "serviceWord",
                         "text": "服务关键词",
                         "description": "服务关键词",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fuwuguanjianci",
                         "url": "/systemManager/showServiceWord"
                     },</cms:havePerm>
                     <cms:havePerm url='/systemManager/showServiceWord'>
                     {
                         "id": "systemManager10",
                         "text": "推送参数",
                         "description": "推送参数",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "tuisongcanshu",
                         "url": "/systemManager/pushIndex"
                     },</cms:havePerm>
                      <cms:havePerm url='/systemManager/pushConfigIndex'>
                     {
                         "id": "pushConfigIndex",
                         "text": "推送关联服务",
                         "description": "推送关联服务",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "tuisongcanshu",
                         "url": "/systemManager/pushConfigIndex"
                     },</cms:havePerm>
                    
                     <cms:havePerm url='/serviceTag/personalTagIndex'>
                     {
                         "id": "personltag",
                         "text": "个性化标签",
                         "description": "个性化标签",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fuwubiaoqian",
                         "url": "/serviceTag/personalTagIndex"
                     },</cms:havePerm>
                     <cms:havePerm url='/systemManager/appKeywordsIndex'>
                     {
                         "id": "systemManager12",
                         "text": "应用关键词",
                         "description": "应用关键词",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "yingyongguanjianci",
                         "url": "/systemManager/appKeywordsIndex"
                     }</cms:havePerm>
                         ]
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
                     },</cms:havePerm>
                     <cms:havePerm url='/terminal/index'>
                     {
                         "id": "appVersion",
                         "text": "版本管理",
                         "description": "版本管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Banbenguanli",
                         "url": "/terminal/index"
                     },
                     </cms:havePerm>
                     <cms:havePerm url='/terminal/loadingIndex'>
                     {
                         "id": "loading",
                         "text": "启动页管理",
                         "description": "启动页管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "qidongyeguanli",
                         "url": "/loading/loadingIndex"
                     },
                     </cms:havePerm>

                    <cms:havePerm url='/maintain'>
                        {
                        "id": "maintain",
                        "text": "系统维护页面",
                        "description": "系统维护页面",
                        "father_id": "1",
                        "leaf": 1,
                        "type_id": "MENU",
                        "iconCls": "qidongyeguanli",
                        "url": "/maintain"
                        }
                    </cms:havePerm>

			]
}