<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/cms.tld" prefix="cms" %>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setHeader("Expires","0");

	request.setCharacterEncoding("UTF-8");
	
	String httpstr="http://"+request.getServerName();
	int port = request.getServerPort();
	if(port!=0 && port!=80)
		httpstr+=":"+port;
	httpstr += request.getContextPath();
	request.setAttribute("MAN_PATH", httpstr);//域名地址
	
	//String webRoot = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="http://pmg.oomeng.cn/manager/ExtJS4.2/resources/css/ext-all-neptune-rtl.css" rel="stylesheet">
    <link href="http://pmg.oomeng.cn/manager/ExtJS4.2/css/icon.css" rel="stylesheet">
    <link href="http://pmg.oomeng.cn/manager/css/icon.css" rel="stylesheet">
    <link href="http://pmg.oomeng.cn/manager/css/msgTip.css" rel="stylesheet">
    <script src="http://pmg.oomeng.cn/manager/ExtJS4.2/ext-all.js"></script>
    <script src="http://pmg.oomeng.cn/manager/ExtJS4.2/ux/data/PagingMemoryProxy.js"></script>
    <script src="http://pmg.oomeng.cn/manager/ExtJS4.2/ux/ProgressBarPager.js"></script>
    <script src="http://pmg.oomeng.cn/manager/ExtJS4.2/locale/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="http://pmg.oomeng.cn/manager/js/localXHR.js"></script>
    <script type="text/javascript" src="http://pmg.oomeng.cn/manager/date/UX_TimePickerField.js"></script>
    <script type="text/javascript" src="http://pmg.oomeng.cn/manager/date/UX_DateTimePicker.js"></script>
    <script type="text/javascript" src="http://pmg.oomeng.cn/manager/date/UX_DateTimeMenu.js"></script>
    <script type="text/javascript" src="http://pmg.oomeng.cn/manager/date/UX_DateTimeField.js"></script>
    <script type="text/javascript" src="http://pmg.oomeng.cn/manager/js/msgTip.js"></script>  
    <script type="text/javascript" src="http://pmg.oomeng.cn/manager/js/CustomSizePagingToolbar.js"></script>
    <link href="http://pmg.oomeng.cn/manager/date/select.css" rel="stylesheet" type="text/css" />
</head>
<body>
</body>
</html>