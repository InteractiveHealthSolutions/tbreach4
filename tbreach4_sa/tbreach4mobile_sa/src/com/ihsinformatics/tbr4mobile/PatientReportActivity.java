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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import com.ihsinformatics.tbr4mobile.custom.MyButton;
import com.ihsinformatics.tbr4mobile.custom.MyCheckBox;
import com.ihsinformatics.tbr4mobile.custom.MyEditText;
import com.ihsinformatics.tbr4mobile.custom.MyRadioButton;
import com.ihsinformatics.tbr4mobile.custom.MyRadioGroup;
import com.ihsinformatics.tbr4mobile.custom.MyTextView;
import com.ihsinformatics.tbr4mobile.shared.AlertType;
import com.ihsinformatics.tbr4mobile.shared.FormType;
import com.ihsinformatics.tbr4mobile.util.RegexUtil;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class PatientReportActivity extends AbstractFragmentActivity
{
	static final int	GET_PATIENT_ID	= 1;
	// Views displayed in pages, sorted w.r.t. appearance on pager

	MyTextView          searchPatientTextView;
	MyRadioGroup		searchOption;
	MyRadioButton		patientIdRadioButton;
	MyRadioButton		nhlsIdRadioButton;
	
	MyTextView			patientIdMyTextView;
	MyEditText			patientId;
	MyTextView			testIdMyTextView;
	MyEditText			testId;
	MyButton			scanBarcode;
	
	MyButton 			searchButton;

	MyTextView			firstNameTextView;
	MyEditText			firstName;
	MyTextView			surnameTextView;
	MyEditText			surname;
	MyTextView			dobTextView;
	MyEditText			dob;
	MyTextView			ageTextView;
	MyEditText			age;
	MyTextView			genderTextView;
	MyEditText			gender;
	
	MyTextView			physicalAddressTextView;
	MyEditText			physicalAddress;
	//MyTextView      	colonyAddressTextView;
	//MyEditText			colonyAddress;
	MyTextView			townAddressTextView;
	MyEditText			townAddress;
	MyTextView			landmarkAddressTextView;
	MyEditText			landmarkAddress;
	/*MyTextView			cityTextView;
	MyEditText			city;*/
	MyTextView			countryTextView;
	MyEditText			country;
	
	MyTextView			phoneTextView;
	MyEditText			phone;
	MyTextView			phone2TextView;
	MyEditText			phone2;
	MyTextView			contactTbTextView;
	MyEditText			contactTb;
	MyTextView			lastHivResultTextView;
	MyEditText			lastHivResult;
	MyTextView			diabetesTextView;
	MyEditText			diabetes;
	MyTextView			sputumSubmissionDateTextView;
	MyEditText			sputumSubmissionDate;
	MyTextView			sputumResultDateTextView;
	MyEditText			sputumResultDate;
	MyTextView			genexpertResultTextView;
	MyEditText			genexpertResult;
	MyTextView			treatmentInitiationDateTextView;
	MyEditText			treatmentInitiationDate;
	
	MyTextView			pidTextView;
	MyEditText			pid;
	MyTextView			nhlsIdTextView;
	MyEditText			nhlsId;
	
	final int PATIENT_ID = 1;
	final int NHLS_ID = 2;
	
	String[][] patientDetail = null;

	/**
	 * Subclass representing Fragment for test indication form
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	class TestIndicationFragment extends Fragment
	{
		int	currentPage;

		@Override
		public void onCreate (Bundle savedInstanceState)
		{
			super.onCreate (savedInstanceState);
			Bundle data = getArguments ();
			currentPage = data.getInt ("current_page", 0);
		}

		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			// Return a layout of views from pre-filled ArrayList of groups
			if (currentPage != 0 && groups.size () != 0)
				return groups.get (currentPage - 1);
			else
				return null;
		}
	}

	/**
	 * Subclass for Pager Adapter. Uses TestIndicationFragment subclass as items
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	class TestIndicationFragmentPagerAdapter extends FragmentPagerAdapter
	{
		/** Constructor of the class */
		public TestIndicationFragmentPagerAdapter (FragmentManager fragmentManager)
		{
			super (fragmentManager);
		}

		/** This method will be invoked when a page is requested to create */
		@Override
		public Fragment getItem (int arg0)
		{
			TestIndicationFragment fragment = new TestIndicationFragment ();
			Bundle data = new Bundle ();
			data.putInt ("current_page", arg0 + 1);
			fragment.setArguments (data);
			return fragment;
		}

		/** Returns the number of pages */
		@Override
		public int getCount ()
		{
			return PAGE_COUNT;
		}
	}

	@Override
	public void createViews (final Context context)
	{
		FORM_NAME = "Patient Report";
		TAG = "PatientReportActivity";
		PAGE_COUNT = 7;
		pager = (ViewPager) findViewById (R.template_id.pager);
		navigationSeekbar.setMax (PAGE_COUNT - 1);
		navigatorLayout = (LinearLayout) findViewById (R.template_id.navigatorLayout);
		// If the form consists only of single page, then hide the
		// navigatorLayout
		if (PAGE_COUNT < 2)
		{
			navigatorLayout.setVisibility (View.GONE);
		}
		FragmentManager fragmentManager = getSupportFragmentManager ();
		TestIndicationFragmentPagerAdapter pagerAdapter = new TestIndicationFragmentPagerAdapter (fragmentManager);
		pager.setAdapter (pagerAdapter);
		pager.setOffscreenPageLimit (PAGE_COUNT);
		// Create views for pages

		searchPatientTextView = new MyTextView (context, R.style.text, R.string.search_option);
		patientIdRadioButton = new MyRadioButton (context, R.string.patient_id_radio, R.style.radio, R.string.patient_id_radio);
		nhlsIdRadioButton = new MyRadioButton (context, R.string.nhls_id_radio, R.style.radio, R.string.nhls_id_radio);
		searchOption = new MyRadioGroup (context, new MyRadioButton[] {patientIdRadioButton, nhlsIdRadioButton}, R.string.search_option, R.style.radio, App.isLanguageRTL (),0);

		
		patientIdMyTextView = new MyTextView (context, R.style.text, R.string.patient_id);
		patientId = new MyEditText (context, R.string.patient_id, R.string.patient_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.idLength, false);
		scanBarcode = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.scan_code, R.string.scan_code);
		
		testIdMyTextView = new MyTextView (context, R.style.text, R.string.test_id);
		testId = new MyEditText (context, R.string.test_id, R.string.lab_test_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.labTestIdLength, false);
		
		searchButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.fetch_name, R.string.fetch_name);
		
		// Patient's Name
		firstNameTextView = new MyTextView (context, R.style.text, R.string.first_name);
		firstName = new MyEditText (context, R.string.first_name, R.string.empty_string, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 20, false);
		surnameTextView = new MyTextView (context, R.style.text, R.string.last_name);
		surname = new MyEditText (context, R.string.last_name, R.string.empty_string, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 20, false);
		firstName.setKeyListener(null);
		surname.setKeyListener(null);
		
		dobTextView = new MyTextView (context, R.style.text, R.string.dob);
		dob = new MyEditText (context, R.string.dob, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 20, false);
		dob.setKeyListener(null);
		
		ageTextView = new MyTextView (context, R.style.text, R.string.age);
		age = new MyEditText (context, R.string.age, R.string.empty_string, InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);
		age.setKeyListener(null);
		
		genderTextView = new MyTextView (context, R.style.text, R.string.gender);
		gender = new MyEditText (context, R.string.gender, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 10, false);
		gender.setKeyListener(null);
		
		physicalAddressTextView = new MyTextView (context, R.style.text, R.string.physical_address); 
		physicalAddress  = new MyEditText (context, R.string.physical_address, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 0, false);
		physicalAddress.setKeyListener(null);
		
		
		townAddressTextView = new MyTextView (context, R.style.text, R.string.town_address);
		townAddress = new MyEditText (context, R.string.town_address, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 0, false);
		townAddress.setKeyListener(null);
		
		landmarkAddressTextView = new MyTextView (context, R.style.text, R.string.landmark_address);
		landmarkAddress  = new MyEditText (context, R.string.landmark_address, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 0, false);
		landmarkAddress.setKeyListener(null);
		
		/*cityTextView = new MyTextView (context, R.style.text, R.string.city_address);
		city  = new MyEditText (context, R.string.city_address, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 0, false);
		city.setKeyListener(null);*/
		
		countryTextView = new MyTextView (context, R.style.text, R.string.country_address);
		country  = new MyEditText (context, R.string.country_address, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 0, false);
		country.setKeyListener(null);
		
		phoneTextView = new MyTextView (context, R.style.text, R.string.phone1);
		phone = new MyEditText (context, R.string.phone_number, R.string.empty_string, InputType.TYPE_CLASS_PHONE, R.style.edit, 20, false);
		phone.setKeyListener(null);
		
		phone2TextView = new MyTextView (context, R.style.text, R.string.phone2);
		phone2 = new MyEditText (context, R.string.phone_number, R.string.empty_string, InputType.TYPE_CLASS_PHONE, R.style.edit, 20, false);
		phone2.setKeyListener(null);
		
		contactTbTextView  = new MyTextView (context, R.style.text, R.string.contact_with_tb);
		contactTb = new MyEditText (context, R.string.contact_with_tb, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 20, false);
		contactTb.setKeyListener(null);
		
		lastHivResultTextView  = new MyTextView (context, R.style.text, R.string.hivTestResult);
		lastHivResult = new MyEditText (context, R.string.hivTestResult, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 20, false);
		lastHivResult.setKeyListener(null);
		
		diabetesTextView = new MyTextView (context, R.style.text, R.string.diabetes);
		diabetes = new MyEditText (context, R.string.diabetes, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 20, false);
		diabetes.setKeyListener(null);
		
		sputumSubmissionDateTextView = new MyTextView (context, R.style.text, R.string.date_of_sputum_submission);
		sputumSubmissionDate = new MyEditText (context, R.string.date_of_sputum_submission, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 20, false);
		sputumSubmissionDate.setKeyListener(null);
		
		sputumResultDateTextView = new MyTextView (context, R.style.text, R.string.date_of_sputum_result);
		sputumResultDate = new MyEditText (context, R.string.date_of_sputum_result, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 20, false);
		sputumResultDate.setKeyListener(null);
		
		genexpertResultTextView = new MyTextView (context, R.style.text, R.string.gxp_result);
		genexpertResult = new MyEditText (context, R.string.gxp_result, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 20, false);
		genexpertResult.setKeyListener(null);
		
		treatmentInitiationDateTextView = new MyTextView(context, R.style.text, R.string.treatment_initiation_date);
		treatmentInitiationDate = new MyEditText (context, R.string.treatment_initiation_date, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 20, false);
		treatmentInitiationDate.setKeyListener(null);
		
		pidTextView = new MyTextView (context, R.style.text, R.string.patient_id);
		pid = new MyEditText (context, R.string.patient_id, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.idLength, false);
		pid.setKeyListener(null);
		
		nhlsIdTextView = new MyTextView (context, R.style.text, R.string.test_id);
		nhlsId = new MyEditText (context, R.string.test_id, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.labTestIdLength, false);
		nhlsId.setKeyListener(null);
		
		//saveButton.setText("Search");
		
		View[][] viewGroups = { {searchPatientTextView, searchOption, scanBarcode, patientIdMyTextView, patientId, testIdMyTextView, testId, searchButton},
								{pidTextView, pid, nhlsIdTextView, nhlsId, firstNameTextView,firstName,surnameTextView,surname,},
								{genderTextView, gender,dobTextView,dob, ageTextView, age},
								{phoneTextView, phone, phone2TextView, phone2},
								{physicalAddressTextView, physicalAddress, townAddressTextView, townAddress, landmarkAddressTextView, landmarkAddress, /*cityTextView, city,*/ countryTextView, country,  },
								{contactTbTextView, contactTb, lastHivResultTextView, lastHivResult, diabetesTextView, diabetes, },
								{sputumSubmissionDateTextView, sputumSubmissionDate,sputumResultDateTextView,sputumResultDate,genexpertResultTextView,genexpertResult,treatmentInitiationDateTextView,treatmentInitiationDate}};
		// Create layouts and store in ArrayList
		groups = new ArrayList<ViewGroup> ();
		for (int i = 0; i < PAGE_COUNT; i++)
		{
			LinearLayout layout = new LinearLayout (context);
			layout.setOrientation (LinearLayout.VERTICAL);
			for (int j = 0; j < viewGroups[i].length; j++)
			{
				layout.addView (viewGroups[i][j]);
			}
			ScrollView scrollView = new ScrollView (context);
			scrollView.setLayoutParams (new LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			scrollView.addView (layout);
			groups.add (scrollView);
		}
		// Set event listeners
		firstButton.setOnClickListener (this);
		lastButton.setOnClickListener (this);
		clearButton.setOnClickListener (this);
		scanBarcode.setOnClickListener (this);
		searchButton.setOnClickListener (this);
		
		patientIdRadioButton.setOnClickListener(this);
		nhlsIdRadioButton.setOnClickListener(this);
		
		navigationSeekbar.setOnSeekBarChangeListener (this);
		views = new View[] {patientId, testId, pid, nhlsId, firstName, surname, dob, age, gender, physicalAddress, townAddress, landmarkAddress, /*city,*/ country,
							phone, phone2, contactTb, diabetes,sputumSubmissionDate,sputumResultDate,genexpertResult,treatmentInitiationDate};
		
		pager.setOnPageChangeListener (this);
		patientId.setOnLongClickListener (this);
		// Detect RTL language
		if (App.isLanguageRTL ())
		{
			Collections.reverse (groups);
			for (ViewGroup g : groups)
			{
				LinearLayout linearLayout = (LinearLayout) g.getChildAt (0);
				linearLayout.setGravity (Gravity.RIGHT);
			}
			for (View v : views)
			{
				if (v instanceof EditText)
				{
					((EditText) v).setGravity (Gravity.RIGHT);
				}
			}
		}
	}

	@Override
	public void initView (View[] views)
	{
		super.initView (views);
		
		patientIdRadioButton.setChecked(true);
		testId.setEnabled(false);
		testIdMyTextView.setEnabled(false);
		
		patientId.setEnabled(true);
		patientId.requestFocus();
		testId.clearFocus();
		testId.setText("");
		
		treatmentInitiationDate.setVisibility(View.GONE);
		treatmentInitiationDateTextView.setVisibility(View.GONE);
		
		Intent thisIntent = this.getIntent();
		
		if(thisIntent.hasExtra("pid")){
			
			String pid = thisIntent.getStringExtra("pid");
			patientId.setText(pid);
			getPatientDetails(pid,PATIENT_ID);
			
		}
		
	}

	@Override
	public void onBackPressed (){
		
		Intent thisIntent = this.getIntent();
		if(thisIntent.hasExtra("pid")){
			
			String pid = thisIntent.getStringExtra("pid");
			pid = pid + "123";
			finish();
			
		}
		else{
			AlertDialog confirmationDialog = new AlertDialog.Builder (this).create ();
			confirmationDialog.setTitle (getResources ().getString (R.string.close_form));
			confirmationDialog.setMessage (getResources ().getString (R.string.confirm_close));
			confirmationDialog.setButton (AlertDialog.BUTTON_POSITIVE, getResources ().getString (R.string.yes), new AlertDialog.OnClickListener ()
			{
				@Override
				public void onClick (DialogInterface dialog, int which)
				{
					finish ();
					Intent mainMenuIntent = new Intent (getApplicationContext (), MainMenuActivity.class);
					startActivity (mainMenuIntent);
				}
			});
			confirmationDialog.setButton (AlertDialog.BUTTON_NEGATIVE, getResources ().getString (R.string.cancel), new AlertDialog.OnClickListener ()
			{
				@Override
				public void onClick (DialogInterface dialog, int which)
				{
				}
			});
			confirmationDialog.show ();
		}

	}
	
	@Override
	public void updateDisplay ()
	{
	
	}

	@Override
	public boolean validate ()
	{
		boolean valid = true;
		
		return valid;
	}

	public boolean submit ()
	{
		
		return true;
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult (requestCode, resultCode, data);
		// Retrieve barcode scan results or Search for ID
		if (requestCode == Barcode.BARCODE_RESULT || requestCode == GET_PATIENT_ID)
		{
			if (resultCode == RESULT_OK)
			{
				String str = "";
				if (requestCode == Barcode.BARCODE_RESULT)
					str = data.getStringExtra (Barcode.SCAN_RESULT);
				else if (requestCode == GET_PATIENT_ID)
					str = data.getStringExtra (PatientSearchActivity.SEARCH_RESULT);
				// Check for valid Id
				if(patientIdRadioButton.isChecked())
				{	
					if (RegexUtil.isValidId (str) && !RegexUtil.isNumeric (str, false))
					{
						patientId.setText (str);
					}
					else
					{
						App.getAlertDialog (this, AlertType.ERROR, patientId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data)).show ();
					}
				}
				else
				{
					if (str.length() <= 11)
					{
						testId.setText (str);
					}
					else
					{
						App.getAlertDialog (this, AlertType.ERROR, patientId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data)).show ();
					}
				}
			}
			else if (resultCode == RESULT_CANCELED)
			{
				// Handle cancel
				App.getAlertDialog (this, AlertType.ERROR, getResources ().getString (R.string.operation_cancelled)).show ();
			}
			// Set the locale again, since the Barcode app restores system's
			// locale because of orientation
			Locale.setDefault (App.getCurrentLocale ());
			Configuration config = new Configuration ();
			config.locale = App.getCurrentLocale ();
			getApplicationContext ().getResources ().updateConfiguration (config, null);
		}
	}

	@Override
	public void onClick (View view)
	{
		view.startAnimation (alphaAnimation);
		if (view == firstButton)
		{
			gotoFirstPage ();
		}
		else if (view == lastButton)
		{
			gotoLastPage ();
		}
		else if (view == clearButton)
		{
			
			initView(views);
				
		}
		else if (view == scanBarcode)
		{
			Intent intent = new Intent (Barcode.BARCODE_INTENT);
			intent.putExtra (Barcode.SCAN_MODE, Barcode.QR_MODE);
			startActivityForResult (intent, Barcode.BARCODE_RESULT);
		}
		else if (view == patientIdRadioButton)
		{
			patientId.setEnabled(true);
			patientIdMyTextView.setEnabled(true);
			testId.setEnabled(false);
			testIdMyTextView.setEnabled(false);
			patientId.requestFocus();
			testId.clearFocus();
			testId.setText("");
		}
		else if (view == nhlsIdRadioButton)
		{
			patientId.setEnabled(false);
			patientIdMyTextView.setEnabled(false);
			testId.setEnabled(true);
			testIdMyTextView.setEnabled(true);
			testId.requestFocus();
			patientId.clearFocus();
			patientId.setText("");
		}
		else if ( view == searchButton )
		{
			
			clear();
			
			if(patientIdRadioButton.isChecked())
			{
				String value = patientId.getText().toString();
				if(value.equals("") || value == null)
				{
					Toast toast = Toast.makeText (PatientReportActivity.this, "", App.getDelay ());
					toast.setText (R.string.empty_data);
					toast.setGravity (Gravity.CENTER, 0, 0);
					toast.show ();
					return;
				}
				else
				{
				  if(RegexUtil.isValidId (value) && !RegexUtil.isNumeric (value, false))	
				  {
					  getPatientDetails(value,PATIENT_ID);
				  }
				  else
				  {
					  Toast toast = Toast.makeText (PatientReportActivity.this, "", App.getDelay ());
					  toast.setText (R.string.invalid_data);
					  toast.setGravity (Gravity.CENTER, 0, 0);
					  toast.show ();
					  return; 
				  }
				}
			}
			else
			{
				String value = testId.getText().toString();
				if(value.equals("") || value == null)
				{
					Toast toast = Toast.makeText (PatientReportActivity.this, "", App.getDelay ());
					toast.setText (R.string.empty_data);
					toast.setGravity (Gravity.CENTER, 0, 0);
					toast.show ();
					return;
				}
				else
				{
					
						getPatientDetails(value,NHLS_ID);
				    
				}
			}
			
		}
	}

	
	public void clear ()
	{
		
		View[] viewsArray = new View[] {pid, nhlsId, firstName, surname, dob, age, gender, physicalAddress, townAddress, landmarkAddress, /*city,*/ country,
				phone, contactTb, diabetes,sputumSubmissionDate,sputumResultDate,genexpertResult,treatmentInitiationDate};
		
		for (View v : viewsArray)
		{
			if (v instanceof MyEditText)
			{
				((MyEditText) v).setText ("");
			}
		}
	}
	
	public void getPatientDetails (final String id, final int value)
	{
	 
		AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String> ()
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
						
						patientDetail = serverService.getPatientReport(id,value);
						if(patientDetail == null)
						   return "FAIL";		
						return "SUCCESS";
					}

					@Override
					protected void onProgressUpdate (String... values)
					{
					};

					@Override
					protected void onPostExecute (String result)
					{
						
						super.onPostExecute (result);
						loading.dismiss ();
						if (!result.equals ("SUCCESS"))
						{
							Toast toast = Toast.makeText (PatientReportActivity.this, "", App.getDelay ());
							if(patientIdRadioButton.isChecked())
								toast.setText (R.string.patient_id_missing);
							else
								toast.setText (R.string.test_id_missing);
							toast.setGravity (Gravity.CENTER, 0, 0);
							toast.show ();
							return;
						}
						firstName.setText(patientDetail[0][1]);
						surname.setText(patientDetail[1][1]);
						dob.setText(patientDetail[2][1]);
						age.setText(patientDetail[3][1]);
						gender.setText(patientDetail[4][1]);
						physicalAddress.setText(patientDetail[5][1]);
						townAddress.setText(patientDetail[6][1]);
						landmarkAddress.setText(patientDetail[7][1]);
						/*city.setText(patientDetail[8][1]);*/
						country.setText(patientDetail[9][1]);
						phone.setText(patientDetail[10][1]);
						phone2.setText(patientDetail[20][1]);
						contactTb.setText(patientDetail[11][1]);
						diabetes.setText(patientDetail[12][1]);
						sputumSubmissionDate.setText(patientDetail[13][1]);
						nhlsId.setText(patientDetail[14][1]);
						pid.setText(patientDetail[15][1]);
						lastHivResult.setText(patientDetail[18][1]);
						sputumResultDate.setText(patientDetail[16][1]);
						genexpertResult.setText(patientDetail[17][1]);
						if(patientDetail[17][1].equals("MTB Positive")){
							treatmentInitiationDate.setText(patientDetail[19][1]);
							treatmentInitiationDate.setVisibility(View.VISIBLE);
							treatmentInitiationDateTextView.setVisibility(View.VISIBLE);
						}
						else
						{
							treatmentInitiationDate.setVisibility(View.GONE);
							treatmentInitiationDateTextView.setVisibility(View.GONE);
						}
						gotoPage(1);
							
					}
				};
			updateTask.execute ("");
	}
	
	@Override
	public void onCheckedChanged (CompoundButton button, boolean state)
	{
		// Not implemented
	}

	@Override
	public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
	{
		// Not implemented
	}

	@Override
	public boolean onLongClick (View view)
	{
		if (view == patientId)
		{
			Intent intent = new Intent (view.getContext (), PatientSearchActivity.class);
			intent.putExtra (PatientSearchActivity.SEARCH_RESULT, "");
			startActivityForResult (intent, GET_PATIENT_ID);
			return true;
		}
		return false;
	}
}
