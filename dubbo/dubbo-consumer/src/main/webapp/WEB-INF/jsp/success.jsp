<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	<title>测试</title>
  </head>
  <body>
    <form name="testForm" action="/data_api/testuser/upload2" enctype="multipart/form-data" method="post"">  
        <div id="newUpload2">  
            <input type="file" name="file">  
        </div>  
        <input type="text" name="phone" > 
        <input type="submit" value="上传" >  
    </form>   
  </body>
</html>