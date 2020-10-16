package com.demo.ActivitiDemo.controller;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demo.ActivitiDemo.bean.FormData;
import com.demo.ActivitiDemo.service.ProcessService;

@Controller
@RequestMapping("/process")
public class ProcessTaskController {

	@Resource
	private ProcessService processServiceImpl;

	// 获取受理员任务列表
	@RequestMapping("/queryTaskSl")
	public String findTasksForSL(ModelMap modelMap, String userId) {
		List<Task> lists = processServiceImpl.findTasksByUserId(userId);
		modelMap.addAttribute("tasks", lists);
		modelMap.addAttribute("userId", userId);
		return "taskList";
	}

	// 受理员受理数据
	@RequestMapping("/completeTaskSl")
	public String completeTasksForSL(ModelMap modelMap, FormData formData) {
		processServiceImpl.completeTask(formData.getId(), formData.getUserId(), formData.getAttr1());// 受理后，任务列表数据减少
		return findTasksForSL(modelMap, formData.getUserId());
	}

	@RequestMapping("/form")
	public String form(FormData formData, ModelMap modelMap) {
		Task task = processServiceImpl.findTaskById(formData.getId());
		modelMap.addAttribute("data", formData);
		modelMap.addAttribute("task", task);
		if (StringUtils.isNotEmpty(task.getFormKey())) {
			return "activitiForm/" + task.getFormKey();
		}
		return "form";
	}

	// 生成流程图（高亮）---232501
	@RequestMapping("/queryProHighLighted")
	public @ResponseBody String queryProHighLighted(String proInsId) throws Exception {
		String imageByteArray = processServiceImpl.queryProHighLighted(proInsId);
		return imageByteArray;
	}
}
