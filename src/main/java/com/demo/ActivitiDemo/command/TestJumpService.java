package com.demo.ActivitiDemo.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @desc 解决不了并发的问题，所以唯一的选择是命令式的jumpcmd一条路了。
 * @author cheng.chen
 *
 */
@Deprecated
@Service
public class TestJumpService {
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private RepositoryService repositoryService;
	
	
	
	
	/**
	 * 流程转向操作
	 *
	 * @param taskId     当前任务ID
	 * @param activityId 目标节点任务ID
	 * @param variables  流程变量
	 * @throws Exception
	 */
	public void turnTransition(String taskId, String activityId, Map<String, Object> variables) {
	    // 当前节点
	    ActivityImpl currActivity = findActivitiImpl(taskId, null);
	    // 清空当前流向
	    List<PvmTransition> oriPvmTransitionList = clearTransition(currActivity);

	    // 创建新流向
	    TransitionImpl newTransition = currActivity.createOutgoingTransition();
	    // 目标节点
	    ActivityImpl pointActivity = findActivitiImpl(taskId, activityId);
	    // 设置新流向的目标节点
	    newTransition.setDestination(pointActivity);
	    // 执行转向任务
	    taskService.complete(taskId, variables);
	    // 删除目标节点新流入
	    pointActivity.getIncomingTransitions().remove(newTransition);

	    // 还原以前流向
	    restoreTransition(currActivity, oriPvmTransitionList);
	}
	/**
	 * 根据任务ID和节点ID获取活动节点 <br>
	 *
	 * @param taskId
	 * @param activityId 活动节点ID <br>
	 *                   如果为null或""，则默认查询当前活动节点 <br>
	 *                   如果为"end"，则查询结束节点 <br>
	 * @return
	 * @throws Exception
	 */
	public ActivityImpl findActivitiImpl(String taskId, String activityId) {
	    // 取得流程定义
	    ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(taskService.createTaskQuery().taskId(taskId).singleResult().getProcessDefinitionId());
	    // 获取当前活动节点ID
	    if (StringUtils.isEmpty(activityId)) {
	        activityId = getTaskById(taskId).getTaskDefinitionKey();
	    }
	    // 根据流程定义，获取该流程实例的结束节点
	    ProcessDefinitionImpl processDefinitionImpl = (ProcessDefinitionImpl) getReadOnlyProcessDefinitionByProcDefId(processDefinition.getId());
	    if (activityId.toUpperCase().equals("END")) {
	        for (ActivityImpl activityImpl : processDefinitionImpl.getActivities()) {
	            List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
	            if (pvmTransitionList.isEmpty()) {
	                return activityImpl;
	            }
	        }
	    }
	    // 根据节点ID，获取对应的活动节点
	    ActivityImpl activityImpl = processDefinitionImpl.findActivity(activityId);
	    // 此处改动，无法获取到对应的活动节点，所以此处采用迂回的方式，如果前面可以取到则跳过，如果没有取到则自己取
	    if (activityImpl == null) {
	        List<ActivityImpl> activities = processDefinitionImpl.getActivities();
	        for (ActivityImpl actImpl : activities) {
	            if (actImpl.getId().equals(activityId)) {
	                activityImpl = actImpl;
	                break;
	            }
	        }
	    }
	    return activityImpl;
	}
	private ProcessDefinition getReadOnlyProcessDefinitionByProcDefId(String id) {
		// TODO Auto-generated method stub
		ProcessDefinition processDefinition = repositoryService.getProcessDefinition(id);  
		return processDefinition;
	}
	private Task getTaskById(String taskId) {
		// TODO Auto-generated method stub
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		return task;
	}
	/**
	 * 清空指定活动节点现有流向,且将清空的现有流向返回
	 *
	 * @param activityImpl 活动节点
	 * @return 节点流向集合
	 */
	public List<PvmTransition> clearTransition(ActivityImpl activityImpl) {
	    // 存储当前节点所有流向临时变量
	    List<PvmTransition> oriPvmTransitionList = new ArrayList<>();
	    // 获取当前节点所有流向，存储到临时变量，然后清空
	    List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
	    for (PvmTransition pvmTransition : pvmTransitionList) {
	        oriPvmTransitionList.add(pvmTransition);
	    }
	    pvmTransitionList.clear();

	    return oriPvmTransitionList;
	}
	
	/**
	 * 清空指定节点现有流向，且将新流向接入
	 *
	 * @param activityImpl         活动节点
	 * @param oriPvmTransitionList 新流向节点集合
	 */
	public static void restoreTransition(ActivityImpl activityImpl, List<PvmTransition> oriPvmTransitionList) {
	    // 清空现有流向
	    List<PvmTransition> pvmTransitionList = activityImpl.getOutgoingTransitions();
	    pvmTransitionList.clear();
	    // 还原以前流向
	    for (PvmTransition pvmTransition : oriPvmTransitionList) {
	        pvmTransitionList.add(pvmTransition);
	    }
	}

}
