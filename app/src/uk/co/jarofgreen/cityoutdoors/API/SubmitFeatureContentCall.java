package uk.co.jarofgreen.cityoutdoors.API;

import java.io.File;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Model.UploadFeatureContent;

import android.content.Context;
import android.sax.Element;
import android.sax.RootElement;
import android.sax.StartElementListener;

public class SubmitFeatureContentCall extends BaseSubmitContentOrReportCall {

	public SubmitFeatureContentCall(Context context, OurApplication ourApplication) {
		super(context, ourApplication);
	}

	public SubmitFeatureContentCall(InformationNeededFromContext informationNeededFromContext) {
		super(informationNeededFromContext);
	}

	String resultSuccess;

	
	public void setUpCall( UploadFeatureContent uploadContent) {
		this.uploadData = uploadContent;
	}
	
	public void execute() {
		RootElement root = new RootElement("data");
		Element result = root.getChild("result");
		result.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				resultSuccess = attributes.getValue("success");
			}
		});

		setUpCall("/api/v1/newFeatureContent.php?showLinks=0&");

		if (uploadData.hasFeatureID()) {
			addDataToCall("featureID",uploadData.getFeatureID());
		} else if (uploadData.hasLatLng()) {
			addDataToCall("lat", uploadData.getLat());
			addDataToCall("lng", uploadData.getLng());
		}

		addDataToCall("comment", uploadData.getComment());
		addDataToCall("name", uploadData.getName());

		if (uploadData.hasPhoto()) {
			addFileToCall("photo", uploadData.getPhotoFileNameForUpload());
		}

		makeCall(root);

	}

	
	public boolean getWasResultASuccess() {
		return resultSuccess != null && resultSuccess.compareTo("yes") == 0;
	}
}
