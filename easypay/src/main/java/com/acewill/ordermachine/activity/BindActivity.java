package com.acewill.ordermachine.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acewill.chikenprintlibrary.model.PowerController;
import com.acewill.ordermachine.BaseApplication;
import com.acewill.ordermachine.R;
import com.acewill.ordermachine.adapter.MyAdapter;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.constant.Contants;
import com.acewill.ordermachine.dialog.EnvironmentDialog;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.fragmentdialog.DownLoadProgressDialog;
import com.acewill.ordermachine.fragmentdialog.FogetPwdDialog;
import com.acewill.ordermachine.fragmentdialog.NewVersionDialog;
import com.acewill.ordermachine.model.DishKindRes;
import com.acewill.ordermachine.model.DishModel;
import com.acewill.ordermachine.model.LoginUserRecord;
import com.acewill.ordermachine.model.MenuData;
import com.acewill.ordermachine.model.StoreInfoResp;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.util.FileUtils;
import com.acewill.ordermachine.util.SharedPreferencesUtil;
import com.acewill.ordermachine.util.permission.IPermission;
import com.acewill.ordermachine.util_new.AppUtils;
import com.acewill.ordermachine.util_new.DownLoadAPPUtils;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.LogUtils;
import com.acewill.ordermachine.util_new.NetworkUtils;
import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.ordermachine.util_new.TimeUtil;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.ordermachine.widget.DropEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * Author：Anch
 * Date：2017/11/16 9:29
 * Desc：
 */
public class BindActivity extends BaseActivity implements View.OnClickListener {
	private static final String  TAG   = "BindActivity";
	private              boolean click = false;

	//	http://sz.canxingjian.com/management/user/sendValidateCode


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind);
		NetworkUtils.NetworkType type = NetworkUtils.getNetworkType();
		switch (type) {
			case NETWORK_2G:
				PrintFile.printResult2("NETWORK_2G");
				break;
			case NETWORK_3G:
				PrintFile.printResult2("NETWORK_3G");
				break;
			case NETWORK_4G:
				PrintFile.printResult2("NETWORK_4G");
				break;
			case NETWORK_WIFI:
				PrintFile.printResult2("NETWORK_WIFI");
				break;
			case NETWORK_UNKNOWN:
				PrintFile.printResult2("NETWORK_UNKNOWN");
				break;
			case NETWORK_NO:
				PrintFile.printResult2("NETWORK_NO");
				break;
		}
		initView();
		initEvent();
		initData();
		if (Common.initInfo(this)) {
			terminalLogin();
		} else {
			bind_layout.setVisibility(View.VISIBLE);
			login_layout.setVisibility(View.GONE);
		}
		autoUploadLog();
		initPermission();
	}

	private void initPermission() {
		String[] permission = new String[]{Manifest.permission.CAMERA,
				Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.ACCESS_FINE_LOCATION,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.READ_EXTERNAL_STORAGE};
		requestRunTimePermission(permission, new PermisionListenr());
	}

	private class PermisionListenr
			implements IPermission {
		@Override
		public void onGranted() {
			LogUtils.e(TAG, "onGranted");
		}

		@Override
		public void onDenied(List<String> deniedPermission) {
			LogUtils.e(TAG, "onDenied");
		}
	}

	ArrayList<String> phoneList;
	ArrayList<String> pwdList;


	private void initData() {
		if (NewPos.getInstance(this)
				.isDebug()) {
			NewPos.BASE_URL = "sz.canxingjian.com/";
		} else {
			NewPos.BASE_URL = SharedPreferencesUtil.getBaseUrl(mActivity);
		}

		//		 	NewPos.BASE_URL = NewPos.getInstance(this)
		//				.isDebug() ? "192.168.1.120:4444/" : "www.smarant.com/";
		List<LoginUserRecord> recordlist = DataSupport.order("createat desc")
				.find(LoginUserRecord.class);
		if (recordlist == null || recordlist.size() == 0)
			return;
		phoneList = new ArrayList<String>();
		for (LoginUserRecord record : recordlist) {
			phoneList.add(record.getPhone() + "(" + record.getRealname() + ")");
		}
		if (phoneList != null && phoneList.size() > 0) {
			phone_et.setText(phoneList.get(0));
			phone_et.setAdapter(new PhoneAdapter(phoneList));
		}
	}

	/**
	 * 自动上传昨天日志
	 */
	private void autoUploadLog() {
		String lastUploadTime = SharedPreferencesUtil
				.getLastUploadTime(mActivity);
		//获取当前时间
		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		//是不是同一天
		if (!today.equals(lastUploadTime)) {
			NewPos.getInstance(this).uploadLog2(FileUtils.getUploadLog(1), this);
		}
	}

	private void initEvent() {
		findViewById(R.id.bind_btn).setOnClickListener(this);
		findViewById(R.id.login_btn).setOnClickListener(this);
		findViewById(R.id.bind_layout2).setOnClickListener(this);
		findViewById(R.id.bind_layout3).setOnClickListener(this);
		findViewById(R.id.exit_system_btn).setOnClickListener(this);
		findViewById(R.id.where_tofind_tv).setOnClickListener(this);
		findViewById(R.id.bind_bg).setOnClickListener(this);
	}

	private DropEditText   bind_et;
	private DropEditText   phone_et;
	private DropEditText   pwd_et;
	private RelativeLayout bind_layout;
	private RelativeLayout login_layout;
	private TextView       store_name_tv;
	private TextView       version_name_tv;
	private TextView       shengyushijian_tv;
	private TextView       name_version;
	private TextView       name_and_ip;

	private void initView() {
		bind_et = (DropEditText) findViewById(R.id.bind_et);
		phone_et = (DropEditText) findViewById(R.id.phone_et);
		pwd_et = (DropEditText) findViewById(R.id.pwd_et);

		store_name_tv = (TextView) findViewById(R.id.store_name_tv);
		bind_layout = (RelativeLayout) findViewById(R.id.bind_layout);
		login_layout = (RelativeLayout) findViewById(R.id.login_layout);
		version_name_tv = (TextView) findViewById(R.id.version_name_tv);
		shengyushijian_tv = (TextView) findViewById(R.id.shengyushijian_tv);
		name_version = (TextView) findViewById(R.id.name_version);
		name_and_ip = (TextView) findViewById(R.id.name_and_ip);
		findViewById(R.id.forget_pwd_btn).setOnClickListener(this);
		init();
		//		new NewPos().getInstance(this).getHostIp();
	}

	private void terminalLogin() {
		NewPos.getInstance(this).terminalLogin(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(String response, int id) {
				LogUtils.e(response);
				PrintFile
						.printResult2("Class_BindActivity_Method_terminalLogin_resposne>>\n" + response + "\n");
				try {
					if (GsonUtils.getIntData("result", response) != 0) {
						SharedPreferencesUtil.cleanLoginInfo(mActivity);
						ToastUtils.showToast(mActivity, GsonUtils
								.getData("errmsg", response));
						bind_layout.setVisibility(View.VISIBLE);
						login_layout.setVisibility(View.GONE);
						return;
					}
					NewPos.token = GsonUtils.getData("token", response);
					refreshUI();
					//如果之前you登录过的，那么再登录一次？？
					//3秒后无操作 先登录，然后进入点餐界面
					bind_layout.setVisibility(View.GONE);
					login_layout.setVisibility(View.VISIBLE);
					int version     = GsonUtils.getIntData("version", response);
					int versionCode = AppUtils.getAppVersionCode();
					if (versionCode < version) {
						//有新版本
						downLoadPath = GsonUtils.getData("downloadpath", response);
						SharedPreferencesUtil.saveNewVersionPath(mActivity, downLoadPath);
						String description = GsonUtils.getData("description", response);
						SharedPreferencesUtil.saveNewVersionDescription(mActivity, description);
						//提示版本更新
						showNewVersionDialog(description);
					} else {
						SharedPreferencesUtil.saveNewVersionPath(mActivity, "");
						SharedPreferencesUtil.saveNewVersionDescription(mActivity, "");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void showNewVersionDialog(String description) {
		NewVersionDialog dialog = NewVersionDialog.newInstance(description);
		dialog.show(getSupportFragmentManager(), "NewVersionDialog");

	}

	private String                 downLoadPath;
	private DownLoadProgressDialog mDownLoadProgressDialog;

	private void showDownLoadProgressDialog(int max) {
		mDownLoadProgressDialog = DownLoadProgressDialog.newInstance(max);
		mDownLoadProgressDialog.show(getSupportFragmentManager(), "DownLoadProgressDialog");
	}

	/**
	 * */
	public void downFile(final String urlStr, final String path,
	                     final String fileName) {
		showDownLoadProgressDialog(100);
		OkHttpUtils.get().url(urlStr).build()
				.execute(new FileCallBack(path, fileName) {
					@Override
					public void inProgress(float progress, long total, int id) {
						mDownLoadProgressDialog.setProgress((int) (progress * 100));
					}

					@Override
					public void onResponse(File response, int id) {
						mDownLoadProgressDialog.dismiss();
						DownLoadAPPUtils.getInstance(mActivity).install(response);
					}

					@Override
					public void onError(Call call, Exception e, int id) {
						PrintFile
								.printResult2("Class_SetActivity_Method_downFile()_onError()异常,异常原因:" + e
										.getMessage());
						mDownLoadProgressDialog.dismiss();
						e.printStackTrace();
					}
				});

	}

	private void refreshUI() {
		try {
			int betweenday = TimeUtil
					.daysBetween(new Date(), new Date(Common.SHOP_INFO.storeEndTime));
			if (betweenday <= 15) {
				shengyushijian_tv.setVisibility(View.VISIBLE);
				String html = "<font color=\"red\">" + "*" + "</font> 提示:门店授权使用有效期还剩" + "<font color=\"red\">" + betweenday + "</font>" + "天,请及时联系" + "<br/>" + "系统服务商处理，以免影响门店正常运营";
				shengyushijian_tv.setText(Html.fromHtml(html));
			} else {
				shengyushijian_tv.setVisibility(View.INVISIBLE);
			}

			store_name_tv.setText("[" + Common.SHOP_INFO.sname + "]");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private void init() {
		version_name_tv.setText(AppUtils.getAppName() + " V" + AppUtils.getAppVersionName());
		String s = "<font color=\"#3aa8d9\">" + AppUtils
				.getAppName() + "</font><font color=\"#ffffff\">" + " V" + AppUtils
				.getAppVersionName() + "</font>";
		name_version.setText(Html.fromHtml(s));
		name_and_ip.setText(Common.SHOP_INFO.tname + " | " + NetworkUtils.getIPAddress(true));
	}


	private class PhoneAdapter<String> extends MyAdapter {
		public PhoneAdapter(List<String> dataList) {
			super(dataList);
		}

		@Override
		protected View getItemView(int position, View convertView, ViewGroup parent) {
			TextView tv = new TextView(mActivity);
			tv.setText(dataList.get(position) + "");
			return tv;
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bind_btn:
				bind(bind_et.getText().toString().trim());
				break;
			case R.id.bind_bg:
				if (mTipPop != null && mTipPop.isShowing()) {
					mTipPop.dismiss();
				}
				break;
			case R.id.login_btn:
				userLogin(phone_et.getText().toString().trim(), pwd_et.getText().toString().trim());
				break;
			case R.id.exit_system_btn:
				BaseApplication.getInstance().exit();
				break;
			case R.id.where_tofind_tv:
				showTipsPop(v);

				break;
			case R.id.forget_pwd_btn:
				//				showTipsPop(v);
				showFogetPwdDialog();
				break;
			case R.id.bind_layout2:
			case R.id.bind_layout3:
				if (mTipPop != null) {
					mTipPop.dismiss();
				}
				//                showLoginDialog();
				//每点击一次 实现左移一格数据
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				//给数组的最后赋当前时钟值
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				//当0出的值大于当前时间-500时  证明在500秒内点击了2次
				if (mHits[0] > SystemClock.uptimeMillis() - 500) {
					EnvironmentDialog dialog = EnvironmentDialog.newInstance();
					dialog.show(getSupportFragmentManager(), "EnvironmentDialog");
				}
				break;
		}
	}

	private long[] mHits = new long[3];

	private void showFogetPwdDialog() {
		FogetPwdDialog dialog = new FogetPwdDialog();
		dialog.show(getSupportFragmentManager(), "FogetPwdDialog");
	}

	@Subscribe
	public void downLoadNewVersion(OnFinishEvent event) {
		if ("newversion".equals(event.getMsg())) {
			if (TextUtils.isEmpty(downLoadPath)) {
				showToast("下载出现异常!");
				return;
			}
			downFile(downLoadPath, Contants.SDPATH, "diancan.apk");
		} else if ("changEnvironmentOk".equals(event.getMsg())) {
			SharedPreferencesUtil.cleanLoginInfo(mActivity);
			bind_layout.setVisibility(View.VISIBLE);
			login_layout.setVisibility(View.GONE);
		}
	}

	private PopupWindow mTipPop;

	private void showTipsPop(View view) {
		View contentView = LayoutInflater.from(mActivity)
				.inflate(R.layout.poplayout_tip, null);
		TextView tip1 = (TextView) contentView.findViewById(R.id.where_tofind_terminal_code_tip1);
		String url = NewPos.BASE_URL
				.equals("sz.canxingjian.com/") ? "sz.canxingjian.com" : "www.smarant.com";
		String s = "<font color=\"red\">*" + "</font>在管理后台(<font color=\"#3aa8d9\">" + url + "</font>)-门店运";
		tip1.setText(Html.fromHtml(s));
		mTipPop = new PopupWindow(contentView);
		mTipPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		mTipPop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		mTipPop.setOutsideTouchable(true);
		mTipPop.showAsDropDown(view, -2, 10);
	}

	private void userLogin(final String phone, final String pwd) {

		if (!NetworkUtils.isConnected() && !NetworkUtils.isAvailableByPing()) {
			showToast("未连接网络,请检查网络！");
			return;
		}


		if (TextUtils.isEmpty(phone)) {
			showToast("请输入手机号!");
			return;
		}

		if (TextUtils.isEmpty(pwd)) {
			showToast("请输入密码!");
			return;
		}
		String phone2 = phone;
		if (phone.contains("(")) {
			int i = phone.indexOf('(');
			phone2 = phone.substring(0, i);
		}
		final String finalPhone = phone2;

		showLoadingDialog();
		new NewPos().userLogin(finalPhone, pwd, new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
				dismissLoadingDialog();
				showErrorDialog(Common.errMsg, false);
			}

			@Override
			public void onResponse(String response, int id) {
				PrintFile
						.printResult2("Class_BindActivity_Method_userLogin_resposne>>\n" + response + "\n");
				dismissLoadingDialog();
				try {
					if (0 != GsonUtils.getIntData("result", response)) {
						showToast(GsonUtils.getData("errmsg", response));
						return;
					}
					String content = GsonUtils
							.getData("content", response);
					JSONObject    object1 = new JSONObject(content);
					JSONArray     i       = object1.getJSONArray("authorityIDs");
					List<Integer> idList  = new ArrayList<Integer>();
					for (int j = 0; j < i.length(); j++) {
						idList.add((int) i.get(j));
					}
					if (idList != null) {
						SharedPreferencesUtil.setRefundAthuority(mActivity, idList
								.contains(PowerController.REFUND_DISH));
						SharedPreferencesUtil.setKaiqianxiangAthority(mActivity, idList
								.contains(PowerController.OPEN_POS_BOX));
					}
					JSONObject object   = new JSONObject(response);
					JSONObject content2 = object.getJSONObject("content");
					String     realname = content2.getString("realname");
					List<LoginUserRecord> records = DataSupport.where("phone=?", finalPhone)
							.find(LoginUserRecord.class);
					if (records == null || records.size() == 0) {
						LoginUserRecord record = new LoginUserRecord();
						record.setCreateAt(new Date().getTime());
						record.setPhone(finalPhone);
						record.setRealname(realname);
						record.setPwd(pwd);
						record.save();
					} else {
						ContentValues values = new ContentValues();
						values.put("createat", new Date().getTime());
						DataSupport.update(LoginUserRecord.class, values, records.get(0).getId());
					}
					Common.SHOP_INFO.realname = realname;
					Common.SHOP_INFO.userphone = finalPhone;
					Common.SHOP_INFO.userpwd = pwd;
					getData();
					startMain();
				} catch (Exception e) {
					dismissLoadingDialog();
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 获取菜品的信息
	 */
	private void getData() {
		NewPos.getInstance(mActivity).getKind(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(String response, int id) {
				Log.e(TAG, "response>>" + response);
				try {
					if (GsonUtils.getIntData("result", response) == 0) {
						DishKindRes bean = GsonUtils.getSingleBean(response, DishKindRes.class);
						if (Common.dishKindList != null) {
							Common.dishKindList.clear();
							Common.dishKindList.addAll(bean.getDishKindData());
						}
						getDishList();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void getDishList() {
		NewPos.getInstance(mActivity).getDish(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(String response, int id) {
				Log.e(TAG, "response>");
				try {
					if (GsonUtils.getIntData("result", response) == 0) {
						try {
							MenuData bean = GsonUtils
									.getSingleBean(response, MenuData.class);
							if (bean != null) {
								List<MenuData.MenuDataBean> data = bean.getMenuData();
								if (data != null && data.size() > 0) {
									MenuData.MenuDataBean bean1 = data.get(0);
									if (bean1 != null) {
										List<DishModel> data1 = bean1
												.getDishData();
										if (data1 != null && data1.size() > 0) {
											Common.dishModel = data1.get(0);
										}
									}
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Log.e(TAG, "response>>" + response);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	//	class MyCallBack implements NewPos.Callback<BaseModelSz> {
	//
	//		@Override
	//		public void onError(Call call, Exception e, int id) {
	//			dialog = new DialogUtils().show(BindActivity.this, Common.errMsg);
	//		}
	//
	//		@Override
	//		public void onResponse(BaseModelSz response, int id) {
	//			if (response.result == 0) {
	//				startMain();
	//			} else {
	//				dialog = new DialogUtils().show(BindActivity.this, response.errmsg);
	//			}
	//		}
	//
	//	}

	private void startMain() {
		startActivity(new Intent(this, EasyMainActivity.class));
		BaseApplication.getInstance().finishActivity(this);
	}


	private void bind(final String code) {
		if (TextUtils.isEmpty(code)) {
			showToast("终端码不能为空!");
			return;
		}
		showLoadingDialog();
		new NewPos().bind(code, new NewPos.Callback<StoreInfoResp>() {
			@Override
			public void onError(Call call, Exception e, int id) {
				dismissLoadingDialog();
				showErrorDialog(Common.errMsg, false);
				e.printStackTrace();
			}

			@Override
			public void onResponse(StoreInfoResp response, int id) {
				PrintFile
						.printResult2("Class_BindActivity_Method_bind_resposne>>\n" + response + "\n");
				dismissLoadingDialog();
				if (response.result == 0) {
					SharedPreferencesUtil.saveShopInfo(mActivity, response.content);
					refreshUI();
					Toast.makeText(mActivity, "绑定成功", Toast.LENGTH_SHORT).show();
					//跳转到下个界面了
					changeToLoginView();
				} else {
					showErrorDialog(response.errmsg, false);
				}

			}
		});
	}


	private void changeToLoginView() {
		bind_layout.setVisibility(View.GONE);
		login_layout.setVisibility(View.VISIBLE);
	}

}
