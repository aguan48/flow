package com.sinog2c.flow.domain;

import java.util.Date;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.rest.common.util.DateToStringSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/**
 * 历史流程任务转换类
* @ClassName:：HistoricTaskInstanceResponse 
* @Description： TODO
* @author ：gxx  
* @date ：2018年6月27日 下午4:39:18 
*
 */
public class HistoricTaskInstanceResponse {
	
	private String Id;
	private String assignee;
	private String processDefinitionId;
	private String processInstanceId;
	private String excutionId;
	private String deleteReason;
	@JsonSerialize(using = DateToStringSerializer.class, as=Date.class)
	private Date startTime;
	@JsonSerialize(using = DateToStringSerializer.class, as=Date.class)
	private Date endTime;
	private long durationInmillis;
	private long workTimeInMillis;
	@JsonSerialize(using = DateToStringSerializer.class, as=Date.class)
	private Date claimTime;
	
	
	public HistoricTaskInstanceResponse(HistoricTaskInstance historicTaskInstance) {
		setDeleteReason(historicTaskInstance.getDeleteReason() == null ? "":historicTaskInstance.getDeleteReason());
		if(historicTaskInstance.getStartTime() != null) {
			setStartTime(historicTaskInstance.getStartTime());
		}
		if(historicTaskInstance.getEndTime() != null) {
			setEndTime(historicTaskInstance.getEndTime());
		}
		if(historicTaskInstance.getDurationInMillis() != null) {
			setDurationInmillis(historicTaskInstance.getDurationInMillis());
		}
		if(historicTaskInstance.getWorkTimeInMillis() != null) {
			setWorkTimeInMillis(historicTaskInstance.getWorkTimeInMillis());
		}
		if(historicTaskInstance.getClaimTime() != null) {
			setClaimTime(historicTaskInstance.getClaimTime());
		}
		setAssignee(historicTaskInstance.getAssignee());
		setExcutionId(historicTaskInstance.getExecutionId());
		setProcessDefinitionId(historicTaskInstance.getProcessDefinitionId());
		setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
		setId(historicTaskInstance.getId());
	}
	
	
	
	public String getDeleteReason() {
		return deleteReason;
	}
	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
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
	public long getDurationInmillis() {
		return durationInmillis;
	}
	public void setDurationInmillis(long durationInmillis) {
		this.durationInmillis = durationInmillis;
	}
	public long getWorkTimeInMillis() {
		return workTimeInMillis;
	}
	public void setWorkTimeInMillis(long workTimeInMillis) {
		this.workTimeInMillis = workTimeInMillis;
	}
	public Date getClaimTime() {
		return claimTime;
	}
	public void setClaimTime(Date claimTime) {
		this.claimTime = claimTime;
	}
	public String getId() {
		return Id;
	}



	public void setId(String id) {
		Id = id;
	}



	public String getAssignee() {
		return assignee;
	}



	public void setAssignee(String assignee) {
		this.assignee = assignee;
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
		return excutionId;
	}



	public void setExcutionId(String excutionId) {
		this.excutionId = excutionId;
	}
	
	
	
	
	
	
	  
}
