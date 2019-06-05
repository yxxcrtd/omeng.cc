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
<title>消息发送失败列表</title>
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
                   {header:'id',dataIndex:'id',sortable:true,fixed:false,hidden:true,align:'center'},
                   {header:'账单id',dataIndex:'business_id',sortable:true,fixed:false,align:'center'},
                   {header:'队列名',dataIndex:'queueName',sortable:true,fixed:false,align:'center'},
                   {header:'消息体',dataIndex:'msg',align:'center'},
                   {header : '类型',dataIndex : 'type',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>订单收入</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>订单奖励</span>";  
                       }else if(value=='3'){  
                           return "<span style='color:green;font-weight:bold';>活动奖励</span>";  
                       }else if(value=='4'){  
                           return "<span style='color:green;font-weight:bold';>开业剪彩</span>";  
                       }
           	       }},
                   {header : '状态',dataIndex : 'status',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:red;font-weight:bold';>未处理</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>已处理</span>";  
                       }
           	       }},
                   {header:'加入时间',dataIndex:'join_time',align:'center'},
                   {header:'处理时间',dataIndex:'deal_time',align:'center'},
                   {header:'处理人',dataIndex:'user_name',align:'center'}
                    ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/systemManager/getMqSendFailureList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id',type:'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'business_id'},
           {name:'queueName'},
           {name:'msg'},
           {name:'type'},
           {name:'status'}, 
           {name:'join_time'}, 
           {name:'deal_time'},
           {name:'user_name'}
        ]  
    });
 
	
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {   
    	 var status=Ext.getCmp('status').getValue();
    	 var type=Ext.getCmp('type').getValue();
		 var new_params = { status:status,type:type};    
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
                <cms:havePerm url='/systemManager/delMqSendFailureList'>
                {  xtype: 'button', text: '重发',iconCls:'Daochu',
   	                listeners: {
   		             click:function(){
   		            	delOrderReAccount();
   		         }
   	            }
                }</cms:havePerm> 
                
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	         {xtype : 'combobox',id : 'type',name : 'type',fieldLabel : '类型',value:'',valueField : 'key',displayField : 'value',
				         store: Ext.create('Ext.data.Store', {  
                        fields: [  
                          {name:'key'},{ name: 'value'}  
                       ],  
                        data: [  
                      { 'key':'1','value': '订单收入' },  
                      { 'key':'2','value': '订单奖励' },  
                      { 'key':'3','value': '活动奖励' } ,  
                      { 'key':'4','value': '开业剪彩' }
                      ]  
                }),queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
                {xtype : 'combobox',id : 'status',name : 'status',fieldLabel : '状态',value:'',valueField : 'key',displayField : 'value',
			         store: Ext.create('Ext.data.Store', {  
                   fields: [  
                     {name:'key'},{ name: 'value'}  
                  ],  
                   data: [  
                 { 'key':'0','value': '未处理' },  
                 { 'key':'1','value': '已处理' }
                 ]  
           }),queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
		
			               
                   <cms:havePerm url='/systemManager/getMqSendFailureList'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var status=Ext.getCmp('status').getValue();
    	    		       var type=Ext.getCmp('type').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,status:status,type:type}}); 
    	    		   }
    	    		   }}</cms:havePerm>
                  ]
        }],
        bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
            displayInfo : true,
            store : store
        })
    });
    
    store.load({params:{start:0,limit:20}}); 
  

    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    <cms:havePerm url='/systemManager/editMqSendFailureList'>
    grid.on("itemdblclick",function(grid, row){
    	addform();
    });
    </cms:havePerm>
  //广告删除
	 function delOrderReAccount()  
   {  
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
       
        for(var j = 0;j<rows.length;j++)  
        {  
         var is_transfer= rows[j].get('is_transfer'); 
         if(is_transfer==1){
        	 Ext.example.msg("提示","请选择未处理的消息！");
             return ;   
         }  
        }  
        //没有选择要执行操作的对象  
          
        if(id == "")  
        {  
			 Ext.example.msg("提示","请选择要处理的消息！");
             return ;  
        }else{  
           Ext.Msg.confirm("提示信息","请确定要执行消息处理操作?",function (btn){  
               if(btn == 'yes')  
               {  
                   Ext.Ajax.request({  
                       url:"/systemManager/delMqSendFailureList",  
                       params:{id:id},  
                       method:'post',  
                       success:function(o){  
                      	 Ext.example.msg("提示","消息处理成功！");
                         store.reload();  
                         return ;  
                       },  
                       failure:function(form,action){ 
                      	 Ext.example.msg("提示",o.responseText);
                       }  
                   });    
               }  
           });  
        }  
   } ;
   
	var addform=function(){
		var rows = grid.getSelectionModel().getSelection();  
		if(rows.length==0){
			 Ext.example.msg("提示","请选择要处理的消息！");
             return ; 
		}
		if(rows.length>1){
			 Ext.example.msg("提示","请选择一个要处理的消息！");
             return ; 
		}
		if(rows.length==1){
			 var status= rows[0].get('status'); 
	         if(status==1){
	        	 Ext.example.msg("提示","请选择未处理的消息！");
	             return ;   
	         }  
		}
 	    var add_orderForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 470, 
 	                height:270,
 	               // autoScroll: true, 
 	                bodyPadding:5,
 	                fileUpload:true,
 	               // resizable : true,  
  	                //autoScroll: true,  
  	              // 是否可以拖动  
  	                //draggable:true,  
  	               // collapsible : true, // 允许缩放条  
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
 	                    value: ''  
 	                     
 	                }, {
	    				xtype: 'hidden',
	    				name: "id"
	    			},{
	    				// 配置value
	    				xtype : 'textfield',
	    				name : 'msg',
	    				fieldLabel : '消息体'
	    			}]  
 	            });  
 	    
	    		// 是编辑状态
	    	    add_orderForm.form.findField('id').setValue(rows[0].get('id'));
	    	    add_orderForm.form.findField('msg').setValue(rows[0].get('msg'));  
	    	  
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var orderRewardwin = Ext.create('Ext.window.Window',{  
 	              title : '设置',  
 	              width : 500, 
 	              height: 250,
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
 	              items : [add_orderForm],  
 	              buttons : [{  
 	                         text : "保存",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	
 	                        	if (add_orderForm.getForm().isValid()) {
 	                        		add_orderForm.getForm().submit({  
 	                                          url :'/systemManager/editMqSendFailureList',  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',  
 	                                            
 	                                          success: function(fp, o) {  
 	                                              if (o.result== true) { 
 	                                               	 Ext.example.msg("提示","保存成功！");
 	                                               	 orderRewardwin.close(); //关闭窗口  
 	                                                 store.reload();  
 	                                              }else { 
 	                                               	 Ext.example.msg("提示","状态已改变，保存失败！");
 	                                              }  
 	                                          },  
 	                                          failure: function() {
 	                                          	 Ext.example.msg("提示","状态已改变，保存失败！");
 	                                          }  
 	                                       });  
 	                            } else{
 	                            	Ext.example.msg("提示","请填写完整信息！");
 	                            }
 	                        	} 
 	                         }  
 	                    , {  
 	                         text : "关闭",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	orderRewardwin.close();  
 	                         }  
 	                     }]  
 	           });  
 	   orderRewardwin.show();  
 	    };
	
	
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
});


</script>

</head>
<body>
</body>
</html>