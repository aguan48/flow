package com.sinog2c.flow.act;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import com.sinog2c.flow.domain.HistoricActivityInstanceResponse;

/**
 * Activiti 流程引擎相关工具类
 * 
* @ClassName:：ActivitiUtil 
* @Description： TODO
* @author ：gxx  
* @date ：2018年9月6日 上午10:39:17 
*
 */
public class ActivitiUtil {
	
	
	/*************************************************************************************************************************************
	 * 
	 * 解析List<Task>为List<Map<String,Object>>
	 * 参数：			list					任务集合
	 * 				runtimeService			流程引擎运行接口
	 * 
	 * 响应：			resultList				任务集合Map
	 * 
	 *************************************************************************************************************************************/
	public static List<Map<String,Object>> parseListTask2ListMap(List<Task> list,ProcessEngine processEngine){
		RuntimeService runtimeService = processEngine.getRuntimeService();/**提供流程文件和流程实例的方法 */
		
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for(Task task : list) {
			Map<String,Object> resultMap = new HashMap<String,Object>();
			resultMap.put("taskId", task.getId());//taskId
			resultMap.put("processInstanceId", task.getProcessInstanceId());//流程实例ID
			resultMap.put("createTime", task.getCreateTime());//创建时间
			resultMap.put("processVariables", task.getProcessVariables());//流程变量
			resultMap.put("assignee", task.getAssignee());//代理人
			resultMap.put("name", task.getName());//name
			resultMap.put("desctiption", task.getDescription());//描述
			resultMap.put("processDefinitionId", task.getProcessDefinitionId());//流程定义ID
			resultMap.put("delegationState", task.getDelegationState());//委派状态
			resultMap.put("tenantId", task.getTenantId());//所属系统标识
			//流程业务主键
			ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
			resultMap.put("businessKey", pi.getBusinessKey());//业务主键
			
			//解析了几个常用的，如果使用时缺少，可以在此补充
			
			resultList.add(resultMap);
		}
		return resultList;
	}
	
	/*************************************************************************************************************************************
	 * 
	 * 解析List<HistoricActivityInstance>为List<Map<String,Object>>
	 * 
	 * @param list
	 * @return
	 * 
	 ************************************************************************************************************************************/
	public static List<Map<String,Object>> parseListHistoricActivityInstance2ListMap(List<HistoricActivityInstance> list,
			ProcessEngine processEngine){
		
		TaskService taskService = processEngine.getTaskService();
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		String postilMessage = "";
		
		for (HistoricActivityInstance his : list) {
			// 本地类实体转换
			HistoricActivityInstanceResponse historicActivityInstance = new HistoricActivityInstanceResponse(his);
			
			Map<String,Object> resultMap = new HashMap<String,Object>();
			postilMessage = "";
			
			resultMap.put("id", historicActivityInstance.getId());
			resultMap.put("activityId", historicActivityInstance.getActivityId());// 活动ID
			resultMap.put("activityName", historicActivityInstance.getActivityName());	//活动名称		
			resultMap.put("activityType", historicActivityInstance.getActivityType());  //活动类型
			resultMap.put("assignee", historicActivityInstance.getAssignee());  //办理人
			resultMap.put("startTime", historicActivityInstance.getStartTime());  //开始时间
			resultMap.put("endTime", historicActivityInstance.getEndTime());  //结束时间
			resultMap.put("time", historicActivityInstance.getTime());  //time
			resultMap.put("tenantId", historicActivityInstance.getTenantId()); //tenantId所属系统租户
			resultMap.put("processInstanceId", historicActivityInstance.getProcessInstanceId()); // 流程实例
			//处理批注信息
			String taskId = historicActivityInstance.getTaskId();
			List<Comment> commentsList = taskService.getTaskComments(taskId);
			if(commentsList.size()>0) {
				for (Comment comment : commentsList) {
					postilMessage += comment.getFullMessage()+"。";
				}
			}			
			resultMap.put("postilMessage", postilMessage);
			
			resultList.add(resultMap);
		}
		return resultList;
	}
	
	/*************************************************************************************************************************************
	 * 
	 * 解析bootstarptable列表request的分页相关数据请求
	 * 
	 *************************************************************************************************************************************/
	public static Map<String,Object> dealQueryListParam(HttpServletRequest request){
		Map<String, Object> param = new HashMap<String, Object>();
		Integer limit = request.getParameter("limit") == null ? 20 : Integer.parseInt(request.getParameter("limit"));
		Integer offset = request.getParameter("offset") == null ? 0 : Integer.parseInt(request.getParameter("offset"));
		String sort = request.getParameter("sort") == null ? "" : request.getParameter("sort"); //排序字段
		String order = request.getParameter("order") == null ? "" : request.getParameter("order"); //排序方式
		String search = request.getParameter("search") == null ? "" : request.getParameter("search"); //排序方式
		param.put("offset", offset);
		param.put("limit", limit);
		param.put("sort", sort);
		param.put("order", order);
		param.put("search", search);
		return param;
	}
	
}
