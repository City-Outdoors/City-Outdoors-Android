package uk.co.jarofgreen.cityoutdoors.UI;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import uk.co.jarofgreen.cityoutdoors.R;
import uk.co.jarofgreen.cityoutdoors.Service.SendFeatureContentService;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class NewFeatureContentActivity extends BaseNewFeatureContentOrReportActivity {
	

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_feature_content);  
        TitleBar.populate(this);
        
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureID = extras.getInt("featureID",-1);
			Log.d("FEATURE from INTENT",Integer.toString(featureID));
			lat = (float)extras.getFloat("lat",0);
			lng = (float)extras.getFloat("lng",0);
		}   

        if (isUserLoggedIn()) {
        	View tandc = findViewById(R.id.anonymous_user_wrapper);
        	tandc.setVisibility(View.GONE);
        } else {
            SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
            TextView tv = (TextView)findViewById(R.id.name);
            tv.setText(settings.getString("newFeatureContentName", ""));
        }
        
        promptForPosition();
        
    }
    
    
    public void onClickSend(View v) {
    	if (!isUserLoggedIn()) {
    		CheckBox tandc = (CheckBox)findViewById(R.id.agree_T_and_C);
    		if (!(tandc.isChecked())) {
    			Toast.makeText(this, "You must agree to the terms and conditions!", Toast.LENGTH_LONG).show();
    			return;
    		}
    	}
    	
    	if (!hasPosition()) {
    		Toast.makeText(this, "We are still trying to get your current position; please wait a second and try again.", Toast.LENGTH_LONG).show();
			return;
		}
    	
    	Intent i = new Intent(this,SendFeatureContentService.class);
    	i.putExtra("featureID", featureID);
    	i.putExtra("lat", lat);
    	i.putExtra("lng", lng);
    	
    	TextView tv = (TextView)findViewById(R.id.comment);
    	i.putExtra("comment", tv.getText().toString());


		if (!isUserLoggedIn()) {
			SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = settings.edit();
			tv = (TextView)findViewById(R.id.name);
			String name = tv.getText().toString();
			i.putExtra("name", name);
			editor.putString("newFeatureContentName", name);
			editor.commit();
		}

    	if (hasPhoto) {
    		i.putExtra("photoFileName", photoFileName);
    	}
    	
    	startService(i);
    	finish();
    }

    
}
