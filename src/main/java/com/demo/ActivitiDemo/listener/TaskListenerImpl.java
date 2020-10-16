package com.demo.ActivitiDemo.listener;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.demo.ActivitiDemo.bean.AlarmDetailDTO;
import com.demo.ActivitiDemo.command.SpringUtil;
import com.demo.ActivitiDemo.service.impl.AlarmDetailService;

public class TaskListenerImpl implements TaskListener{
	
	private AlarmDetailService alarmDetailService;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		// TODO Auto-generated method stub
		String eventName = delegateTask.getEventName();
		
		alarmDetailService = SpringUtil.getBean("alarmDetailService");
		
		List<AlarmDetailDTO> lists = alarmDetailService.listDetails();
		for(AlarmDetailDTO dto : lists) {
			System.out.println("dto:----" + dto.getAlarmDesc());
		}
		
		System.out.println("TaskListenerImpl==========eventName=========="+eventName);
		System.out.println("TaskListenerImpl==========name=========="+delegateTask.getName());
		System.out.println("TaskListenerImpl==========taskid=========="+delegateTask.getId());
		System.out.println("TaskListenerImpl==========DefinitionKey=========="+delegateTask.getTaskDefinitionKey());
		System.out.println("TaskListenerImpl==========assignee=========="+delegateTask.getAssignee());
		System.out.println("TaskListenerImpl==========Candidates()=========="+delegateTask.getCandidates());
		
		Set<IdentityLink> candidates = delegateTask.getCandidates();
		
			for(IdentityLink id : candidates) {
				System.out.println("groupid:------"+id.getGroupId()+"  userid:"+id.getUserId());
				if(StringUtils.isNoneBlank(id.getGroupId())) {
					delegateTask.addCandidateUsers(Arrays.asList("gwUser2", "test4", "test5"));
				}
			}
		
	}

}
