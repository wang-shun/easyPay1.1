package com.acewill.ordermachine.pos;

import java.util.ArrayList;
import java.util.List;

public class Order 
{
	public static class Item
	{
		public String dishName;
		public String dishMemo = "";
		public int quantity;
		public float sumOfMoney;
	}
	
	public String	orderNo = "";
	
	public String restaurantName = "";
	
	public String telNo = "";
	
	public boolean bTakeOut = false;
	
	public List<Item> lstOfItems = new ArrayList<Item>();
	
	public float discountMoney = (float) 0.0;
	
	public String tips = "";
	
	public String orderDishORCode = "";
	
	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public float getDiscountMoney() {
		return discountMoney;
	}

	public void setDiscountMoney(float discountMoney) {
		this.discountMoney = discountMoney;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public List<Item> getLstOfItems() {
		return lstOfItems;
	}

	public void setLstOfItems(List<Item> lstOfItems) {
		this.lstOfItems = lstOfItems;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	
}
