package uk.co.jarofgreen.cityoutdoors.API;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestion;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionFreeText;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionContent;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionMultipleChoice;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionHigherOrLower;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionPossibleAnswer;
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
public class FeatureCheckinQuestionsCall extends BaseCall  {


	
	public FeatureCheckinQuestionsCall(Context context) {
		super(context);
	}

	FeatureCheckinQuestion lastCheckinQuestion = null;
	FeatureCheckinQuestionPossibleAnswer lastPossibleAnswer = null;
	
	List<FeatureCheckinQuestion> checkinQuestions;
	protected void addCheckinQuestion(FeatureCheckinQuestion fq) {
		checkinQuestions.add(fq);
	}
	
    public void execute(Integer featureID) {
		checkinQuestions = new ArrayList<FeatureCheckinQuestion>();
		
        RootElement root = new RootElement("data");
        Element feature = root.getChild("feature");
        
        Element question = feature.getChild("checkinQuestions").getChild("checkinQuestion");
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
						return;
					}
				} else {
					// The API has not told us the type. Just ignore.
					lastCheckinQuestion = null;
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
				lastPossibleAnswer = new FeatureCheckinQuestionPossibleAnswer();
				lastPossibleAnswer.setId(attributes.getValue("id"));
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
        
        setUpCall("/api/v1/featureCheckinQuestions.php?showLinks=0&id="+Integer.toString(featureID));
        makeCall(root);        
    }
    
    public List<FeatureCheckinQuestion> getCheckinQuestions() { 
    	return checkinQuestions; 
    }
    
    public boolean hasCheckinQuestions() {    	
    	return (checkinQuestions.size() > 0);
    }	
	
}
