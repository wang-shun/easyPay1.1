package com.acewill.ordermachine.util_new;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Author：Anch
 * Date：2017/5/9 19:13
 * Desc：
 */
public class PrintFile {
	private static final String           TAG      = "PrintFile";
	private static       SimpleDateFormat format   = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");// 用于格式化日期,作为日志文件名的一部分
	private static       ArrayList<File>  fileList = new ArrayList<>();

	//	public static void printResult(Context context, String result, String fileName) {
	//		if (Environment.getExternalStorageState()
	//				.equals(Environment.MEDIA_MOUNTED)) {
	//			try {
	//				File dir = new File(Environment
	//						.getExternalStorageDirectory() + File.separator + "diancan");
	//				Log.e(TAG, dir.toString());
	//				if (!dir.exists()) {
	//					dir.mkdirs();
	//				}
	//				File file = new File(dir, fileName + "###" + format.format(new Date()));
	//				;
	//				FileOutputStream fos = new FileOutputStream(file);
	//				if (result != null) {
	//					fos.write(result.toString()
	//							.getBytes());
	//					fos.close();
	//				}
	//				//				//上传到服务器
	//				//				fileList.add(file);
	//				//				if (ifUpLoad) {
	//				//					upLoadPayFile(context);
	//				//					fileList.clear();
	//				//				}
	//			} catch (FileNotFoundException e) {
	//				e.printStackTrace();
	//				e.printStackTrace();
	//			} catch (IOException e) {
	//			}
	//		}
	//	}


	public static void printResult(String result, String fileName) {
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File dir = new File(Environment
						.getExternalStorageDirectory() + File.separator + "diancan2.0/OtherCatalog");
				Log.e(TAG, dir.toString());
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(dir, fileName + "###" + format.format(new Date()));
				;
				FileOutputStream fos = new FileOutputStream(file);
				if (result != null) {
					fos.write(result.toString()
							.getBytes());
					fos.close();
				}
				//				//上传到服务器
				//				fileList.add(file);
				//				if (ifUpLoad) {
				//					upLoadPayFile(context);
				//					fileList.clear();
				//				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				e.printStackTrace();
			} catch (IOException e) {
			}
		}
	}


	public static void printResult2(String result) {
		if (Environment.getExternalStorageState()
				.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File dir = new File(Environment
						.getExternalStorageDirectory() + File.separator + "diancan2.0/OtherCatalog");
				Log.e(TAG, dir.toString());
				if (!dir.exists()) {
					dir.mkdirs();
				}
				Calendar      cal     = Calendar.getInstance();
				int           year    = cal.get(Calendar.YEAR);//获取年份
				int           month   = cal.get(Calendar.MONTH);//获取月份
				int           day     = cal.get(Calendar.DATE);//获取日
				StringBuilder builder = new StringBuilder();
				builder.append(year).append(month+1).append(day);
				File             file = new File(dir, builder.toString());
				FileOutputStream fos  = new FileOutputStream(file, true);
				if (result != null) {
					fos.write(result.toString()
							.getBytes());
					fos.close();
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				e.printStackTrace();
			} catch (IOException e) {
			}
		}
	}
}
