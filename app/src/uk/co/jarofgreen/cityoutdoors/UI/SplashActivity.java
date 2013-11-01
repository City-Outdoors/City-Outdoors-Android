package uk.co.jarofgreen.cityoutdoors.UI;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.R;
import uk.co.jarofgreen.cityoutdoors.Service.LoadDataIfStaleService;
import uk.co.jarofgreen.cityoutdoors.Service.LoadDataService;
import uk.co.jarofgreen.cityoutdoors.Service.SendFeatureFavouriteService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

public class SplashActivity extends BaseActivity {


	protected static final int DELAY_TILL_FIRST_CHECK = 2000;
	protected static final int DELAY_STILL_WORKING = 2000;

	protected TextView textView;

	Handler handler;
	final Runnable runnable = new Runnable() {
		public void run() {
			SplashActivity.this.nextScreenPlease(null);
		}
	};;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		textView = (TextView)findViewById(R.id.message);

		handler=new Handler();
		handler.postDelayed(runnable, SplashActivity.DELAY_TILL_FIRST_CHECK);

		startService(new Intent(this, LoadDataIfStaleService.class));
		startService(new Intent(this, SendFeatureFavouriteService.class));
	}

	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(runnable);
	}

	@Override
	protected void onResume() {
		super.onResume();
		handler.postDelayed(runnable, SplashActivity.DELAY_TILL_FIRST_CHECK);
	}

	public void nextScreenPlease(View v) {
		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
		long last = settings.getLong("lastDataUpdate", -1);
		if (last > 1000) {
			startActivity(new Intent(this, MainActivity.class));
			handler.removeCallbacks(runnable);
			finish();
		} else {		
			textView.setText(R.string.splash_still_loading);
			handler.removeCallbacks(runnable);
			handler.postDelayed(runnable, SplashActivity.DELAY_STILL_WORKING);			
			if (((OurApplication)getApplication()).getLoadDataSerivceState() == OurApplication.GET_DATA_SERVICE_STATE_NONE) {
				startService(new Intent(this, LoadDataService.class));
			}
		}
	}	
}
