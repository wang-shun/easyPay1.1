package com.acewill.ordermachine.dialog;

import android.view.View;

import com.acewill.ordermachine.R;

/**
 * Author：Anch
 * Date：2017/12/25 14:56
 * Desc：
 */
public class ChangeLanguageDialog extends BaseDialog implements View.OnClickListener {
	/**
	 * @return
	 */
	public static ChangeLanguageDialog newInstance() {
		ChangeLanguageDialog fragment = new ChangeLanguageDialog();
		return fragment;
	}

	@Override
	public View getView() {
		View view = View.inflate(mcontext, R.layout.dialog_changelanguage, null);
		view.findViewById(R.id.zhongwen).setOnClickListener(this);
		view.findViewById(R.id.yingwen).setOnClickListener(this);
		return view;
	}

	private OnLanguageSelectListener mListener;

	public void setOnLanguageSelectListener(OnLanguageSelectListener listener) {
		this.mListener = listener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.zhongwen:
				if (mListener != null)
					mListener.onLanguageSelect(0);
				dismiss();
				break;
			case R.id.yingwen:
				if (mListener != null)
					mListener.onLanguageSelect(1);
				dismiss();
				break;
		}
	}

	public interface OnLanguageSelectListener {
		void onLanguageSelect(int position);
	}

	@Override
	public float getSize() {
		return 0.5f;
	}
}
