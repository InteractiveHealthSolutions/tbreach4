/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * 
 */

package com.ihsinformatics.tbr4mobile;



import com.ihsinformatics.tbr4mobile.shared.AlertType;
import com.ihsinformatics.tbr4mobile.util.ServerService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class LoginActivity extends Activity implements IActivity, OnClickListener
{
	private ServerService			serverService;
	protected static ProgressDialog	loading;
	EditText						username;
	EditText						password;
	Button							login;
	CheckBox						offline;
	TextView						showPassword;
	View[]							views;
	Animation						alphaAnimation;
	
	String                          tempUsername;
	String							tempPassword;
	
	String 							screenerLocationSetup[] = null;

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		setTheme (App.getTheme ());
		setContentView (R.layout.login);
		serverService = new ServerService (getApplicationContext ());
		loading = new ProgressDialog (this);
		username = (EditText) findViewById (R.login_id.usernameEditText);
		password = (EditText) findViewById (R.login_id.passwordEditText);
		login = (Button) findViewById (R.login_id.loginButton);
		offline = (CheckBox) findViewById (R.login_id.offlineCheckBox);
		showPassword = (TextView) findViewById (R.login_id.showPasswordTextView);
		alphaAnimation = AnimationUtils.loadAnimation (this, R.anim.alpha_animation);
		
		login.setOnClickListener (this);
		showPassword.setOnClickListener (this);
		views = new View[] {username, password, login};
		super.onCreate (savedInstanceState);
		//password.setInputType(InputType.TYPE_CLASS_TEXT);;
		initView (views);
	}

	@Override
	public void initView (View[] views)
	{
		
		Boolean status = serverService.renewLoginStatus(); // Check if it's first login of a day.
		
		if(!status){  // if not
			if (App.isAutoLogin ())  // if app on auto login mode, bypass login page to Main Menu
			{
				serverService.setCurrentUser (App.get (username));
				Intent intent = new Intent (this, MainMenuActivity.class);
				startActivity (intent);
				finish ();
			}
		}
		username.setText (App.getUsername ());
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		switch (item.getItemId ())
		{
			case R.menu_id.itemPreferences :
				startActivity (new Intent (this, Preferences.class));
				break;
		}
		return true;
	}

	@Override
	protected void onStop ()
	{
		super.onStop ();
		finish ();
	}

	@Override
	public void updateDisplay ()
	{
	}

	@Override
	public boolean validate ()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer ();
		// Validate mandatory controls
		if (App.get (username).equals (""))
		{
			valid = false;
			message.append (username.getTag () + ". ");
			((EditText) username).setHintTextColor (getResources ().getColor (R.color.Red));
		}
		if (App.get (password).equals (""))
		{
			valid = false;
			message.append (password.getTag () + ". ");
			((EditText) password).setHintTextColor (getResources ().getColor (R.color.Red));
		}
		if (!valid)
		{
			message.append (getResources ().getString (R.string.empty_data) + "\n");
		}
		// For offline mode, match current username and password with saved
		if (offline.isChecked ())
		{
			if (!App.get (username).equals (App.getUsername ()))
			{
				if (!App.get (password).equals (App.getPassword ()))
				{
					valid = false;
					message.append (getResources ().getString (R.string.authentication_error));
				}
			}
		}
		if (!valid)
		{
			App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();
		}
		return valid;
	}

	@Override
	public boolean submit ()
	{
		// Check connection with server
		if (!serverService.checkInternetConnection () && !offline.isChecked ())
		{
			AlertDialog alertDialog = App.getAlertDialog (this, AlertType.ERROR, getResources ().getString (R.string.data_connection_error));
			alertDialog.setTitle (getResources ().getString (R.string.error_title));
			alertDialog.setButton (AlertDialog.BUTTON_POSITIVE, "OK", new AlertDialog.OnClickListener ()
			{
				@Override
				public void onClick (DialogInterface dialog, int which)
				{
					finish ();
				}
			});
			alertDialog.show ();
		}
		else if (validate ())
		{
			// Authenticate from server
			AsyncTask<String, String, String> authenticationTask = new AsyncTask<String, String, String> ()
			{
				@Override
				protected String doInBackground (String... params)
				{
					runOnUiThread (new Runnable ()
					{
						@Override
						public void run ()
						{
							loading.setIndeterminate (true);
							loading.setCancelable (false);
							loading.setMessage (getResources ().getString (R.string.loading_message));
							loading.show ();
						}
					});
					if (offline.isChecked ()) // if offline mode match credentials with preferences
					{
						if (App.getUsername ().equalsIgnoreCase (App.get (username)) && App.getPassword ().equals (App.get (password)))
						{
							return "SUCCESS";
						}
						return "FAIL";
					}
					
					tempUsername = App.getUsername();
					tempPassword = App.getPassword();
					
					App.setUsername (App.get (username));
					App.setPassword (App.get (password));
					
					String exists = serverService.authenticate (App.get (username));
					return exists;
					//return "SUCCESS";
				}

				@Override
				protected void onProgressUpdate (String... values)
				{
				};

				@Override
				protected void onPostExecute (String result)
				{
					super.onPostExecute (result); // Sample result value "Success:Rabbia"
					loading.dismiss ();
					
					String resultsPart[] = result.split(":");
					
					// first part show status of request
					if (resultsPart[0].equals("SUCCESS"))  // if success 
					{
						serverService.setCurrentUser (App.get (username));  // retrive username
						
						if(!App.isOfflineMode())
							App.setScreenerName(resultsPart[1]);
						else
							App.setScreenerName(App.getScreenerName());
						
						serverService.updateLoginTime();
						
						App.setUsername (App.get (username));
						App.setPassword (App.get (password));
						
						
						/*
						ContentValues values = new ContentValues();
						values.put ("name", newTimeStamp);
						
						dbUtil.update(Metadata.METADATA_TABLE, values, "type='"+Metadata.TIME_STAMP+"' and id='"+App.getUsername()+"'", null);*/
						
						
						// Save username and password in preferences
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (LoginActivity.this);
						SharedPreferences.Editor editor = preferences.edit ();
						editor.putString (Preferences.USERNAME, App.getUsername ());
						editor.putString (Preferences.PASSWORD, App.getPassword ());
						editor.putString (Preferences.SCREENER_NAME, App.getScreenerName());
						editor.apply ();
						
						//open next activity
						Intent intent = new Intent (LoginActivity.this, MainMenuActivity.class);
						intent.putExtra("new_login", "yes");
						startActivity (intent);
						finish ();
					}
					else if(result.equals("FAIL")) // if fails
					{
						/*App.setUsername ("");
						App.setPassword ("");*/
						
						App.setUsername (tempUsername);
						App.setPassword (tempPassword);
						
						Toast toast = Toast.makeText (LoginActivity.this, getResources ().getString (R.string.authentication_error), App.getDelay ());
						toast.setGravity (Gravity.CENTER, 0, 0);
						toast.show ();
					}
					else if(result.equals("CONNECTION_ERROR")) 
					{
						/*App.setUsername ("");
						App.setPassword ("");*/
						
						App.setUsername (tempUsername);
						App.setPassword (tempPassword);
						
						Toast toast = Toast.makeText (LoginActivity.this, getResources ().getString (R.string.data_connection_error), App.getDelay ());
						toast.setGravity (Gravity.CENTER, 0, 0);
						toast.show ();
					}
					else {
						
						/*App.setUsername ("");
						App.setPassword ("");*/
						
						App.setUsername (tempUsername);
						App.setPassword (tempPassword);
						
						Toast toast = Toast.makeText (LoginActivity.this, result, App.getDelay ());
						toast.setGravity (Gravity.CENTER, 0, 0);
						toast.show ();
						
					}
				}
			};
			authenticationTask.execute ("");
		}
		return false;
	}

	@Override
	public void onClick (View view)
	{
		Intent browserIntent;
		view.startAnimation (alphaAnimation);
		switch (view.getId ())
		{
			case R.login_id.loginButton :
				App.setOfflineMode (offline.isChecked ());
				submit ();
				break;
			case R.login_id.showPasswordTextView :
				String status = showPassword.getText().toString();
				if(status.equals("Show Password"))
				{
					password.setInputType( InputType.TYPE_TEXT_VARIATION_PASSWORD);
					showPassword.setText("Hide Password");
				}
				else{
					password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					showPassword.setText("Show Password");
				}
				
				int position = password.getText().length(); 
				password.setSelection(position);
				break;
		}
	}
	
	
}
