package com.zhy.http.okhttp.callback;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class StringCallback extends Callback<String> {
	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public String parseNetworkResponse(Response response, int id, String url, String requestParams) throws Exception {
		String re = response.body().string();
		if (!url.contains("dishKind")&& !url.contains("dishmenu") ) {
			FileLog
					.log("Res", "", "onResponse", "", format
							.format(new Date()) + "/url>" + url + ";\n" + format
							.format(new Date()) + "/response>" + re);
		}


		return re;
	}
}
