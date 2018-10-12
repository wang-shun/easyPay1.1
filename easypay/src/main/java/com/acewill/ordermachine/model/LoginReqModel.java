package com.acewill.ordermachine.model;

import android.text.TextUtils;

public class LoginReqModel{
	public static String appid = "";// ：商户ID，String
	public static String brandid = "";// ：品牌ID，int
	public static String storeid = "";// ： 门店ID， int
	public static String tname = "";// ： 终端名称， String
	public static String sname = "";// ： 终端名称， String


	public static boolean checkParams(){
		if(TextUtils.isEmpty(appid)){
			return false;
		}
		if(TextUtils.isEmpty(brandid)){
			return false;
		}
		if(TextUtils.isEmpty(storeid)){
			return false;
		}
		if(TextUtils.isEmpty(tname)){
			return false;
		}
		return true;
	}

	public static String getAppid() {
		return appid;
	}

	public static void setAppid(String appid) {
		LoginReqModel.appid = appid;
	}

	public static String getBrandid() {
		return brandid;
	}

	public static void setBrandid(String brandid) {
		LoginReqModel.brandid = brandid;
	}

	public static String getStoreid() {
		return storeid;
	}

	public static void setStoreid(String storeid) {
		LoginReqModel.storeid = storeid;
	}

	public static String getTname() {
		return tname;
	}

	public static void setTname(String tname) {
		LoginReqModel.tname = tname;
	}
}
