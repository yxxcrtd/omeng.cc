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
<title>订单推送充值申请</title>
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
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'编号',dataIndex:'id',hidden:true,align:'center'},
                   {header:'服务商名称',dataIndex:'merchantName',align:'center'},
                   {header:'联系方式',dataIndex:'telephone',width:80,align:'center'},
                   {header:'地址',dataIndex:'address',align:'center'},
                   {header:'省份',dataIndex:'province',width:40,align:'center'},
                   {header:'城市',dataIndex:'city',width:50,align:'center'},
                   {header:'app类型',dataIndex:'appName',width:50,align:'center'},
                   {header:'申请时间',dataIndex:'applyTime',align:'center'},
                   {header:'申请额度',dataIndex:'applyNum',width:40,align:'center'},
                   {header:'确认时间',dataIndex:'confirmTime',hidden:true,align:'center'},
                   //{header:'确认用户',dataIndex:'confirmUser'},
                   {header:'开通时间',dataIndex:'openTime',align:'center'},
                   //{header:'开通用户',dataIndex:'openUser'},
                   //{header:'过期时间',dataIndex:'failureTime'},
                   {header:'状态',dataIndex:'applyStatus',sortable:true,width:50,align:'center',fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:orangered;font-weight:bold';>已确认</span>";  
                       }else if(value=='2'){
                           return "<span style='color:green;font-weight:bold';>已开通</span>";  
                       }else if(value=='3'){
                           return "<span style='color:darkblue;font-weight:bold';>无效</span>";  
                       }else if(value=='4'){
                           return "<span style='color:purple;font-weight:bold';>已过期</span>";  
                       }else{
                    	   return "<span style='color:red;font-weight:bold';>待开通</span>"; 
                       }
           		   }},
           	      // <cms:havePerm url='/valueAdded/confirmOrderPushStatus'>
                   //{id: 'confirm',  header: '确认收款',  dataIndex: 'confirm', width:60, sortable:true,fixed:false,renderer:function(v,d,r){
                	//   var str = '';
                	//   var status = r.data.applyStatus;
                	//   var num = r.data.applyNum;
                	//   if(status=='0'){
                		   //待确认收款
                	//	   str = '<a  href="javascript:void(0);" onclick="confirmApply('+r.data.id+',\''+status+'\',\''+num+'\')">收款</a>';
                	  // }
                   	 //  return str;
                  // }},
       	          // </cms:havePerm>
                   <cms:havePerm url='/valueAdded/closeOrderPushStatus'>
                   {id: 'close',  header: '关闭',  dataIndex: 'close', width:60,align:'center', sortable:true,fixed:false,renderer:function(v,d,r){
                	   var str = '';
                	   var status = r.data.applyStatus;
                       if(status=='0'){
                		   //关闭
                		   str = '<a href="javascript:void(0);" onclick="closeApply('+r.data.id+',\''+status+'\')">关闭</a>';
                	   }
                   	   return str;
                   }},
                   </cms:havePerm>
                   <cms:havePerm url='/valueAdded/openOrderPushStatus'>
                   {id: 'open',  header: '开通功能',  dataIndex: 'open', width:60,align:'center', sortable:true,fixed:false,renderer:function(v,d,r){
                	   var str = '';
                	   var status = r.data.applyStatus;
                       if(status=='0'){
                		   //待开通
                		   str = '<a href="javascript:void(0);" onclick="openApply('+r.data.id+',\''+status+'\')">开通</a>';
                	   }
                   	   return str;
                   }},
                   </cms:havePerm>
                   {header:'操作',dataIndex:'last_login_time',hidden:true,align:'center'}
               ];
	
        store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true, //设置属性进行请求后台排序
        proxy:{  
            type:'ajax',  
            url:'/valueAdded/orderPushApplyList',
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
                {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
                {name:'merchantName'},
                {name:'telephone'},
                {name:'address'},
                {name:'province'},
     		    {name:'city'},
                {name:'appName'},
                {name:'appType'},
                {name:'applyTime'},
     		    {name:'confirmTime'},
     		    {name:'confirmUser'},
                {name:'openTime'},
         	    {name:'openUser'},
         	    {name:'applyNum'},
         	    {name:'failureTime'},
                {name:'applyStatus'}
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
    var applyStatusStore = new Ext.data.SimpleStore({
    	// 0-待确认 1-已确认 2-已开通 3-无效（代理审核为无效）4-已过期（开通一年到期）
    	    fields:['name','value'],
    	    data:[['全部',''],
    	          ['待开通','0'],
    	       //   ['已确认','1'],
    	          ['已开通','2'],
    	          ['无效','3']
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
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var merchantName=Ext.getCmp('merchantName').getValue();
		var telephone=Ext.getCmp('telephone').getValue();
		var province=Ext.getCmp('province').getRawValue();
		var city=Ext.getCmp('city').getRawValue();
		var applyStatus=Ext.getCmp('applyStatus').getValue();
		var appType=Ext.getCmp('appType').getValue();
		var stime=Ext.getCmp('stime').getValue();
		var etime=Ext.getCmp('etime').getValue();
        var new_params = {merchantName:merchantName,telephone:telephone,
		           province:province,city:city,applyStatus:applyStatus,appType:appType,stime:stime,etime:etime};    
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
           <cms:havePerm url='/valueAdded/deleteOrderPushApply'>
            {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:['-',
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delRecord();
        	    		   }
        	       }},'-',
        	       <cms:havePerm url='/valueAdded/exportPushApplyExcel'>
         	       { xtype: 'button', text: '导出',iconCls:'Daochu',
             	    	   listeners: {
             	    		   click:function(){
             	    			   exportAll();
             	    		   }
             	    	   }
                    }</cms:havePerm>
                 ],
           },
        </cms:havePerm>
        {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'merchantName',name: 'merchantName',fieldLabel: '服务商名称',labelAlign:'left',labelWidth:70},'-',
        	       { xtype: 'textfield',id:'telephone',name: 'telephone',fieldLabel: '联系方式',labelAlign:'left',labelWidth:60},'-',
        	       {
		                  xtype:'combo',
		                  id:'applyStatus',
		                  store : applyStatusStore,
		                   name:'applyStatus',
			               triggerAction: 'all',
			               hiddenName:'value',
			               displayField: 'name',
			               valueField: 'value',
			               value:'',
			   		       labelWidth:60,
		                   mode:'local',
		                   fieldLabel: '状态' 
	              },'-',
	               {
						xtype : 'combobox',
						name : 'appType',
						id: 'appType',
						fieldLabel : 'app类型',
						valueField : 'app_type',
						displayField : 'app_name',
						store : store_appType,
						allowBlank	:false,
						editable:false,
						hiddenName:'',
						labelWidth:60,
						value:'',
						allowBlank	:false,
						queryMode : 'local'
				   }
                   ]
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
                {
				xtype : 'combobox',
				id : 'province',
				name : 'province',
				fieldLabel : '省份',
				valueField : 'id',
				hiddenName:'id',
				labelWidth:60,
				displayField : 'area',
				store : provinceStore,
				listeners : { // 监听该下拉列表的选择事件
					select : function(combobox,record,index) {
						Ext.getCmp('city').setValue('');
						cityStore.load({
									params : {
										parentId : combobox.value
									}
								});
					}
				},
				queryMode : 'local',
				labelAlign : 'left'
			     },'-',
			     {
						xtype : 'combobox',
						name : 'city',
						id : 'city',
						labelWidth:60,
						fieldLabel : '城市',
						valueField : 'id',
			   			hiddenName:'id',
						displayField : 'area',
						store : cityStore,
						queryMode : 'local',
						labelAlign : 'left'
				 },'-',
				   {
						xtype : 'datetimefield',
						id : 'stime',
						name : 'stime',
						format : 'Y-m-d',
						fieldLabel : '开始时间',
						labelAlign : 'left',
						labelWidth : 60
					},'-', 
					{
						xtype : 'datetimefield',
						id : 'etime',
						name : 'etime',
						format : 'Y-m-d',
						fieldLabel : '结束时间',
						labelAlign : 'left',
						labelWidth : 60
					},'-', 
				    <cms:havePerm url='/valueAdded/orderPushApplyList'>
                   { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
      	    			   var merchantName=Ext.getCmp('merchantName').getValue();
    	    			   var telephone=Ext.getCmp('telephone').getValue();
      	    			   var province=Ext.getCmp('province').getRawValue();
    	    			   var city=Ext.getCmp('city').getRawValue();
    	    			   var applyStatus=Ext.getCmp('applyStatus').getValue();
    	    			   var appType=Ext.getCmp('appType').getValue();
    	    			   var stime=Ext.getCmp('stime').getValue();
    	    			   var etime=Ext.getCmp('etime').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,merchantName:merchantName,telephone:telephone,
    	    				           province:province,city:city,applyStatus:applyStatus,appType:appType,stime:stime,etime:etime}}); 
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
    store_appType.load();
    provinceStore.load();
   // cityStore.load();
    
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
	

	 //导出所有关键词
	function exportAll() {
		   var merchantName=Ext.getCmp('merchantName').getValue();
		   var telephone=Ext.getCmp('telephone').getValue();
		   var province=Ext.getCmp('province').getRawValue();
		   var city=Ext.getCmp('city').getRawValue();
		   var applyStatus=Ext.getCmp('applyStatus').getValue();
		   var appType=Ext.getCmp('appType').getValue();
		   var stime=Ext.getCmp('stime').getValue();
		   var etime=Ext.getCmp('etime').getValue();
		
         window.location.href = '/valueAdded/exportPushApplyExcel?merchantName='+merchantName+'&telephone='+telephone+'&province='+province
			+'&city='+city+'&applyStatus='+applyStatus+'&appType='+appType+'&stime='+stime+'&etime='+etime;
	};
	
	
    
	//删除
   function delRecord()  
   {  
        //grid中复选框被选中的项  
        var rows = grid.getSelectionModel().getSelection();     
        var ids = '';  
        for(var i = 0;i<rows.length;i++)  
        {  
           var status = rows[i].get('applyStatus');	
           if(status=='0'||status=='1'||status=='2'){
        	   //无效或过期
        	   Ext.example.msg("提示","选择删除的记录必须是【无效】或【过期】数据！");
               return ;  
           }
           if(i>0)  
           {  
               ids = ids+','+rows[i].get('id');  
           }else{  
               ids = ids+rows[i].get('id');  
           }  
        }  
        //没有选择要执行操作的对象  
        if(ids == "")  
        { 
     	   Ext.example.msg("提示","请选择要删除的对象！");
           return ;  
        }else{  
           Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
               if(btn == 'yes')  
               {  
                   Ext.Ajax.request({  
                       url:"/valueAdded/deleteOrderPushApply",  
                       params:{ids:ids},  
                       method:'post',  
                       success:function(o){  
                    	   Ext.example.msg("提示","删除成功！");
                           store.reload();  
                           return ;  
                       },  
                       failure:function(form,action){  
                    	   Ext.example.msg("提示","删除失败！");
                       }  
                   });    
               }  
           });  
        }  
   };    
});

function storeReload(){ 
	store.reload();
 }   
function confirmApply(id,status,num){
	var str = "确认已收到商家增值服务费<span style='color:red;font-weight:bold';>"+num+"</span>元?";
    Ext.Msg.confirm("提示信息",str,function (btn){  
        if(btn == 'yes')  
        {  
            Ext.Ajax.request({  
                url:"/valueAdded/confirmOrderPushStatus",  
                params:{id:id,status:1,num:num},  
                method:'post',  
                success:function(o){  
                  	var res = eval(o.responseText);
                	if(res=='1'){
                   	    Ext.example.msg("提示","收款成功！");
                        window.setTimeout("storeReload()", 1000);  
                        return ;  
                	}else if(res=='2'){
                  	    Ext.example.msg("提示","余额不足，请充值！");
                        return;
                	}else{
                 	    Ext.example.msg("提示","收款失败！");
                        return;
                	}  
                },  
                failure:function(form,action){  
            	    Ext.example.msg("提示","收款失败！");
                }  
            });    
        }  
    }); 
}

function closeApply(id,status){
	var str = "确认已和商家沟通，关闭本次申请吗?";
    Ext.Msg.confirm("提示信息",str,function (btn){  
        if(btn == 'yes')  
        {  
            Ext.Ajax.request({  
                url:"/valueAdded/closeOrderPushStatus",  
                params:{id:id,status:3},   
                method:'post',  
                success:function(o){  
            	    Ext.example.msg("提示","关闭成功！");
                	window.setTimeout("storeReload()", 1000);   
                    return ;  
                },  
                failure:function(form,action){  
              	    Ext.example.msg("提示","关闭失败！");
                }  
            });    
        }  
    }); 
}

function openApply(id,status){
	var str = "确认给商家开通订单推送充值?";
    Ext.Msg.confirm("提示信息",str,function (btn){  
        if(btn == 'yes')  
        {  
            Ext.Ajax.request({  
                url:"/valueAdded/openOrderPushStatus",  
                params:{id:id,status:2},   
                method:'post',  
                success:function(o){  
                    Ext.example.msg("提示","开通成功！");
                	window.setTimeout("storeReload()", 1000);  
                    return ;  
                },  
                failure:function(form,action){  
              	    Ext.example.msg("提示","开通失败！");
                }  
            });    
        }  
    }); 
}

</script>

</head>
<body>
</body>
</html>