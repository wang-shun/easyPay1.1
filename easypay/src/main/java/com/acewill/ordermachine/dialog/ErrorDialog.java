package com.acewill.ordermachine.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.acewill.ordermachine.BaseApplication;
import com.acewill.ordermachine.OnCancleClickListener;
import com.acewill.ordermachine.R;
import com.acewill.ordermachine.util.SharedPreferencesUtil;

/**
 * Author：Anch
 * Date：2017/9/8 17:40
 * Desc：
 */
public class ErrorDialog extends BaseDialog {
	/**
	 * @return
	 */
	public static ErrorDialog newInstance(String msg, boolean show) {
		ErrorDialog fragment = new ErrorDialog();
		Bundle      bundle   = new Bundle();
		bundle.putString("msg", msg);
		bundle.putBoolean("show", show);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View getView() {
		View              view   = View.inflate(mcontext, R.layout.dialog_message, null);
		TextView          tv_msg = (TextView) view.findViewById(R.id.tv_msg);
		final RadioButton rBtn   = (RadioButton) view.findViewById(R.id.no_tips_again);
		String            msg    = getArguments().getString("msg");
		boolean           show   = getArguments().getBoolean("show");
		rBtn.setVisibility(show ? View.VISIBLE : View.GONE);
		tv_msg.setText(msg);
		View btn_ok = view.findViewById(R.id.btn_ok);
		btn_ok.setVisibility(View.VISIBLE);
		btn_ok.setEnabled(true);
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferencesUtil.saveCheckPrintBeforeNewOrder(BaseApplication.getInstance()
						.getBaseContext(), !rBtn.isChecked());
				if (mCancleClickListener != null) {
					mCancleClickListener.onCancle();
				}
				dismiss();
			}
		});
		setCancelable(false);
		return view;
	}


	@Override
	public float getSize() {
		return 0.5f;
	}

	private OnCancleClickListener mCancleClickListener;

	public void setonCancleClickListener(OnCancleClickListener listener) {
		mCancleClickListener = listener;
	}
}