package com.ihsinformatics.tbr4mobile_pk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.Intent;
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
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ihsinformatics.tbr4mobile_pk.custom.MyButton;
import com.ihsinformatics.tbr4mobile_pk.custom.MyCheckBox;
import com.ihsinformatics.tbr4mobile_pk.custom.MyEditText;
import com.ihsinformatics.tbr4mobile_pk.custom.MyRadioButton;
import com.ihsinformatics.tbr4mobile_pk.custom.MyRadioGroup;
import com.ihsinformatics.tbr4mobile_pk.custom.MySpinner;
import com.ihsinformatics.tbr4mobile_pk.custom.MyTextView;
import com.ihsinformatics.tbr4mobile_pk.shared.AlertType;
import com.ihsinformatics.tbr4mobile_pk.shared.FormType;
import com.ihsinformatics.tbr4mobile_pk.util.RegexUtil;

public class ReverseContactTracingActivity extends AbstractFragmentActivity
		implements OnEditorActionListener, OnFocusChangeListener
{
	// Views displayed in pages, sorted w.r.t. appearance on pager
	MyTextView formDateTextView;
	MyButton formDateButton;

	MyTextView indexCaseIdTextView;
	MyEditText indexCaseId;

	MyTextView indexDistrictTbNumberTextView;
	MyEditText indexDistrictTbNumber;

	MyTextView diagnosisTextView;
	MySpinner diagnosis;

	MyTextView totalNumberMembersTextView;
	MyEditText totalNumberMembers;

	MyTextView totalNumberMembersWithSymptomsTextView;
	MyEditText totalNumberMembersWithSymptoms;

	MyTextView namesSymptomaticMembersTextView;
	MyEditText namesSymptomaticMembers;

	MyButton scanBarcode;

	/**
	 * Subclass representing Fragment for Pediatric-screeening suspect form
	 * 
	 * @author owais.hussain@ihsinformatics.com
	 * 
	 */
	@SuppressLint("ValidFragment")
	class ReverseContactTracingFragment extends Fragment
	{
		int currentPage;

		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			Bundle data = getArguments();
			currentPage = data.getInt("current_page", 0);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			// Return a layout of views from pre-filled ArrayList of groups
			if (currentPage != 0 && groups.size() != 0)
				return groups.get(currentPage - 1);
			else
				return null;
		}
	}

	/**
	 * Subclass for Pager Adapter. Uses PediatricScreeningSuspect subclass as
	 * items
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	class ReverseContactTracingFragmentPagerAdapter extends
			FragmentPagerAdapter
	{
		/** Constructor of the class */
		public ReverseContactTracingFragmentPagerAdapter(
				FragmentManager fragmentManager)
		{
			super(fragmentManager);
		}

		/** This method will be invoked when a page is requested to create */
		@Override
		public Fragment getItem(int arg0)
		{
			ReverseContactTracingFragment fragment = new ReverseContactTracingFragment();
			Bundle data = new Bundle();
			data.putInt("current_page", arg0 + 1);
			fragment.setArguments(data);
			return fragment;
		}

		/** Returns the number of pages */
		@Override
		public int getCount()
		{
			return PAGE_COUNT;
		}
	}

	@Override
	public void createViews(Context context)
	{
		TAG = "ReverseContactTracingActivity";
		PAGE_COUNT = 2;
		pager = (ViewPager) findViewById(R.template_id.pager);
		navigationSeekbar.setMax(PAGE_COUNT - 1);
		navigatorLayout = (LinearLayout) findViewById(R.template_id.navigatorLayout);
		// If the form consists only of single page, then hide the
		// navigatorLayout
		if (PAGE_COUNT < 2)
		{
			navigatorLayout.setVisibility(View.GONE);
		}
		FragmentManager fragmentManager = getSupportFragmentManager();
		ReverseContactTracingFragmentPagerAdapter pagerAdapter = new ReverseContactTracingFragmentPagerAdapter(
				fragmentManager);
		pager.setAdapter(pagerAdapter);
		pager.setOffscreenPageLimit(PAGE_COUNT);
		// Create views for pages
		formDateTextView = new MyTextView(context, R.style.text,
				R.string.form_date);
		formDateButton = new MyButton(context, R.style.button,
				R.drawable.custom_button_beige, R.string.form_date,
				R.string.form_date);

		indexCaseIdTextView = new MyTextView(context, R.style.text,
				R.string.index_case_id);
		indexCaseId = new MyEditText(context, R.string.index_case_id,
				R.string.index_case_id_hint, InputType.TYPE_CLASS_TEXT,
				R.style.edit, 12, false);

		indexDistrictTbNumberTextView = new MyTextView(context, R.style.text,
				R.string.index_district_number);
		indexDistrictTbNumber = new MyEditText(context,
				R.string.index_district_number,
				R.string.index_district_number_hint,
				InputType.TYPE_CLASS_TEXT, R.style.edit, 12, false);

		diagnosisTextView = new MyTextView(context, R.style.text,
				R.string.diagnosis);
		diagnosis = new MySpinner(context, getResources().getStringArray(
				R.array.diagnosis_options), R.string.diagnosis,
				R.string.option_hint);

		totalNumberMembersTextView = new MyTextView(context, R.style.text,
				R.string.total_household_members);
		totalNumberMembers = new MyEditText(context,
				R.string.total_household_members,
				R.string.total_household_members_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 2, false);

		totalNumberMembersWithSymptomsTextView = new MyTextView(context,
				R.style.text, R.string.total_household_members_with_symptoms);
		totalNumberMembersWithSymptoms = new MyEditText(context,
				R.string.total_household_members_with_symptoms,
				R.string.total_household_members_with_symptoms_hint,
				InputType.TYPE_CLASS_NUMBER, R.style.edit, 2, false);

		namesSymptomaticMembersTextView = new MyTextView(context, R.style.text,
				R.string.names_symptomatic_members);
		namesSymptomaticMembers = new MyEditText(context,
				R.string.names_symptomatic_members,
				R.string.names_symptomatic_members_hint,
				InputType.TYPE_CLASS_TEXT, R.style.edit, 100, false);

		scanBarcode = new MyButton(context, R.style.button,
				R.drawable.custom_button_beige, R.string.scan_barcode,
				R.string.scan_barcode);

		View[][] viewGroups = {
				{ formDateTextView, formDateButton, indexCaseIdTextView,
						indexCaseId, scanBarcode,
						indexDistrictTbNumberTextView, indexDistrictTbNumber,
						diagnosisTextView, diagnosis,
						totalNumberMembersTextView, totalNumberMembers },
				{ totalNumberMembersWithSymptomsTextView,
						totalNumberMembersWithSymptoms,
						namesSymptomaticMembersTextView,
						namesSymptomaticMembers } };

		// Create layouts and store in ArrayList
		groups = new ArrayList<ViewGroup>();
		for (int i = 0; i < PAGE_COUNT; i++)
		{
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.VERTICAL);
			for (int j = 0; j < viewGroups[i].length; j++)
			{
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
		totalNumberMembers.setOnFocusChangeListener(this);
		totalNumberMembersWithSymptoms.addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				if(!"".equals(App.get(totalNumberMembersWithSymptoms)))
				{
					boolean hasSymptomaticMembers = Integer.parseInt(App.get(totalNumberMembersWithSymptoms)) > 0;
					namesSymptomaticMembersTextView.setEnabled(hasSymptomaticMembers);
					namesSymptomaticMembers.setEnabled(hasSymptomaticMembers);
				}
				else
				{
					namesSymptomaticMembersTextView.setEnabled(false);
					namesSymptomaticMembers.setEnabled(false);
				}
			}
		});
		views = new View[] { indexCaseId, indexDistrictTbNumber, diagnosis,
				totalNumberMembers, totalNumberMembersWithSymptoms,
				namesSymptomaticMembers };

		for (View v : views)
		{
			if (v instanceof Spinner)
			{
				((Spinner) v).setOnItemSelectedListener(this);
			}
			else if (v instanceof CheckBox)
			{
				((CheckBox) v).setOnCheckedChangeListener(this);
			}
		}
		pager.setOnPageChangeListener(this);

		indexCaseId.setOnLongClickListener(new OnLongClickListener()
		{
			public boolean onLongClick(View view)
			{
				// Intent intent = new Intent (context,
				// PatientSearchActivity.class);
				// startActivity (intent);
				return false;
			}
		});
		// Detect RTL language
		if (App.isLanguageRTL())
		{
			Collections.reverse(groups);
			for (ViewGroup g : groups)
			{
				LinearLayout linearLayout = (LinearLayout) g.getChildAt(0);
				linearLayout.setGravity(Gravity.RIGHT);
			}
			for (View v : views)
			{
				if (v instanceof EditText)
				{
					((EditText) v).setGravity(Gravity.RIGHT);
				}
			}
		}
	}

	@Override
	public void initView(View[] views)
	{
		super.initView(views);
		formDate = Calendar.getInstance();
		totalNumberMembersWithSymptomsTextView.setEnabled(false);
		totalNumberMembersWithSymptoms.setEnabled(false);
		namesSymptomaticMembersTextView.setEnabled(false);
		namesSymptomaticMembers.setEnabled(false);
		updateDisplay();
	}

	@Override
	public void onClick(View view)
	{
		if (view == formDateButton)
		{
			showDialog(DATE_DIALOG_ID);
		}
		else if (view == firstButton)
		{
			gotoFirstPage();
		}
		else if (view == lastButton)
		{
			gotoLastPage();
		}
		else if (view == clearButton)
		{
			initView(views);
		}
		else if (view == saveButton)
		{
			submit();
		}
		else if (view == scanBarcode)
		{
			Intent intent = new Intent(Barcode.BARCODE_INTENT);
			intent.putExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
			startActivityForResult(intent, Barcode.BARCODE_RESULT);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id)
	{
		MySpinner spinner = (MySpinner) parent;
		boolean visible = spinner.getSelectedItemPosition() == 1;
		updateDisplay();
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onLongClick(View arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateDisplay()
	{
		formDateButton.setText(DateFormat.format("dd-MMM-yyyy", formDate));
		
		// Auto-populate TB suspect logic

	}

	@Override
	public boolean validate()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer();
		// Validate mandatory controls
		View[] mandatory = { indexCaseId, totalNumberMembers };
		for (View v : mandatory)
		{
			if (App.get(v).equals(""))
			{
				valid = false;
				message.append(v.getTag().toString() + ". ");
				((EditText) v).setHintTextColor(getResources().getColor(
						R.color.Red));
			}
		}
		if(!"".equals(App.get(totalNumberMembers)))
		{
			if (Integer.parseInt(App.get(totalNumberMembers)) > 0
					&& App.get(totalNumberMembersWithSymptoms).equals(""))
			{
				valid = false;
				message.append(totalNumberMembersWithSymptoms.getTag().toString()
						+ ". ");
				totalNumberMembersWithSymptoms.setHintTextColor(getResources()
						.getColor(R.color.Red));
			}
		}
		if(!"".equals(App.get(totalNumberMembersWithSymptoms)))
		{
			if (Integer.parseInt(App.get(totalNumberMembersWithSymptoms)) > 0
					&& App.get(namesSymptomaticMembers).equals(""))
			{
				valid = false;
				message.append(namesSymptomaticMembers.getTag().toString() + ". ");
				namesSymptomaticMembers.setHintTextColor(getResources().getColor(
						R.color.Red));
			}
		}
		if (!valid)
		{
			message.append(getResources().getString(R.string.empty_data) + "\n");
		}

		// // Validate data
		if (valid)
		{
			if (RegexUtil.matchId(App.get(indexCaseId)))
			{
				if (!RegexUtil.isValidId(App.get(indexCaseId)))
				{
					valid = false;
					message.append(indexCaseId.getTag().toString() + ": "
							+ getResources().getString(R.string.invalid_data)
							+ "\n");
					indexCaseId.setTextColor(getResources().getColor(
							R.color.Red));
				}
			}
			else
			{
				valid = false;
				message.append(indexCaseId.getTag().toString() + ": "
						+ getResources().getString(R.string.invalid_data)
						+ "\n");
				indexCaseId.setTextColor(getResources().getColor(R.color.Red));
			}

			if (!RegexUtil.isNumeric(App.get(totalNumberMembers), false))
			{
				valid = false;
				message.append(totalNumberMembers.getTag().toString() + ": "
						+ getResources().getString(R.string.invalid_data)
						+ "\n");
				totalNumberMembers.setTextColor(getResources().getColor(
						R.color.Red));
			}

			if (!RegexUtil.isNumeric(App.get(totalNumberMembersWithSymptoms),
					false))
			{
				valid = false;
				message.append(totalNumberMembersWithSymptoms.getTag()
						.toString()
						+ ": "
						+ getResources().getString(R.string.invalid_data)
						+ "\n");
				totalNumberMembersWithSymptoms.setTextColor(getResources()
						.getColor(R.color.Red));
			}

		}

		if (!valid)
		{
			App.getAlertDialog(this, AlertType.ERROR, message.toString())
					.show();
		}
		return valid;
	}

	@Override
	public boolean submit()
	{
		if (validate())
		{
			final ContentValues values = new ContentValues();
			values.put("formDate", App.getSqlDate(formDate));
			values.put("location", App.getLocation());
			// values.put ("age", ageInYears);
			values.put("patientId", App.get(indexCaseId));
			final ArrayList<String[]> observations = new ArrayList<String[]>();

			observations.add(new String[] { "Index Case District TB Number",
					App.get(indexDistrictTbNumber) });
			observations.add(new String[] { "Index Case Diagnosis",
					App.get(diagnosis) });
			observations.add(new String[] { "Total Household Members",
					App.get(totalNumberMembers) });

			if (Integer.parseInt(App.get(totalNumberMembers)) > 0)
			{
				observations.add(new String[] {
						"Total Household Members with Symptoms",
						App.get(totalNumberMembersWithSymptoms) });
			}

			if (Integer.parseInt(App.get(totalNumberMembersWithSymptoms)) > 0)
			{
				observations.add(new String[] {
						"Symptomatic Household Members Names",
						App.get(namesSymptomaticMembers) });
			}

			AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String>()
			{
				@Override
				protected String doInBackground(String... params)
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							loading.setIndeterminate(true);
							loading.setCancelable(false);
							loading.setMessage(getResources().getString(
									R.string.loading_message));
							loading.show();
						}
					});

					String result = "";
					result = serverService.saveReverseContact(
							FormType.REVERSE_CONTACT_TRACING, values,
							observations.toArray(new String[][] {}));

					return result;
				}

				@Override
				protected void onProgressUpdate(String... values)
				{
				};

				@Override
				protected void onPostExecute(String result)
				{
					// TODO: check this method

					super.onPostExecute(result);
					loading.dismiss();
					if (result.equals("SUCCESS"))
					{
						App.getAlertDialog(ReverseContactTracingActivity.this,
								AlertType.INFO,
								getResources().getString(R.string.inserted))
								.show();
						initView(views);
					}
					else
					{
						App.getAlertDialog(ReverseContactTracingActivity.this,
								AlertType.ERROR, result).show();
					}
				}
			};
			updateTask.execute("");
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		// Retrieve barcode scan results
		if (requestCode == Barcode.BARCODE_RESULT)
		{
			if (resultCode == RESULT_OK)
			{
				String str = data.getStringExtra(Barcode.SCAN_RESULT);
				// Check for valid Id
				if (RegexUtil.isValidId(str)
						&& !RegexUtil.isNumeric(str, false))
				{
					indexCaseId.setText(str);
				}
				else
				{
					App.getAlertDialog(
							this,
							AlertType.ERROR,
							indexCaseId.getTag().toString()
									+ ": "
									+ getResources().getString(
											R.string.invalid_data)).show();
				}
			}
			else if (resultCode == RESULT_CANCELED)
			{
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
	public boolean onEditorAction(TextView v, int arg1, KeyEvent arg2)
	{
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		if (v == totalNumberMembers)
		{
			if (!hasFocus && !App.get(totalNumberMembers).equals(""))
			{
				boolean hasMembers = Integer.parseInt(App.get(totalNumberMembers)) > 0 ;
				totalNumberMembersWithSymptomsTextView.setEnabled(hasMembers);
				totalNumberMembersWithSymptoms.setEnabled(hasMembers);
			
			}
		}
//		else if (v == totalNumberMembersWithSymptoms)
//		{
//			if (!hasFocus && !App.get(totalNumberMembersWithSymptoms).equals(""))
//			{
//				boolean hasSymptomaticMembers = Integer.parseInt(App.get(totalNumberMembersWithSymptoms)) > 0;
//				namesSymptomaticMembersTextView.setEnabled(hasSymptomaticMembers);
//				namesSymptomaticMembers.setEnabled(hasSymptomaticMembers);
//			}
//		}
	}
}
