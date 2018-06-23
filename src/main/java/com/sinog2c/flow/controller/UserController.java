package com.sinog2c.flow.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sinog2c.flow.domain.User;
import com.sinog2c.flow.service.UserService;
import com.sinog2c.flow.util.Util;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping(value = "/login")
	public String login(HttpServletRequest request) {
		String view = "login";
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		if(StringUtils.isEmpty(username)) {
			request.setAttribute("message", "请输入用户名！");
		}else if(StringUtils.isEmpty(password)) {
			request.setAttribute("message", "请输入密码！");
		}else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("username", username);
			map.put("state", "1");
			User user = userService.selectUserById(map);
			if(user == null) {
				request.setAttribute("message", "该户名不存在");
			} else {
				//验证密码
				if(user.getPassword().equals(Util.getPassword(username, password))) {
					//登录成功，创建session
					setSessionAttribute(request, SESSION_USER_KEY, user);
					//跳转页面
					view = "redirect:home";
				} else {
					request.setAttribute("message", "该户名或密码错误");
					request.setAttribute("username", username);
				}
			}
		}
		return view;
	}
	
	@RequestMapping(value = "/home")
	public String toHome(HttpServletRequest request) {
		String page = "home";
		User user = getLoginUser(request);
		if(user != null) {
			request.setAttribute("user", user);
		} else {
			page = "redirect:login";
		}
		return page;
	}
	
	@GetMapping(value = "/login")
	public String getLogin(HttpServletRequest request) {
		return "login";
	}
	
}
