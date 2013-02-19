package uk.co.jarofgreen.cityoutdoors.UI;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.Storage;
import uk.co.jarofgreen.cityoutdoors.API.FeatureCall;
import uk.co.jarofgreen.cityoutdoors.API.SubmitFeatureCheckinQuestionFreeTextAnswerCall;
import uk.co.jarofgreen.cityoutdoors.API.SubmitFeatureCheckinQuestionHigherOrLowerAnswerCall;
import uk.co.jarofgreen.cityoutdoors.API.SubmitFeatureCheckinQuestionMultipleChoiceAnswerCall;
import uk.co.jarofgreen.cityoutdoors.Model.Content;
import uk.co.jarofgreen.cityoutdoors.Model.Feature;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class FeatureActivity extends BaseActivity {

	int featureID;
	ProgressDialog mDialog;
	FeatureTask featureTask;
	DownloadImagesTask downloadImagesTask;
	String shareURL;
	
	String email;
	String telephone;
	
	HashMap childViews = new LinkedHashMap<Integer, View>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feature);  
		TitleBar.populate(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			featureID = extras.getInt("featureID");
			Log.d("FEATURE from INTENT",Integer.toString(featureID));
		}
				
		featureTask = new FeatureTask(featureID);
		
		mDialog = new ProgressDialog(this);
		mDialog.setMessage(getString(R.string.loading_data_from_server_wait));
    	mDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
            	featureTask.cancel(true);
            	FeatureActivity.this.finish();
            }
        });
    	mDialog.setCancelable(true);
    	mDialog.show();

		featureTask.execute(true);

	}


	public void onClickFavourite(View v) {
		Storage s = new Storage(this);
		s.featureFavourite(featureID, false);
		startService(new Intent(this, SendFeatureFavouriteService.class));
		Toast.makeText(this, getString(R.string.feature_add_to_favourites_result_success), Toast.LENGTH_SHORT).show();
	}
	
	public void onClickShare(View v) {
		Intent intent=new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

		String shareTxt = shareURL;
		intent.putExtra(Intent.EXTRA_SUBJECT, shareTxt);
		intent.putExtra(Intent.EXTRA_TEXT, shareTxt);
		
		startActivity(Intent.createChooser(intent, getString(R.string.feature_share_chooser_title)));
	}	
	
	public void onClickAction(View v) {
		openOptionsMenu();
	}
	
	public void onClickNewFeatureContent(View v) {
		Intent i = new Intent(this, NewFeatureContentActivity.class);
		i.putExtra("featureID", featureID);
		startActivity(i);
	}
	

	public void onClickNewFeatureReport(View v) {
		Intent i = new Intent(this, NewFeatureReportActivity.class);
		i.putExtra("featureID", featureID);
		startActivity(i);
	}
	
	public void onClickFeatureContentPicture(View v) {
		Integer idx = (Integer)v.getTag();
		Intent i = new Intent(this, ViewImageActivity.class);
		i.putExtra("featureContentID", idx);
		i.putExtra("featureID", featureID);
		startActivity(i);
	}
	
	public void onClickMap(View v) {
		Storage s = new Storage(this);
		Feature f = s.getFeature(featureID);
		Log.d("CLICK",Integer.toString(featureID));
		if (f != null) {
			Intent i = new Intent(this, BrowseMapActivity.class);
			i.putExtra("lat", f.getLat());
			i.putExtra("lng", f.getLng());
			startActivity(i);
		}
	}
	

	public void onClickFieldEmail(View v) {
		String email = (String)v.getTag();
		startIntentForEmailAddress(email);
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
	
	protected void startIntentForEmailAddress(String email) {
		Intent intent = new Intent(Intent.ACTION_SEND); 
		intent.setType("plain/text");
		String emailAddressList[] = { email };
		intent.putExtra(Intent.EXTRA_EMAIL, emailAddressList);  
		//intent.putExtra(Intent.EXTRA_SUBJECT, ''); 
		
		try {
			startActivity(Intent.createChooser(intent, getString(R.string.feature_email_chooser_title)));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(this,getString(R.string.feature_no_email_clients_error),Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onClickFieldPhone(View v) {
		final String phone = ((String)v.getTag()).trim();
		startIntentForPhoneNumber(phone);
	}

	protected void startIntentForPhoneNumber(String phone) {
		String uri = "tel:" + phone;
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse(uri));
		startActivity(intent);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feature_menu, menu);
        return true;
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
    	super.onPrepareOptionsMenu(menu);
       	if (email == null) {
       		menu.findItem(R.id.email).setVisible(false);
       	}
       	if (telephone == null) {
       		menu.findItem(R.id.phone).setVisible(false);
       	}       	
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.favourite:
        	onClickFavourite(null);
            return true;
        case R.id.comment:
        	onClickNewFeatureContent(null);
            return true;
        case R.id.report:
        	onClickNewFeatureReport(null);
            return true;
        case R.id.map:
        	onClickMap(null);
            return true;
        case R.id.share:
        	onClickShare(null);
            return true;                 
        case R.id.phone:
        	startIntentForPhoneNumber(telephone);
        	return true;
        case R.id.email:
        	startIntentForEmailAddress(email);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	

	private class FeatureTask extends AsyncTask<Boolean, Void, Boolean> {

		protected int featureID;
		protected FeatureCall call;

		public FeatureTask(int featureID) {
			super();
			this.featureID = featureID;
		}

		protected Boolean doInBackground(Boolean... dummy) {

			try{

				call = new FeatureCall(FeatureActivity.this);
				call.execute(featureID);

			} catch(Exception e) {
				Log.d("ERRORINLOGIN",e.toString());
				if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
			}

			return false;

		}

		protected void onPostExecute(Boolean result) {
			FeatureActivity.this.mDialog.dismiss();

			FeatureActivity.this.shareURL = call.getFeature().getShareURL();

			TextView tvTitle = (TextView)FeatureActivity.this.findViewById(R.id.featureTitle);
			tvTitle.setText(call.getFeature().getTitle());
			
			LinearLayout parent = (LinearLayout)findViewById(R.id.content_container);

			OurApplication appState = ((OurApplication)FeatureActivity.this.getApplication());
			appState.setFeatureContent(call.getFeature(), call.getContent());
			
			List<ImageToDownload> imagesToDownload = new ArrayList<FeatureActivity.ImageToDownload>();
			
			// items
			for (Item item: call.getItems()) {
				if (!item.isDeleted()) {
					addItemToLayout(item, parent);
				}
			}			

			// questions
			boolean hasQuestions = false;
			for (FeatureCheckinQuestion featureCheckinQuestion : call.getCheckinQuestions()) {
				if (featureCheckinQuestion.isActive() && !featureCheckinQuestion.isDeleted()) {
					addQuestionToLayout(featureCheckinQuestion, parent);
					hasQuestions = true;
				}
			}
			
			if (hasQuestions && !FeatureActivity.this.isUserLoggedIn()) {
				LayoutInflater layoutInflater = getLayoutInflater();
				View child = layoutInflater.inflate(R.layout.feature_login_to_checkin,null);
				parent.addView(child);
			}
			
			// content
			for (Content content : call.getContent()) {
				addContentToLayout(content, parent, imagesToDownload);
			}
			
			// finally start images downloading
			downloadImagesTask = new DownloadImagesTask(imagesToDownload);
			downloadImagesTask.execute(true);			
		}
		
		protected void addItemToLayout(Item item, LinearLayout parent) {
			LayoutInflater layoutInflater = getLayoutInflater();
			
			LinearLayout child = (LinearLayout) layoutInflater.inflate(R.layout.item_on_feature,null);
			for (ItemField itemField: item.getFields()) {
				
				if (itemField.isHasValue()) {
				
					LinearLayout fieldView;
											
					if (itemField.isTypePhone()) {
						fieldView = (LinearLayout) layoutInflater.inflate(R.layout.item_field_phone,null);
						TextView tv3 = (TextView)fieldView.findViewById(R.id.value);
						tv3.setText(itemField.getValueText());
						tv3.setTag(itemField.getValueText());
						telephone = itemField.getValueText();
					} else if (itemField.isTypeEmail()) {
						fieldView = (LinearLayout) layoutInflater.inflate(R.layout.item_field_email,null);
						TextView tv3 = (TextView)fieldView.findViewById(R.id.value);
						tv3.setText(itemField.getValueText());
						tv3.setTag(itemField.getValueText());
						email = itemField.getValueText();
						tv3.setPaintFlags(tv3.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);							
					} else if (itemField.isTypeHTML()) {
						fieldView = (LinearLayout) layoutInflater.inflate(R.layout.item_field_html,null);
						WebView tv3 = (WebView)fieldView.findViewById(R.id.value);
						tv3.loadData(itemField.getValueHTML(), "text/html", "utf-8");							
					} else {
						fieldView = (LinearLayout) layoutInflater.inflate(R.layout.item_field,null);
						TextView tv3 = (TextView)fieldView.findViewById(R.id.value);
						tv3.setText(itemField.getValueText());
					}						

					TextView tv2 = (TextView)fieldView.findViewById(R.id.title);
					tv2.setText(itemField.getTitle());			
					
					child.addView(fieldView);
				}
				
			}
			
			parent.addView(child);
		}
		
		protected void addContentToLayout(Content content, LinearLayout parent, List<ImageToDownload> imagesToDownload) {
			View child;
			LayoutInflater layoutInflater = getLayoutInflater();
			
			if (content.isHasPicture()) {
				child = layoutInflater.inflate(R.layout.feature_content_with_picture_row,null);
				
				ImageView imageView = (ImageView)child.findViewById(R.id.picture);
				
				imagesToDownload.add(new ImageToDownload(imageView, content.getPictureThumbURL()));
				
				imageView.setTag(content.getId());

				imageView.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						FeatureActivity.this.onClickFeatureContentPicture(v);
					}
				});
				
			} else {
				child = layoutInflater.inflate(R.layout.feature_content_row,null);
			}
			
			TextView tv = (TextView)child.findViewById(R.id.body);
			tv.setText(content.getBody());

			TextView tvdetails = (TextView)child.findViewById(R.id.details);
			tvdetails.setText(getString(R.string.feature_content_posted_by) + " " + content.getDisplayName());
			
			parent.addView(child);
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
							TextView possibleAnswerView = new RadioButton(FeatureActivity.this);
							possibleAnswerView.setTextAppearance(FeatureActivity.this, R.style.text_radio_button);
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

	private class ImageToDownload {
		protected ImageView imageView;
		protected String imageURL;
		protected boolean placed;
		protected Drawable drawable;
		public ImageToDownload(ImageView imageView, String imageURL) {
			super();
			this.imageView = imageView;
			this.imageURL = imageURL;
			placed = false;
		}
		public String getImageURL() {
			return imageURL;
		}
		public void setDrawable(Drawable drawable) {
			this.drawable = drawable;
		}
		public void processIfPossible() {
			if (drawable != null && !placed) {
				imageView.setImageDrawable(drawable);
				placed = true;
				// we can nullify all pointers and let any memory GC happen asap!
				imageView = null;
				drawable = null;
			}
		}
		
	}
	
	private class DownloadImagesTask extends AsyncTask<Boolean, Void, Boolean> {

		List<ImageToDownload> imagesToDownload;
		

		public DownloadImagesTask(List<ImageToDownload> imagesToDownload) {
			super();
			this.imagesToDownload = imagesToDownload;
		}

		protected Boolean doInBackground(Boolean... dummy) {
			for (ImageToDownload imageToDownload : imagesToDownload) {
				if (!isCancelled()) { 
					try {
						Log.d("DOWNLOAD",imageToDownload.getImageURL());
						URL url = new URL(imageToDownload.getImageURL());
						InputStream is = (InputStream)  url.getContent();
						Drawable d = Drawable.createFromStream(is, "src");
						imageToDownload.setDrawable(d);
					} catch (Exception e) {
						if (e.getMessage() != null) Log.d("ERRORDOWNLOAD",e.getMessage());
						return null;
					}
				}
			}
			return null;
		}

		protected void onProgressUpdate(Void... values) {
			placeLoadedImagesOnActivity();
		}

		protected void onPostExecute(Boolean result) {
			placeLoadedImagesOnActivity();
		}

		protected void placeLoadedImagesOnActivity() {
			for (ImageToDownload imageToDownload : imagesToDownload) {
				imageToDownload.processIfPossible();
			}
		}
		
	}

	@Override
	public void finish() {
		if (downloadImagesTask != null) downloadImagesTask.cancel(true);
		super.finish();
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
				call = new SubmitFeatureCheckinQuestionFreeTextAnswerCall(FeatureActivity.this);
				call.execute(featureCheckinQuestion, answer);
			} catch(Exception e) {				
				Log.d("ERRORINLOGIN",e.toString());
				if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			mDialog.dismiss();
			if (call.hasError()) {
				Toast.makeText(getApplicationContext(), "Sorry, an error occured! "+call.getErrorMessage(), Toast.LENGTH_LONG).show();
			} else if (call.getResult()) {
				Toast.makeText(getApplicationContext(), getString(R.string.checkin_result_correct), Toast.LENGTH_LONG).show();
				// load new user data to get new score and other data 
				FeatureActivity.this.startService(new Intent(FeatureActivity.this, LoadUserDataService.class));
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
				call = new SubmitFeatureCheckinQuestionHigherOrLowerAnswerCall(FeatureActivity.this);
				call.execute(featureCheckinQuestion, answer);
			} catch(Exception e) {
				Log.d("ERRORINLOGIN",e.toString());
				if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			mDialog.dismiss();
			if (call.hasError()) {
				Toast.makeText(getApplicationContext(), "Sorry, an error occured! "+call.getErrorMessage(), Toast.LENGTH_LONG).show();
			} else if (call.getResult()) {
				Toast.makeText(getApplicationContext(), getString(R.string.checkin_result_correct), Toast.LENGTH_LONG).show();
				// load new user data to get new score and other data 
				FeatureActivity.this.startService(new Intent(FeatureActivity.this, LoadUserDataService.class));
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
				call = new SubmitFeatureCheckinQuestionMultipleChoiceAnswerCall(FeatureActivity.this);
				call.execute(featureCheckinQuestion, answer);
			} catch(Exception e) {
				Log.d("ERRORINLOGIN",e.toString());
				if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
			}
			return false;
		}

		protected void onPostExecute(Boolean result) {
			mDialog.dismiss();
			if (call.hasError()) {
				Toast.makeText(getApplicationContext(), "Sorry, an error occured! "+call.getErrorMessage(), Toast.LENGTH_LONG).show();
			} else if (call.getResult()) {
				Toast.makeText(getApplicationContext(), getString(R.string.checkin_result_correct), Toast.LENGTH_LONG).show();
				// load new user data to get new score and other data 
				FeatureActivity.this.startService(new Intent(FeatureActivity.this, LoadUserDataService.class));
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


