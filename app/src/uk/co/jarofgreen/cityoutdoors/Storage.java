package uk.co.jarofgreen.cityoutdoors;


import java.util.ArrayList;
import java.util.List;

import uk.co.jarofgreen.cityoutdoors.Model.Collection;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckin;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureFavourite;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class Storage extends SQLiteOpenHelper {
	
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "heresatree.db";

    public Storage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE  feature ( "+
	         BaseColumns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
	         " lat REAL NOT NULL, "+
	         " lng REAL NOT NULL, "+
	         " collectionID INTEGER NULL, "+
	         " title VARCHAR(255) NULL " +
	         ");");
        db.execSQL("CREATE TABLE  feature_favourite ( "+
      	         " feature_id INTEGER NOT NULL, " +
      	         " favourite_at INTEGER NOT NULL, "+
      	         " server TINYINT NOT NULL DEFAULT 0 "+      	         
  	         ");");            
        db.execSQL("CREATE TABLE  collection ( "+
   	         BaseColumns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
   	         " title VARCHAR(255) NOT NULL, "+
   	         " slug VARCHAR(255) NOT NULL, "+
   	         " iconURL VARCHAR(255) NULL, "+
   	         " thumbnailURL VARCHAR(255) NULL, "+
   	         " description TEXT NULL "+
   	         ");");
        db.execSQL("CREATE TABLE  item ( "+
      	         BaseColumns._ID + " INTEGER PRIMARY KEY NOT NULL, " +
      	         " collection_id INTEGER NOT NULL, "+
      	         " feature_id INTEGER NOT NULL, "+
      	         " slug VARCHAR(255) NOT NULL, "+
      	         " title VARCHAR(255) NULL "+
      	         ");");              
    }
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		if (arg1 < 4) {
			arg0.execSQL("ALTER TABLE feature ADD title VARCHAR(255) NULL " );
		}
	
	}
	
	public void storeFeature(Feature feature) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put("lat", feature.getLat());
		cv.put("lng", feature.getLng());
		cv.put("collectionID", feature.getCollectionID());
		cv.put("title", feature.getTitle());
		String[]  whereArgs = { Integer.toString(feature.getId()) };
		if (0 == db.update("feature", cv,  BaseColumns._ID+"=?", whereArgs)){
			
			cv.put(BaseColumns._ID,feature.getId());
			db.insert("feature", null, cv);
			Log.d("STORAGE","Inserted Feature "+Integer.toString(feature.getId()));
			
		} else {
			Log.d("STORAGE","Updated Feature "+Integer.toString(feature.getId()));
		}
		
		
		db.close();
	}
	
	public void storeItem(Item item) {
		SQLiteDatabase db = this.getWritableDatabase();
		String[]  whereArgs = { Integer.toString(item.getId()) };
		if (item.isDeleted()) {
			db.delete("item", BaseColumns._ID+"=?", whereArgs);
			Log.d("STORAGE","Deleted Item "+Integer.toString(item.getId()));
		} else {
			ContentValues cv = new ContentValues();
			cv.put("collection_id", item.getCollectionId());
			cv.put("feature_id", item.getFeatureId());
			cv.put("slug", item.getSlug());
			cv.put("title", item.getTitle());
			if (0 == db.update("item", cv,  BaseColumns._ID+"=?", whereArgs)){
				cv.put(BaseColumns._ID,item.getId());
				db.insert("item", null, cv);
				Log.d("STORAGE","Inserted Item "+Integer.toString(item.getId()));
			} else {
				Log.d("STORAGE","Updated Item "+Integer.toString(item.getId()));
			}
		}
		db.close();
	}	
	
	public List<Feature> getFeatures() {
        
		List<Feature> features = new ArrayList<Feature>();
        SQLiteDatabase db = getReadableDatabase();
        String[]  d = { };
        Cursor c = db.rawQuery("SELECT "+BaseColumns._ID+", lat, lng, collectionID, title FROM feature ", d);
        for(int i = 0; i < c.getCount(); i++) {
                c.moveToPosition(i);
                features.add(new Feature(c.getInt(0),c.getFloat(1),c.getFloat(2),c.getInt(3),c.getString(4))); 
        }
        db.close();
        return features;
	}
	
	

	public List<Feature> getFeatures(Double top, Double bottom, Double left, Double right) {
		List<Feature> features = new ArrayList<Feature>();
		SQLiteDatabase db = getReadableDatabase();
		String[]  d = { Double.toString(top), Double.toString(bottom),  Double.toString(left), Double.toString(right) };
		Cursor c = db.rawQuery("SELECT "+BaseColumns._ID+", lat, lng, collectionID, title FROM feature WHERE lat < ? AND lat > ? AND lng > ? AND lng < ?", d);
		for(int i = 0; i < c.getCount(); i++) {
			c.moveToPosition(i);
			features.add(new Feature(c.getInt(0),c.getFloat(1),c.getFloat(2),c.getInt(3),c.getString(4))); 
		}
		db.close();
		return features;
	}

	public Feature getFeature(int id) {
		Feature f = null;
		SQLiteDatabase db = getReadableDatabase();
		String[]  d = { Integer.toString(id) };
		Cursor c = db.rawQuery("SELECT "+BaseColumns._ID+", lat, lng, collectionID FROM feature WHERE "+BaseColumns._ID+"=?", d);
		if (c.getCount() > 0){
			c.moveToPosition(0);
			f = new Feature(c.getInt(0),c.getFloat(1),c.getFloat(2),c.getInt(3));
		}
		db.close();
		return f;
	}
	
		
	public List<Collection> getCollections() {
        
		List<Collection> collections = new ArrayList<Collection>();
        SQLiteDatabase db = getReadableDatabase();
        String[]  d = { };
        Cursor c = db.rawQuery("SELECT "+BaseColumns._ID+", slug, title, thumbnailURL, iconURL, description FROM collection ", d);
        for(int i = 0; i < c.getCount(); i++) {
                c.moveToPosition(i);
                collections.add(new Collection(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5))); 
        }
        db.close();
        return collections;
	}

	public int getFeatureIdOfItem(int itemID) {

		List<Collection> collections = new ArrayList<Collection>();
		SQLiteDatabase db = getReadableDatabase();
		String[]  d = { Integer.toString(itemID) };
		Cursor c = db.rawQuery("SELECT feature_id FROM item WHERE "+BaseColumns._ID+"=?", d);
		if (c.getCount() == 0) {
			throw new RuntimeException("ID Not Found");
		}
		c.moveToPosition(0);
		int i = c.getInt(0); 
		db.close();
		return i; 
	}
		
	
	
	public void storeCollection(Collection collection) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put("title", collection.getTitle());
		cv.put("slug", collection.getSlug());
		cv.put("iconURL", collection.getIconURL());
		cv.put("thumbnailURL", collection.getThumbnailURL());
		cv.put("description", collection.getDescription());
		String[]  whereArgs = { Integer.toString(collection.getId()) };
		if (0 == db.update("collection", cv,  BaseColumns._ID+"=?", whereArgs)){
			
			cv.put(BaseColumns._ID,collection.getId());
			db.insert("collection", null, cv);
			Log.d("STORAGE","Inserted Collection "+Integer.toString(collection.getId()));
			
		} else {
			Log.d("STORAGE","Updated Collection "+Integer.toString(collection.getId()));
		}
		
		
		db.close();
	}
		
	

	public void featureFavourite(int featureID, Boolean server) {
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put("feature_id", featureID);
		Long tsLong = System.currentTimeMillis()/1000;
		cv.put("favourite_at", tsLong.toString());
		if (server) {
			cv.put("server", 1);
		} else {
			cv.put("server", 0);
		}
		db.insert("feature_favourite", null, cv);
		
		Log.d("STORAGE","Favourite Feature "+Integer.toString(featureID));
		
		db.close();		
	}
	
	public List<FeatureFavourite> getFeatureFavouritesToSendToServer() {
		List<FeatureFavourite> featureCheckins = new ArrayList<FeatureFavourite>();
        SQLiteDatabase db = getReadableDatabase();
        String[]  d = { };
        Cursor c = db.rawQuery("SELECT feature_id, favourite_at FROM feature_favourite WHERE server=0 ", d);
        for(int i = 0; i < c.getCount(); i++) {
                c.moveToPosition(i);
                featureCheckins.add(new FeatureFavourite(c.getInt(0),c.getInt(1))); 
        }
        db.close();		
		return featureCheckins;
	}
	
	public void featureFavouriteSentToServer(FeatureFavourite featureFavourite) {
		SQLiteDatabase db = this.getWritableDatabase();
		
        String[]  d2 = { Integer.toString(featureFavourite.getFeatureID()) };
		db.execSQL("UPDATE feature_favourite SET server=1 WHERE feature_id=?", d2);
		
		db.close();		
	}
	
	
	
	
	public void deleteUserData() {
		SQLiteDatabase db = this.getWritableDatabase();

		String[]  d2 = { };
		db.execSQL("DELETE FROM feature_favourite", d2);

		db.close();			
	}

}

