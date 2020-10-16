package com.demo.ActivitiDemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.demo.ActivitiDemo.bean.AlarmDetailDTO;
import com.demo.ActivitiDemo.service.impl.AlarmDetailService;

@RestController
@RequestMapping(value = "alarmDetails")
public class AlarmDetailsController {
	@Autowired
	private AlarmDetailService alarmDetailService;
	
	@RequestMapping(value = "/insert",method = RequestMethod.POST)
	public AlarmDetailDTO insert(AlarmDetailDTO dto) {
		return alarmDetailService.insertAlarmDetail(dto);
	}
	
	@RequestMapping(value = "/list",method = RequestMethod.GET)
	public List<AlarmDetailDTO> list(){
		return alarmDetailService.listDetails();
	}
	
	@RequestMapping(value = "update",method = RequestMethod.POST)
	public Integer update(AlarmDetailDTO dto) {
		return alarmDetailService.updateAlarmDetail(dto);
	}
}
