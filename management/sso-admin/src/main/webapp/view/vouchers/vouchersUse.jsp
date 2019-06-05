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
<title>代金卷使用记录</title>
<script type="text/javascript">
//引入扩展组件
Ext.Loader.setConfig({
	enabled : true
});

Ext.Loader.setPath('Ext.ux', '../ExtJS4.2/ux/');

Ext.require([ 'Ext.data.*', 'Ext.grid.*', 'Ext.util.*',
		'Ext.tip.QuickTipManager']);

Ext.onReady(function() {
	var columns = [ 
	    {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
		{header : '手机号',dataIndex : 'phone',align:'center'}, 
	    {header : '代金券',dataIndex : 'voucher_name',align:'center'}, 
	    {header : '使用时间',dataIndex : 'join_time',align:'center'},
	    {header : '服务商',dataIndex : 'merchants_name',align:'center'}];
	//创建store数据源

	//列表展示数据源
	var store = Ext.create("Ext.data.Store", {
		pageSize : 20, //每页显示几条数据  
		remoteSort: true,
		proxy : {
			type : 'ajax',
			url : '/vouchers/getUseVouchers',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data',
				idProperty : '#'
			}
		},
		fields : [ {
			name : 'phone'
		}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
		{
			name : 'name'
		}, {
			name : 'voucher_name'
		}, {
			name : 'join_time'
		},{
			name : 'merchants_name'
		}]
	});
	//服务类型
	var store_ServiceType = Ext.create("Ext.data.Store", {
		pageSize : 20, //每页显示几条数据  
		proxy : {
			type : 'ajax',
			url : '#',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data',
				idProperty : 'id'
			}
		},
		fields : [ {
			name : 'id'
		}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
		{
			name : 'coupons_type'
		} ]
	});
	//app类型
	var store_appType = Ext.create("Ext.data.Store", {
		pageSize : 20, //每页显示几条数据  
		proxy : {
			type : 'ajax',
			url : '/common/showAppType',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'app_type'
		}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
		{
			name : 'app_name'
		} ]
	});
	//点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var phone = Ext.getCmp('phone').getValue();
		var start_time = Ext.getCmp('start_time').getValue();
		var  off_time= Ext.getCmp('off_time').getValue();
        var new_params = {phone : phone,
        		start_time : start_time,
        		off_time : off_time};    
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
			items : [ {
				xtype : 'textfield',
				id : 'phone',
				name : 'phone',
				fieldLabel : '手机号',
				labelAlign : 'left',
				labelWidth : 45
			},'-', {
				xtype : 'datetimefield',
				id : 'start_time',
				name : 'start_time',
				format : 'Y-m-d',
				fieldLabel : '开始时间',
				labelAlign : 'left',
				labelWidth : 60
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
			<cms:havePerm url='/vouchers/getUseVouchers'>
			{
				xtype : 'button',
				id : 'select',
				text : '查询',
				iconCls : 'Select',
				listeners : {
					click : function() {
						var phone = Ext.getCmp('phone').getValue();
						var start_time = Ext.getCmp('start_time').getValue();
						var off_time = Ext.getCmp('off_time').getValue();
						store.currentPage = 1;
						store.load({
							params : {
								start:0,
								page : 1,
								limit : 20,
								phone : phone,
								start_time : start_time,
								off_time : off_time
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
	store_appType.load();
	
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
						url : "#",
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
	}
	;

	//form表单
	var showform = function() {
		var add_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 450,
			height : 470,
			bodyPadding : 5,
			//renderTo:"panel21",  
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [ {
				xtype : 'combobox',
				name : 'app_type',
				fieldLabel : 'app类型',
				valueField : 'app_type',
				displayField : 'app_name',
				store : store_appType,
				listeners : { // 监听该下拉列表的选择事件
					select : function(combobox, record, index) {
						Ext.getCmp('service_type').setValue('');
						store_ServiceType.load({
							params : {
								app : combobox.value
							}
						});
					}
				},
				queryMode : 'local',
			}, {
				xtype : 'combobox',
				id : 'service_type',
				name : 'coupons_type',
				fieldLabel : '服务类型',
				valueField : 'coupons_type',
				displayField : 'coupons_name',
				store : store_coupons,
				queryMode : 'local',
			}, {
				// 输入姓名
				xtype : 'textfield',
				name : 'price',
				fieldLabel : '价格'
			}, {
				// 输入姓名
				xtype : 'textfield',
				name : 'count',
				fieldLabel : '数量'
			} ]

		});

		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增代金券",
			width : 450,
			height : 350,
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
							url : '/vouchers/addVouchers',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',

							success : function(fp, o) {
								if (o.result == true) {
									Ext.example.msg("提示","保存成功！"); 
									syswin.close(); //关闭窗口  
									store.reload();
								} else {
									Ext.example.msg("提示","保存失败！"); 
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

});
</script>
</head>
<body>
</body>
</html>