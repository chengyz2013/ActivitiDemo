package com.demo.ActivitiDemo.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.demo.ActivitiDemo.bean.FormData;

@RestController
@RequestMapping(value = "/test")
public class TestController {
	
	@RequestMapping(value = "/save",method = RequestMethod.POST)
	@ResponseBody
	public FormData save(@RequestParam("userId")String userId,@RequestBody FormData formData){
		System.out.println("----userId:"+userId);
		System.out.println("------"+formData);
		//formData.setTestBigDecimal(new BigDecimal(4.544222));
		System.out.println("------"+formData);
		return formData;
	}
	
	@RequestMapping(value = "/test",method = RequestMethod.GET)
	@ResponseBody
	public String test(){
		System.out.println("----test--");
		return "success";
	}

}
