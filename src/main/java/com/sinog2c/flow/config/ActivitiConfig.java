package com.sinog2c.flow.config;

import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Activiti 配置类
* @ClassName:：ActivitiConfig 
* @Description： TODO
* @author ：gxx  
* @date ：2018年6月26日 下午2:09:39 
*
 */
@Configuration
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer{
	
	@Autowired(required=true)
	private IDGenerator idGenerator;
	
	//自定义流程引擎主键生成策略
	@Bean
	public ProcessEngineConfigurationImpl processEngineConfigurationImpl(ProcessEngineConfigurationImpl processEngineConfigurationImpl){
		//设置ProcessEngineConfigurationImpl里的uuidGenerator
		processEngineConfigurationImpl.setIdGenerator(idGenerator);
		//设置DbSqlSessionFactory的uuidGenerator，否则流程id，任务id，实例id依然是用DbIdGenerator生成
		processEngineConfigurationImpl.getDbSqlSessionFactory().setIdGenerator(idGenerator);
		return processEngineConfigurationImpl;
	}
	
	//流程图显示字体设置（乱码问题）
	@Override
	public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
		// TODO Auto-generated method stub
		processEngineConfiguration.setActivityFontName("宋体");
		processEngineConfiguration.setLabelFontName("宋体");
		processEngineConfiguration.setAnnotationFontName("宋体");
	}
	
}
