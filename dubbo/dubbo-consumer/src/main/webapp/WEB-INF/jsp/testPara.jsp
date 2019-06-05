<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	<title>测试</title>
  </head>
  <body>
    <form name="testForm" action="/data_api/testuser/getPara" method="post">  
        <input type="text" name="userId" > 
        <input type="text" name="name" > 
        <input type="submit" value="提交" >  
    </form>   
  </body>
</html>