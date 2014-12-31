package com.ihsinformatics.tbr4mobile;


import com.ihsinformatics.tbr4mobile.custom.MySpinner;
import com.ihsinformatics.tbr4mobile.shared.AlertType;
import com.ihsinformatics.tbr4mobile.util.ServerService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.Spinner;

public class LocationSetupActivity extends Activity implements IActivity, OnClickListener, OnCheckedChangeListener, OnItemSelectedListener
{

	private static final String		TAG					= "LocationSetupActivity";
	private ServerService			serverService;
	protected static ProgressDialog	loading;
	RadioButton						communityRadioButton;
	RadioButton						facilityRadioButton;
	TextView						districtTextView;
	Spinner							districtSpinner;
	ImageButton						districtSearchButton;
	TextView						screeningStrategyTextView;
	Spinner							screeningStrategySpinner;
	ImageButton						screeningStrategySearchButton;
	TextView						facilityTextView;
	Spinner							facilitySpinner;
	ImageButton						facilitySearchButton;
	TextView						locationIdTextView;
	
	//CheckBox						offline; 
	View[]							views;
	Animation						alphaAnimation;
	
	private static final int		DISTRICT_DIALOG	= 1;
	private static final int		FACILITY_DIALOG	= 2;
	private static final int		SCREENING_STRATEGY_DIALOG	= 3;
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		setTheme (App.getTheme ());
		setContentView (R.layout.location_setup);
		Configuration config = new Configuration ();
		config.locale = App.getCurrentLocale ();
		getApplicationContext ().getResources ().updateConfiguration (config, null);
		serverService = new ServerService (getApplicationContext ());
		loading = new ProgressDialog (this);
		
		communityRadioButton = (RadioButton) findViewById(R.location_id.communityRadio);
		facilityRadioButton = (RadioButton) findViewById(R.location_id.facilityRadio);
		districtTextView = (TextView) findViewById(R.location_id.districtTextView);
		districtSpinner = (Spinner) findViewById(R.location_id.districtSpinner);
		districtSearchButton = (ImageButton) findViewById(R.location_id.districtButton);
		screeningStrategyTextView = (TextView) findViewById(R.location_id.screeningStrategyTextView);
		screeningStrategySpinner = (Spinner) findViewById(R.location_id.screeningStrategySpinner);
		screeningStrategySearchButton = (ImageButton) findViewById(R.location_id.screeningStrategyButton);
		facilityTextView = (TextView) findViewById(R.location_id.facilityTextView);
		facilitySpinner = (Spinner) findViewById(R.location_id.facilitySpinner);
		facilitySearchButton = (ImageButton) findViewById(R.location_id.facilityButton);
		locationIdTextView = (TextView) findViewById(R.location_id.locationIdTextView);
		locationIdTextView.setTextColor(getResources().getColor(
				R.color.IRDTitle));
		
		alphaAnimation = AnimationUtils.loadAnimation (this, R.anim.alpha_animation);
		
		
		// Disable fetch button if offline
		if (App.isOfflineMode ())
		{
			districtSearchButton.setEnabled(false);
			screeningStrategySearchButton.setEnabled(false);
			facilitySearchButton.setEnabled(false);
		}
		
		facilitySpinner.setOnItemSelectedListener(this);
		districtSpinner.setOnItemSelectedListener(this);
		screeningStrategySpinner.setOnItemSelectedListener(this);
		communityRadioButton.setOnCheckedChangeListener(this);
		facilityRadioButton.setOnCheckedChangeListener(this);
		districtSearchButton.setOnClickListener (this);
		facilitySearchButton.setOnClickListener (this);
		screeningStrategySearchButton.setOnClickListener (this);
		views = new View[] {communityRadioButton, facilityRadioButton};
		super.onCreate (savedInstanceState);
		initView (views);
	}
	
	
	@Override
	public void onCheckedChanged(CompoundButton button, boolean state) {
		if(button == facilityRadioButton)
		{
			if(communityRadioButton.isChecked())
				App.setScreeningType("Community");
			if(facilityRadioButton.isChecked())
			{	App.setScreeningType("Facility");
			    App.setLocation("");
			    locationIdTextView.setText(App.getLocation());
			    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (getBaseContext());
			    SharedPreferences.Editor editor = preferences.edit ();
			    editor.putString(Preferences.SCREENING_TYPE, App.getScreeningType());
			    editor.putString(Preferences.LOCATION, App.getLocation());
			    editor.apply();
			}    
			
		}
		else if(button == communityRadioButton)
		{
			if(facilityRadioButton.isChecked())
				App.setScreeningType("Facility");
		    if(communityRadioButton.isChecked())
			{
				App.setScreeningType("Community");
				if(screeningStrategySpinner.getSelectedItemPosition () == -1)
				{
					// Try to fetch from local DB or Server
					AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String> ()
					{
						@Override
						protected String doInBackground (String... params)
						{
							try
							{
								if (!serverService.checkInternetConnection ())
								{
									AlertDialog alertDialog = App.getAlertDialog (LocationSetupActivity.this, AlertType.ERROR, getResources ().getString (R.string.data_connection_error));
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
								else
								{
									runOnUiThread (new Runnable ()
									{
										@Override
										public void run ()
										{
											loading.setIndeterminate (true);
											loading.setCancelable (false);
											loading.show ();
										}
									});
									// Update database
									publishProgress ("Searching...");
									serverService.getScreeningStrategies ();
								}
							}
							catch (Exception e)
							{
								Log.e (TAG, e.getMessage ());
							}
							return "SUCCESS";
						}
	
						@Override
						protected void onProgressUpdate (String... values)
						{
							loading.setMessage (values[0]);
						};
	
						@Override
						protected void onPostExecute (String result)
						{
							super.onPostExecute (result);
							if (!result.equals ("SUCCESS"))
							{
								App.getAlertDialog (LocationSetupActivity.this, AlertType.ERROR, getResources ().getString (R.string.item_not_found)).show ();
							}
							loading.dismiss ();
							initView (views);
						}
					};
					updateTask.execute ("");
				}
				
			}
			
			App.setLocation("");
			locationIdTextView.setText(App.getLocation());
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (getBaseContext());
			SharedPreferences.Editor editor = preferences.edit ();
			editor.putString(Preferences.SCREENING_TYPE, App.getScreeningType());
			editor.putString(Preferences.LOCATION, App.getLocation());
			editor.apply();
			
		}
		
		initView(views);
		
	}

	@Override
	public void onClick(View view) {
		view.startAnimation (alphaAnimation);
		if (view == districtSearchButton)
		{
			showDialog (DISTRICT_DIALOG);
		}
		else if (view == facilitySearchButton)
		{
			if(!App.getDistrict().equals(""))
			   showDialog (FACILITY_DIALOG);
			else
			{
				Toast toast = Toast.makeText (LocationSetupActivity.this, "", App.getDelay ());
				toast.setText (R.string.choose_district);
				toast.setGravity (Gravity.CENTER, 0, 0);
				toast.show ();
			}	
		}
		else if (view == screeningStrategySearchButton)
		{
			showDialog (SCREENING_STRATEGY_DIALOG);
		}
		else if (view == facilityRadioButton){
			if(facilityRadioButton.isChecked())
				App.setScreeningType("Facility");
			
			
		}else if(view == communityRadioButton){
			
				App.setScreeningType("Community");
		}
			
	}

	@Override
	public void initView(View[] views) {
	
		/*String[] screeningStrategiesList = serverService.getScreeningStrategiesFromLocal ();
		ArrayAdapter screeningStrategiesAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, screeningStrategiesList);
		screeningStrategySpinner.setAdapter (screeningStrategiesAdapter);
		
		String screeningStrategy = App.getScreeningStrategy();
		if(screeningStrategy != "")
		{
			ArrayAdapter screeningStrategyAdap = (ArrayAdapter) screeningStrategySpinner.getAdapter(); //cast to an ArrayAdapter
			int spinnerPosition = screeningStrategyAdap.getPosition(screeningStrategy);
			//set the default according to value
			screeningStrategySpinner.setSelection(spinnerPosition);
		}*/
		
		districtTextView.setEnabled(true);
		if (!App.isOfflineMode ())
			districtSearchButton.setEnabled(true);
		districtSpinner.setEnabled(true);
		
		String screeningType = App.getScreeningType();
		if(screeningType.equals("Facility"))
		{
			facilityRadioButton.setChecked(true);
			communityRadioButton.setChecked(false);
			
			screeningStrategyTextView.setEnabled(false);
			if (!App.isOfflineMode ())
				screeningStrategySearchButton.setEnabled(false);
			screeningStrategySpinner.setEnabled(false);
			facilityTextView.setEnabled(true);
			if (!App.isOfflineMode ())
				facilitySearchButton.setEnabled(true);
			facilitySpinner.setEnabled(true);
			
		}
		else if(screeningType.equals("Community"))
		{
			facilityRadioButton.setChecked(false);
			communityRadioButton.setChecked(true);
			
			screeningStrategyTextView.setEnabled(true);
			if (!App.isOfflineMode ())
				screeningStrategySearchButton.setEnabled(true);
			screeningStrategySpinner.setEnabled(true);
			facilityTextView.setEnabled(false);
			if (!App.isOfflineMode ())
				facilitySearchButton.setEnabled(false);
			facilitySpinner.setEnabled(false);
			
		}
		else 
		{
			facilityRadioButton.setChecked(false);
			communityRadioButton.setChecked(false);
			
			districtTextView.setEnabled(false);
			if (!App.isOfflineMode ())
				districtSearchButton.setEnabled(false);
			districtSpinner.setEnabled(false);
			screeningStrategyTextView.setEnabled(false);
			if (!App.isOfflineMode ())
				screeningStrategySearchButton.setEnabled(false);
			screeningStrategySpinner.setEnabled(false);
			facilityTextView.setEnabled(false);
			if (!App.isOfflineMode ())
				facilitySearchButton.setEnabled(false);
			facilitySpinner.setEnabled(false);
		}
		
		/*String[] districtList = serverService.getDistricts ();
		ArrayAdapter districtAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, districtList);
		districtSpinner.setAdapter (districtAdapter);
		
		String district = App.getDistrict();
		if(district != "")
		{
			ArrayAdapter districtAdap = (ArrayAdapter) districtSpinner.getAdapter(); //cast to an ArrayAdapter
			int spinnerPosition = districtAdap.getPosition(district);
			//set the default according to value
			districtSpinner.setSelection(spinnerPosition);
		}*/
		
		setSpinnerItemState();
		
	}

	private void setSpinnerItemState(){
	
		/*String selectedValue = districtSpinner.getSelectedItem().toString();					
		String prefix = selectedValue.substring(0,2);
		String[] facilitiesList = serverService.getFacilities (prefix);
		Boolean enabled = facilitySpinner.isEnabled();
		int drawable = enabled ? R.drawable.custom_spinner_item_disabled : R.drawable.custom_spinner_item_enabled;
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String> (this.getBaseContext (), drawable, facilitiesList);
		facilitySpinner.setAdapter (arrayAdapter);
		
		String facility = App.getFacility();
		if(facility != "")
		{
			ArrayAdapter facilityAdap = (ArrayAdapter) facilitySpinner.getAdapter(); //cast to an ArrayAdapter
			int spinnerPosition = facilityAdap.getPosition(facility);
			//set the default according to value
			facilitySpinner.setSelection(spinnerPosition);
		}*/
		
		String[] districtList = serverService.getDistricts ();
		Boolean enabled = districtSpinner.isEnabled();
		int drawable = enabled ? R.drawable.custom_spinner_item_enabled : R.drawable.custom_spinner_item_disabled;
		ArrayAdapter<String> districtAdapter = new ArrayAdapter<String> (this.getBaseContext (), drawable, districtList);
		districtSpinner.setAdapter (districtAdapter);
		
		String district = App.getDistrict();
		if(district != "")
		{
			ArrayAdapter districtAdap = (ArrayAdapter) districtSpinner.getAdapter(); //cast to an ArrayAdapter
			int spinnerPosition = districtAdap.getPosition(district);
			//set the default according to value
			districtSpinner.setSelection(spinnerPosition);
		}
		
		String[] screeningStrategiesList = serverService.getScreeningStrategiesFromLocal ();
		enabled = screeningStrategySpinner.isEnabled();
		drawable = enabled ? R.drawable.custom_spinner_item_enabled : R.drawable.custom_spinner_item_disabled;
        ArrayAdapter<String> screeningStrategiesAdapter = new ArrayAdapter<String> (this.getBaseContext (), drawable, screeningStrategiesList);
		screeningStrategySpinner.setAdapter (screeningStrategiesAdapter);
		
		String screeningStrategy = App.getScreeningStrategy();
		if(screeningStrategy != "")
		{
			ArrayAdapter screeningStrategyAdap = (ArrayAdapter) screeningStrategySpinner.getAdapter(); //cast to an ArrayAdapter
			int spinnerPosition = screeningStrategyAdap.getPosition(screeningStrategy);
			//set the default according to value
			screeningStrategySpinner.setSelection(spinnerPosition);
		}
		
	}
	
	
	@Override
	public void updateDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean submit() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onBackPressed ()
	{
		finish ();
		Intent mainMenuIntent = new Intent (getApplicationContext (), MainMenuActivity.class);
		startActivity (mainMenuIntent);
			
	}
	
	@Override
	protected Dialog onCreateDialog (int id)
	{
		Dialog dialog = super.onCreateDialog (id);
		AlertDialog.Builder builder = new AlertDialog.Builder (this);
		int maxLength;
		switch (id)
		{
		// Show a list of all locations to choose. This is to limit the
		// locations displayed on site spinner
			case DISTRICT_DIALOG :
				builder.setTitle (getResources ().getString (R.string.district_id));
				final EditText districtText = new EditText (this);
				districtText.setTag ("district");
				districtText.setHint (R.string.district_search_hint);
				maxLength = 2;    
				districtText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
				builder.setView (districtText);
				builder.setPositiveButton (R.string.save, new DialogInterface.OnClickListener ()
				{
					@Override
					public void onClick (DialogInterface dialogInterface, int i)
					{
						final String selected = App.get (districtText);
						if (selected.equals (""))
						{
							Toast toast = Toast.makeText (LocationSetupActivity.this, "", App.getDelay ());
							toast.setText (R.string.empty_data);
							toast.setGravity (Gravity.CENTER, 0, 0);
							toast.show ();
							return;
						}
						if (selected.length()!=2)
						{
							Toast toast = Toast.makeText (LocationSetupActivity.this, "", App.getDelay ());
							toast.setText (R.string.invalid_id);
							toast.setGravity (Gravity.CENTER, 0, 0);
							toast.show ();
							return;
						}
						// Try to fetch from local DB or Server
						AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String> ()
						{
							@Override
							protected String doInBackground (String... params)
							{
								try
								{
									if (!serverService.checkInternetConnection ())
									{
										AlertDialog alertDialog = App.getAlertDialog (LocationSetupActivity.this, AlertType.ERROR, getResources ().getString (R.string.data_connection_error));
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
									else
									{
										runOnUiThread (new Runnable ()
										{
											@Override
											public void run ()
											{
												loading.setIndeterminate (true);
												loading.setCancelable (false);
												loading.show ();
											}
										});
										// Update database
										publishProgress ("Searching...");
										String district = serverService.getDistrict (selected);
										if (district != null)
										{
											App.setDistrict (district);

											// Save district in preferences
											SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (getBaseContext());
											SharedPreferences.Editor editor = preferences.edit ();
											editor.putString(Preferences.DISTRICT, App.getDistrict ());
											editor.apply ();
											
										}
										else
										{
											return "FAIL";
										}
									}
								}
								catch (Exception e)
								{
									Log.e (TAG, e.getMessage ());
								}
								return "SUCCESS";
							}

							@Override
							protected void onProgressUpdate (String... values)
							{
								loading.setMessage (values[0]);
							};

							@Override
							protected void onPostExecute (String result)
							{
								super.onPostExecute (result);
								if (!result.equals ("SUCCESS"))
								{
									App.getAlertDialog (LocationSetupActivity.this, AlertType.ERROR, getResources ().getString (R.string.item_not_found)).show ();
								}
								loading.dismiss ();
								initView (views);
							}
						};
						updateTask.execute ("");
					}
				}); 
				builder.setNegativeButton (R.string.cancel, null);
				dialog = builder.create ();
				break;
				
				
			case FACILITY_DIALOG :
				builder.setTitle (getResources ().getString (R.string.facility_id));
				final EditText facilityText = new EditText (this);
				facilityText.setTag ("facility");
				facilityText.setHint (R.string.facility_search_hint);
				maxLength = 3;    
				facilityText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
				builder.setView (facilityText);
				builder.setPositiveButton (R.string.save, new DialogInterface.OnClickListener ()
				{
					@Override
					public void onClick (DialogInterface dialogInterface, int i)
					{
						final String selected = App.get (facilityText);
						if (selected.equals (""))
						{
							Toast toast = Toast.makeText (LocationSetupActivity.this, "", App.getDelay ());
							toast.setText (R.string.empty_data);
							toast.setGravity (Gravity.CENTER, 0, 0);
							toast.show ();
							return;
						}
						if (selected.length()!=3)
						{
							Toast toast = Toast.makeText (LocationSetupActivity.this, "", App.getDelay ());
							toast.setText (R.string.invalid_id);
							toast.setGravity (Gravity.CENTER, 0, 0);
							toast.show ();
							return;
						}
						// Try to fetch from local DB or Server
						AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String> ()
						{
							@Override
							protected String doInBackground (String... params)
							{
								try
								{
									if (!serverService.checkInternetConnection ())
									{
										AlertDialog alertDialog = App.getAlertDialog (LocationSetupActivity.this, AlertType.ERROR, getResources ().getString (R.string.data_connection_error));
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
									else
									{
										runOnUiThread (new Runnable ()
										{
											@Override
											public void run ()
											{
												loading.setIndeterminate (true);
												loading.setCancelable (false);
												loading.show ();
											}
										});
										// Update database
										publishProgress ("Searching...");
										String district = serverService.getFacility (App.getDistrict().substring(0,2).concat(selected));
										if (district != null)
										{
											App.setFacility (district);

											// Save district in preferences
											SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (getBaseContext());
											SharedPreferences.Editor editor = preferences.edit ();
											editor.putString(Preferences.FACILITY, App.getFacility ());
											editor.apply ();
											
										}
										else
										{
											return "FAIL";
										}
									}
								}
								catch (Exception e)
								{
									Log.e (TAG, e.getMessage ());
								}
								return "SUCCESS";
							}

							@Override
							protected void onProgressUpdate (String... values)
							{
								loading.setMessage (values[0]);
							};

							@Override
							protected void onPostExecute (String result)
							{
								super.onPostExecute (result);
								if (!result.equals ("SUCCESS"))
								{
									App.getAlertDialog (LocationSetupActivity.this, AlertType.ERROR, getResources ().getString (R.string.item_not_found)).show ();
								}
								loading.dismiss ();
								initView (views);
							}
						};
						updateTask.execute ("");
					}
				}); 
				builder.setNegativeButton (R.string.cancel, null);
				dialog = builder.create ();
				break;
				
				
			case SCREENING_STRATEGY_DIALOG :
				builder.setTitle (getResources ().getString (R.string.screening_strategy_id));
				final EditText screeningStrategyText = new EditText (this);
				screeningStrategyText.setTag ("screening_strategy");
				screeningStrategyText.setHint (R.string.screening_strategy_search_hint);
				maxLength = 3;    
				screeningStrategyText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
				builder.setView (screeningStrategyText);
				builder.setPositiveButton (R.string.save, new DialogInterface.OnClickListener ()
				{
					@Override
					public void onClick (DialogInterface dialogInterface, int i)
					{
						final String selected = App.get (screeningStrategyText);
						if (selected.equals (""))
						{
							Toast toast = Toast.makeText (LocationSetupActivity.this, "", App.getDelay ());
							toast.setText (R.string.empty_data);
							toast.setGravity (Gravity.CENTER, 0, 0);
							toast.show ();
							return;
						}
						if (selected.length()!=3)
						{
							Toast toast = Toast.makeText (LocationSetupActivity.this, "", App.getDelay ());
							toast.setText (R.string.invalid_id);
							toast.setGravity (Gravity.CENTER, 0, 0);
							toast.show ();
							return;
						}
						// Try to fetch from local DB or Server
						AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String> ()
						{
							@Override
							protected String doInBackground (String... params)
							{
								try
								{
									if (!serverService.checkInternetConnection ())
									{
										AlertDialog alertDialog = App.getAlertDialog (LocationSetupActivity.this, AlertType.ERROR, getResources ().getString (R.string.data_connection_error));
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
									else
									{
										runOnUiThread (new Runnable ()
										{
											@Override
											public void run ()
											{
												loading.setIndeterminate (true);
												loading.setCancelable (false);
												loading.show ();
											}
										});
										// Update database
										publishProgress ("Searching...");
										String strategy = serverService.getStrategy ("Strategy ".concat(selected),selected);
										if (strategy != null)
										{
											App.setScreeningStrategy(strategy);

											// Save district in preferences
											SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (getBaseContext());
											SharedPreferences.Editor editor = preferences.edit ();
											editor.putString(Preferences.SCREENING_STRATEGY, App.getScreeningStrategy ());
											editor.apply ();
											
										}
										else
										{
											return "FAIL";
										}
									}
								}
								catch (Exception e)
								{
									Log.e (TAG, e.getMessage ());
								}
								return "SUCCESS";
							}

							@Override
							protected void onProgressUpdate (String... values)
							{
								loading.setMessage (values[0]);
							};

							@Override
							protected void onPostExecute (String result)
							{
								super.onPostExecute (result);
								if (!result.equals ("SUCCESS"))
								{
									App.getAlertDialog (LocationSetupActivity.this, AlertType.ERROR, getResources ().getString (R.string.item_not_found)).show ();
								}
								loading.dismiss ();
								initView (views);
							}
						};
						updateTask.execute ("");
					}
				}); 
				builder.setNegativeButton (R.string.cancel, null);
				dialog = builder.create ();
			break;
		}
		
		return dialog;
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Spinner spinner = (Spinner) parent;
		
		if(spinner == districtSpinner)
		{
			String selectedValue =spinner.getSelectedItem().toString();
			App.setDistrict (selectedValue);

			// Save district in preferences
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (getBaseContext());
			SharedPreferences.Editor editor = preferences.edit ();
			editor.putString(Preferences.DISTRICT, App.getDistrict ());
			
			Boolean enabled = facilitySpinner.isEnabled();
			int drawable = enabled ? R.drawable.custom_spinner_item_enabled : R.drawable.custom_spinner_item_disabled;
			String prefix = selectedValue.substring(0,2);
			String[] facilitiesList = serverService.getFacilities (prefix);
			ArrayAdapter<String> facilityAdapter = new ArrayAdapter<String> (this.getBaseContext (), drawable, facilitiesList);
			facilitySpinner.setAdapter(null);
			facilitySpinner.setAdapter (facilityAdapter);
			
			if(facilityRadioButton.isChecked())
			{
				
				String facility = App.getFacility();
				if(facility != "")
				{
					ArrayAdapter facilityAdap = (ArrayAdapter) facilitySpinner.getAdapter(); //cast to an ArrayAdapter
					int spinnerPosition = facilityAdap.getPosition(facility);
					//set the default according to value
					if(spinnerPosition != -1)
						facilitySpinner.setSelection(spinnerPosition);
				}
				
				if(facilitySpinner.getSelectedItemPosition () != -1)
				{
					selectedValue =facilitySpinner.getSelectedItem().toString();
					App.setFacility (selectedValue);
					locationIdTextView.setText(App.getFacility());
				}
				else
				{
					App.setFacility ("");
					locationIdTextView.setText(App.getFacility());
				}
				
				App.setLocation(App.getFacility());
				// Save location in preferences
				editor.putString(Preferences.LOCATION, App.getLocation ());
			
			}
			else
			{
				App.setLocation(App.getDistrict());
				// Save location in preferences
				locationIdTextView.setText(App.getLocation());
				editor.putString(Preferences.LOCATION, App.getLocation ());
			}
			editor.apply ();
		}
		
		else if(spinner == facilitySpinner)
		{
			String selectedValue =spinner.getSelectedItem().toString();
			App.setFacility (selectedValue);

			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (getBaseContext());
			SharedPreferences.Editor editor = preferences.edit ();
			editor.putString(Preferences.FACILITY, App.getFacility ());
			
			if(facilityRadioButton.isChecked())
			{
				if(facilitySpinner.getSelectedItemPosition () != -1)
				{
					
					locationIdTextView.setText(App.getFacility());
				}
				else
				{
					App.setFacility ("");
					locationIdTextView.setText(App.getFacility());
				}
				
				App.setLocation(App.getFacility());
				// Save location in preferences
				editor.putString(Preferences.LOCATION, App.getLocation ());
				
			}
			editor.apply ();
		}
		
		else if(spinner == screeningStrategySpinner)
		{
			String selectedValue =spinner.getSelectedItem().toString();
			App.setScreeningStrategy (selectedValue);
			
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (getBaseContext());
			SharedPreferences.Editor editor = preferences.edit ();
			editor.putString(Preferences.SCREENING_STRATEGY, App.getScreeningStrategy());
			editor.apply ();
		}
		
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	
}