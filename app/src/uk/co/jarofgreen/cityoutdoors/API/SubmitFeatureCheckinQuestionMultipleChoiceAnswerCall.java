package uk.co.jarofgreen.cityoutdoors.API;


import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionMultipleChoice;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionPossibleAnswer;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class SubmitFeatureCheckinQuestionMultipleChoiceAnswerCall extends BaseCall {

	public SubmitFeatureCheckinQuestionMultipleChoiceAnswerCall(Context context) {
		super(context);
	}

	protected Integer resultSuccessCode;
	protected String explanationValueHTML;

	public boolean execute(FeatureCheckinQuestionMultipleChoice featureCheckinQuestion, FeatureCheckinQuestionPossibleAnswer answer) {

		RootElement root = new RootElement("data");

		Element result = root.getChild("result");
		result.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				resultSuccessCode = Integer.parseInt(attributes.getValue("success"));
			}
		});

		Element explanationValueHTMLElement = result.getChild("explanation").getChild("valueHTML");
		explanationValueHTMLElement.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				setExplanationValueHTML(body);
			}
		}); 
		
		
		setUpCall("/api/v1/submitFeatureCheckinQuestionMultipleChoiceAnswer.php?showLinks=0&id="+Integer.toString(featureCheckinQuestion.getId()));
		if (!isUserTokenAttached) {
			return false;
		}

		addDataToCall("answerID", answer.getId());
		makeCall(root);

		featureCheckinQuestion.setExplanationHTML(explanationValueHTML);
		
		return true;

	}

	public boolean getResult() {
		return (resultSuccessCode != null) && (resultSuccessCode == 1);
	}

	protected void setExplanationValueHTML(String explanationValueHTML) {
		this.explanationValueHTML = explanationValueHTML;
	}


}
