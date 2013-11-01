package uk.co.jarofgreen.cityoutdoors.API;



import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureFavourite;
import android.content.Context;
import android.sax.RootElement;
import android.util.Log;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureFavouriteCall extends BaseCall {

	public FeatureFavouriteCall(Context context, OurApplication ourApplication) {
		super(context, ourApplication);
	}

	public FeatureFavouriteCall(InformationNeededFromContext informationNeededFromContext) {
		super(informationNeededFromContext);
	}

    public boolean execute(FeatureFavourite featureFavourite) {

    	setUpCall("/api/v1/newFeatureFavourite.php?showLinks=0&");

    	if (!isUserTokenAttached) {
    		return false;
    	}

    	addDataToCall("featureID", featureFavourite.getFeatureID());
    	addDataToCall("favouriteAt", featureFavourite.getFavouriteAt());

    	Log.d("FAVOURITE","Sending "+featureFavourite.toString());

    	RootElement root = new RootElement("data");
    	// TODO should check response
    	makeCall(root);
    	


    	informationNeededFromContext.getStorage().featureFavouriteSentToServer(featureFavourite);

    	return true;
    }
}
