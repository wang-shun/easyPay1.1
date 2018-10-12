package com.acewill.ordermachine.printadapter;

import android.util.Log;

import com.acewill.chikenprintlibrary.PrinterDataController;
import com.acewill.chikenprintlibrary.model.PA_KDS;
import com.acewill.chikenprintlibrary.model.PA_KDSRes;
import com.acewill.chikenprintlibrary.model.PA_KitchenPrintMode;
import com.acewill.chikenprintlibrary.model.PA_KitchenStall;
import com.acewill.chikenprintlibrary.model.PA_KitchenStallRes;
import com.acewill.chikenprintlibrary.model.PA_Order;
import com.acewill.chikenprintlibrary.model.PA_StoreInfo;
import com.acewill.chikenprintlibrary.model.PA_StoreInfoRes;
import com.acewill.chikenprintlibrary.model.PrinterRes;
import com.acewill.chikenprintlibrary.model.PrinterTemplatesRes;
import com.acewill.chikenprintlibrary.model.WorkShiftNewReport;
import com.acewill.chikenprintlibrary.model.WorkShiftReport;
import com.acewill.chikenprintlibrary.printer.PrintTemplateType;
import com.acewill.chikenprintlibrary.printer.Printer;
import com.acewill.chikenprintlibrary.printer.PrinterTemplates;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.model.OrderReq;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by John5 on 2017/5/1.
 */
public class PosKitchenPrintAdapter {
	private static final String TAG = "PosKitchenPrintAdapter";
	private PrinterDataController printerDataController;

	private static PosKitchenPrintAdapter instance;

	private PosKitchenPrintAdapter() {
		printerDataController = new PrinterDataController();
	}

	public static PosKitchenPrintAdapter getInstance() {
		if (instance == null)
			instance = new PosKitchenPrintAdapter();
		return instance;
	}

	/**
	 * 初始化厨房打印的信息
	 *
	 * @param serverUrl http://host:port
	 * @param appid
	 * @param brandid
	 * @param storeid
	 * @param token
	 * @throws Exception
	 */
	public void init(String serverUrl, String appid, String brandid, String storeid, String token) throws Exception {

		// 获取打印需要的相关信息
		// 获取门店设置信息
		KitChenPrintManager.getInstance()
				.sendPostRequest(serverUrl, token, appid, brandid, KitChenPrintManager.GETSTORECONFIGURATIONURL, storeid, KitChenPrintManager.GETSTORECONFIGURATION, new KitChenPrintCallBack());

		// 获取打印机的信息
		KitChenPrintManager.getInstance()
				.sendPostRequest(serverUrl, token, appid, String
						.valueOf(brandid), KitChenPrintManager.GETPRINTERSURL, String
						.valueOf(storeid), KitChenPrintManager.GETPRINTERS, new KitChenPrintCallBack());

		// 获取KDS的信息
		KitChenPrintManager.getInstance()
				.sendPostRequest(serverUrl, token, appid, String
						.valueOf(brandid), KitChenPrintManager.GETKDSESURL, String
						.valueOf(storeid), KitChenPrintManager.GETKDSES, new KitChenPrintCallBack());

		// 获取档口的信息
		KitChenPrintManager.getInstance()
				.sendPostRequest(serverUrl, token, appid, String
						.valueOf(brandid), KitChenPrintManager.GETKICHENSTALLSURL, String
						.valueOf(storeid), KitChenPrintManager.GETKICHENSTALLS, new KitChenPrintCallBack());

		// 获取打印模板信息
		KitChenPrintManager.getInstance()
				.sendPostRequest(serverUrl, token, appid, String
						.valueOf(brandid), KitChenPrintManager.GETALLTEMPLATESURL, String
						.valueOf(storeid), KitChenPrintManager.GETALLTEMPLATES, new KitChenPrintCallBack());

		//		printerDataController.set
	}

	/**
	 * 进行厨房打印处理
	 *
	 * @param order
	 * @throws Exception
	 */
	public void print(PA_Order order) throws Exception {
		// 处理厨房打印
		printerDataController.printKitchenOrder(order);
	}

	/**
	 * 打印小票
	 *
	 * @param req
	 */
	public void print2(OrderReq req) {
		printerDataController.print(Common.SHOP_PRINTER, req);
	}

	/**
	 * 打印退单小票
	 *
	 * @param req
	 */
	public void print4(OrderReq req) {
		printerDataController.print4(Common.SHOP_PRINTER, req);
	}

	/**
	 * 打印测试小票
	 */
	public void print3() {
		printerDataController.print2(Common.SHOP_PRINTER);
	}


	/**
	 * 打印交班小票
	 *
	 * @param report
	 */
	public void printWorkShift(WorkShiftNewReport report) {
		printerDataController
				.printWorkShift(Common.SHOP_INFO.brandName, Common.SHOP_PRINTER, report);
	}


	public List<Printer> getPrintList(PA_Order order) {
		return printerDataController.getPrintList(order);
	}

	public Printer getPrint(int printId) {
		return printerDataController.getPrint(printId);
	}

	public List<PA_KDS> getKdsList() {
		return printerDataController.getKdsList();
	}

	public void resetPrintStatus() {
		printerDataController.resetPrintStatus();
	}


	/**
	 * 打开钱箱
	 */
	public void openBox() {

		printerDataController.openBox();
	}

	public void printRiJieTicket(WorkShiftReport workShiftReport) {
		printerDataController.printRiJieTicket(Common.SHOP_PRINTER, workShiftReport);
	}


	private class KitChenPrintCallBack extends StringCallback {


		@Override
		public void onError(okhttp3.Call call, Exception e, int i) {
			e.printStackTrace();
		}

		@Override
		public void onResponse(String response, int id) {
			try {
				switch (id) {
					case KitChenPrintManager.GETSTORECONFIGURATION:
						Log.e(TAG, "id:" + id + ",response >>" + response);
						PA_StoreInfoRes storeInfo = GsonUtils
								.getSingleBean(response, PA_StoreInfoRes.class);
						PA_StoreInfo info = new PA_StoreInfo();
						info.setCardNumberMode(storeInfo.getContent().isCardNumberMode());
						printerDataController.setStoreInfo(info);
						break;
					case KitChenPrintManager.GETPRINTERS:
						Log.e(TAG, "id:" + id + ",response >>" + response);
						PrinterRes printer = GsonUtils
								.getSingleBean(response, PrinterRes.class);
						List<Printer> plist = printer.getContent();
						if (plist != null) {
							printerDataController.setPrinterList(plist);
						}
						break;
					case KitChenPrintManager.GETKDSES:
						Log.e(TAG, "id:" + id + ",response >>" + response);
						PA_KDSRes kds = GsonUtils
								.getSingleBean(response, PA_KDSRes.class);
						List<PA_KDSRes.ContentBean> kdslist = kds.getContent();
						if (kdslist != null) {
							ArrayList<PA_KDS> kdses = new ArrayList<>();
							for (PA_KDSRes.ContentBean bean : kdslist) {
								PA_KDS pk = new PA_KDS();
								pk.setId(bean.getId());
								pk.setIp(bean.getIp());
								pk.setKdsName(bean.getKdsName());
								kdses.add(pk);
							}
							printerDataController.setKdsList(kdses);
						}
						break;
					case KitChenPrintManager.GETKICHENSTALLS:
						Log.e(TAG, "id:" + id + ",response >>" + response);
						PA_KitchenStallRes kitchenStall = GsonUtils
								.getSingleBean(response, PA_KitchenStallRes.class);
						List<PA_KitchenStallRes.ContentBean> slist = kitchenStall.getContent();
						if (slist != null) {
							ArrayList<PA_KitchenStall> stalls = new ArrayList<>();
							for (PA_KitchenStallRes.ContentBean bean : slist) {
								PA_KitchenStall k = new PA_KitchenStall();
								k.setAppid(bean.getAppid());
								k.setBrandid(bean.getBrandid());
								k.setStoreid(bean.getStoreid());
								k.setStallsid(bean.getStallsid());
								k.setStallsName(bean.getStallsName());
								k.setPrinterid(bean.getPrinterid());
								k.setKdsid(bean.getKdsid());
								k.setSummaryReceiptCounts(bean.getSummaryReceiptCounts());
								k.setDishReceiptCounts(bean.getDishReceiptCounts());
								k.setDishIdList(bean.getDishIdList());
								k.setKitchenPrintMode(PA_KitchenPrintMode
										.fromValue(bean.getKitchenPrintMode()));
								stalls.add(k);
							}
							printerDataController.setKitchenStallList(stalls);
						}
						break;
					case KitChenPrintManager.GETALLTEMPLATES:
						Log.e(TAG, "id:" + id + ",response >>" + response);
						PrinterTemplatesRes templates = GsonUtils
								.getSingleBean(response, PrinterTemplatesRes.class);
						List<PrinterTemplatesRes.ContentBean> tlist = templates.getContent();
						if (tlist != null) {
							ArrayList<PrinterTemplates> templateList = new ArrayList<PrinterTemplates>();
							for (PrinterTemplatesRes.ContentBean bean : tlist) {
								PrinterTemplates t = new PrinterTemplates();
								t.setTemplateType(PrintTemplateType
										.valueOf(bean.getTemplateType()));
								t.setModels(bean.getModels());
								templateList.add(t);
							}
							printerDataController.setPrinterTemplatesList(templateList);
						}

						break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
