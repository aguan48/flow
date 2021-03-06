package com.sinog2c.flow.act;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

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
	public static List<Map<String,Object>> parseListTask2ListMap(List<Task> list,ProcessEngine processEngine) throws Exception{
		/**提供流程文件和流程实例的方法 */
		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		for(Task task : list) {
			Map<String,Object> resultMap = new HashMap<String,Object>(16);
			// taskId
			resultMap.put("taskId", task.getId());
			// 流程实例ID
			resultMap.put("processInstanceId", task.getProcessInstanceId());
			// 创建时间
			resultMap.put("createTime", task.getCreateTime());
			// 流程变量
			resultMap.put("processVariables", task.getProcessVariables());
			// 代理人
			resultMap.put("assignee", task.getAssignee());
			//name
			resultMap.put("name", task.getName());
			//描述
			resultMap.put("desctiption", task.getDescription());
			//流程定义ID
			resultMap.put("processDefinitionId", task.getProcessDefinitionId());
			//委派状态
			resultMap.put("delegationState", task.getDelegationState());
			//所属系统标识
			resultMap.put("tenantId", task.getTenantId());
			//流程实例
			ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
			//业务主键
			resultMap.put("businessKey", processInstance.getBusinessKey());
			// 流程定义名称
			resultMap.put("processDefinitionName", processInstance.getProcessDefinitionName());
			// 当前节点可编辑节点
			String candealaipformnode = getCustomPropertyByName(CustomStencilConstants.PROPERTY_CANDEAL_AIPFORMNODE,processInstance,null,processEngine);
			resultMap.put("candealaipformnode", candealaipformnode);
			
			//解析了常用的，如果使用时缺少，可以在此补充
			
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
			
			Map<String,Object> resultMap = new HashMap<String,Object>(16);
			postilMessage = "";
			
			resultMap.put("id", historicActivityInstance.getId());
			// 活动ID
			resultMap.put("activityId", historicActivityInstance.getActivityId());
			//活动名称		
			resultMap.put("activityName", historicActivityInstance.getActivityName());
			//活动类型
			resultMap.put("activityType", historicActivityInstance.getActivityType()); 
			//办理人
			resultMap.put("assignee", historicActivityInstance.getAssignee()); 
			//开始时间
			resultMap.put("startTime", historicActivityInstance.getStartTime());  
			//结束时间
			resultMap.put("endTime", historicActivityInstance.getEndTime());
			//time
			resultMap.put("time", historicActivityInstance.getTime()); 
			//tenantId所属系统租户
			resultMap.put("tenantId", historicActivityInstance.getTenantId()); 
			// 流程实例
			resultMap.put("processInstanceId", historicActivityInstance.getProcessInstanceId()); 
			//处理批注信息
			String taskId = historicActivityInstance.getTaskId();
			List<Comment> commentsList = taskService.getTaskComments(taskId);
			if(commentsList.size()>0) {
				for (Comment comment : commentsList) {
					postilMessage += comment.getFullMessage();
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
		Map<String, Object> param = new HashMap<String, Object>(16);
		Integer limit = request.getParameter("limit") == null ? 20 : Integer.parseInt(request.getParameter("limit"));
		Integer offset = request.getParameter("offset") == null ? 0 : Integer.parseInt(request.getParameter("offset"));
		//排序字段
		String sort = request.getParameter("sort") == null ? "" : request.getParameter("sort"); 
		//排序方式
		String order = request.getParameter("order") == null ? "" : request.getParameter("order");
		//排序方式
		String search = request.getParameter("search") == null ? "" : request.getParameter("search"); 
		param.put("offset", offset);
		param.put("limit", limit);
		param.put("sort", sort);
		param.put("order", order);
		param.put("search", search);
		return param;
	}
	
	
	/**
	 * 获取自定义扩展属性
	 * @param propertyName
	 * @param processInstance
	 * @return
	 * @throws Exception
	 */
	public static String getCustomPropertyByName(String propertyName,ProcessInstance processInstance,String activityId,ProcessEngine processEngine) throws Exception{
		String elementValue = null;
		
		RepositoryService repositoryService = processEngine.getRepositoryService();
		
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
	
}
