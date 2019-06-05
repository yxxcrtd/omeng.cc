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
<title>推送管理</title>
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
                   {header : '服务类型ID',dataIndex : 'service_type_id',align:'center',hidden:true}, 
                   {header : '服务类型',dataIndex : 'service_name',align:'center'}, 
                   {header:'推送类型',dataIndex:'push_type',sortable:true,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:fe7352;font-weight:bold';>城市</span>";  
                       }else if(value=='2'){  
                           return "<span style='color:e872af;font-weight:bold';>标签</span>";  
                       }else if(value=='3'){  
                           return "<span style='color:red;font-weight:bold';>其他</span>";  
                       }else{
                    	   return "<span style='color:green;font-weight:bold';>经纬度</span>"; 
                       }
           		   }},
                   {header:'推送距离',dataIndex:'push_range',sortable:true,fixed:false}
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/systemManager/pushParamList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'service_name'},
           {name:'service_type_id'},
           {name:'push_type'},
           {name:'push_range'}
        ]  
    });
	
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var service_type_name = Ext.getCmp('service_type_name').getValue();
         var new_params = {service_type_name : service_type_name};    
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
	    	   <cms:havePerm url='/systemManager/addPushParam'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform(null);
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/systemManager/deletePushParams'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		    	}}
			   </cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	       {
				xtype : 'textfield',
				id : 'service_type_name',
				name : 'service_type_name',
				fieldLabel : '服务名称',
				labelAlign : 'left',
				labelWidth : 60
			      },'-',
        	       <cms:havePerm url='/systemManager/pushParamList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var service_type_name = Ext.getCmp('service_type_name').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,service_type_name : service_type_name}}); 
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
    
    
    <cms:havePerm url='/systemManager/editPushParam'>
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data); 
    });</cms:havePerm>
    
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
	
  
	

	//删除
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
                     url:"/systemManager/deletePushParams",  
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
      
    //推送类型。0按照经纬度。1按照城市，2按照标签，3其他
	var commonStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['经纬度','0'],
    	      ['城市','1'],
    	      ['标签','2'],
    	      ['其他','3']
    	]
    });
    
	//form表单编辑用户
	var showform=function(d){  
	    var edit_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 440,
	                height:400,
	                bodyPadding:5,  
	                //renderTo:"panel21",  
	                fieldDefaults: {  
	                    labelAlign: 'left',  
	                    labelWidth: 90,  
	                    anchor: '100%'  
	                },  
	                items: [ {
	    				// id
	    				xtype : 'textfield',
	    				name : 'id',
	    				hidden	:true
	    			},{
	    				// 
	    				xtype : 'textfield',
	    				name : 'service_id',
	    				hidden:true
	    			
	    			},{
	                    xtype: "triggerfield",
	                    name: "service_name",
	                    fieldLabel: "服务名称",
	                    editable:false,
	                    hideTrigger: false,
	                    allowBlank	:false,
	                    onTriggerClick: function () {
	                        addService(edit_winForm);
	                       
	                    }
	                },{
             	xtype:'combo',
			    store : commonStore,
    		    width:80,
    		    name:'push_type',
   			    triggerAction: 'all',
   			    displayField: 'name',
   			    valueField: 'value',
   			    hiddenName:'push_type',
   			    value:'0',
			    mode:'local',
			    fieldLabel: '推送类型' 
		   },{
				xtype : 'numberfield',
				name : 'push_range',
				fieldLabel : '推送距离'
			}]  
	            });  

	    var title = '新增推送参数';
	    var reqName = 'addPushParam';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    edit_winForm.form.findField('id').setValue(d.id);
	    	    edit_winForm.form.findField('service_id').setValue(d.service_type_id);  
	    	    edit_winForm.form.findField('service_name').setValue(d.service_name);  
	    	    edit_winForm.form.findField('push_type').setValue(d.push_type+'');
	    	    edit_winForm.form.findField('push_range').setValue(d.push_range); 
	        	title = '编辑推送参数';
	        	reqName = 'editPushParam';
	    	}

        //================判断是编辑还是新增===============
	    //创建window面板，表单面板是依托window面板显示的  
	    var editwindow = Ext.create('Ext.window.Window',{  
	              title : title,  
	              width: 450,
	              height:250,  
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
	                                          url :'/systemManager/'+reqName,  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
	                                            
	                                          success: function(fp, o) {  
	                                              if (o.result.data == 1) {  
	                                            	  editwindow.close(); //关闭窗口  
	                                               	  Ext.example.msg("提示",o.result.message);
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
	                            else{
	                            	Ext.example.msg("提示","请填写完整信息");
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
	    
	  //***************************************添加个性服务  start*************************************
 	    
	 	   var addService = function(add_RecForm){  
				var columns = [ {header:'序号',xtype: 'rownumberer',width:50},
				                {header:'ID',dataIndex:'id',sortable:true,fixed:false},
				                {header:'服务名称',dataIndex:'service_type_name',sortable:true,fixed:false}
				];
			//创建store数据源

			var storeCatalogService = Ext.create("Ext.data.Store", {
				pageSize : 20, //每页显示几条数据  
				proxy : {
					type : 'ajax',
					url : '/systemManager/getServiceType',
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
	            width: 600,
	            height:500,  
	            //height : 120,  
	            //plain : true,  
	            iconCls : "addicon",  
	            // 不可以随意改变大小  
	            resizable : true,  
	            // 是否可以拖动  
	            draggable:true, 
	            autoScroll: true,
	           // collapsible : true, // 允许缩放条  
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
	                    	 add_RecForm.form.findField('service_id').setValue(rowsCata[0].get("id"));
	                    	 add_RecForm.form.findField('service_name').setValue(rowsCata[0].get("service_type_name"));
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