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

package com.ihsinformatics.tbr4mobile.custom;

import com.ihsinformatics.tbr4mobile.R;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MyRadioGroup extends RadioGroup implements android.widget.CompoundButton.OnCheckedChangeListener
{
	MyRadioButton[]	buttons;
	
	private static final String	TAG			= "MyRadioGroup";
	public int HORIZONTAL = 0;
	public int VERTICAL = 1;

	public MyRadioGroup (Context context)
	{
		super (context);
	}

	/**
	 * 
	 * @param context
	 * @param radioButtons
	 *            RadioButton[] array containing list of RadioButton objects in
	 *            this group
	 * @param tag
	 *            Text ID from resources. Pass -1 if tag is not to be set
	 * @param style
	 *            Style ID from resources. Pass -1 if style is not to be set
	 * @param isRTL
	 *            Should this group be displayed Right-to-Left?
	 */
	public MyRadioGroup (Context context, MyRadioButton[] radioButtons, int tag, int style, boolean isRTL , int layout)
	{
		super (context);
		if (tag != -1)
		{
			setTag (getResources ().getString (tag));
		}
		if(layout == 0){
			LinearLayout group = new LinearLayout (context);
			group.setOrientation (LinearLayout.HORIZONTAL);
			//group.setLayoutParams (new LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			//group.setGravity (Gravity.CENTER_VERTICAL | Gravity.RIGHT);
			
			for (RadioButton rb : radioButtons)
			{
				TextView rbTextView = new TextView (context);
				rbTextView.setTextAppearance (context, style);
				rbTextView.setText (rb.getText ());
				rb.setText ("");
				
				if (isRTL)
				{
					
					group.addView (rbTextView);
					group.addView (rb);
				}
				else
				{
					group.addView (rb);
					group.addView (rbTextView);
				}
				rb.setOnCheckedChangeListener (this);
				
			}
			
			addView (group);
			buttons = radioButtons;
		}else{
			for (RadioButton rb : radioButtons)
			{
				TextView rbTextView = new TextView (context);
				rbTextView.setTextAppearance (context, style);
				rbTextView.setText (rb.getText ());
				rb.setText ("");
				LinearLayout group = new LinearLayout (context);
				group.setOrientation (LinearLayout.HORIZONTAL);
				group.setLayoutParams (new LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				if (isRTL)
				{
					group.setGravity (Gravity.CENTER_VERTICAL | Gravity.RIGHT);
					group.addView (rbTextView);
					group.addView (rb);
				}
				else
				{
					group.addView (rb);
					group.addView (rbTextView);
				}
				rb.setOnCheckedChangeListener (this);
				addView (group);
				buttons = radioButtons;
			}
			
		}
	}

	@Override
	public void onCheckedChanged (CompoundButton button, boolean state)
	{
		if (state)
		{
			for (RadioButton r : buttons)
			{
				if (r != button)
					r.setChecked (false);
			}
		}
	}
	
	@Override
	public void setEnabled (boolean state)
	{
		super.setEnabled (state);
		/*
		if(state == false){
			for (RadioButton r : buttons)
			{
				    r.setEnabled(false);
					r.setChecked (false);
					r.setTextAppearance(getContext (), R.style.radio_disable);
			}
		}else{
			buttons[0].setChecked(true);
			for (RadioButton r : buttons)
			{
				    r.setEnabled(true);
				    r.setTextAppearance(getContext (), R.style.radio);
			}
		}*/
		
	}
	
	
	//NOT WORKING
	public String getSelectedValue (){
		
		String selectedValue = "";
		
		for (int i = 0 ; i < buttons.length; i++){
			if(buttons[i].isChecked()){
				Log.i (TAG, buttons[i].getTag().toString()+"YES!");
			}
		}
		return selectedValue;
		
	}
}
