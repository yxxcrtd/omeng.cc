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
<title>粉丝增加</title>
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
                   {header:'服务商ID',dataIndex:'id',type: 'string',hidden:true,align:'center'},
                   {header:'服务商名称',dataIndex:'name',align:'center'},
                   {header:'注册号码',dataIndex:'telephone',align:'center'},
                   {header:'省份',dataIndex:'province',align:'center'},
                   {header:'城市',dataIndex:'city',align:'center'},
                   {header:'app类型',dataIndex:'app_name',align:'center'},
                   {header:'地址',dataIndex:'address',align:'center'},
                   {header:'粉丝量',dataIndex:'fensiTotal',align:'center'}
               ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/merchants/getMerchantsInfoListForFensi',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id',type:'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'name'}, 
           {name:'telephone'}, 
           {name:'join_time'},
           {name:'address'},
           {name:'app_name'},
           {name:'province'},
           {name:'city'},
           {name:'fensiTotal'}
        ]  
    });
 // app类型
	var store_appType = Ext.create("Ext.data.Store", {
		pageSize : 20, // 每页显示几条数据
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
			}]
		});
	 
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var name=Ext.getCmp('name').getValue();
		 var province=Ext.getCmp('province').getRawValue();
		 var city=Ext.getCmp('city').getRawValue();
		 var app_type=Ext.getCmp('app_type').getValue();
		 var telephone=Ext.getCmp('telephone').getValue();
		 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
		 var new_params = { name:name,province:province,city:city,app_type:app_type,start_time : start_time,off_time : off_time,telephone:telephone};    
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
        	       
        	    	 <cms:havePerm url='/merchants/addMerchantsInfoForFensi'>
                   { xtype: 'button', id:'add', text: '添加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   editform();
        	    		   }
        	    	   }},'-',</cms:havePerm>
        	    	 
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id: 'name',name: 'name',fieldLabel: '服务商名称',labelAlign:'left',labelWidth:70},'-',
        	       
					{ xtype: 'textfield',id: 'telephone',name: 'telephone',fieldLabel: '注册号码',labelAlign:'left',labelWidth:70},'-',
        	      	{xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省份',valueField : 'id',displayField : 'area',
							store : provinceStore,
							listeners : { // 监听该下拉列表的选择事件
																select : function(
																		combobox,
																		record,
																		index) {
																	Ext
																			.getCmp(
																					'city')
																			.setValue('');
																	cityStore.load({params : {
																					parentId : combobox.value
																				}
																			});
																}
															},
							queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
				   {xtype : 'combobox',id : 'city',name : 'city',fieldLabel : '城市',valueField : 'id',displayField : 'area',
							store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',	
							]
		},	{
			xtype : 'toolbar',
			dock : 'top',
			displayInfo : true,
			items : [ 
               { xtype: 'datetimefield',id: 'start_time',name: 'start_time',fieldLabel: '开始时间',format : 'Y-m-d',value:'${start_time}',labelAlign:'left',labelWidth:70},'-',
               { xtype: 'datetimefield',id: 'off_time',name: 'off_time',fieldLabel: '结束时间',format : 'Y-m-d',labelAlign:'left',labelWidth:70},'-',
	
							{xtype : 'combobox',id : 'app_type',name : 'appType',fieldLabel : 'app类型',value:'',valueField : 'app_type',displayField : 'app_name',
								store : store_appType,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
							<cms:havePerm url='/merchants/getMerchantsInfoListForFensi'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var name=Ext.getCmp('name').getValue();
    	    			   var province=Ext.getCmp('province').getRawValue();
    	    			   var city=Ext.getCmp('city').getRawValue();
    	    			   var app_type=Ext.getCmp('app_type').getValue();
    	    			   var telephone=Ext.getCmp('telephone').getValue();
    	    			   var start_time=Ext.getCmp('start_time').getValue();
    	    			   var off_time=Ext.getCmp('off_time').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20, name:name,province:province,city:city,app_type:app_type,start_time : start_time,off_time : off_time,telephone:telephone}}); 
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
    //加载数据  
    provinceStore.load();
    store_appType.load();
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
   
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
  
		
   //form表单编辑服务商
 	var editform=function(){  
 	    var edit_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 440,
 	                height:450,
 	                autoScroll:true,
 	                bodyPadding:5,  
 	                //renderTo:"panel21",  
 	                fieldDefaults: {  
 	                    labelAlign: 'left',  
 	                    labelWidth: 90,  
 	                    anchor: '100%'  
 	                },  
 	                items: [{  
 	                    //显示文本框，相当于label  
 	                    xtype: 'displayfield',   
 	                    name: 'displayfield1',  
// 	                    fieldLabel: 'Display field',  
 	                    value: '修改服务商相关信息'  
 	                     
 	                },{  
 	                    //输入服务商ID
 	                    xtype: 'textfield', 
 	                    name: 'id', 
 	                    hidden:true,
 	                    fieldLabel: '服务商ID'  
 	                },{  
 	                    //输入服务商名称
 	                    xtype: 'textfield', 
 	                    name: 'name',
 	                    fieldLabel: '服务商名称'  
 	                },{
 	   				// 输入要增加的粉丝量
 	   				xtype : 'numberfield',
 	   				name : 'fensiTotal',
 	   				allowDecimals: false,
 	   				minValue: 0,
 	   				maxValue: 1000,
 	   				allowBlank	:false,
 	   				fieldLabel : '粉丝量'
 	   			}
 	              ]  
 	            });  
 	    //创建window面板，表单面板是依托window面板显示的  
 	    
 	    var rows = grid.getSelectionModel().getSelection();  
         //user_id：所有选中的服务商Id的集合使用','隔开，初始化为空    
 	    if(rows.length == 0)  
         {  
 	    	 Ext.example.msg("提示","请选择要增加粉丝的店铺！");
             return ;  
         }
 	   if(rows.length > 1)  
       {  
 		  Ext.example.msg("提示","只能选择一个增加的店铺！");
          return ;  
       }
 	 
 	   edit_winForm.form.findField('id').setValue(rows[0].get('id'));  
       edit_winForm.form.findField('name').setValue(rows[0].get('name'));
       
 	    var editwindow = Ext.create('Ext.window.Window',{  
 	              title : "增加粉丝",  
 	              width: 450,
	              height:400,  
 	              //height : 120,  
 	              //plain : true,  
 	              iconCls : "addicon",  
 	              // 不可以随意改变大小  
 	              resizable : true,  
 	              // 是否可以拖动  
 	              draggable:true,  
 	              collapsible : true, // 允许缩放条  
 	              closeAction : 'close',  
 	              closable : true,  
 	              // 弹出模态窗体  
 	              modal : 'true',  
 	              buttonAlign : "center",  
 	              bodyStyle : "padding:0 0 0 0",  
 	              items : [edit_winForm],  
 	              buttons : [{  
 	                         text : "保存",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                            if (edit_winForm.getForm().isValid()) {  
 	                            	edit_winForm.getForm().submit({  
 	                                          url :'/merchants/addMerchantsInfoForFensi',  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',                                    
 	                                          success: function(fp, o) {  
 	                                              if (o.result== true) {
 	                                        		  Ext.example.msg("提示","增加成功！");
 	                                                  editwindow.close(); //关闭窗口  
 	                                                  store.reload();  
 	                                              }else {  
 	                                       		      Ext.example.msg("提示","增加失败！");
 	                                              }  
 	                                          },  
 	                                          failure: function() { 
 	                                             Ext.example.msg("提示","增加失败！");
 	                                          }  
 	                                       });  
 	                              
 	                         } }
 	                     }, {  
 	                         text : "关闭",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	 editwindow.close();  
 	                         }  
 	                     }]  
 	           });  
 	    editwindow.show();  
 	    
 	    };
 	   
 		 
 		
});


</script>

</head>
<body>
</body>
</html>