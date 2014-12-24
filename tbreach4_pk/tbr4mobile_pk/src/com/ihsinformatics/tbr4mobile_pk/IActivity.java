/**
 * Interface for Activities in the project
 */

package com.ihsinformatics.tbr4mobile_pk;

import android.view.View;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public interface IActivity
{
	/**
	 * Resets the data in Views to initialize
	 * 
	 * @param view
	 */
	public void initView (View[] views);

	/**
	 * Updates the values in the Views to latest
	 */
	public void updateDisplay ();

	/**
	 * Validates the data in Views according to the rules defined
	 * 
	 * @return true/false
	 */
	public boolean validate ();

	/**
	 * Save form
	 * 
	 * @param view
	 * @return true/false
	 */
	public boolean submit ();
}
