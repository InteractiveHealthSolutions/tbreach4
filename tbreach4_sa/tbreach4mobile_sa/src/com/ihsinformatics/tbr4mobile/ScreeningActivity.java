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

import java.text.SimpleDateFormat;
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
import com.ihsinformatics.tbr4mobile.custom.MySpinner;
import com.ihsinformatics.tbr4mobile.custom.MyTextView;
import com.ihsinformatics.tbr4mobile.shared.AlertType;
import com.ihsinformatics.tbr4mobile.shared.FormType;
import com.ihsinformatics.tbr4mobile.util.GPSTracker;
import com.ihsinformatics.tbr4mobile.util.RegexUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class ScreeningActivity extends AbstractFragmentActivity implements
		OnEditorActionListener {
	
	public static final int			DOB_DIALOG_ID	= 3;
	
	// Views displayed in pages, sorted w.r.t. appearance on pager
	MyTextView formDateTextView;
	MyButton formDateButton;

	MyTextView firstNameTextView;
	MyEditText firstName;
	MyTextView surnameTextView;
	MyEditText surname;
	MyTextView genderTextView;
	MyRadioGroup gender;
	MyRadioButton male;
	MyRadioButton female;

	MyTextView ageTextView;
	MyTextView age;
	
	MyTextView dateOfBirthTextView;
	MyButton dateOfBirthButton;
	Calendar dateOfBirth;
	/*MyTextView dobTextView;
	DatePicker dobPicker;
	Calendar dob;*/
	/*
	 * MyTextView LanguagesSpokenTextView; MyCheckBox english; MyCheckBox
	 * afrikaans; MyCheckBox zulu; MyCheckBox xhosa; MyCheckBox swati;
	 * MyCheckBox tswana; MyCheckBox hindiUrdu; MyCheckBox other; MyCheckBox
	 * dontKnow; MyCheckBox refuse;
	 */

	/*MyTextView mineHeadingTextView;
	MyTextView workingOnMineTextView;
	MySpinner workingOnMine;
	MyTextView yearsWorkingNowTextView;
	MyEditText yearsWorkingNow;
	MyTextView workingOnMinePreviouslyTextView;
	MySpinner workingOnMinePreviously;
	MyTextView yearsWorkingOnMinePreviouslyTextView;
	MyEditText yearsWorkingPreviously;
	MyTextView householdWorkingMineTextView;
	MySpinner householdWorkingMine;*/

	MyTextView symptomsHeadingTextView;
	MyTextView symptomQuestions;
	MyTextView coughTextView;
	MyRadioGroup coughGroup;
	MyRadioButton noCough;
	MyRadioButton yesCough;
	MyRadioButton refuseCough;
	//MySpinner cough;
	MyTextView nightSweatsTextView;
	MyRadioGroup nightSweatsGroup;
	MyRadioButton noNightSweats;
	MyRadioButton yesNightSweats;
	MyRadioButton refuseNightSweats;
	//MySpinner nightSweats;
	MyTextView weightLossTextView;
	MyRadioButton noWeightLoss;
	MyRadioButton yesWeightLoss;
	MyRadioButton refuseWeightLoss;
	MyRadioGroup weightLossGroup;
	//MySpinner weightLoss;
	MyTextView feverTextView;
	MyRadioButton noFever;
	MyRadioButton yesFever;
	MyRadioButton refuseFever;
	MyRadioGroup feverGroup;
	//MySpinner fever;
	MyTextView haemoptysisTextView;
	//MySpinner haemoptysis;
	MyRadioButton noHaemoptysis;
	MyRadioButton yesHaemoptysis;
	MyRadioButton refuseHaemoptysis;
	MyRadioGroup haemoptysisGroup;

	MyTextView summaryHeadingTextView;
	MyTextView symptomQuestion;

	MyTextView coughSymptom;
	MyEditText coughSymptomDuration;
	//MyTextView coughDays;
	MyTextView nigtSweatsSymptom;
	MyEditText nightSweatsSymptomDuration;
	//MyTextView nightSweatsDays;
	MyTextView weightLossSymptom;
	MyEditText weightLossSymptomDuration;
	//MyTextView weightLossDays;
	MyTextView feverSymptom;
	MyEditText feverSymptomDuration;
	//MyTextView feverDays;
	MyTextView haemoptysisSymptom;
	MyEditText haemoptysisSymptomDuration;
	//MyTextView haemoptysisDays;

	MyTextView screenerInstructionTextView1;
	MyTextView screenerInstructionRiskTextView;

	MyTextView riskFactorHeadingTextView;
	MyTextView contactWithTbTextView;
	
	MyRadioGroup contactWithTbGroup;
	MyRadioButton noContactWithTb;
	MyRadioButton yesContactWithTb;
	MyRadioButton refuseContactWithTb;
	
	//MySpinner contactWithTb;
	MyTextView tbTreatmentPastTextView;
	
	MyRadioGroup tbTreatmentPastGroup;
	MyRadioButton noTbTreatmentPast;
	MyRadioButton yesTbTreatmentPast;
	MyRadioButton refuseTbTreatmentPast;
	
	//MySpinner tbTreatmentPast;
	MyTextView tbTreatmentPastDurationTextView;
	MyEditText tbTreatmentPastDuration;
	MyTextView diabetesTextView;
	
	MyRadioGroup diabetesGroup;
	MyRadioButton noDiabetes;
	MyRadioButton yesDiabetes;
	MyRadioButton unknownDiabetes;
	
	//MySpinner diabetes;
	/*MyTextView familyDiabetesTextView;
	MySpinner familyDiabetes;
	MyTextView hypertensionTextView;
	MySpinner hypertension;
	MyTextView breathingShortnessTextView;
	MySpinner breathingShortness;
	MyTextView tobaccoCurrentTextView;
	MySpinner tobaccoCurrent;*/

	MyTextView hivHeadingTextView;
	MyTextView hivTestTextView;
	
	MyRadioGroup hivTestGroup;
	MyRadioButton noHivTest;
	MyRadioButton yesHivTest;
	MyRadioButton refuseHivTest;
	
	//MySpinner hivTest;
	MyTextView hivTestResultTextView;
	
	MyRadioGroup hivTestResultGroup;
	MyRadioButton positiveHivTestResult;
	MyRadioButton negativeHivTestResult;
	MyRadioButton unknownHivTestResult;
	
	//MySpinner hivTestResult;
	MyTextView hivTestNewTextView;
	
	MyRadioGroup hivTestNewGroup;
	MyRadioButton yesHivTestNew;
	MyRadioButton noHivTestNew;
	MyRadioButton refuseHivTestNew;
	
	//MySpinner hivTestNew;

	MyTextView screenerInstructionTextView2;
	MyTextView screenerInstructionOneTextView;
	MyTextView screenerInstructionTwoTextView;
	
	MyTextView patientReferredTextView;
	
	MyRadioGroup patientReferredGroup;
	MyRadioButton noPatientReferred;
	MyRadioButton yesPatientReferred;
	
	//MySpinner  patientReferred;

	MyTextView screenerInstructionTextView3;
	MyCheckBox noPatientInformation;
	MyCheckBox yesPatientInformation;

	MyTextView patientIdTextView;
	MyEditText patientId;
	MyButton scanBarcode;

	MyTextView providePhone1TextView;
	MyRadioGroup providePhone1;
	MyRadioButton noPhone1;
	MyRadioButton yesPhone1;
	MyTextView phone1TextView;
	MyEditText phone1;
	MyTextView phone1OwnerTextView;
	
	MyRadioGroup phone1OwnerGroup;
	MyRadioButton myselfPhone1Owner;
	MyRadioButton otherPhone1Owner;
	
	//MySpinner phone1Owner;
	MyTextView phone1OtherOwnerTextView;
	MyEditText phone1OtherOwner;

	MyTextView providePhone2TextView;
	MyRadioGroup providePhone2;
	MyRadioButton noPhone2;
	MyRadioButton yesPhone2;
	MyTextView phone2TextView;
	MyEditText phone2;
	MyTextView phone2OwnerTextView;
	
	MyRadioGroup phone2OwnerGroup;
	MyRadioButton myselfPhone2Owner;
	MyRadioButton otherPhone2Owner;
	
	//MySpinner phone2Owner;
	MyTextView phone2OtherOwnerTextView;
	MyEditText phone2OtherOwner;

	MyTextView physicalAddressTextView;
	MyEditText physicalAddress;
	MyTextView townAddressTextView;
	MyEditText townAddress;
	MyTextView landmarkAddressTextView;
	MyEditText landmarkAddress;
	MyTextView sputumVideoInstructionTextView;
	
	MyRadioGroup sputumVideoInstructionGroup;
	MyRadioButton noSputumVideoInstruction;
	MyRadioButton yesSputumVideoInstruction;
	MyRadioButton refuseSputumVideoInstruction;
	MyRadioButton unavailableSputumVideoInstruction;
	
	//MySpinner sputumVideoInstruction;
	
	MyTextView screeningSpace;
	MyTextView instruction;
	
	MyButton saveButton;

	/**
	 * Subclass representing Fragment for screening form
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	@SuppressLint("ValidFragment")
	class ScreeningFragment extends Fragment {
		int currentPage;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Bundle data = getArguments();
			currentPage = data.getInt("current_page", 0);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Return a layout of views from pre-filled ArrayList of groups
			if (currentPage != 0 && groups.size() != 0)
				return groups.get(currentPage - 1);
			else
				return null;
		}
	}

	/**
	 * Subclass for Pager Adapter. Uses Screening subclass as items
	 * 
	 * @author owais.hussain@irdresearch.org
	 */
	class ScreeningPagerAdapter extends FragmentPagerAdapter {
		/** Constructor of the class */
		public ScreeningPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		/** This method will be invoked when a page is requested to create */
		@Override
		public Fragment getItem(int arg0) {
			ScreeningFragment fragment = new ScreeningFragment();
			Bundle data = new Bundle();
			data.putInt("current_page", arg0 + 1);
			fragment.setArguments(data);
			return fragment;
		}

		/** Returns the number of pages */
		@Override
		public int getCount() {
			return PAGE_COUNT;
		}
	}

	@Override
	public void createViews(Context context) {
		FORM_NAME = "Screening";
		TAG = "ScreeningActivity";
		PAGE_COUNT = 13;
		pager = (ViewPager) findViewById(R.template_id.pager);
		navigationSeekbar.setMax(PAGE_COUNT - 1);
		navigatorLayout = (LinearLayout) findViewById(R.template_id.navigatorLayout);
		// If the form consists only of single page, then hide the
		// navigatorLayout
		if (PAGE_COUNT < 2) {
			navigatorLayout.setVisibility(View.GONE);
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		ScreeningPagerAdapter pagerAdapter = new ScreeningPagerAdapter(
				fragmentManager);
		pager.setAdapter(pagerAdapter);
		pager.setOffscreenPageLimit(PAGE_COUNT);
		// Create views for pages
		formDateTextView = new MyTextView(context, R.style.text,
				R.string.form_date);
		formDateButton = new MyButton(context, R.style.button,
				R.drawable.custom_button_beige, R.string.form_date,
				R.string.form_date);

		// Patient's Name
		firstNameTextView = new MyTextView(context, R.style.text,
				R.string.first_name);
		firstName = new MyEditText(context, R.string.first_name,
				R.string.first_name_hint,
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS, R.style.edit, 20,
				false);
		surnameTextView = new MyTextView(context, R.style.text,
				R.string.last_name);
		surname = new MyEditText(context, R.string.last_name,
				R.string.last_name_hint,
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS, R.style.edit, 20,
				false);

		// Demographics
		genderTextView = new MyTextView(context, R.style.text, R.string.gender);
		male = new MyRadioButton(context, R.string.male, R.style.radio,
				R.string.male);
		female = new MyRadioButton(context, R.string.female, R.style.radio,
				R.string.female);
		gender = new MyRadioGroup(context,
				new MyRadioButton[] { male, female }, R.string.gender,
				R.style.radio, App.isLanguageRTL(),1);

		dateOfBirthTextView = new MyTextView(context, R.style.text,
				R.string.dob);
		dateOfBirthButton = new MyButton(context, R.style.button,
				R.drawable.custom_button_beige, R.string.dob,
				R.string.dob);
		
		/*dobTextView = new MyTextView(context, R.style.text, R.string.dob);
		dobPicker = new DatePicker(context);
		ArrayList<View> touchables = dobPicker.getTouchables();
		for (int i = 0; i < touchables.size(); i++) {
			if (i == 2 || i == 5 || i == 8)
				touchables.get(i).setBackgroundResource(
						R.drawable.numberpicker_down_normal);
			else if (i == 0 || i == 3 || i == 6)
				touchables.get(i).setBackgroundResource(
						R.drawable.numberpicker_up_normal);
			else
				touchables.get(i).setBackgroundResource(
						R.drawable.custom_button_beige);
		}
		dob = Calendar.getInstance();
		dobPicker.init(dob.get(Calendar.YEAR), dob.get(Calendar.MONTH),
				dob.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
	   
					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {


						Calendar selectedCal = Calendar.getInstance();
	                    selectedCal.set(year, monthOfYear, dayOfMonth);

	                    long selectedMilli = selectedCal.getTimeInMillis();

	                    Date datePickerDate = new Date(selectedMilli);
	                    if (datePickerDate.after(new Date())) {
	                    	final Calendar cal = Calendar.getInstance();
	                    	dobPicker.updateDate(cal.get(Calendar.YEAR),
	                                cal.get(Calendar.MONTH),
	                                cal.get(Calendar.DAY_OF_MONTH));
	                    	} 
						else{			
							dob.set(Calendar.YEAR, year);
							dob.set(Calendar.MONTH, monthOfYear);
							dob.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	
							int diff = formDate.get(Calendar.YEAR)
									- dob.get(Calendar.YEAR);
							if (dob.get(Calendar.MONTH) > formDate
									.get(Calendar.MONTH)
									|| (dob.get(Calendar.MONTH) == formDate
											.get(Calendar.MONTH) && dob
											.get(Calendar.DATE) > formDate
											.get(Calendar.DATE))) {
								diff--;
							}
	
							age.setText(Integer.toString(diff));
						}
						
					}
				});*/
		
		ageTextView = new MyTextView(context, R.style.text, R.string.age);
		age = new MyTextView(context, R.style.text, R.string.zero);
		
		/*
		 * LanguagesSpokenTextView = new MyTextView(context, R.style.text,
		 * R.string.language_spoken);
		 * 
		 * english = new MyCheckBox(context, R.string.english, R.style.edit,
		 * R.string.english, false); afrikaans = new MyCheckBox(context,
		 * R.string.afrikaans, R.style.edit, R.string.afrikaans, false); zulu =
		 * new MyCheckBox(context, R.string.zulu, R.style.edit, R.string.zulu,
		 * false); xhosa = new MyCheckBox(context, R.string.xhosa, R.style.edit,
		 * R.string.xhosa, false); swati = new MyCheckBox(context,
		 * R.string.swati, R.style.edit, R.string.swati, false); tswana = new
		 * MyCheckBox(context, R.string.tswana, R.style.edit, R.string.tswana,
		 * false); hindiUrdu = new MyCheckBox(context, R.string.hindiUrdu,
		 * R.style.edit, R.string.hindiUrdu, false); other = new
		 * MyCheckBox(context, R.string.other, R.style.edit, R.string.other,
		 * false); dontKnow = new MyCheckBox(context, R.string.dontKnow,
		 * R.style.edit, R.string.dontKnow, false); refuse = new
		 * MyCheckBox(context, R.string.refused, R.style.edit, R.string.refused,
		 * false);
		 */

		// mine related
		/*mineHeadingTextView = new MyTextView(context, R.style.text,
				R.string.mineHeading);
		mineHeadingTextView.setTypeface(null, Typeface.BOLD);
		workingOnMineTextView = new MyTextView(context, R.style.text,
				R.string.workNowMine);
		workingOnMine = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.workNowMine, R.string.option_hint);
		yearsWorkingNowTextView = new MyTextView(context, R.style.text,
				R.string.yearWorkingNow);
		yearsWorkingNow = new MyEditText(context, R.string.yearWorkingNow,
				R.string.yearWorking_hint, InputType.TYPE_CLASS_NUMBER,
				R.style.edit, 2, false);
		workingOnMinePreviouslyTextView = new MyTextView(context, R.style.text,
				R.string.workMinePreviously);
		workingOnMinePreviously = new MySpinner(context, getResources()
				.getStringArray(R.array.options), R.string.workMinePreviously,
				R.string.option_hint);
		yearsWorkingOnMinePreviouslyTextView = new MyTextView(context,
				R.style.text, R.string.yearWorkingPreviously);
		yearsWorkingPreviously = new MyEditText(context,
				R.string.yearWorkingPreviously, R.string.yearWorking_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 2, false);
		householdWorkingMineTextView = new MyTextView(context, R.style.text,
				R.string.householdWorkMine);
		householdWorkingMine = new MySpinner(context, getResources()
				.getStringArray(R.array.options), R.string.householdWorkMine,
				R.string.option_hint);*/

		// Symptoms
		symptomsHeadingTextView = new MyTextView(context, R.style.text,
				R.string.symptomsHeading);
		symptomsHeadingTextView.setTypeface(null, Typeface.BOLD);
		symptomQuestions = new MyTextView(context, R.style.text,
				R.string.symptoms_question);
		coughTextView = new MyTextView(context, R.style.text, R.string.cough);
		coughTextView.setTextSize(20);
		
		noCough = new MyRadioButton(context, R.string.n, R.style.radio,
				R.string.n);
		yesCough = new MyRadioButton(context, R.string.y, R.style.radio,
				R.string.y);
		refuseCough = new MyRadioButton(context, R.string.r, R.style.radio,
				R.string.r);
		coughGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noCough, yesCough, refuseCough }, R.string.cough,
				R.style.radio, App.isLanguageRTL(),0);
		
		//cough = new MySpinner(context, getResources().getStringArray(R.array.options), R.string.cough, R.string.option_hint);
		//coughDays = new MyTextView(context, R.style.text, R.string.day);
		nightSweatsTextView = new MyTextView(context, R.style.text,
				R.string.night_sweats);
		nightSweatsTextView.setTextSize(20);
		
		noNightSweats = new MyRadioButton(context, R.string.n, R.style.radio,
				R.string.n);
		yesNightSweats = new MyRadioButton(context, R.string.y, R.style.radio,
				R.string.y);
		refuseNightSweats = new MyRadioButton(context, R.string.r, R.style.radio,
				R.string.r);
		
		nightSweatsGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noNightSweats, yesNightSweats, refuseNightSweats }, R.string.night_sweats,
				R.style.radio, App.isLanguageRTL(),0);
		//nightSweats = new MySpinner(context, getResources().getStringArray(R.array.options), R.string.night_sweats, R.string.option_hint);
		//nightSweatsDays = new MyTextView(context, R.style.text, R.string.day);
		weightLossTextView = new MyTextView(context, R.style.text,
				R.string.weight_loss);
		weightLossTextView.setTextSize(20);
		
		noWeightLoss = new MyRadioButton(context, R.string.n, R.style.radio,
				R.string.n);
		yesWeightLoss = new MyRadioButton(context, R.string.y, R.style.radio,
				R.string.y);
		refuseWeightLoss = new MyRadioButton(context, R.string.r, R.style.radio,
				R.string.r);
		
		weightLossGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noWeightLoss , yesWeightLoss , refuseWeightLoss  }, R.string.weight_loss,
				R.style.radio, App.isLanguageRTL(),0);
		
		//weightLoss = new MySpinner(context, getResources().getStringArray(R.array.options), R.string.weight_loss, R.string.option_hint);
		//weightLossDays = new MyTextView(context, R.style.text, R.string.day);
		feverTextView = new MyTextView(context, R.style.text, R.string.fever);
		feverTextView.setTextSize(20);
		
		noFever = new MyRadioButton(context, R.string.n, R.style.radio,
				R.string.n);
		yesFever = new MyRadioButton(context, R.string.y, R.style.radio,
				R.string.y);
		refuseFever = new MyRadioButton(context, R.string.r, R.style.radio,
				R.string.r);
		
		feverGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noFever , yesFever , refuseFever }, R.string.fever,
				R.style.radio, App.isLanguageRTL(),0);
		
		//fever = new MySpinner(context, getResources().getStringArray(R.array.options), R.string.fever, R.string.option_hint);
		//feverDays = new MyTextView(context, R.style.text, R.string.day);
		haemoptysisTextView = new MyTextView(context, R.style.text,
				R.string.haemoptysis);
		haemoptysisTextView.setTextSize(20);
		
		noHaemoptysis = new MyRadioButton(context, R.string.n, R.style.radio,
				R.string.n);
		yesHaemoptysis = new MyRadioButton(context, R.string.y, R.style.radio,
				R.string.y);
		refuseHaemoptysis = new MyRadioButton(context, R.string.r, R.style.radio,
				R.string.r);
		
		haemoptysisGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noHaemoptysis , yesHaemoptysis , refuseHaemoptysis }, R.string.fever,
				R.style.radio, App.isLanguageRTL(),0);
		
		//haemoptysis = new MySpinner(context, getResources().getStringArray(R.array.options), R.string.haemoptysis, R.string.option_hint);
		//haemoptysisDays = new MyTextView(context, R.style.text, R.string.day);
		
		instruction = new MyTextView(context, R.style.text,
				R.string.instructions);
		instruction.setTextSize(18); 
		summaryHeadingTextView = new MyTextView(context, R.style.text,
				R.string.summary);
		summaryHeadingTextView.setTypeface(null, Typeface.BOLD);
		symptomQuestion = new MyTextView(context, R.style.text,
				R.string.symptom_question);
		coughSymptom = new MyTextView(context, R.style.text,
				R.string.cough_checkbox);
		coughSymptomDuration = new MyEditText(context,
				R.string.symptoms_period, R.string.symptoms_period_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);
		nigtSweatsSymptom = new MyTextView(context, R.style.text,
				R.string.night_sweats_checkbox);
		nightSweatsSymptomDuration = new MyEditText(context,
				R.string.symptoms_period, R.string.symptoms_period_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);
		weightLossSymptom = new MyTextView(context, R.style.text,
				R.string.weight_loss_checkbox);
		weightLossSymptomDuration = new MyEditText(context,
				R.string.symptoms_period, R.string.symptoms_period_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);
		feverSymptom = new MyTextView(context, R.style.text,
				R.string.fever_checkbox);
		feverSymptomDuration = new MyEditText(context,
				R.string.symptoms_period, R.string.symptoms_period_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);
		haemoptysisSymptom = new MyTextView(context, R.style.text,
				R.string.haemoptysis_checkbox);
		haemoptysisSymptomDuration = new MyEditText(context,
				R.string.symptoms_period, R.string.symptoms_period_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);

		/*
		 * symptomsPeriodTextView = new MyTextView(context, R.style.text,
		 * R.string.symptoms_period); symptomsPeriod = new MyEditText(context,
		 * R.string.symptoms_period, R.string.symptoms_period_hint,
		 * InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);
		 */

		// Risk Factor

		screenerInstructionTextView1 = new MyTextView(context, R.style.text,
				R.string.screenerInstructionHeading);
		screenerInstructionTextView1.setTextColor(getResources().getColor(
				R.color.GreenShade));
		screenerInstructionTextView1.setTypeface(null, Typeface.BOLD);
		screenerInstructionRiskTextView = new MyTextView(context, R.style.text,
				R.string.risk_instruction);
		screenerInstructionRiskTextView.setGravity(Gravity.CENTER);
		screenerInstructionRiskTextView.setTextColor(getResources().getColor(
				R.color.Green));
		screenerInstructionRiskTextView
				.setBackgroundResource(R.color.White);
		screenerInstructionRiskTextView.setTypeface(null, Typeface.ITALIC);

		riskFactorHeadingTextView = new MyTextView(context, R.style.text,
				R.string.riskFactorHeading);
		riskFactorHeadingTextView.setTypeface(null, Typeface.BOLD);
		contactWithTbTextView = new MyTextView(context, R.style.text,
				R.string.contact_with_tb);
		
		noContactWithTb = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesContactWithTb = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		refuseContactWithTb = new MyRadioButton(context, R.string.refuse, R.style.radio,
				R.string.refuse);
		
		contactWithTbGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noContactWithTb, yesContactWithTb, refuseContactWithTb }, R.string.contact_with_tb,
				R.style.radio, App.isLanguageRTL(),0);
		
		/*contactWithTb = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.contact_with_tb,
				R.string.option_hint);*/
		tbTreatmentPastTextView = new MyTextView(context, R.style.text,
				R.string.tb_treatment_previously);
		
		noTbTreatmentPast = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesTbTreatmentPast = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		refuseTbTreatmentPast = new MyRadioButton(context, R.string.refuse, R.style.radio,
				R.string.refuse);
		
		tbTreatmentPastGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noTbTreatmentPast, yesTbTreatmentPast, refuseTbTreatmentPast }, R.string.tb_treatment_previously,
				R.style.radio, App.isLanguageRTL(),0);
		
		/*tbTreatmentPast = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.tb_treatment_previously,
				R.string.option_hint);*/
		tbTreatmentPastDurationTextView = new MyTextView(context, R.style.text,
				R.string.tb_treatment_duration);
		tbTreatmentPastDuration = new MyEditText(context,
				R.string.tb_treatment_duration,
				R.string.tb_treatment_duration_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 2, false);

		diabetesTextView = new MyTextView(context, R.style.text,
				R.string.diabetes);
		
		noDiabetes = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesDiabetes = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		unknownDiabetes = new MyRadioButton(context, R.string.unknown, R.style.radio,
				R.string.unknown);
		
		diabetesGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noDiabetes, yesDiabetes, unknownDiabetes }, R.string.diabetes,
				R.style.radio, App.isLanguageRTL(),0);
		
		/*diabetes = new MySpinner(context, getResources().getStringArray(
				R.array.option), R.string.diabetes, R.string.option_hint);*/
		/*familyDiabetesTextView = new MyTextView(context, R.style.text,
				R.string.family_diabetes);
		familyDiabetes = new MySpinner(context, getResources().getStringArray(
				R.array.option), R.string.family_diabetes, R.string.option_hint);
		hypertensionTextView = new MyTextView(context, R.style.text,
				R.string.hypertension);
		hypertension = new MySpinner(context, getResources().getStringArray(
				R.array.option), R.string.hypertension, R.string.option_hint);

		breathingShortnessTextView = new MyTextView(context, R.style.text,
				R.string.breathing_shortness);
		breathingShortness = new MySpinner(context, getResources()
				.getStringArray(R.array.options), R.string.breathing_shortness,
				R.string.option_hint);
		tobaccoCurrentTextView = new MyTextView(context, R.style.text,
				R.string.tobacco_current);
		tobaccoCurrent = new MySpinner(context, getResources().getStringArray(
				R.array.tobacco_durations), R.string.tobacco_current,
				R.string.option_hint);*/

		// HIV related
		hivHeadingTextView = new MyTextView(context, R.style.text,
				R.string.hivHeading);
		hivHeadingTextView.setTypeface(null, Typeface.BOLD);
		hivTestTextView = new MyTextView(context, R.style.text,
				R.string.hivTest);
		
		noHivTest = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesHivTest = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		refuseHivTest = new MyRadioButton(context, R.string.refuse, R.style.radio,
				R.string.refuse);
		
		hivTestGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noHivTest , yesHivTest , refuseHivTest }, R.string.hivTest,
				R.style.radio, App.isLanguageRTL(),0);
		
		
		/*hivTest = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.hivTest, R.string.option_hint);*/
		hivTestResultTextView = new MyTextView(context, R.style.text,
				R.string.hivTestResult);
		
		positiveHivTestResult = new MyRadioButton(context, R.string.positive, R.style.radio,
				R.string.positive);
		negativeHivTestResult = new MyRadioButton(context, R.string.negative, R.style.radio,
				R.string.negative);
		unknownHivTestResult = new MyRadioButton(context, R.string.unknown, R.style.radio,
				R.string.unknown);
		
		hivTestResultGroup = new MyRadioGroup(context,
				new MyRadioButton[] { positiveHivTestResult , negativeHivTestResult , unknownHivTestResult }, R.string.hivTestResult,
				R.style.radio, App.isLanguageRTL(),1);
		
		
		/*hivTestResult = new MySpinner(context, getResources().getStringArray(
				R.array.hiv_result), R.string.hivTestResult, R.string.option_hint);*/
		hivTestNewTextView = new MyTextView(context, R.style.text,
				R.string.hivTestNew);
		
		noHivTestNew = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesHivTestNew = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		refuseHivTestNew = new MyRadioButton(context, R.string.refuse, R.style.radio,
				R.string.refuse);
		
		hivTestNewGroup = new MyRadioGroup(context,
				new MyRadioButton[] { yesHivTestNew , noHivTestNew , refuseHivTestNew }, R.string.hivTestNew,
				R.style.radio, App.isLanguageRTL(),0);
		
		/*hivTestNew = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.hivTestNew, R.string.option_hint);*/

		// Screener Instruction
		screenerInstructionTextView2 = new MyTextView(context, R.style.text,
				R.string.screenerInstructionHeading);
		screenerInstructionTextView2.setTextColor(getResources().getColor(
				R.color.GreenShade));
		screenerInstructionTextView2.setTypeface(null, Typeface.BOLD);
		screenerInstructionOneTextView = new MyTextView(context, R.style.text,
				R.string.screener_instruction_one_yes);
		screenerInstructionOneTextView.setGravity(Gravity.CENTER);
		screenerInstructionOneTextView.setTextColor(getResources().getColor(
				R.color.Green));
		screenerInstructionOneTextView.setBackgroundResource(R.color.White);
		screenerInstructionOneTextView.setTypeface(null, Typeface.ITALIC);

		screenerInstructionTextView3 = new MyTextView(context, R.style.text,
				R.string.screenerInstructionHeading);
		screenerInstructionTextView3.setTextColor(getResources().getColor(
				R.color.GreenShade));
		screenerInstructionTextView3.setTypeface(null, Typeface.BOLD);
		screenerInstructionTwoTextView = new MyTextView(context, R.style.text,
				R.string.screener_instruction_two);
		screenerInstructionTwoTextView.setGravity(Gravity.CENTER);
		screenerInstructionTwoTextView.setTextColor(getResources().getColor(
				R.color.Green));
		screenerInstructionTwoTextView.setBackgroundResource(R.color.White);
		screenerInstructionTwoTextView.setTypeface(null, Typeface.ITALIC);
		
		patientReferredTextView = new MyTextView(context, R.style.text,
				R.string.patient_referred);
		
		noPatientReferred = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesPatientReferred = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		
		patientReferredGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noPatientReferred , yesPatientReferred  },  R.string.patient_referred,
				R.style.radio, App.isLanguageRTL(),0);
		
		/*patientReferred = new MySpinner(context, getResources().getStringArray(
				R.array.option_yes_no), R.string.patient_referred,
				R.string.option_hint);*/
		
		
		noPatientInformation = new MyCheckBox(context, R.string.no,
				R.style.edit, R.string.no, true);
		yesPatientInformation = new MyCheckBox(context, R.string.yes,
				R.style.edit, R.string.yes, false);
		noPatientInformation.setClickable(false);
		yesPatientInformation.setClickable(false);

		// Contact Information

		patientIdTextView = new MyTextView(context, R.style.text,
				R.string.patient_id);
		patientId = new MyEditText(context, R.string.patient_id,
				R.string.patient_id_hint, InputType.TYPE_CLASS_TEXT,
				R.style.edit, RegexUtil.idLength, false);
		scanBarcode = new MyButton(context, R.style.button,
				R.drawable.custom_button_beige, R.string.scan_qr_code,
				R.string.scan_qr_code);

		providePhone1TextView = new MyTextView(context, R.style.text,
				R.string.phone1_provided);
		noPhone1 = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesPhone1 = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		providePhone1 = new MyRadioGroup(context, new MyRadioButton[] {
				noPhone1, yesPhone1 }, R.string.phone_1, R.style.radio,
				App.isLanguageRTL(),0);
		phone1TextView = new MyTextView(context, R.style.text, R.string.phone_1);
		phone1 = new MyEditText(context, R.string.phone1, R.string.phone1_hint,
				InputType.TYPE_CLASS_PHONE, R.style.edit, 20, false);
		phone1OwnerTextView = new MyTextView(context, R.style.text,
				R.string.phone1_owner);
		
		myselfPhone1Owner = new MyRadioButton(context, R.string.myself, R.style.radio,
				R.string.myself);
		otherPhone1Owner = new MyRadioButton(context, R.string.someone_else, R.style.radio,
				R.string.someone_else);
		
		phone1OwnerGroup = new MyRadioGroup(context,
				new MyRadioButton[] { myselfPhone1Owner, otherPhone1Owner }, R.string.phone1_owner,
				R.style.radio, App.isLanguageRTL(),0);
		
		
		/*phone1Owner = new MySpinner(context, getResources().getStringArray(
				R.array.owner_options), R.string.phone1_owner,
				R.string.option_hint);*/
		phone1OtherOwnerTextView = new MyTextView(context, R.style.text,
				R.string.phone1_other_owner);
		phone1OtherOwner = new MyEditText(context, R.string.phone2_owner,
				R.string.owner_name_hint,
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 11,
				false);

		providePhone2TextView = new MyTextView(context, R.style.text,
				R.string.phone2_provided);
		noPhone2 = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesPhone2 = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		providePhone2 = new MyRadioGroup(context, new MyRadioButton[] {
				noPhone2, yesPhone2 }, R.string.phone_2, R.style.radio,
				App.isLanguageRTL(),0);
		phone2TextView = new MyTextView(context, R.style.text, R.string.phone_2);
		phone2 = new MyEditText(context, R.string.phone2, R.string.phone1_hint,
				InputType.TYPE_CLASS_PHONE, R.style.edit, 10, false);
		phone2OwnerTextView = new MyTextView(context, R.style.text,
				R.string.phone2_owner);
		
		
		myselfPhone2Owner = new MyRadioButton(context, R.string.myself, R.style.radio,
				R.string.myself);
		otherPhone2Owner = new MyRadioButton(context, R.string.someone_else, R.style.radio,
				R.string.someone_else);
		
		phone2OwnerGroup = new MyRadioGroup(context,
				new MyRadioButton[] { myselfPhone2Owner, otherPhone2Owner }, R.string.phone2_owner,
				R.style.radio, App.isLanguageRTL(),0);
		
		
		/*phone2Owner = new MySpinner(context, getResources().getStringArray(
				R.array.owner_options), R.string.phone2_owner,
				R.string.option_hint);*/
		phone2OtherOwnerTextView = new MyTextView(context, R.style.text,
				R.string.phone2_other_owner);
		phone2OtherOwner = new MyEditText(context, R.string.phone2_owner,
				R.string.owner_name_hint,
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 11,
				false);

		physicalAddressTextView = new MyTextView(context, R.style.text,
				R.string.physical_address);
		physicalAddress = new MyEditText(context, R.string.physical_address,
				R.string.physical_address_hint, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
				R.style.edit, 225, false);
		townAddressTextView = new MyTextView(context, R.style.text,
				R.string.town_address);
		townAddress = new MyEditText(context, R.string.town_address,
				R.string.town_address_hint, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
				R.style.edit, 225, false);
		landmarkAddressTextView = new MyTextView(context, R.style.text,
				R.string.landmark_address);
		landmarkAddress = new MyEditText(context, R.string.landmark_address,
				R.string.landmark_address_hint, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
				R.style.edit, 225, false);
		sputumVideoInstructionTextView = new MyTextView(context, R.style.text,
				R.string.sputum_video);
		
		noSputumVideoInstruction = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesSputumVideoInstruction = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		refuseSputumVideoInstruction = new MyRadioButton(context, R.string.refuse, R.style.radio,
				R.string.refuse);
		unavailableSputumVideoInstruction = new MyRadioButton(context, R.string.video_unavailable, R.style.radio,
				R.string.video_unavailable);
		
		sputumVideoInstructionGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noSputumVideoInstruction, yesSputumVideoInstruction, refuseSputumVideoInstruction, unavailableSputumVideoInstruction }, R.string.sputum_video,
				R.style.radio, App.isLanguageRTL(),1);
		
		/*sputumVideoInstruction = new MySpinner(context, getResources()
				.getStringArray(R.array.sputum_video_options),
				R.string.phone2_owner, R.string.option_hint);*/
		
		screeningSpace = new MyTextView(context, R.style.text,
				R.string.screening_space);
		
		saveButton = new MyButton(context, R.style.button,
				R.drawable.custom_button_beige, R.string.submit_form,
				R.string.submit_form);

		View[][] viewGroups = {
				{ formDateTextView, formDateButton, firstNameTextView,
						firstName, surnameTextView, surname, genderTextView,
						gender },
				{ dateOfBirthTextView, dateOfBirthButton, /*dobTextView, dobPicker,*/ ageTextView, age },
				/*
				 * { LanguagesSpokenTextView, english, afrikaans, zulu, xhosa,
				 * swati, tswana, hindiUrdu, other, dontKnow, refuse },
				 */
				/*{ mineHeadingTextView, workingOnMineTextView, workingOnMine,
						yearsWorkingNowTextView, yearsWorkingNow },
				{ workingOnMinePreviouslyTextView, workingOnMinePreviously,
						yearsWorkingOnMinePreviouslyTextView,
						yearsWorkingPreviously, householdWorkingMineTextView,
						householdWorkingMine },*/
				{ symptomsHeadingTextView, symptomQuestions, instruction, coughTextView, coughGroup, /*cough,*/
						nightSweatsTextView, nightSweatsGroup, /*nightSweats,*/ weightLossTextView, weightLossGroup/*,
						weightLoss*/,feverTextView, feverGroup, /*fever,*/ haemoptysisTextView, haemoptysisGroup , /*haemoptysis*/ },
				{ summaryHeadingTextView, symptomQuestion, coughSymptom,
						coughSymptomDuration, /*coughDays,*/ nigtSweatsSymptom,
						nightSweatsSymptomDuration, /*nightSweatsDays,*/ weightLossSymptom,
						weightLossSymptomDuration, /*weightLossDays,*/ feverSymptom,
						feverSymptomDuration, /*feverDays,*/ haemoptysisSymptom,
						haemoptysisSymptomDuration,/* haemoptysisDays*/ },
				{ screenerInstructionTextView1,
						screenerInstructionRiskTextView,
						riskFactorHeadingTextView, contactWithTbTextView, contactWithTbGroup
						/*contactWithTb,*/  },
				{ tbTreatmentPastTextView, tbTreatmentPastGroup /*,tbTreatmentPast*/, tbTreatmentPastDurationTextView, tbTreatmentPastDuration,
						diabetesTextView, diabetesGroup/*, diabetes, familyDiabetesTextView,
						familyDiabetes */},
				/*{ hypertensionTextView, hypertension,
						breathingShortnessTextView, breathingShortness,
						tobaccoCurrentTextView, tobaccoCurrent },*/
				{ hivHeadingTextView, hivTestTextView, hivTestGroup, /*hivTest,*/
						hivTestResultTextView, hivTestResultGroup /*hivTestResult,*/
						/*, hivTestNew */},
				{ hivTestNewTextView, hivTestNewGroup, screenerInstructionTextView2, screenerInstructionOneTextView,patientReferredTextView, patientReferredGroup/*, patientReferred*/ },
				{ screenerInstructionTextView3, screenerInstructionTwoTextView,
						noPatientInformation, yesPatientInformation,
						patientIdTextView, patientId, scanBarcode },
				{ providePhone1TextView, providePhone1, phone1TextView, phone1,
						phone1OwnerTextView, phone1OwnerGroup ,/* phone1Owner,*/ phone1OtherOwnerTextView ,phone1OtherOwner },
				{ providePhone2TextView, providePhone2, phone2TextView, phone2,
						phone2OwnerTextView, phone2OwnerGroup , /*phone2Owner,*/ phone2OtherOwnerTextView, phone2OtherOwner },
				{ physicalAddressTextView, physicalAddress,
						townAddressTextView, townAddress,
						landmarkAddressTextView, landmarkAddress },
				{ sputumVideoInstructionTextView, sputumVideoInstructionGroup , /*sputumVideoInstruction, screeningSpace,*/ saveButton },
				};
		// Create layouts and store in ArrayList
		groups = new ArrayList<ViewGroup>();
		for (int i = 0; i < PAGE_COUNT; i++) {
			LinearLayout layout = new LinearLayout(context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			params.setMargins(0, 5, 0, 5);
			layout.setLayoutParams(params);
			layout.setOrientation(LinearLayout.VERTICAL);

			for (int j = 0; j < viewGroups[i].length; j++) {

				if (i == 3 || i == 2) {
					LinearLayout horizontalLayout = new LinearLayout(context);
					horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
					if (j == 0 || j == 1) {
						horizontalLayout.addView(viewGroups[i][j]);
					} else if (i == 2 && j == 2){
						horizontalLayout.addView(viewGroups[i][j]);	
					}					
					else {
						horizontalLayout.addView(viewGroups[i][j]);
						j++;
						horizontalLayout.addView(viewGroups[i][j]);
						/*j++;
						horizontalLayout.addView(viewGroups[i][j]);*/
					}
					layout.addView(horizontalLayout);
				} else
					layout.addView(viewGroups[i][j]);
			}

			ScrollView scrollView = new ScrollView(context);
			scrollView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			scrollView.addView(layout);
			groups.add(scrollView);
		}
		// Set event listeners
		formDateButton.setOnClickListener(this);
		dateOfBirthButton.setOnClickListener(this);
		firstButton.setOnClickListener(this);
		lastButton.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		scanBarcode.setOnClickListener(this);
		navigationSeekbar.setOnSeekBarChangeListener(this);
		age.setOnEditorActionListener(this);
		noPhone1.setOnClickListener(this);
		yesPhone1.setOnClickListener(this);
		noPhone2.setOnClickListener(this);
		yesPhone2.setOnClickListener(this);
		noTbTreatmentPast.setOnClickListener(this);
		yesTbTreatmentPast.setOnClickListener(this);
		refuseTbTreatmentPast.setOnClickListener(this);
		myselfPhone2Owner.setOnClickListener(this);
		otherPhone2Owner.setOnClickListener(this);
		myselfPhone1Owner.setOnClickListener(this);
		otherPhone1Owner.setOnClickListener(this);
		noHivTest.setOnClickListener(this);
		yesHivTest.setOnClickListener(this);
		refuseHivTest.setOnClickListener(this);
		negativeHivTestResult.setOnClickListener(this);
		positiveHivTestResult.setOnClickListener(this);
		unknownHivTestResult.setOnClickListener(this);
		yesHivTestNew.setOnClickListener(this);
		noHivTestNew.setOnClickListener(this);
		refuseHivTestNew.setOnClickListener(this);
		/*phone1.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
				String s = phone1.getText().toString();
				int length = s.length();
				
				if(length == 4) {
					phone1.setText(s.substring(0,3)+"-"+s.substring(4,1));
				}
				
				int position = phone1.length();
				phone1.setSelection(position);
			}
			   
	});*/
		
		
		/*cough.setOnItemSelectedListener(this);
		nightSweats.setOnItemSelectedListener(this);
		weightLoss.setOnItemSelectedListener(this);
		fever.setOnItemSelectedListener(this);
		haemoptysis.setOnItemSelectedListener(this);*/
		
		TextView[] textViews = new TextView[]{
				/* yearsWorkingNow     
				,yearsWorkingPreviously */  
				coughSymptomDuration  
				,nightSweatsSymptomDuration 
				,weightLossSymptomDuration
				,feverSymptomDuration
				,haemoptysisSymptomDuration
				,tbTreatmentPastDuration
				,phone2OtherOwner
				,phone1OtherOwner
				,phone2
				,phone1
				,patientId
				};
		
		for (TextView v : textViews) {
			v.isFocusableInTouchMode();
		}
		

		views = new View[] { /*
							 * english, afrikaans, zulu, xhosa, swati, tswana,
							 * hindiUrdu, other, dontKnow, refuse,
							 */age, firstName, surname,/* yearsWorkingNow,
				yearsWorkingPreviously, workingOnMine, workingOnMinePreviously,
				householdWorkingMine,*/ /*hivTest,*/ /*hivTestResult,*/ /*hivTestNew,*/
				/*cough, nightSweats, weightLoss, fever, haemoptysis,*/
				/*contactWithTb,*/ /*tbTreatmentPast,*/ tbTreatmentPastDuration,
				/*diabetes, familyDiabetes, hypertension, breathingShortness,
				tobaccoCurrent,*/ noPatientInformation, yesPatientInformation,
				phone1, /*phone1Owner,*/ phone1OtherOwner, phone2, /*phone2Owner,*/
				phone2OtherOwner, physicalAddress, townAddress,
				landmarkAddress, /*sputumVideoInstruction,*/
				/*tobaccoCurrentTextView,*/ patientId, coughSymptomDuration,
				nightSweatsSymptomDuration, weightLossSymptomDuration,
				feverSymptomDuration, haemoptysisSymptomDuration };
		for (View v : views) {
			if (v instanceof Spinner) {
				((Spinner) v).setOnItemSelectedListener(this);
			} else if (v instanceof CheckBox) {
				((CheckBox) v).setOnCheckedChangeListener(this);
			}
		}
		pager.setOnPageChangeListener(this);
		// Detect RTL language
		if (App.isLanguageRTL()) {
			Collections.reverse(groups);
			for (ViewGroup g : groups) {
				LinearLayout linearLayout = (LinearLayout) g.getChildAt(0);
				linearLayout.setGravity(Gravity.RIGHT);
			}
			for (View v : views) {
				if (v instanceof EditText) {
					((EditText) v).setGravity(Gravity.RIGHT);
				}
			}
		}

	}

	@Override
	public void initView(View[] views) {

		/*dob.setTime(new Date());
		dobPicker.updateDate(dob.get(Calendar.YEAR), dob.get(Calendar.MONTH),
				dob.get(Calendar.DAY_OF_MONTH));*/
		formDate = Calendar.getInstance();
		dateOfBirth = Calendar.getInstance();
		updateDisplay();
		male.setChecked(true);
		yesPhone1.setChecked(true);
		yesPhone2.setChecked(true);
		noPatientInformation.setChecked(true);
		noCough.setChecked(true);
		noNightSweats.setChecked(true);
		noWeightLoss.setChecked(true);
		noFever.setChecked(true);
		noHaemoptysis.setChecked(true);
		noContactWithTb.setChecked(true);
		noTbTreatmentPast.setChecked(true);
		noPatientReferred.setChecked(true);
		noSputumVideoInstruction.setChecked(true);
		unknownDiabetes.setChecked(true);
		myselfPhone1Owner.setChecked(true);
		myselfPhone2Owner.setChecked(true);
		noHivTest.setChecked(true);
		unknownHivTestResult.setChecked(true);
		yesHivTestNew.setChecked(true);
		physicalAddressTextView.setEnabled(false);
		landmarkAddressTextView.setEnabled(false);
		townAddressTextView.setEnabled(false);	
		physicalAddress.setEnabled(false);
		landmarkAddress.setEnabled(false);
		townAddress.setEnabled(false);		
		patientIdTextView.setVisibility(View.GONE);
		patientId.setVisibility(View.GONE);
		scanBarcode.setVisibility(View.GONE);
		tbTreatmentPastDuration.setVisibility(View.GONE);
		tbTreatmentPastDurationTextView.setVisibility(View.GONE);
		super.initView(views);
		/* refuse.setChecked(true); */
		/*yearsWorkingNowTextView.setEnabled(false);
		yearsWorkingNow.setEnabled(false);
		yearsWorkingOnMinePreviouslyTextView.setEnabled(false);
		yearsWorkingPreviously.setEnabled(false);*/
		/*hivTestResult.setEnabled(false);*/
		hivTestResultTextView.setVisibility(View.GONE);
		hivTestResultGroup.setVisibility(View.GONE);
		phone2OtherOwner.setVisibility(View.GONE);
		phone2OtherOwnerTextView.setVisibility(View.GONE);
		phone1OtherOwner.setVisibility(View.GONE);
		phone1OtherOwnerTextView.setVisibility(View.GONE);
		
		phone1.setVisibility(View.VISIBLE);
		phone1TextView.setVisibility(View.VISIBLE);
		phone1OwnerTextView.setVisibility(View.VISIBLE);
		phone1OwnerGroup.setVisibility(View.VISIBLE);
		
		phone2.setVisibility(View.VISIBLE);
		phone2TextView.setVisibility(View.VISIBLE);
		phone2OwnerTextView.setVisibility(View.VISIBLE);
		phone2OwnerGroup.setVisibility(View.VISIBLE);
		
		hivTestNewTextView.setVisibility(View.VISIBLE);
		hivTestNewGroup.setVisibility(View.VISIBLE);
		
		/*hivTestResult.setSelection(2);*/
		/*hivTestNew.setSelection(1);*/
		/*diabetes.setSelection(2);
		familyDiabetes.setSelection(2);
		hypertension.setSelection(2);*/
		
		if(!coughSymptom.isEnabled())
			coughSymptomDuration.setHint("  0  ");
		if(!nigtSweatsSymptom.isEnabled())
			nightSweatsSymptomDuration.setHint("  0  ");
		if(!weightLossSymptom.isEnabled())
			weightLossSymptomDuration.setHint("  0  ");
		if(!haemoptysisSymptom.isEnabled())
			haemoptysisSymptomDuration.setHint("  0  ");
		if(!feverSymptom.isEnabled())
			feverSymptomDuration.setHint("  0  ");

	}

	@Override
	public void updateDisplay() {
		formDateButton.setText(DateFormat.format("dd-MMM-yyyy", formDate));
		
		dateOfBirthButton.setText(DateFormat.format("dd-MMM-yyyy", dateOfBirth));

		int diff = formDate.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
		if (dateOfBirth.get(Calendar.MONTH) > formDate.get(Calendar.MONTH) || (dateOfBirth.get(Calendar.MONTH) == formDate.get(Calendar.MONTH) && dateOfBirth.get(Calendar.DATE) > formDate.get(Calendar.DATE))) {
			diff--;
		}
		
		age.setText(Integer.toString(diff));
		
		if(diff < 0)
			age.setTextColor(getResources().getColor(R.color.Red));
		else
			age.setTextColor(getResources().getColor(R.color.IRDTitle));
			

		boolean hasCough = yesCough.isChecked();
		boolean hasNightSweats = yesNightSweats.isChecked();
		boolean hasWeightLoss = yesWeightLoss.isChecked();
		boolean hasFever = yesFever.isChecked();
		boolean hasHaemoptysis = yesHaemoptysis.isChecked();
		boolean lastHivResult = positiveHivTestResult.isChecked();
		boolean hasContactWithTb = yesContactWithTb.isChecked();
		boolean hadTbTreatmentInPast = yesTbTreatmentPast.isChecked();
		
		coughSymptom.setEnabled(hasCough);
		coughSymptomDuration.setEnabled(hasCough);
		//coughDays.setEnabled(hasCough);
		nigtSweatsSymptom.setEnabled(hasNightSweats);
		nightSweatsSymptomDuration.setEnabled(hasNightSweats);
		//nightSweatsDays.setEnabled(hasNightSweats);
		weightLossSymptom.setEnabled(hasWeightLoss);
		weightLossSymptomDuration.setEnabled(hasWeightLoss);
		//weightLossDays.setEnabled(hasWeightLoss);
		feverSymptom.setEnabled(hasFever);
		feverSymptomDuration.setEnabled(hasFever);
		//feverDays.setEnabled(hasFever);
		haemoptysisSymptom.setEnabled(hasHaemoptysis);
		haemoptysisSymptomDuration.setEnabled(hasHaemoptysis);
		//haemoptysisDays.setEnabled(hasHaemoptysis);
		

		boolean check = (lastHivResult | hasCough | hasFever | hasWeightLoss
				| hasNightSweats | hasHaemoptysis | hasContactWithTb | hadTbTreatmentInPast);
		yesPatientInformation.setChecked(check);
		noPatientInformation.setChecked(!check);

	}

	@Override
	public boolean validate() {
		boolean valid = true;
		StringBuffer message = new StringBuffer();
		// Validate mandatory controls
		View[] mandatory = { firstName, surname, age, /*yearsWorkingNow,
				yearsWorkingPreviously,*/ tbTreatmentPastDuration, patientId,
				phone1, phone1OtherOwner, phone2, phone2OtherOwner,
				physicalAddress, 
				coughSymptomDuration, nightSweatsSymptomDuration,
				weightLossSymptomDuration, feverSymptomDuration,
				haemoptysisSymptomDuration };
		for (View v : mandatory) {
			if (v.getVisibility() == View.VISIBLE && v.isEnabled()) {
				if (App.get(v).equals("")) {
					valid = false;
					message.append(v.getTag().toString() + ". ");
					((EditText) v).setHintTextColor(getResources().getColor(
							R.color.Red));
				}
			}
		}
		
		if (townAddress.isEnabled()){
			if(App.get(townAddress).equals("") && landmarkAddress.equals("")){
				
				valid = false;
				message.append(townAddress.getTag().toString() + ". ");
				((EditText) townAddress).setHintTextColor(getResources().getColor(
						R.color.Red));
				message.append(landmarkAddress.getTag().toString() + ". ");
				((EditText) landmarkAddress).setHintTextColor(getResources().getColor(
						R.color.Red));
			}
		}
		
		if (!valid) {
			message.append(getResources().getString(R.string.empty_data) + "\n");
		}
		
		// validate data
		if (valid) {
			if (!RegexUtil.isWord(App.get(firstName))) {
				valid = false;
				message.append(firstName.getTag().toString() + ": "
						+ getResources().getString(R.string.invalid_data)
						+ "\n");
				firstName.setTextColor(getResources().getColor(R.color.Red));
			}
			if (!RegexUtil.isWord(App.get(surname))) {
				valid = false;
				message.append(surname.getTag().toString() + ": "
						+ getResources().getString(R.string.invalid_data)
						+ "\n");
				surname.setTextColor(getResources().getColor(R.color.Red));
			}
			if (!RegexUtil.isNumeric(App.get(age), false)) {
				valid = false;
				message.append(age.getTag().toString() + ": "
						+ getResources().getString(R.string.invalid_data)
						+ "\n");
				age.setTextColor(getResources().getColor(R.color.Red));
			}

		}

		if (valid) {
			try {
				// Form date range
				if (formDate.getTime().after(new Date())) {
					valid = false;
					message.append(formDateButton.getTag()
							+ ": "
							+ getResources().getString(
									R.string.invalid_date_or_time) + "\n");
				}
				// Age range
				int a = Integer.parseInt(App.get(age));
				if (a < 0) {
					valid = false;
					message.append(dateOfBirthTextView.getTag().toString() + ": "
							+ getResources().getString(R.string.out_of_range)
							+ "\n");
				}
				if (a > 110) {
					valid = false;
					message.append(dateOfBirthTextView.getTag().toString() + ": "
							+ getResources().getString(R.string.out_of_range)
							+ "\n");
				}
				// Past Treatment Duration
				if (tbTreatmentPastDuration.getVisibility() == View.VISIBLE) {
					int b = Integer.parseInt(App.get(tbTreatmentPastDuration));
					if (b < 1) {
						valid = false;
						message.append(tbTreatmentPastDuration.getTag()
								.toString()
								+ ": "
								+ getResources().getString(
										R.string.out_of_range) + "\n");
						tbTreatmentPastDuration.setTextColor(getResources().getColor(R.color.Red));
					}
					if (b > 24) {
						valid = false;
						message.append(tbTreatmentPastDuration.getTag()
								.toString()
								+ ": "
								+ getResources().getString(
										R.string.out_of_range) + "\n");
						tbTreatmentPastDuration.setTextColor(getResources().getColor(R.color.Red));
					}
				}
				if (patientId.getVisibility() == View.VISIBLE) {
					if (!RegexUtil.isValidId(App.get(patientId))) {
						valid = false;
						message.append(patientId.getTag().toString()
								+ ": "
								+ getResources().getString(
										R.string.invalid_data) + "\n");
						patientId.setTextColor(getResources().getColor(
								R.color.Red));
					}
				}
			} catch (NumberFormatException e) {
			}
			if (townAddress.isEnabled()){
				if (!RegexUtil.isValidAddress(App.get(physicalAddress))) {
					valid = false;
					message.append(physicalAddress.getTag().toString()
							+ ": "
							+ getResources().getString(
									R.string.invalid_data) + "\n");
					physicalAddress.setTextColor(getResources().getColor(
							R.color.Red));
				}
				
				if(!App.get(townAddress).equals("")){
					if (!RegexUtil.isValidAddress(App.get(townAddress))) {
						valid = false;
						message.append(townAddress.getTag().toString()
								+ ": "
								+ getResources().getString(
										R.string.invalid_data) + "\n");
						townAddress.setTextColor(getResources().getColor(
								R.color.Red));
					}
				}
				if(!App.get(landmarkAddress).equals("")){
					if (!RegexUtil.isValidAddress(App.get(landmarkAddress))) {
						valid = false;
						message.append(landmarkAddress.getTag().toString()
								+ ": "
								+ getResources().getString(
										R.string.invalid_data) + "\n");
						landmarkAddress.setTextColor(getResources().getColor(
								R.color.Red));
					}
				}
			}
			
			
		}
		if (!valid) {
			App.getAlertDialog(this, AlertType.ERROR, message.toString())
					.show();
		}
		return valid;
	}

	@Override
	public boolean submit() {
		if (validate()) {

			double latitude = 0;
			double longitude = 0;

			GPSTracker gps = new GPSTracker(ScreeningActivity.this);

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

			values.put("formDate", App.getSqlDate(formDate));

			values.put("firstName", App.get(firstName));
			values.put("lastName", App.get(surname));
			values.put("gender", male.isChecked() ? "M" : "F");
			values.put("age", App.get(age));

			values.put("dateOfBirth", App.getSqlDate(dateOfBirth));

			if (patientId.getVisibility() == View.VISIBLE) {
				values.put("patientId", App.get(patientId));
				values.put("TB Suspect", "Suspect");
			} else
				values.put("TB Suspect", "Non-Suspect");

			if (yesPhone1.isChecked()) {
				values.put("phone1", App.get(phone1));
				if (phone1OtherOwner.getVisibility() == View.VISIBLE)
					values.put("phone1Owner", App.get(phone1OtherOwner));
			}

			if (yesPhone2.isChecked()) {
				values.put("phone2", App.get(phone2));
				if (phone2OtherOwner.getVisibility() == View.VISIBLE)
					values.put("phone2Owner", App.get(phone2OtherOwner));
			}

			values.put("address1", App.get(physicalAddress));
			values.put("town", App.get(townAddress));
			values.put("landmark", App.get(landmarkAddress));
			values.put("city", App.getCity());
			values.put("country", App.getCountry());

			/*
			 * if (english.isChecked()) observations.add(new String[] {
			 * "Language", "English" }); if (afrikaans.isChecked())
			 * observations.add(new String[] { "Language", "Afrikaans" }); if
			 * (zulu.isChecked()) observations.add(new String[] { "Language",
			 * "Zulu" }); if (xhosa.isChecked()) observations.add(new String[] {
			 * "Language", "Xhosa" }); if (swati.isChecked())
			 * observations.add(new String[] { "Language", "Swati" }); if
			 * (tswana.isChecked()) observations.add(new String[] { "Language",
			 * "Tswana" }); if (hindiUrdu.isChecked()) observations.add(new
			 * String[] { "Language", "Hindi/Urdu" }); if (other.isChecked())
			 * observations.add(new String[] { "Language", "Other" }); if
			 * (dontKnow.isChecked()) observations.add(new String[] {
			 * "Language", "Don't Know" }); if (refuse.isChecked())
			 * observations.add(new String[] { "Language", "Refuse" });
			 */

			/*observations.add(new String[] { "Miner currently",
					App.get(workingOnMine) });
			if (App.get(workingOnMine).equals("Yes"))
				observations.add(new String[] { "Miner currently duration",
						App.get(yearsWorkingNow) });
			if (workingOnMinePreviously.isEnabled()) {
				observations.add(new String[] { "Miner previously",
						App.get(workingOnMinePreviously) });
				if (App.get(workingOnMinePreviously).equals("Yes"))
					observations.add(new String[] {
							"Miner previously duration",
							App.get(yearsWorkingPreviously) });
			}
			observations.add(new String[] { "Miner Family Member",
					App.get(householdWorkingMine) });*/

			observations.add(new String[] { "Cough",  noCough.isChecked() ? "No" : (yesCough.isChecked() ? "Yes" : "Refuse") });
			observations.add(new String[] { "Night Sweats",
					noNightSweats.isChecked() ? "No" : (yesNightSweats.isChecked() ? "Yes" : "Refuse") });
			observations
					.add(new String[] { "Weight Loss", noWeightLoss.isChecked() ? "No" : (yesWeightLoss.isChecked() ? "Yes" : "Refuse") });
			observations.add(new String[] { "Fever", noFever.isChecked() ? "No" : (yesFever.isChecked() ? "Yes" : "Refuse") });
			observations
					.add(new String[] { "Haemoptysis", noHaemoptysis.isChecked() ? "No" : (yesHaemoptysis.isChecked() ? "Yes" : "Refuse") });
			if(coughSymptomDuration.isEnabled())
				observations.add(new String[] { "Cough Duration", App.get(coughSymptomDuration) });
			if(nightSweatsSymptomDuration.isEnabled())
				observations.add(new String[] { "Night Sweats Duration", App.get(nightSweatsSymptomDuration) });
			if(weightLossSymptomDuration.isEnabled())
				observations.add(new String[] { "Weight Loss Duration", App.get(weightLossSymptomDuration) });
			if(feverSymptomDuration.isEnabled())
				observations.add(new String[] { "Fever Duration", App.get(feverSymptomDuration) });
			if(haemoptysisSymptomDuration.isEnabled())
				observations.add(new String[] { "Haemoptysis Duration", App.get(haemoptysisSymptomDuration) });

			observations.add(new String[] { "Contact with TB",
					noContactWithTb.isChecked() ? "No" : (yesContactWithTb.isChecked() ? "Yes" : "Refuse") });
			observations.add(new String[] { "TB treatment past",
					noTbTreatmentPast.isChecked() ? "No" : (yesTbTreatmentPast.isChecked() ? "Yes" : "Refuse") });
			if (tbTreatmentPastDuration.getVisibility() == View.VISIBLE)
				observations.add(new String[] { "TB treatment past duration",
						App.get(tbTreatmentPastDuration) });
			observations.add(new String[] { "Diabetes", noDiabetes.isChecked() ? "No" : (yesDiabetes.isChecked() ? "Yes" : "Unknown") });
			/*observations.add(new String[] { "Family Diabetes",
					App.get(familyDiabetes) });
			observations.add(new String[] { "Hypertension",
					App.get(hypertension) });
			observations.add(new String[] { "Breathing Shortness",
					App.get(breathingShortness) });
			observations
					.add(new String[] { "Smoking", App.get(tobaccoCurrent) });*/

			observations
					.add(new String[] { "HIV test before", noHivTest.isChecked() ? "No" : (yesHivTest.isChecked() ? "Yes" : "Refuse") });
			if (yesHivTest.isChecked())
				observations.add(new String[] { "Last HIV result",
						positiveHivTestResult.isChecked() ? "No" : (negativeHivTestResult.isChecked() ? "Yes" : "Unknown") });
			if (!positiveHivTestResult.isChecked())
				observations.add(new String[] { "Do HIV test",
						noHivTestNew.isChecked() ? "No" : (yesHivTestNew.isChecked() ? "Yes" : "Refuse") });
			
			observations.add(new String[] { "Patient Referred", noPatientReferred.isChecked() ? "No" : "Yes" });

			observations.add(new String[] { "Sputum Instruction",
					noSputumVideoInstruction.isChecked() ? "No" : (yesSputumVideoInstruction.isChecked() ? "Yes" :  (refuseSputumVideoInstruction.isChecked() ? "Refuse" : "Video unavailable")) });

			observations.add(new String[] { "Screening Type",
					App.getScreeningType() });

			observations.add(new String[] { "District", App.getDistrict() });

			if (App.getScreeningType().equals("Community")) {
				String screeningStrategy = App.getScreeningStrategy()
						.substring(6, App.getScreeningStrategy().length());
				observations.add(new String[] { "Screening Strategy",
						screeningStrategy });
			} else
				observations.add(new String[] { "Facility name",
						App.getFacility() });
			observations.add(new String[] { "Latitude",
					String.valueOf(latitude) });
			observations.add(new String[] { "Longitude",
					String.valueOf(longitude) });

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

					result = serverService.saveScreening(FormType.SCREENING,
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
						App.getAlertDialog (ScreeningActivity.this, AlertType.INFO, getResources ().getString (R.string.success_screening)+result).show ();
						initView (views);
					} else {
						App.getAlertDialog(ScreeningActivity.this,
								AlertType.ERROR, result).show();
					}
				}
			};
			updateTask.execute("");
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Retrieve barcode scan results
		if (requestCode == Barcode.BARCODE_RESULT) {
			if (resultCode == RESULT_OK) {
				String str = data.getStringExtra(Barcode.SCAN_RESULT);
				// Check for valid Id
				if (RegexUtil.isValidId(str)
						&& !RegexUtil.isNumeric(str, false)) {
					patientId.setText(str);
				} else {
					App.getAlertDialog(
							this,
							AlertType.ERROR,
							patientId.getTag().toString()
									+ ": "
									+ getResources().getString(
											R.string.invalid_data)).show();
				}
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				App.getAlertDialog(this, AlertType.ERROR,
						getResources().getString(R.string.operation_cancelled))
						.show();
			}
			// Set the locale again, since the Barcode app restores system's
			// locale because of orientation
			Locale.setDefault(App.getCurrentLocale());
			Configuration config = new Configuration();
			config.locale = App.getCurrentLocale();
			getApplicationContext().getResources().updateConfiguration(config,
					null);
		}
	};

	@Override
	protected Dialog onCreateDialog (int id)
	{
		switch (id)
		{
			
		// Show date dialog
			case DATE_DIALOG_ID :
				OnDateSetListener dateSetListener = new OnDateSetListener ()
				{
					@Override
					public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth)
					{
						formDate.set (year, monthOfYear, dayOfMonth);
						updateDisplay ();
					}
				};
				return new DatePickerDialog (this, dateSetListener, formDate.get (Calendar.YEAR), formDate.get (Calendar.MONTH), formDate.get (Calendar.DAY_OF_MONTH));
				// Show date dialog
			case DOB_DIALOG_ID :
				OnDateSetListener dobSetListener = new OnDateSetListener ()
				{
					@Override
					public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth)
					{
						dateOfBirth.set (year, monthOfYear, dayOfMonth);
						updateDisplay ();
					}
				};
				return new DatePickerDialog (this, dobSetListener, dateOfBirth.get (Calendar.YEAR), dateOfBirth.get (Calendar.MONTH), dateOfBirth.get (Calendar.DAY_OF_MONTH));
					
		
		}	
		return null;
	}			
	
	
	@Override
	public void onClick(View view) {
		view.startAnimation(alphaAnimation);
		if (view == formDateButton) {
			showDialog(DATE_DIALOG_ID);
		} else if (view == dateOfBirthButton) {
			showDialog(DOB_DIALOG_ID);
		} else if (view == firstButton) {
			gotoFirstPage();
		} else if (view == lastButton) {
			gotoLastPage();
		} else if (view == clearButton) {
			AlertDialog confirmationDialog = new AlertDialog.Builder(this)
					.create();
			confirmationDialog.setTitle(getResources().getString(
					R.string.clear_form));
			confirmationDialog.setMessage(getResources().getString(
					R.string.clear_close));
			confirmationDialog.setButton(AlertDialog.BUTTON_POSITIVE,
					getResources().getString(R.string.yes),
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							initView(views);
						}
					});
			confirmationDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
					getResources().getString(R.string.cancel),
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			confirmationDialog.show();
		} else if (view == saveButton) {
			AlertDialog confirmationDialog = new AlertDialog.Builder(this)
					.create();
			confirmationDialog.setTitle(getResources().getString(
					R.string.save_form));
			confirmationDialog.setMessage(getResources().getString(
					R.string.save_close));
			confirmationDialog.setButton(AlertDialog.BUTTON_POSITIVE,
					getResources().getString(R.string.yes),
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							submit();
						}
					});
			confirmationDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
					getResources().getString(R.string.cancel),
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			confirmationDialog.show();
		} else if (view == scanBarcode) {
			Intent intent = new Intent(Barcode.BARCODE_INTENT);
			intent.putExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
			startActivityForResult(intent, Barcode.BARCODE_RESULT);
		} else if (view == noPhone1 || view == yesPhone1) {
			
			boolean check = yesPhone1.isChecked();
			
			if(check){
				
				phone1.setVisibility(View.VISIBLE);
				phone1TextView.setVisibility(View.VISIBLE);
				phone1OwnerTextView.setVisibility(View.VISIBLE);
				phone1OwnerGroup.setVisibility(View.VISIBLE);
				
				check = otherPhone1Owner.isChecked();
				
				if(check){
					phone1OtherOwner.setVisibility(View.VISIBLE);
					phone1OtherOwnerTextView.setVisibility(View.VISIBLE);
				}else{
					phone1OtherOwner.setVisibility(View.GONE);
					phone1OtherOwnerTextView.setVisibility(View.GONE);
				}
				
			}
			
			else{
				
				phone1.setVisibility(View.GONE);
				phone1TextView.setVisibility(View.GONE);
				phone1OwnerTextView.setVisibility(View.GONE);
				phone1OwnerGroup.setVisibility(View.GONE);
				phone1OtherOwner.setVisibility(View.GONE);
				phone1OtherOwnerTextView.setVisibility(View.GONE);
				
			}
			
		} else if (view == noPhone2 || view == yesPhone2) {
			boolean check = yesPhone2.isChecked();
			
			if(check){
				
				phone2.setVisibility(View.VISIBLE);
				phone2TextView.setVisibility(View.VISIBLE);
				phone2OwnerTextView.setVisibility(View.VISIBLE);
				phone2OwnerGroup.setVisibility(View.VISIBLE);
				
				check = otherPhone2Owner.isChecked();
				
				if(check){
					phone2OtherOwner.setVisibility(View.VISIBLE);
					phone2OtherOwnerTextView.setVisibility(View.VISIBLE);
				}else{
					phone2OtherOwner.setVisibility(View.GONE);
					phone2OtherOwnerTextView.setVisibility(View.GONE);
				}
				
			}
			
			else{
				
				phone2.setVisibility(View.GONE);
				phone2TextView.setVisibility(View.GONE);
				phone2OwnerTextView.setVisibility(View.GONE);
				phone2OwnerGroup.setVisibility(View.GONE);
				phone2OtherOwner.setVisibility(View.GONE);
				phone2OtherOwnerTextView.setVisibility(View.GONE);				
			}
			
		} else if (view == noTbTreatmentPast || view == yesTbTreatmentPast || view == refuseTbTreatmentPast){
			boolean hadTbTreatmentInPast = yesTbTreatmentPast.isChecked();
			
			if(hadTbTreatmentInPast){
				tbTreatmentPastDuration.setVisibility(View.VISIBLE);
				tbTreatmentPastDurationTextView.setVisibility(View.VISIBLE);
			}
			else{
				tbTreatmentPastDuration.setVisibility(View.GONE);
				tbTreatmentPastDurationTextView.setVisibility(View.GONE);
			}
			
		}else if (view == myselfPhone1Owner || view == otherPhone1Owner){
			
			boolean check = otherPhone1Owner.isChecked();
			
			if(check){
				phone1OtherOwner.setVisibility(View.VISIBLE);
				phone1OtherOwnerTextView.setVisibility(View.VISIBLE);
			}else{
				phone1OtherOwner.setVisibility(View.GONE);
				phone1OtherOwnerTextView.setVisibility(View.GONE);
			}
		}else if (view == myselfPhone2Owner || view == otherPhone2Owner){
			
			boolean check = otherPhone2Owner.isChecked();
			
			if(check){
				phone2OtherOwner.setVisibility(View.VISIBLE);
				phone2OtherOwnerTextView.setVisibility(View.VISIBLE);
			}else{
				phone2OtherOwner.setVisibility(View.GONE);
				phone2OtherOwnerTextView.setVisibility(View.GONE);
			}
		}else if(view == noHivTest || view == yesHivTest || view == refuseHivTest){
			boolean check = yesHivTest.isChecked();
			
			if(check){
				hivTestResultTextView.setVisibility(View.VISIBLE);
				hivTestResultGroup.setVisibility(View.VISIBLE);
				if(negativeHivTestResult.isChecked() || unknownHivTestResult.isChecked()){
					hivTestNewTextView.setVisibility(View.VISIBLE);
					hivTestNewGroup.setVisibility(View.VISIBLE);
				}
				else{
					hivTestNewTextView.setVisibility(View.GONE);
					hivTestNewGroup.setVisibility(View.GONE);
				}
			}
			else{
				hivTestResultTextView.setVisibility(View.GONE);
				hivTestResultGroup.setVisibility(View.GONE);
				hivTestNewTextView.setVisibility(View.VISIBLE);
				hivTestNewGroup.setVisibility(View.VISIBLE);
			}
			
		}else if(view == negativeHivTestResult || view == positiveHivTestResult || view == unknownHivTestResult){
			 
			Boolean check = positiveHivTestResult.isChecked();
			
			if(check){
				hivTestNewTextView.setVisibility(View.GONE);
				hivTestNewGroup.setVisibility(View.GONE);
				
			}else{
				hivTestNewTextView.setVisibility(View.VISIBLE);
				hivTestNewGroup.setVisibility(View.VISIBLE);	
			}
			
		}else if (view == noHivTestNew || view == yesHivTestNew || view == refuseHivTestNew){
			
			Boolean check = yesHivTestNew.isChecked();
			
			if(check)
				screenerInstructionOneTextView.setText(R.string.screener_instruction_one_yes);
			else
				screenerInstructionOneTextView.setText(R.string.screener_instruction_one_no);
			
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		MySpinner spinner = (MySpinner) parent;
		boolean visible = spinner.getSelectedItemPosition() == 1;
		/*if (parent == workingOnMine) {
			yearsWorkingNowTextView.setEnabled(visible);
			yearsWorkingNow.setEnabled(visible);
			if (visible) {
				workingOnMinePreviouslyTextView.setEnabled(false);
				workingOnMinePreviously.setEnabled(false);
				yearsWorkingOnMinePreviouslyTextView.setEnabled(false);
				yearsWorkingPreviously.setEnabled(false);
			} else {
				workingOnMinePreviouslyTextView.setEnabled(true);
				workingOnMinePreviously.setEnabled(true);
			}
		} else if (parent == workingOnMinePreviously) {
			yearsWorkingOnMinePreviouslyTextView.setEnabled(visible);
			yearsWorkingPreviously.setEnabled(visible);
		} else*/ /*if (parent == hivTest) {
			if (!visible) {
				hivTestResult.setEnabled(false);
				hivTestResultTextView.setEnabled(false);
				hivTestNewTextView.setEnabled(true);
				hivTestNew.setEnabled(true);
			} else {
				hivTestResult.setEnabled(true);
				hivTestResultTextView.setEnabled(true);
			}
			hivTestResult.setSelection(2);
			hivTestNew.setSelection(1);
		} else if (parent == hivTestResult) {
			int count = hivTestResult.getSelectedItemPosition();
			if (count == 0) {
				hivTestNewTextView.setEnabled(false);
				hivTestNew.setEnabled(false);
			} else {
				int chk = hivTest.getSelectedItemPosition();
				if (chk == 1) {
					hivTestNewTextView.setEnabled(true);
					hivTestNew.setEnabled(true);
				}
			}
			hivTestNew.setSelection(1);
		} else if (parent == hivTestNew) {
			if (visible)
				screenerInstructionOneTextView
						.setText(R.string.screener_instruction_one_yes);
			else
				screenerInstructionOneTextView
						.setText(R.string.screener_instruction_one_no);
		}*/ 
		updateDisplay();
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean state) {
		/*
		 * if (button == refuse) { if (button.isChecked()) {
		 * english.setChecked(false); afrikaans.setChecked(false);
		 * zulu.setChecked(false); xhosa.setChecked(false);
		 * swati.setChecked(false); tswana.setChecked(false);
		 * hindiUrdu.setChecked(false); other.setChecked(false);
		 * dontKnow.setChecked(false); }
		 * 
		 * } else if (button == english) { if (button.isChecked())
		 * refuse.setChecked(false); } else if (button == afrikaans) { if
		 * (button.isChecked()) refuse.setChecked(false); } else if (button ==
		 * zulu) { if (button.isChecked()) refuse.setChecked(false); } else if
		 * (button == xhosa) { if (button.isChecked()) refuse.setChecked(false);
		 * } else if (button == swati) { if (button.isChecked())
		 * refuse.setChecked(false); } else if (button == tswana) { if
		 * (button.isChecked()) refuse.setChecked(false); } else if (button ==
		 * hindiUrdu) { if (button.isChecked()) refuse.setChecked(false); } else
		 * if (button == other) { if (button.isChecked())
		 * refuse.setChecked(false); } else
		 */
		if (button == yesPatientInformation) {
			if (button.isChecked()) {
				patientIdTextView.setVisibility(View.VISIBLE);
				patientId.setVisibility(View.VISIBLE);
				scanBarcode.setVisibility(View.VISIBLE);
				physicalAddressTextView.setEnabled(true);
				landmarkAddressTextView.setEnabled(true);
				townAddressTextView.setEnabled(true);
				physicalAddress.setEnabled(true);
				landmarkAddress.setEnabled(true);
				townAddress.setEnabled(true);
			} else {
				patientIdTextView.setVisibility(View.GONE);
				patientId.setVisibility(View.GONE);
				scanBarcode.setVisibility(View.GONE);
				physicalAddressTextView.setEnabled(false);
				landmarkAddressTextView.setEnabled(false);
				townAddressTextView.setEnabled(false);
				physicalAddress.setEnabled(false);
				landmarkAddress.setEnabled(false);
				townAddress.setEnabled(false);
			}
		}

	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		// TODO Auto-generated method stub
		return false;
	}



}
