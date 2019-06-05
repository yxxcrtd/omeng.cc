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
<title>终端信息统计</title>
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
	
	var pieStore = Ext.create("Ext.data.Store",{
        proxy:{  
            type:'ajax',  
            url:'/merchantTerminal/merchantChannelPieList',  
            reader:{  
                type:'json',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'channel'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'num'}
        ]  
    });

	var pieChart = Ext.create('Ext.chart.Chart', {
    renderTo: Ext.getBody(),
    width: 300,
    height: 250,
    animate: true,
    store: pieStore,
    theme: 'Base:gradients',
    legend: {
        position: 'bottom'
    },
    series: [{
        type: 'pie',
        angleField: 'num',
        showInLegend: true,
        //title:'变得快',
        tips: {
            trackMouse: true,
            width: 160,
            height: 30,
            renderer: function(storeItem, item) {
                // calculate and display percentage on hover
                var total = 0;
                pieStore.each(function(rec) {
                    total += rec.get('num');
                });
                this.setTitle(storeItem.get('channel') + ': ' +storeItem.get('num')+'台，占比'+ Math.round(storeItem.get('num') / total * 100) + '%');
            }
        },
        highlight: {
            segment: {
                margin: 20
            }
        },
        label: {
            field: 'channel',
            display: 'rotate',
            contrast: true,
            font: '18px Arial'
        }
    }]
});
	pieStore.load();  	
	//***
	
	var chartStore = Ext.create("Ext.data.Store",{
        proxy:{  
            type:'ajax',  
            url:'/merchantTerminal/merchantChannelColList',  
            reader:{  
                type:'json',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'channel'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'num'}
        ]  
    });
	
	var colors = ['#6E548D','#94AE0A','#FF7348','#3D96AE','#6E548D','#94AE0A','#FF7348','#3D96AE','#6E548D','#94AE0A','#FF7348']; 

	var chart = Ext.create('Ext.chart.Chart', {
	    renderTo: Ext.getBody(),
	    //width: 500,
		region: 'center',
	    height: 350,
	    animate: true,
	    
	    store: chartStore,
	    axes: [
	        {
	            type: 'Numeric',
	            position: 'left',
	            fields: ['num'],
	            label: {
	                renderer: Ext.util.Format.numberRenderer('0,0')
	            },
	            title: '累计用户',
	            grid: true,
	            minimum: 0
	        },
	        {
	            type: 'Category',
	            position: 'bottom',
	            fields: ['channel'],
	            title: '渠道累计用户分布图'
	        }
	    ],
	    series: [
	        {
	            type: 'column',
	            axis: 'left',
	            highlight: true,
	            tips: {
	              trackMouse: true,
	              width: 140,
	              height: 30,
	              renderer: function(storeItem, item) {
	                this.setTitle(storeItem.get('channel') + ': '+storeItem.get('num'));
	              }
	            },
	            label: {
	              display: 'insideEnd',
	              'text-anchor': 'middle',
	                field: 'num',
	                renderer: Ext.util.Format.numberRenderer('0'),
	                orientation: 'vertical',
	               // color: '#f00', renderer: function (v) { this.fill = 'red'; return v; }
	                color: '#f00'
	            },
	            style: {
	            	background:'#F00', 
	                color:'#FFF',
	            	size: 20
	            },
	            xField: 'channel',
	            yField: 'num',
	            //此渲染器的存在能够使每条柱子的颜色，与上方声明的颜色数组相同
	            renderer: function(sprite, storeItem, barAttr, i, store) {  
	                barAttr.fill = colors[i];
	                return barAttr;  
	            }
	        }
	    ]
	});
    chartStore.load();  
	
	 var columns = [
	                   {header:'序号',xtype: 'rownumberer',width:50},
	                   {header:'渠道',itemId: 'channel',align:'center',dataIndex:'channel',sortable:true,fixed:false},
	                   {header:'新增用户',itemId: 'addNum',align:'center',dataIndex:'addNum',sortable:true,fixed:false},
	                   {header:'活跃用户',itemId: 'activityNum',align:'center',dataIndex:'activityNum',sortable:true,fixed:false}
	               ];
	  
	    var store = Ext.create("Ext.data.Store",{
	    	pageSize:20, //每页显示几条数据  
	        proxy:{  
	            type:'ajax',  
	            url:'/merchantTerminal/merchantChannelList',  
	            reader:{  
	                type:'json',  
	                totalProperty:'total',  
	                root:'data',  
	                idProperty:'ext'  
	            }  
	        },  
	        fields:[  
	           {name:'channel'}, 
	           {name:'addNum'}, 
	           {name:'activityNum'}
	        ]  
	    });

	    //点击下一页时传递搜索框值到后台  
	    store.on('beforeload', function (store, options) {    
			var startTime=Ext.getCmp('startTime').getValue();
			var endTime=Ext.getCmp('endTime').getValue();
			var new_params = { startTime:startTime,endTime:endTime};  
	        Ext.apply(store.proxy.extraParams, new_params); 
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
	        	items:[{xtype : 'datefield',id: 'startTime',name: 'startTime',fieldLabel: '开始日期',format : 'Y-m-d',labelAlign:'left',labelWidth:75},'-',
	        	       {xtype : 'datefield',id: 'endTime',name: 'endTime',fieldLabel: '结束日期',format : 'Y-m-d',labelAlign:'left',labelWidth:75},'-',
	        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',width : 100,listeners: {
	    	    		   click:function(){		  
	    	    				var startTime=Ext.getCmp('startTime').getValue();
	    	    				var endTime=Ext.getCmp('endTime').getValue();
	    	    				store.currentPage = 1;
	     	    			    store.load({params:{start:0,page:1,limit:20,startTime:startTime,endTime:endTime}}); 
	    	    		   }
	    	    		   }}
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
    	//title: '商户设备类型占比',
        region: 'west',
        xtype: 'panel',
        margins: '5 0 0 5',
        width: 500,
        items: [pieChart],
        //collapsible: true,   // 可折叠/展开
        id: 'west-region-container',
        layout: 'fit'
    }, {
      	//title: '商户设备机型Top-10',
        region: 'center',     // 必须指定中间区域
        xtype: 'panel',
        items: [chart],
        layout: 'fit',
        margins: '5 5 0 0'
    }]
		});

	});  
</script>
</head>
<body>
</body>
</html>