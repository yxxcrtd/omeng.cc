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
<title>服务编辑器管理</title>
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


var grid;
var store;
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'编号',dataIndex:'id',hidden:true,align:'center'},
                   {header:'标题',dataIndex:'title',align:'center'},
                   {header:'分享标题',dataIndex:'share_title',align:'center'},
                   {header:'摘要',dataIndex:'share_abstract',align:'center'},
                   {header:'内容类型',dataIndex:'cont_type',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>消费引导</span>";  
                       } else{  
                           return "<span style='color:green;font-weight:bold';></span>";  
                       }
           	       }},
                   {header:'服务项目',dataIndex:'service_name',align:'center'},
                   {header:'发布状态',dataIndex:'is_pub',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:red;font-weight:bold';>未发布</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>已发布</span>";  
                       }
           	       }},
                  
                   {header:'加入时间',dataIndex:'create_time',align:'center'},
                   {header:'页面url',dataIndex:'url',align:'center'},
                   {header: '操作',  dataIndex: '',  sortable:true,align:'center',fixed:false,renderer:function(value,v,r){
                	   var id = r.data.id;
                	   var title = r.data.title;
                	   return '<a href="javascript:editHtml(\''+id+'\',\''+title+'\')"><span style="color:green;font-weight:bold";>编辑</span></a>'; 
                 	  
                    }}
               ];
	//创建store数据源
    
    store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/editHtml/getEditHtmlList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'title'},  
           {name:'create_time'},
           {name:'url'},
           {name:'share_title'},
           {name:'is_pub'},
           {name:'share_abstract'},
           {name:'cont_type'},
           {name:'service_name'}
           
        ]  
    });

    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
		var title = Ext.getCmp('title').getValue();
		var service_name = Ext.getCmp('service_name').getValue();
        var new_params = {title:title,service_name:service_name};    
        Ext.apply(store.proxy.extraParams, new_params);    
    });
    var sm = Ext.create('Ext.selection.CheckboxModel');
    grid = Ext.create("Ext.grid.Panel",{
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
                 <cms:havePerm url='/editHtml/addEditHtml'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },'-',</cms:havePerm>
        	       <cms:havePerm url='/editHtml/deleteEditHtml'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delRecordAll();
        	    		   }
        	      }},'-',</cms:havePerm>
        	      <cms:havePerm url='/editHtml/cancelEditHtml'>
                  { xtype: 'button', id:'cancel', text: '撤回',iconCls:'NewDelete',
       	    	   listeners: {
       	    		   click:function(){
       	    			   cancelRecordAll();
       	    		   }
       	      }},'-',</cms:havePerm>
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
                { xtype: 'textfield',id:'title',name: 'title',fieldLabel: '标题',labelAlign:'left',labelWidth:70},'-',
                { xtype: 'textfield',id:'service_name',name: 'service_name',fieldLabel: '服务项目',labelAlign:'left',labelWidth:70},'-',
                <cms:havePerm url='/editHtml/getEditHtmlList'>
				   { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var title = Ext.getCmp('title').getValue();
    	    			   var service_name = Ext.getCmp('service_name').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,title:title,service_name:service_name}}); 
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
   
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    <cms:havePerm url='/editHtml/editEditHtml'>
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data);
    });</cms:havePerm>

    
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	
	//删除
	 function delRecordAll()  
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
                         url:"/editHtml/deleteEditHtml",  
                         params:{id:id},  
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
          }  
     } ;

   //删除
	 function cancelRecordAll()  
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
			 Ext.example.msg("提示","请选择要撤回的对象！");
             return ;  
          }else{  
             Ext.Msg.confirm("提示信息","请确定要执行撤回操作吗?",function (btn){  
                 if(btn == 'yes')  
                 {  
                     Ext.Ajax.request({  
                         url:"/editHtml/cancelRecordAll",  
                         params:{id:id},  
                         method:'post',  
                         success:function(o){  
                        	 Ext.example.msg("提示","撤回成功！");
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                        	 Ext.example.msg("提示","撤回失败！");
                         }  
                     });    
                 }  
             });  
          }  
     } ; 
   
 	var showform=function(d){
 		var pic = '';
 		var rows = grid.getSelectionModel().getSelection();
 		if(d!=null&&d.id!=null&&d.id!=0){
 			pic =rows[0].raw.image;
 		}
 	    var add_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 400, 
 	                height:670,
 	                // autoScroll: true, 
 	                bodyPadding:5,
 	                //renderTo:"panel21",  
 	                fieldDefaults: {  
 	                    labelAlign: 'left',  
 	                    labelWidth: 90,  
 	                    anchor: '90%'  
 	                },  
 	                items: [{
	    				xtype: 'hidden',
	    				name: "id"
	    			},{  
   	                	xtype: 'textfield',   
		                name: 'title',  
		                fieldLabel: '标题'  
	                },{  
   	                	xtype: 'textfield',   
		                name: 'share_title',  
		                fieldLabel: '分享标题'  
	                },{  
   	                	xtype: 'textfield',   
		                name: 'share_abstract',  
		                fieldLabel: '摘要'  
	                },{
                  	    //内容类型
                        xtype: 'combobox', //9  
                        fieldLabel: '内容类型',
                        displayField: 'value',
                        valueField : 'key',
                        name:'cont_type',
                        editable:false,
                        allowBlank	:false,
                        store: Ext.create('Ext.data.Store', {  
                            fields: [  
                              {name:'key'},{ name: 'value' }  
                              ],  
                              data: [  
                              { 'key':'1','value': '消费引导' }
                              ]  
                        })
        			},{
        				// 
        				xtype : 'textfield',
        				name : 'business_id',
        				hidden:true
        			
        			},{  
   	                	xtype: 'textfield',   
		                name: 'url',  
		                fieldLabel: '页面url',
		                readOnly:true
	                },{
                        xtype: "triggerfield",
                        name: "service_name",
                        fieldLabel: "服务名称",
                        editable:false,
                        hideTrigger: false,
                        allowBlank	:false,
                        onTriggerClick: function () {
                            addService(add_winForm);
                           
                        }
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
             	                    value: '图片尺寸不得超过216*252px'
             	                    }]
            	              }]
                    },{
            			xtype: 'hidden',
            			name: "image"
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
 	    
	    var title = '新增编辑页';
	    var reqName = 'addEditHtml';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    		
	    	    add_winForm.form.findField('id').setValue(rows[0].get("id"));
	    	    add_winForm.form.findField('title').setValue(rows[0].get("title"));  
	    	    add_winForm.form.findField('share_title').setValue(rows[0].get("share_title"));
	    	    add_winForm.form.findField('share_abstract').setValue(rows[0].get("share_abstract"));
	    	    add_winForm.form.findField('cont_type').setValue(rows[0].get("cont_type")+'');
	    	    add_winForm.form.findField('service_name').setValue(rows[0].get("service_name"));
	    	    add_winForm.form.findField('url').setValue(rows[0].get("url"));
	    	    add_winForm.form.findField('image').setValue(rows[0].raw.image);
	    	    add_winForm.form.findField('business_id').setValue(rows[0].raw.business_id);
	        	title = '编辑编辑页';
	        	reqName = 'editEditHtml';
	    	}else{
	    	add_winForm.form.findField('url').setVisible(false);	
	    	}
        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 470, 
 	              height: 320,
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
 	                                          url :'/editHtml/'+reqName,  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',  
 	                                          success: function(fp, o) {
 	                                        	 if (o.result== true) {  
 	                                        		  syswin.close(); //关闭窗口  
	                                             	  Ext.example.msg("提示","保存成功");
	                                                  store.reload();  
	                                              }else {  
	                                            	  Ext.example.msg("提示","服务项目重复，保存失败");
	                                              }  
 	                                          },  
 	                                          failure: function() {
 	                                          	 Ext.example.msg("提示","服务项目重复，保存失败");
 	                                          }  
 	                                       });  
 	                            } } 
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
					url : '/systemManager/getServiceType',
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
	            width: 600,
	            height:500,  
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
	                    	 add_RecForm.form.findField('business_id').setValue(rowsCata[0].get("id"));
	                    	 add_RecForm.form.findField('service_name').setValue(rowsCata[0].get("service_type_name"));
	                    
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
	 	    
 	    
 	    };
 	 
});


function editHtml(id,name){
	  if(name=='null'){
			name='';
		}
	  parent.addTab("edit_html_"+id,"页面【"+name+"】编辑","Fuwushangyunyingxinxiguanli","/maintain?id="+id); 	 
}

</script>

</head>
<body>
</body>
</html>