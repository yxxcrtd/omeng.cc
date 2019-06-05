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
<title>用户反馈管理</title>
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
var store;
var grid;
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'编号',dataIndex:'id',hidden:true,align:'center'},
                   {header:'用户ID',dataIndex:'customer_id',hidden:true,align:'center'},
                   {header:'用户名',dataIndex:'name',align:'center'},
                   {header:'联系方式',dataIndex:'phone',align:'center'},
                   {header:'用户类型',dataIndex:'customer_type',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='font-weight:bold';>商户</span>";  
                       } else if(value=='2'){  
                           return "<span style='font-weight:bold';>用户</span>";  
                       }
           		   }},
           		   {header:'反馈类型',dataIndex:'feedback_type',align:'center',renderer:function(value){  
                    if(value=='1'){  
                        return "<span style='font-weight:bold';>建议</span>";  
                    } else if(value=='2'){  
                        return "<span style='font-weight:bold';>咨询</span>";  
                    }else if(value=='3'){  
                        return "<span style='font-weight:bold';>故障</span>";  
                    }else if(value=='4'){  
                        return "<span style='font-weight:bold';>新需求</span>";  
                    }else if(value=='5'){  
                        return "<span style='font-weight:bold';>闪退、卡顿或界面错位</span>";  
                    }
        		   }},
                   {header:'内容',dataIndex:'content'},
                   {header:'app类型',dataIndex:'app_name',align:'center',hidden:true},
                   {header:'反馈时间',dataIndex:'feedback_time',align:'center'},
                   {header:'反馈状态',dataIndex:'status',align:'center',renderer:function(value,v,r){  
                	   if(value=='1'){  
                           return "<span style='font-weight:bold';>已处理</span>";  
                       } else{  
                    	   return '<a href="javascript:DealWith(\''+r.data.id+'\','+value+')">处理</a>'; 
                       }
             		}},
                   {header:'操作',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                       return '<a href="javascript:showDetail()">详情</a>';
             		}}
               ];
	
     store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/user/showUserFeedBack',
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'customer_id'},
           {name:'name'},
           {name:'phone'},
           {name:'customer_type'},
           {name:'customer_type'},
           {name:'feedback_type'},
           {name:'content'},   
           {name:'app_name'},
           {name:'status'},
           {name:'feedback_time'}
           
        ]  
    });
 // app类型
	var store_appType = Ext.create("Ext.data.Store", {
		pageSize : 20, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/showAppType',
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
 //反馈类型
	var feedbackTypeStore = new Ext.data.SimpleStore({
     	fields:['type','value'],
     	data:[['','全部'],
     	      ['1','建议'],
     	      ['2','咨询'],
     	      ['3','故障'],
     	      ['4','新需求'],
     	      ['5','闪退、卡顿或界面错位']
     	]
     });
	//用户类型 
	var customerTypeStore = new Ext.data.SimpleStore({
     	fields:['type','value'],
     	data:[['1','商户'],
     	      ['2','用户']
     	]
     });
	//反馈状态 
	var feedBackStatusStore = new Ext.data.SimpleStore({
     	fields:['type','value'],
     	data:[['','全部'],
     	      ['0','未处理'],
     	      ['1','已处理']
     	]
     });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var customer_type=Ext.getCmp('customer_type').getValue();
    	 var phone=Ext.getCmp('phone').getValue();
    	 var feedback_type=Ext.getCmp('feedback_type').getValue();
    	 var status=Ext.getCmp('status').getValue();
    	 var customer_id=Ext.getCmp('customer_id').getValue();
    	 var app_type=Ext.getCmp('app_name').getValue();
    	 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
         var new_params = {customer_type:customer_type,status:status,feedback_type:feedback_type,customer_id:customer_id,app_type:app_type,phone:phone,start_time : start_time,off_time : off_time};    
        Ext.apply(store.proxy.extraParams, new_params);    
    });
    var sm = Ext.create('Ext.selection.CheckboxModel');
    grid = Ext.create("Ext.grid.Panel",{
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
        	       <cms:havePerm url='/user/deletetUserFeedBack'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	    	   }},</cms:havePerm>
        	    	   <cms:havePerm url='/user/userFeedBackExport'>
	        	       { xtype: 'button', text: '导出',iconCls:'Daochu',
	            	    	   listeners: {
	            	    		   click:function(){
	            	    			   exportAll();
	            	    		   }
	            	    	   }
	                   },'-'</cms:havePerm>
                 ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id: 'customer_id',name: 'customer_id',fieldLabel: '用户ID',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'textfield',id: 'phone',name: 'phone',fieldLabel: '联系方式',labelAlign:'left',labelWidth:60},'-',
        	       {xtype : 'combobox',id : 'customer_type',name : 'customer_type',fieldLabel : '用户类型 ',value:'2',valueField : 'type',editable:false,displayField : 'value',
				   store : customerTypeStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60,hidden:true},'-',
        	       {xtype : 'combobox',id : 'feedback_type',name : 'feedback_type',fieldLabel : '反馈类型',value:'',valueField : 'type',editable:false,displayField : 'value',
				   store : feedbackTypeStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
        	       	]
					},
					{
			xtype : 'toolbar',
			dock : 'top',
			displayInfo : true,
			items : [ {xtype : 'combobox',id : 'status',name : 'status',fieldLabel : '反馈状态',value:'',valueField : 'type',editable:false,displayField : 'value',
				      store : feedBackStatusStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
			          {xtype : 'combobox',id : 'app_name',name : 'app_name',fieldLabel : 'app类型',valueField : 'app_type',displayField : 'app_name',
				       store : store_appType,editable:false,queryMode : 'local',labelAlign : 'left',labelWidth : 60,hidden:true
			           },'-',
        	       { xtype: 'datetimefield',id: 'start_time',name: 'start_time',format : 'Y-m-d',fieldLabel: '开始时间',value:'${start_time}',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'datetimefield',id: 'off_time',name: 'off_time',format : 'Y-m-d',fieldLabel: '结束时间',labelAlign:'left',labelWidth:60},'-',
        	       <cms:havePerm url='/user/showUserFeedBack'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var customer_type=Ext.getCmp('customer_type').getValue();
    	    			   var phone=Ext.getCmp('phone').getValue();
    	    		       var feedback_type=Ext.getCmp('feedback_type').getValue();
    	    		       var status=Ext.getCmp('status').getValue();
    	    		       var customer_id=Ext.getCmp('customer_id').getValue();
    	    			   var app_type=Ext.getCmp('app_name').getValue();
    	    			   var start_time=Ext.getCmp('start_time').getValue();
    	    			   var off_time=Ext.getCmp('off_time').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,start:0,status:status,phone:phone,customer_type:customer_type,feedback_type:feedback_type,customer_id:customer_id,app_type:app_type,start_time : start_time,off_time : off_time}}); 
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
    // 表格配置结束
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    grid.on("itemdblclick",function(grid, row){
    	//Ext.Msg.alert("系统提示","你双击啦！ID为："+row.data.id);  
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
                        url:"/user/deletetUserFeedBack",  
                        params:{id:id},  
                        method:'post',  
                        success:function(o){  
                        	Ext.example.msg("提示","反馈删除成功！");
                            store.reload();  
                            return ;  
                        },  
                        failure:function(form,action){  
                        	Ext.example.msg("提示","反馈删除失败！");
                        }  
                    });    
                }  
            });  
         }  
    } ;
	  
    //导出所有商户
	function exportAll() {
		   var customer_type=Ext.getCmp('customer_type').getValue();
		   var phone=Ext.getCmp('phone').getValue();
	       var feedback_type=Ext.getCmp('feedback_type').getValue();
	       var customer_id=Ext.getCmp('customer_id').getValue();
		   var app_type=Ext.getCmp('app_name').getValue();
		   var off_time=Ext.getCmp('off_time').getValue();
		   var start_time=Ext.getCmp('start_time').getValue();
		   var status=Ext.getCmp('status').getValue();
           window.location.href = '/user/userFeedBackExport?customer_type='+customer_type+'&start_time='+start_time+'&off_time='+off_time
           +'&status='+status+'&feedback_type='+feedback_type+'&customer_id='+customer_id+'&app_type='+app_type+'&phone='+phone;
	};
    
});

function showDetail(){
	 var rows = grid.getSelectionModel().getSelection();  
	    //user_id：所有选中的服务商Id的集合使用','隔开，初始化为空    
	    if(rows.length == 0)  
	    {  
	  	    Ext.example.msg("提示","请选择要查看的对象！");
	       return ;  
	    }
	   if(rows.length > 1)  
	   {  
		  Ext.example.msg("提示","只能选择一个查看的对象！");
	     return ;  
	    }
	  var feedBackId = rows[0].get('id');
      var phone =rows[0].get('phone'); 
      var album = rows[0].raw.album;
	  parent.removeTab("feedback_detail_");
	  parent.addTab("feedback_detail_","用户反馈详情","","/user/feedBackDetail?feedBackId="+feedBackId+"&phone="+phone+"&album="+album); 	 
	 
}


var DealWith=function(id,value){

	Ext.Msg.confirm("提示信息", "确定处理此条记录吗", function(btn) {
		if (btn == 'yes') {
			Ext.Ajax.request({
				url : "/user/dealWithFeedBack",
				params : {
					id : id
				},
				method : 'post',
				success : function(o) {
				    Ext.example.msg("提示","处理成功"); 
					store.reload();
					return;
				},
				failure : function(form, action) {
				    Ext.example.msg("提示","处理失败"); 
				}
			});
		}
	});
};
</script>

</head>
<body>
</body>
</html>