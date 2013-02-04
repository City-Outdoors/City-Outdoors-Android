package uk.co.jarofgreen.cityoutdoors.API;



import org.xml.sax.Attributes;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.sax.Element;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class IndexCall extends BaseCall {

	public IndexCall(Context context) {
		super(context);
	}

	Float startingBoundsMinLat;
	Float startingBoundsMaxLat;
	Float startingBoundsMinLng;
	Float startingBoundsMaxLng;
	
    public void execute() {
        RootElement root = new RootElement("data");
        Element startingBounds = root.getChild("startingBounds");
        startingBounds.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				if (attributes.getValue("maxLat") != null) {
					startingBoundsMaxLat = Float.parseFloat(attributes.getValue("maxLat"));
					Log.d("INDEXCALL","startingBoundsMaxLat="+Float.toString(startingBoundsMaxLat));
				}
				if (attributes.getValue("minLat") != null) {
					startingBoundsMinLat = Float.parseFloat(attributes.getValue("minLat"));
					Log.d("INDEXCALL","startingBoundsMinLat="+Float.toString(startingBoundsMinLat));
				}
				if (attributes.getValue("maxLng") != null) {
					startingBoundsMaxLng = Float.parseFloat(attributes.getValue("maxLng"));
					Log.d("INDEXCALL","startingBoundsMaxLng="+Float.toString(startingBoundsMaxLng));
				}
				if (attributes.getValue("minLng") != null) {
					startingBoundsMinLng = Float.parseFloat(attributes.getValue("minLng"));
					Log.d("INDEXCALL","startingBoundsMinLng="+Float.toString(startingBoundsMinLng));
				}
			}
        });
        
        setUpCall("/api/v1/index.php?showLinks=0&");
        makeCall(root);

        if (startingBoundsMaxLat != null && startingBoundsMinLat != null && startingBoundsMaxLng != null && startingBoundsMinLng != null) {
	        SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(context);
	        SharedPreferences.Editor editor = settings.edit();
        	editor.putFloat("startingBoundsMaxLat", startingBoundsMaxLat);
        	editor.putFloat("startingBoundsMinLat", startingBoundsMinLat);
        	editor.putFloat("startingBoundsMaxLng", startingBoundsMaxLng);
        	editor.putFloat("startingBoundsMinLng", startingBoundsMinLng);
        	editor.commit();
        }
    }
	
}
