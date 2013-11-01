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

	public IndexCall(InformationNeededFromContext informationNeededFromContext) {
		super(informationNeededFromContext);
	}

	Float startingBoundsMinLat;
	Float startingBoundsMaxLat;
	Float startingBoundsMinLng;
	Float startingBoundsMaxLng;
	Integer uploadsMaxSize;
	
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

        Element uploads = root.getChild("uploads");
        uploads.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				if (attributes.getValue("maxSize") != null) {
					uploadsMaxSize = Integer.parseInt(attributes.getValue("maxSize"));
					Log.d("MAXUPLOADSSIZE","uploadsMaxSize="+Integer.toString(uploadsMaxSize));
				}
			}
        });
        
        
        setUpCall("/api/v1/index.php?showLinks=0&");
        makeCall(root);

        SharedPreferences.Editor editor = informationNeededFromContext.getSettings().edit();
        if (startingBoundsMaxLat != null && startingBoundsMinLat != null && startingBoundsMaxLng != null && startingBoundsMinLng != null) {
        	editor.putFloat("startingBoundsMaxLat", startingBoundsMaxLat);
        	editor.putFloat("startingBoundsMinLat", startingBoundsMinLat);
        	editor.putFloat("startingBoundsMaxLng", startingBoundsMaxLng);
        	editor.putFloat("startingBoundsMinLng", startingBoundsMinLng);
        }
        if (uploadsMaxSize != null) {
        	editor.putInt("uploadsMaxSize", uploadsMaxSize);
        }
        editor.commit();
        
    }
	
}
