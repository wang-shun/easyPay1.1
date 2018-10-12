package com.acewill.paylibrary;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.epay.AliBarPayAction;
import com.acewill.paylibrary.epay.EPayResult;
import com.acewill.paylibrary.epay.WeiXinScanPayAction;
import com.acewill.paylibrary.tencent.WXPay;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class EPayTask extends AsyncTask<PayReqModel, String, EPayResult> {
	private              SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分
	private static final String           TAG    = "myLog";

	@Override
	protected EPayResult doInBackground(PayReqModel... params) {
		PayReqModel payModel   = params[0];
		EPayResult  ePayResult = null;
		if (payModel.payType == PayReqModel.PTID_SSS_ALI) {

			ePayResult = AliPay(payModel);
		} else if (payModel.payType == PayReqModel.PTID_SSS_WEIXIN) {

			ePayResult = weixinPay(payModel);
		}
		if (ePayResult == null) {
			System.out.println("pay nul");
			publishProgress("");
			return null;
		}
		if (ePayResult.success == false) {
			System.out.println("pay false");
			publishProgress("");
			return null;
		}
		publishProgress(ePayResult.qr_code);
		if (payModel.payType == PayReqModel.PTID_SSS_ALI) {

			ePayResult = StartWaitingForPayCompletion(payModel);
		} else {

			ePayResult = rollbackTransaction(payModel);
		}

		return ePayResult;

	}


	protected EPayResult StartWaitingForPayCompletion(PayReqModel payModel) {

		EPayResult result = new EPayResult();

		try {
			long start = System.currentTimeMillis();
			SystemClock.sleep(1000);
			while (System.currentTimeMillis() - start < 2 * 60 * 1000)// 20
			// seconds
			{
				if (isCancelled()) {
					AliBarPayAction.cancelPayment(AlipayConfig.APPID,
							AlipayConfig.key, payModel.orderNo);
					return result;
				}
				SystemClock.sleep(1000);
				result = AliBarPayAction.aliCheckPayStatus(AlipayConfig.APPID,
						AlipayConfig.key, payModel.orderNo);
				if (isCancelled()) {
					AliBarPayAction.cancelPayment(AlipayConfig.APPID,
							AlipayConfig.key, payModel.orderNo);
					result.success = false;
					return result;
				}
				if (EPayResult.TRADE_SUCCESS
						.equalsIgnoreCase(result.trade_status)) {
					result.success = true;
					return result;
				} else if (EPayResult.PAY_STATUS_WAIT
						.equalsIgnoreCase(result.trade_status)) {

					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		PrintFile.printResult(result.toString(),"WaitingForPay");
		return result;
	}

	private EPayResult AliPay(PayReqModel payModel) {
		EPayResult ePayResult;
		try {
			String totalAmount = payModel.totalAmount + "";
			if (payModel.isDebug) {
				totalAmount = "0.01";
			}
			ePayResult = AliBarPayAction.pay(AlipayConfig.APPID,
					AlipayConfig.key, payModel.orderNo, totalAmount, payModel.store_name,
					payModel.aliGoodsItem, payModel.store_id, payModel.terminal_id);
			return ePayResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private EPayResult weixinPay(PayReqModel payModel) {
		// 扫码支付
		String notify_url = "11111111";
		String trade_type = "NATIVE";
		String product_id = "0000";

		int fenShouldPay = new BigDecimal(payModel.totalAmount).multiply(
				new BigDecimal(100)).intValue();

		if (payModel.isDebug) {
			fenShouldPay = 1;
		}

		String body = payModel.wxGoodsDetail;

		body = body.substring(0, Math.min(30, body.length()));

		EPayResult result = WeiXinScanPayAction.unifiedorder(WXPay.APPID,
				WXPay.MCH_ID, WXPay.KEY, payModel.orderNo, fenShouldPay, body,
				WXPay.SUB_MCH_ID, "fds", notify_url, trade_type, product_id);
		return result;
	}

	protected EPayResult rollbackTransaction(PayReqModel payReqModel) {
		System.out.println("start query pay");

		int trans_state = WeiXinScanPayAction.TRANS_UNKNOWN;

		for (int i = 0; i < 120; i++) {
			if (isCancelled()) {
				// WeiXinScanPayAction.cancel(WXPay.APPID, WXPay.MCH_ID,
				// WXPay.KEY, "", Common.cart.orderNo, WXPay.SUB_MCH_ID);
				WeiXinScanPayAction.refund(WXPay.APPID, WXPay.MCH_ID,
						WXPay.KEY, WXPay.SUB_MCH_ID, "", payReqModel.orderNo,
						"", "", 1, 1, WXPay.MCH_ID, "CNY");
				return null;
			}
			waitFor(2);
			System.out.println(" query pay state");
			EPayResult ePayResult = WeiXinScanPayAction.query(WXPay.APPID,
					WXPay.MCH_ID, WXPay.KEY, "", payReqModel.orderNo,
					WXPay.SUB_MCH_ID);
			System.out.println("-----------查询订单" + trans_state);
			switch (ePayResult.result) {
				case WeiXinScanPayAction.TRANS_UNKNOWN:
					continue;
				case WeiXinScanPayAction.USERPAYING:
					continue;
				case WeiXinScanPayAction.TRANS_CLOSED:
					return ePayResult;
				case WeiXinScanPayAction.TRANS_OPEN:
					if (ePayResult.trade_state.equals("SUCCESS")) {
						// myState 为 SUCCESS 则已支付
						ePayResult.success = true;
						return ePayResult;
					} else {
						break;
					}

				default:
					continue;
			}
			PrintFile.printResult(ePayResult
					.toString(), "rollbackTransaction");
		}

		for (int i = 0; i < 3; i++) {
			waitFor(1);

			trans_state = WeiXinScanPayAction.cancel(WXPay.APPID, WXPay.MCH_ID,
					WXPay.KEY, "", payReqModel.orderNo, WXPay.SUB_MCH_ID);
			System.out.println("-----------退单订单" + trans_state);
			switch (trans_state) {
				case WeiXinScanPayAction.TRANS_UNKNOWN:
					continue;
				case WeiXinScanPayAction.TRANS_CLOSED:
					return null;
				default:
					continue;
			}
		}

		return null;
	}

	private void waitFor(int secs) {
		try {
			Thread.currentThread();
			Thread.sleep(secs * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
