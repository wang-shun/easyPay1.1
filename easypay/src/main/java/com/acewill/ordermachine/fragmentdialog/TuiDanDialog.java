package com.acewill.ordermachine.fragmentdialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.adapter.ListViewAdapter;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.dialog.BaseDialog;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.model.OrderReq;
import com.acewill.ordermachine.model.RefundReasonBean;
import com.acewill.ordermachine.model.RefundReasonReq;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.util.SharedPreferencesUtil;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * Author：Anch
 * Date：2017/11/20 17:14
 * Desc：
 */
public class TuiDanDialog extends BaseDialog implements
		View.OnClickListener {
	private static final String TAG = "TuiDanDialog";

	/**
	 * @param curReq
	 * @return
	 */
	public static TuiDanDialog newInstance(OrderReq curReq) {
		TuiDanDialog fragment = new TuiDanDialog();
		Bundle       bundle   = new Bundle();
		bundle.putSerializable("req", curReq);
		//		bundle.putString("oid", oid);
		//		bundle.putString("cost", cost);
		//		bundle.putString("payMethodName", payMethodName);
		//		bundle.putInt("myid", myid);
		fragment.setArguments(bundle);
		return fragment;
	}

	//	@Nullable
	//	@Override
	//	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	//		mcontext = inflater.getContext();
	//		Dialog dialog = getDialog();
	//		View   view   = inflater.inflate(R.layout.fragment_tuikuan_dialog, null);
	//		if (dialog != null) {
	//			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
	//			//若无该段代码则用系统的样式而不是自己定义的背景样式
	//			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	//		}
	//		initView(view);
	//		initData();
	//		return view;
	//	}

	@Override
	public View getView() {
		Dialog dialog = getDialog();
		View   view   = View.inflate(mcontext, R.layout.fragment_tuikuan_dialog, null);
		if (dialog != null) {
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
			//若无该段代码则用系统的样式而不是自己定义的背景样式
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
		initView(view);
		initData();
		return view;
	}

	private List<RefundReasonBean> list = new ArrayList<>();

	private void initData() {

		NewPos.getInstance(mcontext).getRefundReason(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
				ToastUtils.showToast(mcontext, Common.errMsg);
			}

			@Override
			public void onResponse(String response, int id) {
				Log.e(TAG, "response>" + response);
				PrintFile
						.printResult2("Class_TuiDanDialog_method_initData_onResponse_" + response + "\n");
				try {
					if (GsonUtils.getIntData("result", response) == 0) {
						RefundReasonReq req = GsonUtils
								.getSingleBean(response, RefundReasonReq.class);
						list = req.getContent();
						if (adapter == null)
							adapter = new ListViewAdapter(mcontext, list);
						listView.setAdapter(adapter);
					} else {
						ToastUtils.showToast(mcontext, GsonUtils.getErrMsg(response));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	private ListViewAdapter adapter;
	private TextView        item_dingdanhao_tv;
	private TextView        item_shishoujine_tv;
	private TextView        item_zhifufangshi_tv;
	private TextView        tuidan_reason_tv;
	private RadioGroup      item_radio_group;
	private RadioButton     item_radio_btn1;
	private String          oid;
	private String          cost;
	private String          payMethodName;
	private int             myid;
	private ListView        listView;

	private void initView(View view) {
		listView = (ListView) view.findViewById(R.id.listview);
		item_dingdanhao_tv = (TextView) view.findViewById(R.id.item_dingdanhao_tv);
		item_shishoujine_tv = (TextView) view.findViewById(R.id.item_shishoujine_tv);
		item_zhifufangshi_tv = (TextView) view.findViewById(R.id.item_zhifufangshi_tv);
		tuidan_reason_tv = (TextView) view.findViewById(R.id.tuidan_reason_tv);
		String tuidanReasonStr = "<font color=\"red\">*</font>退单原因";
		tuidan_reason_tv.setText(Html.fromHtml(tuidanReasonStr));
		item_radio_group = (RadioGroup) view.findViewById(R.id.item_radio_group);

		item_radio_btn1 = (RadioButton) view.findViewById(R.id.item_radio_btn1);
		view.findViewById(R.id.dialog_btn).setOnClickListener(this);
		view.findViewById(R.id.close_img).setOnClickListener(this);
		item_radio_btn1.setChecked(true);
		//		oid = getArguments().getString("oid");
		//		cost = getArguments().getString("cost");
		//		payMethodName = getArguments().getString("payMethodName");
		//		myid = getArguments().getInt("myid");
		OrderReq req = (OrderReq) getArguments().getSerializable("req");
		oid = req.getOid() + "";
		cost = req.getCost();
		payMethodName = req.getPaymentTypeName();
		myid = req.getMyid();
		item_dingdanhao_tv.setText(this.oid + "");
		item_shishoujine_tv.setText(cost);
		item_zhifufangshi_tv.setText(payMethodName);
	}

	private int reasonid;


	//	/**
	//	 * 该段代码设置弹框大小
	//	 */
	//	@Override
	//	public void onStart() {
	//		super.onStart();
	//		Dialog dialog = getDialog();
	//		if (dialog != null) {
	//			DisplayMetrics dm = new DisplayMetrics();
	//			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
	//			dialog.getWindow()
	//					.setLayout((int) (dm.widthPixels * 0.3), ViewGroup.LayoutParams.WRAP_CONTENT);
	//		}
	//	}

	@Override
	public float getSize() {
		return 0.3f;
	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.close_img:
				dismiss();
				break;
			case R.id.dialog_btn:
				doRefund();
				break;
		}

	}

	private void doRefund() {
		if (adapter != null) {
			reasonid = adapter.getSelect();
		}
		final boolean returnFund = item_radio_group
				.getCheckedRadioButtonId() == R.id.item_radio_btn1 ? true : false;
		//微信支付宝的退款
		new NewPos().getInstance(mcontext)
				.refund(oid, reasonid + "", Common.SHOP_INFO.phone, Common.SHOP_INFO.realname, SharedPreferencesUtil
						.getTempAhroityName(mcontext), returnFund, new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						e.printStackTrace();
						PrintFile.printResult2("Class_TuiDanDialog_Method_onError>" + e
								.getMessage() + "\n");
					}

					@Override
					public void onResponse(String response, int id) {
						PrintFile
								.printResult2("Class_TuiDanDialog_method_onResponse>" + response + "\n");
						try {
							int result = GsonUtils.getIntData("result", response);
							if (result != 0) {
								ToastUtils
										.showToast(mcontext, GsonUtils.getData("errmsg", response));
							} else {
								ContentValues values = new ContentValues();
								values.put("refundmoney", returnFund ? cost : String
										.valueOf(0));//设置退款的金额额
								values.put("refundtime", new Date().getTime());//设置退款时间
								values.put("paymentstatus", "REFUND");//设置成已经退款
								values.put("isrefundmoney", returnFund ? 0 : 1);//是否已经退了钱,默认退款 ，退款为0 ，不退款为1
								int update = DataSupport.update(OrderReq.class, values, myid);
								if (update == 1) {
									ToastUtils.showToast(mcontext, "退款成功");
									OnFinishEvent event = new OnFinishEvent();
									event.setMsg("refundSuccess");
									event.setContent(returnFund + "");
									EventBus.getDefault().post(event);
								} else {
									ToastUtils
											.showToast(mcontext, "退款失败");//并且要做记录 // TODO: 2017/12/1 anch
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
		dismiss();
	}
}
