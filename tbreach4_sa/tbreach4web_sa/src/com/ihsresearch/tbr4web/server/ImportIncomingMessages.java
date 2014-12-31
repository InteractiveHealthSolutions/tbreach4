
package com.ihsresearch.tbr4web.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.ihsresearch.tbr4web.server.util.DateTimeUtil;

public class ImportIncomingMessages
{

	private Date date;
	
	public ImportIncomingMessages ()
	{
		System.out.println ("Importing Incoming Messages from SMSTareseel - STARTS!");
		
		// Import TimeStamp 
		date = new Date();
		String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String stringDate = sdf.format(date );
		String sqlDate = DateTimeUtil.convertToSQL (stringDate, DATE_FORMAT_NOW);
		
		MobileService.getService ().execute ("update openmrs_rpt.metadata set value = '"+sqlDate+"' where type = 'lastUpdate';");
		
		System.out.println (date);

		String dropScreenerDataQuery = "drop table openmrs_rpt.screener_data;";
		MobileService.getService ().execute (dropScreenerDataQuery);
		
		String createScreenerDataQuery 
						= "create table openmrs_rpt.screener_data(" +
								"username VARCHAR(50) NOT NULL," +
								"location VARCHAR(50), " +
								"primaryNumber VARCHAR(20)," +
								"secondaryNumber VARCHAR(20));"; 
		
		MobileService.getService ().execute (createScreenerDataQuery);
		
		String createTemporaryTableQuery 
		               = "create table openmrs_rpt.temporarytable(" +
		               		"originator VARCHAR(20) NOT NULL," +
		               		"referenceNumber VARCHAR(16) NOT NULL," +
		               		"recieveDate datetime NOT NULL," +
		               		"message VARCHAR(20) NOT NULL," +
		               		"dateText VARCHAR(20) NOT NULL," +
		               		"PRIMARY KEY ( referenceNumber ));";
		
		MobileService.getService ().execute (createTemporaryTableQuery);
		
		String insertTemporaryTableQuery
		              = "INSERT INTO openmrs_rpt.temporarytable (originator, referenceNumber, recieveDate, message, dateText)" +
		              		"(select * from " +
		              	     	"(Select originator, referenceNumber, recieveDate, text, DATE_FORMAT(recieveDate, '%y-%m-%d') as Date " +
		              	     	"from smstarseel.inboundmessage where recieveDate >= '2014-09-29 %' and recieveDate <= '2014-09-29 23:59:59.999'" +
		              	     	"order by recieveDate desc) as temp " +
		              	    "where referenceNumber NOT IN (Select referenceNumber from openmrs_rpt.inboundmessages) " +
		              	    "group by Date, originator);";
		
		MobileService.getService ().execute (insertTemporaryTableQuery);
		
		String insertScreenerDataQuery
		               = "INSERT into openmrs_rpt.screener_data (username, location, primaryNumber, secondaryNumber) (" +
		               		"Select table1.username, table2.location, concat('+',table3.pp) as primaryPhoneNUmber , concat('+',table4.sp) as secondaryPhoneNumber from (" +
		               		   "(select p.person_id, username " +
		               		   "from openmrs.users u , openmrs.person p " +
		               		   "where u.person_id = p.person_id and u.retired = 0 and username is not null ) as table1 " +
		               		   "left join " +
		               		   "(select person_id, name as location from openmrs.person_attribute pa, openmrs.location l " +
		               		   "where pa.person_attribute_type_id = 13 and l.location_id = pa.value and pa.voided = 0) as table2 " +
		               		   "ON table1.person_id = table2.person_id " +
		               		   "left join " +
		               		   "(select value as pp, person_id from openmrs.person_attribute pa " +
		               		   "where pa.person_attribute_type_id = 8 and pa.voided = 0) as table3 " +
		               		   "ON table2.person_id = table3.person_id " +
		               		   "left join " +
		               		   "(select value as sp, person_id from openmrs.person_attribute pa " +
		               		   "where pa.person_attribute_type_id = 10 and pa.voided = 0) as table4 " +
		               		   "ON table3.person_id = table4.person_id " +
		               		   ")" +
		               		");";
		
		MobileService.getService ().execute (insertScreenerDataQuery);
		
		String insertInboundMessagesQuery
		              = "insert into openmrs_rpt.inboundmessages(originator, referenceNumber, recieveDate, message, dateText, username, location, primaryNumber, secondaryNumber) " +
		              		"Select * from openmrs_rpt.temporarytable tt, openmrs_rpt.screener_data sd " +
		              		"where tt.originator = sd.primaryNumber OR tt.originator = sd.secondaryNumber;";
		
		MobileService.getService ().execute (insertInboundMessagesQuery);
		
		String dropTemporarayTableQuery = "drop table openmrs_rpt.temporarytable;";
		
		MobileService.getService ().execute (dropTemporarayTableQuery);
		
		System.out.println ("Importing Incoming Messages from SMSTareseel - ENDS");

	}

	public void setDate (Date date)
	{
		this.date = date;
	}

	public Date getDate ()
	{
		return date;
	}
}
