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
<title>推荐服务</title>
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
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50},
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false},
                   {header:'标题',dataIndex:'title',sortable:true,fixed:false},
                   {header:'推荐服务',dataIndex:'serviceTypeName',sortable:true,fixed:false},
                   {header:'状态',dataIndex:'is_pub',align:'center',renderer:function(value,v,r){  
           			 if(value==1){  
                         return '<a href="javascript:StartOrStopAct(\''+r.data.id+'\','+value+')"><span style="color:green;font-weight:bold;">启用</span></a>';
                    } else if(value==0){  
                         return '<a href="javascript:StartOrStopAct(\''+r.data.id+'\','+value+')"><span style="color:red;font-weight:bold;">暂停</span></a>';
                       }
        		   }}
               ];
    
     store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/slider/getRecommendService',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'id'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'title'},  
           {name:'serviceTypeName'},  
           {name:'is_pub'}
        ]  
    });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var title=Ext.getCmp('title').getValue();
		 var serviceTypeName=Ext.getCmp('serviceTypeName').getValue();
		 var is_pub=Ext.getCmp('is_pub').getValue();
         var new_params = {title:title,serviceTypeName : serviceTypeName,is_pub:is_pub};    
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
           
	    	   <cms:havePerm url='/slider/addRecommendService'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform(null);
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/slider/deleteRecommendService'>
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
        	items:[{ xtype: 'textfield',id:'title',name: 'title',fieldLabel: '标题',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'textfield',id:'serviceTypeName',name: 'serviceTypeName',fieldLabel: '关联服务',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'combobox',id:'is_pub',name: 'is_pub',displayField: 'status_name',valueField : 'status_type',
                  		fieldLabel: '状态',store: Ext.create('Ext.data.Store', {  
                           fields: [  
      	                          { name: 'status_type'},{ name: 'status_name' }  
      	                          ],  
      	                          data: [  
      	                          { "status_type": "1","status_name": "启用 " },  
      	                          { "status_type": "0","status_name": "暂停" }
      	                          ]  
      	                    }),queryMode: 'local',labelAlign:'left',labelWidth:60},
        	       <cms:havePerm url='/slider/getRecommendService'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			     var title=Ext.getCmp('title').getValue();
    	    				 var serviceTypeName=Ext.getCmp('serviceTypeName').getValue();
    	    				 var is_pub=Ext.getCmp('is_pub').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,title:title,serviceTypeName:serviceTypeName,is_pub:is_pub}}); 
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
    
    <cms:havePerm url='/slider/editRecommendService'>
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
                     url:"/slider/deleteRecommendService",  
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
	var showform = function(d) {
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
			items : [
              {
	            xtype : 'textfield',
	            name : 'id',
	            hidden	:true
            },{
				//表名
				xtype : 'textfield',
				name : 'title',
				allowBlank	:false,
				fieldLabel : '标题'
			},
			{
				xtype: 'hidden',
				name: "service_id"
			},{
                xtype: "triggerfield",
                name: "serviceTypeName",
                fieldLabel: "推荐服务",
                editable:false,
                hideTrigger: false,
                allowBlank	:false,
                onTriggerClick: function () {
                   addService(add_winForm);
                }
            },{
				 // 输入排序
				 xtype : 'numberfield',
				 name : 'rank',
				 allowDecimals: false,
				 minValue: 0,
				 maxValue: 10000,
				 fieldLabel : '排序'
			}]

		});
		var rows = grid.getSelectionModel().getSelection();
		 var title = '新增推荐服务';
		    var reqName = 'addRecommendService';
		    //================判断是编辑还是新增===============
		    	if(d!=null&&d.id!=null&&d.id!=0){
		    		// 是编辑状态
		    	    add_winForm.form.findField('id').setValue(rows[0].get("id"));
		    	    add_winForm.form.findField('title').setValue(rows[0].get("title"));  
		    	    add_winForm.form.findField('service_id').setValue(rows[0].raw.service_id);
		    	    add_winForm.form.findField('serviceTypeName').setValue(rows[0].get("serviceTypeName"));
		    	    add_winForm.form.findField('rank').setValue(rows[0].raw.rank);
		    	    
		        	title = '编辑推荐服务';
		        	reqName = 'editRecommendService';
		    	}
		
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
							url : '/slider/'+reqName,
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',
							success : function(fp, o) {
								if (o.result== true) { 
				              	    Ext.example.msg("提示","保存成功！");                                                                                     
									syswin.close(); //关闭窗口  
									store.reload();
								}else{
									Ext.example.msg("提示","该服务已存在！"); 
								}
							},
							failure : function(fp, o) {
								 Ext.example.msg("提示","该服务已存在！");
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
 	    
	 	     var addService = function(add_winForm){  
	 		 var url='/systemManager/getServiceType';
			 
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
	                    	 
	                    	 add_winForm.form.findField('service_id').setValue(rowsCata[0].get("id"));
	                    	 add_winForm.form.findField('serviceTypeName').setValue(rowsCata[0].get("service_type_name")); 
	                    	
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

var StartOrStopAct=function(id,status){

	var sucessmessage;
	var errormessage;
	var message;
	var edit_del;
	if(status==1){
		edit_del=0;
		message='请确定要执行暂停操作吗?'
	}else if(status==0){
		edit_del=1;
		message='请确定要执行启用操作吗?'
	}
	Ext.Msg.confirm("提示信息", message, function(btn) {
		if (btn == 'yes') {
			Ext.Ajax.request({
				url : "/slider/startOrstopRecommendSer",
				params : {
					id : id,
					status : edit_del
				},
				method : 'post',
				success : function(o) {
				    Ext.example.msg("提示",o.responseText); 
					store.reload();
					return;
				},
				failure: function(o) {
                  	 Ext.example.msg("提示",o.responseText);
                  } 
			});
		}
	});
};

</script>
</head>
<body>
</body>
</html>