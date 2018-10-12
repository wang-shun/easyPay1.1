package com.acewill.ordermachine.util;

import android.text.TextUtils;

import com.acewill.ordermachine.model.LoginReqModel;
import com.acewill.paylibrary.PayReqModel;
import com.acewill.paylibrary.epay.AliGoodsItem;

import java.util.ArrayList;
import java.util.List;


public class PayUtil {
	public static List<AliGoodsItem> createAliItemList() {
		// TODO Auto-generated method stub
		List<AliGoodsItem> aliItems = new ArrayList<>();

		AliGoodsItem oneAliItem = new AliGoodsItem();
		oneAliItem.setGoods_id(String.valueOf(System.currentTimeMillis()));
		oneAliItem.setGoods_category("");
		oneAliItem.setGoods_name(String.valueOf(System.currentTimeMillis()));
		try {
			oneAliItem.setPrice("0.01");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		oneAliItem.setQuantity("1");

		aliItems.add(oneAliItem);

		return aliItems;
	}


	public static String getWXDetailsStr2() {
		// TODO Auto-generated method stub
		StringBuffer res = new StringBuffer();
		String subject = "";
		if (TextUtils.isEmpty(LoginReqModel.sname)) {
			subject = "未知商品信息";
		} else
			subject = LoginReqModel.sname;
		res.append(subject);
		return res.toString();
	}


	/*
	payType:支付方式id
	authCode : 微信或者支付宝的支付码
	amount : 支付的金额
	payTypeName :支付方式的名称
	orderNo :支付的流水号
	 */
	public static PayReqModel getPayReqModel(int payType, String authCode, String amount, String payTypeName, String orderNo) {

		PayReqModel payReqModel = new PayReqModel();
		payReqModel.payTypeName = payTypeName;
		payReqModel.orderNo = orderNo;
		payReqModel.authCode = authCode;
		payReqModel.payType = payType;
		payReqModel.store_id = LoginReqModel.storeid;
		payReqModel.store_name = "深圳奥琦玮";
		payReqModel.terminal_id = LoginReqModel.tname;
		if (payType == PayReqModel.PTID_SSS_ALI) {
			payReqModel.aliGoodsItem = createAliItemList();
		} else if (payType == PayReqModel.PTID_SSS_WEIXIN) {
			payReqModel.wxGoodsDetail = getWXDetailsStr2();
		} else if (payType == PayReqModel.PTID_SSS_SWIFTPASS) {
			payReqModel.orderNo = authCode;
		}
		payReqModel.totalAmount = amount;
		return payReqModel;
	}
}
