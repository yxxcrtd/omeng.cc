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
			        <cms:havePerm url='/statistic/userTerminal'>
                     {
                         "id": "user_terminal",
                         "text": "终端统计",
                         "description": "终端统计",
                         "father_id": "1",
                         "leaf": 0,
                         "type_id": "MENU",
                         "iconCls": "shanghuzhongduantongji",
                         "url": "",
                         "data":[
                         <cms:havePerm url='/userTerminal/userVersionIndex'>
                         {
                          "id": "userVersionIndex",
                          "text": "版本统计",
                          "description": "版本统计",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "banbentongji",
                          "url": "/userTerminal/userVersionIndex"
                      },</cms:havePerm>
                      <cms:havePerm url='/userTerminal/userDeviceIndex'>
                      {
                          "id": "userDeviceIndex",
                          "text": "设备统计",
                          "description": "设备统计",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "shebeitongji",
                          "url": "/userTerminal/userDeviceIndex"
                      },</cms:havePerm>
                      <cms:havePerm url='/userTerminal/userStartUpIndex'>
                      {
                          "id": "userStartUpIndex",
                          "text": "启动次数",
                          "description": "启动次数",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "qidongcishu",
                          "url": "/userTerminal/userStartUpIndex"
                      },</cms:havePerm>
                       <cms:havePerm url='/userTerminal/userChannelIndex'>
                      {
                          "id": "userChannelIndex",
                          "text": "渠道统计",
                          "description": "渠道统计",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "qudaotongji",
                          "url": "/userTerminal/userChannelIndex"
                      },</cms:havePerm>
                       <cms:havePerm url='/userTerminal/userAreaIndex'>
                      {
                          "id": "userAreaIndex",
                          "text": "地域统计",
                          "description": "地域统计",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "diyutongji",
                          "url": "/userTerminal/userAreaIndex"
                      }</cms:havePerm>
                      ]
                     },</cms:havePerm>
                     <cms:havePerm url='/statistic/merchant'>
                     {
                         "id": "merchant_login_statistic",
                         "text": "商户统计",
                         "description": "商户入驻统计",
                         "father_id": "1",
                         "leaf": 0,
                         "type_id": "MENU",
                         "iconCls": "shanghuruzhutongji",
                         "url": "",
                         "data":[
                         <cms:havePerm url='/statistic/loginMerchantShow'>
                         {
                          "id": "loginMerchantShow",
                          "text": "商户入驻统计",
                          "description": "入驻统计",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "ruzhutongji",
                          "url": "/statistic/loginMerchantShow"
                      },</cms:havePerm>
                      <cms:havePerm url='/statistic/loginMerchantTrend'>
                      {
                          "id": "loginMerchantTrend",
                          "text": "商户入驻趋势",
                          "description": "入驻趋势",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "ruzhuqushi",
                          "url": "/statistic/loginMerchantTrend"
                      }</cms:havePerm>
                      ]
                     },</cms:havePerm>
                     <cms:havePerm url='/statistic/user'>
                     {
                         "id": "user_login_statistic",
                         "text": "用户统计",
                         "description": "用户注册统计",
                         "father_id": "1",
                         "leaf": 0,
                         "type_id": "MENU",
                         "iconCls": "yonghutongji",
                         "url": "",
                         "data":[
                         <cms:havePerm url='/statistic/loginUserShow'>
                         {
                          "id": "loginUserShow",
                          "text": "用户注册统计",
                          "description": "入驻统计",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "yonghuzhucetongji",
                          "url": "/statistic/loginUserShow"
                      },</cms:havePerm>
                      <cms:havePerm url='/statistic/loginUserTrend'>
                      {
                          "id": "loginUserTrend",
                          "text": "用户注册趋势",
                          "description": "入驻趋势",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "yonghuzhucequshi",
                          "url": "/statistic/loginUserTrend"
                     }</cms:havePerm>
                      ]
                     },</cms:havePerm>
                     <cms:havePerm url='/statistic/order'>
                     {
                         "id": "user_order_statistic",
                         "text": "订单统计",
                         "description": "用户订单统计",
                         "father_id": "1",
                         "leaf": 0,
                         "type_id": "MENU",
                         "iconCls": "dingdantongji",
                         "url": "",
                         "data":[
                         <cms:havePerm url='/statistic/userOrderShow'>
                         {
                          "id": "UserOrderShow",
                          "text": "新增订单统计",
                          "description": "新增订单统计",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "xinzengdingdantongji",
                          "url": "/statistic/userOrderShow"
                      },</cms:havePerm>
                       <cms:havePerm url='/statistic/userOrderTrend'>
                      {
                          "id": "UserOrderTrend",
                          "text": "新增订单趋势",
                          "description": "新增订单趋势",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "yonghuzhucequshi",
                          "url": "/statistic/userOrderTrend"
                      }</cms:havePerm>
                      ]
                     },</cms:havePerm>
                      <cms:havePerm url='/analysis'>
                    {
                         "id": "analysis",
                         "text": "业务分析",
                         "description": "业务分析",
                         "father_id": "1",
                         "leaf": 0,
                         "type_id": "MENU",
                         "iconCls": "shanghuzhongduantongji",
                         "url": "",
                         "data":[
                         <cms:havePerm url='/analysis/orderProcerShow'>
                         {
                          "id": "orderProcer",
                          "text": "订单过程统计",
                          "description": "订单过程统计",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "banbentongji",
                          "url": "/analysis/orderProcerShow"
                      },</cms:havePerm>
                      <cms:havePerm url='/analysis/agentInstallShow'>
                         {
                          "id": "agentInstall",
                          "text": "行业安装统计",
                          "description": "行业安装统计",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "banbentongji",
                          "url": "/analysis/agentInstallShow"
                      },</cms:havePerm>
                      <cms:havePerm url='/analysis/merchantOrderShow'>
                      {
                          "id": "merchantOrder",
                          "text": "服务商业务统计",
                          "description": "服务商业务统计信息",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "banbentongji",
                          "url": "/analysis/merchantOrderShow"
                      },</cms:havePerm>
                      ]
                     }</cms:havePerm>
			]
}