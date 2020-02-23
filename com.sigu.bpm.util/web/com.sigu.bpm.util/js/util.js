/**
 * 公用JS引用地址如下
 * <script type="text/javascript" src="../apps/com.sigu.bpm.util/js/util.js"></script>
 */

//执行导出文件相关的cmd并自动打开下载地址下载文件
function exportFile(data) {
	awsui.ajax.request({
		url : "./jd",
		method : "POST",
		data : data,
     	ok : function(r) {
            // console.log(r.data);
            window.open(r.data);
        },
        err : function(r) {
            // $.simpleAlert('错误信息内容','error');
            // $.simpleAlert('警告信息内容','warning');
        }
	});
}

//创建dialog文件上传弹框
function initDivDialog(){
	var div = "<div title='Excel数据导入' style='width:510px;display:none;' id='dialog-normal'>";
  	div += "<span id='myUpfile' class='button green'>上传</span>";
  	div += "</div>";
  	$("body").append(div);
}

//参数依次为 应用ID、Plugins中定义的dc仓库目录名称、自定义业务参数、回调函数
function initFileUpload(appId, repositoryName, extParam, callBack){
	//myUpfile对象绑定upfile组件(将Dialog弹框的指定按钮绑定文件上传事件)
    $("#myUpfile").upfile({
        sid: $("#sid").val(), // 会话ID
        appId: appId, // 应用ID
      	repositoryName: repositoryName, // 该应用申请的DC名 这个和Plugins里面声明的一致
        groupValue: "import", // DC大类，建议业务模块缩写	
        fileValue: "excel", // DC小类，建议具体功能模块的缩写
        numLimit: "1", //最多一次允许上传几个，0(无限制)
        //filesToFilter : [["Images (*.jpg; *.jpeg; *.gif; *.png; *.bmp)","*.jpg; *.jpeg; *.gif; *.png; *.bmp"]],
        extParam: extParam, //自定义业务参数，可以不定义，默认为空字符串
        done: function(e, data){
            //事件回调函数
            if (data['result']['data']['result'] == 'ok') {
                //关闭弹框
              	$("#dialog-normal").dialog("close");
              	//提示上传成功
              	$.simpleAlert('文件上传成功!');
            } else {
              	//关闭Dialog弹框
				$("#dialog-normal").dialog("close");
                // 上传失败，打印出错误信息
                $.simpleAlert(data['result']['data']['msg'], data['result']['data']['result']);
            }
			// 执行自定义回调函数
			if(typeof callBack === "function"){
				callBack(data);
			}
        }
    });
}

function getUserInfo(userId, callback){
	awsui.ajax.request({
		url : "./jd",
		method : "POST",
		data : {
			sid : $("#sid").val(),
			cmd : "com.sigu.bpm.util.getUserInfo",
          	userId : userId
		},
		ok : function(r) {
			//$.simpleAlert('提示信息内容','info',2000);//2000是表示2000毫秒后弹框自动消失
			//console.log(r);
          	if(typeof callback === "function"){
            	callback(r.data);
            }
		},
		err : function(r){
			//$.simpleAlert('错误信息内容','error');
			//$.simpleAlert('警告信息内容','warning');
		}
	});
}
