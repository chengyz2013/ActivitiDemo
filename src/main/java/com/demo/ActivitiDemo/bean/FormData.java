package com.demo.ActivitiDemo.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
public class FormData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String userId;
	private String attr1;
	private String attr2;
	
	@JsonSerialize(using = Decimal2Serializer.class)
	BigDecimal testBigDecimal;
	
	public static void main(String[] args) {
		String a = "  -1-";
		String b = "-1-  ";
		String c = "-1 -";
		
		System.out.println(a);
	}
	
	public void setTestBigDecimal(BigDecimal b){
		if(b == null) {
			testBigDecimal = BigDecimal.ZERO;
		}else {
			testBigDecimal = b;
		}
		
		testBigDecimal = testBigDecimal.setScale(3, RoundingMode.HALF_UP);
		System.out.println("set b :----"+b);
		System.out.println("set testBigDecimal :----"+testBigDecimal);
	}

	/*
	 * public String getId() { return id; }
	 * 
	 * public void setId(String id) { this.id = id; }
	 * 
	 * public String getUserId() { return userId; }
	 * 
	 * public void setUserId(String userId) { this.userId = userId; }
	 * 
	 * public String getAttr1() { return attr1; }
	 * 
	 * public void setAttr1(String attr1) { this.attr1 = attr1; }
	 * 
	 * public String getAttr2() { return attr2; }
	 * 
	 * public void setAttr2(String attr2) { this.attr2 = attr2; }
	 * 
	 * public BigDecimal getTestBigDecimal() { return testBigDecimal; }
	 */

	/*
	 * public void setTestBigDecimal(BigDecimal testBigDecimal) {
	 * this.testBigDecimal = testBigDecimal; }
	 */
	
	
	
	
}
