package com.acewill.ordermachine.model;


import com.acewill.ordermachine.common.Common;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class DishModel extends DataSupport implements Serializable {
	private String   dishName;// :菜品名称, String,
	private String   dishID;// :菜品ID, Stirng
	private String   price;// :菜品价格, decimal
	private String   dishUnit;// : 菜品单位名称，String
	private int      quantity;//菜品数量
	private String   cost;//成本
	private String   dishKindStr;
	private String   dishKind;// : 菜品分类
	private OrderReq orderreq;

	public String getDishName() {
		return dishName;
	}

	public void setDishName(String dishName) {
		this.dishName = dishName;
	}

	public String getDishID() {
		return dishID;
	}

	public void setDishID(String dishID) {
		this.dishID = dishID;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDishUnit() {
		return dishUnit;
	}

	public void setDishUnit(String dishUnit) {
		this.dishUnit = dishUnit;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}


	public String getDishKindStr() {
		return dishKindStr;
	}

	public void setDishKindStr(String dishKindStr) {
		this.dishKindStr = dishKindStr;
	}

	public String getDishKind() {
		return dishKind;
	}

	public void setDishKind(String dishKind) {
		this.dishKind = dishKind;
	}


	public OrderReq getOrderreq() {
		return orderreq;
	}

	public void setOrderreq(OrderReq orderreq) {
		this.orderreq = orderreq;
	}

}
