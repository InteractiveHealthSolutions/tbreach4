/**
 * Custom CheckBox view for ease
 */

package com.ihsinformatics.tbr4mobile.custom;

import android.content.Context;
import android.widget.CheckBox;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MyCheckBox extends CheckBox
{
	public MyCheckBox (Context context)
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
	 * @param checked
	 *            Initial checked state
	 */
	public MyCheckBox (Context context, int tag, int style, int text, boolean checked)
	{
		super (context);
		if (tag != -1)
		{
			setTag (getResources ().getString (tag));
		}
		if (style != -1)
		{
			setTextAppearance (context, style);
		}
		if (text != -1)
		{
			setText (text);
		}
		setChecked (checked);
	}
}
