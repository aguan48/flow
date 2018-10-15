package com.sinog2c.flow.domain;

import java.util.Date;

public class FlowStatus {
	
	public FlowStatus() {
		super();
	}

	private String processInstanceId;
	private String flowStatus;
	private String postilMessage;
	private String opid;
	private Date optime;
	
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getFlowStatus() {
		return flowStatus;
	}
	public void setFlowStatus(String flowStatus) {
		this.flowStatus = flowStatus;
	}
	public String getPostilMessage() {
		return postilMessage;
	}
	public void setPostilMessage(String postilMessage) {
		this.postilMessage = postilMessage;
	}
	public String getOpid() {
		return opid;
	}
	public void setOpid(String opid) {
		this.opid = opid;
	}
	public Date getOptime() {
		return optime;
	}
	public void setOptime(Date optime) {
		this.optime = optime;
	}
	
	public FlowStatus(String processInstanceId, String flowStatus, String postilMessage, String opid, Date optime) {
		super();
		this.processInstanceId = processInstanceId;
		this.flowStatus = flowStatus;
		this.postilMessage = postilMessage;
		this.opid = opid;
		this.optime = optime;
	}
	@Override
	public String toString() {
		return "FlowStatus [processInstanceId=" + processInstanceId + ", flowStatus=" + flowStatus + ", postilMessage="
				+ postilMessage + ", opid=" + opid + ", optime=" + optime + "]";
	}
	
	
	
}
