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
<title>订单奖励活动开放城市</title>
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
                   {header:'省份',dataIndex:'province',sortable:true,fixed:false},
                   {header:'城市',dataIndex:'city',sortable:true,fixed:false}
                  
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/activity/getOrderRewardOpenCity',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'province'},
           {name:'city'} 
           
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
    		url : '/common/showServiveCity',
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
    	 var province=Ext.getCmp('province').getRawValue();
    	 var city=Ext.getCmp('city').getRawValue();
    	 var activity_id=Ext.getCmp('activity_id').getValue();
         var new_params = {activity_id:activity_id,province:province,city:city};    
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
	    	   <cms:havePerm url='/activity/addOrderRewardOpenCity'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   addOrderRewardOpenCity('${activity_id}');
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/activity/deleteOrderRewardOpenCity'>
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
						
        	       <cms:havePerm url='/slider/getOrderRewardOpenCity'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			     var province=Ext.getCmp('province').getRawValue();
    	    		    	 var city=Ext.getCmp('city').getRawValue();
    	    		    	 var activity_id=Ext.getCmp('activity_id').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,activity_id:activity_id,province:province,city:city}}); 
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
    provinceStore.load(); 
   
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
                     url:"/activity/deleteOrderRewardOpenCity",  
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
		var addOrderRewardOpenCity = function(activity_id){  
			var columns = [ {header:'序号',xtype: 'rownumberer',width:50},
			                {header:'ID',dataIndex:'id',sortable:true,fixed:false},
			                {header:'省份',dataIndex:'province',sortable:true,fixed:false},
			                {header:'城市',dataIndex:'city',sortable:true,fixed:false}
			                
			];
		

		//列表展示数据源
		var storeCatalogService = Ext.create("Ext.data.Store", {
			pageSize : 20, //每页显示几条数据  
			proxy : {
				type : 'ajax',
				url : '/common/showAllProAndCity',
				reader : {
					type : 'json',
					totalProperty : 'total',
					root : 'data',
					idProperty : '#'
				}
			},
			fields : [  {name:'id'},
			            {name:'province'},
			            {name:'city'}  ]
		});
		
		
		//点击下一页时传递搜索框值到后台  
	    storeCatalogService.on('beforeload', function (store, options) {    
	    	var date= gridCatalogService.dockedItems.items[1].items.items;
			var province =date[0].rawValue;
			var city = date[2].rawValue;
	        var new_params = {province:province,city:city};    
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
				items : [  {xtype : 'combobox',name : 'provinceN',fieldLabel : '省份',valueField : 'id',displayField : 'area',
					store : provinceStore,
					listeners : { // 监听该下拉列表的选择事件
														select : function(
																combobox,
																record,
																index) {
															gridCatalogService.dockedItems.items[1].items.items[2]
																	.setValue('');
															cityStore.load({params : {
																			parentId : combobox.value
																		}
																	});
														}
													},
					queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
		        {xtype : 'combobox',name : 'cityN',fieldLabel : '城市',valueField : 'id',displayField : 'area',
					store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',	
					
				    {
					xtype : 'button',
					text : '查询',
					iconCls : 'Select',
					listeners : {
						click : function() {
							var date= gridCatalogService.dockedItems.items[1].items.items;
	    	    			var province =date[0].rawValue;
	   						var city = date[2].rawValue;
					    	 storeCatalogService.currentPage = 1;
					    	 storeCatalogService.load({
	   						 params : {start:0,page : 1,limit : 20,province:province,city:city}
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
            title : "添加城市",  
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
                   		     Ext.example.msg("提示","请选择要添加的城市！"); 
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
                      		 url :'/activity/addOrderRewardOpenCity',  
                      		 params:{activity_id:activity_id,id:ids},  
                               method:'post',  
                               success:function(fp, o){  
                            	   if (fp.responseText== 'true') { 
                                   Ext.example.msg("提示","添加成功！"); 
                                   issuewindow.close();
                                   store.reload();  
                                   return ;  
                            	   }else{
                            		   Ext.example.msg("提示","请不要重复添加开放城市！"); 
                            		   issuewindow.close();
                            	   }
                               },  
                               failure:function(form,action){  
                            	   Ext.example.msg("提示","请不要重复添加开放城市！"); 
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