<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta charset="UTF-8">
		<title>邀请好友来剪彩</title>
		<script src="scripts/emRemLayer.js"></script>
		<link rel="stylesheet" type="text/css" href="styles/main.css">
		<meta name="viewport" id="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no,minimal-ui">
		<meta name="apple-touch-fullscreen" content="yes">
		<meta name="apple-mobile-web-app-capable" content="yes"> 
		<meta name="format-detection" content="telephone=no, email=no"> 
		<script src="scripts/jquery-1.8.3.min.js" ></script>
		<script src="scripts/fastclick.min.js" ></script>
		<script>
		    window.addEventListener( "load", function() { FastClick.attach( document.body ); }, false );
		    window.onload=function(){document.getElementById('loading').style.display="none";}
		</script>
	</head>
	<body>
	    <input id="merchant_id" type="hidden" value="${info.merchantInfo.merchantId}">
		<div id="loading"><span><img src="images/loading.gif" class="img"></span></div>
		<!--第1屏-->
		<section class="page_1 plrtb">
			<div class="Field_1">
				<div class="pic"><img src="${info.merchantInfo.icon}" class="img"></div>
				<div class="cont">
					<div class="title"><span class="name">${info.merchantInfo.name}</span><span class="city">${info.merchantInfo.city}</span></div>
					<div class="item">
					
                        <div class="TT">服务项目：</div>
						<div class="CC">${info.merchantInfo.serviceNames}</div>
						</div>
					<div class="desc">已有 <span class="num">${info.merchantInfo.totalNum}</span> 个好友帮忙剪彩</div>
				</div>
			</div>
			<div class="Field_2">
				<audio src="scripts/baozhu.mp3" id="myVoice0" loop="loop"></audio>
				<audio src="scripts/XIQIN.mp3" id="myVoice1" loop="loop"></audio>
				<div class="title"><span>选择标签送贺词</span></div>
				<div class="cont">
				  <c:forEach items="${info.labelList}" var="item">
                      <label><input id="${item.label}" type="checkbox" class="inputC" value="0">${item.labelDesc}</label>
                  </c:forEach>
				</div>
				<div class="btn"><input type="button" class="inputB inputC_1" value="帮TA剪彩"></div>
			</div>
		</section>
	    <!--成功剪裁-->
		<div class="layerPannel" style="visibility: hidden;">
			<div class="box_1 plrtb" style="display: none;">
				<div class="bjField plrtb"><img src="images/lbj_1.png" class="img"></div>
				<div class="imgField"><img src="images/lImg_1.png" class="img"></div>
				<div class="gifField_1"><img src="images/fireworks.gif" class="img"></div>
				<div class="gifField_2"><img src="images/ren.gif" class="img"></div>
				<div class="botField"><img src="images/lbj_2.jpg" class="img"></div>
			</div>
			<div class="box_2" style="display: none;">
				<div class="title">
					已为<span class="tt">${info.merchantInfo.name}</span>成功剪彩<br>多谢捧场！
				</div>
				<div class="btn_1">
					<a href="down.html" class="inputB">我也要开店</a>
				</div>
			</div>
		</div>
		<!--没选中标签提示框-->
		<div class="tishiPannel" style="display: none;">
			<div class="box">
				<p style="margin-top:1rem;">请选择标签送贺词~</p>
				<a class="close">关闭</a>
			</div>
		</div>

		<script>
			/*选项是否选中*/
			$('.Field_2 label input').click(function(){
		
				if($(this).parent('label').hasClass('cur')){
					$(this).parent('label').find('input:checkbox').parent('label').removeClass('cur');
					$(this).val(0);
				}
				else{
					$(this).parents('.cont').find('input:radio').parent('label').removeClass('cur');
					$(this).parent('label').addClass('cur');
					$(this).val(1);	
				}
				if($('.Field_2').find('.cur').length>0){
					$('.Field_2 .btn .inputB').removeClass('inputC_1');
				}
				else{
					$('.Field_2 .btn .inputB').addClass('inputC_1');
				}
			});

			/*动画弹框*/
			var audio0 = document.getElementById('myVoice0');
			var audio1 = document.getElementById('myVoice1');
			$('.btn .inputB').click(function(){
				if($('.page_1 .Field_2').find('.cur').length>0){
					submitCutting();
					audio0.play();
					audio1.play();
			    	$('.layerPannel').css({'visibility':'visible'});
			    	$('.layerPannel .box_1').show();
					var dhtime=setTimeout(function(){
						audio0.pause();
						audio1.pause();
						$('.layerPannel .box_1').fadeOut();
						$('.layerPannel .box_2').fadeIn();
					},4200);
				}
				else{
					$('.tishiPannel').show();
				}
		    });
		    /*关闭提示*/
		    $('.tishiPannel .close').click(function(){
		    	$('.tishiPannel').hide();
		    });
		    
		    function submitCutting(){
		    	var merchantId=$("#merchant_id").val();
		    	var label1=$("#label1").val();
		    	var label2=$("#label2").val();
		    	var label3=$("#label3").val();
		    	var label4=$("#label4").val();
		    	var label5=$("#label5").val();
		    	var label6=$("#label6").val();
		    	var label7=$("#label7").val();
		    	var label8=$("#label8").val();
		    	var label9=$("#label9").val();
		    	var label10=$("#label10").val();
		    	var label11=$("#label11").val();
		    	var label12=$("#label12").val();
		    	var label13=$("#label13").val();
		    	var label14=$("#label14").val();
		    	//1.$.ajax带json数据的异步请求  
		    	$.ajax( {    
		    	    url:'/activity/saveCuttingInfo',//    
		    	    data:{    
		    	    	     label1 : label1,    
		    	    	     label2 : label2, 
		    	    	     label3 : label3, 
		    	    	     label4 : label4, 
		    	    	     label5 : label5, 
		    	    	     label6 : label6, 
		    	    	     label7 : label7, 
		    	    	     label8 : label8, 
		    	    	     label9 : label9, 
		    	    	     label10 : label10, 
		    	    	     label11 : label11, 
		    	    	     label12 : label12, 
		    	    	     label13 : label13, 
		    	    	     label14 : label14, 
		    	    	     merchantId : merchantId  
		    	    },    
		    	    type:'post',    
		    	    cache:false,    
		    	    dataType:'json',    
		    	    success:function(data) {    
                        // alert(data);   
		    	     },    
		    	     error : function() {    
		    	         alert("异常！");    
		    	     }    
		    	});  
		    }
		    
		</script>
	</body>
</html>

