package com.acewill.ordermachine.fragmentdialog;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.dialog.BaseDialog;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.ordermachine.widget.DropEditText;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;

import okhttp3.Call;

/**
 * Author：Anch
 * Date：2017/12/13 20:10
 * Desc：
 */
public class FogetPwdDialog extends BaseDialog implements View.OnClickListener {
	private static final String TAG = "FogetPwdDialog";
	DropEditText user_et;
	DropEditText pwd_et;

	@Override
	public View getView() {
		View view = View.inflate(mcontext, R.layout.dialog_forgetpwd, null);
		initView(view);

		return view;
	}

	private TextView btn_send_msg;

	private void initView(View view) {
		user_et = (DropEditText) view.findViewById(R.id.common_user_et);
		pwd_et = (DropEditText) view.findViewById(R.id.common_pwd_et);
		btn_send_msg = (TextView) view.findViewById(R.id.btn_send_msg);
		btn_send_msg.setOnClickListener(this);
		view.findViewById(R.id.dialog_btn).setOnClickListener(this);
		view.findViewById(R.id.close_img).setOnClickListener(this);
	}

	@Override
	public float getSize() {
		return 0.35f;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_send_msg:

				doSendMsg();
				break;
			case R.id.dialog_btn:
				doChangPwd();
				break;
			case R.id.close_img:
				dismiss();
				break;
		}
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mHandler.sendEmptyMessage(0);
			mHandler.postDelayed(mRunnable, 1000);
		}
	};

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {

			if (time > 0) {
				time--;
				btn_send_msg.setText("倒计时" + time);
			} else {
				mHandler.removeCallbacks(mRunnable);
				time = 60;
				btn_send_msg.setText("发送验证码");
				btn_send_msg.setEnabled(true);
			}
			return false;
		}
	});
	private int     time     = 60;

	private void doSendMsg() {
		String phone = user_et.getText().toString().trim();
		if (TextUtils.isEmpty(phone)) {
			ToastUtils.showToast(mcontext, "请填写手机号或邮箱!");
			return;
		}

		btn_send_msg.setEnabled(false);
		mHandler.postDelayed(mRunnable, 0);
		NewPos.getInstance(mcontext).sendValidateMsg(phone, new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(String response, int id) {
				Log.e("ds", "response>>" + response);
				//置灰，然后倒计时

			}
		});
	}

	private void doChangPwd() {
		String user = user_et.getText().toString().trim();
		String pwd  = pwd_et.getText().toString().trim();
		if (TextUtils.isEmpty(user)) {
			ToastUtils.showToast(mcontext, "请填写手机号或邮箱!");
			return;
		}
		if (TextUtils.isEmpty(pwd)) {
			ToastUtils.showToast(mcontext, "请输入验证码!");
			return;
		}
		//
		NewPos.getInstance(mcontext).forgetPwd(user, pwd, new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(String response, int id) {
				try {
					if (0 == GsonUtils.getIntData("result", response)) {
						dismiss();
					} else {
						ToastUtils.showToast(mcontext, GsonUtils.getErrMsg(response));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
