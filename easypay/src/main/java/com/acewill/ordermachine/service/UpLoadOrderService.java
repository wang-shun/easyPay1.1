package com.acewill.ordermachine.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.acewill.chikenprintlibrary.printer.Printer;
import com.acewill.chikenprintlibrary.printer.PrinterFactory;
import com.acewill.chikenprintlibrary.printer.PrinterInterface;
import com.acewill.chikenprintlibrary.printer.PrinterState;
import com.acewill.chikenprintlibrary.printer.PrinterStatus;
import com.acewill.chikenprintlibrary.printer.PrinterVendor;
import com.acewill.ordermachine.R;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.model.DishModel;
import com.acewill.ordermachine.model.OrderReq;
import com.acewill.ordermachine.model.Payment;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.PrintFile;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;

import static java.lang.Thread.currentThread;

/**
 * Author：Anch
 * Date：2017/5/19 15:58
 * Desc：
 */
public class UpLoadOrderService extends Service {

	private static final String   TAG                 = "UpLoadOrderService";
	private static final int      uploadOrderTag      = 100;
	private static final int      checkprintTag       = 200;
	private              Runnable upLoadOrderRunnable = new Runnable() {

		@Override
		public void run() {
			handler.sendEmptyMessage(uploadOrderTag);
			handler.postDelayed(upLoadOrderRunnable, 1000);
		}
	};

	private Runnable       checkprintRunable = new Runnable() {

		@Override
		public void run() {
			if (Common.SHOP_PRINTER == null || Common.SHOP_PRINTER.getVendor()
					.equals("sprt") || Common.SHOP_PRINTER
					.getVendor()
					.equals("unknown") || Common.SHOP_PRINTER
					.getVendor()
					.equals("shangmi_fix")) {
				PrintFile.printResult2("斯博瑞特和未知打印机不去检测连接是否正常-----------" + "\n");
			} else {
				handler.sendEmptyMessage(checkprintTag);
			}
			handler.postDelayed(checkprintRunable, 1000 * 60);//1分钟
		}
	};

	public  Handler        handler           = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case uploadOrderTag:
					//计时，如果5分钟没有操作就进入到屏保界面
					//关闭该service
					if (Common.currentScreentProtectTime > 0) {
						Common.currentScreentProtectTime--;
					} else if (Common.currentScreentProtectTime == 0) {
						//						stopSelf();
						Common.currentScreentProtectTime--;
						handler.removeCallbacks(upLoadOrderRunnable);
						startUpLoadOrder();
					}
					break;
				case checkprintTag:
					checkPrinter();
					break;
			}

		}
	};
	private List<OrderReq> uploadlist        = new ArrayList<>();
	private int excutePosition;

	private void startUpLoadOrder() {
		Log.e(TAG, "upload.>");//执行上传操作，完成后，再次开启计时
		ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();
		//		Cursor cursor = DataSupport.findBySQL("select * from orderreq where upload =0");
		uploadlist.clear();
		excutePosition = 0;
		uploadlist = DataSupport.where("upload=?", 0 + "").find(OrderReq.class);
		if (uploadlist != null && uploadlist.size() == 0)
			startTimer();
		for (int i = 0; i < uploadlist.size(); i++) {
			OrderReq req = uploadlist.get(i);
			List<DishModel> models = DataSupport
					.where("orderreq_id=?", req.getId() + "")
					.find(DishModel.class);
			List<Payment> payments = DataSupport
					.where("orderreq_id=?", req.getId() + "").find(Payment.class);
			req.setItemList(models);
			req.setPaymentList(payments);
		}
		for (int i = 0; i < uploadlist.size(); i++) {
			final int index = i;
			fixedThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					getOrderId(index);//数组索引，和当前实体类的id
				}
			});

		}


	}

	/**
	 * 支付完成之后要去获取一个订单号
	 */
	private void getOrderId(int index) {
		NewPos.getInstance(this).getOrderId(index, new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
				PrintFile
						.printResult2("Class_UploadOrderService_Method_getOrderId()_onError()结果" + e
								.getMessage() + "\n");
			}

			@Override
			public void onResponse(String response, int index) {//数组索引
				PrintFile
						.printResult2("Class_UploadOrderService_Method_getOrderId()_onResponse结果" + response + "\n");
				try {
					OrderReq req = uploadlist.get(index);
					synchronized (req) {
						ContentValues values = new ContentValues();
						values.put("oid", Long.parseLong(GsonUtils.getData("content", response)));
						DataSupport.update(OrderReq.class, values, req.getMyid());
						req.setId(Long.parseLong(GsonUtils.getData("content", response)));
						pushOrder2(index);//将处理的结果发送到本地服务器 //虽然数据库的myid已经变了，但是这个myid还是没有变的 还是2
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/*
  下单 ，调用后台的接口
	*/
	private void pushOrder2(int index) {
		NewPos.getInstance(this)
				.pushOrder(index, new Gson().toJson(uploadlist.get(index)), new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						e.printStackTrace();
						excutePosition++;
						if (excutePosition == uploadlist.size()) {
							startTimer();
						}
						PrintFile
								.printResult2("Class_UploadOrderService_Method_pushOrder2()_onError()结果" + e
										.getMessage() + "\n");
					}

					@Override
					public void onResponse(String response, int index) {
						PrintFile
								.printResult2("Class_UploadOrderService_Method_pushOrder2()_onResponse()结果" + response + "\n");
						OrderReq req = uploadlist.get(index);
						synchronized (req) {
							try {
								excutePosition++;
								int result = GsonUtils.getIntData("result", response);
								if (0 == result) {
									ContentValues values = new ContentValues();
									values.put("upload", 1);
									DataSupport.update(OrderReq.class, values, req
											.getMyid());
								}
								if (excutePosition == uploadlist.size()) {
									startTimer();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});
	}

	public class MyBinder extends Binder {
		public UpLoadOrderService getService() {
			return UpLoadOrderService.this;
		}
	}

	//通过binder实现调用者client与Service之间的通信
	private MyBinder binder = new MyBinder();

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "onCreate");
		Notification notifation = new Notification.Builder(this)
				.setContentTitle("自选快餐POS")
				.setContentText("正在运行中...")
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
				.build();
		NotificationManager manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manger.notify(0, notifation);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startTimer();
		return START_NOT_STICKY;
	}

	/**
	 * 开启一个线程去检测打印机
	 */
	private void checkPrinter() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int printStatus = checkPrinterState(Common.SHOP_PRINTER);
					Common.isPrintOk = printStatus == 576 ? true :
							false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 检测打印机连接的状态
	 *
	 * @param printer
	 * @throws IOException
	 */
	private int checkPrinterState(Printer printer) throws IOException {

		PrinterInterface printerInterface = null;
		if (printer != null) {
			printerInterface = PrinterFactory
					.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer
							.getIp(), printer.getWidth());
			Log.i("打印机id===" + printer.getId(), printer.getDescription() + "====" + printer
					.getIp());
			Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer.getIp());
		}
		String errMessage = "";
		if (printerInterface == null) {
			errMessage = printer.getDescription() + "打印机初始化打印机失败";
			printer.setPrinterState(PrinterState.ERROR);
			printer.setErrMessage(errMessage);
			return 100;
		}
		try {
			printerInterface.init();
		} catch (Exception e) {
			e.printStackTrace();
			errMessage = printer.getDescription() + "连接超时";
			printer.setPrinterState(PrinterState.ERROR);
			printer.setErrMessage(errMessage);
			Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer
					.getIp() + "========" + errMessage);
			return 100;
		}
		if (printerInterface.checkStatus() != PrinterStatus.OK) {
			if (printerInterface.checkStatus() == PrinterStatus.COVER_OPEN) {
				errMessage = printer.getDescription() + "打印机仓盖未关闭";
			} else if (printerInterface.checkStatus() == PrinterStatus.NO_PAPER) {
				errMessage = printer.getDescription() + "打印纸用尽,请更换打印纸";
			} else if (printerInterface.checkStatus() == PrinterStatus.ERROR_OCCURED) {
				errMessage = printer.getDescription() + "出现未知错误";
			} else if (printerInterface.checkStatus() == PrinterStatus.TIMEOUT_ERROR) {
				errMessage = printer.getDescription() + "打印机连接超时";
			} else {
				errMessage = "打印机出现未知异常";
			}
			printer.setPrinterState(PrinterState.ERROR);
			printer.setErrMessage(errMessage);
			PrintFile.printResult2("  errMessage");
			printerInterface.closeNoCutPaper();
			Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer
					.getIp() + "========" + errMessage);
			return 100;
		}
		printerInterface.closeNoCutPaper();
		printer.setPrinterState(PrinterState.SUCCESS);
		PrintFile
				.printResult2("Class_PayOrderPresenter--Method_checkPrinterState()--------打印机:" + printer
						.getDescription() + ",IP:" + printer.getIp() + "正常\n");
		Log.i("打印机:", "打印机=======================");
		return 576;
	}

	public void startTimer() {
		Common.currentScreentProtectTime = Common.ScreenProtectTime;
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.postDelayed(upLoadOrderRunnable, 1000);//1 秒执行一次
			}
		}).start();
	}

	public void startCheckPrint() {
		if (Common.SHOP_PRINTER != null)
			handler.postDelayed(checkprintRunable, 1000);//1秒后开始执行
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "TestService -> onBind, Thread: " + currentThread().getName());
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "TestService -> onUnbind, from:" + intent.getStringExtra("from"));
		return false;
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "TestService -> onDestroy, Thread: " + currentThread().getName());
		super.onDestroy();
	}

}
