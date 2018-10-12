package com.acewill.ordermachine.util_new;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Author：Anch
 * Date：2017/5/27 12:31
 * Desc：
 */
public class ToastUtils {
	private static Handler mhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};
	private static Toast mToast;
	private static Runnable r = new Runnable() {
		@Override
		public void run() {
			mToast.show();
		}
	};

	@SuppressLint("ShowToast")
	public static void showToast(Context context, String msg) {

		mhandler.removeCallbacks(r);
		if (null != mToast) {
			mToast.setText(msg);
		} else {
			mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		}
		mhandler.postDelayed(r, 1000);
		mToast.show();
	}
}
