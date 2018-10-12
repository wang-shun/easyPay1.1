package com.acewill.ordermachine.eventbus;

/**
 * Author：Anch
 * Date：2017/12/12 17:40
 * Desc：
 */
public class OnCheckAthorityEvent {
	private int type;

	public OnCheckAthorityEvent(int type, boolean statu) {
		this.type = type;
		this.statu = statu;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isStatu() {
		return statu;
	}

	public void setStatu(boolean statu) {
		this.statu = statu;
	}

	private boolean statu;

}
