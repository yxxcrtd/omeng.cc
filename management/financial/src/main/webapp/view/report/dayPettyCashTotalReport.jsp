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

        $(function () {
            //计算备付金净值
            $("table>tbody>tr").each(function(i, tr){
                tr = $(tr);

                var bfjjz = tr.find("td:last");

                //+
                var qbcz = parseFloat(tr.find("td:eq(2)").text());//钱包充值
                if(isNaN(qbcz)) qbcz = 0.00;
                var shdd = parseFloat(tr.find("td:eq(4)").text());//商户订单
                if(isNaN(shdd)) shdd = 0.00;
                var jchb = parseFloat(tr.find("td:eq(6)").text());//剪彩红包
                if(isNaN(jchb)) jchb = 0.00;
                var ddjl = parseFloat(tr.find("td:eq(8)").text());//订单奖励
                if(isNaN(ddjl)) ddjl = 0.00;
                var hdjl = parseFloat(tr.find("td:eq(10)").text());//活动奖励
                if(isNaN(hdjl)) hdjl = 0.00;

                //-
                var jhtx = parseFloat(tr.find("td:eq(12)").text());//活动奖励
                if(isNaN(jhtx)) jhtx = 0.00;
                var vip = parseFloat(tr.find("td:eq(14)").text());//活动奖励
                if(isNaN(vip)) vip = 0.00;
                var gwh = parseFloat(tr.find("td:eq(16)").text());//活动奖励
                if(isNaN(gwh)) gwh = 0.00;
                var qdj = parseFloat(tr.find("td:eq(18)").text());//活动奖励
                if(isNaN(qdj)) qdj = 0.00;

                bfjjz.text((qbcz + shdd + jchb + ddjl + hdjl - jhtx - vip - gwh - qdj).toFixed(2));
            });
        })
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
        <input type="hidden" name="filename" value="khbfjrjb">
        <textarea name="html" style="display: none"></textarea>
        <button type="button" onclick="doExport()">导出Excel</button>
    </form>
</div>
<div id="divReport">
    <table class="bordered" style="min-width: 2700px; font-size: 13px">
        <thead>
        <tr>
            <th colspan="24">客户备付金汇总日计表</th>
        </tr>
        <tr>
            <th></th>
            <th colspan="10">客户备付金增加</th>
            <th colspan="8">客户备付金减少</th>
            <th colspan="4">备付金内部交易</th>
            <th></th>
        </tr>
        <tr>
            <th>日期</th>
            <th>钱包充值笔数</th>
            <th>钱包充值金额</th>
            <th>商户订单笔数</th>
            <th>商户订单金额</th>
            <th>剪彩红包笔数</th>
            <th>剪彩红包金额</th>
            <th>订单奖励笔数</th>
            <th>订单奖励金额</th>
            <th>活动奖励笔数</th>
            <th>活动奖励金额</th>
            <th>用户提现笔数</th>
            <th>用户提现金额</th>
            <th>购买VIP特权笔数</th>
            <th>购买VIP特权金额</th>
            <th>购买顾问号笔数</th>
            <th>购买顾问号金额</th>
            <th>购买抢单金笔数</th>
            <th>购买抢单金金额</th>
            <th>商户订单收入笔数</th>
            <th>商户订单收入金额</th>
            <th>用户订单支付笔数</th>
            <th>用户订单支付金额</th>
            <th>备付金净值</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${dateList}" var="date">
            <c:set var="rMap" value="${reportMap.get(date)}"></c:set>
            <tr>
                <td>${date}</td>
                <td class="text-center">${rMap.get("khbfjzj_qbcz_1")}</td>
                <td class="text-right">${rMap.get("khbfjzj_qbcz_2")}</td>
                <td class="text-center">${rMap.get("khbfjzj_shdd_1")}</td>
                <td class="text-right">${rMap.get("khbfjzj_shdd_2")}</td>
                <td class="text-center">${rMap.get("khbfjzj_jchb_1")}</td>
                <td class="text-right">${rMap.get("khbfjzj_jchb_2")}</td>
                <td class="text-center">${rMap.get("khbfjzj_ddjl_1")}</td>
                <td class="text-right">${rMap.get("khbfjzj_ddjl_2")}</td>
                <td class="text-center">${rMap.get("khbfjzj_hdjl_1")}</td>
                <td class="text-right">${rMap.get("khbfjzj_hdjl_2")}</td>
                <td class="text-right">${rMap.get("khbfjjs_tx_1")}</td>
                <td class="text-right">${rMap.get("khbfjjs_tx_2")}</td>
                <td class="text-center">${rMap.get("zzfwxs_vip_1")}</td>
                <td class="text-right">${rMap.get("zzfwxs_vip_2")}</td>
                <td class="text-center">${rMap.get("zzfwxs_gwh_1")}</td>
                <td class="text-right">${rMap.get("zzfwxs_gwh_2")}</td>
                <td class="text-center">${rMap.get("zzfwxs_qdj_1")}</td>
                <td class="text-right">${rMap.get("zzfwxs_qdj_2")}</td>
                <td class="text-center">${rMap.get("bfjnbjy_shddsr_1")}</td>
                <td class="text-right">${rMap.get("bfjnbjy_shddsr_2")}</td>
                <td class="text-center">${rMap.get("bfjnbjy_yhddsr_1")}</td>
                <td class="text-right">${rMap.get("bfjnbjy_yhddsr_1")}</td>
                <td class="text-right">${rMap.get("bfjjz")}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
