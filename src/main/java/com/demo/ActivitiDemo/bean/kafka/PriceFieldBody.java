package com.demo.ActivitiDemo.bean.kafka;

public class PriceFieldBody {
	private char[] trading_day = new char[8];
	private char[] product_id = new char[8];
	private Float spot_price;
	private Integer spot_volume;
	private char[] primaryinst_id = new char[30];
	private Float primaryinst_closeprice;
	private Float primaryinst_pricechangeratio;
	private Float overseas_closeprice;
	private Float overseas_pricechangeratio;
	private Float exchoverseas_closeprice;
	private Float exchoverseas_pricechangeratio;
	
	

}
