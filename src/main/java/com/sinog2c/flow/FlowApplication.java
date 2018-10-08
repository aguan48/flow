package com.sinog2c.flow;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.sinog2c.flow.config.JsonpCallbackFilter;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableTransactionManagement//事物
@EnableAutoConfiguration(exclude = {
        org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
        org.activiti.spring.boot.SecurityAutoConfiguration.class
})
public class FlowApplication {

	private static final Logger logger = Logger.getLogger(FlowApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FlowApplication.class, args);
		logger.info("=======================================================================");
		logger.info(" gk-flow --> service start message: flow-流程服务提供者启动成功... ");
		logger.info("=======================================================================");
	}
	
	//整合activiti所需要的filter
    @Bean
    public JsonpCallbackFilter filter(){
        return new JsonpCallbackFilter();
    }
    
}
