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
<title>服务商标签管理</title>
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
                   {header:'服务商名称',dataIndex:'merchant_name',align:'center'},
                   {header:'联系方式',dataIndex:'telephone',align:'center'},
                   {header:'商户地址',dataIndex:'location_address',align:'center'},
                   {header:'标签',dataIndex:'name',align:'center'},
                   {header:'标签价格',dataIndex:'price',align:'center',hidden:true},
                   {header:'审核状态',dataIndex:'is_audit',align:'center',renderer:function(value){  
                       if(value=='1'){  
                           return "<span style='color:green;font-weight:bold';>已通过</span>";  
                       }else if(value=='2'){
                    	   return "<span style='color:red;font-weight:bold';>未通过</span>";  
                       }else{ 
                           return "<span style='color:blue;font-weight:bold';>待审核</span>";  
                       }
           		   }},
                   {header:'创建时间',dataIndex:'join_time',align:'center'},
                   {header:'备注',dataIndex:'demand',align:'center'}
               ];
	//创建store数据源
    
    //列表展示数据源
    var store = Ext.create("Ext.data.Store",{
    	pageSize:20, //每页显示几条数据  
    	remoteSort: true,
        proxy:{  
            type:'ajax',  
            url:'/serviceTag/merchantServiceTagList',  
            reader:{  
                type:'json',  
                totalProperty:'total',  
                root:'data',  
                idProperty:'ext'  
            }  
        },
        fields:[  
           {name:'id'},
           {name:'telephone'}, 
           {name:'name'}, 
           {name:'location_address'},  
           {name:'merchant_name'},  
           {name:'price'},
           {name:'is_audit'},
           {name:'join_time'},
           {name:'demand'}
        ]  
    });

    // 审核状态
    var auditComboStore = new Ext.data.SimpleStore({
    	fields:['name','value'],
    	data:[
    	      ['全部',''],
    	      ['待审核','0'],
    	      ['已通过','1'],
    	      ['未通过','2']
    	]
    });

 // app类型
	var defineStore = Ext.create("Ext.data.Store", {
		pageSize : 20, // 每页显示几条数据
		proxy : {
			type : 'ajax',
			url : '/common/getGxfwCatalog',
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
			name : 'name'
		} ]
	});
    
   
    //点击下一页时传递搜索框值到后台  
    store.on('beforeload', function (store, options) {    
		var name = Ext.getCmp('name').getValue();
		var telephone=Ext.getCmp('telephone').getValue();
		var is_audit=Ext.getCmp('is_audit').getValue();
		var start_time=Ext.getCmp('start_time').getValue();
		var off_time=Ext.getCmp('off_time').getValue();
        var new_params = {name:name,telephone:telephone,is_audit:is_audit,start_time:start_time,off_time:off_time};    
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
        	       <cms:havePerm url='/serviceTag/deleteMerchantServiceTag'> 
                   { xtype: 'button', id:'del', text: '删除',iconCls:'NewDelete',
        	    	   listeners: {
        	    		   click:function(){
        	    			   delRecordAll();
        	    		   }
        	      }},'-',
        	      </cms:havePerm> 
       	       <cms:havePerm url='/serviceTag/auditMerchantServiceTag'>
    	       { xtype: 'button',id:'audit_1', text: '审核通过',iconCls:'shenhetongguo',
    	    	   listeners: {
    	    		   click:function(){
    	    			   auditRepeat(1);
    	    		   }
    	    	   }
    	       },'-',
    	       </cms:havePerm> 
    	       <cms:havePerm url='/serviceTag/auditMerchantServiceTag'>
    	       { xtype: 'button',id:'audit_2', text: '审核不通过',iconCls:'shenhebutongguo',
    	    	   listeners: {
    	    		   click:function(){
    	    			   auditRepeat(2);
    	    		   }
    	    	   }
    	       },'-',
    	       </cms:havePerm> 
    	       <cms:havePerm url='/serviceTag/exportExcel'>
    	       { xtype: 'button',id:'export', text: '导出',iconCls:'Daochu',
        	    	   listeners: {
        	    		   click:function(){
        	    			   exportAll();
        	    		   }
        	    	   }
               },'-',</cms:havePerm>
        	      ],
        },{
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'textfield',id: 'name',name: 'name',fieldLabel: '标签',labelAlign:'left',labelWidth:60},'-',
        	       { xtype: 'textfield',id: 'telephone',name: 'telephone',fieldLabel: '联系方式',labelAlign:'left',labelWidth:60},'-',
        	       {
   			     	xtype:'combo',
   					fieldLabel : '审核状态',
   					id: 'is_audit',
   				    store : auditComboStore,
   	    		    name:'is_audit',
   	   			    triggerAction: 'all',
   	   			    displayField: 'name',
   	   			    valueField: 'value',
   	   			    value:'',
   	   			    labelWidth:60,
   				    mode:'local'
   			        },'-',
        	       
        	       ]},
				   {
        	xtype:'toolbar',
        	dock:'top',
        	displayInfo: true,
        	items:[{ xtype: 'datetimefield',id: 'start_time',name: 'start_time',fieldLabel: '创建时间',format : 'Y-m-d',labelAlign:'left',labelWidth:65},'～',
        	       { xtype: 'datetimefield',id: 'off_time',name: 'off_time',format : 'Y-m-d',labelAlign:'left',labelWidth:65},'-',
  				 
				   <cms:havePerm url='/serviceTag/merchantServiceTagList'> 
				   { xtype: 'button', text: '查询',id:'select',iconCls:'Select',listeners: {
    	    		   click:function(){
    	    			   var name=Ext.getCmp('name').getValue();
    	    			   var is_audit=Ext.getCmp('is_audit').getValue();
    	    			   var telephone=Ext.getCmp('telephone').getValue();
    	    			   var start_time=Ext.getCmp('start_time').getValue();
    	    			   var off_time=Ext.getCmp('off_time').getValue();
    	    			   store.currentPage = 1;
    	    			   store.load({params:{start:0,page:1,limit:20,name:name,telephone:telephone,is_audit:is_audit,start_time:start_time,off_time:off_time}}); 
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
    defineStore.load(); 
    grid.on("itemcontextmenu",function(view,record,item,index,e){  
        e.preventDefault();  
        contextmenu.showAt(e.getXY());  
    });
    grid.on("itemdblclick",function(grid, row){

    });

    
	// 整体架构容器
	Ext.create("Ext.container.Viewport", {
		layout : 'border',
		autoHeight: true,
		border: false,
		items : [grid]
	});
	
	
	
	function auditRepeat(status){
		 //grid中复选框被选中的项  
        var rows = grid.getSelectionModel().getSelection(); 
        if(rows.length <= 0){
          	 Ext.example.msg("提示","请选择要审核的对象！");
            return ;  
   	    }else if(rows.length>1){
   		 Ext.example.msg("提示","请选择一个进行审核！");
            return ; 
   	     }
        if(rows[0].get('is_audit')=='1'){
        	Ext.example.msg("提示","已通过，无法进行审核！");
            return ; 
        }
        if(rows[0].get('is_audit')=='2'){
        	Ext.example.msg("提示","未通过，无法进行审核！");
            return ; 
        }
        //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
        var id = '';  
        for(var i = 0;i<rows.length;i++)  
        {  
           if(i>0)  
           {  
               id = id+','+rows[i].get('id');  
           }else{  
               id = id+rows[i].get('id');  
           }  
        }
        var repeat=1;
        	Ext.Ajax.request({  
                url:"/serviceTag/getTagRepeat",  
                params:{ids:id},  
                method:'post',  
                success:function(fp,o){  
                	var json = Ext.JSON.decode(fp.responseText);
                	repeat=json.total;
                	id=json.id;
                	auditRecord(status,id,repeat);
                },  
                failure:function(form,action){ 
                	auditRecord(status,id,repeat);
                }  
            });
        
	}
	
	//审核
	 function auditRecord(status,id,repeat)  
    { 
		var confim="请确定要执行审核操作吗?";
		if(repeat>1){
			confim="有"+repeat+"个相同的标签，请确认全部审核";
        }
        Ext.Msg.confirm("提示信息",confim,function (btn){  
                 if(btn == 'yes')  
                 {  
                	 if(status==1){
                	 definprompt(status,id);
                 }else{
                	 definprompt1(status,id); 	  
                 }
                 }   
        	 });
         
    } ;
    //-------------选择分类start----------------------------
  //自定义弹出框
	   var definprompt=function(status,id){
	 	var formPanel = new Ext.form.FormPanel({
	 		   autoWidth:true,
	 		   layout:"form",
	 		   frame:true,
	 		   labelWidth:75,
	 		   labelAlign:"right",
	 		   items:[{
	 		    xtype:"label",
	 		    height  : 20,
	 		    text :"请选择标签所属分类:"
	 		   },{
	   				xtype : 'combobox',
					name : 'txt',		
					valueField : 'id',
					displayField : 'name',
					store : defineStore,
					allowBlank	:false,
					editable:false,
					queryMode : 'local',
					forceSelection:false
				}],  
	 		    buttons : [{
	 		    text : '确定',
	 		    handler : function(){
	 		    
                 var txt=formPanel.form.findField('txt').getValue();
                 
	             if(txt==''||txt==null){
	              	Ext.example.msg("提示","请选择标签所属分类！");
	              	return;
	              }
	             Ext.Ajax.request({  
                     url:"/serviceTag/auditMerchantServiceTag",  
                     params:{ids:id,status:status,calogId:txt,demand:''},  
                     method:'post',  
                     success:function(o){  
                     	if(o.responseText=='true'){
                     		  Ext.example.msg("提示","审核成功！");
                     		  formPanel.close();
                     		  win.close();
                              store.reload();  
                              return ; 
                     	}else{
                     		formPanel.close();
                     		win.close();
                     		Ext.example.msg("提示","服务已存在或状态改变,审核失败！");
                     	}
                    	
                     },  
                     failure:function(form,action){ 
                    	 formPanel.close();
                    	 win.close();
                    	 Ext.example.msg("提示","审核失败！");
                     }  
                 });
                 	  
	         
	 		    }
	 		   }, {
	 		    text : '取消',
	 		    handler : function(){
	 		    formPanel.close();
	 		     win.close();
	 		    }
	 		   }]
	 		  });
	 		  var win = new Ext.Window({
	 		   title:"选择分类",
	 		   modal:true,
	 		   width:250,
	 		   height:150,
	 		   collapsible:false,
	 		   resizable:false,
	 		   closeAction:'hide',
	 		   items:[formPanel]
	 		  });
	 		   win.on("close",function(){
	 			  formPanel.close();

	 		     });
	 		    win.show();
	           }
    
	 //-------------选择分类end---------------------------- 
	 
	 var refuseStore = new Ext.data.SimpleStore({
 	fields:['type','name'],
 	data:[['',''],
 	      ['1','无法准确定义您所能提供的服务类型，建议您换个词语试试。'],
 	      ['2','不符合国家规定或者O盟服务规范，建议您换个词语试试。'],
 	      ['3','不属于个性服务范围，建议您以企业店主的身份，通过“***（行业）-***（服务类型）”（自定义）入驻平台。'],
 	      ['4','与个性服务中现有服务类型相似或包含，建议您以“***行业-***服务标签”（自定义）标签入驻平台。'],
 	      ['5','与个性服务中现有服务类型相似或包含，建议您以“行业1-标签1”或“行业2-标签2”入驻平台。'],
 	      ['6','与个性服务中现有服务类型相似或包含，建议您以“行业1-***标签1”和“行业2-标签2”入驻平台。']
 	]
 });
	   //-------------选择不通过原因start----------------------------
	   //自定义弹出框
	 	   var definprompt1=function(status,id){
	 	 	var formPanel = new Ext.form.FormPanel({
	 	 		   layout:"form",
	 	 		   labelWidth:75,
	 	 		   labelAlign:"center",
	 	 		   items:[{
	 	 		    xtype:"label",
	 	 		    height  : 20,
	 	 		    text :"请选择不通过原因:"
	 	 		   },
	 	 		 {
	 	 		        xtype: 'radiogroup',
	 	 		        fieldLabel: '',
	 	 		        // Arrange radio buttons into two columns, distributed vertically
	 	 		        columns: 1,
	 	 		        vertical: true,
	 	 		        items: [
	 	 		            { boxLabel: '无法准确定义您所能提供的服务类型，建议您换个词语试试。', name: 'job', inputValue: '无法准确定义您所能提供的服务类型，建议您换个词语试试。' },
	 	 			        { boxLabel: '不符合国家规定或者O盟服务规范，建议您换个词语试试。', name: 'job', inputValue: '不符合国家规定或者O盟服务规范，建议您换个词语试试。' },
	 	 			        { boxLabel: '不属于个性服务范围，建议您以企业店主的身份，通过“***（行业）-***（服务类型）”（自定义）入驻平台。', name: 'job', inputValue: '不属于个性服务范围，建议您以企业店主的身份，通过“***（行业）-***（服务类型）”（自定义）入驻平台。' },
	 	 			        { boxLabel: '与个性服务中现有服务类型相似或包含，建议您以“***行业-***服务标签”（自定义）标签入驻平台。', name: 'job', inputValue: '与个性服务中现有服务类型相似或包含，建议您以“***行业-***服务标签”（自定义）标签入驻平台。' },
	 	 			        { boxLabel: '与个性服务中现有服务类型相似或包含，建议您以“行业1-标签1”或“行业2-标签2”入驻平台。', name: 'job', inputValue: '与个性服务中现有服务类型相似或包含，建议您以“行业1-标签1”或“行业2-标签2”入驻平台。' },
	 	 			        { boxLabel: '与个性服务中现有服务类型相似或包含，建议您以“行业1-***标签1”和“行业2-标签2”入驻平台。', name: 'job', inputValue: '与个性服务中现有服务类型相似或包含，建议您以“行业1-***标签1”和“行业2-标签2”入驻平台。' },
	 	 			        { boxLabel: '自定义', name: 'job', inputValue: '您可以选择上述模板后进行人纷纷调整，也可以可以直接在此输入您的意见。' }
	 	 		        ] , 
	 	 		        listeners:{
	 	 		            //通过change触发
	 	 		            change: function(g , newValue , oldValue){
	 	 		            	formPanel.form.findField('txt').setValue(newValue.job);  
	 	 		            	
	 	 		            }
	 	 		        }
	 	 		    },
	 	 		   {
	 	 			    xtype : 'textarea',
	 					name : 'txt',	
	 					value:'',
	 					queryMode : 'local',
	 				}],  
	 	 		    buttons : [{
	 	 		    text : '确定',
	 	 		    handler : function(){
	 	 		    
	                  var txt=formPanel.form.findField('txt').getValue();
	                  
	 	             if(txt==''||txt==null){
	 	              	Ext.example.msg("提示","请选择不通过原因！");
	 	              	return;
	 	              }
	 	             
	 	             var t = txt.indexOf('*');
	 	             if(t>0){
	 	              	Ext.example.msg("提示","请输入正确的原因！");
	 	              	return;
	 	              }
	 	             Ext.Ajax.request({  
	                      url:"/serviceTag/auditMerchantServiceTag",  
	                      params:{ids:id,status:status,calogId:'',demand:txt},  
	                      method:'post',  
	                      success:function(o){  
	                      	if(o.responseText=='true'){
	                      		  Ext.example.msg("提示","审核成功！");
	                      		  formPanel.close();
	                      		  win.close();
	                              store.reload();  
	                               return ; 
	                      	}else{
	                      		formPanel.close();
	                      		win.close();
	                      		Ext.example.msg("提示","审核失败！");
	                      	}
	                     	
	                      },  
	                      failure:function(form,action){ 
	                     	 formPanel.close();
	                     	 win.close();
	                     	 Ext.example.msg("提示","审核失败！");
	                      }  
	                  });
	                  	  
	 	         
	 	 		    }
	 	 		   }, {
	 	 		    text : '取消',
	 	 		    handler : function(){
	 	 		    formPanel.close();
	 	 		     win.close();
	 	 		    }
	 	 		   }]
	 	 		  });
	 	 		 
	 	 	 var win = Ext.create('Ext.window.Window',{  
		              title : "选择原因",  
		              width:750,
		 	 		   height:400,
		              //plain : true,  
		              iconCls : "addicon",  
		              // 不可以随意改变大小  
		              resizable : false, 
		              autoScroll:true, 
		              // 是否可以拖动  
		              // draggable:false,  
		              collapsible : true, // 允许缩放条  
		              closeAction : 'close',  
		              closable : true,  
		              // 弹出模态窗体  
		              modal : 'true',  
		              buttonAlign : "center",  
		              bodyStyle : "padding:0 0 0 0",  
		              items : [formPanel]
		           }); 
	 	 	
	 	 		   win.on("close",function(){
	 	 			  formPanel.close();

	 	 		     });
	 	 		    win.show();
	 	           }
	     
	 	 //-------------选择不通过原因end---------------------------- 
	 
	 
	 
	 
	 
    
	//删除
	 function delRecordAll()  
     {  
          //grid中复选框被选中的项  
          var rows = grid.getSelectionModel().getSelection();  
          //user_id：所有选中的用户Id的集合使用','隔开，初始化为空    
          var id = '';  
          for(var i = 0;i<rows.length;i++)  
          {  
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
                         url:"/serviceTag/deleteMerchantServiceTag",  
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

     function sleepTime(){
    	 Ext.getCmp('export').setDisabled(true);
    	 setTimeout(function(){
    		Ext.getCmp('export').setDisabled(false);
    	}, 30000);//js定时器
      } 
   //导出所有商户
		function exportAll() {
			 var name=Ext.getCmp('name').getValue();
			 var is_audit=Ext.getCmp('is_audit').getValue();
			 var telephone=Ext.getCmp('telephone').getValue();
			 var start_time=Ext.getCmp('start_time').getValue();
			 var off_time=Ext.getCmp('off_time').getValue();
             window.location.href = '/serviceTag/exportExcel?name='+name+'&is_audit='+is_audit+'&telephone='+telephone
             +'&start_time='+start_time+'&off_time='+off_time;
             
             Ext.example.msg("提示","正在导出报表，请稍后！");
             sleepTime();
		};

 	 
});



</script>

</head>
<body>
</body>
</html>