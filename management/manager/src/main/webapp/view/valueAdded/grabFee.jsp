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
<title>抢单费用</title>
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
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,hidden:true,align:'center'},
                   {header:'省份',dataIndex:'province',sortable:true,fixed:false,align:'center'},
                   {header:'城市',dataIndex:'city',sortable:true,fixed:false,align:'center'},
                   {header:'app类型',dataIndex:'app_name',sortable:true,fixed:false,align:'center'},
                   {header:'抢单费用',dataIndex:'grab_fee',sortable:true,fixed:false,align:'center'},
                   {header:'执行状态',dataIndex:'status',align:'center',renderer:function(value){  
                       if(value=='0'){ 
                           return "<span style='color:red;font-weight:bold';>未执行</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>已执行</span>";  
                       }
           		}},
           	 <cms:havePerm url='/valueAdded/updateGrabFee'>
           		{header:'操作',dataIndex:'status',align:'center',renderer:function(value,v,r){  
                  var id=r.data.id;
                  var province=r.data.province;
                  var city=r.data.city;
                  var app_type=r.raw.app_type;
                  var grab_fee=r.raw.grab_fee;
                   if(value==0){
                 return '<a href="javascript:updateGrabFee(\''+id+'\',\''+province +'\',\''+ city+'\',\''+app_type +'\',\''+grab_fee +'\')"><span style="color:red;font-weight:bold";>执行</span></a>';
                  }else{
                  return '';
                  } 
        		}}</cms:havePerm>
               ];
    
    store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true, //设置属性进行请求后台排序
        proxy:{  
            type:'ajax',  
            url:'/valueAdded/getGrabFeeList',  
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
           {name:'city'},  
           {name:'app_name'},
           {name:'grab_fee'},
           {name:'status'}
        ]  
    });
    
     // app类型
				var store_appType = Ext.create("Ext.data.Store", {
					pageSize : 20, // 每页显示几条数据
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
		 var app_type=Ext.getCmp('app_type').getValue();
         var new_params = {province:province,city:city,app_type:app_type};    
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
	    	   <cms:havePerm url='/valueAdded/addGrabFee'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform();
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/valueAdded/deleteGrabFee'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteGrabFee();
		    		   }
		    	   }} </cms:havePerm>
			      
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[	{xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省份',valueField : 'id',displayField : 'area',width : 250,
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
				   {xtype : 'combobox',id : 'city',name : 'city',fieldLabel : '城市',valueField : 'id',displayField : 'area',width : 250,
							store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',		
				   {xtype : 'combobox',id : 'app_type',name : 'appType',fieldLabel : 'app类型',value:'',valueField : 'app_type',displayField : 'app_name',width : 250,
							store : store_appType,editable:false,queryMode : 'local',labelAlign : 'left',value:'',labelWidth : 60},'-',

        	       <cms:havePerm url='/valueAdded/getGrabFeeList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			  var province=Ext.getCmp('province').getRawValue();
		                  var city=Ext.getCmp('city').getRawValue();
		                  var app_type=Ext.getCmp('app_type').getValue();
    	     			  store.currentPage = 1;
    	    			  store.load({params:{start:0,page:1,limit:20,province:province,city:city,app_type:app_type}}); 
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
    provinceStore.load();
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    <cms:havePerm url='/valueAdded/editGrabFee'>
    grid.on("itemdblclick",function(grid, row){
    	editform();
    });</cms:havePerm>
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	
	
	//刷新系统
	function flushParam(){
        Ext.Ajax.request({  
            url:"/common/flushSystemParam",  
            method:'post',  
            success:function(o){ 
            	Ext.example.msg("提示","刷新成功！");
                store.reload();  
                return ;  
            },  
            failure:function(form,action){ 
             	Ext.example.msg("提示","刷新失败！");
            }  
        });
	}  
	

	//删除字典值
	function deleteGrabFee()  
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
                     url:"/valueAdded/deleteGrabFee",  
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
			height : 360,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [{  
		                    //显示文本框，相当于label  
		                    xtype: 'displayfield',   
		                    name: 'displayfield1',  
//		                    fieldLabel: 'Display field',  
		                    value: '请填写抢单费用信息'  
		                     
		                }, {
							xtype : 'combobox',
							name : 'province',
							fieldLabel : '省份',
							valueField : 'area',
							hiddenName:'id',
							displayField : 'area',
							store : provinceStore,
							listeners : { // 监听该下拉列表的选择事件
							select : function(combobox,record,index) {
							//Ext.getCmp('city').setValue('');
							
																add_winForm.form.findField('city').setValue('');
																cityStore.load({
																			params : {
																				parentId : record[0].raw.id
																			}
																		});
															}
														},
														queryMode : 'local',
														labelAlign : 'left'
													},
				{
														xtype : 'combobox',
														name : 'city',
														fieldLabel : '城市',
														valueField : 'area',
		        			    			   			hiddenName:'id',
														displayField : 'area',
														store : cityStore,
														queryMode : 'local',
														labelAlign : 'left'
													},{
						xtype : 'combobox',
						name : 'app_type',
						fieldLabel : 'app类型',
						valueField : 'app_type',
						displayField : 'app_name',
						store : store_appType,
						allowBlank	:false,
						 editable:false,
						 hiddenName:'',
						 allowBlank	:false,
						queryMode : 'local'
					},
			{
				// 父级字典ID
				xtype : 'numberfield',
				name : 'grab_fee',
				minValue:0,
				fieldLabel : '抢单费用'
			}]

		});

		
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增抢单费用",
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
							url : '/valueAdded/addGrabFee',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',

							success : function(fp, o) {
								if(o.result.data==1){
								 	Ext.example.msg("提示","保存成功！");
									syswin.close(); //关闭窗口  
									store.reload();
								}else{
								 	Ext.example.msg("提示",o.result.message);
								}
									
							
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
    
	  //form表单
	var editform = function() {
		var edit_winForm = Ext.create('Ext.form.Panel', {
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
		                    //显示文本框，相当于label  
		                    xtype: 'displayfield',   
		                    name: 'displayfield1',  
//		                    fieldLabel: 'Display field',  
		                    value: '请填写抢单费用信息'  
		                     
		                },  {
		    				// 
		    				xtype : 'textfield',
		    				name : 'id',
		    				hidden:true
		    			
		    			}, {
		    				// 
		    				xtype : 'textfield',
		    				name : 'province',
		    				fieldLabel : '省份',
		    			    readOnly:true
		    				
		    			},
		    			{
		    				// 
		    				xtype : 'textfield',
		    				name : 'city',
		    				fieldLabel : '城市',
		    			    readOnly:true
		    				
		    			},  {
						xtype : 'combobox',
						name : 'app_type',
						fieldLabel : 'app类型',
						valueField : 'app_type',
						displayField : 'app_name',
						store : store_appType,
						allowBlank	:false,
						 editable:false,
						 hiddenName:'',
						 allowBlank	:false,
						 readOnly:true,
						queryMode : 'local'
					},
			{
				// 父级字典ID
				xtype : 'numberfield',
				name : 'grab_fee',
				minValue:0,
				fieldLabel : '抢单费用'
			}]

		});
		
		var rows = grid.getSelectionModel().getSelection();
		edit_winForm.form.findField('id').setValue(rows[0].get("id"));
		edit_winForm.form.findField('province').setValue(rows[0].get("province"));
		edit_winForm.form.findField('city').setValue(rows[0].get("city"));
		edit_winForm.form.findField('app_type').setValue(rows[0].raw.app_type);
		edit_winForm.form.findField('grab_fee').setValue(rows[0].get("grab_fee"));
		
		
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "编辑抢单费用",
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
			items : [ edit_winForm ],
			buttons : [ {
				text : "保存",
				minWidth : 70,
				handler : function() {
					if (edit_winForm.getForm().isValid()) {
						edit_winForm.getForm().submit({
							url : '/valueAdded/editGrabFee',
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
	
});

function storeReload(){ 
	store.reload();
 } 
//执行操作
	function updateGrabFee(id,province,city,app_type,grab_fee)  
    {   
        
         Ext.Msg.confirm("提示信息","请确定要执行操作吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/valueAdded/updateGrabFee",  
                     params:{id:id,province:province,city:city,app_type:app_type,grab_fee:grab_fee},  
                     method:'post',  
                     success:function(o){ 
                 	     Ext.example.msg("提示","执行成功！");
                 		 window.setTimeout("storeReload()", 2000); 
                         return ;  
                     },  
                     failure:function(form,action){
                    	 Ext.example.msg("提示","执行失败！");
                     }  
                 });    
             }  
         });  
    } ;
      

</script>
</head>
<body>
</body>
</html>