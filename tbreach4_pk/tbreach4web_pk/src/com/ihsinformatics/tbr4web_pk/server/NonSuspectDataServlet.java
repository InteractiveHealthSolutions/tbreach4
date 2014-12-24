package com.ihsinformatics.tbr4web_pk.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class NonSuspectDataServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1338942060211437433L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if(request.getParameter("Logout") != null)
		{
			RequestDispatcher rd;
			//invalidate the session if exists
			HttpSession userSession = request.getSession(false);
			if(userSession!=null)
			{
				userSession.removeAttribute("username");
				userSession.invalidate();
			}
			
			//response.sendRedirect("dashboardlogin.jsp");
			rd = request.getRequestDispatcher("/dashboardlogin.jsp");
        	rd.forward(request, response);
			
		}
		else if(request.getParameter("generateCsv") != null)
		{
			response.setContentType("text/csv");
			String filename = String.valueOf(new Date().getTime()) + ".csv";
			String disposition = "attachment; filename=" + filename;
			response.setHeader("Content-Disposition", disposition);
			
			String fromDate = request.getParameter("date1");
			String toDate = request.getParameter("date2");
			String suspectedBy = request.getParameter("suspectBy");
			String treatmentCenter = request.getParameter("treatmentCenter");
			String patientType = request.getParameter("paramRadio");
			//String paediatric = request.getParameter("");
			//String both = request.getParameter("");
			System.out.println(fromDate + "---" + toDate);
			
			String result = MobileService.getService().generateCSVfromQuery(fromDate, toDate, suspectedBy, treatmentCenter, patientType);
			try
			{
				if(!result.equals(""))
				{
					OutputStream outputStream = response.getOutputStream();
			        outputStream.write(result.getBytes());
			        outputStream.flush();
			        outputStream.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		else if(request.getParameter("generateDemoCsv") != null)
		{
			response.setContentType("text/csv");
			String filename = "NonSuspectDemo-" + String.valueOf(new Date().getTime()) + ".csv";
			String disposition = "attachment; filename=" + filename;
			response.setHeader("Content-Disposition", disposition);
			
			String result = MobileService.getService().generateDemographicsCSV();
			//String result = MobileService.getService().generateCSVfromQuery(fromDate, toDate, suspectedBy, treatmentCenter, patientType);
			try
			{
				if(!result.equals(""))
				{
					OutputStream outputStream = response.getOutputStream();
			        outputStream.write(result.getBytes());
			        outputStream.flush();
			        outputStream.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
                                                                                                                                     	}
}
