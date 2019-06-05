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
<title>服务商列表</title>
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
                   {header:'服务商ID',dataIndex:'id',type: 'string',hidden:true,align:'center'},
                   {header:'服务商名称',dataIndex:'name',align:'center'},
                   {header:'注册号码',dataIndex:'telephone',align:'center'},
                   {header:'省份',dataIndex:'province',align:'center'},
                   {header:'城市',dataIndex:'city',align:'center'},
                   {header:'认证类型',dataIndex:'auth_type',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>企业认证</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>个人认证</span>";  
                       }else{
                    	   return "<span style='font-weight:bold';>未认证</span>";  
                       }
           	       }},
                   {header:'认证状态',dataIndex:'auth_status',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:red;font-weight:bold';>未通过</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>已认证</span>";  
                       }else if(value=='2'){
                    	   return "<span style='color:blue;font-weight:bold';>待认证</span>";  
                       }else if(value=='3'){
                    	   return "<span style='color:black;font-weight:bold';>已取消</span>";  
                       }else{
                    	   return "<span style='font-weight:bold';>空</span>";  
                       }
           		   }},
                   {header:'开店时间',dataIndex:'join_time',align:'center'},
                   {header:'行业类型',dataIndex:'app_name',align:'center'},
                   {header:'地址',dataIndex:'address',align:'center'},
                   {header:'经度',dataIndex:'longitude',align:'center'},
                   {header:'纬度',dataIndex:'latitude',align:'center'},
                   {header:'邀请码',dataIndex:'invitation_code',align:'center'},
                   {header:'代金券',dataIndex:'vouchers',align:'center',renderer:function(value,v,r){  
                     return '<a href="javascript:showVouchers(\''+r.data.id+'\')"><span style="color:red;font-weight:bold";>'+value+'</span></a>';
           		  }},
           	      {header:'收入详情',dataIndex:'vouchers',align:'center',renderer:function(value,v,r){  
                    return '<a href="javascript:showPayDetail(\''+r.data.id+'\')"><span style="color:red;font-weight:bold";>收入详情</span></a>';
       		      }},
           		  {header:'操作',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
           			var name='';
           			if(r.data.name==''||r.data.name==null){
             		   name='**'; 
             	    }else{
             	     name =r.data.name.replace('"', '').replace("'", "");
             	    }
           			  return '<a href="javascript:showDetail(\''+r.data.id+'\',\''+name+'\')">详情</a>';
           		}},
       		   {header:'状态',dataIndex:'is_del',align:'center',hidden:true,renderer:function(value){  
                   if(value=='0'){  
                       return "<span style='color:green;font-weight:bold';>未删除</span>";  
                   } else if(value=='1'){  
                       return "<span style='color:red;font-weight:bold';>已删除</span>";  
                   }
       		   }},  
               ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/merchants/getMerchantsInfoList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id',type:'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'name'}, 
           {name:'telephone'}, 
           {name:'auth_type'},
           {name:'auth_status'},  
           {name:'join_time'},
           {name:'address'},
           {name:'app_name'},
           {name:'province'},
           {name:'city'},
           {name:'orderStatuName'},
           {name:'invitation_code'},
           {name:'vouchers'},
           {name:'is_del'},
           {name:'longitude'},
           {name:'latitude'}
        ]  
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
	var store_couponsType = Ext.create("Ext.data.Store", {
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
	// 银行类型
	var store_withdraw = Ext.create("Ext.data.Store", {
		pageSize : 20, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/showWithdraw',
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
			name : 'dict_value'
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
			}]
		});
	 var authTypeStore = new Ext.data.SimpleStore({
     	fields:['statusName','auth_type'],
     	data:[['全部',''],
     	      ['未认证','0'],
     	      ['企业认证','1'],
     	      ['个人认证','2']
     	]
     });
	 var authStatusStore = new Ext.data.SimpleStore({
	     	fields:['statusName','auth_status'],
	     	data:[['全部',''],
	     	      ['空','null'],
	     	      ['未通过','0'],
	     	      ['已认证','1'],
	     	      ['待认证','2'],
	     	      ['已取消','3']
	     	]
	     }); 
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var name=Ext.getCmp('name').getValue();
    	 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
		 var auth_type=Ext.getCmp('auth_type').getValue();
		 var auth_status=Ext.getCmp('auth_status').getValue();
		 var province=Ext.getCmp('province').getRawValue();
		 var city=Ext.getCmp('city').getRawValue();
		 var app_type=Ext.getCmp('app_type').getValue();
		 var telephone=Ext.getCmp('telephone').getValue();
		 var invite_code=Ext.getCmp('invite_code').getValue();
		 var agentId='';
		 var new_params = { name:name,auth_type:auth_type,auth_status:auth_status,start_time : start_time,off_time : off_time,province:province,city:city,agentId:agentId,app_type:app_type,telephone:telephone,invite_code:invite_code};    
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
        	       <cms:havePerm url='/merchants/editMerchants'>
                   { xtype: 'button', id:'edit', text: '编辑',iconCls:'Edit',
        	    	   listeners: {
        	    		   click:function(){
        	    			   editform();
        	    		   }
        	    	   }},'-',</cms:havePerm>
        	    	   <cms:havePerm url='/merchants/deleteStore'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	    	   }},'-',</cms:havePerm>
        	    	   <cms:havePerm url='/merchants/exportExcel'>
	        	       { xtype: 'button',id:'export', text: '导出',iconCls:'Daochu',
	            	    	   listeners: {
	            	    		   click:function(){
	            	    			   exportAll();
	            	    		   }
	            	    	   }
	                   },'-',</cms:havePerm>
	                   <cms:havePerm url='/merchants/rebuildMerchantIndex'>
	        	       { xtype: 'button', text: '更新索引',iconCls:'Daochu',
	            	    	   listeners: {
	            	    		   click:function(){
	            	    			   rebuildMerchant();
	            	    		   }
	            	    	   }
	                   },'-',</cms:havePerm>
        	    	   <cms:havePerm url='/merchants/downloadExcel'>
	        	       { xtype: 'button', text: '下载',iconCls:'Daochu',
	            	   	   listeners: {
	            	    		   click:function(){
	            	    			   download();
	            	    		   }
	            	    	   }
	                   }</cms:havePerm>
                  
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id: 'name',name: 'name',fieldLabel: '服务商名称',labelAlign:'left',value:'${name}',labelWidth:70},'-',
        	       { xtype: 'textfield',id: 'invite_code',name: 'invite_code',fieldLabel: '邀请码',labelWidth:55,value:'${invite_code}'},'-',
        	       {xtype : 'combobox',id : 'auth_type',name : 'auth_type',fieldLabel : '认证类型',value:'',valueField : 'auth_type',editable:false,displayField : 'statusName',
				   store : authTypeStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
				   {xtype : 'combobox',id : 'auth_status',name : 'auth_status',fieldLabel : '认证状态',value:'${auth_status}',valueField : 'auth_status',editable:false,displayField : 'statusName',
				         store : authStatusStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
				   
				   { xtype: 'datetimefield',id: 'start_time',name: 'start_time',fieldLabel: '开店时间',format : 'Y-m-d',value:'${start_time}',editable:false,labelAlign:'left',labelWidth:60},'～',
        	       { xtype: 'datetimefield',id: 'off_time',name: 'off_time',format : 'Y-m-d',value:'${off_time}',labelAlign:'left',labelWidth:60}
        	      	]
							},	{
												xtype : 'toolbar',
												dock : 'top',
												displayInfo : true,
												items : [ 
					{ xtype: 'textfield',id: 'telephone',name: 'telephone',fieldLabel: '注册号码',labelAlign:'left',labelWidth:70},'-',
        	      	{xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省份',valueField : 'id',displayField : 'area',
							store : provinceStore,
							listeners : { // 监听该下拉列表的选择事件
																select : function(combobox,record,index) {
																	Ext.getCmp('city')
																			.setValue('');
																	cityStore.load({params : {
																					parentId : combobox.value
																				}
																			});
																	
																}
															},
							queryMode : 'local',labelAlign : 'left',labelWidth : 55},'-',
				   {xtype : 'combobox',id : 'city',name : 'city',fieldLabel : '城市',valueField : 'id',displayField : 'area',
							store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',	
							 {xtype : 'combobox',id : 'app_type',name : 'appType',fieldLabel : '行业类型',value:'',valueField : 'app_type',displayField : 'app_name',
								store : store_appType,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
							<cms:havePerm url='/merchants/getMerchantsInfoList'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var name=Ext.getCmp('name').getValue();
    	    			   var start_time=Ext.getCmp('start_time').getValue();
    	    			   var off_time=Ext.getCmp('off_time').getValue();
    	    			   var auth_type=Ext.getCmp('auth_type').getValue();
    	    			   var auth_status=Ext.getCmp('auth_status').getValue();
    	    			   var province=Ext.getCmp('province').getRawValue();
		                   var city=Ext.getCmp('city').getRawValue();
		                   var app_type=Ext.getCmp('app_type').getValue();
		                   var telephone=Ext.getCmp('telephone').getValue();
		                   var invite_code=Ext.getCmp('invite_code').getValue();
		                   var agentId='';
		                 
		                   if(off_time==null){
		                	   
		                   }else{
		                   if(start_time>off_time){
		                	   Ext.example.msg("提示","开始时间大于结束时间！");
		                	   return;
		                   }
		                   }
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,name:name,auth_type:auth_type,auth_status:auth_status,start_time : start_time,off_time : off_time,province:province,city:city,agentId:agentId,app_type:app_type,telephone:telephone,invite_code:invite_code}}); 
    	    		   }
    	    		   }}</cms:havePerm>
                  ]
        }],
        bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
            displayInfo : true,
            store : store
        })
    });
    
     function sleepTime(){
    	 Ext.getCmp('export').setDisabled(true);
    	 setTimeout(function(){
    		Ext.getCmp('export').setDisabled(false);
    	}, 30000);//js定时器
      } 
    
    function paramLoad(){
    	
    	if('${province}'!=''){
    		Ext.getCmp('province').setValue(${province});
    		if('${city}'!=''){
    			cityStore.load({params : {
					parentId : '${province}'
				}
			});
    			Ext.getCmp('city').setValue(${city});
    		}
    		
    		store.load({params:{start:0,limit:20,province:'${provinceDesc}',city:'${cityDesc}'}}); 
    	}else{
    		store.load({params:{start:0,limit:20}}); 
    	}
    }

    //加载数据  
    provinceStore.load();
    paramLoad();
    
    store_appType.load();
    store_withdraw.load();
    authTypeStore.load();
   
   
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    <cms:havePerm url='/merchants/editMerchants'>
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
	
	//商户删除
	 function delUserAll()  
     {  
          //grid中复选框被选中的项  
            
          var rows = grid.getSelectionModel().getSelection();  
          //user_id：所有选中的服务商Id的集合使用','隔开，初始化为空    
          var user_id = ''; 
          var phone='';
          var appType='';
          for(var i = 0;i<rows.length;i++)  
          {  
             if(i>0)  
             {  
                 user_id = user_id+','+rows[i].get('id');  
                 phone = phone+','+rows[i].get('telephone'); 
                 appType = appType+','+rows[i].raw.app_type; 
                
             }else{  
                 user_id = user_id+rows[i].get('id');
                 phone = phone+rows[i].get('telephone');
                 appType = appType+rows[i].raw.app_type; 
             }  
          }  
          //没有选择要执行操作的对象  
            
          if(user_id == "")  
          {  
           	 Ext.example.msg("提示","请选择要删除的对象！");
             return ;  
          }else{  
             Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
                 if(btn == 'yes')  
                 {  
                     Ext.Ajax.request({  
                         url:"/merchants/deleteStore",  
                         params:{id:user_id,phone:phone,appType:appType},  
                         method:'post',  
                         success:function(o){  
                           	 Ext.example.msg("提示","服务商删除成功！");
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                          	 Ext.example.msg("提示","服务商删除失败！");
                         }  
                     });    
                 }  
             });  
          }  
     } ;
     
     
   //更新商户索引
	 function rebuildMerchant()  
     {  
         Ext.Msg.confirm("提示信息","请确定要更新商户索引?",function (btn){  
            if(btn == 'yes')  
               {  
                     Ext.Ajax.request({  
                         url:"/merchants/rebuildMerchantIndex",  
                         params:{},  
                         method:'post',  
                         success:function(o){  
                           	 Ext.example.msg("提示","更新成功！");
                             return ;  
                         },  
                         failure:function(form,action){ 
                          	 Ext.example.msg("提示","更新失败！");
                         }  
                     });    
                 }  
             });  
           
     } ;
     

   //导出所有商户
		function exportAll() {
			   var name=Ext.getCmp('name').getValue();
			   var start_time=Ext.getCmp('start_time').getValue();
			   var off_time=Ext.getCmp('off_time').getValue();
			   var auth_type=Ext.getCmp('auth_type').getValue();
			   var auth_status=Ext.getCmp('auth_status').getValue();
			   var province=Ext.getCmp('province').getRawValue();
               var city=Ext.getCmp('city').getRawValue();
               var app_type=Ext.getCmp('app_type').getValue();
               var telephone=Ext.getCmp('telephone').getValue();
               var invite_code=Ext.getCmp('invite_code').getValue();
               var agentId='';
               window.location.href = '/merchants/exportExcel?name='+name+'&start_time='+start_time+'&off_time='+off_time
				+'&auth_type='+auth_type+'&auth_status='+auth_status+'&province='+province+'&city='+city+'&app_type='+app_type+'&telephone='+telephone+'&agentId='+agentId+'&invite_code='+invite_code;
               
               Ext.example.msg("提示","正在导出报表，请稍后！");
               sleepTime();
   };
		  //下载所有商户运营信息
		function download() {
			definprompt();
				};
	//-------------------start---------------------
				var definprompt=function(){
				 	var formPanel = new Ext.form.FormPanel({
				 		   autoWidth:true,
				 		   layout:"form",
				 		   frame:true,
				 		   labelWidth:75,
				 		   labelAlign:"right",
				 		   items:[{
				 		    xtype:"label",
				 		    height  : 20,
				 		    text :"请输入时间:"
				 		   },{
			 					xtype : 'datefield',
			 					name: 'head_date',
			 					format : 'Y-m',
			 					labelAlign:'left',				
			 					
			 				}],  
				 		   buttons : [{
				 		    text : '确定',
				 		    handler : function(){
				 		    
			                    var head_date=formPanel.form.findField('head_date').getValue();
				              
				              	Ext.Ajax.request({  
			                         url:"/merchants/downloadExcel",  
			                         method:'post',  
			                         params:{time:head_date},  
			                         success:function(o){
			                        	 win.close();
			                        	 var hrefadd=o.responseText;
			                        	 if(hrefadd==''){
			                        		 Ext.example.msg("提示","代理商才有下载权限");
			                        		 return;
			                        	 }else if(hrefadd=='1'){
			                        		 Ext.example.msg("提示","请选择小于当前月，并且大于6月的时间");
			                        		 return;
			                        	 }else{
			                        		 window.location.href = o.responseText; 
			                        	 }
			                        	 
			                         },  
			                         failure:function(form,action){ 
			                          	 Ext.example.msg("提示","下载失败！");
			                         }  
			                     });    
				 		    }
				 		   }, {
				 		    text : '取消',
				 		    handler : function(){
				 		    formPanel.close();
				 		     win.close();
				 		    }
				 		   }]
				 		  });
				 		  var win = new Ext.Window({
				 		   title:"输入框",
				 		   modal:true,
				 		   width:250,
				 		   height:150,
				 		   collapsible:false,
				 		   resizable:false,
				 		   closeAction:'hide',
				 		   items:[formPanel]
				 		  });
				 		  
				 		    win.show();
				           };
				  	     
	//*********************下载报表end*********************
				
   //form表单编辑服务商
 	var editform=function(){  
 	    var edit_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields', 
 	                width: 420,
 	                height:345,
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
// 	                    fieldLabel: 'Display field',  
 	                    value: '修改服务商相关信息'  
 	                     
 	                },{  
 	                    //输入服务商ID
 	                    xtype: 'textfield', 
 	                    name: 'id', 
 	                    hidden:true,
 	                    fieldLabel: '服务商ID'  
 	                }, {  
 	                    //输入服务商名称
 	                    xtype: 'textfield', 
 	                    name: 'name',
 	                    allowBlank	:false,
 	                    fieldLabel: '服务商名称'  
 	                }, {  
 	                    //输入联系方式
 	                    xtype: 'textfield',  
 	                    name: 'telephone',  
 	                    fieldLabel: '注册号码', 
 	           		    readOnly:true
 	                    //value: '输入文本框',  
 	                    
 	                },{  
 	                    //输入地理位置
 	                    xtype: 'textfield',  
 	                    name: 'address',
 	           		    readOnly:true,
 	                    fieldLabel: '地理位置'  
 	                    //value: '输入文本框',  
 	                  
 	                },
 	               {  
 	                    //输入经度
 	                    xtype: 'textfield',  
 	                    name: 'longitude',
 	           		    readOnly:true,
 	                    fieldLabel: '经度'  
 	                    //value: '输入文本框',  
 	                  
 	                },
 	               {  
 	                    //输入纬度
 	                    xtype: 'textfield',  
 	                    name: 'latitude',
 	           		    readOnly:true,
 	                    fieldLabel: '纬度'  
 	                    //value: '输入文本框',  
 	                  
 	                },
 	               {
						xtype : 'combobox',
						name : 'app_type',
						fieldLabel : 'app类型',
						valueField : 'app_type',
						displayField : 'app_name',
						width : 200,
						store : store_appType,
						listeners : { // 监听该下拉列表的选择事件
							select : function(combobox,record,index) {
							
								edit_winForm.form.findField('service_type').setValue('');
								
								store_couponsType.load({
											params : {
												app : combobox.value
											}
										});
							}
						},
						editable:false,
						value: '',
						readOnly:true,
						queryMode : 'local'
					},
					{
						xtype : 'combobox',
						name : 'service_type',
						width : 200,
						fieldLabel : '服务类型',
						valueField : 'service_type',
						displayField : 'service_name',
						store : store_couponsType,
						editable:false,
						readOnly:true,
						value: '',
						queryMode : 'local'
					},
					{
						xtype : 'combobox',
						name : 'withdraw',
						width : 200,
						fieldLabel : '银行类型',
						valueField : 'id',
						displayField : 'dict_value',
						store : store_withdraw,
						editable:false,
						value: '',
						readOnly:true,
						queryMode : 'local'
					},
 	               {  
 	                    //输入开户行账号
 	                    xtype: 'textfield',  
 	                    name: 'withdraw_no', 
 	                    value: '',
 	           		    readOnly:true,
 	                    fieldLabel: '开户行账号'
 	                },{  
 	                    //输入认证信息
 	                    xtype: 'textfield',  
 	                    name: 'auth_status',  
 	                    fieldLabel: '认证状态',
 	                    readOnly:true
 	                }]  
 	            });  
 	    //创建window面板，表单面板是依托window面板显示的  
 	    
 	    var rows = grid.getSelectionModel().getSelection();  
         //user_id：所有选中的服务商Id的集合使用','隔开，初始化为空    
 	    if(rows.length == 0)  
         {  
 	    	 Ext.example.msg("提示","请选择要编辑的对象！");
             return ;  
         }
 	   if(rows.length > 1)  
       {  
 		  Ext.example.msg("提示","只能选择一个编辑的对象！");
          return ;  
       }
 	  var auth_status;
 	  if(rows[0].get('auth_status')=='0'){  
 		 auth_status='未通过 ';
       } else if(rows[0].get('auth_status')=='1'){  
    	   auth_status='已认证';  
       }else if(rows[0].get('auth_status')=='2'){
    	   auth_status='待认证';
       }else if(rows[0].get('auth_status')=='3'){
    	   auth_status='已取消';
       }else{
    	   auth_status='空';  
       }
         edit_winForm.form.findField('id').setValue(rows[0].get('id'));  
         edit_winForm.form.findField('name').setValue(rows[0].get('name'));
         edit_winForm.form.findField('telephone').setValue(rows[0].raw.telephone);
         edit_winForm.form.findField('address').setValue(rows[0].raw.address);
         edit_winForm.form.findField('auth_status').setValue(auth_status);
         edit_winForm.form.findField('longitude').setValue(rows[0].get('longitude'));
         edit_winForm.form.findField('latitude').setValue(rows[0].get('latitude'));
         edit_winForm.form.findField('app_type').setValue(rows[0].raw.app_type);
         store_couponsType.load({
				params : {
					app : rows[0].raw.app_type
				}
			}); 
 	    var editwindow = Ext.create('Ext.window.Window',{  
 	              title : "编辑服务商",  
 	              width: 450,
	               height:425,  
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
 	              autoScroll: true, 
 	              // 弹出模态窗体  
 	              modal : 'true',  
 	              buttonAlign : "center",  
 	              bodyStyle : "padding:0 0 0 0",  
 	              items : [edit_winForm],  
 	              buttons : [{  
 	                         text : "保存",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	if(validForm()){
 	                            if (edit_winForm.getForm().isValid()) {  
 	                            	edit_winForm.getForm().submit({  
 	                                          url :'/merchants/editMerchants',  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',                                    
 	                                          success: function(fp, o) {  
 	                                              if (o.result== true) {
 	                                        		  Ext.example.msg("提示","修改成功！");
 	                                                  edit_winForm.form.findField('service_type').setValue('');
 	                                                  editwindow.close(); //关闭窗口  
 	                                                  store.reload();  
 	                                                  grid.getSelectionModel().clearSelections();
 	                                              }else {  
 	                                       		      Ext.example.msg("提示","店铺名称重名,修改失败！");
 	                                              }  
 	                                          },  
 	                                          failure: function() { 
 	                                             Ext.example.msg("提示","店铺名称重名,修改失败！");
 	                                          }  
 	                                       });  
 	                            }  
 	                         } }
 	                     }, {  
 	                         text : "关闭",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	 editwindow.close();  
 	                         }  
 	                     }
 	                     
 	                     ]  
 	           });  
 	    editwindow.show();  
 	    
 	   
 	    
 	    
 	      var validForm=function(){
          var withdraw_no=edit_winForm.form.findField('withdraw_no').getValue();
          var withdraw=edit_winForm.form.findField('withdraw').getValue();	
          var app_type=edit_winForm.form.findField('app_type').getValue();
          var service_type=edit_winForm.form.findField('service_type').getValue();
          if(withdraw_no!=''&&withdraw==''){
              Ext.example.msg("提示","请选择银行类型！");
        	  return false;
          }
          if(withdraw!=''&&withdraw_no==''){
        	  Ext.example.msg("提示","请填写银行帐号！");
        	  return false;
          }
          if(service_type!=''&&app_type==''){
        	  Ext.example.msg("提示","请选择app类型！");
        	  return false;
          }
          return true;
 	    };
 	    };
 	   
 		 
 		
});

//查看商户启用的代金券
  		var showVouchers=function(merchantsid){ 
  			var columns = [
  			    {xtype : 'rownumberer'},
  				{header : '服务类型',dataIndex : 'service_type_name'},
  				{header : '代金券金额',dataIndex : 'price'}
  			];
  		//列表展示数据源
  			var storeVouchers = Ext.create("Ext.data.Store", {
  				pageSize : 20, //每页显示几条数据  
  				proxy : {
  					type : 'ajax',
  					url : '/vouchers/getVouchersByMerId',
  					reader : {
  						type : 'json',
  						totalProperty : 'total',
  						root : 'data',
  						idProperty : 'id'
  					}
  				},
  		        fields:[   //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
  		           {name:'service_type_name'},  
  		           {name:'price'}
  		        ]  
  		    });
  			storeVouchers.on('beforeload', function (storeVouchers, options) {    
		        var new_params = {merchantsid:merchantsid };    
		        Ext.apply(storeVouchers.proxy.extraParams, new_params);    
		    }); 
  		    var sm = Ext.create('Ext.selection.CheckboxModel');
  		    var gridVouchers = Ext.create("Ext.grid.Panel",{
  		    	region: 'center',
  		    	border: false,
  		    	store: storeVouchers,
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
  		            store: storeVouchers,   // GridPanel使用相同的数据源
  		            dock: 'bottom',
  		            displayInfo: true,
  		            plugins: Ext.create('Ext.ux.ProgressBarPager'),
  		            emptyMsg: "没有记录" //没有数据时显示信息
  		        }]
  		    });
  		  //加载数据  

  		  storeVouchers.load({params:{start:0,limit:20,merchantsid:merchantsid}}); 
  		    var voucherwin = Ext.create('Ext.window.Window',{  
  		              title : "查看代金券",  
  		              width : 700, 
  		             
  		             height : 400,  
  		              //plain : true,  
  		              iconCls : "addicon",  
  		              // 不可以随意改变大小  
  		              resizable : false, 
  		              autoScroll:true, 
  		              // 是否可以拖动  
  		              // draggable:false,  
  		              collapsible : true, // 允许缩放条  
  		              closeAction : 'close',  
  		              closable : true,  
  		              // 弹出模态窗体  
  		              modal : 'true',  
  		              buttonAlign : "center",  
  		              bodyStyle : "padding:0 0 0 0",  
  		              items : [gridVouchers]
  		           });  
  		  voucherwin.show();  
};

//查看商户收入明细
	var showPayDetail=function(merchantsid){ 
		var columns = [
		    {xtype : 'rownumberer'},
		    {header : '订单号',dataIndex : 'order_no',renderer:function(value,v,r){  
       			var payment_type=r.data.payment_type;
       			if(payment_type=='0'){
       				return "<span style='color:red;font-weight:bold';>"+value+"</span>";
         	    }else{
         	    	return "<span style='color:red;font-weight:bold';></span>";
         	    }
       		}},
			{header:'收支类型',dataIndex:'payment_type',renderer:function(value){  
                if(value=='0'){  
                    return "<span style='color:red;font-weight:bold';>订单收入</span>";  
                } else if(value=='1'){  
                    return "<span style='color:green;font-weight:bold';>提现</span>";  
                }else if(value=='2'){
             	   return "<span style='color:blue;font-weight:bold';>红包收入</span>";  
                }else if(value=='3'){
             	   return "<span style='color:black;font-weight:bold';>粉丝奖</span>";  
                }else if(value=='4'){
             	   return "<span style='color:black;font-weight:bold';>城市人气服务商奖</span>";  
                }else if(value=='5'){
             	   return "<span style='color:black;font-weight:bold';>订单奖励</span>";  
                }else{
             	   return "<span style='font-weight:bold';>其他</span>";  
                }
    		   }},
			{header : '金额',dataIndex : 'payment_price'},
			{header:'支付类型',dataIndex:'order_pay_type',align:'center',renderer:function(value,v,r){  
       			var payment_type=r.data.payment_type;
       			if(payment_type=='0'){
       				if(value=='1'){  
                        return "<span style='color:green;font-weight:bold';>支付宝支付</span>";  
                    } else if(value=='2'){  
                        return "<span style='color:green;font-weight:bold';>微信支付</span>";  
                    }else if(value=='3'){  
                        return "<span style='color:green;font-weight:bold';>现金支付</span>";  
                    }else if(value=='4'){  
                        return "<span style='color:green;font-weight:bold';>免单</span>";  
                    }	
         	    }else{
         	    	return "<span style='color:red;font-weight:bold';></span>";
         	    }
       		}},
			{header : '时间',dataIndex : 'payment_time'}
		];
	//列表展示数据源
		var storePayDeatil = Ext.create("Ext.data.Store", {
			pageSize : 20, //每页显示几条数据  
			proxy : {
				type : 'ajax',
				url : '/merchants/getPayDeatilByMerId',
				reader : {
					type : 'json',
					totalProperty : 'total',
					root : 'data',
					idProperty : 'id'
				}
			},
	        fields:[   //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
	           {name:'payment_type'},  
	           {name:'payment_price'},  
	           {name:'payment_time'},  
	           {name:'order_no'},  
	           {name:'order_pay_type'}
	        ]  
	    });
		storePayDeatil.on('beforeload', function (storeVouchers, options) {    
        var new_params = {merchantsid:merchantsid };    
        Ext.apply(storeVouchers.proxy.extraParams, new_params);    
    }); 
	    var sm = Ext.create('Ext.selection.CheckboxModel');
	    var gridPay = Ext.create("Ext.grid.Panel",{
	    	region: 'center',
	    	border: false,
	    	store: storePayDeatil,
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
	            store: storePayDeatil,   // GridPanel使用相同的数据源
	            dock: 'bottom',
	            displayInfo: true,
	            plugins: Ext.create('Ext.ux.ProgressBarPager'),
	            emptyMsg: "没有记录" //没有数据时显示信息
	        }]
	    });
	  //加载数据  

	  
	   gridPay.on("itemdblclick",function(grid, row){
    	addform(row.data);
    });
  
	  
	  storePayDeatil.load({params:{start:0,limit:20,merchantsid:merchantsid}}); 
	    var paywin = Ext.create('Ext.window.Window',{  
	              title : "查看收入明细",  
	              width : 700, 
	              height : 400,  
	              //plain : true,  
	              iconCls : "addicon",  
	              // 不可以随意改变大小  
	              resizable : false, 
	              autoScroll:true, 
	              // 是否可以拖动  
	              // draggable:false,  
	              collapsible : true, // 允许缩放条  
	              closeAction : 'close',  
	              closable : true,  
	              // 弹出模态窗体  
	              modal : 'true',  
	              buttonAlign : "center",  
	              bodyStyle : "padding:0 0 0 0",  
	              items : [gridPay]
	           });  
	    paywin.show();  
	    
	    
	   //*********************************查看订单号--------------------
	   
	    var addform=function(d){
	 	    var add_orderForm =  Ext.create('Ext.form.Panel', {  
	 	                frame: true,   //frame属性  
	 	                //title: 'Form Fields',  
	 	                width: 380, 
	 	                height:250,
	 	               // autoScroll: true, 
	 	                bodyPadding:5,
	 	                fileUpload:true,
	 	               // resizable : true,  
	  	                //autoScroll: true,  
	  	              // 是否可以拖动  
	  	                //draggable:true,  
	  	               // collapsible : true, // 允许缩放条  
	 	                //renderTo:"panel21",  
	 	                fieldDefaults: {  
	 	                    labelAlign: 'left',  
	 	                    labelWidth: 90,  
	 	                    anchor: '90%'  
	 	                },  
	 	                items: [ {xtype : 'textfield',
					              name : 'order_no',
					              fieldLabel : '订单号',
					              readOnly:true
	                        }  ]
	 	            });  
	 	    
		    
		    	if(d!=null){
		    		
		    	    add_orderForm.form.findField('order_no').setValue(d.order_no);
		    	}
		    	
	        //================判断是编辑还是新增===============
	 	    
	 	    //创建window面板，表单面板是依托window面板显示的  
	 	    var orderRewardwin = Ext.create('Ext.window.Window',{  
	 	              title : '查看订单号',  
	 	              width : 400, 
	 	              height: 200,
	 	              //height : 120,  
	 	              //plain : true,  
	 	              iconCls : "addicon",  
	 	              // 不可以随意改变大小  
	 	              resizable : true,  
	 	             
	 	              // 是否可以拖动  
	 	              // draggable:false,  
	 	              collapsible : true, // 允许缩放条  
	 	              closeAction : 'close',  
	 	              closable : true,  
	 	              // 弹出模态窗体  
	 	              modal : 'true',  
	 	              buttonAlign : "center",  
	 	              bodyStyle : "padding:0 0 0 0",  
	 	              items : [add_orderForm],  
	 	              buttons : [{  
	 	                         text : "关闭",  
	 	                         minWidth : 70,  
	 	                         handler : function() {  
	 	                        	orderRewardwin.close();  
	 	                         }  
	 	                     }]  
	 	           });  
	 	   orderRewardwin.show();  
	 	    };
	   //*********************************结束**************
};

    //查看服务商的服务项目
  		var showServiceType=function(merchantId){  
  			var columns = [
                {header:'序号',xtype: 'rownumberer',width:50},
  				{header : '服务类型',dataIndex : 'service_type_name'}
  				
  			];
  		//列表展示数据源
  			var storeService = Ext.create("Ext.data.Store", {
  				pageSize : 20, //每页显示几条数据  
  				proxy : {
  					type : 'ajax',
  					url : '/merchants/getServiceTypeById',
  					reader : {
  						type : 'json',
  						totalProperty : 'total',
  						root : 'data',
  						idProperty : '#'
  					}
  				},
  		        fields:[  
  		           {name:'service_type_name'} //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
  		           
  		        ]  
  		    });
  		
		 
  		    
  		    var gridServices = Ext.create("Ext.grid.Panel",{
  		    	region: 'center',
  		    	border: false,
  		    	store: storeService,
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
  		            store: storeService,   // GridPanel使用相同的数据源
  		            dock: 'bottom',
  		            displayInfo: true,
  		            plugins: Ext.create('Ext.ux.ProgressBarPager'),
  		            emptyMsg: "没有记录" //没有数据时显示信息
  		        }]
  		    });
  		  //加载数据  

  		    storeService.load({params:{start:0,limit:20,merchantId:merchantId}}); 
  		    var agentwin = Ext.create('Ext.window.Window',{  
  		              title : "查看服务类型",  
  		              width : 350, 
//  		              height: 500,
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
  		              items : [gridServices]
  		           });  
  		  agentwin.show();  
  		 };	
  		 
  function showDetail(id,name){
	  if(name=='null'){
			name='';
		}
	  parent.addTab("merchant_detail_"+id,"服务商【"+name+"】详情","Fuwushangyunyingxinxiguanli","/merchants/merchantDetail?merchantId="+id); 	 
  }
</script>

</head>
<body>
</body>
</html>