package com.sinog2c.flow.config;

import javax.sql.DataSource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.history.HistoryLevel;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

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
	
	@Autowired
    private PlatformTransactionManager transactionManager;
	
	@Autowired
    private DataSource druidDataSource;
	
	//自定义流程引擎主键生成策略
	@Bean
	public ProcessEngineConfigurationImpl processEngineConfigurationImpl(ProcessEngineConfigurationImpl processEngineConfigurationImpl){
		//设置ProcessEngineConfigurationImpl里的uuidGenerator
		processEngineConfigurationImpl.setIdGenerator(idGenerator);
		//设置DbSqlSessionFactory的uuidGenerator，否则流程id，任务id，实例id依然是用DbIdGenerator生成
		processEngineConfigurationImpl.getDbSqlSessionFactory().setIdGenerator(idGenerator);
		return processEngineConfigurationImpl;
	}
	
	//配置
	@Override
	public void configure(SpringProcessEngineConfiguration processEngineConfiguration) {
		// TODO Auto-generated method stub
		// 流程图显示字体设置（乱码问题）
		processEngineConfiguration.setActivityFontName("宋体");
		processEngineConfiguration.setLabelFontName("宋体");
		processEngineConfiguration.setAnnotationFontName("宋体");
		// 数据源设置
		processEngineConfiguration.setDataSource(druidDataSource);
		processEngineConfiguration.setDatabaseType("mysql");
		processEngineConfiguration.setDatabaseSchemaUpdate("true");
		// 事物控制
		processEngineConfiguration.setTransactionManager(transactionManager);
		// 任务执行器、异步执行器
		processEngineConfiguration.setJobExecutorActivate(false);
		processEngineConfiguration.setAsyncExecutorEnabled(false);
		processEngineConfiguration.setAsyncExecutorActivate(false);
		// 历史记录等级
		processEngineConfiguration.setHistoryLevel(HistoryLevel.FULL);
		// 数据库验证
		processEngineConfiguration.setDbIdentityUsed(false);
		
	}
	
	
	@Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }
 
    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }
 
    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }
 
    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }
 
    @Bean
    public ManagementService managementService(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }
 
    @Bean
    public FormService formService(ProcessEngine processEngine) {
        return processEngine.getFormService();
    }
	
    
    @Bean
    public IdentityService identityService(ProcessEngine processEngine) {
        return processEngine.getIdentityService();
    }
}
