package uk.co.jarofgreen.cityoutdoors.Model;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureCheckin {

	private int featureID;
	private int checkinAt;
	
	
	
	
	public FeatureCheckin(int featureID, int checkinAt) {
		super();
		this.featureID = featureID;
		this.checkinAt = checkinAt;
	}

	public int getFeatureID() {
		return featureID;
	}
	
	public void setFeatureID(int featureID) {
		this.featureID = featureID;
	}
	
	public int getCheckinAt() {
		return checkinAt;
	}
	
	public void setCheckinAt(int checkinAt) {
		this.checkinAt = checkinAt;
	}
	
	
	
}
