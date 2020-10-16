package com.demo.ActivitiDemo.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

public interface ProcessService {

	/**
	 * 
	 * @param key activiti.act_re_procdef表的key_字段
	 */
	ProcessInstance startProcesses(String key);
	
	/**
	 * 
	 * @param key activiti.act_re_procdef表的key_字段
	 */
	ProcessInstance startProcesses(String key,String businessKey,Map<String, Object> variables);

	/**
	 * 加载用户所有的任务件（包括待选件和已选件待完成的）
	 * @param userId
	 * @return
	 */
	List<Task> findTasksByUserId(String userId);
	
	List<Task> findPendingTasks(String bizKey);
	
	List<Task> findPendingTasksByUserId(String userId);
	
	void claimTask(String taskId,String userId);
	
	void assignTask(String taskId,String userId);
	
	void completeTask(String taskId, Map<String,Object> variables);

	Task findTaskById(String taskId);
	
	Task findTaskByExecId(String execId);

	void completeTask(String taskId, String userId, String result);
	
	void jumpToTask(String executionIdOrProcInsId, String sourceNodeId,String targetNodeId,Map<String, Object> params, String reason) throws Exception;

	void updateBizStatus(DelegateExecution execution, String status);

	String queryProHighLighted(String processInstanceId) throws Exception;
	
	public List<String> findOutComeListByTaskId(String taskId);

	/*
	 * List<String> findUsersForSL(DelegateExecution execution);
	 * 
	 * List<String> findUsersForSP(DelegateExecution execution);
	 * 
	 * void queryProImg(String processInstanceId) throws Exception;
	 * 
	 * 
	 */

}
