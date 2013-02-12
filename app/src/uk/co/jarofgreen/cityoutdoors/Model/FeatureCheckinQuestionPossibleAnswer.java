package uk.co.jarofgreen.cityoutdoors.Model;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureCheckinQuestionPossibleAnswer {

	protected int id;
	protected String answer;
	
	public FeatureCheckinQuestionPossibleAnswer(int id) {
		super();
		this.id = id;
	}
	
	public FeatureCheckinQuestionPossibleAnswer() {
		super();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setId(String id) {
		if (id != null) this.id = Integer.parseInt(id);
	}		

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}
