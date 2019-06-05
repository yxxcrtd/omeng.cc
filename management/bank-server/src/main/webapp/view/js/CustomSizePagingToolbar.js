Ext.define("Ext.ux.CustomSizePagingToolbar", {// 定义的名字要和文件的名字大小写一样
    extend : "Ext.toolbar.Paging",
    alias : "widget.custompaging",// 别名
    beforSizeText : "每页",
    afterSizeText : "条",
    getCustomItems : function() {
        var me = this;
        // 自定义customComStore
        var customComStore = Ext.create("Ext.data.JsonStore", {
            fields : [ "customPageSize" ],
            data : [ {
                customPageSize : "10"
            }, {
                customPageSize : "20"
            }, {
                customPageSize : "50"
            }, {
                customPageSize : "100"
            } ]
        })
        // 自定义customComboBox
        var customComboBox = Ext.create("Ext.form.field.ComboBox", {
            itemId : "customComboId",
            store : customComStore,
            queryMode : "local",
            displayField : "customPageSize",
            valueField : "customPageSize",
            enableKeyEvents : true,// 感应键盘事件
            width : 60,
            listeners : {
                scope : me,// 作用域
                select : me.onCustomSelect,
                keydown : me.onCustomKeyDown,
                blur : me.onCustomBlur
            }
        });
        // - 表示分割线,> 表示右边显示
        return [ "-", me.beforSizeText, customComboBox, me.afterSizeText ];
    },
    onCustomSelect : function(combo, records, eOpts) {// 选择事件触发
        var me = this;
        me.store.pageSize = records[0].data.customPageSize;
        me.store.loadPage(1);// 默认加载第一页
    },
    onCustomKeyDown : function(field, e, eOpts) {// 按键事件触发
        var me = this;
        var k = e.getKey();
        if (k == e.ENTER) {
            e.stopEvent();// 停止其他事件
            me.store.pageSize = me.child("#customComboId").getValue();
            me.store.loadPage(1);
        }
    },
    onCustomBlur : function(combo, the, eOpts) {// 失去焦点事件
        var me = this;
        me.child("#customComboId").setValue(me.store.pageSize);
    },
    // 初始化界面组件
    initComponent : function() {
        var me = this;
        Ext.apply(me, {// 应用、附加
            items : me.getCustomItems()
        });
        me.callParent(arguments);
        me.child("#customComboId").setValue(me.store.pageSize);// 初始化赋值
    }
})