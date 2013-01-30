package uk.co.jarofgreen.cityoutdoors.Service;

import java.util.List;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.API.FeatureFavouriteCall;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckin;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureFavourite;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class SendFeatureFavouriteService extends IntentService {


	public SendFeatureFavouriteService() {
		super("SendFeatureFavouriteService");
	}
	  
	protected void onHandleIntent(Intent intent) {	
		
		// only if user is logged in
		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
		int userID = settings.getInt("userID", -1);
		if (userID < 1) return;
		
		// ok lets go
		Log.d("FAVOURITE","Starting Sending All Unsent");
		Storage s = new Storage(this);
		
		FeatureFavouriteCall call = new FeatureFavouriteCall(this);
		
		try {
			for (FeatureFavourite featureFavourite : s.getFeatureFavouritesToSendToServer()) {
				call.execute(featureFavourite);
			}
		} catch (Exception e) {
			Log.d("APIERROR",e.toString());
			// assume it's a net connection error. ignore. Not the best to do.
		}		
	}	
	
}
