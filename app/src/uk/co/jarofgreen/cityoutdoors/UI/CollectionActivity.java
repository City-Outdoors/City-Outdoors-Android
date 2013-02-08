package uk.co.jarofgreen.cityoutdoors.UI;


import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.API.FeatureCall;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Service.SendFeatureFavouriteService;
import uk.co.jarofgreen.cityoutdoors.R;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class CollectionActivity extends BaseListActivity {

	int collectionID;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ListView listView = getListView();  

		// view 
		LayoutInflater layoutInflater = getLayoutInflater();
		View header = layoutInflater.inflate(R.layout.title_bar,null);
		listView.addHeaderView(header, new Object(), false);
		TitleBar.populate(this);
		
		listView.setBackgroundResource(R.drawable.background);
		listView.setCacheColorHint(Color.WHITE);

		// data		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			collectionID = extras.getInt("collectionID");
		}
		
		 Storage storage = new Storage(getApplicationContext());
         SQLiteDatabase db = storage.getReadableDatabase();

         final String fields[] = { BaseColumns._ID, "title"};
         Cursor mCursor = db.query("item", fields, " collection_id="+Integer.toString(collectionID), null, null, null, "title ASC");
         startManagingCursor(mCursor);
         ListAdapter adapter = new SimpleCursorAdapter(
                 this,
                 R.layout.item_row,
                 mCursor,
                 new String[] {"title"},
                 new int[] {R.id.title});
         setListAdapter(adapter);      

         listView.setOnItemClickListener(new OnItemClickListener() {
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                         Intent intent = new Intent(CollectionActivity.this, FeatureActivity.class);
                         Storage s = new Storage(CollectionActivity.this.getApplicationContext());
                         int featureID = s.getFeatureIdOfItem((int)id);
                         intent.putExtra("featureID",featureID);
                         startActivity(intent);
                 }
         });  
	}	
	
}
