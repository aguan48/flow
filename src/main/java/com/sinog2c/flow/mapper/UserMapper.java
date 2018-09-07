package com.sinog2c.flow.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sinog2c.flow.domain.User;

@Mapper
public interface UserMapper {
	
	public User selectUserById(Map<String, Object> param);
	
	public void updateUser(User user);
	
	List<Map> selectGroupIdsByUserIdAndTenantId(Map<String, Object> map);
	
}
