package com.acewill.ordermachine.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Author：Anch
 * Date：2017/11/21 11:37
 * Desc：
 */
public abstract class BaseDialog extends DialogFragment {

	protected Context mcontext;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mcontext = context;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Dialog dialog = getDialog();

		if (dialog != null) {
			Window window = getDialog().getWindow();
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
			//若无该段代码则用系统的样式而不是自己定义的背景样式
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			WindowManager.LayoutParams lp = window.getAttributes();
			window.setGravity(Gravity.CENTER);
			window.setAttributes(lp);
		}
		//XXX初始化view的各控件
		return getView();
	}

	public abstract View getView();

	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = getDialog();
		if (dialog != null) {
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			dialog.getWindow()
					.setLayout((int) (dm.widthPixels * getSize()), ViewGroup.LayoutParams.WRAP_CONTENT);
		}
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
		loadingDialog.setMsg("");
		loadingDialog.dismiss();
	}

	public void dismissErrorDialog() {
		if (mErrorDialog == null)
			return;
		mErrorDialog.dismiss();
	}

	public ErrorDialog showErrorDialog(String msg, boolean showRadioBtn) {
		mErrorDialog = ErrorDialog.newInstance(msg, showRadioBtn);
		mErrorDialog.show(getActivity().getSupportFragmentManager(), "ErrorMsg");
		return mErrorDialog;
	}


	public abstract float getSize();

}
