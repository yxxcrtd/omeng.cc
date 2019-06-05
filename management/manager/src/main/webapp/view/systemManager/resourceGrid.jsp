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
<title>资源管理</title>
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
                   {header:'资源ID',dataIndex:'id',sortable:true,width:30,fixed:false},
                   {header:'父节点ID',dataIndex:'fatherId',width:30,sortable:true,fixed:false},
                   {header:'排序',dataIndex:'rank',sortable:true,width:30,fixed:false},
                   {header:'资源名称',dataIndex:'resName',sortable:true,fixed:false},
                   {header:'资源类型',dataIndex:'type',sortable:true,width:30,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>查询</span>";  
                       }else if(value=='2'){
                    	   return "<span style='color:green;font-weight:bold';>新增</span>"; 
                       }else if(value=='3'){
                    	   return "<span style='color:green;font-weight:bold';>更新</span>"; 
                       }else if(value=='4'){
                    	   return "<span style='color:green;font-weight:bold';>删除</span>"; 
                       }else if(value=='5'){
                    	   return "<span style='color:green;font-weight:bold';>上传</span>"; 
                       }else if(value=='6'){
                    	   return "<span style='color:green;font-weight:bold';>下载</span>"; 
                       }else if(value=='7'){
                    	   return "<span style='color:green;font-weight:bold';>导出</span>"; 
                       }else if(value=='8'){
                    	   return "<span style='color:green;font-weight:bold';>导入</span>"; 
                       }else{
                    	   return "<span style='color:green;font-weight:bold';>展示</span>"; 
                       }
           		   }},
                   {header:'状态',dataIndex:'disabled',sortable:true,width:30,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>禁用</span>";  
                       }else{
                    	   return "<span style='color:green;font-weight:bold';>启用</span>"; 
                       }
           		   }},
                   {header:'叶节点',dataIndex:'isLeaf',sortable:true,width:30,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>是</span>";  
                       }else{
                    	   return "<span style='color:green;font-weight:bold';>否</span>"; 
                       }
           		   }},
                   {header:'通用资源',dataIndex:'isCommon',sortable:true,width:30,fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>是</span>";  
                       }else{
                    	   return "<span style='color:green;font-weight:bold';>否</span>"; 
                       }
           		   }},
                   {header:'资源链接',dataIndex:'linkPath',sortable:true,fixed:false}
               ];
    var typeComboStore = new Ext.data.SimpleStore({
    	fields:['typeName','type'],
    	data:[['全部',''],
    	      ['展示','0'],
    	      ['查询','1'],
    	      ['新增','2'],
    	      ['更新','3'],
    	      ['删除','4'],    
    	      ['上传','5'],
    	      ['下载','6'],
    	      ['导出','7'],
              ['导入','8']
    	]
    });
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true, //设置属性进行请求后台排序
        proxy:{  
            type:'ajax',  
            url:'/systemManager/resourceList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'resName'},  
           {name:'disabled'},  
           {name:'linkPath'},
           {name:'type'},
           {name:'fatherId'},
           {name:'rank'},
           {name:'isLeaf'},
           {name:'isCommon'},
           {name:'remark'}
        ]  
    });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var resName=Ext.getCmp('resName').getValue();
		 var linkPath=Ext.getCmp('linkPath').getValue();
		 var type=Ext.getCmp('type').getValue();
		 var fatherId=Ext.getCmp('fatherId').getValue();
         var new_params = { resName:resName,linkPath:linkPath,type:type,fatherId:fatherId};    
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
	       <cms:havePerm url='/common/flushSystemResource'>
          { xtype: 'button', id:'rush', text: '刷新资源',iconCls:'Refresh',
            listeners: {
               click:function(){
	                flushResource();
           }
          }},'-',
	       </cms:havePerm> 
	       <cms:havePerm url='/systemManager/addResource'>
           { xtype: 'button', id:'edit', text: '添加',iconCls:'NewAdd',
	    	   listeners: {
	    		   click:function(){
	    			   showform(null);
	    		   }
	    	   }},'-',
	       </cms:havePerm>  
	       <cms:havePerm url='/systemManager/deleteResource'>
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
        	items:[
        	       { xtype: 'textfield',id:'resName',name: 'resName',fieldLabel: '资源名称',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'textfield',id:'linkPath',name: 'linkPath',fieldLabel: '资源链接',labelAlign:'left',labelWidth:60},'-',
       	          '资源类型:',
     	          {
					xtype:'combo',
					id:'type',
					store : typeComboStore,
		    		width:100,
		    		name:'type',
		   			triggerAction: 'all',
		   			displayField: 'typeName',
		   			valueField: 'type',
		   			value:'',
					mode:'local'
				   },'-', 
				   { xtype: 'textfield',id:'fatherId',name: 'fatherId',fieldLabel: '父节点ID',labelAlign:'left',labelWidth:60},'-',
        	       <cms:havePerm url='/systemManager/resourceList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var resName=Ext.getCmp('resName').getValue();
    	    			   var linkPath=Ext.getCmp('linkPath').getValue();
    	    			   var type=Ext.getCmp('type').getValue();
    	    			   var fatherId=Ext.getCmp('fatherId').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,resName:resName,linkPath:linkPath,type:type,fatherId:fatherId}}); 
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
    <cms:havePerm url='/systemManager/editResource'>
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
	
	//刷新资源
	function flushResource(){
        Ext.Ajax.request({  
            url:"/common/flushSystemResource",  
            method:'post',  
            success:function(o){   
                Ext.Msg.alert("提示信息","刷新成功!"); 
                store.reload();  
                return ;  
            },  
            failure:function(form,action){  
                Ext.Msg.alert("提示信息","刷新失败!");  
            }  
        });
	}  
	
	
	//删除系统资源
	function deleteRecord()  
    {  
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
             Ext.Msg.alert("提示信息","请选择要删除的对象");  
             return ;  
    	 }
         //ids：所有选中的用户Id的集合使用','隔开，初始化为空  
         var ids = '';  
         for(var i = 0;i<records.length;i++)  
         {  
             var disabled = records[i].get('disabled');	
             if(disabled!=1){
          	   //非启用
                 Ext.Msg.alert("提示信息","选择删除的记录必须是【禁用】数据");  
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
                     url:"/systemManager/deleteResource",  
                     params:{ids:ids},  
                     method:'post',  
                     success:function(o){   
                         Ext.Msg.alert("提示信息","删除成功!"); 
                         store.reload();  
                         return ;  
                     },  
                     failure:function(form,action){  
                         Ext.Msg.alert("提示信息","删除失败!");  
                     }  
                 });    
             }  
         });  
    } ;
    
    
    
    //************************************保存资源信息start************************************
	//form表单
	var comboStore = new Ext.data.SimpleStore({
	    	fields:['name','value'],
	    	data:[['启用','0'],
	    	      ['禁用','1']
	    	]
	    });
	var leafStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['否','0'],
    	      ['是','1']
    	]
    });
	var commonStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['否','0'],
    	      ['是','1']
    	]
    });
    var sTypeComboStore = new Ext.data.SimpleStore({
    	fields:['typeName','type'],
    	data:[['全部',''],
    	      ['展示','0'],
    	      ['查询','1'],
    	      ['新增','2'],
    	      ['更新','3'],
    	      ['删除','4'],    
    	      ['上传','5'],
    	      ['下载','6'],
    	      ['导出','7'],
              ['导入','8']
    	]
    });
	function showform(d){

	    var res_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 600, 
	                height:470,
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
	                    value: '系统资源信息'  
	                     
	                },{
	    				xtype: 'hidden',
	    				name: "id"
	    			},{  
	                    //资源名称
	                    xtype: 'textfield', 
	                    name: 'resName',
	                    allowBlank: false,
	                    fieldLabel: '资源名称'  
	                }, {  
	                    //资源链接 
	                    xtype: 'textfield', 
	                    name: 'linkPath', 
	                    allowBlank: false,
	                    fieldLabel: '资源链接'  
	                },{
						xtype:'combo',
						store : sTypeComboStore,
			    		width:80,
			    		name:'type',
			   			triggerAction: 'all',
			   			displayField: 'typeName',
			   			valueField: 'type',
			   			hiddenName:'type',
			   			value:'0',
						mode:'local',
						fieldLabel: '资源类型'  
					   }, {
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
						   //排序
		                    xtype: 'numberfield',  
		                    name: 'rank',   
		                    fieldLabel: '排序', 
		                    minValue: 1,  
		                    maxValue: 20  
		                },{  
		                    //父节点ID
		                    xtype: 'textfield', 
		                    name: 'fatherId',
		                    regex: /^[1-9]\d*$/,
		                    regexText:"父节点ID必须为正整数！", 
		                    fieldLabel: '父节点ID'  
		                }, {
							xtype:'combo',
							store : leafStore,
				    		width:80,
				    		name:'isLeaf',
				   			triggerAction: 'all',
				   			displayField: 'name',
				   			valueField: 'value',
				   			hiddenName:'isLeaf',
				   			value:'0',
							mode:'local',
						   	editable: false,
		    		    	allowBlank: false,
							fieldLabel: '叶节点'  
						   },{
								xtype:'combo',
								store : commonStore,
					    		width:80,
					    		name:'isCommon',
					   			triggerAction: 'all',
					   			displayField: 'name',
					   			valueField: 'value',
					   			hiddenName:'isCommon',
					   			value:'0',
								mode:'local',
							   	editable: false,
			    		    	allowBlank: false,
								fieldLabel: '公共资源'  
							},{  
	                	 xtype: 'textareafield',   
		                 name: 'remark',  
		                 fieldLabel: '备注',   
		                 value: '' 
	                }]  

	            });  
	    
	    var title = '新增资源';
	    var reqName = 'addResource';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    res_winForm.form.findField('id').setValue(d.id);
	    	    res_winForm.form.findField('resName').setValue(d.resName);  
	    	    res_winForm.form.findField('disabled').setValue(d.disabled+'');
	    	    res_winForm.form.findField('linkPath').setValue(d.linkPath); 
	    	    res_winForm.form.findField('rank').setValue(d.rank); 
	    	    res_winForm.form.findField('fatherId').setValue(d.fatherId); 
	    	    res_winForm.form.findField('type').setValue(d.type+''); 
	    	    res_winForm.form.findField('isLeaf').setValue(d.isLeaf+''); 
	     	    res_winForm.form.findField('isCommon').setValue(d.isCommon+''); 
	    	    res_winForm.form.findField('remark').setValue(d.remark); 
	        	title = '编辑资源';
	        	reqName = 'editResource';
	    	}
	

        //================判断是编辑还是新增===============
	    //创建window面板，表单面板是依托window面板显示的  
	    var syswin = Ext.create('Ext.window.Window',{  
	              title : title,  
	              width : 550, 
	              height: 420,
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
	                                res_winForm.getForm().submit({  
	                                          url :'/systemManager/'+reqName,  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
	                                            
	                                          success: function(fp, o) {  
	                                              //alert(o);success函数，成功提交后，根据返回信息判断情况  
	                                              if (o.result== true) {  
	                                                  syswin.close(); //关闭窗口  
	                                                  Ext.example.msg("提示","资源信息保存成功！");  
	                                                  store.reload();  
	                                              }else {  
	                                            	  Ext.example.msg("提示","资源信息保存异常！"); 
	                                              }  
	                                          },  
	                                          failure: function() {  
	                                          	  Ext.example.msg("提示","资源信息保存失败！"); 
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
	    //************************************保存资源信息end************************************
    
    
});



</script>
</head>
<body>
</body>
</html>