package com.acewill.ordermachine.pos;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.acewill.ordermachine.activity.WshCreateDeal;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.model.LoginRep;
import com.acewill.ordermachine.model.LoginReqModel;
import com.acewill.ordermachine.model.StoreInfoResp;
import com.acewill.ordermachine.model.StringResp;
import com.acewill.ordermachine.util.SharedPreferencesUtil;
import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.paylibrary.PayReqModel;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;

import static com.zhy.http.okhttp.OkHttpUtils.post;

public class NewPos {
	//	public static String BASE_URL_SZ = "http://43.241.226.10:18080/";


	//			public static        String BASE_URL   = "http://www.smarant.com:8080/";
	//	public static        String BASE_URL         = "http://sz.canxingjian.com/";
	private              boolean isDebug               = false;
	public static        String  BASE_URL              = "";
	private static final String  TAG                   = "NewPos";
	public static        String  loginUrl              = "/api/terminal/login";
	public static        String  paytypesUrl           = "/api/terminal/paytypes";
	public static        String  bindUrl               = "/api/terminal/bind";
	public static        String  unbindUrl             = "/api/terminal/unbind";
	public static        String  store_operation       = "/api/store_operation";
	private static       String  memberUrl             = "public/members/getMemberInfo";
	private static       String  printListUrl          = "api/kichenStalls/getPrinters";
	private static       String  storeConfigurationUrl = "api/store_operation/getStoreConfiguration";
	private static       String  historyOrderUrl       = "api/terminal/getOrderHistoryByDate";
	private static       String  dailyReportUrl        = "api/report/daily";
	private static       String  endDailyBusinessUrl   = "api/orders/endDailyBusiness";
	public static        String  token                 = "";
	public static boolean HAVA_WX;
	public static boolean HAVA_ALI;
	public static boolean HAVA_SWIFTPASS;
	public static boolean HAVA_CASH;
	public static final int WX_PAY        = 2;
	public static final int ALI_PAY       = 1;
	public static final int HY_PAY        = 6;
	public static final int SWIFTPASS_PAY = -8;//威富通支付

	private static NewPos mInstance;
	private String openWorkShiftUrl   = "/api/store_operation/work_shifts";
	private String workShiftUrl       = "api/store_operation/work_shift_definition";
	private String startWorkShiftUrl  = "/api/store_operation/work_shifts_dc";
	private String endWordShiftUrl    = "/api/store_operation/end_work_shifts_dc";
	private String workShiftsStore    = "/api/store_operation/work_shifts_store";
	private String workShiftReportUrl = "/api/report/workShift";
	private String validateURL        = "/management/user/sendValidateCode";
	private String forgetPwdUrl       = "/management/user/forgetPwd";
	public  String dishMenuUrl        = "/api/terminal/dishmenu/sku?token=";
	public  String dishKindUrl        = "/api/terminal/dishKind?token=";
	private String FaceToFacePayment  = "alipay/FaceToFacePayment";
	private String queryAlipayResult  = "alipay/queryAlipayResult";

	public static NewPos getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new NewPos();
		}
		return mInstance;
	}

	public static String getBaseUrl() {
		return "http://" + BASE_URL;
	}

	public String userLoginUrl = "api/terminal/userlogin";


	public String getDishMenuUrl() {
		return getBaseUrl() + dishMenuUrl + token;
	}

	public String getDishKindUrl() {
		return getBaseUrl() + dishKindUrl + token;
	}

	private String getOpenWorkShiftUrl() {
		return getBaseUrl() + openWorkShiftUrl;
	}

	private String getWorkShiftUrl() {
		return getBaseUrl() + workShiftUrl;
	}

	private String getHistoryOrderUrl() {
		return getBaseUrl() + historyOrderUrl;
	}

	private String getDailyReportUrl() {
		return getBaseUrl() + dailyReportUrl;
	}

	private String getEndDailyBusinessUrl() {
		return getBaseUrl() + endDailyBusinessUrl;
	}


	private String startWorkShiftUrl() {
		return getBaseUrl() + startWorkShiftUrl;
	}

	private String getWorkShiftsStoreUrl() {
		return getBaseUrl() + workShiftsStore;
	}

	private String getWorkShiftReportUrl() {
		return getBaseUrl() + workShiftReportUrl;
	}

	private String getStoreConfigurationUrl() {
		return getBaseUrl() + storeConfigurationUrl;
	}

	/**
	 * 获取门店设置信息
	 *
	 * @return
	 */
	public void getStoreConfiguration() {
		OkHttpUtils.post().url(getStoreConfigurationUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("storeid", LoginReqModel.storeid)
				.build().execute(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(String response, int id) {
				Log.e(TAG, "response>>" + response);
			}
		});
	}

	/**
	 * 获取交接班报表数据，用来打印的
	 *
	 * @return
	 */
	public void workShiftReport(String workShiftId, String endWorkAmount, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils.get().url(getWorkShiftReportUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("brandId", LoginReqModel.brandid)
				.addParams("workShiftId", workShiftId)
				.addParams("endWorkAmount", endWorkAmount)
				.build().execute(callback);


	}

	/**
	 * 获取日结报表，用来打印的
	 *
	 * @return
	 */
	public void dailyReport(com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils.get().url(getDailyReportUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("brandId", LoginReqModel.brandid)
				.build().execute(callback);
	}

	/**
	 * 日结
	 *
	 * @return
	 */
	public void endDailyBusiness(com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils.post().url(getEndDailyBusinessUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("brandId", LoginReqModel.brandid)
				.build().execute(callback);
	}

	/**
	 * 获取历史订单
	 *
	 * @return
	 */
	public void getHistoryOrder(String startDate, String endDate, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils.post().url(getHistoryOrderUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("storeid", LoginReqModel.storeid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("startDate", startDate)
				.addParams("endDate", endDate)
				.addParams("token", token)
				.build().execute(callback);


	}


	/**
	 * 获取交接班记录
	 *
	 * @return
	 */
	public void getWorkShiftStore(String startTime, String endTime, com.zhy.http.okhttp.callback.Callback callback) {

		OkHttpUtils.get().url(getWorkShiftsStoreUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("brandId", LoginReqModel.brandid)
				.addParams("startTime", startTime)
				.addParams("endTime", endTime).build().execute(callback);
	}

	/**
	 * 开启一个班次
	 *
	 * @return
	 */
	public void startWorkShift(String workShift, com.zhy.http.okhttp.callback.Callback callback) {

		OkHttpUtils.post().url(startWorkShiftUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("workShift", workShift).build().execute(callback);

	}

	/**
	 * 检测当前是否有未交的班次
	 *
	 * @return
	 */
	public void getOpenWorkShift(com.zhy.http.okhttp.callback.Callback callback) {

		OkHttpUtils.get().url(getOpenWorkShiftUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("terminalId", Common.SHOP_INFO.terminalid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("userName", Common.SHOP_INFO.userphone).build().execute(callback);


	}

	/**
	 * 获取后台定义的班次信息
	 *
	 * @return
	 */
	public void getWorkShift(com.zhy.http.okhttp.callback.Callback callback) {

		OkHttpUtils.get().url(getWorkShiftUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("brandId", LoginReqModel.brandid)
				.addParams("storeId", LoginReqModel.storeid)
				.build().execute(callback);
	}


	public String getUserLoginUrl() {
		return getBaseUrl() + userLoginUrl;
	}

	public void userLogin(String phone, String pwd, StringCallback callback) {
		OkHttpUtils
				.post()
				.url(getUserLoginUrl())
				.addParams("username", phone)
				.addParams("pwd", pwd)
				.addParams("appid", LoginReqModel.appid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("storeid", LoginReqModel.storeid)
				.build()
				.execute(callback);
	}

	public String getPrintListUrlUrl() {
		return getBaseUrl() + printListUrl;
	}

	public void getPrintList(StringCallback callback) {
		OkHttpUtils
				.post()
				.url(getPrintListUrlUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("storeid", LoginReqModel.storeid)
				.build()
				.execute(callback);
	}


	public String checkUserAuthorityUrl = "/api/terminal/checkUserAuthority";

	public String getRefundReasonUrl() {
		return getBaseUrl() + refundReasonUrl;
	}

	public String refundReasonUrl = "/api/orders/reason";


	public void getRefundReason(StringCallback callback) {
		OkHttpUtils
				.get()
				.url(getRefundReasonUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("storeid", LoginReqModel.storeid)
				.build()
				.execute(callback);
	}

	public void checkAthourity(String username, String pwd, String authroityid, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils
				.post()
				.url(getCheckAthroityUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("storeid", LoginReqModel.storeid)
				.addParams("username", username)
				.addParams("pwd", pwd)
				.addParams("authroityid", authroityid)
				.build()
				.execute(callback);
	}

	private String getCheckAthroityUrl() {
		return getBaseUrl() + checkUserAuthorityUrl;
	}

	/**
	 * 交班
	 *
	 * @param workShiftId
	 * @param workShift
	 * @param callback
	 */
	public void endWorkShift(long workShiftId, String workShift, com.zhy.http.okhttp.callback.Callback callback) {

		OkHttpUtils
				.post()
				.url(getEndWordShiftUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("id", workShiftId + "")
				.addParams("workShift", workShift)
				.build()
				.execute(callback);
	}

	private String getEndWordShiftUrl() {
		return getBaseUrl() + endWordShiftUrl;
	}


	public boolean isDebug() {
		return isDebug;
	}

	public void sendValidateMsg(String phone, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils.post().url(getValidateURL()).addParams("username", phone)
				.addParams("isLogin", "true").build().execute(callback);

	}

	private String getValidateURL() {
		return getBaseUrl() + validateURL;
	}

	private String getForgetPwdUrl() {
		return getBaseUrl() + forgetPwdUrl;
	}


	public void forgetPwd(String user, String pwd, StringCallback callback) {
		OkHttpUtils.post().url(getForgetPwdUrl()).addParams("username", user)
				.addParams("validateCode", pwd).build().execute(callback);
	}

	private String backoutUrl = "/api/orders/backout";

	private String getBackOutUrl() {
		return getBaseUrl() + backoutUrl;
	}

	public void backOut(String cost, String payNo, String paymentTypeId, StringCallback callback) {
		OkHttpUtils
				.post()
				.url(getBackOutUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("brandId", LoginReqModel.brandid)
				.addParams("total_fee", cost)
				.addParams("paymentNo", payNo)
				.addParams("paymentTypeId", paymentTypeId)
				.addParams("returnUserName", LoginReqModel.tname)
				.addParams("token", token)
				.build()
				.execute(callback);
	}

	public interface Callback<T> {
		void onError(Call call, Exception e, int id);

		void onResponse(T response, int id);
	}

	public interface LoginCallback {
		void onError(Call call, Exception e, int id);

		void onResponse(LoginRep response, int id);
	}

	private Callback callback;

	public static String getLoginUrl() {
		return getBaseUrl() + loginUrl;
	}


	public static String getStoreOperationUrl() {
		return getBaseUrl() + store_operation;
	}

	public static String getPaytypesUrl() {
		return getBaseUrl() + paytypesUrl;
	}


	public static String getBindUrl() {
		return getBaseUrl() + bindUrl;
	}

	public static String getUnbindUrl() {
		return getBaseUrl() + unbindUrl;
	}


	public void bind(String code, final Callback<StoreInfoResp> callback) {
		//		S77149026
		OkHttpUtils
				.post()
				.url(getBindUrl())
				.addParams("terminalMac", code)
				.addParams("terminalType", "0")
				.build()
				.execute(
						new GenericsCallback<StoreInfoResp>(
								new JsonGenericsSerializator()) {

							@Override
							public void onError(Call call, Exception e,
							                    int id) {
								e.printStackTrace();
								callback.onError(call, e, id);
							}

							@Override
							public void onResponse(StoreInfoResp response,
							                       int id) {
								if (response.result == 0) {
									System.out.println("bind succ");
									LoginReqModel.appid = response.content.appid;
									LoginReqModel.brandid = response.content.brandid;
									LoginReqModel.storeid = response.content.storeid;
								} else {
									System.out.println("bind err");
								}
								callback.onResponse(response, id);
							}
						});
	}

	public void unbind(Context context, String code, final Callback callback) {
		post()
				.url(getUnbindUrl())
				.addParams("terminalMac", code)
				.addParams("token", token)
				.build()
				.execute(
						new GenericsCallback<StringResp>(
								new JsonGenericsSerializator()) {

							@Override
							public void onError(Call call, Exception e,
							                    int id) {
								e.printStackTrace();
								callback.onError(call, e, id);
							}

							@Override
							public void onResponse(StringResp response,
							                       int id) {

								callback.onResponse(response, id);
							}
						});
	}


	/**
	 * appid登录
	 *
	 * @param callback
	 */
	public void terminalLogin(final com.zhy.http.okhttp.callback.Callback callback) {

		post()
				.url(NewPos.getLoginUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("storeid", LoginReqModel.storeid)
				.addParams("tname", LoginReqModel.tname)
				.addParams("terminalmac", Common.SHOP_INFO.terminalMac)
				.addParams("receiveNetOrder", "0")
				.addParams("longitute", "0")
				.addParams("latitute", "0")
				.addParams("description", "")
				.addParams("currentVersion", "1")
				.addParams("versionid", "pos_canteen")
				.build()
				.execute(callback);
	}

	private static NewPos.Callback mCallback;


	private static String getCommitWshDealUrl() {
		return getBaseUrl() + commitWshDealUrl;
	}

	private String refundUrl = "api/orders/refund";

	public String getRefundUrl() {
		return getBaseUrl() + refundUrl;
	}

	public void refund(String orderId, String reasonId, String userName, String returnUserName, String authUserName, boolean returnMoney, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils
				.post()
				.url(getRefundUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("brandId", LoginReqModel.brandid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("orderId", orderId)
				.addParams("reasonId", reasonId)
				.addParams("userName", userName)//登录名
				.addParams("returnUserName", returnUserName)//真实姓名
				.addParams("authUserName", authUserName)
				.addParams("returnMoney", returnMoney + "")
				.addParams("token", token)
				.build()
				.execute(callback);
	}


	private String orderIdUrl = "api/orders/nextOrderId";

	public String getOrderIdUrl() {
		return getBaseUrl() + orderIdUrl;
	}

	public void getOrderId(int index, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils
				.get()
				.url(getOrderIdUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("brandId", LoginReqModel.brandid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("token", token)
				.id(index)
				.build()
				.execute(callback);
	}

	public String orderUrl = "/api/orders?token=";


	public String getOrderUrl() {
		return getBaseUrl() + orderUrl + token;
		// +"&appId="+LoginReqModel.appid+"&brandId="+LoginReqModel.brandid+"&storeId="+LoginReqModel.storeid;
	}

	public void pushOrder(int index, String jsondata, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils.post().url(getOrderUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("brandId", LoginReqModel.brandid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("order", jsondata)
				.id(index)
				.build().execute(callback);
	}

	private static String commitWshDealUrl = "public/members/commitDeal";

	public void memberLogin(String account, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils
				.get()
				.url(getMemberUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("brandId", LoginReqModel.brandid)
				.addParams("account", account)
				.addParams("token", token)
				.build()
				.execute(callback);
	}

	private static String createDealUrl = "public/members/createDealDCJ";

	private String getCreateDealUrl() {
		return getBaseUrl() + createDealUrl;
	}

	/**
	 * okHttp post同步请求
	 */
	public void createDeal(final WshCreateDeal.Request wshRequest, com.zhy.http.okhttp.callback.Callback callback) {
		String params = new Gson().toJson(wshRequest);
		OkHttpUtils.post()
				.url(getCreateDealUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("brandId", LoginReqModel.brandid)
				.addParams("token", token)
				.addParams("deal", params).build().execute(callback);
	}

	public String getMemberUrl() {
		return getBaseUrl() + memberUrl;
	}

	public static void commitWshDeal(String bizId, String verifySms, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils
				.post()
				.url(getCommitWshDealUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("brandId", LoginReqModel.brandid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("bizId", bizId)
				.addParams("verifySms", verifySms)
				.build()
				.execute(callback);
	}

	public void getPayment(com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils.post()
				.url(NewPos.getPaytypesUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("storeid", LoginReqModel.storeid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("terminalid", Common.SHOP_INFO.terminalid)
				.build()
				.execute(callback);
	}

	public String getSwiftPassPayUrl() {
		return getBaseUrl() + swiftpassUrl;
	}

	public String getSwiftPassPayResultUrl() {
		return getBaseUrl() + swiftpassQueryUrl;
	}

	/**
	 * 方法 ：威富通下单之后 查询订单是否成功
	 * <p>
	 * out_trade_no   订单号 5位数以上
	 *
	 * @param callback
	 */
	public void getSwiftPassPayResult(String out_trade_no, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils
				.post()
				.url(getSwiftPassPayResultUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("out_trade_no", out_trade_no)
				.addParams("token", token)
				.build()
				.execute(callback);
	}

	private String swiftpassQueryUrl = "api/swiftpass/query";
	private String swiftpassUrl      = "api/swiftpass/gateway";

	public void swiftPassPay(String code, String body, String mch_create_ip, String total_fee, String out_trade_no, com.zhy.http.okhttp.callback.Callback callback) {

		OkHttpUtils
				.post()
				.url(getSwiftPassPayUrl())
				.addParams("appId", LoginReqModel.appid)
				.addParams("storeId", LoginReqModel.storeid)
				.addParams("auth_code", code)
				.addParams("mch_create_ip", mch_create_ip)
				.addParams("body", body)
				.addParams("total_fee", total_fee)
				.addParams("out_trade_no", out_trade_no)
				.addParams("token", token)
				.build()
				.connTimeOut(300000)
				.execute(callback);
	}

	public String uploadLogUrl = "/api/terminal/uploadLog?token=";

	public String getUploadLogUrl() {
		return getBaseUrl() + uploadLogUrl + token;
	}


	public void uploadLog(final File file,
	                      final Context context) {
		if (file == null) {
			return;
		}
		OkHttpUtils
				.post()
				.addFile("file", file.getName(), file)
				.url(getUploadLogUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("storeid", LoginReqModel.storeid)
				.addParams("tname", Common.SHOP_INFO.tname)
				.addParams("terminalid", Common.SHOP_INFO.terminalid)
				.build()
				.writeTimeOut(120 * 1000)
				.execute(
						new GenericsCallback<LoginRep>(
								new JsonGenericsSerializator()) {
							@Override
							public void onError(Call call, Exception e, int id) {
								ToastUtils.showToast(context, "网络异常!");
								PrintFile.printResult2("日志上传失败--失败原因:\n" + e.getMessage() + "\n");
							}

							@Override
							public void onResponse(LoginRep response, int id) {
								ToastUtils.showToast(context, "日志上传成功");
								PrintFile.printResult2("日志上传成功");
							}
						});
	}

	public void uploadLog2(final File file,
	                       final Context context) {
		if (file == null) {
			return;
		}
		OkHttpUtils
				.post()
				.addFile("file", file.getName(), file)
				.url(getUploadLogUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("storeid", LoginReqModel.storeid)
				.addParams("tname", Common.SHOP_INFO.tname)
				.addParams("terminalid", Common.SHOP_INFO.terminalid)
				.build()
				.writeTimeOut(120 * 1000)
				.execute(
						new GenericsCallback<LoginRep>(
								new JsonGenericsSerializator()) {
							@Override
							public void onError(Call call, Exception e, int id) {
								ToastUtils.showToast(context, "网络异常!");
								PrintFile.printResult2("日志上传失败2--失败原因:\n" + e.getMessage() + "\n");
							}

							@Override
							public void onResponse(LoginRep response, int id) {
								String today = new SimpleDateFormat("yyyy-MM-dd")
										.format(new Date());
								SharedPreferencesUtil.setLastUploadTime(context, today);
								PrintFile.printResult2("日志上传成功2");
							}
						});
	}


	public void getKind(com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils
				.post()
				.url(getDishKindUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("storeid", LoginReqModel.storeid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("terminalid", Common.SHOP_INFO.terminalid)
				.addParams("sourcetype", "1")//堂食还是外带
				.build()
				.connTimeOut(300000)
				.execute(callback);
	}

	public void getDish(com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils
				.post()
				.url(getDishMenuUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("storeid", LoginReqModel.storeid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("terminalid", Common.SHOP_INFO.terminalid)
				.addParams("sourcetype", "1")
				.build()
				.connTimeOut(300000)
				.execute(callback);
	}


	public void aliFaceToFacePay(PayReqModel model, com.zhy.http.okhttp.callback.Callback callback) {
		String subject = "";
		if (TextUtils.isEmpty(LoginReqModel.sname)) {
			subject = "未知商品信息";
		} else
			subject = LoginReqModel.sname;
		OkHttpUtils
				.post()
				.url(getAliFaceToFaceUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("storeid", LoginReqModel.storeid)
				.addParams("outTradeNo", model.orderNo)
				.addParams("authCode", model.authCode)
				.addParams("subject", subject)
				.addParams("totalAmount", model.totalAmount)
				.addParams("timeoutExpress", "1")
				.addParams("paymentStr", model.sPaymentInfo)
				.build()
				.execute(callback);
	}

	private String getAliFaceToFaceUrl() {
		return getBaseUrl() + FaceToFacePayment;
	}

	public void queryAlipayResult(PayReqModel model, com.zhy.http.okhttp.callback.Callback callback) {
		OkHttpUtils
				.post()
				.url(getqueryAlipayResultUrl())
				.addParams("appid", LoginReqModel.appid)
				.addParams("brandid", LoginReqModel.brandid)
				.addParams("storeid", LoginReqModel.storeid)
				.addParams("outTradeNo", model.orderNo)
				.addParams("paymentStr", model.sPaymentInfo)
				.build()
				.execute(callback);
	}

	private String getqueryAlipayResultUrl() {

		return getBaseUrl() + queryAlipayResult;
	}
}
