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
<title>推送关联服務</title>
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
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false},
                   {header:'个性标签',dataIndex:'gxfwName',sortable:true,fixed:false},
                   {header:'行业服务',dataIndex:'serviceTypeName',sortable:true,fixed:false}
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/systemManager/getPushConfig',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'id'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'gxfwName'},  
           {name:'serviceTypeName'}
        ]  
    });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var gxfwName=Ext.getCmp('gxfwName').getValue();
		 var serviceTypeName=Ext.getCmp('serviceTypeName').getValue();
         var new_params = {gxfwName:gxfwName,serviceTypeName : serviceTypeName};    
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
           
	    	   <cms:havePerm url='/systemManager/addSystemParam'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform();
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/systemManager/deleteSystemParam'>
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
        	items:[{ xtype: 'textfield',id:'gxfwName',name: 'gxfwName',fieldLabel: '个性标签',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'textfield',id:'serviceTypeName',name: 'serviceTypeName',fieldLabel: '行业服务',labelAlign:'left',labelWidth:60},'-',

        	       <cms:havePerm url='/systemManager/getPushConfig'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			     var gxfwName=Ext.getCmp('gxfwName').getValue();
    	    				 var serviceTypeName=Ext.getCmp('serviceTypeName').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,gxfwName:gxfwName,serviceTypeName:serviceTypeName}}); 
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
                     url:"/systemManager/deletePushConfig",  
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
      
    
  //form表单
	var showform = function() {
		var add_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 440,
			height : 260,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [{
				xtype: 'hidden',
				name: "gxfwServiceTypeId"
			},{
				xtype: 'hidden',
				name: "serviceTypeId"
			},{
                xtype: "triggerfield",
                name: "gxfwName",
                fieldLabel: "个性标签",
                editable:false,
                hideTrigger: false,
                allowBlank	:false,
                onTriggerClick: function () {
                	addService(add_winForm,1);
                }
            },{
                xtype: "triggerfield",
                name: "serviceTypeName",
                fieldLabel: "行业服务",
                editable:false,
                hideTrigger: false,
                allowBlank	:false,
                onTriggerClick: function () {
                   addService(add_winForm,2);
                }
            }]

		});

		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增推送关联服务",
			width : 450,
			height : 260,
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
							url : '/systemManager/addPushConfig',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',
							success : function(fp, o) {
								if (o.result== true) { 
				              	    Ext.example.msg("提示","保存成功！");                                                                                     
									syswin.close(); //关闭窗口  
									store.reload();
								}else{
									Ext.example.msg("提示","失败！"); 
								}
							},
							failure : function(fp, o) {
								 Ext.example.msg("提示",o.response.responseText);
								}
						});
					}else{
						Ext.example.msg("提示","请填写完整信息！");
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
		
		 //***************************************添加个性或行业服务  start*************************************
 	    
	 	     var addService = function(add_winForm,type){  
	 		 var url='/systemManager/getServiceType';
			 if(type==1){
				 url='/slider/getServiceType';
			 }else if(type==2){
				 url='/systemManager/getServiceType';
			 }
				var columns = [ {header:'序号',xtype: 'rownumberer',width:50},
				                {header:'ID',dataIndex:'id',sortable:true,fixed:false},
				                {header:'服务名称',dataIndex:'service_type_name',sortable:true,fixed:false}
				];
			//创建store数据源

			var storeCatalogService = Ext.create("Ext.data.Store", {
				pageSize : 20, //每页显示几条数据  
				proxy : {
					type : 'ajax',
					url : url,
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
	            width: 500,
	            height:500,  
	            //height : 120,  
	            //plain : true,  
	            iconCls : "addicon",  
	            // 不可以随意改变大小  
	            resizable : true,  
	            // 是否可以拖动  
	           // draggable:true, 
	            autoScroll: true,
	            collapsible : true, // 允许缩放条  
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
	                    	 if(type==1){
	                    		 add_winForm.form.findField('gxfwServiceTypeId').setValue(rowsCata[0].get("id"));
	                    		 add_winForm.form.findField('gxfwName').setValue(rowsCata[0].get("service_type_name"));
	                    	 }else{
	                    		 add_winForm.form.findField('serviceTypeId').setValue(rowsCata[0].get("id"));
	                    		 add_winForm.form.findField('serviceTypeName').setValue(rowsCata[0].get("service_type_name")); 
	                    	 }
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

</script>
</head>
<body>
</body>
</html>