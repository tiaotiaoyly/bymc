package com.bspif.app.mobilemechanic;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ListView lv = createListView();
		this.setContentView(lv);
	}
	
	private ListView createListView() {
		ListView lv = new ListView(this);
		ListViewAdapter adapter = new ListViewAdapter();
		ListViewAdapter.Item item = null;
		
		item = adapter.new Item(this, R.layout.list_item_switch, "switch");
		Util.setViewText(item.view, R.id.title, "Maintenance Item Duration ...");
		adapter.add(item);
		
		item = adapter.new Item(this, R.layout.list_item_pop, "pop");
		Util.setViewText(item.view, R.id.title, "Odometer Unit");
		adapter.add(item);
		
		item = adapter.new Item(this, R.layout.list_item_pop, "pop");
		Util.setViewText(item.view, R.id.title, "Gas Volume Unit");
		adapter.add(item);
		
		item = adapter.new Item(this, R.layout.list_item_pop, "pop");
		Util.setViewText(item.view, R.id.title, "Generated Maintenance Report");
		adapter.add(item);
		
		lv.setAdapter(adapter);
		return lv;
	}
	
}
