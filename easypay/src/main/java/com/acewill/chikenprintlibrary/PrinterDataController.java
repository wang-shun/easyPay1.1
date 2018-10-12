package com.acewill.chikenprintlibrary;


import android.content.ContentValues;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.acewill.chikenprintlibrary.model.PA_KDS;
import com.acewill.chikenprintlibrary.model.PA_KitchenPrintMode;
import com.acewill.chikenprintlibrary.model.PA_KitchenStall;
import com.acewill.chikenprintlibrary.model.PA_Option;
import com.acewill.chikenprintlibrary.model.PA_Order;
import com.acewill.chikenprintlibrary.model.PA_OrderItem;
import com.acewill.chikenprintlibrary.model.PA_StoreInfo;
import com.acewill.chikenprintlibrary.model.WorkShiftNewReport;
import com.acewill.chikenprintlibrary.model.WorkShiftReport;
import com.acewill.chikenprintlibrary.printer.Alignment;
import com.acewill.chikenprintlibrary.printer.BitmapRow;
import com.acewill.chikenprintlibrary.printer.Column;
import com.acewill.chikenprintlibrary.printer.PrintModelInfo;
import com.acewill.chikenprintlibrary.printer.PrintTemplateType;
import com.acewill.chikenprintlibrary.printer.Printer;
import com.acewill.chikenprintlibrary.printer.PrinterFactory;
import com.acewill.chikenprintlibrary.printer.PrinterInterface;
import com.acewill.chikenprintlibrary.printer.PrinterLinkType;
import com.acewill.chikenprintlibrary.printer.PrinterOutputType;
import com.acewill.chikenprintlibrary.printer.PrinterTemplates;
import com.acewill.chikenprintlibrary.printer.PrinterVendor;
import com.acewill.chikenprintlibrary.printer.PrinterWidth;
import com.acewill.chikenprintlibrary.printer.Separator;
import com.acewill.chikenprintlibrary.printer.Table;
import com.acewill.chikenprintlibrary.printer.TextRow;
import com.acewill.chikenprintlibrary.printer.gpnetwork.GpEnternetPrint;
import com.acewill.chikenprintlibrary.util.PA_Constant;
import com.acewill.chikenprintlibrary.util.PrintUtils;
import com.acewill.ordermachine.common.Common;
import com.acewill.ordermachine.model.OrderReq;
import com.acewill.ordermachine.model.Payment;
import com.acewill.paylibrary.PayReqModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by DHH on 2017/3/17.
 */

public class PrinterDataController {
	private int bigSize     = 2;
	private int scaleHeight = 2;
	private int small       = 1;


	private PA_StoreInfo storeInfo;

	public void setStoreInfo(PA_StoreInfo storeInfo) {
		this.storeInfo = storeInfo;
	}

	/**
	 * 门店打印机列表
	 */
	private List<Printer>         printerList      = new ArrayList<>();
	/**
	 * 门店KDS列表
	 */
	private List<PA_KDS>          kdsList          = new ArrayList<>();
	/**
	 * 门店档口列表
	 */
	private List<PA_KitchenStall> kitchenStallList = new ArrayList<>();

	/**
	 * 打印模板类型映射
	 */
	private HashMap<Integer, HashMap<String, PrintModelInfo>> printModeMap = new HashMap<>();

	/**
	 * 菜品对应的厨房档口映射
	 */
	private HashMap<Long, List<PA_KitchenStall>> kitchenStallDishMap = new HashMap<>();
	/**
	 * 档口与打印机的映射
	 */
	private Map<Integer, Printer>                printerMap          = new HashMap<>();
	/**
	 * 档口与KDS的映射
	 */
	private Map<Integer, PA_KDS>                 kdsMap              = new HashMap<>();

	//    //临时存储打总单菜品的档口
	//    private HashMap<Long, List<PA_KitchenStall>> summaryKitchenStallMap = new HashMap<>();
	//    //将要在总单上打印的菜品
	//    private HashMap<Long, List<PA_OrderItem>> summaryOiPrintMap = new HashMap<>();
	//
	//    private HashMap<Long, PA_KitchenStall> summaryTempKitchenStallMap = new HashMap<>();

	private Map<Integer, Printer> getPrinterListforMap(List<Printer> printers) {
		if (printerMap == null)
			printerMap = new HashMap<Integer, Printer>();
		else
			printerMap.clear();
		if (printers != null && printers.size() > 0) {
			for (Printer printer : printers) {
				printerMap.put(printer.getId(), printer);
			}
		}
		return printerMap;
	}


	public List<PA_KDS> getKdsList() {
		return kdsList;
	}

	private Map<Integer, PA_KDS> getKdsIdforMap(List<PA_KDS> kdsList) {
		if (kdsMap == null)
			kdsMap = new HashMap<Integer, PA_KDS>();
		else
			kdsMap.clear();
		if (kdsList != null && kdsList.size() > 0) {
			for (PA_KDS kds : kdsList) {
				kdsMap.put(kds.getId(), kds);
			}
		}
		return kdsMap;
	}

	public void setPrinterList(List<Printer> printerList) {
		this.printerList = printerList;
		getPrinterListforMap(printerList);
	}

	public void setKdsList(List<PA_KDS> kdsList) {
		this.kdsList = kdsList;
		getKdsIdforMap(kdsList);
	}

	public void setKitchenStallList(List<PA_KitchenStall> kitchenStallList) {
		this.kitchenStallList = kitchenStallList;

		handleKitchenStall();
	}

	//    public List<PrinterVendors> getPrinterVendorsList() {
	//        return printerVendorsList;
	//    }
	//
	//    public void setPrinterVendorsList(List<PrinterVendors> printerVendorsList) {
	//        this.printerVendorsList = printerVendorsList;
	//    }

	public void setPrinterTemplatesList(List<PrinterTemplates> printerTemplatesList) {
		this.printerTemplatesList = printerTemplatesList;

		if (!printModeMap.isEmpty())
			printModeMap.clear();

		int size = printerTemplatesList.size();
		for (int i = 0; i < size; i++) {
			PrinterTemplates printerTemplate = printerTemplatesList.get(i);
			//客用小票
			if (printerTemplate.getTemplateType() == PrintTemplateType.CUSTOMER) {
				printModeMap.put(PA_Constant.EventState.PRINTER_ORDER, printerTemplate.getModels());
			}
			//加菜单
			if (printerTemplate.getTemplateType() == PrintTemplateType.ADD_DISH) {
				printModeMap.put(PA_Constant.JsToAndroid.JS_A_ADDDISH, printerTemplate.getModels());
			}
			//退菜单
			if (printerTemplate.getTemplateType() == PrintTemplateType.RETREAT_DISH) {
				printModeMap
						.put(PA_Constant.EventState.PRINTER_RETREAT_KITCHEN_ORDER, printerTemplate
								.getModels());
			}
			//预结单
			if (printerTemplate.getTemplateType() == PrintTemplateType.PRE_CHECKOUT) {
				printModeMap.put(PA_Constant.EventState.ORDER_TYPE_ADVANCE, printerTemplate
						.getModels());
			}
			//结账单
			if (printerTemplate.getTemplateType() == PrintTemplateType.CHECKOUT) {
				printModeMap
						.put(PA_Constant.EventState.PRINT_CHECKOUT, printerTemplate.getModels());
			}
			//标签
			if (printerTemplate.getTemplateType() == PrintTemplateType.LABEL) {

			}
			//厨房单
			if (printerTemplate.getTemplateType() == PrintTemplateType.KICHEN) {
				printModeMap.put(PA_Constant.EventState.PRINTER_KITCHEN_ORDER, printerTemplate
						.getModels());
			}
		}
	}

	/**
	 * 映射厨房档口打印机数据
	 */
	private void handleKitchenStall() {
		if (kitchenStallDishMap == null)
			kitchenStallDishMap = new HashMap<Long, List<PA_KitchenStall>>();
		else
			kitchenStallDishMap.clear();
		if (kitchenStallList != null && !kitchenStallList.isEmpty()) {
			int size = kitchenStallList.size();
			for (int i = 0; i < size; i++) {
				//循环档口列表
				PA_KitchenStall kitchenStall = kitchenStallList.get(i);
				//如果该档口下的菜品列表不为空
				if (kitchenStall.getDishIdList() != null && kitchenStall.getDishIdList()
						.size() > 0) {
					for (Long dishId : kitchenStall.getDishIdList()) {
						//判断该菜品档口是否添加档口数据  list是因为一个菜品可能在多个档口
						List<PA_KitchenStall> kitchenStallList = kitchenStallDishMap
								.get(Long.valueOf(String.valueOf(dishId)));
						if (kitchenStallList == null) {
							kitchenStallList = new ArrayList<>();
							kitchenStallDishMap
									.put(Long.valueOf(String.valueOf(dishId)), kitchenStallList);
						}
						if (!kitchenStallList.contains(kitchenStall))
							kitchenStallList.add(kitchenStall);
					}
				}
			}
		}
	}


	/**
	 * 打印菜品
	 *
	 * @param order
	 * @param oi
	 * @param printer
	 * @param kitchenStall
	 * @param printCount   当前的打印次数
	 */
	private void printOrderItemDish(final PA_Order order, final PA_OrderItem oi, final Printer printer, final PA_KitchenStall kitchenStall, int printCount) {

		try {
			if (printer.getIp() != null) {
				//                List<PA_KitchenStall> kitchenStallList = kitchenStallDishMap.get(oi.getDishId());
				//                int kitchenSize = kitchenStallList.size();
				//判断是标签纸还是普通纸  并且不是退菜单
				if (printer
						.getOutputType() == PrinterOutputType.LABEL && !isLogicRefundDish(order)) {
					//标签一份打印一张
					if (printer.getLinkType() == PrinterLinkType.NETWORK) {
						for (int i = 0; i < oi.getQuantity(); i++) {
							GpEnternetPrint.gpPrint(oi, printer.getIp(), printer.getLabelHeight());
						}
					}
				} else {
					if (kitchenStall.getDishReceiptCounts() != null && (int) kitchenStall
							.getDishReceiptCounts() > 0) {
						boolean isRetreatDish = false;
						//判断是否是退菜模式
						if (order.getTableStyle() == PA_Constant.EventState.PRINTER_RETREAT_DISH) {
							isRetreatDish = true;
						}
						int modeCount = getKitModeCount(oi, kitchenStall, isRetreatDish);
						for (int i = 0; i < modeCount; i++) {
							final PrinterInterface printerInterface = PrinterFactory
									.createPrinter(PrinterVendor
											.fromName(printer.getVendor()), printer.getIp(), printer
											.getWidth());
							new Thread() {
								@Override
								public void run() {
									try {
										printKitchenReceipt(printerInterface, order, oi, kitchenStall, printer);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}.start();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//    /**
	//     * 保存总单上打印的菜品
	//     *
	//     * @param oi
	//     * @param kitchenStall
	//     */
	//    private void saveKitchenStallForDish(PA_OrderItem oi, PA_KitchenStall kitchenStall) {
	//        List<PA_OrderItem> oiList = summaryOiPrintMap.get(kitchenStall.getStallsid());
	//        if (oiList == null) {
	//            oiList = new ArrayList<>();
	//            summaryOiPrintMap.put(kitchenStall.getStallsid(), oiList);
	//        }
	//        oiList.add(oi);
	//    }

	private void printKitchenReceipt(PrinterInterface printerInterface, PA_Order order, PA_OrderItem orderItem, PA_KitchenStall kitchenStall, Printer printer) throws Exception {
		printerInterface.init();
		if (printer.isStandBy()) {
			TextRow row = createRow(true, 2, printer.getStandByErrmsg());
			row.setAlign(Alignment.CENTER);
			printerInterface.printRow(row);
			printerInterface.printRow(new Separator("-"));
			printerInterface.printRow(new Separator(" "));
		}

		String orderType = order.getOrderType();
		if (order.getTableStyle() == PA_Constant.EventState.PRINTER_RETREAT_DISH) {
			orderType = "退菜";
		} else if (order.getTableStyle() == PA_Constant.EventState.PRINTER_RUSH_DISH) {
			orderType = "催菜";
		} else if (order.getTableStyle() == PA_Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT) {
			orderType = "补打";
		} else {
			orderType = orderType.equals("EAT_IN") ? "堂食" : "外带";
		}
		TextRow row = createRow(false, 2, "厨房分单");
		row.setAlign(Alignment.CENTER);
		printerInterface.printRow(row);

		printerInterface.printRow(new Separator("-"));
		String tableName = order.getTableNames();
		String comment   = "";
		comment = order.getComment();
		if (logicIsTable()) {
			if (tableName != null && tableName.length() > 0) {
				TextRow rowNum = createRow(true, 2, "桌台号:" + (tableName == null ? "0" : tableName) + "     " + orderType);
				rowNum.setAlign(Alignment.CENTER);
				printerInterface.printRow(rowNum);
			}
		} else {
			String cardNumberType = "";
			String callNumber     = "";
			if (storeInfo.isCardNumberMode()) {
				cardNumberType = "餐牌号:";
				callNumber = (order.getTableNames() == null ? "0" : order.getTableNames());
			} else {
				cardNumberType = "取餐号:";
				callNumber = (order.getCallNumber() == null ? "0" : order.getCallNumber());
			}

			row = createRow(false, 2, cardNumberType + callNumber + "     " + orderType);
			row.setAlign(Alignment.CENTER);
			row.setScaleHeight(2);
			printerInterface.printRow(row);
		}

		printerInterface.printRow(new Separator("-"));

		printerInterface.printRow(createRow(false, 1, "订单号: " + order.getId()));
		printerInterface.printRow(createRow(false, 1, "下单时间: " + order.getCreatedAtStr()));

		printerInterface.printRow(new Separator(" "));
		String orderTitle = PrintUtils.getStr("菜品", 21, PrintUtils.TYPE_BACK) + PrintUtils
				.getStr("数量", 21, PrintUtils.TYPE_TOP);
		printerInterface.printRow(createRow(false, 1, orderTitle));
		printerInterface.printRow(new Separator("-"));

		String dishItem     = "";
		String dishQuantity = "";
		if (kitchenStall.getKitchenPrintMode() == PA_KitchenPrintMode.PER_DISH) {
			//退菜
			if (isLogicRefundDish(order)) {
				dishQuantity = orderItem.getRejectedQuantity() + "";
				dishQuantity = getDishRejectedQuantity(dishQuantity);
			}
			//正常下单打菜
			else {
				dishQuantity = orderItem.getQuantity() + "";
			}
		} else {
			dishQuantity = "1";
		}
		HashMap<String, PrintModelInfo> printMode = printModeMap
				.get(PA_Constant.EventState.PRINTER_KITCHEN_ORDER);//厨房小票样式
		PrintModelInfo packageDetail = printMode.get("packageName");//套餐名称
		if (packageDetail != null && packageDetail.isShouldPrint())//查询是否要打印套餐名称
		{
			if (orderItem.getPackName() != null && orderItem.getPackName().length() > 0) {
				printerInterface.printRow(createRow(false, 2, "(" + orderItem
						.getPackName() + isShowDishPackageMoney(orderItem) + ")"));
			}
		}

		if (isLogicRefundDish(order)) {
			dishItem = PrintUtils.getStr("[退]" + orderItem.getDishName() + isWaiDai(order
					.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils
					.getStr("-" + dishQuantity + "份" + isShowDishMoney(orderItem), 7, PrintUtils.TYPE_TOP);
		} else {
			dishItem = PrintUtils.getStr(orderItem.getDishName() + isWaiDai(order
					.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils
					.getStr(dishQuantity + "份" + isShowDishMoney(orderItem), 7, PrintUtils.TYPE_TOP);
		}
		//厨房单里的菜品需要比较大的字体
		printerInterface.printRow(createRow(false, 2, dishItem));

		printDishOption(true, printerInterface, orderItem.optionList);
		if (comment != null) {
			TextRow rowNum = createRow(true, 2, "备注:( " + (comment == null ? "" : comment) + " ) ");
			rowNum.setAlign(Alignment.LEFT);
			printerInterface.printRow(rowNum);
		}
		printerInterface.close();
	}

	/**
	 * 这个订单是不是退菜模式
	 */
	private static boolean isLogicRefundDish(PA_Order order) {
		if (order.getTableStyle() == PA_Constant.EventState.PRINTER_RETREAT_DISH) {
			return true;
		}
		return false;
	}

	private static String getDishRejectedQuantity(String dishRejectedQuantity) {
		if (dishRejectedQuantity == null || "0".equals(dishRejectedQuantity)) {
			dishRejectedQuantity = "1";
		}
		return dishRejectedQuantity;
	}

	/**
	 * 厨房单是否要显示菜品金额  套餐项
	 *
	 * @param oi
	 * @return
	 */
	private String isShowDishPackageMoney(PA_OrderItem oi) {
		return "";
	}

	/**
	 * 厨房单是否要显示菜品金额  普通菜品
	 *
	 * @param oi
	 * @return
	 */
	private String isShowDishMoney(PA_OrderItem oi) {
		return "";
	}

	/**
	 * 打印普通菜品中的定制项
	 *
	 * @param printerInterface
	 * @param optionList
	 */
	private BigDecimal printDishOption(boolean isKitReceipt, PrinterInterface printerInterface, List<PA_Option> optionList) {
		BigDecimal dishOption = new BigDecimal("0.00");
		try {
			if (printerInterface != null && optionList != null && optionList.size() > 0) {
				if (optionList != null && optionList.size() > 0) {
					StringBuffer sb = new StringBuffer();
					for (PA_Option option : optionList) {
						if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
							sb.append(option.name + "、");
						} else {
							dishOption = dishOption.add(option.getPrice());
							sb.append(option.name + "(" + option.getPrice() + "元)、");
						}
					}
					TextRow oiRow = createRow(false, 1, "    " + sb.toString());
					if (isKitReceipt) {
						oiRow.setScaleWidth(2);
						oiRow.setScaleHeight(2);
					}
					printerInterface.printRow(oiRow);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dishOption;
	}


	/**
	 * 打印总单处理
	 *
	 * @param summaryKitchenStallDishItemMap
	 * @param order
	 */
	private void printOrderKitSummaryTicket(Map<PA_KitchenStall, List<PA_OrderItem>> summaryKitchenStallDishItemMap, final PA_Order order) {
		if (summaryKitchenStallDishItemMap == null || summaryKitchenStallDishItemMap.isEmpty())
			return;

		for (final PA_KitchenStall kitchenStall : summaryKitchenStallDishItemMap.keySet()) {
			final List<PA_OrderItem> itemList = summaryKitchenStallDishItemMap.get(kitchenStall);
			final Printer            printer  = printerMap.get(kitchenStall.getPrinterid());


			if (printer != null) {
				final PrinterInterface printerInterface;
				if (printer.isUseStandBy()) {
					final Printer printer2 = printerMap.get(Integer
							.parseInt(printer.getStandbyPrinterIdList()));
					printerInterface = PrinterFactory
							.createPrinter(PrinterVendor.fromName(printer2.getVendor()), printer2
									.getIp(), printer2.getWidth());
					new Thread() {
						@Override
						public void run() {
							printOrderDishKitSummaryTicket(printerInterface, printer2, order, kitchenStall, itemList);
						}
					}.start();
				} else {
					printerInterface = PrinterFactory
							.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer
									.getIp(), printer.getWidth());
					new Thread() {
						@Override
						public void run() {
							printOrderDishKitSummaryTicket(printerInterface, printer, order, kitchenStall, itemList);
						}
					}.start();

				}
			}
		}
	}
	//    /**
	//     * 初始化打印厨房总单
	//     *
	//     * @param orderKit
	//     */
	//    private void printOrderKitSummaryTicket(final PA_Order orderKit) {
	//        summaryTempKitchenStallMap.clear();
	//        if (summaryKitchenStallMap != null && summaryKitchenStallMap.size() > 0) {
	//            Iterator iter = summaryKitchenStallMap.entrySet().iterator();
	//            while (iter.hasNext()) {
	//                Map.Entry entry = (Map.Entry) iter.next();
	//                Long stallsid = (Long) entry.getKey();//找出总单map key
	//                if (orderKit != null && orderKit.getItemList() != null && orderKit.getItemList().size() > 0) {
	//                    List<PA_KitchenStall> kitchenStallList = summaryKitchenStallMap.get(stallsid);
	//                    if (kitchenStallList != null && kitchenStallList.size() > 0) {
	//                        //将同一个档口的菜品集合到一起
	//                        for (PA_KitchenStall kitchenStall : kitchenStallList) {
	//                            PA_KitchenStall tempMap = summaryTempKitchenStallMap.get(kitchenStall.getStallsid());
	//                            if (tempMap == null) {
	//                                summaryTempKitchenStallMap.put(kitchenStall.getStallsid(), kitchenStall);
	//                            }
	//                        }
	//                        Iterator iter2 = summaryTempKitchenStallMap.entrySet().iterator();
	//                        while (iter2.hasNext()) {
	//                            Map.Entry entry2 = (Map.Entry) iter2.next();
	//                            Long stallsid2 = (Long) entry2.getKey();//找出总单map key
	//                            final PA_KitchenStall kitchenStall2 = summaryTempKitchenStallMap.get(stallsid2);
	//
	//                            final Printer printer = printerMap.get(kitchenStall2.getPrinterid());
	//                            if (printer != null) {
	//                                final PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), printer.getWidth());
	//                                new Thread()
	//                                {
	//                                    @Override
	//                                    public void run()
	//                                    {
	//                                        printOrderDishKitSummaryTicket(printerInterface, printer, orderKit, kitchenStall2);
	//                                    }
	//                                }.start();
	//                            }
	//                        }
	//                    }
	//                }
	//            }
	//        }
	//    }

	/**
	 * 打印厨房总单
	 *
	 * @param printerInterface
	 * @param printer
	 * @param order
	 * @param kitchenStall
	 */
	private void printOrderDishKitSummaryTicket(PrinterInterface printerInterface, Printer printer, PA_Order order, PA_KitchenStall kitchenStall, List<PA_OrderItem> orderItemList) {
		//        List<PA_OrderItem> orderItemList = null;
		//        if (summaryOiPrintMap != null && summaryOiPrintMap.size() > 0) {
		//            orderItemList = summaryOiPrintMap.get(kitchenStall.getStallsid());
		//        }

		if (orderItemList != null && orderItemList.size() > 0) {
			try {
				printerInterface.init();
				if (printer.isStandBy()) {
					TextRow row = createRow(true, 2, printer.getStandByErrmsg());
					row.setAlign(Alignment.CENTER);
					printerInterface.printRow(row);
					printerInterface.printRow(new Separator("-"));
					printerInterface.printRow(new Separator(" "));
				}
				for (PA_OrderItem orderItem : orderItemList) {
					orderItem.setPackName(orderItem.getTempPackName());
				}
				String orderType = order.getOrderType();
				if (order.getTableStyle() == PA_Constant.EventState.PRINTER_RETREAT_DISH) {
					orderType = "退菜";
				} else if (order.getTableStyle() == PA_Constant.EventState.PRINTER_RUSH_DISH) {
					orderType = "催菜";
				} else if (order
						.getTableStyle() == PA_Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT) {
					orderType = "补打";
				} else {
					orderType = orderType.equals("EAT_IN") ? "堂食" : "外带";
				}
				TextRow row = createRow(false, 2, "厨房总单");
				row.setAlign(Alignment.CENTER);
				printerInterface.printRow(row);

				printerInterface.printRow(new Separator("-"));
				String tableName = order.getTableNames();
				String comment   = "";
				comment = order.getComment();
				if (logicIsTable()) {
					if (tableName != null) {
						TextRow rowNum = createRow(true, 2, "桌台号:" + (tableName == null ? "0" : tableName) + "     " + orderType);
						rowNum.setAlign(Alignment.CENTER);
						printerInterface.printRow(rowNum);
					}
				} else {
					String cardNumberType = "";
					String callNumber     = "";
					if (storeInfo.isCardNumberMode()) {
						cardNumberType = "餐牌号:";
						callNumber = (order.getTableNames() == null ? "0" : order.getTableNames());
					} else {
						cardNumberType = "取餐号:";
						callNumber = (order.getCallNumber() == null ? "0" : order.getCallNumber());
					}

					row = createRow(false, 2, cardNumberType + callNumber + "     " + orderType);
					row.setAlign(Alignment.CENTER);
					row.setScaleHeight(2);
					printerInterface.printRow(row);
				}

				printerInterface.printRow(new Separator("-"));

				printerInterface.printRow(createRow(false, 1, "订单号: " + order.getId()));
				printerInterface.printRow(createRow(false, 1, "下单时间: " + order.getCreatedAtStr()));

				printerInterface.printRow(new Separator(" "));
				String orderTitle = PrintUtils.getStr("菜品", 21, PrintUtils.TYPE_BACK) + PrintUtils
						.getStr("数量", 21, PrintUtils.TYPE_TOP);
				printerInterface.printRow(createRow(false, 1, orderTitle));
				printerInterface.printRow(new Separator("-"));

				String previousPackageName = "";
				for (PA_OrderItem orderItem : orderItemList) {
					orderItem.setTempPackName(orderItem.getPackName());
					if (previousPackageName.equals(orderItem.getPackName())) {
						orderItem.setPackName("");
					} else {
						previousPackageName = orderItem.getPackName();
					}
				}
				for (PA_OrderItem orderItem : orderItemList) {
					String dishItem     = "";
					String dishQuantity = "";
					//退菜
					if (isLogicRefundDish(order)) {
						dishQuantity = orderItem.getRejectedQuantity() + "";
						dishQuantity = getDishRejectedQuantity(dishQuantity);
					}
					//正常下单打菜
					else {
						dishQuantity = orderItem.getQuantity() + "";
					}

					HashMap<String, PrintModelInfo> printMode = printModeMap
							.get(PA_Constant.EventState.PRINTER_KITCHEN_ORDER);//厨房小票样式
					PrintModelInfo packageDetail = printMode
							.get("packageName");//套餐名称
					if (packageDetail != null && packageDetail.isShouldPrint())//查询是否要打印套餐名称
					{
						if (orderItem.getPackName() != null && orderItem.getPackName()
								.length() > 0) {
							TextRow rowNum = createRow(true, 2, "(" + orderItem
									.getPackName() + isShowDishPackageMoney(orderItem) + ")");
							rowNum.setAlign(Alignment.LEFT);
							printerInterface.printRow(rowNum);
						}
					}
					if (isLogicRefundDish(order)) {
						dishItem = PrintUtils
								.getStr("[退]" + orderItem.getDishName() + isWaiDai(order
										.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils
								.getStr("-" + dishQuantity + "份" + isShowDishMoney(orderItem), 5, PrintUtils.TYPE_TOP);
					} else {
						dishItem = PrintUtils.getStr(orderItem.getDishName() + isWaiDai(order
								.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils
								.getStr(dishQuantity + "份" + isShowDishMoney(orderItem), 5, PrintUtils.TYPE_TOP);
					}


					printerInterface.printRow(createRow(false, 2, dishItem));


					printDishOption(true, printerInterface, orderItem.optionList);


					if (comment != null) {
						TextRow rowNum = createRow(true, 2, "备注:( " + (comment == null ? "" : comment) + " ) ");
						rowNum.setAlign(Alignment.LEFT);
						printerInterface.printRow(rowNum);
					}
				}


				String kdsCode = getBarcode(order.getId() + "");
				printerInterface.printRow(new Separator(" "));

				Bitmap qrcode = createQRCode(kdsCode, 250);
				printerInterface.printBmp(new BitmapRow(qrcode), true);

				printerInterface.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException {
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		BitMatrix matrix = new MultiFormatWriter()
				.encode(str, BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight);
		int   width  = matrix.getWidth();
		int   height = matrix.getHeight();
		int[] pixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (matrix.get(x, y)) {
					pixels[y * width + x] = 0xff000000;
				} else {
					pixels[y * width + x] = 0xffffffff;
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}

	private String getBarcode(String oid) {
		return "X" + oid + "Z";
	}

	private Alignment getAlian(int alian) {
		Alignment align = Alignment.LEFT;
		if (alian == 0) {
			align = Alignment.LEFT;
		}
		if (alian == 2) {
			align = Alignment.CENTER;
		}
		if (alian == 3) {
			align = Alignment.LEFT;
		}
		return align;
	}

	private void printEnter(PrinterInterface printerInterface, boolean isPrintEnter) throws IOException {
		if (isPrintEnter) {
			printerInterface.printRow(new Separator(" "));
		}
	}

	private static TextRow createRow(boolean bold, int size, String content) {
		TextRow title = new TextRow(content);
		title.setScaleWidth(size);
		title.setScaleHeight(size);
		title.setBoldFont(bold);
		return title;
	}

	//创建一行， 内容为 左对齐 和 有对齐 的2列
	private Table createRow(boolean bold, int size, String left, String right) {
		Table   table = new Table(2);
		TextRow row   = new TextRow();

		row.setScaleHeight(size);
		row.setScaleWidth(size);
		row.setBoldFont(bold);
		row.addColumn(new Column(left, Alignment.LEFT));
		row.addColumn(new Column(right, Alignment.RIGHT));

		table.addRow(row);
		return table;
	}

	private int getKitModeCount(PA_OrderItem oi, PA_KitchenStall kitchenStall, boolean isRefundDish) {
		int modeCount = 0;
		if (kitchenStall.getKitchenPrintMode() == PA_KitchenPrintMode.PER_DISH) {//多份一单
			modeCount = 1;
		} else //多份多单
		{
			if (isRefundDish) {
				modeCount = oi.getRejectedQuantity();
			} else {
				modeCount = oi.getQuantity();
			}
		}
		return modeCount;
	}

	/**
	 * 判断是否配置桌台  true是  false否
	 *
	 * @return
	 */
	private boolean logicIsTable() {
		boolean isTable   = true;
		String  storeMode = storeInfo.getStoreMode();
		if ("TABLE".equals(storeMode)) {
			isTable = true;
		} else {
			isTable = false;
		}
		return isTable;
	}

	private String isWaiDai(String orderType) {
		if (!orderType.equals("EAT_IN")) {
			return "(外带)";
		}
		return "";
	}

	//    private String getTimeStr(long time) {
	//        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	//        return sf.format(new Date(time));
	//    }

	// 对订单中的菜品进行处理， 把套餐中的每一项都抽出来
	private PA_Order initOrder(PA_Order o) {
		List<PA_OrderItem> copyOrderItemList = new CopyOnWriteArrayList<>();
		//全新的订单菜品list
		for (PA_OrderItem orderItem : o.getItemList()) {
			//套餐
			if (orderItem.getSubItemList() != null && orderItem.getSubItemList().size() > 0) {
				List<PA_OrderItem> subDishes = orderItem.getSubItemList();
				int                size      = subDishes.size();
				for (int i = 0; i < size; i++) {
					PA_OrderItem orderItem1 = subDishes.get(i);
					orderItem1.setRejectedQuantity(orderItem.getRejectedQuantity() * orderItem1
							.getQuantity());
					orderItem1.setQuantity(orderItem.quantity * orderItem1.quantity);
					orderItem1.setPackName(orderItem.getDishName());
					orderItem1.setDishName("[套]" + orderItem1.getDishName());
					//                    orderItem1.setPrinterStr(orderItem1.getPrinterStr());
					orderItem1.setDishKind(orderItem1.getDishKind());
					;
					//如果是全单退菜模式 退菜份数等于菜品选择的份数
					if (o.getTableStyle() == PA_Constant.EventState.PRINTER_RETREAT_DISH) {
						orderItem1.setRejectedQuantity(orderItem.getQuantity() * orderItem1
								.getQuantity());
					}
					copyOrderItemList.add(orderItem1);
				}
			}
			//普通菜品
			else {
				//                orderItem.setPrinterStr(orderItem.getPrinterStr());
				if (o.getTableStyle() == PA_Constant.EventState.PRINTER_RETREAT_DISH) {
					orderItem.setRejectedQuantity(orderItem.getQuantity());
				}
				copyOrderItemList.add(orderItem);
			}
		}
		o.setItemList(copyOrderItemList);
		return o;
	}


	public Printer getPrint(int printId) {
		Printer printer = printerMap
				.get(printId);//通过档口id找到打印机
		return printer;
	}

	/**
	 * 打印厨房单
	 *
	 * @param order
	 */
	public void printKitchenOrder(PA_Order order) {
		//        summaryKitchenStallMap.clear();
		//        summaryOiPrintMap.clear();
		//        summaryTempKitchenStallMap.clear();
		Map<PA_KitchenStall, List<PA_OrderItem>> summaryKitchenStallDishItemMap = new HashMap<PA_KitchenStall, List<PA_OrderItem>>();
		PA_Order                                 order1                         = initOrder(order);
		if (order != null && order.getItemList() != null && order.getItemList().size() > 0) {
			for (PA_OrderItem oi : order.getItemList()) {
				oi.setOrderId(order.getId());
				List<PA_KitchenStall> kitchenStallList = kitchenStallDishMap
						.get(oi.getDishId());//通过菜品id找到相应的档口
				if (kitchenStallList != null) {
					int size = kitchenStallList.size();
					for (int i = 0; i < size; i++) {
						PA_KitchenStall kitchenStall = kitchenStallList.get(i);

						//如果是总单档口
						if (kitchenStall.getSummaryReceiptCounts() != null && kitchenStall
								.getSummaryReceiptCounts() > 0) {
							//                            List<PA_KitchenStall> summaryKitchenStallList = summaryKitchenStallMap.get(kitchenStall.getStallsid());
							//                            if (summaryKitchenStallList == null) {
							//                                summaryKitchenStallList = new ArrayList<>();
							//                                summaryKitchenStallMap.put(kitchenStall.getStallsid(), summaryKitchenStallList);
							//                            }
							//                            if (!summaryKitchenStallList.contains(kitchenStall))
							//                                summaryKitchenStallList.add(kitchenStall);
							//
							//                            saveKitchenStallForDish(oi, kitchenStall);
							if (!summaryKitchenStallDishItemMap.containsKey(kitchenStall)) {
								List<PA_OrderItem> itemList = new ArrayList<PA_OrderItem>();
								itemList.add(oi);
								summaryKitchenStallDishItemMap.put(kitchenStall, itemList);
							} else {
								List<PA_OrderItem> itemList = summaryKitchenStallDishItemMap
										.get(kitchenStall);
								itemList.add(oi);
							}
						}
						if (kitchenStall.getDishReceiptCounts() > 0) {
							//判断门店配置的打印机打印菜品,打印分单
							if (printerMap != null && printerMap.size() > 0) {
								Printer printer = printerMap
										.get(kitchenStall.getPrinterid());//通过档口id找到打印机
								if (printer == null) {
									return;
								}
								if (printer.isUseStandBy()) {
									Printer printer2 = printerMap.get(Integer
											.parseInt(printer.getStandbyPrinterIdList()));
									if (printer2 != null) {
										printOrderItemDish(order, oi, printer2, kitchenStall, i);
									}
								} else {
									printOrderItemDish(order, oi, printer, kitchenStall, i);
								}
							}
						}
					}
				}
			}

			// 如果总单中不是空的，打印总单
			//            if(!summaryKitchenStallMap.isEmpty())
			//                printOrderKitSummaryTicket(order);
			if (!summaryKitchenStallDishItemMap.isEmpty())
				printOrderKitSummaryTicket(summaryKitchenStallDishItemMap, order);
		}
	}

	public List<Printer> getPrintList(PA_Order order) {
		ArrayList<Printer>                       printerList                    = new ArrayList<>();
		ArrayList<Integer>                       idList                         = new ArrayList<>();
		Map<PA_KitchenStall, List<PA_OrderItem>> summaryKitchenStallDishItemMap = new HashMap<PA_KitchenStall, List<PA_OrderItem>>();
		if (order != null && order.getItemList() != null && order.getItemList().size() > 0) {
			for (PA_OrderItem oi : order.getItemList()) {
				List<PA_KitchenStall> kitchenStallList = kitchenStallDishMap
						.get(oi.getDishId());//通过菜品id找到相应的档口
				if (kitchenStallList != null) {
					int size = kitchenStallList.size();
					for (int i = 0; i < size; i++) {
						PA_KitchenStall kitchenStall = kitchenStallList.get(i);
						if (kitchenStall.getDishReceiptCounts() > 0) {
							//判断门店配置的打印机打印菜品,打印分单
							if (printerMap != null && printerMap.size() > 0) {
								Printer printer = printerMap
										.get(kitchenStall.getPrinterid());//通过档口id找到打印机

								if (printer == null) {
									continue;
								}
								if (!idList.contains(kitchenStall.getPrinterid())) {
									if (!printerList.contains(printer)) {
										printerList.add(printer);
									}
									idList.add(kitchenStall.getPrinterid());
								}
							}
						}
						if (kitchenStall.getSummaryReceiptCounts() != null && kitchenStall
								.getSummaryReceiptCounts() > 0) {
							//                            List<PA_KitchenStall> summaryKitchenStallList = summaryKitchenStallMap.get(kitchenStall.getStallsid());
							//                            if (summaryKitchenStallList == null) {
							//                                summaryKitchenStallList = new ArrayList<>();
							//                                summaryKitchenStallMap.put(kitchenStall.getStallsid(), summaryKitchenStallList);
							//                            }
							//                            if (!summaryKitchenStallList.contains(kitchenStall))
							//                                summaryKitchenStallList.add(kitchenStall);
							//
							//                            saveKitchenStallForDish(oi, kitchenStall);
							if (!summaryKitchenStallDishItemMap.containsKey(kitchenStall)) {
								Printer printer = printerMap
										.get(kitchenStall.getPrinterid());//通过档口id找到打印机
								if (!printerList.contains(printer)) {
									printerList.add(printer);
								}
							}
						}
					}
				}
			}
		}
		return printerList;
	}

	public void resetPrintStatus() {
		if (printerMap != null) {
			for (Integer key : printerMap.keySet()) {
				Printer printer = printerMap.get(key);
				if (printer != null) {
					printer.setUseStandBy(false);
					printer.setErrMessage("");
				}
			}
		}
	}

	private SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	public void print(final Printer printer, final OrderReq req) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final PrinterInterface printerInterface = logicInitPrint(printer);
					Payment                payment          = req.getPaymentList().get(0);
					printerInterface.init();
					TextRow row = createRow(false, 2, "消费结账单");
					row.setAlign(Alignment.CENTER);
					printerInterface.printRow(row);
					printerInterface.printRow(new Separator(" "));
					row = createRow(false, 1, "(客)");
					row.setAlign(Alignment.CENTER);
					printerInterface.printRow(row);
					printerInterface.printRow(new Separator("-"));
					printerInterface
							.printRow(createRow(false, 1, "门店名称:" + Common.SHOP_INFO.sname + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "终端名称:" + Common.SHOP_INFO.tname + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "操作人员:" + Common.SHOP_INFO.realname + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "终端单号:" + req
									.getTerminalOrderId() + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "下单时间: " + format2
									.format(new Date()) + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "消费金额: " + req.getCost() + "（元）\n"));
					printerInterface.printRow(createRow(false, 1, "支付方式: " + payment
							.getPaymentTypeName() + "\n"));
					printerInterface.printRow(new Separator("-"));
					int payId = Integer.parseInt(payment.getPaymentTypeId());
					if (payId == PayReqModel.PTID_SSS_MEMBER_CHUZHI || payId == PayReqModel.PTID_SSS_MEMBER_JIFEN || payId == PayReqModel.PTID_SSS_MEMBER_YOUHUIQUAN) {
						row = createRow(true, 1, "会员消费详情");
						row.setAlign(Alignment.LEFT);
						printerInterface.printRow(row);
						String phone = req.getMemberPhoneNumber();
						String char1 = phone.substring(0, 3);
						String char2 = phone.substring(7, 11);
						phone = char1 + "****" + char2;

						String name = req.getMemberName();
						for (int i = 0; i < name.length(); i++) {
							if (i != 0) {
								name = name.replace(name.charAt(i), '*');
							}
						}

						printerInterface
								.printRow(createRow(false, 1, "会员卡号: " + req
										.getMemberid() + "(" + phone + ")"));
						printerInterface
								.printRow(createRow(false, 1, "会员姓名: " + name));
						printerInterface
								.printRow(createRow(false, 1, "卡 等 级: " + req.getMemberGrade()));
						printerInterface
								.printRow(createRow(false, 1, "消费金额: " + req.getCost()));
						printerInterface
								.printRow(createRow(false, 1, "(如有获赠积分卡券等，此与会员消费规则有关，详情咨询门店)"));
						printerInterface.printRow(new Separator("-"));
					}
					printerInterface
							.printRow(createRow(false, 1, "门店电话: " + Common.SHOP_INFO.phone));
					printerInterface
							.printRow(createRow(false, 1, "门店地址: " + Common.SHOP_INFO.address));
					printerInterface
							.printRow(createRow(false, 1, "打印时间: " + format2.format(new Date())));
					printerInterface.close();
					//保存打印记录
					ContentValues values = new ContentValues();
					values.put("printstatu", 1);
					values.put("printname", printer.getDescription());
					DataSupport.update(OrderReq.class, values, req.getId());
				} catch (Exception e) {
					ContentValues values = new ContentValues();
					values.put("printname", printer.getDescription());
					DataSupport.update(OrderReq.class, values, req.getId());
					e.printStackTrace();

				}
			}
		}).start();

	}
	/**
	 * 打印模板列表
	 */
	/**
	 * 收银打印机对象
	 */
	public static  Printer                receiptPrinter;
	private static List<PrinterTemplates> printerTemplatesList;
	private static boolean cashPrinterIsUSB = false;


	//打印交接班小票
	public void printWorkShift(final String brandName, final Printer printer, final WorkShiftNewReport workShiftReport) {
		PrinterInterface printerInterface = null;
		try {
			Log.i("打印" + "交接班" + "小票", "<<======printWorkShift");

			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						final PrinterInterface printerInterface = logicInitPrint(printer);
						printerInterface.init();
						printWorkShiftReceipt(printerInterface, workShiftReport, "交接班", brandName);
					} catch (Exception e) {
						e.printStackTrace();

					}
				}
			}).start();

		} catch (Exception e) {
			e.printStackTrace();
			closePrinterInterFace(printerInterface);
			//			EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_CASH, printType, e
			//					.getMessage()));
		}
	}


	public void printWorkShiftReceipt(PrinterInterface printerInterface, WorkShiftNewReport workShiftReport, String printStr, String brandName) {
		try {
			TextRow row1 = null;
			row1 = createRow(false, 2, brandName);
			row1.setAlign(Alignment.CENTER);
			printerInterface.printRow(row1);
			printerInterface.printRow(new Separator(" "));
			TextRow row = createRow(false, 2, printStr + "打印单");
			row.setAlign(Alignment.CENTER);
			printerInterface.printRow(row);
			printerInterface.printRow(new Separator(" "));

			row = createRow(false, 2, printStr + "人 : " + workShiftReport.getPersonName());
			row.setAlign(Alignment.LEFT);
			printerInterface.printRow(row);
			printerInterface.printRow(new Separator(" "));

			if (!TextUtils.isEmpty(workShiftReport.getWorkShiftName())) {
				row = createRow(false, 1, printStr + "班次名称 : " + workShiftReport
						.getWorkShiftName());
				row.setAlign(Alignment.LEFT);
				printerInterface.printRow(row);
			}

			if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
				row = createRow(false, 1, printStr + "开始时间 : " + workShiftReport.getStartTime());
				row.setAlign(Alignment.LEFT);
				printerInterface.printRow(row);
			}
			if (!TextUtils.isEmpty(workShiftReport.getEndTime())) {
				row = createRow(false, 1, printStr + "结束时间 : " + workShiftReport.getEndTime());
				row.setAlign(Alignment.LEFT);
				printerInterface.printRow(row);
			}

			if (workShiftReport.getStartWorkShiftCash() > 0) {
				row = createRow(false, 1, "开班钱箱余额 : " + workShiftReport.getStartWorkShiftCash());
				row.setAlign(Alignment.LEFT);
				printerInterface.printRow(row);
			}
			if (workShiftReport.getEndWorkShiftCash() > 0) {
				row = createRow(false, 1, "交班钱箱余额 : " + workShiftReport.getEndWorkShiftCash());
				row.setAlign(Alignment.LEFT);
				printerInterface.printRow(row);
			}
			row = createRow(false, 1, "小票打印时间 : " + getTimeStr(System.currentTimeMillis()));
			row.setAlign(Alignment.LEFT);
			printerInterface.printRow(row);

			createWorkShiftItem(printerInterface, workShiftReport);

			printerInterface.printTable(createRow(false, 1, "应交现金:", String
					.valueOf(workShiftReport.getSubmitCash() + " / 元")));
			printerInterface.printTable(createRow(false, 1, "差额:", String
					.valueOf(workShiftReport.getDifferenceCash() + " / 元")));
			printerInterface.printRow(new Separator(" "));
			printerInterface.close();
		} catch (Exception e) {
			e.printStackTrace();
			closePrinterInterFace(printerInterface);
			//			MyApplication.getInstance().ShowToast("打印小票失败,请检查打印机设置!");
		}
	}

	public static String getTimeStr(long time) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sf.format(new Date(time));
	}

	private void createWorkShiftItem(PrinterInterface printerInterface, WorkShiftNewReport workShiftReport) {
		try {
			for (WorkShiftNewReport.WorkShiftCategoryDataList itemCategorySalesDataList : workShiftReport
					.getWorkShiftCategoryDataList()) {
				printerInterface.printRow(new Separator("-"));

				TextRow row = createRow(false, 1, itemCategorySalesDataList.getName());
				row.setAlign(Alignment.CENTER);
				printerInterface.printRow(row);

				printerInterface.printRow(new Separator("-"));

				Table table = new Table(3);

				TextRow title = new TextRow();
				title.setBoldFont(true);
				title.setScaleWidth(1);
				title.addColumn(new Column("名称", Alignment.LEFT));
				title.addColumn(new Column("数量", Alignment.LEFT));
				title.addColumn(new Column("金额", Alignment.RIGHT));
				table.setTitle(title);
				table.addRow(new Separator("-"));

				for (WorkShiftNewReport.WorkShiftCategoryDataList.WorkShiftItemDatas itemSalesDataList : itemCategorySalesDataList
						.getWorkShiftItemDatas()) {
					title = new TextRow();
					title.setScaleWidth(1);
					title.addColumn(new Column(itemSalesDataList.getName() + " ", Alignment.LEFT));
					title.addColumn(new Column(String
							.valueOf(itemSalesDataList.getItemCounts()), Alignment.LEFT));
					title.addColumn(new Column(String
							.valueOf(itemSalesDataList.getTotal()), Alignment.RIGHT));
					table.addRow(title);
				}
				//统计行
				printerInterface.printTable(table);
			}
		} catch (Exception e) {
			e.printStackTrace();
			closePrinterInterFace(printerInterface);
		}
	}

	private void closePrinterInterFace(PrinterInterface printerInterface) {
		if (printerInterface != null) {
			try {
				printerInterface.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void print2(final Printer printer) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final PrinterInterface printerInterface = logicInitPrint(printer);
					printerInterface.init();
					TextRow row = createRow(false, 2, "测试页");
					row.setAlign(Alignment.CENTER);
					printerInterface.printRow(row);
					printerInterface.close();
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}).start();
	}

	public void openBox() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final PrinterInterface printerInterface = PrinterFactory
						.createPrinter(PrinterVendor
								.fromName(Common.SHOP_PRINTER.getVendor()), Common.SHOP_PRINTER
								.getIp(), PrinterWidth.WIDTH_80MM);
				try {
					printerInterface.init();
					printerInterface.openCachBox();
					printerInterface.close();
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}).start();
	}

	private PrinterInterface logicInitPrint(Printer printer) throws IOException {
		PrinterInterface printerInterface = null;
		if (printer != null && printer.getVendor().equals("shangmi_fix")) {

			printerInterface = PrinterFactory.createBluetoothPrinter(PrinterWidth.WIDTH_80MM);
			Log.i("蓝牙打印机===", "");
		} else if (printer != null) {
			printerInterface = PrinterFactory
					.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer
							.getIp(), printer.getWidth());
			Log.i("打印机id===" + printer.getId(), printer.getDescription() + "====" + printer
					.getIp());
		}
		//		String errMessage = "";
		//		if (printerInterface == null) {
		//			if (printer == null) {
		//				errMessage = "收银打印机打印失败,请打开蓝牙!";
		//				try {
		//					throw new Exception(errMessage);
		//				} catch (Exception e) {
		//					e.printStackTrace();
		//				}
		//			} else {
		//				errMessage = "未配置收银打印机!";
		//				try {
		//					throw new Exception(errMessage);
		//				} catch (Exception e) {
		//					e.printStackTrace();
		//				}
		//			}
		//		}
		//
		//		try {
		//			printerInterface.init();
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//			errMessage = receiptPrinter.getDescription() + "打印机连接超时,请重启!";
		//			try {
		//				throw new Exception(errMessage);
		//			} catch (Exception e1) {
		//				e1.printStackTrace();
		//			}
		//		}
		//
		//		if (printerInterface.checkStatus() != PrinterStatus.OK && receiptPrinter != null) {
		//			if (printerInterface.checkStatus() == PrinterStatus.COVER_OPEN) {
		//				errMessage = receiptPrinter.getDescription() + "打印机仓盖未关闭!";
		//			} else if (printerInterface.checkStatus() == PrinterStatus.NO_PAPER) {
		//				errMessage = receiptPrinter.getDescription() + "打印纸用尽,请更换打印纸!";
		//			} else if (printerInterface.checkStatus() == PrinterStatus.ERROR_OCCURED) {
		//				errMessage = receiptPrinter.getDescription() + "打印机未知错误,请重启!";
		//			}
		//			try {
		//				throw new Exception(errMessage);
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		}
		return printerInterface;
	}

	/**
	 * 打印退单小票
	 *
	 * @param printer
	 * @param req
	 */
	public void print4(final Printer printer, final OrderReq req) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					final PrinterInterface printerInterface = logicInitPrint(printer);
					printerInterface.init();
					TextRow row = createRow(false, 2, "消费退单");
					row.setAlign(Alignment.CENTER);
					printerInterface.printRow(row);
					printerInterface.printRow(new Separator(" "));
					row = createRow(false, 1, "(客)");
					row.setAlign(Alignment.CENTER);
					printerInterface.printRow(row);
					printerInterface.printRow(new Separator("-"));
					printerInterface
							.printRow(createRow(false, 1, "门店名称:" + Common.SHOP_INFO.sname + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "终端名称:" + Common.SHOP_INFO.tname + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "操作人员:" + Common.SHOP_INFO.realname + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "终端单号:" + req.getMyid() + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "退单时间: " + format2
									.format(new Date()) + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "退款金额: " + req.getCost() + "\n"));
					printerInterface.printRow(createRow(false, 1, "退款方式: " + req
							.getPaymentTypeName() + "\n"));
					printerInterface
							.printRow(createRow(false, 1, "打印时间:" + format2.format(new Date())));
					printerInterface.close();
				} catch (Exception e) {
					e.printStackTrace();

				}
			}
		}).start();
	}


	/**
	 * 打印日结小票
	 *
	 * @param printer
	 * @param workShiftReport
	 */
	public void printRiJieTicket(final Printer printer, final WorkShiftReport workShiftReport) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					PrinterInterface printerInterface = null;
					String           printStr         = "日结";
					printerInterface = logicInitPrint(printer);
					printerInterface.init();
					TextRow row = null;
					row = createRow(false, 2, printStr + "打印单");
					row.setAlign(Alignment.CENTER);
					printerInterface.printRow(row);
					printerInterface.printRow(new Separator(" "));

					row = createRow(false, 2, printStr + "人 : " + Common.SHOP_INFO.realname);
					row.setAlign(Alignment.LEFT);
					printerInterface.printRow(row);
					printerInterface.printRow(new Separator(" "));

					if (!TextUtils.isEmpty(workShiftReport.getStartTime())) {
						row = createRow(false, 1, printStr + "开始时间 : " + workShiftReport
								.getStartTime());
						row.setAlign(Alignment.LEFT);
						printerInterface.printRow(row);
					}
					if (!TextUtils.isEmpty(workShiftReport.getEndTime())) {
						row = createRow(false, 1, printStr + "结束时间 : " + workShiftReport
								.getEndTime());
						row.setAlign(Alignment.LEFT);
						printerInterface.printRow(row);
					}
					row = createRow(false, 1, "小票打印时间 : " + getTimeStr(System.currentTimeMillis()));
					row.setAlign(Alignment.LEFT);
					printerInterface.printRow(row);

					createWorkShiftItem(printerInterface, workShiftReport);
					printerInterface.close();
				} catch (Exception e) {
					e.printStackTrace();
					//			ToastUtils.showToast("打印小票失败,请检查打印机设置!");
				}

			}
		}).start();
	}

	/**
	 * 创建日结item
	 *
	 * @param workShiftReport
	 */
	private void createWorkShiftItem(PrinterInterface printerInterface, WorkShiftReport workShiftReport) {
		try {
			for (WorkShiftReport.ItemCategorySalesDataList itemCategorySalesDataList : workShiftReport
					.getItemCategorySalesDataList()) {
				printerInterface.printRow(new Separator("-"));

				TextRow row = createRow(false, 1, itemCategorySalesDataList.getName());
				row.setAlign(Alignment.CENTER);
				printerInterface.printRow(row);

				printerInterface.printRow(new Separator("-"));

				Table table = new Table(3);

				TextRow title = new TextRow();
				title.setBoldFont(true);
				title.setScaleWidth(1);
				title.addColumn(new Column("名称", Alignment.LEFT));
				title.addColumn(new Column("数量", Alignment.LEFT));
				title.addColumn(new Column("金额", Alignment.RIGHT));
				table.setTitle(title);
				table.addRow(new Separator("-"));

				for (WorkShiftReport.ItemCategorySalesDataList.ItemSalesDataList itemSalesDataList : itemCategorySalesDataList
						.getItemSalesDataList()) {
					title = new TextRow();
					title.setScaleWidth(1);
					title.addColumn(new Column(itemSalesDataList.getName() + " ", Alignment.LEFT));
					title.addColumn(new Column(String
							.valueOf(itemSalesDataList.getItemCounts()), Alignment.LEFT));
					title.addColumn(new Column(String
							.valueOf(itemSalesDataList.getTotal()), Alignment.RIGHT));
					table.addRow(title);
				}
				//统计行
				printerInterface.printTable(table);
			}

			printerInterface.printRow(new Separator("-"));

			TextRow row = createRow(false, 2, "客单价统计");
			row.setAlign(Alignment.CENTER);
			printerInterface.printRow(row);

			printerInterface.printRow(new Separator("-"));

			WorkShiftReport.PctData pctData = workShiftReport.getPctData();
			printerInterface.printTable(createRow(false, 1, "订单总数", String
					.valueOf(pctData.getOrderCounts() + " /条")));
			printerInterface.printTable(createRow(false, 1, "客人总数", String
					.valueOf(pctData.getCustomerCounts() + " /人")));
			printerInterface.printTable(createRow(false, 1, "平均订单金额", " ￥ " + String
					.valueOf(pctData.getPricePerOrder() + " / 元")));
			printerInterface.printTable(createRow(false, 1, "客单价", " ￥ " + String
					.valueOf(pctData.getPricePerCustomer() + " / 元")));
		} catch (Exception e) {
			e.printStackTrace();
			closePrinterInterFace(printerInterface);
		}
	}

}
