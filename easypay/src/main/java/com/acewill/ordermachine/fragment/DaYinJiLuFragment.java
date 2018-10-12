package com.acewill.ordermachine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.acewill.ordermachine.R;
import com.acewill.ordermachine.adapter.MyAdapter;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.model.DishModel;
import com.acewill.ordermachine.model.OrderReq;
import com.acewill.ordermachine.model.Payment;
import com.acewill.ordermachine.printadapter.PosKitchenPrintAdapter;
import com.acewill.ordermachine.util_new.DateUtils;
import com.acewill.ordermachine.util_new.TimeUtil;
import com.acewill.ordermachine.widget.CommonEditText;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author：Anch
 * Date：2017/11/24 12:44
 * Desc：
 */
public class DaYinJiLuFragment extends BaseFragment implements View.OnClickListener {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = View.inflate(inflater.getContext(), R.layout.fragment_dayinjilu, null);
		initView(view);
		initData();
		return view;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.search_btn:
				search();
				break;
		}
	}

	private String xiadanshijian;
	private String dayinji;
	private String dayinizhuangtai;

	private void search() {
		String         searchContnt = search_et.getText().toString();
		List<OrderReq> oList        = new ArrayList<>();
		List<OrderReq> orderList    = new ArrayList<>();
		if (xiadanshijian.equals("今天")) {
			orderList.addAll(todayOrders);
		} else {
			orderList.addAll(yesterdayOrders);
		}

		int sum = 0;
		for (OrderReq order : orderList) {
			boolean a;
			if (TextUtils.isEmpty(searchContnt))
				a = true;
			else
				a = !TextUtils.isEmpty(searchContnt) && (searchContnt
						.equals(order.getTerminalOrderId() + "") || searchContnt
						.equals(order.getOid() + ""));
			boolean b = dayinji.equals("全部") || dayinji.equals(order.getPrintName());
			boolean c = dayinizhuangtai
					.equals("全部") || dayinizhuangtai
					.equals(order.getPrintStatu() == 0 ? "未完成" : "已完成");
			if (order.getPrintStatu() == 0)
				sum++;
			if (a && b && c)
				oList.add(order);
		}
		adapter.setData(oList);
		notiActivity(sum);
		adapter.notifyDataSetChanged();

	}

	private void notiActivity(int sum) {
		OnFinishEvent event = new OnFinishEvent();
		event.setMsg("weiwancheng");
		event.setContent(sum + "");
		EventBus.getDefault().post(event);
	}

	private CommonEditText search_et;

	private void initView(View view) {
		listview = (ListView) view.findViewById(R.id.listview);
		mRadioGroup1 = (RadioGroup) view.findViewById(R.id.radioGroup1);
		mRadioGroup3 = (RadioGroup) view.findViewById(R.id.radioGroup3);
		//		gridView = (MyGridView) view.findViewById(R.id.mygridview);
		search_et = (CommonEditText) view.findViewById(R.id.search_et);
		view.findViewById(R.id.search_btn).setOnClickListener(this);
		mRadioGroup1.setOnCheckedChangeListener(new RadioButtonOnCheckedChangeListenerImpl());
		mRadioGroup3.setOnCheckedChangeListener(new RadioButtonOnCheckedChangeListenerImpl());
	}


	//	private class DaYinJiAdapter<PrintName> extends MyAdapter {
	//
	//
	//		public DaYinJiAdapter(Context mcontext, List dataList) {
	//			super(mcontext, dataList);
	//		}
	//
	//		@Override
	//		protected View getItemView(int position, View convertView, ViewGroup parent) {
	//			MyViewHolder holder;
	//			if (convertView == null) {
	//				holder = new MyViewHolder();
	//				convertView = View
	//						.inflate(mContext, R.layout.item_kaiban, null);
	//				holder.item_kaiban_tv = (TextView) convertView.findViewById(R.id.item_kaiban_tv);
	//				convertView.setTag(holder);
	//			} else {
	//				holder = (MyViewHolder) convertView.getTag();
	//			}
	//			com.acewill.ordermachine.model.PrintName printName = (com.acewill.ordermachine.model.PrintName) dataList
	//					.get(position);
	//
	//			holder.item_kaiban_tv.setText(printName.getName());
	//			if (printName.isSelect()) {
	//				holder.item_kaiban_tv.setTextColor(mContext.getResources()
	//						.getColor(R.color.color_white));
	//				holder.item_kaiban_tv.setBackground(mContext.getResources()
	//						.getDrawable(R.drawable.border_blue_normal));
	//			} else {
	//				holder.item_kaiban_tv
	//						.setTextColor(mContext.getResources()
	//								.getColor(R.color.color_666666));
	//				holder.item_kaiban_tv.setBackground(mContext.getResources()
	//						.getDrawable(R.drawable.border_black_dash_line));
	//			}
	//			return convertView;
	//		}
	//
	//		private class MyViewHolder {
	//			TextView item_kaiban_tv;
	//		}
	//
	//		public com.acewill.ordermachine.model.PrintName getSeleted() {
	//			for (Object bean2 : dataList) {
	//				com.acewill.ordermachine.model.PrintName bean3 = (com.acewill.ordermachine.model.PrintName) bean2;
	//				if (bean3.isSelect()) {
	//					return bean3;
	//				}
	//			}
	//			return null;
	//		}
	//
	//		public void setSelected(com.acewill.ordermachine.model.PrintName bean) {
	//			for (Object bean2 : dataList) {
	//				com.acewill.ordermachine.model.PrintName bean3 = (com.acewill.ordermachine.model.PrintName) bean2;
	//				if (bean3.getName().equals(bean.getName())) {
	//					bean3.setSelect(true);
	//				} else {
	//					bean3.setSelect(false);
	//				}
	//			}
	//			notifyDataSetChanged();
	//		}
	//
	//	}

	List<OrderReq> todayOrders     = new ArrayList<>();//今天的记录
	List<OrderReq> yesterdayOrders = new ArrayList<>();//昨天的记录

	// 监听单选的变化
	private void initData() {
		xiadanshijian = "今天";
		dayinji = "全部";
		dayinizhuangtai = "全部";
		List<OrderReq> orderReqList = DataSupport
				.where("createdat>?", DateUtils.getBeginDayOfYesterday().getTime() + "")
				.order("createdat desc")
				.find(OrderReq.class);//查出来所有的记录
		int sum = 0;
		for (int i = 0; i < orderReqList.size(); i++) {
			OrderReq req = orderReqList.get(i);
			List<DishModel> models = DataSupport
					.where("orderreq_id=?", req.getId() + "")
					.find(DishModel.class);
			List<Payment> payments = DataSupport
					.where("orderreq_id=?", req.getId() + "").find(Payment.class);
			req.setItemList(models);
			req.setPaymentList(payments);
		}
		for (OrderReq orderReq : orderReqList) {
			Date    date    = new Date(Long.parseLong(orderReq.getCreatedAt()));
			boolean isToday = TimeUtil.isToday(date);
			if (isToday) {
				todayOrders.add(orderReq);
				if (orderReq.getPrintStatu() == 0) {
					sum++;
				}
			} else {
				yesterdayOrders.add(orderReq);
			}
		}
		List<OrderReq> orList = new ArrayList<>();
		orList.addAll(todayOrders);
		notiActivity(sum);
		//		List<PrintName> printNameList = new ArrayList<>();
		//		List<String>    printStrNameList = new ArrayList<>();
		//		printNameList.add(new PrintName("全部", true));

		//		for (OrderReq orderReq : orderReqList) {
		//			if (!printStrNameList.contains(orderReq.getPrintName()))
		//		printStrNameList.add(orderReq.getPrintName());
		//		}
		//		for (String s : printStrNameList) {

		//		printNameList.add(new PrintName(Common.SHOP_PRINTER.getDescription(), false));
		//		}
		//		adapter1 = new DaYinJiAdapter<>(mContext, printNameList);
		//		gridView.setAdapter(adapter1);
		//		gridView.setOnItemClickListener(this);
		adapter = new DaYinJiLuAdapter<>(mContext, orList);
		listview.setAdapter(adapter);
	}

	private DaYinJiLuAdapter<OrderReq> adapter;
	//		private DaYinJiAdapter<PrintName>  adapter1;


	//	@Override
	//	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	//		adapter1.setSelected((PrintName) adapter1.getItem(position));
	//		dayinji = adapter1.getSeleted().getName();
	//	}

	private class RadioButtonOnCheckedChangeListenerImpl implements
			RadioGroup.OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			RadioButton rb = (RadioButton) group.findViewById(group
					.getCheckedRadioButtonId());
			String currentSelected = rb.getText().toString();
			switch (group.getId()) {
				case R.id.radioGroup1:
					xiadanshijian = currentSelected;
					break;
				case R.id.radioGroup3:
					dayinizhuangtai = currentSelected;
					break;
			}
		}
	}


	private ListView   listview;
	private RadioGroup mRadioGroup1;
	private RadioGroup mRadioGroup3;
	//	private MyGridView gridView;


	private class DaYinJiLuAdapter<OrderReq> extends MyAdapter {
		public DaYinJiLuAdapter(Context context, List dataList) {
			super(context, dataList);
		}

		@Override
		protected View getItemView(int position, View convertView, ViewGroup parent) {
			MyHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.item_dayinjilu, null);
				holder = new MyHolder();
				holder.dayinjilu_dayinibianhao = (TextView) convertView
						.findViewById(R.id.dayinjilu_dayinibianhao);
				holder.dayinjilu_dayinji = (TextView) convertView
						.findViewById(R.id.dayinjilu_dayinji);
				holder.dayinjilu_dayinzhuangtai = (TextView) convertView
						.findViewById(R.id.dayinjilu_dayinzhuangtai);
				holder.dayinjilu_dingdanhao = (TextView) convertView
						.findViewById(R.id.dayinjilu_dingdanhao);
				holder.dayinjilu_xiadanshijian = (TextView) convertView
						.findViewById(R.id.dayinjilu_xiadanshijian);
				holder.dayinjilu_dingdanjine = (TextView) convertView
						.findViewById(R.id.dayinjilu_dingdanjine);
				holder.dayinjilu_dingdanzhuangtai = (TextView) convertView
						.findViewById(R.id.dayinjilu_dingdanzhuangtai);
				holder.dayinjilu_zhifufangshi = (TextView) convertView
						.findViewById(R.id.dayinjilu_zhifufangshi);
				holder.dayinjilu_budaxiaopiao = (TextView) convertView
						.findViewById(R.id.dayinjilu_budaxiaopiao);
				convertView.setTag(holder);
			} else {
				holder = (MyHolder) convertView.getTag();
			}
			final com.acewill.ordermachine.model.OrderReq req = (com.acewill.ordermachine.model.OrderReq) dataList
					.get(position);
			holder.dayinjilu_dayinibianhao.setText(req.getTerminalOrderId() + "");
			holder.dayinjilu_dayinji.setText(req.getPrintName());
			if (req.getPrintStatu() == 0) {
				holder.dayinjilu_dayinzhuangtai.setText("未完成");
				holder.dayinjilu_dayinzhuangtai
						.setTextColor(mContext.getResources().getColor(R.color.color_red));
			} else {
				holder.dayinjilu_dayinzhuangtai.setText("已完成");
				holder.dayinjilu_dayinzhuangtai
						.setTextColor(mContext.getResources().getColor(R.color.color_666666));
			}
			holder.dayinjilu_dingdanhao.setText(req.getOid() + "");
			holder.dayinjilu_xiadanshijian
					.setText(format.format(new Date(Long.parseLong(req.getCreatedAt()))));
			holder.dayinjilu_dingdanjine.setText(req.getCost());
			if (req.getPaymentStatus().equals("PAYED")) {
				holder.dayinjilu_dingdanzhuangtai.setText("已支付");
			} else if (req.getPaymentStatus().equals("REFUND")) {
				holder.dayinjilu_dingdanzhuangtai.setText("已退款");
			}
			holder.dayinjilu_zhifufangshi.setText(req.getPaymentTypeName());
			holder.dayinjilu_budaxiaopiao.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//					Printer printer = new Printer();
					//					printer.setVendor(Common.SHOP_INFO.vendor);
					//					printer.setDescription(Common.SHOP_INFO.printerName);
					//					printer.setIp(Common.SHOP_INFO.printerIp);
					PosKitchenPrintAdapter.getInstance().print2(req);
				}
			});
			return convertView;
		}

		private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		private class MyHolder {
			TextView dayinjilu_dayinibianhao;
			TextView dayinjilu_dayinji;
			TextView dayinjilu_dayinzhuangtai;
			TextView dayinjilu_dingdanhao;
			TextView dayinjilu_xiadanshijian;
			TextView dayinjilu_dingdanjine;
			TextView dayinjilu_dingdanzhuangtai;
			TextView dayinjilu_zhifufangshi;
			TextView dayinjilu_budaxiaopiao;
		}
	}
}
