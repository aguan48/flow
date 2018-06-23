package com.sinog2c.flow.service;

import java.util.Map;

import com.sinog2c.flow.domain.User;

public interface UserService {
	
	public User selectUserById(Map<String, Object> param);
	
}
