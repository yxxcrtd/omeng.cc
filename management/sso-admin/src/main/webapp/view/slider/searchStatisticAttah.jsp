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
<title>行业图标管理</title>
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
                   {header:'编号',dataIndex:'id',hidden:true,align:'center'},
                   {header:'行业名称',dataIndex:'name',align:'center'},
                   {header:'图片大小',dataIndex:'size',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>小图标</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:red;font-weight:bold';>大图标</span>";  
                       }
           		   }},
                   {header:'图标地址',dataIndex:'pics_path',align:'center'}
                   
               ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/slider/getSearchStatisticsAttch',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'}, 
           {name:'name'},
           {name:'size'},
           {name:'pics_path'}
        ]  
    });


    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {  
    	var name=Ext.getCmp('name').getValue();
        var new_params = {name:name};    
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
        	       <cms:havePerm url='/slider/editSearchStatisticsAttch'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },</cms:havePerm>
        	       <cms:havePerm url='/slider/deleteSearchStatisticsAttch'>
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
        	items:[ { xtype: 'textfield',id: 'name',name: 'name',fieldLabel: '行业名称',labelAlign:'left',labelWidth:60},'-',
			       
   	                 <cms:havePerm url='/slider/getSearchStatisticsAttch'>
        	       { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    		       var name=Ext.getCmp('name').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20}}); 
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
    
    <cms:havePerm url='/slider/editSearchStatisticsAttch'>
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
                         url:"/slider/deleteSearchStatisticsAttch",  
                         params:{id:id},  
                         method:'post',  
                         success:function(o){  
                        	 Ext.example.msg("提示","行业删除成功！");
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                        	 Ext.example.msg("提示","行业删除失败！");
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
 			pic =rows[0].get("pics_path");
 		}
 	    var add_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 470, 
 	                height:400,
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
 	                    value: '填写行业相关信息'  
 	                     
 	                }, {
	    				xtype: 'hidden',
	    				name: "id"
	    			},{
	    				xtype: 'hidden',
	    				name: "catalog_id"
	    			},{
                        xtype: "triggerfield",
                        name: "name",
                        fieldLabel: "行业名称",
                        editable:false,
                        hideTrigger: false,
                        onTriggerClick: function () {
                           
                           addCatalog(add_winForm);
                           
                        }
                    },
 	               {
 	                 	  //下拉列表框  
 	                       xtype: 'combobox', //9  
 	                       fieldLabel: '大小',
 	                       displayField: 'value',
 	                       valueField : 'key',
 	                       name:'size',
 	                       store: Ext.create('Ext.data.Store', {  
 	                           fields: [  
 	                             {name:'key'},{ name: 'value' }  
 	                             ],  
 	                                             data: [  
 	                             { 'key':'1','value': '小图标' },  
 	                             { 'key':'2','value': '大图标' } 
 	                             ]  
 	                       }),
 	                    width:220
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
			 	                    value: ' 图片大小10M以内，格式jpg、png'
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
                	        tag: 'img',    //指定为img标签  
                	        src: pic    //指定url路径  
                	    }  
                	} 
 	               ]  
 	            });  
 	    
	    var title = '新增行业图标';
	    var reqName = 'editSearchStatisticsAttch';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_winForm.form.findField('id').setValue(rows[0].get("id")); 
	    	    add_winForm.form.findField('name').setValue(rows[0].get("name"));
	    	    add_winForm.form.findField('catalog_id').setValue(rows[0].raw.catalog_id);
	    	    add_winForm.form.findField('size').setValue(rows[0].get("size")+'');
	    	    add_winForm.form.findField('pics_path').setValue(rows[0].get("pics_path")); 
	        	title = '编辑行业图标';
	        	reqName = 'editSearchStatisticsAttch';
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
 	    
 	   
 	 	 //***************************************添加行业  start*************************************
 	 	    
 		 	   var addCatalog = function(add_winForm){  
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
 		            width: 1200,
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
 		                    	add_winForm.form.findField('catalog_id').setValue(rowsCata[0].get("id"));
 		                    	add_winForm.form.findField('name').setValue(rowsCata[0].get("name"));
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
 	 
});
</script>

</head>
<body>
</body>
</html>