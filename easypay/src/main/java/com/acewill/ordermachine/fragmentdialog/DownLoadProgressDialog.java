package com.acewill.ordermachine.fragmentdialog;

import android.os.Bundle;
import android.view.View;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.dialog.BaseDialog;
import com.acewill.ordermachine.widget.RingProgressBar;

/**
 * Author：Anch
 * Date：2017/12/15 11:40
 * Desc：
 */
public class DownLoadProgressDialog extends BaseDialog {
	private RingProgressBar mRingProgressBar2;

	/**
	 * @param des dialog的类型
	 * @return
	 */
	public static DownLoadProgressDialog newInstance(int max) {
		DownLoadProgressDialog fragment = new DownLoadProgressDialog();
		Bundle                 bundle   = new Bundle();
		bundle.putInt("max", max);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View getView() {
		View view = View.inflate(mcontext, R.layout.dialog_downloadprogress_layout, null);
		initView(view);
		return view;
	}


	private void initView(View view) {
		mRingProgressBar2 = (RingProgressBar) view.findViewById(R.id.progress_bar_2);
		mRingProgressBar2.setMax(getArguments().getInt("max"));

	}

	public void setProgress(int progress) {
		mRingProgressBar2.setProgress(progress);
	}

	@Override
	public float getSize() {
		return 0.15f;
	}
}
