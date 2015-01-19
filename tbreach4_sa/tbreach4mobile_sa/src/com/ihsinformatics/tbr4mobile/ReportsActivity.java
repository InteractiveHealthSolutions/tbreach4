/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Activity to display mobile reports
 */

package com.ihsinformatics.tbr4mobile;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ihsinformatics.tbr4mobile.custom.MyTextView;
import com.ihsinformatics.tbr4mobile.shared.AlertType;
import com.ihsinformatics.tbr4mobile.util.ServerService;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class ReportsActivity extends Activity implements OnCheckedChangeListener, OnClickListener
{
	public static final String			TAG				= "ReportsActivity";
	public static final String			SEARCH_RESULT	= "SEARCH_RESULT";
	private static final AtomicInteger	counter			= new AtomicInteger ();
	public static ServerService			serverService;
	public static ProgressDialog		loading;

	Button								screeningReportButton;
	Button								dailySummaryButton;
	Button								formsByDateButton;
	Button								performanceGraphButton;
	Button								savedFormsButton;
	ScrollView							searchResultsScrollView;
	ListView							savedFormsListView;
	RadioGroup							reportsRadioGroup;
	Animation							alphaAnimation;

	protected void onCreate (Bundle savedInstanceState)
	{
		setTheme (App.getTheme ());
		setContentView (R.layout.reports);
		super.onCreate (savedInstanceState);
		serverService = new ServerService (this);
		loading = new ProgressDialog (this);
		screeningReportButton = (Button) findViewById (R.reports_id.screeningReportButton);
		dailySummaryButton = (Button) findViewById (R.reports_id.dailyReportButton);
		formsByDateButton = (Button) findViewById (R.reports_id.formsByDateButton);
		performanceGraphButton = (Button) findViewById (R.reports_id.performanceGraphButton);
		savedFormsButton = (Button) findViewById (R.reports_id.savedFormsButton);
		searchResultsScrollView = (ScrollView) findViewById (R.reports_id.resultsScrollView);
		savedFormsListView = (ListView) findViewById (R.reports_id.savedFormsListView);
		reportsRadioGroup = (RadioGroup) findViewById (R.reports_id.reportsRadioGroup);
		alphaAnimation = AnimationUtils.loadAnimation (this, R.anim.alpha_animation);

		screeningReportButton.setOnClickListener (this);
		dailySummaryButton.setOnClickListener (this);
		formsByDateButton.setOnClickListener (this);
		performanceGraphButton.setOnClickListener (this);
		savedFormsButton.setOnClickListener (this);
	}

	@Override
	public void onBackPressed ()
	{
		finish ();
		Intent loginIntent = new Intent (getApplicationContext (), MainMenuActivity.class);
		startActivity (loginIntent);
	}

	@Override
	public void onCheckedChanged (RadioGroup radioGroup, int checkedId)
	{
		// Not implemented
	}

	@Override
	public void onClick (View view)
	{
		if (view == screeningReportButton)
		{
			AsyncTask<String, String, String[][]> reportTask = new AsyncTask<String, String, String[][]> ()
			{
				protected String[][] doInBackground (String... params)
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
					return serverService.getScreeningReport ();
				}

				protected void onPostExecute (String[][] result)
				{
					super.onPostExecute (result);
					loading.dismiss ();
					String[][] reportsList = result;
					// Display a message if no results were found
					if (reportsList == null)
					{
						App.getAlertDialog (ReportsActivity.this, AlertType.INFO, getResources ().getString (R.string.data_access_error)).show ();
					}
					else
					{
						reportsRadioGroup.removeAllViews ();
						for (int i = 0; i < reportsList.length; i++)
						{
							MyTextView textView = new MyTextView (ReportsActivity.this, R.style.text, R.string.empty_string);
							textView.setId (counter.getAndIncrement ());
							textView.setText (reportsList[i][0] + " : " + reportsList[i][1]);
							textView.setTag (reportsList[i][0]);
							reportsRadioGroup.addView (textView);
						}
					}
				}
			};
			reportTask.execute ("");
		}
		else if (view == dailySummaryButton)
		{
			AsyncTask<String, String, String[][]> reportTask = new AsyncTask<String, String, String[][]> ()
			{
				protected String[][] doInBackground (String... params)
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
					return serverService.getDailySummary ();
				}

				protected void onPostExecute (String[][] result)
				{
					super.onPostExecute (result);
					loading.dismiss ();
					String[][] reportsList = result;
					// Display a message if no results were found
					if (reportsList == null)
					{
						App.getAlertDialog (ReportsActivity.this, AlertType.INFO, getResources ().getString (R.string.data_access_error)).show ();
					}
					else
					{
						reportsRadioGroup.removeAllViews ();
						for (int i = 0; i < reportsList.length; i++)
						{
							MyTextView textView = new MyTextView (ReportsActivity.this, R.style.text, R.string.empty_string);
							textView.setId (counter.getAndIncrement ());
							textView.setText (reportsList[i][0] + " : " + reportsList[i][1]);
							textView.setTag (reportsList[i][0]);
							reportsRadioGroup.addView (textView);
						}
					}
				}
			};
			reportTask.execute ("");
		}
		else if (view == formsByDateButton)
		{
			final Calendar date = Calendar.getInstance ();
			OnDateSetListener dateSetCallback = new OnDateSetListener ()
			{
				@Override
				public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth)
				{
					date.set (Calendar.YEAR, year);
					date.set (Calendar.MONTH, monthOfYear);
					date.set (Calendar.DAY_OF_MONTH, dayOfMonth);
					AsyncTask<String, String, String[][]> formsByDateTask = new AsyncTask<String, String, String[][]> ()
					{
						protected String[][] doInBackground (String... params)
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
							return serverService.getFormsByDate (date.getTime ());
						}

						protected void onPostExecute (String[][] result)
						{
							super.onPostExecute (result);
							loading.dismiss ();
							String[][] reportsList = result;
							// Display a message if no results were found
							if (reportsList == null)
							{
								App.getAlertDialog (ReportsActivity.this, AlertType.INFO, getResources ().getString (R.string.data_access_error)).show ();
							}
							else
							{
								reportsRadioGroup.removeAllViews ();
								for (int i = 0; i < reportsList.length; i++)
								{
									MyTextView textView = new MyTextView (ReportsActivity.this, R.style.text, R.string.empty_string);
									textView.setId (counter.getAndIncrement ());
									textView.setText (reportsList[i][0] + " : " + reportsList[i][1]);
									textView.setTag (reportsList[i][0]);
									reportsRadioGroup.addView (textView);
								}
							}
						}
					};
					formsByDateTask.execute ("");
				}
			};
			DatePickerDialog datePickerDialog = new DatePickerDialog (this, dateSetCallback, date.get (Calendar.YEAR), date.get (Calendar.MONTH), date.get (Calendar.DAY_OF_MONTH));
			datePickerDialog.show ();
		}
		else if (view == performanceGraphButton)
		{
			AsyncTask<String, String, String[][]> searchTask = new AsyncTask<String, String, String[][]> ()
			{
				protected String[][] doInBackground (String... params)
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
					return serverService.getPerfornamceData ();
				}

				protected void onPostExecute (String[][] result)
				{
					super.onPostExecute (result);
					loading.dismiss ();
					String[][] reportsList = result;
					// Display a message if no results were found
					if (reportsList == null)
					{
						App.getAlertDialog (ReportsActivity.this, AlertType.INFO, getResources ().getString (R.string.data_access_error)).show ();
					}
					else
					{
						try
						{
							reportsRadioGroup.removeAllViews ();
							JSONArray nonSuspectJson = new JSONArray (result[0][1]);
							MyTextView nonSuspectTextView = new MyTextView (ReportsActivity.this, R.style.text, R.string.empty_string);
							nonSuspectTextView.setId (counter.getAndIncrement ());
							nonSuspectTextView.setText (result[0][0]);
							nonSuspectTextView.setTag (result[0][0]);
							reportsRadioGroup.addView (nonSuspectTextView);
							for (int i = 0; i < nonSuspectJson.length (); i++)
							{
								JSONObject jsonObj = nonSuspectJson.getJSONObject (i);
								String month = jsonObj.getString ("Month");
								String total = jsonObj.getString ("Total");
								MyTextView textView = new MyTextView (ReportsActivity.this, R.style.text, R.string.empty_string);
								textView.setId (counter.getAndIncrement ());
								textView.setText (month + " : " + total);
								textView.setTag (result[0][0] + month);
								reportsRadioGroup.addView (textView);
							}
							JSONArray suspectJson = new JSONArray (result[1][1]);
							MyTextView suspectTextView = new MyTextView (ReportsActivity.this, R.style.text, R.string.empty_string);
							suspectTextView.setId (counter.getAndIncrement ());
							suspectTextView.setText (result[1][0]);
							suspectTextView.setTag (result[1][0]);
							reportsRadioGroup.addView (suspectTextView);
							for (int i = 0; i < suspectJson.length (); i++)
							{
								JSONObject jsonObj = suspectJson.getJSONObject (i);
								String month = jsonObj.getString ("Month");
								String total = jsonObj.getString ("Total");
								MyTextView textView = new MyTextView (ReportsActivity.this, R.style.text, R.string.empty_string);
								textView.setId (counter.getAndIncrement ());
								textView.setText (month + " : " + total);
								textView.setTag (result[1][0] + month);
								reportsRadioGroup.addView (textView);
							}
						}
						catch (JSONException e)
						{
							e.printStackTrace ();
						}
					}
				}
			};
			searchTask.execute ("");
		}
		else if (view == savedFormsButton)
		{
			String[] savedFiles = serverService.getSavedFormFiles ();
			ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, android.R.id.text1, savedFiles);
			savedFormsListView.setAdapter (adapter);
			savedFormsListView.setOnItemClickListener (new OnItemClickListener ()
			{
				@Override
				public void onItemClick (AdapterView<?> parent, View view, int position, long id)
				{
					String itemValue = (String) savedFormsListView.getItemAtPosition (position);
					String[] lines = serverService.getLines (itemValue, false);
					Toast.makeText (ReportsActivity.this, "Total forms: " + (lines.length - 1), Toast.LENGTH_LONG).show ();
				}
			});
		}
	}
}
