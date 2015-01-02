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
import android.text.InputType;
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
	MyEditText age;
	MyTextView dobTextView;
	DatePicker dobPicker;
	Calendar dob;
	/*
	 * MyTextView LanguagesSpokenTextView; MyCheckBox english; MyCheckBox
	 * afrikaans; MyCheckBox zulu; MyCheckBox xhosa; MyCheckBox swati;
	 * MyCheckBox tswana; MyCheckBox hindiUrdu; MyCheckBox other; MyCheckBox
	 * dontKnow; MyCheckBox refuse;
	 */

	MyTextView mineHeadingTextView;
	MyTextView workingOnMineTextView;
	MySpinner workingOnMine;
	MyTextView yearsWorkingNowTextView;
	MyEditText yearsWorkingNow;
	MyTextView workingOnMinePreviouslyTextView;
	MySpinner workingOnMinePreviously;
	MyTextView yearsWorkingOnMinePreviouslyTextView;
	MyEditText yearsWorkingPreviously;
	MyTextView householdWorkingMineTextView;
	MySpinner householdWorkingMine;

	MyTextView symptomsHeadingTextView;
	MyTextView coughTextView;
	MySpinner cough;
	MyTextView nightSweatsTextView;
	MySpinner nightSweats;
	MyTextView weightLossTextView;
	MySpinner weightLoss;
	MyTextView feverTextView;
	MySpinner fever;
	MyTextView haemoptysisTextView;
	MySpinner haemoptysis;

	MyTextView summaryHeadingTextView;
	MyTextView symptomQuestion;

	MyTextView coughSymptom;
	MyEditText coughSymptomDuration;
	MyTextView coughDays;
	MyTextView nigtSweatsSymptom;
	MyEditText nightSweatsSymptomDuration;
	MyTextView nightSweatsDays;
	MyTextView weightLossSymptom;
	MyEditText weightLossSymptomDuration;
	MyTextView weightLossDays;
	MyTextView feverSymptom;
	MyEditText feverSymptomDuration;
	MyTextView feverDays;
	MyTextView haemoptysisSymptom;
	MyEditText haemoptysisSymptomDuration;
	MyTextView haemoptysisDays;

	MyTextView screenerInstructionTextView1;
	MyTextView screenerInstructionRiskTextView;

	MyTextView riskFactorHeadingTextView;
	MyTextView contactWithTbTextView;
	MySpinner contactWithTb;
	MyTextView tbTreatmentPastTextView;
	MySpinner tbTreatmentPast;
	MyTextView tbTreatmentPastDurationTextView;
	MyEditText tbTreatmentPastDuration;
	MyTextView diabetesTextView;
	MySpinner diabetes;
	MyTextView familyDiabetesTextView;
	MySpinner familyDiabetes;
	MyTextView hypertensionTextView;
	MySpinner hypertension;
	MyTextView breathingShortnessTextView;
	MySpinner breathingShortness;
	MyTextView tobaccoCurrentTextView;
	MySpinner tobaccoCurrent;

	MyTextView hivHeadingTextView;
	MyTextView hivTestTextView;
	MySpinner hivTest;
	MyTextView hivTestResultTextView;
	MySpinner hivTestResult;
	MyTextView hivTestNewTextView;
	MySpinner hivTestNew;

	MyTextView screenerInstructionTextView2;
	MyTextView screenerInstructionOneTextView;
	MyTextView screenerInstructionTwoTextView;

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
	MySpinner phone1Owner;
	MyEditText phone1OtherOwner;

	MyTextView providePhone2TextView;
	MyRadioGroup providePhone2;
	MyRadioButton noPhone2;
	MyRadioButton yesPhone2;
	MyTextView phone2TextView;
	MyEditText phone2;
	MyTextView phone2OwnerTextView;
	MySpinner phone2Owner;
	MyEditText phone2OtherOwner;

	MyTextView physicalAddressTextView;
	MyEditText physicalAddress;
	MyTextView townAddressTextView;
	MyEditText townAddress;
	MyTextView landmarkAddressTextView;
	MyEditText landmarkAddress;
	MyTextView sputumVideoInstructionTextView;
	MySpinner sputumVideoInstruction;

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
		PAGE_COUNT = 17;
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
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 20,
				false);
		surnameTextView = new MyTextView(context, R.style.text,
				R.string.last_name);
		surname = new MyEditText(context, R.string.last_name,
				R.string.last_name_hint,
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 20,
				false);

		// Demographics
		genderTextView = new MyTextView(context, R.style.text, R.string.gender);
		male = new MyRadioButton(context, R.string.male, R.style.radio,
				R.string.male);
		female = new MyRadioButton(context, R.string.female, R.style.radio,
				R.string.female);
		gender = new MyRadioGroup(context,
				new MyRadioButton[] { male, female }, R.string.gender,
				R.style.radio, App.isLanguageRTL());

		dobTextView = new MyTextView(context, R.style.text, R.string.dob);
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
				});
		ageTextView = new MyTextView(context, R.style.text, R.string.age);
		age = new MyEditText(context, R.string.age, R.string.age_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);
		age.setEnabled(false);
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
		mineHeadingTextView = new MyTextView(context, R.style.text,
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
				R.string.option_hint);

		// Symptoms
		symptomsHeadingTextView = new MyTextView(context, R.style.text,
				R.string.symptomsHeading);
		symptomsHeadingTextView.setTypeface(null, Typeface.BOLD);
		coughTextView = new MyTextView(context, R.style.text, R.string.cough);
		cough = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.cough, R.string.option_hint);
		coughDays = new MyTextView(context, R.style.text, R.string.day);
		nightSweatsTextView = new MyTextView(context, R.style.text,
				R.string.night_sweats);
		nightSweats = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.night_sweats, R.string.option_hint);
		nightSweatsDays = new MyTextView(context, R.style.text, R.string.day);
		weightLossTextView = new MyTextView(context, R.style.text,
				R.string.weight_loss);
		weightLoss = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.weight_loss, R.string.option_hint);
		weightLossDays = new MyTextView(context, R.style.text, R.string.day);
		feverTextView = new MyTextView(context, R.style.text, R.string.fever);
		fever = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.fever, R.string.option_hint);
		feverDays = new MyTextView(context, R.style.text, R.string.day);
		haemoptysisTextView = new MyTextView(context, R.style.text,
				R.string.haemoptysis);
		haemoptysis = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.haemoptysis, R.string.option_hint);
		haemoptysisDays = new MyTextView(context, R.style.text, R.string.day);
		
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
				R.color.BlueShade));
		screenerInstructionTextView1.setTypeface(null, Typeface.BOLD);
		screenerInstructionRiskTextView = new MyTextView(context, R.style.text,
				R.string.risk_instruction);
		screenerInstructionRiskTextView.setGravity(Gravity.CENTER);
		screenerInstructionRiskTextView.setTextColor(getResources().getColor(
				R.color.White));
		screenerInstructionRiskTextView
				.setBackgroundResource(R.color.LightBlue);
		screenerInstructionRiskTextView.setTypeface(null, Typeface.ITALIC);

		riskFactorHeadingTextView = new MyTextView(context, R.style.text,
				R.string.riskFactorHeading);
		riskFactorHeadingTextView.setTypeface(null, Typeface.BOLD);
		contactWithTbTextView = new MyTextView(context, R.style.text,
				R.string.contact_with_tb);
		contactWithTb = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.contact_with_tb,
				R.string.option_hint);
		tbTreatmentPastTextView = new MyTextView(context, R.style.text,
				R.string.tb_treatment_previously);
		tbTreatmentPast = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.tb_treatment_previously,
				R.string.option_hint);
		tbTreatmentPastDurationTextView = new MyTextView(context, R.style.text,
				R.string.tb_treatment_duration);
		tbTreatmentPastDuration = new MyEditText(context,
				R.string.tb_treatment_duration,
				R.string.tb_treatment_duration_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 2, false);

		diabetesTextView = new MyTextView(context, R.style.text,
				R.string.diabetes);
		diabetes = new MySpinner(context, getResources().getStringArray(
				R.array.option), R.string.diabetes, R.string.option_hint);
		familyDiabetesTextView = new MyTextView(context, R.style.text,
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
				R.string.option_hint);

		// HIV related
		hivHeadingTextView = new MyTextView(context, R.style.text,
				R.string.hivHeading);
		hivHeadingTextView.setTypeface(null, Typeface.BOLD);
		hivTestTextView = new MyTextView(context, R.style.text,
				R.string.hivTest);
		hivTest = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.hivTest, R.string.option_hint);
		hivTestResultTextView = new MyTextView(context, R.style.text,
				R.string.hivTestResult);
		hivTestResult = new MySpinner(context, getResources().getStringArray(
				R.array.hiv_result), R.string.workNowMine, R.string.option_hint);
		hivTestNewTextView = new MyTextView(context, R.style.text,
				R.string.hivTestNew);
		hivTestNew = new MySpinner(context, getResources().getStringArray(
				R.array.options), R.string.hivTestNew, R.string.option_hint);

		// Screener Instruction
		screenerInstructionTextView2 = new MyTextView(context, R.style.text,
				R.string.screenerInstructionHeading);
		screenerInstructionTextView2.setTextColor(getResources().getColor(
				R.color.BlueShade));
		screenerInstructionTextView2.setTypeface(null, Typeface.BOLD);
		screenerInstructionOneTextView = new MyTextView(context, R.style.text,
				R.string.screener_instruction_one_yes);
		screenerInstructionOneTextView.setGravity(Gravity.CENTER);
		screenerInstructionOneTextView.setTextColor(getResources().getColor(
				R.color.White));
		screenerInstructionOneTextView.setBackgroundResource(R.color.LightBlue);
		screenerInstructionOneTextView.setTypeface(null, Typeface.ITALIC);

		screenerInstructionTextView3 = new MyTextView(context, R.style.text,
				R.string.screenerInstructionHeading);
		screenerInstructionTextView3.setTextColor(getResources().getColor(
				R.color.BlueShade));
		screenerInstructionTextView3.setTypeface(null, Typeface.BOLD);
		screenerInstructionTwoTextView = new MyTextView(context, R.style.text,
				R.string.screener_instruction_two);
		screenerInstructionTwoTextView.setGravity(Gravity.CENTER);
		screenerInstructionTwoTextView.setTextColor(getResources().getColor(
				R.color.White));
		screenerInstructionTwoTextView.setBackgroundResource(R.color.LightBlue);
		screenerInstructionTwoTextView.setTypeface(null, Typeface.ITALIC);
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
				R.drawable.custom_button_beige, R.string.scan_barcode,
				R.string.scan_barcode);

		providePhone1TextView = new MyTextView(context, R.style.text,
				R.string.phone1_provided);
		noPhone1 = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesPhone1 = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		providePhone1 = new MyRadioGroup(context, new MyRadioButton[] {
				noPhone1, yesPhone1 }, R.string.phone_1, R.style.radio,
				App.isLanguageRTL());
		phone1TextView = new MyTextView(context, R.style.text, R.string.phone_1);
		phone1 = new MyEditText(context, R.string.phone1, R.string.phone1_hint,
				InputType.TYPE_CLASS_PHONE, R.style.edit, 20, false);
		phone1OwnerTextView = new MyTextView(context, R.style.text,
				R.string.phone1_owner);
		phone1Owner = new MySpinner(context, getResources().getStringArray(
				R.array.owner_options), R.string.phone1_owner,
				R.string.option_hint);
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
				App.isLanguageRTL());
		phone2TextView = new MyTextView(context, R.style.text, R.string.phone_2);
		phone2 = new MyEditText(context, R.string.phone2, R.string.phone1_hint,
				InputType.TYPE_CLASS_PHONE, R.style.edit, 20, false);
		phone2OwnerTextView = new MyTextView(context, R.style.text,
				R.string.phone2_owner);
		phone2Owner = new MySpinner(context, getResources().getStringArray(
				R.array.owner_options), R.string.phone2_owner,
				R.string.option_hint);
		phone2OtherOwner = new MyEditText(context, R.string.phone2_owner,
				R.string.owner_name_hint,
				InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 11,
				false);

		physicalAddressTextView = new MyTextView(context, R.style.text,
				R.string.physical_address);
		physicalAddress = new MyEditText(context, R.string.physical_address,
				R.string.physical_address_hint, InputType.TYPE_CLASS_TEXT,
				R.style.edit, 0, false);
		townAddressTextView = new MyTextView(context, R.style.text,
				R.string.town_address);
		townAddress = new MyEditText(context, R.string.town_address,
				R.string.town_address_hint, InputType.TYPE_CLASS_TEXT,
				R.style.edit, 0, false);
		landmarkAddressTextView = new MyTextView(context, R.style.text,
				R.string.landmark_address);
		landmarkAddress = new MyEditText(context, R.string.landmark_address,
				R.string.landmark_address_hint, InputType.TYPE_CLASS_TEXT,
				R.style.edit, 0, false);
		sputumVideoInstructionTextView = new MyTextView(context, R.style.text,
				R.string.sputum_video);
		sputumVideoInstruction = new MySpinner(context, getResources()
				.getStringArray(R.array.sputum_video_options),
				R.string.phone2_owner, R.string.option_hint);

		View[][] viewGroups = {
				{ formDateTextView, formDateButton, firstNameTextView,
						firstName, surnameTextView, surname, genderTextView,
						gender },
				{ dobTextView, dobPicker, ageTextView, age },
				/*
				 * { LanguagesSpokenTextView, english, afrikaans, zulu, xhosa,
				 * swati, tswana, hindiUrdu, other, dontKnow, refuse },
				 */
				{ mineHeadingTextView, workingOnMineTextView, workingOnMine,
						yearsWorkingNowTextView, yearsWorkingNow },
				{ workingOnMinePreviouslyTextView, workingOnMinePreviously,
						yearsWorkingOnMinePreviouslyTextView,
						yearsWorkingPreviously, householdWorkingMineTextView,
						householdWorkingMine },
				{ symptomsHeadingTextView, coughTextView, cough,
						nightSweatsTextView, nightSweats, weightLossTextView,
						weightLoss },
				{ feverTextView, fever, haemoptysisTextView, haemoptysis },
				{ summaryHeadingTextView, symptomQuestion, coughSymptom,
						coughSymptomDuration, coughDays, nigtSweatsSymptom,
						nightSweatsSymptomDuration, nightSweatsDays, weightLossSymptom,
						weightLossSymptomDuration, weightLossDays, feverSymptom,
						feverSymptomDuration, feverDays, haemoptysisSymptom,
						haemoptysisSymptomDuration, haemoptysisDays },
				{ screenerInstructionTextView1,
						screenerInstructionRiskTextView,
						riskFactorHeadingTextView, contactWithTbTextView,
						contactWithTb, tbTreatmentPastTextView, tbTreatmentPast },
				{ tbTreatmentPastDurationTextView, tbTreatmentPastDuration,
						diabetesTextView, diabetes, familyDiabetesTextView,
						familyDiabetes },
				{ hypertensionTextView, hypertension,
						breathingShortnessTextView, breathingShortness,
						tobaccoCurrentTextView, tobaccoCurrent },
				{ hivHeadingTextView, hivTestTextView, hivTest,
						hivTestResultTextView, hivTestResult,
						hivTestNewTextView, hivTestNew },
				{ screenerInstructionTextView2, screenerInstructionOneTextView },
				{ screenerInstructionTextView3, screenerInstructionTwoTextView,
						noPatientInformation, yesPatientInformation,
						patientIdTextView, patientId, scanBarcode },
				{ providePhone1TextView, providePhone1, phone1TextView, phone1,
						phone1OwnerTextView, phone1Owner, phone1OtherOwner },
				{ providePhone2TextView, providePhone2, phone2TextView, phone2,
						phone2OwnerTextView, phone2Owner, phone2OtherOwner },
				{ physicalAddressTextView, physicalAddress,
						townAddressTextView, townAddress,
						landmarkAddressTextView, landmarkAddress },
				{ sputumVideoInstructionTextView, sputumVideoInstruction }, };
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

				if (i == 6) {
					LinearLayout horizontalLayout = new LinearLayout(context);
					horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
					if (j == 0 || j == 1) {
						horizontalLayout.addView(viewGroups[i][j]);
					} else {
						horizontalLayout.addView(viewGroups[i][j]);
						j++;
						horizontalLayout.addView(viewGroups[i][j]);
						j++;
						horizontalLayout.addView(viewGroups[i][j]);
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
		
		cough.setOnItemSelectedListener(this);
		nightSweats.setOnItemSelectedListener(this);
		weightLoss.setOnItemSelectedListener(this);
		fever.setOnItemSelectedListener(this);
		haemoptysis.setOnItemSelectedListener(this);

		views = new View[] { /*
							 * english, afrikaans, zulu, xhosa, swati, tswana,
							 * hindiUrdu, other, dontKnow, refuse,
							 */age, firstName, surname, yearsWorkingNow,
				yearsWorkingPreviously, workingOnMine, workingOnMinePreviously,
				householdWorkingMine, hivTest, hivTestResult, hivTestNew,
				cough, nightSweats, weightLoss, fever, haemoptysis,
				contactWithTb, tbTreatmentPast, tbTreatmentPastDuration,
				diabetes, familyDiabetes, hypertension, breathingShortness,
				tobaccoCurrent, noPatientInformation, yesPatientInformation,
				phone1, phone1Owner, phone1OtherOwner, phone2, phone2Owner,
				phone2OtherOwner, physicalAddress, townAddress,
				landmarkAddress, sputumVideoInstruction,
				tobaccoCurrentTextView, patientId, coughSymptomDuration,
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

		dob.setTime(new Date());
		dobPicker.updateDate(dob.get(Calendar.YEAR), dob.get(Calendar.MONTH),
				dob.get(Calendar.DAY_OF_MONTH));
		formDate = Calendar.getInstance();
		updateDisplay();
		male.setChecked(true);
		yesPhone1.setChecked(true);
		yesPhone2.setChecked(true);
		noPatientInformation.setChecked(true);
		patientIdTextView.setEnabled(false);
		patientId.setEnabled(false);
		scanBarcode.setEnabled(false);
		super.initView(views);
		phone1.setEnabled(true);
		phone1TextView.setEnabled(true);
		phone1OwnerTextView.setEnabled(true);
		phone1Owner.setEnabled(true);
		phone1OtherOwner.setEnabled(false);
		phone2.setEnabled(true);
		phone2TextView.setEnabled(true);
		phone2OwnerTextView.setEnabled(true);
		phone2Owner.setEnabled(true);
		phone2OtherOwner.setEnabled(false);
		/* refuse.setChecked(true); */
		yearsWorkingNowTextView.setEnabled(false);
		yearsWorkingNow.setEnabled(false);
		yearsWorkingOnMinePreviouslyTextView.setEnabled(false);
		yearsWorkingPreviously.setEnabled(false);
		hivTestResult.setEnabled(false);
		hivTestResultTextView.setEnabled(false);
		hivTestResult.setSelection(2);
		hivTestNew.setSelection(1);
		diabetes.setSelection(2);
		familyDiabetes.setSelection(2);
		hypertension.setSelection(2);
		
		if(!coughSymptom.isEnabled())
			coughSymptomDuration.setText("0");
		if(!nigtSweatsSymptom.isEnabled())
			nightSweatsSymptomDuration.setText("0");
		if(!weightLossSymptom.isEnabled())
			weightLossSymptomDuration.setText("0");
		if(!haemoptysisSymptom.isEnabled())
			haemoptysisSymptomDuration.setText("0");
		if(!feverSymptom.isEnabled())
			feverSymptomDuration.setText("0");

	}

	@Override
	public void updateDisplay() {
		formDateButton.setText(DateFormat.format("dd-MMM-yyyy", formDate));

		boolean hasCough = cough.getSelectedItemPosition() == 1;
		boolean hasNightSweats = nightSweats.getSelectedItemPosition() == 1;
		boolean hasWeightLoss = weightLoss.getSelectedItemPosition() == 1;
		boolean hasFever = fever.getSelectedItemPosition() == 1;
		boolean hasHaemoptysis = haemoptysis.getSelectedItemPosition() == 1;
		boolean lastHivResult = hivTestResult.getSelectedItemPosition() == 0;
		boolean hasContactWithTb = contactWithTb.getSelectedItemPosition() == 1;
		boolean hadTbTreatmentInPast = tbTreatmentPast
				.getSelectedItemPosition() == 1;
		
		coughSymptom.setEnabled(hasCough);
		coughSymptomDuration.setEnabled(hasCough);
		coughDays.setEnabled(hasCough);
		nigtSweatsSymptom.setEnabled(hasNightSweats);
		nightSweatsSymptomDuration.setEnabled(hasNightSweats);
		nightSweatsDays.setEnabled(hasNightSweats);
		weightLossSymptom.setEnabled(hasWeightLoss);
		weightLossSymptomDuration.setEnabled(hasWeightLoss);
		weightLossDays.setEnabled(hasWeightLoss);
		feverSymptom.setEnabled(hasFever);
		feverSymptomDuration.setEnabled(hasFever);
		feverDays.setEnabled(hasFever);
		haemoptysisSymptom.setEnabled(hasHaemoptysis);
		haemoptysisSymptomDuration.setEnabled(hasHaemoptysis);
		haemoptysisDays.setEnabled(hasHaemoptysis);
		
		if(!coughSymptom.isEnabled())
			coughSymptomDuration.setText("0");
		if(!nigtSweatsSymptom.isEnabled())
			nightSweatsSymptomDuration.setText("0");
		if(!weightLossSymptom.isEnabled())
			weightLossSymptomDuration.setText("0");
		if(!haemoptysisSymptom.isEnabled())
			haemoptysisSymptomDuration.setText("0");
		if(!feverSymptom.isEnabled())
			feverSymptomDuration.setText("0");
		
		

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
		View[] mandatory = { firstName, surname, age, yearsWorkingNow,
				yearsWorkingPreviously, tbTreatmentPastDuration, patientId,
				phone1, phone1OtherOwner, phone2, phone2OtherOwner,
				physicalAddress, townAddress, landmarkAddress,
				coughSymptomDuration, nightSweatsSymptomDuration,
				weightLossSymptomDuration, feverSymptomDuration,
				haemoptysisSymptomDuration };
		for (View v : mandatory) {
			if (v.isEnabled()) {
				if (App.get(v).equals("")) {
					valid = false;
					message.append(v.getTag().toString() + ". ");
					((EditText) v).setHintTextColor(getResources().getColor(
							R.color.Red));
				}
			}
		}
		if (!valid) {
			message.append(getResources().getString(R.string.empty_data) + "\n");
		}

		/*
		 * if (!(english.isChecked() || afrikaans.isChecked() ||
		 * zulu.isChecked() || xhosa.isChecked() || swati.isChecked() ||
		 * tswana.isChecked() || hindiUrdu.isChecked() || other.isChecked() ||
		 * dontKnow.isChecked() || refuse.isChecked())) { valid = false;
		 * message.append(LanguagesSpokenTextView.getText() + ": " +
		 * getResources().getString(R.string.empty_selection) + "\n"); }
		 */

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
					message.append(dobPicker.getTag().toString() + ": "
							+ getResources().getString(R.string.out_of_range)
							+ "\n");
				}
				if (a > 110) {
					valid = false;
					message.append(dobPicker.getTag().toString() + ": "
							+ getResources().getString(R.string.out_of_range)
							+ "\n");
				}
				// Past Treatment Duration
				if (tbTreatmentPastDuration.isEnabled()) {
					int b = Integer.parseInt(App.get(tbTreatmentPastDuration));
					if (b < 1) {
						valid = false;
						message.append(tbTreatmentPastDuration.getTag()
								.toString()
								+ ": "
								+ getResources().getString(
										R.string.out_of_range) + "\n");

					}
					if (b > 24) {
						valid = false;
						message.append(tbTreatmentPastDuration.getTag()
								.toString()
								+ ": "
								+ getResources().getString(
										R.string.out_of_range) + "\n");

					}
				}
				if (patientId.isEnabled()) {
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

			int day = dobPicker.getDayOfMonth();
			int month = dobPicker.getMonth();
			int year = dobPicker.getYear();
			Calendar date = Calendar.getInstance();
			date.set(Calendar.DAY_OF_MONTH, day);
			date.set(Calendar.MONTH, month);
			date.set(Calendar.YEAR, year);

			Date dateBirth = date.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateOfBirth = sdf.format(dateBirth);

			values.put("dateOfBirth", dateOfBirth);

			if (patientId.isEnabled()) {
				values.put("patientId", App.get(patientId));
				values.put("TB Suspect", "Suspect");
			} else
				values.put("TB Suspect", "Non-Suspect");

			if (yesPhone1.isChecked()) {
				values.put("phone1", App.get(phone1));
				if (phone1OtherOwner.isEnabled())
					values.put("phone1Owner", App.get(phone1OtherOwner));
			}

			if (yesPhone2.isChecked()) {
				values.put("phone2", App.get(phone2));
				if (phone2OtherOwner.isEnabled())
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

			observations.add(new String[] { "Miner currently",
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
					App.get(householdWorkingMine) });

			observations.add(new String[] { "Cough", App.get(cough) });
			observations.add(new String[] { "Night Sweats",
					App.get(nightSweats) });
			observations
					.add(new String[] { "Weight Loss", App.get(weightLoss) });
			observations.add(new String[] { "Fever", App.get(fever) });
			observations
					.add(new String[] { "Haemoptysis", App.get(haemoptysis) });
			if(coughSymptomDuration.isEnabled())
				observations.add(new String[] { "Cough Duration", App.get(coughSymptomDuration) });
			if(nightSweatsSymptomDuration.isEnabled())
				observations.add(new String[] { "Night Sweats Duration", App.get(nightSweatsSymptomDuration) });
			if(weightLossSymptomDuration.isEnabled())
				observations.add(new String[] { "Night Sweats Duration", App.get(weightLossSymptomDuration) });
			if(feverSymptomDuration.isEnabled())
				observations.add(new String[] { "Fever Duration", App.get(feverSymptomDuration) });
			if(haemoptysisSymptomDuration.isEnabled())
				observations.add(new String[] { "Haemoptysis Duration", App.get(haemoptysisSymptomDuration) });

			observations.add(new String[] { "Contact with TB",
					App.get(contactWithTb) });
			observations.add(new String[] { "TB treatment past",
					App.get(tbTreatmentPast) });
			if (tbTreatmentPastDuration.isEnabled())
				observations.add(new String[] { "TB treatment past duration",
						App.get(tbTreatmentPastDuration) });
			observations.add(new String[] { "Diabetes", App.get(diabetes) });
			observations.add(new String[] { "Family Diabetes",
					App.get(familyDiabetes) });
			observations.add(new String[] { "Hypertension",
					App.get(hypertension) });
			observations.add(new String[] { "Breathing Shortness",
					App.get(breathingShortness) });
			observations
					.add(new String[] { "Smoking", App.get(tobaccoCurrent) });

			observations
					.add(new String[] { "HIV test before", App.get(hivTest) });
			if (hivTestResult.isEnabled())
				observations.add(new String[] { "Last HIV result",
						App.get(hivTestResult) });
			if (hivTestNew.isEnabled())
				observations.add(new String[] { "Do HIV test",
						App.get(hivTestNew) });

			observations.add(new String[] { "Sputum Instruction",
					App.get(sputumVideoInstruction) });

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
					if (result.equals("SUCCESS")) {
						App.getAlertDialog(ScreeningActivity.this,
								AlertType.INFO,
								getResources().getString(R.string.inserted))
								.show();
						initView(views);
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
	public void onClick(View view) {
		view.startAnimation(alphaAnimation);
		if (view == formDateButton) {
			showDialog(DATE_DIALOG_ID);
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
			phone1.setEnabled(yesPhone1.isChecked());
			phone1TextView.setEnabled(yesPhone1.isChecked());
			phone1OwnerTextView.setEnabled(yesPhone1.isChecked());
			phone1Owner.setEnabled(yesPhone1.isChecked());
			boolean check = phone1Owner.isEnabled()
					&& phone1Owner.getSelectedItemPosition() == 1;
			phone1OtherOwner.setEnabled(check);
		} else if (view == noPhone2 || view == yesPhone2) {
			phone2.setEnabled(yesPhone2.isChecked());
			phone2TextView.setEnabled(yesPhone2.isChecked());
			phone2OwnerTextView.setEnabled(yesPhone2.isChecked());
			phone2Owner.setEnabled(yesPhone2.isChecked());
			phone2OtherOwner.setEnabled(yesPhone2.isChecked());
			boolean check = phone2Owner.isEnabled()
					&& phone2Owner.getSelectedItemPosition() == 1;
			phone2OtherOwner.setEnabled(check);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		MySpinner spinner = (MySpinner) parent;
		boolean visible = spinner.getSelectedItemPosition() == 1;
		if (parent == workingOnMine) {
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
		} else if (parent == hivTest) {
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
		} else if (parent == tbTreatmentPast) {
			tbTreatmentPastDuration.setEnabled(visible);
			tbTreatmentPastDurationTextView.setEnabled(visible);
		} else if (parent == phone1Owner) {
			phone1OtherOwner.setEnabled(visible);
		} else if (parent == phone2Owner) {
			phone2OtherOwner.setEnabled(visible);
		}
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
				patientIdTextView.setEnabled(true);
				patientId.setEnabled(true);
				scanBarcode.setEnabled(true);
			} else {
				patientIdTextView.setEnabled(false);
				patientId.setEnabled(false);
				scanBarcode.setEnabled(false);
			}
		}

	}

	@Override
	public boolean onLongClick(View v) {
		return false;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (v == age) {
			updateDob();
		}
		return true;
	}

	/**
	 * Updates the DOB picker date
	 */
	private void updateDob() {
		// Calculate dob
		if (!"".equals(App.get(age))) {
			dob = Calendar.getInstance();
			int a = dob.get(Calendar.YEAR) - Integer.parseInt(App.get(age));
			int year = dob.get(Calendar.YEAR) - a;
			dob.add(Calendar.YEAR, -year);
			dobPicker.updateDate(dob.get(Calendar.YEAR),
					dob.get(Calendar.MONTH), dob.get(Calendar.DAY_OF_MONTH));
		}
	}
}
