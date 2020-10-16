package com.demo.ActivitiDemo.bean.kafka;

import com.demo.ActivitiDemo.bean.kafka.MsgField.FillSide;

public class FtdHead extends MsgPiece {
	
	private String type = "0";
	private String ext = "0";
	private int length;
	
	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getExt() {
		return ext;
	}



	public void setExt(String ext) {
		this.ext = ext;
	}



	public int getLength() {
		return length;
	}



	public void setLength(int length) {
		this.length = length;
	}



	public void setLength(short length) {
		this.length = length;
	}


	private static final MsgField[] items = new MsgField[]{
            new MsgField("type", 1, '0', FillSide.LEFT),
            new MsgField("ext", 1, '0', FillSide.LEFT),
            new MsgField("length", 2, '0', FillSide.LEFT)
        };
	

	public FtdHead() {
		super(items);
		// TODO Auto-generated constructor stub
	}

}
