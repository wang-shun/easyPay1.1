package com.acewill.ordermachine.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderReq extends DataSupport implements Serializable {
	private int    myid;
	private int    oid;
	private String total;// " : 30.2,
	private String cost;// " : 10.2,
	private String source;// " : "点餐机1",
	private String discount;// " : 0.78, //折扣率
	private String comment;// " : "自动生成订单",
	private String createdBy;// " : "服务员1",
	private String customerAmount;// " : 2,
	private String subtraction;// " : 10, //全单减10块
	private String orderType;// " : "EAT_IN", 或者 " TAKE_OUT",
	private String paymentStatus;// " : "PAYED", 或者"NOT_PAYED", 如果是PAYED,
	private String paymentTypeName;// " : "PAYED", 或者"NOT_PAYED", 如果是PAYED,
	// 那么需要带上paymentList.具体内容看订单结账接口
	private String createdAt;//

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	private String realname;//


	public int getIsRefundMoney() {
		return isRefundMoney;
	}

	public void setIsRefundMoney(int isRefundMoney) {
		this.isRefundMoney = isRefundMoney;
	}

	//是否已经退了钱,默认已退单退钱 ，已退钱为0 ，不退钱为1
	private int isRefundMoney;

	public long getCreatedtime() {
		return createdtime;
	}

	public void setCreatedtime(long createdtime) {
		this.createdtime = createdtime;
	}

	private long            createdtime;
	// 那么需要带上paymentList.具体内容看订单结账接口
	private long            tableId;
	private List<DishModel> itemList;
	private List<Payment>   paymentList;
	private String          paidAt;
	private long            id;
	private String          bis_id;
	private int             upload;//标记该订单是否已经上传
	private String          memberid; //对应微生活账号中的uid字段
	private String          memberName;//对应微生活账号中的name字段
	private String          memberPhoneNumber;//对应微生活账号中的phone字段
	private String          memberBizId;// String类型  微生活业务id
	private String          memberGrade;//会员等级名
	private long            refundTime;//退款时间
	private String          refundMoney;//退款金额

	public int getWorkShiftId() {
		return workShiftId;
	}

	public void setWorkShiftId(int workShiftId) {
		this.workShiftId = workShiftId;
	}

	public int getTerminalOrderId() {
		return terminalOrderId;
	}

	public void setTerminalOrderId(int terminalOrderId) {
		this.terminalOrderId = terminalOrderId;
	}

	private int workShiftId;//pos端下单要绑定班次
	private int printStatu;
	private int terminalOrderId;

	public long getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(long refundTime) {
		this.refundTime = refundTime;
	}

	public String getRefundMoney() {
		return refundMoney;
	}

	public void setRefundMoney(String refundMoney) {
		this.refundMoney = refundMoney;
	}

	private String authorityName;//授权退款人的名称


	public String getAuthorityName() {
		return authorityName;
	}

	public void setAuthorityName(String authorityName) {
		this.authorityName = authorityName;
	}


	public String getUserphone() {
		return userphone;
	}

	public void setUserphone(String userphone) {
		this.userphone = userphone;
	}

	private String userphone;// 这个订单是有哪个收银员收的,这个是登录的收银员的手机号


	public int getOid() {
		return oid;
	}

	public void setOid(int oid) {
		this.oid = oid;
	}

	public int getMyid() {
		return myid;
	}

	public void setMyid(int myid) {
		this.myid = myid;
	}

	public String getPaymentTypeName() {
		return paymentTypeName;
	}

	public void setPaymentTypeName(String paymentTypeName) {
		this.paymentTypeName = paymentTypeName;
	}


	public OrderReq() {
		itemList = new ArrayList<>();
		paymentList = new ArrayList<>();
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCustomerAmount() {
		return customerAmount;
	}

	public void setCustomerAmount(String customerAmount) {
		this.customerAmount = customerAmount;
	}

	public String getSubtraction() {
		return subtraction;
	}

	public void setSubtraction(String subtraction) {
		this.subtraction = subtraction;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public long getTableId() {
		return tableId;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	public List<DishModel> getItemList() {
		return itemList;
	}

	public void setItemList(List<DishModel> itemList) {
		this.itemList = itemList;
	}

	public List<Payment> getPaymentList() {
		return paymentList;
	}

	public void setPaymentList(List<Payment> paymentList) {
		this.paymentList = paymentList;
	}

	public String getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(String paidAt) {
		this.paidAt = paidAt;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBis_id() {
		return bis_id;
	}

	public void setBis_id(String bis_id) {
		this.bis_id = bis_id;
	}

	public int getUpload() {
		return upload;
	}

	public void setUpload(int upload) {
		this.upload = upload;
	}

	public String getMemberid() {
		return memberid;
	}

	public void setMemberid(String memberid) {
		this.memberid = memberid;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberPhoneNumber() {
		return memberPhoneNumber;
	}

	public void setMemberPhoneNumber(String memberPhoneNumber) {
		this.memberPhoneNumber = memberPhoneNumber;
	}

	public String getMemberBizId() {
		return memberBizId;
	}

	public void setMemberBizId(String memberBizId) {
		this.memberBizId = memberBizId;
	}

	public String getMemberGrade() {
		return memberGrade;
	}

	public void setMemberGrade(String memberGrade) {
		this.memberGrade = memberGrade;
	}


	public int getPrintStatu() {
		return printStatu;
	}

	public void setPrintStatu(int printStatu) {
		this.printStatu = printStatu;
	}

	private String printName;


	public String getPrintName() {
		return printName;
	}

	public void setPrintName(String printName) {
		this.printName = printName;
	}

	private String uActualNo;


	public String getuActualNo() {
		return uActualNo;
	}

	public void setuActualNo(String uActualNo) {
		this.uActualNo = uActualNo;
	}
}
