package com.example.dailyselfie.adapters;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dailyselfie.R;

public class ImageAdapter extends BaseAdapter {
	private Activity activity;
	private ArrayList<ViewBitmapHandler> files;

	public ImageAdapter(Activity activity, String fileDir) throws IOException {
		super();
		files = new ArrayList<ViewBitmapHandler>();
		this.activity = activity;
		File targetDirector = new File(fileDir);
		if (!targetDirector.exists()) {
			if (!targetDirector.mkdirs()) {
				Toast.makeText(activity, "Dirrectory not created",Toast.LENGTH_LONG).show();
				throw new IOException();
			}
		}
		File[] temp = targetDirector.listFiles();
		if (temp != null) {
			for (File file : temp)
				files.add(new ViewBitmapHandler(file));
		}

	}

	public void add(File newFile) {
		files.add(new ViewBitmapHandler(newFile));
	}

	public void remove(int index) {
		files.remove(index);
	}

	public ArrayList<ViewBitmapHandler> removeAll() {
		ArrayList<ViewBitmapHandler> temp = files;
		files = new ArrayList<ViewBitmapHandler>();
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

		if (convertView == null) {
			convertView = activity.getLayoutInflater().inflate(
					R.layout.list_item, null, false);
		}
		ViewBitmapHandler current = files.get(position);
		ImageView photoPreview = (ImageView) convertView.findViewById(R.id.photoPreview);
		photoPreview.setImageResource(R.drawable.loading);
		// if preview not loaded - loading bitmap
		if (current.getBitmap() == null) {
			ImageLoader loader = new ImageLoader(photoPreview, current);
			loader.execute(current.getFile().getAbsolutePath());
		} else {
			photoPreview.setImageBitmap(current.getBitmap());
		}

		return convertView;
	}

	// class for loading image previews in background thread
	public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> imageViewReference;
		private final WeakReference<ViewBitmapHandler> handlerReference;
		String filePath = "";

		public ImageLoader(ImageView imageView, ViewBitmapHandler handler) {
			imageViewReference = new WeakReference<ImageView>(imageView);
			handlerReference = new WeakReference<ViewBitmapHandler>(handler);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			filePath = params[0];
			return decodeSampledBitmapFromUri(filePath, 150, 150);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (imageViewReference != null && bitmap != null
					&& handlerReference != null) {
				final ImageView imageView = imageViewReference.get();
				final ViewBitmapHandler handler = handlerReference.get();
				if (imageView != null) {
					imageView.setImageBitmap(bitmap);
					handler.setBitmap(bitmap);
				}
			}
		}

		private Bitmap decodeSampledBitmapFromUri(String path, int reqWidth,
				int reqHeight) {
			Bitmap bm = null;

			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, options);

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

			// Decode square bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			try {
				BitmapRegionDecoder BRD = BitmapRegionDecoder.newInstance(path, true);
				Rect rect = decodeBounds(options);
				bm = BRD.decodeRegion(rect, options);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bm;
		}

		private int calculateInSampleSize(BitmapFactory.Options options,
				int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {
				if (width > height) {
					inSampleSize = Math.round((float) height
							/ (float) reqHeight);
				} else {
					inSampleSize = Math.round((float) width / (float) reqWidth);
				}
			}

			return inSampleSize;
		}

		private Rect decodeBounds(BitmapFactory.Options options) {
			Rect rect = new Rect();
			if (options.outWidth < options.outHeight) {
				rect.left = 0;
				rect.right = options.outWidth;
				rect.top = (options.outHeight - options.outWidth) / 2;
				rect.bottom = options.outHeight - rect.top;
			} else {
				rect.top = 0;
				rect.bottom = options.outHeight;
				rect.left = (options.outWidth - options.outHeight) / 2;
				rect.right = options.outWidth - rect.left;
			}

			return rect;
		}

	}

	// class for handling loaded bitmap
	public class ViewBitmapHandler {
		private File file;
		private Bitmap bitmap;

		public ViewBitmapHandler(File file, Bitmap bitmap) {
			this.file = file;
			this.bitmap = bitmap;
		}

		public ViewBitmapHandler(File file) {
			this(file, null);
		}

		public ViewBitmapHandler() {
			this(null, null);
		}

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}

		public Bitmap getBitmap() {
			return bitmap;
		}

		public void setBitmap(Bitmap bitmap) {
			this.bitmap = bitmap;
		}

	}

}
