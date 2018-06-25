package com.sinog2c.flow.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;

import com.sinog2c.flow.domain.DeploymentResponse;

public interface CommonFlowQueryListService {
	
	List<Model> getModelList(ModelQuery query, Map<String, Object> param);
	
	List<DeploymentResponse> getDeploymentList(DeploymentQuery query,Map<String,Object> param);
}
