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
<title>代理商消费记录</title>
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
                   {header:'订单号',dataIndex:'id',align:'center'},
                   {header:'账号',dataIndex:'agent_name',align:'center'},
                   {header:'操作时间',dataIndex:'charge_time',align:'center'},
                   {header:'代理省份',dataIndex:'provinceDesc',sortable:true,fixed:false,align:'center'},
                   {header:'代理城市',dataIndex:'cityDesc',sortable:true,fixed:false,align:'center'},
                   {header:'代理类型',dataIndex:'userType',sortable:true,align:'center',fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='font-weight:bold';>公司员工</span>";  
                       }else if(value=='2'){
                    	   return "<span style='color:green;font-weight:bold';>省代理</span>"; 
                       }else if(value=='3'){
                    	   return "<span style='color:red;font-weight:bold';>市代理</span>"; 
                       }else if(value=='4'){
                    	   return "<span style='color:blue;font-weight:bold';>项目代理</span>"; 
                       }
           		   }},
                   {header:'金额类型',dataIndex:'charge_type',align:'center',sortable:true,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>充值</span>";  
                       }else{
                    	   return "<span style='color:green;font-weight:bold';>扣费</span>"; 
                       }
           		   }},
                   {header:'金额',dataIndex:'charge_money',sortable:true,fixed:false,align:'center'},
                   {header:'操作人',dataIndex:'head_name',sortable:true,fixed:false,align:'center'},
                   {header:'代理项目',dataIndex:'agent_id',align:'center',renderer:function(value,v,r){
                   if(r.data.userType==4){  
                           return "<a href='javascript:showApp("+value+")'>查看</a>";
                    }else{
                    return "<span style='color:red;font-weight:bold';></span>";
                    }
           		   }}
                   
               ];
	
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/agent/getAgentCharge',  
            reader:{  
                type:'json',  
                totalProperty:'total', 
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'},
           {name:'order_id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'agent_id'},
           {name:'agent_name'},  
           {name:'charge_time'},
           {name:'provinceDesc'}, 
           {name:'cityDesc'},
           {name:'userType'},
           {name:'charge_type'}, 
           {name:'charge_money'},
           {name:'head_name'},
           {name:'order_status'}
        ]  
    });
    var comboStore = new Ext.data.SimpleStore({
    	fields:['userTypeName','userType'],
    	data:[['全部',''],
    	     
    	      ['省代理','2'],
    	      ['市代理','3'],
    	      ['项目代理','4']
    	]
    });	
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var order_id=Ext.getCmp('order_id').getValue();
    	 var agent_name=Ext.getCmp('agent_name').getValue();
    	 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
		 var head_name=Ext.getCmp('head_name').getValue();
		 var userType=Ext.getCmp('userType').getValue();
		 var charge_type=Ext.getCmp('charge_type').getValue();
		 var province;
		 var city;
		 var agentId;
         var new_params = { order_id:order_id,agent_name : agent_name,start_time : start_time,off_time : off_time,head_name:head_name,charge_type:charge_type,userType:userType,province:province,city:city,agentId:agentId };    
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
        	       <cms:havePerm url='/agentCharge/detail'>
                   { xtype: 'button', id:'detail', text: '详情',iconCls:'Detail',
        	    	   listeners: {
        	    		   click:function(){
        	    			   editform();   
        	    		   }
        	    	   }},'-',</cms:havePerm> 
        	    	   <cms:havePerm url='/agent/deleteAgentCharge'>
        	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        		    	   listeners: {
        		    		   click:function(){
        		    			   delAgent();
        		    		   }
        		    	   }},
        	           </cms:havePerm>
            	    	   <cms:havePerm url='/agent/exportExcel'>
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
        	items:[{ xtype: 'textfield',id:'order_id',name: 'order_id',fieldLabel: '订单号',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'textfield',id: 'agent_name',name: 'agent_name',fieldLabel: '账号',labelAlign:'left',labelWidth:70},'-',
        	       { xtype: 'datetimefield',id: 'start_time',name: 'start_time',format : 'Y-m-d',fieldLabel: '开始时间',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'datetimefield',id: 'off_time',name: 'off_time',format : 'Y-m-d',fieldLabel: '结束时间',labelAlign:'left',labelWidth:60},'-',
        	       ]},
				   {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{xtype : 'combobox',id : 'userType',name : 'userType',fieldLabel : '代理类型',value:'',editable:false,valueField : 'userType',displayField : 'userTypeName',
							store : comboStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
        	       {xtype: 'textfield',id: 'head_name',name: 'head_name',fieldLabel: '操作人',labelAlign:'left',labelWidth:70},'-',
        	       { xtype: 'combobox',id: 'charge_type',name: 'charge_type',fieldLabel: '金额类型',editable:false,displayField: 'charge_name',valueField : 'charge_type',value:'',store: Ext.create('Ext.data.Store', {  
                       fields: [  
  	                          { name: 'charge_type'},{ name: 'charge_name' }  
  	                          ],  
  	                                          data: [  
  	                          { "charge_type": "","charge_name": "全部" },
  	                          { "charge_type": "1","charge_name": "充值" },  
  	                          { "charge_type": "2","charge_name": "扣费" }
  	                          ]  
  	                    }),queryMode: 'local',labelAlign:'left',labelWidth:60},'-',
  	                  <cms:havePerm url='/agent/getAgentCharge'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var order_id=Ext.getCmp('order_id').getValue();
    	    		       var agent_name=Ext.getCmp('agent_name').getValue();
    	    		       var start_time=Ext.getCmp('start_time').getValue();
    	    			   var off_time=Ext.getCmp('off_time').getValue();
    	    			   var head_name=Ext.getCmp('head_name').getValue();
    	    			   var userType=Ext.getCmp('userType').getValue();
    	    			   var charge_type=Ext.getCmp('charge_type').getValue();
    	    			   var province;
    	    			   var city;
    	    			   var agentId;
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,order_id:order_id,agent_name : agent_name,start_time : start_time,off_time : off_time,head_name:head_name,charge_type:charge_type,userType:userType,province:province,city:city,agentId:agentId}}); 
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
   
 	  <cms:havePerm url='/agentCharge/detail'>
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
	
	//删除消费记录
	function delAgent()  
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
                     url:"/agent/deleteAgentCharge",  
                     params:{id:ids},  
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
	
    //导出所有代理商充值记录
	function exportAll() {
		   var order_id=Ext.getCmp('order_id').getValue();
	       var agent_name=Ext.getCmp('agent_name').getValue();
	       var start_time=Ext.getCmp('start_time').getValue();
		   var off_time=Ext.getCmp('off_time').getValue();
		   var head_name=Ext.getCmp('head_name').getValue();
		   var userType=Ext.getCmp('userType').getValue();
		   var charge_type=Ext.getCmp('charge_type').getValue();
		   var province='';
		   var city='';
		   var agentId='';
		  window.location.href = '/agent/exportExcel?order_id='+order_id+'&agent_name='+agent_name+'&start_time='+start_time
		              +'&off_time='+off_time+'&head_name='+head_name+'&userType='+userType+'&charge_type='
		              +charge_type+'&province='+province+'&city='+city+'&agentId='+agentId;
	};
    function AuditAll()  {
    	var rows = grid.getSelectionModel().getSelection();  
    	var order_status=rows[0].get('order_status');
    	if(order_status==1){
    		Ext.example.msg("提示","订单已结束！"); 
			return;
    	}
        //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
        var id = '';  
        for(var i = 0;i<rows.length;i++)  
        {  
           if(i>0)  
           {  
               id = id+','+rows[i].get('id');  
           }else{  
               id = id+rows[i].get('id');  
           }  
        }  
        Ext.Msg.confirm("提示信息", "请确定要执行审核操作吗?", function(btn) {
			if (btn == 'yes') {
        Ext.Ajax.request({  
            url:"/agent/AuditAgentCharge",  
            params:{id:id},  
            method:'post',  
            success:function(o){  
        		Ext.example.msg("提示","审核成功！"); 
                store.reload();  
                return ;  
            },  
            failure:function(form,action){  
            	Ext.example.msg("提示","审核失败！"); 
            }  
        });
			}
		}); 
    };
   
 // 代理商充值详情页
	var editform = function() {
		
		var edit_winForm = Ext.create('Ext.form.Panel', {
			frame : true, // frame属性
			// title: 'Form Fields',
			width : 450,
			height : 470,
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
				value : '代理商充值详细信息'

			}, {
				// 输入姓名
				xtype : 'textfield',
				name : 'agent_name',
				fieldLabel : '代理商',
				readOnly:true
			}, 
			{
		    	// 选择充值还是扣费 
		    	xtype: 'combobox',
		    	name: 'charge_type',
		    	fieldLabel: '金额类型',
		    	displayField: 'charge_name',
		    	valueField : 'charge_type',
		    	store: Ext.create('Ext.data.Store', {  
                fields: [  
  	                          { name: 'charge_type'},{ name: 'charge_name' }  
  	                          ],  
  	                                          data: [  
  	                          { "charge_type": "1","charge_name": "充值" },  
  	                          { "charge_type": "2","charge_name": "扣费" }
  	                          ]  
  	                    }),
  	             queryMode: 'local',
  	             labelAlign:'left'
  	            },{
				// 输入充值金额
				xtype : 'textfield',
				name : 'charge_money',
				fieldLabel : '金额',
				readOnly:true
			}, 
			{ 
				xtype: 'datetimefield',
				name: 'charge_time',
				fieldLabel: '操作时间',
				format : 'Y-m-d',
				readOnly:true

				},
			{ 
					xtype: 'textfield',
					name: 'head_name',
					fieldLabel: '负责人',
					readOnly:true
              },
              {  
                  //多行文本输入框  
                  xtype: 'textareafield', //5  
                  name: 'remark',  
                  fieldLabel: '备注',   
                  readOnly:true
              }
			
			]
		});
		
		// 创建window面板，表单面板是依托window面板显示的

		var rows = grid.getSelectionModel().getSelection();
		// user_id：所有选中的用户Id的集合使用','隔开，初始化为空
		if (rows.length == 0) {
        	Ext.example.msg("提示","请选择要编辑的对象！"); 
			return;
		}
		 if(rows.length > 1)  
	       {  
		      Ext.example.msg("提示","只能选择一个编辑的对象！"); 
	          return ;  
	       }
		
		edit_winForm.form.findField('agent_name').setValue(rows[0].get('agent_name'));
		edit_winForm.form.findField('charge_type').setValue(rows[0].get('charge_type')+'');
		edit_winForm.form.findField('charge_time').setValue(rows[0].get('charge_time'));
		edit_winForm.form.findField('charge_money').setValue(rows[0].get('charge_money'));
		edit_winForm.form.findField('head_name').setValue(rows[0].get('head_name'));
		edit_winForm.form.findField('remark').setValue(rows[0].raw.remark);
		
		var editwindow = Ext
				.create(
						'Ext.window.Window',
						{
							title : "查看详情",
							width : 450,
							height : 350,
							// height : 120,
							// plain : true,
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
							items : [ edit_winForm ]
							
						});
		editwindow.show();
	};
	  //选择代理商，充值
	var showform=function(){  
		
		var columns = [
	                   {xtype: 'rownumberer'},
	                   {header:'编号',dataIndex:'id',hidden:true},
	                   {header:'代理商',dataIndex:'userName'}
	               ];
		
	    var storeUser = Ext.create("Ext.data.Store",{
	    	pageSize:20, //每页显示几条数据  
	        proxy:{  
	            type:'ajax',  
	            url:'/agent/getAgentList',
	            reader:{  
	                type:'json',  
	                totalProperty:'total',  
	                root:'data',  
	                idProperty:'id'  
	            }  
	        },  
	        fields:[  
	           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
	           {name:'userName'}
	        ]  
	    });
	    var gridUser = Ext.create("Ext.grid.Panel",{
	    	region: 'center',
	    	border: false,
	    	store: storeUser,
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
	        	xtype:'toolbar',
	        	dock:'top',
	        	displayInfo: true,
	        	items:[{ xtype: 'textfield',name: 'name',fieldLabel: '代理商',labelAlign:'left',labelWidth:55},
	                  
	                   { xtype: 'button', text: '查询',iconCls:'Usermagnify',listeners: {
	    	    		   click:function(){
	    	    			   var agent_name= gridUser.dockedItems.items[1].items.items[0].rawValue;
	    	    			   storeUser.currentPage = 1;
	    	    			   storeUser.load({params:{start:0,page:1,limit:20,agent_name:agent_name}}); 
	    	    		   }
	    	    		   }}]
	        },{
	            xtype: 'pagingtoolbar',
	            store: storeUser,   // GridPanel使用相同的数据源
	            dock: 'bottom',
	            displayInfo: true,
	            plugins: Ext.create('Ext.ux.ProgressBarPager'),
	            emptyMsg: "没有记录" //没有数据时显示信息
	        }]
	    });
	  //加载数据  
	    storeUser.load({params:{start:0,limit:20}}); 
	    var syswin = Ext.create('Ext.window.Window',{  
	              title : "选择代理商",  
	              width : 700, 
//	              height: 500,
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
	              items : [gridUser],  
	              buttons : [{  
	                         text : "充值",  
	                         minWidth : 70,  
	                         handler : function() {
	                        	agentCharge();
	                         }  
	                     }, {  
	                         text : "关闭",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            syswin.close();  
	                         }  
	                     }]  
	           });  
	    syswin.show();  
	    
	  //选择金额充值
		var agentCharge=function(){  
			
		    var charge_winForm =  Ext.create('Ext.form.Panel', {  
		                frame: true,   //frame属性  
		                //title: 'Form Fields',  
		                width: 440,
		                height:470,
		                bodyPadding:5,  
		                //renderTo:"panel21",  
		                fieldDefaults: {  
		                    labelAlign: 'left',  
		                    labelWidth: 90,  
		                    anchor: '100%'  
		                },  
		                items: [{  
		                    //显示文本框，相当于label  
		                    xtype: 'displayfield',   
		                    name: 'displayfield1',  
//		                    fieldLabel: 'Display field',  
		                    value: '请填写充值信息'  
		                     
		                }, 
		                {
		    				// 输入姓名
		    				xtype : 'textfield',
		    				name : 'agent_id',
		    				fieldLabel : '代理商ID',
		    				hidden:true
		    			}, {
		    				// 输入姓名
		    				xtype : 'textfield',
		    				name : 'agent_name',
		    				fieldLabel : '代理商',
		    				readOnly:true
		    			}, {
		    				// 输入充值金额
		    				xtype : 'numberfield',
		    				name : 'agent_charge',
							allowBlank	:false,
		    				fieldLabel : '充值金额'
		    				
		    			}, 
		    			{ 
		    				xtype: 'datetimefield',
		    				name: 'charge_time',
		    				fieldLabel: '充值时间',
		    				format : 'Y-m-d'

		    				},
		    			{ 
		    					xtype: 'textfield',
		    					name: 'head_name',
								allowBlank	:false,
		    					fieldLabel: '负责人',
		    				    readOnly:true
		                  },
		                  {  
		                      //多行文本输入框  
		                      xtype: 'textareafield', //5  
		                      name: 'remark',  
		                      fieldLabel: '备注'
		                  }
		    			]  
		            });  
		    //创建window面板，表单面板是依托window面板显示的  
		    
		    var rows = gridUser.getSelectionModel().getSelection();  
	        //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
		    if(rows.length == 0)  
	        {  
			   Ext.example.msg("提示","请选择要充值的对象！"); 
	           return ;  
	        }
	        charge_winForm.form.findField('agent_id').setValue(rows[0].get('id'));
		    charge_winForm.form.findField('agent_name').setValue(rows[0].get('userName'));
		    charge_winForm.form.findField('head_name').setValue('${_user_name}');
		    var issuewindow = Ext.create('Ext.window.Window',{  
		              title : "充值",  
		              width: 450,
		                  height:350,  
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
		              items : [charge_winForm],  
		              buttons : [{  
		                         text : "充值",  
		                         minWidth : 70,  
		                         handler : function() {  
		                        	
		                            if (charge_winForm.getForm().isValid()) {  
		                            	charge_winForm.getForm().submit({  
		                                          url :'/agent/addAgentCharge',
		                                          //等待时显示 等待  
		                                          waitTitle: '请稍等...',  
		                                          waitMsg: '正在提交信息...',  
		                                            
		                                          success: function(fp, o) {
		                               			       Ext.example.msg("提示","充值成功！");
                                                       issuewindow.close(); //关闭窗口  
													   store.reload(); 
		                                          },  
		                                          failure: function() { 
		                           			           Ext.example.msg("提示","充值失败！");
		                                          }  
		                                       });  
		                            }  
		                         }  
		                     }, {  
		                         text : "关闭",  
		                         minWidth : 70,  
		                         handler : function() {  
		                        	 issuewindow.close();  
		                         }  
		                     }]  
		           });  
		    issuewindow.show();  
		    };
	    
	    };
});

 //查看代理商代理的项目
		var showApp=function(agentId){  
			var columns = [
                {header:'序号',xtype: 'rownumberer',width:50},
				{header : '项目',dataIndex : 'app_name'}
				
			];
		//列表展示数据源
			var storeApp = Ext.create("Ext.data.Store", {
				pageSize : 20, //每页显示几条数据  
				proxy : {
					type : 'ajax',
					url : '/agent/getAppByAgentId',
					reader : {
						type : 'json',
						totalProperty : 'total',
						root : 'data',
						idProperty : '#'
					}
				},
		        fields:[  
		           {name:'app_name'} //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
		           
		        ]  
		    });
		
			 storeApp.on('beforeload', function (storeApp, options) {    
	  		    	
	  		        var new_params = { agentId:agentId};    
	  		        Ext.apply(storeApp.proxy.extraParams, new_params);    
	  		    }); 
		   
		    var gridApps = Ext.create("Ext.grid.Panel",{
		    	region: 'center',
		    	border: false,
		    	store: storeApp,
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
		        	items:[]
		        },{
		        	xtype:'toolbar',
		        	dock:'top',
		        	displayInfo: true,
		        	
		        },{
		            xtype: 'pagingtoolbar',
		            store: storeApp,   // GridPanel使用相同的数据源
		            dock: 'bottom',
		            displayInfo: true,
		            plugins: Ext.create('Ext.ux.ProgressBarPager'),
		            emptyMsg: "没有记录" //没有数据时显示信息
		        }]
		    });
		  //加载数据  

		  storeApp.load({params:{start:0,limit:20}}); 
		    var agentwin = Ext.create('Ext.window.Window',{  
		              title : "查看代理项目",  
		              width : 350, 
		              height: 320,
		              //height : 120,  
		              //plain : true,  
		              autoScroll: true, 
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
		              items : [gridApps]
		           });  
		  agentwin.show();  
		 };  




</script>
</head>
<body>
</body>
</html>