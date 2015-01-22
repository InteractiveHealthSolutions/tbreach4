/**
 * 
 */

package org.irdresearch.tbr3;

import com.sun.media.sound.InvalidFormatException;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class ChecksumHandler
{
	/**
	 * Calculates Luhn check digit
	 * 
	 * @param idWithoutCheckdigit
	 * @return
	 * @throws Exception
	 */
	public static int calculateLuhnDigit (String idWithoutCheckdigit) throws InvalidFormatException
	{
		String validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVYWXZ_";
		idWithoutCheckdigit = idWithoutCheckdigit.trim ().toUpperCase ();
		int sum = 0;
		for (int i = 0; i < idWithoutCheckdigit.length (); i++)
		{
			char ch = idWithoutCheckdigit.charAt (idWithoutCheckdigit.length () - i - 1);
			if (validChars.indexOf (ch) == -1)
				throw new InvalidFormatException ("\"" + ch + "\" is an invalid character");
			int digit = (int) ch - 48;
			int weight;
			if (i % 2 == 0)
			{
				weight = (2 * digit) - (int) (digit / 5) * 9;
			}
			else
			{
				weight = digit;
			}
			sum += weight;
		}
		sum = Math.abs (sum) + 10;
		return (10 - (sum % 10)) % 10;

	}
}
