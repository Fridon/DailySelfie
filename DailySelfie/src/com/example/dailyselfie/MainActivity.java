package com.example.dailyselfie;

import java.io.File;
import java.io.IOException;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends ListActivity {
	 
	private static final String PHOTO_FOLDER = "/daily_selfie/";
	String cameraPath;
	ImageAdapter mAdapter;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);
		
		File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		cameraPath = dcim.getAbsolutePath();
		
		try {
			mAdapter = new ImageAdapter(this, cameraPath + PHOTO_FOLDER);
		} catch (IOException e) {
			e.printStackTrace();
			finish();
		}
		
		setListAdapter(mAdapter);
	 
	}
}
