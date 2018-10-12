package com.acewill.ordermachine.printadapter;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

/**
 * Author：Anch
 * Date：2017/5/4 18:57
 * Desc：
 */
public class KitChenPrintManager {
	private static KitChenPrintManager mManager;

	public static final int GETSTORECONFIGURATION = 0;
	public static final int GETPRINTERS           = 1;
	public static final int GETKDSES              = 2;
	public static final int GETKICHENSTALLS       = 3;
	public static final int GETALLTEMPLATES       = 4;

	//	serverUrl + uri + "?token=" + token
	public static final String GETSTORECONFIGURATIONURL = "api/store_operation/getStoreConfiguration";
	public static final String GETPRINTERSURL           = "api/kichenStalls/getPrinters";
	public static final String GETKDSESURL              = "api/kichenStalls/getKDSes";
	public static final String GETKICHENSTALLSURL       = "api/kichenStalls/getKichenStalls";
	public static final String GETALLTEMPLATESURL       = "api/printTemplate/getAllTemplates";

	public static KitChenPrintManager getInstance() {
		if (mManager == null) {
			mManager = new KitChenPrintManager();
		}
		return mManager;
	}


	/**
	 * @param serverUrl
	 * @param token
	 * @param url
	 * @param appidParam
	 * @param brandidParam
	 * @param storeidParam
	 * @param id
	 * @param callback
	 */
	public void sendPostRequest(String serverUrl, String token, String appidParam, String brandidParam, String url, String storeidParam, int id, StringCallback callback) {

		OkHttpUtils.getInstance().post().url(serverUrl + url)
				.id(id)
				.addParams("appid", appidParam)
				.addParams("brandid", brandidParam)
				.addParams("storeid", storeidParam)
				.addParams("token", token)
				.build().execute(callback);

	}
}
