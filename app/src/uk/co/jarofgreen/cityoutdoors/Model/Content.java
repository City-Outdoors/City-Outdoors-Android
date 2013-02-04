package uk.co.jarofgreen.cityoutdoors.Model;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class Content {

	int id;
	boolean hasPicture;
	String body;
	String displayName;
	
	String pictureFullURL;
	String pictureNormalURL;
	String pictureThumbURL;
	
	public String getPictureFullURL() {
		return pictureFullURL;
	}
	public void setPictureFullURL(String pictureFullURL) {
		this.pictureFullURL = pictureFullURL;
	}
	public String getPictureNormalURL() {
		return pictureNormalURL;
	}
	public void setPictureNormalURL(String pictureNormalURL) {
		this.pictureNormalURL = pictureNormalURL;
	}
	public String getPictureThumbURL() {
		return pictureThumbURL;
	}
	public void setPictureThumbURL(String pictureThumbURL) {
		this.pictureThumbURL = pictureThumbURL;
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
	public boolean isHasPicture() {
		return hasPicture;
	}
	public void setHasPicture(boolean hasPicture) {
		this.hasPicture = hasPicture;
	}
	public void setHasPicture(String hasPicture) {
		if (hasPicture != null) {
			this.hasPicture = (hasPicture.compareTo("1") == 0 || hasPicture.compareTo("yes") == 0);
		}
	}	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	
}
