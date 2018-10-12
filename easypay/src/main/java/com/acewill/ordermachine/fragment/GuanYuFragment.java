package com.acewill.ordermachine.fragment;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acewill.ordermachine.R;

/**
 * Author：Anch
 * Date：2017/11/23 12:23
 * Desc：
 */
public class GuanYuFragment extends Fragment {

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = View.inflate(inflater.getContext(), R.layout.fragment_guanyu, null);
		initView(view);
		return view;
	}

	private TextView fuwuqiaddress;
	private TextView gengduozixun_tv;

	private void initView(View view) {
		fuwuqiaddress = (TextView) view.findViewById(R.id.fuwuqiaddress);
		gengduozixun_tv = (TextView) view.findViewById(R.id.gengduozixun_tv);
		init();
	}

	private void init() {
		fuwuqiaddress.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
		fuwuqiaddress.getPaint().setAntiAlias(true);//抗锯齿
		gengduozixun_tv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
		gengduozixun_tv.getPaint().setAntiAlias(true);//抗锯齿
	}
}
