package com.sinog2c.flow.act;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.impl.bpmn.parser.BpmnParse;
import org.activiti.engine.impl.bpmn.parser.handler.UserTaskParseHandler;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

/**
 * 扩展属性解析器
* @ClassName:：ExtensionElementsUserTaskParseHandler 
* @Description： TODO
* @author ：gxx  
* @date ：2018年10月24日 上午10:13:02 
*
 */
public class ExtensionElementsUserTaskParseHandler extends UserTaskParseHandler {
	
	public static final String PROPERTY_EXTENSION_ELEMENTS = "extensionElements";
	
	/**
	 * 解析执行
	 */
	@Override
    protected void executeParse(BpmnParse bpmnParse, UserTask userTask) {
        //调用上层的解析
        super.executeParse(bpmnParse, userTask);
 
        ActivityImpl activity = bpmnParse.getCurrentScope().findActivity(userTask.getId());
        Map<String, String> operationMap = parseUserTaskOperations(bpmnParse, userTask);
 
        //将扩展属性设置给activity
        activity.setProperty(PROPERTY_EXTENSION_ELEMENTS, operationMap);
    }
	
	/**
	 * 解析用户任务
	 * @param bpmnParse
	 * @param userTask
	 * @return
	 */
    public Map<String, String> parseUserTaskOperations(BpmnParse bpmnParse, UserTask userTask) {
        Map<String, String> operationMap = new HashMap<String, String>(16);
        //获取扩展属性标签元素
        List<ExtensionElement> operationsElement = userTask.getExtensionElements().get(PROPERTY_EXTENSION_ELEMENTS);
        
        if (operationsElement != null) {
            for (ExtensionElement operationElement : operationsElement) {
 
                operationMap.put(operationElement.getName(), operationElement.getElementText());
            }
        }
 
        return operationMap;
    }

}