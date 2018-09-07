package com.sinog2c.flow.act;

import org.activiti.editor.constants.StencilConstants;

/**
 * 流程编辑modeler扩展自定义属性
 * 
 * 【注意：】Activiti源码中限定扩展的属性名必须全小写字母
 * 
* @ClassName:：CustomStencilConstants 
* @Description： TODO
* @author ：gxx  
* @date ：2018年9月5日 下午1:50:22 
*
 */
public interface CustomStencilConstants extends StencilConstants{
	final String PROPERTY_CANDEAL_AIPFORMNODE = "candealaipformnode";//可以操作的Aip表单节点
}
