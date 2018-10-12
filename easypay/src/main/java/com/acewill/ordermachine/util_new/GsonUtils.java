package com.acewill.ordermachine.util_new;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 功能描述：使用Gson解析Json数据信息的工具类
 *
 * @author jiangyizhao@huaxunchina.cn
 * @date 2014年7月22日 上午8:59:31
 */
public class GsonUtils {

	public static String getCode(String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     code       = jsonobject.getString("code");
		return code;
	}

	public static String getResultcode(String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     resultcode = jsonobject.getString("resultcode");
		return resultcode;
	}

	public static String getReason(String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     reason     = jsonobject.getString("reason");
		return reason;
	}

	public static String getErrCode(String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     code       = jsonobject.getString("errCode");
		return code;
	}

	public static String getObj(String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     code       = jsonobject.getString("obj");
		return code;
	}

	public static String getMessage(String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     message    = jsonobject.getString("message");
		return message;
	}

	public static String getErrMsg(String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     message    = jsonobject.getString("errmsg");
		return message;
	}

	public static String getMsg(String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     message    = jsonobject.getString("msg");
		return message;
	}

	public static String getData(String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     data       = jsonobject.getString("data");
		return data;
	}

	public static String getResult(String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     result     = jsonobject.getString("result");
		return result;
	}

	public static String getData(String key, String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     data       = jsonobject.getString(key);
		return data;
	}

	public static boolean getBooleanData(String key, String json)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		boolean    data       = jsonobject.getBoolean(key);
		return data;
	}

	public static String getJson(String json, String key)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		String     data       = jsonobject.getString(key);
		if (data.equals("null") || data == null) {
			data = "";
		}
		return data;
	}

	public static long getContent(String json, String key)
			throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		long       contentId  = jsonobject.getLong(key);
		return contentId;
	}

	/**
	 * 功能描述：把JSON数据转换成普通字符串列表
	 *
	 * @param jsonData JSON数据
	 * @return
	 * @throws Exception
	 */
	public static List<String> getStringList(String jsonData)
			throws Exception {
		return new Gson().fromJson(jsonData, new TypeToken<List<String>>() {
		}.getType());
	}

	/**
	 * 功能描述：把JSON数据转换成指定的java对象
	 *
	 * @param jsonData JSON数据
	 * @param clazz    指定的java对象
	 * @return
	 * @throws Exception
	 */
	public static <T> T getSingleBean(String jsonData, Class<T> clazz)
			throws Exception {
		return new Gson().fromJson(jsonData, clazz);
	}

	/**
	 * 功能描述：把JSON数据转换成指定的java对象列表
	 *
	 * @param jsonData JSON数据
	 * @return
	 * @throws Exception
	 */
	public static <T> List<T> getBeanList(String jsonData)
			throws Exception {
		return new Gson().fromJson(jsonData, new TypeToken<List<T>>() {
		}.getType());
	}

	public static <T, clazz> List<T> getBeanList(String jsonData, Class<T> clazz)
			throws Exception {
		return new Gson().fromJson(jsonData, new TypeToken<List<clazz>>() {
		}.getType());
	}

	/**
	 * 功能描述：把JSON数据转换成较为复杂的java对象列表
	 *
	 * @param jsonData JSON数据
	 * @return
	 * @throws Exception
	 */
	public static List<Map<String, Object>> getBeanMapList(String jsonData)
			throws Exception {
		return new Gson().fromJson(jsonData,
				new TypeToken<List<Map<String, Object>>>() {
				}.getType());
	}


	public static String objectToString(Object object) {
		return new Gson().toJson(object);

	}

	public static int getIntData(String key, String json) throws JSONException {
		JSONObject jsonobject = new JSONObject(json);
		int        data       = jsonobject.getInt(key);
		return data;
	}


	//	public static StringEntity objectToJson(Object object){
	//		String json = new Gson().toJson(object);
	//		System.out.println("post:"+json);
	//		StringEntity entity = null;
	//	try {
	//		entity = new  StringEntity(json,"UTF-8");
	//		} catch (UnsupportedEncodingException e)
	//		{
	//			e.printStackTrace();
	//		}
	//		return entity;
	//	}


}
