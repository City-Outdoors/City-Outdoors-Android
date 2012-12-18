package uk.co.jarofgreen.cityoutdoors.Model;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureFavourite {

	private int featureID;
	private int favouriteAt;
	
	
	
	
	public FeatureFavourite(int featureID, int favouriteAt) {
		super();
		this.featureID = featureID;
		this.favouriteAt = favouriteAt;
	}

	public int getFeatureID() {
		return featureID;
	}
	
	public void setFeatureID(int featureID) {
		this.featureID = featureID;
	}
	
	public int getFavouriteAt() {
		return favouriteAt;
	}
	
	public void setFavouriteAt(int favouriteAt) {
		this.favouriteAt = favouriteAt;
	}
}
