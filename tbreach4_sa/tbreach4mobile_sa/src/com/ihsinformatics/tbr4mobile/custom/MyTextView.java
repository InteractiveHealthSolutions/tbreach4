/**
 * Custom TextView for ease
 */

package com.ihsinformatics.tbr4mobile.custom;

import com.ihsinformatics.tbr4mobile.R;

import android.content.Context;
import android.widget.TextView;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MyTextView extends TextView
{
	public MyTextView (Context context)
	{
		super (context);
	}

	/**
	 * 
	 * @param context
	 * @param style
	 *            Style Id from resources. Pass -1 to keep default style
	 * @param text
	 *            Text Id from resources. Pass -1 if not to be set
	 */
	public MyTextView (Context context, int style, int text)
	{
		super (context);
		if (style != -1)
		{
			setTextAppearance (context, style);
		}
		if (text != -1)
		{
			setText (text);
		}
	}

	@Override
	public void setEnabled (boolean enabled)
	{
		super.setEnabled (enabled);
		if (enabled)
		{
			setTextColor (getResources ().getColor (R.color.Black));
		}
		else
		{
			setTextColor (getResources ().getColor (R.color.DarkGray));
		}
	}
}
