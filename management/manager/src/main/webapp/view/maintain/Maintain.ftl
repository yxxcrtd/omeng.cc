<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>html发布编辑页面</title>
    </head>

    <body>
        <div id="content">
            <form action="" id="form" method="post" enctype="multipart/form-data">
                <div>
                    <textarea name="maintain" id="maintain" style="width: 900px; height: 500px;">${obj.content!}</textarea>
                </div>

                <div id="operation">
                    <input type="submit" value="submit" hidden="true"  />
                    <input type="button" value="发布" onclick="checkName(this)"/>
                    <input type="button" value="保存" onclick="checkName(this)"/>
                </div>
                <input type="hidden" name="id" value="${obj.id!}" />
                <input type="hidden" name="title" value="${obj.title!}" />
            </form>
             
        </div>

        <script language="javascript" src="${request.contextPath}/view/js/kindeditor.js"></script>
        <script type="text/javascript" src="/view/js/jquery-1.8.0.min.js"></script>
        <script language="javascript">
        var editor;
        KindEditor.ready(function(K) {
            editor = K.create("#maintain", {
                pasteType : 1,
                newlineTag : "br",
                filterMode : false,
                formatUploadUrl : false,
                afterBlur:function () {
                    this.sync(); // 编辑器失去焦点时直接同步，可以取到编辑器的值
                }
            });
        });

function checkName(txt){
var val=txt.value;
if(val=='发布'){
    $("#form").attr("action", "/maintain/save");
    $("#form").submit();
  }else{
    $("#form").attr("action", "/maintain/onlySave");
    $("#form").submit();
  }
};
        </script>
    </body>
</html>
