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
public class SputumSubmissionActivity extends AbstractFragmentActivity
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
	
	MyTextView		    viewedSputumVideoTextView;
	
	MyRadioGroup 		viewedSputumVideoGroup;
	MyRadioButton 		noViewedSputumVideo;
	MyRadioButton 		yesViewedSputumVideo;
	MyRadioButton 		refuseViewedSputumVideo;
	MyRadioButton 		unknownViewedSputumVideo;
	MyRadioButton 		unavailableViewedSputumVideo;
	
	MyTextView		    sputumTimeTextView;
	
	MyRadioGroup 		sputumTimeGroup;
	MyRadioButton 		spotSputumTime;
	MyRadioButton 		secondDaySputumTime;
	MyRadioButton 		thirdDaySputumTime;
	MyRadioButton 		moreThirdDaySputumTime;
	MyRadioButton 		unknownSputumTime;
	
	MyTextView			sputumStatusTextView;
	
	MyRadioGroup 		sputumStatusGroup;
	MyRadioButton 		acceptSputumStatus;
	MyRadioButton 		rejectSputumStatus;
	
	MyTextView			rejectionReasonTextView;
	
	MyRadioGroup 		rejectionReasonGroup;
	MyRadioButton 		salivaRejectionReason;
	MyRadioButton 		foodParticlesRejectionReason;
	MyRadioButton 		insufficientRejectionReason;
	MyRadioButton 		oldRejectionReason;
	
	MyTextView 			labTestIdTextView;
	MyEditText			labTestId;
	MyButton			scanBarcodeLabTestId;
	
	MyTextView			screenerInstructionOneTextView;
	MyTextView			screenerInstructionOne;
	
	MyTextView			screenerInstructionTwoTextView;
	MyTextView			screenerInstructionTwo;
	
	MyTextView			screenerInstructionThreeTextView;
	MyTextView			screenerInstructionThree;
	
	MyTextView 			sputumSubmissionSpace;
	
	MyButton 			saveButton;
			
	
	String firstNameValue = "";
	String lastNameValue = "";
	private static Toast toast;
	
	View[] visibleViews;
	View[] goneViews;
	

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
		
		FORM_NAME = "Sputum Submission Form";
		TAG = "SputumSubmissionActivity";
		PAGE_COUNT = 6;
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
        
		viewedSputumVideoTextView = new MyTextView (context, R.style.text, R.string.video_viewed);
		
		noViewedSputumVideo = new MyRadioButton(context, R.string.no, R.style.radio,
				R.string.no);
		yesViewedSputumVideo = new MyRadioButton(context, R.string.yes, R.style.radio,
				R.string.yes);
		refuseViewedSputumVideo = new MyRadioButton(context, R.string.refuse, R.style.radio,
				R.string.refuse);
		unknownViewedSputumVideo = new MyRadioButton(context, R.string.unknown, R.style.radio,
				R.string.unknown);
		unavailableViewedSputumVideo = new MyRadioButton(context, R.string.video_unavailable, R.style.radio,
				R.string.video_unavailable);
		
		viewedSputumVideoGroup = new MyRadioGroup(context,
				new MyRadioButton[] { noViewedSputumVideo, yesViewedSputumVideo, refuseViewedSputumVideo, unknownViewedSputumVideo, unavailableViewedSputumVideo }, R.string.sputum_video,
				R.style.radio, App.isLanguageRTL(),1);
		
		sputumTimeTextView = new MyTextView (context, R.style.text, R.string.sputum_time);
		
		spotSputumTime = new MyRadioButton(context, R.string.spot, R.style.radio,
				R.string.spot);
		spotSputumTime.setPadding(0, 0, 0, 30);
		secondDaySputumTime = new MyRadioButton(context, R.string.second_day, R.style.radio,
				R.string.second_day);
		secondDaySputumTime.setPadding(0, 0, 0, 30);
		thirdDaySputumTime = new MyRadioButton(context, R.string.third_day, R.style.radio,
				R.string.third_day);
		moreThirdDaySputumTime = new MyRadioButton(context, R.string.after_third_day, R.style.radio,
				R.string.after_third_day);
		unknownSputumTime = new MyRadioButton(context, R.string.unknown, R.style.radio,
				R.string.unknown);
		
		sputumTimeGroup = new MyRadioGroup(context,
				new MyRadioButton[] { spotSputumTime, secondDaySputumTime, thirdDaySputumTime, moreThirdDaySputumTime, unknownSputumTime }, R.string.sputum_time,
				R.style.radio, App.isLanguageRTL(),1);
		
		sputumStatusTextView = new MyTextView (context, R.style.text, R.string.sputum_status);
		acceptSputumStatus = new MyRadioButton(context, R.string.accept, R.style.radio,
				R.string.accept);
		rejectSputumStatus = new MyRadioButton(context, R.string.reject, R.style.radio,
				R.string.reject);
		
		sputumStatusGroup = new MyRadioGroup(context,
				new MyRadioButton[] { acceptSputumStatus, rejectSputumStatus }, R.string.sputum_status,
				R.style.radio, App.isLanguageRTL(),0);
		
		
		//sputumStatus = new MySpinner (context, getResources ().getStringArray (R.array.sputum_status_option), R.string.sputum_status, R.string.option_hint); 
		
		rejectionReasonTextView = new MyTextView (context, R.style.text, R.string.rejection_reason);
		
		salivaRejectionReason = new MyRadioButton(context, R.string.saliva, R.style.radio,
				R.string.saliva);
		foodParticlesRejectionReason = new MyRadioButton(context, R.string.food_particles, R.style.radio,
				R.string.food_particles);
		insufficientRejectionReason = new MyRadioButton(context, R.string.insufficient_quantity, R.style.radio,
				R.string.insufficient_quantity);
		oldRejectionReason = new MyRadioButton(context, R.string.more_than_three_days, R.style.radio,
				R.string.more_than_three_days);
		
		rejectionReasonGroup = new MyRadioGroup(context,
				new MyRadioButton[] { salivaRejectionReason, foodParticlesRejectionReason, insufficientRejectionReason, oldRejectionReason }, R.string.sputum_status,
				R.style.radio, App.isLanguageRTL(),1);
		
		labTestIdTextView = new MyTextView (context, R.style.text, R.string.lab_test_id);
		labTestId = new MyEditText (context, R.string.lab_test_id, R.string.lab_test_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.labTestIdLength, false);
		scanBarcodeLabTestId = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.scan_nhls_code, R.string.scan_nhls_code);
	
		screenerInstructionOneTextView = new MyTextView (context, R.style.text, R.string.screenerInstructionHeading);
		screenerInstructionOneTextView.setTypeface(null, Typeface.BOLD);
		screenerInstructionOneTextView.setTextColor(getResources().getColor(
				R.color.GreenShade));
		screenerInstructionOne = new MyTextView (context, R.style.text, R.string.screener_instruction_verify_patient);
		screenerInstructionOne.setGravity(Gravity.CENTER);
		screenerInstructionOne.setTextColor(getResources().getColor(R.color.Green));
		screenerInstructionOne.setBackgroundResource(R.color.White);
		screenerInstructionOne.setTypeface(null, Typeface.ITALIC);
		
		screenerInstructionTwoTextView = new MyTextView (context, R.style.text, R.string.screenerInstructionHeading);
		screenerInstructionTwoTextView.setTypeface(null, Typeface.BOLD);
		screenerInstructionTwoTextView.setTextColor(getResources().getColor(
				R.color.GreenShade));
		screenerInstructionTwo = new MyTextView (context, R.style.text, R.string.screener_instruction_for_sputum_accept);
		screenerInstructionTwo.setGravity(Gravity.CENTER);
		screenerInstructionTwo.setTextColor(getResources().getColor(R.color.Green));
		screenerInstructionTwo.setBackgroundResource(R.color.White);
		screenerInstructionTwo.setTypeface(null, Typeface.ITALIC);
	  	
	  	screenerInstructionThreeTextView = new MyTextView (context, R.style.text, R.string.empty_string);
		screenerInstructionThreeTextView.setTypeface(null, Typeface.BOLD);
		screenerInstructionThreeTextView.setTextColor(getResources().getColor(
				R.color.GreenShade));
		screenerInstructionThree = new MyTextView (context, R.style.text, R.string.empty_string);
		screenerInstructionThree.setGravity(Gravity.CENTER);
		screenerInstructionThree.setTextColor(getResources().getColor(R.color.Green));
		screenerInstructionThree.setBackgroundResource(R.color.White);
		screenerInstructionThree.setTypeface(null, Typeface.ITALIC);
		
		sputumSubmissionSpace = new MyTextView(context, R.style.text,
				R.string.sputumSubmission_space);
		
		saveButton = new MyButton(context, R.style.button,
				R.drawable.custom_button_beige, R.string.submit_form,
				R.string.submit_form);
		
		View[][] viewGroups = { 
					{formDateTextView, formDateButton, patientIdTextView, patientId, scanBarcode, firstNameTextView, firstName, surnameTextView, surname,  searchPatientButton}, 
				    {screenerInstructionOneTextView, screenerInstructionOne},
					{viewedSputumVideoTextView, viewedSputumVideoGroup },
					{sputumTimeTextView, sputumTimeGroup, },
					{screenerInstructionTwoTextView,screenerInstructionTwo, sputumStatusTextView, sputumStatusGroup ,rejectionReasonTextView, rejectionReasonGroup},
					{ screenerInstructionThreeTextView,screenerInstructionThree,labTestIdTextView,labTestId,scanBarcodeLabTestId ,sputumSubmissionSpace,saveButton}
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
		navigationSeekbar.setOnSeekBarChangeListener (this);
		
		views = new View[] {patientId, firstName, surname,labTestId,
				unknownViewedSputumVideo,unknownSputumTime,acceptSputumStatus,salivaRejectionReason};
		
		
		View[] setListener = new View[]{
				formDateButton,firstButton,lastButton,clearButton,saveButton,scanBarcode,searchPatientButton,scanBarcodeLabTestId,
				spotSputumTime,secondDaySputumTime,thirdDaySputumTime,moreThirdDaySputumTime,unknownSputumTime,acceptSputumStatus,rejectSputumStatus,
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
		
		goneViews = new View[]{rejectionReasonTextView,rejectionReasonGroup,screenerInstructionThree,screenerInstructionThreeTextView};
		visibleViews = new View[]{labTestIdTextView,labTestId,scanBarcodeLabTestId,sputumSubmissionSpace};
	}

	@Override
	public void initView (View[] views)
	{
		super.initView (views);
		formDate = Calendar.getInstance ();
		updateDisplay ();
		
		// Hide Views
		for (View v : goneViews) {
			v.setVisibility(View.GONE);
		}
		
		// Show Views
		for (View v : visibleViews) {
			v.setVisibility(View.VISIBLE);
		}
		
		screenerInstructionThreeTextView.setText(R.string.empty_string);
		screenerInstructionThree.setText(R.string.empty_string);
        firstNameValue = "";
        lastNameValue = "";
        firstName.setText(firstNameValue);
		surname.setText(lastNameValue);
	}

	@Override
	public void updateDisplay ()
	{
		formDateButton.setText (DateFormat.format ("dd-MMM-yyyy", formDate));
		
		if(formDate.getTime().after(new Date())){
			
			if (toast != null)
			    toast.cancel();
			
			formDateButton.setTextColor(getResources().getColor(R.color.Red));
			toast = Toast.makeText(SputumSubmissionActivity.this,"Form Date: "+getResources().getString(R.string.invalid_date_or_time), Toast.LENGTH_SHORT);
			toast.show();
		}
		else
			formDateButton.setTextColor(getResources().getColor(R.color.IRDTitle));
	}

	@Override
	public boolean validate ()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer ();
		// Validate mandatory controls
		View[] mandatory = {patientId};
		for (View v : mandatory)
		{
			if(v.isEnabled())
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
							String[][] result = serverService.getPatientName (patient,"pid");
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
							firstName.setText(firstNameValue);
							surname.setText(lastNameValue);
							
							super.onPostExecute (result);
							loading.dismiss ();
							if (!result.equals ("SUCCESS"))
							{
								Toast toast = Toast.makeText (SputumSubmissionActivity.this, "", App.getDelay ());
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
			
			final ContentValues values = new ContentValues ();
			values.put ("formDate", App.getSqlDate (formDate));
			values.put ("location", App.getLocation());
			values.put ("patientId", App.get (patientId));
			values.put ("LabTestId", App.get(labTestId));
			
			final ArrayList<String[]> observations = new ArrayList<String[]> ();
			observations.add (new String[] {"Viewed Sputum Video", noViewedSputumVideo.isChecked() ? "No" : (yesViewedSputumVideo.isChecked() ? "Yes" :  (refuseViewedSputumVideo.isChecked() ? "Refuse" : (unknownViewedSputumVideo.isChecked() ? "Unknown" :"Video unavailable")))});
			observations.add (new String[] {"Sputum Submission Time", spotSputumTime.isChecked() ? "Spot (Same day as screening)" : (secondDaySputumTime.isChecked() ? "2nd Day Morning (the day after screening)" :  (thirdDaySputumTime.isChecked() ? "3rd Day Morning" : (moreThirdDaySputumTime.isChecked() ? "More than 3 days ago" :"Unknown")))});
			observations.add (new String[] {"Sputum Sample", acceptSputumStatus.isChecked() ? "Accept" :"Reject"});
			if (rejectionReasonGroup.getVisibility() == View.VISIBLE)
				observations.add (new String[] {"Sputum Rejection Reason", salivaRejectionReason.isChecked() ? "Saliva" :(foodParticlesRejectionReason.isChecked() ? "Food Particles" : (insufficientRejectionReason.isChecked() ? "Insufficient Quantity" : "More than 3 days old"))});
			if (labTestId.getVisibility() == View.VISIBLE)
			observations.add (new String[] {"Lab Test Id", App.get (labTestId)});
			
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
					String result = serverService.saveSputumSubmission (FormType.SPUTUM_SUBMISSION, values, observations.toArray (new String[][] {}));
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
						App.getAlertDialog (SputumSubmissionActivity.this, AlertType.INFO, getResources ().getString (R.string.inserted)).show ();
						initView (views);
					}
					else
					{
						App.getAlertDialog (SputumSubmissionActivity.this, AlertType.ERROR, result).show ();
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
		
		if (requestCode == Barcode.BARCODE_RESULT_TEST_ID)
		{
			if (resultCode == RESULT_OK)
			{
				String str = data.getStringExtra (Barcode.SCAN_RESULT);
				
				if (str.length() <= 11)
				{
					labTestId.setText (str);
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
		else if (requestCode == Barcode.BARCODE_RESULT || requestCode == GET_PATIENT_ID)
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
		else if (view == scanBarcodeLabTestId)
		{
			Intent intent = new Intent (Barcode.BARCODE_INTENT);
			intent.putExtra (Barcode.SCAN_MODE, Barcode.QR_MODE);
			startActivityForResult (intent, Barcode.BARCODE_RESULT_TEST_ID);
		}
		else if (view == spotSputumTime || view == secondDaySputumTime || view == thirdDaySputumTime || view == moreThirdDaySputumTime || view == unknownSputumTime){
			boolean check = moreThirdDaySputumTime.isChecked();
			if(check)
				screenerInstructionTwo.setText(R.string.screener_instruction_for_sputum_reject);
			else
				screenerInstructionTwo.setText(R.string.screener_instruction_for_sputum_accept);
		}
		else if (view == acceptSputumStatus || view == rejectSputumStatus){
			boolean check = acceptSputumStatus.isChecked(); 
			
			if(!check)
			{
				labTestIdTextView.setVisibility(View.GONE);
				labTestId.setVisibility(View.GONE);
				scanBarcodeLabTestId.setVisibility(View.GONE);
				rejectionReasonTextView.setVisibility(View.VISIBLE);
				rejectionReasonGroup.setVisibility(View.VISIBLE);
				screenerInstructionThreeTextView.setText(R.string.screenerInstructionHeading);
				screenerInstructionThree.setText(R.string.screener_instruction_repeat);
				screenerInstructionThree.setVisibility(View.VISIBLE);
				screenerInstructionThreeTextView.setVisibility(View.VISIBLE);
				sputumSubmissionSpace.setVisibility(View.VISIBLE);
			}
			else
			{
				labTestIdTextView.setVisibility(View.VISIBLE);
				labTestId.setVisibility(View.VISIBLE);
				scanBarcodeLabTestId.setVisibility(View.VISIBLE);
				rejectionReasonTextView.setVisibility(View.GONE);
				rejectionReasonGroup.setVisibility(View.GONE);
				screenerInstructionThreeTextView.setText(R.string.empty_string);
				screenerInstructionThree.setText(R.string.empty_string);
				screenerInstructionThree.setVisibility(View.GONE);
				screenerInstructionThreeTextView.setVisibility(View.GONE);
				sputumSubmissionSpace.setVisibility(View.VISIBLE);
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
