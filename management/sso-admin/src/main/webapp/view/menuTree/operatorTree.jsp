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
			         <cms:havePerm url='/opercator/voucher'>
                     {
                         "id": "opercator_1",
                         "text": "代金券管理",
                         "description": "代金券管理",
                         "father_id": "1",
                         "leaf": 0,
                         "type_id": "MENU",
                         "iconCls": "Daijinquan",
                         "url": "",
                         "data":[
                         <cms:havePerm url='/vouchers/showVouchers'>
                         {
                          "id": "opercator_2",
                          "text": "代金券添加管理",
                          "description": "代金券添加管理",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "Daijinquantianjiajilu",
                          "url": "/vouchers/showVouchers"
                      },</cms:havePerm>
                       <cms:havePerm url='/vouchers/showVouchersUse'>
                      {
                          "id": "opercator_3",
                          "text": "代金券使用记录",
                          "description": "代金券使用记录",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "Daijinquanshiyongjilu",
                          "url": "/vouchers/showVouchersUse"
                      },</cms:havePerm>
                        <cms:havePerm url='/vouchers/showVouchersIssue'>
                      {
                          "id": "opercator_4",
                          "text": "代金券发放管理",
                          "description": "代金券发放管理",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "Fafangdaijinquanjilu",
                          "url": "/vouchers/showVouchersIssue"
                      }</cms:havePerm>
                      ]
                     },</cms:havePerm>
                    <cms:havePerm url='/valueAdded'>
                     {
                         "id": "valueAdded",
                         "text": "增值服务管理",
                         "description": "增值服务管理",
                         "father_id": "1",
                         "leaf": 0,
                         "type_id": "MENU",
                         "iconCls": "Zengzhifuwuguanli",
                         "url": "",
                         "data":[
                          <cms:havePerm url='/valueAdded/vipMemberIndex'>
                         {
                          "id": "valueAdded_1",
                          "text": "VIP会员申请",
                          "description": "VIP会员申请",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "VIPhuiyuanshenqing",
                          "url": "/valueAdded/vipMemberIndex"
                         },</cms:havePerm>
                        <cms:havePerm url='/valueAdded/orderPushApplyIndex'>
                        {
                          "id": "valueAdded_2",
                          "text": "订单推送申请",
                          "description": "订单推送申请",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "Dingdantuisongshenqing",
                          "url": "/valueAdded/orderPushApplyIndex"
                        },</cms:havePerm>
                       <cms:havePerm url='/valueAdded/adviserApplyIndex'>
                        {
                          "id": "valueAdded_3",
                          "text": "顾问号申请",
                          "description": "顾问号申请",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "Guwenhaoshenqing",
                          "url": "/valueAdded/adviserApplyIndex"
                       },</cms:havePerm>
                        <cms:havePerm url='/valueAdded/grabFeeShow'>
                        {
                          "id": "grabFee",
                          "text": "抢单费用标准管理",
                          "description": "抢单费用标准管理",
                          "father_id": "1",
                          "leaf": 1,
                          "type_id": "MENU",
                          "iconCls": "Qiangdanfeiyongguanli",
                          "url": "/valueAdded/grabFeeShow"
                       }</cms:havePerm>
                      ]
                     },</cms:havePerm>
                      <cms:havePerm url='/slider/activityIndex'>
                     {
                         "id": "activity_1",
                         "text": "活动管理",
                         "description": "活动管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Guanggaoguanli",
                         "url": "/slider/activityIndex"
                        
                     },</cms:havePerm>
                     <cms:havePerm url='/activity/orderRewardAccountbyAgent'>
                     {
                         "id": "activity_account",
                         "text": "奖励活动账单",
                         "description": "奖励活动账单",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Guanggaoguanli",
                         "url": "/activity/orderRewardAccountbyAgent"
                        
                     },</cms:havePerm>
                       <cms:havePerm url='/slider'>
                     {
                         "id": "opercator_5",
                         "text": "广告管理",
                         "description": "广告管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Guanggaoguanli",
                         "url": "/slider"
                        
                     },</cms:havePerm>
                     <cms:havePerm url='/slider/recommendIndex'>
                     {
                         "id": "opercator_10",
                         "text": "推荐位管理",
                         "description": "推荐位管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "tuijianweiguanli",
                         "url": "/slider/recommendIndex"
                        
                     },</cms:havePerm>
                     <cms:havePerm url='/slider/searchStatisticIndex'>
                     {
                         "id": "opercator_11",
                         "text": "热门搜索管理",
                         "description": "热门搜索管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "remensousuoguanli",
                         "url": "/slider/searchStatisticIndex"
                        
                     },</cms:havePerm>
                      <cms:havePerm url='/slider/searchStatisticAttahIndex'>
                     {
                         "id": "opercator_12",
                         "text": "行业图标管理",
                         "description": "行业图标管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "hangyetubiaoguanli",
                         "url": "/slider/searchStatisticAttahIndex"
                        
                     },</cms:havePerm>
                      <cms:havePerm url='/slider/thirdAppIndex'>
                     {
                         "id": "opercator_13",
                         "text": "第三方APP管理",
                         "description": "第三方APP管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "disanfangAPPguanli",
                         "url": "/slider/thirdAppIndex"
                        
                     },</cms:havePerm>
                      <cms:havePerm url='/slider/treeServiceIndex'>
                       {
                         "id": "tree_13",
                         "text": "分类管理",
                         "description": "分类管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fenleiguanli",
                         "url": "/slider/treeServiceIndex"
                        
                     },</cms:havePerm>
                     <cms:havePerm url='/slider/treeOrderServiceIndex'>
                       {
                         "id": "tree_23",
                         "text": "表单分类管理",
                         "description": "表单分类管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fenleiguanli",
                         "url": "/slider/treeOrderServiceIndex"
                        
                     },</cms:havePerm>
                      <cms:havePerm url='/serviceTag/serviceTagIndex'>
                     {
                         "id": "systemManager11",
                         "text": "热门搜索标签",
                         "description": "热门搜索标签",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fuwubiaoqian",
                         "url": "/serviceTag/serviceTagIndex"
                     },</cms:havePerm>
                      <cms:havePerm url='/slider/shareActivityIndex'>
                       {
                         "id": "activity",
                         "text": "分享活动管理",
                         "description": "分享活动管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fenleiguanli",
                         "url": "/slider/shareActivityIndex"
                        
                     },</cms:havePerm>
                     <cms:havePerm url='/slider/staticActivityIndex'>
                       {
                         "id": "activity",
                         "text": "静态活动管理",
                         "description": "静态活动管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fenleiguanli",
                         "url": "/slider/staticActivityIndex"
                        
                     },</cms:havePerm>
                      <cms:havePerm url='/slider/recommendServiceIndex'>
                       {
                         "id": "recommendServiceIndex",
                         "text": "推荐服务管理",
                         "description": "推荐服务管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fenleiguanli",
                         "url": "/slider/recommendServiceIndex"
                        
                     },</cms:havePerm>
                      <cms:havePerm url='/user/userFeedBack'>
                     {
                         "id": "opercator_6",
                         "text": "用户反馈管理",
                         "description": "用户反馈管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "Yonghufankuiguanli",
                         "url": "/user/userFeedBack"
                        
                     },</cms:havePerm>
                     <cms:havePerm url='/message/messageIndex'>
                     {
                         "id": "opercator_7",
                         "text": "消息中心",
                         "description": "消息中心",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "xiaoxizhongxin",
                         "url": "/message/messageIndex"
                     },</cms:havePerm>
                     <cms:havePerm url='/operate/serviceCityIndex'>
                     {
                         "id": "opercator_9",
                         "text": "服务城市管理",
                         "description": "服务城市管理",
                         "father_id": "1",
                         "leaf": 1,
                         "type_id": "MENU",
                         "iconCls": "fuwuchengshiguanli",
                         "url": "/operate/serviceCityIndex"
                     }</cms:havePerm>
			]
}