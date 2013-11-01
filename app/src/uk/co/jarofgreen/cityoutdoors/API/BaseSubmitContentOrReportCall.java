package uk.co.jarofgreen.cityoutdoors.API;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import uk.co.jarofgreen.cityoutdoors.OurApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.Log;

public abstract class BaseSubmitContentOrReportCall extends BaseCall {

	public BaseSubmitContentOrReportCall(InformationNeededFromContext informationNeededFromContext) {
		super(informationNeededFromContext);
	}


	public BaseSubmitContentOrReportCall(Context context, OurApplication ourApplication) {
		super(context, ourApplication);
	}


	protected int featureID;
	protected float lat;
	protected float lng;
	protected String comment;
	protected String name;
	protected String photoFileName;
	protected PhotoDetails photoDetails = null;	
	
	protected class PhotoDetails {
		public boolean deleteAfterUse = false;
		public String fileName;
		public PhotoDetails(boolean deleteAfterUse, String fileName) {
			super();
			this.deleteAfterUse = deleteAfterUse;
			this.fileName = fileName;
		}
	}

	public PhotoDetails getPhotoDetailsForSending(String photoFileName) throws Exception {
		File inputFile = new File(photoFileName);
		long inputFilelength = inputFile.length();
		//Log.i("ORIGFILESIZE",Long.toString(inputFilelength));

		int maxFileLength = informationNeededFromContext.getSettings().getInt("uploadsMaxSize", 1024*1024);  // assume 1MB if not set.
		// maxFileLength = 512*1024; // for testing you may want to make the limit really low 
		//Log.i("MAXFILESIZE",Integer.toString(maxFileLength));

		if (inputFilelength > maxFileLength) {
			//Log.i("RESULT","Must Resize!");

			Float scale = 0.9f;

			File outputFile = File.createTempFile("photo", ".jpg", informationNeededFromContext.getCacheDir());
			long outputFileLength = inputFilelength;

			FileInputStream inFile = new FileInputStream(inputFile);
			Bitmap inputBitmap = new BitmapDrawable(inFile).getBitmap();
			inFile.close();

			while (outputFileLength > maxFileLength && scale > 0.1) {

				//Log.i("SCALE",Float.toString(scale));
				int scaleWidth = (int)(inputBitmap.getWidth() * scale);
				int scaleHeight = (int)(inputBitmap.getHeight() * scale);
				//Log.i("NEWWIDTH",Integer.toString(scaleWidth));
				//Log.i("NEWHEIGHT",Integer.toString(scaleHeight));
				Bitmap newBitmap = Bitmap.createScaledBitmap(inputBitmap, scaleWidth, scaleHeight, true);

				FileOutputStream fos = new FileOutputStream(outputFile.getAbsolutePath());
				newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
				fos.close();

				outputFileLength = outputFile.length();
				//Log.i("NEWSIZE",Long.toString(outputFileLength));
				scale = scale * 0.9f;

			}

			return new PhotoDetails(true, outputFile.getAbsolutePath());


		} else {
			Log.i("RESULT","Just use original file");
			return new PhotoDetails(false, photoFileName);
		}
	}
	
	
	public void cleanUp(Context context) {
		if (photoDetails != null && photoDetails.deleteAfterUse) {
			context.deleteFile(photoDetails.fileName);
			photoDetails = null;
		}
	}
	
}



