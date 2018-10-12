package com.acewill.ordermachine.activity;

import com.acewill.ordermachine.dialog.ErrorDialog;
import com.acewill.ordermachine.enum_c.PayStatu;

/**
 * Author：Anch
 * Date：2017/4/25 16:04
 * Desc：
 */
public interface PayOrderView {
	String getCost();

	void refreshUI();//下单成功了，刷新一下界面

	void calculate_clear();

	void showKaiBanDialog();

	void showPayLoadingDialog();

	void showLoadingDialog();

	void dismissLoadingDialog();

	void dismissPayLoadingDialog(PayStatu type, String reason);

	void showPayInputDialog(int type);

	void generateTerminalOrderId();

	int getTerminalOrderId();

	void openBox();

	ErrorDialog showErrorTipsDialog(int type, String name, boolean showRadioBtn);

	void dismissPayInputDialog();

	void showFuPing(String title, String content);

	void changePayTips(String s);
}
