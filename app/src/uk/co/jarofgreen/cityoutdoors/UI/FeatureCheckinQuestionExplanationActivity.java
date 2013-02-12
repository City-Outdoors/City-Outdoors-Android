package uk.co.jarofgreen.cityoutdoors.UI;

import uk.co.jarofgreen.cityoutdoors.R;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureCheckinQuestionExplanationActivity extends BaseActivity {


	 WebView webview;
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.feature_checkin_question_explanation);  
	        TitleBar.populate(this);
	        

			Bundle extras = getIntent().getExtras();
			if (extras == null) {
				finish();
			}
	        
	        webview = (WebView)findViewById(R.id.webview);
	        webview.loadData(extras.getString("html"), "text/html", "utf-8");
	 }
	
}
