package com.sinog2c.flow.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController {

	@RequestMapping(value="/error/{code}")
	public ModelAndView errorPage(@PathVariable(name="code") String code, HttpServletRequest request) {
		String page = null;
		if(StringUtils.isNotEmpty(code)) {
			request.setAttribute("code", code);
			page = "errorPage";
		}
		return new ModelAndView(page);
	}
}
