package com.bspif.app.mobilemechanic;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
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
	
	public static final String SD_HOME = Environment.getExternalStorageDirectory().toString().concat("/.MobileMechanic");
	public static final String HD_IMAEG_DIR = SD_HOME.concat("/hd_images");
	
	public static final String LOADING_HTML_URL = "http://www.ctiaotiao.com/temp/loading.html";
	public static final String LOADING_HTML_FILE = "loading.html";
	
	/////////////////////////////////////////////////
	
	public final static Global instance = new Global();
	
	public AdRequest adRequest = null;
	public AdHandler adHandler = null;
	public AdView adView = null;
	
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
	
}
