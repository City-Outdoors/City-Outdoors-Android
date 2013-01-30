package uk.co.jarofgreen.cityoutdoors.UI;


import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.API.FeatureCall;
import uk.co.jarofgreen.cityoutdoors.API.FeatureCheckinQuestionsCall;
import uk.co.jarofgreen.cityoutdoors.API.LogInCall;
import uk.co.jarofgreen.cityoutdoors.API.SubmitFeatureCheckinQuestionAnswerCall;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestion;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import uk.co.jarofgreen.cityoutdoors.Model.ItemField;
import uk.co.jarofgreen.cityoutdoors.Service.SendFeatureFavouriteService;
import uk.co.jarofgreen.cityoutdoors.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureCheckinQuestionsActivity extends BaseActivity {

	int featureID;

	ProgressDialog mDialog;
	
	HashMap childViews = new LinkedHashMap<Integer, View>();
	FeatureCheckinQuestionsTask featureCheckinQuestionsTask;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isUserLoggedIn()) {
			setContentView(R.layout.feature_checkin_question);
		} else {
			setContentView(R.layout.feature_checkin_question_loggedout);
		}
		TitleBar.populate(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureID = extras.getInt("featureID");
			Log.d("FEATURE from INTENT",Integer.toString(featureID));
		}
		
		featureCheckinQuestionsTask = new FeatureCheckinQuestionsTask(featureID);
		
    	mDialog = new ProgressDialog(this);
    	mDialog.setMessage("Loading, please wait ...");
    	mDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
            	featureCheckinQuestionsTask.cancel(true);
            	FeatureCheckinQuestionsActivity.this.finish();
            }
        });
    	mDialog.setCancelable(true);
    	mDialog.show();
		
		featureCheckinQuestionsTask.execute(true);
	}

	
	public void onClickAnswer(View view) {
		final FeatureCheckinQuestion fcq = (FeatureCheckinQuestion)view.getTag();
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle("Enter your answer");
		alert.setMessage(fcq.getQuestion());
		
		final EditText input = new EditText(this);
		alert.setView(input);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				
				mDialog.setMessage("Checking answer, please wait ...");
				mDialog.show();
				CheckAnswerTask t = new CheckAnswerTask(fcq.getId(), value);
				t.execute(true);
				
			}
		});
		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		
		alert.show();
		
	}

	private class FeatureCheckinQuestionsTask extends AsyncTask<Boolean, Void, Boolean> {

		protected int featureID;
		protected FeatureCheckinQuestionsCall call;

		public FeatureCheckinQuestionsTask(int featureID) {
			super();
			this.featureID = featureID;
		}

		protected Boolean doInBackground(Boolean... dummy) {

			try{

				call = new FeatureCheckinQuestionsCall(FeatureCheckinQuestionsActivity.this);
				call.execute(featureID);

			} catch(Exception e) {
				Log.d("ERRORINLOGIN",e.toString());
				if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
			}

			return false;

		}

		protected void onPostExecute(Boolean result) {
			FeatureCheckinQuestionsActivity.this.mDialog.dismiss();

			LinearLayout parent = (LinearLayout)findViewById(R.id.content_container);
			
			LayoutInflater layoutInflater = getLayoutInflater();
			
			for (FeatureCheckinQuestion featureCheckinQuestion : call.getCheckinQuestions()) {
			
				
				View child;
				
				if (isUserLoggedIn()) {
					child = layoutInflater.inflate(R.layout.feature_checkin_question_question_row,null);
					
					View button = child.findViewById(R.id.answer);
					View answered = child.findViewById(R.id.answered);
					if (featureCheckinQuestion.isHasAnswered()) {
						button.setVisibility(View.INVISIBLE);
						answered.setVisibility(View.VISIBLE);
					} else {
						button.setVisibility(View.VISIBLE);
						button.setTag(featureCheckinQuestion);
						answered.setVisibility(View.INVISIBLE);
					}
					
				} else {
					child = layoutInflater.inflate(R.layout.feature_checkin_question_question_row_loggedout,null);
				}
				
				TextView tv = (TextView)child.findViewById(R.id.question);
				tv.setText(featureCheckinQuestion.getQuestion());
				
				parent.addView(child);
				
				childViews.put(Integer.valueOf(featureCheckinQuestion.getId()), child);
			}

		}

	}
	
	private class CheckAnswerTask extends AsyncTask<Boolean, Void, Boolean> {

		protected int featureCheckinQuestionID;
		protected String answer;
		protected SubmitFeatureCheckinQuestionAnswerCall call;
		
		public CheckAnswerTask(int featureCheckinQuestionID, String answer) {
			super();
			this.featureCheckinQuestionID = featureCheckinQuestionID;
			this.answer = answer;
		}

		protected Boolean doInBackground(Boolean... dummy) {
			
	    	
			
			
			try{

				call = new SubmitFeatureCheckinQuestionAnswerCall(FeatureCheckinQuestionsActivity.this);
				call.execute(featureCheckinQuestionID, answer);

			} catch(Exception e) {
				Log.d("ERRORINLOGIN",e.toString());
				if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
			}

			return false;

		}

		protected void onPostExecute(Boolean result) {
			mDialog.dismiss();
			
			if (call.getResult()) {
				Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();

				// change screen underneath
				View child = (View)childViews.get(Integer.valueOf(featureCheckinQuestionID));
				View button = child.findViewById(R.id.answer);
				View answered = child.findViewById(R.id.answered);				
				button.setVisibility(View.INVISIBLE);
				answered.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(getApplicationContext(), "Sorry, that is the wrong answer", Toast.LENGTH_LONG).show();
			}
		}

	}
}
