package uk.co.jarofgreen.cityoutdoors.API;

import org.xml.sax.Attributes;


import android.content.Context;
import android.sax.Element;
import android.sax.RootElement;
import android.sax.StartElementListener;

public class SubmitFeatureReportCall extends BaseSubmitContentOrReportCall {

	public SubmitFeatureReportCall(Context context) {
		super(context);
	}

	String resultSuccess;

	protected String email;
	
	public void setUpCall(int featureID, float lat, float lng, String comment, String name, String email, String photoFileName) {
		this.featureID = featureID;
		this.lat = lat;
		this.lng = lng;
		this.comment = comment;
		this.name = name;
		this.email = email;
		this.photoFileName = photoFileName;
			
		if (photoFileName != null) {
			try {
				photoDetails = getPhotoDetailsForSending(photoFileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void execute() {
		RootElement root = new RootElement("data");
		Element result = root.getChild("result");
		result.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				resultSuccess = attributes.getValue("success");
			}
		});

		setUpCall("/api/v1/newFeatureReport.php?showLinks=0&");

		if (featureID > 0) {
			addDataToCall("featureID",featureID);
		} else if (lat != 0 && lng != 0) {
			// technically this could cause problems for others as 0,0 is a valid position but it von't for Edinburgh so left for new.
			addDataToCall("lat", lat);
			addDataToCall("lng", lng);
		}

		addDataToCall("comment", comment);
		addDataToCall("name", name);
		addDataToCall("email", email);
		
		if (photoFileName != null) {
			addFileToCall("photo", photoDetails.fileName);
		}
		
		makeCall(root);

	}
	
	public boolean getWasResultASuccess() {
		return resultSuccess != null && resultSuccess.compareTo("yes") == 0;
	}
	 
}
