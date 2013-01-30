package uk.co.jarofgreen.cityoutdoors.UI;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.API.FeatureCall;
import uk.co.jarofgreen.cityoutdoors.API.LogInCall;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import uk.co.jarofgreen.cityoutdoors.Model.ItemField;
import uk.co.jarofgreen.cityoutdoors.Service.SendFeatureFavouriteService;
import uk.co.jarofgreen.cityoutdoors.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureActivity extends Activity {

	int featureID;
	boolean hasCheckinQuestions = false;
	ProgressDialog mDialog;
	FeatureTask featureTask;
	DownloadImagesTask downloadImagesTask;
	String shareURL;
	
	String email;
	String telephone;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feature);  

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureID = extras.getInt("featureID");
			Log.d("FEATURE from INTENT",Integer.toString(featureID));
		}
				
		featureTask = new FeatureTask(featureID);
		
		mDialog = new ProgressDialog(this);
    	mDialog.setMessage("Loading, please wait ...");
    	mDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
            	featureTask.cancel(true);
            	FeatureActivity.this.finish();
            }
        });
    	mDialog.setCancelable(true);
    	mDialog.show();

		featureTask.execute(true);

	}

	public void onClickCheckIn(View v) {
		Intent i = new Intent(this, FeatureCheckinQuestionsActivity.class);
		i.putExtra("featureID", featureID);
		startActivity(i);		
	}

	public void onClickFavourite(View v) {
		Storage s = new Storage(this);
		s.featureFavourite(featureID, false);
		startService(new Intent(this, SendFeatureFavouriteService.class));
		Toast.makeText(this, "This has been added to your favourites", Toast.LENGTH_SHORT).show();
	}
	
	public void onClickShare(View v) {
		Intent intent=new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		String shareTxt = shareURL;
		intent.putExtra(Intent.EXTRA_SUBJECT, shareTxt);
		intent.putExtra(Intent.EXTRA_TEXT, shareTxt);
		
		startActivity(Intent.createChooser(intent, "How do you want to share?"));
	}	
	
	public void onClickAction(View v) {
		openOptionsMenu();
	}
	
	public void onClickNewFeatureContent(View v) {
		Intent i = new Intent(this, NewFeatureContentActivity.class);
		i.putExtra("featureID", featureID);
		startActivity(i);
	}
	

	public void onClickNewFeatureReport(View v) {
		Intent i = new Intent(this, NewFeatureReportActivity.class);
		i.putExtra("featureID", featureID);
		startActivity(i);
	}
	
	public void onClickFeatureContentPicture(View v) {
		Integer idx = (Integer)v.getTag();
		Intent i = new Intent(this, ViewImageActivity.class);
		i.putExtra("featureContentID", idx);
		i.putExtra("featureID", featureID);
		startActivity(i);
	}
	
	public void onClickMap(View v) {
		Storage s = new Storage(this);
		Feature f = s.getFeature(featureID);
		Log.d("CLICK",Integer.toString(featureID));
		if (f != null) {
			Intent i = new Intent(this, BrowseMapActivity.class);
			i.putExtra("lat", f.getLat());
			i.putExtra("lng", f.getLng());
			startActivity(i);
		}
	}
	

	public void onClickFieldEmail(View v) {
		String email = (String)v.getTag();
		startIntentForEmailAddress(email);
	}
	
	protected void startIntentForEmailAddress(String email) {
		Intent intent = new Intent(Intent.ACTION_SEND); 
		intent.setType("plain/text");
		String emailAddressList[] = { email };
		intent.putExtra(Intent.EXTRA_EMAIL, emailAddressList);  
		//intent.putExtra(Intent.EXTRA_SUBJECT, ''); 
		
		try {
			startActivity(Intent.createChooser(intent, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this,"There are no email clients installed.",Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onClickFieldPhone(View v) {
		final String phone = ((String)v.getTag()).trim();
		startIntentForPhoneNumber(phone);
	}

	protected void startIntentForPhoneNumber(String phone) {
		String uri = "tel:" + phone;
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse(uri));
		startActivity(intent);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feature_menu, menu);
        return true;
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
       	menu.findItem(R.id.checkin).setVisible(hasCheckinQuestions);
       	if (email == null) {
       		menu.findItem(R.id.email).setVisible(false);
       	}
       	if (telephone == null) {
       		menu.findItem(R.id.phone).setVisible(false);
       	}       	
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.checkin:
        	onClickCheckIn(null);
            return true;
        case R.id.favourite:
        	onClickFavourite(null);
            return true;
        case R.id.comment:
        	onClickNewFeatureContent(null);
            return true;
        case R.id.report:
        	onClickNewFeatureReport(null);
            return true;
        case R.id.map:
        	onClickMap(null);
            return true;
        case R.id.share:
        	onClickShare(null);
            return true;                 
        case R.id.phone:
        	startIntentForPhoneNumber(telephone);
        	return true;
        case R.id.email:
        	startIntentForEmailAddress(email);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	

	private class FeatureTask extends AsyncTask<Boolean, Void, Boolean> {

		protected int featureID;
		protected FeatureCall call;

		public FeatureTask(int featureID) {
			super();
			this.featureID = featureID;
		}

		protected Boolean doInBackground(Boolean... dummy) {

			try{

				call = new FeatureCall(FeatureActivity.this);
				call.execute(featureID);

			} catch(Exception e) {
				Log.d("ERRORINLOGIN",e.toString());
				if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
			}

			return false;

		}

		protected void onPostExecute(Boolean result) {
			FeatureActivity.this.mDialog.dismiss();

			FeatureActivity.this.shareURL = call.getFeature().getShareURL();

			TextView tvTitle = (TextView)FeatureActivity.this.findViewById(R.id.featureTitle);
			tvTitle.setText(call.getFeature().getTitle());
			
			LinearLayout parent = (LinearLayout)findViewById(R.id.content_container);
			
			List<ImageToDownload> imagesToDownload = new ArrayList<FeatureActivity.ImageToDownload>();

			LayoutInflater layoutInflater = getLayoutInflater();
			
			for (Item item: call.getItems()) {
				LinearLayout child = (LinearLayout) layoutInflater.inflate(R.layout.item_on_feature,null);
						
				for (ItemField itemField: item.getFields()) {
					
					if (itemField.isHasValue()) {
					
						LinearLayout fieldView;
												
						if (itemField.isTypePhone()) {
							fieldView = (LinearLayout) layoutInflater.inflate(R.layout.item_field_phone,null);
							TextView tv3 = (TextView)fieldView.findViewById(R.id.value);
							tv3.setText(itemField.getValueText());
							tv3.setTag(itemField.getValueText());
							telephone = itemField.getValueText();
						} else if (itemField.isTypeEmail()) {
							fieldView = (LinearLayout) layoutInflater.inflate(R.layout.item_field_email,null);
							TextView tv3 = (TextView)fieldView.findViewById(R.id.value);
							tv3.setText(itemField.getValueText());
							tv3.setTag(itemField.getValueText());
							email = itemField.getValueText();
							tv3.setPaintFlags(tv3.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);							
						} else if (itemField.isTypeHTML()) {
							fieldView = (LinearLayout) layoutInflater.inflate(R.layout.item_field_html,null);
							WebView tv3 = (WebView)fieldView.findViewById(R.id.value);
							tv3.loadData(itemField.getValueHTML(), "text/html", "utf-8");							
						} else {
							fieldView = (LinearLayout) layoutInflater.inflate(R.layout.item_field,null);
							TextView tv3 = (TextView)fieldView.findViewById(R.id.value);
							tv3.setText(itemField.getValueText());
						}						

						TextView tv2 = (TextView)fieldView.findViewById(R.id.title);
						tv2.setText(itemField.getTitle());			
						
						child.addView(fieldView);
					}
					
				}
				
				parent.addView(child);
				
			}			
			
			OurApplication appState = ((OurApplication)FeatureActivity.this.getApplication());
			appState.setFeatureContent(call.getFeature(), call.getContent());
			
			for (Content content : call.getContent()) {
				View child;
				
				if (content.isHasPicture()) {
					child = layoutInflater.inflate(R.layout.feature_content_with_picture_row,null);
					
					ImageView imageView = (ImageView)child.findViewById(R.id.picture);
					
					imagesToDownload.add(new ImageToDownload(imageView, content.getPictureThumbURL()));
					
					imageView.setTag(content.getId());

					imageView.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							FeatureActivity.this.onClickFeatureContentPicture(v);
						}
					});
					
				} else {
					child = layoutInflater.inflate(R.layout.feature_content_row,null);
				}
				
				TextView tv = (TextView)child.findViewById(R.id.body);
				tv.setText(content.getBody());

				TextView tvdetails = (TextView)child.findViewById(R.id.details);
				tvdetails.setText("Posted by " + content.getDisplayName());
				
				parent.addView(child);
				
			}

			hasCheckinQuestions = call.hasCheckinQuestions();
			
			downloadImagesTask = new DownloadImagesTask(imagesToDownload);
			downloadImagesTask.execute(true);

		}

	}

	private class ImageToDownload {
		protected ImageView imageView;
		protected String imageURL;
		protected boolean placed;
		protected Drawable drawable;
		public ImageToDownload(ImageView imageView, String imageURL) {
			super();
			this.imageView = imageView;
			this.imageURL = imageURL;
			placed = false;
		}
		public String getImageURL() {
			return imageURL;
		}
		public void setDrawable(Drawable drawable) {
			this.drawable = drawable;
		}
		public void processIfPossible() {
			if (drawable != null && !placed) {
				imageView.setImageDrawable(drawable);
				placed = true;
				// we can nullify all pointers and let any memory GC happen asap!
				imageView = null;
				drawable = null;
			}
		}
		
	}
	
	private class DownloadImagesTask extends AsyncTask<Boolean, Void, Boolean> {

		List<ImageToDownload> imagesToDownload;
		

		public DownloadImagesTask(List<ImageToDownload> imagesToDownload) {
			super();
			this.imagesToDownload = imagesToDownload;
		}

		protected Boolean doInBackground(Boolean... dummy) {
			for (ImageToDownload imageToDownload : imagesToDownload) {
				if (!isCancelled()) { 
					try {
						Log.d("DOWNLOAD",imageToDownload.getImageURL());
						URL url = new URL(imageToDownload.getImageURL());
						InputStream is = (InputStream)  url.getContent();
						Drawable d = Drawable.createFromStream(is, "src");
						imageToDownload.setDrawable(d);
					} catch (Exception e) {
						if (e.getMessage() != null) Log.d("ERRORDOWNLOAD",e.getMessage());
						return null;
					}
				}
			}
			return null;
		}

		protected void onProgressUpdate(Void... values) {
			placeLoadedImagesOnActivity();
		}

		protected void onPostExecute(Boolean result) {
			placeLoadedImagesOnActivity();
		}

		protected void placeLoadedImagesOnActivity() {
			for (ImageToDownload imageToDownload : imagesToDownload) {
				imageToDownload.processIfPossible();
			}
		}
		
	}

	@Override
	public void finish() {
		if (downloadImagesTask != null) downloadImagesTask.cancel(true);
		super.finish();
	}	
	
	
	
}


