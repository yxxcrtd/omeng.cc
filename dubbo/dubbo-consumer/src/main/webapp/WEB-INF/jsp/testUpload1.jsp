<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
	<title>测试美容</title>
  </head>
  <body>
    <form name="testForm" action="/data_api/userOrder/insertBeautifyCarOrder" enctype="multipart/form-data" method="post">  
         <div id="newUpload2">  
            <input type="file" name="voice">
            <input type="file" name="picture0">  
            <input type="file" name="picture1"> 
            <input type="file" name="picture2"> 
            <input type="file" name="picture3"> 
            <input type="file" name="picture4">     
        </div>  
        <input type="text" name="userId" > 
        <input type="text" name="latitude" > 
        <input type="text" name="longitude" > 
        <input type="text" name="carParkAddress" > 
        <input type="text" name="hopeServiceTime" > 
        <input type="text" name="beautifyServiceItems" > 
        <input type="text" name="serviceSite" >
        <input type="text" name="beautifyDemand" >
        <input type="submit" value="上传" >  
    </form>   
  </body>
</html>