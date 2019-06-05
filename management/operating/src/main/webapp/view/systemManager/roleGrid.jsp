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
<title>角色管理</title>
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
                   {header:'角色ID',dataIndex:'id',sortable:true,fixed:false},
                   {header:'角色名称',dataIndex:'roleName',sortable:true,fixed:false},
                   {header:'状态',dataIndex:'disabled',sortable:true,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>禁用</span>";  
                       }else{
                    	   return "<span style='color:green;font-weight:bold';>启用</span>"; 
                       }
           		   }},
                   {header:'创建者',dataIndex:'createName',sortable:true,fixed:false},
                   {header:'创建时间',dataIndex:'createTime',sortable:true,fixed:false},
                   {header:'更新者',dataIndex:'updateName',sortable:true,fixed:false},
                   {header:'更新时间',dataIndex:'updateTime',sortable:true,fixed:false},
                   <cms:havePerm url='/systemManager/getTreeResourcesList'>
                   {
                       xtype:'actioncolumn', 
                       dataIndex:'id',
                       width:50,
                       align:'center',
                       header:'授权',                
                       items: [{
                    	   //icon : '/manager/view/ExtJS4.2/icons/cog.png',
                    	   iconCls:'Shouquan',
                    	   text: '授权',
                    	   align:'center',
                           handler: function(grid, rowIndex, colIndex) {
                        	   var record = grid.getStore().getAt(rowIndex); 
                       		   var roleId = record.get('id');
            				   var roleName=record.get('roleName');
            				   roleGrant(roleId,roleName);
                           }
                 }]}
                   </cms:havePerm>
               ];
	
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true, //设置属性进行请求后台排序
        proxy:{  
            type:'ajax',  
            url:'/systemManager/roleList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'roleName'},  
           {name:'disabled'},  
           {name:'createName'},
           {name:'createTime'},
           {name:'updateName'},
           {name:'updateTime'},
           {name:'remark'}
        ]  
    });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var roleName=Ext.getCmp('roleName').getValue();
 	    var disabled=Ext.getCmp('disabled').getValue();
        var new_params = { roleName:roleName,disabled:disabled };
        Ext.apply(store.proxy.extraParams, new_params);    
    });  
  
	var disComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['全部',''],
    	      ['启用','0'],
    	      ['禁用','1']
    	]
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
	       <cms:havePerm url='/systemManager/addRole'>
           { xtype: 'button', id:'edit', text: '添加',iconCls:'NewAdd',
	    	   listeners: {
	    		   click:function(){
	    			   showform(null);
	    		   }
	    	   }},'-',
	       </cms:havePerm>
	       <cms:havePerm url='/systemManager/deleteRole'>
           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
	    	   listeners: {
	    		   click:function(){
	    			   deleteRecord();
	    		   }
	    	   }}
           </cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'roleName',name: 'roleName',fieldLabel: '角色名称',labelAlign:'left',labelWidth:60},'-',
        	     '状态:',
           	    {
				xtype:'combo',
				id:'disabled',
				store : disComboStore,
	    		width:100,
	  
	    		name:'disabled',
	   			triggerAction: 'all',
	   			displayField: 'name',
	   			valueField: 'value',
	   			value:'',
				mode:'local'
			    },'-',
        	       <cms:havePerm url='/systemManager/roleList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var roleName=Ext.getCmp('roleName').getValue();
    	    			   var disabled=Ext.getCmp('disabled').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,roleName:roleName,disabled:disabled}}); 
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

    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    // 双击grid记录，编辑用户信息
    <cms:havePerm url='/systemManager/editRole'>
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
	
	
	//删除系统资源
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
             var disabled = records[i].get('disabled');	
             if(disabled!=1){
          	   //非启用
          	     Ext.example.msg("提示","选择删除的记录必须是【禁用】数据！");
                 return ;  
             }
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
                     url:"/systemManager/deleteRole",  
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
    
    
    
    //************************************保存角色信息start************************************
	//form表单
	var comboStore = new Ext.data.SimpleStore({
	    	fields:['name','value'],
	    	data:[['启用','0'],
	    	      ['禁用','1']
	    	]
	    });
	function showform(d){ 
	    var res_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 600, 
	                height:400,
	                bodyPadding:5,  
	                autoScroll: true, 
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
	                    value: '系统角色信息'  
	                     
	                },{
	    				xtype: 'hidden',
	    				name: "id"
	    			},{  
	                    //资源名称
	                    xtype: 'textfield', 
	                    name: 'roleName',
	                    allowBlank: false,
	                    fieldLabel: '角色名称'  
	                },{
						xtype:'combo',
						store : comboStore,
			    		width:80,
			    		name:'disabled',
			   			triggerAction: 'all',
			   			displayField: 'name',
			   			valueField: 'value',
			   			hiddenName:'disabled',
			   			value:'0',
						mode:'local',
					   	editable: false,
	    		    	allowBlank: false,
						fieldLabel: '状态'  
					   },{  
	                	 xtype: 'textareafield',   
		                 name: 'remark',  
		                 fieldLabel: '备注',   
		                 value: '' 
	                }]  

	            });  
	    
	    var title = '新增角色';
	    var reqName = 'addRole';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    res_winForm.form.findField('id').setValue(d.id);
	    	    res_winForm.form.findField('roleName').setValue(d.roleName);
	     	    res_winForm.form.findField('disabled').setValue(d.disabled+'');
	    	    res_winForm.form.findField('remark').setValue(d.remark); 
	        	title = '编辑角色';
	        	reqName = 'editRole';
	    	}
	

        //================判断是编辑还是新增===============
	    //创建window面板，表单面板是依托window面板显示的  
	    var syswin = Ext.create('Ext.window.Window',{  
	              title : title,  
	              width : 550, 
	              height: 350,
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
	              layout:'fit',
	              autoScroll: true,  
	              buttonAlign : "center",  
	              bodyStyle : "padding:0 0 0 0",  
	              items : [res_winForm],  
	              buttons : [{  
	                         text : "保存",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            if (res_winForm.getForm().isValid()) {  
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
	    //************************************保存角色信息end************************************
    
	  
    
});



//************************************授权角色树start************************************ 
    	    function roleGrant(roleId,roleName){
    	    	Ext.QuickTips.init();
    	    	var store = Ext.create('Ext.data.TreeStore', {
	    	        model: 'Task',
	    	        proxy: {
	    	            type: 'ajax',
	    	            url: '/systemManager/getTreeResourcesList?roleId=' + roleId,
	    	        },
	    	        folderSort: true
	    	    });
	    	 //we want to setup a model and store instead of using dataUrl
	    	    Ext.define('Task', {
	    	        extend: 'Ext.data.Model',
	    	        fields : [
	      					{name : "text",type : "string"}]
	      		});

    	    	var tree = Ext.create('Ext.tree.Panel', {
    	            title: '【'+roleName+'】'+'角色权限',
    	            width: 490,
    	            height: 400,
    	            renderTo: Ext.getBody(),
    	            collapsible: true,
    	            autoScroll:true,
    	            useArrows: true,
    	            rootVisible: false,
    	            store: store,
    	            multiSelect: true,
    	            checkOnly:false,
    	            singleExpand: true,
    	            //the 'columns' property is now 'headers'
    	   /**         columns: [{
    	                xtype: 'treecolumn', //this is so we know which column will show the tree
    	                text: '资源名',
    	                width: 240,
    	                sortable: true,
    	                dataIndex: 'text'
    	            }
    	            ]**/
    	            //添加监听事件
    	            listeners: {
    	              checkchange: function(node, state) { 
    	              //如果被勾选的节点有子节点，则将其子节点全部改为根节点状态
    	                if (node.hasChildNodes()) {
    	                  for (var j = 0; j < node.childNodes.length; j++) {
    	                      node.childNodes[j].set('checked', state);
    	                  }
    	                }
    	              }
    	            }
    	        });
    	    	
    	  
    		 var rolewindow = Ext.create('Ext.window.Window',{  
	              title : "分配权限",  
	              width: 500,
	              height:500,  
	              //height : 120,  
	              //plain : true,  
	              iconCls : "addicon",  
	              // 不可以随意改变大小  
	              resizable : true, 
	              //autoScroll:true,
	              // 是否可以拖动  
	              // draggable:false,  
	              collapsible : true, // 允许缩放条  
	              closeAction : 'close',  
	              closable : true,  
	              // 弹出模态窗体  
	              modal : 'true',  
	              buttonAlign : "center",  
	              bodyStyle : "padding:0 0 0 0",  
	              items : [tree],
	              buttons : [{  
	                         text : "保存",  
	                         minWidth : 70,  
	                         handler : function() {  
	                        	var treerows= tree.getChecked( );
	                        	var resIds='';
	                        	for(var i = 0;i<treerows.length;i++)  
	                            {  
	                        		if(i==0){
	                        		 	   resIds = resIds+treerows[i].get('id');  
	                        		}else{
	                        			   resIds = resIds+','+treerows[i].get('id');  
	                        		}
	                            } 
	                        	Ext.MessageBox.wait('正在操作','请稍后...');
	                        	Ext.Ajax.request({  
	                                 url:"/systemManager/saveRoleResources",  
	                                 params:{roleId:roleId,resIds:resIds},  
	                                 method:'post', 
	                                 waitTitle: '请稍等...',  
                                     waitMsg: '正在提交信息...', 
	                                 success:function(o){  
	                                     rolewindow.close();
	                                 	 Ext.example.msg("提示","权限分配成功！");
	                                     store.reload();                    
	                                     return ;  
	                                 },  
	                                 failure:function(form,action){  
	                                 	 Ext.example.msg("提示","权限分配失败！");
	                                 } , 
	                                 callback:function(){
	                                	 Ext.MessageBox.hide();
	                                 }
	                             });    
	                            }  
	                          
	                     }, {  
	                         text : "关闭",  
	                         minWidth : 70,  
	                         handler : function() {  
	                        	 rolewindow.close();  
	                         }  
	                     }]  
	           });  
    		 rolewindow.show();  
    		
    	    };

//************************************授权角色树end************************************ 

</script>
</head>
<body>
</body>
</html>