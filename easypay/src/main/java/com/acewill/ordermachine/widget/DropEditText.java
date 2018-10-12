package com.acewill.ordermachine.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.acewill.ordermachine.R;

public class DropEditText extends FrameLayout implements View.OnClickListener, OnItemClickListener,
		TextWatcher {
	private static final String TAG = "DropEditText";
	private CommonEditText mEditText;  // 输入框
	private ImageView      mDropImageRight; // 右边的图片按钮
	private ImageView      mDropImageLeft; // 右边的图片按钮
	private RelativeLayout dropview_ly; // 右边的图片按钮
	private PopupWindow    mPopup; // 点击图片弹出popupwindow
	private WrapListView   mPopView; // popupwindow的布局
	private boolean        mDrawableRightVisible; //
	private boolean        mDrawableLeftVisible; //

	private int     mDrawableRight;
	private int     mDrawableLeft;
	private int     mDropMode; // flow_parent or wrap_content
	private String  mHit;
	private int     background;
	private int     inputType;
	private boolean focusable;

	public DropEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DropEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.edit_layout, this);
		mPopView = (WrapListView) LayoutInflater.from(context).inflate(R.layout.pop_view, null);

		TypedArray ta = context
				.obtainStyledAttributes(attrs, R.styleable.DropEditText, defStyle, 0);
		mDrawableRight = ta
				.getResourceId(R.styleable.DropEditText_drawableRight, R.drawable.ic_launcher);


		background = ta
				.getResourceId(R.styleable.DropEditText_background, R.drawable.ic_launcher);
		mDrawableLeft = ta
				.getResourceId(R.styleable.DropEditText_drawableLeft, R.drawable.ic_launcher);
		mDropMode = ta.getInt(R.styleable.DropEditText_dropMode, 0);
		mHit = ta.getString(R.styleable.DropEditText_hint);
		mDrawableRightVisible = ta.getBoolean(R.styleable.DropEditText_drawableRightVisible, false);
		mDrawableLeftVisible = ta.getBoolean(R.styleable.DropEditText_drawableLeftVisible, false);
		focusable = ta.getBoolean(R.styleable.DropEditText_focusable, true);
		inputType = ta.getInt(R.styleable.DropEditText_inputType, 1);
		ta.recycle();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mEditText = (CommonEditText) findViewById(R.id.dropview_edit);
		mEditText.addTextChangedListener(this);
		mDropImageRight = (ImageView) findViewById(R.id.dropview_image);
		mDropImageLeft = (ImageView) findViewById(R.id.dropview_image_left);
		dropview_ly = (RelativeLayout) findViewById(R.id.dropview_ly);

		mEditText.setSelectAllOnFocus(true);
		mEditText.setFocusable(focusable);
		mDropImageRight.setImageResource(mDrawableRight);
		mDropImageLeft.setImageResource(mDrawableLeft);
		dropview_ly.setBackground(getResources().getDrawable(background));


		if (!TextUtils.isEmpty(mHit)) {
			mEditText.setHint(mHit);
		}
		if (inputType == 0)
			mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
		//			mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		//		if (mEditText.getInputType() == InputType.TYPE_TEXT_VARIATION_PASSWORD)

		mDropImageRight.setVisibility(mDrawableRightVisible ? View.VISIBLE : View.GONE);
		mDropImageLeft.setVisibility(mDrawableLeftVisible ? View.VISIBLE : View.GONE);
		mDropImageRight.setOnClickListener(this);
		mPopView.setOnItemClickListener(this);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
	                        int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// 如果布局发生改
		// 并且dropMode是flower_parent
		// 则设置ListView的宽度
		if (changed && 0 == mDropMode) {
			mPopView.setListWidth(getMeasuredWidth());
		}
	}

	/**
	 * 设置Adapter
	 *
	 * @param adapter ListView的Adapter
	 */
	public void setAdapter(BaseAdapter adapter) {
		mPopView.setAdapter(adapter);

		mPopup = new PopupWindow(mPopView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mPopup.setFocusable(true); // 让popwin获取焦点
	}

	/**
	 * 获取输入框内的内容
	 *
	 * @return String content
	 */
	public String getText() {
		return mEditText.getText().toString();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.dropview_image) {
			if (mPopup == null) {
				return;
			}
			if (mPopup.isShowing()) {
				mPopup.dismiss();
				return;
			}

			mPopup.showAsDropDown(this, 0, 5);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
	                        long id) {
		mEditText.setText(mPopView.getAdapter().getItem(position).toString());
		mPopup.dismiss();
	}

	public void setText(String s) {
		mEditText.setText(s.toString());
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
