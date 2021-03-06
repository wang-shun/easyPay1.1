package com.acewill.ordermachine.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;


/**
 * Created by aqw on 2016/9/14.
 */
public class CommonEditText extends EditText {

	private Context context;

	public CommonEditText(Context context) {
		super(context);
		this.context = context;
	}

	public CommonEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public CommonEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {

			WindowUtil.hiddenKey();
			WindowUtil.hiddenSysKey(this);
			WindowUtil.showKey(context, this);
		}
		return super.dispatchTouchEvent(ev);
	}


}
