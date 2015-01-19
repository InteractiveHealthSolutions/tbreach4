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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class SputumResultActivity extends AbstractFragmentActivity
{
	static final int	GET_PATIENT_ID	= 1;
	// Views displayed in pages, sorted w.r.t. appearance on pager
	
	MyTextView 			labTestIdTextView;
	MyEditText			labTestId;
	MyButton			scanBarcodeLabTestId;
	
	MyTextView			dateOfTestReportTextView;
	MyButton			dateOfTestReportButton;
	
	MyTextView			formDateTextView;
	MyButton			formDateButton;
	
	MyTextView		    sputumAcceptedTextView;
	MySpinner			sputumAccepted;
	
	MyTextView			rejectionReasonTextView;
	MySpinner			rejectionReason;
	
	MyTextView			genexpertResultTextView;
	MySpinner			genexpertResult;
	
	MyTextView			rifResultTextView;
	MySpinner			rifResult;
	
	MyTextView			mtbBurdenTextView;
	MySpinner			mtbBurden;
	
	MyTextView 			errorCodeTextView;
	MyEditText			errorCode;


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
		FORM_NAME = "Sputum Result";
		TAG = "SputumResultActivity";
		PAGE_COUNT = 3;
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
		
		labTestIdTextView = new MyTextView (context, R.style.text, R.string.lab_test_id);
		labTestId = new MyEditText (context, R.string.lab_test_id, R.string.lab_test_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.labTestIdLength, false);
		scanBarcodeLabTestId = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.scan_barcode, R.string.scan_barcode);
		
		dateOfTestReportTextView = new MyTextView (context, R.style.text, R.string.date_test_report);
		dateOfTestReportButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.date_test_report, R.string.date_test_report);
		
		formDateTextView = new MyTextView (context, R.style.text, R.string.date_result_recieved);
		formDateButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.date_result_recieved, R.string.date_result_recieved);
		
		sputumAcceptedTextView = new MyTextView (context, R.style.text, R.string.sample_accepted);
		sputumAccepted = new MySpinner (context, getResources ().getStringArray (R.array.option_yes_no), R.string.sample_accepted, R.string.option_hint); 
		
		rejectionReasonTextView = new MyTextView (context, R.style.text, R.string.rejection_reason);
		rejectionReason = new MySpinner (context, getResources ().getStringArray (R.array.rejection_reason_option), R.string.rejection_reason, R.string.option_hint); 
		
		genexpertResultTextView = new MyTextView (context, R.style.text, R.string.gxp_result);
		genexpertResult = new MySpinner (context, getResources ().getStringArray (R.array.gxp_result_option), R.string.gxp_result, R.string.option_hint); 
		
		rifResultTextView = new MyTextView (context, R.style.text, R.string.rif_result);
		rifResult = new MySpinner (context, getResources ().getStringArray (R.array.rif_result_option), R.string.rif_result, R.string.option_hint); 
		
		mtbBurdenTextView = new MyTextView (context, R.style.text, R.string.mtb_burden);
		mtbBurden = new MySpinner (context, getResources ().getStringArray (R.array.mtb_burden_option), R.string.mtb_burden, R.string.option_hint); 
	
		errorCodeTextView = new MyTextView (context, R.style.text, R.string.error_code);
		errorCode = new MyEditText (context, R.string.error_code, R.string.error_code, InputType.TYPE_CLASS_NUMBER, R.style.edit, 4, false);
		
		
		View[][] viewGroups = { 
					{labTestIdTextView, labTestId, scanBarcodeLabTestId,  dateOfTestReportTextView, dateOfTestReportButton, formDateTextView, formDateButton}, 
				    {sputumAcceptedTextView, sputumAccepted, rejectionReasonTextView, rejectionReason, genexpertResultTextView, genexpertResult},
					{rifResultTextView, rifResult, mtbBurdenTextView, mtbBurden, errorCodeTextView, errorCode },
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
		dateOfTestReportButton.setOnClickListener(this);
		formDateButton.setOnClickListener (this);
		firstButton.setOnClickListener (this);
		lastButton.setOnClickListener (this);
		clearButton.setOnClickListener (this);
		saveButton.setOnClickListener (this);
		scanBarcodeLabTestId.setOnClickListener(this);
		navigationSeekbar.setOnSeekBarChangeListener (this);
		views = new View[] {labTestId, sputumAccepted, rejectionReason, genexpertResult, rifResult, mtbBurden, errorCode};
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
		testDate = null;
		updateDisplay ();
		
		rejectionReasonTextView.setEnabled(false);
		rejectionReason.setEnabled(false);	
	}

	@Override
	public void updateDisplay ()
	{
		formDateButton.setText (DateFormat.format ("dd-MMM-yyyy", formDate));
		if(testDate == null)
			dateOfTestReportButton.setText(R.string.date_test_report);
		else
			dateOfTestReportButton.setText(DateFormat.format ("dd-MMM-yyyy", testDate));		
	}

	@Override
	public boolean validate ()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer ();
		// Validate mandatory controls
		View[] mandatory = {labTestId, errorCode};
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
			  
		    if(labTestId.isEnabled())
		    {
		    	 if (!(App.get (labTestId).length() == 11))
				{
					valid = false;
					message.append (labTestId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
					labTestId.setTextColor (getResources ().getColor (R.color.Red));
				}
		    }
				
		}
		// Validate range
		if (formDate.getTime ().after (new Date ()))
		{
			valid = false;
			message.append (formDateButton.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
		}
		if(testDate != null)
		{
			if (testDate.getTime ().after (new Date ()))
			{
				valid = false;
				message.append (dateOfTestReportButton.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
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
			values.put ("LabTestId", App.get (labTestId));
			
			final ArrayList<String[]> observations = new ArrayList<String[]> ();
			if(testDate != null)
				observations.add (new String[] {"Date Of Test Report", App.getSqlDate (formDate)});
			if (sputumAccepted.isEnabled())
				observations.add (new String[] {"Sputum Accepted", App.get (sputumAccepted)});
			if (rejectionReason.isEnabled())
				observations.add (new String[] {"Sputum Rejection Reason", App.get (rejectionReason)});
			if (genexpertResult.isEnabled())
				observations.add (new String[] {"GeneXpert Result", App.get (genexpertResult)});
			if (rifResult.isEnabled())
				observations.add (new String[] {"RIF Result", App.get (rifResult)});
			if (mtbBurden.isEnabled())
				observations.add (new String[] {"MTB Burden", App.get (mtbBurden)});
			if (errorCode.isEnabled())
				observations.add (new String[] {"Error Code", App.get (errorCode)});
			if (labTestId.isEnabled())
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
					String result = serverService.saveSputumResult (FormType.SPUTUM_RESULT, values, observations.toArray (new String[][] {}));
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
						App.getAlertDialog (SputumResultActivity.this, AlertType.INFO, getResources ().getString (R.string.inserted)).show ();
						initView (views);
					}
					else
					{
						App.getAlertDialog (SputumResultActivity.this, AlertType.ERROR, result).show ();
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
		}
	}

	@Override
	public void onClick (View view)
	{
		if (view == formDateButton)
		{
			showDialog (DATE_DIALOG_ID);
		}
		else if (view == dateOfTestReportButton)
		{
			showDialog(TEST_DATE_DIALOG_ID);
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
		else if (view == scanBarcodeLabTestId)
		{
			Intent intent = new Intent (Barcode.BARCODE_INTENT);
			intent.putExtra (Barcode.SCAN_MODE, Barcode.QR_MODE);
			startActivityForResult (intent, Barcode.BARCODE_RESULT_TEST_ID);
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
		
		if (spinner == sputumAccepted)
		{
			boolean check = spinner.getSelectedItemPosition () == 0;
			
			rejectionReason.setEnabled(!check);
			rejectionReasonTextView.setEnabled(!check);
			
			genexpertResult.setEnabled(check);
			genexpertResultTextView.setEnabled(check);
			
			rifResultTextView.setEnabled(check);
			rifResult.setEnabled(check);
			
			mtbBurdenTextView.setEnabled(check);
			mtbBurden.setEnabled(check);
			
			errorCodeTextView.setEnabled(check);
			errorCode.setEnabled(check);
			
		}
		else if (spinner == genexpertResult)
		{
			boolean check = spinner.getSelectedItemPosition () == 1;
			
			rifResultTextView.setEnabled(check);
			rifResult.setEnabled(check);
			
			mtbBurdenTextView.setEnabled(check);
			mtbBurden.setEnabled(check);
			
			check = spinner.getSelectedItemPosition () == 2;
			
			errorCodeTextView.setEnabled(check);
			errorCode.setEnabled(check);
			
		}
	
	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
