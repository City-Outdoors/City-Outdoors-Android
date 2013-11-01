package uk.co.jarofgreen.cityoutdoors.API;

import java.io.File;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.R;
import uk.co.jarofgreen.cityoutdoors.Storage;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class is used because we shouldn't really pass Contexts to other processes.
 * When we start API calls from other threads, we sometimes may start other processes.
 * In this class, we extract all the information we may need from the context for the API classes to do their work, then discard the context. 
 *
 */
public class InformationNeededFromContext {

	protected String serverURL;
	protected SharedPreferences settings;
	protected Storage storage;
	protected File cacheDir;

	
	public InformationNeededFromContext(Context context, OurApplication ourApplication) {
		serverURL = context.getString(R.string.server_url);
		settings=PreferenceManager.getDefaultSharedPreferences(context);
		storage = ourApplication.getStorage();
		cacheDir = context.getCacheDir();
	}


	public String getServerURL() {
		return serverURL;
	}


	public SharedPreferences getSettings() {
		return settings;
	}


	public Storage getStorage() {
		return storage;
	}


	public File getCacheDir() {
		return cacheDir;
	}
	
}

