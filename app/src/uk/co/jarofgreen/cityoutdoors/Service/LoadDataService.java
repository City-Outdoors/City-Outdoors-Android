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
	
		try {
		
			IndexCall ic = new IndexCall(this);
			ic.execute();
	
			FeaturesCall fc = new FeaturesCall(this);
			fc.execute();
	
			CollectionsCall cc = new CollectionsCall(this);
			cc.execute();
	
			Storage s = new Storage(getApplicationContext());
			CollectionCall collectionCall = new CollectionCall(this);
			for(Collection collection: s.getCollections()) {
				collectionCall.execute(collection.getId());
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
