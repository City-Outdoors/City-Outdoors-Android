package uk.co.jarofgreen.cityoutdoors.UI;

import java.util.ArrayList;
import java.util.HashMap;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Collection;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureOverlayItem;
import uk.co.jarofgreen.cityoutdoors.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class BrowseMapOverlay extends ItemizedOverlay {

	protected BrowseMapActivity browseMapActivity;
	
	HashMap<Integer, Drawable> icons;
		
	public void setBrowseMapActivity(BrowseMapActivity browseMapActivity) {
		this.browseMapActivity = browseMapActivity;

		// from http://stackoverflow.com/questions/7505574/android-overlayitem-setmarker-change-the-marker-for-one-item

		icons = new HashMap<Integer, Drawable>();
		Storage s = new Storage(browseMapActivity);
		for (Collection collection : s.getCollections()) {
			Drawable icon = null;
			if (collection.getIconURL().contains("park")) {
				icon = browseMapActivity.getResources().getDrawable(R.drawable.marker_park);
			} else if (collection.getIconURL().contains("tree")) {
				icon = browseMapActivity.getResources().getDrawable(R.drawable.marker_tree);
			} else if (collection.getIconURL().contains("monument")) {
				icon = browseMapActivity.getResources().getDrawable(R.drawable.marker_monument);
			} else if (collection.getIconURL().contains("wc")) {
				icon = browseMapActivity.getResources().getDrawable(R.drawable.marker_wc);
			} else if (collection.getIconURL().contains("play")) {
				icon = browseMapActivity.getResources().getDrawable(R.drawable.marker_playground);												
			}
			if (icon != null) {
				icon.setBounds(0 - icon.getIntrinsicWidth() / 2, 0 - icon.getIntrinsicHeight(),icon.getIntrinsicWidth() / 2, 0);
				icons.put(collection.getId(), icon);
			}
			
		}
		
	}

	private ArrayList<FeatureOverlayItem> mOverlays = new ArrayList<FeatureOverlayItem>();
	
	public BrowseMapOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public void addFeature(Feature feature) {
		GeoPoint point = new GeoPoint(Math.round(feature.getLat()*1000000), Math.round(feature.getLng()*1000000));
		FeatureOverlayItem overlayitem = new FeatureOverlayItem(point, "", "", feature.getId());
		
		if (icons.containsKey(Integer.valueOf(feature.getCollectionID()))) {
			overlayitem.setMarker(icons.get(Integer.valueOf(feature.getCollectionID())));
		}
		
		mOverlays.add(overlayitem);
		populate();
	}

	@Override
	protected boolean onTap(int index) {
		int featureID = mOverlays.get(index).getHeresATreeFeatureId();
		Log.d("TAP",Integer.toString(featureID));
		browseMapActivity.startActivity(new Intent(browseMapActivity, FeatureActivity.class).putExtra("featureID", featureID)); 
		return super.onTap(index);
	}


	
	
}
