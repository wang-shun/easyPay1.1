package com.acewill.ordermachine.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acewill.ordermachine.R;

public class TitileLayout extends RelativeLayout implements View.OnClickListener {

	private TextView title;
	private String   title_s;

	public TitileLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitileLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.title_layout, this);

		title = (TextView) findViewById(R.id.title);
		findViewById(R.id.back).setOnClickListener(this);
		TypedArray ta = context
				.obtainStyledAttributes(attrs, R.styleable.TitileLayout, defStyle, 0);
		title_s = ta.getString(R.styleable.TitileLayout_m_title);

		ta.recycle();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		title.setText(title_s);
	}

	@Override
	public void onClick(View v) {
		if (mLisener != null)
			mLisener.onBackClick();
	}

	private OnBackClickLisener mLisener;

	public void setOnBackClickLisener(OnBackClickLisener lisener) {
		this.mLisener = lisener;
	}

	public interface OnBackClickLisener {
		void onBackClick();
	}
}
