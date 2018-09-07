package com.sinog2c.flow.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface TestMapper {
	int insertOne(Map map);
	int insertTwo(Map map);
}
