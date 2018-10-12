package com.acewill.ordermachine.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.model.OrderReq;
import com.acewill.ordermachine.util_new.TimeUtil;
import com.acewill.paylibrary.PayReqModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Author：Anch
 * Date：2017/11/20 16:27
 * Desc：
 */
public class DingDanItemAdapter<T> extends MyAdapter {
	private List<OrderReq>  dataList;
	private FragmentManager manager;

	public DingDanItemAdapter(Context context, List<T> data, FragmentManager ft) {
		super(context, data);
		this.mContext = context;
		dataList = (List<OrderReq>) data;
		manager = ft;
	}

	@Override
	protected View getItemView(int position, View convertView, ViewGroup parent) {
		MyHolder holder;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_dingdantongji, null);
			holder = new MyHolder();
			holder.item_caozuo_img = (ImageView) convertView.findViewById(R.id.item_caozuo_img);
			holder.item_dingdanhao_tv = (TextView) convertView
					.findViewById(R.id.item_dingdanhao_tv);
			holder.item_xiadanshijian_tv = (TextView) convertView
					.findViewById(R.id.item_xiadanshijian_tv);
			holder.item_shishoujine_tv = (TextView) convertView
					.findViewById(R.id.item_shishoujine_tv);
			holder.item_youhuijine_tv = (TextView) convertView
					.findViewById(R.id.item_youhuijine_tv);
			holder.item_dingdanzhuangtai_tv = (TextView) convertView
					.findViewById(R.id.item_dingdanzhuangtai_tv);
			holder.item_tuikuanjine_tv = (TextView) convertView
					.findViewById(R.id.item_tuikuanjine_tv);
			holder.item_tuikuanshijian_tv = (TextView) convertView
					.findViewById(R.id.item_tuikuanshijian_tv);
			holder.item_zhifufangshi_tv = (TextView) convertView
					.findViewById(R.id.item_zhifufangshi_tv);
			holder.shanghuzhifupingtaidingdanhao_tv = (TextView) convertView
					.findViewById(R.id.item_shanghuzhifupingtaidingdanhao_tv);
			holder.item_zhongduandanhao_tv = (TextView) convertView
					.findViewById(R.id.item_zhongduandanhao_tv);
			holder.item_shouyinyuan_tv = (TextView) convertView
					.findViewById(R.id.item_shouyinyuan_tv);
			holder.item_huiyuanxinxi_tv = (TextView) convertView
					.findViewById(R.id.item_huiyuanxinxi_tv);
			convertView.setTag(holder);
		} else {
			holder = (MyHolder) convertView.getTag();
		}
		final OrderReq recorder = dataList.get(position);
		holder.item_dingdanhao_tv.setText(recorder.getOid() + "");//如果已经上传了的话就会变成订单号，如果没有上传就会变成 id
		holder.item_shishoujine_tv.setText(recorder.getCost());
		holder.item_xiadanshijian_tv
				.setText(format.format(new Date(Long.parseLong(recorder.getCreatedAt()))));
		holder.item_zhifufangshi_tv.setText(recorder.getPaymentTypeName());
		holder.item_youhuijine_tv.setText("0");
		holder.item_shouyinyuan_tv.setText(recorder.getRealname());

		if (recorder.getPaymentStatus().equals("PAYED")) {
			holder.item_dingdanzhuangtai_tv.setText("已支付");
			holder.item_dingdanzhuangtai_tv
					.setTextColor(mContext.getResources().getColor(R.color.gray_search_text));
		} else if (recorder.getPaymentStatus().equals("REFUND")) {
			holder.item_dingdanzhuangtai_tv.setText("已退单");
			holder.item_dingdanzhuangtai_tv
					.setTextColor(mContext.getResources().getColor(R.color.red1));
		}
		if (PayReqModel.PTID_SSS_MEMBER_CHUZHISTR
				.equals(recorder.getPaymentTypeName()) || PayReqModel.PTID_SSS_MEMBER_YOUHUIQUANSTR
				.equals(recorder.getPaymentTypeName())) {
			if (!TextUtils.isEmpty(recorder.getMemberPhoneNumber()))
				holder.item_huiyuanxinxi_tv
						.setText(recorder.getMemberName() + "|\n" + recorder
								.getMemberPhoneNumber());
			else
				holder.item_huiyuanxinxi_tv
						.setText(recorder.getMemberName() + "|\n" + recorder
								.getuActualNo());
		} else {
			holder.item_huiyuanxinxi_tv
					.setText("");
		}

		if (!TextUtils.isEmpty(recorder.getRefundMoney()) && Float
				.parseFloat(recorder.getRefundMoney()) != 0) {
			holder.item_tuikuanjine_tv.setText("-" + recorder.getRefundMoney());
			holder.item_tuikuanjine_tv
					.setTextColor(mContext.getResources().getColor(R.color.green1));
		} else {
			holder.item_tuikuanjine_tv.setText("");
			holder.item_tuikuanjine_tv
					.setTextColor(mContext.getResources().getColor(R.color.gray_search_text));
		}

		if (recorder.getRefundTime() != 0) {
			holder.item_tuikuanshijian_tv
					.setText(format.format(new Date(recorder.getRefundTime())));
		} else {
			holder.item_tuikuanshijian_tv
					.setText("");
		}

		holder.shanghuzhifupingtaidingdanhao_tv.setText(recorder.getBis_id());
		holder.item_zhongduandanhao_tv.setText(recorder.getTerminalOrderId() + "");
		//1、获取当前时间和下单时间，如果是一天前的就设置成灰，不能点击
		//2、获取订单状态，如果还没有退款的就设置成绿色，可以退款，如果已经退款，设置成红色不能点击
		try {
			boolean moreThanOneDay = TimeUtil
					.moreThanOneDay(new Date(Long.parseLong(recorder.getCreatedAt())), new Date());
			if (moreThanOneDay) {//大于一天或者不为已支付状态不能退款
				convertView.setEnabled(false);
				holder.item_caozuo_img.setImageResource(R.drawable.cannot_refund);//不能退款的
			} else if (recorder.getPaymentStatus().equals("REFUND")) {
				convertView.setEnabled(false);
				holder.item_caozuo_img.setImageResource(R.drawable.refund_success);//已经退款的
			} else {
				convertView.setEnabled(true);
				holder.item_caozuo_img.setImageResource(R.drawable.refund);//退款成功
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return convertView;
	}

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	private class MyHolder {
		ImageView item_caozuo_img;
		TextView  item_dingdanhao_tv;
		TextView  item_xiadanshijian_tv;
		TextView  item_shishoujine_tv;
		TextView  item_youhuijine_tv;
		TextView  item_dingdanzhuangtai_tv;
		TextView  item_tuikuanjine_tv;
		TextView  item_tuikuanshijian_tv;
		TextView  item_zhifufangshi_tv;
		TextView  shanghuzhifupingtaidingdanhao_tv;
		TextView  item_zhongduandanhao_tv;
		TextView  item_shouyinyuan_tv;
		TextView  item_huiyuanxinxi_tv;
	}


}
