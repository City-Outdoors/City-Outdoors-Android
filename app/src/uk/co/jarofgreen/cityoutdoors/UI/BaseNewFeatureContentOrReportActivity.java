package uk.co.jarofgreen.cityoutdoors.UI;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.jarofgreen.cityoutdoors.R;
import uk.co.jarofgreen.cityoutdoors.Model.BaseUploadContentOrReport;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class BaseNewFeatureContentOrReportActivity extends BaseActivity  {

	protected static final int ACTION_TAKE_PHOTO = 1;
	protected static final int ACTION_SELECT_PHOTO = 2;
	
	protected static final String JPEG_FILE_PREFIX = "IMG_";
	protected static final String JPEG_FILE_SUFFIX = ".jpg";
	
	protected String photoFileName;
	
	protected LocationManager locationManager;
	
	protected BaseUploadContentOrReport uploadData;
	
    protected void promptForPosition() {
        
    	if (!uploadData.hasPosition()) {

    		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    		final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        	if (!gpsEnabled) {
        		
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setMessage("Please turn on GPS so we can get your current position.")
	        		.setCancelable(false)
	        		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int id) {
	        				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	
	        			}
	        		});
        		AlertDialog alert = builder.create();
        		alert.show();
        	}
        	
        }
        
        
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	if (!uploadData.hasPosition()) locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public void onStop() {
    	if (locationManager != null) locationManager.removeUpdates(locationListener);
    	super.onStop();
    }


    protected File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			/**if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				storageDir = new File(
						  Environment.getExternalStoragePublicDirectory(
						    Environment.DIRECTORY_PICTURES
						  ), 
						  getString(R.string.photo_album_name)
						);
			} else { **/
				storageDir =new File (
						Environment.getExternalStorageDirectory()
						+ "/dcim/"
						+ getString(R.string.photo_album_name)
				);
			//}

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
    

    public void onClickSelectPhoto(View v) {
    	Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	startActivityForResult(i, ACTION_SELECT_PHOTO);
    }
    
    public void onClickTakePhoto(View v) {
    	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	
    	
    	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF;
		try {
			imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		} catch (IOException e) {
			// TODO 
			e.printStackTrace();
			return;
		}
		Log.d("PHOTO","Should Save in "+imageF.getAbsolutePath());
		photoFileName = imageF.getAbsolutePath();
    	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageF));
    	
    	startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	if (resultCode != RESULT_OK) {
    		photoFileName= null;
    		return;
    	}
    	
    	if (requestCode == ACTION_TAKE_PHOTO && resultCode == RESULT_OK) {
    		Log.d("PHOTO","Got Photo Back (Taken)");
    		
    	} else if (requestCode == ACTION_SELECT_PHOTO && resultCode == RESULT_OK) {
    		Log.d("PHOTO","Got Photo Back (selected)");
    		
    		Uri selectedImage = intent.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
    
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
    
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            photoFileName = cursor.getString(columnIndex);
            cursor.close();
    		
    	}
    	
    	
    	if (photoFileName != null) {
    		
    		uploadData.setPhotoFileName(photoFileName);
    		
			ImageView ivPhoto = (ImageView)findViewById(R.id.photo_preview);
			ivPhoto.setImageBitmap(uploadData.getPhotoThumbNailBitmap());
			ivPhoto.setVisibility(View.VISIBLE);
    	}
    }
    
    protected final LocationListener locationListener = new LocationListener() {
    	public void onLocationChanged(Location location) {
	    	uploadData.setLatLng((float)location.getLatitude(),(float)location.getLongitude());
    	}

    	public void onStatusChanged(String provider, int status, Bundle extras) {}

    	public void onProviderEnabled(String provider) {}

    	public void onProviderDisabled(String provider) {
    	}
    };    
    
	
}
