/**
 * Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
 * You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
 * Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.
 * Contributors: Owais Hussain
 */
package com.ihsinformatics.minetbdashboard.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.ihsinformatics.minetbdashboard.client.ServerService;
import com.ihsinformatics.minetbdashboard.server.util.DateTimeUtil;
import com.ihsinformatics.minetbdashboard.server.util.HibernateUtil;
import com.ihsinformatics.minetbdashboard.server.util.ReportUtil;
import com.ihsinformatics.minetbdashboard.server.util.Security;
import com.ihsinformatics.minetbdashboard.shared.DataType;
import com.ihsinformatics.minetbdashboard.shared.MineTB;
import com.ihsinformatics.minetbdashboard.shared.Parameter;
import com.ihsinformatics.minetbdashboard.shared.Report;

/**
 * @author owais.hussain@ihsinformatics.com
 * 
 */
public class ServerServiceImpl extends RemoteServiceServlet implements
		ServerService {
	private static final long serialVersionUID = 4123609914879659870L;
	private ReportUtil reportUtil;

	static String resourcePath = "C:\\Users\\Owais\\git\\tbreach4\\tbreach4_sa\\minetbdashboard\\war\\";

	@SuppressWarnings("deprecation")
	public ServerServiceImpl() {
		try {
			Connection connection = HibernateUtil.util.getSession()
					.connection();
			reportUtil = new ReportUtil(resourcePath, connection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * User authentication: Checks whether user exists, then match his password
	 * 
	 * @return Boolean
	 */
	public Boolean authenticate(String userName, String password)
			throws Exception {
		boolean result = false;
		try {
			if (userName != null && password != null) {
				Object[][] data = HibernateUtil.util
						.selectData("select password, salt from users inner join user_role using (user_id) where username = '"
								+ userName
								+ "' and retired = 0 and role in ('System Developer', 'Reporting', 'Program Admin')");
				if (data != null) {
					String passwordHash = data[0][0].toString();
					String salt = data[0][1].toString();
					if (Security.hashMatches(passwordHash, password + salt)) {
						result = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Get number of records in a table, given appropriate filter
	 * 
	 * @return Long
	 */
	public Long count(String tableName, String filter) throws Exception {
		Object obj = HibernateUtil.util.selectObject("select count(*) from "
				+ tableName + " " + arrangeFilter(filter));
		return Long.parseLong(obj.toString());
	}

	/**
	 * Checks existence of data by counting number of records in a table, given
	 * appropriate filter
	 * 
	 * @return Boolean
	 */
	public Boolean exists(String tableName, String filter) throws Exception {
		long count = count(tableName, filter);
		return count > 0;
	}

	private String arrangeFilter(String filter) throws Exception {
		if (filter.trim().equalsIgnoreCase(""))
			return "";
		return (filter.toUpperCase().contains("WHERE") ? "" : " where ")
				+ filter;
	}

	/**
	 * Generates CSV file from query passed along with the filters
	 * 
	 * @param query
	 * @return
	 */
	public String generateCSVfromQuery(String query) throws Exception {
		return reportUtil.generateCSVfromQuery(query, ',');
	}

	/**
	 * Generate report on server side and return the path it was created to
	 * 
	 * @param Path
	 *            of report as String Report parameters as Parameter[] Report to
	 *            be exported in csv format as Boolean
	 * @param export
	 * @return
	 */
	public String generateReport(String reportName, Parameter[] params,
			boolean export) throws Exception {
		return reportUtil.generateReport(reportName, params, export);
	}

	public String[] getColumnData(String tableName, String columnName,
			String filter) throws Exception {
		Object[] data = HibernateUtil.util.selectObjects("select distinct "
				+ columnName + " from " + tableName + " "
				+ arrangeFilter(filter));
		String[] columnData = new String[data.length];
		for (int i = 0; i < data.length; i++)
			columnData[i] = data[i].toString();
		return columnData;
	}

	public String getCurrentUser() throws Exception {
		return MineTB.getCurrentUser();
	}

	public String getObject(String tableName, String columnName, String filter)
			throws Exception {
		return HibernateUtil.util.selectObject(
				"select " + columnName + " from " + tableName
						+ arrangeFilter(filter)).toString();
	}

	@Override
	public Report[] getReportsList() throws Exception {
		ArrayList<Report> reports = reportUtil.getReportList();
		for (Report report : reports) {
			try {
				report.setParameters(getReportParameters(report.getName())
						.toArray(new Parameter[] {}));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return reports.toArray(new Report[] {});
	}

	/**
	 * Get parameters of given report name (file name without extension),
	 * reading from properties file
	 * 
	 * @param reportName
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public ArrayList<Parameter> getReportParameters(String reportName)
			throws FileNotFoundException, IOException {
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		File propsFile = new File(resourcePath + "rpt" + File.separatorChar
				+ "report.properties");
		Properties props = new Properties();
		props.load(new FileInputStream(propsFile));
		String property = props.getProperty(reportName + ".query");
		if (property != null) {
			parameters.add(new Parameter("query", property, DataType.STRING));
		}
		property = props.getProperty(reportName + ".param.date");
		if (property != null) {
			parameters.add(new Parameter("date", property, DataType.DATE));
		}
		property = props.getProperty(reportName + ".param.location");
		if (property != null) {
			parameters
					.add(new Parameter("location", property, DataType.STRING));
		}
		property = props.getProperty(reportName + ".param.username");
		if (property != null) {
			parameters
					.add(new Parameter("username", property, DataType.STRING));
		}
		return parameters;
	}

	public String[] getRowRecord(String tableName, String[] columnNames,
			String filter) throws Exception {
		return getTableData(tableName, columnNames, filter)[0];
	}

	public String getSnapshotTime() throws Exception {
		Date dt = new Date();
		return DateTimeUtil.getSQLDate(dt);
	}

	public String[][] getTableData(String sqlQuery) {
		Object[][] data = HibernateUtil.util.selectData(sqlQuery);
		String[][] stringData = new String[data.length][];
		for (int i = 0; i < data.length; i++) {
			stringData[i] = new String[data[i].length];
			for (int j = 0; j < stringData[i].length; j++) {
				if (data[i][j] == null)
					data[i][j] = "";
				String str = data[i][j].toString();
				stringData[i][j] = str;
			}
		}
		return stringData;
	}

	public String[][] getTableData(String tableName, String[] columnNames,
			String filter) throws Exception {
		StringBuilder columnList = new StringBuilder();
		for (String s : columnNames) {
			columnList.append(s);
			columnList.append(",");
		}
		columnList.deleteCharAt(columnList.length() - 1);
		return getTableData("select " + columnList.toString() + " from "
				+ tableName + " " + arrangeFilter(filter));
	}

	public int execute(String query) throws Exception {
		return HibernateUtil.util.runCommand(query);
	}

	public Boolean execute(String[] queries) throws Exception {
		for (String s : queries) {
			boolean result = execute(s) >= 0;
			if (!result)
				return false;
		}
		return true;
	}

	public Boolean executeProcedure(String procedure) throws Exception {
		return HibernateUtil.util.runProcedure(procedure);
	}

	/**
	 * Sets current user name, this is due to a strange GWT bug/feature that
	 * shared variables, set from Client-side appear to be empty on Server-side
	 * code
	 * 
	 * @return
	 */
	public void setCurrentUser(String userName) {
		MineTB.setCurrentUser(userName);
	}
}
