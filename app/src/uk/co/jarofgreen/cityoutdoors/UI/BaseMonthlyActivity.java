package uk.co.jarofgreen.cityoutdoors.UI;

import java.util.Calendar;

import uk.co.jarofgreen.cityoutdoors.R;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class BaseMonthlyActivity  extends BaseActivity implements OnClickListener {

	 protected WebView webview;
	 protected TextView titleTextEdit;
	 
	 protected int month;

	 private final int SWIPE_MIN_DISTANCE = 300;
	 private final int SWIPE_MAX_OFF_PATH = 250;
	 private GestureDetector gestureDetector;
	 View.OnTouchListener gestureListener;
	    
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        Calendar calendar = Calendar.getInstance();
	        month = calendar.get(Calendar.MONTH)+1;
	        	        
	        gestureDetector = new GestureDetector(new MyGestureDetector());
	        gestureListener = new View.OnTouchListener() {
	        	public boolean onTouch(View v, MotionEvent event) {
	        		return gestureDetector.onTouchEvent(event);
	        	}
	        };

	 }
	 	 
	 protected void loadData() {
		 	Log.d("MONTH",Integer.toString(month));
	        if (month == 1) { titleTextEdit.setText(R.string.month_1); }
	        else if (month == 2) { titleTextEdit.setText(R.string.month_2); }
	        else if (month == 3) { titleTextEdit.setText(R.string.month_3); }
	        else if (month == 4) { titleTextEdit.setText(R.string.month_4); }
	        else if (month == 5) { titleTextEdit.setText(R.string.month_5); }
	        else if (month == 6) { titleTextEdit.setText(R.string.month_6); }
	        else if (month == 7) { titleTextEdit.setText(R.string.month_7); }
	        else if (month == 8) { titleTextEdit.setText(R.string.month_8); }
	        else if (month == 9) { titleTextEdit.setText(R.string.month_9); }
	        else if (month == 10) { titleTextEdit.setText(R.string.month_10); }
	        else if (month == 11) { titleTextEdit.setText(R.string.month_11); }
	        else if (month == 12) { titleTextEdit.setText(R.string.month_12); }
	 }

	 protected void prevMonth() {
		 if (month == 1) { month = 12; } else { month = month - 1; }
		 loadData();		 
	 }

	 protected void nextMonth() {
		 if (month == 12) { month = 1; } else { month = month + 1; }
		 loadData();		 
	 }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	switch (item.getItemId()) {
	    	case R.id.prev_month:
	    		prevMonth();
	    		return true;
	    	case R.id.next_month:
	    		nextMonth();
	    		return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
	    	}
	    }	 
	    


	    class MyGestureDetector extends SimpleOnGestureListener {
	    	@Override
	    	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	    		//Log.i("ONFLING","DIST Y "+ Float.toString(Math.abs(e1.getY() - e2.getY())));
	    		if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;
	    		//Log.i("ONFLING"," DIST " + Float.toString(e1.getX() - e2.getX()));
	    		if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
	    			nextMonth();
	    		}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
	    			prevMonth();
	    		}
	    		return false;
	    	}
	    }



	    public void onClick(View arg0) {

	    }
}