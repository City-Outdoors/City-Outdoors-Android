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
public class FeatureCheckinQuestionsCall {

	HttpClient httpclient;
	HttpPost httppost;
	List<NameValuePair> nameValuePairs;
	HttpResponse response;
	Storage storage;
	HttpEntity entity;
	InputStream  is;
	
	public FeatureCheckinQuestionsCall() {
		super();
	}

	
	FeatureCheckinQuestion lastCheckinQuestion = null;
	List<FeatureCheckinQuestion> checkinQuestions;
	protected void addCheckinQuestion(FeatureCheckinQuestion fq) {
		checkinQuestions.add(fq);
	}
	
    public void execute(Context context, Integer featureID) {
		checkinQuestions = new ArrayList<FeatureCheckinQuestion>();
		
        RootElement root = new RootElement("data");
        Element feature = root.getChild("feature");
        
        Element question = feature.getChild("checkinQuestions").getChild("checkinQuestion");
        question.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastCheckinQuestion = new FeatureCheckinQuestion(Integer.parseInt(attributes.getValue("id")));
				String ha = attributes.getValue("hasAnswered");
				if (ha != null && Integer.parseInt(ha) > 0) {
					lastCheckinQuestion.setHasAnswered(true);
				}
				lastCheckinQuestion.setQuestion(attributes.getValue("question"));
			}
        });
        question.setEndElementListener(new EndElementListener() {
			public void end() {
				addCheckinQuestion(lastCheckinQuestion);
			}
		});       
        
        httpclient = new DefaultHttpClient();                      
        httppost = new HttpPost(context.getString(R.string.server_url) + "/api/v1/featureCheckinQuestions.php?showLinks=0&id="+Integer.toString(featureID));
        nameValuePairs = new ArrayList<NameValuePair>(2);        	

        SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(context);
        int userID = settings.getInt("userID", -1);
        if (userID > 0) {
      	  nameValuePairs.add(new BasicNameValuePair("userID",Integer.toString(userID)));  
      	  nameValuePairs.add(new BasicNameValuePair("userToken",settings.getString("userToken","")));
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
        
    }
    
    public List<FeatureCheckinQuestion> getCheckinQuestions() { 
    	return checkinQuestions; 
    }
    
    public boolean hasCheckinQuestions() {    	
    	return (checkinQuestions.size() > 0);
    }	
	
}
