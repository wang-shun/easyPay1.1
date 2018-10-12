package com.acewill.ordermachine;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.os.RemoteException;

import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.handler_new.CrashHandler;
import com.acewill.ordermachine.util_new.Utils;
import com.xiasuhuei321.loadingdialog.manager.StyleManager;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;

import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

public class BaseApplication extends LitePalApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		Utils.init(this);
		StyleManager s = new StyleManager();

		//在这里调用方法设置s的属性
		//code here...
		s.Anim(false).repeatTime(0).contentSize(-1).intercept(true);
		CrashHandler.getInstance().init(getApplicationContext());
		LoadingDialog.initStyle(s);
		isAidl = true;
		connectPrinterService(this);
	}

	public static Drawable getDefaultDrawable() {
		return getInstance().getResources().getDrawable(R.drawable.default_img);
	}

	/**
	 * 打开的activity
	 **/
	private List<Activity> activities = new ArrayList<Activity>();
	/**
	 * 应用实例
	 **/
	private static BaseApplication instance;

	/**
	 * 获得实例
	 *
	 * @return
	 */
	public static BaseApplication getInstance() {
		return instance;
	}

	/**
	 * 新建了一个activity
	 *
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	/**
	 * 结束指定的Activity
	 *
	 * @param activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			this.activities.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 应用退出，结束所有的activity
	 */
	public void exit() {
		for (Activity activity : activities) {
			if (activity != null) {
				activity.finish();
			}
		}
		System.exit(0);
	}

	/**
	 * 关闭Activity列表中的所有Activity
	 */
	public void finishActivity() {
		for (Activity activity : activities) {
			if (null != activity) {
				activity.finish();
			}
		}
		//杀死该应用进程
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public void clearStatic() {
		Common.workShifit = null;
		Common.workShifIno = null;
		Common.SHOP_PRINTER = null;
		Common.dishModel = null;
	}

	public List<Activity> getActivityList() {
		return activities;
	}

	private boolean isAidl;

	public boolean isAidl() {
		return isAidl;
	}

	public void setAidl(boolean aidl) {
		isAidl = aidl;
	}

	private Context context;

	private static final String SERVICE＿PACKAGE = "woyou.aidlservice.jiuiv5";
	private static final String SERVICE＿ACTION  = "woyou.aidlservice.jiuiv5.IWoyouService";

	/**
	 * 连接服务
	 *
	 * @param context context
	 */
	public void connectPrinterService(Context context) {
		this.context = context.getApplicationContext();
		Intent intent = new Intent();
		intent.setPackage(SERVICE＿PACKAGE);
		intent.setAction(SERVICE＿ACTION);
		context.getApplicationContext().startService(intent);
		context.getApplicationContext().bindService(intent, connService, Context.BIND_AUTO_CREATE);
	}

	private IWoyouService woyouService;

	public void openBox() {
		try {
			if (woyouService!=null){
				woyouService.openDrawer(new ICallback() {
					@Override
					public void onRunResult(boolean isSuccess, int code, String msg) throws RemoteException {

					}

					@Override
					public IBinder asBinder() {
						return null;
					}
				});
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private ServiceConnection connService = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			woyouService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			woyouService = IWoyouService.Stub.asInterface(service);
		}
	};
}
