package com.acewill.paylibrary;

import android.util.Log;

import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.epay.AliBarPayAction;
import com.acewill.paylibrary.epay.EPayResult;
import com.acewill.paylibrary.epay.WeiXinScanPayAction;
import com.acewill.paylibrary.tencent.WXPay;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MicropayTask extends EPayTask {

	private              SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分
	private static final String           TAG    = "myLog";

	@Override
	protected EPayResult doInBackground(PayReqModel... params) {
		PayReqModel payModel   = params[0];
		EPayResult  ePayResult = null;
		if (payModel.payType == PayReqModel.PTID_SSS_ALI) {
			Log.e(TAG, "支付宝开始执行支付的时间:" + format.format(new Date()));
			ePayResult = AliPay(payModel);
		} else if (payModel.payType == PayReqModel.PTID_SSS_WEIXIN) {
			Log.e(TAG, "微信开始执行支付的时间:" + format.format(new Date()));
			ePayResult = weixinPay(payModel);
		}

		if (ePayResult == null) {
			System.out.println("pay nul");
			return null;
		}
		if (ePayResult.success == false) {
			System.out.println("pay false");
			return ePayResult;
		}
//		System.err.println("pay query");
		//		if (payModel.payType == PayReqModel.PTID_SSS_ALI) {
		//
		//			ePayResult = StartWaitingForPayCompletion(payModel);
		//		} else {
		//
		//			ePayResult = rollbackTransaction(payModel);
		//		}
		return ePayResult;
	}


	private EPayResult AliPay(PayReqModel payModel) {
		EPayResult ePayResult;
		try {
			String totalAmount = payModel.totalAmount + "";
			if (payModel.isDebug) {
				totalAmount = "0.01";
			}
			ePayResult = AliBarPayAction.micropay(AlipayConfig.APPID,
					AlipayConfig.key, payModel.orderNo, payModel.authCode,
					totalAmount, payModel.store_name, payModel.aliGoodsItem, payModel.store_id, payModel.terminal_id);
			return ePayResult;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private EPayResult weixinPay(PayReqModel payModel) {
		String notify_url = "11111111";
		String trade_type = "NATIVE";
		String product_id = "0000";

		// System.out.println("weixin appid = " + WXPay.APPID + ",mchId = "
		// + WXPay.MCH_ID + ",order id = " + Common.cart.orderNo);
		int fenShouldPay = new BigDecimal(payModel.totalAmount).multiply(
				new BigDecimal(100)).intValue();

		if (payModel.isDebug) {
			fenShouldPay = 1;
		}

		String body = payModel.wxGoodsDetail;

		body = body.substring(0, Math.min(30, body.length()));

		EPayResult result = WeiXinScanPayAction.micropay(WXPay.APPID,
				WXPay.MCH_ID, WXPay.KEY, payModel.authCode, payModel.orderNo,
				fenShouldPay, body, WXPay.SUB_MCH_ID, product_id);
		return result;

	}
}
