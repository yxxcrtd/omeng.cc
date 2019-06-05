<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户详情</title>
<!-- CSS goes in the document HEAD or added to your external stylesheet -->
<style type="text/css">
table.gridtable {
    table-layout:fixed;
	font-family: verdana,arial,sans-serif;
	font-size:14px;
	text-indent:20px; 
	color:#333333;
	border-width: 1px;
	border-color: #ededed;
	border-collapse: collapse;
}
table.gridtable th {
    height: 35px; 
	text-overflow: ellipsis;
	border-width: 1px;
	padding: 8px;
	border:0px;
	background-color: #f5f5f5;
}
table.gridtable tr{
    border:1px solid #ededed;
}
table.gridtable td {
    height: 35px; 
	text-overflow: ellipsis;
	border-width: 1px;
	padding: 8px;
	border:0px;
	background-color: #ffffff;
}
ul{list-style:none;}
ul li{ display:inline;margin-left:50px;} 
.pic_list{}
.pic_list li{ width:144px; margin:10px; float:left; text-align:center; line-height:20px; color:#666;background-color: #ffffff; }

a{display:block;color:#fff;text-decoration:none;}

td div{height:23px;}

#layerBox{background:url(http://pmg.oomeng.cn/manager/image/layerBj_.png) repeat left top;width:100%;height:100%;position: fixed;left:0;top:0;display: none;}
#layerBox ul{margin:0;padding:0;}
#layerBox .cont{width:800px;height:500px;background:#fff;position:absolute;left:50%;top:50%;margin:-250px 0 0 -400px; z-index: 4;}
#layerBox .cont .imgField{padding:10px;height:400px;position:relative;}
#layerBox .cont .control{position: absolute;top:200px;color:#000;font-size:40px;display:block;font-family: "宋体";font-weight: bold;cursor: pointer;color: #999;}
#layerBox .cont .control.on{color:#e94718;}
#layerBox .cont .prev{left:30px;}
#layerBox .cont .next{right:30px;}
#layerBox .cont .imgField img{width:780px;height:400px;}
#layerBox .cont .imgListField{width:100%;height:80px;position:relative;}
#layerBox .cont .imgListField .scrollImg{width:700px;height:80px;overflow: hidden;position:relative;left:45px;}
#layerBox .cont .imgListField .scrollImg ul{position:absolute;width:3000px;left:0;}
#layerBox .cont .imgListField li{float:left;width:130px;margin:0 0 0 10px;}
#layerBox .cont .imgListField img{width:130px;height:66px;}
#layerBox .cont .imgListField .control{top:14px;}
#layerBox .cont .imgListField .prev{left:15px;}
#layerBox .cont .imgListField .next{right:15px;}
#layerBox .close{width: 42px;height:42px;cursor:pointer;position: absolute;right: 15px;top:15px; z-index: 5;}

#layer{background:url(http://pmg.oomeng.cn/manager/image/layerBj_.png) repeat left top;width:100%;height:100%;position: fixed;left:0;top:0;display: none;}
#layer .close{width: 20px;height:20px;cursor:pointer;position: absolute;right:5px;top:5px; z-index: 5;}
#layer .cont{width:502px;height:300px;background:#fff;position:absolute;left:50%;top:50%;margin:-150px 0 0 -251px; z-index: 4;text-align: center;overflow:hidden;}
#layer .cont img{width:auto;height:100%;}
</style>
<script type="text/javascript" src="/view/js/jquery-1.8.0.min.js"></script>

</head>

<body>
<div style="margin-top:-8px;margin-left:-8px;">
<table frame=above  class="gridtable"  width="100%"  height="100%" >
<tr align="left">
<th style="width:20%">类别</th>
<th style="width:30%">内容</th>
</tr>
<tr>
<td>会员名称</td>
<td>${data.userName}</td>
</tr>
<tr>
<td>会员性别</td>
<td>${data.sex}</td>
</tr>
<tr>
<td>联系方式</td>
<td>${data.telephone}</td>
</tr>
<tr>
<td>地理位置</td>
<td>${data.province} ${data.city}</td>
</tr>
<tr>
<tr>
<td>关注店铺数</td>
<td>${data.focusMer}家</td>
</tr>
<tr>
<td>店铺</td>
<td>${data.merchantNameList}</td>
</tr>
<tr>
<td>累计发单数</td>
<td>${data.allOrderCount}次</td>
</tr>
<tr>
<td>新预约数</td>
<td>${data.newBooking}笔</td>
</tr>
<tr>
<td>进行中订单数</td>
<td>${data.ongoing}笔</td>
</tr>
<tr>
<td>已完成订单数</td>
<td>${data.completed}笔</td>
</tr>
<tr>
<td>已过期订单数</td>
<td>${data.timeOut}笔</td>
</tr>
<tr>
<td>用户关闭订单数</td>
<td>${data.closed}笔</td>
</tr>
</table>
</div>
</body>
</html>