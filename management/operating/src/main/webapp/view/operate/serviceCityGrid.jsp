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
<title>服务城市管理</title>
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


var grid;
var store;
Ext.onReady(function() {
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'编号',dataIndex:'id',hidden:true,align:'center'},
                   {header:'省',dataIndex:'province',align:'center'},
                   {header:'市',dataIndex:'city',align:'center'},
                   {header:'状态',dataIndex:'is_open',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>已开通</span>";  
                       } else{  
                           return "<span style='color:green;font-weight:bold';>未开通</span>";  
                       }
           		   }},
                   {header: '操作',  dataIndex: 'is_open',  sortable:true,align:'center',fixed:false,renderer:function(value,v,r){
       	              if(value=='1'){
       	         	      return '<a href="javascript:operServiceCity(0)"><span style="color:green;font-weight:bold";>关闭</span></a>'; 
                 	  }else{
                 		  return '<a href="javascript:operServiceCity(1)"><span style="color:red;font-weight:bold";>开通</span></a>'; 
                 	  }
                    }}
               ];
	//创建store数据源
    
    //列表展示数据源
    store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/operate/serviceCityList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'province'},  
           {name:'city'},
           {name:'is_open'}
        ]  
    });

    // 开通状态
    var openComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[
    	      ['全部',''],
    	      ['已开通','1'],
    	      ['未开通','0']
    	]
    });
    
	// 地区信息
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
		url : '/common/showServiveCity',
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
		var province = Ext.getCmp('province').getRawValue();
		var city = Ext.getCmp('city').getRawValue();
		var is_open=Ext.getCmp('is_open').getValue();
        var new_params = {province:province,city:city,is_open:is_open};    
        Ext.apply(store.proxy.extraParams, new_params);    
    });
    var sm = Ext.create('Ext.selection.CheckboxModel');
    grid = Ext.create("Ext.grid.Panel",{
    	region: 'center',
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
        	items:[
                 <cms:havePerm url='/operate/addServiceCity'>
        	       { xtype: 'button',id:'add', text: '增加',iconCls:'NewAdd',
        	    	   listeners: {
        	    		   click:function(){
        	    			   showform(null);
        	    		   }
        	    	   }
        	       },'-',</cms:havePerm>
        	       <cms:havePerm url='/operate/delServiceCity'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delRecordAll();
        	    		   }
        	      }},'-',</cms:havePerm>

        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
			        {
						xtype : 'combobox',
						id : 'province',
						name : 'province',
						fieldLabel : '省',
						valueField : 'id',
						hiddenName:'id',
						labelWidth:40,
						displayField : 'area',
						store : provinceStore,
						listeners : { // 监听该下拉列表的选择事件
							select : function(combobox,record,index) {
								Ext.getCmp('city').setValue('');
								cityStore.load({
											params : {
												parentId : combobox.value
											}
										});
							}
						},
						queryMode : 'local',
						labelAlign : 'left'
					     },'-',
					     {
								xtype : 'combobox',
								name : 'city',
								id : 'city',
								labelWidth:40,
								fieldLabel : '市',
								valueField : 'id',
					   			hiddenName:'id',
								displayField : 'area',
								store : cityStore,
								queryMode : 'local',
								labelAlign : 'left'
						 },'-',
     	           {
			     	xtype:'combo',
					fieldLabel : '开通状态',
					id: 'is_open',
				    store : openComboStore,
	    		    name:'is_open',
	   			    triggerAction: 'all',
	   			    displayField: 'name',
	   			    valueField: 'value',
	   			    value:'',
	   			    editable:false,
	   			    labelWidth:60,
				    mode:'local'
			        },'-',
				   { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var province = Ext.getCmp('province').getRawValue();
    	    			   var city = Ext.getCmp('city').getRawValue();
    	    			   var is_open=Ext.getCmp('is_open').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,province:province,city:city,is_open:is_open}}); 
    	    		}
    	    		}}
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
  
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    <cms:havePerm url='/operate/editServiceCity'>
    grid.on("itemdblclick",function(grid, row){
    	showform(row.data);
    });</cms:havePerm>

    
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	
	//删除
	 function delRecordAll()  
     {  
          //grid中复选框被选中的项  
          var rows = grid.getSelectionModel().getSelection();  
          //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
          var id = '';  
          for(var i = 0;i<rows.length;i++)  
          {  
             var is_open = rows[i].get('is_open');	
              if(is_open==1){
           	     //未开通
           	     Ext.example.msg("提示","选择删除的记录必须是【非开通】数据！");
                 return ;  
             }
        	  
             if(i>0)  
             {  
                 id = id+','+rows[i].get('id');  
             }else{  
                 id = id+rows[i].get('id');  
             }  
          }  
          //没有选择要执行操作的对象  
            
          if(id == "")  
          {  
			 Ext.example.msg("提示","请选择要删除的对象！");
             return ;  
          }else{  
             Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
                 if(btn == 'yes')  
                 {  
                     Ext.Ajax.request({  
                         url:"/operate/delServiceCity",  
                         params:{ids:id},  
                         method:'post',  
                         success:function(o){  
                        	 Ext.example.msg("提示","删除成功！");
                             store.reload();  
                             return ;  
                         },  
                         failure:function(form,action){ 
                        	 Ext.example.msg("提示","删除失败！");
                         }  
                     });    
                 }  
             });  
          }  
     } ;


   
 	var showform=function(d){

 	    var add_winForm =  Ext.create('Ext.form.Panel', {  
 	                frame: true,   //frame属性  
 	                //title: 'Form Fields',  
 	                width: 470, 
 	                height:170,
 	                // autoScroll: true, 
 	                bodyPadding:5,
 	                fileUpload:true,
 	                //renderTo:"panel21",  
 	                fieldDefaults: {  
 	                    labelAlign: 'left',  
 	                    labelWidth: 90,  
 	                    anchor: '90%'  
 	                },  
 	                items: [{
	    				xtype: 'hidden',
	    				name: "id"
	    			},{
						xtype : 'combobox',
						name : 'vprovince',
						fieldLabel : '省',
						valueField : 'id',
						hiddenName:'id',
						displayField : 'area',
						store : provinceStore,
						editable:false,
						listeners : { // 监听该下拉列表的选择事件
							select : function(combobox,record,index) {
								//Ext.getCmp('city').setValue('');
								add_winForm.form.findField('vcity').setValue('');
								cityStore.load({
											params : {
												parentId : combobox.value
											}
										});
							}
						},
						queryMode : 'local',
						labelAlign : 'left',
					    anchor: '97.5%'
					},{
						xtype : 'combobox',
						name : 'vcity',
					    anchor: '97.5%',
						fieldLabel : '市',
						editable:false,
						valueField : 'id',
			   			hiddenName:'id',
						displayField : 'area',
						store : cityStore,
						queryMode : 'local',
						labelAlign : 'left'
					},{
	    				xtype: 'hidden',
	    				name: "province"
	    			},{
	    				xtype: 'hidden',
	    				name: "city"
	    			}
 	               ]  
 	            });  
 	    
	    var title = '新增服务城市';
	    var reqName = 'addServiceCity';
	    //================判断是编辑还是新增===============
	    	if(d!=null&&d.id!=null&&d.id!=0){
	    		// 是编辑状态
	    	    add_winForm.form.findField('id').setValue(d.id);
	    	    add_winForm.form.findField('vprovince').setValue(d.province);  
	    	    add_winForm.form.findField('vcity').setValue(d.city);  
	        	title = '编辑服务城市';
	        	reqName = 'editServiceCity';
	    	}

        //================判断是编辑还是新增===============
 	    
 	    //创建window面板，表单面板是依托window面板显示的  
 	    var syswin = Ext.create('Ext.window.Window',{  
 	              title : title,  
 	              width : 470, 
 	              height: 220,
 	              //height : 120,  
 	              //plain : true,  
 	              iconCls : "addicon",  
 	              // 不可以随意改变大小  
 	              resizable : false,  
 	              //autoScroll: true,  
 	              // 是否可以拖动  
 	              // draggable:false,  
 	              collapsible : true, // 允许缩放条  
 	              closeAction : 'close',  
 	              closable : true,  
 	              // 弹出模态窗体  
 	              modal : 'true',  
 	              buttonAlign : "center",  
 	              bodyStyle : "padding:0 0 0 0",  
 	              items : [add_winForm],  
 	              buttons : [{  
 	                         text : "保存",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                        	if (add_winForm.getForm().isValid()) {
 	                       	        var p = add_winForm.form.findField('vprovince').getRawValue();  
 	                   	            var c = add_winForm.form.findField('vcity').getRawValue();  
 	                   	            add_winForm.form.findField('province').setValue(p);  
 	                   	            add_winForm.form.findField('city').setValue(c);  
 	                   	            if(p==null||p==''||c==null||c==''){
 	                   	                Ext.example.msg("提示","必须选择省市信息");
 	                   	                return;
 	                   	            }
 	                                add_winForm.getForm().submit({  
 	                                          url :'/operate/'+reqName,  
 	                                           //等待时显示 等待  
 	                                          waitTitle: '请稍等...',  
 	                                          waitMsg: '正在提交信息...',  
 	                                          success: function(fp, o) {
 	                                        	 if (o.result.data == 1) {  
 	                                        		  syswin.close(); //关闭窗口  
	                                             	  Ext.example.msg("提示",o.result.message);
	                                                  store.reload();  
	                                              }else {  
	                                            	  Ext.example.msg("提示",o.result.message);
	                                              }  
 	                                          },  
 	                                          failure: function() {
 	                                          	 Ext.example.msg("提示",o.result.message);
 	                                          }  
 	                                       });  
 	                            } } 
 	                         }  
 	                    , {  
 	                         text : "关闭",  
 	                         minWidth : 70,  
 	                         handler : function() {  
 	                            syswin.close();  
 	                         }  
 	                     }]  
 	           });  
 	    syswin.show();  
 	    };
 	 
});

function operServiceCity(v){
	var rows = grid.getSelectionModel().getSelection();  
    if(rows.length == 0)  
    {  
  	   Ext.example.msg("提示","请选择要操作的对象！");
       return ;  
    }
   if(rows.length > 1)  
   {  
	  Ext.example.msg("提示","只能选择一个操作的对象！");
      return ;  
    }
   var ids = rows[0].get('id');
   var req = "openServiceCity";
   if(v!=1){
	   req = "closeServiceCity";
   }  
   Ext.Ajax.request({  
       url:"/operate/"+req,  
       params:{ids:ids},  
       method:'post',  
       success:function(o){  
      	   Ext.example.msg("提示","操作成功！");
           store.reload();  
           return;  
       },  
       failure:function(form,action){ 
      	   Ext.example.msg("提示","操作失败！");
       }  
   });   
};



</script>

</head>
<body>
</body>
</html>