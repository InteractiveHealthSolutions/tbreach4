/*
Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.minetb.datawarehouse.util;

import java.awt.List;
import java.sql.*;
import java.util.ArrayList;

/**
 * Class to perform some common JDBC Operations
 * 
 * @author owais.hussain@ihsinformatics.com
 * 
 */
public final class DatabaseUtil {
	private Connection con;
	private String url;
	private String driverName;
	private String userName;
	private String password;

	/**
	 * Constructor with arguments
	 * 
	 * @param url
	 * @param dbName
	 * @param driverName
	 * @param userName
	 * @param password
	 */
	public DatabaseUtil(String url, String driverName, String userName,
			String password) {
		this.setConnection(url, driverName, userName, password);
	}

	/**
	 * Set JDBC connection parameters
	 * 
	 * @param url
	 * @param dbName
	 * @param driverName
	 * @param userName
	 * @param password
	 */
	public void setConnection(String url, String driverName, String userName,
			String password) {
		this.setUrl(url);
		this.setDriverName(driverName);
		this.setUser(userName, password);
		this.openConnection();
	}

	/**
	 * JDBC Connection getter
	 * 
	 * @return Connection
	 */
	public Connection getConnection() {
		return con;
	}

	/**
	 * Url setter
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Url getter
	 */

	public String getUrl() {
		return url;
	}

	/**
	 * Database driver setter
	 * 
	 * @param driverName
	 */

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	/**
	 * Database driver getter
	 * 
	 * @return Driver name
	 */

	public String getDriverName() {
		return driverName;
	}

	/**
	 * User setter
	 * 
	 * @param userName
	 * @param password
	 */

	public void setUser(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	/**
	 * User getter
	 * 
	 * @return User name
	 */

	public String getUser() {
		return userName;
	}

	/**
	 * Open connection
	 */

	void openConnection() {
		try {
			Class.forName(driverName).newInstance();
			con = DriverManager.getConnection(this.url, this.userName,
					this.password);
		} catch (Exception e) {
		}
	}

	/**
	 * Close connection
	 */

	void closeConnection() {
		try {
			con.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Create a new database
	 * 
	 * @param databaseName
	 * @return true if the operation was successful, otherwise false
	 */

	public Object createDatabase(String databaseName) {
		String command = "CREATE DATABASE " + databaseName;
		return this.runCommand(CommandType.CREATE, command);
	}

	/**
	 * Delete an existing database
	 * 
	 * @param databaseName
	 * @param defaultDatabase
	 * @return true if the operation was successful, otherwise false
	 */

	public Object deleteDatabase(String databaseName, String defaultDatabase) {
		String command = "USE " + databaseName;
		Object obj = this.runCommand(CommandType.USE, command);
		if (obj.toString() == "true") {
			command = "DROP DATABASE " + databaseName;
			return this.runCommand(CommandType.DROP, command);
		} else {
			return false;
		}
	}

	/**
	 * Retrieve Table names from current Database
	 * 
	 * @return true if the operation was successful, otherwise false
	 */

	public String[] getTableNames() {
		List ls = new List();
		try {
			this.openConnection();
			DatabaseMetaData dbm = con.getMetaData();
			String[] types = {"TABLE"};
			ResultSet rs = dbm.getTables(null, null, "%", types);
			while (rs.next()) {
				ls.add(rs.getString("TABLE_NAME"));
				con.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ls.removeAll();
		}
		String[] tableNames = new String[ls.getItemCount()];
		for (int i = 0; i < ls.getItemCount(); i++)
			tableNames[i] = ls.getItem(i);
		return tableNames;
	}

	/**
	 * Create Unique key for a column in a table
	 * 
	 * @param tableName
	 * @param columnName
	 * @return true if the operation was successful, otherwise false
	 */

	public Object makeUniqueColumn(String tableName, String columnName) {
		String command = "ALTER TABLE " + tableName + " ADD UNIQUE ("
				+ columnName + ")";
		return this.runCommand(CommandType.CREATE, command);
	}

	/**
	 * Create new table in current database giving only one column
	 * 
	 * @param newTableName
	 *            Name for the table creating Like "MyTable"
	 * @param firstColumn
	 *            Name for first column in the table Like "ID"
	 * @param sqlDataType
	 *            Data type for first columns Like "CHAR(10)"
	 * @return true if the operation was successful, otherwise false
	 */

	public Object createTable(String newTableName, String firstColumn,
			String sqlDataType) {
		String command = "CREATE TABLE " + newTableName + " (" + firstColumn
				+ " " + sqlDataType + ")";
		String s = this.runCommand(CommandType.CREATE, command).toString();
		return Boolean.parseBoolean(s);
	}

	/**
	 * Delete a table from current database
	 * 
	 * @param tableName
	 * @return true if the operation was successful, otherwise false
	 */

	public Object deleteTable(String tableName) {
		String command = "DROP TABLE " + tableName;
		String s = this.runCommand(CommandType.DROP, command).toString();
		return Boolean.parseBoolean(s);
	}

	/**
	 * Copy data from one table to another
	 * 
	 * @param sourceTableName
	 *            Source table where data is present
	 * @param destinationTableName
	 *            Destination table where data will be copied
	 * @return Number of records copied
	 */

	public int copyTable(String sourceTableName, String destinationTableName) {
		return this.copyTable(sourceTableName, "*", destinationTableName);
	}

	/**
	 * Copy data from one table to another
	 * 
	 * @param sourceTableName
	 *            Source table where data is present
	 * @param columnList
	 *            Comma separated list of column names Like
	 *            "ID,FirstName,LastName"
	 * @param destinationTableName
	 *            Destination table where data will be copied
	 * @return Number of records copied
	 */

	public int copyTable(String sourceTableName, String columnList,
			String destinationTableName) {
		return this.copyTable(sourceTableName, columnList,
				destinationTableName, "");
	}

	/**
	 * Copy data from one table to another
	 * 
	 * @param sourceTableName
	 *            Source table where data is present
	 * @param columnList
	 *            Comma separated list of column names Like
	 *            "ID,FirstName,LastName"
	 * @param destinationTableName
	 *            Destination table where data will be copied
	 * @param filter
	 *            Filter to copy tables with respect to some criteria like
	 *            "WHERE ID = 100"
	 * @return Number of records copied
	 */

	public int copyTable(String sourceTableName, String columnList,
			String destinationTableName, String filter) {
		String command = "INSERT INTO " + destinationTableName + " SELECT "
				+ columnList + " FROM " + sourceTableName + " " + filter;
		String s = this.runCommand(CommandType.INSERT, command).toString();
		return Integer.parseInt(s);
	}

	/**
	 * Delete all rows from a table
	 * 
	 * @param tableName
	 * @return true if the operation was successful, otherwise false
	 */

	public Object deleteAllRows(String tableName) {
		String command = "TRUNCATE TABLE " + tableName;
		return this.runCommand(CommandType.TRUNCATE, command);
	}

	/**
	 * Add a column to a table
	 * 
	 * @param tableName
	 *            Table name where column will be added Like "MyTable"
	 * @param columnName
	 *            Name of the column to add Like "MyNewColumn"
	 * @param sqlDataType
	 *            Data type of the column Like "CHAR(10)"
	 * @return true if the operation was successful, otherwise false
	 */

	public Object addColumn(String tableName, String columnName,
			String sqlDataType) {
		String command = "ALTER TABLE " + tableName + " ADD " + columnName
				+ " " + sqlDataType;
		return this.runCommand(CommandType.ALTER, command);
	}

	/**
	 * Add a column to a table
	 * 
	 * @param tableName
	 *            Table name where the column exists Like "MyTable"
	 * @param columnName
	 *            Column name Like "MyColumn"
	 * @param newName
	 *            New column name Like "RenamedColumn"
	 * @param sqlDataType
	 *            New data type of the column Like "CHAR(10)"
	 * @return true if the operation was successful, otherwise false
	 */

	public Object changeColumn(String tableName, String columnName,
			String newName, String newDataType) {
		String command = "ALTER TABLE " + tableName + " CHANGE " + columnName
				+ " " + newName + " " + newDataType;
		return Boolean.parseBoolean(this.runCommand(CommandType.ALTER, command)
				.toString());
	}

	/**
	 * Delete a column from a table
	 * 
	 * @param tableName
	 * @param columnName
	 * @return true if the operation was successful, otherwise false
	 */

	public Object deleteColumn(String tableName, String columnName) {
		String command = "ALTER TABLE " + tableName + " DROP " + columnName;
		return this.runCommand(CommandType.ALTER, command);
	}

	/**
	 * Get the total number of rows in a table
	 * 
	 * @param tableName
	 * @return Number of records
	 */

	public long getTotalRows(String tableName) {
		long result = 0;
		try {
			result = this.getTotalRows(tableName, "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Get the total number of rows in a table
	 * 
	 * @param tableName
	 *            Table name to count rows from Like "MyTable"
	 * @param filter
	 *            Filter to specify criteria of counting rows Like
	 *            "WHERE ID = 100"
	 * @return Number of records
	 */

	public long getTotalRows(String tableName, String filter)
			throws SQLException {
		long result = -1;
		try {
			String command = "SELECT COUNT(*) FROM " + tableName + " " + filter;
			String s = this.runCommand(CommandType.SELECT, command).toString();
			result = Long.parseLong(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Get a single record from a table, in case of multiple records only the
	 * first record is returned
	 * 
	 * @param tableName
	 *            Table name where data exists Like "MyTable"
	 * @param filter
	 *            Filter to set criteria to read record Like "WHERE ID = 100"
	 * @return Array of Objects containing record
	 */

	public Object[] getRecord(String tableName, String filter) {
		return this.getRecord(tableName, "*", filter);
	}

	/**
	 * Get a single record from a table, in case of multiple records only the
	 * first record is returned
	 * 
	 * @param tableName
	 *            Table name where data exists Like
	 * @param columnList
	 *            Comma separated list of column names Like
	 *            "ID,FirstName,LastName"
	 * @param filter
	 *            Filter to set criteria to read record Like "WHERE ID = 100"
	 * @return Array of Objects containing record
	 */

	public Object[] getRecord(String tableName, String columnList, String filter) {
		Object[] record;
		ArrayList<Object> array = new ArrayList<Object>();
		try {
			this.openConnection();
			Statement st = con.createStatement();
			String command = "SELECT " + columnList + " FROM " + tableName
					+ " " + filter + " LIMIT 1";
			ResultSet rs = st.executeQuery(command);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				int i = 0;
				while (i < rsmd.getColumnCount()) {
					Object o = rs.getObject(++i) == null ? "" : rs.getObject(i);
					array.add(o);
				}
			}
			this.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		record = array.toArray();
		return record;
	}

	/**
	 * Get a set of records of a single column
	 * 
	 * @param tableName
	 * @param columnName
	 * @param filter
	 * @return
	 */
	public Object[] getColumnData(String tableName, String columnName,
			String filter) {
		// Object array to hold the table data
		Object[] data;
		// Array list of array lists to record data during transaction
		ArrayList<Object> array = new ArrayList<Object>();
		try {
			this.openConnection();
			Statement st = con.createStatement();
			String command = "SELECT " + columnName + " FROM " + tableName
					+ setFilter(filter);
			ResultSet rs = st.executeQuery(command);
			while (rs.next()) {
				array.add(rs.getObject(1));
			}
			// Copy main Array list to an Object array
			data = array.toArray();
			this.closeConnection();
		} catch (Exception e) {
			data = null;
		}
		return data;
	}

	/**
	 * @param filter
	 * @return
	 */
	private String setFilter(String filter) {
		if (filter == null) {
			return "";
		}
		if (filter.equals("")) {
			return filter;
		}
		// If WHERE/where is not mentioned, append
		if (!(filter.startsWith("where") || filter.startsWith("WHERE"))) {
			filter = " WHERE " + filter;
		}
		return filter;
	}

	/**
	 * Get a set of records from database
	 * 
	 * @param tableName
	 *            Table name where data exists
	 * @param columnList
	 *            Comma separated list of columns Like "ID,FirstName,LastName"
	 * @param filter
	 *            Filter to set criteria to read record Like "WHERE ID = 100"
	 * @return 2 dimensional Array of Objects containing records
	 */

	@SuppressWarnings("unchecked")
	public Object[][] getTableData(String tableName, String columnList,
			String filter) {
		// 2 Dimensional Object array to hold the table data
		Object[][] data;
		// Array list of array lists to record data during transaction
		ArrayList<ArrayList<Object>> array = new ArrayList<ArrayList<Object>>();
		try {
			this.openConnection();
			Statement st = con.createStatement();
			String command = "SELECT " + columnList + " FROM " + tableName
					+ setFilter(filter);
			ResultSet rs = st.executeQuery(command);
			// Get the number of columns
			int columns = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				// Array list to temporarily hold a record
				ArrayList<Object> record = new ArrayList<Object>();
				for (int i = 0; i < columns; i++)
					record.add(rs.getObject(i + 1));
				// Add the record to main Array list
				array.add(record);
			}
			// Copy main Array list to an Object array
			Object[] list = array.toArray();
			// Define how many records will be there in table
			data = new Object[list.length][];
			for (int i = 0; i < list.length; i++) {
				// Cast each element in an Array list
				ArrayList<Object> fieldList = (ArrayList<Object>) list[i];
				// Copy record into table
				data[i] = fieldList.toArray();
			}
			this.closeConnection();
		} catch (Exception e) {
			data = null;
		}
		return data;
	}

	/**
	 * Insert a record in a table
	 * 
	 * @param tableName
	 *            Table name where record will be inserted
	 * @param columns
	 *            Comma separated values Like "ID,FirstName,LastName"
	 * @param values
	 *            Comma separated values Like "'1','Owais','Ahmed'"
	 * @return true if the operation was successful, otherwise false
	 */

	public Object insertRecord(String tableName, String columns, String values) {
		String command = "INSERT INTO " + tableName + "(" + columns
				+ ") VALUES (" + values + ")";
		return this.runCommand(CommandType.INSERT, command);
	}

	/**
	 * Insert multiple records in a table
	 * 
	 * @param tableName
	 *            Table name where record will be inserted
	 * @param columns
	 *            Comma separated values Like "ID,FirstName,LastName"
	 * @param values
	 *            Array of Comma separated values Like "1,Owais,Ahmed"
	 * @return
	 */

	public int bulkInsert(String tableName, String columns, String values[]) {
		int result = 0;
		try {
			String comm = columns.replaceAll(" ", "").replaceAll(
					"[a-zA-Z]*[a-zA-Z]", "?");
			String command = "INSERT INTO " + tableName + " (" + columns
					+ ") VALUES (" + comm + ")";
			this.openConnection();
			PreparedStatement st = con.prepareStatement(command);
			for (int i = 0; i < values.length; i++) {
				String[] strings = values[i].split(",");
				for (int j = 0; j < values.length; j++) {
					st.setObject(j + 1, strings[j]);
				}
				st.addBatch();
			}
			int[] results = st.executeBatch();
			this.closeConnection();
			for (int i : results) {
				result += i;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			result = -1;
		}
		return result;
	}

	/**
	 * Update a record in a table
	 * 
	 * @param tableName
	 *            Table name where data exists
	 * @param columns
	 *            Array of column names to be updated
	 * @param values
	 *            Array of values which will be recorded against respective
	 *            column
	 * @param filter
	 *            Filter to set criteria to read record Like "WHERE ID = 100"
	 * @return true if the operation was successful, otherwise false
	 */

	public Object updateRecord(String tableName, String[] columns,
			String[] values, String filter) {
		if (columns.length != values.length)
			return false;
		StringBuilder mapping = new StringBuilder();
		for (int i = 0; i < columns.length; i++) {
			mapping.append(columns[i] + " = '" + values[i] + "', ");
		}
		mapping.deleteCharAt(mapping.lastIndexOf(","));
		String command = "UPDATE " + tableName + " SET A = B " + filter;
		return this.runCommand(CommandType.UPDATE, command);
	}

	/**
	 * Run any SQL command within allowed command types
	 * 
	 * @param type
	 *            Type of Command to be run Like "INSERT"
	 * @param command
	 *            Command Text Like "DROP TABLE MyTable"
	 * @return
	 */

	public Object runCommand(CommandType type, String command) {
		Object obj = new Object();
		try {
			this.openConnection();
			Statement st = con.createStatement();
			switch (type) {
				case ALTER :
				case BACKUP :
				case CREATE :
				case DROP :
				case EXECUTE :
				case GRANT :
				case TRUNCATE :
				case USE :
				case LOCK :
					boolean b = st.execute(command);
					obj = !b;
					break;
				case INSERT :
				case UPDATE :
				case DELETE :
					obj = st.executeUpdate(command);
					break;
				case SELECT :
					this.openConnection();
					ResultSet rs = st.executeQuery(command);
					rs.next();
					obj = rs.getObject(1);
					break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			obj = null;
		} catch (Exception e) {
			e.printStackTrace();
			obj = null;
		}
		this.closeConnection();
		return obj;
	}
}
