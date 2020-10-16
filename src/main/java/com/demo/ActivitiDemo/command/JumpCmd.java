package com.demo.ActivitiDemo.command;

import java.util.Map;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

/**
 * @desc 注意此命令跳转后，触发的是delete事件的监听器，而不是complete事件
 * @author cheng.chen
 *
 */
public class JumpCmd implements Command<ExecutionEntity>{
	private String processInstanceId;
	private String targetActivityId;
	private String deleteReason = "jumpNode by JumpCmd";
	private Map<String, Object> param;

	@Override
	public ExecutionEntity execute(CommandContext commandContext) {
		// TODO Auto-generated method stub
		ExecutionEntity executionEntity = commandContext.getExecutionEntityManager().findExecutionById(processInstanceId);
		executionEntity.setVariables(param);
		executionEntity.destroyScope(deleteReason);
		ActivityImpl targetActivityImpl = executionEntity.getProcessDefinition().findActivity(targetActivityId);
		executionEntity.executeActivity(targetActivityImpl);
		return executionEntity;
	}

	public JumpCmd(String processInstanceId, String targetActivityId, String deleteReason, Map<String, Object> param) {
		this.processInstanceId = processInstanceId;
		this.targetActivityId = targetActivityId;
		this.deleteReason = deleteReason;
		this.param = param;
	}

	public JumpCmd(String processInstanceId, String targetActivityId, Map<String, Object> param) {
		this.processInstanceId = processInstanceId;
		this.targetActivityId = targetActivityId;
		this.param = param;
	}
	
	

}
