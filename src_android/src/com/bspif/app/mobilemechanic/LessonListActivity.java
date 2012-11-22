package com.bspif.app.mobilemechanic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LessonListActivity extends Activity implements OnItemClickListener  {

	private static final String TAG = "SubCat";
	private int catID = -1;
	private AppData.CategoryData catData = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = this.getIntent();
		catID = intent.getExtras().getInt("catID");
		catData = AppData.getCategory(catID);
		
		this.setTitle(catData.title);
		
		String[] titles = catData.getTitles();
		ArrayAdapter<String> ad = new ArrayAdapter<String>(this, R.layout.art_list_item, titles);
		
		ListView lv = new ListView(this);
		lv.setAdapter(ad);
		lv.setOnItemClickListener(this);
		this.setContentView(lv);
	}

	public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
		Log.d(TAG, String.format("on subcat item clicked %d", index));
		AppData.LessonData lessonData = catData.getLesson(index); 
		if (null == lessonData) {
			return;	// DO NOTHING
		}
		// TODO lesson link
		Intent intent = new Intent(this, LessonActivity.class);
		intent.putExtra("catID", catID);
		intent.putExtra("lessonID", index);
		this.startActivity(intent);
	}
}
