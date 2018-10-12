/**
 * ClassName: MyProcessDialog.java
 * created on 2012-3-17
 * Copyrights 2011-2012 qjyong All rights reserved.
 * site: http://blog.csdn.net/qjyong
 * email: qjyong@gmail.com
 */
package com.acewill.ordermachine.dialog;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acewill.ordermachine.BaseApplication;
import com.acewill.ordermachine.OnCancleClickListener;
import com.acewill.ordermachine.R;


/**
 * 对话框
 */
public class LoadingDialog extends BaseDialog implements View.OnClickListener {

	private TextView tv_load;

	/**
	 * @return
	 */
	public static LoadingDialog newInstance() {
		LoadingDialog fragment = new LoadingDialog();
		return fragment;
	}

	public void setMsg(String msg) {
		if (null != this.tv_load) {
			tv_load.setVisibility(View.VISIBLE);
			this.tv_load.setText(msg);
		}
	}

	@Override
	public void onClick(View v) {
		if (listener != null) {
			listener.onCancle();
		}
	}

	private OnCancleClickListener listener;

	public void setOnCancleClickListener(OnCancleClickListener listener) {
		this.listener = listener;
	}

	@Override
	public View getView() {
		View view = View.inflate(mcontext, R.layout.progress_load, null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		ImageView img_load = (ImageView) view.findViewById(R.id.img_load);
		tv_load = (TextView) view.findViewById(R.id.tv_load);
		tv_load.setVisibility(View.GONE);
		AnimationDrawable drawable = (AnimationDrawable) img_load.getBackground();
		drawable.start();
		setCancelable(false);
	}

	@Override
	public float getSize() {
		return 0.3f;
	}
}
