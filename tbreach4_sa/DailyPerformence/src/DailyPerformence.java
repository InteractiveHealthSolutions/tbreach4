import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;


public class DailyPerformence {

	public static void main(String[] args) {
	
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		//String dateTo = dateFormat.format(cal.getTime());
		String dateTo = "2015-09-07";
		
		//Check for Weekend
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
		    System.out.println("Weekend!");
		    return;
		}
		
		//Check for other holidays
		String getHolidays = "Select * from openmrs_rpt.holiday where date like '"+dateTo+"';";
		String[][] holidays = DatabaseUtil.getDbCon().executeQuery (getHolidays, null);
		
		if(holidays.length != 0){
			 System.out.println("Holiday! " + dateTo);
			 return;
		}
		
		// Keep backup
		String query = "insert into openmrs_rpt.daily_feedback_message_backup (screener_id, date, message, total_screened, total_sputum_collected, total_suspect, percentage, sent) "+
		                             "Select screener_id, date, message, total_screened, total_sputum_collected, total_suspect, percentage, sent from openmrs_rpt.daily_feedback_message";
		DatabaseUtil.getDbCon().execute (query);
		
		//truncate table
		String truncateQuery = "truncate table openmrs_rpt.daily_feedback_message";
		DatabaseUtil.getDbCon().execute (truncateQuery);
		
		//Get all screeners
		String userList = "Select username from openmrs.users where username like '0%' and retired = 0";
		String[][] users  = DatabaseUtil.getDbCon().executeQuery (userList, null);
		
		// for evrery screener
		for(int count = 0;count<users.length; count ++) {
			
			String username = users[count][0]; 
			//String username = "01006";
			
			// get today's screening and sputums submitted numbers
			String selectQuery2 = "select IFNULL(SUM(case when (e.encounter_type = 1) then 1 else 0 end),0) , IFNULL(SUM(case when (e.encounter_type = 2) then 1 else 0 end),0) , IFNULL(SUM(case when ( pa.value = 'Suspect' and e.encounter_type = 1 ) then 1 else 0 end),0) " + 
					"from openmrs.encounter e , openmrs.encounter_provider ep, openmrs.provider p, openmrs.person_attribute pa  " +
					"where e.encounter_datetime = '"+dateTo+" %' and e.encounter_id = ep.encounter_id and ep.provider_id = p.provider_id and p.identifier = '"+username+"' and ep.voided = 0 and (e.encounter_type = 1 or e.encounter_type = 2) and e.patient_id = pa.person_id and pa.person_attribute_type_id = 12;";

			String[][] data2  = DatabaseUtil.getDbCon().executeQuery (selectQuery2, null);
			
			String querySputumFromScreeningToday = "select count(distinct e.patient_id) from openmrs.encounter e , openmrs.encounter_provider ep, openmrs.provider p "+
														"where e.encounter_datetime = '"+dateTo+" %' and e.encounter_id = ep.encounter_id and ep.provider_id = p.provider_id and p.identifier = '"+username+"' and ep.voided = 0 and e.encounter_type = 2 and e.patient_id IN ( " +
																"select e.patient_id from openmrs.encounter e , openmrs.encounter_provider ep, openmrs.provider p, openmrs.person_attribute pa " +
																	"where e.encounter_datetime = '"+dateTo+" %' and e.encounter_id = ep.encounter_id and ep.provider_id = p.provider_id and p.identifier = '"+username+"' and ep.voided = 0 and e.encounter_type = 1 and e.patient_id = pa.person_id and pa.person_attribute_type_id = 12 and pa.value = 'Suspect');";
			String[][] pidCount  = DatabaseUtil.getDbCon().executeQuery (querySputumFromScreeningToday, null);
			
			// calculaion 
			float percentage = 0;
			if(!(pidCount[0][0].equals("0") || data2[0][2].equals("0"))){
				 percentage = (Integer.parseInt(pidCount[0][0]) * 100/ Integer.parseInt(data2[0][2]));
			}
			int screenedToday = Integer.parseInt(data2[0][0]);
			int sputumSubmittedToday = Integer.parseInt(data2[0][1]);
			int suspectToday = Integer.parseInt(data2[0][2]);
			
			String message = "";
			String insertQuery = "";
			
			// Messages...
			if(percentage >= 80)
				message = "Great job! Yesterday you got sputum samples from at least 80% of the suspects you screened! :-)";
			else if (data2[0][2].equals("0"))
				message = "";
			else if (percentage <= 50)
				message =  "Eish! You got sputum samples from less than 50% of suspects yesterday. Your target is 100%.";
		
			
			if(screenedToday > 50)
				message = message + ":;:" + "Shap shap! You screened over 50 people yesterday!";
			else if (screenedToday < 20)
				message = message + ":;:" + "You need to screen more. You screened under 20 people yesterday. ";
			
			if(sputumSubmittedToday > 10)
				message = message + ":;:" + "Awesome! You collected more than 10 sputum samples yesterday! :-)";
			else if (sputumSubmittedToday < 4)
				message = message + ":;:" + "Try harder- you collected fewer than 4 sputum samples yesterday.";
			
			// insert
			insertQuery = "insert into openmrs_rpt.daily_feedback_message (screener_id, date, message, total_screened, total_sputum_collected, total_suspect, percentage, sputum_Submitted_of_screened_today) values ('"+username+"','"+dateTo+"','"+message+"',"+screenedToday+","+sputumSubmittedToday+","+suspectToday+","+percentage+","+pidCount[0][0]+");";
			DatabaseUtil.getDbCon().execute (insertQuery);
			
		}
		
		
		System.out.println("End");
		
	}

	

}



















