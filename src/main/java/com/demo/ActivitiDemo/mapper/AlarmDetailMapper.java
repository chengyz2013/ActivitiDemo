package com.demo.ActivitiDemo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.demo.ActivitiDemo.bean.AlarmDetailDTO;
import com.demo.ActivitiDemo.bean.AlarmParams;

@Mapper
public interface AlarmDetailMapper {
	public int insertAlarmDetail(AlarmDetailDTO dto);
	public List<AlarmDetailDTO> listDetails();
	public int updateAlarmDetail(AlarmDetailDTO dto);
	public List<AlarmDetailDTO> queryByParams(AlarmParams params);
	public AlarmDetailDTO loadById(int id);
	public List<AlarmDetailDTO> queryByExecId(String execId);
}
