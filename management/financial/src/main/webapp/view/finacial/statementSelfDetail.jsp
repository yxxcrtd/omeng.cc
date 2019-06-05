<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/cms.tld" prefix="cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Expires", "0");

    request.setCharacterEncoding("UTF-8");

    //String webRoot = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>O盟财务管理系统</title>
    <link href="http://pmg.oomeng.cn/manager/ExtJS4.2/resources/css/ext-all-neptune-rtl.css" rel="stylesheet">
    <link href="http://pmg.oomeng.cn/manager/ExtJS4.2/css/icon.css" rel="stylesheet">
    <link href="http://pmg.oomeng.cn/financial/css/msgTip.css" rel="stylesheet">
    <link href="http://pmg.oomeng.cn/financial/css/icon.css" rel="stylesheet">
    <script src="http://pmg.oomeng.cn/manager/ExtJS4.2/ext-all.js"></script>
    <script src="http://pmg.oomeng.cn/manager/ExtJS4.2/ux/data/PagingMemoryProxy.js"></script>
    <script src="http://pmg.oomeng.cn/manager/ExtJS4.2/ux/ProgressBarPager.js"></script>
    <script src="http://pmg.oomeng.cn/manager/ExtJS4.2/locale/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="http://pmg.oomeng.cn/financial/js/msgTip.js"></script>
    <script type="text/javascript" src="http://pmg.oomeng.cn/financial/js/localXHR.js"></script>
    <script type="text/javascript" src="http://pmg.oomeng.cn/manager/js/CustomSizePagingToolbar.js"></script>
    <link href="http://pmg.oomeng.cn/financial/date/select.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" href="http://pmg.oomeng.cn/financial/css/table.css">
    <script src="http://pmg.oomeng.cn/financial/js/jquery-1.8.3.min.js"></script>
</head>
<script type="text/javascript">
    var store = null;
    Ext.onReady(function () {
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.getElementById("toolbar"),
            width: '100%',
            margin: "0 0 5 0",
            items: [
                {
                    text: "导出Excel",
                    handler: function () {
                        doExport();
                    }
                }
            ]
        });
    });
    function doExport() {
        $("#exportForm textarea").text($("#divReport").html());
        $("#exportForm").submit();
    }
</script>


<body>
<form id="exportForm" action="/report/htmlToExcel" method="post" style="display: inline;">
    <input type="hidden" name="filename" value="钱包对账单明细">
    <textarea name="html" style="display: none"></textarea>
</form>
<div id="toolbar"></div>
<div class="tooltip" style="color: red;">提示：账单明细中存在不平衡的数据，请及时检查原因，并调整正确。</div>
<div id="divReport">
    <table class="bordered">
        <thead>
        <tr>
            <th></th>
            <th colspan="3">钱包平台数据</th>
            <th colspan="4">App平台数据</th>
            <th></th>
        </tr>
        <tr>
            <th>序号</th>
            <th>钱包业务编号</th>
            <th>金额</th>
            <th>时间</th>
            <th>App业务编号</th>
            <th>项目类型</th>
            <th>金额</th>
            <th>时间</th>
            <th>对账结果</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="obj" items="${list}" varStatus="s">
            <tr>
                <td>${s.index+1}</td>
                <td>${obj.get("wallet_no")}</td>
                <td>${obj.get("wallet_amount")}</td>
                <td><fmt:formatDate value="${obj.get(\"wallet_date\")}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td>${obj.get("omeng_no")}</td>
                <td>${obj.get("omeng_type")}</td>
                <td>${obj.get("omeng_amount")}</td>
                <td><fmt:formatDate value="${obj.get(\"omeng_date\")}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                <td>
                    <c:if test="${obj.get('result') == 0}">
                        <span style="color: red;">不平衡</span>
                    </c:if>
                    <c:if test="${obj.get('result') == 1}">
                        <span>平衡</span>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
