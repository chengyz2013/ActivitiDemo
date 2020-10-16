package com.demo.ActivitiDemo.bean.kafka;

import com.demo.ActivitiDemo.bean.kafka.MsgField.FillSide;

public class TestBody extends MsgPiece{
	private static final MsgField[] items = new MsgField[]{
	        new MsgField("content", 20, ' ',FillSide.RIGHT),
	    };
	    
	    public TestBody() {
	        super(items);
	    }
	    
	    private String content;

	    public String getContent() {
	        return content;
	    }

	    public void setContent(String content) {
	        this.content = content;
	    }


}
