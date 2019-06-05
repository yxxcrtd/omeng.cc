<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/cms.tld" prefix="cms" %>
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
</head>
<script type="text/javascript">
    var dInfo;
    Ext.onReady(function () {
//        var payStatusStore = Ext.create('Ext.data.Store', {
//            fields: ['id', 'text'],
//            data: [
//                {"id": "", "text": "全部"},
//                {"id": "1", "text": "新预约"},
//                {"id": "2", "text": "待选择"},
//                {"id": "3", "text": "已确认"},
//                {"id": "4", "text": "已完成"},
//                {"id": "5", "text": "支付完成"},
//                {"id": "6", "text": "订单已过期"},
//                {"id": "7", "text": "无效订单"}
//            ]
//        });
        var txtUserName = Ext.create('Ext.form.field.Text', {
            fieldLabel: '姓名'
        });
        var txtPhone = Ext.create('Ext.form.field.Text', {
            fieldLabel: '手机号'
        });
        var txtTxBegin = Ext.create('Ext.form.field.Text', {
            fieldLabel: '可提现金额'
        });
        var txtTxEnd = Ext.create('Ext.form.field.Text', {
        });

        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            enableOverflow: true,
            width: '100%',
            items: [
                txtUserName, txtPhone, txtTxBegin, txtTxEnd,
                {
                    xtype: "button",
                    text: "查询",
                    handler: function () {
                        var params = {};
                        params.userName = txtUserName.getValue();
                        params.phone = txtPhone.getValue();
                        params.txBegin = txtTxBegin.getValue();
                        params.txEnd = txtTxEnd.getValue();

                        store.load({params: params});

                        getOrderTotals(params);
                    }
                }
            ]
        });
        dInfo = Ext.create('Ext.form.field.Display', {
            value: "累计余额：0元，累计可提现余额：0元"
        });
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            width: '100%',
            margin: "0 0 5 0",
            items: [
                dInfo,
                {
                    text: "导出",
                    handler: function () {
                        window.open("/report/exportUserAccountData");
                    }
                }
            ]
        });
        var store = Ext.create("Ext.data.Store", {
            pageSize: 20, //每页显示几条数据
            remoteSort: true,
            proxy: {
                type: 'ajax',
                url: '/report/userAccountData',
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    root: 'data',
                    idProperty: '#'
                }
            }, fields: [
                {name: 'user_name', type: 'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {name: 'user_phone'},
                {name: 'user_account_total'},
                {name: 'user_account_djs'},
                {name: 'user_account_ktx'},
                {name: 'user_account_txz'},
                {name: 'id'}
            ]
        });
        store.on('beforeload', function (store, options) {
            var params = {};
            params.userName = txtUserName.getValue();
            params.phone = txtPhone.getValue();
            params.txBegin = txtTxBegin.getValue();
            params.txEnd = txtTxEnd.getValue();
            Ext.apply(store.proxy.extraParams, params);
        });

        Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {header: '序号', xtype: 'rownumberer', width:"20px"},
                {header: '姓名', dataIndex: 'user_name'},
                {header: '手机号', dataIndex: 'user_phone'},
                {header: '钱包总额', dataIndex: 'user_account_total'},
                {header: '待结算金额', dataIndex: 'user_account_djs'},
                {header: '可提现金额', dataIndex: 'user_account_ktx'},
                {header: '提现中金额', dataIndex: 'user_account_txz'}
            ],
            width: '100%',
            forceFit: true, //自动填满表格
            bbar: Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
                displayInfo: true,
                store: store
            }),
            renderTo: Ext.getBody()
        });
        store.load({params: {start: 0, limit: 20}});
        getOrderTotals({});
    });
    function getOrderTotals(params) {
        Ext.Ajax.request({
            url: '/report/userAccountTotals',
            params: params,
            success: function (response) {
                var text = response.responseText;
                dInfo.setValue(text);
            }
        });
    }
</script>
<body>
</body>
</html>
