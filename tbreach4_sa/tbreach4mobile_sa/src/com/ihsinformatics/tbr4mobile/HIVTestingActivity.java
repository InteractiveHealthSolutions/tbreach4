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
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
public class HIVTestingActivity extends AbstractFragmentActivity
{
	static final int	GET_PATIENT_ID	= 1;
	// Views displayed in pages, sorted w.r.t. appearance on pager
	MyTextView			formDateTextView;
	MyButton			formDateButton;
	
	MyTextView			patientIdTextView;
	MyButton			searchPatientButton;
	MyEditText			patientId;
	MyButton			scanBarcode;

	MyTextView		    firstNameTextView;
	MyTextView		    firstName;
	MyTextView		    surnameTextView;
	MyTextView			surname;
	
	MyTextView		    consentTextView;
	
	MyRadioGroup 		consentGroup;
	MyRadioButton 		noConsent;
	MyRadioButton 		yesConsent;
	
	MyTextView		    clientSignTextView;
	
	MyRadioGroup 		clientSignGroup;
	MyRadioButton 		noClientSign;
	MyRadioButton 		yesClientSign;
	
	MyTextView			counselorSignTextView;
	
	MyRadioGroup		counselorSign;
	MyRadioButton 		noCounselorSign;
	MyRadioButton 		yesCounselorSign;
	
	MyTextView			firstHIVScreeningResultTextView;
	
	MyRadioGroup		firstHIVScreeningResult;
	MyRadioButton 		positiveFirstHIVScreeningResult;
	MyRadioButton 		negativeFirstHIVScreeningResult;
	MyRadioButton 		inconclusiveFirstHIVScreeningResult;
	MyRadioButton 		refusedFirstHIVScreeningResult;
	
	MyTextView			secondHIVScreeningResultTextView;
	
	MyRadioGroup		secondHIVScreeningResult;
	MyRadioButton 		positiveSecondHIVScreeningResult;
	MyRadioButton 		negativeSecondHIVScreeningResult;
	MyRadioButton 		inconclusiveSecondHIVScreeningResult;
	MyRadioButton 		refusedSecondHIVScreeningResult;
	
	MyTextView			elisaSampleTakenTextView;
	MyRadioGroup		elisaSampleTaken;
	MyRadioButton 		noElisaSampleTaken;
	MyRadioButton 		yesElisaSampleTaken;
	
	MyTextView			finalHIVTestResultTextView;
	
	MyRadioGroup		finalHIVTestResult;
	MyRadioButton 		positiveFinalHIVTestResult;
	MyRadioButton 		negativeFinalHIVTestResult;
	MyRadioButton 		inconclusiveFinalHIVTestResult;
	MyRadioButton 		refusedFinalHIVTestResult;
	
	MyTextView			contactConsentTextView;
	
	MyRadioGroup 		contactConsentGroup;
	MyRadioButton 		noContactConsent;
	MyRadioButton 		yesContactConsent;
	
	MyTextView 			phone1TextView;
	MyEditText 			phone1;
	MyTextView 			seprator1A;
	MyEditText 			phone1B;
	MyTextView 			seprator1B;
	MyEditText 			phone1C;
	
	MyTextView 			phone2TextView;
	MyEditText 			phone2;
	MyTextView 			seprator2A;
	MyEditText 			phone2B;
	MyTextView 			seprator2B;
	MyEditText 			phone2C;
	
	MyTextView			hivClientRefferedTextView;
	
	MyRadioGroup		clientReffered;
	MyRadioButton		noClientReffered;
	MyRadioButton 		yesClientReffered;
	
	MyTextView 			hivRefferedClinicTextView;
	MyEditText			hivRefferedClinic;
				
	MyTextView 			callerPseudonymTextView;
	MyEditText			callerPseudonym;
	
	MyTextView 			space;
	
	MyButton 			saveButton;
				
	String firstNameValue = "";
	String lastNameValue = "";
	String phone1Value = "";
	String phone2Value = "";
	private static Toast toast;
	

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
		
		toast = new Toast(context);
		
		FORM_NAME = "HIV Test Form";
		TAG = "HIVTestActivity";
		PAGE_COUNT = 8;
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
		patientIdTextView = new MyTextView (context, R.style.text, R.string.patient_id);
		searchPatientButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.search_patient, R.string.fetch_name);
		patientId = new MyEditText (context, R.string.patient_id, R.string.patient_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.idLength, false);
		patientId.setCompoundDrawables (getResources ().getDrawable (R.drawable.barcode), null, null, null);
		scanBarcode = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.scan_qr_code, R.string.scan_qr_code);
		
		firstNameTextView = new MyTextView (context, R.style.text, R.string.first_name);
		firstName = new MyTextView (context, R.style.text, R.string.empty_string);
		firstName.setTextColor(getResources ().getColor (R.color.IRDTitle));
		surnameTextView = new MyTextView (context, R.style.text, R.string.last_name);
		surname = new MyTextView (context, R.style.text, R.string.empty_string);
		surname.setTextColor(getResources ().getColor (R.color.IRDTitle));
        
		consentTextView = new MyTextView (context, R.style.text, R.string.client_consent);
		
		noConsent = new MyRadioButton(context, R.string.no, R.style.radio,R.string.no);
		yesConsent = new MyRadioButton(context, R.string.yes, R.style.radio,R.string.yes);
		consentGroup = new MyRadioGroup(context,new MyRadioButton[] { noConsent, yesConsent }, R.string.client_consent, R.style.radio, App.isLanguageRTL(),0);
		
		clientSignTextView = new MyTextView (context, R.style.text, R.string.client_sign);
		
		noClientSign = new MyRadioButton(context, R.string.no, R.style.radio,R.string.no);
		yesClientSign = new MyRadioButton(context, R.string.yes, R.style.radio,R.string.yes);
		clientSignGroup = new MyRadioGroup(context,new MyRadioButton[] { noClientSign, yesClientSign }, R.string.client_sign, R.style.radio, App.isLanguageRTL(),0);
		
		counselorSignTextView = new MyTextView (context, R.style.text, R.string.counselor_sign);
		
		noCounselorSign = new MyRadioButton(context, R.string.no, R.style.radio,R.string.no);
		yesCounselorSign = new MyRadioButton(context, R.string.yes, R.style.radio,R.string.yes);
		counselorSign = new MyRadioGroup(context,new MyRadioButton[] { noCounselorSign, yesCounselorSign }, R.string.counselor_sign, R.style.radio, App.isLanguageRTL(),0);
		
		firstHIVScreeningResultTextView = new MyTextView (context, R.style.text, R.string.first_hiv_test_result);
		
		positiveFirstHIVScreeningResult = new MyRadioButton(context, R.string.positive, R.style.radio,R.string.positive);
		negativeFirstHIVScreeningResult = new MyRadioButton(context, R.string.negative, R.style.radio,R.string.negative);
		inconclusiveFirstHIVScreeningResult = new MyRadioButton(context, R.string.inconclusive, R.style.radio,R.string.inconclusive);
		refusedFirstHIVScreeningResult = new MyRadioButton(context, R.string.refused, R.style.radio,R.string.refused);
		firstHIVScreeningResult = new MyRadioGroup(context,new MyRadioButton[] { positiveFirstHIVScreeningResult, negativeFirstHIVScreeningResult,  inconclusiveFirstHIVScreeningResult, refusedFirstHIVScreeningResult }, R.string.first_hiv_test_result, R.style.radio, App.isLanguageRTL(),1);
		
		secondHIVScreeningResultTextView = new MyTextView (context, R.style.text, R.string.second_hiv_test_result);
		
		positiveSecondHIVScreeningResult = new MyRadioButton(context, R.string.positive, R.style.radio,R.string.positive);
		negativeSecondHIVScreeningResult = new MyRadioButton(context, R.string.negative, R.style.radio,R.string.negative);
		inconclusiveSecondHIVScreeningResult = new MyRadioButton(context, R.string.inconclusive, R.style.radio,R.string.inconclusive);
		refusedSecondHIVScreeningResult = new MyRadioButton(context, R.string.refused, R.style.radio,R.string.refused);
		secondHIVScreeningResult = new MyRadioGroup(context,new MyRadioButton[] { positiveSecondHIVScreeningResult, negativeSecondHIVScreeningResult, inconclusiveSecondHIVScreeningResult, refusedSecondHIVScreeningResult }, R.string.second_hiv_test_result, R.style.radio, App.isLanguageRTL(),1);
		
		elisaSampleTakenTextView = new MyTextView (context, R.style.text, R.string.elisa_sample_taken);
		
		noElisaSampleTaken = new MyRadioButton(context, R.string.no, R.style.radio,R.string.no);
		yesElisaSampleTaken = new MyRadioButton(context, R.string.yes, R.style.radio,R.string.yes);
		elisaSampleTaken = new MyRadioGroup(context,new MyRadioButton[] { noElisaSampleTaken, yesElisaSampleTaken }, R.string.elisa_sample_taken, R.style.radio, App.isLanguageRTL(),0);

		finalHIVTestResultTextView = new MyTextView (context, R.style.text, R.string.final_hiv_test_result);
		
		positiveFinalHIVTestResult = new MyRadioButton(context, R.string.positive, R.style.radio,R.string.positive);
		negativeFinalHIVTestResult = new MyRadioButton(context, R.string.negative, R.style.radio,R.string.negative);
		inconclusiveFinalHIVTestResult = new MyRadioButton(context, R.string.inconclusive, R.style.radio,R.string.inconclusive);
		refusedFinalHIVTestResult = new MyRadioButton(context, R.string.refused, R.style.radio,R.string.refused);
		finalHIVTestResult = new MyRadioGroup(context,new MyRadioButton[] { positiveFinalHIVTestResult, negativeFinalHIVTestResult,  inconclusiveFinalHIVTestResult, refusedFinalHIVTestResult }, R.string.final_hiv_test_result, R.style.radio, App.isLanguageRTL(),1);
		
		contactConsentTextView = new MyTextView (context, R.style.text, R.string.contact_consent);
		
		noContactConsent = new MyRadioButton(context, R.string.no, R.style.radio,R.string.no);
		yesContactConsent = new MyRadioButton(context, R.string.yes, R.style.radio,R.string.yes);
		contactConsentGroup = new MyRadioGroup(context,new MyRadioButton[] { noContactConsent, yesContactConsent }, R.string.contact_consent, R.style.radio, App.isLanguageRTL(),0);
		
		phone1TextView = new MyTextView (context, R.style.text, R.string.phone1);
		phone1 = new MyEditText(context, R.string.phone1, R.string.phone1_hint_A,
				InputType.TYPE_CLASS_PHONE, R.style.edit, 3, false);
		seprator1A = new MyTextView(context, R.style.text, R.string.hash);
		phone1B = new MyEditText(context, R.string.phone1, R.string.phone1_hint_A,
				InputType.TYPE_CLASS_PHONE, R.style.edit, 3, false);
		seprator1B = new MyTextView(context, R.style.text, R.string.hash);
		phone1C = new MyEditText(context, R.string.phone1, R.string.phone1_hint_B,
				InputType.TYPE_CLASS_PHONE, R.style.edit, 4, false);
		
		phone2TextView = new MyTextView (context, R.style.text, R.string.phone2);
		phone2 = new MyEditText(context, R.string.phone1, R.string.phone1_hint_A,
				InputType.TYPE_CLASS_PHONE, R.style.edit, 3, false);
		seprator2A = new MyTextView(context, R.style.text, R.string.hash);
		phone2B = new MyEditText(context, R.string.phone1, R.string.phone1_hint_A,
				InputType.TYPE_CLASS_PHONE, R.style.edit, 3, false);
		seprator2B = new MyTextView(context, R.style.text, R.string.hash);
		phone2C = new MyEditText(context, R.string.phone1, R.string.phone1_hint_B,
				InputType.TYPE_CLASS_PHONE, R.style.edit, 4, false);
		
		hivClientRefferedTextView = new MyTextView (context, R.style.text, R.string.client_referred);
		
		noClientReffered = new MyRadioButton(context, R.string.no, R.style.radio,R.string.no);
		yesClientReffered = new MyRadioButton(context, R.string.yes, R.style.radio,R.string.yes);
		clientReffered = new MyRadioGroup(context,new MyRadioButton[] { noClientReffered, yesClientReffered }, R.string.client_referred, R.style.radio, App.isLanguageRTL(),0);
		
		hivRefferedClinicTextView = new MyTextView (context, R.style.text, R.string.referred_clinic);
		hivRefferedClinic = new MyEditText(context, R.string.referred_clinic,
				R.string.referred_clinic_hint,
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS, R.style.edit, 20,
				false);
					
		callerPseudonymTextView = new MyTextView (context, R.style.text, R.string.caller_pseudonym);
		callerPseudonym = new MyEditText(context, R.string.caller_pseudonym,
				R.string.caller_pseudonym_hint,
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS, R.style.edit, 20,
				false);
		
		space = new MyTextView (context, R.style.text, R.string.space_hiv_testing);
		
		saveButton = new MyButton(context, R.style.button,
				R.drawable.custom_button_beige, R.string.submit_form,
				R.string.submit_form);
		
		View[][] viewGroups = { 
					{formDateTextView, formDateButton, patientIdTextView, patientId, scanBarcode, firstNameTextView, firstName, surnameTextView, surname,  searchPatientButton}, 
					{consentTextView, consentGroup, clientSignTextView, clientSignGroup, counselorSignTextView, counselorSign},
					{firstHIVScreeningResultTextView, firstHIVScreeningResult},
					{secondHIVScreeningResultTextView, secondHIVScreeningResult},
					{elisaSampleTakenTextView, elisaSampleTaken, finalHIVTestResultTextView, finalHIVTestResult},
					{contactConsentTextView, contactConsentGroup, phone1TextView, phone1, seprator1A, phone1B, seprator1B, phone1C, phone2TextView, phone2, seprator2A, phone2B, seprator2B, phone2C},
					{hivClientRefferedTextView, clientReffered, hivRefferedClinicTextView, hivRefferedClinic},
					{callerPseudonymTextView, callerPseudonym, space, saveButton}
				    };
		// Create layouts and store in ArrayList
		groups = new ArrayList<ViewGroup> ();
		for (int i = 0; i < PAGE_COUNT; i++)
		{
			LinearLayout layout = new LinearLayout(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			params.setMargins(0, 5, 0, 5);
			layout.setLayoutParams(params);
			layout.setOrientation(LinearLayout.VERTICAL);  

			for (int j = 0; j < viewGroups[i].length; j++) {  

				 if (i == 5 && (j == 3 || j == 9) ){
					LinearLayout horizontalLayout = new LinearLayout(context);
					horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
					horizontalLayout.addView(viewGroups[i][j]); 
					j++;
					horizontalLayout.addView(viewGroups[i][j]);
					j++;
					horizontalLayout.addView(viewGroups[i][j]);
					j++;
					horizontalLayout.addView(viewGroups[i][j]);
					j++;
					horizontalLayout.addView(viewGroups[i][j]);
					layout.addView(horizontalLayout);
				} 
				
				else
					layout.addView(viewGroups[i][j]); 
				
			}

			ScrollView scrollView = new ScrollView(context);
			scrollView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			scrollView.addView(layout);
			groups.add(scrollView);
		}
		// Set event listeners
		navigationSeekbar.setOnSeekBarChangeListener (this);
		
		views = new View[] {patientId, firstName, surname,callerPseudonym, hivRefferedClinic, phone1, phone1B, phone1C, phone2, phone2B, phone2C,
				            noContactConsent, negativeFinalHIVTestResult, negativeSecondHIVScreeningResult, negativeFirstHIVScreeningResult,
				            yesElisaSampleTaken, yesCounselorSign, yesClientSign, yesClientReffered, yesConsent};
		
		
		View[] setListener = new View[]{
				formDateButton,firstButton,lastButton,clearButton,saveButton,scanBarcode,searchPatientButton,
				yesConsent, noConsent, positiveFirstHIVScreeningResult, negativeFirstHIVScreeningResult, inconclusiveFirstHIVScreeningResult, refusedFirstHIVScreeningResult, 
				positiveSecondHIVScreeningResult, negativeSecondHIVScreeningResult, inconclusiveSecondHIVScreeningResult, refusedSecondHIVScreeningResult,
				yesContactConsent, noContactConsent, noClientReffered, yesClientReffered
				};
		
		for (View v : setListener)
		{
			if (v instanceof Spinner)
			{
				((Spinner) v).setOnItemSelectedListener (this);
			}
			else if (v instanceof CheckBox)
			{
				((CheckBox) v).setOnCheckedChangeListener (this);
			}
			else if (v instanceof Button) {
				((Button) v).setOnClickListener(this);
			}  else if (v instanceof RadioButton) {
				((RadioButton) v).setOnClickListener(this);
			}
		}
		
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
		formDate = Calendar.getInstance ();
		updateDisplay ();
		
        firstNameValue = "";
        lastNameValue = "";
        phone1Value = "";
        phone2Value = "";
        firstName.setText(firstNameValue);
		surname.setText(lastNameValue);
		
		phone1.setText("");
		phone1B.setText("");
		phone1C.setText("");
		phone2.setText("");
		phone2B.setText("");
		phone2C.setText("");
	}

	@Override
	public void updateDisplay ()
	{
		formDateButton.setText (DateFormat.format ("dd-MMM-yyyy", formDate));
		
		if(formDate.getTime().after(new Date())){
			
			if (toast != null)
			    toast.cancel();
			
			formDateButton.setTextColor(getResources().getColor(R.color.Red));
			toast = Toast.makeText(HIVTestingActivity.this,"Form Date: "+getResources().getString(R.string.invalid_date_or_time), Toast.LENGTH_SHORT);
			toast.show();
		}
		else
			formDateButton.setTextColor(getResources().getColor(R.color.IRDTitle));
		
			
		if(yesConsent.isChecked()){
		
			if(noClientReffered.isChecked()){
				hivRefferedClinicTextView.setVisibility(View.GONE);
				hivRefferedClinic.setVisibility(View.GONE);
			}else{
				hivRefferedClinicTextView.setVisibility(View.VISIBLE);
				hivRefferedClinic.setVisibility(View.VISIBLE);
			}
					
			if(noContactConsent.isChecked()){
				
				phone1TextView.setVisibility(View.GONE);
				phone1.setVisibility(View.GONE);
				seprator1A.setVisibility(View.GONE);
				phone1B.setVisibility(View.GONE);
				seprator1B.setVisibility(View.GONE);
				phone1C.setVisibility(View.GONE);
				
				phone2TextView.setVisibility(View.GONE);
				phone2.setVisibility(View.GONE);
				seprator2A.setVisibility(View.GONE);
				phone2B.setVisibility(View.GONE);
				seprator2B.setVisibility(View.GONE);
				phone2C.setVisibility(View.GONE);
			
			}
			else{
				
				phone1TextView.setVisibility(View.VISIBLE);
				phone1.setVisibility(View.VISIBLE);
				seprator1A.setVisibility(View.VISIBLE);
				phone1B.setVisibility(View.VISIBLE);
				seprator1B.setVisibility(View.VISIBLE);
				phone1C.setVisibility(View.VISIBLE);
				
				phone2TextView.setVisibility(View.VISIBLE);
				phone2.setVisibility(View.VISIBLE);
				seprator2A.setVisibility(View.VISIBLE);
				phone2B.setVisibility(View.VISIBLE);
				seprator2B.setVisibility(View.VISIBLE);
				phone2C.setVisibility(View.VISIBLE);
				
			}
			
			if(positiveFirstHIVScreeningResult.isChecked() || inconclusiveFirstHIVScreeningResult.isChecked()){
					
					secondHIVScreeningResultTextView.setVisibility(View.VISIBLE);
					secondHIVScreeningResult.setVisibility(View.VISIBLE);
					hivTestingFirstScreeningSkipFlag = false;
					
			}else{
					
					secondHIVScreeningResultTextView.setVisibility(View.GONE);
					secondHIVScreeningResult.setVisibility(View.GONE);
					hivTestingFirstScreeningSkipFlag = true;
					
			}
				
				
			if(inconclusiveFirstHIVScreeningResult.isChecked() && inconclusiveSecondHIVScreeningResult.isChecked()){
					
					elisaSampleTakenTextView.setVisibility(View.VISIBLE);
					elisaSampleTaken.setVisibility(View.VISIBLE);
					
			}else{
					
					elisaSampleTakenTextView.setVisibility(View.GONE);
					elisaSampleTaken.setVisibility(View.GONE);
					
			}
		}
	
	}

	@Override
	public boolean validate ()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer ();
		// Validate mandatory controls
		View[] mandatory = {patientId, hivRefferedClinic, callerPseudonym, phone1, phone1B, phone1C, phone2, phone2B, phone2C};
		for (View v : mandatory)
		{
			if(v.isEnabled())
			{	
				if(v.getVisibility() == View.VISIBLE)
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
		// Validate data
		if (valid)
		{
			  
		    if (!RegexUtil.isValidId (App.get (patientId)))
			{
				valid = false;
				message.append (patientId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				patientId.setTextColor (getResources ().getColor (R.color.Red));
			}
				
		}
		// Validate range
		if (formDate.getTime ().after (new Date ()))
		{
			valid = false;
			message.append (formDateButton.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
		}
		if (!valid)
		{
			App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();
		}
		
		
		return valid;
	}
	
	public boolean searchPatient ()
	{
		final String patient = App.get(patientId);
		StringBuffer message = new StringBuffer ();
		if(!patient.equals ("")){
			if(RegexUtil.isValidId (patient))
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
							//TODO: Uncomment when live
							String[][] result = serverService.getPatientReport (patient,GET_PATIENT_ID);
							firstNameValue = "";
							lastNameValue = "";
							phone1Value = "";
							phone2Value = "";
							
							if(result == null)
							   return "FAIL";
							
							firstNameValue = result[0][1];
							lastNameValue = result[1][1];
							phone1Value = result[10][1];
							phone2Value = result[20][1];
							
							return "SUCCESS";
						}
	
						@Override
						protected void onProgressUpdate (String... values)
						{
						};
	
						@Override
						protected void onPostExecute (String result)
						{
							firstName.setText(firstNameValue);
							surname.setText(lastNameValue);
							
							if(phone1Value.length() == 10){
								phone1.setText(phone1Value.substring(0, 3));
								phone1B.setText(phone1Value.substring(3, 6));
								phone1C.setText(phone1Value.substring(6));
							}
							
							if(phone2Value.length() == 10){
								phone2.setText(phone2Value.substring(0, 3));
								phone2B.setText(phone2Value.substring(3, 6));
								phone2C.setText(phone2Value.substring(6));
							}
							
							super.onPostExecute (result);
							loading.dismiss ();
							if (!result.equals ("SUCCESS"))
							{
								Toast toast = Toast.makeText (HIVTestingActivity.this, "", App.getDelay ());
								toast.setText (R.string.patient_id_missing);
								toast.setGravity (Gravity.CENTER, 0, 0);
								toast.show ();
								return;
							}
						}
					};
				updateTask.execute ("");
				
			}
			else
			{
				message.append (patientId.getTag () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();
			}
		}
		else
		{
			message.append (patientId.getTag () + ": " + getResources ().getString (R.string.empty_data) + "\n");
			App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();		
		}
		return true;
	}

	public boolean submit ()
	{
		if (validate ())
		{
			String phoneNo = "";
			final ContentValues values = new ContentValues ();
			values.put ("formDate", App.getSqlDate (formDate));
			values.put ("location", App.getLocation());
			values.put ("patientId", App.get (patientId));
			
			final ArrayList<String[]> observations = new ArrayList<String[]> ();
			observations.add (new String[] {"Client Consent", noConsent.isChecked() ? "No" : "Yes"});
			if(clientSignTextView.getVisibility() == View.VISIBLE)
				observations.add (new String[] {"Client Signature Present", noClientSign.isChecked() ? "No" : "Yes"});
			if(counselorSignTextView.getVisibility() == View.VISIBLE)
				observations.add (new String[] {"Counselor Signature Present", noCounselorSign.isChecked() ? "No" : "Yes"});
			if(firstHIVScreeningResultTextView.getVisibility() == View.VISIBLE)
				observations.add (new String[] {"First HIV Screening Result", positiveFirstHIVScreeningResult.isChecked() ? "Positive" : (negativeFirstHIVScreeningResult.isChecked() ? "Negative" :  (inconclusiveFirstHIVScreeningResult.isChecked() ? "Inconclusive" : "Refused"))});
			if(secondHIVScreeningResultTextView.getVisibility() == View.VISIBLE)
				observations.add (new String[] {"Second HIV Screening Result", positiveSecondHIVScreeningResult.isChecked() ? "Positive" : (negativeSecondHIVScreeningResult.isChecked() ? "Negative" :  (inconclusiveSecondHIVScreeningResult.isChecked() ? "Inconclusive" : "Refused"))});
			if(elisaSampleTakenTextView.getVisibility() == View.VISIBLE)
				observations.add (new String[] {"ELISA Sample Taken", noElisaSampleTaken.isChecked() ? "No" : "Yes"});
			if(finalHIVTestResultTextView.getVisibility() == View.VISIBLE)
				observations.add (new String[] {"Final HIV Test Result", positiveFinalHIVTestResult.isChecked() ? "Positive" : (negativeFinalHIVTestResult.isChecked() ? "Negative" :  (inconclusiveFinalHIVTestResult.isChecked() ? "Inconclusive" : "Refused"))});
			if(contactConsentTextView.getVisibility() == View.VISIBLE)
				observations.add (new String[] {"Contact Consent", noContactConsent.isChecked() ? "No" : "Yes"});
			if(phone1TextView.getVisibility() == View.VISIBLE)
			{
				phoneNo = App.get(phone1) + App.get(phone1B) + App.get(phone1C);
				values.put("phone1", phoneNo);
			}
			if(phone2TextView.getVisibility() == View.VISIBLE)
			{	
				phoneNo = App.get(phone2) + App.get(phone2B) + App.get(phone2C);
				values.put("phone2", phoneNo);
			}
			if(hivClientRefferedTextView.getVisibility() == View.VISIBLE)
				observations.add (new String[] {"Client Referred", noClientReffered.isChecked() ? "No" : "Yes"});
			if(hivRefferedClinicTextView.getVisibility() == View.VISIBLE)
				observations.add (new String[] {"Referred Clinic Name", App.get(hivRefferedClinic)});
			if(callerPseudonymTextView.getVisibility() == View.VISIBLE)
			observations.add (new String[] {"Caller Pseudonym", App.get(callerPseudonym)});
		
			observations.add (new String[] {"District", App.getDistrict ()});
			
			if(App.getScreeningType ().equals("Community"))
			{
				String screeningStrategy = App.getScreeningStrategy().substring(6,App.getScreeningStrategy().length());
				observations.add (new String[] {"Screening Strategy", screeningStrategy});
			}	
			else
				observations.add (new String[] {"Facility name", App.getFacility()});
			
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
					String result = serverService.saveHIVTesting (FormType.HIV_TESTING, values, observations.toArray (new String[][] {}));
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
						App.getAlertDialog (HIVTestingActivity.this, AlertType.INFO, getResources ().getString (R.string.inserted)).show ();
						initView (views);
					}
					else
					{
						App.getAlertDialog (HIVTestingActivity.this, AlertType.ERROR, result).show ();
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
				if (RegexUtil.isValidId (str) && !RegexUtil.isNumeric (str, false))
				{
					patientId.setText (str);
				}
				else
				{
					App.getAlertDialog (this, AlertType.ERROR, patientId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data)).show ();
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
		
	}

	@Override
	public void onClick (View view)
	{
		if (view == formDateButton)
		{
			showDialog (DATE_DIALOG_ID);
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
		else if (view == scanBarcode)
		{
			Intent intent = new Intent (Barcode.BARCODE_INTENT);
			intent.putExtra (Barcode.SCAN_MODE, Barcode.QR_MODE);
			startActivityForResult (intent, Barcode.BARCODE_RESULT);
		}
		else if (view == searchPatientButton)
		{
			searchPatient ();
		}
		else if(view == noConsent || view == yesConsent){
			
			View[] v =  new View[]{clientSignTextView, clientSignGroup, counselorSignTextView, counselorSign, firstHIVScreeningResultTextView, firstHIVScreeningResult,
					secondHIVScreeningResultTextView, secondHIVScreeningResult, elisaSampleTakenTextView, elisaSampleTaken, finalHIVTestResultTextView, finalHIVTestResult,
					contactConsentTextView, contactConsentGroup, phone1TextView, phone1, seprator1A, phone1B, seprator1B, phone1C, phone2TextView, phone2, seprator2A, phone2B, seprator2B, phone2C,
					hivClientRefferedTextView, clientReffered, hivRefferedClinicTextView, hivRefferedClinic, callerPseudonym, callerPseudonymTextView};
			
			int visibility;
			
			if(noConsent.isChecked()){
				visibility = View.INVISIBLE;
				hivTestingConsentSkipFlag = true;
			}
			else{
				visibility = View.VISIBLE;
				hivTestingConsentSkipFlag = false;
			}
			
			for(View vi : v){
				
				vi.setVisibility(visibility);
			}
			
		}
		
		updateDisplay();
		
	}

	@Override
	public void onCheckedChanged (CompoundButton button, boolean state)
	{
		
	}

	@Override
	public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
	{
		
	}

	@Override
	public boolean onLongClick (View view)
	{
		/*if (view == patientId)
		{
			Intent intent = new Intent (view.getContext (), PatientSearchActivity.class);
			intent.putExtra (PatientSearchActivity.SEARCH_RESULT, "");
			startActivityForResult (intent, GET_PATIENT_ID);
			return true;
		}*/
		return false;
	}
}
