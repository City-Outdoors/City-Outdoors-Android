package uk.co.jarofgreen.cityoutdoors.Model;

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
