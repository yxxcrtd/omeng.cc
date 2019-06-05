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
        <input type="hidden" name="filename" value="收入统计表">
        <textarea name="html" style="display: none"></textarea>
        <button type="button" onclick="doExport()">导出Excel</button>
    </form>
</div>
<div id="divReport">
    <table class="bordered" style="font-size: 13px">
        <thead>
        <tr>
            <th colspan="5">收入统计表</th>
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
            <td colspan="2" class="text-left strong">线上支付</td>
            <c:set var="zzfwxs_1" value="${reportMap.get(\"zzfwxs_vip_1\")+reportMap.get(\"zzfwxs_gwh_1\")+reportMap.get(\"zzfwxs_qdj_1\")}"></c:set>
            <c:set var="zzfwxs_2" value="${reportMap.get(\"zzfwxs_vip_2\")+reportMap.get(\"zzfwxs_gwh_2\")+reportMap.get(\"zzfwxs_qdj_2\")}"></c:set>
            <td class="text-center strong">${zzfwxs_1}</td>
            <td class="text-right strong"><fmt:formatNumber value="${zzfwxs_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td>公司的第三方平台账户有资金流入</td>
        </tr>
        <tr>
            <td></td>
            <td>VIP特权</td>
            <td class="text-center">${reportMap.get("zzfwxs_vip_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxs_vip_2")}</td>
            <td>服务开通的时间点来分摊收入，当月开通的当月分摊，分摊金额=支付金额/开通月数；</td>
        </tr>
        <tr>
            <td></td>
            <td>顾问号</td>
            <td class="text-center">${reportMap.get("zzfwxs_gwh_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxs_gwh_2")}</td>
            <td>服务开通的时间点来分摊收入，当月开通的当月分摊，分摊金额=支付金额/开通月数；</td>
        </tr>
        <tr>
            <td></td>
            <td>抢单金</td>
            <td class="text-center">${reportMap.get("zzfwxs_qdj_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxs_qdj_2")}</td>
            <td>根据用户实际消费的抢单金统计；</td>
        </tr>
        <tr>
            <td colspan="2" class="text-left strong">线下收入</td>
            <c:set var="zzfwxx_1" value="${reportMap.get(\"zzfwxx_vip_1\")+reportMap.get(\"zzfwxx_gwh_1\")+reportMap.get(\"zzfwxx_qdj_1\")}"></c:set>
            <c:set var="zzfwxx_2" value="${reportMap.get(\"zzfwxx_vip_2\")+reportMap.get(\"zzfwxx_gwh_2\")+reportMap.get(\"zzfwxx_qdj_2\")}"></c:set>
            <td class="text-center strong">${zzfwxx_1}</td>
            <td class="text-right strong"><fmt:formatNumber value="${zzfwxx_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td>有资金流入公司账户</td>
        </tr>
        <tr>
            <td></td>
            <td>VIP特权</td>
            <td class="text-center">${reportMap.get("zzfwxx_vip_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxx_vip_2")}</td>
            <td>服务开通的时间点来分摊收入，当月开通的当月分摊，分摊金额=支付金额/开通月数；</td>
        </tr>
        <tr>
            <td></td>
            <td>顾问号</td>
            <td class="text-center">${reportMap.get("zzfwxx_gwh_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxx_gwh_2")}</td>
            <td>服务开通的时间点来分摊收入，当月开通的当月分摊，分摊金额=支付金额/开通月数；</td>
        </tr>
        <tr>
            <td></td>
            <td>抢单金</td>
            <td class="text-center">${reportMap.get("zzfwxx_qdj_1")}</td>
            <td class="text-right">${reportMap.get("zzfwxx_qdj_2")}</td>
            <td>根据用户实际消费的抢单金统计；</td>
        </tr>
        <tr>
            <td colspan="2" class="text-left strong">钱包收入</td>
            <c:set var="qbsr_1" value="${reportMap.get(\"qbsr_vip_1\")+reportMap.get(\"qbsr_gwh_1\")+reportMap.get(\"qbsr_qdj_1\")}"></c:set>
            <c:set var="qbsr_2" value="${reportMap.get(\"qbsr_vip_2\")+reportMap.get(\"qbsr_gwh_2\")+reportMap.get(\"qbsr_qdj_2\")}"></c:set>
            <td class="text-center strong">${qbsr_1}</td>
            <td class="text-right strong"><fmt:formatNumber value="${qbsr_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td>统计通过钱包购买的增值服务收入</td>
        </tr>
        <tr>
            <td></td>
            <td>VIP特权</td>
            <td class="text-center">${reportMap.get("qbsr_vip_1")}-</td>
            <td class="text-right">${reportMap.get("qbsr_vip_2")}-</td>
            <td>服务开通的时间点来分摊收入，当月开通的当月分摊，分摊金额=支付金额/开通月数；</td>
        </tr>
        <tr>
            <td></td>
            <td>顾问号</td>
            <td class="text-center">${reportMap.get("qbsr_gwh_1")}-</td>
            <td class="text-right">${reportMap.get("qbsr_gwh_2")}-</td>
            <td>服务开通的时间点来分摊收入，当月开通的当月分摊，分摊金额=支付金额/开通月数；</td>
        </tr>
        <tr>
            <td></td>
            <td>抢单金</td>
            <td class="text-center">${reportMap.get("qbsr_qdj_1")}-</td>
            <td class="text-right">${reportMap.get("qbsr_qdj_1")}-</td>
            <td>根据用户实际消费的抢单金统计；</td>
        </tr>
        <tr>
            <td class="strong">合计</td>
            <td></td>
            <td class="text-center">${zzfwxs_1+zzfwxx_1+qbsr_1}</td>
            <td class="text-right"><fmt:formatNumber value="${zzfwxs_2+zzfwxx_2+qbsr_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td></td>
        </tr>
        </tbody>
    </table>
</div>
</body>
</html>
