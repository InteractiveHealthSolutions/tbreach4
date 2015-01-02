<%-- Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. --%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ include file="header.jsp" %> 
<%@ page import="com.ihsresearch.tbr4web.server.MobileService"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript">

var buttonIndex;

function hide_show(){
	var val = document.getElementById('anchor').text;
	if(val == "show details ..."){
		document.getElementById('detail_stat_table').style.display = 'block';
		document.getElementById('anchor').text = '... hide details';
	}
	else{
		document.getElementById('detail_stat_table').style.display = 'none';
		document.getElementById('anchor').text = 'show details ...';
	}
}

function load(){
	<%
	    String usernames[][] = MobileService.getService().getUsernameList();
	    String locations[][] = MobileService.getService().getAllLocations();
	    for(int i =0; i< usernames.length; i++){%>
	    	var username = "<%=usernames[i][0]%>";
	    	addOption(username,username,'usernames');
	   <% }
	    
	    for(int i =0; i< locations.length; i++){%>
    	var location = "<%=locations[i][0]%>";
    	addOption(location,location,'locations');
      
      <% }
	    
	%>
	addOption('-----------------------------','','usernames');
	addOption('-----------------------------','','locations');
	document.getElementById('detail_stat_table').style.display = 'none';
}

//Add new option to the dropdown (use for them month dropdown)
function addOption(text,value,type)
{
 var select = document.getElementById(type);
 var optn = document.createElement("OPTION");
 optn.text = text;
 optn.value = value;
 select.add(optn, 0);
 }
 
function viewMessage()
{
	var dateFrom = document.getElementById('dateFrom').value;
	var dateTo = document.getElementById('dateTo').value;
	if (dateFrom == '' || dateTo == ''){
		alert('Missing Date(s). Please select date to continue.');
		return false;
	}
}


</script>
</head>
<body onload="load()">

<h2>Screening statistics from SMS</h2>

<table align="center">
<tr><td>
<div align="center" class="divCenter">
<form name="view_form" action="DashboardServlet" method="post" ONSUBMIT="return viewMessage()">
<table>
<tr>
<td>Date (from):</td>
<td><input type="date" name="dateFrom" id="dateFrom"></td>
</tr>
<tr>
<td>Date (to):</td>
<td><input type="date" name="dateTo" id="dateTo"></td>
</tr>
<tr>
<td>Username:</td>
<td>
   <select name="usernames" id="usernames">
   </select>
   <i>(Optional)</i>
</td>
</tr>
<tr>
<td>Location:</td>
<td>
   <select name="locations" id="locations">
   </select>
   <i>(Optional)</i>
</td>
</tr>
</table>
<input type="hidden" id="reqType" name="reqType" value = "GetSSSMS">
<br>
<input type="submit" value="View" name="View" id="View"> &nbsp;&nbsp;
<input type="submit" value="Export" name="Export" id="Export">
</form>
<font size=2>
Last Import on: <i>  <%= MobileService.getService().getLastImport() %> SAST</i>
</font>
</div>
</td></tr>
</table>

<% if(request.getAttribute("size") != null){
%>

<center>
<font color='#F58220'>

   <a id='anchor' onclick='hide_show()'>show details ...</a>
   
</font> 

<font size="4" color="orange"> 
<br>
<br>
Date: &nbsp; <%=request.getAttribute("dateFrom")%>&nbsp; - &nbsp; <%=request.getAttribute("dateTo")%>
<% if(request.getAttribute("username") != null){
%> <br> Username : <%=request.getAttribute("username")%> <% }
if(request.getAttribute("location") != null){
%>
<br> Location : <%=request.getAttribute("location")%> <% }%>
</font>

</center>


<div align="center" >
<table class="myTable" id="detail_stat_table" align="center" >
<tr>
<th>USERNAME</th>
<th>LOCATION</th>
<th>PHONE NUMBER</th>
<th>RECIEVE DATE</th>
<th>TOTAL SCREENED</th>
<th>SUSPECT</th>
<th>NON-SUSPECT</th>
<th>SPUTUM SUBMITTED</th>
</tr>

<%

int totalScreeningOverall = 0;
int totalSuspectOverall = 0;
int totalNonSuspectOverall = 0;
int totalSputumSubmittedOverall = 0;

int totalScreeningPerDay = 0;
int totalSuspectPerDay = 0;
int totalNonSuspectPerDay = 0;
int totalSputumSubmittedPerDay = 0;
String dateFilter = "";

int size = Integer.valueOf(request.getAttribute("size").toString());
for(int i = 0; i<size; i++){
	
	String username = request.getAttribute("username-"+i).toString();
	String dateText = request.getAttribute("dateText-"+i).toString();
	String location = request.getAttribute("location-"+i).toString();
	String phoneNumber = request.getAttribute("phoneNumber-"+i).toString();
	String message = request.getAttribute("message-"+i).toString();
	String recieveDate = request.getAttribute("recieveDate-"+i).toString();
	
	String[] tokens = message.split("\\.|,|\\s|:|-");
	if(tokens.length != 4){
		   message = message.replaceAll(" ", "").trim();
		   tokens = message.split("\\.|,|\\s|:|-");
	}
		
    if (tokens.length != 4){
    	boolean flag = true;
    	int j = i+1;
    	if(request.getAttribute("dateText-"+j) != null){
    		String value = request.getAttribute("dateText-"+j).toString();
    		if(!value.equals(dateText))
    			flag = false;
    	}
    	else {
    		if(!(j<size))
    			flag = false;
    	}
    	 
    	 if(!flag){%>
    		 
    		 <tr>
		<td></td>
		<td></td>
		<td></td>
		<th>TOTAL:</th>
		<th><%=totalScreeningPerDay%></th>
		<th><%=totalSuspectPerDay %></th>
		<th><%=totalNonSuspectPerDay %></th>
		<th><%=totalSputumSubmittedPerDay %></th>
		</tr>
    	
    		 
    		 
    	 <%
    	 
    	 totalScreeningPerDay = 0;
    	 totalSuspectPerDay = 0;
    	 totalNonSuspectPerDay = 0;
    	 totalSputumSubmittedPerDay = 0;
    	 
    	 }
    	
    	
		continue;
    }
    int totalScreened = 0;
	int suspect = 0;
	int nonSuspect = 0;
	int sputumSent = 0;
    
    try {
		totalScreened = Integer.valueOf(tokens[0]);
		suspect = Integer.valueOf(tokens[1]);
		nonSuspect = Integer.valueOf(tokens[2]);
		sputumSent = Integer.valueOf(tokens[3]);
	} catch (Exception e) {
		boolean flag = true;
		int j = i+1;
		if(request.getAttribute("dateText-"+j) != null){
			String value = request.getAttribute("dateText-"+j).toString();
			if(!value.equals(dateText))
				flag = false;
		}
		else {
			if(!(j<size))
				flag = false;
		}
		 if(!flag){%>
		 
		 <tr>
	<td></td>
	<td></td>
	<td></td>
	<th>TOTAL:</th>
	<th><%=totalScreeningPerDay%></th>
	<th><%=totalSuspectPerDay %></th>
	<th><%=totalNonSuspectPerDay %></th>
	<th><%=totalSputumSubmittedPerDay %></th>
	</tr>
	
	 <%
	 
	 totalScreeningPerDay = 0;
	 totalSuspectPerDay = 0;
	 totalNonSuspectPerDay = 0;
	 totalSputumSubmittedPerDay = 0;	 
		 
		 }
		continue;
	}
	
	
%>

<tr>
<td><%=username %></td>
<td><%=location%></td>
<td><%=phoneNumber %></td>
<td><%=recieveDate%></td>
<td><%=totalScreened%></td>
<td><%=suspect %></td>
<td><%=nonSuspect %></td>
<td><%=sputumSent %></td>
</tr>

<%

	totalScreeningPerDay = totalScreeningPerDay + totalScreened;
	totalSuspectPerDay = totalSuspectPerDay + suspect;
	totalNonSuspectPerDay = totalNonSuspectPerDay + nonSuspect;
	totalSputumSubmittedPerDay = totalSputumSubmittedPerDay + sputumSent;
	
	totalScreeningOverall = totalScreeningOverall + totalScreened;
	totalSuspectOverall = totalSuspectOverall + suspect;
	totalNonSuspectOverall = totalNonSuspectOverall + nonSuspect;
	totalSputumSubmittedOverall = totalSputumSubmittedOverall + sputumSent;

    boolean flag = true;
	int j = i+1;
	if(request.getAttribute("dateText-"+j) != null){
		String value = request.getAttribute("dateText-"+j).toString();
		if(!value.equals(dateText))
			flag = false;
	}
	else {
		if(!(j<size))
			flag = false;
	}	
		
	if(!flag){%>
		<tr>
		<td></td>
		<td></td>
		<td></td>
		<th>TOTAL (<%=dateText%>):</th>
		<th><%=totalScreeningPerDay%></th>
		<th><%=totalSuspectPerDay %></th>
		<th><%=totalNonSuspectPerDay %></th>
		<th><%=totalSputumSubmittedPerDay %></th>
		</tr>
	<%
	
	totalScreeningPerDay = 0;
	totalSuspectPerDay = 0;
	totalNonSuspectPerDay = 0;
	totalSputumSubmittedPerDay = 0;
	
	}

}%>
</table>
</div>

<div align="center">
<table class="myTable">

<tr>

<td></td>
<th>TOTAL (OVERALL) </th>

</tr>

<tr>
<td>SCREENED:</td>
<td><%=totalScreeningOverall %></td>
</tr>
<tr>
<td>SUSPECT:</td>
<td><%=totalSuspectOverall %></td>
</tr>
<tr>
<td>NON-SUSPECT:</td>
<td><%=totalNonSuspectOverall %></td>
</tr>
<tr>
<td>SPUTUM SUBMITTED</td>
<td><%=totalSputumSubmittedOverall %></td>
</tr>

</table>
</div>
<% } %>


</body>

</html>
