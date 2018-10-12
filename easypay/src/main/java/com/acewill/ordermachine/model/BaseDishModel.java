//package com.acewill.ordermachine.model;
//
//import android.text.TextUtils;
//
//import org.litepal.crud.DataSupport;
//
//import java.io.Serializable;
//
//public class BaseDishModel extends DataSupport implements Serializable {
//
//
//	public int getAlreadyCount() {
//		return quantity;
//	}
//
//	public boolean isPackage() {
//		return isPackage == 1;
//	}
//
//	public boolean containsPrint(String printerId) {
//		if (TextUtils.isEmpty(printerStr)) {
//			return false;
//		}
//		return printerStr.contains(printerId);
//	}
//
//	public String getRemarks() {
//		return remarks;
//	}
//
//	public int getDishCount() {
//		return 1;
//	}
//
//}
