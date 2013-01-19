package com.bspif.app.mobilemechanic;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	public class Item {
		public boolean isSeperator = false;
		public View view = null;
		public String type = null;
		public String name = null;
		public int arg = 0;
		
		public Item(View view, String type) {
			this.view = view;
			this.type = type;
		}
		public Item(Context context, int resID, String type) {
			LayoutInflater inflater = LayoutInflater.from(context);
			this.view = inflater.inflate(R.layout.cat_list_seperator, null);
			this.type = type;
		}
	}
	
	public class SeperatorItem extends Item {
		public SeperatorItem(Context context, String title) {
			super(context, R.layout.cat_list_seperator, "Serperator");
			this.isSeperator = true;
			TextView tv = (TextView) this.view.findViewById(R.id.seperator_title);
			tv.setText(title);
		}
	}

	//////////////////////////////////////
	
	private ArrayList<Item> mItems = new ArrayList<Item>();
	
	
	public ListViewAdapter() {
		// DO NOTHING
	}
	
	public void add(Item item) {
		mItems.add(item);
	}
	
	public Item get(int index) {
		return mItems.get(index);
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	
	@Override
	public int getCount() {
		return mItems.size();
	}
	
	@Override
	public boolean isEnabled(int position) {
		return !mItems.get(position).isSeperator;
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return mItems.get(position).view;
	}
}
