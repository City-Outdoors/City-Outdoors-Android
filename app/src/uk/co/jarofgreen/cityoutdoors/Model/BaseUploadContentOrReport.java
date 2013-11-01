package uk.co.jarofgreen.cityoutdoors.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

public abstract class BaseUploadContentOrReport {

	public static final int THUMBNAIL_SIZE = 100;

	protected String comment;
	protected String name;
	protected String photoFileName;
	protected String photoFileNameForUpload;
	protected int maxFileLength;
	protected File cacheDirectory;
	protected int featureID = -1;
	protected float lat = 0;
	protected float lng = 0;

	public BaseUploadContentOrReport(Context context) {
		super();
		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(context);
		maxFileLength = settings.getInt("uploadsMaxSize", 2*1024*1024);  // assume 2MB if not set, that is PHP standard on Debian.
		cacheDirectory = context.getCacheDir();
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPhotoFileNameForUpload() {

		if (photoFileNameForUpload != null) {
			return photoFileNameForUpload;
		}	

		try {

			File inputFile = new File(photoFileName);
			long inputFilelength = inputFile.length();
			//Log.i("ORIGFILESIZE",Long.toString(inputFilelength));

			//maxFileLength = 128*1024; // for testing you may want to make the limit really low 
			//Log.i("MAXFILESIZE",Integer.toString(maxFileLength));


			if (inputFilelength > maxFileLength) {
				//Log.i("RESULT","Must Resize!");

				File outputFile = File.createTempFile("photo", ".jpg", cacheDirectory);
				long outputFileLength = inputFilelength;

				int factor = 1;

				while (outputFileLength > maxFileLength) {

					factor = factor * 2;

					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inSampleSize = factor;

					Bitmap newBitmap = BitmapFactory.decodeFile(photoFileName, opts);

					FileOutputStream fos = new FileOutputStream(outputFile.getAbsolutePath());
					newBitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
					fos.close();

					outputFileLength = outputFile.length();
					//Log.i("NEWSIZE",Long.toString(outputFileLength));

				}

				photoFileNameForUpload = outputFile.getAbsolutePath();
				return photoFileNameForUpload;

			} else {
				//Log.i("RESULT","Just use original file");
				return photoFileName;
			}

		} catch (IOException e) {
			return photoFileName;
		} catch (OutOfMemoryError e) {	
			return photoFileName;
		}


	}

	public String getPhotoFileName() {
		return photoFileName;
	}

	public boolean hasPhoto() {
		return photoFileName != null;
	}


	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}


	public void cleanUp(Context context) {
		if (photoFileNameForUpload != null) { 
			File f = new File(photoFileNameForUpload);
			f.delete();
			photoFileNameForUpload = null;
		}
	}

	public Bitmap getPhotoThumbNailBitmap() {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(photoFileName, opts);
			int filenameWidth = opts.outHeight;
			int filenameHeight = opts.outWidth;

			int factor = 1;
			while ( Math.max(filenameHeight / (factor * 2), filenameWidth / (factor * 2)) > THUMBNAIL_SIZE) {
				factor = factor * 2;
			}

			//Log.i("THUMB_FACTOR OF",Integer.toString(factor));


			//Log.i("THUMB_NEWSCALEWIDTH",Integer.toString(filenameWidth / factor));
			//Log.i("THUMB_NEWSCALEHEIGHT",Integer.toString(filenameHeight / factor));

			BitmapFactory.Options optsOut = new BitmapFactory.Options();
			optsOut.inSampleSize = factor;
			return BitmapFactory.decodeFile(photoFileName, optsOut);

		} catch (OutOfMemoryError e) {	
			return null;
		}
	}

	public boolean hasFeatureID() {
		return featureID > -1;
	}

	public int getFeatureID() {
		return featureID;
	}


	public void setFeatureID(int featureID) {
		this.featureID = featureID;
	}


	public float getLat() {
		return lat;
	}

	public boolean hasLatLng() {
		// technically this could cause problems for others as 0,0 is a valid position but it won't for Edinburgh so left for new.
		return (lat != 0.0 || lng != 0.0);
	}

	public void setLatLng(float lat, float lng) {
		this.lat = lat;
		this.lng = lng;
	}


	public float getLng() {
		return lng;
	}

	public boolean hasPosition() {
		return hasFeatureID() || hasLatLng();
	}


}

