package uk.co.jarofgreen.cityoutdoors.API;


import android.content.Context;
import android.sax.Element;
import android.sax.EndTextElementListener;
import android.sax.RootElement;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class SubmitFeatureCheckinQuestionAnswerCall extends BaseCall {
	
	public SubmitFeatureCheckinQuestionAnswerCall(Context context) {
		super(context);
	}

	public void setResult(String r) {
		this.result = r;
	}
	
	String result;
	
    public boolean execute(Integer featureCheckinQuestionID, String answer) {
		
        RootElement root = new RootElement("data");
        Element result = root.getChild("result");
        
        result.setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				setResult(body);
			}
		}); 
        
        setUpCall("/api/v1/submitFeatureCheckinQuestionAnswer.php?showLinks=0&id="+Integer.toString(featureCheckinQuestionID));
        if (!isUserTokenAttached) {
        	return false;
        }
        
        addDataToCall("answer", answer);
        makeCall(root);
        
        return true;
        
    }

	public boolean getResult() {
		return result.compareTo("OK") == 0;
	}
    
    
}
