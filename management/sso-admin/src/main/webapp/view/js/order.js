var switchAppType = function() {

detailform();

}

var autoloadcontrol =new Ext.form.FieldSet({
               layout: "column",
               title: "属性表单",
           });
           
 
    // 旅行者订单详情页
var detailform = function(order_id, app_type, service_type,orderTypeName) {
	var detail_winForm = Ext.create('Ext.form.Panel', {
		frame : true, // frame属性
		// title: 'Form Fields',
		width : 440,
		height : 350,
		bodyPadding : 5,
		// renderTo:"panel21",
		fieldDefaults : {
			labelAlign : 'left',
			labelWidth : 90,
			anchor : '90%'
		},
		items :autoloadcontrol
	});

	
	var detailwindow = Ext.create('Ext.window.Window', {
		title : "订单详情",
		width : 450,
		height : 350,
		// height : 120,
		// plain : true,
		iconCls : "addicon",
		// 不可以随意改变大小
		resizable : true,
		// 是否可以拖动
		draggable : true,
		collapsible : true, // 允许缩放条
		closeAction : 'close',
		closable : true,
		// 弹出模态窗体
		modal : 'true',
		buttonAlign : "center",
		bodyStyle : "padding:0 0 0 0",
		items : [ detail_winForm ],
		buttons: [{
                    xtype: "button",text:"addform", listeners: {
                        "click": function () {
                            Ext.Ajax.request({
                                url: "/order/getOrderDet",
                                success: function (response) {
                                     //后台得到字符串
                                    autoloadcontrol.add(eval(response.responseText))
                                    autoloadcontrol.doLayout();   //用来刷新FormPanel
                                },
                            })
                        }
                    }
                }]
	});
	detailwindow.show();
};
    