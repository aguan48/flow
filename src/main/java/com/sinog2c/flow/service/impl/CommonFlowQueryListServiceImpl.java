package com.sinog2c.flow.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sinog2c.flow.domain.HistoricActivityInstanceResponse;
import com.sinog2c.flow.domain.HistoricProcessInstanceResponse;
import com.sinog2c.flow.domain.HistoricTaskInstanceResponse;
import com.sinog2c.flow.domain.HistoricVariableInstanceResponse;
import com.sinog2c.flow.domain.ProcessDefinitionResponse;
import com.sinog2c.flow.service.CommonFlowQueryListService;

/**
 * 流程相关列表查询
 */
@Service("commonFlowQueryListService")
public class CommonFlowQueryListServiceImpl implements CommonFlowQueryListService{
	
	private Logger logger = LoggerFactory.getLogger(CommonFlowQueryListServiceImpl.class);
	
	/**
	 * 模型查询列表
	 */
	@Override
	public List<Model> getModelList(ModelQuery query, Map<String, Object> param) {
		List<Model> list = null;
		String sort = param.get("sort").toString();
		String order = param.get("order").toString();
		String search = param.get("search").toString();
		if(!"".equals(search)) {
			query = query.modelNameLike(search);
		}
		if("id".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByModelId().asc();
			}else {
				query = query.orderByModelId().desc();
			}
		}else if("key".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByModelKey().asc();
			}else {
				query = query.orderByModelKey().desc();
			}
		}else if("name".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByModelName().asc();
			}else {
				query = query.orderByModelName().desc();
			}
		}else if("revision".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByModelVersion().asc();
			}else {
				query = query.orderByModelVersion().desc();
			}
		}else if("category".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByModelCategory().asc();
			}else {
				query = query.orderByModelCategory().desc();
			}
		}else if("lastupdatetime".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByLastUpdateTime().asc();
			}else {
				query = query.orderByLastUpdateTime().desc();
			}
		}else if("tenantid".equals(sort.toLowerCase())) {
			if("asc".equals(order)) {
				query = query.orderByTenantId().asc();
			}else {
				query = query.orderByTenantId().desc();
			}
		}else{
			if("asc".equals(order)) {
				query = query.orderByCreateTime().asc();
			}else {
				query = query.orderByCreateTime().desc();
			}
		}
		list = query.listPage(Integer.parseInt(param.get("offset").toString()), 
				Integer.parseInt(param.get("limit").toString()));
		return list;
	}
	
	/**
	 * 已部署流程定义查询列表
	 */
	@Override
	public List<ProcessDefinitionResponse> getDeploymentList(ProcessDefinitionQuery query, Map<String, Object> param) {
		List<ProcessDefinition> processDefinitions = null;
		String sort = param.get("sort").toString().toLowerCase();
		String order = param.get("order").toString();
		String search = param.get("search").toString();
		
		/**查询search*/
		if(!"".equals(search)) {
			query = query.processDefinitionNameLike(search);
		}
		
		
		/**判断排序根据*/
		if("id".equals(sort)) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByProcessDefinitionId().asc();
			}else if("desc".equals(order)){
				query = query.orderByProcessDefinitionId().desc();
			}
		}else if("name".equals(sort)) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByProcessDefinitionName().asc();
			}else if("desc".equals(order)){
				query = query.orderByProcessDefinitionName().desc();
			}
		}else if("version".equals(sort)) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByProcessDefinitionVersion().asc();
			}else if("desc".equals(order)){
				query = query.orderByProcessDefinitionVersion().desc();
			}
		}else if("key".equals(sort)) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByProcessDefinitionKey().asc();
			}else if("desc".equals(order)){
				query = query.orderByProcessDefinitionKey().desc();
			}
		}
		
		processDefinitions = query.listPage(Integer.parseInt(param.get("offset").toString()), 
					Integer.parseInt(param.get("limit").toString()));
		List<ProcessDefinitionResponse> list = new ArrayList<>();
        for(ProcessDefinition processDefinition: processDefinitions){
            list.add(new ProcessDefinitionResponse(processDefinition));
        }
		
		return list;
	}

	@Override
	public List<HistoricProcessInstanceResponse> queryHistoricProcessInstance(HistoricProcessInstanceQuery query,
			Map<String, Object> param) {
		List<HistoricProcessInstance> historicProcessInstances = null;
		String sort = param.get("sort").toString().toLowerCase();
		String order = param.get("order").toString();
		String search = param.get("search").toString();
		if(!"".equals(search)) {
			query = query.processInstanceId(search);
		}
		
		if("endTime".equals(sort)) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByProcessInstanceEndTime().asc();
			}else if("desc".equals(order)){
				query = query.orderByProcessInstanceEndTime().desc();
			}
		}else {
			query = query.orderByProcessInstanceEndTime().desc();
		}
		
		historicProcessInstances = query.listPage(Integer.parseInt(param.get("offset").toString()), 
				Integer.parseInt(param.get("limit").toString()));
		
		List<HistoricProcessInstanceResponse> list = new ArrayList<>();
		for(HistoricProcessInstance historicProcessInstance: historicProcessInstances){
            list.add(new HistoricProcessInstanceResponse(historicProcessInstance));
        }
		
		return list;
	}

	@Override
	public List<HistoricActivityInstanceResponse> queryHistoricActivityInstance(HistoricActivityInstanceQuery query,
			Map<String, Object> param) {
		List<HistoricActivityInstance> historicActivityInstances = null;
		String sort = param.get("sort").toString().toLowerCase();
		String order = param.get("order").toString();
		String search = param.get("search").toString();
		if(!"".equals(search)) {
			query = query.processInstanceId(search);
		}
		
		if("startTime".equals(sort)) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByHistoricActivityInstanceStartTime().asc();
			}else if("desc".equals(order)){
				query = query.orderByHistoricActivityInstanceStartTime().desc();
			}
		}else {
			query = query.orderByHistoricActivityInstanceStartTime().desc();
		}
		
		historicActivityInstances = query.listPage(Integer.parseInt(param.get("offset").toString()), 
				Integer.parseInt(param.get("limit").toString()));
		
		List<HistoricActivityInstanceResponse> list = new ArrayList<>();
		for(HistoricActivityInstance historicActivityInstance: historicActivityInstances){
            list.add(new HistoricActivityInstanceResponse(historicActivityInstance));
        }
		
		return list;
	}

	@Override
	public List<HistoricTaskInstanceResponse> queryHistoricTaskInstance(HistoricTaskInstanceQuery query,
			Map<String, Object> param) {
		List<HistoricTaskInstance> historicTaskInstances = null;
		String sort = param.get("sort").toString().toLowerCase();
		String order = param.get("order").toString();
		String search = param.get("search").toString();
		if(!"".equals(search)) {
			query = query.processInstanceId(search);
		}
		
		if("startTime".equals(sort)) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByHistoricTaskInstanceStartTime().asc();
			}else if("desc".equals(order)){
				query = query.orderByHistoricTaskInstanceStartTime().desc();
			}
		}else {
			query = query.orderByHistoricTaskInstanceStartTime().desc();
		}
		
		historicTaskInstances = query.listPage(Integer.parseInt(param.get("offset").toString()), 
				Integer.parseInt(param.get("limit").toString()));
		
		List<HistoricTaskInstanceResponse> list = new ArrayList<>();
		for(HistoricTaskInstance historicTaskInstance: historicTaskInstances){
            list.add(new HistoricTaskInstanceResponse(historicTaskInstance));
        }
		
		return list;
	}

	@Override
	public List<HistoricVariableInstanceResponse> queryHistoricVariableInstance(HistoricVariableInstanceQuery query,
			Map<String, Object> param) {
		List<HistoricVariableInstance> historicVariableInstances = null;
		String sort = param.get("sort").toString().toLowerCase();
		String order = param.get("order").toString();
		String search = param.get("search").toString();
		if(!"".equals(search)) {
			query = query.processInstanceId(search);
		}
		
		if("processInstanceId".equals(sort)) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByProcessInstanceId().asc();
			}else if("desc".equals(order)){
				query = query.orderByProcessInstanceId().desc();
			}
		}
		
		historicVariableInstances = query.listPage(Integer.parseInt(param.get("offset").toString()), 
				Integer.parseInt(param.get("limit").toString()));
		
		List<HistoricVariableInstanceResponse> list = new ArrayList<>();
		for(HistoricVariableInstance historicVariableInstance: historicVariableInstances){
            list.add(new HistoricVariableInstanceResponse(historicVariableInstance));
        }
		
		return list;
	}
	
}
