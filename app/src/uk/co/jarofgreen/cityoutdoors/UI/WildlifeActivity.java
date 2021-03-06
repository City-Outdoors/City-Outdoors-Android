package uk.co.jarofgreen.cityoutdoors.UI;

import uk.co.jarofgreen.cityoutdoors.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.webkit.WebView;
import android.widget.TextView;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class WildlifeActivity extends BaseMonthlyActivity {


	 
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.wildlife);  
	        TitleBar.populate(this);
	        
	        titleTextEdit = (TextView)findViewById(R.id.title);
	        webview = (WebView)findViewById(R.id.webview);

	        loadData();

	        webview.setOnClickListener(this); 
	        webview.setOnTouchListener(gestureListener);	        
	        
	 }
	 
	 protected void loadData() {
		 	super.loadData();
		   webview.loadUrl(getString(R.string.server_url)+"/api/v1/wildlife.php?month="+month);
	 }	 

	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.wildlife_menu, menu);
	        return true;
	    }
	    
}


