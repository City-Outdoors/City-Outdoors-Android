package uk.co.jarofgreen.cityoutdoors.UI;



import uk.co.jarofgreen.cityoutdoors.API.LogInOrSignUpCall;
import uk.co.jarofgreen.cityoutdoors.Service.LoadUserDataService;
import uk.co.jarofgreen.cityoutdoors.Service.SendFeatureFavouriteService;
import uk.co.jarofgreen.cityoutdoors.OurApplication;
import uk.co.jarofgreen.cityoutdoors.R;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author James Baster  <james@jarofgreen.co.uk>
 * @copyright City of Edinburgh Council & James Baster
 * @license Open Source under the 3-clause BSD License
 * @url https://github.com/City-Outdoors/City-Outdoors-Android
 */
public class LogInOrSignUpActivity extends BaseActivity {
	
	 ProgressDialog mDialog;
	 LogInTask logInTask;
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_or_signup);  
    }
    
    
    public void onClickLogIn(View v) {
    	logInTask = new LogInTask();
    	
    	mDialog = new ProgressDialog(this);
    	mDialog.setMessage(getString(R.string.login_checking_data_with_server_wait));
    	mDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface arg0) {
            	logInTask.cancel(true);
            }
        });
    	mDialog.setCancelable(true);  	
    	mDialog.show();

    	logInTask.setEmail(((EditText)findViewById(R.id.email)).getText().toString());
    	logInTask.setPassword(((EditText) findViewById(R.id.password)).getText().toString());
    	logInTask.execute(true);   	
    }
    
    public void onClickLogInTwitter(View v) {
    	Intent i = new Intent(this, LogInTwitterActivity.class);
    	startActivity(i);  	
    }

    public void onClickForgotPassword(View v) {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.server_url)+"/forgottenPassword.php"));
        startActivity(browserIntent);
    }
         
    private class LogInTask extends AsyncTask<Boolean, Void, Boolean> {
 
		protected String Email;
    	protected String Password;
    	
    	protected LogInOrSignUpCall logInOrSignUpCall;
    	
        protected Boolean doInBackground(Boolean... dummy) {
        	
            try{
            	logInOrSignUpCall = new LogInOrSignUpCall(LogInOrSignUpActivity.this, (OurApplication)getApplication());
            	return logInOrSignUpCall.execute(Email, Password);
            } catch(Exception e) {
            	Log.d("ERRORINLOGIN",e.toString());
            	if (e.getMessage() != null) Log.d("ERRORINLOGIN",e.getMessage());
            }
        	
            return false;
        	
        }

        protected void onPostExecute(Boolean result) {
        	LogInOrSignUpActivity.this.mDialog.dismiss();

        	if (result) {
        		logInOrSignUpCall.saveResults();
        		// send data to and get data from server in background
        		LogInOrSignUpActivity.this.startService(new Intent(LogInOrSignUpActivity.this, SendFeatureFavouriteService.class));
        		LogInOrSignUpActivity.this.startService(new Intent(LogInOrSignUpActivity.this, LoadUserDataService.class));
        		// start main screen
        		Intent i = new Intent(LogInOrSignUpActivity.this, MainActivity.class);
        		LogInOrSignUpActivity.this.startActivity(i);
        		// kill login screen
        		LogInOrSignUpActivity.this.finish();
        	} else {
        		Toast.makeText(LogInOrSignUpActivity.this, LogInOrSignUpActivity.this.getString(R.string.login_or_signup_result_fail)+" "+logInOrSignUpCall.getErrorMessage(), Toast.LENGTH_LONG).show();
        	}

        }
                
    	public void setEmail(String email) {
			Email = email;
		}

		public void setPassword(String password) {
			Password = password;
		}
    }
    
    
    
}