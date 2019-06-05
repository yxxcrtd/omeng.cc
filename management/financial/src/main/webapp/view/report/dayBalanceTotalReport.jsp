<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2016/7/12
  Time: 11:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>收/付款汇总日计表</title>

    <link rel="stylesheet" href="http://pmg.oomeng.cn/financial/css/table.css">
    <script src="http://pmg.oomeng.cn/financial/js/jquery-1.8.3.min.js"></script>
    <script>
        function doExport() {
            $("#exportForm textarea").text($("#divReport").html());
            $("#exportForm").submit();
        }
    </script>
</head>
<body>
<div style="margin-bottom: 5px;">
    <form action="" method="post" style="display: inline;">
        年：
        <select name="year">
            <c:forEach items="${yearList}" var="y">
                <option value="${y}" <c:if test="${y == year}">selected</c:if>>${y}</option>
            </c:forEach>
        </select>
        月：
        <select name="month">
            <c:forEach items="${monthList}" var="m">
                <option value="${m}" <c:if test="${m == month}">selected</c:if>>${m}</option>
            </c:forEach>
        </select>
        <button>查询</button>
    </form>
    <form id="exportForm" action="/report/htmlToExcel" method="post" style="display: inline;">
        <input type="hidden" name="filename" value="szhzrjb">
        <textarea name="html" style="display: none"></textarea>
        <button type="button" onclick="doExport()">导出Excel</button>
    </form>
</div>
<div id="divReport">
    <table class="bordered" style="width: 2120px; font-size: 13px">
        <thead>
        <tr>
            <th colspan="23">收/支汇总日计表</th>
        </tr>
        <tr>
            <th></th>
            <th colspan="6">线上支付收入</th>
            <th colspan="6">线下支付收入</th>
            <th colspan="6">钱包支付收入</th>
            <th colspan="4">费用支出</th>
        </tr>
        <tr>
            <th>日期</th>
            <th>VIP特权笔数</th>
            <th>VIP特权金额</th>
            <th>顾问号笔数</th>
            <th>顾问号金额</th>
            <th>抢单金笔数</th>
            <th>抢单金金额</th>
            <th>VIP特权笔数</th>
            <th>VIP特权金额</th>
            <th>顾问号笔数</th>
            <th>顾问号金额</th>
            <th>抢单金笔数</th>
            <th>抢单金金额</th>
            <th>VIP特权笔数</th>
            <th>VIP特权金额</th>
            <th>顾问号笔数</th>
            <th>顾问号金额</th>
            <th>抢单金笔数</th>
            <th>抢单金金额</th>
            <th>订单奖励笔数</th>
            <th>订单奖励金额</th>
            <th>活动奖励笔数</th>
            <th>活动奖励金额</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${dateList}" var="date">
            <c:set var="rMap" value="${reportMap.get(date)}"></c:set>
            <tr>
                <td>${date}</td>
                <td class="text-center">${rMap.get("zzfwxs_vip_1")}</td>
                <td class="text-right">${rMap.get("zzfwxs_vip_2")}</td>
                <td class="text-center">${rMap.get("zzfwxs_gwh_1")}</td>
                <td class="text-right">${rMap.get("zzfwxs_gwh_2")}</td>
                <td class="text-center">${rMap.get("zzfwxs_qdj_1")}</td>
                <td class="text-right">${rMap.get("zzfwxs_qdj_2")}</td>
                <td class="text-center">${rMap.get("zzfwxx_vip_1")}</td>
                <td class="text-right">${rMap.get("zzfwxx_vip_2")}</td>
                <td class="text-center">${rMap.get("zzfwxx_gwh_1")}</td>
                <td class="text-right">${rMap.get("zzfwxx_gwh_2")}</td>
                <td class="text-center">${rMap.get("zzfwxx_qdj_1")}</td>
                <td class="text-right">${rMap.get("zzfwxx_qdj_2")}</td>
                <td class="text-center">-</td>
                <td class="text-center">-</td>
                <td class="text-center">-</td>
                <td class="text-center">-</td>
                <td class="text-center">-</td>
                <td class="text-center">-</td>
                <td class="text-center">${rMap.get("fyzc_ddjl_1")}</td>
                <td class="text-right">${rMap.get("fyzc_ddjl_2")}</td>
                <td class="text-center">${rMap.get("fyzc_hdjl_1")}</td>
                <td class="text-right">${rMap.get("fyzc_hdjl_2")}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
