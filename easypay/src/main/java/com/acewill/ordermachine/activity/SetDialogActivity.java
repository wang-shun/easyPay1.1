package com.acewill.ordermachine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.common.Common;

public class SetDialogActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_dialog);
		String[] data = getIntent().getStringArrayExtra("data");

		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
		int i = 0;
		for (String str : data) {

			View item_ll_set =  View.inflate(this,
					R.layout.item_ll_set, null);
			LayoutParams layoutParams = new LinearLayout.LayoutParams(Common.screenWidth - 64, 100);
			final TextView tv = (TextView) item_ll_set.findViewById(R.id.tv);
			tv.setLayoutParams(layoutParams);
			tv.setText(str);
			tv.setId(i);
			i++;
			if(i == data.length){
				item_ll_set.findViewById(R.id.line).setVisibility(View.GONE);
			}
			item_ll_set.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					setResult(RESULT_OK,new Intent().putExtra("position", tv.getId()));
					finish();
				}
			});
			ll.addView(item_ll_set);
		}
		
		findViewById(R.id.tv_cancer).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
