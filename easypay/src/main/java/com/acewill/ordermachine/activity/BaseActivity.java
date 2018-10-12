package com.acewill.ordermachine.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.acewill.ordermachine.BaseApplication;
import com.acewill.ordermachine.dialog.ErrorDialog;
import com.acewill.ordermachine.dialog.LoadingDialog;
import com.acewill.ordermachine.model.Store;
import com.acewill.ordermachine.util.permission.IPermission;
import com.acewill.ordermachine.widget.SystemUIUtils;
import com.acewill.ordermachine.widget.WindowUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class BaseActivity extends FragmentActivity {
	// 数据返回后，用来刷新界面用，运行在GUI线程
	protected Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this);
		mActivity = this;
		EventBus.getDefault().register(this);
		changeAppLanguage();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(String str) {
		switch (str) {
			case "EVENT_REFRESH_LANGUAGE":
				changeAppLanguage();
				recreate();//刷新界面
				break;
		}
	}

	public void changeAppLanguage() {
		String sta = Store.getLanguageLocal(this);
		if (sta != null && !"".equals(sta)) {
			// 本地语言设置
			Locale         myLocale = new Locale(sta);
			Resources      res      = getResources();
			DisplayMetrics dm       = res.getDisplayMetrics();
			Configuration  conf     = res.getConfiguration();
			conf.locale = myLocale;
			res.updateConfiguration(conf, dm);
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInput(v, ev)) {
				WindowUtil.hiddenKey();
			}

			//            else {
			//                WindowUtil.showKey(context,(EditText) v);
			//            }
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	public boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			//            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(),0);
			int[] leftTop = {0, 0};
			//获取输入框当前的location位置
			v.getLocationInWindow(leftTop);
			int left   = leftTop[0];
			int top    = leftTop[1];
			int bottom = top + v.getHeight();
			int right  = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件

				return false;
			} else {
				return true;
			}
		}
		return true;
	}


	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	protected void onPause() {
		super.onPause();
		WindowUtil.hiddenKey();
	}

	@Override
	public void onBackPressed() {
		WindowUtil.hiddenKey();
		super.onBackPressed();
	}


	public void showToast(String content) {
		Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		SystemUIUtils.setStickFullScreen(getWindow().getDecorView());
	}

	public LoadingDialog loadingDialog;
	public ErrorDialog   mErrorDialog;

	public void showLoadingDialog() {
		loadingDialog = LoadingDialog.newInstance();
		loadingDialog.show(getSupportFragmentManager(), "LoadingDialog");
	}

	public void dismissLoadingDialog() {
		if (loadingDialog == null)
			return;
		loadingDialog.setMsg("");
		loadingDialog.dismiss();
	}

	public void dismissErrorDialog() {
		if (mErrorDialog == null)
			return;
		mErrorDialog.dismiss();
	}

	public ErrorDialog showErrorDialog(String msg, boolean showRadioBtn) {
		mErrorDialog = ErrorDialog.newInstance(msg, showRadioBtn);
		mErrorDialog.show(getSupportFragmentManager(), "ErrorMsg");
		return mErrorDialog;
	}


	public static void requestRunTimePermission(String[] permissions, IPermission listener) {
		Activity topActivity = getTopActivity();

		if (topActivity == null) {
			return;
		}
		mListener = listener;
		List<String> permissionList = new ArrayList<>();
		for (String permission : permissions) {
			if (ContextCompat.checkSelfPermission(topActivity,
					permission) != PackageManager.PERMISSION_GRANTED) {
				permissionList.add(permission);
			}
		}
		if (!permissionList.isEmpty()) {
			ActivityCompat.requestPermissions(topActivity,
					permissionList.toArray(new String[permissionList.size()]),
					REQUEST_CODE);
		} else {
			//doSomething
			mListener.onGranted();
		}
	}


	private static Activity getTopActivity() {

		if (BaseApplication.getInstance().getActivityList()
				.isEmpty()) {
			return null;
		} else {
			return BaseApplication.getInstance().getActivityList()
					.get(BaseApplication.getInstance().getActivityList()
							.size() - 1);
		}
	}

	private final static int REQUEST_CODE = 1;
	private static IPermission mListener;

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case REQUEST_CODE:
				if (grantResults.length > 0) {
					List<String> deniedPermissions = new ArrayList<>();
					for (int i = 0; i < grantResults.length; i++) {
						int    grantResult = grantResults[i];
						String permission  = permissions[i];
						if (grantResult != PackageManager.PERMISSION_GRANTED) {
							deniedPermissions.add(permission);
						}
					}
					if (deniedPermissions.isEmpty()) {
						mListener.onGranted();
					} else {
						mListener.onDenied(deniedPermissions);
					}
				}
				break;
			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
