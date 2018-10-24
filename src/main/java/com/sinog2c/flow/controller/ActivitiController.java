package com.sinog2c.flow.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.UserTask;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sinog2c.flow.act.ActivitiUtil;
import com.sinog2c.flow.act.CustomBpmnJsonConverter;
import com.sinog2c.flow.act.CustomUserTaskJsonConverter;
import com.sinog2c.flow.domain.HistoricProcessInstanceResponse;
import com.sinog2c.flow.domain.HistoricTaskInstanceResponse;
import com.sinog2c.flow.domain.HistoricVariableInstanceResponse;
import com.sinog2c.flow.domain.ProcessDefinitionResponse;
import com.sinog2c.flow.service.CommonFlowQueryListService;
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
	
	private static final Logger logger = Logger.getLogger(ActivitiController.class);
	
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private CommonFlowQueryListService commonFlowQueryListService;
	@Autowired
	private HistoryService historyService;
	
	//模型列表
	@RequestMapping(value="/modelList")
	private ModelAndView modelList(HttpServletRequest request) {
		return new ModelAndView("modelList");
	}
	
	//流程定义列表
	@RequestMapping(value="/deployList")
	private ModelAndView deployList(HttpServletRequest request) {
		return new ModelAndView("deployList");
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
			logger.error(e.getMessage());
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
			List<Model> resultList = commonFlowQueryListService.getModelList(query, param);
			Long total = query.count();
			json.setRows(resultList);//数据
			json.setTotal(total);//总记录数
			json.setSuccess(true);
			json.setMessage("获取模型列表成功!");
		} catch (Exception e) {
			logger.error(e.getMessage());
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
			logger.error(e.getMessage());
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
			logger.error(e.getMessage());
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
	public Result deploy(@RequestParam("modelId") String modelId, 
			@RequestParam("tenantId") String tenantId, 
			HttpServletRequest request) {
		Result result = new Result(false, "部署流程失败！");
		if(StringUtils.isEmpty(tenantId)) {
			result.setMessage("tenantId系统编号不能为空！！！");
			return result;
		}
		try {
			Model modelData = repositoryService.getModel(modelId);
			if(StringUtils.isEmpty(modelData.getKey())) {
				result.setMessage("流程KEY不能为空，请编辑补充！请求失败");
				return result;
			}
			if(StringUtils.isEmpty(modelData.getName())) {
				result.setMessage("流程名称不能为空，请编辑补充！请求失败");
				return result;
			}
			
			byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
			if(bytes == null) {
				result.setMessage("模型数据为空，请先设计流程并成功保存，再进行发布。");
			}else {
				ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(bytes);
				byte[] bpmnBytes = null;
				
				// TODO:UserTask自定义扩展属性，自定义解析
				CustomBpmnJsonConverter.getConvertersToBpmnMap().put("UserTask", CustomUserTaskJsonConverter.class);
				CustomBpmnJsonConverter.getConvertersToJsonMap().put(UserTask.class, CustomUserTaskJsonConverter.class);
				BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
				
				BpmnModel model = jsonConverter.convertToBpmnModel(modelNode);
				if(model.getProcesses().size()==0){
		            result.setMessage("数据模型不符要求，请至少设计一条主线流程。");
		        }else {
		        	bpmnBytes = new BpmnXMLConverter().convertToXML(model);
					String processName = modelData.getName() + ".bpmn20.xml";
					Deployment deployment = repositoryService.createDeployment().tenantId(tenantId).name(modelData.getName()).addString(processName, new String(bpmnBytes,"utf-8")).deploy();
					result.setSuccess(true);
					result.setObj(deployment.getId());
					result.setMessage("部署流程成功！");
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 导出model的xml文件
	 */
	@RequestMapping(value = "/export/{modelId}")
	public void export(@PathVariable("modelId") String modelId, HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");  
		response.setContentType("application/json; charset=utf-8");  
		try {
			Model modelData = repositoryService.getModel(modelId);
			
			// TODO:UserTask自定义扩展属性
			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			CustomBpmnJsonConverter.getConvertersToBpmnMap().put("UserTask", CustomUserTaskJsonConverter.class);
			CustomBpmnJsonConverter.getConvertersToJsonMap().put(UserTask.class, CustomUserTaskJsonConverter.class);
			
			//获取节点信息
			byte[] arg0 = repositoryService.getModelEditorSource(modelData.getId());
			JsonNode editorNode = new ObjectMapper().readTree(arg0);
			//将节点信息转换为xml
			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
        	BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
			byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

			ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
			IOUtils.copy(in, response.getOutputStream());
			String filename = modelData.getName() + ".bpmn20.xml";
			response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(filename, "UTF-8"));
			response.flushBuffer();
		} catch (Exception e){
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			out.write("未找到对应数据");
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	/**
	 * 检测所选择模型是否符合导出条件
	 */
	@RequestMapping(value = "/judgeModelIsOK/{modelId}")
	public Result judgeModelIsOK(@PathVariable("modelId") String modelId, HttpServletResponse response) {
		Result result = new Result(false, "检测模型失败");
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
		            result.setMessage("数据模型不符要求，请至少设计一条主线流程！");
		        } else {
					result.setSuccess(true);
					result.setMessage("所选模型符合导出条件！");
		        }
			} else {
				result.setMessage("模型数据为空，请先设计流程并成功保存，再进行导出！");
			}
		} catch (Exception e){
			logger.error(e.getMessage());
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
	
	/**
	 * 查询流程定义列表
	 * @return
	 */
	@RequestMapping(value="/selectDeploymentAll")
	@ResponseBody
	public DataJsonResult selectDeploymentAll(HttpServletRequest request){
		DataJsonResult json = new DataJsonResult(false, "获取流程定义列表失败!");
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
			ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
			List<ProcessDefinitionResponse> resultList = commonFlowQueryListService.getDeploymentList(query, param);
			Long total = query.count();
			json.setRows(resultList);//数据
			json.setTotal(total);//总记录数
			json.setSuccess(true);
			json.setMessage("获取流程定义列表成功!");
		} catch (Exception e) {
			logger.error("[查询流程定义列表]"+e.getMessage());
		}
		return json;
	}
	
	/**
	 * 批量删除已部署但未执行的流程
	 * @param ids
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deleteDeploymentByIds")
	@ResponseBody
	public Result deleteDeploymentByIds(String ids,HttpServletRequest request) {
		Result result = new Result(false, "删除失败!");
		try {
			if(StringUtils.isNotEmpty(ids)) {
				String[] idStr = ids.split(",");
				for(String id : idStr){
					repositoryService.deleteDeployment(id);
				}
			}
			result.setSuccess(true);
			result.setMessage("删除成功!");
		} catch (Exception e) {
			logger.error("[删除已部署但未执行的流程]"+e.getMessage());
			result.setMessage("删除失败！【注意：】已流转流程不允许删除");
		}
		return result;
	}
	
	/**
	 * 流程文件导入部署流程定义--通过xml文件
	 */
	@RequestMapping(value = "/deploymentByXML")
	@ResponseBody
	public Result deploymentByXML(@RequestParam("import_filepath") MultipartFile import_filepath,
			@RequestParam("import_name") String import_name,
			@RequestParam("filename") String filename,
			@RequestParam("tenantId") String tenantId,
			HttpServletRequest request) {
		Result result = new Result(false, "导入部署流程失败!");
		if(StringUtils.isEmpty(tenantId)) {
			result.setMessage("tenantId系统编号不能为空！！！");
			return result;
		}
		try {
			//获取上传文件
			InputStream fileInputStream =  import_filepath.getInputStream();
			repositoryService.createDeployment().name(import_name).tenantId(tenantId).addInputStream(filename, fileInputStream).deploy();
			result.setSuccess(true);
			result.setMessage("导入部署成功");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return result;
	}
	
	/**
	 * 根据流程id获取流程部署预览图
	 */
	@RequestMapping(value = "/getDeploymentPreViewPic")
	@ResponseBody
	public void getDeploymentPreViewPic(@RequestParam String id,HttpServletRequest request,
			HttpServletResponse httpServletResponse)throws Exception  {
		List<String> names = repositoryService.getDeploymentResourceNames(id);
		
		String imageName = null;
		for(String name : names ) {
			System.out.println("name:"+name);
			if(name.indexOf(".png")>0) {
				imageName = name;
			}
		}
		System.out.println("imageName"+imageName);
		if(imageName != null) {
			//通过部署id和文件名称获取输入流
			InputStream in = repositoryService.getResourceAsStream(id, imageName);
			OutputStream out = null;
	        byte[] buf = new byte[1024];
	        int legth = 0;
	        try {
	            out = httpServletResponse.getOutputStream();
	            while ((legth = in.read(buf)) != -1) {
	                out.write(buf, 0, legth);
	            }
	        } finally {
	            if (in != null) {
	                in.close();
	            }
	            if (out != null) {
	                out.close();
	            }
	        }
		}
	}
	
	/********************************************流程实例历史、流程活动历史、流程任务历史、流程变量历史************************************************/
	
	/**
	 * 流程实例历史页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/toHistoryProcessInstancePage")
	private ModelAndView toHistoryProcessInstancePage(HttpServletRequest request) {
		return new ModelAndView("historyProcessInstance");
	}
	
	/**
	 * 流程实例历史
	 */
	@RequestMapping(value = "/queryHistoricProcessInstance")
	@ResponseBody
	public DataJsonResult queryHistoricProcessInstance(HttpServletRequest request) {
		DataJsonResult json = new DataJsonResult(false, "获取模型列表失败!");
		try {
			Map<String, Object> param = ActivitiUtil.dealQueryListParam(request);
			HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
			List<HistoricProcessInstanceResponse> resultList = commonFlowQueryListService.queryHistoricProcessInstance(query, param);
			Long total = query.count();
			json.setRows(resultList);//数据
			json.setTotal(total);//总记录数
			json.setSuccess(true);
			json.setMessage("获取模型列表成功!");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 流程活动历史页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/toHistoryActivityInstancePage")
	private ModelAndView toHistoryActivityInstancePage(HttpServletRequest request) {
		return new ModelAndView("historyActivityInstance");
	}
	/**
	 * 流程历史活动
	 */
	@RequestMapping(value = "/queryHistoricActivityInstance")
	@ResponseBody
	public DataJsonResult queryHistoricActivityInstance(HttpServletRequest request) {
		DataJsonResult json = new DataJsonResult(false, "获取模型列表失败!");
		try {
			Map<String, Object> param = ActivitiUtil.dealQueryListParam(request);
			HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery();
			List<Map<String,Object>> resultList = commonFlowQueryListService.queryHistoricActivityInstance(query, param);
			Long total = query.count();
			json.setRows(resultList);//数据
			json.setTotal(total);//总记录数
			json.setSuccess(true);
			json.setMessage("获取模型列表成功!");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 流程任务历史页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/toHistoryTaskInstancePage")
	private ModelAndView toHistoryTaskInstancePage(HttpServletRequest request) {
		return new ModelAndView("historyTaskInstance");
	}
	
	/**
	 * 流程任务历史
	 */
	@RequestMapping(value = "/queryHistoricTaskInstance")
	@ResponseBody
	public DataJsonResult queryHistoricTaskInstance(HttpServletRequest request) {
		DataJsonResult json = new DataJsonResult(false, "获取模型列表失败!");
		try {
			Map<String, Object> param = ActivitiUtil.dealQueryListParam(request);
			HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
			List<HistoricTaskInstanceResponse> resultList = commonFlowQueryListService.queryHistoricTaskInstance(query, param);
			Long total = query.count();
			json.setRows(resultList);//数据
			json.setTotal(total);//总记录数
			json.setSuccess(true);
			json.setMessage("获取模型列表成功!");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return json;
	}
	
	/**
	 * 流程任务历史页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/toHistoryVariableInstancePage")
	private ModelAndView toHistoryVariableInstancePage(HttpServletRequest request) {
		return new ModelAndView("historyVariableInstance");
	}
	
	/**
	 * 流程变量历史
	 */
	@RequestMapping(value = "/queryHistoricVariableInstance")
	@ResponseBody
	public DataJsonResult queryHistoricVariableInstance(HttpServletRequest request) {
		DataJsonResult json = new DataJsonResult(false, "获取模型列表失败!");
		try {
			Map<String, Object> param = ActivitiUtil.dealQueryListParam(request);
			HistoricVariableInstanceQuery query = historyService.createHistoricVariableInstanceQuery();
			List<HistoricVariableInstanceResponse> resultList = commonFlowQueryListService.queryHistoricVariableInstance(query, param);
			Long total = query.count();
			json.setRows(resultList);//数据
			json.setTotal(total);//总记录数
			json.setSuccess(true);
			json.setMessage("获取模型列表成功!");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return json;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
