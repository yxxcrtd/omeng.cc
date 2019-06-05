<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/cms.tld" prefix="cms" %>
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
<title>O盟后台管理</title>
<link href="http://pmg.oomeng.cn/manager/ExtJS4.2/resources/css/ext-all-neptune-rtl.css" rel="stylesheet">
<link href="http://pmg.oomeng.cn/manager/ExtJS4.2/css/icon.css" rel="stylesheet">
<link href="http://pmg.oomeng.cn/manager/css/msgTip.css" rel="stylesheet">
<link href="http://pmg.oomeng.cn/manager/css/icon.css" rel="stylesheet">
<script src="http://pmg.oomeng.cn/manager/ExtJS4.2/ext-all.js"></script>
<script src="http://pmg.oomeng.cn/manager/ExtJS4.2/ux/data/PagingMemoryProxy.js"></script>
<script src="http://pmg.oomeng.cn/manager/ExtJS4.2/ux/ProgressBarPager.js"></script>
<script src="http://pmg.oomeng.cn/manager/ExtJS4.2/locale/ext-lang-zh_CN.js"></script>
<script type="text/javascript" src="http://pmg.oomeng.cn/manager/js/msgTip.js"></script>  
<script type="text/javascript" src="http://pmg.oomeng.cn/manager/js/localXHR.js"></script>
<link href="http://pmg.oomeng.cn/manager/date/select.css" rel="stylesheet" type="text/css" />
<style type="text/css">

.sys_user {
    color: #FFFFFF;
    font-size: 16px;
    padding-bottom:5%;
    padding-left: 190px;
    padding-top:2%;
    background: rgba(0, 0, 0, 0) url("../view/image/icons/yonghu.png") no-repeat scroll 152px 8px;
}

.sys_h_r {
    float: right;
    height: 16px;
    margin: 10px 0;
    min-width: 400px;
    padding-right: 1%;
}


.bt_sys_edit {
    background: rgba(0, 0, 0, 0) url("../view/image/icons/bianji.png") no-repeat scroll 0 center;
    margin: 0 2%;
}

.bt_sys_pass {
    background: rgba(0, 0, 0, 0) url("../view/image/icons/xiugaimima.png") no-repeat scroll 0 center;
    margin: 0 2%;
}

.bt_sys_exit {
    background: rgba(0, 0, 0, 0) url("../view/image/icons/tuichu.png") no-repeat scroll 0 center;
    margin: 0 1%;
}
.bt_action {
    color: #FFFFFF;
    float: right;
    font-size: 13px;
    height: 30px;
    line-height: 30px;
    padding-left:30px;
}

</style>
<!-- 
<script type="text/javascript" src="${CONTEXT_PATH}/view/js/indexBak.js"></script> -->

<script type="text/javascript" src="../view/js/indexBak.js"></script>
<script type="text/javascript" src="http://pmg.oomeng.cn/manager/ExtJS4.2/ux/TabCloseMenu.js"></script>
<script type="text/javascript">
document.onkeydown=function(event){ 
    e = event ? event :(window.event ? window.event : null); 
    if(e.keyCode==8){ 
        //执行的方法  
        //alert('退格键检测到了'); 
       window.location.href='#';
    } 
} 


function editform(){
    var edit_winForm =  Ext.create('Ext.form.Panel', {  
                frame: true,   //frame属性  
                //title: 'Form Fields',  
                width: 440,
                height:470,
                bodyPadding:5, 
                border:false,
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
//                    fieldLabel: 'Display field',  
                    value: '编辑信息'  
                     
                },{  
                    //输入账号  
                    xtype: 'textfield', 
                    name: 'user_id',  
                    hidden:true,
                    fieldLabel: 'id'  
                },
                {  
                    //输入账号  
                    xtype: 'textfield', 
                    name: 'user_name',  
                    readOnly:true,
                    fieldLabel: '账号'  
                },{  
                    //输入手机号  
                    xtype: 'textfield', 
                    name: 'user_phone',  
                    fieldLabel: '手机号'  
                },{  
                    //输入姓名  
                    xtype: 'textfield', 
                    name: 'user_realName',  
                    fieldLabel: '姓名'  
                }, {  
                    //输入Email
                    xtype: 'textfield',  
                    name: 'user_email',  
                    fieldLabel: 'Email'
                },
                {  
                    //输入备注
                    xtype: 'textarea',  
                    name: 'user_remark',  
                    fieldLabel: '备注'
                }]  
            });  
    
    edit_winForm.form.findField('user_id').setValue('${_user.id}'); 
    edit_winForm.form.findField('user_name').setValue('${_user.userName}'); 
    edit_winForm.form.findField('user_phone').setValue('${_user.phone}');  
    edit_winForm.form.findField('user_realName').setValue('${_user.realName}'); 
    edit_winForm.form.findField('user_email').setValue('${_user.email}');  
    edit_winForm.form.findField('user_remark').setValue('${_user.remark}');
    
    
    var editwindow = Ext.create('Ext.window.Window',{  
              title : "编辑用户",  
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
                                          url :'/systemManager/editSystemUserInfo',  
                                           //等待时显示 等待  
                                          waitTitle: '请稍等...',  
                                          waitMsg: '正在提交信息...',  
                                            
                                          success: function(fp, o) {  
      
                                            if (o.result== true) { 
                                             Ext.example.msg("提示","修改成功！");
	                                         editwindow.close(); //关闭窗口  
	                                         edit_winForm.form.reset(); 
	                                         var url = window.location.href;
	                                         window.location.href = '/systemManager/showLoginUserInfo';
	                                         //location.href = 'http://localhost:8080/view/homePage/homePage.jsp';
	                                         //location.reload();
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
  //创建window面板，表单面板是依托window面板显示的  
   
    
    };
    function passform(){  
	    var pass_winForm =  Ext.create('Ext.form.Panel', {  
	                frame: true,   //frame属性  
	                //title: 'Form Fields',  
	                width: 440,
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
	                    value: '修改密码'  
	                     
	                },{  
	                    //输入id  
	                    xtype: 'textfield', 
	                    name: 'user_id',
	                    hidden:true,
	                    fieldLabel: 'id'  
	                },{  
	                    //输入原密码
	                    xtype: 'textfield', 
	                    name: 'old_user_psw', 
	                    inputType: 'password',
	                    allowBlank: false,
	                    fieldLabel: '原密码'  
	                },
	                {  
	                    //输入密码
	                    xtype: 'textfield', 
	                    name: 'user_psw', 
	                    inputType: 'password',
	                    allowBlank: false,
	                    fieldLabel: '新密码'  
	                },{  
	                    //确认密码  
	                    xtype: 'textfield', 
	                    name: 'user_confirmPsw',
	                    inputType: 'password',
	                    allowBlank: false,
	                    fieldLabel: '确认密码'  
	                },
	                {  
	                    //密码提示  
	                    xtype: 'textfield', 
	                    name: 'user_pswHints',  
	                    fieldLabel: '密码提示'  
	                }
	                
	                ]  
	            });  
	    
	    pass_winForm.form.findField('user_id').setValue('${_user.id}'); 
	   
	    var passwindow = Ext.create('Ext.window.Window',{  
	              title : "修改密码",  
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
	              items : [pass_winForm],  
	              buttons : [{  
	                         text : "保存",  
	                         minWidth : 70,  
	                         handler : function() {  
	                            if (pass_winForm.getForm().isValid()) {  
	                            	
	                            	var user_psw=pass_winForm.form.findField('user_psw').getValue();
	                            	var user_confirmPsw=pass_winForm.form.findField('user_confirmPsw').getValue();
	                            	if(user_psw != user_confirmPsw){
	                            		 Ext.example.msg("提示","两次 密码输入不一致！");
	                            		 return;
	                            	}
	                            	pass_winForm.getForm().submit({  
	                                          url :'/systemManager/modifyUserPwd',  
	                                           //等待时显示 等待  
	                                          waitTitle: '请稍等...',  
	                                          waitMsg: '正在提交信息...',  
	                                            
	                                          success: function(fp, o) {
	                                        	  if (o.result.data == 1) {  
	                                        		  passwindow.close(); //关闭窗口  
	                                        		  pass_winForm.form.reset(); 
	                                        		  window.location.href = '/welcome';
	                                              }else {  
	                                            		 Ext.example.msg("提示",o.result.message);
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
	                        	 passwindow.close();  
	                         }  
	                     }]  
	           });  
	    passwindow.show();  
	  
	    };
	
	    
   function exit(){
	   Ext.Msg.confirm("提示信息", "请确定要退出吗?", function(btn) {
			if (btn == 'yes') {
				  window.location.href = '/systemManager/logout';
			}
		});
	 
   } 
</script>
</head>
<body>
     <div id="north-div" style="height: 110px; background-color: #232323;"><img alt="善金科技管理平台" style="padding-left: 60px; padding-top : 19px" src="http://pmg.oomeng.cn/manager/image/shanjin.png">
     <div class="sys_h_r">
     <div class="sys_user"><div style="padding-top:2%"> 您好：${_user_name} &nbsp;<span style="color:red;">(${_user_type})</span></div> </div>
     <div>
       <a  href="javascript:void(0);" onclick="exit();return false;">
         <div class="bt_sys_exit bt_action">退出</div>
       </a>
       <a target="_blank" href="javascript:void(0);" onclick="passform();return false;">
      <div class="bt_sys_pass bt_action">修改密码</div>
       </a>
      <cms:havePerm url='/systemManager/editSystemUserInfo'>
       <a target="_blank" href="javascript:void(0);" onclick="editform();return false;">
      <div class="bt_sys_edit bt_action">编辑</div>
       </a>
      </cms:havePerm>
      </div>

     
     </div>
    </div>
      <div id="south-div" align="center" style="color:#fff">技术支持 善金科技  Version 1.0</div>
      <div id="div2"></div>
</body>
</html>