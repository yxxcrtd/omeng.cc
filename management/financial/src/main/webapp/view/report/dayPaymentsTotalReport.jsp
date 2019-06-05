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
        <input type="hidden" name="filename" value="sfkhzrjb">
        <textarea name="html" style="display: none"></textarea>
        <button type="button" onclick="doExport()">导出Excel</button>
    </form>
</div>
<div id="divReport">
    <table class="bordered" style="width: 2000px; font-size: 13px">
        <thead>
        <tr>
            <th colspan="21">收/付款汇总日计表</th>
        </tr>
        <tr>
            <th></th>
            <th colspan="6">客户备付金增加</th>
            <th colspan="6">增值服务线上支付</th>
            <th colspan="6">增值服务线下支付</th>
            <th colspan="2">财务付款</th>
        </tr>
        <tr>
            <th>日期</th>
            <th>钱包充值笔数</th>
            <th>钱包充值金额</th>
            <th>商户订单笔数</th>
            <th>商户订单金额</th>
            <th>剪彩红包笔数</th>
            <th>剪彩红包金额</th>
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
            <th>提现笔数</th>
            <th>提现金额</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${dateList}" var="date">
            <c:set var="rMap" value="${reportMap.get(date)}"></c:set>
            <tr>
                <td>${date}</td>
                <td class="text-center">${rMap.get("khbfjzj_qbcz_1")}</td>
                <td class="text-center">${rMap.get("khbfjzj_qbcz_2")}</td>
                <td class="text-center">${rMap.get("khbfjzj_shdj_1")}</td>
                <td class="text-right">${rMap.get("khbfjzj_shdj_2")}</td>
                <td class="text-center">${rMap.get("khbfjzj_jchb_1")}</td>
                <td class="text-right">${rMap.get("khbfjzj_jchb_2")}</td>
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
                <td class="text-center">${rMap.get("cwfk_tx_1")}</td>
                <td class="text-right">${rMap.get("cwfk_tx_2")}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
