package com.zhy.http.okhttp.callback;

import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class Callback<T> {
	/**
	 * UI Thread
	 *
	 * @param request
	 */
	public void onBefore(Request request, int id) {
	}

	/**
	 * UI Thread
	 *
	 * @param
	 */
	public void onAfter(int id) {
	}

	/**
	 * UI Thread
	 *
	 * @param progress
	 */
	public void inProgress(float progress, long total, int id) {

	}

	/**
	 * if you parse reponse code in parseNetworkResponse, you should make this method return true.
	 *
	 * @param response
	 * @return
	 */
	public boolean validateReponse(Response response, int id) {
		return response.isSuccessful();
	}

	/**
	 * Thread Pool Thread
	 *
	 * @param response
	 */
	public abstract T parseNetworkResponse(Response response, int id, String url, String requestParams) throws Exception;

	public abstract void onError(Call call, Exception e, int id);

	public abstract void onResponse(T response, int id);

	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static Callback CALLBACK_DEFAULT = new Callback() {


		@Override
		public Object parseNetworkResponse(Response response, int id, String url, String requestParams) throws Exception {
			return null;
		}

		@Override
		public void onError(Call call, Exception e, int id) {
			FileLog
					.log("Res", "", "onError", "", e.getMessage() + "");
		}

		@Override
		public void onResponse(Object response, int id) {

		}


	};

}