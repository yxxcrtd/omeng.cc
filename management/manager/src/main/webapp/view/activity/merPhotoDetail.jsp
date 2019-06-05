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
<title>服务商信息</title>
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
var grid;
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'ID',dataIndex:'id',sortable:true,fixed:false,hidden:true,align:'center'},
                   {header:'省份',dataIndex:'province',align:'center'},
                   {header:'城市',dataIndex:'city',align:'center'},
                   {header:'注册号码',dataIndex:'phone',align:'center'},
                   {header:'app类型',dataIndex:'app_name',align:'center'},
                   {header:'店铺名称',dataIndex:'name',align:'center'},
                   {header:'店铺地址',dataIndex:'location_address',align:'center'},
                   {header:'邀请码',dataIndex:'invitation_code',align:'center'},
                   {header:'最近上传相片时间',dataIndex:'photo_time',align:'center'},
                   {header:'所有上传时间',dataIndex:'time',align:'center',renderer:function(value,v,r){  
                       return '<a href="javascript:showPhotimeDetail(\''+r.data.id+'\')"><span style="color:red;font-weight:bold";>时间列表</span></a>';
          		   }},
                   {header:'操作',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                       return '<a href="javascript:showInfo()">查看</a>';
             		}}
                   ];
	
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/activity/getDayMerPhotoDetail',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id',type:'string'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'province'}, 
           {name:'city'}, 
           {name:'app_name'},
           {name:'phone'},
           {name:'name'},
           {name:'location_address'},
           {name:'invitation_code'},
           {name:'photo_time'},
           {name:'invitation_code'}
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
	 
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {   
    	 var province=Ext.getCmp('province').getValue();
    	 var city=Ext.getCmp('city').getValue();
    	 var area=Ext.getCmp('area').getValue();
    	 var phone=Ext.getCmp('phone').getValue();
    	 var invitation_code=Ext.getCmp('invitation_code').getValue();
		 var name=Ext.getCmp('name').getValue();
		 var app_type=Ext.getCmp('app_type').getValue();
		 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
		 var new_params = {province:province,city:city,area:area,phone:phone,name:name,app_type:app_type,start_time:start_time,off_time:off_time,invitation_code:invitation_code};    
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
        	       
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[	{ xtype: 'textfield',id:'phone',name: 'phone',fieldLabel: '注册号码',labelAlign:'left',labelWidth:70},'-',
         	        { xtype: 'textfield',id:'name',name: 'name',fieldLabel: '店铺名称',labelAlign:'left',labelWidth:65},'-',
         	        {xtype : 'combobox',id : 'app_type',name : 'app_type',fieldLabel : 'app类型',valueField : 'app_type',displayField : 'app_name',
						store : store_appType,value:'',editable:false,queryMode : 'local',labelAlign : 'left',labelWidth : 65},'-',
         	         ]
		      },	{
			    xtype : 'toolbar',
			    dock : 'top',
			    displayInfo : true,
			    items : [  
                        { xtype: 'textfield',id:'invitation_code',name: 'invitation_code',fieldLabel: '邀请码',labelAlign:'left',labelWidth:65},'-',
 
                        { xtype: 'datetimefield',id: 'start_time',name: 'start_time',fieldLabel: '开始时间',format : 'Y-m-d',labelAlign:'left',labelWidth:65},'-',
                        { xtype: 'datetimefield',id: 'off_time',name: 'off_time',fieldLabel: '结束时间',format : 'Y-m-d',labelAlign:'left',labelWidth:65},'-',
						{ xtype: 'textfield',id:'province',name: 'province',fieldLabel: '省',labelAlign:'left',value:'${province}',labelWidth:70,hidden:true},
	         	        { xtype: 'textfield',id:'city',name: 'city',fieldLabel: '城市',labelAlign:'left',value:'${city}',labelWidth:65,hidden:true},
	         	        { xtype: 'textfield',id:'area',name: 'area',fieldLabel: '区',labelAlign:'left',value:'${area}',labelWidth:65,hidden:true},
	         	       
        	        <cms:havePerm url='/activity/getDayMerPhotoDetail'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			    var province=Ext.getCmp('province').getValue();
    	    		    	var city=Ext.getCmp('city').getValue();
    	    		    	var area=Ext.getCmp('area').getValue();  
    	    			    var phone=Ext.getCmp('phone').getValue();
    	    			    var invitation_code=Ext.getCmp('invitation_code').getValue();
    	    				var name=Ext.getCmp('name').getValue();
    	    				var app_type=Ext.getCmp('app_type').getValue();
    	    				var start_time=Ext.getCmp('start_time').getValue();
    	    				var off_time=Ext.getCmp('off_time').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,invitation_code:invitation_code,province:province,city:city,area:area, phone:phone,name:name,app_type:app_type,start_time:start_time,off_time:off_time}}); 
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
		
});

//*********************服务商信息查看start*********************
function  showInfo(){     
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

     var name = rows[0].get('name');
     var location_address = rows[0].get('location_address');
     var pic =rows[0].raw.path; 

		var show_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,  
	                width: 820,
	                height:1200,
	                bodyPadding:5,  
	                
	                fieldDefaults: {  
	                    labelAlign: 'left',  
	                    labelWidth: 90,  
	                    anchor: '100%'  
	                },  
	                items: [{  
	                    //显示文本框，相当于label  
	                    xtype: 'displayfield',   
	                    name: 'displayfield1',   
	                    value: '最新上传相片'  
	                     
	                }, 
	               {  
	                    //服务商名称
	                    xtype: 'textfield', 
	                    name: 'name',
	                    readOnly:true,
	                    fieldLabel: '服务商名称'  
	                }, {  
	                    //输入联系方式
	                    xtype: 'textfield',  
	                    name: 'location_address',
	                    readOnly:true,
	                    fieldLabel: '商户地址'
	                },{  
	                    //显示文本框，相当于label  
	                    xtype: 'displayfield',   
	                    name: 'pic',  
//	                    fieldLabel: 'Display field',  
	                    value: '上传图片信息'  
	                     
	                }, 
	             {  
	         	    xtype: 'box', //或者xtype: 'component',  
	         	    width: 500, //图片宽度  
	         	    //height: 1000, //图片高度  
	         	    autoEl: {  
	         	        tag: 'img',    //指定为img标签  
	         	        onclick:'zoomImg(\''+pic+'\')',
	         	        src: pic    //指定url路径  
	         	    }  
	         	}
	               
	                
	               ]  
	            });  
	    //创建window面板，表单面板是依托window面板显示的  
	    
	    
	    
        show_winForm.form.findField('name').setValue(name);  
        show_winForm.form.findField('location_address').setValue(location_address);  
       
	    var showwindow = Ext.create('Ext.window.Window',{  
	              title : "最新上传相片信息",  
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
	              items : [show_winForm],  
	              buttons : [{  
                     text : "关闭",  
                     minWidth : 70,  
                     handler : function() {  
                    	 showwindow.close();  
                     }  
                 }]  
	           });  
	       showwindow.show();  
	    };
 //*********************服务商信息认证end*********************
 
 //查看商户上传相册时间
	var showPhotimeDetail=function(merchantsid){ 
		var columns = [
		    {xtype : 'rownumberer'},
			{header : '上传时间',dataIndex : 'join_time'}
		];
	//列表展示数据源
		var storePhotimeDeatil = Ext.create("Ext.data.Store", {
			pageSize : 20, //每页显示几条数据  
			proxy : {
				type : 'ajax',
				url : '/activity/getPhotimeByMerId',
				reader : {
					type : 'json',
					totalProperty : 'total',
					root : 'data',
					idProperty : '#'
				}
			},
	        fields:[   //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
	           {name:'join_time'}
	        ]  
	    });
		storePhotimeDeatil.on('beforeload', function (storeVouchers, options) {    
        var new_params = {merchantsid:merchantsid };    
        Ext.apply(storeVouchers.proxy.extraParams, new_params);    
    }); 
	    var sm = Ext.create('Ext.selection.CheckboxModel');
	    var gridPhotime = Ext.create("Ext.grid.Panel",{
	    	region: 'center',
	    	border: false,
	    	store: storePhotimeDeatil,
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
	        	items:[]
	        },{
	        	xtype:'toolbar',
	        	dock:'top',
	        	displayInfo: true,
	        	
	        },{
	            xtype: 'pagingtoolbar',
	            store: storePhotimeDeatil,   // GridPanel使用相同的数据源
	            dock: 'bottom',
	            displayInfo: true,
	            plugins: Ext.create('Ext.ux.ProgressBarPager'),
	            emptyMsg: "没有记录" //没有数据时显示信息
	        }]
	    });
	  //加载数据  

	  storePhotimeDeatil.load({params:{start:0,limit:20,merchantsid:merchantsid}}); 
	    var Photimewin = Ext.create('Ext.window.Window',{  
	              title : "查看上传相册时间",  
	              width : 350, 
	              height : 400,  
	              //plain : true,  
	              iconCls : "addicon",  
	              // 不可以随意改变大小  
	              resizable : false, 
	              autoScroll:true, 
	              // 是否可以拖动  
	              // draggable:false,  
	              collapsible : true, // 允许缩放条  
	              closeAction : 'close',  
	              closable : true,  
	              // 弹出模态窗体  
	              modal : 'true',  
	              buttonAlign : "center",  
	              bodyStyle : "padding:0 0 0 0",  
	              items : [gridPhotime]
	           });  
	    Photimewin.show();  
};
 
</script>

</head>
<body>
</body>
</html>