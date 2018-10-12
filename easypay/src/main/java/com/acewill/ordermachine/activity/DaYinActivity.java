package com.acewill.ordermachine.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acewill.ordermachine.BaseApplication;
import com.acewill.ordermachine.R;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.fragment.DaYinJiLuFragment;
import com.acewill.ordermachine.fragment.DaYinJiZhuangTaiFragment;
import com.acewill.ordermachine.widget.TitileLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Author：Anch
 * Date：2017/11/24 12:31
 * Desc：
 */
public class DaYinActivity extends BaseActivity implements View.OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dayin);
		initTitleLayout();
		initView();
		initFragment1();
	}


	private TextView       dayinjizhungtai;
	private TextView       dayinjilu;
	private TextView       error_count;
	private RelativeLayout dayinjilu_ly;

	private void initView() {
		dayinjizhungtai = (TextView) findViewById(R.id.dayinjizhungtai);
		error_count = (TextView) findViewById(R.id.error_count);
		dayinjilu = (TextView) findViewById(R.id.dayinjilu);
		dayinjilu_ly = (RelativeLayout) findViewById(R.id.dayinjilu_ly);
		dayinjilu_ly.setOnClickListener(this);
		dayinjizhungtai.setOnClickListener(this);
		dayinjizhungtai.setSelected(true);
		//		int count = DataSupport.where("printstatu=?", "0").count(OrderReq.class);
		//		if (count != 0) {
		//			error_count.setVisibility(View.VISIBLE);
		//			error_count.setText(count + "");
		//		}
	}

	private void initTitleLayout() {
		TitileLayout layout = (TitileLayout) findViewById(R.id.title_ly);
		layout.setOnBackClickLisener(new TitileLayout.OnBackClickLisener() {
			@Override
			public void onBackClick() {
				BaseApplication.getInstance().finishActivity(DaYinActivity.this);
			}
		});
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void weiwancheng(OnFinishEvent event) {
		if (event.getMsg().equals("weiwancheng")) {
			if (Integer.parseInt(event.getContent()) != 0) {
				error_count.setVisibility(View.VISIBLE);
				error_count.setText(event.getContent());
			}
		}
	}

	private DaYinJiZhuangTaiFragment f1;
	private DaYinJiLuFragment        f2;

	//显示第一个fragment
	private void initFragment1() {

		//开启事务，fragment的控制是由事务来实现的
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		//第一种方式（add），初始化fragment并添加到事务中，如果为null就new一个
		if (f1 == null) {
			f1 = new DaYinJiZhuangTaiFragment();
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
			f2 = new DaYinJiLuFragment();
			transaction.add(R.id.container_ly, f2);
		}
		hideFragment(transaction);
		transaction.show(f2);

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

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.dayinjilu_ly:
				if (dayinjilu.isSelected())
					return;
				dayinjilu_ly.setSelected(!dayinjilu_ly.isSelected());
				dayinjizhungtai.setSelected(!dayinjizhungtai.isSelected());
				initFragment2();
				break;
			case R.id.dayinjizhungtai:
				if (dayinjizhungtai.isSelected())
					return;
				dayinjilu_ly.setSelected(!dayinjilu_ly.isSelected());
				dayinjizhungtai.setSelected(!dayinjizhungtai.isSelected());
				initFragment1();
				break;
		}
	}
}
