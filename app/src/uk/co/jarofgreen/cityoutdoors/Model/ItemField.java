package uk.co.jarofgreen.cityoutdoors.Model;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class ItemField {

	int id;
	String title;
	String valueText;
	String valueHTML;
	boolean hasValue;
	String type;
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		if (id != null) this.id = Integer.parseInt(id);
	}	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getValueHTML() {
		return valueHTML;
	}
	public void setValueHTML(String valueHTML) {
		this.valueHTML = valueHTML;
	}
	public String getValueText() {
		return valueText;
	}
	public void setValueText(String valueText) {
		this.valueText = valueText;
	}
	public boolean isHasValue() {
		return hasValue;
	}
	public void setHasValue(boolean hasValue) {
		this.hasValue = hasValue;
	}
	public void setHasValue(String hasValue) {
		if (hasValue != null) {
			this.hasValue = (hasValue.compareTo("yes") == 0);
		}
	}		
	public String getType() {
		return type;
	}
	public boolean isTypePhone() {
		return this.type.compareTo("phone") == 0;
	}
	public boolean isTypeEmail() {
		return this.type.compareTo("email") == 0;
	}	
	public boolean isTypeHTML() {
		return this.type.compareTo("html") == 0;
	}		
	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
