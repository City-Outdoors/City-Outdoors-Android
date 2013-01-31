package uk.co.jarofgreen.cityoutdoors.Model;

public class FeatureCheckinQuestionPossibleAnswer {

	protected int id;
	protected String answer;
	
	public FeatureCheckinQuestionPossibleAnswer(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}
