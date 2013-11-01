package uk.co.jarofgreen.cityoutdoors.UI;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Service.LoadDataIfStaleService;
import uk.co.jarofgreen.cityoutdoors.Service.LoadDataService;
import uk.co.jarofgreen.cityoutdoors.Service.SendFeatureFavouriteService;
import uk.co.jarofgreen.cityoutdoors.R;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class MainActivity extends BaseActivity  {
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        View v;
        if (isUserLoggedIn()) {
        	v = findViewById(R.id.login);
        } else {
        	v = findViewById(R.id.logout);
        }
        v.setVisibility(View.GONE);

        startService(new Intent(this, LoadDataIfStaleService.class));
        startService(new Intent(this, SendFeatureFavouriteService.class));
        
    }
    

    public void onClickBrowseMap(View v) {
    	Intent i = new Intent(this, BrowseMapActivity.class);
    	startActivity(i);
    }
    
    public void onClickLogIn(View v) {
    	Intent i = new Intent(this, LogInOrSignUpActivity.class);
    	startActivity(i);
    }    
    
    public void onClickCollections(View v) {
    	Intent i = new Intent(this, CollectionsActivity.class);
    	startActivity(i);
    }    
    
    public void onClickNewFeatureContent(View v) {
    	Intent i = new Intent(this, NewFeatureContentActivity.class);
    	startActivity(i);
    }   

    
    public void onClickNewFeatureReport(View v) {
    	Intent i = new Intent(this, NewFeatureReportActivity.class);
    	startActivity(i);
    }   

    
    public void onClickFavourites(View v) {
    	Intent i = new Intent(this, FavouritesActivity.class);
    	startActivity(i);
    }   
    
    public void onClickWhatsOn(View v) {
    	Intent i = new Intent(this, WhatsOnActivity.class);
    	startActivity(i);  	
    }
      

    public void onClickWildlife(View v) {
    	Intent i = new Intent(this, WildlifeActivity.class);
    	startActivity(i);  	
    }
    
    public void onClickLogOut(View v) {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle(getString(R.string.main_screen_log_out_confirm_title));
    	alert.setMessage(getString(R.string.main_screen_log_out_confirm_message));

    	alert.setPositiveButton(getString(R.string.main_screen_log_out_confirm_ok_button), new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    			SharedPreferences.Editor editor = settings.edit();
    			editor.remove("userID");
    			editor.remove("userToken");
    			editor.commit();

    			Storage s = ((OurApplication)getApplication()).getStorage();
    			s.deleteUserData();

    			View vb;
    			vb = findViewById(R.id.logout);
    			vb.setVisibility(View.GONE);    	
    			vb = findViewById(R.id.login);
    			vb.setVisibility(View.VISIBLE);
    		}
    	});

    	alert.setNegativeButton(getString(R.string.main_screen_log_out_confirm_cancel_button), new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    		}
    	});

    	alert.show();
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
    	if (isUserLoggedIn()) {
    		menu.findItem(R.id.login).setVisible(false);
    		menu.findItem(R.id.logout).setVisible(true);
    	} else {
    		menu.findItem(R.id.login).setVisible(true);
    		menu.findItem(R.id.logout).setVisible(false);
    	}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.about:
    		startActivity(new Intent(this, AboutActivity.class));
    		return true;
    	case R.id.map:
    		onClickBrowseMap(null);
    		return true;
    	case R.id.collections:
    		onClickCollections(null);
    		return true;
    	case R.id.new_feature_content:
    		onClickNewFeatureContent(null);
    		return true;
    	case R.id.new_feature_report:
    		onClickNewFeatureReport(null);
    		return true;
    	case R.id.login:
    		onClickLogIn(null);
    		return true;
    	case R.id.favourites:
    		onClickFavourites(null);
    		return true;                       
    	case R.id.logout:
    		onClickLogOut(null);
    		return true;
    	case R.id.wildlife:
    		onClickWildlife(null);
    		return true; 
    	case R.id.whatson:
    		onClickWhatsOn(null);
    		return true; 
    	case R.id.update_now:
    		Intent i1 = new Intent(this, LoadDataService.class);
    		startService(i1);
    		Intent i2 = new Intent(this, SendFeatureFavouriteService.class);
    		startService(i2);
    		Toast.makeText(this, getString(R.string.main_screen_update_now), Toast.LENGTH_SHORT).show();
    		return true;
    	case R.id.prefences:
    		startActivity(new Intent(this, PreferencesActivity.class));   		
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
	
  
}
