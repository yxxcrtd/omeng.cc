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
<title>代理商员工考核</title>
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
                   {header:'序号',xtype: 'rownumberer',width:50},
                   {header:'员工ID',dataIndex:'id',sortable:true,fixed:false,hidden:true},
           		   {header:'省份',dataIndex:'provinceName',sortable:true,fixed:false},
                   {header:'城市',dataIndex:'cityName',sortable:true,fixed:false},
                   {header:'员工姓名',dataIndex:'name',sortable:true,fixed:false},
                   {header:'联系方式',dataIndex:'phone',sortable:true,fixed:false},
                   {header:'加入时间',dataIndex:'join_time',sortable:true,fixed:false},
                   {header:'邀请码',dataIndex:'invite_code',sortable:true,fixed:false},
                   {header:'商户数量',dataIndex:'merchant_total',sortable:true,fixed:false},
                   {header:'企业认证商户',dataIndex:'con_auth_total',sortable:true,fixed:false},
                   {header:'个人认证商户',dataIndex:'per_auth_total',sortable:true,fixed:false}
               ];
  
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/agent/getCheckAgentEmployeeList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'provinceName'},  
           {name:'cityName'}, 
           {name:'name'},  
           {name:'join_time'}, 
           {name:'phone'}, 
           {name:'invite_code'},
           {name:'merchant_total'},
           {name:'per_auth_total'},
           {name:'con_auth_total'}
           
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
		
		
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var name=Ext.getCmp('name').getValue();
		var phone=Ext.getCmp('phone').getValue();
		var province=Ext.getCmp('province').getValue();
		var city=Ext.getCmp('city').getValue();
		 var provinceDesc=Ext.getCmp('province').getRawValue();
		 var cityDesc=Ext.getCmp('city').getRawValue();
		var invite_code=Ext.getCmp('invite_code').getValue();
		var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
        var new_params = { name:name,phone : phone,province:province,city:city,invite_code:invite_code,start_time:start_time,off_time:off_time,provinceDesc:provinceDesc,cityDesc:cityDesc};    
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
        dockedItems: [
    {
	xtype:'toolbar',
	dock:'top',
	displayInfo: true,
	items:['-',
            <cms:havePerm url='/agent/exportAgentEmployeeExcel'>
		        	       { xtype: 'button', text: '导出',iconCls:'Daochu',
		            	    	   listeners: {
		            	    		   click:function(){
		            	    			   exportAll();
		            	    		   }
		            	    	   }
		                   }</cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'name',name: 'name',fieldLabel: '姓名',labelAlign:'left',labelWidth:70},'-',
        	       { xtype: 'textfield',id:'phone',name: 'phone',fieldLabel: '联系方式',labelAlign:'left',labelWidth:65},'-',
        	       { xtype: 'datetimefield',id: 'start_time',name: 'start_time',fieldLabel: '开始时间',format : 'Y-m-d',labelAlign:'left',labelWidth:65},'-',
        	       { xtype: 'datetimefield',id: 'off_time',name: 'off_time',fieldLabel: '结束时间',format : 'Y-m-d',labelAlign:'left',labelWidth:65},'-',
				   
        	       
				   ]},
				   {
        	     xtype:'toolbar',
        	     dock:'top',
        	     displayInfo: true,
        	items:[	
                 { xtype: 'textfield',id:'invite_code',name: 'invite_code',fieldLabel: '邀请码',labelAlign:'left',labelWidth:70},'-',
        	     {xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省份',value:'',valueField : 'id',displayField : 'area',
							store : provinceStore,
							listeners : { // 监听该下拉列表的选择事件
																select : function(
																		combobox,
																		record,
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
							queryMode : 'local',labelAlign : 'left',labelWidth : 65},'-',
				   {xtype : 'combobox',id : 'city',name : 'city',fieldLabel : '城市',value:'',valueField : 'id',displayField : 'area',
							store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 65},'-',		
				  
				   <cms:havePerm url='/agent/getAgentEmployeeList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			  var name=Ext.getCmp('name').getValue();
		                  var phone=Ext.getCmp('phone').getValue();
		                  var province=Ext.getCmp('province').getValue();
		                  var city=Ext.getCmp('city').getValue();
		                  var provinceDesc=Ext.getCmp('province').getRawValue();
		         		  var cityDesc=Ext.getCmp('city').getRawValue();
		                  var invite_code=Ext.getCmp('invite_code').getValue();
		                  var start_time=Ext.getCmp('start_time').getValue();
		          		  var off_time=Ext.getCmp('off_time').getValue();
    	    			  store.currentPage = 1;
    	    			  store.load({params:{start:0,page:1,limit:20,name:name,phone : phone,province:province,city:city,invite_code:invite_code,start_time:start_time,off_time:off_time,provinceDesc:provinceDesc,cityDesc:cityDesc}}); 
    	    		   }
    	    		   }}
				   </cms:havePerm>
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
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
   
   
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	 //导出所有员工的考核信息
	function exportAll() {
		var name=Ext.getCmp('name').getValue();
        var phone=Ext.getCmp('phone').getValue();
        var province=Ext.getCmp('province').getValue();
        var city=Ext.getCmp('city').getValue();
        var provinceDesc=Ext.getCmp('province').getRawValue();
		var cityDesc=Ext.getCmp('city').getRawValue();
        var invite_code=Ext.getCmp('invite_code').getValue();
        var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
		
        window.location.href = '/agent/exportCheckAgentEmployeeExcel?name='+name+'&phone='+phone+'&province='+province
			+'&city='+city+'&invite_code='+invite_code+'&start_time='+start_time+'&off_time='+off_time
			+'&provinceDesc='+provinceDesc+'&cityDesc='+cityDesc;
	
	};
	
});
 
</script>
</head>
<body>
</body>
</html>