package uk.co.jarofgreen.cityoutdoors.Model;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureCheckinQuestionContent  extends FeatureCheckinQuestion {

	public FeatureCheckinQuestionContent(int id) {
		super(id);
	}
	
	public FeatureCheckinQuestionContent() {
		super();
	}	
	
	public boolean canAnswerMultipleTimes() { return true; }
	
}
