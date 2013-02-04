package uk.co.jarofgreen.cityoutdoors.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureCheckinQuestionMultipleChoice extends FeatureCheckinQuestion {

	

	protected List<FeatureCheckinQuestionPossibleAnswer> checkinQuestionsPossibleAnswers;
	
	public FeatureCheckinQuestionMultipleChoice(int id) {
		super(id);
		checkinQuestionsPossibleAnswers = new ArrayList<FeatureCheckinQuestionPossibleAnswer>();
	}
	
	public FeatureCheckinQuestionMultipleChoice() {
		super();
		checkinQuestionsPossibleAnswers = new ArrayList<FeatureCheckinQuestionPossibleAnswer>();
	}	

	public void addPossibleAnswer(FeatureCheckinQuestionPossibleAnswer a) {
		checkinQuestionsPossibleAnswers.add(a);
	}
	

	public List<FeatureCheckinQuestionPossibleAnswer> getCheckinQuestionsPossibleAnswers() {
		return checkinQuestionsPossibleAnswers;
	}
	
	public boolean canAnswerMultipleTimes() { return false; }

}
