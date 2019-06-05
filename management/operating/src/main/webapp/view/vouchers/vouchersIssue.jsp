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
<title>代金卷发放记录</title>
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
                   {header:'编号',dataIndex:'id',hidden: true,align:'center'},
                   {header:'代金券名称',dataIndex:'coupons_name',align:'center'},
                   {header:'价格',dataIndex:'price',align:'center'},
                   {header:'发放时间',dataIndex:'issue_time',align:'center'},
                   {header:'发放人数',dataIndex:'issue_total',align:'center'}   
               ];
	
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/vouchers/getIssueVouchers',
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'coupons_name'},
           {name:'price'},
           {name:'issue_time'},
           {name:'issue_total'}
        ]  
    });
 
	
	
	
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	   var coupons_name=Ext.getCmp('coupons_name').getValue();
		 //var usergrade=Ext.getCmp('usergrade').getValue();
		   var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
        var new_params = {coupons_name:coupons_name,start_time:start_time,off_time:off_time};    
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
        	        <cms:havePerm url='/vouchers/IssueVouchers'>
        	       { xtype: 'button',id:'issue', text: '发放代金券',iconCls:'Fafang',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform();
        	    		   }
        	    	   }
        	       },'-',</cms:havePerm>
        	       <cms:havePerm url='/vouchers/deleteVoucherIssue'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	    	   }}</cms:havePerm>
                 ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'coupons_name',name: 'coupons_name',fieldLabel: '代金券名',labelAlign:'left',labelWidth:60},'-',
                   {xtype : 'datetimefield',id : 'start_time',name : 'start_time',format : 'Y-m-d',fieldLabel : '开始时间',labelAlign : 'left',labelWidth : 60},'-',
			       {xtype : 'datetimefield',id : 'off_time',name : 'off_time',format : 'Y-m-d',fieldLabel : '结束时间',labelAlign : 'left',labelWidth : 60},'-',
                   <cms:havePerm url='/vouchers/getIssueVouchers'>
                   { xtype: 'button', text: '查询',iconCls:'Usermagnify',listeners: {
    	    		   click:function(){
    	    			   var coupons_name=Ext.getCmp('coupons_name').getValue();
    	    			  // var usergrade=Ext.getCmp('usergrade').getValue();
    	    			    var start_time=Ext.getCmp('start_time').getValue();
		                    var off_time=Ext.getCmp('off_time').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({start:0,params:{page:1,limit:20,coupons_name:coupons_name,start_time:start_time,off_time:off_time}}); 
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
    
   
   
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	//订单删除
	 function delUserAll()  
    {  
         var rows = grid.getSelectionModel().getSelection();  
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
                        url:"/vouchers/deleteVoucherIssue",  
                        params:{id:id},  
                        method:'post',  
                        success:function(o){  
                        	Ext.example.msg("提示","代金券删除成功！"); 
                            store.reload();  
                            return ;  
                        },  
                        failure:function(form,action){  
                         	Ext.example.msg("提示","代金券删除失败！"); 
                        }  
                    });    
                }  
            });  
         }  
    } ;
	
  
    //发放代金券选择会员
	var showform=function(){  
		
		var columns = [
	                   {header:'序号',xtype: 'rownumberer',width:50},
	                   {header:'编号',dataIndex:'id'},
	                   {header:'会员',dataIndex:'name'},
	                   {header:'手机号',dataIndex:'phone'}
	               ];
		
	    var storeUser = Ext.create("Ext.data.Store",{
	    	pageSize:20, //每页显示几条数据  
	    	remoteSort: true,
	        proxy:{  
	            type:'ajax',  
	            url:'/user/showUser',
	            reader:{  
	                type:'json',  
	                totalProperty:'total',  
	                root:'data',  
	                idProperty:'#'  
	            }  
	        },  
	        fields:[  
	           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
	           {name:'name'},
	           {name:'phone'}
	        ]  
	    });
	    var sm = Ext.create('Ext.selection.CheckboxModel');
	    var gridUser = Ext.create("Ext.grid.Panel",{
	    	region: 'center',
	    	border: false,
	    	store: storeUser,
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
	        	items:[{ xtype: 'textfield',name: 'phone',fieldLabel: '手机号',labelAlign:'left',labelWidth:55},
	                  
	                   { xtype: 'button', text: '查询',iconCls:'Usermagnify',listeners: {
	    	    		   click:function(){
	    	    			   var phone= gridUser.dockedItems.items[1].items.items[0].rawValue;
	    	    			   storeUser.currentPage = 1;
	    	    			   storeUser.load({params:{start:0,page:1,limit:20,phone:phone}}); 
	    	    		   }
	    	    		   }}]
	        },{
	            xtype: 'pagingtoolbar',
	            store: storeUser,   // GridPanel使用相同的数据源
	            dock: 'bottom',
	            displayInfo: true,
	            plugins: Ext.create('Ext.ux.ProgressBarPager'),
	            emptyMsg: "没有记录" //没有数据时显示信息
	        }]
	    });
	  //加载数据  
	    storeUser.load({params:{start:0,limit:20}}); 
	    var syswin = Ext.create('Ext.window.Window',{  
	              title : "选择会员",  
	              width : 700, 
	              height: 500,
	              //height : 120,  
	              //plain : true,  
	              iconCls : "addicon",  
	              // 不可以随意改变大小  
	              resizable : false,  
	              autoScroll: true,
	              // 是否可以拖动  
	              // draggable:false,  
	             // collapsible : true, // 允许缩放条  
	              closeAction : 'close',  
	              closable : true,  
	              // 弹出模态窗体  
	              modal : 'true',  
	              buttonAlign : "center",  
	              bodyStyle : "padding:0 0 0 0",  
	              items : [gridUser],  
	              buttons : [{  
	                         text : "发放",  
	                         minWidth : 70,  
	                         handler : function() {
	                        	issueVouchers();
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
	    
	  //选择代金券
		var issueVouchers = function(){  
			var columns = [ {header:'序号',xtype: 'rownumberer',width:50},
				{header : '编号',dataIndex : 'id',hidden:true},
				{header : '代金券类型',dataIndex : 'coupons_type_name'}, 
				{header : '代金券金额',dataIndex : 'price'}, 
			    {header : '截止时间',dataIndex : 'cutoff_time'}
			];
		//创建store数据源

		//列表展示数据源
		var storeVoucher = Ext.create("Ext.data.Store", {
			pageSize : 20, //每页显示几条数据  
			proxy : {
				type : 'ajax',
				url : '/vouchers/getCanIsuseVouchers',
				reader : {
					type : 'json',
					totalProperty : 'total',
					root : 'data',
					idProperty : '#'
				}
			},
			fields : [ {
				name : 'id'
			}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
			{
				name : 'coupons_type_name'
			}, {
				name : 'price'
			}, {
				name : 'cutoff_time'
			}]
		});
		
		// 服务类型
		var store_ServiceType = Ext.create("Ext.data.Store", {
			pageSize : 20, // 每页显示几条数据
			proxy : {
				type : 'ajax',
				url : '/common/showServiceType',
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
	    storeVoucher.on('beforeload', function (store, options) {    
	    	var date= gridVoucher.dockedItems.items[1].items.items;
			
				var price = date[1].rawValue;
				var start_time=date[2].rawValue;
				var off_time=date[3].rawValue;
				var is_del=0;
	        var new_params = {
	        		start_time : start_time,off_time : off_time,is_del : is_del,
					price : price};    
	        Ext.apply(store.proxy.extraParams, new_params);    
	    });
	    var gridVoucher = Ext.create("Ext.grid.Panel", {
			region : 'center',
			border : false,
			store : storeVoucher,
			selModel: Ext.create("Ext.selection.CheckboxModel", {
	    	    mode: "single"
	    	}),
			columns : columns,
			region : 'center', //框架中显示位置，单独运行可去掉此段
			loadMask : true, //显示遮罩和提示功能,即加载Loading……  
			forceFit : true, //自动填满表格  
			columnLines : false, //列的边框
			rowLines : true, //设置为false则取消行的框线样式
			dockedItems : [ {
				xtype : 'toolbar',
				dock : 'top',
				displayInfo : true,
				items : [{
					xtype : 'textfield',
					name : 'service_type_name',
					fieldLabel : '服务名称',
					labelAlign : 'left',
					labelWidth : 60
				}, {
					xtype : 'textfield',
					name : 'price',
					fieldLabel : '金额',
					labelAlign : 'left',
					labelWidth : 45
				}, {
					xtype : 'datetimefield',
					name : 'start_time',
					format : 'Y-m-d',
					fieldLabel : '开始时间',
					labelAlign : 'left',
					labelWidth : 60
				},
				{
					xtype : 'datetimefield',
					name : 'off_time',
					format : 'Y-m-d',
					fieldLabel : '结束时间',
					labelAlign : 'left',
					labelWidth : 60
				},
				{
					xtype : 'button',
					text : '查询',
					iconCls : 'Usermagnify',
					listeners : {
						click : function() {
							var date= gridVoucher.dockedItems.items[1].items.items;
	    	    			var service_type_name =date[0].value;
	   						var price = date[1].rawValue;
	   						var is_del=0;
	   						var start_time=date[2].rawValue;
	   						var off_time=date[3].rawValue;
	   						storeVoucher.currentPage = 1;
	   						storeVoucher.load({
	   							params : {
	   								start:0,
	   								page : 1,
	   								limit : 20,
	   								service_type_name : service_type_name,
	   								start_time : start_time,
	   								off_time : off_time,
	   								price : price,
	   								is_del:is_del
	   							}
	   						});
						}
					}
				} 
				]
			}, {
				xtype : 'pagingtoolbar',
				store : storeVoucher, // GridPanel使用相同的数据源
				dock : 'bottom',
				displayInfo : true,
				plugins : Ext.create('Ext.ux.ProgressBarPager'),
				emptyMsg : "没有记录" //没有数据时显示信息
			} ]
		});

		//加载数据  
		storeVoucher.load({
			params : {
				start : 0,
				limit : 20
			}
		});
		
 
		var issuewindow = Ext.create('Ext.window.Window',{  
            title : "选择代金券",  
            width: 1200,
            height:500,  
            //height : 120,  
            //plain : true,  
            iconCls : "addicon",  
            // 不可以随意改变大小  
            resizable : true,  
            // 是否可以拖动  
            draggable:true, 
            autoScroll: true,
           // collapsible : true, // 允许缩放条  
            closeAction : 'close',  
            closable : true,  
            // 弹出模态窗体  
            modal : 'true',  
            buttonAlign : "center",  
            bodyStyle : "padding:0 0 0 0",  
            items : [gridVoucher],  
            buttons : [{  
                       text : "发放",  
                       minWidth : 70,  
                       handler : function() {
                      	 var rowsVoucher = gridVoucher.getSelectionModel().getSelection();  
                      	 var coupons_name=rowsVoucher[0].get('coupons_type_name');
                      	 var voucher_id=rowsVoucher[0].get('id');
                      	 var price=rowsVoucher[0].get('price');
                      	 var rows = gridUser.getSelectionModel().getSelection();  
                      	 var userid='';
                      	 
                      	 for(var i = 0;i<rows.length;i++)  
	                            {  
	                               if(i>0)  
	                               {  
	                            	   userid = userid+','+rows[i].get('id');  
	                            	
	                               }else{  
	                            	   userid = userid+rows[i].get('id');  
	                            	  
	                               }  
	                            }  
                      	 Ext.Ajax.request({  
                      		 url :'/vouchers/IssueVouchers',  
                      		 params:{userid:userid,coupons_name:coupons_name,voucher_id:voucher_id,price:price},  
                               method:'post',  
                               success:function(o){  
                                   Ext.example.msg("提示","发放成功！"); 
                                   issuewindow.close();
                                   store.reload();  
                                   return ;  
                               },  
                               failure:function(form,action){  
                                   Ext.example.msg("提示","发放失败！"); 
                               }  
                           }); 
                      	 }   
            } ,{
				text : "关闭",
				minWidth : 70,
				handler : function() {
					issuewindow.close();
				}
			}]                  		
         });   
		    issuewindow.show();  
		    };
	    
	    };
    
	    
	    
});
</script>
</head>
<body>
</body>
</html>