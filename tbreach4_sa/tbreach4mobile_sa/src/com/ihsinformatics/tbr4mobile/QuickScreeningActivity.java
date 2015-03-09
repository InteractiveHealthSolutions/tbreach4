/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.tbr4mobile;

import com.ihsinformatics.tbr4mobile.util.ServerService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class QuickScreeningActivity extends Activity implements IActivity, OnClickListener, OnCheckedChangeListener, OnItemSelectedListener
{

	private static final String		TAG					= "QuickScreeningActivity";
	private ServerService			serverService;
	protected static ProgressDialog	loading;
	
	RadioButton						maleRadioButton;
	RadioButton						femaleRadioButton;
	CheckBox						coughCheckBox;
	CheckBox						nightSweatsCheckBox;
	CheckBox						weightLossCheckBox;
	CheckBox						feverCheckBox;
	Button							saveButton;
	TextView						screenerInstructionHeadingTextView;
	TextView						screenerInstructionTextView;
	EditText						ageEditText;
	
	String							FORM_NAME;
	View[]							views;
	Animation						alphaAnimation;
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		setTheme (App.getTheme ());
		setContentView (R.layout.quick_screening);
		Configuration config = new Configuration ();
		config.locale = App.getCurrentLocale ();
		getApplicationContext ().getResources ().updateConfiguration (config, null);
		serverService = new ServerService (getApplicationContext ());
		loading = new ProgressDialog (this);
		
		FORM_NAME = "Quick Screening Form";
		
		femaleRadioButton = (RadioButton) findViewById(R.quickscreening_id.femaleradioButton);
		maleRadioButton = (RadioButton) findViewById(R.quickscreening_id.maleradioButton);
		coughCheckBox = (CheckBox) findViewById(R.quickscreening_id.checkBoxCough);
		nightSweatsCheckBox = (CheckBox) findViewById(R.quickscreening_id.checkBoxNightSweats);
		weightLossCheckBox = (CheckBox) findViewById(R.quickscreening_id.checkBoxWeightLoss);
		feverCheckBox = (CheckBox) findViewById(R.quickscreening_id.checkBoxFever);
		saveButton = (Button) findViewById(R.quickscreening_id.buttonSave);
		screenerInstructionHeadingTextView = (TextView) findViewById(R.quickscreening_id.textViewscreenerInstructionHeading);
		screenerInstructionTextView = (TextView) findViewById(R.quickscreening_id.textViewinstruction);
		ageEditText = (EditText) findViewById(R.quickscreening_id.editTextAge);
				
		femaleRadioButton.setOnClickListener(this);
		maleRadioButton.setOnClickListener(this);
		
		coughCheckBox.setOnCheckedChangeListener(this);
		nightSweatsCheckBox.setOnCheckedChangeListener(this);
		weightLossCheckBox.setOnCheckedChangeListener(this);
		feverCheckBox.setOnCheckedChangeListener(this);
		
		saveButton.setOnClickListener(this);
		
		this.setTitle(FORM_NAME);
		super.onCreate (savedInstanceState);
		initView (views);
	
	}
	
	
	@Override
	public void onCheckedChanged(CompoundButton button, boolean state) {
		if(button == coughCheckBox || button == nightSweatsCheckBox || button == weightLossCheckBox || button == feverCheckBox ){
			
			if( coughCheckBox.isChecked() == true || nightSweatsCheckBox.isChecked() == true || weightLossCheckBox.isChecked() == true || feverCheckBox.isChecked() == true){
					saveButton.setVisibility(View.GONE);
					screenerInstructionHeadingTextView.setVisibility(View.VISIBLE);
					screenerInstructionTextView.setVisibility(View.VISIBLE);
			}
			else{
				saveButton.setVisibility(View.VISIBLE);
				screenerInstructionHeadingTextView.setVisibility(View.GONE);
				screenerInstructionTextView.setVisibility(View.GONE);
			}
			
		}
		
	}

	@Override
	public void onClick(View view) {
		if(view == femaleRadioButton){
			maleRadioButton.setChecked(false);
		}
		else if(view == maleRadioButton){
			femaleRadioButton.setChecked(false);
		}
		else if(view == saveButton){
			submit();
		}
		
	}

	@Override
	public void initView(View[] views) {
		maleRadioButton.setChecked(true);
		femaleRadioButton.setChecked(false);
		saveButton.setVisibility(View.VISIBLE);
		screenerInstructionHeadingTextView.setVisibility(View.GONE);
		screenerInstructionTextView.setVisibility(View.GONE);
	}
	
	
	@Override
	public void updateDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean validate() {
		
		if (App.get (ageEditText).equals ("")){
			
			ageEditText.setHintTextColor (getResources ().getColor (R.color.Red));
		}
		return true;
	}

	@Override
	public boolean submit() {
		if (validate ())
		{
			
		}
		return true;
	}
	
	@Override
	public void onBackPressed ()
	{
		finish ();
		Intent mainMenuIntent = new Intent (getApplicationContext (), MainMenuActivity.class);
		startActivity (mainMenuIntent);
			
	}
	
	


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
