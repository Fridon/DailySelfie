package com.example.dailyselfie.activities;

import com.example.dailyselfie.R;
import com.example.dailyselfie.R.id;
import com.example.dailyselfie.R.layout;
import com.example.dailyselfie.R.menu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PictureActivity extends Activity implements
		OnSystemUiVisibilityChangeListener, OnClickListener {

	public final int INVISIBLE = View.SYSTEM_UI_FLAG_FULLSCREEN
			| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

	ImageView pictureView;
	LinearLayout pictureLayout;
	View decorView;

	//Runnable for posting to handler
	Runnable mNavHider = new Runnable() {
		@Override
		public void run() {
			hideUI();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_picture);
		decorView = getWindow().getDecorView();
		pictureView = (ImageView) findViewById(R.id.pictureView);
		pictureLayout = (LinearLayout) findViewById(R.id.pictureLayout);
		pictureLayout.setOnClickListener(this);
		decorView.setOnSystemUiVisibilityChangeListener(this);
		decorView.setOnClickListener(this);
		getActionBar().setTitle(getIntent().getStringExtra("FileName"));
		Bitmap bm = decodeFullBitmapFromUri(getIntent().getStringExtra("FileDir"));
		pictureView.setImageBitmap(bm);
	}

	@Override
	public void onResume() {
		super.onResume();
		hideUI();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pictureLayout:
			if (decorView.getSystemUiVisibility() == View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) {
				decorView.getHandler().removeCallbacks(mNavHider);
			}
			decorView.getHandler().postDelayed(mNavHider, 3000);
			break;
		}

	}

	void hideUI() {
		getActionBar().hide();
		decorView.setSystemUiVisibility(INVISIBLE);
	}

	@Override
	public void onSystemUiVisibilityChange(int visibility) {
		if (visibility == 0) {
			getActionBar().show();
			Handler h = decorView.getHandler();
			if (h != null)
				h.postDelayed(mNavHider, 3000);
		}
	}

	private Bitmap decodeFullBitmapFromUri(String path) {
		Bitmap bm = null;
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		bm = BitmapFactory.decodeFile(path, options);

		return bm;
	}
}
