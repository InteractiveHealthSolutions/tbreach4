/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.tbr4mobile;

import java.util.ArrayList;
import java.util.Calendar;

import com.ihsinformatics.tbr4mobile.shared.AlertType;
import com.ihsinformatics.tbr4mobile.shared.FormType;
import com.ihsinformatics.tbr4mobile.util.GPSTracker;
import com.ihsinformatics.tbr4mobile.util.ServerService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class QuickScreeningActivity extends Activity implements IActivity, OnClickListener, OnCheckedChangeListener, OnItemSelectedListener
{

	private static final String		TAG					= "QuickScreeningActivity";
	private ServerService			serverService;
	protected static ProgressDialog	loading;
	
	RadioButton						maleRadioButton;
	RadioButton						femaleRadioButton;
	CheckBox						coughCheckBox;
	CheckBox						nightSweatsCheckBox;
	CheckBox						weightLossCheckBox;
	CheckBox						feverCheckBox;
	Button							saveButton;
	TextView						screenerInstructionHeadingTextView;
	TextView						screenerInstructionTextView;
	EditText						ageEditText;
	
	Calendar						formDate;
	String							FORM_NAME;
	View[]							views;
	Animation						alphaAnimation;
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		setTheme (App.getTheme ());
		setContentView (R.layout.quick_screening);
		Configuration config = new Configuration ();
		config.locale = App.getCurrentLocale ();
		getApplicationContext ().getResources ().updateConfiguration (config, null);
		serverService = new ServerService (getApplicationContext ());
		loading = new ProgressDialog (this);
		
		FORM_NAME = "Quick Screening Form";
		
		femaleRadioButton = (RadioButton) findViewById(R.quickscreening_id.femaleradioButton);
		maleRadioButton = (RadioButton) findViewById(R.quickscreening_id.maleradioButton);
		coughCheckBox = (CheckBox) findViewById(R.quickscreening_id.checkBoxCough);
		nightSweatsCheckBox = (CheckBox) findViewById(R.quickscreening_id.checkBoxNightSweats);
		weightLossCheckBox = (CheckBox) findViewById(R.quickscreening_id.checkBoxWeightLoss);
		feverCheckBox = (CheckBox) findViewById(R.quickscreening_id.checkBoxFever);
		saveButton = (Button) findViewById(R.quickscreening_id.buttonSave);
		screenerInstructionHeadingTextView = (TextView) findViewById(R.quickscreening_id.textViewscreenerInstructionHeading);
		screenerInstructionTextView = (TextView) findViewById(R.quickscreening_id.textViewinstruction);
		ageEditText = (EditText) findViewById(R.quickscreening_id.editTextAge);
		ageEditText.setTag("Age");
		ageEditText.setHint(R.string.age_hint);
				
		femaleRadioButton.setOnClickListener(this);
		maleRadioButton.setOnClickListener(this);
		
		coughCheckBox.setOnCheckedChangeListener(this);
		nightSweatsCheckBox.setOnCheckedChangeListener(this);
		weightLossCheckBox.setOnCheckedChangeListener(this);
		feverCheckBox.setOnCheckedChangeListener(this);
		
		saveButton.setOnClickListener(this);
		
		this.setTitle(FORM_NAME);
		super.onCreate (savedInstanceState);
		initView (views);
	
	}
	
	
	@Override
	public void onCheckedChanged(CompoundButton button, boolean state) {
		if(button == coughCheckBox || button == nightSweatsCheckBox || button == weightLossCheckBox || button == feverCheckBox ){
			
			if( coughCheckBox.isChecked() == true || nightSweatsCheckBox.isChecked() == true || weightLossCheckBox.isChecked() == true || feverCheckBox.isChecked() == true){
					screenerInstructionHeadingTextView.setVisibility(View.VISIBLE);
					screenerInstructionTextView.setVisibility(View.VISIBLE);
			}
			else{
				screenerInstructionHeadingTextView.setVisibility(View.GONE);
				screenerInstructionTextView.setVisibility(View.GONE);
			}
			
		}
		
	}

	@Override
	public void onClick(View view) {
		if(view == femaleRadioButton){
			maleRadioButton.setChecked(false);
		}
		else if(view == maleRadioButton){
			femaleRadioButton.setChecked(false);
		}
		else if(view == saveButton){
			submit();
		}
		
	}

	@Override
	public void initView(View[] views) {
		ageEditText.setText("");
		coughCheckBox.setChecked(false);
		nightSweatsCheckBox.setChecked(false);
		weightLossCheckBox.setChecked(false);
		feverCheckBox.setChecked(false);
		formDate = Calendar.getInstance ();
		maleRadioButton.setChecked(true);
		femaleRadioButton.setChecked(false);
		saveButton.setVisibility(View.VISIBLE);
		screenerInstructionHeadingTextView.setVisibility(View.GONE);
		screenerInstructionTextView.setVisibility(View.GONE);
	}
	
	
	@Override
	public void updateDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validate() {
		
		Boolean valid = true;
		StringBuffer message = new StringBuffer ();
		
		View[] mandatory = {ageEditText};
		for (View v : mandatory)
		{
			if(v.isEnabled())
			{	
				if (App.get (v).equals (""))
				{
					valid = false;
					message.append (v.getTag () + ". ");
					((EditText) v).setTextColor (getResources ().getColor (R.color.Red));
				}
			}
		}
		
		if (!valid)
		{
			message.append (getResources ().getString (R.string.empty_data) + "\n");
		}
		else{
			
			String age = App.get(ageEditText);
			int a = Integer.parseInt(age);
			
			if(a<=0 || a>120 ){
				valid = false;
				message.append (ageEditText.getTag () + ". ");
				ageEditText.setTextColor (getResources ().getColor (R.color.Red));
			}
			
		}
		
		if (!valid)
		{
			message.append (getResources ().getString (R.string.invalid_data) + "\n");
		}
		
		if (!valid)
		{
			App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();
		}
		return valid;
	}

	@Override
	public boolean submit() {
		if (validate ())
		{
			
			double latitude = 0;
			double longitude = 0;

			GPSTracker gps = new GPSTracker(QuickScreeningActivity.this);

			// check if GPS enabled
			if (gps.canGetLocation()) {

				latitude = gps.getLatitude();
				longitude = gps.getLongitude();

			} else {
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gps.showSettingsAlert();
				return false;
			}
			
			final ContentValues values = new ContentValues();
			final ArrayList<String[]> observations = new ArrayList<String[]>();

			values.put("firstName", "Quick");
			values.put("lastName", "Screen");
			values.put("formDate", App.getSqlDate(formDate));
			values.put("gender", maleRadioButton.isChecked() ? "M" : "F");
			values.put("age", App.get(ageEditText));
			Calendar dateOfBirth =  Calendar.getInstance ();
			dateOfBirth.add(Calendar.YEAR, -Integer.parseInt(App.get(ageEditText)));
			values.put("dob", App.getSqlDate(dateOfBirth));
			
			if(coughCheckBox.isChecked() || nightSweatsCheckBox.isChecked() || weightLossCheckBox.isChecked() || feverCheckBox.isChecked())
				values.put("TB Suspect", "Suspect");
			else
				values.put("TB Suspect", "Non-Suspect");

			observations.add(new String[] { "Cough",  coughCheckBox.isChecked() ? "Yes" : "No" });
			observations.add(new String[] { "Night Sweats", nightSweatsCheckBox.isChecked() ? "Yes" : "No" });
			observations.add(new String[] { "Weight Loss", weightLossCheckBox.isChecked() ? "Yes" : "No" });
			observations.add(new String[] { "Fever", feverCheckBox.isChecked() ? "Yes" : "No" });
			
			observations.add(new String[] { "Screening Type",App.getScreeningType() });

			observations.add(new String[] { "District", App.getDistrict() });

			if (App.getScreeningType().equals("Community")) {
				String screeningStrategy = App.getScreeningStrategy().substring(6, App.getScreeningStrategy().length());
				observations.add(new String[] { "Screening Strategy",screeningStrategy });
			} else
				observations.add(new String[] { "Facility name",App.getFacility() });
			
			observations.add(new String[] { "Latitude",String.valueOf(latitude) });
			observations.add(new String[] { "Longitude",String.valueOf(longitude) });
			
			AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String>() {
				@Override
				protected String doInBackground(String... params) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							loading.setIndeterminate(true);
							loading.setCancelable(false);
							loading.setMessage(getResources().getString(
									R.string.loading_message));
							loading.show();
						}
					});

					String result = "";

					result = serverService.saveQuickScreening(FormType.QUICK_SCREENING,
							values, observations.toArray(new String[][] {}));

					return result;
				}

				@Override
				protected void onProgressUpdate(String... values) {
				};

				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					loading.dismiss();
					if (result.contains ("SUCCESS"))
					{
						result = result.replace("SUCCESS","");
						App.getAlertDialog (QuickScreeningActivity.this, AlertType.INFO, getResources ().getString (R.string.success_screening)).show ();
						initView (views);
					} else {
						App.getAlertDialog(QuickScreeningActivity.this,
								AlertType.ERROR, result).show();
					}
				}
			};
			updateTask.execute("");		
		}
		return true;
	}
	
	@Override
	public void onBackPressed ()
	{
		finish ();
		Intent mainMenuIntent = new Intent (getApplicationContext (), MainMenuActivity.class);
		startActivity (mainMenuIntent);
			
	}
	
	


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
