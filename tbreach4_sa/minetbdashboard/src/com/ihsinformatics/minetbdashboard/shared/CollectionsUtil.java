/* Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 */

/**
 * 
 */
package com.ihsinformatics.minetbdashboard.shared;

/**
 * This utility provides helping methods for Collections and Data structures
 * 
 * @author Owais
 *
 */
public class CollectionsUtil {

	/**
	 * Sorts an array of String using bubble sort algorithm
	 * 
	 * @param list
	 * @return
	 */
	public static String[] sortArray(String[] list) {
		int j;
		boolean flag = true; // will determine when the sort is finished
		String temp;
		while (flag) {
			flag = false;
			for (j = 0; j < list.length - 1; j++) {
				if (list[j].compareToIgnoreCase(list[j + 1]) > 0) { // ascending
																	// sort
					temp = list[j];
					list[j] = list[j + 1]; // swapping
					list[j + 1] = temp;
					flag = true;
				}
			}
		}
		return list;
	}

	/**
	 * Sorts an array of String using bubble sort algorithm
	 * 
	 * @param list
	 * @return
	 */
	public static Double[] sortArray(Double[] list) {
		int j;
		boolean flag = true; // will determine when the sort is finished
		Double temp;
		while (flag) {
			flag = false;
			for (j = 0; j < list.length - 1; j++) {
				if (list[j] > list[j + 1]) { // ascending sort
					temp = list[j];
					list[j] = list[j + 1]; // swapping
					list[j + 1] = temp;
					flag = true;
				}
			}
		}
		return list;
	}

	/**
	 * Converts an array of Strings into Double type, from which, more primitive
	 * types can be extracted
	 * 
	 * @param values
	 * @return
	 */
	public static Double[] convertToNumeric(String[] values) {
		Double[] converted = new Double[values.length];
		for (int i = 0; i < values.length; i++) {
			converted[i] = Double.parseDouble(values[i]);
		}
		return converted;
	}
}
