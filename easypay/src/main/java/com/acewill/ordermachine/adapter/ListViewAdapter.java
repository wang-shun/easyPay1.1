package com.acewill.ordermachine.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.model.RefundReasonBean;

import java.util.List;

/**
 * Author：Anch
 * Date：2017/11/30 16:25
 * Desc：
 */
public class ListViewAdapter extends MyAdapter {


	public ListViewAdapter(Context context, List dataList) {
		super(context, dataList);
	}

	@Override
	protected View getItemView(final int position, View convertView, ViewGroup parent) {
		MyViewHolder holder;
		if (convertView == null) {
			convertView = View
					.inflate(mContext, R.layout.item_tuikuan, null);
			holder = new MyViewHolder();
			holder.item_tuikuan_list_img = (ImageView) convertView
					.findViewById(R.id.item_tuikuan_list_img);
			holder.item_tuikuan_list_tv = (TextView) convertView
					.findViewById(R.id.item_tuikuan_list_tv);
			convertView.setTag(holder);
		} else {
			holder = (MyViewHolder) convertView.getTag();
		}
		final RefundReasonBean bean = (RefundReasonBean) dataList.get(position);
		holder.item_tuikuan_list_tv.setText(bean.getReason());
		holder.item_tuikuan_list_img.setVisibility(bean.isSelect() ? View.VISIBLE : View.INVISIBLE);
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bean.isSelect()) {
					return;
				}
				for (Object bean3 : dataList) {
					RefundReasonBean bean2 = (RefundReasonBean) bean3;
					if (bean2.getId() == bean.getId())
						bean2.setSelect(true);
					else
						bean2.setSelect(false);
				}
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	private class MyViewHolder {

		ImageView item_tuikuan_list_img;

		TextView item_tuikuan_list_tv;
	}

	public int getSelect() {
		for (Object bean3 : dataList) {
			RefundReasonBean bean2 = (RefundReasonBean) bean3;
			if (bean2.isSelect()) {
				return bean2.getId();
			}
		}
		return 0;
	}

}