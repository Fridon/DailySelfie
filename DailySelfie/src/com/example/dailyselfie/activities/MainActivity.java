package com.example.dailyselfie.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.example.dailyselfie.R;
import com.example.dailyselfie.adapters.ImageAdapter;
import com.example.dailyselfie.alarms.DailySelfieAlarmManager;
import com.example.dailyselfie.dialogs.AlarmDialog;

public class MainActivity extends Activity implements OnItemClickListener{
	 
	public static final String PHOTO_FOLDER = "/daily_selfie/";
	public static final int REQUEST_CODE_PHOTO = 1;
	
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); 
	
	static ImageAdapter mImageAdapter;
	AlarmDialog dialog;
	DailySelfieAlarmManager mManager;
	String cameraPath;
	File currentPhoto;
	GridView gridView;
	
	
	//***********************************************
	//Begin of activity's life-cycle methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		cameraPath = dcim.getAbsolutePath();
		gridView = (GridView)findViewById(R.id.mainGridView);
		try {
			if(mImageAdapter == null)
				mImageAdapter = new ImageAdapter(this, cameraPath + PHOTO_FOLDER);
		} catch (IOException e) {
			e.printStackTrace();
			finish();
		}
		
		gridView.setAdapter(mImageAdapter);
		gridView.setOnItemClickListener(this);
		registerForContextMenu(gridView);
	}
		
	@Override
	public void onResume(){
		super.onResume();
		mManager = new DailySelfieAlarmManager(this);
		dialog = new AlarmDialog(mManager);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    outState.putString("cameraPath", cameraPath);
	    if(currentPhoto != null)
	    	outState.putString("currentPhotoPath", currentPhoto.getAbsolutePath());
	   	}
		
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    cameraPath = savedInstanceState.getString("cameraPath");
	    if(savedInstanceState.getString("currentPhotoPath") != null)
	    	currentPhoto = new File(savedInstanceState.getString("currentPhotoPath"));
	}
	
	//End of activity's life-cycle methods
	//***********************************************
	
	//***********************************************
	//Begin of menu methods
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.actionBarCamera:
	        	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	            intent.putExtra(MediaStore.EXTRA_OUTPUT, generatePhotoURI());
	            startActivityForResult(intent, REQUEST_CODE_PHOTO);
	            return true;
	        case R.id.actionBarAlarm:
	        	dialog.show(getFragmentManager(), "Alarm dialog");
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void onCreateContextMenu (ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		 MenuInflater inflater = getMenuInflater();
		 inflater.inflate(R.menu.item_context_menu, menu);
		 super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	public boolean onContextItemSelected (MenuItem item) {
	
		switch(item.getItemId()){
		case R.id.deleteItem:
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
			int position = info.position;
			File file = (File)mImageAdapter.getItem(position);
			if(deleteFile(file)){
				mImageAdapter.remove(position);
				mImageAdapter.notifyDataSetChanged();
				return true;	
			}
			return false;
		case R.id.deleteAll:
			for(File temp:mImageAdapter.removeAll()){
				deleteFile(temp);
			}
			mImageAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onContextItemSelected(item);	
		}				
	}
	
	//End of menu methods
	//***********************************************
	
	//***********************************************
	//Begin of implementation methods
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK){
			
        	//Preventing bug "https://code.google.com/p/android/issues/detail?id=38282"
        	//You can see this bug at devices with MTP such as Nexus 5 and Nexus 7.	
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(currentPhoto)));
			
			//Adding new photo to list
			mImageAdapter.add(currentPhoto);
			mImageAdapter.notifyDataSetChanged();	    
		}
		super.onActivityResult(requestCode, resultCode, intent);

	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, PictureActivity.class);
		File photo = (File)mImageAdapter.getItem(position);
		intent.putExtra("FileDir", photo.getAbsolutePath());
		intent.putExtra("FileName", photo.getName());
		startActivity(intent);	
	}
	
	
	//End of implementation methods
	//***********************************************
	
	//***********************************************
	//Begin of helping methods
	private Uri generatePhotoURI(){
		Date date = new Date(System.currentTimeMillis());
		currentPhoto = new File(cameraPath + PHOTO_FOLDER + DATE_FORMAT.format(date) + ".jpg");		
		return Uri.fromFile(currentPhoto);
	}
	
	private boolean deleteFile(File file){
		boolean result =  file.delete();
		//Preventing bug "https://code.google.com/p/android/issues/detail?id=38282"
    	//You can see this bug at devices with MTP such as Nexus 5 and Nexus 7.	
		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
		return result;
	}
	//End of helping methods
	//***********************************************	
}
