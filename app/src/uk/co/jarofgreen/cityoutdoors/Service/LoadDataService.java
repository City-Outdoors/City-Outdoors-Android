package uk.co.jarofgreen.cityoutdoors.Service;


import java.util.ArrayList;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.API.CollectionCall;
import uk.co.jarofgreen.cityoutdoors.API.CollectionsCall;
import uk.co.jarofgreen.cityoutdoors.API.FeaturesCall;
import uk.co.jarofgreen.cityoutdoors.API.IndexCall;
import uk.co.jarofgreen.cityoutdoors.Model.Collection;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class LoadDataService  extends IntentService {

	public LoadDataService() {
		super("LoadDataService");
	}

	protected void onHandleIntent(Intent intent) {	

		long now = java.lang.System.currentTimeMillis();

		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
		long last = settings.getLong("lastDataUpdate", 0);

		boolean updateNeeded = false;
		
		// we may choose to update because it has been a long time since we did.
		// this is just a catch; if last > now someone has been messing with system clock and we should definetly update.
		if (last == 0) {
			updateNeeded = true;
			Log.d("UPDATE","Yes, never updated before");			
		} else if (now > last) {
			// if last updated more than a hour ago
			Log.d("UPDATEDATENOW",Long.toString(now));
			Log.d("UPDATEDATELAST",Long.toString(last));
			Log.d("UPDATEDATEMINUS",Long.toString(now - last));
			ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			long interval;
			if (mWifi.isConnected()) {
				Log.d("UPDATE","WiFi Connected so 1 week");
				interval = 1000*60*60*24*7;
			} else {
				Log.d("UPDATE","No WiFi so 1 month");
				interval = 1000*60*60*24*7;
			}
			
			if ((now - last) > interval) {
				updateNeeded = true;
				Log.d("UPDATE","Yes, to old");
			}        	
		} else {
			updateNeeded = true;
			Log.d("UPDATE","Yes, clock has run backwards");
		}
		
		// we may choose to update because we are specifically told
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (extras.getBoolean("alwaysUpdate", false)) {
				updateNeeded = true;
				Log.d("UPDATE","Yes, told to");

			}
		}		

		if (!updateNeeded) {
			Log.d("UPDATE","Skipping");
			return;
		}
		
		try {
		
			IndexCall ic = new IndexCall();
			ic.execute(this);
	
			FeaturesCall fc = new FeaturesCall();
			fc.execute(this);
	
			CollectionsCall cc = new CollectionsCall();
			cc.execute(this);
	
			Storage s = new Storage(getApplicationContext());
			CollectionCall collectionCall = new CollectionCall();
			for(Collection collection: s.getCollections()) {
				collectionCall.execute(getApplicationContext(), collection.getId());
			}
	
			SharedPreferences.Editor editor = settings.edit();
			editor.putLong("lastDataUpdate", now);
			editor.commit();
		
		} catch (Exception e) {
			Log.d("APIERROR",e.toString());
			
			// assume it's a net connection error. ignore. Not the best to do.
			
		}
	}
	
	
}
