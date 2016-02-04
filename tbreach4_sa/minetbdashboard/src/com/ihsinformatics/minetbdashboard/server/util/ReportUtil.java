/**
 * Implements reporting features. 
 */
package com.ihsinformatics.minetbdashboard.server.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import com.ihsinformatics.minetbdashboard.shared.Parameter;
import com.ihsinformatics.minetbdashboard.shared.Report;

/**
 * @author owais.hussain@ihsinformatics.com
 * 
 */
public class ReportUtil {
	private static final String databaseName = "sz_dw";
	private static String reportsPath;
	private static String dataPath;
	public static final char separatorChar = File.separatorChar;
	public static final String[] allowedExtensions = { "jrxml", "doc", "docx",
			"xls", "xlsx", "rar", "zip" };
	private Connection con;

	/**
	 * @param resourcePath
	 */
	@SuppressWarnings("deprecation")
	public ReportUtil(String resourcePath) {
		if (!resourcePath.endsWith(String.valueOf(separatorChar))) {
			resourcePath += separatorChar;
		}
		reportsPath = resourcePath + "rpt" + separatorChar;
		dataPath = resourcePath + "data" + separatorChar;
		try {
			con = HibernateUtil.util.getSession().connection();
			con.setCatalog(databaseName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param resourcePath
	 * @param con
	 */
	public ReportUtil(String resourcePath, Connection con) {
		if (!resourcePath.endsWith(String.valueOf(separatorChar))) {
			resourcePath += separatorChar;
		}
		reportsPath = resourcePath + "rpt" + separatorChar;
		dataPath = resourcePath + "data" + separatorChar;
		this.con = con;
		try {
			con.setCatalog(databaseName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String generateCSVfromQuery(String query, char separator) {
		try {
			Statement statement = con.createStatement();
			ResultSet result = statement.executeQuery(query);
			ArrayList<String> list = new ArrayList<String>();
			int range = result.getMetaData().getColumnCount();
			String record = "";
			for (int i = 0; i < range; i++)
				record += result.getMetaData().getColumnName(i + 1) + separator;
			list.add(record.substring(0, record.length() - 1));
			while (result.next()) {
				record = "";
				for (int i = 0; i < range; i++)
					record += result.getString(i + 1) + separator;
				if (record.length() > 0)
					list.add(record.substring(0, record.length() - 1));
			}
			String dest = getDataPath() + String.valueOf(new Date().getTime())
					+ ".csv";
			StringBuilder text = new StringBuilder();
			for (int i = 0; i < list.size(); i++)
				text.append(list.get(i) + "\r\n");
			// Delete file if existing
			try {
				File file = new File(dest);
				file.delete();
				Writer output = null;
				output = new BufferedWriter(new FileWriter(file));
				output.write(text.toString());
				output.flush();
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return dest.substring(dest.lastIndexOf(File.separatorChar) + 1);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public String generateReport(String fileName, Parameter[] params,
			boolean export) {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < params.length; i++) {
				/**
				 * Cast the parameter into appropriate type
				 */
				map.put(params[i].getName(), toObject(params[i]));
			}
			JasperReport jasperReport = JasperCompileManager
					.compileReport(getReportsPath() + fileName
							+ (fileName.endsWith(".jrxml") ? "" : ".jrxml"));
			JasperPrint print = JasperFillManager.fillReport(jasperReport, map,
					con);
			String dest = getDataPath() + String.valueOf(new Date().getTime())
					+ (export == true ? ".csv" : ".pdf");
			// Delete file if existing
			try {
				File file = new File(dest);
				file.delete();
			} catch (Exception e) {
				// Not implemented
			}
			JRAbstractExporter exporter;
			if (export)
				exporter = new JRCsvExporter();
			else
				exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE, new File(
					dest));
			exporter.exportReport();
			System.out.println(dest.substring(dest
					.lastIndexOf(File.separatorChar) + 1));
			return dest.substring(dest.lastIndexOf(File.separatorChar) + 1);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Get list of Reports in reports directory with last modified date
	 * 
	 * @return String[][]
	 */
	public ArrayList<Report> getReportList() {
		ArrayList<Report> reports = new ArrayList<Report>();
		File dir = new File(getReportsPath());
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				for (String s : allowedExtensions)
					if (name.endsWith(s))
						return true;
				return false;
			}
		};
		File[] files = dir.listFiles(filter);
		for (int i = 0; i < files.length; i++) {
			Report report = new Report();
			report.setPath(getReportsPath() + files[i].getName());
			String fileName = files[i].getName();
			report.setName(fileName.substring(0, fileName.lastIndexOf('.')));
			report.setType(fileName.substring(fileName.lastIndexOf('.') + 1));
			report.setParameters(null);
			reports.add(report);
		}
		return reports;
	}

	public Object toObject(Parameter param) {
		try {
			String value = param.getValue();
			switch (param.getType()) {
			case BOOLEAN:
				return Boolean.parseBoolean(value);
			case BYTE:
				return Byte.parseByte(value);
			case CHAR:
				return value.charAt(0);
			case DATE:
				return new Date(Long.parseLong(value));
			case DOUBLE:
				return Double.parseDouble(value);
			case FLOAT:
				return Float.parseFloat(value);
			case INT:
				return Integer.parseInt(value);
			case LONG:
				return Long.parseLong(value);
			case SHORT:
				return Short.parseShort(value);
			case STRING:
				return value;
			default:
				return null;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getReportsPath() {
		return reportsPath;
	}

	public static String getDataPath() {
		return dataPath;
	}
}
