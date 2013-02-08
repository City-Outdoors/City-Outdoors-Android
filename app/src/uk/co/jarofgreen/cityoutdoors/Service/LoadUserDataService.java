package uk.co.jarofgreen.cityoutdoors.Service;


import uk.co.jarofgreen.cityoutdoors.API.CheckCurrentUserCall;
import uk.co.jarofgreen.cityoutdoors.API.FeaturesCall;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
/**
 * This reloads only the data about the current user from the server and saves it.
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class LoadUserDataService  extends IntentService {

	public LoadUserDataService() {
		super("LoadUserDataService");
	}

	protected void onHandleIntent(Intent intent) {	

		long now = java.lang.System.currentTimeMillis();
		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
	
		try {
	
			// get latest data for answeredAllQuestions variable 
			FeaturesCall fc = new FeaturesCall(this);
			fc.execute();
	
			// get latest score
			CheckCurrentUserCall cu = new CheckCurrentUserCall(this);
			cu.execute();
		
		} catch (Exception e) {
			Log.d("APIERROR",e.toString());
			
			// assume it's a net connection error. ignore. Not the best to do.
			
		}
	}
	
	
}
