<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html lang="en" ng-app="omengApp">
<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>选择平台</title>
    <link rel="stylesheet" href="../view/css/ui-dialog.css">
    <style>
        .clearfix:after {
            content: " ";
            visibility: hidden;
            display: block;
            clear: both;
            height: 0;
            font-size: 0;
        }

        .clearfix {
            *zoom: 1;
        }

        body {
            background: #373737;
        }

        .platformPanel {
            min-height: 500px;
            width: 500px;
            border-radius: 15px;
            box-shadow: 0px 0px 10px 0px #000;
            overflow: hidden;
            background: rgba(255, 255, 255, 0.1);
            position: absolute;
            top: 50%;
            left: 50%;
            margin: -250px 0 0 -250px;
        }

        .platformPanel .header {
            height: 30px;
            line-height: 30px;
            padding: 15px 25px;
            color: #fff;
            background: #FC5929;
            text-align: center;
        }

        .platformPanel .header .title {
            float: left;
        }

        .platformPanel .header .aside {
            float: right;
        }

        .platformPanel .header .aside a {
            outline: none;
        }

        .platformPanel .header .aside a {
            margin: 0 0 0 10px;
            text-decoration: none;
            color: #FFF;
        }

        .platformPanel .content {
            padding: 15px 5px 15px 25px;
            font-size: 0;
        }

        .platformPanel .content:after {
            content: '';
            clear: left;
        }

        .box-item {
            background: #FAFFBD;
            display: table;
            width: 130px;
            height: 130px;
            border-radius: 15px;
            cursor: pointer;
            float: left;
            font-size: 18px;
            margin: 0 25px 25px 0;
            box-shadow: 0px 0px 10px 0px #000;
        }

        .box-item .item-wrap {
            display: table-cell;
            padding: 10px;
            text-align: center;
            vertical-align: middle;
        }

        .box-item .item-wrap a {
            text-decoration: none;
            color: #000;
        }

        .editPop {
        }

        .editPop ul {
            margin: 0;
            padding: 0;
            list-style: none;
        }

        .editPop ul li {
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<div class="platformPanel">
    <div class="header">
        <div class="title">欢迎访问O盟后管平台</div>
        <div class="aside">
            欢迎您,<span>admin</span> <a id="editInfo" href="javascript:;">编辑</a><a id="changePsd"
                                                                                 href="JavaScript:;">改密</a>
        </div>
    </div>
    <div class="content clearfix">
      <c:forEach items="${sysList}" var="item">
      <a target="_blank" href="${item.sysLink}">
          <div class="box-item">
            <div class="item-wrap">   ${item.sysName}          </div>
          </div>
      </a>
      </c:forEach>
    </div>
</div>
<div class="editLayer" style="display: none;">
    <div class="editPop">
        <form>
            <ul>
                <li>
                    <label for="111">姓名</label>
                    <input type="text" name="name" id="111">
                </li>
                <li>
                    <label for="222">电话</label>
                    <input type="text" name="phone" id="222">
                </li>
                <li>
                    <label for="333">地址</label>
                    <input type="text" name="address" id="333">
                </li>
                <li>
                    <label for="444">邮箱</label>
                    <input type="text" name="email" id="444">
                </li>
            </ul>
        </form>
    </div>
</div>
<script src="../view/js/jquery-1.8.0.min.js"></script>
<script src="../view/js/dialog-min.js"></script>
<script>
    $(document).on("click", "#editInfo", function () {

        var d = dialog({
            id:"editPop",
            fixed: true,
            quickClose: true,
            autofocus:true,
            title      : '提示',
            content    : $(".editPop"),
            okValue    : '确定',
            ok         : function () {
                var self = this;
                this.title('提交中…');
                setTimeout(function () {
                    self.title('已提交');
                    self.content('保存成功!');
                    setTimeout(function(){
                        self.close();
                    },1000)
                }, 2000);
                return false;
            },
            cancelValue: '取消',
            cancel     : function () {
            }
        });
        d.show();
    })
</script>
</body>
</html>
