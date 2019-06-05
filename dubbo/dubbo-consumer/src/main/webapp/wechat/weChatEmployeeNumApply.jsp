<%@ page language="java" contentType="text/html; charset=GBK" pageEncoding="GBK"%>
<%@ page import="com.tenpay.service.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
//---------------------------------------------------------
//财付通支付通知（后台通知）示例，商户按照此文档进行开发即可
//---------------------------------------------------------
//商户号
//String partner = "1253386901";

//密钥
//String key = "562f50d8c68b6a1e27d36e7a3b92c34d";
ResponseHandler resHandler = new ResponseHandler(request, response);
if(WeChartMerchantInfoService.increaseEmployeeNumApply(request, response)){
	resHandler.sendToCFT("Success");
}

%>

