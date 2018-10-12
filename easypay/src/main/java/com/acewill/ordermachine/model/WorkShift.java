package com.acewill.ordermachine.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;


public class WorkShift extends DataSupport implements Serializable {
	private int   id;//班次id
	private int    userId;
	private String userName;//这个是手机号码
	private String realName;
	private int    terminalId;
	private String terminalName;
	private int    definitionId; //班次定义的id，
	private String definitionName; //班次定义的名称，
	private String spareCash; //备用金
	private String cashRevenue; //现金收入 （钱箱里除去备用金意外的收入）
	private long   startTime;

	public String getWorkShiftDetail() {
		return workShiftDetail;
	}

	public void setWorkShiftDetail(String workShiftDetail) {
		this.workShiftDetail = workShiftDetail;
	}

	private String workShiftDetail;

	public String getStatuStr() {
		return statuStr;
	}

	public void setStatuStr(String statuStr) {
		this.statuStr = statuStr;
	}

	private String statuStr;

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	private Long    endTime; // new Gson().toJson();这个方法，如果字段是java的基本数据类型就会自动赋值，也就是说long 是会有一个0 的
	private boolean createdOffline; //是否为网络断开时本地创建的？
	private String  startTimeStr;//开班时间
	private String  endTimeStr;//交班时间


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public int getTerminalId() {
		return terminalId;
	}

	public void setTerminalId(int terminalId) {
		this.terminalId = terminalId;
	}

	public String getTerminalName() {
		return terminalName;
	}

	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	public int getDefinitionId() {
		return definitionId;
	}

	public void setDefinitionId(int definitionId) {
		this.definitionId = definitionId;
	}

	public String getDefinitionName() {
		return definitionName;
	}

	public void setDefinitionName(String definitionName) {
		this.definitionName = definitionName;
	}

	public String getSpareCash() {
		return spareCash;
	}

	public void setSpareCash(String spareCash) {
		this.spareCash = spareCash;
	}

	public String getCashRevenue() {
		return cashRevenue;
	}

	public void setCashRevenue(String cashRevenue) {
		this.cashRevenue = cashRevenue;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}


	public boolean isCreatedOffline() {
		return createdOffline;
	}

	public void setCreatedOffline(boolean createdOffline) {
		this.createdOffline = createdOffline;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

}
