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
<title>启动页管理</title>
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
                   {header:'编号',dataIndex:'id',hidden:true},
                   {header:'标题',dataIndex:'title'},
                   {header:'开始时间',dataIndex:'start_time'},
                   {header:'过期时间',dataIndex:'end_time'},
                   {header:'创建时间',dataIndex:'create_time'},
                   {header:'发布状态',dataIndex:'is_pub',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>已发布</span>";  
                       } else{  
                           return "<span style='color:red;font-weight:bold';>未发布</span>";  
                       }
           		   }},
                   {header:'展示类型',dataIndex:'show_type',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>展示</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:red;font-weight:bold';>外链</span>";  
                       }
           		   }}, 
                   {header:'链接地址',dataIndex:'link'},
                   {header:'配图',dataIndex:'image',hidden:true}
               ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/loading/loadingList',  
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
           {name:'show_type'},
           {name:'start_time'},
           {name:'end_time'},
           {name:'create_time'},
           {name:'is_pub'},
           {name:'link'},
           {name:'image'}
        ]  
    });

    //  app类型
	var store_appType = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
		remoteSort: true, //设置属性进行请求后台排序
		proxy : {
			type : 'ajax',
			url : '/common/showAppTypeByPackType',
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
    	data:[['全部',''],
    	      ['使用中','1'],
    	      ['未开始','2'],
    	      ['已过期','3']
    	]
    });
    // 发布状态
    var pubComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[['全部',''],
    	      ['已发布','1'],
    	      ['未发布','0']
    	]
    });

    // 展示类型
    var showTypeComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[
    	      ['展示','1'],
    	      ['外链','2']
    	]
    });
    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) { 
    	var title=Ext.getCmp('title').getValue();
		var is_pub=Ext.getCmp('is_pub').getValue();
		var time_status=Ext.getCmp('time_status').getValue();
        var new_params = {title:title,is_pub:is_pub,time_status:time_status};    
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
        	       <cms:havePerm url='/loading/addLoading'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },'-',
        	       </cms:havePerm> 
        	       <cms:havePerm url='/loading/deleteLoading'> 
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	      }},'-',
        	      </cms:havePerm> 
       	          <cms:havePerm url='/loading/publishLoading'> 
                  { xtype: 'button', id:'publish', text: '发布',iconCls:'Fabu',
       	    	   listeners: {
       	    		   click:function(){
       	    			   publish(null);
       	    		   }
       	         }},'-',
          	      </cms:havePerm> 
      	          <cms:havePerm url='/loading/cancelPublishLoading'> 
                  { xtype: 'button', id:'cancelPublish', text: '撤回',iconCls:'Chehui',
       	    	   listeners: {
       	    		   click:function(){
       	    			   cancelPublish();
       	    		   }
       	          }}
        	      </cms:havePerm> 
                  
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id: 'title',name: 'title',fieldLabel: '标题',labelAlign:'left',labelWidth:60},'-',
	     	        {
				     	xtype:'combo',
						fieldLabel : '发布状态',
						id: 'is_pub',
					    store : pubComboStore,
		    		    name:'is_pub',
		   			    triggerAction: 'all',
		   			    displayField: 'name',
		   			    valueField: 'value',
		   			    value:'',
		   			    labelWidth:60,
					    mode:'local'
				   },'-',
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
			        },'-',
				   <cms:havePerm url='/loading/loadingList'> 
				   { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var title=Ext.getCmp('title').getValue();
    	    			   var is_pub=Ext.getCmp('is_pub').getValue();
    	    			   var time_status=Ext.getCmp('time_status').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,title:title,is_pub:is_pub,time_status:time_status}}); 
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
    <cms:havePerm url='/loading/editLoading'> 
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
	
	//发布
	function publish()  
    {  
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
       		 Ext.example.msg("提示","请选择要发布的记录！");
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
         Ext.Msg.confirm("提示信息","确定发布选中的记录?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/loading/publishLoading",  
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
    
	//撤销发布
	function cancelPublish()  
    {  
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
       		 Ext.example.msg("提示","请选择要撤回发布的记录！");
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
         Ext.Msg.confirm("提示信息","确定撤回选中的记录?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/loading/cancelPublishLoading",  
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
	
	//删除
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
                         url:"/loading/deleteLoading",  
                         params:{ids:id},  
                         method:'post',  
                         success:function(o){  
                        	 Ext.example.msg("提示","启动页删除成功！");
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                        	 Ext.example.msg("提示","启动页删除失败！");
                         }  
                     });    
                 }  
             });  
          }  
     } ;


   
 	var showform=function(d){
 		var pic = '';
 		if(d!=null&&d.id!=null&&d.id!=0){
 			pic = d.image;
 		}
 	    var add_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 470, 
 	                height:470,
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
	    				xtype: 'hidden',
	    				name: "id"
	    			},{  
 	                    //输入启动页名称  
 	                    xtype: 'textfield', 
 	                    name: 'title',  
 	                    fieldLabel: '标题',
 	                    allowBlank	:false
 	                },{
 	                	xtype:'combo',
					    store : showTypeComboStore,
		    		    width:80,
		    		    name:'show_type',
		   			    triggerAction: 'all',
		   			    displayField: 'name',
		   			    valueField: 'value',
		   			    hiddenName:'value',
		   			    value:'1',
					    mode:'local',
					    fieldLabel: '展示类型' 
				   }, {  
 	   	                xtype: 'textfield',   
  		                name: 'link',  
  		                fieldLabel: '链接地址'
  	                }, { xtype: 'datetimefield',
                         name: 'start_time',
                         format : 'Y-m-d',
                         allowBlank	:false,
                         fieldLabel: '开始时间'
                    },
                    {   xtype: 'datetimefield',
                        name: 'end_time',
                        format : 'Y-m-d',
                        allowBlank	:false,
                        fieldLabel: '过期时间'
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
                   },{
	    				xtype: 'hidden',
	    				name: "image"
	    			},{  
                	    xtype: 'box', //或者xtype: 'component',  
                	    width: 200, //图片宽度  
                	    height: 300, //图片高度  
                	    autoEl: {  
                	        tag: 'img',    //指定为img标签  
                	        src: pic    //指定url路径  
                	    }  
                	}
 
 	               ]  
 	            });  
 	    
	    var title = '新增启动页';
	    var reqName = 'addLoading';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_winForm.form.findField('id').setValue(d.id);
	    	    add_winForm.form.findField('title').setValue(d.title);  
	    	    add_winForm.form.findField('show_type').setValue(d.show_type+'');
	    	    add_winForm.form.findField('link').setValue(d.link);  
	    	    add_winForm.form.findField('start_time').setValue(d.start_time); 
	    	    add_winForm.form.findField('end_time').setValue(d.end_time); 
	    	    add_winForm.form.findField('image').setValue(d.image); 
	        	title = '编辑启动页';
	        	reqName = 'editLoading';
	    	}
	

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 500, 
 	              height: 500,
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
 	              items : [add_winForm],  
 	              buttons : [{  
 	                         text : "保存",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	if (add_winForm.getForm().isValid()) {
 	                            add_winForm.getForm().submit({  
 	                                          url :'/loading/'+reqName,  
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
 	 
});



</script>

</head>
<body>
</body>
</html>