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
	
	private static class CategoryListItemAdapter extends BaseAdapter {
		
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return true;
		}

		private LayoutInflater inflater = null;
		private View[] items = null;
		private String[] itemsType = null;
		private String[] itemsParam = null;
		
		public static final String TYPE_HEADER = "header";
		public static final String TYPE_CATEGORY = "category";
		public static final String TYPE_SETTING = "settings";
		public static final String TYPE_ADD_CAR = "addcar";
		public static final String TYPE_CAR_INFO = "carinfo";
		public static final String TYPE_FACEBOOK = "facebook";
		public static final String TYPE_TWITTER = "twitter";
		public static final String TYPE_WEBSITE = "website";

		public CategoryListItemAdapter(Context context) {
			super();
			int cateOffset = getCategoryOffset();
			items = new View[cateOffset + AppData.categories.length];
			inflater = LayoutInflater.from(context);
			
			// car title
			View title1 = inflater.inflate(R.layout.cat_list_seperator, null);
			((TextView)title1.findViewById(R.id.title)).setText("Car Managerment");
			items[0] = title1;
			
			// TODO cars
			for (int i = 0; i < AppData.getCarCount(); i++) {
				AppData.CarData carData = AppData.getCarData(i);
				View item = inflater.inflate(R.layout.cat_list_item, null);
				((TextView)item.findViewById(R.id.title)).setText(carData.name);
				((ImageView)item.findViewById(R.id.icon)).setImageResource(R.drawable.icon);
				items[1 + i] = item;
			}
			
			// add a cars 
			View addCarItem = inflater.inflate(R.layout.cat_list_item, null);
			((TextView)addCarItem.findViewById(R.id.title)).setText("Add a car");
			((ImageView)addCarItem.findViewById(R.id.icon)).setImageResource(R.drawable.icon);
			items[cateOffset - 2] = addCarItem;
			
			// category title
			View title2 = inflater.inflate(R.layout.cat_list_seperator, null);
			((TextView)title2.findViewById(R.id.title)).setText("Categorys");
			items[cateOffset - 1] = title2;
			
			// new category views items
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
				items[cateOffset - i] = item;
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
		
		public static int getCategoryOffset() {
			return AppData.cars.length + 3;	// two titles and 'add a car' option
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setTitle(R.string.category_activity_title);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// ad view
        AdView adview = Global.instance.getAdView();
        
        // head list view
        ListView headList = new ListView(this);
        
        
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
		
		
		// on category clicked
		index -= CategoryListItemAdapter.getCategoryOffset();
		
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
			if (AppData.has(AppData.JSON_DATA_FACEBOOK_SHARE_TEXT)) {
				try {
					shareText = AppData.getString(AppData.JSON_DATA_FACEBOOK_SHARE_TEXT);
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
