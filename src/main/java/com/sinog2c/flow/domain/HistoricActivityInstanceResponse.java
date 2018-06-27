package com.sinog2c.flow.domain;

import java.util.Date;

import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.rest.common.util.DateToStringSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 历史活动转换类
* @ClassName:：HistoricActivityInstanceResponse 
* @Description： TODO
* @author ：gxx  
* @date ：2018年6月27日 下午4:38:35 
*
 */
public class HistoricActivityInstanceResponse {
	
	private String id;
	private String activityId;
	private String activityName;
	private String processDefinitionId;
	private String processInstanceId;
	private String ExcutionId;
	private String taskId;
	private String calledProcessInstanceId;
	private String assignee;
	@JsonSerialize(using = DateToStringSerializer.class, as=Date.class)
	private Date startTime;
	@JsonSerialize(using = DateToStringSerializer.class, as=Date.class)
	private Date endTime;
	private long durationInMillis;
	
	public HistoricActivityInstanceResponse(HistoricActivityInstance historicActivityInstance) {
		setId(historicActivityInstance.getId() == null ? "":historicActivityInstance.getId());
		setActivityId(historicActivityInstance.getActivityId() == null ? "":historicActivityInstance.getActivityId());
		setActivityName(historicActivityInstance.getActivityName() == null ? "":historicActivityInstance.getActivityName());
		setProcessDefinitionId(historicActivityInstance.getProcessDefinitionId() == null ? "":historicActivityInstance.getProcessDefinitionId());
		setProcessInstanceId(historicActivityInstance.getProcessInstanceId() == null ? "":historicActivityInstance.getProcessInstanceId());
		setExcutionId(historicActivityInstance.getExecutionId() == null ? "":historicActivityInstance.getExecutionId());
		setTaskId(historicActivityInstance.getTaskId() == null ? "":historicActivityInstance.getTaskId());
		setCalledProcessInstanceId(historicActivityInstance.getCalledProcessInstanceId() == null ? "":historicActivityInstance.getCalledProcessInstanceId());
		setAssignee(historicActivityInstance.getAssignee() == null ? "":historicActivityInstance.getAssignee());
		if(historicActivityInstance.getStartTime() != null) {
			setStartTime(historicActivityInstance.getStartTime());
		}
		if(historicActivityInstance.getEndTime() != null) {
			setEndTime(historicActivityInstance.getEndTime());
		}
		if(historicActivityInstance.getDurationInMillis() != null) {
			setDurationInMillis(historicActivityInstance.getDurationInMillis());
		}
		
		
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getExcutionId() {
		return ExcutionId;
	}

	public void setExcutionId(String excutionId) {
		ExcutionId = excutionId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getCalledProcessInstanceId() {
		return calledProcessInstanceId;
	}

	public void setCalledProcessInstanceId(String calledProcessInstanceId) {
		this.calledProcessInstanceId = calledProcessInstanceId;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public long getDurationInMillis() {
		return durationInMillis;
	}

	public void setDurationInMillis(long durationInMillis) {
		this.durationInMillis = durationInMillis;
	}

	
	
	
}
