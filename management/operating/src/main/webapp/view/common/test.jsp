<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
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
<title>test</title>
<script type="text/javascript">
function getData(){
	$.ajax({
		 type : "POST",  //提交方式  
         url : "/manager/common/getData",//路径  
         data : {  
             
         },//数据，这里使用的是Json格式进行传输  
         success : function(result) {//返回数据根据结果进行相应的处理  
             if ( result.success ) {  
            	 var data=result.data;
            	 for(var i=0; i<data.length; i++){
                     var htmRow="<tr> <td>"+data[i].name+"</td><td>"+data[i].sex+"</td></tr>";
                     $("#tab").append(htmRow);
                 }
             } else {  
                 $("#tipMsg").text("删除数据失败");  
             }  
         }
	});
}
</script>
<script src="${CONTEXT_PATH}/view/js/jquery-1.8.0.min.js"></script>
</head>
<body>
<table border="1" id="tab">

<tr>
<td>姓名</td>
<td>性别</td>
</tr>

</table>
<input type="button" name="name" value="异步获取数据" onclick="getData();"/>
</body>
</html>