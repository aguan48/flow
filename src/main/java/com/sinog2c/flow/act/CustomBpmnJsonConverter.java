package com.sinog2c.flow.act;

import java.util.Map;

import org.activiti.bpmn.model.BaseElement;
import org.activiti.editor.language.json.converter.BaseBpmnJsonConverter;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;

/**
 * 通过继承BpmnJsonConverter，放开对两个属性的访问
 * 
* @ClassName:：CustomBpmnJsonConverter 
* @Description： TODO
* @author ：gxx  
* @date ：2018年9月5日 下午7:26:33 
*
 */
public class CustomBpmnJsonConverter extends BpmnJsonConverter{
	
	//通过继承开放convertersToJsonMap的访问
    public static Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> getConvertersToJsonMap(){
        return convertersToJsonMap;
    }

    //通过继承开放convertersToBpmnMap的访问
    public static Map<String, Class<? extends BaseBpmnJsonConverter>> getConvertersToBpmnMap(){
        return convertersToBpmnMap;
    }
	
}
