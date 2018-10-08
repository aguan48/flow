package com.sinog2c.flow.domain;

import java.util.Date;

/**
 * 流程流转实例记录
* @ClassName:：FlowProcInstRec 
* @Description： TODO
* @author ：gxx  
* @date ：2018年9月21日 下午5:20:15 
*
 */
public class FlowProcInstRec {
	
	private String id;// 主键
	private String processInstanceId;// 流程实例id
	private String processDefinitionKey;// 流程定义key
	private String businessKey;// 业务主键
	private String userId;// 任务办理人
	private String startUserId;// 流程发起人
	private String taskId;// 任务编号
	private String node;// 节点
	private String nodeDetail;// 节点描述 
	private String nodeName;// 节点名称
	private String flowStatus;// 流程状态
	private String postilMessage;// 批注
	private Date opTime;// 操作时间
	private String candealaipformnode;// 可编辑节点 
	private String tenantId;// 系统标识
	private String sn;// 排序
	
	public FlowProcInstRec() {
		super();
	}
	

	public FlowProcInstRec(String id, String processInstanceId, String processDefinitionKey, String businessKey,
			String userId, String startUserId, String taskId, String node, String nodeDetail, String nodeName,
			String flowStatus, String postilMessage, Date opTime, String candealaipformnode, String tenantId,
			String sn) {
		super();
		this.id = id;
		this.processInstanceId = processInstanceId;
		this.processDefinitionKey = processDefinitionKey;
		this.businessKey = businessKey;
		this.userId = userId;
		this.startUserId = startUserId;
		this.taskId = taskId;
		this.node = node;
		this.nodeDetail = nodeDetail;
		this.nodeName = nodeName;
		this.flowStatus = flowStatus;
		this.postilMessage = postilMessage;
		this.opTime = opTime;
		this.candealaipformnode = candealaipformnode;
		this.tenantId = tenantId;
		this.sn = sn;
	}


	@Override
	public String toString() {
		return "FlowProcInstRec [id=" + id + ", processInstanceId=" + processInstanceId + ", processDefinitionKey="
				+ processDefinitionKey + ", businessKey=" + businessKey + ", userId=" + userId + ", startUserId="
				+ startUserId + ", taskId=" + taskId + ", node=" + node + ", nodeDetail=" + nodeDetail + ", nodeName="
				+ nodeName + ", flowStatus=" + flowStatus + ", postilMessage=" + postilMessage + ", opTime=" + opTime
				+ ", candealaipformnode=" + candealaipformnode + ", tenantId=" + tenantId + ", sn=" + sn + "]";
	}


	public String getNode() {
		return node;
	}


	public void setNode(String node) {
		this.node = node;
	}


	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProcessInstanceId() {
		return processInstanceId;
	}
	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	public String getProcessDefinitionKey() {
		return processDefinitionKey;
	}
	public void setProcessDefinitionKey(String processDefinitionKey) {
		this.processDefinitionKey = processDefinitionKey;
	}
	public String getBusinessKey() {
		return businessKey;
	}
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getStartUserId() {
		return startUserId;
	}
	public void setStartUserId(String startUserId) {
		this.startUserId = startUserId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getNodeDetail() {
		return nodeDetail;
	}
	public void setNodeDetail(String nodeDetail) {
		this.nodeDetail = nodeDetail;
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
	public Date getOpTime() {
		return opTime;
	}
	public void setOpTime(Date opTime) {
		this.opTime = opTime;
	}
	public String getCandealaipformnode() {
		return candealaipformnode;
	}
	public void setCandealaipformnode(String candealaipformnode) {
		this.candealaipformnode = candealaipformnode;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	
	




	




	


	
	
	
}
