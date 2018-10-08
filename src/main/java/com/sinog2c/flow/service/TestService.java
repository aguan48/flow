package com.sinog2c.flow.service;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "SINOG2C-RESOURCES")
public interface TestService {
	
	@GetMapping("/view/archive/merge/archive-merge-page")
	String testConnect();
	
}
