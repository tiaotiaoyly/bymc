package com.bspif.app.mobilemechanic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class AppData {
	
	private static final String CAT_TITLE		= "title";
	private static final String CAT_ICON		= "icon";
	private static final String CAT_LESSONS 	= "content";
	private static final String LESSON_TITLE	= "title";
	private static final String LESSON_PAGES	= "content";
	private static final String LESSON_LINK		= "link";
	private static final String LESSON_INAPP	= "inapp";
	private static final String LESSON_ICON		= "icon";
	private static final String PAGE_IMAGE		= "image";
	private static final String PAGE_TEXT		= "text";
	private static final String LINK_CAT		= "Category";
	private static final String LINK_LESSON		= "SubCategory";
	private static final String LINK_PAGE		= "Page";
	private static final String WEBSITE			= "website";
	private static final String FACEBOOK		= "facebook";
	private static final String TWITTER			= "twitter";
	
	public static CategoryData[] categories = null;
	public static String[] catNames = null;
	public static boolean isInited = false;
	public static boolean isPurchased = false;	// TODO
	
	private AppData() {
	}
	
	public static boolean initialize(Context context) {
		String jsonStr = "";
		try {
			InputStreamReader ireader = new InputStreamReader(context.getAssets().open("AppData.json"));
			BufferedReader br = new BufferedReader(ireader);
			String str = null;
			while((str = br.readLine()) != null) {
				jsonStr += str;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			JSONArray jsonCats = new JSONArray(jsonStr);
			categories = new CategoryData[jsonCats.length()];
			for (int i = 0; i < jsonCats.length(); i ++) {
				categories[i] = new CategoryData(jsonCats.getJSONObject(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.d("appdata", jsonStr);
			return false;
		}
		isInited = true;
		return true;
	}
	
	public static CategoryData getCategory(int catID) {
		assert(isInited);
		return categories[catID];
	}
	
	public static String[] getTitles() {
		assert(isInited);
		if (catNames != null)
			return catNames;
		catNames = new String[categories.length];
		for (int i = 0; i < categories.length; i++) {
			if (null != categories[i]) {
				catNames[i] = categories[i].title;
			} else {
				catNames[i] = i + ": ERROR";
			}
		}
		return catNames;
	}
	
	public static int findCategoryByTitle(String title) {
		for (int i = 0; i < categories.length; i++) {
			if (null != categories[i] && categories[i].title.equals(title)) {
				return i;
			}
		}
		return -1;
	}
	
	/////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////
	
	public static class CategoryData {
		public String title = "";
		public String icon = "";
		public LessonData[] lessons = null;
		public String[] lessonTitles = null;
		public String website = null;
		public String facebook = null;
		public String twitter = null;
		
		CategoryData(JSONObject json) throws JSONException {
			JSONArray jsonLessons = null;
			title = json.getString(CAT_TITLE);
			if (json.has(CAT_LESSONS)) {
				jsonLessons = json.getJSONArray(CAT_LESSONS);
				if (null != jsonLessons) {
					lessons = new LessonData[jsonLessons.length()];
					for (int i = 0; i < jsonLessons.length(); i++) {
						lessons[i] = new LessonData(jsonLessons.getJSONObject(i));
					}
				}
			} else if (json.has(WEBSITE)) {
				website = json.getString(WEBSITE);
			} else if (json.has(FACEBOOK)) {
				facebook = json.getString(FACEBOOK);
			} else if (json.has(TWITTER)) {
				twitter = json.getString(TWITTER);
			}
			if (json.has(CAT_ICON)) {
				icon = json.getString(CAT_ICON);
			}
		}
		
		public String[] getTitles() {
			if (lessonTitles != null)
				return lessonTitles;
			lessonTitles = new String[lessons.length];
			for (int i = 0; i < lessons.length; i ++) {
				if (null != lessons[i]) {
					lessonTitles[i] = lessons[i].title;
				} else {
					lessonTitles[i] = i + ": ERROR ";
				}
			}
			return lessonTitles;
		}

		public LessonData getLesson(int index) {
			return lessons[index];
		}
		
		public int findLessonByTitle(String title) {
			for (int i = 0; i < lessons.length; i++) {
				if (null != lessons[i] && lessons[i].title.equals(title)) {
					return i;
				}
			}
			return -1;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	
	public static class LessonData {
		public String title = "";
		public PageData[] pages = null;
		public LinkData link = null;
		public String icon = null;
		public boolean isInAppBilling = false;
		
		LessonData(JSONObject json) throws JSONException {
			title = json.getString(LESSON_TITLE);
			if (json.has(LESSON_PAGES)) {
				JSONArray jsonPages = json.getJSONArray(LESSON_PAGES);
				if (null != jsonPages) {
					pages = new PageData[jsonPages.length()];
					for (int i = 0; i < jsonPages.length(); i++) {
						pages[i] = new PageData(jsonPages.getJSONObject(i));
					}
				}
			} else if (json.has(LESSON_LINK)) {
				link = new LinkData(json.getJSONObject(LESSON_LINK));
			} else {
				assert(false);
			}
			if (json.has(LESSON_INAPP)) {
				isInAppBilling = json.getBoolean(LESSON_INAPP);
			}
			if (json.has(LESSON_ICON)) {
				icon = json.getString(LESSON_ICON);
			}
		}
		
		public PageData getPage(int index) {
			return pages[index];
		}
		
		public boolean canAccess() {
			return !isInAppBilling || AppData.isPurchased; 
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	
	public static class PageData {
		public String image = "";
		public String text = "";
		
		PageData(JSONObject json) throws JSONException {
			image = json.getString(PAGE_IMAGE);
			text = json.getString(PAGE_TEXT);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////
	
	public static class LinkData {
		public String category = null;
		public String lesson = null;
		public int page = 0;
		
		LinkData(JSONObject json) throws JSONException {
			category = json.getString(LINK_CAT);
			lesson = json.getString(LINK_LESSON);
			page = json.getInt(LINK_PAGE);
		}
	}
}


