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
<title>代金卷添加记录</title>
<script type="text/javascript">
//引入扩展组件
Ext.Loader.setConfig({
	enabled : true
});

Ext.Loader.setPath('Ext.ux', '../ExtJS4.2/ux/');

Ext.require([ 'Ext.data.*', 'Ext.grid.*', 'Ext.util.*',
		'Ext.tip.QuickTipManager']);
var store;
Ext.onReady(function() {
	var columns = [ {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
			{header : '编号',dataIndex : 'id',hidden:true,align:'center'},
			{header : '代金券类型',dataIndex : 'coupons_type_name',align:'center'}, 
			{header : '代金券金额',dataIndex : 'price',align:'center'}, 
		    {header : '截止时间',dataIndex : 'cutoff_time',align:'center'}, 
		    {header:'状态',dataIndex:'is_del',align:'center',renderer:function(value,v,r){  
                if(value=='0'){  
                return '<a href="javascript:StartOrStopVouchers(\''+r.data.id+'\','+value+')"><span style="color:green;font-weight:bold;">启用</span></a>';
                } else if(value=='1'){  
                return '<a href="javascript:StartOrStopVouchers(\''+r.data.id+'\','+value+')"><span style="color:red;font-weight:bold;">暂停</span></a>';
                }
    		}}
		];
	//创建store数据源

	//列表展示数据源
	store = Ext.create("Ext.data.Store", {
		pageSize : 20, //每页显示几条数据  
		remoteSort: true,
		proxy : {
			type : 'ajax',
			url : '/vouchers/getVouchers',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data',
				idProperty : '#'
			}
		},
		fields : [ {
			name : 'id'
		}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
		{
			name : 'coupons_type_name'
		}, {
			name : 'price'
		}, {
			name : 'cutoff_time'
		}, 
		{   name : 'is_del'
		}]
	});
	
	
	
	//点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var service_type_name = Ext.getCmp('service_type_name').getValue();
    	var is_del = Ext.getCmp('is_del').getValue();
		var price = Ext.getCmp('price').getValue();
		var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
        var new_params = {service_type_name : service_type_name,
        		start_time : start_time,off_time : off_time,
				price : price,is_del:is_del};    
        Ext.apply(store.proxy.extraParams, new_params);    
    });
	var sm = Ext.create('Ext.selection.CheckboxModel');
	var grid = Ext.create("Ext.grid.Panel", {
		region : 'center',
		border : false,
		store : store,
		selModel : sm,
		columns : columns,
		region : 'center', //框架中显示位置，单独运行可去掉此段
		loadMask : true, //显示遮罩和提示功能,即加载Loading……  
		forceFit : true, //自动填满表格  
		columnLines : false, //列的边框
		rowLines : true, //设置为false则取消行的框线样式
		dockedItems : [ {
			xtype : 'toolbar',
			dock : 'top',
			displayInfo : true,
			items : [ '-',
			          <cms:havePerm url='/vouchers/addVouchers'>
			          {
				xtype : 'button',id : 'add',text : '添加',
				iconCls : 'NewAdd',
				listeners : {
					click : function() {
						showform(null);
					}
				}
			}, '-', </cms:havePerm> 
			<cms:havePerm url='/vouchers/deleteVouchers'>
			{
				xtype : 'button',id : 'del',text : '删除',
				iconCls : 'NewDelete',
				listeners : {
					click : function() {
						delUserAll();
					}
				}
			},'-',</cms:havePerm> 
			<cms:havePerm url='/vouchers/exportExcel'>
 	       { xtype: 'button', text: '导出',iconCls:'Daochu',
 	    	   listeners: {
 	    		   click:function(){
 	    			   exportAll();
 	    		   }
 	    	   }
        }</cms:havePerm> 

			],
		},{
			xtype : 'toolbar',
			dock : 'top',
			displayInfo : true,
			items : [ {
				xtype : 'textfield',
				id : 'service_type_name',
				name : 'service_type_name',
				fieldLabel : '代金券类型',
				labelAlign : 'left',
				labelWidth : 70
			},'-', {
             	  //下拉列表框  
             	id : 'is_del',
                xtype: 'combobox', //9  
                fieldLabel: '状态',
                displayField: 'value',
                valueField : 'key',
                name:'is_del',
                store: Ext.create('Ext.data.Store', {  
                    fields: [  
                      {name:'key'},{ name: 'value' }  
                      ],  
                                      data: [  
                      { 'key':'0','value': '启动' },  
                      { 'key':'1','value': '暂停' } 
                      ]  
                }),
                labelWidth : 60
			},'-', {
				xtype : 'numberfield',
				id : 'price',
				name : 'price',
				fieldLabel : '金额',
				allowDecimals: false,
				minValue: 0,
				maxValue: 10000,
				labelAlign : 'left',
				labelWidth : 45
			},'-',
			
			]
		},	{
			xtype : 'toolbar',
			dock : 'top',
			displayInfo : true,
			items : [ 
			
			{
				xtype : 'datetimefield',
				id : 'start_time',
				name : 'start_time',
				format : 'Y-m-d',
				fieldLabel : '开始时间',
				labelAlign : 'left',
				labelWidth : 70
			},'-',
			
			{
				xtype : 'datetimefield',
				id : 'off_time',
				name : 'off_time',
				format : 'Y-m-d',
				fieldLabel : '结束时间',
				labelAlign : 'left',
				labelWidth : 60
			},'-',
			 <cms:havePerm url='/vouchers/getVouchers'>
			{
				xtype : 'button',
				text : '查询',
				id:'select',
				iconCls : 'Select',
				listeners : {
					click : function() {
						var service_type_name = Ext.getCmp('service_type_name').getValue();
						var price = Ext.getCmp('price').getValue();
						var is_del = Ext.getCmp('is_del').getValue();
						var start_time=Ext.getCmp('start_time').getValue();
						var off_time=Ext.getCmp('off_time').getValue();
						 store.currentPage = 1;
						store.load({
							params : {
								start:0,
								page : 1,
								limit : 20,
								service_type_name : service_type_name,
								start_time : start_time,
								off_time : off_time,
								price : price,
								is_del:is_del
							}
						});
					}
				}
			}</cms:havePerm> 
			]
		} ],
        bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
            displayInfo : true,
            store : store
        })
	});
	//加载数据  
	store.load({
		params : {
			start : 0,
			limit : 20
		}
	});


	 <cms:havePerm url='/vouchers/editVouchers'>
	    grid.on("itemdblclick",function(grid, row){
	    	showform(row.data);
	    });</cms:havePerm>
	
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight : true,
		border : false,
		items : [ grid ]
	});

	//代金券删除
	function delUserAll() {
		//grid中复选框被选中的项  

		var rows = grid.getSelectionModel().getSelection();
		//user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
		var id = '';
		for (var i = 0; i < rows.length; i++) {
			if (i > 0) {
				id = id + ',' + rows[i].get('id');
			} else {
				id = id + rows[i].get('id');
			}
		}
		//没有选择要执行操作的对象  

		if (id == "") {
			Ext.example.msg("提示","请选择要删除的对象！"); 
			return;
		} else {
			Ext.Msg.confirm("提示信息", "请确定要执行删除操作吗?", function(btn) {
				if (btn == 'yes') {
					Ext.Ajax.request({
						url : "/vouchers/deleteVouchers",
						params : {
							id : id
						},
						method : 'post',
						success : function(o) {
							Ext.example.msg("提示","代金券删除成功！"); 
							store.reload();
							return;
						},
						failure : function(form, action) {
							Ext.example.msg("提示","代金券删除失败！"); 
						}
					});
				}
			});
		}
	};
	 //导出所有代金券
	function exportAll() {
		var service_type_name = Ext.getCmp('service_type_name').getValue();
		var price = Ext.getCmp('price').getValue();
		var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
		window.location.href = '/vouchers/exportExcel?service_type_name='+service_type_name+'&price='+price+'&start_time='+start_time+'&off_time='+off_time;
	};
	//form表单
	var showform = function(d) {
		var pic = '';
 		var rows = grid.getSelectionModel().getSelection();
 		if(d!=null&&d.id!=null&&d.id!=0){
 			pic =rows[0].raw.icon_path;
 		}
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
			items : [  {
				// 
				xtype : 'textfield',
				name : 'id',
				hidden:true
			
			},{
				// 
				xtype : 'textfield',
				name : 'service_id',
				hidden:true
			
			},{
				// 
				xtype : 'textfield',
				name : 'app_type',
				hidden:true
			
			},{
                xtype: "triggerfield",
                name: "service_name",
                fieldLabel: "服务名称",
                editable:false,
                hideTrigger: false,
                allowBlank	:false,
                onTriggerClick: function () {
                    addService(add_winForm);
                   
                }
            }, {
				// 输入价格
				xtype : 'numberfield',
				name : 'price',
				allowDecimals: false,
				 minValue: 0,
				 maxValue: 10000,
				allowBlank	:false,
				fieldLabel : '价格'
			}, 
			{
				xtype : 'datetimefield',
				name : 'cutoff_time',
				format : 'Y-m-d',
				allowBlank	:false,
				fieldLabel : '截至时间'
			},{
      	      layout:'column',
       		  border:false,
              items:[{  
              layout:'form',
              anchor: '70%',
     		  border:false,
              columnWidth:.48,
              items:[{ 
                    fieldLabel:'上传图片',
                    name:'upload',
                    xtype:'fileuploadfield',  
                    anchor:'80%',  
                    emptyText:'请选择文件', 
                    regex:/(png)|(jpg)$/i,
                    buttonText: '选择',
                    selectOnFocus:true  
                }]
              },{  
                  layout:'form',
                  anchor: '30%',
         		  border:false,
                  columnWidth:.48,
                  items:[{xtype: 'displayfield',   
 	                    name: 'displayfield',  
 	                    value: '图片尺寸不得超过216*252px'
 	                    }]
	              }]
        },{
			xtype: 'hidden',
			name: "icon_path"
		},{  
    	    xtype: 'box', //或者xtype: 'component',  
    	    width: 300, //图片宽度  
    	    height: 200, //图片高度  
    	    autoEl: {  
    	        tag: 'img',  //指定为img标签  
    	        src: pic    //指定url路径  
    	    }  
    	}]

		});
		 

	    var title = '新增代金券';
	    var reqName = 'addVouchers';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		
	    		add_winForm.form.findField('id').setValue(rows[0].get("id"));
	    		add_winForm.form.findField('price').setValue(rows[0].get("price"));
	    		add_winForm.form.findField('service_name').setValue(rows[0].get("coupons_type_name"));
	    		add_winForm.form.findField('service_id').setValue(rows[0].raw.service_type_id);
	    		add_winForm.form.findField('app_type').setValue(rows[0].raw.app_type);
	    		add_winForm.form.findField('cutoff_time').setValue(rows[0].get("cutoff_time"));
	    		add_winForm.form.findField('icon_path').setValue(rows[0].raw.icon_path);
	        	title = '编辑代金券';
	        	reqName = 'editVouchers';
	    	}
	    

        //================判断是编辑还是新增===============
		
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增代金券",
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
							url : '/vouchers/'+reqName,
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',

							success : function(fp, o) {
								    Ext.example.msg("提示","保存成功！"); 
									syswin.close(); //关闭窗口  
									store.reload();
							
							},
							failure: function(fp, o) {
								 syswin.close();
                              	 Ext.example.msg("提示",o.response.responseText);
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
		
		 //***************************************添加个性服务  start*************************************
 	    
	 	   var addService = function(add_RecForm){  
				var columns = [ {header:'序号',xtype: 'rownumberer',width:50},
				                {header:'ID',dataIndex:'id',sortable:true,fixed:false},
				                {header:'服务名称',dataIndex:'service_type_name',sortable:true,fixed:false}
				];
			//创建store数据源

			var storeCatalogService = Ext.create("Ext.data.Store", {
				pageSize : 20, //每页显示几条数据  
				proxy : {
					type : 'ajax',
					url : '/systemManager/getServiceType',
					reader : {
						type : 'json',
						totalProperty : 'total',
						root : 'data',
						idProperty : '#'
					}
				},
				fields : [  {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
				            
				            {name:'service_type_name'} ]
			});
			
			
			//点击下一页时传递搜索框值到后台  
		    storeCatalogService.on('beforeload', function (store, options) {    
		    	 var date= gridCatalogService.dockedItems.items[1].items.items;
		    	 var service_type_name=date[0].rawValue;
				
		        var new_params = {service_type_name : service_type_name};    
		        Ext.apply(store.proxy.extraParams, new_params);    
		    });
		    var sm = Ext.create('Ext.selection.CheckboxModel');
		    var gridCatalogService = Ext.create("Ext.grid.Panel", {
				region : 'center',
				border : false,
				store : storeCatalogService,
				selModel: sm,
				columns : columns,
				region : 'center', //框架中显示位置，单独运行可去掉此段
				loadMask : true, //显示遮罩和提示功能,即加载Loading……  
				forceFit : true, //自动填满表格  
				columnLines : false, //列的边框
				rowLines : true, //设置为false则取消行的框线样式
				dockedItems : [ {
					xtype : 'toolbar',
					dock : 'top',
					displayInfo : true,
					items : [  { xtype: 'textfield',name: 'service_type_name',fieldLabel: '服务名称',labelAlign:'left',labelWidth:70},'-',

					    {
						xtype : 'button',
						text : '查询',
						iconCls : 'Select',
						listeners : {
							click : function() {
								var date= gridCatalogService.dockedItems.items[1].items.items;
						    	var service_type_name=date[0].rawValue;
						    	 storeCatalogService.currentPage = 1;
						    	 storeCatalogService.load({
		   							params : {
		   								start:0,
		   								page : 1,
		   								limit : 20,
		   								service_type_name : service_type_name
		   							}
		   						});
							}
						}
					} 
					]
				}, {
					xtype : 'pagingtoolbar',
					store : storeCatalogService, // GridPanel使用相同的数据源
					dock : 'bottom',
					displayInfo : true,
					plugins : Ext.create('Ext.ux.ProgressBarPager'),
					emptyMsg : "没有记录" //没有数据时显示信息
				} ]
			});

			//加载数据  
			storeCatalogService.load({
				params : {
					start : 0,
					limit : 20
				}
			});
			
			var issuewindow = Ext.create('Ext.window.Window',{  
	            title : "添加服务",  
	            width: 600,
	            height:500,  
	            //height : 120,  
	            //plain : true,  
	            iconCls : "addicon",  
	            // 不可以随意改变大小  
	            resizable : true,  
	            // 是否可以拖动  
	            draggable:true, 
	            autoScroll: true,
	           // collapsible : true, // 允许缩放条  
	            closeAction : 'close',  
	            closable : true,  
	            // 弹出模态窗体  
	            modal : 'true',  
	            buttonAlign : "center",  
	            bodyStyle : "padding:0 0 0 0",  
	            items : [gridCatalogService],  
	            buttons : [{  
	                       text : "确定",  
	                       minWidth : 70,  
	                       handler : function() {
	                    	 var rowsCata = gridCatalogService.getSelectionModel().getSelection();
	                    	 if(rowsCata.length <= 0){
	                   		     Ext.example.msg("提示","请选择要添加的服务！"); 
	                             return ;  
	                    	 }else if(rowsCata.length>1){
	                    		 Ext.example.msg("提示","请选择一个服务添加！"); 
	                             return ; 
	                    	 }
	                    	 add_RecForm.form.findField('service_id').setValue(rowsCata[0].get("id"));
	                    	 add_RecForm.form.findField('service_name').setValue(rowsCata[0].get("service_type_name"));
	                    	 add_RecForm.form.findField('app_type').setValue(rowsCata[0].raw.app_type);
	                    	 issuewindow.close(); 
	                       }   
	                  } ,{
					text : "取消",
					minWidth : 70,
					handler : function() {
						issuewindow.close();
					}
				}]                  		
	         });   
			    issuewindow.show();  
			    };
		    
	 	    
	 	//**********************************添加个性服务end**********************************************************    
	 	    
	};


	
	
	
});
var StartOrStopVouchers=function(id,is_del){

    		var sucessmessage;
    		var errormessage;
    		var message;
    		var edit_del;
    		if(is_del==0){
    			edit_del=1;
    			sucessmessage='代金券已暂停！';
    			errormessage='代金券暂停失败！';
    			message='请确定要执行暂停操作吗?'
    		}else if(is_del==1){
    			edit_del=0;
    			sucessmessage='代金券已启用！';
    			errormessage='代金券启用失败！'
    			message='请确定要执行启用操作吗?'
    		}
    		Ext.Msg.confirm("提示信息", message, function(btn) {
				if (btn == 'yes') {
					Ext.Ajax.request({
						url : "/vouchers/startOrstopVouchers",
						params : {
							id : id,
							is_del : edit_del
						},
						method : 'post',
						success : function(o) {
						    Ext.example.msg("提示",sucessmessage); 
							store.reload();
							return;
						},
						failure : function(form, action) {
						    Ext.example.msg("提示",errormessage); 
						}
					});
				}
			});
};
</script>
</head>
<body>
</body>
</html>