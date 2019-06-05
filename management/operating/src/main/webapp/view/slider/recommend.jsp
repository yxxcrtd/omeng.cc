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
<title>热门推荐管理</title>
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
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'编号',dataIndex:'id',hidden:true,align:'center'},  
           		   {header:'省份',dataIndex:'province',align:'center'}, 
                   {header:'城市',dataIndex:'city',align:'center'},
           		   {header:'开始时间',dataIndex:'join_time',align:'center'},
                   {header:'过期时间',dataIndex:'overdue_time',align:'center'},
                   {header:'推荐位',dataIndex:'recs',align:'center',renderer:function(value,v,r){  
                     return '<a href="javascript:showRecs(\''+r.data.id+'\')"><span style="color:red;font-weight:bold";>'+value+'</span></a>';
           		}},
           	    {header:'状态',dataIndex:'status',align:'center',renderer:function(value,v,r){  
                 if(value=='1'){  
                 return '<a href="javascript:StartOrStopRecs(\''+r.data.id+'\',\''+r.data.province+'\',\''+r.data.city+'\','+value+')"><span style="color:green;font-weight:bold;">启用</span></a>';
                 } else if(value=='2'){  
                 return '<a href="javascript:StartOrStopRecs(\''+r.data.id+'\',\''+r.data.province+'\',\''+r.data.city+'\','+value+')"><span style="color:red;font-weight:bold;">暂停</span></a>';
                 }
     		}}
               ];
	//创建store数据源
    
    //列表展示数据源
    store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/slider/getRecommends',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
        
           {name:'join_time'},
           {name:'overdue_time'},
           {name:'status'},
           {name:'province'},
           {name:'city'},
           {name:'recs'}
        ]  
    });
	
 // 省信息
	var provinceStore = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/showChinaArea',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'name'
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
			url : '/common/showChinaArea',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'name'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'area'
		}]
	});
	
 
    // 时间状态
    var timeStatusComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['全部','0'],
    	      ['使用中','1'],
    	      ['未开始','2'],
    	      ['已过期','3']
    	]
    });
    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
		var time_status=Ext.getCmp('time_status').getValue();
		var province=Ext.getCmp('province').getValue();
		var city=Ext.getCmp('city').getValue();
		var status=Ext.getCmp('status').getValue();  
        var new_params = { time_status:time_status,province:province,city:city,status:status};    
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
        	       <cms:havePerm url='/slider/editRecommend'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },</cms:havePerm>
        	       <cms:havePerm url='/slider/deleteRecommend'>
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
        	items:[ {xtype:'combo',fieldLabel : '时间状态',id: 'time_status',store : timeStatusComboStore,name:'time_status',
	   			          triggerAction: 'all',displayField: 'name',valueField: 'value',value:'',labelWidth:60,mode:'local'
			        },'-',
			        {xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省份',valueField : 'name',displayField : 'area',
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
						queryMode : 'local',labelAlign : 'left',labelWidth : 55},'-',
			             {xtype : 'combobox',id : 'city',name : 'city',fieldLabel : '城市',valueField : 'name',displayField : 'area',
						store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',	
						 
        	       { xtype: 'combobox',id:'status',name: 'status',displayField: 'status_name',valueField : 'status_type',
               		fieldLabel: '启用状态',store: Ext.create('Ext.data.Store', {  
                        fields: [  
   	                          { name: 'status_type'},{ name: 'status_name' }  
   	                          ],  
   	                          data: [  
   	                          { "status_type": "1","status_name": "启用 " },  
   	                          { "status_type": "2","status_name": "禁用" }
   	                          ]  
   	                    }),queryMode: 'local',labelAlign:'left',labelWidth:60},
   	                 <cms:havePerm url='/slider/getRecommends'>
        	       { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			  
    	    				var time_status=Ext.getCmp('time_status').getValue();
    	    				var province=Ext.getCmp('province').getValue();
    	    				var city=Ext.getCmp('city').getValue();
    	    				var status=Ext.getCmp('status').getValue();  
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,time_status:time_status,province:province,city:city,status:status}}); 
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
    provinceStore.load();
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    <cms:havePerm url='/slider/editRecommend'>
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
                         url:"/slider/deleteRecommend",  
                         params:{id:id},  
                         method:'post',  
                         success:function(o){  
                        	 Ext.example.msg("提示","推荐位删除成功！");
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                        	 Ext.example.msg("提示","推荐位删除失败！");
                         }  
                     });    
                 }  
             });  
          }  
     } ;
    
   //form表单
   	var typeStore = new Ext.data.SimpleStore({
	    	fields:['name','value'],
	    	data:[['行业推荐','1'],
	    	      ['个性推荐','2']
	    	      
	    	]
	    });
   	var statusStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['启用','1'],
    	      ['禁用','2']
    	   
    	]
    });
   
 	var showform=function(d){
 		
 		var rows = grid.getSelectionModel().getSelection();
 	    var add_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 470, 
 	                height:250,
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
	    			},{ xtype: 'datetimefield',
                         name: 'join_time',
                         format : 'Y-m-d',
                         fieldLabel: '开始时间'
                    },
                    { xtype: 'datetimefield',
                        name: 'overdue_time',
                        format : 'Y-m-d',
                        allowBlank	:false,
                        fieldLabel: '过期时间'
                   },{
    				xtype : 'combobox',
    				name : 'province',
    				fieldLabel : '省份',
    				allowBlank	:false,
    				valueField : 'name',
    				displayField : 'area',
    				store : provinceStore,
    				listeners : { // 监听该下拉列表的选择事件
    						select : function(combobox,record,index) {
    							add_winForm.form.findField('city').setValue('');
    								cityStore.load({
    										params : {
    												parentId : combobox.value
    												}
    								});
    												}
    							 },
    								queryMode : 'local'
    					},
    								
    				   {xtype : 'combobox', 
    					   name : 'city',
    					   fieldLabel : '城市',
    					   valueField : 'name',
    					    displayField : 'area',
    					    store : cityStore,
    					    queryMode : 'local'	   
    				}]  
 	            });  
 	    
	    var title = '新增推荐方案';
	    var reqName = 'editRecommend';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    		var status=rows[0].get('status')
	    		if(status==1){
	    			 Ext.example.msg("提示","启用状态不允许修改");
	    			 return;
	    		}
	    	    add_winForm.form.findField('id').setValue(rows[0].get("id"));
	    	    add_winForm.form.findField('province').setValue(rows[0].get("province"));
	    	    cityStore.load({
					params : {
						parentId : rows[0].raw.province
							 }
							});
	    	    add_winForm.form.findField('city').setValue(rows[0].get("city"));
	    	    add_winForm.form.findField('join_time').setValue(d.join_time); 
	    	    add_winForm.form.findField('overdue_time').setValue(d.overdue_time); 
	        	title = '编辑推荐方案';
	        	reqName = 'editRecommend';
	    	}
	

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 500, 
 	              height: 300,
 	              //height : 120,  
 	              //plain : true,  
 	              iconCls : "addicon",  
 	              // 不可以随意改变大小  
 	              resizable : true,  
 	              autoScroll: true,  
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
 	                            }else{
 	                            	Ext.example.msg("提示","请填写完整信息！");
 	                            }
 	                        	} 
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
 	    };
 	 
});

var StartOrStopRecs=function(id,province,city,status){

	var sucessmessage;
	var errormessage;
	var message;
	var edit_del;
	if(status==1){
		edit_del=2;
		message='请确定要执行暂停操作吗?'
	}else if(status==2){
		edit_del=1;
		message='请确定要执行启用操作吗?'
	}
	Ext.Msg.confirm("提示信息", message, function(btn) {
		if (btn == 'yes') {
			Ext.Ajax.request({
				url : "/slider/startOrstopRecs",
				params : {
					id : id,
					province : province,
					city : city,
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

//************************************查看推荐方案的具体推荐位************************************ 

	var showRecs=function(rec_id){ 
		var columns = [
		    {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
            {header:'编号',dataIndex:'id',hidden:true,align:'center'},
			{header:'标题',dataIndex:'title',align:'center'},
            {header:'推荐类型',dataIndex:'type',align:'center',renderer:function(value){  
            if(value=='1'){  
                   return "<span style='color:green;font-weight:bold';>行业推荐</span>";  
            } else if(value=='2'){  
                   return "<span style='color:red;font-weight:bold';>个性推荐</span>";  
            }
   		   }},
   		{header:'推荐语',dataIndex:'detail',align:'center'},
   		{header:'排序',dataIndex:'rank',align:'center'}
		];
	//列表展示数据源
		var storeRecService = Ext.create("Ext.data.Store", {
			pageSize : 20, //每页显示几条数据  
			proxy : {
				type : 'ajax',
				url : '/slider/getServiceByRecId',
				reader : {
					type : 'json',
					totalProperty : 'total',
					root : 'data',
					idProperty : '#'
				}
			},
	        fields:[ 
	           {name:'id'}, 
	           {name:'title'},  
               {name:'detail'},
               {name:'type'},
               {name:'rank'}
	        ]  
	    });
	

			var typeStore = new Ext.data.SimpleStore({
		    	fields:['name','value'],
		    	data:[['行业推荐','1'],
		    	      ['个性推荐','2']
		    	      
		    	]
		    });
			
		storeRecService.on('beforeload', function (storeRecService, options) {    
		var title=gridRec.dockedItems.items[2].items.items[0].rawValue;
        var new_params = { title:title,rec_id:rec_id};   
        Ext.apply(storeRecService.proxy.extraParams, new_params);    
    }); 
		 var sm = Ext.create('Ext.selection.CheckboxModel');
	    var gridRec = Ext.create("Ext.grid.Panel",{
	    	region: 'center',
	    	border: false,
	    	store: storeRecService,
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
	        	items:[<cms:havePerm url='/slider/editServiceRec'>
	              { xtype: 'button', text: '增加',iconCls:'NewAdd',
	    	           listeners: {
	    		         click:function(){
	    			      showRecform(null);
	    		   }
	    	   }
	       }</cms:havePerm>
	       ]
	        },{
	        	xtype:'toolbar',
	        	dock:'top',
	        	displayInfo: true,
	        	items:[{ xtype: 'textfield',name: 'title',fieldLabel: '标题',labelAlign:'left',labelWidth:60},'-',
	           
                <cms:havePerm url='/slider/getServiceByRecId'>
	           { xtype: 'button', text: '查询',iconCls:'Select',listeners: {
    		    click:function(){
    			   var title=gridRec.dockedItems.items[2].items.items[0].rawValue;
    			   storeRecService.currentPage = 1;
    			   storeRecService.load({params:{start:0,page:1,limit:20,title:title,rec_id:rec_id}}); 
    		}
    		}}</cms:havePerm>
              ]
	        	
	        },{
	            xtype: 'pagingtoolbar',
	            store: storeRecService,   // GridPanel使用相同的数据源
	            dock: 'bottom',
	            displayInfo: true,
	            plugins: Ext.create('Ext.ux.ProgressBarPager'),
	            emptyMsg: "没有记录" //没有数据时显示信息
	        }]
	    });
	  //加载数据  

	  storeRecService.load({params:{start:0,limit:20}}); 
	  
	  <cms:havePerm url='/slider/editServiceRec'>
	  gridRec.on("itemdblclick",function(grid, row){
		  showRecform(row.data);
	    });
	    </cms:havePerm>
	  
	    var recwin = Ext.create('Ext.window.Window',{  
	              title : "查看推荐位",  
	              width : 1000, 
	             height : 450,  
	              //plain : true,  
	              iconCls : "addicon",  
	              // 不可以随意改变大小  
	              resizable : false, 
	              autoScroll:true, 
	              // 是否可以拖动  
	              // draggable:false,  
	              collapsible : true, // 允许缩放条  
	              closeAction : 'close',  
	              closable : true,  
	              // 弹出模态窗体  
	              modal : 'true',  
	              buttonAlign : "center",  
	              bodyStyle : "padding:0 0 0 0",  
	              items : [gridRec]
	           });  
	  recwin.show();  
	  
	  
	  
	  
	  //新增推荐位
	  var showRecform=function(d){
	 		var pic = '';
	 		var rows = gridRec.getSelectionModel().getSelection();
	 		if(d!=null&&d.id!=null&&d.id!=0){
	 			pic =rows[0].raw.pics_path;
	 		}
	 	    var add_RecForm =  Ext.create('Ext.form.Panel', {  
	 	                frame: true,   //frame属性  
	 	                //title: 'Form Fields',  
	 	                width: 500, 
	 	                height:500,
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
		    				name: "catalog_id"
		    			},{
		    				xtype: 'hidden',
		    				name: "service_id"
		    			},   {
		    				xtype: 'hidden',
		    				name: "rec_id"
		    			},{  
	 	                    //输入推荐位标题  
	 	                    xtype: 'textfield', 
	 	                    name: 'title',  
	 	                    fieldLabel: '标题',
	 	                     maxLength: 4 ,
	 	                    allowBlank	:false
	 	                },{  
	 	                    //输入推荐语
	 	                    xtype: 'textfield', 
	 	                    name: 'detail', 
	 	                     maxLength: 7 ,
	 	                    fieldLabel: '推荐语'
	 	                },{
	 	                	xtype:'combo',
						    store : typeStore,
			    		    width:80,
			    		    name:'type',
			   			    triggerAction: 'all',
			   			    displayField: 'name',
			   			    valueField: 'value',
			   			    hiddenName:'value',
			   			    value:'1',
						    mode:'local',
						    allowBlank	:false,
						    fieldLabel: '类型' 
					   },{
	                        xtype: "triggerfield",
	                        name: "catOrSerId",
	                        fieldLabel: "推荐服务",
	                        editable:false,
	                        hideTrigger: false,
	                        allowBlank	:false,
	                        onTriggerClick: function () {
	                            var type = add_RecForm.form.findField("type").getValue();
	                            if(type==1){
	                            	addCatalog(add_RecForm);
	                            }else if(type==2){
	                            	addService(add_RecForm);
	                            }else{
	                            	 Ext.example.msg("提示","请选择推荐类型");
	                            }
	                            
	                        }
	                    },{
	                 	  //下拉列表框  
	                       xtype: 'combobox', //9  
	                       fieldLabel: '是否热门',
	                       displayField: 'value',
	                       valueField : 'key',
	                       name:'is_hot',
	                       store: Ext.create('Ext.data.Store', {  
	                           fields: [  
	                             {name:'key'},{ name: 'value' }  
	                             ],  
	                                             data: [  
	                             { 'key':'0','value': '热门' },  
	                             { 'key':'1','value': '不热门' } 
	                             ]  
	                       }),
	                    width:220
	       			}, {  
	 	   	                	xtype: 'textfield',   
	  		                    name: 'color',  
	  		                    fieldLabel: '颜色'
	  		                    //allowBlank	:false
	  	                },{
	       					// 输入排序
	       					xtype : 'numberfield',
	       					name : 'rank',
	       					allowDecimals: false,
	       					minValue: 0,
	       					maxValue: 100,
	       					fieldLabel : '排序'
	       				},
	    				{
	                	      layout:'column',
				       		  border:false,
				              items:[{  
			                  layout:'form',
			                  anchor: '70%',
			         		  border:false,
			                  columnWidth:.48,
			                  items:[{ 
			                	    
				                    fieldLabel:'上传图片',
				                    name:'upload',
				                    xtype:'fileuploadfield',  
				                    anchor:'80%',  
				                    emptyText:'请选择文件', 
				                    regex:/(png)|(jpg)$/i,
				                    buttonText: '选择',
				                    selectOnFocus:true  
				                }]
				              },{  
				                  layout:'form',
				                  anchor: '30%',
				         		  border:false,
				                  columnWidth:.48,
				                  items:[{xtype: 'displayfield',   
				 	                    name: 'displayfield',  
				 	                    value: '个性推荐140*88px,行业推荐144*168px'
				 	                    }]
					              }]
	                    },{
		    				xtype: 'hidden',
		    				name: "pics_path"
		    			},{  
	                	    xtype: 'box', //或者xtype: 'component',  
	                	    width: 300, //图片宽度  
	                	    height: 200, //图片高度  
	                	    autoEl: {  
	                	        tag: 'img',  //指定为img标签  
	                	        src: pic    //指定url路径  
	                	    }  
	                	} 
	 	               ]  
	 	            });  
	 	    
		    var title = '新增推荐位';
		    var reqName = 'editServiceRec';
		    //================判断是编辑还是新增===============
		    	if(d!=null&&d.id!=null&&d.id!=0){
		    		
		    		// 是编辑状态
		    	    add_RecForm.form.findField('id').setValue(rows[0].get("id"));
		    	    add_RecForm.form.findField('title').setValue(rows[0].get("title"));  
		    	    add_RecForm.form.findField('detail').setValue(rows[0].raw.detail);
		    	    add_RecForm.form.findField('rank').setValue(rows[0].get("rank"));
		    	    add_RecForm.form.findField('type').setValue(rows[0].get("type")+''); 
		    	    add_RecForm.form.findField('is_hot').setValue(rows[0].raw.is_hot+'');
		    	    add_RecForm.form.findField('color').setValue(rows[0].raw.color); 
		    	    add_RecForm.form.findField('pics_path').setValue(rows[0].raw.pics_path);
		    	    add_RecForm.form.findField('catalog_id').setValue(rows[0].raw.catalog_id);
		    	    add_RecForm.form.findField('service_id').setValue(rows[0].raw.service_id);
		    	    if(rows[0].get("type")==1){
		    	    	add_RecForm.form.findField('catOrSerId').setValue(rows[0].raw.cat_name); 
		    	    }else{
		    	    	add_RecForm.form.findField('catOrSerId').setValue(rows[0].raw.ser_name); 
		    	    }
		    	    
		        	title = '编辑推荐位';
		        	reqName = 'editServiceRec';
		    	}
		    	 add_RecForm.form.findField('rec_id').setValue(rec_id);

	        //================判断是编辑还是新增===============
	 	    
	 	    //创建window面板，表单面板是依托window面板显示的  
	 	    var sysRecwin = Ext.create('Ext.window.Window',{  
	 	              title : title,  
	 	              width : 510, 
	 	              height: 400,
	 	              //height : 120,  
	 	              //plain : true,  
	 	              iconCls : "addicon",  
	 	              // 不可以随意改变大小  
	 	              resizable : true,  
	 	              autoScroll: true,  
	 	              // 是否可以拖动  
	 	              // draggable:false,  
	 	              collapsible : true, // 允许缩放条  
	 	              closeAction : 'close',  
	 	              closable : true,  
	 	              // 弹出模态窗体  
	 	              modal : 'true',  
	 	              buttonAlign : "center",  
	 	              bodyStyle : "padding:0 0 0 0",  
	 	              items : [add_RecForm],  
	 	              buttons : [{  
	 	                         text : "保存",  
	 	                         minWidth : 70,  
	 	                         handler : function() {  
	 	                        	
	 	                        	if (add_RecForm.getForm().isValid()) {
	 	                        		add_RecForm.getForm().submit({  
	 	                                          url :'/slider/'+reqName,  
	 	                                           //等待时显示 等待  
	 	                                          waitTitle: '请稍等...',  
	 	                                          waitMsg: '正在提交信息...',  
	 	                                            
	 	                                          success: function(fp, o) {  
	 	                                              if (o.result== true) { 
	 	                                               	 Ext.example.msg("提示","保存成功！");
	 	                                                 sysRecwin.close(); //关闭窗口  
	 	                                                 storeRecService.reload();  
	 	                                              }else { 
	 	                                               	 Ext.example.msg("提示","保存失败！");
	 	                                              }  
	 	                                          },  
	 	                                          failure: function(fp, o) {
	 	                                        	 sysRecwin.close();
	 	                                          	 Ext.example.msg("提示",o.response.responseText);
	 	                                          }  
	 	                                       });  
	 	                            }else{
	 	                            	 Ext.example.msg("提示","请填写完整信息！");
	 	                            } 
	 	                        	} 
	 	                         }  
	 	                    , {  
	 	                         text : "关闭",  
	 	                         minWidth : 70,  
	 	                         handler : function() {  
	 	                            sysRecwin.close();  
	 	                         }  
	 	                     }]  
	 	           });  
	 	    sysRecwin.show();  
	 	    
	 	    
	 	    
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
					url : '/slider/getServiceType',
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
	                    	 add_RecForm.form.findField('service_id').setValue(rowsCata[0].get("id"));
	                    	 add_RecForm.form.findField('catOrSerId').setValue(rowsCata[0].get("service_type_name"));
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
	 	    
		//***************************************添加行业  start*************************************
		 	    
			 	   var addCatalog = function(add_RecForm){  
						var columns = [ {header:'序号',xtype: 'rownumberer',width:50},
						                {header:'ID',dataIndex:'id',sortable:true,fixed:false},
						                {header:'分类名称',dataIndex:'name',sortable:true,fixed:false}
						];
					
					//创建store数据源
					var storeCatalog = Ext.create("Ext.data.Store", {
						pageSize : 20, //每页显示几条数据  
						proxy : {
							type : 'ajax',
							url : '/slider/getAllCatalog',
							reader : {
								type : 'json',
								totalProperty : 'total',
								root : 'data',
								idProperty : '#'
							}
						},
						fields : [  {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
						            
						            {name:'name'} ]
					});
					
					//点击下一页时传递搜索框值到后台  
				    storeCatalog.on('beforeload', function (store, options) {    
				    	 var date= gridCatalog.dockedItems.items[1].items.items;
				    	 var name=date[0].rawValue;
				         var new_params = {name : name};    
				        Ext.apply(store.proxy.extraParams, new_params);    
				    });
				    var sm = Ext.create('Ext.selection.CheckboxModel');
				    var gridCatalog = Ext.create("Ext.grid.Panel", {
						region : 'center',
						border : false,
						store : storeCatalog,
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
							items : [  { xtype: 'textfield',name: 'name',fieldLabel: '分类名称',labelAlign:'left',labelWidth:70},'-',
							    {
								xtype : 'button',
								text : '查询',
								iconCls : 'Select',
								listeners : {
									click : function() {
										var date= gridCatalog.dockedItems.items[1].items.items;
								    	var name=date[0].rawValue;
										
								    	 storeCatalog.currentPage = 1;
								    	 storeCatalog.load({
				   							params : {
				   								start:0,
				   								page : 1,
				   								limit : 20,
				   								name : name
				   							}
				   						});
									}
								}
							} 
							]
						}, {
							xtype : 'pagingtoolbar',
							store : storeCatalog, // GridPanel使用相同的数据源
							dock : 'bottom',
							displayInfo : true,
							plugins : Ext.create('Ext.ux.ProgressBarPager'),
							emptyMsg : "没有记录" //没有数据时显示信息
						} ]
					});

					//加载数据  
					storeCatalog.load({
						params : {
							start : 0,
							limit : 20
						}
					});
					
					var catalogwindow = Ext.create('Ext.window.Window',{  
			            title : "添加服务",  
			            width: 500,
			            height:500,  
			            //height : 120,  
			            //plain : true,  
			            iconCls : "addicon",  
			            // 不可以随意改变大小  
			            resizable : true,  
			            // 是否可以拖动  
			            //draggable:true, 
			            autoScroll: true,
			            collapsible : true, // 允许缩放条  
			            closeAction : 'close',  
			            closable : true,  
			            // 弹出模态窗体  
			            modal : 'true',  
			            buttonAlign : "center",  
			            bodyStyle : "padding:0 0 0 0",  
			            items : [gridCatalog],  
			            buttons : [{  
			                       text : "确定",  
			                       minWidth : 70,  
			                       handler : function() {
			                    	 var rowsCata = gridCatalog.getSelectionModel().getSelection();
			                    	 if(rowsCata.length <= 0){
			                   		     Ext.example.msg("提示","请选择要添加的分类！"); 
			                             return ;  
			                    	 }else if(rowsCata.length>1){
			                    		 Ext.example.msg("提示","请选择一个分类添加！"); 
			                             return ; 
			                    	 }
			                    	 add_RecForm.form.findField('catalog_id').setValue(rowsCata[0].get("id"));
			                    	 add_RecForm.form.findField('catOrSerId').setValue(rowsCata[0].get("name"));
			                    	 catalogwindow.close(); 
			                       }   
			                  } ,{
							text : "取消",
							minWidth : 70,
							handler : function() {
								catalogwindow.close();
							}
						}]                  		
			         });   
					catalogwindow.show();  
					    };
				    
			 	    
			 	//**********************************添加行业end**********************************************************    
			 	        
	 	    
	 	    
	 	    
	 	    
	 	    
	 	    };
	  
	  
};
</script>

</head>
<body>
</body>
</html>