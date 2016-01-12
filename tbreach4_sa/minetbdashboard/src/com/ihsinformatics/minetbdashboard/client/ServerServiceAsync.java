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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.ihsinformatics.minetbdashboard.shared.Parameter;
import com.ihsinformatics.minetbdashboard.shared.Report;

/**
 * @author Owais
 * 
 */
public interface ServerServiceAsync {
	void authenticate(String userName, String password,
			AsyncCallback<Boolean> asyncCallback) throws Exception;

	void setCurrentUser(String userName, AsyncCallback<Void> asyncCallback) throws Exception;

	void generateCSVfromQuery(String query, AsyncCallback<String> asyncCallback)
			throws Exception;

	void generateReport(String reportSelected, Parameter[] params,
			boolean export, AsyncCallback<String> asyncCallback)
			throws Exception;

	void getReportsList(AsyncCallback<Report[]> callback) throws Exception;

	void getTableData(String sqlQuery, AsyncCallback<String[][]> callback) throws Exception;
}
