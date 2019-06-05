<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>订单状态详情</title>
<!-- CSS goes in the document HEAD or added to your external stylesheet -->
<style type="text/css">
table.gridtable {
    table-layout:fixed;
	font-family: verdana,arial,sans-serif;
	font-size:14px;
	text-indent:20px; 
	color:#333333;
	border-width: 1px;
	border-color: #ededed;
	border-collapse: collapse;
}
table.gridtable th {
    height: 35px; 
	text-overflow: ellipsis;
	border-width: 1px;
	padding: 8px;
	border:0px;
	background-color: #f5f5f5;
}
table.gridtable tr{
    border:1px solid #ededed;
}
table.gridtable td {
    height: 35px; 
	text-overflow: ellipsis;
	border-width: 1px;
	padding: 8px;
	border:0px;
	background-color: #ffffff;
}
ul{list-style:none;}
ul li{ display:inline;margin-left:50px;} 
.pic_list{}
.pic_list li{ width:144px; margin:10px; float:left; text-align:center; line-height:20px; color:#666;background-color: #ffffff; }



td div{height:23px;}

#layerBox{background:url(../view/image/layerBj_.png) repeat left top;width:100%;height:100%;position: fixed;left:0;top:0;display: none;}
#layerBox ul{margin:0;padding:0;}
#layerBox .cont{width:800px;height:500px;background:#fff;position:absolute;left:50%;top:50%;margin:-250px 0 0 -400px; z-index: 4;}
#layerBox .cont .imgField{padding:10px;height:400px;position:relative;}
#layerBox .cont .control{position: absolute;top:200px;color:#000;font-size:40px;display:block;font-family: "宋体";font-weight: bold;cursor: pointer;color: #999;}
#layerBox .cont .control.on{color:#e94718;}
#layerBox .cont .prev{left:30px;}
#layerBox .cont .next{right:30px;}
#layerBox .cont .imgField img{width:780px;height:400px;}
#layerBox .cont .imgListField{width:100%;height:80px;position:relative;}
#layerBox .cont .imgListField .scrollImg{width:700px;height:80px;overflow: hidden;position:relative;left:45px;}
#layerBox .cont .imgListField .scrollImg ul{position:absolute;width:3000px;left:0;}
#layerBox .cont .imgListField li{float:left;width:130px;margin:0 0 0 10px;}
#layerBox .cont .imgListField img{width:130px;height:66px;}
#layerBox .cont .imgListField .control{top:14px;}
#layerBox .cont .imgListField .prev{left:15px;}
#layerBox .cont .imgListField .next{right:15px;}
#layerBox .close{width: 42px;height:42px;cursor:pointer;position: absolute;right: 15px;top:15px; z-index: 5;}

#layer{background:url(../view/image/layerBj_.png) repeat left top;width:100%;height:100%;position: fixed;left:0;top:0;display: none;}
#layer .close{width: 20px;height:20px;cursor:pointer;position: absolute;right:5px;top:5px; z-index: 5;}
#layer .cont{width:502px;height:300px;background:#fff;position:absolute;left:50%;top:50%;margin:-150px 0 0 -251px; z-index: 4;text-align: center;overflow:hidden;}
#layer .cont img{width:auto;height:100%;}

#plan_layer{background:url(../view/image/layerBj_.png) repeat left top;width:100%;height:100%;position: fixed;left:0;top:0;display: none;}
#order_layer{background:url(../view/image/layerBj_.png) repeat left top;width:100%;height:100%;position: fixed;left:0;top:0;display: none;}
#plan_layer .close{width: 20px;height:20px;cursor:pointer;position: absolute;right:5px;top:5px; z-index: 5;}
#plan_layer .cont{width:502px;height:300px;background:#fff;position:absolute;left:50%;top:50%;margin:-150px 0 0 -251px; z-index: 4;text-align: center;overflow:hidden;}
#plan_layer .cont img{width:auto;height:100%;}

#order_pay{background:url(../view/image/layerBj_.png) repeat left top;width:100%;height:100%;position: fixed;left:0;top:0;display: none;}
#order_pay .close{width: 20px;height:20px;cursor:pointer;position: absolute;right:5px;top:5px; z-index: 5;}
#order_pay .cont{width:502px;height:300px;background:#fff;position:absolute;left:50%;top:50%;margin:-150px 0 0 -251px; z-index: 4;text-align: center;overflow:hidden;}
#order_pay .cont img{width:auto;height:100%;}

#order_layer .close{width: 20px;height:20px;cursor:pointer;position: absolute;right:5px;top:5px; z-index: 5;}
#order_layer .cont{width:380px;height:300px;background:#fff;position:absolute;left:50%;top:50%;
margin:-150px 0 0 -200px; z-index: 4;text-align: center;overflow:hidden;}
#order_layer .cont img{width:auto;height:100%;}

.ul{list-style: none;padding-left:0;}
#order_layer .list_1{padding-top:10px;}
#order_layer .ul li{ line-height:30px;margin-left:0;display:block;text-align:left;padding:0 10px;}
</style>
<script type="text/javascript" src="/view/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript">

function closeEvaluation(){
	 $('#layer').hide();
}
function showEvaluation(){
	 $('#layer').show();
}

function showPlan(pid){
	 $('.plan').hide();
	 $('#'+pid).show();
	 $('#order_pay').show();
	 $('#plan_layer').show();
	 $('#order_pay .orderPay').hide();
}
function closePlan(){
	 $('#plan_layer').hide();
	 $('#order_pay').hide();
	 $('#plan_layer').hide();
	 $('#order_pay .orderPay').hide();
}

function showDetail(){
	 $('#order_pay').show();
	 $('#order_pay .orderPay').hide();
	 $('#plan_layer').hide();
	 $('#order_layer').show();
}
function closeOrder(){
	 $('#order_layer').hide();
	 $('#order_pay').hide();
	 $('#order_pay .orderPay').hide();
	 $('#plan_layer').hide();
	 $('#order_layer').hide();
}

function showOrderPay(){
	 $('#order_pay').show();
	 $('#order_pay .orderPay').show();
}

function closeOrderpay(){
	 $('#order_pay').hide();
	 $('#order_pay .orderPay').hide();
	 
}

function merchantDetail(name){
	parent.addTab("merchants_1","服务商运营信息管理","Fuwushangyunyingxinxiguanli","/merchants/showStore?name="+name); 
}
function userDetail(phone){
	parent.addTab("user1","会员信息管理","Huiyuanxinxiguanli","/user?phone="+phone); 
}
</script>
</head>

<body>
<div style="margin-top:-8px;margin-left:-8px;">
<table frame=above style="word-wrap:break-word;" class="gridtable"  width="100%"  height="100%" >
<tr align="left">
<th style="width:20%">状态</th>
<th style="width:40%">时间</th>
<th>描述</th>
</tr>
<tr>
<td>用户发单</td>
<td>
<fmt:formatDate value="${data.orderBasic.join_time}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
<td><span style="color:blue;"><a href="javascript:;" onclick="userDetail( '${data.orderBasic.phone}');">${data.orderBasic.phone}</a></span> 发布一条新订单,近7日推送${data.pushCount}人<span style="color:blue;"><a href="javascript:;" onclick="showDetail();" style="display:inline-block;text-decoration:none;" >订单详情></a></span></td>
</tr>

<c:choose>
    <c:when test="${data.orderBasic.order_status>=2&&data.orderBasic.order_status<=6&&data.orderPlan!=null}">
   
         <tr>
         <td>商户抢单</td>
         <td>
               <c:forEach items="${data.orderPlan}" var="item">
               <div><fmt:formatDate value="${item.join_time}" pattern="yyyy-MM-dd HH:mm:ss" /></div>
               </c:forEach>
         </td>
         <td>
               <c:forEach items="${data.orderPlan}" var="item">
               <div>服务商 <span style="color:blue;"><a href="javascript:;" onclick="merchantDetail( '${item.name}');">${item.name}</a></span> 提供 <span style="color:blue;"><a href="javascript:;" onclick="showPlan(${item.id});" style="display:inline-block;text-decoration:none;" >报价方案></a></span></div>
               </c:forEach>    
         </td>
         </tr>

    </c:when>
    
        <c:when test="${data.orderBasic.order_status==7}">
         <tr>
         <td>取消订单</td>
         <td></td>
         <td>订单已取消</td>
         </tr>
    </c:when>
    <c:otherwise>

    </c:otherwise>
</c:choose>
<c:choose>
<c:when test="${data.orderBasic.order_status==6}">
         <tr>
         <td>订单过期</td>
         <td></td>
         <td>订单已过期</td>
         </tr>
    </c:when>
</c:choose>
<c:if test="${data.confirmMerchant!=null}">
<tr>
<td>确认服务商</td>
<td><fmt:formatDate value="${data.orderBasic.confirmTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
<td>用户已选择 <span style="color:blue;"><a href="javascript:;" onclick="merchantDetail( '${data.confirmMerchant.name}');">${data.confirmMerchant.name}</a></span> 提供服务</td>
</tr>
</c:if>
 
<c:if test="${data.orderBasic.finshService_time!=null}">
<tr>
<td>服务完成</td>
<td><fmt:formatDate value="${data.orderBasic.finshService_time}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
<td>商户 <span style="color:blue;">${data.confirmMerchant.name}</span>已交付</td>
</tr>
</c:if>
 
<c:if test="${data.orderBasic.order_status==5}">
<tr>
<td>支付完成</td>
<td><fmt:formatDate value="${data.orderBasic.deal_time}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
<td>用户已支付 <span style="color:blue;">${data.confirmMerchant.name}</span><span style="color:blue;"><a href="javascript:;" onclick="showOrderPay();" style="display:inline-block;text-decoration:none;" >支付详情></a></span></td>
</tr>
<c:if test="${data.evaluation!=null}">
         <tr>
         <td>用户评价</td>
         <td><fmt:formatDate value="${data.evaluation.join_time}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
         <td>用户已对 <span style="color:blue;display:inline-block;">${data.confirmMerchant.name}</span> 进行评价 <a href="javascript:;" onclick="showEvaluation();" style="display:inline-block;text-decoration:none;" >点击查看></a>
  
 </td>
   </tr>
</c:if>
</c:if>

</table>
</div>

<c:if test="${data.evaluation!=null}">
		<div id="layer">
			<div class="cont"><div class="close" onclick="closeEvaluation();"><img src="/view/image/close_but_01.png" width="20"></div>
			<div style="height:100px;"></div>
			<div> 服务态度: <span style="color:blue;">${data.evaluation.attitude_evaluation}</span> </div>
		    <div> 服务质量: <span style="color:blue;">${data.evaluation.quality_evaluation}</span> </div>
		    <div> 服务速度: <span style="color:blue;">${data.evaluation.speed_evaluation}</span> </div>
		    <div> 评价描述: <span style="color:blue;">${data.evaluation.text_evaluation}</span> </div>
			</div>
		</div>
</c:if>		

<c:if test="${data.orderBasic!=null}">
		<div id="order_pay">
			<div class="orderPay">
				<div class="cont"><div class="close" onclick="closeOrderpay();"><img src="/view/image/close_but_01.png" width="20"></div>
				<div style="height:80px;"></div>
				<div> 支付方式: <span >${data.orderBasic.orderPayType}</span> </div>
		   		 <div> 订单金额: <span >${data.orderBasic.order_price}</span> </div>
		   		 <div> 实付金额: <span >${data.orderBasic.order_actual_price}</span> </div>
			</div>
		</div>
</c:if>	

<c:if test="${data.orderPlanDetail!=null}">
		<div id="plan_layer">
			<div class="cont"><div class="close" onclick="closePlan();"><img src="/view/image/close_but_01.png" width="20"></div>
			<div style="height:100px;"></div>
			<c:forEach items="${data.orderPlanDetail}" var="item">
			<div id="${item.id}" class="plan">
			<div> 方案价格: <span >${item.price}</span> </div>
		    <div> 优惠价: <span >${item.discount_price}</span> </div>
		    <div> 方案内容: <span >${item.content}</span> </div>
		    <c:forEach items="${item.detail}" var="deitem">
		    <div><span >${deitem}</span> </div>
		    </c:forEach>   
		    </div>
            </c:forEach>
			</div>
		</div>
</c:if>	

<c:if test="${list!=null}">
		<div id="order_layer">
			<div class="cont">
			<div class="close" onclick="closeOrder();"><img src="/view/image/close_but_01.png" width="20"></div>
		    <div class="list_1">
		    	<ul class="ul">
		    	  <%-- <li>服务类型:${data.orderBasic.service_name}</li> --%>
				 <c:forEach items="${list}" var="item">
				  <li>${item}</li>
	             </c:forEach>    
				</ul>
		    </div>
			</div>
		</div>
</c:if>		
</body>
</html>