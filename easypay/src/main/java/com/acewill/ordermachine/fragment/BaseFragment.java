package com.acewill.ordermachine.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.acewill.ordermachine.dialog.ErrorDialog;
import com.acewill.ordermachine.dialog.LoadingDialog;

/**
 * Author：Anch
 * Date：2017/11/23 16:39
 * Desc：
 */
public class BaseFragment extends Fragment {
	protected Context mContext;
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mContext = context;
	}
	public LoadingDialog loadingDialog;
	public ErrorDialog   mErrorDialog;

	public void showLoadingDialog() {
		loadingDialog = LoadingDialog.newInstance();
		loadingDialog.show(getActivity().getSupportFragmentManager(), "LoadingDialog");
	}

	public void dismissLoadingDialog() {
		if (loadingDialog == null)
			return;
		loadingDialog.dismiss();
	}

	public void dismissErrorDialog() {
		if (mErrorDialog == null)
			return;
		mErrorDialog.dismiss();
	}

	public void showErrorDialog(String msg,boolean showRadioBtn) {
		mErrorDialog = ErrorDialog.newInstance(msg,showRadioBtn);
		mErrorDialog.show(getActivity().getSupportFragmentManager(), "ErrorMsg");
	}
}
