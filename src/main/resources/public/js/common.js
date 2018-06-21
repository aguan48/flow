(function($) {  
	$.alerts = {         
			alert: function(message, title, callback) {  
				$.alerts._uuid = uuid();
				if(!title) title = '温馨提示';  
				else if(typeof title == 'function'){
					callback = title;
					title = '温馨提示';
				}
				$.alerts._show(title, message, null, 'alert', function(result) {  
					if(callback && typeof callback == 'function' ) callback(result);  
				});  
			},  

			confirm: function(message, title, callback) { 
				$.alerts._uuid = uuid();
				if(!title) title = '温馨提示'; 
				else if(typeof title == 'function') {
					callback = title;
					title = '温馨提示';  
				}
				$.alerts._show(title, message, null, 'confirm', function(result) {  
					if(callback && typeof callback == 'function') callback(result);
				});
			},
			_show: function(title, msg, value, type, callback) {  
				var _html = ""; 
				_html += '<div id="mb_box_'+$.alerts._uuid+'"></div><div id="mb_con_'+$.alerts._uuid+'">';
				if(title){
					_html += '<span id="mb_tit_'+$.alerts._uuid+'">' + title + '</span>';
				}
				_html += '<div id="mb_msg_'+$.alerts._uuid+'">' + msg + '</div><div id="mb_btnbox_'+$.alerts._uuid+'">';  
				if (type == "alert") {  
					_html += '<input id="mb_btn_ok_'+$.alerts._uuid+'" type="button" value="确定" />';  
				}  
				if (type == "confirm") {  
					_html += '<input id="mb_btn_ok_'+$.alerts._uuid+'" type="button" value="确定" />';  
					_html += '<input id="mb_btn_no_'+$.alerts._uuid+'" type="button" value="取消" />';  
				}  
				_html += '</div></div>';  

				//必须先将_html添加到body，再设置Css样式  
				$("body").append(_html); GenerateCss();  

				switch( type ) {  
					case 'alert':  
	
						$("#mb_btn_ok_"+$.alerts._uuid).click( function() {  
							$.alerts._hide(this);  
							callback(true);  
						});  
						$("#mb_btn_ok_"+$.alerts._uuid).focus().keypress( function(e) {  
							if( e.keyCode == 13 || e.keyCode == 27 ) $("#mb_btn_ok_"+$.alerts._uuid).trigger('click');  
						});  
						break;  
					case 'confirm':  
	
						$("#mb_btn_ok_"+$.alerts._uuid).click( function() {  
							$.alerts._hide(this);  
							if( callback ) callback(true); 
						});  
						$("#mb_btn_no_"+$.alerts._uuid).click( function() {  
							$.alerts._hide(this);  
							if( callback ) callback(false);  
						});  
						$("#mb_btn_no_"+$.alerts._uuid).focus();  
						$("#mb_btn_ok_"+$.alerts._uuid+", #mb_btn_no_"+$.alerts._uuid).keypress( function(e) {  
							if( e.keyCode == 13 ) $("#mb_btn_ok_"+$.alerts._uuid).trigger('click');  
							if( e.keyCode == 27 ) $("#mb_btn_no_"+$.alerts._uuid).trigger('click');  
						});  
						break;  
					}  
				},  
				_hide: function(obj) {
					var id = $(obj).attr("id")
					id = id.substring(id.lastIndexOf("_")+1);
					$("#mb_box_"+id+",#mb_con_"+id).remove();  
				}  
	}  
	// Shortuct functions  
	zdalert = function(message, title, callback) {  
		$.alerts.alert(message, title, callback);  
	}  

	zdconfirm = function(message, title, callback) {  
		$.alerts.confirm(message, title, callback);  
	};  
	//生成Css  
	var GenerateCss = function () {  
		$("#mb_box_"+$.alerts._uuid).css({ width: '100%', height: '100%', zIndex: '99999', position: 'fixed',  
			filter: 'Alpha(opacity=60)', backgroundColor: 'black', top: '0', left: '0', opacity: '0.6'  
		});  
		$("#mb_con_"+$.alerts._uuid).css({ zIndex: '999999', minWidth: '20%', maxWidth: '50%', position: 'fixed',  
			backgroundColor: 'White', borderRadius: '5px'  
		});  
		$("#mb_tit_"+$.alerts._uuid).css({ display: 'block', fontSize: '14px', color: '#444', padding: '10px 15px',  
			backgroundColor: '#DDD', borderRadius: '5px 5px 0 0',  
			borderBottom: '3px solid #009BFE', fontWeight: 'bold'  
		});  
		$("#mb_msg_"+$.alerts._uuid).css({ padding: '20px', lineHeight: '20px',  
			borderBottom: '1px dashed #DDD', fontSize: '13px'  
		});  
		$("#mb_ico_"+$.alerts._uuid).css({ display: 'block', position: 'absolute', right: '10px', top: '9px',  
			border: '1px solid Gray', width: '18px', height: '18px', textAlign: 'center',  
			lineHeight: '16px', cursor: 'pointer', borderRadius: '5px', fontFamily: '微软雅黑'  
		});  
		$("#mb_btnbox_"+$.alerts._uuid).css({ margin: '15px 0 10px 0', textAlign: 'center' });  
		$("#mb_btn_ok_"+$.alerts._uuid+",#mb_btn_no_"+$.alerts._uuid).css({ width: '85px', height: '30px', color: 'white', border: 'none' });  
		$("#mb_btn_ok_"+$.alerts._uuid).css({ backgroundColor: '#168bbb' });  
		$("#mb_btn_no_"+$.alerts._uuid).css({ backgroundColor: 'gray', marginLeft: '20px' });  
		//右上角关闭按钮hover样式  
		$("#mb_ico_"+$.alerts._uuid).hover(function () {  
			$(this).css({ backgroundColor: 'Red', color: 'White' });  
		}, function () {  
			$(this).css({ backgroundColor: '#DDD', color: 'black' });  
		});  
		var _widht = document.documentElement.clientWidth; //屏幕宽  
		var _height = document.documentElement.clientHeight; //屏幕高  
		var boxWidth = $("#mb_con_"+$.alerts._uuid).width();  
		var boxHeight = $("#mb_con_"+$.alerts._uuid).height();  
		//让提示框居中  
		$("#mb_con_"+$.alerts._uuid).css({ top: (_height - boxHeight) / 2 + "px", left: (_widht - boxWidth) / 2 + "px" });  
	}  
})(jQuery); 
function uuid() {
    var s = [];
    var hexDigits = "0123456789abcdef";
    for (var i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";
 
    var uuid = s.join("");
    return uuid;
}

window.alert = $.alerts.alert;
window.confirm = $.alerts.confirm;