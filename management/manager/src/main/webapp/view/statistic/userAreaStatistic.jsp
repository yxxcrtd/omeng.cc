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
<title>地域统计</title>
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

	var chartStore = Ext.create("Ext.data.Store",{
        proxy:{  
            type:'ajax',  
            url:'/userTerminal/userAreaColList',  
            reader:{  
                type:'json',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'areaName'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
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
	            title: '用户数',
	            grid: true,
	            minimum: 0
	        },
	        {
	            type: 'Category',
	            position: 'bottom',
	            fields: ['areaName'],
	            title: '用户地域分布图 TOP-10'
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
	                this.setTitle(storeItem.get('areaName') + ': '+storeItem.get('num'));
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
	            xField: 'areaName',
	            yField: 'num',
	            //此渲染器的存在能够使每条柱子的颜色，与上方声明的颜色数组相同
	            renderer: function(sprite, storeItem, barAttr, i, store) {  
	                barAttr.fill = colors[i];
	                return barAttr;  
	            }
	        }
	    ]
	});
    chartStore.load({params:{dimension:'province'}});  
	
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
                       items:[{xtype: 'button', id:'select1',text: '省份',width : 60,listeners: {
              			   click:function(){
              				   var dimension='province';
              				   chartStore.load({params:{dimension:dimension}}); 
              			   }}},
              		   {xtype: 'button', id:'select2',text: '城市',width : 60,listeners: {
              				   click:function(){		  
              					   var dimension='city';
              					   chartStore.load({params:{dimension:dimension}}); 
              				   }}}]  
                }]  
               
        }],  
        items:chart  
    }); 
    
	 var columns = [
	                   {header:'序号',xtype: 'rownumberer',width:50},
	                   {header:'省份',itemId: 'province',align:'center',dataIndex:'province',sortable:true,hidden:true,fixed:false},
	                   {header:'城市',itemId: 'city',align:'center',dataIndex:'city',sortable:true,hidden:true,fixed:false},
	                   {header:'累计用户',itemId: 'accumulateNum',align:'center',dataIndex:'accumulateNum',sortable:true,fixed:false},
	                   {header:'新增用户',itemId: 'addNum',align:'center',dataIndex:'addNum',sortable:true,fixed:false},
	                   {header:'活跃用户',itemId: 'activityNum',align:'center',dataIndex:'activityNum',sortable:true,fixed:false}
	               ];
	  
	    var store = Ext.create("Ext.data.Store",{
	    	pageSize:20, //每页显示几条数据  
	        proxy:{  
	            type:'ajax',  
	            url:'/userTerminal/userAreaList',  
	            reader:{  
	                type:'json',  
	                totalProperty:'total',  
	                root:'data',  
	                idProperty:'ext'  
	            }  
	        },  
	        fields:[  
	           {name:'province'}, 
	           {name:'city'}, 
	           {name:'accumulateNum'}, 
	           {name:'addNum'}, 
	           {name:'activityNum'}
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
	    //点击下一页时传递搜索框值到后台  
	    store.on('beforeload', function (store, options) { 
	    	var dimension=getCheckValue();
			var province=Ext.getCmp('provinceFilter').getRawValue();
			var city=Ext.getCmp('cityFilter').getRawValue();
			var startTime=Ext.getCmp('startTime').getValue();
			var endTime=Ext.getCmp('endTime').getValue();
			var new_params = {dimension:dimension,province:province,city:city,startTime:startTime,endTime:endTime};    
	        Ext.apply(store.proxy.extraParams, new_params); 
	    });  
	    var checkboxgroup = new Ext.form.CheckboxGroup({
            fieldLabel: '维度',
            width: 500,
            items: [{
                boxLabel: '省份',
                inputValue: 'province'
            }, {
                boxLabel: '城市',
                  inputValue: 'city'
               }]
        });


	        function getCheckValue() {
			//CheckboxGroup取值方法    
			var ss = [];
			var j = 0;
			for (var i = 0; i < checkboxgroup.items.length; i++) {
				var val = checkboxgroup.items.get(i).inputValue;
				if (checkboxgroup.items.get(i).checked) {
					grid.down('#' + val).show();
					ss[j] = val;
					j++;

				} else {
					grid.down('#' + val).hide();
				}
			}
			return ss;
		}
		var sm = Ext.create('Ext.selection.CheckboxModel');
		var grid = Ext.create("Ext.grid.Panel", {
			border : false,
			store : store,
			selModel : sm,
			columns : columns,
			region : 'center', //框架中显示位置，单独运行可去掉此段
			loadMask : true, //显示遮罩和提示功能,即加载Loading……  
			forceFit : true, //自动填满表格  
			columnLines : false, //列的边框
			rowLines : true, //设置为false则取消行的框线样式
			dockedItems : [
					{
						xtype : 'toolbar',
						dock : 'top',
						displayInfo : true,
						items : [
								{
									xtype : 'combobox',
									id : 'provinceFilter',
									name : 'province',
									fieldLabel : '省份',
									valueField : 'id',
									displayField : 'area',
									width : 250,
									store : provinceStore,
									listeners : { // 监听该下拉列表的选择事件
										select : function(combobox, record,
												index) {
											Ext.getCmp('cityFilter').setValue(
													'');
											cityStore.load({
												params : {
													parentId : combobox.value
												}
											});
										}
									},
									queryMode : 'local',
									labelAlign : 'left',
									labelWidth : 60
								},
								'-',
								{
									xtype : 'combobox',
									id : 'cityFilter',
									name : 'city',
									fieldLabel : '城市',
									valueField : 'id',
									displayField : 'area',
									width : 250,
									store : cityStore,
									queryMode : 'local',
									labelAlign : 'left',
									labelWidth : 60
								},
								'-',
								{
									xtype : 'datefield',
									id : 'startTime',
									name : 'startTime',
									fieldLabel : '开始日期',
									format : 'Y-m-d',
									labelAlign : 'left',
								    value:Ext.util.Format.date(Ext.Date.add(new Date(),Ext.Date.DAY,-1),"Y-m-d"), 
									labelWidth : 75
								},
								'-',
								{
									xtype : 'datefield',
									id : 'endTime',
									name : 'endTime',
									fieldLabel : '结束日期',
									format : 'Y-m-d',
									labelAlign : 'left',
									value:Ext.util.Format.date(Ext.Date.add(new Date(),Ext.Date.DAY,-1),"Y-m-d"), 
									labelWidth : 75
								},
								'-',
								{
									xtype : 'button',
									id : 'select',
									text : '查询',
									iconCls : 'Select',
									width : 100,
									listeners : {
										click : function() {
											var dimension = getCheckValue();
											var province = Ext.getCmp(
													'provinceFilter')
													.getRawValue();
											var city = Ext.getCmp('cityFilter')
													.getRawValue();
											var startTime = Ext.getCmp(
													'startTime').getValue();
											var endTime = Ext.getCmp('endTime')
													.getValue();
											store.currentPage = 1;
											store.load({
												params : {
													start : 0,
													page : 1,
													limit : 50,
													dimension : dimension,
													province : province,
													city : city,
													startTime : startTime,
													endTime : endTime
												}
											});
										}
									}
								},'->',
                                 <cms:havePerm url='/userTerminal/exportUserAreaListExcel'>
		                         { xtype: 'button', text: '导出',iconCls:'Daochu',
			    	                  listeners: {
			    		                click:function(){
			    			              exportAll();
			    		            }
			    	                   }
		                         }</cms:havePerm> ]
					}, {
						xtype : 'toolbar',
						dock : 'top',
						displayInfo : true,
						items : [ checkboxgroup ]
					} ],
			bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
				displayInfo : true,
				store : store
			})
		});

		//加载数据  
		store.load({
			params : {
				start : 0,
				limit : 50
			}
		});
		// 加载权限
		provinceStore.load();
		
		//导出
		function exportAll() {
			var dimension=getCheckValue();
			var province=Ext.getCmp('provinceFilter').getRawValue();
			var city=Ext.getCmp('cityFilter').getRawValue();
			var startTime=Ext.getCmp('startTime').getValue();
			var endTime=Ext.getCmp('endTime').getValue();
	         window.location.href = '/userTerminal/exportUserAreaListExcel?province='+province+'&city='+city+'&dimension='+dimension+'&startTime='+startTime+'&endTime='+endTime;	
		};

		grid.on("itemcontextmenu", function(view, record, item, index, e) {
			e.preventDefault();
			contextmenu.showAt(e.getXY());
		});

		// 整体架构容器
		Ext.create("Ext.container.Viewport", {
			layout : 'border',
			autoHeight : true,
			border : true,
			items : [ {
				region : 'south', // 所在的位置
				xtype : 'panel',
				height : 400,
				items : [ grid ],
				layout : 'fit',
				split : true, // 允许调整大小
				margins : '0 5 5 5'
			}, {
				//title: '商户设备机型Top-10',
				region : 'center', // 必须指定中间区域
				xtype : 'panel',
				items : [ _tbfxChar ],
				layout : 'fit',
				margins : '5 5 0 5'
			} ]
		});

	});
</script>
</head>
<body>
</body>
</html>