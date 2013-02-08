package uk.co.jarofgreen.cityoutdoors.API;

import org.apache.http.client.HttpClient;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.R;

public abstract class BaseCall {
	
	Context context;
	
	public BaseCall(Context context) {
		super();
		this.context = context;
	}

	protected String errorMessage;
	protected String errorCode;
	
	protected HttpClient httpclient;
	protected HttpPost httppost;
	protected List<NameValuePair> nameValuePairs;
	protected HttpResponse response;
	protected HttpEntity entity;
	protected InputStream  is;
	protected boolean isUserTokenAttached = false;
	
	protected void setUpCall(String url) {
		httpclient = new DefaultHttpClient();                      
		httppost = new HttpPost(context.getString(R.string.server_url) + url);
		nameValuePairs = new ArrayList<NameValuePair>(4);        	

		SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(context);
		int userID = settings.getInt("userID", -1);
		if (userID > 0) {
			nameValuePairs.add(new BasicNameValuePair("userID",Integer.toString(userID)));  
			nameValuePairs.add(new BasicNameValuePair("userToken",settings.getString("userToken","")));
			isUserTokenAttached = true;
		}		
	}
	
	protected void addDataToCall(String key, String value) {
		nameValuePairs.add(new BasicNameValuePair(key,value));
	}

	protected void addDataToCall(String key, Integer value) {
		nameValuePairs.add(new BasicNameValuePair(key,Integer.toString(value)));
	}
	
	
	protected void makeCall(RootElement root) {
		Element errorTag = root.getChild("error");
		errorTag.setStartElementListener(new StartElementListener(){
				public void start(Attributes attributes) {
					errorCode = attributes.getValue("code");
				}
	        });
		errorTag.setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				errorMessage = body;
			}
		}); 
		
		try {
        	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      	  	response = httpclient.execute(httppost);
            entity = response.getEntity();
            is = entity.getContent();   	  	
      	  	
            Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}

	public boolean hasError() {
		return (errorMessage != null) || (errorCode != null) ;
	}
	
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}
	
	
	
}
