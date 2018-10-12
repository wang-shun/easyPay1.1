package com.acewill.ordermachine.fragmentdialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.acewill.chikenprintlibrary.model.PowerController;
import com.acewill.ordermachine.R;
import com.acewill.ordermachine.eventbus.OnCheckAthorityEvent;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.ordermachine.widget.DropEditText;
import com.acewill.ordermachine.widget.WindowUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;

/**
 * Author：Anch
 * Date：2017/11/20 17:14
 * Desc：
 */
public class CommonEditDialog extends DialogFragment implements View.OnClickListener,
		View.OnFocusChangeListener {
	private Context      mcontext;
	private DropEditText user_et;
	private DropEditText pwd_et;
	private TextView     dialog_title;
	private TextView     dialog_name;
	private int          type;
	private Button       dialog_btn;

	/**
	 * @param type dialog的类型
	 * @return
	 */
	public static CommonEditDialog newInstance(int type) {
		CommonEditDialog fragment = new CommonEditDialog();
		Bundle           bundle   = new Bundle();
		bundle.putInt("type", type);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mcontext = inflater.getContext();
		Dialog dialog = getDialog();
		View   view   = inflater.inflate(R.layout.fragment_qianxianglogin_dialog, null);
		if (dialog != null) {
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
			//若无该段代码则用系统的样式而不是自己定义的背景样式
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
		user_et = (DropEditText) view.findViewById(R.id.common_user_et);
		pwd_et = (DropEditText) view.findViewById(R.id.common_pwd_et);
		user_et.setOnFocusChangeListener(this);
		pwd_et.setOnFocusChangeListener(this);
		dialog_title = (TextView) view.findViewById(R.id.dialog_title);
		dialog_name = (TextView) view.findViewById(R.id.dialog_name);
		view.findViewById(R.id.close_img).setOnClickListener(this);
		dialog_btn = (Button) view.findViewById(R.id.dialog_btn);
		dialog_btn.setOnClickListener(this);
		type = getArguments().getInt("type");
		switch (type) {
			case PowerController.REFUND_DISH://退款
				dialog_title.setText("抱歉,您没有退单退款权限！需授权:");
				dialog_name.setText("退款退单授权");
				dialog_btn.setText("退款退单授权");
				break;
			case PowerController.OPEN_POS_BOX://开钱箱
				dialog_title.setText("抱歉,您没有非结账时开钱箱权限！需授权:");
				dialog_name.setText("开钱箱授权");
				dialog_btn.setText("开钱箱授权");
				break;
		}

		return view;
	}


	/**
	 * 该段代码设置弹框大小
	 */
	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = getDialog();
		if (dialog != null) {
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			dialog.getWindow()
					.setLayout((int) (dm.widthPixels * 0.3), ViewGroup.LayoutParams.WRAP_CONTENT);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dialog_btn:
				switch (type) {
					case PowerController.REFUND_DISH:
						//是否有退款权限
						doCheck(PowerController.REFUND_DISH);
						break;
					case PowerController.OPEN_POS_BOX:
						doCheck(PowerController.OPEN_POS_BOX);
						break;
				}
				break;
			case R.id.close_img:
				dismiss();
				break;

		}
	}

	private void doCheck(final int type) {
		if (TextUtils.isEmpty(user_et.getText().toString())) {
			ToastUtils.showToast(mcontext, "输入用户名不能为空");
			return;
		}
		if (TextUtils.isEmpty(pwd_et.getText().toString())) {
			ToastUtils.showToast(mcontext, "输入密码不能为空");
			return;
		}
		new NewPos().getInstance(mcontext)
				.checkAthourity(user_et.getText().toString(), pwd_et.getText()
						.toString(), type + "", new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						e.printStackTrace();
					}

					@Override
					public void onResponse(String response, int id) {
						Log.e("commoneditdialog", "response>>" + response);

						try {
							if (GsonUtils.getIntData("result", response) != 0) {
								ToastUtils
										.showToast(mcontext, GsonUtils.getData("errmsg", response));
							} else {
								//有权权限 ，记录这个授权人的名字  然后就退款
								//								SharedPreferencesUtil
								//										.saveTempAhroityName(mcontext, user_et.getText()
								//												.toString());
								//																OnFinishEvent event = new OnFinishEvent();
								//								event.setMsg("checkAthourityOk");
								//								EventBus.getDefault().post(event);
								dismiss();
								OnCheckAthorityEvent event = new OnCheckAthorityEvent(type, true);
								EventBus.getDefault().post(event);
							}
						} catch (Exception e) {

						}
					}
				});
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		WindowUtil.hiddenKey();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

		if (!hasFocus) {
			WindowUtil.hiddenKey();
		}
	}
}
