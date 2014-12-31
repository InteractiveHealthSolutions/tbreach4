/**
 * Custom Spinner view for ease
 */

package com.ihsinformatics.tbr4mobile.custom;

import com.ihsinformatics.tbr4mobile.R;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MySpinner extends Spinner
{
	private String[]	itemList;

	public MySpinner (Context context)
	{
		super (context);
	}

	/**
	 * 
	 * @param context
	 * @param adapter
	 *            String list adapter
	 * @param tag
	 *            Text Id from resources. Pass -1 if no tag is to be set
	 * @param hint
	 *            Text Id from resources. Pass -1 if no hint is to be set
	 */
	public MySpinner (Context context, String[] itemList, int tag, int hint)
	{
		super (context);
		this.itemList = itemList;
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String> (context, R.drawable.custom_spinner_item_enabled, this.itemList);
		setAdapter (arrayAdapter);
		setLayoutParams (new LayoutParams (LayoutParams.MATCH_PARENT, getResources ().getDimensionPixelSize (R.dimen.gigantic)));
		if (tag != -1)
		{
			setTag (getResources ().getString (tag));
		}
		if (hint != -1)
		{
			setPromptId (hint);
		}
		int backgroundResource = R.drawable.custom_spinner_beige;
		if (backgroundResource != -1)
		{
			setBackgroundResource (backgroundResource);
		}
	}

	@Override
	public void setEnabled (boolean enabled)
	{
		super.setEnabled (enabled);
		int drawable = enabled ? R.drawable.custom_spinner_item_enabled : R.drawable.custom_spinner_item_disabled;
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String> (getContext (), drawable, this.itemList);
		setAdapter (arrayAdapter);
	}
}
