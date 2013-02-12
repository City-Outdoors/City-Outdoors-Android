package uk.co.jarofgreen.cityoutdoors.UI;




import uk.co.jarofgreen.cityoutdoors.Service.LoadUserDataService;
import uk.co.jarofgreen.cityoutdoors.Service.SendFeatureFavouriteService;
import uk.co.jarofgreen.cityoutdoors.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class LogInTwitterActivity extends BaseActivity {

	WebView webview;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_twitter);
        
        webview = (WebView)findViewById(R.id.webview);
        webview.loadUrl(getString(R.string.server_url)+"/android/loginTwitter.php");
        JavaScriptInterface ji = new JavaScriptInterface(this);
        // Old servers may have the old name, leave for backwards compatability
        webview.addJavascriptInterface(ji, "HeresATree");
        // But this is the new name.
        webview.addJavascriptInterface(ji, "CityOutdoors");
        webview.setWebViewClient(new WebViewClient());
        
        // Remove all cookies. This is so if the last Twitter user checked "remember me" it won't.
        // Because if you do, Twitter logs you straight in which is a security risk. 
        // Even if the last user remembered they checked "remember me" and tried to return to 
        //     the twitter site to logout, there is no way to do that.
        CookieSyncManager.createInstance(this); 
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSavePassword(false);

    }
    	
    public class JavaScriptInterface {
    	Context mContext;

    	JavaScriptInterface(Context c) {
    		mContext = c;
    	}

    	public void authDone(int userID, String token) {

    		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(mContext);
    		SharedPreferences.Editor editor = settings.edit();
    		editor.putInt("userID", userID);
    		editor.putString("userToken", token);
    		editor.commit();

    		// send data to and get data from server in background
        	LogInTwitterActivity.this.startService(new Intent(LogInTwitterActivity.this, SendFeatureFavouriteService.class));
        	LogInTwitterActivity.this.startService(new Intent(LogInTwitterActivity.this, LoadUserDataService.class));
    		// start main screen
    		Intent i = new Intent(LogInTwitterActivity.this, MainActivity.class);
    		LogInTwitterActivity.this.startActivity(i);
    		// kill login screen
    		LogInTwitterActivity.this.finish();
    	}
    }
	
}
