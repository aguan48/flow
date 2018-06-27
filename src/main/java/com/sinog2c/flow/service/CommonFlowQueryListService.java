package com.sinog2c.flow.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinitionQuery;

import com.sinog2c.flow.domain.HistoricActivityInstanceResponse;
import com.sinog2c.flow.domain.HistoricProcessInstanceResponse;
import com.sinog2c.flow.domain.HistoricTaskInstanceResponse;
import com.sinog2c.flow.domain.HistoricVariableInstanceResponse;
import com.sinog2c.flow.domain.ProcessDefinitionResponse;

public interface CommonFlowQueryListService {
	
	List<Model> getModelList(ModelQuery query, Map<String, Object> param);
	
	List<ProcessDefinitionResponse> getDeploymentList(ProcessDefinitionQuery query,Map<String,Object> param);
	
	List<HistoricProcessInstanceResponse> queryHistoricProcessInstance(HistoricProcessInstanceQuery query,Map<String,Object> param);
	
	List<HistoricActivityInstanceResponse> queryHistoricActivityInstance(HistoricActivityInstanceQuery query,Map<String,Object> param);
	
	List<HistoricTaskInstanceResponse> queryHistoricTaskInstance(HistoricTaskInstanceQuery query,Map<String,Object> param);
	
	List<HistoricVariableInstanceResponse> queryHistoricVariableInstance(HistoricVariableInstanceQuery query,Map<String,Object> param);
}
