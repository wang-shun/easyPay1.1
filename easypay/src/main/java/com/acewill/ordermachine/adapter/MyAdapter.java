package com.acewill.ordermachine.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Author：Anch
 * Date：2017/11/20 16:28
 * Desc：
 */
public abstract class MyAdapter<T> extends BaseAdapter {


	public Context mContext;
	public List<T> dataList;

	public MyAdapter(List<T> dataList) {
		this.dataList = dataList;
	}

	public MyAdapter(Context context, List dataList) {
		this.mContext = context;
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		if (dataList != null)
			return dataList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (dataList != null)
			return dataList.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItemView(position, convertView, parent);
	}

	protected abstract View getItemView(int position, View convertView, ViewGroup parent);

	public void setData(List<T> coupons) {
		if (dataList != null) {
			dataList.clear();
		}
		dataList.addAll(coupons);
		Log.e("jlkj", "fas");
	}


}
