package com.sinog2c.flow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinog2c.flow.act.CustomStencilConstants;
import com.sinog2c.flow.config.IDGenerator;
import com.sinog2c.flow.domain.FlowStatus;
import com.sinog2c.flow.mapper.FlowStatusMapper;
import com.sinog2c.flow.service.FlowProcessCoreService;
import com.sinog2c.flow.util.Constant;
import com.sinog2c.flow.util.JsonResult;

/**
 * Activiti流程引擎【流程流转】核心服务
 * 
* @ClassName:：ActivitiCoreServiceImpl 
* @Description： 流程流转
* @author ：gxx  
* @date ：2018年8月28日 上午11:47:17 
*
 */

@Service("flowProcessCoreService")
public class FlowProcessCoreServiceImpl implements FlowProcessCoreService {
		
	private static final Logger logger = Logger.getLogger(FlowProcessCoreServiceImpl.class);
	
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
	
	/**表单*/
	@Autowired
	private FormService formService;
	
	@Autowired
	private IDGenerator IDGenerator;
	
	@Autowired
	private FlowStatusMapper flowStatusMapper;
	
	@Autowired
	private IdentityService identityService;
	
	private final String userTask = "userTask";
	private static final String END = "END";
	
	
	
	/*********************************************************************************************************************************************
	 * 启动一个或多个流程
	 * variables包括processDefinitionKey、tenantId、userId
	 * @param processDefinitionKey		流程定义key
	 * @param tenantId					系统ID
	 * @param variables					流程变量
	 * @param businessKeys				业务主键以英文逗号,分隔
	 * @param userId					用户编号
	 * @return
	 ********************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=60000,rollbackFor=Exception.class)
	public JsonResult<String> startFlowProcess(Map<String,Object> variables) 
			throws Exception{
		
		logger.info("======================开始启动流程=====================");
		String processDefinitionKey = (String)variables.get("processDefinitionKey");
		String tenantId = (String)variables.get("tenantId");
		List<Map<String,String>> businessList = (List<Map<String,String>>)variables.get("businessList");
		String userId = (String)variables.get("userId");
		variables.remove("processDefinitionKey");
		variables.remove("tenantId");
		variables.remove("businessList");
		variables.remove("userId");
		
		if(StringUtils.isAnyEmpty(processDefinitionKey,tenantId,userId) && businessList.size()>0) {
			String errorInfo = "启动失败：缺少参数！";
			errorInfo  += "[processDefinitionKey="+processDefinitionKey;
			errorInfo  += ",tenantId="+tenantId;
			errorInfo  += ",businessList="+businessList.toString();
			errorInfo  += ",userId="+userId + "]";
			logger.info(errorInfo);
			return JsonResult.failMessage(errorInfo);
		}
		
		/**流程变量传递办理人，第一级流程节点为个人任务节点*/
		variables.put("startUserId", userId);
		
		for (Map<String, String> map : businessList) {
			String businessKey = map.get("businessKey");
			String flowBusinessKey = IDGenerator.getNextId();
			String applyid = map.get("applyid");
			String applyname = map.get("applyname");
			/** 设置发起人*/
			identityService.setAuthenticatedUserId(userId);
			/**根据流程定义ID启动流程*/
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey, flowBusinessKey, variables, tenantId);
			String processInstanceId = processInstance.getId();
			
			/**记录状态*/
			// 当前节点可编辑节点
			String candealaipformnode = getCustomPropertyByName(CustomStencilConstants.PROPERTY_CANDEAL_AIPFORMNODE,processInstance,null);
			FlowStatus flowStatus = new FlowStatus(businessKey,flowBusinessKey,processInstanceId,Constant.flow_start,"启动流程",candealaipformnode,applyid,applyname,userId,new Date());
			flowStatusMapper.insertFlowStatus(flowStatus);
			flowStatusMapper.insertFlowStatusHis(flowStatus);
		}
		
		logger.info("======================启动成功=====================");
		return JsonResult.success("启动成功");
	}
	
	/********************************************************************************************************************************************
	 * 流程退回操作
	 * 此方法不支持多实例回退！！！如果扩展需求，有多实例应用场景，请重新编写
	 * 
	 * @param taskIds					当前任务ID,多个以英文逗号分隔
	 * @param variables					流程变量
	 * @return
	 * @throws Exception 
	 *******************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=60000,rollbackFor=Exception.class)
	public JsonResult<?> backFlowProcess(String taskIds,
			String postilMessage,
			Map<String,Object> variables,
			String userId) 
			throws Exception{
		
		logger.info("==============================流程退回操作==========================");
		if(StringUtils.isAnyEmpty(taskIds,userId)) {
			String errorInfo = "退回失败：缺少参数！";
			errorInfo  += "[taskIds="+taskIds;
			errorInfo  += ",userId="+userId + "]";
			logger.info(errorInfo);
			return JsonResult.failMessage(errorInfo);
		}
		
		/** 批注信息,可以为空 */
		if(StringUtils.isEmpty(postilMessage)) {
			postilMessage = "退回";
		}
		
		/** 流程变量为空，初始化 */
		if( variables == null ) {
			variables = new HashMap<String,Object>(16);
		}
		
		/**任务循环退回操作*/
		String[] taskIdsC = taskIds.split(",");
		for (String taskId : taskIdsC) {
			/**添加批注信息*/
			addProcessPostil(taskId,postilMessage);
			Task task = this.findTaskById(taskId);
			String processInstanceId = task.getProcessInstanceId();
			ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
			/**获取可以退回的的任务节点*/
			List<ActivityImpl> activityList = this.findBackAvtivity(taskId);
			if( activityList == null || activityList.size() <= 0 ) {
				logger.error("==============================[流程退回操作]方法名：backFlowProcess[没有检测到可退回的节点！]==========================");
				throw new Exception("没有检测到可退回的节点");
			}
			ActivityImpl activityImpl = activityList.get(0);
			String activityId = activityImpl.getId();
			/** 退回 */
			this.backProcess(taskId, activityId, variables);
			/** 自动接收*/
			this.autoClaimTaskAfterBackTask(activityId,processInstanceId);
			
			/**更新状态*/
			String candealaipformnode = getCustomPropertyByName(CustomStencilConstants.PROPERTY_CANDEAL_AIPFORMNODE,processInstance,activityId);
			FlowStatus flowStatus = flowStatusMapper.selectFlowStatusByProcessInstanceId(processInstanceId);
			flowStatus.setFlowStatus(Constant.flow_back);
			flowStatus.setOpid(userId);
			flowStatus.setOptime(new Date());
			flowStatus.setPostilMessage(postilMessage);
			flowStatus.setCandealaipformnode(candealaipformnode);
			flowStatusMapper.insertFlowStatusHis(flowStatus);
			flowStatusMapper.updateFlowStatus(flowStatus);
		}
		logger.info("==============================[流程退回操作]方法名：backFlowProcess[退回成功]==========================");
		return JsonResult.successMessage("退回成功");
	}
	
	/********************************************************************************************************************************************
	 * 流程提交操作
	 * 
	 * @param taskIds					当前任务ID，多个以英文逗号分隔
	 * @param variables					流程变量
	 * @return
	 * @throws Exception 
	 *******************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=60000,rollbackFor=Exception.class)
	public JsonResult<?> passFlowProcess(String taskIds,
			String postilMessage, 
			Map<String, Object> variables,
			String userId) 
			throws Exception {
		
		logger.info("==============================流程提交操作==========================");
		if(StringUtils.isAnyEmpty(taskIds,userId)) {
			String errorInfo = "提交失败：缺少参数！";
			errorInfo  += "[taskIds="+taskIds;
			errorInfo  += ",userId="+userId + "]";
			logger.info(errorInfo);
			return JsonResult.failMessage(errorInfo);
		}
		
		/** 批注信息*/
		if(StringUtils.isEmpty(postilMessage)) {
			postilMessage = "同意";
		}
		/** 流程变量为空，初始化*/
		if( variables == null ) {
			variables = new HashMap<String,Object>(16);
		}
		
		Map<String,String> resultMap = new HashMap<String,String>(16);
		
		String[] taskIdsC = taskIds.split(",");
		for (String taskId : taskIdsC) {
			/**记录流程状态*/
			ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
			String processInstacneId = processInstance.getId();
			// 判断下级节点是否结束节点
			String status = Constant.flow_agree;
			if(this.judgeNextIsOver(taskId)) {
				// 流程结束
				status = Constant.flow_pass;
			}
			
			/**添加批注信息，提交通过*/
			this.addProcessPostil(taskId,postilMessage);
			this.passProcess(taskId, variables);
			
			/**存储审批通过后流程是否结束*/
			String businessKey = processInstance.getBusinessKey();
			resultMap.put(businessKey, status);
			
			/**更新状态*/
			FlowStatus flowStatus = flowStatusMapper.selectFlowStatusByProcessInstanceId(processInstacneId);
			flowStatus.setFlowStatus(status);
			flowStatus.setOpid(userId);
			flowStatus.setPostilMessage(postilMessage);
			flowStatus.setOptime(new Date());
			flowStatus.setCandealaipformnode("null");
			flowStatusMapper.insertFlowStatusHis(flowStatus);
			flowStatusMapper.updateFlowStatus(flowStatus);
		}
		logger.info("==============================[流程提交操作]方法名：passFlowProcess[提交成功]==========================");
		return JsonResult.success(resultMap);
	}
	
	/*********************************************************************************************************************************************
	 * 
	 * 流程拒绝操作（直接结束END）
	 * 
	 * @param taskIds					当前任务ID,多个以英文逗号分隔
	 * @param variables					流程变量
	 * @return
	 * @throws Exception 
	 * 
	 ********************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=60000,rollbackFor=Exception.class)
	public JsonResult<?> refuseFlowProcess(String taskIds,
			String postilMessage, 
			Map<String, Object> variables,
			String userId) throws Exception {
		
		logger.info("==============================流程拒绝操作==========================");
		if(StringUtils.isAnyEmpty(taskIds,userId)) {
			String errorInfo = "拒绝失败：缺少参数！";
			errorInfo  += "[taskIds="+taskIds;
			errorInfo  += ",userId="+userId + "]";
			logger.info(errorInfo);
			return JsonResult.failMessage(errorInfo);
		}
		
		/** 批注信息 */
		if(StringUtils.isEmpty(postilMessage)) {
			postilMessage = "拒绝";
		}
		/** 流程变量为空，初始化 */
		if( variables == null ) {
			variables = new HashMap<String,Object>(16);
		}
		
		/**多任务循环处理*/
		String[] taskIdsC = taskIds.split(",");
		for (String taskId : taskIdsC) {
			
			/**更新状态*/
			/**记录流程状态*/
			ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
			String processInstacneId = processInstance.getId();
			
			/**更新状态*/
			FlowStatus flowStatus = flowStatusMapper.selectFlowStatusByProcessInstanceId(processInstacneId);
			
			HistoricProcessInstance hpi= historyService // 历史任务Service  
			        .createHistoricProcessInstanceQuery() // 创建历史流程实例查询  
			        .processInstanceId(processInstacneId) // 指定流程实例ID  
			        .singleResult();
			String startUserId = hpi.getStartUserId();
			
			/** 拒绝用户和启动用户为同一个人，流程自动标记撤回状态 */
			if(userId.equals(startUserId)) {
				flowStatus.setFlowStatus(Constant.flow_recall);
			}else {
				flowStatus.setFlowStatus(Constant.flow_refuse);
			}
			
			flowStatus.setOpid(userId);
			flowStatus.setPostilMessage(postilMessage);
			flowStatus.setOptime(new Date());
			flowStatusMapper.insertFlowStatusHis(flowStatus);
			flowStatusMapper.updateFlowStatus(flowStatus);
			
			//添加批注
			this.addProcessPostil(taskId,postilMessage);
			//结束流程
			this.endProcess(taskId,variables);
		}
		
		logger.info("==============================[流程拒绝（结束）操作]方法名：refuseFlowProcess[拒绝（结束）成功]==========================");
		return JsonResult.successMessage("拒绝（结束）成功");
	}
	
	/*********************************************************************************************************************************************
	 * 任务接收
	 * 
	 * @param taskIds					当前任务ID,多个以英文逗号分隔
	 * @param userId					接收人编号userId
	 * @return
	 ********************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=60000,rollbackFor=Exception.class)
	public synchronized JsonResult<?> claimTask(String taskIds, String userId) throws Exception{
		
		logger.info("==============================任务接收操作==========================");
		if(StringUtils.isAnyEmpty(taskIds,userId)) {
			String errorInfo = "拒绝失败：缺少参数！";
			errorInfo  += "[taskIds="+taskIds;
			errorInfo  += ",userId="+userId + "]";
			logger.info(errorInfo);
			return JsonResult.failMessage(errorInfo);
		}
		
		List<String> resultKey = new ArrayList<String>();
		String[] taskIdsC = taskIds.split(",");
		for (String taskId : taskIdsC) {
			// 任务接收
			taskService.claim(taskId, userId);
			/**记录流程状态*/
			ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
			String processInstacneId = processInstance.getId();
			
			/**更新状态*/
			String candealaipformnode = getCustomPropertyByName(CustomStencilConstants.PROPERTY_CANDEAL_AIPFORMNODE,processInstance,null);
			FlowStatus flowStatus = flowStatusMapper.selectFlowStatusByProcessInstanceId(processInstacneId);
			flowStatus.setFlowStatus(Constant.flow_claim);
			flowStatus.setOpid(userId);
			flowStatus.setOptime(new Date());
			flowStatus.setPostilMessage("接收成功");
			flowStatus.setCandealaipformnode(candealaipformnode);
			flowStatusMapper.insertFlowStatusHis(flowStatus);
			flowStatusMapper.updateFlowStatus(flowStatus);
			
			// 返回业务主键集合
			resultKey.add(processInstance.getBusinessKey());
		}
		logger.info("======================任务接收成功====================");
		return JsonResult.success(resultKey);
	}
	
	/*********************************************************************************************************************************************
	 * 任务退回组内
	 * 
	 * @param taskIds					当前任务ID,多个以英文逗号分隔
	 * @param userId					接收人编号userId
	 * @return
	 ********************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=60000,rollbackFor=Exception.class)
	public JsonResult<?> unClaimTask(String taskIds,String userId) throws Exception{
		
		logger.info("==============================任务退组操作==========================");
		if(StringUtils.isAnyEmpty(taskIds,userId)) {
			String errorInfo = "拒绝失败：缺少参数！";
			errorInfo  += "[taskIds="+taskIds;
			errorInfo  += ",userId="+userId + "]";
			logger.info(errorInfo);
			return JsonResult.failMessage(errorInfo);
		}
		List<String> resultKey = new ArrayList<String>();
		String[] taskIdsC = taskIds.split(",");
		for (String taskId : taskIdsC) {
			taskService.unclaim(taskId);
			ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
			String processInstacneId = processInstance.getId();
			/**更新状态*/
			FlowStatus flowStatus = flowStatusMapper.selectFlowStatusByProcessInstanceId(processInstacneId);
			flowStatus.setFlowStatus(Constant.flow_unclaim);
			flowStatus.setOpid(userId);
			flowStatus.setOptime(new Date());
			flowStatus.setPostilMessage("退组成功");
			flowStatusMapper.insertFlowStatusHis(flowStatus);
			flowStatusMapper.updateFlowStatus(flowStatus);
			resultKey.add(processInstance.getBusinessKey());
		}
		logger.info("======================任务退组成功=====================");
		return JsonResult.success(resultKey);
	}
	
	
	
//************************************************************核心方法**************************************************************************
	
	
	/********************************************************************************************************************************************
	 * 根据当前任务ID，查询可以退回的任务节点
	 * 
	 * @param taskId		当前任务ID
	 *******************************************************************************************************************************************/
	public List<ActivityImpl> findBackAvtivity(String taskId) throws Exception {
		List<ActivityImpl> rtnList = iteratorBackActivity(taskId, findActivitiImpl(taskId,null), new ArrayList<ActivityImpl>(),new ArrayList<ActivityImpl>());
		return reverList(rtnList);
	}
 
	/********************************************************************************************************************************************
	 * 审批通过(退回直接跳回功能需后续扩展)
	 * 
	 * @param taskId		当前任务ID
	 * @param variables		流程存储参数
	 * @throws Exception
	 *******************************************************************************************************************************************/
	public void passProcess(String taskId, Map<String, Object> variables)
			throws Exception {
		commitProcess(taskId, variables, null);
	}
 
	/********************************************************************************************************************************************
	 * 退回流程
	 * 
	 * @param taskId		当前任务ID
	 * @param activityId	退回节点ID
	 * @param variables		流程存储参数
	 * @throws Exception
	 *******************************************************************************************************************************************/
	public void backProcess(String taskId, String activityId,
			Map<String, Object> variables) throws Exception {
		if (StringUtils.isEmpty(activityId)) {
			throw new Exception("退回目标节点ID为空！");
		}
//		 // 查询本节点发起的会签任务，并结束  
//        List<Task> tasks = taskService.createTaskQuery().parentTaskId(taskId)  
//                .taskDescription("jointProcess").list();  
//        for (Task task : tasks) {  
//            commitProcess(task.getId(), null, null);  
//        }  
		// 查找所有并行任务节点，同时退回
		List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(
				taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());
		for (Task task : taskList) {
			commitProcess(task.getId(), variables, activityId);
		}
	}
 
	/********************************************************************************************************************************************
	 * 取回流程
	 * 
	 * @param taskId			当前任务ID
	 * @param activityId		取回节点ID
	 * @throws Exception
	 *******************************************************************************************************************************************/
	public void callBackProcess(String taskId, String activityId)
			throws Exception {
		if (StringUtils.isEmpty(activityId)) {
			throw new Exception("目标节点ID为空！");
		}
 
		// 查找所有并行任务节点，同时取回
		List<Task> taskList = findTaskListByKey(findProcessInstanceByTaskId(
				taskId).getId(), findTaskById(taskId).getTaskDefinitionKey());
		for (Task task : taskList) {
			commitProcess(task.getId(), null, activityId);
		}
	}
 
	/*******************************************************************************************************************************************
	 * 中止流程(特权人直接审批通过等)拒绝
	 * 
	 * @param taskId	当前任务ID
	 ******************************************************************************************************************************************/
	public void endProcess(String taskId,Map<String,Object> variables) throws Exception {
		ActivityImpl endActivity = findActivitiImpl(taskId, "end");
		commitProcess(taskId, variables, endActivity.getId());
	}
	
	/******************************************************************************************************************************************* 
     * 会签操作 
     *  
     * @param taskId 
     *            当前任务ID 
     * @param userCodes 
     *            会签人账号集合 
     * @throws Exception 
     ******************************************************************************************************************************************/  
    public void jointProcess(String taskId, List<String> userCodes)  
            throws Exception {  
        for (String userCode : userCodes) {  
            TaskEntity task = (TaskEntity) taskService.newTask(IDGenerator.getNextId());  
            task.setAssignee(userCode);  
            task.setName(findTaskById(taskId).getName() + "-会签");  
            task.setProcessDefinitionId(findProcessDefinitionEntityByTaskId(  
                    taskId).getId());  
            task.setProcessInstanceId(findProcessInstanceByTaskId(taskId)  
                    .getId());  
            task.setParentTaskId(taskId);  
            task.setDescription("jointProcess");  
            taskService.saveTask(task);  
        }  
    }  
 
	/******************************************************************************************************************************************
	 * 转办流程
	 * 
	 * @param taskId	当前任务节点ID
	 * @param userCode	被转办人Code
	 *****************************************************************************************************************************************/
	public void transferAssignee(String taskId, String userCode) {
		taskService.setAssignee(taskId, userCode);
	}
	
	//*****************************************************************************************************************************************
	//以下为流程转向操作核心逻辑
	
	/******************************************************************************************************************************************
	 * 流程流转
	 * 
	 * @param taskId		当前任务ID
	 * @param variables		流程变量
	 * @param activityId	流转目标（此参数为空，默认为提交操作）
	 * @throws Exception
	 ******************************************************************************************************************************************/
	private void commitProcess(String taskId, Map<String, Object> variables,
			String activityId) throws Exception {
		if (variables == null) {
			variables = new HashMap<String, Object>(16);
		}
		// 跳转节点为空，默认提交操作
		if (StringUtils.isEmpty(activityId)) {
			taskService.complete(taskId, variables);
		} else {// 流程转向操作
			turnTransition(taskId, activityId, variables);
		}
	}
 
	/******************************************************************************************************************************************
	 * 清空指定活动节点流向
	 * 
	 * @param activityImpl		活动节点
	 * @return 节点流向集合
	 ******************************************************************************************************************************************/
	private List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
		// 存储当前节点所有流向临时变量
		List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();
		// 获取当前节点所有流向，存储到临时变量，然后清空
		List<PvmTransition> pvmTransitionList = activityImpl
				.getOutgoingTransitions();
		for (PvmTransition pvmTransition : pvmTransitionList) {
			oriPvmTransitionList.add(pvmTransition);
		}
		pvmTransitionList.clear();
 
		return oriPvmTransitionList;
	}
 
	/********************************************************************************************************************************************
	 * 还原指定活动节点流向
	 * 
	 * @param activityImpl
	 *            活动节点
	 * @param oriPvmTransitionList
	 *            原有节点流向集合
	 *******************************************************************************************************************************************/
	private void restoreTransition(ActivityImpl activityImpl,
			List<PvmTransition> oriPvmTransitionList) {
		// 清空现有流向
		List<PvmTransition> pvmTransitionList = activityImpl
				.getOutgoingTransitions();
		pvmTransitionList.clear();
		// 还原以前流向
		for (PvmTransition pvmTransition : oriPvmTransitionList) {
			pvmTransitionList.add(pvmTransition);
		}
	}
 
	/********************************************************************************************************************************************
	 * 流程转向操作
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param activityId
	 *            目标节点任务ID
	 * @param variables
	 *            流程变量
	 * @throws Exception
	 *******************************************************************************************************************************************/
	private void turnTransition(String taskId, String activityId,
			Map<String, Object> variables) throws Exception {
		
		// 当前节点
		ActivityImpl currActivity = findActivitiImpl(taskId, null);
		
		// 清空当前流向
		List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);
 
		// 创建新流向
		TransitionImpl newTransition = currActivity.createOutgoingTransition();
		
		// 目标节点
		ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
		
		// 设置新流向的目标节点
		newTransition.setDestination(pointActivity);
 
		// 执行转向任务
		taskService.complete(taskId, variables);
		
		// 删除目标节点新流入
		pointActivity.getIncomingTransitions().remove(newTransition);
 
		// 还原以前流向
		restoreTransition(currActivity, oriPvmTransitionList);
	}
	
	
	
	/********************************************************************************************************************************************
	 * 迭代循环流程树结构，查询当前节点可退回的任务节点
	 * 
	 * @param taskId
	 *            当前任务ID
	 * @param currActivity
	 *            当前活动节点
	 * @param rtnList
	 *            存储回退节点集合
	 * @param tempList
	 *            临时存储节点集合（存储一次迭代过程中的同级userTask节点）
	 * @return 回退节点集合
	 *******************************************************************************************************************************************/
	private List<ActivityImpl> iteratorBackActivity(String taskId,
			ActivityImpl currActivity, List<ActivityImpl> rtnList,
			List<ActivityImpl> tempList) throws Exception {
		
		ActivityImpl currActivityFirst = findActivitiImpl(taskId,null);
		String type1 = (String) currActivity.getProperty("type");
		if(!(currActivity.getId()).equals(currActivityFirst.getId()) && type1.equals(userTask)) {
			return rtnList;
		}
		// 查询流程实例，生成流程树结构
		ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
		// 当前节点的流入来源
		List<PvmTransition> incomingTransitions = currActivity.getIncomingTransitions();
		// 条件分支节点集合，userTask节点遍历完毕，迭代遍历此集合，查询条件分支对应的userTask节点
		List<ActivityImpl> exclusiveGateways = new ArrayList<ActivityImpl>();
		// 并行节点集合，userTask节点遍历完毕，迭代遍历此集合，查询并行节点对应的userTask节点
		List<ActivityImpl> parallelGateways = new ArrayList<ActivityImpl>();
		// 遍历当前节点所有流入路径
		for (PvmTransition pvmTransition : incomingTransitions) {
			TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
			ActivityImpl activityImpl = transitionImpl.getSource();
			String type = (String) activityImpl.getProperty("type");
			/**
			 * 并行节点配置要求：
			 * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
			 */
			// 并行路线
			if ("parallelGateway".equals(type)) {
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId.lastIndexOf("_") + 1);
				// 并行起点，停止递归
				if ("START".equals(gatewayType.toUpperCase())) {
					return rtnList;
				} else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
					parallelGateways.add(activityImpl);
				}
			} else if ("startEvent".equals(type)) {
				// 开始节点，停止递归
				return rtnList;
			} else if ("userTask".equals(type)) {
				// 用户任务
				tempList.add(activityImpl);
			} else if ("exclusiveGateway".equals(type)) {
				// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
				currActivity = transitionImpl.getSource();
				exclusiveGateways.add(currActivity);
			}
		}
		/**迭代条件分支集合，查询对应的userTask节点*/
		for (ActivityImpl activityImpl : exclusiveGateways) {
			iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
		}
		/**迭代并行集合，查询对应的userTask节点*/
		for (ActivityImpl activityImpl : parallelGateways) {
			iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
		}
		/**
		 * 根据同级userTask集合，过滤最近发生的节点
		 */
		currActivity = filterNewestActivity(processInstance, tempList);
		if (currActivity != null) {
			// 查询当前节点的流向是否为并行终点，并获取并行起点ID
			String id = findParallelGatewayId(currActivity);
			if (StringUtils.isEmpty(id)) {
				// 并行起点ID为空，此节点流向不是并行终点，符合退回条件，存储此节点
				rtnList.add(currActivity);
			} else {
				// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
				currActivity = findActivitiImpl(taskId, id);
			}
			// 清空本次迭代临时集合
			tempList.clear();
			// 执行下次迭代
			iteratorBackActivity(taskId, currActivity, rtnList, tempList);
		}
		return rtnList;
	}
 
	/********************************************************************************************************************************************
	 * 反向排序list集合，便于退回节点按顺序显示
	 * 
	 * @param list
	 * @return
	 *******************************************************************************************************************************************/
	private List<ActivityImpl> reverList(List<ActivityImpl> list) {
		List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();
		// 由于迭代出现重复数据，排除重复
		for (int i = list.size(); i > 0; i--){
			if (!rtnList.contains(list.get(i - 1))){
				rtnList.add(list.get(i - 1));
			}
		}
		return rtnList;
	}
 
	/********************************************************************************************************************************************
	 * 根据当前节点，查询输出流向是否为并行终点，如果为并行终点，则拼装对应的并行起点ID
	 * 
	 * @param activityImpl
	 *            当前节点
	 * @return
	 *******************************************************************************************************************************************/
	private String findParallelGatewayId(ActivityImpl activityImpl) {
		List<PvmTransition> incomingTransitions = activityImpl
				.getOutgoingTransitions();
		for (PvmTransition pvmTransition : incomingTransitions) {
			TransitionImpl transitionImpl = (TransitionImpl) pvmTransition;
			activityImpl = transitionImpl.getDestination();
			String type = (String) activityImpl.getProperty("type");
			if ("parallelGateway".equals(type)) {
				// 并行路线
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId
						.lastIndexOf("_") + 1);
				if ("END".equals(gatewayType.toUpperCase())) {
					return gatewayId.substring(0, gatewayId.lastIndexOf("_"))
							+ "_start";
				}
			}
		}
		return null;
	}
 
	/********************************************************************************************************************************************
	 * 根据流入任务集合，查询最近一次的流入任务节点
	 * 
	 * @param processInstance
	 *            流程实例
	 * @param tempList
	 *            流入任务集合
	 * @return
	 *******************************************************************************************************************************************/
	private ActivityImpl filterNewestActivity(ProcessInstance processInstance,
			List<ActivityImpl> tempList) {
		while (tempList.size() > 0) {
			ActivityImpl activity1 = tempList.get(0);
			HistoricActivityInstance activityInstance1 = findHistoricUserTask(
					processInstance, activity1.getId());
			if (activityInstance1 == null) {
				tempList.remove(activity1);
				continue;
			}
 
			if (tempList.size() > 1) {
				ActivityImpl activity2 = tempList.get(1);
				HistoricActivityInstance activityInstance2 = findHistoricUserTask(
						processInstance, activity2.getId());
				if (activityInstance2 == null) {
					tempList.remove(activity2);
					continue;
				}
 
				if (activityInstance1.getEndTime().before(
						activityInstance2.getEndTime())) {
					tempList.remove(activity1);
				} else {
					tempList.remove(activity2);
				}
			} else {
				break;
			}
		}
		if (tempList.size() > 0) {
			return tempList.get(0);
		}
		return null;
	}
 
	/********************************************************************************************************************************************
	 * 查询指定任务节点的最新记录
	 * 
	 * @param processInstance
	 *            流程实例
	 * @param activityId
	 * @return
	 *******************************************************************************************************************************************/
	private HistoricActivityInstance findHistoricUserTask(
			ProcessInstance processInstance, String activityId) {
		HistoricActivityInstance rtnVal = null;
		// 查询当前流程实例审批结束的历史节点
		List<HistoricActivityInstance> historicActivityInstances = historyService
				.createHistoricActivityInstanceQuery().activityType("userTask")
				.processInstanceId(processInstance.getId()).activityId(
						activityId).finished()
				.orderByHistoricActivityInstanceEndTime().desc().list();
		if (historicActivityInstances.size() > 0) {
			rtnVal = historicActivityInstances.get(0);
		}
 
		return rtnVal;
	}
 
	
	/*********************************************************************************************************************************************
	 * 
	 * 以下为activiti 核心service
	 * 
	 ********************************************************************************************************************************************/
	public void setFormService(FormService formService) {
		this.formService = formService;
	}
 
	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}
 
	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}
 
	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}
 
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	//*******************************************************************************************************************************************
	//以下为根据 任务节点ID 获取流程各对象查询方法
	
	/********************************************************************************************************************************************
	 * 根据任务ID获得任务实例
	 * 
	 * @param taskId	任务ID
	 * @return
	 * @throws Exception
	 *******************************************************************************************************************************************/
	private TaskEntity findTaskById(String taskId) throws Exception {
		TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new Exception("任务实例未找到!");
		}
		return task;
	}
 
	/********************************************************************************************************************************************
	 * 根据流程实例ID和任务key值查询所有同级任务集合
	 * 
	 * @param processInstanceId  	流程实例ID
	 * @param key				 	taskDefinitionKey
	 * @return
	 *******************************************************************************************************************************************/
	private List<Task> findTaskListByKey(String processInstanceId, String key) {
		return taskService.createTaskQuery().processInstanceId(
				processInstanceId).taskDefinitionKey(key).list();
	}
 
	/********************************************************************************************************************************************
	 * 根据任务ID获取对应的流程实例
	 * 
	 * @param taskId	任务ID
	 * @return
	 * @throws Exception
	 *******************************************************************************************************************************************/
	public ProcessInstance findProcessInstanceByTaskId(String taskId)
			throws Exception {
		// 找到流程实例
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery().processInstanceId(
						findTaskById(taskId).getProcessInstanceId())
				.singleResult();
		if (processInstance == null) {
			throw new Exception("流程实例未找到!");
		}
		return processInstance;
	}
 
	/********************************************************************************************************************************************
	 * 根据任务ID获取流程定义
	 * 
	 * @param taskId		任务ID
	 * @throws Exception
	 *******************************************************************************************************************************************/
	private ProcessDefinitionEntity findProcessDefinitionEntityByTaskId(
			String taskId) throws Exception {
		// 取得流程定义
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService)
				.getDeployedProcessDefinition(findTaskById(taskId)
						.getProcessDefinitionId());
 
		if (processDefinition == null) {
			throw new Exception("流程定义未找到!");
		}
 
		return processDefinition;
	}
 
	/********************************************************************************************************************************************
	 * 根据任务ID和节点ID获取活动节点 <br>
	 * 
	 * @param taskId		任务ID
	 * @param activityId
	 *            活动节点ID 
	 *            如果为null或""，则默认查询当前活动节点 
	 *            如果为"end"，则查询结束节点 
	 * @return
	 * @throws Exception
	 *******************************************************************************************************************************************/
	private ActivityImpl findActivitiImpl(String taskId, String activityId)
			throws Exception {
		// 取得流程定义
		ProcessDefinitionEntity processDefinition = findProcessDefinitionEntityByTaskId(taskId);
 
		// 获取当前活动节点ID
		if (StringUtils.isEmpty(activityId)) {
			activityId = findTaskById(taskId).getTaskDefinitionKey();
		}
 
		// 根据流程定义，获取该流程实例的结束节点
		if (activityId.toUpperCase().equals(END)) {
			for (ActivityImpl activityImpl : processDefinition.getActivities()) {
				List<PvmTransition> pvmTransitionList = activityImpl
						.getOutgoingTransitions();
				if (pvmTransitionList.isEmpty()) {
					return activityImpl;
				}
			}
		}
 
		// 根据节点ID，获取对应的活动节点
		ActivityImpl activityImpl = ((ProcessDefinitionImpl) processDefinition)
				.findActivity(activityId);
 
		return activityImpl;
	}
	
	/********************************************************************************************************************************************
	 * 添加批注信息
	 * @param taskId			任务编号
	 * @param postilMessage		批注信息
	 * @throws Exception
	 *******************************************************************************************************************************************/
	private void addProcessPostil(String taskId,String postilMessage) throws Exception{
		String processInstanceId = "";
		ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
		processInstanceId = processInstance.getId();
		taskService.addComment(taskId, processInstanceId, postilMessage);
	}
	
	/********************************************************************************************************************************************
	 * 退回时尝试找到上一个办理人自动接收任务
	 * 
	 * @param activityId
	 * @param taskId
	 * @throws Exception
	 *******************************************************************************************************************************************/
	private Map<String,String> autoClaimTaskAfterBackTask(String activityId,String processInstanceId) throws Exception {
		//找到最后一个代理人自动接收
		List<HistoricActivityInstance> list = historyService
				.createHistoricActivityInstanceQuery()
				.activityId(activityId)
				.orderByHistoricActivityInstanceEndTime()
				.desc()
				.list();
		String lastAssignee = list.get(0).getAssignee();
		
		//找到退回操作后新的任务编号
		List<Task> tasks = taskService.createTaskQuery()
							.processInstanceId(processInstanceId)
							.orderByTaskCreateTime().desc()
							.list();
		Task lastTask = tasks.get(0);
		String lastTaskId = lastTask.getId();
		taskService.claim(lastTaskId, lastAssignee);
		Map<String,String> result = new HashMap<String,String>(16);
		result.put("laskTaskId", lastTaskId);
		result.put("lastAssignee", lastAssignee);
		return result;
	}
	
	/**
	 * 获取自定义扩展属性
	 * @param propertyName
	 * @param processInstance
	 * @return
	 * @throws Exception
	 */
	public String getCustomPropertyByName(String propertyName,ProcessInstance processInstance,String activityId) throws Exception{
		String elementValue = null;
		
		/**查询流程当前组编号和节点描述*/
		String processDefinitionId = processInstance.getProcessDefinitionId();
		
		if(activityId == null) {
			activityId = processInstance.getActivityId();
		}
		
		BpmnModel bmplModel = repositoryService.getBpmnModel(processDefinitionId);
		FlowElement flowElement = bmplModel.getProcesses().get(0).getFlowElement(activityId);
		
		// 获取扩展元素的信息
		Map<String, List<ExtensionElement>> extensionElements = flowElement
				.getExtensionElements();
		Iterator<Entry<String, List<ExtensionElement>>> it = extensionElements
				.entrySet().iterator();
		
		while (it.hasNext()) {
			Entry<String, List<ExtensionElement>> entry = it.next();
			// 获取根标签的名称
			String elementKey = entry.getKey();
			List<ExtensionElement> value = entry.getValue();
			if(propertyName.equals(elementKey)) {
				ExtensionElement e = value.get(0);
				elementValue = e.getElementText();
				if(StringUtils.isNotEmpty(elementValue)) {
					break;
				}
			}
			
		}
		
		return elementValue;
	}
	
	/**
	 * 判断下节点是否结束节点，只做同意操作时调用
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	private Boolean judgeNextIsOver(String taskId) throws Exception{
		ActivityImpl currActivity = findActivitiImpl(taskId, null);
		List<PvmTransition> outp = currActivity.getOutgoingTransitions();
		for (PvmTransition pvmTransition : outp) {
			PvmActivity ac = pvmTransition.getDestination();
			String nextType = (String)ac.getProperty("type");
			if("endEvent".equals(nextType)) {
				return true;
			}
		}
		return false;
	}
	
}
