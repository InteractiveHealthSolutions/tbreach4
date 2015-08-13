/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * This class incorporates Open MRS API services. The content type used is JSON for Requests and Responses
 * 
 */

package com.ihsresearch.tbr4web.server;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
import com.ihsresearch.tbr4web.server.MobileService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSearchResult;
import org.openmrs.ConceptWord;
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
import com.ihsresearch.tbr4web.server.util.DateTimeUtil;
import com.ihsresearch.tbr4web.server.util.JsonUtil;
import com.ihsresearch.tbr4web.shared.App;
import com.ihsresearch.tbr4web.shared.CustomMessage;
import com.ihsresearch.tbr4web.shared.ErrorType;
import com.ihsresearch.tbr4web.shared.FormType;

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
	//static final String				propFilePath	=  "/usr/share/tomcat6/.OpenMRS/openmrs-runtime.properties";
	static final String				propFilePath	= "C:\\Application Data\\OpenMRS\\openmrs-runtime.properties";
	private static File				propsFile;
	private static Properties		props;
	private static Connection		conn;
	private static String			url, username, password;
	private static MobileService	service			= new MobileService ();
	
	// Singleton. Called only once to fire up Open MRS
	private MobileService ()
	{
		try
		{
			propsFile = new File (propFilePath);
			props = new Properties ();
			OpenmrsUtil.loadProperties (props, propsFile);
			url = (String) props.get ("connection.url");
			username = (String) props.get ("connection.username");
			password = (String) props.get ("connection.password");
			openConnection ();
			Context.startup (url, username, password, props);
			
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
			String driver = "com.mysql.jdbc.Driver";
			try
			{
				Class.forName (driver).newInstance ();
			}
			catch (InstantiationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace ();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				System.out.println ("Error: Driver not found");
			}
			catch (ClassNotFoundException e)
			{
				// TODO Auto-generated catch block
				System.out.println ("Error: Driver not found");
			}
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
			String s = App.appVersion;
			if (!App.validateVersion (appVer))
			{
				return JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.VERSION_MISMATCH_ERROR)).toString ();
			}
			String formType = jsonObject.getString ("form_name");
			System.out.println (formType);
			if (formType.equals (FormType.GET_USER))
				response = getUser (formType, jsonObject);
			else if (formType.equals (FormType.GET_SPUTUM_SUBMISSION_STATUS))
				response = getSputumSubmissionStatus (formType, jsonObject);
			else if (formType.equals (FormType.GET_LOCATION) || formType.equals (FormType.GET_FACILITY))
				response = getLocation (formType, jsonObject);
			else if (formType.equals (FormType.GET_DISTRICT))
				response = getDistrict (formType, jsonObject);
			else if (formType.equals (FormType.GET_SCREENING_STRATEGIES))
				response = getScreeningStrategies (formType, jsonObject);
			else if (formType.equals (FormType.GET_STRATEGY))
				response = getStrategy (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT))
				response = getPatient (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT_NAME))
				response = getPatientName (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT_NAME_FROM_TESTID))
				response = getPatientNameFromTestId (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT_DETAIL))
				response = getPatientDetail (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT_REPORT))
				response = getPatientReport (formType, jsonObject);
			else if (formType.equals (FormType.GET_PATIENT_FROM_TEST_ID))
				response = getPatientIdFromTestId (formType, jsonObject);
			else if (formType.equals(FormType.GET_LOCATION_SETUP))
				response = getScreenerLocationSetupFromUsername(formType, jsonObject);
			else if (formType.equals (FormType.NON_SUSPECT))
				response = doNonSuspectScreening (formType, jsonObject);
			else if (formType.equals (FormType.SCREENING) || formType.equals (FormType.NON_PULMONARY) || formType.equals (FormType.PAEDIATRIC_SCREENING) || formType.equals (FormType.QUICK_SCREENING))
				response = doScreening (formType, jsonObject);
			else if (formType.equals (FormType.CUSTOMER_INFO))
				response = doCustomerInfo (formType, jsonObject);
			else if (formType.equals (FormType.FEEDBACK))
				response = doFeedback (formType, jsonObject);
			else if (formType.equals (FormType.TEST_INDICATION) || formType.equals (FormType.SPUTUM_SUBMISSION) 
					|| formType.equals (FormType.SPUTUM_RESULT) || formType.equals(FormType.TREATMENT_INITIATION)  
					|| formType.equals(FormType.TREATMENT_FOLLOWUP)  || formType.equals(FormType.TREATMENT_OUTCOME))
				response = doGenericForm (formType, jsonObject);
			else if (formType.equals("DATA_UPLOAD"))
			    response = doDataUpload();
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
			response = JsonUtil.getJsonError (CustomMessage.getErrorMessage (ErrorType.AUTHENTICATION_ERROR)).toString();
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

	private String getScreenerLocationSetupFromUsername (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String username = values.getString ("username");
			User user = Context.getUserService().getUserByUsername(username);
			Person person = user.getPerson();
			PersonAttribute locationAttribute = person.getAttribute(13);
			String location = "";
			if(locationAttribute != null)
				location = locationAttribute.toString();
			PersonAttribute screeningTypeAttribute = person.getAttribute(14);
			String screeningType = "";
			if(screeningTypeAttribute != null)
				screeningType = screeningTypeAttribute.toString();
			
			JSONObject locationObj = new JSONObject ();
			locationObj.put ("result", "SUCCESS");
			locationObj.put ("facility", location);
			locationObj.put ("screener_type", screeningType);
			
			/*String selectQuery = "SELECT * from openmrs_rpt.daily_feedback_message where screener_id = '"+username+"' and sent = '0'";
			System.out.println(selectQuery);
		    String[][] data = executeQuery (selectQuery, null);
		    
		    if(data != null || data.length != 0){
			    locationObj.put("overall_message", data[0][1]);
			    locationObj.put("message", data[0][2]);
		    }
		    else{
		    	locationObj.put("overall_message", "");
			    locationObj.put("message", "");
		    }
			
		    String updateQuery = "Update openmrs_rpt.daily_feedback_message" +
					 				" set sent = '1' " +
					 				" where screener_id = '"+username+"'" ;

		    execute(updateQuery);*/
		    
			json = locationObj.toString ();
			
		}	
		
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}
	
	
	private String getSputumSubmissionStatus (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String pid = values.getString ("p_id");
			
			List<Patient> patients = new ArrayList<Patient> ();
			patients = Context.getPatientService ().getPatients (pid);
			if(patients == null){
				return json;
			}
			Patient patient = patients.get(0);
			
			Collection<EncounterType> sputumResultEncounterType = Context.getEncounterService ().findEncounterTypes ("Sputum Result");
			
			/*
			 * 1-Patient who, 2-Location loc, 3-Date fromDate, 4-Date toDate,
			 * 5-Collection<Form> enteredViaForms, 6-Collection<EncounterType>
			 * encounterTypes, 7-Collection<Provider> providers,
			 * 8-Collection<VisitType> visitTypes, 9-Collection<Visit> visits,
			 * 10-boolean includeVoided)
			 */
			List<Encounter> sputumResultEncounters = Context.getEncounterService ().getEncounters (patient, null, null, null, null, sputumResultEncounterType, null, null, null, false);
			
			if(sputumResultEncounters == null || sputumResultEncounters.size() == 0){
				
				Collection<EncounterType> sputumSubmissionEncounterType = Context.getEncounterService ().findEncounterTypes ("Sputum Submission");
				List<Encounter> sputumSubmissionEncounters = Context.getEncounterService ().getEncounters (patient, null, null, null, null, sputumSubmissionEncounterType, null, null, null, false);
				
				if(sputumSubmissionEncounters == null || sputumSubmissionEncounters.size() == 0)
					return json;	
				else{
					
					List<Concept> concepts = Context.getConceptService ().getConceptsByName ("Sputum Sample");
					List<Obs> observations = Context.getObsService ().getObservations (null, sputumSubmissionEncounters, concepts, null, null, null, null, null, null, null, null, false);
					
					for (Obs obs : observations)
					{
						Concept sputumSample = obs.getValueCoded();
						String ss = sputumSample.getName().toString();
						if (ss.equals ("Accept"))
						{
							JSONObject jsonObj = new JSONObject ();
							jsonObj.put ("status", "YES");
							json = jsonObj.toString ();
							return json;
						}
					}
					
					JSONObject jsonObj = new JSONObject ();
					jsonObj.put ("status", "No");
					json = jsonObj.toString ();
					return json;
				}
					
			}
			else{
				return json;
			}
			
		}	
		
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}
	
	
	private String getPatientIdFromTestId (String formType, JSONObject values)
	{

		String json = null;
		try
		{
			String id = values.getString ("test_id");
			String result = values.getString ("result");

			String patientId = null;
			Collection<EncounterType> encounterType = Context.getEncounterService ().findEncounterTypes ("Sputum Submission");

			/*
			 * 1-Patient who, 2-Location loc, 3-Date fromDate, 4-Date toDate,
			 * 5-Collection<Form> enteredViaForms, 6-Collection<EncounterType>
			 * encounterTypes, 7-Collection<Provider> providers,
			 * 8-Collection<VisitType> visitTypes, 9-Collection<Visit> visits,
			 * 10-boolean includeVoided)
			 */

			// 1 2 3 4 5 6 7 8 9 10
			List<Encounter> encounters = Context.getEncounterService ().getEncounters (null, null, null, null, null, encounterType, null, null, null, false);
			List<Concept> concepts = Context.getConceptService ().getConceptsByName ("Lab Test Id");
			/*
			 * 1 - whom 2 - encounters, 3 - questions, 4 - answers, 5 -
			 * personTypes, 6 - locations, 7 - sort, 8 - mostRecentN, 9 -
			 * obsGroupId, 10 - fromDate, 11 - toDate, 12 - includeVoidedObs
			 */
			// 1 2 3 4 5 6 7 8 9 10 11 12
			List<Obs> observations = Context.getObsService ().getObservations (null, encounters, concepts, null, null, null, null, null, null, null, null, false);
			for (Obs obs : observations)
			{
				String testId = obs.getValueText ();
				if (testId.equals (id))
				{
					patientId = obs.getPatient ().getPatientIdentifier ().toString ();
				}
			}

			if (patientId != null)
			{

				if (result.equals ("Y"))
				{
					List<Patient> patients = Context.getPatientService ().getPatients (patientId);
					encounterType = null;
					encounterType = Context.getEncounterService ().findEncounterTypes ("Sputum Result");
					encounters = null;
					encounters = Context.getEncounterService ().getEncounters (patients.get (0), null, null, null, null, encounterType, null, null, null, false);
					concepts = null;
					if (encounters.size () != 0)
					{
						concepts = Context.getConceptService ().getConceptsByName ("Lab Test Id");
						observations = null;
						observations = Context.getObsService ().getObservations (null, encounters, concepts, null, null, null, null, null, null, null, null, false);
						if (observations != null)
						{
							for (Obs obs : observations)
							{
								String testId = obs.getValueText ();
								if (testId.equals (id))
								{
									return json;
								}
							}
						}
					}
				}

				JSONObject jsonObj = new JSONObject ();
				jsonObj.put ("pid", patientId);
				json = jsonObj.toString ();
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	private String getPatientName (String formType, JSONObject values)
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
					json.put ("first_name", p.getPersonName ().getGivenName ());
					json.put ("last_name", p.getPersonName ().getFamilyName ());
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

	private String getPatientNameFromTestId (String formType, JSONObject values){
		
		JSONObject json = new JSONObject ();
		try
		{
			String id = values.getString ("test_id");
			String result = values.getString ("result");
			List<Patient> patients = new ArrayList<Patient> ();

			String patientId = null;
			Collection<EncounterType> encounterType = Context.getEncounterService ().findEncounterTypes ("Sputum Submission");

			/*
			 * 1-Patient who, 2-Location loc, 3-Date fromDate, 4-Date toDate,
			 * 5-Collection<Form> enteredViaForms, 6-Collection<EncounterType>
			 * encounterTypes, 7-Collection<Provider> providers,
			 * 8-Collection<VisitType> visitTypes, 9-Collection<Visit> visits,
			 * 10-boolean includeVoided)
			 */

			// 1 2 3 4 5 6 7 8 9 10
			List<Encounter> encounters = Context.getEncounterService ().getEncounters (null, null, null, null, null, encounterType, null, null, null, false);
			List<Concept> concepts = Context.getConceptService ().getConceptsByName ("Lab Test Id");
			/*
			 * 1 - whom 2 - encounters, 3 - questions, 4 - answers, 5 -
			 * personTypes, 6 - locations, 7 - sort, 8 - mostRecentN, 9 -
			 * obsGroupId, 10 - fromDate, 11 - toDate, 12 - includeVoidedObs
			 */
			// 1 2 3 4 5 6 7 8 9 10 11 12
			List<Obs> observations = Context.getObsService ().getObservations (null, encounters, concepts, null, null, null, null, null, null, null, null, false);
			for (Obs obs : observations)
			{
				String testId = obs.getValueText ();
				if (testId.equals (id))
				{
					patientId = obs.getPatient ().getPatientIdentifier ().toString ();
				}
			}
			
			if(patientId!=null){
				
				patients = Context.getPatientService ().getPatients (patientId);
				if (patients != null)
				{
					Patient p = patients.get (0);
					json.put ("first_name", p.getPersonName ().getGivenName ());
					json.put ("last_name", p.getPersonName ().getFamilyName ());
				}
				
			}
			
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json.toString ();
		
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
			userObj.put ("result", "SUCCESS");
			userObj.put ("id", user.getUserId ());
			userObj.put ("name", user.getUsername ());
			userObj.put ("sname", user.getGivenName());
			
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

	public String getDistrict (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			String locationName = values.getString ("location_name");
			List<Location> locations = Context.getLocationService ().getLocations (locationName);
			Location location = locations.get (0);
			int size = locations.size ();

			JSONObject locationObj = new JSONObject ();
			locationObj.put ("id", location.getLocationId ());
			locationObj.put ("name", location.getName ());

			locationObj.put ("size", size);

			for (int i = 1; i < size; i++)
			{
				location = locations.get (i);
				locationObj.put ("id" + i, location.getId ());
				locationObj.put ("name" + i, location.getName ());
			}

			json = locationObj.toString ();
		}
		catch (JSONException e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	public String[][] getSmsData (String to, String from, String username, String password)
	{
		String[][] data = null;
		try
		{
			Context.openSession ();
			Context.authenticate (username, password);
			String selectQuery = "SELECT Date,Name,Facility,District,originator,recieveDate,text FROM openmrs_rpt.data_screener ,"
					+ "(SELECT originator, recieveDate, text, DATE_FORMAT(recieveDate, '%y-%m-%d') as Date FROM (Select * from smstarseel.inboundmessage where recieveDate >= '" + from
					+ "' and recieveDate <= '" + to + " 23:59:59.999' order by recieveDate desc) as temp group by Date, originator order by recieveDate desc) as screening_summary "
					+ "where data_screener.PhoneNumber = screening_summary.originator order by recieveDate desc;";
			data = executeQuery (selectQuery, null);
		}
		catch(Exception e){
			
		}
		finally
		{
			Context.closeSession ();
		}
		return data;
	}

	/**
	 * Prepares and returns screening numbers by users
	 * 
	 * @param granularity
	 *            pass day, week or month to get the level of aggregate
	 * @return
	 */
	public String[][] getScreeningData (String granularity)
	{

		String dateFilter = "year(pt.date_created) = year(curdate()) ";
		if (granularity.equals ("month"))
		{
			dateFilter += "and month(pt.date_created) = month(curdate()) ";
		}
		else if (granularity.equals ("week"))
		{
			dateFilter += "and month(pt.date_created) = month(curdate()) and week(pt.date_created) = week(curdate()) ";
		}
		else if (granularity.equals ("day"))
		{
			dateFilter += "and month(pt.date_created) = month(curdate()) and day(pt.date_created) = day(curdate()) ";
		}

		String selectQuery = "select substring(u.username, 1, 2), u.username, count(pt.patient_id), count(CASE WHEN p_att.value = 'Suspect' then 1 else null end), count(CASE WHEN p_att.value = 'Non-Suspect' then 1 else null end)"
				+ " from  openmrs.users as u , openmrs.patient as pt , openmrs.person_attribute as p_att"
				+ " where pt.creator = u.user_id and pt.patient_id = p_att.person_id and p_att.person_attribute_type_id = 12 and " + dateFilter + " group by u.username;";

		String[][] data = executeQuery (selectQuery, null);
		return data;
	}

	public String getScreeningStrategies (String formType, JSONObject values)
	{
		String json = null;
		try
		{
			Concept screeningStrategyConcept = Context.getConceptService ().getConceptByName ("Screening Strategy");
			List<ConceptWord> concepts = Context.getConceptService ().getConceptAnswers ("", Context.getLocale (), screeningStrategyConcept);
			JSONObject conceptObj = new JSONObject ();
			int size = concepts.size ();
			conceptObj.put ("size", size);
			int counter = 0;
			for (ConceptWord c : concepts)
			{

				Concept con = c.getConcept ();
				conceptObj.put ("name" + counter, con.getDescription ());
				conceptObj.put ("id" + counter, con.getId ());
				counter++;
			}
			json = conceptObj.toString ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		return json;
	}

	public String getStrategy (String formType, JSONObject values)
	{

		String json = null;
		try
		{	
			String strategyName = values.getString ("location_name");
			Concept screeningStrategyConcept = Context.getConceptService ().getConceptByName (strategyName);
			
			JSONObject conceptObj = new JSONObject ();
			
			if(screeningStrategyConcept == null){
				conceptObj.put ("status", "FAIL");
			}
			else{
				conceptObj.put ("status", "OK");
				conceptObj.put ("name", screeningStrategyConcept.getDescription ());
				conceptObj.put ("id", screeningStrategyConcept.getId ());
			}

			json = conceptObj.toString ();
		}
		catch (Exception e)
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

	public String getPatientReport (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		try
		{
			List<Patient> patients = new ArrayList<Patient> ();
			String id = values.getString ("id");
			
			if (!id.equals (""))
			{
				patients = Context.getPatientService ().getPatients (id);
				if (patients != null)
				{
					Patient p = patients.get (0);
					json.put ("first_name", p.getPersonName ().getGivenName ());
					json.put ("last_name", p.getPersonName ().getFamilyName ());
					json.put ("gender", p.getGender ());
					json.put ("age", p.getAge ());
					json.put ("dob", p.getBirthdate ().toString ());
					if (p.getPersonAddress ().getAddress1 () != null)
						json.put ("address", p.getPersonAddress ().getAddress1 ());
					else
						json.put ("address", "");
					if (p.getPersonAddress ().getAddress2 () != null)
						json.put ("colony", p.getPersonAddress ().getAddress2 ());
					else
						json.put ("colony", "");
					if (p.getPersonAddress ().getAddress3 () != null)
						json.put ("town", p.getPersonAddress ().getAddress3 ());
					else
						json.put ("town", "");
					if (p.getPersonAddress ().getAddress4 () != null)
						json.put ("landmark", p.getPersonAddress ().getAddress4 ());
					else
						json.put ("landmark", "");
					if (p.getPersonAddress ().getCityVillage () != null)
						json.put ("city", p.getPersonAddress ().getCityVillage ());
					else
						json.put ("city", "");
					if (p.getPersonAddress ().getCountry () != null)
						json.put ("country", p.getPersonAddress ().getCountry ());
					else
						json.put ("country", "");
					json.put ("pid", p.getPatientIdentifier ());
					PersonAttribute pa = p.getAttribute ("Primary Phone");
					if (pa != null)
						json.put ("phone1", pa.getValue ());
					else
						json.put ("phone1", "");

					Concept contactTbConcept = Context.getConceptService ().getConceptByName ("Contact with TB");
					List<Obs> contactTbObs = new LinkedList<Obs> ();
					contactTbObs = Context.getObsService ().getObservationsByPersonAndConcept (p, contactTbConcept);
					if (contactTbObs != null)
					{
						
						int size = contactTbObs.size ();
						if(size != 0){
							Obs contactTbO = contactTbObs.get (0);
							String contactTbValue = contactTbO.getValueCoded ().getName ().getName ();
							json.put ("contact_tb", contactTbValue);
						}
						else
							json.put ("contact_tb", "");
					}
					else
						json.put ("contact_tb", "");

					Concept diabetesConcept = Context.getConceptService ().getConceptByName ("Diabetes");
					List<Obs> diabetesObs = new LinkedList<Obs> ();
					diabetesObs = Context.getObsService ().getObservationsByPersonAndConcept (p, diabetesConcept);
					if (diabetesObs != null)
					{
						int size = diabetesObs.size ();
						if(size != 0){
							Obs diabetesO = diabetesObs.get (0);
							String diabetesValue = diabetesO.getValueCoded ().getName ().getName ();
							json.put ("diabetes", diabetesValue);
						}
						else
							json.put ("diabetes", "");
					}
					else
						json.put ("diabetes", "");

					Concept labTestIdConcept = Context.getConceptService ().getConceptByName ("Lab Test Id");
					List<Obs> labTestIdObs = new LinkedList<Obs> ();
					labTestIdObs = Context.getObsService ().getObservationsByPersonAndConcept (p, labTestIdConcept);
					
					if(labTestIdObs != null){
						int size = labTestIdObs.size ();
						if (size != 0)
						{
							Obs labTestIdO = labTestIdObs.get (0);
							String labTestIdValue = labTestIdO.getValueText ();
							if(labTestIdValue != null)
								json.put ("test_lab_id", labTestIdValue);
							else
								json.put ("test_lab_id", "");
						}
						else
							json.put ("test_lab_id", "");
					}
					else
						json.put ("test_lab_id", "");
					
					
					Concept lastHivResultConcept = Context.getConceptService ().getConceptByName ("Last HIV result");
					List<Obs> lastHivResultObs = new LinkedList<Obs> ();
					lastHivResultObs = Context.getObsService ().getObservationsByPersonAndConcept (p, lastHivResultConcept);
					if (lastHivResultObs != null)
					{
						
						int size = lastHivResultObs.size ();
						if(size != 0){
							Obs lastHivO = lastHivResultObs.get (0);
							String lastHivValue = lastHivO.getValueCoded ().getName ().getName ();
							json.put ("last_hiv", lastHivValue);
						}
						else
							json.put ("last_hiv", "");
					}
					else
						json.put ("last_hiv", "");

					json.put ("date_sputum_submission", "");
					List<Encounter> sputumSubmissionEncountersByPatient = Context.getEncounterService ().getEncountersByPatient (p);
					for (Encounter e : sputumSubmissionEncountersByPatient)
					{
						if (e.getEncounterType ().getName ().equals ("Sputum Submission"))
						{
							json.put ("date_sputum_submission", DateTimeUtil.getSQLDate (e.getEncounterDatetime ()));
						}
					}
					
					json.put ("date_sputum_result", "");
					List<Encounter> sputumResultEncountersByPatient = Context.getEncounterService ().getEncountersByPatient (p);
					for (Encounter e : sputumResultEncountersByPatient)
					{
						if (e.getEncounterType ().getName ().equals ("Sputum Result"))
						{
							json.put ("date_sputum_result", DateTimeUtil.getSQLDate (e.getEncounterDatetime ()));
						}
					}
						
					
					json.put ("date_treatment_initiation", "");
					Concept genexpertResultConcept = Context.getConceptService ().getConceptByName ("GeneXpert Result");
					List<Obs> genexpertResultObs = new LinkedList<Obs> ();
					genexpertResultObs = Context.getObsService ().getObservationsByPersonAndConcept (p, genexpertResultConcept);
					if (genexpertResultObs != null)
					{
						
						int size = genexpertResultObs.size ();
						if(size != 0){
							Obs genexpertO = genexpertResultObs.get (0);
							String genexpertValue = genexpertO.getValueCoded ().getName ().getName ();
							json.put ("genexpert_result", genexpertValue);
							
							if(genexpertValue.equals("MTB Positive")){
								/*List<Encounter> treatmentInitiationEncountersByPatient = Context.getEncounterService ().getEncountersByPatient (p);
								for (Encounter e : treatmentInitiationEncountersByPatient)
								{
									if (e.getEncounterType ().getName ().equals ("Treatment Initiation"))
									{
										json.put ("date_treatment_initiation", DateTimeUtil.getSQLDate (e.getEncounterDatetime ()));
									}
								}*/
								
								Concept treatmentInitiationDateConcept = Context.getConceptService ().getConceptByName ("Treatment Initiation Date");
								List<Obs> treatmentInitiationObs = new LinkedList<Obs> ();
								treatmentInitiationObs = Context.getObsService ().getObservationsByPersonAndConcept (p, treatmentInitiationDateConcept);
								if (treatmentInitiationObs != null)
								{
									size = treatmentInitiationObs.size ();
									if(size != 0){
										Obs treatmentInititionDateO = treatmentInitiationObs.get (0);
										String value = DateTimeUtil.getSQLDate(treatmentInititionDateO.getValueDate());
										json.put ("date_treatment_initiation", value);
									}
									else
										json.put ("date_treatment_initiation", "");
								}
								else
									json.put ("date_treatment_initiation", "");
								
							}
						}
						else
							json.put ("genexpert_result", "");
					}
					else
						json.put ("genexpert_result", "");
					
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
		try
		{
			int age = Integer.parseInt (values.get ("age").toString ());
			String gender = values.getString ("gender").toString ();
			String weight = values.getString ("weight").toString ();
			String height = values.getString ("height").toString ();
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String formDate = values.getString ("formdate").toString ();
			String uuid = UUID.randomUUID ().toString ();
			Date dob = new Date ();
			dob.setYear (dob.getYear () - age);
			StringBuffer query = new StringBuffer ();
			query.append ("insert into openmrs_rpt.person (pid, first_name, last_name, gender, dob) values (?, ?, ?, ?, ?)");
			String[] params = {uuid, "", "", gender, DateTimeUtil.getSQLDate (dob)};
			if (executeUpdate (query.toString (), params))
			{
				query = new StringBuffer ();
				query.append ("insert into openmrs_rpt.patient (pid, patient_id, date_screened, suspected_by, treatment_centre, weight, height, patient_status) values (?, ?, ?, ?, ?, ?, ?, ?)");
				params = new String[] {uuid, uuid, formDate, username, location, weight, height, "SCREENED"};
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

	/**
	 * Save Adult, Paediatric, Non Pulmonary or any other Screening form
	 * 
	 * @param formType
	 * @param values
	 * @return
	 */

	public String doScreening (String formType, JSONObject values)
	{
		JSONObject json = new JSONObject ();
		String error = "";
		try
		{
			int age = Integer.parseInt (values.get ("age").toString ());
			String gender = values.getString ("gender").toString ();
			String location = values.getString ("location").toString ();
			String username = values.getString ("username").toString ();
			String patientId = values.getString ("patient_id");

			if (patientId.equals (""))
			{
				long date = System.currentTimeMillis ();
				patientId = String.valueOf (date);
				patientId = getIdWithCheckDigit (patientId);
			}

			String givenName = values.getString ("given_name");
			String familyName = values.getString ("family_name");
			String encounterType = values.getString ("encounter_type");
			String dob = values.getString ("dob");
			Date dateOfBirth = DateTimeUtil.getDateFromString (dob, DateTimeUtil.SQL_DATE);

			String address1 = values.getString ("address1");
			String colony = values.getString ("colony");
			String town = values.getString ("town");
			String landmark = values.getString ("landmark");
			//String city = values.getString ("city");
			String country = values.getString ("country");

			String formDate = values.getString ("form_date");
			Date encounterDatetime = DateTimeUtil.getDateFromString (formDate, DateTimeUtil.SQL_DATE);
			String encounterLocation = values.getString ("encounter_location");
			String provider = values.getString ("provider");

			JSONArray attributes = new JSONArray (values.getString ("attributes"));
			JSONArray obs = new JSONArray (values.getString ("obs"));
			// Get Creator
			User creatorObj = Context.getUserService ().getUserByUsername (username);
			// Get Identifier type
			List<PatientIdentifierType> allIdTypes = Context.getPatientService ().getAllPatientIdentifierTypes ();
			PatientIdentifierType patientIdTypeObj = allIdTypes.get (1);
			// Get Location
			Location locationObj = Context.getLocationService ().getLocation (location);
			// Get Encounter type
			EncounterType encounterTypeObj = Context.getEncounterService ().getEncounterType (encounterType);
			// Create Patient
			{
				// Create Person object
				Person person = new Person ();
				// Date dob = new Date ();
				// dob.setYear (dob.getYear () - age);
				person.setBirthdate (dateOfBirth);
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
				// Add address details
				{
					PersonAddress address = new PersonAddress ();
					address.setAddress1 (address1);
					address.setAddress2 (colony);
					address.setAddress3 (town);
					address.setAddress4 (landmark);
					//address.setCityVillage (city);
					address.setCountry (country);
					address.setCreator (creatorObj);
					address.setDateCreated (new Date ());
					person.addAddress (address);
				}
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
				patientId = "Patient Id: " + patientId;
				json.put("pid", patientId);
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
		catch (Exception e)
		{
			e.printStackTrace ();
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
			String stateProvince = values.getString ("stateProvince");
			String address4 = values.getString ("address4");
			String countyDistrict = values.getString ("countyDistrict");
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
				address.setStateProvince (stateProvince);
				address.setAddress4 (address4);
				address.setCountyDistrict (countyDistrict);
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
			
			String check = checkForPrerequisite(patientId,encounterType);
			
			if(!check.equals("")){
				json.put ("result", check);
				return json.toString ();
			}
				
			
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

	
	public String checkForPrerequisite(String patientId, String encounterType){
		
		List<Patient> patients = new ArrayList<Patient> ();
		patients = Context.getPatientService ().getPatients (patientId);
		if(patients == null){
			return CustomMessage.getErrorMessage(ErrorType.PATIENT_NOT_FOUND);
		}
		Patient patient = patients.get(0);
		
		if(encounterType.equals("Sputum Result")){
			
			// Get 'Sputum Result' Encounter Type 
			Collection<EncounterType> sputumResultEncounterType = Context.getEncounterService ().findEncounterTypes ("Sputum Result");
			// Get all Encounters from encounter type 'Sputum Result' for patient id
			List<Encounter> sputumResultEncounters = Context.getEncounterService ().getEncounters (patient, null, null, null, null, sputumResultEncounterType, null, null, null, false);
			
			
			// Get 'Sputum Submission' Encounter Type
			Collection<EncounterType> sputumSubmissionEncounterType = Context.getEncounterService ().findEncounterTypes ("Sputum Submission");
			// Get all Encounters from encounter type 'Sputum submission' for patient id
			List<Encounter> sputumSubmissionEncounters = Context.getEncounterService ().getEncounters (patient, null, null, null, null, sputumSubmissionEncounterType, null, null, null, false);
			
			
			//If no encounter found check if 'Sputum Submission' form is submitted with 'Sputum Sample' as 'Accept' 
			if(sputumResultEncounters == null || sputumResultEncounters.size() == 0){	
				
				// If Null return false - Prerequisite fails
				if(sputumSubmissionEncounters == null || sputumSubmissionEncounters.size() == 0)
					return CustomMessage.getErrorMessage(ErrorType.SPUTUM_SUBMISSION_NOT_FOUND);	
				else{
					
					// Get 'Sputum Sample'
					List<Concept> concepts = Context.getConceptService ().getConceptsByName ("Sputum Sample");
					// Get All observations for encounter type and concept
					List<Obs> observations = Context.getObsService ().getObservations (null, sputumSubmissionEncounters, concepts, null, null, null, null, null, null, null, null, false);
					
					for (Obs obs : observations)
					{
						Concept sputumSample = obs.getValueCoded();
						String ss = sputumSample.getName().toString();
						if (ss.equals ("Accept"))   // if concept value is 'Accept' - Prerequisite Pass
						{
							return "";
						}
					}
					
					return CustomMessage.getErrorMessage(ErrorType.NO_ACCEPTED_SPUTUM_SUBMISSION_FOUND);  // Prerequisite fails
				}
					
			}
			else{
				
				  
				if(sputumResultEncounters.size() == sputumSubmissionEncounters.size())  // Prerequisite fails
					return CustomMessage.getErrorMessage(ErrorType.SPUTUM_SUBMISSION_NOT_FOUND);
				else {
					
					// Get 'Sputum Sample' Concept
					List<Concept> concepts = Context.getConceptService ().getConceptsByName ("Sputum Sample");
					// Get All observations for encounter type and concept
					List<Obs> observations = Context.getObsService ().getObservations (null, sputumSubmissionEncounters, concepts, null, null, null, null, null, null, null, null, false);
					
					int count = 0; 
					for (Obs obs : observations)
					{
						Concept sputumSample = obs.getValueCoded();
						String ss = sputumSample.getName().toString();
						if (ss.equals ("Accept"))   // if concept value is 'Accept'
						{
							count++;  // no of 'sputumSample' as accept
						}
					}
					
					if (count > sputumResultEncounters.size()) //if count is greater than SputumResult encounter size
						return ""; // Prerequisite Pass
					else 
						return CustomMessage.getErrorMessage(ErrorType.NO_ACCEPTED_SPUTUM_SUBMISSION_FOUND); // Prerequisite fails
				}
			}
			
		}
		
		else if (encounterType.equals("Treatment Initiation")){
			
			// Get 'Treatment Initiation' Encounter Type 
			Collection<EncounterType> treatmentInitiationEncounterType = Context.getEncounterService ().findEncounterTypes ("Treatment Initiation");
			// Get all Encounters from encounter type 'Treatment Initiation' for patient id
			List<Encounter> treatmentInitiationEncounters = Context.getEncounterService ().getEncounters (patient, null, null, null, null, treatmentInitiationEncounterType, null, null, null, false);
			
			// if encounter in null
			if(treatmentInitiationEncounters == null || treatmentInitiationEncounters.size() == 0){
				// Get 'Sputum Result' Encounter Type 
				Collection<EncounterType> sputumResultEncounterType = Context.getEncounterService ().findEncounterTypes ("Sputum Result");
				// Get all Encounters from encounter type 'Sputum Result' for patient id
				List<Encounter> sputumResultEncounters = Context.getEncounterService ().getEncounters (patient, null, null, null, null, sputumResultEncounterType, null, null, null, false);
				
				// if encounter is null
				if(sputumResultEncounters == null || sputumResultEncounters.size() == 0){
					return CustomMessage.getErrorMessage(ErrorType.GENEXPERT_RESULT_NOT_FOUND);   // Prerequisite fails
				}
				else{
					// Get 'GeneXpert Result' Concept
					List<Concept> concepts = Context.getConceptService ().getConceptsByName ("GeneXpert Result");
					// Get All observations for encounter type and concept
					List<Obs> observations = Context.getObsService ().getObservations (null, sputumResultEncounters, concepts, null, null, null, null, null, null, null, null, false);
					
					for (Obs obs : observations)
					{
						Concept genexpertResult = obs.getValueCoded();
						String ss = genexpertResult.getName().toString();
						if (ss.equals ("MTB Positive")) // if value is MTB Positive
						{
							return ""; // Prerequisite Pass
						}
					}
					return CustomMessage.getErrorMessage(ErrorType.POSITIVE_GENEXPERT_RESULT_NOT_FOUND); // Prerequisite fails
				}
			}
			else{ // if encounter is not null
				
				// Get 'Treatment Initiated' Concept
				List<Concept> concepts = Context.getConceptService ().getConceptsByName ("Treatment Initiated");
				// Get All observations for encounter type and concept
				List<Obs> observations = Context.getObsService ().getObservations (null, treatmentInitiationEncounters, concepts, null, null, null, null, null, null, null, null, false);
				
				for (Obs obs : observations)
				{
					Concept treatmentInitiated = obs.getValueCoded();
					String ss = treatmentInitiated.getName().toString();
					if (ss.equals ("Yes"))  // if value is Yes
					{
						return CustomMessage.getErrorMessage(ErrorType.TREATMENT_ALREADY_INITIATED); // Prerequisite fails
					}
				}
				return ""; // Prerequisite Pass

			}
			
		}
		
		else if(encounterType.equals("Treatment Followup")){
			
			// Get 'Treatment Initiation' Encounter Type 
			Collection<EncounterType> treatmentInitiationEncounterType = Context.getEncounterService ().findEncounterTypes ("Treatment Initiation");
			// Get all Encounters from encounter type 'Treatment Initiation' for patient id
			List<Encounter> treatmentInitiationEncounters = Context.getEncounterService ().getEncounters (patient, null, null, null, null, treatmentInitiationEncounterType, null, null, null, false);
			
			// Get 'Treatment Initiated' Concept
			List<Concept> concepts = Context.getConceptService ().getConceptsByName ("Treatment Initiated");
			// Get All observations for encounter type and concept
			List<Obs> observations = Context.getObsService ().getObservations (null, treatmentInitiationEncounters, concepts, null, null, null, null, null, null, null, null, false);
			
			
			// if encounter is null
			if(treatmentInitiationEncounters == null || treatmentInitiationEncounters.size() == 0){
				return CustomMessage.getErrorMessage(ErrorType.TREATMENT_INITIATION_FORM_NOT_FILLED);  // Prerequisite fails
			}
			else{
				
				for (Obs obs : observations)
				{
					Concept treatmentInitiated = obs.getValueCoded();
					String ss = treatmentInitiated.getName().toString();  // if value is No
					if (ss.equals ("Yes"))
					{
						return "";  // Prerequisite Pass
					}
				}
				return CustomMessage.getErrorMessage(ErrorType.TREATMENT_NOT_INITIATED);  // Prerequisite Fail
				
			}
			
		}
		
		else if(encounterType.equals("Treatment Outcome")){
			
			// Get 'Treatment Outcome' Encounter Type 
			Collection<EncounterType> treatmentOuctomeEncounterType = Context.getEncounterService ().findEncounterTypes ("Treatment Outcome");
			// Get all Encounters from encounter type 'Treatment Outcome' for patient id
			List<Encounter> treatmentOutcomeEncounters = Context.getEncounterService ().getEncounters (patient, null, null, null, null, treatmentOuctomeEncounterType, null, null, null, false);
			
			
			// if encounter is null
			if(treatmentOutcomeEncounters == null || treatmentOutcomeEncounters.size() == 0){
				
				// Get 'Treatment Initiation' Encounter Type 
				Collection<EncounterType> treatmentInitiationEncounterType = Context.getEncounterService ().findEncounterTypes ("Treatment Initiation");
				// Get all Encounters from encounter type 'Treatment Initiation' for patient id
				List<Encounter> treatmentInitiationEncounters = Context.getEncounterService ().getEncounters (patient, null, null, null, null, treatmentInitiationEncounterType, null, null, null, false);
				
				// if encounter is null
				if(treatmentInitiationEncounters == null || treatmentInitiationEncounters.size() == 0)
					return CustomMessage.getErrorMessage(ErrorType.TREATMENT_INITIATION_FORM_NOT_FILLED);  // Prerequisite Fail
				else{
					
					// Get 'Treatment Initiated' Concept
					List<Concept> concepts = Context.getConceptService ().getConceptsByName ("Treatment Initiated");
					// Get All observations for encounter type and concept
					List<Obs> observations = Context.getObsService ().getObservations (null, treatmentInitiationEncounters, concepts, null, null, null, null, null, null, null, null, false);
					
					for (Obs obs : observations)
					{
						Concept treatmentInitiated = obs.getValueCoded();
						String ss = treatmentInitiated.getName().toString();
						if (ss.equals ("Yes"))
						{
							return "";    // Prerequisite true
						}
					}
					return CustomMessage.getErrorMessage(ErrorType.TREATMENT_NOT_INITIATED); 	// Prerequisite Fail				
				}
				
			}
			else
				
				return CustomMessage.getErrorMessage(ErrorType.TREATMENT_OUTCOME_ALREADY_FILLED); // Prerequisite Fail
			
		} 
		
		return "";
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
			return false;
		}
		return true;
	}

	public String getIdWithCheckDigit (String idWithoutCheckDigit)
	{
		String validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVYWXZ_";
		int sum = 0;
		for (int i = 0; i < idWithoutCheckDigit.length (); i++)
		{
			char ch = idWithoutCheckDigit.charAt (idWithoutCheckDigit.length () - i - 1);
			if (validChars.indexOf (ch) == -1)
				return "";
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
		int checkDigit = (10 - (sum % 10)) % 10;
		String idWithCheckDigit = idWithoutCheckDigit.concat ("-");
		return idWithCheckDigit.concat (Integer.toString (checkDigit));
	}
	
	public String getLastImport(){
		String selectQuery = "SELECT * from openmrs_rpt.metadata where type = 'lastUpdate'";
	    String[][] data = executeQuery (selectQuery, null);
	    String value = data[0][1];
		return value;
	}
	
	public String[][] getUsername(String username){
		String selectQuery = "SELECT * from openmrs_rpt.data_screener where username = '"+username+"'";
	    String[][] data = executeQuery (selectQuery, null);
		return data;
	}
	
	public boolean saveText(String[] username , String[] messageDetail){
		// username                                // messageDetail
		// 0 - username                            // 0 - date
		// 1 - name                                // 1 - referenceNumber
		// 2 - location                           // 2 - text
		// 3 - primaryNumber                      // 3 - addedby
		// 4 - secondaryNumber                    // 4 - formatteddate
		
		String originator = username[3];
		if(username[3]== null || username[3]=="null"){
			originator = username[4];
		}
		String insertQuery = 
			"insert into openmrs_rpt.inboundmessages " +
			"(originator,referenceNumber,recieveDate,message,dateText,username,name,location,primaryNumber,secondaryNumber,addedby) " +
			"values('"+originator+"', '"+messageDetail[1]+"', '"+messageDetail[0]+" 23:59:59.999',  '"+messageDetail[2]+"', '"+messageDetail[4]+"', '"+username[0]+"', '"+username[1]+"', '"+username[2]+"', '"+username[3]+"', '"+username[4]+"', '"+messageDetail[3]+"');";
		System.out.println (insertQuery);
		boolean flag = execute (insertQuery);
		return flag;	
	}
	
	
	public String[][] getUsernameList(){
		String selectQuery = "SELECT username from openmrs_rpt.data_screener";
	    String[][] data = executeQuery (selectQuery, null);
		return data;
	}
	
	public String[][] getAllMessages(String filter){
		String selectQuery = "SELECT * from openmrs_rpt.inboundMessages where voided = 0 "+filter+" order by SUBSTRING(recieveDate, 1, 10) desc, username";
		System.out.println (selectQuery);
	    String[][] data = executeQuery (selectQuery, null);
		return data;
	}
	
	public String[][] getAllLocations(){
		String selectQuery = "select name from openmrs.location";
	    String[][] data = executeQuery (selectQuery, null);
		return data;
	}
	
	public String[][] getAllScreeners(){
		String selectQuery = "select * from openmrs_rpt.data_screener where username like '0%'";
	    String[][] data = executeQuery (selectQuery, null);
		return data;
	}
	
	public String[][] getAllSceenersData(String dateFilter){
		String selectQuery = 
			"select total_screened , suspect, non_suspect, total_sputum_submitted, left_username, left_date " +
			"from (select total_screened, total_sputum_submitted, screening_total.username as left_username, screening_total.date as left_date from " +
			          "((select count(*) as total_screened, u.username, substring(e.encounter_datetime,1,10) as date " +
			             "from openmrs.encounter e, openmrs.users u " +
			             "where e.encounter_type = 1 and "+dateFilter+" and e.creator = u.user_id " +
			             "group by substring(e.encounter_datetime,1,10), e.creator " +
			             "order by substring(e.encounter_datetime,1,10) desc) as screening_total " +
			           "left join " +
			           "(select count(*) as total_sputum_submitted, u.username, substring(e.encounter_datetime,1,10) as date " +
			            "from openmrs.encounter e, openmrs.users u " +
			            "where e.encounter_type = 2 and "+dateFilter+" and e.creator = u.user_id " +
			            "group by substring(e.encounter_datetime,1,10), e.creator " +
			            "order by substring(e.encounter_datetime,1,10) desc) as sputum_total " +
			           "ON screening_total.username = sputum_total.username and screening_total.date = sputum_total.date) " +
			       "union " +
			           "select total_screened, total_sputum_submitted, screening_total.username as right_username, screening_total.date as right_date  from " +
			           "((select count(*) as total_screened, u.username, substring(e.encounter_datetime,1,10) as date " +
			              "from openmrs.encounter e, openmrs.users u " +
			              "where e.encounter_type = 1 and "+dateFilter+" and e.creator = u.user_id " +
			              "group by substring(e.encounter_datetime,1,10), e.creator " +
			              "order by substring(e.encounter_datetime,1,10) desc) as screening_total " +
			           "right join " +
			           "(select count(*) as total_sputum_submitted, u.username, substring(e.encounter_datetime,1,10) as date " +
			              "from openmrs.encounter e, openmrs.users u " +
			              "where e.encounter_type = 2 and "+dateFilter+" and e.creator = u.user_id " +
			              "group by substring(e.encounter_datetime,1,10), e.creator " +
			              "order by substring(e.encounter_datetime,1,10) desc) as sputum_total " +
			           "ON screening_total.username = sputum_total.username and screening_total.date = sputum_total.date)) as big_table " +
		   "left join " +
		    "(select count(CASE WHEN pa.value = 'Suspect' then 1 else null end) as suspect, count(CASE WHEN pa.value = 'Non-Suspect' then 1 else null end) as non_suspect, u.username, substring(en.encounter_datetime,1,10) as date " +
		    "from openmrs.encounter en, openmrs.person_attribute pa, openmrs.users u " +
		    "where pa.person_attribute_type_id = 12 and en.patient_id = pa.person_id and en.encounter_type = 1 and en.creator = u.user_id " +
		    "group by substring(en.encounter_datetime,1,10), en.creator " +
		    "order by substring(en.encounter_datetime,1,10) desc) as screening_summary " +
        "ON big_table.left_username = screening_summary.username and big_table.left_date = screening_summary.date;";
		System.out.println(selectQuery);
		String[][] data = executeQuery (selectQuery, null);
		return data;
	}
	
	/*
	  * TEMPORARY. Remove after all MS Access data has been uploaded
	  */
	 private String doDataUpload() {
	  return new AccessUpload().upload();
	 }

}
