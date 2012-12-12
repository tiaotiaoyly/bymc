package com.bspif.app.mobilemechanic;

public class Log {
	
	private final static String DEFAULT_TAG = "NO_TAG";
	
	public static void v(String tag, String format, Object ... args) {
		if (args.length > 0) {
			format = String.format(format, args);
		}
		android.util.Log.v(tag != null ? tag : DEFAULT_TAG, format);
	}
	
	public static void d(String tag, String format, Object ... args) {
		if (args.length > 0) {
			format = String.format(format, args);
		}
		android.util.Log.d(tag != null ? tag : DEFAULT_TAG, format);
	}
	
	public static void i(String tag, String format, Object ... args) {
		if (args.length > 0) {
			format = String.format(format, args);
		}
		android.util.Log.i(tag != null ? tag : DEFAULT_TAG, format);
	}
	
	public static void w(String tag, String format, Object ... args) {
		if (args.length > 0) {
			format = String.format(format, args);
		}
		android.util.Log.w(tag != null ? tag : DEFAULT_TAG, format);
	}
	
	public static void e(String tag, String format, Object ... args) {
		if (args.length > 0) {
			format = String.format(format, args);
		}
		android.util.Log.e(tag != null ? tag : DEFAULT_TAG, format);
	}
}
