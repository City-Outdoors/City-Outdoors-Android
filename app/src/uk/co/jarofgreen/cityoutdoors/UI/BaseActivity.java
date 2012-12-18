package uk.co.jarofgreen.cityoutdoors.UI;

import uk.co.jarofgreen.cityoutdoors.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class BaseActivity extends Activity {
	
	protected boolean isUserLoggedIn() {
        SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
        int userID = settings.getInt("userID", -1);
        if (userID > 0) {
        	return true;  
        } else {
        	return false;
        }

	}
	
	public void onClickViewTAndC(View v) {
		startActivity(new Intent(this, TermsAndConditionsActivity.class));
	}	
	
}
