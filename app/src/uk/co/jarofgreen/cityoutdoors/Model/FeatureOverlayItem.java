package uk.co.jarofgreen.cityoutdoors.Model;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


public class FeatureOverlayItem extends OverlayItem {
	
	public FeatureOverlayItem(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
	}

	public FeatureOverlayItem(GeoPoint arg0, String arg1, String arg2, int heresATreeFeatureID) {
		super(arg0, arg1, arg2);
		this.heresATreeFeatureID = heresATreeFeatureID;
	}
	
	
	protected int heresATreeFeatureID;

	public int getHeresATreeFeatureId() {
		return heresATreeFeatureID;
	}
	
	
}
