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
<title>用户管理</title>
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
	                   {header:'编号',dataIndex:'id',hidden: true},
	                   {header:'服务商名称',dataIndex:'merchants_name',width:150,align:'center'},	
	                   {header:'员工姓名',dataIndex:'name',align:'center'},
	                   {header:'注册号码',dataIndex:'phone',align:'center'},               
	                   {header:'员工类型',dataIndex:'employees_type',align:'center',renderer:function(value){  
	                	   if(value=='1'){  
	                           return "<span style='color:red;font-weight:bold';>老板</span>";  
	                       }else if(value=='3'){
	                    	   return "<span style='color:blue;font-weight:bold';>财务</span>";  
	                       }else{
	                    	   return "<span style='color:green;font-weight:bold';>普通员工</span>";  
	                       }
	           		   }},
	                   
	           	   {header:'加入时间',dataIndex:'join_time',align:'center',width:150},
	               ];
		
	    var store = Ext.create("Ext.data.Store",{
	    	pageSize:20, //每页显示几条数据  
	    	remoteSort: true,
	        proxy:{  
	            type:'ajax',  
	            url:'/merchants/getEmployeeList',  
	            reader:{  
	                type:'json',  
	                totalProperty:'total',  
	                root:'data',  
	                idProperty:'ee'  
	            }  
	        },  
	        fields:[  
	           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
	           {name:'name'},  
	           {name:'phone'},
	           {name:'merchants_name'}, 
	           {name:'employees_type'},  
	           {name:'verification_status'},
	           {name:'join_time'}
	        ]  
	    });
	    
	    var comboStore = new Ext.data.SimpleStore({
	    	fields:['employeeTypeName','employeeType'],
	    	data:[['全部',''],
	    	      ['老板','1'],
	    	      ['财务','3'],
	    	      ['普通员工','2']
	    	]
	    });	
	  //点击下一页时传递搜索框值到后台  
	    store.on('beforeload', function (store, options) {    
	    	 var name=Ext.getCmp('merchant_name').getValue();
	    	 var phone=Ext.getCmp('phone').getValue();
	    	 var start_time=Ext.getCmp('start_time').getValue();
			 var off_time=Ext.getCmp('off_time').getValue();
			 var employees_type=Ext.getCmp('employees_type').getValue();
			 var province='';
	 		 var city='';
	 		 var agentId='';
	 		 var merchantsid='';
	         var new_params = { name:name,start_time : start_time,off_time : off_time,phone:phone,province:province,city:city,agentId:agentId,merchantsid: merchantsid,employees_type:employees_type};    
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
	        	       '-',
	        	      
	        	    	   <cms:havePerm url='/merchants/deleteEmployee'>
	                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
	        	    	   listeners: {
	        	    		   click:function(){
	        	    			   delUserAll();
	        	    		   }
	        	    	   }},'-',</cms:havePerm>
	        	    	   <cms:havePerm url='/merchants/exportEmployeeExcel'>
		        	       { xtype: 'button', text: '导出',iconCls:'Daochu',
		            	    	   listeners: {
		            	    		   click:function(){
		            	    			   exportAll();
		            	    		   }
		            	    	   }
		                   }</cms:havePerm>
	                   
	                  ],
	        },{
	        	xtype:'toolbar',
	        	dock:'top',
	        	displayInfo: true,
	        	items:[{ xtype: 'textfield',id:'merchant_name',name: 'merchant_name',fieldLabel: '服务商名称',labelAlign:'left',labelWidth:70},'-',
	        	       { xtype: 'textfield',id:'phone',name: 'phone',fieldLabel: '注册号码',labelAlign:'left',labelWidth:70}, '-',
	        	       {xtype : 'combobox',id : 'employees_type',name : 'employees_type',fieldLabel : '员工类型',editable:false,valueField : 'employeeType',value:'',displayField : 'employeeTypeName',width : 250,
							store : comboStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
	        	       { xtype: 'datetimefield',id: 'start_time',name: 'start_time',format : 'Y-m-d',fieldLabel: '加入时间',value:'${start_time}',labelAlign:'left',labelWidth:60},'～',
                       { xtype: 'datetimefield',id: 'off_time',name: 'off_time',format : 'Y-m-d',labelAlign:'left',labelWidth:60},'-',       	      
	  	                  <cms:havePerm url='/merchants/getEmployeeList'>
	        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
	    	    		   click:function(){
	    	    			   var name=Ext.getCmp('merchant_name').getValue();
	    	    			   var start_time=Ext.getCmp('start_time').getValue();
	    	    			   var off_time=Ext.getCmp('off_time').getValue();
	    	    			   var employees_type=Ext.getCmp('employees_type').getValue();
	    	    			   var province='';
	    	    		 	   var city='';
	    	    		 	   var agentId='';
	    	    		 	   var merchantsid='';
	    	    			   var phone=Ext.getCmp('phone').getValue();
	    	    			   store.currentPage = 1;
	    	    			   store.load({params:{start:0,page:1,limit:20,name:name,start_time : start_time,off_time : off_time,phone:phone,
	    	    				   province:province,city:city,agentId:agentId,merchantsid:merchantsid,employees_type:employees_type}}); 
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
	    <cms:havePerm url='/merchants/saveEmployee'>
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
		
		
		//员工删除
		 function delUserAll()  
	    {  
	         //grid中复选框被选中的项  
	           
	         var rows = grid.getSelectionModel().getSelection();  
	         //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
	         var user_id = '';  
	         for(var i = 0;i<rows.length;i++)  
	         {  
	            if(i>0)  
	            {  
	                user_id = user_id+','+rows[i].get('id');  
	            }else{  
	                user_id = user_id+rows[i].get('id');  
	            }
	            if(rows[i].get('employees_type')==1){
		        	 Ext.example.msg("提示","老板不允许删除！");
		             return ;  
		         }
	         }  
	         //没有选择要执行操作的对象  
	           
	         if(user_id == "")  
	         {  
	             Ext.example.msg("提示","请选择要删除的对象！");
	             return ;  
	         }else{  
	            Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
	                if(btn == 'yes')  
	                {  
	                    Ext.Ajax.request({  
	                        url:"/merchants/deleteEmployee",  
	                        params:{id:user_id},  
	                        method:'post',  
	                        success:function(o){   
	                            Ext.example.msg("提示","员工删除成功！");
	                            store.reload();  
	                            return ;  
	                        },  
	                        failure:function(form,action){  
	                            Ext.example.msg("提示","员工删除失败！");
	                        }  
	                    });    
	                }  
	            });  
	         }  
	    } ;
	    //导出所有员工
		function exportAll() {
			var name=Ext.getCmp('merchant_name').getValue();
			   var start_time=Ext.getCmp('start_time').getValue();
			   var off_time=Ext.getCmp('off_time').getValue();
			   var employees_type=Ext.getCmp('employees_type').getValue();
			   var province='';
		 	   var city='';
		 	   var agentId='';
		 	   var merchantsid='';
			   var phone=Ext.getCmp('phone').getValue();
			window.location.href = '/merchants/exportEmployeeExcel?name='+name+'&start_time='+start_time+'&off_time='+off_time+'&employees_type='+employees_type
					+'&province='+province+'&city='+city+'&agentId='+agentId+'&merchantsid='+merchantsid+'&phone'+phone;
		};
		
	    function AuditAll()  {
	    	var rows = grid.getSelectionModel().getSelection();  
	        //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
	        var user_id = '';  
	        for(var i = 0;i<rows.length;i++)  
	        {  
	           if(i>0)  
	           {  
	               user_id = user_id+','+rows[i].get('id'); 
	               if(rows[i].get('verification_status')==1){
	                   Ext.example.msg("提示","员工已审核！");
	            	   return ; 
	   	        }
	           }else{  
	               user_id = user_id+rows[i].get('id'); 
	               if(rows[i].get('verification_status')==1){
	                   Ext.example.msg("提示","员工已审核！");
	            	   return ;
	           }  }
	        } 
	        
	        
	        Ext.Msg.confirm("提示信息","请确定要审核操作吗?",function (btn){  
                if(btn == 'yes')  
                {  
	        Ext.Ajax.request({  
	            url:"/merchants/AuditAllEmployee",  
	            params:{id:user_id},  
	            method:'post',  
	            success:function(o){  
	                Ext.example.msg("提示","审核成功！");
	                store.reload();  
	                return ;  
	            },  
	            failure:function(form,action){  
	                Ext.example.msg("提示","审核失败！");
	            }  
	        });
                }
                }) 
	    };
	    
	  //************************************编辑员工信息start************************************
		//form表单
		var comboStore = new Ext.data.SimpleStore({
		    	fields:['name','value'],
		    	data:[['老板','1'],
		    	      ['普通员工','2'],
		    	      ['财务','3']
		    	]
		    });
		function showform(d){

		    var em_winForm =  Ext.create('Ext.form.Panel', {  
		                frame: true,   //frame属性  
		                //title: 'Form Fields',  
		                width: 600, 
		                height:400,
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
		                    value: '员工基本信息'  
		                     
		                },{
		    				xtype: 'hidden',
		    				name: "id"
		    			},{  
		                    //员工姓名
		                    xtype: 'textfield', 
		                    name: 'employeeName',
		                    fieldLabel: '员工姓名'  
		                }, {  
		                    //手机号
		                    xtype: 'textfield', 
		                    name: 'employeePhone', 
		                    allowBlank: false,
		                    readOnly:true,
		                    regex: /^(1[3,4,5,8,7]{1}[\d]{9})|(((400)-(\d{3})-(\d{4}))|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{3,7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)$/,
		                    fieldLabel: '注册号码'  
		                }, {
							xtype:'combo',
							store : comboStore,
				    		width:80,
				    		name:'employeeType',
				   			triggerAction: 'all',
				   			displayField: 'name',
				   			valueField: 'value',
				   			hiddenName:'disabled',
				   			value:'0',
							mode:'local',
						   	editable: false,
		    		    	allowBlank: false,
		    		    	readOnly:true,
							fieldLabel: '员工类型'  
						   },{  
			                    //商户名
			                    xtype: 'textfield', 
			                    name: 'merchants_name',
			                    fieldLabel: '所属服务商'  
			                },]  

		            });
        	var title = '编辑员工信息';
		    //================判断是编辑还是新增===============
		    	if(d!=null&&d.id!=null&&d.id!=0){
		    		// 是编辑状态
		    	    em_winForm.form.findField('id').setValue(d.id);
		    	    em_winForm.form.findField('employeeName').setValue(d.name);  
		    	    em_winForm.form.findField('employeePhone').setValue(d.phone);
		    	    em_winForm.form.findField('employeeType').setValue(d.employees_type+''); 
		    	    em_winForm.form.findField('merchants_name').setValue(d.merchants_name);
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
		              buttonAlign : "center",  
		              bodyStyle : "padding:0 0 0 0",  
		              items : [em_winForm],  
		              buttons : [{  
		                         text : "保存",  
		                         minWidth : 70,  
		                         handler : function() {  
		                            if (em_winForm.getForm().isValid()) {  
		                                em_winForm.getForm().submit({  
		                                          url :'/merchants/saveEmployee',  
		                                           //等待时显示 等待  
		                                          waitTitle: '请稍等...',  
		                                          waitMsg: '正在提交信息...',  
		                                            
		                                          success: function(fp, o) {  
		                                              if (o.result== true) {  
		                                                  syswin.close(); //关闭窗口  
		                                                  Ext.example.msg("提示","保存成功！");
		                                                  store.reload();  
		                                              }else {  
		                                            	  Ext.example.msg("提示","保存时出现异常！");
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
		                            syswin.close();  
		                         }  
		                     }]  
		           });  
		    syswin.show();  
		    };
		    //************************************编辑员工信息end************************************
	    
	});

</script>

</head>
<body>
</body>
</html>