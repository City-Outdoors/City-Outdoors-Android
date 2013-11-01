package uk.co.jarofgreen.cityoutdoors.UI;



import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.R;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

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
		
		 Storage storage = ((OurApplication)getApplication()).getStorage();
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
                         Storage s = ((OurApplication)getApplication()).getStorage();
                         int featureID = s.getFeatureIdOfItem((int)id);
                         intent.putExtra("featureID",featureID);
                         startActivity(intent);
                 }
         });  
	}	
	
}
