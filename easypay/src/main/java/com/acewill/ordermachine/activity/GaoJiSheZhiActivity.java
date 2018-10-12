package com.acewill.ordermachine.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.fragment.GuanYuFragment;
import com.acewill.ordermachine.fragment.MenDianPeiZhiFragment;
import com.acewill.ordermachine.fragment.ZhongDuanPeiZhiFragment;
import com.acewill.ordermachine.util.SharedPreferencesUtil;
import com.acewill.ordermachine.util_new.TimeUtil;
import com.acewill.ordermachine.widget.TitileLayout;

import java.text.ParseException;
import java.util.Date;

/**
 * Author：Anch
 * Date：2017/11/23 11:53
 * Desc：
 */
public class GaoJiSheZhiActivity extends BaseActivity implements View.OnClickListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaojishezhi);
		initTitleLayout();
		initFragment1();
		initView();
	}

	private TextView mendianpeizhi;
	private TextView zhongduanpeizhi;
	private TextView guanyu;
	private TextView error_count;
	private TextView error_count2;

	private void initView() {
		mendianpeizhi = (TextView) findViewById(R.id.mendianpeizhi);
		zhongduanpeizhi = (TextView) findViewById(R.id.zhongduanpeizhi);
		error_count = (TextView) findViewById(R.id.error_count);
		error_count2 = (TextView) findViewById(R.id.error_count2);
		error_count.setVisibility(TextUtils.isEmpty(SharedPreferencesUtil
				.getNewVersionPath(mActivity)) ? View.GONE : View.VISIBLE);
		try {
			int betweenday = TimeUtil
					.daysBetween(new Date(), new Date(Common.SHOP_INFO.storeEndTime));
			error_count2.setVisibility(betweenday <= 15 ? View.VISIBLE : View.GONE);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		guanyu = (TextView) findViewById(R.id.guanyu);
		mendianpeizhi.setOnClickListener(this);
		zhongduanpeizhi.setOnClickListener(this);
		guanyu.setOnClickListener(this);
		mendianpeizhi.setSelected(true);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.mendianpeizhi:
				mendianpeizhi.setSelected(true);
				zhongduanpeizhi.setSelected(false);
				guanyu.setSelected(false);
				changeFragment(R.id.mendianpeizhi);
				break;
			case R.id.zhongduanpeizhi:
				zhongduanpeizhi.setSelected(true);
				mendianpeizhi.setSelected(false);
				guanyu.setSelected(false);
				changeFragment(R.id.zhongduanpeizhi);
				break;
			case R.id.guanyu:
				guanyu.setSelected(true);
				zhongduanpeizhi.setSelected(false);
				mendianpeizhi.setSelected(false);
				changeFragment(R.id.guanyu);
				break;
		}
	}

	private void changeFragment(int id) {
		switch (id) {
			case R.id.mendianpeizhi:
				initFragment1();
				break;
			case R.id.zhongduanpeizhi:
				initFragment2();
				break;
			case R.id.guanyu:
				initFragment3();
				break;
		}
	}


	private MenDianPeiZhiFragment   f1;
	private ZhongDuanPeiZhiFragment f2;
	private GuanYuFragment          f3;


	//显示第一个fragment
	private void initFragment1() {

		//开启事务，fragment的控制是由事务来实现的
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		//第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个
		if (f1 == null) {
			f1 = new MenDianPeiZhiFragment();
			transaction.add(R.id.container_ly, f1);
		}
		//隐藏所有fragment
		hideFragment(transaction);
		//显示需要显示的fragment
		transaction.show(f1);

		//提交事务
		transaction.commit();
	}

	//显示第二个fragment
	private void initFragment2() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		if (f2 == null) {
			f2 = new ZhongDuanPeiZhiFragment();
			transaction.add(R.id.container_ly, f2);
		}
		hideFragment(transaction);
		transaction.show(f2);

		transaction.commit();
	}

	//显示第三个fragment
	private void initFragment3() {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		if (f3 == null) {
			f3 = new GuanYuFragment();
			transaction.add(R.id.container_ly, f3);
		}
		hideFragment(transaction);
		transaction.show(f3);
		transaction.commit();
	}

	//隐藏所有的fragment
	private void hideFragment(FragmentTransaction transaction) {
		if (f1 != null) {
			transaction.hide(f1);
		}
		if (f2 != null) {
			transaction.hide(f2);
		}
		if (f3 != null) {
			transaction.hide(f3);
		}
	}


}
