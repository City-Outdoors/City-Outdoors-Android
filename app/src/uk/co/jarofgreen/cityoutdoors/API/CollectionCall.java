package uk.co.jarofgreen.cityoutdoors.API;


import java.net.URL;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Collection;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import uk.co.jarofgreen.cityoutdoors.R;

import android.app.IntentService;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
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
public class CollectionCall {

	Item lastItem;
	Storage storage;
	int currentCollectionID;
	
    public void execute(Context context, int collectionID) {
    	this.currentCollectionID = collectionID;
    	storage = new Storage(context);
        RootElement root = new RootElement("data");
        Element collection = root.getChild("collection");
        Element items = collection.getChild("items");       
        Element item = items.getChild("item"); 
        final Storage storage = new Storage(context);
        
        item.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastItem = new Item();
				lastItem.setId(Integer.parseInt(attributes.getValue("id")));
				lastItem.setSlug(attributes.getValue("slug"));	
				lastItem.setCollectionId(currentCollectionID);
			}
        });
        item.setEndElementListener(new EndElementListener() {
			public void end() {
				storage.storeItem(lastItem);
			}
		});
        
        Element feature = item.getChild("feature"); 
        feature.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastItem.setFeatureId(Integer.parseInt(attributes.getValue("id")));
			}
        });
        
        Element title = item.getChild("title"); 
        title.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				lastItem.setTitle(body);
			}
		});       
        
        try {
            Xml.parse(new URL(context.getString(R.string.server_url) + "/api/v1/collection.php?showLinks=0&id=" + collectionID).openConnection().getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
}
