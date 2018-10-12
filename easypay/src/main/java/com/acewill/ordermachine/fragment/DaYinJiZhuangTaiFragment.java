package com.acewill.ordermachine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.acewill.chikenprintlibrary.printer.Printer;
import com.acewill.chikenprintlibrary.printer.PrinterFactory;
import com.acewill.chikenprintlibrary.printer.PrinterInterface;
import com.acewill.chikenprintlibrary.printer.PrinterState;
import com.acewill.chikenprintlibrary.printer.PrinterStatus;
import com.acewill.chikenprintlibrary.printer.PrinterVendor;
import com.acewill.ordermachine.R;
import com.acewill.ordermachine.adapter.MyAdapter;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.printadapter.PosKitchenPrintAdapter;
import com.acewill.ordermachine.util_new.PrintFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author：Anch
 * Date：2017/11/24 12:43
 * Desc：
 */
public class DaYinJiZhuangTaiFragment extends BaseFragment {
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = View.inflate(inflater.getContext(), R.layout.fragment_dayinjizhuangtai, null);
		initView(view);
		initData();
		return view;
	}

	private void initData() {
		Printer printer = new Printer();
		printer.setDescription(Common.SHOP_INFO.printerName);
		printer.setVendor(Common.SHOP_INFO.vendor);
		printer.setIp(Common.SHOP_INFO.printerIp);
		printer.setLinkTypeStr(Common.SHOP_INFO.tname);
		List<Printer> printers = new ArrayList<>();
		printers.add(printer);
		adapter = new DaYinJiZhuangTaiAdapter<>(mContext, printers);
		listview.setAdapter(adapter);
	}

	private DaYinJiZhuangTaiAdapter<Printer> adapter;
	private ListView                         listview;

	private void initView(View view) {
		listview = (ListView) view.findViewById(R.id.listview);
	}

	private class DaYinJiZhuangTaiAdapter<Printer> extends MyAdapter {
		public DaYinJiZhuangTaiAdapter(Context context, List dataList) {
			super(context, dataList);
		}

		@Override
		protected View getItemView(int position, View convertView, ViewGroup parent) {
			MyHolder holder;
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.item_dayinjizhuangtai, null);
				holder = new MyHolder();

				holder.item_dayinjizhuangtai_name = (TextView) convertView
						.findViewById(R.id.item_dayinjizhuangtai_name);
				holder.item_dayinjizhuangtai_type = (TextView) convertView
						.findViewById(R.id.item_dayinjizhuangtai_type);
				holder.item_dayinjizhuangtai_ip = (TextView) convertView
						.findViewById(R.id.item_dayinjizhuangtai_ip);
				holder.item_dayinjizhuangtai_relative = (TextView) convertView
						.findViewById(R.id.item_dayinjizhuangtai_relative);
				holder.item_dayinjizhuangtai_statu = (ImageView) convertView
						.findViewById(R.id.item_dayinjizhuangtai_statu);
				holder.item_dayinjizhuangtai_test = (TextView) convertView
						.findViewById(R.id.item_dayinjizhuangtai_test);


				convertView.setTag(holder);
			} else {
				holder = (MyHolder) convertView.getTag();
			}
			final com.acewill.chikenprintlibrary.printer.Printer printer = (com.acewill.chikenprintlibrary.printer.Printer) dataList
					.get(position);

			holder.item_dayinjizhuangtai_name.setText(printer
					.getDescription());

			holder.item_dayinjizhuangtai_type.setText(printer.getVendor());
			holder.item_dayinjizhuangtai_ip.setText(printer.getIp());
			holder.item_dayinjizhuangtai_relative.setText(printer.getLinkTypeStr());
			//			if (checkChickenPrint(printer)) {
			holder.item_dayinjizhuangtai_statu
					.setImageDrawable(Common.isPrintOk ? mContext.getResources()
							.getDrawable(R.drawable.pay_success_new) : mContext.getResources()
							.getDrawable(R.drawable.close_back));
			//			} else {
			//				holder.item_dayinjizhuangtai_statu.setImageDrawable(mContext.getResources()
			//						.getDrawable(R.drawable.close_back));
			//			}
			holder.item_dayinjizhuangtai_test.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					PosKitchenPrintAdapter.getInstance().print3();
				}
			});
			return convertView;
		}


		private class MyHolder {
			TextView  item_dayinjizhuangtai_name;
			TextView  item_dayinjizhuangtai_type;
			TextView  item_dayinjizhuangtai_ip;
			TextView  item_dayinjizhuangtai_relative;
			ImageView item_dayinjizhuangtai_statu;
			TextView  item_dayinjizhuangtai_test;
		}
	}


	private boolean checkChickenPrint(final Printer printer) {

		// 转化订单结构，进行厨房打印

		//监测KDS
		//监测厨房打印

		try {
			if (printer.getVendor().equals("sprt") || printer.getVendor()
					.equals("unknown")) {
				PrintFile.printResult2("斯博瑞特和未知打印机不去检测连接是否正常-----------" + "\n");
				return true;
			}
			int printStatus = checkPrinterState(printer);
			if (printStatus != 576) {
				String list1 = printer.getStandbyPrinterIdList();
				if (list1 != null && !TextUtils.isEmpty(list1)) {
					Printer print = PosKitchenPrintAdapter.getInstance()
							.getPrint(Integer.parseInt(list1));
					int i = checkPrinterState(print);
					if (i != 576) {
						return true;
					} else if (i == 576) {
						return false;
					}
				} else {
					return false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 检测打印机连接的状态
	 *
	 * @param printer
	 * @throws IOException
	 */
	private static int checkPrinterState(Printer printer) throws IOException {

		PrinterInterface printerInterface = null;
		if (printer != null) {
			printerInterface = PrinterFactory
					.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer
							.getIp(), printer.getWidth());
			Log.i("打印机id===" + printer.getId(), printer.getDescription() + "====" + printer
					.getIp());
			Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer.getIp());
		}
		String errMessage = "";
		if (printerInterface == null) {
			errMessage = printer.getDescription() + "打印机初始化打印机失败";
			printer.setPrinterState(PrinterState.ERROR);
			printer.setErrMessage(errMessage);
			PrintFile.printResult2("  errMessage");
			return 100;
		}
		try {
			printerInterface.init();
		} catch (Exception e) {
			e.printStackTrace();
			errMessage = printer.getDescription() + "连接超时";
			printer.setPrinterState(PrinterState.ERROR);
			printer.setErrMessage(errMessage);
			Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer
					.getIp() + "========" + errMessage);
			PrintFile.printResult2("  errMessage");
			return 100;
		}
		if (printerInterface.checkStatus() != PrinterStatus.OK) {
			if (printerInterface.checkStatus() == PrinterStatus.COVER_OPEN) {
				errMessage = printer.getDescription() + "打印机仓盖未关闭";
			} else if (printerInterface.checkStatus() == PrinterStatus.NO_PAPER) {
				errMessage = printer.getDescription() + "打印纸用尽,请更换打印纸";
			} else if (printerInterface.checkStatus() == PrinterStatus.ERROR_OCCURED) {
				errMessage = printer.getDescription() + "出现未知错误";
			} else if (printerInterface.checkStatus() == PrinterStatus.TIMEOUT_ERROR) {
				errMessage = printer.getDescription() + "打印机连接超时";
			} else {
				errMessage = "打印机出现未知异常";
			}
			printer.setPrinterState(PrinterState.ERROR);
			printer.setErrMessage(errMessage);
			PrintFile.printResult2("  errMessage");
			printerInterface.closeNoCutPaper();
			Log.i("打印机:==", "==" + printer.getDescription() + "---" + printer
					.getIp() + "========" + errMessage);
			return 100;
		}
		printerInterface.closeNoCutPaper();
		printer.setPrinterState(PrinterState.SUCCESS);
		PrintFile
				.printResult2("Class_PayOrderPresenter--Method_checkPrinterState()--------打印机:" + printer
						.getDescription() + ",IP:" + printer.getIp() + "正常\n");
		Log.i("打印机:", "打印机=======================");
		return 576;
	}
}
