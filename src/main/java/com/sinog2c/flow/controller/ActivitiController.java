package com.sinog2c.flow.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sinog2c.flow.util.DataJsonResult;
import com.sinog2c.flow.util.Result;

/**
 * 流程控制层
 * @author 许杰
 *
 */
@RestController
@RequestMapping("/activiti")
public class ActivitiController extends BaseController{

	@Autowired
	private RepositoryService repositoryService;
	
	//模型列表
	@RequestMapping(value="/modelList")
	private ModelAndView modelList(HttpServletRequest request) {
		return new ModelAndView("modelList");
	}
	
	//编辑模型
	@RequestMapping(value = "/editFlow")  
	public void editFlow(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("id") String id) {
		try {
			if(StringUtils.isNotEmpty(id)) {
				//通过id查找
				Model modelData = repositoryService.getModel(id);
				if(modelData != null) {
					response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());
				}
			}
		} catch (Exception e) {
			
		}
		
	}
	

	/**
	 * 查询
	 * @return
	 */
	@RequestMapping(value="/selectAll")
	@ResponseBody
	public DataJsonResult selectAll(HttpServletRequest request){
		DataJsonResult json = new DataJsonResult(false, "获取模型列表失败!");
		try {
			Integer limit = request.getParameter("limit") == null ? 20 : Integer.parseInt(request.getParameter("limit"));
			Integer offset = request.getParameter("offset") == null ? 0 : Integer.parseInt(request.getParameter("offset"));
			String sort = request.getParameter("sort") == null ? "" : request.getParameter("sort"); //排序字段
			String order = request.getParameter("order") == null ? "" : request.getParameter("order"); //排序方式
			String search = request.getParameter("search") == null ? "" : request.getParameter("search"); //排序方式
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("offset", offset);
			param.put("limit", limit);
			param.put("sort", sort);
			param.put("order", order);
			param.put("search", search);
			ModelQuery query = repositoryService.createModelQuery();
			List<Model> resultList = getModelList(query, param);
			Long total = query.count();
			json.setRows(resultList);//数据
			json.setTotal(total);//总记录数
			json.setSuccess(true);
			json.setMessage("获取模型列表成功!");
		} catch (Exception e) {

		}
		return json;
	}

	/**
	 * 批量删除
	 * @param ids
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteByIds")
	@ResponseBody
	public Result deleteByIds(String ids,HttpServletRequest request) {
		Result result = new Result(false, "删除失败!");
		try {
			if(StringUtils.isNotEmpty(ids)) {
				String[] idStr = ids.split(",");
				for(String id : idStr){
					repositoryService.deleteModel(id);
				}
			}
			result.setSuccess(true);
			result.setMessage("删除成功!");
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	/** 
	 * 新增模型
	 * @return 
	 */  
	@PostMapping(value = "/create")  
	public Result getEditor(
			@RequestParam("description") String description,
			@RequestParam("name") String name,
			@RequestParam("key") String key,
			HttpServletRequest request, HttpServletResponse response){  
		Result json = new Result(false, "新增模型失败！");
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode editorNode = objectMapper.createObjectNode();
			editorNode.put("id", "canvas");
			editorNode.put("resourceId", "canvas");
			ObjectNode stencilSetNode = objectMapper.createObjectNode();
			stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
			editorNode.put("stencilset", stencilSetNode);
			Model modelData = repositoryService.newModel();
			ObjectNode modelObjectNode = objectMapper.createObjectNode();
			modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
			modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
			description = StringUtils.defaultString(description);
			modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);

			modelData.setMetaInfo(modelObjectNode.toString());
			modelData.setName(name);
			modelData.setKey(StringUtils.defaultString(key));

			repositoryService.saveModel(modelData);
			repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
			json.setSuccess(true);
			json.setMessage("创建模型成功！！");
			json.setObj(modelData.getId());
		} catch (Exception e) {
			System.out.println("创建模型失败");
			json.setSuccess(false);
			json.setMessage("创建模型失败！");
		}
		return json;
	} 

	/**
	 * 部署
	 */
	@PostMapping(value = "/deploy")
	@ResponseBody
	public Result deploy(@RequestParam("modelId") String modelId, HttpServletRequest request) {
		Result result = new Result(false, "部署流程失败！");
		try {
			Model modelData = repositoryService.getModel(modelId);
			byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
			if(bytes == null) {
				result.setMessage("模型数据为空，请先设计流程并成功保存，再进行发布。");
			}else {
				ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(bytes);
				byte[] bpmnBytes = null;
				BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
				if(model.getProcesses().size()==0){
		            result.setMessage("数据模型不符要求，请至少设计一条主线流程。");
		        }else {
		        	bpmnBytes = new BpmnXMLConverter().convertToXML(model);
					String processName = modelData.getName() + ".bpmn20.xml";
					Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes,"utf-8")).deploy();
					result.setSuccess(true);
					result.setObj(deployment.getId());
					result.setMessage("部署流程成功！");
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 导出model的xml文件
	 */
	@RequestMapping(value = "/export/{modelId}")
	public Result export(@PathVariable("modelId") String modelId, HttpServletResponse response) {
		Result result = new Result(false, "导出模型失败");
		response.setCharacterEncoding("UTF-8");  
		response.setContentType("application/json; charset=utf-8");  
		try {
			Model modelData = repositoryService.getModel(modelId);
			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			//获取节点信息
			byte[] arg0 = repositoryService.getModelEditorSource(modelData.getId());
			if(arg0 != null) {
				JsonNode editorNode = new ObjectMapper().readTree(arg0);
				//将节点信息转换为xml
				BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
				if(bpmnModel.getProcesses().size()==0){
		            result.setMessage("数据模型不符要求，请至少设计一条主线流程。");
		        } else {
		        	BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
					byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

					ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
					IOUtils.copy(in, response.getOutputStream());
					String filename = modelData.getName() + ".bpmn20.xml";
					response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(filename, "UTF-8"));
					response.flushBuffer();
					result.setSuccess(true);
					result.setMessage("导出模型成功");
		        }
			} else {
				result.setMessage("模型数据为空，请先设计流程并成功保存，再进行导出。");
			}
			
		} catch (Exception e){
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			out.write("未找到对应数据");
			e.printStackTrace();
		}
		return result;
	}


}
