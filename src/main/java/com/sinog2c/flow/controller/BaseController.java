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
	
}
