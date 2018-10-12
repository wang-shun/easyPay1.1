package com.acewill.ordermachine.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.adapter.MyAdapter;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.util.PriceUtil;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.ordermachine.widget.DropEditText;
import com.acewill.ordermachine.widget.TitileLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * Author：Anch
 * Date：2017/11/22 11:53
 * Desc：
 */
public class HuiYuanChaXunActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = "HuiYuanChaXunActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_huiyuanchaxun);
		initTitleLayout();
		initView();
		initEvent();
	}


	private GridView        gridView;
	private ListView        listView;
	private ListViewAdapter mListViewAdapter;
	private LinearLayout    container_ly;

	private void initView() {
		gridView = (GridView) findViewById(R.id.mygridview);
		listView = (ListView) findViewById(R.id.mylistview);
		member_name = (TextView) findViewById(R.id.member_name);
		member_phone = (TextView) findViewById(R.id.member_phone);
		member_uno = (TextView) findViewById(R.id.member_uno);
		member_gradename = (TextView) findViewById(R.id.member_gradename);
		member_birthday = (TextView) findViewById(R.id.member_birthday);
		member_registered = (TextView) findViewById(R.id.member_registered);
		member_type = (TextView) findViewById(R.id.member_type);
		member_balance = (TextView) findViewById(R.id.member_balance);
		member_credit = (TextView) findViewById(R.id.member_credit);
		tips = (TextView) findViewById(R.id.tips);
		container_ly = (LinearLayout) findViewById(R.id.container_ly);
		mListViewAdapter = new ListViewAdapter(new ArrayList());

		String html = "<font color=\"red\">*</font>提示:会员门店POS地址为:<font color=\"#3aa8d9\">pos.acewill.net</font>,会员商户中心地址为:<font color=\"#3aa8d9\">manage.acewill.net</font>";
		tips
				.setText(Html.fromHtml(html));
	}

	private DropEditText member_query_et;

	private void initEvent() {
		member_query_et = (DropEditText) findViewById(R.id.member_query_et);
		findViewById(R.id.btn_search).setOnClickListener(this);
	}

	private TextView member_name;
	private TextView member_phone;
	private TextView member_uno;
	private TextView member_gradename;
	private TextView member_birthday;
	private TextView member_registered;
	private TextView member_type;
	private TextView member_balance;
	private TextView member_credit;
	private TextView tips;


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFinish(OnFinishEvent event) {
		//		ToastUtils.showToast(HuiYuanChaXunActivity.this, event.getMsg());

	}


	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分
	private List<WshAccount> accountList;

	private void doMemberLogin(String code) {
		Log.e(TAG, "会员支付开始时间:" + format.format(new Date()));
		new NewPos().memberLogin(code, new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(String response, int id) {
				try {
					int result = GsonUtils.getIntData("result", response);
					if (result == 0) {
						WshAccountRes res = GsonUtils
								.getSingleBean(response, WshAccountRes.class);
						accountList = res.content;
						List<CardName> accountNameList = new ArrayList<>();
						for (int i = 0; accountList != null && i < accountList.size(); i++) {
							if (i == 0)
								accountNameList
										.add(new CardName(accountList.get(i).getGradeName(), true));
							else
								accountNameList.add(new CardName(accountList.get(i)
										.getGradeName(), false));
						}
						gridView.setAdapter(new GridViewAdapter(accountNameList));
						mListViewAdapter.setData(accountList.get(0).getCoupons());
						listView.setAdapter(mListViewAdapter);
						refreshUI(accountList.get(0));

						//第二步支付
					} else {
						ToastUtils.showToast(HuiYuanChaXunActivity.this, GsonUtils
								.getData("errmsg", response));
					}
				} catch (Exception e) {

				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		String number = member_query_et.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			ToastUtils.showToast(HuiYuanChaXunActivity.this, "请输入会员卡号或手机号");
			return;
		}
		doMemberLogin(number);
	}

	private class CardName {
		private String  name;
		private boolean isSelect;

		public CardName(String name, boolean isSelect) {
			this.name = name;
			this.isSelect = isSelect;
		}

		public boolean isSelect() {
			return isSelect;
		}

		public void setSelect(boolean select) {
			isSelect = select;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private void refreshUI(WshAccount account) {
		member_name.setText(account.getName());
		member_uno.setText(account.getUno() + "");
		member_phone.setText(account.getPhone());
		member_birthday.setText(account.getBirthday());
		member_registered.setText(account.getRegistered());
		member_gradename.setText(account.getGradeName());

		if ("wx".equals(account.getType())) {
			member_type.setText("微信会员卡");
		} else if ("ph".equals(account.getType())) {
			member_type.setText("实体卡");
		} else {
			member_type.setText(account.getType());
		}
		member_balance.setText(PriceUtil.divide(account.getBalance() + "", "100").toString());
		member_credit.setText(account.getCredit() + "");
		mListViewAdapter.setData(account.getCoupons());
		mListViewAdapter.notifyDataSetChanged();
		if (container_ly.getVisibility() != View.VISIBLE)
			container_ly.setVisibility(View.VISIBLE);
	}

	private class GridViewAdapter<String> extends MyAdapter {
		public GridViewAdapter(List dataList) {
			super(dataList);
		}

		@Override
		protected View getItemView(final int position, View convertView, ViewGroup parent) {
			final Button   button = new Button(HuiYuanChaXunActivity.this);
			final CardName name   = (CardName) dataList.get(position);
			button.setText(name.getName());
			button.setTextColor(getResources().getColor(R.color.color_white));
			if (name.isSelect()) {
				button.setBackground(getResources().getDrawable(R.drawable.border_blue_selector));
			} else {
				button.setBackgroundColor(getResources().getColor(R.color.gray));
			}
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					refreshUI(accountList.get(position));
					setSelect(dataList);
					notifyDataSetChanged();
				}
			});
			return button;
		}

		private void setSelect(List<CardName> dataList) {
			for (CardName name : dataList)
				name.setSelect(!name.isSelect());
		}
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

	private class ListViewAdapter extends MyAdapter {


		public ListViewAdapter(List dataList) {
			super(dataList);
		}

		@Override
		protected View getItemView(int position, View convertView, ViewGroup parent) {
			MyViewHolder holder;
			if (convertView == null) {
				convertView = View
						.inflate(HuiYuanChaXunActivity.this, R.layout.item_huiyuanguanli, null);
				holder = new MyViewHolder();
				holder.item_member_quanmingcheng = (TextView) convertView
						.findViewById(R.id.item_member_quanmingcheng);
				holder.item_member_quanshuliang = (TextView) convertView
						.findViewById(R.id.item_member_quanshuliang);
				holder.item_member_quanqishishijian = (TextView) convertView
						.findViewById(R.id.item_member_quanqishishijian);
				holder.item_member_quanguoqishijian = (TextView) convertView
						.findViewById(R.id.item_member_quanguoqishijian);
				convertView.setTag(holder);
			} else {
				holder = (MyViewHolder) convertView.getTag();
			}
			WshAccountCoupon coupon = (WshAccountCoupon) dataList.get(position);
			holder.item_member_quanmingcheng.setText(coupon.getTitle());
			holder.item_member_quanshuliang.setText(coupon.getCoupon_ids().size() + "");
			holder.item_member_quanqishishijian.setText(coupon.getEffective_time());
			holder.item_member_quanguoqishijian.setText(coupon.getFailure_time());
			return convertView;
		}

		private class MyViewHolder {
			TextView item_member_quanmingcheng;
			TextView item_member_quanshuliang;
			TextView item_member_quanqishishijian;
			TextView item_member_quanguoqishijian;
		}
	}

}
