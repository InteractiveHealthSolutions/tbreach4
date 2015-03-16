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
import com.ihsinformatics.tbr4mobile.custom.MyEditText;
import com.ihsinformatics.tbr4mobile.custom.MyRadioButton;
import com.ihsinformatics.tbr4mobile.custom.MyRadioGroup;
import com.ihsinformatics.tbr4mobile.custom.MySpinner;
import com.ihsinformatics.tbr4mobile.custom.MyTextView;
import com.ihsinformatics.tbr4mobile.shared.AlertType;
import com.ihsinformatics.tbr4mobile.shared.FormType;
import com.ihsinformatics.tbr4mobile.util.RegexUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class TreatmentActivity extends AbstractFragmentActivity
{
	static final int	GET_PATIENT_ID	= 1;
	// Views displayed in pages, sorted w.r.t. appearance on pager
	
	
	MyTextView			formDateTextView;
	MyButton			formDateButton;
	
	MyTextView			treatmentTypeTextView;
	MyRadioGroup		treatmentTypeGroup;
	MyRadioButton		initiationTreatment;
	MyRadioButton		followupTreatment;
	MyRadioButton		outcomeTreatment;
	
	MyTextView          submitByTextView;
	MyRadioGroup		submitOption;
	MyRadioButton		patientIdRadioButton;
	MyRadioButton		nhlsIdRadioButton;
	
	MyTextView			patientIdMyTextView;
	MyEditText			patientId;
	MyTextView			testIdMyTextView;
	MyEditText			testId;
	MyButton			scanBarcode;
	
	MyTextView		    firstNameTextView;
	MyTextView		    firstName;
	MyButton			searchPatientButton;
	
	//Treatment Initiation Questions

	MyTextView			treatmentInitiatedTextView;
	
	MyRadioGroup 		treatmentInitiatedGroup;
	MyRadioButton 		yesTreatmentInitiated;
	MyRadioButton 		noTreatmentInitiated;
	
	MyTextView			reasonTreatmentNotInitiatedTextView;
	
	MyRadioGroup 		reasonTreatmentNotInitiatedGroup;
	MyRadioButton 		refusedReasonTreatmentNotInitiated;
	MyRadioButton 		notFoundReasonTreatmentNotInitiated;
	MyRadioButton 		contactMissingReasonTreatmentNotInitiated;
	
	MyTextView			treatmentInitiationTextView;
	
	MyRadioGroup 		treatmentInitiationGroup;
	MyRadioButton 		transferredOutTreatmentInitiation;
	MyRadioButton 		clinicTreatmentInitiation;
	
	MyTextView			transferOutLocationTextView;
	MyEditText			transferOutLocation;
	MyTextView			transferOutDateTextView;
	DatePicker 			transferOutDatePicker;
	Calendar			transferDate;
	
	MyTextView			treatmentInitiationLocationTextView;
	MyEditText			treatmentInitiationLocation;
	MyTextView			treatmentInitiationDateTextView;
	DatePicker			treatmentInitiationDatePicker;
	Calendar			initiationDate;
	
	//Treatment Followup Question
	
	MyTextView			followUpDateTextView;
	DatePicker			followUpDatePicker;
	Calendar			followUpDate;
	MyTextView			smearTestLocationTextView;
	MyEditText			smearTestLocation;
	MyTextView			smearResultTextView;
	MyEditText			smearResult;
	
	//Treatment Outcome Questions
	
	MyTextView			treatmentOutcomeTextView;
	MyRadioGroup 		treatmentOutcomeGroup;
	MyRadioButton 		curedTreatmentOutcome;
	MyRadioButton 		completedTreatmentOutcome;
	MyRadioButton 		defaultTreatmentOutcome;
	MyRadioButton 		failureTreatmentOutcome;
	MyRadioButton 		deathTreatmentOutcome;
	Calendar			outcomeDate;
	MyTextView			outcomeDateTextView;
	
	DatePicker			outcomeDatePicker;
	
	MyTextView 			treatmentInitiatedSpace;
	MyTextView			treatmentFollowupSpace;
	MyTextView			outcomeSpace;
	
	MyButton 			saveButton;

	View[]				allFormViews;
	View[]				radioButtonViews;
	
	String firstNameValue = "";
	String lastNameValue = "";
	String formType = "";

	/**
	 * Subclass representing Fragment for customer info form
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	@SuppressLint("ValidFragment")
	class CustomerInfoFragment extends Fragment
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
			return null;
		}
	}

	/**
	 * Subclass for Pager Adapter. Uses FeedbackFragment subclass as items
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	class CustomerInfoFragmentPagerAdapter extends FragmentPagerAdapter
	{
		/** Constructor of the class */
		public CustomerInfoFragmentPagerAdapter (FragmentManager fragmentManager)
		{
			super (fragmentManager);
		}

		/** This method will be invoked when a page is requested to create */
		@Override
		public Fragment getItem (int arg0)
		{
			CustomerInfoFragment fragment = new CustomerInfoFragment ();
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
		FORM_NAME = "Treatment Form";
		TAG = "TreatmentActivity";
		PAGE_COUNT = 4;
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
		CustomerInfoFragmentPagerAdapter pagerAdapter = new CustomerInfoFragmentPagerAdapter (fragmentManager);
		pager.setAdapter (pagerAdapter);
		pager.setOffscreenPageLimit (PAGE_COUNT);
		// Create views for pages
		
		formDateTextView = new MyTextView (context, R.style.text, R.string.form_date);
		formDateButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.form_date, R.string.form_date);
		
		treatmentTypeTextView = new MyTextView (context, R.style.text, R.string.type_treament_form);
		
		initiationTreatment = new MyRadioButton(context, R.string.treament_initiation, R.style.radio,R.string.treament_initiation);
		followupTreatment = new MyRadioButton(context, R.string.treament_followup, R.style.radio,R.string.treament_followup);
		outcomeTreatment = new MyRadioButton(context, R.string.treament_outcome, R.style.radio,R.string.treament_outcome);
		treatmentTypeGroup = new MyRadioGroup(context,
				new MyRadioButton[] { initiationTreatment, followupTreatment, outcomeTreatment }, R.string.type_treament_form,
				R.style.radio, App.isLanguageRTL(),1);
	    
		submitByTextView = new MyTextView (context, R.style.text, R.string.search_option);
		patientIdRadioButton = new MyRadioButton (context, R.string.patient_id_radio, R.style.radio, R.string.patient_id_radio);
		nhlsIdRadioButton = new MyRadioButton (context, R.string.nhls_id_radio, R.style.radio, R.string.nhls_id_radio);
		submitOption = new MyRadioGroup (context, new MyRadioButton[] {patientIdRadioButton, nhlsIdRadioButton}, R.string.search_option, R.style.radio, App.isLanguageRTL (),0);
	
		patientIdMyTextView = new MyTextView (context, R.style.text, R.string.patient_id);
		patientId = new MyEditText (context, R.string.patient_id, R.string.patient_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.idLength, false);
		scanBarcode = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.scan_qr_code, R.string.scan_code);
		
		testIdMyTextView = new MyTextView (context, R.style.text, R.string.test_id);
		testId = new MyEditText (context, R.string.test_id, R.string.lab_test_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.labTestIdLength, false);
		
		firstNameTextView = new MyTextView (context, R.style.text, R.string.name);
		firstName = new MyTextView (context, R.style.text, R.string.empty_string);
		firstName.setTextColor(getResources ().getColor (R.color.IRDTitle));
		
		searchPatientButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.search_patient, R.string.fetch_name);
	
		//Treatment Initiated Question!
		
		treatmentInitiatedTextView = new MyTextView (context, R.style.text, R.string.treament_initiated);
		
		yesTreatmentInitiated = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		noTreatmentInitiated = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		
		treatmentInitiatedGroup = new MyRadioGroup(context,
				new MyRadioButton[] { yesTreatmentInitiated, noTreatmentInitiated }, R.string.treament_initiated,
				R.style.radio, App.isLanguageRTL(),0);
		
		reasonTreatmentNotInitiatedTextView = new MyTextView (context, R.style.text, R.string.reason_treament_uninitiated);
		
		refusedReasonTreatmentNotInitiated = new MyRadioButton(context, R.string.refused_treatment, R.style.radio,
				R.string.refused_treatment);
		
		notFoundReasonTreatmentNotInitiated = new MyRadioButton(context, R.string.patient_not_found, R.style.radio,
				R.string.patient_not_found);
		contactMissingReasonTreatmentNotInitiated = new MyRadioButton(context, R.string.missing_contacts, R.style.radio,
				R.string.missing_contacts);
		
		reasonTreatmentNotInitiatedGroup = new MyRadioGroup(context,
				new MyRadioButton[] { refusedReasonTreatmentNotInitiated, notFoundReasonTreatmentNotInitiated, contactMissingReasonTreatmentNotInitiated}, R.string.treament_initiated,
				R.style.radio, App.isLanguageRTL(),1);
		
		treatmentInitiationTextView = new MyTextView (context, R.style.text, R.string.treatment_initiation);
		
		transferredOutTreatmentInitiation = new MyRadioButton(context, R.string.transferred_out, R.style.radio,
				R.string.transferred_out);
		clinicTreatmentInitiation = new MyRadioButton(context, R.string.treatment_clinic, R.style.radio,
				R.string.treatment_clinic);
		
		treatmentInitiationGroup = new MyRadioGroup(context,
				new MyRadioButton[] { transferredOutTreatmentInitiation, clinicTreatmentInitiation }, R.string.treatment_initiation,
				R.style.radio, App.isLanguageRTL(),1);
		
		transferOutLocationTextView = new MyTextView (context, R.style.text, R.string.tranfer_out_location);
		transferOutLocation = new MyEditText (context, R.string.tranfer_out_location, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 25, false);
		transferOutDateTextView = new MyTextView (context, R.style.text, R.string.tranfer_out_date);
		transferDate = Calendar.getInstance ();
		transferOutDatePicker = new DatePicker(context);
		transferOutDatePicker.setTag("Transfer Out Date");
		transferOutDatePicker.init(transferOutDatePicker.getYear(), transferOutDatePicker.getMonth(), transferOutDatePicker.getDayOfMonth(),new OnDateChangedListener() {
			    
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				transferDate.set (year, monthOfYear, dayOfMonth);
			   }
			   
			 });
		
		treatmentInitiationLocationTextView = new MyTextView (context, R.style.text, R.string.treatment_initiation_location);
		treatmentInitiationLocation = new MyEditText (context, R.string.treatment_initiation_location, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 25, false);
		treatmentInitiationDateTextView = new MyTextView (context, R.style.text, R.string.treatment_initiation_date);
		initiationDate = Calendar.getInstance ();
		treatmentInitiationDatePicker = new DatePicker(context);
		treatmentInitiationDatePicker.setTag("Treatment Initiation Date");
		treatmentInitiationDatePicker.init(treatmentInitiationDatePicker.getYear(), treatmentInitiationDatePicker.getMonth(), treatmentInitiationDatePicker.getDayOfMonth(),new OnDateChangedListener() {
		    
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				initiationDate.set (year, monthOfYear, dayOfMonth);
			   }
			   
			 });
		
		//FollowUpDate
		
		followUpDateTextView = new MyTextView (context, R.style.text, R.string.followup_date);
		followUpDate = Calendar.getInstance ();
		followUpDatePicker = new DatePicker(context);
		followUpDatePicker.setTag("Followup Date");
		followUpDatePicker.init(followUpDatePicker.getYear(), followUpDatePicker.getMonth(), followUpDatePicker.getDayOfMonth(),new OnDateChangedListener() {
		    
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				followUpDate.set (year, monthOfYear, dayOfMonth);
			   }
			   
			 });
		smearTestLocationTextView  = new MyTextView (context, R.style.text, R.string.smear_test_location);
		smearTestLocation = new MyEditText (context, R.string.smear_test_location, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 25, false);
		smearResultTextView  = new MyTextView (context, R.style.text, R.string.smear_result); 
		smearResult = new MyEditText (context, R.string.smear_result, R.string.empty_string, InputType.TYPE_CLASS_TEXT, R.style.edit, 25, false);
		
		//Outcome
		
		outcomeDateTextView = new MyTextView (context, R.style.text, R.string.outcome_date);
		outcomeDate = Calendar.getInstance ();
		outcomeDatePicker = new DatePicker(context);
		outcomeDatePicker.setTag("Treatment Outcome Date");
		outcomeDatePicker.init(outcomeDatePicker.getYear(), outcomeDatePicker.getMonth(), outcomeDatePicker.getDayOfMonth(),new OnDateChangedListener() {
		
			
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				outcomeDate.set (year, monthOfYear, dayOfMonth);
			   }
			   
			 });
		
		
		treatmentOutcomeTextView = new MyTextView (context, R.style.text, R.string.treatment_outcome_string);
		
		curedTreatmentOutcome = new MyRadioButton(context, R.string.cured, R.style.radio,R.string.cured);
		completedTreatmentOutcome = new MyRadioButton(context, R.string.treatment_comleted, R.style.radio,R.string.treatment_comleted);
		defaultTreatmentOutcome = new MyRadioButton(context, R.string.default_patient, R.style.radio,R.string.default_patient);
		failureTreatmentOutcome = new MyRadioButton(context, R.string.treatment_failure, R.style.radio,R.string.treatment_failure);
		deathTreatmentOutcome = new MyRadioButton(context, R.string.death, R.style.radio,R.string.death);
		
		treatmentOutcomeGroup = new MyRadioGroup(context,
				new MyRadioButton[] { curedTreatmentOutcome, completedTreatmentOutcome, defaultTreatmentOutcome, failureTreatmentOutcome, deathTreatmentOutcome}, R.string.treatment_outcome_string,
				R.style.radio, App.isLanguageRTL(),1);
		
		
		treatmentInitiatedSpace = new MyTextView(context, R.style.text,
				R.string.initiated_yes_space);
		treatmentFollowupSpace = new MyTextView(context, R.style.text,
				R.string.followup_space);
		outcomeSpace = new MyTextView(context, R.style.text,
				R.string.outcome_space);
		
		
		saveButton = new MyButton(context, R.style.button,
				R.drawable.custom_button_beige, R.string.submit_form,
				R.string.submit_form);
	
		allFormViews = new View[] {treatmentInitiatedTextView,treatmentInitiatedGroup,reasonTreatmentNotInitiatedTextView, reasonTreatmentNotInitiatedGroup, treatmentInitiationTextView,treatmentInitiationGroup,transferOutLocationTextView,transferOutLocation,transferOutDateTextView,transferOutDatePicker,treatmentInitiationLocationTextView,treatmentInitiationLocation,treatmentInitiationDateTextView,treatmentInitiationDatePicker, followUpDateTextView,followUpDatePicker,smearTestLocationTextView,smearTestLocation,smearResultTextView,smearResult,treatmentOutcomeTextView, treatmentOutcomeGroup, outcomeDateTextView, outcomeDatePicker,treatmentInitiatedSpace, treatmentFollowupSpace, outcomeSpace, saveButton};
		radioButtonViews = new View[] {initiationTreatment,followupTreatment,outcomeTreatment,yesTreatmentInitiated,noTreatmentInitiated,refusedReasonTreatmentNotInitiated,notFoundReasonTreatmentNotInitiated,contactMissingReasonTreatmentNotInitiated,transferredOutTreatmentInitiation,clinicTreatmentInitiation,curedTreatmentOutcome,completedTreatmentOutcome,defaultTreatmentOutcome,failureTreatmentOutcome,deathTreatmentOutcome};
		
		View[][] viewGroups = { 
				    {formDateTextView, formDateButton, treatmentTypeTextView, treatmentTypeGroup},
				    {submitByTextView, submitOption, scanBarcode, patientIdMyTextView, patientId, testIdMyTextView, testId,firstNameTextView, firstName, searchPatientButton},
				    {treatmentInitiatedTextView,treatmentInitiatedGroup,reasonTreatmentNotInitiatedTextView, reasonTreatmentNotInitiatedGroup, treatmentInitiationTextView,treatmentInitiationGroup,followUpDateTextView,followUpDatePicker,smearTestLocationTextView,smearTestLocation, treatmentOutcomeTextView, treatmentOutcomeGroup},
				    {transferOutLocationTextView,transferOutLocation,transferOutDateTextView,transferOutDatePicker,treatmentInitiationLocationTextView,treatmentInitiationLocation,treatmentInitiationDateTextView,treatmentInitiationDatePicker,smearResultTextView,smearResult,outcomeDateTextView, outcomeDatePicker,treatmentInitiatedSpace, treatmentFollowupSpace, outcomeSpace, saveButton}
		};
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
		formDateButton.setOnClickListener (this);
		firstButton.setOnClickListener (this);
		lastButton.setOnClickListener (this);
		clearButton.setOnClickListener (this);
		saveButton.setOnClickListener (this);
		scanBarcode.setOnClickListener(this);
		searchPatientButton.setOnClickListener(this);
		navigationSeekbar.setOnSeekBarChangeListener (this);
		
		curedTreatmentOutcome.setOnClickListener(this);
		completedTreatmentOutcome.setOnClickListener(this);
		defaultTreatmentOutcome.setOnClickListener(this);
		failureTreatmentOutcome.setOnClickListener(this);
		deathTreatmentOutcome.setOnClickListener(this);
		
		initiationTreatment.setOnClickListener(this);
		followupTreatment.setOnClickListener(this);
		outcomeTreatment.setOnClickListener(this);
		
		refusedReasonTreatmentNotInitiated.setOnClickListener(this);
		notFoundReasonTreatmentNotInitiated.setOnClickListener(this); 
		contactMissingReasonTreatmentNotInitiated.setOnClickListener(this);
		
		yesTreatmentInitiated.setOnClickListener(this);
		noTreatmentInitiated.setOnClickListener(this);
		
		transferredOutTreatmentInitiation.setOnClickListener(this);
		clinicTreatmentInitiation.setOnClickListener(this);
		
		patientIdRadioButton.setOnClickListener(this);
		nhlsIdRadioButton.setOnClickListener(this);
		
		
		views = new View[] {testId, patientId,treatmentInitiationLocation,transferOutLocation};
		for (View v : views)
		{
			if (v instanceof Spinner)
			{
				((Spinner) v).setOnItemSelectedListener (this);
			}
			else if (v instanceof CheckBox)
			{
				((CheckBox) v).setOnCheckedChangeListener (this);
			}
		}
		pager.setOnPageChangeListener (this);
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
		formDate = Calendar.getInstance ();
		updateDisplay ();
		
		patientIdRadioButton.setChecked(true);
		
		testIdMyTextView.setEnabled(false);
		testId.setEnabled(false);
		
		firstNameValue = "";
        lastNameValue = "";
        firstName.setText(firstNameValue+" "+lastNameValue);
		
        for (View v : allFormViews)
		{
			v.setVisibility(View.GONE);
		}
        
        for (View v : radioButtonViews)
		{
        	((MyRadioButton) v).setChecked (false);
		}
			
	}

	@Override
	public void updateDisplay ()
	{
		formDateButton.setText (DateFormat.format ("dd-MMM-yyyy", formDate));		
	}

	@Override
	public boolean validate ()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer ();
		// Validate mandatory controls
		View[] mandatory = {testId, patientId,treatmentInitiationLocation,transferOutLocation, smearTestLocation,smearResult};
		for (View v : mandatory)
		{
			if(v.getVisibility() == View.VISIBLE && v.isEnabled())
			{	
				if (App.get (v).equals (""))
				{
					valid = false;
					message.append (v.getTag () + ". ");
					((EditText) v).setHintTextColor (getResources ().getColor (R.color.Red));
				}
			}
		}
		
			
		if (!valid)
		{
			message.append (getResources ().getString (R.string.empty_data) + "\n");
		}
		if(valid){
			
			// Validate range
			if (formDate.getTime ().after (new Date ()))
			{
				valid = false;
				message.append (formDateButton.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
			}
			
			if(treatmentInitiationDatePicker.getVisibility() == View.VISIBLE){
				// Validate range
				if (initiationDate.getTime ().after (new Date ()))
				{
					valid = false;
					message.append (treatmentInitiationDatePicker.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
				}
			}
			
			if(transferOutDatePicker.getVisibility() == View.VISIBLE){
				// Validate range
				if (transferDate.getTime ().after (new Date ()))
				{
					valid = false;
					message.append (transferOutDatePicker.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
				}
			}
			
			if(followUpDatePicker.getVisibility() == View.VISIBLE){
				// Validate range
				if (followUpDate.getTime ().after (new Date ()))
				{
					valid = false;
					message.append (followUpDatePicker.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
				}
			}
			
			if(outcomeDatePicker.getVisibility() == View.VISIBLE){
				// Validate range
				if (outcomeDate.getTime ().after (new Date ()))
				{
					valid = false;
					message.append (outcomeDatePicker.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
				}
			}			
			
			if(patientId.isEnabled()){
				if (!RegexUtil.isValidId (App.get (patientId)))
				{
					valid = false;
					message.append (patientId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
					patientId.setTextColor (getResources ().getColor (R.color.Red));
				}
				
			}
		}
		
		if (!valid)
		{
			App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();
		}
		
		return valid;
	}
	

	public boolean submit ()
	{
		if (validate ())
		{
		
				final ContentValues values = new ContentValues ();
				values.put ("formDate", App.getSqlDate (formDate));
				values.put ("LabTestId", App.get (testId));
				values.put ("PatientId", App.get (patientId));
				
				final ArrayList<String[]> observations = new ArrayList<String[]> ();
				
				if(testId.isEnabled())
					observations.add(new String[] { "Smear Result",  App.get(testId)});
				
				if(treatmentInitiatedGroup.getVisibility() == View.VISIBLE)
					observations.add(new String[] { "Treatment Initiated",  noTreatmentInitiated.isChecked() ? "No" : "Yes" });

				if(reasonTreatmentNotInitiatedGroup.getVisibility() == View.VISIBLE)
					observations.add(new String[] { "Reason Treatment Not Initiated",  refusedReasonTreatmentNotInitiated.isChecked() ? "Refused Treatment" : (notFoundReasonTreatmentNotInitiated.isChecked() ? "Patient Not Found" : "Missing Patient Information") });
				
				if(treatmentInitiationGroup.getVisibility() == View.VISIBLE)
					observations.add(new String[] { "Treatment Initiation",  transferredOutTreatmentInitiation.isChecked() ? "Transferred Out" :  "Treatment Initiated at Clinic" });
				
				if(treatmentInitiationLocation.getVisibility() == View.VISIBLE)
					observations.add(new String[] { "Treatment Initiation Location",  App.get(treatmentInitiationLocation)});
				
				if(treatmentInitiationDatePicker.getVisibility() == View.VISIBLE)
					observations.add (new String[] {"Treatment Initiation Date", App.getSqlDate (initiationDate)});
				
				if(transferOutLocation.getVisibility() == View.VISIBLE)
					observations.add(new String[] { "Transfer Out Location",  App.get(transferOutLocation)});
				
				if(transferOutDatePicker.getVisibility() == View.VISIBLE)
					observations.add (new String[] {"Transfer Date", App.getSqlDate (transferDate)});
				
				if(followUpDatePicker.getVisibility() == View.VISIBLE)
					observations.add (new String[] {"Followup Smear Date", App.getSqlDate (followUpDate)});
				
				if(smearTestLocation.getVisibility() == View.VISIBLE)
					observations.add(new String[] { "Smear Test Location",  App.get(smearTestLocation)});
				
				if(smearResult.getVisibility() == View.VISIBLE)
					observations.add(new String[] { "Smear Result",  App.get(smearResult)});
				
				if(treatmentOutcomeGroup.getVisibility() == View.VISIBLE)
					observations.add(new String[] { "Treatment Outcome",  curedTreatmentOutcome.isChecked() ? "Cured" : (completedTreatmentOutcome.isChecked() ? "Completed" : (defaultTreatmentOutcome.isChecked() ? "Default" : (failureTreatmentOutcome.isChecked() ? "Failure" : "Death"))) });
				
				if(outcomeDatePicker.getVisibility() == View.VISIBLE)
					observations.add (new String[] {"Treatment Outcome Date", App.getSqlDate (outcomeDate)});
				
				if(initiationTreatment.isChecked())
					formType = FormType.TREATMENT_INITIATION;
				else if(followupTreatment.isChecked())
					formType = FormType.TREATMENT_FOLLOWUP;
				else if(outcomeTreatment.isChecked())
					formType = FormType.TREATMENT_OUTCOME;
				
				
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
						//TODO: Uncomment when live
						String result = serverService.saveTreatment (formType, values, observations.toArray (new String[][] {}));
						return result;
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
						if (result.equals ("SUCCESS"))
						{
							App.getAlertDialog (TreatmentActivity.this, AlertType.INFO, getResources ().getString (R.string.inserted)).show ();
							initView (views);
						}
						else
						{
							App.getAlertDialog (TreatmentActivity.this, AlertType.ERROR, result).show ();
						}
					}
				};
				updateTask.execute ("");
			}
	    
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
		
		/*if (requestCode == Barcode.BARCODE_RESULT || requestCode == GET_PATIENT_ID)
		{
			if (requestCode == Barcode.BARCODE_RESULT_TEST_ID)
			{
				if (resultCode == RESULT_OK)
				{
					String str = data.getStringExtra (Barcode.SCAN_RESULT);
					
					// Check for valid Id
					if (RegexUtil.isValidId (str) && !RegexUtil.isNumeric (str, false))
					{
						labTestId.setText (str);
					}
					else
					{
						App.getAlertDialog (this, AlertType.ERROR, labTestId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data)).show ();
					}
				}
				else if (resultCode == RESULT_CANCELED)
				{
					// Handle cancel
					App.getAlertDialog (this, AlertType.ERROR, getResources ().getString (R.string.operation_cancelled)).show ();
				}
				// Set the locale again, since the Barcode app restores system's locale because of orientation
				Locale.setDefault (App.getCurrentLocale ());
				Configuration config = new Configuration ();
				config.locale = App.getCurrentLocale ();
				getApplicationContext ().getResources ().updateConfiguration (config, null);
			   }
		}*/
	}

	@Override
	public void onClick (View view)
	{
		if (view == formDateButton)
		{
			showDialog (DATE_DIALOG_ID);
		}
		else if (view == searchPatientButton)
		{
			String id = "";
			String type = "";
			StringBuffer message = new StringBuffer ();
			
			if(patientIdRadioButton.isChecked())
			{	
				type = "pid";
			    id  = App.get(patientId);
			    
			    if(id.equals ("")){
			    
						message.append (patientId.getTag () + ": " + getResources ().getString (R.string.empty_data) + "\n");
						App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();	
						return;
			    }
			    else{
			    	if(!RegexUtil.isValidId (id))
			    	{
			    		message.append (patientId.getTag () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
						App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();
						return;
			    	}
			    }
			    
			}
			else
			{	
				
				type = "tid";
				id = App.get(testId);
				
				 if(id.equals ("")){
					    
						message.append (testId.getTag () + ": " + getResources ().getString (R.string.empty_data) + "\n");
						App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();	
						return;
			    }
				 
			}
			
			searchPatient (id,type);
		}	
		else if (view == firstButton)
		{
			gotoFirstPage ();
		}
		else if (view == lastButton)
		{
			gotoLastPage ();
		}
		else if (view == clearButton)
		{
			AlertDialog confirmationDialog = new AlertDialog.Builder (this).create ();
			confirmationDialog.setTitle (getResources ().getString (R.string.clear_form));
			confirmationDialog.setMessage (getResources ().getString (R.string.clear_close));
			confirmationDialog.setButton (AlertDialog.BUTTON_POSITIVE, getResources ().getString (R.string.yes), new AlertDialog.OnClickListener ()
			{
				@Override
				public void onClick (DialogInterface dialog, int which)
				{
					initView(views);
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
		else if (view == saveButton)
		{
			AlertDialog confirmationDialog = new AlertDialog.Builder (this).create ();
			confirmationDialog.setTitle (getResources ().getString (R.string.save_form));
			confirmationDialog.setMessage (getResources ().getString (R.string.save_close));
			confirmationDialog.setButton (AlertDialog.BUTTON_POSITIVE, getResources ().getString (R.string.yes), new AlertDialog.OnClickListener ()
			{
				@Override
				public void onClick (DialogInterface dialog, int which)
				{
					submit();
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
		else if (view == scanBarcode)
		{
			Intent intent = new Intent (Barcode.BARCODE_INTENT);
			intent.putExtra (Barcode.SCAN_MODE, Barcode.QR_MODE);
			startActivityForResult (intent, Barcode.BARCODE_RESULT);
		}
		
		else if(view == curedTreatmentOutcome || view == completedTreatmentOutcome  || view == defaultTreatmentOutcome  || view == failureTreatmentOutcome  || view == deathTreatmentOutcome){
			outcomeDateTextView.setVisibility(View.VISIBLE); 
			outcomeDatePicker.setVisibility(View.VISIBLE);
			
			outcomeSpace.setVisibility(View.VISIBLE);
			saveButton.setVisibility(View.VISIBLE);
		}
		else if (view == refusedReasonTreatmentNotInitiated || view ==  notFoundReasonTreatmentNotInitiated || view == contactMissingReasonTreatmentNotInitiated ){
			treatmentInitiatedSpace.setVisibility(View.VISIBLE);
			treatmentInitiatedSpace.setText(getResources ().getString (R.string.initiated_no_space));
			saveButton.setVisibility(View.VISIBLE);
		}
		else if (view == transferredOutTreatmentInitiation || view == clinicTreatmentInitiation){
			
			treatmentInitiatedSpace.setVisibility(View.VISIBLE);
			treatmentInitiatedSpace.setText(getResources ().getString (R.string.initiated_yes_space));
			saveButton.setVisibility(View.VISIBLE);
			
			if(clinicTreatmentInitiation.isChecked()){
				transferOutLocationTextView.setVisibility(View.GONE);
				transferOutLocation.setVisibility(View.GONE);
				transferOutDateTextView.setVisibility(View.GONE);
				transferOutDatePicker.setVisibility(View.GONE);
				treatmentInitiationLocationTextView.setVisibility(View.VISIBLE);
				treatmentInitiationLocation.setVisibility(View.VISIBLE);
				treatmentInitiationDateTextView.setVisibility(View.VISIBLE);
				treatmentInitiationDatePicker.setVisibility(View.VISIBLE);
			}
			else{
				transferOutLocationTextView.setVisibility(View.VISIBLE);
				transferOutLocation.setVisibility(View.VISIBLE);
				transferOutDateTextView.setVisibility(View.VISIBLE);
				transferOutDatePicker.setVisibility(View.VISIBLE);
				treatmentInitiationLocationTextView.setVisibility(View.GONE);
				treatmentInitiationLocation.setVisibility(View.GONE);
				treatmentInitiationDateTextView.setVisibility(View.GONE);
				treatmentInitiationDatePicker.setVisibility(View.GONE);					
			}
		}
		else if (view == yesTreatmentInitiated || view == noTreatmentInitiated){
			
			boolean check = noTreatmentInitiated.isChecked();
			
			if (check){
				reasonTreatmentNotInitiatedTextView.setVisibility(View.VISIBLE);
				reasonTreatmentNotInitiatedGroup.setVisibility(View.VISIBLE);
				treatmentInitiationTextView.setVisibility(View.GONE);
				treatmentInitiationGroup.setVisibility(View.GONE);
				
				transferOutLocationTextView.setVisibility(View.GONE);
				transferOutLocation.setVisibility(View.GONE);
				transferOutDateTextView.setVisibility(View.GONE);
				transferOutDatePicker.setVisibility(View.GONE);
				treatmentInitiationLocationTextView.setVisibility(View.GONE);
				treatmentInitiationLocation.setVisibility(View.GONE);
				treatmentInitiationDateTextView.setVisibility(View.GONE);
				treatmentInitiationDatePicker.setVisibility(View.GONE);
				
				if (refusedReasonTreatmentNotInitiated.isChecked() || notFoundReasonTreatmentNotInitiated.isChecked() || contactMissingReasonTreatmentNotInitiated.isChecked()){
					treatmentInitiatedSpace.setVisibility(View.VISIBLE);
					treatmentInitiatedSpace.setText(getResources ().getString (R.string.initiated_no_space));
					saveButton.setVisibility(View.VISIBLE);
				}
				else{
					saveButton.setVisibility(View.GONE);
					treatmentInitiatedSpace.setVisibility(View.GONE);
				}				
			}
			else {
				reasonTreatmentNotInitiatedTextView.setVisibility(View.GONE);
				reasonTreatmentNotInitiatedGroup.setVisibility(View.GONE);
				treatmentInitiationTextView.setVisibility(View.VISIBLE);
				treatmentInitiationGroup.setVisibility(View.VISIBLE);
				
				if(clinicTreatmentInitiation.isChecked()){
					transferOutLocationTextView.setVisibility(View.GONE);
					transferOutLocation.setVisibility(View.GONE);
					transferOutDateTextView.setVisibility(View.GONE);
					transferOutDatePicker.setVisibility(View.GONE);
					treatmentInitiationLocationTextView.setVisibility(View.VISIBLE);
					treatmentInitiationLocation.setVisibility(View.VISIBLE);
					treatmentInitiationDateTextView.setVisibility(View.VISIBLE);
					treatmentInitiationDatePicker.setVisibility(View.VISIBLE);
					
					treatmentInitiatedSpace.setVisibility(View.VISIBLE);
					treatmentInitiatedSpace.setText(getResources ().getString (R.string.initiated_yes_space));
					saveButton.setVisibility(View.VISIBLE);
				}
				else if (transferredOutTreatmentInitiation.isChecked()){
					transferOutLocationTextView.setVisibility(View.VISIBLE);
					transferOutLocation.setVisibility(View.VISIBLE);
					transferOutDateTextView.setVisibility(View.VISIBLE);
					transferOutDatePicker.setVisibility(View.VISIBLE);
					treatmentInitiationLocationTextView.setVisibility(View.GONE);
					treatmentInitiationLocation.setVisibility(View.GONE);
					treatmentInitiationDateTextView.setVisibility(View.GONE);
					treatmentInitiationDatePicker.setVisibility(View.GONE);	
					
					treatmentInitiatedSpace.setVisibility(View.VISIBLE);
					treatmentInitiatedSpace.setText(getResources ().getString (R.string.initiated_yes_space));
					saveButton.setVisibility(View.VISIBLE);
				}
				else{
					treatmentInitiatedSpace.setVisibility(View.GONE);
					saveButton.setVisibility(View.GONE);
				}
				
			}	
			
			
		}
		else if (view == initiationTreatment || view == followupTreatment || view == outcomeTreatment){
			
			
			for (View v : allFormViews)
			{
				v.setVisibility(View.GONE);
			}
			
			boolean check = initiationTreatment.isChecked();
			if(check){
				
				treatmentInitiatedTextView.setVisibility(View.VISIBLE);
				treatmentInitiatedGroup.setVisibility(View.VISIBLE);
				
				if(yesTreatmentInitiated.isChecked()){
					treatmentInitiationTextView.setVisibility(View.VISIBLE);
					treatmentInitiationGroup.setVisibility(View.VISIBLE);
					
					if(clinicTreatmentInitiation.isChecked()){
						treatmentInitiationLocationTextView.setVisibility(View.VISIBLE);
						treatmentInitiationLocation.setVisibility(View.VISIBLE);
						treatmentInitiationDateTextView.setVisibility(View.VISIBLE);
						treatmentInitiationDatePicker.setVisibility(View.VISIBLE);
						
						treatmentInitiatedSpace.setVisibility(View.VISIBLE);
						treatmentInitiatedSpace.setText(getResources ().getString (R.string.initiated_yes_space));
						saveButton.setVisibility(View.VISIBLE);
					}
					else if (transferredOutTreatmentInitiation.isChecked()){
						transferOutLocationTextView.setVisibility(View.VISIBLE);
						transferOutLocation.setVisibility(View.VISIBLE);
						transferOutDateTextView.setVisibility(View.VISIBLE);
						transferOutDatePicker.setVisibility(View.VISIBLE);	
						
						treatmentInitiatedSpace.setVisibility(View.VISIBLE);
						treatmentInitiatedSpace.setText(getResources ().getString (R.string.initiated_yes_space));
						saveButton.setVisibility(View.VISIBLE);
					}
				}
				else if (noTreatmentInitiated.isChecked()){
					reasonTreatmentNotInitiatedTextView.setVisibility(View.VISIBLE);
					reasonTreatmentNotInitiatedGroup.setVisibility(View.VISIBLE);
					
					treatmentInitiatedSpace.setVisibility(View.VISIBLE);
					treatmentInitiatedSpace.setText(getResources ().getString (R.string.initiated_no_space));
					saveButton.setVisibility(View.VISIBLE);
				}
			
			}
			
			else if (followupTreatment.isChecked()){
				
				followUpDateTextView.setVisibility(View.VISIBLE);
				followUpDatePicker.setVisibility(View.VISIBLE);
				smearTestLocationTextView.setVisibility(View.VISIBLE);
				smearTestLocation.setVisibility(View.VISIBLE);
				smearResultTextView.setVisibility(View.VISIBLE);
				smearResult.setVisibility(View.VISIBLE);
				
				treatmentFollowupSpace.setVisibility(View.VISIBLE);
				saveButton.setVisibility(View.VISIBLE);
				
			}
			
			else if (outcomeTreatment.isChecked()){
				treatmentOutcomeTextView.setVisibility(View.VISIBLE);
				treatmentOutcomeGroup.setVisibility(View.VISIBLE);
			}
		}
		
	}

	@Override
	public void onCheckedChanged (CompoundButton button, boolean state)
	{
		
	}

	@Override
	public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
	{
		MySpinner spinner = (MySpinner) parent;
		
	
	}
	
	public boolean searchPatient (String id, String type)
	{
		final String ids = id;
		final String types = type;
		
	
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
							//TODO: Uncomment when live
							String[][] result = serverService.getPatientName (ids,types);
							firstNameValue = "";
							lastNameValue = "";
							if(result == null)
							   return "FAIL";
							
							firstNameValue = result[0][1];
							lastNameValue = result[1][1];
							
							return "SUCCESS";
						}
	
						@Override
						protected void onProgressUpdate (String... values)
						{
						};
	
						@Override
						protected void onPostExecute (String result)
						{
							firstName.setText(firstNameValue+" "+lastNameValue);
							
							super.onPostExecute (result);
							loading.dismiss ();
							if (!result.equals ("SUCCESS"))
							{
								Toast toast = Toast.makeText (TreatmentActivity.this, "", App.getDelay ());
								if(patientIdRadioButton.isChecked())
									toast.setText (R.string.patient_id_missing);
								else
									toast.setText (R.string.test_id_missing);
								toast.setGravity (Gravity.CENTER, 0, 0);
								toast.show ();
								return;
							}
						}
					};
				updateTask.execute ("");
				
			
			
		
		return true;
	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
