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
<title>黑名单管理</title>
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
                   {header:'序号',xtype: 'rownumberer',width:50,align:'center'},
                   {header:'编号',dataIndex:'id',hidden:true,align:'center'},
                   {header:'手机号',dataIndex:'phone',align:'center'},
                   {header:'加入时间',dataIndex:'join_time',align:'center'},
                   {header:'操作人',dataIndex:'operat_name',align:'center'}
               ];
	
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/user/getBlackUser',
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'},
           {name:'phone'},   
           {name:'operat_name'}, 
           {name:'join_time'}
        ]  
    });
  //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
    	 var phone=Ext.getCmp('phone').getValue();
    	 var start_time=Ext.getCmp('start_time').getValue();
		 var off_time=Ext.getCmp('off_time').getValue();
         var new_params = {phone:phone,start_time : start_time,off_time : off_time};    
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
        dockedItems: [{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[
        	       '-',
        	       <cms:havePerm url='/user/deletetBlack'>
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delUserAll();
        	    		   }
        	    	   }}</cms:havePerm> 
                 ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'phone',name: 'phone',fieldLabel: '手机号',labelAlign:'left',labelWidth:45},'-',
        	       { xtype: 'datetimefield',id: 'start_time',name: 'start_time',format : 'Y-m-d',fieldLabel: '开始时间',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'datetimefield',id: 'off_time',name: 'off_time',format : 'Y-m-d',fieldLabel: '结束时间',labelAlign:'left',labelWidth:60},'-',
        	       <cms:havePerm url='/user/getBlackUser'>
        	       { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var phone=Ext.getCmp('phone').getValue();
    	    			   var start_time=Ext.getCmp('start_time').getValue();
    	    			   var off_time=Ext.getCmp('off_time').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,start:0,phone:phone,start_time : start_time,off_time : off_time}}); 
    	    		   }
    	    		   }}</cms:havePerm>
                   ]
        }],
        bbar : Ext.create("Ext.ux.CustomSizePagingToolbar", {// 在表格底部 配置分页
            displayInfo : true,
            store : store
        })
    });
  //加载数据  
    store.load({params:{start:0,limit:20}}); 
    // 表格配置结束
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    
    grid.on("itemdblclick",function(grid, row){
    	//Ext.Msg.alert("系统提示","你双击啦！ID为："+row.data.id);  
    });
    
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	//黑名单删除
	 function delUserAll()  
    {  
         //grid中复选框被选中的项  
           
         var rows = grid.getSelectionModel().getSelection();  
         //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
         var user_id = '';  
         for(var i = 0;i<rows.length;i++)  
         {  
            if(i>0)  
            {  
                user_id = user_id+','+rows[i].get('id');  
            }else{  
                user_id = user_id+rows[i].get('id');  
            }  
         }  
         //没有选择要执行操作的对象  
           
         if(user_id == "")  
         {  
        	Ext.example.msg("提示","请选择要删除的对象！");
            return ;  
         }else{  
            Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function (btn){  
                if(btn == 'yes')  
                {  
                    Ext.Ajax.request({  
                        url:"/user/deletetBlackUser",  
                        params:{id:user_id},  
                        method:'post',  
                        success:function(o){  
                        	Ext.example.msg("提示","黑名单删除成功！");
                            store.reload();  
                            return ;  
                        },  
                        failure:function(form,action){  
                        	Ext.example.msg("提示","黑名单删除失败！");
                        }  
                    });    
                }  
            });  
         }  
    } ;
	    
});
</script>

</head>
<body>
</body>
</html>