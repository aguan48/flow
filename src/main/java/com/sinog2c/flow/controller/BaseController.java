package com.sinog2c.flow.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;

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

	/**
	 * 分页排序查询列表
	 * @param query
	 * @param param
	 * @return
	 */
	public List<Model> getModelList(ModelQuery query, Map<String, Object> param){
		List<Model> list = null;
		String sort = param.get("sort").toString();
		String order = param.get("order").toString();
		String search = param.get("search").toString();
		if(!"".equals(search)) {
			query = query.modelNameLike(search);
		}
		if("id".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByModelId().asc();
			}else {
				query = query.orderByModelId().desc();
			}
		}else if("key".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByModelKey().asc();
			}else {
				query = query.orderByModelKey().desc();
			}
		}else if("name".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByModelName().asc();
			}else {
				query = query.orderByModelName().desc();
			}
		}else if("revision".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByModelVersion().asc();
			}else {
				query = query.orderByModelVersion().desc();
			}
		}else if("category".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByModelCategory().asc();
			}else {
				query = query.orderByModelCategory().desc();
			}
		}else if("lastupdatetime".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByLastUpdateTime().asc();
			}else {
				query = query.orderByLastUpdateTime().desc();
			}
		}else if("tenantid".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByTenantId().asc();
			}else {
				query = query.orderByTenantId().desc();
			}
		}else{
			if("asc".equals(order)) {
				query = query.orderByCreateTime().asc();
			}else {
				query = query.orderByCreateTime().desc();
			}
		}
		list = query.listPage(Integer.parseInt(param.get("offset").toString()), 
				Integer.parseInt(param.get("limit").toString()));
		return list;
	}
	
}
