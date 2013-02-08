package uk.co.jarofgreen.cityoutdoors.UI;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BaseListActivity extends ListActivity {


	private boolean analyticsEnabled = true;

	@Override
	public void onStart() {		
		super.onStart();
		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
		analyticsEnabled = (settings.getString("analyticsEnabled", "yes").compareTo("yes") == 0);
		if (analyticsEnabled) EasyTracker.getInstance().activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (analyticsEnabled) EasyTracker.getInstance().activityStop(this);
	}
	
}
