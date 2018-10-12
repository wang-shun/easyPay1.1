package com.acewill.ordermachine.model;

import java.util.List;

/**
 * Author：Anch
 * Date：2017/12/5 15:15
 * Desc：
 */
public class WorkShiftRes {


	/**
	 * result : 0
	 * content : [{"id":1322,"appId":"59841423","storeId":1,"userId":1,"userName":"13822540060","terminalId":6,"terminalName":null,"definitionId":1032,"definitionName":"中班","spareCash":0,"cashRevenue":1,"startTime":1512455649000,"endTime":1512456985000,"durationMinutes":null,"realName":"谢老板","startTimeStr":"2017-12-05 14:34:09","endTimeStr":"2017-12-05 14:56:25","workShiftDetail":null},{"id":1321,"appId":"59841423","storeId":1,"userId":1,"userName":"13822540060","terminalId":6,"terminalName":null,"definitionId":1033,"definitionName":"晚班","spareCash":0,"cashRevenue":1,"startTime":1512454082000,"endTime":1512455637000,"durationMinutes":null,"realName":"谢老板","startTimeStr":"2017-12-05 14:08:02","endTimeStr":"2017-12-05 14:33:57","workShiftDetail":null},{"id":1320,"appId":"59841423","storeId":1,"userId":1,"userName":"13822540060","terminalId":6,"terminalName":null,"definitionId":1032,"definitionName":"中班","spareCash":0,"cashRevenue":1,"startTime":1512454051000,"endTime":1512454071000,"durationMinutes":null,"realName":"谢老板","startTimeStr":"2017-12-05 14:07:31","endTimeStr":"2017-12-05 14:07:51","workShiftDetail":null},{"id":1319,"appId":"59841423","storeId":1,"userId":1,"userName":"13822540060","terminalId":6,"terminalName":null,"definitionId":1033,"definitionName":"晚班","spareCash":0,"cashRevenue":1,"startTime":1512453357000,"endTime":1512454031000,"durationMinutes":null,"realName":"谢老板","startTimeStr":"2017-12-05 13:55:57","endTimeStr":"2017-12-05 14:07:11","workShiftDetail":null},{"id":1318,"appId":"59841423","storeId":1,"userId":1,"userName":"13787719750","terminalId":6,"terminalName":null,"definitionId":1032,"definitionName":"中班","spareCash":0,"cashRevenue":1,"startTime":1512453321000,"endTime":1512453329000,"durationMinutes":null,"realName":"谭艳霞","startTimeStr":"2017-12-05 13:55:21","endTimeStr":"2017-12-05 13:55:29","workShiftDetail":null},{"id":1317,"appId":"59841423","storeId":1,"userId":1,"userName":"13822540060","terminalId":6,"terminalName":null,"definitionId":1031,"definitionName":"早班","spareCash":0,"cashRevenue":1,"startTime":1512453294000,"endTime":1512453302000,"durationMinutes":null,"realName":"谢老板","startTimeStr":"2017-12-05 13:54:54","endTimeStr":"2017-12-05 13:55:02","workShiftDetail":null},{"id":1316,"appId":"59841423","storeId":1,"userId":1,"userName":"13822540060","terminalId":6,"terminalName":null,"definitionId":1031,"definitionName":"早班","spareCash":0,"cashRevenue":1,"startTime":1512452992000,"endTime":1512453006000,"durationMinutes":null,"realName":"谢老板","startTimeStr":"2017-12-05 13:49:52","endTimeStr":"2017-12-05 13:50:06","workShiftDetail":null},{"id":1315,"appId":"59841423","storeId":1,"userId":1,"userName":"13822540060","terminalId":6,"terminalName":null,"definitionId":1032,"definitionName":"中班","spareCash":0,"cashRevenue":1,"startTime":1512452827000,"endTime":1512452849000,"durationMinutes":null,"realName":"谢老板","startTimeStr":"2017-12-05 13:47:07","endTimeStr":"2017-12-05 13:47:29","workShiftDetail":null},{"id":1314,"appId":"59841423","storeId":1,"userId":1,"userName":"13822540060","terminalId":6,"terminalName":null,"definitionId":1031,"definitionName":"早班","spareCash":0,"cashRevenue":1,"startTime":1512452692000,"endTime":1512452709000,"durationMinutes":null,"realName":"谢老板","startTimeStr":"2017-12-05 13:44:52","endTimeStr":"2017-12-05 13:45:09","workShiftDetail":null},{"id":1313,"appId":"59841423","storeId":1,"userId":1,"userName":"13822540060","terminalId":6,"terminalName":null,"definitionId":1031,"definitionName":"早班","spareCash":0,"cashRevenue":1,"startTime":1512452144000,"endTime":1512452165000,"durationMinutes":null,"realName":"谢老板","startTimeStr":"2017-12-05 13:35:44","endTimeStr":"2017-12-05 13:36:05","workShiftDetail":null},{"id":1312,"appId":"59841423","storeId":1,"userId":1,"userName":"谢老板","terminalId":6,"terminalName":null,"definitionId":1032,"definitionName":"中班","spareCash":0,"cashRevenue":0,"startTime":1512441723000,"endTime":null,"durationMinutes":null,"realName":null,"startTimeStr":"2017-12-05 10:42:03","endTimeStr":null,"workShiftDetail":null}]
	 * errmsg :
	 * total : 11
	 * page : 0
	 * limit : 2147483647
	 */

	private int result;
	private String          errmsg;
	private int             total;
	private int             page;
	private int             limit;
	private List<WorkShift> content;

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

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<WorkShift> getContent() {
		return content;
	}

	public void setContent(List<WorkShift> content) {
		this.content = content;
	}
}
