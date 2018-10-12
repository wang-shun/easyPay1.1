package com.acewill.ordermachine.eventbus;

/**
 * Author：Anch
 * Date：2017/11/22 19:01
 * Desc：
 */
public class OnFinishEvent {
	private String msg;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	private String content;

	public void setMsg(String trim) {
		msg = trim;
	}

	public String getMsg() {
		return msg;
	}
}
