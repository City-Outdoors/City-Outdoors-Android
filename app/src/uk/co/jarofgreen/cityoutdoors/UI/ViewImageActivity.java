package uk.co.jarofgreen.cityoutdoors.UI;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.R;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class ViewImageActivity extends BaseActivity  implements OnClickListener  {

	ProgressDialog mDialog;
	
	List<Content> content;
	int contentIDX;
	int featureContentID = 0;
	int featureID;
	
	DownloadImageTask downLoadImageTask;
	

	 private final int SWIPE_MIN_DISTANCE = 300;
	 private final int SWIPE_MAX_OFF_PATH = 250;
	 private GestureDetector gestureDetector;
	 View.OnTouchListener gestureListener;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_image);
        
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureContentID = extras.getInt("featureContentID");
			Log.d("FEATURECONTENTID",Integer.toString(featureContentID));
			featureID = extras.getInt("featureID");
			Log.d("FEATUREID",Integer.toString(featureID));
		}
		
		OurApplication appState = ((OurApplication)getApplication());
		content = appState.getFeatureContent(featureID);
		
		for (int i = 0; i < content.size(); i++) {
			if (content.get(i).getId() == featureContentID) {
				contentIDX = i;
			}
		}

    	mDialog = new ProgressDialog(this);
    	mDialog.setMessage("Loading, please wait ...");
    	mDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
            	downLoadImageTask.cancel(true);
            	ViewImageActivity.this.finish();
            }
        });		
		
		loadImage();
		
        if (content.size() > 1) {
	        gestureDetector = new GestureDetector(new MyGestureDetector());
	        gestureListener = new View.OnTouchListener() {
	        	public boolean onTouch(View v, MotionEvent event) {
	        		return gestureDetector.onTouchEvent(event);
	        	}
	        };
	    
	        View v = findViewById(R.id.image);
	        v.setOnClickListener(this); 
	        v.setOnTouchListener(gestureListener);
        }
        
    }
    
    protected void loadImage() {
    	mDialog.show();	
    	
    	if (downLoadImageTask != null) {
    		downLoadImageTask.cancel(true);
    	}
    	
    	Content thisContent = content.get(contentIDX);
    	downLoadImageTask = new DownloadImageTask(thisContent.getPictureFullURL());
    	downLoadImageTask.execute(true);
    }
    


	 protected void prevImage() {
		 if (contentIDX == 0) { contentIDX = content.size() -1; } else { --contentIDX; }
		 loadImage();		 
	 }

	 protected void nextImage() {
		 ++contentIDX;
		 if (contentIDX == content.size()) { contentIDX = 0; }
		 loadImage();		 
	 }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (content.size() > 1) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.view_image_menu, menu);
	        return true;
    	} else {
    		return false;
    	}
    }
    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.prev_image:
    		prevImage();
    		return true;
    	case R.id.next_image:
    		nextImage();
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }	 
      
    
    private class DownloadImageTask extends AsyncTask<Boolean, Void, Boolean> {

    	Drawable draw;
    	String imageURL;
    	
		public DownloadImageTask(String imageURL) {
			super();
			this.imageURL = imageURL;			
		}

		protected Boolean doInBackground(Boolean... dummy) {
			try {
				URL url = new URL(imageURL);
				InputStream is = (InputStream)  url.getContent();
				draw = Drawable.createFromStream(is, "src");
			} catch (Exception e) {
				return null;
			}
			return null;
		}


		protected void onPostExecute(Boolean result) {
			if (!isCancelled()) { 
				ImageView iv = (ImageView)findViewById(R.id.image);
				iv.setImageDrawable(draw);
				
				mDialog.dismiss();
			}
		}
		
	}	
    


    class MyGestureDetector extends SimpleOnGestureListener {
    	@Override
    	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    		Log.i("ONFLING","DIST Y "+ Float.toString(Math.abs(e1.getY() - e2.getY())));
    		if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;
    		Log.i("ONFLING"," DIST " + Float.toString(e1.getX() - e2.getX()));
    		if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
    			nextImage();
    		}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
    			prevImage();
    		}
    		return false;
    	}
    }



	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}


    
}
