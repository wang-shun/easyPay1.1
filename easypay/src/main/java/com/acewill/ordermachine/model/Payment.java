package com.acewill.ordermachine.model;

import org.litepal.crud.DataSupport;

/**
 * Author：Anch
 * Date：2017/11/28 12:49
 * Desc：
 */

public class Payment extends DataSupport {
	private String paymentTypeId;// ":1, 支付类型
	private String openid;// 支付宝或者微信支付的时候才具有这个openid
	private String paymentNo;//  支付流水号，无论何种支付方式，都用同一个paymentNo就行了，用于退款
	private String value;//该种支付方式支付了多少钱
	private String createdAt;//下单时间
	private String paidAt;//结账时间
	private String paymentTypeName;//
	private String transaction_no;//

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	private String operation;//

	public OrderReq getOrderreq() {
		return orderreq;
	}

	public void setOrderreq(OrderReq orderreq) {
		this.orderreq = orderreq;
	}

	private OrderReq orderreq;
	//需要变的 是以上的两个参数  openid 和 paymentTypeId


	public String getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(String paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(String paidAt) {
		this.paidAt = paidAt;
	}

	public String getPaymentTypeName() {
		return paymentTypeName;
	}

	public void setPaymentTypeName(String paymentTypeName) {
		this.paymentTypeName = paymentTypeName;
	}

	public String getTransaction_no() {
		return transaction_no;
	}

	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
	}
}