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
<title>热门搜索管理</title>
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
                   {header:'编号',dataIndex:'id',hidden:true,align:'center'},
                   {header:'服务名称',dataIndex:'service_name',align:'center'},
                   {header:'搜索次数',dataIndex:'search_count',align:'center'},
                   {header:'排序',dataIndex:'rank',align:'center'},
                   {header:'是否固定',dataIndex:'is_fixed',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:green;font-weight:bold';>否</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>是</span>";  
                       }
           		   }}
               ];
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/slider/getSearchStatistics',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'},   
           {name:'service_name'},
           {name:'search_count'},
           {name:'rank'},
           {name:'is_fixed'}
        ]  
    });
	
	// 固定状态
    var IsFixedStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['全部',''],
    	      ['否','0'],
    	      ['是','1']
    	]
    });
    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {  
	    var service_name=Ext.getCmp('service_name').getValue();
		var is_fixed=Ext.getCmp('is_fixed').getValue();  
        var new_params = {service_name:service_name,is_fixed:is_fixed};    
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
        	       <cms:havePerm url='/slider/editSearchStatistics'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },</cms:havePerm>
        	       <cms:havePerm url='/slider/deleteSearchStatistics'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	    	   }},'-',</cms:havePerm>
        	    	  
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	       
               { xtype: 'textfield',id: 'service_name',name: 'service_name',fieldLabel: '服务名称',labelAlign:'left',labelWidth:60},'-',
 
			  {xtype:'combo',fieldLabel : '是否固定',id: 'is_fixed',store : IsFixedStore,name:'is_fixed', displayField: 'name',
	   			    valueField: 'value',editable:false,labelWidth:60,mode:'local'},'-',
   	                 
	   			    <cms:havePerm url='/slider/getSearchStatistics'>
        	       { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var service_name=Ext.getCmp('service_name').getValue();
    	    				var is_fixed=Ext.getCmp('is_fixed').getValue();   
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,service_name:service_name,is_fixed:is_fixed}}); 
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
   
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    <cms:havePerm url='/slider/editSearchStatistics'>
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
	
	//推荐位删除
	 function delUserAll()  
     {  
          //grid中复选框被选中的项  
            
          var rows = grid.getSelectionModel().getSelection();  
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
          //没有选择要执行操作的对象  
            
          if(id == "")  
          {  
			 Ext.example.msg("提示","请选择要删除的对象！");
             return ;  
          }else{  
             Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
                 if(btn == 'yes')  
                 {  
                     Ext.Ajax.request({  
                         url:"/slider/deleteSearchStatistics",  
                         params:{id:id},  
                         method:'post',  
                         success:function(o){  
                        	 Ext.example.msg("提示","热门搜索删除成功！");
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                        	 Ext.example.msg("提示","热门搜索删除失败！");
                         }  
                     });    
                 }  
             });  
          }  
     } ;
    
 	var showform=function(d){
 		var rows = grid.getSelectionModel().getSelection();
 	    var add_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 470, 
 	                height:380,
 	               // autoScroll: true, 
 	                bodyPadding:5,
 	                fileUpload:true,
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
 	                    value: '填写推荐位相关信息'  
 	                     
 	                }, {
	    				xtype: 'hidden',
	    				name: "id"
	    		   },{
	    				xtype: 'hidden',
	    				name: "service_id"
	    		   },{
                       xtype: "triggerfield",
                       name: "service_name",
                       fieldLabel: "服务名称",
                       editable:false,
                       hideTrigger: false,
                       allowBlank:false,
                       onTriggerClick: function () {
                           	addService(add_winForm);
                           
                       }
                   },
	    		   {
             	   //下拉列表框  
                   xtype: 'combobox', //9  
                   fieldLabel: '是否固定',
                   displayField: 'value',
                   valueField : 'key',
                   name:'is_fixed',
                   store: Ext.create('Ext.data.Store', {  
                       fields: [  
                         {name:'key'},{ name: 'value'}  
                         ],  
                          data: [  
                         { 'key':'0','value': '否' },  
                         { 'key':'1','value': '是' } 
                         ]  
                   }), width:220
   			    },{
   					// 输入排序
   					xtype : 'numberfield',
   					name : 'rank',
   					allowDecimals: false,
   					minValue: 0,
   					fieldLabel : '排序'
   				}]  
 	            });  
 	    
	    var title = '新增服务项目';
	    var reqName = 'editSearchStatistics';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_winForm.form.findField('id').setValue(rows[0].get("id"));
	    	    add_winForm.form.findField('service_name').setValue(rows[0].get("service_name"));
	    	    add_winForm.form.findField('service_id').setValue(rows[0].raw.service_id);
	    	    add_winForm.form.findField('is_fixed').setValue(rows[0].get("is_fixed")+'');
	    	    add_winForm.form.findField('rank').setValue(rows[0].raw.rank);
	        	title = '编辑服务项目';
	        	reqName = 'editSearchStatistics';
	    	}
	

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 500, 
 	              height: 380,
 	              //height : 120,  
 	              //plain : true,  
 	              iconCls : "addicon",  
 	              // 不可以随意改变大小  
 	              resizable : false,  
 	              // autoScroll: true,  
 	              // 是否可以拖动  
 	              // draggable:false,  
 	              collapsible : true, // 允许缩放条  
 	              closeAction : 'close',  
 	              closable : true,  
 	              // 弹出模态窗体  
 	              modal : 'true',  
 	              buttonAlign : "center",  
 	              bodyStyle : "padding:0 0 0 0",  
 	              items : [add_winForm],  
 	              buttons : [{  
 	                         text : "保存",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	
 	                        	if (add_winForm.getForm().isValid()) {
 	                        		add_winForm.getForm().submit({  
 	                                          url :'/slider/'+reqName,  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',  
 	                                            
 	                                          success: function(fp, o) {  
 	                                              if (o.result== true) { 
 	                                               	 Ext.example.msg("提示","保存成功！");
 	                                                 syswin.close(); //关闭窗口  
 	                                                 store.reload();  
 	                                              }else { 
 	                                               	 Ext.example.msg("提示","保存失败！");
 	                                              }  
 	                                          },  
 	                                          failure: function() {
 	                                          	 Ext.example.msg("提示","保存失败！");
 	                                          }  
 	                                       });  
 	                            } } 
 	                         }  
 	                    , {  
 	                         text : "关闭",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                            syswin.close();  
 	                         }  
 	                     }]  
 	           });  
 	    syswin.show();  
 	    
 	   //***************************************添加个性服务  start*************************************
 	    
	 	   var addService = function(add_winForm){  
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
	            width: 1200,
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
	                    	 add_winForm.form.findField('service_id').setValue(rowsCata[0].get("id"));
	                    	 add_winForm.form.findField('service_name').setValue(rowsCata[0].get("service_type_name"));
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