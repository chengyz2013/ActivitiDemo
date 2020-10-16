package com.demo.ActivitiDemo.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.activation.MailcapCommandMap;
import javax.imageio.ImageIO;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.ActivitiDemo.command.NodeJumpTaskCmd;
import com.demo.ActivitiDemo.command.NodeJumpTaskCmdUtil;
import com.demo.ActivitiDemo.service.ProcessService;

import sun.misc.BASE64Encoder;

@Service
public class ProcessServiceImpl implements ProcessService {

	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
    private HistoryService historyService;
	
    @Autowired
    private RepositoryService repositoryService;
    
    @Autowired
    private ProcessEngineConfigurationImpl processEngineConfiguration;
    
    @Autowired
    private ProcessEngine processEngine;
	
	@Override
	public ProcessInstance startProcesses(String key) {
		// TODO Auto-generated method stub
		ProcessInstance pi = runtimeService.startProcessInstanceByKey(key);//流程图id，业务表id
		System.out.println("流程启动成功，流程id:"+pi.getId());
		return pi;
	}

	@Override
	public List<Task> findTasksByUserId(String userId) {
		// TODO Auto-generated method stub
		
		List<Task> retList = new ArrayList<Task>();
		
		retList = taskService.createTaskQuery().taskCandidateOrAssigned(userId).list();
		
		if(retList == null) {
			return new ArrayList<Task>();
		}
		
		return retList;
	}

	@Override
	public Task findTaskById(String taskId) {
		// TODO Auto-generated method stub
		List<Task> resultTask = taskService.createTaskQuery().taskId(taskId).list();
        if(resultTask != null && resultTask.size() > 0){
            return resultTask.get(0);
        }
        return null;
	}

	@Override
	public void completeTask(String taskId, String userId, String result) {
		// TODO Auto-generated method stub
		//获取流程实例
        taskService.claim(taskId, userId);
        taskService.complete(taskId);
        //获取任务
		/*
		 * Task task=taskService.createTaskQuery().taskId(taskId).singleResult();
		 * //获取流程实例ID String proInsId = task.getProcessInstanceId(); //获取流程实例
		 * ProcessInstance process =
		 * runtimeService.createProcessInstanceQuery().processInstanceId(proInsId).
		 * singleResult(); //获取业务外键 String business_key = process.getBusinessKey();
		 * String[] array = business_key.split(":"); String business_Id = array[1];
		 * //业务处理 try { Class clazz=BusinessTaskUtil.class; Object obj =
		 * clazz.newInstance(); Method method =
		 * clazz.getMethod("actBusiness_"+task.getFormKey(),String.class,String.class,
		 * String.class); method.invoke(obj,userId,business_Id,result); }catch
		 * (Exception e){ e.printStackTrace(); logger.error("执行业务方法错误！");
		 * 
		 * }
		 */
	}

	@Override
	public void updateBizStatus(DelegateExecution execution, String status) {
		// TODO Auto-generated method stub
		
	}

	 /**
     *	* 流程图高亮显示
     *	* 首先启动流程，获取processInstanceId，替换即可生成
     * @throws Exception
     */
	@Override
	public String queryProHighLighted(String processInstanceId) throws Exception {
		// TODO Auto-generated method stub
		//获取历史流程实例
        HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
 
        ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
 
        List<HistoricActivityInstance> highLightedActivitList =  historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        //高亮环节id集合
        List<String> highLightedActivitis = new ArrayList<String>();
 
        //高亮线路id集合
        List<String> highLightedFlows = getHighLightedFlows(definitionEntity,highLightedActivitList);
 
        for(HistoricActivityInstance tempActivity : highLightedActivitList){
            String activityId = tempActivity.getActivityId();
            highLightedActivitis.add(activityId);
        }
        //配置字体
        InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis, highLightedFlows,"宋体","微软雅黑","黑体",null,2.0);
        BufferedImage bi = ImageIO.read(imageStream);
//        File file = new File("demo2.png");
//        if(!file.exists()) file.createNewFile();
//        FileOutputStream fos = new FileOutputStream(file);
        ByteArrayOutputStream bos= new ByteArrayOutputStream();
        ImageIO.write(bi, "png", bos);
        byte[] bytes = bos.toByteArray();//转换成字节
        BASE64Encoder encoder = new BASE64Encoder();
        String png_base64 =  encoder.encodeBuffer(bytes);//转换成base64串
        png_base64 = png_base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
        bos.close();
        imageStream.close();
        return png_base64;
	}
	
	@Override
	public List findOutComeListByTaskId(String taskId) {
	      //返回存放连线的名称集合
	      List list = new ArrayList();
	      //1:使用任务ID，查询任务对象
	      Task task = taskService.createTaskQuery()//
	               .taskId(taskId)//使用任务ID查询
	               .singleResult();
	      //2：获取流程定义ID
	      String processDefinitionId = task.getProcessDefinitionId();
	      //3：查询ProcessDefinitionEntiy对象
	      ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
	      //使用任务对象Task获取流程实例ID
	      String processInstanceId = task.getProcessInstanceId();
	      //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
	      ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
	               .processInstanceId(processInstanceId)//使用流程实例ID查询
	               .singleResult();
	      //获取当前活动的id
	      String activityId = pi.getActivityId();
	      //4：获取当前的活动
	      ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
	      //5：获取当前活动完成之后连线的名称
	      List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
	      if(pvmList!=null && pvmList.size()>0){
	         for(PvmTransition pvm:pvmList){
	        	TransitionImpl transitionImpl = (TransitionImpl) pvm;
	        	ActivityImpl dest = transitionImpl.getDestination();
	        	System.out.println(dest.getId()+"  ----   "+dest.getProperty("name"));
	            String name = (String) pvm.getProperty("name");
	            if(StringUtils.isNotBlank(name)){
	               list.add(name);
	            }
	            else{
	               list.add("默认提交");
	            }
	         }
	      }
	      return list;
	    }
	
	/**
     *	* 获取需要高亮的线
     * @param processDefinitionEntity
     * @param historicActivityInstances
     * @return
     */
    private List<String> getHighLightedFlows(
            ProcessDefinitionEntity processDefinitionEntity,
            List<HistoricActivityInstance> historicActivityInstances) {
 
        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
            ActivityImpl activityImpl = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i)
                            .getActivityId());// 得到节点定义的详细信息
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1)
                            .getActivityId());
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances
                        .get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances
                        .get(j + 1);// 后续第二个节点
                if (activityImpl1.getStartTime().equals(
                        activityImpl2.getStartTime())) {
                    // 如果第一个节点和第二个节点开始时间相同保存
                    ActivityImpl sameActivityImpl2 = processDefinitionEntity
                            .findActivity(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {
                    // 有不相同跳出循环
                    break;
                }
            }
            List<PvmTransition> pvmTransitions = activityImpl
                    .getOutgoingTransitions();// 取出节点的所有出去的线
            for (PvmTransition pvmTransition : pvmTransitions) {
                // 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition
                        .getDestination();
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }

	@Override
	public List<Task> findPendingTasks(String bizKey) {
		// TODO Auto-generated method stub
		List<Task> tasks = taskService.createTaskQuery().processDefinitionKey(bizKey).taskUnassigned().orderByTaskCreateTime().desc().list();
		return tasks;
	}

	@Override
	public List<Task> findPendingTasksByUserId(String userId) {
		// TODO Auto-generated method stub
		List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(userId).orderByTaskCreateTime().desc().list();
		return tasks;
	}

	@Override
	public void claimTask(String taskId, String userId) {
		// TODO Auto-generated method stub
		
		//一旦被userId claim后不能再次被其他人claim
		taskService.claim(taskId, userId);
	}

	@Override
	public void assignTask(String taskId, String userId) {
		// TODO Auto-generated method stub
		//一旦被userId claim或者assignee后还可继续被assign到其他userId
		taskService.setAssignee(taskId, userId);
	}

	@Override
	public void completeTask(String taskId, Map<String,Object> variables) {
		// TODO Auto-generated method stub
		//任务没有分配人也可以结束
		taskService.complete(taskId, variables);
	}

	@Override
	public void jumpToTask(String executionIdOrProcInsId, String sourceNodeId, String targetNodeId, Map<String, Object> params,
			String reason) throws Exception {
		// TODO Auto-generated method stub
		//NodeJumpTaskCmdUtil.jumpTask(processEngine, params, executionIdOrProcInsId, targetNodeId, sourceNodeId);
		NodeJumpTaskCmdUtil.jumpTask(processEngine, params, executionIdOrProcInsId, targetNodeId);
	}

	@Override
	public Task findTaskByExecId(String execId) {
		// TODO Auto-generated method stub
		List<Task> tasks = taskService.createTaskQuery().executionId(execId).orderByTaskCreateTime().desc().list();
		if(tasks == null || tasks.size() == 0) {
			return null;
		}
		return tasks.get(0);
	}

	@Override
	public ProcessInstance startProcesses(String key, String businessKey,Map<String, Object> variables) {
		// TODO Auto-generated method stub
		ProcessInstance instance = runtimeService.startProcessInstanceByKey(key, businessKey,variables);
		return instance;
	}

}
