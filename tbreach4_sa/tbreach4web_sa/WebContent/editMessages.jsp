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
		alert('Missing Date. Please select date to continue.');
		return false;
	}
}

function editMessages(){
	var update = 'UPDATE';
	if(buttonIndex == 0){
		update = 'VOID';
	}
	var r = confirm("Pressing 'Ok' will "+update+" all selected messages. Are you sure you want to continue.");
	if (r != true) {
	    return false;
	}    
}

</script>
</head>
<body onload="load()">

<h2>View & Edit Messages</h2>

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
<input type="hidden" id="reqType" name="reqType" value = "View">
<br>
<input type="submit" value="View Messages" >
</form>
<font size=2>
Last Import on: <i>  <%= MobileService.getService().getLastImport() %> SAST </i>
</font>
</div>
</td></tr>
</table>

<% if(request.getAttribute("size") != null){
%>

<center>
<font color="orange"> 
<br>
Date : <%=request.getAttribute("dateFrom")%> - <%=request.getAttribute("dateTo")%>
<% if(request.getAttribute("username") != null){
%> &nbsp;&nbsp; Username : <%=request.getAttribute("username")%> <% } %>
</font>
</center>

<form id="login" name="login" action="DashboardServlet" method="post" ONSUBMIT="return editMessages()">
<%
int size = Integer.valueOf(request.getAttribute("size").toString());
String dateFrom = request.getAttribute("dateFrom").toString();
String dateTo = request.getAttribute("dateTo").toString();
String u = "";
if(request.getAttribute("username") != null){
   u = request.getAttribute("username").toString();
}
%>
<input type="hidden" id="dateFrom" name="dateFrom" value = "<%=dateFrom%>">
<input type="hidden" id="dateTo" name="dateTo" value = "<%=dateTo%>">
<input type="hidden" id="usernames" name="usernames" value = "<%=u%>">
<input type="hidden" id="size" name="size" value = "<%=size%>">
<input type="hidden" id="user" name="user" value = "<%=userName%>">
<table align="center">
<tr><td>
<table class="myTable" align="center">
<tr>
<th style="display:none;"></th>
<th>EDIT</th>
<th>NAME</th>
<th>LOCATION</th>
<th>PHONE NUMBER</th>
<th>MESSAGE TEXT</th>
<th>RECIEVE DATE</th>
</tr>
<%
String separator = "";
for(int i = 0; i<size; i++){
	String m = "message-"+i;
	String v = "void-"+i;
	String rn = "referenceNumber-"+i;
	String s = "selected-"+i;
	String referenceNumber = request.getAttribute(rn).toString();
	String username = request.getAttribute("username-"+i).toString();
	String location = request.getAttribute("location-"+i).toString();
	String phoneNumber = request.getAttribute("phoneNumber-"+i).toString();
	String message = request.getAttribute(m).toString();
	String recieveDate = request.getAttribute("recieveDate-"+i).toString();	
	
	String[] array = recieveDate.split(" ");
	if(separator.equals("")){
		separator = array[0];
		%>	    
		<tr>
		<td style="display:none;"></td>
		<td><%=separator%></td>
		</tr>
	  	<%
	}
	else if(!array[0].equals(separator)){
		separator = array[0];
	%>	    
		<tr>
		<td style="display:none;"></td>
		<td><%=separator%></td>
		</tr>
	  	
<%	}
%>
<tr>
<td style="display:none;"> <input type="hidden" id="<%=rn%>" name="<%=rn%>" value = "<%= referenceNumber %>"> </td>
<td><input type="checkbox" name="<%=s%>" value="<%=s%>" id="<%=s%>" checked></td>
<td><%=username%></td>
<td><%=location%></td>
<td><%=phoneNumber%></td>
<td><input type="text" value="<%=message%>" name="<%=m%>" id="<%=m%>"></td>
<td><%=recieveDate%></td>
</tr>
<%
}
%>
</table>
</td></tr>

<tr><td>
<center>
<font color="red">
<%
if(request.getAttribute("success") != null){
%>	
   <%=request.getAttribute("success")%>
<% 	
}
%>
</font>
</center>
</td></tr>
<tr><td>
<center>
<input type="hidden" id="reqType" name="reqType" value = "Edit">
<input type="submit" value="Void" id="Void" name="Void" onclick="buttonIndex=0;">
<input type="submit" value="Save" id="Save" name="Save" onclick="buttonIndex=1;">
</center>
</td></tr>

</table>
</form>

<% } %>

</body>

</html>
