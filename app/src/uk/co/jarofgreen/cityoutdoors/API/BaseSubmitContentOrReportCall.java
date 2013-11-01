package uk.co.jarofgreen.cityoutdoors.API;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Model.BaseUploadContentOrReport;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.Log;

public abstract class BaseSubmitContentOrReportCall extends BaseCall {

	public BaseSubmitContentOrReportCall(InformationNeededFromContext informationNeededFromContext) {
		super(informationNeededFromContext);
	}


	public BaseSubmitContentOrReportCall(Context context, OurApplication ourApplication) {
		super(context, ourApplication);
	}


	protected BaseUploadContentOrReport uploadData;
	
		
}



