Ext.Loader.setConfig({
	enabled : true
});

Ext.Loader.setPath({
	'Ext.ux' : '../ExtJS4.2/ux'
});


Ext.onReady(function() {
	var rightPanel = Ext.create('Ext.tab.Panel', {
				activeTab : 0,
				border : false,
				autoScroll : true,
				//iconCls:'Applicationviewlist',
				region : 'center',
				items : [{
							title : '首页',
							iconCls: 'House',
							html: '<iframe frameborder="no" border=0 height="100%" width="100%" src="/systemUser/showBlank" scrolling="auto"></iframe>',
						}],
				plugins: [Ext.create('Ext.ux.TabCloseMenu',{
		        		  	closeTabText: '关闭当前',
		        		  	closeOthersTabsText: '关闭其他',
		        		  	closeAllTabsText: '关闭所有'
		        		  })]
			});
	
	var tree = Ext.create("Ext.panel.Panel", {
		height : '70%',
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

	// 左边下部Panel
	var detailsPanel = {
		id : 'details-panel',
		iconCls : "User",
		collapsible : true, // 是否折叠
		title : '信息',
		region : 'center',
		bodyStyle : 'padding-bottom:15px;background:#eee;',
		autoScroll : true,
		html : '<p class="details-info">用户名：李杰<br />部&nbsp;&nbsp;&nbsp;门：管理部<br />登录IP：156.15.26.101</p>'
	};
	// 左边下部Panel结束
	
	var model = Ext.define("TreeModel", { // 定义树节点数据模型
		extend : "Ext.data.Model",
		fields : [{name : "id",type : "string"},
				{name : "text",type : "string"},
				{name : "iconCls",type : "string"},
				{name : "leaf",type : "boolean"},
				{name : 'url',type:"string"},
				{name : 'description',type:"string"}]
	});
	
	//组建树
	var buildTree = function(pid) {
		return Ext.create('Ext.tree.Panel', {
					rootVisible : false,
					border : false,
					lines:false,
					autoScroll:true,
					store : Ext.create('Ext.data.TreeStore', {
								model : model,
								proxy: {
							         type: 'ajax',
							         url: '/homePage/showSubMenu/'+pid,
							         //							         url:'/manager/role/showMenu',
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
									if (leaf) { //判断是否点中
										var panel = Ext.create('Ext.panel.Panel',{
											title : text,
											closable : true,
											iconCls : icon,
											html : '<iframe width="100%" height="100%" frameborder="0" src='+url+'></iframe>'
										});
										rightPanel.add(panel);
										rightPanel.setActiveTab(panel);
									}
								},
								scope : this
							}
				});
	};

	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		items : [ {
			region : 'north',
			contentEl : 'north-div',
			height : 73,
			bodyStyle : 'background-color:#BBCCEE;'
		}, {
			region : 'south',
			contentEl : 'south-div',
			height : 20,
			bodyStyle : 'background-color:#BBCCEE;'
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
			items : [ tree, detailsPanel ]
		},
			rightPanel
		],
		listeners : {
			afterrender : function() {
				Ext.getBody().mask('正在加载系统菜单....');
				 Ext.Ajax.request({
					 url:'/homePage/showMenu',
					 success : function(response) {
				     Ext.getBody().unmask();	 
					 console.log(response);
					 console.log(response.responseText);
						 var json = Ext.JSON.decode(response.responseText);
						 console.log(json);
						  console.log(json.code);
						 if(json.code == 'OK'){
							 Ext.each(json.data, function(el) {
								 var panel = Ext.create('Ext.panel.Panel', {
									 			id : el.id,
									 			title : el.text,
									 			iconCls:el.iconCls,
									 			leaf	:el.leaf,
									 			layout : 'fit'
										 });
								 panel.add(buildTree(el.id));
								 tree.add(panel);
							 });
						 }else{}
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