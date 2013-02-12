package uk.co.jarofgreen.cityoutdoors.UI;

import com.google.android.gms.common.GooglePlayServicesUtil;

import android.os.Bundle;
import android.widget.TextView;
import uk.co.jarofgreen.cityoutdoors.R;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class AboutLegalActivity extends BaseActivity {
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.about_legal);
	        TitleBar.populate(this);
	        TextView tv =(TextView)findViewById(R.id.google_play);
	        tv.setText(GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this));
	 }
	 
}
