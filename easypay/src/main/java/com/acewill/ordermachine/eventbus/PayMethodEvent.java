package com.acewill.ordermachine.eventbus;

/**
 * Author：Anch
 * Date：2017/12/29 19:13
 * Desc：
 */
public class PayMethodEvent {
	private  int id;
	private  String name;
	private boolean cashbox;

	public PayMethodEvent(int id,String name,boolean cashbox) {
		this.id = id;
		this.name =name;
		this.cashbox = cashbox;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCashbox() {
		return cashbox;
	}

	public void setCashbox(boolean cashbox) {
		this.cashbox = cashbox;
	}
}
