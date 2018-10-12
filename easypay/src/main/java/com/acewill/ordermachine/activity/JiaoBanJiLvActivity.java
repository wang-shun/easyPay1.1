package com.acewill.ordermachine.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.acewill.chikenprintlibrary.model.WorkShiftNewReport;
import com.acewill.ordermachine.R;
import com.acewill.ordermachine.adapter.MyAdapter;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.model.WorkShift;
import com.acewill.ordermachine.model.WorkShiftDetail;
import com.acewill.ordermachine.model.WorkShiftRes;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.printadapter.PosKitchenPrintAdapter;
import com.acewill.ordermachine.util_new.DateUtils;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.ordermachine.widget.TitileLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;

/**
 * Author：Anch
 * Date：2017/11/23 9:52
 * Desc：
 */
public class JiaoBanJiLvActivity extends BaseActivity implements View.OnClickListener,
		AdapterView.OnItemSelectedListener {
	private ListView mListView;
	private TextView start_time;
	private TextView end_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_jiaobanjilv);
		initTitleLayout();
		initView();
		initData();
		initDialog();
	}

	private Spinner  changyongshijian_spi;
	private Spinner  jiaoban_banci_spi;
	private Spinner  jiaoban_dangbanren_spi;
	private Spinner  jiaoban_zhuangtai_spi;
	private TextView tips;

	private void initView() {
		mListView = (ListView) findViewById(R.id.listview);
		start_time = (TextView) findViewById(R.id.start_time);
		end_time = (TextView) findViewById(R.id.end_time);
		tips = (TextView) findViewById(R.id.tips);
		String html = "<font color=\"red\">" + "*" + "</font> 提示:查询跨度不能超过<font color=\"red\">" + "31" + "</font>天";
		tips.setText(Html.fromHtml(html));

		changyongshijian_spi = (Spinner) findViewById(R.id.changyongshijian_spi);
		changyongshijian_spi.setOnItemSelectedListener(this);
		jiaoban_banci_spi = (Spinner) findViewById(R.id.jiaoban_banci_spi);
		jiaoban_banci_spi.setOnItemSelectedListener(this);
		jiaoban_dangbanren_spi = (Spinner) findViewById(R.id.jiaoban_dangbanren_spi);
		jiaoban_dangbanren_spi.setOnItemSelectedListener(this);
		jiaoban_zhuangtai_spi = (Spinner) findViewById(R.id.jiaoban_zhuangtai_spi);
		jiaoban_zhuangtai_spi.setOnItemSelectedListener(this);
		findViewById(R.id.search_btn).setOnClickListener(this);
		start_time.setOnClickListener(this);
		end_time.setOnClickListener(this);
	}

	private JiaoBanJiLuItemAdapter<WorkShift> adapter;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private List<String> banciArr;
	private List<String> dangbanRenArr;
	private String[]     zhuangtaiArr;

	private void initData() {
		final Calendar ca = Calendar.getInstance();
		mYear = ca.get(Calendar.YEAR);
		mMonth = ca.get(Calendar.MONTH);
		mDay = ca.get(Calendar.DAY_OF_MONTH);
		changyongshijianArr = getResources().getStringArray(R.array.changyongshijian);
		zhuangtaiArr = getResources().getStringArray(R.array.jiaojiebanzhuangtai);
		banciArr = new ArrayList<>();
		dangbanRenArr = new ArrayList<>();
		banciArr.add("全部");
		dangbanRenArr.add("全部");
		//从后台去数据 ，需要穿一个开始时间， 一个结束时间  ，默认是当天的 开始时间和当前的结束时间
		currentStime = format.format(DateUtils.getDayBegin()
				.getTime());
		currentEtime = format
				.format(DateUtils.getDayEnd().getTime());
		getData(currentStime, currentEtime);

	}

	private String currentStime;
	private String currentEtime;
	private List<WorkShift> workShiftList = new ArrayList<>();

	private void searchData(String banci, String dangbanren, String zhungtai) {
		//这个是相同时间的查询，那么就根据其它三项查询
		List<WorkShift> workShifts = new ArrayList<WorkShift>();
		for (WorkShift workShift : workShiftList) {
			if (workShift.getEndTimeStr() == null)
				workShift.setStatuStr("未交班");
			else
				workShift.setStatuStr("已交班");
			if ((banci
					.equals("全部") || banci.equals(workShift.getDefinitionName())) && (dangbanren
					.equals("全部") || dangbanren
					.equals(workShift.getRealName())) && (zhungtai.equals("全部") || zhungtai
					.equals(workShift.getStatuStr()))) {
				workShifts.add(workShift);
			}
		}
		adapter.setData(workShifts);
		adapter.notifyDataSetChanged();
	}

	private void getData(String sTime, String eTime) {
		showLoadingDialog();
		new NewPos().getInstance(this).getWorkShiftStore(sTime, eTime, new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				dismissLoadingDialog();
				showToast(Common.errMsg);
				e.printStackTrace();
			}

			@Override
			public void onResponse(String response, int id) {
				PrintFile
						.printResult2("Class_JiaoBanJiLvActivity_method_getData_onResponse>>" + response + "\n");
				dismissLoadingDialog();
				try {
					WorkShiftRes bean = GsonUtils
							.getSingleBean(response, WorkShiftRes.class);
					List<WorkShift> workShifts = bean.getContent();

					workShiftList.clear();
					workShiftList.addAll(workShifts);
					banciArr.clear();
					banciArr.add("全部");
					dangbanRenArr.clear();
					dangbanRenArr.add("全部");
					for (WorkShift workShift : workShifts) {
						if (!banciArr.contains(workShift.getDefinitionName())) {
							banciArr.add(workShift.getDefinitionName());
						}
						if (!dangbanRenArr.contains(workShift.getRealName())) {
							dangbanRenArr.add(workShift.getRealName());
						}
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<>(JiaoBanJiLvActivity.this, R.layout.item_text, banciArr);
					jiaoban_banci_spi.setAdapter(adapter);
					ArrayAdapter<String> adapter2 = new ArrayAdapter<>(JiaoBanJiLvActivity.this, R.layout.item_text, dangbanRenArr);
					jiaoban_dangbanren_spi.setAdapter(adapter2);
					if (JiaoBanJiLvActivity.this.adapter == null) {
						JiaoBanJiLvActivity.this.adapter = new JiaoBanJiLuItemAdapter(JiaoBanJiLvActivity.this, workShifts);
						mListView.setAdapter(JiaoBanJiLvActivity.this.adapter);
					} else {
						JiaoBanJiLvActivity.this.adapter.setData(workShifts);
						JiaoBanJiLvActivity.this.adapter.notifyDataSetChanged();
					}
				} catch (Exception e) {
					dismissLoadingDialog();
					e.printStackTrace();
				}
			}
		});
	}


	private void initTitleLayout() {
		TitileLayout layout = (TitileLayout) findViewById(R.id.title_ly);
		layout.setOnBackClickLisener(new TitileLayout.OnBackClickLisener() {
			@Override
			public void onBackClick() {
				finish();
			}
		});
	}

	private String[] changyongshijianArr;

	private String banci;
	private String dangbanren;
	private String zhungtai;

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {

			case R.id.jiaoban_banci_spi:
				banci = banciArr.get(position);
				break;
			case R.id.jiaoban_dangbanren_spi:
				dangbanren = dangbanRenArr.get(position);
				break;
			case R.id.jiaoban_zhuangtai_spi:
				zhungtai = zhuangtaiArr[position];
				break;
			case R.id.changyongshijian_spi:
				if ("今天".equals(changyongshijianArr[position])) {
					start_time.setText(format.format(DateUtils.getDayBegin()));
					end_time.setText(format.format(DateUtils.getDayEnd()));
				} else if ("昨天".equals(changyongshijianArr[position])) {
					start_time.setText(format.format(DateUtils.getBeginDayOfYesterday()));
					end_time.setText(format.format(DateUtils.getEndDayOfYesterDay()));
				} else if ("前天".equals(changyongshijianArr[position])) {
					start_time.setText(format.format(DateUtils.getBeginTheDayBeforeYesterday()));
					end_time.setText(format.format(DateUtils.getEndTheDayBeforeYesterday()));
				} else if ("本周".equals(changyongshijianArr[position])) {
					start_time.setText(format.format(DateUtils.getBeginDayOfWeek()));
					end_time.setText(format.format(DateUtils.getEndDayOfWeek()));
				} else if ("本月".equals(changyongshijianArr[position])) {
					start_time.setText(format.format(DateUtils.getBeginDayOfMonth()));
					end_time.setText(format.format(DateUtils.getEndDayOfMonth()));
				}
				break;

		}
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}


	private class JiaoBanJiLuItemAdapter<T> extends MyAdapter {

		public JiaoBanJiLuItemAdapter(Context context, List dataList) {
			super(context, dataList);
		}

		@Override
		protected View getItemView(int position, View convertView, ViewGroup parent) {
			MyViewHodler hodler;
			if (convertView == null) {
				hodler = new MyViewHodler();
				convertView = View.inflate(mContext, R.layout.item_jiaobanjilu, null);
				hodler.workshift_start_time = (TextView) convertView
						.findViewById(R.id.workshift_start_time);
				hodler.workshift_end_time = (TextView) convertView
						.findViewById(R.id.workshift_end_time);
				hodler.workshift_banci_tv = (TextView) convertView
						.findViewById(R.id.workshift_banci_tv);
				hodler.workshift_realname_tv = (TextView) convertView
						.findViewById(R.id.workshift_realname_tv);
				hodler.workshift_statu = (TextView) convertView.findViewById(R.id.workshift_statu);
				hodler.workshift_shishou = (TextView) convertView
						.findViewById(R.id.workshift_shishou);
				hodler.workshift_kaibanmoney = (TextView) convertView
						.findViewById(R.id.workshift_kaibanmoney);
				hodler.workshift_jiaobanmoney = (TextView) convertView
						.findViewById(R.id.workshift_jiaobanmoney);
				hodler.workshift_yingjiaomoney = (TextView) convertView
						.findViewById(R.id.workshift_yingjiaomoney);
				hodler.workshift_xianjinchae = (TextView) convertView
						.findViewById(R.id.workshift_xianjinchae);
				hodler.workshift_budajiaojieban = (TextView) convertView
						.findViewById(R.id.workshift_budajiaojieban);
				convertView.setTag(hodler);
			} else {
				hodler = (MyViewHodler) convertView.getTag();
			}
			final WorkShift workshift = (WorkShift) dataList.get(position);
			hodler.workshift_start_time.setText(workshift.getStartTimeStr());
			hodler.workshift_end_time.setText(workshift.getEndTimeStr());
			hodler.workshift_banci_tv.setText(workshift.getDefinitionName());
			hodler.workshift_realname_tv.setText(workshift.getRealName());

			hodler.workshift_budajiaojieban.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (Common.isPrintOk) {
						doPrintJiaoBanTicket(workshift);
					} else {
						showErrorDialog("打印机连接异常，请检查打印机!", false);
					}
				}
			});

			hodler.workshift_statu
					.setText(!TextUtils.isEmpty(workshift.getStartTimeStr()) && !TextUtils
							.isEmpty(workshift.getEndTimeStr()) ? "已交班" : "未交班");
			hodler.workshift_budajiaojieban
					.setVisibility(!TextUtils.isEmpty(workshift.getStartTimeStr()) && !TextUtils
							.isEmpty(workshift.getEndTimeStr()) ? View.VISIBLE : View.GONE);
			try {
				String detail = workshift.getWorkShiftDetail();
				if (!TextUtils.isEmpty(detail)) {
					WorkShiftDetail bean = GsonUtils.getSingleBean(detail, WorkShiftDetail.class);

					List<WorkShiftDetail.WorkShiftCategoryDataListBean> list = bean
							.getWorkShiftCategoryDataList();
					List<WorkShiftDetail.WorkShiftCategoryDataListBean.WorkShiftItem> datas = null;
					for (WorkShiftDetail.WorkShiftCategoryDataListBean bean2 : list) {
						if (bean2.getName().equals("实收")) {
							datas = bean2
									.getWorkShiftItemDatas();
						}
					}
					float total = 0;
					for (WorkShiftDetail.WorkShiftCategoryDataListBean.WorkShiftItem bean3 : datas) {
						if (bean3.getName().equals("总计")) {
							total = bean3.getTotal();
						}
					}
					bean.getSubmitCash();
					bean.getDifferenceCash();
					hodler.workshift_shishou.setText(total + "");
					hodler.workshift_yingjiaomoney.setText(bean.getSubmitCash() + "");
					hodler.workshift_xianjinchae.setText(bean.getDifferenceCash() + "");
				} else {
					hodler.workshift_shishou.setText("");
					hodler.workshift_yingjiaomoney.setText("");
					hodler.workshift_xianjinchae.setText("");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			hodler.workshift_kaibanmoney.setText(workshift.getSpareCash());
			hodler.workshift_jiaobanmoney.setText(workshift.getCashRevenue());
			return convertView;
		}

		private void doPrintJiaoBanTicket(WorkShift workShift) {

			new NewPos().getInstance(mContext)
					.workShiftReport(workShift.getId() + "", workShift
							.getCashRevenue(), new StringCallback() {
						@Override
						public void onError(Call call, Exception e, int id) {
							e.printStackTrace();
							showErrorDialog(e.getMessage(), false);
						}

						@Override
						public void onResponse(String response, int id) {
							Log.e("JiaoBanDialog", "response>>" + response);
							PrintFile
									.printResult2("Class_JiaoBanJiLvActivity_Method_doPrintJiaoBanTicket()_onResponse>" + response + "\n");
							try {
								WorkShiftNewReport result = GsonUtils
										.getSingleBean(GsonUtils
												.getData("content", response), WorkShiftNewReport.class);
								PosKitchenPrintAdapter.getInstance()
										.printWorkShift(result);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		}


		private class MyViewHodler {

			public TextView workshift_start_time;
			public TextView workshift_end_time;
			public TextView workshift_banci_tv;
			public TextView workshift_realname_tv;
			public TextView workshift_statu;
			public TextView workshift_shishou;
			public TextView workshift_kaibanmoney;
			public TextView workshift_jiaobanmoney;
			public TextView workshift_yingjiaomoney;
			public TextView workshift_xianjinchae;
			public TextView workshift_budajiaojieban;
		}
	}

	private DatePickerDialog dialog1;
	private DatePickerDialog dialog2;

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
				String stime = start_time.getText().toString();
				String etime = end_time.getText().toString();
				try {
					if (format.parse(etime).getTime() < format.parse(stime).getTime()) {
						ToastUtils.showToast(mActivity, "结束时间不能小于开始时间!");
						return;
					}
					if (stime.equals(currentStime) && etime.equals(currentEtime)) {
						//相同查询
						searchData(banci, dangbanren, zhungtai);
					} else {
						currentStime = start_time.getText().toString();
						currentEtime = end_time.getText()
								.toString();
						//不同查询
						getData(currentStime, currentEtime);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				break;
		}
	}

	private int mYear;
	private int mMonth;
	private int mDay;

	private void initDialog() {
		dialog1 = new DatePickerDialog(this, mdateListener1, mYear, mMonth, mDay);
		dialog2 = new DatePickerDialog(this, mdateListener2, mYear, mMonth, mDay);
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

	/**
	 * 设置日期 利用StringBuffer追加
	 */
	public void display1() {
		start_time
				.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-")
						.append(mDay).append(" 00:00:00"));
	}

	/**
	 * 设置日期 利用StringBuffer追加
	 */
	public void display2() {
		end_time
				.setText(new StringBuffer().append(mYear).append("-").append(mMonth + 1).append("-")
						.append(mDay).append(" 23:59:59"));
	}
}
