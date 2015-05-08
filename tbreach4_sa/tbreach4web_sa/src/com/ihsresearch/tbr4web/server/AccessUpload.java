package com.ihsresearch.tbr4web.server;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.jfree.util.Log;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterRole;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Provider;
import org.openmrs.User;
import org.openmrs.api.context.Context;

import com.ihsresearch.tbr4web.server.util.DateTimeUtil;
import com.ihsresearch.tbr4web.shared.CsvUtil;

public class AccessUpload {

	static final String filePath = "C:\\Users\\Rabbia\\Desktop\\MINE-TB Clean data.csv";

	static final char separator = ',';

	static final String dateFormat = "dd-MM-yy";

	static final boolean deleteExisting = false;

	/**
	 * Uploads the CSV data
	 * 
	 * @return
	 */
	public String upload() {
		StringBuilder message = new StringBuilder("SUCCESS");
		// Initialize the service class
		CsvUtil csvUtil = new CsvUtil(filePath, separator, true);
		String[] header = csvUtil.getHeader();
		String[][] data = csvUtil.readData();

		CsvImporterConfiguration configuration = new CsvImporterConfiguration();
		configuration.setConfigName("MINE-TB import");
		configuration.setConfigDescription("MINE-TB legacy data import");
		User user = Context.getUserService().getUserByUsername("owais");
		configuration.setCreator(user);
		configuration
				.setCsvHeader("form_date,time_stamp,district_name,facility_name,screener_id,user_id,nhls_id,first_name,surname,dob,age,gender,address,phone1,tb_contact,hiv_positive,diabetes,sputum_collection_date,sputum_result_date,sputum_result,mdr,treatment_start_date,died,lost_followup,transferred,transfer_to,comments");
		configuration.setDateCreated(new Date());
		configuration.setDateFormat(dateFormat);
		configuration.setFieldSeparator(separator);
		configuration.setId(0);
		configuration.setLineSeparator('\n');
		configuration.setTimeFormat("");
		configuration.setUuid(UUID.randomUUID().toString());

		List<CsvImporterMapping> map = createMapping(configuration);
		configuration.setMappings(map);

		// Map each index in header to respective mapping column name
		Map<String, Integer> indices = new HashMap<String, Integer>();
		// Search for the index of each mapping in the header
		Iterator<CsvImporterMapping> iter = map.iterator();
		while (iter.hasNext()) {
			CsvImporterMapping mapping = iter.next();
			int index = Arrays.asList(header).indexOf(mapping.getColumnName());
			indices.put(mapping.getObjectName(), index);
		}

		// Declare empty arrays to fill in
		List<CsvImporterMapping> definedAttributes = new ArrayList<CsvImporterMapping>();
		// Manually adding attributes from the list
		definedAttributes.add(map.get(9));
		definedAttributes.add(map.get(10));
		definedAttributes.add(map.get(11));

		// For each record, fill respective properties
		for (int i = 0; i < data.length; i++) {
			try {
				Patient patient = new Patient();
				Date dateCreated = DateTimeUtil.getDateFromString(data[i][indices.get("date_created")], dateFormat);
				User creator = Context.getUserService().getUserByUsername(data[i][indices.get("creator")]);
				PatientIdentifierType identifierType = Context.getPatientService().getPatientIdentifierTypeByName(
						"OpenMRS Identification Number");
				// Patient ID
				if (indices.containsKey("patient_identifier")) {
					String id = data[i][indices.get("patient_identifier")];
					// Check if this one already exists
					List<Patient> patients = Context.getPatientService().getPatients(id);
					if (patients != null) {
						if (patients.size() > 0) {
							message.append("ERROR. Patient ID " + id + " already exists. Skipping/Deleting.\n");
							if (deleteExisting)
								Context.getPatientService().purgePatient(patients.get(0));
							continue;
						}
					}
					PatientIdentifier patientId = new PatientIdentifier(id, identifierType, null);
					if (indices.containsKey("location")) {
						Location location = Context.getLocationService().getLocation(data[i][indices.get("location")]);
						patientId.setLocation(location);
					}
					patient.addIdentifier(patientId);
				} else {
					message.append("ERROR. Skipping record " + (i + 1) + " because of missing patient ID.\n");
					continue;
				}
				// Demographics
				if (indices.containsKey("gender"))
					patient.setGender(data[i][indices.get("gender")]);
				else {
					message.append("Skipping record " + (i + 1) + " because of missing gender.\n");
					continue;
				}
				if (indices.containsKey("date_created")) {
					patient.setPersonDateCreated(dateCreated);
					patient.setDateCreated(dateCreated);
				}
				if (indices.containsKey("date_of_birth"))
					patient.setBirthdate(DateTimeUtil.getDateFromString(data[i][indices.get("date_of_birth")],
							dateFormat));
				else if (indices.containsKey("age"))
					patient.setBirthdateFromAge(Integer.parseInt(data[i][indices.get("age")]), dateCreated);
				else {
					message.append("Skipping record " + (i + 1) + " because of missing age.\n");
					continue;
				}
				if (indices.containsKey("creator")) {
					patient.setCreator(creator);
					patient.setPersonCreator(creator);
				}
				// Person Name
				PersonName name = new PersonName();
				if (indices.containsKey("given_name"))
					name.setGivenName(data[i][indices.get("given_name")]);
				else {
					Log.warn("ERROR. Skipping record " + (i + 1) + " because of missing given name.\n");
					continue;
				}
				if (indices.containsKey("family_name"))
					name.setFamilyName(data[i][indices.get("family_name")]);
				else {
					Log.warn("ERROR. Skipping record " + (i + 1) + " because of missing last name.\n");
					continue;
				}
				patient.addName(name);
				// Person Address
				PersonAddress address = new PersonAddress();
				if (indices.containsKey("address1"))
					address.setAddress1(data[i][indices.get("address1")]);
				if (indices.containsKey("city_village"))
					address.setCityVillage(data[i][indices.get("city_village")]);
				if (indices.containsKey("country"))
					address.setCountry(data[i][indices.get("country")]);
				patient.addAddress(address);
				// Person Attributes
				for (int j = 0; j < definedAttributes.size(); j++) {
					CsvImporterMapping mapping = definedAttributes.get(j);
					String type = mapping.getObjectName();
					PersonAttributeType attributeType = Context.getPersonService().getPersonAttributeTypeByName(type);
					PersonAttribute attribute = new PersonAttribute();
					attribute.setAttributeType(attributeType);
					attribute.setValue(data[i][indices.get(mapping.getObjectName())]);
					patient.addAttribute(attribute);
				}
				Patient savedPatient = Context.getPatientService().savePatient(patient);
				if (savedPatient != null) {
					message.append("SUCCESS. " + savedPatient.getPatientIdentifier().getIdentifier() + "\n");
					/*
					 * This is specific to MINE-TB Access imports. Remove
					 * afterwards
					 */
					try {
						Encounter screening = prepareScreeningEncounter(savedPatient, map, data[i], indices, creator);
						screening = Context.getEncounterService().saveEncounter(screening);
					} catch (Exception e) {
						message.append("Screening encounter did not save for: "
								+ patient.getPatientIdentifier().getIdentifier() + "\n");
					}
					try {
						Encounter sputumSubmission = prepareSputumSubmission(savedPatient, map, data[i], indices,
								creator);
						sputumSubmission = Context.getEncounterService().saveEncounter(sputumSubmission);
					} catch (Exception e) {
						message.append("Sputum Submission encounter did not save for: "
								+ patient.getPatientIdentifier().getIdentifier() + "\n");
					}
					try {
						Encounter sputumResults = prepareSputumResult(savedPatient, map, data[i], indices, creator);
						sputumResults = Context.getEncounterService().saveEncounter(sputumResults);
					} catch (Exception e) {
						message.append("Sputum Results encounter did not save for: "
								+ patient.getPatientIdentifier().getIdentifier() + "\n");
					}
					/* Block comment end */
				}
			} catch (ParseException e) {
				message.append("ERROR. Some parse thing with record " + (i + 1) + "\n");
			} catch (NullPointerException e) {
				message.append("ERROR. Some null thing with record " + (i + 1) + "\n");
			} catch (Exception e) {
				message.append("ERROR. Some exception thing with record " + (i + 1) + ". Here: " + e.getMessage()
						+ "\n");
			}
		}
		Context.closeSession();
		System.out.println(message.toString());
		return message.toString();
	}

	private List<CsvImporterMapping> createMapping(CsvImporterConfiguration configuration) {
		List<CsvImporterMapping> map = new ArrayList<CsvImporterMapping>();
		// Person
		int k = 0;
		User user = configuration.getCreator();
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "age",
				OpenMrsObjectCategory.PERSON, "age", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "dob",
				OpenMrsObjectCategory.PERSON, "date_of_birth", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "gender",
				OpenMrsObjectCategory.PERSON, "gender", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "form_date",
				OpenMrsObjectCategory.PERSON, "date_created", null));
		// Person Names
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "first_name",
				OpenMrsObjectCategory.PERSON_NAME, "given_name", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "surname",
				OpenMrsObjectCategory.PERSON_NAME, "family_name", null));
		// Person Address
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "address",
				OpenMrsObjectCategory.PERSON_ADDRESS, "address1", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "city",
				OpenMrsObjectCategory.PERSON_ADDRESS, "city_village", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "country",
				OpenMrsObjectCategory.PERSON_ADDRESS, "country", null));
		// Person attributes
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "phone1",
				OpenMrsObjectCategory.PERSON_ATTRIBUTE, "Primary Phone", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "facility_name",
				OpenMrsObjectCategory.PERSON_ATTRIBUTE, "Location", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "suspect",
				OpenMrsObjectCategory.PERSON_ATTRIBUTE, "Suspect/Non-Suspect", null));
		// Patient
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "user_id",
				OpenMrsObjectCategory.PATIENT, "creator", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "facility_name",
				OpenMrsObjectCategory.LOCATION, "location", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "patient_id",
				OpenMrsObjectCategory.PATIENT, "patient_identifier", null));
		// Provider
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "screener_id",
				OpenMrsObjectCategory.PROVIDER, "provider", null));
		// Encounters
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "form_date",
				OpenMrsObjectCategory.ENCOUNTER, "screening_date_created", "Screening"));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "time_stamp",
				OpenMrsObjectCategory.ENCOUNTER, "time_stamp", "Screening"));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "sputum_collection_date",
				OpenMrsObjectCategory.ENCOUNTER, "sputum_submission_date_created", "Sputum Submission"));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "sputum_result_date",
				OpenMrsObjectCategory.ENCOUNTER, "sputum_result_date_created", "Sputum Result"));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "treatment_start_date",
				OpenMrsObjectCategory.ENCOUNTER, "treatment_date_created", "Treatment Initiation"));
		// Observations
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "district_name",
				OpenMrsObjectCategory.OBS, "District", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "facility_name",
				OpenMrsObjectCategory.OBS, "Facility name", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "diabetes",
				OpenMrsObjectCategory.OBS, "Diabetes", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "tb_contact",
				OpenMrsObjectCategory.OBS, "Contact with TB", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "hiv_positive",
				OpenMrsObjectCategory.OBS, "Last HIV result", null));
		// Sputum Submission/Result
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "nhls_id",
				OpenMrsObjectCategory.OBS, "Lab Test ID", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "sputum_result",
				OpenMrsObjectCategory.OBS, "GeneXpert Result", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "rif",
				OpenMrsObjectCategory.OBS, "RIF Result", null));
		map.add(new CsvImporterMapping(k++, new Date(), user, null, null, configuration, "sputum_result_date",
				OpenMrsObjectCategory.OBS, "Date Of Test Report", null));
		return map;
	}

	private Encounter prepareScreeningEncounter(Patient patient, List<CsvImporterMapping> map, String[] data,
			Map<String, Integer> indices, User creator) throws ParseException, NullPointerException {
		Encounter encounter = null;
		String formDate = data[indices.get("screening_date_created")];
		if (!formDate.equals("")) {
			encounter = new Encounter();
			encounter.setEncounterType(Context.getEncounterService().getEncounterType("Screening"));
			Date date = DateTimeUtil.getDateFromString(formDate, dateFormat);
			Date timestamp = DateTimeUtil.getDateFromString(data[indices.get("time_stamp")], dateFormat);
			encounter.setCreator(creator);
			encounter.setDateCreated(timestamp);
			encounter.setEncounterDatetime(date);
			EncounterRole role = Context.getEncounterService().getEncounterRole(1);
			Provider provider = Context.getProviderService().getProviderByIdentifier(creator.getUsername());
			if (provider == null) {
				provider = Context.getProviderService().getProviderByIdentifier("RACHEL");
			}
			encounter.addProvider(role, provider);
			Location location = Context.getLocationService().getLocation(data[indices.get("location")]);
			encounter.setLocation(location);
			encounter.setPatient(patient);
			Concept question, answerCoded;
			String answerText;
			// District
			question = Context.getConceptService().getConceptByName("District");
			answerText = data[indices.get("District")];
			Obs obs = new Obs(patient, question, timestamp, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueText(answerText);
			encounter.addObs(obs);
			// Facility name
			question = Context.getConceptService().getConceptByName("Facility name");
			answerText = data[indices.get("Facility name")];
			obs = new Obs(patient, question, timestamp, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueText(answerText);
			encounter.addObs(obs);
			// Diabetes
			question = Context.getConceptService().getConceptByName("Diabetes");
			answerCoded = Context.getConceptService().getConceptByName(data[indices.get("Diabetes")]);
			obs = new Obs(patient, question, timestamp, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueCoded(answerCoded);
			encounter.addObs(obs);
			// Contact with TB
			question = Context.getConceptService().getConceptByName("Contact with TB");
			answerCoded = Context.getConceptService().getConceptByName(data[indices.get("Contact with TB")]);
			obs = new Obs(patient, question, timestamp, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueCoded(answerCoded);
			encounter.addObs(obs);
			// Last HIV result
			question = Context.getConceptService().getConceptByName("Last HIV result");
			answerCoded = Context.getConceptService().getConceptByName(data[indices.get("Last HIV result")]);
			obs = new Obs(patient, question, timestamp, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueCoded(answerCoded);
			encounter.addObs(obs);
		}
		return encounter;
	}

	private Encounter prepareSputumSubmission(Patient patient, List<CsvImporterMapping> map, String[] data,
			Map<String, Integer> indices, User creator) throws ParseException, NullPointerException {
		Encounter encounter = null;
		String formDate = data[indices.get("sputum_submission_date_created")];
		if (!formDate.equals("")) {
			encounter = new Encounter();
			encounter.setEncounterType(Context.getEncounterService().getEncounterType("Sputum Submission"));
			Date date = DateTimeUtil.getDateFromString(formDate, dateFormat);
			encounter.setCreator(creator);
			encounter.setDateCreated(date);
			encounter.setEncounterDatetime(date);
			EncounterRole role = Context.getEncounterService().getEncounterRole(1);
			Provider provider = Context.getProviderService().getProviderByIdentifier(creator.getUsername());
			if (provider == null) {
				provider = Context.getProviderService().getProviderByIdentifier("RACHEL");
			}
			encounter.addProvider(role, provider);
			Location location = Context.getLocationService().getLocation(data[indices.get("location")]);
			encounter.setLocation(location);
			encounter.setPatient(patient);
			Concept question;
			String answerText;
			// District
			question = Context.getConceptService().getConceptByName("District");
			answerText = data[indices.get("District")];
			Obs obs = new Obs(patient, question, date, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueText(answerText);
			encounter.addObs(obs);
			// Facility name
			question = Context.getConceptService().getConceptByName("Facility name");
			answerText = data[indices.get("Facility name")];
			obs = new Obs(patient, question, date, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueText(answerText);
			encounter.addObs(obs);
			// Lab Test ID
			question = Context.getConceptService().getConceptByName("Lab Test ID");
			answerText = data[indices.get("Lab Test ID")];
			obs = new Obs(patient, question, date, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueText(answerText);
			encounter.addObs(obs);
		}
		return encounter;
	}

	private Encounter prepareSputumResult(Patient patient, List<CsvImporterMapping> map, String[] data,
			Map<String, Integer> indices, User creator) throws ParseException, NullPointerException {
		Encounter encounter = null;
		String formDate = data[indices.get("sputum_result_date_created")];
		if (!formDate.equals("")) {
			encounter = new Encounter();
			encounter.setEncounterType(Context.getEncounterService().getEncounterType("Sputum Result"));
			Date date = DateTimeUtil.getDateFromString(formDate, dateFormat);
			encounter.setCreator(creator);
			encounter.setDateCreated(date);
			encounter.setEncounterDatetime(date);
			EncounterRole role = Context.getEncounterService().getEncounterRole(1);
			Provider provider = Context.getProviderService().getProviderByIdentifier(creator.getUsername());
			if (provider == null) {
				provider = Context.getProviderService().getProviderByIdentifier("RACHEL");
			}
			encounter.addProvider(role, provider);
			Location location = Context.getLocationService().getLocation(data[indices.get("location")]);
			encounter.setLocation(location);
			encounter.setPatient(patient);
			Concept question, answerCoded;
			String answerText;
			Date answerDate;
			// District
			question = Context.getConceptService().getConceptByName("District");
			answerText = data[indices.get("District")];
			Obs obs = new Obs(patient, question, date, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueText(answerText);
			encounter.addObs(obs);
			// Facility name
			question = Context.getConceptService().getConceptByName("Facility name");
			answerText = data[indices.get("Facility name")];
			obs = new Obs(patient, question, date, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueText(answerText);
			encounter.addObs(obs);
			// Lab Test ID
			question = Context.getConceptService().getConceptByName("Lab Test ID");
			answerText = data[indices.get("Lab Test ID")];
			obs = new Obs(patient, question, date, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueText(answerText);
			encounter.addObs(obs);
			// Date Of Test Report
			question = Context.getConceptService().getConceptByName("Date Of Test Report");
			answerDate = DateTimeUtil.getDateFromString(data[indices.get("Date Of Test Report")], dateFormat);
			obs = new Obs(patient, question, date, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueDate(answerDate);
			encounter.addObs(obs);
			// GeneXpert Result
			question = Context.getConceptService().getConceptByName("GeneXpert Result");
			answerCoded = Context.getConceptService().getConceptByName(data[indices.get("GeneXpert Result")]);
			obs = new Obs(patient, question, date, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueCoded(answerCoded);
			encounter.addObs(obs);
			// RIF Result
			question = Context.getConceptService().getConceptByName("RIF Result");
			answerCoded = Context.getConceptService().getConceptByName(data[indices.get("RIF Result")]);
			obs = new Obs(patient, question, date, location);
			obs.setComment("Imported from MS Access DB");
			obs.setValueCoded(answerCoded);
			encounter.addObs(obs);
		}
		return encounter;
	}
}
