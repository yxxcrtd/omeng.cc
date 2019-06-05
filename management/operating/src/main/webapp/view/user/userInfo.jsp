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
<title>用户管理</title>
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
                   {header:'编号',dataIndex:'id',type: 'string',hidden:true,align:'center'},
                   {header:'手机号',dataIndex:'phone',align:'center'},
                   {header:'会员名',dataIndex:'name',align:'center'},
                   {header:'性别',dataIndex:'sex',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:green;font-weight:bold';>保密</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>男</span>";  
                       }else if(value=='2'){
                    	   return "<span style='font-weight:bold';>女</span>";  
                       }
           		   }},
           		 {header:'省',dataIndex:'province',align:'center'},
                 {header:'市',dataIndex:'city',align:'center'},
                 {header:'注册时间',dataIndex:'join_time',align:'center'},
                 {header:'最后活跃时间',dataIndex:'last_login_time',align:'center'},
                 {header:'用户类型',dataIndex:'user_type',align:'center',renderer:function(value){  
                       if(value=='0'){  
                           return "<span style='color:green;font-weight:bold';>普通用户</span>";  
                       } else if(value=='1'){  
                           return "<span style='color:red;font-weight:bold';>商铺用户</span>";  
                       }
           		  }},
           		{header:'用户状态',dataIndex:'blackCount',align:'center',renderer:function(value){  
                    if(value==0){  
                        return "<span style='color:green;font-weight:bold';>正常用户</span>";  
                    } else if(value>0){  
                        return "<span style='color:red;font-weight:bold';>黑名单</span>";  
                    }
        		  }},
                  {header:'操作',dataIndex:'detail',align:'center',renderer:function(value,v,r){  
                	   var name='';
                	   if(r.data.name==''||r.data.name==null){
                		   name='**'; 
                	   }else{
                	    name =r.data.name.replace('"', '').replace("'", "");
                	   }
                	     return '<a href="javascript:showDetail(\''+r.data.id+'\',\''+r.data.phone+'\',\''+name+'\')">详情</a>';
             		}}
               ];
	
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/user/showUser',
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'#'  
            }  
        },  
        fields:[  
           {name:'id'}, //mapping:0 这样的可以指定列显示的位置，0代表第1列，可以随意设置列显示的位置  
           {name:'name'},
           {name:'phone'},
           {name:'sex'},
           {name:'join_time'},
           {name:'user_type'},
           {name:'province'},  
           {name:'city'},
           {name:'last_login_time'},
           {name:'blackCount'}
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
    	var phone=Ext.getCmp('phone').getValue();
		var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
		var login_start_time=Ext.getCmp('login_start_time').getValue();
		var login_off_time=Ext.getCmp('login_off_time').getValue();
		var province = Ext.getCmp('province').getRawValue();
		var city = Ext.getCmp('city').getRawValue();
        var new_params = {phone:phone,province:province,city:city,start_time:start_time,off_time:off_time,login_start_time:login_start_time,login_off_time:login_off_time};    
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
        	       <cms:havePerm url='/user/editUser'>
                   { xtype: 'button', id:'edit', text: '编辑',iconCls:'Edit',
        	    	   listeners: {
        	    		   click:function(){
        	    			   editform();
        	    		   }
        	    	   }},'-',</cms:havePerm>
        	    	    <cms:havePerm url='/user/addBlackUser'>
                   { xtype: 'button', id:'addBalck', text: '加入黑名单',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   addBlackUser();
        	    		   }
        	    	   }},'-',</cms:havePerm> 
        	    	   <cms:havePerm url='/user/deleteBlackUserByuserId'>
                       { xtype: 'button', id:'delBalck', text: '删除黑名单',iconCls:'NewDelete',
            	    	   listeners: {
            	    		   click:function(){
            	    			   deleteBlackUser();
            	    		   }
            	    	   }},'-',</cms:havePerm> 
        	    	   <cms:havePerm url='/user/exportExcel'>
	        	       { xtype: 'button',id:'export', text: '导出',iconCls:'Daochu',
	            	    	   listeners: {
	            	    		   click:function(){
	            	    			   exportAll();
	            	    		   }
	            	    	   }
	                   }</cms:havePerm>
                 ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id:'phone',name: 'phone',fieldLabel: '手机号',labelAlign:'left',value:'${phone}',labelWidth:55},'-',
        	       {xtype : 'combobox',id : 'province',name : 'province',fieldLabel : '省',valueField : 'id',hiddenName:'id',
				         labelWidth:40,displayField : 'area',store : provinceStore,
				         listeners : { // 监听该下拉列表的选择事件
					        select : function(combobox,record,index) {
					         	Ext.getCmp('city').setValue('');
						        cityStore.load({
									params : {
										parentId : combobox.value
									}
								});
					}
				},queryMode : 'local',labelAlign : 'left' },'-',
			     {xtype : 'combobox',name : 'city',id : 'city',labelWidth:40,fieldLabel : '市',valueField : 'id',
			   			hiddenName:'id',displayField : 'area',store : cityStore,queryMode : 'local',labelAlign : 'left'},'-',
        	       
        	       ]
		    },	{
			xtype : 'toolbar',
			dock : 'top',
			displayInfo : true,
			items : [  { xtype: 'datetimefield',id: 'start_time',name: 'start_time',fieldLabel: '注册时间',value:'${start_time}',format : 'Y-m-d',labelAlign:'left',labelWidth:65},'～',
	        	       { xtype: 'datetimefield',id: 'off_time',name: 'off_time',format : 'Y-m-d',labelAlign:'left',labelWidth:65},'-',
	                   
                     { xtype: 'datetimefield',id: 'login_start_time',name: 'login_start_time',fieldLabel: '最近活跃时间',format : 'Y-m-d',labelAlign:'left',labelWidth:85},'～',
                     { xtype: 'datetimefield',id: 'login_off_time',name: 'login_off_time',format : 'Y-m-d',labelAlign:'left',labelWidth:85},'-',

        	       <cms:havePerm url='/user/showUser'>
                   { xtype: 'button',id:'select', text: '查询',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var phone=Ext.getCmp('phone').getValue();
    	    			   var start_time=Ext.getCmp('start_time').getValue();
		                   var off_time=Ext.getCmp('off_time').getValue();
		                   var login_start_time=Ext.getCmp('login_start_time').getValue();
		           		   var login_off_time=Ext.getCmp('login_off_time').getValue();
		           		   var province = Ext.getCmp('province').getRawValue();
		        		   var city = Ext.getCmp('city').getRawValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,province:province,city:city,phone:phone,start_time:start_time,off_time:off_time,login_start_time:login_start_time,login_off_time:login_off_time}}); 
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
    provinceStore.load(); 
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    <cms:havePerm url='/user/editUser'>
    grid.on("itemdblclick",function(grid, row){
    	 editform(); 
    });</cms:havePerm>
    
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	function sleepTime(){
   	 Ext.getCmp('export').setDisabled(true);
   	 setTimeout(function(){
   		Ext.getCmp('export').setDisabled(false);
   	}, 30000);//js定时器
     } 
	
	//加入黑名单
	 function addBlackUser()  
   {  
        var rows = grid.getSelectionModel().getSelection();  
        var user_id = '';  
        for(var i = 0;i<rows.length;i++)  
        { 
        	if(rows[i].get('blackCount')>0){
        		 Ext.example.msg("提示","已是黑名单！"); 
        		 return;
        	}
        	
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
       	Ext.example.msg("提示","请选择要添加的用户！");
           return ;  
        }else{  
           Ext.Msg.confirm("提示信息","请确定要执行添加操作吗?",function(btn){  
               if(btn == 'yes')  
               {  
                   Ext.Ajax.request({  
                       url:"/user/addBlackUser",  
                       params:{id:user_id},  
                       method:'post',  
                       success:function(o){  
                       	Ext.example.msg("提示","黑名单添加成功！");
                           store.reload();  
                           return ;  
                       },  
                       failure:function(form,action){  
                       	Ext.example.msg("提示","黑名单添加失败！");
                       }  
                   });    
               }  
           });  
        }  
   } ;
	
 //删除黑名单
	 function deleteBlackUser()  
     {  
      var rows = grid.getSelectionModel().getSelection();  
      var user_id = '';  
      for(var i = 0;i<rows.length;i++)  
      {  
    	  if(rows[i].get('blackCount')==0){
     		 Ext.example.msg("提示","请选择黑名单用户！"); 
     		 return;
     	}
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
     	Ext.example.msg("提示","请选择要删除的黑名单用户！");
         return ;  
      }else{  
         Ext.Msg.confirm("提示信息","请确定要执行删除操作吗?",function(btn){  
             if(btn == 'yes')  
             {  
                 Ext.Ajax.request({  
                     url:"/user/deleteBlackUserByuserId",  
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
	
  //导出所有会员
	function exportAll() {
		 var phone=Ext.getCmp('phone').getValue();
		 var start_time=Ext.getCmp('start_time').getValue();
         var off_time=Ext.getCmp('off_time').getValue();
         var login_start_time=Ext.getCmp('login_start_time').getValue();
 		 var login_off_time=Ext.getCmp('login_off_time').getValue();
 		 var province = Ext.getCmp('province').getRawValue();
		 var city = Ext.getCmp('city').getRawValue();
		window.location.href = '/user/exportExcel?phone='+phone+'&province='+province+'&city='+city+'&start_time='+start_time+'&off_time='+off_time
		                      +'&login_start_time='+login_start_time+'&login_off_time='+login_off_time;
		
		Ext.example.msg("提示","正在导出报表，请稍后！");
		sleepTime();
  };
  //form表单编辑用户
	var editform=function(){  
	    var edit_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 450,
	                height:470,
	                bodyPadding:5,  
	                //renderTo:"panel21",  
	                fieldDefaults: {  
	                    labelAlign: 'left',  
	                    labelWidth: 90,  
	                    anchor: '100%'  
	                },  
	                items: [{  
	                    //显示文本框，相当于label  
	                    xtype: 'displayfield',   
	                    name: 'displayfield1',  
//	                    fieldLabel: 'Display field',  
	                    value: '修改会员相关信息'  
	                     
	                },
	                {  
	                    //输入姓名  
	                    xtype: 'textfield', 
	                    name: 'id',  
	                    readOnly:true,
	                    fieldLabel: '会员ID',
	                    hidden:true 
	                },{  
	                    //输入姓名  
	                    xtype: 'textfield', 
	                    name: 'name',  
	                    fieldLabel: '会员名称'  
	                },{  
	                    //下拉列表框  
	                    xtype: 'combobox', //9  
	                    fieldLabel: '性别',
	                    displayField: 'name',
	                    valueField : 'sex',
	                    name:'sex',
	                    store: Ext.create('Ext.data.Store', {  
	                        fields: [  
	                          {name:'sex'},{ name: 'name' }  
	                          ],  
	                                          data: [  
	                          { 'sex':'0','name': '保密' },  
	                          { 'sex':'1','name': '男' } ,  
	                          { 'sex':'2','name': '女' }  
	                          ]  
	                    })
	                   
	                }, {  
	                    //输入会员名
	                    xtype: 'textfield',  
	                    name: 'phone', 
	                    readOnly:true,
	                    fieldLabel: '联系方式'
	                }]  
	            });  
	    //创建window面板，表单面板是依托window面板显示的  
	    
	    var rows = grid.getSelectionModel().getSelection();  
        //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
	    if(rows.length == 0)  
        {  
           Ext.example.msg("提示","请选择要编辑的对象！");
           return ;  
        }
	    if(rows.length > 1)  
	       {  
	          Ext.example.msg("提示","只能选择一个编辑的对象！");
	          return ;  
	       }
	    edit_winForm.form.findField('id').setValue(rows[0].get('id')); 
        edit_winForm.form.findField('name').setValue(rows[0].get('name'));  
        edit_winForm.form.findField('sex').setValue(rows[0].raw.sex+''); 
        edit_winForm.form.findField('phone').setValue(rows[0].raw.phone);  
	    var editwindow = Ext.create('Ext.window.Window',{  
	              title : "编辑会员",  
	              width: 450,
	              height:350,  
	              //height : 120,  
	              //plain : true,  
	              iconCls : "addicon",  
	              // 不可以随意改变大小  
	              resizable : true,  
	              // 是否可以拖动  
	              draggable:true,  
	              collapsible : true, // 允许缩放条  
	              closeAction : 'close',  
	              closable : true,  
	              // 弹出模态窗体  
	              modal : 'true',  
	              buttonAlign : "center",  
	              bodyStyle : "padding:0 0 0 0",  
	              items : [edit_winForm],  
	              buttons : [{  
	                         text : "保存",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            if (edit_winForm.getForm().isValid()) {  
	                            	edit_winForm.getForm().submit({  
	                                          url :'/user/editUser',  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
	                                            
	                                          success: function(fp, o) {  
	                                              if (o.result== true) {  
	                                            	    Ext.example.msg("提示","修改成功！");
	                                                    editwindow.close(); //关闭窗口  
	                                                    store.reload();
	                                                    grid.getSelectionModel().clearSelections();
	                                              }else {  
	                                             	    Ext.example.msg("提示","修改失败！");
	                                              }  
	                                          },  
	                                          failure: function() {  
	                                         	    Ext.example.msg("提示","修改失败！");  
	                                          }  
	                                       });  
	                            }  
	                         }  
	                     }, {  
	                         text : "关闭",  
	                         minWidth : 70,  
	                         handler : function() {  
	                        	 editwindow.close();  
	                         }  
	                     }]  
	           });  
	    editwindow.show();  
	    };
	    
});

function showDetail(id,phone,name){
	if(name=='null'){
		name=phone;
	}
    parent.addTab("user_detail_"+id,"会员【"+name+"】详情","Huiyuanxinxiguanli","/user/getUserDetail?userId="+id); 	 
}
</script>
</head>
<body>
</body>
</html>