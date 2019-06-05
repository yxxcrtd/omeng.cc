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
                   {header:'群组ID',dataIndex:'id',sortable:true,fixed:false},
                   {header:'群组名称',dataIndex:'groupName',sortable:true,fixed:false},
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
                   {
              		header: '授权',
           			width: 60,
           			renderer : function(value, metadata, record, rowIndex,colIndex) {
           				var groupId = record.data.id;
           				var groupName=record.data.groupName;
           				var str = '<button value="授权"  onclick="roleGrant('
           				+ groupId + ',\'' + groupName + '\')">&nbsp;</button>';
           				return str;
           			}
                   }
               ];
	
    var store = Ext.create("Ext.data.Store",{
    	pageSize:10, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/manager/systemManager/groupList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'id'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'groupName'},  
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
    	// var name=Ext.getCmp('merchant_name').getValue();
    	// var start_time=Ext.getCmp('start_time').getValue();
		// var off_time=Ext.getCmp('off_time').getValue();
		 //  var verification_status=Ext.getCmp('verification_status').getValue();
       // var new_params = { name:name,start_time : start_time,off_time : off_time,verification_status:verification_status };    
        var new_params = {};
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

           { xtype: 'button', id:'edit', text: '添加',iconCls:'Useredit',
	    	   listeners: {
	    		   click:function(){
	    			   showform(null);
	    			   // AuditAll();
	    		   }
	    	   }},'-',
           { xtype: 'button', id:'del', text: '删除',iconCls:'Userdelete',
	    	   listeners: {
	    		   click:function(){
	    			   deleteRecord();
	    		   }
	    	   }}
           
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'groupName',name: 'groupName',fieldLabel: '群组名称',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Usermagnify',listeners: {
    	    		   click:function(){
    	    			   var groupName=Ext.getCmp('groupName').getValue();
    	    			   store.load({params:{page:1,limit:10,groupName:groupName}}); 
    	    		   }
    	    		   }}]
        },{
            xtype: 'pagingtoolbar',
            store: store,   // GridPanel使用相同的数据源
            dock: 'bottom',
            displayInfo: true,
            plugins: Ext.create('Ext.ux.ProgressBarPager'),
            emptyMsg: "没有记录" //没有数据时显示信息
        }]
    });
  //加载数据  
    store.load({params:{start:0,limit:10}}); 

    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    // 双击grid记录，编辑用户信息
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
                     url:"/manager/systemManager/deleteGroup",  
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
    
    
    
    //************************************保存群组信息start************************************
	//form表单
	var comboStore = new Ext.data.SimpleStore({
	    	fields:['name','value'],
	    	data:[['启用','0'],
	    	      ['禁用','1']
	    	]
	    });
	function showform(d){
		var groupid=0;
		if(d!=null&&d.id!=null){
			groupid=d.id;
		}
		  var rolefromStore = Ext.create("Ext.data.Store",{
		    	pageSize:10, //每页显示几条数据  
		    	autoload:true,
		        proxy:{  
		            type:'ajax',  
		            url:'/manager/systemManager/getSelfRoles?grouId=' + groupid,  
		            reader:{  
		                type:'json',  
		                totalProperty:'total',  
		                root:'data',  
		                idProperty:'id'  
		            }  
		        },  
		        fields:[  
		           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
		           {'name':'roleName'}
		        ]  
		    });
		
//		rolefromStore.load();
		 var roletoStore = Ext.create("Ext.data.Store",{
		    	pageSize:10, //每页显示几条数据  
		    	autoload:true,
		        proxy:{  
		            type:'ajax',  
		            url:'/manager/systemManager/getOtherRoles?grouId=' + groupid,  
		            reader:{  
		                type:'json',  
		                totalProperty:'total',  
		                root:'data',  
		                idProperty:'id'  
		            }  
		        },  
		        fields:[  
		           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
		           {'name':'roleName'}
		        ]  
		    });
		
	//	roletoStore.load();
		
	    var res_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 800, 
	                height:700,
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
	                    value: '系统群组信息'  
	                     
	                },
	                {
	    				xtype:'fieldset',
	    				title: '基本信息',
	    				collapsible: true,
	    				autoHeight: true,
	    				anchor: '95%' ,
	    				items: [{
		    				xtype: 'hidden',
		    				name: "id"
		    			},{  
		                    //资源名称
		                    xtype: 'textfield', 
		                    name: 'groupName',
		                    allowBlank: false,
		                    fieldLabel: '群组名称'  
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
	    			},{
	    				bodyStyle: 'padding-left:5px;',
	    				items: {
	    					xtype:'fieldset',
	    					title: '分配角色',
	    					collapsible: true,
	    					autoHeight: true,
	    					anchor: '95%' ,
	    					items: [{			
	    						xtype: "itemselector",
	    						//id: 'itemselectorRoles'+itemid,
	    						name: "itemselectorRole",
	    						fieldLabel: "分配角色",
//	    						store:rolefromStore,
	    						 multiselects: [{
	    							 width: 150, 
	    							 height: 260,
	    							 legend:'可选条目',
	    							 store: rolefromStore,
	    							 displayField: 'id',
	    							 valueField: 'roleName'
	    						 },
	    						 {
	    							 width: 150, 
	    							 height: 260,
	    							 hideLabel:true,
	    							 egend:'已选条目',
	    							 store:roletoStore,
	    							 displayField: 'id',
	    							 valueField: 'roleName',
	    							tbar:[{
	    								text: '清空',
	    								iconCls:'remove',
	    								handler:function(b,e){
	    									 Ext.getCmp('selector_form').getForm().findField('itemselector').reset();
	    								}
	    							}]
	    					
	    						}]
	    					}]
	    				}
	    			}
                    ]  

	            });  
	    
	    var title = '新增群组';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    res_winForm.form.findField('id').setValue(d.id);
	    	    res_winForm.form.findField('groupName').setValue(d.groupName);
	    	    res_winForm.form.findField('disabled').setValue(d.disabled);  
	    	    res_winForm.form.findField('remark').setValue(d.remark); 
	        	title = '编辑群组';
	    	}
	

        //================判断是编辑还是新增===============
	    //创建window面板，表单面板是依托window面板显示的  
	    var syswin = Ext.create('Ext.window.Window',{  
	              title : title,  
	              width : 800, 
	              height: 700,
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
	                                          url :'/manager/systemManager/saveGroup',  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
	                                            
	                                          success: function(fp, o) {  
	                                              //alert(o);success函数，成功提交后，根据返回信息判断情况  
	                                              if (o.result.data == 1) {  
	                                                  syswin.close(); //关闭窗口  
	                                                  store.reload();  
	                                              }else {  
	                                            	  Ext.Msg.alert("提示信息",o.result.message);  
	                                              }  
	                                          },  
	                                          failure: function() {  
	                                        	    Ext.Msg.alert("提示信息",o.result.message);  
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
	    //************************************保存群组信息end************************************
	    
	    
	  
//************************************授权角色树end************************************ 
    
});

//************************************授权角色树start************************************ 
function roleGrant(groupId,groupName){
	Ext.QuickTips.init();
	var store = Ext.create('Ext.data.TreeStore', {
        model: 'Task',
        proxy: {
            type: 'ajax',
            url: '/manager/systemManager/getGroupRole?groupId=' + groupId,
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
        title: '【'+groupName+'】'+'角色权限',
        width: 548,
        height: 521,
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
        
    });
	

 var rolewindow = Ext.create('Ext.window.Window',{  
      title : "分配角色",  
      width: 560,
      height:600,  
      //height : 120,  
      //plain : true,  
      iconCls : "addicon",  
      // 不可以随意改变大小  
      resizable : true, 
      autoScroll:true,
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
                	var flag = true;
                	for(var i = 0;i<treerows.length;i++)  
                    {  
                		if(treerows[i].get('leaf')){
                    		if(flag==true){
                    		 	   resIds = resIds+treerows[i].get('id');  
                    		}else{
                    			   resIds = resIds+','+treerows[i].get('id');  
                    		}
                    		flag=false;
                		}
                    } 
                	Ext.MessageBox.wait('正在操作','请稍后...');
                	Ext.Ajax.request({  
                         url:"/manager/systemManager/saveGroupRoles",  
                         params:{groupId:groupId,resIds:resIds},  
                         method:'post', 
                         waitTitle: '请稍等...',  
                         waitMsg: '正在提交信息...', 
                         success:function(o){  
                             Ext.Msg.alert("提示信息","角色分配成功!");  
                             rolewindow.close();
                             //rolewindow.close();
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){  
                             Ext.Msg.alert("提示信息","角色分配失败!");  
                             //rolewindow.close();
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
