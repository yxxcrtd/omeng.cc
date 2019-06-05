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
<title>版本管理</title>
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
                   {xtype: 'rownumberer'},
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,hidden:true},
                   {header:'包名',dataIndex:'package_name',sortable:true,fixed:false},
                   {header:'版本号',dataIndex:'version',sortable:true,fixed:false},
                   {header:'包类型',dataIndex:'package_type',sortable:true,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>商户版</span>";  
                       } else{  
                           return "<span style='color:red;font-weight:bold';>用户版</span>";  
                       }
           		   }},
                   {header:'APP',dataIndex:'app_type',sortable:true,fixed:false},
                   {header:'升级类型',dataIndex:'update_type',sortable:true,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>提示升级</span>";  
                       } else{  
                           return "<span style='color:red;font-weight:bold';>强制升级</span>";  
                       }
           		   }},
                   {header:'发布状态',dataIndex:'publish_status',sortable:true,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>发布</span>";  
                       } else{  
                           return "<span style='color:red;font-weight:bold';>撤回</span>";  
                       }
           		   }},
                   {header:'渠道',dataIndex:'channel',sortable:true,fixed:false,hidden:true},
                   {header:'下载地址',dataIndex:'download_url',sortable:true,fixed:false},
                   {header:'升级信息',dataIndex:'detail',sortable:true,fixed:false}
               ];
	
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据
    	remoteSort: true, //设置属性进行请求后台排序
        proxy:{  
            type:'ajax',  
            url:'/terminal/clientVersionList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'id'  
            }  
        },  
        fields:[  
           {name:'id'}, 
           {name:'app_type'},  
           {name:'package_name'}, 
           {name:'version'},
           {name:'package_type'},  
           {name:'update_type'}, 
           {name:'publish_status'},
           {name:'channel'},  
           {name:'download_url'}, 
           {name:'detail'},  
           {name:'is_del'}
        ]  
    });
    
   	var packageTypeStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['全部','0'],
    	      ['商户版','1'],
    	      ['用户版','2'] 
    	]
    });
	var publishStatusStore = new Ext.data.SimpleStore({
	    fields:['name','value'],
	    data:[['全部','0'],
	          ['发布','1'],
	          ['撤回','2']
	]
    });
    
    
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {
    	var package_name=Ext.getCmp('package_name').getValue();
		var app_type=Ext.getCmp('app_type').getValue();
    	var package_type=Ext.getCmp('package_type').getValue();
		var publish_status=Ext.getCmp('publish_status').getValue();
        var new_params = {package_name:package_name,app_type:app_type,package_type:package_type,publish_status:publish_status};    
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

           { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
	    	   listeners: {
	    		   click:function(){
	    			   showform(null);
	    		   }
	       }},'-',

           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
	    	   listeners: {
	    		   click:function(){
	    			   delRecord();
	    		   }
	       }},'-',
           { xtype: 'button', id:'publish', text: '发布',iconCls:'Fabu',
	    	   listeners: {
	    		   click:function(){
	    			   publish(null);
	    		   }
	       }},'-',

           { xtype: 'button', id:'cancelPublish', text: '撤回',iconCls:'Chehui',
	    	   listeners: {
	    		   click:function(){
	    			   cancelPublish();
	    		   }
	       }}
           
          ],
      },
      {   
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'package_name',name: 'package_name',fieldLabel: '包名',labelAlign:'left',labelWidth:35},'-',
        	       { xtype: 'textfield',id:'app_type',name: 'app_type',fieldLabel: 'app类型',labelAlign:'left',labelWidth:55},'-',
        	       {
				               xtype:'combo',
				               id:'package_type',
				               store : packageTypeStore,
	    		               name:'package_type',
	   			               triggerAction: 'all',
	   			               hiddenName:'value',
	   			               displayField: 'name',
	   			               valueField: 'value',
	   			               value:'0',
				               mode:'local',
				   			   labelWidth:50,
				               fieldLabel: '包类型' 
			       },'-', 
        	       {
		                  xtype:'combo',
		                  id:'publish_status',
		                  store : publishStatusStore,
		                   name:'publish_status',
			               triggerAction: 'all',
			               hiddenName:'value',
			               displayField: 'name',
			               valueField: 'value',
			               value:'0',
			   		       labelWidth:60,
		                   mode:'local',
		                   fieldLabel: '发布状态' 
	              },'-',
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    		    	var package_name=Ext.getCmp('package_name').getValue();
    	    				var app_type=Ext.getCmp('app_type').getValue();
    	    		    	var package_type=Ext.getCmp('package_type').getValue();
    	    				var publish_status=Ext.getCmp('publish_status').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,package_name:package_name,app_type:app_type,package_type:package_type,publish_status:publish_status}}); 
    	    		   }
    	    		   }}

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
    
    // 双击grid记录，编辑项目信息

    grid.on("itemdblclick",function(grid, row){
    	showform(row.data);
    });

	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	
	//删除版本
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
             var publish_status = records[i].get('publish_status');	
             if(publish_status=='1'){
          	   //发布数据不可删
          	     Ext.example.msg("提示","选择删除的记录必须是【非发布】数据！");
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
                     url:"/terminal/deleteClientVersion",  
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
    
	//发布版本
	function publish()  
    {  
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
       		 Ext.example.msg("提示","请选择要发布的版本！");
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
         Ext.Msg.confirm("提示信息","确定发布选中的版本?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/terminal/publishClientVersion",  
                     params:{ids:ids},  
                     method:'post',  
                     success:function(o){ 
                   		 Ext.example.msg("提示","发布成功！");
                         store.reload();  
                         return ;  
                     },  
                     failure:function(form,action){ 
                   		 Ext.example.msg("提示","发布失败！");
                     }  
                 });    
             }  
         });  
    } ;
    
	//撤销发布版本
	function cancelPublish()  
    {  
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
       		 Ext.example.msg("提示","请选择要撤回发布的版本！");
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
         Ext.Msg.confirm("提示信息","确定撤回选中的版本?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/terminal/cancelPublishClientVersion",  
                     params:{ids:ids},  
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
    } ;
    
    //************************************保存客户端版本信息start************************************
	//form表单
    var fUpdateTypeStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[
    	      ['提示升级','1'],
    	      ['强制升级','2'] 
    	]
    });
   	var fPackageTypeStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[
    	      ['商户版','1'],
    	      ['用户版','2'] 
    	]
    });

 	var showform=function(d){
 	    var terminal_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 470, 
 	                height:450,
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
 	                    value: 'APP版本信息'  
 	                     
 	                }, {
	    				xtype: 'hidden',
	    				name: "id"
	    			},{
	    				xtype: 'hidden',
	    				name: "publish_status"
	    			},{    
 	                    xtype: 'textfield', 
 	                    name: 'package_name',  
 	                    fieldLabel: '包名',
 	                    allowBlank	:false
 	                },{
 	                	xtype:'combo',
					    store : fPackageTypeStore,
		    		    width:80,
		    		    name:'package_type',
		   			    triggerAction: 'all',
		   			    displayField: 'name',
		   			    valueField: 'value',
		   			    hiddenName:'value',
		   			    value:'1',
					    mode:'local',
						allowBlank	:false,
					    fieldLabel: '包类型' 
				   },{    
 	                    xtype: 'textfield', 
 	                    name: 'app_type',  
 	                    fieldLabel: 'app类型',
 	                    allowBlank	:false
 	                },{
 	                	xtype:'combo',
					    store : fUpdateTypeStore,
		    		    width:80,
		    		    name:'update_type',
		   			    triggerAction: 'all',
		   			    displayField: 'name',
		   			    valueField: 'value',
		   			    hiddenName:'value',
		   			    value:'1',
		   				allowBlank	:false,
					    mode:'local',
					    fieldLabel: '升级类型' 
				   },{  
 	   	                xtype: 'textfield',   
  		                name: 'version', 
  		  			    allowBlank	:false,
  		                fieldLabel: '版本号'
  	                },{  
	                	 xtype: 'textareafield',   
		                 name: 'detail',
		     			 allowBlank	:false,
		                 fieldLabel: '升级描述',   
		                 value: '' 
	                },{
               	     layout:'column',
		       		  border:false,
		              items:[{  
	                  layout:'form',
	                  anchor: '70%',
	         		  border:false,
	                  columnWidth:.48,
	                  items:[{ 
	                	    
		                    fieldLabel:'上传apk',
		                    name:'upload',
		                    xtype:'fileuploadfield',  
		                    anchor:'80%',  
		                    emptyText:'请选择安装包', 
		                    regex:/(apk)$/i,
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
		 	                    value: '填入【下载地址】可以不用上传apk'
		 	                    }]
			              }]
                 },{  
  	                    xtype: 'textfield',   
		                name: 'download_url',  
		                fieldLabel: '下载地址'
	               }
 	               ]  
 	            });  
 	    
	    var title = '新增版本';
	    var reqName = 'addClientVersion';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    terminal_winForm.form.findField('id').setValue(d.id);
	    	    terminal_winForm.form.findField('publish_status').setValue(d.publish_status);  
	    	    terminal_winForm.form.findField('package_name').setValue(d.package_name);
	    	    terminal_winForm.form.findField('package_type').setValue(d.package_type+'');
	    	    terminal_winForm.form.findField('app_type').setValue(d.app_type); 
	    	    terminal_winForm.form.findField('update_type').setValue(d.update_type+'');
	    	    terminal_winForm.form.findField('version').setValue(d.version); 
	    	    terminal_winForm.form.findField('download_url').setValue(d.download_url); 
	    	    terminal_winForm.form.findField('detail').setValue(d.detail); 
	        	title = '编辑版本';
	        	reqName = 'editClientVersion';
	    	}
	

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 500, 
 	              height: 450,
 	              //height : 120,  
 	              //plain : true,  
 	              iconCls : "addicon",  
 	              // 不可以随意改变大小  
 	              resizable : false,  
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
 	              items : [terminal_winForm],  
 	              buttons : [{  
 	                         text : "保存",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	if (terminal_winForm.getForm().isValid()) {
 	                        		terminal_winForm.getForm().submit({  
 	                                          url :'/terminal/'+reqName,  
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
 	    };
	    //************************************保存客户端版本信息end************************************
    
    
});



</script>
</head>
<body>
</body>
</html>