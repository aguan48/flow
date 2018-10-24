package com.sinog2c.flow.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sinog2c.flow.domain.User;

public class BaseController {
	
	public static final String SESSION_USER_KEY = "session_user_key";
	
	/**
	 * 设置session属性
	 * @param request HttpServletRequest 请求对象
	 * @param name 属性名
	 * @param value 属性值, 可序列化对象
	 */
	public static void setSessionAttribute(HttpServletRequest request, String name, Object value) {
		// 当前是基于单容器的实现
		HttpSession session = request.getSession(true);
		session.setAttribute(name, value);
	}
	
	/**
	 * 获取session
	 * @param request
	 * @param name
	 * @return
	 */
	public static Object getSessionAttribute(HttpServletRequest request, String name) {
		HttpSession session = request.getSession(true);
		return session.getAttribute(name);
	}
	
	/**
	 * 设置Session存活时间
	 * @param request
	 * @param aliveTimeSeconds
	 */
	public static void setSessionAliveTime(HttpServletRequest request, int aliveTimeSeconds) {
		// 当前是基于单容器的实现
		HttpSession session = request.getSession(true);
		session.setMaxInactiveInterval(aliveTimeSeconds);
	}
	
	/**
	 * 获取当前登录的用户
	 * @param request
	 * @return
	 */
	public static User getLoginUser(HttpServletRequest request) {
		User user = null;
		Object obj = getSessionAttribute(request, SESSION_USER_KEY);
		if(obj instanceof User){
			user = (User)obj;
		}
		return user;
	}
	
}
