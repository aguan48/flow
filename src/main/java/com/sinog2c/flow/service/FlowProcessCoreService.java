package com.sinog2c.flow.service;

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
	 * @param paramMap					参数集合
	 * @return
	 * @throws Exception
	 */
	JsonResult<String> startFlowProcess(Map<String,Object> paramMap) throws Exception;
	
	/**
	 * 流程退回操作
	 * 
	 * @param taskIds					当前任务ID,多个以英文逗号分隔
	 * @param postilMessage				批注信息
	 * @param variables					流程变量
	 * @param userId					接收人编号userId
	 * @return
	 * @throws Exception 
	 */
	JsonResult<?> backFlowProcess(String taskId,String postilMessage,Map<String,Object> variables,String userId) throws Exception;
	
	/**
	 * 流程提交操作
	 * @param taskIds					当前任务ID,多个以英文逗号分隔
	 * @param postilMessage				批注信息
	 * @param variables					流程变量
	 * @param userId					接收人编号userId
	 * @return
	 * @throws Exception 
	 */
	JsonResult<?> passFlowProcess(String taskIds,String postilMessage,Map<String,Object> variables,String userId) throws Exception;
	
	/**
	 * 流程拒绝操作（结束）
	 * @param taskIds					当前任务ID,多个以英文逗号分隔
	 * @param postilMessage				批注信息
	 * @param variables					流程变量
	 * @param userId					接收人编号userId
	 * @return
	 * @throws Exception
	 */
	JsonResult<?> refuseFlowProcess(String taskIds,String postilMessage,Map<String,Object> variables,String userId) throws Exception;
	
	/**
	 * 任务接收
	 * 
	 * @param taskIds					当前任务ID,多个以英文逗号分隔
	 * @param userId					接收人编号userId
	 * @return
	 * @throws Exception
	 */
	JsonResult<?> claimTask(String taskIds,String userId) throws Exception;
	
	/**
	 * 任务退回到组内
	 * @param taskIds					当前任务ID,多个以英文逗号分隔
	 * @param userId					用户编号
	 * @return
	 * @throws Exception
	 */
	JsonResult<?> unClaimTask(String taskIds,String userId) throws Exception;
	
}
