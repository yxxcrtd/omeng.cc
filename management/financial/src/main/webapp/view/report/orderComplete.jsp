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
//                {"id": "3", "text": "现金"}
            ]
        });
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
        var txtOrderNo = Ext.create('Ext.form.field.Text', {
            fieldLabel: '订单号'
        });
        var txtServiceName = Ext.create('Ext.form.field.Text', {
            fieldLabel: '服务商'
        });
        var txtPhone = Ext.create('Ext.form.field.Text', {
            fieldLabel: '手机号'
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
        var txtDealBeginTime = Ext.create('Ext.form.field.Date', {
            fieldLabel: '完成日期'
        });
        var txtDealEndTime = Ext.create('Ext.form.field.Date', {});
        var txtPayNo = Ext.create('Ext.form.field.Text', {
            fieldLabel: "关联支付单号"
        });
//        var selPayStatus = Ext.create('Ext.form.ComboBox', {
//            fieldLabel: '支付状态',
//            value: "",
//            store: payStatusStore,
//            queryMode: 'local',
//            editable: false,
//            displayField: 'text',
//            valueField: 'id',
//            renderTo: Ext.getBody()
//        });
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

        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            enableOverflow: true,
            width: '100%',
            border: '1 1 0 1',
            items: [
                txtOrderNo,
                txtServiceName,
                txtPhone,
                selPayType
            ]
        });
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            enableOverflow: true,
            width: '100%',
            border: '0 1 1 1',
            margin: "0 0 5 0",
            items: [
                txtDealBeginTime,
                txtDealEndTime,
                txtPayNo,
                selProvince, selCity,
//                selPayStatus,
                {
                    xtype: "button",
                    text: "查询",
                    handler: function () {
                        var params = {};
                        params.orderNo = txtOrderNo.getValue();
                        params.serviceName = txtServiceName.getValue();
                        params.phone = txtPhone.getValue();
                        params.payType = selPayType.getValue();
                        if (txtDealBeginTime.getValue())
                            params.dealBeginTime = Ext.Date.format(new Date(txtDealBeginTime.getValue()), 'Y-m-d');
                        if (txtDealEndTime.getValue())
                            params.dealEndTime = Ext.Date.format(new Date(txtDealEndTime.getValue()), 'Y-m-d');
                        params.payNo = txtPayNo.getValue();
                        //params.payStatus = selPayStatus.getValue();
                        params.province = selProvince.getRawValue();
                        params.city = selCity.getRawValue();

                        store.load();

                        getOrderTotals(params);
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
//                {
//                    text: "导出",
//                    handler: function () {
//                        window.open("/report/exportOrderData");
//                    }
//                }
            ]
        });
        var store = Ext.create("Ext.data.Store", {
            pageSize: 20, //每页显示几条数据
            remoteSort: true,
            proxy: {
                type: 'ajax',
                url: '/report/orderData',
                reader: {
                    type: 'json',
                    totalProperty: 'total',
                    root: 'data',
                    idProperty: '#'
                }
            }, fields: [
                {name: 'order_no', type: 'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {name: 'merchant_name'},
                {name: 'merchant_phone'},
                {name: 'service_type_name'},
                {name: 'payment_price'},
                {name: 'user_name'},
                {name: 'order_pay_type'},
                {name: 'trade_no'},
//                {name: 'order_status'},
                {name: 'deal_time'},
                {name: 'id'},
                {name: 'province'},
                {name: 'city'}
            ]
        });
        store.on('beforeload', function (store, options) {
            var params = {};
            params.orderNo = txtOrderNo.getValue();
            params.serviceName = txtServiceName.getValue();
            params.phone = txtPhone.getValue();
            params.payType = selPayType.getValue();
            if (txtDealBeginTime.getValue())
                params.dealBeginTime = Ext.Date.format(new Date(txtDealBeginTime.getValue()), 'Y-m-d');
            if (txtDealEndTime.getValue())
                params.dealEndTime = Ext.Date.format(new Date(txtDealEndTime.getValue()), 'Y-m-d');
            params.payNo = txtPayNo.getValue();
            //params.payStatus = selPayStatus.getValue();
            params.province = selProvince.getRawValue();
            params.city = selCity.getRawValue();
            Ext.apply(store.proxy.extraParams, params);
        });

        Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {header: '序号', xtype: 'rownumberer', width: "20px"},
                {header: '订单号', dataIndex: 'order_no', width:"10%"},
                {header: '省份', dataIndex: 'province'},
                {header: '城市', dataIndex: 'city'},
                {header: '服务商', dataIndex: 'merchant_name'},
                {header: '手机号', dataIndex: 'merchant_phone'},
                {header: '服务项目', dataIndex: 'service_type_name'},
                {header: '金额', dataIndex: 'payment_price'},
                {header: '服务对象', dataIndex: 'user_name'},
                {
                    header: '支付方式', dataIndex: 'order_pay_type', renderer: function (val) {
                    if (val == 1) {
                        return "支付宝";
                    } else if (val == 2) {
                        return "微信";
                    } else if (val == 3) {
                        return "现金"
                    }
                }
                },
                {header: '关联支付单号', dataIndex: 'trade_no'},
//                {
//                    header: '支付状态', dataIndex: 'order_status', renderer: function (val) {
//                    switch (val) {
//                        case 1:
//                            return "新预约";
//                        case 2:
//                            return "待选择";
//                        case 3:
//                            return "已确认";
//                        case 4:
//                            return "已完成";
//                        case 5:
//                            return "支付完成";
//                        case 6:
//                            return "订单已过期";
//                        case 7:
//                            return "无效订单";
//                    }
//                }
//                },
                {header: '完成时间', dataIndex: 'deal_time', width:"8%"}
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
        getOrderTotals({});
    });
    function getOrderTotals(params) {
        Ext.Ajax.request({
            url: '/report/orderTotals',
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
