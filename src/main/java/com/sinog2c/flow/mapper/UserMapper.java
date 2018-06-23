package com.sinog2c.flow.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sinog2c.flow.domain.User;

@Mapper
public interface UserMapper {
	
	public User selectUserById(Map<String, Object> param);
	
}
