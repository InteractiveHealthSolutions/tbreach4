/**
 * Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
 * You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
 * Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 * Contributors: Owais Hussain
 */
package com.ihsinformatics.minetbdashboard.client;

import java.util.Date;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.ihsinformatics.minetbdashboard.shared.MineTB;

/**
 * Provides various Client-side methods used in the Application
 * @author owais.hussain@ihsinformatics.com
 * 
 */
public final class MineTBClient {
	public static boolean isLoggedIn(String userName) {
		String loginTimeCookie = Cookies.getCookie("LoginTime");
		String userLogged = Cookies.getCookie("UserName");
		if (!userLogged.equals(userName))
			return false;
		if (loginTimeCookie.length() < 8)
			return false;
		long loginTime = Long.parseLong(loginTimeCookie);
		// Check if the session has expired
		if (new Date().getTime() > new Date(loginTime + MineTB.sessionLimit)
				.getTime())
			return false;
		return false;
	}

	/**
	 * Creates a 'long' code for a given string using some mathematical
	 * mechanism
	 * 
	 * @param string
	 * @return
	 */
	public static long getSimpleCode(String string) {
		long code = 1;
		for (int i = 0; i < string.length(); i++)
			code *= string.charAt(i);
		return code;
	}

	/**
	 * Verifies whether client has entered a valid pass code (required for some
	 * sensitive operations)
	 * 
	 * @return
	 */
	public static boolean verifyClientPasscode(String passcode) {
		try {
			String storedPasscode = Cookies.getCookie("Pass");
			long passedCode = getSimpleCode(passcode.substring(0, 3));
			long existing = Long.parseLong(storedPasscode);
			return (passedCode == existing);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Get usually desired value from a widget
	 * 
	 * 1. Text fields return their respective text
	 * 
	 * 2. List boxes return selected value
	 * 
	 * @param control
	 * @return
	 */
	public static String get(Widget control) {
		if (control instanceof TextBoxBase)
			return ((TextBoxBase) control).getText();
		if (control instanceof ListBox)
			return ((ListBox) control).getValue(((ListBox) control)
					.getSelectedIndex());
		if (control instanceof ValueBoxBase<?>)
			return ((ValueBoxBase<?>) control).getText();
		return "";
	}

	/**
	 * Get index of a given value from a widget (probably ListBox)
	 * 
	 * @param control
	 * 
	 * @param value
	 * @return
	 */
	public static int getIndex(Widget control, String value) {
		if (control instanceof ListBox) {
			ListBox listBox = (ListBox) control;
			for (int i = 0; i < listBox.getItemCount(); i++)
				if (listBox.getValue(i).equalsIgnoreCase(value))
					return i;
		}
		return -1;
	}
}
