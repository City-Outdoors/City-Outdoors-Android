package uk.co.jarofgreen.cityoutdoors.UI;

import java.util.HashMap;
import java.util.List;
import java.util.Set;



import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Collection;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;
import uk.co.jarofgreen.cityoutdoors.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class BrowseMapActivity extends android.support.v4.app.FragmentActivity implements OnInfoWindowClickListener {
	
	protected GoogleMap map;
	protected HashMap<Integer, Marker> featureMarkers = new HashMap<Integer, Marker>();	
	protected HashMap<Integer, BitmapDescriptor> collectionIcons = new HashMap<Integer, BitmapDescriptor>();
	protected HashMap<Integer, BitmapDescriptor> collectionQuestionIcons = new HashMap<Integer, BitmapDescriptor>();
	
	public static final double STARTING_LAT = (double) 55.95284338416757;
	public static final double STARTING_LNG = (double) -3.198369775390575;
	public static final int STARTING_ZOOM = 12;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_map);
        TitleBar.populate(this);
        setUpMapIfNeeded();
    }

    @Override
    protected void onPause() {
    	super.onPause();
    	if (map != null) {
    		map.setMyLocationEnabled(false);
    	}
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	setUpMapIfNeeded();
    	if (map != null) {
    		map.setMyLocationEnabled(true);
    	}
    }  

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView
     * MapView}) will show a prompt for the user to install/update the Google Play services APK on
     * their device.
     * <p>
     * A user can return to this Activity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the Activity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
        	map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview)).getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // first, do we start at a certain point or do we start at the default bounds?
        boolean zoomed = false;
        Bundle extras = getIntent().getExtras();
        
		if (extras != null) {
			Float lat = extras.getFloat("lat");
			Float lng = extras.getFloat("lng");
			Log.d("LOADMAP", Float.toString(lat));
			Log.d("LOADMAP", Float.toString(lng));
			if (lat != 0.0 && lng != 0.0) {
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 18));
				zoomed = true;
			}
		}
		

		if (!zoomed) {
			// We can't call newLatLngBounds at this point, the map isn't sized and app crashes.
			// So first we set the map to our hard coded range so it's at least sensible.
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(STARTING_LAT,STARTING_LNG), STARTING_ZOOM));

			// now set up a handler to zoom to correct place a second later. This feels like a bad hack.
			Handler handler=new Handler();
			final Runnable r = new Runnable() {
				public void run() {
					SharedPreferences settings=PreferenceManager.getDefaultSharedPreferences(BrowseMapActivity.this);
					float startingBoundsMaxLat = settings.getFloat("startingBoundsMaxLat", 0);
					float startingBoundsMinLat = settings.getFloat("startingBoundsMinLat", 0);
					float startingBoundsMaxLng = settings.getFloat("startingBoundsMaxLng", 0);
					float startingBoundsMinLng = settings.getFloat("startingBoundsMinLng", 0);

					if (!(startingBoundsMaxLat == 0 && startingBoundsMinLat == 0 && startingBoundsMaxLng == 0 && startingBoundsMinLng == 0)) {
						LatLng southwest = new LatLng(startingBoundsMinLat, startingBoundsMinLng);
						LatLng northeast = new LatLng(startingBoundsMaxLat, startingBoundsMaxLng);
						try {
							map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southwest, northeast), 10));
						} catch (Exception e) {
							// this feels like a bad hack, so if it crashes just ignore it. We will still be zoomed to sensible bounds.
						}
					}
				}
			};
			handler.postDelayed(r, 250);			
		}

		// icons

		Storage s = new Storage(this);
		for (Collection collection : s.getCollections()) {
			// normal icon
			BitmapDescriptor icon = null;
			if (collection.getIconURL() != null) {
				if (collection.getIconURL().contains("park")) {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_park);
				} else if (collection.getIconURL().contains("tree")) {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_tree);
				} else if (collection.getIconURL().contains("monument")) {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_monument);
				} else if (collection.getIconURL().contains("wc")) {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_wc);
				} else if (collection.getIconURL().contains("play")) {
					icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_playground);
				}
			}
			if (icon != null) {
				collectionIcons.put(collection.getId(), icon);
			}
			// question icon
			BitmapDescriptor questionIcon = null;
			if (collection.getQuestionIconURL() != null) {
				if (collection.getQuestionIconURL().contains("park")) {
					questionIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_park_question);
				} else if (collection.getQuestionIconURL().contains("tree")) {
					questionIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_tree_question);
				} else if (collection.getQuestionIconURL().contains("monument")) {
					questionIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_monument_question);
				} else if (collection.getQuestionIconURL().contains("play")) {
					questionIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_playground_question);
				}
			}
			if (questionIcon != null) {
				collectionQuestionIcons.put(collection.getId(), questionIcon);
			} else if (icon != null) {
				// if we don't have a question icon but we have a normal icon, just reuse that.
				collectionQuestionIcons.put(collection.getId(), icon);
			}
		}
		
		// markers
		map.setOnCameraChangeListener(new OnCameraChangeListener(){
			public void onCameraChange(CameraPosition position) {
				LatLngBounds bounds = BrowseMapActivity.this.map.getProjection().getVisibleRegion().latLngBounds;
				new MarkerTask(bounds.northeast.latitude, bounds.southwest.latitude, bounds.southwest.longitude, bounds.northeast.longitude).execute(true);
			}
		});
        map.setOnInfoWindowClickListener(this);
		
    }
    
	public void onInfoWindowClick(Marker marker) {
		Set<Integer> keys = featureMarkers.keySet();
		for(Integer id: keys) {
			if (marker.equals(featureMarkers.get(id))) {
				startActivity(new Intent(this, FeatureActivity.class).putExtra("featureID", id)); 
				return;
			}
		}
	}

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
				Log.i("FOUND",Integer.toString(features.size()));
				for (Feature feature : features) {
					if (!featureMarkers.containsKey(Integer.valueOf(feature.getId()))) {
						MarkerOptions mo = new MarkerOptions()
							.position(new LatLng(feature.getLat(), feature.getLng()))
							.title(feature.getTitle("Click for More Info"));
						if (feature.isAnsweredAllQuestions()) {
							if (collectionIcons.containsKey(feature.getCollectionID())) {
								mo.icon(collectionIcons.get(feature.getCollectionID()));
							}							
						} else {
							if (collectionQuestionIcons.containsKey(feature.getCollectionID())) {
								mo.icon(collectionQuestionIcons.get(feature.getCollectionID()));
							}							
						}

						Marker m = map.addMarker(mo);
						featureMarkers.put(Integer.valueOf(feature.getId()), m);
					}
				}
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
        switch (item.getItemId()) {
        case R.id.add_content_current_position:
        	startActivity(new Intent(this, NewFeatureContentActivity.class));
            return true;
        case R.id.report_it_current_position:
        	startActivity(new Intent(this, NewFeatureReportActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}