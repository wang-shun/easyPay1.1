package com.acewill.ordermachine.util;

import android.content.Context;
import android.os.Environment;

import com.acewill.ordermachine.util_new.ToastUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FileUtils {
	public static String SDPATH = Environment.getExternalStorageDirectory() + "/";
	;

	private        int    FILESIZE  = 4 * 1024;
	private static String imagePaht = "image/dishImage/";

	public String getSDPATH() {
		return SDPATH;
	}

	/**
	 * 在SD卡上创建文件
	 *
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建文件
	 *
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String absolute, String fileName)
			throws IOException {
		File file = new File(absolute + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 *
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		mkDir(dir);
		return dir;
	}

	public File createAbsSDDir(String dirName) {
		File dir = new File(dirName);
		mkDir(dir);
		return dir;
	}

	public static void mkDir(File file) {
		if (file.getParentFile().exists()) {
			file.mkdir();
		} else {
			mkDir(file.getParentFile());
			file.mkdir();
		}
	}


	public static String getFileName(int position) {
		String fileName = null;
		switch (position) {
			case 0:
				Calendar cal1 = Calendar.getInstance();
				fileName = new SimpleDateFormat("yyyy-MM-dd").format(cal1.getTime());
				break;
			case 1:
				Calendar cal2 = Calendar.getInstance();
				cal2.add(Calendar.DATE, -1);
				fileName = new SimpleDateFormat("yyyy-MM-dd").format(cal2.getTime());
				break;
			case 2:
				Calendar cal3 = Calendar.getInstance();
				cal3.add(Calendar.DATE, -2);
				fileName = new SimpleDateFormat("yyyy-MM-dd").format(cal3.getTime());
				break;
			default:
				break;
		}
		return fileName + ".txt";
	}

	public static File[] clearLog(Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				final File file2 = new File(SDPATH + "diancan2.0/crash");
				if (file2.exists()) {
					File[] files2 = file2.listFiles(new FileFilter() {
						@Override
						public boolean accept(File file2) {
							return true;
						}
					});
					for (File file3 : files2) {
						file3.delete();
					}
				}
			}
		}).start();

		final List<String> nameList = getFileNameList();

		final File file = new File(SDPATH + "diancan2.0/OtherCatalog");
		if (!file.exists()) {
			ToastUtils.showToast(context, "没有需要清理的缓存和日志");
			return null;
		}
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file2) {
				for (String name : nameList) {
					//要拿到不同名字的
					if (file2.getName().equals(name)) {
						return false;
					}
				}
				return true;
			}
		});

		return files;
	}

	private static List<String> getFileNameList() {
		Calendar cal       = Calendar.getInstance();
		String   fileName1 = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()) + ".txt";
		cal.add(Calendar.DATE, -1);
		String   fileName2 = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()) + ".txt";
		Calendar cal2      = Calendar.getInstance();
		cal2.add(Calendar.DATE, -2);
		String       fileName3 = new SimpleDateFormat("yyyy-MM-dd").format(cal2.getTime()) + ".txt";
		List<String> nameList  = new ArrayList<String>();
		nameList.add(fileName1);
		nameList.add(fileName2);
		nameList.add(fileName3);
		return nameList;
	}


	public static File getUploadLog(int position) {

		final String fileName = getFileName(position);

		File file = new File(SDPATH + "diancan2.0/OtherCatalog");
		File[] files = file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file2) {
				return file2.getName().equals(fileName);
			}
		});

		if (files != null && files.length > 0) {
			return files[0];
		} else {

			return null;
		}
	}


	public static String getCacheSize() {
		File file = new File(SDPATH + "diancan2.0/OtherCatalog");
		if (file == null || !file.exists() || file.length() < 1024) {
			return "0 kb";
		}
		File[] files = file.listFiles();
		if (files.length <= 3) {
			return "0 kb";
		}
		return convertFileSize(file.length());
	}

	public static String convertFileSize(long size) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (size >= gb) {
			return String.format("%.1f GB", (float) size / gb);
		} else if (size >= mb) {
			float f = (float) size / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (size >= kb) {
			float f = (float) size / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", size);
	}
}
