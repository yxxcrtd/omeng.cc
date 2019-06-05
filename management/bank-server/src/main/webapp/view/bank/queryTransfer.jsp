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
    <link href="${CONTEXT_PATH}/view/ExtJS4.2/resources/css/ext-all-neptune-rtl.css" rel="stylesheet">
    <link href="${CONTEXT_PATH}/view/ExtJS4.2/css/icon.css" rel="stylesheet">
    <link href="${CONTEXT_PATH}/view/css/msgTip.css" rel="stylesheet">
    <link href="${CONTEXT_PATH}/view/css/icon.css" rel="stylesheet">
    <script src="${CONTEXT_PATH}/view/ExtJS4.2/ext-all.js"></script>
    <script src="${CONTEXT_PATH}/view/ExtJS4.2/ux/data/PagingMemoryProxy.js"></script>
    <script src="${CONTEXT_PATH}/view/ExtJS4.2/ux/ProgressBarPager.js"></script>
    <script src="${CONTEXT_PATH}/view/ExtJS4.2/locale/ext-lang-zh_CN.js"></script>
    <script type="text/javascript" src="${CONTEXT_PATH}/view/js/msgTip.js"></script>
    <script type="text/javascript" src="${CONTEXT_PATH}/view/js/localXHR.js"></script>
    <link href="${CONTEXT_PATH}/view/date/select.css" rel="stylesheet" type="text/css"/>
</head>
<script type="text/javascript">
    Ext.onReady(function () {
        var batchNo = Ext.create('Ext.form.field.Text', {
            fieldLabel: '批次号'
        });
        var entryDate = Ext.create('Ext.form.field.Date', {
            fieldLabel: '操作时间'
        });
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            width: '100%',
            margin: "0 0 5 0",
            items: [
                batchNo,
                entryDate,
                {
                    xtype : "button",
                    text: "查询",
                    handler: function () {
                        store.load({params:{batchNo:batchNo.getValue(),entryDate:entryDate.getValue()}});
                    }
                }
            ]
        });
        
        var store = Ext.create("Ext.data.Store",{
            remoteSort: true,
            proxy:{
                type:'ajax',
                url:'/bank/queryBatchTransferResult',
                reader:{
                    type:'json',
                    totalProperty:'total',
                    root:'list',
                    idProperty:'#'
                }
            },fields:[
                {name:'userName',type:'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {name:'bankCode'},
                {name:'money'},
                {name:'message'},
                {name:'status'}
            ]
        });
        //创建多选框
        var grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {header: '序号', xtype: 'rownumberer', width:'20px',dataIndex: 'id'},
                {header: '姓名', dataIndex: 'userName'},
                {header: '银行卡号', dataIndex: 'bankCode'},
                {header: '提现金额', dataIndex: 'money'},
                {header: '银行返回信息', dataIndex: 'message'},
                {header: '提现结果', dataIndex: 'status',renderer:function(val, p, r){
                    if(val == "0"){
                        return "<span style='color:red;'>失败</span>";
                    }else{
                        return "<span style='color:green;'>成功</span>";
                    }
                }}
            ],
            width: '100%',
            forceFit : true, //自动填满表格
            renderTo: Ext.getBody()
        });
        
        
    });
    
</script>
<body>
</body>
</html>
