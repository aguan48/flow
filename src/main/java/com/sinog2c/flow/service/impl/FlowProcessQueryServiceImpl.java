package com.sinog2c.flow.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinog2c.flow.act.ActivitiUtil;
import com.sinog2c.flow.mapper.FlowStatusMapper;
import com.sinog2c.flow.mapper.UserMapper;
import com.sinog2c.flow.service.FlowProcessQueryService;
import com.sinog2c.flow.util.JsonResult;

/**
 * 流程查询服务
* @ClassName:：FlowProcessQueryService 
* @Description： TODO
* @author ：gxx  
* @date ：2018年8月27日 上午11:28:48 
*
 */
@Service("flowProcessQueryService")
public class FlowProcessQueryServiceImpl implements FlowProcessQueryService{
	
	private static final Logger logger = Logger.getLogger(FlowProcessQueryServiceImpl.class);
	
	/** 注入流程引擎*/
	@Autowired
    private ProcessEngine processEngine;
	
	/**提供流程文件和流程实例的方法 */
	@Autowired
    private RuntimeService runtimeService;
	
	/**提供对任务相关的各种操作 */
	@Autowired
    private TaskService taskService;
	
	/**提供方法获取各种流程和部署文件的信息 ，与流程定义与部署相关的接口方法仓库服务对象*/
	@Autowired
	private RepositoryService repositoryService;
	
	/**流程、变量历史*/
	@Autowired
	private HistoryService historyService;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private FlowStatusMapper flowStatusMapper;

	
	/*********************************************************************************************************************************************
	 * 根据用户获取个人任务集合
	 * 
	 * @param userId					用户编号userId
	 * @param processDefinitionKey		流程定义key
	 * @return
	 ********************************************************************************************************************************************/
	@Override
	public JsonResult<List<Map<String, Object>>> getPersonalTaskListByAssignee(String userId,
			String tenantId) {
		logger.info("======================[根据userId获取任务集合]方法名：getTaskListByAssignee[开始执行]=====================");
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		if(StringUtils.isEmpty(userId)) {
			return JsonResult.failMessage("用户编号不能为空！");
		}else {
			try {
				/**调用流程任务服务接口，根据个人userId查询个人任务 */
				List<Task> tasksList = taskService.createTaskQuery().taskAssignee(userId).taskTenantId(tenantId)
						.orderByTaskCreateTime().desc().list();
				if(tasksList != null || tasksList.size() > 0) {
					resultList = ActivitiUtil.parseListTask2ListMap(tasksList,processEngine);
				}
				return JsonResult.success(resultList);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("======================[根据userId获取任务集合]方法名：getTaskListByAssignee[获取异常]=====================");
				return JsonResult.failMessage("根据userId获取任务集合调用API异常");
			}
		}
	}
	
	/*********************************************************************************************************************************************
	 * 根据用户获取个人任务集合
	 * 
	 * @param userId					用户编号userId
	 * @param processDefinitionKey		流程定义key
	 * @return
	 ********************************************************************************************************************************************/
	@Override
	public JsonResult<List<Map<String, Object>>> getPersonalTaskListByAssignee(String userId, 
			String processDefinitionKey,
			String tenantId) {
		logger.info("======================[根据userId获取任务集合]方法名：getTaskListByAssignee[开始执行]=====================");
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(processDefinitionKey)) {
			return JsonResult.failMessage("userId和processDefinitionKey均不能为空！");
		}else {
			try {
				/**调用流程任务服务接口，根据个人userId查询个人任务 */
				List<Task> tasksList = taskService.createTaskQuery().taskAssignee(userId).taskTenantId(tenantId)
						.processDefinitionKey(processDefinitionKey).orderByTaskCreateTime().desc().list();
				if(tasksList != null || tasksList.size() > 0) {
					resultList = ActivitiUtil.parseListTask2ListMap(tasksList,processEngine);
				}
				return JsonResult.success(resultList);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("======================[根据userId获取任务集合]方法名：getTaskListByAssignee[获取异常]=====================");
				return JsonResult.failMessage("根据userId获取任务集合调用API异常");
			}
		}
	}
	
	/*********************************************************************************************************************************************
	 * 根据组编号获取组任务集合
	 * @param groupId				  	组（角色）编号
	 * @param processDefinitionKey		流程定义key
	 * @return
	 ********************************************************************************************************************************************/
	@Override
	public JsonResult<List<Map<String, Object>>> getGroupTaskListByGroupId(String groupId) {
		logger.info("======================[根据groupId获取组任务集合]方法名：getGroupListByGroupId[开始执行]=====================");
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		if(StringUtils.isEmpty(groupId)) {
			logger.error("=========[根据groupId获取组任务集合]方法名：getGroupListByGroupId[groupId均不能为空！]============");
			return JsonResult.failMessage("groupId均不能为空！");
		}else {
			try {
				/**调用流程服务接口，根据groupId查询组任务*/
				List<Task> tasksList = taskService.createTaskQuery().taskCandidateGroup(groupId)
						.orderByTaskCreateTime().desc().list();
				if(tasksList != null || tasksList.size() > 0) {
					resultList = ActivitiUtil.parseListTask2ListMap(tasksList,processEngine);
				}
				return JsonResult.success(resultList);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("======================[根据groupId获取组任务集合]方法名：getGroupListByGroupId[获取任务集合调用API异常]=====================");
				return JsonResult.failMessage("获取任务集合调用API异常");
			}
		}
	}

	/*********************************************************************************************************************************************
	 * 根据组编号获取组任务集合
	 * @param groupId				  	组（角色）编号
	 * @param processDefinitionKey		流程定义key
	 * @return
	 ********************************************************************************************************************************************/
	@Override
	public JsonResult<List<Map<String, Object>>> getGroupTaskListByGroupId(String groupId, String processDefinitionKey) {
		logger.info("======================[根据groupId获取组任务集合]方法名：getGroupListByGroupId[开始执行]=====================");
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		if(StringUtils.isEmpty(groupId) || StringUtils.isEmpty(processDefinitionKey)) {
			logger.error("=========[根据groupId获取组任务集合]方法名：getGroupListByGroupId[processDefinitionKey和groupId均不能为空！]============");
			return JsonResult.failMessage("processDefinitionKey和groupId均不能为空！");
		}else {
			try {
				/**调用流程服务接口，根据groupId查询组任务*/
				List<Task> tasksList = taskService.createTaskQuery().taskCandidateGroup(groupId)
						.processDefinitionKey(processDefinitionKey).orderByTaskCreateTime().desc().list();
				if(tasksList != null || tasksList.size() > 0) {
					resultList = ActivitiUtil.parseListTask2ListMap(tasksList,processEngine);
				}
				return JsonResult.success(resultList);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("======================[根据groupId获取组任务集合]方法名：getGroupListByGroupId[获取任务集合调用API异常]=====================");
				return JsonResult.failMessage("获取任务集合调用API异常");
			}
		}
	}
	
	/**
	 * 根据userId和系统代码Id获取用户所有流程的个人任务
	 * 
	 * @param userId
	 * @param tenantId
	 * @return
	 */
	@Override
	public JsonResult<List<Map<String, Object>>> getPersonalTaskListByAssigneeAndTenantId(String userId,
			String tenantId) {
		logger.info("======================[根据userId获取任务集合]方法名：getTaskListByAssignee[开始执行]=====================");
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		if(StringUtils.isEmpty(userId)) {
			return JsonResult.failMessage("用户编号不能为空！");
		}else {
			try {
				/**调用流程任务服务接口，根据个人userId查询个人任务 */
				List<Task> tasksList = taskService.createTaskQuery().taskAssignee(userId).taskTenantId(tenantId)
						.orderByTaskCreateTime().desc().list();
				if(tasksList != null || tasksList.size() > 0) {
					resultList = ActivitiUtil.parseListTask2ListMap(tasksList,processEngine);
				}
				return JsonResult.success(resultList);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("======================[根据userId获取任务集合]方法名：getTaskListByAssignee[获取异常]=====================");
				return JsonResult.failMessage("根据userId获取任务集合调用API异常");
			}
		}
	}
	
	/**
	 * 根据userId和系统代码Id获取用户本系统processDefinitionKey流程的组任务
	 * 
	 * @param userId
	 * @param tenantId
	 * @param processDefinitionKey
	 * @return
	 */
	@Override
	public JsonResult<List<Map<String, Object>>> getPersonalTaskListByAssigneeAndTenantId(String userId,
			String tenantId, String processDefinitionKey) {
		logger.info("======================[根据userId获取任务集合]方法名：getTaskListByAssignee[开始执行]=====================");
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(processDefinitionKey)) {
			return JsonResult.failMessage("userId和processDefinitionKey均不能为空！");
		}else {
			try {
				/**调用流程任务服务接口，根据个人userId查询个人任务 */
				List<Task> tasksList = taskService.createTaskQuery().taskAssignee(userId).taskTenantId(tenantId)
						.processDefinitionKey(processDefinitionKey).orderByTaskCreateTime().desc().list();
				if(tasksList != null || tasksList.size() > 0) {
					resultList = ActivitiUtil.parseListTask2ListMap(tasksList,processEngine);
				}
				return JsonResult.success(resultList);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("======================[根据userId获取任务集合]方法名：getTaskListByAssignee[获取异常]=====================");
				return JsonResult.failMessage("根据userId获取任务集合调用API异常");
			}
		}
	}
	
	/**
	 * 根据userId和系统代码Id获取用户本系统所在的组所有流程的组任务
	 * 
	 * @param userId
	 * @param tenantId
	 * @return
	 */
	@Override
	public JsonResult<List<Map<String, Object>>> getGroupTaskListByAssigneeAndTenantId(String userId, String tenantId) {
		logger.info("=============[根据userId和系统代码Id获取用户本系统所在的组所有流程的组任务]方法名：getGroupTaskListByAssigneeAndTenantId[开始执行]============");
		if(StringUtils.isEmpty(tenantId)) {
			return JsonResult.failMessage("tenantId系统标识不能为空");
		}else if(StringUtils.isEmpty(userId)) {
			return JsonResult.failMessage("userId用户编号不能为空");
		}else {
			Map paramMap = new HashMap();
			paramMap.put("userId", userId);
			paramMap.put("tenantId", tenantId);
			List<Map<String,Object>> taskLists = new ArrayList<Map<String,Object>>();
			try {
				//获取系统tenantId中userId所有的角色
				List<Map> groupIds = userMapper.selectGroupIdsByUserIdAndTenantId(paramMap);
				if(groupIds.size() < 1) {
					return JsonResult.successMessage("用户当前系统角色查询异常，请检查用户系统角色");
				}else {
					for (Map map : groupIds) {
						String groupId = (String)map.get("groupId");
						JsonResult<List<Map<String,Object>>> groupResult = this.getGroupTaskListByGroupId(groupId);
						List<Map<String,Object>> list = groupResult.getData();
						if(list != null && list.size() > 0) {
							if(taskLists.containsAll(list)) {
								continue;
							}else {
								taskLists.addAll(list);
							}
						}
					}
				}
				return JsonResult.success(taskLists);
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("=====[根据userId和系统代码Id获取用户本系统所在的组所有流程的组任务]方法名：getGroupTaskListByAssigneeAndTenantId[调用接口异常]====");
				return JsonResult.failMessage("调用查询接口异常");
			}
		}
	}
	
	/**
	 * 根据userId和系统代码Id获取用户本系统所在的组processDefinitionKey流程的组任务
	 * 
	 * @param userId
	 * @param tenantId
	 * @param processDefinitionKey
	 * @return
	 */
	@Override
	public JsonResult<List<Map<String, Object>>> getGroupTaskListByAssigneeAndTenantId(String userId, String tenantId,
			String processDefinitionKey) {
		logger.info("=============[根据userId和系统代码Id获取用户本系统所在的组processDefinitionKey流程的组任务]方法名：getGroupTaskListByAssigneeAndTenantId[开始执行]============");
		if(StringUtils.isEmpty(tenantId)) {
			return JsonResult.failMessage("tenantId系统标识不能为空");
		}else if(StringUtils.isEmpty(userId)) {
			return JsonResult.failMessage("userId用户编号不能为空");
		}else {
			Map<String,Object> paramMap = new HashMap();
			paramMap.put("userId", userId);
			paramMap.put("tenantId", tenantId);
			List<Map<String,Object>> taskLists = null;
			try {
				List<Map> groupIds = userMapper.selectGroupIdsByUserIdAndTenantId(paramMap);
				if(groupIds.size() < 1) {
					return JsonResult.successMessage("用户当前系统角色查询异常，请检查用户系统角色");
				}else {
					for (Map<String,Object> map : groupIds) {
						String groupId = (String)map.get("groupId");
						JsonResult<List<Map<String,Object>>> groupResult = this.getGroupTaskListByGroupId(groupId,processDefinitionKey);
						List<Map<String,Object>> list = groupResult.getData();
						if(list != null && list.size() > 0) {
							taskLists.addAll(list);
						}
					}
				}
				return JsonResult.success(taskLists);
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("=====[根据userId和系统代码Id获取用户本系统所在的组所有流程的组任务]方法名：getGroupTaskListByAssigneeAndTenantId[调用接口异常]====");
				return JsonResult.failMessage("调用查询接口异常");
			}
		}
	}

	/**
	 * 根据流程实例Id：processInstanceId获取历史活动
	 * @param processInstanceId			流程实例编号
	 * @return
	 */
	@Override
	public JsonResult<List<Map<String, Object>>> getHisActivityByProcessInstanceId(String processInstanceId) {
		//活动查询
		try {
			// 历史相关Service
			// 创建历史活动实例查询
			// 执行流程实例id
			//开始时间倒序
			List<HistoricActivityInstance>  list= historyService 
			        .createHistoricActivityInstanceQuery() 
			        .processInstanceId(processInstanceId) 
			        .orderByHistoricActivityInstanceStartTime().desc() 
			        .list();
			List<Map<String,Object>> data = ActivitiUtil.parseListHistoricActivityInstance2ListMap(list,processEngine);
			return JsonResult.success(data);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("=====[根据流程实例Id：processInstanceId获取历史活动]方法名：getHisActivityByProcessInstanceId[调用接口异常]====");
			return JsonResult.failMessage("调用查询接口getHisActivityByProcessInstanceId异常");
		}
	}

	@Override
	public JsonResult<List<Map<String, Object>>> getAllTaskByTenantIdAndProcessDefinitionKey(String tenantId,String processDefinitionKey) {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		try {
			List<Task> tasksList = taskService.createTaskQuery().processDefinitionKey(processDefinitionKey).taskTenantId(tenantId).list();
			if(tasksList != null || tasksList.size() > 0) {
				resultList = ActivitiUtil.parseListTask2ListMap(tasksList,processEngine);
			}
			return JsonResult.success(resultList);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonResult.failMessage("调用查询接口getAllTaskByTenantIdAndProcessDefinitionKey异常");
		}
	}

	@Override
	public JsonResult<Integer> selectPersonalBacklogTaskCount(String userId, String tenantId) {
		Map<String,String> param = new HashMap<String,String>(16);
		param.put("userId", userId);
		param.put("tenantId", tenantId);
		int total = flowStatusMapper.selectPersonalBacklogTaskCount(param);
		return JsonResult.success(total);
	}

	@Override
	public JsonResult<Integer> selectGroupBacklogTaskCount(String groupIds, String tenantId) {
		Map<String,String> param = new HashMap<String,String>(16);
		param.put("groupIds", groupIds);
		param.put("tenantId", tenantId);
		int total = flowStatusMapper.selectGroupBacklogTaskCount(param);
		return JsonResult.success(total);
	}
	


	
	
	
	
	
	
	
	
	

}
