package com.bspif.app.mobilemechanic;

import java.io.File;

import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	
	private static final String TAG = "Main";
	
	private ViewGroup contentView = null;
	
	private class BackgroudRunnable implements Runnable {
		Context context = null;
		public BackgroudRunnable(Context context) {
			this.context = context;
		}
		public void run() {
			File loadingHtmlFile = new File(Global.LOADING_HTML_FILE);
			if (!loadingHtmlFile.exists()) {
				String htmlContent = Util.httpRead(Global.LOADING_HTML_URL);
				Log.v(TAG, "%s", htmlContent);
				if (htmlContent != null) {
					boolean b = Util.writeToFile(context, htmlContent, Global.LOADING_HTML_FILE);
					Log.d(TAG, "download loading html result %s, %s, %s", b, Global.LOADING_HTML_URL, Global.LOADING_HTML_FILE);
				}
			}
		}
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        ImageView btn = (ImageView) this.findViewById(R.id.main_btn);
        btn.setOnClickListener(this);
        
        Global.instance.newAdView(this);
        
//		String htmlContent = Util.readFromFile(this, Global.LOADING_HTML_FILE);
//		WebView webView = (WebView) this.findViewById(R.id.loadingWebView);
//		webView.loadData(htmlContent, "text/html", "utf-8");
//		webView.setBackgroundColor(0);
        
        Thread thread = new Thread(new BackgroudRunnable(this));
		thread.start();
		ActionReceiver.startService(this);
		
        contentView = (ViewGroup) this.findViewById(R.id.main_view);
        Global.instance.initBilling(this);
    }

	@Override
	protected void onResume() {
		AdView adView = Global.instance.getAdView();
		contentView.addView(adView);
		super.onResume();
	}
	
    @Override
	protected void onPause() {
    	AdView adView = Global.instance.getAdView();
    	contentView.removeView(adView);
		super.onPause();
	}
	
	public void onClick(View v) {
		Intent it = new Intent(this, CategoryListActivity.class);
		this.startActivity(it);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {  
        super.onConfigurationChanged(newConfig);  
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {  
    		// DO NOTHING
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {  
        	// DO NOTHING
        }  
	}
}