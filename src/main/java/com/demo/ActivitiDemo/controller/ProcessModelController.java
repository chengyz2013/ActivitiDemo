package com.demo.ActivitiDemo.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.explorer.util.XmlUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.demo.ActivitiDemo.service.ProcessService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

//@RestController
@Controller
@RequestMapping("/model")
public class ProcessModelController {

	@Autowired
	private RepositoryService repositoryService;

	@Resource
	private ProcessService processServiceImpl;

	/**
	 * @desc 创建模型
	 */
	@RequestMapping("/create")
	public void create(HttpServletRequest request, HttpServletResponse response) {
		try {
			// ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

			// RepositoryService repositoryService = processEngine.getRepositoryService();

			ObjectMapper objectMapper = new ObjectMapper();
			ObjectNode editorNode = objectMapper.createObjectNode();
			editorNode.put("id", "canvas");
			editorNode.put("resourceId", "canvas");
			ObjectNode stencilSetNode = objectMapper.createObjectNode();
			stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
			editorNode.put("stencilset", stencilSetNode);
			Model modelData = repositoryService.newModel();

			ObjectNode modelObjectNode = objectMapper.createObjectNode();
			modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, "newBlankProcess");
			modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
			modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, "新空白流程");
			modelData.setMetaInfo(modelObjectNode.toString());
			modelData.setName("newBlankModel");
			modelData.setKey("newModelDefinition");

			// 保存模型
			repositoryService.saveModel(modelData);
			repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
			response.sendRedirect(request.getContextPath() + "/modeler.html?modelId=" + modelData.getId());
		} catch (Exception e) {
			System.out.println("创建模型失败：");
		}
	}

	/**
	 * @desc 加载模型
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/list")
	public String modelList(org.springframework.ui.Model model, HttpServletRequest request) {
		List<Model> models = repositoryService.createModelQuery().orderByCreateTime().desc().list();
		model.addAttribute("models", models);
		String info = request.getParameter("info");
		if (StringUtils.isNotEmpty(info)) {
			model.addAttribute("info", info);
		}
		return "modellist";
	}

	/**
	 * @desc 删除模型
	 * @param id activiti.act_re_model表的ID_字段
	 * @return
	 */
	@RequestMapping("/delete/{id}")
	public @ResponseBody String deleteModel(@PathVariable("id") String id) {
		repositoryService.deleteModel(id);
		return "删除成功！";
	}

	/**
	 * @desc 部署模型
	 * @param id activiti.act_re_model表的ID_字段
	 * @return
	 */
	@RequestMapping("/deploy/{id}")
	public @ResponseBody String deploy(@PathVariable("id") String id) throws Exception {

		// 获取模型
		Model modelData = repositoryService.getModel(id);
		byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

		if (bytes == null) {
			return "模型数据为空，请先设计流程并成功保存，再进行发布。";
		}

		JsonNode modelNode = new ObjectMapper().readTree(bytes);

		BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
		if (model.getProcesses().size() == 0) {
			return "数据模型不符要求，请至少设计一条主线流程。";
		}
		byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

		// 发布流程
		String processName = modelData.getName() + ".bpmn20.xml";
		Deployment deployment = repositoryService.createDeployment().name(modelData.getName())
				.addString(processName, new String(bpmnBytes, "UTF-8")).deploy();
		modelData.setDeploymentId(deployment.getId());
		repositoryService.saveModel(modelData);
		return "流程发布成功";
	}

	/**
	 * @desc 启动流程
	 * @param key activiti.act_re_model的deployment_Id字段
	 * @return
	 */
	@RequestMapping("/start/{deploymentId}")
	public String startProcess(@PathVariable("deploymentId") String deploymentId, RedirectAttributesModelMap modelMap) {

		modelMap.addFlashAttribute("info", "流程启动成功！");
		// 通过deploymentId查询对应的部署流程key
		ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
				.deploymentId(deploymentId).singleResult();
		String key = processDefinition.getKey();
		processServiceImpl.startProcesses(key);

		return "redirect:/model/list";
	}

	/**
	 * @desc 导出model的xml文件
	 */
	@RequestMapping("/export/{modelId}")
	public void export(@PathVariable("modelId") String modelId, HttpServletResponse response) {
		try {
			Model modelData = repositoryService.getModel(modelId);
			BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
			JsonNode editorNode = new ObjectMapper()
					.readTree(repositoryService.getModelEditorSource(modelData.getId()));
			BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
			BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
			byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);

			ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
			IOUtils.copy(in, response.getOutputStream());
			String filename = bpmnModel.getMainProcess().getId() + ".bpmn20.xml";
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.flushBuffer();
		} catch (Exception e) {
			System.out.println("导出model的xml文件失败：modelId={}" + modelId + e);
		}
	}

	/**
	 * @decs activiti modeler 导入现有流程文件
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public void deployUploadedFile(@RequestParam("uploadfile") MultipartFile uploadfile) {
		InputStreamReader in = null;
		try {
			try {
				boolean validFile = false;
				String fileName = uploadfile.getOriginalFilename();
				if (fileName.endsWith(".bpmn20.xml") || fileName.endsWith(".bpmn")) {
					validFile = true;

					XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
					in = new InputStreamReader(new ByteArrayInputStream(uploadfile.getBytes()), "UTF-8");
					XMLStreamReader xtr = xif.createXMLStreamReader(in);
					BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

					if (bpmnModel.getMainProcess() == null || bpmnModel.getMainProcess().getId() == null) {
//                        notificationManager.showErrorNotification(Messages.MODEL_IMPORT_FAILED,
//                                i18nManager.getMessage(Messages.MODEL_IMPORT_INVALID_BPMN_EXPLANATION));
						System.out.println("err1");
					} else {

						if (bpmnModel.getLocationMap().isEmpty()) {
//                            notificationManager.showErrorNotification(Messages.MODEL_IMPORT_INVALID_BPMNDI,
//                                    i18nManager.getMessage(Messages.MODEL_IMPORT_INVALID_BPMNDI_EXPLANATION));
							System.out.println("err2");
						} else {

							String processName = null;
							if (StringUtils.isNotEmpty(bpmnModel.getMainProcess().getName())) {
								processName = bpmnModel.getMainProcess().getName();
							} else {
								processName = bpmnModel.getMainProcess().getId();
							}
							Model modelData;
							modelData = repositoryService.newModel();
							ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
							modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processName);
							modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
							modelData.setMetaInfo(modelObjectNode.toString());
							modelData.setName(processName);

							repositoryService.saveModel(modelData);

							BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
							ObjectNode editorNode = jsonConverter.convertToJson(bpmnModel);

							repositoryService.addModelEditorSource(modelData.getId(),
									editorNode.toString().getBytes("utf-8"));
						}
					}
				} else {
//                    notificationManager.showErrorNotification(Messages.MODEL_IMPORT_INVALID_FILE,
//                            i18nManager.getMessage(Messages.MODEL_IMPORT_INVALID_FILE_EXPLANATION));
					System.out.println("err3");
				}
			} catch (Exception e) {
				String errorMsg = e.getMessage().replace(System.getProperty("line.separator"), "<br/>");
//                notificationManager.showErrorNotification(Messages.MODEL_IMPORT_FAILED, errorMsg);
				System.out.println("err4");
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
//                    notificationManager.showErrorNotification("Server-side error", e.getMessage());
					System.out.println("err5");
				}
			}
		}
	}
	
	@RequestMapping("/processDefinitionImage/{procDefId}")
    public void processDefinitionImage(@PathVariable("procDefId") String procDefId, HttpServletResponse response) throws IOException {

        if (StringUtils.isBlank(procDefId)) {
            throw new RuntimeException("processDefinitionId 不能为空!");
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getDiagramResourceName());
        OutputStream outputStream = response.getOutputStream();
        try {
        	//FileUtils.copyInputStreamToFile(inputStream, new File("C:/1.png"));
            IOUtils.copy(inputStream,outputStream);
            
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	@RequestMapping("/processDefinitionBpmn/{procDefId}")
    public void processDefinitionBpmn(@PathVariable("procDefId") String procDefId, HttpServletResponse response) throws IOException {

        if (StringUtils.isBlank(procDefId)) {
            throw new RuntimeException("processDefinitionId 不能为空!");
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName());
        OutputStream outputStream = response.getOutputStream();
        try {
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//	public HashMap<String, Object> uploadModel(MultipartFile modelFile) {
//	  HashMap<String, Object> result = new HashMap<>(); InputStreamReader in =
//	  null; try { try { boolean validFile = false; String fileName =
//	  modelFile.getOriginalFilename(); if (fileName.endsWith(".bpmn20.xml") ||
//	  fileName.endsWith(".bpmn")) { validFile = true; XMLInputFactory xif =
//	  XmlUtil.createSafeXmlInputFactory(); in = new InputStreamReader(new
//	  ByteArrayInputStream(modelFile.getBytes()), "UTF-8"); XMLStreamReader xtr =
//	  xif.createXMLStreamReader(in); BpmnModel bpmnModel = new
//	  BpmnXMLConverter().convertToBpmnModel(xtr);
//	  
//	  if (bpmnModel.getMainProcess() == null || bpmnModel.getMainProcess().getId()
//	  == null) { //
//	  notificationManager.showErrorNotification(Messages.MODEL_IMPORT_FAILED, //
//	  i18nManager.getMessage(Messages.MODEL_IMPORT_INVALID_BPMN_EXPLANATION));
//	  result.put("status", "ResponseConstantManager.STATUS_FAIL");
//	  result.put("message", "数据模型无效，必须有一条主流程"); } else { if
//	  (bpmnModel.getLocationMap().isEmpty()) { //
//	  notificationManager.showErrorNotification(Messages.
//	  MODEL_IMPORT_INVALID_BPMNDI, //
//	  i18nManager.getMessage(Messages.MODEL_IMPORT_INVALID_BPMNDI_EXPLANATION));
//	  result.put("status", "ResponseConstantManager.STATUS_FAIL");
//	  result.put("message", "locationMap为空"); } else { String processName = null;
//	  if (StringUtils.isNotEmpty(bpmnModel.getMainProcess().getName())) {
//	  processName = bpmnModel.getMainProcess().getName(); } else { processName =
//	  bpmnModel.getMainProcess().getId(); } Model modelData; modelData =
//	  repositoryService.newModel(); ObjectNode modelObjectNode = new
//	  ObjectMapper().createObjectNode();
//	  modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processName);
//	  modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
//	  modelData.setMetaInfo(modelObjectNode.toString());
//	  modelData.setName(processName);
//	  
//	  repositoryService.saveModel(modelData);
//	  
//	  BpmnJsonConverter jsonConverter = new BpmnJsonConverter(); ObjectNode
//	  editorNode = jsonConverter.convertToJson(bpmnModel);
//	  
//	  byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel); //
//	  System.out.println(new String(bpmnBytes, "UTF-8")); //
//	  System.out.println(editorNode);
//	  
//	  repositoryService.addModelEditorSource(modelData.getId(),
//	  editorNode.toString().getBytes("utf-8")); result.put("status",
//	  "ResponseConstantManager.STATUS_SUCCESS"); result.put("modelId",
//	  modelData.getId()); } } } else { //
//	  notificationManager.showErrorNotification(Messages.MODEL_IMPORT_INVALID_FILE,
//	  // i18nManager.getMessage(Messages.MODEL_IMPORT_INVALID_FILE_EXPLANATION));
//	  result.put("status", "ResponseConstantManager.STATUS_FAIL");
//	  result.put("message", "后缀名无效"); System.out.println("err3"); } } catch
//	  (Exception e) { // String errorMsg =
//	  e.getMessage().replace(System.getProperty("line.separator"), "<br/>"); //
//	  notificationManager.showErrorNotification(Messages.MODEL_IMPORT_FAILED,
//	  errorMsg); result.put("status", "ResponseConstantManager.STATUS_FAIL");
//	  result.put("message", e.toString()); } } finally { if (in != null) { try {
//	  in.close(); } catch (IOException e) { //
//	  notificationManager.showErrorNotification("Server-side error",
//	  e.getMessage()); result.put("status", "ResponseConstantManager.STATUS_FAIL");
//	  result.put("message", e.toString()); } } } return result; }

	/**
	 * @desc 取得流程定义的XML 仅供参考思路
	 * 
	 * @param deployId
	 * @return
	 */
	/*
	 * public String getDefXmlByDeployId(String deployId){ String sql =
	 * "select a.* from ACT_GE_BYTEARRAY a where NAME_ LIKE '%bpmn20.xml' and DEPLOYMENT_ID_= ? "
	 * ; final LobHandler lobHandler = new DefaultLobHandler(); // reusable final
	 * ByteArrayOutputStream contentOs = new ByteArrayOutputStream(); String defXml
	 * = null; try{ jdbcTemplate.query(sql, new Object[]{deployId },new
	 * AbstractLobStreamingResultSetExtractor<Object>(){ public void
	 * streamData(ResultSet rs) throws SQLException, IOException{
	 * FileCopyUtils.copy(lobHandler.getBlobAsBinaryStream(rs, "BYTES_"),
	 * contentOs); } } ); defXml = new String(contentOs.toByteArray(), "UTF-8"); }
	 * catch (Exception ex){ ex.printStackTrace(); } return defXml; }
	 */
}
