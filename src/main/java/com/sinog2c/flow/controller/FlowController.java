package com.sinog2c.flow.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinog2c.flow.util.Result;

/**
 * 流程扭转控制，此类对外暴露，用于对外提供接口
 * @author 许杰
 *
 */
@RestController
@RequestMapping("/flow")
public class FlowController {
	
	private static final Logger logger = Logger.getLogger(FlowController.class);
	
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
	
	
	/**
	 * 启动一个流程---根据流程定义id
	 * 请求 ：
	 * variableMap 			： 流程变量集合
	 * processDefinitionId	： 流程定义Id
	 * 响应：
	 * Obj			： 启动成功返回启动流程实例ID--processInstance.getId()
	 */
	@RequestMapping("/startProcess")
	public Result startProcess(Map<String , Object> variableMap,String processDefinitionKey) {
		Result result = new Result(false, "启动流程失败!");
		int i = 1;
		variableMap.put("v1", i);
		variableMap.put("userId", "2222");
		if(StringUtils.isEmpty(processDefinitionKey)) {
			result.setMessage("流程定义ID不能为空！");
		}else {
			try {
				/**根据流程定义ID启动流程*/
				ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variableMap);
				result.setObj(processInstance.getId());
				result.setMessage("启动流程成功！");
				result.setSuccess(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * 获取个人任务列表---根据用户ID
	 * 请求 ：
	 * userId				： 用户编号ID
	 * 响应：
	 * Obj->tasksList		： 个人任务列表
	 */
	@RequestMapping("/getTaskListByAssignee")
	public Result getTaskListByAssignee(String userId) {
		Result result = new Result(false, "获取个人任务列表失败!");
		if(StringUtils.isEmpty(userId)) {
			result.setMessage("用户编号不能为空！");
		}else {
			try {
				/**调用流程任务服务接口，根据个人userId查询个人任务 */
				List tasksList = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
				System.out.println(userId+"个人任务创建时间倒序："+tasksList.toString());
				result.setObj(tasksList);
				result.setMessage("获取个人任务列表成功");
				result.setSuccess(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
		
	}
	
	
	/**
	 * 获取组任务列表---根据组ID
	 * 请求 ：
	 * groupId				： 组ID
	 * 响应：
	 * Obj->tasksList		： 组任务列表
	 */
	@RequestMapping("/getGroupTaskListByUser")
	public Result getGroupTaskListByUser(String groupId) {
		Result result = new Result(false, "获取组任务列表失败!");
		if(StringUtils.isEmpty(groupId)) {
			result.setMessage("组编号不能为空！");
		}else {
			try {
				/**调用流程服务接口，根据groupId查询组任务*/
				List<Task> tasksList = taskService.createTaskQuery().taskCandidateGroup(groupId).orderByTaskCreateTime().desc().list();
				System.out.println(groupId+"组任务集合创建时间倒序："+tasksList.toString());
				result.setObj(tasksList);
				result.setMessage("获取组任务列表成功");
				result.setSuccess(true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	
	
	/**
	 * 组任务分配接收
	 * 请求：
	 * userId				： 用户编号
	 * taskId				： 任务编号
	 */
	public Result claimGroupTask(String userId,String taskId) {
		Result result = new Result(false, "组任务接收失败!");
		if(StringUtils.isEmpty(taskId)||StringUtils.isEmpty(userId)) {
			result.setMessage("任务id或用户id不能为空！");
			return result;
		}
		try {
			taskService.claim(taskId, userId);
			result.setMessage("组任务分配接收成功！");
			result.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	/**
	 * 提交操作
	 * 请求：
	 * taskId				： 任务编号id
	 * variableMap			：流程变量集合
	 * 响应：
	 */
	@RequestMapping("/processFlow")
	public Result processFlow(String taskId, 
			Map<String, Object> variableMap) {
		Result result = new Result(false, "流程流转失败!");
		if(StringUtils.isEmpty(taskId)) {
			result.setMessage("任务ID不能为空！请求失败");
			return result;
		}
		if(variableMap == null) {
			variableMap = new HashMap();
		}
		try {
			taskService.complete(taskId, variableMap);
			result.setMessage("提交成功！");
			result.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
		
	}
	
	/**
	 * 退回操作
	 * 请求：
	 * taskId			： 任务id
	 * 响应：
	 * 
	 */
	@RequestMapping("/taskRollBack")
	public Result taskRollBack(String taskId) {
		Result result = new Result(false, "流程流转失败!");
		if(StringUtils.isEmpty(taskId)) {
			result.setMessage("任务ID不能为空！请求失败");
			return result;
		}
		try {
			/**根据任务编号取得当前任务*/  
			HistoricTaskInstance currTask = historyService  
			        .createHistoricTaskInstanceQuery().taskId(taskId)  
			        .singleResult();
			/**取得流程实例*/
			ProcessInstance instance = runtimeService  
			        .createProcessInstanceQuery()  
			        .processInstanceId(currTask.getProcessInstanceId())  
			        .singleResult();
			if(instance == null) {
				result.setMessage("当前流程已经结束！请求失败");
				return result;
			}
			/**取得流程变量*/
			Map<String, Object> variablesMap = instance.getProcessVariables();  
			/** 取得流程定义  */
			ProcessDefinitionEntity definition = (ProcessDefinitionEntity) (repositoryService.getProcessDefinition(currTask.getProcessDefinitionId()));  	
			if (definition == null) {  
				result.setMessage("流程定义未找到！请求失败");
				return result; 
			}
			/** 取得上一步活动  */
			ActivityImpl currActivity = ((ProcessDefinitionImpl) definition).findActivity(currTask.getTaskDefinitionKey());  
			List<PvmTransition> nextTransitionList = currActivity.getIncomingTransitions(); 
			/** 清除当前活动的出口  */
			List<PvmTransition> oriPvmTransitionList = new ArrayList<PvmTransition>();  
			List<PvmTransition> pvmTransitionList = currActivity  
			        .getOutgoingTransitions();  
			for (PvmTransition pvmTransition : pvmTransitionList) {  
			    oriPvmTransitionList.add(pvmTransition);  
			}  
			pvmTransitionList.clear(); 
			/** 建立新出口  */
			List<TransitionImpl> newTransitions = new ArrayList<TransitionImpl>();  
			for (PvmTransition nextTransition : nextTransitionList) {  
			    PvmActivity nextActivity = nextTransition.getSource();  
			    ActivityImpl nextActivityImpl = ((ProcessDefinitionImpl) definition)  
			            .findActivity(nextActivity.getId());  
			    TransitionImpl newTransition = currActivity  
			            .createOutgoingTransition();  
			    newTransition.setDestination(nextActivityImpl);  
			    newTransitions.add(newTransition);  
			}  
			/** 完成任务  */
			List<Task> tasks = taskService.createTaskQuery()  
			        .processInstanceId(instance.getId())  
			        .taskDefinitionKey(currTask.getTaskDefinitionKey()).list();  
			for (Task task : tasks) {  
			    taskService.complete(task.getId(), variablesMap);  
			    historyService.deleteHistoricTaskInstance(task.getId());  
			}  
			/** 恢复方向  */
			for (TransitionImpl transitionImpl : newTransitions) {  
			    currActivity.getOutgoingTransitions().remove(transitionImpl);  
			}  
			for (PvmTransition pvmTransition : oriPvmTransitionList) {  
			    pvmTransitionList.add(pvmTransition);  
			}
			result.setMessage("任务回退成功！");
			result.setSuccess(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
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
	public void genProcessDiagramPic(HttpServletResponse httpServletResponse, String processId)throws Exception {
		
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();

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
