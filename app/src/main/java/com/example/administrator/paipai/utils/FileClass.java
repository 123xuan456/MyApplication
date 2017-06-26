package com.example.administrator.paipai.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by liguo on 16/10/27.
 */

public class FileClass {

	// 获取私有目录
	private static String getPrivateDirectory(Context context) {
		return context.getFilesDir().getPath();
	}

	// 获取SD卡目录
	private static String getSDPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	/*
	 * 保存文件
	 * *********************************************************************
	 * ********
	 */
	// 写数据到SD中的文件
	public static void writeSDFile(String fileName, String write_str)
			throws IOException {
		try {
			File file = new File(getSettingPath(), fileName);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(write_str.getBytes());
			fos.close();
			MyLog.i("写入成功：", getSettingPath() + fileName + write_str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeSDFile(String fileName, byte[] write_byte)
			throws IOException {
		try {
			File file = new File(getSettingPath(), fileName);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(write_byte);
			fos.close();
			MyLog.i("写入成功：", getSettingPath() + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 把文件保存在私有目录
	public static boolean savePrivateFile(Context context, String str_fileName,
										  String str_data) {
		try {
			FileOutputStream fileOutputStream = context.openFileOutput(
					str_fileName, Context.MODE_PRIVATE);
			try {
				fileOutputStream.write(str_data.getBytes());
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * 获取文件
	 * *********************************************************************
	 * ********
	 */
	// 读SD中的文件
	public static String readSDFile(String fileName) throws IOException {
		String res = "";
		try {
			File file = new File(getSettingPath(), fileName);
			FileInputStream is = new FileInputStream(file);
			byte[] b = new byte[(int) file.length()];
			is.read(b);
			res = new String(b, 0, (int) file.length());
			// System.out.println("读取成功："+res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	// 获取发送文件内容
	public static byte[] readFile(String filePath) throws IOException {
		if (filePath == null || filePath.length() < 1)
			return null;
		String fileName = URLDecoder.decode(filePath, "UTF-8");
		fileName = fileName.replace("file:", "");
		MyLog.i("更改后文件地址", fileName);
		String res = "";
		File file = new File(fileName, "");
		// File file = new File(uri);
		FileInputStream is = new FileInputStream(file);
		// 文件限制在10M。
		// long fileLength = file.length() > 10240000 ? 10240000 :
		// file.length();
		byte[] b = new byte[(int) file.length()];
		is.read(b);
		return b;
	}

	public static byte[] readFileToByte(String filePath) throws IOException {
		if (filePath == null || filePath.length() < 1)
			return null;
		String fileName = URLDecoder.decode(filePath, "UTF-8");
		fileName = fileName.replace("file:", "");
		MyLog.i("更改后文件地址", fileName);
		File file = new File(fileName, "");
		FileInputStream is = new FileInputStream(fileName);
		long fileLength = file.length();
		byte[] b = new byte[(int) fileLength];
		is.read(b);
		return b;
	}

	// 获取文件名
	public static String getFileName(String pathandname) {
		int start = pathandname.lastIndexOf("/");
		int end = pathandname.lastIndexOf(".");
		if (start != -1 && end != -1) {
			return pathandname.substring(start + 1, end);
		} else {
			return "";
		}
	}

	/**
	 * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
	 * 
	 * @return
	 */
	public static boolean isSdCardExist() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取SD卡根目录路径
	 * 
	 * @return
	 */
	private static String getSettingPath() {
		return getFeasycomPath("Setting1");
	}

	public static String getFeasycomPath(String path) {
		boolean exist = isSdCardExist();
		String sdpath = "";
		if (exist) {
			sdpath = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/Feasycom/FeasycomBluetooth/"
					+ path
					+ "/";
			File destDir = new File(sdpath);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
		} else {
			sdpath = String.valueOf("NULL");
		}
		return sdpath;
	}

	public static String getSdPath() {
		boolean exist = isSdCardExist();
		String sdpath = "";
		if (exist) {
			sdpath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/";
			File destDir = new File(sdpath);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
		} else {
			sdpath = String.valueOf("NULL");
		}
		return sdpath;
	}

	public static String getFileAbsolutePath(Context context, Uri fileUri) {
		if (context == null || fileUri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
				&& DocumentsContract.isDocumentUri(context, fileUri)) {
			if (isExternalStorageDocument(fileUri)) {
				String docId = DocumentsContract.getDocumentId(fileUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
			} else if (isDownloadsDocument(fileUri)) {
				String id = DocumentsContract.getDocumentId(fileUri);
				Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(fileUri)) {
				String docId = DocumentsContract.getDocumentId(fileUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(fileUri))
				return fileUri.getLastPathSegment();
			return getDataColumn(context, fileUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
			MyLog.i("文件路径", fileUri.getPath());
			return fileUri.getPath();
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri,
									   String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String[] projection = { MediaStore.Images.Media.DATA };
		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return "";
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	/**
	 * 格式转换
	 */
	public static String byteToHex(final byte[] buf) {
		if (buf.length <= 0)
			return "";
		String string = "";
		for (int i = 0; i < buf.length; i++) {
			string = String.format("%s%02X ", string, buf[i]);
		}
		return string;
	}

	public static String byteToHex(final byte buf) {
		String string;
		return String.format("%02X ", buf);
	}

	public static String byteToString(final byte[] buf) {
		if (buf.length <= 0)
			return "";
		String string = new String(buf, 0, buf.length);
		return string;
	}

	public static byte[] hexToByte(final String string) {
		if (string.length() <= 0)
			return new byte[0];
		String str = string.replace(" ", "");
		// 如果是奇位数，最后一个数前面补0
		if (str.length() % 2 == 1) {
			StringBuffer strBuf = new StringBuffer(str);
			strBuf.insert(str.length() - 1, '0');
			str = strBuf.toString();
		}
		char[] ch = str.toCharArray();
		// 把小写转大写
		for (int i = 0; i < str.length(); i++) {
			if (ch[i] >= 'a' && ch[i] <= 'f') {
				ch[i] = (char) (ch[i] - ('a' - 'A'));
			}
		}

		byte[] buf = new byte[(str.length() + 1) / 2];
		int strLen = str.length() % 2 > 0 ? str.length() + 1 : str.length();
		for (int i = 0; i < strLen; i++) {
			if (ch[i] >= '0' && ch[i] <= '9') {
				ch[i] -= '0';
			} else {
				ch[i] -= 'A' - 10;
			}
		}
		for (int i = 0; i < (str.length() + 1) / 2; i++) {
			buf[i] = (byte) (ch[i * 2] * 16 + ch[i * 2 + 1]);
		}
		return buf;
	}

	public static byte[] stringToByte(final String string) {
		if (string.length() <= 0)
			return new byte[0];
		byte[] buf = string.getBytes();
		return buf;
	}

	public static String stringToHex(String string) {
		String str = byteToHex(stringToByte(string));
		return str;
	}

	public static String hexToString(String string) {
		String str = byteToString(hexToByte(string));
		return str;
	}

	// 将十六进制中的字母转为对应的数字
	private static int formattingOneHexToInt(String a) {

		int i = 0;
		for (int u = 0; u < 10; u++) {
			if (a.equals(String.valueOf(u))) {
				i = u;
			}
		}
		if (a.equals("a") || a.equals("A")) {
			i = 10;
		}
		if (a.equals("b") || a.equals("B")) {
			i = 11;
		}
		if (a.equals("c") || a.equals("C")) {
			i = 12;
		}
		if (a.equals("d") || a.equals("D")) {
			i = 13;
		}
		if (a.equals("e") || a.equals("E")) {
			i = 14;
		}
		if (a.equals("f") || a.equals("F")) {
			i = 15;
		}
		return i;
	}

	private static int formattingHexToInt(String a) {
		String a1 = a.substring(0, 1);
		String a2 = a.substring(1, 2);
		return formattingOneHexToInt(a1) * 16 + formattingOneHexToInt(a2);
	}

	// 无符号加法
	public static int add(int a, int b) {
		int jw = a & b;
		int re = a ^ b;
		int temp;
		while (jw != 0) {
			temp = jw << 1;
			jw = re & temp;
			re = re ^ temp;
		}
		return re;
	}

	// 无符号减法
	public static int minus(int a, int b) {
		return add(a, add(~b, 1));
	}

	// 若某字节被解释成负的则需将其转成无符号正数
	private static long transform(byte temp) {
		long tempInt = (long) temp;
		if (tempInt < 0) {
			tempInt += 256;
		}
		return tempInt;
	}

	// public static long transform(int temp){
	// byte[] byte_int = intToBytes(temp);
	// // MyLog.i("byte_int", fileClass.byteToHex(byte_int));
	// return byteToLong_2(byte_int[0],byte_int[1],byte_int[2],byte_int[3]);
	// }
	/* 小端 ************************************************** */
	public static int byteToInt_2(byte a) {
		return (int) a & 0xff;
	}

	public static int byteToInt_2(byte a, byte b) {
		int int_b = b & 0xff;
		int int_a = a & 0xff;
		return int_a + (int_b << 8);
	}

	public static int byteToInt_2(byte a, byte b, byte c, byte d) {
		int int_d = d & 0xff;
		int int_c = c & 0xff;
		int int_b = b & 0xff;
		int int_a = a & 0xff;
		return int_a + (int_b << 8) + (int_c << 16) + (int_d << 24);
		// return add(add(add((int)a&0xff, b<<8), c<<16), d<<24);
	}

	/**
	 * 字符串转以小端转int,字符串必须是4个数字，不足4数字后面补0，超过4个数字只取前4个。
	 */
	public static int stringToInt(String string) {
		String str1 = string.substring(0, 2);
		String str2 = string.substring(2, 4);
		int int1 = formattingHexToInt(str1);
		int int2 = formattingHexToInt(str2);
		byte byte1 = Byte.parseByte(String.valueOf(int1));
		byte byte2 = Byte.parseByte(String.valueOf(int2));
		int reInt = byteToInt_2(byte1, byte2);
		MyLog.i("解析出", String.valueOf(reInt));
		return reInt;
	}

	public static short byteToShort_2(byte a, byte b) {
		return (short) add(a & 0xff, b << 8);
	}

	// public static long byteToLong_2(byte a, byte b, byte c, byte d){
	// long long_d = d&0xff;
	// long long_c = c&0xff;
	// long long_b = b&0xff;
	// long long_a = a&0xff;
	// return long_a + (long_b<<8) + (long_c<<16) + (long_d<<24);
	// // return transform(add(add(add((int)a&0xff, b<<8), c<<16), d<<24));
	// }
	// public static byte[] intToBytes(int integer){
	// int byteNum = (40 -Integer.numberOfLeadingZeros (integer < 0 ? ~integer :
	// integer))/ 8;
	// byte[] byteArray = new byte[4];
	//
	// for (int n = 0; n < byteNum; n++)
	// byteArray[n] = (byte) (integer>>> (n * 8));
	//
	// return (byteArray);
	// }
	// byte[]型数据转成int[]型数据
	public static int[] byteToInt(byte[] content) {

		int[] result = new int[content.length / 4]; // 除以2的n次方 == 右移n位 即
													// content.length / 4 ==
													// content.length ＞＞ 2
		for (int i = 0, j = 0; j < content.length; i++, j += 4) {
			result[i] = (int) (transform(content[j])
					| transform(content[j + 1]) << 8
					| transform(content[j + 2]) << 16 | transform(content[j + 3]) << 24);
		}
		return result;

	}

	// int[]型数据转成byte[]型数据
	public static byte[] intToByte(int[] content) {
		byte[] result = new byte[content.length * 4]; // 乘以2的n次方 == 左移n位 即
														// content.length * 4 ==
														// content.length ＜＜ 2
		for (int i = 0, j = 0; j < result.length; i++, j += 4) {
			result[j] = (byte) (content[i] & 0xff);
			result[j + 1] = (byte) ((content[i] >> 8) & 0xff);
			result[j + 2] = (byte) ((content[i] >> 16) & 0xff);
			result[j + 3] = (byte) ((content[i] >> 24) & 0xff);
		}
		return result;
	}

	// int 转无符号 long，位不变
	public static long intToLong(int i) {
		long l = i & 0xffffffffL;
		return l;
	}

	/**********************************************************/

	public static String getType_modelName(int type_model) {
		switch (type_model) {
		case 1:
			return "BT401";
		case 2:
			return "BT405";
		case 3:
			return "BT426N";
		case 4:
			return "BT501";
		case 5:
			return "BT502";
		case 6:
			return "BT522";
		case 7:
			return "BT616";
		case 8:
			return "BT625";
		case 9:
			return "BT626";
		case 10:
			return "BT803";
		case 11:
			return "BT813D";
		case 12:
			return "BT816S";
		case 13:
			return "BT821";
		case 14:
			return "BT822";
		case 15:
			return "BT826";
		case 16:
			return "BT826N";
		case 17:
			return "BT836";
		case 18:
			return "BT836N";
		case 19:
			return "BT906";
		case 20:
			return "BT909";
		case 21:
			return "BP102";
		case 22:
			return "BT816s3";
		default:
			return "Unknown";
		}
	}
}
