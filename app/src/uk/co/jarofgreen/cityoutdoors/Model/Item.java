package uk.co.jarofgreen.cityoutdoors.Model;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
import java.util.ArrayList;
import java.util.List;

public class Item {

	private int id;
	private int collection_id;
	private int feature_id;
	private String slug;
	private String title;
	private List<ItemField> fields;
	
	
	
	public Item() {
		super();
		fields = new ArrayList<ItemField>();
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
	
	public int getCollectionId() {
		return collection_id;
	}
	public void setCollectionId(int collection_id) {
		this.collection_id = collection_id;
	}
	public void setCollectionId(String collection_id) {
		if (collection_id != null) this.collection_id = Integer.parseInt(collection_id);
	}		
	
	public String getSlug() {
		return slug;
	}
	public void setSlug(String slug) {
		this.slug = slug;
	}

	public int getFeatureId() {
		return feature_id;
	}
	public void setFeatureId(int feature_id) {
		this.feature_id = feature_id;
	}
	
	public void setFeatureId(String feature_id) {
		if (feature_id != null) this.feature_id = Integer.parseInt(feature_id);
	}	
	
	public void addField(ItemField field) {
		fields.add(field);
	}

	public List<ItemField> getFields() {
		return fields;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
