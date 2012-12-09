package com.bspif.app.mobilemechanic;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		this.setContentView(R.layout.loading);
		
		String htmlContent = Util.readFromFile(this, Global.LOADING_HTML_FILE);
		WebView webView = (WebView) this.findViewById(R.id.loadingWebView);
		webView.loadData(htmlContent, "text/html", "utf-8");
		webView.setBackgroundColor(0);
		
		Thread thread = new Thread(new LoadingRunnable(this));
		thread.start();
	}
	
}
