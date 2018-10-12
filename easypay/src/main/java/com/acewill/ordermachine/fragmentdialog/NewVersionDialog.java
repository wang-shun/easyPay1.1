package com.acewill.ordermachine.fragmentdialog;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.dialog.BaseDialog;
import com.acewill.ordermachine.eventbus.OnFinishEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Author：Anch
 * Date：2017/12/14 14:06
 * Desc：
 */
public class NewVersionDialog extends BaseDialog implements View.OnClickListener {


	/**
	 * @param des dialog的类型
	 * @return
	 */
	public static NewVersionDialog newInstance(String des) {
		NewVersionDialog fragment = new NewVersionDialog();
		Bundle           bundle   = new Bundle();
		bundle.putString("des", des);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View getView() {
		View view = View.inflate(mcontext, R.layout.dialog_newviewsion, null);
		initView(view);
		return view;
	}
	private boolean isDebug = false;
	private void initView(View view) {
		String   des          = (String) getArguments().get("des");
		String   title        = (String) getArguments().get("title");
		TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
		view.findViewById(R.id.close_img).setOnClickListener(this);
		view.findViewById(R.id.dialog_btn).setOnClickListener(this);
		TextView newversion_content = (TextView) view.findViewById(R.id.newversion_content);
		dialog_title.setText("版本更新");
		newversion_content.setText(des);
		if (isDebug) {
			setCancelable(false);
			view.findViewById(R.id.close_img).setVisibility(View.GONE);
		}
	}

	@Override
	public float getSize() {
		return 0.35f;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.close_img:
				dismiss();
				break;
			case R.id.dialog_btn:
				dismiss();
				OnFinishEvent event = new OnFinishEvent();
				event.setMsg("newversion");
				EventBus.getDefault().post(event);
				break;
		}
	}
}
