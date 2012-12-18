package uk.co.jarofgreen.cityoutdoors.API;

import java.net.URL;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.R;

import android.app.IntentService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.sax.Element;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Log;
import android.util.Xml;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class IndexCall {

	float startingBoundsMinLat;
	float startingBoundsMaxLat;
	float startingBoundsMinLng;
	float startingBoundsMaxLng;
	
    public void execute(Context context) {
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

        try {
            Xml.parse(new URL(context.getString(R.string.server_url) + "/api/v1/index.php?showLinks=0&").openConnection().getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("startingBoundsMaxLat", startingBoundsMaxLat);
        editor.putFloat("startingBoundsMinLat", startingBoundsMinLat);
        editor.putFloat("startingBoundsMaxLng", startingBoundsMaxLng);
        editor.putFloat("startingBoundsMinLng", startingBoundsMinLng);
        editor.commit();
    }
	
}
