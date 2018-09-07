package com.sinog2c.flow.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sinog2c.flow.service.FlowProcessCoreService;
import com.sinog2c.flow.service.FlowProcessQueryService;
import com.sinog2c.flow.util.JsonResult;

/**
 * 流程扭转控制，此类对外暴露，用于对外提供接口
 * @author 许杰
 *
 */
@RestController
@RequestMapping("/flowService")
public class FlowServiceController {
	
	private static final Logger logger = Logger.getLogger(FlowServiceController.class);
	
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
	private FlowProcessCoreService flowProcessCoreService;
	
	@Autowired
	private FlowProcessQueryService flowProcessQueryService;
	
	//启动（开启一个流程）
	@PostMapping("/startFlowProcess")
	public JsonResult<?> startFlowProcess(@RequestParam(name = "processDefinitionKey",required = true) String processDefinitionKey,
			@RequestParam(name = "tenantId",required = true) String tenantId,
			@RequestParam(name = "businessKey",required = true) String businessKey,
			@RequestBody(required = false) Map<String,Object> variables) {
		JsonResult<?> result = JsonResult.failMessage("启动流程异常");
		try {
			result = flowProcessCoreService.startFlowProcess(processDefinitionKey, tenantId, variables, businessKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//通过（提交）
	@PostMapping("/passFlowProcess")
	public JsonResult<?> passFlowProcess(@RequestParam(name = "taskId",required = true) String taskId,
			@RequestParam(name = "postilMessage",required = false) String postilMessage,
			@RequestBody(required = false) Map<String,Object> variables) {
		JsonResult<?> result = JsonResult.failMessage("提交流程异常");
		try {
			result = flowProcessCoreService.passFlowProcess(taskId, postilMessage, variables);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	//拒绝（结束）
	@PostMapping("/refuseFlowProcess")
	public JsonResult<?> refuseFlowProcess(@RequestParam(name = "taskId",required = true) String taskId,
			@RequestParam(name = "postilMessage",required = false) String postilMessage,
			@RequestBody(required = false) Map<String,Object> variables){
		JsonResult<?> result= JsonResult.failMessage("启动流程异常");
		try {
			result = flowProcessCoreService.refuseFlowProcess(taskId, postilMessage, variables);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	//退回（驳回）
	@PostMapping("/backFlowProcess")
	public JsonResult<?> backFlowProcess(@RequestParam(name = "taskId",required = true) String taskId,
			@RequestParam(name = "postilMessage",required = false) String postilMessage,
			@RequestBody(required = false) Map<String,Object> variables) {
		try {
			return flowProcessCoreService.backFlowProcess(taskId, postilMessage, variables);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return JsonResult.failMessage("流程退回异常");
		}
	}
	
	//任务取回
	@PostMapping("/claimTask")
	public JsonResult<?> claimTask(@RequestParam(name = "taskId",required = true) String taskId,
			@RequestParam(name = "userId",required = true) String userId){
		try {
			return flowProcessCoreService.claimTask(taskId, userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return JsonResult.failMessage("任务取回接口调用异常");
		}
	}
	
	//任务放回组内
	@PostMapping("/unClaimTask")
	public JsonResult<?> unClaimTask(@RequestParam(name = "taskId",required = true) String taskId){
		try {
			return flowProcessCoreService.unClaimTask(taskId);
		} catch (Exception e) {
			e.printStackTrace();
			return JsonResult.failMessage("任务放回组内异常");
		}
	}
	
	//获取个人任务全部流程全部集合
	@PostMapping("/getAllPersonalTaskListByAssignee")
	public JsonResult<List<Map<String,Object>>> getPersonalTaskListByAssignee(@RequestParam(name = "userId",required = true) String userId) {
		JsonResult<List<Map<String,Object>>> result = flowProcessQueryService.getPersonalTaskListByAssignee(userId);
		return result;
	}
	
	//根据流程定义key获取个人任务集合
	@PostMapping("/getPersonalTaskListByAssignee")
	public JsonResult<List<Map<String,Object>>> getPersonalTaskListByAssignee(@RequestParam(name = "userId",required = true) String userId,
			@RequestParam(name = "processDefinitionKey",required = true) String processDefinitionKey) {
		JsonResult<List<Map<String,Object>>> result = flowProcessQueryService.getPersonalTaskListByAssignee(userId, processDefinitionKey);
		return result;
	}
	
	//获取组任务全部流程全部集合
	@PostMapping("/getAllGroupTaskListByGroupId")
	public JsonResult<List<Map<String,Object>>> getGroupTaskListByGroupId(@RequestParam(name = "groupId",required = true) String groupId) {
		JsonResult<List<Map<String,Object>>> result = flowProcessQueryService.getGroupTaskListByGroupId(groupId);
		return result;
	}
	
	//根据流程定义Key获取组任务集合
	@PostMapping("/getGroupTaskListByGroupId")
	public JsonResult<List<Map<String,Object>>> getGroupTaskListByGroupId(@RequestParam(name = "groupId",required = true) String groupId,
			@RequestParam(name = "processDefinitionKey",required = true) String processDefinitionKey) {
		JsonResult<List<Map<String,Object>>> result = flowProcessQueryService.getGroupTaskListByGroupId(groupId, processDefinitionKey);
		return result;
	}
	
	//根据userId和系统代码Id获取用户所有流程的个人任务
	@PostMapping("/getAllPersonalTaskListByAssigneeAndTenantId")
	public JsonResult<List<Map<String,Object>>> getPersonalTaskListByAssigneeAndTenantId(@RequestParam(name = "userId",required = true) String userId,
			@RequestParam(name = "tenantId",required = true) String tenantId) {
		JsonResult<List<Map<String,Object>>> result = flowProcessQueryService.getPersonalTaskListByAssigneeAndTenantId(userId,tenantId);
		return result;
	}
	
	//根据userId和系统代码Id获取用户本系统processDefinitionKey流程的组任务
	@PostMapping("/getPersonalTaskListByAssigneeAndTenantId")
	public JsonResult<List<Map<String,Object>>> getPersonalTaskListByAssigneeAndTenantId(@RequestParam(name = "userId",required = true) String userId,
			@RequestParam(name = "tenantId",required = true) String tenantId,
			@RequestParam(name = "processDefinitionKey",required = true) String processDefinitionKey) {
		JsonResult<List<Map<String,Object>>> result = flowProcessQueryService.getPersonalTaskListByAssigneeAndTenantId(userId,tenantId, processDefinitionKey);
		return result;
	}
	
	//根据userId和系统代码Id获取用户本系统所在的组所有流程的组任务
	@PostMapping("/getAllGroupTaskListByAssigneeAndTenantId")
	public JsonResult<List<Map<String,Object>>> getGroupTaskListByAssigneeAndTenantId(@RequestParam(name = "userId",required = true) String userId,
			@RequestParam(name = "tenantId",required = true) String tenantId) {
		JsonResult<List<Map<String,Object>>> result = flowProcessQueryService.getGroupTaskListByAssigneeAndTenantId(userId,tenantId);
		return result;
	}
	
	//根据userId和系统代码Id获取用户本系统所在的组processDefinitionKey流程的组任务
	@PostMapping("/getGroupTaskListByAssigneeAndTenantId")
	public JsonResult<List<Map<String,Object>>> getGroupTaskListByAssigneeAndTenantId(@RequestParam(name = "userId",required = true) String userId,
			@RequestParam(name = "tenantId",required = true) String tenantId,
			@RequestParam(name = "processDefinitionKey",required = true) String processDefinitionKey) {
		JsonResult<List<Map<String,Object>>> result = flowProcessQueryService.getGroupTaskListByAssigneeAndTenantId(userId, tenantId, processDefinitionKey);
		return result;
	}
	
	//根据流程实例Id：processInstanceId获取历史活动
	@PostMapping("/getHisActivityByProcessInstanceId")
	public JsonResult<List<Map<String,Object>>> getHisActivityByProcessInstanceId(@RequestParam(name = "processInstanceId",required = true) String processInstanceId){
		JsonResult<List<Map<String,Object>>> result = flowProcessQueryService.getHisActivityByProcessInstanceId(processInstanceId);
		return result;
	}
	
	
	/**
	 * 流程实时流转标记图
	 * 请求：
	 * processId				: 流程实例id
	 * 响应：
	 * 图片流
	 * 
	 */
	@RequestMapping("/genProcessDiagramPic")
	public void getProcessDiagramPic(HttpServletResponse httpServletResponse, String processInstanceId)throws Exception {
		
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        //流程走完的不显示图
        if (pi == null) {
            return;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        String InstanceId = task.getProcessInstanceId();
        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .processInstanceId(InstanceId)
                .list();

        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0);
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
	}
	
	
	
}
