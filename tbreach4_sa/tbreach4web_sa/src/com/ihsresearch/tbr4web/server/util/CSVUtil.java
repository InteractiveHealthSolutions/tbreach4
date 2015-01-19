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
package com.ihsresearch.tbr4web.server.util;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CSVUtil {

	
	public static void makeCsv(ZipOutputStream zip , String[][] record, String form) throws IOException {
       
		BufferedWriter writer = new BufferedWriter(
				   new OutputStreamWriter(zip, Charset.forName("utf-8"))
				   );	
		 if(form == "SMS"){	
		   ZipEntry entry = new ZipEntry("smsscreeningdata.csv");
		   zip.putNextEntry(entry);
           writer.append("\"Screener Name\",\"Facility\",\"Phone Number\",\"System TimeStamp\",\"Total Screened\",\"Suspect\",\"Non-Suspect\",\"Sputum Submitted\"");
		 
         writer.append('\n');	
         int totalPatientScreenedPerDay = 0;
		 int totalSuspectPerDay = 0;
		 int totalNonsuspectPerDay = 0;
		 int totalSputumSentPerDay = 0;
		 
		 int totalPatientScreenedOverall = 0;
		 int totalSuspectOverall = 0;
		 int totalNonsuspectOverall = 0;
		 int totalSputumSentOverall = 0;
		 
         if (record != null) {

				for (int i = 0; i < record.length; i++) {
					
					String originator = record[i][0];
					String recieveDate = record[i][2];
					String text = record[i][3];
					String date = record[i][4];
					String name = record[i][5];
					String facility = record[i][6];
					int totalScreened = 0;
					int suspect = 0;
					int nonSuspect = 0;
					int sputumSent = 0;
					
					String[] tokens = text.split("\\.|,|\\s|:|-");
					if(tokens.length != 4){
						   text = text.replaceAll(" ", "").trim();
						   tokens = text.split("\\.|,|\\s|:|-");
					}
						
				    if (tokens.length != 4){
				    	int j = i+1;
				    	if(!(j<record.length)){
				    		writer.append(("\"\""));
							writer.append(',');
							writer.append(("\"\""));
							writer.append(',');
							writer.append(("\"\""));
							writer.append(',');
							writer.append(("\"Total\""));
							writer.append(',');
							writer.append(("\""+totalPatientScreenedPerDay+"\""));
							writer.append(',');
							writer.append(("\""+totalSuspectPerDay+"\""));
							writer.append(',');
							writer.append(("\""+totalNonsuspectPerDay+"\""));
							writer.append(',');
							writer.append(("\""+totalSputumSentPerDay+"\""));
							writer.append(',');
							
							writer.append('\n');
							
							totalPatientScreenedOverall = totalPatientScreenedOverall  + totalPatientScreenedPerDay;
							totalSuspectOverall = totalSuspectOverall  + totalSuspectPerDay;
							totalNonsuspectOverall = totalNonsuspectOverall  + totalNonsuspectPerDay;
							totalSputumSentOverall = totalSputumSentOverall  + totalSputumSentPerDay;
							
							totalPatientScreenedPerDay = 0;
							totalSuspectPerDay = 0;
							totalNonsuspectPerDay = 0;
							totalSputumSentPerDay = 0;
				    	}
				    	continue;
				    }
					
				    try {
						totalScreened = Integer.valueOf(tokens[0]);
						suspect = Integer.valueOf(tokens[1]);
						nonSuspect = Integer.valueOf(tokens[2]);
						sputumSent = Integer.valueOf(tokens[3]);
					} catch (Exception e) {
						int j = i+1;
				    	if(!(j<record.length)){
				    		writer.append(("\"\""));
							writer.append(',');
							writer.append(("\"\""));
							writer.append(',');
							writer.append(("\"\""));
							writer.append(',');
							writer.append(("\"Total\""));
							writer.append(',');
							writer.append(("\""+totalPatientScreenedPerDay+"\""));
							writer.append(',');
							writer.append(("\""+totalSuspectPerDay+"\""));
							writer.append(',');
							writer.append(("\""+totalNonsuspectPerDay+"\""));
							writer.append(',');
							writer.append(("\""+totalSputumSentPerDay+"\""));
							writer.append(',');
							
							writer.append('\n');
							
							totalPatientScreenedOverall = totalPatientScreenedOverall  + totalPatientScreenedPerDay;
							totalSuspectOverall = totalSuspectOverall  + totalSuspectPerDay;
							totalNonsuspectOverall = totalNonsuspectOverall  + totalNonsuspectPerDay;
							totalSputumSentOverall = totalSputumSentOverall  + totalSputumSentPerDay;
							
							totalPatientScreenedPerDay = 0;
							totalSuspectPerDay = 0;
							totalNonsuspectPerDay = 0;
							totalSputumSentPerDay = 0;
				    	}
						continue;
					}
				    
					writer.append(("\""+name+"\""));
					writer.append(',');
					writer.append(("\""+facility+"\""));
					writer.append(',');
					writer.append(("\""+originator+"\""));
					writer.append(',');
					writer.append(("\""+recieveDate+"\""));
					writer.append(',');
					writer.append(("\""+totalScreened+"\""));
					writer.append(',');
					writer.append(("\""+suspect+"\""));
					writer.append(',');
					writer.append(("\""+nonSuspect+"\""));
					writer.append(',');
					writer.append(("\""+sputumSent+"\""));
					writer.append(',');
					
					writer.append('\n');
					
					totalPatientScreenedPerDay = totalPatientScreenedPerDay + totalScreened;
					totalSuspectPerDay = totalSuspectPerDay + suspect;
					totalNonsuspectPerDay = totalNonsuspectPerDay + nonSuspect;
					totalSputumSentPerDay = totalSputumSentPerDay + sputumSent;
					
					boolean flag = true;
					int j = i+1;
					
					if(!(j<record.length)){
						flag = false;
					}
					else if(!(date.equals (record[i+1][4]))){
						flag = false;
					}
					
					
					if(!flag){
						writer.append(("\"\""));
						writer.append(',');
						writer.append(("\"\""));
						writer.append(',');
						writer.append(("\"\""));
						writer.append(',');
						writer.append(("\"Total\""));
						writer.append(',');
						writer.append(("\""+totalPatientScreenedPerDay+"\""));
						writer.append(',');
						writer.append(("\""+totalSuspectPerDay+"\""));
						writer.append(',');
						writer.append(("\""+totalNonsuspectPerDay+"\""));
						writer.append(',');
						writer.append(("\""+totalSputumSentPerDay+"\""));
						writer.append(',');
						
						writer.append('\n');
						
						totalPatientScreenedOverall = totalPatientScreenedOverall  + totalPatientScreenedPerDay;
						totalSuspectOverall = totalSuspectOverall  + totalSuspectPerDay;
						totalNonsuspectOverall = totalNonsuspectOverall  + totalNonsuspectPerDay;
						totalSputumSentOverall = totalSputumSentOverall  + totalSputumSentPerDay;
						
						totalPatientScreenedPerDay = 0;
						totalSuspectPerDay = 0;
						totalNonsuspectPerDay = 0;
						totalSputumSentPerDay = 0;
					}
					
				}
				
				writer.append(("\"\""));
				writer.append(',');
				writer.append(("\"\""));
				writer.append(',');
				writer.append(("\"\""));
				writer.append(',');
				writer.append(("\"Total (Overall)\""));
				writer.append(',');
				writer.append(("\""+totalPatientScreenedOverall+"\""));
				writer.append(',');
				writer.append(("\""+totalSuspectOverall+"\""));
				writer.append(',');
				writer.append(("\""+totalNonsuspectOverall+"\""));
				writer.append(',');
				writer.append(("\""+totalSputumSentOverall+"\""));
				writer.append(',');
				
				writer.append('\n');
			
         }
         
         	
		
		 writer.flush(); // i've used a buffered writer, so make sure to flush to the
		// underlying zip output stream

		 zip.closeEntry();
		 zip.finish();

		//reader.close(); 
		 writer.close();
    }
		 else if(form == "OMRS"){
			 ZipEntry entry = new ZipEntry("smsscreeningdata.csv");
			   zip.putNextEntry(entry);
	           writer.append("\"Username\",\"System TimeStamp\",\"Total Screened\",\"Suspect\",\"Non-Suspect\",\"Sputum Submitted\"");
			 
	         writer.append('\n');	
	         int totalPatientScreenedPerDay = 0;
			 int totalSuspectPerDay = 0;
			 int totalNonsuspectPerDay = 0;
			 int totalSputumSentPerDay = 0;
			 
			 int totalPatientScreenedOverall = 0;
			 int totalSuspectOverall = 0;
			 int totalNonsuspectOverall = 0;
			 int totalSputumSentOverall = 0;
			 
	         if (record != null) {

					for (int i = 0; i < record.length; i++) {
						
						String totalScreened = record[i][0];
						String suspect = record[i][1];
						String nonSuspect = record[i][2];
						String sputumSubmitted = record[i][3];
						String username = record[i][4];
						String date = record[i][5];
						int ts = 0;
						if(totalScreened != null)
							ts = Integer.valueOf(totalScreened);
						int s = Integer.valueOf(suspect);
						int ns = Integer.valueOf(nonSuspect);
						int ss = 0;
						if(sputumSubmitted != null)
							ss = Integer.valueOf (sputumSubmitted);
							
					    
						writer.append(("\""+username+"\""));
						writer.append(',');
						writer.append(("\""+date+"\""));
						writer.append(',');
						writer.append(("\""+ts+"\""));
						writer.append(',');
						writer.append(("\""+s+"\""));
						writer.append(',');
						writer.append(("\""+ns+"\""));
						writer.append(',');
						writer.append(("\""+ss+"\""));
						writer.append(',');
						
						writer.append('\n');
						
						totalPatientScreenedPerDay = totalPatientScreenedPerDay + ts;
						totalSuspectPerDay = totalSuspectPerDay + s;
						totalNonsuspectPerDay = totalNonsuspectPerDay + ns;
						totalSputumSentPerDay = totalSputumSentPerDay + ss;
						
						boolean flag = true;
						int j = i+1;
						
						if(!(j<record.length)){
							flag = false;
						}
						else if(!(date.equals (record[i+1][5]))){
							flag = false;
						}
						
						
						if(!flag){
							
							writer.append(("\"\""));
							writer.append(',');
							writer.append(("\"Total\""));
							writer.append(',');
							writer.append(("\""+totalPatientScreenedPerDay+"\""));
							writer.append(',');
							writer.append(("\""+totalSuspectPerDay+"\""));
							writer.append(',');
							writer.append(("\""+totalNonsuspectPerDay+"\""));
							writer.append(',');
							writer.append(("\""+totalSputumSentPerDay+"\""));
							writer.append(',');
							
							writer.append('\n');
							
							totalPatientScreenedOverall = totalPatientScreenedOverall  + totalPatientScreenedPerDay;
							totalSuspectOverall = totalSuspectOverall  + totalSuspectPerDay;
							totalNonsuspectOverall = totalNonsuspectOverall  + totalNonsuspectPerDay;
							totalSputumSentOverall = totalSputumSentOverall  + totalSputumSentPerDay;
							
							totalPatientScreenedPerDay = 0;
							totalSuspectPerDay = 0;
							totalNonsuspectPerDay = 0;
							totalSputumSentPerDay = 0;
						}
						
					}
					
					
					writer.append(("\"\""));
					writer.append(',');
					writer.append(("\"Total (Overall)\""));
					writer.append(',');
					writer.append(("\""+totalPatientScreenedOverall+"\""));
					writer.append(',');
					writer.append(("\""+totalSuspectOverall+"\""));
					writer.append(',');
					writer.append(("\""+totalNonsuspectOverall+"\""));
					writer.append(',');
					writer.append(("\""+totalSputumSentOverall+"\""));
					writer.append(',');
					
					writer.append('\n');
				
	         }
	         
	         	
			
			 writer.flush(); // i've used a buffered writer, so make sure to flush to the
			// underlying zip output stream

			 zip.closeEntry();
			 zip.finish();

			//reader.close(); 
			 writer.close();
		 }
}
		
}
