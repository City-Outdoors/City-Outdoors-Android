package uk.co.jarofgreen.cityoutdoors.UI;

import com.google.analytics.tracking.android.EasyTracker;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
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
