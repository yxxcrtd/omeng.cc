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
<title>顾问号申请管理</title>
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
var task_confirm;//声明任务变量
var store;
var grid;
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'编号',dataIndex:'id',sortable:true,sortable:true,hidden:true,align:'center'},
                   {header:'服务商名称',dataIndex:'merchantName',sortable:true,align:'center'},
                   {header:'联系方式',dataIndex:'telephone',sortable:true,width:80,align:'center'},
                   {header:'地址',dataIndex:'address',align:'center'},
                   {header:'省份',dataIndex:'province',sortable:true,width:40,align:'center'},
                   {header:'城市',dataIndex:'city',sortable:true,width:50,align:'center'},
                   {header:'行业类型',dataIndex:'appName',sortable:true,width:50,align:'center'},
                   {header:'终端类型',dataIndex:'client_type',sortable:true,width:50,align:'center',fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:orangered;font-weight:bold';>安卓</span>";  
                       }else if(value=='2'){
                           return "<span style='color:green;font-weight:bold';>IOS</span>";  
                       }else{
                    	   return "<span style='color:red;font-weight:bold';>未知</span>"; 
                       }
           		   }},
                   {header:'申请时间',dataIndex:'applyTime',sortable:true,align:'center'},
                   {header:'申请数量',dataIndex:'applyNum',width:50,align:'center'},
                   {header:'确认时间',dataIndex:'confirmTime',sortable:true,hidden:true,align:'center'},
                   {header:'开通时间',dataIndex:'openTime',sortable:true,align:'center'},
                   {header:'金额',dataIndex:'money',align:'center'},
                   {header:'支付类型',dataIndex:'pay_type',sortable:true,width:50,align:'center',fixed:false,renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:orangered;font-weight:bold';>支付宝支付</span>";  
                       }else if(value=='2'){
                           return "<span style='color:green;font-weight:bold';>微信支付</span>";  
                       }else if(value=='3'){
                           return "<span style='color:darkblue;font-weight:bold';>现金支付</span>";  
                       }
           		   }},
                   {header:'状态',dataIndex:'applyStatus',sortable:true,fixed:false,align:'center',sortable:true,width:50,renderer:function(value){  
                	   if(value=='1'){  
                           return "<span style='color:orangered;font-weight:bold';>待开通</span>";  
                       }else if(value=='2'){
                           return "<span style='color:green;font-weight:bold';>已开通</span>";  
                       }else if(value=='3'){
                           return "<span style='color:darkblue;font-weight:bold';>无效</span>";  
                       }else if(value=='4'){
                           return "<span style='color:purple;font-weight:bold';>已过期</span>";  
                       }else if(value=='0'){
                    	   return "<span style='color:red;font-weight:bold';>待支付</span>"; 
                       }
           		   }},
           	     //  <cms:havePerm url='/valueAdded/confirmAdviserStatus'>
                  // {id: 'confirm',  header: '确认收款',  dataIndex: 'confirm', width:60, sortable:true,fixed:false,renderer:function(v,d,r){
                //	   var str = '';
                	//   var status = r.data.applyStatus;
                	//   var num = r.data.applyNum;
                	//   if(status=='0'){
                		   //待确认收款
                	//	   str = '<a href="javascript:void(0);" onclick="confirmApply('+r.data.id+',\''+status+'\',\''+num+'\')">收款</a>';
                	//   }
                   	 //  return str;
                   //}},
        	      //  </cms:havePerm>
                   <cms:havePerm url='/valueAdded/closeAdviserStatus'>
                   {id: 'close',  header: '关闭',  dataIndex: 'close', width:60,align:'center', sortable:true,fixed:false,renderer:function(v,d,r){
                	   var str = '';
                	   var status = r.data.applyStatus;
                       if(status=='0'||status=='1'){
                		   //关闭
                		   str = '<a href="javascript:void(0);" onclick="closeApply('+r.data.id+',\''+status+'\')">关闭</a>';
                	   }
                   	   return str;
                   }},
                   </cms:havePerm>
                   <cms:havePerm url='/valueAdded/openAdviserStatus'>
                   {id: 'open',  header: '开通功能',  dataIndex: 'open', width:60,align:'center', sortable:true,fixed:false,renderer:function(v,d,r){
                	   var str = '';
                	   var status = r.data.applyStatus;
                       if(status=='0'||status=='1'){
                		   //待开通
                		   str = '<a href="javascript:void(0);" onclick="openApply()">开通</a>';
                	   }else if(status=='2'){
                		   str = '<a href="javascript:void(0);" onclick="openApply()">查看</a>';
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
            url:'/valueAdded/adviserApplyList',
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
                {name:'applyStatus'},
                {name:'money'},
                {name:'pay_type'},
                {name:'client_type'}
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
    	          ['待支付','0'],
    	          ['待开通','1'],
    	          ['已开通','2'],
    	          ['无效','3'],
    	          ['已过期','4']
    	]
     });  
    var store_clientType = new Ext.data.SimpleStore({
    	// 0-待确认 1-已确认 2-已开通 3-无效（代理审核为无效）4-已过期（开通一年到期）
    	    fields:['name','type'],
    	    data:[['全部',''],
    	          ['安卓','0'],
    	          ['IOS','1']
    	]
     }); 
    // 行业类型
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
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var merchantName=Ext.getCmp('merchantName').getValue();
		var telephone=Ext.getCmp('telephone').getValue();
		var province=Ext.getCmp('province').getRawValue();
		var city=Ext.getCmp('city').getRawValue();
		var applyStatus=Ext.getCmp('applyStatus').getValue();
		var clientType=Ext.getCmp('clientType').getValue();
		var appType=Ext.getCmp('appType').getValue();
		var stime=Ext.getCmp('stime').getValue();
		var etime=Ext.getCmp('etime').getValue();
        var new_params = {merchantName:merchantName,telephone:telephone,clientType:clientType,
		           province:province,city:city,applyStatus:applyStatus,appType:appType,stime:stime,etime:etime};    
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
        dockedItems: [
       	   <cms:havePerm url='/valueAdded/deleteAdviserApply'>      
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
        	       <cms:havePerm url='/valueAdded/exportAdviserApplyExcel'>
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
	              },'-',{
						xtype : 'combobox',
						name : 'appType',
						id: 'appType',
						fieldLabel : '行业类型',
						valueField : 'app_type',
						displayField : 'app_name',
						store : store_appType,
						allowBlank	:false,
						editable:false,
						hiddenName:'',
						value:'',
						labelWidth:60,
						allowBlank	:false,
						queryMode : 'local'
				   },'-',{
						xtype : 'combobox',
						name : 'clientType',
						id: 'clientType',
						fieldLabel : '终端类型',
						valueField : 'type',
						displayField : 'name',
						store : store_clientType,
						hiddenName:'',
						value:'',
						labelWidth:60,
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
				   <cms:havePerm url='/valueAdded/adviserApplyList'>
                   { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
      	    			   var merchantName=Ext.getCmp('merchantName').getValue();
    	    			   var telephone=Ext.getCmp('telephone').getValue();
      	    			   var province=Ext.getCmp('province').getRawValue();
    	    			   var city=Ext.getCmp('city').getRawValue();
    	    			   var applyStatus=Ext.getCmp('applyStatus').getValue();
    	    			   var clientType=Ext.getCmp('clientType').getValue();
    	    			   var appType=Ext.getCmp('appType').getValue();
    	    			   var stime=Ext.getCmp('stime').getValue();
    	    			   var etime=Ext.getCmp('etime').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,merchantName:merchantName,telephone:telephone,clientType:clientType,
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
		   var clientType=Ext.getCmp('clientType').getValue();
         window.location.href = '/valueAdded/exportAdviserApplyExcel?merchantName='+merchantName+'&telephone='+telephone+'&province='+province+'&clientType='+clientType
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
                       url:"/valueAdded/deleteAdviserApply",  
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

task_confirm = {
        run: storeReload,//执行任务时执行的函数
        interval: 10000//任务间隔，毫秒为单位，这里是10秒
    }
    
 function storeReload(){ 
	store.reload();
 }   
    
function confirmApply(id,status,num){
	var fee = 0;
	if(num!=null&&num>0){
		fee = 1500*num;
	}
	var str = "确认已收到商家增值服务费<span style='color:red;font-weight:bold';>"+fee+"</span>元?";
    Ext.Msg.confirm("提示信息",str,function (btn){  
        if(btn == 'yes')  
        {  
            Ext.Ajax.request({  
                url:"/valueAdded/confirmAdviserStatus",  
                params:{id:id,status:1,num:num},  
                method:'post',  
                success:function(o){  
                	var res = eval(o.responseText);
                	if(res=='1'){
                        Ext.example.msg("提示","收款成功！");
                		window.setTimeout("storeReload()", 1000);   
                        //store.reload();  
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
                url:"/valueAdded/closeAdviserStatus",  
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

function openApply(){
	var rows = grid.getSelectionModel().getSelection();  
    //user_id：所有选中的服务商Id的集合使用','隔开，初始化为空    
    if(rows.length == 0)  
    {  
  	    Ext.example.msg("提示","请选择要开通的对象！");
        return ;  
    }
    if(rows.length > 1)  
    {  
	   Ext.example.msg("提示","只能选择一个开通的对象！");
       return ;  
    }
     var id = rows[0].get('id');
     var app_type = rows[0].get('appName');
     var merchantName = rows[0].get('merchantName');
     var pay_type = rows[0].get('pay_type');
     var applyStatus = rows[0].get('applyStatus');
     var money = rows[0].get('money');
     var path = rows[0].raw.path;
    
	if(applyStatus==0||applyStatus==1){
		certificate(id,app_type,merchantName,pay_type,applyStatus,money,path);
	}else if(applyStatus==2){
		certificate1(id,app_type,merchantName,pay_type,applyStatus,money,path);
	}
}
var payTypeStore = new Ext.data.SimpleStore({
 	fields:['name','type'],
 	data:[['全部',''],
 	      ['支付宝支付','1'],
 	      ['微信支付','2'],
 	      ['现金支付','3']
 	]
 });
 
//待支付状态开通
function  certificate(id,app_type,merchantName,pay_type,applyStatus,money,path){
	var cert_winForm =  Ext.create('Ext.form.Panel', {  
        frame: true,   //frame属性  
        //title: 'Form Fields',  
        width: 820,
        height:1200,
        bodyPadding:5,  
        //renderTo:"panel21",  
        fieldDefaults: {  
            labelAlign: 'left',  
            labelWidth: 90,  
            anchor: '100%'  
        },  
        items: [
        {   
            xtype: 'textfield', 
            name: 'id',  
            fieldLabel: 'ID',
            hidden:true
        },{  
            xtype: 'textfield', 
            name: 'merchantName',  
            fieldLabel: '服务商名称',
            readOnly:true,
        }, {  
            xtype: 'textfield', 
            name: 'app_type',  
            fieldLabel: '行业类型',
            readOnly:true,
        }, {
				xtype : 'combobox',
				name : 'pay_type',
				fieldLabel : '支付类型',
				valueField : 'type',
				displayField : 'name',
				editable:false,
				store : payTypeStore,
				hiddenName:'',
				allowBlank	:false,
				readOnly:true,
				queryMode : 'local'
			},{
				// 输入价格
				xtype : 'numberfield',
				name : 'price',
				allowDecimals: true,
				minValue: 0,
				maxValue: 100000,
				readOnly:true,
				fieldLabel : '价格'
			},{  
                //多行文本输入框  
                xtype: 'textareafield', //5  
                name: 'remark',  
                fieldLabel: '填写原因',
                hidden:true
            }, {  
            //显示文本框，相当于label  
            xtype: 'displayfield',   
            name: 'pic',   
            value: '银行转账凭证信息'    
           }, 
           {  
       	    xtype: 'box', //或者xtype: 'component',  
       	    width: 500, //图片宽度  
       	    //height: 1000, //图片高度  
       	    autoEl: {  
       	        tag: 'img',    //指定为img标签  
       	        src: path    //指定url路径  
       	    }  
       	}
        
       ]  
    });  
	
	cert_winForm.form.findField('id').setValue(id);
    cert_winForm.form.findField('merchantName').setValue(merchantName);
    cert_winForm.form.findField('app_type').setValue(app_type);  
    cert_winForm.form.findField('pay_type').setValue(pay_type+'');  
    cert_winForm.form.findField('price').setValue(money); 
    
    var certwindow = Ext.create('Ext.window.Window',{  
        title : "vip开通信息",  
        width: 850,
        height:450,
        autoScroll: true, 
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
        items : [cert_winForm],  
        buttons : [{  
                   text : "开通",  
                   minWidth : 70,  
                   handler : function() {  
                      if (cert_winForm.getForm().isValid()) {
                       Ext.Msg.confirm("提示信息","确定开通吗?",function (btn){  
                        if(btn == 'yes')  
                        {  
                            	cert_winForm.getForm().submit({  
                                          url :'/valueAdded/openAdviserStatus',  
                                           //等待时显示 等待  
                                          waitTitle: '请稍等...',  
                                          waitMsg: '正在提交信息...',  
                                            
                                          success: function(fp, o) {  
                                              if (o.result== true) { 
                                     	      Ext.example.msg("提示","开通信息保存成功！");
                                     	      window.setTimeout("storeReload()", 1000);
                                              certwindow.close(); //关闭窗口  
                                              return ;  
                                              }else {  
                                        	      Ext.example.msg("提示","状态已改变，开通信息保存失败！");
                                              }  
                                          },  
                                          failure: function() { 
                                 	         Ext.example.msg("提示","状态已改变，开通信息保存失败！");
                                          }  
                                       });    
                        }  
                    });  

                      }  
                   }  
               }]  
     });  
 certwindow.show(); 

}
//开通状态查看
function  certificate1(id,app_type,merchantName,pay_type,applyStatus,money,path){
	var cert_winForm =  Ext.create('Ext.form.Panel', {  
        frame: true,   //frame属性  
        //title: 'Form Fields',  
        width: 820,
        height:1200,
        bodyPadding:5,  
        //renderTo:"panel21",  
        fieldDefaults: {  
            labelAlign: 'left',  
            labelWidth: 90,  
            anchor: '100%'  
        },  
        items: [
        {   
            xtype: 'textfield', 
            name: 'id',  
            fieldLabel: 'ID',
            hidden:true
        },{  
            xtype: 'textfield', 
            name: 'merchantName',  
            fieldLabel: '服务商名称',
            readOnly:true,
        }, {  
            xtype: 'textfield', 
            name: 'app_type',  
            fieldLabel: '行业类型',
            readOnly:true,
        }, {
				xtype : 'combobox',
				name : 'pay_type',
				fieldLabel : '支付类型',
				valueField : 'type',
				displayField : 'name',
				editable:false,
				store : payTypeStore,
				hiddenName:'',
				allowBlank	:false,
				readOnly:true,
				queryMode : 'local'
			},{
				// 输入价格
				xtype : 'numberfield',
				name : 'price',
				allowDecimals: true,
				minValue: 0,
				maxValue: 100000,
				readOnly:true,
				fieldLabel : '价格'
			},{  
                //多行文本输入框  
                xtype: 'textareafield', //5  
                name: 'remark',  
                fieldLabel: '填写原因',
                hidden:true
            }, {  
            //显示文本框，相当于label  
            xtype: 'displayfield',   
            name: 'pic',   
            value: '银行转账凭证信息'    
           }, 
           {  
       	    xtype: 'box', //或者xtype: 'component',  
       	    width: 500, //图片宽度  
       	    //height: 1000, //图片高度  
       	    autoEl: {  
       	        tag: 'img',    //指定为img标签  
       	        src: path    //指定url路径  
       	    }  
       	}
        
       ]  
    });  
	
	cert_winForm.form.findField('id').setValue(id);
    cert_winForm.form.findField('merchantName').setValue(merchantName);
    cert_winForm.form.findField('app_type').setValue(app_type);  
    cert_winForm.form.findField('pay_type').setValue(pay_type+'');  
    cert_winForm.form.findField('price').setValue(money); 
    
    var certwindow = Ext.create('Ext.window.Window',{  
        title : "vip开通信息",  
        width: 850,
        height:450,
        autoScroll: true, 
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
        items : [cert_winForm],  
        buttons : []  
     });  
 certwindow.show(); 
 
}
</script>

</head>
<body>
</body>
</html>