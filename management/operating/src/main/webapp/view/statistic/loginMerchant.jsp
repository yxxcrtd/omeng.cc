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
<title>入驻商户信息统计</title>
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
	
	var store1 = Ext.create("Ext.data.Store",{
        proxy:{  
            type:'ajax',  
            url:'/statistic/getMerchantPia',  
            reader:{  
                type:'json',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'name'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'data'}
        ]  
    });
    
	
	//饼状图
	var chart=Ext.create('Ext.chart.Chart', {
	    renderTo: Ext.getBody(),
	    title:'统计',
	    region:'center',
	   // width: 500,
	    height: 250,
	    //animate: true,
	    store: store1,
	    theme: 'Base:gradients',
	    series: [{
	        type: 'pie',
	        angleField: 'data',
	        showInLegend: true,
	        tips: {
	            trackMouse: true,
	            width: 140,
	            height: 28,
	            renderer: function(storeItem, item) {
	                // calculate and display percentage on hover
	                var total = 0;
	                store1.each(function(rec) {
	                    total += rec.get('data');
	                });
	                this.setTitle(storeItem.get('name') + ': ' + Math.round(storeItem.get('data') / total * 100) + '%');
	            }
	        },
	        highlight: {
	            segment: {
	                margin: 20
	            }
	        },
	        label: {
	            field: 'name',
	            display: 'rotate',
	            contrast: true,
	            font: '18px Arial'
	        }
	    }]
	});
	 store1.load({params:{dimension:''}}); 

var _tbfxChar = Ext.create('widget.panel', {  
    region : 'center',  
    trackMouseOver : false,  
    height:520,  
    id : 'tbfxCharID',  
    name : 'tbfxChar',  
    border : false,   
    layout: 'fit',  
  //  title : '图表分析',  
    tbar : [{  
            xtype:'container',  
            layout:'anchor',  
            defaultType:'toolbar',  
            border:false,  
            items:[{  
                   items:[{xtype: 'button', id:'select1',text: 'appType',width : 70,listeners: {
            		   click:function(){	
          			     var dimension='app_type';
          			    store1.load({params:{dimension:dimension}}); 
          		  
          		   }}},
          		   {xtype: 'button', id:'select2',text: '省份',width : 60,listeners: {
          			   click:function(){
          				   var dimension='province';
          				   store1.load({params:{dimension:dimension}}); 
          			   }}},
          		   {xtype: 'button', id:'select3',text: '城市',width : 60,listeners: {
          				   click:function(){		  
          					   var dimension='city';
          					   store1.load({params:{dimension:dimension}}); 
          				   }}}]  
            }]  
           
    }],  
    items:chart  
}); 


//折线图

var chart1 = Ext.create('Ext.chart.Chart', {
	style: 'background:#33333',
	region : 'center',
	//width : 500,
	height : 350,
	store : store1,
	renderTo : Ext.getBody(),
	legend : {
		position : 'bottom'
	},
	axes : [{
		type : 'Category',
		position : 'bottom',
		fields : 'name'
	}, {
		type : 'Numeric',
		position : 'left',
		fields : 'data',
		decimals : 0,
		grid : true,
		minimum : 0
	}],
	series : [{
		type : 'line',
		axis : 'left',
		xField : 'name',
		yField : 'data',
		markerCfg : {
			size : 1,
			radius : 1
		},
		tips : {
			width : 170,
			height : 60,
			renderer : function(storeItem, item) {
				this.setTitle(storeItem.get('name') + '<br />' + storeItem.get('data'));
			}
		}
	}]
});

//列表详情

	
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50},
                   {header:'ID',dataIndex:'id',align:'center',sortable:true,fixed:false,hidden:true,width : 400},
                   {header:'商户数量',itemId: 'terminalNum',align:'center',dataIndex:'terminalNum',sortable:true,fixed:false,width : 400},
                   {header:'app类型',itemId: 'app_type',align:'center',dataIndex:'app_type',sortable:true,fixed:false,hidden:true,width : 400},
                   {header:'省份',itemId: 'province',align:'center',dataIndex:'province',sortable:true,fixed:false,hidden:true,width : 400},
                   {header:'城市',itemId: 'city',align:'center',dataIndex:'city',sortable:true,fixed:false,hidden:true,width : 400}
                   ];
  
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/statistic/getLoginMerchantList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'terminalNum'}, 
           {name:'app_type'}, 
           {name:'province'}, 
           {name:'city'} 
        
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
       
    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	var dimension=getCheckValue();
    	var app_type=Ext.getCmp('app_type_Filter').getValue();
		var province=Ext.getCmp('provinceFilter').getRawValue();
		var city=Ext.getCmp('cityFilter').getRawValue();
		var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
		var new_params = { dimension:dimension,app_type : app_type,province:province,city:city,start_time:start_time,off_time:off_time};    
        Ext.apply(store.proxy.extraParams, new_params); 
    });  
    var checkboxgroup = new Ext.form.CheckboxGroup({
    	                 fieldLabel: '维度',
    	                 width: 700,
    	                 items: [{
    	                     boxLabel: 'app类型',
    	                     inputValue: 'app_type'
    	                 }, {
    	                     boxLabel: '省份',
    	                     inputValue: 'province'
    	                 }, {
     	                    boxLabel: '城市',
   	                        inputValue: 'city'
   	                     }]
    	             });
    
    function getCheckValue(){
    	//CheckboxGroup取值方法    
    	var ss=[];
    	var j=0;
        for (var i = 0; i < checkboxgroup.items.length; i++)    
        {    
        	var val=checkboxgroup.items.get(i).inputValue;
            if (checkboxgroup.items.get(i).checked)    
            {   
            	grid.down('#'+val).show();
            	ss[j]=val;
            	j++;
            	                    
            } else{
            	grid.down('#'+val).hide();
            }   
        }
        return ss;
    }
    
    var sm = Ext.create('Ext.selection.CheckboxModel');
    var grid = Ext.create("Ext.grid.Panel",{
    	region: 'south',
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
        	       <cms:havePerm url='/statistic/exportLoginMerchantListExcel'>
 	               { xtype: 'button', text: '导出',iconCls:'Daochu',
     	    	      listeners: {
     	    		   click:function(){
     	    			   exportAll();
     	    		   }
     	    	   }
            }</cms:havePerm>
        	      
                  ],
              },
   
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{xtype : 'combobox',id : 'app_type_Filter',name : 'appType',fieldLabel : 'app类型',value:'',valueField : 'app_type',displayField : 'app_name',width : 250,
				     store : store_appType,editable:false,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',		      
				  {xtype : 'combobox',id : 'provinceFilter',name : 'province',fieldLabel : '省份',valueField : 'id',displayField : 'area',width : 250,
						store : provinceStore,
						listeners : { // 监听该下拉列表的选择事件
															select : function(
																	combobox,
																	record,
																	index) {
																Ext
																		.getCmp(
																				'cityFilter')
																		.setValue(
																				'');
																cityStore
																		.load({
																			params : {
																				parentId : combobox.value
																			}
																		});
															}
														},
						queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',
			   {xtype : 'combobox',id : 'cityFilter',name : 'city',fieldLabel : '城市',valueField : 'id',displayField : 'area',width : 250,
						store : cityStore,queryMode : 'local',labelAlign : 'left',labelWidth : 60},'-',	
				{ xtype: 'datetimefield',id: 'start_time',name: 'start_time',fieldLabel: '开始时间',format : 'Y-m-d',labelAlign:'left',labelWidth:60},'-',
		        { xtype: 'datetimefield',id: 'off_time',name: 'off_time',fieldLabel: '结束时间',format : 'Y-m-d',labelAlign:'left',labelWidth:60}
		        	      	       		
				   ]},
				  
				   {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	       checkboxgroup,
					
				   <cms:havePerm url='/statistic/getLoginMerchantList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',width : 100,listeners: {
    	    		   click:function(){		  
    	    			    var dimension=getCheckValue();
    	    		    	var app_type=Ext.getCmp('app_type_Filter').getValue();
    	    				var province=Ext.getCmp('provinceFilter').getRawValue();
    	    				var city=Ext.getCmp('cityFilter').getRawValue();
    	    				var start_time=Ext.getCmp('start_time').getValue();
    	    				var off_time=Ext.getCmp('off_time').getValue();
    	    				store.currentPage = 1;
     	    			    store.load({params:{start:0,page:1,limit:20,dimension:dimension,app_type : app_type,province:province,city:city,start_time:start_time,off_time:off_time}}); 
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
    provinceStore.load();
    store_appType.load();
 // 加载权限
    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
   
    // 双击grid记录，编辑代理商信息
     
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data);
    });
    
    
  //导出
	function exportAll() {
		var dimension=getCheckValue();
    	var app_type=Ext.getCmp('app_type_Filter').getValue();
		var province=Ext.getCmp('provinceFilter').getRawValue();
		var city=Ext.getCmp('cityFilter').getRawValue();
		var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
           window.location.href = '/statistic/exportLoginMerchantListExcel?dimension='+dimension+'&start_time='+start_time+'&off_time='+off_time
           +'&app_type='+app_type+'&province='+province+'&city='+city;	
	};
    
    
    
   Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: true,
		items : [
		         {
    //title: 'South Region (可调整大小)',
    region: 'south',     // 所在的位置
    xtype: 'panel',
    height: 400,
    items: [grid],
    collapsible: true,
    split: true, 
    layout: 'fit',// 允许调整大小
    margins: '0 5 5 5'
}, {
    //title: 'West Region (可折叠/展开)',
    region: 'west',
    xtype: 'panel',
    margins: '5 0 0 5',
    width: 500,
    items: [_tbfxChar],
    //collapsible: true,   // 可折叠/展开
    id: 'west-region-container',
    layout: 'fit'
}, {
   // title: 'Center Region (必须)',
    region: 'center',     // 必须指定中间区域
    xtype: 'panel',
    items: [chart1],
    layout: 'fit',
    margins: '5 5 0 0'
}]
	});
});	

//var bpanel = new Ext.Panel({ region : 'east', bodyStyle : 'border:none;',layout : 'border',width : 1000,autoHeight: true, frame : true, autoScroll : true, items: [chart1 ] }); 
//var cpanel = new Ext.Panel({ region : 'north', bodyStyle : 'border:none;',layout : 'border',height : 500, frame : true, autoScroll : true, items: [apanel,bpanel ] }); 

	
</script>
</head>
<body>

</body>
</html>