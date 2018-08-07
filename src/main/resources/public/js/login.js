function submitValidate(){
	var flag = true;
	var userid = $("#userid").val();
	var password = $("#password").val();
	if(!userid){
		$("#userid_error").html("请输入用户名");
		$("#userid").focus();
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
	var userid = $("#userid").val();
	var password = $("#password").val();
	if(!userid){
		$("#userid").focus();
	}else if(!password){
		$("#password").focus();
	}
})