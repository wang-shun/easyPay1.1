package com.acewill.ordermachine.model;

/**
 * Author：Anch
 * Date：2017/11/30 11:39
 * Desc：
 */
public class RefundReasonBean {

	/**
	 * id : 41
	 * reason : TUICAI
	 * appid : 59841423
	 * reasonType : 退菜原因
	 * seq : 1
	 * retreatCount : 0
	 * retreatCountStr : 否
	 */

	private int    id;
	private String reason;
	private String appid;
	private String reasonType;
	private int    seq;
	private int    retreatCount;
	private String retreatCountStr;

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	private boolean isSelect;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getReasonType() {
		return reasonType;
	}

	public void setReasonType(String reasonType) {
		this.reasonType = reasonType;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getRetreatCount() {
		return retreatCount;
	}

	public void setRetreatCount(int retreatCount) {
		this.retreatCount = retreatCount;
	}

	public String getRetreatCountStr() {
		return retreatCountStr;
	}

	public void setRetreatCountStr(String retreatCountStr) {
		this.retreatCountStr = retreatCountStr;
	}
}
