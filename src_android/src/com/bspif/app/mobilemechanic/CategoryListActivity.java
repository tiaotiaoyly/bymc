package com.bspif.app.mobilemechanic;


import org.json.JSONException;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class CategoryListActivity extends Activity implements OnItemClickListener {

	private static final String TAG = "CatList";
	
	public static final String TYPE_HEADER = "header";
	public static final String TYPE_CATEGORY = "category";
	public static final String TYPE_SETTING = "settings";
	public static final String TYPE_ADD_CAR = "addcar";
	public static final String TYPE_CAR_INFO = "carinfo";
	public static final String TYPE_FACEBOOK = "facebook";
	public static final String TYPE_TWITTER = "twitter";
	public static final String TYPE_WEBSITE = "website";

	private ListViewAdapter mAdapter = null;
	private ViewGroup contentView = null;
	
	public View newListItemView(Context context, String title, String icon) {
		View view = View.inflate(context, R.layout.cat_list_item, null);
		if (null != title) Util.setViewText(view, R.id.title, title);
		if (null != icon) Util.setViewImage(view, R.id.icon, icon);
		return view;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle(R.string.category_activity_title);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// ad view
        AdView adview = Global.instance.getAdView();
        
		// list view
		ListView lv = createListView();
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
	
	public ListView createListView() {
		ListView lv = new ListView(this);
		ListViewAdapter adapter = new ListViewAdapter();
		ListViewAdapter.Item item;
		View view;
		// cars
		adapter.add(adapter.new SeperatorItem(this, "Cars"));
		// TODO add cars
		
		view = newListItemView(this, "Add a car", null);
		item = adapter.new Item(view, "add_car");
		adapter.add(item);
		
		// settings
		view = newListItemView(this, "Settings", null);
		item = adapter.new Item(view, "settings");
		adapter.add(item);
		
		// categorys
		adapter.add(adapter.new SeperatorItem(this, "Category"));
		for (int i = 0; i < AppData.categories.length; i++) {
			AppData.CategoryData catData = AppData.getCategory(i);
			view = newListItemView(this, catData.title, catData.icon);
			item = adapter.new Item(view, "category");
			item.arg = i;
			adapter.add(item);
		}
		
		// info
		adapter.add(adapter.new SeperatorItem(this, "Informations"));
		// TODO infos
		
		mAdapter = adapter;
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		return lv;
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
		ListViewAdapter.Item item = mAdapter.get(index);
		if (item.type == "settings") {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		} else if (item.type == "add_car") {
			Intent intent = new Intent(this, AddCarActivity.class);
			startActivity(intent);
		} else if (item.type == "car") {
			
		} else if (item.type == "category") {
			onCategoryClicked(item.arg);
		}
	}
	
	private void onCategoryClicked(int index) {
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
			if (AppData.has(AppData.JSON_DATA_FACEBOOK_SHARE_TEXT)) {
				try {
					shareText = AppData.getString(AppData.JSON_DATA_FACEBOOK_SHARE_TEXT);
				} catch (JSONException e1) {
					e1.printStackTrace();
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
			if (AppData.has(AppData.JSON_DATA_TWITTER_SHARE_TEXT)) {
				try {
					shareText = AppData.getString(AppData.JSON_DATA_TWITTER_SHARE_TEXT);
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
