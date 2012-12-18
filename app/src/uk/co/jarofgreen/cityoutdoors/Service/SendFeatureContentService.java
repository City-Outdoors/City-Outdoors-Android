package uk.co.jarofgreen.cityoutdoors.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import uk.co.jarofgreen.cityoutdoors.UI.FeatureActivity;
import uk.co.jarofgreen.cityoutdoors.R;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class SendFeatureContentService extends IntentService {

	public SendFeatureContentService() {
		super("SendFeatureContentService");
	}
	  
	protected void onHandleIntent(Intent intent) {	
		  Bundle b = intent.getExtras();
          int featureID = b.getInt("featureID",-1);
          float lat = b.getFloat("lat",(float)0.0);
          float lng = b.getFloat("lng",(float)0.0);
          String comment = b.getString("comment");
          String name = b.getString("name");
          String photoFileName = b.getString("photoFileName");
          
          
          // ---------- Start Ongoing Notification
          int icon = R.drawable.notification;
          long when = System.currentTimeMillis();
          Notification notification = new Notification(icon, "Sending Your Comment", when);
          
          Intent notificationIntent = new Intent(this, FeatureActivity.class);
          // without this set action line, when you try to make more than one PendingIntent with only extra data different, the original is overwritten!
          // we use requestID as the action, so user only sees one notification per request no matter haw many replies there are.
          // http://stackoverflow.com/questions/3140072/android-keeps-caching-my-intents-extras-how-to-declare-a-pending-intent-that-ke
          notificationIntent.setAction("featureID".concat(Integer.toString(featureID)));
          notificationIntent.putExtra("featureID", featureID);
          PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
          
          notification.setLatestEventInfo(getApplicationContext(), "Sending Your Comment", "Sending Your Comment", contentIntent);
          notification.flags = Notification.DEFAULT_SOUND;
          notification.flags |= Notification.DEFAULT_VIBRATE;
          notification.flags |= Notification.DEFAULT_LIGHTS;
          notification.flags |= Notification.FLAG_ONGOING_EVENT;
          
          NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
          mNotificationManager.notify(featureID, notification);
          
                 
          //------------ Send
          sendFeatureContent(featureID, lat, lng, comment, name, photoFileName);
          
          // ---------- End Ongoing Notification
          mNotificationManager.cancel(featureID);
		
	}
	
	private boolean sendFeatureContent(int featureID, float lat, float lng, String comment, String name, String photoFileName) {
		HttpClient httpclient;
		HttpPost httppost;
		List<NameValuePair> nameValuePairs;
		HttpResponse response;
		HttpEntity entity;
		InputStream  is;
		
		httpclient = new DefaultHttpClient();                      
        httppost = new HttpPost(getString(R.string.server_url)+"/api/v1/newFeatureContent.php");
        MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
        try {
	        if (featureID > 0) {
	        	multipartEntity.addPart("featureID", new StringBody(Integer.toString(featureID)));
	        } else if (lat != 0 && lng != 0) {
	        	// technically this could cause problems for others as 0,0 is a valid position but it von't for Edinburgh so left for new.
	        	multipartEntity.addPart("lat", new StringBody(Float.toString(lat)));
	        	multipartEntity.addPart("lng", new StringBody(Float.toString(lng)));
	        }
	        multipartEntity.addPart("comment", new StringBody(comment));
	        multipartEntity.addPart("name", new StringBody(comment));
	  	        
	        if (photoFileName != null) {
	        	multipartEntity.addPart("photo", new FileBody(new File(photoFileName)));
	        }
	        
	        SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
	        int userID = settings.getInt("userID", -1);
	        if (userID > 0) {
	        	multipartEntity.addPart("userID", new StringBody(Integer.toString(settings.getInt("userID", -1))));
	        	multipartEntity.addPart("userToken", new StringBody(settings.getString("userToken","")));
	        }

        } catch (Exception e) {
        	if (e.getMessage() != null) Log.d("SENDFEATURECONTENT",e.getMessage());
        	return false;
        }
        
		int attempt = 0;
        while (true) { 
        	 if (attempt < 20) attempt += 1;
        	 Log.d("SENDFEATURECONTENT","Trying to send feature content ...");
        	 try {
        		 httppost.setEntity(multipartEntity);
        		 response = httpclient.execute(httppost);
        		 entity = response.getEntity();
        		 is = entity.getContent(); 
        		 // TODO check response was ok
        		 return true;
        	 } catch (Exception e) {
        		 if (e.getMessage() != null) Log.d("SENDFEATURECONTENT",e.getMessage());
                 try {
                         Thread.sleep(Math.min(60000,(long)Math.pow(2,attempt)*1000));
                 } catch (InterruptedException ie) {}
        	 }     
        }
	        
	}
	
}
