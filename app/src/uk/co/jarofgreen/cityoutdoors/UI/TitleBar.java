package uk.co.jarofgreen.cityoutdoors.UI;

import uk.co.jarofgreen.cityoutdoors.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class TitleBar {

	public static void populate(Activity activity ) {
		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(activity);
        int userID = settings.getInt("userID", -1);
        if (userID > 0) {
	        int userScore = settings.getInt("userScore", 0);
	        String userDisplayName = settings.getString("userDisplayName", "");
	        TextView tv = (TextView)activity.findViewById(R.id.user_details);
	        if (tv != null) {
		        if (userScore > 0) {
		        	tv.setText(userDisplayName+" ("+Integer.toString(userScore)+")");
		        } else {
		        	tv.setText(userDisplayName);
		        }
	        }
        }
	}
	
}


