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
<title>配置参数</title>
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
                   {header:'配置ID',dataIndex:'id',sortable:true,fixed:false,hidden:true},
                   {header:'配置key',dataIndex:'config_key',sortable:true,fixed:false},
                   {header:'配置value',dataIndex:'config_value',sortable:true,fixed:false},
                   {header:'备注',dataIndex:'remark',sortable:true,fixed:false},
                   {header:'备用字段1',dataIndex:'standby_field1',sortable:true,fixed:false},
                   {header:'备用字段2',dataIndex:'standby_field2',sortable:true,fixed:false},
                   {header:'备用字段3',dataIndex:'standby_field3',sortable:true,fixed:false},
                   {header:'备用字段4',dataIndex:'standby_field4',sortable:true,fixed:false},
                   {header:'备用字段5',dataIndex:'standby_field5',sortable:true,fixed:false}
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/systemManager/getConfigurationParam',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'id'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'config_key'},  
           {name:'config_value'},  
           {name:'remark'},
           {name:'standby_field1'},
           {name:'standby_field2'},
           {name:'standby_field3'},
           {name:'standby_field4'},
           {name:'standby_field5'}
        ]  
    });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var config_key=Ext.getCmp('config_key').getValue();
		 var config_value=Ext.getCmp('config_value').getValue();
         var new_params = {config_key:config_key,config_value : config_value};    
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
           '-',
	    	   <cms:havePerm url='/systemManager/addConfigurationParam'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform();
		    		   }
		    	   }},'-',
		        </cms:havePerm>
			       <cms:havePerm url='/systemManager/editConfigurationParam'>
		           { xtype: 'button', id:'edit', text: '修改',iconCls:'Edit',
			    	   listeners: {
			    		   click:function(){
			    			   editform();
			    		   }
			    	   }},'-',
	           </cms:havePerm>
	           <cms:havePerm url='/systemManager/deleteConfigurationParam'>
               { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
    	    	   listeners: {
    	    		   click:function(){
    	    			   deleteRecord();
    	    		   }
    	    	   }},'-',</cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'config_key',name: 'config_key',fieldLabel: '配置key',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'textfield',id:'config_value',name: 'config_value',fieldLabel: '配置value',labelAlign:'left',labelWidth:60},'-',

        	       <cms:havePerm url='/systemManager/getSystemParam'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var config_key=Ext.getCmp('config_key').getValue();
    	    			   var config_value=Ext.getCmp('config_value').getValue();
    	    		       store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,config_key:config_key,config_value : config_value}}); 
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

    <cms:havePerm url='/systemManager/editConfigurationParam'>
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
                     url:"/systemManager/deleteConfigurationParam",  
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
			width : 420,
			height : 360,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [{
				//配置key
				xtype : 'textfield',
				name : 'config_key',
				allowBlank	:false,
				fieldLabel : '配置key'
			}, {
				// 配置value
				xtype : 'textfield',
				name : 'config_value',
				allowBlank	:false,
				fieldLabel : '配置value'
			}, 
			{
				//备注
				xtype : 'textfield',
				name : 'remark',
				fieldLabel : '备注'
			}, {
				// 备用
				xtype : 'textfield',
				name : 'standby_field1',
				fieldLabel : '备用1'
			}, 
			{
				// 备用
				xtype : 'textfield',
				name : 'standby_field2',
				fieldLabel : '备用2'
			},
			{
				// 备用
				xtype : 'textfield',
				name : 'standby_field3',
				fieldLabel : '备用3'
			},
			{
				// 备用
				xtype : 'textfield',
				name : 'standby_field4',
				fieldLabel : '备用4'
			},
			{
				// 备用
				xtype : 'textfield',
				name : 'standby_field5',
				fieldLabel : '备用5'
			}]

		});

		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增配置项",
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
							url : '/systemManager/addConfigurationParam',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',
							success : function(fp, o) {
				              	    Ext.example.msg("提示","保存成功！");                                                                                     
									syswin.close(); //关闭窗口  
									store.reload();
							},
							failure : function() {
								Ext.example.msg("提示","保存失败！");                                                                                                                                                                                                                                                                                                                                                                                                							}
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
    
	//form表单编辑用户
	var editform=function(){  
	    var edit_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 420,
	                height:470,
	                bodyPadding:5,  
	                //renderTo:"panel21",  
	                fieldDefaults: {  
	                    labelAlign: 'left',  
	                    labelWidth: 90,  
	                    anchor: '100%'  
	                },  
	                items: [ {
	    				// 配置id
	    				xtype : 'textfield',
	    				name : 'id',
	    				hidden	:true
	    			},{
	    				//配置key
	    				xtype : 'textfield',
	    				name : 'config_key',
	    				allowBlank	:false,
	    				fieldLabel : '配置key'
	    			}, {
	    				// 配置value
	    				xtype : 'textfield',
	    				name : 'config_value',
	    				allowBlank	:false,
	    				fieldLabel : '配置value'
	    			}, 
	    			{
	    				//备注
	    				xtype : 'textfield',
	    				name : 'remark',
	    				fieldLabel : '备注'
	    			}, {
	    				// 备用
	    				xtype : 'textfield',
	    				name : 'standby_field1',
	    				fieldLabel : '备用1'
	    			}, 
	    			{
	    				// 备用
	    				xtype : 'textfield',
	    				name : 'standby_field2',
	    				fieldLabel : '备用2'
	    			},
	    			{
	    				// 备用
	    				xtype : 'textfield',
	    				name : 'standby_field3',
	    				fieldLabel : '备用3'
	    			},
	    			{
	    				// 备用
	    				xtype : 'textfield',
	    				name : 'standby_field4',
	    				fieldLabel : '备用4'
	    			},
	    			{
	    				// 备用
	    				xtype : 'textfield',
	    				name : 'standby_field5',
	    				fieldLabel : '备用5'
	    			}]  
	            });  
	    //创建window面板，表单面板是依托window面板显示的  
	    
	    var rows = grid.getSelectionModel().getSelection();  
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
        edit_winForm.form.findField('config_key').setValue(rows[0].get('config_key'));  
        edit_winForm.form.findField('config_value').setValue(rows[0].get('config_value'));
        edit_winForm.form.findField('remark').setValue(rows[0].get('remark'));  
        edit_winForm.form.findField('standby_field1').setValue(rows[0].get('standby_field1')); 
        edit_winForm.form.findField('standby_field2').setValue(rows[0].get('standby_field2'));
        edit_winForm.form.findField('standby_field3').setValue(rows[0].get('standby_field3'));
        edit_winForm.form.findField('standby_field4').setValue(rows[0].get('standby_field4'));
        edit_winForm.form.findField('standby_field5').setValue(rows[0].get('standby_field5'));
        
	    var editwindow = Ext.create('Ext.window.Window',{  
	              title : "编辑配置项",  
	              width: 450,
	              height:350,  
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
	                                          url :'/systemManager/editConfigurationParam',  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
	                                            
	                                          success: function(fp, o) {  
	                                              if (o.result== true) {  
	                                         	      Ext.example.msg("提示","修改成功！"); 
	                                                  editwindow.close(); //关闭窗口  
	                                                  store.reload();
	                                                  grid.getSelectionModel().clearSelections();
	                                              }else {  
	                                                  Ext.example.msg("提示","修改失败！"); 
	                                              }  
	                                          },  
	                                          failure: function() {  
	                                              Ext.example.msg("提示","修改失败！"); 
	                                          }  
	                                       });  
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



</script>
</head>
<body>
</body>
</html>