package com.demo.ActivitiDemo.bean.kafka;

import com.demo.ActivitiDemo.bean.kafka.MsgField.FillSide;

public class TestHead extends MsgPiece {
	
	private String name;
    private boolean sex;
    private int UAge;
    private String URL;
    private double amt;

    public int getUAge() {
        return UAge;
    }

    public void setUAge(int uAge) {
        UAge = uAge;
    }
    
    public double getAmt() {
        return amt;
    }

    public void setAmt(double amt) {
        this.amt = amt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

    private static final MsgField[] items = new MsgField[]{
            new MsgField("name", 10, ' ', FillSide.RIGHT),
            new MsgField("sex", 10, '0', FillSide.LEFT),
            new MsgField("UAge", 10, '0', FillSide.LEFT),
            new MsgField("URL", 10, ' ', FillSide.RIGHT),
            new MsgField("amt", 10, '0', FillSide.LEFT)
        };

	public TestHead() {
		super(items);
		// TODO Auto-generated constructor stub
	}

}
