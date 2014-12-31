/**
 * 
 */

package com.ihsinformatics.tbr4mobile.custom;

import android.content.Context;
import android.widget.RadioButton;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MyRadioButton extends RadioButton
{
	public MyRadioButton (Context context)
	{
		super (context);
	}

	/**
	 * 
	 * @param context
	 * @param tag
	 *            Text Id from resources. Pass -1 if no tag is to be set
	 * @param style
	 *            Style Id from resources. Pass -1 to keep default style
	 * @param text
	 *            Text Id from resources. Pass -1 if not to be set
	 */
	public MyRadioButton (Context context, int tag, int style, int text)
	{
		super (context);
		if (tag != -1)
		{
			setText (getResources ().getString (tag));
		}
		setTextAppearance (context, style);
		setText (text);
	}
}
