package com.acewill.ordermachine.activity;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.acewill.chikenprintlibrary.model.WorkShiftReport;
import com.acewill.ordermachine.OnCancleClickListener;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.dialog.ErrorDialog;
import com.acewill.ordermachine.enum_c.PayStatu;
import com.acewill.ordermachine.eventbus.OnFinishEvent;
import com.acewill.ordermachine.eventbus.PayStatus;
import com.acewill.ordermachine.model.AliPayResult;
import com.acewill.ordermachine.model.AliPayResult3;
import com.acewill.ordermachine.model.DishKind;
import com.acewill.ordermachine.model.DishModel;
import com.acewill.ordermachine.model.OrderReq;
import com.acewill.ordermachine.model.Payment;
import com.acewill.ordermachine.model.PaymentInfo;
import com.acewill.ordermachine.pos.NewPos;
import com.acewill.ordermachine.printadapter.PosKitchenPrintAdapter;
import com.acewill.ordermachine.util.PayUtil;
import com.acewill.ordermachine.util.PriceUtil;
import com.acewill.ordermachine.util.SharedPreferencesUtil;
import com.acewill.ordermachine.util_new.GsonUtils;
import com.acewill.ordermachine.util_new.PrintFile;
import com.acewill.ordermachine.util_new.ToastUtils;
import com.acewill.paylibrary.MicropayTask;
import com.acewill.paylibrary.PayReqModel;
import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.epay.EPayResult;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;

/**
 * Author：Anch
 * Date：2017/4/25 16:03
 * Desc：
 */
public class EasyPayPresenter implements OnCancleClickListener {

	private static final String TAG = "myLog";

	private Context      mContext;
	private PayOrderView mPayOrderView;

	private PayReqModel payReqModel;
	private ShuaKaTask  myMicropayTask;


	public EasyPayPresenter(Context context, PayOrderView view) {
		this.mContext = context;
		this.mPayOrderView = view;

	}

	SimpleDateFormat format3 = new SimpleDateFormat("yyyyMMddHHmmss");
	private boolean isInit = false;

	public void onGunScanSuccess(String code) {
		if (!isInit) {
			return;
		}

		isInit = false;
		mPayOrderView.showPayLoadingDialog();
		PrintFile
				.printResult2("Class_PayOrderPresenter_Method_startWithRetries()PayCode:" + code + "——————————————\n");
		//微信和支付宝各自支付
		if (NewPos.HAVA_SWIFTPASS) {

			PayReqModel.currentPayName = PayReqModel.PTID_SSS_SWIFTPASSSTR;
			PayReqModel.currentPayNameStr = PayReqModel.PTID_SSS_SWIFTPASSSTR;
			PayReqModel.currentPayType = PayReqModel.PTID_SSS_SWIFTPASS;
			final String mch_create_ip = "192.168.0.101";
			String total_fee = PriceUtil
					.multiplynofloat(new BigDecimal(payCost), new BigDecimal(100))
					.toString();
			payReqModel = PayUtil
					.getPayReqModel(PayReqModel.currentPayType, formatCode(code),
							total_fee, PayReqModel.currentPayName, bis_id);//将这个string 封装
			//如果有威富通支付，就采用威富通支付，不管用户选择微信支付还是支付宝支付

			NewPos.getInstance(mContext)
					.swiftPassPay(code, "智慧食堂", mch_create_ip, total_fee, bis_id, new StringCallback() {
						@Override
						public void onError(Call call, Exception e, int id) {
							ToastUtils.showToast(mContext, "网络异常,支付失败!");
							PrintFile
									.printResult2("Class_PayOrderPresenter_Method_swiftPassPay()_onError():\n" + e
											.getMessage() + "\n");
							if (mPayOrderView != null) {
								payFail(Common.errMsg + "，支付失败!");
							}//威富通支付失败，然后就关闭dialog
						}

						@Override
						public void onResponse(final String response, int id) {
							// TODO: 2017/5/5 anch 这个是支付返回的结果,用着结果来下单
							PrintFile
									.printResult2("Class_PayOrderPresenter_Method_swiftPassPay()_onResponse():\n" + response + "\n");

							try {
								if ("0".equals(GsonUtils.getResult(response))) {

									String data = GsonUtils.getData(response);
									String query = GsonUtils
											.getData("need_query", data);
									boolean success = GsonUtils
											.getBooleanData("success", data);
									String message = GsonUtils.getMessage(data);
									String errcode = GsonUtils.getData("errcode", data);
									//如果
									if (success) {
										//不需要查询,表示已经支付成功
										paySuccess();
									} else if (!success && "false".equals(query)) {
										//支付失败了
										PrintFile
												.printResult2("威富通支付失败0————————————" + "\n");
										payFail(message);
									} else if (!success && !TextUtils
											.isEmpty(errcode) && "true"
											.equals(query)) {
										//errcode,
										if (errcode
												.equals("USERPAYING") || errcode
												.equals("10003")) {
											PayStatus status = new PayStatus();
											status.setPayStatu("USERPAYING");
											EventBus.getDefault().post(status);
											//用户需要输入密码
											handler.postDelayed(new Runnable() {
												@Override
												public void run() {
													try {
														String data =
																GsonUtils.getData("data", response);
														String transaction_id =
																GsonUtils
																		.getData("transaction_id", data);
														//轮询结果
														queryResult();
													} catch (JSONException e) {
														payFail("支付失败");
														e.printStackTrace();
													}
												}
											}, 2000);
										} else {
											//支付失败了
											PrintFile
													.printResult2("威富通支付失败1————————————" + "\n");
											ToastUtils.showToast(mContext, message);
											payFail("支付失败");
										}
									} else {
										PrintFile
												.printResult2("威富通支付失败2————————————" + "\n");
										ToastUtils.showToast(mContext, message);
										payFail("支付失败");
									}
								} else {
									PrintFile
											.printResult2("威富通支付失败6————————————" + "\n");
								}
							} catch (JSONException e) {
								PrintFile
										.printResult2("威富通支付失败7————————————:\n" + e
												.getMessage() + "\n");
								e.printStackTrace();
							}
						}
					});
		} else {
			if (NewPos.HAVA_WX && code.startsWith("13") && code.length() > 11) {
				PayReqModel.currentPayType = PayReqModel.PTID_SSS_WEIXIN;
				PayReqModel.currentPayName = PayReqModel.PTID_SSS_WEIXINSTR;
			} else if (NewPos.HAVA_ALI && code.startsWith("28") && code.length() > 11) {
				PayReqModel.currentPayType = PayReqModel.PTID_SSS_ALI;
				PayReqModel.currentPayName = PayReqModel.PTID_SSS_ALISTRSTR;
			} else {
				payFail("支付方式不正确!");
				return;
			}

			payReqModel = PayUtil
					.getPayReqModel(PayReqModel.currentPayType, formatCode(code), payCost, PayReqModel.currentPayName, bis_id
					);//将这个string 封装


			if (PayReqModel.currentPayType == PayReqModel.PTID_SSS_ALI) {
				PaymentInfo info = new PaymentInfo();
				info.appIDs = AlipayConfig.APPID;
				info.keyStr = AlipayConfig.key;
				info.appsecret = AlipayConfig.appsecret;
				payReqModel.sPaymentInfo = new Gson().toJson(info);
				NewPos.getInstance(mContext).aliFaceToFacePay(payReqModel,new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						payFail(e.getMessage());
						e.printStackTrace();
					}

					@Override
					public void onResponse(String response, int id) {
						try {
							AliPayResult result = GsonUtils.getSingleBean(GsonUtils
									.getData("content", response), AliPayResult.class);
							AliPayResult.AlipayTradePayResponseBean bean = result
									.getAlipay_trade_pay_response();
							if (bean != null) {
								if (bean.getCode().equals("10000") && "Success"
										.equals(bean.getMsg())) {
									paySuccess();
									//									onLinePaySuccess(bean.getTrade_no());
								} else if (bean.getCode()
										.equals("40004") && "ACQ.PAYMENT_AUTH_CODE_INVALID"
										.equals(bean.getSub_code())) {
									payFail(bean.getSub_msg());
								} else if (bean.getCode()
										.equals("10003")) {
									//正在输入密码
									payInProgress();
								} else {
									//未确定其他的
									payFail(bean.getCode() + "," + bean.getMsg() + "," + bean
											.getSub_code() + "," + bean
											.getSub_msg());
								}
							} else {
								payFail("result==null");
							}
						} catch (Exception e) {
							payFail(e.getMessage());
							e.printStackTrace();
						}
					}
				});
			} else {
				myMicropayTask = new ShuaKaTask();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							myMicropayTask.get(60000, TimeUnit.MILLISECONDS);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						} catch (TimeoutException e) {
							e.printStackTrace();
						}
					}
				}).start();

				myMicropayTask
						.execute(payReqModel);//将该东西进行各种各样的操作 ，然后再最后的doInBackGround里面去得到结果
			}

		}
	}

	private void payInProgress() {
		mPayOrderView.changePayTips("请提示用户输入密码!");
		handler.postDelayed(excuteAliQueryRunnable, 5000);
	}

	public void removeQueryAliPayRunnable() {
		handler.removeCallbacks(excuteAliQueryRunnable);
	}

	/**
	 * 支付宝支付，重复查询结果
	 */
	private Runnable excuteAliQueryRunnable = new Runnable() {
		@Override
		public void run() {
			queryResult3();
		}
	};

	private void queryResult3() {
		NewPos.getInstance(mContext)
				.queryAlipayResult(payReqModel,
						new StringCallback() {
							@Override
							public void onError(okhttp3.Call call, Exception e, int id) {
								payFail("SYSTEM_ERROR");
								e.printStackTrace();
							}

							@Override
							public void onResponse(final String response, int id) {
								//// TODO: 2017/5/5 anch
								//查询判断威富通支付究竟有没有成功(这个是去后台去获取的)，如果成功了就下单
								try {

									AliPayResult3 result = GsonUtils.getSingleBean(GsonUtils
											.getData("content", response), AliPayResult3.class);
									AliPayResult3.AlipayTradeQueryResponseBean bean = result
											.getAlipay_trade_query_response();
									if (bean != null) {
										if (bean.getCode().equals("10000") && "TRADE_SUCCESS"
												.equals(bean.getTrade_status())) {
											paySuccess();
											handler.removeCallbacks(excuteAliQueryRunnable);
										} else if ("10000".equals(bean.getCode()) && "TRADE_CLOSED"
												.equals(bean.getTrade_status())) {
											payFail("交易超时");
										} else {
											if (countTime <= 17) {
												countTime++;
												handler.postDelayed(excuteAliQueryRunnable, 5000);
											} else if (countTime == 18 && "10000"
													.equals(bean.getCode()) && "WAIT_BUYER_PAY"
													.equals(bean.getTrade_status())) {
												countTime = 6;
												handler.postDelayed(excuteAliQueryRunnable, 5000);
											} else {
												payFail("TIME_OUT");
											}
										}
									} else {
										if (countTime <= 17) {
											countTime++;
											handler.postDelayed(excuteAliQueryRunnable, 5000);
										} else {
											payFail("TIME_OUT");
										}
									}
								} catch (Exception e) {
									if (mPayOrderView != null) {
										payFail("TIME_OUT");
									}
									e.printStackTrace();
								}
							}
						});
	}

	private int      countTime = 1;
	private Runnable rr        = new

			Runnable() {
				@Override
				public void run() {
					if (countTime <= 18) {
						countTime++;
						PrintFile
								.printResult2("轮询第" + countTime + "次" + "\n");
						queryResult();
					} else {
						PrintFile
								.printResult2("威富通支付失败3,times up————————————" + "\n");
						payFail("支付超时!");
					}
				}
			};

	private void queryResult() {
		NewPos.getInstance(mContext)
				.getSwiftPassPayResult(bis_id, new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {
						PrintFile.printResult2("Class_PayOrderPresenter_Method_queryResult()异常:" + e
								.getMessage() + "\n");
						if (e.getMessage() == null) {
							handler.postDelayed(rr, 5000);
						}
					}

					@Override
					public void onResponse(final String response, int id) {
						//// TODO: 2017/5/5 anch
						//查询判断威富通支付究竟有没有成功(这个是去后台去获取的)，如果成功了就下单
						PrintFile
								.printResult2("Class_PayOrderPresenter_Method_queryResult()结果:\n" + response + "\n");
						try {
							String data = GsonUtils.getData("data", response);
							String need_query = GsonUtils
									.getData("need_query", data);
							boolean success = GsonUtils
									.getBooleanData("success", data);
							final String message = GsonUtils.getMessage(data);
							String       errcode = GsonUtils.getData("errcode", data);


							if (success) {
								//						getOrderId();//威富通支付成功
								paySuccess();
							} else if (!success && "true"
									.equals(need_query)) {
								if (!TextUtils.isEmpty(errcode))
									if ("USERPAYING".equals(errcode) || "10003".equals(errcode)) {
										handler.postDelayed(rr, 5000);
									} else {
										//支付失败了
										PrintFile.printResult2("威富通支付失败4——————————" + "\n");
										ToastUtils.showToast(mContext, message);
										payFail("支付失败");
									}

							} else {
								//支付失败了
								PrintFile.printResult2("威富通支付失败5——————————" + "\n");
								ToastUtils.showToast(mContext, message);
								payFail("支付失败");
							}

						} catch (JSONException e) {
							if (mPayOrderView != null) {
							}
							e.printStackTrace();
						}
					}
				});
	}

	protected void doMemberLogin(String code) {
		Log.e(TAG, "会员支付开始时间:" + format3.format(new Date()));
		mPayOrderView.showPayLoadingDialog();
		new NewPos().memberLogin(code, new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				payFail("支付失败");
				e.printStackTrace();
			}

			@Override
			public void onResponse(String response, int id) {

				try {

					int result = GsonUtils.getIntData("result", response);
					if (result == 0) {
						WshAccountRes res = GsonUtils
								.getSingleBean(response, WshAccountRes.class);
						currentAcctount = res.content.get(0);
						//第二步支付
						doMemberPay(currentAcctount);
					} else {
						payFail(GsonUtils.getData("errmsg", response));
						payinit(true);
					}
				} catch (Exception e) {
					mPayOrderView.dismissPayLoadingDialog(PayStatu.FAILE, Common.errMsg + ",支付失败");
					mPayOrderView.dismissPayInputDialog();
					e.printStackTrace();
				}
			}
		});
	}

	private WshAccount currentAcctount;
	private String     bis_id;

	private void doMemberPay(WshAccount account) {
		//第一步预览
		final WshCreateDeal.Request request = new WshCreateDeal.Request();
		request.setBiz_id(bis_id);
		String cost = payCost;
		if (PriceUtil.subtract(new BigDecimal(account.getBalance()), new BigDecimal(cost))
				.floatValue() < 0) {
			//储值余额还足够
			payFail("储值余额不足");
			return;
		}
		request.setConsume_amount(PriceUtil
				.multiply(new BigDecimal(cost), new BigDecimal(100)).intValue());//分
		request.setCount_num(1);
		request.setPayment_amount(0);
		request.setPayment_mode(1);
		request.setSub_balance(PriceUtil.multiply(new BigDecimal(cost), new BigDecimal(100))
				.intValue());
		request.setSub_credit(0);
		request.setRemark("消费预览");
		request.setCno(account.getUno()); //卡号
		request.setUid(account.getUid());
		new NewPos().createDeal(request, new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
				payFail("支付失败");
			}

			@Override
			public void onResponse(String response, int id) {

				try {
					int result = GsonUtils.getIntData("result", response);
					if (result == 0) {
						MemberPayResultBean bean = GsonUtils
								.getSingleBean(response, MemberPayResultBean.class);
						String tcId = bean.getContent().getTcId();
						//结账
						goMemberPay();
					} else {
						payFail(GsonUtils.getErrMsg(response));
						payinit(true);
					}
				} catch (Exception e) {
					payFail("支付失败");
					e.printStackTrace();
				}
			}
		});
	}

	private void goMemberPay() {
		new NewPos().commitWshDeal(bis_id, "000000", new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
				backOutOrder();
			}

			@Override
			public void onResponse(String response, int id) {
				try {
					if ("0".equals(GsonUtils.getResult(response))) {
						paySuccess();
						mPayOrderView.dismissPayInputDialog();
					} else {
						payFail(GsonUtils.getErrMsg(response));
						payinit(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
					backOutOrder();
				}
			}
		});
	}

	public List<Payment> createPayment() {
		List<Payment> paymentList = new ArrayList<>();
		Payment       payment     = new Payment();
		payment.setValue(payCost);
		payment.setPaymentTypeId(PayReqModel.currentPayType + "");
		payment.setPaidAt(System.currentTimeMillis() + "");
		payment.setCreatedAt(System.currentTimeMillis() + "");
		payment.setPaymentNo(bis_id);
		payment.setPaymentTypeName(PayReqModel.currentPayName);
		payment.setTransaction_no(bis_id);
		payment.setOperation("PAY");
		paymentList.add(payment);
		return paymentList;
	}

	/**
	 * 退款
	 */
	private void backOutOrder() {
		NewPos.getInstance(mContext)
				.backOut(payCost, bis_id, PayReqModel.currentPayType + "", new StringCallback() {
					@Override
					public void onError(okhttp3.Call call, Exception e, int id) {
						e.printStackTrace();
						payFail(Common.errMsg);
					}

					@Override
					public void onResponse(String response, int id) {
						payFail("支付失败，已退款，请提示用户查收!");
					}
				});
	}

	public void userDefindedPay(int id, String name) {
		if (payinit(false)) {
			if (SharedPreferencesUtil.getifPrintTicket(mContext) && SharedPreferencesUtil
					.getCheckPrintBeforeNewOrder(mContext)) {
				if (Common.isPrintOk) {
					//					checkPrinter(PayReqModel.PTID_CASH);
					mPayOrderView.showPayLoadingDialog();
					PayReqModel.currentPayName = name;
					PayReqModel.currentPayType = id;
					paySuccess();
				} else {
					currentPay = PayReqModel.PTID_CASH;
					ErrorDialog dialog = mPayOrderView.showErrorTipsDialog(0, "", true);
					dialog.setCancelable(false);
					dialog.setonCancleClickListener(EasyPayPresenter.this);
				}
			} else {
				mPayOrderView.showPayLoadingDialog();
				PayReqModel.currentPayName = PayReqModel.PTID_CASHSTR;
				PayReqModel.currentPayType = PayReqModel.PTID_CASH;
				paySuccess();
			}
		}
	}


	/**
	 * 刷卡支付
	 *
	 * @author Administrator
	 */
	class ShuaKaTask extends MicropayTask {

		@Override
		protected void onPostExecute(EPayResult result) {//得到支付的结果，然后对该结果进行处理

			if (result != null) {
				//用户没有输入密码的时候 走了这条路径

			} else {
				payFail("支付失败");
			}
			if (result != null && result.success) {
				//				//如果这个是开启了循环了的话，回调还是会到这里来的
				//				if (result.isLoop) {
				//					mHandler.sendEmptyMessage(PAY_SUCCESS);
				//				}
				//
				//				//这个比较慢，我就不用这个处理了
				//				//				mHandler.sendEmptyMessage(PAY_SUCCESS);
				//				//				Log.e(TAG, "微信支付成功后回调的时间:" + format.format(new Date()));
				//				Log.e("myLog", "支付回调到ShuaKaTask" + format.format(new Date()));
			} else {
				payFail("支付失败");
			}
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
		}
	}

	//	private Handler mHandler = new Handler(new Handler.Callback() {
	//		@Override
	//		public boolean handleMessage(Message msg) {
	//			return false;
	//		}
	//	});

	public void payFail(String reason) {
		mPayOrderView.dismissPayInputDialog();
		Log.e(TAG, "支付失败的回调时间:" + format3.format(new Date()));
		isInit = false;
		mPayOrderView.dismissPayLoadingDialog(PayStatu.FAILE, reason);
	}


	public void paySuccess() {
		Log.e(TAG, "支付成功后界面变化的时间:" + format3.format(new Date()));
		mPayOrderView.dismissPayLoadingDialog(PayStatu.SUCCESS, "");
		isInit = false;
		//存本地订单
		OrderReq req = saveOrder();
		mPayOrderView.refreshUI();
		//调用打印服务
		if (SharedPreferencesUtil.getifPrintTicket(mContext))
			printTicket(req);
		mPayOrderView.calculate_clear();
		mPayOrderView.generateTerminalOrderId();
		String title   = null;
		String content = null;
		if (Integer.parseInt(req.getPaymentList().get(0)
				.getPaymentTypeId()) == PayReqModel.PTID_SSS_MEMBER_CHUZHI && currentAcctount != null) {
			String price1 = PriceUtil.divide(currentAcctount.getBalance() + "", "100")
					.toString();
			String leftBalance = PriceUtil.subtract(price1, req.getCost()).toString();
			title = req.getMemberName();
			content = "剩余 " + PriceUtil.formatPrice(leftBalance) + " 元";
			mPayOrderView.showFuPing(title, content);//0表示消费，1表示显示
		}
		Log.e(TAG, "所有流程跑完的时间:" + format3.format(new Date()));
	}


	private ArrayList<DishModel> getItemList() {
		ArrayList<DishModel> dishList = new ArrayList<>();
		DishModel            model    = clone();
		dishList.add(model);
		return dishList;
	}

	public DishModel clone() {
		DishModel dishModel = new DishModel();
		dishModel.setDishName(Common.dishModel.getDishName());
		dishModel.setPrice(Common.dishModel.getPrice());
		dishModel.setDishID(Common.dishModel.getDishID());
		dishModel.setDishUnit(Common.dishModel.getDishUnit());
		dishModel.setQuantity(1);
		dishModel.setCost(payCost);
		dishModel.setDishKindStr(getDishKindStrByKindId(Common.dishModel.getDishKind()));
		dishModel.setDishKind(Common.dishModel.getDishKind());
		dishModel.setOrderreq(Common.dishModel.getOrderreq());
		return dishModel;
	}

	public String getDishKindStrByKindId(String kindId) {
		for (DishKind kind : Common.dishKindList) {
			if (kindId.equals(kind.kindID)) {
				return kind.kindName;
			}
		}
		return null;
	}

	private OrderReq saveOrder() {
		ArrayList<DishModel> list = getItemList();
		DataSupport.saveAll(list);
		List<Payment> paymentList = createPayment();
		DataSupport.saveAll(paymentList);
		OrderReq orderReq = new OrderReq();// 用于下单
		orderReq.setWorkShiftId(Common.workShifit.getId());
		orderReq.setBis_id(bis_id);
		orderReq.setUserphone(Common.SHOP_INFO.userphone);
		orderReq.setRealname(Common.SHOP_INFO.realname);
		//原总价
		orderReq.setTotal("0.01");
		//这是计算优惠方案之后实际支付的价格
		orderReq.setComment("");
		orderReq.setCost(payCost);
		long   l    = System.currentTimeMillis();
		String time = String.valueOf(l);
		//这是传给后台的格式不能改
		orderReq.setCreatedAt(time);
		orderReq.setCreatedtime(l);
		orderReq.setTerminalOrderId(mPayOrderView.getTerminalOrderId());
		orderReq.setPaidAt(time);
		orderReq.setCustomerAmount("0");
		orderReq.setDiscount("1");//菜品数量
		if (Integer.parseInt(paymentList.get(0)
				.getPaymentTypeId()) == PayReqModel.PTID_SSS_MEMBER_CHUZHI && currentAcctount != null) {
			orderReq.setMemberBizId(bis_id);
			orderReq.setMemberGrade(currentAcctount.getGradeName() + "");
			orderReq.setMemberName(currentAcctount.getName());
			orderReq.setMemberPhoneNumber(currentAcctount.getPhone());
			orderReq.setMemberid(currentAcctount.getUno());
			orderReq.setuActualNo(currentAcctount.getUActualNo());
		}
		orderReq.setPaymentList(paymentList);
		orderReq.setItemList(list);
		orderReq.setOrderType("EAT_IN");
		orderReq.setPaymentStatus("PAYED");
		orderReq.setSource(Common.SHOP_INFO.tname);
		orderReq.setSubtraction("0");
		orderReq.setTableId(0);
		orderReq.setTotal(payCost);
		orderReq.setPaymentTypeName(paymentList.get(0).getPaymentTypeName());
		orderReq.save();
		ContentValues values = new ContentValues();
		values.put("myid", orderReq.getId());
		DataSupport.update(OrderReq.class, values, orderReq.getId());
		return orderReq;
	}


	private void printTicket(OrderReq req) {
		PosKitchenPrintAdapter.getInstance()
				.print2(req);
		if (PayReqModel.cashbox) {
			mPayOrderView.openBox();
			PayReqModel.cashbox = false;
		}
	}

	private String formatCode(String code) {
		if (code == null) {
			return "";
		}
		return code.trim();
	}


	public void stopTimer() {
		cancerPay();
	}

	public void cancerPay() {
		if (myMicropayTask != null) {
			myMicropayTask.cancel(true);
		}
	}

	private boolean payinit(boolean needInit) {
		if (Common.dishModel == null) {
			mPayOrderView.showErrorTipsDialog(1, "请到\"智慧快餐后台管理系统-菜品档案-基本档案管理\"添加菜品！", false);
			//			ToastUtils.showToast(mContext, "后台没有配置菜品信息，请到后台配置菜品信息!");
			return false;
		}
		payCost = mPayOrderView.getCost();
		if (Float.parseFloat(payCost) <= 0) {
			ToastUtils.showToast(mContext, "请输入正确金额");
			return false;
		}
		if (Common.workShifit == null) {
			mPayOrderView.showKaiBanDialog();
			return false;
		}
		handler.removeCallbacks(excuteAliQueryRunnable);
		isInit = needInit;
		countTime = 1;
		bis_id = "12" + format3.format(new Date());//生成流水号
		return true;
	}

	private static final int PRINTINITFAIL = 1;
	//	private static final int PRINTINITOK   = 2;

	/**
	 * 检测打印机连接的状态
	 *
	 * @param printer
	 * @throws IOException
	 */

	private String payCost;//为了方便，就定义了一个变量来记住这个支付的金额

	//	private boolean isDebug = true;

	public void cashPay() {
		if (payinit(false)) {
			if (SharedPreferencesUtil.getifPrintTicket(mContext) && SharedPreferencesUtil
					.getCheckPrintBeforeNewOrder(mContext)) {
				if (Common.isPrintOk) {
					//					checkPrinter(PayReqModel.PTID_CASH);
					mPayOrderView.showPayLoadingDialog();
					PayReqModel.currentPayName = PayReqModel.PTID_CASHSTR;
					PayReqModel.currentPayType = PayReqModel.PTID_CASH;
					paySuccess();
				} else {
					currentPay = PayReqModel.PTID_CASH;
					ErrorDialog dialog = mPayOrderView.showErrorTipsDialog(0, "", true);
					dialog.setCancelable(false);
					dialog.setonCancleClickListener(EasyPayPresenter.this);
				}
			} else {
				mPayOrderView.showPayLoadingDialog();
				PayReqModel.currentPayName = PayReqModel.PTID_CASHSTR;
				PayReqModel.currentPayType = PayReqModel.PTID_CASH;
				paySuccess();
			}
		}
	}

	public void onLinePay() {
		//微信 支付宝 威富通支付方式都是这个入口
		if (payinit(true)) {
			//			mPayOrderView.showPayInputDialog(PayReqModel.PTID_SSS_ONLINEPAY);
			if (SharedPreferencesUtil.getifPrintTicket(mContext) && SharedPreferencesUtil
					.getCheckPrintBeforeNewOrder(mContext)) {
				//				checkPrinter(PayReqModel.PTID_SSS_ONLINEPAY);
				if (Common.isPrintOk)
					mPayOrderView.showPayInputDialog(PayReqModel.PTID_SSS_ONLINEPAY);
				else {
					currentPay = PayReqModel.PTID_SSS_ONLINEPAY;
					ErrorDialog dialog = mPayOrderView.showErrorTipsDialog(0, "", true);
					dialog.setCancelable(false);
					dialog.setonCancleClickListener(EasyPayPresenter.this);
				}
			} else {
				mPayOrderView.showPayInputDialog(PayReqModel.PTID_SSS_ONLINEPAY);
			}
		}
	}


	public void memberPay() {
		if (payinit(true)) {
			//微生活会员支付
			//			mPayOrderView.showPayInputDialog(PayReqModel.PTID_SSS_MEMBER_CHUZHI);
			if (SharedPreferencesUtil.getifPrintTicket(mContext) && SharedPreferencesUtil
					.getCheckPrintBeforeNewOrder(mContext)) {
				if (Common.isPrintOk) {
					PayReqModel.currentPayName = PayReqModel.PTID_SSS_MEMBER_CHUZHISTR;
					PayReqModel.currentPayType = PayReqModel.PTID_SSS_MEMBER_CHUZHI;
					mPayOrderView.showPayInputDialog(PayReqModel.PTID_SSS_MEMBER_CHUZHI);
				} else {
					currentPay = PayReqModel.PTID_SSS_MEMBER_CHUZHI;
					ErrorDialog dialog = mPayOrderView.showErrorTipsDialog(0, "", true);
					dialog.setCancelable(false);
					dialog.setonCancleClickListener(EasyPayPresenter.this);
				}
			} else {
				PayReqModel.currentPayName = PayReqModel.PTID_SSS_MEMBER_CHUZHISTR;
				PayReqModel.currentPayType = PayReqModel.PTID_SSS_MEMBER_CHUZHI;
				mPayOrderView.showPayInputDialog(PayReqModel.PTID_SSS_MEMBER_CHUZHI);

			}
		}
	}

	@Override
	public void onCancle() {
		if (currentPay == PayReqModel.PTID_CASH) {
			mPayOrderView.showPayLoadingDialog();
			PayReqModel.currentPayName = PayReqModel.PTID_CASHSTR;
			PayReqModel.currentPayType = PayReqModel.PTID_CASH;
			paySuccess();
		} else if (currentPay == PayReqModel.PTID_SSS_MEMBER_CHUZHI) {
			PayReqModel.currentPayName = PayReqModel.PTID_SSS_MEMBER_CHUZHISTR;
			PayReqModel.currentPayType = PayReqModel.PTID_SSS_MEMBER_CHUZHI;
			mPayOrderView.showPayInputDialog(PayReqModel.PTID_SSS_MEMBER_CHUZHI);
		} else if (currentPay == PayReqModel.PTID_SSS_ONLINEPAY) {
			mPayOrderView.showPayInputDialog(PayReqModel.PTID_SSS_ONLINEPAY);
		}
	}

	private int currentPay;

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {

			return false;
		}
	});


	public void doRiJie() {
		mPayOrderView.showLoadingDialog();
		NewPos.getInstance(mContext).dailyReport(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
				mPayOrderView.dismissLoadingDialog();
				mPayOrderView.showErrorTipsDialog(1, e.getMessage(), false);
			}

			@Override
			public void onResponse(String response, int id) {
				Log.e(TAG, "response>>" + response);
				PrintFile
						.printResult2("Class_EasyPayPresenter_Method_doRiJie()_onResponse>" + response + "\n");
				try {
					if (GsonUtils.getIntData("result", response) == 0) {
						WorkShiftReport bean = GsonUtils
								.getSingleBean(GsonUtils
										.getData("content", response), WorkShiftReport.class);
						doGetRiJieInfo(bean);
					} else {
						mPayOrderView.dismissLoadingDialog();
						mPayOrderView.showErrorTipsDialog(1, GsonUtils.getErrMsg(response), false);
					}
				} catch (Exception e) {
					e.printStackTrace();
					mPayOrderView.dismissLoadingDialog();
					mPayOrderView.showErrorTipsDialog(1, e.getMessage(), false);
				}
			}
		});
	}

	private void doGetRiJieInfo(final WorkShiftReport bean) {

		NewPos.getInstance(mContext).endDailyBusiness(new StringCallback() {
			@Override
			public void onError(Call call, Exception e, int id) {
				e.printStackTrace();
				mPayOrderView.dismissLoadingDialog();
				mPayOrderView.showErrorTipsDialog(1, Common.errMsg, false);
			}

			@Override
			public void onResponse(String response, int id) {
				//						{"result":0,"content":null,"errmsg":"0"}
				mPayOrderView.dismissLoadingDialog();
				Log.e(TAG, "response>>" + response);
				PrintFile
						.printResult2("Class_EasyPay_method_doGetRiJieInfo_onResponse_" + response + "\n");
				try {
					if (GsonUtils.getIntData("result", response) == 0) {
						doPrintRiJieTicket(bean);
						OnFinishEvent event = new OnFinishEvent();
						event.setMsg("jiaobansuccess");
						EventBus.getDefault().post(event);
					} else {
						mPayOrderView.showErrorTipsDialog(1, GsonUtils.getErrMsg(response), false);
					}
				} catch (Exception e) {
					e.printStackTrace();
					mPayOrderView.showErrorTipsDialog(1, e.getMessage(), false);
				}
			}
		});
	}

	private void doPrintRiJieTicket(WorkShiftReport bean) {
		PosKitchenPrintAdapter.getInstance().printRiJieTicket(bean);
	}

}
