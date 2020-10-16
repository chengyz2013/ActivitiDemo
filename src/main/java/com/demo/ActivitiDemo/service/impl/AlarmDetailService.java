package com.demo.ActivitiDemo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.ActivitiDemo.bean.AlarmDetailDTO;
import com.demo.ActivitiDemo.bean.AlarmParams;
import com.demo.ActivitiDemo.mapper.AlarmDetailMapper;

@Service
public class AlarmDetailService {
	@Autowired
	private AlarmDetailMapper alarmDetailMapper;
	
	public List<AlarmDetailDTO> listDetails(){
		return alarmDetailMapper.listDetails();
	}

	public AlarmDetailDTO insertAlarmDetail(AlarmDetailDTO dto) {
		int id = alarmDetailMapper.insertAlarmDetail(dto);
		System.out.println("插入主键是："+id);
		return dto;
	}
	
	public int updateAlarmDetail(AlarmDetailDTO dto) {
		int num = alarmDetailMapper.updateAlarmDetail(dto);
		System.out.println("更新条目数："+num);
		return num;
	}
	
	public AlarmDetailDTO loadById(int id) {
		AlarmDetailDTO dto = alarmDetailMapper.loadById(id);
		return dto;
	}
	
	public List<AlarmDetailDTO> queryByParams(AlarmParams params) {
		List<AlarmDetailDTO> dtos = alarmDetailMapper.queryByParams(params);
		return dtos;
	}
	
	public AlarmDetailDTO queryByExecId(String execId){
		List<AlarmDetailDTO> dtos = alarmDetailMapper.queryByExecId(execId);
		if(dtos == null || dtos.size() == 0) {
			return null;
		}
		return dtos.get(0);
	}
}
