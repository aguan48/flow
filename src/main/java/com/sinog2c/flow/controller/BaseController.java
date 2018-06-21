package com.sinog2c.flow.controller;

import java.util.List;
import java.util.Map;

import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;

public class BaseController {

	/**
	 * 分页排序查询列表
	 * @param query
	 * @param param
	 * @return
	 */
	public List<Model> getModelList(ModelQuery query, Map<String, Object> param){
		List<Model> list = null;
		String sidx = param.get("sidx").toString();
		String sord = param.get("sord").toString();
		if("id".equals(sidx.toLowerCase())) {
			if("asc".equals(sord)) {
				list = query.orderByModelId().asc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}else {
				list = query.orderByModelId().desc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}
		}else if("key".equals(sidx.toLowerCase())) {
			if("asc".equals(sord)) {
				list = query.orderByModelKey().asc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}else {
				list = query.orderByModelKey().desc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}
		}else if("name".equals(sidx.toLowerCase())) {
			if("asc".equals(sord)) {
				list = query.orderByModelName().asc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}else {
				list = query.orderByModelName().desc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}
		}else if("revision".equals(sidx.toLowerCase())) {
			if("asc".equals(sord)) {
				list = query.orderByModelVersion().asc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}else {
				list = query.orderByModelVersion().desc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}
		}else if("category".equals(sidx.toLowerCase())) {
			if("asc".equals(sord)) {
				list = query.orderByModelCategory().asc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}else {
				list = query.orderByModelCategory().desc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}
		}else if("lastupdatetime".equals(sidx.toLowerCase())) {
			if("asc".equals(sord)) {
				list = query.orderByLastUpdateTime().asc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}else {
				list = query.orderByLastUpdateTime().desc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}
		}else if("tenantid".equals(sidx.toLowerCase())) {
			if("asc".equals(sord)) {
				list = query.orderByTenantId().asc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}else {
				list = query.orderByTenantId().desc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}
		}else{
			if("asc".equals(sord)) {
				list = query.orderByCreateTime().asc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}else {
				list = query.orderByCreateTime().desc().listPage(Integer.parseInt(param.get("page").toString())-1, 
							Integer.parseInt(param.get("pageSize").toString()));
			}
		}
		return list;
	}
	
}
