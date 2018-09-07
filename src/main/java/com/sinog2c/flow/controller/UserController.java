package com.sinog2c.flow.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sinog2c.flow.FlowApplication;
import com.sinog2c.flow.domain.User;
import com.sinog2c.flow.service.UserService;
import com.sinog2c.flow.util.Result;
import com.sinog2c.flow.util.PasswordUtil;
/**
 * 用户控制层
 * @author 许杰
 *
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	
	private static final Logger logger = Logger.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	/**
	 * 登录
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/login")
	public String login(HttpServletRequest request) {
		String view = "login";
		String userid = request.getParameter("userid");
		String password = request.getParameter("password");
		if(StringUtils.isEmpty(userid)) {
			request.setAttribute("message", "请输入用户名！");
			logger.info("流程系统登录：用户名为空");
		}else if(StringUtils.isEmpty(password)) {
			request.setAttribute("message", "请输入密码！");
			logger.info("流程系统登录：密码为空");
		}else {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userid", userid);
			map.put("isdelete", "0");
			User user = userService.selectUserById(map);
			if(user == null) {
				request.setAttribute("message", "该户名不存在");
				logger.info("流程系统登录：该用户名不存在");
			} else {
				//验证密码
				if(user.getPassword().equals(PasswordUtil.getPassword(userid, password))) {
					//登录成功，创建session
					setSessionAttribute(request, SESSION_USER_KEY, user);
					//跳转页面
					view = "redirect:home";
				} else {
					request.setAttribute("message", "用户名或密码错误");
					request.setAttribute("userid", userid);
				}
			}
		}
		return view;
	}
	
	/**
	 * 跳到主页
	 * @param request
	 * @return
	 */
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
	
	/**
	 * 跳到登录页
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/login")
	public String getLogin(HttpServletRequest request) {
		return "login";
	}
	
	@RequestMapping(value = "/index")
	public String toIndex(HttpServletRequest request) {
		return "index";
	}
	
	/**
	 * 退出登录
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public String logOut(HttpServletRequest request) {
		//销毁session
		HttpSession session = request.getSession(true);
		session.invalidate();
		return "redirect:login";
	}
	
	/**
	 * 获取用户信息
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/getUser")
	@ResponseBody
	public Result getUser(HttpServletRequest request) {
		Result result = new Result(false, "获取用户信息失败");
		User user = getLoginUser(request);
		if(user != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userid", user.getUserid());
			map.put("isdelete", "0");
			user = userService.selectUserById(map);
			result.setSuccess(true);
			result.setMessage("获取用户信息成功");
			result.setObj(user);
		}
		return result;
	}
	
	/**
	 * 编辑用户
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/editUser")
	@ResponseBody
	public Result editUser(User user, HttpServletRequest request) {
		Result result = new Result(false, "修改用户信息失败");
		String newPassword = request.getParameter("newPassword");
		
		//如果新密码不为空，处理新密码
		if(StringUtils.isNotEmpty(newPassword)) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userid", user.getUserid());
			map.put("isdelete", "0");
			User u = userService.selectUserById(map);
			//处理前台传递旧密码加密
			String password = PasswordUtil.getPassword(u.getUserid(), user.getPassword());
			//比对数据库中查出的密码和前台处理过的密码
			if(u != null && u.getPassword().equals(password)) {
				user.setId(u.getId());
				user.setPassword(PasswordUtil.getPassword(u.getUserid(), newPassword));
			}else {
				//请求失败，原密码填写有误
				result.setSuccess(false);
				result.setMessage("原始密码有误");
				return result;
			}
		}else {
			user.setPassword(null);
		}
		
		user.setOpId(user.getUserid());
		user.setOpTime(new Date());
		userService.updateUser(user);
		//销毁session
		HttpSession session = request.getSession(true);
		session.invalidate();
		//重新创建session
		setSessionAttribute(request, SESSION_USER_KEY, user);
		result.setSuccess(true);
		result.setMessage("修改用户信息成功");
		return result;
	}
	
}
