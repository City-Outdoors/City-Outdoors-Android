package uk.co.jarofgreen.cityoutdoors.UI;

import com.google.android.gms.common.GooglePlayServicesUtil;

import android.os.Bundle;
import android.widget.TextView;
import uk.co.jarofgreen.cityoutdoors.R;

public class AboutLegalActivity extends BaseActivity {
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.about_legal);
	        
	        TextView tv =(TextView)findViewById(R.id.google_play);
	        tv.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this));
	 }
	 
}
