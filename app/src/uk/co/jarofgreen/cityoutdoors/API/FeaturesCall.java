package uk.co.jarofgreen.cityoutdoors.API;




import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;


import android.content.Context;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;


/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeaturesCall extends BaseCall {

	public FeaturesCall(Context context) {
		super(context);
	}

	Feature lastFeature;
	
    public void execute() {
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
        
        setUpCall("/api/v1/features.php?showLinks=0&");
        makeCall(root);
        
    }
    
}
