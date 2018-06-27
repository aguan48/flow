package com.sinog2c.flow.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinitionQuery;

import com.sinog2c.flow.domain.ProcessDefinitionResponse;

public interface CommonFlowQueryListService {
	
	List<Model> getModelList(ModelQuery query, Map<String, Object> param);
	
	List<ProcessDefinitionResponse> getDeploymentList(ProcessDefinitionQuery query,Map<String,Object> param);
	
}
