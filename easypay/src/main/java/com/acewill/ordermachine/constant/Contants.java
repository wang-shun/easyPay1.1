package com.acewill.ordermachine.constant;

import android.os.Environment;

/**
 * Created by Administrator on 2017/4/23.
 */

public class Contants {
	public static String SDPATH = Environment.getExternalStorageDirectory() + "/";

	public static class IntentRequest {
		public static final int ASK_FOR_COMFIRM = 1;
	}
}
