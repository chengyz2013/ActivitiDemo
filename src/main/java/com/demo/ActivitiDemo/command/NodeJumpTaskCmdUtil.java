package com.demo.ActivitiDemo.command;

import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.ReadOnlyProcessDefinition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.context.annotation.Description;

public class NodeJumpTaskCmdUtil {
	
	
	/**
	 * 
	 * @param processEngine 流程引擎
	 * @param vars          该节点带有的参数
	 * @param executionIdOrProcInsId   执行ID或者实例ID
	 * @param destActivityNodeId       目标节点ID
	 * @param curActivityNodeId        当前节点ID
	 */
	@Deprecated
	public static void jumpTask(ProcessEngine processEngine, Map<String, Object> vars, String executionIdOrProcInsId,
			String destActivityNodeId, String curActivityNodeId) {
		RuntimeService runtimeService = processEngine.getRuntimeService();
		// 获取当前的execution 并找到当前execution所属的processdefinition
		// 执行ID
		String executionId = executionIdOrProcInsId;
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
		String procDefId = processInstance.getProcessDefinitionId();

		RepositoryService repositoryService = processEngine.getRepositoryService();
		ReadOnlyProcessDefinition processDefinitionEntity = (ReadOnlyProcessDefinition) repositoryService
				.getProcessDefinition(procDefId);

		ActivityImpl curActivity = (ActivityImpl) processDefinitionEntity.findActivity(curActivityNodeId);
		ActivityImpl destActivity = (ActivityImpl) processDefinitionEntity.findActivity(destActivityNodeId);

		processEngine.getManagementService()
				.executeCommand(new NodeJumpTaskCmd(executionId, destActivity, vars, curActivity));

	}

	@Deprecated
	public static void jumpTask(ProcessEngine processEngine, Map<String, Object> vars, String executionIdOrProcInsId,
			String destActivityNodeId, String curActivityNodeId, String deleteReason) {
		TaskService taskService = processEngine.getTaskService();
		// 获取当前的task 并找到当前task所属的processdefinition
		// 执行ID
		String executionId = executionIdOrProcInsId;
		Task task = taskService.createTaskQuery().processInstanceId(executionId).singleResult();
		String procDefId = task.getProcessDefinitionId();

		RepositoryService repositoryService = processEngine.getRepositoryService();
		ReadOnlyProcessDefinition processDefinitionEntity = (ReadOnlyProcessDefinition) repositoryService
				.getProcessDefinition(procDefId);

		ActivityImpl curActivity = (ActivityImpl) processDefinitionEntity.findActivity(curActivityNodeId);
		ActivityImpl destActivity = (ActivityImpl) processDefinitionEntity.findActivity(destActivityNodeId);

		processEngine.getManagementService()
				.executeCommand(new NodeJumpTaskCmd(executionId, destActivity, vars, curActivity, deleteReason));

	}

	public static void jumpTask(ProcessEngine processEngine,Map<String, Object> vars, String executionIdOrProcInsId,
			String targetActivityId) {
		processEngine.getManagementService()
				.executeCommand(new JumpCmd(executionIdOrProcInsId, targetActivityId, vars));

	}
}
