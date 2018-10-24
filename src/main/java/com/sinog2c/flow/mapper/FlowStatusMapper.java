package com.sinog2c.flow.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sinog2c.flow.domain.FlowStatus;

@Mapper
public interface FlowStatusMapper {
	
	int insertFlowStatus(FlowStatus flowStatus);
	
	int insertFlowStatusHis(FlowStatus flowStatus);
	
	int updateFlowStatus(FlowStatus flowStatus);
	
	FlowStatus selectFlowStatusByProcessInstanceId(String processInstanceId);
	
	int selectPersonalBacklogTaskCount(Map<String,String> map);
	
	int selectGroupBacklogTaskCount(Map<String,String> map);
}
