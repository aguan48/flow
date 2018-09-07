package com.sinog2c.flow.service;

import java.util.List;
import java.util.Map;

import com.sinog2c.flow.util.JsonResult;

/**
 * Activiti流程引擎【流程流转】核心服务
 * 
* @ClassName:：FlowProcessCoreService 
* @Description： TODO
* @author ：gxx  
* @date ：2018年8月28日 下午2:43:09 
*
 */
public interface FlowProcessCoreService {
	
	
	/**
	 * 启动一个流程
	 * 
	 * @param processDefinitionKey		流程定义key
	 * @param tenantId					系统ID
	 * @param variables					流程变量
	 * @param businessKey				业务主键
	 * @return
	 */
	JsonResult<String> startFlowProcess(String processDefinitionKey,String tenantId,Map<String,Object> variables,String businessKey) throws Exception;
	
	/**
	 * 流程退回操作
	 * 
	 * @param taskId					当前任务ID
	 * @param postilMessage				批注信息
	 * @param variables					流程变量
	 * @return
	 * @throws Exception 
	 */
	JsonResult backFlowProcess(String taskId,String postilMessage,Map<String,Object> variables) throws Exception;
	
	/**
	 * 流程提交操作
	 * @param taskId					当前任务ID
	 * @param postilMessage				批注信息
	 * @param variables					流程变量
	 * @return
	 * @throws Exception 
	 */
	JsonResult passFlowProcess(String taskId,String postilMessage,Map<String,Object> variables) throws Exception;
	
	/**
	 * 流程拒绝操作（结束）
	 * @param taskId					当前任务ID
	 * @param postilMessage				批注信息
	 * @param variables					流程变量
	 * @return
	 */
	JsonResult refuseFlowProcess(String taskId,String postilMessage,Map<String,Object> variables) throws Exception;
	
	/**
	 * 任务接收
	 * 
	 * @param taskId					当前任务ID
	 * @param userId					接收人编号userId
	 * @return
	 */
	JsonResult<String> claimTask(String taskId,String userId) throws Exception;
	
	/**
	 * 任务退回到组内
	 * 
	 * @param taskId					当前任务ID
	 * @return
	 */
	JsonResult<String> unClaimTask(String taskId) throws Exception;
	
}
