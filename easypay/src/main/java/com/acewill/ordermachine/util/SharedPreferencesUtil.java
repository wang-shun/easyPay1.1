package com.acewill.ordermachine.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.acewill.chikenprintlibrary.printer.Printer;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.model.LoginReqModel;
import com.acewill.ordermachine.model.StoreInfoModel;

import java.util.HashMap;

/**
 * SharedPreferences 工具
 * 设置页面相关的sp
 */
public class SharedPreferencesUtil {
	private static final String TAG  = "SharedPreferencesUtil";
	private static       String NAME = "info";


	public static void saveShopInfo(Context context, StoreInfoModel store) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("appid", store.appid);
		editor.putString("brandid", store.brandid);
		editor.putString("storeid", store.storeid);
		editor.putString("tname", store.tname);//终端名称
		editor.putString("sname", store.sname);//门店名称
		editor.putString("terminalmac", store.terminalMac);//终端码
		editor.putString("brandName", store.brandName);//品牌名称
		editor.putString("phone", store.phone);//门店电话
		editor.putString("address", store.address);//门店地址
		editor.putString("terminalid", store.terminalid);//门店终端id
		editor.putLong("storeEndTime", store.storeEndTime);//门店过期时间
		editor.putString("currentVersion", store.currentVersion);//门店过期时间
		editor.putString("printerIp", store.printerIp);//打印机ip
		editor.putString("printerName", store.printerName);//打印机ip
		editor.putString("vendor", store.vendor);//打印机ip
		editor.commit();
		Common.SHOP_INFO = getShopInfo(context);
	}

	public static void cleanLoginInfo(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("appid", "");
		editor.putString("brandid", "");
		editor.putString("storeid", "");
		editor.putString("terminalid", "");
		editor.putString("tname", "");
		editor.putString("sname", "");
		editor.putString("terminalmac", "");
		editor.putString("brandName", "");
		editor.putString("phone", "");
		editor.putString("address", "");
		editor.putLong("storeEndTime", 0);
		editor.putString("currentVersion", "");
		editor.putString("printerIp", "");
		editor.putString("printerName", "");
		editor.putString("vendor", "");
		editor.commit();
	}

	public static StoreInfoModel getShopInfo(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);

		LoginReqModel.appid = spf.getString("appid", "");
		LoginReqModel.brandid = spf.getString("brandid", "");
		LoginReqModel.storeid = spf.getString("storeid", "");
		LoginReqModel.tname = spf.getString("tname", "");


		//下面才是StoreInfoModel
		StoreInfoModel shopInfoModel = new StoreInfoModel();
		shopInfoModel.terminalid = spf.getString("terminalid", "");
		shopInfoModel.tname = spf.getString("tname", "");
		shopInfoModel.sname = spf.getString("sname", "");
		shopInfoModel.terminalMac = spf.getString("terminalmac", "");
		shopInfoModel.brandName = spf.getString("brandName", "");
		shopInfoModel.phone = spf.getString("phone", "");
		shopInfoModel.address = spf.getString("address", "");
		shopInfoModel.storeEndTime = spf.getLong("storeEndTime", 0);
		shopInfoModel.currentVersion = spf.getString("currentVersion", "");
		shopInfoModel.printerIp = spf.getString("printerIp", "");
		shopInfoModel.printerName = spf.getString("printerName", "");
		shopInfoModel.vendor = spf.getString("vendor", "");
		Common.PRINTER_PRODUCTID = spf.getString("productId", "");


		if (!TextUtils.isEmpty(shopInfoModel.vendor)) {
			Printer printer = new Printer();
			printer.setIp(shopInfoModel.printerIp);
			printer.setDescription(shopInfoModel.printerName);
			printer.setVendor(shopInfoModel.vendor);
			Common.SHOP_PRINTER = printer;
		}
		return shopInfoModel;
	}

	public static void saveAd(Context context, String ad) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("ad", ad);
		editor.commit();
	}

	/**
	 * 如果是二代打印机，就会有这个产品id
	 *
	 * @param context
	 * @param productId
	 */
	public static void saveProductId(Context context, String productId) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("productId", productId);
		editor.commit();
	}

	public static String getProductId(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getString("productId", "");
	}


	/*打印机的版本，一代打印机还是二代打印机*/
	public static void savePrintType(Context context, int printer_version) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putInt("printer_version", printer_version);
		editor.commit();
	}

	public static int getPrintType(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getInt("printer_version", 1);//默认是一代打印机
	}

	/**
	 * 支付方式（正扫、反扫）
	 *
	 * @param context
	 * @param payType
	 */
	public static void savePayType(Context context, int payType) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putInt("payType", payType);
		editor.commit();
	}

	public static int getPayType(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getInt("payType", 0);//默认是SCAN_TYPE      就是机器扫
	}


	public static String getLastUploadTime(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getString("upload_time", "2016-12-10 000000");
	}

	public static void setLastUploadTime(Context context, String logFileName) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		String[]                 split  = logFileName.split("\\.");
		if (split.length > 1) {

			editor.putString("upload_time", split[0]);
			editor.commit();
		}
	}

	public static HashMap<String, String> getCardInfo(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("3828405866", spf.getString("3828405866", "100.00"));
		map.put("3832102874", spf.getString("3832102874", "200.00"));
		map.put("3948103991", spf.getString("3948103991", "80.00"));
		map.put("3948282887", spf.getString("3948282887", "30.00"));
		return map;
	}

	public static void setCardInfo(Context context, String card, String price) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString(card, price);
		editor.commit();
	}

	public static void saveRecomendSwift(Context context, boolean onOrOff) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("recomendswift", onOrOff);
		editor.commit();
	}

	public static boolean getRecomendSwift(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("recomendswift", false);
	}


	public static void saveBaseUrl(Context context, String baseurl) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("baseurl", baseurl);
		editor.commit();
	}

	public static String getBaseUrl(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getString("baseurl", "www.smarant.com/");
	}


	public static void saveCostomerChoose(Context context, String costomerChoose) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("costomerChoose", costomerChoose);
		editor.commit();
	}

	public static String getCostomerChoose(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getString("costomerChoose", "");
	}

	public static boolean getQRSwfit(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("QRSwfit", false);
	}

	public static void saveQRSwfit(Context context, boolean b) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("QRSwfit", b);
		editor.commit();
	}

	public static void saveifPrintTicket(Context context, boolean b) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("Print", b);
		editor.commit();
	}

	public static boolean getifPrintTicket(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("Print", true);
	}

	public static void savePrint(Context context, boolean b) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("Print", b);
		editor.commit();
	}

	public static boolean getPrint(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("Print", true);
	}


	public static void saveKDS(Context context, boolean b) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("KDS", b);
		editor.commit();
	}

	public static boolean getKDS(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("KDS", true);
	}


	public static String getLoginUser(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getString("user_and_pwd", "");
	}

	public static void saveLoginUser(Context context, String user_and_pwd) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("user_and_pwd", user_and_pwd);
		editor.commit();
	}


	public static String getTempMemberPassword(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getString("tempMemberPassword", "");
	}

	public static void saveTempMemberPassword(Context context, String password) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("tempMemberPassword", password);
		editor.commit();
	}

	public static void saveInvoiceSwift(Context context, boolean b) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("INVOICE", b);
		editor.commit();
	}

	public static boolean getInvoiceSwift(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("INVOICE", false);
	}

	public static void setKaiqianxiangAthority(Context context, boolean contains) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("openposbox_athourity", contains);
		editor.commit();
	}

	public static boolean getKaiqianxiangAthority(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("openposbox_athourity", false);
	}

	public static void setRefundAthuority(Context context, boolean contains) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("refund_athourity", contains);
		editor.commit();
	}

	public static boolean getRefundAthuority(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("refund_athourity", false);
	}

	public static void saveTempAhroityName(Context context, String name) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("tempAhroityName", name);
		editor.commit();
	}


	public static String getTempAhroityName(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getString("tempAhroityName", "");
	}

	public static void saveShowLiGangSuoDing(Context context, boolean show) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("ligangsuoding", show);
		editor.commit();
	}

	public static boolean getShowLiGangSuoDing(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("ligangsuoding", false);
	}

	public static boolean getCheckPrintBeforeNewOrder(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("checkprintbeforeneworder", true);
	}

	public static void saveCheckPrintBeforeNewOrder(Context context, boolean checked) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("checkprintbeforeneworder", checked);//false
		editor.commit();
	}

	public static void saveNewVersionPath(Context context, String path) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("newVersionPath", path);
		editor.commit();
	}

	public static String getNewVersionPath(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getString("newVersionPath", "");
	}

	public static void saveNewVersionDescription(Context context, String path) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putString("NewVersionDescription", path);
		editor.commit();
	}

	public static String getNewVersionDescription(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getString("NewVersionDescription", "");
	}

	public static boolean getRefundPrint(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("refundprint", true);
	}

	public static void saveRefundPrint(Context context, boolean refundprint) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("refundprint", refundprint);
		editor.commit();
	}

	public static boolean getQuanXian(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("quanxian", false);
	}
	public static void saveQuanXian(Context context, boolean quanxian) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("quanxian", quanxian);
		editor.commit();
	}

	public static boolean getJiaoBan(Context context) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		return spf.getBoolean("jiaobanprint", true);
	}
	public static void saveJiaoBan(Context context, boolean jiaoban) {
		SharedPreferences spf = context.getSharedPreferences(NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = spf.edit();
		editor.putBoolean("jiaobanprint", jiaoban);
		editor.commit();
	}
	//	public static void saveVersionCode(Context context, int version) {
	//		SharedPreferences spf = context.getSharedPreferences(NAME,
	//				Context.MODE_PRIVATE);
	//		SharedPreferences.Editor editor = spf.edit();
	//		editor.putInt("currentVersion", version);
	//		editor.commit();
	//	}
	//
	//	public static int getVersionCode(Context context) {
	//		SharedPreferences spf = context.getSharedPreferences(NAME,
	//				Context.MODE_PRIVATE);
	//		return spf.getInt("currentVersion", 0);
	//	}

	//	public static void saveAppDownLoadPath(Context context, String downloadpath) {
	//		SharedPreferences spf = context.getSharedPreferences(NAME,
	//				Context.MODE_PRIVATE);
	//		SharedPreferences.Editor editor = spf.edit();
	//		editor.putString("AppDownLoadPath", downloadpath);
	//		editor.commit();
	//	}
	//
	//	public static String getAppDownLoadPath(Context context) {
	//		SharedPreferences spf = context.getSharedPreferences(NAME,
	//				Context.MODE_PRIVATE);
	//		return spf.getString("AppDownLoadPath", "");
	//	}

	//	public static void saveToken(Context context, String token) {
	//		SharedPreferences spf = context.getSharedPreferences(NAME,
	//				Context.MODE_PRIVATE);
	//		SharedPreferences.Editor editor = spf.edit();
	//		editor.putString("token", token);
	//		editor.commit();
	//	}
	//	public static String getToken(Context context) {
	//		SharedPreferences spf = context.getSharedPreferences(NAME,
	//				Context.MODE_PRIVATE);
	//		return spf.getString("token", "");
	//	}
}
