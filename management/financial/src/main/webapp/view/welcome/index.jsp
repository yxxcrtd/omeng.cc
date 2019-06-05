<%@page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>O盟财务管理系统</title>
	<script src="http://pmg.oomeng.cn/financial/js/jquery-1.8.0.min.js" type="text/javascript"></script>
	<link rel="stylesheet" href="http://pmg.oomeng.cn/financial/css/login.css"/>
	<script type="text/javascript">
	if (window != top)
		top.location.href = location.href; 
$(document).ready(function(){	

});

function valid(form) {  
	  var username=$("#username_").attr("value");
	  var password=$("#password").attr("value");
	  var captcha=$("#captcha_").attr("value");
	  if(username==''){
		  $(".tishi").html("账号不能为空");
		  return false;
	  }else if(password==''){
		  $(".tishi").html("密码不能为空"); 
		  return false;
	  }else if(captcha==''){
		  $(".tishi").html("验证码不能为空");
		  return false;
	  }
	   return true;
	};

function changeValidateCode(obj) {  
    var currentTime= new Date().getTime();  
    obj.src = "/system/showImage?d=" + currentTime;  
};
</script>
	</head>
	<body>
		<div id="pageContainer">
			<form action="/system/login" method="post" onsubmit="return valid(this.form)" name="form1" id="form1" >
			<div class="loginPannel">
				<p class="tishi">${error}</p>
				<table cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<th colspan="2" align="center" class="logo_"><img src="http://pmg.oomeng.cn/financial/image/login/logo.png" width="136"></th>
					</tr>
					<tr>
						<th width="20%">账&nbsp;&nbsp;&nbsp;&nbsp;号:</th>
						<td><input type="text" id="username_" name="username_" class="input inputText01"></td>
					</tr>
					<tr>
						<th>密&nbsp;&nbsp;&nbsp;&nbsp;码:</th>
						<td><input type="password" id="password_" name="password_" class="input inputText02"></td>
					</tr>
					<tr>
						<th>验证码:</th>
						<td><input type="text" id="captcha_" name="captcha_" class="input inputText03"></td>
					</tr>
					<tr>
						<th></th>
						<td><span class="le"><img id="check" onclick="changeValidateCode(this)" src="/system/showImage" width="96"/></span>
						<span class="le"><input type="button" class="inputBut_01" value="换一张" onclick="changeValidateCode(document.getElementById('check'));"></span>
						</td>
					</tr>
				</table>
				<div class="btn"><input type="submit" value="" class="inputBut"></div>
			</div>
			</form>
			<div class="inforPannel">技术支持 善金科技</div>
		</div>
        
	</body>
</html>
