package com.example.dailyselfie.adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dailyselfie.R;

public class ImageAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<File> files;
	
	public ImageAdapter(Activity activity, String fileDir) throws IOException{
		super();
		files = new ArrayList<File>();
		this.activity = activity;
		File targetDirector = new File(fileDir);       	        
        if(!targetDirector.exists()){
        	if(!targetDirector.mkdirs()){ 
        		Toast.makeText(activity, "Dirrectory not created", Toast.LENGTH_LONG).show();
        		throw new IOException();
        	}
        }
        File[] temp = targetDirector.listFiles();
        if(temp != null){
        	for(File file : temp)
        		files.add(file);
        }
	}
	
	public void add(File newFile){
		files.add(newFile);
	}
	
	public void remove(int index){
		files.remove(index);
	}
	
	public ArrayList<File> removeAll(){
		ArrayList<File> temp = files;
		files = new ArrayList<File>();
		return temp;
	}
	
	@Override
	public int getCount() {
		return files.size();
	}

	@Override
	public Object getItem(int position) {
		return files.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null){
			convertView = activity.getLayoutInflater().inflate(R.layout.list_item, null, false);
		}
		File current = files.get(position);
		ImageView photoPreview  = (ImageView)convertView.findViewById(R.id.photoPreview);
		try {
			Bitmap	image = decodeSampledBitmapFromUri(current.getAbsolutePath(), 150, 150);
			photoPreview.setImageBitmap(image);	
		} catch (IOException e) {
			files.remove(position);
			notifyDataSetChanged();
		}
		return convertView;
	}
	
	private Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) throws IOException {
    	Bitmap bm = null;
    	
    	// First decode with inJustDecodeBounds=true to check dimensions
    	final BitmapFactory.Options options = new BitmapFactory.Options();
    	options.inJustDecodeBounds = true;
    	BitmapFactory.decodeFile(path, options);
    	
    	// Calculate inSampleSize
    	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
    	
    	// Decode square bitmap with inSampleSize set
    	options.inJustDecodeBounds = false;
    	BitmapRegionDecoder BRD = BitmapRegionDecoder.newInstance(path, true);
    	Rect rect = decodeBounds(options);
    	bm = BRD.decodeRegion(rect, options);
    	
    	return bm; 	
    }
    
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    	// Raw height and width of image
    	final int height = options.outHeight;
    	final int width = options.outWidth;
    	int inSampleSize = 1;
        
    	if (height > reqHeight || width > reqWidth) {
    		if (width > height) {
    			inSampleSize = Math.round((float)height / (float)reqHeight);  	
    		} else {
    			inSampleSize = Math.round((float)width / (float)reqWidth);  	
    		}  	
    	}
    	
    	return inSampleSize;  	
    }
    
    private Rect decodeBounds(BitmapFactory.Options options){
    	Rect rect = new Rect();
    	if (options.outWidth < options.outHeight) {
    		rect.left = 0;
    		rect.right = options.outWidth;
    		rect.top = (options.outHeight - options.outWidth)/2;
    		rect.bottom = options.outHeight - rect.top;
		} else {
			rect.top = 0;
			rect.bottom = options.outHeight;
			rect.left = (options.outWidth - options.outHeight)/2;
			rect.right = options.outWidth - rect.left;
		} 
    	
    	return rect;
    }

}
