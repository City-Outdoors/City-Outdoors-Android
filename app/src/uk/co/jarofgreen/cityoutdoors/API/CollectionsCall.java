package uk.co.jarofgreen.cityoutdoors.API;

import java.net.URL;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Collection;
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
public class CollectionsCall {

	Collection lastCollection;
	Storage storage;
	
    public void execute(Context context) {
    	storage = new Storage(context);
        RootElement root = new RootElement("data");
        Element collections = root.getChild("collections");
        Element collection = collections.getChild("collection");
        final Storage storage = new Storage(context);
        
        collection.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastCollection = new Collection();
				lastCollection.setId(Integer.parseInt(attributes.getValue("id")));
				lastCollection.setSlug(attributes.getValue("slug"));				
			}
        });
        collection.setEndElementListener(new EndElementListener() {
			public void end() {
				storage.storeCollection(lastCollection);
			}
		});

        Element icon = collection.getChild("icon");
        icon.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {			
				lastCollection.setIconURL(attributes.getValue("url"));				
			}
        });
        
        Element thumbnail = collection.getChild("thumbnail");
        thumbnail.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {			
				lastCollection.setThumbnailURL(attributes.getValue("url"));				
			}
        });     
        
        Element description = collection.getChild("description");
        description.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				lastCollection.setDescription(body);
			}
		});     
        
        Element title = collection.getChild("title");
        title.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				lastCollection.setTitle(body);
			}
		});     
                          
        
        try {
            Xml.parse(new URL(context.getString(R.string.server_url) + "/api/v1/collections.php?showLinks=0&").openConnection().getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
}
