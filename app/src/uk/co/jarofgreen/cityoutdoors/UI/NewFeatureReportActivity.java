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
import uk.co.jarofgreen.cityoutdoors.Model.UploadFeatureReport;
import uk.co.jarofgreen.cityoutdoors.Service.SendFeatureContentOrReportService;
import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.R;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class NewFeatureReportActivity extends BaseNewFeatureContentOrReportActivity {
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_feature_report);  
        TitleBar.populate(this);
        
        uploadData = new UploadFeatureReport(this);
        
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int fid = extras.getInt("featureID",-1);
			if (fid != -1) { 
				uploadData.setFeatureID(fid); 
			}
			float lat = (float)extras.getFloat("lat",0);
			float lng = (float)extras.getFloat("lng",0);
			if (lat != 0.0 || lng != 0.0) {
				uploadData.setLatLng(lat,lng);
			}
		}   

        if (isUserLoggedIn()) {
        	View tandc = findViewById(R.id.agree_T_and_C_wrapper);
        	tandc.setVisibility(View.GONE);
        }
        
        promptForPosition();
        
        SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
        
        TextView tv = (TextView)findViewById(R.id.name);
        tv.setText(settings.getString("newFeatureReportName", ""));
        
        tv = (TextView)findViewById(R.id.email);
        tv.setText(settings.getString("newFeatureReportEmail", ""));
        
        
        
    }
    
    
    public void onClickSend(View v) {
    	if (!isUserLoggedIn()) {
    		CheckBox tandc = (CheckBox)findViewById(R.id.agree_T_and_C);
    		if (!(tandc.isChecked())) {
    			Toast.makeText(this, getString(R.string.new_feature_report_must_agree_T_and_C), Toast.LENGTH_LONG).show();
    			return;
    		}
    	}
    	
    	if (!uploadData.hasPosition()) {
    		Toast.makeText(this, getString(R.string.new_feature_report_still_getting_position), Toast.LENGTH_LONG).show();
			return;
		}
    	
    	TextView tv = (TextView)findViewById(R.id.comment);
    	uploadData.setComment(tv.getText().toString());

    	SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
    	
    	tv = (TextView)findViewById(R.id.name);
    	String name = tv.getText().toString();
    	uploadData.setName(name);
    	editor.putString("newFeatureReportName", name);
    	
    	tv = (TextView)findViewById(R.id.email);
    	String email = tv.getText().toString();
    	((UploadFeatureReport)uploadData).setEmail(email);
		editor.putString("newFeatureReportEmail", email);
		
		editor.commit();


		((OurApplication)getApplication()).addToUploadQue(uploadData);
		
    	startService(new Intent(this, SendFeatureContentOrReportService.class));
    	
    	Toast.makeText(this, getString(R.string.new_feature_report_starting_to_send), Toast.LENGTH_LONG).show();
    	
    	finish();
    }
	
	
	
}
