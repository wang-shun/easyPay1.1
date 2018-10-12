package com.acewill.paylibrary;

import com.acewill.paylibrary.epay.AliGoodsItem;

import java.util.List;

public class PayReqModel {
	public static final int    PTID_SSS_MEMBER               = 345;//会员支付的一个统称，用来表示使用会员支付
	public static final String PTID_SSS_MEMBERSTR            = "会员支付";//会员支付的一个统称，用来表示使用会员支付
	public static final int    PTID_SSS_ONLINEPAY            = 102;
	public static       int    PTID_CASH                     = 0;
	public static       String PTID_CASHSTR                  = "现金";
	public static       int    PTID_SSS_ALI                  = 1;
	public static       String PTID_SSS_ALISTRSTR            = "支付宝";
	public static       int    PTID_SSS_WEIXIN               = 2;
	public static       String PTID_SSS_WEIXINSTR            = "微信";
	public static       int    PTID_SSS_MEMBER_CHUZHI        = 3;
	public static       String PTID_SSS_MEMBER_CHUZHISTR     = "会员储值";
	public static       int    PTID_SSS_MEMBER_YOUHUIQUAN    = 4;
	public static       String PTID_SSS_MEMBER_YOUHUIQUANSTR = "会员优惠券";
	public static       int    PTID_SSS_MEMBER_JIFEN         = 5;
	public static       String PTID_SSS_MEMBER_JIFENSTR      = "会员积分";
	public static       int    PTID_SSS_SWIFTPASS            = -8;
	public static       String PTID_SSS_SWIFTPASSSTR         = "威富通";
	public static boolean                cashbox;
	public        String             totalAmount;
	public        String             orderNo;
	public        List<AliGoodsItem> aliGoodsItem;
	public        String             wxGoodsDetail;
	public        boolean            isDebug;

	public int    payType;//这个payType就是上面的那些个常量
	public String payTypeName;
	public String authCode;
	public String store_id;
	public String terminal_id;
	public String store_name;

	public static int    currentPayType;
	public static String currentPayName;
	public static String currentPayNameStr;


	public String sPaymentInfo;
	@Override
	public String toString() {
		return "PayReqModel{" +
				"totalAmount='" + totalAmount + '\'' +
				", orderNo='" + orderNo + '\'' +
				", aliGoodsItem=" + aliGoodsItem +
				", wxGoodsDetail='" + wxGoodsDetail + '\'' +
				", isDebug=" + isDebug +
				", payType=" + payType +
				", authCode='" + authCode + '\'' +
				", store_id='" + store_id + '\'' +
				", terminal_id='" + terminal_id + '\'' +
				", store_name='" + store_name + '\'' +
				'}';
	}
}
