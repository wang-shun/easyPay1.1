package com.acewill.ordermachine.model;

import org.litepal.crud.DataSupport;

/**
 * Author：Anch
 * Date：2017/12/23 14:16
 * Desc：
 */
public class LoginUserRecord extends DataSupport {
	private String phone;
	private String pwd;
	private String realname;
	private int id;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public long getCreateAt() {
		return createAt;
	}

	public void setCreateAt(long createAt) {
		this.createAt = createAt;
	}

	private long createAt;

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
