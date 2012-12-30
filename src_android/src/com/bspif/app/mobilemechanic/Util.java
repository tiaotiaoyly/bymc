package com.bspif.app.mobilemechanic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Environment;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles" })
public class Util {

	private static String SDPATH;
	private static byte[] buffer = new byte[4 * 1024];

	static {
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	public static Bitmap getBitmapFromAsset(Context conetxt, String filename)
			throws IOException {
		return BitmapFactory.decodeStream(conetxt.getAssets().open(filename));
	}

	public static Bitmap getBitmapFromSDCard(Context conetxt, String path)
			throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeFile(path);
		} catch (Exception e) {
			file.delete();
			return null;
		}
		return bmp;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static String httpRead(String url) {
		StringBuffer sb = null;
		BufferedReader bfr = null;
		try {
			InputStream input = getInputStreamFromUrl(url);
			bfr = new BufferedReader(new InputStreamReader(input));
			String line = "";
			sb = new StringBuffer("");
			while ((line = bfr.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
		} finally {
			try {
				if (null != bfr)
					bfr.close();
			} catch (IOException e) {
			}
		}
		if (null == sb) return null;
		return sb.toString();
	}

	public static int httpDownloadToDevice(Context context, String urlStr,
			String fileName) {
		InputStream inputStream = null;
		try {
			inputStream = getInputStreamFromUrl(urlStr);
			File resultFile = write2DeviceFromInput(context, fileName,
					inputStream);
			if (resultFile == null) {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static int httpDownloadToSdcard(String urlStr, String dirname, String fileName) {
		File dir = new File(dirname);
		if (!dir.exists()) {
			if (!dir.mkdirs())
				Log.e("Util", "dir makdir fail %s", dirname);
		}
		if (!dir.isDirectory()) {
			return -5;
		}
		fileName = dir + "/" + fileName;
		
		// 构造URL
		URL url;
		OutputStream os = null;
		InputStream is = null;
		try {
			url = new URL(urlStr);
			// 打开连接
			URLConnection con = url.openConnection();
			// 获得文件的长度
			int contentLength = con.getContentLength();
			// 输入流
			is = con.getInputStream();
			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;
			// 输出的文件流
			os = new FileOutputStream(fileName);
			// 开始读取
			int readSize = 0;
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
				readSize += len;
			}
			if (readSize != contentLength) {
				return -4;
			}
		} catch (MalformedURLException e) {
			Log.e("Util", e.toString());
			return -1;
		} catch (IOException e) {
			Log.e("Util", e.toString());
			return -2;
		} finally {
			// 完毕，关闭所有链接
			try {
				if (null != os) os.close();
				if (null != is) is.close();
			} catch (IOException e) {
				Log.e("Util", e.toString());
				return -3;
			}
		}
		return 0;
	}

	public static InputStream getInputStreamFromUrl(String url)
			throws IOException {
		InputStream input = null;
		URLConnection conn = null;
		conn = new URL(url).openConnection();
		input = conn.getInputStream();
		return input;
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	public static File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	public static File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdirs();
		return dir;
	}

	@SuppressLint("WorldWriteableFiles")
	public static boolean writeToFile(Context context, String text,
			String filename) {
		OutputStream output = null;
		try {
			output = context.openFileOutput(filename, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
			output.write(text.getBytes());
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public static String readFromFile(Context context, String filename) {
		InputStream input = null;
		StringBuffer sb = new StringBuffer();
		try {
			input = context.openFileInput(filename);
			int length = input.available();
			byte[] buffer = new byte[length];
			while ((input.read(buffer)) != -1) {
				sb.append(new String(buffer));
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return sb.toString();
	}

	public static File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[4 * 1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static File write2DeviceFromInput(Context context, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			output = context.openFileOutput(fileName,
					Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static AdView newAdView(Activity activity, AdRequest adReq,
			AdListener listener) {
		String admobID = activity.getResources().getString(R.string.admob_id);
		AdView adview = new AdView(activity, AdSize.BANNER, admobID);
		adview.loadAd(adReq);
		adview.setAdListener(listener);
		RelativeLayout.LayoutParams adParam = new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		adParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adview.setLayoutParams(adParam);
		adview.setId(12);
		return adview;
	}

	public static String hashMD5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean CheckNetworkState(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// State mobile =
		// manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (wifi == State.CONNECTED)
			return true;
		return false;
	}
}
