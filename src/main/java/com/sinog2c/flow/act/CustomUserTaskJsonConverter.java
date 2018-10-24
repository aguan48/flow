package com.sinog2c.flow.act;

import java.util.Map;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.bpmn.model.CustomProperty;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.activiti.editor.language.json.converter.UserTaskJsonConverter;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 重写UserTaskJsonConverter方法，完成对自定义属性的扩展
 * 
* @ClassName:：CustomUserTaskJsonConverter 
* @Description： TODO
* @author ：gxx  
* @date ：2018年9月5日 下午7:27:30 
*
 */
public class CustomUserTaskJsonConverter extends UserTaskJsonConverter implements CustomStencilConstants{
	
	private static final Logger logger = Logger.getLogger(CustomUserTaskJsonConverter.class);
	
	@Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        super.convertElementToJson(propertiesNode, baseElement);
        logger.info(baseElement.toString());
        
    }
    
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        FlowElement flowElement = super.convertJsonToElement(elementNode,modelNode, shapeMap);
        
        logger.info("convertJsonToElement-elementNode-[编辑器表单节点、属性数据]:::"+elementNode.toString());
        //  将自己的属性添加到activiti自带的自定义属性中
        UserTask userTask = (UserTask) flowElement;
        CustomProperty customProperty = new CustomProperty();
        
        /**==============================================扩展属性在此增加====================================================*/
        
        // 扩展属性：可以处理的Aip表单节点【candealaipformnode】
        customProperty.setName(PROPERTY_CANDEAL_AIPFORMNODE);
        customProperty.setSimpleValue(this.getPropertyValueAsString(PROPERTY_CANDEAL_AIPFORMNODE, elementNode));
        
        /**=============================================================================================================*/
        // 放入自定义属性中
        userTask.getCustomProperties().add(customProperty);
        
        return userTask;
    }
    
    public static void fillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap,
            Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }
  
    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_USER, CustomUserTaskJsonConverter.class);
    }
  
    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, CustomUserTaskJsonConverter.class);
    }
    
}
