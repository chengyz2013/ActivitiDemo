package com.demo.ActivitiDemo.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.ActivitiDemo.command.TestJumpService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest {
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private KafkaSendService kafkaService;
	
	@Autowired
	private RepositoryService repositoryService;
	@Autowired
	private ProcessService processService;
	
	@Autowired
	private TestJumpService testJumpService;
	
	@Autowired
	private AlarmTaskService alarmTaskService;
	
	
	
	@Test
	public void testLoadDeploy() {
		// TODO Auto-generated method stub
		String deploymentId = "62504";
		ProcessDefinition procDef =repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
		List<ProcessDefinition> procDefs = repositoryService.createProcessDefinitionQuery().processDefinitionKey(procDef.getKey()).orderByProcessDefinitionVersion().desc().list();
		for(ProcessDefinition processDefinition : procDefs) {
			System.out.println("processDefinition getId:"+processDefinition.getId());
			System.out.println("processDefinition getKey:"+processDefinition.getKey());
			System.out.println("processDefinition getName:"+processDefinition.getName());
			System.out.println("processDefinition getDeploymentId:"+processDefinition.getDeploymentId());
			System.out.println("processDefinition getVersion:"+processDefinition.getVersion());
			System.out.println("==================================");
		}

	}
	
	@Test
	public void testdeletedeployment() {
		String deploymentId ="37501";
		repositoryService.deleteDeployment(deploymentId, true);
	}
	
	
	@Test
	public void testStartProcess() {
		String bizKeyString = "myProcess";
		processService.startProcesses(bizKeyString);
	}
	
	@Test
	public void testFindPendingTasks() {
		List<Task> tasks = processService.findPendingTasks("myProcess");
		for(Task task : tasks) {
			System.out.println("task name:"+task.getName());
			System.out.println("task assign:"+task.getAssignee());
			System.out.println("task processinstanceid:"+task.getProcessInstanceId());
			System.out.println("task procedefid:"+task.getProcessDefinitionId());
			System.out.println("==================================");
		}
	}
	
	@Test
	public void testFindPendingTasksByUserId() {
		List<Task> tasks = processService.findTasksByUserId("ee");
		for(Task task : tasks) {
			System.out.println("task name:"+task.getName());
			System.out.println("task assign:"+task.getAssignee());
			System.out.println("task processinstanceid:"+task.getProcessInstanceId());
			System.out.println("task procedefid:"+task.getProcessDefinitionId());
			System.out.println("==================================");
		}
	}
	
	@Test
	public void testClaimTask() {
		String userId = "test4";
		String taskId = "360002";
		processService.claimTask(taskId, userId);
		List<Task> tasks = processService.findTasksByUserId(userId);
		for(Task task : tasks) {
			System.out.println("task name:"+task.getName());
			System.out.println("task assign:"+task.getAssignee());
			System.out.println("task processinstanceid:"+task.getProcessInstanceId());
			System.out.println("task procedefid:"+task.getProcessDefinitionId());
			System.out.println("==================================");
		}
	}
	
	@Test
	public void testAssignTask() {
		String userId = "张三";
		String taskId = "365002";
		processService.assignTask(taskId, userId);
		List<Task> tasks = processService.findTasksByUserId(userId);
		for(Task task : tasks) {
			System.out.println("task name:"+task.getName());
			System.out.println("task assign:"+task.getAssignee());
			System.out.println("task processinstanceid:"+task.getProcessInstanceId());
			System.out.println("task procedefid:"+task.getProcessDefinitionId());
			System.out.println("==================================");
		}
	}
	
	@Test
	public void testCompleteTask() {
		Map<String, Object> varMap = new HashMap<String, Object>();
		varMap.put("name", "sp3");
		String taskId = "335002";
		
		processService.completeTask(taskId, varMap);
		List<Task> tasks = processService.findTasksByUserId("审批2");
		for(Task task : tasks) {
			System.out.println("task name:"+task.getName());
			System.out.println("task assign:"+task.getAssignee());
			System.out.println("task processinstanceid:"+task.getProcessInstanceId());
			System.out.println("task procedefid:"+task.getProcessDefinitionId());
			System.out.println("==================================");
		}
	}
	
	@Test
	public void testJumpTask() throws Exception {
		String instId = "340007";
		
		processService.jumpToTask(instId, "sp4", "sp1", null, "");
		List<Task> tasks = processService.findTasksByUserId("ff");
		for(Task task : tasks) {
			System.out.println("task name:"+task.getName());
			System.out.println("task assign:"+task.getAssignee());
			System.out.println("task processinstanceid:"+task.getProcessInstanceId());
			System.out.println("task procedefid:"+task.getProcessDefinitionId());
			System.out.println("==================================");
		}
	}
	
	@Test
	public void testJumpByDrawflow() {
		String taskId = "285002";
		String activityId = "sp1";
		testJumpService.turnTransition(taskId, activityId, null);
	}
	
	@Test
	public void testAddComment() {
		String deploymentId = "312501";
		//repositoryService.deleteDeployment(deploymentId);
		taskService.addComment(null, "95001", "message");
		String processInstanceId = "95001";
		
		List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);
		
		
		
		for(Comment comment : comments) {
			System.out.println(comment.getFullMessage());
		}
	}
	
	@Test
	public void testkafkasend() throws UnsupportedEncodingException {
		String message = "吃嘛嘛香";
		kafkaService.send(message);
	}
	
	@Test
	public void testfindOutComeListByTaskId() throws UnsupportedEncodingException {
		String taskId = "337503";
		List list = processService.findOutComeListByTaskId(taskId);
		
		for(Object name : list) {
			System.out.println(name);
		}
	}
	
	@Test
	public void testfindGrouptask() throws UnsupportedEncodingException {
		String groupId = "group1";
		
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(groupId).list();
		for(Task task : tasks) {
			System.out.println("task name:"+task.getName());
			System.out.println("task assign:"+task.getAssignee());
			System.out.println("task processinstanceid:"+task.getProcessInstanceId());
			System.out.println("task procedefid:"+task.getProcessDefinitionId());
			System.out.println("==================================");
		}
	}
}
