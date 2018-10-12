package com.acewill.ordermachine.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.acewill.ordermachine.R;

import java.util.List;

/**
 * Author：Anch
 * Date：2017/12/29 17:39
 * Desc：
 */
public class TextViewAdapter extends MyAdapter {

	public TextViewAdapter(Context context, List dataList) {
		super(context, dataList);
	}

	@Override
	protected View getItemView(int position, View convertView, ViewGroup parent) {
		return View.inflate(mContext, R.layout.item_text,null);
	}
}
