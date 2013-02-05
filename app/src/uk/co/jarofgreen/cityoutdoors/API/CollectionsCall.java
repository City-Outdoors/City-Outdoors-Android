package uk.co.jarofgreen.cityoutdoors.API;


import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Collection;

import android.content.Context;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class CollectionsCall extends BaseCall {

	public CollectionsCall(Context context) {
		super(context);
	}

	Collection lastCollection;
	Storage storage;
	
    public void execute() {
    	storage = new Storage(context);
        RootElement root = new RootElement("data");
        Element collections = root.getChild("collections");
        Element collection = collections.getChild("collection");
        final Storage storage = new Storage(context);
        
        collection.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastCollection = new Collection();
				lastCollection.setId(attributes.getValue("id"));
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
        
        Element questionIcon = collection.getChild("questionIcon");
        questionIcon.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {			
				lastCollection.setQuestionIconURL(attributes.getValue("url"));				
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
                   
        setUpCall("/api/v1/collections.php?showLinks=0&");
        makeCall(root);
                
    }
    
}
