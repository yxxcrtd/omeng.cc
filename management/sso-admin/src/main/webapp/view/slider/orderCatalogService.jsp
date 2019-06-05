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
<title>系统参数</title>
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
                   {header:'服务名称',dataIndex:'service_type_name',sortable:true,fixed:false},
                   {header:'发布状态',dataIndex:'status',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:red;font-weight:bold';>未发布</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>已发布</span>";  
                       }else{
                    	   return "<span style='font-weight:bold';></span>";  
                       }
           	       }},
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/slider/getOrderCatalog',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'service_type_name'},
           {name:'status'} 
           
        ]  
    });
    var statusStore = new Ext.data.SimpleStore({
     	fields:['statusName','statusType'],
     	data:[['全部',''],
     	      ['未发布','0'],
     	      ['已发布','1']
     	]
     });
    
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var catalogid=Ext.getCmp('catalogid').getValue();
    	 var status=Ext.getCmp('status').getValue();
		 var service_type_name=Ext.getCmp('service_type_name').getValue();
         var new_params = {service_type_name:service_type_name,catalogid:catalogid,status:status};    
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
	    	   <cms:havePerm url='/slider/addOrderCatalogService'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   addCatalogService('${catalogid}');
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/slider/deleteOrderCatalogService'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		       }},'-',
			   </cms:havePerm>
		       <cms:havePerm url='/slider/auditOrderCatalogService'>
	           { xtype: 'button', id:'audit', text: '发布',iconCls:'Audit',
		    	   listeners: {
		    		   click:function(){
		    			   auditRecord(1);
		    		   }
		       }},'-',
	           { xtype: 'button', id:'caudit', text: '撤回',iconCls:'Chehui',
		    	   listeners: {
		    		   click:function(){
		    			   auditRecord(0);
		    		   }
		       }},'-',
			   </cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{xtype : 'combobox',id : 'status',name : 'status',fieldLabel : '发布状态',value:'',valueField : 'statusType',editable:false,displayField : 'statusName',
				   store : statusStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
				   
        	       { xtype: 'textfield',id:'catalogid',name: 'catalogid',fieldLabel: '分类ID',value:'${catalogid}',labelAlign:'left',labelWidth:70,hidden:true},'-',
        	       { xtype: 'textfield',id:'service_type_name',name: 'service_type_name',fieldLabel: '服务名称',labelAlign:'left',labelWidth:70},'-',

        	       <cms:havePerm url='/slider/getOrderCatalog'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var status=Ext.getCmp('status').getValue();
    	    			   var catalogid=Ext.getCmp('catalogid').getValue();
    	    			   var service_type_name=Ext.getCmp('service_type_name').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,service_type_name:service_type_name,catalogid:catalogid,status:status}}); 
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
                     url:"/slider/deleteOrderCatalogService",  
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
      
  //审核服务项目
	function auditRecord(status)  
    {  
	     var title = "【发布】";
	     if(status=='0'){
	    	 title = "【撤回】";
	     }
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
   		     Ext.example.msg("提示","请选择要"+title+"的对象！"); 
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
         Ext.Msg.confirm("提示信息","请确定要执行"+title+"操作吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/slider/auditOrderCatalogService",  
                     params:{id:ids,status:status},  
                     method:'post',  
                     success:function(o){   
               		     Ext.example.msg("提示",title+"成功！"); 
                         store.reload();  
                         return ;  
                     },  
                     failure:function(form,action){  
                    	 Ext.example.msg("提示",title+"失败！"); 
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
			height : 460,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [{
				// 字典类型
				xtype : 'textfield',
				name : 'service_type_id',
				allowBlank	:false,
				fieldLabel : '服务类型ID'
			}, {
				// 字典记录主键
				xtype : 'textfield',
				name : 'service_type_name',
				allowBlank	:false,
				fieldLabel : '服务类型名称'
			}, 
			{
				//字典记录值
				xtype : 'textfield',
				name : 'app_name',
				allowBlank	:false,
				fieldLabel : '应用名称'
			}, {
				// 备注
				xtype : 'textfield',
				name : 'app_type',
				fieldLabel : '应用类型'
			}, //父级字典ID
			{
				// 父级字典ID
				xtype : 'textfield',
				name : 'parent_id',
				fieldLabel : '父级ID'
			},{  
                //下拉列表框  
                xtype: 'combobox', //9  
                fieldLabel: '叶子',
                displayField: 'value',
                valueField : 'key',
                name:'is_leaves',
                store: Ext.create('Ext.data.Store', {  
                    fields: [  
                      {name:'key'},{ name: 'value' }  
                      ],  
                                      data: [  
                             { 'key':'0','value': '否' },  
                             { 'key':'1','value': '是' }  
                      ]  
                })
               
            }]

		});

		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增服务类型",
			width : 450,
			height : 450,
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
							url : '/slider/addOrderCatalog',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',

							success : function(fp, o) {
								    Ext.example.msg("提示","保存成功！"); 
									syswin.close(); //关闭窗口  
									store.reload();
							
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
	
	    
//添加服务	    
	  
		var addCatalogService = function(catalogid){  
			var columns = [ {header:'序号',xtype: 'rownumberer',width:50},
			                {header:'ID',dataIndex:'id',sortable:true,fixed:false},
			                {header:'服务名称',dataIndex:'service_type_name',sortable:true,fixed:false}
			];
		

		//列表展示数据源
		var storeCatalogService = Ext.create("Ext.data.Store", {
			pageSize : 20, //每页显示几条数据  
			proxy : {
				type : 'ajax',
				url : '/systemManager/getServiceType?is_pub=1',
				reader : {
					type : 'json',
					totalProperty : 'total',
					root : 'data',
					idProperty : '#'
				}
			},
			fields : [  {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
			            
			            {name:'service_type_name'}   ]
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
            height:400,  
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
                       text : "添加",  
                       minWidth : 70,  
                       handler : function() {
                    	 var rowsCata = gridCatalogService.getSelectionModel().getSelection();
                    	 if(rowsCata.length <= 0){
                   		     Ext.example.msg("提示","请选择要添加的服务！"); 
                             return ;  
                    	 }
                         //ids：所有选中的用户Id的集合使用','隔开，初始化为空  
                         var ids = '';  
                         for(var i = 0;i<rowsCata.length;i++)  
                         {  
                            if(i>0){  
                            	ids = ids+','+rowsCata[i].get('id');  
                            }else{  
                            	ids = ids+rowsCata[i].get('id');  
                            }  
                         }  
                      	 Ext.Ajax.request({  
                      		 url :'/slider/addOrderCatalogService',  
                      		 params:{id:ids,catalogid:catalogid},  
                               method:'post',  
                               success:function(fp, o){  
                            	   if (fp.responseText== 'true') { 
                                   Ext.example.msg("提示","添加成功！"); 
                                   issuewindow.close();
                                   store.reload();  
                                   return ;  
                            	   }else{
                            		   Ext.example.msg("提示","请不要添加重复服务！"); 
                            		   issuewindow.close();
                            	   }
                               },  
                               failure:function(form,action){  
                            	   Ext.example.msg("提示","请不要添加重复服务！"); 
                               }  
                           }); 
                      	 }   
            } ,{
				text : "关闭",
				minWidth : 70,
				handler : function() {
					issuewindow.close();
				}
			}]                  		
         });   
		    issuewindow.show();  
		    };
	    
	    
	
});



</script>
</head>
<body>
</body>
</html>