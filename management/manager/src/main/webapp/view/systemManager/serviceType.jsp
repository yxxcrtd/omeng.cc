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
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,hidden:true},
                  // {header:'服务类型ID',dataIndex:'service_type_id',sortable:true,fixed:false},
                   {header:'服务名称',dataIndex:'service_type_name',sortable:true,fixed:false},
                  // {header:'应用名称',dataIndex:'app_name',sortable:true,fixed:false},
                   //{header:'应用类型',dataIndex:'app_type',sortable:true,fixed:false},
                   {header:'showIcon',dataIndex:'showIcon',hidden:true},
                   {header:'orderIcon',dataIndex:'orderIcon',hidden:true},
                   //{header:'父级id',dataIndex:'parent_id',sortable:true,fixed:false},
                   {header:'发布状态',dataIndex:'is_pub',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>已发布</span>";  
                       } else{  
                           return "<span style='color:green;font-weight:bold';>未发布</span>";  
                   }}},
                   {header:'发单表单',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                       return '<a href="javascript:orderForm(\''+r.data.id+'\',\''+r.data.service_type_name+'\')">详情</a>';
             		}},
                    {header:'报价表单',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                        return '<a href="javascript:planForm(\''+r.data.id+'\',\''+r.data.service_type_name+'\')">详情</a>';
              		}}
               ];
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/systemManager/getServiceType',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           //{name:'service_type_id'},  
           {name:'service_type_name'},  
          // {name:'app_name'},
           //{name:'app_type'},
           {name:'is_pub'},
           {name:'showIcon'},
           {name:'orderIcon'}
         //  {name:'parent_id'}
        ]  
    });
    
    
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	// var app_name=Ext.getCmp('app_name').getValue();
		// var app_type=Ext.getCmp('app_type').getValue();
		 var service_type_name=Ext.getCmp('service_type_name').getValue();
         var new_params = {service_type_name:service_type_name};    
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
	    	   <cms:havePerm url='/systemManager/addServiceType'>
	           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
		    	   listeners: {
		    		   click:function(){
		    			   editform(null);
		    		   }
		    	   }},'-',
		       </cms:havePerm>
		       <cms:havePerm url='/systemManager/deleteServiceType'>
	           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
		    	   listeners: {
		    		   click:function(){
		    			   deleteRecord();
		    		   }
		       }},'-',
			   </cms:havePerm>
		       <cms:havePerm url='/common/flushServiceType'>
	           { xtype: 'button', id:'flush', text: '刷新',iconCls:'Refresh',
		    	   listeners: {
		    		   click:function(){
		    			   flushParam();
		    		   }
		       }},'-',
			   </cms:havePerm>
			   <cms:havePerm url='/defined/pubObjectTable'>
		       { xtype: 'button', id:'pub', text: '发布',iconCls:'Fabu',
			    	   listeners: {
			    		   click:function(){
			    			   pubObject(1);
			    		   }
			       }},'-',
				</cms:havePerm>
				<cms:havePerm url='/defined/pubObjectTable'>
			       { xtype: 'button', id:'unpub', text: '撤回',iconCls:'Chehui',
				    	   listeners: {
				    		   click:function(){
				    			   pubObject(0);
				    		   }
				    }},'->',
				</cms:havePerm> 

			   <cms:havePerm url='/defined/pubAllObjectTable'>
		           { xtype: 'button', id:'pubAll', text: '发布全部',iconCls:'Fabu',
			    	   listeners: {
			    		   click:function(){
			    			   pubAllObject(1);
			    		   }
			    	   }},'-',
				</cms:havePerm>
				<cms:havePerm url='/defined/pubAllObjectTable'>
			           { xtype: 'button', id:'unpubAll', text: '撤回全部',iconCls:'Chehui',
				    	   listeners: {
				    		   click:function(){
				    			   pubAllObject(0);
				    		   }
				    	   }}
				</cms:havePerm> 	    
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	     //  { xtype: 'textfield',id:'app_name',name: 'app_name',fieldLabel: '应用名称',labelAlign:'left',labelWidth:60},'-',
        	     //  { xtype: 'textfield',id:'app_type',name: 'app_type',fieldLabel: '应用类型',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'textfield',id:'service_type_name',name: 'service_type_name',fieldLabel: '服务类型',labelAlign:'left',labelWidth:70},'-',

        	       <cms:havePerm url='/systemManager/getServiceType'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			  // var app_name=Ext.getCmp('app_name').getValue();
    	    			  // var app_type=Ext.getCmp('app_type').getValue();
    	    			   var service_type_name=Ext.getCmp('service_type_name').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,service_type_name:service_type_name}}); 
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

    <cms:havePerm url='/systemManager/editServiceType'>
    grid.on("itemdblclick",function(grid, row){
    	editform(row.data); 
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
            url:"/common/flushServiceType",  
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
                     url:"/systemManager/deleteServiceType",  
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
    } 
	
	//选择性发布/撤回
	function pubObject(isPub)  
    {  
		var title = '【撤回】';
		if(isPub){
			title = '【发布】';
		}
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
           	 Ext.example.msg("提示","请选择要"+title+"的对象！");
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
         Ext.Msg.confirm("提示信息","请确定要执行"+title+"操作吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/defined/pubObjectTable",  
                     params:{ids:ids,isPub:isPub},  
                     method:'post',  
                     success:function(o){  
                    	 Ext.example.msg("提示",title+"成功！");
                         store.reload();  
                         return ;  
                     },  
                     failure:function(form,action){ 
                     	 Ext.example.msg("提示",title+"失败！");
                     }  
                 });    
             }  
         });  
    } 
    
	//全部发布/撤回
	function pubAllObject(isPub)  
    {  
		var title = '【全部撤回】';
		if(isPub){
			title = '【全部发布】';
		}
         Ext.Msg.confirm("提示信息","请确定要执行"+title+"操作吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/defined/pubAllObjectTable",  
                     params:{isPub:isPub},  
                     method:'post',  
                     success:function(o){  
                    	 Ext.example.msg("提示",title+"成功！");
                         store.reload();  
                         return ;  
                     },  
                     failure:function(form,action){ 
                     	 Ext.example.msg("提示",title+"失败！");
                     }  
                 });    
             }  
         });  
    } 
      
    
  //form表单
	var showform = function() {
		var add_winForm = Ext.create('Ext.form.Panel', {
			frame : true, //frame属性  
			//title: 'Form Fields',  
			width : 440,
			height : 460,
			bodyPadding : 5, 
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%'
			},
			items : [ {
				// 字典记录主键
				xtype : 'textfield',
				name : 'service_type_name',
				allowBlank	:false,
				fieldLabel : '服务类型名称'
			}]

		});

		//创建window面板，表单面板是依托window面板显示的  
		var syswin = Ext.create('Ext.window.Window', {
			title : "新增服务类型",
			width : 450,
			height : 450,
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
							url : '/systemManager/addServiceType',
							//等待时显示 等待  
							waitTitle : '请稍等...',
							waitMsg : '正在提交信息...',

							success : function(fp, o) {
								    Ext.example.msg("提示","保存成功！"); 
									syswin.close(); //关闭窗口  
									store.reload();
							
							},
							failure : function() {
								Ext.example.msg("提示","保存失败！"); 
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
			} ]
		});
		syswin.show();
	};
	
    // 是否叶子节点
    var leavesComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['否','0'],
    	      ['是','1']
    	]
    });
    
	//form表单编辑用户
	var editform=function(d){
 		var showIconImg = '';
 		var orderIconImg = '';
 		if(d!=null&&d.id!=null&&d.id!=0){
 			showIconImg = d.showIcon;
 			orderIconImg = d.orderIcon;
 		}
	    var edit_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 440,
	                height:450,
	                bodyPadding:5,  
	                //autoScroll: true, 
	                //renderTo:"panel21",  
	                fieldDefaults: {  
	                    labelAlign: 'left',  
	                    labelWidth: 90,  
	                    anchor: '100%'  
	                },  
	                items: [ {
	    				// id
	    				xtype : 'textfield',
	    				name : 'id',
	    				hidden	:true
	    			}, {
				// 服务类型名称
				xtype : 'textfield',
				name : 'service_type_name',
				allowBlank	:false,
				fieldLabel : '服务类型名称'
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
                    fieldLabel:'showIcon',
                    name:'showIconUpload',
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
    	        src: showIconImg    //指定url路径  
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
                fieldLabel:'orderIcon',
                name:'orderIconUpload',
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
	        src: orderIconImg    //指定url路径  
	    }  
	}
   ]  
	            });  
	    //创建window面板，表单面板是依托window面板显示的  
	    
	    var title = '新增服务类型';
	    var reqName = 'addServiceType';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    		edit_winForm.form.findField('id').setValue(d.id);
                edit_winForm.form.findField('service_type_name').setValue(d.service_type_name);
	        	title = '编辑服务类型';
	        	reqName = 'editServiceType';
	    	}
	

        //================判断是编辑还是新增===============
	    var editwindow = Ext.create('Ext.window.Window',{  
	              title : title,  
	              width: 470,
	              height:440,  
	              autoScroll: true, 
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
	                                          url :'/systemManager/'+reqName,  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
	                                          success: function(fp, o) {  
	                                              if (o.result== true) { 
	                                            	  Ext.example.msg("提示","保存成功！");
	                                                  editwindow.close(); //关闭窗口  
	                                                  store.reload();
	                                                  grid.getSelectionModel().clearSelections();
	                                              }else {  
	                                            	  Ext.example.msg("提示","保存失败！");
	                                              }  
	                                          },  
	                                          failure: function() {  
	                                         	  Ext.example.msg("提示","保存失败！");
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

function orderForm(id,name){
	  if(name=='null'){
			name='';
		}
	  parent.addTab("object_table_"+id,"【"+name+"】发单表单","Fuwushangyunyingxinxiguanli","/defined/objectModelShow?object_id="+id); 	 
}

function planForm(id,name){
	  if(name=='null'){
			name='';
		}
	  parent.addTab("plan_table_"+id,"【"+name+"】报价表单","Fuwushangyunyingxinxiguanli","/defined/planModelShow?object_id="+id); 	 
}

</script>
</head>
<body>
</body>
</html>