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
<title>价值标签</title>
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
                   {header:'名称',dataIndex:'name',sortable:true,fixed:false},
                   {header:'最小值',dataIndex:'min_value',sortable:true,fixed:false},
                   {header:'最大值',dataIndex:'max_value',sortable:true,fixed:false},
                   {header:'标签类型',dataIndex:'lable_type',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>时间价值</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>爱好价值</span>";  
                       }else {  
                           return "<span style='color:blue;font-weight:bold';>技能价值</span>";  
                       }
           	       }},
                   {header:'时间',dataIndex:'create_time',sortable:true,fixed:false},
                   {header:'状态',dataIndex:'is_use',renderer:function(value,v,r){  
                       if(value=='0'){  
                       return '<a href="javascript:StartOrStopLabel(\''+r.data.id+'\','+value+')"><span style="color:green;font-weight:bold;">禁用</span></a>';
                       } else if(value=='1'){  
                       return '<a href="javascript:StartOrStopLabel(\''+r.data.id+'\','+value+')"><span style="color:red;font-weight:bold;">启用</span></a>';
                       }
           		}},
               ];
    
     store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/systemManager/getValueLabel',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'id'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'name'},  
           {name:'min_value'},  
           {name:'max_value'},
           {name:'is_use'},
           {name:'lable_type'},
           {name:'create_time'}
        ]  
    });
    
    var lableTypeStore = new Ext.data.SimpleStore({
     	fields:['lableName','lableType'],
     	data:[['全部',''],
     	      ['时间价值','1'],
     	      ['爱好价值','2'],
     	      ['技能价值','3']
     	]
     });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var lable_type=Ext.getCmp('lable_type').getValue();
		 var name=Ext.getCmp('name').getValue();
         var new_params = {lable_type:lable_type,name : name};    
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
	    	   <cms:havePerm url='/systemManager/addValueLabel'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform();
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/systemManager/deleteValueLabel'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		    	   }},'-',
			       </cms:havePerm>
			       <cms:havePerm url='/systemManager/editValueLabel'>
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
        	items:[{ xtype: 'textfield',id:'name',name: 'name',fieldLabel: '名称',labelAlign:'left',labelWidth:60},'-',
        	       {xtype : 'combobox',id : 'lable_type',name : 'lable_type',fieldLabel : '标签类型',value:'',valueField : 'lableType',editable:false,displayField : 'lableName',
				   store : lableTypeStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
     	      
        	       <cms:havePerm url='/systemManager/getValueLabel'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var lable_type=Ext.getCmp('lable_type').getValue();
    	    			   var name=Ext.getCmp('name').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,lable_type:lable_type,name : name}}); 
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

    <cms:havePerm url='/systemManager/editValueLabel'>
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
                     url:"/systemManager/deleteValueLabel",  
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
				name : 'name',
				allowBlank	:false,
				fieldLabel : '名称'
			}, {
				// 输入最小值
				xtype : 'numberfield',
				name : 'min_value',
				allowDecimals: false,
				minValue: 0,
				allowBlank	:false,
				fieldLabel : '最小值'
			},{
				// 输入最大值
				xtype : 'numberfield',
				name : 'max_value',
				allowDecimals: false,
				minValue: 0,
				allowBlank	:false,
				fieldLabel : '最大值'
			},{  
                //下拉列表框  
                xtype: 'combobox', //9  
                fieldLabel: '状态',
                displayField: 'value',
                valueField : 'key',
                name:'is_use',
                store: Ext.create('Ext.data.Store', {  
                    fields: [  
                      {name:'key'},{ name: 'value' }  
                      ],  
                                      data: [  
                             { 'key':'0','value': '禁用' },  
                             { 'key':'1','value': '启用' }  
                      ]  
                })
               
            },{
				xtype : 'combobox',
				name : 'lable_type',
				width : 200,
				fieldLabel : '标签类型',
				valueField : 'lableType',
				displayField : 'lableName',
				store : lableTypeStore,
				editable:true,
				value: '',
				queryMode : 'local'
			}]

		});

		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增标签",
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
							url : '/systemManager/addValueLabel',
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
	    				name : 'name',
	    				allowBlank	:false,
	    				fieldLabel : '名称'
	    			}, {
	    				// 输入最小值
	    				xtype : 'numberfield',
	    				name : 'min_value',
	    				allowDecimals: false,
	    				minValue: 0,
	    				allowBlank	:false,
	    				fieldLabel : '最小值'
	    			},{
	    				// 输入最大值
	    				xtype : 'numberfield',
	    				name : 'max_value',
	    				allowDecimals: false,
	    				minValue: 0,
	    				allowBlank	:false,
	    				fieldLabel : '最大值'
	    			},{  
	                    //下拉列表框  
	                    xtype: 'combobox', //9  
	                    fieldLabel: '状态',
	                    displayField: 'value',
	                    valueField : 'key',
	                    name:'is_use',
	                    store: Ext.create('Ext.data.Store', {  
	                        fields: [  
	                          {name:'key'},{ name: 'value' }  
	                          ],  
	                                          data: [  
	                                 { 'key':'0','value': '禁用' },  
	                                 { 'key':'1','value': '启用' }  
	                          ]  
	                    })
	                   
	                },{
	    				xtype : 'combobox',
	    				name : 'lable_type',
	    				width : 200,
	    				fieldLabel : '标签类型',
	    				valueField : 'lableType',
	    				displayField : 'lableName',
	    				store : lableTypeStore,
	    				editable:true,
	    				value: '',
	    				queryMode : 'local'
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
        edit_winForm.form.findField('name').setValue(rows[0].get('name'));  
        edit_winForm.form.findField('min_value').setValue(rows[0].get('min_value'));
        edit_winForm.form.findField('max_value').setValue(rows[0].get('max_value'));  
        edit_winForm.form.findField('is_use').setValue(rows[0].get('is_use')); 
        edit_winForm.form.findField('lable_type').setValue(rows[0].get('lable_type')); 
	    var editwindow = Ext.create('Ext.window.Window',{  
	              title : "编辑标签",  
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
	                                          url :'/systemManager/editValueLabel',  
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

var StartOrStopLabel=function(id,is_use){

	var sucessmessage;
	var errormessage;
	var message;
	var edit_del;
	if(is_use==0){
		edit_del=1;
		sucessmessage='标签已启用！';
		errormessage='标签启用失败！';
		message='请确定要执行启用操作吗?'
	}else if(is_use==1){
		edit_del=0;
		sucessmessage='标签已禁用！';
		errormessage='标签禁用失败！'
		message='请确定要执行禁用操作吗?'
	}
	Ext.Msg.confirm("提示信息", message, function(btn) {
		if (btn == 'yes') {
			Ext.Ajax.request({
				url : "/systemManager/startOrstopLabel",
				params : {
					id : id,
					is_use : edit_del
				},
				method : 'post',
				success : function(o) {
				    Ext.example.msg("提示",sucessmessage); 
					store.reload();
					return;
				},
				failure : function(form, action) {
				    Ext.example.msg("提示",errormessage); 
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