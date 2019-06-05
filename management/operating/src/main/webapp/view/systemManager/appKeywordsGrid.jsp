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
<title>应用关键词</title>
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
                   {header:'app名称',dataIndex:'app_name',sortable:true,fixed:false},
                   {header:'app类型',dataIndex:'app_type',sortable:true,fixed:false},
                   {header:'字数',dataIndex:'wordsNum',sortable:true,fixed:false}
               ];
	
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true, //设置属性进行请求后台排序
        proxy:{  
            type:'ajax',  
            url:'/systemManager/appKeywordsList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'app_name'},  
           {name:'app_type'}, 
           {name:'keyword'}, 
           {name:'wordsNum'}
        ]  
    });
    
	// app类型
	var store_appType = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/showAppType',
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
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var keyword=Ext.getCmp('keyword').getValue();
    	var wordsNum=Ext.getCmp('wordsNum').getValue();
		var app_type=Ext.getCmp('app_type').getValue();
        var new_params = { keyword:keyword,wordsNum:wordsNum,app_type : app_type};    
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
	       <cms:havePerm url='/systemManager/addAppKeywords'>
           { xtype: 'button', id:'edit', text: '添加',iconCls:'NewAdd',
	    	   listeners: {
	    		   click:function(){
	    			   showform(null);
	    		   }
	    	   }},'-',
	       </cms:havePerm>
	       <cms:havePerm url='/systemManager/deleteAppKeywords'>
           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
	    	   listeners: {
	    		   click:function(){
	    			   delRecord();
	    		   }
	       }},'-',
           </cms:havePerm>
		    <cms:havePerm url='/systemManager/importAppKeywords'>
	        { xtype: 'button', id:'import', text: '导入',iconCls:'daoru',
		    	   listeners: {
		    		   click:function(){
		    			   importForm();
		    		   }
		    }},'-',
	        </cms:havePerm>
	        <cms:havePerm url='/systemManager/exportAppKeywords'>
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
				store : store_appType,
				editable:false,
				queryMode : 'local',
				labelAlign : 'left',
				labelWidth : 60
			   },'-',
        	     <cms:havePerm url='/systemManager/appKeywordsList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var keyword=Ext.getCmp('keyword').getValue();
    	    			   var wordsNum=Ext.getCmp('wordsNum').getValue();
    	    			   var app_type=Ext.getCmp('app_type').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,keyword:keyword,wordsNum:wordsNum,app_type : app_type}}); 
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
   
   
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    // 双击grid记录，编辑项目信息
    <cms:havePerm url='/systemManager/editAppKeywords'>
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
	
	 //导出所有应用关键词
	function exportAll() {
		 var keyword=Ext.getCmp('keyword').getValue();
     	 var wordsNum=Ext.getCmp('wordsNum').getValue();
     	 var app_type=Ext.getCmp('app_type').getValue();
		 
        window.location.href = '/systemManager/exportAppKeywords?keyword='+keyword+'&wordsNum='+wordsNum+'&app_type='+app_type;
	};
	
	//删除系统项目
	function delRecord()  
    {  
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
          	 Ext.example.msg("提示","请选择要删除的对象！");
             return ;  
    	 }
         //ids：所有选中的项目Id的集合使用','隔开，初始化为空  
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
                     url:"/systemManager/deleteAppKeywords",  
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
    
    
    
    //************************************保存应用关键词信息start************************************
	//form表单
	function showform(d){ 
	    var res_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 500, 
	                height:300,
	                bodyPadding:5,  
	                //renderTo:"panel21",  
	                fieldDefaults: {  
	                    labelAlign: 'left',  
	                    labelWidth: 100,  
	                    anchor: '80%'  
	                },  
	                items: [{  
	                    //显示文本框，相当于label  
	                    xtype: 'displayfield',   
	                    name: 'displayfield1',  
	                    value: '应用关键词信息'  
	                     
	                },{
	    				xtype: 'hidden',
	    				name: "id"
	    			},{  
	                    //关键词
	                    xtype: 'textfield', 
	                    name: 'keyword',
	                    allowBlank: false,
	                    fieldLabel: '关键词'  
	                },{
	    				xtype : 'combobox',
	    				name : 'app_type',
	    				fieldLabel : 'app类型',
	    				valueField : 'app_type',
	    				displayField : 'app_name',
	    				store : store_appType,
	    				editable:false
	    			}]  

	            });  
	    
	    var title = '新增应用关键词';
	    var reqName = 'addAppKeywords';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    res_winForm.form.findField('id').setValue(d.id);
	    	    res_winForm.form.findField('app_type').setValue(d.app_type);
	    	    res_winForm.form.findField('keyword').setValue(d.keyword); 
	        	title = '编辑应用关键词';
	        	reqName = 'editAppKeywords';
	    	}
	

        //================判断是编辑还是新增===============
	    //创建window面板，表单面板是依托window面板显示的  
	    var syswin = Ext.create('Ext.window.Window',{  
	              title : title,  
	              width : 450, 
	              height: 250,
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
	              items : [res_winForm],  
	              buttons : [{  
	                         text : "保存",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            if (res_winForm.getForm().isValid()) { 
	                            	var at =  res_winForm.form.findField('app_type').getValue();
	                            	if(at==null||at==''){
	                            		  Ext.example.msg("提示","请选择一个app类型！");
	                            		  return;
	                            	}
	                                res_winForm.getForm().submit({  
	                                          url :'/systemManager/'+reqName,  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
	                                            
	                                          success: function(fp, o) {  
	                                              if (o.result.data == 1) {  
	                                                  syswin.close(); //关闭窗口  
	                                                  Ext.example.msg("提示",o.result.message);
	                                                  store.reload();  
	                                              }else {  
	                                            	    Ext.example.msg("提示",o.result.message); 
	                                              }  
	                                          },  
	                                          failure: function() {  
	                                        	    Ext.example.msg("提示",o.result.message);
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
	                     }]  
	           });  
	    syswin.show();  
	    };
	    //************************************添加项目信息end************************************
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
								url : '/systemManager/importAppKeywords',
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