function submitValidate(){
	var flag = true;
	var username = $("#username").val();
	var password = $("#password").val();
	if(!username){
		$("#username_error").html("请输入用户名");
		flag = false;
	}
	if(!password){
		$("#password_error").html("请输入密码");
		flag = false;
	}
	return flag;
}