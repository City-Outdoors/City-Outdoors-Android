package uk.co.jarofgreen.cityoutdoors.API;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Model.UploadFeatureReport;


import android.content.Context;
import android.sax.Element;
import android.sax.RootElement;
import android.sax.StartElementListener;

public class SubmitFeatureReportCall extends BaseSubmitContentOrReportCall {

	public SubmitFeatureReportCall(Context context, OurApplication ourApplication) {
		super(context, ourApplication);
	}

	public SubmitFeatureReportCall(InformationNeededFromContext informationNeededFromContext) {
		super(informationNeededFromContext);
	}

	String resultSuccess;

	protected String email;
	
	public void setUpCall(UploadFeatureReport uploadReport) {
		this.uploadData = uploadReport;
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

		if (uploadData.hasFeatureID()) {
			addDataToCall("featureID",uploadData.getFeatureID());
		} else if (uploadData.hasLatLng()) {
			addDataToCall("lat", uploadData.getLat());
			addDataToCall("lng", uploadData.getLng());
		}

		addDataToCall("comment", uploadData.getComment());
		addDataToCall("name", uploadData.getName());
		addDataToCall("email", ((UploadFeatureReport)uploadData).getEmail());

		if (uploadData.hasPhoto()) {
			addFileToCall("photo", uploadData.getPhotoFileNameForUpload());
		}
		
		makeCall(root);

	}
	
	public boolean getWasResultASuccess() {
		return resultSuccess != null && resultSuccess.compareTo("yes") == 0;
	}
	 
}
