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
        <input type="hidden" name="filename" value="sktjb">
        <textarea name="html" style="display: none"></textarea>
        <button type="button" onclick="doExport()">导出Excel</button>
    </form>
</div>
<div id="divReport">
    <table class="bordered" style="font-size: 13px">
        <thead>
        <tr>
            <th colspan="5">收款统计表</th>
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
            <td colspan="2" class="text-left strong">客户备付金增加</td>
            <c:set var="khbfjzj_1" value="${reportMap.get(\"khbfjzj_shdd_1\")+reportMap.get(\"khbfjzj_jchb_1\")}"></c:set>
            <c:set var="khbfjzj_2" value="${reportMap.get(\"khbfjzj_shdd_2\")+reportMap.get(\"khbfjzj_jchb_2\")}"></c:set>
            <td class="text-center strong">${khbfjzj_1}</td>
            <td class="text-right strong"><fmt:formatNumber value="${khbfjzj_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td>钱包充值</td>
            <td class="text-center">${reportMap.get("khbfjzj_qbcz_1")}</td>
            <td class="text-right">${reportMap.get("khbfjzj_qbcz_2")}</td>
            <td>用户的钱包充值金额；</td>
        </tr>
        <tr>
            <td></td>
            <td>商户订单</td>
            <td class="text-center">${reportMap.get("khbfjzj_shdd_1")}</td>
            <td class="text-right">${reportMap.get("khbfjzj_shdd_2")}</td>
            <td>不含现金、钱包支付的商户订单金额</td>
        </tr>
        <tr>
            <td></td>
            <td>剪彩红包</td>
            <td class="text-center">${reportMap.get("khbfjzj_jchb_1")}</td>
            <td class="text-right">${reportMap.get("khbfjzj_jchb_2")}</td>
            <td>不含现金、钱包支付的商户的剪彩红包；</td>
        </tr>
        <tr>
            <td colspan="2" class="text-left strong">增值服务线上支付收入</td>
            <c:set var="zzfwxs_1" value="${reportMap.get(\"zzfwxs_vip_1\")+reportMap.get(\"zzfwxs_gwh_1\")+reportMap.get(\"zzfwxs_qdj_1\")}"></c:set>
            <c:set var="zzfwxs_2" value="${reportMap.get(\"zzfwxs_vip_2\")+reportMap.get(\"zzfwxs_gwh_2\")+reportMap.get(\"zzfwxs_qdj_2\")}"></c:set>
            <td class="text-center strong">${zzfwxs_1}</td>
            <td class="text-right strong"><fmt:formatNumber value="${zzfwxs_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td>VIP特权</td>
            <td class="text-center">${reportMap.get("zzfwxs_vip_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxs_vip_2")}</td>
            <td>商户线上支付完成的VIP特权金额（不含现金、钱包支付）</td>
        </tr>
        <tr>
            <td></td>
            <td>顾问号</td>
            <td class="text-center">${reportMap.get("zzfwxs_gwh_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxs_gwh_2")}</td>
            <td>商户线上支付完成的顾问号金额（不含现金、钱包支付）</td>
        </tr>
        <tr>
            <td></td>
            <td>抢单金</td>
            <td class="text-center">${reportMap.get("zzfwxs_qdj_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxs_qdj_2")}</td>
            <td>商户线上支付完成的抢单金金额（不含现金、钱包支付）</td>
        </tr>
        <tr>
            <td colspan="2" class="text-left strong">增值服务线下转账收入</td>
            <c:set var="zzfwxx_1" value="${reportMap.get(\"zzfwxx_vip_1\")+reportMap.get(\"zzfwxx_gwh_1\")+reportMap.get(\"zzfwxx_qdj_1\")}"></c:set>
            <c:set var="zzfwxx_2" value="${reportMap.get(\"zzfwxx_vip_2\")+reportMap.get(\"zzfwxx_gwh_2\")+reportMap.get(\"zzfwxx_qdj_2\")}"></c:set>
            <td class="text-center strong">${zzfwxx_1}</td>
            <td class="text-right strong"><fmt:formatNumber value="${zzfwxx_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td></td>
        </tr>
        <tr>
            <td></td>
            <td>VIP特权</td>
            <td class="text-center">${reportMap.get("zzfwxx_vip_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxx_vip_2")}</td>
            <td>商户线下支付完成的VIP特权金额</td>
        </tr>
        <tr>
            <td></td>
            <td>顾问号</td>
            <td class="text-center">${reportMap.get("zzfwxx_gwh_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxx_gwh_2")}</td>
            <td>商户线下支付完成的顾问号金额</td>
        </tr>
        <tr>
            <td></td>
            <td>抢单金</td>
            <td class="text-center">${reportMap.get("zzfwxx_qdj_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxx_qdj_2")}</td>
            <td>商户线下支付完成的抢单金金额</td>
        </tr>
        <tr>
            <td class="strong">合计</td>
            <td></td>
            <td class="text-center">${khbfjzj_1+zzfwxs_1+zzfwxx_1}</td>
            <td class="text-right"><fmt:formatNumber value="${khbfjzj_2+zzfwxs_2+zzfwxx_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td></td>
        </tr>
        </tbody>
    </table>
</div>
</body>
</html>
