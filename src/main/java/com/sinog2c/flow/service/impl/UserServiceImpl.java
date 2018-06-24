package com.sinog2c.flow.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinog2c.flow.domain.User;
import com.sinog2c.flow.mapper.UserMapper;
import com.sinog2c.flow.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;

	@Override
	public User selectUserById(Map<String, Object> param) {
		return userMapper.selectUserById(param);
	}

	@Override
	public void updateUser(User user) {
		userMapper.updateUser(user);
	}

}
