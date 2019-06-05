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
    Ext.onReady(function () {
        var txtName = Ext.create('Ext.form.field.Text', {
            fieldLabel: '姓名',labelWidth : 30
        });
        var txtPhone = Ext.create('Ext.form.field.Text', {
            fieldLabel: '手机号',labelAlign :'right'
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
                txtStartTime,
                txtEndTime,
                {
                    xtype : "button",
                    text: "查询",
                    handler: function () {
                    	var params = {};
                        params.name = txtName.getValue();
                        params.phone = txtPhone.getValue();
                        params.startTime = formatDate(txtStartTime.getValue());
                        params.endTime = formatDate(txtEndTime.getValue());
                        store.load({params: params});
                    	getWithdrawTotals(params);
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
                {
                    text: "提交",
                    handler : function(){
                        var idArray = new Array();
                        var sels = grid.getSelectionModel().getSelection();
                        var totalMoney = 0.0;
                        for(var i=0;i<sels.length;i++){
                            idArray.push(sels[i].getId());
                            totalMoney = Number((totalMoney+sels[i].data.money).toFixed(2));
                        }
                        
                        var selectedLen = idArray.length;
                        
                        if(selectedLen<1){
                        	Ext.MessageBox.alert("提示", "请至少选择一条待审核的提现记录！");
                        	return false;
                        }
                        else if(selectedLen>20){
                        	Ext.MessageBox.alert("提示", "每个批次号处理的提现记录不能超过20条");
                        	return false;
                        }
                        else{
                        	Ext.MessageBox.confirm("确认","本批次提现笔数为:"+selectedLen+"笔，本批次提现金额为:"+totalMoney+"元，确定要向银行发出提现申请吗?",function(btn){
                        		if(btn == 'yes'){
                        			Ext.Ajax.request({
                                        url: '/bank/batchTransfer',
                                        params: {ids:idArray.toString()},
                                        success: function (response) {
                                            var text = response.responseText;
                                            var obj = JSON.parse(text);
                                            var batchNo = obj.batchNo;
                                            var custName = obj.custName;
                                            var date = obj.date;
                                            var remarks = obj.remarks;
                                            var mesage="";
                                            if(batchNo!=null &&batchNo!=null&&custName!=null&&date!=null&&remarks!=null){
                                            	mesage = "批次号:"+batchNo+",经办人:"+custName+",日期:"+date+",描述信息:"+remarks;
                                            }
                                            else{
                                            	mesage = text;
                                            }
                                            Ext.MessageBox.alert("提示", mesage,function(){
                                            	window.location.reload(); 
                                            });
                                        }
                                    });
                        		}
                        	});
                        }
                    }
                    
                },
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
                {name:'operateUser'}
            ]
        });
        
      //点击下一页时传递搜索框值到后台  
        store.on('beforeload', function (store, options) {    
    		 var params = {};
             params.name = txtName.getValue();
             params.phone = txtPhone.getValue();
             params.startTime = formatDate(txtStartTime.getValue());
             params.endTime = formatDate(txtEndTime.getValue());
             Ext.apply(store.proxy.extraParams, params);    
        });
        
        //创建多选框
        var checkBox = Ext.create('Ext.selection.CheckboxModel',{
        	checkOnly:true
        });
        var grid = Ext.create('Ext.grid.Panel', {
            store: store,
            selModel : checkBox,
            columns: [
                {header: '序号', xtype: 'rownumberer', width:'20px',dataIndex: 'id'},
                {header: '姓名', dataIndex: 'userName'},
                {header: '手机号', dataIndex: 'phone'},
                {header: '提现金额', dataIndex: 'money'},
                {header: '提现银行', dataIndex: 'bankName'},
                {header: '银行账户名', dataIndex: 'bankAccountName'},
                {header: '银行卡号', dataIndex: 'bankCode'},
                {header: '申请时间', dataIndex: 'createTime'},
                {header: '状态', dataIndex: 'statusName'},
                {header: '说明', dataIndex: 'messageInfo'},
                {header: '经办人', dataIndex: 'operateUser'}
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
        store.load({params:{start:0,limit:20,status:10}});
        getWithdrawTotals({});
    });
    
    function getWithdrawTotals(params) {
        Ext.Ajax.request({
            url: '/bank/withdrawTotals',
            noCache: false,//设置为false 则不会向后台传参 _dc
            params: params,
            success: function (response) {
 				var text = response.responseText;
                var obj = JSON.parse(text);
                var totalNumber = obj.totalNumber;
                var totalAmount = obj.totalAmount;
                var mesage = "提现笔数："+totalNumber+"笔，提现金额:"+totalAmount+"元";
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
