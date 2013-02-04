package uk.co.jarofgreen.cityoutdoors.Model;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class Feature {

	protected int id;
	protected float lat;
	protected float lng;
	protected int collectionID;
	protected String shareURL;
	protected String title;
	
	public Feature() {
		super();
	}
	
	public Feature(int id, float lat, float lng) {
		super();
		this.id = id;
		this.lat = lat;
		this.lng = lng;
	}

	public Feature(int id, float lat, float lng, int collectionID) {
		super();
		this.id = id;
		this.lat = lat;
		this.lng = lng;
		this.collectionID = collectionID;
	}

	public Feature(int id, float lat, float lng, int collectionID, String title) {
		super();
		this.id = id;
		this.lat = lat;
		this.lng = lng;
		this.collectionID = collectionID;
		this.title = title;
	}
	
	public int getId() {
		return id;
	}

	public float getLat() {
		return lat;
	}
	
	public void setLat(float lat) {
		this.lat = lat;
	}

	public void setLat(String lat) {
		if (lat != null) {
			this.lat = Float.parseFloat(lat);
		}
	}	
	
	public float getLng() {
		return lng;
	}
	
	public void setLng(float lng) {
		this.lng = lng;
	}

	public void setLng(String lng) {
		if (lng != null) {
			this.lng = Float.parseFloat(lng);
		}
	}	

	public int getCollectionID() {
		return collectionID;
	}


	public void setCollectionID(int collectionID) {
		this.collectionID = collectionID;
	}

	public String getShareURL() {
		return shareURL;
	}

	public void setShareURL(String shareURL) {
		this.shareURL = shareURL;
	}

	public String getTitle() {
		return title;
	}

	public String getTitle(String defaultValue) {
		if (title != null && title.length() > 0) {
			return title;
		} else {
			return defaultValue;
		}
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setId(String id) {
		if (id != null) this.id = Integer.parseInt(id);
	}



		
	
	
	
}
