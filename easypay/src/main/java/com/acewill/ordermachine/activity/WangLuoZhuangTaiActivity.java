package com.acewill.ordermachine.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.util_new.NetworkUtils;
import com.acewill.ordermachine.widget.TitileLayout;

/**
 * Author：Anch
 * Date：2017/11/24 16:11
 * Desc：
 */
public class WangLuoZhuangTaiActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wangluozhuangtai);
		initTitleLayout();
		initView();
	}

	private TextView terminal_ip;

	private void initView() {
		terminal_ip = (TextView) findViewById(R.id.terminal_ip);
		terminal_ip.setText("终端IP:" + NetworkUtils.getIPAddress(true));

	}

	private void initTitleLayout() {
		TitileLayout layout = (TitileLayout) findViewById(R.id.title_ly);
		layout.setOnBackClickLisener(new TitileLayout.OnBackClickLisener() {
			@Override
			public void onBackClick() {
				finish();
			}
		});
	}

}
