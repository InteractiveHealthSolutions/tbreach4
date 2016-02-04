/*
Copyright(C) 2016 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html
Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
package com.ihsinformatics.minetb.datawarehouse;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import com.ihsinformatics.minetb.datawarehouse.util.CommandType;
import com.ihsinformatics.minetb.datawarehouse.util.DatabaseUtil;
import com.ihsinformatics.minetb.datawarehouse.util.DateTimeUtil;
import com.ihsinformatics.minetb.datawarehouse.util.FileUtil;

/**
 * OpenMRS database processing class for TBR3 warehouse
 * 
 * @author owais.hussain@ihsinformatics.com
 *
 */
public class OpenMrsProcessor extends AbstractProcessor {

	private static final Logger log = Logger.getLogger(Class.class.getName());
	private static final String schemaName = "openmrs";
	private String scriptFilePath;
	private DatabaseUtil dwDb;
	private DatabaseUtil openMrsDb;
	private ArrayList<String> tablesWithDateCreated;
	private ArrayList<String> tablesWithDateCreatedAndChanged;
	private ArrayList<String> tablesNotWithDateCreated;
	String[] sourceTables = { "active_list", " active_list_allergy",
			" active_list_problem", " active_list_type",
			" calculation_registration", " clob_datatype_storage", " concept",
			" concept_answer", " concept_class", " concept_complex",
			" concept_datatype", " concept_description", " concept_map_type",
			" concept_name", " concept_name_tag", " concept_name_tag_map",
			" concept_numeric", " concept_proposal",
			" concept_proposal_tag_map", " concept_reference_map",
			" concept_reference_source", " concept_reference_term",
			" concept_reference_term_map", " concept_set",
			" concept_set_derived", " concept_state_conversion",
			" concept_stop_word", " concept_word", " drug", " drug_ingredient",
			" drug_order", " encounter", " encounter_provider",
			" encounter_role", " encounter_type", " field", " field_answer",
			" field_type", " form", " form_field", " form_resource",
			" global_property", " hl7_in_archive", " hl7_in_error",
			" hl7_in_queue", " hl7_source", " location", " location_attribute",
			" location_attribute_type", " location_tag", " location_tag_map",
			" logic_rule_definition", " logic_rule_token",
			" logic_rule_token_tag", " logic_token_registration",
			" logic_token_registration_tag", " note", " notification_alert",
			" notification_alert_recipient", " notification_template", "obs",
			" order_type", " orders", " patient", " patient_identifier",
			" patient_identifier_type", " patient_program", " patient_state",
			" person", " person_address", " person_attribute",
			" person_attribute_type", " person_merge_log", " person_name",
			" privilege", " program", " program_workflow",
			" program_workflow_state", " provider", " provider_attribute",
			" provider_attribute_type", " relationship", " relationship_type",
			" role", " role_privilege", " role_role", " scheduler_task_config",
			" scheduler_task_config_property", " test_order", " user_property",
			" user_role", " users", " visit", " visit_attribute",
			" visit_attribute_type", " visit_type" };

	/**
	 * Constructor to initialize the object
	 * 
	 * @param scriptFilePath
	 *            that contains SQL statements to execute for OpenMRS DB
	 * @param openMrsDb
	 *            connection to OpenMRS
	 * @param dwDb
	 *            connection to Data warehouse
	 */
	public OpenMrsProcessor(String scriptFilePath, DatabaseUtil openMrsDb,
			DatabaseUtil dwDb) {
		this.scriptFilePath = scriptFilePath;
		this.openMrsDb = openMrsDb;
		this.dwDb = dwDb;
		Object obj = dwDb.runCommand(CommandType.SELECT,
				"select count(*) from information_schema.tables");
		if (obj == null)
			log.severe("Unable to connect with Data Warehouse!");
		else
			log.info("Data Warehouse connection OK");
		obj = openMrsDb.runCommand(CommandType.SELECT,
				"select count(*) from users");
		if (obj == null)
			log.warning("Unable to connect with OpenMRS!");
		else
			log.info("OpenMRS connection OK");
	}

	// Create OpenMRS DB schema into DW
	public boolean createSchema(boolean fromScratch) {
		FileUtil fileUtil = new FileUtil();
		String[] queries = fileUtil.getLines(scriptFilePath);
		if (fromScratch) {
			// Recreate tables
			for (String query : queries) {
				if (query.toUpperCase().startsWith("DROP")) {
					dwDb.runCommand(CommandType.DROP, query);
				} else {
					log.info(query);
					dwDb.runCommand(CommandType.CREATE, query);
				}
			}
		}
		return true;
	}

	/**
	 * Extracts data from OpenMRS connection and stores as CSV files. If a file
	 * already exists, it will be recreated
	 * 
	 * @param dataPath
	 *            where CSV files will be stored
	 * @return
	 */
	public boolean extract(String dataPath) {
		log.info("Importing data from source into raw files");
		// Fetch file from source and generate CSVs
		for (String table : sourceTables) {
			String fileName = dataPath.replace(FileUtil.SEPARATOR,
					FileUtil.SEPARATOR + FileUtil.SEPARATOR)
					+ schemaName
					+ "_"
					+ table + ".csv";

			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			String query = "SELECT * FROM "
					+ table
					+ " INTO OUTFILE '"
					+ fileName
					+ "' FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\n'";
			Object obj = openMrsDb.runCommand(CommandType.EXECUTE, query);
			if (obj == null) {
				log.warning("No data was exported to CSV for table: " + table);
			}
		}
		return true;
	}

	/**
	 * Extracts data from OpenMRS connection and stores as CSV files. If a file
	 * already exists, it will be recreated
	 * 
	 * @param dataPath
	 *            where CSV files will be stored
	 * @return
	 */
	public boolean extract(String dataPath, Date dateFrom, Date dateTo) {
		log.info("Importing data from source into raw files");
		createStoredProcedure();
		String query = "CALL minetb_dw.extract_openmrs ('"
				+ DateTimeUtil.getSqlDateTime(dateFrom) + "', '"
				+ DateTimeUtil.getSqlDateTime(dateTo) + "', '" + dataPath
				+ "')";
		Object obj = openMrsDb.runCommand(CommandType.EXECUTE, query);
		if (obj == null) {
			log.warning("error");
		}
		// }
		return true;
	}

	/**
	 * Loads table data from CSV files into Data warehouse
	 * 
	 * @param dataPath
	 *            where CSV files will be loaded from
	 * @return
	 */
	public boolean load(String dataPath) {
		boolean noImport = true;
		log.info("Importing data from raw files into data warehouse");
		for (String table : sourceTables) {
			String filePath = dataPath.replace(FileUtil.SEPARATOR,
					FileUtil.SEPARATOR + FileUtil.SEPARATOR)
					+ schemaName
					+ "_"
					+ table + ".csv";
			File file = new File(filePath);
			if (!file.exists()) {
				log.warning("No CSV file exists for table " + table);
				continue;
			}
			String query = "LOAD DATA INFILE '"
					+ filePath
					+ "' INTO TABLE "
					+ table
					+ " FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\n'";
			Object obj = dwDb.runCommand(CommandType.EXECUTE, query);
			if (obj == null) {
				log.warning("No data was from CSV for table: " + table);
			} else {
				noImport = false;
			}
			// Try to delete the CSV
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return !noImport;
	}

	/**
	 * Denormalize and standardize tables according to the warehouse
	 */
	boolean transform() {
		// Create a temporary table to hold respective questions for each
		// encounter type
		dwDb.runCommand(CommandType.DROP, "drop table if exists tmp");
		dwDb.runCommand(CommandType.CREATE,
				"create table tmp select distinct encounter_type, question from dim_obs");
		// Fetch encounter types and names
		Object[][] encounterTypes = dwDb.getTableData("dim_encounter",
				"distinct encounter_type, encounter_name", "");
		if (encounterTypes == null) {
			log.severe("No Encounter types found in OpenMRS data.");
			return false;
		}
		for (Object[] encounterType : encounterTypes) {
			// Create a deencounterized table
			Object[] elements = dwDb.getColumnData("tmp", "question",
					"encounter_type=" + encounterType[0].toString());
			StringBuilder groupConcat = new StringBuilder();
			for (Object element : elements) {
				if (element == null)
					continue;
				String str = element.toString().replaceAll("[^A-Za-z0-9]", "_")
						.toLowerCase();
				groupConcat.append("group_concat(if(o.question = '" + element
						+ "', o.answer, NULL)) AS '" + str + "', ");
			}
			String baseQuery = "select e.surrogate_id, e.system_id, e.encounter_id, e.provider, e.location_id, l.location_name, e.patient_id, e.date_entered, "
					+ groupConcat.toString()
					+ "'' as BLANK "
					+ "from dim_encounter as e inner join dim_obs as o on o.encounter_id = e.encounter_id inner join dim_location as l on l.location_id = e.location_id "
					+ "where e.encounter_type = '"
					+ encounterType[0].toString()
					+ "' "
					+ "group by e.surrogate_id, e.system_id, e.encounter_id, e.patient_id, e.provider, e.location_id, l.location_name, e.patient_id, e.date_entered";
			String encounterName = encounterType[1].toString().toLowerCase()
					.replace(" ", "_").replace("-", "_");
			// Drop previous table
			dwDb.runCommand(CommandType.DROP, "drop table if exists enc_"
					+ encounterName);
			log.info("Generating table for " + encounterType[1].toString());
			// Insert new data
			Object result = dwDb.runCommand(CommandType.CREATE,
					"create table enc_" + encounterName + " " + baseQuery);
			if (result == null) {
				log.warning("No data imported for Encounter "
						+ encounterType[1].toString());
			}
			// Creating Primary key
			dwDb.runCommand(CommandType.ALTER, "alter table enc_"
					+ encounterName
					+ " add primary key surrogate_id (surrogate_id)");
		}
		return false;
	}

	/**
	 * Picks new/changed data and updates Data warehouse
	 */
	boolean update(String dataPath, Date dateFrom, Date dateTo) {
		boolean result = true;
		log.info("Updating OpenMRS data");
		createSchema(false);
		extract(dataPath, dateFrom, dateTo);
		load(dataPath);
		transform();
		return result;
	}

	/**
	 * Seperate all the tables with NoDateCreated, DateCreated and
	 * DateCreated+Changed
	 */
	public void divideTables() {

		tablesWithDateCreatedAndChanged = new ArrayList<String>();
		tablesWithDateCreated = new ArrayList<String>();
		tablesNotWithDateCreated = new ArrayList<String>();

		for (String table : sourceTables) {

			String firstQuery = "SELECT date_changed FROM " + "minetb.om_"
					+ table;
			Object obj = openMrsDb.runCommand(CommandType.EXECUTE, firstQuery);
			if (obj != null) {
				tablesWithDateCreatedAndChanged.add(table);
			} else if (obj == null) {
				String secondQuery = "SELECT date_created FROM " + "minetb.om_"
						+ table;
				obj = openMrsDb.runCommand(CommandType.EXECUTE, secondQuery);
				if (obj != null) {
					tablesWithDateCreated.add(table);
				} else {
					tablesNotWithDateCreated.add(table);
				}
			}
		}

		ArrayList<String> queriesList = new ArrayList<String>();
		int count = 1;
		for (String table : tablesWithDateCreatedAndChanged) {

			String query = "SET @q"
					+ count
					+ "=concat(\"SELECT * FROM om_"
					+ table
					+ " WHERE (date_created BETWEEN '\", start_date, \"' AND '\", end_date, \"') OR (date_changed BETWEEN '\", start_date, \"' AND '\", end_date, \"') \", \" INTO OUTFILE '\", file_path, \""
					+ schemaName
					+ "_"
					+ table
					+ ".csv' FIELDS TERMINATED BY ',' ENCLOSED BY '\\\"' LINES TERMINATED BY '\\n' \");";
			query = query + "\n prepare s" + count + " from @q" + count + ";";
			query = query + "\n execute s" + count + ";";

			queriesList.add(query);
			count++;
			System.out.println(query + "\n");
		}

		for (String table : tablesWithDateCreated) {

			String query = "SET @q"
					+ count
					+ "=concat(\"SELECT * FROM om_"
					+ table
					+ " WHERE (date_created BETWEEN '\", start_date, \"' AND '\", end_date, \"')\", \" INTO OUTFILE '\", file_path, \""
					+ schemaName
					+ "_"
					+ table
					+ ".csv' FIELDS TERMINATED BY ',' ENCLOSED BY '\\\"' LINES TERMINATED BY '\\n' \");";
			query = query + "\n prepare s" + count + " from @q" + count + ";";
			query = query + "\n execute s" + count + ";";

			queriesList.add(query);
			count++;
			System.out.println(query + "\n");
		}

		for (String table : tablesNotWithDateCreated) {

			String query = "SET @q"
					+ count
					+ "=concat(\"SELECT * FROM om_"
					+ table
					+ "  INTO OUTFILE '\", file_path,\""
					+ schemaName
					+ "_"
					+ table
					+ ".csv' FIELDS TERMINATED BY ',' ENCLOSED BY '\\\"' LINES TERMINATED BY '\\n' \");";
			query = query + "\n prepare s" + count + " from @q" + count + ";";
			query = query + "\n execute s" + count + ";";

			queriesList.add(query);
			count++;
			System.out.println(query + "\n");
		}

	}

	public boolean createStoredProcedure() {

		FileUtil fileUtil = new FileUtil();
		String dropProcedureQuery = "DROP PROCEDURE IF EXISTS `extract_openmrs1`";
		String query = fileUtil.getText("stored_procedures.sql");
		dwDb.runCommand(CommandType.CREATE, dropProcedureQuery);
		Object obj = dwDb.runCommand(CommandType.CREATE, query);
		if (obj == null) {
			System.out.println("Stored procedure has error");
		} else {
			System.out.println("Stored procedure created!");
		}
		return true;

	}
}
