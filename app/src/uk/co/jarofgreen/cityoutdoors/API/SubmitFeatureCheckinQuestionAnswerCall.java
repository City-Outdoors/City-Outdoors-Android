package uk.co.jarofgreen.cityoutdoors.API;


import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckin;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestion;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureFavourite;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import uk.co.jarofgreen.cityoutdoors.Model.ItemField;
import uk.co.jarofgreen.cityoutdoors.R;

import android.app.IntentService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
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
public class SubmitFeatureCheckinQuestionAnswerCall {

	HttpClient httpclient;
	HttpPost httppost;
	List<NameValuePair> nameValuePairs;
	HttpResponse response;
	Storage storage;
	HttpEntity entity;
	InputStream  is;
	
	public SubmitFeatureCheckinQuestionAnswerCall() {
		super();
	}

	public void setResult(String r) {
		this.result = r;
	}
	
	String result;
	
    public boolean execute(Context context, Integer featureCheckinQuestionID, String answer) {
		
        RootElement root = new RootElement("data");
        Element result = root.getChild("result");
        
        result.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				setResult(body);
			}
		}); 
        
        httpclient = new DefaultHttpClient();                      
        httppost = new HttpPost(context.getString(R.string.server_url) + "/api/v1/submitFeatureCheckinQuestionAnswer.php?showLinks=0&id="+Integer.toString(featureCheckinQuestionID));
        nameValuePairs = new ArrayList<NameValuePair>(3);        	
        nameValuePairs.add(new BasicNameValuePair("answer",answer));
        
        SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(context);
        int userID = settings.getInt("userID", -1);
        if (userID > 0) {
      	  nameValuePairs.add(new BasicNameValuePair("userID",Integer.toString(userID)));  
      	  nameValuePairs.add(new BasicNameValuePair("userToken",settings.getString("userToken","")));
        } else {
        	return false;
        }
        
        try {
        	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      	  	response = httpclient.execute(httppost);
            entity = response.getEntity();
            is = entity.getContent();   	  	
      	  	
            Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return true;
        
    }

	public boolean getResult() {
		return result.compareTo("OK") == 0;
	}
    
    
}
