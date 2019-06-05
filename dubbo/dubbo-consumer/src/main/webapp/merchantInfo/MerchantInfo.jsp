<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src="/js/jquery-2.1.4/jquery.js"></script>
<script src="/js/jquery-2.1.4/jquery.min.js"></script>
</head>
<script>
	$(function() {
		getMerchantInfo();
	});
	function getMerchantInfo() {
		$.ajax({			
			type : "post",
			url : "/myMerchant/merchantDetailInfo",
			data : {//参数
				merchantId : '144013972128576014'
			},
            async : true, 
			dataType : "json",
			success : function(data) {
				var merchantInfo=data.merchantInfo;
				if(merchantInfo!=null && merchantInfo !=''){//获取数据成功					
					$("#name").html(merchantInfo.name);//店铺名称
					$("#image").html(merchantInfo.iconUrl);//店铺头像
					$("#auth").html(merchantInfo.auth);//店铺头像
					$("#serviceNum").html(merchantInfo.serviceNum);//店铺头像
					$("#detail").html(merchantInfo.detail);//店铺头像
					$("#locationAddress").html(merchantInfo.locationAddress);//店铺头像
				}
			},
			error : function() {			 
				alert("异常！");
			}
		});
	}
</script>
<body>
店铺名称：<div id="name"></div>
店铺头像：<div id="image"></div>
认证信息：<div id="auth"></div>
接单数：<div id="serviceNum"></div>
成交：<div id="serviceNum"></div>
店铺描述：<div id="detail"></div>
地址：<div id="locationAddress"></div>
</body>
</html>