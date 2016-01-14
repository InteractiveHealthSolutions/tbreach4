/*
Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.minetb.datawarehouse.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Utility class to parse and store Comma/Tab/Space separated files
 * 
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class CsvUtil {
    String filePath;
    char fieldSeparator;
    boolean header;

    public static void main(String[] args) {
	CsvUtil csvUtil = new CsvUtil("res/Samples.csv", true);
	String[][] data = csvUtil.readData();
	System.out.println(data);
    }

    public CsvUtil(String filePath) {
	init(filePath, ',', false);
    }

    public CsvUtil(String filePath, boolean header) {
	init(filePath, ',', header);
    }

    public CsvUtil(String filePath, char fieldSeparator, boolean header) {
	init(filePath, fieldSeparator, header);
    }

    private void init(String filePath, char fieldSeparator, boolean header) {
	this.filePath = filePath;
	this.fieldSeparator = fieldSeparator;
	this.header = header;
    }

    /**
     * Returns column names in the header, if present
     * 
     * @return
     */
    public String[] getHeader() {
	if (header) {
	    File file = new File(filePath);
	    String top = new String("");
	    try {
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(
			dis));
		String strLine;
		if ((strLine = br.readLine()) != null) {
		    top = strLine;
		}
		dis.close();
		return top.split(String.valueOf(fieldSeparator));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return null;
    }

    /**
     * If header is true, then returns data without header
     * 
     * @return
     */
    public String[][] readData() {
	String[] lines = readAllLines();
	String[][] data = new String[lines.length][];
	int i = header ? 1 : 0;
	for (int j = 0; i < data.length; i++, j++) {
	    String[] values = lines[i].split(String.valueOf(fieldSeparator));
	    data[j] = values;
	}
	return data;
    }

    public String readAllText() {
	StringBuilder sb = new StringBuilder();
	for (String line : readAllLines()) {
	    sb.append(line);
	}
	return sb.toString();
    }

    public String[] readAllLines() {
	File file = new File(filePath);
	ArrayList<String> lines = new ArrayList<String>();
	try {
	    FileInputStream fis = new FileInputStream(file);
	    DataInputStream dis = new DataInputStream(fis);
	    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
	    String strLine;
	    while ((strLine = br.readLine()) != null) {
		lines.add(strLine);
	    }
	    dis.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return lines.toArray(new String[] {});
    }

    public boolean writeCsv(String[][] data, boolean append) {
	try {
	    File file = new File(filePath);
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
	    for (String[] record : data) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < record.length; i++) {
		    sb.append(record[i] + fieldSeparator);
		}
		// Remove additional separator
		sb.replace(sb.length() - 2, sb.length() - 1, "");
		buf.append(sb.toString());
		buf.newLine();
	    }
	    buf.close();
	    return true;
	} catch (IOException e) {
	    e.printStackTrace();
	    return false;
	}
    }
}
