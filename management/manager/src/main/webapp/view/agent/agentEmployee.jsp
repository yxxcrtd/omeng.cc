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
<title>代理商员工信息管理</title>
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
                   {header:'员工ID',dataIndex:'id',sortable:true,fixed:false,hidden:true,align:'center'},
           		   {header:'省份',dataIndex:'provinceName',sortable:true,fixed:false,align:'center'},
                   {header:'城市',dataIndex:'cityName',sortable:true,fixed:false,align:'center'},
                   {header:'员工姓名',dataIndex:'name',sortable:true,fixed:false,align:'center'},
                   {header:'联系方式',dataIndex:'phone',sortable:true,fixed:false,align:'center'},
                   {header:'加入时间',dataIndex:'join_time',sortable:true,fixed:false,align:'center'},
                   {header:'邀请码',dataIndex:'invite_code',sortable:true,fixed:false,align:'center'},
                   {header:'商户数量',dataIndex:'merchant_total',align:'center',renderer:function(value,v,r){  
                	   var start_time=Ext.getCmp('start_time').getValue();
                	   var start_time1=r.data.join_time;
               		   var off_time=Ext.getCmp('off_time').getValue(); 
               		   var province=Ext.getCmp('province').getValue();
            		   var city=Ext.getCmp('city').getValue();
            		   var provinceDesc=Ext.getCmp('province').getRawValue();
            		   var cityDesc=Ext.getCmp('city').getRawValue();
                       return '<a href="javascript:showMerchants(\''+r.data.invite_code+'\',\''+start_time +'\',\''+off_time +'\',\''+province +'\',\''+city +'\',\''+provinceDesc +'\',\''+cityDesc +'\',\''+start_time1 +'\')"><span style="color:green;font-weight:bold";>'+value+'</span></a>';
             		}},
             		{header:'企业认证商户',dataIndex:'con_auth_total',align:'center',renderer:function(value,v,r){  
                        var auth_type='1';
                        var start_time=Ext.getCmp('start_time').getValue();
                        var start_time1=r.data.join_time;
                        var off_time=Ext.getCmp('off_time').getValue();
                		var province=Ext.getCmp('province').getValue();
                		var city=Ext.getCmp('city').getValue();
                		var provinceDesc=Ext.getCmp('province').getRawValue();
             		    var cityDesc=Ext.getCmp('city').getRawValue();
             			return '<a href="javascript:showAuthMerchants(\''+r.data.invite_code+'\',\''+auth_type +'\',\''+start_time +'\',\''+off_time +'\',\''+province +'\',\''+city  +'\',\''+provinceDesc +'\',\''+cityDesc +'\',\''+start_time1 +'\')"><span style="color:green;font-weight:bold";>'+value+'</span></a>';
              		}},
              		{header:'个人认证商户',dataIndex:'per_auth_total',align:'center',renderer:function(value,v,r){  
              			var auth_type='2';
              			var start_time=Ext.getCmp('start_time').getValue();
              			var start_time1=r.data.join_time;
              			var off_time=Ext.getCmp('off_time').getValue();
              			var province=Ext.getCmp('province').getValue();
              			var city=Ext.getCmp('city').getValue();
              			var provinceDesc=Ext.getCmp('province').getRawValue();
             		    var cityDesc=Ext.getCmp('city').getRawValue();
              			return '<a href="javascript:showAuthMerchants(\''+r.data.invite_code+'\',\''+auth_type+'\',\''+start_time +'\',\''+off_time +'\',\''+province +'\',\''+city +'\',\''+provinceDesc +'\',\''+cityDesc +'\',\''+start_time1 +'\')"><span style="color:green;font-weight:bold";>'+value+'</span></a>';
              		}}
               ];
  
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/agent/getAgentEmployeeList',  
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
	items:[
	        <cms:havePerm url='/agent/addAgentEmployee'>
			          {
				xtype : 'button',id : 'add',text : '添加',
				iconCls : 'NewAdd',
				listeners : {
					click : function() {
						showform();
					}
				}
			}, '-', </cms:havePerm>
	       <cms:havePerm url='/agent/deleteAgentEmployee'>
           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
	    	   listeners: {
	    		   click:function(){
	    			   delUser();
	    		   }
	    	   }},
           </cms:havePerm>
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
   
   
    <cms:havePerm url='/agent/editAgentEmployee'>
    grid.on("itemdblclick",function(grid, row){
    	editform();
    });</cms:havePerm>
	 
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	 //导出所有员工
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
		
        window.location.href = '/agent/exportAgentEmployeeExcel?name='+name+'&phone='+phone+'&province='+province
			+'&city='+city+'&invite_code='+invite_code+'&start_time='+start_time+'&off_time='+off_time
			+'&provinceDesc='+provinceDesc+'&cityDesc='+cityDesc;
	
	};
	
	//删除代理商员工
	function delUser()  
    {  
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
       	     Ext.example.msg("提示","请选择要删除的对象！");  
             return ;  
    	 }
         //ids：所有选中的用户Id的集合使用','隔开，初始化为空  
         var ids = '';  
         for(var i = 0;i<records.length;i++)  
         {  
            if(i>0){  
            	ids = ids+','+records[i].get('id');  
            }else{  
            	ids = ids+records[i].get('id');  
            }  
         }  
         Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/agent/deleteAgentEmployee", 
                     params:{ids:ids},  
                     method:'post',  
                     success:function(o){   
                	     Ext.example.msg("提示","删除成功！");  
                         store.reload();  
                         return ;  
                     },  
                     failure:function(form,action){  
                         Ext.example.msg("提示","删除失败！");  
                     }  
                 });    
             }  
         });  
    };
   
   
   //form表单
	var showform = function() {
		
		var add_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 440,
			height : 400,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [
			{
			xtype : 'combobox',
			name : 'province',
			fieldLabel : '省份',
			valueField : 'id',
			allowBlank	:false,
			editable:false,
			displayField : 'area',
			store : provinceStore,
			listeners : { // 监听该下拉列表的选择事件
					select : function(combobox,record,index) {
						add_winForm.form.findField('city').setValue('');
							cityStore.load({
									params : {
											parentId : combobox.value
											}
							});
											}
						 },
							queryMode : 'local'
				},
							
			  {xtype : 'combobox',
				   name : 'city',
				   fieldLabel : '城市',
				   valueField : 'id',
				   allowBlank	:false,
				   displayField : 'area',
				    store : cityStore,
				    editable:false,
				    queryMode : 'local'
				   
			},
			{
				// 输入姓名
				xtype : 'textfield',
				name : 'name',
				allowBlank	:false,
				fieldLabel : '姓名'
			},
			{
				// 输入联系方式
				xtype : 'textfield',
				name : 'phone',
				allowBlank	:false,
				fieldLabel : '联系方式'
			}, 
			  {
				// 输入邀请码
				xtype : 'textfield',
				name : 'invite_code',
				allowBlank	:false,
				fieldLabel : '邀请码'
			}
			 ]

		});
		 
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增员工",
			width : 450,
			height : 390,
			//height : 120,  
			//plain : true,  
			iconCls : "addicon",
			// 不可以随意改变大小  
			resizable : false,
			// 是否可以拖动  
			// draggable:false,  
			collapsible : true, // 允许缩放条  
			closeAction : 'close',
			closable : true,
			// 弹出模态窗体  
			modal : 'true',
			buttonAlign : "center",
			bodyStyle : "padding:0 0 0 0",
			items : [ add_winForm ],
			buttons : [ {
				text : "保存",
				minWidth : 70,
				handler : function() {
					if (add_winForm.getForm().isValid()) {
						
						add_winForm.getForm().submit({
							url : '/agent/addAgentEmployee',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',

							success : function(fp, o) {
								if (o.result.data == 1) {  
									  syswin.close(); //关闭窗口  
	                               	  Ext.example.msg("提示",o.result.message);
	                                  store.reload();  
	                                }else {  
	                              	  Ext.example.msg("提示",o.result.message);
	                                } 
								   
							},
							failure : function() {
							    Ext.example.msg("提示","保存失败！"); 
							}
						});
					}
				}
			}, {
				text : "关闭",
				minWidth : 70,
				handler : function() {
					syswin.close();
				}
			} ]
		});
		syswin.show();
	};
   
   
   //form表单
	var editform = function() {
		
		var edit_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 440,
			height : 400,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [
			{
				// 
				xtype : 'textfield',
				name : 'id',
				hidden:true
			},
			
			{
			xtype : 'combobox',
			name : 'province',
			fieldLabel : '省份',
			valueField : 'id',
			allowBlank	:false,
			displayField : 'area',
			store : provinceStore,
			listeners : { // 监听该下拉列表的选择事件
					select : function(
						combobox,
						record,
						index) {
						add_winForm.form.findField('city').setValue('');
							cityStore.load({
									params : {
											parentId : combobox.value
											}
											});
											}
										},
							queryMode : 'local',
							labelAlign : 'left'},
			{xtype : 'combobox',
				   name : 'city',
				   fieldLabel : '城市',
				   valueField : 'id',
					allowBlank	:false,
				   displayField : 'area',
				    store : cityStore,
				    queryMode : 'local',
				    labelAlign : 'left'
				   
			},
			{
				// 输入姓名
				xtype : 'textfield',
				name : 'name',
				allowBlank	:false,
				fieldLabel : '姓名'
			},
			{
				// 输入联系方式
				xtype : 'textfield',
				name : 'phone',
				allowBlank	:false,
				fieldLabel : '联系方式'
			}, 
			  {
				// 输入邀请码
				xtype : 'textfield',
				name : 'invite_code',
				allowBlank	:false,
				fieldLabel : '邀请码'
			}
			 ]

		});
		var rows = grid.getSelectionModel().getSelection();  
		 
		edit_winForm.form.findField('id').setValue(rows[0].get("id"));
		edit_winForm.form.findField('name').setValue(rows[0].get("name"));
		edit_winForm.form.findField('phone').setValue(rows[0].get("phone"));
		edit_winForm.form.findField('province').setValue(rows[0].raw.province);
		cityStore.load({
						params : {
							parentId : rows[0].raw.province
								 }
								});
		edit_winForm.form.findField('city').setValue(rows[0].raw.city);
		edit_winForm.form.findField('invite_code').setValue(rows[0].get("invite_code"));
		
		//创建window面板，表单面板是依托window面板显示的  
		var editwin = Ext.create('Ext.window.Window', {
			title : "编辑员工",
			width : 450,
			height : 390,
			//height : 120,  
			//plain : true,  
			iconCls : "addicon",
			// 不可以随意改变大小  
			resizable : false,
			// 是否可以拖动  
			// draggable:false,  
			collapsible : true, // 允许缩放条  
			closeAction : 'close',
			closable : true,
			// 弹出模态窗体  
			modal : 'true',
			buttonAlign : "center",
			bodyStyle : "padding:0 0 0 0",
			items : [ edit_winForm ],
			buttons : [ {
				text : "保存",
				minWidth : 70,
				handler : function() {
					if (edit_winForm.getForm().isValid()) {
						
						edit_winForm.getForm().submit({
							url : '/agent/editAgentEmployee',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',

							success : function(fp, o) {
								if (o.result.data == 1) {  
								  editwin.close(); //关闭窗口  
                               	  Ext.example.msg("提示",o.result.message);
                                  store.reload();  
                                }else {  
                              	  Ext.example.msg("提示",o.result.message);
                                }    
							},
							failure : function() {
							    Ext.example.msg("提示","保存失败！"); 
							}
						});
					}
				}
			}, {
				text : "关闭",
				minWidth : 70,
				handler : function() {
					editwin.close();
				}
			} ]
		});
		editwin.show();
	}; 
});

function showMerchants(invite_code,start_time,off_time,province,city,provinceDesc,cityDesc,start_time1){
	parent.removeTab("merchant_detail_"); 
	parent.addTab('merchant_detail_','服务商数量','','/merchants/showStore?invite_code='+invite_code+'&start_time='+start_time+'&off_time='+off_time+'&province='+province+'&city='+city+'&provinceDesc='+provinceDesc+'&cityDesc='+cityDesc+'&start_time1='+start_time1); 	 
}

function showAuthMerchants(invite_code,auth_type,start_time,off_time,province,city,provinceDesc,cityDesc,start_time1){
	
	  parent.removeTab("auth_detail_");
	  parent.addTab("auth_detail_","服务商认证数量","","/merchants/showStoreAudit?invite_code="+invite_code+"&auth_status=1&auth_type="+auth_type+"&start_time="+start_time+"&off_time="+off_time+'&province='+province+'&city='+city+'&provinceDesc='+provinceDesc+'&cityDesc='+cityDesc+'&start_time1='+start_time1); 	 
}	
</script>
</head>
<body>
</body>
</html>