package com.bspif.app.mobilemechanic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class LoadingActivity extends Activity {    
	
	private static final String TAG = "Loading";
	
	private class LoadingRunnable implements Runnable {
		Activity activity;
		
		public LoadingRunnable(Activity activity) {
			this.activity = activity;
		}
		
		public void run() {
			AppData.initialize(activity);
			Intent it = new Intent(activity, MainActivity.class);
			activity.startActivity(it);
			activity.finish();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.loading);
		
		Thread thread = new Thread(new LoadingRunnable(this));
		thread.start();
	}
	
}
