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
import uk.co.jarofgreen.cityoutdoors.API.SubmitFeatureCheckinQuestionFreeTextAnswerCall;
import uk.co.jarofgreen.cityoutdoors.API.SubmitFeatureCheckinQuestionHigherOrLowerAnswerCall;
import uk.co.jarofgreen.cityoutdoors.API.SubmitFeatureCheckinQuestionMultipleChoiceAnswerCall;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestion;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionContent;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionFreeText;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionHigherOrLower;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionMultipleChoice;
import uk.co.jarofgreen.cityoutdoors.Model.FeatureCheckinQuestionPossibleAnswer;
import uk.co.jarofgreen.cityoutdoors.Model.Item;
import uk.co.jarofgreen.cityoutdoors.Model.ItemField;
import uk.co.jarofgreen.cityoutdoors.Service.LoadUserDataService;
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
import android.sax.TextElementListener;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    	mDialog.setMessage(getString(R.string.loading_data_from_server_wait));
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

	
	public void onClickAnswerFreeText(View view) {
		final FeatureCheckinQuestionFreeText fcq = (FeatureCheckinQuestionFreeText)view.getTag();
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle(getString(R.string.checkin_answer_box_title));
		alert.setMessage(fcq.getQuestion());
		
		final EditText input = new EditText(this);
		alert.setView(input);
		
		alert.setPositiveButton(getString(R.string.checkin_answer_box_ok_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				mDialog.setMessage(getString(R.string.checkin_checking_answer_with_server_wait));
				mDialog.show();
				CheckFreeTextAnswerTask t = new CheckFreeTextAnswerTask(fcq, value);
				t.execute(true);
			}
		});
		
		alert.setNegativeButton(getString(R.string.checkin_answer_box_cancel_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		
		alert.show();
		
	}
	
	public void onClickAnswerContent(View view) {
		Intent i = new Intent(this, NewFeatureContentActivity.class);
		i.putExtra("featureID", featureID);
		startActivity(i);
	}

	public void onClickAnswerHigherOrLower(View view) {
		final FeatureCheckinQuestionHigherOrLower fcq = (FeatureCheckinQuestionHigherOrLower)view.getTag();
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		alert.setTitle(getString(R.string.checkin_answer_box_title));
		alert.setMessage(fcq.getQuestion());
		
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);
		
		alert.setPositiveButton(getString(R.string.checkin_answer_box_ok_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				mDialog.setMessage(getString(R.string.checkin_checking_answer_with_server_wait));
				mDialog.show();
				CheckHigherOrLowerAnswerTask t = new CheckHigherOrLowerAnswerTask(fcq, value);
				t.execute(true);
			}
		});
		
		alert.setNegativeButton(getString(R.string.checkin_answer_box_cancel_button), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		
		alert.show();
	}	
	

	public void onClickAnswerMultipleChoice(View view) {
		FeatureCheckinQuestionMultipleChoice featureCheckinQuestion = (FeatureCheckinQuestionMultipleChoice)view.getTag();
		View fcqview = (View)childViews.get(Integer.valueOf(featureCheckinQuestion.getId()));
		RadioGroup radioGroupView = (RadioGroup)fcqview.findViewById(R.id.possible_answers_radio_group);
		
		if (radioGroupView.getCheckedRadioButtonId() > 0) {
			RadioButton radioButton = (RadioButton)radioGroupView.findViewById(radioGroupView.getCheckedRadioButtonId());
			FeatureCheckinQuestionPossibleAnswer answer = (FeatureCheckinQuestionPossibleAnswer)radioButton.getTag();
			
			mDialog.setMessage(getString(R.string.checkin_checking_answer_with_server_wait));
			mDialog.show();
			CheckMultipleChoiceAnswerTask t = new CheckMultipleChoiceAnswerTask(featureCheckinQuestion,answer);
			t.execute(true);
			
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.checkin_no_multiplechoice_selected), Toast.LENGTH_LONG).show();
		}
	}		
	
	public void onClickAnswerExplanation(View view) {
		FeatureCheckinQuestion featureCheckinQuestion = (FeatureCheckinQuestion)view.getTag();
		Intent i = new Intent(this, FeatureCheckinQuestionExplanationActivity.class);
		i.putExtra("html", featureCheckinQuestion.getExplanationHTML());
		startActivity(i);
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
			
			for (FeatureCheckinQuestion featureCheckinQuestion : call.getCheckinQuestions()) {
				if (featureCheckinQuestion.isActive() && !featureCheckinQuestion.isDeleted()) {
					addQuestionToLayout(featureCheckinQuestion, parent);
				}
			}

		}
		
		protected void addQuestionToLayout(FeatureCheckinQuestion featureCheckinQuestion, LinearLayout parent) {
			LayoutInflater layoutInflater = getLayoutInflater();
			
			View child;
			
			if (isUserLoggedIn()) {
				if (featureCheckinQuestion instanceof FeatureCheckinQuestionFreeText) {
					child = layoutInflater.inflate(R.layout.feature_checkin_question_freetext_question_row,null);
				} else if (featureCheckinQuestion instanceof FeatureCheckinQuestionContent) {
					child = layoutInflater.inflate(R.layout.feature_checkin_question_content_question_row,null);
				} else if (featureCheckinQuestion instanceof FeatureCheckinQuestionMultipleChoice) {
					child = layoutInflater.inflate(R.layout.feature_checkin_question_multiplechoice_question_row,null);
				} else if (featureCheckinQuestion instanceof FeatureCheckinQuestionHigherOrLower) {
					child = layoutInflater.inflate(R.layout.feature_checkin_question_higherorlower_question_row,null);
				} else {
					return; // this should be impossible ...
				}
				
				View button = child.findViewById(R.id.answer);
				View answered = child.findViewById(R.id.answered);
				if (featureCheckinQuestion.isHasAnswered()) {
					if (!featureCheckinQuestion.canAnswerMultipleTimes()) button.setVisibility(View.INVISIBLE);
					answered.setVisibility(View.VISIBLE);
					if (featureCheckinQuestion.hasExplanationHTML()) {
						View ae = child.findViewById(R.id.answer_explanation);
						ae.setVisibility(View.VISIBLE);
						ae.setTag(featureCheckinQuestion);
					}
				} else {
					button.setVisibility(View.VISIBLE);
					button.setTag(featureCheckinQuestion);
					answered.setVisibility(View.INVISIBLE);
				}
				
			} else {
				if (featureCheckinQuestion instanceof FeatureCheckinQuestionFreeText) {
					child = layoutInflater.inflate(R.layout.feature_checkin_question_freetext_question_row_loggedout,null);
				} else if (featureCheckinQuestion instanceof FeatureCheckinQuestionContent) {
					child = layoutInflater.inflate(R.layout.feature_checkin_question_content_question_row_loggedout,null);
				} else if (featureCheckinQuestion instanceof FeatureCheckinQuestionMultipleChoice) {
					child = layoutInflater.inflate(R.layout.feature_checkin_question_multiplechoice_question_row_loggedout,null);
				} else if (featureCheckinQuestion instanceof FeatureCheckinQuestionHigherOrLower) {
					child = layoutInflater.inflate(R.layout.feature_checkin_question_higherorlower_question_row_loggedout,null);
				} else {
					return; // this should be impossible ...
				}
			}
			
			if (featureCheckinQuestion instanceof FeatureCheckinQuestionMultipleChoice) {
				if (featureCheckinQuestion.isHasAnswered()) {
					child.findViewById(R.id.answer).setVisibility(View.INVISIBLE);
					child.findViewById(R.id.possible_answers_radio_group).setVisibility(View.INVISIBLE);
					child.findViewById(R.id.answered).setVisibility(View.VISIBLE);
				} else {
					FeatureCheckinQuestionMultipleChoice fcqmc = (FeatureCheckinQuestionMultipleChoice)featureCheckinQuestion;
					if (isUserLoggedIn()) {
						RadioGroup possibleAnswersView = (RadioGroup)child.findViewById(R.id.possible_answers_radio_group);
						for(FeatureCheckinQuestionPossibleAnswer possibleAnswer : fcqmc.getCheckinQuestionsPossibleAnswers()) {
							TextView possibleAnswerView = new RadioButton(FeatureCheckinQuestionsActivity.this);
							possibleAnswerView.setTextAppearance(FeatureCheckinQuestionsActivity.this, R.style.text_radio_button);
							possibleAnswerView.setText(possibleAnswer.getAnswer());
							possibleAnswerView.setTag(possibleAnswer);
							possibleAnswersView.addView(possibleAnswerView);						
						}
					} else {
						LinearLayout possibleAnswersView = (LinearLayout)child.findViewById(R.id.possible_answers_container);
						for(FeatureCheckinQuestionPossibleAnswer possibleAnswer : fcqmc.getCheckinQuestionsPossibleAnswers()) {
							View possibleAnswerView = (View)layoutInflater.inflate(R.layout.feature_checkin_question_multiplechoice_question_row_possible_answer_loggedout,null);
							TextView tv = (TextView)possibleAnswerView.findViewById(R.id.possible_answer);
							tv.setText(possibleAnswer.getAnswer());
							possibleAnswersView.addView(possibleAnswerView);
						}					
					}
				}
			}
			
			
			TextView tv = (TextView)child.findViewById(R.id.question);
			tv.setText(featureCheckinQuestion.getQuestion());
			
			parent.addView(child);		
			
			childViews.put(Integer.valueOf(featureCheckinQuestion.getId()), child);
		}

	}
	
	private class CheckFreeTextAnswerTask extends AsyncTask<Boolean, Void, Boolean> {
		protected FeatureCheckinQuestionFreeText featureCheckinQuestion;
		protected String answer;
		protected SubmitFeatureCheckinQuestionFreeTextAnswerCall call;
		
		public CheckFreeTextAnswerTask(FeatureCheckinQuestionFreeText featureCheckinQuestion, String answer) {
			super();
			this.featureCheckinQuestion = featureCheckinQuestion;
			this.answer = answer;
		}

		protected Boolean doInBackground(Boolean... dummy) {
			try{
				call = new SubmitFeatureCheckinQuestionFreeTextAnswerCall(FeatureCheckinQuestionsActivity.this);
				call.execute(featureCheckinQuestion, answer);
			} catch(Exception e) {				
				Log.d("ERRORINLOGIN",e.toString());
				if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			mDialog.dismiss();
			if (call.hasErrorMessage()) {
				Toast.makeText(getApplicationContext(), "Sorry, an error occured! "+call.getErrorMessage(), Toast.LENGTH_LONG).show();
			} else if (call.getResult()) {
				Toast.makeText(getApplicationContext(), getString(R.string.checkin_result_correct), Toast.LENGTH_LONG).show();
				// load new user data to get new score and other data 
				FeatureCheckinQuestionsActivity.this.startService(new Intent(FeatureCheckinQuestionsActivity.this, LoadUserDataService.class));
				// change screen underneath
				View child = (View)childViews.get(Integer.valueOf(featureCheckinQuestion.getId()));
				child.findViewById(R.id.answer).setVisibility(View.INVISIBLE);
				child.findViewById(R.id.answered).setVisibility(View.VISIBLE);
				if (featureCheckinQuestion.hasExplanationHTML()) {
					View ae = child.findViewById(R.id.answer_explanation);
					ae.setVisibility(View.VISIBLE);
					ae.setTag(featureCheckinQuestion);
				}
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.checkin_result_wrong), Toast.LENGTH_LONG).show();
			}
		}
	}
	
	
	private class CheckHigherOrLowerAnswerTask extends AsyncTask<Boolean, Void, Boolean> {
		protected FeatureCheckinQuestionHigherOrLower featureCheckinQuestion;
		protected String answer;
		protected SubmitFeatureCheckinQuestionHigherOrLowerAnswerCall call;
		
		public CheckHigherOrLowerAnswerTask(FeatureCheckinQuestionHigherOrLower featureCheckinQuestion, String answer) {
			super();
			this.featureCheckinQuestion = featureCheckinQuestion;
			this.answer = answer;
		}

		protected Boolean doInBackground(Boolean... dummy) {
			try{
				call = new SubmitFeatureCheckinQuestionHigherOrLowerAnswerCall(FeatureCheckinQuestionsActivity.this);
				call.execute(featureCheckinQuestion, answer);
			} catch(Exception e) {
				Log.d("ERRORINLOGIN",e.toString());
				if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			mDialog.dismiss();
			if (call.hasErrorMessage()) {
				Toast.makeText(getApplicationContext(), "Sorry, an error occured! "+call.getErrorMessage(), Toast.LENGTH_LONG).show();
			} else if (call.getResult()) {
				Toast.makeText(getApplicationContext(), getString(R.string.checkin_result_correct), Toast.LENGTH_LONG).show();
				// load new user data to get new score and other data 
				FeatureCheckinQuestionsActivity.this.startService(new Intent(FeatureCheckinQuestionsActivity.this, LoadUserDataService.class));
				// change screen underneath
				View child = (View)childViews.get(Integer.valueOf(featureCheckinQuestion.getId()));
				child.findViewById(R.id.answer).setVisibility(View.INVISIBLE);
				child.findViewById(R.id.answered).setVisibility(View.VISIBLE);	
				if (featureCheckinQuestion.hasExplanationHTML()) {
					View ae = child.findViewById(R.id.answer_explanation);
					ae.setVisibility(View.VISIBLE);
					ae.setTag(featureCheckinQuestion);
				}				
			} else {
				if (call.getTrueAnswerCode() == 1) {
					Toast.makeText(getApplicationContext(), getString(R.string.checkin_result_wrong_higherorlower_tohigh), Toast.LENGTH_LONG).show();
				} else if (call.getTrueAnswerCode() == -1) {
					Toast.makeText(getApplicationContext(), getString(R.string.checkin_result_wrong_higherorlower_tolow), Toast.LENGTH_LONG).show();
				} 
			}
		}
	}	
	

	private class CheckMultipleChoiceAnswerTask extends AsyncTask<Boolean, Void, Boolean> {
		protected FeatureCheckinQuestionMultipleChoice featureCheckinQuestion;
		protected FeatureCheckinQuestionPossibleAnswer answer;
		protected SubmitFeatureCheckinQuestionMultipleChoiceAnswerCall call;
		
		public CheckMultipleChoiceAnswerTask(FeatureCheckinQuestionMultipleChoice featureCheckinQuestion, FeatureCheckinQuestionPossibleAnswer answer) {
			super();
			this.featureCheckinQuestion = featureCheckinQuestion;
			this.answer = answer;
		}

		protected Boolean doInBackground(Boolean... dummy) {
			try{
				call = new SubmitFeatureCheckinQuestionMultipleChoiceAnswerCall(FeatureCheckinQuestionsActivity.this);
				call.execute(featureCheckinQuestion, answer);
			} catch(Exception e) {
				Log.d("ERRORINLOGIN",e.toString());
				if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			mDialog.dismiss();
			if (call.hasErrorMessage()) {
				Toast.makeText(getApplicationContext(), "Sorry, an error occured! "+call.getErrorMessage(), Toast.LENGTH_LONG).show();
			} else if (call.getResult()) {
				Toast.makeText(getApplicationContext(), getString(R.string.checkin_result_correct), Toast.LENGTH_LONG).show();
				// load new user data to get new score and other data 
				FeatureCheckinQuestionsActivity.this.startService(new Intent(FeatureCheckinQuestionsActivity.this, LoadUserDataService.class));
				// change screen underneath
				View child = (View)childViews.get(Integer.valueOf(featureCheckinQuestion.getId()));
				child.findViewById(R.id.answer).setVisibility(View.INVISIBLE);
				child.findViewById(R.id.possible_answers_radio_group).setVisibility(View.INVISIBLE);
				child.findViewById(R.id.answered).setVisibility(View.VISIBLE);
				if (featureCheckinQuestion.hasExplanationHTML()) {
					View ae = child.findViewById(R.id.answer_explanation);
					ae.setVisibility(View.VISIBLE);
					ae.setTag(featureCheckinQuestion);
				}
			} else {
				Toast.makeText(getApplicationContext(), getString(R.string.checkin_result_wrong), Toast.LENGTH_LONG).show();
			}
		}
	}	
}
