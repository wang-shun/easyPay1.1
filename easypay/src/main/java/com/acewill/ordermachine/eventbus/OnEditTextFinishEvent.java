package com.acewill.ordermachine.eventbus;

/**
 * Author：Anch
 * Date：2017/12/8 11:36
 * Desc：
 */
public class OnEditTextFinishEvent {
	private int    type;// 0- 去支付
	private String code;// 0- 去支付

	public OnEditTextFinishEvent(int type, String code) {
		this.type = type;
		this.code = code;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
