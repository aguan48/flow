package com.sinog2c.flow.controller;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.sinog2c.flow.mapper.TestMapper;
import com.sinog2c.flow.service.TestService;

/**
 * 对外测试
 * 
* @ClassName:：TestController 
* @Description： TODO
* @author ：gxx  
* @date ：2018年8月28日 下午3:27:04 
*
 */
@Controller
@RequestMapping("/test")
public class TestController {
	
	@Autowired
	private TestMapper testMapper;
	
	@Autowired
	private RepositoryService repositoryService;
	
	
	@GetMapping("/testConnect")
	@ResponseBody
	public String testConnect() {
		
		return "弄死你！！！";
	}
	
	/**
	 * 测试事物
	 * @return
	 */
	@RequestMapping("/testTransactional")
	@ResponseBody
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
	public String testTransactional() {
		
		Map map = new HashMap();
		
		map.put("id", "12345");
		map.put("name", "家属的恐惧感");
		
		testMapper.insertOne(map);//业务操作
		
		repositoryService.deleteModel("1234");//流程操作
		
//		testMapper.insertTwo(map);		
		return "end";
	}
	
}
