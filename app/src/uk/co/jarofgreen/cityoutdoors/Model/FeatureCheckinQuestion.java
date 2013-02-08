package uk.co.jarofgreen.cityoutdoors.Model;


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
	protected String explanationHTML;
	private boolean deleted = false;
	private boolean active = true;
	
	public FeatureCheckinQuestion(int id) {
		super();
		this.id = id;
	}
	
	public FeatureCheckinQuestion() {
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
	public void setHasAnswered(String hasAnswered) {
		if (hasAnswered != null) {
			this.hasAnswered = (hasAnswered.compareTo("1") == 0 || hasAnswered.compareTo("yes") == 0);
		}
	}
	
	public abstract boolean canAnswerMultipleTimes();

	public String getExplanationHTML() {
		return explanationHTML;
	}

	public void setExplanationHTML(String explanationHTML) {
		this.explanationHTML = explanationHTML;
	}
	
	public boolean hasExplanationHTML() {
		return (this.explanationHTML != null) && (this.explanationHTML.length() > 0);
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setDeleted(String deleted) {
		if (deleted != null) {
			this.deleted = (deleted.compareTo("1") == 0 || deleted.compareTo("yes") == 0);
		}
	}	

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setActive(String active) {
		if (active != null) {
			this.active = (active.compareTo("1") == 0 || active.compareTo("yes") == 0);
		}
	}	
}
