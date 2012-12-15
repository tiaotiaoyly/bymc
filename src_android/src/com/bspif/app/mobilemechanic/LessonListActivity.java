package com.bspif.app.mobilemechanic;

import java.io.IOException;

import com.bspif.app.mobilemechanic.AppData.CategoryData;
import com.bspif.app.mobilemechanic.AppData.LessonData;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LessonListActivity extends Activity implements OnItemClickListener, OnClickListener {

	private static final String TAG = "SubCat";
	private int catID = -1;
	private AppData.CategoryData catData = null;
	private ViewGroup contentView = null;
	private ListView listView = null;
	
	private class LessonListItemAdapter extends BaseAdapter {
		private LayoutInflater inflater = null;
		private View[] items = null;

		public LessonListItemAdapter(Context context) {
			super();
			inflater = LayoutInflater.from(context);
			items = new View[catData.lessons.length];
			for (int i = 0; i < catData.lessons.length; i++) {
				LessonData lessonData = catData.getLesson(i);
				View item = inflater.inflate(R.layout.art_list_item, null);
				TextView titleView = (TextView) item.findViewById(R.id.title);
				ImageView iconView = (ImageView) item.findViewById(R.id.icon);
				titleView.setText(lessonData.title);
				try {
					String iconFileName = lessonData.icon;
					if (null == iconFileName) {
						iconFileName = catData.icon;
					}
					Bitmap iconBmp = Util.getBitmapFromAsset(context, iconFileName);
					iconView.setImageBitmap(iconBmp);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (!lessonData.canAccess()) {
					titleView.setTextColor(Color.RED);
				}
				items[i] = item;
			}
		}
		
		public int getCount() {
			return catData.getTitles().length;
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
		
		Intent intent = this.getIntent();
		catID = intent.getExtras().getInt("catID");
		catData = AppData.getCategory(catID);
		this.setTitle(catData.title);

		//ad view
		AdView adView = Global.instance.getAdView();
		
		// list view
		//String[] titles = catData.getTitles();
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.art_list_item, titles);
		BaseAdapter adapter = new LessonListItemAdapter(this);
		listView = new ListView(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		RelativeLayout.LayoutParams lvParam = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lvParam.addRule(RelativeLayout.ABOVE, adView.getId());
		listView.setLayoutParams(lvParam);
		
		// add views
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(listView);
		//layout.addView(adView);
		contentView = layout;
		this.setContentView(contentView);
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
		Log.d(TAG, String.format("on subcat item clicked %d", index));
		AppData.LessonData lessonData = catData.getLesson(index); 
		if (null == lessonData) {
			return;	// DO NOTHING
		}
		
		if (!lessonData.canAccess()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Lesson locked");
			builder.setMessage("Buy full version?");
			builder.setPositiveButton("Yes", this);
			builder.setNegativeButton("Later", null);
			builder.show();
			return;
		}
		
		// TODO lesson link
		if (null != lessonData.link) {
			
			int cID = AppData.findCategoryByTitle(lessonData.link.category);
			if (cID < 0) {
				// ERROR
				return;
			}
			CategoryData catData = AppData.getCategory(cID);
			int lsID = catData.findLessonByTitle(lessonData.link.lesson);
			if (lsID < 0) {
				// ERROR
				return;
			}
			int p = lessonData.link.page;
			
			Intent intent = new Intent(this, LessonActivity.class);
			intent.putExtra("catID", cID);
			intent.putExtra("lessonID", lsID);
			intent.putExtra("page", p);
			this.startActivity(intent);
			return;
		}
		
		Intent intent = new Intent(this, LessonActivity.class);
		intent.putExtra("catID", catID);
		intent.putExtra("lessonID", index);
		this.startActivity(intent);
	}

	public void onClick(DialogInterface dialog, int which) {
		if (!Global.instance.billingService.checkBillingSupported(Consts.ITEM_TYPE_INAPP)) {
			Toast.makeText(this, "In App Billing NOT Supported", Toast.LENGTH_LONG).show();
			return;
		}
		Global.instance.billingService.requestPurchase("", Consts.ITEM_TYPE_INAPP, null);
	}

}
