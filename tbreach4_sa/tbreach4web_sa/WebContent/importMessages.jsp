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




function importMessage()
{
   document.getElementById("status_span").innerHTML="... Importing ..."; 
   alert('Importing... Please wait.');
   document.import_form.submit();   
}


</script>
</head>
<body>


<h2>Import Messages from SMSTarseel</h2>

<table align="center">
<tr><td>
<div align="center" class="divCenter">
Last Import on: <i>  <%= MobileService.getService().getLastImport() %> SAST</i>

<form name="import_form" action="DashboardServlet" method="post" ONSUBMIT="return importMessage()">
<br>
<font size="3">'Click on import button to import new messages from SMSTarseel'</font>
<input type="hidden" id="reqType" name="reqType" value = "Import">
<input type="submit" value="Import" >
<br>
<br>
<span id="status_span"><% if (request.getAttribute ("imported") != null) %> Messages imported successfully. </span>
</form>

</div>
</td></tr>

<tr><td>
<br>
</td></tr>

<tr><td>
<center><b><font size=5>Screener's List</font></b></center>
</td></tr>

<tr><td>
<div align="center">

<table class="myTable">
<tr>
<th>NAME</th>
<th>LOCATION</th>
<th>PRIMARY NUMBER</th>
<th>SECONDARY NUMBER</th>
</tr>

<%
String screenerList[][] = MobileService.getService().getAllScreeners();
for(int i = 0; i <screenerList.length; i++){
%>

<tr>
<td> <%=screenerList[i][1]%> <br> (<%=screenerList[i][0]%>) </td>
<td> <%=screenerList[i][2]%> </td>
<td> <%=screenerList[i][3]%> </td>
<td> <%=screenerList[i][4]%> </td>
</tr>	
<%	
}
%>

</table>

</div>
</td></tr>

</table>

</body>

</html>
