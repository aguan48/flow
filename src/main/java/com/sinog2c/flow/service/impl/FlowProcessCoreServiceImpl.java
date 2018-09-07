package com.sinog2c.flow.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sinog2c.flow.service.FlowProcessCoreService;
import com.sinog2c.flow.util.JsonResult;

/**
 * Activiti流程引擎【流程流转】核心服务
 * 
* @ClassName:：ActivitiCoreServiceImpl 
* @Description： TODO
* @author ：gxx  
* @date ：2018年8月28日 上午11:47:17 
*
 */

@Service("flowProcessCoreService")
public class FlowProcessCoreServiceImpl implements FlowProcessCoreService {
		
	private static final Logger logger = Logger.getLogger(FlowProcessCoreServiceImpl.class);
	
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
	
	/**表单*/
	@Autowired
	private FormService formService;
	
	
	
	
	/*********************************************************************************************************************************************
	 * 启动一个流程
	 * 
	 * @param processDefinitionKey		流程定义key
	 * @param tenantId					系统ID
	 * @param variables					流程变量
	 * @param businessKey				业务主键
	 * @return
	 ********************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
	public JsonResult<String> startFlowProcess(String processDefinitionKey,String tenantId, Map<String, Object> variables,String businessKey) 
			throws Exception{
		// TODO Auto-generated method stub
		logger.info("======================开始启动一个流程：：：processDefinitionKey"+processDefinitionKey+"=====================");
		if(StringUtils.isEmpty(processDefinitionKey)) {
			logger.error("======================启动失败，流程定义key不能为空：：：processDefinitionKey=====================");
			return JsonResult.failMessage("流程定义不能为空！");
		}else if(StringUtils.isEmpty(tenantId)){
			logger.error("======================启动失败，系统tenantId不能为空：：：processDefinitionKey=====================");
			return JsonResult.failMessage("系统tenantId不能为空！");
		}else {
			/**根据流程定义ID启动流程*/
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKeyAndTenantId(processDefinitionKey, businessKey, variables, tenantId);
			String processInstanceId = processInstance.getId();
			logger.info("======================启动成功：：：processDefinitionKey"+processDefinitionKey+"=====================");
			return JsonResult.success(processInstanceId);
		}
	}
	
	/********************************************************************************************************************************************
	 * 流程退回操作
	 * 此方法不支持会签回退！！！
	 * 
	 * @param taskId					当前任务ID
	 * @param variables					流程变量
	 * @return
	 * @throws Exception 
	 *******************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
	public JsonResult<?> backFlowProcess(String taskId,String postilMessage,Map<String,Object> variables) throws Exception{
		// TODO Auto-generated method stub
		logger.info("==============================[流程退回操作]方法名：backFlowProcess[开始执行]==========================");
		if( StringUtils.isEmpty(taskId) ) {
			logger.error("==============================[流程退回操作]方法名：backFlowProcess[taskId不能为空]==========================");
			return JsonResult.failMessage("当前任务ID不能为空！");
		}
		// 批注信息
		if(StringUtils.isEmpty(postilMessage)) {
			postilMessage = "退回";
		}
		// 流程变量为空，初始化
		if( variables == null ) {
			variables = new HashMap<String,Object>();
		}
		//添加批注信息
		addProcessPostil(taskId,postilMessage);
		Task task = this.findTaskById(taskId);
		String processInstanceId = task.getProcessInstanceId();
		//获取可以退回的的任务节点
		List<ActivityImpl> activityList = this.findBackAvtivity(taskId);
		if( activityList == null || !(activityList.size() > 0 )) {
			logger.error("==============================[流程退回操作]方法名：backFlowProcess[没有检测到可退回的节点！]==========================");
			return JsonResult.failMessage("没有检测到可退回的节点！");
		}
		//遍历退回
		for (ActivityImpl activityImpl : activityList) {
			String activityId = activityImpl.getId();
			// 退回
			this.backProcess(taskId, activityId, variables);
			// 自动接收
			autoClaimTaskAfterBackTask(activityId,processInstanceId);
		}
		logger.info("==============================[流程退回操作]方法名：backFlowProcess[退回成功]==========================");
		return JsonResult.successMessage("退回成功");
	}
	
	/********************************************************************************************************************************************
	 * 流程提交操作
	 * 
	 * @param taskId					当前任务ID
	 * @param variables					流程变量
	 * @return
	 * @throws Exception 
	 *******************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
	public JsonResult<?> passFlowProcess(String taskId,String postilMessage, Map<String, Object> variables) throws Exception {
		// TODO Auto-generated method stub
		logger.info("==============================[流程提交操作]方法名：passFlowProcess[开始执行]==========================");
		if( StringUtils.isEmpty(taskId) ) {
			logger.error("==============================[流程提交操作]方法名：passFlowProcess[taskId不能为空]==========================");
			return JsonResult.failMessage("当前任务ID不能为空！");
		}
		// 批注信息
		if(StringUtils.isEmpty(postilMessage)) {
			postilMessage = "同意";
		}
		// 流程变量为空，初始化
		if( variables == null ) {
			variables = new HashMap<String,Object>();
		}
		//添加批注信息
		addProcessPostil(taskId,postilMessage);
		this.passProcess(taskId, variables);			
		logger.info("==============================[流程提交操作]方法名：passFlowProcess[提交成功]==========================");
		return JsonResult.successMessage("提交成功");
	}
	
	/*********************************************************************************************************************************************
	 * 
	 * 流程拒绝操作（直接结束END）
	 * 
	 * @param taskId					当前任务ID
	 * @param variables					流程变量
	 * @return
	 * @throws Exception 
	 * 
	 ********************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
	public JsonResult<?> refuseFlowProcess(String taskId,String postilMessage, Map<String, Object> variables) throws Exception {
		// TODO Auto-generated method stub
		logger.info("==============================[流程拒绝（结束）操作]方法名：refuseFlowProcess[开始执行]==========================");
		if( StringUtils.isEmpty(taskId) ) {
			logger.error("==============================[流程拒绝（结束）操作]方法名：refuseFlowProcess[taskId不能为空]==========================");
			return JsonResult.failMessage("当前任务ID不能为空！");
		}
		// 批注信息
		if(StringUtils.isEmpty(postilMessage)) {
			postilMessage = "拒绝";
		}
		//流程变量为空，初始化
		if( variables == null ) {
			variables = new HashMap<String,Object>();
		}
		//添加批注
		addProcessPostil(taskId,postilMessage);
		//结束流程
		this.endProcess(taskId,variables);
		logger.info("==============================[流程拒绝（结束）操作]方法名：refuseFlowProcess[拒绝（结束）成功]==========================");
		return JsonResult.successMessage("拒绝（结束）成功");
	}
	
	/*********************************************************************************************************************************************
	 * 任务接收
	 * 
	 * @param taskId					当前任务ID
	 * @param userId					接收人编号userId
	 * @return
	 ********************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
	public JsonResult<String> claimTask(String taskId, String userId) throws Exception{
		// TODO Auto-generated method stub
		logger.info("======================任务接收：：：用户userId：：：userId"+userId+"===任务编号taskId：：："+taskId+"=====================");
		if(StringUtils.isEmpty(taskId)||StringUtils.isEmpty(userId)) {
			return JsonResult.failMessage("userId和taskId  均不能为空");
		}
		taskService.claim(taskId, userId);
		logger.info("======================任务接收成功：：：用户userId：：：userId"+userId+"===任务编号taskId：：："+taskId+"=====================");
		return JsonResult.success();
	}
	
	/*********************************************************************************************************************************************
	 * 任务退回组内
	 * 
	 * @param taskId					当前任务ID
	 * @param userId					接收人编号userId
	 * @return
	 ********************************************************************************************************************************************/
	@Override
	@Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout=36000,rollbackFor=Exception.class)
	public JsonResult<String> unClaimTask(String taskId) throws Exception{
		// TODO Auto-generated method stub
		logger.info("======================任务退回组内：：：：任务编号taskId：：："+taskId+"=====================");
		if(StringUtils.isEmpty(taskId)) {
			return JsonResult.failMessage("userId不能为空");
		}
		taskService.unclaim(taskId);
		logger.info("======================任务接收成功：：：任务编号taskId：：："+taskId+"=====================");
		return JsonResult.success();
	}
	
	
	
	
	
	
	
	
	//*******************************************************************************************************************************************
	//************************************************************核心方法**************************************************************************
	//*******************************************************************************************************************************************
	
	
	/********************************************************************************************************************************************
	 * 根据当前任务ID，查询可以驳回的任务节点
	 * 
	 * @param taskId		当前任务ID
	 *******************************************************************************************************************************************/
	public List<ActivityImpl> findBackAvtivity(String taskId) throws Exception {
		List<ActivityImpl> rtnList = iteratorBackActivity(taskId, findActivitiImpl(taskId,null), new ArrayList<ActivityImpl>(),new ArrayList<ActivityImpl>());
		return reverList(rtnList);
	}
 
	/********************************************************************************************************************************************
	 * 审批通过(驳回直接跳回功能需后续扩展)
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
	 * 驳回流程
	 * 
	 * @param taskId		当前任务ID
	 * @param activityId	驳回节点ID
	 * @param variables		流程存储参数
	 * @throws Exception
	 *******************************************************************************************************************************************/
	public void backProcess(String taskId, String activityId,
			Map<String, Object> variables) throws Exception {
		if (StringUtils.isEmpty(activityId)) {
			throw new Exception("驳回目标节点ID为空！");
		}
		// 查找所有并行任务节点，同时驳回
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
			variables = new HashMap<String, Object>();
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
	 * 迭代循环流程树结构，查询当前节点可驳回的任务节点
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
		// 查询流程定义，生成流程树结构
		ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
 
		// 当前节点的流入来源
		List<PvmTransition> incomingTransitions = currActivity
				.getIncomingTransitions();
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
			 * 并行节点配置要求：<br>
			 * 必须成对出现，且要求分别配置节点ID为:XXX_start(开始)，XXX_end(结束)
			 */
			if ("parallelGateway".equals(type)) {// 并行路线
				String gatewayId = activityImpl.getId();
				String gatewayType = gatewayId.substring(gatewayId
						.lastIndexOf("_") + 1);
				if ("START".equals(gatewayType.toUpperCase())) {// 并行起点，停止递归
					return rtnList;
				} else {// 并行终点，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
					parallelGateways.add(activityImpl);
				}
			} else if ("startEvent".equals(type)) {// 开始节点，停止递归
				return rtnList;
			} else if ("userTask".equals(type)) {// 用户任务
				tempList.add(activityImpl);
			} else if ("exclusiveGateway".equals(type)) {// 分支路线，临时存储此节点，本次循环结束，迭代集合，查询对应的userTask节点
				currActivity = transitionImpl.getSource();
				exclusiveGateways.add(currActivity);
			}
		}
 
		/**
		 * 迭代条件分支集合，查询对应的userTask节点
		 */
		for (ActivityImpl activityImpl : exclusiveGateways) {
			iteratorBackActivity(taskId, activityImpl, rtnList, tempList);
		}
 
		/**
		 * 迭代并行集合，查询对应的userTask节点
		 */
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
			if (StringUtils.isEmpty(id)) {// 并行起点ID为空，此节点流向不是并行终点，符合驳回条件，存储此节点
				rtnList.add(currActivity);
			} else {// 根据并行起点ID查询当前节点，然后迭代查询其对应的userTask任务节点
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
	 * 反向排序list集合，便于驳回节点按顺序显示
	 * 
	 * @param list
	 * @return
	 *******************************************************************************************************************************************/
	private List<ActivityImpl> reverList(List<ActivityImpl> list) {
		List<ActivityImpl> rtnList = new ArrayList<ActivityImpl>();
		// 由于迭代出现重复数据，排除重复
		for (int i = list.size(); i > 0; i--) {
			if (!rtnList.contains(list.get(i - 1)))
				rtnList.add(list.get(i - 1));
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
			if ("parallelGateway".equals(type)) {// 并行路线
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
			ActivityImpl activity_1 = tempList.get(0);
			HistoricActivityInstance activityInstance_1 = findHistoricUserTask(
					processInstance, activity_1.getId());
			if (activityInstance_1 == null) {
				tempList.remove(activity_1);
				continue;
			}
 
			if (tempList.size() > 1) {
				ActivityImpl activity_2 = tempList.get(1);
				HistoricActivityInstance activityInstance_2 = findHistoricUserTask(
						processInstance, activity_2.getId());
				if (activityInstance_2 == null) {
					tempList.remove(activity_2);
					continue;
				}
 
				if (activityInstance_1.getEndTime().before(
						activityInstance_2.getEndTime())) {
					tempList.remove(activity_1);
				} else {
					tempList.remove(activity_2);
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
	private ProcessInstance findProcessInstanceByTaskId(String taskId)
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
		if (activityId.toUpperCase().equals("END")) {
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
	
	/**
	 * 添加批注信息
	 * @param taskId			任务编号
	 * @param postilMessage		批注信息
	 * @throws Exception
	 */
	private void addProcessPostil(String taskId,String postilMessage) throws Exception{
		String processInstanceId = "";
		ProcessInstance processInstance = findProcessInstanceByTaskId(taskId);
		processInstanceId = processInstance.getId();
		taskService.addComment(taskId, processInstanceId, postilMessage);
	}
	
	/**
	 * 退回时尝试找到上一个办理人自动接收任务
	 * 
	 * @param activityId
	 * @param taskId
	 * @throws Exception
	 */
	private void autoClaimTaskAfterBackTask(String activityId,String processInstanceId) throws Exception {
		//找到最后一个代理人自动接收，不知道有没有问题，先试试  0-0
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
	}



	

	
}
