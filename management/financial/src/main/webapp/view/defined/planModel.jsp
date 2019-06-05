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
<title>报价方案表单详情</title>
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
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,align:'center'},
                   {header:'字段名',dataIndex:'col_name',sortable:true,fixed:false,align:'center'},
                   {header:'字段长度',dataIndex:'col_len',sortable:true,fixed:false,align:'center'},
                   {header:'描述',dataIndex:'col_desc',sortable:true,fixed:false,align:'center'},
                   {header:'控件类型',dataIndex:'control_type',sortable:true,fixed:false,align:'center'},
                   {header:'默认提示语',dataIndex:'hint_value',sortable:true,fixed:false,align:'center'},
                   {header:'默认值',dataIndex:'default_value',sortable:true,fixed:false,align:'center'},
                   {header:'表名',dataIndex:'name',sortable:true,fixed:false,align:'center'},
                   {header:'排序',dataIndex:'rank',sortable:true,fixed:false,align:'center'},
                   {header:'是否隐藏',dataIndex:'is_hide',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:red;font-weight:bold';>展示</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>隐藏</span>";  
                       }
           	       }},
              	    {header:'操作',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
              	    	var control_type=r.data.control_type;
               	    	if(control_type=='GRID'||control_type=='POP'){
               	    		return '<a href="javascript:showDetail(\''+r.data.id+'\',\''+r.data.col_name+'\')">详情</a>';
               	    	}else{
               	    		
               	    	}
              		}}
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/defined/getPlanModelList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'id'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'col_name'},  
           {name:'col_desc'},
           {name:'control_type'},
           {name:'col_len'},
           {name:'name'},
           {name:'rank'},
           {name:'is_hide'},
           {name:'hint_value'},
           {name:'default_value'}
        ]  
    });
    
 // 控件类型
	var store_ControlType = Ext.create("Ext.data.Store", {
		pageSize : 20, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/showControlType',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'type'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'name'
		} ]
	});
	// 控件展示类型
	var store_ControlShowType = Ext.create("Ext.data.Store", {
		pageSize : 20, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/showControlShowType',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'type'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'name'
		} ]
	});
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var service_type_name=Ext.getCmp('service_type_name').getValue();
    	 var object_id=Ext.getCmp('object_id').getValue();
		 var col_name=Ext.getCmp('col_name').getValue();
		 var new_params = {service_type_name:service_type_name,col_name : col_name,object_id:object_id};    
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
           
	    	   <cms:havePerm url='/defined/addPlanModel'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform();
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/defined/deletePlanModel'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		    	   }},'-',
			       </cms:havePerm>
			       <cms:havePerm url='/defined/editPlanModel'>
		           { xtype: 'button', id:'edit', text: '修改',iconCls:'Edit',
			    	   listeners: {
			    		   click:function(){
			    			   editform();
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
        	       { xtype: 'textfield',id:'object_id',name: 'object_id',fieldLabel: '表Id',value:'${object_id}',labelAlign:'left',labelWidth:60,hidden:true},'-',
        	       { xtype: 'textfield',id:'col_name',name: 'col_name',fieldLabel: '字段名',labelAlign:'left',labelWidth:60},'-',
        	      
        	       <cms:havePerm url='/defined/getPlanModelList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var service_type_name=Ext.getCmp('service_type_name').getValue();
    	    			   var object_id=Ext.getCmp('object_id').getValue();
    	    			   var col_name=Ext.getCmp('col_name').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,service_type_name:service_type_name,col_name : col_name,object_id:object_id}}); 
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
    store_ControlType.load();
    
    <cms:havePerm url='/defined/editPlanModel'>
    grid.on("itemdblclick",function(grid, row){
    	 editform(); 
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
                     url:"/defined/deletePlanModel",  
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
			layout:"form",
		    frame:true,
		    labelWidth:80,
		    labelAlign:"left",
			items : [{//行1  
                layout:"column",//从左往右的布局   
                items:[{  
                    columnWidth:.5,//该列在整行中所占的百分比    
                    layout:"form",//从上往下的布局   
                    items:[{
        				//表名
        				xtype : 'textfield',
        				name : 'col_name',
        				allowBlank	:false,
        				fieldLabel : '字段名',  
                        width:220 
        			}]  
                },{  
                    columnWidth:.5,//该列在整行中所占的百分比    
                    layout:"form",//从上往下的布局   
                    style:'margin-left:20px',
                    items:[{
        				// 描述
        				xtype : 'textfield',
        				name : 'col_desc',
        				fieldLabel : '描述',  
                        width:220
        			}]  
                }]  
    },{//行1  
        layout:"column",//从左往右的布局   
        items:[{  
            columnWidth:.5,//该列在整行中所占的百分比    
            layout:"form",//从上往下的布局   
            items:[{ xtype : 'combobox',
				 name : 'control_type',
				 fieldLabel : '控件类型',
				 valueField : 'type',
				 displayField : 'name',
				 editable:false,
				 allowBlank	:false,
				 store : store_ControlType,
				 listeners : { // 监听该下拉列表的选择事件
					select : function(
							combobox,
							record,
							index) {
						add_winForm.form.findField('show_type').setValue('');
						store_ControlShowType.load({
									params : {
										control_type : combobox.value
									}
								});
					}
				},
				hiddenName:'',
				queryMode : 'local',
				width:220
			}]  
        },{  
            columnWidth:.5,//该列在整行中所占的百分比    
            layout:"form",//从上往下的布局   
            style:'margin-left:20px',
            items:[{
				// 描述
				xtype : 'textfield',
				name : 'control_type_desc',
				fieldLabel : '控件类型描述', 
                width:220
			}]  
        }]  
       },{//行1  
           layout:"column",//从左往右的布局   
           items:[{  
               columnWidth:.5,//该列在整行中所占的百分比    
               layout:"form",//从上往下的布局   
               items:[{
   				xtype : 'combobox',
				name : 'show_type',
				fieldLabel : '展示类型',
				valueField : 'type',
				displayField : 'name',
				editable:false,
				hiddenName:'',
				store : store_ControlShowType,
				queryMode : 'local',
                width:220
			}]  
           },{  
               columnWidth:.5,
               layout:"form", 
               style:'margin-left:20px',
               items:[{
            	 //展示类型描述
     			xtype : 'textfield',
     			name : 'show_type_desc',
     			fieldLabel : '展示类型描述',
                width:220
                   
   			}]  
           }]  
          },{
              layout:"column",
              items:[{  
                  columnWidth:.5,
                  layout:"form",
                  items:[{  
                	//长度
                  	xtype : 'numberfield',
              		name : 'col_len',
              		allowDecimals: false,
              		fieldLabel : '长度',
              		minValue: 0,
              		maxValue: 1000,
              		width:220
                  }]  
              },{  
                  columnWidth:.5,
                  layout:"form",
                  style:'margin-left:20px',
                  items:[{
               	   //下拉列表框  
				xtype : 'textfield',
				name : 'hint_value',
				fieldLabel : '默认提示语',
                 width:220
      			}]  
              }]  
             },{//行1  
                 layout:"column",
                 items:[{  
                     columnWidth:.5,
                     layout:"form",   
                     items:[{  
                    	//长度
         				xtype : 'textfield',
         				name : 'default_value',
         				fieldLabel : '默认值',
                      width:220
                     }]  
                 },{  
                     columnWidth:.5,
                     layout:"form",
                     style:'margin-left:20px',
                     items:[{
               	  //下拉列表框  
                     xtype: 'combobox', //9  
                     fieldLabel: '是否隐藏',
                     displayField: 'value',
                     valueField : 'key',
                     name:'is_hide',
                     store: Ext.create('Ext.data.Store', {  
                         fields: [  
                           {name:'key'},{ name: 'value' }  
                           ],  
                                           data: [  
                           { 'key':'0','value': '展示' },  
                           { 'key':'1','value': '隐藏' } 
                           ]  
                     }),
                  width:220
     			}]  
             }]  
                },{//行1  
                    layout:"column",
                    items:[{  
                        columnWidth:.5,
                        layout:"form",
                        items:[{  
                        	// 输入最小值
            				xtype : 'numberfield',
            				name : 'min_val',
            				allowDecimals: false,
            				minValue: 0,
            				fieldLabel : '最小值',
                         width:220
                        }]  
                    },{  
                        columnWidth:.5,
                        layout:"form",
                        style:'margin-left:20px',
                        items:[{
                        	// 输入最大值
            				xtype : 'numberfield',
            				name : 'max_val',
            				allowDecimals: false,
            				minValue: 0,
            				fieldLabel : '最大值',
                           width:220
            			}]  
                    }]  
                   },{//行1  
                       layout:"column",
                       items:[{  
                           columnWidth:.5,
                           layout:"form",
                           items:[{  
                        	// 表ID
               				xtype : 'textfield',
               				name : 'object_id',
               				hidden	:true,
               				fieldLabel : '表ID',
                            width:220
                           }]  
                       },{  
                           columnWidth:.5,
                           layout:"form",
                           items:[{
                        	// 排序
               				xtype : 'textfield',
               				name : 'rank',
               				fieldLabel : '排序',
                            width:220
               			}]  
                       }]  
                      },{
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
              	                    value: ' 图片大小10M以内，格式jpg、png'
              	                    }]
              	              }]
                    } ,{
              			xtype: 'hidden',
              			name: "pics_path"
              		}]

		});
		add_winForm.form.findField('object_id').setValue('${object_id}');
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增表单字段 ",
			width : 500,
			height : 400,
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
			items : [ add_winForm ],
			buttons : [ {
				text : "保存",
				minWidth : 70,
				handler : function() {
					if (add_winForm.getForm().isValid()) {
						add_winForm.getForm().submit({
							url : '/defined/addPlanModel',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',
							success : function(fp, o) {
				              	    Ext.example.msg("提示","保存成功！");                                                                                     
									syswin.close(); //关闭窗口  
									store.reload();
							},
							failure : function() {
								 Ext.example.msg("提示","保存成功！");                                                                                     
								 syswin.close(); //关闭窗口  
								 store.reload();                                                                                                                                                                                                                                                                                                                                                                                               							}
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
	};
    
	//form表单编辑用户
	var editform=function(){  
		var rows = grid.getSelectionModel().getSelection();
		var pic=rows[0].raw.icon;
		var edit_winForm =Ext.create('Ext.form.Panel', {
			layout:"form",
		    frame:true,
		    labelWidth:80,
		    labelAlign:"left",
			items : [{
				// 字典类型
				xtype : 'textfield',
				name : 'id',
				hidden	:true
			},{//行1  
                layout:"column",//从左往右的布局   
                items:[{  
                    columnWidth:.5,//该列在整行中所占的百分比    
                    layout:"form",//从上往下的布局   
                    items:[{
        				//表名
        				xtype : 'textfield',
        				name : 'col_name',
        				allowBlank	:false,
        				fieldLabel : '字段名',  
                        width:220 
        			}]  
                },{  
                    columnWidth:.5,//该列在整行中所占的百分比    
                    layout:"form",//从上往下的布局   
                    style:'margin-left:20px',
                    items:[{
        				// 描述
        				xtype : 'textfield',
        				name : 'col_desc',
        				fieldLabel : '描述',  
                        width:220
        			}]  
                }]  
    },{//行1  
        layout:"column",//从左往右的布局   
        items:[{  
            columnWidth:.5,//该列在整行中所占的百分比    
            layout:"form",//从上往下的布局   
            items:[{ xtype : 'combobox',
				 name : 'control_type',
				 fieldLabel : '控件类型',
				 valueField : 'type',
				 displayField : 'name',
				 editable:false,
				 allowBlank	:false,
				 store : store_ControlType,
				 listeners : { // 监听该下拉列表的选择事件
					select : function(
							combobox,
							record,
							index) {
						edit_winForm.form.findField('show_type').setValue('');
						store_ControlShowType.load({
									params : {
										control_type : combobox.value
									}
								});
					}
				},
				hiddenName:'',
				queryMode : 'local',
				width:220
			}]  
        },{  
            columnWidth:.5,//该列在整行中所占的百分比    
            layout:"form",//从上往下的布局   
            style:'margin-left:20px',
            items:[{
				// 描述
				//字段类型描述
				xtype : 'textfield',
				name : 'control_type_desc',
				fieldLabel : '控件类型描述', 
                width:220
			}]  
        }]  
       },{//行1  
           layout:"column",//从左往右的布局   
           items:[{  
               columnWidth:.5,//该列在整行中所占的百分比    
               layout:"form",//从上往下的布局   
               items:[{
      				xtype : 'combobox',
    				name : 'show_type',
    				fieldLabel : '展示类型',
    				valueField : 'type',
    				displayField : 'name',
    				editable:false,
    				hiddenName:'',
    				store : store_ControlShowType,
    				queryMode : 'local',
                    width:220
    			}]  
           },{  
               columnWidth:.5,
               layout:"form", 
               style:'margin-left:20px',
               items:[{
            	 //展示类型描述
     			xtype : 'textfield',
     			name : 'show_type_desc',
     			fieldLabel : '展示类型描述',
                width:220
                   
   			}]  
           }]  
          },{
              layout:"column",
              items:[{  
                  columnWidth:.5,
                  layout:"form",
                  items:[{  
                	//长度
                  	xtype : 'numberfield',
              		name : 'col_len',
              		allowDecimals: false,
              		fieldLabel : '长度',
              		minValue: 0,
              		maxValue: 1000,
              		width:220
                  }]  
              },{  
                  columnWidth:.5,
                  layout:"form",
                  style:'margin-left:20px',
                  items:[{
               	   //下拉列表框  
				xtype : 'textfield',
				name : 'hint_value',
				fieldLabel : '默认提示语',
                 width:220
      			}]  
              }]  
             },{//行1  
                 layout:"column",
                 items:[{  
                     columnWidth:.5,
                     layout:"form",   
                     items:[{  
                    	//长度
         				xtype : 'textfield',
         				name : 'default_value',
         				fieldLabel : '默认值',
                        width:220
                     }]  
                 },{  
                     columnWidth:.5,
                     layout:"form",
                     style:'margin-left:20px',
                     items:[{
               	  //下拉列表框  
                     xtype: 'combobox', //9  
                     fieldLabel: '是否隐藏',
                     displayField: 'value',
                     valueField : 'key',
                     name:'is_hide',
                     store: Ext.create('Ext.data.Store', {  
                         fields: [  
                           {name:'key'},{ name: 'value' }  
                           ],  
                                           data: [  
                           { 'key':'0','value': '展示' },  
                           { 'key':'1','value': '隐藏' } 
                           ]  
                     }),
                    width:220
     			}]  
             }]  
                },{//行1  
                    layout:"column",
                    items:[{  
                        columnWidth:.5,
                        layout:"form",
                        items:[{  
                        	// 输入最小值
            				xtype : 'numberfield',
            				name : 'min_val',
            				allowDecimals: false,
            				minValue: 0,
            				fieldLabel : '最小值',
                            width:220
                        }]  
                    },{  
                        columnWidth:.5,
                        layout:"form",
                        style:'margin-left:20px',
                        items:[{
                        	// 输入最大值
            				xtype : 'numberfield',
            				name : 'max_val',
            				allowDecimals: false,
            				minValue: 0,
            				fieldLabel : '最大值',
                           width:220
            			}]  
                    }]  
                   },{//行1  
                       layout:"column",
                       items:[{  
                           columnWidth:.5,
                           layout:"form",
                           items:[{  
                        	// 表ID
               				xtype : 'textfield',
               				name : 'object_id',
               				hidden	:true,
               				fieldLabel : '表ID',
                            width:220
                           }]  
                       },{  
                           columnWidth:.5,
                           layout:"form",
                           items:[{
                        	// 排序
               				xtype : 'textfield',
               				name : 'rank',
               				fieldLabel : '排序',
                            width:220
               			}]  
                       }]  
                      },{
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
   	       	                    value: ' 图片大小10M以内，格式jpg、png'
   	       	                    }]
   	       	              }]
   	             } ,{  
   	           	    xtype: 'box', //或者xtype: 'component',  
   	           	    width: 300, //图片宽度  
   	           	    height: 200, //图片高度  
   	           	    autoEl: {  
   	           	        tag: 'img',    //指定为img标签  
   	           	        src: pic    //指定url路径  
   	           	    }  
   	           	}]

		});
	    //创建window面板，表单面板是依托window面板显示的  
	    
	   
        //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
	    if(rows.length == 0)  
        {  
		   Ext.example.msg("提示","请选择要编辑的对象！");    
           return ;  
        }
	    if(rows.length > 1)  
	       {  
	 	      Ext.example.msg("提示","只能选择一个编辑的对象！");    
	          return ;  
	       }
	    edit_winForm.form.findField('id').setValue(rows[0].get('id'));
        edit_winForm.form.findField('col_name').setValue(rows[0].get('col_name')); 
        edit_winForm.form.findField('control_type').setValue(rows[0].get('control_type'))
        edit_winForm.form.findField('col_desc').setValue(rows[0].get('col_desc'));
        edit_winForm.form.findField('col_len').setValue(rows[0].get('col_len'));  
        store_ControlShowType.load({
			params : {
				control_type : rows[0].get('control_type')
			}
		});
        edit_winForm.form.findField('show_type').setValue(rows[0].raw.show_type+''); 
        edit_winForm.form.findField('object_id').setValue(rows[0].raw.object_id); 
        edit_winForm.form.findField('min_val').setValue(rows[0].raw.min_val); 
        edit_winForm.form.findField('max_val').setValue(rows[0].raw.max_val); 
        edit_winForm.form.findField('control_type_desc').setValue(rows[0].raw.control_type_desc); 
        edit_winForm.form.findField('show_type_desc').setValue(rows[0].raw.show_type_desc); 
        
        edit_winForm.form.findField('rank').setValue(rows[0].get('rank')); 
        edit_winForm.form.findField('is_hide').setValue(rows[0].get('is_hide')+''); 
        edit_winForm.form.findField('hint_value').setValue(rows[0].get('hint_value')); 
        edit_winForm.form.findField('default_value').setValue(rows[0].get('default_value')); 
	    
        var editwindow = Ext.create('Ext.window.Window',{  
	              title : "编辑表单字段",  
	              width: 500,
	              height:400,  
	              //height : 120,  
	              //plain : true,  
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
	                                          url :'/defined/editPlanModel',  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
	                                            
	                                          success: function(fp, o) {  
	                                         	      Ext.example.msg("提示","修改成功！"); 
	                                                  editwindow.close(); //关闭窗口  
	                                                  store.reload();
	                                                  grid.getSelectionModel().clearSelections();
	                                          },  
	                                          failure: function() {  
	                                        	  Ext.example.msg("提示","修改成功！"); 
                                                  editwindow.close(); //关闭窗口  
                                                  store.reload();
                                                  grid.getSelectionModel().clearSelections(); 
	                                          }  
	                                       });  
	                            } else{
	                            	Ext.example.msg("提示","请填写完整信息！"); 
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
	    };
});

function showDetail(id,name){
	  if(name=='null'){
			name='';
		}
	  parent.addTab("plan_model_"+id,"字段【"+name+"】详情","Fuwushangyunyingxinxiguanli","/defined/planModelItemShow?model_id="+id); 	 
}


</script>
</head>
<body>
</body>
</html>