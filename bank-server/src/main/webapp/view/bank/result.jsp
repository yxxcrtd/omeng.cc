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
	var dInfo;
	var allTotal;
    Ext.onReady(function () {
        var txtName = Ext.create('Ext.form.field.Text', {
            fieldLabel: '姓名',labelWidth : 30
        });
        var txtPhone = Ext.create('Ext.form.field.Text', {
            fieldLabel: '手机号',labelAlign :'right'
        });
        
        var statusStore = Ext.create('Ext.data.Store', {
            fields: ['id', 'text'],
            data: [
                {"id": "40", "text": "全部"},
                {"id": "12", "text": "受理中"},
                {"id": "20", "text": "成功"},
                {"id": "30", "text": "失败"},
            ]
        });
        
        var statusType = Ext.create('Ext.form.ComboBox', {
            fieldLabel: '状态',
            labelAlign :'right',
            value: "40",
            store: statusStore,
            queryMode: 'local',
            editable: false,
            displayField: 'text',
            valueField: 'id',
            renderTo: Ext.getBody()
        });
        
        var txtStartTime = Ext.create('Ext.form.field.Date', {
            fieldLabel: '申请时间',labelAlign :'right'
        });
        var txtEndTime = Ext.create('Ext.form.field.Date');
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            width: '100%',
            margin: "0 0 5 0",
            items: [
                txtName,
                txtPhone,
                statusType,
                txtStartTime,
                txtEndTime,
                
                {
                    xtype : "button",
                    text: "查询",
                    handler: function () {
                    	var params = {};
                        params.name = txtName.getValue();
                        params.phone = txtPhone.getValue();
                        params.status = statusType.getValue();
                        params.startTime = formatDate(txtStartTime.getValue());
                        params.endTime = formatDate(txtEndTime.getValue());
                        store.load({params: params});
						getWithdrawTotals(params);
                    }
                },
                
                {
                    xtype : "button",
                    text: "导出",
                    handler: function () {
                    	
                    	var params = {};
                        params.name = txtName.getValue();
                        params.phone = txtPhone.getValue();
                        params.status = statusType.getValue();
                        params.startTime = formatDate(txtStartTime.getValue());
                        params.endTime = formatDate(txtEndTime.getValue());
						
						Ext.Ajax.request({
				            url: '/bank/withdrawTotals',
				            params: params,
				            success: function (response) {
				                var text = response.responseText;
				                var obj = JSON.parse(text);
				                var totalNumber = obj.totalNumber;
				                
				                if(totalNumber<1){
		                    		Ext.MessageBox.alert("提示", "没有查询到数据无法导出");
		                    		return false;
		                    	}
		                    	if(totalNumber>10000){
		                    		Ext.MessageBox.alert("提示", "一次性导出数据不能超过10000条,您正在导出"+totalNumber+"条提现记录");
		                    		return false;
		                    	}
		                        var name = txtName.getValue();
		                        var phone = txtPhone.getValue();
		                        var status = statusType.getValue();
		                        var startTime = formatDate(txtStartTime.getValue());
		                        var endTime = formatDate(txtEndTime.getValue());
		                        location.href="/bank/exportWithdrawal?name="+name+"&phone="+phone+"&status="+status+"&startTime="+startTime+"&endTime="+endTime;
				                
				            }
				        });
                    }
                }
            ]
        });

		dInfo = Ext.create('Ext.form.field.Display', {
            value: "提现笔数：0笔，提现总额：0元"
        });
        
        Ext.create('Ext.toolbar.Toolbar', {
            renderTo: document.body,
            width: '100%',
            margin: "0 0 5 0",
            items: [
                dInfo
            ]
        });
        
        
        
        var store = Ext.create("Ext.data.Store",{
            pageSize:20, //每页显示几条数据
            remoteSort: true,
            proxy:{
                type:'ajax',
                url:'/bank/findAllApplyRecodList',
                reader:{
                    type:'json',
                    totalProperty:'total',
                    root:'data',
                    idProperty:'#'
                }
            },fields:[
                {name:'userName',type:'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
                {name:'phone'},
                {name:'money'},
                {name:'bankCode'},
                {name:'bankName'},
                {name:'createTime'},
                {name:'statusName'},
                {name:'bankAccountName'},
                {name:'statusName'},
                {name:'messageInfo'},
                {name:'endTime'},
                {name:'operateUser'},
                {name:'withdrawBankBatchNo'}
                
            ]
        });
        
        
      //点击下一页时传递搜索框值到后台  
        store.on('beforeload', function (store, options) {    
    		 var params = {};
             params.name = txtName.getValue();
             params.phone = txtPhone.getValue();
             params.status = statusType.getValue();
             params.startTime = formatDate(txtStartTime.getValue());
             params.endTime = formatDate(txtEndTime.getValue());
             Ext.apply(store.proxy.extraParams, params);    
        });
        
        var grid = Ext.create('Ext.grid.Panel', {
            store: store,
            columns: [
                {header: '序号', xtype: 'rownumberer', width:'20px',dataIndex: 'id'},
                {header: '姓名', dataIndex: 'userName'},
                {header: '手机号', dataIndex: 'phone'},
                {header: '提现金额', dataIndex: 'money'},
                {header: '提现银行', dataIndex: 'bankName'},
                {header: '银行账户名', dataIndex: 'bankAccountName'},
                {header: '银行卡号', dataIndex: 'bankCode'},
                {header: '申请时间', dataIndex: 'createTime'},
                {header: '状态', dataIndex: 'statusName',renderer:function(val, p, r){
                    if(val == "成功"){
                        return "<span style='color:green;'>成功</span>";
                    }else if(val == "失败"){
                    	return "<span style='color:red;'>失败</span>";
                    }
                    else{
                    	return val;
                    }
                }},
                {header: '说明', dataIndex: 'messageInfo'},
                {header: '完成时间', dataIndex: 'endTime'},
                {header: '经办人', dataIndex: 'operateUser'},
                {header: '批次号', dataIndex: 'withdrawBankBatchNo'}
            ],
//            height: 200,
            width: '100%',
            forceFit : true, //自动填满表格
            bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
                displayInfo : true,
                store : store
            }),
            renderTo: Ext.getBody()
        });
        store.load({params:{start:0,limit:20,status:40}});
		getWithdrawTotals({status:40});
    });
    
    function getWithdrawTotals(params) {
        Ext.Ajax.request({
            url: '/bank/withdrawTotals',
            params: params,
            success: function (response) {
                var text = response.responseText;
                var obj = JSON.parse(text);
                var totalNumber = obj.totalNumber;
                var totalAmount = obj.totalAmount;
                var mesage = "提现笔数："+totalNumber+"笔，提现金额:"+totalAmount+"元";
                allTotal = totalNumber;
                dInfo.setValue(mesage);
            }
        });
    }
    
    function formatDate (date) {
    	if(date!=null && date!=""){
    		var y = date.getFullYear();  
            var m = date.getMonth() + 1;  
            m = m < 10 ? '0' + m : m;  
            var d = date.getDate();  
            d = d < 10 ? ('0' + d) : d;  
            return y + '-' + m + '-' + d;
    	}
    	return date;
          
    };
    
</script>
<body>
</body>
</html>
