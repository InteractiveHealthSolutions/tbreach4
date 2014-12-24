/**
 * Custom Button view for ease
 */

package com.ihsinformatics.tbr4mobile_pk.custom;

import com.ihsinformatics.tbr4mobile_pk.R;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MyButton extends Button
{
	public MyButton (Context context)
	{
		super (context);
	}

	/**
	 * 
	 * @param context
	 * @param textStyle
	 *            Style Id from resources. Pass -1 to keep default style
	 * @param backgroundResource
	 *            Drawable to be used as background. Pass -1 to keep default
	 * @param tag
	 *            Tag to be used. Pass -1 if not to be set
	 * @param text
	 *            Text Id from resources. Pass -1 if not to be set
	 */
	public MyButton (Context context, int textStyle, int backgroundResource, int tag, int text)
	{
		super (context);
		setLayoutParams (new LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setText (text);
		if (tag != -1)
		{
			setTag (getResources ().getString (tag));
		}
		if (backgroundResource != -1)
		{
			setBackgroundResource (backgroundResource);
		}
		if (textStyle != -1)
		{
			setTextAppearance (context, textStyle);
		}
	}

	@Override
	public void setEnabled (boolean enabled)
	{
		super.setEnabled (enabled);
		if (enabled)
		{
			setTextColor (getResources ().getColor (R.color.Chocolate));
		}
		else
		{
			setTextColor (getResources ().getColor (R.color.DarkGray));
		}
	}
}
