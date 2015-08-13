/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/* Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Error provider class
 */

package com.ihsresearch.tbr4web.shared;

import com.ihsresearch.tbr4web.shared.App;
import com.ihsresearch.tbr4web.shared.ErrorType;
import com.ihsresearch.tbr4web.shared.InfoType;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class CustomMessage
{
	public static String getInfoMessage (InfoType infoType)
	{
		String message = "";
		switch (infoType)
		{
			case ACCESS_GRANTED :
				message = "Access granted, you have successfully logged in.";
				break;
			case CONFIRM_CLOSE :
				message = "Are you sure you want to close the application?";
				break;
			case CONFIRM_OPERATION :
				message = "This operation is irreversable. Are you sure you want to proceed?";
				break;
			case CONNECTION_SUCCESSFUL :
				message = "Connection with the server was successful.";
				break;
			case DELETED :
				message = "Data deleted successfully.";
				break;
			case INSERTED :
				message = "Data inserted successfully.";
				break;
			case OPERATION_SUCCESSFUL :
				message = "Operation was successful.";
				break;
			case SESSION_RENEWED :
				message = "Session has been renewed.";
				break;
			case UPDATED :
				message = "Data updated successfully.";
				break;
			case VALID :
				message = "Data validated.";
				break;
		}
		return message;
	}

	public static String getErrorMessage (ErrorType errorType)
	{
		String error = "";
		switch (errorType)
		{
			case AUTHENTICATION_ERROR :
				error = "Authentication failed! Please enter valid id and password.";
				break;
			case DATA_ACCESS_ERROR :
				error = "Access to data failed! You may not have sufficient rights.";
				break;
			case DATA_CONNECTION_ERROR :
				error = "Could not connect to Data source! Please check your connection settings.";
				break;
			case DATA_MISMATCH_ERROR :
				error = "Data or value(s) did not match!";
				break;
			case DELETE_ERROR :
				error = "Error in deleting data. Some other data may be dependent on the item you want to delete.";
				break;
			case DUPLICATION_ERROR :
				error = "Duplication error! Another copy of the same data exists in the database.";
				break;
			case EMPTY_DATA_ERROR :
				error = "Empty data field! Please fill in the required field(s) first.";
				break;
			case INSERT_ERROR :
				error = "Error in inserting data. Please make sure you are not violating validation rules.";
				break;
			case ITEM_NOT_FOUND :
				error = "Data not found.";
				break;
			case INVALID_DATA_ERROR :
				error = "Invalid data! Please make sure you are not violating validation rules.";
				break;
			case PARAMETER_MISSING :
				error = "One or more Parameters are invalid or missing. Please make sure you have defined valid parameters.";
				break;
			case PARSING_ERROR :
				error = "Data cannot be parsed. Please make sure data is in valid format.";
				break;
			case SESSION_EXPIRED :
				error = "Session expired! Please re-login to continue operation.";
				break;
			case UNKNOWN_ERROR :
				error = "Unknown error encountered! Please check error log for details.";
				break;
			case UPDATE_ERROR :
				error = "Error in updating data. Please make sure you are not violating validation rules.";
				break;
			case USER_NOT_FOUND :
				error = "User not found! Please enter correct user name.";
				break;
			case USER_ROLE_UNDEFINED :
				error = "User role not found or undefined! Please contact Administrator or select a different Role.";
				break;
			case VERSION_MISMATCH_ERROR:
				error = "Version does not match. Make sure your application is updated to version " + App.appVersion;
				break;
			case PATIENT_NOT_FOUND :
				error = "Patient not Found! Please check patient id.";
			case SPUTUM_SUBMISSION_NOT_FOUND:
				error = "Sputum Submission form not found. If form is already submitted, please check the patient/test id.";
				break;
			case NO_ACCEPTED_SPUTUM_SUBMISSION_FOUND:
				error = "Sputum Submission form not found with accept status. If form is already submitted, please check the patient/test id.";
				break;
			case GENEXPERT_RESULT_NOT_FOUND :
				error = "GeneXpert Result not Found. If form is already submitted, please check the patient/test id.";
				break;
			case POSITIVE_GENEXPERT_RESULT_NOT_FOUND :
				error = "GeneXpert Result with positive result not Found. If form is already submitted, please check the patient/test id.";
				break;
			case TREATMENT_ALREADY_INITIATED :
				error = "Treatment already initiated. If not, please check the patient/test id.";
				break;
			case TREATMENT_INITIATION_FORM_NOT_FILLED :
				error = "Treatment Initiation form not submitted. If form is already submitted, please check the patient/test id.";	
				break;
			case TREATMENT_NOT_INITIATED :
				error = "Treatment not initiated. If already initiated, please check the patient/test id.";	
				break;
			case TREATMENT_OUTCOME_ALREADY_FILLED :
				error = "Treatment outcome form already filled. If not, please check the patient/test id.";
				break;
			
		}
		return error;
	}
}
