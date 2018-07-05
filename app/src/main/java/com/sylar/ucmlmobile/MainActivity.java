package com.sylar.ucmlmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.administrator.circlegit.R;

public class MainActivity extends Activity {

	private Button btn_enter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);
		setViews();
		setListeners();

		if (Build.VERSION.SDK_INT >= 23) {
			requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, 1);
		}

	}

	private void setListeners() {
		btn_enter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, Main3Activity.class));
			}
		});
		
	}

	private void setViews() {
		btn_enter = (Button)findViewById(R.id.btn_enter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
