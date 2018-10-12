package com.acewill.ordermachine.model;

import java.io.Serializable;
import java.util.List;

/**
 * Author：Anch
 * Date：2017/12/21 11:57
 * Desc：
 */
public class WorkShiftDetail implements Serializable {


	/**
	 * personName : 谢老板
	 * workShiftName : 早班
	 * startTime : 2017-12-21 11:17:08
	 * endTime : 2017-12-21 11:17:27
	 * startWorkShiftCash : 12.0
	 * endWorkShiftCash : 12.0
	 * submitCash : 12.0
	 * differenceCash : 0.00
	 * workShiftCategoryDataList : [{"name":"销售统计","workShiftItemDatas":[]},{"name":"退款清单","workShiftItemDatas":[{"name":"总计","itemCounts":"0","total":0}]},{"name":"优惠清单","workShiftItemDatas":[{"name":"总计","itemCounts":"0","total":0}]},{"name":"实收","workShiftItemDatas":[{"name":"合计","itemCounts":"0","total":0},{"name":"抹零","itemCounts":"2","total":0},{"name":"总计","itemCounts":"","total":0}]},{"name":"备用金(营业外收入/支出)","workShiftItemDatas":[]}]
	 * startTimeLong : 0
	 * endTiemLong : 0
	 */

	private String personName;
	private String                              workShiftName;
	private String                              startTime;
	private String                              endTime;
	private double                              startWorkShiftCash;
	private double                              endWorkShiftCash;
	private double                              submitCash;
	private String                              differenceCash;
	private long                                 startTimeLong;
	private long                                 endTiemLong;
	private List<WorkShiftCategoryDataListBean> workShiftCategoryDataList;

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getWorkShiftName() {
		return workShiftName;
	}

	public void setWorkShiftName(String workShiftName) {
		this.workShiftName = workShiftName;
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

	public double getStartWorkShiftCash() {
		return startWorkShiftCash;
	}

	public void setStartWorkShiftCash(double startWorkShiftCash) {
		this.startWorkShiftCash = startWorkShiftCash;
	}

	public double getEndWorkShiftCash() {
		return endWorkShiftCash;
	}

	public void setEndWorkShiftCash(double endWorkShiftCash) {
		this.endWorkShiftCash = endWorkShiftCash;
	}

	public double getSubmitCash() {
		return submitCash;
	}

	public void setSubmitCash(double submitCash) {
		this.submitCash = submitCash;
	}

	public String getDifferenceCash() {
		return differenceCash;
	}

	public void setDifferenceCash(String differenceCash) {
		this.differenceCash = differenceCash;
	}

	public long getStartTimeLong() {
		return startTimeLong;
	}

	public void setStartTimeLong(long startTimeLong) {
		this.startTimeLong = startTimeLong;
	}

	public long getEndTiemLong() {
		return endTiemLong;
	}

	public void setEndTiemLong(long endTiemLong) {
		this.endTiemLong = endTiemLong;
	}

	public List<WorkShiftCategoryDataListBean> getWorkShiftCategoryDataList() {
		return workShiftCategoryDataList;
	}

	public void setWorkShiftCategoryDataList(List<WorkShiftCategoryDataListBean> workShiftCategoryDataList) {
		this.workShiftCategoryDataList = workShiftCategoryDataList;
	}

	public static class WorkShiftCategoryDataListBean {
		/**
		 * name : 销售统计
		 * workShiftItemDatas : []
		 */

		private String name;
		private List<WorkShiftItem> workShiftItemDatas;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public List<WorkShiftItem> getWorkShiftItemDatas() {
			return workShiftItemDatas;
		}

		public void setWorkShiftItemDatas(List<WorkShiftItem> workShiftItemDatas) {
			this.workShiftItemDatas = workShiftItemDatas;
		}
		public static class WorkShiftItem {

			/**
			 * name : 总计
			 * itemCounts : 0
			 * total : 0
			 */

			private String name;
			private String itemCounts;
			private float    total;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getItemCounts() {
				return itemCounts;
			}

			public void setItemCounts(String itemCounts) {
				this.itemCounts = itemCounts;
			}

			public float getTotal() {
				return total;
			}

			public void setTotal(float total) {
				this.total = total;
			}
		}
	}


}
