package com.demo.ActivitiDemo.bean;

import java.io.Serializable;

public class AlarmParams implements Serializable {
	private String serialNo;
	private String contractNo;
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	
	
}
