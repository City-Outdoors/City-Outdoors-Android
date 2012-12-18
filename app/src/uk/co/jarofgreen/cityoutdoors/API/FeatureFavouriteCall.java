package uk.co.jarofgreen.cityoutdoors.API;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckin;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureFavourite;
import uk.co.jarofgreen.cityoutdoors.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureFavouriteCall {

	HttpClient httpclient;
	HttpPost httppost;
	List<NameValuePair> nameValuePairs;
	HttpResponse response;
	Storage storage;

    public boolean execute(Context context, FeatureFavourite featureFavourite) {

          httpclient = new DefaultHttpClient();                      
          httppost = new HttpPost(context.getString(R.string.server_url) + "/api/v1/newFeatureFavourite.php?showLinks=0&");
          nameValuePairs = new ArrayList<NameValuePair>(4);        	
    	  nameValuePairs.add(new BasicNameValuePair("featureID",Integer.toString(featureFavourite.getFeatureID())));  
    	  nameValuePairs.add(new BasicNameValuePair("favouriteAt",Integer.toString(featureFavourite.getFavouriteAt()))); 

          SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(context);
          int userID = settings.getInt("userID", -1);
          if (userID > 0) {
        	  nameValuePairs.add(new BasicNameValuePair("userID",Integer.toString(userID)));  
        	  nameValuePairs.add(new BasicNameValuePair("userToken",settings.getString("userToken","")));
          } else {
        	  return false;
          }
          
          Log.d("FAVOURITE","Sending "+featureFavourite.toString());
          
          try {
        	  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        	  response = httpclient.execute(httppost);
          } catch (Exception e) {
        	  throw new RuntimeException(e);
          }

          storage = new Storage(context);
          storage.featureFavouriteSentToServer(featureFavourite);
          
          return true;
    }
}
