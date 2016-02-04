/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors.

Contributors: Tahira Niazi */
/**
 * This class incorporates Open MRS API services. The content type used is JSON for Requests and Responses
 * 
 */

package com.ihsinformatics.tbr4web_pk.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.xerces.impl.dv.util.Base64;
import org.hibernate.NonUniqueObjectException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ContextAuthenticationException;
import org.openmrs.module.ModuleMustStartException;
import org.openmrs.util.DatabaseUpdateException;
import org.openmrs.util.InputRequiredException;
import org.openmrs.util.OpenmrsUtil;

import com.ihsinformatics.tbr4web_pk.server.util.DateTimeUtil;
import com.ihsinformatics.tbr4web_pk.server.util.JsonUtil;
import com.ihsinformatics.tbr4web_pk.shared.CustomMessage;
import com.ihsinformatics.tbr4web_pk.shared.ErrorType;
import com.ihsinformatics.tbr4web_pk.shared.FormType;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MobileService
{
	/**
	 * 
	 */
	private HttpServletRequest		request;

	// OpenMRS-related
	
//	static final String				propFilePath	= "/usr/share/tomcat6/.OpenMRS/openmrs-runtime.properties";
	//static final String propFilePath = "c:\\Application Data\\OpenMRS\\openmrs-runtime.properties";
	
	//static final String				propFilePath	= "C:\\workspace\\tbreach3web\\openmrs-runtime.properties";
	static final String				propFilePath	= "C:\\Users\\Tahira\\AppData\\Roaming\\OpenMRS\\openmrs-runtime.properties";
	private static File				propsFile;
	private static Properties		props;
	private static Properties		tbreachProps;
	private static InputStream		tbreachPropertiesInput = null;		
	private static Connection		conn;
	private static String			url, username, password, appMajorMinorVersion;
	private static MobileService	service			= new MobileService ();

	// Singleton. Called only once to fire up Open MRS
	private MobileService ()
	{
		try
		{
			propsFile = new File (propFilePath);
			props = new Properties ();
			
			// TODO : start from here
			tbreachProps = new Properties();
			tbreachPropertiesInput = MobileService.class.getClassLoader().getResourceAsStream("/tbreach4.properties");
			tbreachProps.load(tbreachPropertiesInput);
			
			appMajorMinorVersion = (String) tbreachProps.get("major.minor");
			
			OpenmrsUtil.loadProperties (props, propsFile);
			url = (String) props.get ("connection.url");
			username = (String) props.get ("connection.username");
			password = (String) props.get ("connection.password");
			Context.startup (url, username, password, props);
			openConnection ();
		}
		catch (ModuleMustStartException e)
		{
			e.printStackTrace ();
		}
		catch (DatabaseUpdateException e)
		{
			e.printStackTrace ();
		}
		catch (InputRequiredException e)
		{
			e.printStackTrace ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		finally
		{
			if (Context.isSessionOpen ())
			{
				Context.closeSession ();
			}
		}
	}

	/**
	 * Initialize native connection
	 * 
	 * @return
	 */
	private boolean openConnection ()
	{
		try
		{
			conn = DriverManager.getConnection (url.substring (0, url.lastIndexOf ('/')), username, password);
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			return false;
		}
		return true;
	}

	public static MobileService getService ()
	{
		return MobileService.service;
	}

	public String handleEvent (HttpServletRequest request)
	{
		System.out.println ("Posting to Server..");
		String response = "Response";
		try
		{
			// Check if the login credentials are provided in request as plain
			// text
			String username = null;
			String password = null;
			try
			{
				username = request.getParameter ("username");
				password = request.getParameter ("password");
				if (username == null || password == null)
				{
					throw new IllegalArgumentException ();
				}
			}
			catch (IllegalArgumentException e)
			{
				// Read the credentials from encrypted Authorization header
				String header = request.getHeader ("Authorization");
				byte[] authBytes = Base64.decode (header);
				String authData = new String (authBytes, "UTF-8");
				// Username and password MUST be separated using colon
				String[] credentials = authData.split (":");
				if (credentials.length == 2)
				{
					username = credentials[0];
					password = credentials[1];
				}
				else
				{
					throw new ContextAuthenticationException ();
				}
			}
			// Open OpenMRS Context session
			Context.openSession ();
			// Authenticate using Username and Password in the parameters
			Context.authenticate (username, password);

			// Read JSON from the Request
			String json = request.getParameter ("content");
			if (json == null)
				throw new JSONException ("JSON Content not found in the request.");
			JSONObject jsonObject = JsonUtil.getJSONObject (json);
			String appVer = jsonObject.getString ("app_ver");
			String mobileMajorMinor = appVer.split("\\.")[0].concat(".").concat(appVer.split("\\.")[1]);
			if (!mobileMajorMinor.equals(appMajorMinorVersion))
			{
				return JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.VERSION_MISMATCH_ERROR)).toString ();
			}
			String formType = jsonObject.getString ("form_name");
			if (formType.equals (FormType.GET_USER))
				response = getUser (formType, jsonObject);
			else if (formType.equals (FormType.GET_LOCATION))
				response = getLocation (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT))
				response = getPatient (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT_OBS))
				response = getPatientObs (formType, jsonObject);
			else if (formType.equals(FormType.GET_SPECIFIC_PATIENT_OBS))
				response = getSpecificPatientObs(formType, jsonObject);
			else if (formType.equals (FormType.GET_SCREENING_REPORT))
				response = getScreeningReport (formType, jsonObject);
			else if (formType.equals (FormType.GET_DAILY_SUMMARY))
				response = getDailySummary (formType, jsonObject);
			else if (formType.equals (FormType.GET_FORMS_BY_DATE))
				response = getFormsByDate (formType, jsonObject);
			else if (formType.equals (FormType.GET_PERFORMANCE_DATA))
				response = getPerformanceData (formType, jsonObject);
			else if (formType.equals (FormType.SEARCH_PATIENTS))
				response = searchPatients (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT_DETAIL))
				response = getPatientDetail (formType, jsonObject);
			else if (formType.equals (FormType.NON_SUSPECT))
				response = doNonSuspectScreening (formType, jsonObject);
			else if (formType.equals (FormType.SCREENING) || formType.equals (FormType.NON_PULMONARY) || formType.equals (FormType.PAEDIATRIC_SCREENING))
				response = doScreening (formType, jsonObject);
			else if (formType.equals (FormType.CUSTOMER_INFO))
				response = doCustomerInfo (formType, jsonObject);
			else if (formType.equals (FormType.BLOOD_SUGAR_TEST))
				response = doBloodSugarTestOrder (formType, jsonObject);
			else if (formType.equals (FormType.BLOOD_SUGAR_RESULTS))
				response = doBloodSugarTestResults (formType, jsonObject);
			else if (formType.equals (FormType.PATIENT_GPS))
				response = doPatientGps (formType, jsonObject);
			else if (formType.equals (FormType.FEEDBACK))
				response = doFeedback (formType, jsonObject);
			else if (formType.equals (FormType.TEST_INDICATION) || formType.equals (FormType.BLOOD_SUGAR_RESULTS) || formType.equals (FormType.CLINICAL_EVALUATION)
					|| formType.equals (FormType.DRUG_DISPERSAL) || formType.equals (FormType.SPUTUM_INSTRUCTIONS))
				response = doGenericForm (formType, jsonObject);
			else if(formType.equals(FormType.GET_PERSON_DETAIL))
				response = getPersonDetail(formType, jsonObject);
			else if(formType.equals(FormType.PATIENT_REGISTRATION))
				response = doPatientRegistration(formType, jsonObject);
			// change from Reverse Contact tracing to Contact Tracing  
			else if(formType.equals(FormType.REVERSE_CONTACT_TRACING))
				response = doReverseContactTracing(formType, jsonObject);
			else if(formType.equals(FormType.ADULT_REVERSE_CONTACT_TRACING))
				response = doAdultReverseContactTracing(formType, jsonObject);
			else if(formType.equals(FormType.PAEDIATRIC_CONTACT_TRACING))
				response = doPaeditricContactTracing(formType, jsonObject);
			else if(formType.equals(FormType.CLINICAL_VISIT_BARRIERS))
				response = doClinicalVisitBarriers(formType, jsonObject);
			else
				throw new Exception ();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.DATA_MISMATCH_ERROR) + "\n" + e.getMessage ()).toString ();
		}
		catch (ContextAuthenticationException e)
		{
			e.printStackTrace ();
			response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.AUTHENTICATION_ERROR) + "\n" + e.getMessage ()).toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR) + "\n" + e.getMessage ()).toString ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.UNKNOWN_ERROR) + "\n" + e.getMessage ()).toString ();
		}
		finally
		{
			if (Context.isSessionOpen ())
			{
				Context.closeSession ();
			}
		}
		return response;
	}

	public HttpServletRequest getRequest ()
	{
		return request;
	}

	public String getUser (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String username = values.getString ("username");
			User user = Context.getUserService ().getUserByUsername (username);
			JSONObject userObj = new JSONObject ();
			userObj.put ("id", user.getUserId ());
			userObj.put ("name", user.getUsername ());
			json = userObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	public String getLocation (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String locationName = values.getString ("location_name");
			List<Location> locations = Context.getLocationService ().getLocations (locationName);
			Location location = locations.get (0);
			JSONObject locationObj = new JSONObject ();
			locationObj.put ("id", location.getLocationId ());
			locationObj.put ("name", location.getName ());
			json = locationObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	/**
	 * Return report of a user provided in JSONObject containing number of
	 * Suspects/Non-suspects in the current month, previous month and all months
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String getScreeningReport (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String username = values.getString ("username");
			User user = Context.getUserService ().getUserByUsername (username);
			user.getId ();
			ArrayList<String> queries = new ArrayList<String> ();
			Calendar cal = Calendar.getInstance ();
			int year = cal.get (Calendar.YEAR);
			int month = cal.get (Calendar.MONTH) + 1;
			// Total non-suspects
			queries.add ("select 'Total Non Suspects' as column_name, count(*) as total from openmrs_rpt.patient where suspected_by = '" + username + "'");
			// Total non-suspects last month
			queries.add ("select 'Non Suspects Last Month' as column_name, count(*) as total from openmrs_rpt.patient where suspected_by = '" + username + "' and year(date_screened) = " + year
					+ " and month(date_screened) = " + (month - 1));
			// Total non-suspects in current month
			queries.add ("select 'Non Suspects This Month' as column_name, count(*) as total from openmrs_rpt.patient where suspected_by = '" + username + "' and year(date_screened) = " + year
					+ " and month(date_screened) = " + month);
			// Total suspects
			queries.add ("select 'Total Suspects' as column_name, count(*) as total from openmrs.patient as p inner join openmrs.users as u on u.user_id = p.creator where u.username = '" + username
					+ "'");
			// Total suspects last month
			queries.add ("select 'Suspects Last Month' as column_name, count(*) as total from openmrs.patient as p inner join openmrs.users as u on u.user_id = p.creator where u.username = '"
					+ username + "' and year(p.date_created) = " + year + " and month(p.date_created) = " + (month - 1));
			// Total suspects in current month
			queries.add ("select 'Suspects This Month' as column_name, count(*) as total from openmrs.patient as p inner join openmrs.users as u on u.user_id = p.creator where u.username = '"
					+ username + "' and year(p.date_created) = " + year + " and month(p.date_created) = " + month);
			JSONObject jsonObj = new JSONObject ();
			for (String q : queries)
			{
				List<List<Object>> result = Context.getAdministrationService ().executeSQL (q, true);
				if (result == null)
				{
					continue;
				}
				String key = result.get (0).get (0).toString ();
				String count = result.get (0).get (1).toString ();
				jsonObj.put (key, count);
			}
			json = jsonObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	/**
	 * Return report of a user provided in JSONObject containing number of
	 * Suspects/Non-suspects in the current date
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String getDailySummary (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String username = values.getString ("username");
			User user = Context.getUserService ().getUserByUsername (username);
			user.getId ();
			ArrayList<String> queries = new ArrayList<String> ();
			String sqlDate = DateTimeUtil.getSQLDate (new Date ());
			// Total non-suspects today
			queries.add ("select 'Non Suspects' as column_name, count(*) as total from openmrs_rpt.patient where suspected_by = '" + username + "' and date(date_screened) = '" + sqlDate + "'");
			// Total suspects today
			queries.add ("select 'Suspects This Month' as column_name, count(*) as total from openmrs.patient as p inner join openmrs.users as u on u.user_id = p.creator where u.username = '"
					+ username + "' and date(p.date_created) = date('" + sqlDate + "')");
			JSONObject jsonObj = new JSONObject ();
			for (String q : queries)
			{
				List<List<Object>> result = Context.getAdministrationService ().executeSQL (q, true);
				if (result == null)
				{
					continue;
				}
				String key = result.get (0).get (0).toString ();
				String count = result.get (0).get (1).toString ();
				jsonObj.put (key, count);
			}
			json = jsonObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	/**
	 * Return report of a user provided in JSONObject containing number of
	 * Suspects/Non-suspects in the date provided in parameters
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String getFormsByDate (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String username = values.getString ("username");
			String sqlDate = values.getString ("date");
			User user = Context.getUserService ().getUserByUsername (username);
			user.getId ();
			ArrayList<String> queries = new ArrayList<String> ();
			// Total non-suspects today
			queries.add ("select 'Non Suspects' as column_name, count(*) as total from openmrs_rpt.patient where suspected_by = '" + username + "' and date_screened = date('" + sqlDate + "')");
			// Total suspects today
			queries.add ("select 'Suspects' as column_name, count(*) as total from openmrs.patient as p inner join openmrs.users as u on u.user_id = p.creator where u.username = '" + username
					+ "' and date(p.date_created) = date('" + sqlDate + "')");
			JSONObject jsonObj = new JSONObject ();
			for (String q : queries)
			{
				List<List<Object>> result = Context.getAdministrationService ().executeSQL (q, true);
				if (result == null)
				{
					continue;
				}
				String key = result.get (0).get (0).toString ();
				String count = result.get (0).get (1).toString ();
				jsonObj.put (key, count);
			}
			json = jsonObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	/**
	 * Return Screening numbers of a user provided in JSONObject containing
	 * number of Suspects/Non-suspects, grouped by months
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String getPerformanceData (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String username = values.getString ("username");
			User user = Context.getUserService ().getUserByUsername (username);
			user.getId ();
			List<List<Object>> result;
			// Total non-suspects
			String query = "select concat(year(date_screened), month(date_screened)) as yearmonth, count(*) as total from openmrs_rpt.patient where suspected_by = '" + username
					+ "' group by concat(year(date_screened), month(date_screened))";
			result = Context.getAdministrationService ().executeSQL (query, true);
			JSONArray jsonArray = new JSONArray ();
			for (int i = 0; i < result.size (); i++)
			{
				JSONObject jsonObj = new JSONObject ();
				jsonObj.put ("Month", result.get (i).get (0));
				jsonObj.put ("Total", result.get (i).get (1));
				jsonArray.put (jsonObj);
			}
			JSONObject jsonObject = new JSONObject ();
			jsonObject.put ("Non-Suspects", jsonArray.toString ());
			// Total suspects
			query = "select concat(year(e.encounter_datetime), '-', month(e.encounter_datetime)) as yearmonth, count(*) as total from encounter as e inner join users as u on u.user_id = e.creator where e.encounter_type = 1 and u.username = '"
					+ username + "' group by concat(year(e.encounter_datetime), month(e.encounter_datetime))";
			result = Context.getAdministrationService ().executeSQL (query, true);
			for (int i = 0; i < result.size (); i++)
			{
				JSONObject jsonObj = new JSONObject ();
				jsonObj.put ("Month", result.get (i).get (0));
				jsonObj.put ("Total", result.get (i).get (1));
				jsonArray.put (jsonObj);
			}
			jsonObject.put ("Suspects", jsonArray.toString ());
			json = jsonObject.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}
	
	//for checking patient existance
	public String getPersonDetail(String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String patientId = values.getString ("patient_id");
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients == null)
			{
				return json;
			}
			if (patients.isEmpty ())
			{
				return json;
			}
			Patient patient = patients.get (0);
			JSONObject patientObj = new JSONObject ();
			patientObj.put ("id", patient.getPatientId ());
			patientObj.put ("first name", patient.getPersonName ().getGivenName());
			patientObj.put("last name", patient.getPersonName().getFamilyName());
			patientObj.put("age", patient.getAge());
			patientObj.put("gender", patient.getGender());
			PersonAttribute pa = patient.getAttribute("Primary Phone");
			if(pa != null)
				patientObj.put("client phone", pa.getValue());
			else
				patientObj.put("client phone", "");
			pa = patient.getAttribute("Secondary Phone");
			if(pa != null)
				patientObj.put("alternate phone", pa.getValue());
			else
				patientObj.put("alternate phone", "");
			
			json = patientObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	public String getPatient (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String patientId = values.getString ("patient_id");
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients == null)
			{
				return json;
			}
			if (patients.isEmpty ())
			{
				return json;
			}
			Patient patient = patients.get (0);
			JSONObject patientObj = new JSONObject ();
			patientObj.put ("id", patient.getPatientId ());
			patientObj.put ("name", patient.getPersonName ().getFullName ());
			json = patientObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	/**
	 * Warning! TNT Method, handle with care. Ever heard of SETI? Yeah, it's
	 * something like that. It searches for details about a Patient
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String getPatientDetail (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		try
		{
			List<Patient> patients = new ArrayList<Patient> ();
			String patientId = values.getString ("patient_id");
			if (!patientId.equals (""))
			{
				patients = Context.getPatientService ().getPatients (patientId);
				if (patients != null)
				{
					Patient p = patients.get (0);
					json.put ("name", p.getPersonName ().getGivenName () + " " + p.getPersonName ().getFamilyName ());
					json.put ("gender", p.getGender ());
					json.put ("age", p.getAge ());
					List<Encounter> encountersByPatient = Context.getEncounterService ().getEncountersByPatient (p);
					JSONArray encountersArray = new JSONArray ();
					for (Encounter e : encountersByPatient)
					{
						JSONObject jsonObj = new JSONObject ();
						jsonObj.put ("encounter", e.getEncounterType ().getName ());
						jsonObj.put ("date", DateTimeUtil.getSQLDate (e.getEncounterDatetime ()));
						encountersArray.put (jsonObj);
					}
					if (encountersArray.length () != 0)
					{
						json.put ("encounters", encountersArray.toString ());
					}
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		finally
		{
			try
			{
				if (json.length () == 0)
				{
					json.put ("result", "FAIL. " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	/**
	 * Return observations by Patient ID and Question Concept (in JSON Object)
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String getPatientObs (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String patientId = values.getString ("patient_id");
			String conceptName = values.getString ("concept");
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			Concept concept = Context.getConceptService ().getConceptByName (conceptName);
			if (patients == null)
			{
				return json;
			}
			if (patients.isEmpty ())
			{
				return json;
			}
			Patient patient = patients.get (0);
			JSONArray obsArray = new JSONArray ();
			List<Obs> obs = new LinkedList<Obs> ();
			if (values.has ("encounter"))
			{
				String encounterName = values.getString ("encounter");
				List<Encounter> encounters = Context.getEncounterService ().getEncountersByPatient (patient);
				for (Encounter e : encounters)
				{
					if (e.getEncounterType ().getName ().equalsIgnoreCase (encounterName))
					{
						Set<Obs> allObs = e.getAllObs ();
						Iterator<Obs> iterator = allObs.iterator ();
						while (iterator.hasNext ())
						{
							Obs o = iterator.next ();
							if (o.getConcept ().equals (concept))
							{
								obs.add (o);
							}
						}
					}
				}
			}
			else
			{
				obs = Context.getObsService ().getObservationsByPersonAndConcept (patient, concept);
			}
			Set<String> obsValues = new HashSet<String> ();
			for (Obs o : obs)
			{
				String value = "";
				String hl7Abbreviation = concept.getDatatype ().getHl7Abbreviation ();
				if (hl7Abbreviation.equals ("NM"))
				{
					value = o.getValueNumeric ().toString ();
				}
				else if (hl7Abbreviation.equals ("CWE"))
				{
					value = o.getValueCoded ().getName ().getName ();
				}
				else if (hl7Abbreviation.equals ("ST"))
				{
					value = o.getValueText ();
				}
				else if (hl7Abbreviation.equals ("DT"))
				{
					value = DateTimeUtil.getSQLDate (o.getValueDate ());
				}
				obsValues.add (value);
			}
			for (String value : obsValues)
			{
				JSONObject jsonObj = new JSONObject ();
				jsonObj.put ("value", value);
				obsArray.put (jsonObj);
			}
			json = obsArray.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}
	
	// TODO: check this for populating concepts with their observations
	public String getSpecificPatientObs (String formType, JSONObject values)
	{
		String json = null;
		String error = "";
		try
		{
			String patientId = values.getString ("patient_id");
			//String conceptName = values.getString ("concept");
			JSONArray conceptsArray = new JSONArray (values.getString ("concepts"));
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			//Concept concept = Context.getConceptService ().getConceptByName (conceptName);
			if (patients.isEmpty ())
				throw new Exception ();
			Patient patient = patients.get (0);
			JSONArray obsArray = new JSONArray ();
			List<Obs> obs = new LinkedList<Obs> ();
			
			if (values.has ("encounter"))
			{
				String encounterName = values.getString ("encounter");
				List<Encounter> encounters = Context.getEncounterService ().getEncountersByPatient (patient);
				
				for (Encounter e : encounters)
				{
					
					if (e.getEncounterType ().getName ().equalsIgnoreCase (encounterName))
					{
						for(int conceptCounter = 0; conceptCounter < conceptsArray.length(); conceptCounter++)
						{
							String conceptName = conceptsArray.getString(conceptCounter);
						
							Concept concept = Context.getConceptService ().getConceptByName (conceptName);
							Set<Obs> allObs = e.getAllObs ();
							Iterator<Obs> iterator = allObs.iterator ();
							while (iterator.hasNext ())
							{
								Obs o = iterator.next ();
								if (o.getConcept ().equals (concept))
								{
									obs.add (o);
								}
							}
						}
					}
				}
			}
			
			Set<String> obsValues = new HashSet<String> ();
			
			for(int conceptsCounter = 0; conceptsCounter < conceptsArray.length(); conceptsCounter++)
			{
				String conceptName = conceptsArray.getString(conceptsCounter);
			
				Concept concept = Context.getConceptService ().getConceptByName (conceptName);
				for (Obs o : obs)
				{
					String value = "";
					String hl7Abbreviation = concept.getDatatype ().getHl7Abbreviation ();
					if (hl7Abbreviation.equals ("NM"))
					{
						value = o.getValueNumeric ().toString ();
					}
					else if (hl7Abbreviation.equals ("CWE"))
					{
						if(o.getValueCoded() != null)
							value = o.getValueCoded ().getName ().getName ();
						else
							value = o.getValueText();
					}
					else if (hl7Abbreviation.equals ("ST"))
					{
						value = o.getValueText ();
					}
					else if (hl7Abbreviation.equals ("DT"))
					{
						value = DateTimeUtil.getSQLDate (o.getValueDate ());
					}
					obsValues.add (value);
					
					JSONObject jsonObj = new JSONObject ();
					if(value != null && !value.equals(""))
					{
						jsonObj.put (conceptName, value);
						obsArray.put (jsonObj);
						break;
					}
				}
			}

			json = obsArray.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = "Error. PatientID: " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND);
			return error;
		}
		
		return json;
	}

	/**
	 * Search and return an array of Patients matching given criteria
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String searchPatients (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		try
		{
			List<Patient> patients = new ArrayList<Patient> ();
			String patientId = values.getString ("patient_id");
			String firstName = values.getString ("first_name");
			String lastName = values.getString ("last_name");
			String gender = values.getString ("gender");
			Integer ageStart = values.getInt ("age_start");
			Integer ageEnd = values.getInt ("age_end");
			// If Patient Id is provided, search only based on it
			if (!patientId.equals (""))
			{
				patients = Context.getPatientService ().getPatients (patientId);
			}
			else
			{
				// Fetch all the people with first OR last name as given
				List<Person> people = Context.getPersonService ().getPeople (firstName, false);
				if (!lastName.equals (""))
				{
					people.addAll (Context.getPersonService ().getPeople (lastName, false));
				}
				Collection<Integer> ids = new ArrayList<Integer> ();
				for (int i = 0; i < people.size (); i++)
				{
					Person p = people.get (i);
					// Filter out using gender criteria
					if (!gender.equals (""))
					{
						if (!p.getGender ().equals (gender))
						{
							continue;
						}
					}
					// Filter out using age criteria
					if (ageStart != null)
					{
						if (p.getAge () < ageStart)
						{
							continue;
						}
					}
					if (ageEnd != null)
					{
						if (p.getAge () > ageEnd)
						{
							continue;
						}
					}
					ids.add (p.getId ());
				}
				patients = Context.getPatientSetService ().getPatients (ids);
			}
			JSONArray jsonArray = new JSONArray ();
			for (Patient p : patients)
			{
				JSONObject jsonObj = new JSONObject ();
				jsonObj.put ("patient_id", p.getPatientIdentifier ().getIdentifier ());
				jsonObj.put ("name", p.getPersonName ().getGivenName () + " " + p.getPersonName ().getFamilyName ());
				jsonObj.put ("gender", gender);
				jsonObj.put ("age", p.getAge ());
				jsonArray.put (jsonObj);
			}
			if (jsonArray.length () != 0)
			{
				json.put ("patients", jsonArray.toString ());
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		finally
		{
			try
			{
				if (json.length () == 0)
				{
					json.put ("result", "FAIL. " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	/**
	 * Save Non-suspects into a separate database
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doNonSuspectScreening (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String patientStatus = "";
		String indexCaseID = ""; // for Contact and Reverse Contact Tracing non-suspects
		try
		{
			//int age = Integer.parseInt (values.get ("age").toString ());
			String gender = values.getString ("gender").toString ();
			//String weight = values.getString ("weight").toString ();
			//String height = values.getString ("height").toString ();
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String formDate = values.getString ("formdate").toString ();
			
			String uuid = UUID.randomUUID ().toString ();
			Date dob = new Date ();
			if(values.has("age"))
			{
				int age = Integer.parseInt (values.get ("age").toString ());
				dob.setYear (dob.getYear () - age);
			}
			else if(values.has("dob"))
			{
				//String dateOfBirth = values.getString("dob").toString();
				//dob = DateTimeUtil.getDateFromString(dateOfBirth, DateTimeUtil.DOB_FORMAT);
				String dateOfBirth = values.getString("dob");
				dob = DateTimeUtil.getDateFromString(dateOfBirth, DateTimeUtil.SQL_DATE);
			}
			
			if(values.has("reverseNonSuspect"))
			{
				patientStatus = "REVERSE";
				indexCaseID = values.getString("indexCaseId");
			}
			else
				patientStatus = "SCREENED";
			
			StringBuffer query = new StringBuffer ();
			query.append ("insert into openmrs_rpt.person (pid, first_name, last_name, gender, dob) values (?, ?, ?, ?, ?)");
			String[] params = {uuid, "", "", gender, DateTimeUtil.getSQLDate (dob)};
			if (executeUpdate (query.toString (), params))
			{
				query = new StringBuffer ();
				//query.append ("insert into openmrs_rpt.patient (pid, patient_id, date_screened, suspected_by, treatment_centre, weight, height, patient_status) values (?, ?, ?, ?, ?, ?, ?, ?)");
				query.append ("insert into openmrs_rpt.patient (pid, patient_id, date_screened, suspected_by, treatment_centre, patient_status, treatment_supporter) values (?, ?, ?, ?, ?, ?, ?)");
				params = new String[] {uuid, uuid, formDate, username, location, /* weight, height, */ patientStatus, indexCaseID};
				if (executeUpdate (query.toString (), params))
				{
					json.put ("result", "SUCCESS");
				}
				else
				{
					json.put ("result", "Failed to Insert record");
					executeUpdate ("delete from openmrs_rpt.person where pid=?", new String[] {uuid});
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		return json.toString ();
	}
	
	public String doPatientRegistration (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		
		try
		{
			String patientId = values.getString("patient_id");
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			
			String addressComplete = values.getString("address");
//			String houseNumber = values.getString("houseNumber");
//			String streetName = values.getString("streetName");
//			String colonyName = values.getString("colonyName");
//			String town = values.getString("town");
			String city = values.getString("city");
			String landmark = values.getString("landmark");
			
			JSONArray attributes = new JSONArray (values.getString ("attributes"));
			
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Get Patient object
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients.isEmpty ())
				throw new Exception ();
			Patient patient = Context.getPatientService ().getPatients (patientId).get (0);
			// Get Person object
			Person person = Context.getPersonService ().getPerson (patient.getPersonId ());
			// Add address details
			{
				PersonAddress address = new PersonAddress();
				address.setAddress1(addressComplete);
				address.setCityVillage(city);
				address.setCountry(landmark);
				address.setCreator(creatorObj);
				address.setDateCreated(new Date());
				person.addAddress(address);
			}
			// Add patient attributes
			for (int i = 0; i < attributes.length (); i++)
			{
				JSONObject pair = attributes.getJSONObject (i);
				PersonAttributeType personAttributeType;
				try
				{
					personAttributeType = Context.getPersonService ().getPersonAttributeTypeByName (pair.getString ("attribute"));
					PersonAttribute attribute = new PersonAttribute ();
					attribute.setAttributeType (personAttributeType);
					attribute.setValue (pair.getString ("value"));
					attribute.setCreator (creatorObj);
					attribute.setDateCreated (new Date ());
					person.addAttribute (attribute);
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
			Context.getPersonService ().savePerson (person);
			Encounter encounter = new Encounter ();
			encounter.setEncounterType (encounterTypeObj);
			encounter.setPatient (patient);
			// In case of Encounter location different than login location
			if (!encounterLocation.equalsIgnoreCase (location))
			{
				locationObj = Context.getLocationService ().getLocation (encounterLocation);
			}
			encounter.setLocation (locationObj);
			encounter.setEncounterDatetime (encounterDatetime);
			encounter.setCreator (creatorObj);
			encounter.setDateCreated (new Date ());
			if (creatorObj.getUsername ().equals (provider))
				encounter.setProvider (creatorObj);
			// Save encounter without observations
			Context.getEncounterService ().saveEncounter (encounter);
			json.put ("result", "SUCCESS");
			
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.PARSING_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = "PatientID: " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND);
		}
		
		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	/**
	 * Save Adult, Paediatric, Non Pulmonary or any other Screening form
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doScreening (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			//int age = Integer.parseInt (values.get ("age").toString ());
			String gender = values.getString ("gender").toString ();
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String patientId = values.getString ("patient_id");
			String givenName = values.getString ("given_name");
			String familyName = values.getString ("family_name");
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			JSONArray obs = new JSONArray (values.getString ("obs"));
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Identifier type
			List<PatientIdentifierType> allIdTypes = Context.getPatientService ().getAllPatientIdentifierTypes ();
			PatientIdentifierType patientIdTypeObj = allIdTypes.get (0);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Create Patient
			{
				// Create Person object
				Person person = new Person ();
				Date dob = new Date ();
				if(encounterType.equals(FormType.SCREENING))
				{
					int age = Integer.parseInt (values.get ("age").toString ());
					dob.setYear (dob.getYear () - age);
				}
				else if(encounterType.equals(FormType.PAEDIATRIC_SCREENING))
				{
					String dateOfBirth = values.getString("dob");
					dob = DateTimeUtil.getDateFromString(dateOfBirth, DateTimeUtil.SQL_DATE);
				}
				person.setBirthdate (dob);
				person.setBirthdateEstimated (true);
				person.setGender (gender);
				person.setCreator (creatorObj);
				person.setDateCreated (new Date ());
				// Create names set
				{
					SortedSet<PersonName> names = new TreeSet<PersonName> ();
					PersonName name = new PersonName (givenName, null, familyName);
					name.setCreator (creatorObj);
					name.setDateCreated (new Date ());
					name.setPreferred (true);
					names.add (name);
					person.setNames (names);
				}
				// Create Patient object
				Patient patient = new Patient (person);
				// Create Patient identifier
				{
					SortedSet<PatientIdentifier> identifiers = new TreeSet<PatientIdentifier> ();
					PatientIdentifier identifier = new PatientIdentifier ();
					identifier.setIdentifier (patientId);
					identifier.setIdentifierType (patientIdTypeObj);
					identifier.setLocation (locationObj);
					identifier.setCreator (creatorObj);
					identifier.setDateCreated (new Date ());
					identifier.setPreferred (true);
					identifiers.add (identifier);
					patient.setIdentifiers (identifiers);
				}
				patient.setCreator (creatorObj);
				patient.setDateCreated (new Date ());
				patient = Context.getPatientService ().savePatient (patient);
				error = "Patient was created with Error. ";
				Encounter encounter = new Encounter ();
				encounter.setEncounterType (encounterTypeObj);
				encounter.setPatient (patient);
				// In case of Encounter location different than login location
				if (!encounterLocation.equalsIgnoreCase (location))
				{
					locationObj = Context.getLocationService ().getLocation (encounterLocation);
				}
				encounter.setLocation (locationObj);
				encounter.setEncounterDatetime (encounterDatetime);
				encounter.setCreator (creatorObj);
				encounter.setDateCreated (new Date ());
				// Create Observations set
				{
					for (int i = 0; i < obs.length (); i++)
					{
						Obs ob = new Obs ();
						// Create Person object
						{
							Person personObj = Context.getPersonService ().getPerson (patient.getPatientId ());
							ob.setPerson (personObj);
						}
						// Create question/answer Concept object
						{
							JSONObject pair = obs.getJSONObject (i);
							Concept concept = Context.getConceptService ().getConcept (pair.getString ("concept"));
							ob.setConcept (concept);
							String hl7Abbreviation = concept.getDatatype ().getHl7Abbreviation ();
							if (hl7Abbreviation.equals ("NM"))
							{
								ob.setValueNumeric (Double.parseDouble (pair.getString ("value")));
							}
							else if (hl7Abbreviation.equals ("CWE"))
							{
								Concept valueObj = Context.getConceptService ().getConcept (pair.getString ("value"));
								ob.setValueCoded (valueObj);
							}
							else if (hl7Abbreviation.equals ("ST"))
							{
								ob.setValueText (pair.getString ("value"));
							}
							else if (hl7Abbreviation.equals ("DT"))
							{
								ob.setValueDate (DateTimeUtil.getDateFromString (pair.getString ("value"), DateTimeUtil.SQL_DATE));
							}
						}
						ob.setObsDatetime (encounterDatetime);
						ob.setLocation (locationObj);
						ob.setCreator (creatorObj);
						ob.setDateCreated (new Date ());
						encounter.addObs (ob);
					}
					if (creatorObj.getUsername ().equals (provider))
						encounter.setProvider (creatorObj);
				}
				Context.getEncounterService ().saveEncounter (encounter);
				json.put ("result", "SUCCESS");
			}
		}
		catch (NonUniqueObjectException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.PARSING_ERROR);
		}
		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}
	
	private String doReverseContactTracing(String formType, JSONObject values)
	{
		JSONObject json = new JSONObject();
		String error = "";
		try
		{
			String patientId = values.getString("patient_id");
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			JSONArray obs = new JSONArray (values.getString ("obs"));
			
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Get Patient object
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients.isEmpty ())
				throw new Exception ();
			Patient patient = Context.getPatientService ().getPatients (patientId).get (0);
			// Get Person object
			Person person = Context.getPersonService ().getPerson (patient.getPersonId ());
			
			Encounter encounter = new Encounter ();
			encounter.setEncounterType (encounterTypeObj);
			encounter.setPatient (patient);
			// In case of Encounter location different than login location
			if (!encounterLocation.equalsIgnoreCase (location))
			{
				locationObj = Context.getLocationService ().getLocation (encounterLocation);
			}
			encounter.setLocation (locationObj);
			encounter.setEncounterDatetime (encounterDatetime);
			encounter.setCreator (creatorObj);
			encounter.setDateCreated (new Date ());
			// Create Observations set
			{
				for (int i = 0; i < obs.length (); i++)
				{
					Obs ob = new Obs ();
					// Create Person object
					{
						Person personObj = Context.getPersonService ().getPerson (patient.getPatientId ());
						ob.setPerson (personObj);
					}
					// Create question/answer Concept object
					{
						JSONObject pair = obs.getJSONObject (i);
						Concept concept = Context.getConceptService ().getConcept (pair.getString ("concept"));
						ob.setConcept (concept);
						String hl7Abbreviation = concept.getDatatype ().getHl7Abbreviation ();
						if (hl7Abbreviation.equals ("NM"))
						{
							ob.setValueNumeric (Double.parseDouble (pair.getString ("value")));
						}
						else if (hl7Abbreviation.equals ("CWE"))
						{
							Concept valueObj = Context.getConceptService ().getConcept (pair.getString ("value"));
							ob.setValueCoded (valueObj);
						}
						else if (hl7Abbreviation.equals ("ST"))
						{
							ob.setValueText (pair.getString ("value"));
						}
						else if (hl7Abbreviation.equals ("DT"))
						{
							ob.setValueDate (DateTimeUtil.getDateFromString (pair.getString ("value"), DateTimeUtil.SQL_DATE));
						}
					}
					ob.setObsDatetime (encounterDatetime);
					ob.setLocation (locationObj);
					ob.setCreator (creatorObj);
					ob.setDateCreated (new Date ());
					encounter.addObs (ob);
				}
				if (creatorObj.getUsername ().equals (provider))
					encounter.setProvider (creatorObj);
			}
			Context.getEncounterService ().saveEncounter (encounter);
			json.put ("result", "SUCCESS");
			
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			error += CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR); 
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			error += CustomMessage.getErrorMessage(ErrorType.PARSING_ERROR); 
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = "PatientID: " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND);
		}

		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}	
	
	private String doPaeditricContactTracing(String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			//int age = Integer.parseInt (values.get ("age").toString ());
			String gender = values.getString ("gender").toString ();
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String patientId = values.getString ("patient_id");
			String givenName = values.getString ("given_name");
			String familyName = values.getString ("family_name");
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			String dateOfBirth = values.getString("dob");
			Date dob = new Date ();
			dob = DateTimeUtil.getDateFromString(dateOfBirth, DateTimeUtil.SQL_DATE);
			
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			JSONArray obs = new JSONArray (values.getString ("obs"));
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Identifier type
			List<PatientIdentifierType> allIdTypes = Context.getPatientService ().getAllPatientIdentifierTypes ();
			PatientIdentifierType patientIdTypeObj = allIdTypes.get (0);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Create Patient
			{
				// Create Person object
				Person person = new Person ();				
//				else if(encounterType.equals(FormType.PAEDIATRIC_SCREENING))
//				{
//					String dateOfBirth = values.getString("dob");
//					dob = DateTimeUtil.getDateFromString(dateOfBirth, DateTimeUtil.SQL_DATE);
//				}
				person.setBirthdate (dob);
				person.setBirthdateEstimated (true);
				person.setGender (gender);
				person.setCreator (creatorObj);
				person.setDateCreated (new Date ());
				// Create names set
				{
					SortedSet<PersonName> names = new TreeSet<PersonName> ();
					PersonName name = new PersonName (givenName, null, familyName);
					name.setCreator (creatorObj);
					name.setDateCreated (new Date ());
					name.setPreferred (true);
					names.add (name);
					person.setNames (names);
				}
				// Create Patient object
				Patient patient = new Patient (person);
				// Create Patient identifier
				{
					SortedSet<PatientIdentifier> identifiers = new TreeSet<PatientIdentifier> ();
					PatientIdentifier identifier = new PatientIdentifier ();
					identifier.setIdentifier (patientId);
					identifier.setIdentifierType (patientIdTypeObj);
					identifier.setLocation (locationObj);
					identifier.setCreator (creatorObj);
					identifier.setDateCreated (new Date ());
					identifier.setPreferred (true);
					identifiers.add (identifier);
					patient.setIdentifiers (identifiers);
				}
				patient.setCreator (creatorObj);
				patient.setDateCreated (new Date ());
				patient = Context.getPatientService ().savePatient (patient);
				error = "Patient was created with Error. ";
				Encounter encounter = new Encounter ();
				encounter.setEncounterType (encounterTypeObj);
				encounter.setPatient (patient);
				// In case of Encounter location different than login location
				if (!encounterLocation.equalsIgnoreCase (location))
				{
					locationObj = Context.getLocationService ().getLocation (encounterLocation);
				}
				encounter.setLocation (locationObj);
				encounter.setEncounterDatetime (encounterDatetime);
				encounter.setCreator (creatorObj);
				encounter.setDateCreated (new Date ());
				// Create Observations set
				{
					for (int i = 0; i < obs.length (); i++)
					{
						Obs ob = new Obs ();
						// Create Person object
						{
							Person personObj = Context.getPersonService ().getPerson (patient.getPatientId ());
							ob.setPerson (personObj);
						}
						// Create question/answer Concept object
						{
							JSONObject pair = obs.getJSONObject (i);
							Concept concept = Context.getConceptService ().getConcept (pair.getString ("concept"));
							ob.setConcept (concept);
							String hl7Abbreviation = concept.getDatatype ().getHl7Abbreviation ();
							if (hl7Abbreviation.equals ("NM"))
							{
								ob.setValueNumeric (Double.parseDouble (pair.getString ("value")));
							}
							else if (hl7Abbreviation.equals ("CWE"))
							{
								Concept valueObj = Context.getConceptService ().getConcept (pair.getString ("value"));
								ob.setValueCoded (valueObj);
							}
							else if (hl7Abbreviation.equals ("ST"))
							{
								ob.setValueText (pair.getString ("value"));
							}
							else if (hl7Abbreviation.equals ("DT"))
							{
								ob.setValueDate (DateTimeUtil.getDateFromString (pair.getString ("value"), DateTimeUtil.SQL_DATE));
							}
						}
						ob.setObsDatetime (encounterDatetime);
						ob.setLocation (locationObj);
						ob.setCreator (creatorObj);
						ob.setDateCreated (new Date ());
						encounter.addObs (ob);
					}
					//TODO: check the code below .. it throws exception
					if (creatorObj.getUsername ().equals (provider))
						encounter.setProvider (creatorObj);
				}
				
				Context.getEncounterService ().saveEncounter (encounter);
				json.put ("result", "SUCCESS");
			}
		}
		catch (NonUniqueObjectException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.PARSING_ERROR);
		}
		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	
	private String doAdultReverseContactTracing(String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			//int age = Integer.parseInt (values.get ("age").toString ());
			String gender = values.getString ("gender").toString ();
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String patientId = values.getString ("patient_id");
			String givenName = values.getString ("given_name");
			String familyName = values.getString ("family_name");
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			JSONArray obs = new JSONArray (values.getString ("obs"));
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Identifier type
			List<PatientIdentifierType> allIdTypes = Context.getPatientService ().getAllPatientIdentifierTypes ();
			PatientIdentifierType patientIdTypeObj = allIdTypes.get (0);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Create Patient
			{
				// Create Person object
				Person person = new Person ();
				Date dob = new Date ();
				if(encounterType.equals(FormType.ADULT_REVERSE_CONTACT_TRACING))
				{
					int age = Integer.parseInt (values.get ("age").toString ());
					dob.setYear (dob.getYear () - age);
				}
//				else if(encounterType.equals(FormType.PAEDIATRIC_SCREENING))
//				{
//					String dateOfBirth = values.getString("dob");
//					dob = DateTimeUtil.getDateFromString(dateOfBirth, DateTimeUtil.SQL_DATE);
//				}
				person.setBirthdate (dob);
				person.setBirthdateEstimated (true);
				person.setGender (gender);
				person.setCreator (creatorObj);
				person.setDateCreated (new Date ());
				// Create names set
				{
					SortedSet<PersonName> names = new TreeSet<PersonName> ();
					PersonName name = new PersonName (givenName, null, familyName);
					name.setCreator (creatorObj);
					name.setDateCreated (new Date ());
					name.setPreferred (true);
					names.add (name);
					person.setNames (names);
				}
				// Create Patient object
				Patient patient = new Patient (person);
				// Create Patient identifier
				{
					SortedSet<PatientIdentifier> identifiers = new TreeSet<PatientIdentifier> ();
					PatientIdentifier identifier = new PatientIdentifier ();
					identifier.setIdentifier (patientId);
					identifier.setIdentifierType (patientIdTypeObj);
					identifier.setLocation (locationObj);
					identifier.setCreator (creatorObj);
					identifier.setDateCreated (new Date ());
					identifier.setPreferred (true);
					identifiers.add (identifier);
					patient.setIdentifiers (identifiers);
				}
				patient.setCreator (creatorObj);
				patient.setDateCreated (new Date ());
				patient = Context.getPatientService ().savePatient (patient);
				error = "Patient was created with Error. ";
				Encounter encounter = new Encounter ();
				encounter.setEncounterType (encounterTypeObj);
				encounter.setPatient (patient);
				// In case of Encounter location different than login location
				if (!encounterLocation.equalsIgnoreCase (location))
				{
					locationObj = Context.getLocationService ().getLocation (encounterLocation);
				}
				encounter.setLocation (locationObj);
				encounter.setEncounterDatetime (encounterDatetime);
				encounter.setCreator (creatorObj);
				encounter.setDateCreated (new Date ());
				// Create Observations set
				{
					for (int i = 0; i < obs.length (); i++)
					{
						Obs ob = new Obs ();
						// Create Person object
						{
							Person personObj = Context.getPersonService ().getPerson (patient.getPatientId ());
							ob.setPerson (personObj);
						}
						// Create question/answer Concept object
						{
							JSONObject pair = obs.getJSONObject (i);
							Concept concept = Context.getConceptService ().getConcept (pair.getString ("concept"));
							ob.setConcept (concept);
							String hl7Abbreviation = concept.getDatatype ().getHl7Abbreviation ();
							if (hl7Abbreviation.equals ("NM"))
							{
								ob.setValueNumeric (Double.parseDouble (pair.getString ("value")));
							}
							else if (hl7Abbreviation.equals ("CWE"))
							{
								Concept valueObj = Context.getConceptService ().getConcept (pair.getString ("value"));
								ob.setValueCoded (valueObj);
							}
							else if (hl7Abbreviation.equals ("ST"))
							{
								ob.setValueText (pair.getString ("value"));
							}
							else if (hl7Abbreviation.equals ("DT"))
							{
								ob.setValueDate (DateTimeUtil.getDateFromString (pair.getString ("value"), DateTimeUtil.SQL_DATE));
							}
						}
						ob.setObsDatetime (encounterDatetime);
						ob.setLocation (locationObj);
						ob.setCreator (creatorObj);
						ob.setDateCreated (new Date ());
						encounter.addObs (ob);
					}
					//TODO: check the code below .. it throws exception
					if (creatorObj.getUsername ().equals (provider))
						encounter.setProvider (creatorObj);
				}
				
				Context.getEncounterService ().saveEncounter (encounter);
				json.put ("result", "SUCCESS");
			}
		}
		catch (NonUniqueObjectException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			error += CustomMessage.getErrorMessage (ErrorType.PARSING_ERROR);
		}
		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}
	
	private String doClinicalVisitBarriers(String formType, JSONObject values)
	{
		JSONObject json = new JSONObject();
		String error = "";
		try
		{
			String patientId = values.getString("patient_id");
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			JSONArray obs = new JSONArray (values.getString ("obs"));
			
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Get Patient object
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients.isEmpty ())
				throw new Exception ();
			Patient patient = Context.getPatientService ().getPatients (patientId).get (0);
			// Get Person object
			Person person = Context.getPersonService ().getPerson (patient.getPersonId ());
			
			Encounter encounter = new Encounter ();
			encounter.setEncounterType (encounterTypeObj);
			encounter.setPatient (patient);
			// In case of Encounter location different than login location
			if (!encounterLocation.equalsIgnoreCase (location))
			{
				locationObj = Context.getLocationService ().getLocation (encounterLocation);
			}
			encounter.setLocation (locationObj);
			encounter.setEncounterDatetime (encounterDatetime);
			encounter.setCreator (creatorObj);
			encounter.setDateCreated (new Date ());
			// Create Observations set
			{
				for (int i = 0; i < obs.length (); i++)
				{
					Obs ob = new Obs ();
					// Create Person object
					{
						Person personObj = Context.getPersonService ().getPerson (patient.getPatientId ());
						ob.setPerson (personObj);
					}
					// Create question/answer Concept object
					{
						JSONObject pair = obs.getJSONObject (i);
						Concept concept = Context.getConceptService ().getConcept (pair.getString ("concept"));
						ob.setConcept (concept);
						String hl7Abbreviation = concept.getDatatype ().getHl7Abbreviation ();
						if (hl7Abbreviation.equals ("NM"))
						{
							ob.setValueNumeric (Double.parseDouble (pair.getString ("value")));
						}
						else if (hl7Abbreviation.equals ("CWE"))
						{
							Concept valueObj = Context.getConceptService ().getConcept (pair.getString ("value"));
							ob.setValueCoded (valueObj);
						}
						else if (hl7Abbreviation.equals ("ST"))
						{
							ob.setValueText (pair.getString ("value"));
						}
						else if (hl7Abbreviation.equals ("DT"))
						{
							ob.setValueDate (DateTimeUtil.getDateFromString (pair.getString ("value"), DateTimeUtil.SQL_DATE));
						}
					}
					ob.setObsDatetime (encounterDatetime);
					ob.setLocation (locationObj);
					ob.setCreator (creatorObj);
					ob.setDateCreated (new Date ());
					encounter.addObs (ob);
				}
				if (creatorObj.getUsername ().equals (provider))
					encounter.setProvider (creatorObj);
			}
			Context.getEncounterService ().saveEncounter (encounter);
			json.put ("result", "SUCCESS");
			
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			error += CustomMessage.getErrorMessage(ErrorType.INVALID_DATA_ERROR); 
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			error += CustomMessage.getErrorMessage(ErrorType.PARSING_ERROR); 
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = "PatientID: " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND);
		}

		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}
	
	
	/**
	 * Save Client's Contact and Address information. Also creates and Encounter
	 * without Observations
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doCustomerInfo (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String patientId = values.getString ("patient_id");
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			String address1 = values.getString ("address1");
			String address2 = values.getString ("address2");
			String cityVillage = values.getString ("cityVillage");
			String country = values.getString ("country");
			JSONArray attributes = new JSONArray (values.getString ("attributes"));
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Get Patient object
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients.isEmpty ())
				throw new Exception ();
			Patient patient = Context.getPatientService ().getPatients (patientId).get (0);
			// Get Person object
			Person person = Context.getPersonService ().getPerson (patient.getPersonId ());
			// Add address details
			{
				PersonAddress address = new PersonAddress ();
				address.setAddress1 (address1);
				address.setAddress2 (address2);
				address.setCityVillage (cityVillage);
				address.setCountry (country);
				address.setCreator (creatorObj);
				address.setDateCreated (new Date ());
				person.addAddress (address);
			}
			// Add patient attributes
			for (int i = 0; i < attributes.length (); i++)
			{
				JSONObject pair = attributes.getJSONObject (i);
				PersonAttributeType personAttributeType;
				try
				{
					personAttributeType = Context.getPersonService ().getPersonAttributeTypeByName (pair.getString ("attribute"));
					PersonAttribute attribute = new PersonAttribute ();
					attribute.setAttributeType (personAttributeType);
					attribute.setValue (pair.getString ("value"));
					attribute.setCreator (creatorObj);
					attribute.setDateCreated (new Date ());
					person.addAttribute (attribute);
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
			Context.getPersonService ().savePerson (person);
			Encounter encounter = new Encounter ();
			encounter.setEncounterType (encounterTypeObj);
			encounter.setPatient (patient);
			// In case of Encounter location different than login location
			if (!encounterLocation.equalsIgnoreCase (location))
			{
				locationObj = Context.getLocationService ().getLocation (encounterLocation);
			}
			encounter.setLocation (locationObj);
			encounter.setEncounterDatetime (encounterDatetime);
			encounter.setCreator (creatorObj);
			encounter.setDateCreated (new Date ());
			if (creatorObj.getUsername ().equals (provider))
				encounter.setProvider (creatorObj);
			// Save encounter without observations
			Context.getEncounterService ().saveEncounter (encounter);
			json.put ("result", "SUCCESS");
		}
		catch (NonUniqueObjectException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.PARSING_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = "PatientID: " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND);
		}
		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	/**
	 * Save Blood Sugar Test Order form after validating
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doBloodSugarTestOrder (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			JSONArray obs = new JSONArray (values.getString ("obs"));
			for (int i = 0; i < obs.length (); i++)
			{
				JSONObject pair = obs.getJSONObject (i);
				String conceptName = pair.getString ("concept");
				if (conceptName.equalsIgnoreCase ("Blood Sugar Test Barcode"))
				{
					List<Concept> concepts = new ArrayList<Concept> ();
					Concept concept = Context.getConceptService ().getConcept ("Blood Sugar Test Barcode");
					concepts.add (concept);
					Date start = new Date ();
					Date end = new Date ();
					start.setYear (end.getYear () - 99);
					List<Obs> observations = Context.getObsService ().getObservations (concepts, start, end);
					String value = pair.getString ("value");
					for (Obs o : observations)
					{
						if (value.equalsIgnoreCase (o.getValueText ()))
						{
							throw new Exception ();
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
			try
			{
				json.put ("result", "FAIL. " + error);
				return json.toString ();
			}
			catch (JSONException ex)
			{
				ex.printStackTrace ();
			}
		}
		return doGenericForm (formType, values);
	}

	/**
	 * Save Blood Sugar Test Results form after validating
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String doBloodSugarTestResults (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			JSONArray obs = new JSONArray (values.getString ("obs"));
			for (int i = 0; i < obs.length (); i++)
			{
				JSONObject pair = obs.getJSONObject (i);
				String conceptName = pair.getString ("concept");
				if (conceptName.equalsIgnoreCase ("Blood Sugar Test Barcode"))
				{
					String value = pair.getString ("value");
					String query = "select count(*) as total from openmrs.obs where value_text = ?";
					String[][] results = executeQuery (query, new String[] {value});
					if (results != null)
					{
						if (results.length > 0)
						{
							int records = Integer.parseInt (results[0][0]);
							if (records != 1)
							{
								throw new Exception ();
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
			try
			{
				json.put ("result", "FAIL. " + error);
				return json.toString ();
			}
			catch (JSONException ex)
			{
				ex.printStackTrace ();
			}
		}
		return doGenericForm (formType, values);
	}

	/**
	 * Save Forms consisting of Encounters and Observations only
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doGenericForm (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String patientId = values.getString ("patient_id");
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			JSONArray obs = new JSONArray (values.getString ("obs"));
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Get Patient object
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients.isEmpty ())
				throw new Exception ();
			Patient patient = Context.getPatientService ().getPatients (patientId).get (0);
			// Create Encounter
			Encounter encounter = new Encounter ();
			encounter.setEncounterType (encounterTypeObj);
			encounter.setPatient (patient);
			// In case of Encounter location different than login location
			if (!encounterLocation.equalsIgnoreCase (location))
			{
				locationObj = Context.getLocationService ().getLocation (encounterLocation);
			}
			encounter.setLocation (locationObj);
			encounter.setEncounterDatetime (encounterDatetime);
			encounter.setCreator (creatorObj);
			encounter.setDateCreated (new Date ());
			// Create Observations set
			for (int i = 0; i < obs.length (); i++)
			{
				Obs ob = new Obs ();
				// Create Person object
				{
					Person personObj = Context.getPersonService ().getPerson (patient.getPatientId ());
					ob.setPerson (personObj);
				}
				// Create question/answer Concept object
				{
					JSONObject pair = obs.getJSONObject (i);
					Concept concept = Context.getConceptService ().getConceptByName (pair.getString ("concept"));
					ob.setConcept (concept);
					String hl7Abbreviation = concept.getDatatype ().getHl7Abbreviation ();
					if (hl7Abbreviation.equals ("NM"))
					{
						ob.setValueNumeric (Double.parseDouble (pair.getString ("value")));
					}
					else if (hl7Abbreviation.equals ("CWE"))
					{
						Concept valueObj = Context.getConceptService ().getConcept (pair.getString ("value"));
						ob.setValueCoded (valueObj);
					}
					else if (hl7Abbreviation.equals ("ST"))
					{
						ob.setValueText (pair.getString ("value"));
					}
					else if (hl7Abbreviation.equals ("DT"))
					{
						ob.setValueDate (DateTimeUtil.getDateFromString (pair.getString ("value"), DateTimeUtil.SQL_DATE));
					}
				}
				ob.setObsDatetime (encounterDatetime);
				ob.setLocation (locationObj);
				ob.setCreator (creatorObj);
				ob.setDateCreated (new Date ());
				encounter.addObs (ob);
			}
			if (creatorObj.getUsername ().equals (provider))
				encounter.setProvider (creatorObj);
			Context.getEncounterService ().saveEncounter (encounter);
			json.put ("result", "SUCCESS");
		}
		catch (NonUniqueObjectException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.PARSING_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = "PatientID: " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND);
		}
		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	/**
	 * Save Client's GPS and Address information. Also creates and Encounter
	 * without Observations
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String doPatientGps (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String patientId = values.getString ("patient_id");
			String encounterType = values.getString ("encounter_type");
			String formDate = values.getString ("form_date");
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");
			String address1 = values.getString ("address1");
			String colony = values.getString ("address2");
			String town = values.getString ("address3");
			String cityVillage = values.getString ("cityVillage");
			String country = values.getString ("country");
			String latitude = values.getString ("latitude");
			String longitude = values.getString ("longitude");
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Get Patient object
			List<Patient> patients = Context.getPatientService ().getPatients (patientId);
			if (patients.isEmpty ())
				throw new Exception ();
			Patient patient = Context.getPatientService ().getPatients (patientId).get (0);
			// Get Person object
			Person person = Context.getPersonService ().getPerson (patient.getPersonId ());
			// Add address details
			{
				PersonAddress address = new PersonAddress ();
				address.setLatitude (latitude);
				address.setLongitude (longitude);
				address.setAddress1 (address1);
				address.setAddress2 (colony);
				address.setAddress3 (town);
				address.setCityVillage (cityVillage);
				address.setCountry (country);
				address.setCreator (creatorObj);
				address.setDateCreated (new Date ());
				person.addAddress (address);
			}
			Context.getPersonService ().savePerson (person);
			Encounter encounter = new Encounter ();
			encounter.setEncounterType (encounterTypeObj);
			encounter.setPatient (patient);
			// In case of Encounter location different than login location
			if (!encounterLocation.equalsIgnoreCase (location))
			{
				locationObj = Context.getLocationService ().getLocation (encounterLocation);
			}
			encounter.setLocation (locationObj);
			encounter.setEncounterDatetime (encounterDatetime);
			encounter.setCreator (creatorObj);
			encounter.setDateCreated (new Date ());
			if (creatorObj.getUsername ().equals (provider))
				encounter.setProvider (creatorObj);
			// Save encounter without observations
			Context.getEncounterService ().saveEncounter (encounter);
			json.put ("result", "SUCCESS");
		}
		catch (NonUniqueObjectException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.DUPLICATION_ERROR);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.INVALID_DATA_ERROR);
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
			error = CustomMessage.getErrorMessage (ErrorType.PARSING_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			error = "PatientID: " + CustomMessage.getErrorMessage (ErrorType.ITEM_NOT_FOUND);
		}
		finally
		{
			try
			{
				if (!json.has ("result"))
				{
					json.put ("result", "FAIL. " + error);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return json.toString ();
	}

	/**
	 * Save user's Feedback form into separate database
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */
	public String doFeedback (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		try
		{
			String location = values.getString ("location").toUpperCase ();
			String feedbackType = values.getString ("feedback_type").toUpperCase ();
			String feedbackText = values.getString ("feedback").toUpperCase ();
			String userName = values.getString ("username").toUpperCase ();
			StringBuffer query = new StringBuffer ();
			query.append ("insert into openmrs_rpt.feedback (sender_id,feedback_type,feedback,date_reported,feedback_status) values (?, ?, ?, ?, ?)");
			String[] params = {userName, feedbackType, feedbackText.replace ("'", "") + ". Location " + location, DateTimeUtil.getSQLDate (new Date ()), "PENDING"};
			if (executeUpdate (query.toString (), params))
			{
				json.put ("result", "SUCCESS");
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json.toString ();
	}

	/**
	 * Prepares and returns screening numbers by users
	 * 
	 * @param granularity
	 *            pass day, week or month to get the level of aggregate
	 * @return
	 */
	public String[][] getScreeningData (String granularity, String userLike)
	{
		String dropQuery = "drop table if exists openmrs_rpt.screening_summary";
		execute (dropQuery);
		String nonSuspectDateFilter = "year(date_screened) = year(curdate()) ";
		String suspectDateFilter = "year(p.date_created) = year(curdate()) ";
		if (granularity.equals ("month"))
		{
			nonSuspectDateFilter += "and month(date_screened) = month(curdate()) ";
			suspectDateFilter += "and month(p.date_created) = month(curdate()) ";
		}
		else if (granularity.equals ("week"))
		{
			nonSuspectDateFilter += "and month(date_screened) = month(curdate()) and week(date_screened) = week(curdate()) ";
			suspectDateFilter += "and month(p.date_created) = month(curdate()) and week(p.date_created) = week(curdate()) ";
		}
		else if (granularity.equals ("day"))
		{
			nonSuspectDateFilter += "and month(date_screened) = month(curdate()) and day(date_screened) = day(curdate()) ";
			suspectDateFilter += "and month(p.date_created) = month(curdate()) and day(p.date_created) = day(curdate()) ";
		}
		String createQuery = "create table openmrs_rpt.screening_summary " + "select suspected_by as username, 'Non Suspects' as what, count(*) as total from openmrs_rpt.patient "
				+ "where suspected_by like '" + userLike + "' and " + nonSuspectDateFilter + " group by suspected_by "
				+ "union select u.username, 'Suspects' as what, count(*) as total from openmrs.patient as p " + "inner join openmrs.users as u on u.user_id = p.creator " + "where u.username like '"
				+ userLike + "' and " + suspectDateFilter + " group by u.username";
		execute (createQuery);
		String selectQuery = "select substring(u.username, 1, 2) as centre, u.username, ifnull(s1.total, 0) as suspects, ifnull(s2.total, 0) as non_suspects from openmrs.users as u left outer join openmrs_rpt.screening_summary as s1 on s1.username = u.username and s1.what = 'Suspects' left outer join openmrs_rpt.screening_summary as s2 on s2.username = u.username and s2.what = 'Non Suspects' where u.username like '"
				+ userLike + "' and u.retired = 0 having suspects is not null or non_suspects is not null order by username";
		String[][] data = executeQuery (selectQuery, null);
		return data;
	}
	
	public String[][] getNonSuspectScreeningData(String fromDate, String toDate, String suspectedBy, String treatmentCenter, String patientType)
	{
		String dateFilter = "";
		String suspectedByFilter = "";
		String treatmentCenterFilter = "";
		String ageFilter = "";
		String[][] data = null;
		
		if(!fromDate.equals("") && !toDate.equals(""))
		{
			dateFilter = " date(date_screened) between '"+ fromDate +"' AND '"+ toDate + "'";
		}
		else
		{
			dateFilter = " date(date_screened) between curdate() AND curdate() ";
		}
		
		if(suspectedBy.equals("All"))
		{
			suspectedByFilter = " suspected_by like '%'";
		}
		else 
		{
			suspectedByFilter = " suspected_by like '" + suspectedBy + "%'" ;
		}
		
		if(treatmentCenter.equals("All"))
		{
			treatmentCenterFilter = " treatment_centre like '%'";
		}
		else
		{
			treatmentCenterFilter = " treatment_centre like '"+ treatmentCenter +"%'";
		}
		
		if(patientType.equals("both"))
		{
			ageFilter = " (((Year(date_screened) - Year(dob))*12 + (Month(date_screened) - Month(dob)))/12) >=0 ";
		}
		
		else if(patientType.equals("adult"))
		{
			ageFilter = " (((Year(date_screened) - Year(dob))*12 + (Month(date_screened) - Month(dob)))/12) >15 ";
		}
		else if(patientType.equals("paeds"))
		{
			ageFilter = " (((Year(date_screened) - Year(dob))*12 + (Month(date_screened) - Month(dob)))/12) <=15 ";
		}
		
		String selectQuery = "select treatment_centre, suspected_by as username, 'Non Suspects' as what, count(*) as total from openmrs_rpt.patient pt "
							 + "JOIN openmrs_rpt.person pr on pr.pid = pt.pid "
							 + " where" + suspectedByFilter + " AND "+ treatmentCenterFilter +" AND "+ dateFilter +" AND " 
							 + ageFilter + " group by suspected_by ";
		
		
		//execute (createQuery);

		data = executeQuery (selectQuery, null);
		return data;
		
	}
	
	public String generateDemographicsCSV()
	{
		ArrayList<String> list = new ArrayList<>();
		String columnNames = "ScreenerID, Location, Screening Date, Type, Age, Age Modifier, Gender, Birth Date";
		String[][] data = null;
		String record = "";
		String query = "select suspected_by as ScreenerID, treatment_centre as Location, date_screened as 'Screening Date',"
					+ "'Non Suspect' as Type, DATEDIFF(date_screened,dob) AS AgeInDays," 
					+ "gender, dob from openmrs_rpt.patient pt JOIN openmrs_rpt.person pr on pr.pid = pt.pid";
		
		try
		{
			data = executeQuery(query, null);
			list.add(columnNames);
			
			if(data != null && data.length > 0)
			{
				for(int i = 0; i < data.length; i++)
				{
					record = "";
					if(Integer.parseInt(data[i][4]) < 31)
					{
						//days
						record = data[i][0] + "," + data[i][1] + "," + data[i][2] + "," + data[i][3] + "," + data[i][4] + "," + "Day(s)" + "," + data[i][5] + "," + data[i][6];
					}
					else
					{
						int ageInMonths = Integer.parseInt(data[i][4])/31;
						if(ageInMonths < 12)
						{
							//months
							record = data[i][0] + "," + data[i][1] + "," + data[i][2] + "," + data[i][3] + "," + ageInMonths + "," + "Month(s)" + "," + data[i][5] + "," + data[i][6];
						}
						else
						{
							//years
							int ageInYears = Integer.parseInt(data[i][4])/365;
							record = data[i][0] + "," + data[i][1] + "," + data[i][2] + "," + data[i][3] + "," + ageInYears + "," + "Year(s)" + "," + data[i][5] + "," + data[i][6];
						}
					}
					if (record.length() > 0)
					    list.add(record);
					record = "";
				}
			}
			
			StringBuilder text = new StringBuilder();
		    for (int i = 0; i < list.size(); i++)
			text.append(list.get(i) + "\r\n");
		    
		    return text.toString();
		    
		}
		catch(Exception e)
		{
			return "";
		}
	}
	
	public String generateCSVfromQuery(String fromDate, String toDate, String suspectedBy, String treatmentCenter, String patientType)
	{
		String dateFilter = "";
		String suspectedByFilter = "";
		String treatmentCenterFilter = "";
		String ageFilter = "";
		String[][] data = null;
		String record = "";
		String separator = ",";
		String sc = "";
		String location = "";
		int nSusp = 0;
		
		String columnNames = "Location, Username, Type, Non Suspects";
		if(!fromDate.equals("") && !toDate.equals(""))
		{
			dateFilter = " date(date_screened) between '"+ fromDate +"' AND '"+ toDate + "'";
		}
		else
		{
			dateFilter = " date(date_screened) between curdate() AND curdate() ";
		}
		
		if(suspectedBy.equals("All"))
		{
			suspectedByFilter = " suspected_by like '%'";
		}
		else 
		{
			suspectedByFilter = " suspected_by like '" + suspectedBy + "%'" ;
		}
		
		if(treatmentCenter.equals("All"))
		{
			treatmentCenterFilter = " treatment_centre like '%'";
		}
		else
		{
			treatmentCenterFilter = " treatment_centre like '"+ treatmentCenter +"%'";
		}
		
		if(patientType.equals("both"))
		{
			ageFilter = " (((Year(date_screened) - Year(dob))*12 + (Month(date_screened) - Month(dob)))/12) >=0 ";
		}
		
		else if(patientType.equals("adult"))
		{
			ageFilter = " (((Year(date_screened) - Year(dob))*12 + (Month(date_screened) - Month(dob)))/12) >15 ";
		}
		else if(patientType.equals("paediatric"))
		{
			ageFilter = " (((Year(date_screened) - Year(dob))*12 + (Month(date_screened) - Month(dob)))/12) <=15 ";
		}
		
		String selectQuery = "select treatment_centre, suspected_by as username, 'Non Suspects' as what, count(*) as total from openmrs_rpt.patient pt "
							 + "JOIN openmrs_rpt.person pr on pr.pid = pt.pid "
							 + " where" + suspectedByFilter + " AND "+ treatmentCenterFilter +" AND "+ dateFilter +" AND " 
							 + ageFilter + " group by suspected_by ";
		
		ArrayList<String> list = new ArrayList<>();
		
		try
		{
			data = executeQuery(selectQuery, null);
			list.add(columnNames);
			
			if(data != null && data.length > 0)
			{
				sc = data[0][0];
				for(int i = 0; i < data.length; i++)
				{
					location = data[i][0];
					String nonSuspectsCount = data[i][3];
					record = "";
					if (!location.equals(sc))
					{
						sc = location;
						record = " , , Total , " + nSusp;
						nSusp = 0;
						if (record.length() > 0)
						    list.add(record);
						record = "";
					}

					nSusp += Integer.parseInt (nonSuspectsCount);
					
					for(int j = 0; j < data[0].length; j++)
					{
						record += data[i][j] + separator;
					}
					if (record.length() > 0)
					    list.add(record.substring(0, record.length() - 1));
					
					if(i == data.length - 1)
					{
						record = " , , Total , " + nSusp;
						nSusp = 0;
						if (record.length() > 0)
						    list.add(record);
						record = "";
					}
				}
			}
			
			StringBuilder text = new StringBuilder();
		    for (int i = 0; i < list.size(); i++)
			text.append(list.get(i) + "\r\n");
		    
		    return text.toString();
		    
		}
		catch(Exception e)
		{
			return "";
		}
	}
	
	
	/**
	 * Execute native DML query to fetch data
	 * 
	 * @param query
	 * @param parameterValues
	 * @return
	 */
	public String[][] executeQuery (String query, String[] parameterValues)
	{
		String[][] result = null;
		try
		{
			if (conn.isClosed ())
			{
				if (!openConnection ())
				{
					return result;
				}
			}
			PreparedStatement statement = conn.prepareStatement (query);
			int count = 1;
			if (parameterValues != null)
			{
				for (String s : parameterValues)
				{
					statement.setString (count++, s);
				}
			}
			ResultSet resultSet = statement.executeQuery ();
			resultSet.last ();
			int rows = resultSet.getRow ();
			resultSet.beforeFirst ();
			int columns = resultSet.getMetaData ().getColumnCount ();
			result = new String[rows][columns];
			int i = 0;
			while (resultSet.next ())
			{
				for (int j = 0; j < columns; j++)
				{
					result[i][j] = resultSet.getString (j + 1);
				}
				i++;
			}
			statement.close ();
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			result = null;
		}
		return result;
	}

	/**
	 * Execute Parameterized native DML query
	 * 
	 * @param query
	 * @param parameterValues
	 * @return
	 */
	public boolean executeUpdate (String query, String[] parameterValues)
	{
		boolean result = true;
		try
		{
			if (conn.isClosed ())
			{
				if (!openConnection ())
				{
					return false;
				}
			}
			PreparedStatement statement = conn.prepareStatement (query);
			int count = 1;
			if (parameterValues != null)
			{
				for (String s : parameterValues)
				{
					statement.setString (count++, s);
				}
			}
			statement.executeUpdate ();
			statement.close ();
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
			result = false;
		}
		return result;
	}

	/**
	 * Execute native DML query to fetch data
	 * 
	 * @param query
	 * @param parameterValues
	 * @return
	 */
	public boolean execute (String query)
	{
		boolean result = false;
		try
		{
			if (conn.isClosed ())
			{
				if (!openConnection ())
				{
					return result;
				}
			}
			Statement statement = conn.createStatement ();
			result = statement.execute (query);
			statement.close ();
		}
		catch (SQLException e)
		{
			e.printStackTrace ();
		}
		return result;
	}
	
	public String authenticate(String username, String password)
	{
		String result = "";
		try
		{
			Context.openSession();
			Context.authenticate(username, password);
			result = "SUCCESS";
		}
		catch (ContextAuthenticationException e)
		{
			//e.printStackTrace ();
			//response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.AUTHENTICATION_ERROR) + "\n" + e.getMessage ()).toString ();
			result = "FAIL";
			return result;
		}
		return result;
	}
	
	public String[] getLocations(String username, String password)
	{
		String[] locations ;
		
		try
		{
			
			Context.openSession();
			Context.authenticate(username, password);
			List<Location> locationList = Context.getLocationService ().getAllLocations();		
			Object locationArray[] = locationList.toArray();
			
			locations = new String[locationArray.length + 1];
			locations[0] = "All";
			for(int i=0; i<locationArray.length; i++)
			{
				locations[i+1] = locationArray[i].toString();
			}
		}
		catch (ContextAuthenticationException e)
		{
			//e.printStackTrace ();
			//response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.AUTHENTICATION_ERROR) + "\n" + e.getMessage ()).toString ();
			return new String[0];
		}
		
		return locations;
	}
	
	public String[] getUsers(String username, String password)
	{
		String[] users;
		
		try
		{
			Context.openSession();
			Context.authenticate(username, password);
			List<User> userList = Context.getUserService().getAllUsers();
			Object userArray[] = userList.toArray();
			users = new String[userArray.length + 1];
			users[0] = "All";
			for(int i=0; i<userArray.length; i++)
			{
				users[i+1] = userArray[i].toString();
			}
		}
		catch (ContextAuthenticationException e)
		{
			//e.printStackTrace ();
			//response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.AUTHENTICATION_ERROR) + "\n" + e.getMessage ()).toString ();
			return new String[0];
		}
		return users;
	}

}
