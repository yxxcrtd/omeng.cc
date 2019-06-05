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
<title>启动次数统计</title>
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
var sDay=''; // 日期
var title = '时段启动次数分布图'; 
Ext.onReady(function() {
	
	var lineStore = Ext.create("Ext.data.Store",{
        proxy:{  
            type:'ajax',  
            url:'/userTerminal/userStartUpLineList',  
            reader:{  
                type:'json',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'hour'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'total'},
           {name:'aphone'},
           {name:'iphone'}
        ]  
    });

	var lineChart = Ext.create('Ext.chart.Chart', {
	    renderTo: Ext.getBody(),
		legend : {
			position : 'top'
		},
	    width: 500,
	    height: 250,
	    animate: true,
	    store: lineStore,
	    axes: [
	        {
	            type: 'Numeric',
	            position: 'left',
	            fields: ['total','aphone','iphone'],
	            label: {
	                renderer: Ext.util.Format.numberRenderer('0,0')
	            },
	            title: '启动次数',
	            grid: true,
	            minimum: 0
	        },
	        {
	            type: 'Category',
	            position: 'bottom',
	            fields: ['hour'],
	            title: title
	        }
	    ],
	    series: [
	        {
	            type: 'line',
	            highlight: {
	                size: 7,
	                radius: 7
	            },
	            tips: {
	            	  trackMouse: true,
	            	  width: 140,
	            	  height: 30,
	            	  renderer: function(storeItem, item) {
	            	    this.setTitle(storeItem.get('hour') + '时共启动: ' + storeItem.get('total') + '次');
	            	  }
	            	},
	            axis: 'left',
	            xField: 'hour',
	            yField: 'total',
	            markerConfig: {
	                type: 'circle',
	                size: 4,
	                radius: 4,
	                'stroke-width': 0
	            }
	        },
	        {
	            type: 'line',
	            highlight: {
	                size: 7,
	                radius: 7
	            },
	            tips: {
	            	  trackMouse: true,
	            	  width: 140,
	            	  height: 30,
	            	  renderer: function(storeItem, item) {
	            	    this.setTitle(storeItem.get('hour') + '时安卓启动: ' + storeItem.get('aphone') + '次');
	            	  }
	            	},
	            axis: 'left',
	            fill: true,
	            xField: 'hour',
	            yField: 'aphone',
	            markerConfig: {
	                type: 'cross',
	                size: 4,
	                radius: 4,
	                'stroke-width': 0
	            }
	        },
	        {
	            type: 'line',
	            highlight: {
	                size: 7,
	                radius: 7
	            },
	            tips: {
	            	  trackMouse: true,
	            	  width: 140,
	            	  height: 30,
	            	  renderer: function(storeItem, item) {
	            	    this.setTitle(storeItem.get('hour') + '时苹果启动: ' + storeItem.get('iphone') + '次');
	            	  }
	            	},
	            axis: 'left',
	            fill: true,
	            xField: 'hour',
	            yField: 'iphone',
	            markerConfig: {
	                type: 'circle',
	                size: 4,
	                radius: 4,
	                'stroke-width': 0
	            }
	        }
	    ]
	});
	lineStore.load();  	

	 var columns = [
	                   {header:'序号',xtype: 'rownumberer',width:50},
	                   {header:'日期',itemId: 'visitDay',align:'center',dataIndex:'visitDay',sortable:true,fixed:false},
	                   {header:'启动次数(aphone)',itemId: 'aphoneStartNum',align:'center',dataIndex:'aphoneStartNum',sortable:true,fixed:false},
	                   {header:'启动次数(iphone)',itemId: 'iphoneStartNum',align:'center',dataIndex:'iphoneStartNum',sortable:true,fixed:false},
	                   {header:'启动次数(合计)',itemId: 'startNum',align:'center',dataIndex:'startNum',sortable:true,fixed:false}
	               ];
	  
	    var store = Ext.create("Ext.data.Store",{
	    	pageSize:20, //每页显示几条数据  
	        proxy:{  
	            type:'ajax',  
	            url:'/userTerminal/userStartUpList',  
	            reader:{  
	                type:'json',  
	                totalProperty:'total',  
	                root:'data',  
	                idProperty:'ext'  
	            }  
	        },  
	        fields:[  
	           {name:'visitDay'}, 
	           {name:'startNum'},    
	           {name:'aphoneStartNum'}, 
	           {name:'iphoneStartNum'}
	        ]  
	    });

	    //点击下一页时传递搜索框值到后台  
	    store.on('beforeload', function (store, options) {    
			var startTime=Ext.getCmp('startTime').getValue();
			var endTime=Ext.getCmp('endTime').getValue();
			var new_params = { startTime:startTime,endTime:endTime};    
	        Ext.apply(store.proxy.extraParams,new_params); 
	    });  

	    var sm = Ext.create('Ext.selection.CheckboxModel');
	    var grid = Ext.create("Ext.grid.Panel",{
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
	      {
	        	xtype:'toolbar',
	        	dock:'top',
	        	displayInfo: true,
	        	items:[{xtype : 'datefield',id: 'startTime',name: 'startTime',fieldLabel: '开始日期',format : 'Y-m-d',labelAlign:'left',
				        value:Ext.util.Format.date(Ext.Date.add(new Date(),Ext.Date.DAY,-1),"Y-m-d"), labelWidth:75},'-',
	        	       {xtype : 'datefield',id: 'endTime',name: 'endTime',fieldLabel: '结束日期',format : 'Y-m-d',labelAlign:'left',
					   value:Ext.util.Format.date(Ext.Date.add(new Date(),Ext.Date.DAY,-1),"Y-m-d"), labelWidth:75},'-',
	        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',width : 100,listeners: {
	    	    		   click:function(){		  
	    	    				var startTime=Ext.getCmp('startTime').getValue();
	    	    				var endTime=Ext.getCmp('endTime').getValue();
	    	    				store.currentPage = 1;
	     	    			    store.load({params:{start:0,page:1,limit:20,startTime:startTime,endTime:endTime}}); 
	    	    		   }
	    	    		   }},'->',	
		           	       <cms:havePerm url='/userTerminal/exportUserStartUpListExcel'>
	                          { xtype: 'button', text: '导出',iconCls:'Daochu',
	                            listeners: {
	                              click:function(){
	                                exportAll();
	                               }
	                            }
	                            }</cms:havePerm>
					   ]}],
	        bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
	            displayInfo : true,
	            store : store
	        })
	    });
	  
	  //加载数据  
	    store.load({params:{start:0,limit:20}}); 
	 // 加载权限
	 

	    grid.on("itemcontextmenu",function(view,record,item,index,e){  
	        e.preventDefault();  
	        contextmenu.showAt(e.getXY());  
	    });
	    grid.on("itemclick",function(grid, row){
	    	sDay = row.data.visitDay;
	 	    lineStore.load({params:{visitDay:sDay}});
	    });

	  //导出
		function exportAll() {
			 var startTime=Ext.getCmp('startTime').getValue();
			 var endTime=Ext.getCmp('endTime').getValue();
	         window.location.href = '/userTerminal/exportUserStartUpListExcel?startTime='+startTime+'&endTime='+endTime;	
		}; 
	    
		// 整体架构容器
		Ext.create("Ext.container.Viewport", {
			layout : 'border',
			autoHeight: true,
			border: true,
			items : [
			         {
        region: 'south',     // 所在的位置
        xtype: 'panel',
        height: 400,
        items: [grid],
        layout: 'fit',
        split: true,         // 允许调整大小
        margins: '0 5 5 5'
    }, {
      	//title: '时段（小时）启动次数',
        region: 'center',     // 必须指定中间区域
        xtype: 'panel',
        items: [lineChart],
        layout: 'fit',
        margins: '5 5 0 5'
    }]
		});

	});  
</script>
</head>
<body>
</body>
</html>