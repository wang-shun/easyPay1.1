package com.acewill.ordermachine.model;

import java.io.Serializable;
import java.util.List;

/**
 * Author：Anch
 * Date：2017/12/4 14:27
 * Desc：
 */
public class WorkShiftInfo implements Serializable{


	/**
	 * result : 0
	 * content : [{"id":1031,"appId":"59841423","storeId":1,"brandId":47,"name":"早班","startTime":"00:00","endTime":"09:00"},{"id":1032,"appId":"59841423","storeId":1,"brandId":47,"name":"中班","startTime":"09:00","endTime":"15:00"},{"id":1033,"appId":"59841423","storeId":1,"brandId":47,"name":"晚班","startTime":"15:00","endTime":"00:00"}]
	 * errmsg : 0
	 */

	private int result;
	private String            errmsg;
	private List<ContentBean> content;

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

	public List<ContentBean> getContent() {
		return content;
	}

	public void setContent(List<ContentBean> content) {
		this.content = content;
	}

	public static class ContentBean implements Serializable{
		/**
		 * id : 1031
		 * appId : 59841423
		 * storeId : 1
		 * brandId : 47
		 * name : 早班
		 * startTime : 00:00
		 * endTime : 09:00
		 */

		private int id;
		private String appId;
		private int    storeId;
		private int    brandId;
		private String name;
		private String startTime;
		private String endTime;

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

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public int getStoreId() {
			return storeId;
		}

		public void setStoreId(int storeId) {
			this.storeId = storeId;
		}

		public int getBrandId() {
			return brandId;
		}

		public void setBrandId(int brandId) {
			this.brandId = brandId;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}
	}
}
