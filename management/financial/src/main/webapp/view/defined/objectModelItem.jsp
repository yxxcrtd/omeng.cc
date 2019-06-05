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
<title>自定义表单字典</title>
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
                   {header:'itemID',dataIndex:'id',sortable:true,fixed:false,align:'center',hidden:true},
                   {header:'字段名',dataIndex:'col_name',sortable:true,fixed:false,align:'center'},
                   {header:'枚举值',dataIndex:'item_name',sortable:true,fixed:false,align:'center'}
                  
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/defined/getObjectModelItemList',  
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
           {name:'item_name'}
        ]  
    });
    
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var model_id=Ext.getCmp('model_id').getValue();
		 var item_name=Ext.getCmp('item_name').getValue();
		 var new_params = {model_id:model_id,item_name : item_name};    
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
           
	    	   <cms:havePerm url='/defined/addObjectModelItem'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform();
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/defined/deleteObjectModelItem'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		    	   }},'-',
			       </cms:havePerm>
			       <cms:havePerm url='/defined/editObjectModelItem'>
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
        	items:[{ xtype: 'textfield',id:'model_id',name: 'model_id',fieldLabel: '字段Id',value:'${model_id}',labelAlign:'left',labelWidth:60,hidden:true},'-',
        	       { xtype: 'textfield',id:'item_name',name: 'item_name',fieldLabel: '枚举值',labelAlign:'left',labelWidth:60},'-',
         	      
        	       <cms:havePerm url='/defined/getObjectModelItemList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var model_id=Ext.getCmp('model_id').getValue();
    	    			   var item_name=Ext.getCmp('item_name').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,model_id:model_id,item_name : item_name}}); 
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
    
    <cms:havePerm url='/defined/editObjectModelItem'>
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
                     url:"/defined/deleteObjectModelItem",  
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
			width : 340,
			height : 160,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [ {
				// 枚举值
				xtype : 'textfield',
				name : 'item_name',
				allowBlank	:false,
				fieldLabel : '枚举值'
			},{
				// 字段id
				xtype : 'textfield',
				name : 'model_id',
				fieldLabel : '字段id',
				hidden:true
			}
			]

		});
		add_winForm.form.findField('model_id').setValue('${model_id}'); 
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增字段枚举值",
			width : 360,
			height : 160,
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
							url : '/defined/addObjectModelItem',
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
	    var edit_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 430,
	                height:470,
	                bodyPadding:5,  
	                //renderTo:"panel21",  
	                fieldDefaults: {  
	                    labelAlign: 'left',  
	                    labelWidth: 90,  
	                    anchor: '100%'  
	                },  
	                items: [ {
	    				// 字典类型
	    				xtype : 'textfield',
	    				name : 'id',
	    				hidden	:true
	    			}, {
	    				// 枚举值
	    				xtype : 'textfield',
	    				name : 'item_name',
	    				allowBlank	:false,
	    				fieldLabel : '枚举值'
	    			},{
	    				// 字段id
	    				xtype : 'textfield',
	    				name : 'model_id',
	    				fieldLabel : '字段id',
	    				hidden:true
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
        edit_winForm.form.findField('item_name').setValue(rows[0].get('item_name')); 
        edit_winForm.form.findField('model_id').setValue(rows[0].raw.model_id); 
       
	    var editwindow = Ext.create('Ext.window.Window',{  
	              title : "编辑字段枚举值",  
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
	                                          url :'/defined/editObjectModelItem',  
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
	                            }  else{
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


</script>
</head>
<body>
</body>
</html>