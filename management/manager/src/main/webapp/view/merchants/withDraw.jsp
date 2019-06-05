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
<title>商户提现记录</title>
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
var grid;
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50},
                   {header:'编号',dataIndex:'id',hidden:true},
                   {header:'服务商名称',dataIndex:'merchants_name'},
                   {header:'省份',dataIndex:'province',hidden:true},
                   {header:'城市',dataIndex:'city',hidden:true},
                   {header:'姓名',dataIndex:'real_name'},
                   {header:'注册号码',dataIndex:'telephone'},
                   {header:'身份证号',dataIndex:'ID_No'},
                   {header:'银行类型',dataIndex:'withdraw_name'},
                   {header:'银行卡号',dataIndex:'withdraw_no'},
                   {header:'提现金额',dataIndex:'withdraw_price'},
                   {header:'申请时间',dataIndex:'withdraw_time'},
                   {header:'申请状态',dataIndex:'withdraw_status',renderer:function(value){  
                       if(value=='0'){ 
                           return "<span style='color:red;font-weight:bold';>已拒绝</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>已完成</span>";  
                       }else if(value=='2'){  
                           return "<span style='color:blue;font-weight:bold';>待审核</span>";  
                       }
           		}},
           	    {header:'审核人',dataIndex:'audit_name'},
           		{header:'操作',dataIndex:'withdraw_status',renderer:function(value,v,r){  
                  
                    if(value=='0'){ 
                     return '<a href="javascript:audit()"><span style="color:red;font-weight:bold";>拒绝原因</span></a>';
                    } else if(value=='1'){  
                        return '<a href="javascript:audit()"><span style="color:green;font-weight:bold";>流水号</span></a>'; 
                    }else if(value=='2'){  
                      return '<a href="javascript:audit()"><span style="color:blue;font-weight:bold";>审核</span></a>';
                    }
        		}}
               
               ];
	
     store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/merchants/getMerchantsWithDraw',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'
            }  
        },  
        fields:[  
           {name:'merchants_name'}, 
           {name:'withdraw_price'},  
           {name:'real_name'},
           {name:'ID_No'},
           {name:'telephone'},
           {name:'province'},
           {name:'city'},
           {name:'withdraw_time'},
           {name:'withdraw_status'},
           {name:'withdraw_no'},
           {name:'withdraw_name'},
           {name:'audit_name'}
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
    	 var merchants_name=Ext.getCmp('merchants_name').getValue();
    	 var withdraw_status=Ext.getCmp('withdraw_status').getValue();
    	 var start_time = Ext.getCmp('start_time').getValue();
		 var off_time= Ext.getCmp('off_time').getValue();
		 var province=Ext.getCmp('province').getRawValue();
		 var city=Ext.getCmp('city').getRawValue();
		 var agentId='';
         var new_params = { merchants_name:merchants_name,withdraw_status : withdraw_status,start_time : start_time,off_time : off_time,province:province,city:city,agentId:agentId};    
         Ext.apply(store.proxy.extraParams, new_params);    
    });  
    var sm = Ext.create('Ext.selection.CheckboxModel');
     grid = Ext.create("Ext.grid.Panel",{
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
        	    	   <cms:havePerm url='/merchants/exportWithDrawExcel'>
	        	       { xtype: 'button', text: '导出',iconCls:'Daochu',
	            	    	   listeners: {
	            	    		   click:function(){
	            	    			   exportAll();
	            	    		   }
	            	    	   }
	                   }</cms:havePerm>
                   
                  ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'merchants_name',name: 'merchants_name',fieldLabel: '服务商名称',labelAlign:'left',labelWidth:70},'-',
        	       {xtype : 'datetimefield',id : 'start_time',name : 'start_time',format : 'Y-m-d',fieldLabel : '开始时间',labelAlign : 'left',labelWidth : 60},'-',
				   {xtype : 'datetimefield',id : 'off_time',name : 'off_time',format : 'Y-m-d',fieldLabel : '结束时间',labelAlign : 'left',labelWidth : 60}, '-',
				   ]
		              },	{
		  	xtype : 'toolbar',
			dock : 'top',
			displayInfo : true,
			items : [ 
			          {xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省份',valueField : 'id',displayField : 'area',
				          store : provinceStore,listeners : { // 监听该下拉列表的选择事件
													select : function(
															combobox,
															record,
															index) {
														Ext
																.getCmp(
																		'city')
																.setValue('');
														cityStore.load({params : {
																		parentId : combobox.value
																	}
																});
													}
												},
				        queryMode : 'local',labelAlign : 'left',labelWidth : 70},'-',
	              {xtype : 'combobox',id : 'city',name : 'city',fieldLabel : '城市',valueField : 'id',displayField : 'area',
				      store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',	
				   { xtype: 'combobox',id: 'withdraw_status',name: 'withdraw_status',fieldLabel: '申请状态',displayField: 'status_name',value:'',valueField : 'status_type',store: Ext.create('Ext.data.Store', {  
                       fields: [  
  	                          { name: 'status_type'},{ name: 'status_name' }  
  	                          ],  
  	                                          data: [  
  	                          { "status_type": "","status_name": "全部" }, 
  	                          { "status_type": "0","status_name": "已拒绝" },  
  	                          { "status_type": "1","status_name": "已完成" },
  	                          { "status_type": "2","status_name": "待审核" }
  	                          ]  
  	                    }),queryMode: 'local',labelAlign:'left',labelWidth:60},'-',
  	                  <cms:havePerm url='/merchants/getMerchantsWithDraw'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			     var merchants_name=Ext.getCmp('merchants_name').getValue();
    	    		    	 var withdraw_status=Ext.getCmp('withdraw_status').getValue();
    	    		    	 var start_time = Ext.getCmp('start_time').getValue();
    	    				 var  off_time= Ext.getCmp('off_time').getValue();
    	    				 var province=Ext.getCmp('province').getRawValue();
    	    				 var city=Ext.getCmp('city').getRawValue();
    	    				 var agentId='';
    	    				 store.currentPage = 1;
    	    			     store.load({params:{start:0,page:1,limit:20,merchants_name:merchants_name,withdraw_status : withdraw_status,start_time : start_time,off_time : off_time,province:province,city:city,agentId:agentId}}); 
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
	
	
	
    //导出所有体现记录
	function exportAll() {
		 var merchants_name=Ext.getCmp('merchants_name').getValue();
    	 var withdraw_status=Ext.getCmp('withdraw_status').getValue();
    	 var start_time = Ext.getCmp('start_time').getValue();
		 var off_time= Ext.getCmp('off_time').getValue();
		 var province=Ext.getCmp('province').getRawValue();
		 var city=Ext.getCmp('city').getRawValue();
		 var agentId='';
		 window.location.href = '/merchants/exportWithDrawExcel?merchants_name='+merchants_name+'&withdraw_status='
				+withdraw_status+'&start_time='+start_time+'&off_time='+off_time+'&province='+province+'&city='+city+'&agentId='+agentId;
	};
});

var audit=function(){
	var rows = grid.getSelectionModel().getSelection();  
    //user_id：所有选中的服务商Id的集合使用','隔开，初始化为空    
    if(rows.length == 0)  
    {  
  	    Ext.example.msg("提示","请选择要审核的对象！");
       return ;  
    }
   if(rows.length > 1)  
   {  
	  Ext.example.msg("提示","只能选择一个审核的对象！");
     return ;  
    }
    
   var id=rows[0].get('id');
   var merchants_name=rows[0].get('merchants_name');
   var withdraw_price=rows[0].get('withdraw_price');
   var withdraw_time=rows[0].get('withdraw_time');
   var withdraw_name=rows[0].get('withdraw_name');
   var withdraw_no=rows[0].get('withdraw_no');
   var real_name=rows[0].get('real_name');
   var ID_No=rows[0].get('ID_No');
   var merchant_id=rows[0].raw.merchant_id;
   var withdraw_status=rows[0].get('withdraw_status');
   var remark=rows[0].raw.remark;
   
	if(withdraw_status==0){
	   // Ext.example.msg("拒绝原因",remark);
		Ext.Msg.alert("拒绝原因", remark);
	}else if(withdraw_status==1){
		// Ext.example.msg("流水号",remark);
		Ext.Msg.alert("流水号", remark);
	}else if(withdraw_status==2){
		editform(id,merchants_name,withdraw_price,withdraw_time,withdraw_name,withdraw_no,merchant_id,real_name,ID_No);
	}
};

// form表单编辑用户
	var editform = function(id,merchants_name,withdraw_price,withdraw_time,withdraw_name,withdraw_no,merchant_id,real_name,ID_No) {
		var edit_winForm = Ext.create('Ext.form.Panel', {
			frame : true, // frame属性
			// title: 'Form Fields',
			width : 450,
			height : 400,
			bodyPadding : 5,
			// renderTo:"panel21",
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [ {
				// 显示文本框，相当于label
				xtype : 'displayfield',
				name : 'displayfield1',
				value : '服务商提现请求审核'

			}, 
			{
				// 服务商名称
				xtype : 'textfield',
				name : 'merchants_name',
				fieldLabel : '服务商名称',
				readOnly:true
			},{
				// 姓名
				xtype : 'textfield',
				name : 'real_name',
				fieldLabel : '姓名',
				readOnly:true
			},
			{
				// 身份证号
				xtype : 'textfield',
				name : 'ID_No',
				fieldLabel : '身份证号',
				readOnly:true
			},{
				// 输入金额
				xtype : 'textfield',
				name : 'withdraw_price',
				fieldLabel : '申请提现金额',
				readOnly:true
			}, 
			{ 
				xtype: 'textfield',
				name: 'withdraw_time',
				fieldLabel: '申请时间',
				readOnly:true
			},
			{ 
				xtype: 'textfield',
				name: 'withdraw_name',
				fieldLabel: '提现银行',
				readOnly:true
					},
			{ 
				xtype: 'textfield',
				name: 'withdraw_no',
				fieldLabel: '提现账号',
				readOnly:true
              }
			]
		});
		// 创建window面板，表单面板是依托window面板显示的
		edit_winForm.form.findField('merchants_name').setValue(merchants_name);
		edit_winForm.form.findField('real_name').setValue(real_name);
		edit_winForm.form.findField('ID_No').setValue(ID_No);
		edit_winForm.form.findField('withdraw_price').setValue(withdraw_price);
		edit_winForm.form.findField('withdraw_time').setValue(withdraw_time);
		edit_winForm.form.findField('withdraw_name').setValue(withdraw_name);
		edit_winForm.form.findField('withdraw_no').setValue(withdraw_no);
	   
		var editwindow = Ext
				.create(
						'Ext.window.Window',
						{
							title : "查看详情",
							width : 460,
							height : 400,
							// height : 120,
							// plain : true,
							iconCls : "addicon",
							// 不可以随意改变大小
							resizable : true,
							// 是否可以拖动
							draggable : true,
							collapsible : true, // 允许缩放条
							closeAction : 'close',
							closable : true,
							// 弹出模态窗体
							modal : 'true',
							buttonAlign : "center",
							bodyStyle : "padding:0 0 0 0",
							items : [ edit_winForm ],
							buttons : [
										{
											text : "允许提现",
											minWidth : 70,
											handler : function() {
												var withdraw_status=1;
												 Ext.MessageBox.prompt("输入框","请输入流水号：",function(bu,txt){  
												if(bu=='ok'){
													if(txt==''){
														Ext.example.msg("提示","请输入流水号");
														return;
													}
												     Ext.Ajax.request({  
												            url:"/merchants/AuditAllWithDraw",  
												            params:{id:id,withdraw_status:withdraw_status,remark:txt,withdraw_price:withdraw_price,merchant_id:merchant_id},  
												            method:'post',  
												            success:function(o){ 
												            	if(o.responseText=='true'){
											    			    Ext.example.msg("提示","审核成功！");
												                store.reload(); 
												                editwindow.close();
												                return ;  
												            	}else{
												            		Ext.example.msg("提示","审核失败！");	
												            	}
												            },  
												            failure:function(form,action){  
											    			    Ext.example.msg("提示","审核失败！");
												                editwindow.close();
												            }  
												        });
												        }
												},this,300);
											}
										}, {
											text : "拒绝提现",
											minWidth : 70,
											handler : function() {
												var withdraw_status=0;
												Ext.MessageBox.prompt("输入框","请输入拒绝原因：",function(bu,txt){ 
												if(bu=='ok'){
													if(txt==''){
														Ext.example.msg("提示","请输入拒绝原因");
														return;
													}
												Ext.Ajax.request({  
										            url:"/merchants/AuditAllWithDraw",  
										            params:{id:id,withdraw_status:withdraw_status,remark:txt,withdraw_price:withdraw_price,merchant_id:merchant_id},  
										            method:'post',  
										            success:function(o){  
										            	if(o.responseText=='true'){
									    			    Ext.example.msg("提示","审核成功！");
										                editwindow.close();
										                store.reload();  
										                return ;  
										            	}else{
										            		Ext.example.msg("提示","审核失败！");	
										            	}
										            },  
										            failure:function(form,action){ 
									    			    Ext.example.msg("提示","审核失败！");
										                editwindow.close();
										            }  
										        }); 
										         }
										},this,300);
									}
										} ]
							
							
						});
		editwindow.show();
	};
</script>

</head>
<body>
</body>
</html>