package com.example.dailyselfie;

import java.io.File;
import java.io.IOException;

import android.app.ListActivity;
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
import android.widget.ListView;

public class MainActivity extends ListActivity {
	 
	public static final String PHOTO_FOLDER = "/daily_selfie/";
	public static final int REQUEST_CODE_PHOTO = 1;
	
	static ImageAdapter mAdapter;
	String cameraPath;
	File currentPhoto;
	
	
	//***********************************************
	//Begin of activity's life-cycle methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		cameraPath = dcim.getAbsolutePath();
		
		try {
			if(mAdapter == null)
				mAdapter = new ImageAdapter(this, cameraPath + PHOTO_FOLDER);
		} catch (IOException e) {
			e.printStackTrace();
			finish();
		}
		
		registerForContextMenu(getListView());
		setListAdapter(mAdapter);		
	}
	
	@Override
	public void onStop(){
		super.onStop();
	}

	@Override
	public void onRestart(){
		super.onRestart();
	}
		
	@Override
	public void onStart(){
		super.onStart();
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
			File file = (File)mAdapter.getItem(position);
			if(deleteFile(file)){
				mAdapter.remove(position);
				mAdapter.notifyDataSetChanged();
				return true;	
			}
			return false;
		case R.id.deleteAll:
			for(File temp:mAdapter.removeAll()){
				deleteFile(temp);
			}
			mAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onContextItemSelected(item);	
		}				
	}
	
	//End of menu methods
	//***********************************************
	
	//***********************************************
	//Begin of implementation methods
	protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
		if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK){
			
        	//Preventing bug "https://code.google.com/p/android/issues/detail?id=38282"
        	//You can see this bug at devices with MTP such as Nexus 5 and Nexus 7.	
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(currentPhoto)));
			
			//Adding new photo to list
			mAdapter.add(currentPhoto);
			mAdapter.notifyDataSetChanged();	    
		}
		super.onActivityResult(requestCode, resultCode, intent);

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id){
		try {
			Class clazz = Class.forName("com.example.dailyselfie.PictureActivity");
			Intent intent = new Intent(this, clazz);
			File photo = (File)mAdapter.getItem(position);
			Uri fileUri = Uri.fromFile(photo);
			intent.putExtra("FileDir", fileUri.getEncodedPath());
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//End of implementation methods
	//***********************************************
	
	//***********************************************
	//Begin of helping methods
	private Uri generatePhotoURI(){
		currentPhoto = new File(cameraPath +"/"+ PHOTO_FOLDER + System.currentTimeMillis() + ".jpg");		
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
