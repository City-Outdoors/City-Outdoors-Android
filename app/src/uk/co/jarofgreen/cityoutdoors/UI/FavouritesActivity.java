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
public class FavouritesActivity extends BaseListActivity {

	int collectionID;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// view 
		LayoutInflater layoutInflater = getLayoutInflater();
		View header = layoutInflater.inflate(R.layout.title_bar,null);
		ListView listView = getListView();  
		listView.addHeaderView(header, new Object(), false);
		TitleBar.populate(this);
		listView.setBackgroundResource(R.drawable.background);
		
		// data		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			collectionID = extras.getInt("collectionID");
		}
		
		 Storage storage = new Storage(getApplicationContext());
         SQLiteDatabase db = storage.getReadableDatabase();

         final String args[] = { };
         Cursor mCursor = db.rawQuery("SELECT item."+BaseColumns._ID+", item.title "+
        		 	"FROM item "+
        		 	"JOIN feature_favourite ON feature_favourite.feature_id = item.feature_id", args);
         startManagingCursor(mCursor);
         ListAdapter adapter = new SimpleCursorAdapter(
                 this,
                 R.layout.item_row,
                 mCursor,
                 new String[] {"title"},
                 new int[] {R.id.title});
         setListAdapter(adapter);      

         ListView lv = getListView();
         lv.setOnItemClickListener(new OnItemClickListener() {
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                         Intent intent = new Intent(FavouritesActivity.this, FeatureActivity.class);
                         Storage s = new Storage(FavouritesActivity.this.getApplicationContext());
                         int featureID = s.getFeatureIdOfItem((int)id);
                         intent.putExtra("featureID",featureID);
                         startActivity(intent);
                 }
         });  
	}	
	
}
