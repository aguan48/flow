package com.sinog2c.flow.domain;

import java.util.Date;

public class FlowStatus {
	
	public FlowStatus() {
		super();
	}

	private String processInstanceId;
	private String flowStatus;
	private String postilMessage;
	private String candealaipformnode;
	private String applyid;
	private String applyname;
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
	
	public String getCandealaipformnode() {
		return candealaipformnode;
	}
	public void setCandealaipformnode(String candealaipformnode) {
		this.candealaipformnode = candealaipformnode;
	}
	public String getApplyid() {
		return applyid;
	}
	public void setApplyid(String applyid) {
		this.applyid = applyid;
	}
	public String getApplyname() {
		return applyname;
	}
	public void setApplyname(String applyname) {
		this.applyname = applyname;
	}
	
	
	
	public FlowStatus(String processInstanceId, String flowStatus, String postilMessage, String candealaipformnode,
			String applyid, String applyname, String opid, Date optime) {
		super();
		this.processInstanceId = processInstanceId;
		this.flowStatus = flowStatus;
		this.postilMessage = postilMessage;
		this.candealaipformnode = candealaipformnode;
		this.applyid = applyid;
		this.applyname = applyname;
		this.opid = opid;
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
				+ postilMessage + ", candealaipformnode=" + candealaipformnode + ", applyid=" + applyid + ", applyname="
				+ applyname + ", opid=" + opid + ", optime=" + optime + "]";
	}
	
	
	
}
