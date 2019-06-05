<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp"%>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setHeader("Expires","0");

	request.setCharacterEncoding("UTF-8");	
	
	//String webRoot = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>服务商业务统计</title>
<script type="text/javascript">
//引入扩展组件
Ext.Loader.setConfig({enabled: true});

Ext.Loader.setPath('Ext.ux', '../ExtJS4.2/ux/');

Ext.require([
    'Ext.data.*',
    'Ext.grid.*',
    'Ext.util.*',
    'Ext.tip.QuickTipManager'
]);

Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'订单编号',dataIndex:'order_no',align:'center'},
                   {header:'省份',dataIndex:'province',align:'center'},
                   {header:'城市',dataIndex:'city',align:'center'},
                   {header:'服务项目',dataIndex:'orderTypeName',align:'center'},
                   {header:'下单时间',dataIndex:'join_time',align:'center'},
                   {header:'订单状态',dataIndex:'orderStatusName',align:'center'},
                   {header:'近7日推送商家数',dataIndex:'push_count',align:'center'},
                   {header:'抢单数',dataIndex:'grab_count',align:'center'},
                   {header:'合作服务商',dataIndex:'name',align:'center'},
                   {header:'首次抢单时间',dataIndex:'first_time',align:'center'},
                   {header:'选定服务商时间',dataIndex:'confirm_time',align:'center'},
                   {header:'服务完成时间',dataIndex:'finish_time',align:'center'},
                   {header:'付款完成时间',dataIndex:'deal_time',align:'center'},
                   {header:'用户关闭时间',dataIndex:'cacel_time',align:'center'},
                   {header:'订单过期时间',dataIndex:'over_time',align:'center'},
                   {header:'付款方式',dataIndex:'order_pay_type',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>支付宝支付</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>微信支付</span>";  
                       }else if(value=='3'){  
                           return "<span style='color:green;font-weight:bold';>现金支付</span>";  
                       }else if(value=='4'){  
                           return "<span style='color:green;font-weight:bold';>免单</span>";  
                       }else {  
                           return "<span style='color:green;font-weight:bold';></span>";  
                       }
           	       }}, 
           	       {header:'删除状态',dataIndex:'is_del',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:green;font-weight:bold';>未删除</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>已删除</span>";  
                       }
           	       }}
               ];
	
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/analysis/getOrderProcerStatis',
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'},
           {name:'order_no'},   
           {name:'province'}, 
           {name:'city'}, 
           {name:'orderTypeName'}, 
           {name:'join_time'}, 
           {name:'orderStatusName'}, 
           {name:'push_count'}, 
           {name:'grab_count'}, 
           {name:'name'}, 
           {name:'first_time'}, 
           {name:'confirm_time'}, 
           {name:'finish_time'}, 
           {name:'deal_time'}, 
           {name:'cacel_time'}, 
           {name:'over_time'}, 
           {name:'order_pay_type'}, 
           {name:'is_del'}
        ]  
    });
    
 // 省信息
	var provinceStore = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/showArea',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'id'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'area'
		} ]
	});
	// 市信息
    var cityStore = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/showServiveCity',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'id'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'area'
		} ]
	});
    
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var province=Ext.getCmp('province').getValue();
    	 var city=Ext.getCmp('city').getRawValue();
    	 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
		 var order_no=Ext.getCmp('order_no').getValue();
         var new_params = {order_no:order_no,province:province,city:city,start_time : start_time,off_time : off_time};    
        Ext.apply(store.proxy.extraParams, new_params);    
    });
    var sm = Ext.create('Ext.selection.CheckboxModel');
    var grid = Ext.create("Ext.grid.Panel",{
    	region: 'center',
    	border: false,
    	store: store,
    	selModel: sm,
        columns: columns,
        region: 'center', //框架中显示位置，单独运行可去掉此段
        loadMask:true, //显示遮罩和提示功能,即加载Loading……  
        forceFit:true, //自动填满表格  
        columnLines:false, //列的边框
        rowLines:true, //设置为false则取消行的框线样式
        dockedItems: [{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	       '-',
        	       <cms:havePerm url='/analysis/exportOrderProcerExcel'>
        	       { xtype: 'button',id:'export', text: '导出',iconCls:'Daochu',
        	    	   listeners: {
        	    		   click:function(){
        	    			   exportAll();
        	    		   }
        	    	   }
               },</cms:havePerm> 
               
                 ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{xtype : 'textfield',id : 'order_no',name : 'order_no',fieldLabel : '订单号',labelAlign : 'left',labelWidth : 60},'-',
			{xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省份',valueField : 'id',displayField : 'area',
				store : provinceStore,
				listeners : { // 监听该下拉列表的选择事件
									select : function(combobox,record,
															index) {
														Ext
																.getCmp(
																		'city')
																.setValue(
																		'');
														cityStore
																.load({
																	params : {
																		parentId : combobox.value
																	}
																});
													}
												},
				queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
	   {xtype : 'combobox',id : 'city',name : 'city',fieldLabel : '城市',valueField : 'id',displayField : 'area',
				store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-', 
				{ xtype: 'datetimefield',id: 'start_time',name: 'start_time',value:'${start_time}',format : 'Y-m-d',fieldLabel: '加入时间',labelAlign:'left',labelWidth:60},'~',
        	    { xtype: 'datetimefield',id: 'off_time',name: 'off_time',format : 'Y-m-d',labelAlign:'left',labelWidth:60},'-',
        	       <cms:havePerm url='/analysis/getOrderProcerStatis'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var province=Ext.getCmp('province').getRawValue();
    	    		       var city=Ext.getCmp('city').getRawValue();
    	    			   var start_time=Ext.getCmp('start_time').getValue();
    	    			   var off_time=Ext.getCmp('off_time').getValue();
    	    			   var order_no=Ext.getCmp('order_no').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,start:0,order_no:order_no,province:province,city:city,start_time : start_time,off_time : off_time}}); 
    	    		   }
    	    		   }}</cms:havePerm>
                   ]
        }],
        bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
            displayInfo : true,
            store : store
        })
    });
  //加载数据  
    store.load({params:{start:0,limit:20}}); 
    provinceStore.load(); 
    // 表格配置结束
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    grid.on("itemdblclick",function(grid, row){
    	//Ext.Msg.alert("系统提示","你双击啦！ID为："+row.data.id);  
    });
    
    function sleepTime(){
   	 Ext.getCmp('export').setDisabled(true);
   	 setTimeout(function(){
   		Ext.getCmp('export').setDisabled(false);
   	}, 30000);//js定时器
     } 
  //导出所有订单
	function exportAll() {
		var province=Ext.getCmp('province').getRawValue();
	    var city=Ext.getCmp('city').getRawValue();
		var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
		 var order_no=Ext.getCmp('order_no').getValue();
		window.location.href = '/analysis/exportOrderProcerExcel?province='+province+'&city='+city+'&order_no='+order_no
		         +'&start_time='+start_time+'&off_time='+off_time;
		
		Ext.example.msg("提示","正在导出报表，请稍后！");
        sleepTime();
	};
	
	
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	    
});
</script>

</head>
<body>
</body>
</html>