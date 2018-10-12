package com.acewill.ordermachine.model;

import java.util.List;

/**
 * Author：Anch
 * Date：2017/11/30 11:39
 * Desc：
 */
public class RefundReasonReq {


	/**
	 * result : 0
	 * content : [{"id":41,"reason":"TUICAI","appid":"59841423","reasonType":"退菜原因","seq":1,"retreatCount":0,"retreatCountStr":"否"}]
	 * errmsg : 0
	 */

	private int                    result;
	private String                 errmsg;

	public List<RefundReasonBean> getContent() {
		return content;
	}

	public void setContent(List<RefundReasonBean> content) {
		this.content = content;
	}

	private List<RefundReasonBean> content;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}


}
