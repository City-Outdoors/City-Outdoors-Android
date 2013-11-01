package uk.co.jarofgreen.cityoutdoors.Service;



import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import uk.co.jarofgreen.cityoutdoors.API.SubmitFeatureContentCall;
import uk.co.jarofgreen.cityoutdoors.UI.FeatureActivity;
import uk.co.jarofgreen.cityoutdoors.OurApplication;
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
		int attempt = 0;
		SubmitFeatureContentCall call = new SubmitFeatureContentCall(this, (OurApplication)getApplication());
		call.setUpCall(featureID, lat, lng, comment, name, photoFileName);
		while (true) { 
			if (attempt < 20) attempt += 1;
			Log.d("SENDFEATURECONTENT","Trying to send feature content ...");
			try {
				call.execute();
				if (call.getWasResultASuccess()) {
					call.cleanUp(this);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage() != null) Log.d("SENDFEATURECONTENT",e.getMessage());
			}   
			try { Thread.sleep(Math.min(60000,(long)Math.pow(2,attempt)*1000)); } catch (InterruptedException ie) {}
		}
	}
	
}
