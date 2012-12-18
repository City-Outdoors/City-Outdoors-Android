package uk.co.jarofgreen.cityoutdoors;

import java.util.HashMap;
import java.util.List;

import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;

import android.app.Application;
import android.util.Log;
/**
 * 
 * @author James Baster <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 *  */
public class OurApplication extends Application {

	protected HashMap<Integer, List<Content>> featureContent;
	

	public void setFeatureContent(Feature feature, List<Content> data ) {
		//Log.d("OURAPPLIACTION","SET "+Integer.toString(feature.getId()));
		if (featureContent == null) {
			featureContent = new HashMap<Integer, List<Content>>();
			//Log.d("OURAPPLIACTION","CREATING DATA");
		}
		featureContent.put(Integer.valueOf(feature.getId()), data);
	}
	

	public List<Content> getFeatureContent(Integer featureID) {
		//Log.d("OURAPPLIACTION","GET "+Integer.toString(featureID));
		if (featureContent == null) {
			//Log.d("OURAPPLIACTION","GET NO DATA???!?!?!?!!?");
			return null;				
		}
		if (featureContent.containsKey(featureID)) {
			return (List<Content>)featureContent.get(Integer.valueOf(featureID));
		}
		return null;
	}


	@Override
	public void onLowMemory() {
		super.onLowMemory();
		// TODO something
	}
	
	
	
}
