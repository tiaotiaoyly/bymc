package com.bspif.app.mobilemechanic;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class SettingsActivity extends Activity implements OnItemSelectedListener, OnCheckedChangeListener {

	public static final String KEY_DURATION = "duration";
	public static final String KEY_ODO_UNIT = "odo_unit";
	public static final String KEY_GAS_UNIT = "gas_unit";
	public static final String KEY_REPORT_TIME = "report_time";
	
	private static final String TAG = "Settings";
	ArrayList<String> mNames = new ArrayList<String>();
	
	
	public boolean getDuration() {
		try {
			return AppData.getBoolean(KEY_DURATION);
		} catch (JSONException e) {
			return false;
		}
	}
	
	public int getOdoUnit() {
		try {
			return AppData.getInt(KEY_ODO_UNIT);
		} catch (JSONException e) {
			return 0;
		}
	}
	
	public int getGasUnit() {
		try {
			return AppData.getInt(KEY_GAS_UNIT);
		} catch (JSONException e) {
			return 0;
		}
	}
	
	public int getReportTime() {
		try {
			return AppData.getInt(KEY_REPORT_TIME);
		} catch (JSONException e) {
			return 0;
		}
	}
	
	////////////////////////////////
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ListView lv = createListView();
		this.setContentView(lv);
		this.setTitle("Settings");
	}
	
	private ListView createListView() {
		ListView lv = new ListView(this);
		ListViewAdapter adapter = new ListViewAdapter();
		ListViewAdapter.Item item = null;
		
		boolean valueDuration = this.getDuration();
		int valueOdoUnit = this.getOdoUnit();
		int valueGasUnit = this.getGasUnit();
		int valueReportTime = this.getReportTime();
		
		item = adapter.new Item(this, R.layout.list_item_toggle, "toggle");
		Util.setViewText(item.view, R.id.title, "Maintenance Item Duration ...");
		setupToggle((ToggleButton)item.view.findViewById(R.id.toggle), KEY_DURATION, valueDuration);
		adapter.add(item);
		
		item = adapter.new Item(this, R.layout.list_item_pop, "spinner");
		Util.setViewText(item.view, R.id.title, "Odometer Unit");
		setupSpinner(item.view, R.id.spinner, R.array.odo_units, KEY_ODO_UNIT, valueOdoUnit);
		adapter.add(item);
		
		item = adapter.new Item(this, R.layout.list_item_pop, "spinner");
		Util.setViewText(item.view, R.id.title, "Gas Volume Unit");
		setupSpinner(item.view, R.id.spinner, R.array.gas_unit, KEY_GAS_UNIT, valueGasUnit);
		adapter.add(item);
		
		item = adapter.new Item(this, R.layout.list_item_pop, "spinner");
		Util.setViewText(item.view, R.id.title, "Generated Maintenance Report");
		setupSpinner(item.view, R.id.spinner, R.array.report_time_range, KEY_REPORT_TIME, valueReportTime);
		adapter.add(item);
		
		lv.setAdapter(adapter);
		return lv;
	}
	
	private void setupToggle(ToggleButton toggle, String name, boolean checked) {
		int id = mNames.size();
		mNames.add(name);
		toggle.setId(id);
		toggle.setChecked(checked);
		toggle.setOnCheckedChangeListener(this);
	}
	
	private void setupSpinner(View view, int spinID, int textArrayID, String name, int selected) {
		Spinner spinner = (Spinner) view.findViewById(spinID);
		ArrayAdapter<CharSequence> a = ArrayAdapter.createFromResource(this, textArrayID, android.R.layout.simple_spinner_item);
		a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(a);
		int id = mNames.size();
		mNames.add(name);
		spinner.setId(id);
		spinner.setSelection(selected);
		spinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id = buttonView.getId();
		String name = mNames.get(id);
		Log.v(TAG, "on clicked %s %s", name, isChecked);
		AppData.put(name, isChecked);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> toggle, View view, int index,
			long arg) {
		int id = toggle.getId();
		String name = mNames.get(id);
		Log.v(TAG, "on click %s %s", name, index);
		AppData.put(name, index);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// DO NOTHING
	}

	
}
