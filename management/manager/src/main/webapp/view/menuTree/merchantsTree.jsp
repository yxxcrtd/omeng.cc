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
			<cms:havePerm url='/merchants/showStore'>
                     {
                         "id": "merchants_1",
                         "text": "服务商运营信息管理",
                         "description": "服务商运营信息管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Fuwushangyunyingxinxiguanli",
                         "url": "/merchants/showStore"
                     },</cms:havePerm>
                     <cms:havePerm url='/merchants'>
                     {
                         "id": "merchants_2",
                         "text": "服务商员工信息管理",
                         "description": "服务商员工信息管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Fuwushangyuangongxinxiguanli",
                         "url": "/merchants"
                     },</cms:havePerm>
                     <cms:havePerm url='/merchants/showStoreAudit'>
                     {
                         "id": "merchants_3",
                         "text": "服务商认证管理",
                         "description": "服务商认证管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Fuwushangxinxiguanli",
                         "url": "/merchants/showStoreAudit"
                     },</cms:havePerm>
                     <cms:havePerm url='/serviceTag/merchantServiceTagIndex'>
                     {
                         "id": "opercator_8",
                         "text": "服务商个性标签",
                         "description": "服务商个性标签",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fuwushangfuwubiaoqian",
                         "url": "/serviceTag/merchantServiceTagIndex"
                     },</cms:havePerm>
                      <cms:havePerm url='/merchants/MerchantsInfoListForFensiIndex'>
                     {
                         "id": "fensi",
                         "text": "服务商增加粉丝",
                         "description": "服务商增加粉丝",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fuwushangfuwubiaoqian",
                         "url": "/merchants/MerchantsInfoListForFensiIndex"
                     },</cms:havePerm>
                      <cms:havePerm url='/activity'>
                     {
                         "id": "fensiDayAdd",
                         "text": "粉丝日增长",
                         "description": "粉丝日增长",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fuwushangfuwubiaoqian",
                         "url": "/activity"
                     },</cms:havePerm>
                     <cms:havePerm url='/activity/fensiRankingindex'>
                     {
                         "id": "fensiDayAddRanking",
                         "text": "粉丝日排行",
                         "description": "粉丝日排行",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fuwushangfuwubiaoqian",
                         "url": "/activity/fensiRankingindex"
                     },</cms:havePerm>
                       <cms:havePerm url='/activity/merPhotoindex'>
                     {
                         "id": "merPhotoindex",
                         "text": "店铺相册上传统计",
                         "description": "店铺相册上传统计",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fuwushangfuwubiaoqian",
                         "url": "/activity/merPhotoindex"
                     },</cms:havePerm>
			]
}