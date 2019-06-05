Ext.onReady(function () {
    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        items: [{
            region: 'north',
            html: '<h1 class="x-panel-header" style="color:red;text-align:center;">善金科技管理平台</h1>',
            border: false,
            height: 50,
            margins: '0 0 0 0'
        }, {
            region: 'west',
            collapsible: true,
            split: true,
            id: 'MainMenu',
            title: '系统导航',
            width: 150,
            layout: 'accordion',
            items: [
                {
                    title: '系统管理',
                    layout: 'fit',
                    items: [
                        {
                            xtype: 'treepanel',
                            border: 0,
                            rootVisible: false,
                            root: {
                                expanded: true,
                                children: [
                                    { id: "01", text: "用户管理", leaf: true, href: 'http://www.baidu.com' },
                                    { id: "02", text: "密码修改", leaf: true, href: '#' },
                                    { id: "03", text: "权限管理", leaf: true, href: '#' }
                                ]
                            }
                        }
                    ]
                },
                {
                    title: '用户管理',
                    layout: 'fit',
                    items: [
                        {
                            xtype: 'treepanel',
                            border: 0,
                            rootVisible: false,
                            root: {
                                expanded: true,
                                children: [
                                    { id: "01", text: "用户管理", leaf: true, href: '#' },      
                                ]
                            }
                        }
                    ]
                },
                {
                    title: '商户管理',
                    layout: 'fit',
                    items: [
                        {
                            xtype: 'treepanel',
                            border: 0,
                            rootVisible: false,
                            root: {
                                expanded: true,
                                children: [
                                    { id: "01", text: "商户管理", leaf: true, href: '#' },
                                ]
                            }
                        }
                    ]
                },
                {
                    title: '订单管理',
                    layout: 'fit',
                    items: [
                        {
                            xtype: 'treepanel',
                            border: 0,
                            rootVisible: false,
                            root: {
                                expanded: true,
                                children: [
                                    { id: "01", text: "订单管理", leaf: true, href: '#' },
                                ]
                            }
                        }
                    ]
                },
            ]
            // could use a TreePanel or AccordionLayout for navigational items
        }, {
            region: 'south',
            collapsible: false,
            html: '状态栏',
            split: false,
            height: 22
        }, {
            region: 'east',
            title: '在线用户',
            collapsible: true,
            split: true,
            width: 150
        }, {
            region: 'center',
            xtype: 'tabpanel', 
            id: 'MainTabPanel',
            activeTab: 0,  
            items: {
                title: '首页',
                html: '<h1>欢迎使用</h1><input type="button" value="添加新标签" onclick="CreateIframeTab(\'MainTabPanel\',\'01\', \'系统管理\', \'http://www.baidu.com\');" />'
            }
        }]
    });

    bindNavToTab("MainMenu", "MainTabPanel");
});

function bindNavToTab(accordionId, tabId) {
    var accordionPanel = Ext.getCmp(accordionId);
    if (!accordionPanel) return;

    var treeItems = accordionPanel.queryBy(function (cmp) {
        if (cmp && cmp.getXType() === 'treepanel') return true;
        return false;
    });
    if (!treeItems || treeItems.length == 0) return;

    for (var i = 0; i < treeItems.length; i++) {
        var tree = treeItems[i];

        tree.on('itemclick', function (view, record, htmlElement, index, event, opts) {
            if (record.isLeaf()) {
                // 阻止事件传播
                event.stopEvent();

                var href = record.data.href;

                if (!href) return;
                // 修改地址栏
                window.location.hash = '#' + href;
                // 新增Tab节点
                CreateIframeTab(tabId, record.data.id, record.data.text, href);
            }
        });
    }
}

function CreateIframeTab(tabpanelId, tabId, tabTitle, iframeSrc) {
    var tabpanel = Ext.getCmp(tabpanelId);
    if (!tabpanel) return;  //未找到tabpanel，返回

    //寻找id相同的tab
    var tab = Ext.getCmp(tabId);
    if (tab) { tabpanel.setActiveTab(tab); return; }

    //新建一个tab，并将其添加到tabpanel中
    //tab = Ext.create('Ext.tab.Tab', );
    tab = tabpanel.add({
        id: tabId,
        title: tabTitle,
        closable: true,
        html: '<iframe style="overflow:auto;width:100%; height:100%;" src="' + iframeSrc + '" frameborder="0"></iframe>'
    });
    tabpanel.setActiveTab(tab);
}