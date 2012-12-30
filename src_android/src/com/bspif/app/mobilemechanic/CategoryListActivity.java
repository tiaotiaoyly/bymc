package com.bspif.app.mobilemechanic;

import java.io.IOException;

import org.json.JSONException;

import com.bspif.app.mobilemechanic.AppData.CategoryData;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoryListActivity extends Activity implements OnItemClickListener {

	private static final String TAG = "CatList";
	private ViewGroup contentView = null;
	private class CategoryListItemAdapter extends BaseAdapter {
		private LayoutInflater inflater = null;
		private View[] items = null;

		public CategoryListItemAdapter(Context context) {
			super();
			inflater = LayoutInflater.from(context);
			items = new View[AppData.categories.length];
			for (int i = 0; i < AppData.categories.length; i++) {
				CategoryData catData = AppData.getCategory(i);
				View item = inflater.inflate(R.layout.cat_list_item, null);
				TextView titleView = (TextView) item.findViewById(R.id.title);
				ImageView iconView = (ImageView) item.findViewById(R.id.icon);
				titleView.setText(catData.title);
				try {
					Bitmap iconBmp = Util.getBitmapFromAsset(context, catData.icon);
					iconView.setImageBitmap(iconBmp);
				} catch (IOException e) {
					e.printStackTrace();
				}
				items[i] = item;
			}
		}
		
		public int getCount() {
			return AppData.categories.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return items[position];
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle(R.string.category_activity_title);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// ad view
        AdView adview = Global.instance.getAdView();
        
		// list view
		ListView lv = new ListView(this);
		CategoryListItemAdapter ad = new CategoryListItemAdapter(this); 
		lv.setAdapter(ad);
		lv.setOnItemClickListener(this);
		RelativeLayout.LayoutParams lvParam = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lvParam.addRule(RelativeLayout.ABOVE, adview.getId());
		lv.setLayoutParams(lvParam);
		
		// add views
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(lv);
		//layout.addView(adview);
		contentView = layout;
		setContentView(contentView);
	}

	@Override
	protected void onResume() {
		if (!AppData.isPurchased) {
			AdView adView = Global.instance.getAdView();
			contentView.addView(adView);
		}
		super.onResume();
	}
	
    @Override
	protected void onPause() {
    	AdView adView = Global.instance.getAdView();
    	contentView.removeView(adView);
		super.onPause();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {  
        super.onConfigurationChanged(newConfig);  
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {  
                // land do nothing is ok  
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {  
                // port do nothing is ok  
        }  
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
			
			String shareText = catData.facebook;
			if (Global.instance.mJsonData.has("FACEBOOK_SHARE_TEXT")) {
				try {
					shareText = Global.instance.mJsonData.getString("FACEBOOK_SHARE_TEXT");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			String[] params = shareText.split(";");
			String p = null;
			String key, val;
			for (int i = 0; i < params.length; i++) {
				p = params[i];
				int ind = p.indexOf(':');
				if (-1 == ind) continue;
				key = p.substring(0, ind);
				val = p.substring(ind + 1);
				param.putString(key.toLowerCase(), val);
				Log.i(TAG, "++++++ put %s , %s", key, val);
			}
			facebook.dialog(this, "feed", param, new DialogListener() {
				public void onComplete(Bundle values) {
					Log.d(TAG, "on Dialog complete");
				}
				public void onFacebookError(FacebookError e) {
					Log.d(TAG, "on Facebook error");
				}
				public void onError(DialogError e) {
					Log.d(TAG, "on Dialog error");
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
			String shareText = catData.twitter;
			if (Global.instance.mJsonData.has("TWITTER_SHARE_TEXT")) {
				try {
					shareText = Global.instance.mJsonData.getString("TWITTER_SHARE_TEXT");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			shareText.replaceFirst("<Html>", "");
			shareText.replaceFirst("</Html>", "");
			String tweetUrl = "http://twitter.com/intent/tweet?text=" + java.net.URLEncoder.encode(shareText);
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
}
