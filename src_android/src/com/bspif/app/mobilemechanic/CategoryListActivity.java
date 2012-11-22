package com.bspif.app.mobilemechanic;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class CategoryListActivity extends Activity implements OnItemClickListener, AdListener {

	private static final String TAG = "CatList"; 
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle("Category view");
		
		String[] catNames = AppData.getTitles();
		ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.cat_list_item, catNames);
		
		String admobID = this.getResources().getString(R.string.admob_id);
        AdView adview = new AdView(this, AdSize.BANNER, admobID);
        AdRequest adreq = new AdRequest();
        adreq.setTesting(true);
        //adview.loadAd(adreq);
		adview.setAdListener(this);
		RelativeLayout.LayoutParams adParam = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		adParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adview.setLayoutParams(adParam);
		adview.setId(12);
        
		ListView lv = new ListView(this);
		lv.setAdapter(ad);
		lv.setOnItemClickListener(this);
		RelativeLayout.LayoutParams lvParam = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lvParam.addRule(RelativeLayout.ABOVE, adview.getId());
		lv.setLayoutParams(lvParam);
		
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(lv);
		layout.addView(adview);
		setContentView(layout);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
		Log.d("Cat", String.format("on category item clicked %d", index));
		AppData.CategoryData catData = AppData.getCategory(index);
		if (null == catData) {
			return;	// DO NOTHING
		}
		if (catData.website != null) {
			Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(catData.website));
			it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
			this.startActivity(it);
			return;
		}
		if (catData.facebook != null) {
			String appID = this.getResources().getString(R.string.facebook_app_id);
			Facebook facebook = new Facebook(appID);
			Bundle param = new Bundle();
			param.putString("message", "helloworld");
			facebook.dialog(this, "me/feed", param, new DialogListener() {
				public void onComplete(Bundle values) {
					Log.d(TAG, "on Dialog complete");
				}
				public void onFacebookError(FacebookError e) {
					Log.d(TAG, "on Facebook error");
				}
				public void onError(DialogError e) {
					Log.d(TAG, "on Dialog complete");
				}
				public void onCancel() {
					Log.d(TAG, "on Dialog cancel");
				}
			});
//			String share = "http://m.facebook.com/sharer.php?t="+catData.facebook;
//			Uri uri = Uri.parse(share);
//			this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
			return;
		}
		if (catData.twitter != null) {
			String tweetUrl = "http://twitter.com/intent/tweet?text="+catData.twitter;
			Uri uri = Uri.parse(tweetUrl);
			this.startActivity(new Intent(Intent.ACTION_VIEW, uri));
			return;
		}
		if (catData.lessons.length == 1) {
			AppData.LessonData lessonData = catData.lessons[0];
			if (lessonData.title.equals("")) {
				Intent intent = new Intent(this, LessonActivity.class);
				intent.putExtra("catID", index);
				intent.putExtra("lessonID", 0);
				this.startActivity(intent);
				return;
			}
		}
		Intent intent = new Intent(this, LessonListActivity.class);
		intent.putExtra("catID", index);
		startActivity(intent);
	}

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
