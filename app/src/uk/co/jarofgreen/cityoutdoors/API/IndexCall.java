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
				startingBoundsMaxLat = Float.parseFloat(attributes.getValue("maxLat"));
				startingBoundsMinLat = Float.parseFloat(attributes.getValue("minLat"));
				startingBoundsMaxLng = Float.parseFloat(attributes.getValue("maxLng"));
				startingBoundsMinLng = Float.parseFloat(attributes.getValue("minLng"));
				Log.d("INDEXCALL","startingBoundsMaxLat="+Float.toString(startingBoundsMaxLat));
				Log.d("INDEXCALL","startingBoundsMinLat="+Float.toString(startingBoundsMinLat));
				Log.d("INDEXCALL","startingBoundsMaxLng="+Float.toString(startingBoundsMaxLng));
				Log.d("INDEXCALL","startingBoundsMinLng="+Float.toString(startingBoundsMinLng));
			}
        });
        
        setUpCall("/api/v1/index.php?showLinks=0&");
        makeCall(root);

        SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        if (startingBoundsMaxLat != null) editor.putFloat("startingBoundsMaxLat", startingBoundsMaxLat);
        if (startingBoundsMinLat != null) editor.putFloat("startingBoundsMinLat", startingBoundsMinLat);
        if (startingBoundsMaxLng != null)  editor.putFloat("startingBoundsMaxLng", startingBoundsMaxLng);
        if (startingBoundsMinLng != null) editor.putFloat("startingBoundsMinLng", startingBoundsMinLng);
        editor.commit();
    }
	
}
