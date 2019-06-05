<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../common/common.jsp"%>
<%
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setHeader("Expires","0");

	request.setCharacterEncoding("UTF-8");	
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统操作管理</title>
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
    var columns = [
                   {header:'序号',xtype: 'rownumberer',width:50},
                   {header:'日志ID',dataIndex:'id',sortable:true,fixed:false,hidden:true},
                   {header:'日志名称',dataIndex:'source_name',sortable:true,fixed:false},
                   {header:'操作类型',dataIndex:'operate_type',sortable:true,fixed:false,width:50},
                   {header:'请求URL',dataIndex:'source_url',sortable:true,fixed:false},
                   {header:'操作用户',dataIndex:'operate_user',sortable:true,fixed:false,width:50},
                   {header:'操作IP',dataIndex:'operate_ip',sortable:true,fixed:false,width:60},
                   {header:'操作时间',dataIndex:'operate_time',sortable:true,fixed:false,width:60}
               ];
    var typeComboStore = new Ext.data.SimpleStore({
    	fields:['typeName','operate_type'],
    	data:[['全部',''],
    	      ['新增','新增'],
    	      ['更新','更新'],
    	      ['删除','删除'],    
    	      ['上传','上传'],
    	      ['下载','下载'],
    	      ['导出','导出'],
    	      ['导入','导入'],
	          ['登录','登录'],
	          ['登出','登出']
    	]
    });
    
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据
    	remoteSort: true, //设置属性进行请求后台排序
        proxy:{  
            type:'ajax',  
            url:'/systemManager/systemLogList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'source_name'},  
           {name:'operate_type'},  
           {name:'source_url'},
           {name:'operate_user'},
           {name:'operate_ip'},
           {name:'operate_time'}
        ]  
    });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var source_name=Ext.getCmp('source_name').getValue();
		 var operate_type=Ext.getCmp('operate_type').getValue();
		 var operate_user=Ext.getCmp('operate_user').getValue();
		 var stime=Ext.getCmp('stime').getValue();
		 var etime=Ext.getCmp('etime').getValue();
         var new_params = { source_name:source_name,operate_type : operate_type,operate_user:operate_user,stime:stime,etime:etime};    
         Ext.apply(store.proxy.extraParams, new_params);    
    });  
    
    var sm = Ext.create('Ext.selection.CheckboxModel');
    var grid = Ext.create("Ext.grid.Panel",{
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
        dockedItems: [
    {
	xtype:'toolbar',
	dock:'top',
	displayInfo: true,
	items:[
	       <cms:havePerm url='/systemManager/deleteSystemLog'>
           { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
	    	   listeners: {
	    		   click:function(){
	    			   deleteRecord();
	    		   }
	    	   }}
           </cms:havePerm>
          ],
      },
      {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'source_name',name: 'source_name',fieldLabel: '日志名称',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'textfield',id:'operate_user',name: 'operate_user',fieldLabel: '操作用户',labelAlign:'left',labelWidth:60},'-',
       	          '日志类型:',
     	          {
					xtype:'combo',
					id:'operate_type',
					store : typeComboStore,
		    		width:100,
		    		name:'operate_type',
		   			triggerAction: 'all',
		   			displayField: 'typeName',
		   			valueField: 'operate_type',
		   			value:'',
					mode:'local'
				   },'-', 
				   {
						xtype : 'datetimefield',
						id : 'stime',
						width : 200,
						name : 'stime',
						format : 'Y-m-d',
						value:'${start_time}',
						fieldLabel : '开始时间',
						labelAlign : 'left',
						labelWidth : 60
					},
					{
						xtype : 'datetimefield',
						id : 'etime',
						width : 200,
						name : 'etime',
						format : 'Y-m-d',
						fieldLabel : '结束时间',
						labelAlign : 'left',
						labelWidth : 60
					},
        	       <cms:havePerm url='/systemManager/systemLogList'>
        	       { xtype: 'button', id:'select',text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var source_name=Ext.getCmp('source_name').getValue();
    	    			   var operate_type=Ext.getCmp('operate_type').getValue();
    	    			   var operate_user=Ext.getCmp('operate_user').getValue();
    	    			   var stime=Ext.getCmp('stime').getValue();
    	    			   var etime=Ext.getCmp('etime').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,source_name:source_name,operate_type :operate_type,operate_user:operate_user,stime:stime,etime:etime}}); 
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

    
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    // 双击grid记录，编辑用户信息
   // grid.on("itemdblclick",function(grid, row){
   // 	showform(row.data);
   // });
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	
	//删除操作日志
	function deleteRecord()  
    {  
         //grid中复选框被选中的项  
         var records = grid.getSelectionModel().getSelection();  
       	 if(records.length <= 0){
        	 Ext.example.msg("提示","请选择要删除的对象！");
             return ;  
    	 }
         //ids：所有选中的用户Id的集合使用','隔开，初始化为空  
         var ids = '';  
         for(var i = 0;i<records.length;i++)  
         {  
            if(i>0){  
            	ids = ids+','+records[i].get('id');  
            }else{  
            	ids = ids+records[i].get('id');  
            }  
         }  
         Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/systemManager/deleteSystemLog",  
                     params:{ids:ids},  
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
    } ;
    
});



</script>
</head>
<body>
</body>
</html>