<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp"%>
<%
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Expires", "0");

	request.setCharacterEncoding("UTF-8");

	//String webRoot = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>入驻商户趋势曲线图</title>

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
	var historyDayTimeAxis = false;    //存储时间轴对象  
	var historyDayFromDate ;    //时间轴起始时间  
	var historyDaytoDate ;    //时间轴截止时间  
	var dimension='';
	var store = Ext.create("Ext.data.Store",{
        proxy:{  
            type:'ajax',  
            url:'/statistic/getMerchantByTime',  
            reader:{  
                type:'json',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'join_time'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'data'}
        ]  
    });
	
	
	var chart=Ext.create('Ext.chart.Chart', {
		   renderTo: Ext.getBody(),
		   id: 'dayChart',
		   width: 1000,
		   height: 800,
		   store: store,
		   theme: 'Red',
	    axes: [
	        {
	            title: '入驻数量',
	            type: 'Numeric',
	            position: 'left',
	            fields: ['data'],
	            minimum: 0
	           // maximum: 100
	        },
	        {
	            title: '时间',
	            type: 'Time',
	            position: 'bottom',
	            fields: ['join_time'],
	            dateFormat: 'y-m-d',
	            step: [Ext.Date.DAY, 1],
	            fromDate: historyDayFromDate,
	            toDate: historyDaytoDate
	        }
	    ],
	    series: [
	             {
	                 type: 'line',
	                 xField: 'join_time',
	                 yField: 'data',
	                 markerCfg : {
	             			size : 1,
	             			radius : 1
	             		         },
	             	 smooth: true,
	             		tips : {
	             			width : 170,
	             			height : 60,
	             			renderer : function(storeItem, item) {
	             				var dd=storeItem.get('join_time');
	             				this.setTitle(dd + '<br />入驻量:' + storeItem.get('data'));
	             			}
	             		}
	             	}]
	});
	
	var _tbfxChar = Ext.create('widget.panel', {  
	    region : 'center',  
	    trackMouseOver : false,  
	    height:520,  
	    id : 'tbfxCharID',  
	    name : 'tbfxChar',  
	    border : false,   
	    layout: 'fit',  
	   // title : '图表分析',  
	    tbar : [{  
	            xtype:'container',  
	            layout:'anchor',  
	            defaultType:'toolbar',  
	            border:false,  
	            items:[{  
	                   items:[{xtype: 'button', id:'select1',text: '天',width : 70,listeners: {
	            		   click:function(){	
	            			     dimension='day';
		          			     historyDaytoDate = Ext.getCmp('endTime').getValue();   //时间轴截止时间  
		          				 if(historyDaytoDate==null){
		          					historyDaytoDate=new Date();
		          				 }
		          				 historyDayFromDate= Ext.getCmp('startTime').getValue();
		          				 if(historyDayFromDate==null){
		          					historyDayFromDate = new Date(historyDaytoDate.getTime() - 24*60*60*1000*29);    //时间轴起始时间  
		          				 }
		          			     historyDayTimeAxis.dateFormat='M d';
		          				 historyDayTimeAxis.step=[Ext.Date.DAY, 1];
	          			     store.load({params:{dimension:dimension}}); 
	          		  
	          		   }}},
	          		   {xtype: 'button', id:'select2',text: '月',width : 60,listeners: {
	          			   click:function(){
	          				 dimension='month';
	          				 historyDaytoDate = Ext.getCmp('endTime').getValue();   //时间轴截止时间  
	          				 if(historyDaytoDate==null){
	          					historyDaytoDate=new Date();
	          				 }
	          				 historyDayFromDate= Ext.getCmp('startTime').getValue();
	          				 if(historyDayFromDate==null){
	          					historyDayFromDate = new Date(historyDaytoDate.getTime() - 24*60*60*1000*364);    //时间轴起始时间  
	          				 }	 
	          				
	          				 historyDayTimeAxis.dateFormat='Y M';
	          				 historyDayTimeAxis.step=[Ext.Date.MONTH, 1];
	          				 store.load({params:{dimension:dimension}}); 
	          			   }}},
	          		   {xtype: 'button', id:'select3',text: '年',width : 60,listeners: {
	          				   click:function(){		  
	          					 dimension='year';
	          					 historyDaytoDate = Ext.getCmp('endTime').getValue();   //时间轴截止时间  
		          				 if(historyDaytoDate==null){
		          					historyDaytoDate=new Date();
		          				 }
		          				 historyDayFromDate= Ext.getCmp('startTime').getValue();
		          				 if(historyDayFromDate==null){
		          					historyDayFromDate = new Date(historyDaytoDate.getTime() - 24*60*60*1000*364*2);    //时间轴起始时间  
		          				 }	
		          				 historyDayTimeAxis.dateFormat='Y';
		          				 historyDayTimeAxis.step=[Ext.Date.YEAR, 1];
	          					 store.load({params:{dimension:dimension}}); 
	          				   }}}]  
	            }]  
	           
	    },{  
            xtype:'container',  
            layout:'anchor',  
            defaultType:'toolbar',  
            border:false,  
            items:[{  
                   items:[ {xtype : 'datefield',id: 'startTime',
         	          name: 'time',fieldLabel: '开始时间',
        	          format : 'Y-m-d',labelAlign:'left',labelWidth:75},'-',
        	       {xtype : 'datefield',id: 'endTime',
            	       name: 'time',fieldLabel: '结束时间',
            	       format : 'Y-m-d',labelAlign:'left',labelWidth:75},'-',
            	       <cms:havePerm url='/statistic/exportMerchantTrendExcel'>
	        	       { xtype: 'button', text: '导出',iconCls:'Daochu',
	            	    	   listeners: {
	            	    		   click:function(){
	            	    			   exportAll();
	            	    		   }
	            	    	   }
	                   },'-',</cms:havePerm>
          		   ]  
            }]  
           
    }],  
	    items:chart  
	}); 
	
	//点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var startTime=Ext.getCmp('startTime').getValue();
   	    var endTime=Ext.getCmp('endTime').getValue();
		var new_params = { startTime:startTime,endTime:endTime}; 
        Ext.apply(store.proxy.extraParams, new_params);    
    });
	store.load({params:{dimension:dimension}}); 
	historyDayTimeAxis = Ext.getCmp('dayChart').axes.get(1);
	historyDaytoDate = new Date();  
	historyDayFromDate = new Date(historyDaytoDate.getTime() - 24*60*60*1000*29);    //时间轴起始时间  
	
	//导出
	function exportAll() {
		   var startTime=Ext.getCmp('startTime').getValue();
		   var endTime=Ext.getCmp('endTime').getValue();
           window.location.href = '/statistic/exportMerchantTrendExcel?dimension='+dimension+'&startTime='+startTime+'&endTime='+endTime;	
	};
	 Ext.create("Ext.container.Viewport", {
			layout : 'border',
			autoHeight: true,
			border: false,
			items : [
            _tbfxChar    
			        ]
		});
	
});
</script>
</script>
</head>
<body></body>
</html>