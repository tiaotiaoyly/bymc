package com.bspif.app.mobilemechanic;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

public class Global {

	public class AdHandler implements AdListener {
		private static final String TAG = "AdHandler";
		
		public void onDismissScreen(Ad arg0) {
			Log.d(TAG, "onDismissScreen");
		}

		public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
			Log.d(TAG, "onFailedToReceiveAd");
		}

		public void onLeaveApplication(Ad arg0) {
			Log.d(TAG, "onLeaveApplication");
		}

		public void onPresentScreen(Ad arg0) {
			Log.d(TAG, "onReceiveAd");
		}

		public void onReceiveAd(Ad arg0) {
			Log.d(TAG, "onReceiveAd");
		}
	}

	/////////////////////////////////////////////////
	
	public static final String SD_HOME = Environment.getExternalStorageDirectory().toString().concat("/MobileMechanic");
	public static final String HD_IMAEG_DIR = SD_HOME.concat("/hd_images");
	
	public static final String HD_IMAEG_BASE_URL = "http://www.bspif.com/mm/";
	//public static final String HD_IMAEG_BASE_URL = "http://www.ctiaotiao.com/temp/mm/";
	
	public static final String LOADING_HTML_URL = "http://www.bspif.com/apps.html";
	//public static final String LOADING_HTML_URL = "http://www.ctiaotiao.com/temp/loading.html";
	
	public static final String URL_NOTIFICATION_JSON = "http://www.bspif.com/mm/notification.json";
	//public static final String URL_NOTIFICATION_JSON = "http://ctiaotiao.com/temp/notification.json";

	public static final String URL_TWITTER_SHARE_TEXT = "http://www.bspif.com/mmTsharing.html";
	
	public static final String URL_FACEBOOK_SHARE_TEXT = "http://www.bspif.com/mmFsharing.html";
	
	public final static int NOTIFICATION_REPEAT = 6 * 60 * 60 * 1000;
	
	public static final String LOADING_HTML_FILE = "loading.html";
	public static final String JSON_DATA_FILE = "data.dat";
	
	public static final String JSON_DATA_PURCHASE_STATE_KEY = "purchaseState";
	
	/////////////////////////////////////////////////
	
	public final static Global instance = new Global();
	
	public AdRequest adRequest = null;
	public AdHandler adHandler = null;
	public AdView adView = null;
	public JSONObject mJsonData;
	
	private Global() {
		adRequest = new AdRequest();
		adRequest.addTestDevice("9988B214E74650294BA998943E7BC554");	// Tiaotiao HTC G6
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		adHandler = new AdHandler();
	}
	
	////////////////////

	
	public AdView newAdView(Activity activity) {
		adView = Util.newAdView(activity, adRequest, adHandler);
		return adView;
	}
	
	public AdView getAdView() {
		return adView;
	}
	
	public void refreshAdRequest() {
		adRequest = new AdRequest();
	}
	
	////////////////////////
	
	public String getPurchasedHashKay(Context context) {
		String ANDROID_ID = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		if (null == ANDROID_ID) {
			ANDROID_ID = "default_id";
		}
		return Util.hashMD5(ANDROID_ID + "__Purchased__");
	} 
	
	public boolean loadData(Context context) {
		String jsonString = Util.readFromFile(context, Global.JSON_DATA_FILE);
		mJsonData = null;
		if (null != jsonString) {
			try {
				mJsonData = new JSONObject(jsonString);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (null == mJsonData) {
			mJsonData = new JSONObject();
			return false;
		}
		if (mJsonData.has(JSON_DATA_PURCHASE_STATE_KEY)) {
			try {
				String state = mJsonData.getString(JSON_DATA_PURCHASE_STATE_KEY);
				if (state == getPurchasedHashKay(context)) {
					AppData.isPurchased = true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public boolean saveData(Context context) {
		if (null == mJsonData) {
			return false;
		}
		String jsonString = mJsonData.toString();
		if (jsonString == null) {
			return false;
		}
		return Util.writeToFile(context, jsonString, Global.JSON_DATA_FILE);
	}
	
}
