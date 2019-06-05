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
<title>日粉丝增涨量</title>
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
                   {header:'省份',dataIndex:'province',align:'center'},
                   {header:'城市',dataIndex:'city',align:'center'},
                   {header:'日期',dataIndex:'head_date',align:'center'},
                   {header:'新增服务商',dataIndex:'increase_merchant_num',align:'center'},
                   {header:'增粉服务商',dataIndex:'increase_fans_merchant_num',align:'center'},
                   {header:'粉丝数',dataIndex:'increase_fans_num',align:'center'},
                   {header:'平均粉丝数',dataIndex:'average_fans_num',align:'center'}
                   
                   ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/activity/getDayAddFensi',  
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
           {name:'head_date'},
           {name:'increase_merchant_num'},
           {name:'increase_fans_merchant_num'},
           {name:'increase_fans_num'},
           {name:'average_fans_num'}
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
			}]
		});
	 
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {   
		 var province=Ext.getCmp('province').getRawValue();
		 var city=Ext.getCmp('city').getRawValue();
		 var head_date=Ext.getCmp('head_date').getValue();
		 var new_params = {province:province,city:city,head_date:head_date};    
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
        	items:['-',
        	       <cms:havePerm url='/activity/exportExcel'>
        	       { xtype: 'button', text: '导出',iconCls:'Daochu',
            	    	   listeners: {
            	    		   click:function(){
            	    			   exportAll();
            	    		   }
            	    	   }
                   }</cms:havePerm> 
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[	{xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省份',valueField : 'id',displayField : 'area',
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
							
						{xtype : 'datefield',id: 'head_date',name: 'head_date',fieldLabel: '时间',format : 'Y-m-d',labelAlign:'left',labelWidth:75},'-',
						       
								<cms:havePerm url='/activity/getDayAddFensi'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			     var province=Ext.getCmp('province').getRawValue();
    	    				 var city=Ext.getCmp('city').getRawValue();
    	    				 var head_date=Ext.getCmp('head_date').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20, province:province,city:city,head_date:head_date}}); 
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
   
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    //导出所有粉丝
	function exportAll() {
		 var province=Ext.getCmp('province').getRawValue();
		 var city=Ext.getCmp('city').getRawValue();
		 var head_date=Ext.getCmp('head_date').getValue();
		window.location.href = '/activity/exportExcel?province='+province+'&city='+city+'&head_date='+head_date;
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