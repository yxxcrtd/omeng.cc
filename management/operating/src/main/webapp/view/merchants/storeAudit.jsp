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
<title>服务商认证管理</title>

<style type="text/css">
     #picLayer{background: rgba(0,0,0,0.5);position: fixed;left: 0;right: 0;top: 0;bottom: 0;z-index:19009;display:none;}
     #picLayer .box{position: absolute;left:50%;top: 50%;width: 800px;height: 600px;margin: -300px 0 0 -400px;text-align: center;} 
     #picLayer .box img{max-width: 760px;max-height:600px;width: auto;height: auto; vertical-align: middle;}
     #picLayer .box .imgHM{width:0;height:94%; vertical-align: middle;display:inline-block;*zoom:1;*display: inline;}
     #picLayer .contorl{position: absolute;right: -80px;top: 150px;height: 30px;}
     #picLayer .but{background: none;border:1px solid #fff;color: #fff;width:100px;height: 30px;line-height: 30px;text-align:center;margin-top: 10px;display: block;}
     #picLayer .close{position: absolute;right:10px;top:10px;width:30px;height:30px;line-height:30px;color: #fff;background:#000000;}
</style>
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
var store_appType;
var grid;
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'编号',dataIndex:'id',hidden:true},
                   {header:'服务商ID',dataIndex:'merchantId',hidden:true,align:'center'},
                   {header:'服务商名称',dataIndex:'name',align:'center'},
                   {header:'行业类型',dataIndex:'app_type',hidden:true,align:'center'},
                   {header:'省份',dataIndex:'province',hidden:true,align:'center'},
                   {header:'城市',dataIndex:'city',hidden:true,align:'center'},
                   {header:'行业类型',dataIndex:'app_name',align:'center'},
                   {header:'注册号码',dataIndex:'telephone',align:'center'},
                   {header:'申请时间',dataIndex:'join_time',align:'center'},
                   {header:'地址',dataIndex:'address',align:'center'},
                   {header : '认证次数',dataIndex : 'auth_total',align:'center',renderer:function(value,v,r){  
                       return '<a href="javascript:showAuthTotal(\''+r.data.merchantId+'\')"><span style="color:red;font-weight:bold";>'+value+'</span></a>';
                     
          		}},
                
                   {header:'认证类型',dataIndex:'auth_type',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>企业认证</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>个人认证</span>";  
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
                       }
           	       }},<cms:havePerm url='/merchants/AuditStore'>
                   { id: 'certificate',  header: '服务商认证',  dataIndex: 'auth_status',align:'center',  sortable:true,fixed:false,renderer:function(value,v,r){
      	              if(value=='2'){
                	    return '<a href="javascript:audit()">认证</a>';
                	  }else if(value=='0'){
                		  return '<a href="javascript:audit()"><span style="color:red;font-weight:bold";>拒绝原因</span></a>'; 
                	  }else{
                		  return '';  
                	  }
                   }},</cms:havePerm>
                   { id: 'showInfo',  header: '操作',  dataIndex: 'auth_status',  sortable:true,fixed:false,renderer:function(value,v,r){
                 	  return '<a href="javascript:showInfo()">查看</a>';
                    }},
                    {header:'操作时间',dataIndex:'auth_time',align:'center'},
                    {header:'拒绝原因',dataIndex:'remark',align:'center'},
                    {header:'操作人',dataIndex:'oper_user',align:'center'}
               ];
	//创建store数据源
    // app类型
	 store_appType = Ext.create("Ext.data.Store", {
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
    //列表展示数据源
     store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/merchants/getStoreAudit',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'exc'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'merchantId'},
           {name:'name'},  
           {name:'app_type'}, 
           {name:'province'},
           {name:'city'},
           {name:'app_name'}, 
           {name:'telephone'},  
           {name:'join_time'},
           {name:'auth_total'},
           {name:'address'},  
           {name:'path'},
           {name:'remark'},
           {name:'auth_type'},
           {name:'oper_user'},
           {name:'auth_status'} ,
           {name:'auth_time'}
        ]  
    });
     var authStatusStore = new Ext.data.SimpleStore({
     	fields:['statusName','auth_status'],
     	data:[['全部',''],
     	      ['未通过','0'],
     	      ['已认证','1'],
     	      ['待认证','2'],
     	      ['已取消','3']
     	]
     });
     var authTypeStore = new Ext.data.SimpleStore({
      	fields:['auth_name','auth_type'],
      	data:[['全部',''],
      	      ['企业认证','1'],
      	      ['个人认证','2']
      	]
      }); 
     
     
     var defineStore1= new Ext.data.SimpleStore({
 	 	fields:['type','name'],
 	 	data:[ ['1','证件类型不符'],
 	 	      ['2','图片信息不清楚'],
 	 	      ['3','入驻行业不符'],
 	 	      ['4','认证图片不标准'],
 	 	      ['5','证照内容与注册信息不符'],
 	 	      ['6','其他']
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
    	  var start_time=Ext.getCmp('start_time').getValue();
 		  var off_time=Ext.getCmp('off_time').getValue();
    	  var auth_status=Ext.getCmp('auth_status').getValue();
    	  var auth_type=Ext.getCmp('auth_type').getValue();
    	  var telephone=Ext.getCmp('telephone').getValue();
    	  var province=Ext.getCmp('province').getRawValue();
 		  var city=Ext.getCmp('city').getRawValue();
 		  var app_type=Ext.getCmp('app_type').getValue();
 		  var invite_code=Ext.getCmp('invite_code').getValue();
 		  var remark=Ext.getCmp('remark').getRawValue();
 		  var agentId='';
 		
        var new_params = { remark:remark,name:name,auth_status:auth_status,auth_type:auth_type,start_time : start_time,off_time : off_time,telephone:telephone,province:province,city:city,agentId:agentId,app_type:app_type,invite_code:invite_code};    
        Ext.apply(store.proxy.extraParams, new_params);    
    });
    var sm = Ext.create('Ext.selection.CheckboxModel');
    grid = Ext.create("Ext.grid.Panel",{
    	region: 'center',
    	border: false,
    	store: store,
    	selModel:sm,
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
    	    	   <cms:havePerm url='/merchants/exportStoreAuditExcel'>
        	       { xtype: 'button', id:'export',text: '导出',iconCls:'Daochu',
            	    	   listeners: {
            	    		   click:function(){
            	    			   exportAll();
            	    		   }
            	    	   }
                   }</cms:havePerm>
            	    	  
            ]
         },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id: 'name',name: 'name',fieldLabel: '服务商名称',labelAlign:'left',labelWidth:70},'-',
        	       { xtype: 'textfield',id: 'invite_code',name: 'invite_code',fieldLabel: '邀请码',value:'${invite_code}',hidden:true},
        	       {xtype : 'combobox',id : 'auth_status',name : 'auth_status',fieldLabel : '认证状态',value:'${auth_status}',valueField : 'auth_status',editable:false,displayField : 'statusName',
				         store : authStatusStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
				   {xtype : 'combobox',id : 'auth_type',name : 'auth_type',fieldLabel : '认证类型',value:'${auth_type}',valueField : 'auth_type',editable:false,displayField : 'auth_name',
					      store : authTypeStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',   
				   { xtype: 'datetimefield',id: 'start_time',name: 'start_time',fieldLabel: '认证时间',format : 'Y-m-d',value:'${start_time}',editable:false,labelAlign:'left',labelWidth:60},'～',
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
						queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
			    {xtype : 'combobox',id : 'city',name : 'city',fieldLabel : '城市',valueField : 'id',displayField : 'area',
						store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',	
				{xtype : 'combobox',id : 'app_type',name : 'appType',fieldLabel : '行业类型',value:'',valueField : 'app_type',displayField : 'app_name',
							store : store_appType,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
				{xtype : 'combobox',id : 'remark',name : 'remark',fieldLabel : '不通过原因',valueField : 'type',displayField : 'name',
							      store : defineStore1,queryMode : 'local',labelAlign : 'left',labelWidth : 70},'-',   
						 
							<cms:havePerm url='/merchants/getStoreAudit'>
 	               { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
	    		   click:function(){
	    			   var name=Ext.getCmp('name').getValue();
	    			   var auth_status=Ext.getCmp('auth_status').getValue();
	    			   var auth_type=Ext.getCmp('auth_type').getValue();
	    		       var telephone=Ext.getCmp('telephone').getValue();
	    		       var start_time=Ext.getCmp('start_time').getValue();
	    			   var off_time=Ext.getCmp('off_time').getValue();
	    			   var province=Ext.getCmp('province').getRawValue();
	                   var city=Ext.getCmp('city').getRawValue();
	                   var app_type=Ext.getCmp('app_type').getValue();
	                   var invite_code=Ext.getCmp('invite_code').getValue();
	                   var remark=Ext.getCmp('remark').getRawValue();
	    		 	   var agentId='';
	    		 	   if(off_time==null){
	                 	   
	                   }else{
	                   if(start_time>off_time){
	                	   Ext.example.msg("提示","开始时间大于结束时间！");
	                	   return;
	                   }
	                   }
	    			   store.currentPage = 1;
	    			   store.load({params:{start:0,page:1,limit:20,name:name,auth_status:auth_status,auth_type:auth_type,start_time : start_time,off_time : off_time,telephone:telephone,app_type:app_type,province:province,city:city,agentId:agentId,invite_code:invite_code,remark:remark}}); 
	    		   }
	    		   }}</cms:havePerm>]
         }],
         bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
             displayInfo : true,
             store : store
         })
    });
    
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
   
   
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	//订单删除
	 function delUserAll()  
     {  
          //grid中复选框被选中的项  
            
          var rows = grid.getSelectionModel().getSelection();  
          //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
          var user_id = '';  
          for(var i = 0;i<rows.length;i++)  
          {  
             if(i>0)  
             {  
                 user_id = user_id+','+rows[i].get('merchantId');  
             }else{  
                 user_id = user_id+rows[i].get('merchantId');  
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
                         params:{id:user_id},  
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
	
     function sleepTime(){
    	 Ext.getCmp('export').setDisabled(true);
    	 setTimeout(function(){
    		Ext.getCmp('export').setDisabled(false);
    	}, 30000);//js定时器
      }  
   //导出所有商户
		function exportAll() {
			   var name=Ext.getCmp('name').getValue();
			   var start_time=Ext.getCmp('start_time').getValue();
			   var off_time=Ext.getCmp('off_time').getValue();
			   var auth_status=Ext.getCmp('auth_status').getValue();
			   var auth_type=Ext.getCmp('auth_type').getValue();
			   var province=Ext.getCmp('province').getRawValue();
               var city=Ext.getCmp('city').getRawValue();
               var app_type=Ext.getCmp('app_type').getValue();
               var telephone=Ext.getCmp('telephone').getValue();
               var invite_code=Ext.getCmp('invite_code').getValue();
               var remark=Ext.getCmp('remark').getRawValue();
               var agentId='';
               window.location.href = '/merchants/exportStoreAuditExcel?name='+name+'&start_time='+start_time+'&off_time='+off_time
				+'&auth_status='+auth_status+'&auth_type='+auth_type+'&province='+province+'&city='+city+'&app_type='+app_type+'&telephone='+telephone+'&agentId='+agentId+'&invite_code='+invite_code+'&remark='+remark;
               
               Ext.example.msg("提示","正在导出报表，请稍后！");
               sleepTime();
   };
   //form表单编辑用户
   
 	var editform=function(){  
 		var edit_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 440,
 	                height:300,
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
 	                    value: '编辑服务商信息'  
 	                     
 	                }, 
 	               {  
 	                    //输入姓名  
 	                    xtype: 'textfield', 
 	                    name: 'merchantId',  
 	                    fieldLabel: '服务商ID',
 	                    hidden:true
 	                }, {  
 	                    //输入姓名  
 	                    xtype: 'textfield', 
 	                    name: 'name',  
 	                    fieldLabel: '服务商名称'  
 	                },{
 	   				xtype : 'combobox',
 					name : 'app_type',
 					fieldLabel : 'app类型',
 					valueField : 'app_type',
 					displayField : 'app_name',
 					editable:false,
 					store : store_appType,
 					hiddenName:'',
 					allowBlank	:false,
 					readOnly:true,
 					queryMode : 'local'
 				},
 	             {  
 	                    //输入联系方式
 	                    xtype: 'textfield',  
 	                    name: 'telephone',  
 	                    fieldLabel: '联系方式'
 	                },{  
 	                    //输入地理位置
 	                    xtype: 'textfield',  
 	                    name: 'location_address',  
 	                    fieldLabel: '地理位置'
 	                }
 	               ]  
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
 	   
	    		 // 是编辑状态
	    	     edit_winForm.form.findField('merchantId').setValue(rows[0].get('merchantId'));
	             edit_winForm.form.findField('name').setValue(rows[0].get('name'));  
	             edit_winForm.form.findField('app_type').setValue(rows[0].get('app_type'));  
	             edit_winForm.form.findField('location_address').setValue(rows[0].get('address'));  
	             edit_winForm.form.findField('telephone').setValue(rows[0].get('telephone')); 
	    	
        
 	    var editwindow = Ext.create('Ext.window.Window',{  
 	              title : "编辑服务商信息",  
 	              width: 450,
	              height:300,  
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
 	                                          url :'/merchants/saveStore',  
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

//*********************服务商信息认证start*********************
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
     var id = rows[0].get('id');
     var merchantId = rows[0].get('merchantId');
     var name = rows[0].get('name');
     var phone = rows[0].get('telephone');
     var appType = rows[0].get('app_type');
     var address = rows[0].get('address');
     var remark = rows[0].raw.remark;
     var auth_status= rows[0].get('auth_status');
     var pic = rows[0].get('path');
	if(auth_status==0){
	   // Ext.example.msg("拒绝原因",remark);
		Ext.Msg.alert("拒绝原因", remark);
	}else if(auth_status==2){
		certificate(id,merchantId,name,phone,appType,address,pic,remark,auth_status);
	}
};


var defineStore = new Ext.data.SimpleStore({
 	fields:['type','name'],
 	data:[['','请选择'],
 	      ['1','证件类型不符'],
 	      ['2','图片信息不清楚'],
 	      ['3','入驻行业不符'],
 	      ['4','认证图片不标准'],
 	      ['5','证照内容与注册信息不符'],
 	      ['6','其他']
 	]
 });
 	   function  certificate(id,merchantId,name,phone,appType,address,pic,remark,auth_status){  
 		 // defineStore.load();
		var cert_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 820,
	                height:1200,
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
//	                    fieldLabel: 'Display field',  
	                    value: '服务商认证'  
	                     
	                }, 
	               {   
	                    xtype: 'textfield', 
	                    name: 'id',  
	                    fieldLabel: '服务商ID',
	                    hidden:true
	                }, {  
	                    xtype: 'textfield', 
	                    name: 'merchantId',  
	                    fieldLabel: '服务商ID',
	                    hidden:true
	                }, {  
	                    //输入姓名  
	                    xtype: 'textfield', 
	                    name: 'name',
	                    readOnly:true,
	                    fieldLabel: '服务商名称'  
	                }, {
	 	   				xtype : 'combobox',
	 					name : 'app_type',
	 					fieldLabel : 'app类型',
	 					valueField : 'app_type',
	 					displayField : 'app_name',
	 					editable:false,
	 					store : store_appType,
	 					hiddenName:'',
	 					allowBlank	:false,
	 					readOnly:true,
	 					queryMode : 'local'
	 				},{  
	                    //输入联系方式
	                    xtype: 'textfield',  
	                    name: 'telephone',
	                    readOnly:true,
	                    fieldLabel: '联系方式'
	                },{  
	                    //输入地理位置
	                    xtype: 'textfield',  
	                    name: 'address',
	                    readOnly:true,
	                    fieldLabel: '地理位置'
	                },{  
	                    //显示文本框，相当于label  
	                    xtype: 'displayfield',   
	                    name: 'pic',  
//	                    fieldLabel: 'Display field',  
	                    value: '认证图片信息'  
	                     
	                }, 
	  	             {  
	  	         	    xtype: 'box', //或者xtype: 'component',  
	  	         	    width: 500, //图片宽度  
	  	         	    //height: 1000, //图片高度  
	  	         	    autoEl: {  
	  	         	        tag: 'img',    //指定为img标签  
	  	    	            onclick:'zoomImg(\''+pic+'\')',
	  	         	        src: pic    //指定url路径  
	  	         	    }  
	  	         	},{   
	                    xtype: 'textfield', 
	                    name: 'remark',  
	                    fieldLabel: '备注',
	                    hidden:true
	                }
	               
	                
	               ]  
	            });  
	    //创建window面板，表单面板是依托window面板显示的  
	    
	     cert_winForm.form.findField('id').setValue(id);
	     cert_winForm.form.findField('merchantId').setValue(merchantId);
         cert_winForm.form.findField('name').setValue(name);  
         cert_winForm.form.findField('app_type').setValue(appType);  
         cert_winForm.form.findField('address').setValue(address);  
         cert_winForm.form.findField('telephone').setValue(phone);  
     
	    var certwindow = Ext.create('Ext.window.Window',{  
	              title : "服务商认证",  
	              width: 850,
	              height:450,
	              autoScroll: true, 
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
	              items : [cert_winForm],  
	              buttons : [{  
	                         text : "通过",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            if (cert_winForm.getForm().isValid()) {
	                             Ext.Msg.confirm("提示信息","确定审核通过吗?",function (btn){  
	                              if(btn == 'yes')  
	                              {  
	  	                            	cert_winForm.getForm().submit({  
	  	                                          url :'/merchants/AuditStore',  
	  	                                           //等待时显示 等待  
	  	                                          waitTitle: '请稍等...',  
	  	                                          waitMsg: '正在提交信息...',  
	  	                                            
	  	                                          success: function(fp, o) {  
	  	                                              if (o.result== true) { 
	  	                                     	      Ext.example.msg("提示","审核信息保存成功！");
	  	                                              store.reload();
	  	                                              certwindow.close(); //关闭窗口  
	  	                                              return ;  
	  	                                              }else {  
	  	                                        	      Ext.example.msg("提示","状态已改变，审核信息保存失败！");
	  	                                              }  
	  	                                          },  
	  	                                          failure: function() { 
	  	                                 	         Ext.example.msg("提示","状态已改变，审核信息保存失败！");
	  	                                          }  
	  	                                       });    
	                              }  
	                          });  

	                            }  
	                         }  
	                     }, {  
	                         text : "不通过",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            if (cert_winForm.getForm().isValid()) {
	                            	
	                            	definprompt();
	                            	
	                            }  
	                         }  
	                     }]  
	           });  
	       certwindow.show();  
	    //自定义弹出框
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
	 		    text :"请输入不通过原因:"
	 		   },{
	   				xtype : 'combobox',
 					name : 'txt',		
 					valueField : 'type',
 					displayField : 'name',
 					store : defineStore,
 					value:'',
 					allowBlank	:false,
 					editable:false,
 					queryMode : 'local',
 					forceSelection:false
 				}],  
	 		   buttons : [{
	 		    text : '确定',
	 		    handler : function(){
	 		    
                    var txt=formPanel.form.findField('txt').getRawValue();
	              	cert_winForm.form.findField('remark').setValue(txt);
	              	if(txt==''||txt=='请选择'){
	              		Ext.example.msg("提示","请输入不通过原因！");
	              		return;
	              	}
	              	  cert_winForm.getForm().submit({  
	                      url :'/merchants/RefuseAuditStore',  
	                       //等待时显示 等待  
	                      waitTitle: '请稍等...',  
	                      waitMsg: '正在提交信息...',  
	                        
	                      success: function(fp, o) {  

	                          if (o.result== true) { 
	                 	      Ext.example.msg("提示","审核信息保存成功！");
	                          store.reload();
	                          certwindow.close(); //关闭窗口  
	                          formPanel.close();
	                          win.close();
	                          return ;  
	                          }else {  
	                 	         Ext.example.msg("提示","审核信息保存失败！");
	                          }  
	                      },  
	                      failure: function() {  
	                         Ext.example.msg("提示","审核信息保存失败！");
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
	 		   win.on("close",function(){
	 			  formPanel.close();

	 		     });
	 		    win.show();
	           }
	  	     };
 	    //*********************服务商信息认证end*********************
 	    function zoomImg(v){
 	      //parent.addTab("merchant_auth_img","服务商认证图片","Fuwushangxinxiguanli",v); 
 	      $("#xzpic").attr("src",v);
 	      $("#picLayer").show();
 	    }
 	    
 	    
//*********************服务商信息查看start*********************
 	   function  showInfo(){     
 		  var rows = grid.getSelectionModel().getSelection();  
 		    //user_id：所有选中的服务商Id的集合使用','隔开，初始化为空    
 		    if(rows.length == 0)  
 		    {  
 		  	    Ext.example.msg("提示","请选择要查看的对象！");
 		       return ;  
 		    }
 		   if(rows.length > 1)  
 		   {  
 			  Ext.example.msg("提示","只能选择一个查看的对象！");
 		     return ;  
 		    }
 		     var id = rows[0].get('id');
             var merchantId = rows[0].get('merchantId');
             var name = rows[0].get('name');
             var phone =rows[0].get('telephone'); 
             var appType = rows[0].get('app_type');
             var address = rows[0].get('address');
             var pic =rows[0].get('path'); 
	
	
 	  		var show_winForm =  Ext.create('Ext.form.Panel', {  
 	  	                frame: true,   //frame属性  
 	  	                //title: 'Form Fields',  
 	  	                width: 820,
 	  	                height:1200,
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
// 	  	                    fieldLabel: 'Display field',  
 	  	                    value: '服务商认证信息'  
 	  	                     
 	  	                }, 
 	  	               {   
 	  	                    xtype: 'textfield', 
 	  	                    name: 'id',  
 	  	                    fieldLabel: '服务商ID',
 	  	                    hidden:true
 	  	                }, {  
 	  	                    xtype: 'textfield', 
 	  	                    name: 'merchantId',  
 	  	                    fieldLabel: '服务商ID',
 	  	                    hidden:true
 	  	                }, {  
 	  	                    //输入姓名  
 	  	                    xtype: 'textfield', 
 	  	                    name: 'name',
 	  	                    readOnly:true,
 	  	                    fieldLabel: '服务商名称'  
 	  	                }, {
 	  	 	   				xtype : 'combobox',
 	  	 					name : 'app_type',
 	  	 					fieldLabel : 'app类型',
 	  	 					valueField : 'app_type',
 	  	 					displayField : 'app_name',
 	  	 					editable:false,
 	  	 					store : store_appType,
 	  	 					hiddenName:'',
 	  	 					allowBlank	:false,
 	  	 					readOnly:true,
 	  	 					queryMode : 'local'
 	  	 				},{  
 	  	                    //输入联系方式
 	  	                    xtype: 'textfield',  
 	  	                    name: 'telephone',
 	  	                    readOnly:true,
 	  	                    fieldLabel: '联系方式'
 	  	                },{  
 	  	                    //输入地理位置
 	  	                    xtype: 'textfield',  
 	  	                    name: 'address',
 	  	                    readOnly:true,
 	  	                    fieldLabel: '地理位置'
 	  	                },{  
 	  	                    //显示文本框，相当于label  
 	  	                    xtype: 'displayfield',   
 	  	                    name: 'pic',  
// 	  	                    fieldLabel: 'Display field',  
 	  	                    value: '认证图片信息'  
 	  	                     
 	  	                }, 
 	  	             {  
 	  	         	    xtype: 'box', //或者xtype: 'component',  
 	  	         	    width: 500, //图片宽度  
 	  	         	    //height: 1000, //图片高度  
 	  	         	    autoEl: {  
 	  	         	        tag: 'img',    //指定为img标签  
 	  	         	        onclick:'zoomImg(\''+pic+'\')',
 	  	         	        src: pic    //指定url路径  
 	  	         	    }  
 	  	         	}
 	  	               
 	  	                
 	  	               ]  
 	  	            });  
 	  	    //创建window面板，表单面板是依托window面板显示的  
 	  	    
 	  	     show_winForm.form.findField('id').setValue(id);
 	  	     show_winForm.form.findField('merchantId').setValue(merchantId);
 	         show_winForm.form.findField('name').setValue(name);  
 	         show_winForm.form.findField('app_type').setValue(appType);  
 	         show_winForm.form.findField('address').setValue(address);  
 	         show_winForm.form.findField('telephone').setValue(phone);  
 	        
 	  	    var showwindow = Ext.create('Ext.window.Window',{  
 	  	              title : "服务商认证",  
 	  	              width: 850,
 	  	              height:450,
 	  	              autoScroll: true, 
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
 	  	              items : [show_winForm],  
 	  	              buttons : [{  
	                         text : "关闭",  
	                         minWidth : 70,  
	                         handler : function() {  
	                        	 showwindow.close();  
	                         }  
	                     }]  
 	  	           });  
 	  	       showwindow.show();  
 	  	    };
 	    //*********************服务商信息认证end*********************
</script>

</head>
<body>
   <div id="picLayer">
		<div class="box">
			<img src="http://120.55.119.232/upload/merchantInfo/image/auth/201510/480caffc-8ad8-4950-9c79-fe7a5d573708.jpg" id="xzpic"><span class="imgHM"></span>
			<div class="contorl">
				<input type="button" class="but" value="顺时针旋转" id="xzBut_s">
				<input type="button" class="but" value="逆时针旋转" id="xzBut_n">
				<input type="button" class="but" value="查看原图" id="xzBut_c">
				<input type="button" class="but" value="关闭" id="xzBut_d">
			</div>
			
		</div>
	</div>
<script type="text/javascript" src="/view/js/jquery-1.8.3.min.js"></script>
<script type="text/javascript" src="/view/js/jquery.rotate.min.js"></script>
<script type="text/javascript">
var xzvalue = 0;
$("#xzBut_s").rotate({ 
   bind: 
     { 
        click: function(){
            xzvalue +=90;
            $('#xzpic').rotate({ animateTo:xzvalue});
        }
     } 
   
});
$("#xzBut_n").rotate({ 
   bind: 
     { 
        click: function(){
            xzvalue -=90;
            $('#xzpic').rotate({ animateTo:xzvalue})
        }
     } 
   
});

jQuery("#xzBut_c").click(function () { 
    var v = $("#xzpic").attr("src");
	parent.addTab("merchant_auth_img","服务商认证图片","Fuwushangxinxiguanli",v);
	});

/*关闭*/
$('#picLayer .close').click(function(){
	$('#picLayer').hide();
});

$('#xzBut_d').click(function(){
	$('#picLayer').hide();
});


//查看之前的认证信息	
var showAuthTotal=function(merchantsId){  	
	var columns = [
{header:'序号',xtype: 'rownumberer',width:50},
{header:'编号',dataIndex:'id',hidden:true},
{header:'服务商ID',dataIndex:'merchantId',hidden:true},
{header:'服务商名称',dataIndex:'name'},
{header:'app类型',dataIndex:'app_type',hidden:true},
{header:'省份',dataIndex:'province',hidden:true},
{header:'城市',dataIndex:'city',hidden:true},
{header:'app类型',dataIndex:'app_name'},
{header:'联系方式',dataIndex:'telephone'},
{header:'申请时间',dataIndex:'join_time'},
{header:'地址',dataIndex:'address'},
{header:'认证类型',dataIndex:'auth_type',renderer:function(value){  
    if(value=='1'){  
        return "<span style='color:red;font-weight:bold';>企业认证</span>";  
    } else if(value=='2'){  
        return "<span style='color:green;font-weight:bold';>个人认证</span>";  
    }
   }},
{header:'认证状态',dataIndex:'auth_status',renderer:function(value){  
    if(value=='0'){  
        return "<span style='color:red;font-weight:bold';>未通过</span>";  
    } else if(value=='1'){  
        return "<span style='color:green;font-weight:bold';>已认证</span>";  
    }else if(value=='2'){
 	   return "<span style='color:blue;font-weight:bold';>待认证</span>";  
    }else if(value=='3'){
 	   return "<span style='color:black;font-weight:bold';>已取消</span>";  
    }
   }},
 {header:'拒绝原因',dataIndex:'remark'},
 {header:'操作人',dataIndex:'oper_user'}
               ];
	
    var storeMerchants = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/merchants/getAuthDetailByMerchantId',
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
{name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
{name:'merchantId'},
{name:'name'},  
{name:'app_type'}, 
{name:'province'},
{name:'city'},
{name:'app_name'}, 
{name:'telephone'},  
{name:'join_time'},
{name:'address'},  
{name:'remark'},
{name:'auth_type'},
{name:'oper_user'},
{name:'auth_status'} 
        ]  
    });
    storeMerchants.on('beforeload', function (storeMerchants, options) {    
    	
        var new_params = { merchants_id:merchantsId};    
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
    storeMerchants.load({params:{start:0,limit:20,merchants_id:merchantsId}}); 
    var syswinMerchants = Ext.create('Ext.window.Window',{  
              title : "认证历史记录",  
              width :1000, 
              height: 350,
              //height : 120,  
              //plain : true,  
              iconCls : "addicon", 
              autoScroll : true,
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
	</script>
</body>
</html>