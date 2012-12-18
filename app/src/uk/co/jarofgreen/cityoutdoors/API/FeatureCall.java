package uk.co.jarofgreen.cityoutdoors.API;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestion;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import uk.co.jarofgreen.cityoutdoors.Model.ItemField;
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
public class FeatureCall {

	
	public FeatureCall() {
		super();
	}

	Content lastContent = null; 
	List<Content> content;
	
	Item lastItem = null;
	List<Item> items;
	
	FeatureCheckinQuestion lastCheckinQuestion = null;
	List<FeatureCheckinQuestion> checkinQuestions;
	
	ItemField lastItemField;
	
	Feature feature;
	
	protected void addContent(Content c) {
		content.add(c);
	}
	protected void addItem(Item i) {
		items.add(i);
	}
	protected void addCheckinQuestion(FeatureCheckinQuestion fq) {
		checkinQuestions.add(fq);
	}
	
    public void execute(Context context, Integer featureID) {
    	feature = new Feature();
		content = new ArrayList<Content>();
		items = new ArrayList<Item>();
		checkinQuestions = new ArrayList<FeatureCheckinQuestion>();
		
        RootElement root = new RootElement("data");
        Element featureNode = root.getChild("feature");
        
        featureNode.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				feature.setId(Integer.valueOf(attributes.getValue("id")));
				feature.setShareURL(attributes.getValue("shareURL"));
				feature.setTitle(attributes.getValue("title"));
			}
        });
        
        // content
        
        Element content = featureNode.getChild("contents").getChild("content");
        content.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastContent = new Content();
				lastContent.setId(Integer.parseInt(attributes.getValue("id")));
				if (attributes.getValue("hasPicture").compareTo("yes") == 0) {
					lastContent.setHasPicture(true);
				} else {
					lastContent.setHasPicture(false);
				}
			}
        });
        content.setEndElementListener(new EndElementListener() {
			public void end() {
				addContent(lastContent);
			}
		});

        Element body = content.getChild("body");
        body.setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				lastContent.setBody(body);
			}
         });     

        Element displayName = content.getChild("displayName");
        displayName.setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				lastContent.setDisplayName(body);
			}
         });     
        Element picture = content.getChild("picture");
        picture.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastContent.setPictureFullURL(attributes.getValue("fullURL"));
				lastContent.setPictureNormalURL(attributes.getValue("normalURL"));
				lastContent.setPictureThumbURL(attributes.getValue("thumbURL"));
			}
        });     

        // items
        Element item = featureNode.getChild("items").getChild("item");
        item.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastItem = new Item();
				lastItem.setId(Integer.parseInt(attributes.getValue("id")));
				lastItem.setCollectionId(Integer.parseInt(attributes.getValue("collectionID")));
			}
        });
        item.setEndElementListener(new EndElementListener() {
			public void end() {
				addItem(lastItem);
			}
		});
        
        Element field = item.getChild("fields").getChild("field");
        field.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastItemField = new ItemField();
				lastItemField.setId(Integer.parseInt(attributes.getValue("id")));
				lastItemField.setTitle(attributes.getValue("title"));
				lastItemField.setHasValue(attributes.getValue("hasValue").compareTo("yes") == 0);
				lastItemField.setType(attributes.getValue("type"));
			}
        });
        field.setEndElementListener(new EndElementListener() {
			public void end() {
				lastItem.addField(lastItemField);
			}
		});

        Element fieldValueText = field.getChild("valueText");
        fieldValueText.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				lastItemField.setValueText(body);
			}
		}); 
        

        Element fieldValueHTML = field.getChild("valueHTML");
        fieldValueHTML.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				lastItemField.setValueHTML(body);
			}
		});       
        Element question = featureNode.getChild("checkinQuestions").getChild("checkinQuestion");
        question.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastCheckinQuestion = new FeatureCheckinQuestion(Integer.parseInt(attributes.getValue("id")));
				lastCheckinQuestion.setQuestion(attributes.getValue("question"));
			}
        });
        question.setEndElementListener(new EndElementListener() {
			public void end() {
				addCheckinQuestion(lastCheckinQuestion);
			}
		});       
        
        
        try {
            Xml.parse(new URL(context.getString(R.string.server_url) + "/api/v1/feature.php?showLinks=0&fieldInContentArea=mobileapp&id="+Integer.toString(featureID)).openConnection().getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
    }
    
    public List<Item> getItems() { 
    	return items; 
    }
    
    public List<Content> getContent() { 
    	return content; 
    }

    public List<FeatureCheckinQuestion> getCheckinQuestions() { 
    	return checkinQuestions; 
    }
    
    public boolean hasCheckinQuestions() {    	
    	return (checkinQuestions.size() > 0);
    }
	public Feature getFeature() {
		return feature;
	}
    
}

