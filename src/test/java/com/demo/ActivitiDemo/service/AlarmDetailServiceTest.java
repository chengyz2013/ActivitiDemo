package com.demo.ActivitiDemo.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.demo.ActivitiDemo.bean.AlarmDetailDTO;
import com.demo.ActivitiDemo.bean.AlarmParams;
import com.demo.ActivitiDemo.bean.AlarmTaskVO;
import com.demo.ActivitiDemo.service.impl.AlarmDetailService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AlarmDetailServiceTest {
	
	@Autowired
	private AlarmDetailService alarmDetailService;
	
	@Autowired
	private AlarmTaskService alarmTaskService;
	
	@Test
	public void insertAlarmDetail() {
		/*
		 * AlarmDetailDTO dto = new AlarmDetailDTO();
		 * dto.setSerialNo("2020002testdfsdfdsfsd"); dto.setContractNo("110099test");
		 * dto.setProduct("cu"); dto.setAlarmDesc("首次测试");
		 */
		
		
		AlarmDetailDTO dto = new AlarmDetailDTO();
		dto.setSerialNo("20200302testds2424sd");
		dto.setContractNo("1545099test");
		dto.setProduct("ag");
		dto.setAlarmDesc("测试二");
		
		alarmDetailService.insertAlarmDetail(dto);
	}
	
	@Test
	public void listDetails() {
		List<AlarmDetailDTO> lists = alarmDetailService.listDetails();
		for(AlarmDetailDTO dto : lists) {
			System.out.println("dto:----" + dto.getAlarmDesc());
		}
	}
	
	@Test
	public void updateAlarmDetail() {
		AlarmDetailDTO dto = new AlarmDetailDTO();
		dto.setId(1);
		dto.setUpdateUser("cccc");
		
		alarmDetailService.updateAlarmDetail(dto);
	}
	
	@Test
	public void loadById() {
		AlarmDetailDTO detailDTO = alarmDetailService.loadById(2);
		System.out.println("detailDTO:---"+detailDTO.getAlarmDesc());
	}
	
	@Test
	public void queryByParams() {
		AlarmParams params = new AlarmParams();
		params.setSerialNo("2020002testdfsdfdsfsd");
		params.setContractNo("110099test1");
		List<AlarmDetailDTO> lists = alarmDetailService.queryByParams(params);
		for(AlarmDetailDTO dto : lists) {
			System.out.println("dto:----" + dto.getAlarmDesc());
		}
	}
	
	@Test
	public void queryByExecId() {
		AlarmDetailDTO detailDTO = alarmDetailService.queryByExecId("1111");
		System.out.println("detailDTO:---"+detailDTO.getAlarmDesc());
	}
	
	@Test
	public void listUserTask() throws Exception {
		String userId = "小二";
		List<AlarmTaskVO> retList = alarmTaskService.listUndoneTask(userId);
		if(retList != null) {
			for(AlarmTaskVO vo : retList) {
				System.out.println("AlarmDesc:--"+vo.getAlarmDesc()
				+"SerialNo:--"+vo.getSerialNo()
				+"NodeState:--"+vo.getNodeState());
			}
		}
	}
}
