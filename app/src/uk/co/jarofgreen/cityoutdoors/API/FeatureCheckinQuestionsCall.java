package uk.co.jarofgreen.cityoutdoors.API;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestion;
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
public class FeatureCheckinQuestionsCall extends BaseCall  {


	
	public FeatureCheckinQuestionsCall(Context context) {
		super(context);
	}

	FeatureCheckinQuestion lastCheckinQuestion = null;
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
				lastCheckinQuestion = new FeatureCheckinQuestion(Integer.parseInt(attributes.getValue("id")));
				String ha = attributes.getValue("hasAnswered");
				if (ha != null && Integer.parseInt(ha) > 0) {
					lastCheckinQuestion.setHasAnswered(true);
				}
				lastCheckinQuestion.setQuestion(attributes.getValue("question"));
			}
        });
        question.setEndElementListener(new EndElementListener() {
			public void end() {
				addCheckinQuestion(lastCheckinQuestion);
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
