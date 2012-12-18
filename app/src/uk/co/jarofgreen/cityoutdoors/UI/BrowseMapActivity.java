package uk.co.jarofgreen.cityoutdoors.UI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.API.FeatureCall;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import uk.co.jarofgreen.cityoutdoors.Model.ItemField;
import uk.co.jarofgreen.cityoutdoors.Service.LoadDataService;
import uk.co.jarofgreen.cityoutdoors.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class BrowseMapActivity extends MapActivity {
	
	MapView mapView;
	MapController mapController;
	
	List<Overlay> mapOverlays;
	Drawable drawable;
	BrowseMapOverlay browseMapOverlay;
	MyLocationOverlay myLocationOverlay;

	MarkerTask markerTask;

	HashMap<Integer, Boolean> featureAdded = new HashMap<Integer, Boolean>();

	private Handler mHandler = new Handler(); 

	public static final Float STARTING_BOUNDS_MIN_LAT = (float) 55.878290;
	public static final Float STARTING_BOUNDS_MAX_LAT = (float) 55.993363;
	public static final Float STARTING_BOUNDS_MIN_LNG = (float) -3.314781;
	public static final Float STARTING_BOUNDS_MAX_LNG = (float) -3.044586;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_map);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
		mapController = mapView.getController();


        boolean zoomed = false;
		Bundle extras = getIntent().getExtras();
		
		if (extras != null) {
			Float lat = extras.getFloat("lat");
			Float lng = extras.getFloat("lng");
			Log.d("LOADMAP", Float.toString(lat));
			Log.d("LOADMAP", Float.toString(lng));
			if (lat != 0.0 && lng != 0.0) {
				mapController.setCenter(new GeoPoint(Math.round(lat*1000000), Math.round(lng*1000000)));
				mapController.setZoom(18);
				zoomed = true;
			}
		}
		
		if (!zoomed) toDefaultBounds();
        
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.marker_usercontent);
        browseMapOverlay = new BrowseMapOverlay(drawable);
        browseMapOverlay.setBrowseMapActivity(this);

        mapOverlays.add(browseMapOverlay);
        
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        mapOverlays.add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		myLocationOverlay.enableMyLocation();

		// http://stackoverflow.com/questions/2667386/mapview-getlatitudespan-and-getlongitudespan-not-working
		// This is needed as if markertask is called immedieatly Google Maps won't have finished initilising and 
		// getLatitudeSpan/getLongitudeSpan may return bad values. So we have to have a hacky delay here.
		mHandler.postDelayed(mUpdateMarkers, 500);
        
    }
    
    
    
    protected void toDefaultBounds() {
    			
		final SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(this);
		final float latRange =  settings.getFloat("startingBoundsMaxLat", STARTING_BOUNDS_MAX_LAT) - settings.getFloat("startingBoundsMinLat", STARTING_BOUNDS_MIN_LAT);
		final float lat = latRange/2 + settings.getFloat("startingBoundsMinLat", STARTING_BOUNDS_MIN_LAT);
		final float lngRange = settings.getFloat("startingBoundsMaxLng", STARTING_BOUNDS_MAX_LNG) - settings.getFloat("startingBoundsMinLng", STARTING_BOUNDS_MIN_LNG);
		final float lng = lngRange/2 + settings.getFloat("startingBoundsMinLng", STARTING_BOUNDS_MIN_LNG);
		mapController.setCenter(new GeoPoint(Math.round(lat*1000000), Math.round(lng*1000000)));
		mapController.zoomToSpan(Math.round(latRange*1000000), Math.round(lngRange*1000000));
    }
    
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	myLocationOverlay.disableMyLocation();
    	myLocationOverlay.disableCompass();
    	mHandler.removeCallbacks(mUpdateMarkers);
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	myLocationOverlay.enableCompass();
    	myLocationOverlay.enableMyLocation();
    	mHandler.postDelayed(mUpdateMarkers, 1000);
    }  

	Double lastSeenTop,lastSeenBottom,lastSeenLeft,lastSeenRight;
	
	/*
	 * This calls regularly and when the map changes, we load and add markers. 
	 */
	private Runnable mUpdateMarkers = new Runnable() {
		public void run() {

			//Log.d("RUN","NOW");

			GeoPoint c = mapView.getMapCenter();
			int latSpan = mapView.getLatitudeSpan() / 2;
			int lngSpan = mapView.getLongitudeSpan() / 2;

			Double top = ((double)(c.getLatitudeE6() + latSpan))/1000000.0;
			Double bottom = ((double)(c.getLatitudeE6() - latSpan))/1000000.0;	        
			Double left = ((double)(c.getLongitudeE6() - lngSpan))/1000000.0;
			Double right = ((double)(c.getLongitudeE6() + lngSpan))/1000000.0;

			if (lastSeenTop == null || top.compareTo(lastSeenTop) != 0 || bottom.compareTo(lastSeenBottom) != 0 || left.compareTo(lastSeenLeft) != 0 || right.compareTo(lastSeenRight) != 0) {

				//Log.d("MAP L",Double.toString(left));
				//Log.d("MAP R",Double.toString(right));
				//Log.d("MAP T",Double.toString(top));
				//Log.d("MAP B",Double.toString(bottom));

				lastSeenBottom = bottom;
				lastSeenTop = top;
				lastSeenLeft = left;
				lastSeenRight = right;

				if (markerTask != null) markerTask.cancel(true);
				markerTask = new MarkerTask(top,bottom,left,right);
				markerTask.execute(true);
			}

			mHandler.postDelayed(this, 1000);

		}
	};
	
	private class MarkerTask extends AsyncTask<Boolean, Void, Boolean> {
		List<Feature> features;
		Double top,bottom,left,right;

		public MarkerTask(Double top, Double bottom, Double left, Double right) {
			super();
			this.top = top;
			this.bottom = bottom;
			this.right = right;
			this.left = left;
		}

		protected Boolean doInBackground(Boolean... dummy) {
			Storage s = new Storage(BrowseMapActivity.this);
			features = s.getFeatures(top, bottom, left, right);
			return false;
		}

		protected void onPostExecute(Boolean result) {
			if (!isCancelled()) {
				//Log.d("FOUND",Integer.toString(features.size()));
				int added = 0;
				for (Feature feature : features) {
					if (!featureAdded.containsKey(Integer.valueOf(feature.getId()))) {
						++added;
						featureAdded.put(Integer.valueOf(feature.getId()), true);
						browseMapOverlay.addFeature(feature);
					}
				}
				if (added > 0) {
					// Sometimes markers don't appear so we need to invalidate map to make them.
					// specifically, a) go into map, b) go back to main menu c) go into map - markers don't appear
					mapView.invalidate();
				}
				//Log.d("ADDED",Integer.toString(added));
			}
		}

	}
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browse_map_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	GeoPoint p;
        switch (item.getItemId()) {
        case R.id.add_content_current_position:
        	p = myLocationOverlay.getMyLocation();
        	if (p != null) {
	        	Intent i = new Intent(this, NewFeatureContentActivity.class);
	        	i.putExtra("lat", (float)p.getLatitudeE6()/1000000);
	        	i.putExtra("lng", (float)p.getLongitudeE6()/1000000);
	        	startActivity(i);
        	} else {
        		Toast.makeText(this, "We can not find your current location.", Toast.LENGTH_SHORT).show();
        	}
            return true;
        case R.id.report_it_current_position:
        	p = myLocationOverlay.getMyLocation();
        	if (p != null) {
	        	Intent i = new Intent(this, NewFeatureReportActivity.class);
	        	i.putExtra("lat", (float)p.getLatitudeE6()/1000000);
	        	i.putExtra("lng", (float)p.getLongitudeE6()/1000000);
	        	startActivity(i);
        	} else {
        		Toast.makeText(this, "We can not find your current location.", Toast.LENGTH_SHORT).show();
        	}
            return true;
        case R.id.go_to_my_current_location:
        	p = myLocationOverlay.getMyLocation();
        	if (p != null) {
        		mapController.setCenter(p);
        		if (mapView.getZoomLevel() < 14) {
        			mapController.setZoom(14);
        		}
        	} else {
        		Toast.makeText(this, "We can not find your current location.", Toast.LENGTH_SHORT).show();
        	}
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
    
}