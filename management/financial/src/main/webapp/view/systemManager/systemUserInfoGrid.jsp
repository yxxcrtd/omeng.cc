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
<title>用户管理</title>
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
var gridRecovery;
var storeRecoveryUser;
var grid;
var store;
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50},
                   {header:'用户ID',dataIndex:'id',sortable:true,fixed:false,hidden:true},
                   {header:'账号',dataIndex:'userName',sortable:true,fixed:false},
                   {header:'姓名',dataIndex:'realName',sortable:true,fixed:false},
                   {header:'手机号',dataIndex:'phone',sortable:true,fixed:false},
                   {header:'状态',dataIndex:'disabled',sortable:true,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>禁用</span>";  
                       }else{
                    	   return "<span style='color:green;font-weight:bold';>启用</span>"; 
                       }
           		   }},
                   {header:'用户类型',dataIndex:'userType',sortable:true,fixed:false,renderer:function(value){
                	   if(value==2){
                    	   return "<span style='color:#fe7352;font-weight:bold';>省代理</span>"; 
                	   }else if(value==3){
                		   return "<span style='color:#e872af;font-weight:bold';>市代理</span>";
                	   }else if(value==4){
                		   return "<span style='color:#a6ce6b;font-weight:bold';>项目代理</span>";
                	   }else{
                		   return "<span style='color:red;font-weight:bold';>公司员工</span>";  
                	   }
           		   }},
           		   {header:'省',dataIndex:'provinceDesc',sortable:true,fixed:false},
                   {header:'市',dataIndex:'cityDesc',sortable:true,fixed:false},
                   {header:'收益账号',dataIndex:'isAccount',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>是</span>";  
                       } else{  
                           return "<span style='color:green;font-weight:bold';>否</span>";  
                       }
           	       }},<cms:havePerm url='/systemManager/setAccount'>
                   { id: 'isAccount',header: '操作',  dataIndex: 'isAccount',  sortable:true,fixed:false,renderer:function(value,v,r){
      	              if(value=='0'){
                	    return '<a href="javascript:setAccount()">设为收益账号</a>';
                	  }else{
                		  return '';  
                	  }
                   }},</cms:havePerm>
                   <cms:havePerm url='/systemManager/getUserRoles'>
                   {
                       xtype:'actioncolumn', 
                       dataIndex:'employee',
                       width:50,
                       text: '分配角色',
                       align:'center',
                       header:'分配角色',                
                       items: [{
                    	   //icon : '/manager/view/ExtJS4.2/icons/cog.png',
                    	   iconCls:'Shouquan',
                    	   text: '分配角色',
                    	   align:'center',
                           handler: function(grid, rowIndex, colIndex) {
                        	   var record = grid.getStore().getAt(rowIndex); 
                       		   var userId = record.get('id');
            				   var userName=record.get('userName');
            				   roleGrant(userId,userName);
                           }
                 }]},
                 </cms:havePerm>
                 <cms:havePerm url='/systemManager/getUserGroups'>
                 {
                     xtype:'actioncolumn', 
                     dataIndex:'id',
                     width:50,
                     align:'center',
                     header:'分配群组',                
                     items: [{
                  	   //icon : '/manager/view/ExtJS4.2/icons/cog.png',
                  	   iconCls:'Shouquan',
                  	   text: '分配群组',
                  	   align:'center',
                         handler: function(grid, rowIndex, colIndex) {
                      	   var record = grid.getStore().getAt(rowIndex); 
                     	   var userId = record.get('id');
          				   var userName=record.get('userName');
          				   groupGrant(userId,userName);
                         }
                  }]},
                  </cms:havePerm>
                   {header:'省',dataIndex:'province',sortable:true,hidden:true,fixed:false},
                   {header:'市',dataIndex:'city',sortable:true,hidden:true,fixed:false},
                   {header:'邮箱',dataIndex:'email',sortable:true,hidden:true,fixed:false}
               ];
    var userTypeComboStore = new Ext.data.SimpleStore({
    	fields:['userTypeName','userType'],
    	data:[['全部','0'],
    	      ['公司员工','1'],
    	      ['省代理','2'],
    	      ['市代理','3'],
    	      ['项目代理','4']
    	]
    });
    
	var disComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['全部',''],
    	      ['启用','0'],
    	      ['禁用','1']
    	]
    });  
	// 地区信息
	    // 省信息
	var uprovinceStore = Ext.create("Ext.data.Store", {
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
    var ucityStore = Ext.create("Ext.data.Store", {
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
	
    store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true, //设置属性进行请求后台排序
        proxy:{  
            type:'ajax',  
            url:'/systemManager/systemUserList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'userName'},  
           {name:'realName'}, 
           {name:'phone'}, 
           {name:'disabled'},  
           {name:'userType'},
           {name:'provinceDesc'},
           {name:'cityDesc'},
           {name:'province'},
           {name:'psw'},
           {name:'pswHints'},
           {name:'city'},
           {name:'isAccount'},
           {name:'accountName'},
           {name:'accountBank'},
           {name:'accountNumber'},
           {name:'remark'},
           {name:'email'}
        ]  
    });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var userName=Ext.getCmp('userName').getValue();
    	var phone=Ext.getCmp('phone').getValue();
		var userType=Ext.getCmp('userInfo.userType').getValue();
		var province=Ext.getCmp('userInfo.province').getValue();
		var city=Ext.getCmp('userInfo.city').getValue();
		var disabled=Ext.getCmp('disabled').getValue();
        var new_params = { userName:userName,phone:phone,userType : userType,disabled:disabled,province:province,city:city};    
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
        dockedItems: [
    {
	xtype:'toolbar',
	dock:'top',
	displayInfo: true,
	items:[
	       <cms:havePerm url='/systemManager/addUser'>
           { xtype: 'button', id:'edit', text: '添加',iconCls:'NewAdd',
	    	   listeners: {
	    		   click:function(){
	    			   showform();
	    		   }
	    	   }},'-',
	       </cms:havePerm>
	       <cms:havePerm url='/systemManager/deleteUser'>
           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
	    	   listeners: {
	    		   click:function(){
	    			   delUser();
	    		   }
	    	   }},'->',
           </cms:havePerm>
	       <cms:havePerm url='/systemManager/recoveryUser'>
           { xtype: 'button', id:'recovery', text: '恢复用户',iconCls:'Edit',
	    	   listeners: {
	    		   click:function(){
	    			   recovery();
	    		   }
	    	   }}
	       </cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'userName',name: 'userName',fieldLabel: '账号',labelAlign:'left',labelWidth:50},'-',
        	       { xtype: 'textfield',id:'phone',name: 'phone',fieldLabel: '手机号',labelAlign:'left',labelWidth:50},'-',
        	        '用户类型:',
        	       {
					xtype:'combo',
					id:'userInfo.userType',
					store : userTypeComboStore,
		    		width:100,
		    		name:'userInfo.userType',
		   			triggerAction: 'all',
		   			displayField: 'userTypeName',
		   			valueField: 'userType',
		   			value:'0',
					mode:'local'
				   },'-',
				   '状态:',
				   {
						xtype:'combo',
						id:'disabled',
						store : disComboStore,
			    		width:100,
			    		name:'disabled',
			   			triggerAction: 'all',
			   			displayField: 'name',
			   			valueField: 'value',
			   			value:'',
						mode:'local'
					   },'-',
			        {
							xtype : 'combobox',
							id : 'userInfo.province',
							name : 'userInfo.province',
							fieldLabel : '省份',
							valueField : 'id',
							hiddenName:'id',
							labelWidth:40,
							displayField : 'area',
							store : uprovinceStore,
							listeners : { // 监听该下拉列表的选择事件
								select : function(combobox,record,index) {
									Ext.getCmp('userInfo.city').setValue('');
									ucityStore.load({
												params : {
													parentId : combobox.value
												}
											});
								}
							},
							queryMode : 'local',
							labelAlign : 'left'
						     },'-',
						     {
									xtype : 'combobox',
									name : 'userInfo.city',
									id : 'userInfo.city',
									labelWidth:40,
									fieldLabel : '城市',
									valueField : 'id',
						   			hiddenName:'id',
									displayField : 'area',
									store : ucityStore,
									queryMode : 'local',
									labelAlign : 'left'
							 },'-',
				   <cms:havePerm url='/systemManager/systemUserList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var userName=Ext.getCmp('userName').getValue();
    	    			   var phone=Ext.getCmp('phone').getValue();
    	    			   var disabled=Ext.getCmp('disabled').getValue();
    	    			   var userType=Ext.getCmp('userInfo.userType').getValue();
    	    			   var province=Ext.getCmp('userInfo.province').getValue();
    	    			   var city=Ext.getCmp('userInfo.city').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,userName:userName,phone:phone,disabled:disabled,userType : userType,province:province,city:city}}); 
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
    uprovinceStore.load();
 // 加载权限

   
   
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    // 双击grid记录，编辑用户信息
      <cms:havePerm url='/systemManager/editUser'>
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data);
    });
	   </cms:havePerm>
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	
	//删除系统用户
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
             var disabled = records[i].get('disabled');	
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
    
	//彻底删除系统用户
	function delAbUser()  
    {  
         //grid中复选框被选中的项  
         var records = gridRecovery.getSelectionModel().getSelection();  
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
         Ext.Msg.confirm("提示信息","彻底删除用户一旦操作不可恢复，确定吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/systemManager/deleteAbUser",  
                     params:{ids:ids},  
                     method:'post',  
                     success:function(o){  
                	     Ext.example.msg("提示","删除成功！"); 
                	     storeRecoveryUser.reload();  
                         return ;  
                     },  
                     failure:function(form,action){  
                    	 Ext.example.msg("提示","删除失败！");
                     }  
                 });    
             }  
         });  
    } ;
    
	//恢复已删除的系统用户
	function recoveryUser()  
    {  
         //grid中复选框被选中的项  
         var records = gridRecovery.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
             Ext.example.msg("提示","请选择要恢复的用户对象！"); 
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
         Ext.Msg.confirm("提示信息","确定恢复选中的用户吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/systemManager/recoveryUser",  
                     params:{ids:ids},  
                     method:'post',  
                     success:function(o){  
                	     Ext.example.msg("提示","恢复成功！"); 
                	     storeRecoveryUser.reload();
                         store.reload();  
                         return ;  
                     },  
                     failure:function(form,action){  
                    	 Ext.example.msg("提示","恢复失败！");
                     }  
                 });    
             }  
         });  
    } ;


    //恢复用户
    function recovery(){
		 var columns = [
		                   {header:'序号',xtype: 'rownumberer',width:50},
		                   {header:'用户ID',dataIndex:'id',sortable:true,fixed:false,hidden:true},
		                   {header:'账号',dataIndex:'userName',sortable:true,fixed:false},
		                   {header:'姓名',dataIndex:'realName',sortable:true,fixed:false},
		                   {header:'手机号',dataIndex:'phone',sortable:true,fixed:false},
		                   {header:'用户类型',dataIndex:'userType',sortable:true,fixed:false,renderer:function(value){
		                	   if(value==2){
		                    	   return "<span style='color:#fe7352;font-weight:bold';>省代理</span>"; 
		                	   }else if(value==3){
		                		   return "<span style='color:#e872af;font-weight:bold';>市代理</span>";
		                	   }else if(value==4){
		                		   return "<span style='color:#a6ce6b;font-weight:bold';>项目代理</span>";
		                	   }else{
		                		   return "<span style='color:red;font-weight:bold';>公司员工</span>";  
		                	   }
		           		   }},
		           		   {header:'省',dataIndex:'provinceDesc',sortable:true,fixed:false},
		                   {header:'市',dataIndex:'cityDesc',sortable:true,fixed:false},
		               ];
			 
			    storeRecoveryUser = Ext.create("Ext.data.Store",{
		    	pageSize:20, //每页显示几条数据  
		        proxy:{  
		            type:'ajax',  
		            url:'/systemManager/systemDelUserList',  
		            reader:{  
		                type:'json',  
		                totalProperty:'total',  
		                root:'data',  
		                idProperty:'id'  
		            }  
		        },  
		        fields:[  
		                {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
		                {name:'userName'},  
		                {name:'realName'}, 
		                {name:'phone'}, 
		                {name:'disabled'},  
		                {name:'userType'},
		                {name:'provinceDesc'},
		                {name:'cityDesc'},
		                {name:'province'},
		                {name:'psw'},
		                {name:'pswHints'},
		                {name:'city'},
		                {name:'accountName'},
		                {name:'accountBank'},
		                {name:'accountNumber'},
		                {name:'remark'},
		                {name:'email'}
		        ]  
		    });
			//点击下一页时传递搜索框值到后台  
			storeRecoveryUser.on('beforeload', function (storeRecoveryUser, options) {    
		        var new_params = {};    
		        Ext.apply(storeRecoveryUser.proxy.extraParams, new_params);    
		    }); 
		    var sm = Ext.create('Ext.selection.CheckboxModel');
		    gridRecovery = Ext.create("Ext.grid.Panel",{
		    	region: 'center',
		    	border: false,
		    	store: storeRecoveryUser,
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
		        	       <cms:havePerm url='/systemManager/deleteAbUser'>
		                   { xtype: 'button',  text: '彻底删除',iconCls:'NewDelete',
		        	    	   listeners: {
		        	    		   click:function(){
		        	    			   delAbUser();
		        	    		   }
		        	    	   }},'-',
		                   </cms:havePerm>
		        	       <cms:havePerm url='/systemManager/recoveryUser'>
		                   { xtype: 'button',  text: '恢复用户',iconCls:'Edit',
		        	    	   listeners: {
		        	    		   click:function(){
		        	    			   recoveryUser();
		        	    		   }
		        	    	   }}
		        	       </cms:havePerm>
		                  ]

		        },{
		            xtype: 'pagingtoolbar',
		            store: storeRecoveryUser,   // GridPanel使用相同的数据源
		            dock: 'bottom',
		            displayInfo: true,
		            plugins: Ext.create('Ext.ux.ProgressBarPager'),
		            emptyMsg: "没有记录" //没有数据时显示信息
		        }]
		    });
		  //加载数据  
		  
		   storeRecoveryUser.load({params:{start:0,limit:20}}); 
		   var recoverWin = Ext.create('Ext.window.Window',{  
		              title : "已删用户",  
		              width : 1100, 
		              height: 400,
		              //height : 120,  
		              //plain : true,  
		              iconCls : "addicon", 
		              autoScroll:true,
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
		              items : [gridRecovery],
                      buttons : [{  
                        text : "关闭",  
                        minWidth : 70,  
                        handler : function() {  
                        recoverWin.close();  
                       }  
                    }] 
		           });  
		    recoverWin.show();  
    	
    }
    
    
    
    
    //************************************添加用户信息start************************************
	//form表单
	var statusComboStore = new Ext.data.SimpleStore({
	    	fields:['name','value'],
	    	data:[['启用','0'],
	    	      ['禁用','1']
	    	]
	});
    var comboStore = new Ext.data.SimpleStore({
    	fields:['userTypeName','userType'],
    	data:[
    	      ['公司员工','1'],
    	      ['省代理','2'],
    	      ['市代理','3'],
    	      ['项目代理','4']
    	]
    });
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
	                    value: '填写用户相关信息'  
	                       },{
	        			xtype: 'fieldset',
	        			title: '用户基本信息',
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
	        			    	                    //用户账号  
	        			    	                    xtype: 'textfield',
	        			    	                    anchor: '97.5%',
	        			    	                    name: 'userName',  
	        			    	                    fieldLabel: '账号<span style="color:red;">*</span>',
	        			    	                    blankText: '必填字段',
	        			    	                    allowBlank: false 
	        			    	                },{  
	        			    	                    //手机号  
	        			    	                    xtype: 'textfield', 
	        			    	                    anchor: '97.5%',
	        			    	                    name: 'phone',
	        			    	                    blankText: '必填字段',
	        			    	                    fieldLabel: '手机号  <span style="color:red;">*</span>',
	        			    	                    regex: /^(1[3,5,8,7]{1}[\d]{9})|(((400)-(\d{3})-(\d{4}))|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{3,7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)$/,
	        			    	                    allowBlank: false 
	        			    	                },{  
	        			    	                    //密码
	        			    	                  	xtype: "textfield",
	        			    	                    anchor: '97.5%',
	        			    				        inputType: 'password', 
	        			    	                    name: 'psw', 
	        			    	                    blankText: '必填字段',
	        			    	                    fieldLabel: '密码<span style="color:red;">*</span>',  
	        			    	                    allowBlank: false  
	        			    	                },{  
	        			    	                    //密码提示  
	        			    	                    xtype: 'textfield', 
	        			    	                    anchor: '97.5%',
	        			    	                    name: 'pswHints',  
	        			    	                    fieldLabel: '密码提示'  
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
	        				                    allowBlank: false 
	        				                    },{  
	        			    	                    //email 
	        			    	                    xtype: 'textfield', 
	        			    	                    anchor: '97.5%',
	        			    	                    name: 'email',  
	        			    	                    fieldLabel: 'Email<span style="color:red;">*</span>',       
	        			    	                    regex: /^([\w]+)(.[\w]+)*@([\w-]+\.){1,5}([A-Za-z]){2,4}$/, //输入内容合法的正则表达式  
	        			    	                    regexText: 'email格式不正确', //如果输入的文本不合法，提示文本  
	        			    	                    allowBlank: false  
	        			    	                },{  
	        			    	                    //确认密码
	        			    	                  	xtype: "textfield",
	        			    	                    anchor: '97.5%',
	        			    				        inputType: 'password', 
	        			    	                    name: 'confirmPsw', 
	        			    	                    blankText: '必填字段',
	        			    	                    fieldLabel: '确认密码<span style="color:red;">*</span>',  
	        			    	                    allowBlank: false  
	        			    	                }]            
	        			           } ]
	        			          },{  
			    	                    //appGroup hidden 
			    	                    xtype: 'textfield', 
			    	                    anchor: '97.5%',
			    	                    name: 'appIds', 
			    	                    hidden: true,
			    	                    fieldLabel: 'appIds'  
			    	                },{  
	        		                	 xtype: 'textareafield',   
	        			                 name: 'remark',
	        			                 fieldLabel: '备注',   
	        			                 value: '' 
	        		                }
	        			          ]   
	                },{
	        			xtype: 'fieldset',
	        			title: '用户状态及类型',
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
	        			    						fieldLabel: '用户类型<span style="color:red;">*</span>',
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
  				                    fieldLabel: '开户行 '  
  				                  }, {  
    				                    //  开户名 
    				                    xtype: 'textfield', 
    				                    anchor: '97.5%',
    				                    name: 'accountName',  
    				                    fieldLabel: '开户名 '  
    				              }, {  
    	  				                    //  卡号 
    	  				                    xtype: 'textfield', 
    	  				                    anchor: '97.5%',
    	  				                    name: 'accountNumber',  
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
		    	    user_winForm.form.findField('psw').setValue(d.psw); 
		    	    user_winForm.form.findField('confirmPsw').setValue(d.psw); 
		    	    user_winForm.form.findField('pswHints').setValue(d.pswHints);
		     	    user_winForm.form.findField('realName').setValue(d.realName);
		    	    user_winForm.form.findField('email').setValue(d.email); 
		    	    user_winForm.form.findField('remark').setValue(d.remark);
		     	    user_winForm.form.findField('disabled').setValue(d.disabled+'');
		    	    user_winForm.form.findField('remark').setValue(d.remark); 
		    	    var uType = d.userType+'';
		    	    user_winForm.form.findField('userType').setValue(d.userType+'');
		    	    if(uType=='4'){
		    	    	// 项目代理
		    	    	user_winForm.form.findField('appGroup').show();
		    	    }else{
		    	    	user_winForm.form.findField('appGroup').hide();
		    	    }
		     	    user_winForm.form.findField('province').setValue(d.province);
		    	    user_winForm.form.findField('city').setValue(d.city); 
		    	    user_winForm.form.findField('accountBank').setValue(d.accountBank);
		     	    user_winForm.form.findField('accountName').setValue(d.accountName);
		    	    user_winForm.form.findField('accountNumber').setValue(d.accountNumber); 
		    	    user_winForm.form.findField('userName').setReadOnly(true);
		        	title = '编辑账号';
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
	                         text : "保存",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            if (user_winForm.getForm().isValid()) {
	                            	//必选输入验证
	                            	var uType = user_winForm.form.findField('userType').getValue();//1.公司员工；2.省代理；3.市代理；4.项目代理
	                            	var psw = user_winForm.form.findField('psw').getValue();
	                            	var confirmPsw = user_winForm.form.findField('confirmPsw').getValue();
	                            	if(psw!=confirmPsw){
	                            		// 两次密码输入不一致
	                            		Ext.example.msg("提示","两次密码输入不一致，请重新输入！");
	                            		return;
	                            	}
	                            	var province = user_winForm.form.findField('province').getValue();
	                            	var city = user_winForm.form.findField('city').getValue();
	                            	
	                            	if(uType==2){
	                            		if(province==null||province==''){
	                            			Ext.example.msg("提示","省代理必须选择代理的省份！");
		                            		return;
	                            		}
	                            	}else if(uType==3){
	                            		if(province==null||province==''||city==null||city==''){
	                            			Ext.example.msg("提示","市代理必须选择代理的省、市！");
		                            		return;
	                            		}
	                            	}else if(uType==4){
		                            	var appIds='';
		                                var groupItems =user_winForm.form.findField('appGroup').items;
	                                    //CheckboxGroup取值方法
	                                    var flag=false;
	                                    for (var i = 0; i < groupItems.length; i++)     
	                                    {     
	                                        if (groupItems.get(i).checked==true)     
	                                        {     
	                                        	if(flag){
	                                        		appIds=appIds+","+groupItems.get(i).inputValue;    
	                                        	}else{
	                                        		appIds=appIds+groupItems.get(i).inputValue;   
	                                        		flag=true;
	                                        	}
	                                        	                 
	                                        }     
	                                    }
	                                    if(province==null||province==''||city==null||city==''||appIds==null||appIds==''){
	                                		Ext.example.msg("提示","市代理必须选择代理的省、市、项目！");
		                            		return;
	                                    }
	                                    user_winForm.form.findField('appIds').setValue(appIds);
	                            	}
	                                user_winForm.getForm().submit({  
	                                          url :'/systemManager/'+reqName,  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
  
	                                          success: function(fp, o) {  
	                                        	  if (o.result.data == 1) {  
	                                                  syswin.close(); //关闭窗口  
	                                               	  Ext.example.msg("提示",o.result.message);
	                                                  user_winForm.form.reset(); 
	                                                  store.reload();  
	                                              }else { 
	                                             	  Ext.example.msg("提示",o.result.message);
	                                              }  
	                                          },  
	                                          failure: function() {  
	                                         	   Ext.example.msg("提示",o.result.message);
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
	                     }]  
	           });  
	    syswin.show(); 
	    };
	    
	    
	    //************************************添加用户信息end************************************
    
    
});


//************设置成收益账号****************
function setAccount(){
	var rows = grid.getSelectionModel().getSelection();  
    if(rows.length == 0)  
    {  
  	   Ext.example.msg("提示","请选择要设置的账号！");
       return ;  
    }
    if(rows.length > 1)  
    {  
	  Ext.example.msg("提示","只能选择一个账号！");
      return ;  
    }
     var id = rows[0].get('id');
     var userType = rows[0].get('userType');
     if(userType=='1'){
    	   Ext.example.msg("提示","设置收益账号必须是代理商账号！");
           return ; 
     }
     Ext.Msg.confirm("提示信息","设为收益账号后不可更改，请确定?",function (btn){  
         if(btn == 'yes')  
         {  
             Ext.Ajax.request({  
                 url:"/systemManager/setAccount",  
                 params:{id:id},  
                 method:'post',  
                 success:function(o){  
                	// var obj = eval(o);
                	var json = Ext.JSON.decode(o.responseText);
                	var data = json.data;
                	Ext.example.msg("提示",json.message);
                 	if(data==1){
                        store.reload();  
                 	}
                     return ;  
                 },  
                 failure:function(form,action){ 
                  	 Ext.example.msg("提示","服务商删除失败！");
                 }  
             });    
         }  
     }); 
};


//************************************用户授权（分配角色和群组）end************************************ 
//************************************用户分配角色start************************************ 
function roleGrant(userId,userName){
	Ext.QuickTips.init();
	var store = Ext.create('Ext.data.TreeStore', {
        model: 'Task',
        proxy: {
            type: 'ajax',
            url: '/systemManager/getUserRoles?userId=' + userId,
        },
        folderSort: true
    });
 //we want to setup a model and store instead of using dataUrl
    Ext.define('Task', {
        extend: 'Ext.data.Model',
        fields : [
					{name : "text",type : "string"}]
		});

	var tree = Ext.create('Ext.tree.Panel', {
        title: '【'+userName+'】'+'角色分配',
        width: 490,
        height: 400,
        renderTo: Ext.getBody(),
        collapsible: true,
        autoScroll:true,
        useArrows: true,
        rootVisible: false,
        store: store,
        multiSelect: true,
        checkOnly:false,
        singleExpand: true,
        //the 'columns' property is now 'headers'
/**         columns: [{
            xtype: 'treecolumn', //this is so we know which column will show the tree
            text: '资源名',
            width: 240,
            sortable: true,
            dataIndex: 'text'
        }
        ]**/
        
    });
	

 var rolewindow = Ext.create('Ext.window.Window',{  
      title : "分配角色",  
      width: 500,
      height:460,  
      //height : 120,  
      //plain : true,  
      iconCls : "addicon",  
      // 不可以随意改变大小  
      resizable : true, 
      //autoScroll:true,
      // 是否可以拖动  
      // draggable:false,  
      collapsible : true, // 允许缩放条  
      closeAction : 'close',  
      closable : true,  
      // 弹出模态窗体  
      modal : 'true',  
      buttonAlign : "center",  
      bodyStyle : "padding:0 0 0 0",  
      items : [tree],
      buttons : [{  
                 text : "保存",  
                 minWidth : 70,  
                 handler : function() {  
                	var treerows= tree.getChecked( );
                	var roleIds='';
                	var flag = true;
                	for(var i = 0;i<treerows.length;i++)  
                    {  
                		if(treerows[i].get('leaf')){
                    		if(flag==true){
                    			roleIds = roleIds+treerows[i].get('id');  
                    		}else{
                    			roleIds = roleIds+','+treerows[i].get('id');  
                    		}
                    		flag=false;
                		}
                    } 
                	Ext.MessageBox.wait('正在操作','请稍后...');
                	Ext.Ajax.request({  
                         url:"/systemManager/saveUserRoles",  
                         params:{userId:userId,roleIds:roleIds},  
                         method:'post', 
                         waitTitle: '请稍等...',  
                         waitMsg: '正在提交信息...', 
                         success:function(o){ 
                        	 Ext.example.msg("提示","角色分配成功！");
                             rolewindow.close();
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                          	 Ext.example.msg("提示","角色分配失败！");
                             //rolewindow.close();
                         } , 
                         callback:function(){
                        	 Ext.MessageBox.hide();
                         }
                     });    
                    }  
                  
             }, {  
                 text : "关闭",  
                 minWidth : 70,  
                 handler : function() {  
                	 rolewindow.close();  
                 }  
             }]  
   });  
 rolewindow.show();  

};
//************************************用户分配角色end************************************ 

//************************************用户分配群组start************************************ 
function groupGrant(userId,userName){
	Ext.QuickTips.init();
	var store = Ext.create('Ext.data.TreeStore', {
        model: 'Task',
        proxy: {
            type: 'ajax',
            url: '/systemManager/getUserGroups?userId=' + userId,
        },
        folderSort: true
    });
 //we want to setup a model and store instead of using dataUrl
    Ext.define('Task', {
        extend: 'Ext.data.Model',
        fields : [
					{name : "text",type : "string"}]
		});

	var tree = Ext.create('Ext.tree.Panel', {
        title: '【'+userName+'】'+'群组分配',
        width: 490,
        height: 400,
        renderTo: Ext.getBody(),
        collapsible: true,
        autoScroll:true,
        useArrows: true,
        rootVisible: false,
        store: store,
        multiSelect: true,
        checkOnly:false,
        singleExpand: true,
        //the 'columns' property is now 'headers'
/**         columns: [{
            xtype: 'treecolumn', //this is so we know which column will show the tree
            text: '资源名',
            width: 240,
            sortable: true,
            dataIndex: 'text'
        }
        ]**/
        
    });
	

 var groupwindow = Ext.create('Ext.window.Window',{  
      title : "分配群组",  
      width: 500,
      height:460,  
      //height : 120,  
      //plain : true,  
      iconCls : "addicon",  
      // 不可以随意改变大小  
      resizable : true, 
      //sautoScroll:true,
      // 是否可以拖动  
      // draggable:false,  
      collapsible : true, // 允许缩放条  
      closeAction : 'close',  
      closable : true,  
      // 弹出模态窗体  
      modal : 'true',  
      buttonAlign : "center",  
      bodyStyle : "padding:0 0 0 0",  
      items : [tree],
      buttons : [{  
                 text : "保存",  
                 minWidth : 70,  
                 handler : function() {  
                	var treerows= tree.getChecked( );
                	var groupIds='';
                	var flag = true;
                	for(var i = 0;i<treerows.length;i++)  
                    {  
                		if(treerows[i].get('leaf')){
                    		if(flag==true){
                    			groupIds = groupIds+treerows[i].get('id');  
                    		}else{
                    			groupIds = groupIds+','+treerows[i].get('id');  
                    		}
                    		flag=false;
                		}
                    } 
                	Ext.MessageBox.wait('正在操作','请稍后...');
                	Ext.Ajax.request({  
                         url:"/systemManager/saveUserGroups",  
                         params:{userId:userId,groupIds:groupIds},  
                         method:'post', 
                         waitTitle: '请稍等...',  
                         waitMsg: '正在提交信息...', 
                         success:function(o){
                          	 Ext.example.msg("提示","群组分配成功！");
                             groupwindow.close();
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                           	 Ext.example.msg("提示","群组分配失败！");
                         } , 
                         callback:function(){
                        	 Ext.MessageBox.hide();
                         }
                     });    
                    }  
                  
             }, {  
                 text : "关闭",  
                 minWidth : 70,  
                 handler : function() {  
                	 groupwindow.close();  
                 }  
             }]  
   });  
 groupwindow.show();  

};
//************************************用户分配群组end************************************ 
//************************************用户授权（分配角色和群组）end************************************ 

</script>
</head>
<body>
</body>
</html>