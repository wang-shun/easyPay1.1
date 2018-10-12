package com.acewill.ordermachine.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.eventbus.PayMethodEvent;
import com.acewill.ordermachine.model.PaymentInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Author：Anch
 * Date：2017/12/29 17:53
 * Desc：
 */
public class PayMethodAdapter extends MyAdapter {

	private TextView paymethod_name;

	public PayMethodAdapter(Context context, List dataList) {
		super(context, dataList);
	}

	@Override
	protected View getItemView(int position, View convertView, ViewGroup parent) {
		final PaymentInfo pInfo = (PaymentInfo) dataList.get(position);
		View              view  = null;
		if (pInfo.id == -8 || pInfo.id == 1 || pInfo.id == 2) {
			view = View.inflate(mContext, R.layout.item_paymethod2, null);
			paymethod_name = (TextView) view.findViewById(R.id.paymethod_name);
		} else {
			view = View.inflate(mContext, R.layout.item_paymethod, null);
			paymethod_name = (TextView) view.findViewById(R.id.paymethod_name);
			paymethod_name.setText(pInfo.name);
		}
		if (pInfo.status == 0 || pInfo.id == 4 || pInfo.id == 5 || pInfo.id == -33)
			view.setVisibility(View.GONE);
		else
			view.setVisibility(View.VISIBLE);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PayMethodEvent event = new PayMethodEvent(pInfo.id, pInfo.name, pInfo.cashbox);
				EventBus.getDefault().post(event);
			}
		});
		return view;
	}


}
