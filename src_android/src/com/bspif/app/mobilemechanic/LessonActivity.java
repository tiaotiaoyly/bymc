package com.bspif.app.mobilemechanic;

import java.io.IOException;
import java.util.ArrayList;

import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LessonActivity extends Activity implements OnClickListener, OnPageChangeListener{

	private static final String TAG = "Lesson";

	private AppData.CategoryData catData = null;
	private AppData.LessonData lessonData = null;
	
	private ViewPager pager = null;
	private ArrayList<View> pagesList = null;
	private PopupWindow mCurrentPopupWindow = null;
	private ViewGroup contentView = null;
	private PageFoot mPageFoot = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		int catID = intent.getExtras().getInt("catID");
		int lessonID = intent.getExtras().getInt("lessonID");
		int page = intent.getExtras().getInt("page");
		if (page == 0) page = 1;
		catData = AppData.getCategory(catID);
		lessonData = catData.getLesson(lessonID);
		
		String title = lessonData.title;
		if (title.equals("")) {
			title = catData.title;
		}
		this.setTitle(title);
		pager = new ViewPager(this);
		pager.setAdapter(new Adapter());
		
		pagesList = new ArrayList<View>();
		LayoutInflater inflater = this.getLayoutInflater();
		for (int i = 0; i < lessonData.pages.length; i++) {
			ViewGroup pageView = (ViewGroup) inflater.inflate(R.layout.lesson_page, null);
			TextView tv = (TextView) pageView.findViewById(R.id.textView1);
			tv.setText(lessonData.pages[i].text);
			ImageView iv = (ImageView) pageView.findViewById(R.id.imageButton1);
			String imgFilename = lessonData.pages[i].image;
			Bitmap bmp;
			try {
				bmp = Util.getBitmapFromAsset(this, imgFilename);
				if (null != bmp) {
					iv.setImageBitmap(bmp);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			//iv.setLayoutParams(new LayoutParams(150, 100));
			iv.setId(i);
			iv.setOnClickListener(this);
			pagesList.add(pageView);
		}
		AdView adView = Global.instance.getAdView();
		
		RelativeLayout.LayoutParams lvParam = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lvParam.addRule(RelativeLayout.ABOVE, adView.getId());
		pager.setLayoutParams(lvParam);
		pager.arrowScroll(50);
		
		mPageFoot = new PageFoot(this, lessonData.pages.length, page - 1);
		RelativeLayout.LayoutParams pfParam = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		pfParam.addRule(RelativeLayout.ALIGN_TOP);
		mPageFoot.setLayoutParams(pfParam);
		pager.setOnPageChangeListener(this);
		
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(pager);
		layout.addView(mPageFoot);
		
		contentView = layout;
		this.setContentView(contentView);
		
		pager.setCurrentItem(page - 1);
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
    	//ad view
    	AdView adView = Global.instance.getAdView();
    	contentView.removeView(adView);
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		if (null != mCurrentPopupWindow) {
			mCurrentPopupWindow.dismiss();
			mCurrentPopupWindow = null;
		}
		super.onDestroy();
	}

	protected Bitmap getBitmap(String filename) {
		Bitmap bmp;
		try {
			String hdImgPath = Global.HD_IMAEG_DIR.concat("/" + filename);
			bmp = Util.getBitmapFromSDCard(this, hdImgPath);
			if (null == bmp) {
				bmp = Util.getBitmapFromAsset(this, filename);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bmp;
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
	
	private class Adapter extends PagerAdapter {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			//super.destroyItem(container, position, object);
			container.removeView(pagesList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.d(TAG, "instantiateItem %d, list=%d", position, pagesList.size());
			container.addView(pagesList.get(position));
			return pagesList.get(position);
		}

		@Override
		public int getCount() {
			return pagesList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
	}

	public void onClick(View v) {
		if (null != mCurrentPopupWindow) {
			mCurrentPopupWindow.dismiss();
			mCurrentPopupWindow = null;
		}
		int i = v.getId();
		String filename = lessonData.pages[i].image;
		Log.d(TAG, "on image click %d, %s, %s", i, filename, v.getClass());
		Bitmap bmp = getBitmap(filename);
		View view = new ImageViewer(v.getContext(), bmp);
		mCurrentPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
		mCurrentPopupWindow.showAtLocation(pager, Gravity.CENTER, 0, 0);
		mCurrentPopupWindow.setAnimationStyle(android.R.style.Animation_Toast);
		view.setOnClickListener(new DismissPopupWindow());
	}
	
	class DismissPopupWindow implements OnClickListener {
		public void onClick(View v) {
			if (null == mCurrentPopupWindow) {
				return;
			}
			mCurrentPopupWindow.dismiss();
			mCurrentPopupWindow = null;
		}
	}
	
	class PageFoot extends RelativeLayout {
		private LinearLayout mLinearLayout = null;
		private Drawable mLightDrawable = null;
		private Drawable mDarkDrawable = null;
		private ImageView[] mTokens = null;
		private int mCurrent = -1;
		
		public PageFoot(Context context, int size, int current) {
			super(context);
			mLinearLayout = new LinearLayout(context);
			mTokens = new ImageView[size];
			mLightDrawable = context.getResources().getDrawable(R.drawable.page_token);
			mDarkDrawable = context.getResources().getDrawable(R.drawable.page_token_dark);
			for (int i = 0; i < size; i++) {
				ImageView img = new ImageView(context);
				LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lparams.setMargins(10, 10, 10, 10);
				Drawable drawable = context.getResources().getDrawable(R.drawable.page_token_dark);
				img.setImageDrawable(drawable);
				img.setLayoutParams(lparams);
				mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
				mLinearLayout.addView(img);
				mTokens[i] = img;
			}
			RelativeLayout.LayoutParams rparams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			rparams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			mLinearLayout.setLayoutParams(rparams);
			this.addView(mLinearLayout);
			
			onChange(current);
		}
		
		public void onChange(int index) {
			if (mCurrent >=0) {
				setToken(mCurrent, false);
			}
			setToken(index, true);
			mCurrent = index;
		}
		
		private void setToken(int index, boolean highlight) {
			ImageView token = mTokens[index];
			if (highlight) {
				token.setImageDrawable(mLightDrawable);
			} else {
				token.setImageDrawable(mDarkDrawable);
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int page) {
		mPageFoot.onChange(page);
	}
}
