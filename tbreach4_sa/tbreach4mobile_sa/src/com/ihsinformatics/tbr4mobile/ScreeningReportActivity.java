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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.INotificationSideChannel;
import android.support.v4.view.ViewPager;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class ScreeningReportActivity extends AbstractFragmentActivity
{
	static final int	GET_PATIENT_ID	= 1;
	// Views displayed in pages, sorted w.r.t. appearance on pager
	
	MyTextView			screeningDateTextView;
	MyButton			formDateButton;
	MyButton			fetchScreeningButton;
	MyTextView			screenerNameTextView;
	MyTextView			locationTextView;
	MyTextView			screeningStatsTextView;
	MyTextView			totalScreenedTextView;
	MyTextView			numberOfSuspectTextView;
	MyTextView			numberOfNonSuspectTextView;
	MyTextView			numberOfSputumSubmittedTextView;
	MyTextView			ListOfScreenedTextView;
	
	LinearLayout		screeningStatsLinearLayout;
	LinearLayout		screeningInfoLinearLayout;
	
	Context				mContext;
	ArrayList results;
	
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
		mContext =  this;
		
		FORM_NAME = "Screening Report";
		TAG = "ScreeningReportActivity";
		PAGE_COUNT = 2;
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
		
		screeningDateTextView = new MyTextView (context, R.style.text, R.string.screening_date);
		screeningDateTextView.setTextSize(17);
		formDateButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.screening_date, R.string.screening_date);
		formDateButton.setTextSize(17);
		
		fetchScreeningButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.fetch_screening_info, R.string.fetch_screening_info);
		LinearLayout.LayoutParams paramsCenter = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramsCenter.setMargins(0, 10, 0, 0);
		paramsCenter.gravity = Gravity.CENTER;
		fetchScreeningButton.setLayoutParams(paramsCenter);
		fetchScreeningButton.setTextSize(17);
	
		screenerNameTextView = new MyTextView (context, R.style.text, R.string.screener_name);
		LinearLayout.LayoutParams paramCenter1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramCenter1.setMargins(0, 20, 0, 0);
		paramCenter1.gravity = Gravity.CENTER;
		screenerNameTextView.setLayoutParams(paramCenter1);
		screenerNameTextView.setTextSize(17);
		
		locationTextView = new MyTextView (context, R.style.text, R.string.screener_location);
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		param.gravity = Gravity.CENTER;
		locationTextView.setLayoutParams(param);
		locationTextView.setTextSize(17);
		
		LinearLayout.LayoutParams paramLeft2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramLeft2.setMargins(0, 20, 0, 0);
		paramLeft2.gravity = Gravity.LEFT;
		
		screeningStatsTextView= new MyTextView (context, R.style.text, R.string.screening_stats);
		screeningStatsTextView.setLayoutParams(paramLeft2);
		screeningStatsTextView.setTextSize(17);
		
		LinearLayout.LayoutParams paramsLeft = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramsLeft.setMargins(0, 10, 0, 0);
		paramsLeft.gravity = Gravity.LEFT;
		totalScreenedTextView= new MyTextView (context, R.style.text, R.string.screening_stats);
		totalScreenedTextView.setLayoutParams(paramsLeft);
		totalScreenedTextView.setTextSize(17);
		
		LinearLayout.LayoutParams paramsLeft1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramsLeft1.gravity = Gravity.LEFT;
		
		numberOfSuspectTextView= new MyTextView (context, R.style.text, R.string.suspects_no);
		numberOfSuspectTextView.setLayoutParams(paramsLeft1);
		numberOfSuspectTextView.setTextSize(17);
		
		numberOfNonSuspectTextView= new MyTextView (context, R.style.text, R.string.nonsuspects_no);
		numberOfNonSuspectTextView.setLayoutParams(paramsLeft1);
		numberOfNonSuspectTextView.setTextSize(17);
		
		numberOfSputumSubmittedTextView= new MyTextView (context, R.style.text, R.string.sputum_submitted_no);
		numberOfSputumSubmittedTextView.setLayoutParams(paramsLeft1);
		numberOfSputumSubmittedTextView.setTextSize(17);
		
		ListOfScreenedTextView= new MyTextView (context, R.style.text, R.string.screening_list);
		paramsLeft1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		paramsLeft1.setMargins(0, 10, 0, 0);
		ListOfScreenedTextView.setLayoutParams(paramsLeft1);
		ListOfScreenedTextView.setTextSize(17);
		ListOfScreenedTextView.setTextColor(getResources().getColor(R.color.IRDTitle));
				
		
		screeningInfoLinearLayout = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 10, 0, 0);
		params.gravity = Gravity.CENTER;
		screeningInfoLinearLayout.setLayoutParams(params);
		screeningInfoLinearLayout.setOrientation(LinearLayout.VERTICAL);
		
		
		clearButton.setVisibility(View.GONE);
		
		View[][] viewGroups = { 
				    
				    {screeningDateTextView, formDateButton, fetchScreeningButton, screenerNameTextView, locationTextView, /*screeningStatsLinearLayout*/screeningStatsTextView, totalScreenedTextView, numberOfSuspectTextView, numberOfNonSuspectTextView, numberOfSputumSubmittedTextView,},
				    {ListOfScreenedTextView, screeningInfoLinearLayout},
					
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
		View[] setListener = new View[]{formDateButton,clearButton,fetchScreeningButton};
		
		for (View v : setListener) {
			if (v instanceof Spinner) {
				((Spinner) v).setOnItemSelectedListener(this);
			} else if (v instanceof CheckBox) {
				((CheckBox) v).setOnCheckedChangeListener(this);
			}  else if (v instanceof Button) {
				((Button) v).setOnClickListener(this);
			}  else if (v instanceof RadioButton) {
				((RadioButton) v).setOnClickListener(this);
			}
		}
		
		navigationSeekbar.setOnSeekBarChangeListener (this);
		
		views = new View[] {};
		
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
		
		
		screenerNameTextView.setText(getResources().getString(R.string.screener_name));
		screenerNameTextView.setVisibility(View.GONE);
		locationTextView.setText(getResources().getString(R.string.screener_location));
		locationTextView.setVisibility(View.GONE);
		totalScreenedTextView.setText(getResources().getString(R.string.total_screened));
		totalScreenedTextView.setVisibility(View.GONE);
		numberOfSuspectTextView.setText(getResources().getString(R.string.suspects_no));
		numberOfSuspectTextView.setVisibility(View.GONE);
		numberOfNonSuspectTextView.setText(getResources().getString(R.string.nonsuspects_no));
		numberOfNonSuspectTextView.setVisibility(View.GONE);
		numberOfSputumSubmittedTextView.setText(getResources().getString(R.string.sputum_submitted_no));
		numberOfSputumSubmittedTextView.setVisibility(View.GONE);
		
		screeningStatsTextView.setVisibility(View.GONE);
		ListOfScreenedTextView.setVisibility(View.GONE);
		screeningInfoLinearLayout.removeAllViewsInLayout();  
	}

	@Override
	public void updateDisplay ()
	{
		formDateButton.setText (DateFormat.format ("dd-MMM-yyyy", formDate));	
		
		//screeningStatsTextView.setText(Html.fromHtml(getResources().getString(R.string.screening_stats)+ "  <font color=#f58220>" + DateFormat.format ("dd-MMM-yyyy", formDate) + "</font>"));
		
		if(formDate.getTime().after(new Date())){
			
			if (toast != null)
			    toast.cancel();
			
			formDateButton.setTextColor(getResources().getColor(R.color.Red));
			toast = Toast.makeText(ScreeningReportActivity.this,"Form Date: "+getResources().getString(R.string.invalid_date_or_time), Toast.LENGTH_SHORT);
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
		View[] mandatory = {};
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
		
		if(valid){
			// Validate range
			if (formDate.getTime ().after (new Date ()))
			{
				valid = false;
				message.append (formDateButton.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
			}
			
			
		}
		return valid;
	}
	

	public boolean submit ()
	{
		if (validate ())
		{
		
				final ContentValues values = new ContentValues ();
				values.put ("formDate", App.getSqlDate (formDate));
				
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
						
						results = serverService.getScreeningStats(App.getUsername(),App.getSqlDate (formDate));
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
						if (result.equals ("SUCCESS"))
						{
							
							screeningInfoLinearLayout.removeAllViewsInLayout(); 
							
							screenerNameTextView.setVisibility(View.VISIBLE);
							screenerNameTextView.setText(Html.fromHtml(getResources().getString(R.string.screener_name)+ "  <font color=#f58220>" + App.getScreenerName() + "</font> (" + App.getUsername() + ")"));
							locationTextView.setVisibility(View.VISIBLE);
							locationTextView.setText(Html.fromHtml(getResources().getString(R.string.screener_location)+ "  <font color=#f58220>" + App.getLocation() + "</font>" ));
							
							totalScreenedTextView.setVisibility(View.VISIBLE);
							totalScreenedTextView.setText(Html.fromHtml(getResources().getString(R.string.total_screened)+ "  <font color=#f58220>" + results.get(0) + "</font>"));
							numberOfSuspectTextView.setVisibility(View.VISIBLE);
							numberOfSuspectTextView.setText(Html.fromHtml(getResources().getString(R.string.suspects_no)+ "  <font color=#f58220>" + results.get(2) + "</font>"));
							numberOfNonSuspectTextView.setVisibility(View.VISIBLE);
							numberOfNonSuspectTextView.setText(Html.fromHtml(getResources().getString(R.string.nonsuspects_no)+ "  <font color=#f58220>" + results.get(3) + "</font>"));
							numberOfSputumSubmittedTextView.setVisibility(View.VISIBLE);
							numberOfSputumSubmittedTextView.setText(Html.fromHtml(getResources().getString(R.string.sputum_submitted_no)+ "  <font color=#f58220>" + results.get(1) + "</font>"));
							
							screeningStatsTextView.setVisibility(View.VISIBLE);
							screeningStatsTextView.setText(Html.fromHtml(getResources().getString(R.string.screening_stats)+ "  <font color=#f58220>" + DateFormat.format ("dd-MMM-yyyy", formDate) + "</font>"));
						
							ListOfScreenedTextView.setVisibility(View.VISIBLE);
							
							int total = Integer.parseInt(results.get(0).toString());
							if(total != 0){
								
								int j = 4;
								for(int i = 0; i<total; i++){
									
									final MyTextView tv1 = new MyTextView(mContext);
									int count = i + 1;
									
									String s = String.valueOf(count);
									
									if(total < 90){
									
										if(count < 10)
											s = "0"+s;
									}
									else{
										
										if(count < 10)
											s = "00"+s;
										else if (count < 100)
											s = "0"+s;
										
									}
									
									String resultArray[] = results.get(j).toString().split(";:;");
									
									String text = "";
									
									if(resultArray[2].equals("Non-Suspect"))
										text = "<font color=#f58220> " +  s  +". </font>" + resultArray[0];
									else
										text = "<font color=#f58220> " +  s  +". </font> <font color=#656a23> <b>" + resultArray[0] + "</b> </font>";
									tv1.setText(Html.fromHtml(text));
									tv1.setTag(resultArray[1]);
									
									if(resultArray[2].equals("Suspect")){
										
										tv1.setOnClickListener (new OnClickListener ()
										{
											@Override
											public void onClick (View view)
											{
										
												
												viewPatientDetail(tv1.getTag().toString());
												
											}
										});
										
									}
									
									j++;
									screeningInfoLinearLayout.addView(tv1);
									
								}
							}
							
						}
						else
						{
							App.getAlertDialog (ScreeningReportActivity.this, AlertType.ERROR, result).show ();
						}
					}
				};
				updateTask.execute ("");
			}
	    
		return true;
	}

	

	@Override
	public void onClick (View view)
	{
		if (view == formDateButton)
		{
			showDialog (DATE_DIALOG_ID);
		}
		else if (view == fetchScreeningButton){
			submit();
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

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	public void viewPatientDetail(String pid){
		

		//finish();
		Intent intent = new Intent (mContext, PatientReportActivity.class);
		intent.putExtra ("pid", pid);
		this.startActivityForResult(intent, -1);
		
	}

}
