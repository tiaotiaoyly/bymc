package com.bspif.app.mobilemechanic;

import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        AppData.initialize(this);

        Button btn = (Button) this.findViewById(R.id.main_btn);
        btn.setOnClickListener(this);
    }

	public void onClick(View v) {
		Intent it = new Intent(this, CategoryListActivity.class);
		this.startActivity(it);
		
		ActionReceiver.startService(this);
	}
}