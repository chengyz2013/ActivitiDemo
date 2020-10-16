package com.demo.ActivitiDemo.bean;

import java.util.Date;

public class TaskLinkBizDTO {
	private String bizId;
	private String bizType;
	private String bizState;
	private String procInstId;
	private String execId;
	private Date cTime;
	private String mUser;
	private Date mTime;
	public String getBizId() {
		return bizId;
	}
	public void setBizId(String bizId) {
		this.bizId = bizId;
	}
	public String getBizType() {
		return bizType;
	}
	public void setBizType(String bizType) {
		this.bizType = bizType;
	}
	
	
	
}
