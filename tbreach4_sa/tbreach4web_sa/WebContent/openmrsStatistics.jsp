<%-- Copyright(C) 2014 Interactive Health Solutions, Pvt. Ltd.

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
	document.getElementById('detail_stat_table').style.display = 'none';
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

<h2>Screening statistics from Openmrs</h2>

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
</table>
<input type="hidden" id="reqType" name="reqType" value = "GetSSOMRS">
<br>
<input type="submit" value="View" name="View" id="View"> &nbsp;&nbsp;
<input type="submit" value="Export" name="Export" id="Export">
</form>
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
</font>
</center>

<div align="center">
<table class="myTable" id="detail_stat_table" align="center" >
<tr>
<th>USERNAME</th>
<th>DATE</th>
<th>TOTAL SCREENED</th>
<th>SUSPECT</th>
<th>NON-SUSPECT</th>
<th>SPUTUM SUBMITTED</th>
</tr>

<%
int size = Integer.valueOf(request.getAttribute("size").toString());
int totalScreenedPerDay = 0;
int totalSuspectPerDay = 0;
int totalNonSuspectPerDay = 0;
int totalSputumSubmittedPerDay = 0;
int totalScreenedOverall = 0;
int totalSuspectOverall = 0;
int totalNonSuspectOverall = 0;
int totalSputumSubmittedOverall = 0;

for(int i=0; i<size; i++){
	String sputumSubmitted = "0";
	String totalScreened = "0";
	String username = request.getAttribute("username-"+i).toString();
	String suspect = request.getAttribute("suspect-"+i).toString();
	String nonSuspect = request.getAttribute("nonSuspect-"+i).toString();
	if(request.getAttribute("sputumSubmitted-"+i) != null)
	    sputumSubmitted = request.getAttribute("sputumSubmitted-"+i).toString();
	if(request.getAttribute("totalScreened-"+i) != null)
		totalScreened = request.getAttribute("totalScreened-"+i).toString();
	String date = request.getAttribute("date-"+i).toString();
	
	totalScreenedPerDay = totalScreenedPerDay + Integer.valueOf(totalScreened);
	totalSuspectPerDay = totalSuspectPerDay + Integer.valueOf(suspect);
	totalNonSuspectPerDay = totalNonSuspectPerDay + Integer.valueOf(nonSuspect);
	totalSputumSubmittedPerDay = totalSputumSubmittedPerDay + Integer.valueOf(sputumSubmitted);
	
%>

<tr>
<td><%=username %></td>
<td><%=date %></td>
<td><%=totalScreened %></td>
<td><%=suspect %></td>
<td><%=nonSuspect %></td>
<td><%=sputumSubmitted %></td>
</tr>

<%

boolean flag = true;
int j = i+1;
if(request.getAttribute("date-"+j) != null){
	String value = request.getAttribute("date-"+j).toString();
	if(!value.equals(date))
		flag = false;
}
else {
	if(!(j<size))
		flag = false;
}

if(!flag){ %>
	
<tr>
<td></td>
<th>TOTAL (<%=date%>)</th>
<th><%=totalScreenedPerDay %></th>
<th><%=totalSuspectPerDay %></th>
<th><%=totalNonSuspectPerDay %></th>
<th><%=totalSputumSubmittedPerDay %></th>
</tr>
	
<% 	

totalScreenedOverall = totalScreenedOverall + totalScreenedPerDay;
totalSuspectOverall = totalSuspectOverall + totalSuspectPerDay;
totalNonSuspectOverall = totalNonSuspectOverall + totalNonSuspectPerDay;
totalSputumSubmittedOverall = totalSputumSubmittedOverall + totalSputumSubmittedPerDay;
totalScreenedPerDay = 0;
totalSuspectPerDay = 0;
totalNonSuspectPerDay = 0;
totalSputumSubmittedPerDay = 0;

}

} 

%>
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
<td><%=totalScreenedOverall %></td>
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

<%} %>


</body>

</html>
