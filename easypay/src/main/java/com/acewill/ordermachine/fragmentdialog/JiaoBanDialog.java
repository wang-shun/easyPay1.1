package com.acewill.ordermachine.fragmentdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.acewill.chikenprintlibrary.model.WorkShiftNewReport;
import com.acewill.ordermachine.OnCancleClickListener;
import com.acewill.ordermachine.R;
import com.acewill.ordermachine.activity.EasyMainActivity;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.dialog.BaseDialog;
import com.acewill.ordermachine.dialog.ErrorDialog;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.model.WorkShift;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.printadapter.PosKitchenPrintAdapter;
import com.acewill.ordermachine.util.PriceUtil;
import com.acewill.ordermachine.util.SharedPreferencesUtil;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.ordermachine.util_new.TimeUtil;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.ordermachine.widget.DropEditText;
import com.acewill.ordermachine.widget.WindowUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;

/**
 * Author：Anch
 * Date：2017/11/21 11:37
 * Desc：
 */
public class JiaoBanDialog extends BaseDialog implements View.OnClickListener,
		OnCancleClickListener {
	private static final String TAG = "JiaoBanDialog";
	private EasyMainActivity mcontext;
	private DropEditText     qianxiangjine_et;
	private WorkShift        workShift;

	/**
	 * @return
	 */
	public static JiaoBanDialog newInstance(int type) {
		JiaoBanDialog fragment = new JiaoBanDialog();
		Bundle        bundle   = new Bundle();
		bundle.putInt("type", type);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mcontext = (EasyMainActivity) context;
	}


	@Override
	public View getView() {
		Dialog dialog = getDialog();
		View   view   = View.inflate(mcontext, R.layout.fragment_jiaoban_dialog, null);

		if (dialog != null) {
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
			//若无该段代码则用系统的样式而不是自己定义的背景样式
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		}

		workShift = Common.workShifit;
		TextView jiaobanren_name_tv    = (TextView) view.findViewById(R.id.jiaobanren_name_tv);
		TextView jiaobanren_banci_tv   = (TextView) view.findViewById(R.id.jiaobanren_banci_tv);
		TextView jiaobanren_shijian_tv = (TextView) view.findViewById(R.id.jiaobanren_shijian_tv);
		//		TextView tips_one              = (TextView) view.findViewById(R.id.tips_one);
		//		TextView tips_two              = (TextView) view.findViewById(R.id.tips_two);
		//		String   tips_oneStr           = "<font color=\"red\">*</font>提示";
		//		String   tips_twoStr           = "<font color=\"red\">日结</font>将对今日内\"已交班\"的订单进行日结操作，日结后可继续点单收银，做了日结的订单将归纳日\"历史订单\"中，能查询统计，不能退单及退款";
		//		tips_one.setText(Html.fromHtml(tips_oneStr));
		//		tips_two.setText(Html.fromHtml(tips_twoStr));
		TextView jiaobanren_shichang_tv = (TextView) view
				.findViewById(R.id.jiaobanren_shichang_tv);
		String jiaobanren_name_str = "您好 <font color=\"#3aa8d9\">" + Common.SHOP_INFO.realname + "(" + Common.SHOP_INFO.userphone + ")" + "</font>，您的";
		String jiaobanren_banci_str = "当前班次:<font color=\"#3aa8d9\">" + workShift
				.getDefinitionName() + "</font>";
		long hour   = TimeUtil.getDateBetweenHour(new Date(), new Date(workShift.getStartTime()));
		long minute = TimeUtil.getDateBetweenMinute(new Date(), new Date(workShift.getStartTime()));
		String jiaobanren_shijian_str = "开班时间:<font color=\"#3aa8d9\">" + workShift
				.getStartTimeStr() + "</font>";
		String jiaobanren_shichang_str = "当班时长:<font color=\"#3aa8d9\">" + hour + "</font>" + "小时<font color=\"#3aa8d9\">" + minute + "</font>分钟";
		jiaobanren_name_tv.setText(Html.fromHtml(jiaobanren_name_str));
		jiaobanren_banci_tv.setText(Html.fromHtml(jiaobanren_banci_str));
		jiaobanren_shijian_tv.setText(Html.fromHtml(jiaobanren_shijian_str));
		jiaobanren_shichang_tv.setText(Html.fromHtml(jiaobanren_shichang_str));

		qianxiangjine_et = (DropEditText) view.findViewById(R.id.qianxiangjine_et);
		view.findViewById(R.id.dialog_btn).setOnClickListener(this);
		view.findViewById(R.id.close_img).setOnClickListener(this);
		return view;
	}


	@Override
	public float getSize() {
		return 0.3f;
	}

	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.dialog_btn:
				doJiaoBan();
				break;
			case R.id.close_img:
				dismiss();
				break;
		}
	}

	private void doJiaoBanAndRiJie() {
		//先交班
		doJiaoBan();
		//然后打印机交班小票
		//然后获取日结报表
		//然后日结
	}

	private void doJiaoBan() {
		String qianxiangjine = qianxiangjine_et.getText().toString().trim();
		if (TextUtils.isEmpty(qianxiangjine)) {
			ToastUtils.showToast(mcontext, "钱箱金额不能为空");
			return;
		}
		dismiss();
		WindowUtil.hiddenKey();
		showLoadingDialog();
		final Date   date  = new Date();
		final String price = PriceUtil.formatPrice(new BigDecimal(qianxiangjine));
		workShift.setCashRevenue(price);//交班金额
		workShift.setEndTime(date.getTime());
		workShift.setEndTimeStr(format.format(date));
		new NewPos().getInstance(mcontext)
				.endWorkShift(workShift.getId(), new Gson()
						.toJson(workShift), new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						e.printStackTrace();
						PrintFile.printResult2("Class_JiaoBanDialog_method_doJiaoBan_onError_" + e
								.getMessage() + "\n");
						dismissLoadingDialog();
					}

					@Override
					public void onResponse(String response, int id) {
						PrintFile
								.printResult2("Class_JiaoBanDialog_method_doJiaoBan_onResponse_" + response + "\n");
						try {
							//							if (0 == GsonUtils.getIntData("result", response)) {
							//打印交班小票
							//1、获取当前班次和当天报表数据
							//								if (SharedPreferencesUtil
							//										.getJiaoBan(mcontext)) {
							int result = GsonUtils.getIntData("result", response);
							if (result == 0)
								doGetJiaoBanInfo(price);//去打印的
							else {
								OnFinishEvent event = new OnFinishEvent();
								event.setMsg("jiaobanfail");
								event.setContent(GsonUtils.getErrMsg(response));
								EventBus.getDefault().post(event);
								dismissLoadingDialog();
							}
							//								} else {
							//									OnFinishEvent event = new OnFinishEvent();
							//									event.setMsg("jiaobansuccess");
							//									EventBus.getDefault().post(event);
							//									dismissLoadingDialog();
							//								}
							//							} else {
							//								dismissLoadingDialog();
							//								ToastUtils
							//										.showToast(mcontext, GsonUtils.getData("errmsg", response));
							//							}
						} catch (Exception e) {
							dismissLoadingDialog();
							e.printStackTrace();
						}
					}
				});
	}

	private void doGetJiaoBanInfo(String price) {
		new NewPos().getInstance(mcontext).workShiftReport(workShift
				.getId() + "", price, new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
				PrintFile.printResult2("Class_JiaoBanDialog_method_doGetJiaoBanInfo_onError_" + e
						.getMessage() + "\n");
				dismissLoadingDialog();
			}

			@Override
			public void onResponse(String response, int id) {
				dismissLoadingDialog();
				PrintFile
						.printResult2("Class_JiaoBanDialog_method_doGetJiaoBanInfo_onResponse_" + response + "\n");
				try {
					if (GsonUtils.getIntData("result", response) == 0) {
						WorkShiftNewReport result = GsonUtils
								.getSingleBean(GsonUtils
										.getData("content", response), WorkShiftNewReport.class);
						//						jiaobanfail

						if (SharedPreferencesUtil.getJiaoBan(mcontext)) {
							if (Common.isPrintOk) {
								PosKitchenPrintAdapter.getInstance()
										.printWorkShift(result);
								OnFinishEvent event = new OnFinishEvent();
								event.setMsg("jiaobansuccess");
								EventBus.getDefault().post(event);
							} else {
								ErrorDialog dialog = ErrorDialog
										.newInstance("打印机连接异常，请检查打印机!", false);
								dialog.setonCancleClickListener(JiaoBanDialog.this);
								dialog.setCancelable(false);
							}
						} else {
							OnFinishEvent event = new OnFinishEvent();
							event.setMsg("jiaobansuccess");
							EventBus.getDefault().post(event);
						}

					} else {
						OnFinishEvent event = new OnFinishEvent();
						event.setMsg("jiaobanfail");
						event.setContent(GsonUtils.getErrMsg(response));
						EventBus.getDefault().post(event);
					}

				} catch (Exception e) {
					dismissLoadingDialog();
					e.printStackTrace();
				}
			}
		});
	}


	@Override
	public void onCancle() {
		OnFinishEvent event = new OnFinishEvent();
		event.setMsg("jiaobansuccess");
		EventBus.getDefault().post(event);
		SharedPreferencesUtil
				.saveCheckPrintBeforeNewOrder(mcontext, true);
	}
}
