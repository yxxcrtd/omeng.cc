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
<title>搜索分词</title>
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
                   {header:'关键词',dataIndex:'keyword',sortable:true,fixed:false},
                   {header:'服务类型ID',dataIndex:'serviceType',hidden:true,sortable:true,fixed:false},
                   {header:'服务类型',dataIndex:'serviceTypeName',sortable:true,fixed:false},
                   {header:'app类型',dataIndex:'appName',sortable:true,fixed:false},
                   {header:'app类型',dataIndex:'appType',hidden:true,sortable:true,fixed:false},
                   {header:'图片',dataIndex:'img',hidden:true,sortable:true,fixed:false},
                   {header:'url',dataIndex:'url',sortable:true,fixed:false},
                   {header:'字数',dataIndex:'wordsNum',sortable:true,fixed:false}
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/systemManager/searchWordsList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'keyword'},  
           {name:'serviceType'},  
           {name:'serviceTypeName'},  
           {name:'appName'},
           {name:'appType'},
           {name:'img'},
           {name:'url'},
           {name:'wordsNum'}
        ]  
    });
    
	// app类型
	var store_appType = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
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
	// 服务类型
	var store_ServiceType = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/getServiceByCatId',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'service_type'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'service_name'
		} ]
	});
    
    
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var keyword=Ext.getCmp('keyword').getValue();
     	 var wordsNum=Ext.getCmp('wordsNum').getValue();
		 var appType=Ext.getCmp('app_type').getValue();
		 var serviceType=Ext.getCmp('service_type').getValue();
         var new_params = {keyword:keyword,wordsNum:wordsNum,appType:appType,serviceType:serviceType};    
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
	    	   <cms:havePerm url='/systemManager/addSearchWords'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   showform(null);
		    		   }
		       }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/systemManager/deleteSearchWords'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		    	}},'-',
			    </cms:havePerm>
			    <cms:havePerm url='/systemManager/importSearchWords'>
		        { xtype: 'button', id:'import', text: '导入',iconCls:'daoru',
			    	   listeners: {
			    		   click:function(){
			    			   importForm();
			    		   }
			    }},'-',
		        </cms:havePerm>
		        <cms:havePerm url='/systemManager/exportSearchWords'>
     	       { xtype: 'button', text: '导出',iconCls:'Daochu',
         	    	   listeners: {
         	    		   click:function(){
         	    			   exportAll();
         	    		   }
         	    	   }
                }</cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'keyword',name: 'keyword',fieldLabel: '关键词',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'numberfield',id:'wordsNum',name: 'wordsNum',fieldLabel: '字数',labelAlign:'left',labelWidth:60},'-',
					{
				xtype : 'combobox',
				id : 'app_type',
				name : 'app_type',
				fieldLabel : 'app类型',
				valueField : 'app_type',
				displayField : 'app_name',
				width : 200,
				store : store_appType,
				editable:true,
				listeners : { // 监听该下拉列表的选择事件
					select : function(
							combobox,
							record,
							index) {
						Ext.getCmp('service_type').setValue('');
						store_ServiceType
								.load({
									params : {
										app : combobox.value
									}
								});
					}
				},
				queryMode : 'local',
				labelAlign : 'left',
				labelWidth : 60
			},'-',
			{
				xtype : 'combobox',
				id : 'service_type',
				name : 'service_type',
				width : 200,
				fieldLabel : '服务类型',
				valueField : 'service_type',
				displayField : 'service_name',
				store : store_ServiceType,
				queryMode : 'local',
				labelAlign : 'left',
				labelWidth : 60
			},'-',

        	       <cms:havePerm url='/systemManager/searchWordsList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var keyword=Ext.getCmp('keyword').getValue();
    	    			   var wordsNum=Ext.getCmp('wordsNum').getValue();
    	    			   var appType=Ext.getCmp('app_type').getValue();
    	    			   var serviceType=Ext.getCmp('service_type').getValue();  
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,keyword:keyword,wordsNum:wordsNum,appType:appType,serviceType:serviceType}}); 
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
  
    <cms:havePerm url='/systemManager/editSearchWords'>
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data); 
    });
    </cms:havePerm>
    
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
	
	
	 //导出所有关键词
	function exportAll() {
		 var keyword=Ext.getCmp('keyword').getValue();
     	 var wordsNum=Ext.getCmp('wordsNum').getValue();
		 var appType=Ext.getCmp('app_type').getValue();
		 var serviceType=Ext.getCmp('service_type').getValue();
		
        window.location.href = '/systemManager/exportSearchWords?keyword='+keyword+'&wordsNum='+wordsNum+'&appType='+appType
			+'&serviceType='+serviceType;
	};
	
	
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
                     url:"/systemManager/deleteSearchWords",  
                     params:{ids:ids},  
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
 		var pic = '';
 		if(d!=null&&d.id!=null&&d.id!=0){
 			pic = d.img;
 		}
		var add_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 440,
			height : 400,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [
            {
	            xtype: 'hidden',
	            name: "id"
            },
			{
				// app类型
				xtype : 'textfield',
				name : 'keyword',
				allowBlank	:false,
				fieldLabel : '关键词'
			},{
				xtype : 'combobox',
				name : 'appType',
				fieldLabel : 'app类型',
				valueField : 'app_type',
				displayField : 'app_name',
				store : store_appType,
				editable:true,
				listeners : { // 监听该下拉列表的选择事件
					select : function(combobox,record,index) {
						add_winForm.form.findField('serviceType').setValue('');
						store_ServiceType.load({
									params : {
										app : combobox.value
									}
								});
					}
				}
			},{
				xtype : 'combobox',
				name : 'serviceType',
				fieldLabel : '服务类型',
				valueField : 'service_type',
				displayField : 'service_name',
				store : store_ServiceType,
				queryMode : 'local',
				editable:true
			},{
				// url
				xtype : 'textfield',
				name : 'url',
				fieldLabel : '链接url'
			}
			/**,{
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
			name: "img"
		},{  
   	    xtype: 'box', //或者xtype: 'component',  
   	    width: 220, //图片宽度  
   	    height: 130, //图片高度  
   	    autoEl: {  
   	        tag: 'img',    //指定为img标签  
   	        src: pic    //指定url路径  
   	    }  
   	   } **/
   	   ]

		});

	    var title = '新增关键词';
	    var reqName = 'addSearchWords';
 	    add_winForm.form.findField('appType').setValue('');
 	    add_winForm.form.findField('serviceType').setValue('');
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_winForm.form.findField('id').setValue(d.id);
	    	    add_winForm.form.findField('keyword').setValue(d.keyword);
	    	    add_winForm.form.findField('appType').setValue(d.appType);
				store_ServiceType.load({
					params : {
						app : d.appType
					}
				});
	    	    add_winForm.form.findField('serviceType').setValue(parseInt(d.serviceType));
	    	    add_winForm.form.findField('url').setValue(d.url); 
	    	  //  add_winForm.form.findField('img').setValue(d.img); 
	        	title = '编辑关键词';
	        	reqName = 'editSearchWords';
	    	}
        //================判断是编辑还是新增===============
		
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : title,
			width : 450,
			height : 380,
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
						var at = add_winForm.form.findField('appType').getValue();
						if(at==null||at==''){
						 	Ext.example.msg("提示","请选择app类型！");
							return;
						}
						//var st = add_winForm.form.findField('serviceType').getValue();
						//if(st==null||st==''){
						// 	Ext.example.msg("提示","请选择服务类型！");
						//	return;
						//}
						add_winForm.getForm().submit({
							url : '/systemManager/'+reqName,
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',
						    method : "POST",  
						    success : function(fp, o) {
                                if (o.result.data == 1) {  
                                    syswin.close(); //关闭窗口  
                                    Ext.example.msg("提示",o.result.message);
                            		add_winForm.form.reset(); 
                                    store.reload();  
                                }else {  
                              	    Ext.example.msg("提示",o.result.message); 
                                }  
							},
							failure : function() {
		                         syswin.close(); //关闭窗口  
                                 Ext.example.msg("提示","保存成功");
                         		 add_winForm.form.reset(); 
                                 store.reload();  
                                 return;
							}
						});
					}
				}
			}, {
				text : "关闭",
				minWidth : 70,
				handler : function() {
					add_winForm.form.reset(); 
					syswin.close();
				}
			} ]
		});
		syswin.show();
	};
    
	
	// =======导入======
	var importForm = function(d) {
		var import_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 440,
			height : 300,
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
                 value: '导入关键词库'  
                  
             },
    {
     layout:'column',
	 border:false,
     items:[{ 
         fieldLabel:'选择Excel文件',
         name:'upload',
         xtype:'fileuploadfield',  
         anchor:'80%',  
         emptyText:'请选择Excel文件', 
         regex:/(xls)|(xlsx)$/i,
         buttonText: '选择',
         selectOnFocus:true  
     },{xtype: 'displayfield',   
         name: 'displayfield',  
         value: ' 文件大小10M以内，格式xls、xlsx'
         }]
        }]

		});

	    var title = '导入关键词库';
		
		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : title,
			width : 450,
			height : 280,
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
			items : [ import_winForm ],
			buttons : [ {
				text : "保存",
				minWidth : 70,
				handler : function() {
					if (import_winForm.getForm().isValid()) {
						import_winForm.getForm().submit({
							url : '/systemManager/importSearchWords',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',
							success : function(fp, o) {
								    Ext.example.msg("提示","导入成功！");
									syswin.close(); //关闭窗口  
									import_winForm.form.reset(); 
									store.reload();
							},
							failure : function() {
							    Ext.example.msg("提示","导入失败！");
							}
						});
					}
				}
			}, {
				text : "关闭",
				minWidth : 70,
				handler : function() {
					import_winForm.form.reset(); 
					syswin.close();
				}
			} ]
		});
		syswin.show();
	};
	
});



</script>
</head>
<body>
</body>
</html>