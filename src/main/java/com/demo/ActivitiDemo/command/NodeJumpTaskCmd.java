package com.demo.ActivitiDemo.command;

import java.util.Iterator;
import java.util.Map;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;

/**
 * 
 * @author cheng.chen
 * @Desc 该方法会导致act_hi_actinst 的 end_time记录失效 主要是对删除任务产生分歧，
 *       -可以参考jumpcmd的executionEntity.destroyScope(deleteReason);方法
 *       -删除任务这块逻辑感觉还是有点粗糙
 */
@Deprecated
public class NodeJumpTaskCmd implements Command<Void>{
	
	private String executionId;//流程执行ID
	private ActivityImpl desActivity;//目标引擎对象
	private Map<String, Object> param;
	private ActivityImpl curActivity;//当前引擎对象
	private String deleteReason = "completed by jump cmd";
	
	public NodeJumpTaskCmd(String executionId, ActivityImpl desActivity, Map<String, Object> param,
			ActivityImpl curActivity) {
		super();
		this.executionId = executionId;
		this.desActivity = desActivity;
		this.param = param;
		this.curActivity = curActivity;
	}

	public NodeJumpTaskCmd(String executionId, ActivityImpl desActivity, Map<String, Object> param,
			ActivityImpl curActivity, String deleteReason) {
		this.executionId = executionId;
		this.desActivity = desActivity;
		this.param = param;
		this.curActivity = curActivity;
		this.deleteReason = deleteReason;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		// TODO Auto-generated method stub
		//获取执行实体管理
		ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
		//根据ExecutionID查找执行实体
		ExecutionEntity executionEntity = executionEntityManager.findExecutionById(executionId);
		executionEntity.setVariables(param);
		executionEntity.setEventSource(this.curActivity);
		executionEntity.setActivity(this.curActivity);
		//executionEntity.destroyScope(this.deleteReason);
		//获取当前ExecutionID的任务 根据executionID获取Task
		Iterator<TaskEntity> localIterator = commandContext.getTaskEntityManager().findTasksByExecutionId(executionId).iterator();
		while(localIterator.hasNext()) {
			TaskEntity taskEntity = localIterator.next();
			//触发任务监听,如果该任务配置 "任务监听器",并且配置了"complete"类型的监听类，则通知该类执行。
			taskEntity.fireEvent("delete");
			//删除任务的原因
			commandContext.getTaskEntityManager().deleteTask(taskEntity, this.deleteReason, false);
		}
		
		executionEntity.executeActivity(this.desActivity);
		return null;
	}

}
