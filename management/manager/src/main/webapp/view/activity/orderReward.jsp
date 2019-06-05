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
<title>订单奖励</title>
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
	var activtiy_id='${activity_id}';
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,hidden:true,align:'center'},
                   {header:'奖励方式',dataIndex:'reward_type',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>统一到账</span>";  
                       } else if(value=='2'){  
                           return "<span style='color:green;font-weight:bold';>次日到账</span>";  
                       }
           	       }},
           	      {header:'到账处理',dataIndex:'handle_type',align:'center',renderer:function(value){  
                    if(value=='0'){  
                        return "<span style='color:red;font-weight:bold';>手动</span>";  
                    } else if(value=='1'){  
                        return "<span style='color:green;font-weight:bold';>自动</span>";  
                    }
        	       }},
                   {header:'开放行业',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                       return '<a href="javascript:showOpenService(\''+r.raw.activity_id+'\')">行业设置</a>';
             	   }},
             	    {header:'开放城市',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                        return '<a href="javascript:showOpenCity(\''+r.raw.activity_id+'\')">城市设置</a>';
              	   }},
             	   {header:'查账',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                        return '<a href="javascript:showOrderAccountDetail(\''+r.raw.activity_id+'\')">查看到帐情况</a>';
              		}}
                    ];
	//创建store数据源
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/activity/getOrderRewardList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id',type:'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'reward_type'}, 
           {name:'handle_type'}
        ]  
    });

 // 订单服务对象
		var orderObjectStore = Ext.create("Ext.data.Store", {
			pageSize : 20, // 每页显示几条数据
				proxy : {
					type : 'ajax',
					url : '/common/showOrderObjectStore',
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
					name : 'value'
			 } ]
			});
  // 奖励总额上限
		var orderRewardLimitStore = Ext.create("Ext.data.Store", {
			pageSize : 20, // 每页显示几条数据
				proxy : {
					type : 'ajax',
					url : '/common/showOrderObjectStore',
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
					name : 'value'
			 } ]
			});
 
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {   
    	var activity_id=Ext.getCmp('activity_id').getValue();
		var new_params = { activity_id:activity_id};    
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
        	items:[ '-',
        	       <cms:havePerm url='/activity/addOrderReward'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   addform(null);
        	    		   }
        	    	   }
        	       },</cms:havePerm>
        	       <cms:havePerm url='/activity/deleteOrderReward'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	    	   }},'-',</cms:havePerm>
        	    	   <cms:havePerm url='/activity/editOrderRewardRule'>
                       { xtype: 'button', id:'del', text: '配置规则',iconCls:'NewDelete',
            	    	   listeners: {
            	    		   click:function(){
            	    			   showform();
            	    		   }
            	    	   }},'-',</cms:havePerm>
               
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
                   { xtype: 'textfield',id: 'activity_id',name: 'activity_id',fieldLabel: '活动Id',value:'${activity_id}',labelAlign:'left',labelWidth:60,hidden:true},'-',
                   			<cms:havePerm url='/activity/getOrderRewardList'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var activity_id=Ext.getCmp('activity_id').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,activity_id:activity_id}}); 
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
    orderObjectStore.load({params:{group:2}}); 
    orderRewardLimitStore.load({params:{group:1}}); 
    
    <cms:havePerm url='/activity/editOrderReward'>
    grid.on("itemdblclick",function(grid, row){
    	addform(row.data);
    });
    </cms:havePerm>
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
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
			 Ext.example.msg("提示","请选择要删除的对象！");
            return ;  
         }else{  
            Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
                if(btn == 'yes')  
                {  
                    Ext.Ajax.request({  
                        url:"/activity/deleteOrderReward",  
                        params:{id:id},  
                        method:'post',  
                        success:function(o){  
                       	 Ext.example.msg("提示","活动删除成功！");
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
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	var addform=function(d){
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
 	                    value: '填写广告相关信息'  
 	                     
 	                }, {
	    				xtype: 'hidden',
	    				name: "id"
	    			},{
	    				xtype: 'hidden',
	    				name: "activity_id"
	    			},{
	                 	   //下拉列表框  
	                       xtype: 'combobox', //9  
	                       fieldLabel: '奖励方式',
	                       displayField: 'value',
	                       valueField : 'key',
	                       name:'reward_type',
	                       store: Ext.create('Ext.data.Store', {  
	                           fields: [  
	                             {name:'key'},{ name: 'value'}  
	                             ],  
	                              data: [  
	                             { 'key':'1','value': '统一到账' },  
	                             { 'key':'2','value': '次日到账' } 
	                             ]  
	                       }),
	                       allowBlank:false,
	                       width:220
	       			    },{
	                  	   //下拉列表框  
	                        xtype: 'combobox', //9  
	                        fieldLabel: '到帐处理',
	                        displayField: 'value',
	                        valueField : 'key',
	                        name:'handle_type',
	                        store: Ext.create('Ext.data.Store', {  
	                            fields: [  
	                              {name:'key'},{ name: 'value'}  
	                              ],  
	                               data: [  
	                              { 'key':'0','value': '手动' },  
	                              { 'key':'1','value': '自动' } 
	                              ]  
	                        }),
	                        allowBlank	:false,
	                        width:220
	                        
	        			    }]  
 	            });  
 	    
	    var title = '新增奖励';
	    var reqName = 'addOrderReward';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_orderForm.form.findField('id').setValue(d.id);
	    	    add_orderForm.form.findField('reward_type').setValue(d.reward_type+'');  
	    	    add_orderForm.form.findField('handle_type').setValue(d.handle_type+'');
	    	   
	        	title = '编辑奖励';
	        	reqName = 'editOrderReward';
	    	}
	    	 add_orderForm.form.findField('activity_id').setValue(activtiy_id);

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var orderRewardwin = Ext.create('Ext.window.Window',{  
 	              title : title,  
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
 	                                          url :'/activity/'+reqName,  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',  
 	                                            
 	                                          success: function(fp, o) {  
 	                                              if (o.result== true) { 
 	                                               	 Ext.example.msg("提示","保存成功！");
 	                                               	 orderRewardwin.close(); //关闭窗口  
 	                                                 store.reload();  
 	                                              }else { 
 	                                               	 Ext.example.msg("提示","不要添加多条记录，保存失败！");
 	                                              }  
 	                                          },  
 	                                          failure: function() {
 	                                          	 Ext.example.msg("提示","不要添加多条记录，保存失败！");
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
	
	
	
	//编辑订单奖励规则
	var showform=function(){
	 var i=0;
 	    var add_winForm = Ext.create('Ext.form.Panel', {
            width: 550, 
            height:750,
			layout:"form",
		    frame:true,
		    //labelWidth:80,
		    labelAlign : "right",
			items : [{  
                //显示文本框，相当于label  
                xtype: 'displayfield',   
                name: 'displayfield1',  
//                fieldLabel: 'Display field',  
                value: '编辑订单奖励规则'  
                 
            }, {
				xtype: 'hidden',
				name: "activity_id"
			},{
				xtype: 'hidden',
				name: "count"
			},{
              layout:"column",
              items:[{  
                  columnWidth:.5,
                  layout:"form",
                  items:[{
                	  //下拉列表框  
                      xtype: 'combobox', //9  
                      fieldLabel: '订单服务对象',
                      displayField: 'value',
                      valueField : 'id',
                      name:'orderObject',
                      //editable:false,
                      store: orderObjectStore,
                      listeners : { // 监听该下拉列表的选择事件
      					select : function(combobox,record,index) {
      						var sle=combobox.value;
      						if(sle==4){
      							add_winForm.form.findField('orderObjectValue').setValue();
      							add_winForm.form.findField('orderObjectValue').setVisible(true);	
      						}else{		
      							add_winForm.form.findField('orderObjectValue').setValue();
      						    add_winForm.form.findField('orderObjectValue').setVisible(false);
      						}	
      					}
      				},
                      queryMode : 'local',
                      allowBlank	:false,
                      width:220
      			}]  
              },{  
                  columnWidth:.5,
                  layout:"form",
                  style:'margin-left:20px',
                  items:[{
               	   //下拉列表框  
				        xtype: 'numberfield',  
	                    name: 'orderObjectValue', //10  
	                    fieldLabel: 'N= ', 
	                    minValue: 0,  
	                    maxValue: 999999, 
	                    emptyText:'请输入0-999999的整数',
	                    value:'',
	                    labelWidth:50,
                        width:220
      			}]  
              }]  
             },{
                    layout:"column",
                    items:[{  
                        columnWidth:.5,
                        layout:"form",
                        items:[{
                      	  //下拉列表框  
                            xtype: 'combobox', //9  
                            fieldLabel: '奖励总额上限',
                            displayField: 'value',
                            valueField : 'id',
                            name:'orderRewardLimit',
                            //editable:false,
                            store: orderRewardLimitStore,
                            listeners : { // 监听该下拉列表的选择事件
              					select : function(combobox,record,index) {
              						var sle=combobox.value;
              						if(sle==2){
              							add_winForm.form.findField('orderRewardLimitValue').setValue();
              							add_winForm.form.findField('orderRewardLimitValue').setVisible(false);	
              						}else{
              							add_winForm.form.findField('orderRewardLimitValue').setValue();
              						    add_winForm.form.findField('orderRewardLimitValue').setVisible(true);
              						}	
              					}
              				},
                            queryMode : 'local',
                            allowBlank	:false,
                            width:220
            			}]  
                    },{  
                        columnWidth:.5,
                        layout:"form",
                        style:'margin-left:20px',
                        items:[{
                     	   //下拉列表框  
      				        xtype: 'numberfield',  
      	                    name: 'orderRewardLimitValue',  
      	                    fieldLabel: '', 
      	                    minValue: 0,  
      	                    maxValue: 999999, 
      	                    emptyText:'请输入0-999999的整数',
      	                    value:'',
                            width:220
            			}]  
                    }]  
                   }
                   ]

		});
 	    
     	 //添加输入框
    	   var adFormItem=function(){
 		  
 		    var limt=1;
 		    if(i>0){
 			  var pa=i-1;
 			  limt=	add_winForm.form.findField('orderCount'+pa).getValue()+1;
 		    }
 		    var orderCountlimt="orderCountlimt"+i;
   	    	var orderCount="orderCount"+i;
   	    	var orderCountDanwei="orderCountDanwei"+i;
   	    	var orderMoneyLimit="orderMoneyLimit"+i;
   	    	var orderMoneyLimitDanwei="orderMoneyLimitDanwei"+i;
   	    	var lessOrderMoney="lessOrderMoney"+i;
   	    	var lessOrderMoneyDanwei="lessOrderMoneyDanwei"+i;
   	    	var moreOrderMoney="moreOrderMoney"+i;
   	    	var moreOrderMoneyDanwei="moreOrderMoneyDanwei"+i;
   	    	var configItem=[{
   			xtype: 'fieldset',
   			title: '单笔订单奖励金额',
   			collapsible: true,
   			anchor: '98%',
   			hideBorders: false,
   			autoHeight: true,
   			items: [
          
          {
              layout:"column",
              items:[{
            	   //下拉列表框  
			        xtype: 'numberfield',  
                    name: orderCountlimt, 
                    minValue: 0, 
                    value:limt,
                    maxValue: 999999,
                    hidden:true
  			},{  
                  columnWidth:.8,
                  layout:"form",
                  items:[{
   		    				// 订单数区间
   	                	    xtype: 'numberfield',  
   		                    name: orderCount, //10  
   		                    fieldLabel: '订单数区间  ：'+ limt+' ~  ', 
   		                    minValue: 0,  
   		                    maxValue: 999999 ,
   		                    allowDecimals: false,
   		                    emptyText:'请输入0-999999的整数',
   		                    value:'',
   		                    labelWidth:140,
   		                    allowBlank	:false,
                            width:220
      			}]  
              },{  
                  columnWidth:.2,
                  layout:"form",
                  style:'margin-left:20px',
                  items:[
               	       { xtype: 'displayfield',   
   	 	                 name: orderCountDanwei,  
   	 	                 value: '单',
   	 	                 width:220
   	 	                }]  
              }]  
             },{
                  layout:"column",
                  items:[{  
                      columnWidth:.8,
                      layout:"form",
                      items:[{
                   	     xtype: 'numberfield',  
   		                 name: orderMoneyLimit,   
   		                 fieldLabel: '限制金额', 
   		                 minValue: 0,  
   		                 maxValue: 999999,
   		                 allowDecimals: false,
   		                 emptyText:'请输入0-999999的整数',
   		                 value:'',
   		                 labelWidth:140,
                         width:220
          			}]  
                  },{  
                      columnWidth:.2,
                      layout:"form",
                      style:'margin-left:20px',
                      items:[
                   	       {
                   		        xtype: 'displayfield',   
   		 	                    name: orderMoneyLimitDanwei,  
   		 	                    value: '',
   		 	                    width:220
   		 	                    }]  
                  }]  
                 }, {
                  layout:"column",
                  items:[{  
                      columnWidth:.8,
                      layout:"form",
                      items:[{
                   	      xtype: 'numberfield',  
   			              name: lessOrderMoney,   
   			              fieldLabel: '小于限制金额 : 奖励金额 = 实付金额×', 
   			              minValue: 0,  
   			              maxValue: 100,
   			              allowDecimals: false,
   			              emptyText:'请输入0-100的整数',
   			              value:'',
   			              labelWidth:230,
                          width:220
          			}]  
                  },{  
                      columnWidth:.2,
                      layout:"form",
                      style:'margin-left:20px',
                      items:[
                   	       {
                   	    	xtype: 'displayfield',   
   		 	                name: lessOrderMoneyDanwei,  
   		 	                value: '%',
   		 	                width:220
   		 	                    }]  
                  }]  
                 },{
                      layout:"column",
                      items:[{  
                          columnWidth:.8,
                          layout:"form",
                          items:[{
                       	      xtype: 'numberfield',  
    			              name: moreOrderMoney,   
    			              fieldLabel: '大于限制金额  :      奖励金额= ', 
    			              minValue: 0,  
    			              maxValue: 99999,
    			              emptyText:'请输入0-999999的整数',
    			              labelWidth:230,
   		 	                  width:220
              			}]  
                      },{  
                          columnWidth:.2,
                          layout:"form",
                          style:'margin-left:20px',
                          items:[
                       	   {xtype: 'displayfield',   
   		 	                    name: moreOrderMoneyDanwei,  
   		 	                    value: '元',
   		 	                    width:220
   		 	                    }]  
                      }]  
                     }]}];
   	    	
   	    	var formPanel=add_winForm.form.owner;
   	    	formPanel.add(configItem[0]);
   	    	
   	    	i++;
   	    	
   	    } 
  	    
  	 //-------------------end----------------------   
  	    
 	   var rows = grid.getSelectionModel().getSelection();
		// user_id：所有选中的代理商Id的集合使用','隔开，初始化为空
		if (rows.length == 0) {
    	    Ext.example.msg("提示","请选择要编辑的对象！"); 
			return;
		}
		 if(rows.length > 1)  
	       {  
        	  Ext.example.msg("提示","只能选择一个编辑的对象！"); 
	          return ;  
	       }
	    var title = '编辑规则';
	    var reqName = 'editOrderRewardRule';
	    add_winForm.form.findField('activity_id').setValue(activtiy_id);
	    add_winForm.form.findField('orderObject').setValue(rows[0].raw.orderObject+'');
	    if(rows[0].raw.orderObject!=4){
	    	add_winForm.form.findField('orderObjectValue').setVisible(false);
	    }
	    add_winForm.form.findField('orderObjectValue').setValue(rows[0].raw.orderObjectValue);
	    add_winForm.form.findField('orderRewardLimit').setValue(rows[0].raw.orderRewardLimit+'');
	    if(rows[0].raw.orderRewardLimit==2){
	    	add_winForm.form.findField('orderRewardLimitValue').setVisible(false);
	    }
	    add_winForm.form.findField('orderRewardLimitValue').setValue(rows[0].raw.orderRewardLimitValue);
	     
	    var singleRules=rows[0].raw.singleRule;
	 
	    var orderCount="orderCount"+i;
    	var orderCountDanwei="orderCountDanwei"+i;
    	var orderMoneyLimit="orderMoneyLimit"+i;
    	var orderMoneyLimitDanwei="orderMoneyLimitDanwei"+i;
    	var lessOrderMoney="lessOrderMoney"+i;
    	var lessOrderMoneyDanwei="lessOrderMoneyDanwei"+i;
    	var moreOrderMoney="moreOrderMoney"+i;
    	var moreOrderMoneyDanwei="moreOrderMoneyDanwei"+i;
	    
 	   var count=singleRules.length;
 	   if(count>0){
	    	for(var j=0;j<count;j++){
	    		adFormItem();
	    		add_winForm.form.findField('orderCount'+j).setValue(singleRules[j].extend2);
	    	    add_winForm.form.findField('orderMoneyLimit'+j).setValue(singleRules[j].extend3);
	    	    add_winForm.form.findField('lessOrderMoney'+j).setValue(singleRules[j].extend4);
	    	    add_winForm.form.findField('moreOrderMoney'+j).setValue(singleRules[j].extend5); 
	    	}
	    }
	  
	    
	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : "编辑奖励规则",  
 	              width : 600, 
 	              height: 450,
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
 	              items : [add_winForm],  
 	              buttons : [{  
 	                         text : "保存",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	add_winForm.form.findField('count').setValue(i);
 	                        	if (add_winForm.getForm().isValid()) {
 	                        		add_winForm.getForm().submit({  
 	                                          url :'/activity/'+reqName,  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',  
 	                                          submitEmptyText: false,  
 	                                          success: function(fp, o) {  
 	                                               	 Ext.example.msg("提示","保存成功！");
 	                                               	 delAllFormItem();
 	                                               	 syswin.close(); //关闭窗口  
 	                                                 store.reload();
 	                                                 
 	                                          },  
 	                                          failure: function() {
 	                                          	 Ext.example.msg("提示","保存失败！");
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
 	                        	 delAllFormItem();
 	                        	 syswin.close();  
 	                         }  
 	                     } , {  
                         text : "增加条件",  
                         minWidth : 70,  
                         handler : function() { 
                        	if(i==0){
                        		adFormItem();  
                        	} else{
                        	var pa=i-1;
                        	var limt=add_winForm.form.findField('orderCount'+pa).getValue();
                        	if(limt>0){
                        		adFormItem();  
                        	} else{
                        		Ext.example.msg("提示","请填写订单区间！");
                        	}
                        	}
                         }  
                     }, {  
                         text : "删除条件",  
                         minWidth : 70,  
                         handler : function() {  
                        	delFormItem();  
                         }  
                     } ]  
 	           });  
 	    syswin.show();  
 	   
 	    var delFormItem=function(){
    	if(i<1){
    		Ext.example.msg("提示","无法删除！");	
    	}else{
    	var formPanel=add_winForm.form.owner;
    	formPanel.remove(formPanel.items.last());
    	i--;
    	formPanel.doLayout();
    	}
    }
 	    
 	//窗口关闭，表单恢复到初始化状态
 	   var delAllFormItem=function(){
 		var count=i;
 	    for(var k=0;k<count;k++){
 	    	var formPanel=add_winForm.form.owner;
 	    	formPanel.remove(formPanel.items.last());
 	    	i--;
 	    	formPanel.doLayout();
 	    }
 	}
 	//-------end--------- 
 	
	};  
});

function showOpenCity(id){
	parent.removeTab("activity_order_reward_city");
	parent.addTab("activity_order_reward_city","奖励活动开放城市","Fuwushangyunyingxinxiguanli","/activity/orderRewardOpenCityindex?activity_id="+id); 	 
}

function showOpenService(id){
	parent.removeTab("activity_order_reward_service");
	parent.addTab("activity_order_reward_service","奖励活动开放行业","Fuwushangyunyingxinxiguanli","/activity/orderRewardOpenServiceindex?activity_id="+id); 	 
}

function showOrderAccountDetail(id){
	parent.removeTab("activity_order_reward_account");
	parent.addTab("activity_order_reward_account","查看到帐情况","Fuwushangyunyingxinxiguanli","/activity/orderRewardAccountindex?activity_id="+id); 	 
}

</script>

</head>
<body>
</body>
</html>