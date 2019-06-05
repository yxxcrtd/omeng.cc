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
<title>广告管理</title>
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
                   {header:'编号',dataIndex:'id',hidden:true},
                   {header:'广告名称',dataIndex:'name'},
                   {header:'开始时间',dataIndex:'join_time'},
                   {header:'过期时间',dataIndex:'overdue_time'},
                   {header:'启用状态',dataIndex:'slider_status',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>启用</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:red;font-weight:bold';>禁用</span>";  
                       }
           		   }},
                   {header:'广告类型',dataIndex:'slider_type',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>外链</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>展示</span>";  
                       }
           		   }}, 
                   {header:'展现序号',dataIndex:'sort'},
                   {header:'app类型',dataIndex:'app_type',hidden:true},
                   {header:'app类型',dataIndex:'app_name'},
                   {header:'链接地址',dataIndex:'link_url'},
                   {header:'广告配图',dataIndex:'pics_path',renderer:function(value){
                	   return "<span style='color:green;font-weight:bold';>下载</span>";
                   }}
               ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
        proxy:{  
            type:'ajax',  
            url:'/manager/slider/getSliders',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'id'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'name'},  
           {name:'join_time'},
           {name:'overdue_time'},
           {name:'slider_status'},
           {name:'slider_type'},
           {name:'sort'},
           {name:'app_type'},
           {name:'app_name'},
           {name:'link_url'},
           {name:'pics_path'}
        ]  
    });
 // app类型
	var store_appType = Ext.create("Ext.data.Store", {
		pageSize : 20, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/manager/common/showAppType',
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
    // 时间状态
    var timeStatusComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['全部','0'],
    	      ['使用中','1'],
    	      ['未开始','2'],
    	      ['已过期','3']
    	]
    });
    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var name=Ext.getCmp('name').getValue();
		var time_status=Ext.getCmp('time_status').getValue();
		var app_type=Ext.getCmp('app_type').getValue();
		var status_type=Ext.getCmp('status_type').getValue();  
        var new_params = { name:name,time_status:time_status,app_type:app_type,status_type:status_type};    
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
        	       <cms:havePerm url='/manager/slider/addSlider'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'Useradd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },</cms:havePerm>
        	       <cms:havePerm url='/manager/slider/deleteSlider'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'Userdelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	    	   }},'-',</cms:havePerm>
        	    	   <cms:havePerm url='/manager/slider/exportExcel'>
	        	       { xtype: 'button', text: '导出',iconCls:'Pagewhiteoffice',
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
        	items:[{ xtype: 'textfield',id: 'name',name: 'name',fieldLabel: '广告名称',labelAlign:'left',labelWidth:60},
     	           {
			     	xtype:'combo',
					fieldLabel : '时间状态',
					id: 'time_status',
				    store : timeStatusComboStore,
	    		    name:'time_status',
	   			    triggerAction: 'all',
	   			    displayField: 'name',
	   			    valueField: 'value',
	   			    value:'',
	   			    labelWidth:60,
				    mode:'local'
			        },{
						xtype : 'combobox',
						name : 'app_type',
						id: 'app_type',
						fieldLabel : 'app类型',
						valueField : 'app_type',
						displayField : 'app_name',
						store : store_appType,
						allowBlank	:false,
						editable:false,
						hiddenName:'',
						labelWidth:60,
						allowBlank	:false,
						queryMode : 'local'
					},
        	       { xtype: 'combobox',id:'status_type',name: 'status_type',displayField: 'status_name',valueField : 'status_type',
               		fieldLabel: '启用状态',store: Ext.create('Ext.data.Store', {  
                        fields: [  
   	                          { name: 'status_type'},{ name: 'status_name' }  
   	                          ],  
   	                          data: [  
   	                          { "status_type": "1","status_name": "启用 " },  
   	                          { "status_type": "2","status_name": "禁用" }
   	                          ]  
   	                    }),queryMode: 'local',labelAlign:'left',labelWidth:60},
   	                 <cms:havePerm url='/operator/slider/select'>
        	       { xtype: 'button', text: '查询',id:'select',iconCls:'Usermagnify',listeners: {
    	    		   click:function(){
    	    			   var name=Ext.getCmp('name').getValue();
    	    			   var time_status=Ext.getCmp('time_status').getValue();
    	    			   var app_type=Ext.getCmp('app_type').getValue();
    	    			   var status_type=Ext.getCmp('status_type').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,name:name,time_status:time_status,app_type:app_type,status_type:status_type}}); 
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
    store_appType.load();
  
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data);
    });
    
    grid.on("cellclick",function(table, td, cellIndex,record, tr, rowIndex,  e, eOpts){
    	if(cellIndex==6){
    		window.location.href = '/manager/slider/downLoad?path='+record.get('path');
    	}else{
    		return false;
    	}
    	});
    
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	//广告删除
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
             Ext.Msg.alert("提示信息","请选择要删除的对象");  
             return ;  
          }else{  
             Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
                 if(btn == 'yes')  
                 {  
                     Ext.Ajax.request({  
                         url:"/manager/slider/deleteSlider",  
                         params:{id:id},  
                         method:'post',  
                         success:function(o){  
                             
                             Ext.Msg.alert("提示信息","广告删除成功!");  
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){  
                             Ext.Msg.alert("提示信息","广告删除失败!");  
                         }  
                     });    
                 }  
             });  
          }  
     } ;
     //导出所有广告
 	function exportAll() {
 		window.location.href = '/manager/slider/exportExcel';
 	};
   //form表单
   	var typeStore = new Ext.data.SimpleStore({
	    	fields:['name','value'],
	    	data:[['外链','1'],
	    	      ['展示','2']
	    	      
	    	]
	    });
   	var statusStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['启用','1'],
    	      ['禁用','2']
    	   
    	]
    });
   
 	var showform=function(d){  
 	    var add_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 450, 
 	                height:470,
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
 	                    value: '填写广告相关信息'  
 	                     
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
			 	                    value: ' 图片大小10M以内，格式jpg、png'
			 	                    }]
				              }]
                   }
 	                    ]  
 	            });  
 	    
	    var title = '新增广告';
	    var reqName = 'addSlider';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态

	        	title = '编辑广告';
	        	reqName = 'editSlider';
	    	}
	

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 450, 
 	              height: 450,
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
 	              items : [add_winForm],  
 	              buttons : [{  
 	                         text : "保存",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	
 	                        	if (add_winForm.getForm().isValid()) {
 	                        		add_winForm.getForm().submit({  
 	                                          url :'/manager/slider/test',  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',  
 	                                            
 	                                          success: function(fp, o) {  
 	       //alert(o);success函数，成功提交后，根据返回信息判断情况  
 	                                              if (o.result== true) {  
 	                                                 Ext.MessageBox.alert("信息提示","保存成功!");  
 	                                                 syswin.close(); //关闭窗口  
 	                                                 store.reload();  
 	                                              }else {  
 	                                            	 Ext.MessageBox.alert("信息提示","保存失败!");   
 	                                              }  
 	                                          },  
 	                                          failure: function() {  
 	                                        	 Ext.MessageBox.alert("信息提示","保存失败!");  
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
 	 
});
</script>

</head>
<body>
</body>
</html>