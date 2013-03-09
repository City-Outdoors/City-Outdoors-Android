package uk.co.jarofgreen.cityoutdoors.API;


import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
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
public class CollectionCall extends BaseCall {

	public CollectionCall(Context context) {
		super(context);
	}

	Item lastItem;
	Storage storage;
	int currentCollectionID;
	
    public void execute(int collectionID) {
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
				lastItem.setId(attributes.getValue("id"));
				lastItem.setSlug(attributes.getValue("slug"));	
				lastItem.setCollectionId(currentCollectionID);
				lastItem.setDeleted(attributes.getValue("deleted"));
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
				lastItem.setFeatureId(attributes.getValue("id"));
			}
        });
        
        Element title = item.getChild("title"); 
        title.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				lastItem.setTitle(body);
			}
		});       
        
        setUpCall("/api/v1/collection.php?showLinks=0&showDeleted=1&id=" + collectionID);
        makeCall(root);
        
    }
}
