package com.sinog2c.flow.service;

import java.util.List;
import java.util.Map;

import com.sinog2c.flow.util.JsonResult;

/**
 * 流程查询服务
* @ClassName:：FlowProcessQueryService 
* @Description： TODO
* @author ：gxx  
* @date ：2018年8月27日 上午11:28:48 
*
 */
public interface FlowProcessQueryService {
	
	
	/**
	 * 获取全部流程个人任务集合
	 * 
	 * @param userId					用户编号userId
	 * @return
	 */
	JsonResult<List<Map<String,Object>>> getPersonalTaskListByAssignee(String userId,
			String tenantId);
	
	/**
	 * 根据流程定义key获取个人任务集合
	 * 
	 * @param userId					用户编号userId
	 * @param processDefinitionKey		流程定义key
	 * @return
	 */
	JsonResult<List<Map<String,Object>>> getPersonalTaskListByAssignee(String userId,
			String processDefinitionKey,
			String tenantId);
	
	
	/**
	 * 获取组任务全部流程集合
	 * @param groupId				  	组（角色）编号
	 * @return
	 */
	JsonResult<List<Map<String,Object>>> getGroupTaskListByGroupId(String groupId);
	
	/**
	 * 根据流程定义key获取组任务集合
	 * @param groupId				  	组（角色）编号
	 * @param processDefinitionKey		流程定义key
	 * @return
	 */
	JsonResult<List<Map<String,Object>>> getGroupTaskListByGroupId(String groupId,String processDefinitionKey);
	
	/**
	 * 根据userId和系统代码Id获取用户所有流程的个人任务
	 * 
	 * @param userId
	 * @param tenantId
	 * @return
	 */
	JsonResult<List<Map<String,Object>>> getPersonalTaskListByAssigneeAndTenantId(String userId ,String tenantId);
	
	/**
	 * 根据userId和系统代码Id获取用户本系统processDefinitionKey流程的组任务
	 * 
	 * @param userId
	 * @param tenantId
	 * @param processDefinitionKey
	 * @return
	 */
	JsonResult<List<Map<String,Object>>> getPersonalTaskListByAssigneeAndTenantId(String userId ,String tenantId ,String processDefinitionKey);
	
	/**
	 * 根据userId和系统代码Id获取用户本系统所在的组所有流程的组任务
	 * 
	 * @param userId
	 * @param tenantId
	 * @return
	 */
	JsonResult<List<Map<String,Object>>> getGroupTaskListByAssigneeAndTenantId(String userId ,String tenantId);
	
	/**
	 * 根据userId和系统代码Id获取用户本系统所在的组processDefinitionKey流程的组任务
	 * 
	 * @param userId
	 * @param tenantId
	 * @param processDefinitionKey
	 * @return
	 */
	JsonResult<List<Map<String,Object>>> getGroupTaskListByAssigneeAndTenantId(String userId ,String tenantId ,String processDefinitionKey);
	
	/**
	 * 根据流程实例Id：processInstanceId获取历史活动
	 * 
	 * @param processInstanceId				流程实例编号
	 * @return
	 */
	JsonResult<List<Map<String,Object>>> getHisActivityByProcessInstanceId(String processInstanceId);
	
	/**
	 * 根据租户和流程定义获取全部任务
	 * 
	 * @param tenantId				系统
	 * @param processDefinitionKey	流程定义key
	 * @return
	 */
	JsonResult<List<Map<String,Object>>> getAllTaskByTenantIdAndProcessDefinitionKey(String tenantId,String processDefinitionKey);
	
	/**
	 * 获取个人某系统待办统计
	 * 
	 * @param tenantId				系统
	 * @param processDefinitionKey	流程定义key
	 * @return
	 */
	JsonResult<Integer> selectPersonalBacklogTaskCount(String userId,String tenantId);
	
	/**
	 * 获取组任务数量
	 * 
	 * @param tenantId				系统
	 * @param processDefinitionKey	流程定义key
	 * @return
	 */
	JsonResult<Integer> selectGroupBacklogTaskCount(String groupIds,String tenantId);
}
