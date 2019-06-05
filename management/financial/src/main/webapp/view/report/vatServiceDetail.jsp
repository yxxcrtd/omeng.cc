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
        var payTypeStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'text'],
            data: [
                {"id": "", "text": "全部"},
                {"id": "1", "text": "支付宝"},
                {"id": "2", "text": "微信"},
                {"id": "3", "text": "现金"}
            ]
        });
        var serviceTypeStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'text'],
            data: [
                {"id": "", "text": "全部"},
                {"id": "1", "text": "VIP服务"},
                {"id": "2", "text": "顾问号"},
                {"id": "3", "text": "订单推送"}
            ]
        });

        var txtFws = Ext.create('Ext.form.field.Text', {
            fieldLabel: '服务商'
        });
        var txtPhone = Ext.create('Ext.form.field.Text', {
            fieldLabel: '手机号'
        });
        var selZzfwlx = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '增值服务类型',
            value: "",
            store: serviceTypeStore,
            queryMode: 'local',
            editable: false,
            displayField: 'text',
            valueField: 'id',
            renderTo: Ext.getBody()
        });
        var selPayType = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '支付方式',
            value: "",
            store: payTypeStore,
            queryMode: 'local',
            editable: false,
            displayField: 'text',
            valueField: 'id',
            renderTo: Ext.getBody()
        });
        var txtBeginBuyDate = Ext.create('Ext.form.field.Date', {
            fieldLabel: '购买日期'
        });
        var txtEndBuyDate = Ext.create('Ext.form.field.Date', {});
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            enableOverflow : true,
            width: '100%',
            margin:"0 0 0 0",
            border:"1 1 0 1",
            items: [
                txtFws,
                txtPhone,
                selZzfwlx,
                selPayType
            ]
        });
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            enableOverflow : true,
            width: '100%',
            margin:"0 0 5 0",
            border:"0 1 1 1",
            items: [
                txtBeginBuyDate,
                txtEndBuyDate,
                {
                    xtype: "button",
                    text: "查询",
                    handler: function () {
                        var params = {};
                        params.merchantName = txtFws.getValue();
                        params.vatType = selZzfwlx.getValue();
                        params.payType = selPayType.getValue();
                        if(txtBeginBuyDate.getValue())
                            params.beginTime = Ext.Date.format(new Date(txtBeginBuyDate.getValue()),'Y-m-d');
                        if(txtEndBuyDate.getValue())
                            params.endTime = Ext.Date.format(new Date(txtEndBuyDate.getValue()),'Y-m-d');
                        params.phone = txtPhone.getValue();

                        store.load({params:params});

                        getTotals(params);
                    }
                }
            ]
        });
        dInfo = Ext.create('Ext.form.field.Display', {
            value: "订单笔数：0笔，总金额：0元"
        });
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            width: '100%',
            margin: "0 0 5 0",
            items: [
                dInfo,
//                {text: "导出"}
            ]
        });
        var store = Ext.create("Ext.data.Store", {
            pageSize: 20, //每页显示几条数据
            remoteSort: true,
            proxy: {
                type: 'ajax',
                url: '/report/vatServiceData',
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    root: 'data',
                    idProperty: '#'
                }
            }, fields: [
                {name: 'name', type: 'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {name: 'telephone'},
                {name: 'vat_type'},
                {name: 'money'},
                {name: 'apply_time'},
                {name: 'pay_type'},
                {name: 'open_time'},
                {name: 'id'}
            ]
        });
        store.on('beforeload', function (store, options) {
            var params = {};
            params.merchantName = txtFws.getValue();
            params.vatType = selZzfwlx.getValue();
            params.payType = selPayType.getValue();
            if(txtBeginBuyDate.getValue())
                params.beginTime = Ext.Date.format(new Date(txtBeginBuyDate.getValue()),'Y-m-d');
            if(txtEndBuyDate.getValue())
                params.endTime = Ext.Date.format(new Date(txtEndBuyDate.getValue()),'Y-m-d');
            params.phone = txtPhone.getValue();
            Ext.apply(store.proxy.extraParams, params);
        });

        Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {header: '序号', xtype: 'rownumberer', width: '5%'},
                {header: '服务商', dataIndex: 'name', width: '10%'},
                {header: '手机号', dataIndex: 'telephone'},
                {header: '增值服务类型', dataIndex: 'vat_type', width: '10%'},
                {header: '金额', dataIndex: 'money'},
                {header: '购买日期', dataIndex: 'apply_time', width: '15%'},
                {header: '支付方式', dataIndex: 'pay_type', width: '10%', renderer:function(val){
                    if(val == 1){
                        return "支付宝";
                    }else if(val == 2){
                        return "微信";
                    }else if(val == 3){
                        return "现金";
                    }
                }},
                {header: '开通时间', dataIndex: 'open_time'}
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
        getTotals({});
    });

    function getTotals(params){
        Ext.Ajax.request({
            url: '/report/vatServiceTotals',
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
