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
public abstract class FeatureCheckinQuestion {

	protected int id;

	protected boolean hasAnswered = false;

	protected String question;
	
	public FeatureCheckinQuestion(int id) {
		super();
		this.id = id;
	}
	
	public int getId() {
		return id;
	}


	public String getQuestion() {
		return question;
	}


	public void setQuestion(String question) {
		this.question = question;
	}


	public boolean isHasAnswered() {
		return hasAnswered;
	}


	public void setHasAnswered(boolean hasAnswered) {
		this.hasAnswered = hasAnswered;
	}
	
	public abstract boolean canAnswerMultipleTimes();


	
}
