package uk.co.jarofgreen.cityoutdoors.API;

import org.apache.http.client.HttpClient;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.Xml;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.R;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public abstract class BaseCall {
	
	InformationNeededFromContext informationNeededFromContext;
	
	public BaseCall(InformationNeededFromContext informationNeededFromContext) {
		super();
		this.informationNeededFromContext = informationNeededFromContext;
	}

	public BaseCall(Context context, OurApplication ourApplication) {
		super();
		this.informationNeededFromContext = new InformationNeededFromContext(context, ourApplication);
	}
	
	protected String errorMessage;
	protected String errorCode;
	
	protected HttpClient httpclient;
	protected HttpPost httppost;
	protected MultipartEntity multipartEntity;
	protected HttpResponse response;
	protected HttpEntity entity;
	protected InputStream  is;
	protected boolean isUserTokenAttached = false;
	
	protected void setUpCall(String url) {
		httpclient = new DefaultHttpClient();                      
		httppost = new HttpPost(informationNeededFromContext.getServerURL() + url);
		multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  

		int userID = informationNeededFromContext.getSettings().getInt("userID", -1);
		if (userID > 0) {
			addDataToCall("userID", userID);
			addDataToCall("userToken", informationNeededFromContext.getSettings().getString("userToken",""));
			isUserTokenAttached = true;
		}		
	}
	
	protected void addDataToCall(String key, String value) {
		if (value != null) {
			try {
				multipartEntity.addPart(key, new StringBody(value));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	protected void addDataToCall(String key, Integer value) {
		if (value != null) {
			try {
				multipartEntity.addPart(key, new StringBody(Integer.toString(value)));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	protected void addDataToCall(String key, Float value) {
		if (value != null) {
			try {
				multipartEntity.addPart(key, new StringBody(Float.toString(value)));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void addFileToCall(String key, String fileName) {
		multipartEntity.addPart(key, new FileBody(new File(fileName)));
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
			httppost.setEntity(multipartEntity);
			response = httpclient.execute(httppost);
			entity = response.getEntity();
			is = entity.getContent();   

			Xml.parse(is, Xml.Encoding.UTF_8, root.getContentHandler());
		} catch (SocketException e) {
			errorCode = "A1";
			errorMessage = "Can not connect to internet";
		} catch (UnknownHostException e) {
			errorCode = "A2";
			errorMessage = "Can not connect to internet";			
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
