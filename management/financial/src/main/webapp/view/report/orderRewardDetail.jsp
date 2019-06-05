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
        var txtDpm = Ext.create('Ext.form.field.Text', {
            fieldLabel: '服务商名称'
        });
        var txtBeginDate = Ext.create('Ext.form.field.Date', {
            fieldLabel: '订单完成时间'
        });
        var txtEndDate = Ext.create('Ext.form.field.Date', {});
        var provinceStore = Ext.create("Ext.data.Store", {
            pageSize: 50, // 每页显示几条数据
            proxy: {
                type: 'ajax',
                url: '/common/showArea',
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    root: 'data'
                }
            },
            fields: [{
                name: 'id'
            }, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {
                    name: 'area'
                }]
        });
        // 市信息
        var cityStore = Ext.create("Ext.data.Store", {
            pageSize: 50, // 每页显示几条数据
            proxy: {
                type: 'ajax',
                url: '/common/showServiveCity',
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    root: 'data'
                }
            },
            fields: [{
                name: 'id'
            }, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {
                    name: 'area'
                }]
        });
        var appTypeStore = Ext.create("Ext.data.Store", {
            pageSize: 50, // 每页显示几条数据
            proxy: {
                type: 'ajax',
                url: '/common/showAppType',
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    root: 'data'
                }
            },
            fields: [{
                name: 'app_type'
            }, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {
                    name: 'app_name'
                }]
        });
        var selProvince = Ext.create('Ext.form.ComboBox', {
            id: 'province', name: 'province', fieldLabel: '省', valueField: 'id', hiddenName: 'id',
            labelWidth: 40, displayField: 'area', store: provinceStore,
            listeners: { // 监听该下拉列表的选择事件
                select: function (combobox, record, index) {
                    Ext.getCmp('city').setValue('');
                    cityStore.load({
                        params: {
                            parentId: combobox.value
                        }
                    });
                }
            }, queryMode: 'local', labelAlign: 'left',width:130
        });
        var selCity = Ext.create('Ext.form.ComboBox', {
            name: 'city',
            id: 'city',
            labelWidth: 40,
            fieldLabel: '市',
            valueField: 'id',
            hiddenName: 'id',
            displayField: 'area',
            store: cityStore,
            queryMode: 'local',
            labelAlign: 'left',
            width:130
        });
        var selAppType = Ext.create('Ext.form.ComboBox', {
            name: 'appType',
            id: 'appType',
            labelWidth: 80,
            fieldLabel: '行业类型',
            valueField: 'app_type',
            hiddenName: 'app_type',
            displayField: 'app_name',
            store: appTypeStore,
            queryMode: 'local',
            labelAlign: 'left',
            width:200
        });
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            enableOverflow : true,
            width: '100%',
            margin:"0 0 5 0",
            items: [
                txtDpm,
                txtBeginDate,
                txtEndDate,
                selProvince,
                selCity,
                selAppType,
                {
                    xtype: "button",
                    text: "查询",
                    handler: function () {
                        var params = {};

                        params.merchantName = txtDpm.getValue();
                        if(txtBeginDate.getValue())
                            params.beginTime = Ext.Date.format(new Date(txtBeginDate.getValue()),'Y-m-d');
                        if(txtEndDate.getValue())
                            params.endTime = Ext.Date.format(new Date(txtEndDate.getValue()),'Y-m-d');
                        params.province = selProvince.getRawValue();
                        params.city = selCity.getRawValue();
                        params.appType = selAppType.getValue();

                        store.load();
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
                url: '/report/orderRewardData',
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    root: 'data',
                    idProperty: '#'
                }
            }, fields: [
                {name: 'name', type: 'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {name: 'deal_time'},
                {name: 'service_type_name'},
                {name: 'order_price'},
                {name: 'order_actual_price'},
                {name: 'amount'},
                {name: 'create_time'},
                {name: 'id'},
                {name: 'province'},
                {name: 'city'},
                {name: 'app_name'},
                {name: 'order_no'}
            ]
        });
        store.on('beforeload', function (store, options) {
            var params = {};

            params.merchantName = txtDpm.getValue();
            if(txtBeginDate.getValue())
                params.beginTime = Ext.Date.format(new Date(txtBeginDate.getValue()),'Y-m-d');
            if(txtEndDate.getValue())
                params.endTime = Ext.Date.format(new Date(txtEndDate.getValue()),'Y-m-d');
            params.province = selProvince.getRawValue();
            params.city = selCity.getRawValue();
            params.appType = selAppType.getValue();

            Ext.apply(store.proxy.extraParams, params);
        });

        Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {header: '序号', xtype: 'rownumberer', width: '20px'},
                {header: '订单号', dataIndex: 'order_no', width: '10%'},
                {header: '省份', dataIndex: 'province', width: '10%'},
                {header: '城市', dataIndex: 'city', width: '10%'},
                {header: '服务商名称', dataIndex: 'name', width: '10%'},
                {header: '订单完成时间', dataIndex: 'deal_time', width: '10%'},
                {header: '行业类型', dataIndex: 'app_name', width: '10%'},
//                {header: '订单类型', dataIndex: 'service_type_name'},
//                {header: '评价', dataIndex: 'phone', width: '10%'},
                {header: '应付金额', dataIndex: 'order_price'},
                {header: '实付金额', dataIndex: 'order_actual_price', width: '5%'},
                {header: '奖励金额', dataIndex: 'amount', width: '5%'},
                {header: '奖励发放时间', dataIndex: 'create_time'}
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
        provinceStore.load();
        appTypeStore.load();
        getTotals({});
    });

    function getTotals(params){
        Ext.Ajax.request({
            url: '/report/orderRewardTotals',
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
