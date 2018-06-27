package com.sinog2c.flow.interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sinog2c.flow.controller.BaseController;
import com.sinog2c.flow.domain.User;

public class LoginInterceptor implements HandlerInterceptor {
	
	private Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);
	
	private List<String> excludeList;

	@Value("${excludeList}")
	public void setExcludeList(String param) {
		List<String> excludeList = new ArrayList<String>();
		if(StringUtils.isNotEmpty(param)) {
			String[] values = param.split(",");
			excludeList.addAll(Arrays.asList(values));
		}
		this.excludeList = excludeList;
	}
	
	private boolean validURI(HttpServletRequest request){
		logger.debug("验证用户是否登录");
		User user = BaseController.getLoginUser(request);
		if(user != null) {
			return true;
		}
		// 排除列表中
		logger.debug("排除列表中不拦截的url");
		String uri = request.getRequestURI();

		Iterator<String> iterator = excludeList.iterator();
		while (iterator.hasNext()) {
			String exURI = (String) iterator.next();
			if(null != exURI && uri.contains(exURI)){
				return true;
			}
		}
		// 未登录,不允许
		return false;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean flag = validURI(request);
		if(!flag) {
			String url = request.getScheme() + "://"+ request.getServerName() + 
					":" + request.getLocalPort() + request.getContextPath();
			//请求不合法，跳到登录页
			redirect(request, response, url + "/user/login");
		}
		return flag;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	//对于请求是ajax请求重定向问题的处理方法  
	public void redirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException{  
		//如果request.getHeader("X-Requested-With") 返回的是"XMLHttpRequest"说明就是ajax请求，需要特殊处理 否则直接重定向就可以了  
		if("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))){  
			//告诉ajax我是重定向  
			response.setHeader("REDIRECT", "REDIRECT");  
			//告诉ajax我重定向的路径  
			response.setHeader("CONTENTPATH", url);  
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);  
		}else{  
			response.sendRedirect(url);
		}  
	}
}
