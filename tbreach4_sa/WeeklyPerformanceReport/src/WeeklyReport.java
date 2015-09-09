
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


public class WeeklyReport {

	public static void main(String[] args) {
	
		String zipFileName = "";		
		//String workingdirectory = System.getProperty("user.dir");
		String workingdirectory = "C:\\Users\\CellDatabase";
		//String workingdirectory = "C:\\Users\\Rabbia";
		workingdirectory = workingdirectory+"\\Documents";
		String tomcatDirectory = "C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\Openmrs";
		
		// Assume it's Sunday Evening
		
		File directory = new File(workingdirectory+"\\WeeklyReports");
		if (!directory.exists()) {
			if (directory.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}else{
			 try {
				delete(directory);
				if (directory.mkdir()) {
					System.out.println("Directory is created!");
				} else {
					System.out.println("Failed to create directory!");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String today = dateFormat.format(cal.getTime());
		cal.add(Calendar.DATE, -1);
		String dateTo = dateFormat.format(cal.getTime());
		cal.add(Calendar.DATE, -6);
		String dateFrom = dateFormat.format(cal.getTime());
		
		String insertQuery = "insert into openmrs_rpt.working_weeks (week_no,date_start,date_end) " +
		                             "values (WEEKOFYEAR('"+dateTo+"'),'"+dateFrom+"','"+dateTo+"')";
								
		DatabaseUtil.getDbCon().execute (insertQuery);
		
		String query = "truncate table openmrs_rpt.weekly_screener_summary";
		DatabaseUtil.getDbCon().execute (query);
		
		/*String dateTo = "2015-07-25";
		String dateFrom = "2015-07-19";
		*/
		String userList = "Select username from openmrs.users where username like '0%' and retired = 0";
		String[][] users  = DatabaseUtil.getDbCon().executeQuery (userList, null);
		
		String weekList = "Select week_no, date_start, date_end from openmrs_rpt.working_weeks";
		String[][] weeks  = DatabaseUtil.getDbCon().executeQuery (weekList, null);
		
		for(int count = 0;count<users.length; count ++) {
	
			String username = users[count][0]; 
		
			for(int x = 0;x<weeks.length; x ++) {
			
				insertQuery = "insert into openmrs_rpt.weekly_screener_summary (no_of_suspects, no_of_screening, no_of_sputum_submitted, no_of_sputum_result, no_of_mtb_pos, no_of_treatment_initiated, username, week_no) " +
										"select IFNULL(SUM(case when ( pa.value = 'Suspect' and e.encounter_type = 1 ) then 1 else 0 end),0) , IFNULL(SUM(case when e.encounter_type = 1 then 1 else 0 end),0) ,  IFNULL(SUM(case when e.encounter_type = 2 then 1 else 0 end),0) , IFNULL(SUM(case when e.encounter_type = 3 then 1 else 0 end),0),0, 0 ,'"+username+"', WEEKOFYEAR('"+weeks[x][2]+"') " +
										"from openmrs.encounter e , openmrs.encounter_provider ep, openmrs.provider p, openmrs.person_attribute pa " +
										"where e.encounter_datetime >= '"+weeks[x][1]+" %' and e.encounter_datetime <= '"+weeks[x][2]+" %' and e.encounter_id = ep.encounter_id and ep.provider_id = p.provider_id and p.identifier = '"+username+"' and ep.voided = 0 and e.patient_id = pa.person_id and pa.person_attribute_type_id = 12;";
				
				DatabaseUtil.getDbCon().execute (insertQuery);
				
				String selectQuery = "select IFNULL(SUM(case when (o.value_coded = 71 and e.encounter_type = 3 and o.concept_id = 75) then 1 else 0 end),0) , IFNULL(SUM(case when (o.value_coded = 1 and e.encounter_type = 4 and o.concept_id = 99) then 1 else 0 end),0) " +
									"from openmrs.encounter e , openmrs.encounter_provider ep, openmrs.provider p, openmrs.person_attribute pa, openmrs.obs o " +
									"where e.encounter_datetime >= '"+weeks[x][1]+" %' and e.encounter_datetime <= '"+weeks[x][2]+" %' and e.encounter_id = ep.encounter_id and ep.provider_id = p.provider_id and p.identifier = '"+username+"' and ep.voided = 0 and e.patient_id = pa.person_id and pa.person_attribute_type_id = 12 and e.encounter_id = o.encounter_id and (e.encounter_type = 3 or e.encounter_type = 4) and (o.concept_id = 99 or o.concept_id = 75);";
		
				String[][] data  = DatabaseUtil.getDbCon().executeQuery (selectQuery, null);
				
				String updateQuery = "Update openmrs_rpt.weekly_screener_summary" +
									 " set no_of_mtb_pos = " + data[0][0] +
									 " , no_of_treatment_initiated = " + data[0][1] +
									 " where username = '"+username+"' and week_no = WEEKOFYEAR('"+weeks[x][2]+"')" ;
				
				DatabaseUtil.getDbCon().execute(updateQuery);
			
			}
			
			String getScreenerReportData = "select * from openmrs_rpt.weekly_screener_summary where username = '"+username+"' order by week_no asc;";
			ResultSet resultSetData = DatabaseUtil.getDbCon().executeQueryResultSet(getScreenerReportData);
			
			try{
				// Make Jasper Report
				JRResultSetDataSource resultSource = new JRResultSetDataSource (resultSetData);	
				
				String getScreenerNameQuery = "SELECT pn.given_name, pn.family_name FROM openmrs.users u, openmrs.person_name pn where username = '"+username+"' and u.person_id = pn.person_id;";
				String[][] data1  = DatabaseUtil.getDbCon().executeQuery (getScreenerNameQuery, null);
				
				String[][] dataArray =  DatabaseUtil.getDbCon().executeQuery (getScreenerReportData, null);
				
				int firstQuraterTotalSputumResult = 0;
				int firstQuratertotalMtbPositive = 0;
				int firstQuratertotalTreatmentInitiated = 0;
				
				int secondQuraterTotalSputumResult = 0;
				int secondQuratertotalMtbPositive = 0;
				int secondQuratertotalTreatmentInitiated = 0;
				
				int thirdQuraterTotalSputumResult = 0;
				int thirdQuratertotalMtbPositive = 0;
				int thirdQuratertotalTreatmentInitiated = 0;
				
				int fourthQuraterTotalSputumResult = 0;
				int fourthQuratertotalMtbPositive = 0;
				int fourthQuratertotalTreatmentInitiated = 0;
				
				for(int o=0; o<dataArray.length; o++){
					
					int week = Integer.parseInt(dataArray[o][7]);
					int sputumResult = Integer.parseInt(dataArray[o][3]);
					int mtbPos = Integer.parseInt(dataArray[o][4]);
					int treatmentInitiated = Integer.parseInt(dataArray[o][5]);
					
					if(week >= 1 && week <= 13){
						
						firstQuraterTotalSputumResult = firstQuraterTotalSputumResult + sputumResult;
						firstQuratertotalMtbPositive = firstQuratertotalMtbPositive + mtbPos;
						firstQuratertotalTreatmentInitiated = firstQuratertotalTreatmentInitiated + treatmentInitiated;
						
					}else if(week >= 14 && week <= 26){
						
						secondQuraterTotalSputumResult = secondQuraterTotalSputumResult + sputumResult;
						secondQuratertotalMtbPositive = secondQuratertotalMtbPositive + mtbPos;
						secondQuratertotalTreatmentInitiated = secondQuratertotalTreatmentInitiated + treatmentInitiated;
						
					}else if(week >= 27 && week <= 39){
						
						thirdQuraterTotalSputumResult = thirdQuraterTotalSputumResult + sputumResult;
						thirdQuratertotalMtbPositive = thirdQuratertotalMtbPositive + mtbPos;
						thirdQuratertotalTreatmentInitiated = thirdQuratertotalTreatmentInitiated + treatmentInitiated;
						
					}else if(week >= 40 && week <= 52){
						
						fourthQuraterTotalSputumResult = fourthQuraterTotalSputumResult + sputumResult;
						fourthQuratertotalMtbPositive = fourthQuratertotalMtbPositive + mtbPos;
						fourthQuratertotalTreatmentInitiated = fourthQuratertotalTreatmentInitiated + treatmentInitiated;
					}
				}
				
				double percentage = 0.0;
				DecimalFormat df = new DecimalFormat("#.##");
				
				Map hashMap = new HashMap();    
				hashMap.put("screener_name", data1[0][0]+" "+data1[0][1]);
				
				//first quarter info
				hashMap.put("first_q_mtb_pos_no", String.valueOf(firstQuratertotalMtbPositive));
				if(firstQuraterTotalSputumResult != 0){
					percentage = ((double)firstQuratertotalMtbPositive/firstQuraterTotalSputumResult)*100;
					hashMap.put("first_q_mtb_pos_per", df.format(percentage)+" %");
				}
				else 
					hashMap.put("first_q_mtb_pos_per", "0.0 %");
				hashMap.put("first_q_treatment_no", String.valueOf(firstQuratertotalTreatmentInitiated));
				if(firstQuratertotalMtbPositive != 0){
					percentage = ((double)firstQuratertotalTreatmentInitiated/firstQuratertotalMtbPositive)*100;
					hashMap.put("first_q_treatment_per", df.format(percentage)+" %");
				}	
				else
					hashMap.put("first_q_treatment_per", "0.0 %");
				
				//second quarter info
				hashMap.put("second_q_mtb_pos_no", String.valueOf(secondQuratertotalMtbPositive));
				if(secondQuraterTotalSputumResult != 0){
					percentage = ((double)secondQuratertotalMtbPositive/secondQuraterTotalSputumResult)*100;
					hashMap.put("second_q_mtb_pos_per", df.format(percentage)+" %");
				}
				else 
					hashMap.put("second_q_mtb_pos_per", "0.0 %");
				hashMap.put("second_q_treatment_no", String.valueOf(secondQuratertotalTreatmentInitiated));
				if(secondQuratertotalMtbPositive != 0){
					percentage = ((double)secondQuratertotalTreatmentInitiated/secondQuratertotalMtbPositive)*100;
					hashMap.put("second_q_treatment_per", df.format(percentage)+" %");
				}
				else
					hashMap.put("second_q_treatment_per", "0.0 %");
				
				//third quarter info
				hashMap.put("third_q_mtb_pos_no", String.valueOf(thirdQuratertotalMtbPositive));
				if(thirdQuraterTotalSputumResult != 0){
					percentage = ((double)thirdQuratertotalMtbPositive/thirdQuraterTotalSputumResult)*100;
					hashMap.put("third_q_mtb_pos_per", df.format(percentage)+" %");
				}	
				else 
					hashMap.put("third_q_mtb_pos_per", "0.0 %");
				hashMap.put("third_q_treatment_no", String.valueOf(thirdQuratertotalTreatmentInitiated));
				if(thirdQuratertotalMtbPositive != 0){
					percentage = ((double)thirdQuratertotalTreatmentInitiated/thirdQuratertotalMtbPositive)*100;
					hashMap.put("third_q_treatment_per", df.format(percentage)+" %");
				}
				else
					hashMap.put("third_q_treatment_per", "0.0 %");
				
				//fourth quarter info
				hashMap.put("fourth_q_mtb_pos_no", String.valueOf(fourthQuratertotalMtbPositive));
				if(fourthQuraterTotalSputumResult != 0){
					percentage = ((double)fourthQuratertotalMtbPositive/fourthQuraterTotalSputumResult)*100;
					hashMap.put("fourth_q_mtb_pos_per", df.format(percentage)+" %");
				}	
				else 
					hashMap.put("fourth_q_mtb_pos_per", "0.0 %");
				hashMap.put("fourth_q_treatment_no", String.valueOf(fourthQuratertotalTreatmentInitiated));
				if(fourthQuratertotalMtbPositive != 0){
					percentage = ((double)fourthQuratertotalTreatmentInitiated/fourthQuratertotalMtbPositive)*100;
					hashMap.put("fourth_q_treatment_per", df.format(percentage)+" %");
				}	
				else
					hashMap.put("fourth_q_treatment_per", "0.0 %");
				
				JasperReport jasperReport = JasperCompileManager.compileReport (workingdirectory+"\\WeeklySummary.jrxml");
				JasperPrint print = JasperFillManager.fillReport (jasperReport, hashMap, resultSource);
				String dest = workingdirectory+"\\WeeklyReports\\"+username+".pdf"; 
				
				// Delete file if existing
				try
				{
					File file = new File (dest);
					file.delete ();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				JRAbstractExporter exporter;
				exporter = new JRPdfExporter ();
				//exporter = new JRHtmlExporter ();
				exporter.setParameter (JRExporterParameter.JASPER_PRINT, print);
				exporter.setParameter (JRExporterParameter.OUTPUT_FILE, new File (dest));
				exporter.exportReport ();	
				
			} catch (JRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
		zipFileName = tomcatDirectory+"\\WeeklyReports-"+ today +".zip";
		
		 try {
			zipFolder(workingdirectory+"\\WeeklyReports", zipFileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*// SMTP info
        String host = "smtp.gmail.com";
        String port = "587";
        String mailFrom = "";
        String password = "";
 
        String emailQuery = "select value from openmrs_rpt.metadata where type = 'email';";
        String[][] emailData  = DatabaseUtil.getDbCon().executeQuery (emailQuery, null);
        
        for(int m = 0; m<emailData.length; m++){
        	
        	 // message info
        	String mailTo = emailData[m][0];
        	String subject = "Weekly Report ("+dateFrom+" - "+dateTo+")";
            String message = "Autogenerated - Weekly Report ("+dateFrom+" - "+dateTo+")";
     
            // attachments
            String[] attachFiles = new String[1];
            attachFiles[0] = zipFileName;
     
            try {
                EmailAttachmentSender.sendEmailWithAttachments(host, port, mailFrom, password, mailTo,
                    subject, message, attachFiles);
                System.out.println("Email sent.");
            } catch (Exception ex) {
                System.out.println("Could not send email.");
                ex.printStackTrace();
            }
        }*/
		
	}

	
	static public void zipFolder(String srcFolder, String destZipFile) throws Exception {
	    ZipOutputStream zip = null;
	    FileOutputStream fileWriter = null;

	    fileWriter = new FileOutputStream(destZipFile);
	    zip = new ZipOutputStream(fileWriter);

	    addFolderToZip("", srcFolder, zip);
	    zip.flush();
	    zip.close();
	  }

	  static private void addFileToZip(String path, String srcFile, ZipOutputStream zip)
	      throws Exception {

	    File folder = new File(srcFile);
	    if (folder.isDirectory()) {
	      addFolderToZip(path, srcFile, zip);
	    } else {
	      byte[] buf = new byte[1024];
	      int len;
	      FileInputStream in = new FileInputStream(srcFile);
	      zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
	      while ((len = in.read(buf)) > 0) {
	        zip.write(buf, 0, len);
	      }
	    }
	  }

	  static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
	      throws Exception {
	    File folder = new File(srcFolder);

	    for (String fileName : folder.list()) {
	      if (path.equals("")) {
	        addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
	      } else {
	        addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
	      }
	    }
	  }	
	  
	  
	  
	  public static void delete(File file)
		    	throws IOException{
		  
		  String[]entries = file.list();
		  for(String s: entries){
		      File currentFile = new File(file.getPath(),s);
		      currentFile.delete();
		  }
		  file.delete();	
		  
	  }
	  
}



















