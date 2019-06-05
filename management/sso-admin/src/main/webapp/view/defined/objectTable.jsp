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
<title>自定义表单</title>
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
                   {header:'表单ID',dataIndex:'object_id',sortable:true,fixed:false,align:'center'},
                   {header:'描述',dataIndex:'remark',sortable:true,fixed:false,align:'center'},
                  // {header:'有效值',dataIndex:'disabled',sortable:true,fixed:false,align:'center'},
                   {header:'服务名称',dataIndex:'service_type_name',sortable:true,fixed:false,align:'center'},
                   {header:'发布状态',dataIndex:'is_publish',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>已发布</span>";  
                       } else{  
                           return "<span style='color:green;font-weight:bold';>未发布</span>";  
                   }}},
                   {header:'操作',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                       return '<a href="javascript:showDetail(\''+r.data.object_id+'\',\''+r.data.service_type_name+'\')">详情</a>';
             		}}
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/defined/getObjectTableList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'id'  
            }  
        },  
        fields:[  
           {name:'object_id'}, 
           {name:'remark'},
           {name:'is_publish'},
           {name:'service_type_name'}
        ]  
    });
    
 
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	
		 var service_type_name=Ext.getCmp('service_type_name').getValue();
		 var new_params = {service_type_name:service_type_name};    
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
           
	    	   <cms:havePerm url='/defined/addObjectTable'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform(null);
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/defined/deleteObjectTable'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		    	   }},'-',
			   </cms:havePerm>
			   <cms:havePerm url='/defined/pubObjectTable'>
		       { xtype: 'button', id:'pub', text: '发布',iconCls:'Fabu',
			    	   listeners: {
			    		   click:function(){
			    			   pubObject(1);
			    		   }
			       }},'-',
				</cms:havePerm>
				<cms:havePerm url='/defined/pubObjectTable'>
			       { xtype: 'button', id:'unpub', text: '撤回',iconCls:'Chehui',
				    	   listeners: {
				    		   click:function(){
				    			   pubObject(0);
				    		   }
				    }},'->',
				</cms:havePerm> 

			   <cms:havePerm url='/defined/pubAllObjectTable'>
		           { xtype: 'button', id:'pubAll', text: '发布全部',iconCls:'Fabu',
			    	   listeners: {
			    		   click:function(){
			    			   pubAllObject(1);
			    		   }
			    	   }},'-',
				</cms:havePerm>
				<cms:havePerm url='/defined/pubAllObjectTable'>
			           { xtype: 'button', id:'unpubAll', text: '撤回全部',iconCls:'Chehui',
				    	   listeners: {
				    		   click:function(){
				    			   pubAllObject(0);
				    		   }
				    	   }}
				</cms:havePerm> 	   
			      
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'service_type_name',name: 'service_type_name',fieldLabel: '服务名称',labelAlign:'left',labelWidth:60},'-',
        	       
        	       <cms:havePerm url='/defined/getObjectTableList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var service_type_name=Ext.getCmp('service_type_name').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,service_type_name:service_type_name}}); 
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
    
    <cms:havePerm url='/defined/editObjectTable'>
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
            	ids = ids+','+records[i].get('object_id');  
            }else{  
            	ids = ids+records[i].get('object_id');  
            }  
         }  
         Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/defined/deleteObjectTable",  
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
    
	//选择性发布/撤回
	function pubObject(isPub)  
    {  
		var title = '【撤回】';
		if(isPub){
			title = '【发布】';
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
            	ids = ids+','+records[i].get('object_id');  
            }else{  
            	ids = ids+records[i].get('object_id');  
            }  
         }  
         Ext.Msg.confirm("提示信息","请确定要执行"+title+"操作吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/defined/pubObjectTable",  
                     params:{ids:ids,isPub:isPub},  
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
    } 
    
	//全部发布/撤回
	function pubAllObject(isPub)  
    {  
		var title = '【全部撤回】';
		if(isPub){
			title = '【全部发布】';
		}
         Ext.Msg.confirm("提示信息","请确定要执行"+title+"操作吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/defined/pubAllObjectTable",  
                     params:{isPub:isPub},  
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
    } 
      
    
  //form表单
	var showform = function(d) {
		var add_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 440,
			height : 360,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [{
				// id
				xtype : 'textfield',
				name : 'id',
				hidden	:true
			},{
				// 服务类型id
				xtype : 'textfield',
				name : 'service_type_id',
				hidden	:true
			},{
				// 描述
				xtype : 'textfield',
				name : 'remark',
				fieldLabel : '描述'
			},{
                xtype: "triggerfield",
                name: "service_type_name",
                fieldLabel: "服务名称",
                editable:false,
                hideTrigger: false,
                allowBlank	:false,
                onTriggerClick: function () {
                    addService(add_winForm);
                }
            } 
			
			]

		});

		
		 var title = '新增表单';
		  var reqName = 'addObjectTable';
		    //================判断是编辑还是新增===============
		    	if(d!=null&&d.object_id!=null&&d.object_id!=0){
		    		var rows = grid.getSelectionModel().getSelection();
		    		// 是编辑状态
		    	    add_winForm.form.findField('id').setValue(rows[0].get('object_id'));
		    	    add_winForm.form.findField('service_type_id').setValue(rows[0].raw.service_type_id); 
		    	    add_winForm.form.findField('service_type_name').setValue(rows[0].get('service_type_name'));
		    	    add_winForm.form.findField('remark').setValue(rows[0].get('remark')); 
		    	   
		        	title = '编辑表单';
		        	reqName = 'editObjectTable';
		    	}
		
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : title,
			width : 450,
			height : 350,
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
							url : '/defined/'+reqName,
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
	                    	 add_winForm.form.findField('service_type_id').setValue(rowsCata[0].get("id"));
	                    	 add_winForm.form.findField('service_type_name').setValue(rowsCata[0].get("service_type_name"));
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

function showDetail(id,name){
	  if(name=='null'){
			name='';
		}
	  parent.addTab("object_table_"+id,"表【"+name+"】详情","Fuwushangyunyingxinxiguanli","/defined/objectModelShow?object_id="+id); 	 
}

</script>
</head>
<body>
</body>
</html>