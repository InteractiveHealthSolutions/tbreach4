/**
 * 
 */

package com.ihsinformatics.tbr4mobile.util;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class TextUtil
{
	/**
	 * Capitalizes first letter of the string passed
	 * 
	 * @param string
	 * @return
	 */
	public static String capitalizeFirstLetter (String string)
	{
		if (string != null)
		{
			if (!"".equals (string))
			{
				string = Character.toUpperCase (string.charAt (0)) + string.substring (1);
			}
		}
		return string;
	}
}
