package com.sinog2c.flow.domain;

import java.util.Date;

import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.rest.common.util.DateToStringSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/**
 * 历史流程变量转换类
* @ClassName:：HistoricVariableInstanceResponse 
* @Description： TODO
* @author ：gxx  
* @date ：2018年6月27日 下午4:39:39 
*
 */
public class HistoricVariableInstanceResponse {
	
	private String id;
	private String variableName;
	private Object value;
	private String processInstanceId;
	private String taskId;
	@JsonSerialize(using = DateToStringSerializer.class, as=Date.class)
	private Date createTime;
	@JsonSerialize(using = DateToStringSerializer.class, as=Date.class)
	private Date lastUpdatedTime;
	
	public HistoricVariableInstanceResponse (HistoricVariableInstance historicVariableInstance) {
		setId(historicVariableInstance.getId());
		setVariableName(historicVariableInstance.getVariableName());
		setValue(historicVariableInstance.getValue());
		setProcessInstanceId(historicVariableInstance.getProcessInstanceId());
		setTaskId(historicVariableInstance.getTaskId());
		setCreateTime(historicVariableInstance.getCreateTime());
		setLastUpdatedTime(historicVariableInstance.getLastUpdatedTime());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}
	
	
}
