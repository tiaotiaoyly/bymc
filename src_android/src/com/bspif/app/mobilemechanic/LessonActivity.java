package com.bspif.app.mobilemechanic;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.PopupWindow;
import android.widget.TextView;

public class LessonActivity extends Activity implements OnClickListener{

	@SuppressWarnings("unused")
	private static final String TAG = "Lesson";

	private AppData.CategoryData catData = null;
	private AppData.LessonData lessonData = null;
	
	private ViewPager pager = null;
	private ArrayList<View> pagesList = null;
	private PopupWindow mCurrentPopupWindow = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		int catID = intent.getExtras().getInt("catID");
		int lessonID = intent.getExtras().getInt("lessonID");
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
			try {
				iv.setImageBitmap(Util.getBitmapFromAsset(this, lessonData.pages[i].image));
				//iv.setLayoutParams(new LayoutParams(150, 100));
				iv.setId(i);
				iv.setOnClickListener(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			pagesList.add(pageView);
		}
		
		this.setContentView(pager);
	}
	
	@Override
	protected void onDestroy() {
		if (null != mCurrentPopupWindow) {
			mCurrentPopupWindow.dismiss();
			mCurrentPopupWindow = null;
		}
		super.onDestroy();
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
		View view = new ImageViewer(v.getContext(), filename);
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
}
