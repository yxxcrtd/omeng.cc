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
<title>订单奖励活动开放服务</title>
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
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,hidden:true},
                   {header:'行业',dataIndex:'app_name',sortable:true,fixed:false},
                   {header:'服务类型',dataIndex:'service_name',sortable:true,fixed:false}
                  
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/activity/getOrderRewardOpenService',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'app_name'}, 
           {name:'service_name'} 
        ]  
    });
    
 // app类型
	var store_appType = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
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
		pageSize : 50, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/getServiceByCatId',
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
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var app_type=Ext.getCmp('app_type').getValue();
		 var service_id=Ext.getCmp('service_id').getValue();
    	 var activity_id=Ext.getCmp('activity_id').getValue();
         var new_params = {activity_id:activity_id,app_type:app_type,service_id:service_id};    
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
	    	   <cms:havePerm url='/activity/addOrderRewardOpenService'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   addOrderRewardOpenService('${activity_id}');
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/activity/deleteOrderRewardOpenService'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		       }},'-',
			   </cms:havePerm>
		       
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	       { xtype: 'textfield',id:'activity_id',name: 'activity_id',fieldLabel: '活动ID',value:'${activity_id}',labelAlign:'left',labelWidth:70,hidden:true},'-',
        	       {xtype : 'combobox',id : 'app_type',name : 'app_type',fieldLabel : 'app类型',valueField : 'app_type',
       				displayField : 'app_name',width : 200,store : store_appType,editable:true,
       				listeners : { // 监听该下拉列表的选择事件
       					select : function(combobox,record,index) {
       						Ext.getCmp('service_id').setValue('');
       						store_ServiceType
       								.load({
       									params : {
       										app : combobox.value
       									}
       								});
       					}
       				},
       				queryMode : 'local',
       				labelAlign : 'left',
       				labelWidth : 60
       			},'-',
       			{xtype : 'combobox',id : 'service_id',name : 'service_id',width : 200,fieldLabel : '服务类型',valueField : 'service_type',displayField : 'service_name',
       				store : store_ServiceType,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
        	       <cms:havePerm url='/slider/getOrderRewardOpenCity'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var app_type=Ext.getCmp('app_type').getValue();
    	    				 var service_id=Ext.getCmp('service_id').getValue();
    	    		    	 var activity_id=Ext.getCmp('activity_id').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,activity_id:activity_id,app_type:app_type,service_id:service_id}}); 
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
    store_appType.load(); 
   
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
	
	

	//删除字典值
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
                     url:"/activity/deleteOrderRewardOpenService",  
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
	
	    
  //添加服务	  
	var addOrderRewardOpenService = function(activity_id) {
		var add_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 440,
			height : 200,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [
            {
	            xtype: 'hidden',
	            name: "activity_id"
            },
			{
				xtype : 'combobox',
				name : 'app_type',
				fieldLabel : 'app类型',
				valueField : 'app_type',
				displayField : 'app_name',
				store : store_appType,
				editable:true,
				listeners : { // 监听该下拉列表的选择事件
					select : function(combobox,record,index) {
						add_winForm.form.findField('service_id').setValue('');
						store_ServiceType.load({
									params : {
										app : combobox.value
									}
								});
					}
				}
			},{
				xtype : 'combobox',
				name : 'service_id',
				fieldLabel : '服务类型',
				valueField : 'service_type',
				displayField : 'service_name',
				store : store_ServiceType,
				queryMode : 'local',
				editable:true
			}]

		});

	    var title = '新增服务';
	    var reqName = 'addOrderRewardOpenService';
	    add_winForm.form.findField('activity_id').setValue(activity_id);
	    add_winForm.form.findField('app_type').setValue('');
 	    add_winForm.form.findField('service_id').setValue('');
	    	 
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : title,
			width : 450,
			height : 220,
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
						var at = add_winForm.form.findField('app_type').getValue();
						if(at==null||at==''){
						 	Ext.example.msg("提示","请选择app类型！");
							return;
						}
						
						add_winForm.getForm().submit({
							url : '/activity/'+reqName,
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',
						    method : "POST",  
						    success : function(fp, o) {
                                    syswin.close(); //关闭窗口  
                                    Ext.example.msg("提示","保存成功");
                                    store.reload();  
                                  
							},
							failure : function() {
		                         syswin.close(); //关闭窗口  
                                 Ext.example.msg("提示","请不要重复开通行业");
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