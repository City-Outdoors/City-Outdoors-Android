package uk.co.jarofgreen.cityoutdoors.UI;

import uk.co.jarofgreen.cityoutdoors.R;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class TermsAndConditionsActivity extends BaseActivity {

	 WebView webview;
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.terms_and_conditions);  
	        
	        
	        webview = (WebView)findViewById(R.id.webview);
	        webview.loadUrl(getString(R.string.server_url)+"/api/v1/termsAndConditions.php");   
	 }
	 
}


