package com.sinog2c.flow.config;

import org.activiti.engine.impl.cfg.IdGenerator;
import org.springframework.stereotype.Component;

import com.sinog2c.flow.util.IdGen;

/**
 * 自定义流程引擎主键生成策略
* @ClassName:：IDGenerator 
* @Description： TODO
* @author ：gxx  
* @date ：2018年8月7日 下午3:02:51 
*
 */
@Component
public class IDGenerator implements IdGenerator{

	@Override
	public String getNextId() {
		// TODO Auto-generated method stub
		//使用javaUtil的UUID
		//String id = (String) UUID.randomUUID().toString();
		//使用工具生成，调用主键生成工具
		long next = IdGen.get().next();
		String id = String.valueOf(next);
		return id;
	}
}
