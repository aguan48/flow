package com.sinog2c.flow.config;

import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
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

	@Override
	public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
		// TODO Auto-generated method stub
		processEngineConfiguration.setActivityFontName("宋体");
		processEngineConfiguration.setLabelFontName("宋体");
		processEngineConfiguration.setAnnotationFontName("宋体");
	}

}
