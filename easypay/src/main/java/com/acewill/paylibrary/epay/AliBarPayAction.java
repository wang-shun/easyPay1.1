package com.acewill.paylibrary.epay;

import android.os.SystemClock;
import android.util.Log;

import com.acewill.ordermachine.eventbus.PayStatus;
import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.alipay.util.httpClient.HttpProtocolHandler;
import com.acewill.paylibrary.alipay.util.httpClient.HttpRequest;
import com.acewill.paylibrary.alipay.util.httpClient.HttpResponse;
import com.acewill.paylibrary.alipay.util.httpClient.HttpResultType;
import com.google.gson.Gson;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class AliBarPayAction {

	// ///////////////////////////////////////////////////////////////////////////

	private static final String ALIPAY_GATE_WAY    = "https://openapi.alipay.com/gateway.do?charset=utf-8";
	/**
	 * 支付宝提供给商户的服务接入网关URL(新)
	 */
	private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?charset=utf-8";

	//	private static long expire_time = System
	//			.currentTimeMillis() + 180 * 1000;


	/**
	 * MAP类型数组转换成NameValuePair类型
	 *
	 * @param properties MAP类型数组
	 * @return NameValuePair类型数组
	 */
	private static NameValuePair[] generatNameValuePair(
			Map<String, String> properties) {
		NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
		int             i             = 0;
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			nameValuePair[i++] = new NameValuePair(entry.getKey(),
					entry.getValue());
		}

		return nameValuePair;
	}

	public static EPayResult micropay(String appId, String pkcs8Key,
	                                  String out_trade_no, String auth_code, String total_amount,
	                                  String subject, List<AliGoodsItem> items, String storeId,
	                                  String terminalId) throws HttpException, IOException {
		return AliPayMethod(appId, pkcs8Key, out_trade_no, auth_code,
				total_amount, subject, items, storeId, terminalId, false);
	}

	public static EPayResult pay(String appId, String pkcs8Key,
	                             String out_trade_no, String total_amount, String subject,
	                             List<AliGoodsItem> items, String storeId, String terminalId)
			throws HttpException, IOException {
		return AliPayMethod(appId, pkcs8Key, out_trade_no, total_amount,
				subject, items, storeId, terminalId, false);
	}

	private static       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分
	private static final String           TAG    = "myLog";

	// 刷卡
	public static EPayResult AliPayMethod(String appId, String pkcs8Key,
	                                      String out_trade_no, String auth_code, String total_amount,
	                                      String subject, List<AliGoodsItem> items, String storeId,
	                                      String terminalId, boolean bUseApatchSigner) throws HttpException,
			IOException {
		Log.e("myLog", "Ali支付参数" + ",appId:" + appId + ",pkcs8Key:" + pkcs8Key + ",out_trade_no:" + out_trade_no + ",total_amount:" + total_amount + ",subject:" + subject + ",items:" + items
				.toString() + ",storeId:" + storeId
				+ ",terminalId:" + terminalId + ",bUseApatchSigner:" + bUseApatchSigner);

		EPayResult ePayResult = new EPayResult();

		String time_expire = DateUtils.getDateToString(System
				.currentTimeMillis() + 240 * 1000); // 3分钟超时时间
		String timestamp = DateUtils.getCurrentDate(); // 获取系统当前时间
		System.out.printf("当前时间：" + timestamp + "\n" + "超时时间：" + time_expire
				+ "\n");
		//
		// String paramStr = "app_id="
		// + appId
		// + "&biz_content="// "2015050800065426&biz_content="
		// + "{\"out_trade_no\":\""
		// + out_trade_no
		// + "\",\"scene\":\"bar_code\",\"auth_code\":\""
		// + auth_code
		// + "\",\"seller_id"
		// + "\":\"\",\"total_amount\":" + total_amount + ","
		// + "\"subject\":\"" + subject + "\","
		// //+ "\"goods_detail\":\"" + getJSONString(items) + "\","
		// + "\"body\":\"餐饮消费\","
		// + "\"operator_id\":1,"
		// //+ "\"store_id\":\" " + storeId + "\","
		// //+ "\"terminal_Id\":\"" + terminalId +"\","
		// + "time_expire\":\"" + time_expire
		// +
		// "\"}&charset=utf-8&method=alipay.trade.pay&sign_flag=true&sign_type="
		// + "RSA&timestamp=" + timestamp + "&version=1.0";

		String paramStr = "app_id="
				+ appId
				+ "&biz_content="// "2015050800065426&biz_content="
				+ "{\"out_trade_no\":\""
				+ out_trade_no
				+ "\",\"scene\":\"bar_code\","
				+ "\"auth_code\":\"" + auth_code + "\","
				+ "\"seller_id\":\"\","
				+ "\"total_amount\":" + total_amount
				+ ",\"subject\":\"" + subject
				+ "\","
				+ "\"goods_detail\":" + getJSONString(items) + ","
				+ "\"body\":\"餐饮消费\","
				+ "\"operator_id\":1,"
				+ "\"store_id\":\"" + storeId + "\","
				+ "\"terminal_Id\":\"" + terminalId + "\","
				+ "\"time_expire\":\"" + time_expire
				+ "\"}&charset=utf-8&method=alipay.trade.pay&sign_flag=true&sign_type="
				+ "RSA&timestamp=" + timestamp + "&version=1.0";

		String sign = "";

		if (bUseApatchSigner) {
			sign = RSASignatureApache.sign(paramStr, pkcs8Key);
		} else {
			sign = RSASignature.sign(paramStr, pkcs8Key);
		}

		if (sign == null) {
			return null;
		}

		System.out.println("sign ====== " + sign);

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("sign", sign);
		paramMap.put("sign_type", "RSA");

		System.out.println("key =" + pkcs8Key);

		StringTokenizer tokens = new StringTokenizer(paramStr, "&");
		while (tokens.hasMoreTokens()) {
			String oneToken = tokens.nextToken();

			String as[] = oneToken.split("=");

			paramMap.put(as[0], as[1]);

			System.out.println(as[0] + "=" + as[1]);
		}

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(ALIPAY_GATE_WAY);

		HttpResponse response = httpProtocolHandler.execute(request, "", "");
		if (response == null) {
			return ePayResult;
		}

		String strResult = response.getStringResult();
		System.out.println("ali pay response = " + strResult);
		PrintFile.printResult(strResult, "alipayresponse");
		JSONObject respJsonObj;
		try {
			respJsonObj = new JSONObject(strResult);
			respJsonObj = respJsonObj
					.optJSONObject("alipay_trade_pay_response");

			ePayResult.desc = respJsonObj.optString("sub_msg");

			ePayResult.code = respJsonObj.optString("code");
			ePayResult.qr_code = respJsonObj.optString("qr_code");
			if (EPayResult.PAY_CODE_OK.equals(ePayResult.code)) {
				ePayResult.success = true;
				Log.e("myLog", "支付宝支付完成的时间" + format.format(new Date()));
				PayStatus status = new PayStatus();
				status.setPayStatu("paySuccess");
				EventBus.getDefault().post(status);


			} else if (EPayResult.PAY_CODE_IN_PROGRESS.equals(ePayResult.code)) {
				//轮询查询

				ePayResult = StartWaitingForPayCompletion(out_trade_no);//60秒去查询，如果时间过了就会超时
				if (ePayResult.success==false){
					PayStatus status = new PayStatus();
					status.setPayStatu("payFail");
					EventBus.getDefault().post(status);
				}

			} else if (EPayResult.PAYCLOSED.equals(ePayResult.code)) {
				PayStatus status = new PayStatus();
				status.setPayStatu("payFail");
				EventBus.getDefault().post(status);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ePayResult;
	}

	/**
	 * @param content 内容
	 * @param touser  openid
	 * @param url     url
	 */
	public static void senMsgToWx(String content, String touser, String url) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("content", content);
		Gson                gson     = new Gson();
		String              json     = gson.toJson(hashMap);
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("touser", touser);
		paramMap.put("msgtype", "text");
		paramMap.put("text", json);

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();
		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(url);

		try {
			HttpResponse response = httpProtocolHandler.execute(request, "", "");
			System.out.println("微信推送result：" + response.getStringResult());
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 正扫
	 *
	 * @param appId
	 * @param pkcs8Key
	 * @param out_trade_no
	 * @param total_amount
	 * @param subject
	 * @param items
	 * @param storeId
	 * @param terminalId
	 * @param bUseApatchSigner
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 */
	public static EPayResult AliPayMethod(String appId, String pkcs8Key,
	                                      String out_trade_no, String total_amount, String subject,
	                                      List<AliGoodsItem> items, String storeId, String terminalId,
	                                      boolean bUseApatchSigner) throws HttpException, IOException {

		Log.e("myLog", "Ali支付参数" + ",appId:" + appId + ",pkcs8Key:" + pkcs8Key + ",out_trade_no:" + out_trade_no + ",total_amount:" + total_amount + ",subject:" + subject + ",items:" + items
				.toString() + ",storeId:" + storeId
				+ ",terminalId:" + terminalId + ",bUseApatchSigner:" + bUseApatchSigner);
		EPayResult ePayResult = new EPayResult();

		String time_expire = DateUtils.getDateToString(System
				.currentTimeMillis() + 240 * 1000); // 3分钟超时时间
		String timestamp = DateUtils.getCurrentDate(); // 获取系统当前时间
		// System.out.printf("当前时间：" + timestamp + "\n" + "超时时间：" + time_expire
		// + "\n");

		String paramStr = "app_id="
				+ appId
				+ "&biz_content="// "2015050800065426&biz_content="
				+ "{\"out_trade_no\":\""
				+ out_trade_no
				+ "\","
				+ "\"seller_id\":\"\","
				+ "\"total_amount\":"
				+ total_amount
				+ ",\"subject\":\""
				+ subject
				+ "\","
				+ "\"goods_detail\":"
				+ getJSONString(items)
				+ ","
				+ "\"body\":\"餐饮消费\","
				+ "\"operator_id\":1,"
				+ "\"store_id\":\""
				+ storeId
				+ "\","
				+ "\"terminal_Id\":\""
				+ terminalId
				+ "\","
				+ "\"time_expire\":\""
				+ time_expire
				+ "\"}&charset=utf-8&method=alipay.trade.precreate&sign_flag=true&sign_type="
				+ "RSA&timestamp=" + timestamp + "&version=1.0";

		String sign = "";

		if (bUseApatchSigner) {
			sign = RSASignatureApache.sign(paramStr, pkcs8Key);
		} else {
			sign = RSASignature.sign(paramStr, pkcs8Key);
		}

		// System.out.println("sign ====== " + sign);

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("sign", sign);
		paramMap.put("sign_type", "RSA");

		// System.out.println("key =" + pkcs8Key);

		StringTokenizer tokens = new StringTokenizer(paramStr, "&");
		while (tokens.hasMoreTokens()) {
			String oneToken = tokens.nextToken();

			String as[] = oneToken.split("=");

			paramMap.put(as[0], as[1]);

			// System.out.println(as[0] + "=" + as[1]);
		}

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(ALIPAY_GATE_WAY);

		HttpResponse response = httpProtocolHandler.execute(request, "", "");
		if (response == null) {
			return ePayResult;
		}

		String strResult = response.getStringResult();
		System.out.println("ali pay response = " + strResult);
		PrintFile.printResult(strResult, "alipayresponse2");
		JSONObject respJsonObj;
		try {
			respJsonObj = new JSONObject(strResult);
			// 反扫支付为 alipay_trade_pay_response
			// 正扫支付为 alipay_trade_precreate_response
			respJsonObj = respJsonObj
					.optJSONObject("alipay_trade_precreate_response");

			// 此处拿到正扫二维码字符串
			String qr_code = respJsonObj.optString("qr_code");
			// System.out.println("*****正扫二维码字符串*********"+qr_code);

			ePayResult.desc = respJsonObj.optString("msg");

			ePayResult.code = respJsonObj.optString("code");
			ePayResult.qr_code = qr_code;
			if (EPayResult.PAY_CODE_OK.equals(ePayResult.code)) {
				ePayResult.success = true;
			} else if (EPayResult.PAY_CODE_IN_PROGRESS.equals(ePayResult.code)) {
				//轮询查询
				ePayResult = StartWaitingForPayCompletion(out_trade_no);
				PrintFile.printResult(ePayResult.toString(), "alibarpayaction");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ePayResult;
	}

	private static String getJSONString(List<AliGoodsItem> items) {
		return new Gson().toJson(items);
	}

	public static EPayResult aliCheckPayStatus(String appId, String pkcs8Key,
	                                           String out_trade_no) throws HttpException, IOException {
		EPayResult ePayResult = new EPayResult();
		String     timestamp  = DateUtils.getCurrentDate(); // 获取系统当前时间

		String paramStr = "app_id="
				+ appId
				+ "&biz_content="
				+ "{\"out_trade_no\":\""
				+ out_trade_no
				+ "\"}&charset=utf-8&method=alipay.trade.query&sign_flag=true&sign_type="
				+ "RSA&timestamp=" + timestamp + "&version=1.0";

		String sign = RSASignature.sign(paramStr, pkcs8Key);

		// System.out.println("sign ====== " + sign);

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("sign", sign);
		paramMap.put("sign_type", "RSA");

		// System.out.println("key =" + pkcs8Key);

		StringTokenizer tokens = new StringTokenizer(paramStr, "&");
		while (tokens.hasMoreTokens()) {
			String oneToken = tokens.nextToken();

			String as[] = oneToken.split("=");

			paramMap.put(as[0], as[1]);

			// System.out.println(as[0] + "=" + as[1]);
		}

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(ALIPAY_GATE_WAY);

		HttpResponse response = httpProtocolHandler.execute(request, "", "");
		if (response == null) {
			return ePayResult;
		}

		String strResult = response.getStringResult();
		// System.out.println("ali query response = " + strResult );

		JSONObject respJsonObj;
		try {
			respJsonObj = new JSONObject(strResult);
			respJsonObj = respJsonObj
					.optJSONObject("alipay_trade_query_response");

			ePayResult.desc = respJsonObj.optString("msg");
			ePayResult.trade_status = respJsonObj.optString("trade_status");

			ePayResult.code = respJsonObj.optString("code");
			System.out.println("\n" + "code: " + ePayResult.code  + ",trade_status: "
					+ ePayResult.trade_status+",desc:"+ePayResult.desc);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ePayResult;
	}

	public static EPayResult cancelPayment(String appId, String pkcs8Key,
	                                       String out_trade_no) throws HttpException, IOException {
		EPayResult ePayResult = new EPayResult();
		String     timestamp  = DateUtils.getCurrentDate(); // 获取系统当前时间

		String paramStr = "app_id="
				+ appId
				+ "&biz_content="
				+ "{\"out_trade_no\":\""
				+ out_trade_no
				+ "\"}&charset=utf-8&method=alipay.trade.cancel&sign_flag=true&sign_type="
				+ "RSA&timestamp=" + timestamp + "&version=1.0";

		String sign = RSASignature.sign(paramStr, pkcs8Key);

		System.out.println("sign ====== " + sign);

		Map<String, String> paramMap = new HashMap<String, String>();

		paramMap.put("sign", sign);
		paramMap.put("sign_type", "RSA");

		System.out.println("key =" + pkcs8Key);

		StringTokenizer tokens = new StringTokenizer(paramStr, "&");
		while (tokens.hasMoreTokens()) {
			String oneToken = tokens.nextToken();

			String as[] = oneToken.split("=");

			paramMap.put(as[0], as[1]);

			System.out.println(as[0] + "=" + as[1]);
		}

		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler
				.getInstance();

		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(AlipayConfig.input_charset);

		request.setParameters(generatNameValuePair(paramMap));
		request.setUrl(ALIPAY_GATE_WAY);

		HttpResponse response = httpProtocolHandler.execute(request, "", "");
		if (response == null) {
			return ePayResult;
		}

		String strResult = response.getStringResult();
		System.out.println("ali cancel response = " + strResult);

		JSONObject respJsonObj;
		try {
			respJsonObj = new JSONObject(strResult);
			respJsonObj = respJsonObj
					.optJSONObject("alipay_trade_cancel_response");

			ePayResult.desc = respJsonObj.optString("msg");

			ePayResult.code = respJsonObj.optString("code");
			if (EPayResult.PAY_CODE_OK.equals(ePayResult.code)) {
				ePayResult.success = true;
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ePayResult;
	}


	public static EPayResult StartWaitingForPayCompletion(String orderNo) {

		EPayResult result  = new EPayResult();
		boolean    hasPush = false;

		try {
			long start = System.currentTimeMillis();
			SystemClock.sleep(1000);
			while (System.currentTimeMillis() - start < 60 * 1000)// 20
			// seconds
			{
				SystemClock.sleep(1000);
				result = AliBarPayAction.aliCheckPayStatus(AlipayConfig.APPID,
						AlipayConfig.key, orderNo);

				if (EPayResult.TRADE_SUCCESS
						.equalsIgnoreCase(result.trade_status)) {
					result.success = true;
					return result;
				} else if (EPayResult.PAY_STATUS_WAIT
						.equalsIgnoreCase(result.trade_status)) {
					if (!hasPush) {
						PayStatus status = new PayStatus();
						status.setPayStatu("USERPAYING");
						EventBus.getDefault().post(status);
						hasPush = true;
					}
					continue;
				} else if (EPayResult.PAYCLOSED
						.equalsIgnoreCase(result.trade_status)) {
					PayStatus status = new PayStatus();
					status.setPayStatu("PAYCLOSED");
					EventBus.getDefault().post(status);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
