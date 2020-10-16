package com.demo.ActivitiDemo.service;

import java.util.List;

import com.demo.ActivitiDemo.bean.AlarmTaskVO;

public interface AlarmTaskService {
	public List<AlarmTaskVO> listAllTask() throws Exception;
	public List<AlarmTaskVO> listUserTask(String userId) throws Exception;
	public List<AlarmTaskVO> listUndoneTask(String userId) throws Exception;
	public List<AlarmTaskVO> listDoneTask(String userId) throws Exception;
	
	/**
	 * 处理人选择该笔件，进行后续处理
	 * @param userId
	 * @param execId
	 * @param serialNo（execId和serialNo必填其一）
	 * @return
	 * @throws Exception
	 */
	public String claimAlarmTask(String userId,String execId,String serialNo) throws Exception;

	public String startAlarmProcess(String key, AlarmTaskVO taskVO) throws Exception;
}
