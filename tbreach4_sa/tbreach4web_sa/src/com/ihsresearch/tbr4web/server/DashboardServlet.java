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

package com.ihsresearch.tbr4web.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipOutputStream;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.ihsresearch.tbr4web.server.util.CSVUtil;
import ca.uhn.hl7v2.util.MessageQuery.Result;

public class DashboardServlet extends HttpServlet
{

	/**
	 * 
	 */

	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		String reqType = request.getParameter ("reqType");
		System.out.println (reqType);

		if (reqType.equals ("Login"))
		{
			doLogin (request, response);
		}
		else if (reqType.equals ("Logout"))
		{
			doLogout (request, response);
		}
		else if (reqType.equals ("Import"))
		{
			doImport (request, response);
		}
		else if (reqType.equals ("View"))
		{
			doView (request, response);
		}
		else if (reqType.equals ("Edit"))
		{
			doEdit (request, response);
		}
		else if (reqType.equals ("Add"))
		{
			doAdd (request, response);
		}
		else if (reqType.equals ("GetSSSMS"))
		{
			doSmsScreeningStatistics (request, response);
		}
		else if (reqType.equals ("GetSSOMRS"))
		{
			doOpenMrsScreeningStatistics (request, response);
		}

	}

	private void doLogin (HttpServletRequest request, HttpServletResponse response)
	{

		try
		{

			String result = "";
			RequestDispatcher rd;
			String username = request.getParameter ("username");
			String password = request.getParameter ("password");
			result = MobileService.getService ().authenticate (username, password);

			if (result.equals ("SUCCESS"))
			{
				HttpSession userSession = request.getSession ();
				userSession.setAttribute ("username", username);
				userSession.setAttribute ("password", password);
				userSession.setMaxInactiveInterval (30 * 60);

				rd = request.getRequestDispatcher ("/dashboard.jsp");
				
				// Session creation!
                Cookie loginCookie = new Cookie("dashboard_user",username);
                //setting cookie to expiry in 30 mins
                loginCookie.setMaxAge(30*60);
                response.addCookie(loginCookie);

				rd.forward (request, response);

			}
			else
			{
				request.setAttribute ("authentic", "failed");
				rd = request.getRequestDispatcher ("login.jsp");

				rd.include (request, response);
			}

		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
	}
	
	private void doLogout (HttpServletRequest request, HttpServletResponse response)
	{

		try{
			
			String dest="/login.jsp";
			RequestDispatcher rd;
	         
	        Cookie loginCookie = null;
	    	Cookie[] cookies = request.getCookies();
	    	if(cookies != null){
	    	for(Cookie cookie : cookies){
	    		if(cookie.getName().equals("dashboard_user")){
	    			loginCookie = cookie;
	    			break;
	    		}
	    	}
	    	}
	    	if(loginCookie != null){
	    		loginCookie.setMaxAge(0);
	        	response.addCookie(loginCookie);
	    	}    
	        
		    rd=getServletContext().getRequestDispatcher(dest);
		    rd.forward(request, response);
		    return;
		    
		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
	}
	
	private void doImport (HttpServletRequest request, HttpServletResponse response)
	{
		
		renewCookie (request, response);
		
		try
		{

		   RequestDispatcher rd;
			
		   ImportIncomingMessages sp = new ImportIncomingMessages();	
		   request.setAttribute ("imported", "success");
		   rd = request.getRequestDispatcher ("importMessages.jsp");
           rd.include (request, response);
		

		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
	}
	
	private void doView (HttpServletRequest request, HttpServletResponse response)
	{

		renewCookie (request, response);
		
		try
		{

		   RequestDispatcher rd;
		
		   String dateFrom = request.getParameter ("dateFrom");
		   String dateTo = request.getParameter ("dateTo");
		   String username = request.getParameter ("usernames");
		   String location = request.getParameter ("locations");
		   
		   String filter = " and recieveDate >= '"+dateFrom+" %' and recieveDate <= '"+dateTo+ " 23:59:59.999'";
		   if(username != "" && username != null){
			   filter = filter + " and username = '"+username+"'";
			   request.setAttribute ("username", username);
		   }
		   
		   if(location != "" && location != null){
			   filter = filter + " and location = '"+location+"'";
			   request.setAttribute ("location", location);
		   }
		   
		   String data[][] = MobileService.getService ().getAllMessages (filter);
		   int size = data.length;
		   
		   request.setAttribute ("dateFrom", dateFrom);
		   request.setAttribute ("dateTo", dateTo);
		   request.setAttribute ("size", size);
		   
		   for (int i = 0; i<size; i++){
			   String user = data[i][13]+" ("+data[i][5]+")";
			   request.setAttribute ("phoneNumber-"+i, data[i][0]);
			   request.setAttribute ("referenceNumber-"+i, data[i][1]);
			   request.setAttribute ("recieveDate-"+i, data[i][2]);
			   request.setAttribute ("message-"+i, data[i][3]);
			   request.setAttribute ("username-"+i, user);
			   request.setAttribute ("location-"+i, data[i][6]);
		   }
		   
		   String reqType = request.getParameter ("reqType");
		   if(reqType.equals ("Edit")){
			   System.out.println("Message(s) successfully saved.");
			   request.setAttribute ("success", "Message(s) updated successfully.");
		   }
		   
		   rd = request.getRequestDispatcher ("editMessages.jsp");
           rd.include (request, response);
		

		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
	}
	
	private void doEdit (HttpServletRequest request, HttpServletResponse response)
	{

		renewCookie (request, response);
		
		try
		{

		   RequestDispatcher rd;
		
		   String save = request.getParameter ("Save");
		   String del = request.getParameter ("Void");
		   String size = request.getParameter ("size");
		   String user = request.getParameter ("user");
		   
		   if(save != null){
			   System.out.println("Saving");
			   for(int i = 0; i<Integer.valueOf (size); i++){
				   String selected = request.getParameter ("selected-"+i);
				   if(selected != null){
					   String referenceNumber = request.getParameter ("referenceNumber-"+i);
					   String message = request.getParameter ("message-"+i);
					   String queryString 
					   			= "Update openmrs_rpt.inboundmessages " +
					   			  "set inboundmessages.message = '"+message+"' , " +
					   			  "inboundmessages.lastchangeby = '"+user+"' " +
					   			  "where referenceNumber = '"+referenceNumber+"';";
					   MobileService.getService ().execute (queryString);
					   System.out.println (referenceNumber+"-"+message);
				   }
			   }
		   }
		   else if(del != null){
			   System.out.println("Voiding");
			   for(int i = 0; i<Integer.valueOf (size); i++){
				   String selected = request.getParameter ("selected-"+i);
				   if(selected != null){
					   String referenceNumber = request.getParameter ("referenceNumber-"+i);
					   String queryString 
					   			= "Update openmrs_rpt.inboundmessages " +
					   			  "set inboundmessages.voided = "+1+" , " +
					   			  "inboundmessages.voidedby = '"+user+"' " +
					   			  "where referenceNumber = '"+referenceNumber+"';";
					   MobileService.getService ().execute (queryString);
					   System.out.println (referenceNumber);
				   }
			   }
		   }
		   
		   doView(request, response);
		   /*rd = request.getRequestDispatcher ("editMessages.jsp");
           rd.include (request, response);*/
		

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
	}

	private void doAdd (HttpServletRequest request, HttpServletResponse response)
	{

		renewCookie (request, response);
		
		try
		{

		   RequestDispatcher rd;
		
		   String date = request.getParameter ("dateFrom");
		   String username = request.getParameter ("usernames");
		   String text = request.getParameter ("text");
		   String user = request.getParameter ("user");
		   
		   String formatedDate = date.substring (2, date.length ());
		   
		  
		   long timestamp = System.currentTimeMillis ();
		   String referenceNumber = "1".concat (String.valueOf (timestamp));
		   
		   String usernames[][] = MobileService.getService ().getUsername (username);
		   String usernameDetails[] = {usernames[0][0],usernames[0][1], usernames[0][2], usernames[0][3], usernames[0][4]};
		   String messageDetails[] = {date,referenceNumber,text,user, formatedDate};
		   
		   boolean flag = MobileService.getService ().saveText (usernameDetails, messageDetails);
		   		   
		   System.out.println(text + "  " + username + "  " + date);
		   if(flag)
			   request.setAttribute ("success", "Message saved successfully.");
		   else
			   request.setAttribute ("success", "Message failed.");  
		   
		   rd = request.getRequestDispatcher ("addMessages.jsp");
           rd.include (request, response);
		

		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
	}
	
	private void doSmsScreeningStatistics (HttpServletRequest request, HttpServletResponse response)
	{

		renewCookie (request, response);
		
		try
		{

		   RequestDispatcher rd;
		
		   String dateFrom = request.getParameter ("dateFrom");
		   String dateTo = request.getParameter ("dateTo");
		   String username = request.getParameter ("usernames");
		   String location = request.getParameter ("locations");
		   String view = request.getParameter ("View");
		   
		   String filter = " and recieveDate >= '"+dateFrom+" %' and recieveDate <= '"+dateTo+ " 23:59:59.999'";
		   if(username != "" || username == null){
			   filter = filter + " and username = '"+username+"'";
			   request.setAttribute ("username", username);
		   }
		   if(location != "" || location == null){
			   filter = filter + " and location = '"+location+"'";
			   request.setAttribute ("location", location);
		   }
		   
		   String data[][] = MobileService.getService ().getAllMessages (filter);
		   int size = data.length;
		   
		   if(view != null){
			   request.setAttribute ("size", size);
			   request.setAttribute ("dateFrom", dateFrom);
			   request.setAttribute ("dateTo", dateTo);
			   
			   for (int i = 0; i<size; i++){
				   String user = data[i][13]+" ("+data[i][5]+")";
				   request.setAttribute ("phoneNumber-"+i, data[i][0]);
				   request.setAttribute ("dateText-"+i, data[i][4]);
				   request.setAttribute ("recieveDate-"+i, data[i][2]);
				   request.setAttribute ("message-"+i, data[i][3]);
				   request.setAttribute ("username-"+i, user);
				   request.setAttribute ("location-"+i, data[i][6]);
			   } 
			   
			   rd = request.getRequestDispatcher ("smsStatistics.jsp");
	           rd.include (request, response);
		   }
		   else
		   {
			   try{
					response.setContentType ("application/zip");

					response.setCharacterEncoding ("UTF-8");
					response.setHeader ("Content-Disposition", "attachment; filename=ScreeningExport-SMS("+dateTo+"-"+dateFrom+").zip");

					ZipOutputStream zip = null;
					try
					{
						zip = new ZipOutputStream (response.getOutputStream ());

					}
					catch (IOException e)
					{

						e.printStackTrace ();
					}

					CSVUtil.makeCsv (zip, data, "SMS");

				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
		   }
		

		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
	}
	
	private void doOpenMrsScreeningStatistics (HttpServletRequest request, HttpServletResponse response)
	{

		renewCookie (request, response);
		
		try
		{

		   RequestDispatcher rd;
		
		   String dateFrom = request.getParameter ("dateFrom");
		   String dateTo = request.getParameter ("dateTo");
		   String view = request.getParameter ("View");
		   
		   String dateFilter = "e.encounter_datetime >= '"+dateFrom+" %' and e.encounter_datetime <= '"+dateTo+ " 23:59:59.999'";
		   
		   String data[][] = MobileService.getService ().getAllSceenersData (dateFilter);
		   int size = data.length;
		   
		   if(view != null){
			   request.setAttribute ("size", size);
			   request.setAttribute ("dateFrom", dateFrom);
			   request.setAttribute ("dateTo", dateTo);
			   
			   for (int i = 0; i<size; i++){
				   request.setAttribute ("totalScreened-"+i, data[i][0]);
				   request.setAttribute ("suspect-"+i, data[i][1]);
				   request.setAttribute ("nonSuspect-"+i, data[i][2]);
				   request.setAttribute ("sputumSubmitted-"+i, data[i][3]);
				   request.setAttribute ("username-"+i, data[i][4]);
				   request.setAttribute ("date-"+i, data[i][5]);
			   } 
			   
			   rd = request.getRequestDispatcher ("openmrsStatistics.jsp");
	           rd.include (request, response);
		   }
		   else
		   {
			   try{
					response.setContentType ("application/zip");

					response.setCharacterEncoding ("UTF-8");
					response.setHeader ("Content-Disposition", "attachment; filename=ScreeningExport-Openmrs("+dateTo+"-"+dateFrom+").zip");

					ZipOutputStream zip = null;
					try
					{
						zip = new ZipOutputStream (response.getOutputStream ());

					}
					catch (IOException e)
					{

						e.printStackTrace ();
					}

					CSVUtil.makeCsv (zip, data, "OMRS");

				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
		   }
		

		}
		catch (ServletException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
	}
	
	
	private void renewCookie(HttpServletRequest request, HttpServletResponse response){
		
		Cookie loginCookie = null;
    	Cookie[] cookies = request.getCookies();
    	if(cookies != null){
    	for(Cookie cookie : cookies){
    		if(cookie.getName().equals("dashboard_user")){
    			loginCookie = cookie;
    			break;
    		}
    	}
    	}
    	if(loginCookie != null){
    		loginCookie.setMaxAge(30*60);
        	response.addCookie(loginCookie);
    	}  
    	
	}
	
	private static final long	serialVersionUID	= 1501875646687116538L;

}
