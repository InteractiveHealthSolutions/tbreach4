/**
 * 
 */

package com.ihsinformatics.tbr4mobile_pk.custom;

import android.content.Context;
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
	public MyRadioGroup (Context context, MyRadioButton[] radioButtons, int tag, int style, boolean isRTL)
	{
		super (context);
		if (tag != -1)
		{
			setTag (getResources ().getString (tag));
		}
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
}
