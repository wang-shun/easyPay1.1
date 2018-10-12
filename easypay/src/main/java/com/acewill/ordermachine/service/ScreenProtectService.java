//package com.acewill.ordermachine.service;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.os.Binder;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.util.Log;
//
//import com.acewill.ordermachine.R;
//import com.acewill.ordermachine.common.Common;
//
//import static java.lang.Thread.currentThread;
//
///**
// * Author：Anch
// * Date：2017/5/19 15:58
// * Desc：
// */
//public class ScreenProtectService extends Service {
//
//	private static final String TAG           = "ScreenProtectService";
//	private static final int    MSG_SHOW_TIPS = 100;
//
//	private Runnable tipsShowRunable = new Runnable() {
//
//		@Override
//		public void run() {
//			handler.sendEmptyMessage(MSG_SHOW_TIPS);
//			handler.postDelayed(tipsShowRunable, 1000);
//		}
//	};
//	//	int i = 60 * 5;
//
//	public Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//				case MSG_SHOW_TIPS:
//					//计时，如果5分钟没有操作就进入到屏保界面
//					//关闭该service
//					if (currentScreentProtectTime > 0) {
//						currentScreentProtectTime--;
////						Log.e(TAG, "i==" + currentScreentProtectTime);
//					} else if (currentScreentProtectTime == 0) {
//						//						stopSelf();
//						currentScreentProtectTime--;
//						handler.removeCallbacks(tipsShowRunable);
//						showScreenProtectActivity();
////						Log.e(TAG, "i==" + currentScreentProtectTime);
//					}
//					break;
//			}
//
//		}
//	};
//
//	private void showScreenProtectActivity() {
//		//进入屏保的页面之前，其他的页面都要关闭，然后进入到
//		Cart.getInstance().clear();
//		Intent dialogIntent = new Intent(getBaseContext(), MainActivity.class);
//		dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		getApplication().startActivity(dialogIntent);
//	}
//
//
//	public class MyBinder extends Binder {
//		public ScreenProtectService getService() {
//			return ScreenProtectService.this;
//		}
//	}
//
//	//通过binder实现调用者client与Service之间的通信
//	private MyBinder binder = new MyBinder();
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		Log.e(TAG, "onCreate");
//		Notification notifation = new Notification.Builder(this)
//				.setContentTitle("自助点餐机")
//				.setContentText("正在运行中...")
//				.setSmallIcon(R.drawable.ic_launcher)
//				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
//				.build();
//		NotificationManager manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//		manger.notify(0, notifation);
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		startTimer();
//		return START_NOT_STICKY;
//	}
//
//	public void startTimer() {
//		if (Common.currentScreentProtectTime == -1) {
//			Common.currentScreentProtectTime = Common.ScreenProtectTime;
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					handler.postDelayed(tipsShowRunable, 1000);//每 10 秒执行一次
//				}
//			}).start();
//		} else {
//			Common.currentScreentProtectTime = Common.ScreenProtectTime;
//		}
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		Log.i(TAG, "TestService -> onBind, Thread: " + currentThread().getName());
//		return binder;
//	}
//
//	@Override
//	public boolean onUnbind(Intent intent) {
//		Log.i(TAG, "TestService -> onUnbind, from:" + intent.getStringExtra("from"));
//		return false;
//	}
//
//	@Override
//	public void onDestroy() {
//		Log.i(TAG, "TestService -> onDestroy, Thread: " + currentThread().getName());
//		super.onDestroy();
//	}
//
//}
