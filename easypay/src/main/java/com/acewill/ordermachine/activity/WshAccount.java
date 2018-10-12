package com.acewill.ordermachine.activity;

import java.io.Serializable;
import java.util.List;

/**
 * Author：Anch
 * Date：2017/5/27 13:07
 * Desc：
 */
public class WshAccount implements Serializable {


	/**
	 * uid : 61568523574414062
	 * uno : 1300575
	 * type : wx
	 * name :
	 * phone :
	 * birthday : 1970-01-01
	 * gender :
	 * registered : 2017-05-27
	 * openid : oLkhtwDRtp8sHP7xEhNAEwQx3XXo
	 * grade : 2004800
	 * gradeName : 会员卡
	 * balance : 0
	 * credit : 0
	 * usecredit : true
	 * inEffect : true
	 * uActualNo : 0
	 * coupons : [{"template_id":"2049287","coupon_ids":["1568523574925141"],"title":"2元代金券","deno":2,"type":1,"sids":[1174517085],"effective_time":"2017-05-27 00:00:00","failure_time":"2017-06-02 23:59:59","limitations":["每次消费最多可以使用1张","不可以和其他种类的券混用"],"enable_amount":0,"max_use":1,"mix_use":false}]
	 */

	private String                 uid;
	private String                 uno;
	private String                 type;
	private String                 name;
	private String                 phone;
	private String                 birthday;
	private String                 gender;
	private String                 registered;
	private String                 openid;
	private long                    grade;
	private String                 gradeName;
	private int                    balance;
	private int                    credit;
	private boolean                usecredit;
	private boolean                inEffect;
	private String                 uActualNo;
	private List<WshAccountCoupon> coupons;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUno() {
		return uno;
	}

	public void setUno(String uno) {
		this.uno = uno;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getRegistered() {
		return registered;
	}

	public void setRegistered(String registered) {
		this.registered = registered;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public long getGrade() {
		return grade;
	}

	public void setGrade(long grade) {
		this.grade = grade;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public boolean isUsecredit() {
		return usecredit;
	}

	public void setUsecredit(boolean usecredit) {
		this.usecredit = usecredit;
	}

	public boolean isInEffect() {
		return inEffect;
	}

	public void setInEffect(boolean inEffect) {
		this.inEffect = inEffect;
	}

	public String getUActualNo() {
		return uActualNo;
	}

	public void setUActualNo(String uActualNo) {
		this.uActualNo = uActualNo;
	}

	public List<WshAccountCoupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<WshAccountCoupon> coupons) {
		this.coupons = coupons;
	}


	public int getCoponSize() {
		int sum = 0;
		if (getCoupons() == null || getCoupons().size() == 0) {
			return sum;
		}
		for (int i = 0; i < getCoupons().size(); i++) {
			sum += getCoupons().get(i).getCoupon_ids().size();
		}
		return sum;
	}
}
