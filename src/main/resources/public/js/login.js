function submitValidate(){
	var flag = true;
	var username = $("#username").val();
	var password = $("#password").val();
	if(!username){
		$("#username_error").html("请输入用户名");
		$("#username").focus();
		flag = false;
	}
	if(!password){
		$("#password_error").html("请输入密码");
		if(flag){
			$("#password").focus();
		}
		flag = false;
	}
	return flag;
}

$(function(){
	//监听回车事件
	$("body").bind("keydown",function(e){
        // 兼容FF和IE和Opera    
	    var theEvent = e || window.event;    
	    var code = theEvent.keyCode || theEvent.which || theEvent.charCode;    
	    if (code == 13) {    
	        //回车执行查询
	        $('#login').submit();
	    }    
	});
	var username = $("#username").val();
	var password = $("#password").val();
	if(!username){
		$("#username").focus();
	}else if(!password){
		$("#password").focus();
	}
})