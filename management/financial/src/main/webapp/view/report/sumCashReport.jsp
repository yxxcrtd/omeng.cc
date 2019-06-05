<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
    <script src="http://pmg.oomeng.cn/financial/js/my97/WdatePicker.js"></script>
    <script>
        function doExport() {
            $("#exportForm textarea").text($("#divReport").html());
            $("#exportForm").submit();
        }
    </script>
</head>
<body>
<div style="margin-bottom: 5px;">
    <form id="exportForm" action="/report/htmlToExcel" method="post" style="display: inline;">
        <input type="hidden" name="filename" value="fytjb">
        <textarea name="html" style="display: none"></textarea>
        <button type="button" onclick="doExport()">导出Excel</button>
    </form>
</div>
<div id="divReport">
    <table class="bordered" style="font-size: 13px">
        <thead>
        <tr>
            <th colspan="5">费用统计表</th>
        </tr>
        <tr>
            <th colspan="4" class="text-left">
                <form action="" method="post" style="display: inline;">
                    日期：
                    <span class="hide">${firstDay}</span>
                    <input name="firstDay" class="Wdate" type="text" onClick="WdatePicker()" value="${firstDay}" style="width: 100px;">
                    至
                    <span class="hide">${lastDay}</span>
                    <input name="lastDay" class="Wdate" type="text" onClick="WdatePicker()" value="${lastDay}" style="width: 100px;">
                    <button>查询</button>
                </form>
            </th>
            <th class="text-right">金额单位：元</th>
        </tr>
        <tr>
            <th>类型</th>
            <th></th>
            <th>笔数</th>
            <th>金额</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td colspan="2" class="text-left strong">奖励金</td>
            <c:set var="khbfjzj_1" value="${reportMap.get(\"khbfjzj_hdjl_1\")+reportMap.get(\"khbfjzj_ddjl_1\")}"></c:set>
            <c:set var="khbfjzj_2" value="${reportMap.get(\"khbfjzj_hdjl_2\")+reportMap.get(\"khbfjzj_ddjl_2\")}"></c:set>
            <td class="text-center strong">${khbfjzj_1}</td>
            <td class="text-right strong"><fmt:formatNumber value="${khbfjzj_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td>活动奖励</td>
            <td class="text-center">${reportMap.get("khbfjzj_hdjl_1")}</td>
            <td class="text-right">${reportMap.get("khbfjzj_hdjl_2")}</td>
            <td>统计平台发放给用户的千万粉丝活动的奖励</td>
        </tr>
        <tr>
            <td></td>
            <td>订单奖励</td>
            <td class="text-center">${reportMap.get("khbfjzj_ddjl_1")}</td>
            <td class="text-right">${reportMap.get("khbfjzj_ddjl_2")}</td>
            <td>统计平台发放给用户的千万粉丝活动的奖励</td>
        </tr>
        <tr>
            <th>合计</th>
            <th></th>
            <th class="text-center">${khbfjzj_1}</th>
            <th class="text-right"><fmt:formatNumber value="${khbfjzj_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></th>
            <th></th>
        </tr>
        </tbody>
    </table>
</div>
</body>
</html>
