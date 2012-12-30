package com.bspif.app.mobilemechanic;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;

public class NotificationService extends Service {

	private final static String TAG = "NotifServ";
	public final static String NOTIFICATION_JSON = "notification.json";
	public final static String NOTIFICATION_ON_CLICK_INTENT = "com.bspif.intent.NOTIFI_ON_CLICK";
	
	private final static int VERSION = 1;
	private final static String KEY_VERSION = "version";
	private final static String KEY_ID = "id";
	private final static String KEY_URL = "url";
	private final static String KEY_TITLE = "title";
	private final static String KEY_TEXT = "text";
	private final static String KEY_FLOW = "flowText";
	private final static String KEY_CLICKED = "clicked";
	
	@Override
	public void onCreate() {
		super.onCreate();
		loadJsonConfig(this);
		startServiceAlarm(this);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		downloadJsonConfig(this);
		showNotification(this);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private static boolean checkJson(JSONObject json) {
		if (!json.has(KEY_VERSION)) return false;
		if (!json.has(KEY_ID)) return false;
		if (!json.has(KEY_URL)) return false;
		if (!json.has(KEY_TITLE)) return false;
		if (!json.has(KEY_TEXT)) return false;
		if (!json.has(KEY_FLOW)) return false;
		try {
			if (json.getInt(KEY_VERSION) > VERSION) return false;
			json.getInt(KEY_ID);
			json.getString(KEY_URL);
			json.getString(KEY_TITLE);
			json.getString(KEY_TEXT);
			json.getString(KEY_FLOW);
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static void downloadJsonConfig(Context context) {
		if (!Util.CheckNetworkState(context)) {
			return;
		}
		String jsonString = Util.httpRead(Global.URL_NOTIFICATION_JSON);
		//Log.d(TAG, "download json config [%s]", jsonString);
		JSONObject jsonNew = null;
		try {
			jsonNew = new JSONObject(jsonString);
			if (!checkJson(jsonNew)) {
				return;
			}
		} catch (Exception e) {
			return;
		}
		JSONObject jsonNotification = loadJsonConfig(context);
		try {
			int newID = jsonNew.getInt(KEY_ID);
			if (null != jsonNotification && jsonNotification.getInt(KEY_ID) == newID) {
				return;
			}
			jsonNew.put(KEY_CLICKED, false);
			jsonNotification = jsonNew;
		} catch (JSONException e) {
			jsonNotification = jsonNew;
			Log.e(TAG, e.toString());
		}
		saveJsonConfig(context, jsonNotification);
	}
	
	private static JSONObject loadJsonConfig(Context context) {
		String jsonStr = Util.readFromFile(context, NOTIFICATION_JSON);
		JSONObject json = null;
		try {
			json = new JSONObject(jsonStr);
			if (!checkJson(json)) {
				return null;
			}
		} catch (JSONException e) {
		}
		Log.d(TAG, "load json config %s", json);
		return json;
	}
	
	private static void saveJsonConfig(Context context, JSONObject json) {
		if (null == json) {
			return;
		}
		String jsonString = json.toString();
		Util.writeToFile(context, jsonString, NOTIFICATION_JSON);
		Log.d(TAG, "save json config %s", jsonString);
	}
	
	public static void onNotificationClick(Context context) {
		Log.i(TAG, "onNotificationClick");
		JSONObject jsonNotification = loadJsonConfig(context);
		if (null == jsonNotification)
			return;
		String url;
		try {
			url = jsonNotification.getString(KEY_URL);
			Log.i(TAG, url);
		} catch (JSONException e1) {
			e1.printStackTrace();
			return;
		}
		Uri uri = Uri.parse(url);
		Intent i = new Intent(Intent.ACTION_VIEW, uri);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		
		try {
			jsonNotification.put("clicked", true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		saveJsonConfig(context, jsonNotification);
	}
	
	public static void showNotification(Context context) {
		JSONObject jsonNotification = loadJsonConfig(context);
		if (null == jsonNotification)
			return;
		Log.d(TAG, "show notif %s", jsonNotification);
		int id = 0;
		String text = null;
		String title = null;
		String flowText = null;
		try {
			id = jsonNotification.getInt(KEY_ID);
			title = jsonNotification.getString(KEY_TITLE);
			text = jsonNotification.getString(KEY_TEXT);
			flowText = jsonNotification.getString(KEY_FLOW);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		boolean clicked = false;
		try {
			clicked = jsonNotification.getBoolean(KEY_CLICKED);
		} catch (JSONException e) {}
		if (clicked)
			return;
		
		new Notification();
		Notification notice = new Notification(R.drawable.icon, flowText, System.currentTimeMillis());
		Intent intent = new Intent(NOTIFICATION_ON_CLICK_INTENT);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		
		notice.flags |= Notification.FLAG_AUTO_CANCEL;
		notice.setLatestEventInfo(context, title, text, pendingIntent);
		NotificationManager notifiMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notifiMgr.notify(id, notice);
	}

	public static void startServiceAlarm(Context context) {
		Intent ai = new Intent(context, ActionReceiver.class);  
		ai.setAction("alarm.action");  
		PendingIntent sender = PendingIntent.getBroadcast(context, 0,  ai, 0);  
		long firstime = SystemClock.elapsedRealtime();  
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, Global.NOTIFICATION_REPEAT, sender);
	}
}
