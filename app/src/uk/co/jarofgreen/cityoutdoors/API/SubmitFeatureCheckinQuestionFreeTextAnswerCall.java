package uk.co.jarofgreen.cityoutdoors.API;


import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import org.xml.sax.Attributes;

import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionFreeText;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class SubmitFeatureCheckinQuestionFreeTextAnswerCall extends BaseCall {

	public SubmitFeatureCheckinQuestionFreeTextAnswerCall(Context context) {
		super(context);
	}

	public void setResult(String r) {
		this.result = r;
	}

	protected String result;
	protected Integer resultSuccessCode;

	public boolean execute(FeatureCheckinQuestionFreeText featureCheckinQuestion, String answer) {

		RootElement root = new RootElement("data");

		Element result = root.getChild("result");
		result.setStartElementListener(new StartElementListener(){
			public void start(Attributes attributes) {
				resultSuccessCode = Integer.parseInt(attributes.getValue("success"));
			}
		});
		result.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				setResult(body);
			}
		}); 

		setUpCall("/api/v1/submitFeatureCheckinQuestionFreeTextAnswer.php?showLinks=0&id="+Integer.toString(featureCheckinQuestion.getId()));
		if (!isUserTokenAttached) {
			return false;
		}

		addDataToCall("answer", answer);
		makeCall(root);

		return true;

	}

	public boolean getResult() {
		return (resultSuccessCode == 1);
	}


}
