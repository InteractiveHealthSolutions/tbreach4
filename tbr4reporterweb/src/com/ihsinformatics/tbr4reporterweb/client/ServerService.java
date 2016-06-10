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
package com.ihsinformatics.tbr4reporterweb.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.ihsinformatics.tbr4reporterweb.shared.Parameter;
import com.ihsinformatics.tbr4reporterweb.shared.Report;
import com.ihsinformatics.tbr4reporterweb.shared.ReportFormat;

/**
 * @author owais.hussain@ihsinformatics.com
 * 
 */
@RemoteServiceRelativePath("greet")
public interface ServerService extends RemoteService {
	Boolean authenticate(String userName, String password) throws Exception;

	void setCurrentUser(String userName) throws Exception;

	String generateCsvfromQuery(String query) throws Exception;

	String generateReport(String reportSelected, Parameter[] params,
			ReportFormat format) throws Exception;

	String generateReport(String reportSelected, String query,
			Parameter[] params, ReportFormat format);

	Report[] getReportsList() throws Exception;

	String[][] getTableData(String query) throws Exception;

	Boolean executeProcedure(String procedure) throws Exception;
}
