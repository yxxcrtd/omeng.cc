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
    var store = null;
    Ext.onReady(function () {
        var beginDate = Ext.create('Ext.form.field.Date', {
            fieldLabel: '时间'
        });
        var endDate = Ext.create('Ext.form.field.Date', {});
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            width: '100%',
            margin: "0 0 5 0",
            items: [
                beginDate,
                endDate,
                {
                    xtype : "button",
                    text: "查询",
                    handler: function () {
                        var params = {};

                        params.beginDate = beginDate.getValue();
                        params.endDate = endDate.getValue();

                        store.load({params:params});
                    }
                }
            ]
        });

//        var dInfo1 = Ext.create('Ext.form.field.Display', {
//            value: "<span style='color:red;font-weight: bold;'>提示：2016-7-2 系统对账不平，为避免数据错误，请及时检查数据</span>"
//        });
//        var dInfo2 = Ext.create('Ext.form.field.Display', {
//            value: "<span style='color:blue;font-weight: bold;'>提示：2016-7-3 系统对账平衡</span>"
//        });
//        Ext.create('Ext.toolbar.Toolbar', {
//            renderTo: document.body,
//            width: '100%',
//            margin: "0 0 5 0",
//            items: [
//                dInfo1
//            ]
//        });
//        Ext.create('Ext.toolbar.Toolbar', {
//            renderTo: document.body,
//            width: '100%',
//            margin: "0 0 5 0",
//            items: [
//                dInfo2
//            ]
//        });
        store = Ext.create("Ext.data.Store",{
            pageSize:20, //每页显示几条数据
            remoteSort: true,
            proxy:{
                type:'ajax',
                url:'/financial/statementWxData',
                reader:{
                    type:'json',
                    totalProperty:'total',
                    root:'data',
                    idProperty:'#'
                }
            },fields:[
                {name:'id',type:'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {name:'bill_date'},
                {name:'wx_num'},
                {name:'wx_amount'},
                {name:'omeng_num'},
                {name:'omeng_amount'},
                {name:'create_time'},
                {name:'result'},
                {name:'id'}
            ]
        });
        store.on('beforeload', function (store, options) {
            var params = {};

            params.beginDate = beginDate.getValue();
            params.endDate = endDate.getValue();
            Ext.apply(store.proxy.extraParams, params);
        });
        Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {header: '序号', xtype: 'rownumberer', width:'20px'},
                {header: '对账日期', dataIndex: 'bill_date', renderer:function(val){
                    if(val.length>10) val = val.substring(0, 10);
                    return val;
                }},
                {header: '微信对账笔数', dataIndex: 'wx_num'},
                {header: '微信对账金额', dataIndex: 'wx_amount'},
                {header: '平台对账笔数', dataIndex: 'omeng_num'},
                {header: '平台对账金额', dataIndex: 'omeng_amount'},
                {header: '对账完成时间', dataIndex: 'create_time'},
                {header: '对账结果', dataIndex: 'result', renderer:function(val){
                    if(val == 0)
                        return "<span style='color:red'>不平衡</span>";
                    else
                        return "平衡";
                }},
                {header: '操作', dataIndex: 'id',renderer:function(val,p, r){
                    var html = "<a href='javascript:;' onclick=\"re('"+r.data["bill_date"]+"')\">重新对账</a>";
                    html += "&nbsp;";
                    html += "<a href='javascript:;' onclick=\"showDetail('"+val+"','"+r.data["bill_date"]+"')\">查看明细</a>";

                    return html;
                }},
            ],
//            height: 200,
            width: '100%',
            forceFit:true, //自动填满表格
            bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
                displayInfo : true,
                store : store
            }),
            renderTo: Ext.getBody()
        });
        store.load({params:{start:0,limit:20}});
    });
    function re(day){
        day = day.substring(0, 10);
        Ext.Ajax.request({
            url: '/financial/reconciliationWx',
            params: {day:day},
            success: function (response) {
                var text = response.responseText;
                if(text == "OK"){
                    Ext.MessageBox.alert("提示", "重新对账成功", function(){
                        store.reload();
                    });

                }
            }
        });
    }
    function showDetail(id, day){
        parent.addTab("financial_statement_wx_detail"+id,"微信对账单详情","financial_statement_wx_detail","/financial/statementWxDetail?day="+day);
        //Ext.MessageBox.alert("info", id);
    }
</script>
<body>
</body>
</html>
