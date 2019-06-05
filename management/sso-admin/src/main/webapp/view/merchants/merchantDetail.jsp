<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>服务商详情</title>
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

a{display:block;color:#fff;text-decoration:none;}

td div{height:23px;}

#layerBox{background:url(http://pmg.oomeng.cn/manager/image/layerBj_.png) repeat left top;width:100%;height:100%;position: fixed;left:0;top:0;display: none;}
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

#layer{background:url(http://pmg.oomeng.cn/manager/image/layerBj_.png) repeat left top;width:100%;height:100%;position: fixed;left:0;top:0;display: none;}
#layer .close{width: 20px;height:20px;cursor:pointer;position: absolute;right:5px;top:5px; z-index: 5;}
#layer .cont{width:502px;height:300px;background:#fff;position:absolute;left:50%;top:50%;margin:-150px 0 0 -251px; z-index: 4;text-align: center;overflow:hidden;}
#layer .cont img{width:auto;height:100%;}
</style>
<script type="text/javascript" src="/view/js/jquery-1.8.0.min.js"></script>
<script type="text/javascript">
function picList(v){
	var len=0;
	   $.ajax({
		   async: false,   
           type:"POST",
           url:"/merchants/merchantPicList",
           data:{albumId:v},
           datatype: "json",         
           success:function(data){
        	   len = data.length;
        	   if(len>0){
        		   var str='';
        		   var listr='';
        		   var img = '';
        		   for(var i=0;i<len;i++){
        			   img = '<img id="img_'+i+'" src="'+data[i]+'" title="'+i+'">';
        			   listr='<li onclick="sbImg('+i+')">'+img+'</li>';
        			   if(i==0){
                		   $(".imgField").append(img);
        			   }
        			   str=str+listr;
        		   }
        		   $("#picImgUl").append(str); 
        		   $('#layerBox').show();
        	   }else{
        		   alert("相册为空，暂无照片！");
        		   return; 
        	   }         
           },
           //调用出错执行的函数
           error: function(){
               //请求出错处理
           }        
        });
}


function showMsg(v){
	 var pic = $("#authPath").val();
	 $('#layer').show();
}
</script>
</head>

<body>
<div style="margin-top:-8px;margin-left:-8px;">
<table frame=above style="word-wrap:break-word;" class="gridtable"  width="100%"  height="100%" >
<tr align="left">
<th style="width:20%">类别</th>
<th style="width:30%">内容</th>
<th>详情</th>
</tr>
<tr>
<td>服务商名称</td>
<td>${data.merchantName}</td>
<td>${data.detail}</td>
</tr>
<tr>
<td>增值服务</td>
<td>

<div>VIP会员</div>
<div>订单推送</div>
<div>顾问号</div>
</td>
<td>
<div>
<c:choose>
    <c:when test="${data.vipStatus==2}">
         已开通
    </c:when>
    <c:otherwise>
         未开通
    </c:otherwise>
</c:choose>
</div>
<div>${data.orderSurplusPrice}元</div>
<div>${data.maxEmployeeNum}个</div>
</td>
</tr>
<tr>
<td>联系方式</td>
<td>${data.telephone}</td>
<td></td>
</tr>
<tr>
<td>地理位置</td>
<td>${data.province} ${data.city}</td>
<td>${data.address}</td>
</tr>
<tr>
<td>公司员工</td>
<td>${data.employeeNum}个</td>
<td>
 <c:if test="${data.employeeNum>0}">
  <c:forEach items="${data.employeeList}" var="item">
      <c:out value="${item.name}(${item.phone}) "></c:out>
  </c:forEach>
 </c:if>
</td>
</tr>
<tr>
<td>行业类型</td>
<td>${data.appName}</td>
<td></td>
</tr>
<tr>
<td>抢单信息</td>
<td>近7日推送次数${data.pushCount}次,抢单次数${data.grabFrequency}次,服务次数${data.serviceFrequency}次,评价次数${data.totalCountEvaluation}次</td>
<td></td>
</tr>
<tr>
<td>服务项目</td>
<td style="word-wrap:break-word;" width="20">
 <c:if test="${data.serviceNum>0}">
  <c:forEach items="${data.serviceTypeList}" var="item">
      <c:out value="${item.serviceName} "></c:out>
  </c:forEach>
 </c:if>
</td>
<td></td>
</tr>
<tr>
<td>认证信息</td>
<td>
<c:choose>
    <c:when test="${data.authPath!=''}">
      <c:if test="${data.authStatus==0}">
              未通过
      </c:if>
      <c:if test="${data.authStatus==1}">
              已通过
      </c:if>
      <c:if test="${data.authStatus==2}">
              认证中
      </c:if>
    </c:when>
    <c:otherwise>
         未认证
    </c:otherwise>
</c:choose>
</td>
<td> 
<c:if test="${data.authPath!=''}">
<input id="authPath" type="hidden" value="${data.authPath}">
 <a href="javascript:;" onclick="showMsg(1);" style="display:block;color:#E94718;text-decoration:none;" >
 查看
 </a>  
</c:if>
</td>
</tr>
<tr>
<td>相册管理</td>
<td></td>
<td></td>
</tr>
</table>
</div>


<div class="pic_list">
<ul>
<c:if test="${data.albumNum>0}">
  <c:forEach items="${data.albumList}" var="item">
     <a href="javascript:;" onclick='picList(${item.albumId});'>
      <li><img src="/view/image/xiangce.png"><p>${item.albumName}</p></li>
     </a> 
  </c:forEach>
</c:if>
</ul>
</div>


<div id="layerBox">
	<div class="cont">
		<div class="close"><img src="/view/image/close_but_01.png" width="42"></div>
		<div  class="imgField">
			<a class="control prev"><</a>
			<a class="control next on">></a>
		</div>
		<div class="imgListField">
			<div class="scrollImg">
			   <ul id="picImgUl">
			   </ul>
			</div>
			<a class="control prev"><</a>
			<a class="control next on">></a>
		</div>
	</div>
</div>

		<div id="layer">
			<div class="cont"><div class="close"><img src="/view/image/close_but_01.png" width="20"></div>
			<img src="${data.authPath}" >
			</div>
		</div>

<script>
	/*换图*/
	
	function sbImg(i){
		var li_=$('#layerBox .imgListField .scrollImg li');
		var imgSrc=li_.eq(i).find('img').attr('src');
		var imgTitle=li_.eq(i).find('img').attr('title');
		$('#layerBox .imgField img').attr('src',imgSrc);
		$('#layerBox .imgField img').attr('title',imgTitle);
	}
	
	function smallImg(v){
		var len=$('#layerBox .imgListField .scrollImg li').length;
		var ulW=140*len;
		$('#layerBox .imgListField .scrollImg ul').width(ulW); 
		var ulS=parseInt($('#layerBox .imgListField .scrollImg ul').css('left'))/700-1;
		var ulL=parseInt($('#layerBox .imgListField .scrollImg ul').css('left'));
		if(ulW>700){
			  if(v&&ulL!=0){
	        	$('#layerBox .imgListField .scrollImg ul').animate({left:'+=700px'});
	        	$('#layerBox .cont .imgListField .next').addClass('on');
	        	if(Math.abs(ulL)==700){
	        		$('#layerBox .cont .imgListField .prev').removeClass('on');
	        	}
	        }
	        else if(!v&&ulS>-(ulW/700)){
	        	$('#layerBox .imgListField .scrollImg ul').animate({left:'-=700px'});
	        	$('#layerBox .cont .imgListField .prev').addClass('on');
	        	if(ulS<-(ulW/700)+1){
	        		$('#layerBox .cont .imgListField .next').removeClass('on');
	        	}
	        }
	        else{
	        	if(ulL==0){alert('到达第一张');}
	        	else{alert('到达最后一张');}
	        }
	      
		}  
        
	}
	
	function bigImg(v){
		var li_id=parseInt($('#layerBox .imgField img').attr('title'));
		var li_num=$('#layerBox .imgListField .scrollImg li').length;
		//alert(li_id);
		if(v&&li_id!=0){
			var li_num_on=li_id-1;
			var imgSrc=$('#layerBox .cont .imgListField li').eq(li_num_on).find('img').attr('src');
			$('#layerBox .cont .imgField .next').addClass('on');
			if(li_num_on==0){$('#layerBox .cont .imgField .prev').removeClass('on');}
			$('#layerBox .imgField img').attr('src',imgSrc);
			$('#layerBox .imgField img').attr('title',li_num_on);
		}
		else if(!v){
			//alert(li_id);
			if(li_id<li_num-1){
				$('#layerBox .cont .imgField .prev').addClass('on');
				if(li_id==li_num-2){$('#layerBox .cont .imgField .next').removeClass('on');};
				var li_num_on=li_id+1;
				var imgSrc=$('#layerBox .cont .imgListField li').eq(li_num_on).find('img').attr('src');
				$('#layerBox .imgField img').attr('src',imgSrc);
				$('#layerBox .imgField img').attr('title',li_num_on);
			}	
		}
	};
	$('#layerBox .cont .imgListField .prev').click(function(){
		smallImg(1);
	});
	$('#layerBox .cont .imgListField .next').click(function(){
		smallImg(0);
	});
	$('#layerBox .cont .imgField .prev').click(function(){
		bigImg(1);
	});
	$('#layerBox .cont .imgField .next').click(function(){
		bigImg(0);
	});
	$('#layerBox .close').click(function(){
		$('#layerBox').hide();
		$("#img_0").remove();
		$("#picImgUl").empty();
	});
	
	$('#layer .close').click(function(){
		$('#layer').hide();
	});
	
</script>
</body>
</html>