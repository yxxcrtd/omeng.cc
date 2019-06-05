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
<title>表单分类管理</title>
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

	var modelMenu = Ext.define("TreeModel", { // 定义树节点数据模型
			extend : "Ext.data.Model",
			fields :[{name : "id",type : "int"},
					{name : "text",type : "string"},
					{name : "demand",type : "string"},
					{name : "iconCls",type : "string"},
					{name : "icon",type : "string"},
					{name : "big_icon",type : "string"},
					{name : "status",type : "int"},
					{name : "flag",type : "int"},
					{name : "rank",type : "int"},
					{name : "link",type : "string"},
					{name : "is_close",type : "int"},
					{name : "leaf",type : "boolean"},
					{name : "leafVal",type : "int"}
					]
		});
	
	var store=Ext.create('Ext.data.TreeStore', {
		model : modelMenu,
		proxy: {
	         type: 'ajax',
	         url:'/slider/getOrderServiceTree',
	         reader: {
	             type: 'json',
	             root: 'data'
	         }
	     },
	     autoLoad: true
	});

	var mytree = Ext.create("Ext.tree.Panel",{
		region: 'center',
		border: false,
		store: store,
	    rootVisible: false,
	    renderTo: Ext.getBody(),
	    listeners:{ 
	    	'itemclick' : function(view, record, item, index, e) {
	    		var leaf = record.data.leaf;
	    		
	    			addMenuTab(record);
	    		
	    	},
	    	
	    	
	        "itemcontextmenu":function(menutree,record,items,index,e){ 
	        	if(record.data.leaf==false){  
	        		e.preventDefault();  
		            e.stopEvent();  
		            new Ext.menu.Menu({  
		                floating:true,  
		                items:[{ 
		                	iconCls:'icon-edit',
		                    text:'编辑此节点',  
		                    handler:function(){
		                    	showNodeForm(record,1);
		                    }  
		                },{  
		                	iconCls:'icon-ok',
		                    text:'添加根节点',  
		                    handler:function(){  
		                    	showNodeForm(record,3);
		                    }  
		                },{  
		                	iconCls:'icon-cancel',
		                    text:'发布此节点',  
		                    handler:function(){  
		                        releaseOrrecall(record,1);
		                    }  
		                },{  
		                	iconCls:'icon-cancel',
		                    text:'撤回此节点',  
		                    handler:function(){  
		                        releaseOrrecall(record,0);
		                    }  
		                },{  
		                	iconCls:'icon-add',
		                    text:'添加子节点',  
		                    handler:function(){  
		                    	showNodeForm(record,2);
		                    }
		                },{ 
		                	iconCls:'icon-del',
		                    text:'删除此节点',  
		                    handler:function(){  
		                    	delUserAll(record);
		                    	rightPanel.remove("leafNode"+record.data.id,true);
		                    }  
		                },{  
		                  	iconCls:'icon-refresh',
		                    text:'刷新分类',  
		                    handler:function(){  
		                        store.load({params:{node:'NaN'}});  
		                    }  
		                }]  
		                  
		            }).showAt(e.getXY());  
	            } else{
	            	
	            	e.preventDefault();  
		            e.stopEvent();  
		            new Ext.menu.Menu({  
		                floating:true,  
		                items:[{  
		                	iconCls:'icon-ok',
		                    text:'添加根节点',  
		                    handler:function(){  
		                    	showNodeForm(record,3);
		                    }  
		                },{ 
		                	iconCls:'icon-edit',
		                    text:'编辑此节点',  
		                    handler:function(){
		                    	showNodeForm(record,1);
		                    	rightPanel.remove("leafNode"+record.data.id,true);
		                    }  
		                },{ 
		                	iconCls:'icon-del',
		                    text:'删除此节点',  
		                    handler:function(){  
		                    	delUserAll(record);
		                    	rightPanel.remove("leafNode"+record.data.id,true);
		                    }  
		                },{  
		                	iconCls:'icon-cancel',
		                    text:'发布此节点',  
		                    handler:function(){  
		                        releaseOrrecall(record,1);
		                    }  
		                },{  
		                	iconCls:'icon-cancel',
		                    text:'撤回此节点',  
		                    handler:function(){  
		                        releaseOrrecall(record,0);
		                    }  
		                },{  
		                  	iconCls:'icon-refresh',
		                    text:'刷新分类',  
		                    handler:function(){  
		                        store.load({params:{node:'NaN'}});  
		                    }  
		                }]  
		                  
		            }).showAt(e.getXY()); 
	            	
	            	
	            }
	        }
	      }
	});

	//删除节点
	 function delUserAll(record)  
    {  
		var id= record.data.id;
           
         if(id == "")  
         {  
			 Ext.example.msg("提示","请选择要删除的节点！");
            return ;  
         }else{  
            Ext.Msg.confirm("提示信息","确定要执行删除节点吗?",function (btn){  
                if(btn == 'yes')  
                {  
                    Ext.Ajax.request({  
                        url:"/slider/deleteOrderCatalog",  
                        params:{id:id},  
                        method:'post',  
                        success:function(o){  
                       	 Ext.example.msg("提示",o.responseText);
                       	 store.load({params:{node:'NaN'}}); 
                            return ;  
                        },  
                        failure:function(o){ 
                       	 Ext.example.msg("提示",o.responseText);
                        }  
                    });    
                }  
            });  
         }  
    } ;


  //发布或撤回分类
	 function releaseOrrecall(record,status)  
    {  
		var id= record.data.id;
       var msg='';
       if(status==1){
       msg='发布';
       }else{
       msg='撤回';
       }    
         if(id == "")  
         {  
			 Ext.example.msg("提示","请选择分类！");
            return ;  
         }else{  
            Ext.Msg.confirm("提示信息","确定要"+msg+"分类吗?",function (btn){  
                if(btn == 'yes')  
                {  
                    Ext.Ajax.request({  
                        url:"/slider/releaseOrrecallOrderCatalog",  
                        params:{id:id,status:status},  
                        method:'post',  
                        success:function(o){  
                       	 Ext.example.msg("提示",o.responseText);
                       	 store.load({params:{node:'NaN'}}); 
                            return ;  
                        },  
                        failure:function(o){ 
                       	 Ext.example.msg("提示",o.responseText);
                        }  
                    });    
                }  
            });  
         }  
    } ;


  //form表单
    function showNodeForm(record,flag){ 
    	var smallImg = '';
    	var bigImg = '';
 		if(flag==1){
 			smallImg = record.data.icon;
 			bigImg = record.data.big_icon;
 		}
	  
    	    var nodeForm =  Ext.create('Ext.form.Panel', {  
    	                frame: true,   //frame属性  
    	                //title: 'Form Fields',  
    	                width: 420, 
    	                height:600,
    	                bodyPadding:4,  
    	                //renderTo:"panel21",  
    	                fieldDefaults: {  
 	                    labelAlign: 'left',  
 	                    labelWidth: 90,  
 	                    anchor: '100%'  
 	                }, 
    	                items: [{  
    	                    //显示文本框，相当于label  
    	                    xtype: 'displayfield',   
    	                    name: 'displayfield1',  
    	                    value: '节点信息'  
    	                     
    	                },{
    	    				xtype: 'hidden',
    	    				name: "id"
    	    			},{
    	    				xtype: 'hidden',
    	    				name: "parentid"
    	    			},{  
    	                    //项目名称
    	                    xtype: 'textfield', 
    	                    name: 'name',
    	                    allowBlank: false,
    	                    fieldLabel: '分类名称'  
    	                },{
    	    				xtype : 'numberfield',
    	    				name : 'rank',
    	    				fieldLabel : '排序',
    	    				allowDecimals: false,
    	    				minValue: 0,
    	    				maxValue: 1000
    	    			},{
    	                 	  //下拉列表框  
    	                       xtype: 'combobox', //9  
    	                       fieldLabel: '叶子',
    	                       displayField: 'value',
    	                       valueField : 'key',
    	                       name:'leaf',
    	                       allowBlank: false,
    	                       editable:false,
    	                       store: Ext.create('Ext.data.Store', {  
    	                           fields: [  
    	                             {name:'key'},{ name: 'value' }  
    	                             ],  
    	                                             data: [  
    	                             { 'key':'0','value': '不是' },  
    	                             { 'key':'1','value': '是' } 
    	                             ]  
    	                       }),
    	                    width:220
    	       			},{
  	                 	  //下拉列表框  
 	                       xtype: 'combobox', //9  
 	                       fieldLabel: '状态',
 	                       displayField: 'value',
 	                       valueField : 'key',
 	                       name:'is_close',
 	                       allowBlank: false,
 	                       editable:false,
 	                       store: Ext.create('Ext.data.Store', {  
 	                           fields: [  
 	                             {name:'key'},{ name: 'value' }  
 	                             ],  
 	                                             data: [  
 	                             { 'key':'0','value': '开放' },  
 	                             { 'key':'1','value': '关闭' } 
 	                             ]  
 	                       }),
 	                    width:220
 	       			},{
	                 	  //下拉列表框  
	                       xtype: 'combobox', //9  
	                       fieldLabel: '分类标识',
	                       displayField: 'value',
	                       valueField : 'key',
	                       name:'flag',
	                       allowBlank: false,
	                       editable:false,
	                       store: Ext.create('Ext.data.Store', {  
	                           fields: [  
	                             {name:'key'},{ name: 'value' }  
	                             ],  
	                                             data: [  
	                             { 'key':'0','value': '行业分类' },  
	                             { 'key':'1','value': '个性服务' },  
	                             { 'key':'2','value': '第三方服务' }  
	                             ]  
	                       }),
	                    width:220
	       			},{  
	                    //项目名称
	                    xtype: 'textfield', 
	                    name: 'link',
	                    fieldLabel: '链接地址'  
	                },{  
    	                    //项目名称
    	                    xtype: 'textareafield', 
    	                    name: 'demand',
    	                    fieldLabel: '分类描述'  
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
    	                          fieldLabel:'小图标',
    	                          name:'icon',
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
    	          	    xtype: 'box', //或者xtype: 'component',  
    	          	    width: 100, //图片宽度  
    	          	    height: 100, //图片高度  
    	          	    autoEl: {  
    	          	        tag: 'img',    //指定为img标签  
    	          	        src: smallImg    //指定url路径  
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
    	                      fieldLabel:'大图标',
    	                      name:'big_icon',
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
    	      	    xtype: 'box', //或者xtype: 'component',  
    	      	    width: 100, //图片宽度  
    	      	    height: 100, //图片高度  
    	      	    autoEl: {  
    	      	        tag: 'img',    //指定为img标签  
    	      	        src: bigImg    //指定url路径  
    	      	    }  
    	      	} ]  

    	            });  
    	    
    	    var title = '新增子节点';
    	    var reqName = 'addOrderCatalog';
    	   
    	    //================判断是编辑还是新增===============
    	    	if(flag==1){
    	    		// 是编辑状态
    	    		nodeForm.form.findField('id').setValue(record.data.id);
    	    	    nodeForm.form.findField('name').setValue(record.data.text);
    	    	    nodeForm.form.findField('demand').setValue(record.data.demand);
    	    	    
    	    	    nodeForm.form.findField('flag').setValue(record.data.flag+'');
    	    	    nodeForm.form.findField('link').setValue(record.data.link);
    	    	    nodeForm.form.findField('is_close').setValue(record.data.is_close+'');
    	    	    nodeForm.form.findField('rank').setValue(record.data.rank); 
     	    	    nodeForm.form.findField('leaf').setValue(record.data.leafVal+'');
    	    	    nodeForm.form.findField('icon').setValue(record.data.icon);
    	    	    nodeForm.form.findField('big_icon').setValue(record.data.big_icon);
    	        	title = '编辑节点';
    	        	reqName = 'editOrderCatalog';
    	    	}else if(flag==2){
    	    		nodeForm.form.findField('parentid').setValue(record.data.id);
    	    	}else if(flag==3){
    	    		title = '添加根节点';
    	        	reqName = 'addOrderRootCatalog';
    	    	}
    	    	

            //================判断是编辑还是新增===============
    	    //创建window面板，表单面板是依托window面板显示的  
    	    var syswin = Ext.create('Ext.window.Window',{  
    	              title : title,  
    	              width : 450, 
    	              height: 350,
    	              //height : 120,  
    	              //plain : true,  
    	              iconCls : "addicon",  
    	              // 不可以随意改变大小  
    	              resizable : false,  
    	              // 是否可以拖动  
    	              // draggable:false,  
    	              collapsible : false, // 允许缩放条  
    	              closeAction : 'close',  
    	              closable : true, 
    	              autoScroll: true,
    	              // 弹出模态窗体  
    	              modal : 'true',  
    	              buttonAlign : "center",  
    	              bodyStyle : "padding:0 0 0 0",  
    	              items : [nodeForm],  
    	              buttons : [{  
    	                         text : "保存",  
    	                         minWidth : 70,  
    	                         handler : function() {  
    	                            if (nodeForm.getForm().isValid()) {  
    	                                nodeForm.getForm().submit({  
    	                                          url :'/slider/'+reqName,  
    	                                           //等待时显示 等待  
    	                                          waitTitle: '请稍等...',  
    	                                          waitMsg: '正在提交信息...', 
    	                                          success: function(fp, o) {  
    	                                                  syswin.close(); //关闭窗口  
    	                                                  Ext.example.msg("提示","添加成功");
    	                                                  store.load({params:{node:'NaN'}}); 
    	                                          },  
    	                                          failure: function(fp, o) {  
    	                                        	    Ext.example.msg("提示",o.response.responseText);
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
//整体架构容器
Ext.create("Ext.container.Viewport", {
	layout : 'border',
	autoHeight: true,
	border: false,
	items : [mytree]
});

var addMenuTab=function(record) {
	var leaf = record.data.leaf;
	var id = record.data.id;
	var text = record.data.text;
	var status = record.data.status;
	var icon;
	if(status==0){
	icon='recall'
	}else{
	icon='release'
	}
	var leafPannel = Ext.getCmp("leafNode"+id);
	
		 if(leafPannel==undefined){
			 var panel = Ext.create('Ext.panel.Panel',{
			 id : "leafNode"+id,
			 title : text,
			 closable : true,
			 iconCls : icon,
			 html : '<iframe width="100%" height="100%" frameborder="0" src="/slider/orderCatalogServiceIndex?catalogid='+id+'"></iframe>'
			 });
			rightPanel.add(panel);
			rightPanel.setActiveTab(panel);
				 }else{
			rightPanel.setActiveTab(leafPannel);
				 }

	}


var rightPanel = Ext.create('Ext.tab.Panel', {
	activeTab : 0,
	border : false,
	autoScroll : true,
	//iconCls:'Applicationviewlist',
	region : 'center',
	items : []
});


Ext.create("Ext.container.Viewport", {
	layout : 'border',
	items : [ {
		layout : 'border',
		title : '分类',
		id : 'layout-browser',
		region : 'west',
		border : false,
		split : true,
		margins : '2 0 5 5',
		width : 200,
		minSize : 100,
		maxSize : 500,
		autoScroll : false,
		collapsible : true, // 是否折叠
		//iconCls : "Application",
		items : [ mytree ]
	},
	   rightPanel
	]
	
});

















});
</script>

</head>
<body>
</body>
</html>