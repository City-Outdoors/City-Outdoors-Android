package uk.co.jarofgreen.cityoutdoors.API;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestion;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionContent;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionFreeText;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionHigherOrLower;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionMultipleChoice;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionPossibleAnswer;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import uk.co.jarofgreen.cityoutdoors.Model.ItemField;
import android.content.Context;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
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
public class FeatureCall extends BaseCall {

	public FeatureCall(Context context, OurApplication ourApplication) {
		super(context, ourApplication);
	}
	
	public FeatureCall(InformationNeededFromContext informationNeededFromContext) {
		super(informationNeededFromContext);
	}
	
	Content lastContent = null; 
	List<Content> content;
	
	Item lastItem = null;
	List<Item> items;
	
	FeatureCheckinQuestion lastCheckinQuestion = null;
	FeatureCheckinQuestionPossibleAnswer lastPossibleAnswer = null;	
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
	
    public void execute(Integer featureID) {
    	feature = new Feature();
		content = new ArrayList<Content>();
		items = new ArrayList<Item>();
		checkinQuestions = new ArrayList<FeatureCheckinQuestion>();
		
        RootElement root = new RootElement("data");
        Element featureNode = root.getChild("feature");
        
        featureNode.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				feature.setId(attributes.getValue("id"));
				feature.setShareURL(attributes.getValue("shareURL"));
				feature.setTitle(attributes.getValue("title"));
			}
        });
        
        // content
        
        Element content = featureNode.getChild("contents").getChild("content");
        content.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastContent = new Content();
				lastContent.setId(attributes.getValue("id"));
				lastContent.setHasPicture(attributes.getValue("hasPicture"));
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
				lastItem.setId(attributes.getValue("id"));
				lastItem.setCollectionId(attributes.getValue("collectionID"));
				lastItem.setDeleted(attributes.getValue("deleted"));
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
				lastItemField.setId(attributes.getValue("id"));
				lastItemField.setTitle(attributes.getValue("title"));
				lastItemField.setHasValue(attributes.getValue("hasValue"));
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
				String type = attributes.getValue("type");
				if (type != null) {
					if (type.toUpperCase().compareTo("FREETEXT") == 0) {
						lastCheckinQuestion = new FeatureCheckinQuestionFreeText();
					} else if (type.toUpperCase().compareTo("CONTENT") == 0) {
						lastCheckinQuestion = new FeatureCheckinQuestionContent();
					} else if (type.toUpperCase().compareTo("MULTIPLECHOICE") == 0) {
						lastCheckinQuestion = new FeatureCheckinQuestionMultipleChoice();
					} else if (type.toUpperCase().compareTo("HIGHERORLOWER") == 0) {
						lastCheckinQuestion = new FeatureCheckinQuestionHigherOrLower();
					} else {
						// it's a question type we don't know how to handle. Just ignore.
						lastCheckinQuestion = null;
						Log.d("FEATURECHECKINQUESTION","Got a type we don't recognise: "+type);
						return;
					}
				} else {
					// The API has not told us the type. Just ignore.
					lastCheckinQuestion = null;
					Log.d("FEATURECHECKINQUESTION","API did not tell us a type!");
					return;
				}
				lastCheckinQuestion.setActive(attributes.getValue("active"));
				lastCheckinQuestion.setDeleted(attributes.getValue("deleted"));
				lastCheckinQuestion.setId(attributes.getValue("id"));
				lastCheckinQuestion.setHasAnswered(attributes.getValue("hasAnswered"));
				lastCheckinQuestion.setQuestion(attributes.getValue("question"));
			}
        });
        question.setEndElementListener(new EndElementListener() {
			public void end() {
				if (lastCheckinQuestion != null) addCheckinQuestion(lastCheckinQuestion);
			}
		});       
        

        Element possibleAnswers = question.getChild("possibleAnswers");
        Element possibleAnswer = possibleAnswers.getChild("possibleAnswer");
        possibleAnswer.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				lastPossibleAnswer = new FeatureCheckinQuestionPossibleAnswer(Integer.parseInt(attributes.getValue("id")));
			}
        });
        possibleAnswer.setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				lastPossibleAnswer.setAnswer(body);
			}
         });     
        possibleAnswer.setEndElementListener(new EndElementListener() {
			public void end() {
				if (lastCheckinQuestion != null) {
					FeatureCheckinQuestionMultipleChoice fcqmc = (FeatureCheckinQuestionMultipleChoice) lastCheckinQuestion;
					fcqmc.addPossibleAnswer(lastPossibleAnswer);
				}
			}
        });
        
        Element explanation = question.getChild("explanation");
        Element explanationValueHTML = explanation.getChild("valueHTML");
        explanationValueHTML.setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				if (lastCheckinQuestion != null) lastCheckinQuestion.setExplanationHTML(body);
			}
         });               
        
        setUpCall("/api/v1/feature.php?showLinks=0&showDeleted=1&fieldInContentArea=mobileapp&id="+Integer.toString(featureID));
        makeCall(root);
        
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
    
    public boolean hasActiveCheckinQuestions() {
    	for (FeatureCheckinQuestion featureCheckinQuestion : checkinQuestions) {
    		if (featureCheckinQuestion.isActive() && !featureCheckinQuestion.isDeleted()) return true;
    	}
    	return false;
    }

	public Feature getFeature() {
		return feature;
	}
    
}

