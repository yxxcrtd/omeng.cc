<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setHeader("Expires","0");

	request.setCharacterEncoding("UTF-8");	
	
	//String webRoot = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>首页</title>
<script type="text/javascript">
function agentManager(){
	parent.addTab("agent_1","代理商信息管理","Dailishangtianjia","/agent/agentShow"); 
}
function userManager(){
	parent.addTab("user1","会员信息管理","Huiyuanxinxiguanli","/user"); 
}
function orderManager(){
	parent.addTab("order_1","订单信息管理","Dingdanxinxiguanli","/order"); 
}
function merchantManager(){
	parent.addTab("merchants_1","服务商运营信息管理","Fuwushangyunyingxinxiguanli","/merchants/showStore"); 
}
</script>

<style>
		    html,body,img,a,p,table,td,th ,ul{border:0;margin:0;padding:0;}
		    ul,li{list-style: none;}
			.boxPannel{width:100%;font-family: "微软雅黑";font-size:25px;color:#fff;line-height: 40px;}
			.boxPannel li{float:left;width:164px;height:119px;padding:40px 10px 10px 135px;margin:45px 0 0 43px; word-wrap: break-word;}
			.boxPannel li.li0{background:url(../view/image/huiyuanshuliang.png) no-repeat 43px center #f9ae53;}
			.boxPannel li.li1{background:url(../view/image/fuwushangshuliang.png) no-repeat 43px center #1bb672;}
			.boxPannel li.li2{background:url(../view/image/dailishangshuliang.png) no-repeat 43px center #79ccec;}
			.boxPannel li.li3{background:url(../view/image/dingdanshuliang.png) no-repeat 43px center #788af0;}
			.boxPannel li.li4{background:url(../view/image/shengdaishuliang.png) no-repeat 43px center #fe7352;}
			.boxPannel li.li5{background:url(../view/image/shidaishuliang.png) no-repeat 43px center #e872af;}
			.boxPannel li.li6{background:url(../view/image/xiangmudaishuliang.png) no-repeat 43px center #a6ce6b;}
</style>
	</head>
	<body>
		<div class="boxPannel">
			<ul>
			  <c:if test="${data.userType==1}">
			  <a href="javascript:;" onclick="userManager();" style="display:block;color:#fff;text-decoration:none;" >
			  <li class="li0">
			    <div class="TT_01">会员数量：</div>
			    <div class="num_">${ data.users}人</div>
			  </li>
			  </a>	
			  </c:if>

			  <a href="javascript:;" onclick="merchantManager();" style="display:block;color:#fff;text-decoration:none;" >
			  <li class="li1">
			    <div class="TT_01">服务商数量：</div>
			    <div class="num_">${ data.merchants}人</div>	
			  </li>
			  </a>
			  
			  <c:if test="${data.userType==1}">	
			  <a href="javascript:;" onclick="agentManager();" style="display:block;color:#fff;text-decoration:none;" >
			  <li class="li2">
			    <div class="TT_01">代理商数量：</div>
			    <div class="num_">${ data.agents}人</div>
			  </li>	
			  </a>
			  </c:if>
			  <c:if test="${data.userType==1}">
			  <a href="javascript:;" onclick="orderManager();" style="display:block;color:#fff;text-decoration:none;" >
			  <li class="li3">
			    <div class="TT_01">订单数量：</div>
			    <div class="num_">${ data.orders}条</div>
			  </li>	
			  </a>
			 </c:if>
			</ul>
			<ul>
			  <c:if test="${data.userType<2}">
			  <a href="javascript:;" onclick="agentManager();" style="display:block;color:#fff;text-decoration:none;" >
			  <li class="li4">
			    <div class="TT_01">省代数量：</div>
			    <div class="num_">${ data.provinceAgents}人</div>
			  </li>	
			  </a>
			  </c:if>
			  <c:if test="${data.userType<3}">
			  <a href="javascript:;" onclick="agentManager();" style="display:block;color:#fff;text-decoration:none;" >
			  <li class="li5">
			    <div class="TT_01">市代数量：</div>
			    <div class="num_">${ data.cityAgents}人</div>
			  </li>	
			  </a>
			  </c:if>
			  <c:if test="${data.userType!=4}">
			  <a href="javascript:;" onclick="agentManager();" style="display:block;color:#fff;text-decoration:none;" >
			  <li class="li6">
			    <div class="TT_01">项目代数量：</div>
			    <div class="num_">${ data.appAgents}人</div>
			  </li>	
			  </a>
			  </c:if>
			</ul>
		</div>

	</body>
</html>