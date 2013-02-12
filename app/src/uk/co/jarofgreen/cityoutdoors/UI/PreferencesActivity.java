package uk.co.jarofgreen.cityoutdoors.UI;

import uk.co.jarofgreen.cityoutdoors.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class PreferencesActivity extends PreferenceActivity {

	 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences_activity);
    }	
	
	
}
