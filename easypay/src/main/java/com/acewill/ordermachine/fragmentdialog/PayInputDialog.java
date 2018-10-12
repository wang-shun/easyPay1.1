package com.acewill.ordermachine.fragmentdialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.dialog.BaseDialog;
import com.acewill.ordermachine.eventbus.OnEditTextFinishEvent;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.ordermachine.widget.CommonEditText;
import com.acewill.ordermachine.widget.WindowUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Author：Anch
 * Date：2017/11/21 11:37
 * Desc：
 */
public class PayInputDialog extends BaseDialog implements View.OnClickListener {
	/**
	 * @param type 支付成功或失败
	 * @return
	 */
	public static PayInputDialog newInstance(int type) {
		PayInputDialog fragment = new PayInputDialog();
		Bundle         bundle   = new Bundle();
		bundle.putInt("type", type);
		fragment.setArguments(bundle);
		return fragment;
	}

	private CommonEditText mEditText;
	private int            type;


	@Override
	public View getView() {
		Dialog dialog = getDialog();
		View   view   = View.inflate(mcontext, R.layout.fragment_payinput_dialog, null);
		if (dialog != null) {
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
			//若无该段代码则用系统的样式而不是自己定义的背景样式
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
		type = getArguments().getInt("type");
		mEditText = (CommonEditText) view
				.findViewById(R.id.ligangsuoding_pwd_e2t);

		view.findViewById(R.id.close_img).setOnClickListener(this);
		TextView tv = (TextView) view.findViewById(R.id.dialog_title);

		switch (type) {
			case 3://会员
				tv.setText(mcontext.getResources().getString(R.string.huiyuanzhifu));
				mEditText.setHint(mcontext.getResources().getString(R.string.qingsaohuiyuan));
				break;
			case 102://移动支付

				break;
		}
		mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				//当actionId == XX_SEND 或者 XX_DONE时都触发
				//或者event.getKeyCode == ENTER 且 event.getAction == ACTION_DOWN时也触发
				//注意，这是一定要判断event != null。因为在某些输入法上会返回null。
				if (actionId == EditorInfo.IME_ACTION_SEND
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| (event != null && KeyEvent.KEYCODE_ENTER == event
						.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
					//处理事件
					String code = mEditText.getText().toString().trim();
					if (TextUtils.isEmpty(code)) {
						ToastUtils.showToast(mcontext, "支付码不能为空!");
						return false;
					}
					EventBus.getDefault()
							.post(new OnEditTextFinishEvent(type, code));
				}
				return false;
			}
		});
		view.findViewById(R.id.dialog_btn).setOnClickListener(this);
		WindowUtil.hiddenKey();
		WindowUtil.hiddenSysKey(mEditText);
		WindowUtil.showKey(mcontext, mEditText);
		return view;
	}



	@Override
	public float getSize() {
		return 0.5f;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.close_img:
				dismiss();
				break;
			case R.id.dialog_btn:
				String code = mEditText.getText().toString().trim();
				if (TextUtils.isEmpty(code)) {
					ToastUtils.showToast(mcontext, "支付码不能为空!");
					return;
				}
				EventBus.getDefault()
						.post(new OnEditTextFinishEvent(type, code));
				break;
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		if (mOnDialogDismissListener != null)
			mOnDialogDismissListener.onDialogDismiss();
	}

	public interface OnDialogDismissListener {
		void onDialogDismiss();
	}

	public OnDialogDismissListener mOnDialogDismissListener;

	public void setOnDialogDismiss(OnDialogDismissListener listener) {
		mOnDialogDismissListener = listener;
	}

}
