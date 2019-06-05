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
    <title>客户备付金统计表</title>

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
        <input type="hidden" name="filename" value="khbfjtjb">
        <textarea name="html" style="display: none"></textarea>
        <button type="button" onclick="doExport()">导出Excel</button>
    </form>
</div>
<div id="divReport">
    <table class="bordered" style="font-size: 13px">
        <thead>
        <tr>
            <th colspan="5">客户备付金统计表</th>
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
            <th>发生笔数</th>
            <th>发生金额</th>
            <th>备注</th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td colspan="2" class="text-left strong">客户备付金增加</td>
            <c:set var="khbfjzj_1" value="${reportMap.get(\"khbfjzj_qbcz_1\")+reportMap.get(\"khbfjzj_shdd_1\")+reportMap.get(\"khbfjzj_jchb_1\")+reportMap.get(\"khbfjzj_ddjl_1\")+reportMap.get(\"khbfjzj_hdjl_1\")}"></c:set>
            <c:set var="khbfjzj_2" value="${reportMap.get(\"khbfjzj_qbcz_2\")+reportMap.get(\"khbfjzj_shdd_2\")+reportMap.get(\"khbfjzj_jchb_2\")+reportMap.get(\"khbfjzj_jchb_2\")+reportMap.get(\"khbfjzj_jchb_2\")}"></c:set>
            <td class="text-center">${khbfjzj_1}</td>
            <td class="text-right"><fmt:formatNumber value="${khbfjzj_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td>公司的第三方平台账户有资金流入</td>
        </tr>
        <tr>
            <td></td>
            <td>钱包充值</td>
            <td class="text-center">${reportMap.get("khbfjzj_qbcz_1")}</td>
            <td class="text-right">${reportMap.get("khbfjzj_qbcz_2")}</td>
            <td>统计用户充值的笔数及金额</td>
        </tr>
        <tr>
            <td></td>
            <td>商户订单</td>
            <td class="text-center">${reportMap.get("khbfjzj_shdd_1")}</td>
            <td class="text-right">${reportMap.get("khbfjzj_shdd_2")}</td>
            <td>除现金、钱包支付以为的商户订单收入</td>
        </tr>
        <tr>
            <td></td>
            <td>剪彩红包</td>
            <td class="text-center">${reportMap.get("khbfjzj_jchb_1")}</td>
            <td class="text-right">${reportMap.get("khbfjzj_jchb_2")}</td>
            <td>统计商户好友赠送的剪彩红包</td>
        </tr>
        <tr>
            <td></td>
            <td>订单奖励</td>
            <td class="text-center">${reportMap.get("khbfjzj_ddjl_1")}</td>
            <td class="text-right">${reportMap.get("khbfjzj_ddjl_2")}</td>
            <td>统计平台发放给商户的订单活动奖励</td>
        </tr>
        <tr>
            <td></td>
            <td>活动奖励</td>
            <td class="text-center">${reportMap.get("khbfjzj_hdjl_1")}</td>
            <td class="text-right">${reportMap.get("khbfjzj_hdjl_2")}</td>
            <td>统计平台发放给商户的千万粉丝活动的奖励；</td>
        </tr>

        <tr>
            <td colspan="2" class="text-left strong">客户备付金减少</td>
            <c:set var="khbfjjs_1" value="${reportMap.get(\"khbfjjs_yhtx_1\")+reportMap.get(\"khbfjjs_vip_1\")+reportMap.get(\"khbfjjs_gwh_1\")+reportMap.get(\"khbfjjs_qdj_1\")}"></c:set>
            <c:set var="khbfjjs_2" value="${reportMap.get(\"khbfjjs_yhtx_2\")+reportMap.get(\"khbfjjs_vip_2\")+reportMap.get(\"khbfjjs_gwh_2\")+reportMap.get(\"khbfjjs_qdj_2\")}"></c:set>
            <td class="text-center">${khbfjjs_1}</td>
            <td class="text-right"><fmt:formatNumber value="${khbfjjs_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td>有资金流入公司账户</td>
        </tr>
        <tr>
            <td></td>
            <td>用户提现</td>
            <td class="text-center">${reportMap.get("khbfjjs_yhtx_1")}</td>
            <td class="text-right">${reportMap.get("khbfjjs_yhtx_2")}</td>
            <td>统计提现完成的数据；</td>
        </tr>
        <tr>
            <td></td>
            <td>购买VIP特权</td>
            <td class="text-center">-${reportMap.get("khbfjjs_vip_1")}</td>
            <td class="text-right">-${reportMap.get("khbfjjs_vip_2")}</td>
            <td>用钱包余额购买VIP特权金额</td>
        </tr>
        <tr>
            <td></td>
            <td>购买顾问号</td>
            <td class="text-center">-${reportMap.get("khbfjjs_gwh_1")}</td>
            <td class="text-right">-${reportMap.get("khbfjjs_gwh_2")}</td>
            <td>用钱包余额购买顾问号金额；</td>
        </tr>
        <tr>
            <td></td>
            <td>购买抢单金</td>
            <td class="text-center">-${reportMap.get("khbfjjs_qdj_1")}</td>
            <td class="text-right">-${reportMap.get("khbfjjs_qdj_2")}</td>
            <td>用钱包余额购买抢单金金额；</td>
        </tr>
        <tr>
            <td colspan="2" class="text-left strong">客户备付金内部交易额</td>
            <c:set var="khbfjnbjy_1" value="${reportMap.get(\"khbfjnbjy_shddsr_1\")+reportMap.get(\"khbfjnbjy_yhddzf_1\")}"></c:set>
            <c:set var="khbfjnbjy_2" value="${reportMap.get(\"khbfjnbjy_shddsr_2\")+reportMap.get(\"khbfjnbjy_yhddzf_2\")}"></c:set>
            <td class="text-center">${khbfjnbjy_1}</td>
            <td class="text-right"><fmt:formatNumber value="${khbfjnbjy_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td>统计通过钱包购买的增值服务收入</td>
        </tr>
        <tr>
            <td></td>
            <td>商户订单收入</td>
            <td class="text-center">${reportMap.get("khbfjnbjy_shddsr_1")}-</td>
            <td class="text-right">${reportMap.get("khbfjnbjy_shddsr_2")}-</td>
            <td>商户收到的用户用钱包支付的订单收入金额</td>
        </tr>
        <tr>
            <td></td>
            <td>用户订单支付</td>
            <td class="text-center">${reportMap.get("khbfjnbjy_yhddzf_1")}-</td>
            <td class="text-right">${reportMap.get("khbfjnbjy_yhddzf_2")}-</td>
            <td>用户用钱包支付给商户的订单金额</td>
        </tr>
        <tr>
            <td class="strong">客户备付金净值</td>
            <td></td>
            <td class="text-center">${khbfjzj_1+khbfjjs_1+khbfjnbjy_1}</td>
            <td class="text-right"><fmt:formatNumber value="${khbfjzj_2+khbfjjs_2+khbfjnbjy_2}" pattern="#.##" minFractionDigits="2" > </fmt:formatNumber></td>
            <td></td>
        </tr>
        </tbody>
    </table>
</div>
</body>
</html>
