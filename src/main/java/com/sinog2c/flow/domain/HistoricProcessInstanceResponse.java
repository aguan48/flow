package com.sinog2c.flow.domain;

import java.util.Date;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.rest.common.util.DateToStringSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * 历史流程实例转换类
* @ClassName:：HistoricProcessInstanceResponse 
* @Description： TODO
* @author ：gxx  
* @date ：2018年6月27日 下午4:39:00 
*
 */
public class HistoricProcessInstanceResponse {
	
	private String id;
	private String name;
	private String decription;
	private String bussinessKey;
	private String processDefinitionId;
	private String processDefinitionName;
	private String processDefintiionKey;
	private Integer processDefinitionVersion;
	private String deploymentId;
	@JsonSerialize(using = DateToStringSerializer.class, as=Date.class)
	private Date startTime;
	@JsonSerialize(using = DateToStringSerializer.class, as=Date.class)
	private Date endTime;
	private long durationInMillis;
	private String startUserId;
	private String startActivityId;
	private String endActivitiId;
	private String deleteReason;
	private String tenantId;
	
	public HistoricProcessInstanceResponse(HistoricProcessInstance historicProcessInstance) {
		setId(historicProcessInstance.getId() == null ? "":historicProcessInstance.getId());
		setName(historicProcessInstance.getName() == null ? "":historicProcessInstance.getName());
		setDecription(historicProcessInstance.getDescription() == null ? "":historicProcessInstance.getDescription());
		setBussinessKey(historicProcessInstance.getBusinessKey() == null ? "":historicProcessInstance.getBusinessKey());
		setProcessDefinitionId(historicProcessInstance.getProcessDefinitionId() == null ? "":historicProcessInstance.getProcessDefinitionId());
		setProcessDefinitionName(historicProcessInstance.getProcessDefinitionName() == null ? "": historicProcessInstance.getProcessDefinitionName());
		setProcessDefintiionKey(historicProcessInstance.getProcessDefinitionKey() == null ? "":historicProcessInstance.getProcessDefinitionKey() );
		if(historicProcessInstance.getProcessDefinitionVersion() != null) {
			setProcessDefinitionVersion(historicProcessInstance.getProcessDefinitionVersion());
		}	
		setDeploymentId(historicProcessInstance.getDeploymentId() == null ? "":historicProcessInstance.getDeploymentId());
		if(historicProcessInstance.getStartTime() != null) {
			setStartTime(historicProcessInstance.getStartTime());
		}
		if(historicProcessInstance.getEndTime() != null) {
			setEndTime(historicProcessInstance.getEndTime());
		}
		if(historicProcessInstance.getDurationInMillis() != null) {
			setDurationInMillis(historicProcessInstance.getDurationInMillis());
		}
		setStartUserId(historicProcessInstance.getStartUserId() == null ? "":historicProcessInstance.getStartUserId());
		setDeleteReason(historicProcessInstance.getDeleteReason() == null ? "":historicProcessInstance.getDeleteReason());
		setTenantId(historicProcessInstance.getTenantId() == null ? "":historicProcessInstance.getTenantId());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDecription() {
		return decription;
	}

	public void setDecription(String decription) {
		this.decription = decription;
	}

	public String getBussinessKey() {
		return bussinessKey;
	}

	public void setBussinessKey(String bussinessKey) {
		this.bussinessKey = bussinessKey;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public String getProcessDefinitionName() {
		return processDefinitionName;
	}

	public void setProcessDefinitionName(String processDefinitionName) {
		this.processDefinitionName = processDefinitionName;
	}

	public String getProcessDefintiionKey() {
		return processDefintiionKey;
	}

	public void setProcessDefintiionKey(String processDefintiionKey) {
		this.processDefintiionKey = processDefintiionKey;
	}

	public Integer getProcessDefinitionVersion() {
		return processDefinitionVersion;
	}

	public void setProcessDefinitionVersion(Integer processDefinitionVersion) {
		this.processDefinitionVersion = processDefinitionVersion;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
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

	public String getStartUserId() {
		return startUserId;
	}

	public void setStartUserId(String startUserId) {
		this.startUserId = startUserId;
	}

	public String getStartActivityId() {
		return startActivityId;
	}

	public void setStartActivityId(String startActivityId) {
		this.startActivityId = startActivityId;
	}

	public String getEndActivitiId() {
		return endActivitiId;
	}

	public void setEndActivitiId(String endActivitiId) {
		this.endActivitiId = endActivitiId;
	}

	public String getDeleteReason() {
		return deleteReason;
	}

	public void setDeleteReason(String deleteReason) {
		this.deleteReason = deleteReason;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	
	
}
