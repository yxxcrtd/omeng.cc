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
<title>订单管理</title>
<script type="text/javascript" src="http://pmg.oomeng.cn/manager/js/order.js"></script>
<script type="text/javascript">
	//引入扩展组件
	Ext.Loader.setConfig({
		enabled : true
	});

	Ext.Loader.setPath('Ext.ux', '../ExtJS4.2/ux/');

	Ext.require([ 'Ext.data.*', 'Ext.grid.*', 'Ext.util.*',
			'Ext.tip.QuickTipManager' ]);

Ext.onReady(function() {
 var columns = [ {header:'序号',xtype: 'rownumberer',width:50}, 
				 {header : 'ID',dataIndex : 'id',align:'center',hidden: true},
				{header : '订单号',dataIndex : 'order_no',align:'center'},
				{header : '会员账号',dataIndex : 'phone',align:'center'},
				{header : '下单时间',dataIndex : 'join_time',align:'center'},
				{header : '订单状态',dataIndex : 'orderStatusName',align:'center'}, 
				{header : '服务名称',dataIndex : 'orderTypeName',align:'center'},
				{header:'状态',dataIndex:'is_del',align:'center',renderer:function(value){  
                    if(value=='0'){  
                        return "<span style='color:red;font-weight:bold';>未删除</span>";  
                    } else if(value=='1'){  
                        return "<span style='color:green;font-weight:bold';>已删除</span>";  
                    }
        	       }},
				{header:'省份',dataIndex:'province',sortable:true,fixed:false,align:'center'},
                {header:'城市',dataIndex:'city',sortable:true,fixed:false,align:'center'},
                {header : '地址',dataIndex : 'address',hidden:true,align:'center'},
                {header : '抢单服务商',dataIndex : 'merchants_count',align:'center',renderer:function(value,v,r){  
                    return '<a href="javascript:showMerchants(\''+r.data.id+'\',\''+r.raw.merchant_id+'\')"><span style="color:red;font-weight:bold";>查看</span></a>';
                   
        		}},
        		{header : '近7日推送服务商',dataIndex : 'merchants_count',align:'center',renderer:function(value,v,r){  
                    return '<a href="javascript:showPushMerchants(\''+r.data.id+'\',\''+r.raw.merchant_id+'\')"><span style="color:red;font-weight:bold";>查看</span></a>';
                   
        		}},
        		{header : '合作服务商',dataIndex : 'merchant_name',align:'center'},
        		{header : '完成时间',dataIndex : 'deal_time',align:'center'},
        		{header : '付款方式',dataIndex : 'order_pay_type',align:'center',renderer:function(value){  
                    if(value=='1'){  
                        return "<span style='color:green;font-weight:bold';>支付宝支付</span>";  
                    } else if(value=='2'){  
                        return "<span style='color:green;font-weight:bold';>微信支付</span>";  
                    }else if(value=='3'){  
                        return "<span style='color:green;font-weight:bold';>现金支付</span>";  
                    }else if(value=='4'){  
                        return "<span style='color:green;font-weight:bold';>免单</span>";  
                    }
        	       }},
        		{header : '订单金额',dataIndex : 'order_price',align:'center'},
        		{header : '实际金额',dataIndex : 'order_actual_price',align:'center'},
         		{header:'详细',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                      return '<a href="javascript:showOrderDetail(\''+r.data.id+'\',\''+r.raw.service_type_id+'\')">详细</a>';
            		}}
            	
				];
				// 创建store数据源
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
				// 列表展示数据源
				var store = Ext.create("Ext.data.Store", {
					pageSize : 20, // 每页显示几条数据
					remoteSort: true,
					proxy : {
						type : 'ajax',
						url : '/order/getOrderList',
						reader : {
							type : 'json',
							totalProperty : 'total',
							root : 'data',
							idProperty : '#'
						}
					},
					fields : [ {
						name : 'id'
					}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
					
					{name : 'phone'}, 
					{name : 'join_time'},
					{name : 'orderStatusName'},
					{name : 'orderTypeName'},
					{name : 'merchants_count'},
					{name : 'is_del'},
					{name : 'province'},
					{name : 'city'},
					{name : 'order_no'},
					{name : 'merchant_name'},
					{name : 'deal_time'},
					{name : 'order_pay_type'},
					{name : 'order_price'},
					{name : 'address'},
					{name : 'order_actual_price'}
					]
				});
				
				// 订单状态
				var store_purchaseStatus = Ext.create("Ext.data.Store", {
					pageSize : 20, // 每页显示几条数据
					proxy : {
						type : 'ajax',
						url : '/common/showUserOrderStatus',
						reader : {
							type : 'json',
							totalProperty : 'total',
							root : 'data'
						}
					},
					fields : [ {
						name : 'dict_key'
					}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
					{
						name : 'dict_value'
					} ]
				});
				// app类型
				var store_appType = Ext.create("Ext.data.Store", {
					pageSize : 20, // 每页显示几条数据
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
				// 服务类型
				var store_ServiceType = Ext.create("Ext.data.Store", {
					pageSize : 20, // 每页显示几条数据
					proxy : {
						type : 'ajax',
						url : '/common/showServiceType',
						reader : {
							type : 'json',
							totalProperty : 'total',
							root : 'data'
						}
					},
					fields : [ {
						name : 'service_type'
					}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
					{
						name : 'service_name'
					} ]
				});
				var payTypeStore = new Ext.data.SimpleStore({
			     	fields:['name','type'],
			     	data:[['全部',''],
			     	      ['支付宝支付','1'],
			     	      ['微信支付','2'],
			     	      ['现金支付','3'],
			     	      ['免单','4']
			     	]
			     });
				//点击下一页时传递搜索框值到后台  
		        store.on('beforeload', function (store, options) {    
		        	var order_id = Ext.getCmp('order_id').getValue();
					var phone = Ext.getCmp('phone').getValue();
					var merchant_phone = Ext.getCmp('merchant_phone').getValue();
					var purchase_status = Ext.getCmp('purchase_status').getValue();
					var service_type_name = Ext.getCmp('service_type_name').getValue();
					var start_time = Ext.getCmp('start_time').getValue();
					var off_time= Ext.getCmp('off_time').getValue();
					var del_start_time = Ext.getCmp('del_start_time').getValue();
					var del_off_time= Ext.getCmp('del_off_time').getValue();
					var order_pay_type= Ext.getCmp('order_pay_type').getValue();
					var merchant_name= Ext.getCmp('merchant_name').getValue();
					var province=Ext.getCmp('province').getRawValue();
                    var city=Ext.getCmp('city').getRawValue();
		            var agentId='';
		            var new_params = { order_id : order_id,
		            		phone : phone,
		            		merchant_phone:merchant_phone,
							purchase_status : purchase_status,
							start_time : start_time,
			        		off_time : off_time,
			        		service_type_name : service_type_name,
							province:province,
							city:city,
							del_start_time:del_start_time,
							del_off_time:del_off_time,
							order_pay_type:order_pay_type,
							merchant_name:merchant_name,
							agentId:agentId
							};    
		            Ext.apply(store.proxy.extraParams, new_params);    
		        });  
		        var sm = Ext.create('Ext.selection.CheckboxModel');
				var grid = Ext
						.create(
								"Ext.grid.Panel",
								{
									region : 'center',
									border : false,
									store : store,
							    	selModel: sm,
									columns : columns,
									region : 'center', // 框架中显示位置，单独运行可去掉此段
									loadMask : true, // 显示遮罩和提示功能,即加载Loading……
									forceFit : true, // 自动填满表格
									columnLines : false, // 列的边框
									rowLines : true, // 设置为false则取消行的框线样式
									dockedItems : [											{
										xtype : 'toolbar',
										dock : 'top',
										displayInfo : true,
										items : [ '-',
										
										<cms:havePerm url='/order/deleteOrder'>
										{
											xtype : 'button',
											id : 'del',
											text : '删除',
											iconCls : 'NewDelete',
											listeners : {
												click : function() {
													delUserAll();
												}
											}
										},'-',</cms:havePerm>
										<cms:havePerm url='/order/exportExcel'>
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
												items : [
														{
															xtype : 'textfield',
															id : 'order_id',
															name : 'order_id',
															fieldLabel : '订单号',
															labelAlign : 'left',
															labelWidth : 60
														},'-',
														{
															xtype : 'textfield',
															id : 'phone',
															name : 'phone',
															fieldLabel : '会员账号',
															labelAlign : 'left',
															labelWidth : 60
														},'-',
														
														{ xtype: 'textfield',id:'service_type_name',name: 'service_type_name',fieldLabel: '服务名称',labelAlign:'left',labelWidth:60},'-',
														{ xtype: 'textfield',id:'merchant_name',name: 'merchant_name',fieldLabel: '服务商名称',labelAlign:'left',labelWidth:70},'-',
														{ xtype: 'textfield',id:'merchant_phone',name: 'merchant_phone',fieldLabel: '服务商号码',labelAlign:'left',labelWidth:70},'-',
														
															]
											},	{
												xtype : 'toolbar',
												dock : 'top',
												displayInfo : true,
												items : [ {xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省份',valueField : 'id',displayField : 'area',
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
											{xtype : 'combobox',id : 'purchase_status',name : 'purchase_status',fieldLabel : '订单状态',valueField : 'dict_key',displayField : 'dict_value',
														editable:false,store : store_purchaseStatus,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
											{xtype : 'combobox',id : 'order_pay_type',name : 'order_pay_type',fieldLabel : '付款方式',valueField : 'type',displayField : 'name',
															editable:false,store : payTypeStore,queryMode : 'local',labelAlign : 'left',labelWidth : 70},'-',
														
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
													value:'${start_time}',
													fieldLabel : '下单时间',
													labelAlign : 'left',
													labelWidth : 60
												},'~',
												{
													xtype : 'datetimefield',
													id : 'off_time',
													name : 'off_time',
													format : 'Y-m-d',
													labelAlign : 'left',
													labelWidth : 60
												},'-',
												{
													xtype : 'datetimefield',
													id : 'del_start_time',
													name : 'del_start_time',
													format : 'Y-m-d',
													fieldLabel : '完成时间',
													labelAlign : 'left',
													labelWidth : 60
												},'~',
												{
													xtype : 'datetimefield',
													id : 'del_off_time',
													name : 'del_off_time',
													format : 'Y-m-d',
													labelAlign : 'left',
													labelWidth : 60
												},'-',
												
												  <cms:havePerm url='/order/getOrderList'>
												{
													xtype : 'button',
													id : 'select',
													text : '查询',
													iconCls : 'Select',
													listeners : {
														click : function() {
															var order_id = Ext.getCmp('order_id').getValue();
															var phone = Ext.getCmp('phone').getValue();
															var merchant_phone = Ext.getCmp('merchant_phone').getValue();
															var purchase_status = Ext.getCmp('purchase_status').getValue();
															var service_type_name = Ext.getCmp('service_type_name').getValue();
															var province=Ext.getCmp('province').getRawValue();
		                                                    var city=Ext.getCmp('city').getRawValue();
		                                                    var agentId='';
															var start_time = Ext.getCmp('start_time').getValue();
															var off_time = Ext.getCmp('off_time').getValue();
															var del_start_time = Ext.getCmp('del_start_time').getValue();
															var del_off_time= Ext.getCmp('del_off_time').getValue();
															var order_pay_type= Ext.getCmp('order_pay_type').getValue();
															var merchant_name= Ext.getCmp('merchant_name').getValue();
															store.currentPage = 1;
															store
																	.load({
																		params : {
																			start:0,
																			page : 1,
																			limit : 20,
																			order_id : order_id,
																			phone : phone,
																			merchant_phone:merchant_phone,
																			purchase_status : purchase_status,
																			start_time : start_time,
																			off_time : off_time,
																			service_type_name : service_type_name,
																			province:province,
							                                                city:city,
							                                                del_start_time:del_start_time,
							                    							del_off_time:del_off_time,
							                    							order_pay_type:order_pay_type,
							                    							merchant_name:merchant_name,
							                                                agentId:agentId
																		}
																	});
														}
													}
												}</cms:havePerm>
												],
											} ],
									        bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
									            displayInfo : true,
									            store : store
									        })
								});
				// 加载数据
				store.load({
					params : {
						start : 0,
						limit : 20
					}
				});
				store_appType.load();
				store_purchaseStatus.load();
				store_ServiceType
				.load({
					params : {
						app : 'cbt'
					}
				});
				 provinceStore.load();

				grid.on("itemcontextmenu", function(view, record, item, index, e) {
					e.preventDefault();
					contextmenu.showAt(e.getXY());
				});
				
				// 整体架构容器
				Ext.create("Ext.container.Viewport", {
					layout : 'border',
					autoHeight : true,
					border : false,
					items : [ grid ]
				});

				// 订单删除
				function delUserAll() {
					// grid中复选框被选中的项

					var rows = grid.getSelectionModel().getSelection();
					// user_id：所有选中的用户Id的集合使用','隔开，初始化为空
					var id = '';
					for (var i = 0; i < rows.length; i++) {
						if (i > 0) {
							id = id + ',' + rows[i].get('id');
						} else {
							id = id + rows[i].get('id');
						}
					}
					// 没有选择要执行操作的对象

					if (id == "") {
        			    Ext.example.msg("提示","请选择要删除的对象！");
						return;
					} else {
						Ext.Msg.confirm("提示信息", "请确定要执行删除操作吗?", function(btn) {
							if (btn == 'yes') {
								Ext.Ajax.request({
									url : "/order/deleteOrder",
									params : {
										id : id
									},
									method : 'post',
									success : function(o) {
									    Ext.example.msg("提示","订单删除成功！");
										store.reload();
										return;
									},
									failure : function(form, action) {
									    Ext.example.msg("提示","订单删除失败！");
									}
								});
							}
						});
					}
				};
				//导出所有订单
				function exportAll() {
					var order_id = Ext.getCmp('order_id').getValue();
					var phone = Ext.getCmp('phone').getValue();
					var merchant_phone = Ext.getCmp('merchant_phone').getValue();
					
					var purchase_status = Ext.getCmp('purchase_status').getValue();
					var service_type_name = Ext.getCmp('service_type_name').getValue();
					var start_time = Ext.getCmp('start_time').getValue();
					var off_time = Ext.getCmp('off_time').getValue();
					var province=Ext.getCmp('province').getRawValue();
		            var city=Ext.getCmp('city').getRawValue();
		            var del_start_time = Ext.getCmp('del_start_time').getValue();
					var del_off_time= Ext.getCmp('del_off_time').getValue();
					var order_pay_type= Ext.getCmp('order_pay_type').getValue();
					var merchant_name= Ext.getCmp('merchant_name').getValue();
		            var agentId='';
					window.location.href = '/order/exportExcel?order_id='+order_id+'&phone='+phone+'&province='+province+'&city='+city+'&agentId='+agentId+'&purchase_status='+purchase_status+'&service_type_name='+service_type_name
					      +'&merchant_phone='+merchant_phone +'&order_pay_type='+order_pay_type+'&merchant_name='+merchant_name
					         +'&start_time='+start_time+'&off_time='+off_time+'&del_start_time='+del_start_time+'&del_off_time='+del_off_time;
				};
				
				//查看订单详情
				function getServiceType() {
					 var rows = grid.getSelectionModel().getSelection();
					 var order_id=rows[0].get('id');
					// var service_type=rows[0].raw.service_type_id;
					// var orderTypeName=rows[0].get('orderTypeName');
					 switchAppType();
				};
            
				
			});
			
			//查看所有抢单的商户	
				var showMerchants=function(orderId,merchantsId){  	
					var columns = [
				                   {xtype: 'rownumberer'},
				                   {header:'编号',dataIndex:'id',hidden:true},
				                   {header:'商户',dataIndex:'name'},
				                   {header:'抢单状态',dataIndex:'attr'},
				            	   {header:'操作',dataIndex:'detail',renderer:function(value,v,r){  
				            		   var name='';
				              			if(r.data.name==''||r.data.name==null){
				                		   name='**'; 
				                	    }else{
				                	     name =r.data.name.replace('"', '').replace("'", "");
				                	    }
				                        return '<a href="javascript:showDetail(\''+r.data.id+'\',\''+name+'\')">详情</a>';
				                   }}
				               ];
					
				    var storeMerchants = Ext.create("Ext.data.Store",{
				    	pageSize:20, //每页显示几条数据  
				        proxy:{  
				            type:'ajax',  
				            url:'/order/getMerchantsByOrderId',
				            reader:{  
				                type:'json',  
				                totalProperty:'total',  
				                root:'data',  
				                idProperty:'id'  
				            }  
				        },  
				        fields:[  
				           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
				           {name:'name'},
				           {name:'attr'}
				        ]  
				    });
				    storeMerchants.on('beforeload', function (storeMerchants, options) {    
			        	
			            var new_params = { order_id:orderId,merchants_id:merchantsId};    
			            Ext.apply(storeMerchants.proxy.extraParams, new_params);    
			        });  
				    var gridMerchants = Ext.create("Ext.grid.Panel",{
				    	region: 'center',
				    	border: false,
				    	store: storeMerchants,
				    	selModel: Ext.create("Ext.selection.CheckboxModel", {
				    	    mode: "single"
				    	}),
				        columns: columns,
				        region: 'center', //框架中显示位置，单独运行可去掉此段
				        loadMask:true, //显示遮罩和提示功能,即加载Loading……  
				        forceFit:true, //自动填满表格  
				        columnLines:false, //列的边框
				        rowLines:true, //设置为false则取消行的框线样式
				        dockedItems: [{
				            xtype: 'pagingtoolbar',
				            store: storeMerchants,   // GridPanel使用相同的数据源
				            dock: 'bottom',
				            displayInfo: true,
				            plugins: Ext.create('Ext.ux.ProgressBarPager'),
				            emptyMsg: "没有记录" //没有数据时显示信息
				        }]
				    });
				  //加载数据  
				    storeMerchants.load({params:{start:0,limit:20,order_id:orderId,merchants_id:merchantsId}}); 
				    var syswinMerchants = Ext.create('Ext.window.Window',{  
				              title : "所有抢单商户",  
				              width :700, 
				              height: 300,
				              //height : 120,  
				              //plain : true,  
				              iconCls : "addicon",  
				              // 不可以随意改变大小  
				              resizable : false,  
				              // 是否可以拖动  
				              // draggable:false,  
				               autoScroll: true, 
				              collapsible : true, // 允许缩放条  
				              closeAction : 'close',  
				              closable : true,  
				              // 弹出模态窗体  
				              modal : 'true',  
				              buttonAlign : "center",  
				              bodyStyle : "padding:0 0 0 0",  
				              items : [gridMerchants],  
				              buttons : [{  
				                         text : "关闭",  
				                         minWidth : 70,  
				                         handler : function() {  
				                        	 syswinMerchants.close();  
				                         }  
				                     }]  
				           });  
				    syswinMerchants.show();  
				    };	
				    
				  //查看所有推送的商户	
					var showPushMerchants=function(orderId,merchantsId){  	
						var columns = [
					                   {xtype: 'rownumberer'},
					                   {header:'编号',dataIndex:'id',hidden:true},
					                   {header:'商户',dataIndex:'name'},
					                   {header:'抢单状态',dataIndex:'attr'},
					            	   {header:'操作',dataIndex:'detail',renderer:function(value,v,r){  
					            		   var name='';
					              			if(r.data.name==''||r.data.name==null){
					                		   name='**'; 
					                	    }else{
					                	     name =r.data.name.replace('"', '').replace("'", "");
					                	    }
					                        return '<a href="javascript:showDetail(\''+r.data.id+'\',\''+name+'\')">详情</a>';
					                   }}
					               ];
						
					    var storePushMerchants = Ext.create("Ext.data.Store",{
					    	pageSize:20, //每页显示几条数据  
					        proxy:{  
					            type:'ajax',  
					            url:'/order/getPushMerchantsByOrderId',
					            reader:{  
					                type:'json',  
					                totalProperty:'total',  
					                root:'data',  
					                idProperty:'id'  
					            }  
					        },  
					        fields:[  
					           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
					           {name:'name'},
					           {name:'attr'}
					        ]  
					    });
					    
					    storePushMerchants.on('beforeload', function (storePushMerchants, options) {    
				            var new_params = { order_id:orderId,merchants_id:merchantsId};    
				            Ext.apply(storePushMerchants.proxy.extraParams, new_params);    
				        });  
					    var gridPushMerchants = Ext.create("Ext.grid.Panel",{
					    	region: 'center',
					    	border: false,
					    	store: storePushMerchants,
					    	selModel: Ext.create("Ext.selection.CheckboxModel", {
					    	    mode: "single"
					    	}),
					        columns: columns,
					        region: 'center', //框架中显示位置，单独运行可去掉此段
					        loadMask:true, //显示遮罩和提示功能,即加载Loading……  
					        forceFit:true, //自动填满表格  
					        columnLines:false, //列的边框
					        rowLines:true, //设置为false则取消行的框线样式
					        dockedItems: [{
					            xtype: 'pagingtoolbar',
					            store: storePushMerchants,   // GridPanel使用相同的数据源
					            dock: 'bottom',
					            displayInfo: true,
					            plugins: Ext.create('Ext.ux.ProgressBarPager'),
					            emptyMsg: "没有记录" //没有数据时显示信息
					        }]
					    });
					  //加载数据  
					    storePushMerchants.load({params:{start:0,limit:20,order_id:orderId,merchants_id:merchantsId}}); 
					    var syswinPushMerchants = Ext.create('Ext.window.Window',{  
					              title : "近7日推送商户",  
					              width :700, 
					              height: 500,
					              //height : 120,  
					              //plain : true,  
					              iconCls : "addicon",  
					              // 不可以随意改变大小  
					              resizable : false,  
					              // 是否可以拖动  
					              // draggable:false,  
					              collapsible : true, // 允许缩放条  
					              closeAction : 'close',  
					              autoScroll: true, 
					              closable : true,  
					              // 弹出模态窗体  
					              modal : 'true',  
					              buttonAlign : "center",  
					              bodyStyle : "padding:0 0 0 0",  
					              items : [gridPushMerchants],  
					              buttons : [{  
					                         text : "关闭",  
					                         minWidth : 70,  
					                         handler : function() {  
					                        	 syswinPushMerchants.close();  
					                         }  
					                     }]  
					           });  
					    syswinPushMerchants.show();  
					    };				    
				    
	function showDetail(id,name){
		parent.addTab("merchant_detail_"+id,"服务商【"+name+"】详情","Fuwushangyunyingxinxiguanli","/merchants/merchantDetail?merchantId="+id); 	 
		 }		
	function showOrderDetail(id,serviceTypeId){
		  parent.addTab("order_detail_"+id,"订单【"+id+"】明细","Fuwushangyunyingxinxiguanli","/order/orderStatusDetail?orderId="+id+"&serviceTypeId="+serviceTypeId); 	 
	  }
	
</script>

</head>
<body>
</body>
</html>