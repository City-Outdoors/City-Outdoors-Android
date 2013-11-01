package uk.co.jarofgreen.cityoutdoors.Model;

import android.content.Context;

public class UploadFeatureReport  extends BaseUploadContentOrReport {

	public UploadFeatureReport(Context context) {
		super(context);
	}

	protected String email;


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
