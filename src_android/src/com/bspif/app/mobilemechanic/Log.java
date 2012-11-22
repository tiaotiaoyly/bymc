package com.bspif.app.mobilemechanic;

public class Log {
	
	public static void v(String tag, String msg, Object ... args) {
		if (args.length > 0) {
			msg = String.format(msg, args);
		}
		android.util.Log.v(tag, msg);
	}
	
	public static void d(String tag, String msg, Object ... args) {
		if (args.length > 0) {
			msg = String.format(msg, args);
		}
		android.util.Log.d(tag, msg);
	}
	
	public static void i(String tag, String msg, Object ... args) {
		if (args.length > 0) {
			msg = String.format(msg, args);
		}
		android.util.Log.i(tag, msg);
	}
	
	
	public static void w(String tag, String msg, Object ... args) {
		if (args.length > 0) {
			msg = String.format(msg, args);
		}
		android.util.Log.w(tag, msg);
	}
	
	public static void e(String tag, String msg, Object ... args) {
		if (args.length > 0) {
			msg = String.format(msg, args);
		}
		android.util.Log.e(tag, msg);
	}
}
