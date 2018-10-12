package com.acewill.ordermachine.fragmentdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.adapter.MyAdapter;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.dialog.BaseDialog;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.model.WorkShift;
import com.acewill.ordermachine.model.WorkShiftInfo;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.util.PriceUtil;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.ordermachine.widget.DropEditText;
import com.acewill.ordermachine.widget.MyGridView;
import com.acewill.ordermachine.widget.WindowUtil;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

/**
 * Author：Anch
 * Date：2017/11/21 11:37
 * Desc：
 */
public class KaiBanDialog extends BaseDialog implements View.OnClickListener,
		AdapterView.OnItemClickListener {
	private MyGridView    gridView;
	private KaiBanAdapter adapter;
	private DropEditText  qianxiangjine_et;

	/**
	 * @return
	 */
	public static KaiBanDialog newInstance() {
		KaiBanDialog fragment = new KaiBanDialog();
		Bundle       bundle   = new Bundle();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View getView() {
		Dialog dialog = getDialog();
		View   view   = View.inflate(mcontext, R.layout.fragment_kaiban_dialog, null);
		if (dialog != null) {
			getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
			//若无该段代码则用系统的样式而不是自己定义的背景样式
			getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		}
		gridView = (MyGridView) view.findViewById(R.id.mygridview);
		qianxiangjine_et = (DropEditText) view.findViewById(R.id.qianxiangjine_et);
		view.findViewById(R.id.kaiban_btn).setOnClickListener(this);
		adapter = new KaiBanAdapter(mcontext, Common.workShifIno);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		dialog.setCanceledOnTouchOutside(true);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		adapter.setSelected((WorkShiftInfo.ContentBean) adapter.getItem(position));
	}

	private class KaiBanAdapter extends MyAdapter {


		public KaiBanAdapter(Context mcontext, List dataList) {
			super(mcontext, dataList);
		}

		@Override
		protected View getItemView(int position, View convertView, ViewGroup parent) {
			MyViewHolder holder;
			if (convertView == null) {
				holder = new MyViewHolder();
				convertView = View
						.inflate(mContext, R.layout.item_kaiban, null);
				holder.item_kaiban_tv = (TextView) convertView.findViewById(R.id.item_kaiban_tv);
				convertView.setTag(holder);
			} else {
				holder = (MyViewHolder) convertView.getTag();
			}
			WorkShiftInfo.ContentBean bean = (WorkShiftInfo.ContentBean) dataList.get(position);
			holder.item_kaiban_tv.setText(bean.getName());
			if (bean.isSelect()) {
				holder.item_kaiban_tv
						.setTextColor(mcontext.getResources().getColor(R.color.color_white));
				holder.item_kaiban_tv.setBackground(mcontext.getResources()
						.getDrawable(R.drawable.border_blue_normal));
			} else {
				holder.item_kaiban_tv
						.setTextColor(mcontext.getResources().getColor(R.color.color_666666));
				holder.item_kaiban_tv.setBackground(mcontext.getResources()
						.getDrawable(R.drawable.border_black_dash_line));
			}
			return convertView;
		}

		private class MyViewHolder {
			TextView item_kaiban_tv;
		}

		public WorkShiftInfo.ContentBean getSeleted() {
			for (Object bean2 : dataList) {
				WorkShiftInfo.ContentBean bean3 = (WorkShiftInfo.ContentBean) bean2;
				if (bean3.isSelect()) {
					return bean3;
				}
			}
			return null;
		}

		public void setSelected(WorkShiftInfo.ContentBean bean) {
			for (Object bean2 : dataList) {
				WorkShiftInfo.ContentBean bean3 = (WorkShiftInfo.ContentBean) bean2;
				if (bean3.getId() == bean.getId()) {
					bean3.setSelect(true);
				} else {
					bean3.setSelect(false);
				}
			}
			notifyDataSetChanged();
		}

	}

	@Override
	public float getSize() {
		return 0.5f;
	}

	@Override
	public void onClick(View v) {
		String qianxiangjine = qianxiangjine_et.getText().toString().trim();
		if (TextUtils.isEmpty(qianxiangjine)) {
			ToastUtils.showToast(mcontext, "开班金额不能为空!");
			return;
		}
		//开班
		final WorkShift bean = new WorkShift();
		if (adapter.getSeleted() == null) {
			ToastUtils.showToast(mcontext, "请选择班次!");
			return;
		}
		dismiss();
		WindowUtil.hiddenKey();
		bean.setUserName(Common.SHOP_INFO.userphone);
		bean.setUserId(1);
		bean.setSpareCash(PriceUtil.formatPrice(new BigDecimal(qianxiangjine)));//备用金
		bean.setDefinitionId(adapter.getSeleted().getId());
		bean.setDefinitionName(adapter.getSeleted().getName());
		bean.setTerminalId(Integer.parseInt(Common.SHOP_INFO.terminalid));
		bean.setStartTime(new Date().getTime());
		bean.setId(adapter.getSeleted().getId());
		bean.setCreatedOffline(false);
		bean.setRealName(Common.SHOP_INFO.realname);
		new NewPos().getInstance(mcontext)
				.startWorkShift(new Gson().toJson(bean), new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						e.printStackTrace();
					}

					@Override
					public void onResponse(String response, int id) {
						PrintFile
								.printResult2("Class_KaiBanDialog_method_onClick_onResponse_" + response + "\n");
						try {
							if (0 == GsonUtils.getIntData("result", response)) {
								String content = GsonUtils.getData("content", response);
								bean.setStartTimeStr(GsonUtils
										.getData("startTimeStr", content));//设置开班时间
								bean.setId(GsonUtils.getIntData("id", content));//设置该班次的id
								Common.workShifit = bean;
								OnFinishEvent event = new OnFinishEvent();
								event.setMsg("kaibansuccess");
								event.setContent(bean.getDefinitionName());
								EventBus.getDefault().post(event);
							} else {
								ToastUtils.showToast(mcontext, "开班失败!");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}
}
