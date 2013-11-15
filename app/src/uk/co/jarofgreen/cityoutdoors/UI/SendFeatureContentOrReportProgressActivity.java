package uk.co.jarofgreen.cityoutdoors.UI;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.R;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class SendFeatureContentOrReportProgressActivity extends BaseActivity {

	protected static final int DELAY = 1000;
	TextView messageTV;
	OurApplication ourApp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_feature_content_or_report_progress);
		
		messageTV = (TextView)findViewById(R.id.message);
		ourApp = (OurApplication)getApplication(); 
		

		updateScreen();
		
		handler=new Handler();
		handler.postDelayed(runnable, SendFeatureContentOrReportProgressActivity.DELAY);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(runnable);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateScreen();
		handler.postDelayed(runnable, SendFeatureContentOrReportProgressActivity.DELAY);
	}

	protected void updateScreen() {
		if (ourApp.hasMoreToUpload()) {
			messageTV.setText(R.string.send_feature_content_or_report_progress_uploading);
		} else {
			messageTV.setText(R.string.send_feature_content_or_report_progress_all_uploaded);
		}
	}
	
	Handler handler;
	final Runnable runnable = new Runnable() {
		public void run() {
			updateScreen();
			handler.postDelayed(runnable, SendFeatureContentOrReportProgressActivity.DELAY);
		}
	};

}
