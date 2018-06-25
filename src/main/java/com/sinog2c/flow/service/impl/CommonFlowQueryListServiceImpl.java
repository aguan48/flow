package com.sinog2c.flow.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sinog2c.flow.domain.DeploymentResponse;
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
	public List<DeploymentResponse> getDeploymentList(DeploymentQuery query, Map<String, Object> param) {
		List<Deployment> deployments = null;
		String sort = param.get("sort").toString();
		String order = param.get("order").toString();
		String search = param.get("search").toString();
		/**查询search*/
		if(!"".equals(search)) {
			query = query.deploymentNameLike(search);
		}
		/**判断排序根据*/
		if("id".equals(sort.toLowerCase())) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByDeploymentId().asc();
			}else if("desc".equals(order)){
				query = query.orderByDeploymentId().desc();
			}
		}else if("name".equals(sort.toLowerCase())) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByDeploymentName().asc();
			}else if("desc".equals(order)){
				query = query.orderByDeploymentName().desc();
			}
		}else if("deploymenttime".equals(sort.toLowerCase())) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByDeploymenTime().asc();
			}else if("desc".equals(order)){
				query = query.orderByDeploymenTime().desc();
			}
		}else if("tenantid".equals(sort.toLowerCase())) {
			/**正序or倒序*/
			if("asc".equals(order)) {
				query = query.orderByTenantId().asc();
			}else if("desc".equals(order)){
				query = query.orderByTenantId().desc();
			}
		}
		
		deployments = query.listPage(Integer.parseInt(param.get("offset").toString()), 
					Integer.parseInt(param.get("limit").toString()));
		List<DeploymentResponse> list = new ArrayList<>();
        for(Deployment deployment: deployments){
            list.add(new DeploymentResponse(deployment));
        }
		return list;
	}
	
}
