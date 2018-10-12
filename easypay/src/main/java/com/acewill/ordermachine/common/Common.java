package com.acewill.ordermachine.common;

import android.content.Context;

import com.acewill.chikenprintlibrary.printer.Printer;
import com.acewill.ordermachine.model.DishKind;
import com.acewill.ordermachine.model.DishModel;
import com.acewill.ordermachine.model.LoginReqModel;
import com.acewill.ordermachine.model.StoreInfoModel;
import com.acewill.ordermachine.model.WorkShift;
import com.acewill.ordermachine.model.WorkShiftInfo;
import com.acewill.ordermachine.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class Common {//假设配置
	public static final int PRINTER_RUSH_DISH             = 0X29;
	public static final int PRINTER_EXTRA_KITCHEN_RECEIPT = 0X33;
	// public static String BASE_IP = "192.168.1.109";
	// public static String port = "8080";

	// public static String BASE_URL =
	// "http://192.168.1.109:8080/AcewillPosServer/";
	//	public static int LAYOUT_TYPE = 1;
	public static       int            LAYOUT_TYPE_LIST  = 1;
	public static       int            LAYOUT_TYPE_GRID  = 2;
	public static       int            LAYOUT_TYPE_GRID3 = 3;
	/**
	 * 扫码支付
	 */
	public static final int            SCAN_PAY          = 1;
	/**
	 * 刷卡支付
	 */
	public static final int            CODE_PAY          = 2;
	public static final int            PRINT_1           = 1;
	public static final int            PRINT_2           = 2;
	public static       int            DISH_VERSION_NUM  = 0;
	public static       int            LANGUAGE          = 0;
	public static final int            CHINESE           = 0;
	public static final int            ENGLISH           = 1;
	public final static String         errMsg            = "网络错误";
	public static       int            printer_version   = PRINT_1;//默认是一代，这个数据没有由后台配置
	public static       boolean        printer1Init      = false;
	public static       boolean        printer2Init      = true;
	public static       StoreInfoModel SHOP_INFO         = new StoreInfoModel();//门店信息
	public static Printer   SHOP_PRINTER;//门店信息
	public static DishModel dishModel;

	/**
	 * 退菜客用打印的标识
	 */
	public static final int PRINTER_RETREAT_DISH_GUEST = 0X65;
	/**
	 * 退菜打印的标识
	 */
	public static final int PRINTER_RETREAT_DISH       = 0X28;
	/**
	 * 下单打印厨房小票的标识
	 */
	public static final int PRINTER_KITCHEN_ORDER      = 0X25;


	public static int DAY_OF_WEEK;

	public static int screenWidth;
	public static int ScreenProtectTime = 8;//自动上传订单的时间（单位 秒）

	public static int currentScreentProtectTime = -1;//屏保倒计时时间

	public static String PRINTER_PRODUCTID;
	public static String background;

	public static List<WorkShiftInfo.ContentBean> workShifIno;
	public static WorkShift                       workShifit;
	public static List<DishKind>                  dishKindList = new ArrayList<>();

	static {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		DAY_OF_WEEK = c.get(Calendar.DAY_OF_WEEK);
	}


	/**
	 * 初始化门店的信息，拿到登陆存的sp数据去赋值，如果赋值成功，说明登录成功
	 *
	 * @param context
	 * @return
	 */
	public static boolean initInfo(Context context) {
		Common.SHOP_INFO = SharedPreferencesUtil.getShopInfo(context);
		return LoginReqModel.checkParams();
	}

	public static boolean isPrintOk = true;

}
