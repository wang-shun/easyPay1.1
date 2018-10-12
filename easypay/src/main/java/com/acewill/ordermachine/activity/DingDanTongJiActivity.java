package com.acewill.ordermachine.activity;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.acewill.chikenprintlibrary.model.PowerController;
import com.acewill.ordermachine.BaseApplication;
import com.acewill.ordermachine.R;
import com.acewill.ordermachine.adapter.DingDanItemAdapter;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.eventbus.OnCheckAthorityEvent;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.fragmentdialog.CommonEditDialog;
import com.acewill.ordermachine.fragmentdialog.TuiDanDialog;
import com.acewill.ordermachine.model.OrderReq;
import com.acewill.ordermachine.printadapter.PosKitchenPrintAdapter;
import com.acewill.ordermachine.util.PriceUtil;
import com.acewill.ordermachine.util.SharedPreferencesUtil;
import com.acewill.ordermachine.util_new.DateUtils;
import com.acewill.ordermachine.util_new.TimeUtil;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.ordermachine.widget.DropEditText;
import com.acewill.ordermachine.widget.TitileLayout;
import com.acewill.ordermachine.widget.WindowUtil;
import com.acewill.paylibrary.PayReqModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Author：Anch
 * Date：2017/11/20 14:49
 * Desc：
 */
public class DingDanTongJiActivity extends BaseActivity implements View.OnClickListener,
		AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {
	private static final String TAG = "DingDanTongJiActivity";
	private ListView                     mListView;
	private DingDanItemAdapter<OrderReq> adapter;
	private DropEditText                 member_query_et;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dingdantongji);
		initView();
		initTitleLayout();
		initData();
		initDialog();
	}

	public void openBox() {
		if (Common.SHOP_PRINTER != null && Common.SHOP_PRINTER.getVendor().equals("shangmi_fix"))
			BaseApplication.getInstance().openBox();//商米打印
		else
			PosKitchenPrintAdapter.getInstance()
					.openBox();//网口打印
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void changePayStatu(OnFinishEvent event) {
		if (event.getMsg().equals("refundSuccess")) {
			search();
			//打印退单小票
			if (SharedPreferencesUtil.getRefundPrint(mActivity)) {
				printRefundTicket(curReq);
				if (PayReqModel.PTID_CASHSTR.equals(curReq.getPaymentTypeName()) && Boolean
						.parseBoolean(event.getContent())) {
					openBox();
				}
			}
		} else if (event.getMsg().equals("checkAthourityOk")) {
			if (dialog != null) {
				dialog.dismiss();
				WindowUtil.hiddenKey();
			}
			//			curReq.getOid() + "", curReq.getCost(), curReq
			//					.getPaymentTypeName(), curReq.getMyid()
			showTuiKuanDialog(curReq);
		}
	}

	private void printRefundTicket(OrderReq req) {
		PosKitchenPrintAdapter.getInstance().print4(req);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onCheckAhority(OnCheckAthorityEvent event) {
		if (event.isStatu() && event.getType() == PowerController.REFUND_DISH) {
			if (dialog != null) {
				dialog.dismiss();
				WindowUtil.hiddenKey();
			}
			showTuiKuanDialog(curReq);
		}
	}


	private Spinner  zhuangtai_spi;
	private Spinner  zhifufangshi_spi;
	private Spinner  changyongshijian_spi;
	private TextView start_time;
	private TextView end_time;
	private TextView dingdan_dingdanshu_tv;
	private TextView dingdan_shishoujine_tv;
	private TextView dingdan_youhuijine_tv;
	private TextView dingdan_tuikuanjine_tv;
	private TextView dingdan_tuidanshuliang_tv;
	private List<OrderReq> orderRecorderList = new ArrayList<>();
	;

	private void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		getData();
		List<OrderReq> list = DataSupport
				.where("userphone=?", Common.SHOP_INFO.userphone)
				.order("createdat desc")
				.find(OrderReq.class);
		Iterator<OrderReq> iterator = list.iterator();
		while (iterator.hasNext()) {
			OrderReq next = iterator.next();
			//获取当前月最后一天
			Calendar ca = Calendar.getInstance();
			ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
//			if (TimeUtil.isSameDate(ca.getTime(), new Date()) && next.getCreatedtime()) {
//				//如果今天是最后一天，并且该订单是上一个月的，那么就将该条数据删除
//
//			}
		}


		for (OrderReq req : list) {


			if (TimeUtil.isSameDate(new Date(req.getCreatedtime()), new Date())) {
				orderRecorderList.add(req);
			}
		}
		//		String sum = DataSupport.where("userphone = ?", Common.SHOP_INFO.userphone)
		//				.sum(OrderReq.class, "cost", float.class) + "";

		adapter = new DingDanItemAdapter<>(this, orderRecorderList, getSupportFragmentManager());
		mListView.setAdapter(adapter);
		start_time = (TextView) findViewById(R.id.start_time);
		end_time = (TextView) findViewById(R.id.end_time);
		zhuangtai_spi = (Spinner) findViewById(R.id.zhuangtai_spi);
		zhifufangshi_spi = (Spinner) findViewById(R.id.zhifufangshi_spi);
		changyongshijian_spi = (Spinner) findViewById(R.id.changyongshijian_spi);
		dingdan_dingdanshu_tv = (TextView) findViewById(R.id.dingdan_dingdanshu_tv);
		dingdan_shishoujine_tv = (TextView) findViewById(R.id.dingdan_shishoujine_tv);
		dingdan_youhuijine_tv = (TextView) findViewById(R.id.dingdan_youhuijine_tv);
		dingdan_tuikuanjine_tv = (TextView) findViewById(R.id.dingdan_tuikuanjine_tv);
		dingdan_tuidanshuliang_tv = (TextView) findViewById(R.id.dingdan_tuidanshuliang_tv);
		member_query_et = (DropEditText) findViewById(R.id.member_query_et);
		zhuangtai_spi.setOnItemSelectedListener(this);
		zhifufangshi_spi.setOnItemSelectedListener(this);
		changyongshijian_spi.setOnItemSelectedListener(this);
		start_time.setOnClickListener(this);
		end_time.setOnClickListener(this);
		findViewById(R.id.search_btn).setOnClickListener(this);
		if (orderRecorderList != null)
			refreshUI(orderRecorderList);

		//		test();
		initEvent();
	}

	private void getData() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		//获取前月的第一天
		Calendar cal_1 = Calendar.getInstance();//获取当前日期
		cal_1.add(Calendar.MONTH, -1);
		cal_1.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
		String firstDay = format.format(cal_1.getTime());
		System.out.println("-----1------firstDay:" + firstDay);
		//获取前月的最后一天
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH, 0);//设置为1号,当前日期既为本月第一天
		String lastDay = format.format(cale.getTime());
		System.out.println("-----2------lastDay:" + lastDay);


		//获取当前月第一天：
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);//设置为1号,当前日期既为本月第一天
		String first = format.format(c.getTime());
		System.out.println("===============first:" + first);

		//获取当前月最后一天
		Calendar ca = Calendar.getInstance();
		ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
		String last = format.format(ca.getTime());
		System.out.println("===============last:" + last);
	}

	private void initEvent() {
		mListView.setOnItemClickListener(this);
	}

	SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");


	private void refreshUI(List<OrderReq> list) {
		BigDecimal total     = new BigDecimal("0");//有效实收金额
		BigDecimal refundSum = new BigDecimal("0");//退单并且退了钱的金额
		int        tuidansum = 0;//退单的单数、
		int        effectSum = 0;//有效单数
		for (OrderReq req : list) {
			if ("REFUND"//如果是退款，那么不包含不退钱的部分
					.equals(req.getPaymentStatus())) {
				tuidansum++;
				if (req.getIsRefundMoney() == 0) {
					refundSum = PriceUtil.add(refundSum, new BigDecimal(req.getCost()));
				} else {
					//这个是退单不退款的处理
					total = PriceUtil.add(total, new BigDecimal(req.getCost()));
				}
			} else {
				effectSum++;
				//不是退单
				total = PriceUtil.add(total, new BigDecimal(req.getCost()));
			}
		}
		String tuidansumStr = "";
		if (refundSum.floatValue() != 0) {
			tuidansumStr = "<font color=\"green\">-" + refundSum.toString() + "</font>";
		} else {
			tuidansumStr = "0.00";
		}
		dingdan_dingdanshu_tv.setText(effectSum + "");
		dingdan_shishoujine_tv.setText(total.toString());
		dingdan_tuikuanjine_tv.setText(Html.fromHtml(tuidansumStr));
		dingdan_tuidanshuliang_tv.setText(tuidansum + "");
	}


	private void initData() {
		final Calendar ca = Calendar.getInstance();
		mYear = ca.get(Calendar.YEAR);
		mMonth = ca.get(Calendar.MONTH);
		mDay = ca.get(Calendar.DAY_OF_MONTH);
		zhuangtaiArr = getResources().getStringArray(R.array.dingdanzhuangtai);
		zhifufangshiArr = getResources().getStringArray(R.array.zhifufangshi);
		changyongshijianArr = getResources().getStringArray(R.array.changyongshijian);
		zhungtai = "";
		zhifufangshi = "";

	}

	private String[] zhuangtaiArr;
	private String[] zhifufangshiArr;
	private String[] changyongshijianArr;
	private int      mYear;
	private int      mMonth;
	private int      mDay;


	private void initTitleLayout() {
		TitileLayout layout = (TitileLayout) findViewById(R.id.title_ly);
		layout.setOnBackClickLisener(new TitileLayout.OnBackClickLisener() {
			@Override
			public void onBackClick() {
				finish();
			}
		});
	}

	private DatePickerDialog dialog1;
	private DatePickerDialog dialog2;

	private void initDialog() {
		dialog1 = new DatePickerDialog(this, mdateListener1, mYear, mMonth, mDay);
		dialog2 = new DatePickerDialog(this, mdateListener2, mYear, mMonth, mDay);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.start_time:
				dialog1.show();
				break;
			case R.id.end_time:
				dialog2.show();
				break;
			case R.id.search_btn:
				search();
				break;
		}
	}

	private String zhungtai;
	private String zhifufangshi;

	/**
	 * 设置日期 利用StringBuffer追加
	 */
	public void display1() {
		start_time
				.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-")
						.append(mDay).append(" ").append(" 00-00-00"));
	}

	/**
	 * 设置日期 利用StringBuffer追加
	 */
	public void display2() {
		end_time
				.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-")
						.append(mDay).append(" ").append(" 23-59-59"));
	}

	private DatePickerDialog.OnDateSetListener mdateListener1 = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
		                      int dayOfMonth) {

			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			display1();
		}
	};
	private DatePickerDialog.OnDateSetListener mdateListener2 = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
		                      int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			display2();
		}
	};

	/*

	 */
	public void search() {
		try {
			String startTime = start_time.getText().toString();
			String endTime   = end_time.getText().toString();
			Date   edate     = format3.parse(endTime);
			long   etime     = edate.getTime();
			Date   sdate     = format3.parse(startTime);
			long   stime     = sdate.getTime();
			if (stime > etime) {
				ToastUtils.showToast(this, "开始时间不得小于结束时间");
				return;
			}
			Cursor cursor;
			//			if (!TextUtils
			//					.isEmpty(member_query_et.getText())) {
			//				cursor = DataSupport
			//						.findBySQL("select * from orderreq where userphone=? and createdat>? and createdat<? and memberphonenumber=? order by createdat desc", Common.SHOP_INFO.userphone, String
			//								.valueOf(stime), String.valueOf(etime), member_query_et.getText()
			//								.toString().trim());
			//			} else
			if (zhungtai.equals("ALL") && zhifufangshi.equals("ALL")) {
				//订单状态为全部 ，支付方式为全部
				cursor = DataSupport
						.findBySQL("select * from orderreq where userphone=? and createdat>? and createdat<?  order by createdat desc", Common.SHOP_INFO.userphone, String
								.valueOf(stime), String.valueOf(etime));
			} else if (zhungtai.equals("ALL")) {
				cursor = DataSupport
						.findBySQL("select * from orderreq where  userphone=? and createdat>? and createdat<? and paymenttypename = ? order by createdat desc", Common.SHOP_INFO.userphone, String
								.valueOf(stime), String.valueOf(etime), zhifufangshi);
			} else if (zhifufangshi.equals("ALL")) {
				//订单状态不为全部 ，支付方式不为全部
				cursor = DataSupport
						.findBySQL("select * from orderreq where  userphone=? and createdat>? and createdat<?  and paymentstatus = ? order by createdat desc", Common.SHOP_INFO.userphone, String
								.valueOf(stime), String.valueOf(etime), zhungtai);
			} else {
				//订单状态不为全部 ，支付方式不为全部
				cursor = DataSupport
						.findBySQL("select * from orderreq where  userphone=? and createdat>? and createdat<? and paymenttypename = ? and paymentstatus = ? order by createdat desc", Common.SHOP_INFO.userphone,
								String
										.valueOf(stime), String
										.valueOf(etime), zhifufangshi, zhungtai);
			}

			String text = member_query_et.getText().toString().trim();


			if (cursor != null) {
				List<OrderReq> orderReqList = new ArrayList<>();
				while (cursor.moveToNext()) {
					OrderReq req = new OrderReq();
					String memberPhoneNumber = cursor
							.getString(cursor.getColumnIndex("memberphonenumber"));
					String uActualNo = cursor
							.getString(cursor.getColumnIndex("uactualno"));
					String bis_id = cursor.getString(cursor.getColumnIndex("bis_id"));
					String paymenttypename = cursor
							.getString(cursor.getColumnIndex("paymenttypename"));
					String paymentstatus = cursor
							.getString(cursor.getColumnIndex("paymentstatus"));
					String cost       = cursor.getString(cursor.getColumnIndex("cost"));
					String createdat  = cursor.getString(cursor.getColumnIndex("createdat"));
					int    oid        = cursor.getInt(cursor.getColumnIndex("oid"));
					int    myid       = cursor.getInt(cursor.getColumnIndex("id"));
					long   refundtime = cursor.getLong(cursor.getColumnIndex("refundtime"));
					String refundmoney = cursor
							.getString(cursor.getColumnIndex("refundmoney"));
					int isrefundmoney = cursor
							.getInt(cursor.getColumnIndex("isrefundmoney"));
					String realname = cursor.getString(cursor.getColumnIndex("realname"));
					String memberName = cursor
							.getString(cursor.getColumnIndex("membername"));

					int terminalOrderId = cursor
							.getInt(cursor.getColumnIndex("terminalorderid"));
					req.setBis_id(bis_id);
					req.setPaymentTypeName(paymenttypename);
					req.setPaymentStatus(paymentstatus);
					req.setCost(cost);
					req.setCreatedAt(createdat);
					req.setOid(oid);
					req.setMyid(myid);
					req.setRefundTime(refundtime);
					req.setRefundMoney(refundmoney);
					req.setIsRefundMoney(isrefundmoney);
					req.setTerminalOrderId(terminalOrderId);
					req.setRealname(realname);
					req.setMemberName(memberName);
					req.setMemberPhoneNumber(memberPhoneNumber);
					try {
						if (!TextUtils.isEmpty(text)) {
							if (String.valueOf(text)
									.equals(String.valueOf(memberPhoneNumber)) || String
									.valueOf(text).equals(String.valueOf(uActualNo)) || String
									.valueOf(text)
									.equals(String.valueOf(oid)) || String.valueOf(text)
									.equals(String
											.valueOf(bis_id)) || String.valueOf(text).equals(String
									.valueOf(terminalOrderId)) || String
									.valueOf(PriceUtil.formatPrice(text))
									.equals(String
											.valueOf(req.getCost()))) {
								orderReqList.add(req);
							} else {
								continue;
							}
						} else
							orderReqList.add(req);
					} catch (NumberFormatException e) {
						e.printStackTrace();
						ToastUtils.showToast(DingDanTongJiActivity.this, "输入的格式有误,请输入数字进行查询!");
					}

				}
				orderRecorderList.clear();
				orderRecorderList.addAll(orderReqList);
				adapter.setData(orderReqList);
				adapter.notifyDataSetChanged();
				if (orderReqList != null)
					refreshUI(orderReqList);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
			case R.id.zhuangtai_spi:
				zhungtai = zhuangtaiArr[position];
				if ("已支付".equals(zhungtai)) {
					zhungtai = "PAYED";
				} else if ("全部".equals(zhungtai)) {
					zhungtai = "ALL";
				} else if ("已退单".equals(zhungtai)) {
					zhungtai = "REFUND";
				}
				break;
			case R.id.zhifufangshi_spi:
				zhifufangshi = zhifufangshiArr[position];
				if ("全部".equals(zhifufangshi)) {
					zhifufangshi = "ALL";
				}
				break;
			case R.id.changyongshijian_spi:
				if ("今天".equals(changyongshijianArr[position])) {
					start_time.setText(format3.format(DateUtils.getDayBegin()));
					end_time.setText(format3.format(DateUtils.getDayEnd()));
				} else if ("昨天".equals(changyongshijianArr[position])) {
					start_time.setText(format3.format(DateUtils.getBeginDayOfYesterday()));
					end_time.setText(format3.format(DateUtils.getEndDayOfYesterDay()));
				} else if ("前天".equals(changyongshijianArr[position])) {
					start_time.setText(format3.format(DateUtils.getBeginTheDayBeforeYesterday()));
					end_time.setText(format3.format(DateUtils.getEndTheDayBeforeYesterday()));
				} else if ("本周".equals(changyongshijianArr[position])) {
					start_time.setText(format3.format(DateUtils.getBeginDayOfWeek()));
					end_time.setText(format3.format(DateUtils.getEndDayOfWeek()));
				} else if ("本月".equals(changyongshijianArr[position])) {
					start_time.setText(format3.format(DateUtils.getBeginDayOfMonth()));
					end_time.setText(format3.format(DateUtils.getEndDayOfMonth()));
				}
				break;

		}
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private CommonEditDialog dialog;

	private void showShouQuanDialog() {
		dialog = CommonEditDialog.newInstance(PowerController.REFUND_DISH);
		dialog.show(getSupportFragmentManager(), "CommonEditDialog");
	}

	private void showTuiKuanDialog(OrderReq curReq) {
		TuiDanDialog dialog = TuiDanDialog.newInstance(curReq);
		dialog.show(getSupportFragmentManager(), "TuiDanDialog");
	}

	private OrderReq curReq;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		curReq = orderRecorderList.get(position);
		boolean moreThanOneDay = false;
		try {
			moreThanOneDay = TimeUtil
					.moreThanOneDay(new Date(Long.parseLong(curReq.getCreatedAt())), new Date());
			if (moreThanOneDay || !curReq.getPaymentStatus()
					.equals("PAYED")) {//大于一天或者不为已支付状态不能退款
				return;
			}
			if (SharedPreferencesUtil.getRefundAthuority(mActivity)) {
				showTuiKuanDialog(curReq);
			} else {
				//授权退款
				if (SharedPreferencesUtil.getQuanXian(mActivity))
					showShouQuanDialog();
				else
					showToast("您没有退款的权限!");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
}
