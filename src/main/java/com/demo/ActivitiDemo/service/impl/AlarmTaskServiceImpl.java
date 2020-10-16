package com.demo.ActivitiDemo.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.demo.ActivitiDemo.bean.AlarmDetailDTO;
import com.demo.ActivitiDemo.bean.AlarmParams;
import com.demo.ActivitiDemo.bean.AlarmTaskVO;
import com.demo.ActivitiDemo.service.AlarmTaskService;
import com.demo.ActivitiDemo.service.ProcessService;

@Service
public class AlarmTaskServiceImpl implements AlarmTaskService {
	
	private static final String SUCCESS = "SUCCESS";
	private static final String DEFAULT_KEY = "testAlarmProcess";
	@Autowired
	private ProcessService processService;
	
	@Autowired
	private AlarmDetailService alarmDetailService;


	@Override
	public List<AlarmTaskVO> listAllTask() {
		// TODO Auto-generated method stub
		/**
		 * 
		 */
		//#todo 加载所有任务，待完善
		return null;
	}

	@Override
	public List<AlarmTaskVO> listUserTask(String userId) throws Exception{
		// TODO Auto-generated method stub
		//#todo 加载该用户所有任务，待完善
		return null;
	}

	private void addResultList(List<AlarmTaskVO> resList, Task task, AlarmDetailDTO alarmDetail) {
		// TODO Auto-generated method stub
		AlarmTaskVO vo = new AlarmTaskVO();
		vo.setAlarmDate(alarmDetail.getAlarmDate());
		vo.setAlarmDesc(alarmDetail.getAlarmDesc());
		vo.setAlarmTime(alarmDetail.getAlarmTime());
		vo.setAlarmType(alarmDetail.getAlarmType());
		vo.setContractNo(alarmDetail.getContractNo());
		vo.setHandleState(alarmDetail.getHandleState());
		vo.setInstanceId(alarmDetail.getInstanceId());
		vo.setExecId(alarmDetail.getExecId());
		vo.setNodeState(task.getName());
		vo.setProduct(alarmDetail.getProduct());
		vo.setSerialNo(alarmDetail.getSerialNo());
		
		resList.add(vo);
	}

	@Override
	public List<AlarmTaskVO> listUndoneTask(String userId) throws Exception{
		// TODO Auto-generated method stub
		List<AlarmTaskVO> resList  = new ArrayList<AlarmTaskVO>();
		
		List<Task> tasks = processService.findTasksByUserId(userId);
		if(tasks == null || tasks.size() == 0) {
			return resList;
		}
		
		for(Task task : tasks) {
			//executionId 跟 processInstanceId理论上应该是一样的，除非有分支任务出现
			//为防止后续需求扩展，用executionId比较合理
			//不影响正常业务查询
			String executionId = task.getExecutionId();
			
			AlarmDetailDTO alarmDetail =  alarmDetailService.queryByExecId(executionId);
			if(alarmDetail == null) {
				continue;
			}
			addResultList(resList,task,alarmDetail);
		}
		
		return resList;
	}

	@Override
	public List<AlarmTaskVO> listDoneTask(String userId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String claimAlarmTask(String userId, String execId, String serialNo) throws Exception {
		// TODO Auto-generated method stub
		
		if(StringUtils.isBlank(userId)) {
			String msg = "处理人userId为空！(userId="+userId+")";
			System.out.println(msg);
			throw new Exception(msg);
		}
		
		String execIdtmp = execId;
		
		if(StringUtils.isBlank(execIdtmp)) {
			 if(StringUtils.isBlank(serialNo)){
				String msg = "该笔件execId和serialNo都为空！";
				System.out.println(msg);
				throw new Exception(msg);
			 }else {
				 AlarmParams queryParams = new AlarmParams();
				 queryParams.setSerialNo(serialNo);
				 List<AlarmDetailDTO> dtos = alarmDetailService.queryByParams(queryParams);
				 if(dtos == null || dtos.size() == 0) {
					 System.out.println("该笔件不存在！serialNo:"+serialNo);
				 }else if(dtos.size() != 1) {
					 System.out.println("该笔件的报警编号对应多笔件！serialNo:"+serialNo);	 
				 }else {
					 AlarmDetailDTO dto = dtos.get(0);
					 execIdtmp = dto.getExecId();
					 if(StringUtils.isBlank(execIdtmp)) {
						 System.out.println("该笔件serialNo不存在对应的execId,serialNo:"+serialNo);
					 }
				 } 
			 }	
		}
		
		
		
		Task task = processService.findTaskByExecId(execIdtmp);
		if(task == null) {
			String msg = "运行中的流程任务查无此execId！(execId="+execIdtmp+")";
			System.out.println(msg);
			throw new Exception(msg);
		}
		String taskId = task.getId();
		
		try {
			processService.claimTask(taskId, userId);
		}catch (Exception e) {
			// TODO: handle exception
			String msg = "用户(userId:"+userId+")申领该笔件失败！execId:"+execIdtmp;
			throw new Exception(msg);
		}
		
		return SUCCESS;
	}

	@Override
	public String startAlarmProcess(String key, AlarmTaskVO taskVO) throws Exception {
		// TODO Auto-generated method stub
		if(StringUtils.isBlank(key)) {
			return startAlarmProcessDefaultKey(key, taskVO);
		}
		ProcessInstance instance = processService.startProcesses(key,taskVO.getSerialNo(),null);
		System.out.println("发起流程成功！instanceid:"+instance.getProcessInstanceId()+" businessKey:"+instance.getBusinessKey());
		taskVO.setInstanceId(instance.getProcessInstanceId());
		taskVO.setExecId(instance.getProcessInstanceId());
		
		AlarmParams params = new AlarmParams();
		params.setSerialNo(taskVO.getSerialNo());
		
		List<AlarmDetailDTO> resDtos = alarmDetailService.queryByParams(params);
		if(resDtos == null || resDtos.size() == 0) {
			AlarmDetailDTO dto = new AlarmDetailDTO();
			vo2dto(taskVO,dto);
			alarmDetailService.insertAlarmDetail(dto);
		}else if(resDtos.size() == 1) {
			AlarmDetailDTO dto = resDtos.get(0);
			vo2dto(taskVO,dto);
			alarmDetailService.updateAlarmDetail(dto);
		}else {
			System.out.println("serialNo 对应多笔件！错误！！！");
			throw new RuntimeException("serialNo 对应多笔件！错误！！！");
		}
		
		return SUCCESS;
	}

	private void vo2dto(AlarmTaskVO taskVO, AlarmDetailDTO dto) {
		// TODO Auto-generated method stub
		dto.setAlarmDate(taskVO.getAlarmDate());
		dto.setAlarmDesc(taskVO.getAlarmDesc());
		dto.setAlarmTime(taskVO.getAlarmTime());
		dto.setAlarmType(taskVO.getAlarmType());
		dto.setContractNo(taskVO.getContractNo());
		dto.setExecId(taskVO.getExecId());
		dto.setInstanceId(taskVO.getInstanceId());
		dto.setHandleState(taskVO.getHandleState());
		dto.setNodeState(taskVO.getNodeState());
		dto.setProduct(taskVO.getProduct());
		dto.setSerialNo(taskVO.getSerialNo());
	}

	private String startAlarmProcessDefaultKey(String key, AlarmTaskVO taskVO) throws Exception {
		// TODO Auto-generated method stub
		return startAlarmProcess(DEFAULT_KEY, taskVO);
	}

}
