Ext.Loader.setConfig({
	enabled : true
});

Ext.Loader.setPath({
	'Ext.ux' : '../ExtJS4.2/ux'
});
var tabMax=0;
var rightPanel;
var addTab;
var removeTab;
Ext.onReady(function() {
	rightPanel = Ext.create('Ext.tab.Panel', {
				activeTab : 0,
				border : false,
				autoScroll : true,
				//iconCls:'Applicationviewlist',
				region : 'center',
				items : [{
							title : '首页',
							iconCls: 'Shouye',
							html: '<iframe frameborder="no" border=0 height="100%" width="100%" src="/systemManager/showBlank" scrolling="auto"></iframe>',
						}],
				plugins: [Ext.create('Ext.ux.TabCloseMenu',{
		        		  	closeTabText: '关闭当前',
		        		  	closeOthersTabsText: '关闭其他',
		        		  	closeAllTabsText: '关闭所有'
		        		  })]
			});
	
	var tree = Ext.create("Ext.panel.Panel", {
		height : '100%',
		region : 'north',
		layout : {
			// layout-specific configs go here
			type : 'accordion',
			titleCollapse : true,
			animate : true,// 动画切换效果
			activeOnTop : false
		// 折叠展开是否改变菜单顺序
		},
		layoutConfig : {
			animate : true
		},
		split : true
	});


	var model = Ext.define("TreeModel", { // 定义树节点数据模型
		extend : "Ext.data.Model",
		fields :[{name : "id",type : "string"},
				{name : "text",type : "string"},
				{name : "iconCls",type : "string"},
				{name : "leaf",type : "boolean"},
				{name : 'url',type:"string"},
				{name : 'description',type:"string"}]
	});
	
	//组建树
	var buildTree = function(children) {
		var childrenUrl = '/view/menuTree/'+children + '.jsp';
		
		   return getRootTree(childrenUrl);
		
	};
	
	/*添加根目录*/ 
 var getRootTree=function(childrenUrl){
     var rootTree = Ext.create('Ext.tree.Panel', {
					rootVisible : false,
					border : false,
					lines:true,
					autoScroll:true,
					store : Ext.create('Ext.data.TreeStore', {
								model : model,
								proxy: {
							         type: 'ajax',
							         url: childrenUrl,
							         reader: {
							             type: 'json',
							             root: 'data'
							         }
							     },
							     autoLoad: true
							}),
							listeners : {
								'itemclick' : function(view, record, item, index, e) {
									var leaf = record.get('leaf');
									var id = record.get("id");
									var text = record.get('text');
									var icon = record.get('iconCls');
									var url = record.get('url')+'?id='+id;
									if (leaf) { //判断是否点中,并且未被打开
										addTab(id,text,icon,url);
									}
								},
								scope : this
							}
				});
	return 	rootTree;	
}

 /*添加tab页带关闭功能,如果已经添加过了则设为活动项，没有添加的就进行新tab添加*/ 

	addTab=function(tabid, title, icon, url) {
				 var leafPannel = Ext.getCmp("leafNode"+tabid);
				
				 if(leafPannel==undefined){
				 var panel = Ext.create('Ext.panel.Panel',{
				 id : "leafNode"+tabid,
				 title : title,
				 closable : true,
				 iconCls : icon,
				 html : '<iframe width="100%" height="100%" frameborder="0" src="'+url+'"></iframe>'
				 });
				 rightPanel.add(panel);
				 rightPanel.setActiveTab(panel);
				
				 }else{
				 rightPanel.setActiveTab(leafPannel);
				 }

	}
	 /*tab页带关闭功能*/ 
	removeTab=function(tabid) {
				 var leafPannel = Ext.getCmp("leafNode"+tabid);
				 if(leafPannel==undefined){
				
				 }else{
				 
				  rightPanel.remove(leafPannel);
				 
				 }

	}
	
// BBCCEE
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		items : [ {
			region : 'north',
			contentEl : 'north-div',
			height : 110,
			bodyStyle : 'background-color:#AACCDD;'
		}, {
			region : 'south',
			contentEl : 'south-div',
			height : 20,
			bodyStyle : 'background-color:#232323;'
		}, {
			layout : 'border',
			title : '菜单',
			id : 'layout-browser',
			region : 'west',
			border : false,
			split : true,
			margins : '2 0 5 5',
			width : 200,
			minSize : 100,
			maxSize : 500,
			autoScroll : false,
			collapsible : true, // 是否折叠
			//iconCls : "Application",
			items : [ tree ]
		},
			rightPanel
		],
		listeners : {
			afterrender : function() {
				Ext.getBody().mask('正在加载系统菜单....');
				 Ext.Ajax.request({
					 url:'/view/menuTree/rootMenu.jsp',
					 success : function(response) {
				             Ext.getBody().unmask();	 
				             var json = Ext.JSON.decode(response.responseText);
					         Ext.each(json.data, function(el) {
					         var panel = Ext.create('Ext.panel.Panel', {
								 			id : el.id,
								 			title : el.text,
								 			iconCls:el.iconCls,
								 			leaf	:el.leaf,
								 			layout : 'fit'
									 });
					         if(el.children!=null&&el.children!=''){
								 panel.add(buildTree(el.children));
					         }
							 tree.add(panel);
						 });
					 },failure : function(request) {
							Ext.MessageBox.show({
										title : '操作提示',
										msg : "连接服务器失败",
										buttons : Ext.MessageBox.OK,
										icon : Ext.MessageBox.ERROR
									});
						},
						method : 'post'
				 });
			}
		}
	});


	
});
