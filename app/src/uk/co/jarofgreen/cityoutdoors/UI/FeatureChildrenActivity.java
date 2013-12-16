package uk.co.jarofgreen.cityoutdoors.UI;

import java.util.List;

import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.R;
import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class FeatureChildrenActivity extends BaseActivity {

	int featureID;
	ListViewAdaptor listViewAdaptor;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feature_children);  

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureID = extras.getInt("featureID");
		}

		ListView listView = (ListView)findViewById(R.id.list_view);

		listView.setBackgroundResource(R.drawable.background);
		listView.setCacheColorHint(Color.WHITE);

		listViewAdaptor = new ListViewAdaptor();
		listView.setAdapter(listViewAdaptor);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(FeatureChildrenActivity.this, FeatureActivity.class);
				intent.putExtra("featureID",((Item)listViewAdaptor.getItem(position)).getFeatureId());
				startActivity(intent);
			}
		});  
	}


	protected class ListViewAdaptor extends BaseAdapter {


		protected List<Item> data;
		protected LayoutInflater layoutInflater; 

		public ListViewAdaptor() {
			super();
			Storage storage = ((OurApplication)getApplication()).getStorage();
			data = storage.getChildItemsOfFeature(featureID);
			layoutInflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );

		}

		public int getCount() {
			return data.size();
		}

		public Object getItem(int arg0) {
			return data.get(arg0);
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
			if (convertView != null) {
				view = convertView;
			} else {
				view = layoutInflater.inflate( R.layout.item_row, parent, false );
			}

			TextView textView;

			textView = (TextView)view.findViewById(R.id.title);
			textView.setText(data.get(position).getTitle());

			return view;
		}

	}


}

