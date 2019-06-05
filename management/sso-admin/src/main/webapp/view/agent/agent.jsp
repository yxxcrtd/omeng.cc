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
<title>代理商信息管理</title>
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
                   {header:'代理商ID',dataIndex:'id',sortable:true,fixed:false,hidden:true,align:'center'},
                   {header:'账号',dataIndex:'userName',sortable:true,fixed:false,align:'center'},
                   {header:'姓名',dataIndex:'realName',sortable:true,fixed:false,align:'center'},
                   {header:'开通时间',dataIndex:'createTime',sortable:true,fixed:false,align:'center'},
                   {header:'联系方式',dataIndex:'phone',sortable:true,fixed:false,align:'center'},
                   {header:'联系地址',dataIndex:'address',sortable:true,fixed:false,align:'center'},
           		   {header:'代理省份',dataIndex:'provinceDesc',sortable:true,fixed:false,align:'center'},
                   {header:'代理城市',dataIndex:'cityDesc',sortable:true,fixed:false,align:'center'},
                   {header:'代理类型',dataIndex:'userType',align:'center',renderer:function(value){  
                       if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>省代理</span>";  
                       } else if(value=='3'){  
                           return "<span style='color:red;font-weight:bold';>市代理</span>";  
                       }else if(value=='4'){
                    	   return "<span style='color:blue;font-weight:bold';>项目代理</span>";  
                       }else{
                           return "<span style='color:red;font-weight:bold';>公司员工</span>";
                       }
           		   }},
                   {header:'余额',dataIndex:'balance',sortable:true,fixed:false,align:'center'},
                   <cms:havePerm url='/agent/getAppByAgentId'>
                   {header:'代理项目',dataIndex:'id',align:'center',renderer:function(value,v,r){
                   if(r.data.userType==4){  
                           return "<a href='javascript:showApp("+value+")'>查看</a>";
                    }else{
                    return "<span style='color:red;font-weight:bold';></span>";
                    }
           		   }},</cms:havePerm>
                   {header:'省',dataIndex:'province',sortable:true,hidden:true,fixed:false,align:'center'},
                   {header:'市',dataIndex:'city',sortable:true,hidden:true,fixed:false,align:'center'}
               ];
  
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/agent/getAgent',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'userName'},  
           {name:'realName'}, 
           {name:'createTime'},  
           {name:'phone'}, 
           {name:'address'},  
           {name:'provinceDesc'},
           {name:'cityDesc'},
           {name:'province'},
           {name:'userType'},
           {name:'balance'},
           {name:'appName'},
           {name:'province'},
           {name:'city'}
        ]  
    });
    
    // app类型
				var store_appType = Ext.create("Ext.data.Store", {
					pageSize : 20, // 每页显示几条数据
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
					}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
					{
						name : 'app_name'
					} ]
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
    	var userName=Ext.getCmp('userName').getValue();
		var phone=Ext.getCmp('phone').getValue();
		var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
		var province=Ext.getCmp('province').getValue();
		var userType=Ext.getCmp('userType').getValue();
		var city=Ext.getCmp('city').getValue();
		var appType=Ext.getCmp('appType').getValue();
		var id;
        var new_params = { userName:userName,phone : phone,start_time:start_time,off_time:off_time,province:province,city:city,appType:appType,id:id,userType:userType};    
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
	        <cms:havePerm url='/agent/addAgentCharge'>
        	    	   { xtype: 'button', id:'charge', text: '充值/扣费',iconCls:'Chongzhi',
            	    	   listeners: {
            	    		   click:function(){
            	    			   agentCharge();
            	    		   }
            	    	   }},'-',</cms:havePerm>
	       <cms:havePerm url='/agent/deleteAgent'>
           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
	    	   listeners: {
	    		   click:function(){
	    			   delUser();
	    		   }
	    	   }}
           </cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'userName',name: 'userName',fieldLabel: '账号',labelAlign:'left',labelWidth:70},'-',
        	       { xtype: 'textfield',id:'phone',name: 'phone',fieldLabel: '联系方式',labelAlign:'left',labelWidth:65},'-',
        	       { xtype: 'datetimefield',id: 'start_time',name: 'start_time',fieldLabel: '开始时间',format : 'Y-m-d',labelAlign:'left',labelWidth:65},'-',
        	       { xtype: 'datetimefield',id: 'off_time',name: 'off_time',fieldLabel: '结束时间',format : 'Y-m-d',labelAlign:'left',labelWidth:65},'-',
				   ]},
				   {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{xtype : 'combobox',id : 'userType',name : 'userType',fieldLabel : '代理类型',editable:false,valueField : 'userType',value:'',displayField : 'userTypeName',
							store : comboStore,queryMode : 'local',labelAlign : 'left',labelWidth : 70},'-',	
        	{xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '代理省份',valueField : 'id',displayField : 'area',
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
				   {xtype : 'combobox',id : 'city',name : 'city',fieldLabel : '代理城市',valueField : 'id',displayField : 'area',
							store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 65},'-',		
				   {xtype : 'combobox',id : 'appType',name : 'appType',fieldLabel : 'app类型',valueField : 'app_type',displayField : 'app_name',
							store : store_appType,value:'',editable:false,queryMode : 'local',labelAlign : 'left',labelWidth : 65},'-',
					
				   <cms:havePerm url='/agent/getAgent'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			  var userName=Ext.getCmp('userName').getValue();
		                  var phone=Ext.getCmp('phone').getValue();
		                  var start_time=Ext.getCmp('start_time').getValue();
		          		  var off_time=Ext.getCmp('off_time').getValue();
		                  var province=Ext.getCmp('province').getValue();
		                  var city=Ext.getCmp('city').getValue();
		                  var userType=Ext.getCmp('userType').getValue();
		                  var appType=Ext.getCmp('appType').getValue();
    	    			  store.currentPage = 1;
    	    			  store.load({params:{start:0,page:1,limit:20,userName:userName,phone : phone,start_time:start_time,off_time:off_time,province:province,city:city,appType:appType,userType:userType}}); 
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
	store_appType.load();
 // 加载权限
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
   
    // 双击grid记录，编辑代理商信息
     
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data);
    });
	 
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	
	//删除系统代理商
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
        	 var disabled = records[i].raw.disabled;	
             if(disabled!=1){
          	   //非启用
          	     Ext.example.msg("提示","选择删除的记录必须是【禁用】数据！"); 
                 return ;  
             }
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
                     url:"/systemManager/deleteUser",  
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

    //************************************充值************************************
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
  	                    editable:false,
  	                    allowBlank:false,
  	                    labelAlign:'left'
  	                   },
		    			{
		    				// 输入金额
		    				xtype : 'numberfield',
		    				name : 'charge_money',
							allowBlank	:false,
							allowDecimals:false,               //不允许输入小数  
			                nanText:'请输入有效整数',           //无效数字提示  
			                allowNegative:false,
			                minValue:0,
			                maxValue:100000,
		    				fieldLabel : '金额'
		    				
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
		    
		    var rows = grid.getSelectionModel().getSelection();  
	        //user_id：所有选中的代理商Id的集合使用','隔开，初始化为空    
		    if(rows.length == 0)  
	        { 
		        Ext.example.msg("提示","请选择要充值的对象！");  
	            return ;  
	        }else if(rows.length > 1){
	        	Ext.example.msg("提示","请选择一个充值对象！");  
		        return ; 
	        }
	        charge_winForm.form.findField('agent_id').setValue(rows[0].get('id'));
		    charge_winForm.form.findField('agent_name').setValue(rows[0].get('userName'));
		    
		    var issuewindow = Ext.create('Ext.window.Window',{  
		              title : "充值/扣费",  
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
		                         text : "确定",  
		                         minWidth : 70,  
		                         handler : function() {  
		                          if(charge_winForm.form.findField('charge_type').getValue()=='2'){
		     if(rows[0].get('balance')<charge_winForm.form.findField('charge_money').getValue()){
		          Ext.example.msg("提示","余额不足！");   
	              return ; 
		     }
		    }
		                                         if (charge_winForm.getForm().isValid()) {  
		                            	           charge_winForm.getForm().submit({  
		                                          url :'/agent/addAgentCharge',
		                                          //等待时显示 等待  
		                                          waitTitle: '请稍等...',  
		                                          waitMsg: '正在提交信息...',  
		                                            
		                                          success: function(fp, o) {  
		       
		                                        	if(o.result.data==1){
		                                		        Ext.example.msg("提示",o.result.message); 
		              									issuewindow.close(); //关闭窗口  
		              									store.reload();
		              								}else if(o.result.data==2){
		              								    Ext.example.msg("提示",o.result.message); 
		              									issuewindow.close(); //关闭窗口  
		              									store.reload();
		              								} else{
		              								    Ext.example.msg("提示",o.result.message); 
		              								}
		                                                  
		                                              
		                                          },  
		                                          failure: function() {
		                                        	    Ext.example.msg("提示","操作失败，请稍后重试！"); 
		                                          }  
		                                       });  
		                            } else{
		                            	 Ext.example.msg("提示","请填写完整信息！"); 
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
    
    //************************************添加代理商信息start************************************
	//form表单
	var statusComboStore = new Ext.data.SimpleStore({
	    	fields:['name','value'],
	    	data:[['启用','0'],
	    	      ['禁用','1']
	    	]
	});
    
	// 地区信息
 	   
    
	var showform=function(d){  
	    var user_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 750, 
	                height:450,
		           // layout:'fit',
		            autoScroll: true, 
	                bodyPadding:5, 
	                hideBorders: false,
	                //renderTo:"panel21",  
	                fieldDefaults: {  
	                    labelAlign: 'left',  
	                    labelWidth: 90,  
	                    anchor: '90%'  
	                },  
	                items: [{  
	                    //显示文本框，相当于label  
	                    xtype: 'displayfield',   
	                    name: 'displayfield1',  
	                    value: '查看代理商相关信息'  
	                       },{
	        			xtype: 'fieldset',
	        			title: '代理商基本信息',
	        			collapsible: true,
	        			anchor: '98%',
	        			hideBorders: false,
	        			autoHeight: true,
	        			items: [
	        			          {
	        			              layout:'column',
	        			       		  border:false,
	        			              items:[{    
	        			                  layout:'form',
	        			                  anchor: '95%',
	        			                  columnWidth:.48,
	        			         		  border:false,
	        			                  items:[   
	        			     	                {
	    				                            xtype: 'hidden',
	    				                            name: "id"
	    			                             },{  
	        			    	                    //代理商账号  
	        			    	                    xtype: 'textfield',
	        			    	                    anchor: '97.5%',
	        			    	                    name: 'userName',  
	        			    	                    fieldLabel: '账号<span style="color:red;">*</span>',
	        			    	                    blankText: '必填字段',
	        			    	                    readOnly:true, 
	        			    	                    allowBlank: false 
	        			    	                },{  
	        			    	                    //手机号  
	        			    	                    xtype: 'textfield', 
	        			    	                    anchor: '97.5%',
	        			    	                    name: 'phone',
	        			    	                    blankText: '必填字段',
	        			    	                    fieldLabel: '手机号  <span style="color:red;">*</span>',
	        			    	                    regex: /^(1[3,5,8,7]{1}[\d]{9})|(((400)-(\d{3})-(\d{4}))|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{3,7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)$/,
	        			    	                   readOnly:true, 
	        			    	                    allowBlank: false 
	        			    	                }]
	        			              },{
	        			                  layout:'form',
	        			                  anchor: '97.5%',
	        			         		  border:false,
	        			                  columnWidth:.48,
	        			                  items:[{  
	        				                    //  真实姓名 
	        				                    xtype: 'textfield', 
	        				                    anchor: '97.5%',
	        				                    name: 'realName',  
	        				                    fieldLabel: '姓名 <span style="color:red;">*</span>',
	        				                    readOnly:true, 
	        				                    allowBlank: false 
	        				                    },{  
	        			    	                    //email 
	        			    	                    xtype: 'textfield', 
	        			    	                    anchor: '97.5%',
	        			    	                    name: 'email',  
	        			    	                    fieldLabel: 'Email<span style="color:red;">*</span>',       
	        			    	                    regex: /^([\w]+)(.[\w]+)*@([\w-]+\.){1,5}([A-Za-z]){2,4}$/, //输入内容合法的正则表达式  
	        			    	                    regexText: 'email格式不正确', //如果输入的文本不合法，提示文本  
	        			    	                    readOnly:true, 
	        			    	                    allowBlank: false  
	        			    	                }]            
	        			           } ]
	        			          },{  
			    	                    //appGroup hidden 
			    	                    xtype: 'textfield', 
			    	                    anchor: '97.5%',
			    	                    name: 'appIds', 
			    	                    hidden: true,
			    	                    readOnly:true, 
			    	                    fieldLabel: 'appIds'  
			    	                },{  
	        		                	 xtype: 'textareafield',   
	        			                 name: 'remark',
	        			                 fieldLabel: '备注', 
	        			                 readOnly:true,   
	        			                 value: '' 
	        		                }
	        			          ]   
	                },{
	        			xtype: 'fieldset',
	        			title: '代理商状态及类型',
	        			collapsible: true,
	        			anchor: '98%',
	        			autoHeight: true,
	        			items: [
	        			          {
	        			              layout:'column',
	        			       		  border:false,
	        			              items:[{    
	        			                  layout:'form',
	        			                  border:false,
	        			                  anchor: '95%',
	        			                  columnWidth:.48,
	        			                  items:[   
                                                {
	        			    						xtype:'combo',
	        			    						store : statusComboStore,
	        			    			    		width:80,
	        			    			    		name:'disabled',
	        			    			   			triggerAction: 'all',
	        			    			   			displayField: 'name',
	        			    			   			valueField: 'value',
	        			    			   			hiddenName:'disabled',
	        			    	                    typeAhead: true,
	        			    	                    mode: 'remote',
	        			    			   			value:'0',
	        			    						//mode:'local',
	        			    					   	editable: false,
	        			    	    		    	allowBlank: false,
	        			    	    		    	readOnly:true, 
	        			    						fieldLabel: '状态'  
	        			    					   }]
	        			              },{
	        			                  layout:'form',
	        			                  anchor: '97.5%',
	        			                  border:false,
	        			                  columnWidth:.48,
	        			                  items:[{
	        			    						xtype:'combo',
	        			    						store : comboStore,
	        			    			    		width:80,
	        			    			    		name:'userType',
	        			    			   			triggerAction: 'all',
	        			    			   			displayField: 'userTypeName',
	        			    			   			valueField: 'userType',
	        			    			   			hiddenName:'userType',
	        			    			   			value:'1',
	        			    						mode:'local',
	        			    					   	editable: false,
	        			    	    		    	allowBlank: false,
	        			    						fieldLabel: '代理商类型',
	        			    						readOnly:true, 
	        			    						listeners : { // 监听该下拉列表的选择事件
														select : function(combobox,record,index) {
															var v = combobox.value;
															if(v==4){
																// 项目代理
																user_winForm.form.findField('appGroup').show();
															}else{
																user_winForm.form.findField('appGroup').hide();
															}
														}
													},
													queryMode : 'local',
	        			    					   }]            
	        			           } ]
	        			          }
	        			          ]   
	        			        
	                },{
	        			xtype: 'fieldset',
	        			title: '代理商代理信息',
	        			collapsible: true,
	        			anchor: '98%',
	        			autoHeight: true,
	        			items: [
	        			          {
	        			              layout:'column',
	        			       		  border:false,
	        			              items:[{    
	        			                  layout:'form',
	        			                  anchor: '95%',
	        			         		  border:false,
	        			                  columnWidth:.48,
	        			                  items:[
	        			                        {
													xtype : 'combobox',
													name : 'province',
													fieldLabel : '省',
													valueField : 'id',
													hiddenName:'id',
													displayField : 'area',
													store : provinceStore,
													listeners : { // 监听该下拉列表的选择事件
														select : function(combobox,record,index) {
															//Ext.getCmp('city').setValue('');
															user_winForm.form.findField('city').setValue('');
															cityStore.load({
																		params : {
																			parentId : combobox.value
																		}
																	});
														}
													},
													queryMode : 'local',
													labelAlign : 'left',
													readOnly:true, 
												    anchor: '97.5%'
												}]
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
													queryMode : 'local',
													readOnly:true, 
													labelAlign : 'left'
												}]            
	        			           } ]
	        			          },  
  				                    //  代理项目 
  				                    {
  				                        xtype: 'checkboxgroup',
  				                        name: 'appGroup',
  				                        itemCls: 'x-check-group-alt',
  				                        columns: 6,
  				                        fieldLabel: '代理项目',
  				                        align: 'left',
  				                        border: true,
  				                        anchor: '97.5%',
  				                        flex: 1,
  				                        readOnly:true, 
  				                        listeners: {
  				                            render: function (view, opt) {
  				                                loadAppCheckGroup();
  				                            }
  				                        }
  				                    }
	        			          ]   
	        			        
	                },{
	        			xtype: 'fieldset',
	        			title: '代理商银行开户信息',
	        			collapsible: true,
	        			anchor: '98%',
	        			hideBorders: false,
	        			autoHeight: true,
	        			items: [
	        			         {  
  				                    //  开户行 
  				                    xtype: 'textfield', 
  				                    anchor: '97.5%',
  				                    name: 'accountBank', 
  				                    readOnly:true,  
  				                    fieldLabel: '开户行 '  
  				                  }, {  
    				                    //  开户名 
    				                    xtype: 'textfield', 
    				                    anchor: '97.5%',
    				                    name: 'accountName',  
    				                    readOnly:true, 
    				                    fieldLabel: '开户名 '  
    				              }, {  
    	  				                    //  卡号 
    	  				                    xtype: 'textfield', 
    	  				                    anchor: '97.5%',
    	  				                    name: 'accountNumber',
    	  				                    readOnly:true,  
    	  				                    fieldLabel: '卡号 '  
    	  				         }
	        			          ]   
	        			        
	                }
	                ]   
	    });     
	    
	    
	      //加载操作全部名称
	      function loadAppCheckGroup() {
	          //通过extjs的ajax获取操作全部名称
	          var userId ;
	          if(d!=null&&d.id!=null){
	        	  userId = d.id;
	          }
	          Ext.Ajax.request({
	              url: '/common/showApp?userId='+userId,
	              method:'post', 
	              success: function (response) {
	            	  var o = eval("(" + response.responseText + ")");
	                  var len = o.data.length;//obj.data.length; "Table"这里的Table指的是后台返回 类似于data
	                  if (o.data == null || len == 0) {
	                      return;
	                  }
	                  var checkboxgroup =user_winForm.form.findField('appGroup');
	                  for (var i = 0; i < len; i++) {
	                      var checkbox = new Ext.form.Checkbox(
	                        {
	                            boxLabel: o.data[i].app_name,
	                            name: o.data[i].app_type,
	                            inputValue: o.data[i].id,
	                            checked: o.data[i].checked
	                        });
	                      checkboxgroup.items.add(checkbox);
	                  }
	                  user_winForm.doLayout(); //重新调整版面布局  
	              }
	          });
	      };
var rows = grid.getSelectionModel().getSelection();
					// user_id：所有选中的代理商Id的集合使用','隔开，初始化为空
					if (rows.length == 0) {
                 	    Ext.example.msg("提示","请选择要编辑的对象！"); 
						return;
					}
					 if(rows.length > 1)  
				       {  
	                 	  Ext.example.msg("提示","只能选择一个编辑的对象！"); 
				          return ;  
				       }
		    var title = '新增账号';
		    var reqName = 'addUser';
		    //================判断是编辑还是新增===============
		    	if(d!=null&&d.id!=null&&d.id!=0){
		    		// 是编辑状态
		    		provinceStore.load();
		            cityStore.load({params : {parentId : d.province}});
		    	    user_winForm.form.findField('id').setValue(d.id);
		    	    user_winForm.form.findField('userName').setValue(d.userName);
		     	    user_winForm.form.findField('phone').setValue(d.phone);
		    	   
		     	    user_winForm.form.findField('realName').setValue(d.realName);
		    	    user_winForm.form.findField('email').setValue(rows[0].raw.email); 
		    	   
		     	    user_winForm.form.findField('disabled').setValue(rows[0].raw.disabled+'');
		    	    var uType = rows[0].raw.userType+'';
		    	    user_winForm.form.findField('userType').setValue(rows[0].raw.userType+'');
		    	    if(uType=='4'){
		    	    	// 项目代理
		    	    	user_winForm.form.findField('appGroup').show();
		    	    }else{
		    	    	user_winForm.form.findField('appGroup').hide();
		    	    }
		     	    user_winForm.form.findField('province').setValue(d.province);
		    	    user_winForm.form.findField('city').setValue(d.city); 
		    	    user_winForm.form.findField('accountBank').setValue(rows[0].raw.accountBank);
		     	    user_winForm.form.findField('accountName').setValue(rows[0].raw.accountName);
		    	    user_winForm.form.findField('accountNumber').setValue(rows[0].raw.accountNumber); 
		    	    user_winForm.form.findField('remark').setValue(rows[0].raw.remark); 
		    	    user_winForm.form.findField('userName').setReadOnly(true);
		        	title = '详情';
		        	reqName = 'editUser';
		    	}else{
		    		provinceStore.load();
		            //cityStore.load({params : {parentId : 0}});
		    	}
		

	        //================判断是编辑还是新增===============
	        			        
	    //创建 window面板，表单面板是依托window面板显示的  
	    var syswin = Ext.create('Ext.window.Window',{  
	              title : title,  
	              width : 800, 
	              height: 450,
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
	              layout:'fit',
	              autoScroll: true,  
	              buttonAlign : "center",  
	              bodyStyle : "padding:0 0 0 0",  
	              items : [user_winForm],  
	              buttons : [{  
	                         text : "关闭",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            syswin.close();  
	                         }  
	                     }]  
	           });  
	    syswin.show(); 
	    };
	    
	   
	    //************************************添加代理商信息end************************************
    
    
});

//************************************查看代理app************************************ 
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
  		
  		//点击下一页时传递搜索框值到后台  
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
//  		              height: 500,
  		              height : 320,  
  		              //plain : true,  
  		              iconCls : "addicon",  
  		              // 不可以随意改变大小  
  		              resizable : false,  
  		              autoScroll: true, 
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