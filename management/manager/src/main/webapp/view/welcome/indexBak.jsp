<%@page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>善金管理系统</title>
	<link rel="stylesheet" href="http://pmg.oomeng.cn/manager/css/login.css"/>
	<style type="text/css">
	html{overflow-y:auto}
	</style>
	<script src="http://pmg.oomeng.cn/manager/js/jquery-1.8.0.min.js" type="text/javascript"></script>

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
	  $("#error").html("用户名为空");
	  return false;
  }else if(password==''){
	  $("#error").html("密码为空"); 
	  return false;
  }else if(captcha==''){
	  $("#error").html("验证码为空"); 
	  return false;
  }
   return true;
};
function changeValidateCode(obj) {  
    var currentTime= new Date().getTime();  
    obj.src = "/systemManager/showImage?d=" + currentTime;  
};
</script>
</head>
<body>
<div id="main">
	<div class="top">
		<div class="logo_fun">
			<div class="logo">
				
			</div>
			<div class="fun">
				<ul class="fun_ul">
					<li><a href="http://www.zan100.com/HELP.pdf" style="margin-right:19px;"></a><a href="http://www.zan100.com/help_docs/xh_user.pdf"></a></li>
					<li>办事处电话：400-011-9321</li>
				</ul>				
			</div>
		</div>
	</div>
	<div class="content" style="height: 800px;">
		<div class="content_cons">
			<div class="log">
			    
				<form action="/systemManager/login" method="post" onsubmit="return valid(this.form)" name="form1" id="form1" class="logininfo">
				<input type="hidden" name="biaoshi" value="hzxh"/>
				
				<h3>登录				
					   <span id="error" style='color:red;font-weight:bold; font-size: 12px;'>${error}</span>    
					</h3>
				<ul>
					<li><span>用户名</span><input type="text" id="username_" name="username_" value=""/></li>
					<li class="password"><span>密码</span><input type="password" id="password" name="password_"/></li>
					<li class="choose"><span>验证码</span><input type="text" id="captcha_" name="captcha_"/>
					<img id="check" onclick="changeValidateCode(this)" src="/systemManager/showImage"/>
					<a href="#" onclick="changeValidateCode(document.getElementById('check'));">换一张</a>
					</li>
					<li class="login_tip" id="message"></li>
					<li class="login_button"><span></span>
					<input id="login_button" type="submit" value=""/></li>
				</ul>
				</form>
			</div>
		</div>
	</div>
	<div class="footer">	
		<div class="footer_info">
			<p class="support">技术支持<a id="footer_a" target="_blank" href="http://omeng.cc/">善金科技</a>
		    </p>
	</div>
</div>



</div></body></html>