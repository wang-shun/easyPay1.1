package com.acewill.ordermachine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.model.LoginReqModel;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.util.SharedPreferencesUtil;
import com.acewill.ordermachine.util_new.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author：Anch
 * Date：2017/11/23 12:13
 * Desc：
 */
public class MenDianPeiZhiFragment extends BaseFragment implements View.OnClickListener,
		CompoundButton.OnCheckedChangeListener {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = View.inflate(inflater.getContext(), R.layout.fragment_mendianpeizhi, null);
		initView(view);
		return view;
	}

	private TextView click_to_manager;
	private TextView mendianmingcheng_tv;
	private TextView mendianid_tv;
	private TextView suoshupinpai_tv;
	private TextView pinpaiid_tv;
	private TextView mendianleixing_tv;
	private TextView mendiandizhi_tv;
	private TextView mendianzuoji_tv;
	private TextView yingyemianji_tv;
	private TextView guanliyuan_tv;
	private TextView shouquandaoqi_tv;
	private TextView suoshuquyu_tv;
	private Switch   refundprint_switch;
	private Switch   quanxian_switch;
	private Switch   jiaobanprint_switch;

	private void initView(View view) {
		click_to_manager = (TextView) view.findViewById(R.id.click_to_manager);
		String url = NewPos.BASE_URL
				.equals("sz.canxingjian.com/") ? "sz.canxingjian.com" : "www.smarant.com";
		String html2 = "管理后台网址:<font color=\"#3aa8d9\">" + url + "</font>(建议使用谷歌浏览器)";
		click_to_manager.setText(Html.fromHtml(html2));
		//		click_to_manager.setOnClickListener(this);
		//		click_to_manager.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
		//		click_to_manager.getPaint().setAntiAlias(true);//抗锯齿

		//找 id
		mendianmingcheng_tv = (TextView) view.findViewById(R.id.mendianmingcheng_tv);
		mendianid_tv = (TextView) view.findViewById(R.id.mendianid_tv);
		suoshupinpai_tv = (TextView) view.findViewById(R.id.suoshupinpai_tv);
		pinpaiid_tv = (TextView) view.findViewById(R.id.pinpaiid_tv);
		suoshuquyu_tv = (TextView) view.findViewById(R.id.suoshuquyu_tv);

		mendianleixing_tv = (TextView) view.findViewById(R.id.mendianleixing_tv);
		mendiandizhi_tv = (TextView) view.findViewById(R.id.mendiandizhi_tv);
		mendianzuoji_tv = (TextView) view.findViewById(R.id.mendianzuoji_tv);
		yingyemianji_tv = (TextView) view.findViewById(R.id.yingyemianji_tv);
		guanliyuan_tv = (TextView) view.findViewById(R.id.guanliyuan_tv);
		shouquandaoqi_tv = (TextView) view.findViewById(R.id.shouquandaoqi_tv);
		refundprint_switch = (Switch) view.findViewById(R.id.refundprint_switch);
		quanxian_switch = (Switch) view.findViewById(R.id.quanxian_switch);
		jiaobanprint_switch = (Switch) view.findViewById(R.id.jiaobanprint_switch);
		refundprint_switch.setOnCheckedChangeListener(this);
		quanxian_switch.setOnCheckedChangeListener(this);
		jiaobanprint_switch.setOnCheckedChangeListener(this);
		refundprint_switch.setChecked(SharedPreferencesUtil.getRefundPrint(mContext));
		quanxian_switch.setChecked(SharedPreferencesUtil.getQuanXian(mContext));
		jiaobanprint_switch.setChecked(SharedPreferencesUtil.getJiaoBan(mContext));

		//赋值
		mendianmingcheng_tv.setText(Common.SHOP_INFO.sname);
		mendianid_tv.setText(LoginReqModel.storeid + "");
		suoshupinpai_tv.setText(Common.SHOP_INFO.brandName);
		pinpaiid_tv.setText(LoginReqModel.brandid);
		mendianleixing_tv.setText("未知类型");
		mendiandizhi_tv.setText(Common.SHOP_INFO.address);
		mendianzuoji_tv.setText(Common.SHOP_INFO.phone);
		yingyemianji_tv.setText("未知大小");
		guanliyuan_tv.setText(Common.SHOP_INFO.realname);
		try {
			int betweenday = TimeUtil
					.daysBetween(new Date(), new Date(Common.SHOP_INFO.storeEndTime));

			String color = "";
			if (betweenday <= 15) {
				color = "red";
			}
			String html = format2
					.format(new Date(Common.SHOP_INFO.storeEndTime)) + "（还剩: <font color=\"" + color + "\">" + betweenday + "</font> 天）";
			shouquandaoqi_tv.setText(Html.fromHtml(html));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		suoshuquyu_tv.setText("未知区域");
	}

	private SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");// 用于格式化日期,作为日志文件名的一部分

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			//			case R.id.click_to_manager:
			//				ToastUtils.showToast(mContext, "manager");
			//				break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.refundprint_switch:
				SharedPreferencesUtil.saveRefundPrint(mContext, isChecked);
				break;
			case R.id.quanxian_switch:
				SharedPreferencesUtil.saveQuanXian(mContext, isChecked);
				break;
			case R.id.jiaobanprint_switch:
				SharedPreferencesUtil.saveJiaoBan(mContext, isChecked);
				break;
		}
	}
}
