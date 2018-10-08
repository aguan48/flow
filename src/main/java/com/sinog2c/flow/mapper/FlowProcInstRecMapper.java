package com.sinog2c.flow.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.sinog2c.flow.domain.FlowProcInstRec;

@Mapper
public interface FlowProcInstRecMapper {
	
	int insertOneFlowProceInstRec(FlowProcInstRec rec);
	
	int insertOneFlowProceInstRecHis(FlowProcInstRec rec);
	
	int delOneFlowProceInstRec(String processInstanceId);
	
	int updateFlowProceInstRec(Map<String,Object> map);
	
	int updateFlowProceInstRecHis(Map<String,Object> map);
	
	FlowProcInstRec selectFlowProcInstRecByProcessInstanceId(String processInstanceId);
	
	FlowProcInstRec selectLastFlowProcInstRecHisByProcessInstanceId(String processInstanceId);
	
	List<Map<String,Object>> selectPersonalBacklogTaskCount(Map<String,String> map);
	
}
