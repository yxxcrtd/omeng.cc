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
<title>订单到帐管理</title>
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
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,hidden:true,align:'center'},
                   {header:'订单号',dataIndex:'order_no',sortable:true,fixed:false,align:'center'},
                   {header:'活动名称',dataIndex:'activity_name',align:'center'},
                   {header:'完成时间',dataIndex:'deal_time',align:'center'},
                   {header:'店铺名称',dataIndex:'merchant_name',align:'center'},
                   {header:'店铺号码',dataIndex:'merPhone',align:'center'},
                   {header:'用户号码',dataIndex:'userPhone',align:'center'},
                   {header:'行业',dataIndex:'app_name',align:'center'},
                   {header:'省份',dataIndex:'province',align:'center'},
                   {header:'城市',dataIndex:'city',align:'center'},
                   {header:'订单类型',dataIndex:'service_name',align:'center'},
                   {header:'评价',dataIndex:'evaluation',align:'center'},
                   {header:'订单金额',dataIndex:'order_amount',align:'center'},
                   {header:'实付金额',dataIndex:'pay_amount',align:'center'},
                   {header:'奖励金额',dataIndex:'amount',align:'center'},
                   {header : '付款方式',dataIndex : 'order_pay_type',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>支付宝支付</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>微信支付</span>";  
                       }else if(value=='3'){  
                           return "<span style='color:green;font-weight:bold';>现金支付</span>";  
                       }else if(value=='4'){  
                           return "<span style='color:green;font-weight:bold';>免单</span>";  
                       }
           	       }},
                   {header:'到帐时间',dataIndex:'account_time',align:'center'},
                   {header:'处理人',dataIndex:'user_name',align:'center'},
                   {header:'到帐状态',dataIndex:'is_transfer',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:red;font-weight:bold';>未到账</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>已到账</span>";  
                       }else if(value=='2'){  
                           return "<span style='color:red;font-weight:bold';>异常</span>";  
                       }
           	       }},
           	       {header:'原因',dataIndex:'remark',align:'center'}
                    ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/activity/getOrderUserRewardAccountList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id',type:'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'activity_name'},
           {name:'deal_time'},
           {name:'merchant_name'},
           {name:'app_name'},
           {name:'province'}, 
           {name:'city'}, 
           {name:'service_name'},
           {name:'evaluation'},
           {name:'order_amount'},
           {name:'amount'},
           {name:'app_name'},
           {name:'account_time'},
           {name:'is_transfer'},
           {name:'user_name'},
           {name:'order_no'},
           {name:'userPhone'},
           {name:'merPhone'},
           {name:'order_pay_type'},
           {name:'remark'},
           {name:'pay_amount'}
        ]  
    });
 
	
 // 省信息
    var provinceStore = Ext.create("Ext.data.Store", {
    	pageSize : 50, // 每页显示几条数据
    	proxy : {
    		type : 'ajax',
    		url : '/common/showArea',
    		reader : {
    			type : 'json',
    			totalProperty : 'total',
    			root : 'data'
    		}
    	},
    	fields : [ {
    		name : 'id'
    	}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
    	{
    		name : 'area'
    	} ]
    });
    // 市信息
    var cityStore = Ext.create("Ext.data.Store", {
    	pageSize : 50, // 每页显示几条数据
    	proxy : {
    		type : 'ajax',
    		url : '/common/showServiveCity',
    		reader : {
    			type : 'json',
    			totalProperty : 'total',
    			root : 'data'
    		}
    	},
    	fields : [ {
    		name : 'id'
    	}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
    	{
    		name : 'area'
    	} ]
    }); 
 // app类型
	var store_appType = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/showAllCatalog',
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
	// 服务类型
	var store_ServiceType = Ext.create("Ext.data.Store", {
		pageSize : 50, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/getServiceByCatId',
			reader : {
				type : 'json',
				totalProperty : 'total',
				root : 'data'
			}
		},
		fields : [ {
			name : 'service_type'
		}, // mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置
		{
			name : 'service_name'
		} ]
	});
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {   
    	 var order_no=Ext.getCmp('order_no').getValue();
    	 var merchant_name=Ext.getCmp('merchant_name').getValue();
    	 var activity_id=Ext.getCmp('activity_id').getValue();
    	 var province=Ext.getCmp('province').getRawValue();
    	 var city=Ext.getCmp('city').getRawValue();
    	 var app_type=Ext.getCmp('app_type').getValue();
		 var service_id=Ext.getCmp('service_id').getValue();
		 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
		 var userPhone=Ext.getCmp('userPhone').getValue();
		 var merPhone=Ext.getCmp('merPhone').getValue();
		 var is_transfer=Ext.getCmp('is_transfer').getValue(); 
		 var new_params = { userPhone:userPhone,merPhone:merPhone,is_transfer:is_transfer,activity_id:activity_id,app_type:app_type,service_id:service_id,province:province,city:city,merchant_name:merchant_name,order_no:order_no,start_time:start_time,off_time:off_time};    
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
                <cms:havePerm url='/activity/delOrderUserReAccount'>
                {  xtype: 'button', text: '到帐处理',iconCls:'Daochu',
   	                listeners: {
   		             click:function(){
   		            	delOrderReAccount();
   		         }
   	            }
                },</cms:havePerm> 
                
      	       <cms:havePerm url='/activity/exportOrderUserRewardAccount'>
      	       { xtype: 'button',id:'export', text: '导出',iconCls:'Daochu',
          	    	   listeners: {
          	    		   click:function(){
          	    			   exportAll();
          	    		   }
          	    	   }
                 },</cms:havePerm> 
                 <cms:havePerm url='/activity/editOrderUserRewardAccount'>
         	       { xtype: 'button', text: '设置状态',iconCls:'Daochu',
             	    	   listeners: {
             	    		   click:function(){
             	    			  addform();
             	    		   }
             	    	   }
                    }</cms:havePerm>  
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	        { xtype: 'textfield',id:'activity_id',name: 'activity_id',fieldLabel: '活动ID',value:'${activity_id}',labelAlign:'left',labelWidth:70,hidden:true},
        	      
        	       {xtype: 'textfield',id: 'order_no',name: 'order_no',fieldLabel: '订单号',labelAlign:'left',labelWidth:60},'-',
        	       {xtype: 'textfield',id: 'merchant_name',name: 'merchant_name',fieldLabel: '店铺名称',labelAlign:'left',labelWidth:60},'-',
                   {xtype : 'combobox',id : 'app_type',name : 'app_type',fieldLabel : '行业类型',valueField : 'app_type',
           				displayField : 'app_name',store : store_appType,editable:true,
           				listeners : { // 监听该下拉列表的选择事件
           					select : function(combobox,record,index) {
           						Ext.getCmp('service_id').setValue('');
           						store_ServiceType
           								.load({
           									params : {
           										app : combobox.value
           									}
           								});
           					}
           				},
           				queryMode : 'local',
           				labelAlign : 'left',
           				labelWidth : 60
           			},'-',
           			{xtype : 'combobox',id : 'service_id',name : 'service_id',fieldLabel : '服务类型',valueField : 'service_type',displayField : 'service_name',
           				store : store_ServiceType,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
					
                   ]
		},	{
			xtype : 'toolbar',
			dock : 'top',
			displayInfo : true,
			items : [  
   {xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省份',valueField : 'id',displayField : 'area',
	store : provinceStore,
	listeners : { // 监听该下拉列表的选择事件
										select : function(combobox,record,index) {
											Ext.getCmp('city').setValue('');
											cityStore.load({params : {
															parentId : combobox.value
														}
													});
										}
									},
	             queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
      {xtype : 'combobox',id : 'city',name : 'city',fieldLabel : '城市',valueField : 'id',displayField : 'area',
	             store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',	
     { xtype: 'datetimefield',id: 'start_time',name: 'start_time',fieldLabel: '开始时间',format : 'Y-m-d',labelAlign:'left',labelWidth:60},'-',
     { xtype: 'datetimefield',id: 'off_time',name: 'off_time',fieldLabel: '结束时间',format : 'Y-m-d',labelAlign:'left',labelWidth:60},'-',
     ]
		},	{
			xtype : 'toolbar',
			dock : 'top',
			displayInfo : true,
			
			items : [  {xtype : 'combobox',id : 'is_transfer',name : 'is_transfer',fieldLabel : '到帐状态',value:'',valueField : 'key',displayField : 'value',
				         store: Ext.create('Ext.data.Store', {  
                        fields: [  
                          {name:'key'},{ name: 'value'}  
                       ],  
                        data: [  
                      { 'key':'0','value': '未到账' },  
                      { 'key':'1','value': '已到账' },  
                      { 'key':'2','value': '异常' } 
                      ]  
                }),queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
		
			         {xtype: 'textfield',id: 'userPhone',name: 'userPhone',fieldLabel: '用户号码',labelAlign:'left',labelWidth:70},'-',
		        	 {xtype: 'textfield',id: 'merPhone',name: 'merPhone',fieldLabel: '店铺号码',labelAlign:'left',labelWidth:70},'-',
		                   
                   <cms:havePerm url='/activity/getOrderUserRewardAccountList'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			     var order_no=Ext.getCmp('order_no').getValue();
    	    			     var merchant_name=Ext.getCmp('merchant_name').getValue();
    	    		    	 var activity_id=Ext.getCmp('activity_id').getValue();
    	    		    	 var province=Ext.getCmp('province').getRawValue();
    	    		    	 var city=Ext.getCmp('city').getRawValue();
    	    		    	 var app_type=Ext.getCmp('app_type').getValue();
    	    				 var service_id=Ext.getCmp('service_id').getValue();
    	    				 var start_time=Ext.getCmp('start_time').getValue();
    	    				 var off_time=Ext.getCmp('off_time').getValue();
    	    				 var is_transfer=Ext.getCmp('is_transfer').getValue();
    	    				 var userPhone=Ext.getCmp('userPhone').getValue();
    	    				 var merPhone=Ext.getCmp('merPhone').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,userPhone:userPhone,merPhone:merPhone,is_transfer:is_transfer,order_no:order_no, activity_id:activity_id,app_type:app_type,service_id:service_id,province:province,city:city,merchant_name:merchant_name,start_time:start_time,off_time:off_time}}); 
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
    store_appType.load(); 
    provinceStore.load(); 

    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    <cms:havePerm url='/activity/editOrderRewardAccount'>
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
        	 Ext.example.msg("提示","请选择未到帐的账单！");
             return ;   
         }  
        }  
        //没有选择要执行操作的对象  
          
        if(id == "")  
        {  
			 Ext.example.msg("提示","请选择要处理的账单！");
             return ;  
        }else{  
           Ext.Msg.confirm("提示信息","请确定要执行账单处理操作?",function (btn){  
               if(btn == 'yes')  
               {  
                   Ext.Ajax.request({  
                       url:"/activity/delOrderUserReAccount",  
                       params:{id:id},  
                       method:'post',  
                       success:function(o){  
                      	 Ext.example.msg("提示","账单处理成功！");
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
   function sleepTime(){
	  	 Ext.getCmp('export').setDisabled(true);
	  	 setTimeout(function(){
	  		Ext.getCmp('export').setDisabled(false);
	  	}, 30000);//js定时器
	    } 
  //导出所有粉丝
	function exportAll() {
		 var order_no=Ext.getCmp('order_no').getValue();
		 var merchant_name=Ext.getCmp('merchant_name').getValue();
   	     var activity_id=Ext.getCmp('activity_id').getValue();
   	     var province=Ext.getCmp('province').getRawValue();
   	     var city=Ext.getCmp('city').getRawValue();
   	     var app_type=Ext.getCmp('app_type').getValue();
		 var service_id=Ext.getCmp('service_id').getValue();
		 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
		 var is_transfer=Ext.getCmp('is_transfer').getValue(); 
		 var userPhone=Ext.getCmp('userPhone').getValue();
		 var merPhone=Ext.getCmp('merPhone').getValue();
		 window.location.href = '/activity/exportOrderUserRewardAccount?province='+province+'&city='+city+'&merchant_name='+merchant_name
		 +'&userPhone='+userPhone+'&merPhone='+merPhone+'&activity_id='+activity_id+'&is_transfer='+is_transfer+'&order_no='+order_no+'&app_type='+app_type+'&service_id='+service_id+'&start_time='+start_time
		+'&off_time='+off_time;
		 
		 Ext.example.msg("提示","正在导出报表，请稍后！");
         sleepTime();
	};
  
	var addform=function(){
		var rows = grid.getSelectionModel().getSelection();  
		if(rows.length==0){
			 Ext.example.msg("提示","请选择要处理的账单！");
             return ; 
		}
		if(rows.length>1){
			 Ext.example.msg("提示","请选择一个要处理的账单！");
            return ; 
		}
		if(rows.length==1){
			 var is_transfer= rows[0].get('is_transfer'); 
	         if(is_transfer==1){
	        	 Ext.example.msg("提示","请选择未到帐或异常的账单！");
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
	                 	   //下拉列表框  
	                       xtype: 'combobox', //9  
	                       fieldLabel: '设置状态',
	                       displayField: 'value',
	                       valueField : 'key',
	                       name:'is_transfer',
	                       store: Ext.create('Ext.data.Store', {  
	                           fields: [  
	                             {name:'key'},{ name: 'value'}  
	                             ],  
	                              data: [  
	                             { 'key':'0','value': '未到账' },  
	                             { 'key':'2','value': '异常' } 
	                             ]  
	                       }),
	                       allowBlank:false,
	                       width:220
	       			    },{  
		                      //多行文本输入框  
		                      xtype: 'textareafield', //5  
		                      name: 'remark',  
		                      fieldLabel: '填写原因'
		                  }]  
 	            });  
 	    
	    		// 是编辑状态
	    	    add_orderForm.form.findField('id').setValue(rows[0].get('id'));
	    	    add_orderForm.form.findField('is_transfer').setValue(rows[0].get('is_transfer')+'');  
	    	    add_orderForm.form.findField('remark').setValue(rows[0].get('remark'));
	    	  
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
 	                                          url :'/activity/editOrderUserRewardAccount',  
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