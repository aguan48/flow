package com.sinog2c.flow.domain;

import org.activiti.engine.repository.ProcessDefinition;
/**
 * 流程定义转换类
* @ClassName:：ProcessDefinitionResponse 
* @Description： TODO
* @author ：gxx  
* @date ：2018年6月27日 下午4:40:18 
*
 */
public class ProcessDefinitionResponse {
	
	  private String id;
	  private String category;
	  private String name;
	  private String key;
	  private String description;
	  private int version;
	  private String resourceName;
	  private String deploymentId;
	  private String diagramResourceName;
	  private boolean hasStartFormKey;
	  private boolean hasGraphicalNotation;
	  private String tenantId;
	 
	  public ProcessDefinitionResponse(ProcessDefinition processDefinition) {
	    setId(processDefinition.getId() == null ? "":processDefinition.getId());
	    setName(processDefinition.getName() == null ? "":processDefinition.getName());
	    setCategory(processDefinition.getCategory() == null ? "":processDefinition.getCategory());
	    setTenantId(processDefinition.getTenantId() == null ? "":processDefinition.getTenantId());
	    setDescription(processDefinition.getDescription() == null ? "":processDefinition.getDescription());
	    setDeploymentId(processDefinition.getDeploymentId() == null ? "":processDefinition.getDeploymentId());
	    setVersion(processDefinition.getVersion());
	    setResourceName(processDefinition.getResourceName() == null ? "":processDefinition.getResourceName());
	    setDiagramResourceName(processDefinition.getDiagramResourceName() == null ? "":processDefinition.getDiagramResourceName());
	    setKey(processDefinition.getKey() == null ? "":processDefinition.getKey());
	  }  
	  
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getDeploymentId() {
		return deploymentId;
	}
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}
	public String getDiagramResourceName() {
		return diagramResourceName;
	}
	public void setDiagramResourceName(String diagramResourceName) {
		this.diagramResourceName = diagramResourceName;
	}
	public boolean isHasStartFormKey() {
		return hasStartFormKey;
	}
	public void setHasStartFormKey(boolean hasStartFormKey) {
		this.hasStartFormKey = hasStartFormKey;
	}
	public boolean isHasGraphicalNotation() {
		return hasGraphicalNotation;
	}
	public void setHasGraphicalNotation(boolean hasGraphicalNotation) {
		this.hasGraphicalNotation = hasGraphicalNotation;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
}
