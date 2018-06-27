package com.sinog2c.flow.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.activiti.engine.repository.Deployment;
import org.activiti.rest.common.util.DateToStringSerializer;

import java.util.Date;
/**
 * 流程发布信息转换类
* @ClassName:：DeploymentResponse 
* @Description： TODO
* @author ：gxx  
* @date ：2018年6月27日 下午4:39:58 
*
 */
public class DeploymentResponse {

  private String id;
  private String name;
  @JsonSerialize(using = DateToStringSerializer.class, as=Date.class)
  private Date deploymentTime;
  private String category;
  private String tenantId;
  
  public DeploymentResponse(Deployment deployment) {
    setId(deployment.getId() == null ? "": deployment.getId());
    setName(deployment.getName() == null ? "": deployment.getName());
    if(deployment.getDeploymentTime() != null) {
    	setDeploymentTime(deployment.getDeploymentTime());
    }
    setCategory(deployment.getCategory() == null ? "": deployment.getCategory());
    setTenantId(deployment.getTenantId() == null ? "": deployment.getTenantId());
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
  public  Date getDeploymentTime() {
    return deploymentTime;
  }
  public void setDeploymentTime( Date deploymentTime) {
    this.deploymentTime = deploymentTime;
  }
  public String getCategory() {
    return category;
  }
  public void setCategory(String category) {
    this.category = category;
  }
  public void setTenantId(String tenantId) {
      this.tenantId = tenantId;
  }
  public String getTenantId() {
	  return tenantId;
  }
}