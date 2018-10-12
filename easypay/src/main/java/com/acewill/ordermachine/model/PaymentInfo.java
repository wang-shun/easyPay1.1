package com.acewill.ordermachine.model;

/**
 * 新pos支付信息接口模型
 * 
 * @author hzc
 * 
 */
public class PaymentInfo {
	
	public int id;// : 支付类型的ID （1：支付宝；2：微信）
	public String name;// ：支付类型的名称
	public String appIDs;//
	public String keyStr;//
	public String mchID;//
	public String subMchID;//
	public String appsecret;//
	public int status;
	public boolean cashbox;

	@Override
	public String toString() {
		return "PaymentInfo{" +
				"id=" + id +
				", name='" + name + '\'' +
				", appIDs='" + appIDs + '\'' +
				", keyStr='" + keyStr + '\'' +
				", mchID='" + mchID + '\'' +
				", subMchID='" + subMchID + '\'' +
				", appsecret='" + appsecret + '\'' +
				", status=" + status +
				'}';
	}
}
