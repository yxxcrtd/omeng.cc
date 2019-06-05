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
                   {header:'字典ID',dataIndex:'id',sortable:true,fixed:false},
                   {header:'字典类型',dataIndex:'dict_type',sortable:true,fixed:false},
                   {header:'字典主键',dataIndex:'dict_key',sortable:true,fixed:false},
                   {header:'字典值',dataIndex:'dict_value',sortable:true,fixed:false},
                   {header:'父级ID',dataIndex:'parent_dict_id',sortable:true,fixed:false},
                   {header:'操作',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                       return '<a href="javascript:showDetail(\''+r.data.id+'\',\''+r.data.dict_type+'\',\''+r.data.dict_value+'\')">附件</a>';
             		}}
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/systemManager/getSystemParam',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'id'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'dict_type'},  
           {name:'dict_key'},  
           {name:'dict_value'},
           {name:'parent_dict_id'}
        ]  
    });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var dict_type=Ext.getCmp('dict_type').getValue();
		 var parent_dict_id=Ext.getCmp('parent_dict_id').getValue();
         var new_params = {dict_type:dict_type,parent_dict_id : parent_dict_id};    
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
           { xtype: 'button', id:'rush', text: '刷新系统',iconCls:'Refresh',
	    	   listeners: {
	    		   click:function(){
	    			   flushParam();
	    		   }
	    	   }},'-',
	    	   <cms:havePerm url='/systemManager/addSystemParam'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform();
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/systemManager/deleteSystemParam'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		    	   }},'-',
			       </cms:havePerm>
			       <cms:havePerm url='/systemManager/editSystemParam'>
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
        	items:[{ xtype: 'textfield',id:'dict_type',name: 'dict_type',fieldLabel: '字典类型',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'textfield',id:'parent_dict_id',name: 'parent_dict_id',fieldLabel: '父级ID',labelAlign:'left',labelWidth:60},'-',

        	       <cms:havePerm url='/systemManager/getSystemParam'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var dict_type=Ext.getCmp('dict_type').getValue();
    	    			   var parent_dict_id=Ext.getCmp('parent_dict_id').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,dict_type:dict_type,parent_dict_id : parent_dict_id}}); 
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

    <cms:havePerm url='/systemManager/editSystemParam'>
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
                     url:"/systemManager/deleteSystemParam",  
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
				// 字典类型
				xtype : 'textfield',
				name : 'dict_type',
				allowBlank	:false,
				fieldLabel : '字典类型'
			}, {
				// 字典记录主键
				xtype : 'textfield',
				name : 'dict_key',
				allowBlank	:false,
				fieldLabel : '字典主键'
			}, 
			{
				//字典记录值
				xtype : 'textfield',
				name : 'dict_value',
				allowBlank	:false,
				fieldLabel : '字典值'
			}, {
				// 备注
				xtype : 'textfield',
				name : 'remark',
				fieldLabel : '备注'
			}, //父级字典ID
			{
				// 父级字典ID
				xtype : 'textfield',
				name : 'parent_dict_id',
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
               
            },
			
			{
				// sort
				xtype : 'textfield',
				name : 'sort',
				fieldLabel : '排序'
			}]

		});

		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增字典",
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
							url : '/systemManager/addSystemParam',
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
		var rows = grid.getSelectionModel().getSelection();
		var pic=rows[0].raw.icon;
	    var edit_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 450,
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
	    			},{
	    				// 字典类型
	    				xtype : 'textfield',
	    				name : 'dict_type',
	    				allowBlank	:false,
	    				fieldLabel : '字典类型'
	    			}, {
	    				// 字典记录主键
	    				xtype : 'textfield',
	    				name : 'dict_key',
	    				allowBlank	:false,
	    				fieldLabel : '字典主键'
	    			}, 
	    			{
	    				//字典记录值
	    				xtype : 'textfield',
	    				name : 'dict_value',
	    				allowBlank	:false,
	    				fieldLabel : '字典值'
	    			}, {
	    				// 备注
	    				xtype : 'textfield',
	    				name : 'remark',
	    				fieldLabel : '备注'
	    			}, //父级字典ID
	    			{
	    				// 父级字典ID
	    				xtype : 'textfield',
	    				name : 'parent_dict_id',
	    				fieldLabel : '父级ID'
	    			},
	    			{  
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
	                   
	                },
	    			{
	    				// sort
	    				xtype : 'textfield',
	    				name : 'sort',
	    				fieldLabel : '排序'
	    			}]  
	            });  
	    
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
        edit_winForm.form.findField('dict_type').setValue(rows[0].get('dict_type'));  
        edit_winForm.form.findField('dict_key').setValue(rows[0].get('dict_key'));
        edit_winForm.form.findField('dict_value').setValue(rows[0].get('dict_value'));  
        edit_winForm.form.findField('remark').setValue(rows[0].raw.remark); 
        edit_winForm.form.findField('parent_dict_id').setValue(rows[0].get('parent_dict_id'));  
        edit_winForm.form.findField('is_leaves').setValue(rows[0].raw.is_leaves+''); 
        edit_winForm.form.findField('sort').setValue(rows[0].raw.sort);
	    var editwindow = Ext.create('Ext.window.Window',{  
	              title : "编辑字典",  
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
	                                          url :'/systemManager/editSystemParam',  
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

 function showDetail(id,dict_type,name){
	  if(name=='null'){
			name='';
		}
	  parent.addTab("dictiony_"+id,"字典【"+name+"】附件","Fuwushangyunyingxinxiguanli","/systemManager/dictAttachMentShow?id="+id+"&dict_type="+dict_type); 	 
}
</script>
</head>
<body>
</body>
</html>