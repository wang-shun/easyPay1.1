package com.acewill.ordermachine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.activity.BindActivity;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.constant.Contants;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.fragmentdialog.DownLoadProgressDialog;
import com.acewill.ordermachine.fragmentdialog.NewVersionDialog;
import com.acewill.ordermachine.model.BaseModelSz;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.util.SharedPreferencesUtil;
import com.acewill.ordermachine.util_new.AppUtils;
import com.acewill.ordermachine.util_new.DownLoadAPPUtils;
import com.acewill.ordermachine.util_new.NetworkUtils;
import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;

import okhttp3.Call;

/**
 * Author：Anch
 * Date：2017/11/23 12:22
 * Desc：
 */
public class ZhongDuanPeiZhiFragment extends BaseFragment implements View.OnClickListener,
		CompoundButton.OnCheckedChangeListener {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = View.inflate(inflater.getContext(), R.layout.fragment_zhongduanpeizhi, null);
		initView(view);
		EventBus.getDefault().register(this);
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	private TextView shouquanma_tv;
	private TextView ip_address_tv;
	private TextView terminal_name;
	private TextView version_btn;

	private Switch shifoufayinxiaofeixiaopiao;

	private void initView(View view) {
		ip_address_tv = (TextView) view.findViewById(R.id.ip_address_tv);
		shouquanma_tv = (TextView) view.findViewById(R.id.shouquanma_tv);
		terminal_name = (TextView) view.findViewById(R.id.terminal_name);
		version_btn = (TextView) view.findViewById(R.id.version_btn);
		version_btn.setOnClickListener(this);
		terminal_name.setText(Common.SHOP_INFO.tname);
		shifoufayinxiaofeixiaopiao = (Switch) view.findViewById(R.id.shifoufayinxiaofeixiaopiao);
		shifoufayinxiaofeixiaopiao.setOnCheckedChangeListener(this);
		view.findViewById(R.id.unbind_tv).setOnClickListener(this);
		ip_address_tv.setText(NetworkUtils.getIPAddress(true));
		shouquanma_tv.setText(Common.SHOP_INFO.terminalMac);
		String path = SharedPreferencesUtil.getNewVersionPath(mContext);
		if (TextUtils.isEmpty(path)) {
			version_btn.setText("当前版本V" + AppUtils.getAppVersionName() + ",已是最新版本！");
			version_btn.setEnabled(false);
		} else {
			version_btn.setText("有新版本，点击更新!");
		}
		shifoufayinxiaofeixiaopiao.setChecked(SharedPreferencesUtil.getifPrintTicket(mContext));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.unbind_tv:
				unbind();
				break;
			case R.id.version_btn:
				showNewVersionDialog(SharedPreferencesUtil.getNewVersionDescription(mContext));
				break;
		}
	}

	@Subscribe
	public void downLoadNewVersion(OnFinishEvent event) {
		if ("newversion".equals(event.getMsg())) {
			if (TextUtils.isEmpty(SharedPreferencesUtil
					.getNewVersionPath(mContext))) {
				ToastUtils.showToast(mContext, "下载出现异常!");
				return;
			}
			downFile(SharedPreferencesUtil
					.getNewVersionPath(mContext), Contants.SDPATH, "diancan.apk");
		}
	}

	private void showNewVersionDialog(String description) {
		NewVersionDialog dialog = NewVersionDialog.newInstance(description);
		dialog.show(getActivity().getSupportFragmentManager(), "NewVersionDialog");

	}

	private DownLoadProgressDialog mDownLoadProgressDialog;

	private void showDownLoadProgressDialog(int max) {
		mDownLoadProgressDialog = DownLoadProgressDialog.newInstance(max);
		mDownLoadProgressDialog
				.show(getActivity().getSupportFragmentManager(), "DownLoadProgressDialog");
	}

	/**
	 * */
	public void downFile(final String urlStr, final String path,
	                     final String fileName) {
		showDownLoadProgressDialog(100);
		OkHttpUtils.get().url(urlStr).build()
				.execute(new FileCallBack(path, fileName) {
					@Override
					public void inProgress(float progress, long total, int id) {
						mDownLoadProgressDialog.setProgress((int) (progress * 100));
					}

					@Override
					public void onResponse(File response, int id) {
						mDownLoadProgressDialog.dismiss();
						DownLoadAPPUtils.getInstance(getActivity()).install(response);
					}

					@Override
					public void onError(Call call, Exception e, int id) {
						PrintFile
								.printResult2("Class_SetActivity_Method_downFile()_onError()异常,异常原因:" + e
										.getMessage());
						mDownLoadProgressDialog.dismiss();
						e.printStackTrace();
					}
				});

	}

	private void unbind() {
		showLoadingDialog();
		new NewPos()
				.unbind(mContext, Common.SHOP_INFO.terminalMac, new NewPos.Callback<BaseModelSz>() {
					@Override
					public void onError(Call call, Exception e, int id) {
						dismissLoadingDialog();
					}

					@Override
					public void onResponse(BaseModelSz response, int id) {
						dismissLoadingDialog();
						if (response.result == 0) {
							SharedPreferencesUtil.cleanLoginInfo(mContext);
							startActivity(new Intent(mContext, BindActivity.class));
							getActivity().finish();
						} else {
							Toast.makeText(mContext, Common.errMsg, Toast.LENGTH_SHORT).show();
						}
					}
				});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.shifoufayinxiaofeixiaopiao:
				SharedPreferencesUtil.saveifPrintTicket(mContext, isChecked);
				break;
		}
	}
}
