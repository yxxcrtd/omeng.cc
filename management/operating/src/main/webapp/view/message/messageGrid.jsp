<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp"%>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setHeader("Expires","0");

	request.setCharacterEncoding("UTF-8");	
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>消息中心</title>
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
var store;
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,hidden:true,align:'center'},
                   {header:'用户类型',dataIndex:'customer_type',sortable:true,fixed:false,align:'center',width:50,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>商户版</span>";  
                       } else{  
                           return "<span style='color:red;font-weight:bold';>用户版</span>";  
                       }
           		   }},
                   {header:'省',dataIndex:'province',width:40,sortable:true,fixed:false,align:'center'},
                   {header:'市',dataIndex:'city',width:40,sortable:true,fixed:false,align:'center'},
                  // {header:'app类型',dataIndex:'app_type',sortable:true,fixed:false},
                   {header:'app类型',dataIndex:'app_name',width:50,sortable:true,fixed:false,align:'center'},
                  //{header:'VIP消息',dataIndex:'is_vip',sortable:true,fixed:false},
                   {header:'标题',dataIndex:'title',width:80,sortable:true,fixed:false,align:'center'},
                   {header:'内容',dataIndex:'content',width:120,sortable:true,fixed:false,align:'center'},
                   {header:'发送时间',dataIndex:'send_time',width:60,sortable:true,fixed:false,align:'center'},
                   {header:'创建时间',dataIndex:'join_time',width:60,sortable:true,fixed:false,align:'center'},
                   {header:'状态',dataIndex:'is_use',sortable:true,fixed:false,width:50,align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>已发送</span>";  
                       } else{  
                           return "<span style='color:red;font-weight:bold';>未发送</span>";  
                       }
           		   }},
                 //{header:'',dataIndex:'is_push',sortable:true,fixed:false},
                 //{header:'app类型',dataIndex:'is_sendSms',sortable:true,fixed:false},
                   {header:'通知方式',dataIndex:'send_type',sortable:true,fixed:false,align:'center',width:50,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:blue;font-weight:bold';>推送通知</span>";  
                       }else if(value=='2'){  
                           return "<span style='color:red;font-weight:bold';>短信通知</span>";  
                       }else{
                    	   return "<span style='color:green;font-weight:bold';>不通知</span>";   
                       }
           		   }},
                   {header:'接受群组',dataIndex:'push_type',sortable:true,fixed:false,align:'center',width:50,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:blue;font-weight:bold';>区域用户</span>";  
                       }else if(value=='2'){  
                           return "<span style='color:red;font-weight:bold';>指定用户</span>";  
                       }else{
                    	   return "<span style='color:green;font-weight:bold';>全部用户</span>";   
                       }
           		   }},
                   {
                       xtype:'actioncolumn', 
                       dataIndex:'send',
                       width:60,
                       text: '发送消息',
                       align:'center',
                       header:'发送消息',                
                       items: [{
                    	   iconCls:'Fabu',
                    	   text: '发送消息',
                    	   align:'center', 
                           handler: function(grid, rowIndex, colIndex) {
                        	   var record = grid.getStore().getAt(rowIndex); 
                       		   var id = record.get('id');
                       		   var is_use = record.get('is_use');
            				   sendMessage(id,is_use);
                           }
                 }]}
               ];
    
     store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/message/messageList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'customer_type'},  
           {name:'province'},  
           {name:'city'},
           {name:'app_type'},  
           {name:'app_name'},  
           {name:'is_vip'},
           {name:'title'},  
           {name:'content'},  
           {name:'send_time'},
           {name:'join_time'},  
           {name:'is_use'},  
           {name:'send_type'},
           {name:'push_type'}
        ]  
    });
    //  app类型
	var store_appType = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
		remoteSort: true, //设置属性进行请求后台排序
		proxy : {
			type : 'ajax',
			url : '/common/showAllCatalog',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'app_type'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'app_name'
		} ]
	});
    
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
		 var app_type=Ext.getCmp('app_type').getValue();
         var new_params = {app_type : app_type};    
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
	    	   <cms:havePerm url='/message/addMessage'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform(null);
		    		   }
		       }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/message/deleteMessage'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		    	}}
			   </cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	       {
				    xtype : 'combobox',
				    id : 'app_type',
				    name : 'app_type',
				    fieldLabel : 'app类型',
				    valueField : 'app_type',
				    displayField : 'app_name',
				    store : store_appType,
				    queryMode : 'local',
				    labelAlign : 'left',
				    labelWidth : 60
			       },'-',
        	       <cms:havePerm url='/message/messageList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var app_type=Ext.getCmp('app_type').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,app_type : app_type}}); 
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
    store_appType.load({params:{package_type:1}});
    
    <cms:havePerm url='/message/editMessage'>
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data); 
    });</cms:havePerm>
    
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
	
  
	

	//删除
	function deleteRecord()  
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
                     url:"/message/deleteMessage",  
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
    } ;
      

    
	// 地区信息
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
	
	//1用户端，2商户端
    var customerTypeComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[
    	      ['商户端','1'],
    	      ['用户端','2']
    	]
    });
	
	//0草稿未发送，1已发送
    var isUseComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['未发送','0'],
    	      ['已发送','1']
    	]
    });

	//通知方式  0不通知，1推送通知，2短信通知
    var sendTypeComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['不通知','0'],
    	      ['推送通知','1'],
    	      ['短信通知','2']
    	]
    });
	
	//接受群组 0全部用户，1区域用户，2指定用户
    var pushTypeComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['全部用户','0'],
    	      ['区域用户','1'],
    	      ['指定用户','2']
    	]
    });
	
	//VIP消息 0否，1是
    var isVipComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['否','0'],
    	      ['是','1']
    	]
    });
    
  
	
	//form表单编辑用户
	var showform=function(d){
	    var edit_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 750,
	                height:500,
	                bodyPadding:5,  
	                //renderTo:"panel21",  
	                fieldDefaults: {  
	                    labelAlign: 'left',  
	                    labelWidth: 90,  
	                    anchor: '100%'  
	                },  
	                items: [ {
                               xtype: 'hidden',
                               name: "id"
                           }, {
    			              layout:'column',
    			       		  border:false,
    			              items:[{    
    			                  layout:'form',
    			                  anchor: '95%',
    			                  columnWidth:.48,
    			         		  border:false,
    			                  items:[
                                        {
                                           xtype : 'combobox',
                                           name : 'customer_type',
  	                                       fieldLabel : '用户类型',
                                           valueField : 'value',
                                           displayField : 'name',
	                                       value:'1',
                                           store : customerTypeComboStore,
                                           editable:false,
                                           listeners : { // 监听该下拉列表的选择事件
	                                       select : function(combobox,record,index) {
		                                       Ext.getCmp('app_type').setValue('');
		                                       store_appType.load({params : {package_type : combobox.value}});
	                                       } },
                                           queryMode : 'local'
                                           // labelAlign : 'left',
                                           // labelWidth : 60
                                         },
                                         {
                                          xtype : 'combobox',
                                          name : 'app_type',
                                          fieldLabel : 'app类型',
                                          valueField : 'app_type',
                                          displayField : 'app_name',
                                          store : store_appType,
                                          queryMode : 'local',
	                                      value:'',
                                          editable:false
                                          // labelAlign : 'left',
                                          // labelWidth : 60
                                         },{
     			    						xtype:'combo',
    			    						store : sendTypeComboStore,
    			    			    		name:'send_type',
    			    			   			triggerAction: 'all',
    			    			   			displayField: 'name',
    			    			   			valueField: 'value',
    			    			   			hiddenName:'send_type',
    			    	                    typeAhead: true,
    			    	                    mode: 'remote',
    			    			   			value:'0',
    			    					   	editable: false,
    			    						fieldLabel: '通知方式'  
    			    					   }]
    			              },{
    			                  layout:'form',
    			                  anchor: '97.5%',
    			         		  border:false,
    			                  columnWidth:.48,
    			                  items:[{
			    						xtype:'combo',
			    						store : isUseComboStore,
			    			    		name:'is_use',
			    			   			triggerAction: 'all',
			    			   			displayField: 'name',
			    			   			valueField: 'value',
			    			   			hiddenName:'is_use',
			    	                    typeAhead: true,
			    	                    mode: 'remote',
			    			   			value:'0',
			    					   	editable: false,
			    						fieldLabel: '消息状态'  
			    					   },{
			    							xtype : 'datetimefield',
			    							name : 'send_time',
			    							format : 'Y-m-d',
			    							anchor: '97.5%',
			    							fieldLabel : '定时发送',
			    							labelAlign : 'left'
			    						}]            
    			           } ]
    			 },{  
		                    //  消息标题 
		                    xtype: 'textfield', 
		                    anchor: '97.5%',
		                    name: 'title',  
		                    fieldLabel: '标题 '  
		                  }, {  
			                    //  消息内容 
			                    xtype: 'textareafield', 
			                    anchor: '97.5%',
			                    name: 'content',  
			                    fieldLabel: '内容 '  
			              },{
    			              layout:'column',
    			       		  border:false,
    			              items:[{    
    			                  layout:'form',
    			                  anchor: '95%',
    			                  columnWidth:.48,
    			         		  border:false,
    			                  items:[{
			    						xtype:'combo',
			    						store : pushTypeComboStore,
			    			    		name:'push_type',
			    			   			triggerAction: 'all',
			    			   			displayField: 'name',
			    			   			valueField: 'value',
			    			   			hiddenName:'push_type',
			    	                    typeAhead: true,
			    	                    mode: 'remote',
			    			   			value:'0',
			    					   	editable: false,
			    						fieldLabel: '接受群组',
			    						listeners : { // 监听该下拉列表的选择事件
											select : function(combobox,record,index) {
												var v = combobox.value;
												if(v==0){
													// 全部用户
													edit_winForm.form.findField('is_vip').show();
													edit_winForm.form.findField('province').hide();
													edit_winForm.form.findField('city').hide();
													edit_winForm.form.findField('users').hide();
													Ext.getCmp("addUser").hide();
													//edit_winForm.form.findField('addUser').hide();
												}else if(v==1){
													// 区域用户
													edit_winForm.form.findField('is_vip').show();
													edit_winForm.form.findField('province').show();
													edit_winForm.form.findField('city').show();
													edit_winForm.form.findField('users').hide();
													Ext.getCmp("addUser").hide();
													//edit_winForm.form.findField('addUser').hide();
												}else if(v==2){
													// 指定用户
													edit_winForm.form.findField('is_vip').hide();
													edit_winForm.form.findField('province').hide();
													edit_winForm.form.findField('city').hide();
													edit_winForm.form.findField('users').show();
													Ext.getCmp("addUser").show();
													//edit_winForm.form.findField('addUser').show();
												}
											}
										},
										queryMode : 'local'  
			    					   },{
										xtype : 'combobox',
										name : 'province',
										fieldLabel : '省',
										valueField : 'id',
										hiddenName:'id',
										editable:false,
										displayField : 'area',
										store : provinceStore,
										listeners : { // 监听该下拉列表的选择事件
											select : function(combobox,record,index) {
												edit_winForm.form.findField('city').setValue('');
												cityStore.load({
															params : {
																parentId : combobox.value
															}
														});
											}
										},
										queryMode : 'local',
										labelAlign : 'left',
									    anchor: '97.5%'
									}]
    			              },{
    			                  layout:'form',
    			                  anchor: '97.5%',
    			         		  border:false,
    			                  columnWidth:.48,
    			                  items:[{
			    						xtype:'combo',
			    						store : isVipComboStore,
			    			    		name:'is_vip',
			    			   			triggerAction: 'all',
			    			   			displayField: 'name',
			    			   			valueField: 'value',
			    			   			hiddenName:'is_vip',
			    	                    typeAhead: true,
			    	                    mode: 'remote',
			    			   			value:'0',
			    					   	editable: false,
			    						fieldLabel: 'VIP消息'  
			    					   },{
        			                  layout:'form',
        			                  anchor: '97.5%',
        			         		  border:false,
        			                  columnWidth:.48,
        			                  items:[{
												xtype : 'combobox',
												name : 'city',
											    anchor: '97.5%',
												fieldLabel : '市',
												valueField : 'id',
        			    			   			hiddenName:'id',
												displayField : 'area',
												store : cityStore,
												editable:false,
												queryMode : 'local',
												labelAlign : 'left'
											}]            
        			           }]            
    			           } ]
    			           },{
      			              layout:'column',
      			       		  border:false,
      			              items:[{    
      			                  layout:'form',
      			                  anchor: '80%',
      			                  columnWidth:.6,
      			         		  border:false,
      			                  items:[{
    								xtype:'textarea',
    								readOnly:true,
    								anchor:'80%',
    								fieldLabel: '具体用户',
    								name: 'users',
    								hidden:true,
    								height:100}]
      			              },{
      			                  layout:'form',
      			                  anchor: '90%',
      			         		  border:false,
      			                  columnWidth:.3,
      			                  items:[{
    								xtype:'button',
    								columnWidth:.5,
    								anchor:'25%',
    								text : '添加用户',
    								id:'addUser',
    								name: 'addUser',
    								handler :this.addUser,
    								iconCls: 'Select',
    								hidden:true,
    								scope : this}]            
      			           } ]
      			           }
	                    ]  
	            });  

	    var title = '新增消息';
	    var reqName = 'addMessage';
  		provinceStore.load();
	    //================判断是编辑还是新增===============
	    if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    		cityStore.load({params : {parentId : d.province}});
	    	    edit_winForm.form.findField('id').setValue(d.id);
	       	    edit_winForm.form.findField('customer_type').setValue(d.customer_type+''); 
	       	    var appType = '';
	       	    if(d.app_type!=null&&d.app_type!=''){
	       	    	appType = d.app_type;
	       	    }
	    	    edit_winForm.form.findField('app_type').setValue(appType);  
	    	    edit_winForm.form.findField('is_use').setValue(d.is_use+''); 
	    	    edit_winForm.form.findField('send_time').setValue(d.send_time); 
	    	    edit_winForm.form.findField('send_type').setValue(d.send_type+'');
	    	    edit_winForm.form.findField('title').setValue(d.title);
	    	    edit_winForm.form.findField('content').setValue(d.content);
	    	    edit_winForm.form.findField('push_type').setValue(d.push_type+'');
	    	    edit_winForm.form.findField('province').setValue(d.province+'');
	    	    edit_winForm.form.findField('city').setValue(d.city+''); 
	    	    edit_winForm.form.findField('is_vip').setValue(d.is_vip+'');
	        	title = '编辑消息';
	        	reqName = 'editMessage';
	    	}
	    var pt = edit_winForm.form.findField('push_type').getValue();
	    if(pt==0){
			// 全部用户
			edit_winForm.form.findField('is_vip').show();
			edit_winForm.form.findField('province').hide();
			edit_winForm.form.findField('city').hide();
			edit_winForm.form.findField('users').hide();
			Ext.getCmp("addUser").hide();
			//edit_winForm.form.findField('addUser').hide();
		}else if(pt==1){
			// 区域用户
			edit_winForm.form.findField('is_vip').show();
			edit_winForm.form.findField('province').show();
			edit_winForm.form.findField('city').show();
			edit_winForm.form.findField('users').hide();
			Ext.getCmp("addUser").hide();
			//edit_winForm.form.findField('addUser').hide();
		}else if(pt==2){
			// 指定用户
			edit_winForm.form.findField('is_vip').hide();
			edit_winForm.form.findField('province').hide();
			edit_winForm.form.findField('city').hide();
			edit_winForm.form.findField('users').show();
			Ext.getCmp("addUser").show();
			//edit_winForm.form.findField('addUser').show();
		}
	    
        //================判断是编辑还是新增===============
	    //创建window面板，表单面板是依托window面板显示的  
	    var editwindow = Ext.create('Ext.window.Window',{  
	              title : title,  
	              width: 750,
	              height:500,  
	              //height : 120,  
	              //plain : true,  
	              iconCls : "addicon",  
	              // 不可以随意改变大小  
	              resizable : true,  
	              // 是否可以拖动  
	              draggable:true,  
	              collapsible : true, // 允许缩放条  
	              closeAction : 'close',  
	              closable : true,  
	              // 弹出模态窗体  
	              modal : 'true',  
	              buttonAlign : "center",  
	              bodyStyle : "padding:0 0 0 0",  
	              items : [edit_winForm],  
	              buttons : [{  
	                         text : "保存",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            if (edit_winForm.getForm().isValid()) {
	                            	edit_winForm.getForm().submit({  
	                                          url :'/message/'+reqName,  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
	                                            
	                                          success: function(fp, o) {  
	                                                  if (o.result== true) {
	 	                                        		 Ext.example.msg("提示","保存成功！");
	 	                                        		 editwindow.close(); //关闭窗口  
	 	                                                 store.reload();  
	 	                                              }else { 
	 	                                        		 Ext.example.msg("提示","保存失败！");
	 	                                              }  
	                                          },  
	                                          failure: function() {  
	                                     		 Ext.example.msg("提示","保存失败！");
	                                          }  
	                                       });  
	                            }  
	                         }  
	                     }, {  
	                         text : "关闭",  
	                         minWidth : 70,  
	                         handler : function() {  
	                        	 editwindow.close();  
	                         }  
	                     }]  
	           });  
	    editwindow.show();  
	    };
	
});

function sendMessage(msgId,status){
	var tips='';
	if(status=='1'){
		// 消息已经发送过
		tips='本消息已发送过，确定重复发送这条信息吗？';
	}else{
		//未发送过
		tips='确定手动发送这条信息吗？';
	}
	
    var msg = Ext.MessageBox.confirm('提示信息',tips, function(btn, text){
    if (btn == 'no'){
           return;
        }else{
           Ext.Ajax.request({
        	   url:"/message/sendMessage",  
               params:{id:msgId},  
               method:'post', 
               waitTitle: '请稍等...',  
               waitMsg: '正在提交信息...', 
               success: function(response) {  
            	   var v = response.responseText;
                   if (v =='true') {
              		   Ext.example.msg("提示","消息发送成功！");
                       store.reload();  
                    }else { 
              		   Ext.example.msg("提示","消息发送失败！");
                    }  
              },  
              failure: function() {  
      		         Ext.example.msg("提示","消息发送失败！");
           } 
           });
        }
        });
}

</script>
</head>
<body>
</body>
</html>