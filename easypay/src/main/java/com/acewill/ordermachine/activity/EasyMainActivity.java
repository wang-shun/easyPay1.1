package com.acewill.ordermachine.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acewill.chikenprintlibrary.model.PowerController;
import com.acewill.ordermachine.BaseApplication;
import com.acewill.ordermachine.OnCancleClickListener;
import com.acewill.ordermachine.R;
import com.acewill.ordermachine.adapter.PayMethodAdapter;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.dialog.ChangeLanguageDialog;
import com.acewill.ordermachine.dialog.ErrorDialog;
import com.acewill.ordermachine.dialog.SetDialog;
import com.acewill.ordermachine.enum_c.PayStatu;
import com.acewill.ordermachine.eventbus.OnCheckAthorityEvent;
import com.acewill.ordermachine.eventbus.OnEditTextFinishEvent;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.eventbus.PayMethodEvent;
import com.acewill.ordermachine.eventbus.PayStatus;
import com.acewill.ordermachine.eventbus.ResultEvent;
import com.acewill.ordermachine.fragmentdialog.CommonEditDialog;
import com.acewill.ordermachine.fragmentdialog.JiaoBanDialog;
import com.acewill.ordermachine.fragmentdialog.KaiBanDialog;
import com.acewill.ordermachine.fragmentdialog.PayInputDialog;
import com.acewill.ordermachine.model.PaymentInfo;
import com.acewill.ordermachine.model.PaymentInfoResp;
import com.acewill.ordermachine.model.Store;
import com.acewill.ordermachine.model.WorkShift;
import com.acewill.ordermachine.model.WorkShiftInfo;
import com.acewill.ordermachine.pos.JsonGenericsSerializator;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.printadapter.PosKitchenPrintAdapter;
import com.acewill.ordermachine.service.UpLoadOrderService;
import com.acewill.ordermachine.util.FileUtils;
import com.acewill.ordermachine.util.PriceUtil;
import com.acewill.ordermachine.util.SharedPreferencesUtil;
import com.acewill.ordermachine.util_new.AppUtils;
import com.acewill.ordermachine.util_new.DateUtils;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.NetworkUtils;
import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.ordermachine.util_new.doubleutil.UPacketFactory;
import com.acewill.ordermachine.widget.DropEditText;
import com.acewill.ordermachine.widget.WindowUtil;
import com.acewill.paylibrary.PayReqModel;
import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.tencent.WXPay;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.zhy.http.okhttp.callback.FileLog;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import sunmi.ds.DSKernel;
import sunmi.ds.callback.IConnectionCallback;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;
import sunmi.ds.data.DataPacket;

/**
 * Author：Anch
 * Date：2017/11/17 15:26
 * Desc：> ,  < ,=  , != （< >）,>=   ,   <=
 */
public class EasyMainActivity extends BaseActivity implements View.OnClickListener, PayOrderView,
		OnCancleClickListener, ChangeLanguageDialog.OnLanguageSelectListener {
	private static final String TAG = "EasyMainActivity";

	private DropEditText pwd_et;
	private GridView     mygridview;
	private IConnectionCallback mIConnectionCallback = new IConnectionCallback() {
		@Override
		public void onDisConnect() {
			Message message = new Message();
			message.what = 1;
			message.obj = "与远程服务连接中断";
			myHandler.sendMessage(message);
		}

		@Override
		public void onConnected(ConnState state) {
			Message message = new Message();
			message.what = 1;
			switch (state) {
				case AIDL_CONN:
					message.obj = "与远程服务绑定成功";
					break;
				case VICE_SERVICE_CONN:
					message.obj = "与副屏服务通讯正常";
					break;
				case VICE_APP_CONN:
					message.obj = "与副屏app通讯正常";
					break;
				default:
					break;
			}
			myHandler.sendMessage(message);
		}
	};

	private static class MyHandler extends Handler {
		private WeakReference<Activity> mActivity;

		public MyHandler(Activity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mActivity.get() != null && !mActivity.get().isFinishing()) {
				switch (msg.what) {
					case 1://消息提示用途
						//						Toast.makeText(mActivity.get(), msg.obj + "", Toast.LENGTH_SHORT).show();
						PrintFile.printResult2(msg.obj + "");
						break;
					default:
						break;
				}
			}
		}

	}

	private ISendCallback    mISendCallback    = new ISendCallback() {
		@Override
		public void onSendSuccess(long taskId) {

		}

		@Override
		public void onSendFail(int errorId, String errorInfo) {

		}

		@Override
		public void onSendProcess(long totle, long sended) {

		}
	};
	private IReceiveCallback mIReceiveCallback = new IReceiveCallback() {
		@Override
		public void onReceiveData(DSData data) {

		}

		@Override
		public void onReceiveFile(DSFile file) {

		}

		@Override
		public void onReceiveFiles(DSFiles files) {

		}

		@Override
		public void onReceiveCMD(DSData cmd) {

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_easymain);
		super.onCreate(savedInstanceState);
		initView();
		Connector.getDatabase();
		initPayChange();
		initFupin();
		startToCheckPrinter();
	}

	private MyHandler myHandler;

	private void initFupin() {
		initSdk();
		myHandler = new MyHandler(this);
		DSKernel mDSKernel = DSKernel.newInstance();
		mDSKernel.init(this, mIConnectionCallback);    //绑定服务的回调
		mDSKernel.addReceiveCallback(mIReceiveCallback);  //双屏通信接收数据回调
	}

	private TextView          num_add_half_one;
	private TextView          dingdianshu_tv;
	private TextView          dingdanjine_tv;
	private TextView          banci_tv;
	private RelativeLayout    easymain_mian_ly;
	private FrameLayout       ligangshuoding_ly;
	private TextView          zhongduandanhao_tv;
	private ImageView         dayin_img;
	//	private ImageView      zaixian_img;
	private AnimationDrawable dayin_imgBg;
	private TextView          error_count;

	private void initView() {
		//数字按键
		findViewById(R.id.num_one).setOnClickListener(this);
		findViewById(R.id.num_two).setOnClickListener(this);
		findViewById(R.id.num_three).setOnClickListener(this);
		findViewById(R.id.num_four).setOnClickListener(this);
		findViewById(R.id.num_five).setOnClickListener(this);
		findViewById(R.id.num_six).setOnClickListener(this);
		findViewById(R.id.num_seven).setOnClickListener(this);
		findViewById(R.id.num_eight).setOnClickListener(this);
		findViewById(R.id.num_nine).setOnClickListener(this);
		findViewById(R.id.num_zero).setOnClickListener(this);
		findViewById(R.id.num_add).setOnClickListener(this);
		findViewById(R.id.num_del).setOnClickListener(this);
		findViewById(R.id.num_dot).setOnClickListener(this);
		num_add_half_one = (TextView) findViewById(R.id.num_add_half_one);
		num_add_half_one.setOnClickListener(this);
		String html = "<font color=\"red\">" + "+" + "</font> 0.5";
		num_add_half_one.setText(Html.fromHtml(html));
		findViewById(R.id.num_clear).setOnClickListener(this);
		//		findViewById(R.id.num_yidongzhifu).setOnClickListener(this);
		//		findViewById(R.id.num_cash).setOnClickListener(this);
		//		findViewById(R.id.num_yuangongka).setOnClickListener(this);
		//		findViewById(R.id.num_coupons).setOnClickListener(this);
		//		findViewById(R.id.num_yinlianka).setOnClickListener(this);
		//		findViewById(R.id.num_add_cash_ten).setOnClickListener(this);
		//		findViewById(R.id.num_add_cash_twenty).setOnClickListener(this);
		//		findViewById(R.id.num_add_cash_fifty).setOnClickListener(this);
		findViewById(R.id.num_equal).setOnClickListener(this);


		//功能按键
		findViewById(R.id.dingdantongji_ly).setOnClickListener(this);
		findViewById(R.id.kaiqianxiang_ly).setOnClickListener(this);
		findViewById(R.id.ligangsuoding_ly).setOnClickListener(this);
		findViewById(R.id.gongnengxuanxiang_ly).setOnClickListener(this);
		findViewById(R.id.zhongduandanhao_layout).setOnClickListener(this);
		findViewById(R.id.textView2).setOnClickListener(this);
		findViewById(R.id.center_bg_ly).setOnClickListener(this);
		findViewById(R.id.tip_ly).setOnClickListener(this);
		findViewById(R.id.bottom_bg_ly).setOnClickListener(this);
		findViewById(R.id.relativeLayout).setOnClickListener(this);
		findViewById(R.id.print_ly).setOnClickListener(this);
		findViewById(R.id.statu_ly).setOnClickListener(this);
		findViewById(R.id.user_name_ly).setOnClickListener(this);
		time1 = (TextView) findViewById(R.id.time1);
		time2 = (TextView) findViewById(R.id.time2);
		dayin_img = (ImageView) findViewById(R.id.dayin_img);
		dayin_img.setBackgroundResource(R.drawable.dayin_ok_img);
		dayin_imgBg = (AnimationDrawable) dayin_img.getBackground();
		shop_name = (TextView) findViewById(R.id.shop_name);
		ip_address = (TextView) findViewById(R.id.ip_address);
		manager_name = (TextView) findViewById(R.id.manager_name);
		version_name_tv = (TextView) findViewById(R.id.version_name_tv);
		pay_et = (EditText) findViewById(R.id.pay_et);
		//		progress_tv = (TextView) findViewById(R.id.progress_tv);
		dingdianshu_tv = (TextView) findViewById(R.id.dingdianshu_tv);
		dingdanjine_tv = (TextView) findViewById(R.id.dingdanjine_tv);
		banci_tv = (TextView) findViewById(R.id.banci_tv);
		zhongduandanhao_tv = (TextView) findViewById(R.id.zhongduandanhao_tv);
		error_count = (TextView) findViewById(R.id.error_count);
		mygridview = (GridView) findViewById(R.id.mygridview);

		init();
		initService();
		initLiGangSuoDingLayout();
		initBanci();

		initPayment();
	}

	private boolean hasAdd;

	private void initPayment() {
		NewPos.getInstance(mActivity).getPayment(
				new GenericsCallback<PaymentInfoResp>(
						new JsonGenericsSerializator()) {

					@Override
					public void onError(Call call, Exception e, int id) {
						e.printStackTrace();
					}

					@Override
					public void onResponse(PaymentInfoResp response,
					                       int id) {
						if (response.result == 0) {

							if (response.paymentTypes != null
									&& response.paymentTypes.size() > 0) {

								ArrayList<PaymentInfo> infos     = new ArrayList<>();
								ArrayList<PaymentInfo> onLinePay = new ArrayList<>();
								for (PaymentInfo payment : response.paymentTypes) {
									if (payment.id == 1 || payment.id == 2 || payment.id == -8) {
										if (hasAdd)
											continue;
										else {
											hasAdd = true;
											onLinePay.add(payment);
										}
									} else {
										if (payment.id == 4 || payment.id == 5 || payment.id == -33)
											continue;
										infos.add(payment);
									}
								}
								onLinePay.addAll(infos);
								mygridview
										.setAdapter(new PayMethodAdapter(mActivity, onLinePay));

								for (PaymentInfo payment : response.paymentTypes) {
									switch (payment.id) {
										case NewPos.WX_PAY:
											WXPay.APPID = payment.appIDs;
											WXPay.SUB_MCH_ID = payment.subMchID;
											WXPay.KEY = payment.keyStr;
											WXPay.MCH_ID = payment.mchID;
											if (payment.status == 1
													&& !TextUtils
													.isEmpty(WXPay.APPID)) {
												NewPos.HAVA_WX = true;
											}
											break;
										case NewPos.ALI_PAY:
											AlipayConfig.APPID = payment.appIDs;
											AlipayConfig.key = payment.keyStr;
											AlipayConfig.appsecret = payment.appsecret;
											if (payment.status == 1
													&& !TextUtils
													.isEmpty(AlipayConfig.APPID)) {
												NewPos.HAVA_ALI = true;
											}
											break;

										case NewPos.SWIFTPASS_PAY:
											//如果威富通支付有的话
											if (payment.status == 1) {
												NewPos.HAVA_SWIFTPASS = true;
											}
											break;
										default:
											break;
									}


								}
							}
						} else {
						}
					}
				});

	}

	private void startToCheckPrinter() {
		mHandler.postDelayed(checkPrinterRunable, 10 * 1000);//10秒后开始检测
		mHandler.postDelayed(checkFuping, 1000);//10秒后开始检测
		showFuPing("欢迎光临", Common.SHOP_INFO.sname);//1表示消费金额改变
	}

	/**
	 * activity显示到屏幕则开启动画
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus)
			dayin_imgBg.start();//开启帧动画
	}

	private Runnable checkPrinterRunable = new Runnable() {
		@Override
		public void run() {
			mHandler.sendEmptyMessage(REFRESHUI);
			mHandler.postDelayed(checkPrinterRunable, 1000 * 60);//1分钟
		}
	};

	private static final int REFRESHUI = 10000;
	private LoadingDialog ld;

	@Override
	public void showPayLoadingDialog() {
		ld = new LoadingDialog(this);
		ld.setLoadingText("支付正在处理中，请稍后...")
				.setSuccessText("支付成功")//显示加载成功时的文字
				.setFailedText("支付失败")
				.setInterceptBack(true)
				.setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
				.setRepeatCount(1)
				.show();
	}

	@Override
	public void dismissPayLoadingDialog(PayStatu type, String reason) {
		switch (type) {
			case SUCCESS://成功
				ld.loadSuccess();
				break;
			case FAILE://失败
				ld.setFailedText(reason);
				ld.loadFailed();
				break;
		}
	}

	private void initBanci() {
		NewPos.getInstance(mActivity)
				.getOpenWorkShift(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						e.printStackTrace();
						showToast(Common.errMsg);
					}

					@Override
					public void onResponse(String response, int id) {
						Log.e(TAG, "response>>" + response);
						PrintFile
								.printResult2("Class_EasyMainActivitgy_method_initBanci_onResponse_" + response + "\n");
						try {
							if (GsonUtils.getIntData("result", response) == 0) {
								String content = GsonUtils
										.getData("content", response);
								if (!TextUtils.isEmpty(content) && !"null".equals(content)) {
									//保存当前的班次
									Common.workShifit = GsonUtils
											.getSingleBean(content, WorkShift.class);
									refreshUI();
									banci_tv.setText(Common.workShifit.getDefinitionName());
								} else {
									initKaiBan();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});


	}

	private void initKaiBan() {
		getWorkShif();
	}

	private void getWorkShif() {
		new NewPos().getInstance(this).getWorkShift(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
				showErrorTipsDialog(1, e.getMessage(), false);
			}

			@Override
			public void onResponse(String response, int id) {
				Log.e(TAG, "response>" + response);
				PrintFile
						.printResult2("Class_EasyMainActivity_Method_getWorkShift()_onResponse>" + response + "\n");
				try {
					WorkShiftInfo bean = GsonUtils
							.getSingleBean(response, WorkShiftInfo.class);
					Common.workShifIno = bean.getContent();
					if (Common.workShifIno == null || Common.workShifIno.size() == 0) {
						showErrorTipsDialog(1, "请到\"智慧餐厅后台管理系统-门店运营管理-班次管理\"添加班次!", false);
					} else
						showKaiBanDialog();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	@Override
	public void showKaiBanDialog() {
		KaiBanDialog dialog = new KaiBanDialog();
		dialog.show(getSupportFragmentManager(), "KaiBanDialog");
	}

	private void initLiGangSuoDingLayout() {
		easymain_mian_ly = (RelativeLayout) findViewById(R.id.easymain_mian_ly);
		ligangshuoding_ly = (FrameLayout) findViewById(R.id.ligangshuoding_ly);

		findViewById(R.id.jiesuo_btn).setOnClickListener(this);
		pwd_et = (DropEditText) findViewById(R.id.ligangsuoding_pwd_et);
		TextView ligangren_tv = (TextView) findViewById(R.id.ligangren_tv);
		ligangren_tv
				.setText("请 " + Common.SHOP_INFO.realname + "（" + Common.SHOP_INFO.userphone + "）输入密码解锁");
		if (SharedPreferencesUtil.getShowLiGangSuoDing(this)) {
			ligangshuoding_ly.setVisibility(View.VISIBLE);
			easymain_mian_ly.setVisibility(View.GONE);
			pwd_et.setText("");
		}

	}


	private UpLoadOrderService service;
	private ServiceConnection conn = new ServiceConnection() {


		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			isBound = true;
			UpLoadOrderService.MyBinder myBinder = (UpLoadOrderService.MyBinder) binder;
			service = myBinder.getService();
			Log.i("DemoLog", "ActivityA onServiceConnected");
			//			int num = service.getRandomNumber();
			service.startTimer();
			service.startCheckPrint();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound = false;
			Log.i("DemoLog", "ActivityA onServiceDisconnected");
		}
	};
	public boolean isBound;

	private void initService() {
		Intent intent = new Intent(this, UpLoadOrderService.class);
		intent.putExtra("from", "ScreenProtectedActivity_new");
		Log.i("DemoLog", "----------------------------------------------------------------------");
		Log.i("DemoLog", "ScreenProtectedActivity_new 执行 bindService");
		bindService(intent, conn, BIND_AUTO_CREATE);
	}

	private void stopService() {
		unbindService(conn);
	}

	private TextView time1;
	private TextView time2;
	private TextView shop_name;
	private TextView ip_address;
	private TextView manager_name;
	private TextView version_name_tv;


	//	@Override
	//	public boolean onKeyDown(int keyCode, KeyEvent event) {
	//		if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_HOME && keyCode != KeyEvent.KEYCODE_MENU) {
	//			mScanGunKeyEventHelper.analysisKeyEvent(event);
	//		}
	//		return super.onKeyDown(keyCode, event);
	//	}


	//	private ScanGunKeyEventHelper mScanGunKeyEventHelper;
	private EasyPayPresenter mEasyPayPresenter;

	private StringBuilder sb;

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService();
	}

	@Override
	protected void onPause() { //如果存在activity跳转，需要做清理操作
		super.onPause();
		mDSKernel.onDestroy();
		mDSKernel = null;
	}


	private void init() {
		sb = new StringBuilder();
		new Thread(currentTimeRunnable).start();
		mEasyPayPresenter = new EasyPayPresenter(this, this);
		//		mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
		mHandler.postDelayed(currentTimeRunnable, 1000);
		shop_name.setText(Common.SHOP_INFO.sname);
		ip_address.setText(Common.SHOP_INFO.tname + " | " + NetworkUtils.getIPAddress(true));
		manager_name.setText(Common.SHOP_INFO.realname);
		version_name_tv.setText(AppUtils.getAppName() + "V " + AppUtils.getAppVersionName());
		pay_et.setFocusable(false);
		error_count.setVisibility(TextUtils.isEmpty(SharedPreferencesUtil
				.getNewVersionPath(mActivity)) ? View.GONE : View.VISIBLE);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void changePayStatu(PayStatus event) {
		if (event.getPayStatu().equals("USERPAYING")) {
			ld.setLoadingText("用户正在输入密码,请稍后...");
		} else if (event.getPayStatu().equals("payFail")) {
			mEasyPayPresenter.payFail("支付失败");
		} else if (event.getPayStatu().equals("paySuccess")) {
			mEasyPayPresenter.paySuccess();
		}
	}

	private SimpleDateFormat format1             = new SimpleDateFormat("HH:mm:ss");// 用于格式化日期,作为日志文件名的一部分
	private SimpleDateFormat format2             = new SimpleDateFormat("yyyy-MM-dd");// 用于格式化日期,作为日志文件名的一部分
	private Runnable         currentTimeRunnable = new Runnable() {
		@Override
		public void run() {
			mHandler.sendEmptyMessage(0);
			mHandler.postDelayed(currentTimeRunnable, 1000);
		}
	};
	private Handler          mHandler            = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case REFRESHUI:
					dayin_img
							.setBackgroundResource(Common.isPrintOk ? R.drawable.dayin_ok_img : R.drawable.dayin_fail_img);
					dayin_imgBg = (AnimationDrawable) dayin_img.getBackground();
					dayin_imgBg.start();
					break;
				case 0:
					time1.setText(format1.format(new Date()));
					time2.setText(format2.format(new Date()));
					break;
				case RESETFUPING:
					showFuPing("欢迎光临", Common.SHOP_INFO.sname);//1表示消费金额改变
					break;

			}
			return false;
		}
	});

	@Override
	public void showPayInputDialog(int type) {
		mPayInputDialog = PayInputDialog.newInstance(type);
		mPayInputDialog.show(getSupportFragmentManager(), "mPayInputDialog");
		mPayInputDialog.setOnDialogDismiss(new PayInputDialog.OnDialogDismissListener() {
			@Override
			public void onDialogDismiss() {
				WindowUtil.hiddenKey();
			}
		});
	}

	@Override
	public void generateTerminalOrderId() {
		int num = 1;
		if (Common.workShifit != null) {
			//			terminalOrderId = DataSupport
			//					.where("workshiftid=?", String.valueOf(Common.workShifit.getId())).
			//							count(OrderReq.class);
			Cursor cursor = DataSupport
					.findBySQL("select * from orderreq where workshiftid=? and userphone=?", Common.workShifit
							.getId() + "", Common.SHOP_INFO.userphone);
			while (cursor.moveToNext()) {
				num++;
			}
		}
		terminalOrderId = num;
		zhongduandanhao_tv.setText(terminalOrderId + "");
	}

	@Override
	public int getTerminalOrderId() {
		return terminalOrderId;
	}

	@Override
	public ErrorDialog showErrorTipsDialog(int type, String printName, boolean showRadioBtn) {
		ErrorDialog dialog = null;
		switch (type) {
			case 0:
				dialog = showErrorDialog("打印机" + Common.SHOP_PRINTER
						.getDescription() + "连接异常,请检查!", showRadioBtn);
				break;
			case 1:
				dialog = showErrorDialog(printName, showRadioBtn);
				break;
		}

		return dialog;
	}


	private EditText    pay_et;
	private PopupWindow mTipPop;
	private int         terminalOrderId;
	//	private TextView    progressTv;
	//	private boolean     isAdd;

	private void showTipPop(View view) {
		View contentView = LayoutInflater.from(mActivity)
				.inflate(R.layout.poplayout_tip2, null);
		TextView tv   = (TextView) contentView.findViewById(R.id.showyingbuzou_tv);
		String   html = "<font color=\"red\">" + "*" + "</font>收银步骤";
		tv.setText(Html.fromHtml(html));
		mTipPop = new PopupWindow(contentView);
		mTipPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		mTipPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		mTipPop.showAsDropDown(view, -2, 10);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.num_one:
				setText("1");
				break;
			case R.id.num_two:
				setText("2");
				break;
			case R.id.num_three:
				setText("3");
				break;
			case R.id.num_four:
				setText("4");
				break;
			case R.id.num_five:
				setText("5");
				break;
			case R.id.num_six:
				setText("6");
				break;
			case R.id.num_seven:
				setText("7");
				break;
			case R.id.num_eight:
				setText("8");
				break;
			case R.id.num_nine:
				setText("9");
				break;
			case R.id.num_zero:
				setText("0");
				break;
			case R.id.num_dot:
				if (!TextUtils.isEmpty(sb.toString()) && !sb.toString().contains("."))
					setText(".");
				break;
			case R.id.num_add_half_one:
				calculate_addNum("0.5");
				break;
			case R.id.num_clear:
				calculate_clear();
				break;
			case R.id.num_equal:
				calculate_equal();
				break;
			case R.id.num_add:
				calculate_add();
				break;
			case R.id.num_del:
				calculate_del();
				break;
			//			case R.id.num_yidongzhifu:
			//				mEasyPayPresenter.onLinePay();
			//				break;
			//			case R.id.num_cash:
			//				mEasyPayPresenter.cashPay();
			//				break;
			//			case R.id.num_yuangongka:
			//				mEasyPayPresenter.memberPay();
			//				break;
			//			case R.id.num_add_cash_ten:
			//				calculate_addNum("10");
			//				break;
			//			case R.id.num_add_cash_twenty:
			//				calculate_addNum("20");
			//				break;
			//			case R.id.num_add_cash_fifty:
			//				calculate_addNum("50");
			//				break;
			//			case R.id.num_yinlianka:
			//				break;
			//			case R.id.num_coupons:
			//				break;

			//功能按键
			case R.id.dingdantongji_ly:
				goDianDanTongJiActivity();
				break;
			case R.id.kaiqianxiang_ly:
				if (SharedPreferencesUtil.getKaiqianxiangAthority(mActivity)) {
					//弹出钱箱
					openBox();
				} else {
					if (SharedPreferencesUtil.getQuanXian(mActivity))
						showQianXiangLoginDialog();
					else
						showToast("您没有开钱箱的权限!");
				}
				break;
			case R.id.ligangsuoding_ly:
				showLiGangSuoDingDialog();
				break;
			case R.id.tip_ly:
				if (mTipPop != null && mTipPop.isShowing()) {
					mTipPop.dismiss();
				} else {
					showTipPop(v);
				}
				break;
			case R.id.gongnengxuanxiang_ly:
				if (mPopWindow != null && mPopWindow.isShowing()) {
					mPopWindow.dismiss();
				} else {
					showPopupWindow(v);
				}
				break;
			case R.id.huiyuanguanli_ly:
				startActivity(new Intent(mActivity, HuiYuanChaXunActivity.class));
				mPopWindow.dismiss();
				break;
			case R.id.jiaobanjilv_ly:
				//				ToastUtils.showToast(EasyMainActivity.this, "交班记录");
				startActivity(new Intent(mActivity, JiaoBanJiLvActivity.class));
				mPopWindow.dismiss();
				break;
			case R.id.yuyan_ly:
				//				ToastUtils.showToast(EasyMainActivity.this, "语言");


				ChangeLanguageDialog dialog = ChangeLanguageDialog.newInstance();
				dialog.setOnLanguageSelectListener(this);
				dialog.show(getSupportFragmentManager(), "ChangeLanguageDialog");
				mPopWindow.dismiss();
				break;
			case R.id.gaojishezhi_ly:
				//				ToastUtils.showToast(EasyMainActivity.this, "高级设置");
				startActivity(new Intent(mActivity, GaoJiSheZhiActivity.class));
				mPopWindow.dismiss();
				break;
			case R.id.login_out_ly:
				//				ToastUtils.showToast(EasyMainActivity.this, "退出");
				BaseApplication.getInstance().finishActivity(this);
				mPopWindow.dismiss();
				break;

			case R.id.print_ly:
				//				ToastUtils.showToast(EasyMainActivity.this, "退出");
				startActivity(new Intent(mActivity, DaYinActivity.class));
				break;
			case R.id.rijie_ly:
				//				ToastUtils.showToast(EasyMainActivity.this, "退出");
				//				startActivity(new Intent(mActivity, JiaoBanDialog.class));
				//				JiaoBanDialog dialog = JiaoBanDialog.newInstance(1);
				//				dialog.show
				mEasyPayPresenter.doRiJie();
				mPopWindow.dismiss();
				break;
			case R.id.statu_ly:
				//				ToastUtils.showToast(EasyMainActivity.this, "退出");
				startActivity(new Intent(mActivity, WangLuoZhuangTaiActivity.class));
				break;
			case R.id.rizhi_ly:
				//				ToastUtils.showToast(EasyMainActivity.this, "退出");
				//				upLogFile();
				showSetDialog(getResources()
						.getStringArray(R.array.uploadfileday), upLoadFileDay_RESULT_CODE);
				break;
			case R.id.user_name_ly:

				if (Common.workShifit != null)
					showJiaoBanDialog(0);

				else
					//					ToastUtils.showToast(EasyMainActivity.this, "未开班");
					showKaiBanDialog();
				break;
			case R.id.ligangshuoding_ly:
				ToastUtils.showToast(mActivity, "lll");
				return;

			case R.id.jiesuo_btn:
				String pwd = pwd_et.getText().toString().trim();
				if (TextUtils.isEmpty(pwd)) {
					ToastUtils.showToast(mActivity, "密码不能为空!");
					return;
				}
				if (pwd.equals(Common.SHOP_INFO.userpwd)) {
					WindowUtil.hiddenKey();
					ligangshuoding_ly.setVisibility(View.GONE);
					easymain_mian_ly.setVisibility(View.VISIBLE);
					SharedPreferencesUtil.saveShowLiGangSuoDing(mActivity, false);
				} else {
					ToastUtils.showToast(mActivity, "输入密码不正确!");
				}
				break;

			case R.id.center_bg_ly:
			case R.id.zhongduandanhao_layout:
			case R.id.textView2:

			case R.id.bottom_bg_ly:
			case R.id.relativeLayout:
				if (mPopWindow != null && mPopWindow.isShowing())
					mPopWindow.dismiss();
				if (mTipPop != null && mTipPop.isShowing())
					mTipPop.dismiss();
				break;
		}
	}

	private void showSetDialog(String[] data, int requestcode) {
		SetDialog.newInstance(data, requestcode)
				.show(getSupportFragmentManager(), "SetDialog");
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onSelect(ResultEvent event) {
		int requestcode = event.getRequestcode();
		int position    = event.getPosition();
		switch (requestcode) {
			case upLoadFileDay_RESULT_CODE:
				upLogFile(position);
				break;
		}
	}

	@Override
	public void openBox() {
		if (Common.SHOP_PRINTER != null && Common.SHOP_PRINTER.getVendor().equals("shangmi_fix"))
			BaseApplication.getInstance().openBox();//商米打印
		else
			PosKitchenPrintAdapter.getInstance()
					.openBox();//网口打印

	}


	private void showJiaoBanDialog(int type) {
		JiaoBanDialog dialog = JiaoBanDialog.newInstance(type);
		dialog.show(getSupportFragmentManager(), "JiaoBanDialog");
	}


	private void showLiGangSuoDingDialog() {
		SharedPreferencesUtil.saveShowLiGangSuoDing(this, true);
		ligangshuoding_ly.setVisibility(View.VISIBLE);
		easymain_mian_ly.setVisibility(View.GONE);
		pwd_et.setText("");
		if (mPopWindow != null) {
			mPopWindow.dismiss();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onPayMethodEvent(PayMethodEvent event) {
		//提醒用户输入密码
		PayReqModel.cashbox = event.isCashbox();
		if (event.getId() == 1 || event.getId() == 2 || event.getId() == -8) {
			mEasyPayPresenter.onLinePay();
		} else if (event.getId() == 3) {
			mEasyPayPresenter.memberPay();
		} else if (event.getId() == 0) {
			mEasyPayPresenter.cashPay();
		} else if (event.getId() == 4 || event.getId() == 5 || event.getId() == -33) {
			ToastUtils.showToast(mActivity, "暂未开通此支付方式");
		} else {
			mEasyPayPresenter.userDefindedPay(event.getId(), event.getName());
		}
	}

	private PopupWindow mPopWindow;

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void OnScanSuccess(OnEditTextFinishEvent event) {
		//提醒用户输入密码
		if (event.getType() == PayReqModel.PTID_SSS_MEMBER_CHUZHI) {
			mEasyPayPresenter.doMemberLogin(event.getCode());
		} else if (event.getType() == PayReqModel.PTID_SSS_ONLINEPAY) {
			mEasyPayPresenter.onGunScanSuccess(event.getCode());
			dismissPayInputDialog();
		}
	}

	@Override
	public void dismissPayInputDialog() {
		try {
			if (mPayInputDialog != null)
				mPayInputDialog.dismiss();
		} catch (Exception e) {
			FileLog.log(Common.errMsg, EasyMainActivity.class, "dismissPayInputDialog", "log", "关闭输入对话框的时候出现了异常!");
			ToastUtils.showToast(mActivity, "请关闭对话框重试!");
		}
		WindowUtil.hiddenKey();
	}

	private static final int      RESETFUPING   = 9000;
	private              Runnable checkFuping   = new Runnable() {
		@Override
		public void run() {
			if (mLastShowTime != null) {
				if (DateUtils.dateDiff(mLastShowTime, new Date()) > 5 * 1000)
					mHandler.sendEmptyMessage(RESETFUPING);
			}
			mHandler.postDelayed(checkFuping, 1000 * 20);//1分钟
		}
	};
	private              Date     mLastShowTime = null;

	@Override
	public void showFuPing(String title, String content) {
		try {
			//			JSONObject json = new JSONObject();
			//			json.put("dataModel", "SHOW_IMG_WELCOME");
			//			json.put("data", "gaolulin");
			//			mDSKernel.sendCMD(SF.DSD_PACKNAME, json.toString(), -1, null);
			mLastShowTime = new Date();
			JSONObject json = new JSONObject();
			json.put("title", title);//title为上面一行的标题内容
			json.put("content", content);//content为下面一行的内容
			String jsonStr = json.toString();
			//构建DataPacket类
			DataPacket packet = UPacketFactory.buildShowText(DSKernel
					.getDSDPackageName(), jsonStr, mISendCallback);//第一个参数是接收数据的副显应用的包名，这里参照Demo就可以,第二个参数是要显示的内容字符串，第三个参数为结果回调。
			if (mDSKernel != null && mDSKernel.isConnected())
				mDSKernel.sendData(packet);//调用sendData方法发送文本
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void changePayTips(String s) {
		ld.setInterceptBack(false);
		ld.setLoadingText(s);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onUserPay(PayStatus event) {
		//提醒用户输入密码

	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFinish(OnFinishEvent event) {
		if ("jiaobansuccess".equals(event.getMsg())) {
			startActivity(new Intent(mActivity, BindActivity.class));
			BaseApplication.getInstance().clearStatic();
			BaseApplication.getInstance().finishActivity(this);
			SharedPreferencesUtil
					.saveCheckPrintBeforeNewOrder(mActivity, true);
		} else if ("jiaobanfail".equals(event.getMsg())) {
			ErrorDialog dialog = showErrorDialog(event.getContent(), false);
			dialog.setonCancleClickListener(this);
		} else if ("kaibansuccess".equals(event.getMsg())) {
			refreshUI();
			banci_tv.setText(event.getContent());
		}

	}

	private static final int upLoadFileDay_RESULT_CODE = 11;

	private void upLogFile(int position) {
		//0.表示今天
		//1.表示昨天
		//2.表示前天
		File   log = FileUtils.getUploadLog(position);
		String day = null;
		if (log == null) {
			switch (position) {
				case 0:
					day = "没有今天的日志";
					break;
				case 1:
					day = "没有昨天的日志";
					break;
				case 2:
					day = "没有前天的日志";
					break;
				default:
					day = "找不到日志";
					break;
			}
			ToastUtils.showToast(this, day);
			return;
		}
		NewPos.getInstance(this).uploadLog(log, this);
	}

	private void showPopupWindow(View view) {
		View contentView = LayoutInflater.from(mActivity)
				.inflate(R.layout.popuplayout, null);
		contentView.findViewById(R.id.huiyuanguanli_ly).setOnClickListener(this);
		contentView.findViewById(R.id.jiaobanjilv_ly).setOnClickListener(this);
		contentView.findViewById(R.id.yuyan_ly).setOnClickListener(this);
		contentView.findViewById(R.id.gaojishezhi_ly).setOnClickListener(this);
		contentView.findViewById(R.id.login_out_ly).setOnClickListener(this);
		contentView.findViewById(R.id.rijie_ly).setOnClickListener(this);
		contentView.findViewById(R.id.rizhi_ly).setOnClickListener(this);
		View id = contentView.findViewById(R.id.error_count);
		id.setVisibility(TextUtils.isEmpty(SharedPreferencesUtil
				.getNewVersionPath(mActivity)) ? View.GONE : View.VISIBLE);
		//		jiaobanjilv_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		mPopWindow = new PopupWindow(contentView);
		mPopWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopWindow.setTouchable(true);
		mPopWindow.setOutsideTouchable(true);
		mPopWindow.showAsDropDown(view, 0, 20);
	}

	//	private void showLiGangSuoDingDialog() {
	//		mLiGangSuoDingDialog = KaiBanDialog.newInstance();
	//		mLiGangSuoDingDialog.show(getSupportFragmentManager(), "tag");
	//		hideBottomUIMenu();
	//	}


	private CommonEditDialog mQianXiangDialog;
	private PayInputDialog   mPayInputDialog;

	private void showQianXiangLoginDialog() {
		mQianXiangDialog = CommonEditDialog.newInstance(PowerController.OPEN_POS_BOX);
		mQianXiangDialog.show(getSupportFragmentManager(), "tag");
	}

	private void goDianDanTongJiActivity() {
		startActivity(new Intent(this, DingDanTongJiActivity.class));
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onCheckAhority(OnCheckAthorityEvent event) {
		if (event.isStatu() && event.getType() == PowerController.OPEN_POS_BOX) {
			//弹出钱箱
			openBox();
			WindowUtil.hiddenKey();
		}
	}


	private void setText(String text) {
		if ("0".equals(sb.toString()) && "0".equals(text)) {
			return;
		}
		if (sb.toString().length() == 6) {
			ToastUtils.showToast(EasyMainActivity.this, "输入金额有误,请检查!");
			return;
		}
		if (sb.length() == 1 && "0".equals(sb.toString()) && !text.equals(".")) {
			sb.delete(0, sb.length());
		}

		if (!isAdd && isEqual) {
			calculate_clear();
			isEqual = false;
		}

		//原来的
		sb.append(text);
		if ("0.".equals(sb.toString())) {
			pay_et.setText(sb.toString());
		} else {
			//			String price = PriceUtil.formatPrice(sb.toString());
			pay_et.setText(sb.toString());
			isAdd = false;
		}

	}


	private void calculate_adDHalfOfOne() {
		String cur = pay_et.getText().toString();
		sb.delete(0, sb.toString().length());
		BigDecimal sum;
		if (!TextUtils.isEmpty(cur)) {
			sum = PriceUtil.add(new BigDecimal("0.5"), new BigDecimal(cur));
		} else {
			sum = new BigDecimal("0.5");
		}
		numArray.add("0.5");
		pay_et.setText(sum.toString());
	}

	private void calculate_del() {
		//		numArray.clear();
		String cur = pay_et.getText().toString().trim();
		if (isAdd || TextUtils.isEmpty(cur)) {
			return;
		}
		sb.delete(0, sb.length());
		if (!TextUtils.isEmpty(cur))
			sb.append(cur).delete(cur.length() - 1, cur.length());
		//		isAdd = false;
		pay_et.setText(sb.toString());
	}

	private void calculate_add() {
		if (isAdd)
			return;
		if (TextUtils.isEmpty(pay_et.getText().toString()) || pay_et.getText().toString()
				.endsWith("."))
			return;
		numArray.add(pay_et.getText().toString());
		sb.delete(0, sb.toString().length());
		BigDecimal sum = new BigDecimal("0");
		for (String num : numArray) {
			sum = PriceUtil.add(sum, new BigDecimal(num));
		}
		isAdd = true;
		pay_et.setText(sum.toString());
	}

	private List<String> numArray = new ArrayList<>();

	private boolean isAdd;

	private void calculate_addNum(String num) {
		String cur = pay_et.getText().toString();
		sb.delete(0, sb.toString().length());
		BigDecimal sum;
		if (!TextUtils.isEmpty(cur)) {
			sum = PriceUtil.add(new BigDecimal(num), new BigDecimal(cur));
		} else {
			sum = new BigDecimal(num);
		}
		numArray.clear();
		numArray.add(sum.toString());
		isAdd = true;
		pay_et.setText(sum.toString());
	}


	private void calculate_equal() {
		if (isAdd)
			return;
		if (TextUtils.isEmpty(pay_et.getText().toString()) || pay_et.getText().toString()
				.endsWith("."))
			return;
		numArray.add(pay_et.getText().toString());
		sb.delete(0, sb.toString().length());
		BigDecimal sum = new BigDecimal("0");
		for (String num : numArray) {
			sum = PriceUtil.add(sum, new BigDecimal(num));
		}
		pay_et.setText(sum.toString());
		isAdd = true;
		isEqual = true;
	}

	boolean isEqual = false;

	public void initPayChange() {
		pay_et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!TextUtils.isEmpty(s)) {
					String content = s.toString();
					String title   = "总金额";
					if (content.contains(".") && content.length() < 3)
						return;
					content = "￥ " + PriceUtil.formatPrice(content);
					showFuPing(title, content);//1表示消费金额改变
				} else {
					showFuPing("欢迎光临", Common.SHOP_INFO.sname);//1表示消费金额改变
				}
			}
		});
	}


	@Override
	public void calculate_clear() {
		sb.delete(0, sb.toString().length());
		pay_et.setText(sb.toString());
		pay_et.setSelection(sb.toString().length());
		numArray.clear();
	}


	@Override
	public String getCost() {
		String text = pay_et.getText().toString();
		if (TextUtils.isEmpty(text))
			return "0.00";
		String price = PriceUtil.formatPrice(text);
		Log.e(TAG, "cost" + price);
		return price;
	}

	private DSKernel mDSKernel = null;

	@Override
	protected void onResume() {
		super.onResume();
		refreshUI();
		if (mDSKernel != null) {
			mDSKernel.checkConnection();
		} else {
			initSdk();
		}
	}

	private void initSdk() {
		mDSKernel = DSKernel.newInstance();
		mDSKernel.init(this, mIConnectionCallback);
		mDSKernel.addReceiveCallback(mIReceiveCallback);
		//		mDSKernel.addReceiveCallback(mIReceiveCallback2);
		mDSKernel.removeReceiveCallback(mIReceiveCallback);
		//		mDSKernel.removeReceiveCallback(mIReceiveCallback2);
		//		showFuPing("欢迎光临", Common.SHOP_INFO.sname);//1表示消费金额改变
	}

	@Override
	public void refreshUI() {
		generateTerminalOrderId();
		BigDecimal sum   = new BigDecimal("0");
		int        count = 0;
		//获取当前班次的 单数和总数
		if (Common.workShifit != null) {

			//			List<OrderReq> orderReqList = DataSupport
			//					.where("workshiftid=?","23" )
			//					.where("paymentstatus=?", "PAYED")
			//					.find(OrderReq.class);
			Cursor cursor = DataSupport
					.findBySQL("select * from orderreq where workshiftid=?  and userphone=?", Common.workShifit
							.getId() + "", Common.SHOP_INFO.userphone);
			while (cursor.moveToNext()) {
				if ("REFUND"//如果是退款，那么不包含不退钱的部分
						.equals(cursor.getString(cursor.getColumnIndex("paymentstatus")))) {
					if (cursor
							.getInt(cursor.getColumnIndex("isrefundmoney")) == 1) {
						sum = PriceUtil.add(sum, new BigDecimal(String
								.valueOf(cursor.getFloat(cursor.getColumnIndex("cost")))));
					}
				} else {
					sum = PriceUtil.add(sum, new BigDecimal(String
							.valueOf(cursor.getFloat(cursor.getColumnIndex("cost")))));
					count++;
				}

			}
		} else {
			sum = new BigDecimal("0");
			count = 0;
		}
		dingdianshu_tv.setText(String.valueOf(count));
		dingdanjine_tv.setText(sum.toString());
	}

	@Override
	public void onCancle() {
		startActivity(new Intent(mActivity, BindActivity.class));
		BaseApplication.getInstance().clearStatic();
		BaseApplication.getInstance().finishActivity(this);
		SharedPreferencesUtil
				.saveCheckPrintBeforeNewOrder(mActivity, true);
	}

	final String[] locals = {"zh_CN", "en"};

	@Override
	public void onLanguageSelect(int position) {
		Store.setLanguageLocal(mActivity, locals[position]);
		EventBus.getDefault().post("EVENT_REFRESH_LANGUAGE");
	}
}
