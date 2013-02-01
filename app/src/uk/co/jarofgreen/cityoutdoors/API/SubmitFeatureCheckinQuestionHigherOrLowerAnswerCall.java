package uk.co.jarofgreen.cityoutdoors.API;


import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;

import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionHigherOrLower;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class SubmitFeatureCheckinQuestionHigherOrLowerAnswerCall extends BaseCall {

	public SubmitFeatureCheckinQuestionHigherOrLowerAnswerCall(Context context) {
		super(context);
	}

	protected Integer resultSuccessCode;
	protected Integer trueAnswerCode = 0;
	protected String explanationValueHTML;
	

	public boolean execute(FeatureCheckinQuestionHigherOrLower featureCheckinQuestion, String answer) {

		RootElement root = new RootElement("data");

		Element result = root.getChild("result");
		result.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				resultSuccessCode = Integer.parseInt(attributes.getValue("success"));
				if (attributes.getValue("trueAnswer") != null) trueAnswerCode = Integer.parseInt(attributes.getValue("trueAnswer"));
			}
		});

		Element explanationValueHTMLElement = result.getChild("explanation").getChild("valueHTML");
		explanationValueHTMLElement.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				setExplanationValueHTML(body);
			}
		}); 		
		
		setUpCall("/api/v1/submitFeatureCheckinQuestionHigherOrLowerAnswer.php?showLinks=0&id="+Integer.toString(featureCheckinQuestion.getId()));
		if (!isUserTokenAttached) {
			return false;
		}
		
		addDataToCall("answer", answer);
		makeCall(root);
		
		featureCheckinQuestion.setExplanationHTML(explanationValueHTML);
		return true;

	}

	public boolean getResult() {
		return (resultSuccessCode != null) && (resultSuccessCode == 1);
	}

	public Integer getTrueAnswerCode() {
		return trueAnswerCode;
	}


	protected void setExplanationValueHTML(String explanationValueHTML) {
		this.explanationValueHTML = explanationValueHTML;
	}

}
