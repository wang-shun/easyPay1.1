package com.acewill.ordermachine.dialog;

import android.support.annotation.IdRes;
import android.text.Html;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.util.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Author：Anch
 * Date：2017/12/12 11:17
 * Desc：
 */
public class EnvironmentDialog extends BaseDialog implements RadioGroup.OnCheckedChangeListener,
		View.OnClickListener {
	/**
	 * @return
	 */
	public static EnvironmentDialog newInstance() {
		EnvironmentDialog fragment = new EnvironmentDialog();
		return fragment;
	}

	@Override
	public View getView() {
		View view = View.inflate(mcontext, R.layout.dialog_environment, null);
		initView(view);
		return view;
	}

	String ev1 = "<font color=\"#3aa8d9\">" + "www.smarant.com" + "</font>" + "(默认，供门店使用的生产环境)";
	String ev2 = "<font color=\"#666666\">" + "www.smarant.com" + "</font>" + "(默认，供门店使用的生产环境)";
	String ev3 = "<font color=\"#3aa8d9\">" + "sz.canxingjian.com" + "</font>" + "(慎用，供技术部门使用的调试环境)";
	String ev4 = "<font color=\"#666666\">" + "sz.canxingjian.com" + "</font>" + "(慎用，供技术部门使用的调试环境)";
	RadioButton environment_choose_rb1;
	RadioButton environment_choose_rb2;
	RadioGroup  environment_choose_rg;

	private void initView(View view) {
		//		*提示:非技术人员请勿更改配置!误入请关闭，谢谢!
		TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
		view.findViewById(R.id.btn).setOnClickListener(this);
		view.findViewById(R.id.close_img).setOnClickListener(this);
		environment_choose_rg = (RadioGroup) view
				.findViewById(R.id.environment_choose_rg);
		environment_choose_rb1 = (RadioButton) view
				.findViewById(R.id.environment_choose_rb1);
		environment_choose_rb2 = (RadioButton) view
				.findViewById(R.id.environment_choose_rb2);
		String html = "<font color=\"red\">" + "*" + "</font> 提示:" + "<font color=\"red\">" + "非技术人员请勿更改配置!误入请关闭" + "</font>" + ",谢谢!";
		dialog_title.setText(Html.fromHtml(html));
		environment_choose_rb1.setText(Html.fromHtml(ev1));
		environment_choose_rb2.setText(Html.fromHtml(ev4));
		environment_choose_rg.setOnCheckedChangeListener(this);
		currentId = "www.smarant.com/"
				.equals(NewPos.BASE_URL) ? R.id.environment_choose_rb1 : R.id.environment_choose_rb2;
		environment_choose_rg.check(currentId);
	}

	private int currentId;

	@Override
	public float getSize() {
		return 0.7f;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
		RadioButton rb = (RadioButton) group.findViewById(group
				.getCheckedRadioButtonId());
		String currentSelected = "";
		switch (rb.getId()) {
			case R.id.environment_choose_rb1:
				environment_choose_rb1.setText(Html.fromHtml(ev1));
				environment_choose_rb2.setText(Html.fromHtml(ev4));
				break;
			case R.id.environment_choose_rb2:
				environment_choose_rb1.setText(Html.fromHtml(ev2));
				environment_choose_rb2.setText(Html.fromHtml(ev3));
				//				NewPos.BASE_URL = "sz.canxingjian.com/";
				break;
		}
	}

	@Override
	public void onClick(View v) {
		if (environment_choose_rg
				.getCheckedRadioButtonId() != currentId) {
			String baseurl = getResources().getStringArray(R.array.baseurl)[environment_choose_rg
					.getCheckedRadioButtonId() == R.id.environment_choose_rb1 ? 0 : 1];
			NewPos.BASE_URL = baseurl;
			SharedPreferencesUtil.saveBaseUrl(mcontext, baseurl);
			OnFinishEvent event = new OnFinishEvent();
			event.setMsg("changEnvironmentOk");
			EventBus.getDefault().post(event);
		}
		dismiss();
	}
}
