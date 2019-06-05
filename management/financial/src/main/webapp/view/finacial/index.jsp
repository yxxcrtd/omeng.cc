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
    Ext.onReady(function () {
        var txtName = Ext.create('Ext.form.field.Text', {
            fieldLabel: '姓名'
        });
        var txtPhone = Ext.create('Ext.form.field.Text', {
            fieldLabel: '手机号'
        });
        var beginDate = Ext.create('Ext.form.field.Date', {
            fieldLabel: '申请时间'
        });
        var endDate = Ext.create('Ext.form.field.Date', {});
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            width: '100%',
            margin: "0 0 5 0",
            items: [
                txtName,
                txtPhone,
                beginDate,
                endDate,
                {
                    xtype : "button",
                    text: "查询",
                    handler: function () {
                        store.load({params:{name:txtName.getValue(),phone:txtPhone.getValue(),beginDate:beginDate.getValue(), endDate:endDate.getValue()}});
                    }
                }
            ]
        });
//        Ext.create('Ext.toolbar.Toolbar', {
//            renderTo: document.body,
//            width: '100%',
//            margin: "0 0 5 0",
//            items: [
//                {
//                    text: "导出",
//                    handler : function(){
//                        window.open("/financial/exportWithdrawal");
//                    }
//                }
//            ]
//        });
        var store = Ext.create("Ext.data.Store",{
            pageSize:20, //每页显示几条数据
            remoteSort: true,
            proxy:{
                type:'ajax',
                url:'/financial/withdrawalData',
                reader:{
                    type:'json',
                    totalProperty:'total',
                    root:'data',
                    idProperty:'#'
                }
            },fields:[
                {name:'name',type:'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {name:'telephone'},
                {name:'withdraw_price'},
                {name:'withdraw_no'},
                {name:'withdraw_bank'},
                {name:'withdraw_time'},
                {name:'withdraw_status'},
                {name:'audit_name'},
                {name:'id'}
            ]
        });
        Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {header: '序号', xtype: 'rownumberer', width:'20px'},
                {header: '姓名', dataIndex: 'name'},
                {header: '手机号', dataIndex: 'telephone'},
                {header: '提现金额', dataIndex: 'withdraw_price'},
                {header: '提现银行', dataIndex: 'withdraw_bank'},
                {header: '银行账户名', dataIndex: 'name'},
                {header: '银行卡号', dataIndex: 'withdraw_no'},
                {header: '申请时间', dataIndex: 'withdraw_time'},
                {header: '状态', dataIndex: 'withdraw_status',renderer:function(val, p, r){
                    if(val == "0"){
                        return "<span style='color:red;'>失败</span>";
                    }else if(val == "1")
                        return "<span style='color:green;'>成功</span>";
                    else if(val == "2")
                        return "提取中";
                }},
                {header: '完成时间', dataIndex: 'phone'},
                {header: '经办人', dataIndex: 'audit_name'},
                {header: '操作', dataIndex: 'id',renderer:function(val,p, r){
                    return "<a href='javascript:;' onclick=\"showDetail('"+val+"')\">详情</a>"
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

    function showDetail(id){
        //Ext.MessageBox.alert("info", id);
    }
</script>
<body>
</body>
</html>
