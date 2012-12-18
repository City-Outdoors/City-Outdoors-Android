package uk.co.jarofgreen.cityoutdoors.API;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.Attributes;

import android.app.IntentService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.sax.Element;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.Xml;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import uk.co.jarofgreen.cityoutdoors.R;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class LogInCall {

	HttpClient httpclient;
	HttpPost httppost;
	List<NameValuePair> nameValuePairs;
	HttpResponse response;
	HttpEntity entity;
	InputStream  is;
	
	
	Integer userID = null;
	String token = null;
	String name = null;
	String email = null;
	
    public boolean execute(Context context, String email, String password) {
    	this.email = email;
        RootElement root = new RootElement("data");
        Element user = root.getChild("user");
        user.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				userID = Integer.parseInt(attributes.getValue("id"));
				token = attributes.getValue("token");
				name = attributes.getValue("name");
				Log.d("LOGINCALL","userID="+Integer.toString(userID));
			}
        });

        httpclient = new DefaultHttpClient();                      
        httppost = new HttpPost(context.getString(R.string.server_url) + "/api/v1/login.php?showLinks=0&");
        nameValuePairs = new ArrayList<NameValuePair>(2);        	
        nameValuePairs.add(new BasicNameValuePair("email",email));
        nameValuePairs.add(new BasicNameValuePair("password",password));
        
        try {
        	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);
            entity = response.getEntity();
            is = entity.getContent(); 
        	
        	
            Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        if (userID != null) {
	        return true;
        } else {
        	return false;
        }
    }
    
    public boolean saveResults(Context context) {
        if (userID != null) {
	        SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(context);
	        SharedPreferences.Editor editor = settings.edit();
	        editor.putInt("userID", userID);
	        editor.putString("userToken", token);
	        editor.putString("userDisplayName", name);
	        editor.putString("userEmail", email);
	        editor.putString("newFeatureReportName", name);
	        editor.putString("newFeatureReportEmail", email);	        
	        editor.commit();
	        return true;
        } else {
        	return false;
        }    	
    }
}
