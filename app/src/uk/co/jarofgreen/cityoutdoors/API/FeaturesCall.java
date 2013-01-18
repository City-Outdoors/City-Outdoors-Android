package uk.co.jarofgreen.cityoutdoors.API;


import java.net.URL;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;
import uk.co.jarofgreen.cityoutdoors.R;

import android.app.IntentService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.sax.Element;
import android.sax.EndElementListener;
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
public class FeaturesCall {

	Feature lastFeature;
	
    public void execute(Context context) {
        RootElement root = new RootElement("data");
        Element features = root.getChild("features");
        Element feature = features.getChild("feature");
        final Storage storage = new Storage(context);

        
        
        feature.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				int id = Integer.parseInt(attributes.getValue("id"));
				float lat = Float.parseFloat(attributes.getValue("lat"));
				float lng = Float.parseFloat(attributes.getValue("lng"));
				lastFeature = new Feature(id, lat, lng);
				lastFeature.setTitle(attributes.getValue("title"));
			}
        });
        feature.setEndElementListener(new EndElementListener() {
			public void end() {
				storage.storeFeature(lastFeature);
			}
		});        
        
        Element item = feature.getChild("items").getChild("item");
        item.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastFeature.setCollectionID((int)Integer.parseInt(attributes.getValue("collectionID")));
			}
        });

        try {
            Xml.parse(new URL(context.getString(R.string.server_url) + "/api/v1/features.php?showLinks=0&").openConnection().getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
}
